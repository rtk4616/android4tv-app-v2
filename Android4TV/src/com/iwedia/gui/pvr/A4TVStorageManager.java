package com.iwedia.gui.pvr;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.iwedia.dtv.setup.OffSignalTimerEvent;
import com.iwedia.gui.MainActivity;

import java.util.HashMap;
import java.util.Map.Entry;

public class A4TVStorageManager {
    private static final String TAG = "A4TVStorageManager";
    private static final String USB_MOUNT_ROOT = "/storage/usb";
    private final static HashMap<String, A4TVUSBStorage> mDevices = new HashMap<String, A4TVUSBStorage>();
    private Context mContext;
    private StorageManager mStorageManager = null;

    private void addUSBStorage(String mountPath) {
        A4TVUSBStorage usb = new A4TVUSBStorage(mountPath);
        mDevices.put(mountPath, usb);
        try {
            MainActivity.service.getSetupControl().offSignalTimerStatusUpdate(
                    OffSignalTimerEvent.USB_MOUNTED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "AddUSBStorage mount dir: " + mountPath);
    }

    private void removeUSBStorage(String mountPath) {
        mDevices.remove(mountPath);
        try {
            MainActivity.service.getSetupControl().offSignalTimerStatusUpdate(
                    OffSignalTimerEvent.USB_UNMOUNTED_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "RemoveUSBStorage mount dir: " + mountPath);
    }

    StorageEventListener mStorageListener = new StorageEventListener() {
        @Override
        public void onStorageStateChanged(String path, String oldState,
                String newState) {
            if (newState.equals("mounted")) {
                addUSBStorage(path);
                Log.d(TAG, "Number of USB devices: " + mDevices.size());
            } else if (newState.equals("removed")) {
                removeUSBStorage(path);
                Log.d(TAG, "Number of USB devices: " + mDevices.size());
            }
        }
    };

    public A4TVStorageManager() {
        mContext = MainActivity.activity.getApplicationContext();
        if (!mDevices.isEmpty()) {
            mDevices.clear();
        }
        mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);
        mStorageManager.registerListener(mStorageListener);
        StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
        Log.d(TAG, "Initial storageVolumes :" + storageVolumes);
        for (int i = 0; i < storageVolumes.length; i++) {
            Log.d(TAG, storageVolumes[i].toString());
            if (storageVolumes[i].getPath().startsWith(USB_MOUNT_ROOT)) {
                Log.d(TAG, "This is an USB device");
                addUSBStorage(storageVolumes[i].getPath());
            }
            if (storageVolumes[i].getDescription(mContext) != null) {
                Log.d(TAG, storageVolumes[i].getDescription(mContext));
            }
        }
        Log.d(TAG, "Number of USB devices: " + mDevices.size());
    }

    public int getNumberOfUSBSorages() {
        return mDevices.size();
    }

    public HashMap<String, A4TVUSBStorage> getUSBStorages() {
        return mDevices;
    }

    public A4TVUSBStorage getUSBStorage(int index) {
        if (index >= mDevices.size()) {
            return null;
        }
        A4TVUSBStorage usbStorage = null;
        int deviceIndex = 0;
        for (Entry<String, A4TVUSBStorage> entry : mDevices.entrySet()) {
            if (deviceIndex++ == index) {
                usbStorage = entry.getValue();
                break;
            }
        }
        return usbStorage;
    }
}
