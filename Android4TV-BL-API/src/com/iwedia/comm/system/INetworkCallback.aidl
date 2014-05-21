package com.iwedia.comm.system;

/**
 * Network settings callback functions.
 *
 * @author Marko Zivanovic
 *
 */
interface INetworkCallback {

	/**
	 * Progress changed.
	 */
	void progressChanged(int value);

	/**
	 * Test finished.
	 */
	void testFinished();

	/**
	 * Network type changed.
	 */
	void networkTypeChanged(int type);

	/**
	 * Connection time changed.
	 */
	void connectionTimeChanged(int time);

	/**
	 * Download speed changed.
	 */
	void downloadSpeed(double speed);

	/**
	 * Informs client about wireless state change.
	 */
	void wirelessNetworksChanged(int state);

	/**
	 * Informs client about WPS Registrar PIN arrival.
	 */
	void wpsPinObtained(String pin);

	/**
	 * Informs client about WPS state change.
	 */
	void wpsStateChanged(int state);
}