package com.iwedia.gui.config_handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Configuration handler class
 * 
 * @author Veljko Ilkic
 */
@SuppressLint("DefaultLocale")
public class ConfigHandler {
    // /////////////////////////////////////////
    // Config file format
    // /////////////////////////////////////////
    // TV_FEATURES:YES/NO
    // #
    // DVBT:YES/NO
    // #
    // DVBS:YES/NO
    // #
    // DVBC:YES/NO
    // #
    // IP:YES/NO
    // #
    // ATV:YES/NO
    // #
    // ATSC:YES/NO
    // #
    // DTMB:YES/NO
    // #
    // SAT2IP:YES/NO
    // #
    // DLNA:YES/NO
    // #
    // DLNA_DMS: YES/NO
    // #
    // APP_SETTINGS: YES/NO
    // #
    // HBB:YES/NO
    // #
    // MHEG:YES/NO
    // #
    // CI:YES/NO
    // #
    // COMPLEX_AUDIO:YES/NO
    // #
    // TIMESHIFT:YES/NO
    // #
    // PVR: YES/NO
    // #
    // PVR_STORAGE(nand/usb): NAND/USB
    // #
    // PVR_THRESHOLD(1-100): 1-100
    // #
    // SEEK_OFFSET: 1-10
    // #
    // USE_LCN:YES/NO
    // #
    // CURL_GRAPHIC_QULAITY:YES/NO
    // /////////////////////////////////////////
    // Config file format
    // /////////////////////////////////////////
    /** Configuration file name */
    private static final String CONFIGURATION_FILE_NAME = "a4tv2.0_config.txt";
    /** Configuration file name */
    public static final String SCREENSAVER_IMAGE = "android_screensaver2";
    public static final String STORE_MODE_VIDEO = "ducks";
    /** Configuration file file path */
    private String mConfigFilePath = "";
    /** Usb device path */
    private static String sUsbPath = "";
    /** Line separator in config file */
    private static final String CONFIG_FILE_LINE_SEPARATOR = "#";
    // ///////////////////////////////////////////
    // Text constants
    // ///////////////////////////////////////////
    private static final String TV_FEATURES_TEXT = "TV_FEATURES";
    private static final String DVBT_TEXT = "DVBT";
    private static final String DVBS_TEXT = "DVBS";
    private static final String DVBC_TEXT = "DVBC";
    private static final String IP_TEXT = "IP";
    private static final String ATV_TEXT = "ATV";
    private static final String ATSC_TEXT = "ATSC";
    private static final String DTMB_TEXT = "DTMB";
    private static final String SAT2IP_TEXT = "SAT2IP";
    private static final String DLNA_TEXT = "DLNA";
    private static final String DLNA_DMS_TEXT = "DLNA_DMS";
    private static final String APP_SETTINGS_TEXT = "APP_SETTINGS";
    private static final String HBB_TEXT = "HBB";
    private static final String MHEG_TEXT = "MHEG";
    private static final String CI_TEXT = "CI";
    private static final String COMPLEX_AUDIO_TEXT = "COMPLEX_AUDIO";
    private static final String YES = "YES";
    private static final String CURL_TIME_MILIS = "CURL_TIME_MILIS";
    private static final String TIMESHIFT_TEXT = "TIMESHIFT";
    private static final String PVR_TEXT = "PVR";
    private static final String PVR_STORAGE_TEXT = "PVR_STORAGE(NAND/USB)";
    private static final String NAND_TEXT = "NAND";
    public static final String USB_TEXT = "USB";
    private static final String PVR_THRESHOLD_TEXT = "PVR_THRESHOLD(1-100)";
    private static final String SEEK_OFFSET_TEXT = "SEEK_OFFSET";
    private static final String USE_LCN_TEXT = "USE_LCN";
    private static final String TVPLATFORM_TEXT = "TVPLATFORM";
    private static final String CURL_GRAPHIC_QUALITY_TEXT = "CURL_GRAPHIC_QULAITY";
    // ///////////////////////////////////////////
    // Include flags
    // ///////////////////////////////////////////
    public static boolean TV_FEATURES = false;
    public static boolean DVB_T = true;
    public static boolean DVB_S = false;
    public static boolean DVB_C = false;
    public static boolean IP = true;
    public static boolean ATV = false;
    public static boolean ATSC = false;
    public static boolean DTMB = false;
    public static boolean SAT2IP = false;
    public static boolean DLNA = true;
    public static boolean DLNA_DMS = false;
    public static boolean APP_SETTINGS = true;
    public static boolean HBB = true;
    public static boolean MHEG = false;
    public static boolean CI = false;
    public static boolean COMPLEX_AUDIO = false;
    public static int CURL_TIME_MILIS_INT = 1000;
    public static boolean TIMESHIFT = true;
    public static boolean PVR = true;
    public static String PVR_STORAGE_STRING = NAND_TEXT;
    public static int PVR_THRESHOLD_INT = 90;
    public static int SEEK_OFFSET_INT = 2;
    public static boolean USE_LCN = false;
    public static boolean TVPLATFORM = false;
    public static boolean CURL_GRAPHIC_QUALITY = true;
    /** App context */
    private Context context;

    /** Constructor 1 */
    public ConfigHandler(Context context) {
        super();
        this.context = context;
    }

    /** Load configuration from config file */
    public void loadConfiguration() {
        // Init Default Path
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            mConfigFilePath = applicationInfo.dataDir;
            sUsbPath = applicationInfo.metaData.getString("USB_PATH");
        } catch (Exception e) {
        }
        // Get the text file
        File file = new File(mConfigFilePath, CONFIGURATION_FILE_NAME);
        if (file.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line = "";
                while (null != (line = br.readLine())) {
                    line = line.trim();
                    if (line.length() > 0) {
                        // Don't parse separator line
                        if (!line.equals(CONFIG_FILE_LINE_SEPARATOR)) {
                            // Transform line to upper case
                            line = line.toUpperCase();
                            String[] temp = line.split(":");
                            String featureName = temp[0].trim();
                            String featureInclude = temp[1].trim();
                            // //////////////////////////////
                            // Check feature name
                            // //////////////////////////////
                            // /////////////////////////////////
                            // TV FEATURES
                            // /////////////////////////////////
                            if (featureName.equals(TV_FEATURES_TEXT)) {
                                TV_FEATURES = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // DVB T
                            // /////////////////////////////////
                            if (featureName.equals(DVBT_TEXT)) {
                                DVB_T = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // DVB S
                            // /////////////////////////////////
                            if (featureName.equals(DVBS_TEXT)) {
                                DVB_S = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // DVB C
                            // /////////////////////////////////
                            if (featureName.equals(DVBC_TEXT)) {
                                DVB_C = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // IP
                            // /////////////////////////////////
                            if (featureName.equals(IP_TEXT)) {
                                IP = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // ATV
                            // /////////////////////////////////
                            if (featureName.equals(ATV_TEXT)) {
                                ATV = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // ATSC
                            // /////////////////////////////////
                            if (featureName.equals(ATSC_TEXT)) {
                                ATSC = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // DTMB
                            // /////////////////////////////////
                            if (featureName.equals(DTMB_TEXT)) {
                                DTMB = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // SAT2IP
                            // /////////////////////////////////
                            if (featureName.equals(SAT2IP_TEXT)) {
                                SAT2IP = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // DLNA
                            // /////////////////////////////////
                            if (featureName.equals(DLNA_TEXT)) {
                                DLNA = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // DLNA DMS
                            // /////////////////////////////////
                            if (featureName.equals(DLNA_DMS_TEXT)) {
                                DLNA_DMS = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // APP SETTINGS
                            // /////////////////////////////////
                            if (featureName.equals(APP_SETTINGS_TEXT)) {
                                APP_SETTINGS = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // HBB
                            // /////////////////////////////////
                            if (featureName.equals(HBB_TEXT)) {
                                HBB = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // MHEG
                            // /////////////////////////////////
                            if (featureName.equals(MHEG_TEXT)) {
                                MHEG = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // CI
                            // /////////////////////////////////
                            if (featureName.equals(CI_TEXT)) {
                                CI = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // COMPLEX SOUND SETTINGS
                            // /////////////////////////////////
                            if (featureName.equals(COMPLEX_AUDIO_TEXT)) {
                                COMPLEX_AUDIO = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // CURL TIME
                            // /////////////////////////////////
                            if (featureName.equals(CURL_TIME_MILIS)) {
                                CURL_TIME_MILIS_INT = Integer
                                        .parseInt(featureInclude.trim());
                            }
                            // /////////////////////////////////
                            // TIME SHIFT
                            // /////////////////////////////////
                            if (featureName.equals(TIMESHIFT_TEXT)) {
                                TIMESHIFT = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // PVR
                            // /////////////////////////////////
                            if (featureName.equals(PVR_TEXT)) {
                                PVR = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // PVR STORAGE
                            // /////////////////////////////////
                            if (featureName.equals(PVR_STORAGE_TEXT)) {
                                PVR_STORAGE_STRING = featureInclude.trim();
                            }
                            // /////////////////////////////////
                            // PVR THRESHOLD
                            // /////////////////////////////////
                            if (featureName.equals(PVR_THRESHOLD_TEXT)) {
                                PVR_THRESHOLD_INT = Integer
                                        .parseInt(featureInclude.trim());
                            }
                            // /////////////////////////////////
                            // SEEK OFFSET
                            // /////////////////////////////////
                            if (featureName.equals(SEEK_OFFSET_TEXT)) {
                                SEEK_OFFSET_INT = Integer
                                        .parseInt(featureInclude.trim());
                            }
                            // /////////////////////////////////
                            // USE_LCN
                            // /////////////////////////////////
                            if (featureName.equals(USE_LCN_TEXT)) {
                                USE_LCN = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // TVPLATFORM
                            // /////////////////////////////////
                            if (featureName.equals(TVPLATFORM_TEXT)) {
                                TVPLATFORM = isFeatureIncluded(featureInclude);
                            }
                            // /////////////////////////////////
                            // CURL_GRAPHIC_QULAITY
                            // /////////////////////////////////
                            if (featureName.equals(CURL_GRAPHIC_QUALITY_TEXT)) {
                                CURL_GRAPHIC_QUALITY = isFeatureIncluded(featureInclude);
                            }
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
                Log.e("CONFIG FILE", "SET DEFAULT VALUES");
            }
        }
        // Error while loading config file
        else {
            Log.e("CONFIG FILE", "SET DEFAULT VALUES");
        }
    }

    /** Check if feature is included in app */
    private boolean isFeatureIncluded(String includeFlag) {
        return (includeFlag.equals(YES));
    }

    public static String getUsbPath() {
        return sUsbPath;
    }
    // /////////////////////////////////////////////////////////////
    // Implementation notes
    // /////////////////////////////////////////////////////////////
    // ////////////////////////////
    // TV FEATURES:
    // ///////////////////////////
    // # Inputs in content list
    // # Inputs in main menu
    // # Input devices in settings menu
    // # Backlight option in picture settings
    // # Multiplex in signal info
    // # Network in signal info
    // # Channel id in signal info
    // ///////////////////////////
    // DVB T
    // ///////////////////////////
    // # DVB T tab in content list
    // # DVB T tab in scan dialog
    // # DVB T item in spinner in channel installation dialog
    // ///////////////////////////
    // DVB S
    // ///////////////////////////
    // # DVB S tab in content list
    // # DVB S tab in scan dialog
    // # DVB S item in spinner in channel installation dialog
    // # Satellite name option in channel installation dialog
    // ///////////////////////////
    // DVB C
    // ///////////////////////////
    // # DVB C tab in content list
    // # DVB C tab in scan dialog
    // # DVB C item in spinner in channel installation dialog
    // /////////////////////////////
    // IP
    // /////////////////////////////
    // # IP tab in content list
    // # IP tab in scan dialog
    // /////////////////////////////
    // HBB
    // /////////////////////////////
    // # Hbb init
    // # Hbb options in settings dialog
    // //////////////////////////////
    // MHEG
    // //////////////////////////////
    // # Mheg init
    // //////////////////////////////
    // CI
    // //////////////////////////////
    // #CI info in channel installation dialog
    // ///////////////////////////////
    // Complex audio
    // ///////////////////////////////
    // # Everything except balance, bass and treble in sound settings
    // # Treble in sound settings
    // # Bass in sound settings
    // //////////////////////////////
    // PVR
    // //////////////////////////////
    // #ENABLE/DISABLE PVR RECORD KEYS
    // //////////////////////////////
    // TIMESHIFT
    // //////////////////////////////
    // #ENABLE/DISABLE TIMESHIFT KEYS
    // //////////////////////////////
    // PVR STORAGE
    // //////////////////////////////
    // #DEFINE DEFAULT STORAGE FOR PVR RECORDINGS
    // ///////////////////////////////
    // SEEK OFFSET
    // ///////////////////////////////
    // #DEFINE VALUE OF SEEK OFFSET IN MEDIA PLAYER
}
