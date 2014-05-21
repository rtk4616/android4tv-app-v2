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
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Product info dialog
 * 
 * @author Sasa Jagodin
 */
public class ServiceSoundDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_SOUND_SETTINGS_VOLUME_FIXED = 1,
            TV_MENU_SOUND_SETTINGS_COMMIT = 4;
    /** IDs for edit text in this dialog */
    public static final int TV_MENU_SOUND_SETTINGS_MAXIMUM_VOLUME = 2,
            TV_MENU_SOUND_SETTINGS_VOLUME_FIXED_LEVEL = 3;
    /** Switch buttons */
    private A4TVButtonSwitch volumeFixedButton;
    private A4TVButton commitButton;
    private A4TVEditText maxVolumeEdit, fixedLevelEdit;
    private IServiceMode serviceMode;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();

    public ServiceSoundDialog(Context context) {
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
        maxVolumeEdit = (A4TVEditText) findViewById(TV_MENU_SOUND_SETTINGS_MAXIMUM_VOLUME);
        volumeFixedButton = (A4TVButtonSwitch) findViewById(TV_MENU_SOUND_SETTINGS_VOLUME_FIXED);
        fixedLevelEdit = (A4TVEditText) findViewById(TV_MENU_SOUND_SETTINGS_VOLUME_FIXED_LEVEL);
        commitButton = (A4TVButton) findViewById(TV_MENU_SOUND_SETTINGS_COMMIT);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
        serviceMode = null;
        commitButton.setText(R.string.button_text_ok);
        try {
            serviceMode = MainActivity.service.getServiceMode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serviceMode != null) {
            String fixedLevelString;
            int fixedLevel;
            try {
                fixedLevel = serviceMode.getVolumeFixedLevel();
                if (fixedLevel > 0) {
                    fixedLevelString = String.valueOf(fixedLevel);
                    fixedLevelEdit.setText(fixedLevelString);
                    fixedLevelEdit.setSelection(fixedLevelString.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String maxVolumeString;
            int maxVolume;
            try {
                maxVolume = serviceMode.getMaxVolume();
                if (maxVolume > 0) {
                    maxVolumeString = String.valueOf(maxVolume);
                    maxVolumeEdit.setText(maxVolumeString);
                    maxVolumeEdit.setSelection(maxVolumeString.length());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean isVolumeFixed = false;
            try {
                isVolumeFixed = serviceMode.getVolumeFixed();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isVolumeFixed) {
                volumeFixedButton.setSelectedStateAndText(true,
                        R.string.button_text_on);
            } else {
                volumeFixedButton.setSelectedStateAndText(false,
                        R.string.button_text_off);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_SOUND_SETTINGS_VOLUME_FIXED: {
                if (volumeFixedButton.isSelected()) {
                    try {
                        serviceMode.setVolumeFixed(false);
                        volumeFixedButton.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        serviceMode.setVolumeFixed(true);
                        volumeFixedButton.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_COMMIT: {
                try {
                    /*
                     * if entered volume bigger than max allowed volume set to
                     * max
                     */
                    if (Integer.valueOf(maxVolumeEdit.getText().toString()) > 100) {
                        maxVolumeEdit.setText("100");
                        serviceMode.setMaxVolume(100);
                    } else {
                        serviceMode.setMaxVolume(Integer.valueOf(maxVolumeEdit
                                .getText().toString()));
                    }
                    /*
                     * if entered volume bigger than max allowed volume set to
                     * max
                     */
                    if (Integer.valueOf(fixedLevelEdit.getText().toString()) > 100) {
                        fixedLevelEdit.setText("100");
                        serviceMode.setVolumeFixedLevel(100);
                    } else {
                        serviceMode.setVolumeFixedLevel(Integer
                                .valueOf(fixedLevelEdit.getText().toString()));
                    }
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
        titleIDs.add(R.string.tv_menu_service_mode_menu_sound_settings);
        ArrayList<Integer> list;
        // program number******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_maximum_volume);
        list.add(TV_MENU_SOUND_SETTINGS_MAXIMUM_VOLUME);
        contentListIDs.add(list);
        // on screen display******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_volume_fixed);
        list.add(TV_MENU_SOUND_SETTINGS_VOLUME_FIXED);
        contentListIDs.add(list);
        // program number******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_volume_fixed_level);
        list.add(TV_MENU_SOUND_SETTINGS_VOLUME_FIXED_LEVEL);
        contentListIDs.add(list);
        // commit ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_service_mode_menu_commit);
        list.add(TV_MENU_SOUND_SETTINGS_COMMIT);
        contentListIDs.add(list);
    }
}
