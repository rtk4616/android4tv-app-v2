package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.osd.IOSDHandler;

public class InfoKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "InfoKeyHandler";
    private MainActivity activity;

    public InfoKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "- keycode " + keyCode);
            switch (keyCode) {
            // /////////////////////////////////////////////////////
            // INFO BANNER
            // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_BACK:
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
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.updateChannelInfo(-1);
                }
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.updateChannelInfo(1);
                }
            }
        }
        return false;
    }
}
