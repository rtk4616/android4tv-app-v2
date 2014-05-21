package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Source menu dialog
 * 
 * @author Sasa Jagodin
 */
public class SourceMenuDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_SOURCE_MENU_BROADCAST = 1,
            TV_MENU_SOURCE_MENU_CVBS = 2, TV_MENU_SOURCE_MENU_VGA = 3,
            TV_MENU_SOURCE_MENU_COMPONENT = 4, TV_MENU_SOURCE_MENU_HDMI1 = 5,
            TV_MENU_SOURCE_MENU_HDMI2 = 6, TV_MENU_SOURCE_MENU_HDMI3 = 7,
            TV_MENU_SOURCE_MENU_HDMI4 = 8;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton buttonBroadcast, buttonCvbs, buttonVga, buttonComponent,
            buttonHdmi1, buttonHdmi2, buttonHdmi3, buttonHdmi4;
    Content inputContent;

    public SourceMenuDialog(Context context) {
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
        buttonBroadcast = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_BROADCAST);
        buttonCvbs = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_CVBS);
        buttonVga = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_VGA);
        buttonComponent = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_COMPONENT);
        buttonHdmi1 = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_HDMI1);
        buttonHdmi2 = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_HDMI2);
        buttonHdmi3 = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_HDMI3);
        buttonHdmi4 = (A4TVButton) findViewById(TV_MENU_SOURCE_MENU_HDMI4);
        buttonBroadcast.setText(R.string.button_text_choose);
        buttonCvbs.setText(R.string.button_text_choose);
        buttonVga.setText(R.string.button_text_choose);
        buttonComponent.setText(R.string.button_text_choose);
        buttonHdmi1.setText(R.string.button_text_choose);
        buttonHdmi2.setText(R.string.button_text_choose);
        buttonHdmi3.setText(R.string.button_text_choose);
        buttonHdmi4.setText(R.string.button_text_choose);
        buttonBroadcast.requestFocus();
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
        boolean isDisabled;
        boolean isConnected;
        try {
            IContentFilter contentFilter = MainActivity.service
                    .getContentListControl()
                    .getContentFilter(FilterType.INPUTS);
            Content inputContent = contentFilter
                    .getContent(TV_MENU_SOURCE_MENU_CVBS - 2);
            Log.d(TAG, "Input " + inputContent.toString());
            isDisabled = MainActivity.service.getContentListControl()
                    .getContentLockedStatus(inputContent);
            if (isDisabled) {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_cvbs,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_cvbs,
                        DisplayMode.SHOW);
            }
            inputContent = contentFilter
                    .getContent(TV_MENU_SOURCE_MENU_VGA - 2);
            Log.d(TAG, "Input " + inputContent.toString());
            isDisabled = MainActivity.service.getContentListControl()
                    .getContentLockedStatus(inputContent);
            if (isDisabled) {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_vga,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_vga,
                        DisplayMode.SHOW);
            }
            inputContent = contentFilter
                    .getContent(TV_MENU_SOURCE_MENU_COMPONENT - 2);
            Log.d(TAG, "Input " + inputContent.toString());
            isDisabled = MainActivity.service.getContentListControl()
                    .getContentLockedStatus(inputContent);
            if (isDisabled) {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_component,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_component,
                        DisplayMode.SHOW);
            }
            inputContent = contentFilter
                    .getContent(TV_MENU_SOURCE_MENU_HDMI1 - 2);
            Log.d(TAG, "Input " + inputContent.toString());
            isDisabled = MainActivity.service.getContentListControl()
                    .getContentLockedStatus(inputContent);
            isConnected = MainActivity.service.getInputOutputControl()
                    .ioGetDeviceConnected(inputContent.getIndex());
            if ((isDisabled == true) || (isConnected == false)) {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi1,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi1,
                        DisplayMode.SHOW);
            }
            inputContent = contentFilter
                    .getContent(TV_MENU_SOURCE_MENU_HDMI2 - 2);
            Log.d(TAG, "Input " + inputContent.toString());
            isDisabled = MainActivity.service.getContentListControl()
                    .getContentLockedStatus(inputContent);
            isConnected = MainActivity.service.getInputOutputControl()
                    .ioGetDeviceConnected(inputContent.getIndex());
            if ((isDisabled == true) || (isConnected == false)) {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi2,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi2,
                        DisplayMode.SHOW);
            }
            inputContent = contentFilter
                    .getContent(TV_MENU_SOURCE_MENU_HDMI3 - 2);
            Log.d(TAG, "Input " + inputContent.toString());
            isDisabled = MainActivity.service.getContentListControl()
                    .getContentLockedStatus(inputContent);
            isConnected = MainActivity.service.getInputOutputControl()
                    .ioGetDeviceConnected(inputContent.getIndex());
            if ((isDisabled == true) || (isConnected == false)) {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi3,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi3,
                        DisplayMode.SHOW);
            }
            inputContent = contentFilter
                    .getContent(TV_MENU_SOURCE_MENU_HDMI4 - 2);
            Log.d(TAG, "Input " + inputContent.toString());
            isDisabled = MainActivity.service.getContentListControl()
                    .getContentLockedStatus(inputContent);
            isConnected = MainActivity.service.getInputOutputControl()
                    .ioGetDeviceConnected(inputContent.getIndex());
            if ((isDisabled == true) || (isConnected == false)) {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi4,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_source_menu_hdmi4,
                        DisplayMode.SHOW);
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            Content activeContent = MainActivity.service
                    .getContentListControl().getActiveContent(0);
            MainActivity.service.getContentListControl().stopContent(
                    activeContent, 0);
            MainActivity.activity.setAnalogSignalLock(false);
            /*
             * Remove WebView from screen and set key mask to 0
             */
            if (0 != (MainActivity.getKeySet())) {
                try {
                    if (!MainActivity.activity.isHbbTVInHTTPPlaybackMode()) {
                        MainActivity.activity.webDialog.getHbbTVView()
                                .setAlpha((float) 0.00);
                        MainActivity.setKeySet(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int currentFilter = 0;
        try {
            currentFilter = MainActivity.service.getContentListControl()
                    .getActiveFilterIndex();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        switch (v.getId()) {
            case TV_MENU_SOURCE_MENU_BROADCAST: {
                /* Launch HbbTV Red Button if exists */
                if (0 == (MainActivity.getKeySet())) {
                    int command = 0;
                    String param = "EXIT";
                    try {
                        Log.d(TAG, "Show HbbTV graphic");
                        MainActivity.activity.service.getHbbTvControl()
                                .notifyAppMngr(command, param);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SOURCE_MENU_CVBS: {
                try {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.INPUTS);
                    inputContent = MainActivity.service.getContentListControl()
                            .getContent(TV_MENU_SOURCE_MENU_CVBS - 2);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOURCE_MENU_VGA: {
                try {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.INPUTS);
                    inputContent = MainActivity.service.getContentListControl()
                            .getContent(TV_MENU_SOURCE_MENU_VGA - 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOURCE_MENU_COMPONENT: {
                try {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.INPUTS);
                    inputContent = MainActivity.service.getContentListControl()
                            .getContent(TV_MENU_SOURCE_MENU_COMPONENT - 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOURCE_MENU_HDMI1: {
                try {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.INPUTS);
                    inputContent = MainActivity.service.getContentListControl()
                            .getContent(TV_MENU_SOURCE_MENU_HDMI1 - 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOURCE_MENU_HDMI2: {
                try {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.INPUTS);
                    inputContent = MainActivity.service.getContentListControl()
                            .getContent(TV_MENU_SOURCE_MENU_HDMI2 - 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOURCE_MENU_HDMI3: {
                try {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.INPUTS);
                    inputContent = MainActivity.service.getContentListControl()
                            .getContent(TV_MENU_SOURCE_MENU_HDMI3 - 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOURCE_MENU_HDMI4: {
                try {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.INPUTS);
                    inputContent = MainActivity.service.getContentListControl()
                            .getContent(TV_MENU_SOURCE_MENU_HDMI4 - 2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            default:
                break;
        }
        if (v.getId() != TV_MENU_SOURCE_MENU_BROADCAST) {
            try {
                MainActivity.service.getContentListControl().setActiveFilter(
                        currentFilter);
                MainActivity.activity.sourceSwichingProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MainActivity.service.getContentListControl()
                                    .goContent(inputContent, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MainActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                MainActivity.activity.sourceSwichingProgressDialog
                                        .cancel();
                                if (MainActivity.activity
                                        .getIsAnalogSignalLocked() == false) {
                                    MainActivity.activity.getCheckServiceType()
                                            .showNoSignalLayout();
                                }
                            }
                        });
                    }
                }).start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        SourceMenuDialog srcMenuDialog = MainActivity.activity
                .getDialogManager().getSourceMenuDialog();
        if (srcMenuDialog != null) {
            srcMenuDialog.cancel();
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        switch (keyCode) {
            case KeyEvent.KEYCODE_I: {
                SourceMenuDialog srcMenuDialog = MainActivity.activity
                        .getDialogManager().getSourceMenuDialog();
                if (srcMenuDialog != null)
                    if (srcMenuDialog.isShowing()) {
                        srcMenuDialog.cancel();
                    }
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
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
        titleIDs.add(R.string.main_menu_description_input_selection);
        ArrayList<Integer> list;
        // broadcast source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_broadcast);
        list.add(TV_MENU_SOURCE_MENU_BROADCAST);
        contentListIDs.add(list);
        // cvbs source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_cvbs);
        list.add(TV_MENU_SOURCE_MENU_CVBS);
        contentListIDs.add(list);
        // vga source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_vga);
        list.add(TV_MENU_SOURCE_MENU_VGA);
        contentListIDs.add(list);
        // component source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_component);
        list.add(TV_MENU_SOURCE_MENU_COMPONENT);
        contentListIDs.add(list);
        // hdmi1 source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_hdmi1);
        list.add(TV_MENU_SOURCE_MENU_HDMI1);
        contentListIDs.add(list);
        // hdmi2 source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_hdmi2);
        list.add(TV_MENU_SOURCE_MENU_HDMI2);
        contentListIDs.add(list);
        // hdmi3 source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_hdmi3);
        list.add(TV_MENU_SOURCE_MENU_HDMI3);
        contentListIDs.add(list);
        // hdmi4 source******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_source_menu_hdmi4);
        list.add(TV_MENU_SOURCE_MENU_HDMI4);
        contentListIDs.add(list);
    }
}
