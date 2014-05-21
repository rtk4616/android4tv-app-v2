package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import com.iwedia.gui.osd.OSDGlobal;

public abstract class AppStateKeyHandler implements OnKeyListener,
        android.content.DialogInterface.OnKeyListener, OSDGlobal {
    public static String TAG = "AppStateKeyHandler";

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return onKeyPressed(null, dialog, keyCode, event, false);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return onKeyPressed(v, null, keyCode, event, false);
    }

    /**
     * Key handling function
     * 
     * @param v
     * @param dialog
     * @param keyCode
     * @param event
     * @return
     */
    public abstract boolean onKeyPressed(View v, DialogInterface dialog,
            int keyCode, KeyEvent event, boolean isFromMheg);
}
