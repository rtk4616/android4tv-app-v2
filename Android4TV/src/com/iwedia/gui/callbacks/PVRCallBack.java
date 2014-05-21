package com.iwedia.gui.callbacks;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IPvrCallback;
import com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVProgressBarPVR.ControlProviderPVR;
import com.iwedia.gui.components.A4TVVideoView;
import com.iwedia.gui.components.dialogs.PVRSettingsDialog;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.dual_video.DualVideoManager;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.osd.infobanner.InfoBannerHandler;
import com.iwedia.gui.pvr.PVRHandler;

/**
 * Handle OneTouchRecord, TimeShift and SmartRecord CallBacks.
 */
public class PVRCallBack extends IPvrCallback.Stub implements OSDGlobal {
    private static final String TAG = "PVRCallback";
    /** Messages */
    private static final int MESSAGE_USB_SPEED = 0;
    private static final int MESSAGE_TIMESHIFT_STOP = 1;
    private static final int MESSAGE_PLAYBACK_STOP = 2;
    private static final int MESSAGE_RECORD_ADD = 3;
    private static final int MESSAGE_RECORD_REMOVE = 4;
    private static final int MESSAGE_RECORD_POSITION = 5;
    private static final int MESSAGE_RECORD_START = 6;
    private static final int MESSAGE_RECORD_STOP = 7;
    /** Instance of MainActivity */
    private MainActivity mActivity = null;
    /** Instance of CallBack Handler. */
    private CallBackHandler mCallBackHandler = null;
    /** UI Handler */
    private Handler mHandler = null;
    /** Fields */
    private int mTimeShiftRecordTime = 0;
    private int mTimeShiftPlayBackTime = 0;

    public PVRCallBack(CallBackHandler callBackHandler, MainActivity activity) {
        mCallBackHandler = callBackHandler;
        mActivity = activity;
        initializeHandler();
    }

    @Override
    public void eventUSBMediaStorageFull() throws RemoteException {
        mCallBackHandler.showToastMessage(R.string.usb_media_storage_is_full);
    }

    @Override
    public void eventUsbSpeed(final int speed) throws RemoteException {
        Log.i(TAG, "speed = " + speed);
        Message.obtain(mHandler, MESSAGE_USB_SPEED, speed).sendToTarget();
    }

    @Override
    public void timeshiftFastForward(int timeshiftSpeed) throws RemoteException {
        /** FF */
        Log.i(TAG, "TimeShift FF - timeshiftSpeed: " + timeshiftSpeed);
        ControlProviderPVR.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.fast_forward_timeshift)
                + " "
                + timeshiftSpeed + "X");
        OSDHandlerHelper.setHandlerState(PVR_STATE_FF_TIME_SHIFT);
    }

    @Override
    public void timeshiftPause() throws RemoteException {
        /** Pause */
        Log.i(TAG, "TimeShift Pause");
        ControlProviderPVR.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.pause_timeshift));
        OSDHandlerHelper.setHandlerState(PVR_STATE_PAUSE_TIME_SHIFT);
    }

    @Override
    public void timeshiftPosition(int recordTime, int playbackTime, int endTime)
            throws RemoteException {
        Log.i(TAG, "TimeShift Position");
        if (mTimeShiftRecordTime != recordTime) {
            PVRHandler.updatePVRTime(recordTime, endTime, PVR_STATE_RECORDING);
            mTimeShiftRecordTime = recordTime;
        }
        if (mTimeShiftPlayBackTime != playbackTime) {
            PVRHandler.updatePVRTime(playbackTime, endTime,
                    CURL_HANDLER_STATE_DO_NOTHING);
            mTimeShiftPlayBackTime = playbackTime;
        }
    }

    @Override
    public void timeshiftRewind(int timeshiftSpeed) throws RemoteException {
        /** REW */
        Log.i(TAG, "TimeShift REW - timeshiftSpeed: " + timeshiftSpeed);
        ControlProviderPVR.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.rewind_timeshift)
                + " "
                + timeshiftSpeed + "X");
        OSDHandlerHelper.setHandlerState(PVR_STATE_REW_TIME_SHIFT);
    }

    @Override
    public void timeshiftPlay() throws RemoteException {
        /** PlayBack started */
        Log.i(TAG, "TimeShift PlayBack Started");
        ControlProviderPVR.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.play_timeshift));
        ControlProviderPVR.setFlagPlay(true);
        OSDHandlerHelper.setHandlerState(PVR_STATE_PLAY_TIME_SHIFT);
    }

    @Override
    public void timeshiftStart() throws RemoteException {
        /** Start */
        Log.i(TAG, "TimeShift Start");
        mTimeShiftRecordTime = 0;
        mTimeShiftPlayBackTime = 0;
        PVRHandler.prepareRecord();
        ControlProviderPVR.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.pause_timeshift));
        OSDHandlerHelper.setHandlerState(PVR_STATE_PAUSE_TIME_SHIFT);
    }

    @Override
    public void timeshiftStop() throws RemoteException {
        Message.obtain(mHandler, MESSAGE_TIMESHIFT_STOP).sendToTarget();
    }

    @Override
    public void playbackFastForward(int speed) throws RemoteException {
        /** FF */
        Log.i(TAG, "FF - playbackFastForward: " + speed);
        ControlProvider.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.fast_forward) + " " + speed + "X");
        OSDHandlerHelper.setHandlerState(PVR_STATE_FF_PLAY_BACK);
    }

    @Override
    public void playbackPause() throws RemoteException {
        /** Pause */
        Log.i(TAG, "Pause");
        ControlProvider.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.pause));
        OSDHandlerHelper.setHandlerState(PVR_STATE_PAUSE_PLAY_BACK);
    }

    @Override
    public void playbackPlay() throws RemoteException {
        if (MainKeyListener.getAppState() == MainKeyListener.MULTIMEDIA_PLAYBACK) {
            /** PlayBack started */
            Log.i(TAG, "PlayBack Started");
            ControlProvider.setFlagPlay(true);
            ControlProvider.setFileDescription(mCallBackHandler
                    .getStringFromRes(R.string.play));
            OSDHandlerHelper.setHandlerState(PVR_STATE_PLAY_PLAY_BACK);
        }
    }

    @Override
    public void playbackPosition(int playTime) throws RemoteException {
        Log.i(TAG, "playTimeChanged: elasped = " + playTime);
        PVRHandler.updatePVRTime(playTime, 0, CURL_HANDLER_STATE_DO_NOTHING);
    }

    @Override
    public void playbackRewind(int speed) throws RemoteException {
        /** REW */
        Log.i(TAG, "REW - playbackRewind: " + speed);
        ControlProvider.setFileDescription(mCallBackHandler
                .getStringFromRes(R.string.rewind) + " " + speed + "X");
        OSDHandlerHelper.setHandlerState(PVR_STATE_REW_PLAY_BACK);
    }

    @Override
    public void playbackStop() throws RemoteException {
        /** Stopped */
        Message.obtain(mHandler, MESSAGE_PLAYBACK_STOP).sendToTarget();
    }

    @Override
    public void recordAdd() throws RemoteException {
        Log.i(TAG, "recordAdd");
        Message.obtain(mHandler, MESSAGE_RECORD_ADD).sendToTarget();
    }

    @Override
    public void recordRemove() throws RemoteException {
        Log.i(TAG, "recordRemove");
        Message.obtain(mHandler, MESSAGE_RECORD_REMOVE).sendToTarget();
    }

    @Override
    public void recordPosition(int recordTime, int endTime)
            throws RemoteException {
        Log.i(TAG, "recordPosition: recordTime = " + recordTime + " endTime = "
                + endTime);
        Message.obtain(mHandler, MESSAGE_RECORD_POSITION).sendToTarget();
        if (mActivity.getPageCurl().getCurrentState() != STATE_MULTIMEDIA_CONTROLLER) {
            PVRHandler.updatePVRTime(recordTime, endTime, PVR_STATE_RECORDING);
        }
    }

    @Override
    public void recordStart() throws RemoteException {
        Log.i(TAG, "recordStart");
        Message.obtain(mHandler, MESSAGE_RECORD_START).sendToTarget();
    }

    @Override
    public void recordStop() throws RemoteException {
        Log.i(TAG, "recordStop");
        Message.obtain(mHandler, MESSAGE_RECORD_STOP);
    }

    private void initializeHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_USB_SPEED: {
                        PVRSettingsDialog.setUsbSpeed((Integer) msg.obj);
                        break;
                    }
                    case MESSAGE_TIMESHIFT_STOP: {
                        Log.i(TAG, "TimeShift Stopped");
                        A4TVProgressBarPVR.getControlProviderPVR()
                                .prepareStop();
                        try {
                            mActivity.service.getContentListControl()
                                    .startVideoPlayback();
                        } catch (RemoteException e) {
                            Log.e(TAG,
                                    "There was an error in Starting Live Stream.",
                                    e);
                        }
                        break;
                    }
                    case MESSAGE_PLAYBACK_STOP: {
                        if (MainKeyListener.getAppState() == MainKeyListener.MULTIMEDIA_PLAYBACK) {
                            // TODO: Applies on primary channel only
                            final int mDisplayId = 0;
                            if (mActivity.getPageCurl()
                                    .getPvrPlayerController() != null) {
                                mActivity.getPageCurl()
                                        .getPvrPlayerController()
                                        .prepareStop(mDisplayId);
                            }
                            // Blank Screen
                            try {
                                mActivity.service.getVideoControl().videoBlank(
                                        0, true);
                            } catch (Exception e) {
                                Log.i(TAG, "Can not video blank!", e);
                            }
                        } else {
                            if (mActivity.getSecondaryVideoView().getPlayMode() == A4TVVideoView.PAP_DISPLAY_MODE) {
                                if (mActivity.getPrimaryMultimediaVideoView() == null)
                                    mActivity.getPrimaryVideoView().setScaling(
                                            0, 0, 1920, 1080);
                            }
                            mActivity.getSecondaryVideoView().hide();
                            try {
                                mActivity.service.getContentListControl()
                                        .setActiveContent(null, 1);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            /** Notify pvr had stoped */
                            DualVideoManager.syncPVRHandle.sendEmptyMessage(1);
                        }
                        break;
                    }
                    case MESSAGE_RECORD_ADD: {
                        final int command = 0;
                        final String param = "EXIT";
                        try {
                            if (mActivity.service.getHbbTvControl()
                                    .notifyAppMngr(command, param)) {
                                mActivity.webDialog.getHbbTVView().setAlpha(
                                        (float) 0.00);
                                mActivity.setKeySet(0);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        OSDHandlerHelper.setHandlerState(PVR_STATE_RECORDING);
                        PVRHandler.prepareRecord();
                        ControlProviderPVR.setFileDescription(mCallBackHandler
                                .getStringFromRes(R.string.shedule_record));
                        mActivity.getPageCurl().multimediaControllerPVR(false);
                        break;
                    }
                    case MESSAGE_RECORD_REMOVE: {
                        A4TVProgressBarPVR.getControlProviderPVR()
                                .prepareStop();
                        final int command = 0;
                        final String param = "EXIT";
                        try {
                            if (mActivity.service.getHbbTvControl()
                                    .notifyAppMngr(command, param)) {
                                mActivity.webDialog.getHbbTVView().setAlpha(
                                        (float) 0.00);
                                mActivity.setKeySet(0);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case MESSAGE_RECORD_POSITION: {
                        try {
                            if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                                if (!A4TVProgressBarPVR.getControlProviderPVR()
                                        .isDiskNearlyFull()) {
                                    IExternalLocalStorageSettings lStorage = mActivity.service
                                            .getSystemControl()
                                            .getExternalLocalStorageControl();
                                    if (lStorage.isExternalMemoryFull()) {
                                        A4TVProgressBarPVR
                                                .getControlProviderPVR()
                                                .setFlagDiskNearlyFull(true);
                                        mActivity.getPageCurl()
                                                .multimediaControllerPVR(true);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG,
                                    "Could not getExternalLocalStorageControl",
                                    e);
                        }
                        break;
                    }
                    case MESSAGE_RECORD_START: {
                        if (!A4TVProgressBarPVR
                                .getControlProviderPVR()
                                .getFileDescription()
                                .equals(mCallBackHandler
                                        .getStringFromRes(R.string.shedule_record))) {
                            if (A4TVProgressBarPVR
                                    .getControlProviderPVR()
                                    .getFileDescription()
                                    .equals(mCallBackHandler
                                            .getStringFromRes(R.string.prepare_timeshift))) {
                                ControlProviderPVR
                                        .setFileDescription("Timeshift Recording");
                            } else {
                                if (A4TVProgressBarPVR
                                        .getControlProviderPVR()
                                        .getFileDescription()
                                        .equals(mCallBackHandler
                                                .getStringFromRes(R.string.prepare_record))) {
                                    ControlProviderPVR
                                            .setFileDescription(mCallBackHandler
                                                    .getStringFromRes(R.string.record));
                                }
                            }
                        }
                        /** This is added for Schedule Record */
                        PVRHandler.prepareRecord();
                        break;
                    }
                    case MESSAGE_RECORD_STOP: {
                        mActivity.getPageCurl().multimediaControllerPVR(true);
                        A4TVProgressBarPVR.getControlProviderPVR()
                                .prepareStop();
                        if (mActivity.getPageCurl() instanceof InfoBannerHandler) {
                            mActivity.getPageCurl().multimediaControllerPVR(
                                    true);
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        };
    }
}
