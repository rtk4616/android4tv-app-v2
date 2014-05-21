package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.view.View;

import com.iwedia.dtv.scan.Modulation;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Pvr settings dialog
 * 
 * @author Sasa Jagodin
 */
public class CableNetworkDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for edit texts */
    public static final int CABLE_NETWORK_MENU_NETWORK_ID = 1,
            CABLE_NETWORK_MENU_FREQUENCY = 2,
            CABLE_NETWORK_MENU_SYMBOL_RATE = 3;
    /** ID for spinner */
    public static final int CABLE_NETWORK_MENU_MODULATION = 41;
    public static final int CABLE_NETWORK_MENU_STORE = 5;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVEditText editNetworkId, editFrequency, editSymbolRate;
    private A4TVButton buttonStore;
    private A4TVSpinner editModulation;
    private Context context;

    public CableNetworkDialog(Context context) {
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
        editNetworkId = (A4TVEditText) findViewById(CABLE_NETWORK_MENU_NETWORK_ID);
        editFrequency = (A4TVEditText) findViewById(CABLE_NETWORK_MENU_FREQUENCY);
        editModulation = (A4TVSpinner) findViewById(CABLE_NETWORK_MENU_MODULATION);
        editSymbolRate = (A4TVEditText) findViewById(CABLE_NETWORK_MENU_SYMBOL_RATE);
        buttonStore = (A4TVButton) findViewById(CABLE_NETWORK_MENU_STORE);
        buttonStore.setText(R.string.button_text_set);
        editModulation.setSelection(0);
    }

    @Override
    public void show() {
        setViews();
        super.show();
    }

    /** Get informations from service and display it */
    private void setViews() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case CABLE_NETWORK_MENU_STORE: {
                int index = editModulation.getCHOOSEN_ITEM_INDEX();
                Modulation modulation;
                try {
                    switch (index) {
                        case 0: {
                            modulation = Modulation.MODULATION_QAM16;
                            break;
                        }
                        case 1: {
                            modulation = Modulation.MODULATION_QAM64;
                            break;
                        }
                        case 2: {
                            modulation = Modulation.MODULATION_QAM128;
                            break;
                        }
                        case 3: {
                            modulation = Modulation.MODULATION_QAM256;
                            break;
                        }
                        default:
                            modulation = Modulation.MODULATION_AUTO;
                            break;
                    }
                    MainActivity.service.getScanControl()
                            .storeNetworkDefaultValues(
                                    Integer.valueOf(editNetworkId.getText()
                                            .toString()),
                                    Integer.valueOf(editFrequency.getText()
                                            .toString()),
                                    Integer.valueOf(editSymbolRate.getText()
                                            .toString()), modulation);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
            default:
                break;
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
        titleIDs.add(R.string.tv_menu_cable_network_settings);
        ArrayList<Integer> list;
        // network id ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_network_id);
        list.add(CABLE_NETWORK_MENU_NETWORK_ID);
        contentListIDs.add(list);
        // frequency ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_centre_frequency);
        list.add(CABLE_NETWORK_MENU_FREQUENCY);
        contentListIDs.add(list);
        // modulation ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_modulation);
        list.add(CABLE_NETWORK_MENU_MODULATION);
        contentListIDs.add(list);
        // symbol rate ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_symbol_rate);
        list.add(CABLE_NETWORK_MENU_SYMBOL_RATE);
        contentListIDs.add(list);
        // store ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_store_values);
        list.add(CABLE_NETWORK_MENU_STORE);
        contentListIDs.add(list);
    }
}
