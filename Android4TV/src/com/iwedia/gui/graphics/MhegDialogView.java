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
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.iwedia.comm.IDTVManagerProxy;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVVideoView;

public class MhegDialogView extends SurfaceView {
    public static final String TAG = "MhegDialogView";
    private LinearLayout mMHEGLayout;
    private MHEGActionCallback mMHEGActionCallback;
    private boolean mIsOn;

    public MhegDialogView(Context context) {
        super(context);
        mMHEGLayout = (LinearLayout) activity().findViewById(R.id.mhegLayout);
        FrameLayout.LayoutParams mhegLayoutParams = new FrameLayout.LayoutParams(
                MainActivity.DEFAULT_HD_REGION_WIDTH,
                MainActivity.DEFAULT_HD_REGION_HEIGHT);
        mhegLayoutParams.gravity = Gravity.CENTER;
        mMHEGLayout.setLayoutParams(mhegLayoutParams);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        getHolder().addCallback(getSurfaceCallback());
        setZOrderOnTop(true);
        mMHEGLayout.addView(this);
        setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    }

    public boolean show() {
        Log.i(TAG, "show");
        if (getVisibility() == View.VISIBLE) {
            Log.i(TAG, "show already visible");
            if (!showMHEG()) {
                mIsOn = false;
            } else {
                mIsOn = false;
            }
        } else {
            Log.i(TAG, "show hidden");
            mMHEGActionCallback = new MHEGActionCallback() {
                @Override
                public boolean showMHEGCommand() {
                    return showMHEG();
                }
            };
            Log.i(TAG, "show hidden setVisibility VISIBLE");
            setVisibility(View.VISIBLE);
        }
        return true;
    }

    public boolean hide() throws RemoteException {
        Log.i(TAG, "hide");
        if (!service().getMhegControl().hide()) {
            return false;
        }
        // setVisibility(View.INVISIBLE);
        return true;
    }

    private SurfaceHolder.Callback getSurfaceCallback() {
        return new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format, int w,
                    int h) {
                Log.i(TAG, "surfaceChanged w[" + w + "] h[" + h + "]");
                // 1 - update surface handle in MW
                activity().getPrimaryVideoView().updateGFXDisplaySurface(5,
                        holder.getSurface());
                // 2 - call callback
                if (mMHEGActionCallback != null) {
                    if (!mMHEGActionCallback.showMHEGCommand()) {
                        setVisibility(View.INVISIBLE);
                    }
                    mMHEGActionCallback = null;
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surfaceCreated");
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surfaceDestroyed");
                A4TVVideoView videoView = activity().getPrimaryVideoView();
                if (videoView != null) {
                    videoView.updateGFXDisplaySurface(5, null);
                }
            }
        };
    }

    private boolean showMHEG() {
        boolean res = false;
        Log.i(TAG, "showMHEG");
        try {
            service().getMhegControl().show();
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public abstract class MHEGActionCallback {
        public MHEGActionCallback() {
        }

        public abstract boolean showMHEGCommand();
    }

    private IDTVManagerProxy service() {
        return MainActivity.service;
    }

    private MainActivity activity() {
        return MainActivity.activity;
    }
}
