package com.iwedia.gui.epg;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.iwedia.gui.R;

public class EPGScrollView extends ScrollView implements OnKeyListener {
    private final String TAG = "EPGScrollView";
    private int visibleRectHeight;
    private EPGHandlingClass epgHandlingClass;
    public static boolean isScrolledToBottom = false, isScrolledToTop = true;
    private boolean canIGo = true;

    public EPGScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnKeyListener(this);
    }

    public EPGScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnKeyListener(this);
    }

    public EPGScrollView(Context context) {
        super(context);
        setOnKeyListener(this);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // Grab the last child placed in the ScrollView, we need it to
        // determinate the bottom position.
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayoutEPGScrollTime);
        if (layout != null && layout.getChildCount() > 0) {
            View view = (View) layout.getChildAt(layout.getChildCount() - 1);
            // Calculate the scrolldiff
            int diff = (view.getBottom() - (getHeight() + getScrollY()));
            // if diff is zero or lower, then the bottom has been reached
            if (diff <= 0) {
                // notify that we have reached the bottom
                Log.d(TAG, "MyScrollView: Bottom has been reached");
                isScrolledToBottom = true;
            } else {
                isScrolledToBottom = false;
            }
            if (oldt > 0 && t == 0) {
                Log.d(TAG, "MyScrollView: Top has been reached");
                isScrolledToTop = true;
            } else {
                isScrolledToTop = false;
            }
        }
        // if scroll view has focus decide if there is child to give focus
        if (hasFocus()) {
            if (epgHandlingClass.getMainLayout().getChildCount() > 2) {
                // Log.d(TAG, "SCROLL VIEW CHILD CHECK");
                View viewToGiveFocus = null;
                boolean someViewHasFocus = false;
                for (int i = 0; i < ((epgHandlingClass.getMainLayout()
                        .getChildCount() > 2) ? ((LinearLayout) epgHandlingClass
                        .getMainLayout().getChildAt(2)).getChildCount() : 0); i++) {
                    View viewEPG = ((LinearLayout) epgHandlingClass
                            .getMainLayout().getChildAt(2)).getChildAt(i);
                    if (viewEPG.getTop() < (getHeight() + getScrollY())
                            && viewEPG.getTop() > getScrollY()) {
                        viewToGiveFocus = viewEPG;
                        // Log.d(TAG, "SCROLL VIEW VIEW FINDED");
                    }
                    if (viewEPG.hasFocus()) {
                        // Log.d(TAG, "SCROLL VIEW VIEW FOCUSED FINDED");
                        someViewHasFocus = true;
                        break;
                    }
                }
                if (!someViewHasFocus && viewToGiveFocus != null) {
                    // Log.d(TAG, "SCROLL VIEW VIEW REQUEST FOCUS");
                    viewToGiveFocus.requestFocus();
                }
            }
        }
        // clear views from overlay view
        if (epgHandlingClass.getFrameLayoutEPGForSmallEvents().getChildCount() > 0) {
            epgHandlingClass.getFrameLayoutEPGForSmallEvents().removeAllViews();
        }
        super.onScrollChanged(l, t, oldl, oldt);
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
                    Log.d(TAG,
                            "ON KEY LISTENER ********************************DOWN****************************** isScrolledToBottom: "
                                    + isScrolledToBottom);
                    if (isScrolledToBottom) {
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
                    Log.d(TAG,
                            "ON KEY LISTENER ********************************UP****************************** isScrolledToTop: "
                                    + isScrolledToTop);
                    if (isScrolledToTop) {
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

    // /////////////////////////////////////////////////////
    // GETTERS AND SETTERS
    // /////////////////////////////////////////////////////
    public int getVisibleRectHeight() {
        return visibleRectHeight;
    }

    public void setVisibleRectHeight(int visibleRectHeight) {
        this.visibleRectHeight = visibleRectHeight;
    }

    public void setEpgHandlingClass(EPGHandlingClass epgHandlingClass) {
        this.epgHandlingClass = epgHandlingClass;
    }
}
