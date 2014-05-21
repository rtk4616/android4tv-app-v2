package com.iwedia.comm.system.about;

import com.iwedia.dtv.swupdate.SWVersionType;
import com.iwedia.comm.system.about.ISoftwareUpdate;

/**
 * The device info. Provides basic information about the device and the software
 * on it.
 *
 * @author Stanislava Markovic
 *
 */

interface IAbout {

	/**
	 * Returns the softer update controller.
	 *
	 * @return {@link com.iwedia.comm.system.about.ISoftwareUpdate}
	 */
	ISoftwareUpdate getSoftwareUpdate();

	/**
	 * Gets local IP address.
	 *
	 * @return local IP address.
	 */
	String getIPAddress();

	/**
	 * Gets MAC address.
	 *
	 * @return MAC address.
	 */
	String getMacAddress();

	/**
	 * Gets model number.
	 *
	 * @return model number.
	 */
	String getModelNumber();

	/**
	 * Gets Android version.
	 *
	 * @return Android version.
	 */
	String getAndroidVersion();

	/**
	 * Gets kernel version.
	 *
	 * @return kernel version.
	 */
	String getKernelVersion();

	/**
	 * Gets build number.
	 *
	 * @return build number.
	 */
	String getBuildNumber();

	/**
	 * Gets software version.
	 *
	 * @return software version.
	 */
	String getSWVersion(in SWVersionType swVersionType);

	/**
	 * Erases all data on device and sets the factory default values.
	 *
	 * @return true if data is successfully erased, otherwise false.
	 */
	boolean factoryReset();

}