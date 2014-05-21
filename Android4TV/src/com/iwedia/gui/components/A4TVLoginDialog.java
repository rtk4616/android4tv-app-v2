package com.iwedia.gui.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;

/**
 * Custom alert dialog builder class
 * 
 * @author Mladen Ilic
 */
public class A4TVLoginDialog extends A4TVAlertDialog {
    private final int NUMBER_OF_CHARS_NEEDED = 0;
    private LayoutInflater inflater;
    private A4TVEditText editText1, editText2;
    private boolean enableOkButton1, enableOkButton2;

    public A4TVLoginDialog(Context arg0, boolean isTextWatcherEnabled) {
        super(arg0);
        this.setCancelable(false);
        inflater = (LayoutInflater) arg0
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.login_settings_view, null);
        this.setView(layout);
        editText1 = (A4TVEditText) layout.findViewById(R.id.editTextLogin);
        editText2 = (A4TVEditText) layout
                .findViewById(R.id.editTextLoginPassword);
        editText2.setVisibility(View.GONE);
        // Attach text watchers
        if (isTextWatcherEnabled) {
            editText1.addTextChangedListener(new MyTextWatcher(editText1
                    .getId()));
            editText2.addTextChangedListener(new MyTextWatcher(editText2
                    .getId()));
        }
    }

    @Override
    public void show() {
        super.show();
        getPositiveButton().setEnabled(false);
        getPositiveButton().setFocusable(false);
        editText1.requestFocus();
        // Check which edit text is visible
        if (!editText2.isShown()) {
            enableOkButton2 = true;
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        editText1.setText("");
        editText2.setText("");
    }

    private class MyTextWatcher implements TextWatcher {
        int id = 0;

        public MyTextWatcher(int id) {
            this.id = id;
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(MainActivity.TAG, "afterTextChanged: " + s.toString());
            // A4TVEditText editTextThis = ((A4TVEditText) A4TVLoginDialog.this
            // .findViewById(id));
            getPositiveButton().setEnabled(true);
            getPositiveButton().setFocusable(true);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            Log.d(MainActivity.TAG, "beforeTextChanged1: " + s.toString());
            A4TVEditText editTextThis = ((A4TVEditText) A4TVLoginDialog.this
                    .findViewById(id));
            int inputType = editTextThis.getInputType();
            if (inputType == A4TVEditText.INPUT_TYPE_NUMBER_PASSWORD) {
                if (editTextThis.getLastEnteredKey() == KeyEvent.KEYCODE_0) {
                    if (after < count) {
                        editTextThis.setText(s);
                        editTextThis.setSelection(editTextThis.getText()
                                .length());
                    }
                }
            }
            Log.d(MainActivity.TAG, "beforeTextChanged2: " + s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        }
    }

    public A4TVEditText getEditText1() {
        return editText1;
    }

    public void setEditText1(A4TVEditText editText1) {
        this.editText1 = editText1;
    }

    public A4TVEditText getEditText2() {
        return editText2;
    }

    public void setEditText2(A4TVEditText editText2) {
        this.editText2 = editText2;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
