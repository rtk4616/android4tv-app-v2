package com.iwedia.gui.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import com.iwedia.comm.IDTVManagerProxy;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVVideoView;

public class SubtitleDialogView extends SurfaceView {
    public static final String TAG = "SubtitleDialogView";
    private LinearLayout mSubtitleLayout;
    private STTActionCallback mSTTActionCallback;
    private boolean mIsOn;

    // private static SubtitleDialogView subtitleDialogView;
    public SubtitleDialogView(Context context) {
        super(context);
        mSubtitleLayout = (LinearLayout) activity().findViewById(
                R.id.subtitleLayout);
        LayoutParams subtitleLayoutParams = new LayoutParams(
                MainActivity.DEFAULT_HD_REGION_WIDTH,
                MainActivity.DEFAULT_HD_REGION_HEIGHT);
        subtitleLayoutParams.gravity = Gravity.CENTER;
        mSubtitleLayout.setLayoutParams(subtitleLayoutParams);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        getHolder().addCallback(getSurfaceCallback());
        setZOrderOnTop(true);
        mSubtitleLayout.addView(this);
        setVisibility(View.INVISIBLE);
        mIsOn = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    public void available() {
        Log.i(TAG, "available");
        int trackIndex = -1;
        try {
            if (service().getSubtitleControl().getSubtitleEnabled()) {
                trackIndex = service().getSubtitleControl()
                        .getCurrentSubtitleTrackIndex();
            }
            Log.i(TAG, "available trackIndex[" + trackIndex + "]");
            // if (trackIndex != 0xFFFF)
            show(trackIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 1 - check automatic is on
        // 2 - if on, get trackIndex
        // 3 - call show track
    }

    public boolean show(int trackIndex) {
        Log.i(TAG, "show  trackIndex[" + trackIndex + "]");
        if (getVisibility() == View.VISIBLE) {
            if (showSTT(trackIndex)) {
                mIsOn = true;
            } else {
                mIsOn = false;
            }
        } else {
            mSTTActionCallback = new STTActionCallback(trackIndex) {
                @Override
                public boolean showSTTCommand(int trackIndex) {
                    return showSTT(trackIndex);
                }
            };
            setVisibility(View.VISIBLE);
        }
        return true;
    }

    public void hideView() {
        Log.i(TAG, "hideView");
        // setVisibility(View.INVISIBLE);
        mIsOn = false;
    }

    public boolean hide() throws RemoteException {
        Log.i(TAG, "hide");
        if (!mIsOn) {
            return true;
        }
        if (!service().getSubtitleControl().hide()) {
            return false;
        }
        return true;
    }

    public boolean isOn() {
        Log.i(TAG, "isOn [" + mIsOn + "]");
        return mIsOn;
    }

    private IDTVManagerProxy service() {
        return MainActivity.service;
    }

    private MainActivity activity() {
        return MainActivity.activity;
    }

    private SurfaceHolder.Callback getSurfaceCallback() {
        return new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format, int w,
                    int h) {
                Log.i(TAG, "surfaceChanged w[" + w + "] h[" + h + "]");
                // 1 - update surface handle in MW
                activity().getPrimaryVideoView().updateGFXDisplaySurface(4,
                        holder.getSurface());
                // 2 - call callback
                if (mSTTActionCallback != null) {
                    if (!mSTTActionCallback.callShowSTT()) {
                        mIsOn = false;
                        // setVisibility(View.INVISIBLE);
                    }
                    mSTTActionCallback = null;
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surfaceCreated");
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surfaceDestroyed");
                // 1 - stop sub
                mIsOn = false;
                // - clear surface handle in MW
                A4TVVideoView videoView = activity().getPrimaryVideoView();
                if (videoView != null) {
                    videoView.updateGFXDisplaySurface(4, null);
                }
            }
        };
    }

    private boolean showSTT(int trackIndex) {
        boolean res = false;
        mIsOn = false;
        try {
            service().getSubtitleControl().setCurrentSubtitleTrack(trackIndex);
            service().getSubtitleControl().show();
            mIsOn = true;
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private abstract class STTActionCallback {
        private int mTrackIndex;

        public STTActionCallback(int trackIndex) {
            mTrackIndex = trackIndex;
        }

        public boolean callShowSTT() {
            return showSTT(mTrackIndex);
        }

        public abstract boolean showSTTCommand(int trackIndex);
    }
}
