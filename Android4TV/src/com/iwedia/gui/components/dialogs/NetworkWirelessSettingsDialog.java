package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.view.View;

import com.iwedia.comm.enums.NetworkType;
import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVLoginDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Wireless network settings dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkWirelessSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_WIRELESS_SETTINGS_FIND_AP = 4440,
            TV_MENU_NETWORK_WIRELESS_SETTINGS_MANUAL_ADD_AP = 4441,
            TV_MENU_NETWORK_WIRELESS_SETTINGS_WPS_CONFIG = 4442,
            TV_MENU_NETWORK_WIRELESS_SETTINGS_TURN_ON_OFF = 4445;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton btnFindAP, btnManualAddAP, btnWPSConfig;
    private A4TVButtonSwitch btnOnOff;
    private INetworkSettings networkSettings;
    private A4TVLoginDialog loginDialog;
    private A4TVEditText editText1, editText2;
    private Context ctx;

    public NetworkWirelessSettingsDialog(Context context) {
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

    /** Init views in dialog */
    private void init() {
        btnFindAP = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_SETTINGS_FIND_AP);
        btnManualAddAP = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_SETTINGS_MANUAL_ADD_AP);
        btnWPSConfig = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_SETTINGS_WPS_CONFIG);
        btnOnOff = (A4TVButtonSwitch) findViewById(TV_MENU_NETWORK_WIRELESS_SETTINGS_TURN_ON_OFF);
        loginDialog = new A4TVLoginDialog(ctx, true);
        loginDialog.setCancelable(false);
        loginDialog.setNegativeButton(R.string.button_text_cancel,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginDialog.cancel();
                    }
                });
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        NetworkSettingsDialog netSettDialog = MainActivity.activity
                .getDialogManager().getNetworkSettingsDialog();
        if (netSettDialog != null) {
            netSettDialog.fillViews();
        }
        super.onBackPressed();
    }

    private void fillViews() {
        networkSettings = null;
        try {
            networkSettings = MainActivity.service.getSystemControl()
                    .getNetworkControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (networkSettings != null) {
            int networkType = NetworkType.UNDEFINED;
            try {
                networkType = networkSettings.getActiveNetworkType();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (networkType == NetworkType.WIRELESS) {
                btnOnOff.setSelectedStateAndText(true, R.string.button_text_yes);
                btnFindAP.setClickable(true);
                btnManualAddAP.setClickable(true);
                btnWPSConfig.setClickable(true);
            } else {
                btnOnOff.setSelectedStateAndText(false, R.string.button_text_no);
                btnFindAP.setClickable(false);
                btnManualAddAP.setClickable(false);
                btnWPSConfig.setClickable(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_NETWORK_WIRELESS_SETTINGS_TURN_ON_OFF: {
                if (v.isSelected()) {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                            R.string.button_text_no);
                    btnFindAP.setClickable(false);
                    btnManualAddAP.setClickable(false);
                    btnWPSConfig.setClickable(false);
                    try {
                        if (networkSettings != null)
                            networkSettings
                                    .setActiveNetworkType(NetworkType.ETHERNET);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                            R.string.button_text_yes);
                    networkSettings = null;
                    try {
                        networkSettings = MainActivity.service
                                .getSystemControl().getNetworkControl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (networkSettings != null)
                            networkSettings
                                    .setActiveNetworkType(NetworkType.WIRELESS);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    btnFindAP.setClickable(true);
                    btnManualAddAP.setClickable(true);
                    btnWPSConfig.setClickable(true);
                }
                break;
            }
            case TV_MENU_NETWORK_WIRELESS_SETTINGS_FIND_AP: {
                NetworkWirelessFindAPDialog netWirelessFindApDialog = MainActivity.activity
                        .getDialogManager().getNetworkWirelessFindAPDialog();
                if (netWirelessFindApDialog != null) {
                    netWirelessFindApDialog.show();
                }
                break;
            }
            case TV_MENU_NETWORK_WIRELESS_SETTINGS_MANUAL_ADD_AP: {
                NetworkWirelessAddHiddenNetworkDialog netWirelessAddHiddenDialog = MainActivity.activity
                        .getDialogManager()
                        .getNetworkWirelessAddHiddenNetworkDialog();
                if (netWirelessAddHiddenDialog != null) {
                    netWirelessAddHiddenDialog.show();
                }
                break;
            }
            case TV_MENU_NETWORK_WIRELESS_SETTINGS_WPS_CONFIG: {
                NetworkWirelessWPSConfigDialog netWirelessWPSDialog = MainActivity.activity
                        .getDialogManager().getNetworkWirelessWPSConfigDialog();
                if (netWirelessWPSDialog != null) {
                    netWirelessWPSDialog.show();
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
        titleIDs.add(R.string.tv_menu_network_wireless_settings);
        // no operation off******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_settings_wireless);
        list.add(TV_MENU_NETWORK_WIRELESS_SETTINGS_TURN_ON_OFF);
        contentListIDs.add(list);
        // network type******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_settings_networks);
        list.add(TV_MENU_NETWORK_WIRELESS_SETTINGS_FIND_AP);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_settings_add_wireless_network);
        list.add(TV_MENU_NETWORK_WIRELESS_SETTINGS_MANUAL_ADD_AP);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_wps_config);
        list.add(TV_MENU_NETWORK_WIRELESS_SETTINGS_WPS_CONFIG);
        contentListIDs.add(list);
    }
}
