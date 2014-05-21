package com.iwedia.gui.listeners;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;

import com.iwedia.gui.MainActivity;

public class A4TVVideoViewOnPreparedListener implements OnPreparedListener {
    private final String TAG = "A4TVVideoViewOnPreparedListener";
    protected MediaPlayer mMP = null;
    protected ScalingCallback mScalingCallback = null;
    private boolean isInitialized = false;

    public A4TVVideoViewOnPreparedListener() {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMP = mp;
        MainActivity.activity.getPrimaryVideoView().setMediaPlayerPrepared();
        MainActivity.activity.getPrimaryVideoView().start();
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.activity.getPrimaryVideoView().setScaling(0, 0,
                        1920, 1080);
            }
        });
        A4TVVideoViewOnCompletionListener.setVideoViewError(false);
        if (!isInitialized) {
            MainActivity.activity.tryConnectWithServer();
            /*
             * A4TVToast toast = new A4TVToast(MainActivity.activity);
             * toast.showToast(R.string.middleware_is_initialized);
             */
            isInitialized = true;
        }
        if (mScalingCallback != null) {
            mScalingCallback.setScaling();
            mScalingCallback = null;
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMP;
    }

    public abstract class ScalingCallback {
        public int mX = 0, mY = 0, mW = 0, mH = 0;

        public ScalingCallback(int x, int y, int w, int h) {
            mX = x;
            mY = y;
            mW = w;
            mH = h;
        }

        public abstract void setScaling();
    }

    public void setScalingCallback(ScalingCallback callback) {
        mScalingCallback = callback;
    }
}
