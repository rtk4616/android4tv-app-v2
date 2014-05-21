package com.iwedia.service.system.about;

import java.io.IOException;

import android.content.SharedPreferences.Editor;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.enums.SWUpgradeMsgs;
import com.iwedia.comm.system.about.ISoftwareUpdate;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.SystemControl;

/**
 * Class which handles the connection to the native 'stbmonitord' daemon. All
 * services which are supported by the daemon are exported with the methods of
 * this class.
 * 
 * @author Stanislava Markovic
 */
public class SoftwareUpdate extends ISoftwareUpdate.Stub implements
        ISTBMonitorListener {
    private static final String LOG_TAG = "SoftwareUpdate";
    private static final String OTA_DESC_XML_URL = "http://stb.rt-rk.com/updates/rk-2010/fw_upgrade.xml";
    private STBMonitor stbMonitor;

    public SoftwareUpdate() {
        if (IWEDIAService.SYSTEM_UPDATE_ENABLED) {
            checkSTBMonitor();
        }
    }

    /**
     * Returns FW version as a string in major.minor.revision format.
     * 
     * @return FW version, or empty string if there was an error.
     */
    @Override
    public String getRunnungVersion() throws RemoteException {
        if (IWEDIAService.SYSTEM_UPDATE_ENABLED) {
            return stbMonitor.getRunnungFWVersion();
        } else {
            return "";
        }
    }

    /**
     * Stops the connection to the daemon. This method should be called whenever
     * application does not need the connection any more (e.g. exiting the
     * application).
     */
    @Override
    public void stopConnection() throws RemoteException {
        if (IWEDIAService.SYSTEM_UPDATE_ENABLED) {
            stbMonitor.stopConnection();
        }
    }

    /**
     * Executes FW upgrade. Bear in mind that this method should <b>never</b>
     * return!
     */
    @Override
    public void upgrade() throws RemoteException {
        if (IWEDIAService.SYSTEM_UPDATE_ENABLED) {
            clearSavedFiles();
            stbMonitor.doFWUpgrade();
        }
    }

    /**
     * Check whether there is available FW upgrade over USB.
     */
    @Override
    public void copyUpgradeFWFromUSB() throws RemoteException {
        Log.e(LOG_TAG, "USB upgrade check called");
        stbMonitor.copyUpgradeFWFromUSB();
    }

    /**
     * Executes USB FW upgrade. Bear in mind that this method should
     * <b>never</b> return!
     */
    @Override
    public void finishUSBUpgrade() throws RemoteException {
        clearSavedFiles();
        stbMonitor.finishUSBFWUpgrade();
    }

    /**
     * Clear saved files.
     */
    private void clearSavedFiles() {
        Editor editor = IWEDIAService.getInstance().getPreferenceManager()
                .edit();
        editor.clear();
        editor.commit();
        IWEDIAService.getInstance().getStorageManager().deleteDatabase();
    }

    /**
     * Check whether there is available FW upgrade. This call is asynchronous
     * (in sense that there is no return value), and caller should expect
     * appropriate event (if there is one). It is synchronous in sense that it
     * WAITS for a connection to be established.
     * 
     * @param url
     *        URL to the
     */
    @Override
    public void upgradeCheck() throws RemoteException {
        if (IWEDIAService.SYSTEM_UPDATE_ENABLED) {
            stbMonitor.fwUpgradeCheck(OTA_DESC_XML_URL);
        }
    }

    /**
     * Event handling method.
     * 
     * @param code
     *        One of the event codes.
     * @param value
     *        Event message (can be null).
     */
    @Override
    public void handleEvent(int code, String value) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "handleEvent:" + code);
        }
        if (code == ISTBMonitorListener.FW_UPGRADE_EVENT) {
            Log.e(LOG_TAG, "FW_UPGRADE_EVENT" + value);
            SystemControl.broadcastUpdateEvent(value);
        } else if (code == ISTBMonitorListener.ERROR_EVENT) {
            SystemControl.broadcastErrorEvent(value);
        } else if (code == ISTBMonitorListener.NO_FW_UPGRADE_EVENT) {
            SystemControl.broadcastNoUpdateEvent("No update available!!!");
        } else if (code == ISTBMonitorListener.USB_FW_UPGRADE_EVENT) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "USB_FW_UPGRADE_EVENT" + value);
            }
            if (value.startsWith("Finish")) {
                stbMonitor.usbFWVersionCheck();
            } else {
                if (value.startsWith("Error")) {
                    SystemControl.broadcastUsbCheckUpdateEvent(
                            SWUpgradeMsgs.NO_ZIP_FILE, value);
                }
            }
            // SystemControl.broadcastUsbUpdateEvent(value);
        } else if (code == ISTBMonitorListener.USB_CHECK_UPGRADE) {
            Log.e(LOG_TAG, "USB_CHECK_UPGRADE" + value);
            if (value.startsWith("Error")) {
                SystemControl.broadcastUsbCheckUpdateEvent(
                        SWUpgradeMsgs.NO_ZIP_FILE, value);
            } else {
                boolean isAvailableSWVersion = false;
                String curStr = stbMonitor.getRunnungFWVersion();
                String updateStr = value;
                if (IWEDIAService.DEBUG)
                    Log.e(LOG_TAG, "USB_CHECK_UPGRADE Cur: " + curStr
                            + " Update: " + value);
                String[] separatedCurStr = new String[3];
                Log.d(LOG_TAG, "String: " + separatedCurStr);
                if (curStr != null) {
                    separatedCurStr = curStr.split("\\.");
                }
                String[] separatedUpdateStr = new String[3];
                Log.d(LOG_TAG, "String: " + separatedUpdateStr);
                separatedUpdateStr = updateStr.split("\\.");
                try {
                    for (int i = 0; i < separatedCurStr.length; i++) {
                        int currValue = Integer.parseInt(separatedCurStr[i]);
                        int updateValue = Integer
                                .parseInt(separatedUpdateStr[i]);
                        if (currValue > updateValue) {
                            if (IWEDIAService.DEBUG) {
                                Log.e(LOG_TAG, "DOWNGRADE" + value);
                            }
                            SystemControl.broadcastUsbCheckUpdateEvent(
                                    SWUpgradeMsgs.DOWNGRADE, value);
                            isAvailableSWVersion = true;
                            break;
                        } else if (currValue < updateValue) {
                            SystemControl.broadcastUsbCheckUpdateEvent(
                                    SWUpgradeMsgs.UPGRADE, value);
                            if (IWEDIAService.DEBUG) {
                                Log.e(LOG_TAG, "UPGRADE" + value);
                            }
                            isAvailableSWVersion = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isAvailableSWVersion = true;
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "INVALID zip: " + value);
                    }
                    SystemControl.broadcastUsbCheckUpdateEvent(
                            SWUpgradeMsgs.INVALID_ZIP, value + ".zip");
                }
                if (!isAvailableSWVersion) {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "NO_AVAILABLE_VERSION currValue: "
                                + value);
                    }
                    SystemControl.broadcastUsbCheckUpdateEvent(
                            SWUpgradeMsgs.NO_AVAILABLE_VERSION, value);
                }
            }
        }
    }

    public void checkSTBMonitor() {
        try {
            if (stbMonitor == null) {
                Log.e(LOG_TAG, "stbMonitor = new STBMonitor(this)");
                stbMonitor = new STBMonitor(this);
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
