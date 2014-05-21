package com.iwedia.gui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.iwedia.gui.R;

public class A4TVProgressInfoBanner extends SeekBar {
    public A4TVProgressInfoBanner(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, android.R.attr.progressBarStyleHorizontal);
        init(context);
    }

    public A4TVProgressInfoBanner(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.progressBarStyleHorizontal);
        init(context);
    }

    private void init(Context context) {
        setProgressDrawable(context.getResources().getDrawable(
                R.drawable.a4tvinfoprogressbar_drawable_ics));
        setFocusable(false);
        setThumb(null);
    }
}
