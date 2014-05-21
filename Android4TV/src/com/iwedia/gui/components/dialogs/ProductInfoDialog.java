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

import java.util.ArrayList;

/**
 * Product info dialog
 * 
 * @author Branimir Pavlovic
 */
public class ProductInfoDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_PRODUCT_INFO_SETTINGS_IP_ADDRESS = 26,
            // TV_MENU_PRODUCT_INFO_SETTINGS_LEGAL_INFORMATION = 27,
            TV_MENU_PRODUCT_INFO_SETTINGS_FIRMWARE_VERSION = 28,
            TV_MENU_PRODUCT_INFO_SETTINGS_MODEL_NUMBER = 29,
            TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION = 30,
            TV_MENU_PRODUCT_INFO_SETTINGS_MAC_ADDRESS = 31,
            TV_MENU_PRODUCT_INFO_SETTINGS_KERNEL_VERSION = 32,
            TV_MENU_PRODUCT_INFO_SETTINGS_BUILD_NUMBER = 33;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonModelNumber, buttonAndroidFirmwareVersion,
            buttonIPAddr, buttonMACAddr, buttonKernelVersion,
            buttonBuildNumber;

    public ProductInfoDialog(Context context) {
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
        buttonModelNumber = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_MODEL_NUMBER);
        buttonAndroidFirmwareVersion = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_FIRMWARE_VERSION);
        buttonIPAddr = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_IP_ADDRESS);
        buttonMACAddr = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_MAC_ADDRESS);
        buttonKernelVersion = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_KERNEL_VERSION);
        buttonBuildNumber = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_BUILD_NUMBER);
        /** Set desired states to buttons */
        disableBtn(buttonModelNumber);
        disableBtn(buttonAndroidFirmwareVersion);
        disableBtn(buttonIPAddr);
        disableBtn(buttonMACAddr);
        disableBtn(buttonKernelVersion);
        disableBtn(buttonBuildNumber);
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
            /******************************* MODEL NUMBER ***************************/
            String modelNumber = null;
            try {
                modelNumber = about.getModelNumber();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (modelNumber != null) {
                buttonModelNumber.setText(modelNumber);
            } else {
                buttonModelNumber
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /******************************* ANDROID VERSION ***************************/
            String androidVersion = null;
            try {
                androidVersion = about.getAndroidVersion();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (androidVersion != null) {
                buttonAndroidFirmwareVersion.setText(androidVersion);
            } else {
                buttonAndroidFirmwareVersion
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /********************************* IP ADDRESS ******************************/
            String ip = null;
            try {
                ip = about.getIPAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ip != null && ip.length() != 0) {
                buttonIPAddr.setText(ip);
            } else {
                buttonIPAddr
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /********************************* MAC ADDRESS ******************************/
            String mac = null;
            try {
                mac = MainActivity.service.getSystemControl()
                        .getNetworkControl().getEthernetMacAddress();
                // mac = about.getMacAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mac != null) {
                buttonMACAddr.setText(mac);
            } else {
                buttonMACAddr
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /********************************* KERNEL VERSION ******************************/
            String kernel = null;
            try {
                kernel = about.getKernelVersion();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (kernel != null) {
                buttonKernelVersion.setText(kernel);
            } else {
                buttonKernelVersion
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /********************************* BUILD NUMBER ******************************/
            String build = null;
            try {
                build = about.getBuildNumber();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (build != null) {
                buttonBuildNumber.setText(build);
            } else {
                buttonBuildNumber
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION: {
                ProductInfoSoftwareStatusDialog productInfoStatusDialog = MainActivity.activity
                        .getDialogManager().getProductInfoStatusDialog();
                if (productInfoStatusDialog != null) {
                    productInfoStatusDialog.show();
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
        titleIDs.add(R.string.tv_menu_product_info_settings);
        // ip address******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_product_info_settings_ip_address);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_IP_ADDRESS);
        contentListIDs.add(list);
        // mac address******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_product_info_settings_mac_address);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_MAC_ADDRESS);
        contentListIDs.add(list);
        // kernel version******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_product_info_settings_kernel);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_KERNEL_VERSION);
        contentListIDs.add(list);
        // legal information******************************************
        // list = new ArrayList<Integer>();
        // list.add(MainMenuContent.TAGA4TVTextView);
        // list.add(MainMenuContent.TAGA4TVButton);
        // contentList.add(list);
        //
        // list = new ArrayList<Integer>();
        // list.add(R.string.tv_menu_product_info_settings_legal_information);
        // list.add(TV_MENU_PRODUCT_INFO_SETTINGS_LEGAL_INFORMATION);
        // contentListIDs.add(list);
        // firmware version******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_product_info_settings_firmware_version);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_FIRMWARE_VERSION);
        contentListIDs.add(list);
        // model number******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_product_info_settings_model_number);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_MODEL_NUMBER);
        contentListIDs.add(list);
        // build number******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_product_info_settings_build_number);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_BUILD_NUMBER);
        contentListIDs.add(list);
        // software version******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_product_info_settings_software_version);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION);
        contentListIDs.add(list);
    }
}
