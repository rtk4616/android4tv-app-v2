package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.DhcpInfo;
import android.view.View;

import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Wired network information dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkAdvancedManualConfigDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for text box */
    public static final int TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_IP = 26,
            TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_NETWORK_PREFIX_LENGTH = 28,
            TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_GATEWAY_IP = 29,
            TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_DNS = 30;
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_START = 31;
    public static final int TV_MENU_NETWORK_SETTINGS_ADDRESS_TYPE = 68;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVSpinner spinnerAddressType;
    private A4TVEditText editTextDNS;
    private A4TVEditText editTextGateway;
    private A4TVEditText editTextIP;
    private A4TVEditText editTextNetworkPrefix;
    private A4TVButton buttonIP, buttonNetmask, buttonGateway, buttonDNS1,
            buttonDNS2;
    private int addressType;

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public NetworkAdvancedManualConfigDialog(Context context) {
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
        editTextDNS = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_DNS);
        editTextGateway = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_GATEWAY_IP);
        editTextIP = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_IP);
        editTextNetworkPrefix = (A4TVEditText) findViewById(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_NETWORK_PREFIX_LENGTH);
        spinnerAddressType = (A4TVSpinner) findViewById(TV_MENU_NETWORK_SETTINGS_ADDRESS_TYPE);
        spinnerAddressType
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        if (index == 1) {
                            enableEditTexts();
                        } else {
                            disableEditTexts();
                        }
                    }
                });
    }

    public void fillViews() {
        INetworkSettings networkSettings;
        try {
            networkSettings = MainActivity.service.getSystemControl()
                    .getNetworkControl();
            if (networkSettings.isDHCPactive() == true) {
                spinnerAddressType.setSelection(0);
                DhcpInfo currentIP = networkSettings.getIP();
                editTextIP.setText(intToIp(currentIP.ipAddress));
                // TODO: This Should Be Fixed!
                // editTextNetworkPrefix.setText(String.valueOf(NetworkUtils
                // .netmaskIntToPrefixLength(currentIP.netmask)));
                // Temp Fix:
                editTextNetworkPrefix.setText("");
                editTextGateway.setText(intToIp(currentIP.gateway));
                editTextDNS.setText(intToIp(currentIP.dns1));
                disableEditTexts();
            } else {
                spinnerAddressType.setSelection(1);
                DhcpInfo currentIP = networkSettings.getIP();
                editTextIP.setText(intToIp(currentIP.ipAddress));
                // TODO: This Should Be Fixed!
                // editTextNetworkPrefix.setText(String.valueOf(NetworkUtils
                // .netmaskIntToPrefixLength(currentIP.netmask)));
                // Temp Fix:
                editTextNetworkPrefix.setText("");
                editTextGateway.setText(intToIp(currentIP.gateway));
                editTextDNS.setText(intToIp(currentIP.dns1));
                enableEditTexts();
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
    }

    private void disableEditTexts() {
        editTextIP.setFocusable(false);
        editTextIP.setFocusableInTouchMode(false);
        editTextIP.setClickable(false);
        editTextNetworkPrefix.setFocusable(false);
        editTextNetworkPrefix.setFocusableInTouchMode(false);
        editTextNetworkPrefix.setClickable(false);
        editTextGateway.setFocusable(false);
        editTextGateway.setFocusableInTouchMode(false);
        editTextGateway.setClickable(false);
        editTextDNS.setFocusable(false);
        editTextDNS.setFocusableInTouchMode(false);
        editTextDNS.setClickable(false);
    }

    private void enableEditTexts() {
        editTextIP.setFocusable(true);
        editTextIP.setFocusableInTouchMode(true);
        editTextIP.setClickable(true);
        editTextNetworkPrefix.setFocusable(true);
        editTextNetworkPrefix.setFocusableInTouchMode(true);
        editTextNetworkPrefix.setClickable(true);
        editTextGateway.setFocusable(true);
        editTextGateway.setFocusableInTouchMode(true);
        editTextGateway.setClickable(true);
        editTextDNS.setFocusable(true);
        editTextDNS.setFocusableInTouchMode(true);
        editTextDNS.setClickable(true);
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        clearEditTexts();
        super.onBackPressed();
    }

    private void clearEditTexts() {
        editTextDNS.setText("");
        editTextGateway.setText("");
        editTextIP.setText("");
        editTextNetworkPrefix.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_START: {
                if (spinnerAddressType.getCHOOSEN_ITEM_INDEX() == 1) {
                    try {
                        INetworkSettings networkSettings;
                        networkSettings = MainActivity.service
                                .getSystemControl().getNetworkControl();
                        networkSettings.setStaticIP("STATIC",
                                "" + editTextIP.getText(),
                                "" + editTextDNS.getText(), ""
                                        + editTextGateway.getText(), ""
                                        + editTextNetworkPrefix.getText());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    try {
                        INetworkSettings networkSettings;
                        networkSettings = MainActivity.service
                                .getSystemControl().getNetworkControl();
                        networkSettings.enableDHCP();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
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
        titleIDs.add(R.string.tv_menu_network_advanced_manual_config);
        // ******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        // list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_advanced_network_setup_ip_settings);
        list.add(TV_MENU_NETWORK_SETTINGS_ADDRESS_TYPE);
        contentListIDs.add(list);
        // ********************
        // ArrayList<Integer> list = new ArrayList<Integer>();
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_IP);
        list.add(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_IP);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_prefix_length);
        list.add(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_NETWORK_PREFIX_LENGTH);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_gateway);
        list.add(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_GATEWAY_IP);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_dns);
        list.add(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_DNS);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.button_text_configure);
        list.add(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_START);
        contentListIDs.add(list);
    }

    public String intToIp(int addr) {
        return ((addr & 0xFF) + "." + ((addr >>>= 8) & 0xFF) + "."
                + ((addr >>>= 8) & 0xFF) + "." + ((addr >>>= 8) & 0xFF));
    }
}
