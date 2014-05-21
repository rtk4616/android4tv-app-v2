package com.iwedia.gui.osd.noneinfobanner;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.ServiceModeDialog;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.controller.MediaController;
import com.iwedia.gui.multimedia.dlna.player.controller.DlnaLocalController;
import com.iwedia.gui.multimedia.pvr.player.controller.PVRPlayerController;
import com.iwedia.gui.multimedia.pvr.record.controller.PVRRecordController;
import com.iwedia.gui.osd.ChannelChangeHandler;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.pvr.PVRHandler;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NoneBannerHandler implements IOSDHandler, OSDGlobal {
    private final static String TAG = "NoneBannerHandler";
    private MainActivity mActivity = null;
    private Thread mTimerThread = null;
    private static int mHandlerState = CURL_HANDLER_STATE_DO_NOTHING;
    // ChannelInfo
    private Content mContentExtendedInfo = null;
    // Channel Change
    private ChannelChangeHandler mChannelChangeHandler = null;
    /** Volume value */
    private int currentVolume = -1;
    private Timer mTimer = new Timer();
    private TimerTask mStateTimerTask = null;
    private PVRHandler mPvrHandler = null;
    private PVRRecordController mPvrRecordController = null;
    private PVRPlayerController mPvrPlayerController = null;
    private MediaController mMediaController = null;
    private DlnaLocalController mDlnaLocalController = null;
    private static final int mDisplayId = 0;
    private static String secretKey = "22223333";
    public int currentState = STATE_INIT;
    public int previousState = STATE_INIT;
    protected ArrayList<String> mStrValues = null;
    private boolean recordInProgress = false;

    // //////////////////////////////////////////////
    public NoneBannerHandler(MainActivity activity) {
        this.mActivity = activity;
        OSDHandlerHelper.setGlobalHandler(this);
    }

    @Override
    public void init(View view) {
        Log.i(TAG, "init");
        mChannelChangeHandler = new ChannelChangeHandler(mActivity);
        mMediaController = ((MainActivity) mActivity).getMediaController();
        // Init PVR Handler and PVR Methods
        mPvrHandler = new PVRHandler();
        initControlProviderPVR();
    }

    /** Change Channel */
    @Override
    public void prepareChannelAndChange(int scenario, int channelState) {
        Log.i(TAG, "prepareChannelAndChange " + channelState);
        if (isServiceListEmpty()) {
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.empty_list);
        } else {
            mChannelChangeHandler
                    .changeChannelUpDownPreviousContent(channelState);
            try {
                if (scenario != SCENARIO_NUMEROUS_CHANNEL_CHANGE) {
                    Content activeContent = MainActivity.service
                            .getContentListControl().getActiveContent(0);
                    Content content = MainActivity.service
                            .getContentListControl().getContent(
                                    mChannelChangeHandler.getChannelIndex());
                    if (activeContent.getSourceType() != SourceType.ANALOG
                            && activeContent.getFilterType() != FilterType.INPUTS
                            && content.getSourceType() == SourceType.ANALOG) {
                        /*
                         * Remove WebView from screen and set key mask to 0
                         */
                        if (0 != (MainActivity.getKeySet())) {
                            if (!MainActivity.activity
                                    .isHbbTVInHTTPPlaybackMode()) {
                                MainActivity.activity.webDialog.getHbbTVView()
                                        .setAlpha((float) 0.00);
                                MainActivity.setKeySet(0);
                            }
                        }
                    }
                    setChannelStrings();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((MainActivity) mActivity).getPageCurl().getChannelChangeHandler()
                    .changeChannel();
            if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                recordInProgress = true;
                multimediaControllerPVR(false);
            }
            // /////////////////////////////////////////////////////
            // If zapping not happens, close info banner
            // /////////////////////////////////////////////////////
            startTimer(ANIMATION_TIME_CHANNEL_CHANGE, STATE_INFO);// !!!!!!!!!!!!!!!!!!
        }
    }

    /** ChangeChannel by Content from ContentList */
    public void changeChannelByContent(Content content, int displayId) {
        mChannelChangeHandler.syncChannelIndexByContent(content, displayId);
        prepareChannelAndChange(SCENARIO_CHANNEL_CHANGE_BY_CONTENT,
                CHANNEL_CONTENT);
    }

    /** ChangeChannel by NumPads */
    public void changeChannelByNum(int channelNum, int displayId) {
        if (ConfigHandler.ATSC) {
            setChannelNumerousStrings(mChannelChangeHandler
                    .fillMajorMinorBuffers(channelNum));
        } else {
            StringBuilder lNumericalChannelBuffer = mChannelChangeHandler
                    .fillNumericalChannelBuffer(channelNum);
            if (lNumericalChannelBuffer != null) {
                setChannelNumerousStrings(lNumericalChannelBuffer.toString());
            }
        }
        mChannelChangeHandler.fillSecretKeyBuffer(channelNum);
        if (mChannelChangeHandler.checkSecretKey()) {
            ServiceModeDialog serModeDialog = MainActivity.activity
                    .getDialogManager().getServiceModeDialog();
            if (serModeDialog != null) {
                serModeDialog.show();
            }
        } else {
            startTimer(ANIMATION_TIME_NUMEROUS_CHANGE_CHANNEL,
                    STATE_NUMEROUS_CHANGE_CHANNEL);
        }
    }

    /** Multimedia controller for pvr */
    public boolean multimediaControllerPVR(boolean openFirst) {
        if (mHandlerState == CURL_HANDLER_STATE_DO_NOTHING) // when stop is
        // pressed
        {
            currentState = STATE_INIT;
            recordInProgress = false;
        }
        return true;
    }

    /**
     * Change Volume
     * 
     * @param volState
     *        VOLUME_UP,VOLUME_DOWN,VOLUME_MUTE
     * @param startCurl
     *        do nothing
     */
    public void volume(int volState, boolean startCurl) {
        setVolume(volState);
    }

    /** Info */
    public void info() {
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_INPUT_INFO:
                case STATE_INFO: {
                    mHandlerState = STATE_INFO_BANNER_HIDDEN;
                    currentState = STATE_INIT;//
                    break;
                }
                case STATE_NUMEROUS_CHANGE_CHANNEL:
                    mChannelChangeHandler.setMaxNumberOfChannels();
                    if (!mChannelChangeHandler
                            .channelExistence(SCENARIO_NUMEROUS_CHANNEL_CHANGE)) {
                        String newString = mChannelChangeHandler
                                .getStrBufferedSecretKey().toString();
                        if (!newString.startsWith(secretKey)) {
                            mChannelChangeHandler
                                    .getToastForNumerousChannelChange();
                        } else {
                            mChannelChangeHandler
                                    .setOverMaxChannelNumber(false);
                        }
                        mChannelChangeHandler.flushChannelIndexBuffer();
                        mChannelChangeHandler.flushSecretKeyBuffer();
                        mChannelChangeHandler
                                .flushMajorMinorChannelIndexBuffer();
                        if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                            recordInProgress = false;
                            multimediaControllerPVR(false);
                        } else {
                            mHandler.sendEmptyMessage(STATE_INFO);
                        }
                        break;
                    }
                    // /////////////////////////////////////////////////////
                    // If zapping not happens, close info banner
                    // /////////////////////////////////////////////////////
                    startTimer(ANIMATION_TIME_CHANNEL_CHANGE, STATE_INFO);// !!!!!!!!!!!!!!!!!!
                    Content activeContent = mChannelChangeHandler
                            .getCurrentChannelContent();
                    getChannelChangeHandler().changeChannel();
                    Content content = mChannelChangeHandler
                            .getCurrentChannelContent();
                    if (activeContent != null)
                        if (content != null)
                            if (activeContent.getSourceType() != SourceType.ANALOG
                                    && activeContent.getFilterType() != FilterType.INPUTS
                                    && content.getSourceType() == SourceType.ANALOG) {
                                /*
                                 * Remove WebView from screen and set key mask
                                 * to 0
                                 */
                                if (0 != (MainActivity.getKeySet())) {
                                    try {
                                        if (!MainActivity.activity
                                                .isHbbTVInHTTPPlaybackMode()) {
                                            MainActivity.activity.webDialog
                                                    .getHbbTVView().setAlpha(
                                                            (float) 0.00);
                                            MainActivity.setKeySet(0);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                        recordInProgress = true;
                        multimediaControllerPVR(false);
                    }
                    break;
            }
        }
    };

    private void startTimer(final int milliseconds, final int what) {
        if (mTimer != null) {
            mTimer.purge();
            if (mStateTimerTask != null) {
                mStateTimerTask.cancel();
            }
            mStateTimerTask = null;
            mStateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(what);
                    }
                }
            };
            mTimer.schedule(mStateTimerTask, milliseconds);
        }
    }

    /** Updated timeShift time */
    public void updateTimeShiftPlayingTime(int mPlayingTime, int progressValue) {
    }

    /** Updated PVR recording and PVR playback time */
    public void updatePlayingTime(int mStartTime, int mEndTime,
            int mPlayingTime, int progressValue) {
    }

    /**
     * Stops background thread
     */
    private void stopThreadMedia() {
        if (mTimerThread != null) {
            Thread moribund = mTimerThread;
            mTimerThread = null;
            moribund.interrupt();
        }
    }

    /** Background thread for updating multimedia playback time */
    private void startThreadMedia() {
        stopThreadMedia();
        mTimerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread thisThread = Thread.currentThread();
                while (true) {
                    if (thisThread.equals(mTimerThread)) {
                        mHandler.sendEmptyMessage(STATE_UPDATE_MULTIMEDIA_PLAYBACK_TIME);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "There Was error in Thread Sleep!", e);
                    }
                }
            }
        });
        mTimerThread.setPriority(Thread.MIN_PRIORITY);
        mTimerThread.start();
    }

    /** Multimedia controller for pvr and multimedia playback */
    public boolean multimediaController(boolean openFirst) {
        if (mHandlerState == CURL_HANDLER_STATE_DO_NOTHING) {
            currentState = STATE_INIT;
        }
        switch (mHandlerState) {
            case CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER: // play, stop
                // multimedia(dlna)
                // playback
            {
                switch (A4TVMultimediaController.getControlPosition()) {
                    case MULTIMEDIA_CONTROLLER_STOP:
                        stopThreadMedia();
                        // A4TVMultimediaController
                        // .setControlPosition(MULTIMEDIA_CONTROLLER_PLAY);
                        break;
                    case MULTIMEDIA_CONTROLLER_PLAY:
                        startThreadMedia();
                        break;
                    default:
                        break;
                }
            }
            // pvr playback
            case PVR_STATE_PLAY_PLAY_BACK:
            case PVR_STATE_PAUSE_PLAY_BACK:
            case PVR_STATE_FF_PLAY_BACK:
            case PVR_STATE_REW_PLAY_BACK:
                currentState = STATE_MULTIMEDIA_CONTROLLER;
                return true;
            default:
                return false;
        }
    }

    /** Prepare Controller for DLNA or Local PalyBack */
    public void prepareAndStartMultiMediaPlayBackDLNALocal(
            MultimediaContent content, boolean isMusicInfo) {
        int displayId = 0;
        mMediaController.stopLiveStream();
        A4TVMultimediaController.setControlPosition(MULTIMEDIA_CONTROLLER_PLAY);
        MainKeyListener.setAppState(MainKeyListener.MULTIMEDIA_PLAYBACK);
        ((MainActivity) mActivity).getMultimediaHandler().closeMultimedia();
        A4TVMultimediaController.getControlProvider().setContent(content);
        A4TVMultimediaController.getControlProvider().play(displayId);
        String strName;
        try {
            strName = content.getName();
        } catch (Exception e) {
            Log.i(TAG,
                    "Method: prepareAndStartMultiMediaPlayBackDLNALocal strName");
            e.printStackTrace();
            strName = "";
        }
        if (!isMusicInfo) {
            ControlProvider.setFileName(strName);
            ControlProvider.setFileDescription("");
            ControlProvider.setNameOfAlbum("");
        }
        /** Show Info */
        setHandlerState(CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER);
        multimediaController(false);
    }

    /** Prepare Controller for PVR PalyBack */
    public void prepareAndStartMultiMediaPlayBackPVR(MultimediaContent content,
            final int displayId) {
        setHandlerState(PVR_STATE_PLAY_PLAY_BACK);
        A4TVMultimediaController.getControlProvider().setContent(content);
        /**
         * Info and Play() moved to playbackStopped Callback in MainActivity,
         * but added here because of sometimes channel can not be changed and
         * there is zapp callback false and no stop playback
         */
        try {
            if (MainActivity.service.getServiceControl().getActiveService()
                    .getServiceIndex() == -1) {
                Log.i(TAG,
                        "Service List is Empty or Channel can not be changed!");
                A4TVMultimediaController.getControlProvider().play(displayId);
                /** Show Info */
                multimediaController(false);
            } else {
                // Stop live stream
                // ZORANA TEMP DISABLE COM 4.0
                /*
                 * ((MainActivity) mActivity).getMediaController()
                 * .stopLiveStream();
                 */
            }
        } catch (Exception e) {
            Log.i(TAG, "Can not start pvr playback!", e);
        }
        String strName = "";
        try {
            strName = content.getName();
        } catch (Exception e) {
            Log.i(TAG, "Method: prepareAndStartMultiMediaPlayBackPVR strName",
                    e);
        }
        ControlProvider.setFileName(strName);
        /** Info and Play() moved to playbackStopped Callback in MainActivity */
        // ZORANA Temp fix until Channel Zap event is implemented!!!
        Log.i(TAG, "PlayBackStopped CallBack");
        if (MainKeyListener.getAppState() != MainKeyListener.CLEAN_SCREEN) {
            Log.i(TAG, "PlayBackStopped CallBack Accepted");
            // Blank Screen
            try {
                MainActivity.service.getVideoControl().videoBlank(0, true);
            } catch (Exception e) {
                Log.e(TAG, "Blank Screen Exception", e);
            }
        }
        if (mHandlerState == PVR_STATE_PLAY_PLAY_BACK) {
            MainActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    A4TVMultimediaController.getControlProvider().play(
                            displayId);
                    /** Show Info */
                    multimediaController(false);
                }
            });
        }
        // ZORANA end of temp fix
    }

    /** Use Controls from MultiMedia and PVR Custom Controller */
    /** Method for moving Left through Controller */
    public void multimediaControllerMoveLeft() {
    }

    /** Method for Using control in Controller */
    public void multimediaControllerClick(boolean immediatelyClick) {
        switch (MainKeyListener.getAppState()) {
            case MainKeyListener.MULTIMEDIA_PLAYBACK: {
                if (A4TVMultimediaController.getControlProvider() != null) {
                    if (multimediaController(true) || immediatelyClick) {
                        A4TVMultimediaController.getControlProvider().click(0);
                        if (mHandlerState == PVR_STATE_STOP_PLAY_BACK) {
                            currentState = STATE_INIT;
                        }
                    }
                }
                break;
            }
            case MainKeyListener.PVR: {
                if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                    if (multimediaControllerPVR(true) || immediatelyClick) {
                        A4TVProgressBarPVR.getControlProviderPVR().click();
                    }
                    if (mHandlerState == CURL_HANDLER_STATE_DO_NOTHING) {
                        recordInProgress = false;
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    /** Method for moving Right through Controller */
    public void multimediaControllerMoveRight() {
    }

    public void setChannelStrings() {
        int activeFilterInService = FilterType.ALL;
        try {
            activeFilterInService = MainActivity.service
                    .getContentListControl().getActiveFilterIndex();
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
        Content content = null;
        try {
            content = MainActivity.service.getContentListControl().getContent(
                    mChannelChangeHandler.getChannelIndex());
        } catch (Exception e) {
            Log.e(TAG, "There was problem with getting content.", e);
        }
        if (content != null) {
            String currentChannel;
            if (ConfigHandler.ATSC) {
                int major;
                int minor;
                if (ConfigHandler.USE_LCN) {
                    major = content.getServiceLCN()
                            / MAJOR_MINOR_CONVERT_NUMBER;
                    minor = content.getServiceLCN()
                            % MAJOR_MINOR_CONVERT_NUMBER;
                } else {
                    major = content.getIndex() / MAJOR_MINOR_CONVERT_NUMBER;
                    minor = content.getIndex() % MAJOR_MINOR_CONVERT_NUMBER;
                }
                currentChannel = String.format("%d-%d", major, minor);
            } else {
                if (activeFilterInService == FilterType.ALL) {
                    currentChannel = String.valueOf(mChannelChangeHandler
                            .getChannelIndex() + 1);
                } else {
                    if (ConfigHandler.USE_LCN)
                        currentChannel = String
                                .valueOf(content.getServiceLCN());
                    else
                        currentChannel = String.valueOf(content.getIndex());
                }
            }
            String strName = "";
            if (content instanceof ServiceContent) {
                strName = content.getName();
                // if (strName.length() >= 15) {
                // strName = strName.substring(0, 14) + "...";
                // }
            } else if (content.getFilterType() == FilterType.INPUTS) {
                currentChannel = mActivity.getResources().getString(
                        R.string.main_menu_content_list_inputs);
                strName = content.getName();
            }
            ArrayList<String> strValues = new ArrayList<String>();
            strValues.add(currentChannel);
            strValues.add(strName);
            setStrValues(strValues);
        } else {
            Log.i(TAG, "setChannelStrings: content = null");
        }
    }

    /**
     * Add Numbers to String Buffer
     * 
     * @param index
     */
    private void setChannelNumerousStrings(String index) {
        ArrayList<String> strValues = new ArrayList<String>();
        strValues.add(index);
        setStrValues(strValues);
    }

    public void setCurlPictures() {
    }

    /**
     * Display channel info or numerous channel info
     * 
     * @param state
     *        - STATE_INFO or STATE_NUMEROUS_CHANGE_CHANNEL
     */
    public void drawInfoBanner(int state) {
    }

    /** Initialize Volume from Proxy. */
    private void initVolume() {
        // /////////////////////////////////////////////////
        // Init Stream Volume
        // /////////////////////////////////////////////////
        try {
            currentVolume = (int) MainActivity.service.getSystemControl()
                    .getSoundControl().getVolume();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Can't Init Volume!");
        }
    }

    /** Change Volume in Comedia and Prepare Strings */
    private void setVolume(int volume) {
        if (currentVolume == -1) {
            initVolume();
        }
        int valVolume;
        try {
            // Change volume
            switch (volume) {
                case VOLUME_UP: {
                    // Get Current Volume from Stream
                    valVolume = (int) MainActivity.service.getSystemControl()
                            .getSoundControl().getVolume();
                    valVolume += 1;
                    if (valVolume > 100) {
                        valVolume = 100;
                    }
                    MainActivity.service.getSystemControl().getSoundControl()
                            .setVolume(valVolume);
                    currentVolume = valVolume;
                    break;
                }
                case VOLUME_DOWN: {
                    // Get Current Volume from Stream
                    valVolume = (int) MainActivity.service.getSystemControl()
                            .getSoundControl().getVolume();
                    valVolume -= 1;
                    if (valVolume < 0) {
                        valVolume = 0;
                    }
                    MainActivity.service.getSystemControl().getSoundControl()
                            .setVolume(valVolume);
                    currentVolume = valVolume;
                    break;
                }
                case VOLUME_MUTE: {
                    MainActivity.service
                            .getSystemControl()
                            .getSoundControl()
                            .muteAudio(
                                    !MainActivity.service.getSystemControl()
                                            .getSoundControl().isMute());
                    // if
                    // (MainActivity.service.getSystemControl().getSoundControl()
                    // .isMute()) {
                    // valVolume = 0;
                    //
                    // } else {
                    // valVolume = currentVolume;
                    //
                    // }
                    break;
                }
                default: {
                    Log.i(TAG, "Wrong Volume Parameter!");
                    break;
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Can't set Volume!");
        }
    }

    /** Check if service list empty */
    public boolean isServiceListEmpty() {
        try {
            int size = MainActivity.service.getServiceControl()
                    .getServiceListCount(ServiceListIndex.MASTER_LIST);
            size += MainActivity.service.getContentListControl()
                    .getContentFilterListSize(FilterType.IP_STREAM);
            if (size == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "GetFilterListSize", e);
            return false;
        }
    }

    /****************************************************************************/
    /** CallBack Method when Channel is Zapped */
    public void channelIsZapped(boolean success) {
    }

    /** Getters and Setters */
    public void setHandlerState(int mCurlHandlerState) {
        mHandlerState = mCurlHandlerState;
    }

    public int getHandlerState() {
        return mHandlerState;
    }

    public ChannelChangeHandler getChannelChangeHandler() {
        return mChannelChangeHandler;
    }

    public Content getContentExtendedInfo() {
        return mContentExtendedInfo;
    }

    public void initControlProviderDLNALocal() {
        if (mDlnaLocalController == null)
            mDlnaLocalController = new DlnaLocalController(mActivity,
                    mMediaController);
        A4TVMultimediaController.setControlProvider(mDlnaLocalController);
    }

    public void initControlProviderPVR() {
        if (mPvrPlayerController == null)
            mPvrPlayerController = new PVRPlayerController(mPvrHandler,
                    mActivity);
        A4TVMultimediaController.setControlProvider(mPvrPlayerController);
        if (mPvrRecordController == null) {
            mPvrRecordController = new PVRRecordController(mPvrHandler);
        }
        A4TVProgressBarPVR.setControlProvider(mPvrRecordController);
    }

    public void setAnimationTimeChannelInfo(int i) {
    }

    public int getCurrentState() {
        return currentState;
    }

    public void startCurlEffect(int scenarioDoNothing) {
    }

    public boolean isFlagChannelInfo() {
        return false;
    }

    public void setUpNewChannelInfo(int index) {
    }

    public PVRPlayerController getPvrPlayerController() {
        return mPvrPlayerController;
    }

    @Override
    public void updateChannelInfo(int channelIndex) {
    }

    @Override
    public void drawInputInfo() {
    }

    public void updateTimeChannelInfo() {
    }

    @Override
    public void scroll(int direction) {
    }

    private void setStrValues(ArrayList<String> strValues) {
        this.mStrValues = strValues;
    }

    @Override
    public void getPreviousChannelInfo() {
        // TODO Auto-generated method stub
    }

    @Override
    public void getNextChannelInfo() {
        // TODO Auto-generated method stub
    }

    @Override
    public int getChannelInfoIndex() {
        return 0;
    }

    @Override
    public void getExtendedInfo() {
        // TODO Auto-generated method stub
    }

    @Override
    public void showPictureFormat(String format) {
        Log.d(TAG, "showPictureFormat" + format);
    }
}
