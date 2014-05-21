package com.iwedia.gui.epg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class EpgSmallScrollView extends HorizontalScrollView {
    private boolean enabledScroll = false;

    public EpgSmallScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EpgSmallScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EpgSmallScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (enabledScroll) {
            super.onScrollChanged(l, t, oldl, oldt);
        } else {
            return;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (enabledScroll) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (enabledScroll) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return true;
        }
    }

    // //////////////////////////////////////
    // Getters and setters
    // //////////////////////////////////////
    public boolean isEnabledScroll() {
        return enabledScroll;
    }

    public void setEnabledScroll(boolean enabledScroll) {
        this.enabledScroll = enabledScroll;
    }
}
