package com.iwedia.gui.pvr;

import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class A4TVUSBStorage {
    private static final String TAG = "A4TVUSBStorage";
    public final long SIZE_KB = 1024L;
    public final long SIZE_MB = SIZE_KB * SIZE_KB;
    public final long SIZE_GB = SIZE_MB * SIZE_KB;
    private static final String PARTITIONS_FILE = "/proc/partitions";
    private static final String MOUNTS_FILE = "/proc/mounts";
    private String mDescription;
    private String mMountPath;
    private int mNumOfPartitions;
    private File mUsbMountDirFile;

    private String getUSBDeviceNameByPartition(String partitionName)
            throws IOException {
        String deviceName;
        String deviceNameFullPath;
        String subPartitionName = "";
        File partitionFile = new File("/sys/block/" + partitionName);
        String[] partitionDirContents = partitionFile.list();
        int index;
        if (partitionDirContents != null)
            for (int i = 0; i < partitionDirContents.length; i++) {
                index = partitionDirContents[i].indexOf(partitionName);
                if (index != -1) {
                    subPartitionName = "/"
                            + partitionDirContents[i].substring(index);
                    break;
                }
            }
        String deviceFileName = "/sys/block/" + partitionName
                + subPartitionName + "/dev";
        File deviceFile = new File(deviceFileName);
        Reader fileReader = new FileReader(deviceFile);
        try {
            BufferedReader deviceBufferReader = new BufferedReader(fileReader);
            deviceName = deviceBufferReader.readLine();
            deviceNameFullPath = "/dev/block/vold/" + deviceName;
            deviceBufferReader.close();
        } finally {
            fileReader.close();
        }
        return deviceNameFullPath;
    }

    private String getUSBStorageVendorByPartition(String partitionName)
            throws IOException {
        String vendorName;
        String vendorFileName = "/sys/block/" + partitionName
                + "/device/vendor";
        File vendorFile = new File(vendorFileName);
        Reader fileReader = new FileReader(vendorFile);
        try {
            BufferedReader vendorBufferReader = new BufferedReader(fileReader);
            vendorName = vendorBufferReader.readLine();
            vendorBufferReader.close();
        } finally {
            fileReader.close();
        }
        return vendorName;
    }

    private String getUSBStorageModelByPartition(String partitionName)
            throws IOException {
        String modelName;
        String modelFileName = "/sys/block/" + partitionName + "/device/model";
        File modelFile = new File(modelFileName);
        Reader fileReader = new FileReader(modelFile);
        try {
            BufferedReader modelBufferReader = new BufferedReader(fileReader);
            modelName = modelBufferReader.readLine();
            modelBufferReader.close();
        } finally {
            fileReader.close();
        }
        return modelName;
    }

    private boolean checkDeviceAndMountPoint(String partitionName,
            String mountPoint) throws IOException {
        boolean retVal = false;
        String tmpLine;
        File mountsFile = new File(MOUNTS_FILE);
        Reader fileReader = new FileReader(mountsFile);
        try {
            BufferedReader mountsBufferReader = new BufferedReader(fileReader);
            while ((tmpLine = mountsBufferReader.readLine()) != null) {
                if (tmpLine.startsWith(partitionName)) {
                    retVal = tmpLine.contains(mountPoint);
                    break;
                }
            }
            mountsBufferReader.close();
        } finally {
            fileReader.close();
        }
        return retVal;
    }

    private String getUSBStorageDescription(String mountPath)
            throws IOException {
        String description;
        String tmpLine;
        String usbDeviceName;
        String currentPartition;
        String prevPartition = "uninitilaized";
        String usbVendorName = "";
        String usbmodel = "";
        File partitionsFile = new File(PARTITIONS_FILE);
        Reader fileReader = new FileReader(partitionsFile);
        try {
            BufferedReader partitionsBufferReader = new BufferedReader(
                    fileReader);
            while ((tmpLine = partitionsBufferReader.readLine()) != null) {
                int index = tmpLine.indexOf("sd");
                if (index != -1) {
                    currentPartition = tmpLine.substring(index);
                    if (currentPartition.startsWith(prevPartition) == true) {
                        continue;
                    } else {
                        prevPartition = currentPartition;
                    }
                    usbDeviceName = getUSBDeviceNameByPartition(currentPartition);
                    if (checkDeviceAndMountPoint(usbDeviceName, mountPath)) {
                        usbVendorName = getUSBStorageVendorByPartition(currentPartition);
                        usbmodel = getUSBStorageModelByPartition(currentPartition);
                    }
                }
            }
            partitionsBufferReader.close();
        } finally {
            fileReader.close();
        }
        description = usbVendorName + " " + usbmodel;
        return description;
    }

    A4TVUSBStorage(String mountPath) {
        mMountPath = mountPath;
        try {
            mDescription = getUSBStorageDescription(mountPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mUsbMountDirFile = new File(mountPath);
        File[] listFile = mUsbMountDirFile.listFiles();
        if (listFile != null) {
            mNumOfPartitions = listFile.length;
        }
        Log.d(TAG, "mDescription: " + mDescription + " mMountPath: "
                + mMountPath + " mNumOfPartitions: " + mNumOfPartitions);
    }

    public String getDescription() {
        return mDescription;
    }

    public String getMountPath() {
        return mMountPath;
    }

    public String getPartitionMountPath(int partition) {
        if (partition >= mNumOfPartitions) {
            return null;
        }
        File[] listFile = mUsbMountDirFile.listFiles();
        if (listFile != null) {
            return listFile[partition].toString();
        } else {
            return null;
        }
    }

    public int getNumOfPartitions() {
        return mNumOfPartitions;
    }

    public long getPartitionSize(int partition) {
        File[] listFile = mUsbMountDirFile.listFiles();
        String usbPartitionDir;
        if (listFile != null) {
            usbPartitionDir = listFile[partition].toString();
        } else {
            usbPartitionDir = "";
        }
        StatFs statFs = new StatFs(usbPartitionDir);
        return (long) (((long) statFs.getBlockSize() * (long) statFs
                .getBlockCount()) / SIZE_MB);
    }

    public long getPartitionAvailableSize(int partition) {
        File[] listFile = mUsbMountDirFile.listFiles();
        String usbPartitionDir;
        if (listFile != null) {
            usbPartitionDir = listFile[partition].toString();
        } else {
            usbPartitionDir = "";
        }
        StatFs statFs = new StatFs(usbPartitionDir);
        return (long) (((long) statFs.getBlockSize() * (long) statFs
                .getFreeBlocks()) / SIZE_MB);
    }
}
