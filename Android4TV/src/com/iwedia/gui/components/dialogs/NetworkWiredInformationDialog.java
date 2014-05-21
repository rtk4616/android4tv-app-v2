package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;

import com.iwedia.comm.system.about.IAbout;
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
 * Wired network information dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkWiredInformationDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_WIRED_INFORMATION__IP = 26,
            TV_MENU_NETWORK_WIRED_INFORMATION_NETMASK = 28,
            TV_MENU_NETWORK_WIRED_INFORMATION_GATEWAY_IP = 29,
            TV_MENU_NETWORK_WIRED_INFORMATION_DNS1 = 30,
            TV_MENU_NETWORK_WIRED_INFORMATION_DNS2 = 31;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonIP, buttonNetmask, buttonGateway, buttonDNS1,
            buttonDNS2;

    public NetworkWiredInformationDialog(Context context) {
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
        buttonIP = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRED_INFORMATION__IP);
        buttonNetmask = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRED_INFORMATION_NETMASK);
        buttonGateway = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRED_INFORMATION_GATEWAY_IP);
        buttonDNS1 = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRED_INFORMATION_DNS1);
        buttonDNS2 = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRED_INFORMATION_DNS2);
        /** Set desired states to buttons */
        disableBtn(buttonIP);
        disableBtn(buttonNetmask);
        disableBtn(buttonGateway);
        disableBtn(buttonDNS1);
        disableBtn(buttonDNS2);
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
        IAbout about = null;
        try {
            about = MainActivity.service.getSystemControl().getAbout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (about != null) {
            /******************************* GATEWAY ADDRESS ***************************/
            buttonGateway
                    .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            /******************************* NETMASK ADDRESS ***************************/
            buttonNetmask
                    .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            /********************************* IP ADDRESS ******************************/
            String ip = null;
            try {
                ip = about.getIPAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ip != null && ip.length() != 0) {
                buttonIP.setText(ip);
            } else {
                buttonIP.setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /********************************* DNS1 ADDRESS ******************************/
            buttonDNS1
                    .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            /********************************* DNS2 ADDRESS ******************************/
            buttonDNS2
                    .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
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
        titleIDs.add(R.string.tv_menu_network_wired_info);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_IP);
        list.add(TV_MENU_NETWORK_WIRED_INFORMATION__IP);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_netmask);
        list.add(TV_MENU_NETWORK_WIRED_INFORMATION_NETMASK);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_gateway);
        list.add(TV_MENU_NETWORK_WIRED_INFORMATION_GATEWAY_IP);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_dns1);
        list.add(TV_MENU_NETWORK_WIRED_INFORMATION_DNS1);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_dns2);
        list.add(TV_MENU_NETWORK_WIRED_INFORMATION_DNS2);
        contentListIDs.add(list);
    }
}
