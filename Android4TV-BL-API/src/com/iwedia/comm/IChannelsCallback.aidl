package com.iwedia.comm;


/** The channel related callbacks.
 *
 *  	@author Marko Zivanovic
 *
 */
interface IChannelsCallback{

	/**
	*Notifies the user if the playback stopped.
	*/
	void playbackStopped(int displayId);

	/**
	*Inform the user about the changes of present-following informations.
	*/
	void nowNextChanged();

	/**
	*Inform the user about the changes of EPG informations.
	*/
	void epgEventsChanged();

	/**
	* Service scrambled state.
	* Throws true if current service is scrambled, otherwise false.
	*/
	void serviceScrambled(boolean state);

	void startingService();

	void antennaConnected(int deviceID, boolean success);

	/**
	* Inform the user when network has changed.
	*/
	void networkChanged(int networkId);
}