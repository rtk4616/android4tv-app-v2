package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.iwedia.dtv.types.TimeDate;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Off timers settings dialog
 * 
 * @author Mladen Ilic
 */
public class OffTimersSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    public static final String TAG = "OffTimersSettingsDialog";
    /** IDs for buttons */
    public static final int TV_MENU_OFFTIMERS_SETTINGS_ADD_TIMERS = 861,
            TV_MENU_OFFTIMERS_SETTINGS_DELETE_TIMERS = 862;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    // views from dialogs
    private A4TVButton btnAddTimer, btnDeleteTimer;
    private Thread backgroundThread;
    private Runnable run;

    public OffTimersSettingsDialog(Context context) {
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
        btnAddTimer = (A4TVButton) findViewById(TV_MENU_OFFTIMERS_SETTINGS_ADD_TIMERS);
        btnDeleteTimer = (A4TVButton) findViewById(TV_MENU_OFFTIMERS_SETTINGS_DELETE_TIMERS);
    }

    /**
     * Fill views with data from service
     */
    public void fillViewsWithData(boolean refreshAll) {
        // if service returns real object
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
            case TV_MENU_OFFTIMERS_SETTINGS_ADD_TIMERS: {
                OffTimersAddDialog offTimersAddDialog = MainActivity.activity
                        .getDialogManager().getOffTimersAddDialog();
                if (offTimersAddDialog != null) {
                    offTimersAddDialog.show();
                }
                break;
            }
            case TV_MENU_OFFTIMERS_SETTINGS_DELETE_TIMERS: {
                TimeDate addTime = new TimeDate();
                addTime.setHour(0);
                addTime.setMin(0);
                boolean retValueTimer = false, retValueTimerRepeat = false;
                try {
                    retValueTimer = MainActivity.service.getSetupControl()
                            .setOffTimer(addTime);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    retValueTimerRepeat = MainActivity.service
                            .getSetupControl().endOffTimer();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (retValueTimer) {
                    if (retValueTimerRepeat) {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast(R.string.tv_menu_timers_settings_delete_toast);
                    } else {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast(R.string.tv_menu_timers_settings_delete_toast_failed);
                    }
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
    public synchronized void startThread(Runnable run) {
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
    public synchronized void stopThread() {
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
        titleIDs.add(R.string.tv_menu_off_timers_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_off_timers_add);
        list.add(TV_MENU_OFFTIMERS_SETTINGS_ADD_TIMERS);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_off_timers_delete);
        list.add(TV_MENU_OFFTIMERS_SETTINGS_DELETE_TIMERS);
        contentListIDs.add(list);
    }
}
