package com.iwedia.service.system;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.enums.FilterType;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.content.ContentFilterPVRRecorded;
import com.iwedia.service.system.external_and_local_storage.ExternalAndLocalStorageManager;

public class MediaMounted {
    private String LOG_TAG = "MediaMounted";
    private String mediaPreviousState = Intent.ACTION_MEDIA_UNMOUNTED;
    private MediaMountedReceiver mediaMountedReceiver;

    public MediaMounted() {
        registerMediaMountedBroadcastReciver();
    }

    private class MediaMountedReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getDataString();
            if (action != null) {
                if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                        && !mediaPreviousState
                                .equals(Intent.ACTION_MEDIA_REMOVED)) {
                    Log.d(LOG_TAG, "Media mounted - " + data);
                    ExternalAndLocalStorageManager.getInstance().setUsbPath(
                            intent.getDataString());
                    SystemControl.broadcastMediaMounted(data);
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (IWEDIAService.DEBUG)
                                Log.e(LOG_TAG,
                                        "starting reinitialize of PVR favourite elements at timer");
                            try {
                                ContentFilterPVRRecorded cfPVRRecorded = (ContentFilterPVRRecorded) (IWEDIAService
                                        .getInstance().getDtvManagerProxy()
                                        .getContentListControl())
                                        .getContentFilter(FilterType.PVR_RECORDED);
                                if (cfPVRRecorded != null) {
                                    cfPVRRecorded.reinitialize();
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(timerTask, 3000);
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "ACTION_MEDIA_MOUNTED");
                    }
                } else if (action.equals(Intent.ACTION_MEDIA_NOFS)
                        || action.equals(Intent.ACTION_MEDIA_UNMOUNTABLE)) {
                    Log.d(LOG_TAG, "No or bad filesystem - " + data);
                    SystemControl.broadcastMediaNotSupported(data);
                } else if (action.equals(Intent.ACTION_MEDIA_EJECT)
                        || action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                    ExternalAndLocalStorageManager.getInstance()
                            .deselectUsbPath();
                    SystemControl.broadcastMediaEjected(data);
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "removal action: " + action);
                    }
                    try {
                        ContentFilterPVRRecorded cfPVRRecorded = (ContentFilterPVRRecorded) (IWEDIAService
                                .getInstance().getDtvManagerProxy()
                                .getContentListControl())
                                .getContentFilter(FilterType.PVR_RECORDED);
                        if (cfPVRRecorded != null) {
                            cfPVRRecorded.mediaEjected();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                mediaPreviousState = action;
            }
        }
    }

    private void registerMediaMountedBroadcastReciver() {
        mediaMountedReceiver = new MediaMountedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        // intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        // intentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
        // intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        // intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
        // intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        // intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        // intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        IWEDIAService.getContext().registerReceiver(mediaMountedReceiver,
                intentFilter);
    }
}
