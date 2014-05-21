package com.iwedia.gui.callbacks;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IDlnaCallback;
import com.iwedia.gui.MainActivity;

public class DLNACallBack extends IDlnaCallback.Stub {
    private static final String TAG = "DLNACallBack";
    /** Messages */
    private static final int HBB_DIALOG = 0;
    /** Instance of MainActivity */
    private MainActivity mActivity = null;
    /** Instance of CallBack Handler. */
    private CallBackHandler mCallBackHandler = null;
    /** Handler */
    private Handler mHandler = null;

    public DLNACallBack(CallBackHandler callBackHandler, MainActivity activity) {
        mCallBackHandler = callBackHandler;
        mActivity = activity;
        initializeHandler();
    }

    @Override
    public void dlnaPauseRendererEvent() throws RemoteException {
        Log.i(TAG, "dlnaPauseRendererEvent");
        mActivity.getRendererController().pause();
    }

    @Override
    public void dlnaResumeRendererEvent() throws RemoteException {
        Log.i(TAG, "dlnaResumeRendererEvent");
        mActivity.getRendererController().resume();
    }

    @Override
    public void dlnaStopRendererEvent() throws RemoteException {
        Log.i(TAG, "dlnaStopRendererEvent");
        mActivity.getRendererController().stop();
    }

    @Override
    public void dlnaPositionRendererEvent() throws RemoteException {
        Log.i(TAG, "dlnaPositionRendererEvent");
        mActivity.getRendererController().setElapsedTime();
    }

    @Override
    public void dlnaSeekToRendererEvent(int milliseconds)
            throws RemoteException {
        Log.i(TAG, "dlnaSeekToRendererEvent");
        mActivity.getRendererController().seekTo(milliseconds);
    }

    @Override
    public void dlnaPlayRendererEvent(String uri, String friendlyName,
            String mime) throws RemoteException {
        Log.i(TAG, "dlnaPlayRendererEvent");
        Message.obtain(mHandler, HBB_DIALOG);
        mActivity.getRendererController().play(uri, friendlyName, mime);
    }

    private void initializeHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HBB_DIALOG: {
                        if (0 != (MainActivity.getKeySet())) {
                            final int command = 0;
                            final String param = "EXIT";
                            try {
                                if (MainActivity.service.getHbbTvControl()
                                        .notifyAppMngr(command, param)) {
                                    MainActivity.webDialog.getHbbTVView()
                                            .setAlpha((float) 0.00);
                                    MainActivity.setKeySet(0);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        };
    }
}
