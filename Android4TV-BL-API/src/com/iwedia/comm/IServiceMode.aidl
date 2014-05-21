package com.iwedia.comm;

import java.util.List;



 /** Service mode functions
 *
 *  	@author
 *
 */
interface IServiceMode{

	/**
	* Returns the number of audio tracks.
	*
	* @return number of available audio tracks.
	*/
	int getMaxVolume();

	boolean setMaxVolume(int maxVolume);

	boolean getVolumeFixed();

	boolean setVolumeFixed(boolean volumeFixed);

	int getVolumeFixedLevel();

	boolean setVolumeFixedLevel(int volumeFixedLevel);

	boolean getRCButton();

	boolean setRCButton(boolean RCButton);

	boolean getPanelButton();

	boolean setPanelButton(boolean RCButton);

	boolean getMenuButton();

	boolean setMenuButton(boolean RCButton);

	int getInputModeStart();

	boolean setInputModeStart(int input);

	boolean getInputModeFixed();

	boolean setInputModeFixed(boolean inputMode);

	int getInputTVProgramNumber();

	boolean setInputTVProgramNumber(int prog_number);

	boolean getOnScreenDisplay();

	boolean setOnScreenDisplay(boolean value);

	boolean reset();

	boolean commit();

	String getNormalStandbyCause();

	boolean resetStandbyCause();

	boolean setPattern(int pattern);


}