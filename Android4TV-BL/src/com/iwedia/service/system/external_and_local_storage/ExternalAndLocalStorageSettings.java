package com.iwedia.service.system.external_and_local_storage;

import android.os.RemoteException;

import com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings;

public class ExternalAndLocalStorageSettings extends
        IExternalLocalStorageSettings.Stub {
    @Override
    public String getExternalStorageTotalSpace() throws RemoteException {
        return ExternalAndLocalStorageManager.getInstance()
                .getSpace(true, true);
    }

    @Override
    public String getExternalStorageAvailableSpace() throws RemoteException {
        return ExternalAndLocalStorageManager.getInstance().getSpace(false,
                true);
    }

    @Override
    public String getLocalStorageTotalSpace() throws RemoteException {
        return ExternalAndLocalStorageManager.getInstance().getSpace(true,
                false);
    }

    @Override
    public String getLocalStorageAvailableSpace() throws RemoteException {
        return ExternalAndLocalStorageManager.getInstance().getSpace(false,
                false);
    }

    @Override
    public void unmount() throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public void format() throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isExternalMemoryFull() throws RemoteException {
        return ExternalAndLocalStorageManager.getInstance()
                .isExternalMemoryFull();
    }

    @Override
    public String getExternalStoragePath() throws RemoteException {
        return ExternalAndLocalStorageManager.getInstance().getUsbPath();
    }
}