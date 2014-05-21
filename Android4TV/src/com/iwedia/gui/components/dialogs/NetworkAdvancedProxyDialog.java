package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
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
public class NetworkAdvancedProxyDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for text box */
    public static final int TV_MENU_NETWORK_WIRED_MANUAL_PROXY = 26,
            TV_MENU_NETWORK_WIRED_MANUAL_PORT = 28;
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_WIRED_MANUAL_PROXY_START = 31;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();

    public NetworkAdvancedProxyDialog(Context context) {
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
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onBackPressed() {
        clearEditTexts();
        super.onBackPressed();
    }

    private void clearEditTexts() {
        A4TVEditText editTextIP = (A4TVEditText) findViewById(TV_MENU_NETWORK_WIRED_MANUAL_PROXY);
        editTextIP.setText("");
        A4TVEditText editPort = (A4TVEditText) findViewById(TV_MENU_NETWORK_WIRED_MANUAL_PORT);
        editPort.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_NETWORK_WIRED_MANUAL_PROXY_START: {
                A4TVEditText editProxy = (A4TVEditText) findViewById(TV_MENU_NETWORK_WIRED_MANUAL_PROXY);
                A4TVEditText editPort = (A4TVEditText) findViewById(TV_MENU_NETWORK_WIRED_MANUAL_PORT);
                INetworkSettings networkSettings;
                try {
                    networkSettings = MainActivity.service.getSystemControl()
                            .getNetworkControl();
                    networkSettings.setWifiProxySettings(
                            "" + editProxy.getText(), "" + editPort.getText());
                } catch (Exception e) {
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
        titleIDs.add(R.string.tv_menu_network_advanced_network_setup_settings_proxy_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_IP);
        list.add(TV_MENU_NETWORK_WIRED_MANUAL_PROXY);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wired_info_port);
        list.add(TV_MENU_NETWORK_WIRED_MANUAL_PORT);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.button_text_configure);
        list.add(TV_MENU_NETWORK_WIRED_MANUAL_PROXY_START);
        contentListIDs.add(list);
    }
}
