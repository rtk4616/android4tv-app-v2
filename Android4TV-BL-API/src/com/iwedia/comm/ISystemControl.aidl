package com.iwedia.comm;

import com.iwedia.comm.system.INetworkSettings;
import com.iwedia.comm.system.IPictureSettings;
import com.iwedia.comm.system.ISoundSettings;
import com.iwedia.comm.system.date_time.IDateTimeSettings;
import com.iwedia.comm.system.language_and_keyboard.ILanguageKeyboardSettings;
import com.iwedia.comm.system.IInputSettings;
import com.iwedia.comm.system.application.IApplicationSettings;
import com.iwedia.comm.system.application.IApplicationRestart;
import com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings;
import com.iwedia.comm.system.account.IAccountSyncSettings;
import com.iwedia.comm.system.IVoiceInputOutputSettings;
import com.iwedia.comm.system.about.IAbout;
import com.iwedia.comm.IActionCallback;
import com.iwedia.comm.system.ICurlSettings;


/**
 *The system settings controller. Provides Android and TV related settings
 *
 *@author Stanislava Markovic
 *
 */

interface ISystemControl {

	/**
	 * Returns basic Android network settings.
	 *
	 * @return {@link com.iwedia.comm.system.INetworkSettings}
	 */
	INetworkSettings getNetworkControl();

	/**
	 * Returns picture settings.
	 *
	 * @return {@link com.iwedia.comm.system.IPictureSettings}
	 */
	IPictureSettings getPictureControl();

	/**
	 * Returns sound settings;
	 *
	 * @return {@link com.iwedia.comm.system.ISoundSettings}
	 */
	ISoundSettings getSoundControl();

	/**
	 * Returns date and time settings.
	 *
	 * @return {@link com.iwedia.comm.system.date_time.IDateTimeSettings}
	 */
	IDateTimeSettings getDateAndTimeControl();

	/**
	 * Returns language and keyboard settings.
	 *
	 * @return {@link com.iwedia.comm.system.language_and_keyboard.ILanguageKeyboardSettings}
	 */
	ILanguageKeyboardSettings getLanguageAndKeyboardControl();

	/**
	 * Returns input settings.
	 *
	 * @return {@link com.iwedia.comm.system.IInputSettings}
	 */
	IInputSettings getInputControl();

	/**
	 * Returns application settings;
	 *
	 * @return {@link com.iwedia.comm.system.application.IApplicationSettings}
	 */
	IApplicationSettings getApplicationControl();

	/**
	 * Returns application settings;
	 *
	 * @return {@link com.iwedia.comm.system.application.IApplicationRestart}
	 */
	IApplicationRestart getApplicationRestart();

	/**
	 * Returns external and local storage settings;
	 *
	 * @return {@link com.iwedia.comm.system.external_and_local_storage.IExternalLocalStorageSettings}
	 */
	IExternalLocalStorageSettings getExternalLocalStorageControl();

	/**
	 * Returns accounts and sync settings;
	 *
	 * @return {@link com.iwedia.comm.system.IAccountSyncSettings}
	 */
	IAccountSyncSettings getAccountSyncControl();

	/**
	 * Returns voice input and output settings;
	 *
	 * @return {@link com.iwedia.comm.system.IVoiceInputOutputSettings}
	 */
	IVoiceInputOutputSettings getVoiceInputOutputControl();

	/**
	 * Returns device info;
	 *
	 * @return {@link com.iwedia.comm.system.about.IAbout}
	 */
	IAbout getAbout();

	/**
	 * Returns Curl effect settings
	 *
	 * @return {@link com.iwedia.comm.system.ICurlSettings}
	 */
	ICurlSettings getCurlSettings();

	/**
	 * Register action callback;
	 *
	 * @param {@link com.iwedia.comm.IActionCallbacks}
	 */
	void registerActionCallback(IActionCallback actionCallback);
}
