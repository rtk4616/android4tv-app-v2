package com.iwedia.service.overlay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Parcel;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.iwedia.service.R;

public class OverlayPlayer {
    private final static String TAG = "OverlayPlayer";
    private final static int PARA_SCALE_DISP_WINDOW = (0x100);
    private final static int PARA_GFX_DISP_SURFACE = (0x101);
    public final static int NONE_DISPLAY_MODE = 0;
    public final static int PIP_DISPLAY_MODE = 1;
    public final static int PAP_DISPLAY_MODE = 2;
    private Context mContext;
    private RelativeLayout mOverlayMain;
    private RelativeLayout mOverlayLayer;
    private VideoView mVideoView = null;
    int video_x = 10;
    int video_y = 50;
    int video_w = 640;
    int video_h = 480;

    public OverlayPlayer(Context ctx) {
        mContext = ctx;
        Log.d(TAG, "STARTING OVERLAY PLAYER...");
        mOverlayMain = (RelativeLayout) View.inflate(mContext,
                R.layout.overlay_player, null);
        mOverlayMain.setBackgroundColor(Color.TRANSPARENT);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSPARENT);
        lp.gravity = Gravity.NO_GRAVITY;
        lp.setTitle("OverlayPlayerMain");
        lp.packageName = mContext.getPackageName();
        // WindowManagerImpl.getDefault().addView(mOverlayMain, lp);
        mOverlayLayer = (RelativeLayout) mOverlayMain
                .findViewById(R.id.overlayPlayerLayout);
    }

    public void start(String play_uri, String video_size) {
        if (video_size != null) {
            String dim[] = video_size.split(",");
            if (dim.length == 4) {
                try {
                    video_x = Integer.parseInt(dim[0]);
                    video_y = Integer.parseInt(dim[1]);
                    video_w = Integer.parseInt(dim[2]);
                    video_h = Integer.parseInt(dim[3]);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing video size: " + video_size);
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Invalid video size: " + video_size);
            }
            Log.d(TAG, "Video size: " + video_x + ", " + video_y + ", "
                    + video_w + ", " + video_h);
        }
        createVideoView();
        if (play_uri != null) {
            mVideoView.setVideoURI(Uri.parse(play_uri));
        }
    }

    public void stop() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        // WindowManagerImpl.getDefault().removeView(mOverlayMain);
    }

    public void prepareSize(int x, int y, int w, int h) {
        video_x = x;
        video_y = y;
        video_w = w;
        video_h = h;
    }

    private void clearVideoView() {
        mVideoView = null;
        mOverlayLayer.removeAllViews();
    }

    private void createVideoView() {
        if (mVideoView != null) {
            clearVideoView();
        }
        mVideoView = new VideoView(mContext);
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(video_w,
                video_h));
        mOverlayLayer.setX(video_x);
        mOverlayLayer.setY(video_y);
        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.setZOrderOnTop(true);
        mVideoView.setZOrderMediaOverlay(false);
        mVideoView.invalidate();
        mOverlayLayer.addView(mVideoView);
        // borders!
        mOverlayLayer.setLayoutParams(new RelativeLayout.LayoutParams(
                video_w + 10, video_h + 10));
        mVideoView.setX(5);
        mVideoView.setY(5);
        mOverlayLayer.requestLayout();
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepare");
                mVideoView.start();
                Parcel request = Parcel.obtain();
                request.writeInt(video_x);
                request.writeInt(video_y);
                request.writeInt(video_w);
                request.writeInt(video_h);
                // mp.setParameter(PARA_SCALE_DISP_WINDOW, request);
            }
        });
    }
}
