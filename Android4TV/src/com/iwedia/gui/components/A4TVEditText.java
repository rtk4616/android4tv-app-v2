package com.iwedia.gui.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.mainmenu.MainMenuContent;

/**
 * Our EditText that has theme change implemented
 * 
 * @author Branimir Pavlovic
 */
public class A4TVEditText extends EditText {
    private Context ctx;
    private int lastEnteredKey = 0;
    public static final int INPUT_TYPE_NUMBER_PASSWORD = 18;

    /**
     * Default constructor, it would create default system button it should not
     * be used since it is not possible to reach and set any theme attributes
     * from here
     * 
     * @param context
     */
    public A4TVEditText(Context context) {
        super(context);
        ctx = context;
        setTag(MainMenuContent.TAGA4TVEditText);
        initEditText();
        setOnKeyListener(new EditKeyListener());
        addTextChangedListener(new TextWatcherEditText());
    }

    /**
     * This constructor is to be used
     * 
     * @param context
     * @param attrs
     */
    public A4TVEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        setTag(MainMenuContent.TAGA4TVEditText);
        initEditText();
        setOnKeyListener(new EditKeyListener());
        addTextChangedListener(new TextWatcherEditText());
    }

    // Virtual keyboard crash fix
    private void initEditText() {
        InputMethodManager mgr = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    private class EditKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            lastEnteredKey = keyCode;
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_CLEAR
                        || keyCode == KeyEvent.KEYCODE_FUNCTION
                        || keyCode == KeyEvent.KEYCODE_F10) {
                    v.onKeyDown(KeyEvent.KEYCODE_DEL, new KeyEvent(0, 0));
                    return true;
                }
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                    || keyCode == KeyEvent.KEYCODE_ENTER) {
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return false;
    }

    private class TextWatcherEditText implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            int inputType = getInputType();
            if (inputType == INPUT_TYPE_NUMBER_PASSWORD) {
                if (lastEnteredKey == KeyEvent.KEYCODE_0) {
                    if (after < count) {
                        setText(s);
                        setSelection(getText().length());
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public int getLastEnteredKey() {
        return lastEnteredKey;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
