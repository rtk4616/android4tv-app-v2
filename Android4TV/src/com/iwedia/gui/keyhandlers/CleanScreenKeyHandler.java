package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.teletext.TeletextMode;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.dtv.sound.AudioChannelMode;
import com.iwedia.dtv.types.AspectRatioMode;
import com.iwedia.dtv.types.UserControl;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVProgressBarPVR.ControlProviderPVR;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.AudioLanguageDialog;
import com.iwedia.gui.components.dialogs.PictureSettingsDialog;
import com.iwedia.gui.components.dialogs.SubtitleLanguageDialog;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.content_list.ContentListHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.osd.infobanner.InfoBannerHandler;
import com.iwedia.gui.osd.noneinfobanner.NoneBannerHandler;
import com.iwedia.gui.pvr.PVRHandler;

import java.util.Timer;

public class CleanScreenKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "CleanScreenKeyHandler";
    private MainActivity activity;
    private Timer t = null;
    // TODO: Applies only on main display
    private int mDisplayId = 0;
    private int err = 0;
    private boolean tempTxt = true;
    private int countTxt = 0;
    private Content previousContent;

    public CleanScreenKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            // ///////////////////////////////////////////////////////
            // Should hbb handle key
            // ///////////////////////////////////////////////////////
            if (hbbKeyHandler(keyCode)) {
                Log.d(TAG, "Key sent to WebView!!");
                MainActivity.webDialog.onKeyDown(keyCode, event);
                return true;
            }
            // ///////////////////////////////////////////////////////
            // Should mheg handle key
            // ///////////////////////////////////////////////////////
            // if(mhegKeyHandler(keyCode)) {
            if (!ConfigHandler.ATSC) {
                try {
                    if (MainActivity.service.getMhegControl().sendInputControl(
                            keyCode, UserControl.PRESSED)) {
                        return true;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // }
            Log.d(LOG_TAG, "KeyCode: " + keyCode);
            switch (keyCode) {
            // ////////////////////////////////
            // PIP
            // ////////////////////////////////
                case KeyEvent.KEYCODE_PROG_YELLOW:
                case KeyEvent.KEYCODE_Y: {
                    // Get active content object and stop secondary display
                    // playback
                    Log.d(TAG, "YELLOW KEY ");
                    if (MainActivity.activity.getDualVideoManager().isPiP()) {
                        MainActivity.activity.getDualVideoManager().stop(
                                MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                        return true;
                    }
                    // TODO: move to dualVideoManager
                    // check if renderer
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() != null) {
                        if (MainActivity.activity.getRendererController()
                                .getmRendererState() != 0) {
                            MainActivity.activity.getRendererController()
                                    .stop();
                        } else {
                            if (MainActivity.activity.getMultimediaMode() == MainActivity.MULTIMEDIA_PIP)
                                MainActivity.activity
                                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PIP);
                        }
                        return true;
                    }
                    return false;
                }
                // ////////////////////////////////
                // PAP
                // ////////////////////////////////
                case KeyEvent.KEYCODE_PROG_BLUE:
                case KeyEvent.KEYCODE_B: {
                    Log.d(TAG, "BLUE KEY ");
                    if (MainActivity.activity.getDualVideoManager().isPaP()) {
                        if (MainActivity.activity.getDualVideoManager().stop(
                                MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                            return true;
                        }
                    }
                    // TODO: move to dualVideoManager
                    // check if renderer
                    Log.d(TAG, "IS pap returned false - check if renderer ... ");
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() != null) {
                        if (MainActivity.activity.getRendererController()
                                .getmRendererState() != 0) {
                            MainActivity.activity.getRendererController()
                                    .stop();
                        } else {
                            if (MainActivity.activity.getMultimediaMode() == MainActivity.MULTIMEDIA_PAP)
                                MainActivity.activity
                                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PAP);
                        }
                        return true;
                    }
                    return false;
                }
                // /////////////////////////////////////
                // CONTENT LIST
                // /////////////////////////////////////
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER: {
                    if (MainActivity.activity.getPageCurl().getCurrentState() == STATE_CHANNEL_INFO) {
                        activity.getPageCurl().getExtendedInfo();
                    } else {
                        // Check if content list is initialized
                        if (activity.getContentListHandler() == null) {
                            activity.initContentList();
                        }
                        // Show content list dialog
                        activity.getContentListHandler().showContentList();
                        // Filter current filter
                        activity.getContentListHandler().filterContent(
                                ContentListHandler.CONTENT_LIST_LAST_FILTER,
                                true);
                        // Set flag to false
                        MainKeyListener.contentListFromMainMenu = false;
                    }
                    return true;
                }
                // ///////////////////////////////////////////////////
                // PVR
                // ///////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F12:
                case KeyEvent.KEYCODE_MEDIA_RECORD: {
                    // Check config file
                    if (ConfigHandler.PVR) {
                        if (OSDHandlerHelper.isServiceListEmpty()) {
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(R.string.empty_list);
                        } else {
                            boolean pvrRecordEnabled = false;
                            // Check which storage is used
                            if (ConfigHandler.PVR_STORAGE_STRING
                                    .equalsIgnoreCase(ConfigHandler.USB_TEXT)) {
                                // Check if usb is attached
                                if (PVRHandler.detectUSB()) {
                                    // Enable pvr recording
                                    pvrRecordEnabled = true;
                                } else {
                                    // Disable pvr recording and show message
                                    pvrRecordEnabled = false;
                                    // No usb drive
                                    A4TVToast toast = new A4TVToast(activity);
                                    toast.showToast(R.string.pvr_no_usb);
                                }
                            } else {
                                // Enable pvr recording for nand memory
                                pvrRecordEnabled = true;
                            }
                            // Check if feature is enabled
                            if (pvrRecordEnabled) {
                                ControlProviderPVR.setFileDescription(activity
                                        .getApplicationContext().getString(
                                                R.string.prepare_record));
                                A4TVProgressBarPVR.getControlProviderPVR()
                                        .record();
                                activity.getPageCurl().multimediaControllerPVR(
                                        false);
                            }
                        }
                    } else {
                        A4TVToast toast = new A4TVToast(activity);
                        toast.showToast(R.string.pvr_disabled);
                    }
                    return true;
                }
                case 126:
                case 127:
                case KeyEvent.KEYCODE_F11:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                    // Check config file
                    if (ConfigHandler.TIMESHIFT) {
                        if (OSDHandlerHelper.isServiceListEmpty()) {
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(R.string.empty_list);
                        } else {
                            boolean timeShiftEnabled = false;
                            // Check if timeshift is enabled
                            if (ConfigHandler.PVR_STORAGE_STRING
                                    .equalsIgnoreCase(ConfigHandler.USB_TEXT)) {
                                // Check if usb is attached
                                if (PVRHandler.detectUSB()) {
                                    // Enable feature
                                    timeShiftEnabled = true;
                                } else {
                                    timeShiftEnabled = false;
                                    // No usb drive
                                    A4TVToast toast = new A4TVToast(activity);
                                    toast.showToast(R.string.pvr_no_usb);
                                }
                            } else {
                                // Enable feature if nand is used
                                timeShiftEnabled = true;
                            }
                            // Check if feature is enabled
                            if (timeShiftEnabled) {
                                Log.d(TAG, "Start timeshift!");
                                ControlProviderPVR.setFileDescription(activity
                                        .getApplicationContext().getString(
                                                R.string.prepare_timeshift));
                                A4TVProgressBarPVR.getControlProviderPVR()
                                        .pause();
                                activity.getPageCurl().multimediaControllerPVR(
                                        false);
                            }
                        }
                    } else {
                        A4TVToast toast = new A4TVToast(activity);
                        toast.showToast(R.string.timeshift_disabled);
                    }
                    return true;
                }
                // ///////////////////////////////////////////////////
                // EPG
                // ///////////////////////////////////////////////////
                case KeyEvent.KEYCODE_E:
                case KeyEvent.KEYCODE_GUIDE:
                case KeyEvent.KEYCODE_PROG_GREEN:
                case KeyEvent.KEYCODE_SEARCH: {
                    if (0 != (MainActivity.getKeySet())) {
                        int command = 0;
                        String param = "EXIT";
                        try {
                            MainActivity.service.getHbbTvControl()
                                    .notifyAppMngr(command, param);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    // check if reminder is for current channel
                    Content content = null;
                    try {
                        content = MainActivity.service.getContentListControl()
                                .getActiveContent(mDisplayId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (content != null) {
                        if (content instanceof ServiceContent) {
                            if (activity.getEpgHandler() == null) {
                                activity.initEPG();
                            }
                            activity.getEpgHandler().showEPGDialog();
                            break;
                        } else {
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(com.iwedia.gui.R.string.epg_not_supported);
                        }
                    }
                }
                // ///////////////////////////////////////////////////
                // Left and Right keys
                // ///////////////////////////////////////////////////
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    if (OSDHandlerHelper.getHandlerState() != STATE_INFO_BANNER_SHOWN
                            || (OSDHandlerHelper.getHandlerState() == STATE_INFO_BANNER_SHOWN
                                    && activity.getPageCurl() instanceof InfoBannerHandler && !InfoBannerHandler.description)
                            || activity.getPageCurl() instanceof NoneBannerHandler) {
                        activity.getPageCurl().getNextChannelInfo();
                    } else {
                        activity.getPageCurl().updateChannelInfo(1);
                    }
                    return true;
                }
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    if (OSDHandlerHelper.getHandlerState() != STATE_INFO_BANNER_SHOWN
                            || (OSDHandlerHelper.getHandlerState() == STATE_INFO_BANNER_SHOWN
                                    && activity.getPageCurl() instanceof InfoBannerHandler && !InfoBannerHandler.description)
                            || activity.getPageCurl() instanceof NoneBannerHandler) {
                        activity.getPageCurl().getPreviousChannelInfo();
                    } else {
                        activity.getPageCurl().updateChannelInfo(-1);
                    }
                    return true;
                }
                // ///////////////////////////////////////////////
                // CHANNEL UP
                // ///////////////////////////////////////////////
                case KeyEvent.KEYCODE_CHANNEL_UP:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_F4: {
                    if (OSDHandlerHelper.getHandlerState() != STATE_INFO_BANNER_SHOWN
                            || (OSDHandlerHelper.getHandlerState() == STATE_INFO_BANNER_SHOWN
                                    && activity.getPageCurl() instanceof InfoBannerHandler && !InfoBannerHandler.description)
                            || activity.getPageCurl() instanceof NoneBannerHandler) {
                        try {
                            if (FilterType.INPUTS == MainActivity.service
                                    .getContentListControl()
                                    .getActiveContent(0).getFilterType()) {
                                A4TVToast toast = new A4TVToast(activity);
                                toast.showToast(R.string.not_supported_action_for_input);
                                return true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!activity.dualVideoActionHandler(CHANNEL_UP, 0)) {
                            return true;
                        }
                        activity.getMainKeyListener().changeChannelUp();
                    } else {
                        activity.getPageCurl().scroll(1);
                    }
                    return true;
                }
                // ///////////////////////////////////////////////////
                // CHANNEL DOWN
                // ///////////////////////////////////////////////////
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_F3: {
                    if (OSDHandlerHelper.getHandlerState() != STATE_INFO_BANNER_SHOWN
                            || (OSDHandlerHelper.getHandlerState() == STATE_INFO_BANNER_SHOWN
                                    && activity.getPageCurl() instanceof InfoBannerHandler && !InfoBannerHandler.description)
                            || activity.getPageCurl() instanceof NoneBannerHandler) {
                        try {
                            if (FilterType.INPUTS == MainActivity.service
                                    .getContentListControl()
                                    .getActiveContent(0).getFilterType()) {
                                A4TVToast toast = new A4TVToast(activity);
                                toast.showToast(R.string.not_supported_action_for_input);
                                return true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!activity.dualVideoActionHandler(CHANNEL_DOWN, 0)) {
                            return true;
                        }
                        activity.getMainKeyListener().changeChannelDown();
                    } else {
                        activity.getPageCurl().scroll(-1);
                    }
                    return true;
                }
                // ////////////////////////////////////////////////////
                // CHANNEL CIRCULAR
                // ////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F9: {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(R.string.swich_to_previous_content);
                    if (!activity.dualVideoActionHandler(
                            CHANNEL_TOGGLE_PREVIOUS, 0)) {
                        return true;
                    }
                    try {
                        Content activeContent = MainActivity.service
                                .getContentListControl().getActiveContent(0);
                        previousContent = MainActivity.service
                                .getContentListControl().getPreviousContent();
                        if (activeContent.getFilterType() == FilterType.INPUTS) {
                            /* Stop active input */
                            MainActivity.service.getContentListControl()
                                    .stopContent(activeContent, 0);
                            if (previousContent.getFilterType() != FilterType.INPUTS) {
                                if (previousContent.isSelectable() == true) {
                                    // Start curl animation
                                    ((MainActivity) activity).getPageCurl()
                                            .changeChannelByContent(
                                                    previousContent, 0);
                                } else {
                                    toast = new A4TVToast(activity);
                                    toast.showToast("Not selectable content");
                                }
                                return true;
                            }
                            ((MainActivity) activity)
                                    .setAnalogSignalLock(false);
                        }
                        if (previousContent.getFilterType() == FilterType.INPUTS) {
                            boolean isDisabled = false;
                            try {
                                isDisabled = MainActivity.service
                                        .getContentListControl()
                                        .getContentLockedStatus(previousContent);
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                            if (isDisabled == false) {
                                if (activeContent.getFilterType() != FilterType.INPUTS) {
                                    /* Stop active input */
                                    MainActivity.service
                                            .getContentListControl()
                                            .stopContent(activeContent, 0);
                                }
                                if (activeContent.getSourceType() != SourceType.ANALOG
                                        && activeContent.getFilterType() != FilterType.INPUTS
                                        && previousContent.getSourceType() == SourceType.ANALOG) {
                                    /*
                                     * Remove WebView from screen and set key
                                     * mask to 0
                                     */
                                    if (0 != (MainActivity.getKeySet())) {
                                        try {
                                            if (!activity
                                                    .isHbbTVInHTTPPlaybackMode()) {
                                                activity.webDialog
                                                        .getHbbTVView()
                                                        .setAlpha((float) 0.00);
                                                MainActivity.setKeySet(0);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                ((MainActivity) activity).sourceSwichingProgressDialog
                                        .show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            err = MainActivity.service
                                                    .getContentListControl()
                                                    .goContent(previousContent,
                                                            0);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            Thread.sleep(10000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                ((MainActivity) activity).sourceSwichingProgressDialog
                                                        .cancel();
                                                if (((MainActivity) activity)
                                                        .getIsAnalogSignalLocked() == false) {
                                                    MainActivity.activity
                                                            .getCheckServiceType()
                                                            .showNoSignalLayout();
                                                }
                                            }
                                        });
                                    }
                                }).start();
                            } else {
                                toast.showToast(R.string.previous_input_is_disabled);
                            }
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activity.getMainKeyListener().changeChannelTogglePrevious();
                    return true;
                }
                // /////////////////////////////////////////////////////
                // INFO BANNER
                // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_SPACE:
                case KeyEvent.KEYCODE_INFO: {
                    // if (MainActivity.activity.getPageCurl().getCurrentState()
                    // == STATE_NUMEROUS_CHANGE_CHANNEL) {
                    // // ForceChangeChannel
                    // activity.getPageCurl().startCurlEffect(
                    // SCENARIO_DO_NOTHING);
                    // } else
                    if (MainActivity.activity.getPageCurl().getCurrentState() == STATE_CHANNEL_INFO) {
                        activity.getPageCurl().info();
                    } else {
                        activity.getPageCurl().info();
                    }
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME UP
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F8:
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_UP, true);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME DOWN
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F7:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_DOWN, true);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME MUTE
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, true);
                    return true;
                }
                // //////////////////////////////////////////////////////////////////////
                // NUMBER TASTERS FOR CHANNEL CHANGE
                // //////////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                case KeyEvent.KEYCODE_F10: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.changeChannelByNum(activity
                            .getMainKeyListener()
                            .generateChannelNumber(keyCode), mDisplayId);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////////
                // MAIN MENU
                // ////////////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_TAB:
                case KeyEvent.KEYCODE_MENU: {
                    if (MainKeyListener.enableKeyCodeMenu) {
                        // // Show main menu
                        // If main menu isn't created create it
                        if (activity.getMainMenuHandler() == null) {
                            activity.initMainMenu();
                        }
                        if (activity.getMainMenuHandler().getMainMenuDialog()
                                .isShowing()) {
                            activity.getMainMenuHandler()
                                    .getA4TVOnSelectLister()
                                    .clearAnimationsManual();
                            activity.getMainMenuHandler().closeMainMenu(true);
                        } else {
                            // Show main menu
                            activity.getMainMenuHandler().showMainMenu();
                        }
                    }
                    return true;
                }
                // ////////////////////////////////////////////////////////////////////
                // EXIT
                // ////////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_DEL:
                case KeyEvent.KEYCODE_BACK: {
                    if (MainKeyListener.enableKeyCodeBack) {
                        // //////////////////////////////////
                        // Info banner bug fix
                        // ////////////////////////////////////
                        if (MainActivity.activity.getPageCurl()
                                .getCurrentState() == STATE_CHANNEL_INFO) {
                            activity.getPageCurl().info();
                            // return true;
                        }
                    }
                    if ((MainKeyListener.enableKeyCodeBack || MainActivity.service == null)
                            && (MainActivity.activity.getPageCurl()
                                    .getCurrentState() == STATE_INIT || MainActivity.activity
                                    .getPageCurl().getCurrentState() == STATE_OFF)) {
                        final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                activity);
                        askDialog.setTitleOfAlertDialog(R.string.exit_massage)
                                .setCancelable(false);
                        askDialog.setPositiveButton(R.string.button_text_yes,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Thread killerThread = new Thread(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        activity.finish();
                                                    }
                                                });
                                        killerThread.start();
                                        askDialog.cancel();
                                    }
                                });
                        askDialog.setNegativeButton(R.string.button_text_no,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        askDialog.cancel();
                                    }
                                });
                        askDialog.show();
                    }
                    return true;
                }
                // ////////////////////////////////////////////////////////////////////
                // SUBTITLES
                // ////////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_CAPTIONS:
                case KeyEvent.KEYCODE_S:
                case KeyEvent.KEYCODE_F1: {
                    showSubtitle();
                    return true;
                }
                // ////////////////////////////////////////////////////////////////////
                // TELETEXT
                // ////////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F5:
                case KeyEvent.KEYCODE_F2: {
                    showTeletext();
                    return true;
                }
                // /////////////////////////////////////
                // Dialog for Available audio languages
                // /////////////////////////////////////
                case KeyEvent.KEYCODE_F6: {
                    showAudio();
                    return true;
                }
                // /////////////////////////////////////////////////
                // Quick menu feature
                // /////////////////////////////////////////////////
                // /////////////////////////////////////////////////
                // Source list shortcut
                // /////////////////////////////////////////////////
                case KeyEvent.KEYCODE_I:
                case KeyEvent.KEYCODE_TV_INPUT: {
                    // Check if content list is initialized
                    if (activity.getContentListHandler() == null) {
                        activity.initContentList();
                    }
                    // Show content list dialog
                    activity.getContentListHandler().showContentList();
                    // Filter current filter
                    activity.getContentListHandler().filterContent(
                            FilterType.INPUTS, true);
                    // Set flag to false
                    MainKeyListener.contentListFromMainMenu = false;
                    break;
                }
                // /////////////////////////////////////////////////
                // Browser shortcut
                // /////////////////////////////////////////////////
                case KeyEvent.KEYCODE_SETTINGS: {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.google.rs"));
                    activity.startActivity(browserIntent);
                    break;
                }
                case KeyEvent.KEYCODE_WINDOW: {
                    if (1 < (MainActivity.getKeySet())) {
                        Log.d(TAG,
                                "Aspect ratio cannot be changed when HbbTV application registers key mask greater than 1!");
                        return true;
                    }
                    PictureFormatHandler pictureFormatHandler = new PictureFormatHandler();
                    IOSDHandler curlHandler = activity.getPageCurl();
                    int curlHandlerState = curlHandler.getCurrentState();
                    int currentInputGroup = PictureSettingsDialog
                            .getCurrentInputTypeGroup(0 /* display id */);
                    if (currentInputGroup != -1) {
                        if (curlHandlerState == OSDGlobal.STATE_PICTURE_FORMAT) { // move
                            // to
                            // next
                            // format
                            if (pictureFormatHandler
                                    .moveToNextFormat(currentInputGroup) == -1) {
                                Log.e(TAG, "Aspect ratio failed to switch.");
                            }
                            curlHandler.showPictureFormat(pictureFormatHandler
                                    .currentFormatToString());
                        } else { // just show current format
                            curlHandler.showPictureFormat(pictureFormatHandler
                                    .currentFormatToString());
                        }
                    } else {
                        Log.w(TAG, "No picture format for active input.");
                    }
                    break;
                }
                case KeyEvent.KEYCODE_ESCAPE: {
                    if (0 != (MainActivity.getKeySet())) {
                        int command = 0;
                        String param = "EXIT";
                        try {
                            if (MainActivity.service.getHbbTvControl()
                                    .notifyAppMngr(command, param)) {
                                MainActivity.webDialog.getHbbTVView().setAlpha(
                                        (float) 0.00);
                                MainActivity.setKeySet(0);
                                return true;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
                default: {
                    Log.d(TAG, "default returned true");
                    return true;
                }
            }// switch
            return false;
        }
        return false;
    }

    /**
     * Show Teletext Dialog.
     */
    public void showTeletext() {
        Log.d(TAG, "KEYCODE_F2 - TELETEXT");
        int command = 1;
        String param = "txt_key";
        int command1 = 2;
        String param1 = "txt_key1";
        try {
            if (false == (MainActivity.service.getHbbTvControl().notifyAppMngr(
                    command, param))) {
                if (tempTxt) {
                    param1 = "1";
                    if (1 < (MainActivity.getKeySet())) {
                        return;
                    }
                    tempTxt = false;
                    if (false == (MainActivity.service.getHbbTvControl()
                            .notifyAppMngr(command1, param1))) {
                        return;
                    }
                } else {
                    param1 = "0";
                    MainActivity.service.getHbbTvControl().notifyAppMngr(
                            command1, param1);
                    tempTxt = true;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            IContentListControl contentListControl = MainActivity.service
                    .getContentListControl();
            if ((null != contentListControl
                    .getActiveContent(((MainActivity) activity).SECONDARY_DISPLAY_UNIT_ID))
                    && (((MainActivity) activity).PAP_MODE == ((MainActivity) activity)
                            .getSecondaryVideoViewState())) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainActivity.activity.getTeletextDialogView()
                .show(TeletextMode.FULL, 0);
    }

    /**
     * Show Subtitle Dialog.
     */
    public void showSubtitle() {
        try {
            IContentListControl contentListControl = MainActivity.service
                    .getContentListControl();
            if ((null != contentListControl
                    .getActiveContent(((MainActivity) activity).SECONDARY_DISPLAY_UNIT_ID))
                    && (((MainActivity) activity).PAP_MODE == ((MainActivity) activity)
                            .getSecondaryVideoViewState())) {
                return;
            }
            SubtitleLanguageDialog subLanguageDialog = activity
                    .getDialogManager().getSubtitleLanguageDialog();
            if (subLanguageDialog != null)
                if (subLanguageDialog.isShowing()) {
                    subLanguageDialog.cancel();
                } else {
                    subLanguageDialog.show();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show Audio Dialog.
     */
    public void showAudio() {
        Content content = null;
        AudioChannelMode mode = AudioChannelMode.SINGLE_MONO;
        A4TVToast toast = new A4TVToast(activity);
        try {
            content = MainActivity.service.getContentListControl()
                    .getActiveContent(mDisplayId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (content != null) {
            if (content.getSourceType() == SourceType.ANALOG) {
                try {
                    MainActivity.service.getSystemControl().getSoundControl()
                            .setAudioChannelMode(mode);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    mode = MainActivity.service.getSystemControl()
                            .getSoundControl().getAudioChannelMode();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                switch (mode) {
                    case STEREO:
                        toast.showToast(com.iwedia.gui.R.string.tv_menu_sound_mode_stereo);
                        break;
                    case SINGLE_MONO:
                        toast.showToast(com.iwedia.gui.R.string.tv_menu_sound_mode_single_mono);
                        break;
                    case DUAL_MONO:
                        toast.showToast(com.iwedia.gui.R.string.tv_menu_sound_dual_mono);
                        break;
                    case SAP:
                        toast.showToast(com.iwedia.gui.R.string.tv_menu_sound_sap);
                        break;
                    default:
                        break;
                }
            } else {
                AudioLanguageDialog audioLangDialog = activity
                        .getDialogManager().getAudioLanguageDialog();
                if (audioLangDialog != null)
                    if (audioLangDialog.isShowing()) {
                        audioLangDialog.cancel();
                    } else {
                        audioLangDialog.show();
                    }
            }
        }
    }

    /**
     * HbbTV key handling function
     * 
     * @param keyCode
     * @return
     */
    public boolean hbbKeyHandler(int keyCode) {
        final int RED_KEY = 1;
        final int GREEN_KEY = 2;
        final int YELLOW_KEY = 4;
        final int BLUE_KEY = 8;
        final int NAVIGATION = 16;
        final int VCR = 32;
        final int NUMERIC = 256;
        Log.d(TAG, "HbbTV keyset mask value = " + MainActivity.getKeySet());
        switch (keyCode) {
            case KeyEvent.KEYCODE_PROG_RED: {
                if (0 != (RED_KEY & MainActivity.getKeySet())) {
                    return true;
                }
            }
                break;
            case KeyEvent.KEYCODE_PROG_GREEN: {
                if (0 != (GREEN_KEY & MainActivity.getKeySet())) {
                    return true;
                }
            }
                break;
            case KeyEvent.KEYCODE_PROG_YELLOW: {
                if (0 != (YELLOW_KEY & MainActivity.getKeySet())) {
                    return true;
                }
            }
                break;
            case KeyEvent.KEYCODE_PROG_BLUE: {
                if (0 != (BLUE_KEY & MainActivity.getKeySet())) {
                    return true;
                }
            }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                // case KeyEvent.KEYCODE_BACK: { // TODO: TEMPORARY COMMENTED!!
                if (0 != (NAVIGATION & MainActivity.getKeySet())) {
                    return true;
                }
            }
                break;
            // TODO: TEMPORARY HERE!! WILL BE MOVED!!
            case KeyEvent.KEYCODE_BACK: {
                if (0 != (MainActivity.getKeySet())) {
                    int command = 0;
                    String param = "EXIT";
                    try {
                        if (MainActivity.service.getHbbTvControl()
                                .notifyAppMngr(command, param)) {
                            return true;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                if (0 != (VCR & MainActivity.getKeySet())) {
                    return true;
                }
            }
                break;
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9: {
                if (0 != (NUMERIC & MainActivity.getKeySet())) {
                    return true;
                }
            }
                break;
            /* Following RC keys are disabled during HbbTV HTTP playback */
            // Aspect ratio
            case KeyEvent.KEYCODE_WINDOW:
                // Source list
            case KeyEvent.KEYCODE_TV_INPUT:
            case KeyEvent.KEYCODE_I:
                // Audio tracks switching
            case KeyEvent.KEYCODE_F6:
                // Txt
            case KeyEvent.KEYCODE_F5:
            case KeyEvent.KEYCODE_F2:
                // Subs
            case KeyEvent.KEYCODE_CAPTIONS:
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_F1:
                // Info menu
            case KeyEvent.KEYCODE_SPACE:
            case KeyEvent.KEYCODE_INFO:
                // Channel zapping
            case KeyEvent.KEYCODE_F9:
            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_F4:
            case KeyEvent.KEYCODE_F3:
                // Epg
            case KeyEvent.KEYCODE_E:
            case KeyEvent.KEYCODE_GUIDE:
            case KeyEvent.KEYCODE_SEARCH:
                // Pvr
            case KeyEvent.KEYCODE_F12:
            case KeyEvent.KEYCODE_MEDIA_RECORD: {
                if (activity.isHbbTVInHTTPPlaybackMode()) {
                    return true;
                }
            }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * Mheg key handling function
     * 
     * @param keyCode
     * @return
     */
    public boolean mhegKeyHandler(int keyCode) {
        final int MHEG_MASK_UP = 0x00000001;
        final int MHEG_MASK_DOWN = 0x00000002;
        final int MHEG_MASK_LEFT = 0x00000004;
        final int MHEG_MASK_RIGHT = 0x00000008;
        final int MHEG_MASK_DIGIT = 0x00000010;
        final int MHEG_MASK_SELECT = 0x00000020;
        final int MHEG_MASK_CALCEL = 0x00000040;
        final int MHEG_MASK_RED = 0x00000100;
        final int MHEG_MASK_GREEN = 0x00000200;
        final int MHEG_MASK_YELLOW = 0x00000400;
        final int MHEG_MASK_BLUE = 0x00000800;
        final int MHEG_MASK_TEXT = 0x00001000;
        final int MHEG_MASK_INFO = 0x00002000;
        final int MHEG_MASK_STOP = 0x00004000;
        final int MHEG_MASK_PLAY = 0x00008000;
        final int MHEG_MASK_PAUSE = 0x00010000;
        final int MHEG_MASK_SKIP_FORWARD = 0x00020000;
        final int MHEG_MASK_SKIP_BACK = 0x00040000;
        final int MHEG_MASK_FAST_FORWARD = 0x00080000;
        final int MHEG_MASK_REWIND = 0x00100000;
        final int MHEG_MASK_PLAY_PAUSE = 0x00400000;
        final int MHEG_MASK_GUIDE = 0x00200000;
        final int MHEG_MASK_EXIT = 0x00800000;
        Log.d(TAG, "Mheg keyset mask value : " + MainActivity.getMhegKeySet()
                + "KeyEvent : " + keyCode + "\n");
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (0 != (MHEG_MASK_UP & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (0 != (MHEG_MASK_DOWN & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (0 != (MHEG_MASK_LEFT & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (0 != (MHEG_MASK_RIGHT & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9: {
                if (0 != (MHEG_MASK_DIGIT & MainActivity.getMhegKeySet())) {
                    return true;
                }
            }
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (0 != (MHEG_MASK_SELECT & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DEL:
            case KeyEvent.KEYCODE_CLEAR:
            case KeyEvent.KEYCODE_F10:
                if (0 != (MHEG_MASK_CALCEL & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_PROG_RED:
                if (0 != (MHEG_MASK_RED & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_PROG_GREEN:
                if (0 != (MHEG_MASK_GREEN & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_PROG_YELLOW:
                Log.d(TAG, "Mhegkeyset " + MainActivity.getMhegKeySet()
                        + "KeyEvent : " + keyCode + " enter\n");
                if (0 != (MHEG_MASK_YELLOW & MainActivity.getMhegKeySet())) {
                    Log.d(TAG, "Mhegkeyset " + MainActivity.getMhegKeySet()
                            + "KeyEvent : " + keyCode + " return true\n");
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_PROG_BLUE:
                if (0 != (MHEG_MASK_BLUE & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_F2:
            case KeyEvent.KEYCODE_F5:
                if (0 != (MHEG_MASK_TEXT & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_INFO:
                if (0 != (MHEG_MASK_INFO & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                if (0 != (MHEG_MASK_STOP & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (0 != (MHEG_MASK_PLAY & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (0 != (MHEG_MASK_PAUSE & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (0 != (MHEG_MASK_SKIP_FORWARD & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                if (0 != (MHEG_MASK_SKIP_BACK & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                if (0 != (MHEG_MASK_FAST_FORWARD & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                if (0 != (MHEG_MASK_REWIND & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (0 != (MHEG_MASK_PLAY_PAUSE & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_GUIDE:
            case KeyEvent.KEYCODE_SEARCH:
                if (0 != (MHEG_MASK_GUIDE & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_ESCAPE:
            case KeyEvent.KEYCODE_BACK:
                if (0 != (MHEG_MASK_EXIT & MainActivity.getMhegKeySet())) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    private class PictureFormatHandler {
        public PictureFormatHandler() {
        };

        int moveToNextFormat(int currentInputGroup) {
            AspectRatioMode currentFormat = AspectRatioMode.AUTO;
            AspectRatioMode nextFormat = AspectRatioMode.AUTO;
            /*
             * PRS - Auto - Normal 4:3 - Zoom 14:9 - Panorama - Letterbox - Full
             * - Cinema 16:9 - Cinema 14:9
             */
            /*
             * enum OUTPUT_ASPECT_RATIO_16_9_AUTO = 7,
             * OUTPUT_ASPECT_RATIO_16_9_FULL = 8,
             * OUTPUT_ASPECT_RATIO_16_9_CINEMA_16_9 = 9,
             * OUTPUT_ASPECT_RATIO_16_9_CINEMA_14_9 = 10,
             * OUTPUT_ASPECT_RATIO_16_9_NOR4_3 = 11,
             * OUTPUT_ASPECT_RATIO_16_9_ZOOM_14_9 = 12,
             * OUTPUT_ASPECT_RATIO_16_9_PANORAMA = 13,
             * OUTPUT_ASPECT_RATIO_16_9_UNDERSCAN = 14,
             */
            try {
                currentFormat = activity.service.getSystemControl()
                        .getPictureControl().getAspectRatioMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (currentFormat) {
            /*
             * If 0 - (OK)digital tuner, (OK)HDMI or (OK)Component source is
             * active the following picture formats shall be supported: - Auto -
             * Normal 4:3 - Zoom 14:9 - Panorama - Letterbox - Full - Cinema
             * 16:9 - Cinema 14:9 If 1 - (OK)analog tuner, SCART or
             * (OK)Composite source is active the following picture formats
             * shall be supported: - Auto - Normal 4:3 - Zoom 14:9 - Panorama -
             * Letterbox - Cinema 16:9 - Cinema 14:9 If 2 - (OK)VGA (PC) source
             * is active, device shall support following picture formats: -
             * Normal 4:3 - Cinema 16:9
             */
                case AUTO:
                    nextFormat = AspectRatioMode.NORMAL_4_3;
                    break;
                case NORMAL_4_3:
                    if (currentInputGroup == PictureSettingsDialog.VGAInputTypeGroup) {
                        nextFormat = AspectRatioMode.CINEMA_16_9;
                    } else {
                        nextFormat = AspectRatioMode.ZOOM_14_9;
                    }
                    break;
                case ZOOM_14_9:
                    nextFormat = AspectRatioMode.PANORAMA;
                    break;
                case PANORAMA:
                    nextFormat = AspectRatioMode.LETTERBOX; // Letterbox
                    break;
                case LETTERBOX: // Letterbox
                    if (currentInputGroup == PictureSettingsDialog.AnalogInputTypeGroup) {
                        nextFormat = AspectRatioMode.CINEMA_16_9;
                    } else {
                        nextFormat = AspectRatioMode.FULL;
                    }
                    break;
                case FULL:
                    nextFormat = AspectRatioMode.CINEMA_16_9;
                    break;
                case CINEMA_16_9:
                    if (currentInputGroup == PictureSettingsDialog.VGAInputTypeGroup) {
                        nextFormat = AspectRatioMode.NORMAL_4_3;
                    } else {
                        nextFormat = AspectRatioMode.CINEMA_14_9;
                    }
                    break;
                case CINEMA_14_9:
                    nextFormat = AspectRatioMode.AUTO;
                    break;
            }
            Log.d(TAG, "moveToNextFormat: nextFormat[" + nextFormat + "]");
            try {
                activity.service.getSystemControl().getPictureControl()
                        .setAspectRatioMode(nextFormat);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return nextFormat.getValue();
        }

        String currentFormatToString() {
            AspectRatioMode format = AspectRatioMode.AUTO;
            try {
                format = activity.service.getSystemControl()
                        .getPictureControl().getAspectRatioMode();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "currentFormatToString: format[" + format + "]");
            switch (format) {
                case AUTO:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_auto);
                case NORMAL_4_3:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_normal_4_3);
                case ZOOM_14_9:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_zoom_14_9);
                case PANORAMA:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_panorama);
                case LETTERBOX:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_letterbox);
                case FULL:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_full);
                case CINEMA_16_9:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_cinema_16_9);
                case CINEMA_14_9:
                    return activity
                            .getResources()
                            .getString(
                                    R.string.tv_menu_picture_settings_aspect_ratio_cinema_14_9);
                default:
                    return "Unknown";
            }
        }
    }
}
