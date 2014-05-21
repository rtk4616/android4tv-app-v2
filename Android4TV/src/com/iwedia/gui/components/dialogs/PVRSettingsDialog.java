package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.RemoteException;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.pvr.A4TVStorageManager;
import com.iwedia.gui.pvr.A4TVUSBStorage;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Pvr settings dialog
 * 
 * @author Sasa Jagodin
 */
public class PVRSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    private static A4TVProgressDialog progressDialog = null;
    private static final double MB_IN_GB = 1024.0;
    /** ID for spinner */
    public static final int TV_MENU_PVR_SETTINGS_DEVICE = 111;
    /** IDs for buttons */
    public static final int TV_MENU_PVR_SETTINGS_FREE_SPACE = 1,
            TV_MENU_PVR_SETTINGS_TOTAL_SPACE = 2,
            TV_MENU_PVR_SETTINGS_SET_PATH = 3,
            TV_MENU_PVR_SETTINGS_START_SPEED_TEST = 4,
            TV_MENU_PVR_SETTINGS_SPEED = 5;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVStorageManager storage = new A4TVStorageManager();
    private static int SD_LIMIT = 2000;
    private static int HD_LIMIT = 5000;
    private A4TVButton buttonFreeSpace, buttonUsedSpace, buttonSetPath,
            buttonSpeedTest;
    private static A4TVButton buttonSpeed;
    private A4TVSpinner spinnerDevice;

    public PVRSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        progressDialog = new A4TVProgressDialog(context);
        progressDialog
                .setTitleOfAlertDialog(R.string.tv_menu_pvr_settings_performing_speed_test);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(R.string.please_wait);
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
        buttonSpeedTest = (A4TVButton) findViewById(TV_MENU_PVR_SETTINGS_START_SPEED_TEST);
        buttonFreeSpace = (A4TVButton) findViewById(TV_MENU_PVR_SETTINGS_FREE_SPACE);
        buttonUsedSpace = (A4TVButton) findViewById(TV_MENU_PVR_SETTINGS_TOTAL_SPACE);
        buttonSpeed = (A4TVButton) findViewById(TV_MENU_PVR_SETTINGS_SPEED);
        buttonSetPath = (A4TVButton) findViewById(TV_MENU_PVR_SETTINGS_SET_PATH);
        spinnerDevice = (A4TVSpinner) findViewById(TV_MENU_PVR_SETTINGS_DEVICE);
        buttonSpeedTest.setText(R.string.button_text_start);
        buttonSetPath.setText(R.string.button_text_enable);
        buttonSpeed.setText("");
        /** Set desired states to buttons */
        disableBtn(buttonFreeSpace);
        disableBtn(buttonUsedSpace);
    }

    /** Disable clicks on buttons and clear background */
    private void disableBtn(A4TVButton btn) {
        btn.setClickable(false);
        btn.setBackgroundColor(Color.TRANSPARENT);
    }

    public static void setUsbSpeed(int speed) {
        progressDialog.dismiss();
        A4TVToast toast = new A4TVToast(MainActivity.activity);
        if (speed > SD_LIMIT) {
            if (speed > HD_LIMIT) {
                toast.showToast(R.string.tv_menu_pvr_settings_sd_hd_streams);
            } else {
                toast.showToast(R.string.tv_menu_pvr_settings_sd_streams);
            }
        } else {
            toast.showToast(R.string.tv_menu_pvr_settings_wrong);
        }
        buttonSpeed.setText("" + speed);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
        /************* Select device *************/
        int what = 0;
        spinnerDevice.fillDialogWithElements(spinnerDevice);
        spinnerDevice.setSelection(what);
        if (storage != null) {
            double freeSpace;
            double totalSpace;
            DecimalFormat dec = new DecimalFormat("0.00");
            A4TVUSBStorage usbStorage = storage.getUSBStorage(what);
            if (usbStorage != null) {
                freeSpace = usbStorage.getPartitionAvailableSize(0) / MB_IN_GB;
                if (freeSpace > 0) {
                    buttonFreeSpace.setText(dec.format(freeSpace));
                } else {
                    buttonFreeSpace.setText(dec.format(0));
                }
                totalSpace = usbStorage.getPartitionSize(0) / MB_IN_GB;
                if (totalSpace > 0) {
                    buttonUsedSpace.setText(dec.format(totalSpace));
                } else {
                    buttonUsedSpace.setText(dec.format(0));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_PVR_SETTINGS_SET_PATH: {
                String mountPath = "../..";
                if (storage != null) {
                    A4TVUSBStorage usbStorage = storage.getUSBStorage(0);
                    if (usbStorage != null) {
                        mountPath = mountPath.concat(usbStorage
                                .getPartitionMountPath(0));
                        try {
                            MainActivity.service.getPvrControl().setDevicePath(
                                    mountPath);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                MainActivity.getSharedPrefs().edit()
                        .putString(MainActivity.PVR_MOUNTH_PATH, mountPath)
                        .commit();
                break;
            }
            case TV_MENU_PVR_SETTINGS_START_SPEED_TEST: {
                String mountPath = "../..";
                if (storage != null) {
                    A4TVUSBStorage usbStorage = storage.getUSBStorage(0);
                    if (usbStorage != null) {
                        mountPath = mountPath.concat(usbStorage
                                .getPartitionMountPath(0));
                        try {
                            MainActivity.service.getPvrControl().setDevicePath(
                                    mountPath);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        progressDialog.show();
                    }
                }
                try {
                    // MainActivity.service.getPvrControl().startSpeedTest();
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
        titleIDs.add(R.string.tv_menu_pvr_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        // select country******************************************
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_settings_device);
        list.add(TV_MENU_PVR_SETTINGS_DEVICE);
        contentListIDs.add(list);
        // speed test ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_settings_start_speed_test);
        list.add(TV_MENU_PVR_SETTINGS_START_SPEED_TEST);
        contentListIDs.add(list);
        // speed******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_settings_speed_test_result);
        list.add(TV_MENU_PVR_SETTINGS_SPEED);
        contentListIDs.add(list);
        // free space******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_settings_free_space);
        list.add(TV_MENU_PVR_SETTINGS_FREE_SPACE);
        contentListIDs.add(list);
        // used space******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_settings_total_space);
        list.add(TV_MENU_PVR_SETTINGS_TOTAL_SPACE);
        contentListIDs.add(list);
        // set pvr path ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pvr_settings_set_path);
        list.add(TV_MENU_PVR_SETTINGS_SET_PATH);
        contentListIDs.add(list);
    }
}
