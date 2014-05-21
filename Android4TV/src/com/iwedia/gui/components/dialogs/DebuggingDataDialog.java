package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.View;

import com.iwedia.comm.IServiceMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.graphics.PatternView;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Product info dialog
 * 
 * @author Sasa Jagodin
 */
public class DebuggingDataDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    Bitmap bitmap = Bitmap.createBitmap(500, 500, Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint();
    private static PatternView patternView;
    /** IDs for buttons */
    public static final int TV_MENU_DEBUGGING_DATA_MAIN_VERSION = 1,
            TV_MENU_DEBUGGING_DATA_HARDWARE_VERSION = 2,
            TV_MENU_DEBUGGING_DATA_STANDBY_CAUSE_RESET = 3,
            TV_MENU_DEBUGGING_DATA_PATTERN = 4,
            TV_MENU_DEBUGGING_DATA_NORMAL_STANDBY_CAUSE = 5;
    /** ID for spinner */
    // public static final int TV_MENU_DEBUGGING_DATA_NORMAL_STANDBY_CAUSE =
    // 467;
    private A4TVButton mainVersion, hardwareVersion, standbyCauseReset,
            pattern, normalStandbyCause;
    // private A4TVSpinner spinner;
    private IServiceMode serviceMode;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();

    public DebuggingDataDialog(Context context) {
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
        mainVersion = (A4TVButton) findViewById(TV_MENU_DEBUGGING_DATA_MAIN_VERSION);
        hardwareVersion = (A4TVButton) findViewById(TV_MENU_DEBUGGING_DATA_HARDWARE_VERSION);
        standbyCauseReset = (A4TVButton) findViewById(TV_MENU_DEBUGGING_DATA_STANDBY_CAUSE_RESET);
        pattern = (A4TVButton) findViewById(TV_MENU_DEBUGGING_DATA_PATTERN);
        // normalStandbyCause = (A4TVButton)
        // findViewById(TV_MENU_DEBUGGING_DATA_NORMAL_STANDBY_CAUSE);
        /** Set desired states to buttons */
        disableBtn(mainVersion);
        disableBtn(hardwareVersion);
        // disableBtn(pattern);
        // disableBtn(standbyCause);
        // disableBtn(normalStandbyCause);
    }

    /** Disable clicks on buttons and clear background */
    private void disableBtn(A4TVButton btn) {
        btn.setClickable(false);
        btn.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
        standbyCauseReset.setText(R.string.button_text_ok);
        pattern.setText(R.string.button_text_ok);
        // spinner.setSelection(0);
        mainVersion.setText("Version 2");
        hardwareVersion.setText("H24 Rev D");
        /*
         * String standbyCause = ""; try { standbyCause =
         * serviceMode.getNormalStandbyCause(); } catch (Exception e) {
         * e.printStackTrace(); } normalStandbyCause.setText(standbyCause);
         */
        serviceMode = null;
        try {
            serviceMode = MainActivity.service.getServiceMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        // patternView = new PatternView(this.getContext());
        patternView = new PatternView(MainActivity.activity
                .getPrimaryVideoView().getContext());
        setContentView(patternView);
        getWindow().getAttributes().width = 1920;
        getWindow().getAttributes().height = 1080; /* Temporary */
        getWindow().setGravity(Gravity.CENTER);
        // MainActivity.activity.getPrimaryVideoView().
        switch (v.getId()) {
            case TV_MENU_DEBUGGING_DATA_PATTERN: {
                try {
                    patternView.invalidatePatternView();
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_DEBUGGING_DATA_STANDBY_CAUSE_RESET: {
                try {
                    serviceMode.resetStandbyCause();
                    // normalStandbyCause.setText("None");
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
        titleIDs.add(R.string.tv_menu_service_mode_menu_debugging_data);
        // main version******************************************
        ArrayList<Integer> list;
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_main_version);
        list.add(TV_MENU_DEBUGGING_DATA_MAIN_VERSION);
        contentListIDs.add(list);
        // hardware version******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_hardware_version);
        list.add(TV_MENU_DEBUGGING_DATA_HARDWARE_VERSION);
        contentListIDs.add(list);
        // normal standby cause******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_normal_standby_cause);
        list.add(TV_MENU_DEBUGGING_DATA_NORMAL_STANDBY_CAUSE);
        contentListIDs.add(list);
        // standby cause reset******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_standby_cause_reset);
        list.add(TV_MENU_DEBUGGING_DATA_STANDBY_CAUSE_RESET);
        contentListIDs.add(list);
        // pattern******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_pattern);
        list.add(TV_MENU_DEBUGGING_DATA_PATTERN);
        contentListIDs.add(list);
    }
}
