package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Inputs settings dialog
 * 
 * @author Branimir Pavlovic
 */
public class InputDevicesSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int tv_menu_factory_input_settings_hdmi_cec = 24;
    /** IDs for spinner */
    public static final int tv_menu_factory_input_settings_input_labels = 5;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();

    public InputDevicesSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, null); // ,
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
        titleIDs.add(R.string.tv_menu_factory_input_settings);
        // hdmi cec******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_factory_input_settings_hdmi_cec);
        list.add(tv_menu_factory_input_settings_hdmi_cec);
        contentListIDs.add(list);
        // input labels******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_factory_input_settings_input_labels);
        list.add(tv_menu_factory_input_settings_input_labels);
        contentListIDs.add(list);
    }
}
