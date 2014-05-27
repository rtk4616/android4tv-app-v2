package com.iwedia.service.scan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

import android.content.SharedPreferences.Editor;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IScanCallback;
import com.iwedia.comm.IScanControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.PlaybackDestinationType;
import com.iwedia.comm.enums.ScanSignalType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.route.broadcast.RouteFrontendType;
import com.iwedia.dtv.route.broadcast.RouteInstallSettings;
import com.iwedia.dtv.scan.BandType;
import com.iwedia.dtv.scan.FecType;
import com.iwedia.dtv.scan.Modulation;
import com.iwedia.dtv.scan.Polarization;
import com.iwedia.dtv.scan.ScanInstallStatus;
import com.iwedia.dtv.scan.SignalInfo;
import com.iwedia.dtv.scan.TunerType;
import com.iwedia.dtv.service.Service;
import com.iwedia.dtv.service.ServiceDescriptor;
import com.iwedia.dtv.service.ServiceListFilter;
import com.iwedia.dtv.service.ServiceListFilterSet;
import com.iwedia.dtv.service.ServiceType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.dtv.types.AnalogEncodingMode;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.content.ContentListControl;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.storage.ControllerType;

public class ScanControl extends IScanControl.Stub implements IDTVInterface {
    private static final String LOG_TAG = "ScanControl";
    @SuppressWarnings("unused")
    private static TunerType tunerType;
    public static int numberOfInstalledRadioServices = 0;
    public static int numberOfInstalledTVServices = 0;
    public static IScanCallback scanCallback;
    private static int scanSignalType;
    private static boolean isAutoScan = false;
    private static boolean isScanStarted = false;
    private static boolean updateList = false;
    private static int installRouteId = -1;
    private static int tmpLiveRoute = -1;
    private static boolean keepChannelList = false;
    /**
     * Instance of CotnentListControl class {@link ContentListControl}.
     */
    private static ContentListControl contentListControl;
    /**
     * Applies on main display only
     */
    private static final int mDisplayId = PlaybackDestinationType.MAIN_LIVE;
    public static int isScanFinished = 0;

    /**
     * Initiate scanning for all services on a complete frequency range.
     * 
     * @param updateList
     *        Flag that specifies should existing service list be updated with
     *        new services, or reset before the scan
     * @return true if everything is ok, else false`
     */
    /**
     * Default constructor
     */
    public ScanControl() {
        isScanStarted = false;
        try {
            contentListControl = (ContentListControl) IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean scanAll(int signalType, boolean keepCurrentChannelList) {
        // if (isAntennaConnected) {
        scanSignalType = signalType;
        isScanStarted = true;
        isAutoScan = true;
        keepChannelList = keepCurrentChannelList;
        isScanFinished = 0;
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "scanAll (" + signalType + ", "
                    + keepCurrentChannelList + ")");
        // if false - clear favourite list
        try {
            if (!keepCurrentChannelList) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "deleting favourite lists");
                }
                IWEDIAService.getInstance().getStorageManager()
                        .setActiveController(ControllerType.FAVOURITE_LIST);
                IWEDIAService
                        .getInstance()
                        .getStorageManager()
                        .removeContentFromList(
                                IWEDIAService.getInstance()
                                        .getFavoriteListTableName(), null);
                IWEDIAService.getInstance().getStorageManager()
                        .setActiveController(ControllerType.RECENTLY_LIST);
                IWEDIAService
                        .getInstance()
                        .getStorageManager()
                        .removeContentFromList(
                                IWEDIAService.getInstance()
                                        .getRecentlyListTableName(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int mainDecoderID = IWEDIAService.getInstance().getDtvManagerProxy()
                .getDecoderID(mDisplayId);
        setScanType(scanSignalType);
        switch (scanSignalType) {
            case ScanSignalType.SIGNAL_TYPE_SATTELITE:
                tunerType = TunerType.SATTELITE;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDSat();
                tmpLiveRoute = IWEDIAService.getInstance().getDtvManagerProxy()
                        .getLiveRouteIDSat(mainDecoderID);
                break;
            case ScanSignalType.SIGNAL_TYPE_TERRESTRIAL:
                tunerType = TunerType.TERRESTRIAL;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDTer();
                tmpLiveRoute = IWEDIAService.getInstance().getDtvManagerProxy()
                        .getLiveRouteIDTer(mainDecoderID);
                break;
            case ScanSignalType.SIGNAL_TYPE_CABLE:
                tunerType = TunerType.CABLE;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDCab();
                tmpLiveRoute = IWEDIAService.getInstance().getDtvManagerProxy()
                        .getLiveRouteIDCab(mainDecoderID);
                break;
            case ScanSignalType.SIGNAL_TYPE_IP:
                tunerType = TunerType.IP;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getIpInstallRouteID();
                break;
        }
        Service service = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getServiceControl()
                .getActiveService(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
        if (service.getServiceIndex() == -1) {
            try {
                IWEDIAService.getInstance().getDTVManager().getScanControl()
                        .autoScan(installRouteId);
            } catch (InternalException e) {
                e.printStackTrace();
                try {
                    scanCallback.errorOccurred();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                return false;
            }
        } else {
            try {
                IWEDIAService
                        .getInstance()
                        .getDTVManager()
                        .getServiceControl()
                        .stopService(
                                IWEDIAService.getInstance()
                                        .getDtvManagerProxy()
                                        .getCurrentLiveRoute());
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startTimerTask();
            } catch (InternalException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void startTimerTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "TIMER	isScanStarted" + isScanStarted);
                }
                if (isScanStarted)
                    if (isAutoScan) {
                        isScanStarted = false;
                        if (IWEDIAService.DEBUG) {
                            Log.e(LOG_TAG, "startScan all: installRoute:"
                                    + installRouteId);
                        }
                        try {
                            IWEDIAService.getInstance().getDTVManager()
                                    .getScanControl().autoScan(installRouteId);
                        } catch (InternalException e) {
                            try {
                                scanCallback.errorOccurred();
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else {
                        IWEDIAService.getInstance().getDTVManager()
                                .getScanControl().appendList(updateList);
                        try {
                            IWEDIAService.getInstance().getDTVManager()
                                    .getScanControl()
                                    .manualScan(installRouteId);
                        } catch (InternalException e) {
                            try {
                                scanCallback.errorOccurred();
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 5);
    }

    /**
     * Abort previously started scan.
     * 
     * @return true if everything is ok, else false`
     */
    @Override
    public boolean abortScan() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "abortScan");
        }
        try {
            IWEDIAService.getInstance().getDTVManager().getScanControl()
                    .abortScan(installRouteId);
            return true;
        } catch (InternalException e) {
            return false;
        }
    }

    /**
     * Manual scan for desired input params. Depending on signal type, other
     * parameters needs to be set for manual scan.
     * 
     * @param signalType
     *        Type of the signal (cable, terrestrial,satellite or IP)
     * @param updateList
     *        Flag that specifies should existing service list be updated with
     *        new stations, or reset before the scan
     * @return true if everything is ok, else false
     */
    @Override
    public boolean manualScan(int signalType, boolean keepCurrentChannelList) {
        isAutoScan = false;
        isScanStarted = true;
        updateList = keepCurrentChannelList;
        keepChannelList = keepCurrentChannelList;
        isScanFinished = 0;
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "manualScan (" + signalType + ", "
                    + keepCurrentChannelList + ")");
        // if false - clear favourite list
        if (!keepCurrentChannelList) {
            IWEDIAService.getInstance().getStorageManager()
                    .setActiveController(ControllerType.FAVOURITE_LIST);
            IWEDIAService
                    .getInstance()
                    .getStorageManager()
                    .removeContentFromList(
                            IWEDIAService.getInstance()
                                    .getFavoriteListTableName(), null);
            IWEDIAService.getInstance().getStorageManager()
                    .setActiveController(ControllerType.RECENTLY_LIST);
            IWEDIAService
                    .getInstance()
                    .getStorageManager()
                    .removeContentFromList(
                            IWEDIAService.getInstance()
                                    .getRecentlyListTableName(), null);
        }
        switch (signalType) {
            case ScanSignalType.SIGNAL_TYPE_SATTELITE:
                tunerType = TunerType.SATTELITE;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDSat();
                break;
            case ScanSignalType.SIGNAL_TYPE_TERRESTRIAL:
                tunerType = TunerType.TERRESTRIAL;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDTer();
                break;
            case ScanSignalType.SIGNAL_TYPE_CABLE:
                tunerType = TunerType.CABLE;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDCab();
                break;
            case ScanSignalType.SIGNAL_TYPE_IP:
                tunerType = TunerType.IP;
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getIpInstallRouteID();
                break;
        }
        Service service = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getServiceControl()
                .getActiveService(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
        if (service.getServiceIndex() == -1) {
            try {
                IWEDIAService.getInstance().getDTVManager().getScanControl()
                        .appendList(updateList);
                IWEDIAService.getInstance().getDTVManager().getScanControl()
                        .manualScan(installRouteId);
            } catch (InternalException e) {
                e.printStackTrace();
                try {
                    scanCallback.errorOccurred();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                return false;
            }
        } else {
            try {
                IWEDIAService
                        .getInstance()
                        .getDTVManager()
                        .getServiceControl()
                        .stopService(
                                IWEDIAService.getInstance()
                                        .getDtvManagerProxy()
                                        .getCurrentLiveRoute());
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startTimerTask();
            } catch (InternalException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Sets the LNB type.
     * 
     * @param lnbType
     *        Index in list of LNB types
     * @return true if everything is ok, else false
     */
    @Override
    public void setLnbType(int lnbType) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setLnbType(lnbType);
    }

    /**
     * Gets the index of current LNB type
     * 
     * @return Index of current LNB type
     */
    @Override
    public int getLnbType() {
        return (int) IWEDIAService.getInstance().getDTVManager()
                .getScanControl().getLnbType();
    }

    /**
     * Gets the count of LNB types
     * 
     * @return Count of LNB types
     */
    @Override
    public int getLnbTypeCount() {
        return (int) IWEDIAService.getInstance().getDTVManager()
                .getScanControl().getLnbTypeCount();
    }

    /**
     * Gets the Name of LNB type
     * 
     * @param index
     *        Index in list of LNB Types
     * @return name of the LNB
     */
    @Override
    public String getLnbName(int index) {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getLnbName(index);
    }

    /**
     * Sets the frequency of LNB oscillator for high band reception.
     * 
     * @param lnbHi
     *        Frequency of LNB oscillator for high band reception. If LNB has
     *        only one oscillator this value should be zero.
     * @return true if everything is ok, else false
     */
    @Override
    public void setLnbHigh(int lnbHi) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setLnbHigh(lnbHi);
    }

    /**
     * Gets the frequency of LNB oscillator for high band reception.
     * 
     * @return Frequency of LNB oscillator for high band reception. If LNB has
     *         only one oscillator the return value should be zero.
     */
    @Override
    public int getLnbHigh() {
        return (int) IWEDIAService.getInstance().getDTVManager()
                .getScanControl().getLnbHigh();
    }

    /**
     * Sets the frequency of LNB oscillator for low band reception.
     * 
     * @param lnbLo
     *        Frequency of LNB oscillator for low band reception. If LNB has
     *        only one oscillator this value should represent frequency of LNB
     *        oscillator.
     * @return true if everything is ok, else false
     */
    @Override
    public void setLnbLow(int lnbLo) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setLnbLow(lnbLo);
    }

    /**
     * Gets the frequency of LNB oscillator for high band reception.
     * 
     * @return Frequency of LNB oscillator for low band reception. If LNB has
     *         only one oscillator the return value represents frequency of LNB
     *         oscillator
     */
    @Override
    public int getLnbLow() {
        return (int) IWEDIAService.getInstance().getDTVManager()
                .getScanControl().getLnbLow();
    }

    /**
     * Sets the LNB band type.
     * 
     * @param bandType
     *        LNB band type
     * @return true if everything is ok, else false
     */
    @Override
    public void setLnbBandType(BandType bandType) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setLnbBandType(bandType);
    }

    /**
     * Gets LNB band type
     * 
     * @return LNB band type
     */
    @Override
    public BandType getLnbBandType() {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getLnbBandType();
    }

    /**
     * Sets the frequency of the service for manual scan.
     * 
     * @param frequency
     *        Frequency of the service for manual scan.
     * @return true if everything is ok, else false
     */
    @Override
    public void setFrequency(int frequency) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "setFrequency:" + frequency);
        }
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setFrequency(frequency);
    }

    /**
     * Gets the frequency of the current service.
     * 
     * @return Frequency of the current service.
     */
    @Override
    public int getFrequency() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getFrequency()");
        }
        return (int) IWEDIAService.getInstance().getDTVManager()
                .getScanControl().getFrequency();
    }

    /**
     * Sets the symbol rate of the service for manual scan.
     * 
     * @param symbolRate
     *        Symbol rate of the service for manual scan.
     * @return true if everything is ok, else false
     */
    @Override
    public void setSymbolRate(int symbolRate) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setSymbolRate(symbolRate);
    }

    /**
     * Gets the symbol rate of the current service.
     * 
     * @return Symbol rate of the service
     */
    @Override
    public int getSymbolRate() {
        return (int) IWEDIAService.getInstance().getDTVManager()
                .getScanControl().getSymbolRate();
    }

    /**
     * Sets the modulation of the service for manual scan.
     * 
     * @param modulation
     *        Modulation of the service for manual scan.
     * @return true if everything is ok, else false
     */
    @Override
    public void setModulation(Modulation modulation) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setModulation(modulation);
    }

    /**
     * Gets the modulation of the current service.
     * 
     * @return Modulation of the service
     */
    @Override
    public Modulation getModulation() {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getModulation();
    }

    /**
     * Sets FecType of the service for manual scan.
     * 
     * @param fec
     *        FecType of the service for manual scan.
     * @return true if everything is ok, else false
     */
    @Override
    public void setFecType(FecType fec) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setFecType(fec);
    }

    /**
     * Gets the FEC of the current service.
     * 
     * @return {@link FEC}
     */
    @Override
    public FecType getFecType() {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getFecType();
    }

    /**
     * Sets the polarization of the service for manual scan.
     * 
     * @param polarization
     *        Polarization of the service for manual scan
     * @return true if everything is ok, else false
     */
    @Override
    public void setPolarization(Polarization polarization) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setPolarization(polarization);
    }

    /**
     * Gets the polarization of the service.
     * 
     * @return Polarization of the service.
     */
    @Override
    public Polarization getPolarization() {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getPolarization();
    }

    /**
     * Gets signal information.
     * 
     * @return {@link SignalInfo}
     */
    @Override
    public SignalInfo getSignalInfo() {
        SignalInfo info = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getScanControl()
                .getSignalInfo(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
        return info;
    }

    @Override
    public void setScanType(int scanType) {
        RouteInstallSettings settings = new RouteInstallSettings();
        switch (scanType) {
            case ScanSignalType.SIGNAL_TYPE_TERRESTRIAL:
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDTer();
                settings.setFrontendType(RouteFrontendType.TER);
                break;
            case ScanSignalType.SIGNAL_TYPE_CABLE:
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDCab();
                settings.setFrontendType(RouteFrontendType.CAB);
                break;
            case ScanSignalType.SIGNAL_TYPE_SATTELITE:
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDSat();
                settings.setFrontendType(RouteFrontendType.SAT);
                break;
            case ScanSignalType.SIGNAL_TYPE_IP:
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getIpInstallRouteID();
                settings.setFrontendType(RouteFrontendType.IP);
                break;
            case ScanSignalType.SIGNAL_TYPE_ANALOG:
                installRouteId = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getInstallRouteIDAtv();
                settings.setFrontendType(RouteFrontendType.ANALOG);
                break;
        }
        IWEDIAService.getInstance().getDTVManager().getBroadcastRouteControl()
                .configureInstallRoute(installRouteId, settings);
    }

    @Override
    public int getScanType() {
        int scanType = -1;
        RouteFrontendType feType;
        // By default, use terrestrial install route
        if (installRouteId == -1) {
            installRouteId = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getInstallRouteIDTer();
        }
        RouteInstallSettings settings = IWEDIAService.getInstance()
                .getDTVManager().getBroadcastRouteControl()
                .getInstallRouteConfiguration(installRouteId);
        feType = settings.getFrontendType();
        switch (feType.getValue()) {
            case 1:
                scanType = ScanSignalType.SIGNAL_TYPE_SATTELITE;
                break;
            case 2:
                scanType = ScanSignalType.SIGNAL_TYPE_CABLE;
                break;
            case 4:
                scanType = ScanSignalType.SIGNAL_TYPE_TERRESTRIAL;
                break;
            case 8:
                scanType = ScanSignalType.SIGNAL_TYPE_IP;
                break;
        }
        return scanType;
        // return ScanSignalType.SIGNAL_TYPE_TERRESTRIAL;
    }

    @Override
    public void registerCallback(IScanCallback mScanCallback) {
        scanCallback = mScanCallback;
    }

    private static void createServiceLists() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "creating favourite lists - start");
        }
        int numberOfServiceLists = 0;
        Log.d(LOG_TAG, "createServiceLists:" + numberOfServiceLists);
        try {
            numberOfServiceLists = (int) IWEDIAService.getInstance()
                    .getDTVManager().getServiceControl()
                    .getNumberOfServiceLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "numberOfServiceLists:" + numberOfServiceLists);
        }
        if (numberOfServiceLists != 1) {
            if (!keepChannelList) {
                deleteFavoriteLists(numberOfServiceLists);
                filterServiceLists();
            } else {
                // switch (tunerType) {
                // case TERRESTRIAL:
                // deleteFavoriteListByListIndex(serviceIndexTer);
                // break;
                // case CABLE:
                // deleteFavoriteListByListIndex(serviceIndexCab);
                // break;
                // case SATTELITE:
                // deleteFavoriteListByListIndex(serviceIndexSat);
                // break;
                // default:
                // break;
                // }
                // deleteFavoriteListByListIndex(serviceIndexRadio);
                // deleteFavoriteListByListIndex(serviceIndexData);
                filterServiceListsByType(tunerType);
            }
        } else {
            // number of service list is 1
            filterServiceLists();
        }
        if (IWEDIAService.WRITE_SERVICE_LISTS_TO_FILE) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "writing service lists to files");
            }
            writeServiceListsToFiles();
        }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "creating favourite lists - end");
        }
    }

    private static int serviceIndexCab = 0;
    private static int serviceIndexSat = 0;
    private static int serviceIndexTer = 0;
    private static int serviceIndexRadio = 0;
    private static int serviceIndexData = 0;
    private static int serviceIndexAtv = 0;

    private static void filterServiceListsByType(TunerType type) {
        try {
            switch (type) {
                case CABLE:
                    // serviceIndexCab = IWEDIAService.getInstance()
                    // .getDTVManager().getServiceControl()
                    // .createServiceList("DVB_C");
                    EnumSet<ServiceListFilter> filterDvbC = EnumSet.of(
                            ServiceListFilter.TV, ServiceListFilter.DVB_C);
                    ServiceListFilterSet filterSetDvbC = new ServiceListFilterSet();
                    filterSetDvbC.setFilter(filterDvbC);
                    IWEDIAService
                            .getInstance()
                            .getDTVManager()
                            .getServiceControl()
                            .filterServiceList(0, serviceIndexCab,
                                    filterSetDvbC);
                    break;
                case SATTELITE:
                    // serviceIndexSat = IWEDIAService.getInstance()
                    // .getDTVManager().getServiceControl()
                    // .createServiceList("DVB_S");
                    EnumSet<ServiceListFilter> filterDvbS = EnumSet.of(
                            ServiceListFilter.TV, ServiceListFilter.DVB_S);
                    ServiceListFilterSet filterSetDvbS = new ServiceListFilterSet();
                    filterSetDvbS.setFilter(filterDvbS);
                    IWEDIAService
                            .getInstance()
                            .getDTVManager()
                            .getServiceControl()
                            .filterServiceList(0, serviceIndexSat,
                                    filterSetDvbS);
                    break;
                case TERRESTRIAL:
                    // serviceIndexTer = IWEDIAService.getInstance()
                    // .getDTVManager().getServiceControl()
                    // .createServiceList("DVB_T");
                    EnumSet<ServiceListFilter> filterDvbT = EnumSet.of(
                            ServiceListFilter.TV, ServiceListFilter.DVB_T);
                    ServiceListFilterSet filterSetDvbT = new ServiceListFilterSet();
                    filterSetDvbT.setFilter(filterDvbT);
                    IWEDIAService
                            .getInstance()
                            .getDTVManager()
                            .getServiceControl()
                            .filterServiceList(0, serviceIndexTer,
                                    filterSetDvbT);
                    break;
                default:
                    break;
            }
            // serviceIndexRadio = IWEDIAService.getInstance().getDTVManager()
            // .getServiceControl().createServiceList("RADIO");
            // serviceIndexData = IWEDIAService.getInstance().getDTVManager()
            // .getServiceControl().createServiceList("DATA");
            /*
             * int serviceIndexIp = IWEDIAService.getInstance().getDTVManager()
             * .getServiceControl().createServiceList("IP");
             */
            Log.e(LOG_TAG, "createFavoriteLists: serviceIndexSat:"
                    + serviceIndexSat + ", serviceIndexTer:" + serviceIndexTer
                    + ", serviceIndexCab:" + serviceIndexCab
                    + ", serviceIndexRadio:" + serviceIndexRadio
                    + ", serviceIndexData:" + serviceIndexData);
            EnumSet<ServiceListFilter> filterDvbR = EnumSet
                    .of(ServiceListFilter.RADIO);
            EnumSet<ServiceListFilter> filterDvbD = EnumSet
                    .of(ServiceListFilter.DATA);
            /*
             * EnumSet<ServiceListFilter> filterDvbI = EnumSet
             * .of(ServiceListFilter.IP);
             */
            ServiceListFilterSet filterSetDvbR = new ServiceListFilterSet();
            ServiceListFilterSet filterSetDvbD = new ServiceListFilterSet();
            // ServiceListFilterSet filterSetDvbI = new ServiceListFilterSet();
            filterSetDvbR.setFilter(filterDvbR);
            filterSetDvbD.setFilter(filterDvbD);
            // filterSetDvbI.setFilter(filterDvbI);
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .filterServiceList(0, serviceIndexRadio, filterSetDvbR);
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .filterServiceList(0, serviceIndexData, filterSetDvbD);
            /*
             * IWEDIAService.getInstance().getDTVManager().getServiceControl()
             * .filterServiceList(0, serviceIndexIp, filterSetDvbI);
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void filterServiceLists() {
        try {
            serviceIndexSat = IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl().createServiceList("DVB_S");
            serviceIndexTer = IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl().createServiceList("DVB_T");
            serviceIndexCab = IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl().createServiceList("DVB_C");
            if (IWEDIAService.isTvPlatform) {
                serviceIndexAtv = IWEDIAService.getInstance().getDTVManager()
                        .getServiceControl().createServiceList("ATV");
            }
            serviceIndexRadio = IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl().createServiceList("RADIO");
            serviceIndexData = IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl().createServiceList("DATA");
            /*
             * int serviceIndexIp = IWEDIAService.getInstance().getDTVManager()
             * .getServiceControl().createServiceList("IP");
             */
            Log.e(LOG_TAG, "createFavoriteLists: serviceIndexSat:"
                    + serviceIndexSat + ", serviceIndexTer:" + serviceIndexTer
                    + ", serviceIndexCab:" + serviceIndexCab
                    + ", serviceIndexAtv:" + serviceIndexAtv
                    + ", serviceIndexRadio:" + serviceIndexRadio
                    + ", serviceIndexData:" + serviceIndexData);
            EnumSet<ServiceListFilter> filterDvbT = EnumSet.of(
                    ServiceListFilter.TV, ServiceListFilter.DVB_T);
            EnumSet<ServiceListFilter> filterDvbS = EnumSet.of(
                    ServiceListFilter.TV, ServiceListFilter.DVB_S);
            EnumSet<ServiceListFilter> filterDvbC = EnumSet.of(
                    ServiceListFilter.TV, ServiceListFilter.DVB_C);
            EnumSet<ServiceListFilter> filterDvbR = EnumSet
                    .of(ServiceListFilter.RADIO);
            EnumSet<ServiceListFilter> filterDvbD = EnumSet
                    .of(ServiceListFilter.DATA);
            /*
             * EnumSet<ServiceListFilter> filterDvbI = EnumSet
             * .of(ServiceListFilter.IP);
             */
            ServiceListFilterSet filterSetDvbT = new ServiceListFilterSet();
            ServiceListFilterSet filterSetDvbC = new ServiceListFilterSet();
            ServiceListFilterSet filterSetDvbS = new ServiceListFilterSet();
            ServiceListFilterSet filterSetDvbR = new ServiceListFilterSet();
            ServiceListFilterSet filterSetDvbD = new ServiceListFilterSet();
            ServiceListFilterSet filterSetAtv = new ServiceListFilterSet();
            // ServiceListFilterSet filterSetDvbI = new ServiceListFilterSet();
            filterSetDvbT.setFilter(filterDvbT);
            filterSetDvbC.setFilter(filterDvbC);
            filterSetDvbS.setFilter(filterDvbS);
            filterSetDvbR.setFilter(filterDvbR);
            filterSetDvbD.setFilter(filterDvbD);
            // filterSetDvbI.setFilter(filterDvbI);
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .filterServiceList(0, serviceIndexTer, filterSetDvbT);
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .filterServiceList(0, serviceIndexCab, filterSetDvbC);
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .filterServiceList(0, serviceIndexSat, filterSetDvbS);
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .filterServiceList(0, serviceIndexRadio, filterSetDvbR);
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .filterServiceList(0, serviceIndexData, filterSetDvbD);
            if (IWEDIAService.isTvPlatform) {
                IWEDIAService.getInstance().getDTVManager().getServiceControl()
                        .filterServiceList(0, serviceIndexAtv, filterSetAtv);
            }
            /*
             * IWEDIAService.getInstance().getDTVManager().getServiceControl()
             * .filterServiceList(0, serviceIndexIp, filterSetDvbI);
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFavoriteLists(int numberOfServiceLists) {
        for (int i = numberOfServiceLists - 1; i > 0; i--) {
            try {
                IWEDIAService.getInstance().getDTVManager().getServiceControl()
                        .deleteServiceList(i);
            } catch (InternalException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteFavoriteListByListIndex(int serviceListIndex) {
        try {
            IWEDIAService.getInstance().getDTVManager().getServiceControl()
                    .deleteServiceList(serviceListIndex);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    private static void writeServiceListsToFiles() {
        int numberOfLists = 0;
        Log.d(LOG_TAG, "numberOfLists init:" + numberOfLists);
        try {
            numberOfLists = (int) IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl().getNumberOfServiceLists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path = IWEDIAService.getInstance().getFilesDir() + "/service_Lists/";
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        for (int i = 0; i < numberOfLists; i++) {
            openFile("serviceList" + i);
            int numberOfElementsInServiceList = 0;
            Log.d(LOG_TAG, "numberOfLists init:"
                    + numberOfElementsInServiceList);
            try {
                numberOfElementsInServiceList = IWEDIAService.getInstance()
                        .getDTVManager().getServiceControl()
                        .getServiceListCount(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int j = 0; j < numberOfElementsInServiceList; j++)
                writeToFile(IWEDIAService.getInstance().getDTVManager()
                        .getServiceControl().getServiceDescriptor(i, j)
                        .getName());
            closeFile();
        }
    }

    private static FileWriter f;
    private static String path;

    private static void openFile(String fileName) {
        try {
            f = new FileWriter(path + "/" + fileName + ".txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(String text) {
        try {
            f.write("\n" + text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeFile() {
        try {
            f.flush();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterCallback(IScanCallback arg0) {
        scanCallback = null;
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .unregisterCallback(ScanControl.getScanCallback());
    }

    /**
     * Get index of the current satellite from satellite list.
     * 
     * @return Returns index of satellite
     */
    @Override
    public int getCurrentSatelliteIndex() {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getSatellite(0);
    }

    /**
     * Get number of satellites.
     * 
     * @return Returns number of satellites
     */
    @Override
    public int getNumberOfSatellites() {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getNumberOfSatellites();
    }

    /**
     * Get name of the satellite with 'satelliteIndex'.
     * 
     * @param satelliteIndex
     *        Index of the satellite
     * @return Returns satellite name
     */
    @Override
    public String getSatelliteName(int index) {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getSatelliteName(index);
    }

    /**
     * Set the sattelite
     * 
     * @param index
     *        - index of the sattelite
     * @return true if everything is ok, else false
     */
    @Override
    public void setSatelite(int index) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setSatellite(0, index);
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    private static Content getFirstCorrectContent() {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "getFirstCorrectContent");
        Content content = null;
        try {
            int numberOfServices = IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl().getServiceListCount(0);
            if (numberOfServices != 0) {
                ServiceDescriptor service = null;
                for (int i = 0; i < numberOfServices; i++) {
                    service = IWEDIAService.getInstance().getDTVManager()
                            .getServiceControl().getServiceDescriptor(0, i);
                    if (!(service.getName().contains("Dummy"))) {
                        content = new ServiceContent(service,
                                service.getMasterIndex(), 0);
                    }
                    if (content != null) {
                        break;
                    }
                }
            } else {
                IContentFilter ipStreamContentFilter = IWEDIAService
                        .getInstance().getDtvManagerProxy()
                        .getContentListControl()
                        .getContentFilter(FilterType.IP_STREAM);
                if (ipStreamContentFilter.getContentListSize() != 0) {
                    return ipStreamContentFilter.getContent(0);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * Sets the atv system of the service for manual scan.
     * 
     * @param system
     *        System of the service for manual scan
     * @return true if everything is ok, else false
     */
    @Override
    public void setAnalogEncodingMode(AnalogEncodingMode mode) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setAnalogEncodingMode(0, mode);
    }

    /**
     * Gets the atv system of the service.
     * 
     * @return Atv system of the service.
     */
    @Override
    public AnalogEncodingMode getAnalogEncodingMode() {
        return IWEDIAService.getInstance().getDTVManager().getScanControl()
                .getAnalogEncodingMode(0);
    }

    @Override
    public void atvFineTune(int frequency, boolean save) {
        try {
            IWEDIAService.getInstance().getDTVManager().getScanControl()
                    .atvFineTune(0, frequency, save);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNetNumber(int netNumber) {
        IWEDIAService.getInstance().getDTVManager().getScanControl()
                .setNetworkNumber(netNumber);
    }

    @Override
    public void storeNetworkDefaultValues(int NID, int frequency,
            int symbolRate, Modulation modulation) {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getScanControl()
                .storeNetworkDefaultValues(NID, frequency, symbolRate,
                        modulation);
    }

    private static com.iwedia.dtv.scan.IScanCallback tunerCallback = new com.iwedia.dtv.scan.IScanCallback() {
        @Override
        public void antennaConnected(int deviceID, boolean success) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "antenna connected callback triggered");
            }
            if (contentListControl.getChannelsCallbackManager() != null)
                try {
                    contentListControl.getChannelsCallbackManager()
                            .antennaConnected(deviceID, success);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

        @Override
        public void installServiceDATANumber(int routeID, int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void installServiceRADIOName(int routeID, String name) {
            try {
                scanCallback.installService(new ServiceContent(name,
                        ServiceType.DIG_RAD));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void installServiceRADIONumber(int routeID, int number) {
            numberOfInstalledRadioServices = number;
        }

        @Override
        public void installServiceTVName(int routeID, String name) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "install service tv:" + name);
            }
            try {
                scanCallback.installService(new ServiceContent(name,
                        ServiceType.DIG_TV));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void installServiceTVNumber(int routeID, int number) {
            numberOfInstalledTVServices = number;
        }

        @Override
        public void scanFinished(int routeID) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "scanFinished-start");
            }
            if (isScanFinished != 0) {
                return;
            }
            isScanFinished = 1;
            Thread thr = new Thread(new Runnable() {
                int mainDecoderID = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getDecoderID(mDisplayId);

                @Override
                public void run() {
                    if (IWEDIAService.FAVORITE) {
                        createServiceLists();
                    }
                    // Call 2 times setActiveContent with parameter null, so
                    // both active and previous
                    // content are set to null.
                    contentListControl.setActiveContent(null, mDisplayId);
                    contentListControl.setActiveContent(null, mDisplayId);
                    int numberOfServices;
                    try {
                        contentListControl.setActiveFilter(FilterType.ALL);
                        contentListControl.reinitialize();
                        numberOfServices = IWEDIAService
                                .getInstance()
                                .getDtvManagerProxy()
                                .getServiceControl()
                                .getServiceListCount(
                                        ServiceListIndex.MASTER_LIST);
                        if (numberOfServices == 0) {
                            if (IWEDIAService.DEBUG)
                                Log.e(LOG_TAG, "numberOfServices: "
                                        + numberOfServices);
                            scanCallback.noChannelsFound();
                        }
                        if (numberOfServices > 0) {
                            /**
                             * Take first service from MW master service list
                             * and check service type of returned DTV service.
                             * Service type will be used to manage index of MW
                             * service list used to initially play service when
                             * scan finished.
                             */
                            Content content = getFirstCorrectContent();
                            contentListControl.setActiveFilter(FilterType.ALL);
                            if (content != null) {
                                if (IWEDIAService.DEBUG)
                                    Log.e(LOG_TAG, "first correct content:"
                                            + content.toString());
                                switch (content.getSourceType()) {
                                    case SAT:
                                        IWEDIAService
                                                .getInstance()
                                                .getDtvManagerProxy()
                                                .setCurrentLiveRoute(
                                                        IWEDIAService
                                                                .getInstance()
                                                                .getDtvManagerProxy()
                                                                .getLiveRouteIDSat(
                                                                        mainDecoderID));
                                        break;
                                    case TER:
                                        IWEDIAService
                                                .getInstance()
                                                .getDtvManagerProxy()
                                                .setCurrentLiveRoute(
                                                        IWEDIAService
                                                                .getInstance()
                                                                .getDtvManagerProxy()
                                                                .getLiveRouteIDTer(
                                                                        mainDecoderID));
                                        break;
                                    case CAB:
                                        IWEDIAService
                                                .getInstance()
                                                .getDtvManagerProxy()
                                                .setCurrentLiveRoute(
                                                        IWEDIAService
                                                                .getInstance()
                                                                .getDtvManagerProxy()
                                                                .getLiveRouteIDCab(
                                                                        mainDecoderID));
                                        break;
                                    case ANALOG:
                                        IWEDIAService
                                                .getInstance()
                                                .getDtvManagerProxy()
                                                .setCurrentLiveRoute(
                                                        IWEDIAService
                                                                .getInstance()
                                                                .getDtvManagerProxy()
                                                                .getLiveRouteIDAtv(
                                                                        mainDecoderID));
                                        break;
                                    default:
                                        IWEDIAService
                                                .getInstance()
                                                .getDtvManagerProxy()
                                                .setCurrentLiveRoute(
                                                        tmpLiveRoute);
                                        break;
                                }
                                /**
                                 *************************************************************************************
                                 */
                                contentListControl.setActiveContent(content,
                                        mDisplayId);
                                contentListControl
                                        .setCurrentServiceIndex(content
                                                .getIndex());
                                /**
                                 *************************************************************************************
                                 */
                                Editor edit = IWEDIAService.getInstance()
                                        .getPreferenceManager().edit();
                                edit.putInt("lastServiceIndex",
                                        content.getIndex());
                                edit.commit();
                                // ///////////////////////////////////////////////
                            }
                        }
                        contentListControl.startVideoPlayback();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    numberOfInstalledRadioServices = 0;
                    numberOfInstalledTVServices = 0;
                    // numberOfInstalledDataServices = 0;
                    isScanStarted = false;
                    try {
                        scanCallback.scanFinished();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "scanFinished-end");
                    }
                }
            });
            thr.start();
        }

        @Override
        public void scanNoServiceSpace(int routeID) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "scan no service space");
            }
            try {
                scanCallback.scanNoServiceSpace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void scanProgressChanged(int routeID, int arg0) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "scan progress changed:" + arg0);
            }
            try {
                scanCallback.scanProgressChanged(arg0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void scanTunFrequency(int routeID, int arg0) {
            try {
                scanCallback.scanTunFrequency(arg0);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void signalBer(int routeID, int arg0) {
            try {
                scanCallback.signalBer(arg0);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void installServiceDATAName(int routeID, String name) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "install service data:" + name);
            }
            try {
                scanCallback.installService(new ServiceContent(name,
                        ServiceType.DATA_BROADCAST));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void signalQuality(int routeID, int arg0) {
            try {
                scanCallback.signalQuality(arg0);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void signalStrength(int routeID, int arg0) {
            try {
                scanCallback.signalStrength(arg0);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void networkChanged(int networkId) {
            try {
                contentListControl.getChannelsCallbackManager().networkChanged(
                        networkId);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void sat2ipServerDropped(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void triggerStatus(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void installStatus(ScanInstallStatus arg0) {
            // TODO Auto-generated method stub
        }
    };

    public static com.iwedia.dtv.scan.IScanCallback getScanCallback() {
        return tunerCallback;
    }
}
