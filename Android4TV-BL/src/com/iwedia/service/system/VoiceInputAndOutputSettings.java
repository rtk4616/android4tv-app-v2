package com.iwedia.service.system;

import android.os.RemoteException;

import com.iwedia.comm.system.IVoiceInputOutputSettings;

public class VoiceInputAndOutputSettings extends IVoiceInputOutputSettings.Stub {
    @Override
    public boolean isCustomSettings() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCustomSettings(boolean value) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public int getNumberOfAvailableLanguages() throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getAvailableLanguage(int index) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setActiveLanguage(int index) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public String getActiveLanguage() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
}