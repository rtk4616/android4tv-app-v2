package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Wired nework settings dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkAdvancedSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG = 5008;
    public static final int TV_MENU_NETWORK_ADVANCED_PROXY_SETTINGS = 5009;
    public static final int TV_MENU_NETWORK_ADVANCED_SOFT_AP_SETTINGS = 5010;
    private A4TVButton btnProxySettings;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();

    public NetworkAdvancedSettingsDialog(Context context) {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG: {
                NetworkAdvancedManualConfigDialog netWiredAdvancedlConfigDialog = MainActivity.activity
                        .getDialogManager()
                        .getNetworkAdvancedManualConfigDialog();
                if (netWiredAdvancedlConfigDialog != null) {
                    netWiredAdvancedlConfigDialog.show();
                }
                break;
            }
            case TV_MENU_NETWORK_ADVANCED_PROXY_SETTINGS: {
                NetworkAdvancedProxyDialog netAdvancedProxyDialog = MainActivity.activity
                        .getDialogManager().getNetworkAdvancedProxyDialog();
                if (netAdvancedProxyDialog != null) {
                    netAdvancedProxyDialog.show();
                }
                break;
            }
            case TV_MENU_NETWORK_ADVANCED_SOFT_AP_SETTINGS: {
                NetworkAdvancedSoftAPDialog netAdvancedSoftAPDialog = MainActivity.activity
                        .getDialogManager().getNetworkAdvancedSoftAPDialog();
                if (netAdvancedSoftAPDialog != null) {
                    netAdvancedSoftAPDialog.show();
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
        titleIDs.add(R.string.tv_menu_network_settings_advanced_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_advanced_manual_config);
        list.add(TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_advanced_proxy_settings);
        list.add(TV_MENU_NETWORK_ADVANCED_PROXY_SETTINGS);
        contentListIDs.add(list);
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_advanced_soft_ap_config);
        list.add(TV_MENU_NETWORK_ADVANCED_SOFT_AP_SETTINGS);
        contentListIDs.add(list);
    }
}
