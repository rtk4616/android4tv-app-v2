package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;

public class EPGKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "EPGKeyHandler";
    private MainActivity activity;

    public EPGKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "KeyCode: " + keyCode);
            switch (keyCode) {
            // /////////////////////////////////////////////////////
            // INFO BANNER
            // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_I:
                case KeyEvent.KEYCODE_INFO: {
                    activity.getPageCurl().info();
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
