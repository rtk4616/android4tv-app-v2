package com.iwedia.gui.components.dialogs;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.iwedia.comm.enums.DateFormatOrder;
import com.iwedia.comm.system.date_time.IDateTimeSettings;
import com.iwedia.comm.system.date_time.TimeZone;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDatePicker;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVTimePicker;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.util.DateTimeConversions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Time & date settings dialog
 * 
 * @author Branimir Pavlovic
 */
public class TimeAndDateSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_TIME_AND_DATE_SETTINGS_AUTOMATIC = 48,
            TV_MENU_TIME_AND_DATE_SETTINGS_SET_DATE = 49,
            TV_MENU_TIME_AND_DATE_SETTINGS_SET_TIME = 50,
            TV_MENU_TIME_AND_DATE_SETTINGS_USE_24_HOUR_FORMAT = 51,
            TV_MENU_TIME_AND_DATE_SETTINGS_TIMER = 52;
    /** IDs for spinner */
    public static final int TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_TIME_ZONE = 11,
            TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_DATE_FORMAT = 15;
    // private final int HOURS_TO_ADD_FOR_PM_TIME = 12;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    private IDateTimeSettings dateTime;
    private boolean is24HourFormat;
    // private Timer timer;
    private Date date;
    // views from dialogs
    private A4TVButtonSwitch btnAutomatic, btn24Format;
    private A4TVButton btnSetDate, btnSetTime;
    private A4TVSpinner spinnerSelectTimeZone, spinnerSelectDateFormat;
    private Thread backgroundThread;
    private Runnable run;
    private Handler handler;
    private static SimpleDateFormat formatMDY = new SimpleDateFormat(
            "MM/dd/yyyy");
    private static SimpleDateFormat formatDMY = new SimpleDateFormat(
            "dd/MM/yyyy");
    private static SimpleDateFormat formatYMD = new SimpleDateFormat(
            "yyyy/MM/dd");

    public TimeAndDateSettingsDialog(Context context) {
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
        startThread(run);
        // fillViewsWithData(true);
        super.show();
    }

    @Override
    public void onBackPressed() {
        stopThread();
        super.onBackPressed();
    }

    @Override
    public void cancel() {
        stopThread();
        super.cancel();
    }

    private void init() {
        btnAutomatic = (A4TVButtonSwitch) findViewById(TV_MENU_TIME_AND_DATE_SETTINGS_AUTOMATIC);
        btn24Format = (A4TVButtonSwitch) findViewById(TV_MENU_TIME_AND_DATE_SETTINGS_USE_24_HOUR_FORMAT);
        btnSetDate = (A4TVButton) findViewById(TV_MENU_TIME_AND_DATE_SETTINGS_SET_DATE);
        spinnerSelectTimeZone = (A4TVSpinner) findViewById(TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_TIME_ZONE);
        spinnerSelectDateFormat = (A4TVSpinner) findViewById(TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_DATE_FORMAT);
        btnSetTime = (A4TVButton) findViewById(TV_MENU_TIME_AND_DATE_SETTINGS_SET_TIME);
        spinnerSelectDateFormat
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getSystemControl()
                                    .getDateAndTimeControl()
                                    .setDateFormat(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        spinnerSelectTimeZone
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        List<TimeZone> timeZones = null;
                        try {
                            timeZones = MainActivity.service.getSystemControl()
                                    .getDateAndTimeControl().getTimeZones();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                        if (timeZones != null) {
                            String buttonText = contents[index];
                            for (int i = 0; i < timeZones.size(); i++) {
                                if (buttonText.contains(timeZones.get(i)
                                        .getDisplayName())
                                        && buttonText.contains(timeZones.get(i)
                                                .getGmt())) {
                                    // set custom text to button
                                    spinner.setText(timeZones.get(i)
                                            .getDisplayName());
                                    // set time zone to service
                                    try {
                                        MainActivity.service
                                                .getSystemControl()
                                                .getDateAndTimeControl()
                                                .setTimeZone(
                                                        timeZones.get(i)
                                                                .getId());
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    } catch (RuntimeException e) {
                                        e.printStackTrace();
                                    }
                                    // exit for loop
                                    break;
                                }
                            }
                        }
                        fillViewsWithData(true);
                    }
                });
        // init runnable to be run in thread
        run = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Thread thisThread = Thread.currentThread();
                    if (thisThread.equals(backgroundThread)) {
                        handler.sendEmptyMessage(0);
                        try {
                            // Sleep 5 seconds
                            Thread.sleep(5 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
        };
        // init handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                fillViewsWithData(true);
                super.handleMessage(msg);
            }
        };
    }

    /**
     * Fill views with data from service
     */
    public void fillViewsWithData(boolean refreshAll) {
        dateTime = null;
        try {
            dateTime = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if service returns real object
        if (dateTime != null) {
            boolean hourAutomatic = false;
            try {
                hourAutomatic = dateTime.isAutomatic();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (refreshAll) {
                /**************************************************************/
                /** Automatic time update */
                if (hourAutomatic) {
                    btnAutomatic.setSelectedStateAndText(true,
                            R.string.button_text_yes);
                } else {
                    btnAutomatic.setSelectedStateAndText(false,
                            R.string.button_text_no);
                }
                /**************************************************************/
                /** Use 24 hour format button */
                boolean hourFormat = true;
                try {
                    hourFormat = dateTime.is24HourFormat();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /** Fill 24 hour format button */
                if (hourFormat) {
                    btn24Format.setSelectedStateAndText(true,
                            R.string.button_text_yes);
                } else {
                    btn24Format.setSelectedStateAndText(false,
                            R.string.button_text_no);
                }
                /**************************************************************/
                int dateFormatIndex = 0;
                try {
                    dateFormatIndex = dateTime.getDateFormat();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                spinnerSelectDateFormat.setSelection(dateFormatIndex);
                /**************************************************************/
                if (hourAutomatic) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_time_and_date_settings_set_date,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_time_and_date_settings_set_date,
                            DisplayMode.SHOW);
                }
                Date date = null;
                try {
                    date = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl().getTimeDate()
                            .getCalendar().getTime();
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (date != null) {
                    switch (spinnerSelectDateFormat.getCHOOSEN_ITEM_INDEX()) {
                        case DateFormatOrder.DMY: {
                            btnSetDate.setText(formatDMY.format(date));
                            break;
                        }
                        case DateFormatOrder.MDY: {
                            btnSetDate.setText(formatMDY.format(date));
                            break;
                        }
                        case DateFormatOrder.YMD: {
                            btnSetDate.setText(formatYMD.format(date));
                            break;
                        }
                        default:
                            break;
                    }
                }
                /**************************************************************/
                // if (hourAutomatic) {
                // spinnerSelectTimeZone.setEnabled(false);
                // } else {
                // spinnerSelectTimeZone.setEnabled(true);
                // }
                int zoneId = -1;
                List<TimeZone> zones = null;
                try {
                    zones = dateTime.getTimeZones();
                    zoneId = dateTime.getActiveTimeZoneIndex();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (zoneId >= 0
                        && zones != null
                        && spinnerSelectTimeZone.getCHOOSEN_ITEM_INDEX() != zoneId) {
                    spinnerSelectTimeZone.setSelection(zoneId);
                    spinnerSelectTimeZone.setText(zones.get(zoneId)
                            .getTimeZoneName());
                }
                /**************************************************************/
            }
            if (hourAutomatic) {
                setLayoutDisplayMode(
                        R.string.tv_menu_time_and_date_settings_set_time,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(
                        R.string.tv_menu_time_and_date_settings_set_time,
                        DisplayMode.SHOW);
            }
            date = null;
            try {
                date = dateTime.getTimeDate().getCalendar().getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String hour = "";
            if (date != null) {
                try {
                    btnSetTime.setText(DateTimeConversions.getTimeSting(date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /**************************************************************/
        }
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, null);// ,
        // pictureBackgroundID);
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
            case TV_MENU_TIME_AND_DATE_SETTINGS_AUTOMATIC: {
                if (v.isSelected()) {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                            R.string.button_text_no);
                } else {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                            R.string.button_text_yes);
                }
                try {
                    MainActivity.service.getSystemControl()
                            .getDateAndTimeControl()
                            .setAutomatic(v.isSelected());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fillViewsWithData(true);
                break;
            }
            case TV_MENU_TIME_AND_DATE_SETTINGS_USE_24_HOUR_FORMAT: {
                if (v.isSelected()) {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                            R.string.button_text_no);
                } else {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                            R.string.button_text_yes);
                }
                try {
                    MainActivity.service.getSystemControl()
                            .getDateAndTimeControl()
                            .set24HourFormat(v.isSelected());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fillViewsWithData(true);
                break;
            }
            case TV_MENU_TIME_AND_DATE_SETTINGS_SET_DATE: {
                dateTime = null;
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
                if (date != null) {
                    try {
                        A4TVDatePicker datePicker = new A4TVDatePicker(ctx,
                                new OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view,
                                            int year, int monthOfYear,
                                            int dayOfMonth) {
                                        view.clearFocus();
                                        try {
                                            dateTime.setDate(dayOfMonth,
                                                    monthOfYear, year);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        TimeAndDateSettingsDialog.this.show();
                                    }
                                }, date.getYear() - 1900, date.getMonth(),
                                date.getDay());
                        datePicker.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_TIME_AND_DATE_SETTINGS_SET_TIME: {
                dateTime = null;
                try {
                    dateTime = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                date = null;
                try {
                    if (dateTime != null) {
                        date = dateTime.getTimeDate().getCalendar().getTime();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (date != null) {
                    is24HourFormat = true;
                    try {
                        is24HourFormat = dateTime.is24HourFormat();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    A4TVTimePicker timePicker = new A4TVTimePicker(
                            ctx,
                            new OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view,
                                        int hourOfDay, int minute) {
                                    Log.d("A4TV2.0",
                                            "CALL BACK SET TIME RETURNED: "
                                                    + hourOfDay + " " + minute);
                                    view.clearFocus();
                                    try {
                                        dateTime.setTime(hourOfDay, minute);
                                        ((A4TVButton) TimeAndDateSettingsDialog.this
                                                .findViewById(TV_MENU_TIME_AND_DATE_SETTINGS_SET_TIME))
                                                .setText(DateTimeConversions
                                                        .getTimeSting(date));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    TimeAndDateSettingsDialog.this.show();
                                }
                            }, date.getHours(), date.getMinutes(),
                            is24HourFormat);
                    timePicker.show();
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * Start background thread
     * 
     * @param run
     *        Runnable to run in thread
     */
    public void startThread(Runnable run) {
        Log.d(MainActivity.TAG, "start thread entered");
        if (backgroundThread == null) {
            backgroundThread = new Thread(run);
            backgroundThread.setPriority(Thread.MIN_PRIORITY);
            backgroundThread.start();
        }
    }

    /**
     * Stops background thread
     */
    public void stopThread() {
        Log.d(MainActivity.TAG, "stop thread entered");
        if (backgroundThread != null) {
            Thread moribund = backgroundThread;
            backgroundThread = null;
            moribund.interrupt();
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
        titleIDs.add(R.string.tv_menu_time_and_date_settings);
        // automatic******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_time_and_date_settings_automatic);
        list.add(TV_MENU_TIME_AND_DATE_SETTINGS_AUTOMATIC);
        contentListIDs.add(list);
        // set date******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_time_and_date_settings_set_date);
        list.add(TV_MENU_TIME_AND_DATE_SETTINGS_SET_DATE);
        contentListIDs.add(list);
        // select time zone******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_time_and_date_settings_select_time_zone);
        list.add(TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_TIME_ZONE);
        contentListIDs.add(list);
        // set time******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_time_and_date_settings_set_time);
        list.add(TV_MENU_TIME_AND_DATE_SETTINGS_SET_TIME);
        contentListIDs.add(list);
        // use 24 hour format******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_time_and_date_settings_use_24_hour_format);
        list.add(TV_MENU_TIME_AND_DATE_SETTINGS_USE_24_HOUR_FORMAT);
        contentListIDs.add(list);
        // select date format******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_time_and_date_settings_select_date_format);
        list.add(TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_DATE_FORMAT);
        contentListIDs.add(list);
        if (ConfigHandler.TV_FEATURES) {
            // timer******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_time_and_date_settings_timer);
            list.add(TV_MENU_TIME_AND_DATE_SETTINGS_TIMER);
            contentListIDs.add(list);
        }
    }
}
