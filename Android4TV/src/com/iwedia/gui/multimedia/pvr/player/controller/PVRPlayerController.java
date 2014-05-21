package com.iwedia.gui.multimedia.pvr.player.controller;

import android.app.Activity;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.multimedia.MultimediaGridHelper;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.pvr.PVRHandler;

/**
 * @author Milos Milanovic
 */
public class PVRPlayerController extends ControlProvider implements OSDGlobal {
    private final String TAG = "PVRPlayerController";
    private MultimediaContent mMultimediaControllerContent = null;
    private PVRHandler mPvrHandler = null;
    private Activity mActivity = null;
    private boolean mNextPreviousContent = false;

    public PVRPlayerController(PVRHandler pvrHandler, Activity activity) {
        this.mPvrHandler = pvrHandler;
        this.mActivity = activity;
    }

    @Override
    public void stop(int displayId) {
        // Stop MultiMedia PlayBack
        A4TVMultimediaController
                .setControlRepeatPosition(MULTIMEDIA_CONTROLLER_REPEAT_OFF);
        mPvrHandler.pvrStop();
    }

    @Override
    public void setContent(MultimediaContent content) {
        mMultimediaControllerContent = content;
    }

    @Override
    public MultimediaContent getContent() {
        return mMultimediaControllerContent;
    }

    @Override
    public void rewind(int displayId) {
        if (mPvrHandler.pvrRewind()) {
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(false);
            this.setFlagFFREW(true);
        }
    }

    @Override
    public void repeatOff(int displayId) {
        Log.i(TAG, "Repeat Off");
    }

    @Override
    public void repeatOne(int displayId) {
        Log.i(TAG, "Repeat One");
        this.setElapsedTime(0);
        play(displayId);
        ((MainActivity) mActivity).getPageCurl().multimediaController(true);
    }

    @Override
    public void repeatAll(int displayId) {
        Log.i(TAG, "Repeat All");
        Content nextContent = ((MainActivity) mActivity).getMultimediaHandler()
                .getMultimediaShowHandler().findNextPvr();
        if (nextContent != null) {
            setContent((MultimediaContent) nextContent);
            play(displayId);
            this.setElapsedTime(0);
            this.setDuration(0);
            String strName;
            try {
                strName = nextContent.getName();
            } catch (Exception e) {
                Log.i(TAG, "Method: next strName", e);
                strName = "";
            }
            ControlProvider.setFileName(strName);
            ((MainActivity) mActivity).getPageCurl().multimediaController(true);
        } else {
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.dlna_playback_no_next_file);
        }
    }

    @Override
    public void previous(int displayId) {
        Content previousContent = ((MainActivity) mActivity)
                .getMultimediaHandler().getMultimediaShowHandler()
                .findPreviousPvr();
        if (previousContent != null) {
            setContent((MultimediaContent) previousContent);
            mNextPreviousContent = true;
            PVRHandler.stopPVRPlayBack();
            this.setElapsedTime(0);
            this.setDuration(0);
            String strName;
            try {
                strName = previousContent.getName();
            } catch (Exception e) {
                Log.i(TAG, "Method: previous strName", e);
                strName = "";
            }
            ControlProvider.setFileName(strName);
        } else {
            stop(displayId);
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.dlna_playback_no_previous_file);
        }
    }

    @Override
    public void play(int displayId) {
        try {
            MainActivity.service.getContentListControl().goContent(
                    mMultimediaControllerContent, displayId);
            Log.i(TAG, "initControlProviderPVR pvrPlay");
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(true);
            // Hide antenna overlay before every playback
            MultimediaGridHelper.hideAntennaOverlay();
        } catch (Exception e) {
            Log.e(TAG, "GoContent", e);
            OSDHandlerHelper.setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
        }
    }

    @Override
    public void pause(int displayId) {
        if (mPvrHandler.pvrPause()) {
            Log.i(TAG, "initControlProviderPVR pvrPause");
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(false);
        }
    }

    @Override
    public void next(int displayId) {
        Content nextContent = ((MainActivity) mActivity).getMultimediaHandler()
                .getMultimediaShowHandler().findNextPvr();
        if (nextContent != null) {
            setContent((MultimediaContent) nextContent);
            mNextPreviousContent = true;
            PVRHandler.stopPVRPlayBack();
            this.setElapsedTime(0);
            this.setDuration(0);
            String strName;
            try {
                strName = nextContent.getName();
            } catch (Exception e) {
                Log.i(TAG, "Method: next strName", e);
                strName = "";
            }
            ControlProvider.setFileName(strName);
        } else {
            stop(displayId);
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.dlna_playback_no_next_file);
        }
    }

    @Override
    public void fastForward(int displayId) {
        if (mPvrHandler.pvrFastForward()) {
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(false);
            this.setFlagFFREW(true);
        }
    }

    @Override
    public void resume(int displayId) {
        if (mPvrHandler.pvrPlay()) {
            Log.i(TAG, "initControlProviderPVR resume");
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(true);
        }
    }

    public void prepareStop(int displayId) {
        if (mNextPreviousContent) {
            mNextPreviousContent = false;
            play(displayId);
        } else {
            if (checkRepeat(displayId)) {
                setFileDescription((mActivity).getApplicationContext()
                        .getString(R.string.stop));
                // Set isPlayingFlag
                ControlProvider.setFlagPlay(false);
                this.setFlagFFREW(false);
                this.setElapsedTime(0);
                A4TVMultimediaController
                        .setControlPosition(MULTIMEDIA_CONTROLLER_STOP);
                Log.i(TAG, "initControlProviderPVR STOP");
                // ////////////////////////////////////////////////////
                OSDHandlerHelper.setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
                // Start live stream upon stop
                ((MainActivity) mActivity).getMediaController()
                        .startLiveStream(false);
                // Show multimedia back to PVR screen
                MultimediaHandler.returnMultimediaToPreviousState();
                // Show antenna overlay if needed after playback
                MultimediaGridHelper.showAntennaOverlay();
                // Show Curl
                ((MainActivity) mActivity).getPageCurl().multimediaController(
                        true);
            }
        }
    }

    public boolean checkRepeat(int displayId) {
        switch (A4TVMultimediaController.getControlRepeatPosition()) {
            case MULTIMEDIA_CONTROLLER_REPEAT_OFF: {
                repeatOff(displayId);
                return true;
            }
            case MULTIMEDIA_CONTROLLER_REPEAT_ONE: {
                repeatOne(displayId);
                return false;
            }
            case MULTIMEDIA_CONTROLLER_REPEAT_ALL: {
                repeatAll(displayId);
                return false;
            }
            default: {
                return true;
            }
        }
    }
}
