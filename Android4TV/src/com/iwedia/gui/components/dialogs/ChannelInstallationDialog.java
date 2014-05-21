package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.IScanControl;
import com.iwedia.comm.enums.ChannelTuningMode;
import com.iwedia.comm.enums.ScanSignalType;
import com.iwedia.comm.enums.TableType;
import com.iwedia.dtv.scan.BandType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Channel installations dialog
 * 
 * @author Branimir Pavlovic
 */
public class ChannelInstallationDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    public static final String TAG = "ChannelInstallationDialog";
    private Context ctx;
    /** IDs for spinners in this dialog */
    public static final int TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE = 4,
            SATELLITE_NAME_SPINNER = 5000,
            TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TABLE_TYPE = 2342,
            TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CHANNEL_TUNING_MODE = 2354;
    /** IDs for buttons in this dialog */
    public static final int TV_MENU_CHANNEL_INSTALLATION_SETTINGS_AUTO_TUNNING = 7,
            TV_MENU_CHANNEL_INSTALLATION_SETTINGS_MANUAL_TUNNING = 8,
            TV_MENU_CHANNEL_INSTALLATION_SETTINGS_SIGNAL_INFO = 9,
            TV_MENU_CHANNEL_INSTALLATION_SETTINGS_AUTO_CHANNEL_NUMBER = 55,
            TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CABLE_NETWORK_DIALOG = 56;
    public static int TUNER_DVBT = -1, TUNER_DVBS = -1, TUNER_DVBC = -1,
            TUNER_IP = -1, TUNER_ATV = -1;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVSpinner spinnerTunerType, spinnerTuningMode;
    private static A4TVSpinner spinnerSateliteName;
    private boolean nordic = false;
    private static A4TVButton buttonSignalInfo, buttonNetworkSettings;
    private int tableType = TableType.TABLE_TYPE_SDT,
            tuningMode = ChannelTuningMode.DIGITAL;
    private A4TVAlertDialog alertDialog;

    public ChannelInstallationDialog(Context context) {
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
    public void onBackPressed() {
        if (MainActivity.isInFirstTimeInstall) {
            StoreModeSettingsDialog sMSettingsDialog = MainActivity.activity
                    .getDialogManager().getStoreModeSettingsDialog();
            if (sMSettingsDialog != null) {
                sMSettingsDialog.show();
            }
        }
        super.onBackPressed();
    }

    /** Check for NORDIC */
    private boolean checkForNordic() {
        // TODO check if it is for nordic
        int activeCountry;
        String country = "";
        try {
            activeCountry = MainActivity.service.getSetupControl()
                    .getActiveCountry();
            country = MainActivity.service.getSetupControl().getCountryCode(
                    activeCountry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (country == null) {
            country = "Unknown";
        }
        Log.e("nordic", "country:" + country);
        Log.d("ACTIVE COUNTRY LOADED FOR NORDIC CHECK", country);
        if (country.equalsIgnoreCase("IRE") || country.equalsIgnoreCase("SWE")
                || country.equalsIgnoreCase("NOR")
                || country.equalsIgnoreCase("DNK")
                || country.equalsIgnoreCase("FIN")) {
            return true;
        }
        return false;
    }

    @Override
    public void show() {
        nordic = checkForNordic();
        if (MainActivity.isInFirstTimeInstall) {
            findViewById(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_AUTO_TUNNING)
                    .requestFocus();
            if (MainActivity.activity.getFirstTimeInfoText() != null) {
                MainActivity.activity.getFirstTimeInfoText().setText(
                        R.string.first_time_install_scan);
            }
            // hide signal info
            findViewById(
                    R.string.tv_menu_channel_installation_settings_signal_info)
                    .setVisibility(View.GONE);
            if (ConfigHandler.DVB_S) {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 3)
                        .setVisibility(View.GONE);
            } else {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 2)
                        .setVisibility(View.GONE);
            }
        } else {
            // show signal info
            findViewById(
                    R.string.tv_menu_channel_installation_settings_signal_info)
                    .setVisibility(View.VISIBLE);
            if (ConfigHandler.DVB_S) {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 3)
                        .setVisibility(View.VISIBLE);
            } else {
                findViewById(DialogCreatorClass.LINES_BASE_ID + 2)
                        .setVisibility(View.VISIBLE);
            }
        }
        String tunerType = "";
        int currentTunerType = -1;
        try {
            currentTunerType = MainActivity.service.getScanControl()
                    .getScanType();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (currentTunerType == ScanSignalType.SIGNAL_TYPE_SATTELITE) {
            if (ConfigHandler.ATSC) {
                tunerType = ctx.getResources().getString(
                        R.string.main_menu_content_list_atsc_s);
            } else {
                tunerType = ctx.getResources().getString(
                        R.string.main_menu_content_list_dvb_s);
            }
        } else if (currentTunerType == ScanSignalType.SIGNAL_TYPE_TERRESTRIAL) {
            if (ConfigHandler.ATSC) {
                tunerType = ctx.getResources().getString(
                        R.string.tv_menu_channel_installation_settings_air);
            } else {
                tunerType = ctx.getResources().getString(
                        R.string.main_menu_content_list_dvb_t);
            }
        } else if (currentTunerType == ScanSignalType.SIGNAL_TYPE_CABLE) {
            if (ConfigHandler.ATSC) {
                tunerType = ctx.getResources().getString(
                        R.string.tv_menu_channel_installation_settings_cable);
            } else {
                tunerType = ctx.getResources().getString(
                        R.string.main_menu_content_list_dvb_c);
            }
        } else if (currentTunerType == ScanSignalType.SIGNAL_TYPE_IP) {
            tunerType = ctx.getResources().getString(
                    R.string.main_menu_content_list_ip);
        } else if (currentTunerType == ScanSignalType.SIGNAL_TYPE_ANALOG) {
            if (ConfigHandler.ATSC) {
                tunerType = ctx.getResources().getString(
                        R.string.main_menu_content_list_ntsc);
            } else {
                tunerType = ctx.getResources().getString(
                        R.string.main_menu_content_list_atv);
            }
        } else if (currentTunerType == ScanSignalType.SIGNAL_TYPE_CABLE_SDT) {
            tableType = TableType.TABLE_TYPE_SDT;
            tuningMode = ChannelTuningMode.DIGITAL;
        } else if (currentTunerType == ScanSignalType.SIGNAL_TYPE_ANALOG_SDT) {
            tableType = TableType.TABLE_TYPE_SDT;
            tuningMode = ChannelTuningMode.ANALOG;
        }
        ((A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE))
                .setSelectionByString(tunerType);
        /*************** Get references of views *************/
        spinnerTunerType = (A4TVSpinner) ChannelInstallationDialog.this
                .findViewById(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE);
        if (ConfigHandler.ATSC) {
            // spinnerTableType = (A4TVSpinner)
            // findViewById(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TABLE_TYPE);
            spinnerTuningMode = (A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CHANNEL_TUNING_MODE);
            spinnerTuningMode.setSelection(tuningMode);
            if (ConfigHandler.ATSC) {
                /*
                 * spinnerTableType
                 * .setOnButtonClickListener(spinnerTableType.new
                 * OnButtonClickListener() {
                 * @Override public void onClick(View view, int
                 * choosenItemIndex) { Log.d(TAG,
                 * "spinnerTableType onClick choosenItemIndex=" +
                 * choosenItemIndex); tableType = choosenItemIndex; }; });
                 */
                spinnerTuningMode
                        .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                            @Override
                            public void onSelect(A4TVSpinner spinner,
                                    int index, String[] contents) {
                                Log.d(TAG,
                                        "spinnerTuningMode onClick choosenItemIndex="
                                                + index);
                                tuningMode = index;
                            }
                        });
            }
        }
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
        // ////////////////////////////
        // Check available tuners
        // ////////////////////////////
        String[] spinnerOptions = spinnerTunerType.getContents();
        for (int i = 0; i < spinnerOptions.length; i++) {
            Log.d(TAG, spinnerOptions[i]);
            if (spinnerOptions[i].equals(ctx.getResources().getString(
                    R.string.main_menu_content_list_dvb_s))
                    || spinnerOptions[i].equals(ctx.getResources().getString(
                            R.string.main_menu_content_list_atsc_s))) {
                TUNER_DVBS = i;
            }
            if (spinnerOptions[i].equals(ctx.getResources().getString(
                    R.string.main_menu_content_list_dvb_t))
                    || spinnerOptions[i]
                            .equals(ctx
                                    .getResources()
                                    .getString(
                                            R.string.tv_menu_channel_installation_settings_air))) {
                TUNER_DVBT = i;
            }
            if (spinnerOptions[i].equals(ctx.getResources().getString(
                    R.string.main_menu_content_list_dvb_c))
                    || spinnerOptions[i]
                            .equals(ctx
                                    .getResources()
                                    .getString(
                                            R.string.tv_menu_channel_installation_settings_cable))) {
                TUNER_DVBC = i;
            }
            if (spinnerOptions[i].equals(ctx.getResources().getString(
                    R.string.main_menu_content_list_atv))
                    || spinnerOptions[i].equals(ctx.getResources().getString(
                            R.string.main_menu_content_list_ntsc))) {
                TUNER_ATV = i;
            }
            if (spinnerOptions[i].equals(ctx.getResources().getString(
                    R.string.main_menu_content_list_ip))) {
                TUNER_IP = i;
            }
        }
        // Write new on cancel listener that acts like onItemSelected
        // listener
        // in spinner
        spinnerTunerType
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        if (ConfigHandler.DVB_S) {
                            // Check if it is satellite
                            if (index == TUNER_DVBS) {
                                findViewById(
                                        R.string.tv_menu_channel_installation_settings_satellite_name)
                                        .setVisibility(View.VISIBLE);
                                // show line
                                findViewById(
                                        DialogCreatorClass.LINES_BASE_ID + 1)
                                        .setVisibility(View.VISIBLE);
                            } else {
                                findViewById(
                                        R.string.tv_menu_channel_installation_settings_satellite_name)
                                        .setVisibility(View.GONE);
                                // hide line
                                findViewById(
                                        DialogCreatorClass.LINES_BASE_ID + 1)
                                        .setVisibility(View.GONE);
                            }
                        }
                        if (!ConfigHandler.ATSC) {
                            if (index == TUNER_DVBC && nordic) {
                                findViewById(
                                        R.string.tv_menu_cable_network_settings)
                                        .setVisibility(View.VISIBLE);
                                // show line
                                findViewById(
                                        DialogCreatorClass.LINES_BASE_ID
                                                + (ConfigHandler.DVB_S ? 4 : 3))
                                        .setVisibility(View.VISIBLE);
                            } else {
                                findViewById(
                                        R.string.tv_menu_cable_network_settings)
                                        .setVisibility(View.GONE);
                                // hide line
                                findViewById(
                                        DialogCreatorClass.LINES_BASE_ID
                                                + (ConfigHandler.DVB_S ? 4 : 3))
                                        .setVisibility(View.GONE);
                            }
                        }
                        /*
                         * if (ConfigHandler.ATSC) { if
                         * (spinnerTunerType.getCHOOSEN_ITEM_INDEX() ==
                         * TUNER_DVBC) { findViewById(
                         * R.string.tv_menu_channel_installation_settings_table_type
                         * ) .setVisibility(View.VISIBLE); // show line
                         * findViewById( DialogCreatorClass.LINES_BASE_ID + 2)
                         * .setVisibility(View.VISIBLE); } else { findViewById(
                         * R
                         * .string.tv_menu_channel_installation_settings_table_type
                         * ) .setVisibility(View.GONE); // hide line
                         * findViewById( DialogCreatorClass.LINES_BASE_ID + 2)
                         * .setVisibility(View.GONE); } }
                         */
                    }
                });
        // ////////////////////////////////
        // DVB S ENABLED
        // ////////////////////////////////
        if (ConfigHandler.DVB_S) {
            spinnerSateliteName = (A4TVSpinner) ChannelInstallationDialog.this
                    .findViewById(SATELLITE_NAME_SPINNER);
            // Check if it is satellite
            if (spinnerTunerType.getCHOOSEN_ITEM_INDEX() == TUNER_DVBS) {
                findViewById(
                        R.string.tv_menu_channel_installation_settings_satellite_name)
                        .setVisibility(View.VISIBLE);
                // show line
                findViewById(DialogCreatorClass.LINES_BASE_ID + 1)
                        .setVisibility(View.VISIBLE);
            } else {
                findViewById(
                        R.string.tv_menu_channel_installation_settings_satellite_name)
                        .setVisibility(View.GONE);
                // hide line
                findViewById(DialogCreatorClass.LINES_BASE_ID + 1)
                        .setVisibility(View.GONE);
            }
            spinnerSateliteName.setInitialText();
        }
        // Check if it is cable and nordig
        if (!ConfigHandler.ATSC) {
            if (spinnerTunerType.getCHOOSEN_ITEM_INDEX() == TUNER_DVBC
                    && nordic) {
                findViewById(R.string.tv_menu_cable_network_settings)
                        .setVisibility(View.VISIBLE);
                // show line
                findViewById(
                        DialogCreatorClass.LINES_BASE_ID
                                + (ConfigHandler.DVB_S ? 4 : 3)).setVisibility(
                        View.VISIBLE);
            } else {
                findViewById(R.string.tv_menu_cable_network_settings)
                        .setVisibility(View.GONE);
                // hide line
                findViewById(
                        DialogCreatorClass.LINES_BASE_ID
                                + (ConfigHandler.DVB_S ? 4 : 3)).setVisibility(
                        View.GONE);
            }
            buttonNetworkSettings = (A4TVButton) findViewById(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CABLE_NETWORK_DIALOG);
            buttonNetworkSettings.setText(R.string.button_text_view);
        }
        buttonSignalInfo = (A4TVButton) findViewById(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_SIGNAL_INFO);
        buttonSignalInfo.setText(R.string.button_text_view);
        /*
         * if (ConfigHandler.ATSC) { if
         * (spinnerTunerType.getCHOOSEN_ITEM_INDEX() == TUNER_DVBC) {
         * findViewById(
         * R.string.tv_menu_channel_installation_settings_table_type)
         * .setVisibility(View.VISIBLE); // show line
         * findViewById(DialogCreatorClass.LINES_BASE_ID + 2)
         * .setVisibility(View.VISIBLE); } else { findViewById(
         * R.string.tv_menu_channel_installation_settings_table_type)
         * .setVisibility(View.GONE); // hide line
         * findViewById(DialogCreatorClass.LINES_BASE_ID + 2)
         * .setVisibility(View.GONE); } }
         */
        super.show();
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

    /**
     * Called when a button in this dialog has been clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_CHANNEL_INSTALLATION_SETTINGS_AUTO_TUNNING: {
                if (spinnerTunerType.getCHOOSEN_ITEM_INDEX() == TUNER_DVBC
                        && nordic) {
                    NetworkIdDialog netDialog = MainActivity.activity
                            .getDialogManager().getNetworkIdDialog();
                    if (netDialog != null) {
                        netDialog.show();
                    }
                } else {
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
                }
                break;
            }
            case TV_MENU_CHANNEL_INSTALLATION_SETTINGS_MANUAL_TUNNING: {
                if (spinnerTunerType.getCHOOSEN_ITEM_INDEX() != TUNER_IP) {
                    // call manual click function
                    manualTuneClicked();
                } else {
                    A4TVToast toast = new A4TVToast(getContext());
                    toast.showToast(R.string.scan_not_available_for_ip);
                }
                break;
            }
            case TV_MENU_CHANNEL_INSTALLATION_SETTINGS_SIGNAL_INFO: {
                // create dialog
                final ChannelInstallationSignalInformationDialog signalInfoDialog = MainActivity.activity
                        .getDialogManager()
                        .getChannelInstallationSignalInfoDialog();
                if (signalInfoDialog != null) {
                    signalInfoDialog.show();
                }
                break;
            }
            case TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CABLE_NETWORK_DIALOG: {
                CableNetworkDialog cnDialog = MainActivity.activity
                        .getDialogManager().getCableNetworkDialog();
                if (cnDialog != null) {
                    cnDialog.show();
                }
                break;
            }
            default:
                break;
        }
    }

    /** Auto scan procedure button click function */
    private void autoTuneClicked(boolean isOperatorScan) {
        int scanSignalType = 0;
        // create scan dialog
        ChannelScanDialog dialogScan = MainActivity.activity.getDialogManager()
                .getChannelScanDialog();
        if (MainActivity.service != null) {
            /** Start scan procedure */
            if (isOperatorScan) {
                // try {
                // ChannelScanDialog.isScanning = MainActivity.service
                // .getScanControl().operatorProfileScan(0);
                // } catch(Exception e) {
                // e.printStackTrace();
                // }
            } else {
                try {
                    int indexSelected = spinnerTunerType
                            .getCHOOSEN_ITEM_INDEX();
                    if (indexSelected == TUNER_DVBC) {
                        if (tuningMode == ChannelTuningMode.DIGITAL) {
                            if (tableType == TableType.TABLE_TYPE_SDT) {
                                scanSignalType = ScanSignalType.SIGNAL_TYPE_CABLE_SDT;
                            } else if (tableType == TableType.TABLE_TYPE_HRC) {
                                scanSignalType = ScanSignalType.SIGNAL_TYPE_CABLE_HRC;
                            } else {
                                scanSignalType = ScanSignalType.SIGNAL_TYPE_CABLE_IRC;
                            }
                        } else {
                            if (tableType == TableType.TABLE_TYPE_SDT) {
                                scanSignalType = ScanSignalType.SIGNAL_TYPE_ANALOG_SDT;
                            } else if (tableType == TableType.TABLE_TYPE_HRC) {
                                scanSignalType = ScanSignalType.SIGNAL_TYPE_ANALOG_HRC;
                            } else {
                                scanSignalType = ScanSignalType.SIGNAL_TYPE_ANALOG_IRC;
                            }
                        }
                    } else if (indexSelected == TUNER_DVBT) {
                        if (tuningMode == ChannelTuningMode.DIGITAL) {
                            scanSignalType = ScanSignalType.SIGNAL_TYPE_TERRESTRIAL;
                        } else {
                            scanSignalType = ScanSignalType.SIGNAL_TYPE_ANALOG;
                        }
                    }
                    if (indexSelected == TUNER_DVBT) {
                        if (ConfigHandler.ATSC) {
                            ChannelScanDialog.setScanning(autoScan(
                                    scanSignalType, false));
                        } else {
                            ChannelScanDialog.setScanning(autoScan(
                                    ScanSignalType.SIGNAL_TYPE_TERRESTRIAL,
                                    false));
                        }
                    } else if (indexSelected == TUNER_DVBS) {
                        ChannelScanDialog.setScanning(autoScan(
                                ScanSignalType.SIGNAL_TYPE_SATTELITE, false));
                    } else if (indexSelected == TUNER_DVBC) {
                        if (ConfigHandler.ATSC) {
                            ChannelScanDialog.setScanning(autoScan(
                                    scanSignalType, false));
                        } else {
                            ChannelScanDialog.setScanning(autoScan(
                                    ScanSignalType.SIGNAL_TYPE_CABLE, false));
                        }
                    } else if (indexSelected == TUNER_ATV) {
                        ChannelScanDialog.setScanning(autoScan(
                                ScanSignalType.SIGNAL_TYPE_ANALOG, false));
                    } else if (indexSelected == TUNER_IP) {
                        ChannelScanDialog.setScanning(autoScan(
                                ScanSignalType.SIGNAL_TYPE_IP, false));
                    }
                } catch (RuntimeException e) {
                    A4TVToast toast = new A4TVToast(getContext());
                    toast.showToast(R.string.null_pointer_error);
                    e.printStackTrace();
                }
            }
        } else {
            A4TVToast toast = new A4TVToast(getContext());
            toast.showToast(R.string.null_pointer_error);
        }
        if (spinnerTunerType.getCHOOSEN_ITEM_INDEX() == TUNER_IP) {
            ChannelScanDialog.setScanning(false);
            ChannelScanDialog csDialog = MainActivity.activity
                    .getDialogManager().getChannelScanDialog();
            if (csDialog != null) {
                csDialog.endScan(true);
            }
        } else if (ChannelScanDialog.isScanning()) {
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
                int indexSelected = spinnerTunerType.getCHOOSEN_ITEM_INDEX();
                if (indexSelected == TUNER_DVBS) {
                    dialogScan
                            .selectFilter(ChannelScanDialog.FILTER_DVB_S_OPTION);
                } else if (indexSelected == TUNER_DVBT) {
                    dialogScan
                            .selectFilter(ChannelScanDialog.FILTER_DVB_T_OPTION);
                } else if (indexSelected == TUNER_DVBC) {
                    dialogScan
                            .selectFilter(ChannelScanDialog.FILTER_DVB_C_OPTION);
                } else if (indexSelected == TUNER_ATV) {
                    dialogScan
                            .selectFilter(ChannelScanDialog.FILTER_ATV_OPTION);
                } else if (indexSelected == TUNER_IP) {
                    dialogScan.selectFilter(ChannelScanDialog.FILTER_IP_OPTION);
                }
            }
            // hide others
            ChannelInstallationDialog.this.cancel();
            MainActivity.activity.getMainMenuHandler().closeMainMenu(false);
        } else {
            // A4TVToast toast = new A4TVToast(getContext());
            // toast.showToast(R.string.no_signal);
        }
    }

    private void manualTuneClicked() {
        // create dialog
        final ChannelInstallationManualTunningDialog manualTuneDialog = MainActivity.activity
                .getDialogManager().getChannelInstallationManualTunningDialog();
        // show dialog
        if (manualTuneDialog != null) {
            manualTuneDialog.show();
        }
    }

    /** Start auto scan function */
    public boolean autoScan(int type, boolean updateList) {
        IScanControl scanControl = null;
        try {
            scanControl = MainActivity.service.getScanControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scanControl != null) {
            try {
                if (type == ScanSignalType.SIGNAL_TYPE_SATTELITE) {
                    // ///////////////////////////////////
                    // Veljko Ilkic
                    // ///////////////////////////////////////
                    scanControl.setSatelite(spinnerSateliteName
                            .getCHOOSEN_ITEM_INDEX());
                    // scanControl.setSatelite(0);
                    // ///////////////////////////////////
                    // Veljko Ilkic
                    // ///////////////////////////////////////
                    scanControl.setLnbType(0);
                    scanControl.setLnbLow(9750);
                    scanControl.setLnbHigh(10600);
                    scanControl.setLnbBandType(BandType.BANDTYPE_KU);
                }
                return scanControl.scanAll(type, updateList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
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
        titleIDs.add(R.string.tv_menu_channel_installation_settings);
        // tuner type******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_settings_tuner_type);
        list.add(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE);
        contentListIDs.add(list);
        if (ConfigHandler.ATSC) {
            // channel scan type******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_settings_channel_tuning_mode);
            list.add(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CHANNEL_TUNING_MODE);
            contentListIDs.add(list);
        }
        // scan type******************************************
        /*
         * list = new ArrayList<Integer>();
         * list.add(MainMenuContent.TAGA4TVTextView);
         * list.add(MainMenuContent.TAGA4TVSpinner); contentList.add(list); list
         * = new ArrayList<Integer>();
         * list.add(R.string.tv_menu_channel_installation_settings_table_type);
         * list.add(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TABLE_TYPE);
         * contentListIDs.add(list);
         */
        if (ConfigHandler.DVB_S) {
            // Hide satellite name row
            // ////////////////////////////////
            // Veljko Ilkic
            // ////////////////////////////////
            // satelite name******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_settings_satellite_name);
            list.add(SATELLITE_NAME_SPINNER);
            contentListIDs.add(list);
            // ////////////////////////////////
            // Veljko Ilkic
            // ////////////////////////////////
        }
        // auto tuning******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_settings_auto_tunning);
        list.add(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_AUTO_TUNNING);
        contentListIDs.add(list);
        if (!ConfigHandler.ATSC) {
            // manual tuning******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_settings_manual_tunning);
            list.add(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_MANUAL_TUNNING);
            contentListIDs.add(list);
        }
        // signal info******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_settings_signal_info);
        list.add(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_SIGNAL_INFO);
        contentListIDs.add(list);
        if (!ConfigHandler.ATSC) {
            // channel search dialog******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_cable_network_settings);
            list.add(TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CABLE_NETWORK_DIALOG);
            contentListIDs.add(list);
        }
    }

    // //////////////////////////////////
    // Veljko Ilkic
    // //////////////////////////////////
    /** Get satellite index from spinner */
    public static int getSatelliteIndexFromSpinner() {
        return spinnerSateliteName.getCHOOSEN_ITEM_INDEX();
    }
}
