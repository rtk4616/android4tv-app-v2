package com.iwedia.gui.listeners;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.multimedia.MultimediaGlobal;
import com.iwedia.gui.multimedia.controller.MediaController;
import com.iwedia.gui.multimedia.dlna.renderer.controller.RendererController;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;

public class A4TVVideoViewOnCompletionListener implements OnCompletionListener,
        MultimediaGlobal, OSDGlobal {
    private final String TAG = "A4TVVideoViewOnCompletionListener";
    private static boolean mVideoViewError = false;
    private Activity mActivity = null;
    private MediaController mMediaController = null;
    private RendererController mRendererController = null;
    private int mDisplayId = 0;

    public A4TVVideoViewOnCompletionListener(Activity activity,
            MediaController mediaController,
            RendererController rendererController) {
        this.mActivity = activity;
        this.mMediaController = mediaController;
        this.mRendererController = rendererController;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "VideoView OnCompletition");
        if (!isVideoViewError()) {
            Log.i(TAG, "VideoView OnCompletition Accepted");
            setVideoViewError(false);
            if (mRendererController.getmRendererState() == RENDERER_STATE_STOP
                    && OSDHandlerHelper.getHandlerState() == CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER) {
                // ///////////////////////////////////////
                // MultimediaController Repeat
                // ///////////////////////////////////////
                switch (A4TVMultimediaController.getControlRepeatPosition()) {
                    case A4TVMultimediaController.MULTIMEDIA_CONTROLLER_REPEAT_OFF: {
                        A4TVMultimediaController.getControlProvider()
                                .repeatOff(mDisplayId);
                        break;
                    }
                    case A4TVMultimediaController.MULTIMEDIA_CONTROLLER_REPEAT_ONE: {
                        A4TVMultimediaController.getControlProvider()
                                .repeatOne(mDisplayId);
                        break;
                    }
                    case A4TVMultimediaController.MULTIMEDIA_CONTROLLER_REPEAT_ALL: {
                        A4TVMultimediaController.getControlProvider()
                                .repeatAll(mDisplayId);
                        break;
                    }
                    default:
                        break;
                }
            } else if (mRendererController.getmRendererState() == RENDERER_STATE_STOP
                    && OSDHandlerHelper.getHandlerState() == CURL_HANDLER_STATE_DO_NOTHING) {
                // DRM On Completion case - temp
                mMediaController.stop(0);
                mMediaController.startLiveStream(true);
            } else {
                mRendererController.onCompletion();
            }
        }
        // ////////////////////////////////////////
    }

    public static boolean isVideoViewError() {
        return mVideoViewError;
    }

    public static void setVideoViewError(boolean mVideoViewError) {
        A4TVVideoViewOnCompletionListener.mVideoViewError = mVideoViewError;
    }
}
