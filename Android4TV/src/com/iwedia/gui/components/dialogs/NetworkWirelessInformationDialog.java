package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.RemoteException;
import android.view.View;

import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.comm.system.WifiScanResult;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wireless network information dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkWirelessInformationDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_WIRELESS_LINK_SPEED = 26,
            TV_MENU_NETWORK_WIRELESS_INFORMATION_AP = 32,
            TV_MENU_NETWORK_WIRELESS_INFORMATION_SIGNAL_STRENGTH = 33,
            TV_MENU_NETWORK_WIRELESS_INFORMATION_ENCRYPTION_METHOD = 34;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonLinkSpeed, buttonAP, buttonSignalStrength,
            buttonEncMethod;
    private INetworkSettings networkSettings;

    public NetworkWirelessInformationDialog(Context context) {
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
        buttonLinkSpeed = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_LINK_SPEED);
        buttonAP = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_INFORMATION_AP);
        buttonEncMethod = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_INFORMATION_ENCRYPTION_METHOD);
        buttonSignalStrength = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_INFORMATION_SIGNAL_STRENGTH);
        /** Set desired states to buttons */
        disableBtn(buttonLinkSpeed);
        disableBtn(buttonAP);
        disableBtn(buttonEncMethod);
        disableBtn(buttonSignalStrength);
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
        WifiScanResult activeNetwork = null;
        networkSettings = null;
        try {
            networkSettings = MainActivity.service.getSystemControl()
                    .getNetworkControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (networkSettings != null) {
            try {
                activeNetwork = networkSettings.getActiveWirelessNetwork();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /********************************* ENCRYPTION METHOD ******************************/
            if (activeNetwork != null) {
                buttonEncMethod.setText(activeNetwork.getCapabilities());
            } else {
                buttonEncMethod
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /********************************* ACCESS POINT ***********************************/
            if (activeNetwork != null) {
                buttonAP.setText(activeNetwork.getSSID());
            } else {
                buttonAP.setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /******************************* LINK SPEED ***************************************/
            if (activeNetwork != null) {
                try {
                    buttonLinkSpeed.setText(networkSettings.getLinkSpeed());
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                buttonLinkSpeed
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /********************************* SIGNAL STRENGTH ********************************/
            if (activeNetwork != null) {
                buttonSignalStrength.setText(activeNetwork.getLevel() + " db");
            } else {
                buttonSignalStrength
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    /**
     * Returns MAC address of the given interface name. Note: requires
     * <uses-permission android:name="android.permission.INTERNET " /> and
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE "
     * /> in AndroidManifest.xml
     * 
     * @param interfaceName
     *        eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        String macaddress = "";
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                        continue;
                    }
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++) {
                    buf.append(String.format("%02X:", mac[idx]));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                macaddress = buf.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return macaddress;
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
        titleIDs.add(R.string.tv_menu_network_wireless_info);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_info_AP);
        list.add(TV_MENU_NETWORK_WIRELESS_INFORMATION_AP);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_info_signal_strength);
        list.add(TV_MENU_NETWORK_WIRELESS_INFORMATION_SIGNAL_STRENGTH);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_info_link_speed);
        list.add(TV_MENU_NETWORK_WIRELESS_LINK_SPEED);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_info_enc_method);
        list.add(TV_MENU_NETWORK_WIRELESS_INFORMATION_ENCRYPTION_METHOD);
        contentListIDs.add(list);
    }
}
