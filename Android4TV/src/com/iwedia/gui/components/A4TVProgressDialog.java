package com.iwedia.gui.components;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;

public class A4TVProgressDialog extends A4TVAlertDialog {
    private LayoutInflater inflater;
    private A4TVTextView message;

    public A4TVProgressDialog(Context arg0) {
        super(arg0);
        inflater = (LayoutInflater) arg0
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_dialog_progress_layout,
                null);
        message = (A4TVTextView) view.findViewById(R.id.aTVTextViewMessage);
        setView(view);
    }

    @Override
    public A4TVAlertDialog setView(View view) {
        return super.setView(view);
    }

    @Override
    public A4TVAlertDialog setMessage(CharSequence text) {
        message.setText(text);
        return this;
    }

    @Override
    public A4TVAlertDialog setMessage(int textResId) {
        message.setText(textResId);
        return this;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
