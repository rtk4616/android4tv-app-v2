package com.iwedia.service.service;

import android.content.SharedPreferences.Editor;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IServiceControl;
import com.iwedia.comm.IServiceListCallback;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.dtvmanager.DTVManager;
import com.iwedia.dtv.route.broadcast.RouteFrontendType;
import com.iwedia.dtv.service.Service;
import com.iwedia.dtv.service.ServiceDescriptor;
import com.iwedia.dtv.service.ServiceListUpdateData;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.content.ContentListControl;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.system.SystemControl;

/**
 * @author Marko Zivanovic This class represent interface to MW service lists;
 */
public class ServiceControl extends IServiceControl.Stub implements
        IDTVInterface {
    private static final String LOG_TAG = "ServiceList";
    /** Index of the current service list */
    protected int mIndex = 0;
    public static IServiceListCallback serviceListCallback;
    /**
     * Instance of CotnentListControl class {@link ContentListControl}.
     */
    private static ContentListControl contentListControl;

    public ServiceControl(String name, int index) {
        try {
            contentListControl = (ContentListControl) IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        int currentRouteId = IWEDIAService.getInstance().getDtvManagerProxy()
                .getCurrentLiveRoute();
        RouteFrontendType feType = IWEDIAService.getInstance()
                .getDtvManagerProxy().getLiveRouteFEType(currentRouteId);
        int serviceListType = 0;
        if (feType == RouteFrontendType.TER) {
            serviceListType = ServiceListIndex.TERRESTRIAL;
        } else if (feType == RouteFrontendType.CAB) {
            serviceListType = ServiceListIndex.CABLE;
        } else if (feType == RouteFrontendType.SAT) {
            serviceListType = ServiceListIndex.SATELLITE;
        }
        this.mIndex = IWEDIAService.getInstance().getPreferenceManager()
                .getInt("lastServiceListIndex", serviceListType);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "currentRouteId:" + currentRouteId);
            Log.e(LOG_TAG, "mIndex:" + mIndex);
        }
    }

    @Override
    public boolean stopService() throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getServiceControl()
                    .stopService(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getRouteManager().getCurrentLiveRoute());
        } catch (InternalException e) {
            e.printStackTrace();
        }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "stopActiveService");
        }
        return true;
    }

    @Override
    public Service getActiveService() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getActiveService");
        }
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getServiceControl()
                .getActiveService(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public ServiceDescriptor getServiceDescriptor(int serviceListIndex,
            int serviceIndex) throws RemoteException {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "getService:" + serviceIndex + "in service list:"
                    + serviceListIndex);
        return IWEDIAService.getInstance().getDTVManager().getServiceControl()
                .getServiceDescriptor(serviceListIndex, serviceIndex);
    }

    public void storeLastPlayedListAndService(Service service)
            throws RemoteException {
        Log.e(LOG_TAG, "storeLastPlayedListAndService (" + service + ")");
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getServiceControl()
                .storeLastPlayedListAndService(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), service);
    }

    public Service getLastPlayedListAndService() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.d(LOG_TAG, "getLastPlayedListAndService");
        }
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getServiceControl()
                .getLastPlayedListAndService(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Switch to service with given service list index and serviceIndex.
     * 
     * @param serviceListIndex
     *        index of MW service list.
     * @param serviceIndex
     *        index of service in service list.
     */
    @Override
    public boolean goServiceIndexFromServiceList(int serviceListIndex,
            int serviceIndex, int routeId) throws RemoteException {
        boolean state = true;
        Service service = new Service(serviceListIndex, serviceIndex);
        try {
            (IWEDIAService.getInstance().getDtvManagerProxy()
                    .getTeletextControl()).deselectCurrentTeletextTrack();
            (IWEDIAService.getInstance().getDtvManagerProxy().getMhegControl())
                    .hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isMainRoute = IWEDIAService.getInstance().getDtvManagerProxy()
                .isMainRoute(routeId);
        if (IWEDIAService.DEBUG)
            Log.v(LOG_TAG, "go service index " + serviceIndex + " route id "
                    + routeId + " in service list " + serviceListIndex
                    + " is main route " + isMainRoute);
        if (isMainRoute) {
            setServiceListIndex(serviceListIndex);
        }
        switch (serviceListIndex) {
            case ServiceListIndex.IP_STREAMED:
                if (isMainRoute) {
                    IWEDIAService
                            .getInstance()
                            .getDtvManagerProxy()
                            .setCurrentRecRoute(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getRecRouteIDIP());
                }
                break;
            case ServiceListIndex.TERRESTRIAL:
                IWEDIAService.getInstance().getDtvManagerProxy()
                        .setCurrentLiveRoute(routeId);
                if (isMainRoute) {
                    IWEDIAService
                            .getInstance()
                            .getDtvManagerProxy()
                            .setCurrentRecRoute(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getRecRouteIDTer());
                }
                break;
            case ServiceListIndex.CABLE:
                IWEDIAService.getInstance().getDtvManagerProxy()
                        .setCurrentLiveRoute(routeId);
                if (isMainRoute) {
                    IWEDIAService
                            .getInstance()
                            .getDtvManagerProxy()
                            .setCurrentRecRoute(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getRecRouteIDCab());
                }
                break;
            case ServiceListIndex.ANALOG:
                IWEDIAService.getInstance().getDtvManagerProxy()
                        .setCurrentLiveRoute(routeId);
                if (isMainRoute) {
                    IWEDIAService
                            .getInstance()
                            .getDtvManagerProxy()
                            .setCurrentRecRoute(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getRecRouteIDAtv());
                }
                break;
            case ServiceListIndex.SATELLITE:
                IWEDIAService.getInstance().getDtvManagerProxy()
                        .setCurrentLiveRoute(routeId);
                if (isMainRoute) {
                    IWEDIAService
                            .getInstance()
                            .getDtvManagerProxy()
                            .setCurrentRecRoute(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getRecRouteIDSat());
                }
                break;
            default: // TODO: in case of favorites and other
                IWEDIAService.getInstance().getDtvManagerProxy()
                        .setCurrentLiveRoute(routeId);
        }
        ContentListControl contentListControl = (ContentListControl) IWEDIAService
                .getInstance().getDtvManagerProxy().getContentListControl();
        boolean isIpUrl = contentListControl.isGuiVideoViewIPUri();
        if (isIpUrl) {
            SystemControl.getActionCallback().startUrl("mrvl://");
            contentListControl.setGuiVideoViewIPUri(false);
        }
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG,
                    "*********************** STARTING SERVICE WITH INDEX IN MASTER LIST:"
                            + serviceIndex + " ON ROUTEID:" + routeId);
        try {
            Editor edit = IWEDIAService.getInstance().getPreferenceManager()
                    .edit();
            edit.putBoolean("ip_last_watched", false);
            edit.commit();
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .startService(routeId, serviceListIndex, serviceIndex);
        } catch (InternalException e) {
            state = false;
        }
        if (state) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "channel go-to OK");
            }
            if (isMainRoute) {
                storeLastPlayedListAndService(service);
            }
            return true;
        }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "channel go-to failed, state: " + state);
        }
        return false;
    }

    @Override
    public int getServiceListCount(int listIndex) throws RemoteException {
        int size = IWEDIAService.getInstance().getDTVManager()
                .getServiceControl().getServiceListCount(listIndex);
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "number of services:" + size + " in service list:"
                    + listIndex);
        return size;
    }

    public void setServiceListIndex(int index) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "setServiceListIndex:" + index);
        }
        this.mIndex = index;
    }

    @Override
    public int getServiceIndexByLCN(int serviceListIndex, int serviceLCN)
            throws RemoteException {
        Log.d(LOG_TAG, "PROXY: getServiceIndexByLCN() - list index: "
                + serviceListIndex + ", LCN: " + serviceLCN);
        return IWEDIAService.getInstance().getDTVManager().getServiceControl()
                .getServiceIndexByLCN(serviceListIndex, serviceLCN);
    }

    @Override
    public boolean addServiceInServiceList(int listIndex, int serviceIndex)
            throws RemoteException {
        Log.d(LOG_TAG, "addServiceInServiceList: index [" + listIndex + "],["
                + serviceIndex + "]");
        try {
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .addServiceInServiceList(listIndex, serviceIndex);
            return true;
        } catch (InternalException e) {
            return false;
        }
    }

    @Override
    public boolean createServiceList(String name) throws RemoteException {
        Log.d(LOG_TAG, "createServiceList: " + name);
        try {
            if (IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .createServiceList(name) > 0) {
                ContentListControl contentListControl = (ContentListControl) IWEDIAService
                        .getInstance().getDtvManagerProxy()
                        .getContentListControl();
                contentListControl.refreshServiceLists();
                return true;
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteServiceList(int index) throws RemoteException {
        Log.d(LOG_TAG, "deleteServiceList: " + index);
        boolean ret = true;
        try {
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .deleteServiceList(index);
        } catch (InternalException e) {
            ret = false;
        }
        ContentListControl contentListControl = (ContentListControl) IWEDIAService
                .getInstance().getDtvManagerProxy().getContentListControl();
        /**
         * Refresh service filters after deleting one list all must be
         * recalculated
         */
        contentListControl.refreshServiceLists();
        return ret;
    }

    @Override
    public boolean deleteServiceFromServiceList(int listIndex, int serviceIndex)
            throws RemoteException {
        Log.d(LOG_TAG, "deleteServiceFromServiceList: index [" + listIndex
                + "],[" + serviceIndex + "]");
        try {
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .deleteServiceFromServiceList(listIndex, serviceIndex);
            return true;
        } catch (InternalException e) {
            return false;
        }
    }

    @Override
    public void movePointedService(int listIndex, int pointedServiceIndex,
            int movedServiceIndex) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getServiceControl()
                .movePointedService(listIndex, pointedServiceIndex,
                        movedServiceIndex - 1);
    }

    @Override
    public int getNumberOfServiceLists() throws RemoteException {
        Log.d(LOG_TAG, "getNumberOfServiceLists");
        return (int) IWEDIAService.getInstance().getDTVManager()
                .getServiceControl().getNumberOfServiceLists();
    }

    @Override
    public void registerCallback(IServiceListCallback eventCallback)
            throws RemoteException {
        serviceListCallback = eventCallback;
    }

    @Override
    public boolean setServiceURL(String url) throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getServiceControl()
                    .zapURL(IWEDIAService.getInstance().getDtvManagerProxy()
                            .getCurrentLiveRoute(), url);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (InternalException e) {
            return false;
        }
    }

    @Override
    public boolean renameService(int listIndex, int serviceIndex,
            String newServiceName) throws RemoteException {
        try {
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .renameService(listIndex, serviceIndex, newServiceName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean renameList(int listIndex, String newListName)
            throws RemoteException {
        try {
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .renameList(listIndex, newListName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getServiceListName(int listIndex) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceControl()
                .getServiceListName(listIndex);
    }

    @Override
    public boolean togglePreviousService() throws RemoteException {
        return false;
    }

    @Override
    public void unregisterCallback(IServiceListCallback arg0)
            throws RemoteException {
        // TODO Auto-generated method stub
    }

    public static com.iwedia.dtv.service.IServiceCallback getServiceListCallback() {
        return serviceCallback;
    }

    private static com.iwedia.dtv.service.IServiceCallback serviceCallback = new com.iwedia.dtv.service.IServiceCallback() {
        @Override
        public void channelChangeStatus(int liveRoute, boolean channelChanged) {
            try {
                if (contentListControl.getActiveContent(0) != null) {
                    contentListControl.addContentToRecentlyWatchedList();
                }
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                serviceListCallback.channelChangeStatus(liveRoute,
                        channelChanged);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void safeToUnblank(int liveRoute) {
            try {
                serviceListCallback.safeToUnblank(liveRoute);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void serviceScrambledStatus(int liveRoute,
                boolean channelScrambled) {
            try {
                serviceListCallback.serviceScrambledStatus(liveRoute,
                        channelScrambled);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void serviceStopped(int liveRoute, boolean serviceStopped) {
            try {
                serviceListCallback.serviceStopped(liveRoute, serviceStopped);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void signalStatus(int liveRoute, boolean channelScrambled) {
            try {
                serviceListCallback.signalStatus(liveRoute, channelScrambled);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void updateServiceList(ServiceListUpdateData arg1) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}