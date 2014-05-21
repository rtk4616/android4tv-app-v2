package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.View;

import com.iwedia.comm.system.about.IAbout;
import com.iwedia.dtv.swupdate.SWVersionType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Product info dialog
 * 
 * @author Branimir Pavlovic
 */
public class ProductInfoSoftwareStatusDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_BSP = 26,
            TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_HAL = 28,
            TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_MWL = 29,
            TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_MAL = 30,
            TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_BUNDLE_VERSION = 31,
            TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_APP_VERSION = 32;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonBSP, buttonHAL, buttonMWL, buttonMAL,
            button_BUNDLEVersion, button_APPVersion;
    private Context ctx;
    public static final String BUNDLE_FILE_NAME = "bundle_version.txt";

    public ProductInfoSoftwareStatusDialog(Context context) {
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

    /** Get references from views and set look and feel */
    private void init() {
        buttonBSP = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_BSP);
        buttonHAL = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_HAL);
        buttonMWL = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_MWL);
        buttonMAL = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_MAL);
        button_BUNDLEVersion = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_BUNDLE_VERSION);
        button_APPVersion = (A4TVButton) findViewById(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_APP_VERSION);
        disableBtn(buttonBSP);
        disableBtn(buttonHAL);
        disableBtn(buttonMWL);
        disableBtn(buttonMAL);
        disableBtn(button_BUNDLEVersion);
        disableBtn(button_APPVersion);
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
            /******************************* BSP ***************************/
            String str = null;
            try {
                str = about.getSWVersion(SWVersionType.BSP);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (str != null) {
                buttonBSP.setText(str);
            } else {
                buttonBSP
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /******************************* HAL ***************************/
            str = null;
            try {
                str = about.getSWVersion(SWVersionType.HAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (str != null) {
                buttonHAL.setText(str);
            } else {
                buttonHAL
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /******************************* MWL ***************************/
            str = null;
            try {
                str = about.getSWVersion(SWVersionType.MWL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (str != null) {
                buttonMWL.setText(str);
            } else {
                buttonMWL
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /******************************* MAL ***************************/
            str = null;
            try {
                str = about.getSWVersion(SWVersionType.MAL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (str != null) {
                buttonMAL.setText(str);
            } else {
                buttonMAL
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /******************************* BUNDLE Version ***************************/
            str = null;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(ctx.getAssets().open(
                                BUNDLE_FILE_NAME)));
                str = reader.readLine();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (str != null) {
                button_BUNDLEVersion.setText(str);
            } else {
                button_BUNDLEVersion
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            }
            /*********************** APPLICATION VERSION ***************************/
            /** Check version number */
            PackageInfo pInfo;
            String version = null;
            try {
                pInfo = ctx.getPackageManager().getPackageInfo(
                        ctx.getPackageName(), 0);
                version = pInfo.versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (version == null) {
                button_APPVersion
                        .setText(R.string.tv_menu_software_upgrade_settings_unavailable);
            } else {
                button_APPVersion.setText(version);
            }
        }
    }

    @Override
    public void onClick(View v) {
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
        // Board Support Packages******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_bsp);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_BSP);
        contentListIDs.add(list);
        // Hardware Abstraction Layer******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_hal);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_HAL);
        contentListIDs.add(list);
        // Middleware******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_mwl);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_MWL);
        contentListIDs.add(list);
        // Middleware Abstraction
        // Layer******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_mal);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_MAL);
        contentListIDs.add(list);
        // Application version******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_software_version);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_APP_VERSION);
        contentListIDs.add(list);
        // Software bundle version******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_svn_version);
        list.add(TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION_BUNDLE_VERSION);
        contentListIDs.add(list);
    }
}
