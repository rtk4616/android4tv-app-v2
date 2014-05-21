package com.iwedia.comm.content.applications;

import com.iwedia.comm.enums.AppListType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is used to collect informations about Android system applications.
 *
 * @author Marko Zivanovic
 *
 */
public class AppItem implements Parcelable {

    /**
     * Name of Android application.
     */
    private String appname;
    /**
     * Application package.
     */
    private String appPackage;

    /**
     * On of the following: {@link com.iwedia.comm.enums.AppListType}.
     */
    private int appListType;

    /**
     * Application class.
     */
    private String appClass;

    public AppItem() {
        this.appname = "";
        this.appPackage = "";
        this.setAppClass("");
        this.appListType = AppListType.NONE;
    }

    /**
     * Returns Application type
     *
     * @return on of the following: {@link com.iwedia.comm.enums.AppListType}.
     */
    public int getAppListType() {
        return appListType;
    }

    /**
     * Sets Application type
     *
     * @param appType
     *            - {@link com.iwedia.comm.enums.AppListType}.
     */
    public void setAppListType(int appType) {
        this.appListType = appType;
    }

    /**
     * Returns application name.
     *
     * @return name of Android application.
     */
    public String getAppname() {
        return appname;
    }

    /**
     * Sets Android application name.
     *
     * @param appname
     */
    public void setAppname(String appname) {
        this.appname = appname;
    }

    /**
     * Gets Android application package.
     *
     * @return application package.
     */
    public String getAppPackage() {
        return appPackage;
    }

    /**
     * Sets application package
     *
     * @param appPackage
     *            - Android application package.
     */
    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(String appClass) {
        this.appClass = appClass;
    }

    public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator<AppItem>() {
        public AppItem createFromParcel(Parcel in) {
            return new AppItem(in);
        }

        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };

    private AppItem(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeString(appname);
            dest.writeString(appPackage);
            dest.writeInt(appListType);
            dest.writeString(appClass);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFromParcel(Parcel in) {
        try {
            appname = in.readString();
            appPackage = in.readString();
            appListType = in.readInt();
            appClass = in.readString();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
