package com.iwedia.gui.callbacks;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IActionCallback;
import com.iwedia.comm.system.application.AppSizeInfo;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.AccountsAndSyncManageAccountsDialog;
import com.iwedia.gui.components.dialogs.ApplicationsAppControlDialog;
import com.iwedia.gui.components.dialogs.ApplicationsManageManageAppsDialog;
import com.iwedia.gui.components.dialogs.ExternalAndLocalStorageDialog;
import com.iwedia.gui.components.dialogs.SoftwareUpgradeDialog;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaGridHelper;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;

public class ActionCallBack extends IActionCallback.Stub implements OSDGlobal {
    private static final String TAG = "ActionCallBack";
    /** Messages */
    private static final int HIDE_SUBTITLE_DIALOG = 0;
    private static final int MEDIA_MOUNTED = 1;
    private static final int MEDIA_EJECTED = 2;
    private static final int MEDIA_NOT_SUPPORTED = 3;
    /** Instance of MainActivity */
    private MainActivity mActivity = null;
    /** Handler */
    private Handler mHandler = null;

    public ActionCallBack(MainActivity activity) {
        mActivity = activity;
        initializeHandler();
    }

    @Override
    public void showTeletext() throws RemoteException {
        Log.i("ActionCallback", "Show teletext");
    }

    @Override
    public void hideTeletext() throws RemoteException {
        Log.i("ActionCallback", "Hide teletext");
    }

    @Override
    public void invalidateTeletext() throws RemoteException {
        Log.i("ActionCallback", "Invalidate teletext");
    }

    @Override
    public void showSubtitle() throws RemoteException {
        Log.i("ActionCallback", "Show subtitle");
    }

    @Override
    public void hideSubtitle() throws RemoteException {
        Log.i("ActionCallback", "Hide subtitle");
        Message.obtain(mHandler, HIDE_SUBTITLE_DIALOG).sendToTarget();
    }

    @Override
    public void invalidateSubtitle(int value) throws RemoteException {
        Log.i("ActionCallback", "Invalidate subtitle");
        // SubtitleDialogView.invalidateSubtitleView(value);
    }

    @Override
    public void invalidateMheg() throws RemoteException {
        Log.i("ActionCallback", "Invalidate Mheg");
    }

    @Override
    public void showMheg() throws RemoteException {
        Log.i("ActionCallback", "Show Mheg");
        // if (mhegDialog == null) {
        // initializeMHEG();
        // mhegDialog.show();
        // }
    }

    @Override
    public void hideMheg() throws RemoteException {
        Log.i("ActionCallback", "Hide Mheg");
        // mhegDialog.cancel();
    }

    @Override
    public void uninstallFinished() throws RemoteException {
        ApplicationsAppControlDialog appControlDialog = mActivity
                .getDialogManager().getApplicationsAppControlDialog();
        if (appControlDialog != null) {
            appControlDialog.cancel();
            ApplicationsManageManageAppsDialog appManageDialog = mActivity
                    .getDialogManager().getApplicationsManageManageAppsDialog();
            if (appManageDialog != null) {
                appManageDialog.show();
            }
        }
    }

    @Override
    public void getAppSizeInfo(AppSizeInfo arg0) throws RemoteException {
        ApplicationsAppControlDialog appControlDialog = mActivity
                .getDialogManager().getApplicationsAppControlDialog();
        if (appControlDialog != null) {
            appControlDialog.setAppSizeInfo(arg0);
        }
    }

    @Override
    public void clearDataCacheFinished(boolean isSucceeded)
            throws RemoteException {
        if (mActivity.getDialogManager().getApplicationsAppControlDialog() != null
                && isSucceeded) {
            ApplicationsAppControlDialog appControlDialog = mActivity
                    .getDialogManager().getApplicationsAppControlDialog();
            if (appControlDialog != null) {
                appControlDialog.setViews();
            }
        }
    }

    @Override
    public void updateEvent(String version) throws RemoteException {
        SoftwareUpgradeDialog softwareUpgradeDialog = mActivity
                .getDialogManager().getSoftwareUpgradeDialog();
        if (softwareUpgradeDialog != null) {
            softwareUpgradeDialog.showAlertDialog(version);
        }
        Log.d(TAG, "updateEvent CALL BACK version: " + version);
    }

    @Override
    public void errorEvent(String err) throws RemoteException {
        Log.d(TAG, "errorEvent CALL BACK error: " + err);
        SoftwareUpgradeDialog softwareUpgradeDialog = mActivity
                .getDialogManager().getSoftwareUpgradeDialog();
        if (softwareUpgradeDialog != null) {
            softwareUpgradeDialog.toastMessage(err);
        }
    }

    @Override
    public void noUpdateEvent(String msg) throws RemoteException {
        Log.d(TAG, "noUpdateEvent CALL BACK msg: " + msg);
        SoftwareUpgradeDialog softwareUpgradeDialog = mActivity
                .getDialogManager().getSoftwareUpgradeDialog();
        if (softwareUpgradeDialog != null) {
            softwareUpgradeDialog.toastMessage(msg);
        }
    }

    @Override
    public void usbUpdateEvent(String msg) throws RemoteException {
        Log.d(TAG, "usbUpdateEvent CALL BACK msg: " + msg);
        SoftwareUpgradeDialog softwareUpgradeDialog = mActivity
                .getDialogManager().getSoftwareUpgradeDialog();
        if (softwareUpgradeDialog != null) {
            softwareUpgradeDialog.usbMessageReceived(msg);
        }
    }

    @Override
    public void usbCheckUpdateEvent(int msgType, String msg)
            throws RemoteException {
        Log.d(TAG, "usbCheckUpdateEvent CALL BACK msg: " + msg + ", msfType: "
                + msgType);
        SoftwareUpgradeDialog softwareUpgradeDialog = mActivity
                .getDialogManager().getSoftwareUpgradeDialog();
        if (softwareUpgradeDialog != null) {
            softwareUpgradeDialog.usbCheckUpdateEvent(msgType, msg);
        }
    }

    @Override
    public void mhegStarted(boolean state) throws RemoteException {
        /*
         * ZORANA C 3.0 MERGE Log.i("Java-Bane",
         * "CALLBACK FROM APPLICATION SERVICE - MHEG STATE:" + state);
         * MhegDialog.started = state;
         */
    }

    @Override
    public void syncStarted() throws RemoteException {
        AccountsAndSyncManageAccountsDialog accSyncManageAccDialog = mActivity
                .getDialogManager().getAccountsAndSyncManageAccountsDialog();
        if (accSyncManageAccDialog != null)
            if (accSyncManageAccDialog.isShowing()) {
                accSyncManageAccDialog.syncStarted();
            }
    }

    @Override
    public void syncFinished() throws RemoteException {
        AccountsAndSyncManageAccountsDialog accSyncManageAccDialog = mActivity
                .getDialogManager().getAccountsAndSyncManageAccountsDialog();
        if (accSyncManageAccDialog != null)
            if (accSyncManageAccDialog.isShowing()) {
                accSyncManageAccDialog.syncFinished();
            }
    }

    @Override
    public void mediaMounted(String path) throws RemoteException {
        final String devicePath = " "
                + path.substring(path.lastIndexOf("/") + 1);
        Message.obtain(mHandler, MEDIA_MOUNTED, devicePath).sendToTarget();
    }

    @Override
    public void mediaEjected(String path) throws RemoteException {
        // TODO: Applies on main display only
        final String devicePath = " "
                + path.substring(path.lastIndexOf("/") + 1);
        Message.obtain(mHandler, MEDIA_EJECTED, devicePath).sendToTarget();
    }

    @Override
    public void mediaNotSupported(String path) throws RemoteException {
        final String devicePath = " "
                + path.substring(path.lastIndexOf("/") + 1);
        Message.obtain(mHandler, MEDIA_NOT_SUPPORTED, devicePath)
                .sendToTarget();
    }

    @Override
    public void startUrl(String url) throws RemoteException {
        // Log.e(TAG, "start url:" + url);
        // Uri video = Uri.parse(url);
        // MainActivity.service.getContentListControl().stopVideoPlayback();
        // videoView.setVideoURI(video);
    }

    private void initializeHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HIDE_SUBTITLE_DIALOG: {
                        try {
                            if (mActivity.getSubtitleDialogView() != null) {
                                mActivity.getSubtitleDialogView().hideView();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case MEDIA_MOUNTED: {
                        new A4TVToast(mActivity).showToast(mActivity
                                .getResources().getString(R.string.usb_mounted)
                                + ((String) msg.obj));
                        // file storage dialog refresh
                        ExternalAndLocalStorageDialog extAndLocalDialog = mActivity
                                .getDialogManager()
                                .getExternalAndLocalStorageDialog();
                        if (extAndLocalDialog != null) {
                            extAndLocalDialog.setInitialViews();
                        }
                        break;
                    }
                    case MEDIA_EJECTED: {
                        final int displayId = 0;
                        new A4TVToast(mActivity).showToast(mActivity
                                .getResources().getString(R.string.usb_ejected)
                                + ((String) msg.obj));
                        switch (OSDHandlerHelper.getHandlerState()) {
                            case PVR_STATE_FF_TIME_SHIFT:
                            case PVR_STATE_PAUSE_TIME_SHIFT:
                            case PVR_STATE_PLAY_TIME_SHIFT:
                            case PVR_STATE_REW_TIME_SHIFT:
                            case PVR_STATE_RECORDING:
                            case PVR_STATE_PLAY_PLAY_BACK:
                            case PVR_STATE_REW_PLAY_BACK:
                            case PVR_STATE_PAUSE_PLAY_BACK:
                            case PVR_STATE_FF_PLAY_BACK: {
                                A4TVProgressBarPVR.getControlProviderPVR()
                                        .stop();
                                OSDHandlerHelper
                                        .setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
                                // Start video stream
                                mActivity.getMediaController().startLiveStream(
                                        true);
                                break;
                            }
                            case CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER: {
                                if (!A4TVMultimediaController
                                        .getControlProvider().getContent()
                                        .getFileURL().contains("http:")) {
                                    A4TVMultimediaController
                                            .getControlProvider().stop(
                                                    displayId);
                                    MultimediaGridHelper.hideDlnaOverlays();
                                    OSDHandlerHelper
                                            .setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
                                    // Start video stream
                                    mActivity.getMediaController()
                                            .startLiveStream(true);
                                    // ///////////////////////////////////////////////
                                    // Show multimedia root
                                    // ///////////////////////////////////////////////
                                    if (mActivity.getMultimediaHandler() == null) {
                                        mActivity.initMultimediaHandler();
                                    }
                                    // Update app state of key listener
                                    MainKeyListener
                                            .setAppState(MainKeyListener.MULTIMEDIA_FIRST);
                                    // Init multimedia just in case
                                    MultimediaHandler.multimediaScreen = MultimediaHandler.MULTIMEDIA_FIRST_SCREEN;
                                    MultimediaHandler.secondScreenFolderLevel = 0;
                                    // Open first multimedia screen and reset
                                    // path
                                    mActivity.getMultimediaHandler().new LoadTask(
                                            "/").execute();
                                    // Set flag to true
                                    MainKeyListener.multimediaFromMainMenu = false;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        if (mActivity.getMultimediaHandler() != null) {
                            // Multimedia is showing
                            if (mActivity.getMultimediaHandler()
                                    .getMultimediaDialog().isShowing()) {
                                // Multimedia image viewer is showing
                                if (mActivity.getMultimediaHandler()
                                        .getMultimediaShowDialog().isShowing()) {
                                    // Close mutlimedia image viewer
                                    mActivity.getMultimediaHandler()
                                            .closeMultimediaShow();
                                }
                                if (MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                                    if (MultimediaGridHelper.isBrowsingUSB) {
                                        mActivity.getMultimediaHandler().new LoadTaskMultimediaBack(
                                                "/",
                                                MultimediaHandler.LOAD_BACK_FIRST_SCREEN)
                                                .execute();
                                        MultimediaHandler.secondScreenFolderLevel = 0;
                                        MainKeyListener
                                                .setAppState(MainKeyListener.MULTIMEDIA_FIRST);
                                        MultimediaHandler.multimediaScreen = MultimediaHandler.MULTIMEDIA_FIRST_SCREEN;
                                        MultimediaGridHelper.isBrowsingUSB = false;
                                    }
                                }
                                if (MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                                    mActivity.getMultimediaHandler().new LoadTaskMultimediaBack(
                                            "/",
                                            MultimediaHandler.LOAD_BACK_FIRST_SCREEN_FROM_PVR)
                                            .execute();
                                    MultimediaHandler.secondScreenFolderLevel = 0;
                                    MainKeyListener
                                            .setAppState(MainKeyListener.MULTIMEDIA_FIRST);
                                    MultimediaHandler.multimediaScreen = MultimediaHandler.MULTIMEDIA_FIRST_SCREEN;
                                }
                            }
                        }
                        // stop software update over usb
                        SoftwareUpgradeDialog softwareUpgradeDialog = mActivity
                                .getDialogManager().getSoftwareUpgradeDialog();
                        if (softwareUpgradeDialog != null) {
                            softwareUpgradeDialog.stopThreadFromCallback();
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // file storage dialog refresh
                                ExternalAndLocalStorageDialog extAndLocalDialog = mActivity
                                        .getDialogManager()
                                        .getExternalAndLocalStorageDialog();
                                if (extAndLocalDialog != null) {
                                    extAndLocalDialog.setInitialViews();
                                }
                            }
                        }, 2000);
                        break;
                    }
                    case MEDIA_NOT_SUPPORTED: {
                        new A4TVToast(mActivity).showToast(mActivity
                                .getResources().getString(
                                        R.string.usb_unmountable)
                                + ((String) msg.obj));
                        // file storage dialog refresh
                        /*
                         * ExternalAndLocalStorageDialog extAndLocalDialog =
                         * dialogManager .getExternalAndLocalStorageDialog(); if
                         * (extAndLocalDialog != null)
                         * extAndLocalDialog.setInitialViews();
                         */
                        break;
                    }
                    default:
                        break;
                }
            }
        };
    }
}
