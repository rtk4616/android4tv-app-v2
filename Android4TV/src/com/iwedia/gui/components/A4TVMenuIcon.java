package com.iwedia.gui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Our ImageView that has theme change implemented
 * 
 * @author Branimir Pavlovic
 */
public class A4TVMenuIcon extends ImageView {
    /**
     * Default constructor, it would create default system ImageView it should
     * not be used since it is not possible to reach and set any theme
     * attributes from here
     * 
     * @param context
     */
    public A4TVMenuIcon(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public A4TVMenuIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public A4TVMenuIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
