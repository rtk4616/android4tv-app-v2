package com.iwedia.gui.osd.curleffect;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVProgressBarPVR.ControlProviderPVR;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.controller.MediaController;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for creating Curl Surface
 */
public class CurlView extends GLSurfaceView implements CurlRenderer.Observer,
        OSDGlobal {
    private static final String TAG = "CurlView";
    /** States for State Machine. */
    private int currentState = STATE_INIT;
    private int nextState = STATE_INIT;
    /** Scenario for State Machine. */
    private int scenario = SCENARIO_DO_NOTHING;
    /** MediaController Reference. */
    private MediaController mMediaController = null;
    /** CurlHandler Reference. */
    private CurlHandler mCurlHandler = null;
    /** Curl Direction Animation. We are flipping none, left or right page. */
    private final int CURL_NONE = 0;
    private final int CURL_RIGHT = 1;
    /** Duration of Curling Animation */
    private int ANIMATION_TIME = 0;
    /** Change Duration of Animation Time Channel Info, loading from Preffs */
    private int animationTimeChannelInfo = ANIMATION_TIME_CHANNEL_INFO;
    /** Create First Time CurlPage */
    private boolean curlFirstTimeStart = true;
    private PointF mCurlDir = new PointF();
    private PointF mCurlPos = new PointF();
    private int mCurlState = CURL_NONE;
    /** Current bitmap index. This is always showed as front of right page. */
    private int mCurrentIndex = 0;
    private RectF pageRect = null;;
    /** Start position for dragging. */
    private PointF mDragStartPos = new PointF();
    /** Bitmap size. These are updated from renderer once it's initialized. */
    private int mPageBitmapHeight = -1;
    private int mPageBitmapWidth = -1;
    /**
     * Page meshes. Left and right meshes are 'static' while curl is used to
     * show page flipping.
     */
    private CurlMesh mPageCurl = null;
    private PageProvider mPageProvider = null;
    private CurlMesh mPage = null;
    private PointF mPos = new PointF();
    private CurlRenderer mRenderer = null;
    private SizeChangedObserver mSizeChangedObserver = null;
    /** Flag ForceStop for changing Scenario. */
    /**
     *
     */
    private boolean forceStopFlag = false;
    /** Flag for fast calling channel info. */
    private boolean fastCallingChannelInfo = true;
    /** Flag for Curling. */
    private boolean curlPageForNumChannelChange = true;
    /** Flags for fast channel changing. */
    private boolean fastlChangeChannelFlag = false;
    private boolean prepareForChannelChange = false;
    /** Flag from Info Button. */
    /** Enable - Disable ChannelInfo Auto Closing */
    private boolean flagChannelInfo = false;
    private boolean flagRenderPagesContinuously = false;
    private boolean flagMultimediaController = false;
    /** Flags for Curl End Callback */
    private boolean flagZappCallBack = false;
    private boolean flagCurlCallBack = false;
    /** Flags for ChangeingPosition Rendering */
    private boolean isCurled = false;
    /** LastAppState for Info */
    private int mLastAppState = -1;
    /** Fields for Smooth Curling. */
    private boolean enableRendering = false;
    private long mAnimationDurationTimeSmooth = 1000;
    private PointF mAnimationSourceSmooth = new PointF();
    private PointF mAnimationTargetSmooth = new PointF();
    private long mAnimationStartTimeSmooth;
    /** TimerTask & Timer. */
    private Timer mTimer = null;
    private TimerTask mStateTimerTask = null;
    private TimerTask mRendererTimerTask = null;
    /** Handle UI Thread */
    private Handler mHandler = null;
    /** Static Reference to This Class. */
    private CurlView mCurlView = null;

    public CurlView(Context ctx) {
        super(ctx);
        init(ctx);
        mCurlView = this;
    }

    public CurlView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        init(ctx);
        mCurlView = this;
    }

    public CurlView(Context ctx, AttributeSet attrs, int defStyle) {
        this(ctx, attrs);
        mCurlView = this;
    }

    private void init(Context ctx) {
        if (ConfigHandler.CURL_GRAPHIC_QUALITY) {
            /** With MultisampleConfigChooser */
            setEGLConfigChooser(new MultisampleConfigChooser());
        } else {
            /** Without MultisampleConfigChooser */
            setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        }
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        this.setZOrderOnTop(true);
        mRenderer = new CurlRenderer(this);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mPage = new CurlMesh(10);
        mPageCurl = new CurlMesh(10);
        mPage.setFlipTexture(false);
        mTimer = new Timer();
        mHandler = new Handler();
        scenario = SCENARIO_DO_NOTHING;
    }

    /** Get currentIndex of Page that is rendering. */
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /** CURL EFFECT */
    public void onDrawFrame() {
        if (enableRendering) {
            smoothCurl();
        }
    }

    public void onPageSizeChanged(int width, int height) {
        mPageBitmapWidth = width;
        mPageBitmapHeight = height;
        updatePages();
        requestRender();
    }

    public void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        requestRender();
        if (mSizeChangedObserver != null) {
            mSizeChangedObserver.onSizeChanged(w, h);
        }
        // Crate Curl Bitmap Textures
        mPageProvider.getCreateBitmaps().createBackgroundLayer(w, h);
        mPageProvider.getCreateBitmaps().createForegroundLayer(w, h);
    }

    public void onSurfaceCreated() {
        mPage.resetTexture();
        mPageCurl.resetTexture();
    }

    // //////////////////////////////////////////////////////////////////// //
    // State Machine //
    // //////////////////////////////////////////////////////////////////// //
    /** Starting Curl Effect from RCU */
    public void startCurlEffect(int scenario) {
        if (acceptScenario(scenario)) {
            this.scenario = scenario;
            if (mStateTimerTask != null) {
                // BruteForce Stop animation
                if (mStateTimerTask.cancel()) {
                    forceStopFlag = true;
                    changeToNextState();
                } else {
                    startStateTimer(0);
                }
            } else {
                startStateTimer(0);
            }
        } else {
            Log.i(TAG, "In State: " + currentState + " Scenario: " + scenario
                    + " will not be accepted!");
        }
    }

    private void startStateTimer(final int milliseconds) {
        if (mTimer != null) {
            mTimer.purge();
            if (mStateTimerTask != null) {
                mStateTimerTask.cancel();
            }
            mStateTimerTask = null;
            mStateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    changeToNextState();
                }
            };
            mTimer.schedule(mStateTimerTask, milliseconds);
        }
    }

    private void changeToNextState() {
        if (currentState == STATE_NUMEROUS_CHANGE_CHANNEL) {
            if (!mCurlHandler.getChannelChangeHandler().channelExistence(
                    scenario)) {
                if (A4TVProgressBarPVR.getControlProviderPVR().isFlagRecord()) {
                    MainKeyListener.setAppState(MainKeyListener.PVR);
                    OSDHandlerHelper.setHandlerState(PVR_STATE_RECORDING);
                    MainActivity.activity.getPageCurl()
                            .multimediaControllerPVR(false);
                }
                nextState = STATE_DEINIT;
            }
        } else if (currentState == STATE_DEINIT) {
            getHandler().post(mRunnableShowToast);
        }
        getHandler().post(mRunnableStateChanger);
    }

    private boolean acceptScenario(int scenario) {
        switch (currentState) {
            case STATE_CHANGE_CHANNEL: {
                switch (scenario) {
                    case SCENARIO_CHANNEL_INFO:
                    case SCENARIO_INPUT_INFO:
                    case SCENARIO_VOLUME:
                    case SCENARIO_PVR_RECORD:
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE:
                    case SCENARIO_PICTURE_FORMAT: {
                        return false;
                    }
                    case SCENARIO_CHANNEL_CHANGE:
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        return true;
                    }
                    default: {
                        return true;
                    }
                }
            }
            case STATE_NUMEROUS_CHANGE_CHANNEL: {
                switch (scenario) {
                    case SCENARIO_CHANNEL_INFO:
                    case SCENARIO_VOLUME:
                    case SCENARIO_PVR_RECORD:
                    case SCENARIO_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT:
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_PICTURE_FORMAT: {
                        return false;
                    }
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        return true;
                    }
                    default: {
                        return true;
                    }
                }
            }
            default: {
                return true;
            }
        }
    }

    private void forceStopHandle() {
        // Channel Info Stay Up
        switch (currentState) {
            case STATE_MULTIMEDIA_CONTROLLER:
            case STATE_VOLUME:
            case STATE_PVR:
            case STATE_NUMEROUS_CHANGE_CHANNEL:
            case STATE_INFO:
            case STATE_INPUT_INFO:
            case STATE_CHANGE_CHANNEL:
            case STATE_PICTURE_FORMAT:
            case STATE_CHANNEL_INFO: {
                if (flagChannelInfo) {
                    flagChannelInfo = false;
                }
            }
            case STATE_DEINIT:
            default:
                break;
        }
        switch (currentState) {
            case STATE_CHANNEL_INFO: {
                switch (scenario) {
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = true;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = false;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_CHANNEL_INFO: {
                        enableRendering = false;
                        nextState = STATE_DEINIT;
                        break;
                    }
                    case SCENARIO_INFO: {
                        enableRendering = false;
                        nextState = STATE_INFO;
                        break;
                    }
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        enableRendering = false;
                        nextState = STATE_NUMEROUS_CHANGE_CHANNEL;
                        // Setting Flag For Curling Numerous Channel Change
                        curlPageForNumChannelChange = true;
                        break;
                    }
                    case SCENARIO_VOLUME: {
                        enableRendering = false;
                        nextState = STATE_VOLUME;
                        break;
                    }
                    case SCENARIO_PVR_RECORD: {
                        enableRendering = false;
                        nextState = STATE_PVR;
                        break;
                    }
                    case SCENARIO_PICTURE_FORMAT: {
                        enableRendering = false;
                        nextState = STATE_PICTURE_FORMAT;
                        break;
                    }
                    default: {
                        Log.i(TAG,
                                "CURLE DEFAULT END FORCE STOP STATE_CHANNEL_INFO");
                        break;
                    }
                }
                break;
            }
            case STATE_INPUT_INFO: {
                switch (scenario) {
                    case SCENARIO_INPUT_INFO: {
                        enableRendering = false;
                        isCurled = true;
                        nextState = STATE_DEINIT;
                        break;
                    }
                    default: {
                        Log.i(TAG,
                                "CURLE DEFAULT END FORCE STOP STATE_INPUT_INFO");
                        break;
                    }
                }
                break;
            }
            case STATE_INFO: {
                switch (scenario) {
                    case SCENARIO_INFO: {
                        enableRendering = false;
                        nextState = STATE_DEINIT;
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END FORCE STOP STATE_INFO");
                        break;
                    }
                }
                break;
            }
            case STATE_VOLUME: {
                switch (scenario) {
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        nextState = STATE_NUMEROUS_CHANGE_CHANNEL;
                        // Setting Flag For Curling Numerous Channel Change
                        curlPageForNumChannelChange = true;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_VOLUME: {
                        nextState = STATE_VOLUME;
                        isCurled = true;
                        break;
                    }
                    case SCENARIO_CHANNEL_INFO: {
                        enableRendering = false;
                        nextState = STATE_CHANNEL_INFO;
                        break;
                    }
                    case SCENARIO_INPUT_INFO: {
                        enableRendering = false;
                        nextState = STATE_INPUT_INFO;
                        break;
                    }
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = true;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = false;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_PVR_RECORD: {
                        nextState = STATE_PVR;
                        isCurled = false;
                        break;
                    }
                    case SCENARIO_MULTIMEDIA_CONTROLLER: {
                        enableRendering = false;
                        nextState = STATE_MULTIMEDIA_CONTROLLER;
                        break;
                    }
                    case SCENARIO_PICTURE_FORMAT: {
                        enableRendering = false;
                        nextState = STATE_PICTURE_FORMAT;
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END FORCE STOP STATE_VOLUME");
                        break;
                    }
                }
                break;
            }
            case STATE_NUMEROUS_CHANGE_CHANNEL: {
                switch (scenario) {
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        nextState = STATE_NUMEROUS_CHANGE_CHANNEL;
                        break;
                    }
                    // On OK Button ChangeChannel
                    case SCENARIO_DO_NOTHING: {
                        Log.i(TAG, "Change Channel On Ok Button NextState: "
                                + nextState);
                        this.scenario = SCENARIO_NUMEROUS_CHANNEL_CHANGE;
                        if (nextState != STATE_DEINIT) {
                            nextState = STATE_CHANGE_CHANNEL;
                        }
                        break;
                    }
                    default: {
                        Log.i(TAG,
                                "CURLE DEFAULT END FORCE STOP STATE_NUMEROUS_CHANGE_CHANNE");
                        break;
                    }
                }
                break;
            }
            case STATE_CHANGE_CHANNEL: {
                switch (scenario) {
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = true;
                        isCurled = true;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = false;
                        break;
                    }
                    default: {
                        Log.i(TAG,
                                "CURLE DEFAULT END FORCE STOP STATE_CHANGE_CHANNEL");
                        break;
                    }
                }
                break;
            }
            case STATE_PVR: {
                switch (scenario) {
                    case SCENARIO_PVR_RECORD: {
                        enableRendering = false;
                        if (flagMultimediaController) {
                            nextState = STATE_DEINIT;
                            flagMultimediaController = false;
                        } else {
                            nextState = STATE_PVR;
                            isCurled = true;
                        }
                        break;
                    }
                    case SCENARIO_VOLUME: {
                        flagRenderPagesContinuously = false;
                        enableRendering = false;
                        nextState = STATE_VOLUME;
                        break;
                    }
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        flagRenderPagesContinuously = false;
                        nextState = STATE_NUMEROUS_CHANGE_CHANNEL;
                        // Setting Flag For Curling Numerous Channel Change
                        curlPageForNumChannelChange = true;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        flagRenderPagesContinuously = false;
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = true;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        flagRenderPagesContinuously = false;
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = false;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_PICTURE_FORMAT: {
                        flagRenderPagesContinuously = false;
                        enableRendering = false;
                        nextState = STATE_PICTURE_FORMAT;
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END FORCE STOP STATE_PVR");
                        break;
                    }
                }
                break;
            }
            case STATE_MULTIMEDIA_CONTROLLER: {
                switch (scenario) {
                    case SCENARIO_MULTIMEDIA_CONTROLLER: {
                        enableRendering = false;
                        if (flagMultimediaController) {
                            nextState = STATE_DEINIT;
                            flagMultimediaController = false;
                        } else {
                            nextState = STATE_MULTIMEDIA_CONTROLLER;
                            isCurled = false;
                        }
                        break;
                    }
                    case SCENARIO_VOLUME: {
                        // Stop Rendering Second Page
                        flagRenderPagesContinuously = false;
                        enableRendering = false;
                        nextState = STATE_VOLUME;
                        break;
                    }
                    case SCENARIO_PICTURE_FORMAT: {
                        flagRenderPagesContinuously = false;
                        enableRendering = false;
                        nextState = STATE_PICTURE_FORMAT;
                        break;
                    }
                    default: {
                        Log.i(TAG,
                                "CURLE DEFAULT END FORCE STOP STATE_MULTIMEDIA_CONTROLLER");
                        break;
                    }
                }
                break;
            }
            case STATE_PICTURE_FORMAT: {
                switch (scenario) {
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        nextState = STATE_NUMEROUS_CHANGE_CHANNEL;
                        // Setting Flag For Curling Numerous Channel Change
                        curlPageForNumChannelChange = true;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_VOLUME: {
                        nextState = STATE_VOLUME;
                        isCurled = false;
                        break;
                    }
                    case SCENARIO_CHANNEL_INFO: {
                        enableRendering = false;
                        nextState = STATE_CHANNEL_INFO;
                        break;
                    }
                    case SCENARIO_INPUT_INFO: {
                        enableRendering = false;
                        nextState = STATE_INPUT_INFO;
                        break;
                    }
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = true;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = false;
                        enableRendering = false;
                        break;
                    }
                    case SCENARIO_PVR_RECORD: {
                        nextState = STATE_PVR;
                        isCurled = false;
                        break;
                    }
                    case SCENARIO_MULTIMEDIA_CONTROLLER: {
                        enableRendering = false;
                        nextState = STATE_MULTIMEDIA_CONTROLLER;
                        break;
                    }
                    case SCENARIO_PICTURE_FORMAT: {
                        nextState = STATE_PICTURE_FORMAT;
                        isCurled = true;
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END FORCE STOP STATE_VOLUME");
                        break;
                    }
                }
                break;
            }
            case STATE_DEINIT: {
                switch (scenario) {
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = true;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        nextState = STATE_CHANGE_CHANNEL;
                        fastlChangeChannelFlag = false;
                        break;
                    }
                    case SCENARIO_CHANNEL_INFO: {
                        nextState = STATE_CHANNEL_INFO;
                        break;
                    }
                    case SCENARIO_INPUT_INFO: {
                        nextState = STATE_INPUT_INFO;
                        break;
                    }
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        nextState = STATE_NUMEROUS_CHANGE_CHANNEL;
                        break;
                    }
                    case SCENARIO_VOLUME: {
                        nextState = STATE_VOLUME;
                        break;
                    }
                    case SCENARIO_PVR_RECORD: {
                        nextState = STATE_PVR;
                        break;
                    }
                    case SCENARIO_MULTIMEDIA_CONTROLLER: {
                        nextState = STATE_MULTIMEDIA_CONTROLLER;
                        break;
                    }
                    case SCENARIO_PICTURE_FORMAT: {
                        nextState = STATE_PICTURE_FORMAT;
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END FORCE STOP STATE_DEINIT");
                        break;
                    }
                }
                break;
            }
            default: {
                Log.i(TAG, "CURLE DEFAULT END FORCE STOP");
                break;
            }
        }
    }

    private void stateChanger() {
        // Handle Force Stop
        if (forceStopFlag) {
            forceStopHandle();
            forceStopFlag = false;
        }
        switch (nextState) {
            case STATE_INIT: {
                currentState = STATE_INIT;
                nextState = currentState;
                ANIMATION_TIME = ANIMATION_TIME_INIT;
                switch (scenario) {
                    case SCENARIO_CHANNEL_INFO: {
                        fastCallingChannelInfo = false;
                        nextState = STATE_CHANNEL_INFO;
                        break;
                    }
                    case SCENARIO_INPUT_INFO: {
                        nextState = STATE_INPUT_INFO;
                        break;
                    }
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE: {
                        nextState = STATE_NUMEROUS_CHANGE_CHANNEL;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE: {
                        fastlChangeChannelFlag = true;
                        nextState = STATE_CHANGE_CHANNEL;
                        break;
                    }
                    case SCENARIO_INFO: {
                        nextState = STATE_INFO;
                        break;
                    }
                    case SCENARIO_VOLUME: {
                        nextState = STATE_VOLUME;
                        break;
                    }
                    case SCENARIO_PVR_RECORD: {
                        nextState = STATE_PVR;
                        break;
                    }
                    case SCENARIO_MULTIMEDIA_CONTROLLER: {
                        nextState = STATE_MULTIMEDIA_CONTROLLER;
                        break;
                    }
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE: {
                        fastlChangeChannelFlag = true;
                        nextState = STATE_CHANGE_CHANNEL;
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT: {
                        fastlChangeChannelFlag = false;
                        nextState = STATE_CHANGE_CHANNEL;
                        break;
                    }
                    case SCENARIO_PICTURE_FORMAT: {
                        nextState = STATE_PICTURE_FORMAT;
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END STATE_INIT");
                        // Clear channel index because scenario is changed and
                        // it will
                        // never end the previous scenario
                        mCurlHandler.getChannelChangeHandler()
                                .flushChannelIndexBuffer();
                        mCurlHandler.getChannelChangeHandler()
                                .flushSecretKeyBuffer();
                        mCurlHandler.getChannelChangeHandler()
                                .flushMajorMinorChannelIndexBuffer();
                        return;
                    }
                }
                states(STATE_INIT, 0);
                startStateTimer(ANIMATION_TIME);
                break;
            }
            case STATE_CHANNEL_INFO: {
                currentState = STATE_CHANNEL_INFO;
                nextState = currentState;
                switch (scenario) {
                    case SCENARIO_CHANNEL_INFO: {
                        // SetUP New Channel Info
                        setUpNewChannelInfoByIndex(mCurlHandler
                                .getChannelChangeHandler().getChannelIndex());
                        states(STATE_CHANNEL_INFO, 500);
                        ANIMATION_TIME = animationTimeChannelInfo;
                        nextState = STATE_DEINIT;
                        fastCallingChannelInfo = true;
                        // Channel Info Stay Up
                        if (flagChannelInfo) {
                            ANIMATION_TIME = ANIMATION_TIME_INIT;
                            forceStopFlag = true;
                            startRendererTimer(TIMERTASK_WHAT_CHANNEL_INFO,
                                    TIMERTASK_TIME_CHANNEL_INFO);
                        } else {
                            flagChannelInfo = false;
                            startStateTimer(ANIMATION_TIME);
                        }
                        break;
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT:
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        // Blank Screen
                        try {
                            MainActivity.service.getVideoControl().videoBlank(
                                    0, true);
                        } catch (Exception e) {
                            Log.i(TAG, "Blank Screen Exception");
                            e.printStackTrace();
                        }
                        // ReRender FirstSide of First Page to Be Transparent
                        mPageProvider.updateFrontSideOfFirstPage(
                                mPageCurl.getTexturePage(), getWidth(),
                                getHeight(), STATE_CHANNEL_INFO);
                        if (!A4TVProgressBarPVR.getControlProviderPVR()
                                .isFlagRecord()) {
                            // SetUP New ChannelInfo or Basic Info
                            setUpNewChannelInfoByIndex(mCurlHandler
                                    .getChannelChangeHandler()
                                    .getChannelIndex());
                        } else {
                            updateBackSideOfFirstPage(true); // clear front side
                                                             // curl
                                                             // page
                        }
                        states(STATE_CHANNEL_INFO,
                                ConfigHandler.CURL_TIME_MILIS_INT);
                        ANIMATION_TIME = animationTimeChannelInfo;
                        nextState = STATE_DEINIT;
                        startStateTimer(ANIMATION_TIME);
                        // if app be in record state, and user change channel,
                        // app must
                        // be in record state
                        if (A4TVProgressBarPVR.getControlProviderPVR()
                                .isFlagRecord()) {
                            MainKeyListener.setAppState(MainKeyListener.PVR);
                            OSDHandlerHelper
                                    .setHandlerState(PVR_STATE_RECORDING);
                            MainActivity.activity.getPageCurl()
                                    .multimediaControllerPVR(false);
                            ControlProviderPVR
                                    .setFileDescription("Recording in progress");
                            ControlProviderPVR.setFileName("");
                        }
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END STATE_CHANNEL_INFO");
                        break;
                    }
                }
                break;
            }
            case STATE_INPUT_INFO: {
                currentState = STATE_INPUT_INFO;
                nextState = currentState;
                switch (scenario) {
                    case SCENARIO_INPUT_INFO: {
                        // Get Strings from Content
                        mCurlHandler.setInputInfoStrings();
                        // ReRender second page
                        updateFrontSideOfSecondPage(STATE_INPUT_INFO);
                        if (!isCurled) {
                            updateBackSideOfFirstPage(false);
                            states(STATE_CHANNEL_INFO, 500);
                            isCurled = false;
                        } else {
                            isCurled = false;
                        }
                        ANIMATION_TIME = ANIMATION_TIME_CHANNEL_INFO;
                        nextState = STATE_DEINIT;
                        startStateTimer(ANIMATION_TIME);
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END STATE_INFO");
                        break;
                    }
                }
                break;
            }
            case STATE_INFO: {
                currentState = STATE_INFO;
                nextState = currentState;
                switch (scenario) {
                    case SCENARIO_INFO: {
                        /** Save last AppState and set New (INFO) */
                        mLastAppState = MainKeyListener.getAppState();
                        MainKeyListener.setAppState(MainKeyListener.INFO);
                        // Hide Dialogs Reverse
                        for (int i = A4TVDialog.getListOfDialogs().size() - 1; i >= 0; i--) {
                            A4TVDialog.getListOfDialogs().get(i).hide();
                        }
                        // ReRender Pages
                        // Clear Page
                        updateBackSideOfFirstPage(true);
                        // mPageProvider.updateFrontSideOfFirstPage(
                        // mPageCurl.getTexturePage(), getWidth(), getHeight());
                        mPageProvider.updateFrontSideOfSecondPage(
                                mPage.getTexturePage(), getWidth(),
                                getHeight(), STATE_INFO);
                        states(STATE_INFO, 0);
                        ANIMATION_TIME = ANIMATION_TIME_INFO;
                        nextState = STATE_DEINIT;
                        startStateTimer(ANIMATION_TIME);
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END STATE_INFO");
                        break;
                    }
                }
                break;
            }
            case STATE_VOLUME: {
                currentState = STATE_VOLUME;
                nextState = currentState;
                if (scenario == SCENARIO_VOLUME) {
                    // ReRender second page
                    updateFrontSideOfSecondPage(STATE_VOLUME);
                    if (!isCurled) {
                        updateBackSideOfFirstPage(true);
                        states(STATE_VOLUME, 500);
                        isCurled = false;
                    } else {
                        isCurled = false;
                    }
                    ANIMATION_TIME = ANIMATION_TIME_VOLUME;
                    startStateTimer(ANIMATION_TIME);
                    nextState = STATE_DEINIT;
                } else {
                    Log.i(TAG, "CURLE DEFAULT END STATE_VOLUME");
                }
                break;
            }
            case STATE_NUMEROUS_CHANGE_CHANNEL: {
                currentState = STATE_NUMEROUS_CHANGE_CHANNEL;
                nextState = currentState;
                if (scenario == SCENARIO_NUMEROUS_CHANNEL_CHANGE) {
                    // ReRender second page
                    if (A4TVProgressBarPVR.getControlProviderPVR()
                            .isFlagRecord()) {
                        updateFrontSideOfSecondPage(STATE_INIT); // clear back
                                                                 // side
                                                                 // curl page
                    }
                    updateFrontSideOfSecondPage(STATE_NUMEROUS_CHANGE_CHANNEL);
                    if (curlPageForNumChannelChange) {
                        states(STATE_NUMEROUS_CHANGE_CHANNEL, 500);
                        curlPageForNumChannelChange = false;
                    }
                    ANIMATION_TIME = ANIMATION_TIME_NUMEROUS_CHANGE_CHANNEL;
                    startStateTimer(ANIMATION_TIME);
                    nextState = STATE_CHANGE_CHANNEL;
                } else {
                    Log.i(TAG,
                            "CURLE DEFAULT END STATE_NUMEROUS_CHANGE_CHANNEL");
                }
                break;
            }
            case STATE_CHANGE_CHANNEL: {
                boolean lCurlPage = true;
                currentState = STATE_CHANGE_CHANNEL;
                nextState = STATE_CHANNEL_INFO;
                ANIMATION_TIME = ANIMATION_TIME_CHANNEL_CHANGE;
                switch (scenario) {
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE: {
                        if (A4TVProgressBarPVR.getControlProviderPVR()
                                .isFlagRecord()) {
                            updateFrontSideOfSecondPage(STATE_INIT); // clear
                                                                     // back
                                                                     // side
                                                                     // curl
                                                                     // page
                        }
                        if (fastlChangeChannelFlag) {
                            ANIMATION_TIME = ANIMATION_TIME_CHANNEL_CHANGE_FCZ;
                            nextState = STATE_CHANGE_CHANNEL;
                            prepareForChannelChange = true;
                        } else {
                            prepareForChannelChange = false;
                        }
                    }
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT:
                        if (!isCurled) {
                            updateBackSideOfFirstPage(true);
                        }
                        updateSecondPage();
                        if (fastlChangeChannelFlag) {
                            fastlChangeChannelFlag = false;
                            break;
                        }
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE:
                        if (mCurlHandler.getChannelChangeHandler()
                                .checkCurrentWithNextChannelIndex()) {
                            nextState = STATE_DEINIT;
                            ANIMATION_TIME = ANIMATION_TIME_CHANNEL_CLOSE;
                            lCurlPage = false;
                        } else {
                            if (mCurlHandler.getChannelChangeHandler()
                                    .checkDualVideService()) {
                                Content activeContent = mCurlHandler
                                        .getChannelChangeHandler()
                                        .getCurrentChannelContent();
                                mCurlHandler.getChannelChangeHandler()
                                        .changeChannel();
                                Content content = mCurlHandler
                                        .getChannelChangeHandler()
                                        .getCurrentChannelContent();
                                if (activeContent != null)
                                    if (content != null)
                                        if (activeContent.getSourceType() != SourceType.ANALOG
                                                && activeContent
                                                        .getFilterType() != FilterType.INPUTS
                                                && content.getSourceType() == SourceType.ANALOG) {
                                            /*
                                             * Remove WebView from screen and
                                             * set key mask to 0
                                             */
                                            if (0 != (MainActivity.getKeySet())) {
                                                try {
                                                    if (!MainActivity.activity
                                                            .isHbbTVInHTTPPlaybackMode()) {
                                                        MainActivity.activity.webDialog
                                                                .getHbbTVView()
                                                                .setAlpha(
                                                                        (float) 0.00);
                                                        MainActivity
                                                                .setKeySet(0);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                            } else {
                                nextState = STATE_DEINIT;
                                ANIMATION_TIME = ANIMATION_TIME_CHANNEL_CLOSE;
                                lCurlPage = false;
                            }
                        }
                        break;
                    default: {
                        nextState = currentState;
                        Log.i(TAG, "CURLE DEFAULT END STATE_CHANNEL_CHANGE");
                        return;
                    }
                }
                /** Prepare and Start CurlEffect */
                if (!isCurled) {
                    if (lCurlPage) {
                        states(STATE_CHANGE_CHANNEL, 1000);
                    }
                    isCurled = false;
                } else {
                    isCurled = false;
                }
                startStateTimer(ANIMATION_TIME);
                break;
            }
            case STATE_PVR: {
                currentState = STATE_PVR;
                nextState = currentState;
                if (scenario == SCENARIO_PVR_RECORD) {
                    ANIMATION_TIME = ANIMATION_TIME_PVR;
                    nextState = STATE_DEINIT;
                    if (!isCurled) {
                        states(STATE_PVR, 0);
                        if (OSDHandlerHelper.getHandlerState() == PVR_STATE_FF_TIME_SHIFT
                                || OSDHandlerHelper.getHandlerState() == PVR_STATE_PLAY_TIME_SHIFT
                                || OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING
                                || OSDHandlerHelper.getHandlerState() == PVR_STATE_PAUSE_TIME_SHIFT
                                || OSDHandlerHelper.getHandlerState() == PVR_STATE_REW_TIME_SHIFT) {
                            flagRenderPagesContinuously = true;
                            startRendererTimer(TIMERTASK_WHAT_PVR,
                                    TIMERTASK_TIME_PVR);
                        } else {
                            updateFrontSideOfSecondPage(STATE_PVR);
                        }
                        isCurled = false;
                    } else {
                        isCurled = false;
                        if (!flagRenderPagesContinuously) {
                            updateFrontSideOfSecondPage(STATE_PVR);
                        }
                    }
                    startStateTimer(ANIMATION_TIME);
                } else {
                    Log.i(TAG, "CURLE DEFAULT END STATE_PVR");
                }
                break;
            }
            case STATE_MULTIMEDIA_CONTROLLER: {
                currentState = STATE_MULTIMEDIA_CONTROLLER;
                nextState = currentState;
                if (scenario == SCENARIO_MULTIMEDIA_CONTROLLER) {
                    ANIMATION_TIME = ANIMATION_TIME_MULTIMEDIA_CONTROLLER;
                    nextState = STATE_DEINIT;
                    if (A4TVProgressBarPVR.getControlProviderPVR()
                            .isFlagRecord()) {
                        isCurled = false;
                    }
                    if (!isCurled) {
                        states(STATE_MULTIMEDIA_CONTROLLER, 0);
                        if (OSDHandlerHelper.getHandlerState() == CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER) {
                            flagRenderPagesContinuously = true;
                            startRendererTimer(
                                    TIMERTASK_WHAT_MULTIMEDIA_CONTROLLER,
                                    TIMERTASK_TIME_MULTIMEDIA_CONTROLLER);
                        } else if (OSDHandlerHelper.getHandlerState() == PVR_STATE_PAUSE_PLAY_BACK
                                || OSDHandlerHelper.getHandlerState() == PVR_STATE_PLAY_PLAY_BACK
                                || OSDHandlerHelper.getHandlerState() == PVR_STATE_FF_PLAY_BACK
                                || OSDHandlerHelper.getHandlerState() == PVR_STATE_REW_PLAY_BACK) {
                            flagRenderPagesContinuously = true;
                            startRendererTimer(
                                    TIMERTASK_WHAT_MULTIMEDIA_CONTROLLER_PVR,
                                    TIMERTASK_TIME_MULTIMEDIA_CONTROLLER_PVR);
                        } else {
                            updateFrontSideOfSecondPage(STATE_MULTIMEDIA_CONTROLLER);
                        }
                        isCurled = false;
                    } else {
                        isCurled = false;
                        if (!flagRenderPagesContinuously) {
                            updateFrontSideOfSecondPage(STATE_MULTIMEDIA_CONTROLLER);
                        }
                    }
                    startStateTimer(ANIMATION_TIME);
                } else {
                    Log.i(TAG, "CURLE DEFAULT END STATE_MULTIMEDIA_CONTROLLER");
                }
                break;
            }
            case STATE_PICTURE_FORMAT: {
                currentState = STATE_PICTURE_FORMAT;
                nextState = currentState;
                if (scenario == SCENARIO_PICTURE_FORMAT) {
                    // ReRender second page
                    updateFrontSideOfSecondPage(STATE_PICTURE_FORMAT);
                    if (!isCurled) {
                        updateBackSideOfFirstPage(true);
                        states(STATE_PICTURE_FORMAT, 500);
                        isCurled = false;
                    } else {
                        isCurled = false;
                    }
                    ANIMATION_TIME = ANIMATION_TIME_PICTURE_FORMAT;
                    startStateTimer(ANIMATION_TIME);
                    nextState = STATE_DEINIT;
                } else {
                    Log.i(TAG, "CURLE DEFAULT END STATE_PICTURE_FORMAT");
                }
                break;
            }
            case STATE_DEINIT: {
                currentState = STATE_DEINIT;
                nextState = currentState;
                switch (scenario) {
                    case SCENARIO_INFO: {
                        if (A4TVDialog.getListOfDialogs() != null) {
                            ArrayList<A4TVDialog> mA4tvDialogs = new ArrayList<A4TVDialog>();
                            mA4tvDialogs.addAll(A4TVDialog.getListOfDialogs());
                            A4TVDialog.getListOfDialogs().clear();
                            if (mA4tvDialogs.size() > 0) {
                                for (int i = mA4tvDialogs.size() - 1; i >= 0; i--) {
                                    mA4tvDialogs.get(i).show();
                                }
                                // Start Again Animation
                                if (mA4tvDialogs.get(mA4tvDialogs.size() - 1)
                                        .equals(MainActivity.activity
                                                .getDialogManager()
                                                .getMainMenuDialog())) {
                                    MainActivity.activity.getMainMenuHandler()
                                            .getA4TVOnSelectLister()
                                            .startAnimationsManual();
                                }
                                mPageProvider.updatePage(
                                        mPageCurl.getTexturePage(), getWidth(),
                                        getHeight(), 0, STATE_DO_NOTHING);
                                requestRender();
                                /** Load last AppState */
                                MainKeyListener.setAppState(mLastAppState);
                                Log.i(TAG, "LastAppState: " + mLastAppState);
                            }
                        }
                    }
                    case SCENARIO_MULTIMEDIA_CONTROLLER:
                    case SCENARIO_PVR_RECORD:
                    case SCENARIO_VOLUME:
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT:
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_INFO:
                    case SCENARIO_INPUT_INFO:
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE:
                    case SCENARIO_PICTURE_FORMAT: {
                        flagRenderPagesContinuously = false;
                        curlPageForNumChannelChange = true;
                        // Clear channel index because
                        mCurlHandler.getChannelChangeHandler()
                                .flushChannelIndexBuffer();
                        mCurlHandler.getChannelChangeHandler()
                                .flushSecretKeyBuffer();
                        mCurlHandler.getChannelChangeHandler()
                                .flushMajorMinorChannelIndexBuffer();
                        states(STATE_DEINIT, 500);
                        nextState = STATE_OFF;
                        isCurled = false;
                        ANIMATION_TIME = ANIMATION_TIME_DE_INIT;
                        startStateTimer(ANIMATION_TIME);
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END STATE_DEINIT");
                        break;
                    }
                }
                break;
            }
            case STATE_OFF: {
                currentState = STATE_OFF;
                nextState = currentState;
                switch (scenario) {
                    case SCENARIO_INFO:
                    case SCENARIO_MULTIMEDIA_CONTROLLER:
                    case SCENARIO_PVR_RECORD:
                    case SCENARIO_VOLUME:
                    case SCENARIO_CHANNEL_INFO:
                    case SCENARIO_INPUT_INFO:
                    case SCENARIO_NUMEROUS_CHANNEL_CHANGE:
                    case SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE:
                    case SCENARIO_CHANNEL_CHANGE_BY_CONTENT:
                    case SCENARIO_CHANNEL_CHANGE:
                    case SCENARIO_PICTURE_FORMAT: {
                        if (flagChannelInfo) {
                            flagChannelInfo = false;
                        }
                        mCurlState = CURL_NONE;
                        scenario = SCENARIO_DO_NOTHING;
                        nextState = STATE_INIT;
                        isCurled = false;
                        ANIMATION_TIME = 1;
                        break;
                    }
                    default: {
                        Log.i(TAG, "CURLE DEFAULT END STATE_OFF");
                        break;
                    }
                }
                break;
            }
            default: {
                nextState = STATE_INIT;
                Log.i(TAG, "CURLE DEFAULT END STATE_CHANGER");
                break;
            }
        }
    }

    /** AnimtionTime in milliseconds, 0 is default value (1000ms) */
    private void states(int state, int milliseconds) {
        int animationDuration;
        if (milliseconds <= 0) {
            animationDuration = 1000;
        } else {
            animationDuration = milliseconds;
        }
        switch (state) {
        // Starting to Curl from the right-down corner
            case STATE_INIT: {
                if (mPageProvider != null) {
                    pageRect = mRenderer.getPageRect();
                    mPos.set(getWidth(), getHeight());
                    mRenderer.translate(mPos);
                    mDragStartPos.set(mPos);
                    if (mDragStartPos.y > pageRect.top) {
                        mDragStartPos.y = pageRect.top;
                    } else if (mDragStartPos.y < pageRect.bottom) {
                        mDragStartPos.y = pageRect.bottom;
                    }
                    mDragStartPos.x = pageRect.right;
                    if (curlFirstTimeStart) {
                        startCurl(CURL_RIGHT);
                        curlFirstTimeStart = false;
                    } else {
                        mCurlState = CURL_RIGHT;
                        updatePage(mPage.getTexturePage(), mCurrentIndex + 1,
                                nextState);
                        updatePage(mPageCurl.getTexturePage(), mCurrentIndex,
                                currentState);
                    }
                }
                break;
            }
            case STATE_CHANNEL_INFO: {
                initSmooth(mPos.x, mPos.y, (getWidth() - 2),
                        calcChannelInfoPosition(), animationDuration);
                enableRendering = true;
                requestRender();
                break;
            }
            case STATE_INFO: {
                int[] curlPosition = calcMainInfoPosition();
                initSmooth(mPos.x, mPos.y, curlPosition[0], curlPosition[1],
                        animationDuration);
                enableRendering = true;
                requestRender();
                break;
            }
            case STATE_VOLUME: {
                initSmooth(mPos.x, mPos.y, (getWidth() - 2),
                        (80 * getHeight()) / 100, animationDuration);
                enableRendering = true;
                requestRender();
                break;
            }
            case STATE_NUMEROUS_CHANGE_CHANNEL: {
                initSmooth(mPos.x, mPos.y, (95 * getWidth()) / 100,
                        (89 * getHeight()) / 100, animationDuration);
                enableRendering = true;
                requestRender();
                break;
            }
            case STATE_CHANGE_CHANNEL: {
                try {
                    Content content = MainActivity.service
                            .getContentListControl().getContent(
                                    MainActivity.activity.getPageCurl()
                                            .getChannelChangeHandler()
                                            .getChannelIndex());
                    if ((content != null)
                            && (content.getSourceType() == SourceType.ANALOG)) {
                        prepareForChannelChange = false;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (prepareForChannelChange) {
                    initSmooth(mPos.x, mPos.y, (getWidth() - 2),
                            calcChannelChangePosition(), animationDuration / 2);
                } else {
                    Content activeContent = mCurlHandler
                            .getChannelChangeHandler().getActiveContent();
                    if (activeContent != null) {
                        int activeFilterType = activeContent.getFilterType();
                        if ((activeFilterType != FilterType.INPUTS)) {
                            if (MainActivity.sharedPrefs.getBoolean(
                                    MainActivity.CURL_ANIMATION_ON_OFF, true)) {
                                int animationDurationChannelChange;
                                if (ConfigHandler.CURL_TIME_MILIS_INT <= 0) {
                                    animationDurationChannelChange = 1000;
                                } else {
                                    animationDurationChannelChange = ConfigHandler.CURL_TIME_MILIS_INT;
                                }
                                initSmooth(mPos.x, mPos.y, 0,
                                        (12 * getHeight()) / 24,
                                        animationDurationChannelChange);
                            }
                        }
                    }
                }
                enableRendering = true;
                requestRender();
                break;
            }
            case STATE_PVR: {
                initSmooth(mPos.x, mPos.y, (getWidth() - 2),
                        calcChannelInfoPosition(), animationDuration);
                enableRendering = true;
                requestRender();
                break;
            }
            case STATE_MULTIMEDIA_CONTROLLER: {
                initSmooth(mPos.x, mPos.y, (getWidth() - 2),
                        calcChannelInfoPosition(), animationDuration);
                enableRendering = true;
                requestRender();
                break;
            }
            case STATE_PICTURE_FORMAT: {
                initSmooth(mPos.x, mPos.y, (getWidth() - 2),
                        (80 * getHeight()) / 100, animationDuration);
                enableRendering = true;
                requestRender();
                break;
            }
            // Default Curl Ending to right-down corner
            case STATE_DEINIT: {
                enableRendering = true;
                initSmooth(mPos.x, mPos.y, getWidth(), getHeight(),
                        animationDuration);
                requestRender();
                break;
            }
        }
    }

    private void startRendererTimer(final int what, final int milliseconds) {
        if (mTimer != null) {
            mTimer.purge();
            if (mRendererTimerTask != null) {
                mRendererTimerTask.cancel();
            }
            mRendererTimerTask = null;
            mRendererTimerTask = new TimerTask() {
                @Override
                public void run() {
                    switch (what) {
                        case TIMERTASK_WHAT_CHANNEL_INFO: {
                            getHandler().post(mRunnableChannelInfo);
                            if (!isFlagChannelInfo()) {
                                mRendererTimerTask.cancel();
                            }
                            break;
                        }
                        case TIMERTASK_WHAT_MULTIMEDIA_CONTROLLER: {
                            if (flagRenderPagesContinuously) {
                                getHandler().post(mRunnableUpdateProgress);
                            } else {
                                mRendererTimerTask.cancel();
                            }
                            break;
                        }
                        case TIMERTASK_WHAT_PVR: {
                            if (flagRenderPagesContinuously) {
                                getHandler().post(mRunnablePVR);
                            } else {
                                mRendererTimerTask.cancel();
                            }
                            break;
                        }
                        case TIMERTASK_WHAT_MULTIMEDIA_CONTROLLER_PVR: {
                            if (flagRenderPagesContinuously) {
                                getHandler().post(mRunnableMultiMedia);
                            } else {
                                mRendererTimerTask.cancel();
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
            };
            mTimer.schedule(mRendererTimerTask, 0, milliseconds);
        }
    }

    /*
     * public synchronized void setUpNewChannelInfo(boolean renderFirstPage,
     * CurlView curlview) { mCurlHandler.setUpChannelInfo(); // ReRender pages
     * if (renderFirstPage) { updateBackSideOfFirstPage(false); }
     * updateFrontSideOfSecondPage(STATE_CHANNEL_INFO); }
     */
    public synchronized void setUpNewChannelInfoByIndex(int index) {
        mCurlHandler.setUpChannelInfoByIndex(index);
        // ReRender pages
        updateBackSideOfFirstPage(false);
        updateFrontSideOfSecondPage(STATE_CHANNEL_INFO);
    }

    private void initSmooth(float sourceWidth, float sourceHeight,
            float targetWidth, float targetHeight, int animationTime) {
        mPos.set(sourceWidth, sourceHeight);
        mAnimationSourceSmooth.set(mPos);
        mAnimationStartTimeSmooth = System.currentTimeMillis();
        // Translate from float to Renderer Coordinates
        PointF target = new PointF(targetWidth, targetHeight);
        mRenderer.translate(target);
        mAnimationTargetSmooth.set(target);
        mAnimationDurationTimeSmooth = animationTime;
    }

    private void smoothCurl() {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= mAnimationStartTimeSmooth
                + mAnimationDurationTimeSmooth) {
            enableRendering = false;
            curlEndCallBack();
        } else {
            mPos.set(mAnimationSourceSmooth);
            float t = 1f - ((float) (currentTime - mAnimationStartTimeSmooth) / mAnimationDurationTimeSmooth);
            t = 1f - (t * t * t * (3 - 2 * t));
            mPos.x += (mAnimationTargetSmooth.x - mAnimationSourceSmooth.x) * t;
            mPos.y += (mAnimationTargetSmooth.y - mAnimationSourceSmooth.y) * t;
            updateCurlPos(mPos);
        }
    }

    private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {
        if (mCurlState == CURL_RIGHT) {
            RectF pageRect = mRenderer.getPageRect();
            if (curlPos.x >= pageRect.right) {
                mPageCurl.reset();
                requestRender();
                return;
            }
            if (curlPos.x < pageRect.left) {
                curlPos.x = pageRect.left;
            }
            if (curlDir.y != 0) {
                float diffX = curlPos.x - pageRect.left;
                float leftY = curlPos.y + (diffX * curlDir.x / curlDir.y);
                if (curlDir.y < 0 && leftY < pageRect.top) {
                    curlDir.x = curlPos.y - pageRect.top;
                    curlDir.y = pageRect.left - curlPos.x;
                } else if (curlDir.y > 0 && leftY > pageRect.bottom) {
                    curlDir.x = pageRect.bottom - curlPos.y;
                    curlDir.y = curlPos.x - pageRect.left;
                }
            }
        }
        // Finally normalize direction vector and do rendering.
        double dist = FloatMath.sqrt((float) (curlDir.x * curlDir.x + curlDir.y
                * curlDir.y));
        if (dist != 0) {
            curlDir.x /= dist;
            curlDir.y /= dist;
            mPageCurl.curl(curlPos, curlDir, radius);
        } else {
            mPageCurl.reset();
        }
        requestRender();
    }

    private void startCurl(int page) {
        if (page == CURL_RIGHT) {
            // Remove meshes from renderer.
            mRenderer.removeCurlMesh(mPage);
            mRenderer.removeCurlMesh(mPageCurl);
            if (mCurrentIndex < mPageProvider.getPageCount() - 1) {
                updatePage(mPage.getTexturePage(), mCurrentIndex + 1, nextState);
                mPage.setRect(mRenderer.getPageRect());
                mPage.setFlipTexture(false);
                mPage.reset();
                mRenderer.addCurlMesh(mPage);
            }
            // Add curled page to renderer.
            // //////////////////////////////
            // Added by NITRO, 31.10.2012.
            // Clear Curled Page
            updatePage(mPageCurl.getTexturePage(), mCurrentIndex, currentState);
            // ////////////////////////////
            mPageCurl.setRect(mRenderer.getPageRect());
            mPageCurl.setFlipTexture(false);
            mPageCurl.reset();
            mRenderer.addCurlMesh(mPageCurl);
            mCurlState = CURL_RIGHT;
        }
    }

    /** Updates curl position. */
    private void updateCurlPos(PointF pointerPos) {
        // Default curl radius.
        double radius = mRenderer.getPageRect().width() / 3;
        radius *= 0.2f;
        mCurlPos.set(pointerPos);
        // If curl happens on right page, or on left page on two page mode,
        // we'll calculate curl position from pointerPos.
        if (mCurlState == CURL_RIGHT) {
            mCurlDir.x = mCurlPos.x - mDragStartPos.x;
            mCurlDir.y = mCurlPos.y - mDragStartPos.y;
            float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y
                    * mCurlDir.y);
            // Adjust curl radius so that if page is dragged far enough on
            // opposite side, radius gets closer to zero.
            float pageWidth = mRenderer.getPageRect().width();
            double curlLen = radius * Math.PI;
            if (dist > (pageWidth * 2) - curlLen) {
                curlLen = Math.max((pageWidth * 2) - dist, 0f);
                radius = curlLen / Math.PI;
            }
            // Actual curl position calculation.
            if (dist >= curlLen) {
                double translate = (dist - curlLen) / 2;
                float pageLeftX = mRenderer.getPageRect().left;
                radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius), 0f);
                mCurlPos.y -= mCurlDir.y * translate / dist;
            } else {
                double angle = Math.PI
                        * FloatMath.sqrt((float) (dist / curlLen));
                double translate = radius * Math.sin(angle);
                mCurlPos.x += mCurlDir.x * translate / dist;
                mCurlPos.y += mCurlDir.y * translate / dist;
            }
        }
        // Otherwise we'll let curl follow pointer position.
        setCurlPos(mCurlPos, mCurlDir, radius);
    }

    /** Updates bitmaps for page meshes */
    private void updatePages() {
        if (mPageProvider == null || mPageBitmapWidth <= 0
                || mPageBitmapHeight <= 0) {
            return;
        }
        // Remove meshes from renderer.
        mRenderer.removeCurlMesh(mPage);
        mRenderer.removeCurlMesh(mPageCurl);
        int rightIdx = mCurrentIndex;
        int curlIdx = -1;
        if (mCurlState == CURL_RIGHT) {
            curlIdx = rightIdx;
            ++rightIdx;
        }
        if (rightIdx >= 0 && rightIdx < mPageProvider.getPageCount()) {
            updatePage(mPage.getTexturePage(), rightIdx, currentState);
            mPage.setFlipTexture(false);
            mPage.setRect(mRenderer.getPageRect());
            mPage.reset();
            mRenderer.addCurlMesh(mPage);
        }
        if (curlIdx >= 0 && curlIdx < mPageProvider.getPageCount()) {
            updatePage(mPageCurl.getTexturePage(), curlIdx, currentState);
            if (mCurlState == CURL_RIGHT) {
                mPageCurl.setFlipTexture(false);
                mPageCurl.setRect(mRenderer.getPageRect());
            }
            mPageCurl.reset();
            mRenderer.addCurlMesh(mPageCurl);
        }
    }

    public void updateSecondPage() {
        if (mCurlView != null) {
            mCurlView.updatePage(mCurlView.mPage.getTexturePage(), 1,
                    currentState);
            mCurlView.requestRender();
        }
    }

    /**
     * @param clear
     *        Clear Back Side of Curled Page!
     */
    public void updateBackSideOfFirstPage(boolean clear) {
        if (mCurlView != null) {
            if (clear) {
                mCurlView.mPageProvider.clearBackSideOfFirstPage(
                        mCurlView.mPageCurl.getTexturePage(),
                        mCurlView.getWidth(), mCurlView.getHeight());
            } else {
                mCurlView.mPageProvider.updateBackSideOfFirstPage(
                        mCurlView.mPageCurl.getTexturePage(),
                        mCurlView.getWidth(), mCurlView.getHeight());
            }
        }
    }

    public void updateFrontSideOfSecondPage(int state) {
        if (mCurlView != null) {
            mCurlView.mPageProvider.updateFrontSideOfSecondPage(
                    mCurlView.mPage.getTexturePage(), mCurlView.getWidth(),
                    mCurlView.getHeight(), state);
            mCurlView.requestRender();
        }
    }

    /** Handle Components in UI Thread. */
    private Runnable mRunnableStateChanger = new Runnable() {
        @Override
        public void run() {
            stateChanger();
        }
    };
    private Runnable mRunnableShowToast = new Runnable() {
        @Override
        public void run() {
            mCurlHandler.getChannelChangeHandler()
                    .getToastForNumerousChannelChange();
        }
    };
    private Runnable mRunnableUpdateProgress = new Runnable() {
        @Override
        public void run() {
            if (mMediaController.isPlaying()) {
                ControlProvider.setFlagPlay(true);
                // Set Duration
                A4TVMultimediaController.getControlProvider().setDuration(
                        mMediaController.getPlayBackDuration());
                // Set ElaspedTime
                A4TVMultimediaController.getControlProvider().setElapsedTime(
                        mMediaController.getElapsedTime());
            } else {
                ControlProvider.setFlagPlay(false);
            }
            updateFrontSideOfSecondPage(STATE_MULTIMEDIA_CONTROLLER);
        }
    };
    private Runnable mRunnableChannelInfo = new Runnable() {
        @Override
        public void run() {
            OSDHandlerHelper.updateTimeChannelInfo();
            updateSecondPage();
        }
    };
    private Runnable mRunnablePVR = new Runnable() {
        @Override
        public void run() {
            if (currentState == STATE_PVR) {
                updateFrontSideOfSecondPage(STATE_PVR);
            }
        }
    };
    private Runnable mRunnableMultiMedia = new Runnable() {
        @Override
        public void run() {
            updateFrontSideOfSecondPage(STATE_MULTIMEDIA_CONTROLLER);
        }
    };

    private int calcChannelInfoPosition() {
        int curlHeight;
        curlHeight = (int) ((getHeight() / 100) * 49);
        return curlHeight;
    }

    private int calcChannelChangePosition() {
        int curlHeight;
        curlHeight = (int) ((getHeight() / 100) * 85);
        return curlHeight;
    }

    private int[] calcMainInfoPosition() {
        int[] curlPosition = { 0, 0 };
        curlPosition[0] = (int) ((getWidth() / 100) * 55);
        curlPosition[1] = (int) ((getHeight() / 100) * 96);
        return curlPosition;
    }

    public void changeCurlPageWH(int a, int b) {
        mPos.set(a, b);
        mRenderer.translate(mPos);
        updateCurlPos(mPos);
    }

    /**
     * This Method is for making sure that CurlEffect for ChannelChange has
     * ended whole animation sequence.
     */
    private void curlEndCallBack() {
        if (mCurlView.mStateTimerTask != null
                && currentState == STATE_CHANGE_CHANNEL
                && !prepareForChannelChange) {
            if (flagZappCallBack) {
                flagZappCallBack = false;
                mCurlView.startStateTimer(0);
                Log.i(TAG, "CurlEndCallBack has been Accepted!");
            } else {
                flagCurlCallBack = true;
            }
        }
    }

    public void setFlagZapChannel(boolean channelIsZapped) {
        if (channelIsZapped) {
            if (mCurlView != null) {
                if (mCurlView.mStateTimerTask != null
                        && currentState == STATE_CHANGE_CHANNEL) {
                    if (flagCurlCallBack) {
                        flagCurlCallBack = false;
                        mCurlView.startStateTimer(0);
                        Log.i(TAG, "setFlagZapChannel has been called!");
                    } else {
                        flagZappCallBack = true;
                    }
                }
            }
        } else {
            nextState = STATE_DEINIT;
            scenario = SCENARIO_CHANNEL_CHANGE;
            // ReRender FirstSide of First Page to Be Transparent
            // If zapp fails to clear screen
            mPageProvider.updateFrontSideOfFirstPage(
                    mPageCurl.getTexturePage(), getWidth(), getHeight(),
                    STATE_DEINIT);
            mCurlView.startStateTimer(0);
        }
    }

    /** Updates given CurlPage via PageProvider for page located at index */
    private void updatePage(CurlPage page, int index, int state) {
        // Ask page provider to fill it up with bitmaps and colors.
        mPageProvider.updatePage(page, mPageBitmapWidth, mPageBitmapHeight,
                index, state);
    }

    public void setBackgroundColor(int color) {
        mRenderer.setBackgroundColor(color);
        requestRender();
    }

    public void setCurrentIndexUpdate(int index) {
        mCurrentIndex = index;
        updatePages();
        requestRender();
    }

    /**
     * Update/set page provider.
     */
    public void setPageProvider(PageProvider pageProvider) {
        mPageProvider = pageProvider;
        // Initialize Curl Page
        mCurrentIndex = 0;
        updatePages();
        requestRender();
    }

    /**
     * Sets SizeChangedObserver for this View. Call back method is called from
     * this View's onSizeChanged method.
     */
    public void setSizeChangedObserver(SizeChangedObserver observer) {
        mSizeChangedObserver = observer;
    }

    public boolean isFlagChannelInfo() {
        return flagChannelInfo;
    }

    // public boolean isFlagRenderPagesContinuously() {
    // return flagRenderPagesContinuously;
    // }
    /** Enable - Disable ChannelInfo Auto Closing */
    public void setFlagChannelInfo(boolean flagChannelInfo) {
        this.flagChannelInfo = flagChannelInfo;
    }

    public void setFlagRenderPagesContinuously(
            boolean argFlagRenderPagesContinuously) {
        flagRenderPagesContinuously = argFlagRenderPagesContinuously;
    }

    public boolean isFlagMultimediaController() {
        return flagMultimediaController;
    }

    public void setFlagMultimediaController(boolean flagMultimediaController) {
        this.flagMultimediaController = flagMultimediaController;
    }

    public boolean isFastCallingChannelInfo() {
        return fastCallingChannelInfo;
    }

    public int getmPageBitmapHeight() {
        return mPageBitmapHeight;
    }

    public int getmPageBitmapWidth() {
        return mPageBitmapWidth;
    }

    public CurlMesh getmPageCurl() {
        return mPageCurl;
    }

    public PageProvider getmPageProvider() {
        return mPageProvider;
    }

    public CurlMesh getmPage() {
        return mPage;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int current) {
        this.currentState = current;
    }

    public int getNextState() {
        return nextState;
    }

    public int getScenario() {
        return scenario;
    }

    public void setScenario(int scenario) {
        this.scenario = scenario;
    }

    public void setmMediaController(MediaController mMediaController) {
        this.mMediaController = mMediaController;
    }

    public int getAnimationTimeChannelInfo() {
        return animationTimeChannelInfo;
    }

    public void setAnimationTimeChannelInfo(int animationTimeChannelInfo) {
        this.animationTimeChannelInfo = animationTimeChannelInfo;
    }

    public CurlHandler getCurlHandler() {
        return mCurlHandler;
    }

    public void setCurlHandler(CurlHandler mCurlHandler) {
        this.mCurlHandler = mCurlHandler;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setNextState(int nextState) {
        this.nextState = nextState;
    }

    public void setForceStopFlag(boolean forceStopFlag) {
        this.forceStopFlag = forceStopFlag;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Interfaces
    // ///////////////////////////////////////////////////////////////////////////
    /**
     * Provider for feeding 'book' with bitmaps which are used for rendering
     * pages.
     */
    public interface PageProvider {
        public int getPageCount();

        public void updatePage(CurlPage page, int width, int height, int index,
                int state);

        public void updateFrontSideOfFirstPage(CurlPage page, int width,
                int height, int state);

        public void updateBackSideOfFirstPage(CurlPage page, int width,
                int height);

        public void clearBackSideOfFirstPage(CurlPage page, int width,
                int height);

        public void updateFrontSideOfSecondPage(CurlPage page, int width,
                int height, int state);

        public void setStrValues(ArrayList<String> strValues);

        public ArrayList<Integer> getImageIds();

        public void setImageIds(ArrayList<Integer> imageIds);

        public void setProgressValue(int mProgressValue);

        public CreateBitmaps getCreateBitmaps();
    }

    /**
     * Observer interface for handling CurlView size changes.
     */
    public interface SizeChangedObserver {
        public void onSizeChanged(int width, int height);
    }
}