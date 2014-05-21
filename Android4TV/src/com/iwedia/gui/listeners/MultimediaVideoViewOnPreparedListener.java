package com.iwedia.gui.listeners;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.listeners.A4TVVideoViewOnPreparedListener.ScalingCallback;

public class MultimediaVideoViewOnPreparedListener implements
        OnPreparedListener {
    private final String TAG = "MultimediaVideoViewOnPreparedListener";
    private boolean mIsPrimaryView = false;
    private ScalingCallback mScalingCallback = null;

    public MultimediaVideoViewOnPreparedListener(boolean isPrimaryView) {
        mIsPrimaryView = isPrimaryView;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, (mIsPrimaryView ? "Primary" : "Secondary")
                + " MultimediaVideoView OnPrepared");
        MainActivity.activity.getPrimaryMultimediaVideoView().start();
        MainActivity.activity.getPrimaryMultimediaVideoView().setVideoSize(
                mp.getVideoWidth(), mp.getVideoHeight());
        MainActivity.activity.getPrimaryMultimediaVideoView().updateWidow();
        if (mIsPrimaryView) {
        } else {
            Log.d(TAG, "Mute Player");
            mp.setVolume(0, 0);
        }
    }
}
