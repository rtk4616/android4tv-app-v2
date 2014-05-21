package com.iwedia.comm.system.about;

/**
 * Interface which handles the connection to the native 'stbmonitord' daemon.
 * All services which are supported by the daemon are exported with the methods
 * of this class.
 * @author Stanislava Markovic
 *
 */

interface ISoftwareUpdate{

	/**
	 * Returns FW version as a string in major.minor.revision format.
	 * @return FW version, or empty string if there was an error.
	 */
	 String getRunnungVersion();

	/**
	 * Executes FW upgrade. Bear in mind that this method should <b>never</b> return!
	 */
	 void upgrade();

	/**
	 * Check whether there is available FW upgrade. This call is asynchronous (in sense that there is no
	 * return value), and caller should expect appropriate event (if there is one). It is synchronous in
	 * sense that it WAITS for a connection to be established.
	 */
	 void upgradeCheck();

	/**
	 * Stops the connection to the daemon. This method should be called whenever application does not need
	 * the connection any more (e.g. exiting the application).
	 */
	 void stopConnection();

	/**
	 * Executes USB FW upgrade. Bear in mind that this method should <b>never</b> return!
	 */
	 void finishUSBUpgrade();

	 /**
	 * Check whether there is available FW upgrade over USB.
	 */
	 void copyUpgradeFWFromUSB();
}