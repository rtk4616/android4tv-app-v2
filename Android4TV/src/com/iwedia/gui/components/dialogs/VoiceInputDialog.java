package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Voice input dialog
 * 
 * @author Branimir Pavlovic
 */
public class VoiceInputDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_VOICE_SETTINGS_ALWAYS_USE_MY_SETTINGS = 18,
            TV_MENU_VOICE_SETTINGS_LISTEN_TO_AN_EXAMPLE = 32;
    /** IDs for spinner */
    public static final int TV_MENU_VOICE_SETTINGS_LANGUAGE = 14;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButtonSwitch buttonAlwaysUseMySettings;

    public VoiceInputDialog(Context context) {
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

    private void init() {
        buttonAlwaysUseMySettings = (A4TVButtonSwitch) findViewById(TV_MENU_VOICE_SETTINGS_ALWAYS_USE_MY_SETTINGS);
        buttonAlwaysUseMySettings.setSelectedStateAndText(false,
                R.string.button_text_off);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_VOICE_SETTINGS_ALWAYS_USE_MY_SETTINGS: {
                if (buttonAlwaysUseMySettings.isSelected()) {
                    buttonAlwaysUseMySettings.setSelectedStateAndText(false,
                            R.string.button_text_off);
                } else {
                    buttonAlwaysUseMySettings.setSelectedStateAndText(true,
                            R.string.button_text_on);
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
        titleIDs.add(R.string.tv_menu_voice_settings);
        // listen_to_an_example******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_voice_settings_listen_to_an_example);
        list.add(TV_MENU_VOICE_SETTINGS_LISTEN_TO_AN_EXAMPLE);
        contentListIDs.add(list);
        // always use my settings******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_voice_settings_always_use_my_settings);
        list.add(TV_MENU_VOICE_SETTINGS_ALWAYS_USE_MY_SETTINGS);
        contentListIDs.add(list);
        // language ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_voice_settings_language);
        list.add(TV_MENU_VOICE_SETTINGS_LANGUAGE);
        contentListIDs.add(list);
    }
}
