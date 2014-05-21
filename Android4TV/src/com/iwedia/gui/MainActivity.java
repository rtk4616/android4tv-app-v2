package com.iwedia.gui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.iwedia.comm.IActionCallback;
import com.iwedia.comm.IChannelsCallback;
import com.iwedia.comm.IDTVManagerProxy;
import com.iwedia.comm.IDlnaCallback;
import com.iwedia.comm.IEpgCallback;
import com.iwedia.comm.IHbbTvCallback;
import com.iwedia.comm.IInputOutputCallback;
import com.iwedia.comm.IMhegCallback;
import com.iwedia.comm.IPvrCallback;
import com.iwedia.comm.ISetupCallback;
import com.iwedia.comm.IStreamComponentCallback;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.comm.reminder.IReminderCallback;
import com.iwedia.comm.system.INetworkCallback;
import com.iwedia.comm.system.application.AppSizeInfo;
import com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings;
import com.iwedia.dtv.reminder.ReminderEventAdd;
import com.iwedia.dtv.reminder.ReminderEventRemove;
import com.iwedia.dtv.reminder.ReminderEventTrigger;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.callbacks.CallBackHandler;
import com.iwedia.gui.callbacks.PVRCallBack;
import com.iwedia.gui.ci.CICallbackController;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVMultimediaVideoView;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVProgressBarPVR.ControlProviderPVR;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.A4TVVideoView;
import com.iwedia.gui.components.dialogs.AccountsAndSyncDialog;
import com.iwedia.gui.components.dialogs.AccountsAndSyncManageAccountsDialog;
import com.iwedia.gui.components.dialogs.ApplicationsAppControlDialog;
import com.iwedia.gui.components.dialogs.ApplicationsManageManageAppsDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationDialog;
import com.iwedia.gui.components.dialogs.ChannelScanDialog;
import com.iwedia.gui.components.dialogs.EPGScheduleDialog;
import com.iwedia.gui.components.dialogs.ExternalAndLocalStorageDialog;
import com.iwedia.gui.components.dialogs.LanguageAndKeyboardDialog;
import com.iwedia.gui.components.dialogs.NetworkSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessAddHiddenNetworkDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessFindAPDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessFindWPSDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessWPSConfigDialog;
import com.iwedia.gui.components.dialogs.PVRSettingsDialog;
import com.iwedia.gui.components.dialogs.ParentalGuidanceDialog;
import com.iwedia.gui.components.dialogs.PasswordSecurityDialog;
import com.iwedia.gui.components.dialogs.PictureSettingsDialog;
import com.iwedia.gui.components.dialogs.ScreenSaverDialog;
import com.iwedia.gui.components.dialogs.ServiceModeDialog;
import com.iwedia.gui.components.dialogs.SoftwareUpgradeDialog;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.content_list.ContentListHandler;
import com.iwedia.gui.dual_video.DualVideoManager;
import com.iwedia.gui.epg.EPGHandlingClass;
import com.iwedia.gui.graphics.HbbTVDialog;
import com.iwedia.gui.graphics.MhegDialogView;
import com.iwedia.gui.graphics.SubtitleDialogView;
import com.iwedia.gui.graphics.TeletextDialogView;
import com.iwedia.gui.imagecache.ImageMemCache;
import com.iwedia.gui.listeners.A4TVGalleryOnClickListener;
import com.iwedia.gui.listeners.A4TVVideoViewOnErrorListener;
import com.iwedia.gui.listeners.A4TVVideoViewOnPreparedListener;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.listeners.MultimediaVideoViewOnCompletionListener;
import com.iwedia.gui.listeners.MultimediaVideoViewOnPreparedListener;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.mainmenu.MainMenuHandlingClass;
import com.iwedia.gui.multimedia.MultimediaGridHelper;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.multimedia.MultimediaPlayer;
import com.iwedia.gui.multimedia.controller.MediaController;
import com.iwedia.gui.multimedia.dlna.renderer.controller.RendererController;
import com.iwedia.gui.osd.CheckServiceType;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.osd.curleffect.CurlHandler;
import com.iwedia.gui.osd.infobanner.InfoBannerHandler;
import com.iwedia.gui.osd.noneinfobanner.NoneBannerHandler;
import com.iwedia.gui.pip.PiPController;
import com.iwedia.gui.pip.PiPView;
import com.iwedia.gui.pvr.A4TVStorageManager;
import com.iwedia.gui.pvr.PVRHandler;
import com.iwedia.gui.widgets.WidgetsHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends Activity implements OSDGlobal {
    public static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;
    public static final int DEFAULT_HD_REGION_WIDTH = 1920;
    public static final int DEFAULT_HD_REGION_HEIGHT = 1080;
    public static final int MULTIMEDIA_MAIN = 0;
    public static final int MULTIMEDIA_PIP = 1;
    public static final int MULTIMEDIA_PAP = 2;
    public static int mMultimediaMode = MULTIMEDIA_MAIN;
    public static final String DATA_STORAGE = "Android4TVGUIData";
    public static final String PRIMARY_VIDEO_SURFACE_NAME = "iwedia.video.surface.primary";
    public static final String SECONDARY_VIDEO_SURFACE_NAME = "iwedia.video.surface.secondary";
    public static final String EARLY_VIDEO_PROPERTY_NAME = "iwedia.app.earlyvideo";
    public static final String OVERLAY_VIDEO_SERVICE = "com.iwedia.OVERLAY_SERVICE";
    /** Configuration handler */
    private ConfigHandler configHandler;
    /** Shared preferences */
    public static SharedPreferences sharedPrefs;
    // /////////////////////////////////////////
    // Shared prefs constant
    // /////////////////////////////////////////
    /** Constant for curl enabled */
    public static final String CURL_ENABLED = "Curl enabled";
    private static SimpleDateFormat formatTime24Hour = new SimpleDateFormat(
            "HH:mm");
    /** Constant for curl enabled */
    public static final String OSD_SELECTION = "Osd selection";
    /** Constant for curl animation time info */
    public static final String CURL_ANIMATION_TIME_INFO = "Curl animation time info";
    /** Constant for curl animation on/off */
    public static final String CURL_ANIMATION_ON_OFF = "Curl animation on off";
    /** Constant for screen saver enabled */
    public static final String SCREENSAVER_ENABLED = "Screen saver enabled";
    /** Constant for screen saver time */
    public static final String SCREENSAVER_TIME_MILISECONDS = "Screen saver time seconds";
    /** Fields for store mode video presentation enabled */
    public static boolean isInStoreMode = false;
    public static final String STORE_MODE_VIDEO_PRESENTATION = "Store mode video presentation enabled";
    public static final String STORE_MODE_START = "Store mode start";
    /** Fields for service mode */
    public static boolean isInServiceMode = false;
    public static final String SERVICE_MODE_START = "Service mode start";
    private static BroadcastReceiver broadcast_reciever;
    private static final String START_PVR_RECORDING = "startPVRrecord";
    private static final String STOP_PVR_RECORDING = "stopPVRrecord";
    /** Constants for PIP settings */
    // TODO: To support custom settings it would be good to store Map in
    // SharedPref?
    public static final String PIP_SIZE = "PIP_size";
    /*
     * 0 - 1/9; 1 - 1/16 2 - Custom
     */
    public static final String PIP_POSITION = "PIP_position";
    /*
     * 0 - upper right; 1 - upper left; 2 - lower left; 3 - lower right 4 -
     * custom
     */
    public static final String PIP_X = "PIP_coordinate_X";
    public static final String PIP_Y = "PIP_coordinate_Y";
    public static final String PIP_WIDTH = "PIP_coordinate_WIDTH";
    public static final String PIP_HEIGHT = "PIP_coordinate_HEIGHT";
    /** Offsets for PIP/PAP window */
    public static final int PIP_WINDOW_HORIZONTAL_OFFSET = 50;
    public static final int PIP_WINDOW_VERTICAL_OFFSET = 30;
    // PIP parameters
    public static int pipWindowCoordinateLeft = 0;
    public static int pipWindowCoordinateTop = 0;
    public static int pipWindowWidth = 0;
    public static int pipWindowHeight = 0;
    // PAP parameters
    public static int papWindowCoordinateLeft = 0;
    public static int papWindowCoordinateTop = 0;
    public static int papWindowWidth = 0;
    public static int papWindowHeight = 0;
    // /////////////////////////////////////////
    /** Enable/Disable animations in app */
    public static boolean enabledAnimations = true;
    /** Enable key handling cool down period in app */
    public static boolean enableKeyHandlingCoolDownPerion = true;
    /** If users holds down some key, handle it every xxx milliseconds */
    public static int KEY_HANDLING_COOL_DOWN_PERIOD = 150;
    public static boolean isStatusPlayback = false;
    /** Screen width and height */
    public static int screenHeight, screenWidth, dialogHeight, dialogWidth,
            dialogListElementHeight;
    private DualVideoManager dualVideoManager;
    /** For video playback */
    private RelativeLayout mPrimaryVideoHolder;
    private A4TVVideoView mPrimaryVideoView;
    /** For secondary video playback */
    private RelativeLayout mSecondaryVideoHolder;
    private A4TVVideoView mSecondaryVideoView;
    public int primaryVideoStyle = 0;
    private MainMenuContent mMainMenuContent;
    /** For early video */
    private boolean mIsEarlyVideo = false;
    /** For DLNA video playback */
    private A4TVMultimediaVideoView mPrimaryMultimediaVideoView = null;
    private boolean mMultimediaVideoReady = false;
    /** PlayBack Controller */
    private MediaController mediaController = null;
    private RendererController rendererController = null;
    /** Dialog manager */
    private DialogManager dialogManager;
    /** Main menu fields */
    private MainMenuHandlingClass mainMenuHandler;
    /** EPG handling class */
    private EPGHandlingClass epgHandler;
    public static IOSDHandler mPageCurl;
    /** CallBack Handler */
    private CallBackHandler mCallBackHandler = null;
    /** Widgets handler class */
    private WidgetsHandler widgetsHandler;
    /** Content list */
    private ContentListHandler contentListHandler;
    /** Multimedia */
    private MultimediaHandler multimediaHandler;
    /** Listeners */
    public static MainKeyListener mainKeyListener;
    public static ScreenSaverDialog screenSaverDialog;
    public static MainActivity activity;
    /** Needed for starting service */
    private Intent remoteServiceIntent;
    /** Binder for service */
    private BinderServiceConnection conn;
    /** Proxy service */
    public static IDTVManagerProxy service = null;
    /** Memory Cache */
    public static ImageMemCache mMemoryCache = null;
    /** Input Manager */
    public static MultimediaPlayer mExternalView = null;
    // private InputManager mInputMgr = null;
    public static SurfaceView mActiveView = null;
    /** Input Manager */
    public WebView mWebView = null;
    /** HBB fields */
    public static int keySet = 0;
    public static HbbTVDialog webDialog;
    public static String pomStr;
    public static int isPlayKey = 1;
    /** MHEG fields */
    public static int mhegKeySet = 0;
    public static boolean videoSignalStatus = false;
    /** PiP */
    private PiPView mPiPView = null;
    private PiPController pipController = null;
    private VideoView mPiPVideoView = null;
    /** CI */
    private static CICallbackController ciCallbackController = null;
    public static boolean isCICardEntered = false;
    /** Bundle for saving important object onPause */
    public static Bundle bundle = new Bundle();
    public static boolean firstTimeForWidgets = true;
    private MhegDialogView mMhegDialogView;
    private TeletextDialogView mTeletextDialogView;
    private SubtitleDialogView mSubtitleDialogView;
    public static final int SCREEN_WIDTH_576P = 720, SCREEN_HEIGHT_576P = 576,
            SCREEN_WIDTH_720P = 1280, SCREEN_HEIGHT_720P = 720,
            SCREEN_WIDTH_1080P = 1920, SCREEN_HEIGHT_1080P = 1080;
    /** For returning main menu when there is app restart settings */
    public static boolean stopVideoOnPauseAndReturnMenuToUser = true;
    /** Progress dialog for loading data from DLNA controller */
    private A4TVProgressDialog progressDialog;
    /** Progress dialog for source swiching */
    public A4TVProgressDialog sourceSwichingProgressDialog;
    private static Handler hbbHandler;
    public static final int HBBTV_ON_TOP = 0, WIDGETS_ON_TOP = 1;
    public static final int PIP_MODE = 0;
    public static final int PAP_MODE = 1;
    public static final int PRIMARY_DISPLAY_UNIT_ID = 0;
    public static final int SECONDARY_DISPLAY_UNIT_ID = 1;
    /** First time installation fields */
    public static boolean runFirstTimeInstallState = true,
            isInFirstTimeInstall = false;
    public static final String FIRST_TIME_INSTALL = "first_time_install";
    private RelativeLayout firstTimeInstallLayout;
    private A4TVTextView firstTimeInfoText;
    private A4TVButton firstTimeInstallNextBtn;
    public static int firstTimeInstallActiveDialog;
    public static boolean factoryReset = false;
    public static boolean showSubtitleWhenTeletextHide = false;
    public static int subtitleTitleTrackIndex = -1;
    public static boolean subtitleON = false;
    private static CheckServiceType checkServiceType;
    private InternetStateChangeReceiver mConnReceiver;
    private static IContentListControl contentListControl;
    private Content inputContent;
    private Content prevContent;
    private int err = 0;
    /** Used for parental control */
    private int currentVideoTrackIndex = 0;
    private int currentAudioTrackIndex = 0;
    private A4TVStorageManager storage;
    private boolean playOverlayVideoOnStop = false;
    public static int epgClientId;
    private static boolean activityActive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        activity = this;
        // Get shared prefs object
        sharedPrefs = getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        // set theme before setting content view
        ThemeUtils.onActivityCreateSetTheme(this);
        Window window = getWindow();
        if (window != null) {
            window.setFormat(PixelFormat.RGBA_8888);
            window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
            window.getDecorView().getBackground().setDither(true);
        }
        // Load configuration
        configHandler = new ConfigHandler(getApplicationContext());
        configHandler.loadConfiguration();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        if (window != null) {
            window.setFormat(PixelFormat.RGBA_8888);
        }
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        activityActive = true;
        // stop overlay video
        Intent overlayIntent = new Intent(OVERLAY_VIDEO_SERVICE);
        if (stopService(overlayIntent)) {
            // TODO: Should we wait for service to release resources?
            Log.d(TAG, "stopService(overlayIntent)");
        }
        mConnReceiver = new InternetStateChangeReceiver();
        // Register receiver for network state change event
        registerReceivers();
        broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action != null)
                    if (action.equals(START_PVR_RECORDING)) {
                        try {
                            MainActivity.service.getPvrControl()
                                    .createOnTouchRecord(0);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if (action.equals(STOP_PVR_RECORDING)) {
                        try {
                            MainActivity.service.getPvrControl().destroyRecord(
                                    0);
                        } catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_PVR_RECORDING);
        filter.addAction(STOP_PVR_RECORDING);
        registerReceiver(broadcast_reciever, filter);
        activity = this;
        // //////////////////////////////////////////
        // Init Memory Cache
        // //////////////////////////////////////////
        // For initialization of main menu icons
        mMainMenuContent = new MainMenuContent(activity);
        mMemoryCache = new ImageMemCache(activity);
        mMemoryCache.addImagesToMemory(MainMenuContent.mainMenuIcons);
        mMemoryCache.addImagesToMemory(MainMenuContent.submenuSettings);
        mMemoryCache
                .addImagesToMemory(MainMenuContent.submenuSettingsTvSettings);
        mMemoryCache
                .addImagesToMemory(MainMenuContent.submenuSettingsTvSecuritySettings);
        // Set xml content
        setContentView(R.layout.main);
        // check if early video
        // TODO: This Should Be Fixed!
        // if (android.os.SystemProperties.get("iwedia.mw.earlyvideo",
        // "disabled")
        // .equals("handled")
        // && System.getProperty(EARLY_VIDEO_PROPERTY_NAME, "1").equals(
        // "1")) {
        // mIsEarlyVideo = true;
        // }
        if (mIsEarlyVideo) {
            connectWithServer();
        } else {
            initialize();
        }
        super.onResume();
    }

    private void initialize() {
        // Init app
        initApp();
        // init main menu
        initMainMenu();
        // Init page curl effect
        initPageCurl();
        // Init widgets handler
        initWidgetsHandler();
        // Initialize HbbTV dialog
        webDialog = new HbbTVDialog(activity,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        webDialog.show();
    }

    public void onResumeFromError() {
        this.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "ON PAUSE");
        activityActive = false;
        /* stop screen saver */
        screenSaverDialog.stopScreensaverTimer();
        this.unregisterReceiver(mConnReceiver);
        this.unregisterReceiver(broadcast_reciever);
        try {
            mMhegDialogView.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mSubtitleDialogView.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTeletextDialogView.hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (stopVideoOnPauseAndReturnMenuToUser) {
            // Stop video playback on pause of activity
            if (service != null) {
                try {
                    service.getContentListControl().stopVideoPlayback();
                    releaseVideoViews();
                    Log.d(TAG, "PASSED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                service.getContentListControl().stopVideoPlayback();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // if some dialog is open
            if (A4TVDialog.getListOfDialogs() != null
                    && A4TVDialog.getListOfDialogs().size() > 0) {
                if (A4TVDialog.getListOfDialogs().get(0)
                        .equals(dialogManager.getLanguageAndKeyboardDialog())) {
                    firstTimeInstallActiveDialog = 1;
                } else if (A4TVDialog.getListOfDialogs().get(0)
                        .equals(dialogManager.getPictureSettingsDialog())) {
                    firstTimeInstallActiveDialog = 2;
                } else if (A4TVDialog.getListOfDialogs().get(0)
                        .equals(dialogManager.getAccountsAndSyncDialog())) {
                    firstTimeInstallActiveDialog = 3;
                } else if (A4TVDialog.getListOfDialogs().get(0)
                        .equals(dialogManager.getNetworkSettingsDialog())) {
                    firstTimeInstallActiveDialog = 4;
                } else if (A4TVDialog.getListOfDialogs().get(0)
                        .equals(dialogManager.getChannelInstallationDialog())) {
                    firstTimeInstallActiveDialog = 5;
                }
            }
        }
        new Thread() {
            public void run() {
                try {
                    Log.d(TAG, "deinitDlna()");
                    MainActivity.service.getDlnaControl().deinitDlna();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            };
        }.start();
        // Destroy page curl object
        mPageCurl = null;
        if (webDialog != null) {
            webDialog.cancel();
            webDialog = null;
        }
        // If widget handler isn't null re-init
        if (widgetsHandler != null) {
            if (widgetsHandler.getmAppWidgetHost() != null) {
                widgetsHandler.getmAppWidgetHost().stopListening();
            }
            try {
                widgetsHandler.saveImportantData();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            widgetsHandler.clearScreen();
        }
        // /////////////////////////////////
        // Alert dialog bug fix
        // /////////////////////////////////
        MainKeyListener.enableKeyCodeBack = false;
        // /////////////////////////////////
        // Main menu dialog icons bug fix
        // /////////////////////////////////
        MainKeyListener.enableKeyCodeMenu = false;
        mainKeyListener = null;
        // Hide opened dialog
        hideDialogs();
        hbbHandler = null;
        System.gc();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        try {
            mCallBackHandler.unRegisterCallBacks(service);
            service.getHbbTvControl().unsetCallbackHbb(hbbCallback);
            service.getInputOutputControl().unregisterCallback(
                    inputOutputCallback);
            MainActivity.service.getCIControl().unregisterCallback(
                    ciCallbackController.getCallback());
            service.getEpgControl()
                    .unregisterCallback(epgCallback, epgClientId);
            service.getEpgControl().releaseEventList(epgClientId);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e(TAG, "Not connected to service");
            e.printStackTrace();
        }
        // ///////////////////////////
        // Unbind from service
        // ///////////////////////////
        if (conn != null) {
            try {
                unbindService(conn);
                conn = null;
            } catch (Exception e) {
                System.out.println(e.toString());
                Log.e(TAG, "unbind on pause exception");
            }
        }
        if (playOverlayVideoOnStop) {
            playOverlayVideo(true);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "DESTROY");
        try {
            service.getHbbTvControl().unsetCallbackHbb(hbbCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // ///////////////////////////////
        // Widget manager
        // ///////////////////////////////
        try {
            widgetsHandler.getmAppWidgetHost().stopListening();
        } catch (RuntimeException ex) {
            Log.w(TAG,
                    "problem while stopping AppWidgetHost during Lockscreen destruction",
                    ex);
        }
        /** Stop connection to update server */
        try {
            MainActivity.service.getSystemControl().getAbout()
                    .getSoftwareUpdate().stopConnection();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /** Show subtitles */
    public static void showSubtitleDialog(final int trackIndex) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    service.getSubtitleControl().setCurrentSubtitleTrack(
                            trackIndex);
                    service.getSubtitleControl().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public Object getSystemService(String name) {
        if (name != null) {
            if (name.equals(PRIMARY_VIDEO_SURFACE_NAME)) {
                try {
                    return service.getDisplayControl().getVideoLayerSurface(0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (name.equals(SECONDARY_VIDEO_SURFACE_NAME)) {
                try {
                    return service.getDisplayControl().getVideoLayerSurface(1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.getSystemService(name);
    }

    A4TVVideoViewOnPreparedListener mmPrepared;

    private void initVideoViews() {
        primaryVideoStyle = 0;
        // int secondaryVideoStyle;
        if (mIsEarlyVideo) {
            primaryVideoStyle = -1;
            // secondaryVideoStyle = -2;
        }
        android.widget.MediaController mMediaController = new android.widget.MediaController(
                getApplicationContext());
        /** PRIMARY VIDEO VIEW */
        mPrimaryVideoHolder = (RelativeLayout) findViewById(R.id.primaryVideoHolder);
        // mPrimaryVideoView = (A4TVVideoView) findViewById(R.id.mainVideo);
        Log.d(TAG, "Create PRIMARY VIDEO VIEW");
        mPrimaryVideoView = new A4TVVideoView(this, null, primaryVideoStyle);
        mPrimaryVideoHolder.addView(mPrimaryVideoView);
        mPrimaryVideoView.setVideoURI(Uri.parse("tv://tv:0?view=0"));
        Log.d(TAG, "Create PRIMARY VIDEO VIEW DONE");
        mPrimaryVideoView.setMediaController(mMediaController);
        mPrimaryVideoView.requestFocus();
        mmPrepared = new A4TVVideoViewOnPreparedListener();
        mPrimaryVideoView.setOnPreparedListener(mmPrepared);
        mPrimaryVideoView
                .setOnErrorListener(new A4TVVideoViewOnErrorListener());
        if (!ConfigHandler.TVPLATFORM) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mmPrepared.onPrepared(new MediaPlayer());
                }
            }).start();
        }
        /** SECONDARY VIDEO VIEW */
        attachSecondaryA4TVVideoView();
    }

    public static void initDialogDimensions(Activity a) {
        screenHeight = a.getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = a.getWindowManager().getDefaultDisplay().getWidth();
        Log.d(TAG, "height, width " + screenHeight + ", " + screenWidth);
        /** Small dialog dimensions */
        if (screenHeight == 1280 || screenWidth == 1280) {
            dialogWidth = (int) ((MainActivity.screenWidth / 2) * 1.1);
            dialogHeight = (int) ((2 * MainActivity.screenHeight / 3) * 1.1);
            dialogListElementHeight = screenHeight / 12;
        } else if (screenHeight == 1920 || screenWidth == 1920) {
            dialogWidth = (int) ((MainActivity.screenWidth / 2) * 1.1);
            dialogHeight = (int) ((2 * MainActivity.screenHeight / 3) * 1.1);
            dialogListElementHeight = screenHeight / 12;
        } else {
            dialogWidth = MainActivity.screenWidth / 2;
            dialogHeight = 2 * MainActivity.screenHeight / 3;
            dialogListElementHeight = screenHeight / 17;
        }
    }

    /** Init main view fields */
    private void initApp() {
        initDialogDimensions(this);
        // set PIP/PAP Window coordinates
        updatePIPCoordinates();
        updatePAPCoordinates();
        Log.d(TAG, "**********************setVideoURI************************");
        initVideoViews();
        dualVideoManager = new DualVideoManager(this);
        mExternalView = new MultimediaPlayer(this);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.a4tv_main);
        layout.addView(mExternalView);
        mPiPVideoView = new VideoView(this);
        layout.addView(mPiPVideoView);
        if (mPiPView == null) {
            mPiPView = new PiPView(activity);
            // mPiPView.attachView(mPiPVideoView);
        }
        // Create Controllers
        mediaController = new MediaController(activity, mExternalView);
        rendererController = new RendererController(activity, mediaController);
        pipController = new PiPController(activity, mPiPView);
        /** Manager that creates dialogs */
        dialogManager = new DialogManager(this);
        dialogManager.init();
        // Create progress dialog for loading data from DLNA controller
        progressDialog = new A4TVProgressDialog(activity);
        progressDialog.setTitleOfAlertDialog(R.string.loading_data);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(R.string.please_wait);
        // Create progress dialog for loading data from DLNA controller
        sourceSwichingProgressDialog = new A4TVProgressDialog(activity);
        sourceSwichingProgressDialog
                .setTitleOfAlertDialog(R.string.source_swiching);
        sourceSwichingProgressDialog.setCancelable(false);
        sourceSwichingProgressDialog.setMessage(R.string.please_wait);
        // Handle hbb and widget dialog conflict
        hbbHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (!isInFirstTimeInstall) {
                    // ///////////////////////
                    // Hbb dialog on top
                    // ///////////////////////
                    if (msg.what == HBBTV_ON_TOP) {
                        webDialog.cancel();
                        webDialog.show();
                        Log.d(TAG, "ENTERED");
                    }
                    // ///////////////////////
                    // Widget dialog on top
                    // ///////////////////////
                    if (msg.what == WIDGETS_ON_TOP) {
                        widgetsHandler.hideWidgetDialog();
                        widgetsHandler.showWidgetDialog();
                        if (A4TVDialog.getListOfDialogs() != null
                                && A4TVDialog.getListOfDialogs().size() > 0) {
                            ArrayList<A4TVDialog> dialogs = new ArrayList<A4TVDialog>();
                            dialogs.addAll(A4TVDialog.getListOfDialogs());
                            for (int i = dialogs.size() - 1; i >= 0; i--) {
                                if (dialogs.size() > 0) {
                                    dialogs.get(i).cancel();
                                    dialogs.get(i).show();
                                }
                            }
                        }
                    }
                }
            };
        };
        // init mheg and subtitle
        initializeSubtitle();
        initializeTeletext();
        initializeMHEG();
        initScreenSaver();
        storage = new A4TVStorageManager();
        // Initialize CallBack Handler
        mCallBackHandler = new CallBackHandler(this);
    }

    public static final String IS_FIRST_TIME_LOADED_FOR_LANGUAGE_CHANGE = "LANGUAGE_CHANGE";
    public static final String PVR_MOUNTH_PATH = "PVR_MOUNTH_PATH";

    /** Start first time install if necessary */
    private void checkFirstTimeInstall() {
        Log.d(TAG,
                "//////////////////////////------------->>>>>>>CHECK FIRST TIME INSTALL ");
        if (runFirstTimeInstallState) {
            isInFirstTimeInstall = sharedPrefs.getBoolean(FIRST_TIME_INSTALL,
                    true);
            Log.d(TAG,
                    "//////////////////////////------------->>>>>>>IS IN FIRST TIME INSTALL "
                            + isInFirstTimeInstall);
            // sharedPrefs.edit().putBoolean(FIRST_TIME_INSTALL,
            // false).commit();
            if (isInFirstTimeInstall) {
                boolean isLanguageSetted = sharedPrefs.getBoolean(
                        IS_FIRST_TIME_LOADED_FOR_LANGUAGE_CHANGE, false);
                if (!isLanguageSetted) {
                    String language = null;
                    int languageIndex = 0;
                    try {
                        language = service.getSystemControl()
                                .getLanguageAndKeyboardControl()
                                .getAvailableLanguages().get(0);
                        languageIndex = service.getSystemControl()
                                .getLanguageAndKeyboardControl()
                                .getActiveLanguageIndex();
                        if (languageIndex > 0) {
                            service.getSystemControl()
                                    .getLanguageAndKeyboardControl()
                                    .setActiveLanguage(language);
                            sharedPrefs
                                    .edit()
                                    .putBoolean(
                                            IS_FIRST_TIME_LOADED_FOR_LANGUAGE_CHANGE,
                                            true).commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                firstTimeInstallLayout = (RelativeLayout) findViewById(R.id.firstTimeInstallLayout);
                firstTimeInstallLayout.setVisibility(View.VISIBLE);
                firstTimeInfoText = (A4TVTextView) findViewById(R.id.aTVTextViewFirstTimeInstallText);
                firstTimeInstallNextBtn = (A4TVButton) findViewById(R.id.aTVButtonFirstTimeInstallNextButton);
                firstTimeInstallNextBtn
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LanguageAndKeyboardDialog langAndKeyboard = dialogManager
                                        .getLanguageAndKeyboardDialog();
                                if (langAndKeyboard != null) {
                                    langAndKeyboard.show();
                                }
                                firstTimeInstallNextBtn
                                        .setVisibility(View.GONE);
                            }
                        });
                firstTimeInstallNextBtn.requestFocus();
                // ZORANA - Temp fix until nex button focus issue is resolved!
                firstTimeInstallNextBtn.setVisibility(View.GONE);
                LanguageAndKeyboardDialog langAndKeyboard = dialogManager
                        .getLanguageAndKeyboardDialog();
                if (langAndKeyboard != null) {
                    langAndKeyboard.show();
                }
            }
        }
    }

    public void tryConnectWithServer() {
        // disable early video for next start
        if (mIsEarlyVideo) {
            System.setProperty(EARLY_VIDEO_PROPERTY_NAME, "0");
            mIsEarlyVideo = false;
        } else {
            connectWithServer();
        }
    }

    /** Connect to proxy service server */
    public void connectWithServer() {
        Intent serviceIntent = new Intent("com.iwedia.PROXY_SERVICE");
        startService(serviceIntent);
        remoteServiceIntent = new Intent("com.iwedia.PROXY_SERVICE");
        conn = new BinderServiceConnection();
        boolean isBound = activity.bindService(remoteServiceIntent, conn,
                Context.BIND_AUTO_CREATE);
        Log.e(TAG, "Bind to service in onResume(), isBinded: " + isBound);
    }

    public void disconnectFromService() {
        if (conn != null) {
            Log.d(TAG, "disconnectFromService: unbind");
            unbindService(conn);
            conn = null;
            Log.d(TAG, "disconnectFromService: stop service");
            Intent serviceIntent = new Intent("com.iwedia.PROXY_SERVICE");
            stopService(serviceIntent);
        }
        Log.d(TAG, "disconnectFromService: done");
    }

    /** Binder connection */
    class BinderServiceConnection implements ServiceConnection {
        public static final String TAG = "BinderServiceConnection";

        public void onServiceConnected(ComponentName className,
                IBinder boundService) {
            service = IDTVManagerProxy.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected");
            initListeners();
            if (mIsEarlyVideo) {
                initialize();
            }
            try {
                // Initialize CallBacks
                mCallBackHandler.registerCallBacks(service);
                service.getStreamComponentControl().registerCallback(
                        streamCallback);
                epgClientId = service.getEpgControl().createEventList();
                Log.e("*****************************",
                        "before epg register callback: epgClientId:"
                                + epgClientId);
                service.getEpgControl().registerCallback(epgCallback,
                        epgClientId);
                Log.e("*****************************",
                        "after epg register callback: epgClientId:"
                                + epgClientId);
                service.getSetupControl().registerCallback(setupCallback);
                service.getHbbTvControl().setCallbackHbb(hbbCallback);
                ciCallbackController = new CICallbackController(
                        MainActivity.this);
                ciCallbackController
                        .setInfoDialog(MainActivity.this.dialogManager
                                .getCiInfoDialog());
                ciCallbackController
                        .setCamInfoDialog(MainActivity.this.dialogManager
                                .getCICamInfoDialog());
                service.getCIControl().registerCallback(
                        ciCallbackController.getCallback());
                service.getSystemControl().getNetworkControl()
                        .registerCallback(networkCallback);
                service.getReminderControl().registerCallback(reminderCallback);
                service.getMhegControl().registerCallback(mhegCallback);
                // Parental control callback
                service.getParentalControl().registerCallback(parentalCallback);
                // init hbb
                initializeHBB();
                // sending command to middleware to reparse AIT
                int command = 0;
                String param = "EXIT";
                try {
                    MainActivity.service.getHbbTvControl().notifyAppMngr(
                            command, param);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    MainActivity.service.getInputOutputControl()
                            .registerCallback(inputOutputCallback);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                /*
                 * String pvrMediaPath = sharedPrefs.getString(PVR_MOUNTH_PATH,
                 * "../.."); try { MainActivity.service.getPvrControl()
                 * .setMediaPath(pvrMediaPath); } catch (RemoteException e) {
                 * e.printStackTrace(); }
                 */
                /** Check if is in Store Mode, if it is clear all settings */
                isInStoreMode = sharedPrefs.getBoolean(STORE_MODE_START, false);
                if (isInStoreMode) {
                    try {
                        /* Save first time install value */
                        boolean isFirstTimeInstall = MainActivity.sharedPrefs
                                .getBoolean(MainActivity.FIRST_TIME_INSTALL,
                                        true);
                        Editor editor = MainActivity.activity
                                .getSharedPreferences("myPrefs",
                                        Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.commit();
                        MainActivity.service.getSetupControl()
                                .resetSettingsInStoreMode();
                        MainActivity.sharedPrefs
                                .edit()
                                .putBoolean(MainActivity.STORE_MODE_START, true)
                                .commit();
                        MainActivity.sharedPrefs
                                .edit()
                                .putBoolean(MainActivity.FIRST_TIME_INSTALL,
                                        isFirstTimeInstall).commit();
                        if (mPrimaryVideoView != null) {
                            if (!(mPrimaryVideoView.isPlaying())) {
                                mPrimaryVideoView.start();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                /** Check if to start first time install procedure */
                checkFirstTimeInstall();
                // Init app state
                initAppState();
                // /** Sync Channel index and get ActiveContent */
                Content lContent = getPageCurl().getChannelChangeHandler()
                        .syncChannelIndex();
                // // when application turn on check for service type
                // // just init service type
                checkServiceType = new CheckServiceType(MainActivity.this);
                if (!isInFirstTimeInstall && !mIsEarlyVideo) {
                    Log.v(TAG, "ZAP ON FIRST SERVICE");
                    // Check Service
                    // if (service.getContentListControl().startVideoPlayback())
                    // {
                    try {
                        CheckServiceType.checkService(lContent, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // }
                }
                /** Quick Fix, This Should Be In IwediaService */
                service.getContentListControl().setActiveFilter(FilterType.ALL);
                /*****************************************/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    /** Callback for parental control */
    private com.iwedia.comm.IParentalCallback parentalCallback = new com.iwedia.comm.IParentalCallback.Stub() {
        @Override
        public void channelLocked(boolean locked) throws RemoteException {
            Log.d("CHANNEL LOCKED", "ENTERED");
        }

        @Override
        public void ageLocked(boolean locked) throws RemoteException {
            Log.d("AGE LOCKED", "ENTERED " + locked);
            if (locked) {
                // Check application state
                // Stop video playback
                // service.getContentListControl().stopVideoPlayback();
                currentAudioTrackIndex = service.getVideoControl()
                        .getCurrentVideoTrackIndex();
                currentVideoTrackIndex = service.getAudioControl()
                        .getCurrentAudioTrackIndex();
                service.getAudioControl().deselectCurrentAudioTrack();
                service.getVideoControl().deselectCurrentVideoTrack();
                // TODO blank video screen check
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Create parental control dialog
                        if (ParentalGuidanceDialog.parentalAlertDialog == null) {
                            ParentalGuidanceDialog
                                    .initParentalControlDialog(MainActivity.this);
                        }
                        // Check if parental dialog is already on the screen
                        if (!ParentalGuidanceDialog.parentalAlertDialog
                                .isShowing()) {
                            // Show parental control password dialog
                            // There is no No Attempt period activated
                            PasswordSecurityDialog.wrongPasswordEntered(null,
                                    false);
                            if (PasswordSecurityDialog.waitFor10Minutes) {
                                A4TVToast toast = new A4TVToast(
                                        getApplicationContext());
                                toast.showToast(R.string.enter_password_no_more_attempts_active);
                            } else {
                                ParentalGuidanceDialog
                                        .showParentalControlAlertDialog(MainActivity.this);
                            }
                        }
                    }
                });
            } else {
                // /////////////////////////////////////////////
                // Hide parental if visible, because parental control is no
                // longer active
                // /////////////////////////////////////////////
                if (ParentalGuidanceDialog.parentalAlertDialog.isShowing()
                        || activity.getCheckServiceType().getParental()
                                .isShown()) {
                    // Start playback of secured channel
                    try {
                        // MainActivity.service.getContentListControl().startVideoPlayback();
                        MainActivity.service.getVideoControl()
                                .setCurrentVideoTrack(
                                        MainActivity.activity
                                                .getCurrentVideoTrackIndex());
                        MainActivity.service.getAudioControl()
                                .setCurrentAudioTrack(
                                        MainActivity.activity
                                                .getCurrentAudioTrackIndex());
                        // Hide parental control check service layer
                        activity.getCheckServiceType()
                                .hideParentalControlLayer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ParentalGuidanceDialog.parentalAlertDialog.cancel();
                }
            }
        }
    };
    public static IMhegCallback.Stub mhegCallback = new IMhegCallback.Stub() {
        @Override
        public void mhegKeyMaskEvent(int keyMask) throws RemoteException {
            Log.i(TAG, "mhegKeyMaskEvent: " + keyMask);
            mhegKeySet = keyMask;
        }
    };
    private IInputOutputCallback.Stub inputOutputCallback = new IInputOutputCallback.Stub() {
        @Override
        public void inputDeviceConnected(int deviceIndex)
                throws RemoteException {
            Log.d(TAG, "Input Connected " + deviceIndex);
            final int connectedDeviceIdx = deviceIndex;
            // TODO: Applied on main display only
            final int displayId = 0;
            try {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            contentListControl = MainActivity.service
                                    .getContentListControl();
                            contentListControl
                                    .setActiveFilter(FilterType.INPUTS);
                            /*
                             * Fixed problem with position after remove RF from
                             * InputContentList
                             */
                            if (connectedDeviceIdx > 0) {
                                inputContent = contentListControl
                                        .getContent(connectedDeviceIdx - 1);
                            } else {
                                inputContent = contentListControl
                                        .getContent(connectedDeviceIdx);
                            }
                            if (inputContent == null) {
                                Log.d(TAG,
                                        "Error while trying to switch to connected device: "
                                                + connectedDeviceIdx);
                                return;
                            }
                            Content activeContent = MainActivity.service
                                    .getContentListControl()
                                    .getActiveContent(0);
                            if ((activeContent.getIndex() >= 5)
                                    && (activeContent.getIndex() <= 8)
                                    && (activeContent.getIndex() < inputContent
                                            .getIndex())) { // HDMI lower
                                                            // priority input
                                String connectedInput = getString(R.string.connected_input);
                                Toast.makeText(
                                        activity,
                                        connectedInput + " "
                                                + inputContent.getName(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                boolean isDisabled = false;
                                try {
                                    isDisabled = MainActivity.service
                                            .getContentListControl()
                                            .getContentLockedStatus(
                                                    inputContent);
                                } catch (RemoteException e1) {
                                    e1.printStackTrace();
                                }
                                if (isDisabled == false) {
                                    String connectedInput = getString(R.string.connected_input);
                                    Toast.makeText(
                                            activity,
                                            connectedInput + " "
                                                    + inputContent.getName(),
                                            Toast.LENGTH_SHORT).show();
                                    Content secondaryContent = contentListControl
                                            .getActiveContent(1);
                                    if (secondaryContent != null) {
                                        if (!MainActivity.activity
                                                .getDualVideoManager()
                                                .checkSupportedScenario(
                                                        inputContent, 0)) {
                                            Log.d(TAG,
                                                    "Not supported dual scenario, close secondary display"
                                                            + inputContent
                                                                    .getName());
                                            contentListControl.stopContent(
                                                    secondaryContent, 1);
                                            mPrimaryVideoView.setScaling(0, 0,
                                                    1920, 1080);
                                            mSecondaryVideoView
                                                    .updateVisibility(View.INVISIBLE);
                                        }
                                    }
                                    contentListControl
                                            .setActiveFilter(FilterType.ALL);
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
                                    /* Stop active input */
                                    MainActivity.service
                                            .getContentListControl()
                                            .stopContent(activeContent, 0);
                                    ((MainActivity) activity)
                                            .setAnalogSignalLock(false);
                                    sourceSwichingProgressDialog.show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            err = 0;
                                            try {
                                                err = contentListControl
                                                        .goContent(
                                                                inputContent,
                                                                displayId);
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
                                                    sourceSwichingProgressDialog
                                                            .cancel();
                                                    if (getIsAnalogSignalLocked() == false) {
                                                        screenSaverDialog
                                                                .setScreenSaverCause(screenSaverDialog.NO_SIGNAL);
                                                        screenSaverDialog
                                                                .updateScreensaverTimer();
                                                        checkServiceType
                                                                .showNoSignalLayout();
                                                    } else {
                                                        screenSaverDialog
                                                                .setScreenSaverCause(screenSaverDialog.LIVE);
                                                        screenSaverDialog
                                                                .updateScreensaverTimer();
                                                    }
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    String connectedInputIsDisabled = getString(R.string.connected_input_is_disabled);
                                    Toast.makeText(
                                            activity,
                                            connectedInputIsDisabled + " "
                                                    + inputContent.getName(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void inputDeviceDisconnected(int deviceIndex)
                throws RemoteException {
            Log.d(TAG, "Input Disconnected " + deviceIndex);
            final int connectedDeviceIdx = deviceIndex;
            // TODO: Applied on main display only
            final int displayId = 0;
            try {
                MainActivity.this.runOnUiThread(new Runnable() {
                    private final int mDisplayId = 0;
                    private final int pipDisplayId = 1;

                    @Override
                    public void run() {
                        try {
                            contentListControl = MainActivity.service
                                    .getContentListControl();
                            contentListControl
                                    .setActiveFilter(FilterType.INPUTS);
                            /*
                             * Fixed problem with position after remove RF from
                             * InputContentList
                             */
                            if (connectedDeviceIdx > 0) {
                                inputContent = contentListControl
                                        .getContent(connectedDeviceIdx - 1);
                            } else {
                                inputContent = contentListControl
                                        .getContent(connectedDeviceIdx);
                            }
                            if (inputContent != null) {
                                String disconnectedInput = getString(R.string.disconnected_input);
                                Toast.makeText(
                                        activity,
                                        disconnectedInput + " "
                                                + inputContent.getName(),
                                        Toast.LENGTH_SHORT).show();
                                Content primaryContent = contentListControl
                                        .getActiveContent(mDisplayId);
                                Content secondaryContent = contentListControl
                                        .getActiveContent(pipDisplayId);
                                if ((primaryContent.getFilterType() == FilterType.INPUTS)
                                        && (primaryContent.getIndex() == inputContent
                                                .getIndex())) {
                                    Log.d(TAG,
                                            "Device from main display diconnected"
                                                    + inputContent.getName());
                                    prevContent = contentListControl
                                            .getPreviousContent();
                                    /* stop disconnected inputs */
                                    contentListControl.stopContent(
                                            inputContent, 0);
                                    ((MainActivity) activity)
                                            .setAnalogSignalLock(false);
                                    contentListControl
                                            .setActiveFilter(FilterType.ALL);
                                    if (prevContent == null) {
                                        Log.d(TAG, "No previous content");
                                        return;
                                    }
                                    Log.d(TAG,
                                            "Switching back to previous content: "
                                                    + prevContent.getName());
                                    if (secondaryContent != null) {
                                        if (!MainActivity.activity.dualVideoManager
                                                .checkSupportedScenario(
                                                        prevContent, 0)) {
                                            Log.d(TAG,
                                                    "Not supported dual scenario, close secondary display"
                                                            + inputContent
                                                                    .getName());
                                            contentListControl.stopContent(
                                                    secondaryContent, 1);
                                            mPrimaryVideoView.setScaling(0, 0,
                                                    1920, 1080);
                                            mSecondaryVideoView
                                                    .updateVisibility(View.INVISIBLE);
                                        }
                                    }
                                    /* Launch HbbTV Red Button if exists */
                                    if (0 == (MainActivity.getKeySet())) {
                                        int command = 0;
                                        String param = "EXIT";
                                        try {
                                            Log.d(TAG, "Show HbbTV graphic");
                                            activity.service.getHbbTvControl()
                                                    .notifyAppMngr(command,
                                                            param);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if ((prevContent.getFilterType() == FilterType.INPUTS)) {
                                        sourceSwichingProgressDialog.show();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    err = contentListControl
                                                            .goContent(
                                                                    prevContent,
                                                                    displayId);
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
                                                        sourceSwichingProgressDialog
                                                                .cancel();
                                                        if ((prevContent
                                                                .getFilterType() == FilterType.INPUTS)) {
                                                            if (displayId == 0) {
                                                                if (getIsAnalogSignalLocked() == false) {
                                                                    checkServiceType
                                                                            .showNoSignalLayout();
                                                                }
                                                            } else {
                                                                if (getIsAnalogSignalLocked() == false) {
                                                                    try {
                                                                        contentListControl
                                                                                .stopContent(
                                                                                        prevContent,
                                                                                        1);
                                                                        if (MainActivity.activity
                                                                                .getPrimaryMultimediaVideoView() == null)
                                                                            activity.getPrimaryVideoView()
                                                                                    .setScaling(
                                                                                            0,
                                                                                            0,
                                                                                            1920,
                                                                                            1080);
                                                                        activity.getSecondaryVideoView()
                                                                                .updateVisibility(
                                                                                        View.INVISIBLE);
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    A4TVToast toast = new A4TVToast(
                                                                            activity);
                                                                    toast.setDuration(A4TVToast.LENGTH_LONG);
                                                                    toast.showToast("No signal on secondary display unit");
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }).start();
                                    } else {
                                        contentListControl.goContent(
                                                prevContent, displayId);
                                    }
                                } else {
                                    if ((secondaryContent != null)
                                            && (secondaryContent
                                                    .getFilterType() == FilterType.INPUTS)
                                            && (secondaryContent.getIndex() == inputContent
                                                    .getIndex())) {
                                        Log.d(TAG,
                                                "Device from secondary display diconnected"
                                                        + inputContent
                                                                .getName());
                                        contentListControl.stopContent(
                                                secondaryContent, 1);
                                        mPrimaryVideoView.setScaling(0, 0,
                                                1920, 1080);
                                        mSecondaryVideoView
                                                .updateVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void inputDeviceVideoSignalChanged(int deviceIndex,
                boolean signalAvailable) throws RemoteException {
            videoSignalStatus = signalAvailable;
            try {
                MainActivity.this.runOnUiThread(new Runnable() {
                    // TODO: Applies on main display only
                    private final int mDisplayId = 0;

                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "VideoSignalChanged");
                            IContentListControl contentListControl = MainActivity.service
                                    .getContentListControl();
                            Content primaryContent = contentListControl
                                    .getActiveContent(0);
                            Content secondaryContent = contentListControl
                                    .getActiveContent(1);
                            int primaryContentFilterType = primaryContent
                                    .getFilterType();
                            if ((FilterType.INPUTS == primaryContent
                                    .getFilterType())
                                    || (SourceType.ANALOG == primaryContent
                                            .getSourceType())) {
                                if (videoSignalStatus == false) {
                                    checkServiceType.showNoSignalLayout();
                                } else {
                                    sourceSwichingProgressDialog.cancel();
                                    checkServiceType.hideNoSignalLayout();
                                }
                            }
                            if (secondaryContent != null) {
                                if ((FilterType.INPUTS == secondaryContent
                                        .getFilterType())
                                        || (SourceType.ANALOG == secondaryContent
                                                .getSourceType())) {
                                    sourceSwichingProgressDialog.cancel();
                                    if (videoSignalStatus == false) {
                                        contentListControl.stopContent(
                                                secondaryContent, 1);
                                        mPrimaryVideoView.setScaling(0, 0,
                                                1920, 1080);
                                        mSecondaryVideoView
                                                .updateVisibility(View.INVISIBLE);
                                        A4TVToast toast = new A4TVToast(
                                                activity);
                                        toast.setDuration(A4TVToast.LENGTH_LONG);
                                        toast.showToast("Signal from secondary display lost");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void inputDeviceAudioSignalChanged(int deviceIndex,
                boolean signalAvailable) throws RemoteException {
            final boolean audioSignalAvailable = signalAvailable;
            try {
                MainActivity.this.runOnUiThread(new Runnable() {
                    // TODO: Applies on main display only
                    private final int mDisplayId = 0;

                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "AudioSignalChanged");
                            if (audioSignalAvailable == false) {
                                A4TVToast toast = new A4TVToast(activity);
                                toast.setDuration(A4TVToast.LENGTH_LONG);
                                toast.showToast("AUDIO Signal Lost");
                                // TODO: Blank video
                            } else {
                                // TODO: Unblank video
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void inputDeviceStarted(int deviceIndex) throws RemoteException {
            // Activate main video view
            // playUriInVideoView("dvb://localhost:1");
        }

        @Override
        public void inputDeviceStopped(int deviceIndex) throws RemoteException {
            // TODO Auto-generated method stub
        }
    };

    /** Init fields for main menu */
    public void initMainMenu() {
        if (mainMenuHandler == null) {
            mainMenuHandler = new MainMenuHandlingClass(this);
            mainMenuHandler.init();
        }
    }

    /** Init epg */
    public void initEPG() {
        epgHandler = new EPGHandlingClass(this);
        epgHandler.init();
    }

    /**
     * Init fields for content list
     * 
     * @param type
     *        what filter is selected initially
     */
    public void initContentList() {
        contentListHandler = new ContentListHandler(this);
        // Init content list
        if (contentListHandler != null) {
            contentListHandler.init();
        }
    }

    /** Init fields for main menu */
    public void initPageCurl() {
        LinearLayout osdMain = (LinearLayout) activity
                .findViewById(R.id.mainOSD);
        osdMain.removeAllViewsInLayout();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View osd;
        int osdSelection = sharedPrefs.getInt(OSD_SELECTION, 0);
        switch (osdSelection) {
            case 0:
                // init curl effect
                osd = inflater.inflate(R.layout.info_banner_curl, osdMain);
                mPageCurl = new CurlHandler(this);
                mPageCurl.init(osd);
                mPageCurl.getChannelChangeHandler().syncChannelIndex();
                // Set curl animation time
                mPageCurl.setAnimationTimeChannelInfo(sharedPrefs.getInt(
                        CURL_ANIMATION_TIME_INFO, 5000));
                break;
            case 1:
                // init info banner
                osd = inflater.inflate(R.layout.osd_info_banner, osdMain);
                mPageCurl = new InfoBannerHandler(this);
                mPageCurl.init(osd);
                mPageCurl.getChannelChangeHandler().syncChannelIndex();
                break;
            case 2:
                // none
                mPageCurl = new NoneBannerHandler(this);
                mPageCurl.init(null);
                mPageCurl.getChannelChangeHandler().syncChannelIndex();
                break;
        }
    }

    /** Init widget handler class */
    public void initWidgetsHandler() {
        widgetsHandler = new WidgetsHandler(this);
        widgetsHandler.init();
    }

    /** Init multimedia handler class */
    public void initMultimediaHandler() {
        multimediaHandler = new MultimediaHandler(this);
        multimediaHandler.init();
    }

    /** Init listeners in application */
    public void initListeners() {
        /** Init key listener */
        mainKeyListener = new MainKeyListener(this);
    }

    /** Init screen saver */
    public void initScreenSaver() {
        screenSaverDialog = new ScreenSaverDialog(activity);
        /* initial start screen saver timer */
        screenSaverDialog.setScreenSaverCause(screenSaverDialog.LIVE);
        screenSaverDialog.startScreensaverTimer();
    }

    private void initializeTeletext() {
        mTeletextDialogView = new TeletextDialogView(this);
    }

    public void initializeSubtitle() {
        mSubtitleDialogView = new SubtitleDialogView(this);
    }

    public void initializeMHEG() {
        mMhegDialogView = new MhegDialogView(this);
    }

    public void initializeHBB() {
        // Show widgets
        widgetsHandler.showWidgets();
    }

    /** Init app state */
    private void initAppState() {
        // /////////////////////////////////
        // Alert dialog and main menu icons bug fix
        // /////////////////////////////////
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainKeyListener.enableKeyCodeBack = true;
                MainKeyListener.enableKeyCodeMenu = true;
            }
        }, 1000);
        openSubmenuOnStart();
    }

    /** Open opened submenu on activity recreate */
    private void openSubmenuOnStart() {
        /**
         * If it is not in first time install
         **/
        if (!isInFirstTimeInstall) {
            // restore previous state of menu when user perform system
            // settings
            if (!stopVideoOnPauseAndReturnMenuToUser) {
                Log.d(TAG, "OPEN SUBMENU ON START: "
                        + MainMenuContent.currentState + ", "
                        + A4TVGalleryOnClickListener.lastClickedIndexPosition);
                mainMenuHandler.showSpecificSubmenu(
                        MainMenuContent.currentState,
                        A4TVGalleryOnClickListener.lastClickedIndexPosition);
            }
            /** Check if is in Service Mode, if it is show service mode menu */
            isInServiceMode = sharedPrefs.getBoolean(SERVICE_MODE_START, false);
            if (isInServiceMode) {
                try {
                    ServiceModeDialog serviceModeDialog = dialogManager
                            .getServiceModeDialog();
                    if (serviceModeDialog != null) {
                        serviceModeDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * If it is in first time install
         **/
        else {
            switch (firstTimeInstallActiveDialog) {
            // language
                case 1: {
                    LanguageAndKeyboardDialog langAndKeyboard = dialogManager
                            .getLanguageAndKeyboardDialog();
                    if (langAndKeyboard != null) {
                        langAndKeyboard.show();
                    }
                    MainKeyListener.setAppState(MainKeyListener.MAIN_MENU);
                    MainActivity.activity.getFirstTimeInstallNextBtn()
                            .setVisibility(View.INVISIBLE);
                    break;
                }
                // picture settings
                case 2: {
                    PictureSettingsDialog picSettingsDialog = dialogManager
                            .getPictureSettingsDialog();
                    if (picSettingsDialog != null) {
                        picSettingsDialog.show();
                    }
                    MainKeyListener.setAppState(MainKeyListener.MAIN_MENU);
                    MainActivity.activity.getFirstTimeInstallNextBtn()
                            .setVisibility(View.INVISIBLE);
                    break;
                }
                // accounts
                case 3: {
                    AccountsAndSyncDialog accSyncDialog = dialogManager
                            .getAccountsAndSyncDialog();
                    if (accSyncDialog != null) {
                        accSyncDialog.show();
                    }
                    MainKeyListener.setAppState(MainKeyListener.MAIN_MENU);
                    MainActivity.activity.getFirstTimeInstallNextBtn()
                            .setVisibility(View.INVISIBLE);
                    break;
                }
                // network
                case 4: {
                    NetworkSettingsDialog netSettingsDialog = dialogManager
                            .getNetworkSettingsDialog();
                    if (netSettingsDialog != null) {
                        netSettingsDialog.show();
                    }
                    MainKeyListener.setAppState(MainKeyListener.MAIN_MENU);
                    MainActivity.activity.getFirstTimeInstallNextBtn()
                            .setVisibility(View.INVISIBLE);
                    break;
                }
                // channel install
                case 5: {
                    ChannelInstallationDialog channelInstallDialog = dialogManager
                            .getChannelInstallationDialog();
                    if (channelInstallDialog != null) {
                        channelInstallDialog.show();
                    }
                    MainKeyListener.setAppState(MainKeyListener.MAIN_MENU);
                    MainActivity.activity.getFirstTimeInstallNextBtn()
                            .setVisibility(View.INVISIBLE);
                    break;
                }
                default:
                    break;
            }
        }
        stopVideoOnPauseAndReturnMenuToUser = true;
    }

    private static IEpgCallback.Stub epgCallback = new IEpgCallback.Stub() {
        @Override
        public void pfAcquisitionFinished(int filterID, int serviceIndex)
                throws RemoteException {
            // TODO Auto-generated method stub
        }

        @Override
        public void pfEventChanged(int filterID, final int serviceIndex)
                throws RemoteException {
            try {
                if (mPageCurl.isFlagChannelInfo()
                        || mPageCurl.getCurrentState() == STATE_CHANNEL_INFO) {
                    activity.getPageCurl().setUpNewChannelInfo(serviceIndex);
                }
                if (serviceIndex == activity.getPageCurl()
                        .getChannelInfoIndex()) {
                    EPGScheduleDialog epgScheduleDialog = ((MainActivity) activity)
                            .getDialogManager().getEpgScheduleDialog();
                    if (epgScheduleDialog != null)
                        if (epgScheduleDialog.isShowing()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainKeyListener
                                            .refreshExtendedInfo(serviceIndex);
                                }
                            });
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void scAcquisitionFinished(int filterID, int serviceIndex)
                throws RemoteException {
            // TODO Auto-generated method stub
        }

        @Override
        public void scEventChanged(int filterID, int serviceIndex)
                throws RemoteException {
            // TODO Auto-generated method stub
        }
    };
    private IStreamComponentCallback.Stub streamCallback = new IStreamComponentCallback.Stub() {
        @Override
        public void componentChanged(int routID) throws RemoteException {
            /*
             * Log.i("ActionCallback", "Component[" + componentType +
             * "] available"); switch(componentType.getValue()) { case 2: //
             * subtitle runOnUiThread(new Runnable() {
             * @Override public void run() { if(mSubtitleDialogView != null) {
             * mSubtitleDialogView.available(); } } }); break; case 4: // mheg
             * runOnUiThread(new Runnable() {
             * @Override public void run() { if(mMhegDialogView != null) {
             * mMhegDialogView.show(); } CheckServiceType.showMhegData(); } });
             * break; default: break; }
             */
        }
    };
    private static ISetupCallback.Stub setupCallback = new ISetupCallback.Stub() {
        @Override
        public void offTimerChanged() throws RemoteException {
            try {
                MainActivity.service.getSetupControl().rebootTV();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /** HbbTv functions */
    private static IHbbTvCallback hbbCallback = new IHbbTvCallback.Stub() {
        Timer trackTimeTimer;

        @Override
        public void createApplication(String uri) throws RemoteException {
            pomStr = uri;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, " Thread!" + pomStr); // webDialog.show();
                    if (webDialog != null) {
                        // Log.i(TAG, "webdialog ok");
                    } else {
                        // Log.i(TAG, "webdialog ok");
                    }
                    if (webDialog != null) {
                        webDialog.getHbbTVView().loadUrl(pomStr);
                        webDialog.getHbbTVView().requestFocus();
                    }
                }
            });
        }

        @Override
        public void destroyApplication() throws RemoteException {
            Log.w(TAG, "cmd: CMD_DESTROY_APPLICATION");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, " Thread!");
                    if (webDialog != null) {
                        Log.i(TAG, "webdialog ok");
                    } else {
                        Log.i(TAG, "webdialog ok");
                    }
                    if (webDialog != null) {
                        webDialog.getHbbTVView().loadUrl("");
                        webDialog.getHbbTVView().requestFocus();
                    }
                }
            });
        }

        @Override
        public void hideApplication() throws RemoteException {
            boolean isHbbTVEnabled = MainActivity.service.getHbbTvControl()
                    .isHbbEnabled();
            if (isHbbTVEnabled) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() { // webDialog.cancel();
                        if (webDialog == null
                                || webDialog.getHbbTVView() == null) {
                            return;
                        }
                        webDialog.getHbbTVView().setAlpha((float) 0.00);
                    }
                });
            }
        }

        @Override
        public void setKeyMask(int keyMask) throws RemoteException {
            setKeySet(keyMask);
            if (keyMask > 1) {
                // Get active content object from secondary display
                int displayId = 1;
                Content activeContent;
                try {
                    activeContent = MainActivity.service
                            .getContentListControl()
                            .getActiveContent(displayId);
                    /* stop secondary display playback */
                    if (activeContent != null
                            && MainActivity.activity.getSecondaryVideoView()
                                    .getPlayMode() == A4TVVideoView.PIP_DISPLAY_MODE) {
                        Log.d(TAG,
                                "PiP Stopping content: "
                                        + activeContent.toString());
                        if (activeContent.getFilterType() == FilterType.PVR_RECORDED) {
                            MainActivity.service.getPvrControl().stopPlayback();
                        } else {
                            ((MainActivity) activity).service
                                    .getContentListControl().stopContent(
                                            activeContent, displayId);
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.activity.getSecondaryVideoView()
                                        .updateVisibility(View.INVISIBLE);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (MainActivity.activity.getPrimaryMultimediaVideoView() != null) {
                    if (MainActivity.activity.getRendererController()
                            .getmRendererState() != 0) {
                        MainActivity.activity.getRendererController().stop();
                    } else {
                        MainActivity.activity
                                .stopMultimediaVideo(MainActivity.MULTIMEDIA_PIP);
                    }
                }
            }
        }

        @Override
        public void showApplication() throws RemoteException {
            boolean isHbbTVEnabled = MainActivity.service.getHbbTvControl()
                    .isHbbEnabled();
            if (isHbbTVEnabled) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.w(TAG, "SHOWING"); //
                        webDialog.show(); // remove this?
                        webDialog.getHbbTVView().requestFocus();
                        webDialog.getHbbTVView().setAlpha((float) 1.0);
                    }
                });
            }
        }
    };
    public IReminderCallback reminderCallback = new IReminderCallback.Stub() {
        @Override
        public void reminderTrigger(final ReminderEventTrigger eventTrigger)
                throws RemoteException {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*
                     * try { if(1 < (MainActivity.getKeySet())) { int command =
                     * 0; String param = "EXIT";
                     * MainActivity.service.getHbbTvControl
                     * ().notifyAppMngr(command, param); } }
                     * catch(RemoteException e) { e.printStackTrace(); }
                     */
                    try {
                        showReminderEventDialog(eventTrigger.getServiceIndex());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void reminderAdd(final ReminderEventAdd eventAdd)
                throws RemoteException {
        }

        public void reminderRemove(final ReminderEventRemove eventRemove)
                throws RemoteException {
        }
    };
    public INetworkCallback networkCallback = new INetworkCallback.Stub() {
        public void testFinished() throws RemoteException {
            MainActivity.this.dialogManager.getNetworkTestDialog()
                    .testFinished();
        }

        public void progressChanged(int value) throws RemoteException {
            MainActivity.this.dialogManager.getNetworkTestDialog()
                    .progressChanged(value);
        }

        public void networkTypeChanged(int type) throws RemoteException {
            MainActivity.this.dialogManager.getNetworkTestDialog()
                    .networkTypeChanged(type);
        }

        public void downloadSpeed(double speed) throws RemoteException {
            MainActivity.this.dialogManager.getNetworkTestDialog()
                    .downloadSpeed(speed);
        }

        public void connectionTimeChanged(int time) throws RemoteException {
            MainActivity.this.dialogManager.getNetworkTestDialog()
                    .connectionTimeChanged(time);
        }

        @Override
        public void wirelessNetworksChanged(int state) throws RemoteException {
            if (activityActive) {
                try {
                    NetworkWirelessWPSConfigDialog netWirelessWPSDialog = MainActivity.this.dialogManager
                            .getNetworkWirelessWPSConfigDialog();
                    if (netWirelessWPSDialog != null) {
                        netWirelessWPSDialog.wirelessNetworksChanged(state);
                    }
                    NetworkWirelessFindAPDialog netWirelessFindAPDialog = MainActivity.this.dialogManager
                            .getNetworkWirelessFindAPDialog();
                    if (netWirelessFindAPDialog != null) {
                        netWirelessFindAPDialog.wirelessNetworksChanged(state);
                    }
                    NetworkWirelessAddHiddenNetworkDialog netWirelessAddNetDialog = MainActivity.this.dialogManager
                            .getNetworkWirelessAddHiddenNetworkDialog();
                    if (netWirelessAddNetDialog != null) {
                        netWirelessAddNetDialog.wirelessNetworksChanged(state);
                    }
                    NetworkWirelessFindWPSDialog netWirelessFindWPSDialog = MainActivity.this.dialogManager
                            .getNetworkWirelessFindWPSDialog();
                    if (netWirelessFindWPSDialog != null) {
                        netWirelessFindWPSDialog.wirelessNetworksChanged(state);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void wpsPinObtained(String pin) throws RemoteException {
            NetworkWirelessWPSConfigDialog netWirelessWPSDialog = MainActivity.this.dialogManager
                    .getNetworkWirelessWPSConfigDialog();
            if (netWirelessWPSDialog != null) {
                netWirelessWPSDialog.wpsPinObtained(pin);
            }
        }

        @Override
        public void wpsStateChanged(int state) throws RemoteException {
            NetworkWirelessWPSConfigDialog netWirelessWPSDialog = MainActivity.this.dialogManager
                    .getNetworkWirelessWPSConfigDialog();
            if (netWirelessWPSDialog != null) {
                netWirelessWPSDialog.wpsStateChanged(state);
            }
            NetworkWirelessFindWPSDialog netWirelessFindWPSDialog = MainActivity.this.dialogManager
                    .getNetworkWirelessFindWPSDialog();
            if (netWirelessFindWPSDialog != null) {
                netWirelessFindWPSDialog.wpsStateChanged(state);
            }
        }
    };

    public boolean isFullHD() {
        if (screenWidth == SCREEN_WIDTH_720P
                || screenHeight == SCREEN_WIDTH_720P) {
            return false;
        }
        return true;
    }

    /** Hide opened dialog in onPause */
    private void hideDialogs() {
        if (mainMenuHandler != null) {
            if (mainMenuHandler.getMainMenuDialog().isShowing()) {
                mainMenuHandler.getMainMenuDialog().cancel();
            }
        }
        if (contentListHandler != null) {
            if (contentListHandler.getContentListDialog().isShowing()) {
                contentListHandler.getContentListDialog().cancel();
            }
        }
        if (multimediaHandler != null) {
            if (multimediaHandler.getMultimediaDialog().isShowing()) {
                multimediaHandler.getMultimediaDialog().cancel();
            }
        }
        dialogManager.hideAllDialogs();
        dialogManager.removeDialogsOnPause();
        mainMenuHandler = null;
        MainKeyListener.setAppState(MainKeyListener.CLEAN_SCREEN);
    }

    // ///////////////////////////////////////////////////////////
    // Getters and Setters
    // ///////////////////////////////////////////////////////////
    /** Get main menu handler class */
    public MainMenuHandlingClass getMainMenuHandler() {
        return mainMenuHandler;
    }

    /** Get content list handler class */
    public ContentListHandler getContentListHandler() {
        return contentListHandler;
    }

    /** Get curl hander */
    public IOSDHandler getPageCurl() {
        return mPageCurl;
    }

    /** Get widget handler object */
    public WidgetsHandler getWidgetsHandler() {
        return widgetsHandler;
    }

    /** Get multimedia handler class */
    public MultimediaHandler getMultimediaHandler() {
        return multimediaHandler;
    }

    /** Get binder connection of service */
    public BinderServiceConnection getConn() {
        return conn;
    }

    public DialogManager getDialogManager() {
        return dialogManager;
    }

    public EPGHandlingClass getEpgHandler() {
        return epgHandler;
    }

    public MainKeyListener getMainKeyListener() {
        return mainKeyListener;
    }

    public ScreenSaverDialog getScreenSaverDialog() {
        return screenSaverDialog;
    }

    public static int getKeySet() {
        Log.d(TAG, "GetKeySet returns: " + keySet);
        return keySet;
    }

    public MediaController getMediaController() {
        return mediaController;
    }

    public A4TVTextView getFirstTimeInfoText() {
        return firstTimeInfoText;
    }

    public RelativeLayout getFirstTimeInstallLayout() {
        return firstTimeInstallLayout;
    }

    public static SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public A4TVButton getFirstTimeInstallNextBtn() {
        return firstTimeInstallNextBtn;
    }

    public static void setKeySet(int keySet) {
        if ((MainActivity.keySet <= 1) & (keySet > 1)) {
            if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                Message msg = new Message();
                msg.what = HBBTV_ON_TOP;
                hbbHandler.sendMessage(msg);
            }
        } else if ((keySet <= 1) & (MainActivity.keySet > 1)) {
            if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                Message msg = new Message();
                msg.what = WIDGETS_ON_TOP;
                hbbHandler.sendMessage(msg);
            }
        }
        MainActivity.keySet = keySet;
    }

    public static int getMhegKeySet() {
        return mhegKeySet;
    }

    public static void setMhegKeySet(int mhegKeySet) {
        MainActivity.mhegKeySet = mhegKeySet;
    }

    public A4TVVideoView getPrimaryVideoView() {
        return mPrimaryVideoView;
    }

    public A4TVVideoView getSecondaryVideoView() {
        return mSecondaryVideoView;
    }

    public boolean isHbbTVInHTTPPlaybackMode() {
        boolean isHbbTVAppActive = 1 < getKeySet() ? true : false;
        Log.d(TAG, "isHbbTVInHTTPPlaybackMode: active " + isHbbTVAppActive);
        return isHbbTVAppActive;
    }

    public VideoView getExternalVideoView() {
        return mExternalView;
    }

    public VideoView getPiPVideoView() {
        return mPiPVideoView;
    }

    public int getSecondaryVideoViewState() {
        int retValue = PIP_MODE;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSecondaryVideoView
                .getLayoutParams();
        if ((960 == params.width) && (1080 == params.height)) {
            retValue = PAP_MODE;
        }
        return retValue;
    }

    public MainMenuContent getMainMenuContent() {
        return mMainMenuContent;
    }

    public TeletextDialogView getTeletextDialogView() {
        return mTeletextDialogView;
    }

    public SubtitleDialogView getSubtitleDialogView() {
        return mSubtitleDialogView;
    }

    public boolean dualVideoActionHandler(int command, int serviceIndex) {
        Content content;
        int servicesNumber = 0;
        try {
            IContentListControl contentListControl = MainActivity.service
                    .getContentListControl();
            if (null != contentListControl.getActiveContent(1)) {
                int index = contentListControl.getActiveContent(0)
                        .getIndexInMasterList();
                A4TVToast toast = new A4TVToast(MainActivity.activity);
                servicesNumber += service.getServiceControl()
                        .getServiceListCount(ServiceListIndex.MASTER_LIST);
                Content secContent = contentListControl.getActiveContent(1);
                int secondaryContentFilterType = secContent.getFilterType();
                switch (command) {
                    case CHANNEL_UP: {
                        Log.i(TAG, "CHANNEL_UP pressed");
                        if (servicesNumber <= (index + 1)) {
                            index = 0;
                        }
                        index++;
                        content = contentListControl.getContent(index);
                        int contentFilterType = content.getFilterType();
                        if ((((SourceType.ANALOG == content.getSourceType()) || (FilterType.INPUTS == contentFilterType)) && ((SourceType.ANALOG == secContent
                                .getSourceType()) || (FilterType.INPUTS == secondaryContentFilterType)))) {
                            Log.i(TAG, "MPQ is not supporting such scenario!!");
                            toast.showToast(R.string.not_supported_dual_scenario);
                            return false;
                        }
                    }
                        break;
                    case CHANNEL_DOWN: {
                        Log.i(TAG, "CHANNEL_DOWN pressed");
                        if (0 > (index - 1)) {
                            index = servicesNumber;
                        }
                        index--;
                        content = contentListControl.getContent(index);
                        int contentFilterType = content.getFilterType();
                        if ((((SourceType.ANALOG == content.getSourceType()) || (FilterType.INPUTS == contentFilterType)) && ((SourceType.ANALOG == secContent
                                .getSourceType()) || (FilterType.INPUTS == secondaryContentFilterType)))) {
                            Log.i(TAG, "MPQ is not supporting such scenario!!");
                            toast.showToast(R.string.not_supported_dual_scenario);
                            return false;
                        }
                    }
                        break;
                    case CHANNEL_TOGGLE_PREVIOUS: {
                        Log.i(TAG, "CHANNEL_TOGGLE_PREVIOUS pressed");
                        // Get previous active content object
                        Content previousContent = contentListControl
                                .getPreviousContent();
                        int previousContentFilterType = previousContent
                                .getFilterType();
                        if ((((SourceType.ANALOG == previousContent
                                .getSourceType()) || (FilterType.INPUTS == previousContentFilterType)) && ((SourceType.ANALOG == secContent
                                .getSourceType()) || (FilterType.INPUTS == secondaryContentFilterType)))) {
                            Log.i(TAG, "MPQ is not supporting such scenario!!");
                            toast.showToast(R.string.not_supported_dual_scenario);
                            return false;
                        }
                    }
                        break;
                    case CHANNEL_GO_TO_INDEX: {
                        Log.i(TAG, "CHANNEL_GO_TO_INDEX pressed");
                        content = contentListControl
                                .getContent(serviceIndex - 1);
                        int contentFilterType = content.getFilterType();
                        if ((((SourceType.ANALOG == content.getSourceType()) || (FilterType.INPUTS == contentFilterType)) && ((SourceType.ANALOG == secContent
                                .getSourceType()) || (FilterType.INPUTS == secondaryContentFilterType)))) {
                            Log.i(TAG, "MPQ is not supporting such scenario!!");
                            toast.showToast(R.string.not_supported_dual_scenario);
                            return false;
                        }
                    }
                        break;
                    default:
                        break;
                }
            } else {
                Log.i(TAG, "No active content in secondary display unit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
     * public MrvlTvView getLiveVideoView() { return mPrimaryVideoView; }
     */
    public PiPView getPiPView() {
        return mPiPView;
    }

    public PiPController getPiPController() {
        return pipController;
    }

    public RendererController getRendererController() {
        return rendererController;
    }

    public int getCurrentVideoTrackIndex() {
        return currentVideoTrackIndex;
    }

    public int getCurrentAudioTrackIndex() {
        return currentAudioTrackIndex;
    }

    public CheckServiceType getCheckServiceType() {
        return checkServiceType;
    }

    public boolean getIsAnalogSignalLocked() {
        return videoSignalStatus;
    }

    public void setAnalogSignalLock(boolean analogSignalStatus) {
        videoSignalStatus = analogSignalStatus;
    }

    public A4TVMultimediaVideoView getPrimaryMultimediaVideoView() {
        return this.mPrimaryMultimediaVideoView;
    }

    public int getMultimediaMode() {
        return MainActivity.mMultimediaMode;
    }

    public DualVideoManager getDualVideoManager() {
        return dualVideoManager;
    }

    public static CICallbackController getCiCallbackController() {
        return ciCallbackController;
    }

    public CallBackHandler getCallBackHandler() {
        return mCallBackHandler;
    }

    public A4TVVideoView attachSecondaryA4TVVideoView() {
        Log.d(TAG, "Create SECONDARY VIDEO VIEW");
        mSecondaryVideoHolder = (RelativeLayout) findViewById(R.id.secondaryVideoHolder);
        mSecondaryVideoView = new A4TVVideoView(this, null, 0);
        mSecondaryVideoHolder.addView(mSecondaryVideoView);
        mSecondaryVideoView.setZOrderOnTop(true);
        mSecondaryVideoView.updateVisibility(View.INVISIBLE);
        mSecondaryVideoView.getParent().requestLayout();
        mSecondaryVideoView.setVideoURI(Uri.parse("tv://tv:0?view=1"));
        Log.d(TAG, "Create SECONDARY VIDEO VIEW DONE");
        mSecondaryVideoView
                .setOnErrorListener(new A4TVVideoViewOnErrorListener());
        return mSecondaryVideoView;
    }

    public VideoView playMultimediaVideo(final String uriString, final int mode) {
        if (mPrimaryMultimediaVideoView != null) {
            Log.e(TAG, "Play failed, DLNA is already playing!");
            return mPrimaryMultimediaVideoView;
        }
        mMultimediaVideoReady = false;
        Log.e(TAG, "Multimedia playback uriString: " + uriString);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mode == MULTIMEDIA_PIP) {
                    Log.e(TAG, "mode == MULTIMEDIA_PIP");
                    mPrimaryMultimediaVideoView = new A4TVMultimediaVideoView(
                            activity);
                    mPrimaryMultimediaVideoView.setZOrderOnTop(true);
                    mSecondaryVideoHolder.removeView(mSecondaryVideoView);
                    mSecondaryVideoHolder.addView(mPrimaryMultimediaVideoView);
                    mPrimaryMultimediaVideoView
                            .setOnPreparedListener(new MultimediaVideoViewOnPreparedListener(
                                    false));
                    mPrimaryMultimediaVideoView
                            .setOnCompletionListener(new MultimediaVideoViewOnCompletionListener(
                                    mode));
                    mPrimaryMultimediaVideoView.gotoPIP();
                    mPrimaryMultimediaVideoView.setVideoURI(Uri
                            .parse(uriString));
                    try {
                        MainActivity.service.getSystemControl()
                                .getSoundControl().muteAudio(true);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mMultimediaVideoReady = true;
                    MainActivity.mMultimediaMode = mode;
                } else if (mode == MULTIMEDIA_PAP) {
                    Log.e(TAG, "mode == MULTIMEDIA_PAP");
                    MainActivity.activity.getPrimaryVideoView().setScaling(0,
                            0, 960, 1080);
                    mPrimaryMultimediaVideoView = new A4TVMultimediaVideoView(
                            activity);
                    mSecondaryVideoHolder.removeView(mSecondaryVideoView);
                    mSecondaryVideoHolder.addView(mPrimaryMultimediaVideoView);
                    mPrimaryMultimediaVideoView
                            .setOnPreparedListener(new MultimediaVideoViewOnPreparedListener(
                                    false));
                    mPrimaryMultimediaVideoView
                            .setOnCompletionListener(new MultimediaVideoViewOnCompletionListener(
                                    mode));
                    mPrimaryMultimediaVideoView
                            .gotoPaP(SECONDARY_DISPLAY_UNIT_ID);
                    mPrimaryMultimediaVideoView.setVideoURI(Uri
                            .parse(uriString));
                    try {
                        MainActivity.service.getSystemControl()
                                .getSoundControl().muteAudio(true);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mMultimediaVideoReady = true;
                    MainActivity.mMultimediaMode = mode;
                } else {
                    Log.e(TAG, "mode == else");
                    /**
                     * Hide primary display (DLNA Video View is below) and stop
                     * playback
                     */
                    mPrimaryVideoView.stopPlayback();
                    mPrimaryVideoView.setVisibility(View.INVISIBLE);
                    mPrimaryMultimediaVideoView = new A4TVMultimediaVideoView(
                            activity);
                    mPrimaryVideoHolder.addView(mPrimaryMultimediaVideoView);
                    mPrimaryMultimediaVideoView
                            .setOnPreparedListener(new MultimediaVideoViewOnPreparedListener(
                                    false));
                    mPrimaryMultimediaVideoView
                            .setOnCompletionListener(new MultimediaVideoViewOnCompletionListener(
                                    0));
                    mPrimaryMultimediaVideoView.gotoFullScreen();
                    mPrimaryMultimediaVideoView.setVideoURI(Uri
                            .parse(uriString));
                    mMultimediaVideoReady = true;
                    MainActivity.mMultimediaMode = mode;
                }
            }
        });
        while (mMultimediaVideoReady == false) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mPrimaryMultimediaVideoView;
    }

    public boolean stopMultimediaVideo(final int mode) {
        if (mPrimaryMultimediaVideoView == null) {
            Log.e(TAG, "Stop failed, multimedia is not playing!");
            return false;
        }
        Log.e(TAG, "Stop Multimedia playback");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mode == MULTIMEDIA_PIP) {
                    try {
                        MainActivity.service.getSystemControl()
                                .getSoundControl().muteAudio(false);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mPrimaryMultimediaVideoView.stopPlayback();
                    mPrimaryMultimediaVideoView.setVisibility(View.GONE);
                    mSecondaryVideoHolder
                            .removeView(mPrimaryMultimediaVideoView);
                    attachSecondaryA4TVVideoView();
                    mPrimaryMultimediaVideoView = null;
                } else if (mode == MULTIMEDIA_PAP) {
                    try {
                        MainActivity.service.getSystemControl()
                                .getSoundControl().muteAudio(false);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    mPrimaryMultimediaVideoView.stopPlayback();
                    mPrimaryMultimediaVideoView.setVisibility(View.GONE);
                    mSecondaryVideoHolder
                            .removeView(mPrimaryMultimediaVideoView);
                    attachSecondaryA4TVVideoView();
                    mPrimaryMultimediaVideoView = null;
                    activity.getPrimaryVideoView().setScaling(0, 0, 1920, 1080);
                } else {
                    /** Stop DLNA playback */
                    mPrimaryMultimediaVideoView.stopPlayback();
                    mPrimaryMultimediaVideoView.setVisibility(View.GONE);
                    mPrimaryVideoHolder.removeView(mPrimaryMultimediaVideoView);
                    mPrimaryMultimediaVideoView = null;
                    /** Start live playback */
                    mPrimaryVideoView.setVisibility(View.VISIBLE);
                    mPrimaryVideoView.start();
                    Content activeContent;
                    try {
                        activeContent = MainActivity.service
                                .getContentListControl().getActiveContent(0);
                        if ((activeContent != null)
                                && (activeContent.getFilterType() == FilterType.INPUTS)) {
                            MainActivity.service.getContentListControl()
                                    .goContent(activeContent, 0);
                        } else {
                            MainActivity.service.getInputOutputControl()
                                    .ioDeviceStartDVB();
                            if ((activeContent != null)
                                    && (activeContent.getSourceType() != SourceType.ANALOG)) {
                                /* Launch HbbTV Red Button if exists */
                                if (0 == (MainActivity.getKeySet())) {
                                    int command = 0;
                                    String param = "EXIT";
                                    try {
                                        Log.d(TAG, "Show HbbTV graphic");
                                        MainActivity.activity.service
                                                .getHbbTvControl()
                                                .notifyAppMngr(command, param);
                                        service.getContentListControl()
                                                .startVideoPlayback();
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    MultimediaHandler.returnMultimediaToPreviousState();
                }
            }
        });
        return true;
    }

    public void pauseVideoViews() {
        if (mPrimaryVideoView != null) {
            if (mPrimaryVideoView.isPlaying()) {
                mPrimaryVideoView.pause();
            }
        }
        if (mSecondaryVideoView != null) {
            if (mSecondaryVideoView.isPlaying()) {
                mSecondaryVideoView.pause();
            }
        }
    }

    public void releaseVideoViews() {
        if (mPrimaryVideoView != null) {
            mPrimaryVideoView.stopPlayback();
            mPrimaryVideoView = null;
            mPrimaryVideoHolder.removeAllViews();
        }
        if (mPrimaryMultimediaVideoView != null) {
            mPrimaryMultimediaVideoView.stopPlayback();
            mPrimaryMultimediaVideoView = null;
        }
        if (mSecondaryVideoView != null) {
            mSecondaryVideoView.stopPlayback();
            mSecondaryVideoView = null;
            mSecondaryVideoHolder.removeAllViews();
        }
    }

    /*
     * method to be invoked to register the receiver
     */
    private void registerReceivers() {
        registerReceiver(mConnReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Shows alert dialog when reminder event is triggered
     */
    private void showReminderEventDialog(final int serviceIndex) {
        final A4TVAlertDialog alert = new A4TVAlertDialog(activity);
        final int mDisplayId = 0;
        alert.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                switch (keyCode) {
                // ///////////////////////////////////////////////////////////////////
                // Disable Volume keys when retry scan dialog is visible
                // ///////////////////////////////////////////////////////////////////
                // case KeyEvent.KEYCODE_F6:
                    case KeyEvent.KEYCODE_VOLUME_UP:
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                    case KeyEvent.KEYCODE_MUTE: {
                        return true;
                    }
                    default:
                        break;
                }
                return false;
            }
        });
        alert.setTitleOfAlertDialog(R.string.reminder_event_triggered);
        alert.setPositiveButton(R.string.button_text_yes,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Content content = null;
                        Log.d(TAG, "INDEX OF SERVICE FROM REMINDER: "
                                + serviceIndex);
                        try {
                            content = MainActivity.service
                                    .getContentListControl()
                                    .getContentByIndexInMasterList(serviceIndex);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (content != null) {
                            MainActivity.activity
                                    .getPageCurl()
                                    .changeChannelByContent(content, mDisplayId);
                        }
                        alert.cancel();
                    }
                });
        alert.setNegativeButton(R.string.button_text_no,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.cancel();
                    }
                });
        // show alert dialog
        alert.show();
    }

    public void updatePIPCoordinates() {
        int pipPosition = MainActivity.sharedPrefs.getInt(PIP_POSITION, 0);
        int pipSize = MainActivity.sharedPrefs.getInt(PIP_SIZE, 0);
        switch (pipSize) {
            case 0: {
                Log.d(TAG, "loadPIPCoordinates -" + " size 1/9");
                pipWindowHeight = screenHeight / 3;
                pipWindowWidth = screenWidth / 3;
            }
                break;
            case 1: {
                Log.d(TAG, "loadPIPCoordinates -" + " size 1/16");
                pipWindowHeight = screenHeight / 4;
                pipWindowWidth = screenWidth / 4;
            }
                break;
            case 2: {
                Log.d(TAG, "loadPIPCoordinates -" + " custom size");
                pipWindowWidth = MainActivity.sharedPrefs.getInt(PIP_WIDTH, 0);
                pipWindowHeight = MainActivity.sharedPrefs
                        .getInt(PIP_HEIGHT, 0);
            }
                break;
            default:
                break;
        }
        switch (pipPosition) {
            case 0:
                /** Upper right corner */
                Log.d(TAG, "loadPIPCoordinates -" + " Upper right corner");
                pipWindowCoordinateTop = PIP_WINDOW_VERTICAL_OFFSET;
                pipWindowCoordinateLeft = screenWidth
                        - PIP_WINDOW_HORIZONTAL_OFFSET - pipWindowWidth;
                break;
            case 1:
                /** Upper left corner */
                Log.d(TAG, "loadPIPCoordinates -" + " Upper left corner");
                pipWindowCoordinateTop = PIP_WINDOW_VERTICAL_OFFSET;
                pipWindowCoordinateLeft = PIP_WINDOW_HORIZONTAL_OFFSET;
                break;
            case 2:
                /** Lower left corner */
                Log.d(TAG, "loadPIPCoordinates -" + " Lower left corner");
                pipWindowCoordinateTop = screenHeight
                        - PIP_WINDOW_VERTICAL_OFFSET - pipWindowHeight;
                pipWindowCoordinateLeft = PIP_WINDOW_HORIZONTAL_OFFSET;
                break;
            case 3:
                /** Lower right corner */
                Log.d(TAG, "loadPIPCoordinates -" + " Lower right corner");
                pipWindowCoordinateTop = screenHeight
                        - PIP_WINDOW_VERTICAL_OFFSET - pipWindowHeight;
                pipWindowCoordinateLeft = screenWidth
                        - PIP_WINDOW_HORIZONTAL_OFFSET - pipWindowWidth;
                break;
            case 4:
                /** Custom settings */
                Log.d(TAG, "loadPIPCoordinates -" + " custom settings");
                Log.d(TAG, "loadPIPCoordinates -" + " custom size");
                pipWindowCoordinateLeft = MainActivity.sharedPrefs.getInt(
                        PIP_X, 0);
                pipWindowCoordinateTop = MainActivity.sharedPrefs.getInt(PIP_Y,
                        0);
                break;
            default:
                break;
        }
        Log.d(TAG, "PIP - LEFT  : " + pipWindowCoordinateLeft);
        Log.d(TAG, "PIP - RIGHT : " + pipWindowCoordinateTop);
        Log.d(TAG, "PIP - WIDTH : " + pipWindowWidth);
        Log.d(TAG, "PIP - HEIGHT: " + pipWindowHeight);
    }

    public void updatePAPCoordinates() {
        MainActivity.papWindowCoordinateLeft = screenWidth / 2;
        MainActivity.papWindowCoordinateTop = 0;
        MainActivity.papWindowHeight = screenHeight;
        MainActivity.papWindowWidth = screenWidth / 2;
        Log.d(TAG, "PAP - LEFT  : " + papWindowCoordinateLeft);
        Log.d(TAG, "PAP - RIGHT : " + papWindowCoordinateTop);
        Log.d(TAG, "PAP - WIDTH : " + papWindowWidth);
        Log.d(TAG, "PAP - HEIGHT: " + papWindowHeight);
    }

    public void enableHbbTV() {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (false == MainActivity.service.getHbbTvControl()
                            .isHbbEnabled()) {
                        Log.d(TAG, "EnableHBBtv");
                        MainActivity.service.getHbbTvControl().enableHBB();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void disableHbbTV() {
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (MainActivity.service.getHbbTvControl().isHbbEnabled()) {
                        Log.d(TAG, "DisableHBBtv");
                        MainActivity.service.getHbbTvControl().disableHBB();
                        MainActivity.webDialog.getHbbTVView().setAlpha(
                                (float) 0.00);
                        MainActivity.setKeySet(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void playOverlayVideo(boolean doStartService) {
        if (doStartService) {
            String play_uri = "tv://overlay:0";
            String video_size = pipWindowCoordinateLeft + ","
                    + pipWindowCoordinateTop + "," + pipWindowWidth + ","
                    + pipWindowHeight;
            Log.d(TAG, "playOverlayVideo - start overlay service");
            Intent intent = new Intent(OVERLAY_VIDEO_SERVICE);
            intent.putExtra("play_uri", play_uri);
            intent.putExtra("video_size", video_size);
            startService(intent);
            playOverlayVideoOnStop = false;
        } else {
            Log.d(TAG, "playOverlayVideo - play on stop");
            playOverlayVideoOnStop = true;
        }
    }
}
