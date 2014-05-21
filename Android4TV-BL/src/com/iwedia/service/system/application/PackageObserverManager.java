package com.iwedia.service.system.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.system.application.AppSizeInfo;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.SystemControl;

public class PackageObserverManager {
    private final static String TAG = "PackageObserverManager";
    private PackageDeleteObserver observerdelete;
    private PackageStatsObserver observerStat;
    private PackageDataObserver observerData;
    private PackageManager pm;
    private Method uninstallmethod;
    private Method getPackageSizeInfo;
    private Method deleteApplicationCacheFiles;
    private Method clearApplicationUserData;
    private String valueString = "";
    private AppSizeInfo appSizeInfo;
    public final static long SIZE_KB = 1024L;
    public final static long SIZE_MB = SIZE_KB * SIZE_KB;
    public final static long SIZE_GB = SIZE_MB * SIZE_KB;

    class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        public void packageDeleted(String packageName, int returnCode)
                throws RemoteException {
        }
    }

    class PackageDataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName,
                final boolean succeeded) {
            if (IWEDIAService.DEBUG) {
                Log.e(TAG, "Package " + packageName + " succeeded " + succeeded);
                SystemControl.broadcastClearDataCacheFinished(succeeded);
            }
        }
    }

    class PackageStatsObserver extends IPackageStatsObserver.Stub {
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            appSizeInfo = new AppSizeInfo();
            appSizeInfo.setCodeSize(getConvertedValue(pStats.codeSize));
            appSizeInfo.setDataSize(getConvertedValue(pStats.dataSize));
            appSizeInfo
                    .setExternalCacheSize(getConvertedValue(pStats.externalCacheSize));
            appSizeInfo.setCacheSize(getConvertedValue(pStats.cacheSize
                    + pStats.externalCacheSize));
            appSizeInfo
                    .setExternalObbSize(getConvertedValue(pStats.externalObbSize));
            appSizeInfo
                    .setExternalDataSize(getConvertedValue(pStats.externalDataSize));
            appSizeInfo
                    .setExternalMediaSize(getConvertedValue(pStats.externalMediaSize));
            appSizeInfo.setTotalSize(getConvertedValue(pStats.codeSize
                    + pStats.dataSize + pStats.externalCacheSize
                    + pStats.externalDataSize));
            if (pStats.cacheSize + pStats.externalCacheSize == 0) {
                appSizeInfo.setCacheEmpty(true);
            } else {
                appSizeInfo.setCacheEmpty(false);
            }
            if (pStats.dataSize == 0) {
                appSizeInfo.setDataEmpty(true);
            } else {
                appSizeInfo.setDataEmpty(false);
            }
            if (IWEDIAService.DEBUG) {
                Log.e(TAG,
                        "appSizeInfo.getTotalSize" + appSizeInfo.getTotalSize());
            }
            SystemControl.broadcastAppSizeInfo(appSizeInfo);
        }
    }

    private String getConvertedValue(long value) {
        double tmpValue = value;
        if ((tmpValue / SIZE_GB) >= 1) {
            tmpValue = tmpValue / SIZE_GB;
            valueString = String.format("%.2f", tmpValue);
            valueString = valueString + " GB";
        } else if ((tmpValue / SIZE_MB) >= 1) {
            tmpValue = tmpValue / SIZE_MB;
            valueString = String.format("%.2f", tmpValue);
            valueString = valueString + " MB";
        } else if ((tmpValue / SIZE_KB) >= 1) {
            tmpValue = tmpValue / SIZE_KB;
            valueString = String.format("%.2f", tmpValue);
            valueString = valueString + " KB";
        } else {
            valueString = String.format("%.2f", tmpValue);
            valueString = valueString + " B";
        }
        return valueString;
    }

    public PackageObserverManager(Context context) throws SecurityException,
            NoSuchMethodException {
        observerStat = new PackageStatsObserver();
        observerdelete = new PackageDeleteObserver();
        observerData = new PackageDataObserver();
        pm = context.getPackageManager();
        Class<?>[] uninstalltypes = new Class[] { String.class,
                IPackageDeleteObserver.class, int.class };
        uninstallmethod = pm.getClass().getMethod("deletePackage",
                uninstalltypes);
        Class<?>[] statstypes = new Class[] { String.class,
                IPackageStatsObserver.class };
        getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo",
                statstypes);
        Class<?>[] dataCacheTypes = new Class[] { String.class,
                IPackageDataObserver.class };
        deleteApplicationCacheFiles = pm.getClass().getMethod(
                "deleteApplicationCacheFiles", dataCacheTypes);
        Class<?>[] dataDataTypes = new Class[] { String.class,
                IPackageDataObserver.class };
        clearApplicationUserData = pm.getClass().getMethod(
                "clearApplicationUserData", dataDataTypes);
    }

    public void getPackageSizeInfo(String packagename)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        getPackageSizeInfo.invoke(pm,
                new Object[] { packagename, observerStat });
    }

    public void uninstallPackage(String packagename)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        uninstallmethod.invoke(pm, new Object[] { packagename, observerdelete,
                0 });
    }

    public void deleteApplicationCacheFiles(String packagename)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        deleteApplicationCacheFiles.invoke(pm, new Object[] { packagename,
                observerData, 0 });
    }

    public void clearApplicationUserData(String packagename)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        clearApplicationUserData.invoke(pm, new Object[] { packagename,
                observerData });
    }
}
