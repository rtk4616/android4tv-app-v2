package com.iwedia.gui.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.iwedia.comm.teletext.TeletextMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.A4TVVideoView;
import com.iwedia.gui.listeners.MainKeyListener;

public class TeletextDialogView extends SurfaceView {
    public static final String TAG = "TeletextDialogView";
    private LinearLayout mTeletextLayout;
    private TTXTActionCallback mTTXTActionCallback;

    public TeletextDialogView(Context context) {
        super(context);
        mTeletextLayout = (LinearLayout) MainActivity.activity
                .findViewById(R.id.teletextLayout);
        FrameLayout.LayoutParams teletextLayoutParams = new FrameLayout.LayoutParams(
                MainActivity.screenWidth, MainActivity.screenHeight);
        teletextLayoutParams.gravity = Gravity.CENTER;
        mTeletextLayout.setLayoutParams(teletextLayoutParams);
        getHolder().setFormat(PixelFormat.RGBA_8888);
        getHolder().addCallback(getSurfaceCallback());
        setZOrderOnTop(true);
        mTeletextLayout.addView(this);
        setBackgroundColor(Color.TRANSPARENT);
        setVisibility(View.INVISIBLE);
    }

    public boolean show(TeletextMode teletextMode, int trackIndex) {
        Log.i(TAG, "show teletextMode[" + teletextMode + "] trackIndex["
                + trackIndex + "]");
        if (getVisibility() == View.VISIBLE) {
            if (!showTTXT(teletextMode, trackIndex)) {
                A4TVToast toast = new A4TVToast(getContext());
                toast.showToast(R.string.no_teletext_available);
            }
        } else {
            mTTXTActionCallback = new TTXTActionCallback(teletextMode,
                    trackIndex) {
                @Override
                public boolean showTTXTCommand(TeletextMode teletextMode,
                        int trackIndex) {
                    return showTTXT(teletextMode, trackIndex);
                }
            };
            setVisibility(View.VISIBLE);
        }
        return true;
    }

    public boolean hide() throws RemoteException {
        Log.i(TAG, "hide");
        if (!service().getTeletextControl().deselectCurrentTeletextTrack()) {
            return false;
        }
        setVisibility(View.INVISIBLE);
        return true;
    }

    public TeletextMode getMode() throws RemoteException {
        return service().getTeletextControl().getTeletextMode();
    }

    public void setMode(TeletextMode teletextMode) throws RemoteException {
        service().getTeletextControl().setTeletextMode(teletextMode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
    }

    private SurfaceHolder.Callback getSurfaceCallback() {
        return new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format, int w,
                    int h) {
                Log.e(TAG, "surfaceChanged w[" + w + "] h[" + h + "]");
                // 1 - update surface handle in MW
                activity().getPrimaryVideoView().updateGFXDisplaySurface(3,
                        holder.getSurface());
                // 2 - call callback
                if (mTTXTActionCallback != null) {
                    if (!mTTXTActionCallback.callShowTTXT()) {
                        A4TVToast toast = new A4TVToast(getContext());
                        toast.showToast(R.string.no_teletext_available);
                        setVisibility(View.INVISIBLE);
                    }
                    mTTXTActionCallback = null;
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                Log.e(TAG, "surfaceCreated");
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e(TAG, "surfaceDestroyed");
                // 1 - stop ttx
                try {
                    service().getTeletextControl()
                            .deselectCurrentTeletextTrack();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                // - clear surface handle in MW
                A4TVVideoView videoView = activity().getPrimaryVideoView();
                if (videoView != null) {
                    videoView.updateGFXDisplaySurface(3, null);
                }
            }
        };
    }

    public abstract class TTXTActionCallback {
        private int mTrackIndex;
        private TeletextMode mTeletextMode;

        public TTXTActionCallback(TeletextMode teletextMode, int trackIndex) {
            mTrackIndex = trackIndex;
            mTeletextMode = teletextMode;
        }

        public boolean callShowTTXT() {
            Log.e(TAG, "callShowTTXT[" + mTeletextMode + "]");
            return showTTXT(mTeletextMode, mTrackIndex);
        }

        public abstract boolean showTTXTCommand(TeletextMode teletextMode,
                int trackIndex);
    }

    private IDTVManagerProxy service() {
        return MainActivity.service;
    }

    private MainActivity activity() {
        return MainActivity.activity;
    }

    private boolean showTTXT(TeletextMode teletextMode, int trackIndex) {
        boolean res = true;
        try {
            switch (teletextMode) {
                case FULL:
                    Log.e(TAG, "show teletextMode[" + teletextMode + "]");
                    if (mTeletextLayout.getWidth() != MainActivity.screenWidth) {
                        FrameLayout.LayoutParams teletextLayoutParams = new FrameLayout.LayoutParams(
                                MainActivity.screenWidth,
                                MainActivity.screenHeight);
                        teletextLayoutParams.gravity = Gravity.CENTER;
                        mTeletextLayout.setLayoutParams(teletextLayoutParams);
                        requestLayout();
                        mTTXTActionCallback = new TTXTActionCallback(
                                teletextMode, trackIndex) {
                            @Override
                            public boolean showTTXTCommand(
                                    TeletextMode teletextMode, int trackIndex) {
                                return showTTXT(teletextMode, trackIndex);
                            }
                        };
                    } else {
                        service().getTeletextControl().setTeletextMode(
                                teletextMode);
                        service().getTeletextControl().setTeletextBgAlpha(0);
                        if (service().getTeletextControl()
                                .setCurrentTeletextTrack(trackIndex) != true) {
                            res = false;
                        } else {
                            MainKeyListener
                                    .setAppState(MainKeyListener.TELETEXT);
                        }
                    }
                    break;
                case MIX:
                    Log.e(TAG, "show teletextMode[" + teletextMode + "]");
                    service().getTeletextControl()
                            .deselectCurrentTeletextTrack();
                    if (mTeletextLayout.getWidth() != MainActivity.screenWidth) {
                        FrameLayout.LayoutParams teletextLayoutParams = new FrameLayout.LayoutParams(
                                MainActivity.screenWidth,
                                MainActivity.screenHeight);
                        teletextLayoutParams.gravity = Gravity.CENTER;
                        mTeletextLayout.setLayoutParams(teletextLayoutParams);
                        requestLayout();
                        mTTXTActionCallback = new TTXTActionCallback(
                                teletextMode, trackIndex) {
                            @Override
                            public boolean showTTXTCommand(
                                    TeletextMode teletextMode, int trackIndex) {
                                return showTTXT(teletextMode, trackIndex);
                            }
                        };
                    } else {
                        service().getTeletextControl().setTeletextMode(
                                teletextMode);
                        service().getTeletextControl().setTeletextBgAlpha(255);
                        if (service().getTeletextControl()
                                .setCurrentTeletextTrack(trackIndex) != true) {
                            res = false;
                        }
                    }
                    break;
                case HALF:
                    Log.e(TAG, "show teletextMode[" + teletextMode + "]");
                    service().getTeletextControl()
                            .deselectCurrentTeletextTrack();
                    if (mTeletextLayout.getWidth() != MainActivity.screenWidth / 2) {
                        FrameLayout.LayoutParams teletextLayoutParams = new FrameLayout.LayoutParams(
                                MainActivity.screenWidth / 2,
                                MainActivity.screenHeight);
                        teletextLayoutParams.gravity = Gravity.RIGHT;
                        mTeletextLayout.setLayoutParams(teletextLayoutParams);
                        requestLayout();
                        mTTXTActionCallback = new TTXTActionCallback(
                                teletextMode, trackIndex) {
                            @Override
                            public boolean showTTXTCommand(
                                    TeletextMode teletextMode, int trackIndex) {
                                return showTTXT(teletextMode, trackIndex);
                            }
                        };
                    } else {
                        service().getTeletextControl().setTeletextMode(
                                teletextMode);
                        service().getTeletextControl().setTeletextBgAlpha(0);
                        if (service().getTeletextControl()
                                .setCurrentTeletextTrack(trackIndex) != true) {
                            res = false;
                        }
                    }
                    break;
                default:
                    Log.e(TAG, "show teletextMode[" + teletextMode + "]");
                    service().getTeletextControl().setTeletextMode(
                            TeletextMode.OFF);
                    service().getTeletextControl().setTeletextBgAlpha(0);
                    service().getTeletextControl()
                            .deselectCurrentTeletextTrack();
                    res = false;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
