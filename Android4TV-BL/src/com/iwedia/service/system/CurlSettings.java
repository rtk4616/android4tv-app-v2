package com.iwedia.service.system;

import android.content.SharedPreferences.Editor;
import android.os.RemoteException;

import com.iwedia.comm.system.ICurlSettings;
import com.iwedia.service.IWEDIAService;

/**
 * GUI application Curl effect settings.
 * 
 * @author Marko Zivanovic
 */
public class CurlSettings extends ICurlSettings.Stub {
    private float timeoutValue;
    private boolean isEnabled;

    /**
     * Default constructor.
     */
    public CurlSettings() {
        timeoutValue = IWEDIAService.getInstance().getPreferenceManager()
                .getFloat("curl_timeout_value", 3);
        isEnabled = IWEDIAService.getInstance().getPreferenceManager()
                .getBoolean("curl_state", true);
    }

    /** Gets GUI application CURL effect timeout value. */
    @Override
    public float getTimeout() throws RemoteException {
        return timeoutValue;
    }

    /** Sets GUI application CURL effect timeout value. */
    @Override
    public void setTimeout(float value) throws RemoteException {
        timeoutValue = value;
        Editor edit = IWEDIAService.getInstance().getPreferenceManager().edit();
        edit.putFloat("curl_timeout_value", timeoutValue);
        edit.commit();
    }

    /** Returns state of CURL effect. True if CURL is enabled, otherwise false. */
    @Override
    public boolean isEnabled() throws RemoteException {
        return isEnabled;
    }

    /** Sets state of CURL effect. True to enable CURL effect, otherwise false. */
    @Override
    public void setEnabled(boolean state) throws RemoteException {
        isEnabled = state;
        Editor edit = IWEDIAService.getInstance().getPreferenceManager().edit();
        edit.putBoolean("curl_state", isEnabled);
        edit.commit();
    }
}
