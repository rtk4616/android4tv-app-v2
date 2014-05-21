package com.iwedia.service.callback;

import android.os.IBinder;
import android.os.RemoteException;

import com.iwedia.comm.ICallbacksControl;

public class CallbacksControl extends ICallbacksControl.Stub {
    @Override
    public void setEventsCallback(IBinder binder) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setChannelsCallback(IBinder binder) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setCICallback(IBinder binder) throws RemoteException {
        // TODO Auto-generated method stub
    }
}
