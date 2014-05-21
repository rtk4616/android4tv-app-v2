package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * File storage dialog
 * 
 * @author Branimir Pavlovic
 */
public class ExternalAndLocalStorageDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons on this dialogs */
    public static final int TV_MENU_STORAGE_SETTINGS_EXTERNAL_FILE_STORAGE = 20,
            TV_MENU_STORAGE_SETTINGS_LOCAL_FILE_STORAGE = 21,
            TV_MENU_STORAGE_SETTINGS_FACTORY_DATA_RESET = 22;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;

    public ExternalAndLocalStorageDialog(Context context) {
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

    @Override
    public void show() {
        setInitialViews();
        super.show();
    }

    /** Set initial set up of views */
    public void setInitialViews() {
        /************* External storage *************/
        String available = "", total = "";
        IExternalLocalStorageSettings storageSettings = null;
        try {
            storageSettings = MainActivity.service.getSystemControl()
                    .getExternalLocalStorageControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (storageSettings != null) {
            try {
                available = storageSettings.getExternalStorageAvailableSpace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                total = storageSettings.getExternalStorageTotalSpace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            A4TVButton btn = (A4TVButton) findViewById(TV_MENU_STORAGE_SETTINGS_EXTERNAL_FILE_STORAGE);
            StringBuilder builder = new StringBuilder();
            btn.setText(builder
                    .append(ctx.getResources().getString(R.string.available))
                    .append(" ").append(available).append("/").append(total));
        } else {
            A4TVButton btn = (A4TVButton) findViewById(TV_MENU_STORAGE_SETTINGS_EXTERNAL_FILE_STORAGE);
            btn.setText(ctx.getResources().getString(R.string.unknown));
        }
        /************* Local storage *************/
        if (storageSettings != null) {
            try {
                available = storageSettings.getLocalStorageAvailableSpace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                total = storageSettings.getLocalStorageTotalSpace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            A4TVButton btn = (A4TVButton) findViewById(TV_MENU_STORAGE_SETTINGS_LOCAL_FILE_STORAGE);
            StringBuilder builder = new StringBuilder();
            btn.setText(builder
                    .append(ctx.getResources().getString(R.string.available))
                    .append(" ").append(available).append("/").append(total));
        } else {
            A4TVButton btn = (A4TVButton) findViewById(TV_MENU_STORAGE_SETTINGS_LOCAL_FILE_STORAGE);
            btn.setText(ctx.getResources().getString(R.string.unknown));
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
        titleIDs.add(R.string.tv_menu_storage_settings);
        // external file storage******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_storage_settings_external_file_storage);
        list.add(TV_MENU_STORAGE_SETTINGS_EXTERNAL_FILE_STORAGE);
        contentListIDs.add(list);
        // local file storage******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_storage_settings_local_file_storage);
        list.add(TV_MENU_STORAGE_SETTINGS_LOCAL_FILE_STORAGE);
        contentListIDs.add(list);
        // factory data reset******************************************
        // list = new ArrayList<Integer>();
        // list.add(MainMenuContent.TAGA4TVTextView);
        // list.add(MainMenuContent.TAGA4TVButton);
        // contentList.add(list);
        //
        // list = new ArrayList<Integer>();
        // list.add(R.string.tv_menu_storage_settings_factory_data_reset);
        // list.add(TV_MENU_STORAGE_SETTINGS_FACTORY_DATA_RESET);
        // contentListIDs.add(list);
    }
}
