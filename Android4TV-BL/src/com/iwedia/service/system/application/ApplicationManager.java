package com.iwedia.service.system.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

import com.iwedia.comm.content.applications.AppItem;
import com.iwedia.comm.enums.AppListType;
import com.iwedia.comm.system.application.AppPermission;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.SystemControl;

/**
 * This class manages applications, their details and settings, registers
 * broadcast receivers and reacts on appropriate action intent.
 * 
 * @author Stanislava Markovic
 */
@SuppressLint("NewApi")
public class ApplicationManager {
    private final String LOG_TAG = "ApplicationManager";
    private final String CONFIG_FILE_NAME = "application_config.txt";
    private final String CONFIG_FILE_NAME_TARGET = "/data/data/com.iwedia.service/";
    private PackageReceiver applicationReceiver;
    private ShutdownReceiver shutdownReceiver;
    private ArrayList<String> configFile;
    private Context activity;
    private static ApplicationManager instance;
    private ArrayList<AppItem> allAppsList;
    private ArrayList<Integer> allAppsIndexList;
    private ArrayList<Integer> installAppsIndexList;
    private ArrayList<Integer> contentAppsIndexList;
    private int appTypeGlobal = AppListType.NONE;
    private static String packageNameGlobal = "";
    // force stop
    private Object am;
    private Class<?> ActivityManagerNative;
    private Class<?> IActivityManager;
    private Method getDefault;
    private Method forceStopPackage;
    // delete packages
    private PackageObserverManager pom;
    // app size info
    private ApplicationInfo appInfo;
    private List<AppPermission> appPermissions;
    private AppSecurityPermissions appSecurityPermissions;
    private ArrayList<ComponentName> prefActList;
    private ArrayList<IntentFilter> intentList;
    // process
    private ArrayList<AppItem> runningServices;

    /**
     * ApplicationManager constructor. Initializes values and registers
     * broadcast receivers.
     * 
     * @param activity
     */
    public ApplicationManager(Context activity) {
        this.activity = activity;
        instance = this;
        applicationReceiver = new PackageReceiver();
        shutdownReceiver = new ShutdownReceiver();
        try {
            pom = new PackageObserverManager(IWEDIAService.getContext());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_FIRST_LAUNCH);
        intentFilter.addDataScheme("package");
        activity.registerReceiver(applicationReceiver, intentFilter);
        IntentFilter intentFilterShutDown = new IntentFilter();
        intentFilterShutDown.addAction(Intent.ACTION_SHUTDOWN);
        activity.registerReceiver(shutdownReceiver, intentFilterShutDown);
        copyFile();
        readFile();
        initApplications();
    }

    /**
     * Gets the instance of the ApplicationManager class.
     * 
     * @return instance of the ApplicationManager class.
     */
    public static ApplicationManager getInstance() {
        return instance;
    }

    /**
     * Gets the instance of the ApplicationManager class.
     * 
     * @param packageName
     *        - package name.
     * @return instance of the ApplicationManager class.
     */
    public static ApplicationManager getInstance(String packageName) {
        packageNameGlobal = packageName;
        return instance;
    }

    /**
     * Sets application type.
     * 
     * @param appType
     *        - application type.
     */
    public void setAppType(int appType) {
        appTypeGlobal = appType;
    }

    /**
     * Gets application with the given index.
     * 
     * @param index
     *        - application index.
     * @return application with the given index.
     */
    public AppItem getApplication(int index) {
        int indexInAllAppsList = 0;
        switch (appTypeGlobal) {
            case AppListType.ALL:
                indexInAllAppsList = allAppsIndexList.get(index);
                break;
            case AppListType.INSTALLED:
                indexInAllAppsList = installAppsIndexList.get(index);
                break;
            case AppListType.CONTENT:
                Log.e(LOG_TAG, "PackageReceiver "
                        + "AppListType.CONTENT allAppsList.size() : "
                        + allAppsList.size());
                indexInAllAppsList = contentAppsIndexList.get(index);
                break;
            default:
                break;
        }
        // if (appTypeGlobal == AppListType.RUNNING)
        // return runningServices.get(index);
        return allAppsList.get(indexInAllAppsList);
    }

    /**
     * Gets size of application list with the given type.
     * 
     * @param appType
     *        - application type - can be one of following: AppListType.ALL,
     *        AppListType.INSTALLED, AppListType.CONTENT
     * @return size of application list.
     */
    public int getSize(int appType) {
        appTypeGlobal = appType;
        int size = 0;
        switch (appType) {
            case AppListType.ALL:
                size = allAppsIndexList.size();
                break;
            case AppListType.INSTALLED:
                size = installAppsIndexList.size();
                break;
            case AppListType.CONTENT:
                size = contentAppsIndexList.size();
                break;
            case AppListType.RUNNING:
                // size = runningServices.size();
                break;
            default:
                break;
        }
        return size;
    }

    /**
     * Copy configuration file.
     */
    private void copyFile() {
        String file = CONFIG_FILE_NAME_TARGET + CONFIG_FILE_NAME;
        File fl = new File(file);
        if (!fl.exists()) {
            copyAssetToData(CONFIG_FILE_NAME);
        }
    }

    /**
     * Copy configuration file from assets to data folder.
     * 
     * @param strFilename
     */
    private void copyAssetToData(String strFilename) {
        // Open your local db as the input stream
        try {
            InputStream myInput = activity.getAssets().open(CONFIG_FILE_NAME);
            String outFileName = CONFIG_FILE_NAME_TARGET + CONFIG_FILE_NAME;
            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            try {
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
            } finally {
                // Close the streams
                myOutput.flush();
                myOutput.close();
            }
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the configuration file with built-in application which will be
     * displayed in Content list.
     */
    private void readFile() {
        configFile = new ArrayList<String>();
        String path = CONFIG_FILE_NAME_TARGET + CONFIG_FILE_NAME;
        File file = new File(path);
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    configFile.add(line);
                }
            } finally {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialization of application list - create one list with all application
     */
    public void initApplications() {
        allAppsList = new ArrayList<AppItem>();
        List<ApplicationInfo> apps = activity.getPackageManager()
                .getInstalledApplications(0);
        AppItem appItem;
        for (int j = 0; j < apps.size(); j++) {
            appItem = new AppItem();
            appItem.setAppname((String) activity.getPackageManager()
                    .getApplicationLabel(apps.get(j)));
            appItem.setAppPackage(apps.get(j).packageName);
            if (isSystemPackage(apps.get(j))) {
                if (configFile.contains(appItem.getAppPackage())) {
                    appItem.setAppListType(AppListType.CONTENT);
                } else {
                    appItem.setAppListType(AppListType.NONE);
                }
            } else {
                appItem.setAppListType(AppListType.INSTALLED);
            }
            if (!appItem.getAppname().toLowerCase().contains("android4tv")
                    && !appItem.getAppname().toLowerCase().equals("tvservice")
                    && !appItem.getAppname().toLowerCase()
                            .equals("iwedia_service")) {
                allAppsList.add(appItem);
            }
        }
        sortAppList();
        createAppListIndexes();
    }

    /**
     * Create lists with indexes from application list with all built-in and
     * installed applications. allAppsIndexList - list with indexes of all
     * applications from all application list. installAppsIndexList - list with
     * indexes of downloaded applications from all application list.
     * contentAppsIndexList - list with indexes of applications which will be
     * displayed in Content list from all application list.
     */
    public void createAppListIndexes() {
        allAppsIndexList = new ArrayList<Integer>();
        installAppsIndexList = new ArrayList<Integer>();
        contentAppsIndexList = new ArrayList<Integer>();
        for (int i = 0; i < allAppsList.size(); i++) {
            allAppsIndexList.add(i);
            if (allAppsList.get(i).getAppListType() == AppListType.INSTALLED) {
                if (!allAppsList.get(i).getAppname().toLowerCase()
                        .contains("android4tv")) {
                    installAppsIndexList.add(i);
                    contentAppsIndexList.add(i);
                }
            } else if (allAppsList.get(i).getAppListType() == AppListType.CONTENT) {
                contentAppsIndexList.add(i);
            }
        }
    }

    /**
     * Sort items in application list in alphabetical order.
     */
    public void sortAppList() {
        Collections.sort(allAppsList, new Comparator<AppItem>() {
            public int compare(AppItem object1, AppItem object2) {
                return object1.getAppname().compareToIgnoreCase(
                        object2.getAppname());
            }
        });
    }

    /**
     * Sort indexes in application list.
     * 
     * @param list
     */
    public void sortIndexAppList(ArrayList<Integer> list) {
        Collections.sort(list, new Comparator<Integer>() {
            public int compare(Integer object1, Integer object2) {
                return object1.compareTo(object2);
            }
        });
    }

    /**
     * Class PackageReceiver - Broadcast receiver which registers package
     * broadcast intents - Intent.ACTION_PACKAGE_ADDED,
     * Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_RESTARTED etc.
     */
    private class PackageReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "PackageReceiver " + " onReceive()");
            }
            Uri data = intent.getData();
            if (data != null) {
                String packageName = data.getSchemeSpecificPart();
                String action = intent.getAction();
                Log.e(LOG_TAG, "ACTION: " + action);
                if (action != null) {
                    if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_ADDED : " + packageName);
                        AppItem p = new AppItem();
                        ApplicationInfo appInfo = null;
                        try {
                            appInfo = activity.getPackageManager()
                                    .getApplicationInfo(packageName, 0);
                        } catch (NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        p.setAppname((String) activity.getPackageManager()
                                .getApplicationLabel(appInfo));
                        if (appInfo != null) {
                            p.setAppPackage(appInfo.packageName);
                        }
                        p.setAppListType(AppListType.INSTALLED);
                        allAppsList.add(p);
                        sortAppList();
                        createAppListIndexes();
                    } else if (action.equals(Intent.ACTION_PACKAGE_CHANGED)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_CHANGED: " + packageName);
                    } else if (action.equals(Intent.ACTION_PACKAGE_INSTALL)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_INSTALL: " + packageName);
                    } else if (action
                            .equals(Intent.ACTION_PACKAGE_DATA_CLEARED)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_DATA_CLEARED: "
                                    + packageName);
                    } else if (action
                            .equals(Intent.ACTION_PACKAGE_FIRST_LAUNCH)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_FIRST_LAUNCH: "
                                    + packageName);
                    } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_REMOVED :" + packageName);
                        for (int i = 0; i < allAppsList.size(); i++)
                            if (allAppsList.get(i).getAppPackage()
                                    .equals(packageName)) {
                                allAppsList.remove(i);
                                break;
                            }
                        createAppListIndexes();
                        SystemControl.broadcastUninstallFinished();
                    } else if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_REPLACED : "
                                    + packageName);
                    } else if (action.equals(Intent.ACTION_PACKAGE_RESTARTED)) {
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "PackageReceiver "
                                    + "ACTION_PACKAGE_RESTARTED: "
                                    + packageName);
                        if (packageName.equals("com.iwedia.gui")) {
                            if (IWEDIAService.DEBUG)
                                Log.e(LOG_TAG, "SERVICE STOPPED "
                                        + "ACTION_PACKAGE_RESTARTED: "
                                        + packageName);
                            // IWEDIAService.getInstance().stopService(
                            // IWEDIAService.getInstance().getServiceIntent());
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if is application with the application info built-in or downloaded.
     * 
     * @param appInfo
     *        - application info.
     * @return true if an application is built-in, else false.
     */
    public boolean isSystemPackage(ApplicationInfo appInfo) {
        return ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    /**
     * Check if is application with the given package name (packageNameGlobal)
     * built-in or downloaded.
     * 
     * @return true if an application is built-in, else false.
     */
    public boolean isSystemPackage() {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = IWEDIAService.getContext().getPackageManager()
                    .getApplicationInfo(packageNameGlobal, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo != null) {
            return isSystemPackage(applicationInfo);
        } else {
            return false;
        }
    }

    /**
     * Check if the application is stopped.
     * 
     * @return true if application is stopped, else false.
     */
    public boolean isStopped() {
        try {
            appInfo = activity.getPackageManager().getApplicationInfo(
                    packageNameGlobal, 0);
        } catch (NameNotFoundException e2) {
            e2.printStackTrace();
        }
        if ((appInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0) {
            Log.e(LOG_TAG, "Active");
            return false;
        } else {
            Log.e(LOG_TAG, "Stopped");
            return true;
        }
    }

    /**
     * Gets list of running services.
     * 
     * @return list of running services.
     */
    public List<AppItem> getRunningServices() {
        runningServices = new ArrayList<AppItem>();
        ActivityManager am = (ActivityManager) IWEDIAService.getInstance()
                .getSystemService(IWEDIAService.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);
        AppItem appItem;
        for (int i = 0; i < rs.size(); i++) {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            if (!rsi.process.contains("phone")
                    && !rsi.process.contains("systemui")
                    && !rsi.service.getPackageName().equals(
                            "com.iwedia.service")
                    && !rsi.service.getPackageName().equals(
                            "com.rtrk.comedia.service")
                    && !rsi.service.getPackageName().contains("Google")
                    && !rsi.service.getPackageName().contains("google")) {
                appItem = new AppItem();
                appItem.setAppListType(AppListType.RUNNING);
                appItem.setAppname(getProcessName(rsi.service.getPackageName()));
                appItem.setAppPackage(rsi.service.getPackageName());
                appItem.setAppClass(rsi.service.getClassName());
                runningServices.add(appItem);
            }
        }
        if (rs.size() == 0) {
            return null;
        }
        return runningServices;
    }

    /**
     * Gets process name with the given package name.
     * 
     * @param packageName
     *        - package name.
     * @return process name.
     */
    private String getProcessName(String packageName) {
        final PackageManager pm = IWEDIAService.getContext()
                .getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        final String processName = (String) (ai != null ? pm
                .getApplicationLabel(ai) : packageName);
        return processName;
    }

    /**
     * Force stop an application with the given package name
     * (packageNameGlobal).
     * 
     * @return true if everything is OK, else false
     */
    public boolean forceStop() {
        try {
            ActivityManagerNative = Class
                    .forName("android.app.ActivityManagerNative");
            IActivityManager = Class.forName("android.app.IActivityManager");
            getDefault = ActivityManagerNative.getMethod("getDefault",
                    (Class<?>[]) null);
            am = IActivityManager.cast(getDefault.invoke(ActivityManagerNative,
                    (Object[]) null));
            Class<?>[] param = new Class[] { String.class };
            forceStopPackage = am.getClass().getMethod("forceStopPackage",
                    param);
            forceStopPackage.invoke(am, new Object[] { packageNameGlobal });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Uninstall the downloaded application.
     * 
     * @return true if everything is OK, else false.
     */
    public boolean uninstall() {
        try {
            pom.uninstallPackage(packageNameGlobal);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets application size information - application (code) size, data size,
     * cache size, external data size etc, external cache size etc.
     */
    public void getAppSizeInfo() {
        try {
            pom.getPackageSizeInfo(packageNameGlobal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enable or disable built-in applications. If application is enabled, this
     * function disables application with the given package (packageNameGlobal),
     * else enables.
     * 
     * @return true if everything is OK, else false.
     */
    public boolean enable() {
        try {
            if (isEnabled()) {
                activity.getPackageManager()
                        .setApplicationEnabledSetting(
                                packageNameGlobal,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER,
                                0);
            } else {
                activity.getPackageManager().setApplicationEnabledSetting(
                        packageNameGlobal,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if is built-in application with the given package name
     * (packageNameGlobal) enabled or disabled.
     * 
     * @return true if application is enabled, else false.
     */
    public boolean isEnabled() {
        if (activity.getPackageManager().getApplicationEnabledSetting(
                packageNameGlobal) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                || activity.getPackageManager().getApplicationEnabledSetting(
                        packageNameGlobal) == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            return true;
        } else if (activity.getPackageManager().getApplicationEnabledSetting(
                packageNameGlobal) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || activity.getPackageManager().getApplicationEnabledSetting(
                        packageNameGlobal) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * Delete application cache files.
     */
    public void deleteApplicationCacheFiles() {
        try {
            pom.deleteApplicationCacheFiles(packageNameGlobal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete application user data - all files, accounts, databases etc.
     */
    public void clearApplicationUserData() {
        try {
            pom.clearApplicationUserData(packageNameGlobal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets list of application permissions.
     * 
     * @return list of application permissions.
     */
    public List<AppPermission> getAppPermissions() {
        appSecurityPermissions = new AppSecurityPermissions(
                IWEDIAService.getContext(), packageNameGlobal);
        if (appSecurityPermissions.getPermissionCount() > 0) {
            appPermissions = appSecurityPermissions.getPermissions();
        } else {
            appPermissions = null;
        }
        return appPermissions;
    }

    /**
     * Check if is application with the given package name (packageNameGlobal)
     * set to open by default for some actions.
     * 
     * @return true if is application with the given package name
     *         (packageNameGlobal) default application, otherwise false.
     */
    public boolean isDefault() {
        prefActList = new ArrayList<ComponentName>();
        intentList = new ArrayList<IntentFilter>();
        try {
            activity.getPackageManager().getPreferredActivities(intentList,
                    prefActList, packageNameGlobal);
            if (prefActList.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clear application's default actions.
     * 
     * @return true if everything is OK, else false.
     */
    public boolean clearDefaults() {
        try {
            activity.getPackageManager().clearPackagePreferredActivities(
                    packageNameGlobal);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private class ShutdownReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.e(LOG_TAG, "SHUT DOWN" + action);
        }
    }

    public void stopService(String packegeName, String className) {
        IWEDIAService.getInstance().stopService(
                new Intent().setComponent(new ComponentName(packegeName,
                        className)));
    }
}
