package com.iwedia.gui.pvr;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVProgressBarPVR.ControlProviderPVR;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.Conversions;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.util.DateTimeConversions;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handle PVR Functions.
 * 
 * @author Milos Milanovic
 */
public class PVRHandler implements OSDGlobal {
    private static final String TAG = "PVRHandler";
    private static String pvrCurrentTime = "";
    private static int pvrEndTime = 0;
    // TODO: Applies on main display only
    private final int mDisplayId = 0;

    public static void updatePVRTime(int elapsedTime, int endTime, int type) {
        
        if(!ConfigHandler.TVPLATFORM){
            elapsedTime*=1000;
            //endTime*=1000;  //TODO
        }
        
        if (MainActivity.activity.getPageCurl().getCurrentState() == STATE_PVR) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date date;
            long currentMSec = 0;
            int nextMSec;
            try {
                date = sdf.parse(pvrCurrentTime);
                currentMSec = date.getTime();
            } catch (Exception e) {
                Log.i(TAG, "Method: updatePVRTime, rawMilliseconds");
                e.printStackTrace();
            }
            if (type == PVR_STATE_RECORDING) {
                /** Add to description that disk is nearly full */
                if (A4TVProgressBarPVR.getControlProviderPVR()
                        .isDiskNearlyFull()
                        && OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                    ControlProviderPVR.setFileDescription(MainActivity.activity
                            .getString(R.string.pvr_low_disk_space));
                }
                pvrEndTime = endTime;
                int progressValue = Conversions.getPVRPassedPercent(
                        (elapsedTime), endTime);
                A4TVProgressBarPVR.getControlProviderPVR()
                        .setSecondaryProgressValue(progressValue);
                nextMSec = (int) (currentMSec + (pvrEndTime));
                A4TVProgressBarPVR.getControlProviderPVR().setElapsedTime(
                        (int) currentMSec);
                A4TVProgressBarPVR.getControlProviderPVR().setSecondTime(
                        elapsedTime);
                A4TVProgressBarPVR.getControlProviderPVR()
                        .setDuration(nextMSec);
                MainActivity.activity.getPageCurl()
                        .updatePlayingTime((int) currentMSec, nextMSec,
                                elapsedTime, progressValue);
            } else {
                switch (OSDHandlerHelper.getHandlerState()) {
                    case PVR_STATE_REW_TIME_SHIFT:
                    case PVR_STATE_PAUSE_TIME_SHIFT:
                    case PVR_STATE_FF_TIME_SHIFT:
                    case PVR_STATE_PLAY_TIME_SHIFT: {
                        if (pvrEndTime != 0) {
                            A4TVProgressBarPVR.getControlProviderPVR()
                                    .setProgressValue(
                                            Conversions.getPVRPassedPercent(
                                                    (elapsedTime % pvrEndTime),
                                                    pvrEndTime));
                        }
                        A4TVProgressBarPVR.getControlProviderPVR()
                                .setFirstTime(elapsedTime);
                        MainActivity.activity.getPageCurl()
                                .updateTimeShiftPlayingTime(
                                        elapsedTime,
                                        Conversions.getPVRPassedPercent(
                                                (elapsedTime % pvrEndTime),
                                                pvrEndTime));
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        } else if (MainActivity.activity.getPageCurl().getCurrentState() == STATE_MULTIMEDIA_CONTROLLER) {
            switch (OSDHandlerHelper.getHandlerState()) {
                case PVR_STATE_REW_PLAY_BACK:
                case PVR_STATE_PAUSE_PLAY_BACK:
                case PVR_STATE_FF_PLAY_BACK:
                case PVR_STATE_PLAY_PLAY_BACK: {
                    A4TVMultimediaController.getControlProvider()
                            .setElapsedTime(elapsedTime);
                    A4TVMultimediaController.getControlProvider().setDuration(
                            (Integer.valueOf(A4TVMultimediaController
                                    .getControlProvider().getContent()
                                    .getDurationTime())) * 1000);
                    int progressValue = Conversions.getPVRPassedPercent(
                            elapsedTime, endTime);
                    MainActivity.activity.getPageCurl().updatePlayingTime(0,
                            endTime, elapsedTime, progressValue);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    public boolean pvrStop() {
        Log.d(TAG,
                "pvrStop, handlerState: " + OSDHandlerHelper.getHandlerState());
        /* set screensaver */
        MainActivity.screenSaverDialog
                .setScreenSaverCause(MainActivity.screenSaverDialog.LIVE);
        MainActivity.screenSaverDialog.updateScreensaverTimer();
        switch (OSDHandlerHelper.getHandlerState()) {
            case PVR_STATE_RECORDING: {
                try {
                    /*
                     * MainActivity.service.getPvrControl().destroyRecord(
                     * MainActivity.service.getContentListControl()
                     * .getActiveContent(mDisplayId) .getIndexInMasterList());
                     */
                    MainActivity.service.getPvrControl().destroyRecord(0);
                    OSDHandlerHelper.setHandlerState(PVR_STATE_STOP_TIME_SHIFT);
                    // } else {
                    // return false;
                    // }
                } catch (Exception e) {
                    Log.e(TAG, "StopRecord", e);
                }
                break;
            }
            case PVR_STATE_REW_TIME_SHIFT:
            case PVR_STATE_FF_TIME_SHIFT:
            case PVR_STATE_PLAY_TIME_SHIFT:
            case PVR_STATE_PAUSE_TIME_SHIFT: {
                try {
                    /** Save Current Subtitle State */
                    saveCurrentSubtitleState();
                    MainActivity.service.getPvrControl().stopTimeshift(false);
                    // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_STOP_TIME_SHIFT);
                    /*
                     * } else { return false; }
                     */
                    // MainActivity.service.getPvrControl().stopTimeshift(false);
                    // CurlHandler.setCurlHandlerState(PVR_STATE_STOP_TIME_SHIFT);
                } catch (Exception e) {
                    Log.e(TAG, "StopTimeShift", e);
                    return false;
                }
                break;
            }
            case PVR_STATE_REW_PLAY_BACK:
            case PVR_STATE_FF_PLAY_BACK:
            case PVR_STATE_PLAY_PLAY_BACK:
            case PVR_STATE_PAUSE_PLAY_BACK: {
                try {
                    Log.i(TAG, "PVR_STATE_STOP_PLAY_BACK");
                    MainActivity.service.getPvrControl().stopPlayback();
                    // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_STOP_PLAY_BACK);
                    // } else {
                    // return false;
                    // }
                } catch (Exception e) {
                    Log.e(TAG, "StopPlayback", e);
                    return false;
                }
                break;
            }
            default: {
                Log.i(TAG, "PVR STOP FALSE");
                return false;
                // break;
            }
        }
        Log.i(TAG, "PVR STOP TRUE");
        return true;
    }

    public boolean pvrFastForward() {
        Log.d(TAG, "pvrFF, handlerState: " + OSDHandlerHelper.getHandlerState());
        switch (OSDHandlerHelper.getHandlerState()) {
            case PVR_STATE_FF_TIME_SHIFT:
            case PVR_STATE_REW_TIME_SHIFT:    
            case PVR_STATE_PAUSE_TIME_SHIFT:
            case PVR_STATE_PLAY_TIME_SHIFT: {
                try {
                    MainActivity.service.getPvrControl().fastForward();
                    // // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_FF_TIME_SHIFT);
                    /*
                     * } else { return false; }
                     */
                    // MainActivity.service.getPvrControl()
                    // .fastForwardTimeshift(false);
                    // CurlHandler.setCurlHandlerState(PVR_STATE_FF_TIME_SHIFT);
                    Log.i(TAG,
                            "Current State: "
                                    + OSDHandlerHelper.getHandlerState());
                } catch (Exception e) {
                    Log.e(TAG, "fastForwardTimeshift", e);
                    return false;
                }
                break;
            }
            case PVR_STATE_FF_PLAY_BACK:
            case PVR_STATE_PLAY_PLAY_BACK: {
                try {
                    MainActivity.service.getPvrControl().fastForward();
                    // // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_FF_PLAY_BACK);
                    /*
                     * } else { return false; }
                     */
                } catch (Exception e) {
                    Log.e(TAG, "fastForwardPlayback", e);
                    return false;
                }
                break;
            }
            default: {
                return false;
                // break;
            }
        }
        return true;
    }

    public boolean pvrRewind() {
        Log.d(TAG, "pvrRW, handlerState: " + OSDHandlerHelper.getHandlerState());
        switch (OSDHandlerHelper.getHandlerState()) {
            case PVR_STATE_REW_TIME_SHIFT:
            case PVR_STATE_FF_TIME_SHIFT:    
            case PVR_STATE_PAUSE_TIME_SHIFT:
            case PVR_STATE_PLAY_TIME_SHIFT: {
                try {
                    MainActivity.service.getPvrControl().rewind();
                    // // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_REW_TIME_SHIFT);
                    /*
                     * } else { return false; }
                     */
                    // MainActivity.service.getPvrControl()
                    // .fastForwardTimeshift(false);
                    // CurlHandler.setCurlHandlerState(PVR_STATE_FF_TIME_SHIFT);
                    Log.i(TAG,
                            "Current State: "
                                    + OSDHandlerHelper.getHandlerState());
                } catch (Exception e) {
                    Log.e(TAG, "rewindTimeshift", e);
                    return false;
                }
                break;
            }
            case PVR_STATE_REW_PLAY_BACK:
            case PVR_STATE_PLAY_PLAY_BACK: {
                try {
                    MainActivity.service.getPvrControl().rewind();
                    // // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_REW_PLAY_BACK);
                    /*
                     * } else { return false; }
                     */
                } catch (Exception e) {
                    Log.e(TAG, "rewindPlayback", e);
                    return false;
                }
                break;
            }
            default: {
                return false;
                // break;
            }
        }
        return true;
    }

    public boolean pvrRecord() {
        Log.d(TAG, "pvrRecord " + OSDHandlerHelper.getHandlerState());
        switch (OSDHandlerHelper.getHandlerState()) {
            case STATE_INFO_BANNER_HIDDEN:
            case STATE_INFO_BANNER_SHOWN:
            case CURL_HANDLER_STATE_DO_NOTHING:
            case PVR_STATE_STOP_PLAY_BACK:
            case PVR_STATE_STOP_TIME_SHIFT: {
                try {
                    Log.d(TAG, "pvrRecord: createOneTouchRecord");
                    MainActivity.service.getPvrControl().createOnTouchRecord(
                            MainActivity.service.getContentListControl()
                                    .getActiveContent(mDisplayId)
                                    .getIndexInMasterList());
                    // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_RECORDING);
                    /*
                     * } else { return false; }
                     */
                } catch (Exception e) {
                    Log.e(TAG, "createOneTouchRecord", e);
                    return false;
                }
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }

    public boolean pvrPlay() {
        Log.d(TAG,
                "pvrPlay, handlerState: " + OSDHandlerHelper.getHandlerState());
        /* set screensaver */
        MainActivity.screenSaverDialog
                .setScreenSaverCause(MainActivity.screenSaverDialog.LIVE);
        MainActivity.screenSaverDialog.updateScreensaverTimer();
        switch (OSDHandlerHelper.getHandlerState()) {
            case PVR_STATE_REW_TIME_SHIFT:
            case PVR_STATE_FF_TIME_SHIFT:
            case PVR_STATE_PAUSE_TIME_SHIFT: {
                try {
                    MainActivity.service.getPvrControl().pause(false);
                    /** Return Previous Subtitle State */
                    setPreviousSubtitleState();
                    // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_PLAY_TIME_SHIFT);
                    /*
                     * } else { return false; }
                     */
                    // MainActivity.service.getPvrControl().pauseTimeshift(false);
                    // CurlHandler.setCurlHandlerState(PVR_STATE_PLAY_TIME_SHIFT);
                } catch (Exception e) {
                    Log.e(TAG, "pauseTimeshift", e);
                    return false;
                }
                break;
            }
            case PVR_STATE_REW_PLAY_BACK:
            case PVR_STATE_FF_PLAY_BACK:
            case PVR_STATE_PAUSE_PLAY_BACK: {
                try {
                    MainActivity.service.getPvrControl().pause(false);
                    // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_PLAY_PLAY_BACK);
                    /*
                     * } else { return false; }
                     */
                } catch (Exception e) {
                    Log.e(TAG, "pausePlayback", e);
                    return false;
                }
                break;
            }
            default: {
                return false;
                // break;
            }
        }
        return true;
    }

    public boolean pvrPause() {
        Log.d(TAG,
                "pvrPause, handlerState: " + OSDHandlerHelper.getHandlerState());
        /* set screensaver */
        MainActivity.screenSaverDialog
                .setScreenSaverCause(MainActivity.screenSaverDialog.PAUSE);
        MainActivity.screenSaverDialog.updateScreensaverTimer();
        switch (OSDHandlerHelper.getHandlerState()) {
            case STATE_INFO_BANNER_HIDDEN:
            case STATE_INFO_BANNER_SHOWN:
            case CURL_HANDLER_STATE_DO_NOTHING:
            case PVR_STATE_STOP_PLAY_BACK:
            case PVR_STATE_STOP_TIME_SHIFT: {
                try {
                    /** Save Current Subtitle State */
                    saveCurrentSubtitleState();
                    MainActivity.service.getPvrControl().startTimeshift();
                    // Set Flag for CurlEffect
                    OSDHandlerHelper
                            .setHandlerState(PVR_STATE_PAUSE_TIME_SHIFT);
                    /*
                     * } else { return false; }
                     */
                    // MainActivity.service.getPvrControl().startTimeshift();
                    // // Set Flag for CurlEffect
                    // CurlHandler.setCurlHandlerState(PVR_STATE_PAUSE_TIME_SHIFT);
                } catch (Exception e) {
                    Log.e(TAG, "startTimeshift", e);
                    return false;
                }
                break;
            }
            case PVR_STATE_REW_TIME_SHIFT:
            case PVR_STATE_FF_TIME_SHIFT:
            case PVR_STATE_PLAY_TIME_SHIFT: {
                try {
                    /** Save Current Subtitle State */
                    saveCurrentSubtitleState();
                    MainActivity.service.getPvrControl().pause(true);
                    // Set Flag for CurlEffect
                    OSDHandlerHelper
                            .setHandlerState(PVR_STATE_PAUSE_TIME_SHIFT);
                    /*
                     * } else { return false; }
                     */
                    // MainActivity.service.getPvrControl().pauseTimeshift(true);
                    // CurlHandler.setCurlHandlerState(PVR_STATE_PAUSE_TIME_SHIFT);
                } catch (Exception e) {
                    Log.e(TAG, "pauseTimeshift", e);
                    return false;
                }
                break;
            }
            case PVR_STATE_PLAY_PLAY_BACK: {
                try {
                    MainActivity.service.getPvrControl().pause(true);
                    // Set Flag for CurlEffect
                    OSDHandlerHelper.setHandlerState(PVR_STATE_PAUSE_PLAY_BACK);
                    /*
                     * } else { return false; }
                     */
                } catch (Exception e) {
                    Log.e(TAG, "pausePlayback", e);
                    return false;
                }
                break;
            }
            default: {
                return false;
                // break;
            }
        }
        return true;
    }

    public static void stopPVRPlayBack() {
        try {
            /* set screensaver */
            MainActivity.screenSaverDialog
                    .setScreenSaverCause(MainActivity.screenSaverDialog.LIVE);
            MainActivity.screenSaverDialog.updateScreensaverTimer();
            MainActivity.service.getPvrControl().stopPlayback();
        } catch (Exception e) {
            Log.i(TAG, "Can not Stop PVR PlayBack!", e);
        }
    }

    public static void prepareRecord() {
        // Hide all dialogs when pvr recording start
        if (MainKeyListener.getAppState() != MainKeyListener.CLEAN_SCREEN
                && MainKeyListener.getAppState() != MainKeyListener.PVR) {
            MainActivity.activity.getDialogManager().hideAllDialogs();
            new A4TVToast(MainActivity.activity)
                    .showToast(R.string.pvr_schedule_start_message);
        }
        if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
            A4TVProgressBarPVR.getControlProviderPVR().setFlagDiskFull(false);
            A4TVProgressBarPVR.getControlProviderPVR().setFlagDiskNearlyFull(
                    false);
            if (!A4TVProgressBarPVR.getControlProviderPVR().isFlagRecord()) {
                A4TVProgressBarPVR.getControlProviderPVR().setFlagRecord(true);
                setStringsForRecord();
                /** This is added for Schedule Record */
                if (OSDHandlerHelper.getHandlerState() != PVR_STATE_RECORDING) {
                    OSDHandlerHelper.setHandlerState(PVR_STATE_RECORDING);
                    MainActivity.activity.getPageCurl()
                            .multimediaControllerPVR(false);
                }
            }
        }
    }

    public static void setStringsForRecord() {
        if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
            try {
                pvrCurrentTime = getCurrentTimeFromStream();
            } catch (Exception e) {
                Log.i(TAG, "Method: startPVR strTime");
                e.printStackTrace();
                pvrCurrentTime = "00:00:00";
            }
            // Set KeyListener State for OneTouch,Schedule and TimeShift
            MainKeyListener.setAppState(MainKeyListener.PVR);
            Content curChannelContent = MainActivity.activity.getPageCurl()
                    .getChannelChangeHandler().getCurrentChannelContent();
            if (curChannelContent != null) {
                String strName = curChannelContent.getName();
                // String strName = MainActivity.activity.getPageCurl()
                // .getChannelChangeHandler().getActiveContent().getName();
                ControlProviderPVR.setFileName(strName);
            }
        }
    }

    /** Get Time from Stream */
    private static String getCurrentTimeFromStream() throws RemoteException {
        String strTime;
        strTime = DateTimeConversions.getTimeSting(MainActivity.service
                .getSystemControl().getDateAndTimeControl().getTimeDate()
                .getCalendar().getTime());
        return strTime;
    }

    /** Check if usb is mounted */
    public static boolean detectUSB() {
        // String usbPath = "/mnt/media";
        // try {
        // IExternalLocalStorageSettings lStorage = MainActivity.service
        // .getSystemControl()
        // .getExternalLocalStorageControl();
        // usbPath = lStorage.getExternalStoragePath();
        // } catch (RemoteException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // File mFile = new File(usbPath);
        // if (mFile.exists()) {
        // if (mFile.isDirectory()) {
        // String[] mFiles = mFile.list();
        // if (mFiles.length > 0) {
        // // Usb found
        // return true;
        // }
        // }
        // }
        // NO Usb Drive
        return false;
    }

    private void saveCurrentSubtitleState() {
        try {
            /****************************/
            /** Save Previous State of Subtitle */
            /*
             * if
             * (!MainActivity.service.getSubtitleControl().getSubtitleEnabled())
             * { int index = MainActivity.service.getSubtitleControl()
             * .getCurrentSubtitleTrackIndex(); if (index > -1) { // Store title
             * track index MainActivity.subtitleTitleTrackIndex = index;
             * MainActivity.showSubtitleWhenTeletextHide = true; } }
             */
            Log.e(TAG, "NO need to saveCurrentSubtitleState!!!");
            /****************************/
        } catch (Exception e) {
            Log.e(TAG, "There was problem Getting Current Subtitle State!", e);
        }
    }

    public static void setPreviousSubtitleState() {
        /*********************************/
        /** Set Previous State of Subtitle */
        try {
            /*
             * if (MainActivity.showSubtitleWhenTeletextHide) {
             * MainActivity.service.getSubtitleControl()
             * .setCurrentSubtitleTrack( MainActivity.subtitleTitleTrackIndex);
             * MainActivity.subtitleTitleTrackIndex = -1;
             * MainActivity.showSubtitleWhenTeletextHide = false; }
             */
            Log.e(TAG, "NO need to setPreviousSubtitleState!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*********************************/
    }
}
