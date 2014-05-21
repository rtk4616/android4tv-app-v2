package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.view.View;

import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Wired network information dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkAdvancedSoftAPDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for text box */
    public static final int TV_MENU_NETWORK_ADVANCED_SOFT_AP_SSID = 26,
            TV_MENU_NETWORK_ADVANCED_SOFT_AP_PASSWORD = 28;
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_ADVANCED_SOFT_AP_START = 31;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private String SSID;
    private String Password;
    private Boolean SoftAPState;
    private A4TVEditText editSSID;
    private A4TVEditText editPassword;

    public NetworkAdvancedSoftAPDialog(Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        init();
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
    }

    /** Get references from views and set look and feel */
    private void init() {
        editSSID = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_SSID);
        editPassword = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_PASSWORD);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onBackPressed() {
        // clearEditTexts();
        super.onBackPressed();
    }

    private void clearEditTexts() {
        A4TVEditText editSSID = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_SSID);
        A4TVEditText editPassword = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_PASSWORD);
        editSSID.setText("");
        editPassword.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_NETWORK_ADVANCED_SOFT_AP_START: {
                INetworkSettings networkSettings;
                try {
                    networkSettings = MainActivity.service.getSystemControl()
                            .getNetworkControl();
                    SoftAPState = networkSettings.getSoftAPState();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (!SoftAPState) {
                    A4TVEditText editSSID = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_SSID);
                    A4TVEditText editPassword = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_PASSWORD);
                    SSID = "" + editSSID.getText();
                    Password = "" + editPassword.getText();
                    try {
                        networkSettings = MainActivity.service
                                .getSystemControl().getNetworkControl();
                        networkSettings.startSoftAP(SSID, Password, true);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    A4TVButton startAP = (A4TVButton) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_START);
                    startAP.setText("Stop");
                } else {
                    A4TVEditText editSSID = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_SSID);
                    A4TVEditText editPassword = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_PASSWORD);
                    try {
                        networkSettings = MainActivity.service
                                .getSystemControl().getNetworkControl();
                        networkSettings.startSoftAP(null, null, false);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    A4TVButton startAP = (A4TVButton) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_START);
                    startAP.setText("Start");
                }
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
        INetworkSettings networkSettings;
        try {
            networkSettings = MainActivity.service.getSystemControl()
                    .getNetworkControl();
            SoftAPState = networkSettings.getSoftAPState();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (SoftAPState) {
            editSSID.setText(SSID);
            editPassword.setText(Password);
            A4TVButton startAP = (A4TVButton) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_START);
            startAP.setText("Stop");
        } else {
            clearEditTexts();
            A4TVButton startAP = (A4TVButton) findViewById(TV_MENU_NETWORK_ADVANCED_SOFT_AP_START);
            startAP.setText("Start");
        }
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
        titleIDs.add(R.string.tv_menu_network_advanced_soft_ap_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_advanced_soft_ap_ssid);
        list.add(TV_MENU_NETWORK_ADVANCED_SOFT_AP_SSID);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_advanced_soft_ap_password);
        list.add(TV_MENU_NETWORK_ADVANCED_SOFT_AP_PASSWORD);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.button_text_configure);
        list.add(TV_MENU_NETWORK_ADVANCED_SOFT_AP_START);
        contentListIDs.add(list);
    }
}
