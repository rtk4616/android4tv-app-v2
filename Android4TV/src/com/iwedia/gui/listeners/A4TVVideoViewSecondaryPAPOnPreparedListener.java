package com.iwedia.gui.listeners;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVVideoView;

public class A4TVVideoViewSecondaryPAPOnPreparedListener extends
        A4TVVideoViewOnPreparedListener implements OnPreparedListener {
    public static final String LOG_TAG = "A4TVVideoViewSecondaryPAPOnPreparedListener";
    private A4TVVideoView videoView;
    private Content contentToPlay;

    public A4TVVideoViewSecondaryPAPOnPreparedListener(A4TVVideoView videoView,
            Content content) {
        super();
        this.videoView = videoView;
        this.contentToPlay = content;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOG_TAG, "OnPrepared");
        this.mMP = mp;
        this.videoView.setMediaPlayerPrepared();
        this.videoView.gotoPaP(MainActivity.SECONDARY_DISPLAY_UNIT_ID);
        MainActivity.activity.getDualVideoManager().playContent(contentToPlay,
                MainActivity.SECONDARY_DISPLAY_UNIT_ID, true);
        this.videoView.setPlayMode(A4TVVideoView.PAP_DISPLAY_MODE);
        if (mScalingCallback != null) {
            mScalingCallback.setScaling();
            mScalingCallback = null;
        }
    }
}
