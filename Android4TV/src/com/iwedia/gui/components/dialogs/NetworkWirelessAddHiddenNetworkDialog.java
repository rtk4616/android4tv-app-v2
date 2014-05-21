package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.iwedia.comm.enums.WirelessState;
import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.comm.system.WifiAddHiddenNetwork;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Wireless network add hidden network dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkWirelessAddHiddenNetworkDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_ADD = 7788;
    /** IDs for spinner */
    public static final int TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_SECURITY = 7777;
    /** IDs for edit text in this dialog */
    public static final int TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_SSID = 7755,
            TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_PASSWORD = 7756;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonAddNetwork;
    private A4TVSpinner spinnerSelectSecurity;
    private A4TVToast toastHiddenSSID;
    private Context ctx;
    private WifiAddHiddenNetwork configureNetwork;
    private String password = "";
    private String SSID = "";
    private INetworkSettings networkSettings;

    public NetworkWirelessAddHiddenNetworkDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        configureNetwork = new WifiAddHiddenNetwork();
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
        buttonAddNetwork = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_ADD);
        spinnerSelectSecurity = (A4TVSpinner) findViewById(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_SECURITY);
        buttonAddNetwork.setClickable(false);
        toastHiddenSSID = new A4TVToast(ctx);
        ((EditText) findViewById(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_SSID))
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                            int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                            int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() > 0) {
                            SSID = s.toString();
                            configureNetwork.setSSID(SSID);
                            buttonAddNetwork.setClickable(true);
                        } else {
                            buttonAddNetwork.setClickable(false);
                        }
                    }
                });
        ((EditText) findViewById(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_PASSWORD))
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start,
                            int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                            int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        password = s.toString();
                        configureNetwork.setPassword(password);
                    }
                });
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_ADD: {
                int choosenSecurity;
                networkSettings = null;
                try {
                    networkSettings = MainActivity.service.getSystemControl()
                            .getNetworkControl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                choosenSecurity = spinnerSelectSecurity.getCHOOSEN_ITEM_INDEX();
                if (choosenSecurity == 0) {
                    configureNetwork.setCapabilities("NONE");
                } else if (choosenSecurity == 1) {
                    configureNetwork.setCapabilities("WEP");
                } else {
                    configureNetwork.setCapabilities("WPA PSK");
                }
                try {
                    if (networkSettings != null) {
                        networkSettings
                                .setHiddenWirelessNetwork(configureNetwork);
                    }
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

    public void wirelessNetworksChanged(int state) {
        if (this.isShowing()) {
            switch (state) {
                case WirelessState.WIRELESS_STATE_CONNECTED: {
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastHiddenSSID.showToast("Connected to " + SSID);
                        }
                    });
                    break;
                }
                case WirelessState.WIRELESS_STATE_FAILED: {
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastHiddenSSID.showToast("Failed to connect to "
                                    + SSID);
                        }
                    });
                    break;
                }
                case WirelessState.WIRELESS_STATE_PASSWORD_INCORRECT: {
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toastHiddenSSID.showToast("Bad parameters " + SSID);
                        }
                    });
                    break;
                }
                default:
                    break;
            }
        }
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
        titleIDs.add(R.string.tv_menu_network_wireless_add_hidden_network);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_add_hidden_network_ssid);
        list.add(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_SSID);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_add_hidden_network_security);
        list.add(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_SECURITY);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_add_hidden_network_password);
        list.add(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_PASSWORD);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_add_hidden_network_add_none);
        list.add(TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_ADD);
        contentListIDs.add(list);
    }
}
