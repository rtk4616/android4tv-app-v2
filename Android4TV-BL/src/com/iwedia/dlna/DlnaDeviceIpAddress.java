package com.iwedia.dlna;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

/**
 * DLNA Local Address class.
 * 
 * @author radakovic
 */
public class DlnaDeviceIpAddress {
    private String ipAddress;
    private Enumeration<NetworkInterface> en;
    private Enumeration<InetAddress> enumIpAddr;
    private NetworkInterface intf;
    private InetAddress inetAddress;

    public DlnaDeviceIpAddress() {
        try {
            en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                intf = en.nextElement();
                enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLinkLocalAddress()) {
                        if (!inetAddress.isLoopbackAddress()) {
                            this.ipAddress = inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            this.ipAddress = "";
            Log.e("Error", ex.toString());
        }
    }

    public void setDeviceIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceIpAddress() {
        return ipAddress;
    }
}
