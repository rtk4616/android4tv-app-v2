package com.iwedia.gui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.iwedia.gui.R;
import com.iwedia.gui.mainmenu.MainMenuContent;

/**
 * TextView with theme change
 * 
 * @author Branimir Pavlovic
 */
public class A4TVTextView extends TextView {
    private Context ctx;
    private int styleTextView;

    /**
     * Constructor for A4TVTextView
     * 
     * @param context
     * @param attrs
     */
    public A4TVTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        setTag(MainMenuContent.TAGA4TVTextView);
        TypedArray atts = context.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVtexViewProps });
        this.styleTextView = atts.getResourceId(0, 0);
        setTextAppearance(ctx, styleTextView);
        atts.recycle();
        // setTypeface(Typeface.DEFAULT, this.styleTextView);
    }

    public A4TVTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ctx = context;
        setTag(MainMenuContent.TAGA4TVTextView);
        TypedArray atts = context.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVtexViewProps });
        this.styleTextView = atts.getResourceId(0, 0);
        setTextAppearance(ctx, styleTextView);
        atts.recycle();
    }

    public A4TVTextView(Context context) {
        super(context);
        this.ctx = context;
        setTag(MainMenuContent.TAGA4TVTextView);
        TypedArray atts = context.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVtexViewProps });
        this.styleTextView = atts.getResourceId(0, 0);
        setTextAppearance(ctx, styleTextView);
        atts.recycle();
    }
}
