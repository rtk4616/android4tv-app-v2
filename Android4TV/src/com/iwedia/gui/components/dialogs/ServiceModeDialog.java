package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVPasswordDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.pvr.A4TVStorageManager;
import com.iwedia.gui.pvr.A4TVUSBStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Product info dialog
 * 
 * @author Sasa Jagodin
 */
public class ServiceModeDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    private final static String TAG = "ServiceMode";
    private A4TVEditText editText1;
    private A4TVPasswordDialog alert;
    private int storeModeExitPassword = 1234;
    private Context ctx;
    private A4TVStorageManager storage = new A4TVStorageManager();
    /** IDs for buttons */
    public static final int TV_MENU_SERVICE_MODE_MENU_SYSTEM_SETTINGS = 1,
            TV_MENU_SERVICE_MODE_MENU_PANEL_SETTINGS = 2,
            TV_MENU_SERVICE_MODE_MENU_VIDEO_SETTINGS = 3,
            TV_MENU_SERVICE_MODE_MENU_SOUND_SETTINGS = 4,
            TV_MENU_SERVICE_MODE_MENU_DEBUGGING_DATA = 5,
            TV_MENU_SERVICE_MODE_SAVE_SETTINGS_TO_USB = 6,
            TV_MENU_SERVICE_MODE_LOAD_SETTINGS_FROM_USB = 7;
    private A4TVButton systemSettings, panelSettings, videoSettings,
            soundSettings, debuggingData, saveSettingsToUSB,
            loadSettingsFromUSB;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();

    public ServiceModeDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
    }

    private boolean checkPassword(int password) {
        if (password == storeModeExitPassword) {
            return true;
        } else {
            return false;
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            Log.d(TAG, "input = " + input);
            output = new FileOutputStream(dest);
            Log.d(TAG, "output = " + output);
            byte[] buf = new byte[1024];
            int bytesRead;
            int i = 0;
            Log.d(TAG, "start copyFile ");
            while ((bytesRead = input.read(buf)) > 0) {
                Log.d(TAG, "Copy i = " + i + "bytesRead = " + bytesRead);
                output.write(buf, 0, bytesRead);
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found");
        } catch (IOException e) {
            Log.i(TAG, "******* IOException");
            e.printStackTrace();
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        // Abort scan
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_DEL:
            case KeyEvent.KEYCODE_BACK: {
                /* enter password to exit service mode */
                editText1.setText("");
                alert.show();
                editText1.requestFocus();
                alert.setNegativeButton(R.string.button_text_no,
                        new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert.cancel();
                            }
                        });
                alert.setPositiveButton(R.string.button_text_yes,
                        new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String password = editText1.getText()
                                        .toString();
                                boolean valid = false;
                                try {
                                    if (password.length() == 4) {
                                        valid = checkPassword(Integer
                                                .valueOf(password));
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                                if (valid) {
                                    try {
                                        /* exit service mode */
                                        ServiceModeDialog.this.cancel();
                                        MainActivity.sharedPrefs
                                                .edit()
                                                .putBoolean(
                                                        MainActivity.SERVICE_MODE_START,
                                                        false).commit();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    alert.cancel();
                                } else {
                                    /* Wrong password */
                                    editText1.setText("");
                                    A4TVToast toast = new A4TVToast(ctx);
                                    toast.showToast(R.string.wrong_pin_entered);
                                    editText1.requestFocus();
                                    PasswordSecurityDialog
                                            .wrongPasswordEntered(alert, false);
                                }
                            }
                        });
            }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /** Get references from views and set look and feel */
    private void init() {
        systemSettings = (A4TVButton) findViewById(TV_MENU_SERVICE_MODE_MENU_SYSTEM_SETTINGS);
        systemSettings.setText(R.string.button_text_view);
        panelSettings = (A4TVButton) findViewById(TV_MENU_SERVICE_MODE_MENU_PANEL_SETTINGS);
        panelSettings.setText(R.string.button_text_view);
        videoSettings = (A4TVButton) findViewById(TV_MENU_SERVICE_MODE_MENU_VIDEO_SETTINGS);
        videoSettings.setText(R.string.button_text_view);
        soundSettings = (A4TVButton) findViewById(TV_MENU_SERVICE_MODE_MENU_SOUND_SETTINGS);
        soundSettings.setText(R.string.button_text_view);
        debuggingData = (A4TVButton) findViewById(TV_MENU_SERVICE_MODE_MENU_DEBUGGING_DATA);
        debuggingData.setText(R.string.button_text_view);
        saveSettingsToUSB = (A4TVButton) findViewById(TV_MENU_SERVICE_MODE_SAVE_SETTINGS_TO_USB);
        saveSettingsToUSB.setText(R.string.button_text_ok);
        loadSettingsFromUSB = (A4TVButton) findViewById(TV_MENU_SERVICE_MODE_LOAD_SETTINGS_FROM_USB);
        loadSettingsFromUSB.setText(R.string.button_text_ok);
        alert = new A4TVPasswordDialog(ctx, true);
        alert.setCancelable(true);
        editText1 = alert.getEditText1();
        alert.getEditText2().setVisibility(View.GONE);
        alert.getEditText3().setVisibility(View.GONE);
    }

    @Override
    public void show() {
        init();
        MainActivity.sharedPrefs.edit()
                .putBoolean(MainActivity.SERVICE_MODE_START, true).commit();
        super.show();
    }

    @Override
    public void onClick(View v) {
        String settingsPathSource;
        String settingsPathDest;
        File fSettingsPathSource;
        String packageName = ctx.getPackageName();
        File fSettingsPathDest;
        switch (v.getId()) {
            case TV_MENU_SERVICE_MODE_MENU_SYSTEM_SETTINGS: {
                SystemSettingsDialog sysSettingsDialog = MainActivity.activity
                        .getDialogManager().getSystemSettingsDialog();
                if (sysSettingsDialog != null) {
                    sysSettingsDialog.show();
                }
                break;
            }
            case TV_MENU_SERVICE_MODE_MENU_SOUND_SETTINGS: {
                ServiceSoundDialog servSoundDialog = MainActivity.activity
                        .getDialogManager().getServiceSoundDialog();
                if (servSoundDialog != null) {
                    servSoundDialog.show();
                }
                break;
            }
            case TV_MENU_SERVICE_MODE_MENU_DEBUGGING_DATA: {
                DebuggingDataDialog ddDialog = MainActivity.activity
                        .getDialogManager().getDebuggingDataDialog();
                if (ddDialog != null) {
                    ddDialog.show();
                }
                break;
            }
            case TV_MENU_SERVICE_MODE_MENU_PANEL_SETTINGS: {
                break;
            }
            case TV_MENU_SERVICE_MODE_SAVE_SETTINGS_TO_USB: {
                try {
                    String mountPath = "../..";
                    if (storage != null) {
                        A4TVUSBStorage usbStorage = storage.getUSBStorage(0);
                        if (usbStorage != null) {
                            mountPath = mountPath.concat(usbStorage
                                    .getPartitionMountPath(0));
                            MainActivity.service.getSetupControl()
                                    .saveSettingsToUSB(mountPath);
                            /* Save shared prefs to USB */
                            settingsPathSource = "/data/data/" + packageName
                                    + "/shared_prefs/" + "myPrefs.xml";
                            settingsPathDest = mountPath + "/myPrefs.xml";
                            fSettingsPathSource = new File(settingsPathSource);
                            if (fSettingsPathSource.exists()) {
                                Log.d(TAG, "Source file " + fSettingsPathSource
                                        + " exists");
                                fSettingsPathDest = new File(settingsPathDest);
                                if (!fSettingsPathDest.exists()) {
                                    Log.d(TAG, "Destination file "
                                            + fSettingsPathDest
                                            + " does not exist, create it");
                                    try {
                                        fSettingsPathDest.createNewFile();
                                    } catch (IOException e) {
                                        Log.e("Error copy file: ",
                                                e.getMessage());
                                    }
                                }
                                if (fSettingsPathDest.exists()) {
                                    Log.d(TAG, "Destination file "
                                            + fSettingsPathDest + " exists");
                                    /* copy file */
                                    try {
                                        copyFile(fSettingsPathSource,
                                                fSettingsPathDest);
                                    } catch (IOException e) {
                                        Log.e("Error copy file: ",
                                                e.getMessage());
                                    }
                                } else {
                                    Log.d(TAG, "Destination file "
                                            + fSettingsPathDest
                                            + " cannot be created");
                                }
                            } else {
                                Log.d(TAG,
                                        "Source file "
                                                + fSettingsPathSource
                                                + " does not exist, cannot be saved to USB");
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SERVICE_MODE_LOAD_SETTINGS_FROM_USB: {
                try {
                    String mountPath = "../..";
                    if (storage != null) {
                        A4TVUSBStorage usbStorage = storage.getUSBStorage(0);
                        if (usbStorage != null) {
                            mountPath = mountPath.concat(usbStorage
                                    .getPartitionMountPath(0));
                            MainActivity.service.getSetupControl()
                                    .loadSettingsFromUSB(mountPath);
                            /* Load shared prefs from USB */
                            settingsPathSource = mountPath + "/myPrefs.xml";
                            settingsPathDest = "/data/data/" + packageName
                                    + "/shared_prefs/" + "myPrefs.xml";
                            fSettingsPathSource = new File(settingsPathSource);
                            if (fSettingsPathSource.exists()) {
                                Log.d(TAG, "Source file " + fSettingsPathSource
                                        + " exists");
                                fSettingsPathDest = new File(settingsPathDest);
                                if (!fSettingsPathDest.exists()) {
                                    Log.d(TAG, "Destination file "
                                            + fSettingsPathDest
                                            + " does not exist, create it");
                                    try {
                                        fSettingsPathDest.createNewFile();
                                    } catch (IOException e) {
                                        Log.e("Error copy file: ",
                                                e.getMessage());
                                    }
                                }
                                if (fSettingsPathDest.exists()) {
                                    Log.d(TAG, "Destination file "
                                            + fSettingsPathDest + " exists");
                                    /* copy file */
                                    try {
                                        copyFile(fSettingsPathSource,
                                                fSettingsPathDest);
                                    } catch (IOException e) {
                                        Log.e("Error copy file: ",
                                                e.getMessage());
                                    }
                                } else {
                                    Log.d(TAG, "Destination file "
                                            + fSettingsPathDest
                                            + " cannot be created");
                                }
                            } else {
                                Log.d(TAG,
                                        "Source file "
                                                + fSettingsPathSource
                                                + " does not exist, cannot be load from USB");
                            }
                            Log.e(TAG, "reboot-start");
                            MainActivity.service.getSetupControl().rebootTV();
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
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
        titleIDs.add(R.string.tv_menu_service_mode_menu);
        // system settings******************************************
        ArrayList<Integer> list;
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_system_settings);
        list.add(TV_MENU_SERVICE_MODE_MENU_SYSTEM_SETTINGS);
        contentListIDs.add(list);
        // panel settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_panel_settings);
        list.add(TV_MENU_SERVICE_MODE_MENU_PANEL_SETTINGS);
        contentListIDs.add(list);
        // video settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_video_settings);
        list.add(TV_MENU_SERVICE_MODE_MENU_VIDEO_SETTINGS);
        contentListIDs.add(list);
        // system setting******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_sound_settings);
        list.add(TV_MENU_SERVICE_MODE_MENU_SOUND_SETTINGS);
        contentListIDs.add(list);
        // system setting******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_debugging_data);
        list.add(TV_MENU_SERVICE_MODE_MENU_DEBUGGING_DATA);
        contentListIDs.add(list);
        // system setting******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_save_settings_to_USB);
        list.add(TV_MENU_SERVICE_MODE_SAVE_SETTINGS_TO_USB);
        contentListIDs.add(list);
        // system setting******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_load_settings_from_USB);
        list.add(TV_MENU_SERVICE_MODE_LOAD_SETTINGS_FROM_USB);
        contentListIDs.add(list);
    }
}
