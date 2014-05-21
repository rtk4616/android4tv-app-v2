package com.iwedia.service.action;

import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.iwedia.comm.IActionCallback;
import com.iwedia.comm.IActionControl;

public class ActionControl extends IActionControl.Stub {
    final static RemoteCallbackList<IActionCallback> mActionsCallback = new RemoteCallbackList<IActionCallback>();

    @Override
    public void registerCallback(IActionCallback actionCallback)
            throws RemoteException {
        mActionsCallback.register(actionCallback);
    }

    @Override
    public void unregisterCallback(IActionCallback actionCallback)
            throws RemoteException {
    }

    @Override
    public void onControllerStateChanged(Bundle bundle) throws RemoteException {
    }
}
