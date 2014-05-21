package com.iwedia.service.system.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.system.about.IAbout;
import com.iwedia.comm.system.about.ISoftwareUpdate;
import com.iwedia.dtv.swupdate.SWVersionType;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.MediaMounted;

/**
 * The device info. Provides basic information about the device and the software
 * on it.
 * 
 * @author Stanislava Markovic
 */
@SuppressLint("NewApi")
public class About extends IAbout.Stub {
    private static final String LOG_TAG = "About";
    ISoftwareUpdate softwareUpdate;
    AboutManager aboutManager;
    MediaMounted mediaMonted;

    public About() {
        softwareUpdate = new SoftwareUpdate();
        aboutManager = new AboutManager();
        mediaMonted = new MediaMounted();
    }

    /**
     * Returns the softer update controller.
     * 
     * @return {@link com.iwedia.comm.system.about.ISoftwareUpdate}
     */
    @Override
    public ISoftwareUpdate getSoftwareUpdate() throws RemoteException {
        return softwareUpdate;
    }

    /**
     * Gets Android version.
     * 
     * @return Android version.
     */
    @Override
    public String getAndroidVersion() throws RemoteException {
        return aboutManager.getAndroidVersion();
    }

    /**
     * Gets kernel version.
     * 
     * @return kernel version.
     */
    @Override
    public String getBuildNumber() throws RemoteException {
        return aboutManager.getBuildNumber();
    }

    /**
     * Gets local IP address.
     * 
     * @return local IP address.
     */
    @Override
    public String getIPAddress() throws RemoteException {
        return aboutManager.getIPAddress();
    }

    /**
     * Gets kernel version.
     * 
     * @return kernel version.
     */
    @Override
    public String getKernelVersion() throws RemoteException {
        return aboutManager.getKernelVersion();
    }

    /**
     * Gets MAC address.
     * 
     * @return MAC address.
     */
    @Override
    public String getMacAddress() throws RemoteException {
        return aboutManager.getMacAddress();
    }

    /**
     * Gets model number.
     * 
     * @return model number.
     */
    @Override
    public String getModelNumber() throws RemoteException {
        return aboutManager.getModelNumber();
    }

    /**
     * Gets software version.
     * 
     * @return software version.
     */
    @Override
    public String getSWVersion(SWVersionType swVersionType)
            throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getSoftwareUpdateControl().getSWVersion(swVersionType);
    }

    /**
     * Erases all data on device and sets the factory default values.
     * 
     * @return true if data is successfully erased, otherwise false.
     */
    @Override
    public boolean factoryReset() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .factoryReset();
        Editor editor = IWEDIAService.getInstance().getPreferenceManager()
                .edit();
        editor.clear();
        editor.commit();
        IWEDIAService.getInstance().getStorageManager().deleteDatabase();
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "reboot-start");
        }
        final PowerManager power = (PowerManager) IWEDIAService.getInstance()
                .getSystemService(Context.POWER_SERVICE);
        power.reboot("fav");
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "reboot-end");
        }
        return false;
    }
}