package com.iwedia.gui.epg;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class EPGKeyListener implements OnKeyListener {
    private final String TAG = "EPGKeyListener";
    private EPGHandlingClass epgHandlingClass;
    private boolean canIGo = true;
    private boolean first, last;

    public EPGKeyListener(EPGHandlingClass epgHandlingClass, boolean first,
            boolean last) {
        this.epgHandlingClass = epgHandlingClass;
        this.first = first;
        this.last = last;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    canIGo = true;
                    return true;
                }
            }
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
            // go down
                case KeyEvent.KEYCODE_DPAD_DOWN: {
                    if (last && EPGScrollView.isScrolledToBottom) {
                        if (epgHandlingClass.getDayInWeekToLoadData() < 7) {
                            epgHandlingClass.createAskDialogAndShow(true);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
                // go up
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (first && EPGScrollView.isScrolledToTop) {
                        if (epgHandlingClass.getDayInWeekToLoadData() > 1) {
                            epgHandlingClass.createAskDialogAndShow(false);
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
                default:
                    break;
            }
        }
        return false;
    }
}
