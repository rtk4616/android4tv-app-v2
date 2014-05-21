package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.osd.IOSDHandler;

public class MultimediaPVRKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "MultimediaPVRKeyListener";
    private MainActivity activity;

    public MultimediaPVRKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "KeyCode: " + keyCode);
            switch (keyCode) {
            // ////////////////////////////////
            // PIP
            // ////////////////////////////////
                case KeyEvent.KEYCODE_PROG_YELLOW:
                case KeyEvent.KEYCODE_Y: {
                    // Get active content object and stop secondary display
                    // playback
                    Log.d(TAG, "YELLOW KEY ");
                    /**
                     * Dirty hack because state is not changed when PVR playback
                     * is ongoing, there is no difference beetween beeing in
                     * multimedia PVR dialog and PVR recording playback!!!! FIX
                     * THIS!
                     */
                    if (((MainActivity) activity).getMultimediaHandler()
                            .getMultimediaDialog().isShowing() == false) {
                        if (MainActivity.activity.getDualVideoManager().isPiP()) {
                            MainActivity.activity.getDualVideoManager().stop(
                                    MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                            return true;
                        }
                    } else {
                        return false;
                    }
                    // TODO: move to dualVideoManager
                    // check if renderer
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() != null) {
                        if (MainActivity.activity.getRendererController()
                                .getmRendererState() != 0) {
                            MainActivity.activity.getRendererController()
                                    .stop();
                        } else {
                            if (MainActivity.activity.getMultimediaMode() == MainActivity.MULTIMEDIA_PIP) {
                                MainActivity.activity
                                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PIP);
                            }
                        }
                        return true;
                    }
                    return false;
                }
                // ////////////////////////////////
                // PAP
                // ////////////////////////////////
                case KeyEvent.KEYCODE_PROG_BLUE:
                case KeyEvent.KEYCODE_B: {
                    Log.d(TAG, "BLUE KEY ");
                    /**
                     * Dirty hack because state is not changed when PVR playback
                     * is ongoing, there is no difference beetween beeing in
                     * multimedia PVR dialog and PVR recording playback!!!! FIX
                     * THIS!
                     */
                    if (((MainActivity) activity).getMultimediaHandler()
                            .getMultimediaDialog().isShowing() == false) {
                        if (MainActivity.activity.getDualVideoManager().isPaP()) {
                            MainActivity.activity.getDualVideoManager().stop(
                                    MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                            return true;
                        }
                    } else {
                        return false;
                    }
                    // TODO: move to dualVideoManager
                    // check if renderer
                    Log.d(TAG, "IS pap returned false - check if renderer ... ");
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() != null) {
                        if (MainActivity.activity.getRendererController()
                                .getmRendererState() != 0) {
                            MainActivity.activity.getRendererController()
                                    .stop();
                        } else {
                            if (MainActivity.activity.getMultimediaMode() == MainActivity.MULTIMEDIA_PAP) {
                                MainActivity.activity
                                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PAP);
                            }
                        }
                        return true;
                    }
                    return false;
                }
                // ///////////////////////////////////////////////////////////////////
                // BACk
                // ///////////////////////////////////////////////////////////////////
                // Stop PlayBack
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_MEDIA_STOP: {
                    activity.getMultimediaHandler().new LoadTaskMultimediaBack(
                            "/",
                            MultimediaHandler.LOAD_BACK_FIRST_SCREEN_FROM_PVR)
                            .execute();
                    MultimediaHandler.secondScreenFolderLevel = 0;
                    MainKeyListener
                            .setAppState(MainKeyListener.MULTIMEDIA_FIRST);
                    MultimediaHandler.multimediaScreen = MultimediaHandler.MULTIMEDIA_FIRST_SCREEN;
                    return true;
                }
                // /////////////////////////////////////////////////////
                // INFO BANNER
                // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_I:
                case KeyEvent.KEYCODE_INFO: {
                    activity.getPageCurl().info();
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME UP
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F8:
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_UP, true);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME DOWN
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F7:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_DOWN, true);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME MUTE
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, true);
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        return false;
    }
}
