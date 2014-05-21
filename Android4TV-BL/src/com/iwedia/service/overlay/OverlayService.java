package com.iwedia.service.overlay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class OverlayService extends Service {
    private final static String TAG = "OverlayService";
    public String ACTION_PLAY_OVERLAY = "iwedia.intent.action.PLAY_OVERLAY";
    public String ACTION_STOP_OVERLAY = "iwedia.intent.action.STOP_OVERLAY";
    public String EXTRA_PLAY_URI = "play_uri";
    public String EXTRA_VIDEO_SIZE = "video_size";
    private OverlayPlayer mOverlayPlayer;
    Handler mHandler = new Handler();
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "INTENT: action=" + action);
            if (action != null) {
                if (action.equals(ACTION_PLAY_OVERLAY)) {
                    // play overlay
                    String play_uri = intent.getStringExtra(EXTRA_PLAY_URI);
                    String video_size = intent.getStringExtra(EXTRA_VIDEO_SIZE);
                    Log.d(TAG, "creating overlay player...");
                    mOverlayPlayer.start(play_uri, video_size);
                } else if (action.equals(ACTION_STOP_OVERLAY)) {
                    // stop overlay
                    mOverlayPlayer.stop();
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mOverlayPlayer = new OverlayPlayer(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY_OVERLAY);
        filter.addAction(ACTION_STOP_OVERLAY);
        registerReceiver(mIntentReceiver, filter, null, mHandler);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        String play_uri = intent.getStringExtra(EXTRA_PLAY_URI);
        String video_size = intent.getStringExtra(EXTRA_VIDEO_SIZE);
        Log.d(TAG, "onStart: play_uri=" + play_uri + " video_size="
                + video_size);
        mOverlayPlayer.start(play_uri, video_size);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(mIntentReceiver);
        mOverlayPlayer.stop();
    }
}
