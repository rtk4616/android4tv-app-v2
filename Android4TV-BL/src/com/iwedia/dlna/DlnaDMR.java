package com.iwedia.dlna;

import android.os.RemoteException;

import com.iwedia.dlna.dmr.service.DlnaDmrNativeService;

/* import com.iwedia.dlna.dmr.service.DlnaDmrNativeService; */
/**
 * DLNA renderer class.
 * 
 * @author maksovic
 */
public class DlnaDMR extends DlnaDevice {
    /**
     *
     */
    private boolean isStart = false;

    /**
     * Constructor.
     * 
     * @param udn
     *        Device UDN.
     * @param friendlyName
     *        Device friendly name.
     */
    public DlnaDMR(String udn, String friendlyName) {
        super(udn, friendlyName);
        setType(DlnaDeviceType.DLNA_RENDERER);
    }

    public DlnaDMR(DlnaDmrNativeService nativeDmrService, String dmrUDN,
            String parentID) {
        super(dmrUDN, parentID);
        if (nativeDmrService == null) {
            throw new IllegalArgumentException();
        }
        try {
            this.nativeHandle = nativeDmrService.getDlnaService().dmrCreate(
                    dmrUDN);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.nativeDmrService = nativeDmrService;
        devType = DlnaDeviceType.DLNA_RENDERER;
    }

    /**
     * Starts DLNA renderer.
     * 
     * @throws DlnaException
     */
    public void start() throws DlnaException {
        try {
            nativeDmrService.getDlnaService().dmrStart(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.isStart = true;
    }

    /**
     * Stop method. DMR will not be available on the network any more.
     * 
     * @throws DlnaException
     */
    public void stop() throws DlnaException {
        try {
            nativeDmrService.getDlnaService().dmrStop(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.isStart = false;
    }

    /**
     * State method. In witch state is DMR.
     * 
     * @return true if DMR is active or false otherwise
     */
    public boolean isStarted() {
        return this.isStart;
    }

    /**
     * Termination method. Used to release all resources, after object is not
     * required any more. This method <b>MUST</b> be called if object was
     * successfully created!
     */
    public void terminate() {
        try {
            nativeDmrService.getDlnaService().dmrDestroy(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void dlnaSetNotify(int notification, int notificationValue,
            String notifyPosition) throws DlnaException {
        try {
            nativeDmrService.getDlnaService().dmrNotify(nativeHandle,
                    notification, notificationValue, notifyPosition);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
