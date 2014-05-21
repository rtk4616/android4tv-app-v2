package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.comm.enums.NetworkType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Network settings dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for spinners */
    public static final int TV_MENU_NETWORK_SETTINGS_NETWORK_TYPE = 9;
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_SETTINGS_WIRELESS_SETTINGS = 37,
            TV_MENU_NETWORK_SETTINGS_ADVANCED_SETTINGS = 38,
            TV_MENU_NETWORK_SETTINGS_NETWORK_INFORMATION = 39,
            TV_MENU_NETWORK_SETTINGS_NETWORK_TEST = 40,
            FIRST_TIME_INSTALL_NEXT_BUTTON = 10001;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonFirstInstallNext;
    private A4TVSpinner spinnerNetworkType;

    public NetworkSettingsDialog(Context context) {
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

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        if (MainActivity.isInFirstTimeInstall) {
            PictureSettingsDialog picSettingsDialog = MainActivity.activity
                    .getDialogManager().getPictureSettingsDialog();
            if (picSettingsDialog != null) {
                picSettingsDialog.show();
            }
        }
        super.onBackPressed();
    }

    public void fillViews() {
        // ///////////////////////////////////
        // Normal mode
        // ///////////////////////////////////
        if (!MainActivity.isInFirstTimeInstall) {
            findViewById(R.string.first_time_install_next).setVisibility(
                    View.GONE);
            if (ConfigHandler.TV_FEATURES) {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 4)
                        .setVisibility(View.GONE);
            } else {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 3)
                        .setVisibility(View.GONE);
            }
        }
        // ///////////////////////////////////
        // First time install mode
        // ///////////////////////////////////
        else {
            findViewById(R.string.first_time_install_next).setVisibility(
                    View.VISIBLE);
            if (ConfigHandler.TV_FEATURES) {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 3)
                        .setVisibility(View.VISIBLE);
            } else {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 2)
                        .setVisibility(View.VISIBLE);
            }
            buttonFirstInstallNext = (A4TVButton) findViewById(FIRST_TIME_INSTALL_NEXT_BUTTON);
            buttonFirstInstallNext
                    .setText(R.string.first_time_install_next_button_text);
            if (MainActivity.activity.getFirstTimeInfoText() != null) {
                MainActivity.activity.getFirstTimeInfoText().setText(
                        R.string.first_time_install_setup_network);
            }
        }
        /******************* SPINNER NETWORK TYPE *********************/
        int networkType = NetworkType.ETHERNET;
        try {
            networkType = MainActivity.service.getSystemControl()
                    .getNetworkControl().getActiveNetworkType();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (networkType == NetworkType.WIRELESS) {
            spinnerNetworkType.setSelection(1);
        } else {
            spinnerNetworkType.setSelection(0);
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

    private void init() {
        spinnerNetworkType = (A4TVSpinner) findViewById(TV_MENU_NETWORK_SETTINGS_NETWORK_TYPE);
        if (!ConfigHandler.TV_FEATURES) {
            findViewById(R.string.tv_menu_network_wired_test).setVisibility(
                    View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 4).setVisibility(
                    View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case FIRST_TIME_INSTALL_NEXT_BUTTON: {
                StoreModeSettingsDialog sMSettingsDialog = MainActivity.activity
                        .getDialogManager().getStoreModeSettingsDialog();
                if (sMSettingsDialog != null) {
                    sMSettingsDialog.show();
                }
                NetworkSettingsDialog.this.cancel();
                break;
            }
            case TV_MENU_NETWORK_SETTINGS_ADVANCED_SETTINGS: {
                try {
                    NetworkAdvancedSettingsDialog netSettingsDialog = MainActivity.activity
                            .getDialogManager()
                            .getNetworkAdvancedSettingsDialog();
                    if (netSettingsDialog != null) {
                        netSettingsDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_NETWORK_SETTINGS_WIRELESS_SETTINGS: {
                NetworkWirelessSettingsDialog netWirelessSettDialog = MainActivity.activity
                        .getDialogManager().getNetworkWirelessSettingsDialog();
                if (netWirelessSettDialog != null) {
                    netWirelessSettDialog.show();
                }
                break;
            }
            case TV_MENU_NETWORK_SETTINGS_NETWORK_INFORMATION: {
                if (spinnerNetworkType.getCHOOSEN_ITEM_INDEX() == 0) {
                    NetworkWiredInformationDialog netWiredInfoDialog = MainActivity.activity
                            .getDialogManager()
                            .getNetworkWiredInformationDialog();
                    if (netWiredInfoDialog != null) {
                        netWiredInfoDialog.show();
                    }
                } else {
                    NetworkWirelessInformationDialog netWirelessInfoDialog = MainActivity.activity
                            .getDialogManager()
                            .getNetworkWirelessInformationDialog();
                    if (netWirelessInfoDialog != null) {
                        netWirelessInfoDialog.show();
                    }
                }
                break;
            }
            case TV_MENU_NETWORK_SETTINGS_NETWORK_TEST: {
                MainActivity.activity.getDialogManager().getNetworkTestDialog()
                        .show();
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
        titleIDs.add(R.string.tv_menu_network_settings);
        // network type******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_settings_network_type);
        list.add(TV_MENU_NETWORK_SETTINGS_NETWORK_TYPE);
        contentListIDs.add(list);
        // wireless settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_settings_wireless_settings);
        list.add(TV_MENU_NETWORK_SETTINGS_WIRELESS_SETTINGS);
        contentListIDs.add(list);
        // wired settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_settings_advanced_settings);
        list.add(TV_MENU_NETWORK_SETTINGS_ADVANCED_SETTINGS);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info);
        list.add(TV_MENU_NETWORK_SETTINGS_NETWORK_INFORMATION);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_test);
        list.add(TV_MENU_NETWORK_SETTINGS_NETWORK_TEST);
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
