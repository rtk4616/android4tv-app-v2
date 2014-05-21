package com.iwedia.service.mheg;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IMhegCallback;
import com.iwedia.comm.IMhegControl;
import com.iwedia.comm.enums.MhegMode;
import com.iwedia.dtv.types.UserControl;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.system.SystemControl;

public class MhegControl extends IMhegControl.Stub implements IDTVInterface {
    private static final String LOG_TAG = "MhegControl";
    final static RemoteCallbackList<IMhegCallback> mMhegCallbackManager = new RemoteCallbackList<IMhegCallback>();

    public MhegControl() {
    }

    private int mhegMode = MhegMode.MODE_OFF;

    @Override
    public boolean show() throws RemoteException {
        mhegMode = MhegMode.MODE_ON;
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getMhegControl()
                .show(IWEDIAService.getInstance().getDtvManagerProxy()
                        .getCurrentLiveRoute(), 0);
        SystemControl.broadcastMhegShow();
        return true;
    }

    @Override
    public boolean hide() throws RemoteException {
        if (mhegMode != MhegMode.MODE_OFF) {
            mhegMode = MhegMode.MODE_OFF;
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getMhegControl()
                    .hide(IWEDIAService.getInstance().getDtvManagerProxy()
                            .getCurrentLiveRoute());
            SystemControl.broadcastMhegHide();
        }
        return true;
    }

    @Override
    public boolean sendInputControl(int keyCode, UserControl ctrl)
            throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getMhegControl()
                .sendInputControl(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), ctrl, keyCode);
        return false;
    }

    private static Object obj1 = new Object();

    public static void sMhegKeyMaskEvent(int keyMask) {
        Log.e(LOG_TAG, "sMhegKeyMaskEvent:" + keyMask);
        synchronized (obj1) {
            int i = mMhegCallbackManager.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mMhegCallbackManager.getBroadcastItem(i).mhegKeyMaskEvent(
                            keyMask);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mMhegCallbackManager.unregister(mMhegCallbackManager
                            .getBroadcastItem(i));
                }
            }
            mMhegCallbackManager.finishBroadcast();
        }
    }

    @Override
    public void registerCallback(IMhegCallback mhegCallback)
            throws RemoteException {
        mMhegCallbackManager.register(mhegCallback);
    }

    @Override
    public int getState() throws RemoteException {
        return mhegMode;
    }

    @Override
    public boolean isPresent() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getMhegControl()
                .isPresent(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}
