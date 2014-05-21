package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.enums.FontScale;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Language and keyboard dialog
 * 
 * @author Branimir Pavlovic
 */
public class LanguageAndKeyboardDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for spinner */
    public static final int TV_MENU_LANGUAGE_SETTINGS_SELECT_LANGUAGE = 6,
            TV_MENU_LANGUAGE_SETTINGS_SELECT_COUNTRY = 7,
            TV_MENU_LANGUAGE_SETTINGS_AUDIO_LANGUAGEa = 8,
            TV_MENU_LANGUAGE_SETTINGS_TEXT_SIZE = 56;
    /** IDs for buttons */
    public static final int TV_MENU_LANGUAGE_SETTINGS_KEYBOARD_SETTINGS = 25,
            FIRST_TIME_INSTALL_NEXT_BUTTON = 34;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonFirstInstallNext;
    private A4TVSpinner mSpinnerTextSize, mSpinnerSelectCountry,
            mSpinnerSelectLanguage;

    public LanguageAndKeyboardDialog(final Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        // find spinners
        mSpinnerTextSize = ((A4TVSpinner) findViewById(TV_MENU_LANGUAGE_SETTINGS_TEXT_SIZE));
        mSpinnerSelectCountry = ((A4TVSpinner) findViewById(TV_MENU_LANGUAGE_SETTINGS_SELECT_COUNTRY));
        mSpinnerSelectLanguage = (A4TVSpinner) findViewById(TV_MENU_LANGUAGE_SETTINGS_SELECT_LANGUAGE);
        // set listeners
        mSpinnerTextSize
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, final int index,
                            String[] contents) {
                        Log.d(TAG, "Choosen from text size: " + contents[index]);
                        MainActivity.stopVideoOnPauseAndReturnMenuToUser = false;
                        A4TVToast toast = new A4TVToast(context);
                        toast.showToast(R.string.app_recreate);
                        Handler delay = new Handler();
                        delay.postDelayed(new Runnable() {
                            public void run() {
                                switch (index) {
                                    case 0: {
                                        try {
                                            MainActivity.service
                                                    .getSystemControl()
                                                    .getLanguageAndKeyboardControl()
                                                    .setFontScale(
                                                            FontScale.SMALL);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        } catch (RuntimeException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                    case 1: {
                                        try {
                                            MainActivity.service
                                                    .getSystemControl()
                                                    .getLanguageAndKeyboardControl()
                                                    .setFontScale(
                                                            FontScale.NORMAL);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        } catch (RuntimeException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                    case 2: {
                                        try {
                                            MainActivity.service
                                                    .getSystemControl()
                                                    .getLanguageAndKeyboardControl()
                                                    .setFontScale(
                                                            FontScale.LARGE);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        } catch (RuntimeException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                    case 3: {
                                        try {
                                            MainActivity.service
                                                    .getSystemControl()
                                                    .getLanguageAndKeyboardControl()
                                                    .setFontScale(
                                                            FontScale.HUGE);
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
                        }, 500);
                    }
                });
        mSpinnerSelectCountry
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        Log.d(TAG, "Choosen from select country: "
                                + contents[index] + ", INDEX: " + index);
                        try {
                            MainActivity.service.getSetupControl().setCountry(
                                    index);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                });
        mSpinnerSelectLanguage
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        Log.d(TAG, "Choosen from select language: "
                                + contents[index]);
                        // ////////////////////////////////
                        // Veljko Ilkic
                        // ////////////////////////////////
                        MainActivity.stopVideoOnPauseAndReturnMenuToUser = false;
                        // ////////////////////////////////
                        // Veljko Ilkic
                        // ////////////////////////////////
                        A4TVToast toast = new A4TVToast(context);
                        toast.showToast(R.string.app_recreate);
                        Handler delay = new Handler();
                        final String optionText = contents[index];
                        delay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MainActivity.service.getSystemControl()
                                            .getLanguageAndKeyboardControl()
                                            .setActiveLanguage(optionText);
                                    if (MainActivity.isInFirstTimeInstall) {
                                        MainActivity.sharedPrefs
                                                .edit()
                                                .putBoolean(
                                                        MainActivity.IS_FIRST_TIME_LOADED_FOR_LANGUAGE_CHANGE,
                                                        true).commit();
                                    }
                                    // return state to clean screen
                                    MainKeyListener
                                            .setAppState(MainKeyListener.CLEAN_SCREEN);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 500);
                    }
                });
    }

    @Override
    public void show() {
        init();
        setInitialViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        if (MainActivity.isInFirstTimeInstall) {
            return;
        }
        super.onBackPressed();
    }

    private void init() {
        if (!MainActivity.isInFirstTimeInstall) {
            // hide first time install next button
            findViewById(R.string.first_time_install_next).setVisibility(
                    View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 3).setVisibility(
                    View.GONE);
        } else {
            buttonFirstInstallNext = (A4TVButton) findViewById(FIRST_TIME_INSTALL_NEXT_BUTTON);
            buttonFirstInstallNext
                    .setText(R.string.first_time_install_next_button_text);
            if (MainActivity.activity.getFirstTimeInfoText() != null) {
                MainActivity.activity.getFirstTimeInfoText().setText(
                        R.string.first_time_install_set_language_and_country);
            }
        }
        // hide keyboard settings when app is STB
        if (!ConfigHandler.TV_FEATURES) {
            findViewById(R.string.tv_menu_language_settings_keyboard_settings)
                    .setVisibility(View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 2).setVisibility(
                    View.GONE);
        }
    }

    /** Set initial set up of views */
    private void setInitialViews() {
        /************* Select country *************/
        int what = 0;
        try {
            what = MainActivity.service.getSetupControl().getActiveCountry();
            MainActivity.service.getSetupControl().setCountry(what);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((A4TVSpinner) findViewById(TV_MENU_LANGUAGE_SETTINGS_SELECT_COUNTRY))
                .setSelection(what);
        /************* Select language *************/
        int indexLanguage = 0;
        try {
            indexLanguage = MainActivity.service.getSystemControl()
                    .getLanguageAndKeyboardControl().getActiveLanguageIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((A4TVSpinner) findViewById(TV_MENU_LANGUAGE_SETTINGS_SELECT_LANGUAGE))
                .setSelection(indexLanguage);
        /************* Text size *************/
        float fontScale = 1.0f;
        try {
            fontScale = MainActivity.service.getSystemControl()
                    .getLanguageAndKeyboardControl().getActiveFontScale();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int index = 0;
        if (fontScale == FontScale.NORMAL) {
            index = 1;
        }
        if (fontScale == FontScale.LARGE) {
            index = 2;
        }
        if (fontScale == FontScale.HUGE) {
            index = 3;
        }
        ((A4TVSpinner) findViewById(TV_MENU_LANGUAGE_SETTINGS_TEXT_SIZE))
                .setSelection(index);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case FIRST_TIME_INSTALL_NEXT_BUTTON: {
                PictureSettingsDialog picSettingsDialog = MainActivity.activity
                        .getDialogManager().getPictureSettingsDialog();
                if (picSettingsDialog != null) {
                    picSettingsDialog.show();
                }
                LanguageAndKeyboardDialog.this.cancel();
                break;
            }
            case TV_MENU_LANGUAGE_SETTINGS_KEYBOARD_SETTINGS: {
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
        titleIDs.add(R.string.tv_menu_language_settings_keyboard_settings);
        // select language******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_language_settings_select_language);
        list.add(TV_MENU_LANGUAGE_SETTINGS_SELECT_LANGUAGE);
        contentListIDs.add(list);
        // select country******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_language_settings_select_country);
        list.add(TV_MENU_LANGUAGE_SETTINGS_SELECT_COUNTRY);
        contentListIDs.add(list);
        // audio language******************************************
        // list = new ArrayList<Integer>();
        // list.add(MainMenuContent.TAGA4TVTextView);
        // list.add(MainMenuContent.TAGA4TVSpinner);
        // contentList.add(list);
        //
        // list = new ArrayList<Integer>();
        // list.add(R.string.tv_menu_language_settings_audio_language);
        // list.add(TV_MENU_LANGUAGE_SETTINGS_AUDIO_LANGUAGE);
        // contentListIDs.add(list);
        // keyboard settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_language_settings_keyboard_settings);
        list.add(TV_MENU_LANGUAGE_SETTINGS_KEYBOARD_SETTINGS);
        contentListIDs.add(list);
        // text size******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_language_settings_text_size);
        list.add(TV_MENU_LANGUAGE_SETTINGS_TEXT_SIZE);
        contentListIDs.add(list);
        // first time install next ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.first_time_install_next);
        list.add(FIRST_TIME_INSTALL_NEXT_BUTTON);
        contentListIDs.add(list);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MUTE) {
            if (null != MainActivity.activity.getFirstTimeInstallLayout()) {
                MainActivity.activity.getFirstTimeInstallLayout()
                        .setVisibility(View.GONE);
            }
            MainActivity.getSharedPrefs().edit()
                    .putBoolean(MainActivity.FIRST_TIME_INSTALL, false)
                    .commit();
            MainActivity.isInFirstTimeInstall = false;
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
