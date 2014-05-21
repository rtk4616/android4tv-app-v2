package com.iwedia.service.system.application;

import java.util.List;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.system.application.AppPermission;
import com.iwedia.comm.system.application.IApplicationDetails;
import com.iwedia.service.IWEDIAService;

/**
 * This class manages application details and settings.
 * 
 * @author stanislava
 */
public class ApplicationDetails extends IApplicationDetails.Stub {
    private static final String LOG_TAG = "ApplicationDetails";
    String packageName = "";

    public ApplicationDetails() {
    }

    /**
     * Delete application cache files.
     */
    @Override
    public void clearCache() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, " clearCache()");
        }
        ApplicationManager.getInstance(packageName)
                .deleteApplicationCacheFiles();
    }

    /**
     * Clear application's default actions.
     * 
     * @return true if everything is OK, else false.
     */
    @Override
    public boolean clearDefaults() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "clearDefaults()");
        }
        ApplicationManager.getInstance(packageName).clearDefaults();
        return false;
    }

    /**
     * Force stop an application with the given package name (packageName).
     * 
     * @return true if everything is OK, else false
     */
    @Override
    public boolean forceStop() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "forceStop()");
        }
        return ApplicationManager.getInstance(packageName).forceStop();
    }

    /**
     * Delete application user data - all files, accounts, databases etc.
     */
    @Override
    public void clearData() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "clearData()");
        }
        ApplicationManager.getInstance(packageName).clearApplicationUserData();
    }

    @Override
    public boolean move() throws RemoteException {
        // TODO Auto-generated method stub
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "move()");
        }
        return false;
    }

    /**
     * Gets application size information - application (code) size, data size,
     * cache size, external data size etc, external cache size etc.
     */
    @Override
    public void getAppSizeInfo() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getAppSizeInfo()");
        }
        ApplicationManager.getInstance(packageName).getAppSizeInfo();
    }

    /**
     * Enable or disable built-in applications. If application is enabled, this
     * function disables application with the given package (packageName), else
     * enables.
     * 
     * @return true if everything is OK, else false.
     */
    @Override
    public boolean enable() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "enable()");
        }
        return ApplicationManager.getInstance(packageName).enable();
    }

    /**
     * Uninstall the downloaded application with the given package name.
     * 
     * @return true if everything is OK, else false.
     */
    @Override
    public boolean uninstall() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "uninstall()");
        }
        return ApplicationManager.getInstance(packageName).uninstall();
    }

    /**
     * Check if is application with the given package name (packageName)
     * built-in or downloaded.
     * 
     * @return true if an application is built-in, else false.
     */
    @Override
    public boolean isSystem() throws RemoteException {
        return ApplicationManager.getInstance(packageName).isSystemPackage();
    }

    /**
     * Check if is built-in application with the given package name
     * (packageName) enabled or disabled.
     * 
     * @return true if application is enabled, else false.
     */
    @Override
    public boolean isEnabled() throws RemoteException {
        return ApplicationManager.getInstance(packageName).isEnabled();
    }

    /**
     * Check if the application with the given package name (packageName) is
     * stopped.
     * 
     * @return true if application is stopped, else false.
     */
    @Override
    public boolean isStopped() throws RemoteException {
        return ApplicationManager.getInstance(packageName).isStopped();
    }

    /**
     * Gets list of application permissions.
     * 
     * @return list of application permissions.
     */
    @Override
    public List<AppPermission> getAppPermissions() throws RemoteException {
        return ApplicationManager.getInstance(packageName).getAppPermissions();
    }

    /**
     * Check if is application with the given package name (packageName) set to
     * open by default for some actions.
     * 
     * @return true if is application with the given package name (packageName)
     *         default application, otherwise false.
     */
    @Override
    public boolean isDefault() throws RemoteException {
        return ApplicationManager.getInstance(packageName).isDefault();
    }

    /**
     * Sets package name.
     * 
     * @param packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
