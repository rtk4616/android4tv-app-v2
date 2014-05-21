package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.system.date_time.IDateTimeSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.pvr.A4TVStorageManager;
import com.iwedia.gui.util.DateTimeConversions;

import java.util.ArrayList;
import java.util.Date;

/**
 * Timers settings dialog
 * 
 * @author Mladen Ilic
 */
public class TimersSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    public static final String TAG = "TimersSettingsDialog";
    /** IDs for buttons */
    public static final int TV_MENU_TIMERS_SETTINGS_OFF_TIMERS = 850,
            TV_MENU_TIMERS_SETTINGS_GET_TIME = 851,
            TV_MENU_TIMERS_SETTINGS_NO_SIGNAL = 852,
            TV_MENU_TIMERS_SETTINGS_ON_TIMERS = 853,
            TV_MENU_TIMERS_SETTINGS_NO_OPERATION = 854,
            TV_MENU_TIMERS_SETTINGS_SET_DEFAULT_SETTINGS = 855;
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
    private A4TVButtonSwitch btnNoSignal, btnNoOperation;
    private A4TVButton btnGetTime, btnOffTimer, btnOnTimer,
            buttonSetDefaultSettings;
    private A4TVStorageManager storage = new A4TVStorageManager();
    private Thread backgroundThread;
    private Runnable run;
    private Handler handler;

    public TimersSettingsDialog(Context context) {
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
        btnGetTime = (A4TVButton) findViewById(TV_MENU_TIMERS_SETTINGS_GET_TIME);
        btnNoSignal = (A4TVButtonSwitch) findViewById(TV_MENU_TIMERS_SETTINGS_NO_SIGNAL);
        btnNoOperation = (A4TVButtonSwitch) findViewById(TV_MENU_TIMERS_SETTINGS_NO_OPERATION);
        btnOffTimer = (A4TVButton) findViewById(TV_MENU_TIMERS_SETTINGS_OFF_TIMERS);
        btnOnTimer = (A4TVButton) findViewById(TV_MENU_TIMERS_SETTINGS_ON_TIMERS);
        buttonSetDefaultSettings = (A4TVButton) findViewById(TV_MENU_TIMERS_SETTINGS_SET_DEFAULT_SETTINGS);
        buttonSetDefaultSettings.setText(R.string.button_text_ok);
        disableBtn(btnGetTime);
        disableBtn(btnOnTimer);
        fillViewsWithData(true);
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

    /** Disable clicks on buttons and clear background */
    private void disableBtn(A4TVButton btn) {
        btn.setClickable(false);
        btn.setBackgroundColor(Color.TRANSPARENT);
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
        boolean noSignal = false;
        boolean noOperation = false;
        try {
            noSignal = MainActivity.service.getSetupControl().getNoSignalOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            noOperation = MainActivity.service.getSetupControl()
                    .getNoOperationOff();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (refreshAll) {
            /**************************************************************/
            /** No signal and no operation timers update */
            if (noSignal) {
                btnNoSignal.setSelectedStateAndText(true,
                        R.string.button_text_yes);
            } else {
                btnNoSignal.setSelectedStateAndText(false,
                        R.string.button_text_no);
            }
            /**************************************************************/
        }
        if (refreshAll) {
            /**************************************************************/
            if (noOperation) {
                btnNoOperation.setSelectedStateAndText(true,
                        R.string.button_text_yes);
            } else {
                btnNoOperation.setSelectedStateAndText(false,
                        R.string.button_text_no);
            }
            /**************************************************************/
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
                btnGetTime.setText(DateTimeConversions.getTimeSting(date));
                TimersSettingsDialog.this.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /**************************************************************/
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
            case TV_MENU_TIMERS_SETTINGS_NO_SIGNAL: {
                if (v.isSelected()) {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                            R.string.button_text_no);
                } else {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                            R.string.button_text_yes);
                }
                try {
                    MainActivity.service.getSetupControl().setNoSignalOff(
                            v.isSelected());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fillViewsWithData(true);
                break;
            }
            case TV_MENU_TIMERS_SETTINGS_NO_OPERATION: {
                if (v.isSelected()) {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                            R.string.button_text_no);
                } else {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                            R.string.button_text_yes);
                }
                try {
                    MainActivity.service.getSetupControl().setNoOperationOff(
                            v.isSelected());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fillViewsWithData(true);
                break;
            }
            case TV_MENU_TIMERS_SETTINGS_OFF_TIMERS: {
                OffTimersSettingsDialog offTimersSettDialog = MainActivity.activity
                        .getDialogManager().getOffTimersSettingsDialog();
                if (offTimersSettDialog != null) {
                    offTimersSettDialog.show();
                }
                break;
            }
            case TV_MENU_TIMERS_SETTINGS_ON_TIMERS: {
                break;
            }
            case TV_MENU_TIMERS_SETTINGS_GET_TIME: {
                break;
            }
            case TV_MENU_TIMERS_SETTINGS_SET_DEFAULT_SETTINGS: {
                try {
                    MainActivity.service.getSetupControl()
                            .resetTimersSettings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.show();
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
        Log.d(TAG, "start thread entered");
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
        Log.d(TAG, "stop thread entered");
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
        titleIDs.add(R.string.tv_menu_timers_settings);
        // clock******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_timers_settings_time);
        list.add(TV_MENU_TIMERS_SETTINGS_GET_TIME);
        contentListIDs.add(list);
        // off timers settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_timers_settings_off_timers);
        list.add(TV_MENU_TIMERS_SETTINGS_OFF_TIMERS);
        contentListIDs.add(list);
        // on timers settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_timers_settings_on_timers);
        list.add(TV_MENU_TIMERS_SETTINGS_ON_TIMERS);
        contentListIDs.add(list);
        // no signal off******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_timers_settings_no_signal);
        list.add(TV_MENU_TIMERS_SETTINGS_NO_SIGNAL);
        contentListIDs.add(list);
        // no operation off******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_timers_settings_no_operation);
        list.add(TV_MENU_TIMERS_SETTINGS_NO_OPERATION);
        contentListIDs.add(list);
        // set default settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_sound_settings_set_default_settings);
        list.add(TV_MENU_TIMERS_SETTINGS_SET_DEFAULT_SETTINGS);
        contentListIDs.add(list);
    }
}
