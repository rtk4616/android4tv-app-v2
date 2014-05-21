package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Pvr settings dialog
 * 
 * @author Sasa Jagodin
 */
public class PVRMenuDialog extends A4TVDialog implements A4TVDialogInterface,
        android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_PVR_MENU_MANUAL_REMINDER = 1,
            TV_MENU_PVR_MENU_MANUAL_SCHEDULE = 2,
            TV_MENU_PVR_MENU_SETTINGS = 3;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonManualReminder, buttonManualSchedule,
            buttonSettings;

    public PVRMenuDialog(Context context) {
        super(context, checkTheme(context), 0);
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
        buttonManualReminder = (A4TVButton) findViewById(TV_MENU_PVR_MENU_MANUAL_REMINDER);
        buttonManualSchedule = (A4TVButton) findViewById(TV_MENU_PVR_MENU_MANUAL_SCHEDULE);
        buttonSettings = (A4TVButton) findViewById(TV_MENU_PVR_MENU_SETTINGS);
        buttonManualReminder.setText(R.string.button_text_view);
        buttonManualSchedule.setText(R.string.button_text_view);
        buttonSettings.setText(R.string.button_text_view);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_PVR_MENU_MANUAL_REMINDER: {
                PVRManualEventReminderDialog pvrManualEventRemDialog = MainActivity.activity
                        .getDialogManager().getPVRManualEventReminderDialog();
                if (pvrManualEventRemDialog != null) {
                    pvrManualEventRemDialog.show();
                }
                break;
            }
            case TV_MENU_PVR_MENU_MANUAL_SCHEDULE: {
                PVRManualScheduleDialog pvrManualScheduleDialog = MainActivity.activity
                        .getDialogManager().getPVRManualScheduleDialog();
                if (pvrManualScheduleDialog != null) {
                    pvrManualScheduleDialog.show();
                }
                break;
            }
            case TV_MENU_PVR_MENU_SETTINGS: {
                PVRSettingsDialog pvrSettingsDialog = MainActivity.activity
                        .getDialogManager().getPVRSettingsDialog();
                if (pvrSettingsDialog != null) {
                    pvrSettingsDialog.show();
                }
                break;
            }
            default:
                break;
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
        titleIDs.add(R.string.tv_menu_pvr_menu);
        ArrayList<Integer> list;
        // manual reminder menu******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_reminder);
        list.add(TV_MENU_PVR_MENU_MANUAL_REMINDER);
        contentListIDs.add(list);
        // manual schedule menu******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_manual_schedule);
        list.add(TV_MENU_PVR_MENU_MANUAL_SCHEDULE);
        contentListIDs.add(list);
        // pvr menu******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_settings);
        list.add(TV_MENU_PVR_MENU_SETTINGS);
        contentListIDs.add(list);
    }
}
