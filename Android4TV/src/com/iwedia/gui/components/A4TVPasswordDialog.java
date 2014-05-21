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
 * @author Branimir Pavlovic
 */
public class A4TVPasswordDialog extends A4TVAlertDialog {
    public static final String TAG = "A4TVPasswordDialog";
    private final int NUMBER_OF_CHARS_NEEDED = 4;
    private LayoutInflater inflater;
    private A4TVEditText editText1, editText2, editText3;
    private boolean enableOkButton1, enableOkButton2, enableOkButton3;

    public A4TVPasswordDialog(Context arg0, boolean isTextWatcherEnabled) {
        super(arg0);
        this.setCancelable(false);
        inflater = (LayoutInflater) arg0
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.security_settings_password_view, null);
        this.setView(layout);
        editText1 = (A4TVEditText) layout
                .findViewById(R.id.editTextFirstPassword);
        editText2 = (A4TVEditText) layout
                .findViewById(R.id.editTextSecondPassword);
        editText2.setVisibility(View.GONE);
        editText3 = (A4TVEditText) layout
                .findViewById(R.id.editTextThirdPassword);
        editText3.setVisibility(View.GONE);
        // Attach text watchers
        if (isTextWatcherEnabled) {
            editText1.addTextChangedListener(new MyTextWatcher(editText1
                    .getId()));
            editText2.addTextChangedListener(new MyTextWatcher(editText2
                    .getId()));
            editText3.addTextChangedListener(new MyTextWatcher(editText3
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
        if (!editText3.isShown()) {
            enableOkButton3 = true;
        }
    }

    @Override
    public void cancel() {
        super.cancel();
        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
    }

    private class MyTextWatcher implements TextWatcher {
        int id = 0;

        public MyTextWatcher(int id) {
            this.id = id;
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: " + s.toString());
            A4TVEditText editTextThis = ((A4TVEditText) A4TVPasswordDialog.this
                    .findViewById(id));
            switch (this.id) {
                case R.id.editTextFirstPassword: {
                    if (editTextThis.getText().length() == NUMBER_OF_CHARS_NEEDED) {
                        enableOkButton1 = true;
                    } else {
                        enableOkButton1 = false;
                    }
                    break;
                }
                case R.id.editTextSecondPassword: {
                    if (editTextThis.getText().length() == NUMBER_OF_CHARS_NEEDED) {
                        enableOkButton2 = true;
                    } else {
                        enableOkButton2 = false;
                    }
                    break;
                }
                case R.id.editTextThirdPassword: {
                    if (editTextThis.getText().length() == NUMBER_OF_CHARS_NEEDED) {
                        enableOkButton3 = true;
                    } else {
                        enableOkButton3 = false;
                    }
                    break;
                }
            }
            // Enable OK button
            if (enableOkButton1 && enableOkButton2 && enableOkButton3) {
                getPositiveButton().setEnabled(true);
                getPositiveButton().setFocusable(true);
            } else {
                getPositiveButton().setEnabled(false);
                getPositiveButton().setFocusable(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            Log.d(TAG, "beforeTextChanged1: " + s.toString());
            A4TVEditText editTextThis = ((A4TVEditText) A4TVPasswordDialog.this
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
            Log.d(TAG, "beforeTextChanged2: " + s.toString());
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

    public A4TVEditText getEditText3() {
        return editText3;
    }

    public void setEditText3(A4TVEditText editText3) {
        this.editText3 = editText3;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
