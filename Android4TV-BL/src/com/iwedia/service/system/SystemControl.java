package com.iwedia.service.system;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IActionCallback;
import com.iwedia.comm.ISystemControl;
import com.iwedia.comm.system.ICurlSettings;
import com.iwedia.comm.system.IInputSettings;
import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.comm.system.IPictureSettings;
import com.iwedia.comm.system.ISoundSettings;
import com.iwedia.comm.system.IVoiceInputOutputSettings;
import com.iwedia.comm.system.about.IAbout;
import com.iwedia.comm.system.account.IAccountSyncSettings;
import com.iwedia.comm.system.application.AppSizeInfo;
import com.iwedia.comm.system.application.IApplicationRestart;
import com.iwedia.comm.system.application.IApplicationSettings;
import com.iwedia.comm.system.date_time.IDateTimeSettings;
import com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings;
import com.iwedia.comm.system.language_and_keyboard.ILanguageKeyboardSettings;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.system.about.About;
import com.iwedia.service.system.account.AccountAndSyncSettings;
import com.iwedia.service.system.application.ApplicationRestart;
import com.iwedia.service.system.application.ApplicationSettings;
import com.iwedia.service.system.date_time.DateAndTimeSettings;
import com.iwedia.service.system.external_and_local_storage.ExternalAndLocalStorageSettings;
import com.iwedia.service.system.language_and_keyboard.LanguageAndKeyboardSettings;
import com.iwedia.service.system.sound.SoundSettings;

/**
 * The system settings controller. Provides Android and TV related settings
 * 
 * @author Stanislava Markovic
 */
public class SystemControl extends ISystemControl.Stub implements IDTVInterface {
    private static final String LOG_TAG = "SystemControl";
    private INetworkSettings networkSettings;
    private IPictureSettings pictureSettings;
    private ISoundSettings soundSettings;
    private IDateTimeSettings dateAndTimeSettings;
    private ILanguageKeyboardSettings languageAndKeyboardSettings;
    private IInputSettings inputSettings;
    private IApplicationSettings applicationSettings;
    private IExternalLocalStorageSettings externalAndLocalStorageSettings;
    private IAccountSyncSettings accountAndSyncSettings;
    private IVoiceInputOutputSettings voiceInputAndOutputSettings;
    private IAbout about;
    private ICurlSettings curlSettings;
    private IApplicationRestart applicationRestart;
    private static IActionCallback actionCallback;

    public static IActionCallback getActionCallback() {
        return actionCallback;
    }

    public SystemControl() {
        networkSettings = new NetworkSettings();
        pictureSettings = new PictureSettings();
        soundSettings = new SoundSettings();
        dateAndTimeSettings = new DateAndTimeSettings();
        languageAndKeyboardSettings = new LanguageAndKeyboardSettings();
        inputSettings = new InputSettings();
        applicationSettings = new ApplicationSettings();
        applicationRestart = new ApplicationRestart();
        externalAndLocalStorageSettings = new ExternalAndLocalStorageSettings();
        accountAndSyncSettings = new AccountAndSyncSettings();
        voiceInputAndOutputSettings = new VoiceInputAndOutputSettings();
        about = new About();
    }

    /**
     * Returns basic Android network settings.
     * 
     * @return {@link com.iwedia.comm.system.INetworkSettings}
     */
    @Override
    public INetworkSettings getNetworkControl() throws RemoteException {
        return networkSettings;
    }

    /**
     * Returns picture settings.
     * 
     * @return {@link com.iwedia.comm.system.IPictureSettings}
     */
    @Override
    public IPictureSettings getPictureControl() throws RemoteException {
        return pictureSettings;
    }

    /**
     * Returns sound settings;
     * 
     * @return {@link com.iwedia.comm.system.ISoundSettings}
     */
    @Override
    public ISoundSettings getSoundControl() throws RemoteException {
        return soundSettings;
    }

    /**
     * Returns date and time settings.
     * 
     * @return {@link com.iwedia.comm.system.date_time.IDateTimeSettings}
     */
    @Override
    public IDateTimeSettings getDateAndTimeControl() throws RemoteException {
        return dateAndTimeSettings;
    }

    /**
     * Returns language and keyboard settings.
     * 
     * @return {@link com.iwedia.comm.system.language_and_keyboard.ILanguageKeyboardSettings}
     */
    @Override
    public ILanguageKeyboardSettings getLanguageAndKeyboardControl()
            throws RemoteException {
        return languageAndKeyboardSettings;
    }

    /**
     * Returns input settings.
     * 
     * @return {@link com.iwedia.comm.system.IInputSettings}
     */
    @Override
    public IInputSettings getInputControl() throws RemoteException {
        return inputSettings;
    }

    /**
     * Returns application settings;
     * 
     * @return {@link com.iwedia.comm.system.application.IApplicationSettings}
     */
    @Override
    public IApplicationSettings getApplicationControl() throws RemoteException {
        return applicationSettings;
    }

    /**
     * Returns application settings;
     * 
     * @return {@link com.iwedia.comm.system.application.IApplicationSettings}
     */
    @Override
    public IApplicationRestart getApplicationRestart() throws RemoteException {
        return applicationRestart;
    }

    /**
     * Returns external and local storage settings;
     * 
     * @return {@link com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings}
     */
    @Override
    public IExternalLocalStorageSettings getExternalLocalStorageControl()
            throws RemoteException {
        return externalAndLocalStorageSettings;
    }

    /**
     * Returns accounts and sync settings;
     * 
     * @return {@link com.iwedia.comm.system.IAccountSyncSettings}
     */
    @Override
    public IAccountSyncSettings getAccountSyncControl() throws RemoteException {
        return accountAndSyncSettings;
    }

    /**
     * Returns voice input and output settings;
     * 
     * @return {@link com.iwedia.comm.system.IVoiceInputOutputSettings}
     */
    @Override
    public IVoiceInputOutputSettings getVoiceInputOutputControl()
            throws RemoteException {
        return voiceInputAndOutputSettings;
    }

    /**
     * Returns device info;
     * 
     * @return {@link com.iwedia.comm.system.about.IAbout}
     */
    @Override
    public IAbout getAbout() throws RemoteException {
        return about;
    }

    /**
     * Register action callback;
     * 
     * @param {@link com.iwedia.comm.IActionCallbacks}
     */
    @Override
    public void registerActionCallback(IActionCallback actionCallback)
            throws RemoteException {
        SystemControl.actionCallback = actionCallback;
    }

    public static void broadcastMhegStarted(boolean state) {
        try {
            if (actionCallback != null) {
                actionCallback.mhegStarted(state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the teletext needs to be shown.
     */
    public static void broadcastTeletextShow() {
        try {
            if (actionCallback != null) {
                actionCallback.showTeletext();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the teletext needs to be closed.
     */
    public static void broadcastHideTeletext() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast hide teletext");
        }
        try {
            if (actionCallback != null) {
                actionCallback.hideTeletext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the teletext needs to be refreshed.
     */
    public static void broadcastInvalidateTeletext() {
        try {
            if (actionCallback != null) {
                actionCallback.invalidateTeletext();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the subtitle needs to be shown.
     */
    public static void broadcastSubtitleShow() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcastSubtitleShow");
        }
        try {
            if (actionCallback != null) {
                actionCallback.showSubtitle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the subtitle needs to be closed.
     */
    public static void broadcastSubtitleHide() {
        try {
            if (actionCallback != null) {
                actionCallback.hideSubtitle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the subtitle needs to be refreshed.
     * 
     * @param value
     *        - width of subtitle region.
     */
    public static void broadcastSubtitleInvalidate(int value) {
        try {
            if (actionCallback != null) {
                actionCallback.invalidateSubtitle(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the mheg needs to be refreshed.
     */
    public static void broadcastMhegInvalidate() {
        try {
            if (actionCallback != null) {
                actionCallback.invalidateMheg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the mheg needs to be closed.
     */
    public static void broadcastMhegHide() {
        try {
            if (actionCallback != null) {
                actionCallback.hideMheg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the mheg needs to be shown.
     */
    public static void broadcastMhegShow() {
        try {
            if (actionCallback != null) {
                actionCallback.showMheg();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns information about the requested application.
     * 
     * @return {@link com.iwedia.comm.system.application.AppSizeInfo}
     */
    public static void broadcastAppSizeInfo(AppSizeInfo appSizeInfo) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast getAppSizeInfo");
        }
        try {
            if (actionCallback != null) {
                actionCallback.getAppSizeInfo(appSizeInfo);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that the uninstallation operation of 3rd party
     * application has been finished.
     */
    public static void broadcastUninstallFinished() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast uninstallFinished");
        }
        try {
            if (actionCallback != null) {
                actionCallback.uninstallFinished();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Informs the user that data or cache is cleared.
     */
    public static void broadcastClearDataCacheFinished(boolean isSucceeded) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast clearDataCacheFinished");
        }
        try {
            if (actionCallback != null) {
                actionCallback.clearDataCacheFinished(isSucceeded);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * FW Update available event.
     */
    public static void broadcastUpdateEvent(String version) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast updateEvent");
        }
        try {
            if (actionCallback != null) {
                actionCallback.updateEvent(version);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Error event (some action failed). It is accompanied y the error message.
     */
    public static void broadcastErrorEvent(String err) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast errorEvent");
        }
        try {
            if (actionCallback != null) {
                actionCallback.errorEvent(err);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * FW Update not available event.
     */
    public static void broadcastNoUpdateEvent(String msg) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast noUpdateEvent");
        }
        try {
            if (actionCallback != null) {
                actionCallback.noUpdateEvent(msg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * FW USB Update available event.
     */
    public static void broadcastUsbUpdateEvent(String msg) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast usbUpdateEvent");
        }
        try {
            if (actionCallback != null) {
                actionCallback.usbUpdateEvent(msg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * FW Update available event.
     */
    public static void broadcastUsbCheckUpdateEvent(int msgType, String version) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast usbCheckUpdateEvent");
        }
        try {
            if (actionCallback != null) {
                actionCallback.usbCheckUpdateEvent(msgType, version);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastSyncStartedEvent() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast broadcastSyncStartedEvent");
        }
        try {
            actionCallback.syncStarted();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastSyncFinishedEvent() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast broadcastFinishedEvent");
        }
        try {
            if (actionCallback != null) {
                actionCallback.syncFinished();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMediaMounted(String path) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast broadcast Media Mounted");
        }
        try {
            actionCallback.mediaMounted(path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMediaEjected(String path) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast broadcast Media Ejected");
        }
        try {
            if (actionCallback != null) {
                actionCallback.mediaEjected(path);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastMediaNotSupported(String path) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "broadcast broadcast Media Not supported");
        }
        try {
            if (actionCallback != null) {
                actionCallback.mediaNotSupported(path);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ICurlSettings getCurlSettings() throws RemoteException {
        if (curlSettings == null) {
            curlSettings = new CurlSettings();
        }
        return curlSettings;
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}
