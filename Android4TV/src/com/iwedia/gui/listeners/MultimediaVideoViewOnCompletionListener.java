package com.iwedia.gui.listeners;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;

public class MultimediaVideoViewOnCompletionListener implements
        OnCompletionListener, OSDGlobal {
    private static final String TAG = "DLNAVideoViewOnCompletionListener";
    private int mode = 0;

    public MultimediaVideoViewOnCompletionListener(int mode) {
        this.mode = mode;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "VideoView OnCompletition");
        if (mode != 0) {
            MainActivity.activity.getDualVideoManager().stop(
                    MainActivity.SECONDARY_DISPLAY_UNIT_ID);
        } else {
            if (OSDHandlerHelper.getHandlerState() == CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER) {
                // ///////////////////////////////////////
                // MultimediaController Repeat
                // ///////////////////////////////////////
                switch (A4TVMultimediaController.getControlRepeatPosition()) {
                    case A4TVMultimediaController.MULTIMEDIA_CONTROLLER_REPEAT_OFF: {
                        // A4TVMultimediaController.getControlProvider().repeatOff(0);
                        MainActivity.activity.stopMultimediaVideo(mode);
                        break;
                    }
                    case A4TVMultimediaController.MULTIMEDIA_CONTROLLER_REPEAT_ONE: {
                        A4TVMultimediaController.getControlProvider()
                                .repeatOne(0);
                        break;
                    }
                    case A4TVMultimediaController.MULTIMEDIA_CONTROLLER_REPEAT_ALL: {
                        A4TVMultimediaController.getControlProvider()
                                .repeatAll(0);
                        break;
                    }
                    default:
                        break;
                }
            } else {
                MainActivity.activity.stopMultimediaVideo(mode);
            }
        }
    }
}
