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
 * Applications dialog
 * 
 * @author Branimir Pavlovic
 */
public class ApplicationsManageDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons in this dialog */
    public static final int TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPLICATIONS = 17;
    /** IDs for buttons in this dialog */
    public static final int TV_MENU_APPLICATIONS_SETTINGS_RUNNING_SERVICES = 313;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    private A4TVButton manageApps, runningServices;

    public ApplicationsManageDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
    }

    @Override
    public void show() {
        fillInitialViews();
        manageApps.performClick();
        // super.show();
    }

    /** Populate views in dialog */
    private void fillInitialViews() {
        /****************************** MANAGE APPLICATIONS *************************************/
        manageApps = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPLICATIONS);
        manageApps.setText(R.string.tv_menu_applications_settings_manage);
        /****************************** RUNNING SERVICES *************************************/
        runningServices = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_RUNNING_SERVICES);
        runningServices.setText(R.string.tv_menu_applications_settings_running);
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
    public void onClick(View v) {
        switch (v.getId()) {
        /** Manage applications click */
            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPLICATIONS: {
                ApplicationsManageManageAppsDialog appmDiaolog = MainActivity.activity
                        .getDialogManager()
                        .getApplicationsManageManageAppsDialog();
                if (appmDiaolog != null) {
                    appmDiaolog.show();
                }
                break;
            }
            /** Running services click */
            case TV_MENU_APPLICATIONS_SETTINGS_RUNNING_SERVICES: {
                ApplicationsManageRunningServicesDialog appmrDialog = MainActivity.activity
                        .getDialogManager()
                        .getApplicationsManageRunningServicesDialog();
                if (appmrDialog != null) {
                    appmrDialog.show();
                }
                break;
            }
            default:
                break;
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
        titleIDs.add(R.string.tv_menu_applications_settings);
        // unknown sources******************************************
        // ArrayList<Integer> list = new ArrayList<Integer>();
        // list.add(MainMenuContent.TAGA4TVTextView);
        // list.add(MainMenuContent.TAGA4TVButtonSwitch);
        // contentList.add(list);
        //
        // list = new ArrayList<Integer>();
        // list.add(R.string.tv_menu_applications_settings_unknown_sources);
        // list.add(TV_MENU_APPLICATIONS_SETTINGS_UNKNOWN_SOURCES);
        // contentListIDs.add(list);
        // manage applications******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPLICATIONS);
        contentListIDs.add(list);
        // running services ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_running_services);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_RUNNING_SERVICES);
        contentListIDs.add(list);
    }
}
