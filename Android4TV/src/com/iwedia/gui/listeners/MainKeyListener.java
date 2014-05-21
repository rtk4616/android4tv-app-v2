package com.iwedia.gui.listeners;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.dialogs.EPGScheduleDialog;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.keyhandlers.AppStateKeyHandler;
import com.iwedia.gui.keyhandlers.CleanScreenKeyHandler;
import com.iwedia.gui.keyhandlers.ContentListKeyHandler;
import com.iwedia.gui.keyhandlers.DLNARendererKeyHandler;
import com.iwedia.gui.keyhandlers.EPGKeyHandler;
import com.iwedia.gui.keyhandlers.InfoKeyHandler;
import com.iwedia.gui.keyhandlers.MainMenuKeyHandler;
import com.iwedia.gui.keyhandlers.MultimediaFirstKeyHandler;
import com.iwedia.gui.keyhandlers.MultimediaPVRKeyHandler;
import com.iwedia.gui.keyhandlers.MultimediaPlaybackKeyHandler;
import com.iwedia.gui.keyhandlers.MultimediaSecondKeyHandler;
import com.iwedia.gui.keyhandlers.PVRKeyHandler;
import com.iwedia.gui.keyhandlers.TeletextKeyHandler;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDHandlerHelper;

/**
 * Key listener for GUI application
 * 
 * @author Branimir Pavlovic
 */
public class MainKeyListener extends AppStateKeyHandler {
    private final static String TAG = "MainKeyListener";
    private final static boolean DEBUG = true;
    /** Possible app states for key handling */
    public static final int CLEAN_SCREEN = 0;
    public static final int MAIN_MENU = 1;
    public static final int CONTENT_LIST = 2;
    public static final int TELETEXT = 3;
    public static final int MULTIMEDIA_FIRST = 4;
    public static final int MULTIMEDIA_SECOND = 5;
    public static final int MULTIMEDIA_PVR = 6;
    public static final int EPG = 7;
    public static final int MULTIMEDIA_PLAYBACK = 8;
    public static final int PVR = 9;
    public static final int DLNA_RENDERER = 10;
    public static final int INFO = 11;
    /** Flag for content list opening */
    public static boolean contentListFromMainMenu = false;
    /** Flag for multimedia list opening */
    public static boolean multimediaFromMainMenu = false;
    /** Prevent main menu icons bug */
    public static boolean enableKeyCodeMenu = false;
    /** Prevent alert dialog showing bug */
    public static boolean enableKeyCodeBack = false;
    /** Current and stored app state */
    private static int storedAppState = CLEAN_SCREEN;
    private static int appState = CLEAN_SCREEN;
    private static AppStateKeyHandler currentKeyHandler;
    /* state key handlers */
    private static CleanScreenKeyHandler cleanScreenKeyHandler;
    private static MainMenuKeyHandler mainMenuKeyHandler;
    private static ContentListKeyHandler contentListKeyHandler;
    private static TeletextKeyHandler teletextKeyHandler;
    private static MultimediaFirstKeyHandler multimediaFirstKeyHandler;
    private static MultimediaSecondKeyHandler multimediaSecondKeyHandler;
    private static MultimediaPVRKeyHandler multimediaPVRKeyHandler;
    private static EPGKeyHandler epgKeyHandler;
    private static MultimediaPlaybackKeyHandler multimediaPlaybackKeyHandler;
    private static PVRKeyHandler pvrKeyHandler;
    private static DLNARendererKeyHandler dlnaRendererKeyHandler;
    private static InfoKeyHandler infoKeyHandler;
    /** Reference of main activity */
    private MainActivity activity;
    /** Progress dialog for loading data */
    private A4TVProgressDialog progressDialog;
    public static final int LOAD_BACK_LEVEL = 1;
    public static final int LOAD_BACK_FIRST_SCREEN = 2;
    public static final int LOAD_BACK_FIRST_SCREEN_FROM_PVR = 3;
    public static final int REOPEN_MULTIMEDIA = 4;

    /** Constructor 1 */
    public MainKeyListener(MainActivity activity) {
        this.activity = activity;
        // Create progress dialog object
        progressDialog = new A4TVProgressDialog(activity);
        progressDialog.setTitleOfAlertDialog(R.string.loading_data);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(R.string.please_wait);
        cleanScreenKeyHandler = new CleanScreenKeyHandler(activity);
        mainMenuKeyHandler = new MainMenuKeyHandler(activity);
        contentListKeyHandler = new ContentListKeyHandler(activity);
        teletextKeyHandler = new TeletextKeyHandler(activity);
        multimediaFirstKeyHandler = new MultimediaFirstKeyHandler(activity);
        multimediaSecondKeyHandler = new MultimediaSecondKeyHandler(activity);
        multimediaPVRKeyHandler = new MultimediaPVRKeyHandler(activity);
        epgKeyHandler = new EPGKeyHandler(activity);
        multimediaPlaybackKeyHandler = new MultimediaPlaybackKeyHandler(
                activity);
        pvrKeyHandler = new PVRKeyHandler(activity, this);
        dlnaRendererKeyHandler = new DLNARendererKeyHandler(activity);
        infoKeyHandler = new InfoKeyHandler(activity);
        currentKeyHandler = null;
        setAppState(CLEAN_SCREEN);
    }

    /**
     * Main key handling function
     * 
     * @param v
     * @param dialog
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        boolean noOperationOff = false;
        if (MainActivity.service != null && currentKeyHandler != null) {
            try {
                noOperationOff = MainActivity.service.getSetupControl()
                        .getNoOperationOff();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (noOperationOff) {
                try {
                    MainActivity.service.getSetupControl()
                            .offOperationTimerStart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MainActivity.activity.getScreenSaverDialog()
                    .updateScreensaverTimer();
            if (DEBUG) {
                Log.d(TAG, " - keycode = " + keyCode + " App State: "
                        + appState);
            }
            return currentKeyHandler.onKeyPressed(v, dialog, keyCode, event,
                    isFromMheg);
        }
        return false;
    }

    /**
     * Get CleanScreen Handler.
     */
    public CleanScreenKeyHandler getCleanScreenHandler() {
        return cleanScreenKeyHandler;
    }

    public static int getAppState() {
        return appState;
    }

    public static void setAppState(int state) {
        switch (state) {
            case CLEAN_SCREEN: {
                currentKeyHandler = cleanScreenKeyHandler;
                break;
            }
            case MAIN_MENU: {
                currentKeyHandler = mainMenuKeyHandler;
                break;
            }
            case CONTENT_LIST: {
                currentKeyHandler = contentListKeyHandler;
                break;
            }
            case TELETEXT: {
                currentKeyHandler = teletextKeyHandler;
                break;
            }
            case MULTIMEDIA_FIRST: {
                currentKeyHandler = multimediaFirstKeyHandler;
                break;
            }
            case MULTIMEDIA_SECOND: {
                currentKeyHandler = multimediaSecondKeyHandler;
                break;
            }
            case MULTIMEDIA_PVR: {
                currentKeyHandler = multimediaPVRKeyHandler;
                break;
            }
            case EPG:
                currentKeyHandler = epgKeyHandler;
                break;
            case MULTIMEDIA_PLAYBACK: {
                currentKeyHandler = multimediaPlaybackKeyHandler;
                break;
            }
            case PVR: {
                currentKeyHandler = pvrKeyHandler;
                break;
            }
            case DLNA_RENDERER: {
                currentKeyHandler = dlnaRendererKeyHandler;
                break;
            }
            case INFO: {
                currentKeyHandler = infoKeyHandler;
                break;
            }
            default:
                Log.e(TAG, "Should not be here - unknown state code: " + state);
                return;
        }
        appState = state;
        if (DEBUG) {
            Log.d(TAG, "Application state changed to: " + appState);
        }
    }

    public static void stopreAppState() {
        storedAppState = appState;
        Log.d(TAG, "Store application state: " + storedAppState);
    }

    public static void returnToStoredAppState() {
        Log.d(TAG, "Returning to stored application state: " + storedAppState);
        // need to check if an error oes or error occured and change needs to be
        // updated ...
        Log.d(TAG, "Checking for state change ...");
        Log.d(TAG, "CurlHandler state: " + OSDHandlerHelper.getHandlerState());
        // NEED STATE MACHINE, PVR CONTROL TO BE AWARE OF PVR/TIMESHIFT STATE,
        // MULTIMEDIA CONTROL
        // TO BE AWARE OF MULTIMEDIA PLAYBACK AND SO ON ... THIS STATE HANDLING
        // IS UGLY HACKERY STUFF
        switch (storedAppState) {
            case PVR:
                /** Checking PVR state */
                if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                    Log.d(TAG, "Still recording ... ");
                    // MainActivity.activity.getPageCurl().multimediaControllerPVR(
                    // false);
                } else if (OSDHandlerHelper.getHandlerState() == PVR_STATE_PLAY_TIME_SHIFT
                        || OSDHandlerHelper.getHandlerState() == PVR_STATE_PAUSE_TIME_SHIFT
                        || OSDHandlerHelper.getHandlerState() == PVR_STATE_REW_TIME_SHIFT
                        || OSDHandlerHelper.getHandlerState() == PVR_STATE_FF_TIME_SHIFT) {
                    Log.d(TAG, "Still in timeshift ... ");
                    // MainActivity.activity.getPageCurl().multimediaControllerPVR(
                    // false);
                } else {
                    Log.d(TAG,
                            "Timeshift/recordig has finished return to clean state ... ");
                    storedAppState = CLEAN_SCREEN;
                }
                break;
            case MULTIMEDIA_PLAYBACK:
                // check is it still playing otherwise we go back to
                // CELAN_SCREEN
                if (OSDHandlerHelper.getHandlerState() == PVR_STATE_PLAY_PLAY_BACK
                        || OSDHandlerHelper.getHandlerState() == PVR_STATE_PAUSE_PLAY_BACK
                        || OSDHandlerHelper.getHandlerState() == PVR_STATE_FF_PLAY_BACK
                        || OSDHandlerHelper.getHandlerState() == PVR_STATE_REW_PLAY_BACK) {
                    Log.d(TAG, "Still in PVR playback state");
                } else if (OSDHandlerHelper.getHandlerState() == CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER
                        && A4TVMultimediaController.getControlProvider()
                                .getFlagPlay() == true) {
                    Log.d(TAG, "Still in multimedia playback mode ... ");
                } else {
                    Log.d(TAG,
                            "Multimedia playback has finished return to clean state ... ");
                    storedAppState = CLEAN_SCREEN;
                }
                break;
            default:
                // just in case ...
                storedAppState = CLEAN_SCREEN;
                break;
        }
        setAppState(storedAppState);
    }

    public int generateChannelNumber(int keycode) {
        switch (keycode) {
            case KeyEvent.KEYCODE_0:
                return 0;
            case KeyEvent.KEYCODE_1:
                return 1;
            case KeyEvent.KEYCODE_2:
                return 2;
            case KeyEvent.KEYCODE_3:
                return 3;
            case KeyEvent.KEYCODE_4:
                return 4;
            case KeyEvent.KEYCODE_5:
                return 5;
            case KeyEvent.KEYCODE_6:
                return 6;
            case KeyEvent.KEYCODE_7:
                return 7;
            case KeyEvent.KEYCODE_8:
                return 8;
            case KeyEvent.KEYCODE_9:
                return 9;
            default:
                if (ConfigHandler.ATSC) {
                    return -1;
                } else {
                    return 0;
                }
        }
    }

    /** Show recently apps */
    public void showRecentlyApps() {
        try {
            Intent intent = activity.getPackageManager()
                    .getLaunchIntentForPackage("factory.widgets.recentapps");
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeChannelUp() {
        IOSDHandler curlHandler = activity.getPageCurl();
        curlHandler
                .prepareChannelAndChange(SCENARIO_CHANNEL_CHANGE, CHANNEL_UP);
    }

    public void changeChannelDown() {
        IOSDHandler curlHandler = activity.getPageCurl();
        curlHandler.prepareChannelAndChange(SCENARIO_CHANNEL_CHANGE,
                CHANNEL_DOWN);
    }

    public void changeChannelTogglePrevious() {
        IOSDHandler curlHandler = activity.getPageCurl();
        curlHandler.prepareChannelAndChange(
                SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE,
                CHANNEL_TOGGLE_PREVIOUS);
    }

    public static void refreshExtendedInfo(int serviceIndex) {
        EPGScheduleDialog epgScheduleDialog = MainActivity.activity
                .getDialogManager().getEpgScheduleDialog();
        if (epgScheduleDialog != null) {
            epgScheduleDialog.setUpNewExtendedInfo(serviceIndex);
        }
    }

    /******************************************************************/
    /** Temp FIX, should be fixed in com.iewdia.service */
    private static int lastFilter = -1;

    public static int getLastFilter() {
        return lastFilter;
    }

    public static void setLastFilter(int lastFilter) {
        MainKeyListener.lastFilter = lastFilter;
    }

    public void filterHandler() throws RemoteException {
        switch (MainActivity.service.getContentListControl()
                .getActiveFilterIndex()) {
            case FilterType.APPS:
            case FilterType.INPUTS:
            case FilterType.MULTIMEDIA:
            case FilterType.PVR_RECORDED:
            case FilterType.PVR_SCHEDULED:
            case FilterType.WIDGETS:
            case FilterType.UNDEFINED: {
                if (getLastFilter() != -1) {
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(getLastFilter());
                } else {
                    Log.i(TAG, "ERROR IN FILTER FIX!!!");
                }
                break;
            }
            default: {
                MainKeyListener.setLastFilter(MainActivity.service
                        .getContentListControl().getActiveFilterIndex());
                break;
            }
        }
    }
}
