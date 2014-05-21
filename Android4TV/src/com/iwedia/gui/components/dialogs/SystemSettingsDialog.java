package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.view.View;

import com.iwedia.comm.IServiceMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * system settings dialog
 * 
 * @author Sasa Jagodin
 */
public class SystemSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** ID for spinner */
    public static final int TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_START = 423;
    /** IDs for buttons */
    public static final int TV_MENU_SYSTEM_SETTINGS_RC_BUTTON = 1,
            TV_MENU_SYSTEM_SETTINGS_PANEL_BUTTON = 2,
            TV_MENU_SYSTEM_SETTINGS_MENU_BUTTON = 3,
            TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_FIXED = 4,
            TV_MENU_SYSTEM_SETTINGS_RESET = 5,
            TV_MENU_SYSTEM_SETTINGS_COMMIT = 6,
            TV_MENU_SYSTEM_SETTINGS_ON_SCREEN_DISPLAY = 7,
            TV_MENU_SYSTEM_SETTINGS_FACTORY_MODE = 8,
            TV_MENU_SYSTEM_SETTINGS_SET = 9;
    /** IDs for edit text in this dialog */
    public static final int TV_MENU_SYSTEM_SETTINGS_INPUT_TV_MODE_PROGRAM_NUMBER = 23;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton resetButton, commitButton, setButton;
    /** Switch buttons */
    private A4TVButtonSwitch rcButton, pannelButton, menuButton,
            inputModeFixedButton, onScreenDisplayButton, factoryModeButton;
    private A4TVEditText progNumberEditText;
    private A4TVSpinner spinner;
    private IServiceMode serviceMode;

    public SystemSettingsDialog(Context context) {
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
        rcButton = (A4TVButtonSwitch) findViewById(TV_MENU_SYSTEM_SETTINGS_RC_BUTTON);
        pannelButton = (A4TVButtonSwitch) findViewById(TV_MENU_SYSTEM_SETTINGS_PANEL_BUTTON);
        menuButton = (A4TVButtonSwitch) findViewById(TV_MENU_SYSTEM_SETTINGS_MENU_BUTTON);
        inputModeFixedButton = (A4TVButtonSwitch) findViewById(TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_FIXED);
        resetButton = (A4TVButton) findViewById(TV_MENU_SYSTEM_SETTINGS_RESET);
        commitButton = (A4TVButton) findViewById(TV_MENU_SYSTEM_SETTINGS_COMMIT);
        onScreenDisplayButton = (A4TVButtonSwitch) findViewById(TV_MENU_SYSTEM_SETTINGS_ON_SCREEN_DISPLAY);
        factoryModeButton = (A4TVButtonSwitch) findViewById(TV_MENU_SYSTEM_SETTINGS_FACTORY_MODE);
        progNumberEditText = (A4TVEditText) findViewById(TV_MENU_SYSTEM_SETTINGS_INPUT_TV_MODE_PROGRAM_NUMBER);
        spinner = (A4TVSpinner) findViewById(TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_START);
        setButton = (A4TVButton) findViewById(TV_MENU_SYSTEM_SETTINGS_SET);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
        rcButton.setSelectedStateAndText(true, R.string.button_text_on);
        pannelButton.setSelectedStateAndText(true, R.string.button_text_on);
        menuButton.setSelectedStateAndText(true, R.string.button_text_on);
        inputModeFixedButton.setSelectedStateAndText(true,
                R.string.button_text_off);
        resetButton.setText(R.string.button_text_ok);
        commitButton.setText(R.string.button_text_ok);
        onScreenDisplayButton.setSelectedStateAndText(true,
                R.string.button_text_on);
        progNumberEditText.setText("");
        serviceMode = null;
        try {
            serviceMode = MainActivity.service.getServiceMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isFactoryMode = false;
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isFactoryMode) {
            factoryModeButton.setSelectedStateAndText(true,
                    R.string.button_text_on);
        } else {
            factoryModeButton.setSelectedStateAndText(false,
                    R.string.button_text_off);
        }
        int inputModeStart = 0;
        try {
            if (serviceMode != null) {
                inputModeStart = serviceMode.getInputModeStart();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        spinner.setSelection(inputModeStart);
        boolean isInputModeFixed = false;
        try {
            if (serviceMode != null) {
                isInputModeFixed = serviceMode.getInputModeFixed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isInputModeFixed) {
            inputModeFixedButton.setSelectedStateAndText(true,
                    R.string.button_text_on);
        } else {
            inputModeFixedButton.setSelectedStateAndText(false,
                    R.string.button_text_off);
        }
        boolean isOSDOn = false;
        try {
            if (serviceMode != null) {
                isOSDOn = serviceMode.getOnScreenDisplay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isOSDOn) {
            inputModeFixedButton.setSelectedStateAndText(true,
                    R.string.button_text_on);
        } else {
            inputModeFixedButton.setSelectedStateAndText(false,
                    R.string.button_text_off);
        }
        String progNumberString;
        int progNumber;
        try {
            if (serviceMode != null) {
                progNumber = serviceMode.getInputTVProgramNumber();
                if (progNumber >= 0) {
                    progNumberString = String.valueOf(progNumber);
                    progNumberEditText.setText(progNumberString);
                    progNumberEditText.setSelection(progNumberString.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_SYSTEM_SETTINGS_FACTORY_MODE: {
                if (factoryModeButton.isSelected()) {
                    try {
                        MainActivity.service.getSetupControl().factoryMode(
                                false);
                        factoryModeButton.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        MainActivity.service.getSetupControl()
                                .factoryMode(true);
                        factoryModeButton.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_SET: {
                try {
                    serviceMode.setInputTVProgramNumber(Integer
                            .valueOf(progNumberEditText.getText().toString()));
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_START: {
                try {
                    int indexSelected = spinner.getCHOOSEN_ITEM_INDEX();
                    serviceMode.setInputModeStart(indexSelected);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_FIXED: {
                if (inputModeFixedButton.isSelected()) {
                    try {
                        serviceMode.setInputModeFixed(false);
                        inputModeFixedButton.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        serviceMode.setInputModeFixed(true);
                        inputModeFixedButton.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_ON_SCREEN_DISPLAY: {
                if (onScreenDisplayButton.isSelected()) {
                    try {
                        serviceMode.setOnScreenDisplay(false);
                        onScreenDisplayButton.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        serviceMode.setInputModeFixed(true);
                        onScreenDisplayButton.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_RC_BUTTON: {
                if (rcButton.isSelected()) {
                    try {
                        serviceMode.setRCButton(false);
                        rcButton.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        serviceMode.setRCButton(true);
                        rcButton.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_PANEL_BUTTON: {
                if (pannelButton.isSelected()) {
                    try {
                        serviceMode.setPanelButton(false);
                        pannelButton.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        serviceMode.setPanelButton(true);
                        pannelButton.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_MENU_BUTTON: {
                if (menuButton.isSelected()) {
                    try {
                        serviceMode.setMenuButton(false);
                        menuButton.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        serviceMode.setMenuButton(true);
                        menuButton.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_RESET: {
                try {
                    serviceMode.reset();
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SYSTEM_SETTINGS_COMMIT: {
                try {
                    serviceMode.setInputTVProgramNumber(Integer
                            .valueOf(progNumberEditText.getText().toString()));
                    serviceMode.commit();
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
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
    public void cancel() {
        try {
            MainActivity.service.getSetupControl().factoryMode(false);
            factoryModeButton.setSelectedStateAndText(false,
                    R.string.button_text_off);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.cancel();
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
        titleIDs.add(R.string.tv_menu_service_mode_menu_system_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        // rc button******************************************
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_rc_button);
        list.add(TV_MENU_SYSTEM_SETTINGS_RC_BUTTON);
        contentListIDs.add(list);
        // panel button******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_panel_button);
        list.add(TV_MENU_SYSTEM_SETTINGS_PANEL_BUTTON);
        contentListIDs.add(list);
        // menu button******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_menu_button);
        list.add(TV_MENU_SYSTEM_SETTINGS_MENU_BUTTON);
        contentListIDs.add(list);
        // input mode start******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_input_mode_start);
        list.add(TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_START);
        contentListIDs.add(list);
        // menu input mode fixed******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_input_mode_fixed);
        list.add(TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_FIXED);
        contentListIDs.add(list);
        // program number******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_input_tv_mode);
        list.add(TV_MENU_SYSTEM_SETTINGS_INPUT_TV_MODE_PROGRAM_NUMBER);
        contentListIDs.add(list);
        // reset ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_reset);
        list.add(TV_MENU_SYSTEM_SETTINGS_RESET);
        contentListIDs.add(list);
        // commit ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_commit);
        list.add(TV_MENU_SYSTEM_SETTINGS_COMMIT);
        contentListIDs.add(list);
        // on screen display******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_on_screen_display);
        list.add(TV_MENU_SYSTEM_SETTINGS_ON_SCREEN_DISPLAY);
        contentListIDs.add(list);
        // factory mode ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_on_factory_mode);
        list.add(TV_MENU_SYSTEM_SETTINGS_FACTORY_MODE);
        contentListIDs.add(list);
    }
}
