package com.iwedia.gui.epg;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class EPGDownGallery extends Gallery {
    public static final String TAG = "EPGDownGallery";
    private EPGHandlingClass epgHandler;

    public EPGDownGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EPGDownGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EPGDownGallery(Context context) {
        super(context);
        init();
    }

    private void init() {
        setFocusable(false);
        setEnabled(false);
        setClickable(false);
        setFocusableInTouchMode(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (hasFocus()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_SEARCH: {
                    epgHandler.hideEPGDialog();
                    return true;
                }
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    epgHandler.goLeft(false);
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    epgHandler.goRigth(false);
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN: {
                    if (epgHandler.getMainLayout().getChildCount() > 2) {
                        epgHandler.getMainLayout().getChildAt(2).requestFocus();
                    }
                    break;
                }
                default:
                    break;
            }
            if (epgHandler != null) {
                epgHandler.giveFocusToDesiredChild();
            }
            Log.d(TAG, "GALLERY ON KEY DOWN HAS FOCUS");
            return true;
        } else {
            Log.d(TAG, "GALLERY ON KEY DOWN DONT HAS FOCUS");
            return super.onKeyDown(keyCode, event);
        }
    }

    public void setEpgHandler(EPGHandlingClass epgHandler) {
        this.epgHandler = epgHandler;
    }
}
