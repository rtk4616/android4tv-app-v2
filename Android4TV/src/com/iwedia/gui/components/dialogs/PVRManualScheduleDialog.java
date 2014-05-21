package com.iwedia.gui.components.dialogs;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.iwedia.dtv.pvr.TimerCreateParams;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.dtv.types.TimerRepeatMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDatePicker;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVTimePicker;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.util.DateTimeConversions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * PVR Manual Schedule Recording dialog
 * 
 * @author Mladen Ilic
 */
public class PVRManualScheduleDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_PVR_SCHEDULE_REMINDER_START = 900,
            TV_MENU_PVR_SCHEDULE_REMINDER_DATE = 901,
            TV_MENU_PVR_SCHEDULE_REMINDER_TIME = 902,
            TV_MENU_PVR_SCHEDULE_REMINDER_DATE_END = 903,
            TV_MENU_PVR_SCHEDULE_REMINDER_TIME_END = 904;
    /** IDs for spinners */
    public static final int TV_MENU_PVR_SCHEDULE_REMINDER_CHANNEL = 905;
    public static final int TV_MENU_PVR_SCHEDULE_RECORDING_REPEAT = 906;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    // views from dialogs
    private A4TVButton btnSetTimeStart, btnAddReminder, btnSetDateStart,
            btnSetDateEnd, btnSetTimeEnd;
    private A4TVSpinner spinnerSelectChannel, spinnerRepeat;
    private static Date dateFromStream = null;
    private static Date dateStart = null;
    private static Date dateEnd = null;

    public PVRManualScheduleDialog(Context context) {
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

    private void init() {
        btnSetTimeStart = (A4TVButton) findViewById(TV_MENU_PVR_SCHEDULE_REMINDER_TIME);
        btnSetDateStart = (A4TVButton) findViewById(TV_MENU_PVR_SCHEDULE_REMINDER_DATE);
        btnSetTimeEnd = (A4TVButton) findViewById(TV_MENU_PVR_SCHEDULE_REMINDER_TIME_END);
        btnSetDateEnd = (A4TVButton) findViewById(TV_MENU_PVR_SCHEDULE_REMINDER_DATE_END);
        btnAddReminder = (A4TVButton) findViewById(TV_MENU_PVR_SCHEDULE_REMINDER_START);
        spinnerSelectChannel = (A4TVSpinner) findViewById(TV_MENU_PVR_SCHEDULE_REMINDER_CHANNEL);
        spinnerSelectChannel.setSelection(0);
        spinnerRepeat = (A4TVSpinner) findViewById(TV_MENU_PVR_SCHEDULE_RECORDING_REPEAT);
        spinnerRepeat.setSelection(0);
    }

    /**
     * Fill views with data from service
     */
    public void fillViewsWithData(boolean refreshAll) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss' 'dd/MM/yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            dateFromStream = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().getTimeDate().getCalendar()
                    .getTime();
            dateStart = dateFromStream;
            dateEnd = dateFromStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        dateStart.setSeconds(0);
        dateEnd.setSeconds(0);
        btnSetDateStart.setText(DateTimeConversions.getDateSting(dateFromStream));
        btnSetTimeStart.setText(DateTimeConversions.getTimeSting(dateFromStream));
        btnSetDateEnd.setText(DateTimeConversions.getDateSting(dateEnd));
        btnSetTimeEnd.setText(DateTimeConversions.getTimeSting(dateEnd));
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
        final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        switch (v.getId()) {
            case TV_MENU_PVR_SCHEDULE_REMINDER_START: {
                boolean result = startTimer();
                if (result) {
                    A4TVToast toast = new A4TVToast(ctx);
                    toast.showToast(R.string.tv_menu_pvr_schedule_toast);
                } else {
                    A4TVToast toast = new A4TVToast(ctx);
                    toast.showToast(R.string.tv_menu_pvr_schedule_toast_failed);
                }
                break;
            }
            case TV_MENU_PVR_SCHEDULE_REMINDER_TIME: {
                A4TVTimePicker timePicker = new A4TVTimePicker(ctx,
                        new OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute) {
                                view.clearFocus();
                                try {
                                    dateStart.setMinutes(minute);
                                    dateStart.setHours(hourOfDay);
                                    btnSetTimeStart.setText(formatTime
                                            .format(dateStart));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, dateStart.getHours(), dateStart.getMinutes(), true);
                timePicker.show();
                break;
            }
            case TV_MENU_PVR_SCHEDULE_REMINDER_DATE: {
                A4TVDatePicker datePicker = new A4TVDatePicker(ctx,
                        new OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth) {
                                view.clearFocus();
                                dateStart.setDate(dayOfMonth);
                                dateStart.setMonth(monthOfYear);
                                dateStart.setYear(year - 1900);
                                btnSetDateStart.setText(formatDate
                                        .format(dateStart));
                            }
                        }, dateStart.getYear() + 1900, dateStart.getMonth(),
                        dateStart.getDate());
                datePicker.show();
                break;
            }
            case TV_MENU_PVR_SCHEDULE_REMINDER_TIME_END: {
                A4TVTimePicker timePicker = new A4TVTimePicker(ctx,
                        new OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute) {
                                view.clearFocus();
                                try {
                                    dateEnd.setMinutes(minute);
                                    dateEnd.setHours(hourOfDay);
                                    btnSetTimeEnd.setText(formatTime
                                            .format(dateEnd));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, dateEnd.getHours(), dateEnd.getMinutes(), true);
                timePicker.show();
                break;
            }
            case TV_MENU_PVR_SCHEDULE_REMINDER_DATE_END: {
                A4TVDatePicker datePicker = new A4TVDatePicker(ctx,
                        new OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth) {
                                view.clearFocus();
                                dateEnd.setDate(dayOfMonth);
                                dateEnd.setMonth(monthOfYear);
                                dateEnd.setYear(year - 1900);
                                btnSetDateEnd.setText(formatDate
                                        .format(dateEnd));
                            }
                        }, dateEnd.getYear() + 1900, dateEnd.getMonth(),
                        dateEnd.getDate());
                datePicker.show();
                break;
            }
            default:
                break;
        }
    }

    private boolean startTimer() {
        TimeDate startTime = new TimeDate();
        TimeDate endTime = new TimeDate();
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(dateStart);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(dateEnd);
        int durationSec;
        int choosenRepeat = spinnerRepeat.getCHOOSEN_ITEM_INDEX();
        int choosenChannel = spinnerSelectChannel.getCHOOSEN_ITEM_INDEX();
        startTime.setSec(calendarStart.get(Calendar.SECOND));
        startTime.setMin(calendarStart.get(Calendar.MINUTE));
        startTime.setHour(calendarStart.get(Calendar.HOUR_OF_DAY));
        startTime.setDay(calendarStart.get(Calendar.DAY_OF_MONTH));
        startTime.setMonth(calendarStart.get(Calendar.MONTH) + 1);
        startTime.setYear(calendarStart.get(Calendar.YEAR) - 2000);
        endTime.setSec(calendarEnd.get(Calendar.SECOND));
        endTime.setMin(calendarEnd.get(Calendar.MINUTE));
        endTime.setHour(calendarEnd.get(Calendar.HOUR_OF_DAY));
        endTime.setDay(calendarEnd.get(Calendar.DAY_OF_MONTH));
        endTime.setMonth(calendarEnd.get(Calendar.MONTH) + 1);
        endTime.setYear(calendarEnd.get(Calendar.YEAR) - 2000);
        /*
         * Log.d(TAG, "startTime SECOND: "+ startTime.getSec()); Log.d(TAG,
         * "startTime MINUTE: "+ startTime.getMin()); Log.d(TAG,
         * "startTime HOUR_OF_DAY: "+ startTime.getHour()); Log.d(TAG,
         * "startTime DAY_OF_MONTH: "+ startTime.getDay()); Log.d(TAG,
         * "startTime MONTH: "+ startTime.getMonth()); Log.d(TAG,
         * "startTime YEAR: "+ startTime.getYear());
         */
        if (dateFromStream.before(dateStart)) {
            if (dateStart.before(dateEnd)) {
                durationSec = (int) (calendarEnd.getTimeInMillis() - calendarStart
                        .getTimeInMillis()) / 1000;
                Log.d(TAG, "duration: " + durationSec);
                try {
                    TimerCreateParams timerCreateParams = new TimerCreateParams(
                            choosenChannel, startTime, endTime,
                            TimerRepeatMode.getFromValue(choosenRepeat));
                    MainActivity.service.getPvrControl().createTimerRecord(
                            timerCreateParams);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
        return false;
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
        titleIDs.add(R.string.tv_menu_pvr_manual_schedule);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder_channel);
        list.add(TV_MENU_PVR_SCHEDULE_REMINDER_CHANNEL);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder_date);
        list.add(TV_MENU_PVR_SCHEDULE_REMINDER_DATE);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder_time);
        list.add(TV_MENU_PVR_SCHEDULE_REMINDER_TIME);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_schedule_date_end);
        list.add(TV_MENU_PVR_SCHEDULE_REMINDER_DATE_END);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_schedule_time_end);
        list.add(TV_MENU_PVR_SCHEDULE_REMINDER_TIME_END);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_scheduled_recording_repeat);
        list.add(TV_MENU_PVR_SCHEDULE_RECORDING_REPEAT);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_scheduled_recording_add);
        list.add(TV_MENU_PVR_SCHEDULE_REMINDER_START);
        contentListIDs.add(list);
    }
}
