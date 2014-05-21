package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.IDlnaControl;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.pvr.A4TVStorageManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * DLNA settings dialog class
 * 
 * @author Branimir Pavlovic
 */
public class DLNASettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** ID's of switch buttons */
    public static final int TV_MENU_DLNA_SETTINGS_ENABLE_SERVER = 12344,
            TV_MENU_DLNA_SETTINGS_ENABLE_RENDERER = 12345,
            TV_MENU_DLNA_SETTINGS_CHANGE_THE_NAME_OF_DLNA_SERVER = 12346,
            TV_MENU_DLNA_SETTINGS_CHANGE_THE_NAME_OF_DLNA_RENDERER = 12347;
    public static final int RENAME_SERVER_NAME = 1, RENAME_RENDERER_NAME = 2;
    public static final int TIME_OUT = 2000;
    private Context ctx;
    @SuppressWarnings("unused")
    private static boolean isServer = false, isRenderer = false;
    @SuppressWarnings("unused")
    private static boolean isUSBMounted = false;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButtonSwitch enableServer, enableRendereer;
    private A4TVEditText changeNameServer, changeNameRenderer;
    private final int STOP_SERVER = 1, START_SERVER = 2, STOP_RENDERER = 3,
            START_RENDERER = 4;
    private static final String CHANGE_NAME_SERVER = "Change name server";
    private static final String CHANGE_NAME_RENDERER = "Change name renderer";
    private A4TVProgressDialog progress;
    private Enumeration<NetworkInterface> en;
    private Enumeration<InetAddress> enumIpAddr;
    private NetworkInterface intf;
    private InetAddress inetAddress;
    // private Thread mThreadServer = null, mThreadRenderer = null;
    private String serverName, rendererName;
    private static A4TVStorageManager storage = new A4TVStorageManager();

    public String getLocalIpAddress() {
        try {
            en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                intf = en.nextElement();
                enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLinkLocalAddress()) {
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Error", ex.toString());
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (!MainActivity.sharedPrefs.getString(CHANGE_NAME_SERVER, "DlnaDMS")
                .equals(serverName)) {
            MainActivity.sharedPrefs.edit()
                    .putString(CHANGE_NAME_SERVER, serverName).commit();
            Thread mThreadServer = new Thread(new Runnable() {
                @Override
                public void run() {
                    renameServerName(serverName);
                }
            });
            mThreadServer.start();
        }
        if (!MainActivity.sharedPrefs
                .getString(CHANGE_NAME_RENDERER, "DlnaDMR")
                .equals(rendererName)) {
            MainActivity.sharedPrefs.edit()
                    .putString(CHANGE_NAME_RENDERER, rendererName).commit();
            Thread mThreadRenderer = new Thread(new Runnable() {
                @Override
                public void run() {
                    renameRendererName(rendererName);
                }
            });
            mThreadRenderer.start();
        }
        super.onBackPressed();
    }

    public DLNASettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        getReferencesFromViews();
    }

    /**
     * Initially get references from views
     */
    private void getReferencesFromViews() {
        enableServer = (A4TVButtonSwitch) findViewById(TV_MENU_DLNA_SETTINGS_ENABLE_SERVER);
        enableRendereer = (A4TVButtonSwitch) findViewById(TV_MENU_DLNA_SETTINGS_ENABLE_RENDERER);
        changeNameServer = (A4TVEditText) findViewById(TV_MENU_DLNA_SETTINGS_CHANGE_THE_NAME_OF_DLNA_SERVER);
        changeNameRenderer = (A4TVEditText) findViewById(TV_MENU_DLNA_SETTINGS_CHANGE_THE_NAME_OF_DLNA_RENDERER);
        progress = new A4TVProgressDialog(ctx);
        progress.setMessage("Performing action!");
        progress.setTitleOfAlertDialog(R.string.loading_message);
        progress.setCancelable(false);
    }

    /**
     * Get USB status
     */
    private static boolean getUsbStatus() {
        boolean usbStatus = false;
        if (storage != null) {
            if (storage.getUSBStorage(0) != null) {
                usbStatus = true;
            }
        }
        // try {
        // Process proc = Runtime.getRuntime().exec("ls /mnt/media");
        // BufferedReader in = new BufferedReader(new InputStreamReader(
        // proc.getInputStream()));
        // String line = null;
        // while ((line = in.readLine()) != null) {
        // System.out.println(line);
        // usbStatus = true;
        // }
        // in.close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        return usbStatus;
    }

    @Override
    public void show() {
        fillInitialViews();
        super.show();
    }

    /** Populate views in dialog */
    private void fillInitialViews() {
        // ZORANA - remove hardcoded states - they should be retrieved in
        // runtime
        try {
            isServer = MainActivity.service.getDlnaControl().getServerStatus();
            isRenderer = MainActivity.service.getDlnaControl()
                    .getRendererStatus();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        // TODO ask service
        if ((getUsbStatus() == false) && (isServer == true)) {
            new MyAsyncTask(STOP_SERVER).execute();
        }
        if (isServer) {
            enableServer.setSelectedStateAndText(isServer,
                    R.string.button_text_yes);
        } else {
            enableServer.setSelectedStateAndText(isServer,
                    R.string.button_text_no);
        }
        if (isRenderer) {
            enableRendereer.setSelectedStateAndText(isRenderer,
                    R.string.button_text_yes);
        } else {
            enableRendereer.setSelectedStateAndText(isRenderer,
                    R.string.button_text_no);
        }
        String prefChangeNameServer = MainActivity.sharedPrefs.getString(
                CHANGE_NAME_SERVER, "DlnaDMS");
        String prefChangeNameRenderer = MainActivity.sharedPrefs.getString(
                CHANGE_NAME_RENDERER, "DlnaDMR");
        changeNameServer.setText(prefChangeNameServer);
        changeNameRenderer.setText(prefChangeNameRenderer);
        changeNameServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (changeNameServer.getText().toString().length() > 0) {
                    serverName = s.toString();
                } else {
                    serverName = "DlnaDMS";
                }
            }
        });
        changeNameRenderer.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (changeNameRenderer.getText().toString().length() > 0) {
                    rendererName = s.toString();
                } else {
                    rendererName = "DlnaDMR";
                }
            }
        });
    }

    private void renameServerName(String prefChangeNameServer) {
        if (isServer) {
            try {
                MainActivity.service.getDlnaControl().changeDMSName(
                        prefChangeNameServer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void renameRendererName(String prefChangeNameRenderer) {
        if (isRenderer) {
            try {
                MainActivity.service.getDlnaControl().changeDMRName(
                        prefChangeNameRenderer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_DLNA_SETTINGS_ENABLE_SERVER: {
                if (getUsbStatus() && (getLocalIpAddress() != null)) {
                    if (((A4TVButtonSwitch) v).getText().equals(
                            ctx.getResources().getString(
                                    R.string.button_text_no))) {
                        // // Disable Renderer
                        // if (enableRendereer.isSelected()) {
                        // new MyAsyncTask(STOP_RENDERER).execute();
                        // enableRendereer.setSelectedStateAndText(false,
                        // R.string.button_text_no);
                        // }
                        new MyAsyncTask(START_SERVER).execute();
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                                R.string.button_text_yes);
                    } else {
                        new MyAsyncTask(STOP_SERVER).execute();
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                                R.string.button_text_no);
                    }
                } else { // getUsbStatus = false
                    if (((A4TVButtonSwitch) v).getText().equals(
                            ctx.getResources().getString(
                                    R.string.button_text_yes))) {
                        new MyAsyncTask(STOP_SERVER).execute();
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                                R.string.button_text_no);
                    }
                    if (getUsbStatus()) {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast("No IP connection");
                    } else {
                        A4TVToast toast = new A4TVToast(ctx);
                        toast.showToast(R.string.pvr_no_usb);
                    }
                }
                break;
            }
            case TV_MENU_DLNA_SETTINGS_ENABLE_RENDERER: {
                if (getLocalIpAddress() != null) {
                    if (((A4TVButtonSwitch) v).getText().equals(
                            ctx.getResources().getString(
                                    R.string.button_text_no))) {
                        // Disable Server
                        // if (enableServer.isSelected()) {
                        // new MyAsyncTask(STOP_SERVER).execute();
                        // enableServer.setSelectedStateAndText(false,
                        // R.string.button_text_no);
                        // }
                        new MyAsyncTask(START_RENDERER).execute();
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                                R.string.button_text_yes);
                    } else {
                        new MyAsyncTask(STOP_RENDERER).execute();
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                                R.string.button_text_no);
                    }
                } else {
                    if (((A4TVButtonSwitch) v).getText().equals(
                            ctx.getResources().getString(
                                    R.string.button_text_yes))) {
                        new MyAsyncTask(STOP_RENDERER).execute();
                        ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                                R.string.button_text_no);
                    }
                    A4TVToast toast = new A4TVToast(ctx);
                    toast.showToast("No IP connection");
                }
                break;
            }
            default:
                break;
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        int typeOfWork;

        public MyAsyncTask(int typeOfWork) {
            this.typeOfWork = typeOfWork;
        }

        @Override
        protected void onPreExecute() {
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            IDlnaControl dlnaControl = null;
            try {
                dlnaControl = MainActivity.service.getDlnaControl();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (dlnaControl != null) {
                switch (typeOfWork) {
                    case STOP_SERVER: {
                        isServer = false;
                        try {
                            dlnaControl.stopDlnaServer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case START_SERVER: {
                        isServer = true;
                        String prefChangeNameServer = MainActivity.sharedPrefs
                                .getString(CHANGE_NAME_SERVER, "DlnaDMS");
                        try {
                            dlnaControl.startDlnaServer(prefChangeNameServer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case START_RENDERER: {
                        isRenderer = true;
                        String prefChangeNameRenderer = MainActivity.sharedPrefs
                                .getString(CHANGE_NAME_RENDERER, "DlnaDMR");
                        try {
                            dlnaControl
                                    .startDlnaRenderer(prefChangeNameRenderer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case STOP_RENDERER: {
                        isRenderer = false;
                        try {
                            dlnaControl.stopDlnaRenderer();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            super.onPostExecute(result);
        }
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
        // title
        titleIDs.add(R.drawable.settings_icon);
        titleIDs.add(R.string.settings_menu_item_dlna);
        // server on off******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_dlna_settings_server_on_off);
        list.add(TV_MENU_DLNA_SETTINGS_ENABLE_SERVER);
        contentListIDs.add(list);
        // Change the name of dlna
        // server******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_dlna_settings_change_the_name_of_dlna_server);
        list.add(TV_MENU_DLNA_SETTINGS_CHANGE_THE_NAME_OF_DLNA_SERVER);
        contentListIDs.add(list);
        // renderer on off******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_dlna_settings_renderer_on_off);
        list.add(TV_MENU_DLNA_SETTINGS_ENABLE_RENDERER);
        contentListIDs.add(list);
        // Change the name of dlna
        // renderer******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_dlna_settings_change_the_name_of_dlna_renderer);
        list.add(TV_MENU_DLNA_SETTINGS_CHANGE_THE_NAME_OF_DLNA_RENDERER);
        contentListIDs.add(list);
    }
}
