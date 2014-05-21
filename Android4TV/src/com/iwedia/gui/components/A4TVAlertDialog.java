package com.iwedia.gui.components;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;

/**
 * Custom alert dialog builder class
 * 
 * @author Branimir Pavlovic
 */
public class A4TVAlertDialog extends Dialog implements OnKeyListener, OSDGlobal {
    private A4TVTextView title = null;
    private A4TVTextView message = null;
    private A4TVButton positiveButton, negativeButton;
    private View layoutView;
    private LinearLayout layoutActionsButtons, layoutMain;

    public A4TVAlertDialog(Context arg0) {
        super(arg0, checkTheme(arg0));
        setContentView(R.layout.alert_dialog_layout);
        getWindow().getAttributes().width = MainActivity.screenWidth;
        getWindow().getAttributes().height = MainActivity.screenHeight;
        /** Get references from layouts */
        layoutMain = (LinearLayout) findViewById(R.id.main);
        layoutActionsButtons = (LinearLayout) findViewById(R.id.actionButtons);
        layoutView = (LinearLayout) findViewById(R.id.view);
        positiveButton = (A4TVButton) findViewById(R.id.aTVButtonPositive);
        negativeButton = (A4TVButton) findViewById(R.id.aTVButtonNegative);
        title = (A4TVTextView) findViewById(R.id.aTVTextViewTitle);
        message = (A4TVTextView) findViewById(R.id.aTVTextViewMessage);
        layoutView.setVisibility(View.GONE);
        layoutActionsButtons.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        layoutMain.setLayoutParams(new FrameLayout.LayoutParams(
                MainActivity.dialogWidth + 10, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        setOnKeyListener(this);
    }

    @Override
    public void show() {
        findViewById(R.id.aTVButtonPositive).requestFocus();
        super.show();
    }

    public A4TVAlertDialog setPositiveButton(int textResId,
            android.view.View.OnClickListener listener) {
        layoutActionsButtons.setVisibility(View.VISIBLE);
        positiveButton.setVisibility(View.VISIBLE);
        positiveButton.setText(textResId);
        positiveButton.setOnClickListener(listener);
        return this;
    }

    public A4TVAlertDialog setNegativeButton(int textResId,
            android.view.View.OnClickListener listener) {
        layoutActionsButtons.setVisibility(View.VISIBLE);
        negativeButton.setVisibility(View.VISIBLE);
        negativeButton.setText(textResId);
        negativeButton.setOnClickListener(listener);
        return this;
    }

    public A4TVAlertDialog setTitleOfAlertDialog(int textResId) {
        title.setText(textResId);
        return this;
    }

    public A4TVAlertDialog setTitleOfAlertDialog(CharSequence text) {
        title.setText(text);
        return this;
    }

    public A4TVAlertDialog setMessage(int textResId) {
        layoutView.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        message.setText(textResId);
        return this;
    }

    public A4TVAlertDialog setMessage(CharSequence text) {
        layoutView.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        message.setText(text);
        return this;
    }

    public A4TVAlertDialog setView(View view) {
        layoutView.setVisibility(View.VISIBLE);
        ((LinearLayout) layoutView).removeAllViews();
        // layoutMain.removeViewInLayout(layoutView);
        ((LinearLayout) layoutView).addView(view);
        return this;
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialogTransparent });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }

    public A4TVButton getPositiveButton() {
        return positiveButton;
    }

    public A4TVButton getNegativeButton() {
        return negativeButton;
    }

    public A4TVTextView getTitle() {
        return title;
    }

    public A4TVTextView getMessage() {
        return message;
    }

    public void setPositiveButton(A4TVButton positiveButton) {
        this.positiveButton = positiveButton;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        switch (keyCode) {
        // ///////////////////////////////////////////////////////////////////
        // VOLUME UP
        // ///////////////////////////////////////////////////////////////////
        // case KeyEvent.KEYCODE_F6:
            case KeyEvent.KEYCODE_VOLUME_UP: {
                IOSDHandler curlHandler = MainActivity.activity.getPageCurl();
                curlHandler.volume(VOLUME_UP, false);
                return true;
            }
            // ///////////////////////////////////////////////////////////////////
            // VOLUME DOWN
            // ///////////////////////////////////////////////////////////////////
            // case 135: {
            // case KeyEvent.KEYCODE_F5: {
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                IOSDHandler curlHandler = MainActivity.activity.getPageCurl();
                curlHandler.volume(VOLUME_DOWN, false);
                return true;
            }
            // ///////////////////////////////////////////////////////////////////
            // VOLUME MUTE
            // ///////////////////////////////////////////////////////////////////
            case KeyEvent.KEYCODE_MUTE: {
                IOSDHandler curlHandler = MainActivity.activity.getPageCurl();
                curlHandler.volume(VOLUME_MUTE, false);
                return true;
            }
            case KeyEvent.KEYCODE_BACK:
                return false;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
