package com.iwedia.gui.mainmenu;

import android.content.Context;
import android.util.Log;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.dialogs.AccountsAndSyncAddAccountDialog;
import com.iwedia.gui.components.dialogs.AccountsAndSyncDialog;
import com.iwedia.gui.components.dialogs.AccountsAndSyncManageAccountsDialog;
import com.iwedia.gui.components.dialogs.ApplicationsAppControlDialog;
import com.iwedia.gui.components.dialogs.ApplicationsManageDialog;
import com.iwedia.gui.components.dialogs.ApplicationsManageManageAppsDialog;
import com.iwedia.gui.components.dialogs.ApplicationsManageRunningServicesDialog;
import com.iwedia.gui.components.dialogs.AudioLanguageDialog;
import com.iwedia.gui.components.dialogs.CICamInfoDialog;
import com.iwedia.gui.components.dialogs.CIInfoDialog;
import com.iwedia.gui.components.dialogs.CISettingsDialog;
import com.iwedia.gui.components.dialogs.CableNetworkDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationManualTunningDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationSignalInformationDialog;
import com.iwedia.gui.components.dialogs.ChannelScanDialog;
import com.iwedia.gui.components.dialogs.ContentDialog;
import com.iwedia.gui.components.dialogs.ContextSmallDialog;
import com.iwedia.gui.components.dialogs.DLNASettingsDialog;
import com.iwedia.gui.components.dialogs.DebuggingDataDialog;
import com.iwedia.gui.components.dialogs.EPGDialog;
import com.iwedia.gui.components.dialogs.EPGScheduleDialog;
import com.iwedia.gui.components.dialogs.EnergySaveDialog;
import com.iwedia.gui.components.dialogs.EpgReminderDialog;
import com.iwedia.gui.components.dialogs.EpgScheduleRecordingDialog;
import com.iwedia.gui.components.dialogs.ExternalAndLocalStorageDialog;
import com.iwedia.gui.components.dialogs.FactoryResetDialog;
import com.iwedia.gui.components.dialogs.FavoriteListDialog;
import com.iwedia.gui.components.dialogs.HBBSettingsDialog;
import com.iwedia.gui.components.dialogs.InputDevicesSettingsDialog;
import com.iwedia.gui.components.dialogs.LanguageAndKeyboardDialog;
import com.iwedia.gui.components.dialogs.MainMenuDialog;
import com.iwedia.gui.components.dialogs.MultimediaDialog;
import com.iwedia.gui.components.dialogs.MultimediaShowDialog;
import com.iwedia.gui.components.dialogs.NetworkAdvancedManualConfigDialog;
import com.iwedia.gui.components.dialogs.NetworkAdvancedProxyDialog;
import com.iwedia.gui.components.dialogs.NetworkAdvancedSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkAdvancedSoftAPDialog;
import com.iwedia.gui.components.dialogs.NetworkIdDialog;
import com.iwedia.gui.components.dialogs.NetworkSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkTestDialog;
import com.iwedia.gui.components.dialogs.NetworkWiredInformationDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessAddHiddenNetworkDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessFindAPDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessFindWPSDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessInformationDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessWPSConfigDialog;
import com.iwedia.gui.components.dialogs.OSDSelectionDialog;
import com.iwedia.gui.components.dialogs.OffTimersAddDialog;
import com.iwedia.gui.components.dialogs.OffTimersSettingsDialog;
import com.iwedia.gui.components.dialogs.PVRManualEventReminderDialog;
import com.iwedia.gui.components.dialogs.PVRManualScheduleDialog;
import com.iwedia.gui.components.dialogs.PVRMenuDialog;
import com.iwedia.gui.components.dialogs.PVRSettingsDialog;
import com.iwedia.gui.components.dialogs.ParentalControlDialog;
import com.iwedia.gui.components.dialogs.ParentalGuidanceDialog;
import com.iwedia.gui.components.dialogs.PasswordSecurityDialog;
import com.iwedia.gui.components.dialogs.PiPSettingsDialog;
import com.iwedia.gui.components.dialogs.PictureSettingsDialog;
import com.iwedia.gui.components.dialogs.ProductInfoDialog;
import com.iwedia.gui.components.dialogs.ProductInfoSoftwareStatusDialog;
import com.iwedia.gui.components.dialogs.ScreensaverSettingsDialog;
import com.iwedia.gui.components.dialogs.ServiceModeDialog;
import com.iwedia.gui.components.dialogs.ServiceSoundDialog;
import com.iwedia.gui.components.dialogs.SoftwareUpgradeDialog;
import com.iwedia.gui.components.dialogs.SoundPostProcessingDialog;
import com.iwedia.gui.components.dialogs.SoundSettingsDialog;
import com.iwedia.gui.components.dialogs.SourceMenuDialog;
import com.iwedia.gui.components.dialogs.StoreModeSettingsDialog;
import com.iwedia.gui.components.dialogs.SubtitleLanguageDialog;
import com.iwedia.gui.components.dialogs.SubtitleSettingsDialog;
import com.iwedia.gui.components.dialogs.SystemSettingsDialog;
import com.iwedia.gui.components.dialogs.TeletextSettingsDialog;
import com.iwedia.gui.components.dialogs.TimeAndDateSettingsDialog;
import com.iwedia.gui.components.dialogs.TimersSettingsDialog;
import com.iwedia.gui.components.dialogs.VoiceInputDialog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Branimir Pavlovic
 */
public class DialogManager {
    private final String TAG = "DialogManager";
    // main fields
    public static DialogCreatorClass dialogCreator;
    private MainActivity activity;
    /** All dialogs are here */
    private AccountsAndSyncDialog accountsAndSyncDialog;
    private AccountsAndSyncAddAccountDialog accountsAndSyncAddAccountDialog;
    private AccountsAndSyncManageAccountsDialog accountsAndSyncManageAccountsDialog;
    private ApplicationsAppControlDialog applicationsAppControlDialog;
    private ApplicationsManageDialog applicationsManageDialog;
    private ApplicationsManageManageAppsDialog applicationsManageManageAppsDialog;
    private ApplicationsManageRunningServicesDialog applicationsManageRunningServicesDialog;
    private AudioLanguageDialog audioLanguageDialog;
    private CableNetworkDialog cableNetworkDialog;
    private ChannelInstallationDialog channelInstallationDialog;
    private ChannelInstallationManualTunningDialog channelInstallationManualTunningDialog;
    private ChannelInstallationSignalInformationDialog channelInstallationSignalInfoDialog;
    private ChannelScanDialog channelScanDialog;
    private CIInfoDialog ciInfoDialog;
    private CICamInfoDialog ciCamInfoDialog;
    private CISettingsDialog ciSettingsDialog;
    private ContentDialog contentDialog;
    private ContextSmallDialog contextSmallDialog;
    private DebuggingDataDialog debuggingDataDialog;
    private DLNASettingsDialog dlnaSettingsDialog;
    private EnergySaveDialog energySaveDialog;
    private EPGDialog epgDialog;
    private EpgReminderDialog epgReminderDialog;
    private EpgScheduleRecordingDialog epgScheduleRecordingDialog;
    private EPGScheduleDialog epgScheduleDialog;
    private ExternalAndLocalStorageDialog externalAndLocalStorageDialog;
    private FactoryResetDialog factoryResetDialog;
    private FavoriteListDialog favoriteListDialog;
    private HBBSettingsDialog hbbSettingsDialog;
    private InputDevicesSettingsDialog inputDevicesSettingsDialog;
    private LanguageAndKeyboardDialog languageAndKeyboardDialog;
    private MainMenuDialog mainMenuDialog;
    private MultimediaDialog multimediaDialog;
    private MultimediaShowDialog multimediaShowDialog;
    private NetworkIdDialog networkIdDialog;
    private NetworkSettingsDialog networkSettingsDialog;
    private NetworkTestDialog networkTestDialog;
    private NetworkWirelessSettingsDialog networkWirelessSettingsDialog;
    private NetworkWirelessInformationDialog networkWirelessInformationDialog;
    private NetworkWiredInformationDialog networkWiredInformationDialog;
    private NetworkAdvancedSettingsDialog networkAdvancedSettingsDialog;
    private NetworkAdvancedManualConfigDialog networkAdvancedManualConfigDialog;
    private NetworkAdvancedProxyDialog networkAdvancedProxyDialog;
    private NetworkAdvancedSoftAPDialog networkAdvancedSoftAPDialog;
    private NetworkWirelessWPSConfigDialog networkWirelessWPSConfigDialog;
    private NetworkWirelessFindWPSDialog networkWirelessFindWPSDialog;
    private NetworkWirelessFindAPDialog networkWirelessFindAPDialog;
    private NetworkWirelessAddHiddenNetworkDialog networkWirelessAddHiddenNetworkDialog;
    private OSDSelectionDialog osdSelectionDialog;
    private OffTimersAddDialog offTimersAddDialog;
    private OffTimersSettingsDialog offTimersSettingsDialog;
    private ParentalControlDialog parentalControlDialog;
    private ParentalGuidanceDialog parentalGuidanceDialog;
    private ProductInfoSoftwareStatusDialog productInfoStatusDialog;
    private PasswordSecurityDialog passwordSecurityDialog;
    private PictureSettingsDialog pictureSettingsDialog;
    private PiPSettingsDialog pipSettingsDialog;
    private ProductInfoDialog productInfoDialog;
    private PVRManualEventReminderDialog pvrManualEventReminderDialog;
    private PVRManualScheduleDialog pvrManualScheduleDialog;
    private PVRMenuDialog pvrMenuDialog;
    private PVRSettingsDialog pvrSettingsDialog;
    private ServiceModeDialog serviceModeDialog;
    private ServiceSoundDialog serviceSoundDialog;
    private ScreensaverSettingsDialog screensaverSettingsDialog;
    private SoftwareUpgradeDialog softwareUpgradeDialog;
    private SoundPostProcessingDialog soundPostProcessingDialog;
    private SourceMenuDialog sourceMenuDialog;
    private SubtitleLanguageDialog subtitleLanguageDialog;
    private SubtitleSettingsDialog subtitleSettingsDialog;
    private SoundSettingsDialog soundSettingsDialog;
    private StoreModeSettingsDialog storeModeSettingsDialog;
    private SystemSettingsDialog systemSettingsDialog;
    private TeletextSettingsDialog teletextSettingsDialog;
    private TimeAndDateSettingsDialog timeAndDateSettingsDialog;
    private TimersSettingsDialog timersSettingsDialog;
    private VoiceInputDialog voiceInputDialog;
    /*******************************************************/
    private ArrayList<A4TVDialog> dialogs;

    // default constructor
    public DialogManager(MainActivity activity) {
        this.activity = activity;
        dialogCreator = new DialogCreatorClass(activity);
        dialogs = new ArrayList<A4TVDialog>();
    }

    public void init() {
        /** Initialize dialogs */
        getCICamInfoDialog();
        getCiInfoDialog();
        getFavoriteListDialog();
        getEpgReminderDialog();
        getEpgScheduleRecordingDialog();
        getEpgScheduleDialog();
        getSoftwareUpgradeDialog();
        getNetworkTestDialog();
        getNetworkWirelessSettingsDialog();
        getNetworkAdvancedSettingsDialog();
        getNetworkAdvancedManualConfigDialog();
        getNetworkAdvancedProxyDialog();
        getNetworkWiredInformationDialog();
        getNetworkWirelessInformationDialog();
        getNetworkWirelessWPSConfigDialog();
        getNetworkWirelessFindAPDialog();
        getNetworkWirelessFindWPSDialog();
        getNetworkWirelessAddHiddenNetworkDialog();
        getAccountsAndSyncManageAccountsDialog();
        getPasswordSecurityDialog();
        getChannelScanDialog();
        getApplicationsAppControlDialog();
        getScreensaverSettingsDialog();
        getStoreModeSettingsDialog();
        getCISettingsDialog();
        getOSDSelectionDialog();
        getPiPSettingsDialog();
    }

    /**
     * Function that creates all dialogs in application
     * 
     * @param dialogClassName
     *        Dialog class name
     * @return Instance of created dialog or null if it is been error
     */
    public A4TVDialog createDialog(String dialogClassName) {
        Class<?> c = null;
        try {
            c = Class.forName("com.iwedia.gui.components.dialogs."
                    + dialogClassName);
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            Log.d(TAG, "Class not found with that name");
        }
        Constructor<?> cons = null;
        try {
            if (c != null) {
                cons = c.getConstructor(Context.class);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            if (cons != null) {
                object = cons.newInstance(activity);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Throwable w = e.getTargetException();
            w.printStackTrace();
            e.printStackTrace();
        }
        return (A4TVDialog) object;
    }

    /**
     * Function that hides all visible dialogs
     */
    public void hideAllDialogs() {
        for (int i = 0; i < dialogs.size(); i++) {
            if (dialogs.get(i) != null) {
                Log.d(TAG, "HIDE DIALOG: " + dialogs.get(i).toString());
                dialogs.get(i).cancel();
            }
        }
    }

    public void removeDialogsOnPause() {
        accountsAndSyncDialog = null;
        applicationsManageDialog = null;
        channelInstallationDialog = null;
        channelInstallationManualTunningDialog = null;
        channelScanDialog = null;
        contentDialog = null;
        energySaveDialog = null;
        externalAndLocalStorageDialog = null;
        factoryResetDialog = null;
        hbbSettingsDialog = null;
        inputDevicesSettingsDialog = null;
        languageAndKeyboardDialog = null;
        mainMenuDialog = null;
        networkSettingsDialog = null;
        networkWirelessSettingsDialog = null;
        networkWirelessInformationDialog = null;
        networkAdvancedSettingsDialog = null;
        networkWiredInformationDialog = null;
        networkAdvancedManualConfigDialog = null;
        networkAdvancedProxyDialog = null;
        networkWirelessWPSConfigDialog = null;
        networkWirelessFindAPDialog = null;
        networkWirelessFindWPSDialog = null;
        networkWirelessAddHiddenNetworkDialog = null;
        parentalGuidanceDialog = null;
        passwordSecurityDialog = null;
        pictureSettingsDialog = null;
        productInfoDialog = null;
        softwareUpgradeDialog = null;
        soundSettingsDialog = null;
        subtitleSettingsDialog = null;
        teletextSettingsDialog = null;
        timeAndDateSettingsDialog = null;
        timersSettingsDialog = null;
        voiceInputDialog = null;
        epgDialog = null;
        channelInstallationSignalInfoDialog = null;
        multimediaDialog = null;
        cableNetworkDialog = null;
        audioLanguageDialog = null;
        multimediaShowDialog = null;
        subtitleLanguageDialog = null;
        epgScheduleDialog = null;
        applicationsManageManageAppsDialog = null;
        applicationsManageRunningServicesDialog = null;
        applicationsAppControlDialog = null;
        dlnaSettingsDialog = null;
        ciInfoDialog = null;
        epgReminderDialog = null;
        epgScheduleRecordingDialog = null;
        parentalControlDialog = null;
        productInfoStatusDialog = null;
        serviceModeDialog = null;
        pvrSettingsDialog = null;
        pvrMenuDialog = null;
        pvrManualEventReminderDialog = null;
        pvrManualScheduleDialog = null;
        sourceMenuDialog = null;
        systemSettingsDialog = null;
        serviceSoundDialog = null;
        debuggingDataDialog = null;
        networkTestDialog = null;
        accountsAndSyncAddAccountDialog = null;
        accountsAndSyncManageAccountsDialog = null;
        ciCamInfoDialog = null;
        offTimersSettingsDialog = null;
        offTimersAddDialog = null;
        screensaverSettingsDialog = null;
        storeModeSettingsDialog = null;
        ciSettingsDialog = null;
        osdSelectionDialog = null;
        networkIdDialog = null;
        pipSettingsDialog = null;
        favoriteListDialog = null;
        dialogs.clear();
        dialogs = null;
    }

    /******************** GETTERS AND SETTERS ******************/
    public AccountsAndSyncDialog getAccountsAndSyncDialog() {
        if (accountsAndSyncDialog == null) {
            accountsAndSyncDialog = (AccountsAndSyncDialog) createDialog("AccountsAndSyncDialog");
            dialogs.add(accountsAndSyncDialog);
        }
        return accountsAndSyncDialog;
    }

    public ApplicationsManageDialog getApplicationsManageDialog() {
        if (applicationsManageDialog == null) {
            applicationsManageDialog = (ApplicationsManageDialog) createDialog("ApplicationsManageDialog");
            dialogs.add(applicationsManageDialog);
        }
        return applicationsManageDialog;
    }

    public ChannelInstallationDialog getChannelInstallationDialog() {
        if (channelInstallationDialog == null) {
            channelInstallationDialog = (ChannelInstallationDialog) createDialog("ChannelInstallationDialog");
            dialogs.add(channelInstallationDialog);
        }
        return channelInstallationDialog;
    }

    public ChannelInstallationManualTunningDialog getChannelInstallationManualTunningDialog() {
        // if (channelInstallationManualTunningDialog == null) {
        // channelInstallationManualTunningDialog =
        // (ChannelInstallationManualTunningDialog)
        // createDialog("ChannelInstallationManualTunningDialog");
        // dialogs.add(channelInstallationManualTunningDialog);
        // }
        if (channelInstallationManualTunningDialog != null) {
            for (int i = 0; i < dialogs.size(); i++) {
                if (dialogs.get(i) != null
                        && dialogs.get(i).equals(
                                channelInstallationManualTunningDialog)) {
                    dialogs.remove(i);
                }
            }
        }
        channelInstallationManualTunningDialog = (ChannelInstallationManualTunningDialog) createDialog("ChannelInstallationManualTunningDialog");
        dialogs.add(channelInstallationManualTunningDialog);
        return channelInstallationManualTunningDialog;
    }

    public ChannelScanDialog getChannelScanDialog() {
        if (channelScanDialog == null) {
            channelScanDialog = (ChannelScanDialog) createDialog("ChannelScanDialog");
            dialogs.add(channelScanDialog);
        }
        // if (channelScanDialog != null) {
        // for (int i = 0; i < dialogs.size(); i++) {
        // if (dialogs.get(i) != null
        // && dialogs.get(i).equals(channelScanDialog)) {
        // dialogs.remove(i);
        // }
        // }
        // }
        // channelScanDialog = (ChannelScanDialog)
        // createDialog("ChannelScanDialog");
        // dialogs.add(channelScanDialog);
        return channelScanDialog;
    }

    public ContentDialog getContentDialog() {
        if (contentDialog == null) {
            contentDialog = (ContentDialog) createDialog("ContentDialog");
            dialogs.add(contentDialog);
        }
        return contentDialog;
    }

    public EnergySaveDialog getEnergySaveDialog() {
        if (energySaveDialog == null) {
            energySaveDialog = (EnergySaveDialog) createDialog("EnergySaveDialog");
            dialogs.add(energySaveDialog);
        }
        return energySaveDialog;
    }

    public ExternalAndLocalStorageDialog getExternalAndLocalStorageDialog() {
        if (externalAndLocalStorageDialog == null) {
            externalAndLocalStorageDialog = (ExternalAndLocalStorageDialog) createDialog("ExternalAndLocalStorageDialog");
            dialogs.add(externalAndLocalStorageDialog);
        }
        return externalAndLocalStorageDialog;
    }

    public FactoryResetDialog getFactoryResetDialog() {
        if (factoryResetDialog == null) {
            factoryResetDialog = (FactoryResetDialog) createDialog("FactoryResetDialog");
            dialogs.add(factoryResetDialog);
        }
        return factoryResetDialog;
    }

    public HBBSettingsDialog getHbbSettingsDialog() {
        if (hbbSettingsDialog == null) {
            hbbSettingsDialog = (HBBSettingsDialog) createDialog("HBBSettingsDialog");
            dialogs.add(hbbSettingsDialog);
        }
        return hbbSettingsDialog;
    }

    public InputDevicesSettingsDialog getInputDevicesSettingsDialog() {
        if (inputDevicesSettingsDialog == null) {
            inputDevicesSettingsDialog = (InputDevicesSettingsDialog) createDialog("InputDevicesSettingsDialog");
            dialogs.add(inputDevicesSettingsDialog);
        }
        return inputDevicesSettingsDialog;
    }

    public LanguageAndKeyboardDialog getLanguageAndKeyboardDialog() {
        if (languageAndKeyboardDialog == null) {
            languageAndKeyboardDialog = (LanguageAndKeyboardDialog) createDialog("LanguageAndKeyboardDialog");
            dialogs.add(languageAndKeyboardDialog);
        }
        return languageAndKeyboardDialog;
    }

    public MainMenuDialog getMainMenuDialog() {
        if (mainMenuDialog == null) {
            mainMenuDialog = (MainMenuDialog) createDialog("MainMenuDialog");
            dialogs.add(mainMenuDialog);
        }
        return mainMenuDialog;
    }

    public NetworkSettingsDialog getNetworkSettingsDialog() {
        if (networkSettingsDialog == null) {
            networkSettingsDialog = (NetworkSettingsDialog) createDialog("NetworkSettingsDialog");
            dialogs.add(networkSettingsDialog);
        }
        return networkSettingsDialog;
    }

    public NetworkWirelessSettingsDialog getNetworkWirelessSettingsDialog() {
        if (networkWirelessSettingsDialog == null) {
            networkWirelessSettingsDialog = (NetworkWirelessSettingsDialog) createDialog("NetworkWirelessSettingsDialog");
            dialogs.add(networkWirelessSettingsDialog);
        }
        return networkWirelessSettingsDialog;
    }

    public NetworkWiredInformationDialog getNetworkWiredInformationDialog() {
        if (networkWiredInformationDialog == null) {
            networkWiredInformationDialog = (NetworkWiredInformationDialog) createDialog("NetworkWiredInformationDialog");
            dialogs.add(networkWiredInformationDialog);
        }
        return networkWiredInformationDialog;
    }

    public NetworkWirelessInformationDialog getNetworkWirelessInformationDialog() {
        if (networkWirelessInformationDialog == null) {
            networkWirelessInformationDialog = (NetworkWirelessInformationDialog) createDialog("NetworkWirelessInformationDialog");
            dialogs.add(networkWirelessInformationDialog);
        }
        return networkWirelessInformationDialog;
    }

    public NetworkAdvancedSettingsDialog getNetworkAdvancedSettingsDialog() {
        if (networkAdvancedSettingsDialog == null) {
            networkAdvancedSettingsDialog = (NetworkAdvancedSettingsDialog) createDialog("NetworkAdvancedSettingsDialog");
            dialogs.add(networkAdvancedSettingsDialog);
        }
        return networkAdvancedSettingsDialog;
    }

    public NetworkAdvancedManualConfigDialog getNetworkAdvancedManualConfigDialog() {
        if (networkAdvancedManualConfigDialog == null) {
            networkAdvancedManualConfigDialog = (NetworkAdvancedManualConfigDialog) createDialog("NetworkAdvancedManualConfigDialog");
            dialogs.add(networkAdvancedManualConfigDialog);
        }
        return networkAdvancedManualConfigDialog;
    }

    public NetworkAdvancedProxyDialog getNetworkAdvancedProxyDialog() {
        if (networkAdvancedProxyDialog == null) {
            networkAdvancedProxyDialog = (NetworkAdvancedProxyDialog) createDialog("NetworkAdvancedProxyDialog");
            dialogs.add(networkAdvancedProxyDialog);
        }
        return networkAdvancedProxyDialog;
    }

    public NetworkAdvancedSoftAPDialog getNetworkAdvancedSoftAPDialog() {
        if (networkAdvancedSoftAPDialog == null) {
            networkAdvancedSoftAPDialog = (NetworkAdvancedSoftAPDialog) createDialog("NetworkAdvancedSoftAPDialog");
            dialogs.add(networkAdvancedSoftAPDialog);
        }
        return networkAdvancedSoftAPDialog;
    }

    public NetworkWirelessWPSConfigDialog getNetworkWirelessWPSConfigDialog() {
        if (networkWirelessWPSConfigDialog == null) {
            networkWirelessWPSConfigDialog = (NetworkWirelessWPSConfigDialog) createDialog("NetworkWirelessWPSConfigDialog");
            dialogs.add(networkWirelessWPSConfigDialog);
        }
        return networkWirelessWPSConfigDialog;
    }

    public NetworkWirelessFindWPSDialog getNetworkWirelessFindWPSDialog() {
        if (networkWirelessFindWPSDialog == null) {
            networkWirelessFindWPSDialog = (NetworkWirelessFindWPSDialog) createDialog("NetworkWirelessFindWPSDialog");
            dialogs.add(networkWirelessFindWPSDialog);
        }
        return networkWirelessFindWPSDialog;
    }

    public NetworkWirelessFindAPDialog getNetworkWirelessFindAPDialog() {
        if (networkWirelessFindAPDialog == null) {
            networkWirelessFindAPDialog = (NetworkWirelessFindAPDialog) createDialog("NetworkWirelessFindAPDialog");
            dialogs.add(networkWirelessFindAPDialog);
        }
        return networkWirelessFindAPDialog;
    }

    public NetworkWirelessAddHiddenNetworkDialog getNetworkWirelessAddHiddenNetworkDialog() {
        if (networkWirelessAddHiddenNetworkDialog == null) {
            networkWirelessAddHiddenNetworkDialog = (NetworkWirelessAddHiddenNetworkDialog) createDialog("NetworkWirelessAddHiddenNetworkDialog");
            dialogs.add(networkWirelessAddHiddenNetworkDialog);
        }
        return networkWirelessAddHiddenNetworkDialog;
    }

    public ParentalGuidanceDialog getParentalGuidanceDialog() {
        if (parentalGuidanceDialog == null) {
            parentalGuidanceDialog = (ParentalGuidanceDialog) createDialog("ParentalGuidanceDialog");
            dialogs.add(parentalGuidanceDialog);
        }
        return parentalGuidanceDialog;
    }

    public PasswordSecurityDialog getPasswordSecurityDialog() {
        if (passwordSecurityDialog == null) {
            passwordSecurityDialog = (PasswordSecurityDialog) createDialog("PasswordSecurityDialog");
            dialogs.add(passwordSecurityDialog);
        }
        return passwordSecurityDialog;
    }

    public PictureSettingsDialog getPictureSettingsDialog() {
        if (pictureSettingsDialog == null) {
            pictureSettingsDialog = (PictureSettingsDialog) createDialog("PictureSettingsDialog");
            dialogs.add(pictureSettingsDialog);
        }
        return pictureSettingsDialog;
    }

    public ProductInfoDialog getProductInfoDialog() {
        if (productInfoDialog == null) {
            productInfoDialog = (ProductInfoDialog) createDialog("ProductInfoDialog");
            dialogs.add(productInfoDialog);
        }
        return productInfoDialog;
    }

    public SoftwareUpgradeDialog getSoftwareUpgradeDialog() {
        if (softwareUpgradeDialog == null) {
            softwareUpgradeDialog = (SoftwareUpgradeDialog) createDialog("SoftwareUpgradeDialog");
            dialogs.add(softwareUpgradeDialog);
        }
        return softwareUpgradeDialog;
    }

    public SoundSettingsDialog getSoundSettingsDialog() {
        if (soundSettingsDialog == null) {
            soundSettingsDialog = (SoundSettingsDialog) createDialog("SoundSettingsDialog");
            dialogs.add(soundSettingsDialog);
        }
        return soundSettingsDialog;
    }

    public SubtitleSettingsDialog getSubtitleSettingsDialog() {
        if (subtitleSettingsDialog == null) {
            subtitleSettingsDialog = (SubtitleSettingsDialog) createDialog("SubtitleSettingsDialog");
            dialogs.add(subtitleSettingsDialog);
        }
        return subtitleSettingsDialog;
    }

    public TeletextSettingsDialog getTeletextSettingsDialog() {
        if (teletextSettingsDialog == null) {
            teletextSettingsDialog = (TeletextSettingsDialog) createDialog("TeletextSettingsDialog");
            dialogs.add(teletextSettingsDialog);
        }
        return teletextSettingsDialog;
    }

    public TimeAndDateSettingsDialog getTimeAndDateSettingsDialog() {
        if (timeAndDateSettingsDialog == null) {
            timeAndDateSettingsDialog = (TimeAndDateSettingsDialog) createDialog("TimeAndDateSettingsDialog");
            dialogs.add(timeAndDateSettingsDialog);
        }
        return timeAndDateSettingsDialog;
    }

    public TimersSettingsDialog getTimersSettingsDialog() {
        if (timersSettingsDialog == null) {
            timersSettingsDialog = (TimersSettingsDialog) createDialog("TimersSettingsDialog");
            dialogs.add(timersSettingsDialog);
        }
        return timersSettingsDialog;
    }

    public VoiceInputDialog getVoiceInputDialog() {
        if (voiceInputDialog == null) {
            voiceInputDialog = (VoiceInputDialog) createDialog("VoiceInputDialog");
            dialogs.add(voiceInputDialog);
        }
        return voiceInputDialog;
    }

    public ContextSmallDialog getContextSmallDialog() {
        contextSmallDialog = (ContextSmallDialog) createDialog("ContextSmallDialog");
        dialogs.add(contextSmallDialog);
        return contextSmallDialog;
    }

    public EPGDialog getEpgDialog() {
        if (epgDialog == null) {
            epgDialog = (EPGDialog) createDialog("EPGDialog");
            dialogs.add(epgDialog);
        }
        return epgDialog;
    }

    public ChannelInstallationSignalInformationDialog getChannelInstallationSignalInfoDialog() {
        if (channelInstallationSignalInfoDialog == null) {
            channelInstallationSignalInfoDialog = (ChannelInstallationSignalInformationDialog) createDialog("ChannelInstallationSignalInformationDialog");
            dialogs.add(channelInstallationSignalInfoDialog);
        }
        return channelInstallationSignalInfoDialog;
    }

    public MultimediaDialog getMultimediaDialog() {
        if (multimediaDialog == null) {
            multimediaDialog = (MultimediaDialog) createDialog("MultimediaDialog");
            dialogs.add(multimediaDialog);
        }
        return multimediaDialog;
    }

    public CableNetworkDialog getCableNetworkDialog() {
        if (cableNetworkDialog == null) {
            cableNetworkDialog = (CableNetworkDialog) createDialog("CableNetworkDialog");
            dialogs.add(cableNetworkDialog);
        }
        return cableNetworkDialog;
    }

    public AudioLanguageDialog getAudioLanguageDialog() {
        if (audioLanguageDialog == null) {
            audioLanguageDialog = (AudioLanguageDialog) createDialog("AudioLanguageDialog");
            dialogs.add(audioLanguageDialog);
        }
        return audioLanguageDialog;
    }

    public MultimediaShowDialog getMultimediaShowDialog() {
        if (multimediaShowDialog == null) {
            multimediaShowDialog = (MultimediaShowDialog) createDialog("MultimediaShowDialog");
            dialogs.add(multimediaShowDialog);
        }
        return multimediaShowDialog;
    }

    public SubtitleLanguageDialog getSubtitleLanguageDialog() {
        if (subtitleLanguageDialog == null) {
            subtitleLanguageDialog = (SubtitleLanguageDialog) createDialog("SubtitleLanguageDialog");
            dialogs.add(subtitleLanguageDialog);
        }
        return subtitleLanguageDialog;
    }

    public EPGScheduleDialog getEpgScheduleDialog() {
        if (epgScheduleDialog == null) {
            epgScheduleDialog = (EPGScheduleDialog) createDialog("EPGScheduleDialog");
            dialogs.add(epgScheduleDialog);
        }
        return epgScheduleDialog;
    }

    public ApplicationsManageManageAppsDialog getApplicationsManageManageAppsDialog() {
        if (applicationsManageManageAppsDialog == null) {
            applicationsManageManageAppsDialog = (ApplicationsManageManageAppsDialog) createDialog("ApplicationsManageManageAppsDialog");
            dialogs.add(applicationsManageManageAppsDialog);
        }
        return applicationsManageManageAppsDialog;
    }

    public ApplicationsManageRunningServicesDialog getApplicationsManageRunningServicesDialog() {
        if (applicationsManageRunningServicesDialog == null) {
            applicationsManageRunningServicesDialog = (ApplicationsManageRunningServicesDialog) createDialog("ApplicationsManageRunningServicesDialog");
            dialogs.add(applicationsManageRunningServicesDialog);
        }
        return applicationsManageRunningServicesDialog;
    }

    public ApplicationsAppControlDialog getApplicationsAppControlDialog() {
        if (applicationsAppControlDialog == null) {
            applicationsAppControlDialog = (ApplicationsAppControlDialog) createDialog("ApplicationsAppControlDialog");
            dialogs.add(applicationsAppControlDialog);
        }
        return applicationsAppControlDialog;
    }

    public DLNASettingsDialog getDlnaSettingsDialog() {
        if (dlnaSettingsDialog == null) {
            dlnaSettingsDialog = (DLNASettingsDialog) createDialog("DLNASettingsDialog");
            dialogs.add(dlnaSettingsDialog);
        }
        return dlnaSettingsDialog;
    }

    public CIInfoDialog getCiInfoDialog() {
        if (ciInfoDialog == null) {
            ciInfoDialog = (CIInfoDialog) createDialog("CIInfoDialog");
            dialogs.add(ciInfoDialog);
        }
        return ciInfoDialog;
    }

    public CICamInfoDialog getCICamInfoDialog() {
        if (ciCamInfoDialog == null) {
            ciCamInfoDialog = (CICamInfoDialog) createDialog("CICamInfoDialog");
            dialogs.add(ciCamInfoDialog);
        }
        return ciCamInfoDialog;
    }

    public EpgReminderDialog getEpgReminderDialog() {
        if (epgReminderDialog != null) {
            for (int i = 0; i < dialogs.size(); i++) {
                if (dialogs.get(i) != null
                        && dialogs.get(i).equals(epgReminderDialog)) {
                    dialogs.remove(i);
                }
            }
        }
        epgReminderDialog = (EpgReminderDialog) createDialog("EpgReminderDialog");
        dialogs.add(epgReminderDialog);
        return epgReminderDialog;
    }

    public EpgScheduleRecordingDialog getEpgScheduleRecordingDialog() {
        if (epgScheduleRecordingDialog != null) {
            for (int i = 0; i < dialogs.size(); i++) {
                if (dialogs.get(i) != null
                        && dialogs.get(i).equals(epgScheduleRecordingDialog)) {
                    dialogs.remove(i);
                }
            }
        }
        epgScheduleRecordingDialog = (EpgScheduleRecordingDialog) createDialog("EpgScheduleRecordingDialog");
        dialogs.add(epgScheduleRecordingDialog);
        return epgScheduleRecordingDialog;
    }

    public ParentalControlDialog getParentalControlDialog() {
        if (parentalControlDialog == null) {
            parentalControlDialog = (ParentalControlDialog) createDialog("ParentalControlDialog");
            dialogs.add(parentalControlDialog);
        }
        return parentalControlDialog;
    }

    public ProductInfoSoftwareStatusDialog getProductInfoStatusDialog() {
        if (productInfoStatusDialog == null) {
            productInfoStatusDialog = (ProductInfoSoftwareStatusDialog) createDialog("ProductInfoSoftwareStatusDialog");
            dialogs.add(productInfoStatusDialog);
        }
        return productInfoStatusDialog;
    }

    public ServiceModeDialog getServiceModeDialog() {
        if (serviceModeDialog == null) {
            serviceModeDialog = (ServiceModeDialog) createDialog("ServiceModeDialog");
            dialogs.add(serviceModeDialog);
        }
        return serviceModeDialog;
    }

    public OffTimersSettingsDialog getOffTimersSettingsDialog() {
        if (offTimersSettingsDialog == null) {
            offTimersSettingsDialog = (OffTimersSettingsDialog) createDialog("OffTimersSettingsDialog");
            dialogs.add(offTimersSettingsDialog);
        }
        return offTimersSettingsDialog;
    }

    public OffTimersAddDialog getOffTimersAddDialog() {
        if (offTimersAddDialog == null) {
            offTimersAddDialog = (OffTimersAddDialog) createDialog("OffTimersAddDialog");
            dialogs.add(offTimersAddDialog);
        }
        return offTimersAddDialog;
    }

    public PVRSettingsDialog getPVRSettingsDialog() {
        if (pvrSettingsDialog == null) {
            pvrSettingsDialog = (PVRSettingsDialog) createDialog("PVRSettingsDialog");
            dialogs.add(pvrSettingsDialog);
        }
        return pvrSettingsDialog;
    }

    public PVRMenuDialog getPVRMenuDialog() {
        if (pvrMenuDialog == null) {
            pvrMenuDialog = (PVRMenuDialog) createDialog("PVRMenuDialog");
            dialogs.add(pvrMenuDialog);
        }
        return pvrMenuDialog;
    }

    public PVRManualEventReminderDialog getPVRManualEventReminderDialog() {
        if (pvrManualEventReminderDialog == null) {
            pvrManualEventReminderDialog = (PVRManualEventReminderDialog) createDialog("PVRManualEventReminderDialog");
            dialogs.add(pvrManualEventReminderDialog);
        }
        return pvrManualEventReminderDialog;
    }

    public PVRManualScheduleDialog getPVRManualScheduleDialog() {
        if (pvrManualScheduleDialog == null) {
            pvrManualScheduleDialog = (PVRManualScheduleDialog) createDialog("PVRManualScheduleDialog");
            dialogs.add(pvrManualScheduleDialog);
        }
        return pvrManualScheduleDialog;
    }

    public SourceMenuDialog getSourceMenuDialog() {
        if (sourceMenuDialog == null) {
            sourceMenuDialog = (SourceMenuDialog) createDialog("SourceMenuDialog");
            dialogs.add(sourceMenuDialog);
        }
        return sourceMenuDialog;
    }

    public SystemSettingsDialog getSystemSettingsDialog() {
        if (systemSettingsDialog == null) {
            systemSettingsDialog = (SystemSettingsDialog) createDialog("SystemSettingsDialog");
            dialogs.add(systemSettingsDialog);
        }
        return systemSettingsDialog;
    }

    public ServiceSoundDialog getServiceSoundDialog() {
        if (serviceSoundDialog == null) {
            serviceSoundDialog = (ServiceSoundDialog) createDialog("ServiceSoundDialog");
            dialogs.add(serviceSoundDialog);
        }
        return serviceSoundDialog;
    }

    public DebuggingDataDialog getDebuggingDataDialog() {
        if (debuggingDataDialog == null) {
            debuggingDataDialog = (DebuggingDataDialog) createDialog("DebuggingDataDialog");
            dialogs.add(debuggingDataDialog);
        }
        return debuggingDataDialog;
    }

    public NetworkTestDialog getNetworkTestDialog() {
        if (networkTestDialog == null) {
            networkTestDialog = new NetworkTestDialog(activity);
        }
        return networkTestDialog;
    }

    public AccountsAndSyncAddAccountDialog getAccountsAndSyncAddAccountDialog() {
        if (accountsAndSyncAddAccountDialog == null) {
            accountsAndSyncAddAccountDialog = (AccountsAndSyncAddAccountDialog) createDialog("AccountsAndSyncAddAccountDialog");
        }
        return accountsAndSyncAddAccountDialog;
    }

    public AccountsAndSyncManageAccountsDialog getAccountsAndSyncManageAccountsDialog() {
        if (accountsAndSyncManageAccountsDialog == null) {
            accountsAndSyncManageAccountsDialog = (AccountsAndSyncManageAccountsDialog) createDialog("AccountsAndSyncManageAccountsDialog");
        }
        return accountsAndSyncManageAccountsDialog;
    }

    public ScreensaverSettingsDialog getScreensaverSettingsDialog() {
        if (screensaverSettingsDialog == null) {
            screensaverSettingsDialog = (ScreensaverSettingsDialog) createDialog("ScreensaverSettingsDialog");
            dialogs.add(screensaverSettingsDialog);
        }
        return screensaverSettingsDialog;
    }

    public StoreModeSettingsDialog getStoreModeSettingsDialog() {
        if (storeModeSettingsDialog == null) {
            storeModeSettingsDialog = (StoreModeSettingsDialog) createDialog("StoreModeSettingsDialog");
            dialogs.add(storeModeSettingsDialog);
        }
        return storeModeSettingsDialog;
    }

    public CISettingsDialog getCISettingsDialog() {
        if (ciSettingsDialog == null) {
            ciSettingsDialog = (CISettingsDialog) createDialog("CISettingsDialog");
        }
        return ciSettingsDialog;
    }

    public OSDSelectionDialog getOSDSelectionDialog() {
        if (osdSelectionDialog == null) {
            osdSelectionDialog = (OSDSelectionDialog) createDialog("OSDSelectionDialog");
        }
        return osdSelectionDialog;
    }

    public NetworkIdDialog getNetworkIdDialog() {
        if (networkIdDialog == null) {
            networkIdDialog = (NetworkIdDialog) createDialog("NetworkIdDialog");
        }
        return networkIdDialog;
    }

    public SoundPostProcessingDialog getSoundPostProcessingDialog() {
        if (soundPostProcessingDialog == null) {
            soundPostProcessingDialog = (SoundPostProcessingDialog) createDialog("SoundPostProcessingDialog");
            dialogs.add(soundPostProcessingDialog);
        }
        return soundPostProcessingDialog;
    }

    public PiPSettingsDialog getPiPSettingsDialog() {
        if (pipSettingsDialog == null) {
            pipSettingsDialog = (PiPSettingsDialog) createDialog("PiPSettingsDialog");
            dialogs.add(pipSettingsDialog);
        }
        return pipSettingsDialog;
    }

    public FavoriteListDialog getFavoriteListDialog() {
        if (favoriteListDialog == null) {
            favoriteListDialog = (FavoriteListDialog) createDialog("FavoriteListDialog");
            dialogs.add(favoriteListDialog);
        }
        return favoriteListDialog;
    }
}
