package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.enums.ScanSignalType;
import com.iwedia.dtv.scan.SignalInfo;
import com.iwedia.dtv.service.Service;
import com.iwedia.dtv.service.ServiceDescriptor;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Manual tuning dialog
 * 
 * @author Branimir Pavlovic
 */
public class ChannelInstallationSignalInformationDialog extends A4TVDialog
        implements A4TVDialogInterface, android.view.View.OnClickListener,
        OnSeekBarChangeListener {
    public static final String TAG = "ChannelInstallationSignalInformationDialog";
    private int currentTunerType = -1;
    /** IDs for buttons in this dialog */
    public static final int tv_menu_channel_installation_signal_information_service_name = 100,
            tv_menu_channel_installation_signal_info_service_id = 101,
            tv_menu_channel_installation_signal_info_channel_id = 111,
            tv_menu_channel_installation_signal_info_multiplex = 102,
            tv_menu_channel_installation_signal_info_network = 103,
            tv_menu_channel_installation_signal_info_network_id = 108,
            tv_menu_channel_installation_signal_info_bit_error_level = 104,
            tv_menu_channel_installation_signal_info_centre_frequency = 110;
    /** IDs for edit text in this dialog */
    public static final int tv_menu_channel_installation_signal_information = 105;
    /** IDs for progress in this dialog */
    public static final int tv_menu_channel_installation_signal_info_signal_strength = 106,
            tv_menu_channel_installation_signal_info_signal_quality = 107;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Thread backgroundThread;
    private Runnable run;
    private Handler handler;
    private boolean nordic = false;
    private final String[] channelString = { "S1", "D1", "S2", "S3", "D2",
            "S4", "D3", "S5", "D4", "S6", "D5", "S7", "D6", "S8", "D7", "S9",
            "D8", "S10", "K5", "D9", "K6", "D10", "K7", "D11", "K8", "D12",
            "K9", "D13", "K10", "D14", "K11", "D15", "K12", "S11", "D16",
            "S12", "D17", "S13", "D18", "S14", "D19", "S15", "D20", "S16",
            "D21", "S17", "D22", "S18", "S19", "D23", "S20", "D24", "S21",
            "S22", "S23", "S24", "S25", "S26", "S27", "S28", "S29", "S30",
            "S31", "S32", "S33", "S34", "S35", "S36", "S37", "S38", "S39",
            "S40", "S41", "K21", "K22", "K23", "K24", "K25", "K26", "K27",
            "K28", "K29", "K30", "K31", "K32", "K33", "K34", "K35", "K36",
            "K37", "K38", "K39", "K40", "K41", "K42", "K43", "K44", "K45",
            "K46", "K47", "K48", "K49", "K50", "K51", "K52", "K53", "K54",
            "K55", "K56", "K57", "K58", "K59", "K60", "K61", "K62", "K63",
            "K64", "K65", "K66", "K67", "K68", "K69" };
    private final int[] frequency = { 107500, 114000, 114500, 121500, 122000,
            128500, 130000, 135500, 138000, 142500, 146000, 149500, 154000,
            156500, 162000, 163500, 170000, 170500, 177500, 178000, 184500,
            186000, 191500, 194000, 198500, 202000, 205500, 210000, 212500,
            218000, 219500, 226000, 226500, 233500, 234000, 240500, 242000,
            247500, 250000, 254500, 258000, 261500, 266000, 268500, 274000,
            275500, 282000, 282500, 289500, 290000, 296500, 298000, 306000,
            314000, 322000, 330000, 338000, 346000, 354000, 362000, 370000,
            378000, 386000, 394000, 402000, 410000, 418000, 426000, 434000,
            442000, 450000, 458000, 466000, 474000, 482000, 490000, 498000,
            506000, 514000, 522000, 530000, 538000, 546000, 554000, 562000,
            570000, 578000, 586000, 594000, 602000, 610000, 618000, 626000,
            634000, 642000, 650000, 658000, 666000, 674000, 682000, 690000,
            698000, 706000, 714000, 722000, 730000, 738000, 746000, 754000,
            762000, 770000, 778000, 786000, 794000, 802000, 810000, 818000,
            826000, 834000, 842000, 850000, 858000 };

    public ChannelInstallationSignalInformationDialog(Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        // init runnable to be run in thread
        run = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Thread thisThread = Thread.currentThread();
                    if (thisThread.equals(backgroundThread)) {
                        handler.sendEmptyMessage(0);
                        try {
                            // Sleep 5 seconds
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            }
        };
        // init handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                setValuesToViews(true);
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void show() {
        nordic = checkForNordic();
        startThread(run);
        // fillViewsWithData(true);
        super.show();
    }

    @Override
    public void onBackPressed() {
        stopThread();
        super.onBackPressed();
    }

    @Override
    public void cancel() {
        stopThread();
        super.cancel();
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, this, this, null);// ,
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

    /**
     * Set values to views
     * 
     * @param fromHisClass
     *        called from his class or not
     */
    public void setValuesToViews(boolean fromHisClass) {
        int freq = 0;
        // check what tunner is selected
        try {
            currentTunerType = MainActivity.service.getScanControl()
                    .getScanType();
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        /*
         * selectedTunerType = ((A4TVSpinner) MainActivity.activity
         * .getDialogManager() .getChannelInstallationDialog() .findViewById(
         * ChannelInstallationDialog
         * .TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE))
         * .getCHOOSEN_ITEM_INDEX();
         */
        SignalInfo signalInfo = null;
        ServiceDescriptor serviceDesc = null;
        try {
            Service activeService = MainActivity.service.getServiceControl()
                    .getActiveService();
            serviceDesc = MainActivity.service.getServiceControl()
                    .getServiceDescriptor(activeService.getListIndex(),
                            activeService.getServiceIndex());
            signalInfo = MainActivity.service.getScanControl().getSignalInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (signalInfo != null) {
            // set values to views
            ((A4TVButton) findViewById(tv_menu_channel_installation_signal_information_service_name))
                    .setText(serviceDesc.getName());
            ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_service_id))
                    .setText(String.valueOf(serviceDesc.getServiceId()));
            ((A4TVProgressBar) findViewById(tv_menu_channel_installation_signal_info_signal_strength))
                    .setProgress(signalInfo.getSignalStrenght());
            findViewById(
                    tv_menu_channel_installation_signal_info_signal_strength)
                    .setEnabled(false);
            ((A4TVProgressBar) findViewById(tv_menu_channel_installation_signal_info_signal_quality))
                    .setProgress(signalInfo.getSignalQuality());
            findViewById(
                    tv_menu_channel_installation_signal_info_signal_quality)
                    .setEnabled(false);
            if (currentTunerType == ScanSignalType.SIGNAL_TYPE_TERRESTRIAL) {
                freq = serviceDesc.getFrequency();
                if (nordic) {
                    if (ConfigHandler.TV_FEATURES) {
                        ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_channel_id))
                                .setText(channelString[freq - 1]);
                    }
                    ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_centre_frequency))
                            .setText((String
                                    .valueOf(frequency[freq - 1] / 1000))
                                    + " MHz");
                } else {
                    if (ConfigHandler.TV_FEATURES) {
                        ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_channel_id))
                                .setText(String.valueOf(String.valueOf(freq)));
                    }
                    ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_centre_frequency))
                            .setText(freq / 1000000 + " MHz");
                }
            } else {
                freq = (serviceDesc.getFrequency()) / 1000000;
                if (ConfigHandler.TV_FEATURES) {
                    ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_channel_id))
                            .setText("");
                }
                ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_centre_frequency))
                        .setText(String.valueOf(freq) + " MHz");
            }
            if (ConfigHandler.TV_FEATURES) {
                ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_network))
                        .setText(serviceDesc.getNetworkName());
                ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_multiplex))
                        .setText(Integer.toString(serviceDesc.getTSID()));
            }
            ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_network_id))
                    .setText(Integer.toString(serviceDesc.getONID()));
            ((A4TVButton) findViewById(tv_menu_channel_installation_signal_info_bit_error_level))
                    .setText(signalInfo.getBitErrorLevel());
        }
    }

    /**
     * Start background thread
     * 
     * @param run
     *        Runnable to run in thread
     */
    public void startThread(Runnable run) {
        Log.d(TAG, "start thread entered");
        if (backgroundThread == null) {
            backgroundThread = new Thread(run);
            backgroundThread.setPriority(Thread.MIN_PRIORITY);
            backgroundThread.start();
        }
    }

    /**
     * Stops background thread
     */
    public void stopThread() {
        Log.d(TAG, "stop thread entered");
        if (backgroundThread != null) {
            Thread moribund = backgroundThread;
            backgroundThread = null;
            moribund.interrupt();
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
        titleIDs.add(R.string.tv_menu_channel_installation_settings_signal_info);
        // service name ******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_service_name);
        list.add(tv_menu_channel_installation_signal_information_service_name);
        contentListIDs.add(list);
        // service id ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_service_id);
        list.add(tv_menu_channel_installation_signal_info_service_id);
        contentListIDs.add(list);
        if (ConfigHandler.TV_FEATURES) {
            // channel id ******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_signal_info_channel_id);
            list.add(tv_menu_channel_installation_signal_info_channel_id);
            contentListIDs.add(list);
        }
        // centre frequency
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_centre_frequency);
        list.add(tv_menu_channel_installation_signal_info_centre_frequency);
        contentListIDs.add(list);
        if (ConfigHandler.TV_FEATURES) {
            // multiplex ******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_signal_info_multiplex);
            list.add(tv_menu_channel_installation_signal_info_multiplex);
            contentListIDs.add(list);
            // network ******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_channel_installation_signal_info_network);
            list.add(tv_menu_channel_installation_signal_info_network);
            contentListIDs.add(list);
        }
        // network id ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_network_id);
        list.add(tv_menu_channel_installation_signal_info_network_id);
        contentListIDs.add(list);
        // bit error level ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_bit_error_level);
        list.add(tv_menu_channel_installation_signal_info_bit_error_level);
        contentListIDs.add(list);
        // signal strength*******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_signal_strength);
        list.add(tv_menu_channel_installation_signal_info_signal_strength);
        contentListIDs.add(list);
        // signal quality*******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_channel_installation_signal_info_signal_quality);
        list.add(tv_menu_channel_installation_signal_info_signal_quality);
        contentListIDs.add(list);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        Log.e("progress", "onProgressChanged progress:" + progress);
        if (currentTunerType == ScanSignalType.SIGNAL_TYPE_TERRESTRIAL
                && nordic) {
            if (progress < 34) {
                ((A4TVProgressBar) seekBar).setTextInformation(
                        String.valueOf(progress), "POOR");
            } else {
                if (progress < 67) {
                    ((A4TVProgressBar) seekBar).setTextInformation(
                            String.valueOf(progress), "FAIR");
                } else {
                    ((A4TVProgressBar) seekBar).setTextInformation(
                            String.valueOf(progress), "GOOD");
                }
            }
        } else {
            ((A4TVProgressBar) seekBar).setText(String.valueOf(seekBar
                    .getProgress()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
