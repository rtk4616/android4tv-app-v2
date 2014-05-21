package com.iwedia.gui.osd.curleffect;

import android.app.Activity;
import android.graphics.Color;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.comm.system.date_time.IDateTimeSettings;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.epg.EpgEventType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.dtv.types.VideoResolution;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.EPGScheduleDialog;
import com.iwedia.gui.components.dialogs.ServiceModeDialog;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaGlobal;
import com.iwedia.gui.multimedia.controller.MediaController;
import com.iwedia.gui.multimedia.dlna.player.controller.DlnaLocalController;
import com.iwedia.gui.multimedia.pvr.player.controller.PVRPlayerController;
import com.iwedia.gui.multimedia.pvr.record.controller.PVRRecordController;
import com.iwedia.gui.osd.ChannelChangeHandler;
import com.iwedia.gui.osd.Conversions;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.pvr.PVRHandler;
import com.iwedia.gui.util.DateTimeConversions;

import java.util.ArrayList;
import java.util.Date;

public class CurlHandler implements IOSDHandler, OSDGlobal, MultimediaGlobal {
    private final static String TAG = "CurlHandler";
    private static CurlView mCurlView = null;
    private static PageProvider mPageProvider = null;
    // Page Index
    private Activity mActivity = null;
    // ChannelInfo
    private Content mContentExtendedInfo = null;
    // Channel Change
    private ChannelChangeHandler mChannelChangeHandler = null;
    // VideoView Controller
    private MediaController mMediaController = null;
    private PVRHandler mPvrHandler = null;
    private PVRRecordController mPvrRecordController = null;
    private PVRPlayerController mPvrPlayerController = null;
    private DlnaLocalController mDlnaLocalController = null;
    // Volume
    private int currentVolume = -1;
    private static int mCurlHandlerState = CURL_HANDLER_STATE_DO_NOTHING;
    private static int channelInfoIndex = 0;
    // TODO: Applies on main display only
    private static final int mDisplayId = 0;
    private IDateTimeSettings dateTime;
    private Date date;

    public CurlHandler(Activity activity) {
        this.mActivity = activity;
        OSDHandlerHelper.setGlobalHandler((IOSDHandler) this);
    }

    public void init(View view) {
        // SettingUp the CurlSurface
        mPageProvider = new PageProvider(mActivity);
        mCurlView = (CurlView) view.findViewById(R.id.curl);
        mCurlView.setPageProvider(mPageProvider);
        mCurlView.setCurlHandler(this);
        mCurlView.setZOrderOnTop(true);
        mCurlView.setBackgroundColor(Color.rgb(255, 255, 255));
        // Init and Set MediaController
        mMediaController = ((MainActivity) mActivity).getMediaController();
        mCurlView.setmMediaController(mMediaController);
        // Init PVR Handler and PVR Methods
        mPvrHandler = new PVRHandler();
        initControlProviderPVR();
        // Init Channel Change Handler
        mChannelChangeHandler = new ChannelChangeHandler(mActivity);
    }

    /****************************************************************************/
    /** Initialization of References */
    /****************************************************************************/
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

    /** Initialize DLNA or Local Controller */
    public void initControlProviderDLNALocal() {
        if (mDlnaLocalController == null)
            mDlnaLocalController = new DlnaLocalController(mActivity,
                    mMediaController);
        A4TVMultimediaController.setControlProvider(mDlnaLocalController);
    }

    /** Initialize PVR Controller */
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

    /****************************************************************************/
    /****************************************************************************/
    /** CurlEffect */
    /****************************************************************************/
    /** Channel */
    /** CurlEffect - ChangeChannel */
    public void prepareChannelAndChange(int scenario, int channelState) {
        if (isServiceListEmpty()) {
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.empty_list);
        } else {
            // Clear PVR icons
            setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
            mChannelChangeHandler
                    .changeChannelUpDownPreviousContent(channelState);
            try {
                if (scenario != SCENARIO_NUMEROUS_CHANNEL_CHANGE) {
                    Content activeContent = MainActivity.service
                            .getContentListControl().getActiveContent(0);
                    Content content = MainActivity.service
                            .getContentListControl().getContent(
                                    mChannelChangeHandler.getChannelIndex());
                    if (activeContent != null
                            && activeContent.getSourceType() != SourceType.ANALOG
                            && activeContent.getFilterType() != FilterType.INPUTS
                            && content.getSourceType() == SourceType.ANALOG) {
                        /*
                         * Remove WebView from screen and set key mask to 0
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
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // The Strings are prepared before in NumChangeChannel
            if (scenario != SCENARIO_NUMEROUS_CHANNEL_CHANGE) {
                setChannelStrings();
            }
            if (A4TVProgressBarPVR.getControlProviderPVR().isFlagRecord()) {
                mCurlView.setNextState(STATE_INIT);
            }
            mCurlView.startCurlEffect(scenario);
        }
    }

    /** CurlEffect - ChangeChannel by Content from ContentList */
    public void changeChannelByContent(Content content, int displayId) {
        mChannelChangeHandler.syncChannelIndexByContent(content, displayId);
        prepareChannelAndChange(SCENARIO_CHANNEL_CHANGE_BY_CONTENT,
                CHANNEL_CONTENT);
    }

    /** CurlEffect - ChangeChannel by NumPads */
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
            prepareChannelAndChange(SCENARIO_NUMEROUS_CHANNEL_CHANGE,
                    CHANNEL_GO_TO_INDEX);
        }
    }

    /** CurlEffect - Volume */
    /**
     * Change Volume
     * 
     * @param volState
     *        VOLUME_UP,VOLUME_DOWN,VOLUME_MUTE
     * @param startCurl
     *        true => use CurlEffect, false => don't use
     */
    public void volume(int volState, boolean startCurl) {
        setVolume(volState);
        if (startCurl) {
            mCurlView.startCurlEffect(SCENARIO_VOLUME);
        }
    }

    /** CurlEffect - Info */
    public void info() {
        switch (mCurlHandlerState) {
            case PVR_STATE_REW_TIME_SHIFT:
            case PVR_STATE_PLAY_TIME_SHIFT:
            case PVR_STATE_PAUSE_TIME_SHIFT:
            case PVR_STATE_FF_TIME_SHIFT:
            case PVR_STATE_RECORDING: {
                mCurlView.setFlagMultimediaController(true);
                mCurlView.startCurlEffect(SCENARIO_PVR_RECORD);
                break;
            }
            case PVR_STATE_REW_PLAY_BACK:
            case PVR_STATE_PLAY_PLAY_BACK:
            case PVR_STATE_PAUSE_PLAY_BACK:
            case PVR_STATE_FF_PLAY_BACK:
            case CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER: {
                mCurlView.setFlagMultimediaController(true);
                mCurlView.startCurlEffect(SCENARIO_MULTIMEDIA_CONTROLLER);
                break;
            }
            // Default is channel info
            default: {
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
                    Log.i(TAG, "Filter Type: " + lFilterType);
                    if (lFilterType == FilterType.INPUTS) {
                        mCurlView.startCurlEffect(SCENARIO_INPUT_INFO);
                        Log.i(TAG, "START CURL EFFECT INPUT INFO");
                    } else {
                        if (isServiceListEmpty()) {
                            A4TVToast toast = new A4TVToast(mActivity);
                            toast.showToast(R.string.empty_list);
                        } else {
                            if (mCurlView.isFastCallingChannelInfo()) {
                                // mCurlView.setFlagChannelInfo(false);
                                mCurlView.setFlagChannelInfo(true);
                                // Clear PVR icons
                                setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
                                Log.i(TAG, "START CURL EFFECT CHANNEL INFO");
                                mCurlView
                                        .startCurlEffect(SCENARIO_CHANNEL_INFO);
                            }
                        }
                    }
                } else {
                    mCurlView.startCurlEffect(SCENARIO_INFO);
                }
                break;
            }
        }
    }

    @Override
    public void getExtendedInfo() {
        EPGScheduleDialog epgScheduleDialog = MainActivity.activity
                .getDialogManager().getEpgScheduleDialog();
        if (epgScheduleDialog != null) {
            epgScheduleDialog.setUpNewExtendedInfo(channelInfoIndex);
        }
    }

    /** MultiMedia & PVR */
    /** CurlEffect - MultiMedia */
    public boolean multimediaController(boolean openFirst) {
        if (openFirst) {
            if (mCurlView.getCurrentState() != STATE_MULTIMEDIA_CONTROLLER) {
                mCurlView.setFlagMultimediaController(false);
                mCurlView.startCurlEffect(SCENARIO_MULTIMEDIA_CONTROLLER);
                return false;
            } else {
                return true;
            }
        } else {
            mCurlView.setFlagMultimediaController(false);
            mCurlView.startCurlEffect(SCENARIO_MULTIMEDIA_CONTROLLER);
            return false;
        }
    }

    /** CurlEffect - PVR */
    public boolean multimediaControllerPVR(boolean openFirst) {
        if (openFirst) {
            if (mCurlView.getCurrentState() != STATE_PVR
                    && mCurlView.getScenario() != SCENARIO_PVR_RECORD) {
                mCurlView.setFlagMultimediaController(false);
                mCurlView.startCurlEffect(SCENARIO_PVR_RECORD);
                return false;
            } else {
                return true;
            }
        } else {
            mCurlView.setFlagMultimediaController(false);
            mCurlView.startCurlEffect(SCENARIO_PVR_RECORD);
            return false;
        }
    }

    /** Prepare Controller for DLNA or Local PalyBack */
    public void prepareAndStartMultiMediaPlayBackDLNALocal(
            MultimediaContent content, boolean isMusicInfo) {
        // TODO: Applies only on main display
        int displayId = 0;
        mMediaController.stopLiveStream();
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
        if (OSDHandlerHelper.getHandlerState() == PVR_STATE_PLAY_PLAY_BACK) {
            MainActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    A4TVMultimediaController.getControlProvider().play(
                            displayId);
                    /** Show Info */
                    if (A4TVProgressBarPVR.getControlProviderPVR()
                            .isFlagRecord()) {
                        mCurlView.setNextState(STATE_INIT);
                    }
                    MainActivity.activity.getPageCurl().multimediaController(
                            false);
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
                    if (multimediaController(true)) {
                        A4TVMultimediaController.getControlProvider()
                                .moveLeft();
                    }
                }
                break;
            }
            case MainKeyListener.PVR: {
                if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                    if (multimediaControllerPVR(true)) {
                        A4TVProgressBarPVR.getControlProviderPVR().moveLeft();
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
        // TODO: Applies on main display only
        int displayId = 0;
        Log.d(TAG, "multimediaControllerClick");
        switch (MainKeyListener.getAppState()) {
            case MainKeyListener.MULTIMEDIA_PLAYBACK: {
                Log.d(TAG,
                        "Application state -  MainKeyListener.MULTIMEDIA_PLAYBACK");
                if (A4TVMultimediaController.getControlProvider() != null) {
                    if (multimediaController(true) || immediatelyClick) {
                        A4TVMultimediaController.getControlProvider().click(
                                displayId);
                    }
                }
                break;
            }
            case MainKeyListener.PVR: {
                Log.d(TAG, "Application state -  MainKeyListener.PVR");
                if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                    if (multimediaControllerPVR(true) || immediatelyClick) {
                        A4TVProgressBarPVR.getControlProviderPVR().click();
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
                    if (multimediaController(true)) {
                        A4TVMultimediaController.getControlProvider()
                                .moveRight();
                    }
                }
                break;
            }
            case MainKeyListener.PVR: {
                if (A4TVProgressBarPVR.getControlProviderPVR() != null) {
                    if (multimediaControllerPVR(true)) {
                        A4TVProgressBarPVR.getControlProviderPVR().moveRight();
                    }
                }
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * @param content
     * @param index
     *        Index in that list. Radio content in all list will not start from
     *        1, but from real index in all list.
     */
    public void setChannelInfoStringsByIndex(int serviceIndex) {
        String strTime = "";
        String strDate = "";
        EpgEvent now = new EpgEvent();
        EpgEvent next = new EpgEvent();
        dateTime = null;
        date = null;
        try {
            dateTime = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl();
            if (dateTime != null) {
                date = dateTime.getTimeDate().getCalendar().getTime();
            }
            if (date != null) {
                strTime = DateTimeConversions.getTimeSting(date);
            }
            strDate = DateTimeConversions.getDateSting(date);
        } catch (Exception e) {
            Log.i(TAG, "Method: setChannelInfoStrings strTime");
            strTime = "00:00";
        }
        int activeFilterInService = FilterType.ALL;
        try {
            activeFilterInService = MainActivity.service
                    .getContentListControl().getActiveFilterIndex();
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
        mContentExtendedInfo = mChannelChangeHandler
                .getContentExtendedInfoByIndex(serviceIndex);
        if (mContentExtendedInfo != null) {
            channelInfoIndex = serviceIndex;
            Log.e(TAG, "channelInfoIndex: " + (channelInfoIndex + 1));
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
                String strNow;
                Boolean scrambledNow = false;
                Log.d(TAG, "scrambledNow: " + scrambledNow);
                try {
                    strNow = now.getName();
                    scrambledNow = now.getScrambled();
                    if (strNow.trim().length() > 0) {
                        if (strNow.length() >= 30) {
                            strNow = strNow.substring(0, 27) + "...";
                        }
                        strNow = mActivity.getResources().getString(
                                R.string.now)
                                + " " + strNow;
                    } else {
                        strNow = "";
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNow");
                    strNow = "";
                }
                String strNext;
                Boolean scrambledNext = false;
                Log.d(TAG, "scrambledNext: " + scrambledNext);
                try {
                    strNext = next.getName();
                    scrambledNext = next.getScrambled();
                    if (strNext.trim().length() > 0) {
                        if (strNext.length() >= 30) {
                            strNext = strNext.substring(0, 27) + "...";
                        }
                        strNext = mActivity.getResources().getString(
                                R.string.next)
                                + " " + strNext;
                    } else {
                        strNext = "";
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNext");
                    strNext = "";
                }
                String strNowTime;
                try {
                    strNowTime = DateTimeConversions.getTimeSting(now
                            .getStartTime().getCalendar().getTime());
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNowTime");
                    strNowTime = "";
                }
                String strNextTime;
                try {
                    strNextTime = DateTimeConversions.getTimeSting(now
                            .getEndTime().getCalendar().getTime());
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strNextTime");
                    strNextTime = "";
                }
                String strName;
                try {
                    strName = sContent.getName();
                    if (strName.length() >= 15) {
                        strName = strName.substring(0, 14) + "...";
                    }
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
                        strChannelNumber = String.valueOf(channelInfoIndex + 1);
                    } else {
                        if (ConfigHandler.USE_LCN)
                            strChannelNumber = String.valueOf(sContent
                                    .getServiceLCN());
                        else
                            // strChannelNumber =
                            // String.valueOf(sContent.getIndex());
                            strChannelNumber = String
                                    .valueOf(channelInfoIndex + 1);
                    }
                }
                ArrayList<String> strValues = new ArrayList<String>();
                ArrayList<Boolean> scrambledValues = new ArrayList<Boolean>();
                // Service Name
                strValues.add(strChannelNumber);
                strValues.add(strTime);
                strValues.add(strNow);
                strValues.add(strNext);
                strValues.add(strNowTime);
                strValues.add(strNextTime);
                strValues.add(strName);
                strValues.add(strDate);
                scrambledValues.add(scrambledNow);
                scrambledValues.add(scrambledNext);
                // Add Strings to PageProvider
                mPageProvider.setStrValues(strValues);
                mPageProvider.setScrambledValues(scrambledValues);
                mPageProvider.setProgressValue(calculateProgress(now, next));
            } else if (mContentExtendedInfo instanceof IpContent) {
                Log.i(TAG, "IP_CONTENT");
                IpContent iContent = (IpContent) mContentExtendedInfo;
                String strNow = "";
                String strNext = "";
                String strNowTime = "";
                String strNextTime = "";
                String strName;
                try {
                    strName = iContent.getName();
                    if (strName.length() >= 15) {
                        strName = strName.substring(0, 14) + "...";
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strName");
                    strName = "";
                }
                String strChannelNumber = String.valueOf(channelInfoIndex + 1);
                ArrayList<String> strValues = new ArrayList<String>();
                // Service Name
                strValues.add(strChannelNumber);
                strValues.add(strTime);
                strValues.add(strNow);
                strValues.add(strNext);
                strValues.add(strNowTime);
                strValues.add(strNextTime);
                strValues.add(strName);
                strValues.add(strDate);
                // Add Strings to PageProvider
                mPageProvider.setStrValues(strValues);
                mPageProvider.setProgressValue(0);
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
                // Add Strings to PageProvider
                mPageProvider.setStrValues(strValues);
                mPageProvider.setProgressValue(0);
            }
        } else {
            if (activeFilterInService == FilterType.IP_STREAM) {
                Log.i(TAG, "IP_STREAM");
                IpContent iContent = null;
                try {
                    iContent = (IpContent) MainActivity.service
                            .getContentListControl().getContent(serviceIndex);
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                String strNow = "";
                String strNext = "";
                String strNowTime = "";
                String strNextTime = "";
                String strName;
                try {
                    strName = iContent.getName();
                    if (strName.length() >= 15) {
                        strName = strName.substring(0, 14) + "...";
                    }
                } catch (Exception e) {
                    Log.i(TAG, "Method: setChannelInfoStrings strName");
                    strName = "";
                }
                String strChannelNumber = String
                        .valueOf(iContent.getIndex() + 1);
                ArrayList<String> strValues = new ArrayList<String>();
                // Service Name
                strValues.add(strChannelNumber);
                strValues.add(strTime);
                strValues.add(strNow);
                strValues.add(strNext);
                strValues.add(strNowTime);
                strValues.add(strNextTime);
                strValues.add(strName);
                strValues.add(strDate);
                // Add Strings to PageProvider
                mPageProvider.setStrValues(strValues);
                mPageProvider.setProgressValue(0);
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
                    .getDateAndTimeControl().getTimeDate().getCalendar()
                    .getTime();
            return Conversions.getEventPassedPercent(startTime, endTime,
                    currentTime);
        } catch (Exception e) {
            Log.i(TAG, "Progress Set Up Method: generateProgress");
        }
        return 0;
    }

    public/* static */void updateTimeChannelInfo() {
        String strTime = "";
        try {
            dateTime = null;
            date = null;
            try {
                dateTime = MainActivity.service.getSystemControl()
                        .getDateAndTimeControl();
                if (dateTime != null) {
                    date = dateTime.getTimeDate().getCalendar().getTime();
                }
                if (date != null) {
                    strTime = DateTimeConversions.getTimeSting(date);
                }
            } catch (Exception e) {
                Log.i(TAG, "Method: setChannelInfoStrings strTime");
                strTime = "00:00";
            }
        } catch (Exception e) {
            Log.i(TAG, "Method: updateTimeChannelInfo strTime");
            strTime = "00:00";
        }
        if (mPageProvider != null) {
            ArrayList<String> values = mPageProvider.getStrValues();
            if (values != null) {
                if (values.size() > 2) {
                    values.set(1, strTime);
                }
            }
        }
    }

    /**
     * Variable "direct" is true if channel is changed from content list
     */
    public void setInputInfoStrings() {
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
                ArrayList<String> strValues = new ArrayList<String>();
                strValues.add(strName);
                strValues.add(strFreq);
                strValues.add(strResolution);
                mPageProvider.setStrValues(strValues);
                mPageProvider.setChannelIconPath("");
            } else {
                Log.i(TAG, "setChannelStrings: content = null");
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Variable "direct" is true if channel is changed from content list
     */
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
                        // currentChannel = String.valueOf(content.getIndex());
                        currentChannel = String.valueOf(content.getIndex() + 1);
                }
            }
            String strName = "";
            if (content instanceof ServiceContent) {
                strName = content.getName();
                if (strName.length() >= 15) {
                    strName = strName.substring(0, 14) + "...";
                }
            } else if (content instanceof IpContent) {
                strName = content.getName();
                if (strName.length() >= 15) {
                    strName = strName.substring(0, 14) + "...";
                }
            } else if (content.getFilterType() == FilterType.INPUTS) {
                currentChannel = mActivity.getResources().getString(
                        R.string.main_menu_content_list_inputs);
                strName = content.getName();
            }
            ArrayList<String> strValues = new ArrayList<String>();
            strValues.add(currentChannel);
            strValues.add(strName);
            mPageProvider.setStrValues(strValues);
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
        mPageProvider.setStrValues(strValues);
    }

    public void setCurlPictures() {
        Content content = mChannelChangeHandler.getActiveContent();
        if (content != null) {
            ArrayList<Integer> imageIds = new ArrayList<Integer>();
            if (content instanceof ServiceContent) {
                ServiceContent sContent = (ServiceContent) content;
                if (sContent.isScrambled()) {
                    imageIds.add(R.drawable.scrambled_channel);
                }
            }
            try {
                int sub = ((MainActivity) mActivity).service
                        .getSubtitleControl().getSubtitleTrackCount();
                Log.i(TAG, "SUBTITLE TRACK COUNT: " + sub);
                if (sub > 0) {
                    imageIds.add(R.drawable.sub);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Coud not get Subtitle track index.", e);
            }
            try {
                int teltxt = ((MainActivity) mActivity).service
                        .getTeletextControl().getTeletextTrackCount();
                Log.i(TAG, "TELETEXT TRACK COUNT: " + teltxt);
                if (teltxt > 0) {
                    imageIds.add(R.drawable.ttxt);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Coud not get Teletext track index.", e);
            }
            // TODO: ZORANA: determine is HD according to dtv.ServiceType
            // if(sContent.isHD()) {
            // imageIds.add(R.drawable.hd);
            // }
            try {
                int state = MainActivity.service.getHbbTvControl()
                        .getHbbState();
                Log.d(TAG, "HBB recieved state" + state);
                if (state == 0) {
                    Log.d(TAG, "HBB present and enabled");
                    imageIds.add(R.drawable.hbb_select);
                } else if (state == 2) {
                    Log.d(TAG, "HBB present and disabled");
                    imageIds.add(R.drawable.hbb_unselect);
                } else {
                    Log.d(TAG, "HBB not present");
                    imageIds.add(R.drawable.hbb_unselect_red);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (!content.getImage().trim().equals("-1")) {
                mPageProvider.setChannelIconPath(content.getImage());
            } else {
                mPageProvider.setChannelIconPath("");
            }
            mPageProvider.setImageIds(imageIds);
        } else {
            mPageProvider.setChannelIconPath("");
            mPageProvider.setImageIds(null);
        }
    }

    /** Prepare ChannelInfo */
    public void setUpChannelInfoByIndex(int index) {
        setChannelInfoStringsByIndex(index);
        setCurlPictures();
    }

    /****************************************************************************/
    /****************************************************************************/
    /** Handle Proxy-Comedia */
    /****************************************************************************/
    /** Change Volume in Comedia and Prepare Strings for CurlEffect */
    public void setVolume(int volume) {
        if (currentVolume == -1) {
            initVolume();
        }
        int valVolume = 0;
        int maxVolume;
        boolean isVolumeFixed;
        int volumeFixedLevel;
        try {
            // Change volume
            switch (volume) {
                case VOLUME_UP: {
                    /* If volume is fixed do not allow change */
                    isVolumeFixed = (boolean) MainActivity.service
                            .getServiceMode().getVolumeFixed();
                    if (isVolumeFixed) {
                        /* Set fixed level volume (read from service menu) */
                        volumeFixedLevel = MainActivity.service
                                .getServiceMode().getVolumeFixedLevel();
                        valVolume = volumeFixedLevel;
                    } else {
                        // Get Current Volume from Stream
                        valVolume = (int) MainActivity.service
                                .getSystemControl().getSoundControl()
                                .getVolume();
                        valVolume += 1;
                    }
                    /* Get value of max allowed volume */
                    maxVolume = (int) MainActivity.service.getServiceMode()
                            .getMaxVolume();
                    if (maxVolume < 0) {
                        maxVolume = 100;
                    }
                    if (valVolume > maxVolume) {
                        valVolume = maxVolume;
                    }
                    MainActivity.service.getSystemControl().getSoundControl()
                            .setVolume(valVolume);
                    currentVolume = valVolume;
                    break;
                }
                case VOLUME_DOWN: {
                    /* If volume is fixed do not allow change */
                    isVolumeFixed = (boolean) MainActivity.service
                            .getServiceMode().getVolumeFixed();
                    if (isVolumeFixed) {
                        /* Set fixed level volume (read from service menu) */
                        volumeFixedLevel = MainActivity.service
                                .getServiceMode().getVolumeFixedLevel();
                        valVolume = volumeFixedLevel;
                    } else {
                        // Get Current Volume from Stream
                        valVolume = (int) MainActivity.service
                                .getSystemControl().getSoundControl()
                                .getVolume();
                        valVolume -= 1;
                    }
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
                    if (MainActivity.service.getSystemControl()
                            .getSoundControl().isMute()) {
                        valVolume = 0;
                    } else {
                        valVolume = currentVolume;
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
        ArrayList<String> strValues = new ArrayList<String>();
        strValues.add(String.valueOf(valVolume));
        mPageProvider.setStrValues(strValues);
    }

    public/* static */boolean isServiceListEmpty() {
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
    public/* static */void channelIsZapped(boolean success) {
        if (success) {
            mCurlView.setFlagZapChannel(true);
        } else {
            mCurlView.setFlagZapChannel(false);
        }
    }

    /** Getters and Setters */
    public/* static */void setHandlerState(int mCurlHandlerState) {
        CurlHandler.mCurlHandlerState = mCurlHandlerState;
    }

    public/* static */int getHandlerState() {
        return mCurlHandlerState;
    }

    public CurlView getCurlView() {
        return mCurlView;
    }

    public PageProvider getPageProvider() {
        return mPageProvider;
    }

    public ChannelChangeHandler getChannelChangeHandler() {
        return mChannelChangeHandler;
    }

    public Content getContentExtendedInfo() {
        return mContentExtendedInfo;
    }

    public PVRPlayerController getPvrPlayerController() {
        return mPvrPlayerController;
    }

    @Override
    public void setAnimationTimeChannelInfo(int i) {
        mCurlView.setAnimationTimeChannelInfo(i);
    }

    @Override
    public int getCurrentState() {
        return mCurlView.getCurrentState();
    }

    @Override
    public void startCurlEffect(int scenarioDoNothing) {
        mCurlView.startCurlEffect(scenarioDoNothing);
    }

    @Override
    public boolean isFlagChannelInfo() {
        return mCurlView.isFlagChannelInfo();
    }

    @Override
    public void setUpNewChannelInfo(int index) {
        // refresh only selected channel info
        if (channelInfoIndex == index) {
            mCurlView.setUpNewChannelInfoByIndex(index);
        }
    }

    @Override
    public void updateChannelInfo(int channelIndex) {
        // TODO Auto-generated method stub
    }

    @Override
    public void drawInfoBanner(int state) {
        // TODO Auto-generated method stub
    }

    @Override
    public void updatePlayingTime(int mStartTime, int mEndTime,
            int mPlayingTime, int progressValue) {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateTimeShiftPlayingTime(int mPlayingTime, int progressValue) {
        // TODO Auto-generated method stub
    }

    @Override
    public void drawInputInfo() {
        // TODO Auto-generated method stub
    }

    @Override
    public void scroll(int direction) {
        // TODO Auto-generated method stub
    }

    @Override
    public void getPreviousChannelInfo() {
        channelInfoIndex = mChannelChangeHandler
                .getPreviousContentIndex(channelInfoIndex);
        mCurlView.setUpNewChannelInfoByIndex(channelInfoIndex);
    }

    @Override
    public void getNextChannelInfo() {
        channelInfoIndex = mChannelChangeHandler
                .getNextContentIndex(channelInfoIndex);
        mCurlView.setUpNewChannelInfoByIndex(channelInfoIndex);
    }

    @Override
    public int getChannelInfoIndex() {
        return channelInfoIndex;
    }

    @Override
    public void showPictureFormat(String format) {
        ArrayList<String> strValues = new ArrayList<String>();
        strValues.add(String.valueOf(format));
        mPageProvider.setStrValues(strValues);
        mCurlView.startCurlEffect(SCENARIO_PICTURE_FORMAT);
    }
}
