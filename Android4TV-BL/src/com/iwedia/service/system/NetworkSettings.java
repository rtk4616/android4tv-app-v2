package com.iwedia.service.system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.enums.NetworkType;
import com.iwedia.comm.enums.WirelessState;
import com.iwedia.comm.enums.WpsState;
import com.iwedia.comm.system.INetworkCallback;
import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.comm.system.WifiAddHiddenNetwork;
import com.iwedia.comm.system.WifiScanResult;
import com.iwedia.service.IWEDIAService;

/**
 * Default Android network manager. Used to manage active network type, gather
 * network parameters, etc.
 * 
 * @author Marko Zivanovic
 */
public class NetworkSettings extends INetworkSettings.Stub {
    public static final int ETHERNET_STATE_UNKNOWN = 0;
    public static final int ETHERNET_STATE_DISABLED = 1;
    public static final int ETHERNET_STATE_ENABLED = 2;
    private final String LOG_TAG = "NetworkSettings";
    /**
     * Network test callback.
     */
    private INetworkCallback callbacks;
    /**
     * Android wireless settings manager.
     */
    private WifiManager wifiManager;
    // TODO: This Should Be Fixed!
    // private EthernetManager ethManager;
    private ConnectivityManager conectivityManager;
    /**
     * Channel
     */
    // private WifiManager.Channel channel;
    private Object channel;
    /**
     * Submitted WPS operation event listener
     */
    // TODO: This Should Be Fixed!
    // private WifiManager.WpsListener wpsSubmitListener;
    /**
     * Canceled WPS operation event listener
     */
    // TODO: This Should Be Fixed!
    // private WifiManager.ActionListener wpsCancelListener;
    /**
     * Available wireless networks.
     */
    private List<ScanResult> scanResults;
    /**
     * {@linkplain WiFiScanReceiver} object.
     */
    private WiFiScanReceiver receiver;
    /**
     * {@linkplain WifiStatusReceiver} object.
     */
    private WifiStatusReceiver statusReceiver;
    /**
     * Currently active wireless network. Null if wireless adapter is disabled
     * or not connected to any wireless network.
     */
    private WifiScanResult currentlyActiveWirelessNetwork;
    /**
     * Currently active network;
     */
    private int activeNetwork = NetworkType.UNDEFINED;

    /**
     * Default constructor.
     */
    public NetworkSettings() {
        wifiManager = (WifiManager) IWEDIAService.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        // ethManager= (EthernetManager) IWEDIAService.getInstance()
        // .getSystemService(Context.ETHERNET_SERVICE);
        conectivityManager = (ConnectivityManager) IWEDIAService.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (MethodExists(wifiManager, "initialize")) {
            Method initializeMethod = GetRemoteMethod(wifiManager, "initialize");
            try {
                channel = (Object) initializeMethod.invoke(wifiManager,
                        IWEDIAService.getContext(), IWEDIAService.getContext()
                                .getMainLooper(), null);
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        receiver = new WiFiScanReceiver();
        statusReceiver = new WifiStatusReceiver();
        // TODO: This Should Be Fixed!
        // wpsSubmitListener = new WpsSubmitListener();
        // wpsCancelListener = new WpsCancelListener();
        /**
         * Register receivers.
         */
        IWEDIAService.getInstance().registerReceiver(receiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter
                .addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        mIntentFilter.addAction(WifiManager.EXTRA_WIFI_STATE);
        mIntentFilter.addAction(WifiManager.EXTRA_NEW_STATE);
        mIntentFilter.addAction(WifiManager.EXTRA_PREVIOUS_WIFI_STATE);
        IWEDIAService.getInstance().registerReceiver(statusReceiver,
                mIntentFilter);
        scanResults = new ArrayList<ScanResult>();
        currentlyActiveWirelessNetwork = null;
        ArrayList<Integer> listOfAvailableInterface = networkType();
        int prefferedNetworkType = IWEDIAService.getInstance()
                .getPreferenceManager()
                .getInt("active_network_type", NetworkType.ETHERNET);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "prefferedNetworkType: " + prefferedNetworkType);
        }
        if (listOfAvailableInterface.contains(prefferedNetworkType)) {
            activeNetwork = prefferedNetworkType;
        }
    }

    public boolean FieldExists(Object RemoteClass, String FieldName) {
        Field[] fields = RemoteClass.getClass().getFields();
        for (Field field : fields) {
            if (field.getName().equals(FieldName)) {
                return true;
            }
        }
        return false;
    }

    public boolean MethodExists(Object RemoteClass, String MethodName) {
        Method[] methods = RemoteClass.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(MethodName)) {
                return true;
            }
        }
        return false;
    }

    public Method GetRemoteMethod(Object RemoteClass, String MethodName) {
        Method[] methods = RemoteClass.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(MethodName)) {
                return method;
            }
        }
        return null;
    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    /**
     * Returns Ethernet MAC address.
     */
    @Override
    public String getEthernetMacAddress() throws RemoteException {
        return getEth0MacAddress();
    }

    /**
     * Returns network type.
     * 
     * @return enum {@link com.iwedia.comm.enums.NetworkType};
     */
    @Override
    public int getActiveNetworkType() throws RemoteException {
        // // conectivityManager = (ConnectivityManager) IWEDIAService
        // // .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        //
        //
        // if(FieldExists(IWEDIAService.getInstance().getContext(),
        // "ETHERNET_SERVICE"))
        // {
        // //have x86-android ethernet fix
        // String ethServiceString;
        // try {
        // ethServiceString = (String)
        // getField(IWEDIAService.getInstance().getContext(),
        // "ETHERNET_SERVICE");
        // ethManager= (EthernetManager)
        // IWEDIAService.getInstance().getSystemService(ethServiceString);
        // }
        // catch (Exception e) {
        // //ETHERNET_SERVICE field exists but unable to get field value
        // e.printStackTrace();
        // }
        //
        //
        // }
        // else
        // {
        // //do everything differently
        //
        // }
        //
        //
        NetworkInfo activeNetworkType = conectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkType != null
                && activeNetworkType.isConnectedOrConnecting()) {
            Log.e(LOG_TAG, "Current active network type is :"
                    + activeNetworkType.getTypeName());
            switch (activeNetworkType.getType()) {
                case ConnectivityManager.TYPE_WIFI: {
                    return NetworkType.WIRELESS;
                }
                case ConnectivityManager.TYPE_ETHERNET: {
                    return NetworkType.ETHERNET;
                }
                default:
                    return NetworkType.UNDEFINED;
            }
        } else
            return NetworkType.UNDEFINED;
    }

    /**
     * Returns wireless MAC address.
     */
    @Override
    public String getWirelessMacAddress() throws RemoteException {
        WifiInfo wifiInf = wifiManager.getConnectionInfo();
        return wifiInf.getMacAddress();
    }

    /**
     * Return name of available wireless network at given index.
     */
    @Override
    public WifiScanResult getWirelessNetwork(int index) throws RemoteException {
        return new WifiScanResult(scanResults.get(index));
    }

    /**
     * Returns number of available wireless networks.
     */
    @Override
    public int getNumberOfAvailableWirelessNetworks() throws RemoteException {
        return scanResults.size();
    }

    /**
     * Connect to given ScanResult.
     * 
     * @param scanResult
     *        represent available Wireless network.
     * @param password
     *        wireless network password. If wireless network is open, pass null.
     */
    @Override
    public boolean setActiveWirelessNetwork(WifiScanResult scanResult,
            String password) throws RemoteException {
        disconnectActiveWirelessNetwork();
        boolean isPasswordCorrect = connectToWirelessNetwork(
                scanResult.getCapabilities(), scanResult.getSSID(),
                scanResult.getBSSID(), password, IWEDIAService.getInstance());
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "status of connection:" + isPasswordCorrect);
        }
        if (!isPasswordCorrect) {
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_PASSWORD_INCORRECT);
        }
        return isPasswordCorrect;
    }

    /**
     * Connect to hidden wireless network.
     * 
     * @param network
     *        all information about hidden network.
     */
    @Override
    public boolean setHiddenWirelessNetwork(WifiAddHiddenNetwork network)
            throws RemoteException {
        disconnectActiveWirelessNetwork();
        boolean isPasswordCorrect = connectToHiddenWirelessNetwork(network);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "status of connection:" + isPasswordCorrect);
        }
        if (!isPasswordCorrect) {
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_PASSWORD_INCORRECT);
        } else {
            try {
                Thread.sleep(4000);
                SupplicantState supState;
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                supState = wifiInfo.getSupplicantState();
                if (supState != SupplicantState.COMPLETED) {
                    broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_FAILED);
                } else {
                    broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_CONNECTED);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e(LOG_TAG, "+continue: ");
        return isPasswordCorrect;
    }

    /**
     * Returns currently active wireless network.
     */
    @Override
    public WifiScanResult getActiveWirelessNetwork() throws RemoteException {
        return currentlyActiveWirelessNetwork;
    }

    /**
     * Disconnect active wireless network.
     */
    @Override
    public void disconnectActiveWirelessNetwork() throws RemoteException {
        currentlyActiveWirelessNetwork = null;
        wifiManager.disconnect();
    }

    /**
     * Sets network type Ethernet or Wireless.
     * 
     * @param type
     *        enum {@link com.iwedia.comm.enums.NetworkType};
     */
    public boolean setActiveNetworkType(int desiredNetworkType)
            throws RemoteException {
        Log.e(LOG_TAG,
                "\ncheck CONNECTIVITY_SERVICE "
                        + FieldExists(IWEDIAService.getInstance().getContext(),
                                "ETHERNET_SERVICE"));
        Boolean retVal = false;
        if (FieldExists(IWEDIAService.getInstance().getContext(),
                "ETHERNET_SERVICE")) {
            // have x86-android ethernet fix
            String ethServiceString;
            try {
                ethServiceString = (String) getField(IWEDIAService
                        .getInstance().getContext(), "ETHERNET_SERVICE");
                // TODO: This Should Be Fixed!
                // ethManager = (EthernetManager) IWEDIAService.getInstance()
                // .getSystemService(ethServiceString);
                retVal = setActiveNetworkType_x86Patch(desiredNetworkType);
            } catch (Exception e) {
                // ETHERNET_SERVICE field exists but unable to get field value
                e.printStackTrace();
            }
        } else {
            retVal = setActiveNetworkType_CLI(desiredNetworkType);
        }
        return retVal;
    }

    public boolean setActiveNetworkType_x86Patch(int desiredNetworkType)
            throws RemoteException {
        NetworkInfo activeNetworkType = conectivityManager
                .getActiveNetworkInfo();
        if (activeNetworkType != null
                && activeNetworkType.isConnectedOrConnecting())
            Log.e(LOG_TAG, "Current active network type is :"
                    + activeNetworkType.getTypeName());
        switch (desiredNetworkType) {
            case NetworkType.ETHERNET: {
                if (activeNetworkType != null
                        && activeNetworkType.getType() == conectivityManager.TYPE_WIFI) {
                    Log.e(LOG_TAG, "Disabling Wifi");
                    wifiManager.setWifiEnabled(false);
                }
                Log.e(LOG_TAG, "Enabling Ethernet");
                currentlyActiveWirelessNetwork = null;
                // TODO: This Should Be Fixed!
                // if (MethodExists(ethManager, "setEnabled")) {
                // Method getWifiApStateMethod = GetRemoteMethod(ethManager,
                // "setEnabled");
                // try {
                // getWifiApStateMethod.invoke(ethManager, true);
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
                break;
            }
            case NetworkType.WIRELESS: {
                if (activeNetworkType != null
                        && activeNetworkType.getType() == conectivityManager.TYPE_ETHERNET) {
                    Log.e(LOG_TAG, "Disabling Ethernet");
                    // TODO: This Should Be Fixed!
                    // if (MethodExists(ethManager, "setEnabled")) {
                    // Method getWifiApStateMethod = GetRemoteMethod(
                    // ethManager, "setEnabled");
                    // try {
                    // getWifiApStateMethod.invoke(ethManager, false);
                    // } catch (Exception e) {
                    // e.printStackTrace();
                    // }
                    // }
                }
                Log.e(LOG_TAG, "Enabling Wifi");
                wifiManager.setWifiEnabled(true);
                break;
            }
            default: {
                // TODO: This Should Be Fixed!
                // if (MethodExists(ethManager, "setEnabled")) {
                // Method getWifiApStateMethod = GetRemoteMethod(ethManager,
                // "setEnabled");
                // try {
                // getWifiApStateMethod.invoke(ethManager, false);
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
                wifiManager.setWifiEnabled(false);
                currentlyActiveWirelessNetwork = null;
            }
                return true;
        }
        return false;
    }

    public boolean setActiveNetworkType_CLI(int desiredNetworkType)
            throws RemoteException {
        // activeNetwork = desiredNetworkType;
        switch (desiredNetworkType) {
            case NetworkType.ETHERNET: {
                wifiManager.setWifiEnabled(false);
                currentlyActiveWirelessNetwork = null;
                scanResults = new ArrayList<ScanResult>();
                Editor editor = IWEDIAService.getInstance()
                        .getPreferenceManager().edit();
                editor.putInt("active_network_type", NetworkType.ETHERNET);
                editor.commit();
                break;
            }
            case NetworkType.WIRELESS: {
                scanResults = new ArrayList<ScanResult>();
                Editor editor = IWEDIAService.getInstance()
                        .getPreferenceManager().edit();
                editor.putInt("active_network_type", NetworkType.WIRELESS);
                editor.commit();
                boolean state = wifiManager.setWifiEnabled(true);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        wifiManager.startScan();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask, 0, 1200);
                String netInf = "";
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process proc = rt.exec("netcfg");
                    InputStream stdin = proc.getInputStream();
                    try {
                        InputStreamReader isr = new InputStreamReader(stdin);
                        BufferedReader br = new BufferedReader(isr);
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            if (line.contains("ra0")) {
                                if (line.toLowerCase().contains("up")) {
                                    netInf = "ra0";
                                }
                            } else if (line.contains("wlan0")) {
                                if (line.toLowerCase().contains("up")) {
                                    netInf = "wlan0";
                                }
                            }
                        }
                    } finally {
                        stdin.close();
                    }
                    proc.waitFor();
                } catch (Exception t) {
                    t.printStackTrace();
                }
                return state;
            }
            default: {
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process proc = rt.exec("ifconfig eth0 down");
                    proc.waitFor();
                } catch (Exception t) {
                    t.printStackTrace();
                }
                wifiManager.setWifiEnabled(false);
                currentlyActiveWirelessNetwork = null;
            }
        }
        return false;
    }

    public boolean setActiveNetworkType_MarvellPatch(int desiredNetworkType)
            throws RemoteException {
        // to be done
        return false;
    }

    /*
     * Load file content to String
     */
    private String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead;
        try {
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
        } finally {
            reader.close();
        }
        return fileData.toString();
    }

    /*
     * Get the STB MacAddress
     */
    private String getEth0MacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Register client for INetworkCallback;
     */
    @Override
    public void registerCallback(INetworkCallback callback)
            throws RemoteException {
        callbacks = callback;
    }

    /**
     * Unregister client for INetworkCallback;
     */
    @Override
    public void unregisteCallback() throws RemoteException {
        callbacks = null;
    }

    /**
     * Get Network type from download rate
     * 
     * @return 0 for wireless and 1 for Ethernet
     */
    private ArrayList<Integer> networkType() {
        ArrayList<Integer> returnValues = new ArrayList<Integer>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("netcfg");
            InputStream stdin = proc.getInputStream();
            try {
                InputStreamReader isr = new InputStreamReader(stdin);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("eth0")) {
                        if (line.toLowerCase().contains("up")) {
                            returnValues.add(NetworkType.ETHERNET);
                        }
                    }
                }
                if (wifiManager.isWifiEnabled()) {
                    returnValues.add(NetworkType.WIRELESS);
                }
            } finally {
                stdin.close();
            }
            proc.waitFor();
        } catch (Exception t) {
            t.printStackTrace();
        }
        return returnValues;
    }

    /**
     * Broadcast receiver class used to receive
     * WifiManager.SCAN_RESULTS_AVAILABLE_ACTION system messages. This receiver
     * will trigger when WifiManager collects available wireless networks.
     * 
     * @author Marko Zivanovic
     */
    private class WiFiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context c, Intent intent) {
            scanResults = wifiManager.getScanResults();
            List<ScanResult> tmpScanResults = new ArrayList<ScanResult>();
            boolean addElement;
            for (int i = 0; i < scanResults.size(); i++) {
                addElement = true;
                for (int j = 0; j < tmpScanResults.size(); j++) {
                    if (tmpScanResults.get(j).SSID
                            .equals(scanResults.get(i).SSID)) {
                        addElement = false;
                        break;
                    }
                }
                if (addElement == true) {
                    tmpScanResults.add(scanResults.get(i));
                }
            }
            scanResults = tmpScanResults;
            if (IWEDIAService.DEBUG)
                Log.e(LOG_TAG,
                        "new wireless networks available:" + scanResults.size());
            /**
             * Inform client that new wireless networks are available.
             */
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_SCANNED);
        }
    }

    public List<Object> removeDuplicates(List<ScanResult> l) {
        Set<ScanResult> s = new TreeSet<ScanResult>(
                new Comparator<ScanResult>() {
                    @Override
                    public int compare(ScanResult o1, ScanResult o2) {
                        if (o1.SSID.equals(o2.SSID)) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
        s.addAll(l);
        return Arrays.asList(s.toArray());
    }

    private class WifiStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "action:" + action);
            }
            if (action != null) {
                if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int iTemp = intent.getIntExtra(
                            WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_UNKNOWN);
                    checkState(iTemp);
                } else if (action
                        .equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                    DetailedState state = WifiInfo
                            .getDetailedStateOf((SupplicantState) intent
                            // .getParcelableExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED));
                                    .getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                    changeState(state);
                } else if (action
                        .equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    DetailedState state = ((NetworkInfo) intent
                            .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO))
                            .getDetailedState();
                    changeState(state);
                }
            }
        }
    }

    // TODO: This Should Be Fixed!
    // class WpsSubmitListener implements WifiManager.WpsListener {
    // public void onStartSuccess(String pin) {
    // if (pin != null) {
    // broadcastWpsPin(pin);
    // } else {
    // }
    // }
    //
    // public void onCompletion() {
    // }
    //
    // public void onFailure(int reason) {
    // switch (reason) {
    // case WifiManager.WPS_OVERLAP_ERROR:
    // case WifiManager.WPS_WEP_PROHIBITED:
    // case WifiManager.WPS_TKIP_ONLY_PROHIBITED:
    // case WifiManager.WPS_AUTH_FAILURE: {
    // broadcastWpsStateChange(WpsState.WPS_STATE_ERROR);
    // break;
    // }
    // case WifiManager.WPS_TIMED_OUT: {
    // broadcastWpsStateChange(WpsState.WPS_STATE_TIMED_OUT);
    // break;
    // }
    // default:
    // break;
    // }
    // }
    // }
    // TODO: This Should Be Fixed!
    // class WpsCancelListener implements WifiManager.ActionListener {
    // @Override
    // public void onSuccess() {
    // broadcastWpsStateChange(WpsState.WPS_STATE_CANCEL_SUCCESS);
    // }
    //
    // @Override
    // public void onFailure(int reason) {
    // broadcastWpsStateChange(WpsState.WPS_STATE_CANCEL_ERROR);
    // }
    // }
    private void changeState(DetailedState aState) {
        if (aState == DetailedState.SCANNING) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "SCANNING");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_SCANNING);
        } else if (aState == DetailedState.CONNECTING) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "CONNECTING");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_CONNECTING);
        } else if (aState == DetailedState.OBTAINING_IPADDR) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "OBTAINING_IPADDR");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_OBTAINING_IP_ADDR);
        } else if (aState == DetailedState.CONNECTED) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "CONNECTED");
            }
            setActiveWirelessConnection();
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_CONNECTED);
        } else if (aState == DetailedState.DISCONNECTING) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "DISCONNECTING");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_DISCONNECTING);
        } else if (aState == DetailedState.DISCONNECTED) {
            Log.e(LOG_TAG, "DISCONNECTED");
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_DISCONNECTED);
        } else if (aState == DetailedState.FAILED) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "FAILED");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_FAILED);
        }
    }

    private void setActiveWirelessConnection() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String wirelessNetworkSSID = wifiInfo.getSSID();
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "connected wireless network SSID:"
                    + wirelessNetworkSSID);
        if (scanResults != null)
            for (int i = 0; i < scanResults.size(); i++)
                if (scanResults.get(i).SSID.equals(wirelessNetworkSSID)) {
                    currentlyActiveWirelessNetwork = new WifiScanResult(
                            scanResults.get(i));
                    break;
                }
    }

    public void checkState(int aInt) {
        if (aInt == WifiManager.WIFI_STATE_ENABLING) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "WIFI_STATE_ENABLING");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_ENABLING);
        } else if (aInt == WifiManager.WIFI_STATE_ENABLED) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "WIFI_STATE_ENABLED");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_ENABLED);
        } else if (aInt == WifiManager.WIFI_STATE_DISABLING) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "WIFI_STATE_DISABLING");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_DISABLING);
        } else if (aInt == WifiManager.WIFI_STATE_DISABLED) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "WIFI_STATE_DISABLED");
            }
            broadcastWirelessStateChange(WirelessState.WIRELESS_STATE_DISABLED);
        }
    }

    public String getScanResultSecurity(ScanResult scanResult) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "* getScanResultSecurity");
        }
        final String cap = scanResult.capabilities;
        final String[] securityModes = { "WEP", "PSK", "EAP" };
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (IWEDIAService.DEBUG)
                Log.e(LOG_TAG, "scanResult SSID:" + scanResult.SSID
                        + " scanResult.capabilities:" + scanResult.capabilities);
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }
        return "OPEN";
    }

    public Boolean connectToWirelessNetwork(String mSecurity, String mssid,
            String MBSSID, String mkey, Context context) {
        // create a new WifiConfiguration
        WifiConfiguration wcg = new WifiConfiguration();
        wcg.BSSID = MBSSID;
        // SSID and preSharedKey must add double quotes, otherwise will cause
        // the connection to fail
        // wcg.SSID = "\" " + mssid + " \"";
        wcg.SSID = "\"" + mssid + "\"";
        wcg.hiddenSSID = false;
        wcg.status = WifiConfiguration.Status.ENABLED;
        // wcg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        // wcg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        // wcg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        // wcg.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        // wcg.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        // wcg.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "connect to network:" + wcg.SSID
                    + " with security type:" + mSecurity);
        // If the encryption mode to WEP
        if (mSecurity.equals("WEP")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wcg.wepKeys[0] = "\"" + mkey + "\""; // This is the WEP Password
            wcg.wepTxKeyIndex = 0;
        }
        // If the encryption mode WPA PSK
        else if (mSecurity.equals("WPA PSK")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.SHARED);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.Status.);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.OPEN);
            // wcg.preSharedKey = "\" " + mkey + " \"";
            wcg.preSharedKey = "\"" + mkey + "\"";
        }
        // If the encryption mode WPA PSK
        else if (mSecurity.equals("WPA2 PSK")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.SHARED);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.Status.);
            // wcg.allowedGroupCiphers.set(WifiConfiguration.AuthAlgorithm.OPEN);
            // wcg.preSharedKey = "\" " + mkey + " \"";
            wcg.preSharedKey = "\"" + mkey + "\"";
        }
        // if encryption mode the WPA EPA
        else if (mSecurity.equals("WPA EAP")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
            wcg.preSharedKey = "\"" + mkey + "\"";
        }
        // encryption the
        else if (mSecurity.equals("NONE")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "connecting to wireless/password:"
                    + wcg.preSharedKey);
        int res = wifiManager.addNetwork(wcg);
        // int res = wifiManager.updateNetwork(wcg);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "mWifiManager.addNetwork:" + res);
        }
        wifiManager.saveConfiguration();
        // Settings.Secure.putString(context.getContentResolver(), "wifi" +
        // mssid,
        // mkey);
        return wifiManager.enableNetwork(res, true);
    }

    public Boolean connectToHiddenWirelessNetwork(
            WifiAddHiddenNetwork hiddenNetwork) {
        // create a new WifiConfiguration
        WifiConfiguration wcg = new WifiConfiguration();
        wcg.SSID = "\"" + hiddenNetwork.getSSID() + "\"";
        wcg.hiddenSSID = true;
        wcg.status = WifiConfiguration.Status.ENABLED;
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "connect to network:" + wcg.SSID
                    + " with security type:" + hiddenNetwork.getCapabilities());
        // If the encryption mode to WEP
        if (hiddenNetwork.getCapabilities().equals("WEP")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wcg.wepKeys[0] = "\"" + hiddenNetwork.getPassword() + "\""; // This
            // is
            // the
            // WEP
            // Password
            wcg.wepTxKeyIndex = 0;
        }
        // If the encryption mode WPA PSK
        else if (hiddenNetwork.getCapabilities().equals("WPA PSK")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wcg.preSharedKey = "\"" + hiddenNetwork.getPassword() + "\"";
        }
        // encryption the
        else if (hiddenNetwork.getCapabilities().equals("NONE")) {
            wcg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "connecting to wireless/password:"
                    + wcg.preSharedKey);
        int res = wifiManager.addNetwork(wcg);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "mWifiManager.addNetwork:" + res);
        }
        Boolean retVal;
        wifiManager.saveConfiguration();
        retVal = wifiManager.enableNetwork(res, true);
        return retVal;
    }

    /**
     * Returns Link speed.
     */
    public String getLinkSpeed() throws RemoteException {
        WifiInfo wi = wifiManager.getConnectionInfo();
        return (wi.getLinkSpeed() + " Mbps");
    }

    /**
     * Sets WPS PIN connection method as registrar
     */
    public void startWpsPinRegistrar(String pin, String BSSID) {
        // TODO: This Should Be Fixed!
        // WpsInfo config = new WpsInfo();
        // config.setup = WpsInfo.KEYPAD;
        // config.pin = pin;
        // config.BSSID = BSSID;
        // if (MethodExists(wifiManager, "startWps")) {
        // Method startWpsMethod = GetRemoteMethod(wifiManager, "startWps");
        // if (startWpsMethod.getParameterTypes().length == 2)
        // try {
        // startWpsMethod.invoke(wifiManager, config,
        // wpsSubmitListener);
        // } catch (IllegalArgumentException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (IllegalAccessException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (InvocationTargetException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // else if (startWpsMethod.getParameterTypes().length == 3)
        // try {
        // startWpsMethod.invoke(wifiManager, channel, config,
        // wpsSubmitListener);
        // } catch (IllegalArgumentException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IllegalAccessException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (InvocationTargetException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // else
        // Log.e(LOG_TAG, "Unknown API, unable to call startWps().");
        // }
    }

    /**
     * Sets WPS PIN connection method as enrollee
     */
    public void startWpsPinEnrollee() {
        // TODO: This Should Be Fixed!
        // WpsInfo config = new WpsInfo();
        // config.setup = WpsInfo.DISPLAY;
        // if (MethodExists(wifiManager, "startWps")) {
        // Method startWpsMethod = GetRemoteMethod(wifiManager, "startWps");
        // if (startWpsMethod.getParameterTypes().length == 2)
        // try {
        // startWpsMethod.invoke(wifiManager, config,
        // wpsSubmitListener);
        // } catch (IllegalArgumentException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (IllegalAccessException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (InvocationTargetException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // else if (startWpsMethod.getParameterTypes().length == 3)
        // try {
        // startWpsMethod.invoke(wifiManager, channel, config,
        // wpsSubmitListener);
        // } catch (IllegalArgumentException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IllegalAccessException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (InvocationTargetException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // else
        // Log.e(LOG_TAG, "Unknown API, unable to call startWps().");
        // }
    }

    /**
     * Sets WPS PBC connection method
     */
    public void startWpsPbc() {
        // TODO: This Should Be Fixed!
        // Log.e(LOG_TAG, "startWpsPbc: ");
        // WpsInfo config = new WpsInfo();
        // config.setup = WpsInfo.PBC;
        // if (MethodExists(wifiManager, "startWps")) {
        // Method startWpsMethod = GetRemoteMethod(wifiManager, "startWps");
        // if (startWpsMethod.getParameterTypes().length == 2)
        // try {
        // startWpsMethod.invoke(wifiManager, config,
        // wpsSubmitListener);
        // } catch (IllegalArgumentException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (IllegalAccessException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (InvocationTargetException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // else if (startWpsMethod.getParameterTypes().length == 3)
        // try {
        // startWpsMethod.invoke(wifiManager, channel, config,
        // wpsSubmitListener);
        // } catch (IllegalArgumentException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IllegalAccessException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (InvocationTargetException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // else
        // Log.e(LOG_TAG, "Unknown API, unable to call startWps().");
        // }
    }

    /**
     * Cancels current WPS operation
     */
    public void cancelWps() {
        // TODO: This Should Be Fixed!
        // Log.e(LOG_TAG, "cancelWps: ");
        // if (MethodExists(wifiManager, "cancelWps")) {
        // Method cancelWpsMethod = GetRemoteMethod(wifiManager, "cancelWps");
        // if (cancelWpsMethod.getParameterTypes().length == 1)
        // try {
        // cancelWpsMethod.invoke(wifiManager, wpsCancelListener);
        // } catch (IllegalArgumentException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (IllegalAccessException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // } catch (InvocationTargetException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }
        // else if (cancelWpsMethod.getParameterTypes().length == 2)
        // try {
        // cancelWpsMethod.invoke(wifiManager, channel,
        // wpsCancelListener);
        // } catch (IllegalArgumentException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IllegalAccessException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (InvocationTargetException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // else
        // Log.e(LOG_TAG, "Unknown API, unable to call startWps().");
        // }
    }

    /**
     * Inform client that new wireless networks are available.
     */
    private void broadcastWirelessStateChange(int state) {
        if (callbacks != null)
            try {
                callbacks.wirelessNetworksChanged(state);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Inform client that WPS PIN is generated
     */
    private void broadcastWpsPin(String pin) {
        if (callbacks != null)
            try {
                callbacks.wpsPinObtained(pin);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Inform client that WPS state is changed
     */
    private void broadcastWpsStateChange(int state) {
        if (callbacks != null)
            try {
                callbacks.wpsStateChanged(state);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void setProxySettings(String assign,
            WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {
        setEnumField(wifiConf, assign, "proxySettings");
    }

    public WifiConfiguration GetCurrentWifiConfiguration() {
        if (!wifiManager.isWifiEnabled()) {
            return null;
        }
        List<WifiConfiguration> configurationList = wifiManager
                .getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = wifiManager.getConnectionInfo().getNetworkId();
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur) {
                configuration = wifiConfiguration;
            }
        }
        return configuration;
    }

    public void setWifiProxySettings(String proxy, String port) {
        WifiConfiguration config = GetCurrentWifiConfiguration();
        if (null == config) {
            return;
        }
        try {
            // get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties) {
                return;
            }
            // get the setHttpProxy method for LinkProperties
            Class proxyPropertiesClass = Class
                    .forName("android.net.ProxyProperties");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy",
                    setHttpProxyParams);
            setHttpProxy.setAccessible(true);
            // get ProxyProperties constructor
            Class[] proxyPropertiesCtorParamTypes = new Class[3];
            proxyPropertiesCtorParamTypes[0] = String.class;
            proxyPropertiesCtorParamTypes[1] = int.class;
            proxyPropertiesCtorParamTypes[2] = String.class;
            Constructor proxyPropertiesCtor = proxyPropertiesClass
                    .getConstructor(proxyPropertiesCtorParamTypes);
            // create the parameters for the constructor
            Object[] proxyPropertiesCtorParams = new Object[3];
            proxyPropertiesCtorParams[0] = proxy;
            proxyPropertiesCtorParams[1] = Integer.parseInt(port);
            proxyPropertiesCtorParams[2] = null;
            // create a new object using the params
            Object proxySettings = proxyPropertiesCtor
                    .newInstance(proxyPropertiesCtorParams);
            // pass the new object to setHttpProxy
            Object[] params = new Object[1];
            params[0] = proxySettings;
            setHttpProxy.invoke(linkProperties, params);
            setProxySettings("STATIC", config);
            // save the settings
            wifiManager.updateNetwork(config);
            wifiManager.saveConfiguration();
            wifiManager.disconnect();
            wifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsetWifiProxySettings() {
        WifiConfiguration config = GetCurrentWifiConfiguration();
        if (null == config) {
            return;
        }
        try {
            // get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties) {
                return;
            }
            // get the setHttpProxy method for LinkProperties
            Class proxyPropertiesClass = Class
                    .forName("android.net.ProxyProperties");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy",
                    setHttpProxyParams);
            setHttpProxy.setAccessible(true);
            // pass null as the proxy
            Object[] params = new Object[1];
            params[0] = null;
            setHttpProxy.invoke(linkProperties, params);
            setProxySettings("NONE", config);
            // save the config
            wifiManager.updateNetwork(config);
            wifiManager.saveConfiguration();
            wifiManager.disconnect();
            wifiManager.reconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIpAssignment(String assign) {
        WifiConfiguration config = GetCurrentWifiConfiguration();
        try {
            setEnumField(config, assign, "ipAssignment");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        wifiManager.updateNetwork(config);
        wifiManager.saveConfiguration();
    }

    public void setIpAddress(String IP, int prefixLength) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        WifiConfiguration wifiConf = GetCurrentWifiConfiguration();
        Object linkProperties = null;
        try {
            linkProperties = getField(wifiConf, "linkProperties");
            if (linkProperties == null) {
                return;
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Class laClass = null;
        try {
            laClass = Class.forName("android.net.LinkAddress");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Constructor laConstructor = null;
        try {
            laConstructor = laClass.getConstructor(new Class[] {
                    InetAddress.class, int.class });
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Object linkAddress = null;
        try {
            linkAddress = laConstructor.newInstance(addr, prefixLength);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList mLinkAddresses = null;
        try {
            mLinkAddresses = (ArrayList) getDeclaredField(linkProperties,
                    "mLinkAddresses");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
        wifiManager.updateNetwork(wifiConf);
        wifiManager.saveConfiguration();
    }

    public void setGateway(String IP) {
        InetAddress gateway = null;
        try {
            gateway = InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        WifiConfiguration wifiConf = GetCurrentWifiConfiguration();
        Object linkProperties = null;
        try {
            linkProperties = getField(wifiConf, "linkProperties");
            if (linkProperties == null) {
                return;
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Class routeInfoClass = null;
        try {
            routeInfoClass = Class.forName("android.net.RouteInfo");
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Constructor routeInfoConstructor = null;
        try {
            routeInfoConstructor = routeInfoClass
                    .getConstructor(new Class[] { InetAddress.class });
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Object routeInfo = null;
        try {
            routeInfo = routeInfoConstructor.newInstance(gateway);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList mRoutes = null;
        try {
            mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mRoutes.clear();
        mRoutes.add(routeInfo);
        wifiManager.updateNetwork(wifiConf);
        wifiManager.saveConfiguration();
    }

    public void setDNS(String IP) {
        InetAddress dns = null;
        Object linkProperties = new Object();
        try {
            dns = InetAddress.getByName(IP);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        WifiConfiguration wifiConf = GetCurrentWifiConfiguration();
        try {
            linkProperties = getField(wifiConf, "linkProperties");
            if (linkProperties == null) {
                return;
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList<InetAddress> mDnses;
        try {
            mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties,
                    "mDnses");
            mDnses.clear();
            mDnses.add(dns);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        wifiManager.updateNetwork(wifiConf);
        wifiManager.saveConfiguration();
    }

    public void setStaticIP(String assigment, String ip, String dns,
            String gateway, String prefix_len) {
        Integer networkPrefixLength = Integer.parseInt(prefix_len);
        setIpAssignment(assigment);
        setDNS(dns);
        setGateway(gateway);
        setIpAddress(ip, networkPrefixLength);
        wifiManager.disconnect();
        wifiManager.reconnect();
    }

    public DhcpInfo getIP() {
        wifiManager = (WifiManager) IWEDIAService.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        DhcpInfo currentIP = wifiManager.getDhcpInfo();
        return currentIP;
    }

    public boolean isDHCPactive() {
        // TODO: This Should Be Fixed!
        // WifiConfiguration config = GetCurrentWifiConfiguration();
        // if (config.ipAssignment == IpAssignment.DHCP) {
        // return true;
        // } else {
        // return false;
        // }
        return false;
    }

    public void enableDHCP() {
        setIpAssignment("DHCP");
        wifiManager.disconnect();
        wifiManager.reconnect();
    }

    public void startSoftAP(String SSID, String password, boolean desiredState) {
        wifiManager = (WifiManager) IWEDIAService.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager conectivityManager = (ConnectivityManager) IWEDIAService
                .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        Method tetheringMethods;
        Method setWifiApEnabledMethod;
        if (desiredState && !getSoftAPState()) {
            Log.e(LOG_TAG, "Trying to enable SoftAP");
            // disable wifi before starting SoftAP
            wifiManager.setWifiEnabled(false);
            // enable ethernet before tethering
            // to be done...
            // start SoftAP
            if (MethodExists(wifiManager, "setWifiApEnabled")) {
                setWifiApEnabledMethod = GetRemoteMethod(wifiManager,
                        "setWifiApEnabled");
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = SSID;
                netConfig.preSharedKey = password;
                netConfig.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.WPA_PSK);
                try {
                    setWifiApEnabledMethod.invoke(wifiManager, netConfig, true);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (MethodExists(conectivityManager,
                    "enforceTetherAccessPermission")) {
                tetheringMethods = GetRemoteMethod(conectivityManager,
                        "enforceTetherAccessPermission");
                try {
                    tetheringMethods.invoke(conectivityManager);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (MethodExists(conectivityManager, "getTetherableIfaces")) {
                tetheringMethods = GetRemoteMethod(conectivityManager,
                        "getTetherableIfaces");
                try {
                    String[] tetherIfaces = (String[]) tetheringMethods
                            .invoke(conectivityManager);
                    for (String iface : tetherIfaces) {
                        Log.d(LOG_TAG, "Tethering is available on " + iface);
                    }
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (MethodExists(conectivityManager, "isTetheringSupported")) {
                tetheringMethods = GetRemoteMethod(conectivityManager,
                        "isTetheringSupported");
                try {
                    boolean supported = (Boolean) tetheringMethods
                            .invoke(conectivityManager);
                    Log.d(LOG_TAG, "Tethering is supported: "
                            + (supported ? "yes" : "no"));
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (MethodExists(conectivityManager, "tether")) {
                tetheringMethods = GetRemoteMethod(conectivityManager, "tether");
                try {
                    int result = (Integer) tetheringMethods.invoke(
                            conectivityManager, "eth0");
                    Log.d(LOG_TAG, "Tether() on eth0 returns: " + result);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else if (!desiredState && getSoftAPState()) {
            Log.e(LOG_TAG, "Trying to disable SoftAP");
            if (MethodExists(conectivityManager, "untether")) {
                tetheringMethods = GetRemoteMethod(conectivityManager,
                        "untether");
                try {
                    int result = (Integer) tetheringMethods.invoke(
                            conectivityManager, "eth0");
                    Log.d(LOG_TAG, "Untether() on eth0 returns: " + result);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (MethodExists(wifiManager, "setWifiApEnabled")) {
                setWifiApEnabledMethod = GetRemoteMethod(wifiManager,
                        "setWifiApEnabled");
                try {
                    Log.d(LOG_TAG, "disableMobileAP try: ");
                    setWifiApEnabledMethod.invoke(wifiManager, null, false);
                    Log.d(LOG_TAG, "disableMobileAP done: ");
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            Log.e(LOG_TAG, "SoftAP is already in desired state:" + desiredState);
        }
    }

    public boolean getSoftAPState() {
        wifiManager = (WifiManager) IWEDIAService.getInstance()
                .getSystemService(Context.WIFI_SERVICE);
        if (MethodExists(wifiManager, "getWifiApState")) {
            Method getWifiApStateMethod = GetRemoteMethod(wifiManager,
                    "getWifiApState");
            // TODO: This Should Be Fixed!
            // try {
            // int wifiApState = (Integer) getWifiApStateMethod
            // .invoke(wifiManager);
            // if ((wifiApState == WifiManager.WIFI_AP_STATE_ENABLING)
            // || (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED)) {
            // Log.e(LOG_TAG, "SoftAP is enabled");
            // return true;
            // } else {
            // Log.e(LOG_TAG, "SoftAP is disabled");
            // return false;
            // }
            // } catch (IllegalArgumentException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // } catch (IllegalAccessException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // } catch (InvocationTargetException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
        }
        return false;
    }
}
