package com.iwedia.gui.listeners;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.iwedia.gui.MainActivity;

/**
 * Click Listener for spinners in dialogs
 * 
 * @author Branimir Pavlovic
 */
public class DialogsOnSpinnerClickListener implements OnItemSelectedListener {
    @SuppressWarnings("unused")
    private MainActivity activity;

    public DialogsOnSpinnerClickListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
