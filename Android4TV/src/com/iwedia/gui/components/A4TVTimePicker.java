package com.iwedia.gui.components;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.util.ViewHelper;

public class A4TVTimePicker extends TimePickerDialog implements
        android.content.DialogInterface.OnKeyListener {
    NumberPicker[] mNumberPicker = new NumberPicker[2];
    Button mDoneButton = null;
    int mNumberInFocus = 0;
    TimePicker mTimePicker;

    public A4TVTimePicker(Context context, int theme,
            OnTimeSetListener callBack, int hourOfDay, int minute,
            boolean is24HourView) {
        super(context, theme, callBack, hourOfDay, minute, is24HourView);
        setOnKeyListener(this);
    }

    public A4TVTimePicker(Context context, OnTimeSetListener callBack,
            int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
        setOnKeyListener(this);
    }

    public void getTimePickerControls() {
        ViewGroup root = (ViewGroup) getButton(BUTTON_POSITIVE).getRootView();
        mTimePicker = (TimePicker) ViewHelper.findViewByClass(root,
                TimePicker.class);
        if (mTimePicker != null) {
            LinearLayout layout = (LinearLayout) mTimePicker.getChildAt(0);
            mNumberPicker[0] = (NumberPicker) layout.getChildAt(0);
            mNumberPicker[1] = (NumberPicker) layout.getChildAt(2);
        }
        mDoneButton = getButton(BUTTON_POSITIVE);
        mDoneButton.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTimePickerControls();
    }

    private void setFocusToControl(int index) {
        if (index < 0 || index > 1) {
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
        for (int i = 0; i < 2; i++) {
            if (mNumberPicker[i].getChildAt(0).equals(view)) {
                picker = mNumberPicker[i];
                break;
            }
        }
        if (picker != null) {
            picker.setValue(picker.getValue() + direction);
            int hour = mNumberPicker[0].getValue();
            int minute = mNumberPicker[1].getValue();
            updateTime(hour, minute);
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
                    mNumberInFocus = 2;
                    setFocusToControl(mNumberInFocus);
                }
                return true;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mNumberInFocus++;
                    if (mNumberInFocus > 2) {
                        mNumberInFocus = 0;
                    }
                    setFocusToControl(mNumberInFocus);
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mNumberInFocus--;
                    if (mNumberInFocus < 0) {
                        mNumberInFocus = 2;
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
