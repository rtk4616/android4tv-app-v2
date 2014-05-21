package com.iwedia.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.dtv.dtvmanager.DTVManager;
import com.iwedia.service.ci.CIControl;
import com.iwedia.service.content.ContentListControl;
import com.iwedia.service.hbb.HbbTvControl;
import com.iwedia.service.io.InputOutputControl;
import com.iwedia.service.parental.ParentalControl;
import com.iwedia.service.proxyservice.DTVManagerProxy;
import com.iwedia.service.pvr.PvrControl;
import com.iwedia.service.scan.ScanControl;
import com.iwedia.service.service.ServiceControl;
import com.iwedia.service.setup.SetupControl;
import com.iwedia.service.storage.StorageManager;
import com.iwedia.service.streamcomponent.StreamComponentControl;
import com.iwedia.service.system.application.ApplicationManager;
import com.iwedia.service.widget.WidgetManager;
import com.iwedia.service.reminder.ReminderControl;

public class IWEDIAService extends android.app.Service {
    public static final boolean DEBUG = true;
    public static final boolean FAVORITE = true;
    public static final boolean WRITE_SERVICE_LISTS_TO_FILE = true;
    public static final boolean SYSTEM_UPDATE_ENABLED = true;
    private final String LOG_TAG = "IWEDIAService";
    public static final String DATA_STORAGE = "IWEDIAServiceData";
    private static IWEDIAService mInstance;
    private static Context mContext;
    private static boolean isRestartNeeded = false;
    private static String activityComponentName = "";
    private static SharedPreferences mPreferences = null;
    private static ApplicationManager appManager;
    private static WidgetManager widgManager;
    DTVManagerProxy mDtvManagerProxy = null;
    DTVManager mDtvManager = null;
    private static StorageManager storageManager;
    private String multimediaListTableName = "multimedia";
    private String favoriteListTableName = "favorite_list";
    private String recentlyListTableName = "recently_list";
    public static boolean isTvPlatform = false;

    @Override
    public IBinder onBind(Intent arg0) {
        if (DEBUG) {
            Log.e(LOG_TAG, "*********onBind()");
        }
        if (mDtvManagerProxy == null) {
            Log.e(LOG_TAG, "ERROR: proxy service not created!");
        }
        return mDtvManagerProxy;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (DEBUG) {
            Log.e(LOG_TAG, "*********onUnbind()");
        }
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        if (DEBUG) {
            Log.e(LOG_TAG, "onRebind");
        }
        // FIX for setting last played content
        try {
            ((ContentListControl) mDtvManagerProxy.getContentListControl())
                    .refreshActiveContent();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.e(LOG_TAG, "onStartCommand");
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG) {
            Log.e(LOG_TAG, "onCreate");
        }
        mInstance = IWEDIAService.this;
        mContext = this.getApplicationContext();
        mPreferences = getSharedPreferences(DATA_STORAGE, MODE_PRIVATE);
        storageManager = new StorageManager();
        appManager = new ApplicationManager(mContext);
        widgManager = new WidgetManager(mContext);
        mDtvManagerProxy = new DTVManagerProxy();
        mDtvManager = new DTVManager();
        mDtvManagerProxy.initialize();
        if (mDtvManager != null) {
            mDtvManager.getStreamComponentControl().registerCallback(
                    StreamComponentControl.getStreamComponentControlCallback());
            mDtvManager.getHbbTvControl().registerCallback(
                    HbbTvControl.getHbbTvCallback());
            mDtvManager.getCIControl().registerCallback(
                    CIControl.getCICallback());
            mDtvManager.getPvrControl().registerCallback(
                    PvrControl.getPVRCallback());
            mDtvManager.getInputOutputControl().registerCallback(
                    InputOutputControl.getIoCallback());
            mDtvManager.getSetupControl().registerCallback(
                    SetupControl.getSetupCallback());
            mDtvManager.getParentalControlDvb().registerCallback(
                    ParentalControl.getParentalCallbackDvb());
            mDtvManager.getServiceControl().registerCallback(
                    ServiceControl.getServiceListCallback());
            mDtvManager.getScanControl().registerCallback(
                    ScanControl.getScanCallback());
            mDtvManager.getReminderControl().registerCallback(
                    ReminderControl.getReminderCallback());
        } else if (DEBUG) {
            Log.e(LOG_TAG, "onServiceConnected - null service");
        }
    }

    @Override
    public void onLowMemory() {
        if (DEBUG)
            Log.i(LOG_TAG,
                    "PROXYYYYYYYYYYYYYYY - Low memory on service side....WHAT SHOULD WE DO??????????");
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        // unbindService(conn);
        if (DEBUG) {
            Log.e(LOG_TAG, "onDestroy");
        }
        mDtvManager.resetServiceBinderState();
        if (IWEDIAService.isRestartNeeded) {
            Log.i(LOG_TAG, "Restart needed!");
            Log.i(LOG_TAG, "Starting " + IWEDIAService.activityComponentName
                    + " from proxyService!");
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setClassName("com.iwedia.gui",
                    IWEDIAService.activityComponentName);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            setRestartFlag(false);
        }
    }

    public DTVManagerProxy getDtvManagerProxy() {
        return mDtvManagerProxy;
    }

    public DTVManager getDTVManager() {
        return mDtvManager;
    }

    public static IWEDIAService getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    public SharedPreferences getPreferenceManager() {
        return mPreferences;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public ApplicationManager getApplicationManager() {
        return appManager;
    }

    public WidgetManager getWidgetManager() {
        return widgManager;
    }

    public String getMultimediaListTableName() {
        return multimediaListTableName;
    }

    public String getFavoriteListTableName() {
        return favoriteListTableName;
    }

    public String getRecentlyListTableName() {
        return recentlyListTableName;
    }

    public void setRestartFlag(boolean isRestartNeeded) {
        IWEDIAService.isRestartNeeded = isRestartNeeded;
    }

    public void setActivityComponentName(String componentName) {
        IWEDIAService.activityComponentName = componentName;
    }
}
