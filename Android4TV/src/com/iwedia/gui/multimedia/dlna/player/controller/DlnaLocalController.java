package com.iwedia.gui.multimedia.dlna.player.controller;

import android.app.Activity;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.multimedia.MultimediaGlobal;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.multimedia.controller.MediaController;
import com.iwedia.gui.osd.OSDGlobal;

/**
 * @author Milos Milanovic
 */
public class DlnaLocalController extends ControlProvider implements OSDGlobal,
        MultimediaGlobal {
    private final String TAG = "DlnaLocalController";
    private Activity mActivity = null;
    private MediaController mMediaController = null;
    private MultimediaContent mMultimediaControllerContent = null;

    public DlnaLocalController(Activity activity,
            MediaController mediaController) {
        this.mActivity = activity;
        this.mMediaController = mediaController;
    }

    @Override
    public void stop(int displayId) {
        // ///////////////////////////////////////////////////
        // Stop MultiMedia PlayBack
        // ///////////////////////////////////////////////////
        this.setElapsedTime(0);
        // //Com_3.0
        mMediaController.seekTo(0);
        mMediaController.stop(0);
        // Com_4.0
        // ((MainActivity) mActivity).getExternalVideoView()
        // .stopPlayback();
        // Set isPlayingFlag
        ControlProvider.setFlagPlay(mMediaController.isPlaying());
        // ////////////////////////////////////////////////////
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
        if (mMediaController.seek(-2000)) {
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(mMediaController.isPlaying());
        }
    }

    @Override
    public void repeatOne(int displayId) {
        // Start from beginning
        mMediaController.seekTo(0);
        resume(displayId);
        ((MainActivity) mActivity).getPageCurl().multimediaController(false);
    }

    @Override
    public void repeatOff(int displayId) {
        stop(displayId);
        mMediaController.startLiveStream(false);
        MultimediaHandler.returnMultimediaToPreviousState();
    }

    @Override
    public void repeatAll(int displayId) {
        next(displayId);
        ((MainActivity) mActivity).getPageCurl().multimediaController(false);
    }

    @Override
    public void previous(int displayId) {
        // ////////////////////////////////////
        // Check current content extension
        // ////////////////////////////////////
        String lExtension;
        if (mMultimediaControllerContent.getMime() != null) {
            lExtension = mMultimediaControllerContent.getMime();
        } else {
            lExtension = mMultimediaControllerContent.getExtension();
        }
        Content previousContent = null;
        if (EXTENSIONS_VIDEO.contains(lExtension.toLowerCase())) {
            // Find previous content
            previousContent = ((MainActivity) mActivity).getMultimediaHandler()
                    .getMultimediaShowHandler().findPreviousVideo();
        } else if (EXTENSIONS_AUDIO.contains(lExtension.toLowerCase())) {
            // Find previous content
            previousContent = ((MainActivity) mActivity).getMultimediaHandler()
                    .getMultimediaShowHandler().findPreviousAudio();
        }
        if (previousContent != null) {
            setContent((MultimediaContent) previousContent);
            // // Com_3.0
            if (mMediaController.play(
                    mMultimediaControllerContent.getFileURL(), 0)) {
                // Set Duration
                this.setDuration(mMediaController.getPlayBackDuration());
                // Set isPlayingFlag
                ControlProvider.setFlagPlay(true);
                String strName;
                try {
                    strName = previousContent.getName();
                } catch (Exception e) {
                    Log.i(TAG, "Method: previous strName", e);
                    strName = "";
                }
                ControlProvider.setFileName(strName);
                ControlProvider.setFileDescription("");
            }
        } else {
            stop(displayId);
            mMediaController.startLiveStream(false);
            MultimediaHandler.returnMultimediaToPreviousState();
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.dlna_playback_no_previous_file);
        }
    }

    @Override
    public void play(int displayId) {
        // // Com_3.0
        if (mMediaController.play(mMultimediaControllerContent.getFileURL(), 0)) {
            // Set Duration
            this.setDuration(mMediaController.getPlayBackDuration());
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(true);
        }
    }

    @Override
    public void pause(int displayId) {
        if (mMediaController.pause()) {
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(false);
        }
    }

    @Override
    public void next(int displayId) {
        // ////////////////////////////////////
        // Check current content extension
        // ////////////////////////////////////
        String lExtension;
        if (mMultimediaControllerContent.getMime() != null) {
            lExtension = mMultimediaControllerContent.getMime();
        } else {
            lExtension = mMultimediaControllerContent.getExtension();
        }
        Content nextContent = null;
        if (EXTENSIONS_VIDEO.contains(lExtension.toLowerCase())) {
            // Find next content
            nextContent = ((MainActivity) mActivity).getMultimediaHandler()
                    .getMultimediaShowHandler().findNextVideo();
        } else if (EXTENSIONS_AUDIO.contains(lExtension.toLowerCase())) {
            // Find next content
            nextContent = ((MainActivity) mActivity).getMultimediaHandler()
                    .getMultimediaShowHandler().findNextAudio();
        }
        if (nextContent != null) {
            setContent((MultimediaContent) nextContent);
            // // Com_3.0
            if (mMediaController.play(
                    mMultimediaControllerContent.getFileURL(), 0)) {
                // Set Duration
                this.setDuration(mMediaController.getPlayBackDuration());
                // Set isPlayingFlag
                ControlProvider.setFlagPlay(true);
                String strName;
                try {
                    strName = nextContent.getName();
                } catch (Exception e) {
                    Log.i(TAG, "Method: next strName", e);
                    strName = "";
                }
                ControlProvider.setFileName(strName);
                ControlProvider.setFileDescription("");
            }
        } else {
            stop(displayId);
            mMediaController.startLiveStream(false);
            MultimediaHandler.returnMultimediaToPreviousState();
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.dlna_playback_no_next_file);
        }
    }

    @Override
    public void fastForward(int displayId) {
        if (mMediaController.seek(2000)) {
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(mMediaController.isPlaying());
        }
    }

    @Override
    public void resume(int displayId) {
        if (mMediaController.resume()) {
            // Set isPlayingFlag
            ControlProvider.setFlagPlay(true);
        }
    }

    public MediaController getMediaController() {
        return mMediaController;
    }
}
