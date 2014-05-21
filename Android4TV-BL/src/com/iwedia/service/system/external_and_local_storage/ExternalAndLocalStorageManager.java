package com.iwedia.service.system.external_and_local_storage;

import java.io.File;
import java.net.URI;

import android.os.StatFs;
import android.util.Log;

public class ExternalAndLocalStorageManager {
    public static final String TAG = "ExternalAndLocalStorageManager";
    public final long SIZE_KB = 1024L;
    public final long SIZE_MB = SIZE_KB * SIZE_KB;
    public final long SIZE_GB = SIZE_MB * SIZE_KB;
    private double space = -1L;
    private double usbTotalSpace = -1L;
    private double usbFreeSpace = -1L;
    private String spaceString = "";
    private StatFs stat;
    private boolean isGB;
    private String usbRoot = "/mnt/media/";
    private String localRoot = "/mnt/sdcard/";
    private File fileGlobal;
    private String usbPathGlobal;
    private static ExternalAndLocalStorageManager instance;

    /**
     * if total space - totalSpace=true, else if available space -
     * totalSpace=false
     */
    public String getSpace(boolean totalSpace, boolean isExternalStorage) {
        try {
            if (isExternalStorage) {
                File file = new File(usbRoot);
                String usbPath;
                File[] fileArray = file.listFiles();
                if (fileArray != null) {
                    if (fileArray.length > 0) {
                        usbPath = fileArray[0].toString();
                        stat = new StatFs(usbPath);
                    } else {
                        return "0 B";
                    }
                } else {
                    return "0 B";
                }
            } else {
                stat = new StatFs(localRoot);
            }
            if (totalSpace) {
                space = (double) stat.getBlockCount()
                        * (double) stat.getBlockSize();
            } else {
                space = (double) stat.getAvailableBlocks()
                        * (double) stat.getBlockSize();
            }
            if ((space / SIZE_GB) >= 1) {
                space = space / SIZE_GB;
                isGB = true;
            } else {
                space = space / SIZE_MB;
                isGB = false;
            }
            spaceString = String.format("%.2f", space);
            if (isGB) {
                spaceString = spaceString + " GB";
            } else {
                spaceString = spaceString + " MB";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spaceString;
    }

    public boolean isExternalMemoryFull() {
        if (usbPathGlobal == null) {
            setUsbPath(null);
        }
        if (usbPathGlobal != null) {
            stat = new StatFs(usbPathGlobal);
            usbTotalSpace = (double) stat.getBlockCount()
                    * (double) stat.getBlockSize();
            usbFreeSpace = (double) stat.getAvailableBlocks()
                    * (double) stat.getBlockSize();
            if (usbFreeSpace > usbTotalSpace * 0.1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void setUsbPath(String path) {
        if (path != null) {
            usbRoot = (new File(URI.create(path))).getAbsolutePath();
            Log.d(TAG, "Setting USB root path to " + usbRoot);
        }
        fileGlobal = new File(usbRoot);
        usbPathGlobal = null;
        File[] fileArray = fileGlobal.listFiles();
        if (fileArray != null)
            if (fileArray.length > 0) {
                usbPathGlobal = fileArray[0].toString();
                Log.d(TAG, "Setting USB record path to " + usbPathGlobal);
            }
    }

    public void deselectUsbPath() {
        fileGlobal = null;
        usbPathGlobal = null;
    }

    public String getUsbPath() {
        return usbPathGlobal;
    }

    public static ExternalAndLocalStorageManager getInstance() {
        if (instance == null) {
            instance = new ExternalAndLocalStorageManager();
        }
        return instance;
    }
}
