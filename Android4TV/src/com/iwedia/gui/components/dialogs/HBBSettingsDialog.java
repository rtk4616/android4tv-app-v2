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
 * HBB settings dialog
 * 
 * @author bane
 */
public class HBBSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int tv_menu_hbb_settings_hbb_tv_enable = 5;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButtonSwitch hbbButton;

    public HBBSettingsDialog(Context context) {
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

    private void init() {
        hbbButton = (A4TVButtonSwitch) findViewById(tv_menu_hbb_settings_hbb_tv_enable);
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    /** Fill views with data */
    private void fillViews() {
        boolean isHbbEnabled = false;
        try {
            isHbbEnabled = MainActivity.service.getHbbTvControl()
                    .isHbbEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isHbbEnabled) {
            hbbButton.setSelectedStateAndText(true, R.string.button_text_yes);
        } else {
            hbbButton.setSelectedStateAndText(false, R.string.button_text_no);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case tv_menu_hbb_settings_hbb_tv_enable: {
                if (v.isSelected()) {
                    try {
                        MainActivity.service.getHbbTvControl().disableHBB();
                        MainActivity.webDialog.getHbbTVView().setAlpha(
                                (float) 0.00);
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                                R.string.button_text_no);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        MainActivity.service.getHbbTvControl().enableHBB();
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                                R.string.button_text_yes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.string.tv_menu_hbb_settings);
        // hbb tv enable******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_hbb_settings_hbb_tv_enable);
        list.add(tv_menu_hbb_settings_hbb_tv_enable);
        contentListIDs.add(list);
    }
}
