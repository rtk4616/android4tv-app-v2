package com.iwedia.gui.mainmenu;

import android.app.Activity;
import android.content.res.TypedArray;
import android.util.Log;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.config_handler.ConfigHandler;

/**
 * Content of main menu
 * 
 * @author Veljko Ilkic
 */
public class MainMenuContent {
    /** Current state of main menu */
    public static int currentState;
    public static int submenuRootResId;
    public static int submenuRootResIdPrevious;
    private static int mMenuID;
    public static final int DEFAULT_MENU = 0;
    public static final int HBB_TV_MENU = 1;
    /** Menu tree */
    /** Main Menu first level */
    public static final int MAIN_MENU = 0;
    /** Submenu second level */
    public static final int CONTENT_LIST = 1;
    public static final int MULTIMEDIA = 2;
    public static final int APPLICATIONS = 3;
    public static final int INPUT_SELECTION = 4;
    public static final int GOOGLE_PLAY = 5;
    public static final int SETTINGS = 6;
    /** Sub menu second level */
    public static final int TV_SETTINGS = 51;
    /** Sub menu third level */
    public static final int SECURITY_SETTINGS = 511;
    // //////////////////////////////////////////
    // Actions
    // //////////////////////////////////////////
    public static final int OPEN_DIALOG = 80;
    public static final int LOAD_SUBMENU = 81;
    // ///////////////////////////////////////////////
    // Dialog actions
    // ///////////////////////////////////////////////
    /** Level zero */
    public static final int OPEN_CONTENT_LIST = 90;
    public static final int OPEN_MULTIMEDIA = 91;
    public static final int OPEN_APPLICATIONS = 92;
    public static final int OPEN_INPUT_SELECTIONS = 93;
    public static final int OPEN_GOOGLE_PLAY = 94;
    /** Level one */
    public static final int OPEN_NETWORK_DIALOG = 100;
    public static final int OPEN_TIME_AND_DATE = 101;
    public static final int OPEN_LANGUAGE_AND_KEYBOARD_DIALOG = 102;
    public static final int OPEN_INPUT_DEVICES_DIALOG = 103;
    public static final int OPEN_APPLICATIONS_DIALOG = 104;
    public static final int OPEN_EXTERNAL_AND_LOCAL_FILE_STORAGE_DIALOG = 105;
    public static final int OPEN_ACCOUNT_AND_SYNC_DIALOG = 106;
    public static final int OPEN_VOICE_INPUT_DIALOG = 107;
    public static final int OPEN_DLNA_SETTINGS = 108;
    public static final int OPEN_PRODUCT_INFO = 109;
    public static final int OPEN_SOFTWARE_UPGRADE = 110;
    public static final int OPEN_ENERGY_SAVE = 111;
    public static final int OPEN_FACTORY_RESET = 112;
    public static final int OPEN_TIMER = 113;
    /** Level two */
    public static final int OPEN_CHANNEL_INSTALLATION_DIALOG = 200;
    public static final int OPEN_SOUND_SETTINGS_DIALOG = 201;
    public static final int OPEN_PICTURE_SETTINGS_DIALOG = 202;
    public static final int OPEN_SUBTITLE_DIALOG = 203;
    public static final int OPEN_HBB_SETTINGS_DIALOG = 204;
    public static final int OPEN_TELETEXT_SETTINGS_DIALOG = 205;
    public static final int OPEN_PVR_MENU = 206;
    /** Level three */
    public static final int OPEN_PARENTAL_GUIDANCE_DIALOG = 300;
    public static final int OPEN_PASSWORD_DIALOG = 301;
    public static final int OPEN_PROGRAM_BLOCKING_DIALOG = 302;
    /** level four */
    public static final int OPEN_CHANNEL_INSTALLATION_MANUAL_TUNING_DIALOG = 2001;
    public static final int OPEN_NETWORK_WIRELESS_SETTINGS_DIALOG = 1001;
    public static final int OPEN_NETWORK_ADVANCED_NETWORK_SETUP_DIALOG = 1002;
    // //////////////////////////////////////////
    // Default icons
    // //////////////////////////////////////////
    /** Main Menu Icons */
    public static Integer[] mainMenuIcons;
    /** Settings icons */
    public static Integer[] submenuSettings;
    /** TV Settings icons */
    public static Integer[] submenuSettingsTvSettings;
    /** Security sub menu icons */
    public static Integer[] submenuSettingsTvSecuritySettings;

    public MainMenuContent(Activity activity) {
        loadDefaultFromThemes(activity);
    }

    public void reloadFromThemes(int menuID) {
        switch (menuID) {
            case MAIN_MENU:
                mMenuID = menuID;
                Log.d(MainActivity.TAG, "HbbTV is not active load Main Menu");
                loadDefaultFromThemes(MainActivity.activity);
                break;
            case HBB_TV_MENU:
                mMenuID = menuID;
                Log.d(MainActivity.TAG, "HbbTV is active, load HbbTV menu");
                loadHBBFromThemes(MainActivity.activity);
                break;
            default:
                Log.e(MainActivity.TAG, "Unknown menu ID");
        }
    }

    private void loadHBBFromThemes(Activity activity) {
        TypedArray atts;
        int listID;
        int attsSize;
        // ///////////////////////////////////////////////////////
        // TV SETTINGS OPTIONS
        // ////////////////////////////////////////////////////////
        atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuIconsListsTVSettings2,
                        R.attr.MainMenuIconsListsTVSettings3,
                        R.attr.MainMenuIconsListsTVSettings5, });
        attsSize = atts.getIndexCount();
        mainMenuIcons = new Integer[attsSize];
        int j = 0;
        for (int i = 0; i < attsSize; i++) {
            listID = atts.getResourceId(i, 0);
            mainMenuIcons[j] = listID;
            j++;
        }
        atts.recycle();
    }

    private void loadDefaultFromThemes(Activity activity) {
        TypedArray atts;
        int listID;
        int attsSize;
        // //////////////////////////////////////////////////////
        // MAIN MENU OPTIONS
        // //////////////////////////////////////////////////////
        // get list of IDs of pictures
        atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuIconsListsMain0,
                        R.attr.MainMenuIconsListsMain1,
                        R.attr.MainMenuIconsListsMain2,
                        R.attr.MainMenuIconsListsMain3,
                        R.attr.MainMenuIconsListsMain4,
                        R.attr.MainMenuIconsListsMain5 });
        attsSize = atts.getIndexCount();
        // ////////////////////////////
        // NO TV FEATURES
        // ////////////////////////////
        if (!ConfigHandler.TV_FEATURES) {
            // //////////////////////////////////
            // Hide input icon
            // //////////////////////////////////
            mainMenuIcons = new Integer[attsSize - 1];
            int j = 0;
            for (int i = 0; i < (attsSize); i++) {
                if (i != 3) {
                    listID = atts.getResourceId(i, 0);
                    mainMenuIcons[j] = listID;// activity.getResources().getIntArray(listID);
                    j++;
                }
            }
        }
        // ////////////////////////////
        // DEFAULT config
        // ////////////////////////////
        else {
            mainMenuIcons = new Integer[attsSize];
            for (int i = 0; i < attsSize; i++) {
                listID = atts.getResourceId(i, 0);
                mainMenuIcons[i] = listID;// activity.getResources().getIntArray(listID);
            }
        }
        // /////////////////////////////////////////////////
        // SETTINGS OPTIONS
        // //////////////////////////////////////////////////
        atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuIconsListsSubSettings0,
                        R.attr.MainMenuIconsListsSubSettings1,
                        R.attr.MainMenuIconsListsSubSettings2,
                        R.attr.MainMenuIconsListsSubSettings3,
                        R.attr.MainMenuIconsListsSubSettings4,
                        R.attr.MainMenuIconsListsSubSettings5,
                        R.attr.MainMenuIconsListsSubSettings6,
                        R.attr.MainMenuIconsListsSubSettings7,
                        R.attr.MainMenuIconsListsSubSettings8,
                        R.attr.MainMenuIconsListsSubSettings9,
                        R.attr.MainMenuIconsListsSubSettings10,
                        R.attr.MainMenuIconsListsSubSettings11,
                        R.attr.MainMenuIconsListsSubSettings12,
                        R.attr.MainMenuIconsListsSubSettings13,
                        R.attr.MainMenuIconsListsSubSettings14 });
        attsSize = atts.getIndexCount();
        // ////////////////////////////
        // NO TV FEATURES OR DLNA
        // ////////////////////////////
        if ((!ConfigHandler.TV_FEATURES && ConfigHandler.DLNA)
                || (ConfigHandler.TV_FEATURES && !ConfigHandler.DLNA)) {
            // Hide input devices option
            if (!ConfigHandler.TV_FEATURES) {
                // hide input devices and energy save and voice input
                submenuSettings = new Integer[12];
            }
            if (!ConfigHandler.DLNA) {
                // hide dlna settings
                submenuSettings = new Integer[14];
            }
            int j = 0;
            for (int i = 0; i < attsSize; i++) {
                // Hide input devices and energy save and voice input
                if (!ConfigHandler.TV_FEATURES && i != 4 && i != 12 && i != 8) {
                    listID = atts.getResourceId(i, 0);
                    submenuSettings[j] = listID;
                    j++;
                }
                // Hide dlna
                if (!ConfigHandler.DLNA && i != 9) {
                    listID = atts.getResourceId(i, 0);
                    submenuSettings[j] = listID;
                    j++;
                }
            }
        }
        // //////////////////////////////
        // NO DLNA AND NO TV FEATURES
        // //////////////////////////////
        if (!ConfigHandler.DLNA && !ConfigHandler.TV_FEATURES) {
            // Hide input devices option
            submenuSettings = new Integer[attsSize - 4];
            int j = 0;
            for (int i = 0; i < 15; i++) {
                if (i != 4 && i != 9 && i != 12 && i != 8) {
                    listID = atts.getResourceId(i, 0);
                    submenuSettings[j] = listID;
                    j++;
                }
            }
        }
        // ////////////////////////////
        // DEFAULT config
        // ////////////////////////////
        if (ConfigHandler.DLNA && ConfigHandler.TV_FEATURES) {
            submenuSettings = new Integer[attsSize];
            for (int i = 0; i < attsSize; i++) {
                listID = atts.getResourceId(i, 0);
                submenuSettings[i] = listID;
            }
        }
        // ///////////////////////////////////////////////////////
        // TV SETTINGS OPTIONS
        // ////////////////////////////////////////////////////////
        atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuIconsListsTVSettings0,
                        R.attr.MainMenuIconsListsTVSettings1,
                        R.attr.MainMenuIconsListsTVSettings2,
                        R.attr.MainMenuIconsListsTVSettings3,
                        R.attr.MainMenuIconsListsTVSettings4,
                        R.attr.MainMenuIconsListsTVSettings5,
                        R.attr.MainMenuIconsListsTVSettings6,
                        R.attr.MainMenuIconsListsTVSettings7,
                        R.attr.MainMenuIconsListsTVSettings8,
                        R.attr.MainMenuIconsListsTVSettings9,
                        R.attr.MainMenuIconsListsTVSettings10,
                        R.attr.MainMenuIconsListsTVSettings11,
                        R.attr.MainMenuIconsListsTVSettings12 });
        attsSize = atts.getIndexCount();
        // ////////////////////////////
        // NO HBB
        // ////////////////////////////
        if (!ConfigHandler.HBB) {
            // TODO: Hbb Settings is not last one, this is not good and will not
            // work!
            // Hide HBB settings
            submenuSettingsTvSettings = new Integer[attsSize - 1];
            int j = 0;
            for (int i = 0; i < attsSize; i++) {
                if (i != 5) {
                    listID = atts.getResourceId(i, 0);
                    submenuSettingsTvSettings[j] = listID;
                    j++;
                }
            }
        }
        // ////////////////////////////
        // DEFAULT config
        // ////////////////////////////
        else {
            submenuSettingsTvSettings = new Integer[attsSize];
            for (int i = 0; i < attsSize; i++) {
                listID = atts.getResourceId(i, 0);
                submenuSettingsTvSettings[i] = listID;
            }
        }
        // // //////////////////////////////////////////
        // SECURITY SETTINGS
        // // //////////////////////////////////////////
        atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuIconsListsSecuritySettings0,
                        R.attr.MainMenuIconsListsSecuritySettings1,
                        R.attr.MainMenuIconsListsSecuritySettings2 });
        attsSize = atts.getIndexCount();
        // //////////////////////////////////////////////
        // NO TV FEATURES
        // //////////////////////////////////////////////
        if (!ConfigHandler.TV_FEATURES) {
            submenuSettingsTvSecuritySettings = new Integer[attsSize - 1];
            for (int i = 0; i < (attsSize - 1); i++) {
                listID = atts.getResourceId(i, 0);
                submenuSettingsTvSecuritySettings[i] = listID;
            }
        }
        // ///////////////////////////////////////////////
        // DEFAULT
        // ///////////////////////////////////////////////
        else {
            submenuSettingsTvSecuritySettings = new Integer[attsSize];
            for (int i = 0; i < attsSize; i++) {
                listID = atts.getResourceId(i, 0);
                submenuSettingsTvSecuritySettings[i] = listID;
            }
        }
        atts.recycle();
    }

    /** Check id resource and return next action */
    public static Integer checkIdResourceAction(int id) {
        // Check image id and return corresponding action
        for (int i = 0; i < mainMenuIcons.length - 1; i++) {
            if (id == mainMenuIcons[i]) {
                return MainMenuContent.OPEN_DIALOG;
            }
        }
        for (int i = 1; i < submenuSettings.length; i++) {
            if (id == submenuSettings[i]) {
                return MainMenuContent.OPEN_DIALOG;
            }
        }
        for (int i = 0; i < submenuSettingsTvSettings.length; i++) {
            if (i != 1) {
                if (id == submenuSettingsTvSettings[i]) {
                    return MainMenuContent.OPEN_DIALOG;
                }
            }
        }
        for (int i = 0; i < submenuSettingsTvSecuritySettings.length; i++) {
            if (id == submenuSettingsTvSecuritySettings[i]) {
                return MainMenuContent.OPEN_DIALOG;
            }
        }
        // ////////////////////////////
        // NO TV FEATURES
        // ////////////////////////////
        if (!ConfigHandler.TV_FEATURES) {
            // FIX indexes because there is no input options
            if (id == submenuSettingsTvSettings[1] || id == submenuSettings[0]
                    || id == mainMenuIcons[4]) {
                return MainMenuContent.LOAD_SUBMENU;
            }
        }
        // ////////////////////////////
        // DEFAULT config
        // ////////////////////////////
        else {
            if (id == submenuSettingsTvSettings[1] || id == submenuSettings[0]
                    || id == mainMenuIcons[5]) {
                return MainMenuContent.LOAD_SUBMENU;
            }
        }
        return -1;
    }

    /** TAG's for our views */
    public static final int TAGA4TVButton = 0, TAGA4TVSpinner = 1,
            TAGA4TVEditText = 2, TAGA4TVTextView = 3, TAGA4TVCheckBox = 4,
            TAGA4TVProgressBar = 5, TAGA4TVRadioButton = 6,
            TAGA4TVButtonSwitch = 7;

    /** Check resource id and return dialog id */
    public static int checkIdResourceDialog(int id) {
        switch (id) {
        // Main Menu level
            case R.drawable.content_list: {
                return OPEN_CONTENT_LIST;
            }
            case R.drawable.multimedia_icon: {
                return OPEN_MULTIMEDIA;
            }
            case R.drawable.applications: {
                return OPEN_APPLICATIONS;
            }
            case R.drawable.input_selection: {
                return OPEN_INPUT_SELECTIONS;
            }
            case R.drawable.google_play: {
                return OPEN_GOOGLE_PLAY;
            }
            // Settings level
            case R.drawable.network: {
                return OPEN_NETWORK_DIALOG;
            }
            case R.drawable.time_and_date: {
                return OPEN_TIME_AND_DATE;
            }
            case R.drawable.language_and_keyboard: {
                return OPEN_LANGUAGE_AND_KEYBOARD_DIALOG;
            }
            case R.drawable.input_devices: {
                return OPEN_INPUT_DEVICES_DIALOG;
            }
            case R.drawable.applications_settings: {
                return OPEN_APPLICATIONS_DIALOG;
            }
            case R.drawable.external_local_file_storage_settings: {
                return OPEN_EXTERNAL_AND_LOCAL_FILE_STORAGE_DIALOG;
            }
            case R.drawable.account_sync: {
                return OPEN_ACCOUNT_AND_SYNC_DIALOG;
            }
            case R.drawable.voice_input_output_settings: {
                return OPEN_VOICE_INPUT_DIALOG;
            }
            case R.drawable.main_menu_dlna_icon: {
                return OPEN_DLNA_SETTINGS;
            }
            case R.drawable.product_info: {
                return OPEN_PRODUCT_INFO;
            }
            case R.drawable.software_upgrade: {
                return OPEN_SOFTWARE_UPGRADE;
            }
            case R.drawable.energy_save: {
                return OPEN_ENERGY_SAVE;
            }
            case R.drawable.factory_reset: {
                return OPEN_FACTORY_RESET;
            }
            case R.drawable.timers: {
                return OPEN_TIMER;
            }
            // TV settings
            // Submenu tv settings
            case R.drawable.channel_installation: {
                return OPEN_CHANNEL_INSTALLATION_DIALOG;
            }
            case R.drawable.sound_settings: {
                return OPEN_SOUND_SETTINGS_DIALOG;
            }
            case R.drawable.picture_settings: {
                return OPEN_PICTURE_SETTINGS_DIALOG;
            }
            case R.drawable.subtitles: {
                return OPEN_SUBTITLE_DIALOG;
            }
            case R.drawable.hbbtv: {
                return OPEN_HBB_SETTINGS_DIALOG;
            }
            case R.drawable.teletext: {
                return OPEN_TELETEXT_SETTINGS_DIALOG;
            }
            case R.drawable.pvr_menu: {
                return OPEN_PVR_MENU;
            }
            // Submenu security settings
            case R.drawable.parential_guidance: {
                return OPEN_PARENTAL_GUIDANCE_DIALOG;
            }
            case R.drawable.password: {
                return OPEN_PASSWORD_DIALOG;
            }
            case R.drawable.program_blocking: {
                return OPEN_PROGRAM_BLOCKING_DIALOG;
            }
            default:
                return -1;
        }
    }

    /** Check resource id and return dialog id */
    public static A4TVDialog getDialogFromMainMenuResource(int id,
            MainActivity activity) {
        switch (id) {
        // Settings level
            case R.drawable.network: {
                return activity.getDialogManager().getNetworkSettingsDialog();
            }
            case R.drawable.time_and_date: {
                return activity.getDialogManager()
                        .getTimeAndDateSettingsDialog();
            }
            case R.drawable.language_and_keyboard: {
                return activity.getDialogManager()
                        .getLanguageAndKeyboardDialog();
            }
            case R.drawable.input_devices: {
                return activity.getDialogManager()
                        .getInputDevicesSettingsDialog();
            }
            case R.drawable.applications_settings: {
                return activity.getDialogManager()
                        .getApplicationsManageDialog();
            }
            case R.drawable.external_local_file_storage_settings: {
                return activity.getDialogManager()
                        .getExternalAndLocalStorageDialog();
            }
            case R.drawable.account_sync: {
                return activity.getDialogManager().getAccountsAndSyncDialog();
            }
            case R.drawable.voice_input_output_settings: {
                return activity.getDialogManager().getVoiceInputDialog();
            }
            case R.drawable.main_menu_dlna_icon: {
                return activity.getDialogManager().getDlnaSettingsDialog();
            }
            case R.drawable.product_info: {
                return activity.getDialogManager().getProductInfoDialog();
            }
            case R.drawable.software_upgrade: {
                return activity.getDialogManager().getSoftwareUpgradeDialog();
            }
            case R.drawable.energy_save: {
                return activity.getDialogManager().getEnergySaveDialog();
            }
            case R.drawable.factory_reset: {
                return activity.getDialogManager().getFactoryResetDialog();
            }
            case R.drawable.timers: {
                return activity.getDialogManager().getTimersSettingsDialog();
            }
            // TV settings
            // Submenu tv settings
            case R.drawable.channel_installation: {
                return activity.getDialogManager()
                        .getChannelInstallationDialog();
            }
            case R.drawable.sound_settings: {
                return activity.getDialogManager().getSoundSettingsDialog();
            }
            case R.drawable.picture_settings: {
                return activity.getDialogManager().getPictureSettingsDialog();
            }
            case R.drawable.subtitles: {
                return activity.getDialogManager().getSubtitleSettingsDialog();
            }
            case R.drawable.hbbtv: {
                return activity.getDialogManager().getHbbSettingsDialog();
            }
            case R.drawable.teletext: {
                return activity.getDialogManager().getTeletextSettingsDialog();
            }
            case R.drawable.pvr_menu: {
                return activity.getDialogManager().getPVRMenuDialog();
            }
            case R.drawable.screensaver: {
                return activity.getDialogManager()
                        .getScreensaverSettingsDialog();
            }
            case R.drawable.storemode: {
                return activity.getDialogManager().getStoreModeSettingsDialog();
            }
            case R.drawable.ci_icon: {
                return activity.getDialogManager().getCISettingsDialog();
            }
            case R.drawable.osd_selection_icon: {
                return activity.getDialogManager().getOSDSelectionDialog();
            }
            case R.drawable.pip_icon: {
                return activity.getDialogManager().getPiPSettingsDialog();
            }
            // Submenu security settings
            case R.drawable.parential_guidance: {
                return activity.getDialogManager().getParentalGuidanceDialog();
            }
            case R.drawable.password: {
                return activity.getDialogManager().getPasswordSecurityDialog();
            }
            default:
                return null;
        }
    }

    /**
     * Check id resource and return next or check current state and return
     * previous submenu that need to be loaded
     * 
     * @param id
     *        Resource id for forward direction
     * @param next_previous
     *        next or previous sub menu content ( true - next false-previous )
     * @return id of menu that will be loaded
     */
    public static int checkIdResourceNextSubmenu(int id, boolean next_previous) {
        // Direction = next
        if (next_previous) {
            // Check id
            switch (id) {
                case R.drawable.settings_icon: {
                    return MainMenuContent.SETTINGS;
                }
                case R.drawable.tv_settings: {
                    return MainMenuContent.TV_SETTINGS;
                }
                case R.drawable.security: {
                    return MainMenuContent.SECURITY_SETTINGS;
                }
                default:
                    return -1;
            }
        }
        // Direction = previous
        else {
            // Check current state
            switch (currentState) {
                case MainMenuContent.MAIN_MENU: {
                    submenuRootResId = 0;
                    return MainMenuContent.MAIN_MENU;
                }
                case MainMenuContent.SETTINGS: {
                    submenuRootResId = 0;
                    return MainMenuContent.MAIN_MENU;
                }
                case MainMenuContent.TV_SETTINGS: {
                    submenuRootResId = R.drawable.settings_icon;
                    return MainMenuContent.SETTINGS;
                }
                case MainMenuContent.SECURITY_SETTINGS: {
                    submenuRootResId = R.drawable.tv_settings;
                    return MainMenuContent.TV_SETTINGS;
                }
                default:
                    return -1;
            }
        }
    }
}
