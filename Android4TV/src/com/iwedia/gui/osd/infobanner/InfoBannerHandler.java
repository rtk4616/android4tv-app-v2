package com.iwedia.gui.osd.infobanner;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.epg.EpgEventType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.dtv.types.VideoResolution;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVInfoDescriptionScrollView.Scrolled;
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
import com.iwedia.gui.osd.Conversions;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.pvr.PVRHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class InfoBannerHandler extends InfoBannerHelper implements IOSDHandler {
    private final static String TAG = "InfoBannerHandler";
    private MainActivity mActivity = null;
    private int scrollValue = 0;
    /**
     * Prevent to display info banner when timeshift occurs. In timeshift,
     * channel callback is called.
     */
    public static boolean flagZapp = false;
    private Thread mTimerThread = null;
    private static int mHandlerState = CURL_HANDLER_STATE_DO_NOTHING;
    private static SimpleDateFormat formatTime24Hour = new SimpleDateFormat(
            "HH:mm");
    // ChannelInfo
    private Content mContentExtendedInfo = null;
    // Channel Change
    private ChannelChangeHandler mChannelChangeHandler = null;
    /** Volume value */
    private int currentVolume = -1;
    private static Date dateFromStream = null;
    private Timer mTimer = new Timer();
    private TimerTask mStateTimerTask = null;
    private PVRHandler mPvrHandler = null;
    private PVRRecordController mPvrRecordController = null;
    private PVRPlayerController mPvrPlayerController = null;
    private MediaController mMediaController = null;
    private DlnaLocalController mDlnaLocalController = null;
    // pvr record and timeshift time
    private String mStartTime = "";
    private String mEndTime = "";
    private String mPlayingTime = "";
    private int mProgressPvrValue = -1;
    // play timeshift
    private String mTimeShiftPlayingTime = "";
    private int mProgressTimeShiftValue = -1;
    private static final int mDisplayId = 0;
    private static String secretKey = "22223333";
    /** Detect scroll end */
    private boolean detectEnd = false;
    /** Detect if exists moving through image player control */
    private boolean moving = false;
    private boolean recordInProgress = false;

    public InfoBannerHandler(MainActivity activity) {
        super(activity);
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
            // startTimer(ANIMATION_TIME_NUMEROUS_CHANGE_CHANNEL,
            // STATE_NUMEROUS_CHANGE_CHANNEL);
            ((MainActivity) mActivity).getPageCurl().getChannelChangeHandler()
                    .changeChannel();
            if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                multimediaControllerPVR(false);
            } else {
                drawInfoBanner(STATE_CHANGE_CHANNEL);
            }
            // /////////////////////////////////////////////////////
            // If zapping not happens, close info banner
            // /////////////////////////////////////////////////////
            startTimer(ANIMATION_TIME_CHANNEL_CHANGE, STATE_INFO);// !!!!!!!!!!!!!!!!!!
            if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                try {
                    Thread.sleep(2000);
                    recordInProgress = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
        if (OSDHandlerHelper.getHandlerState() != PVR_STATE_RECORDING) {
            drawInfoBanner(STATE_NUMEROUS_CHANGE_CHANNEL);
        }
    }

    /** Multimedia controller for pvr */
    public boolean multimediaControllerPVR(boolean openFirst) {
        flagZapp = true;
        if (currentState == STATE_VOLUME) {
            currentState = previousState;
        }
        if (mHandlerState == CURL_HANDLER_STATE_DO_NOTHING) // when stop is
        // pressed
        {
            flushPlayerFields();
            currentState = STATE_INIT;
            textViewPlayerState.setText(R.string.stop);
            recordInProgress = false;
            return true;
        }
        if (!openFirst) {
            mChannelChangeHandler.getExtendedInfo();
            setChannelInfoStrings();
            setPlayerChannelInfoInformation();
        }
        mViewFlipper.setVisibility(View.GONE);
        linearLayoutVolume.setVisibility(View.GONE);
        linearLayoutInput.setVisibility(View.GONE);
        // ///////////////////////////////////////
        // /////pvr and start timeshift
        // ////////////////////////////////////////
        setPlayerImageControl(STATE_PVR);
        if (!linearLayoutPlayerInfo.isShown()) {
            animationIn(linearLayoutPlayerInfo);
        }
        currentState = STATE_PVR;
        startTimer(ANIMATION_TIME_PVR, STATE_PVR);
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
        if (currentState == STATE_VOLUME && previousState != STATE_CHANNEL_INFO) {
            currentState = previousState;
        }
        switch (currentState) {
        // /////////////////////////////////////////////////////////////////////////////////////////////
        // If pvr record is in progress and isn't visible, and then user press
        // info, pvr info is shown
        // /////////////////////////////////////////////////////////////////////////////////////////////
            case STATE_PVR: {
                linearLayoutVolume.setVisibility(View.GONE);
                mViewFlipper.setVisibility(View.GONE);
                linearLayoutInput.setVisibility(View.GONE);
                if (!linearLayoutPlayerInfo.isShown()) {
                    animationIn(linearLayoutPlayerInfo);
                    startTimer(ANIMATION_TIME_PVR, STATE_PVR);
                }
                break;
            }
            // ///////////////////////////////////////////////////////////////////////////////////////////////
            // If pvr playback is in progress and isn't visible, and then user
            // press
            // info, pvr info is shown
            // ///////////////////////////////////////////////////////////////////////////////////////////////
            case STATE_MULTIMEDIA_CONTROLLER: {
                linearLayoutVolume.setVisibility(View.GONE);
                mViewFlipper.setVisibility(View.GONE);
                linearLayoutInput.setVisibility(View.GONE);
                if (!linearLayoutPlayerInfo.isShown()) {
                    animationIn(linearLayoutPlayerInfo);
                    startTimer(ANIMATION_TIME_MULTIMEDIA_CONTROLLER,
                            STATE_STOP_PVR_PLAYBACK);
                }
                break;
            }
            // /////////////////////////////////////////////////////
            // If info pressed second time, info is closed
            // /////////////////////////////////////////////////////
            case STATE_CHANNEL_INFO: {
                linearLayoutVolume.setVisibility(View.INVISIBLE);
                linearLayoutPlayerInfo.setVisibility(View.GONE);
                linearLayoutInput.setVisibility(View.GONE);
                animationOut(mViewFlipper);
                mHandlerState = STATE_INFO_BANNER_HIDDEN;
                currentState = STATE_INIT;//
                mTimer.cancel();
                mTimer = null;
                mTimer = new Timer();
                break;
            }
            default: {
                linearLayoutVolume.setVisibility(View.INVISIBLE);
                linearLayoutPlayerInfo.setVisibility(View.INVISIBLE);
                linearLayoutInput.setVisibility(View.GONE);
                if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                    int lFilterType = -1;
                    try {
                        if (MainActivity.service != null) {
                            lFilterType = MainActivity.service
                                    .getContentListControl()
                                    .getActiveContent(mDisplayId)
                                    .getFilterType();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (lFilterType == FilterType.INPUTS) {
                        drawInputInfo();
                    } else {
                        if (isServiceListEmpty()) {
                            A4TVToast toast = new A4TVToast(mActivity);
                            toast.showToast(R.string.empty_list);
                        } else {
                            scrollValue = 0;
                            scrollView.scrollTo(0, scrollValue);
                            detectEnd = false;
                            currentState = STATE_CHANNEL_INFO;
                            setUpNewChannelInfo(0);
                        }
                    }
                }
                break;
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_INPUT_INFO: {
                    animationOut(linearLayoutInput);
                    mHandlerState = STATE_INFO_BANNER_HIDDEN;
                    currentState = STATE_INIT;//
                    break;
                }
                // /////////////////////////////////////////////
                // When timer expires, close PVR recording info
                // /////////////////////////////////////////////
                case STATE_PVR:
                    animationOut(linearLayoutPlayerInfo);
                    break;
                case STATE_INFO:
                    // /////////////////////////////////////////////
                    // When timer expires, close info
                    // /////////////////////////////////////////////
                    animationOut(mViewFlipper);
                    mHandlerState = STATE_INFO_BANNER_HIDDEN;
                    currentState = STATE_INIT;//
                    break;
                // //////////////////////////////
                // Update timeShift playing time
                // //////////////////////////////
                case STATE_PLAY_TIMESHIFT:
                    linearLayoutPlayerElapsedTime.setVisibility(View.VISIBLE);
                    textViewElapsedTime.setText(mTimeShiftPlayingTime);
                    playerProgressBarTime.setProgress(mProgressTimeShiftValue);
                    break;
                // ////////////////////////////////////////
                // Update PVR and timeShift recording time
                // ////////////////////////////////////////
                case STATE_PVR_TIMESHIFT_RECORD:
                    // timeshift and pvr
                    if (recordInProgress) {
                        textViewPlayerState.setText("");
                        textViewFileName.setText("");
                        imageViewMediaIcon.setImageBitmap(null);
                        textViewPlayerTitle.setText("Recording in progress");
                    } else {
                        textViewPlayerState.setText(A4TVProgressBarPVR
                                .getControlProviderPVR().getFileDescription());
                        textViewFileName.setText(A4TVProgressBarPVR
                                .getControlProviderPVR().getFileName());
                        imageViewMediaIcon
                                .setImageResource(R.drawable.media_controller_icon_movie);
                        textViewPlayerTitle.setText(R.string.recording);
                    }
                    textViewStartTime.setText(mStartTime);
                    textViewEndTime.setText(mEndTime);
                    textViewRecordTime.setText(mPlayingTime);
                    playerProgressBarTime
                            .setSecondaryProgress(mProgressPvrValue);
                    break;
                // //////////////////////////////////////////
                // When timer expires, close volume info
                // //////////////////////////////////////////
                case STATE_VOLUME:
                    animationOut(linearLayoutVolume);
                    break;
                // ////////////////////////////////////////
                // Update PVR playback time
                // ////////////////////////////////////////
                case STATE_PLAY_PVR_PLAYBACK:
                    textViewPlayerTitle.setText(R.string.playing);
                    playerProgressBarTime.setProgress(mProgressPvrValue);
                    textViewPlayerState.setText(A4TVMultimediaController
                            .getControlProvider().getFileDescription());
                    textViewFileName.setText(A4TVMultimediaController
                            .getControlProvider().getFileName());
                    imageViewMediaIcon
                            .setImageResource(R.drawable.media_controller_icon_movie);
                    textViewRecordTime.setText(mPlayingTime);
                    textViewEndTime.setText(mEndTime);
                    textViewStartTime.setText("00:00:00");
                    break;
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
                // /////////////////////////////////////////////
                // When timer expires, close PVR playback info
                // /////////////////////////////////////////////
                case STATE_STOP_PVR_PLAYBACK:
                    if (mHandlerState != CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER) {
                        flushPlayerFields();
                    }
                    animationOut(linearLayoutPlayerInfo);
                    // linearLayoutPlayerInfo.startAnimation(animationFlipOut);
                    break;
                // ////////////////////////////////////////
                // Update multimedia(dlna) playback time
                // ////////////////////////////////////////
                case STATE_UPDATE_MULTIMEDIA_PLAYBACK_TIME:
                    // /////////////////////////////////////
                    if (mMediaController.isPlaying()) {
                        ControlProvider.setFlagPlay(true);
                        // Set Duration
                        A4TVMultimediaController.getControlProvider()
                                .setDuration(
                                        mMediaController.getPlayBackDuration());
                        // Set ElapsedTime
                        A4TVMultimediaController.getControlProvider()
                                .setElapsedTime(
                                        mMediaController.getElapsedTime());
                        textViewFileName.setText(A4TVMultimediaController
                                .getControlProvider().getFileName());
                        textViewFileDescription
                                .setText(A4TVMultimediaController
                                        .getControlProvider()
                                        .getFileDescription());
                        textViewNameOfAlbum.setTypeface(null, Typeface.ITALIC);
                        textViewNameOfAlbum.setText(A4TVMultimediaController
                                .getControlProvider().getNameOfAlbum());
                        textViewPlayerTitle.setText(R.string.playing);
                        textViewRecordTime
                                .setText(calculateTime(mMediaController
                                        .getElapsedTime()));
                        textViewEndTime.setText(calculateTime(mMediaController
                                .getPlayBackDuration()));
                        int progressValue = Conversions.getPVRPassedPercent(
                                mMediaController.getElapsedTime(),
                                mMediaController.getPlayBackDuration());
                        playerProgressBarTime.setProgress(progressValue);
                    } else {
                        ControlProvider.setFlagPlay(false);
                    }
                    // /////////////////////////////////////////
                    break;
                case STATE_PICTURE_FORMAT:
                    animationOut(mLinearLayoutPictureFormatInfo);
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
        mTimeShiftPlayingTime = calculateTime(mPlayingTime);
        mProgressTimeShiftValue = progressValue;
        mHandler.sendEmptyMessage(STATE_PLAY_TIMESHIFT);
    }

    /** Updated PVR recording and PVR playback time */
    public void updatePlayingTime(int mStartTime, int mEndTime,
            int mPlayingTime, int progressValue) {
        // For pvr playback
        if (currentState == STATE_MULTIMEDIA_CONTROLLER) {
            this.mPlayingTime = calculateTime(mPlayingTime * 1000);
            this.mEndTime = calculateTime(mEndTime * 1000);
            mProgressPvrValue = progressValue;
            mHandler.sendEmptyMessage(STATE_PLAY_PVR_PLAYBACK);
            // For pvr recording
        } else {
            this.mStartTime = calculateTime(mStartTime);
            this.mEndTime = calculateTime(mEndTime);
            this.mPlayingTime = calculateTime(mPlayingTime);
            mProgressPvrValue = progressValue;
            mHandler.sendEmptyMessage(STATE_PVR_TIMESHIFT_RECORD);
        }
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
        flagZapp = true;
        if (mHandlerState == CURL_HANDLER_STATE_DO_NOTHING) {
            currentState = STATE_INIT;
            flushPlayerFields();
            textViewPlayerState.setText(R.string.stop);
        }
        if (!openFirst) {
            mChannelChangeHandler.getExtendedInfo();
            setChannelInfoStrings();
            setPlayerChannelInfoInformation();
        }
        switch (mHandlerState) {
            case CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER: // play, stop
                // multimedia(dlna)
                // playback
            {
                switch (A4TVMultimediaController.getControlPosition()) {
                    case MULTIMEDIA_CONTROLLER_STOP:
                        if (!moving) {
                            stopThreadMedia();
                            flushMultimediaPlaybackFields();
                        }
                        break;
                    case MULTIMEDIA_CONTROLLER_PLAY:
                        if (!moving) {
                            textViewPlayerState.setText("");
                            startThreadMedia();
                        }
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
                setPlayerImageControl(STATE_MULTIMEDIA_CONTROLLER);
                if (!linearLayoutPlayerInfo.isShown()) {
                    animationIn(linearLayoutPlayerInfo);
                }
                startTimer(ANIMATION_TIME_MULTIMEDIA_CONTROLLER,
                        STATE_STOP_PVR_PLAYBACK);
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
        moving = false;
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
        switch (MainKeyListener.getAppState()) {
            case MainKeyListener.MULTIMEDIA_PLAYBACK: {
                if (A4TVMultimediaController.getControlProvider() != null) {
                    moving = true;
                    if (multimediaController(true)) {
                        A4TVMultimediaController.getControlProvider()
                                .moveLeft();
                        setPlayerImageControl(STATE_MULTIMEDIA_CONTROLLER);
                    }
                }
                break;
            }
            case MainKeyListener.PVR: {
                if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                    if (multimediaControllerPVR(true)) {
                        A4TVProgressBarPVR.getControlProviderPVR().moveLeft();
                        setPlayerImageControl(STATE_PVR);
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    /** Method for Using control in Controller */
    public void multimediaControllerClick(boolean immediatelyClick) {
        switch (MainKeyListener.getAppState()) {
            case MainKeyListener.MULTIMEDIA_PLAYBACK: {
                if (A4TVMultimediaController.getControlProvider() != null) {
                    moving = false;
                    if (multimediaController(true) || immediatelyClick) {
                        A4TVMultimediaController.getControlProvider().click(0);
                        if (mHandlerState == PVR_STATE_STOP_PLAY_BACK) {
                            currentState = STATE_INIT;
                            textViewPlayerState.setText(R.string.stop);
                        }
                        setPlayerImageControl(STATE_MULTIMEDIA_CONTROLLER);
                    }
                }
                break;
            }
            case MainKeyListener.PVR: {
                if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                    if (multimediaControllerPVR(true) || immediatelyClick) {
                        A4TVProgressBarPVR.getControlProviderPVR().click();
                        setPlayerImageControl(STATE_PVR);
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
        switch (MainKeyListener.getAppState()) {
            case MainKeyListener.MULTIMEDIA_PLAYBACK: {
                if (A4TVMultimediaController.getControlProvider() != null) {
                    moving = true;
                    if (multimediaController(true)) {
                        A4TVMultimediaController.getControlProvider()
                                .moveRight();
                        setPlayerImageControl(STATE_MULTIMEDIA_CONTROLLER);
                    }
                }
                break;
            }
            case MainKeyListener.PVR: {
                if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                    if (multimediaControllerPVR(true)) {
                        A4TVProgressBarPVR.getControlProviderPVR().moveRight();
                        setPlayerImageControl(STATE_PVR);
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    /****************************************************************************/
    /****************************************************************************/
    /** Prepare Strings */
    /****************************************************************************/
    /**
     * @param content
     * @param index
     *        Index in that list. Radio content in all list will not start from
     *        1, but from real index in all list.
     */
    public void setChannelInfoStrings() {
        int activeFilterInService = FilterType.ALL;
        EpgEvent now = new EpgEvent();
        EpgEvent next = new EpgEvent();
        try {
            activeFilterInService = MainActivity.service
                    .getContentListControl().getActiveFilterIndex();
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
        int index = mChannelChangeHandler.getChannelIndex();
        mContentExtendedInfo = mChannelChangeHandler
                .getContentExtendedInfoByIndex(index);
        tvStatus = 0;
        if (mContentExtendedInfo != null) {
            try {
                now = MainActivity.service.getEpgControl()
                        .getPresentFollowingEvent(0,
                                mContentExtendedInfo.getIndexInMasterList(),
                                EpgEventType.PRESENT_EVENT);
                next = MainActivity.service.getEpgControl()
                        .getPresentFollowingEvent(0,
                                mContentExtendedInfo.getIndexInMasterList(),
                                EpgEventType.FOLLOWING_EVENT);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (mContentExtendedInfo instanceof ServiceContent) {
                ServiceContent sContent = (ServiceContent) mContentExtendedInfo;
                // TODO: ZORANA: determine is HD according to dtv.ServiceType
                // if(sContent.isHD()) {
                // imageViewHD.setImageResource(R.drawable.hd_select);
                // } else {
                imageViewHD.setImageResource(R.drawable.hd_unselect);
                // }
                switch (sContent.getServiceType()) {
                    case DIG_TV:
                        tvStatus = 0;
                        break;
                    case DIG_RAD:
                        tvStatus = 2;
                        break;
                    case DATA_BROADCAST:
                        tvStatus = 1;
                        break;
                }
                SimpleDateFormat format = new SimpleDateFormat(
                        "HH:mm:ss' 'dd/MM/yyyy");
                SimpleDateFormat formatTime12Hour = new SimpleDateFormat(
                        "hh:mm a");
                SimpleDateFormat formatTime24Hour = new SimpleDateFormat(
                        "HH:mm");
                SimpleDateFormat formatDate = new SimpleDateFormat("EEE MM/dd");
                SimpleDateFormat formatTime = formatTime24Hour;
                String timeFromStream;
                String strTime = "";
                String date = "";
                boolean is24HourFormat;
                try {
                    is24HourFormat = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl().is24HourFormat();
                    if (!is24HourFormat) {
                        formatTime = formatTime12Hour;
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings is24HourFormat");
                }
                try {
                    dateFromStream = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl().getTimeDate()
                            .getCalendar().getTime();
                    strTime = formatTime.format(dateFromStream);
                    date = formatDate.format(dateFromStream);
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strTime");
                    strTime = "00:00";
                    date = "";
                }
                String strNow;
                String strNext;
                String strNowTime;
                String strNextTime;
                String strNowShortDescription;
                String strNextShortDescription;
                String strNowLongDescription = "";
                String strNextLongDescription = "";
                Log.d(TAG, " Initial strNextLongDescription:"
                        + strNextLongDescription);
                try {
                    strNow = now.getName();
                    if (strNow.trim().length() > 0) {
                        if (strNow.length() >= 30) {
                            strNow = strNow.substring(0, 27) + "...";
                        }
                    } else {
                        strNow = "";
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNow");
                    strNow = "";
                }
                try {
                    strNext = next.getName();
                    if (strNext.trim().length() > 0) {
                        if (strNext.length() >= 30) {
                            strNext = strNext.substring(0, 27) + "...";
                        }
                    } else {
                        strNext = "";
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNext");
                    strNext = "";
                }
                try {
                    strNowTime = formatTime24Hour.format(now.getStartTime()
                            .getCalendar().getTime());
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNowTime");
                    strNowTime = "";
                }
                try {
                    strNextTime = formatTime24Hour.format(next.getStartTime()
                            .getCalendar().getTime());
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNextTime");
                    strNextTime = "";
                }
                try {
                    strNowShortDescription = now.getDescription();
                } catch (Exception e) {
                    Log.i(TAG,
                            "Method: setChannelInfoStrings strNowLongDescription");
                    strNowShortDescription = "";
                }
                try {
                    strNextShortDescription = next.getDescription();
                } catch (Exception e) {
                    Log.i(TAG,
                            "Method: setChannelInfoStrings strNowLongDescription");
                    strNextShortDescription = "";
                }
                /*
                 * try { strNowLongDescription = now.getExtendedDescription(); }
                 * catch (Exception e) { Log.i(TAG,
                 * "Method: setChannelInfoStrings strNowLongDescription");
                 * strNowLongDescription = ""; } try { strNextLongDescription =
                 * next.getExtendedDescription(); } catch (Exception e) {
                 * Log.i(TAG,
                 * "Method: setChannelInfoStrings strNowLongDescription");
                 * strNowLongDescription = ""; }
                 */
                /*
                 * String strDurationTime; try { strDurationTime =
                 * now.getDurationTime(); if (strDurationTime.trim().length() >
                 * 0) { strDurationTime = strDurationTime.substring(0, 5); }
                 * else { strDurationTime = ""; } } catch (Exception e) {
                 * Log.i(TAG, "Method: setChannelInfoStrings strNowTime");
                 * strDurationTime = ""; }
                 */
                String strName;
                try {
                    strName = sContent.getName();
                    // if (strName.length() >= 15) {
                    // strName = strName.substring(0, 14) + "...";
                    // }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strName");
                    strName = "";
                }
                String strChannelNumber;
                if (ConfigHandler.ATSC) {
                    int major;
                    int minor;
                    if (ConfigHandler.USE_LCN) {
                        major = sContent.getServiceLCN()
                                / MAJOR_MINOR_CONVERT_NUMBER;
                        minor = sContent.getServiceLCN()
                                % MAJOR_MINOR_CONVERT_NUMBER;
                    } else {
                        major = sContent.getIndex()
                                / MAJOR_MINOR_CONVERT_NUMBER;
                        minor = sContent.getIndex()
                                % MAJOR_MINOR_CONVERT_NUMBER;
                    }
                    strChannelNumber = String.format("%d-%d", major, minor);
                } else {
                    if (activeFilterInService == FilterType.ALL) {
                        strChannelNumber = String.valueOf(index + 1);
                    } else {
                        if (ConfigHandler.USE_LCN)
                            strChannelNumber = String.valueOf(sContent
                                    .getServiceLCN());
                        else
                            strChannelNumber = String.valueOf(sContent
                                    .getIndex());
                    }
                }
                ArrayList<String> strValues = new ArrayList<String>();
                // Service Name
                strValues.add(strChannelNumber);
                strValues.add(strTime);
                strValues.add(strNow);
                strValues.add(strNext);
                strValues.add(strNowTime);
                strValues.add(strNextTime);
                strValues.add(strName);
                strValues.add(strNowShortDescription);
                strValues.add(strNextShortDescription);
                if (!sContent.getImage().trim().equals("-1")) {
                    strValues.add(sContent.getImage());
                } else {
                    strValues.add("");
                }
                strValues.add(date);
                strValues.add("");
                strValues.add(strNowLongDescription);
                strValues.add(strNextLongDescription);
                // Add Strings to PageProvider
                setStrValues(strValues);
                setChannelProgressValue(calculateProgress(now, next));
            } else if (mContentExtendedInfo instanceof IpContent) {
                IpContent iContent = (IpContent) mContentExtendedInfo;
                SimpleDateFormat format = new SimpleDateFormat(
                        "HH:mm:ss' 'dd/MM/yyyy");
                SimpleDateFormat formatTime12Hour = new SimpleDateFormat(
                        "hh:mm a");
                SimpleDateFormat formatTime24Hour = new SimpleDateFormat(
                        "HH:mm");
                SimpleDateFormat formatDate = new SimpleDateFormat("EEE MM/dd");
                SimpleDateFormat formatTime = formatTime24Hour;
                String strTime = "";
                String date = "";
                boolean is24HourFormat;
                try {
                    is24HourFormat = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl().is24HourFormat();
                    if (!is24HourFormat) {
                        formatTime = formatTime12Hour;
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings is24HourFormat");
                }
                try {
                    dateFromStream = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl().getTimeDate()
                            .getCalendar().getTime();
                    strTime = formatTime.format(dateFromStream);
                    date = formatDate.format(dateFromStream);
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strTime");
                    strTime = "00:00";
                    date = "";
                }
                String strNow = "";
                String strNext = "";
                String strNowTime = "";
                String strNextTime = "";
                String strName;
                try {
                    strName = iContent.getName();
                    // if (strName.length() >= 15) {
                    // strName = strName.substring(0, 14) + "...";
                    // }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strName");
                    strName = "";
                }
                String strChannelNumber = String.valueOf(index + 1);
                ArrayList<String> strValues = new ArrayList<String>();
                // Service Name
                strValues.add(strChannelNumber);
                strValues.add(strTime);
                strValues.add(strNow);
                strValues.add(strNext);
                strValues.add(strNowTime);
                strValues.add(strNextTime);
                strValues.add(strName);
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add(date);
                strValues.add("");
                strValues.add("");
                strValues.add("");
                // Add Strings to PageProvider
                setStrValues(strValues);
                setChannelProgressValue(0);
            } else {
                ArrayList<String> strValues = new ArrayList<String>();
                // Service Name
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                strValues.add("");
                imageViewHD.setImageResource(R.drawable.hd_unselect);
                // Add Strings to PageProvider
                setStrValues(strValues);
                setChannelProgressValue(0);
            }
        }
    }

    /** Calculate ChannelInfo Progress */
    public int calculateProgress(EpgEvent now, EpgEvent next) {
        // SetUp Progress
        try {
            Date startTime = now.getStartTime().getCalendar().getTime();
            Date endTime = next.getStartTime().getCalendar().getTime();
            Date currentTime = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().getTimeDate().getCalendar().getTime();
            return Conversions.getEventPassedPercent(startTime, endTime,
                    currentTime);
        } catch (Exception e) {
            Log.i(TAG, "Progress Set Up Method: generateProgress");
        }
        return 0;
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

    /** Prepare ChannelInfo */
    public void setUpChannelInfo() {
        mChannelChangeHandler.getExtendedInfo();
        nowEvent = true;
        setChannelInfoStrings();
        drawInfoBanner(STATE_INFO);
    }

    /**
     * Display channel info or numerous channel info
     * 
     * @param state
     *        - STATE_INFO or STATE_NUMEROUS_CHANGE_CHANNEL
     */
    public void drawInfoBanner(int state) {
        switch (state) {
            case STATE_INFO:
                setChannelInfoInformation();
                break;
            case STATE_NUMEROUS_CHANGE_CHANNEL:
                setChannelNumericInfoInformation();
                break;
            case STATE_CHANGE_CHANNEL:
                setChannelUpDownInfoInformation();
                break;
            default:
                break;
        }
        if (!mViewFlipper.isShown()) {
            mHandlerState = STATE_INFO_BANNER_SHOWN;
            animationIn(mViewFlipper);
        }
        scrollView.setScrooled(new Scrolled() {
            @Override
            public void scrolled() {
                startTimer(ANIMATION_TIME_CHANNEL_INFO, STATE_INFO);
            }

            @Override
            public void detectEnd() {
                detectEnd = true;
            }
        });
        if (state == STATE_INFO) {
            startTimer(ANIMATION_TIME_CHANNEL_INFO, STATE_INFO);
        }
    }

    /** Initialize Volume from Proxy. */
    private void initVolume() {
        // /////////////////////////////////////////////////
        // Init Stream Volume
        // /////////////////////////////////////////////////
        try {
            // MainActivity.service.getSystemControl().getSoundControl()
            // .setActiveSoundMode(VOLUME_MODE_ALL_CHANNEL);
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
        int valVolume = 0;
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
                    imageViewVolumeState
                            .setImageResource(getImageByVolumeLevel(valVolume));
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
                    imageViewVolumeState
                            .setImageResource(getImageByVolumeLevel(valVolume));
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
                    if (MainActivity.service.getSystemControl()
                            .getSoundControl().isMute()) {
                        valVolume = 0;
                        imageViewVolumeState
                                .setImageResource(getImageByVolumeLevel(valVolume));
                    } else {
                        valVolume = currentVolume;
                        imageViewVolumeState
                                .setImageResource(getImageByVolumeLevel(valVolume));
                    }
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
        volumeProgressBar.setProgress(valVolume);
        textViewVolumeValue.setText(Integer.toString(valVolume));
        linearLayoutPlayerInfo.setVisibility(View.GONE);
        mViewFlipper.setVisibility(View.GONE);
        linearLayoutInput.setVisibility(View.GONE);
        if (!linearLayoutVolume.isShown()) {
            previousState = currentState;
            currentState = STATE_VOLUME;
            animationIn(linearLayoutVolume);
        }
        startTimer(ANIMATION_TIME_VOLUME, STATE_VOLUME);
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

    private String calculateTime(long milliSeconds) {
        String strTime = String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes(milliSeconds)));
        return strTime;
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
        setUpChannelInfo();
    }

    public PVRPlayerController getPvrPlayerController() {
        return mPvrPlayerController;
    }

    @Override
    public void updateChannelInfo(int channelIndex) {
        if (!flagZapp) {
            // currentState = STATE_INFO;
            currentState = STATE_CHANNEL_INFO;
            Log.i(TAG, "updateChannelInfo " + channelIndex);
            if (channelIndex == 1) {
                if (!nowEvent) {
                    return;
                }
                nowEvent = false;
            } else if (channelIndex == -1) {
                if (nowEvent) {
                    return;
                }
                nowEvent = true;
            } else if (channelIndex == 0) {
                nowEvent = true;
                mChannelChangeHandler.getExtendedInfo();
            }
            setChannelInfoStrings();
            drawInfoBanner(STATE_INFO);
        } else {
            flagZapp = false;
        }
    }

    @Override
    public void drawInputInfo() {
        Content content = null;
        String videoScanning = "";
        String strFreq = "";
        String strResolution = "";
        try {
            content = MainActivity.service.getContentListControl()
                    .getActiveContent(mDisplayId);
        } catch (Exception e) {
            Log.e(TAG, "There was problem with getting content.", e);
        }
        try {
            if (content != null) {
                int deviceIndex = content.getIndex();
                VideoResolution resolution = MainActivity.service
                        .getInputOutputControl().ioDeviceGetResolution(
                                deviceIndex);
                double frameRate = ((double) (MainActivity.service
                        .getInputOutputControl()
                        .ioDeviceGetFrameRate(deviceIndex)) / 100.00);
                if (MainActivity.service.getInputOutputControl()
                        .ioDeviceGetVideoScanning(deviceIndex) == com.iwedia.dtv.io.VideoScanning.PROGRESSIVE) {
                    videoScanning += "p";
                } else if (MainActivity.service.getInputOutputControl()
                        .ioDeviceGetVideoScanning(deviceIndex) == com.iwedia.dtv.io.VideoScanning.INTERLACED) {
                    videoScanning += "i";
                }
                String strName = content.getName()
                        + " "
                        + mActivity.getResources().getString(
                                R.string.main_menu_content_list_inputs);
                if (resolution.getVideoHeight() != 0) {
                    strFreq = "Frame Rate :" + String.valueOf(frameRate) + "Hz";
                    strResolution = "Resolution : "
                            + String.valueOf(resolution.getVideoWidth()) + "x"
                            + String.valueOf(resolution.getVideoHeight())
                            + videoScanning;
                }
                textViewInputName.setText(strName);
                textViewFrameRate.setText(strFreq);
                textViewResolution.setText(strResolution);
                linearLayoutPlayerInfo.setVisibility(View.INVISIBLE);
                linearLayoutVolume.setVisibility(View.INVISIBLE);
                mViewFlipper.setVisibility(View.INVISIBLE);
                if (!linearLayoutInput.isShown()) {
                    animationIn(linearLayoutInput);
                }
                currentState = STATE_INPUT_INFO;
                startTimer(3000, STATE_INPUT_INFO);
            } else {
                Log.i(TAG, "setChannelStrings: content = null");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateTimeChannelInfo() {
    }

    @Override
    public void scroll(int direction) {
        if (direction == -1) {
            if (detectEnd) {
                return;
            }
            scrollValue += 5;
            scrollView.scrollTo(0, scrollValue);
        } else {
            detectEnd = false;
            if (scrollValue == 0) {
                return;
            }
            scrollValue -= 5;
            scrollView.scrollTo(0, scrollValue);
        }
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
        mTextViewPictureFormatInfo.setText(format);
        if (!mLinearLayoutPictureFormatInfo.isShown()) {
            previousState = currentState;
            currentState = STATE_PICTURE_FORMAT;
            animationIn(mLinearLayoutPictureFormatInfo);
        }
        startTimer(ANIMATION_TIME_PICTURE_FORMAT, STATE_PICTURE_FORMAT);
    }
}
