package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.IScanControl;
import com.iwedia.comm.enums.ScanSignalType;
import com.iwedia.comm.enums.ScramblingMode;
import com.iwedia.dtv.scan.BandType;
import com.iwedia.dtv.scan.FecType;
import com.iwedia.dtv.scan.Modulation;
import com.iwedia.dtv.scan.Polarization;
import com.iwedia.dtv.types.AnalogEncodingMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manual tuning dialog
 * 
 * @author Branimir Pavlovic
 */
public class ChannelInstallationManualTunningDialog extends A4TVDialog
        implements A4TVDialogInterface, android.view.View.OnClickListener,
        OnSeekBarChangeListener {
    private Context ctx;
    /** IDs for buttons in this dialog */
    public static final int TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_KEEP_CURRENT_LIST = 56,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_START_SEARCH = 11;
    /** IDs for edit text in this dialog */
    public static final int TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY = 0,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT = 55,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NUMBER_DVBT = 58;
    /** IDs for spinner in this dialog */
    public static final int TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SCRAMBLED = 21,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FEC = 22,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE = 19,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_polarization = 20,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_MODULATION = 41,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_NETWORK_ID = 42,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYSTEM = 43,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FINE_TUNE = 44,
            TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NORDIC_BAND = 45;
    /** IDs for progress in this dialog */
    // public static final int
    // TV_MENU_CHANNEL_INSTALLATION_SIGNAL_INFO_SIGNAL_STRENGTH = 206;
    private Handler handlerScanStarted;
    private String channelNumber = "";
    private String channelBand = "";
    private String channelNordic = "";
    private int selectedTunerType = 0;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    // intervals for scanning procedure
    // DVB S
    private int dvbSFreqDown, dvbSFreqUp;
    private int dvbSSimbolRateDown, dvbSSimbolRateUp;
    // DVB C
    private int dvbCFreqDown, dvbCFreqUp;
    private int dvbCSimbolRateDown, dvbCSimbolRateUp;
    // ATV
    private int atvFreqDown, atvFreqUp;
    private final int DVB_T_CHANNEL_NUMBER_DOWN = 5,
            DVB_T_CHANNEL_NUMBER_UP = 83, DVB_T_CHANNEL_FREQ_DOWN = 177500,
            DVB_T_CHANNEL_FREQ_UP = 858000;
    private final int ATV_FINE_TUNE_THRESHOLD = 1000; // In kHz
    private final int ATV_FINE_TUNE_STEP = 100; // In kHz
    private boolean nordic = false;
    private boolean isFreqOK = false;
    private static int analogFreq = 0, analogFreqHigh = 0, analogFreqLow = 0;
    // fields for DVB-S scan
    FecType fec;
    int scrambled = 0;
    int symbolRate = 0;
    Polarization polarization = Polarization.NOT_DEFINED;
    int freq = 0;
    A4TVSpinner spinnerNordicType;
    private A4TVAlertDialog alertDialog;
    static final Map<String, Integer> NORDIC = new HashMap<String, Integer>() {
        {
            put("S1", 1);
            put("D1", 2);
            put("S2", 3);
            put("S3", 4);
            put("D2", 5);
            put("S4", 6);
            put("D3", 7);
            put("S5", 8);
            put("D4", 9);
            put("S6", 10);
            put("D5", 11);
            put("S7", 12);
            put("D6", 13);
            put("S8", 14);
            put("D7", 15);
            put("S9", 16);
            put("D8", 17);
            put("S10", 18);
            put("K5", 19);
            put("D9", 20);
            put("K6", 21);
            put("D10", 22);
            put("K7", 23);
            put("D11", 24);
            put("K8", 25);
            put("D12", 26);
            put("K9", 27);
            put("D13", 28);
            put("K10", 29);
            put("D14", 30);
            put("K11", 31);
            put("D15", 32);
            put("K12", 33);
            put("S11", 34);
            put("D16", 35);
            put("S12", 36);
            put("D17", 37);
            put("S13", 38);
            put("D18", 39);
            put("S14", 40);
            put("D19", 41);
            put("S15", 42);
            put("D20", 43);
            put("S16", 44);
            put("D21", 45);
            put("S17", 46);
            put("D22", 47);
            put("S18", 48);
            put("S19", 49);
            put("D23", 50);
            put("S20", 51);
            put("D24", 52);
            put("S21", 53);
            put("S22", 54);
            put("S23", 55);
            put("S24", 56);
            put("S25", 57);
            put("S26", 58);
            put("S27", 59);
            put("S28", 60);
            put("S29", 61);
            put("S30", 62);
            put("S31", 63);
            put("S32", 64);
            put("S33", 65);
            put("S34", 66);
            put("S35", 67);
            put("S36", 68);
            put("S37", 69);
            put("S38", 70);
            put("S39", 71);
            put("S40", 72);
            put("S41", 73);
            put("K21", 74);
            put("K22", 75);
            put("K23", 76);
            put("K24", 77);
            put("K25", 78);
            put("K26", 79);
            put("K27", 80);
            put("K28", 81);
            put("K29", 82);
            put("K30", 83);
            put("K31", 84);
            put("K32", 85);
            put("K33", 86);
            put("K34", 87);
            put("K35", 88);
            put("K36", 89);
            put("K37", 90);
            put("K38", 91);
            put("K39", 92);
            put("K40", 93);
            put("K41", 94);
            put("K42", 95);
            put("K43", 96);
            put("K44", 97);
            put("K45", 98);
            put("K46", 99);
            put("K47", 100);
            put("K48", 101);
            put("K49", 102);
            put("K50", 103);
            put("K51", 104);
            put("K52", 105);
            put("K53", 106);
            put("K54", 107);
            put("K55", 108);
            put("K56", 109);
            put("K57", 110);
            put("K58", 111);
            put("K59", 112);
            put("K60", 113);
            put("K61", 114);
            put("K62", 115);
            put("K63", 116);
            put("K64", 117);
            put("K65", 118);
            put("K66", 119);
            put("K67", 120);
            put("K68", 121);
            put("K69", 122);
        }
    };
    int index = 0;

    public ChannelInstallationManualTunningDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // check what tunner is selected
        ChannelInstallationDialog chDialog = MainActivity.activity
                .getDialogManager().getChannelInstallationDialog();
        if (chDialog != null)
            selectedTunerType = ((A4TVSpinner) chDialog
                    .findViewById(ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE))
                    .getCHOOSEN_ITEM_INDEX();
        checkIntervalsForScanning();
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
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
        alertDialog.setPositiveButton(R.string.button_text_yes,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startManualScanButton();
                        alertDialog.cancel();
                    }
                });
    }

    @Override
    public void show() {
        setInitialListenersAndViews(true);
        super.show();
    }

    // @Override
    // public void cancel() {
    // clearEditTexts();
    //
    // super.cancel();
    // }
    @Override
    public void onBackPressed() {
        clearEditTexts();
        super.onBackPressed();
    }

    private void clearEditTexts() {
        // set for DVB-T
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT) {
            A4TVEditText editText = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NUMBER_DVBT);
            editText.setText("");
        }
        // set for DVB-S
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
            A4TVEditText editText = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY);
            editText.setText("");
            editText = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT);
            editText.setText("");
        }
        // set for DVB-C
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
            A4TVEditText editText = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY);
            editText.setText("");
            editText = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT);
            editText.setText("");
        }
        // set for ATV
        if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
            A4TVEditText editText = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY);
            editText.setText("");
        }
        IScanControl control = null;
        try {
            control = MainActivity.service.getScanControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (control != null) {
                control.setFrequency(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, this, this, null);
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

    /** Check for NORDIC */
    private void checkIntervalsForScanning() {
        nordic = checkForNordic();
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
            if (nordic) {
                dvbSFreqDown = 10700;// in MHz
                dvbSFreqUp = 12750;
                dvbSSimbolRateDown = 10000;// in KBaud
                dvbSSimbolRateUp = 30000;
            } else {
                dvbSFreqDown = 10700;// in MHz
                dvbSFreqUp = 12750;
                dvbSSimbolRateDown = 10000;// in KBaud
                dvbSSimbolRateUp = 30000;
            }
        } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
            if (nordic) {
                dvbCFreqDown = 110000;// in MHz
                dvbCFreqUp = 862000;
                dvbCSimbolRateDown = 4000;// in Ksymbols/s
                dvbCSimbolRateUp = 7000;
                // dvbCFreqDown = 110;// in MHz
                // dvbCFreqUp = 862;
                // dvbCSimbolRateDown = 4000;// in Ksymbols/s
                // dvbCSimbolRateUp = 7000;
            } else {
                dvbCFreqDown = 110000;// in MHz
                dvbCFreqUp = 862000;
                dvbCSimbolRateDown = 4000;// in Ksymbols/s
                dvbCSimbolRateUp = 7000;
                // dvbCFreqDown = 110;// in MHz
                // dvbCFreqUp = 862;
                // dvbCSimbolRateDown = 4000;// in Ksymbols/s
                // dvbCSimbolRateUp = 7000;
            }
        } else if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
            atvFreqDown = 42000;
            atvFreqUp = 863000;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_KEEP_CURRENT_LIST: {
                if (((A4TVButtonSwitch) v).getText().equals(
                        ctx.getResources().getString(R.string.button_text_no))) {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(true,
                            R.string.button_text_yes);
                } else {
                    ((A4TVButtonSwitch) v).setSelectedStateAndText(false,
                            R.string.button_text_no);
                }
                break;
            }
            case TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_START_SEARCH: {
                if (isFreqOK) {
                    if (MainActivity.isInFirstTimeInstall) {
                        startManualScanButton();
                    } else {
                        if (!ChannelInstallationManualTunningDialog.this
                                .findViewById(
                                        TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_KEEP_CURRENT_LIST)
                                .isSelected()) {
                            alertDialog.show();
                        } else {
                            startManualScanButton();
                        }
                    }
                } else {
                    new A4TVToast(ctx).showToast(R.string.freq_error);
                }
                break;
            }
            default:
                break;
        }
    }

    private void startManualScanButton() {
        if (isFreqOK) {
            Editable edit = null;
            if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
                edit = ((A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                        .getText();
                if (edit.toString().trim().length() > 0) {
                    if (Double.parseDouble(edit.toString()) < dvbSSimbolRateDown
                            || Double.parseDouble(edit.toString()) > dvbSSimbolRateUp) {
                        A4TVToast toast = new A4TVToast(getContext());
                        toast.showToast(R.string.tv_menu_channel_installation_settings_wrong_symbol_rate);
                        return;
                    }
                } else {
                    A4TVToast toast = new A4TVToast(getContext());
                    toast.showToast(R.string.tv_menu_channel_installation_settings_wrong_symbol_rate);
                    return;
                }
            }
            if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
                edit = ((A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                        .getText();
                if (edit.toString().trim().length() > 0) {
                    if (Double.parseDouble(edit.toString()) < dvbCSimbolRateDown
                            || Double.parseDouble(edit.toString()) > dvbCSimbolRateUp) {
                        A4TVToast toast = new A4TVToast(getContext());
                        toast.showToast(R.string.tv_menu_channel_installation_settings_wrong_symbol_rate);
                        return;
                    }
                } else {
                    A4TVToast toast = new A4TVToast(getContext());
                    toast.showToast(R.string.tv_menu_channel_installation_settings_wrong_symbol_rate);
                    return;
                }
            }
            // set scan params
            setScanParams();
            // take reference of spinner
            ChannelInstallationDialog chDialog = MainActivity.activity
                    .getDialogManager().getChannelInstallationDialog();
            if (chDialog != null) {
                final A4TVSpinner spinner;
                spinner = (A4TVSpinner) chDialog
                        .findViewById(ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE);
                final A4TVButtonSwitch btn = ((A4TVButtonSwitch) ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_KEEP_CURRENT_LIST));
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean update = false;
                        if (btn.getText().equals(
                                ctx.getResources().getString(
                                        R.string.button_text_yes))) {
                            update = true;
                        }
                        if (MainActivity.service != null) {
                            /** Start scan procedure */
                            try {
                                int selectedIndex = spinner
                                        .getCHOOSEN_ITEM_INDEX();
                                if (selectedIndex == ChannelInstallationDialog.TUNER_DVBT) {
                                    ChannelScanDialog
                                            .setScanning(MainActivity.service
                                                    .getScanControl()
                                                    .manualScan(
                                                            ScanSignalType.SIGNAL_TYPE_TERRESTRIAL,
                                                            update));
                                } else if (selectedIndex == ChannelInstallationDialog.TUNER_DVBS) {
                                    ChannelScanDialog
                                            .setScanning(manualScan(
                                                    ScanSignalType.SIGNAL_TYPE_SATTELITE,
                                                    freq, update, symbolRate,
                                                    polarization, fec));
                                    // MainActivity.service.getScanControl().manualScan(
                                    // SIGNAL_TYPE.SATELLITE, update);
                                } else if (selectedIndex == ChannelInstallationDialog.TUNER_DVBC) {
                                    ChannelScanDialog
                                            .setScanning(MainActivity.service
                                                    .getScanControl()
                                                    .manualScan(
                                                            ScanSignalType.SIGNAL_TYPE_CABLE,
                                                            update));
                                } else if (selectedIndex == ChannelInstallationDialog.TUNER_ATV) {
                                    ChannelScanDialog
                                            .setScanning(MainActivity.service
                                                    .getScanControl()
                                                    .manualScan(
                                                            ScanSignalType.SIGNAL_TYPE_ANALOG,
                                                            update));
                                } else if (selectedIndex == ChannelInstallationDialog.TUNER_IP) {
                                    ChannelScanDialog
                                            .setScanning(MainActivity.service
                                                    .getScanControl()
                                                    .manualScan(
                                                            ScanSignalType.SIGNAL_TYPE_IP,
                                                            update));
                                }
                            } catch (Exception e) {
                                ChannelScanDialog.setScanning(false);
                                e.printStackTrace();
                            }
                            handlerScanStarted.sendEmptyMessage(0);
                        } else {
                            // Toast.makeText(
                            // ctx,
                            // ctx.getResources().getString(
                            // R.string.null_pointer_error),
                            // Toast.LENGTH_LONG).show();
                        }
                    }
                });
                th.start();
            }
        } else {
            new A4TVToast(ctx).showToast(R.string.freq_error);
        }
    }

    /**
     * Initialize view on dialog
     * 
     * @param fromHisClass
     *        called from his class or not
     */
    public void setInitialListenersAndViews(boolean fromHisClass) {
        // set for all tuner types
        // disable start manual scan button
        isFreqOK = true;
        // set button keep current
        A4TVButtonSwitch buttonKeep = (A4TVButtonSwitch) ChannelInstallationManualTunningDialog.this
                .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_KEEP_CURRENT_LIST);
        buttonKeep.setSelectedStateAndText(true, R.string.button_text_yes);
        try {
            if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT) {
                MainActivity.service.getScanControl().setScanType(
                        ScanSignalType.SIGNAL_TYPE_TERRESTRIAL);
            } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
                MainActivity.service.getScanControl().setScanType(
                        ScanSignalType.SIGNAL_TYPE_SATTELITE);
            } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
                MainActivity.service.getScanControl().setScanType(
                        ScanSignalType.SIGNAL_TYPE_CABLE);
            } else if (selectedTunerType == ChannelInstallationDialog.TUNER_IP) {
                MainActivity.service.getScanControl().setScanType(
                        ScanSignalType.SIGNAL_TYPE_IP);
            } else if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
                MainActivity.service.getScanControl().setScanType(
                        ScanSignalType.SIGNAL_TYPE_ANALOG);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //
        if (!ConfigHandler.TV_FEATURES) {
            try {
                MainActivity.service.getScanControl().setFrequency(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // set for DVB-T
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT) {
            A4TVEditText editText = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NUMBER_DVBT);
            // set frequency
            String frequency = "";
            int freq = 0;
            try {
                // freq = MainActivity.service.getScanControl().getFrequency();
                if (freq > 0) {
                    frequency = String.valueOf(freq);
                    editText.setText(frequency);
                    editText.setSelection(frequency.length());
                } else {
                    editText.setHint(ctx
                            .getResources()
                            .getString(
                                    R.string.tv_menu_channel_installation_manual_tunning_settings_dtv_frequency_hint));
                    isFreqOK = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (nordic) {
                spinnerNordicType = (A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NORDIC_BAND);
                spinnerNordicType.setSelection(0);
                channelBand = ctx.getResources().getString(
                        R.string.nordic_band_s);
            }
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        // set for DVB-S
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
            // set frequency
            String frequency = "";
            int freq = 0;
            try {
                // freq = MainActivity.service.getScanControl().getFrequency();
                if (freq > 0) {
                    A4TVEditText editTextFreq = (A4TVEditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY);
                    frequency = String.valueOf(freq);
                    editTextFreq.setText(frequency);
                    editTextFreq.setSelection(String.valueOf(freq).length());
                } else {
                    ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                            .setHint(ctx
                                    .getResources()
                                    .getString(
                                            R.string.tv_menu_channel_installation_manual_tunning_settings_frequency_hint));
                    isFreqOK = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // set symbol rate
            String symbolRate;
            int sRate = 22000;
            try {
                // sRate =
                // MainActivity.service.getScanControl().getSymbolRate();
                if (sRate > 0) {
                    symbolRate = String.valueOf(sRate);
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                            .setText("");
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                            .setSelection(symbolRate.length());
                } else {
                    ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                            .setHint(ctx
                                    .getResources()
                                    .getString(
                                            R.string.tv_menu_channel_installation_manual_tunning_settings_symbol_rate_dvbs_hint));
                    isFreqOK = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // set frequency hint
            ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                    .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                    .setHint(ctx
                            .getResources()
                            .getString(
                                    R.string.tv_menu_channel_installation_manual_tunning_settings_frequency_hint));
            // set symbol rate hint
            ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                    .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                    .setHint(ctx
                            .getResources()
                            .getString(
                                    R.string.tv_menu_channel_installation_manual_tunning_settings_symbol_rate_dvbs_hint));
            // initial selections for spinners
            ((A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SCRAMBLED))
                    .setSelection(0);
            ((A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FEC))
                    .setSelection(0);
            // get polarization
            Polarization polarization = Polarization.NOT_DEFINED;
            try {
                polarization = MainActivity.service.getScanControl()
                        .getPolarization();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (polarization == Polarization.VERTICAL) {
                ((A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_polarization))
                        .setSelection(1);
            } else {
                ((A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_polarization))
                        .setSelection(0);
            }
        }
        // set for ATV
        if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
            String frequency = "";
            int freq = 0;
            try {
                analogFreq = MainActivity.service.getScanControl()
                        .getFrequency();
                analogFreqHigh = analogFreq + ATV_FINE_TUNE_THRESHOLD;
                analogFreqLow = analogFreq - ATV_FINE_TUNE_THRESHOLD;
                if (freq > 0) {
                    frequency = String.valueOf(freq);
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                            .setText(frequency);
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                            .setSelection(frequency.length());
                } else {
                    // set frequency hint
                    ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                            .setHint(ctx
                                    .getResources()
                                    .getString(
                                            R.string.tv_menu_channel_installation_manual_tunning_settings_frequency_hint));
                    isFreqOK = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                AnalogEncodingMode analogEncodingMode = MainActivity.service
                        .getScanControl().getAnalogEncodingMode();
                ((A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYSTEM))
                        .setSelection(analogEncodingMode.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            A4TVButton fineButton = (A4TVButton) ChannelInstallationManualTunningDialog.this
                    .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FINE_TUNE);
            fineButton.setBackgroundResource(R.drawable.arrows_back_forward);
            fineButton.setText("");
            fineButton.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        // int freq = 0;
                        // try {
                        // freq = MainActivity.service.getScanControl()
                        // .getFrequency();
                        // } catch (RemoteException e1) {
                        // // TODO Auto-generated catch block
                        // e1.printStackTrace();
                        // }
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_LEFT: {
                                try {
                                    if (analogFreqLow < analogFreq) {
                                        analogFreq = analogFreq
                                                - ATV_FINE_TUNE_STEP;
                                    } else {
                                        analogFreq = analogFreqLow;
                                    }
                                    MainActivity.service.getScanControl()
                                            .atvFineTune(analogFreq, false);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                try {
                                    if (analogFreq < analogFreqHigh) {
                                        analogFreq = analogFreq
                                                + ATV_FINE_TUNE_STEP;
                                    } else {
                                        analogFreq = analogFreqHigh;
                                    }
                                    MainActivity.service.getScanControl()
                                            .atvFineTune(analogFreq, false);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_ENTER:
                            case KeyEvent.KEYCODE_DPAD_CENTER: {
                                try {
                                    MainActivity.service.getScanControl()
                                            .atvFineTune(analogFreq, true);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
        }
        // set for DVB-C
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
            // set frequency
            String frequency = "";
            int freq = 0;
            try {
                // freq = MainActivity.service.getScanControl().getFrequency();
                if (freq > 0) {
                    frequency = String.valueOf(freq);
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                            .setText(frequency);
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                            .setSelection(frequency.length());
                } else {
                    // set frequency hint
                    ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                            .setHint(ctx
                                    .getResources()
                                    .getString(
                                            R.string.tv_menu_channel_installation_manual_tunning_settings_frequency_hint));
                    isFreqOK = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // set symbol rate
            String symbolRate;
            int sRate = 6900;
            try {
                // sRate =
                // MainActivity.service.getScanControl().getSymbolRate();
                if (sRate >= 0) {
                    symbolRate = String.valueOf(sRate);
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                            .setText("");
                    ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                            .setSelection(symbolRate.length());
                } else {
                    isFreqOK = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Modulation modulation = MainActivity.service.getScanControl()
                        .getModulation();
                ((A4TVSpinner) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_MODULATION))
                        .setSelection(modulation.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // set symbol rate hint
            ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                    .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                    .setHint(ctx
                            .getResources()
                            .getString(
                                    R.string.tv_menu_channel_installation_manual_tunning_settings_symbol_rate_dvbc_hint));
            if (!nordic) {
                // hide network id if it is not nordic
                findViewById(
                        R.string.tv_menu_channel_installation_manual_tunning_settings_network_id)
                        .setVisibility(View.GONE);
                // hide line
                findViewById(DialogCreatorClass.LINES_BASE_ID + 4)
                        .setVisibility(View.GONE);
            }
        }
        // set listeners for frequency edit text
        if (fromHisClass) {
            if (selectedTunerType != ChannelInstallationDialog.TUNER_DVBT) {
                // set listener to edit text
                ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                        .addTextChangedListener(new TextWatcher() {
                            @Override
                            public void onTextChanged(CharSequence s,
                                    int start, int before, int count) {
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s,
                                    int start, int count, int after) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (s.length() > 0) {
                                    isFreqOK = false;
                                    Integer freq = Integer.valueOf(s.toString());
                                    Log.d("Frequency", freq + "");
                                    // IRD accepts an input signal in the range
                                    // 950
                                    // - 2150 MHz
                                    if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
                                        if (freq >= dvbSFreqDown
                                                && freq <= dvbSFreqUp) {
                                            isFreqOK = true;
                                        } else {
                                            isFreqOK = false;
                                        }
                                        // /////////////////////////////////////ADDED
                                        // int strength = 100;
                                        // IScanControl control = null;
                                        // try {
                                        // control = MainActivity.service
                                        // .getScanControl();
                                        //
                                        // } catch (RemoteException e) {
                                        // e.printStackTrace();
                                        // } catch (NullPointerException e) {
                                        // e.printStackTrace();
                                        // }
                                        // try {
                                        // if (control != null) {
                                        // control.setFrequency(freq);
                                        // strength = control
                                        // .getSignalInfo()
                                        // .getSignalStrenght();
                                        // }
                                        // } catch (RemoteException e) {
                                        // e.printStackTrace();
                                        // } catch (NullPointerException e) {
                                        // e.printStackTrace();
                                        // }
                                        // A4TVProgressBar progress =
                                        // (A4TVProgressBar)
                                        // findViewById(TV_MENU_CHANNEL_INSTALLATION_SIGNAL_INFO_SIGNAL_STRENGTH);
                                        // progress.setProgress(strength);
                                        // ///////////////////////////////ADDED
                                    }
                                    // Digital signals 110 - 862 MHz
                                    if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
                                        if (freq >= dvbCFreqDown
                                                && freq <= dvbCFreqUp) {
                                            isFreqOK = true;
                                        } else {
                                            isFreqOK = false;
                                        }
                                        // /////////////////////////////////////ADDED
                                        // int strength = 100;
                                        // IScanControl control = null;
                                        // try {
                                        // control = MainActivity.service
                                        // .getScanControl();
                                        //
                                        // } catch (RemoteException e) {
                                        // e.printStackTrace();
                                        // } catch (NullPointerException e) {
                                        // e.printStackTrace();
                                        // }
                                        // try {
                                        // if (control != null) {
                                        // control.setFrequency(freq);
                                        // strength = control
                                        // .getSignalInfo()
                                        // .getSignalStrenght();
                                        // }
                                        // } catch (RemoteException e) {
                                        // e.printStackTrace();
                                        // } catch (NullPointerException e) {
                                        // e.printStackTrace();
                                        // }
                                        // A4TVProgressBar progress =
                                        // (A4TVProgressBar)
                                        // findViewById(TV_MENU_CHANNEL_INSTALLATION_SIGNAL_INFO_SIGNAL_STRENGTH);
                                        // progress.setProgress(strength);
                                        // ///////////////////////////////ADDED
                                    }
                                    // Analog signals 42 - 863 MHz
                                    if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
                                        if (freq >= atvFreqDown
                                                && freq <= atvFreqUp) {
                                            isFreqOK = true;
                                        } else {
                                            isFreqOK = false;
                                        }
                                    }
                                }
                            }
                        });
            } else {
                if (nordic) {
                    spinnerNordicType
                            .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                                @Override
                                public void onSelect(A4TVSpinner spinner,
                                        int index, String[] contents) {
                                    if ((ChannelInstallationManualTunningDialog.this
                                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NORDIC_BAND))
                                            .isShown()) {
                                        switch (index) {
                                            case 0: {
                                                channelBand = ctx
                                                        .getResources()
                                                        .getString(
                                                                R.string.nordic_band_s);
                                                break;
                                            }
                                            case 1: {
                                                channelBand = ctx
                                                        .getResources()
                                                        .getString(
                                                                R.string.nordic_band_d);
                                                break;
                                            }
                                            case 2: {
                                                channelBand = ctx
                                                        .getResources()
                                                        .getString(
                                                                R.string.nordic_band_k);
                                                break;
                                            }
                                            default:
                                                break;
                                        }
                                    }
                                    channelNordic = channelBand + channelNumber;
                                    if (NORDIC.containsKey(channelNordic)) {
                                        isFreqOK = true;
                                    } else {
                                        isFreqOK = false;
                                    }
                                }
                            });
                }
                // set listener to edit text
                ((EditText) findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NUMBER_DVBT))
                        .addTextChangedListener(new TextWatcher() {
                            @Override
                            public void onTextChanged(CharSequence s,
                                    int start, int before, int count) {
                            }

                            @Override
                            public void beforeTextChanged(CharSequence s,
                                    int start, int count, int after) {
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (nordic) {
                                    channelNumber = s.toString();
                                    channelNordic = channelBand + channelNumber;
                                    if (NORDIC.containsKey(channelNordic)) {
                                        isFreqOK = true;
                                    } else {
                                        isFreqOK = false;
                                    }
                                } else {
                                    if (s.length() > 0) {
                                        int progress = 100;
                                        Integer freq = Integer.valueOf(s
                                                .toString());
                                        Log.d("Frequency2", progress + " freq "
                                                + freq);
                                        if ((freq >= DVB_T_CHANNEL_NUMBER_DOWN && freq <= DVB_T_CHANNEL_NUMBER_UP)
                                                || (freq >= DVB_T_CHANNEL_FREQ_DOWN && freq <= DVB_T_CHANNEL_FREQ_UP)) {
                                            isFreqOK = true;
                                        } else {
                                            isFreqOK = false;
                                        }
                                    } else {
                                        isFreqOK = false;
                                    }
                                }
                            }
                        });
            }
            // init handler
            handlerScanStarted = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0) {
                        if (ChannelScanDialog.isScanning()) {
                            // hide layout for messages
                            MainActivity.activity.findViewById(
                                    R.id.linLayMessages).setVisibility(
                                    View.GONE);
                            showScanDialogAndSetup();
                            // hide other dialogs
                            ChannelInstallationManualTunningDialog.this
                                    .cancel();
                            ChannelInstallationDialog chDialog = MainActivity.activity
                                    .getDialogManager()
                                    .getChannelInstallationDialog();
                            if (chDialog != null) {
                                chDialog.cancel();
                            }
                            MainActivity.activity.getMainMenuHandler()
                                    .closeMainMenu(false);
                        } else {
                            // A4TVToast toast = new A4TVToast(getContext());
                            // toast.showToast(R.string.no_signal);
                        }
                    }
                    super.handleMessage(msg);
                }
            };
        }
    }

    private void showScanDialogAndSetup() {
        // show dialog
        ChannelScanDialog scanDialog = MainActivity.activity.getDialogManager()
                .getChannelScanDialog();
        Log.d("MANUAL TUNING", "SHOW SCAN DIALOG");
        // show dialog
        if (scanDialog != null) {
            scanDialog.show();
            // set text of top banner
            scanDialog
                    .getTextTopBanner()
                    .setText(
                            ctx.getResources()
                                    .getText(
                                            R.string.tv_menu_channel_installation_settings_manual_tunning));
        }
        // hide frequency text view
        // scanDialog.findViewById(R.id.linearLayoutForScannedFrequency)
        // .setVisibility(View.GONE);
        // set selected button for desired tuner type
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS
                && scanDialog != null) {
            scanDialog.selectFilter(ChannelScanDialog.FILTER_DVB_S_OPTION);
        } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT
                && scanDialog != null) {
            scanDialog.selectFilter(ChannelScanDialog.FILTER_DVB_T_OPTION);
        } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC
                && scanDialog != null) {
            scanDialog.selectFilter(ChannelScanDialog.FILTER_DVB_C_OPTION);
        } else if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV
                && scanDialog != null) {
            scanDialog.selectFilter(ChannelScanDialog.FILTER_ATV_OPTION);
        } else if (selectedTunerType == ChannelInstallationDialog.TUNER_IP
                && scanDialog != null) {
            scanDialog.selectFilter(ChannelScanDialog.FILTER_IP_OPTION);
        }
    }

    public boolean manualScan(int type, int frequency, boolean updateList,
            int symbolRate, Polarization polarization, FecType fec) {
        IScanControl scanControl = null;
        try {
            scanControl = MainActivity.service.getScanControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scanControl != null) {
            try {
                // //////////////////////////////////
                // Veljko Ilkic
                // //////////////////////////////////
                // scanControl.setSatelite(0);
                scanControl.setSatelite(ChannelInstallationDialog
                        .getSatelliteIndexFromSpinner());
                // //////////////////////////////////
                // Veljko Ilkic
                // //////////////////////////////////
                scanControl.setLnbType(0);
                scanControl.setLnbLow(9750);
                scanControl.setLnbHigh(10600);
                scanControl.setLnbBandType(BandType.BANDTYPE_KU);
                scanControl.setSymbolRate(symbolRate);
                scanControl.setPolarization(polarization);
                scanControl.setFecType(fec);
                scanControl.setFrequency(frequency);
                return scanControl.manualScan(type, updateList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * set scanning parametres
     */
    private void setScanParams() {
        try {
            /*************************************************************************/
            // set scan params for DVB-T
            if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT) {
                // get text from frequency edit text and set it to scan
                if (nordic) {
                    Integer nordicFreq = NORDIC.get(channelNordic);
                    if (nordicFreq != null) {
                        MainActivity.service.getScanControl().setFrequency(
                                nordicFreq);
                    }
                } else {
                    EditText freqEditText = (EditText) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NUMBER_DVBT);
                    MainActivity.service.getScanControl().setFrequency(
                            Integer.valueOf(freqEditText.getText().toString()));
                }
            }
            /*************************************************************************/
            // set scan params for DVB-S
            if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
                // get text from frequency edit text and set it to scan
                freq = Integer
                        .valueOf(((EditText) ChannelInstallationManualTunningDialog.this
                                .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                                .getText().toString());
                /** Set polarization */
                if ((ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_polarization))
                        .isShown()) {
                    int index = ((A4TVSpinner) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_polarization))
                            .getCHOOSEN_ITEM_INDEX();
                    polarization = Polarization.values()[index];
                    if (polarization == Polarization.NOT_DEFINED) {
                        MainActivity.service.getScanControl().setPolarization(
                                polarization);
                    }
                }
                // set symbol rate
                if ((ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                        .isShown()) {
                    String number = ((A4TVEditText) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                            .getText().toString();
                    symbolRate = Integer.valueOf(number);
                    // MainActivity.service.getScanControl().setSymbolRate(
                    // Integer.valueOf(number));
                }
                /** Set scrambled */
                if ((ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SCRAMBLED))
                        .isShown()) {
                    int index = ((A4TVSpinner) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SCRAMBLED))
                            .getCHOOSEN_ITEM_INDEX();
                    switch (index) {
                        case 0: {
                            scrambled = ScramblingMode.ALL_SERVICES;
                            // MainActivity.service
                            // .getScanControl()
                            // .setScramblingMode(SCRAMBLING_MODE.ALL_SERVICES);
                            break;
                        }
                        case 1: {
                            scrambled = ScramblingMode.ONLY_SCRAMBLED;
                            // MainActivity.service.getScanControl()
                            // .setScramblingMode(
                            // SCRAMBLING_MODE.ONLY_SCRAMBLED);
                            break;
                        }
                        case 2: {
                            scrambled = ScramblingMode.ONLY_FREE;
                            // MainActivity.service.getScanControl()
                            // .setScramblingMode(SCRAMBLING_MODE.ONLY_FREE);
                            break;
                        }
                        default:
                            break;
                    }
                }
                /** Set fec */
                if ((ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FEC))
                        .isShown()) {
                    int index = ((A4TVSpinner) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FEC))
                            .getCHOOSEN_ITEM_INDEX();
                    fec = FecType.values()[index];
                }
            }
            /*************************************************************************/
            // set scan params for DVB-C
            if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
                // get text from frequency edit text and set it to scan
                MainActivity.service
                        .getScanControl()
                        .setFrequency(
                                Integer.valueOf(((EditText) ChannelInstallationManualTunningDialog.this
                                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                                        .getText().toString()));
                /** Set modulation */
                if ((ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_MODULATION))
                        .isShown()) {
                    int index = ((A4TVSpinner) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_MODULATION))
                            .getCHOOSEN_ITEM_INDEX();
                    MainActivity.service.getScanControl().setModulation(
                            Modulation.values()[index]);
                }
                // set symbol rate
                if ((ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                        .isShown()) {
                    MainActivity.service
                            .getScanControl()
                            .setSymbolRate(
                                    Integer.valueOf(((A4TVEditText) ChannelInstallationManualTunningDialog.this
                                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT))
                                            .getText().toString()));
                }
                /** set network id */
                // if (((A4TVEditText)
                // ChannelInstallationManualTunningDialog.this
                // .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_NETWORK_ID))
                // .isShown()) {
                // TODO set network ID here
                // }
            }
            /*************************************************************************/
            // set scan params for ATV
            if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
                // get text from frequency edit text and set it to scan
                MainActivity.service
                        .getScanControl()
                        .setFrequency(
                                Integer.valueOf(((EditText) ChannelInstallationManualTunningDialog.this
                                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY))
                                        .getText().toString()));
                /** Set system */
                if ((ChannelInstallationManualTunningDialog.this
                        .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYSTEM))
                        .isShown()) {
                    int index = ((A4TVSpinner) ChannelInstallationManualTunningDialog.this
                            .findViewById(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYSTEM))
                            .getCHOOSEN_ITEM_INDEX();
                    switch (index) {
                        case 0: {
                            MainActivity.service.getScanControl()
                                    .setAnalogEncodingMode(
                                            AnalogEncodingMode.PAL_BGH);
                            break;
                        }
                        case 1: {
                            MainActivity.service.getScanControl()
                                    .setAnalogEncodingMode(
                                            AnalogEncodingMode.PAL_I);
                            break;
                        }
                        case 2: {
                            MainActivity.service.getScanControl()
                                    .setAnalogEncodingMode(
                                            AnalogEncodingMode.PAL_D);
                            break;
                        }
                        case 3: {
                            MainActivity.service.getScanControl()
                                    .setAnalogEncodingMode(
                                            AnalogEncodingMode.SECAM_BGH);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
            /*************************************************************************/
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.drawable.channel_installation);
        titleIDs.add(R.string.tv_menu_channel_installation_manual_tunning_settings);
        // update list******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_manual_tunning_keep_current_list);
        list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_KEEP_CURRENT_LIST);
        contentListIDs.add(list);
        if (selectedTunerType != ChannelInstallationDialog.TUNER_DVBT) {
            // frequency******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVEditText);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_frequency);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY);
            contentListIDs.add(list);
        } else { // for DVB-T
            if (nordic) {
                // nordic type******************************************
                list = new ArrayList<Integer>();
                list.add(MainMenuContent.TAGA4TVTextView);
                list.add(MainMenuContent.TAGA4TVSpinner);
                contentList.add(list);
                list = new ArrayList<Integer>();
                list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_nordic_band);
                list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NORDIC_BAND);
                contentListIDs.add(list);
            }
            // frequency******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVEditText);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_channel_number_dvbt);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NUMBER_DVBT);
            contentListIDs.add(list);
        }
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
            // scrambled******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_scrambled);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SCRAMBLED);
            contentListIDs.add(list);
        }
        // symbol rate******************************************
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS
                || selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVEditText);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_symbol_rate);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT);
            contentListIDs.add(list);
        }
        // polarization******************************************
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_polarization);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_polarization);
            contentListIDs.add(list);
        }
        // fec******************************************
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_fec);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FEC);
            contentListIDs.add(list);
        }
        // modulation******************************************
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_modulation);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_MODULATION);
            contentListIDs.add(list);
        }
        // network ID ******************************************
        if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVEditText);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_network_id);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_NETWORK_ID);
            contentListIDs.add(list);
        }
        // system******************************************
        if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_system);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYSTEM);
            contentListIDs.add(list);
        }
        // fine tune******************************************
        if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_fine_tune);
            list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FINE_TUNE);
            contentListIDs.add(list);
        }
        // start search******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_manual_tunning_settings_start_search);
        list.add(TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_START_SEARCH);
        contentListIDs.add(list);
        // if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT) {
        // signal strength*******************************************
        // list = new ArrayList<Integer>();
        // list.add(MainMenuContent.TAGA4TVTextView);
        // list.add(MainMenuContent.TAGA4TVProgressBar);
        // contentList.add(list);
        //
        // list = new ArrayList<Integer>();
        // list.add(R.string.tv_menu_channel_installation_signal_info_signal_strength);
        // list.add(TV_MENU_CHANNEL_INSTALLATION_SIGNAL_INFO_SIGNAL_STRENGTH);
        // contentListIDs.add(list);
        // }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        ((A4TVProgressBar) seekBar).setText(String.valueOf(seekBar
                .getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
