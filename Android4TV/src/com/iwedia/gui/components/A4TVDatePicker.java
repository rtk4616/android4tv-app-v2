package com.iwedia.gui.components;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.iwedia.gui.MainActivity;

public class A4TVDatePicker extends DatePickerDialog implements
        android.content.DialogInterface.OnKeyListener {
    private final int ID = 16908290;
    NumberPicker[] mNumberPicker = new NumberPicker[3];
    Button mDoneButton = null;
    int mNumberInFocus = 0;

    public A4TVDatePicker(Context context, OnDateSetListener callBack,
            int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        setOnKeyListener(this);
    }

    public A4TVDatePicker(Context context, int theme,
            OnDateSetListener callBack, int year, int monthOfYear,
            int dayOfMonth) {
        super(context, theme, callBack, year, monthOfYear, dayOfMonth);
        setOnKeyListener(this);
    }

    public void getDatePickerControls() {
        DatePicker dp = getDatePicker();
        LinearLayout layout = (LinearLayout) dp.getChildAt(0);
        LinearLayout layout2 = (LinearLayout) layout.getChildAt(0);
        mNumberPicker[0] = (NumberPicker) layout2.getChildAt(0);
        mNumberPicker[1] = (NumberPicker) layout2.getChildAt(1);
        mNumberPicker[2] = (NumberPicker) layout2.getChildAt(2);
        mDoneButton = getButton(BUTTON_POSITIVE);
        dp.setCalendarViewShown(false);
        mDoneButton.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDatePickerControls();
    }

    private void setFocusToControl(int index) {
        if (index < 0 || index > 2) {
            if (mDoneButton != null) {
                mDoneButton.requestFocus();
            }
        } else {
            EditText text = (EditText) mNumberPicker[index].getChildAt(0);
            text.requestFocus();
            text.selectAll();
        }
    }

    private boolean changePickerValue(View view, int direction) {
        NumberPicker picker = null;
        for (int i = 0; i < 3; i++) {
            if (mNumberPicker[i].getChildAt(0).equals(view)) {
                picker = mNumberPicker[i];
                break;
            }
        }
        if (picker != null) {
            picker.setValue(picker.getValue() + direction);
            int year = mNumberPicker[2].getValue();
            int month = mNumberPicker[0].getValue();
            int dayOfMonth = mNumberPicker[1].getValue();
            getDatePicker().updateDate(year, month, dayOfMonth);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        View view = null;
        if (getCurrentFocus() != null) {
            view = getCurrentFocus();
        }
        /** If view is found */
        if (view != null) {
            /** If view is Edit Text return true because of keyboard crash */
            if (view instanceof EditText
                    && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mNumberInFocus = 3;
                    setFocusToControl(mNumberInFocus);
                }
                return true;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mNumberInFocus++;
                    if (mNumberInFocus > 3) {
                        mNumberInFocus = 0;
                    }
                    setFocusToControl(mNumberInFocus);
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mNumberInFocus--;
                    if (mNumberInFocus < 0) {
                        mNumberInFocus = 3;
                    }
                    setFocusToControl(mNumberInFocus);
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (changePickerValue(view, 1) == false) {
                        mNumberInFocus = 1;
                        setFocusToControl(mNumberInFocus);
                    }
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    if (changePickerValue(view, -1) == false) {
                        mNumberInFocus = 1;
                        setFocusToControl(mNumberInFocus);
                    }
                    return true;
                }
            }
        }
        /** If no view is found */
        else {
            return false;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
