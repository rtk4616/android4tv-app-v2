package com.iwedia.comm.system.application;

import android.os.Parcel;
import android.os.Parcelable;

public class AppSizeInfo implements Parcelable {

    // App
    private String codeSize;

    // Data
    private String dataSize;

    // SD card
    private String externalCacheSize;

    // Cache
    private String cacheSize;

    private String externalObbSize;
    private String externalDataSize;
    private String externalMediaSize;
    private String totalSize;

    private boolean isCacheEmpty;

    private boolean isDataEmpty;

    public AppSizeInfo() {
        codeSize = "";
        dataSize = "";
        externalCacheSize = "";
        cacheSize = "";

        externalObbSize = "";
        externalDataSize = "";
        externalMediaSize = "";
        totalSize = "";

        isCacheEmpty = true;
        isDataEmpty = true;

    }

    public String getCodeSize() {
        return codeSize;
    }

    public String getDataSize() {
        return dataSize;
    }

    public String getExternalCacheSize() {
        return externalCacheSize;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public String getExternalObbSize() {
        return externalObbSize;
    }

    public String getExternalDataSize() {
        return externalDataSize;
    }

    public String getExternalMediaSize() {
        return externalMediaSize;
    }

    public void setCodeSize(String codeSize) {
        this.codeSize = codeSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public void setExternalCacheSize(String externalCacheSize) {
        this.externalCacheSize = externalCacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setExternalObbSize(String externalObbSize) {
        this.externalObbSize = externalObbSize;
    }

    public void setExternalDataSize(String externalDataSize) {
        this.externalDataSize = externalDataSize;
    }

    public void setExternalMediaSize(String externalMediaSize) {
        this.externalMediaSize = externalMediaSize;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public boolean isCacheEmpty() {
        return isCacheEmpty;
    }

    public boolean isDataEmpty() {
        return isDataEmpty;
    }

    public void setCacheEmpty(boolean isCacheEmpty) {
        this.isCacheEmpty = isCacheEmpty;
    }

    public void setDataEmpty(boolean isDataEmpty) {
        this.isDataEmpty = isDataEmpty;
    }

    public static final Parcelable.Creator<AppSizeInfo> CREATOR = new Parcelable.Creator<AppSizeInfo>() {
        public AppSizeInfo createFromParcel(Parcel in) {
            return new AppSizeInfo(in);
        }

        public AppSizeInfo[] newArray(int size) {
            return new AppSizeInfo[size];
        }
    };

    private AppSizeInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {

            dest.writeString(codeSize);
            dest.writeString(dataSize);
            dest.writeString(externalCacheSize);
            dest.writeString(cacheSize);

            dest.writeString(externalObbSize);
            dest.writeString(externalDataSize);
            dest.writeString(externalMediaSize);
            dest.writeString(totalSize);

            dest.writeByte((byte)(isCacheEmpty ? 1 : 0));
            dest.writeByte((byte)(isDataEmpty ? 1 : 0));

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFromParcel(Parcel in) {
        try {
            codeSize = in.readString();
            dataSize = in.readString();
            externalCacheSize = in.readString();
            cacheSize = in.readString();

            externalObbSize = in.readString();
            externalDataSize = in.readString();
            externalMediaSize = in.readString();
            totalSize = in.readString();

            isCacheEmpty = in.readByte() == 1;
            isDataEmpty = in.readByte() == 1;

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
