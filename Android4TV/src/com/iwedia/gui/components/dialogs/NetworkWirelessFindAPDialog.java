package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.iwedia.comm.enums.WirelessState;
import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.comm.system.WifiScanResult;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Wireless network find AP dialog
 * 
 * @author Mladen Ilic
 */
public class NetworkWirelessFindAPDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener,
        OnItemClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_NETWORK_WIRELESS_SETTINGS_WIRELESS = 40,
            TV_MENU_NETWORK_WIRELESS_SETTINGS_NETWORKS = 41;
    public static final int WIRELESS_NETWORKS_CHANGED = 0,
            BUTTON_SCAN_ANIM = 1, BUTTON_CONNECTING_ANIM = 2;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVButton btnNetworks;
    private ListView listViewNetworks;
    private WirelessNetworksAdapter listAdapter;
    private int numberOfFoundedNetworks = 0;
    private Handler handlerForCallback;
    private INetworkSettings networkSettings;
    private Context ctx;
    private A4TVAlertDialog alertDialog;
    private LayoutInflater inflater;
    private A4TVEditText editTextPassword;
    private WifiScanResult choosenScanResult;
    private WifiScanResult activeWiFiNetwork = null;
    private Thread thread;
    private Runnable run;
    private int textID = BUTTON_SCAN_ANIM;

    public NetworkWirelessFindAPDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
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
    public void fillDialog() {
        listAdapter = new WirelessNetworksAdapter();
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, listAdapter);
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

    /** Init views in dialog */
    private void init() {
        // get reference of views
        listViewNetworks = (ListView) findViewById(DialogCreatorClass.LIST_VIEW_IN_DIALOG_ID);
        listViewNetworks.setOnItemClickListener(this);
        btnNetworks = (A4TVButton) findViewById(TV_MENU_NETWORK_WIRELESS_SETTINGS_NETWORKS);
        /** Disable button networks */
        disableBtn(btnNetworks, false);
        btnNetworks.setText("");
        btnNetworks.setGravity(Gravity.LEFT);
        LinearLayout view = (LinearLayout) findViewById(R.string.tv_menu_network_wireless_settings_networks);
        if (view != null) {
            view.setPadding(4, 4, 15, 4);
        }
        /** Init handler */
        handlerForCallback = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WIRELESS_NETWORKS_CHANGED) {
                    int state = (Integer) msg.obj;
                    switch (state) {
                        case WirelessState.WIRELESS_STATE_SCANNED:
                        case WirelessState.WIRELESS_STATE_CONNECTED: {
                            networkSettings = null;
                            try {
                                networkSettings = MainActivity.service
                                        .getSystemControl().getNetworkControl();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (networkSettings != null) {
                                numberOfFoundedNetworks = 0;
                                try {
                                    numberOfFoundedNetworks = networkSettings
                                            .getNumberOfAvailableWirelessNetworks();
                                    activeWiFiNetwork = networkSettings
                                            .getActiveWirelessNetwork();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.d(MainActivity.TAG,
                                        "number Of Founded WiFi Networks: "
                                                + numberOfFoundedNetworks);
                                stopThread();
                                listAdapter.notifyDataSetChanged();
                            }
                            break;
                        }
                        case WirelessState.WIRELESS_STATE_CONNECTING: {
                            break;
                        }
                        default:
                            break;
                    }
                }
                if (msg.what == BUTTON_SCAN_ANIM) {
                    if (btnNetworks.getText().toString().contains("...")) {
                        btnNetworks.setText(R.string.scaning);
                    } else {
                        btnNetworks.setText(btnNetworks.getText().toString()
                                + ".");
                    }
                }
                if (msg.what == BUTTON_CONNECTING_ANIM) {
                    if (btnNetworks.getText().toString().contains("...")) {
                        btnNetworks.setText(R.string.connecting);
                    } else {
                        btnNetworks.setText(btnNetworks.getText().toString()
                                + ".");
                    }
                }
                super.handleMessage(msg);
            }
        };
        inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        /** Init alert dialog */
        alertDialog = new A4TVAlertDialog(ctx);
        alertDialog.setCancelable(true);
        alertDialog
                .setTitleOfAlertDialog(R.string.tv_menu_network_wireless_settings_enter_password);
        alertDialog.setNegativeButton(R.string.button_text_cancel,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
        alertDialog.setPositiveButton(R.string.button_text_connect,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (choosenScanResult != null
                                && networkSettings != null) {
                            try {
                                networkSettings.setActiveWirelessNetwork(
                                        choosenScanResult, editTextPassword
                                                .getText().toString());
                                startThread(BUTTON_CONNECTING_ANIM);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            alertDialog.cancel();
                            editTextPassword.setText("");
                        }
                    }
                });
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.security_settings_password_view, null);
        editTextPassword = (A4TVEditText) layout
                .findViewById(R.id.editTextFirstPassword);
        InputFilter maxLengthFilter = new InputFilter.LengthFilter(100);
        editTextPassword.setFilters(new InputFilter[] { maxLengthFilter });
        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alertDialog.getPositiveButton().setEnabled(false);
        editTextPassword.addTextChangedListener(new TextWatcher() {
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
                if (s.length() == 0) {
                    alertDialog.getPositiveButton().setEnabled(false);
                } else {
                    alertDialog.getPositiveButton().setEnabled(true);
                }
            }
        });
        alertDialog.setView(layout);
        // hide second password
        layout.findViewById(R.id.editTextSecondPassword).setVisibility(
                View.GONE);
        run = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Thread currentThread = Thread.currentThread();
                    if (currentThread.equals(thread)) {
                        handlerForCallback.sendEmptyMessage(textID);
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
        };
    }

    /** Disables button GUI representation */
    private void disableBtn(A4TVButton btn, boolean focusable) {
        btn.setFocusable(focusable);
        btn.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        NetworkSettingsDialog netSettDialog = MainActivity.activity
                .getDialogManager().getNetworkSettingsDialog();
        if (netSettDialog != null) {
            netSettDialog.fillViews();
        }
        super.onBackPressed();
    }

    private void fillViews() {
        startThread(BUTTON_SCAN_ANIM);
        networkSettings = null;
        try {
            networkSettings = MainActivity.service.getSystemControl()
                    .getNetworkControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        activeWiFiNetwork = null;
        try {
            if (networkSettings != null) {
                activeWiFiNetwork = networkSettings.getActiveWirelessNetwork();
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        // load networks
        numberOfFoundedNetworks = 0;
        try {
            if (networkSettings != null) {
                numberOfFoundedNetworks = networkSettings
                        .getNumberOfAvailableWirelessNetworks();
                activeWiFiNetwork = networkSettings.getActiveWirelessNetwork();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(MainActivity.TAG, "number Of Founded WiFi Networks: "
                + numberOfFoundedNetworks);
        listAdapter.notifyDataSetChanged();
    }

    public void wirelessNetworksChanged(int state) {
        Log.d(MainActivity.TAG, "WIRELESS STATE: " + state);
        handlerForCallback.sendMessage(Message.obtain(handlerForCallback,
                WIRELESS_NETWORKS_CHANGED, state));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            choosenScanResult = networkSettings.getWirelessNetwork(arg2);
            if (getScanResultSecurity(choosenScanResult).equals("OPEN")) {
                // connect if it is open network
                networkSettings.setActiveWirelessNetwork(choosenScanResult,
                        null);
                startThread(BUTTON_CONNECTING_ANIM);
            } else {
                alertDialog.show();
                if (editTextPassword != null) {
                    editTextPassword.requestFocus();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class WirelessNetworksAdapter extends BaseAdapter {
        private final int LIST_ITEM_WEIGHT_SUM = 5, TEXT_WEIGHT = 4,
                IMAGE_WEIGHT = 1;

        @Override
        public int getCount() {
            return numberOfFoundedNetworks;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = new LinearLayout(ctx);
                holder = new ViewHolder();
                holder.textViewNetworkName = new A4TVTextView(ctx);
                holder.textViewDescription = new A4TVTextView(ctx);
                holder.image = new ImageView(ctx);
                holder.layoutForText = new LinearLayout(ctx);
                // add text to layout
                holder.layoutForText.addView(holder.textViewNetworkName);
                holder.layoutForText.addView(holder.textViewDescription);
                ((LinearLayout) convertView).addView(holder.layoutForText);
                ((LinearLayout) convertView).addView(holder.image);
                (convertView).setPadding(20, 2, 15, 2);
                ((LinearLayout) convertView)
                        .setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) convertView)
                        .setGravity(Gravity.CENTER_VERTICAL);
                ((LinearLayout) convertView).setWeightSum(LIST_ITEM_WEIGHT_SUM);
                convertView
                        .setBackgroundResource(R.drawable.list_view_selector);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            setRow(holder, position);
            convertView.setLayoutParams(new ListView.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    (int) (MainActivity.dialogListElementHeight * 1.2)));
            return convertView;
        }

        /** Function that connects list views child with view holder */
        private void setRow(ViewHolder holder, int position) {
            String text = "";
            WifiScanResult scanResult = null;
            try {
                scanResult = networkSettings.getWirelessNetwork(position);
            } catch (Exception e4) {
                e4.printStackTrace();
            }
            if (scanResult != null) {
                Log.d(TAG, "Initial text:" + text);
                // ///////////////////////////////
                // Fill text
                // ///////////////////////////////
                try {
                    text = scanResult.getSSID();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                if (text != null) {
                    holder.textViewNetworkName.setText(text);
                }
                // if board is connected to this network
                if (activeWiFiNetwork != null
                        && activeWiFiNetwork.getSSID().equals(text)) {
                    holder.textViewDescription.setText(R.string.connected);
                } else {
                    text = "";
                    Log.d(TAG, "Initial text:" + text);
                    try {
                        text = scanResult.getCapabilities();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    if (text != null) {
                        holder.textViewDescription.setText(text);
                    }
                }
                // //////////////////////////////
                // Set text and layout params
                // //////////////////////////////
                holder.layoutForText.setOrientation(LinearLayout.VERTICAL);
                holder.textViewNetworkName
                        .setTextSize(MainActivity.dialogListElementHeight / 4);
                holder.textViewDescription
                        .setTextSize(MainActivity.dialogListElementHeight / 4);
                // /////////////////////////////////////////////
                // Set proper image on image view
                // /////////////////////////////////////////////
                // check network strength
                int strength = 0;
                Log.d(TAG, "Initial strength value:" + strength);
                try {
                    strength = checkNetworkStrength(scanResult.getLevel());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                // ////////////////////////////////////
                // If network is open
                // ////////////////////////////////////
                boolean openNetwork = true;
                try {
                    openNetwork = getScanResultSecurity(
                            networkSettings.getWirelessNetwork(position))
                            .equals("OPEN");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setStrengthImageToImageView(strength, holder.image, openNetwork);
                holder.image.setLayoutParams(new LinearLayout.LayoutParams(0,
                        LayoutParams.WRAP_CONTENT, IMAGE_WEIGHT));
                holder.layoutForText
                        .setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.WRAP_CONTENT, TEXT_WEIGHT));
            }
        }

        private class ViewHolder {
            ImageView image;
            TextView textViewNetworkName, textViewDescription;
            LinearLayout layoutForText;
        }
    }

    /** Is network open or password protected */
    public static String getScanResultSecurity(WifiScanResult scanResult) {
        final String cap = scanResult.getCapabilities();
        final String[] securityModes = { "WEP", "PSK", "EAP" };
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }
        return "OPEN";
    }

    /** Check network strength for wireless strength indicator */
    public static int checkNetworkStrength(int strengthIndBm) {
        // bad signal
        if (strengthIndBm < -90) {
            return 0;
        }
        if (strengthIndBm < -65) {
            return 1;
        }
        if (strengthIndBm < -45) {
            return 2;
        }
        // good signal
        return 3;
    }

    /** Set wireless strength indicator */
    public static void setStrengthImageToImageView(int strength,
            ImageView image, boolean openNetwork) {
        // open network
        if (openNetwork) {
            switch (strength) {
                case 0: {
                    image.setImageResource(R.drawable.wireless_normal_0);
                    break;
                }
                case 1: {
                    image.setImageResource(R.drawable.wireless_normal_1);
                    break;
                }
                case 2: {
                    image.setImageResource(R.drawable.wireless_normal_2);
                    break;
                }
                case 3: {
                    image.setImageResource(R.drawable.wireless_normal_3);
                    break;
                }
                default:
                    break;
            }
        }
        // password protected network
        else {
            switch (strength) {
                case 0: {
                    image.setImageResource(R.drawable.wireless_locked_0);
                    break;
                }
                case 1: {
                    image.setImageResource(R.drawable.wireless_locked_1);
                    break;
                }
                case 2: {
                    image.setImageResource(R.drawable.wireless_locked_2);
                    break;
                }
                case 3: {
                    image.setImageResource(R.drawable.wireless_locked_3);
                    break;
                }
                default:
                    break;
            }
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
        titleIDs.add(R.drawable.network);
        titleIDs.add(R.string.tv_menu_network_wireless_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_network_wireless_settings_networks);
        list.add(TV_MENU_NETWORK_WIRELESS_SETTINGS_NETWORKS);
        contentListIDs.add(list);
    }

    /**
     * Start background thread
     * 
     * @param run
     *        Runnable to run in thread
     */
    public void startThread(int animType) {
        Log.d(MainActivity.TAG, "start thread entered");
        if (thread == null) {
            if (BUTTON_SCAN_ANIM == animType) {
                btnNetworks.setText(R.string.scaning);
            } else {
                btnNetworks.setText(R.string.connecting);
            }
            textID = animType;
            thread = new Thread(run);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    /**
     * Stops background thread
     */
    public void stopThread() {
        Log.d(MainActivity.TAG, "stop thread entered");
        if (thread != null) {
            Thread moribund = thread;
            thread = null;
            moribund.interrupt();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnNetworks.setText("");
                }
            }, 400);
        }
    }

    public A4TVButton getBtnNetworks() {
        return btnNetworks;
    }
}
