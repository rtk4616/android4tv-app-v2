package com.iwedia.gui.components;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;

public class A4TVToast extends Toast {
    LayoutInflater inflater;
    TextView toastTextView;
    Handler handler;

    public A4TVToast(Context context) {
        super(context);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);
        toastTextView = (TextView) layout.findViewById(R.id.toastText);
        this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0,
                MainActivity.screenHeight / 7);
        this.setView(layout);
        this.setDuration(LENGTH_SHORT);
    }

    @Override
    public void setText(CharSequence s) {
        toastTextView.setText(s);
    }

    @Override
    public void show() {
        super.show();
    }

    public void showToast(String text) {
        toastTextView.setText(text);
        show();
    }

    public void showToast(int textId) {
        toastTextView.setText(textId);
        show();
    }
}
