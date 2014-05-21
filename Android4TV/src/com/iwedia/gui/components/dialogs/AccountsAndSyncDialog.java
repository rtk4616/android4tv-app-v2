package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.comm.system.account.IAccountSyncSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

public class AccountsAndSyncDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons in this dialog */
    public static final int TV_MENU_ACCOUNT_SETTINGS_AUTO_SYNC = 34,
            TV_MENU_ACCOUNT_SETTINGS_MANAGE_ACCOUNTS = 35,
            TV_MENU_ACCOUNT_SETTINGS_ADD_ACCOUNT = 36;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonFirstInstallNext, buttonAddAccount,
            buttonManageAccounts;
    private A4TVButtonSwitch buttonAutoSync;

    public AccountsAndSyncDialog(Context context) {
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
    public void show() {
        fillViews();
        super.show();
    }

    private void init() {
        buttonAutoSync = (A4TVButtonSwitch) findViewById(TV_MENU_ACCOUNT_SETTINGS_AUTO_SYNC);
        buttonManageAccounts = (A4TVButton) findViewById(TV_MENU_ACCOUNT_SETTINGS_MANAGE_ACCOUNTS);
        buttonAddAccount = (A4TVButton) findViewById(TV_MENU_ACCOUNT_SETTINGS_ADD_ACCOUNT);
    }

    private void fillViews() {
        IAccountSyncSettings accoutsSettings = null;
        try {
            accoutsSettings = MainActivity.service.getSystemControl()
                    .getAccountSyncControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (accoutsSettings != null) {
            /*********************** AUTO SYNC **********************/
            boolean isAutoSync = false;
            try {
                isAutoSync = accoutsSettings.isAutoSync();
            } catch (Exception e) {
                e.printStackTrace();
            };
            if (isAutoSync) {
                buttonAutoSync.setSelectedStateAndText(true,
                        R.string.button_text_on);
            } else {
                buttonAutoSync.setSelectedStateAndText(false,
                        R.string.button_text_off);
            }
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

    /**
     * This is called when a button is clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_ACCOUNT_SETTINGS_AUTO_SYNC: {
                if (buttonAutoSync.isSelected()) {
                    try {
                        MainActivity.service.getSystemControl()
                                .getAccountSyncControl().setAutoSync(false);
                        buttonAutoSync.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        MainActivity.service.getSystemControl()
                                .getAccountSyncControl().setAutoSync(true);
                        buttonAutoSync.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_ACCOUNT_SETTINGS_MANAGE_ACCOUNTS: {
                AccountsAndSyncManageAccountsDialog asmDialog = MainActivity.activity
                        .getDialogManager()
                        .getAccountsAndSyncManageAccountsDialog();
                if (asmDialog != null) {
                    asmDialog.show();
                }
                break;
            }
            case TV_MENU_ACCOUNT_SETTINGS_ADD_ACCOUNT: {
                AccountsAndSyncAddAccountDialog accDialog = MainActivity.activity
                        .getDialogManager()
                        .getAccountsAndSyncAddAccountDialog();
                if (accDialog != null) {
                    accDialog.show();
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
        titleIDs.add(R.string.tv_menu_account_settings);
        // auto sync******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_account_settings_auto_sync);
        list.add(TV_MENU_ACCOUNT_SETTINGS_AUTO_SYNC);
        contentListIDs.add(list);
        // manage account******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_account_settings_manage_accounts);
        list.add(TV_MENU_ACCOUNT_SETTINGS_MANAGE_ACCOUNTS);
        contentListIDs.add(list);
        // add account******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_account_settings_add_account);
        list.add(TV_MENU_ACCOUNT_SETTINGS_ADD_ACCOUNT);
        contentListIDs.add(list);
    }
}
