package com.iwedia.comm.system;

import com.iwedia.comm.system.INetworkCallback;
import com.iwedia.comm.system.WifiScanResult;
import com.iwedia.comm.system.WifiAddHiddenNetwork;
import android.net.DhcpInfo;

interface INetworkSettings {

	/**
	 * Returns active network type
	 */
	int getActiveNetworkType();

	/**
	 * Set active network type;
	 */
	boolean setActiveNetworkType(in int networkType);

	/**
	 * Get MAC address of wireless adapter;
	 */
	String getWirelessMacAddress();

	/**
	 * Get MAC address of ethernet adapter;
	 */
	String getEthernetMacAddress();

	/**
	 * Get number of available wireless networks;
	 */
	int getNumberOfAvailableWirelessNetworks();

	/**
	 * Returns wireless network at specific position;
	 */
	WifiScanResult getWirelessNetwork(in int index);

	/**
	 * Set active wireless network by specific position;
	 */
	boolean setActiveWirelessNetwork(in WifiScanResult scanResult, String password);

	/**
	 * Set hidden wireless network;
	 */
	boolean setHiddenWirelessNetwork(in WifiAddHiddenNetwork network);

	/**
	* Returns currently active wireless network.
	*/
	WifiScanResult getActiveWirelessNetwork();

	/**
	* Disconnects currently active wireless network.
	*/
	void disconnectActiveWirelessNetwork();

	/**
	 * Returns Link speed.
	 */
	String getLinkSpeed();

	/**
	 * Sets WPS PIN connection method as enrolee
	 */
	void startWpsPinEnrollee();

	/**
	 * Sets WPS PIN connection method as registrar
	 */
	void startWpsPinRegistrar(String pin, String BSSID);

	/**
	 * Sets WPS PBC connection method
	 */
	void startWpsPbc();

	/**
	 * Cancel current WPS operation
	 */
	void cancelWps();

	/**
	 * Register client for INetworkCallback;
	 */
	void registerCallback(INetworkCallback callback);

	/**
	 * Unregister client for INetworkCallback;
	 */
	void unregisteCallback();

	boolean isDHCPactive();
 	void enableDHCP(); 
	void setWifiProxySettings(String url, String port);
	void unsetWifiProxySettings();
 	void setStaticIP(String assigment, String ip, String dns, String gateway, String prefix_len);
 	DhcpInfo getIP();
 	void startSoftAP(String SSID, String password, boolean enabled);
 	boolean getSoftAPState();
}