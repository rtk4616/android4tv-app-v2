package com.iwedia.gui.dual_video;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.A4TVVideoView;
import com.iwedia.gui.content_list.AllHandler;
import com.iwedia.gui.listeners.A4TVVideoViewSecondaryPAPOnPreparedListener;
import com.iwedia.gui.listeners.A4TVVideoViewSecondaryPIPOnPreparedListener;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;

public class DualVideoManager {
    public static final String TAG = "DualVideoManager";
    private MainActivity activity;
    public static pvrStopHandler syncPVRHandle;

    /** Default constructor */
    public DualVideoManager(MainActivity activity) {
        this.activity = activity;
        syncPVRHandle = new pvrStopHandler();
    }

    public A4TVVideoView getPrimaryDisplayUnit() {
        return activity.getPrimaryVideoView();
    }

    public A4TVVideoView getSecondaryDisplayUnit() {
        return activity.getSecondaryVideoView();
    }

    /** Play content in primary display unit */
    public boolean play(Content content) {
        return false;
    }

    /** Play content in PiP */
    public boolean playPiP(Content content) {
        // Get clicked content object and indicate secondary display playback
        Boolean isContentActive = false;
        Log.d(TAG, "playPiP ");
        try {
            IContentListControl contentListControl = MainActivity.service
                    .getContentListControl();
            Content secondaryContent = contentListControl
                    .getActiveContent(MainActivity.SECONDARY_DISPLAY_UNIT_ID);
            Content primaryContent = contentListControl
                    .getActiveContent(MainActivity.PRIMARY_DISPLAY_UNIT_ID);
            if (secondaryContent != null) {
                Log.d(TAG,
                        "playPiP - secondary: " + secondaryContent.toString());
            } else {
                Log.d(TAG, "playPiP - secondary is null");
            }
            if (content == null) {
                Log.e(TAG, "playPiP - Not valid content - null");
                return false;
            }
            if (!checkSupportedScenario(content,
                    MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                return false;
            }
            if (secondaryContent != null) {
                isContentActive = content.equals(secondaryContent);
                Log.d(TAG, "playPiP - isContentActive: " + isContentActive);
            }
            if (!isContentActive) {
                // //////////////////////////////////////////////////////////////////////
                // The requestet content is not active in secondary display unit
                // (PIP or PAP) */
                // //////////////////////////////////////////////////////////////////////
                if (null != secondaryContent) {
                    // //////////////////////////////////////////////////////////////////////
                    // A content is active in secondary display unit (PIP or
                    // PAP) */
                    // //////////////////////////////////////////////////////////////////////
                    Log.d(TAG,
                            "playPiP - Switching to content: "
                                    + content.toString());
                    Log.d(TAG, "playPiP - Stopping already active content: "
                            + secondaryContent.toString());
                    stopContent(secondaryContent,
                            MainActivity.SECONDARY_DISPLAY_UNIT_ID, true);
                    if (secondaryContent.getFilterType() == FilterType.PVR_RECORDED) {
                        // Waiting for stop event
                        Log.d(TAG, "playPiP -  Wait for PVR_STOP EVENT ... ");
                        Log.d(TAG, "playPiP - Next state ... PIP_DISPLAY_MODE");
                        syncPVRHandle.waitAndSynchronize(content,
                                A4TVVideoView.PIP_DISPLAY_MODE);
                        MainKeyListener.returnToStoredAppState();
                        MainActivity.activity.getMultimediaHandler()
                                .closeMultimedia();
                        return false;
                    }
                    getSecondaryDisplayUnit().hide();
                    /* if previous content active in PAP, enable HbbTV */
                    if ((getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PAP_DISPLAY_MODE)
                            && (primaryContent.getSourceType() != SourceType.ANALOG)
                            && (primaryContent.getFilterType() != FilterType.INPUTS)) {
                        /* Enable HbbTV */
                        MainActivity.activity.enableHbbTV();
                    }
                }
                // //////////////////////////////////////////////////////////////////////
                // No content is active in secondary display unit (PIP or PAP)
                // */
                // //////////////////////////////////////////////////////////////////////
                Log.d(TAG, "playPiP - Play content: " + content.toString());
                if (MainActivity.activity.getPrimaryMultimediaVideoView() == null) {
                    getPrimaryDisplayUnit().setScaling(0, 0, 1920, 1080);
                }
                if (content.getFilterType() != FilterType.MULTIMEDIA) {
                    getSecondaryDisplayUnit().setOnPreparedListener(
                            new A4TVVideoViewSecondaryPIPOnPreparedListener(
                                    getSecondaryDisplayUnit(), content));
                    getSecondaryDisplayUnit().updateVisibility(View.VISIBLE);
                } else {
                    playContent(content,
                            MainActivity.SECONDARY_DISPLAY_UNIT_ID, true);
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.PIP_DISPLAY_MODE);
                }
            } else {
                // //////////////////////////////////////////////////////////////////////
                // The same content in already secondary display unit (PIP) */
                // //////////////////////////////////////////////////////////////////////
                if (getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PIP_DISPLAY_MODE) {
                    Log.d(TAG,
                            "playPiP - Already running in PIP, stopping content: "
                                    + content.toString());
                    stop(MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                } else {
                    // //////////////////////////////////////////////////////////////////////
                    // There same content in already secondary display unit
                    // (PAP) */
                    // //////////////////////////////////////////////////////////////////////
                    Log.d(TAG,
                            "playPiP - Switching the content from PAP to PIP: "
                                    + content.toString());
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() == null) {
                        getPrimaryDisplayUnit().setScaling(0, 0, 1920, 1080);
                    }
                    MainActivity.activity.updatePIPCoordinates();
                    getSecondaryDisplayUnit().gotoPIP();
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.PIP_DISPLAY_MODE);
                    if ((primaryContent.getSourceType() != SourceType.ANALOG)
                            && (primaryContent.getFilterType() != FilterType.INPUTS)) {
                        /* Enable HbbTV */
                        MainActivity.activity.enableHbbTV();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Play content in PaP */
    public boolean playPaP(Content content) {
        // Get clicked content object and indicate secondary display playback
        Boolean isContentActive = false;
        Log.d(TAG, "playPaP ");
        try {
            IContentListControl contentListControl = MainActivity.service
                    .getContentListControl();
            Content secondaryContent = contentListControl
                    .getActiveContent(MainActivity.SECONDARY_DISPLAY_UNIT_ID);
            Content primaryContent = contentListControl
                    .getActiveContent(MainActivity.PRIMARY_DISPLAY_UNIT_ID);
            if (primaryContent != null) {
                Log.d(TAG, "playPaP - primary: " + primaryContent.toString());
            }
            if (secondaryContent != null) {
                Log.d(TAG,
                        "playPaP - secondary: " + secondaryContent.toString());
            }
            if (content == null) {
                Log.e(TAG, "playPaP Not valid content - null");
                return false;
            }
            if (!checkSupportedScenario(content,
                    MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                return false;
            }
            Log.d(TAG, "playPaP - contentToPlay: " + content.toString());
            if (secondaryContent != null) {
                isContentActive = content.equals(secondaryContent);
                Log.d(TAG, "playPaP - isContentActive: " + isContentActive);
            }
            if (!isContentActive) {
                if (null != secondaryContent) {
                    // //////////////////////////////////////////////////////////////////////
                    // There is a content already active in secondary display
                    // unit (PIP or PAP) */
                    // //////////////////////////////////////////////////////////////////////
                    Log.d(TAG,
                            "playPaP - Switching to content: "
                                    + content.toString());
                    Log.d(TAG, "playPaP - Stopping already active content: "
                            + secondaryContent.toString());
                    stopContent(secondaryContent,
                            MainActivity.SECONDARY_DISPLAY_UNIT_ID, false);
                    if (secondaryContent.getFilterType() == FilterType.PVR_RECORDED) {
                        // Waiting for stop event
                        Log.d(TAG, "playPaP -  Wait for PVR_STOP EVENT ... ");
                        Log.d(TAG, "playPaP - Next state ... PAP_DISPLAY_MODE");
                        syncPVRHandle.waitAndSynchronize(content,
                                A4TVVideoView.PAP_DISPLAY_MODE);
                        MainKeyListener.returnToStoredAppState();
                        MainActivity.activity.getMultimediaHandler()
                                .closeMultimedia();
                        return false;
                    }
                    getSecondaryDisplayUnit().hide();
                }
                /* Disable HbbTV */
                MainActivity.activity.disableHbbTV();
                Log.d(TAG, "playPaP - Play content: " + content.toString());
                if (MainActivity.activity.getPrimaryMultimediaVideoView() == null) {
                    getPrimaryDisplayUnit().setScaling(0, 0, 960, 1080);
                }
                if (content.getFilterType() != FilterType.MULTIMEDIA) {
                    getSecondaryDisplayUnit().setOnPreparedListener(
                            new A4TVVideoViewSecondaryPAPOnPreparedListener(
                                    getSecondaryDisplayUnit(), content));
                    getSecondaryDisplayUnit().updateVisibility(View.VISIBLE);
                } else {
                    playContent(content,
                            MainActivity.SECONDARY_DISPLAY_UNIT_ID, false);
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.PAP_DISPLAY_MODE);
                }
            } else {
                // //////////////////////////////////////////////////////////////////////
                // There same content in already secondary display unit (PAP) */
                // //////////////////////////////////////////////////////////////////////
                if (getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PAP_DISPLAY_MODE) {
                    Log.d(TAG,
                            "playPaP - Already running in PIP, stopping content: "
                                    + content.toString());
                    stop(MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                } else {
                    // //////////////////////////////////////////////////////////////////////
                    // There same content in already secondary display unit
                    // (PIP) */
                    // //////////////////////////////////////////////////////////////////////
                    if (!checkSupportedScenario(content,
                            MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                        return false;
                    }
                    Log.d(TAG,
                            "playPaP - Switching the content from PIP to PAP: "
                                    + content.toString());
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() == null) {
                        getPrimaryDisplayUnit().setScaling(0, 0, 960, 1080);
                    }
                    getSecondaryDisplayUnit().gotoPaP(
                            MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.PAP_DISPLAY_MODE);
                    /* Disable HbbTV */
                    MainActivity.activity.disableHbbTV();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean stopContent(Content content, int displayID, boolean isPiP) {
        Log.d(TAG, "stopContent: " + content.toString());
        if (content.getFilterType() == FilterType.MULTIMEDIA) {
            // Multimedia (dlna playback) - stop
            Log.d(TAG,
                    "stopContent - Stopping multimedia content: "
                            + content.toString());
            if (isPiP) {
                MainActivity.activity
                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PIP);
            } else {
                MainActivity.activity
                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PAP);
            }
            try {
                MainActivity.service.getContentListControl().setActiveContent(
                        null, MainActivity.SECONDARY_DISPLAY_UNIT_ID);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (content.getFilterType() == FilterType.PVR_RECORDED) {
            // PVR playback - stop
            Log.d(TAG,
                    "stopContent - Stopping PVR content: " + content.toString());
            try {
                MainActivity.service.getPvrControl().stopPlayback();
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Regular TV content or input - stop
            Log.d(TAG,
                    "stopContent - Stopping live/input content: "
                            + content.toString());
            try {
                MainActivity.service.getContentListControl().stopContent(
                        content, displayID);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean playContent(Content content, int displayID, boolean isPiP) {
        Log.d(TAG, "playContent: " + content.toString());
        if (content.getFilterType() == FilterType.MULTIMEDIA) {
            // Multimedia (dlna playback) - play
            Log.d(TAG,
                    "playContent - play multimedia content: "
                            + content.toString());
            if (isPiP) {
                MainActivity.activity.playMultimediaVideo(
                        ((MultimediaContent) content).getFileURL(),
                        MainActivity.MULTIMEDIA_PIP);
            } else {
                MainActivity.activity.playMultimediaVideo(
                        ((MultimediaContent) content).getFileURL(),
                        MainActivity.MULTIMEDIA_PAP);
            }
            try {
                MainActivity.service.getContentListControl().setActiveContent(
                        content, MainActivity.SECONDARY_DISPLAY_UNIT_ID);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (content.getFilterType() == FilterType.PVR_RECORDED) {
            // PVR playback - play
            Log.d(TAG, "playContent - play PVR content: " + content.toString());
            try {
                MainActivity.service.getPvrControl().startPlayback(
                        MainActivity.SECONDARY_DISPLAY_UNIT_ID,
                        content.getIndex());
                MainActivity.service.getContentListControl().setActiveContent(
                        content, MainActivity.SECONDARY_DISPLAY_UNIT_ID);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            // Regular TV content or input - play
            Log.d(TAG,
                    "playContent -  live/input content: " + content.toString());
            AllHandler.contentHelper.goContent(content, displayID);
        }
        return true;
    }

    /** Stop content in given display unit */
    public boolean stop(int displayID) {
        Log.d(TAG, "stop dual video");
        try {
            Content primaryContent = MainActivity.service
                    .getContentListControl().getActiveContent(
                            MainActivity.PRIMARY_DISPLAY_UNIT_ID);
            Content secondaryContent = MainActivity.service
                    .getContentListControl().getActiveContent(
                            MainActivity.SECONDARY_DISPLAY_UNIT_ID);
            if ((secondaryContent != null)
                    && (displayID == MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                if (getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PIP_DISPLAY_MODE) {
                    Log.d(TAG, "stop pip - Stopping content: "
                            + secondaryContent.toString());
                    stopContent(secondaryContent, displayID, true);
                    if (secondaryContent.getFilterType() == FilterType.PVR_RECORDED) {
                        // Waiting for stop event
                        Log.d(TAG, "stop -  Wait for PVR_STOP EVENT ... ");
                        Log.d(TAG, "stop - Next state  ... NONE_DISPLAY_MODE");
                        syncPVRHandle.waitAndSynchronize(secondaryContent,
                                A4TVVideoView.NONE_DISPLAY_MODE);
                        return true;
                    }
                    getSecondaryDisplayUnit().hide();
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.NONE_DISPLAY_MODE);
                    return true;
                } else if (getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PAP_DISPLAY_MODE) {
                    Log.d(TAG, "stop pap - Stopping content: "
                            + secondaryContent.toString());
                    if ((primaryContent.getSourceType() != SourceType.ANALOG)
                            && (primaryContent.getFilterType() != FilterType.INPUTS)) {
                        MainActivity.activity.enableHbbTV();
                    }
                    stopContent(secondaryContent, displayID, false);
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() == null) {
                        getPrimaryDisplayUnit().setScaling(0, 0, 1920, 1080);
                    }
                    if (secondaryContent.getFilterType() == FilterType.PVR_RECORDED) {
                        // Waiting for stop event
                        Log.d(TAG, "stop -  Wait for PVR_STOP EVENT ... ");
                        Log.d(TAG, "stop - Next state  ... NONE_DISPLAY_MODE");
                        syncPVRHandle.waitAndSynchronize(secondaryContent,
                                A4TVVideoView.NONE_DISPLAY_MODE);
                        return true;
                    }
                    getSecondaryDisplayUnit().hide();
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.NONE_DISPLAY_MODE);
                    return true;
                }
            } else {
                // TODO if renderer there is no active content but PIP is
                // active?
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    };

    public boolean isPiP() {
        if (getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PIP_DISPLAY_MODE) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPaP() {
        if (getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PAP_DISPLAY_MODE) {
            return true;
        } else {
            return false;
        }
    }

    /** Check if content can be played in given display unit in current scenario */
    public boolean checkSupportedScenario(Content content, int displayID) {
        boolean dualScenarioSupported = true;
        try {
            IContentListControl contentListControl = MainActivity.service
                    .getContentListControl();
            Content clickedContent = content;
            Content primaryContent = contentListControl
                    .getActiveContent(MainActivity.PRIMARY_DISPLAY_UNIT_ID);
            Content secondaryContent;
            A4TVToast toast = new A4TVToast(MainActivity.activity);
            if (displayID == 0) {
                if (null != contentListControl
                        .getActiveContent(MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                    secondaryContent = contentListControl.getActiveContent(1);
                    if ((((SourceType.ANALOG == content.getSourceType()) || (FilterType.INPUTS == clickedContent
                            .getFilterType())) && ((SourceType.ANALOG == secondaryContent
                            .getSourceType()) || (FilterType.INPUTS == secondaryContent
                            .getFilterType())))) {
                        dualScenarioSupported = false;
                    }
                }
            } else { // displayID == 1 (PIP)
                /**
                 * Conetents not allowed in secondary display (second analog
                 * content, widget or application)
                 */
                if ((((SourceType.ANALOG == primaryContent.getSourceType()) || (FilterType.INPUTS == primaryContent
                        .getFilterType())) && ((SourceType.ANALOG == clickedContent
                        .getSourceType()) || (FilterType.INPUTS == clickedContent
                        .getFilterType())))
                        || (FilterType.WIDGETS == clickedContent
                                .getFilterType())) {
                    dualScenarioSupported = false;
                } else if ((OSDHandlerHelper.getHandlerState() == OSDGlobal.PVR_STATE_PLAY_TIME_SHIFT
                        || OSDHandlerHelper.getHandlerState() == OSDGlobal.PVR_STATE_PAUSE_TIME_SHIFT
                        || OSDHandlerHelper.getHandlerState() == OSDGlobal.PVR_STATE_REW_TIME_SHIFT || OSDHandlerHelper
                        .getHandlerState() == OSDGlobal.PVR_STATE_FF_TIME_SHIFT)
                        && (clickedContent.getFilterType() == FilterType.PVR_RECORDED)) {
                    /**
                     * Conetents not allowed in secondary display (PVR Playback
                     * if main in timeshift or PVR playback mode)
                     */
                    dualScenarioSupported = false;
                } else if (((activity.getPrimaryMultimediaVideoView() != null) && (activity
                        .getMultimediaMode() == MainActivity.MULTIMEDIA_MAIN))
                        && content.getFilterType() == FilterType.MULTIMEDIA) {
                    /**
                     * Cannot play two multimedia - temp
                     */
                    dualScenarioSupported = false;
                } else if ((OSDHandlerHelper.getHandlerState() == OSDGlobal.PVR_STATE_PLAY_PLAY_BACK
                        || OSDHandlerHelper.getHandlerState() == OSDGlobal.PVR_STATE_PAUSE_PLAY_BACK
                        || OSDHandlerHelper.getHandlerState() == OSDGlobal.PVR_STATE_FF_PLAY_BACK || OSDHandlerHelper
                        .getHandlerState() == OSDGlobal.PVR_STATE_REW_PLAY_BACK)
                        && content.getFilterType() == FilterType.PVR_RECORDED) {
                    /**
                     * Cannot play two PVR playbacks
                     */
                    dualScenarioSupported = false;
                }
            }
            if (dualScenarioSupported == false) {
                Log.d(TAG, "MPQ is not supporting such scenario!!");
                toast.showToast(R.string.not_supported_dual_scenario);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dualScenarioSupported;
    }

    public class pvrStopHandler extends Handler {
        private Content content = null;
        private int nexState = -1;

        public void waitAndSynchronize(Content content, int nextState) {
            this.content = content;
            this.nexState = nextState;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "message recieved!");
            switch (nexState) {
                case A4TVVideoView.PIP_DISPLAY_MODE:
                    Log.d(TAG, "Next action is to play content in PIP");
                    playPiP(content);
                    nexState = -1;
                    break;
                case A4TVVideoView.PAP_DISPLAY_MODE:
                    Log.d(TAG, "Next action is to play content in PAP");
                    playPaP(content);
                    nexState = -1;
                    break;
                case A4TVVideoView.NONE_DISPLAY_MODE:
                    Log.d(TAG,
                            "Next action is NONE, just set state and finish ... ");
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.NONE_DISPLAY_MODE);
                    nexState = -1;
                    break;
                default:
                    Log.d(TAG, "pvr stopped just set state and finish ... ");
                    getSecondaryDisplayUnit().setPlayMode(
                            A4TVVideoView.NONE_DISPLAY_MODE);
                    nexState = -1;
                    break;
            }
            super.handleMessage(msg);
        }
    }
}
