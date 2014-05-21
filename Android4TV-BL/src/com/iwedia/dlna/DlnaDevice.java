package com.iwedia.dlna;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.dlna.dmr.service.DlnaDmrNativeService;
import com.iwedia.dlna.dms.service.DlnaNativeService;

/*
 * import com.iwedia.dlna.dms.service.DlnaNativeService; import
 * com.iwedia.dlna.dmr.service.DlnaDmrNativeService;
 */
/**
 * DLNA device class.
 * 
 * @author maksovic
 */
public class DlnaDevice extends DlnaObject {
    private static final String LOG_TAG = "DlnaDevice";
    /**
     * Device icon URI.
     */
    protected String iconURI;
    /**
     * Device type.
     */
    protected DlnaDeviceType devType;
    /**
     * Device address.
     */
    protected InetAddress addr;
    /**
     * Remote flag. If this is remote device, it will
     */
    protected boolean isRemote;
    protected long nativeHandle;
    protected DlnaNativeService nativeService;
    protected DlnaDmrNativeService nativeDmrService;

    /**
     * Constructor.
     * 
     * @param udn
     *        Device UDN.
     * @param friendlyName
     *        Device Friendly name.
     * @param addr
     *        Device address (if unknown, pass <code>null</code>).
     */
    public DlnaDevice(String udn, String friendlyName, String parentID) {
        super(udn, friendlyName, parentID);
        iconURI = null;
        devType = DlnaDeviceType.DLNA_UNKNOWN;
    }

    public DlnaDevice(String udn, String parentID) {
        super(udn, "", parentID);
    }

    /**
     * Sets device type.
     * 
     * @param type
     *        One of types defined by DlnaDeviceType.
     */
    protected void setType(DlnaDeviceType type) {
        devType = type;
    }

    /**
     * Sets icon URI.
     * 
     * @param uri
     *        Device icon URI.
     */
    public void setIconURI(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        iconURI = uri;
    }

    /**
     * Returns icon URI.
     * 
     * @return Device icon URI.
     */
    public String getIconURI() {
        return iconURI;
    }

    /**
     * Sets device IP address. This has effect only if this is remote device and
     * we figured out it address.
     * 
     * @param addr
     *        Device IP address.
     */
    public void setAddress(String addr) {
        if (addr != null) {
            try {
                this.addr = InetAddress.getByName(addr);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns IP address of the device.
     * 
     * @return IP address (can be <code>null</code> if unknown).
     */
    public InetAddress getAddress() {
        return addr;
    }

    /**
     * Sets remote flag.
     * 
     * @param isRemote
     *        <code>true</code> for remote device, or <code>false</code> for
     *        local device.
     */
    void setIsRemote(boolean isRemote) {
        this.isRemote = isRemote;
    }

    /**
     * Checks if the device is remote device, and if it is exception is thrown.
     * 
     * @throws DlnaException
     */
    protected void checkRemote() throws DlnaException {
        if (isRemote == true) {
            throw new DlnaException("REMOTE_DEVICE");
        }
    }

    /**
     * Checks if the device is local device, and if it is exception is thrown.
     * 
     * @throws DlnaException
     */
    protected void checkLocal() throws DlnaException {
        if (isRemote == false) {
            throw new DlnaException("LOCAL_DEVICE");
        }
    }

    /**
     * Sets device friendly name.
     * 
     * @param frandlyName
     *        Device friendly name
     * @throws DlnaException
     */
    public void setFrandlyName(String friendlyName, String device)
            throws DlnaException {
        checkRemote();
        if (friendlyName == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                if (nativeService != null)
                    nativeService.getDlnaService().devSetFriendlyName(
                            nativeHandle, friendlyName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                nativeDmrService.getDlnaService().devSetFriendlyName(
                        nativeHandle, friendlyName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }

    /**
     * Sets device manufacturer name.
     * 
     * @param manufacturerName
     *        Device manufacturer name
     * @throws DlnaException
     */
    public void setManufacturerName(String manufacturerName, String device)
            throws DlnaException {
        checkRemote();
        if (manufacturerName == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                nativeService.getDlnaService().devSetManufacturerName(
                        nativeHandle, manufacturerName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                if (nativeDmrService != null)
                    nativeDmrService.getDlnaService().devSetManufacturerName(
                            nativeHandle, manufacturerName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }

    /**
     * Sets device manufacturer url.
     * 
     * @param manufacturerURL
     *        Device manufacturer url
     * @throws DlnaException
     */
    public void setManufacturerURL(String manufacturerURL, String device)
            throws DlnaException {
        checkRemote();
        if (manufacturerURL == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                nativeService.getDlnaService().devSetManufacturerURL(
                        nativeHandle, manufacturerURL);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                nativeDmrService.getDlnaService().devSetManufacturerURL(
                        nativeHandle, manufacturerURL);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }

    /**
     * Sets device model name.
     * 
     * @param modelName
     *        Device model name
     * @throws DlnaException
     */
    public void setModelName(String modelName, String device)
            throws DlnaException {
        checkRemote();
        if (modelName == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                nativeService.getDlnaService().devSetModelName(nativeHandle,
                        modelName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                nativeDmrService.getDlnaService().devSetModelName(nativeHandle,
                        modelName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }

    /**
     * Sets device model description.
     * 
     * @param modelDesc
     *        Device model description
     * @throws DlnaException
     */
    public void setModelDesc(String modelDesc, String device)
            throws DlnaException {
        checkRemote();
        if (modelDesc == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                nativeService.getDlnaService().devSetModelDesc(nativeHandle,
                        modelDesc);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                nativeDmrService.getDlnaService().devSetModelDesc(nativeHandle,
                        modelDesc);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }

    /**
     * Sets device model number.
     * 
     * @param modelNumber
     *        Device model number
     * @throws DlnaException
     */
    public void setModelNumber(String modelNumber, String device)
            throws DlnaException {
        checkRemote();
        if (modelNumber == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                nativeService.getDlnaService().devSetModelNumber(nativeHandle,
                        modelNumber);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                nativeDmrService.getDlnaService().devSetModelNumber(
                        nativeHandle, modelNumber);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }

    /**
     * Sets device model URL.
     * 
     * @param modelURL
     *        Device model URL
     * @throws DlnaException
     */
    public void setModelURL(String modelURL, String device)
            throws DlnaException {
        checkRemote();
        if (modelURL == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                nativeService.getDlnaService().devSetModelURL(nativeHandle,
                        modelURL);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                nativeDmrService.getDlnaService().devSetModelURL(nativeHandle,
                        modelURL);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }

    /**
     * Sets device other description.
     * 
     * @param otherDesc
     *        Device other description
     * @throws DlnaException
     */
    public void setOtherDesc(String otherDesc, String device)
            throws DlnaException {
        checkRemote();
        if (otherDesc == null) {
            throw new IllegalArgumentException();
        }
        if (device.equals("DMS")) {
            try {
                nativeService.getDlnaService().devSetOther(nativeHandle,
                        otherDesc);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (device.equals("DMR")) {
            try {
                nativeDmrService.getDlnaService().devSetOther(nativeHandle,
                        otherDesc);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(LOG_TAG, "Invalid input device");
        }
    }
}
