package com.iwedia.gui.pip;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.VideoView;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVVideoView;
import com.iwedia.gui.multimedia.MultimediaGlobal;
import com.iwedia.gui.multimedia.dlna.renderer.controller.RendererController;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PiPView {
    public class UriType {
        public static final int URI_FILE_TYPE = 0;
        public static final int URI_STREAM_TYPE = 1;
    };

    private static final String TAG = "PiPView";
    private static final boolean DEBUG = true;
    private MainActivity mActivity;
    private View mView;
    private Boolean mScalled;
    private Rect mRectangle;
    private MediaPlayer mVideoMediaPlayer;
    private Uri mUri;
    private int mUriType;
    MediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            if (null != mVideoMediaPlayer) {
                scaleView();
                show(true);
            }
        }
    };
    MediaPlayer.OnPreparedListener mVideoPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            if (DEBUG) {
                Log.d(TAG, "mVideoPreparedListener onPrepared");
            }
            mVideoMediaPlayer = mp;
            if (mView.getClass() == A4TVVideoView.class) {
                ((A4TVVideoView) mView).start();
            } else if (mView.getClass() == VideoView.class) {
                mVideoMediaPlayer.start();
                mVideoMediaPlayer
                        .setOnVideoSizeChangedListener(mVideoSizeChangedListener);
                MainActivity.activity
                        .getPiPController()
                        .setmPiPControllerState(
                                PiPController.PiPState.PIP_CONTROLLER_STATE_PLAY);
            };
        }
    };
    private MediaPlayer.OnCompletionListener mVideoCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            if (DEBUG) {
                Log.d(TAG, "mVideoCompletionListener onCompletion");
            }
            if (mActivity.getRendererController().isPiPMode()) {
                Log.d(TAG, "onCompletion Renderer is in PiP mode");
                mActivity.getRendererController().onCompletion();
            } else {
                if (mActivity.getPiPController().getmPiPControllerState() == PiPController.PiPState.PIP_CONTROLLER_STATE_PLAY) {
                    stop();
                } else {
                    Log.d(TAG, "onCompletion Nothing to do, already stopped");
                }
            }
            RendererController.setOnCompletition(true);
        }
    };
    private MediaPlayer.OnErrorListener mVideoOnErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e(TAG, "On error listener - CODE: " + what);
            if (MainActivity.activity.getRendererController()
                    .getmRendererState() == MultimediaGlobal.RENDERER_STATE_PLAY_PIP) {
                // if mVideoMediaPlayer is null, onPrepared never happend
                if (mVideoMediaPlayer == null) {
                    // Notify renderer to stop and set state to
                    // RENDERER_STATE_STOP
                    try {
                        MainActivity.service.getDlnaControl()
                                .notifyDlnaRenderer(0, 0, "");
                        MainActivity.activity.getRendererController()
                                .setmRendererState(
                                        MultimediaGlobal.RENDERER_STATE_STOP);
                        return false;
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return false;
            }
            return true;
        }
    };

    public PiPView(MainActivity activity) {
        if (DEBUG) {
            Log.d(TAG, "PiPView Constructor");
        }
        mActivity = activity;
        mScalled = false;
        /**
         * Default rectangle 640x360 in upper right corner. It will be scaled up
         * to 960x540
         */
        mRectangle = new Rect(640, 0, 1280, 360);
        if (DEBUG) {
            Log.d(TAG, "Default video rectangle " + mRectangle);
        }
    }

    public void clear() {
        stop();
        mVideoMediaPlayer = null;
        mView = null;
    }

    public void attachView(View view) {
        if (DEBUG) {
            Log.d(TAG, "attachView");
        }
        mView = view;
    }

    public View getPiPVideoView() {
        if (DEBUG) {
            Log.d(TAG, "getPiPVideoView");
        }
        return mView;
    }

    public int setUri(String uriString) {
        if (DEBUG) {
            Log.d(TAG, "setUri from String : " + uriString);
        }
        mUri = null;
        /** File or stream - can be relevant if using MediaPlayer ... */
        if (uriString.startsWith("/")) {
            File file = new File(uriString);
            mUri = Uri.fromFile(file);
            mUriType = UriType.URI_FILE_TYPE;
            Log.d(TAG, "setUri - file uri:" + mUri.toString());
        } else {
            Log.d(TAG, "setUri - NOT starting with root");
            mUri = Uri.parse(uriString);
            mUriType = UriType.URI_STREAM_TYPE;
        }
        return 0;
    }

    public int show(Boolean show) {
        return 0;
    }

    public int start() {
        if (DEBUG) {
            Log.d(TAG, "start");
        }
        if (mView != null) {
            if (mView.getClass() == A4TVVideoView.class) {
                ((A4TVVideoView) mView)
                        .setOnPreparedListener(mVideoPreparedListener);
                ((A4TVVideoView) mView).setVideoURI(mUri);
            } else if (mView.getClass() == VideoView.class) {
                ((VideoView) mView)
                        .setOnPreparedListener(mVideoPreparedListener);
                ((VideoView) mView)
                        .setOnCompletionListener(mVideoCompletionListener);
                ((VideoView) mView).setOnErrorListener(mVideoOnErrorListener);
                ((VideoView) mView).setVideoURI(mUri);
            } else if (mView.getClass() == WebView.class) {
                ((WebView) mView).getSettings().setJavaScriptEnabled(true);
                ((WebView) mView).getSettings().setPluginsEnabled(true);
                ((WebView) mView).getSettings().setUserAgentString("test");
                ((WebView) mView).getSettings().setUseWideViewPort(true);
                ((WebView) mView).setInitialScale(30);
                ((WebView) mView).setWebViewClient(new WebViewClient() {
                });
                (mView).setOnKeyListener(MainActivity.activity
                        .getMainKeyListener());
                ((WebView) mView).loadUrl(mUri.toString());
                scaleView();
                show(true);
            } else {
                Log.d(TAG, "unknown object");
            }
        } else {
            Log.d(TAG, "No video View attached");
        }
        return 0;
    }

    public int stop() {
        if (DEBUG) {
            Log.d(TAG, "stop");
        }
        if (mView.getClass() == WebView.class) {
            ((WebView) mView).destroy();
            mView = null;
        } else if (mView.getClass() == A4TVVideoView.class) {
            ((A4TVVideoView) mView).stopPlayback();
        } else if (mView.getClass() == VideoView.class) {
            seekTo(0);
            ((VideoView) mView).stopPlayback();
            MainActivity.activity.getPiPController().setmPiPControllerState(
                    PiPController.PiPState.PIP_CONTROLLER_STATE_STOP);
        }
        if (mVideoMediaPlayer != null) {
            mVideoMediaPlayer.release();
            mVideoMediaPlayer = null;
        }
        mView = null;
        return 0;
    }

    public int sendInputControl(KeyEvent event, int keyCode) {
        // if (mView instanceof A4TVVideoView) {
        //
        // } else if (mView.getClass() == VideoView.class) {
        //
        // }
        return 0;
    }

    public void setViewRectangle(int x, int y, int width, int height) {
        if (DEBUG) {
            Log.d(TAG, "setViewRectangle x = " + x + " y = " + y + " width = "
                    + width + " height = " + height);
        }
        mRectangle.left = x;
        mRectangle.top = y;
        mRectangle.bottom = mRectangle.top + height;
        mRectangle.right = mRectangle.left + width;
    }

    private void scaleView() {
        if (mView instanceof WebView) {
            /** Scaling web view is done by setting web view layout params */
            LayoutParams param = (mView).getLayoutParams();
            param.height = mRectangle.bottom - mRectangle.top;
            param.width = mRectangle.right - mRectangle.left;
            (mView).setLayoutParams(param);
            (mView).setX(mRectangle.left);
            (mView).setY(mRectangle.top);
        } else {
            /** Scaling A4TV and VideoView is done by invoke */
            Parcel request = Parcel.obtain();
            request.writeInterfaceToken("android.media.IMediaPlayer");
            Parcel reply = Parcel.obtain();
            Method hiddenInvokeMethod = null;
            try {
                hiddenInvokeMethod = MediaPlayer.class.getDeclaredMethod(
                        "invoke", Parcel.class, Parcel.class);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "Error geting mediaplayer invoke method");
                e.printStackTrace();
            }
            // No need to change access rights since invoke method is public
            // already
            // but just in case
            if (hiddenInvokeMethod != null) {
                hiddenInvokeMethod.setAccessible(true);
            }
            request.writeInt(3);
            request.writeInt(mRectangle.left);
            request.writeInt(mRectangle.top);
            request.writeInt(mRectangle.right - mRectangle.left);
            request.writeInt(mRectangle.bottom - mRectangle.top);
            Object[] params = new Object[] { request, reply };
            try {
                if (hiddenInvokeMethod != null) {
                    hiddenInvokeMethod.invoke(mVideoMediaPlayer, params);
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

    public boolean isPlaying() {
        if (mView == null) {
            return false;
        }
        if (mView instanceof WebView) {
            return true;
        } else {
            if (mVideoMediaPlayer != null) {
                return mVideoMediaPlayer.isPlaying();
            }
        }
        return false;
    }

    /**
     * Seek File to given milliseconds
     * 
     * @param milliseconds
     *        - Milliseconds
     * @return True if succeed or False if not
     */
    public boolean seekTo(int milliseconds) {
        if (mView != null) {
            if (((VideoView) mView).canSeekBackward()
                    && ((VideoView) mView).canSeekForward()) {
                ((VideoView) mView).seekTo(milliseconds);
                return true;
            }
        }
        return false;
    }

    /**
     * Resume
     * 
     * @return True if succeed or False if not
     */
    public boolean resume() {
        if (mView != null) {
            if (!((VideoView) mView).isPlaying()) {
                ((VideoView) mView).start();
                return true;
            }
        }
        return false;
    }

    public int getElapsedTime() {
        if (mView != null) {
            if (((VideoView) mView).isPlaying()) {
                return ((VideoView) mView).getCurrentPosition();
            }
        }
        return 0;
    }
}
