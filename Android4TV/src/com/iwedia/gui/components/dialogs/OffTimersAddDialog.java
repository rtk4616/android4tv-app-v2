package com.iwedia.gui.components.dialogs;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.view.View;
import android.widget.TimePicker;

import com.iwedia.comm.system.date_time.IDateTimeSettings;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.dtv.types.TimerRepeatMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVTimePicker;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.util.DateTimeConversions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Off timers add dialog
 * 
 * @author Mladen Ilic
 */
public class OffTimersAddDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_OFFTIMERS_SETTINGS_START = 871,
            TV_MENU_OFFTIMERS_SETTINGS_TIME = 872;
    /** IDs for spinners */
    public static final int TV_MENU_OFFTIMERS_SETTINGS_REPEAT_MODE = 873;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    // views from dialogs
    private A4TVButton btnSetTime, btnAddTimer;
    private A4TVSpinner spinnerSelectRepeatMode;
    private IDateTimeSettings dateTime;
    private Date date;
    private int minutesTimers = 0;
    private int hoursTimers = 0;

    public OffTimersAddDialog(Context context) {
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

    @Override
    public void show() {
        fillViewsWithData(true);
        super.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void cancel() {
        int repeatMode = 0;
        spinnerSelectRepeatMode.setSelection(repeatMode);
        btnSetTime.setClickable(true);
        btnAddTimer.setClickable(true);
        super.cancel();
    }

    private void init() {
        btnSetTime = (A4TVButton) findViewById(TV_MENU_OFFTIMERS_SETTINGS_TIME);
        btnAddTimer = (A4TVButton) findViewById(TV_MENU_OFFTIMERS_SETTINGS_START);
        spinnerSelectRepeatMode = (A4TVSpinner) findViewById(TV_MENU_OFFTIMERS_SETTINGS_REPEAT_MODE);
        // btnSetTime.setClickable(false);
        // btnAddTimer.setClickable(false);
        int repeatMode = 0;
        spinnerSelectRepeatMode.setSelection(repeatMode);
        spinnerSelectRepeatMode
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        if (spinnerSelectRepeatMode.getCHOOSEN_ITEM_INDEX() == 0) {
                            spinnerSelectRepeatMode
                                    .setSelection(spinnerSelectRepeatMode
                                            .getCHOOSEN_ITEM_INDEX());
                            // btnSetTime.setClickable(false);
                            // btnAddTimer.setClickable(false);
                        } else {
                            // btnSetTime.setClickable(true);
                            // btnAddTimer.setClickable(true);
                            spinnerSelectRepeatMode
                                    .setSelection(spinnerSelectRepeatMode
                                            .getCHOOSEN_ITEM_INDEX());
                        }
                    }
                });
    }

    /**
     * Fill views with data from service
     */
    public void fillViewsWithData(boolean refreshAll) {
        // if service returns real object
        dateTime = null;
        try {
            dateTime = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (dateTime != null) {
                date = dateTime.getTimeDate().getCalendar().getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date != null) {
            try {
                btnSetTime.setText(DateTimeConversions.getTimeSting(date));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
    public void onClick(final View v) {
        switch (v.getId()) {
            case TV_MENU_OFFTIMERS_SETTINGS_START: {
                startTimer();
                cancel();
                break;
            }
            case TV_MENU_OFFTIMERS_SETTINGS_TIME: {
                dateTime = null;
                date = null;
                try {
                    dateTime = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    if (dateTime != null) {
                        date = dateTime.getTimeDate().getCalendar().getTime();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                A4TVTimePicker timePicker = new A4TVTimePicker(ctx,
                        new OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute) {
                                view.clearFocus();
                                try {
                                    minutesTimers = minute;
                                    hoursTimers = hourOfDay;
                                    StringBuilder builder = new StringBuilder();
                                    ((A4TVButton) OffTimersAddDialog.this
                                            .findViewById(TV_MENU_OFFTIMERS_SETTINGS_TIME))
                                            .setText(DateTimeConversions
                                                    .getTimeSting(date));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, date.getHours(), date.getMinutes(), true);
                timePicker.show();
                break;
            }
            default:
                break;
        }
    }

    private void startTimer() {
        dateTime = null;
        date = null;
        TimeDate addTime = new TimeDate();
        int choosenRepeatMode;
        choosenRepeatMode = spinnerSelectRepeatMode.getCHOOSEN_ITEM_INDEX();
        try {
            MainActivity.service.getSetupControl().setOffTimerRepeat(
                    TimerRepeatMode.values()[choosenRepeatMode]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (addTime != null) {
            try {
                dateTime = MainActivity.service.getSystemControl()
                        .getDateAndTimeControl();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                if (dateTime != null) {
                    date = dateTime.getTimeDate().getCalendar().getTime();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Integer min = date.getMinutes();
            Integer hour = date.getHours();
            if (min <= minutesTimers) {
                int i = minutesTimers - min;
                addTime.setMin(i);
                if (hour <= hoursTimers) {
                    int i1 = hoursTimers - hour;
                    addTime.setHour(i1);
                } else {
                    int i2 = 24 - hour + hoursTimers;
                    addTime.setHour(i2);
                }
            } else {
                int j = 60 - min + minutesTimers;
                addTime.setMin(j);
                if (hour < hoursTimers) {
                    int j1 = hoursTimers - hour - 1;
                    addTime.setHour(j1);
                } else {
                    int j2 = 23 - hour + hoursTimers;
                    addTime.setHour(j2);
                }
            }
            try {
                MainActivity.service.getSetupControl().setOffTimer(addTime);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                MainActivity.service.getSetupControl().startOffTimer();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
        titleIDs.add(R.string.tv_menu_off_timers_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_off_timers_repeat_mode);
        list.add(TV_MENU_OFFTIMERS_SETTINGS_REPEAT_MODE);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_timers_settings_time);
        list.add(TV_MENU_OFFTIMERS_SETTINGS_TIME);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_off_timers_add);
        list.add(TV_MENU_OFFTIMERS_SETTINGS_START);
        contentListIDs.add(list);
    }
}
