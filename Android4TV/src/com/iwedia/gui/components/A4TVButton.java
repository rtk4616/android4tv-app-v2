package com.iwedia.gui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.Button;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.mainmenu.MainMenuContent;

/**
 * Our button that has theme change implemented
 * 
 * @author Branimir Pavlovic
 */
public class A4TVButton extends Button {
    public A4TVButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTag(MainMenuContent.TAGA4TVButton);
    }

    /**
     * This constructor should be used for A4TVButton
     * 
     * @param context
     */
    public A4TVButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTag(MainMenuContent.TAGA4TVButton);
    }

    /**
     * Default constructor, it would create default system button it should not
     * be used since it is not possible to reach and set any theme attributes
     * from here
     * 
     * @param context
     */
    public A4TVButton(Context context) {
        super(context);
        setTag(MainMenuContent.TAGA4TVButton);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
