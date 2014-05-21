package com.iwedia.service.system.application;

import java.util.List;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.applications.AppItem;
import com.iwedia.comm.enums.AppListType;
import com.iwedia.comm.system.application.IApplicationDetails;
import com.iwedia.comm.system.application.IApplicationSettings;
import com.iwedia.service.IWEDIAService;

/**
 * The applications controller. This class manages applications, their details
 * and settings.
 * 
 * @author Stanislava Markovic
 */
public class ApplicationSettings extends IApplicationSettings.Stub {
    private static final String LOG_TAG = "ApplicationSettings";
    private IApplicationDetails applicationDetails;

    public ApplicationSettings() {
        applicationDetails = new ApplicationDetails();
    }

    /**
     * Gets application details with the given package name
     * 
     * @param packageName
     *        - package name of application you want to get details
     * @return {@link com.iwedia.comm.system.application.IApplicationDetails}
     */
    @Override
    public IApplicationDetails getApplicationDeatails(String packageName)
            throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getApplicationDeatails " + packageName);
        }
        ((ApplicationDetails) applicationDetails).setPackageName(packageName);
        return applicationDetails;
    }

    /**
     * Returns the number of applications with the given application type.
     * 
     * @param appType
     *        - it can be {@link AppListType#ALL} or
     *        {@link AppListType#INSTALLED} or {@link AppListType#RUNNING} or
     *        {@link AppListType#EXTERNAL}
     * @return number of applications.
     */
    @Override
    public int getAppListSize(int appType) throws RemoteException {
        return ApplicationManager.getInstance().getSize(appType);
    }

    /**
     * Gets application with the given index.
     * 
     * @param index
     *        - index of the application you want to get.
     * @return {@link com.iwedia.comm.content.applications.AppItem}
     */
    @Override
    public AppItem getApplication(int index) throws RemoteException {
        return ApplicationManager.getInstance().getApplication(index);
    }

    /**
     * Check if is allowed installation of non-Market applications.
     * 
     * @return true if allowed, else false.
     */
    @Override
    public boolean isUnknownSource() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Allow or disallow installation of non-Market applications.
     * 
     * @param value
     *        - true if you want to allow, else false.
     */
    @Override
    public void setUnknownSource(boolean arg0) throws RemoteException {
        // TODO Auto-generated method stub
    }

    /**
     * Gets list of running services.
     * 
     * @return list of runningServices.
     */
    @Override
    public List<AppItem> getRunningServices() throws RemoteException {
        return ApplicationManager.getInstance().getRunningServices();
    }

    @Override
    public void stopService(String packegeName, String className)
            throws RemoteException {
        ApplicationManager.getInstance().stopService(packegeName, className);
    }
}
