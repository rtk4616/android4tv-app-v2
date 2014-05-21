package com.iwedia.comm;

import java.util.List;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.dtv.types.TimerRepeatMode;
import com.iwedia.dtv.setup.OffSignalTimerEvent;
import com.iwedia.comm.ISetupCallback;

 /** The setup controller. Sets MW settings.
 *
 *  	@author Marko Zivanovic
 *
 */
interface ISetupControl{

 	/** Gets active country. */
	int getActiveCountry();

	/** Gets the country count*/
	int getCountryCount();

	/** Gets country name for the given index */
	String getCountry(int index);

	/** Sets active country. */
	 boolean setCountry(int country);

	/** Gets country code for the given index */
	String getCountryCode(int index);

 	/** Gets the language count (for subtitle and audio). */
	int getLanguageCount();

	/** Gets language name for the given language index */
	String getLanguageName(int languageIndex);

	boolean setTimeZone(int minutes);

	int getTimeZone();

	/** Set off timer. */
	boolean setOffTimer(in TimeDate time);
	
	/** Start off timer */
	boolean startOffTimer();
	
	/** end off timer */
	boolean endOffTimer();

	/** Set off timer. */
	String getOffTimer();

	/** Set off timer repeat mode. */
	boolean setOffTimerRepeat(in TimerRepeatMode repeatMode);

	/** Get off timer repeat mode. */
	TimerRepeatMode getOffTimerRepeat();

	/** Set timer for no operation off. */
	boolean setNoOperationOff(boolean noOperation);

	/** Get timer for no operation off. */
	boolean getNoOperationOff();

	/** Set timer for no signal off. */
	boolean setNoSignalOff(boolean noSignal);

	/** Get timer for no signal off. */
	boolean getNoSignalOff();

	/** Set on timer. */
	boolean setOnTimer(in TimeDate time);
	
	/** Start on timer */
	boolean startOnTimer();
	
	/** end on timer */
	boolean endOnTimer();

	/** Get on timer. */
	String getOnTimer();

	/** Set on timer repeat mode. */
	boolean setOnTimerRepeat(in TimerRepeatMode repeatMode);

	/** Get on timer repeat mode. */
	TimerRepeatMode getOnTimerRepeat();

	/**Set event and start No Signal Off Timer */
	boolean offSignalTimerStatusUpdate(in OffSignalTimerEvent event);

	/** Start No Operation Off Timer*/
	boolean offOperationTimerStart();

	/** Set default values for timers settings. */
	boolean resetTimersSettings();

	boolean rebootTV();

	/** Set factory Mode*/
	boolean factoryMode(boolean mode);

	/** Save control array to USB */
	boolean saveSettingsToUSB(String mediaPath);
	
	/** Load control array from USB */
	boolean loadSettingsFromUSB(String mediaPath);
	
	/** Reset settings to default values in Store Mode */
	boolean resetSettingsInStoreMode();
	
	void registerCallback(ISetupCallback callback);
	
	void unregisterCallback(ISetupCallback callback);
 }