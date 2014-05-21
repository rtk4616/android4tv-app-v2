package com.iwedia.gui.multimedia;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.VideoView;

import com.iwedia.gui.multimedia.controller.MediaController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MultimediaPlayer extends VideoView {
    private static final String TAG = "MultimediaPlayer";
    private static final boolean DEBUG = true;
    private MediaPlayer mMediaPlayer = null;
    private Rect mRectangle;
    MediaPlayer.OnPreparedListener mVideoPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            if (DEBUG) {
                Log.d(TAG, "mVideoPreparedListener onPrepared");
            }
            mMediaPlayer = mp;
            mMediaPlayer
                    .setOnVideoSizeChangedListener(mVideoSizeChangedListener);
            MediaController.isStopped = false;
        }
    };

    @Override
    public void stopPlayback() {
        Log.e("VideoView", TAG
                + "***********************************  stopPlayback");
        super.stopPlayback();
    }

    @Override
    public void start() {
        Log.e("VideoView", TAG + "***********************************  start");
        super.start();
    }

    public void stop() {
        Log.e("VideoView", TAG + "***********************************  stop");
    }

    MediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (null != mMediaPlayer) {
                scale();
            }
        }
    };
    private MediaPlayer.OnErrorListener mVideoOnErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e(TAG, "On error listener - CODE: " + what);
            if (!MediaController.isStopped) {
                Log.e(TAG, "On error listener - setting isStopped flag to true");
                MediaController.isStopped = true;
                return false;
            }
            return true;
        }
    };

    private Point getDefaultDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public MultimediaPlayer(Context context) {
        super(context);
        Point size = getDefaultDisplaySize(context);
        mRectangle = new Rect(0, 0, size.x, size.y + getStatusBarHeight());
        setOnPreparedListener(mVideoPreparedListener);
        setOnErrorListener(mVideoOnErrorListener);
        MediaController.isStopped = false;
    }

    public void setViewRectangle(int x, int y, int width, int height) {
        if (DEBUG) {
            Log.d(TAG, "MultimediaPlayer rectangle: " + mRectangle);
        }
        mRectangle.left = x;
        mRectangle.top = y;
        mRectangle.bottom = mRectangle.top + height;
        mRectangle.right = mRectangle.left + width;
    }

    public void scale() {
        Method hiddenInvokeMethod = null;
        Parcel request = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        request.writeInterfaceToken("android.media.IMediaPlayer");
        try {
            hiddenInvokeMethod = MediaPlayer.class.getDeclaredMethod("invoke",
                    Parcel.class, Parcel.class);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "Error geting mediaplayer invoke method");
            e.printStackTrace();
        }
        request.writeInt(3);
        request.writeInt(mRectangle.left);
        request.writeInt(mRectangle.top);
        request.writeInt(mRectangle.right - mRectangle.left);
        request.writeInt(mRectangle.bottom - mRectangle.top);
        Object[] params = new Object[] { request, reply };
        try {
            if (hiddenInvokeMethod != null) {
                hiddenInvokeMethod.invoke(mMediaPlayer, params);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG,
                    "Error calling mediaplayer invoke method (IllegalArgumentException)");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG,
                    "Error calling mediaplayer invoke method (IllegalAccessException)");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(TAG,
                    "Error calling mediaplayer invoke method (InvocationTargetException)");
            e.printStackTrace();
        }
    }
}
