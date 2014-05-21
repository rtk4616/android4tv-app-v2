package com.iwedia.gui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.SeekBar;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.mainmenu.MainMenuContent;

/**
 * Theme change implemented in this class
 * 
 * @author Branimir Pavlovic
 */
public class A4TVProgressBar extends SeekBar {
    private String text = "";
    private int textColor = Color.WHITE;
    private float textSize;
    private boolean showText = true;

    /**
     * This constructor should be used for A4TVProgressBar
     * 
     * @param context
     * @param attrs
     * @param canUserManipulate
     */
    public A4TVProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTag(MainMenuContent.TAGA4TVProgressBar);
        // draw initial text
        setText(String.valueOf(getProgress()));
        textSize = context.getResources().getDimension(
                R.dimen.a4tvdialog_progress_text_size);
    }

    public A4TVProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTag(MainMenuContent.TAGA4TVProgressBar);
        // draw initial text
        setText(String.valueOf(getProgress()));
        textSize = context.getResources().getDimension(
                R.dimen.a4tvdialog_progress_text_size);
    }

    public A4TVProgressBar(Context context, boolean show) {
        super(context);
        setTag(MainMenuContent.TAGA4TVProgressBar);
        showText = show;
        // draw initial text
        setText(String.valueOf(getProgress()));
        textSize = context.getResources().getDimension(
                R.dimen.a4tvdialog_progress_text_size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // create an instance of class Paint, set color and font size
        if (showText) {
            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(textColor);
            textPaint.setTextSize(textSize);
            // In order to show text in a middle, we need to know its size
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            Typeface face = Typeface.create("Sans", Typeface.BOLD);
            textPaint.setTypeface(face);
            // Now we store font size in bounds variable and can calculate it's
            // position
            int x = 15;// bounds.centerX();// getWidth() / 2 - bounds.centerX();
            int y = getHeight() / 2 - bounds.centerY();
            // drawing text with appropriate color and size in the center
            canvas.drawText(text, x, y, textPaint);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text != null) {
            if (text.contains("%")) {
                this.text = text;
            } else {
                this.text = text + " %";
            }
        } else {
            this.text = "0 %";
        }
        postInvalidate();
    }

    public void setTextInformation(String text, String information) {
        if (text != null) {
            if (text.contains("%")) {
                this.text = information + " " + text;
            } else {
                this.text = information + " " + text + " %";
            }
        } else {
            this.text = "0 %";
        }
        postInvalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public synchronized void setTextColor(int textColor) {
        this.textColor = textColor;
        postInvalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public synchronized void setTextSize(float textSize) {
        this.textSize = textSize;
        postInvalidate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
