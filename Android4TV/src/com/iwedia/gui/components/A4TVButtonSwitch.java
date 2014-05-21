package com.iwedia.gui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Button;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.mainmenu.MainMenuContent;

/**
 * Our button that has theme change implemented
 * 
 * @author Branimir Pavlovic
 */
public class A4TVButtonSwitch extends Button {
    private Context ctx;

    public A4TVButtonSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ctx = context;
        setTag(MainMenuContent.TAGA4TVButtonSwitch);
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVSwitchButton });
        int backgroundID = atts.getResourceId(0, 0);
        if (backgroundID != 0) {
            setBackgroundResource(backgroundID);
        }
        atts.recycle();
        setPadding(
                (int) ctx.getResources().getDimension(R.dimen.padding_small),
                0,
                (int) ctx.getResources().getDimension(R.dimen.padding_small), 0);
    }

    /**
     * This constructor should be used for A4TVButton
     * 
     * @param context
     */
    public A4TVButtonSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        setTag(MainMenuContent.TAGA4TVButtonSwitch);
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVSwitchButton });
        int backgroundID = atts.getResourceId(0, 0);
        if (backgroundID != 0) {
            setBackgroundResource(backgroundID);
        }
        atts.recycle();
        setPadding(
                (int) ctx.getResources().getDimension(R.dimen.padding_small),
                0,
                (int) ctx.getResources().getDimension(R.dimen.padding_small), 0);
    }

    /**
     * Default constructor, it would create default system button it should not
     * be used since it is not possible to reach and set any theme attributes
     * from here
     * 
     * @param context
     */
    public A4TVButtonSwitch(Context context) {
        super(context);
        this.ctx = context;
        setTag(MainMenuContent.TAGA4TVButtonSwitch);
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVSwitchButton });
        int backgroundID = atts.getResourceId(0, 0);
        if (backgroundID != 0) {
            setBackgroundResource(backgroundID);
        }
        atts.recycle();
        setPadding(
                (int) ctx.getResources().getDimension(R.dimen.padding_large),
                0,
                (int) ctx.getResources().getDimension(R.dimen.padding_large), 0);
    }

    /**
     * Most important method in class that changes button picture and text
     * 
     * @param selected
     *        is true or false
     * @param resId
     *        ID of text to display
     */
    public void setSelectedStateAndText(boolean selected, int resId) {
        if (selected) {
            setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        } else {
            setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        }
        super.setSelected(selected);
        super.setText(resId);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
