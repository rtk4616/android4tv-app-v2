package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVPasswordDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Store mode settings dialog
 * 
 * @author Mladen Ilic
 */
public class StoreModeSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_STORE_MODE_START = 2555,
            TV_MENU_STORE_MODE_LAUNCH_VIDEO_PRESENTATION = 2556,
            FIRST_TIME_INSTALL_NEXT_BUTTON = 2557;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButtonSwitch buttonStart, buttonLaunchVideo;
    private A4TVButton buttonFirstInstallNext;
    private A4TVPasswordDialog alert;
    private A4TVEditText editText1;
    private Context ctx;
    private int storeModeExitPassword = 1234;

    public StoreModeSettingsDialog(Context context) {
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

    private boolean checkPassword(int password) {
        if (password == storeModeExitPassword) {
            return true;
        } else {
            return false;
        }
    }

    /** Get references from views and set look and feel */
    private void init() {
        buttonStart = (A4TVButtonSwitch) findViewById(TV_MENU_STORE_MODE_START);
        buttonLaunchVideo = (A4TVButtonSwitch) findViewById(TV_MENU_STORE_MODE_LAUNCH_VIDEO_PRESENTATION);
        buttonLaunchVideo.setClickable(false);
        alert = new A4TVPasswordDialog(ctx, true);
        alert.setCancelable(true);
        editText1 = alert.getEditText1();
        alert.getEditText2().setVisibility(View.GONE);
        alert.getEditText3().setVisibility(View.GONE);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        if (MainActivity.isInFirstTimeInstall) {
            NetworkSettingsDialog netSettDialog = MainActivity.activity
                    .getDialogManager().getNetworkSettingsDialog();
            if (netSettDialog != null) {
                netSettDialog.show();
            }
        }
        super.onBackPressed();
    }

    /** Get informations from service and display it */
    private void setViews() {
        boolean isEnabled;
        /* Get stored store mode start, default false */
        isEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.STORE_MODE_START, false);
        buttonStart.setSelectedStateAndText(isEnabled,
                isEnabled ? R.string.button_text_on : R.string.button_text_off);
        if (isEnabled) {
            buttonLaunchVideo.setClickable(true);
        }
        /* Get stored store mode video presentation enabled, default true */
        isEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.STORE_MODE_VIDEO_PRESENTATION, true);
        buttonLaunchVideo.setSelectedStateAndText(isEnabled,
                isEnabled ? R.string.button_text_on : R.string.button_text_off);
        // ///////////////////////////////////
        // Normal mode
        // ///////////////////////////////////
        if (!MainActivity.isInFirstTimeInstall) {
            findViewById(R.string.first_time_install_next).setVisibility(
                    View.GONE);
            if (ConfigHandler.TV_FEATURES) {
                findViewById(DialogCreatorClass.LINES_BASE_ID).setVisibility(
                        View.GONE);
            } else {
                findViewById(DialogCreatorClass.LINES_BASE_ID).setVisibility(
                        View.GONE);
            }
        }
        // ///////////////////////////////////
        // First time install mode
        // ///////////////////////////////////
        else {
            findViewById(R.string.first_time_install_next).setVisibility(
                    View.VISIBLE);
            if (ConfigHandler.TV_FEATURES) {
                findViewById(DialogCreatorClass.LINES_BASE_ID).setVisibility(
                        View.VISIBLE);
            } else {
                findViewById(DialogCreatorClass.LINES_BASE_ID).setVisibility(
                        View.VISIBLE);
            }
            buttonFirstInstallNext = (A4TVButton) findViewById(FIRST_TIME_INSTALL_NEXT_BUTTON);
            buttonFirstInstallNext
                    .setText(R.string.first_time_install_next_button_text);
            if (MainActivity.activity.getFirstTimeInfoText() != null) {
                MainActivity.activity.getFirstTimeInfoText().setText(
                        R.string.first_time_install_setup_network);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case FIRST_TIME_INSTALL_NEXT_BUTTON: {
                ChannelInstallationDialog chDialog = MainActivity.activity
                        .getDialogManager().getChannelInstallationDialog();
                if (chDialog != null) {
                    chDialog.show();
                }
                StoreModeSettingsDialog.this.cancel();
                break;
            }
            case TV_MENU_STORE_MODE_START: {
                /* exit store mode */
                if (v.isSelected()) {
                    /*
                     * ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                     * R.string.button_text_off);
                     * buttonLaunchVideo.setClickable(false);
                     */
                    PasswordSecurityDialog.wrongPasswordEntered(null, false);
                    if (PasswordSecurityDialog.waitFor10Minutes) {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast(R.string.enter_password_no_more_attempts_active);
                    } else {
                        /* enter password to exit store mode */
                        editText1.setText("");
                        alert.show();
                        editText1.requestFocus();
                        alert.setNegativeButton(R.string.button_text_no,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.cancel();
                                    }
                                });
                        alert.setPositiveButton(R.string.button_text_yes,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String password = editText1.getText()
                                                .toString();
                                        boolean valid = false;
                                        try {
                                            if (password.length() == 4) {
                                                valid = checkPassword(Integer
                                                        .valueOf(password));
                                            }
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                        if (valid) {
                                            try {
                                                buttonStart
                                                        .setSelectedStateAndText(
                                                                false,
                                                                R.string.button_text_off);
                                                buttonLaunchVideo
                                                        .setClickable(false);
                                                /* Save selection */
                                                MainActivity.sharedPrefs
                                                        .edit()
                                                        .putBoolean(
                                                                MainActivity.STORE_MODE_START,
                                                                false).commit();
                                                MainActivity.activity
                                                        .getScreenSaverDialog()
                                                        .updateScreensaverTimer();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            alert.cancel();
                                        } else {
                                            /* Wrong password */
                                            editText1.setText("");
                                            A4TVToast toast = new A4TVToast(ctx);
                                            toast.showToast(R.string.wrong_pin_entered);
                                            editText1.requestFocus();
                                            PasswordSecurityDialog
                                                    .wrongPasswordEntered(
                                                            alert, false);
                                        }
                                    }
                                });
                    }
                } else {
                    /* enter store mode */
                    PasswordSecurityDialog.wrongPasswordEntered(null, false);
                    if (PasswordSecurityDialog.waitFor10Minutes) {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast(R.string.enter_password_no_more_attempts_active);
                    } else {
                        /* enter password to enter store mode */
                        editText1.setText("");
                        alert.show();
                        editText1.requestFocus();
                        alert.setNegativeButton(R.string.button_text_no,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.cancel();
                                    }
                                });
                        alert.setPositiveButton(R.string.button_text_yes,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String password = editText1.getText()
                                                .toString();
                                        boolean valid = false;
                                        try {
                                            if (password.length() == 4) {
                                                valid = checkPassword(Integer
                                                        .valueOf(password));
                                            }
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                        if (valid) {
                                            try {
                                                buttonStart
                                                        .setSelectedStateAndText(
                                                                true,
                                                                R.string.button_text_on);
                                                buttonLaunchVideo
                                                        .setClickable(true);
                                                /* Save selection */
                                                MainActivity.sharedPrefs
                                                        .edit()
                                                        .putBoolean(
                                                                MainActivity.STORE_MODE_START,
                                                                true).commit();
                                                MainActivity.activity
                                                        .getScreenSaverDialog()
                                                        .updateScreensaverTimer();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            alert.cancel();
                                        } else {
                                            /* Wrong password */
                                            editText1.setText("");
                                            A4TVToast toast = new A4TVToast(ctx);
                                            toast.showToast(R.string.wrong_pin_entered);
                                            editText1.requestFocus();
                                            PasswordSecurityDialog
                                                    .wrongPasswordEntered(
                                                            alert, false);
                                        }
                                    }
                                });
                    }
                }
                break;
            }
            case TV_MENU_STORE_MODE_LAUNCH_VIDEO_PRESENTATION: {
                if (v.isSelected()) {
                    buttonLaunchVideo.setSelectedStateAndText(false,
                            R.string.button_text_off);
                } else {
                    buttonLaunchVideo.setSelectedStateAndText(true,
                            R.string.button_text_on);
                }
                /* Save selection */
                MainActivity.sharedPrefs
                        .edit()
                        .putBoolean(MainActivity.STORE_MODE_VIDEO_PRESENTATION,
                                v.isSelected()).commit();
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
        titleIDs.add(R.string.tv_menu_store_mode_settings);
        ArrayList<Integer> list;
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_store_mode_start);
        list.add(TV_MENU_STORE_MODE_START);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_store_mode_launch_video_presentation);
        list.add(TV_MENU_STORE_MODE_LAUNCH_VIDEO_PRESENTATION);
        contentListIDs.add(list);
        // first time install next ***********************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.first_time_install_next);
        list.add(FIRST_TIME_INSTALL_NEXT_BUTTON);
        contentListIDs.add(list);
    }
}
