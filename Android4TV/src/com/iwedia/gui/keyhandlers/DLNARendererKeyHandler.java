package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.osd.IOSDHandler;

public class DLNARendererKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "DLNARendererKeyListener";
    private MainActivity activity;

    public DLNARendererKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "KeyCode: " + keyCode);
            switch (keyCode) {
            // //////////////////////////////////////////////////////////////
            // STOP
            // //////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MEDIA_STOP: {
                    // Stop Renderer
                    activity.getRendererController().stop();
                    return true;
                }
                // /////////////////////////////////////////////////////
                // INFO BANNER
                // //////////////////////////////////////////////////////
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
            }
        }
        return false;
    }
}
