package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.enums.ScanSignalType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Pvr settings dialog
 * 
 * @author Sasa Jagodin
 */
public class NetworkIdDialog extends A4TVDialog implements A4TVDialogInterface,
        android.view.View.OnClickListener {
    /** IDs for edit texts */
    public static final int NETWORK_ID_MENU_NETWORK_ID = 1;
    /** IDs for buttons */
    public static final int NETWORK_ID_MENU_START_SEARCH = 2;
    private Context ctx;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVEditText editNetworkId;
    private A4TVButton buttonStartSearch;
    private A4TVAlertDialog alertDialog;
    private String networkId;

    public NetworkIdDialog(Context context) {
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

    /** Get references from views and set look and feel */
    private void init() {
        editNetworkId = (A4TVEditText) findViewById(NETWORK_ID_MENU_NETWORK_ID);
        buttonStartSearch = (A4TVButton) findViewById(NETWORK_ID_MENU_START_SEARCH);
        buttonStartSearch.setText(R.string.button_text_start);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
        editNetworkId.setText("");
        // init alert dialog
        alertDialog = new A4TVAlertDialog(ctx);
        alertDialog.setCancelable(true);
        alertDialog.setTitleOfAlertDialog(R.string.manual_scan_prompt);
        alertDialog.setNegativeButton(R.string.button_text_no,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case NETWORK_ID_MENU_START_SEARCH: {
                networkId = editNetworkId.getText().toString();
                if (networkId.length() > 0) {
                    if (MainActivity.isInFirstTimeInstall) {
                        autoTuneClicked(false);
                    } else {
                        alertDialog.setPositiveButton(R.string.button_text_yes,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // call click function
                                        autoTuneClicked(false);
                                        alertDialog.cancel();
                                    }
                                });
                        alertDialog.show();
                    }
                } else {
                    new A4TVToast(ctx).showToast(R.string.network_id__error);
                }
                break;
            }
            default:
                break;
        }
    }

    /** Auto scan procedure button click function */
    private void autoTuneClicked(boolean isOperatorScan) {
        // create scan dialog
        ChannelScanDialog dialogScan = MainActivity.activity.getDialogManager()
                .getChannelScanDialog();
        if (MainActivity.service != null) {
            /** Start scan procedure */
            try {
                try {
                    MainActivity.service.getScanControl().setNetNumber(
                            Integer.valueOf(networkId));
                    ChannelScanDialog.setScanning(MainActivity.service
                            .getScanControl().scanAll(
                                    ScanSignalType.SIGNAL_TYPE_CABLE, false));
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (RuntimeException e) {
                A4TVToast toast = new A4TVToast(getContext());
                toast.showToast(R.string.null_pointer_error);
                e.printStackTrace();
            }
        } else {
            A4TVToast toast = new A4TVToast(getContext());
            toast.showToast(R.string.null_pointer_error);
        }
        if (ChannelScanDialog.isScanning()) {
            if (dialogScan != null) {
                dialogScan
                        .getTextTopBanner()
                        .setText(
                                ctx.getResources()
                                        .getText(
                                                R.string.tv_menu_channel_installation_settings_auto_tunning));
                Log.d(TAG, "SHOW SCAN DIALOG");
                // hide layout for messages
                MainActivity.activity.findViewById(R.id.linLayMessages)
                        .setVisibility(View.GONE);
                // show scan dialog
                dialogScan.show();
                dialogScan.selectFilter(ChannelScanDialog.FILTER_DVB_C_OPTION);
                // hide others
                ChannelInstallationDialog chDialog = MainActivity.activity
                        .getDialogManager().getChannelInstallationDialog();
                if (chDialog != null) {
                    chDialog.cancel();
                }
            }
            NetworkIdDialog.this.cancel();
            MainActivity.activity.getMainMenuHandler().closeMainMenu(false);
        } else {
            // A4TVToast toast = new A4TVToast(getContext());
            // toast.showToast(R.string.no_signal);
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
        titleIDs.add(R.string.tv_menu_network_id_selection);
        ArrayList<Integer> list;
        // network id ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_network_id);
        list.add(NETWORK_ID_MENU_NETWORK_ID);
        contentListIDs.add(list);
        // store ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_start_search);
        list.add(NETWORK_ID_MENU_START_SEARCH);
        contentListIDs.add(list);
    }
}
