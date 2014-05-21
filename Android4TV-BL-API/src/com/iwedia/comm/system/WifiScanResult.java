package com.iwedia.comm.system;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describes information about a detected access point.
 *
 * @author Marko Zivanovic
 */

public class WifiScanResult implements Parcelable {

    /**
     * Android ScanResult information.
     */
    /**
     * Network name;
     */
    private String SSID;
    /** The address of the access point. */
    private String BSSID;
    /**
     * Describes the authentication, key management, and encryption schemes
     * supported by the access point.
     */
    private String capabilities;
    /**
     * The detected signal level in dBm. The smaller value, the weaker signal
     * strength.
     */
    private int level;
    /**
     * The frequency in MHz of the channel over which the client is
     * communicating with the access point.
     */
    private int frequency;

    public WifiScanResult(android.net.wifi.ScanResult scanResult) {
        this.SSID = scanResult.SSID;
        this.BSSID = scanResult.BSSID;
        this.capabilities = scanResult.capabilities;
        this.level = scanResult.level;
        this.frequency = scanResult.frequency;
    }

    /** Returns network name. */
    public String getSSID() {
        return this.SSID;
    }

    /** The address of the access point. */
    public String getBSSID() {
        return this.BSSID;
    }

    /**
     * Describes the authentication, key management, and encryption schemes
     * supported by the access point.
     */
    public String getCapabilities() {
        return convertCapabilityToString(this.capabilities);
    }

    /**
     * Returns short format of AP capability.
     */
    public String getExtendedCapabilities() {
        return this.capabilities;
    }

    /**
     * The detected signal level in dBm. The smaller value, the weaker signal
     * strength.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * The frequency in MHz of the channel over which the client is
     * communicating with the access point.
     */
    public int getFrequency() {
        return this.frequency;
    }

    public static final Parcelable.Creator<WifiScanResult> CREATOR = new Parcelable.Creator<WifiScanResult>() {
        public WifiScanResult createFromParcel(Parcel in) {
            return new WifiScanResult(in);
        }

        public WifiScanResult[] newArray(int size) {
            return new WifiScanResult[size];
        }
    };

    private WifiScanResult(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SSID);
        dest.writeString(BSSID);
        dest.writeString(capabilities);
        dest.writeInt(level);
        dest.writeInt(frequency);
    }

    public void readFromParcel(Parcel in) {
        this.SSID = in.readString();
        this.BSSID = in.readString();
        this.capabilities = in.readString();
        this.level = in.readInt();
        this.frequency = in.readInt();
    }

    private String convertCapabilityToString(String capability) {
        if(capability.startsWith("[WPA-PSK")) {
            return "WPA PSK";
        } else if(capability.startsWith("[WPA2-PSK")) {
            return "WPA2 PSK";
        }
        if(capability.startsWith("[WPA-EAP")) {
            return "WPA EAP";
        }
        if(capability.startsWith("[WPA2-EAP")) {
            return "WPA EAP";
        }
        if(capability.startsWith("[WEP")) {
            return "WEP";
        } else {
            return "NONE";
        }

    }
}