package com.iwedia.service.route;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import android.util.Log;

import com.iwedia.comm.enums.PlaybackDestinationType;
import com.iwedia.dtv.route.broadcast.RouteDemuxDescriptor;
import com.iwedia.dtv.route.broadcast.RouteFrontendDescriptor;
import com.iwedia.dtv.route.broadcast.RouteFrontendType;
import com.iwedia.dtv.route.broadcast.RouteMassStorageDescriptor;
import com.iwedia.dtv.route.common.RouteDecoderDescriptor;
import com.iwedia.dtv.route.common.RouteInputOutputDescriptor;
import com.iwedia.dtv.route.common.RouteInputOutputDeviceType;
import com.iwedia.dtv.service.ServiceType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.service.IWEDIAService;

public class RouteManager {
    private final String LOG_TAG = "RouteManager";
    private int mLiveRouteIDTerMain = -1;
    private int mLiveRouteIDTerSecondary = -1;
    private int mLiveRouteIDCabMain = -1;
    private int mLiveRouteIDCabSecondary = -1;
    private int mLiveRouteIDATVMain = -1;
    private int mLiveRouteIDATVSecondary = -1;
    private int mLiveRouteIDSatMain = -1;
    private int mLiveRouteIDSatSecondary = -1;
    private int mLiveRouteIDIPMain = -1;
    private int mLiveRouteIDIPSecondary = -1;
    private int mInstallRouteIDTer = -1;
    private int mInstallRouteIDCab = -1;
    private int mInstallRouteIDSat = -1;
    private int mInstallRouteIDAtv = -1;
    private int mIPInstallRouteID = -1;
    private int recRouteIDTer = -1;
    private int recRouteIDCab = -1;
    private int recRouteIDSat = -1;
    private int recRouteIDAtv = -1;
    private int recRouteIDIP = -1;
    private int recRouteCurrent = -1;
    private int mPlaybackRouteIDMain = -1;
    private int mPlaybackRouteIDSecondary = -1;
    private RouteFrontendDescriptor dvbTFrDescriptor;
    private RouteFrontendDescriptor dvbCFrDescriptor;
    private RouteFrontendDescriptor dvbSFrDescriptor;
    private RouteFrontendDescriptor dvbAFrDescriptor;
    RouteInputOutputDescriptor mHDMIOutputDescriptor;
    private int mMainDecoderDescriptorId = -1;
    private int mSecondaryDecoderDescriptorId = -1;
    /*
     * RouteDecoderDescriptor mMainDecoderDescriptor; RouteDecoderDescriptor
     * mSecondaryDecoderDescriptor;
     */
    // TODO: Scan related operations should use installRoute!
    HashMap<Integer, RouteDecoderDescriptor> mPlaybackDestinationMap;
    ArrayList<RouteInputOutputDescriptor> mInputDeviceList;
    HashMap<Integer, Integer> mInputRouteMap;
    private int currentLiveRoute = -1;

    private int calculateKey(int devID, int decID, int port) {
        return devID * 100 + decID + port;
    }

    public RouteManager() {
    }

    public void initialize() {
        RouteFrontendDescriptor fDescriptorIterator;
        dvbTFrDescriptor = new RouteFrontendDescriptor();
        dvbCFrDescriptor = new RouteFrontendDescriptor();
        dvbSFrDescriptor = new RouteFrontendDescriptor();
        dvbAFrDescriptor = new RouteFrontendDescriptor();
        RouteFrontendDescriptor ipFrDescriptor = new RouteFrontendDescriptor();
        int frontendCnt = 0;
        boolean foundIpFrontent = false;
        boolean foundLiveDVBTFrontent = false;
        boolean foundLiveDVBCFrontent = false;
        boolean foundLiveDVBSFrontent = false;
        boolean foundLiveANAFrontent = false;
        mHDMIOutputDescriptor = new RouteInputOutputDescriptor();
        int outputDescriptorCnt = 0;
        boolean foundHDMIOutputDescriptor = false;
        int decoderCnt = 0;
        int ioDeviceCnt = 0;
        mPlaybackDestinationMap = new HashMap<Integer, RouteDecoderDescriptor>();
        mInputDeviceList = new ArrayList<RouteInputOutputDescriptor>();
        mInputRouteMap = new HashMap<Integer, Integer>();
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // /////////////// RETRIEVE DEMUX DESCRIPTOR ////////////////////////
        // //////////////////////////////////////////////////////////////////
        RouteDemuxDescriptor dDescriptor = new RouteDemuxDescriptor();
        dDescriptor = IWEDIAService.getInstance().getDTVManager()
                .getBroadcastRouteControl().getDemuxDescriptor(0);
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // ///////////////// RETRIEVE DECODER DESCRIPTOR ////////////////////
        // //////////////////////////////////////////////////////////////////
        decoderCnt = IWEDIAService.getInstance().getDTVManager()
                .getCommonRouteControl().getDecoderNumber();
        if (IWEDIAService.DEBUG) {
            Log.d(LOG_TAG, "setting decoder desc no[" + decoderCnt + "]");
        }
        RouteDecoderDescriptor dDecDescriptor = new RouteDecoderDescriptor();
        // rError = null;
        for (int i = 0; i < decoderCnt; i++) {
            dDecDescriptor = IWEDIAService.getInstance().getDTVManager()
                    .getCommonRouteControl().getDecoderDescriptor(i);
            // Comedia 3.0 and Comedia4.0 confict
            // if(dDecDescriptor.getDecoderId() == 0)
            // if(dDecDescriptor.getDecoderId() == 1)
            if (i == 0) { // main
                mPlaybackDestinationMap.put(PlaybackDestinationType.MAIN_LIVE,
                        dDecDescriptor);
                mPlaybackDestinationMap.put(PlaybackDestinationType.MAIN_PVR,
                        dDecDescriptor);
                mPlaybackDestinationMap.put(PlaybackDestinationType.MAIN_INPUT,
                        dDecDescriptor);
                mMainDecoderDescriptorId = dDecDescriptor.getDecoderId();
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "setting main decoder descriptor");
                }
                // if(dDecDescriptor.getDecoderId() == 1)
                // if(dDecDescriptor.getDecoderId() == 2)
            } else if (i == 1) { // secondary
                mPlaybackDestinationMap.put(PlaybackDestinationType.PIP_LIVE,
                        dDecDescriptor);
                mPlaybackDestinationMap.put(PlaybackDestinationType.PAP_LIVE,
                        dDecDescriptor);
                mPlaybackDestinationMap.put(PlaybackDestinationType.PIP_PVR,
                        dDecDescriptor);
                mPlaybackDestinationMap.put(PlaybackDestinationType.PAP_PVR,
                        dDecDescriptor);
                mPlaybackDestinationMap.put(PlaybackDestinationType.PIP_INPUT,
                        dDecDescriptor);
                mPlaybackDestinationMap.put(PlaybackDestinationType.PAP_INPUT,
                        dDecDescriptor);
                mSecondaryDecoderDescriptorId = dDecDescriptor.getDecoderId();
                if (IWEDIAService.DEBUG)
                    Log.e(LOG_TAG, "setting secondary decoder descriptor");
            }
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVING OUTPUT DESCRIPTOR /////////////////////
        // //////////////////////////////////////////////////////////////////
        outputDescriptorCnt = IWEDIAService.getInstance().getDTVManager()
                .getCommonRouteControl().getInputOutputNumber();
        if (IWEDIAService.DEBUG)
            Log.d(LOG_TAG, "setting output desc no[" + outputDescriptorCnt
                    + "]");
        // rError = null;
        RouteInputOutputDescriptor oDescriptor = new RouteInputOutputDescriptor();
        for (int i = 0; i < outputDescriptorCnt; i++) {
            oDescriptor = IWEDIAService.getInstance().getDTVManager()
                    .getCommonRouteControl().getInputOutputDescriptor(i);
            if (oDescriptor.getInputOutputDeviceType().getValue() == RouteInputOutputDeviceType.HDMI
                    .getValue() && !foundHDMIOutputDescriptor) { // we
                                                                 // are
                                                                 // intrested
                                                                 // in
                                                                 // HDMI
                                                                 // output
                if (mHDMIOutputDescriptor == null) {
                    mHDMIOutputDescriptor = oDescriptor;
                    if (IWEDIAService.DEBUG) {
                        Log.d(LOG_TAG, "HDMI output descriptor set.");
                    }
                    foundHDMIOutputDescriptor = true;
                } else {
                    if (IWEDIAService.DEBUG)
                        Log.e(LOG_TAG,
                                "error retrieving HDMI output descriptor not set!!");
                }
            }
        }
        // //////////////////////////////////////////////////////////////////
        // ////////////// RETRIEVE MASS STORAGE DESCRIPTOR //////////////////
        // //////////////////////////////////////////////////////////////////
        RouteMassStorageDescriptor mDescriptor = new RouteMassStorageDescriptor();
        mDescriptor = IWEDIAService.getInstance().getDTVManager()
                .getBroadcastRouteControl().getMassStorageDescriptor(0);
        // //////////////////////////////////////////////////////////////////
        // ///////////// GET NUMBER OF FRONTENDS ////////////////////////////
        // //////////////////////////////////////////////////////////////////
        frontendCnt = IWEDIAService.getInstance().getDTVManager()
                .getBroadcastRouteControl().getFrontendNumber();
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "numberOfFrontendDescriptors:" + frontendCnt);
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // ///////////// FIND DVB and IP frontent descriptor ///////////////
        // //////////////////////////////////////////////////////////////////
        for (int i = 0; i < frontendCnt; i++) {
            fDescriptorIterator = new RouteFrontendDescriptor();
            fDescriptorIterator = IWEDIAService.getInstance().getDTVManager()
                    .getBroadcastRouteControl().getFrontendDescriptor(i);
            EnumSet<RouteFrontendType> frontendType;
            frontendType = fDescriptorIterator.getFrontendType();
            if (frontendType.contains(RouteFrontendType.IP)) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "found frontent type ip");
                }
                if (!foundIpFrontent) {
                    foundIpFrontent = true;
                    ipFrDescriptor = fDescriptorIterator;
                }
            }
            if (frontendType.contains(RouteFrontendType.SAT)) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "found frontent type sat");
                }
                if (!foundLiveDVBSFrontent) {
                    foundLiveDVBSFrontent = true;
                    dvbSFrDescriptor = fDescriptorIterator;
                    if (mMainDecoderDescriptorId != -1
                            && mHDMIOutputDescriptor != null) {
                        mLiveRouteIDSatMain = IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getBroadcastRouteControl()
                                .getLiveRoute(dvbSFrDescriptor.getFrontendId(),
                                        dDescriptor.getDemuxId(),
                                        mMainDecoderDescriptorId);
                    }
                    if (currentLiveRoute == -1) {
                        currentLiveRoute = mLiveRouteIDSatMain;
                    }
                }
            }
            if (frontendType.contains(RouteFrontendType.TER)) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "found frontent type ter");
                }
                if (!foundLiveDVBTFrontent) {
                    foundLiveDVBTFrontent = true;
                    dvbTFrDescriptor = fDescriptorIterator;
                    // Set route for main decoder
                    if (mMainDecoderDescriptorId != -1
                            && mHDMIOutputDescriptor != null) {
                        mLiveRouteIDTerMain = IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getBroadcastRouteControl()
                                .getLiveRoute(dvbTFrDescriptor.getFrontendId(),
                                        dDescriptor.getDemuxId(),
                                        mMainDecoderDescriptorId);
                    }
                    if (currentLiveRoute == -1) {
                        currentLiveRoute = mLiveRouteIDTerMain;
                    }
                    // Set route for secondary decoder
                    if (mSecondaryDecoderDescriptorId != -1
                            && mHDMIOutputDescriptor != null) {
                        mLiveRouteIDTerSecondary = IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getBroadcastRouteControl()
                                .getLiveRoute(dvbTFrDescriptor.getFrontendId(),
                                        dDescriptor.getDemuxId(),
                                        mSecondaryDecoderDescriptorId);
                    }
                }
            }
            if (frontendType.contains(RouteFrontendType.CAB)) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "found frontent type cab");
                }
                if (!foundLiveDVBCFrontent) {
                    foundLiveDVBCFrontent = true;
                    dvbCFrDescriptor = fDescriptorIterator;
                    if (mMainDecoderDescriptorId != -1
                            && mHDMIOutputDescriptor != null) {
                        mLiveRouteIDCabMain = IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getBroadcastRouteControl()
                                .getLiveRoute(dvbCFrDescriptor.getFrontendId(),
                                        dDescriptor.getDemuxId(),
                                        mMainDecoderDescriptorId);
                    }
                    if (currentLiveRoute == -1) {
                        currentLiveRoute = mLiveRouteIDCabMain;
                    }
                    // Set route for secondary decoder
                    if (mSecondaryDecoderDescriptorId != -1
                            && mHDMIOutputDescriptor != null) {
                        mLiveRouteIDCabSecondary = IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getBroadcastRouteControl()
                                .getLiveRoute(dvbCFrDescriptor.getFrontendId(),
                                        dDescriptor.getDemuxId(),
                                        mSecondaryDecoderDescriptorId);
                    }
                }
            }
            if (frontendType.contains(RouteFrontendType.ANALOG)) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "found frontent type ana");
                }
                if (!foundLiveANAFrontent) {
                    foundLiveANAFrontent = true;
                    // Set route for main decoder
                    dvbAFrDescriptor = fDescriptorIterator;
                    if (mMainDecoderDescriptorId != -1
                            && mHDMIOutputDescriptor != null) {
                        mLiveRouteIDATVMain = IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getBroadcastRouteControl()
                                .getLiveRoute(dvbAFrDescriptor.getFrontendId(),
                                        dDescriptor.getDemuxId(),
                                        mMainDecoderDescriptorId);
                    }
                    if (currentLiveRoute == -1) {
                        currentLiveRoute = mLiveRouteIDATVMain;
                    }
                    // Set route for secondary decoder
                    if (mSecondaryDecoderDescriptorId != -1
                            && mHDMIOutputDescriptor != null) {
                        mLiveRouteIDATVSecondary = IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getBroadcastRouteControl()
                                .getLiveRoute(dvbAFrDescriptor.getFrontendId(),
                                        dDescriptor.getDemuxId(),
                                        mSecondaryDecoderDescriptorId);
                    }
                }
            }
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // ///////////// RETRIEVE INSTALL ROUTE ID ATV //////////////////////
        // //////////////////////////////////////////////////////////////////
        mInstallRouteIDAtv = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getInstallRoute(dvbAFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId());
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // ////////////// RETRIEVE INSTALL ROUTE ID TER /////////////////////
        // //////////////////////////////////////////////////////////////////
        mInstallRouteIDTer = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getInstallRoute(dvbTFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId());
        // //////////////////////////////////////////////////////////////////
        // //////////// RETRIEVE INSTALL ROUTE ID CAB ///////////////////////
        // //////////////////////////////////////////////////////////////////
        mInstallRouteIDCab = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getInstallRoute(dvbCFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId());
        // //////////////////////////////////////////////////////////////////
        // //////////// RETRIEVE INSTALL ROUTE ID SAT ///////////////////////
        // //////////////////////////////////////////////////////////////////
        mInstallRouteIDSat = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getInstallRoute(dvbSFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId());
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE IP INSTALL ROUTE ID
        // ////////////////////////
        // //////////////////////////////////////////////////////////////////
        mIPInstallRouteID = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getInstallRoute(ipFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId());
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE RECORD ROUTE ID ATV ////////////////////
        // //////////////////////////////////////////////////////////////////
        recRouteIDAtv = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getRecordRoute(dvbAFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId(),
                        mDescriptor.getMassStorageId());
        if (recRouteCurrent == -1) {
            recRouteCurrent = recRouteIDAtv;
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE RECORD ROUTE ID TER ////////////////////
        // //////////////////////////////////////////////////////////////////
        recRouteIDTer = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getRecordRoute(dvbTFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId(),
                        mDescriptor.getMassStorageId());
        if (recRouteCurrent == -1) {
            recRouteCurrent = recRouteIDTer;
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE RECORD ROUTE ID CAB ////////////////////
        // //////////////////////////////////////////////////////////////////
        recRouteIDCab = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getRecordRoute(dvbCFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId(),
                        mDescriptor.getMassStorageId());
        if (recRouteCurrent == -1) {
            recRouteCurrent = recRouteIDCab;
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE RECORD ROUTE ID SAT ////////////////////
        // //////////////////////////////////////////////////////////////////
        recRouteIDSat = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getRecordRoute(dvbSFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId(),
                        mDescriptor.getMassStorageId());
        if (recRouteCurrent == -1) {
            recRouteCurrent = recRouteIDSat;
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE RECORD ROUTE ID IP ////////////////////
        // //////////////////////////////////////////////////////////////////
        recRouteIDIP = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getRecordRoute(ipFrDescriptor.getFrontendId(),
                        dDescriptor.getDemuxId(),
                        mDescriptor.getMassStorageId());
        if (recRouteCurrent == -1) {
            recRouteCurrent = recRouteIDIP;
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // ///////////// RETRIEVE PLAY ROUTE IDs ////////////////////////////
        // //////////////////////////////////////////////////////////////////
        int demuxID = dDescriptor.getDemuxId();
        int decoderID = 0;
        if (mMainDecoderDescriptorId != -1) {
            decoderID = mMainDecoderDescriptorId;
        }
        int outputID = 0;
        if (mHDMIOutputDescriptor != null) {
            outputID = mHDMIOutputDescriptor.getInputOutputId();
        }
        mPlaybackRouteIDMain = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getPlaybackRoute(mDescriptor.getMassStorageId(), demuxID,
                        decoderID);
        if (mSecondaryDecoderDescriptorId != -1) {
            decoderID = mSecondaryDecoderDescriptorId;
        }
        if (mHDMIOutputDescriptor != null) {
            outputID = mHDMIOutputDescriptor.getInputOutputId();
        }
        mPlaybackRouteIDSecondary = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getBroadcastRouteControl()
                .getPlaybackRoute(mDescriptor.getMassStorageId(), demuxID,
                        decoderID);
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE IP ROUTE ID ////////////////////////////
        // //////////////////////////////////////////////////////////////////
        if (mMainDecoderDescriptorId != -1 && mHDMIOutputDescriptor != null) {
            mLiveRouteIDIPMain = IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getBroadcastRouteControl()
                    .getLiveRoute(ipFrDescriptor.getFrontendId(),
                            dDescriptor.getDemuxId(), mMainDecoderDescriptorId);
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////// RETRIEVE IO ROUTES //////////////////////////////
        // //////////////////////////////////////////////////////////////////
        ioDeviceCnt = IWEDIAService.getInstance().getDTVManager()
                .getCommonRouteControl().getInputOutputNumber();
        if (IWEDIAService.DEBUG) {
            Log.d(LOG_TAG, "setting io devices no[" + ioDeviceCnt + "]");
        }
        for (int i = 0; i < ioDeviceCnt; i++) {
            boolean isInput = IWEDIAService.getInstance().getDTVManager()
                    .getInputOutputControl().isDeviceInput(i);
            RouteInputOutputDescriptor device = new RouteInputOutputDescriptor();
            if (isInput) {
                device = IWEDIAService.getInstance().getDTVManager()
                        .getCommonRouteControl().getInputOutputDescriptor(i);
                mInputDeviceList.add(device);
                int key;
                switch (device.getInputOutputDeviceType()) {
                    case COMPONENT:
                    case HDMI:
                    case VGA:
                    case CVBS:
                        if (mMainDecoderDescriptorId != -1
                                && mHDMIOutputDescriptor != null) {
                            int route = IWEDIAService
                                    .getInstance()
                                    .getDTVManager()
                                    .getCommonRouteControl()
                                    .getInputRoute(device.getInputOutputId(),
                                            mMainDecoderDescriptorId);
                            key = calculateKey(device.getInputOutputId(),
                                    (int) mMainDecoderDescriptorId,
                                    device.getPort());
                            mInputRouteMap.put(key, route);
                        }
                        if (mSecondaryDecoderDescriptorId != -1
                                && mHDMIOutputDescriptor != null) {
                            int route = IWEDIAService
                                    .getInstance()
                                    .getDTVManager()
                                    .getCommonRouteControl()
                                    .getInputRoute(device.getInputOutputId(),
                                            mSecondaryDecoderDescriptorId);
                            key = calculateKey(device.getInputOutputId(),
                                    (int) mSecondaryDecoderDescriptorId,
                                    device.getPort());
                            mInputRouteMap.put(key, route);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        // //////////////////////////////////////////////////////////////////
        if (IWEDIAService.DEBUG) {
            Log.v(LOG_TAG, "Initialized routes:");
            Log.v(LOG_TAG, "===========================================");
            Log.v(LOG_TAG, "main live T routeID[" + mLiveRouteIDTerMain + "]");
            Log.v(LOG_TAG, "secondary live T routeID["
                    + mLiveRouteIDTerSecondary + "]");
            Log.v(LOG_TAG, "main live C routeID[" + mLiveRouteIDCabMain + "]");
            Log.v(LOG_TAG, "secondary live C routeID["
                    + mLiveRouteIDCabSecondary + "]");
            Log.v(LOG_TAG, "main live A routeID[" + mLiveRouteIDATVMain + "]");
            Log.v(LOG_TAG, "secondary live A routeID["
                    + mLiveRouteIDATVSecondary + "]");
            Log.v(LOG_TAG, "main live S routeID[" + mLiveRouteIDSatMain + "]");
            Log.v(LOG_TAG, "secondary live S routeID["
                    + mLiveRouteIDSatSecondary + "]");
            Log.v(LOG_TAG, "main playback routeID[" + mPlaybackRouteIDMain
                    + "]");
            Log.v(LOG_TAG, "secondary playback routeID["
                    + mPlaybackRouteIDSecondary + "]");
            Log.v(LOG_TAG, "install(scan) T routeID[" + mInstallRouteIDTer
                    + "]");
            Log.v(LOG_TAG, "install(scan) C routeID[" + mInstallRouteIDCab
                    + "]");
            Log.v(LOG_TAG, "install(scan) A routeID[" + mInstallRouteIDAtv
                    + "]");
            Log.v(LOG_TAG, "install(scan) IP routeID[" + mIPInstallRouteID
                    + "]");
            Log.v(LOG_TAG, "install(scan) S routeID[" + mInstallRouteIDSat
                    + "]");
            Log.v(LOG_TAG, "record T routeID[" + recRouteIDTer + "]");
            Log.v(LOG_TAG, "record C routeID[" + recRouteIDCab + "]");
            Log.v(LOG_TAG, "record A routeID[" + recRouteIDAtv + "]");
            Log.v(LOG_TAG, "record IP routeID[" + recRouteIDIP + "]");
            Log.v(LOG_TAG, "record S routeID[" + recRouteIDSat + "]");
            Log.v(LOG_TAG, "input devices count[" + mInputDeviceList.size()
                    + "]");
            Log.v(LOG_TAG, "input routes count[" + mInputRouteMap.size() + "]");
            for (int i = 0; i < mInputRouteMap.keySet().size(); i++) {
                int key = (Integer) mInputRouteMap.keySet().toArray()[i];
                Log.v(LOG_TAG, "input route key[" + key + "] val["
                        + mInputRouteMap.get(key) + "]");
            }
            Log.v(LOG_TAG, "===========================================");
        }
    }

    public int getDecoderID(int playbackDestination) {
        return mPlaybackDestinationMap.get(playbackDestination).getDecoderId();
    };

    public int getInstallRouteIDTer() {
        return mInstallRouteIDTer;
    }

    public int getRecRouteIDTer() {
        return recRouteIDTer;
    }

    public int getRecRouteIDCab() {
        return recRouteIDCab;
    }

    public int getInstallRouteIDAtv() {
        return mInstallRouteIDAtv;
    }

    public int getRecRouteIDAtv() {
        return recRouteIDAtv;
    }

    public int getRecRouteIDIP() {
        return recRouteIDIP;
    }

    public int getRecRouteIDSat() {
        return recRouteIDSat;
    }

    public int getCurrentRecRoute() {
        return recRouteCurrent;
    }

    public void setCurrentRecRoute(int recRouteCurrent) {
        this.recRouteCurrent = recRouteCurrent;
    }

    public int getLiveRouteIDTer(int decoderID) {
        if (decoderID == mMainDecoderDescriptorId) {
            return mLiveRouteIDTerMain;
        } else if (decoderID == mSecondaryDecoderDescriptorId) {
            return mLiveRouteIDTerSecondary;
        } else {
            return -1;
        }
    }

    public int getLiveRouteIDAtv(int decoderID) {
        if (decoderID == mMainDecoderDescriptorId) {
            return mLiveRouteIDATVMain;
        } else if (decoderID == mSecondaryDecoderDescriptorId) {
            return mLiveRouteIDATVSecondary;
        } else {
            return -1;
        }
    }

    public int getPlayRouteID(int decoderID) {
        if (decoderID == mMainDecoderDescriptorId) {
            return mPlaybackRouteIDMain;
        } else if (decoderID == mSecondaryDecoderDescriptorId) {
            return mPlaybackRouteIDSecondary;
        } else {
            return -1;
        }
    }

    public int getIpRouteID(int decoderID) {
        return mLiveRouteIDIPMain;
//        if (decoderID == mMainDecoderDescriptorId) {
//            return mLiveRouteIDIPMain;
//        } else if (decoderID == mSecondaryDecoderDescriptorId) {
//            return mLiveRouteIDIPSecondary;
//        } else {
//            return mLiveRouteIDIPMain;
//        }
    }

    public int getIpInstallRouteID() {
        return mIPInstallRouteID;
    }

    public RouteFrontendDescriptor getDvbTFrDescriptor() {
        return dvbTFrDescriptor;
    }

    public int getLiveRouteIDCab(int decoderID) {
        if (decoderID == mMainDecoderDescriptorId) {
            return mLiveRouteIDCabMain;
        } else if (decoderID == mSecondaryDecoderDescriptorId) {
            return mLiveRouteIDCabSecondary;
        } else {
            return -1;
        }
    }

    public int getInstallRouteIDCab() {
        return mInstallRouteIDCab;
    }

    public int getInstallRouteIDSat() {
        return mInstallRouteIDSat;
    }

    public int getLiveRouteIDSat(int decoderID) {
        if (decoderID == mMainDecoderDescriptorId) {
            return mLiveRouteIDSatMain;
        } else if (decoderID == mSecondaryDecoderDescriptorId) {
            return mLiveRouteIDSatSecondary;
        } else {
            return -1;
        }
    }

    public int getCurrentLiveRoute() {
        return currentLiveRoute;
    }

    public void setCurrentLiveRoute(int currentLiveRoute) {
        this.currentLiveRoute = currentLiveRoute;
    }

    public boolean isMainRoute(int routeID) {
        if (routeID != -1) {
            return (routeID == mLiveRouteIDATVMain
                    || routeID == mLiveRouteIDCabMain
                    || routeID == mLiveRouteIDIPMain
                    || routeID == mLiveRouteIDSatMain || routeID == mLiveRouteIDTerMain);
        }
        return false;
    }

    public RouteFrontendType getLiveRouteFEType(int routeID) {
        if (routeID == mLiveRouteIDATVMain
                || routeID == mLiveRouteIDATVSecondary) {
            return RouteFrontendType.ANALOG;
        } else if (routeID == mLiveRouteIDTerMain
                || routeID == mLiveRouteIDTerSecondary) {
            return RouteFrontendType.TER;
        } else if (routeID == mLiveRouteIDCabMain
                || routeID == mLiveRouteIDCabSecondary) {
            return RouteFrontendType.CAB;
        } else if (routeID == mLiveRouteIDSatMain
                || routeID == mLiveRouteIDSatSecondary) {
            return RouteFrontendType.SAT;
        } else if (routeID == mLiveRouteIDIPMain
                || routeID == mLiveRouteIDIPSecondary) {
            return RouteFrontendType.IP;
        }
        return RouteFrontendType.TER;
    }

    public int getInputDevicesCount() {
        return mInputDeviceList.size();
    }

    public ArrayList<RouteInputOutputDescriptor> getInputDevicesList() {
        return mInputDeviceList;
    }

    public int getInputRouteID(int deviceID, int decoderID) {
        // device id and device index are same
        int port = mInputDeviceList.get(deviceID).getPort();
        Integer route = mInputRouteMap.get(calculateKey(deviceID,
                (int) decoderID, port));
        return route.intValue();
    }

    public int getLiveRouteId(SourceType sourceType, int decoderId) {
        switch (sourceType) {
            case ANALOG:
                return getLiveRouteIDAtv(decoderId);
            case CAB:
                return getLiveRouteIDCab(decoderId);
            case IP:
                break;
            case SAT:
                return getLiveRouteIDSat(decoderId);
            case TER:
                return getLiveRouteIDTer(decoderId);
            default:
                break;
        }
        return -1;
    }
}
