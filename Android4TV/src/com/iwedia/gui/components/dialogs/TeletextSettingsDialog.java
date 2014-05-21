package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.comm.ITeletextControl;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Subtitle settings dialog
 * 
 * @author Sasa Jagodin
 */
public class TeletextSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for spinners */
    public static final int TV_MENU_TELETEXT_SETTINGS_TELETEXT_FIRST_LANGUAGE = 755,
            TV_MENU_TELETEXT_SETTINGS_TELETEXT_SECOND_LANGUAGE = 756;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVSpinner teletextFirst, teletextSecond;

    public TeletextSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        teletextFirst = (A4TVSpinner) findViewById(TV_MENU_TELETEXT_SETTINGS_TELETEXT_FIRST_LANGUAGE);
        teletextSecond = (A4TVSpinner) findViewById(TV_MENU_TELETEXT_SETTINGS_TELETEXT_SECOND_LANGUAGE);
        // set listeners for spinners
        teletextFirst
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getTeletextControl()
                                    .setFirstTeletextLanguage(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        teletextSecond
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getTeletextControl()
                                    .setSecondTeletextLanguage(index);
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
        ITeletextControl teletextControl = null;
        try {
            teletextControl = MainActivity.service.getTeletextControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*********************** FIRST TELETEXT LANGUAGE **************************/
        int first = 0;
        try {
            if (teletextControl != null) {
                first = teletextControl.getFirstTeletextLanguage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        teletextFirst.setSelection(first);
        /*********************** SECOND TELETEXT LANGUAGE **************************/
        int second = 0;
        try {
            if (teletextControl != null) {
                second = teletextControl.getSecondTeletextLanguage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        teletextSecond.setSelection(second);
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
        titleIDs.add(com.iwedia.gui.R.string.tv_menu_teletext_settings);
        // subtitle mode******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        // teletext first language******************************************
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_teletext_settings_language);
        list.add(TV_MENU_TELETEXT_SETTINGS_TELETEXT_FIRST_LANGUAGE);
        contentListIDs.add(list);
        // teletext second language******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(com.iwedia.gui.R.string.tv_menu_teletext_settings_second_language);
        list.add(TV_MENU_TELETEXT_SETTINGS_TELETEXT_SECOND_LANGUAGE);
        contentListIDs.add(list);
    }
}
