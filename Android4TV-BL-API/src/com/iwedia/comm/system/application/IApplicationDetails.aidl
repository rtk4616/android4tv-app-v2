package com.iwedia.comm.system.application;

import com.iwedia.comm.system.application.AppSizeInfo;
import com.iwedia.comm.system.application.AppPermission;
import java.util.List;

/**
 * This interface manages application details and settings.
 *
 * @author stanislava
 *
 */

interface IApplicationDetails {

	/**
	 * Force stop an application with the given package name (packageName).
	 *
	 * @return true if everything is OK, else false
	 */
	boolean forceStop();

	/**
	 * Uninstall the downloaded application with the given package name.
	 *
	 * @return true if everything is OK, else false.
	 */
	boolean uninstall();

	/**
	 * Enable or disable built-in applications. If application is enabled, this
	 * function disables application with the given package (packageName), else
	 * enables.
	 *
	 * @return true if everything is OK, else false.
	 */
	boolean enable();

	/**
	 * Delete application user data - all files, accounts, databases etc.
	 */
	void clearData();

	boolean move();

	/**
	 * Delete application cache files.
	 */
	void clearCache();

	/**
	 * Clear application's default actions.
	 *
	 * @return true if everything is OK, else false.
	 */
	boolean clearDefaults();

	/**
	 * Gets application size information - application (code) size, data size,
	 * cache size, external data size etc, external cache size etc.
	 */
	void getAppSizeInfo();

	/**
	 * Check if is application with the given package name (packageName)
	 * built-in or downloaded.
	 *
	 * @return true if an application is built-in, else false.
	 */
	boolean isSystem();

	/**
	 * Check if is built-in application with the given package name
	 * (packageName) enabled or disabled.
	 *
	 * @return true if application is enabled, else false.
	 */
	boolean isEnabled();

	/**
	 * Check if the application with the given package name (packageName) is
	 * stopped.
	 *
	 * @return true if application is stopped, else false.
	 */
	boolean isStopped();

	/**
	 * Check if is application with the given package name (packageName) set to
	 * open by default for some actions.
	 *
	 * @return true if is application with the given package name (packageName)
	 *         default application, otherwise false.
	 */
	boolean isDefault();

	/**
	 * Gets list of application permissions.
	 *
	 * @return list of application permissions.
	 */
	List<AppPermission> getAppPermissions();

}