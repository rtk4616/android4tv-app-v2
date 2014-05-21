package com.iwedia.gui.callbacks;

import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.IChannelsCallback;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.CheckServiceType;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.osd.curleffect.CurlHandler;

public class ChannelsCallBack extends IChannelsCallback.Stub implements
        OSDGlobal {
    private static final String TAG = "ChannelsCallBack";
    /** Messages */
    private static final int HBB_HIDE = 0;
    private static final int HIDE_SECOND_DISPLAY = 1;
    private static final int UPDATE_CHANNEL_INFO = 2;
    private static final int PLAYBACK_STOP = 3;
    private static final int NETWORK_CHANGED = 4;
    /** Instance of MainActivity */
    private MainActivity mActivity = null;
    /** Instance of CallBack Handler. */
    private CallBackHandler mCallBackHandler = null;
    /** UI Handler */
    private Handler mHandler = null;

    public ChannelsCallBack(CallBackHandler callBackHandler,
            MainActivity activity) {
        mCallBackHandler = callBackHandler;
        mActivity = activity;
        initializeHandlers();
    }

    @Override
    public void antennaConnected(int deviceID, boolean state)
            throws RemoteException {
        Log.d(TAG, "ANTENNA IS CONNECTED deviceID: " + deviceID + " state: "
                + state);
        mCallBackHandler.setAntenaConnected(state);
        IContentListControl contentListControl = MainActivity.service
                .getContentListControl();
        Content primaryContent = contentListControl.getActiveContent(0);
        Content secondaryContent = contentListControl.getActiveContent(1);
        int primaryContentFilterType = primaryContent.getFilterType();
        if (deviceID == 0) {
            if ((SourceType.CAB == primaryContent.getSourceType())
                    || (SourceType.TER == primaryContent.getSourceType())
                    || (SourceType.SAT == primaryContent.getSourceType())) {
                // antenna connected
                if (state) {
                    mActivity.getCheckServiceType().hideNoSignalLayout();
                }
                // antenna disconnected
                else {
                    Message.obtain(mHandler, HBB_HIDE);
                    mActivity.getCheckServiceType().showNoSignalLayout();
                }
            }
        } else {
            if (secondaryContent != null) {
                int secondaryContentFilterType = secondaryContent
                        .getFilterType();
                if ((SourceType.CAB == secondaryContent.getSourceType())
                        || (SourceType.TER == secondaryContent.getSourceType())
                        || (SourceType.SAT == secondaryContent.getSourceType())) {
                    if (state == false) {
                        contentListControl.stopContent(secondaryContent, 1);
                        Message.obtain(mHandler, HIDE_SECOND_DISPLAY);
                    }
                }
            }
        }
    }

    @Override
    public void nowNextChanged() throws RemoteException {
        Message.obtain(mHandler, UPDATE_CHANNEL_INFO);
    }

    @Override
    public void epgEventsChanged() throws RemoteException {
    }

    @Override
    public void playbackStopped(int displayId) throws RemoteException {
        Log.i(TAG, "PlayBackStopped CallBack");
        if (MainKeyListener.getAppState() != MainKeyListener.CLEAN_SCREEN
                || !MainActivity.sharedPrefs.getBoolean(
                        MainActivity.CURL_ANIMATION_ON_OFF, true)) {
            Log.i(TAG, "PlayBackStopped CallBack Accepted");
            // Blank Screen
            try {
                MainActivity.service.getVideoControl().videoBlank(0, true);
            } catch (Exception e) {
                Log.e(TAG, "Blank Screen Exception", e);
            }
        }
        Message.obtain(mHandler, PLAYBACK_STOP);
    }

    @Override
    public void serviceScrambled(boolean state) throws RemoteException {
        Log.d(TAG, "serviceScrambled callback, state: " + state);
        CheckServiceType.serviceScrambledChanged(state);
    }

    @Override
    public void networkChanged(int networkId) throws RemoteException {
        Log.d(TAG, "networkChanged callback, networkId: " + networkId);
        Message.obtain(mHandler, NETWORK_CHANGED);
    }

    @Override
    public void startingService() throws RemoteException {
        // Check Service
        try {
            Content lContent = MainActivity.service.getContentListControl()
                    .getActiveContent(0);
            CheckServiceType.checkService(lContent, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeHandlers() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HBB_HIDE: {
                        /*
                         * Remove WebView from screen and set key mask to 0
                         */
                        if (0 != (MainActivity.getKeySet())) {
                            try {
                                if (!mActivity.isHbbTVInHTTPPlaybackMode()) {
                                    mActivity.webDialog.getHbbTVView()
                                            .setAlpha((float) 0.00);
                                    MainActivity.setKeySet(0);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    case HIDE_SECOND_DISPLAY: {
                        mActivity.getPrimaryVideoView().setScaling(0, 0, 1920,
                                1080);
                        mActivity.getSecondaryVideoView().updateVisibility(
                                View.INVISIBLE);
                        mCallBackHandler
                                .showToastMessage(R.string.second_disaplay_signal_lost);
                        break;
                    }
                    case UPDATE_CHANNEL_INFO: {
                        try {
                            CurlHandler lCurlHandler = ((CurlHandler) mActivity
                                    .getPageCurl());
                            if (lCurlHandler.getCurlView().isFlagChannelInfo()
                                    || lCurlHandler.getCurlView()
                                            .getCurrentState() == STATE_CHANNEL_INFO) {
                                lCurlHandler
                                        .getCurlView()
                                        .setUpNewChannelInfoByIndex(
                                                lCurlHandler
                                                        .getChannelChangeHandler()
                                                        .getChannelIndex());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case PLAYBACK_STOP: {
                        if (OSDHandlerHelper.getHandlerState() == PVR_STATE_PLAY_PLAY_BACK) {
                            // TODO: Applies for main display only
                            final int mDisplayId = 0;
                            A4TVMultimediaController.getControlProvider().play(
                                    mDisplayId);
                            /** Show Info */
                            mActivity.getPageCurl().multimediaController(false);
                        }
                        // Hide multimedia if video stream is in the background
                        if (mActivity.getPageCurl().getHandlerState() == CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER) {
                            mActivity.getMultimediaHandler().closeMultimedia();
                            /** If antenna layout is visible hide it */
                            if (!mCallBackHandler.isAntennaConnected()) {
                                mActivity.getCheckServiceType()
                                        .showNoSignalLayout();
                            }
                        }
                        break;
                    }
                    case NETWORK_CHANGED: {
                        final A4TVAlertDialog alert = new A4TVAlertDialog(
                                mActivity);
                        alert.setOnKeyListener(new OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog,
                                    int keyCode, KeyEvent event) {
                                switch (keyCode) {
                                // ///////////////////////////////////////////////////////////////////
                                // Disable Volume keys when retry scan dialog is
                                // visible
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
                        alert.setTitleOfAlertDialog(R.string.network_changed_dialog_title);
                        alert.setMessage(R.string.network_changed_dialog_need_rescan);
                        alert.setPositiveButton(R.string.button_text_ok,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alert.cancel();
                                    }
                                });
                        // MK: We can offer the user choice to rescan now when
                        // the
                        // NIT scan API is not ready
                        /*
                         * alert.setPositiveButton(R.string.
                         * network_changed_dialog_scan_now, new
                         * android.view.View.OnClickListener() {
                         * @Override public void onClick(View v) { // MK TODO
                         * start quick NIT scan alert.cancel(); } });
                         * alert.setNegativeButton
                         * (R.string.network_changed_dialog_scan_latter, new
                         * android.view.View.OnClickListener() {
                         * @Override public void onClick(View v) { // MK TODO
                         * schadule scan, or do nothing, mw will re scan on
                         * power up alert.cancel(); } });
                         */
                        // show alert dialog
                        alert.show();
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        };
    }
}
