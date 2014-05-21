package com.iwedia.service.proxyservice;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IActionControl;
import com.iwedia.comm.IAudioControl;
import com.iwedia.comm.ICIControl;
import com.iwedia.comm.ICallbacksControl;
import com.iwedia.comm.IDTVManagerProxy;
import com.iwedia.comm.IDisplayControl;
import com.iwedia.comm.IDlnaControl;
import com.iwedia.comm.IEpgControl;
import com.iwedia.comm.IHbbTvControl;
import com.iwedia.comm.IInputOutputControl;
import com.iwedia.comm.IMhegControl;
import com.iwedia.comm.IOnDemandControl;
import com.iwedia.comm.IParentalControl;
import com.iwedia.comm.IPvrControl;
import com.iwedia.comm.IReminderControl;
import com.iwedia.comm.IScanControl;
import com.iwedia.comm.IServiceControl;
import com.iwedia.comm.IServiceMode;
import com.iwedia.comm.ISetupControl;
import com.iwedia.comm.IStreamComponentControl;
import com.iwedia.comm.ISubtitleControl;
import com.iwedia.comm.ISystemControl;
import com.iwedia.comm.ITeletextControl;
import com.iwedia.comm.IVideoControl;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.enums.PlaybackDestinationType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dlna.DlnaControl;
import com.iwedia.dtv.route.broadcast.RouteFrontendType;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.action.ActionControl;
import com.iwedia.service.audio.AudioControl;
import com.iwedia.service.callback.CallbacksControl;
import com.iwedia.service.ci.CIControl;
import com.iwedia.service.content.ContentListControl;
import com.iwedia.service.display.DisplayControl;
import com.iwedia.service.epg.EpgControl;
import com.iwedia.service.hbb.HbbTvControl;
import com.iwedia.service.io.InputOutputControl;
import com.iwedia.service.mheg.MhegControl;
import com.iwedia.service.parental.ParentalControl;
import com.iwedia.service.pvr.PvrControl;
import com.iwedia.service.reminder.ReminderControl;
import com.iwedia.service.route.RouteManager;
import com.iwedia.service.scan.ScanControl;
import com.iwedia.service.service.ServiceControl;
import com.iwedia.service.servicemode.ServiceMode;
import com.iwedia.service.setup.SetupControl;
import com.iwedia.service.streamcomponent.StreamComponentControl;
import com.iwedia.service.subtitle.SubtitleControl;
import com.iwedia.service.system.SystemControl;
import com.iwedia.service.teletext.TeletextControl;
import com.iwedia.service.video.VideoControl;
import com.iwedia.service.vod.VideoOnDemandControl;

public class DTVManagerProxy extends IDTVManagerProxy.Stub {
    private final String LOG_TAG = "DTVManagerProxy";
    private DTVInterfaceManager dtvInterfaceManager;
    private IAudioControl mAudioControl;
    private ICIControl mCIControl;
    private IEpgControl mEpgControl;
    private IHbbTvControl mHbbTvControl;
    private IScanControl mScanControl;
    private IMhegControl mMhegControl;
    private IPvrControl mPvrControl;
    private IReminderControl mReminderControl;
    private ISubtitleControl mSubtitleControl;
    private IVideoControl mVideoControl;
    private ITeletextControl mTeletextControl;
    private IParentalControl mParentalControl;
    private ICallbacksControl mCallbacksControl;
    private IActionControl mActionControl;
    private IContentListControl mContentListControl;
    private ISystemControl mSystemControl;
    private IDlnaControl mDlnaControl;
    private IOnDemandControl mVideoDemandControl;
    private ISetupControl mSetupControl;
    private IInputOutputControl mInputOutputControl;
    private IServiceMode mServiceMode;
    private IDisplayControl mDisplayControl;
    private IStreamComponentControl mStreamComponentControl;
    private RouteManager mRouteManager;
    private IServiceControl mServiceControl;

    public DTVManagerProxy() {
        dtvInterfaceManager = new DTVInterfaceManager();
        mRouteManager = new RouteManager();
    }

    public void initialize() {
        /**
         * Initialize routeIds.
         */
        mRouteManager.initialize();
        try {
            ((ContentListControl) getContentListControl()).initialize();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IAudioControl getAudioControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getAudioControl");
        }
        if (mAudioControl == null) {
            mAudioControl = new AudioControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.AUDIO_CONTROL,
                    (IDTVInterface) mAudioControl);
        }
        return mAudioControl;
    }

    @Override
    public ICIControl getCIControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getCIControl");
        }
        if (mCIControl == null) {
            mCIControl = new CIControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.COMMON_INTERFACE_CONTROL,
                    (IDTVInterface) mCIControl);
        }
        return mCIControl;
    }

    @Override
    public IEpgControl getEpgControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getEpgControl");
        }
        if (mEpgControl == null) {
            mEpgControl = new EpgControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.EPG_CONTROL,
                    (IDTVInterface) mEpgControl);
        }
        return mEpgControl;
    }

    @Override
    public IHbbTvControl getHbbTvControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getHbbControl");
        }
        if (mHbbTvControl == null) {
            mHbbTvControl = new HbbTvControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.HBBC_ONTROL,
                    (IDTVInterface) mHbbTvControl);
        }
        return mHbbTvControl;
    }

    @Override
    public IScanControl getScanControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getScanControl");
        }
        if (mScanControl == null) {
            mScanControl = new ScanControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.SCAN_CONTROL,
                    (IDTVInterface) mScanControl);
        }
        return mScanControl;
    }

    @Override
    public IMhegControl getMhegControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getMhegControl");
        }
        if (mMhegControl == null) {
            mMhegControl = new MhegControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.MHEG_CONTROL,
                    (IDTVInterface) mMhegControl);
        }
        return mMhegControl;
    }

    @Override
    public IPvrControl getPvrControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getPvrControl");
        }
        if (mPvrControl == null) {
            mPvrControl = new PvrControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.PVR_CONTROL,
                    (IDTVInterface) mPvrControl);
        }
        return mPvrControl;
    }

    @Override
    public IReminderControl getReminderControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getReminderControl");
        }
        if (mReminderControl == null) {
            mReminderControl = new ReminderControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.REMINDER_CONTROL,
                    (IDTVInterface) mReminderControl);
        }
        return mReminderControl;
    }

    @Override
    public IServiceControl getServiceControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getServiceListControl");
        }
        if (mServiceControl == null) {
            mServiceControl = new ServiceControl("Primary service list",
                    ServiceListIndex.MASTER_LIST);
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.SERVICELIST__CONTROL,
                    (IDTVInterface) mServiceControl);
        }
        return mServiceControl;
    }

    @Override
    public ISubtitleControl getSubtitleControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getSubtitleControl");
        }
        if (mSubtitleControl == null) {
            mSubtitleControl = new SubtitleControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.SUBTITLE_CONTROL,
                    (IDTVInterface) mSubtitleControl);
        }
        return mSubtitleControl;
    }

    @Override
    public IVideoControl getVideoControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getVideoControl");
        }
        if (mVideoControl == null) {
            mVideoControl = new VideoControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.VIDEO_CONTROl,
                    (IDTVInterface) mVideoControl);
        }
        return mVideoControl;
    }

    @Override
    public ITeletextControl getTeletextControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getTeletextControl");
        }
        if (mTeletextControl == null) {
            mTeletextControl = new TeletextControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.TELETEXT_CONTROL,
                    (IDTVInterface) mTeletextControl);
        }
        return mTeletextControl;
    }

    @Override
    public IParentalControl getParentalControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getParentalControl");
        }
        if (mParentalControl == null) {
            mParentalControl = new ParentalControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.PARENTAL_CONTROL,
                    (IDTVInterface) mParentalControl);
        }
        return mParentalControl;
    }

    @Override
    public ICallbacksControl getCallbacksControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getCallbacksControl");
        }
        if (mCallbacksControl == null) {
            mCallbacksControl = new CallbacksControl();
        }
        return mCallbacksControl;
    }

    @Override
    public IActionControl getActionControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getActionControl");
        }
        if (mActionControl == null) {
            mActionControl = new ActionControl();
        }
        return mActionControl;
    }

    @Override
    public IContentListControl getContentListControl() throws RemoteException {
        if (mContentListControl == null) {
            mContentListControl = new ContentListControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.CONTENTLIST_CONTROL,
                    (IDTVInterface) mContentListControl);
        }
        return mContentListControl;
    }

    @Override
    public ISystemControl getSystemControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getSystemControl");
        }
        if (mSystemControl == null) {
            mSystemControl = new SystemControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.SYSTEM_CONTROL,
                    (IDTVInterface) mSystemControl);
        }
        return mSystemControl;
    }

    @Override
    public IDlnaControl getDlnaControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getDlnaControl");
        }
        if (mDlnaControl == null) {
            mDlnaControl = new DlnaControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.DLNA_CONTROL,
                    (IDTVInterface) mDlnaControl);
        }
        return mDlnaControl;
    }

    @Override
    public IOnDemandControl getVideoOnDemandControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getVideoOnDemandControl");
        }
        if (mVideoDemandControl == null) {
            mVideoDemandControl = new VideoOnDemandControl();
            dtvInterfaceManager.addDTVInterface(
                    DTVInterfaceKeys.ONDEMAND_CONTROL,
                    (IDTVInterface) mVideoDemandControl);
        }
        return mVideoDemandControl;
    }

    @Override
    public ISetupControl getSetupControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getSetupControl");
        }
        if (mSetupControl == null) {
            mSetupControl = new SetupControl();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.SETUP_CONTROL,
                    (IDTVInterface) mSetupControl);
        }
        return mSetupControl;
    }

    @Override
    public IInputOutputControl getInputOutputControl() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getInputOutputControl");
        }
        if (mInputOutputControl == null) {
            mInputOutputControl = InputOutputControl.getInstance();
        }
        return mInputOutputControl;
    }

    @Override
    public IServiceMode getServiceMode() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getServicedMode");
        }
        if (mServiceMode == null) {
            mServiceMode = new ServiceMode();
            dtvInterfaceManager.addDTVInterface(DTVInterfaceKeys.SERVICE_MODE,
                    (IDTVInterface) mServiceMode);
        }
        return mServiceMode;
    }

    @Override
    public IDisplayControl getDisplayControl() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getDisplayControl");
        }
        if (mDisplayControl == null) {
            mDisplayControl = new DisplayControl();
        }
        Log.d(LOG_TAG, "getDisplayControl: mDisplayControl=" + mDisplayControl);
        return mDisplayControl;
    }

    @Override
    public IStreamComponentControl getStreamComponentControl()
            throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getStreamComponentControl");
        }
        if (mStreamComponentControl == null) {
            mStreamComponentControl = new StreamComponentControl();
        }
        Log.d(LOG_TAG, "getStreamComponentControl: mStreamComponentControl="
                + mStreamComponentControl);
        return mStreamComponentControl;
    }

    public RouteManager getRouteManager() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getRouteManager");
        }
        return mRouteManager;
    }

    public int getDecoderID(int playbackDestination) {
        return mRouteManager.getDecoderID(playbackDestination);
    };

    /**
     * Returns install routeId.
     * 
     * @return install routeId;
     */
    public int getInstallRouteIDTer() {
        return mRouteManager.getInstallRouteIDTer();
    }

    /**
     * Returns install routeId.
     * 
     * @return install routeId;
     */
    public int getInstallRouteIDAtv() {
        return mRouteManager.getInstallRouteIDAtv();
    }

    /**
     * Returns IP install route id.
     * 
     * @return IP install route id.
     */
    public int getIpInstallRouteID() {
        return mRouteManager.getIpInstallRouteID();
    }

    public int getInstallRouteIDCab() {
        return mRouteManager.getInstallRouteIDCab();
    }

    public int getInstallRouteIDSat() {
        return mRouteManager.getInstallRouteIDSat();
    }

    public int getRecRouteIDTer() {
        return mRouteManager.getRecRouteIDTer();
    }

    public int getRecRouteIDCab() {
        return mRouteManager.getRecRouteIDCab();
    }

    public int getRecRouteIDAtv() {
        return mRouteManager.getRecRouteIDAtv();
    }

    public int getRecRouteIDIP() {
        return mRouteManager.getRecRouteIDIP();
    }

    public int getRecRouteIDSat() {
        return mRouteManager.getRecRouteIDSat();
    }

    public int getCurrentRecRoute() {
        return mRouteManager.getCurrentRecRoute();
    }

    public void setCurrentRecRoute(int recRouteCurrent) {
        mRouteManager.setCurrentRecRoute(recRouteCurrent);
    }

    /**
     * Returns live routeId.
     * 
     * @return live routeId depending on decoder;
     */
    public int getLiveRouteIDTer(int decoderID) {
        return mRouteManager.getLiveRouteIDTer(decoderID);
    }

    /**
     * Returns live routeId.
     * 
     * @return live routeId depending on decoder;
     */
    public int getLiveRouteIDAtv(int decoderID) {
        return mRouteManager.getLiveRouteIDAtv(decoderID);
    }

    /**
     * Returns playback routeId.
     * 
     * @return playback routeId;
     */
    public int getPlayRouteID() {
        int decoderID = mRouteManager
                .getDecoderID(PlaybackDestinationType.MAIN_LIVE);
        return mRouteManager.getPlayRouteID(decoderID);
    }

    /**
     * Returns playback routeId.
     * 
     * @return playback routeId depending on decoder;
     */
    public int getPlayRouteID(int decoderID) {
        return mRouteManager.getPlayRouteID(decoderID);
    }

    public int getIpRouteID(int decoderID) {
        return mRouteManager.getIpRouteID(decoderID);
    }

    /**
     * Returns live routeId.
     * 
     * @return live routeId depending on decoder;
     */
    public int getLiveRouteIDCab(int decoderID) {
        return mRouteManager.getLiveRouteIDCab(decoderID);
    }

    /**
     * Returns live routeId.
     * 
     * @return live routeId depending on decoder;
     */
    public int getLiveRouteIDSat(int decoderID) {
        return mRouteManager.getLiveRouteIDSat(decoderID);
    }

    public int getCurrentLiveRoute() {
        return mRouteManager.getCurrentLiveRoute();
    }

    public void setCurrentLiveRoute(int currentLiveRoute) {
        mRouteManager.setCurrentLiveRoute(currentLiveRoute);
    }

    public boolean isMainRoute(int routeID) {
        return mRouteManager.isMainRoute(routeID);
    }

    public RouteFrontendType getLiveRouteFEType(int routeID) {
        return mRouteManager.getLiveRouteFEType(routeID);
    }

    /**
     * Returns interface manager.
     * 
     * @return {@link DTVManagerProxy}.
     */
    public DTVInterfaceManager getDTVInterfaceManager() {
        return dtvInterfaceManager;
    }
}
