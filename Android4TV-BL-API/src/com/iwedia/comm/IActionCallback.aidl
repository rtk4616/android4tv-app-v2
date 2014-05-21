package com.iwedia.comm;

import com.iwedia.comm.system.application.AppSizeInfo;

/**
 * This interface contains declarations of functions that cause the appropriate
 * action to be taken in GUI applications.
 *
 * @author Marko Zivanovic
 *
 */
interface IActionCallback {

	/**
	 * Informs the user that the teletext needs to be shown.
	 */
	void showTeletext();

	/**
	 * Informs the user that the teletext needs to be closed.
	 */
	void hideTeletext();

	/**
	 * Informs the user that the teletext needs to be refreshed.
	 */
	void invalidateTeletext();

	/**
	 * Informs the user that the subtitle needs to be shown.
	 */
	void showSubtitle();

	/**
	 * Informs the user that the subtitle needs to be closed.
	 */
	void hideSubtitle();

	/**
	 * Informs the user that the subtitle needs to be refreshed.
	 *
	 * @param value
	 *            - width of subtitle region.
	 */
	void invalidateSubtitle(int value);

	/**
	 * Informs the user that the mheg needs to be refreshed.
	 */
	void invalidateMheg();

	/**
	 * Informs the user that the mheg needs to be shown.
	 */
	void showMheg();

	/**
	 * Informs the user that the mheg needs to be closed.
	 */
	void hideMheg();

	/**
	 * Informs the user that antenna is connected or disconnected.
	 *
	 * @param state
	 *            - true if antenna is connected, otherwise false.
	 */
	void mhegStarted(boolean state);

	/**
	 * Returns information about the requested application.
	 *
	 * @return {@link com.iwedia.comm.system.application.AppSizeInfo}
	 */
	void getAppSizeInfo(in AppSizeInfo appSizeInfo);

	/**
	 * Informs the user that the uninstallation operation of 3rd party
	 * application has been finished.
	 */
	void uninstallFinished();

	/**
	 * Informs the user that data or cache is cleared.
	 */
	void clearDataCacheFinished(boolean isSucceeded);

	/**
	 * FW Update available event.
	 */
	void updateEvent(String version);

	/**
	 * Error event (some action failed). It is accompanied y the error message.
	 */
	void errorEvent(String err);

	/**
	 * FW Update not available event.
	 */
	void noUpdateEvent(String msg);

	/**
	 * FW USB Update available event.
	 */
	void usbUpdateEvent(String msg);

	/**
	 * FW USB check update event.
	 */
	void usbCheckUpdateEvent(int msgType, String msg);

	/**
	 * Informs the user that syncing account is started.
	 */
	void syncStarted();

	/**
	 * Informs the user that syncing account is finished.
	 */
	void syncFinished();

	/**
	 * Informs the user that media is mounted.
	 */
	void mediaMounted(String path);

	/**
	 * Informs the user that media is ejected.
	 */
	void mediaEjected(String path);

	/**
	 * Informs the user that media is unmountable.
	 */
	void mediaNotSupported(String path);

	void startUrl(String url);


}