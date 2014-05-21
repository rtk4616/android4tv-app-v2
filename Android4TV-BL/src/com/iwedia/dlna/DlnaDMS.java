package com.iwedia.dlna;

import android.os.RemoteException;

import com.iwedia.dlna.dms.service.DlnaNativeService;

// import android.util.Log;
/**
 * DLNA server class.
 * 
 * @author maksovic
 */
public class DlnaDMS extends DlnaDevice {
    /**
     * Root directory.
     */
    private DlnaContainer root;
    private boolean isStart = false;

    /**
     * Constructor.
     * 
     * @param udn
     *        Device UDN.
     * @param friendlyName
     *        Device friendly name.
     * @param addr
     *        Device address (if unknown, pass <code>null</code>)
     */
    public DlnaDMS() {
        super(null, null, null);
    }

    public DlnaDMS(String udn, String friendlyName, String parentID) {
        super(udn, friendlyName, parentID);
        setType(DlnaDeviceType.DLNA_SERVER);
        root = new DlnaContainer(id, friendlyName, parentID);
        root.setParent(null);
    }

    public DlnaDMS(DlnaNativeService nativeService, String udn, String dbPath,
            String parentID) throws DlnaException {
        super(udn, parentID);
        if (nativeService == null) {
            throw new IllegalArgumentException();
        }
        try {
            this.nativeHandle = nativeService.getDlnaService().dmsCreate(
                    dbPath, udn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.nativeService = nativeService;
        devType = DlnaDeviceType.DLNA_SERVER;
    }

    /**
     * Startup method (search for devices is done).
     * 
     * @throws DlnaException
     */
    public void start(int dtcp_ip) throws DlnaException {
        try {
            nativeService.getDlnaService().dmsStart(nativeHandle, dtcp_ip);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.isStart = true;
    }

    /**
     * Stop method (devices are removed from the list).
     * 
     * @throws DlnaException
     */
    public void stop() throws DlnaException {
        try {
            nativeService.getDlnaService().dmsStop(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.isStart = false;
    }

    /**
     * Shares directory with given path. Shared directory will be scanned for
     * media files, and all detected media files will be available on the
     * network for DLNA clients.
     * 
     * @param path
     *        Absolute file system path to the directory to share.
     * @throws DlnaException
     */
    public void share(String path) throws DlnaException {
        checkRemote();
        try {
            nativeService.getDlnaService().dmsShare(nativeHandle, path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes directory from share point list. All media files from this
     * directory will be removed from the server.
     * 
     * @param path
     * @throws DlnaException
     */
    public void unshare(String path) throws DlnaException {
        checkRemote();
        try {
            nativeService.getDlnaService().dmsUnshare(nativeHandle, path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * State method. in witch state is DMS.
     * 
     * @return true if DMS is active or false otherwise
     */
    public boolean isStarted() {
        return this.isStart;
    }

    public void setDtcpPort(int dtcp_port) throws DlnaException {
        checkRemote();
        try {
            nativeService.getDlnaService().dmsSetDtcpPort(nativeHandle,
                    dtcp_port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sharef(String file, int dtcp_port) throws DlnaException {
        checkRemote();
        try {
            nativeService.getDlnaService().dmsSharef(nativeHandle, file,
                    dtcp_port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unsharef(String file) throws DlnaException {
        checkRemote();
        try {
            nativeService.getDlnaService().dmsUnsharef(nativeHandle, file);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Share external streaming.
     * 
     * @param param
     *        all information about external streaming file.
     * @throws DlnaException
     */
    public void shareStreamExt(DlnaStreamParam param) throws DlnaException {
        /*
         * checkRemote(); try{ Log.i("DlnaDMS",
         * "param.getDtcp_host(): "+param.getDtcp_host()); Log.i("DlnaDMS",
         * "param.getMime(): "+param.getMime()); Log.i("DlnaDMS",
         * "param.getPort(): "+param.getPort()); Log.i("DlnaDMS",
         * "param.getProfile(): "+param.getProfile()); Log.i("DlnaDMS",
         * "param.getUri(): "+param.getUri());
         * nativeService.getDlnaService().dmsShareStreamExt(nativeHandle,
         * param); }catch (RemoteException e){ e.printStackTrace(); }
         */
    }

    /**
     * Unshare external streaming.
     * 
     * @param param
     *        all information about external streaming file.
     * @throws DlnaException
     */
    public void unshareStreamExt(DlnaStreamParam param) throws DlnaException {
        /*
         * checkRemote(); try{
         * nativeService.getDlnaService().dmsUnshareStreamExt(nativeHandle,
         * param); }catch (RemoteException e){ e.printStackTrace(); }
         */
    }

    /**
     * Termination method. Used to release all resources, after object is not
     * required any more. This method <b>MUST</b> be called if object was
     * successfully created!
     */
    public void terminate() {
        try {
            nativeService.getDlnaService().dmsDestroy(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns server root directory.
     * 
     * @return Root directory.
     */
    public DlnaContainer getRoot() {
        return root;
    }
}