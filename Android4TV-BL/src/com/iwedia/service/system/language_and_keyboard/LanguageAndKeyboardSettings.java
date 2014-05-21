package com.iwedia.service.system.language_and_keyboard;

import java.util.List;

import android.os.RemoteException;

import com.iwedia.comm.system.language_and_keyboard.ILanguageKeyboardSettings;

public class LanguageAndKeyboardSettings extends ILanguageKeyboardSettings.Stub {
    @Override
    public List<String> getAvailableLanguages() throws RemoteException {
        return LanguageManager.getInstance().getAvailableLanguages();
    }

    @Override
    public void setActiveLanguage(String language) throws RemoteException {
        LanguageManager.getInstance().changeLanguage(language);
    }

    @Override
    public int getActiveLanguageIndex() throws RemoteException {
        return LanguageManager.getInstance().getActiveLanguage();
    }

    @Override
    public List<String> getAvailableCountries() throws RemoteException {
        return null;
    }

    @Override
    public void setActiveContry(String country) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public String getActiveCountry() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getAvailableKeyboardTypes() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getActiveKeyboardType() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setActiveKeyboardType(String keyboardType)
            throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean systemStandby() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setFontScale(float scale) throws RemoteException {
        LanguageManager.getInstance().setFontScale(scale);
    }

    @Override
    public float getActiveFontScale() throws RemoteException {
        return LanguageManager.getInstance().getActiveFontScale();
    }

    @Override
    public String convertTrigramsToLanguage(String language, boolean isLanguage)
            throws RemoteException {
        return LanguageManager.getInstance().convertTrigramsToLanguage(
                language, isLanguage);
    }
}
