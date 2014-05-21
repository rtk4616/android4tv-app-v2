package com.iwedia.gui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RadioButton;

import com.iwedia.gui.MainActivity;

/**
 * Our radio button
 * 
 * @author Branimir Pavlovic
 */
public class A4TVRadioButton extends RadioButton {
    public A4TVRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public A4TVRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public A4TVRadioButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
