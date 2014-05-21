package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.osd.IOSDHandler;

public class MultimediaFirstKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "MultimediaFirstKeyHandler";
    private MainActivity mActivity = null;

    public MultimediaFirstKeyHandler(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "- keycode " + keyCode);
            switch (keyCode) {
            // //////////////////////////////////////////
            // CLOSE
            // //////////////////////////////////////////
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_CLEAR: {
                    Log.d(TAG, " - keycode BACK/CLEAR ");
                    MultimediaHandler.secondScreenFolderLevel = 0;
                    mActivity.getMultimediaHandler().closeMultimedia();
                    if (MainKeyListener.multimediaFromMainMenu) {
                        mActivity.getMainMenuHandler().showMainMenu();
                        mActivity.getMainMenuHandler().getA4TVOnSelectLister()
                                .startAnimationsManual();
                        MainKeyListener.multimediaFromMainMenu = false;
                        // } else {
                        // Log.d(TAG, " - keycode BACK/CLEAR in else ");
                        // MainKeyListener.returnToStoredAppState();
                        //
                    }
                    return true;
                }
                // /////////////////////////////////////////////////////
                // INFO BANNER
                // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_INFO: {
                    mActivity.getPageCurl().info();
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME UP
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F8:
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    IOSDHandler curlHandler = mActivity.getPageCurl();
                    curlHandler.volume(VOLUME_UP, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME DOWN
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F7:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler curlHandler = mActivity.getPageCurl();
                    curlHandler.volume(VOLUME_DOWN, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME MUTE
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = mActivity.getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, false);
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
