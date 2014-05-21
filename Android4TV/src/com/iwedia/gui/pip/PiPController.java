package com.iwedia.gui.pip;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;

public class PiPController {
    private final String TAG = "PiPController";
    private final boolean DEBUG = true;
    private Activity mActivity = null;
    private PiPView mPiPPView = null;
    private WebView mWebView = null;
    private int mPiPControllerState = PiPState.PIP_CONTROLLER_STATE_STOP;

    public static class PiPState {
        public static final int PIP_CONTROLLER_STATE_STOP = 0;
        public static final int PIP_CONTROLLER_STATE_PLAY = 1;
    }

    public static class PiPMode {
        public static final int PIP_VIDEO = 1;
        public static final int PIP_WEB_VIEW = 2;
    }

    public PiPController(Activity activity, PiPView view) {
        mActivity = activity;
        mPiPPView = view;
        setmPiPControllerState(PiPState.PIP_CONTROLLER_STATE_STOP);
    }

    public void play(String uriString, int source) {
        switch (source) {
        /** Video View */
            case PiPMode.PIP_VIDEO: {
                if (MainActivity.activity.getSecondaryVideoView().isPlaying() != false) {
                    stop();
                }
                if (DEBUG) {
                    Log.d(TAG, "Play video in PiP");
                }
                MainActivity.activity.playMultimediaVideo(uriString, 1);
            }
                break;
            /** Web View */
            case PiPMode.PIP_WEB_VIEW: {
                if (mPiPPView.isPlaying() != false) {
                    stop();
                }
                if (DEBUG) {
                    Log.d(TAG, "PiP using Web View");
                }
                RelativeLayout layout = (RelativeLayout) MainActivity.activity
                        .findViewById(R.id.a4tv_main);
                mWebView = new WebView(mActivity);
                layout.addView(mWebView);
                mPiPPView.setUri(uriString);
                mPiPPView.attachView(mWebView);
                mPiPPView.setViewRectangle(640, 0, 640, 360);
                mPiPPView.start();
            }
                break;
            default:
                break;
        }
    }

    public void stop() {
        if (mPiPPView.isPlaying() == true) {
            if (DEBUG) {
                Log.d(TAG, "Stop PiP");
            }
            MainActivity.activity.getSecondaryVideoView().pause();
            MainActivity.activity.getSecondaryVideoView().stopPlayback();
            // // WebView PIP
            // if (mWebView != null) {
            // RelativeLayout layout = (RelativeLayout)
            // MainActivity.activity.findViewById(R.id.a4tv_main);
            // layout.removeView(mWebView);
            // mPiPPView.stop();
            // mWebView.destroy();
            // mWebView = null;
            // } else {
            // if (MainActivity.activity.getRendererController().isPiPMode())
            // MainActivity.activity.getRendererController().stop();
            // else
            // mPiPPView.stop();
            // }
        }
    }

    public int getmPiPControllerState() {
        return mPiPControllerState;
    }

    public void setmPiPControllerState(int mPiPControllerState) {
        this.mPiPControllerState = mPiPControllerState;
    }
}