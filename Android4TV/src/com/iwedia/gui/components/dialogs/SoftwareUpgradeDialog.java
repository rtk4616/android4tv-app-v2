package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.iwedia.comm.system.about.ISoftwareUpdate;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.pvr.A4TVStorageManager;
import com.iwedia.gui.pvr.A4TVUSBStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Software update dialog
 * 
 * @author Branimir Pavlovic
 */
public class SoftwareUpgradeDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    public static final String TAG = "SoftwareUpgradeDialog";
    /** IDs for buttons */
    public static final int TV_MENU_SOFTWARE_UPGRADE_SETTINGS_AUTO_UPGRADE = 30,
            TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE = 31,
            TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_CURR_VERSION = 32,
            TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_UPGRADE = 33,
            TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_USB = 34;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonManuallyUpgrade,
            buttonManuallyUpgradeCurrentVersion,
            buttonManuallyUpgradeUpgradeBtn, buttonManuallyUpgradeUSB;
    private A4TVButtonSwitch buttonAutoUpgrade;
    private A4TVAlertDialog alertDialog;
    private Handler handlerForCallBacks;
    private Context ctx;
    private A4TVStorageManager storage = new A4TVStorageManager();
    public static final int SHOW_ALERT_DIALOG = 0, TOAST_MESSAGE = 1,
            USB_UPGRADE_MSG = 2, USB_CHECK_UPDATE = 3;
    // fields for animation
    private Thread thread;
    private Runnable run;
    private int checkType;
    private final int CHECKING = 11, DOWNLOADING = 12;

    public SoftwareUpgradeDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        fillInitialViews();
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

    /** Get references from views */
    private void fillInitialViews() {
        /** Get references from views */
        buttonManuallyUpgrade = (A4TVButton) findViewById(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE);
        buttonAutoUpgrade = (A4TVButtonSwitch) findViewById(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_AUTO_UPGRADE);
        buttonManuallyUpgradeCurrentVersion = (A4TVButton) findViewById(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_CURR_VERSION);
        buttonManuallyUpgradeUpgradeBtn = (A4TVButton) findViewById(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_UPGRADE);
        buttonManuallyUpgradeUSB = (A4TVButton) findViewById(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_USB);
        // hide some views and change paddings
        buttonManuallyUpgrade.setBackgroundColor(Color.TRANSPARENT);
        buttonManuallyUpgrade.setFocusable(false);
        buttonManuallyUpgrade.setGravity(Gravity.LEFT);
        buttonManuallyUpgradeCurrentVersion
                .setBackgroundColor(Color.TRANSPARENT);
        buttonManuallyUpgradeUpgradeBtn
                .setText(R.string.tv_menu_software_upgrade_settings_manually_uprade_btn);
        buttonManuallyUpgradeUSB
                .setText(R.string.tv_menu_software_upgrade_settings_manually_uprade_btn);
        LinearLayout view = (LinearLayout) findViewById(R.string.tv_menu_software_upgrade_settings_manually_uprade);
        if (view != null) {
            view.setPadding(4, 4, 15, 4);
        }
        /** Init handler for executing commands from call backs */
        handlerForCallBacks = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_ALERT_DIALOG: {
                        stopThread();
                        showAlertDialogWithMessage((String) msg.obj, -1, false);
                        break;
                    }
                    case TOAST_MESSAGE: {
                        stopThread();
                        final A4TVAlertDialog alert = new A4TVAlertDialog(ctx);
                        alert.setCancelable(true);
                        alert.setTitleOfAlertDialog((String) msg.obj);
                        alert.setPositiveButton(R.string.parental_control_ok,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.cancel();
                                    }
                                });
                        alert.show();
                        break;
                    }
                    case USB_UPGRADE_MSG: {
                        stopThread();
                        String message = (String) msg.obj;
                        Log.d(TAG, "RECEIVED USB UPGRADE" + message);
                        if (message.contains("Error:")) {
                            final A4TVAlertDialog alert = new A4TVAlertDialog(
                                    ctx);
                            alert.setCancelable(true);
                            alert.setTitleOfAlertDialog(R.string.no_usb);
                            alert.setPositiveButton(
                                    R.string.parental_control_ok,
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alert.cancel();
                                        }
                                    });
                            alert.show();
                        }
                        if (message.contains("Finish:")) {
                            // FTI should run after update
                            clearSavedData();
                            A4TVToast toast = new A4TVToast(ctx);
                            toast.showToast(R.string.upgrade_finished_over_usb);
                        }
                        break;
                    }
                    case USB_CHECK_UPDATE: {
                        stopThread();
                        int swType = msg.arg1;
                        String swVersion = (String) msg.obj;
                        showAlertDialogWithMessage(swVersion, swType, true);
                        break;
                    }
                    case DOWNLOADING: {
                        if (buttonManuallyUpgrade.getText().toString()
                                .contains("...")) {
                            buttonManuallyUpgrade.setText(R.string.downloading);
                        } else {
                            buttonManuallyUpgrade.setText(buttonManuallyUpgrade
                                    .getText().toString() + ".");
                        }
                        break;
                    }
                    case CHECKING: {
                        if (buttonManuallyUpgrade.getText().toString()
                                .contains("...")) {
                            buttonManuallyUpgrade.setText(R.string.checking);
                        } else {
                            buttonManuallyUpgrade.setText(buttonManuallyUpgrade
                                    .getText().toString() + ".");
                        }
                        break;
                    }
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        run = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Thread currentThread = Thread.currentThread();
                    if (currentThread.equals(thread)) {
                        handlerForCallBacks.sendEmptyMessage(checkType);
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
        };
    }

    /** Hide progress dialog and toast error message */
    public void toastMessage(String message) {
        handlerForCallBacks.sendMessage(Message.obtain(handlerForCallBacks,
                TOAST_MESSAGE, message));
    }

    /** This function need to be called from call back to show alert dialog */
    public void showAlertDialog(String version) {
        handlerForCallBacks.sendMessage(Message.obtain(handlerForCallBacks,
                SHOW_ALERT_DIALOG, version));
    }

    public void usbMessageReceived(String msg) {
        handlerForCallBacks.sendMessage(Message.obtain(handlerForCallBacks,
                USB_UPGRADE_MSG, msg));
    }

    public void usbCheckUpdateEvent(int msgType, String msg) {
        handlerForCallBacks.sendMessage(Message.obtain(handlerForCallBacks,
                USB_CHECK_UPDATE, msgType, 0, msg));
    }

    /**
     * Shows alert dialog with messages
     * 
     * @param version
     *        version of new software
     * @param SWUpgradeMsgs
     *        upgrade or down grade
     */
    private void showAlertDialogWithMessage(String version, int swUpgradeMsgs,
            boolean overUSB) {
        /** Initialize alert dialog */
        alertDialog = new A4TVAlertDialog(ctx);
        if (swUpgradeMsgs == -1
                || swUpgradeMsgs == com.iwedia.comm.enums.SWUpgradeMsgs.UPGRADE) {
            alertDialog.setTitleOfAlertDialog(ctx.getResources().getString(
                    R.string.software_upgrade_text_part_one)
                    + " "
                    + version
                    + " "
                    + ctx.getResources().getString(
                            R.string.software_upgrade_text_part_two_upgrade));
        }
        if (swUpgradeMsgs == com.iwedia.comm.enums.SWUpgradeMsgs.DOWNGRADE) {
            alertDialog.setTitleOfAlertDialog(ctx.getResources().getString(
                    R.string.software_upgrade_text_part_one)
                    + " "
                    + version
                    + " "
                    + ctx.getResources().getString(
                            R.string.software_upgrade_text_part_two_downgrade));
        }
        if (swUpgradeMsgs == com.iwedia.comm.enums.SWUpgradeMsgs.NO_AVAILABLE_VERSION) {
            final A4TVAlertDialog alert = new A4TVAlertDialog(ctx);
            alert.setCancelable(true);
            alert.setTitleOfAlertDialog(R.string.upgrade_no_new_version);
            alert.setPositiveButton(R.string.parental_control_ok,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                        }
                    });
            alert.show();
            return;
        }
        if (swUpgradeMsgs == com.iwedia.comm.enums.SWUpgradeMsgs.NO_ZIP_FILE) {
            final A4TVAlertDialog alert = new A4TVAlertDialog(ctx);
            alert.setCancelable(true);
            alert.setTitleOfAlertDialog(R.string.no_usb);
            alert.setPositiveButton(R.string.parental_control_ok,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                        }
                    });
            alert.show();
            return;
        }
        if (swUpgradeMsgs == com.iwedia.comm.enums.SWUpgradeMsgs.INVALID_ZIP) {
            final A4TVAlertDialog alert = new A4TVAlertDialog(ctx);
            alert.setCancelable(true);
            alert.setTitleOfAlertDialog(ctx.getResources().getString(
                    R.string.upgrade_invalid_zip));
            alert.setPositiveButton(R.string.parental_control_ok,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                        }
                    });
            alert.show();
            return;
        }
        alertDialog.setCancelable(false);
        // ////////////////////////////////////////////
        // OVER NETWORK
        // ////////////////////////////////////////////
        if (!overUSB) {
            alertDialog.setPositiveButton(R.string.button_text_yes,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /** Upgrade software to new version */
                            try {
                                clearSavedData();
                                MainActivity.service.getSystemControl()
                                        .getAbout().getSoftwareUpdate()
                                        .upgrade();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            alertDialog.cancel();
                        }
                    });
        }
        // ////////////////////////////////////////////
        // USB
        // ////////////////////////////////////////////
        else {
            alertDialog.setPositiveButton(R.string.button_text_yes,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /** Upgrade software to new version */
                            try {
                                clearSavedData();
                                MainActivity.service.getSystemControl()
                                        .getAbout().getSoftwareUpdate()
                                        .finishUSBUpgrade();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            alertDialog.cancel();
                        }
                    });
        }
        alertDialog.setNegativeButton(R.string.button_text_no,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void fillViews() {
        ISoftwareUpdate softwareUpdate = null;
        try {
            softwareUpdate = MainActivity.service.getSystemControl().getAbout()
                    .getSoftwareUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (softwareUpdate != null) {
            String runningVersion = null;
            try {
                runningVersion = softwareUpdate.getRunnungVersion();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (runningVersion != null) {
                buttonManuallyUpgradeCurrentVersion.setText(runningVersion);
            }
        }
        /********* AUTO UPGRADE SWITCH BUTTON **********/
        buttonAutoUpgrade.setSelectedStateAndText(false,
                R.string.button_text_no);
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    /** Check internet connection */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_UPGRADE: {
                if (isOnline()) {
                    try {
                        MainActivity.service.getSystemControl().getAbout()
                                .getSoftwareUpdate().upgradeCheck();
                        startThread(DOWNLOADING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    A4TVToast toast = new A4TVToast(ctx);
                    toast.showToast(R.string.no_internet_connection);
                }
                break;
            }
            case TV_MENU_SOFTWARE_UPGRADE_SETTINGS_AUTO_UPGRADE: {
                /********* AUTO UPGRADE SWITCH BUTTON **********/
                buttonAutoUpgrade.setSelectedStateAndText(!v.isSelected(), !v
                        .isSelected() ? R.string.button_text_yes
                        : R.string.button_text_no);
                break;
            }
            case TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_USB: {
                String usbMountPath = "../..";
                File usbRootDir;
                String[] filesOnUsbRoot;
                boolean upgradeZipFound = false;
                if (storage != null) {
                    if (storage.getUSBStorage(0) == null) {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast(R.string.no_usb);
                        break;
                    }
                    /* TODO: what if there is more than one partition on USB? */
                    A4TVUSBStorage usbStorage = storage.getUSBStorage(0);
                    if (usbStorage != null)
                        usbMountPath = usbMountPath.concat(usbStorage
                                .getPartitionMountPath(0));
                    usbRootDir = new File(usbMountPath);
                    filesOnUsbRoot = usbRootDir.list();
                    if (filesOnUsbRoot != null)
                        for (int i = 0; i < filesOnUsbRoot.length; i++) {
                            /*
                             * TODO: what is going to be the name of the zip
                             * file with upgrades? upgrade.zip?
                             */
                            if (filesOnUsbRoot[i].matches("upgrade.zip")) {
                                upgradeZipFound = true;
                                break;
                            }
                        }
                    if (upgradeZipFound == false) {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast(R.string.no_usb_zip_file);
                        break;
                    }
                }
                try {
                    MainActivity.service.getSystemControl().getAbout()
                            .getSoftwareUpdate().copyUpgradeFWFromUSB();
                    startThread(CHECKING);
                } catch (Exception e) {
                    e.printStackTrace();
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
        titleIDs.add(R.string.tv_menu_software_upgrade_settings);
        // auto upgrade******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_auto_upgrade);
        list.add(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_AUTO_UPGRADE);
        contentListIDs.add(list);
        // manually upgrade******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_manually_uprade);
        list.add(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE);
        contentListIDs.add(list);
        // manually upgrade current
        // version******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_manually_uprade_current_version);
        list.add(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_CURR_VERSION);
        contentListIDs.add(list);
        // manually upgrade upgrade******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_manually_uprade_upgrade);
        list.add(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_UPGRADE);
        contentListIDs.add(list);
        // manually upgrade over USB******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_software_upgrade_settings_manually_uprade_usb);
        list.add(TV_MENU_SOFTWARE_UPGRADE_SETTINGS_MANUALLY_UPGRADE_USB);
        contentListIDs.add(list);
    }

    private void clearSavedData() {
        Editor editor = MainActivity.getSharedPrefs().edit();
        editor.clear();
        editor.commit();
        MainActivity.getSharedPrefs().edit()
                .putBoolean(MainActivity.FIRST_TIME_INSTALL, true).commit();
        // Remove widgets
        if (MainActivity.activity.getWidgetsHandler() != null) {
            MainActivity.activity.getWidgetsHandler().removeAllWidgets();
            try {
                MainActivity.activity.getWidgetsHandler().saveImportantData();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Start background thread
     */
    public void startThread(int checkType) {
        Log.d(TAG, "start thread entered");
        if (thread == null) {
            if (DOWNLOADING == checkType) {
                buttonManuallyUpgrade.setText(R.string.downloading);
            } else {
                buttonManuallyUpgrade.setText(R.string.checking);
            }
            this.checkType = checkType;
            thread = new Thread(run);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    /**
     * Stops background thread
     */
    public void stopThread() {
        Log.d(TAG, "stop thread entered");
        if (thread != null) {
            Thread moribund = thread;
            thread = null;
            moribund.interrupt();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttonManuallyUpgrade.setText("");
                }
            }, 400);
        }
    }

    public void stopThreadFromCallback() {
        Log.d(TAG, "stop thread from callback entered");
        if (thread != null
                && buttonManuallyUpgrade.getText().toString()
                        .contains(ctx.getText(R.string.checking))) {
            Thread moribund = thread;
            thread = null;
            moribund.interrupt();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttonManuallyUpgrade.setText("");
                }
            }, 100);
        }
    }
}
