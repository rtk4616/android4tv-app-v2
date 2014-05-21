package com.iwedia.gui.components.dialogs;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.TimePicker;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTimePicker;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Screensaver settings dialog
 * 
 * @author Mladen Ilic
 */
public class ScreensaverSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_SCREENSAVER_START = 1111,
            TV_MENU_SCREENSAVER_SET_TIME = 1112;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonSetTime;
    private A4TVButtonSwitch buttonStart;
    Context ctx;
    private int minutesTimers = 0;
    private int hoursTimers = 0;

    public ScreensaverSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init();
    }

    /** Get references from views and set look and feel */
    private void init() {
        buttonStart = (A4TVButtonSwitch) findViewById(TV_MENU_SCREENSAVER_START);
        buttonSetTime = (A4TVButton) findViewById(TV_MENU_SCREENSAVER_SET_TIME);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
        /* Get stored screen saver enable */
        boolean isEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.SCREENSAVER_ENABLED, true);
        buttonStart.setSelectedStateAndText(isEnabled,
                isEnabled ? R.string.button_text_on : R.string.button_text_off);
        /*
         * Get stored screen saver start time in milliseconds (default 10
         * minutes)
         */
        int screensaverSetTimeMiliseconds = MainActivity.sharedPrefs.getInt(
                MainActivity.SCREENSAVER_TIME_MILISECONDS,
                MainActivity.activity.getScreenSaverDialog().TIME_DEFAULT);
        StringBuilder builder = new StringBuilder();
        int minutes = (int) ((screensaverSetTimeMiliseconds / (1000 * 60)) % 60);
        int hours = (int) ((screensaverSetTimeMiliseconds / (1000 * 60 * 60)) % 24);
        String minutesStr = String.format("%02d", minutes);
        String hoursStr = String.format("%02d", hours);
        buttonSetTime.setText(builder.append(hoursStr).append(":")
                .append(minutesStr));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_SCREENSAVER_START: {
                if (v.isSelected()) {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                            R.string.button_text_off);
                } else {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                            R.string.button_text_on);
                }
                /* Save selection */
                MainActivity.sharedPrefs
                        .edit()
                        .putBoolean(MainActivity.SCREENSAVER_ENABLED,
                                v.isSelected()).commit();
                break;
            }
            case TV_MENU_SCREENSAVER_SET_TIME: {
                String minutes = "0";
                String hours = "0";
                A4TVTimePicker timePicker = new A4TVTimePicker(
                        ctx,
                        new OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute) {
                                view.clearFocus();
                                try {
                                    minutesTimers = minute;
                                    hoursTimers = hourOfDay;
                                    StringBuilder builder = new StringBuilder();
                                    String minutesTimersStr = String.format(
                                            "%02d", minutesTimers);
                                    String hoursTimersStr = String.format(
                                            "%02d", hoursTimers);
                                    ((A4TVButton) ScreensaverSettingsDialog.this
                                            .findViewById(TV_MENU_SCREENSAVER_SET_TIME))
                                            .setText(builder
                                                    .append(hoursTimersStr)
                                                    .append(":")
                                                    .append(minutesTimersStr));
                                    /* Save selection */
                                    MainActivity.sharedPrefs
                                            .edit()
                                            .putInt(MainActivity.SCREENSAVER_TIME_MILISECONDS,
                                                    hoursTimers * 60 * 60
                                                            * 1000
                                                            + minutesTimers
                                                            * 60 * 1000)
                                            .commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, Integer.parseInt(hours), Integer.parseInt(minutes),
                        true);
                timePicker.show();
                break;
            }
            default:
                break;
        }
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, null);
        setContentView(view);
    }

    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialog });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
        // clear old data in lists
        contentList.clear();
        contentListIDs.clear();
        titleIDs.clear();
        // title
        titleIDs.add(R.drawable.settings_icon);
        titleIDs.add(R.string.tv_menu_screensaver_settings);
        ArrayList<Integer> list;
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_screensaver_start);
        list.add(TV_MENU_SCREENSAVER_START);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_screensaver_set_time);
        list.add(TV_MENU_SCREENSAVER_SET_TIME);
        contentListIDs.add(list);
    }
}
