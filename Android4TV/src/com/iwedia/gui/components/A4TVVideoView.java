package com.iwedia.gui.components;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.iwedia.comm.IDisplayControl;
import com.iwedia.dtv.display.SurfaceBundle;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.A4TVVideoViewOnPreparedListener;
import com.iwedia.gui.listeners.A4TVVideoViewOnPreparedListener.ScalingCallback;

public class A4TVVideoView extends VideoView {
    private final static String TAG = "A4TVVideoView";
    private final static int PARA_SCALE_DISP_WINDOW = (0x100);
    public final static int NONE_DISPLAY_MODE = 0;
    public final static int PIP_DISPLAY_MODE = 1;
    public final static int PAP_DISPLAY_MODE = 2;
    private int playMode;
    private boolean mMediaPlayerPrepared = false;
    private A4TVVideoViewOnPreparedListener mOnPreparedListener = null;

    public A4TVVideoView(Context context) {
        super(context);
    }

    @Override
    public void stopPlayback() {
        Log.e("VideoView", TAG
                + "***********************************  stopPlayback");
        Log.e("VideoView:", "ConfigHandler.TVPLATFORM:"
                + ConfigHandler.TVPLATFORM);
        if (ConfigHandler.TVPLATFORM == true) {
            Log.e("VideoView", TAG + "super.stopPlayback()");
            super.stopPlayback();
        } else {
            try {
                Log.e("VideoView", TAG
                        + "contentListControl.stopVideoPlayback()");
                MainActivity.service.getContentListControl()
                        .stopVideoPlayback();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        Log.e("VideoView", TAG + "***********************************  start");
        Log.e("VideoView:", "ConfigHandler.TVPLATFORM:"
                + ConfigHandler.TVPLATFORM);
        if (ConfigHandler.TVPLATFORM == true) {
            super.start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2500);
                        MainActivity.service.getContentListControl()
                                .startVideoPlayback();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public A4TVVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public A4TVVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        super.setOnPreparedListener(l);
        mOnPreparedListener = (A4TVVideoViewOnPreparedListener) l;
        playMode = NONE_DISPLAY_MODE;
    }

    public void setScaling(int x, int y, int width, int height) {
        ScalingCallback scalingCallback = mOnPreparedListener.new ScalingCallback(
                x, y, width, height) {
            @Override
            public void setScaling() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                params.width = mW;
                params.height = mH;
                setLayoutParams(params);
                setX(mX);
                setY(mY);
                if (getMediaPlayer() != null) {
                    Parcel request = Parcel.obtain();
                    request.writeInt(mX);
                    request.writeInt(mY);
                    request.writeInt(mW);
                    request.writeInt(mH);
                    MediaPlayer mPlayer = getMediaPlayer();
                    if (mPlayer != null) {
                        mPlayer.setParameter(PARA_SCALE_DISP_WINDOW, request);
                        Log.i(TAG, "setParameter para["
                                + PARA_SCALE_DISP_WINDOW + "] x[" + mX + "] y["
                                + mY + "] width[" + mW + "] height[" + mH + "]");
                    }
                }
                getParent().requestLayout();
            };
        };
        if (mMediaPlayerPrepared) {
            scalingCallback.setScaling();
        } else {
            mOnPreparedListener.setScalingCallback(scalingCallback);
        }
    }

    public void updateVisibility(int visibility) {
        if ((visibility == View.INVISIBLE) || (visibility == View.GONE)) {
            clearMediaPlayerPrepared();
        }
        setVisibility(visibility);
        getParent().requestLayout();
    }

    private MediaPlayer getMediaPlayer() {
        if (mOnPreparedListener == null) {
            return null;
        }
        return mOnPreparedListener.getMediaPlayer();
    }

    public void setMediaPlayerPrepared() {
        mMediaPlayerPrepared = true;
    }

    public void clearMediaPlayerPrepared() {
        mMediaPlayerPrepared = false;
    }

    /*
     * layerID - MW GFX layer ID (TTXT=3, STT=4, MHEG=5) surface - surface
     * object from surface view holder
     */
    public void updateGFXDisplaySurface(int layerID, Surface surface) {
        try {
            IDisplayControl displayControl = MainActivity.service
                    .getDisplayControl();
            SurfaceBundle surfaceBundle = new SurfaceBundle();
            surfaceBundle.setSurface(surface);
            // Log.i(TAG, "updateGFXDisplaySurface layerID[" + layerID +
            // "] surface[" + surface + "] surfaceBundle.surface[" +
            // surfaceBundle.getSurface() + "]!");
            displayControl.setVideoLayerSurface(layerID, surfaceBundle);
        } catch (RemoteException e) {
            Log.e(TAG,
                    "updateGFXDisplaySurface failed to set video layer surface!");
        }
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }

    /** Scale VideoView to pip coordinates */
    public void gotoPIP() {
        Log.d(TAG, "gotoPIP");
        MainActivity.activity.updatePIPCoordinates();
        updateVisibility(View.VISIBLE);
        setScaling(MainActivity.pipWindowCoordinateLeft,
                MainActivity.pipWindowCoordinateTop,
                MainActivity.pipWindowWidth, MainActivity.pipWindowHeight);
    }

    /** Scale VideoView to pap coordinates */
    public void gotoPaP(int displayID) {
        /**
         * deppending on display id scale will be on the left or right side of
         * the screen
         */
        Log.d(TAG, "gotoPaP - display id:" + displayID);
        if (displayID == MainActivity.PRIMARY_DISPLAY_UNIT_ID) {
            updateVisibility(View.VISIBLE);
            setScaling(0, 0, 960, 1080);
        } else if (displayID == MainActivity.SECONDARY_DISPLAY_UNIT_ID) {
            updateVisibility(View.VISIBLE);
            setScaling(960, 0, 960, 1080);
        }
    }

    /** Scale VideoView to full screen */
    public void gotoFullScreen() {
        Log.d(TAG, "gotoFullScreen");
        updateVisibility(View.VISIBLE);
        setScaling(0, 0, 1920, 1080);
    }

    /** Scale to zero and hide */
    public void hide() {
        Log.d(TAG, "hide");
        // setScaling(0, 0, 0, 0);
        updateVisibility(View.INVISIBLE);
    }
}
