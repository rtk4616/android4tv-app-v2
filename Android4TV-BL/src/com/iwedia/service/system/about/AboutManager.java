package com.iwedia.service.system.about;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.iwedia.service.IWEDIAService;

public class AboutManager {
    private static final String LOG_TAG = "AboutManager";
    private final String FILENAME_PROC_VERSION = "/proc/version";
    private final String UNAVAILABLE = "Unavailable";

    public AboutManager() {
        // TODO Auto-generated constructor stub
    }

    public String getMacAddress() {
        String macAddress;
        WifiManager wifiMgr = (WifiManager) IWEDIAService.getContext()
                .getSystemService(Context.WIFI_SERVICE);
        macAddress = wifiMgr.getConnectionInfo().getMacAddress();
        if (macAddress.length() == 0 || macAddress == null) {
            macAddress = UNAVAILABLE;
        }
        return macAddress;
    }

    public String getIPAddress() {
        String ipAddress = UNAVAILABLE;
        byte[] localIP = getLocalIPAddress();
        if (localIP != null) {
            ipAddress = getDottedDecimalIP(localIP);
            if (ipAddress.length() == 0 || ipAddress == null) {
                ipAddress = UNAVAILABLE;
            }
        }
        return ipAddress;
    }

    public String getModelNumber() {
        String buildModel;
        buildModel = Build.MODEL;
        if (buildModel.length() == 0 || buildModel == null) {
            buildModel = UNAVAILABLE;
        }
        return buildModel;
    }

    public String getAndroidVersion() {
        String androidVersion;
        androidVersion = Build.VERSION.RELEASE;
        if (androidVersion.length() == 0 || androidVersion == null) {
            androidVersion = Build.VERSION.RELEASE;
        }
        return androidVersion;
    }

    public String getKernelVersion() {
        try {
            return formatKernelVersion(readLine(FILENAME_PROC_VERSION));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return UNAVAILABLE;
        }
    }

    public String getBuildNumber() {
        String buildNumber;
        buildNumber = Build.DISPLAY;
        if (buildNumber.length() == 0 || buildNumber == null) {
            buildNumber = UNAVAILABLE;
        }
        return buildNumber;
    }

    private byte[] getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, "getLocalIPAddress()", ex);
        }
        return null;
    }

    private String getDottedDecimalIP(byte[] ipAddr) {
        // convert to dotted decimal notation:
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                res.append(".");
            }
            res.append(ipAddr[i] & 0xFF);
        }
        return res.toString();
    }

    /**
     * Reads a line from the specified file.
     * 
     * @param filename
     *        the file to read from
     * @return the first line, if any.
     * @throws IOException
     *         if the file couldn't be read
     */
    private static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename),
                256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        // (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        // Thu Jun 28 11:02:39 PDT 2012
        final String PROC_VERSION_REGEX = "Linux version (\\S+) " + /*
                                                                     * group 1:
                                                                     * "3.0.31-g6fb96c9"
                                                                     */
        "\\((\\S+?)\\) " + /* group 2: "x@y.com" (kernel builder) */
        "(?:\\(gcc.+? \\)) " + /* ignore: GCC version information */
        "(#\\d+) " + /* group 3: "#1" */
        "(?:.*?)?" + /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
        "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /*
                                              * group 4:
                                              * "Thu Jun 28 11:02:39 PDT 2012"
                                              */
        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(
                rawKernelVersion);
        if (!m.matches()) {
            Log.e(LOG_TAG, "Regex did not match on /proc/version: "
                    + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(LOG_TAG,
                    "Regex match on /proc/version only returned "
                            + m.groupCount() + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" + // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4); // Thu Jun 28 11:02:39 PDT 2012
    }
}
