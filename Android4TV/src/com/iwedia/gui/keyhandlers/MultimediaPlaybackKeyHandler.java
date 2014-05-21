package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.osd.curleffect.CurlHandler;

public class MultimediaPlaybackKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "MultimediaPlaybackKeyListener";
    private MainActivity activity;

    public MultimediaPlaybackKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        // TODO: Applies on main display only
        final int displayId = 0;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "- keycode " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    IOSDHandler mCurlHandler = activity.getPageCurl();
                    mCurlHandler.multimediaControllerMoveLeft();
                    return true;
                }
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    IOSDHandler mCurlHandler = activity.getPageCurl();
                    mCurlHandler.multimediaControllerMoveRight();
                    return true;
                }
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER: {
                    Log.d(TAG, "KeyCode Enter");
                    IOSDHandler mCurlHandler = activity.getPageCurl();
                    mCurlHandler.multimediaControllerClick(false);
                    return true;
                }
                // ////////////////////////////////
                // PIP
                // ////////////////////////////////
                case KeyEvent.KEYCODE_PROG_YELLOW:
                case KeyEvent.KEYCODE_Y: {
                    // Get active content object and stop secondary display
                    // playback
                    Log.d(LOG_TAG, "YELLOW KEY ");
                    if (MainActivity.activity.getDualVideoManager().isPiP()) {
                        MainActivity.activity.getDualVideoManager().stop(
                                MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                        return true;
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
                                        .getDualVideoManager()
                                        .stop(MainActivity.SECONDARY_DISPLAY_UNIT_ID);
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
                    Log.d(LOG_TAG, "BLUE KEY ");
                    if (MainActivity.activity.getDualVideoManager().isPaP()) {
                        if (MainActivity.activity.getDualVideoManager().stop(
                                MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                            return true;
                        }
                    }
                    // TODO: move to dualVideoManager
                    // check if renderer
                    Log.d(LOG_TAG,
                            "IS pap returned false - check if renderer ... ");
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
                case KeyEvent.KEYCODE_TAB:
                case KeyEvent.KEYCODE_MENU: {
                    if (MainKeyListener.enableKeyCodeMenu) {
                        MainKeyListener.stopreAppState();
                        // If main menu isn't created create it
                        if (activity.getMainMenuHandler() == null) {
                            activity.initMainMenu();
                        }
                        if (activity.getMainMenuHandler().getMainMenuDialog()
                                .isShowing()) {
                            activity.getMainMenuHandler()
                                    .getA4TVOnSelectLister()
                                    .clearAnimationsManual();
                            activity.getMainMenuHandler().closeMainMenu(true);
                        } else {
                            // Show main menu
                            activity.getMainMenuHandler().showMainMenu();
                        }
                    }
                    return true;
                }
                // /////////////////////////////////////////////////////
                // INFO BANNER
                // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_INFO: {
                    activity.getPageCurl().info();
                    return true;
                }
                // Stop PlayBack
                case KeyEvent.KEYCODE_MEDIA_STOP: {
                    IOSDHandler mCurlHandler = activity.getPageCurl();
                    A4TVMultimediaController
                            .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_STOP);
                    mCurlHandler.multimediaControllerClick(true);
                    return true;
                }
                // Play & Pause & Resume
                case 126:
                case 127:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                    IOSDHandler mCurlHandler = activity.getPageCurl();
                    A4TVMultimediaController
                            .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_PLAY);
                    mCurlHandler.multimediaControllerClick(true);
                    return true;
                }
                // FF & Next
                case 87:
                case 125:
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                    IOSDHandler mCurlHandler = activity.getPageCurl();
                    A4TVMultimediaController
                            .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_FF_NEXT);
                    mCurlHandler.multimediaControllerClick(true);
                    return true;
                }
                // REW & Previous
                case 88:
                case KeyEvent.KEYCODE_MEDIA_REWIND: {
                    IOSDHandler mCurlHandler = activity.getPageCurl();
                    A4TVMultimediaController
                            .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_REW_PREVIOUS);
                    mCurlHandler.multimediaControllerClick(true);
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
                // ///////////////////////////////////////////////////////////////////
                // BACk
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_BACK: {
                    // Stop Current PlayBack
                    A4TVMultimediaController.getControlProvider().stop(
                            displayId);
                    if (OSDHandlerHelper.getHandlerState() == CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER) {
                        activity.getMediaController().startLiveStream(false);
                        // Close Info Banner if is On
                        if (MainActivity.activity.getPageCurl() instanceof CurlHandler
                                && (MainActivity.activity.getPageCurl()
                                        .getCurrentState() == STATE_PVR || MainActivity.activity
                                        .getPageCurl().getCurrentState() == STATE_MULTIMEDIA_CONTROLLER)) {
                            activity.getPageCurl().info();
                        }
                        // Clear PVR icons
                        OSDHandlerHelper
                                .setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
                        if (MainActivity.service != null) {
                            MultimediaHandler.returnMultimediaToPreviousState();
                        } else {
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(com.iwedia.gui.R.string.proxy_service_is_null);
                        }
                    }
                    return true;
                }
                case KeyEvent.KEYCODE_CHANNEL_UP:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_F4: {
                    activity.getMultimediaHandler().scrollLyrics(1);
                    return true;
                }
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_F3: {
                    activity.getMultimediaHandler().scrollLyrics(-1);
                    return true;
                }
                default:
                    return false;
            }
        }
        return false;
    }
}
