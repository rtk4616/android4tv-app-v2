package com.iwedia.dlna;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.dlna.dmc.service.DlnaDmcContainer;
import com.iwedia.dlna.dmc.service.DlnaDmcObject;
import com.iwedia.dlna.dmc.service.DlnaDmcServer;
import com.iwedia.dlna.dmc.service.DlnaDmcService;

/**
 * DLNA DMP class, which contains all player functionalities.
 * 
 * @author maksovic
 */
public class DlnaDMP {
    static final String LOG_TAG = "DlnaDigitalMediaPlayer";
    /**
     * Native handler.
     */
    protected long nativeHandle;
    /**
     * Native service.
     */
    protected DlnaDmcService nativeService;

    /**
     * Simple constructor which does nothing special.
     */
    protected DlnaDMP() {
        nativeHandle = 0;
    }

    public DlnaDMP(DlnaDmcService nativeService) throws DlnaException {
        if (nativeService == null) {
            throw new IllegalArgumentException();
        }
        try {
            Log.d(LOG_TAG,
                    "Calling nativeService.getDlnaService().dmcCreate(0)");
            this.nativeHandle = nativeService.getDlnaService().dmcCreate(0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.nativeService = nativeService;
    }

    /**
     * Startup method (search for devices is done).
     * 
     * @throws DlnaException
     */
    public void start() throws DlnaException {
        Log.d(LOG_TAG,
                "Calling nativeService.getDlnaService().dmcStart(nativeHandle)");
        try {
            nativeService.getDlnaService().dmcStart(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop method (devices are removed from the list).
     * 
     * @throws DlnaException
     */
    public void stop() throws DlnaException {
        Log.d(LOG_TAG,
                "Calling nativeService.getDlnaService().dmcStop(nativeHandle)");
        try {
            nativeService.getDlnaService().dmcStop(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Termination method. Used to release all resources, after object is not
     * required any more. This method <b>MUST</b> be called if object was
     * successfully created!
     */
    public void terminate() {
        try {
            nativeService.getDlnaService().dmcDestroy(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void refresh() throws DlnaException {
        try {
            nativeService.getDlnaService().dmcRescan(nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns found DMS devices count.
     * 
     * @return device count.
     * @throws DlnaException
     */
    public int getServerCount() throws DlnaException {
        try {
            return nativeService.getDlnaService().dmcGetServerCount(
                    nativeHandle);
        } catch (RemoteException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Returns DMS device by index.
     * 
     * @param index
     *        Device list index.
     * @return The device.
     * @throws DlnaException
     */
    public DlnaDMS getServer(int index) throws DlnaException {
        DlnaDMS ret;
        try {
            DlnaDmcServer nativeRet = new DlnaDmcServer();
            nativeService.getDlnaService().dmcGetServerByIndex(nativeHandle,
                    index, nativeRet);
            DlnaDMS serverDevice = new DlnaDMS(nativeRet.getDmcUdnValue(),
                    nativeRet.getDmcFriendlyName(), nativeRet.getDmcADDRValue());
            ret = serverDevice;
        } catch (RemoteException e) {
            e.printStackTrace();
            ret = new DlnaDMS();
            return ret;
        }
        ret.setIsRemote(true);
        return ret;
    }

    /**
     * Returns DMS device by UDN.
     * 
     * @param udn
     *        Device UDN.
     * @return The device.
     * @throws DlnaException
     */
    public DlnaDMS getServer(String udn) throws DlnaException {
        DlnaDMS ret;
        try {
            DlnaDmcServer nativeRet = new DlnaDmcServer();
            nativeService.getDlnaService().dmcGetServerByUDN(nativeHandle, udn,
                    nativeRet);
            DlnaDMS serverDevice = new DlnaDMS(nativeRet.getDmcUdnValue(),
                    nativeRet.getDmcFriendlyName(), nativeRet.getDmcADDRValue());
            ret = serverDevice;
        } catch (RemoteException e) {
            e.printStackTrace();
            ret = new DlnaDMS();
            return ret;
        }
        ret.setIsRemote(true);
        return ret;
    }

    /**
     * Opens container (prepares it for browsing).
     * 
     * @param id
     *        Container ID.
     * @return Created container object.
     * @throws DlnaException
     */
    public DlnaContainer openContainer(String id) throws DlnaException {
        DlnaContainer ret;
        try {
            DlnaDmcContainer nativeCont = new DlnaDmcContainer();
            nativeService.getDlnaService().dmcOpenDir(nativeHandle, id,
                    nativeCont);
            DlnaContainer container = new DlnaContainer(nativeCont.getID(),
                    nativeCont.getName(), nativeCont.getParentID());
            container.setChildCount(nativeCont.getCount());
            container.setNativeHandle(nativeCont.getHandle());
            ret = container;
        } catch (RemoteException e) {
            e.printStackTrace();
            ret = new DlnaContainer(null, null, null);
            return ret;
        }
        return ret;
    }

    /**
     * Convenience method, used if user already has valid container which should
     * be reused. Opened container <b>MUST</b> be closed to release all
     * resources @see closeContainer
     * 
     * @param container
     *        Directory to open.
     * @throws DlnaException
     */
    public void openContainer(DlnaContainer container) throws DlnaException {
        DlnaContainer ret;
        try {
            DlnaDmcContainer nativeCont = new DlnaDmcContainer();
            nativeService.getDlnaService().dmcOpenDir(nativeHandle,
                    container.id, nativeCont);
            DlnaContainer tmpContainer = new DlnaContainer(nativeCont.getID(),
                    nativeCont.getName(), nativeCont.getParentID());
            tmpContainer.setChildCount(nativeCont.getCount());
            tmpContainer.setNativeHandle(nativeCont.getHandle());
            ret = tmpContainer;
        } catch (RemoteException e) {
            e.printStackTrace();
            return;
        }
        container.setChildCount(ret.getChildCount());
        container.setNativeHandle(ret.getNativeHandle());
    }

    /**
     * Reads next child object in the passed directory.
     * 
     * @param container
     *        Container to read.
     * @return Retrieved object.
     * @throws DlnaException
     */
    public DlnaObject readContainer(DlnaContainer container)
            throws DlnaException {
        try {
            DlnaDmcObject nativeObject = new DlnaDmcObject();
            nativeService.getDlnaService().dmcReadDir(nativeHandle,
                    container.getNativeHandle(), nativeObject);
            DlnaObject tmp = new DlnaObject(nativeObject.getDlnaDmcObjectID(),
                    nativeObject.getDlnaDmcObjectFriendlyName(),
                    nativeObject.getDlnaDmcObjectParentID());
            switch (nativeObject.getDlnaDmcObjectType()) {
                case 1: {// container
                    DlnaContainer tmpCont = new DlnaContainer(tmp.getID(),
                            tmp.getFriendlyName(), tmp.getID());
                    tmpCont.setParent(container);
                    return tmpCont;
                }
                case 2: {// image item
                    DlnaPictureItem tmpImageItem = new DlnaPictureItem(
                            tmp.getID(), tmp.getFriendlyName(), tmp.getID());
                    tmpImageItem.setResolution(
                            nativeObject.getDlnaDmcObjectWidth(),
                            nativeObject.getDlnaDmcObjectHeight());
                    tmpImageItem.setThumbnailURI(nativeObject
                            .getDlnaDmcObjectThumbUri());
                    tmpImageItem.setURI(nativeObject.getDlnaDmcObjectUri());
                    tmpImageItem.setMime(nativeObject.getDlnaDmcObjectMime());
                    tmpImageItem.setParent(container);
                    return tmpImageItem;
                }
                case 3: {// audio item
                    DlnaAudioItem tmpAudioItem = new DlnaAudioItem(tmp.getID(),
                            tmp.getFriendlyName(), tmp.getID());
                    tmpAudioItem.setURI(nativeObject.getDlnaDmcObjectUri());
                    tmpAudioItem.setMime(nativeObject.getDlnaDmcObjectMime());
                    tmpAudioItem.setDuration(nativeObject
                            .getDlnaDmcObjectDuration());
                    tmpAudioItem.setAlbumArtURI(nativeObject
                            .getDlnaDmcObjectAlbum());
                    tmpAudioItem.setBitRate(nativeObject
                            .getDlnaDmcObjectBitRate());
                    tmpAudioItem.setSamplingRate(nativeObject
                            .getDlnaDmcObjectSamplingRate());
                    tmpAudioItem.setNumChannels(nativeObject
                            .getDlnaDmcObjectNumChannels());
                    tmpAudioItem.setParent(container);
                    return tmpAudioItem;
                }
                case 4: {// video item
                    Log.d(LOG_TAG, "Video new item");
                    DlnaVideoItem tmpVideoItem = new DlnaVideoItem(tmp.getID(),
                            tmp.getFriendlyName(), tmp.getID());
                    tmpVideoItem.setResolution(
                            nativeObject.getDlnaDmcObjectWidth(),
                            nativeObject.getDlnaDmcObjectHeight());
                    tmpVideoItem.setDuration(nativeObject
                            .getDlnaDmcObjectDuration());
                    tmpVideoItem.setURI(nativeObject.getDlnaDmcObjectUri());
                    tmpVideoItem.setMime(nativeObject.getDlnaDmcObjectMime());
                    tmpVideoItem.setParent(container);
                    return tmpVideoItem;
                }
                default: { // Unknown object
                    return tmp;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            DlnaObject ret = new DlnaObject();
            return ret;
        }
    }

    /**
     * Convenience method, used for full directory browse. Container <b>MUST</b>
     * be already opened.
     * 
     * @param container
     *        Container to browse.
     * @return Browsed array.
     * @throws DlnaException
     */
    public DlnaObject[] browseContainer(DlnaContainer container)
            throws DlnaException {
        DlnaObject[] ret = new DlnaObject[container.getChildCount()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = readContainer(container);
        }
        return ret;
    }

    /**
     * Closes directory and releases all related resources.
     * 
     * @param container
     *        Container to close.
     * @throws DlnaException
     */
    public void closeContainer(DlnaContainer container) throws DlnaException {
        try {
            nativeService.getDlnaService().dmcCloseDir(nativeHandle,
                    container.getNativeHandle());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        container.setNativeHandle(0);
    }
}
