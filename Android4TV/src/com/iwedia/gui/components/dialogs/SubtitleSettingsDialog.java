package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.comm.ISubtitleControl;
import com.iwedia.dtv.subtitle.SubtitleMode;
import com.iwedia.dtv.subtitle.SubtitleType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Subtitle settings dialog
 * 
 * @author Branimir Pavlovic
 */
public class SubtitleSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    // public static final int tv_menu_subtitle_settings_subtitle_mode = 6;
    public static final int TV_MENU_SUBTITLE_SETTINGS_ON_OFF = 6;
    /** IDs for spinners */
    public static final int TV_MENU_SUBTITLE_SETTINGS_TELETEXT_LANGUAGE = 16,
            TV_MENU_SUBTITLE_SETTINGS_EPG_LANGUAGE = 17,
            TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_FIRST_LANGUAGE = 18,
            TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_SECOND_LANGUAGE = 666,
            TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_MODE = 328,
            TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_TYPE = 841;
    /** IDs for buttons */
    public static final int TV_MENU_SUBTITLE_SETTINGS_SET_DEFAULT_SETTINGS = 1;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButtonSwitch subtitlesEnabledSwitch;
    private A4TVSpinner subtitleFirst, subtitleSecond, subtitleMode,
            subtitleType;
    private A4TVButton buttonSetDefaultSettings;

    public SubtitleSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        // just init views
        subtitlesEnabledSwitch = (A4TVButtonSwitch) findViewById(TV_MENU_SUBTITLE_SETTINGS_ON_OFF);
        subtitleFirst = (A4TVSpinner) findViewById(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_FIRST_LANGUAGE);
        subtitleSecond = (A4TVSpinner) findViewById(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_SECOND_LANGUAGE);
        subtitleMode = (A4TVSpinner) findViewById(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_MODE);
        subtitleType = (A4TVSpinner) findViewById(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_TYPE);
        buttonSetDefaultSettings = (A4TVButton) findViewById(TV_MENU_SUBTITLE_SETTINGS_SET_DEFAULT_SETTINGS);
        buttonSetDefaultSettings
                .setText(com.iwedia.gui.R.string.button_text_ok);
        // set listeners for spinner
        subtitleFirst
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getSubtitleControl()
                                    .setFirstSubtitleLanguage(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        subtitleSecond
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getSubtitleControl()
                                    .setSecondSubtitleLanguage(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        subtitleMode
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getSubtitleControl()
                                    .setSubtitleMode(
                                            SubtitleMode.values()[index]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        subtitleType
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getSubtitleControl()
                                    .setSubtitleType(
                                            SubtitleType.values()[index]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    private void fillViews() {
        ISubtitleControl subtitleControl = null;
        try {
            subtitleControl = MainActivity.service.getSubtitleControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (subtitleControl != null) {
            boolean subState = false;
            try {
                subState = subtitleControl.getSubtitleEnabled();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (subState) {
                subtitlesEnabledSwitch.setSelectedStateAndText(true,
                        com.iwedia.gui.R.string.button_text_on);
            } else {
                subtitlesEnabledSwitch.setSelectedStateAndText(false,
                        com.iwedia.gui.R.string.button_text_off);
            }
            /*********************** FIRST SUBTITLE LANGUAGE **************************/
            int first = 0;
            try {
                first = subtitleControl.getFirstSubtitleLanguage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            subtitleFirst.setSelection(first);
            /*********************** SECOND SUBTITLE LANGUAGE **************************/
            int second = 0;
            try {
                second = subtitleControl.getSecondSubtitleLanguage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            subtitleSecond.setSelection(second);
            /*********************** SUBTITLE MODE **************************/
            int mode = 0;
            try {
                mode = subtitleControl.getSubtitleMode().getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            subtitleMode.setSelection(mode);
            /*********************** SUBTITLE TYPE **************************/
            int type = 0;
            try {
                type = subtitleControl.getSubtitleType().getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            subtitleType.setSelection(type);
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
                new int[] { com.iwedia.gui.R.attr.A4TVDialog });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case TV_MENU_SUBTITLE_SETTINGS_ON_OFF: {
                if (subtitlesEnabledSwitch.isSelected()) {
                    try {
                        ISubtitleControl subtitleControl = MainActivity.service
                                .getSubtitleControl();
                        subtitleControl.setSubtitleEnabled(false);
                        subtitleControl.hide();
                        subtitlesEnabledSwitch.setSelectedStateAndText(false,
                                com.iwedia.gui.R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        ISubtitleControl subtitleControl = MainActivity.service
                                .getSubtitleControl();
                        subtitleControl.setSubtitleEnabled(true);
                        int trackCount = subtitleControl
                                .getSubtitleTrackCount();
                        if (trackCount > 0) {
                            if (subtitleControl.getCurrentSubtitleTrackIndex() == -1) {
                                int i, index = -1;
                                String trackLanguage;
                                String firstSubtitleLanguage = MainActivity.service
                                        .getSetupControl()
                                        .getLanguageName(
                                                subtitleControl
                                                        .getFirstSubtitleLanguage());
                                String secondSubtitleLanguage = MainActivity.service
                                        .getSetupControl()
                                        .getLanguageName(
                                                subtitleControl
                                                        .getSecondSubtitleLanguage());
                                for (i = 0; i < trackCount; i++) {
                                    trackLanguage = subtitleControl
                                            .getSubtitleTrack(i);
                                    if (trackLanguage
                                            .equals(firstSubtitleLanguage)) {
                                        index = i;
                                        break;
                                    } else if (trackLanguage
                                            .equals(secondSubtitleLanguage)) {
                                        index = i;
                                    }
                                }
                                if (index != -1) {
                                    subtitleControl
                                            .setCurrentSubtitleTrack(index);
                                } else {
                                    subtitleControl.setCurrentSubtitleTrack(0);
                                }
                            }
                        }
                        subtitlesEnabledSwitch.setSelectedStateAndText(true,
                                com.iwedia.gui.R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SUBTITLE_SETTINGS_SET_DEFAULT_SETTINGS: {
                try {
                    ISubtitleControl subtitleControl = MainActivity.service
                            .getSubtitleControl();
                    subtitleControl.resetSubtitleSettings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.show();
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
        titleIDs.add(com.iwedia.gui.R.drawable.settings_icon);
        titleIDs.add(com.iwedia.gui.R.drawable.tv_menu_icon);
        titleIDs.add(com.iwedia.gui.R.string.tv_menu_subtitle_settings);
        // subtitle mode******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_subtitle_settings_subtitle_enabled);
        list.add(TV_MENU_SUBTITLE_SETTINGS_ON_OFF);
        contentListIDs.add(list);
        // subtitle language******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_subtitle_settings_subtitle_language);
        list.add(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_FIRST_LANGUAGE);
        contentListIDs.add(list);
        // subtitle second language******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_subtitle_settings_subtitle_second_language);
        list.add(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_SECOND_LANGUAGE);
        contentListIDs.add(list);
        // subtitle mode******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_subtitle_settings_subtitle_mode);
        list.add(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_MODE);
        contentListIDs.add(list);
        // subtitle type******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_subtitle_settings_subtitle_type);
        list.add(TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_TYPE);
        contentListIDs.add(list);
        // subtitle set default settings
        // ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_sound_settings_set_default_settings);
        list.add(TV_MENU_SUBTITLE_SETTINGS_SET_DEFAULT_SETTINGS);
        contentListIDs.add(list);
    }
}
