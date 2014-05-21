package com.iwedia.gui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * Vertical scrool view
 * 
 * @author Branimir Pavlovic
 */
public class A4TVInfoDescriptionScrollView extends ScrollView {
    public Scrolled scrolled;

    public A4TVInfoDescriptionScrollView(Context context) {
        super(context);
    }

    public A4TVInfoDescriptionScrollView(Context context, AttributeSet as) {
        super(context, as);
    }

    public A4TVInfoDescriptionScrollView(Context context, AttributeSet as, int i) {
        super(context, as, i);
    }

    public void setScrooled(Scrolled scrolled) {
        this.scrolled = scrolled;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        View view = (View) getChildAt(getChildCount() - 1);
        // Calculate the scrolldiff
        int diff = (view.getBottom() - (getHeight() + getScrollY()));
        // if diff is zero, then the bottom has been reached
        if (diff <= 0) {
            if (scrolled != null) {
                scrolled.detectEnd();
            }
            // notify that we have reached the bottom
            // Log.d(ScrollTest.LOG_TAG, "MyScrollView: Bottom has been reached"
            // );
        }
        if (scrolled != null) {
            scrolled.scrolled();
        }
    }

    public interface Scrolled {
        public void scrolled();

        public void detectEnd();
    }
}
