package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iwedia.comm.content.applications.AppItem;
import com.iwedia.comm.system.application.AppPermission;
import com.iwedia.comm.system.application.AppSizeInfo;
import com.iwedia.comm.system.application.IApplicationDetails;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsAppControlDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    public static final String TAG = "ApplicationsAppControlDialog";
    /** IDs for buttons */
    public static final int TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_FORCE_STOP = 938573,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_UNINSTALL = 938574,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE = 938566,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_TOTAL = 938575,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_APP = 938576,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_DATA = 938578,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_EXTERNAL = 938579,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_CLEAR_DATA = 938580,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE = 938581,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE_CLEAR = 938585,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_DEFAULTS = 938582,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_DEFAULTS_CLEAR = 938583,
            TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_PERMISSIONS = 938584;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    private AppItem appItem;
    private A4TVTextView textViewTitle;
    /** Non active buttons */
    private A4TVButton btnStorage, btnStorageTotal, btnStorageApp,
            btnStorageData, btnStorageExternal, btnCache, btnLAunchByDef,
            btnPermissions;
    /** Active buttons */
    private A4TVButton btnForceStop, btnUninstall, btnClearData, btnClearCache,
            btnClearDefaults;
    private LinearLayout layoutForInflating;
    private AppSizeInfo appSizeInfo;
    private IApplicationDetails appDetails;

    public ApplicationsAppControlDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        appItem = new AppItem();
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        fillInitialViews();
    }

    public void showDialog(AppItem appItem) {
        this.appItem = appItem;
        setViews();
        super.show();
    }

    /** Get references from views and set it */
    private void fillInitialViews() {
        /** Non active views references */
        textViewTitle = (A4TVTextView) findViewById(DialogCreatorClass.CUSTOM_TITLE_ID);
        btnStorage = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE);
        btnStorageTotal = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_TOTAL);
        btnStorageApp = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_APP);
        btnStorageData = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_DATA);
        btnStorageExternal = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_EXTERNAL);
        btnCache = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE);
        btnLAunchByDef = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_DEFAULTS);
        btnPermissions = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_PERMISSIONS);
        layoutForInflating = (LinearLayout) findViewById(DialogCreatorClass.LAYOUT_FOR_INFLATING);
        /** Active views references */
        btnForceStop = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_FORCE_STOP);
        btnUninstall = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_UNINSTALL);
        btnClearData = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_CLEAR_DATA);
        btnClearCache = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE_CLEAR);
        btnClearDefaults = (A4TVButton) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_DEFAULTS_CLEAR);
        /** Hide buttons and disable focus for some */
        disableBtn(btnStorage, false);
        LinearLayout view = (LinearLayout) findViewById(R.string.tv_menu_applications_settings_manage_applications_storage);
        if (view != null) {
            // view.setBackgroundColor(Color.GRAY);
            view.setPadding(4, 4, 15, 4);
        }
        disableBtn(btnStorageTotal, true);
        disableBtn(btnStorageApp, true);
        disableBtn(btnStorageData, true);
        disableBtn(btnStorageExternal, true);
        disableBtn(btnCache, false);
        view = (LinearLayout) findViewById(R.string.tv_menu_applications_settings_manage_applications_cache);
        if (view != null) {
            // view.setBackgroundColor(Color.GRAY);
            view.setPadding(4, 4, 15, 4);
        }
        disableBtn(btnLAunchByDef, false);
        view = (LinearLayout) findViewById(R.string.tv_menu_applications_settings_manage_applications_default);
        if (view != null) {
            // view.setBackgroundColor(Color.GRAY);
            view.setPadding(4, 4, 15, 4);
        }
        disableBtn(btnPermissions, false);
        view = (LinearLayout) findViewById(R.string.tv_menu_applications_settings_manage_applications_permissions);
        if (view != null) {
            // view.setBackgroundColor(Color.GRAY);
            view.setPadding(4, 4, 15, 4);
        }
        /** Set text to enabled buttons */
        btnForceStop
                .setText(R.string.tv_menu_applications_settings_manage_applications_force_stop);
        btnUninstall
                .setText(R.string.tv_menu_applications_settings_manage_applications_uninstall);
        btnClearData
                .setText(R.string.tv_menu_applications_settings_manage_applications_clear_data);
        btnClearCache
                .setText(R.string.tv_menu_applications_settings_manage_applications_cache_clear);
        btnClearDefaults
                .setText(R.string.tv_menu_applications_settings_manage_applications_default_clear);
    }

    private void disableBtn(A4TVButton btn, boolean focusable) {
        btn.setFocusable(focusable);
        btn.setBackgroundColor(Color.TRANSPARENT);
    }

    /** Set views data from app item */
    public void setViews() {
        if (appItem != null) {
            /****** TITLE *******/
            textViewTitle.setText(appItem.getAppname());
            /****** STRORAGE *******/
            try {
                appDetails = MainActivity.service.getSystemControl()
                        .getApplicationControl()
                        .getApplicationDeatails(appItem.getAppPackage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (appDetails != null) {
                try {
                    appDetails.getAppSizeInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setViewsFromCallBack() {
        Log.d(TAG, "ApplicationsAppControlDialog, SET VIEWS FROM CALLBACK");
        if (appSizeInfo != null) {
            try {
                appDetails = MainActivity.service.getSystemControl()
                        .getApplicationControl()
                        .getApplicationDeatails(appItem.getAppPackage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (appDetails != null) {
                /******** DEFAULTS ***************/
                boolean isDef = true;
                try {
                    isDef = appDetails.isDefault();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "APPLICATION IS DEFAULT" + isDef + "");
                btnClearDefaults.setEnabled(isDef);
                /******* FORCE STOP ****/
                boolean isStopped = true;
                try {
                    isStopped = appDetails.isStopped();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                btnForceStop.setEnabled(!isStopped);
                /******* UNINSTALL ********/
                boolean isSystemApp = true;
                try {
                    isSystemApp = appDetails.isSystem();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                if (isSystemApp) {
                    boolean isEnabled = false;
                    try {
                        isEnabled = appDetails.isEnabled();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    if (isEnabled) {
                        btnUninstall
                                .setText(R.string.tv_menu_applications_settings_manage_applications_disable);
                    } else {
                        btnUninstall
                                .setText(R.string.tv_menu_applications_settings_manage_applications_enable);
                    }
                } else {
                    btnUninstall
                            .setText(R.string.tv_menu_applications_settings_manage_applications_uninstall_btn);
                }
                /******** PERMMISIONS *********/
                fillPermisions();
            }
            /****** STRORAGE TOTAL *******/
            btnStorageTotal.setText(appSizeInfo.getTotalSize());
            /****** STRORAGE APP *******/
            btnStorageApp.setText(appSizeInfo.getCodeSize());
            /****** STRORAGE DATA *******/
            btnStorageData.setText(appSizeInfo.getDataSize());
            Log.d(TAG, "DATA SIZE: " + appSizeInfo.getDataSize());
            /****** STRORAGE SD CARD *******/
            btnStorageExternal.setText(appSizeInfo.getExternalCacheSize());
            /****** CACHE *******/
            btnCache.setText(appSizeInfo.getCacheSize());
            /******** CLEAR CACHE *********/
            btnClearCache.setEnabled(!appSizeInfo.isCacheEmpty());
            /******** CLEAR DATA *********/
            btnClearData.setEnabled(!appSizeInfo.isDataEmpty());
        }
    }

    private void fillPermisions() {
        layoutForInflating.removeAllViews();
        List<AppPermission> permissions = null;
        try {
            permissions = appDetails.getAppPermissions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (permissions != null) {
            // list permissions and create views for each
            for (int i = 0; i < permissions.size(); i++) {
                final LinearLayout smallLayoutHorizontal = new LinearLayout(ctx);
                smallLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                smallLayoutHorizontal
                        .setLayoutParams(new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                MainActivity.dialogListElementHeight));
                smallLayoutHorizontal
                        .setWeightSum(DialogCreatorClass.SMALL_LAYOUT_WEIGHT_SUM);
                smallLayoutHorizontal.setPadding(15, 4, 15, 4);
                /******* TEXT VIEW *******/
                A4TVTextView textView = new A4TVTextView(ctx, null);
                textView.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LayoutParams.WRAP_CONTENT,
                        DialogCreatorClass.ELEMENTS_WEIGHT_BIG));
                textView.setGravity(Gravity.CENTER_VERTICAL);
                // auto scroll text in text view
                textView.setEllipsize(TruncateAt.MARQUEE);
                textView.setSingleLine(true);
                textView.setTextSize(ctx.getResources().getDimension(
                        R.dimen.a4tvdialog_textview_size));
                // set text to text view
                textView.setText(permissions.get(i).getPermissionGroup());
                // add text view to small layout
                smallLayoutHorizontal.addView(textView);
                /*********** BUTTON ************/
                A4TVButton button = new A4TVButton(ctx);
                button.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LayoutParams.MATCH_PARENT,
                        DialogCreatorClass.ELEMENTS_WEIGHT_SMALL));
                // set text to button
                button.setText(permissions.get(i).getDescription());
                button.setTextSize(ctx.getResources().getDimension(
                        R.dimen.a4tvdialog_button_text_size));
                button.setEllipsize(TruncateAt.MARQUEE);
                button.setSingleLine(true);
                button.setBackgroundColor(Color.TRANSPARENT);
                // add focus listener for button
                button.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        // get drawable from theme for small layout
                        // background
                        TypedArray atts = ctx
                                .getTheme()
                                .obtainStyledAttributes(
                                        new int[] { R.attr.LayoutFocusDrawable });
                        int backgroundID = atts.getResourceId(0, 0);
                        if (hasFocus) {
                            smallLayoutHorizontal.getChildAt(0).setSelected(
                                    true);
                            smallLayoutHorizontal
                                    .setBackgroundResource(backgroundID);
                        } else {
                            smallLayoutHorizontal.getChildAt(0).setSelected(
                                    false);
                            smallLayoutHorizontal
                                    .setBackgroundColor(Color.TRANSPARENT);
                        }
                        atts.recycle();
                    }
                });
                // add button to small layout
                smallLayoutHorizontal.addView(button);
                /** Add view to dialog */
                layoutForInflating.addView(smallLayoutHorizontal);
                if (i < permissions.size() - 1) {
                    // create horizontal line
                    ImageView horizLin = new ImageView(ctx);
                    horizLin.setLayoutParams(new LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
                    // get drawable from theme for image source
                    TypedArray att = ctx.getTheme().obtainStyledAttributes(
                            new int[] { R.attr.DialogSmallDividerLine });
                    int src = att.getResourceId(0, 0);
                    horizLin.setBackgroundResource(src);
                    att.recycle();
                    // add horiz line to main layout
                    layoutForInflating.addView(horizLin);
                }
            }
        }
    }

    private void refreshGUI() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setViewsFromCallBack();
            }
        }, 1000);
    }

    /**
     * Click listener for buttons in dialog
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_FORCE_STOP: {
                try {
                    appDetails.forceStop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshGUI();
                break;
            }
            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_UNINSTALL: {
                showAskDialog(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_UNINSTALL);
                break;
            }
            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_CLEAR_DATA: {
                showAskDialog(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_CLEAR_DATA);
                break;
            }
            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE_CLEAR: {
                showAskDialog(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE_CLEAR);
                break;
            }
            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_DEFAULTS_CLEAR: {
                try {
                    appDetails.clearDefaults();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                refreshGUI();
                break;
            }
            default:
                break;
        }
    }

    /**
     * Show ask dialog for user to confirm action
     */
    private void showAskDialog(final int ID) {
        // create ask dialog
        final A4TVAlertDialog askDialog = new A4TVAlertDialog(ctx);
        askDialog.setTitleOfAlertDialog(R.string.ask_massage).setCancelable(
                false);
        askDialog.setPositiveButton(R.string.button_text_yes,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (ID) {
                            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_UNINSTALL: {
                                if (btnUninstall
                                        .getText()
                                        .equals(ctx
                                                .getResources()
                                                .getString(
                                                        R.string.tv_menu_applications_settings_manage_applications_uninstall_btn))) {
                                    try {
                                        appDetails.uninstall();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        appDetails.enable();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    refreshGUI();
                                }
                                break;
                            }
                            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_CLEAR_DATA: {
                                try {
                                    appDetails.clearData();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                                refreshGUI();
                                break;
                            }
                            case TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE_CLEAR: {
                                try {
                                    appDetails.clearCache();
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        };
                        askDialog.cancel();
                    }
                });
        askDialog.setNegativeButton(R.string.button_text_no,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        askDialog.cancel();
                    }
                });
        askDialog.show();
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
    public void fillDialog() {
        DialogManager.dialogCreator.setAppItem(appItem);
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

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
        // clear old data in lists
        contentList.clear();
        contentListIDs.clear();
        titleIDs.clear();
        // force stop******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_force_stop);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_FORCE_STOP);
        contentListIDs.add(list);
        // uninstall******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_uninstall);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_UNINSTALL);
        contentListIDs.add(list);
        // storage******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_storage);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE);
        contentListIDs.add(list);
        // storage total******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_storage_total);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_TOTAL);
        contentListIDs.add(list);
        // storage app******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_storage_app);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_APP);
        contentListIDs.add(list);
        // storage data******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_storage_data);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_DATA);
        contentListIDs.add(list);
        // storage SD card******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_storage_sd_card);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_EXTERNAL);
        contentListIDs.add(list);
        // storage clear data******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_clear_data);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_STORAGE_CLEAR_DATA);
        contentListIDs.add(list);
        // cache******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_cache);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE);
        contentListIDs.add(list);
        // clear cache******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_cache_clear);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_CACHE_CLEAR);
        contentListIDs.add(list);
        // launch by default******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_default);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_DEFAULTS);
        contentListIDs.add(list);
        // clear defaults******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_default_clear);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_DEFAULTS_CLEAR);
        contentListIDs.add(list);
        // permissions******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_permissions);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_PERMISSIONS);
        contentListIDs.add(list);
    }

    public AppSizeInfo getAppSizeInfo() {
        return appSizeInfo;
    }

    public void setAppSizeInfo(AppSizeInfo appSizeInfo) {
        this.appSizeInfo = appSizeInfo;
        setViewsFromCallBack();
    }
}
