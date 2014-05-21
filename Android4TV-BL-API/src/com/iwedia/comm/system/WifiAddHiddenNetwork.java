package com.iwedia.comm.system;

import android.os.Parcel;
import android.os.Parcelable;

public class WifiAddHiddenNetwork implements Parcelable {

    /**
     * Network name;
     */
    private String SSID;
    /**
     * Describes the authentication, key management, and encryption schemes
     * supported by the access point.
     */
    private String capabilities;
    /**
     * Password hor hidden network;
     */
    private String password;

    public WifiAddHiddenNetwork() {
        SSID = "";
        capabilities = "";
        password = "";
    }

    /** Returns network name. */
    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    /**
     * Describes the authentication, key management, and encryption schemes
     * supported by the access point.
     */
    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Password hor hidden network;
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static final Parcelable.Creator<WifiAddHiddenNetwork> CREATOR = new Parcelable.Creator<WifiAddHiddenNetwork>() {
        public WifiAddHiddenNetwork createFromParcel(Parcel in) {
            return new WifiAddHiddenNetwork(in);
        }

        public WifiAddHiddenNetwork[] newArray(int size) {
            return new WifiAddHiddenNetwork[size];
        }
    };

    private WifiAddHiddenNetwork(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SSID);
        dest.writeString(capabilities);
        dest.writeString(password);
    }

    public void readFromParcel(Parcel in) {
        this.SSID = in.readString();
        this.capabilities = in.readString();
        this.password = in.readString();
    }

}