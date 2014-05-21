package com.iwedia.gui.callbacks;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

import com.iwedia.comm.IDTVManagerProxy;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.listeners.MainKeyListener;

/**
 * Handler For All CallBacks.
 */
public class CallBackHandler {
    private static String TAG = "CallBackHandler";
    /** Messages */
    private static final int MESSAGE_SHOW_TOAST = 0;
    private static final int UPDATE_CHANNEL_INFO = 1;
    /** Instance of MainActivity */
    private MainActivity mActivity = null;
    /** UI Handler */
    private Handler mHandler = null;
    /** CallBacks. */
    private PVRCallBack mPVRCallBack = null;
    private ServiceListCallBack mServiceListCallBack = null;
    private DLNACallBack mDLNACallBack = null;
    private ChannelsCallBack mChannelsCallBack = null;
    private ScanCallback mScanCallback = null;
    private ActionCallBack mActionCallBack = null;

    public CallBackHandler(MainActivity activity) {
        mActivity = activity;
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_SHOW_TOAST: {
                        Toast.makeText(mActivity, (Integer) msg.obj,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case UPDATE_CHANNEL_INFO: {
                        if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                            mActivity.getPageCurl().updateChannelInfo(0);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        initializeCallBacks();
    }

    private void initializeCallBacks() {
        mPVRCallBack = new PVRCallBack(this, mActivity);
        mServiceListCallBack = new ServiceListCallBack(this, mActivity);
        mDLNACallBack = new DLNACallBack(this, mActivity);
        mChannelsCallBack = new ChannelsCallBack(this, mActivity);
        mScanCallback = new ScanCallback(mActivity);
        mActionCallBack = new ActionCallBack(mActivity);
    }

    public void registerCallBacks(IDTVManagerProxy dtvManager)
            throws RemoteException {
        dtvManager.getPvrControl().registerCallback(mPVRCallBack);
        dtvManager.getServiceControl().registerCallback(mServiceListCallBack);
        dtvManager.getDlnaControl().registerCallback(mDLNACallBack);
        dtvManager.getContentListControl().registerCallback(mChannelsCallBack);
        dtvManager.getScanControl().registerCallback(mScanCallback);
        dtvManager.getSystemControl().registerActionCallback(mActionCallBack);
    }

    public void unRegisterCallBacks(IDTVManagerProxy dtvManager)
            throws RemoteException {
        dtvManager.getPvrControl().unregisterCallback(mPVRCallBack);
        dtvManager.getServiceControl().unregisterCallback(mServiceListCallBack);
        dtvManager.getDlnaControl().unregisterCallback(mDLNACallBack);
        dtvManager.getScanControl().unregisterCallback(mScanCallback);
    }

    /**
     * Show Toast Message.
     * 
     * @param resStringId
     *        - Resource ID of String.
     */
    public void showToastMessage(int resStringId) {
        Message.obtain(mHandler, MESSAGE_SHOW_TOAST, resStringId)
                .sendToTarget();
    }

    /**
     * Get String from Resource.
     * 
     * @param resStringId
     *        - Resource ID of String.
     * @return String.
     */
    public String getStringFromRes(int resStringId) {
        return mActivity.getApplicationContext().getString(resStringId);
    }

    /**
     * Update ChannelInfo
     */
    public void updateChannelInfo() {
        Message.obtain(mHandler, UPDATE_CHANNEL_INFO).sendToTarget();
    }

    public boolean isAntennaConnected() {
        return mServiceListCallBack.isAntennaConected();
    }

    public void setAntenaConnected(boolean antennaConnected) {
        mServiceListCallBack.setAntennaConected(antennaConnected);
    }
}
