package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.osd.IOSDHandler;

public class MultimediaSecondKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "MultimediaSecondKeyHandler";
    private MainActivity activity;

    public MultimediaSecondKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "- keycode " + keyCode);
            switch (keyCode) {
            // /////////////////////////////////////
            // BACK
            // /////////////////////////////////////
                case KeyEvent.KEYCODE_BACK: {
                    // //////////////////
                    // Go level UP
                    // //////////////////
                    if (MultimediaHandler.secondScreenFolderLevel > 1) {
                        MultimediaHandler.secondScreenFolderLevel--;
                        activity.getMultimediaHandler().new LoadTaskMultimediaBack(
                                "..", MultimediaHandler.LOAD_BACK_LEVEL)
                                .execute();
                    }
                    // //////////////////////////////////////////
                    // Close second screen and open first screen
                    // ///////////////////////////////////////////
                    else {
                        activity.getMultimediaHandler().new LoadTaskMultimediaBack(
                                "/", MultimediaHandler.LOAD_BACK_FIRST_SCREEN)
                                .execute();
                        MultimediaHandler.secondScreenFolderLevel = 0;
                        MainKeyListener
                                .setAppState(MainKeyListener.MULTIMEDIA_FIRST);
                        MultimediaHandler.multimediaScreen = MultimediaHandler.MULTIMEDIA_FIRST_SCREEN;
                    }
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
                    curlHandler.volume(VOLUME_UP, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME DOWN
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F7:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_DOWN, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME MUTE
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, false);
                    return true;
                }
                // /////////////////////////////////////////
                // CLOSE
                // /////////////////////////////////////////
                case KeyEvent.KEYCODE_CLEAR: {
                    MultimediaHandler.secondScreenFolderLevel = 0;
                    activity.getMultimediaHandler().closeMultimedia();
                    activity.getMainMenuHandler().showMainMenu();
                    activity.getMainMenuHandler().getA4TVOnSelectLister()
                            .startAnimationsManual();
                    return true;
                }
            }
        }
        return false;
    }
}
