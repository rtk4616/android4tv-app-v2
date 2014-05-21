package com.iwedia.gui.components.dialogs;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.iwedia.dtv.reminder.ReminderTimerParam;
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
 * PVR Manual Event Reminder dialog
 * 
 * @author Mladen Ilic
 */
public class PVRManualEventReminderDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_PVR_MANUAL_REMINDER_START = 900,
            TV_MENU_PVR_MANUAL_REMINDER_DATE = 901,
            TV_MENU_PVR_MANUAL_REMINDER_TIME = 902;
    /** IDs for spinners */
    public static final int TV_MENU_PVR_MANUAL_REMINDER_CHANNEL = 903;
    public static final int TV_MENU_PVR_MANUAL_REMINDER_REPEAT = 904;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    // views from dialogs
    private A4TVButton btnSetTime, btnAddReminder, btnSetDate;
    private A4TVSpinner spinnerSelectChannel;
    private A4TVSpinner spinnerSelectRepeat;
    private static Date dateFromStream = null;
    private static Date dateStart = null;

    public PVRManualEventReminderDialog(Context context) {
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
        btnSetTime = (A4TVButton) findViewById(TV_MENU_PVR_MANUAL_REMINDER_TIME);
        btnSetDate = (A4TVButton) findViewById(TV_MENU_PVR_MANUAL_REMINDER_DATE);
        btnAddReminder = (A4TVButton) findViewById(TV_MENU_PVR_MANUAL_REMINDER_START);
        spinnerSelectChannel = (A4TVSpinner) findViewById(TV_MENU_PVR_MANUAL_REMINDER_CHANNEL);
        spinnerSelectRepeat = (A4TVSpinner) findViewById(TV_MENU_PVR_MANUAL_REMINDER_REPEAT);
        spinnerSelectChannel.setSelection(0);
        spinnerSelectRepeat.setSelection(0);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        dateStart.setSeconds(0);
        btnSetDate.setText(DateTimeConversions.getDateSting(dateFromStream));
        btnSetTime.setText(DateTimeConversions.getTimeSting(dateFromStream));
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
            case TV_MENU_PVR_MANUAL_REMINDER_START: {
                boolean result = startTimer();
                if (result) {
                    A4TVToast toast = new A4TVToast(ctx);
                    toast.showToast(R.string.tv_menu_pvr_manual_reminder_toast);
                } else {
                    A4TVToast toast = new A4TVToast(ctx);
                    toast.showToast(R.string.tv_menu_pvr_manual_reminder_toast_failed);
                }
                break;
            }
            case TV_MENU_PVR_MANUAL_REMINDER_TIME: {
                A4TVTimePicker timePicker = new A4TVTimePicker(ctx,
                        new OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                    int hourOfDay, int minute) {
                                view.clearFocus();
                                try {
                                    dateStart.setMinutes(minute);
                                    dateStart.setHours(hourOfDay);
                                    btnSetTime.setText(formatTime
                                            .format(dateStart));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, dateStart.getHours(), dateStart.getMinutes(), true);
                timePicker.show();
                break;
            }
            case TV_MENU_PVR_MANUAL_REMINDER_DATE:
                A4TVDatePicker datePicker = new A4TVDatePicker(
                        ctx,
                        new OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                    int monthOfYear, int dayOfMonth) {
                                view.clearFocus();
                                dateStart.setDate(dayOfMonth);
                                dateStart.setMonth(monthOfYear);
                                dateStart.setYear(year - 1900);
                                btnSetDate.setText(formatDate.format(dateStart));
                            }
                        }, dateStart.getYear() + 1900, dateStart.getMonth(),
                        dateStart.getDate());
                datePicker.show();
                break;
            default:
                break;
        }
    }

    private boolean startTimer() {
        int mChoosenChannel;
        TimerRepeatMode mRepeat;
        TimeDate mStartTime = new TimeDate();
        TimeDate mEndTime = new TimeDate();
        // ReminderEvent mReminder = new ReminderEvent();
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(dateStart);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(dateStart);
        mStartTime.setSec(calendarStart.get(Calendar.SECOND));
        mStartTime.setMin(calendarStart.get(Calendar.MINUTE));
        mStartTime.setHour(calendarStart.get(Calendar.HOUR_OF_DAY));
        mStartTime.setDay(calendarStart.get(Calendar.DAY_OF_MONTH));
        mStartTime.setMonth(calendarStart.get(Calendar.MONTH) + 1);
        mStartTime.setYear(calendarStart.get(Calendar.YEAR) - 2000);
        mEndTime.setSec(calendarStart.get(Calendar.SECOND));
        mEndTime.setMin(calendarStart.get(Calendar.MINUTE));
        mEndTime.setHour(calendarStart.get(Calendar.HOUR_OF_DAY));
        mEndTime.setDay(calendarStart.get(Calendar.DAY_OF_MONTH));
        mEndTime.setMonth(calendarStart.get(Calendar.MONTH) + 1);
        mEndTime.setYear(calendarStart.get(Calendar.YEAR) - 2000);
        mRepeat = TimerRepeatMode.getFromValue(spinnerSelectRepeat
                .getCHOOSEN_ITEM_INDEX());
        mChoosenChannel = spinnerSelectChannel.getCHOOSEN_ITEM_INDEX();
        ReminderTimerParam param = new ReminderTimerParam(mChoosenChannel,
                mRepeat, mStartTime);
        // mReminder.setStartTime(mStartTime);
        // mReminder.setEndTime(mEndTime);
        // mReminder.setName("");
        // mReminder.setServiceIndex(mChoosenChannel);
        // mReminder.setRepeatMode(mRepeat);
        if (dateFromStream.before(dateStart)) {
            try {
                MainActivity.service.getReminderControl().createTimer(param);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
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
        titleIDs.add(R.string.tv_menu_pvr_manual_reminder);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder_channel);
        list.add(TV_MENU_PVR_MANUAL_REMINDER_CHANNEL);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder_date);
        list.add(TV_MENU_PVR_MANUAL_REMINDER_DATE);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder_time);
        list.add(TV_MENU_PVR_MANUAL_REMINDER_TIME);
        contentListIDs.add(list);
        // //////////////////////////////
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_scheduled_recording_repeat);
        list.add(TV_MENU_PVR_MANUAL_REMINDER_REPEAT);
        contentListIDs.add(list);
        // /////////////////////////////////////////
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder_add);
        list.add(TV_MENU_PVR_MANUAL_REMINDER_START);
        contentListIDs.add(list);
    }
}
