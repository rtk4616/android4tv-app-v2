package com.iwedia.gui.callbacks;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IServiceListCallback;
import com.iwedia.comm.content.Content;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.osd.CheckServiceType;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.pvr.PVRHandler;

public class ServiceListCallBack extends IServiceListCallback.Stub {
    private static final String TAG = "ServiceListCallBack";
    /** Instance of MainActivity */
    private MainActivity mActivity = null;
    /** Instance of CallBack Handler. */
    private CallBackHandler mCallBackHandler = null;
    /** Fields */
    private boolean isAntennaConected = true;
    private boolean isZapped = false;
    private boolean zappOnRunApplication = false;

    public ServiceListCallBack(CallBackHandler callBackHandler,
            MainActivity activity) {
        mCallBackHandler = callBackHandler;
        mActivity = activity;
    }

    @Override
    public void channelChangeStatus(long arg0, boolean success)
            throws RemoteException {
        Log.e(TAG, "channelChangeStatus: success:" + success);
        // Check Service
        Content lContent = null;
        try {
            lContent = MainActivity.service.getContentListControl()
                    .getActiveContent(0);
            CheckServiceType.checkService(lContent, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lContent != null) {
            int activeFilterType = lContent.getFilterType();
            /*
             * if (((activeFilterType == FilterType.ANALOG) || (activeFilterType
             * == FilterType.RADIO) || (activeFilterType == FilterType.DATA))) {
             */
            isZapped = success;
            Log.i(TAG, "Zapp CallBack: " + success);
            OSDHandlerHelper.channelIsZapped(success);
            // if zap is success hide layout for antenna
            if (success) {
                mActivity.getCheckServiceType().hideNoSignalLayout();
            }
            /** If Subtitle was On, reSet */
            if (success) {
                PVRHandler.setPreviousSubtitleState();
            }
            if (!success) {
                // Blank Screen
                try {
                    MainActivity.service.getVideoControl().videoBlank(0, true);
                } catch (Exception e) {
                    Log.e(TAG, "Blank Screen Exception", e);
                }
            }
            Log.i(TAG, "Pre No CAM message! ");
            if (CheckServiceType.isScrambled == true
                    && MainActivity.getCiCallbackController().getTotalCam() == 0) {
                Log.i(TAG, "No CAM message! ");
                mCallBackHandler.showToastMessage(R.string.no_cam_present);
            }
            if (success && zappOnRunApplication) {
                mCallBackHandler.updateChannelInfo();
            }
            zappOnRunApplication = true;
            isAntennaConected = success;
            Log.d(TAG, "ANTENNA IS CONNECTED IN ZAP CALLBACK " + success);
            if (success) {
                mActivity.getCheckServiceType().hideNoSignalLayout();
            } else {
                mActivity.getCheckServiceType().showNoSignalLayout();
            }
        }
    }

    @Override
    public void safeToUnblank(long arg0) throws RemoteException {
        isZapped = true;
        OSDHandlerHelper.channelIsZapped(true);
        // if zap is success hide layout for antenna
        mActivity.getCheckServiceType().hideNoSignalLayout();
        /** If Subtitle was On, reSet */
        PVRHandler.setPreviousSubtitleState();
        Log.i(TAG, "Pre No CAM message! ");
        if (CheckServiceType.isScrambled == true
                && MainActivity.getCiCallbackController().getTotalCam() == 0) {
            Log.i(TAG, "No CAM message! ");
            mCallBackHandler.showToastMessage(R.string.no_cam_present);
        }
        if (zappOnRunApplication) {
            mCallBackHandler.updateChannelInfo();
        }
        zappOnRunApplication = true;
        isAntennaConected = true;
    }

    @Override
    public void serviceScrambledStatus(long arg0, boolean arg1)
            throws RemoteException {
    }

    @Override
    public void serviceStopped(long arg0, boolean arg1) throws RemoteException {
    }

    @Override
    public void signalStatus(long arg0, boolean arg1) throws RemoteException {
    }

    @Override
    public void updateServiceList() throws RemoteException {
    }

    public boolean isAntennaConected() {
        return isAntennaConected;
    }

    public void setAntennaConected(boolean antennaConnected) {
        isAntennaConected = antennaConnected;
    }
}
