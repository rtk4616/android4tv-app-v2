package com.iwedia.comm;

/**
 * On demand functionalities.
 *
 * @author Nikola Crvenkovic
 *
 */
 interface IOnDemandControl {

	/**
	 * Start an on demand stream.
	 *
	 * @param url
	 * 				- url to the on demand stream.
	 * @return true if everything is ok, else false
	 */
	 boolean start(String url);

	/**
	 * Stop an on demand stream.
	 *
	 * @return true if everything is ok, else false
	 */
	 boolean stop();

	 /**
	 * Pause an on demand stream.
	 *
	 * @return true if everything is ok, else false
	 */
	 boolean pause();

	/**
	 * Resume an on demand stream.
	 *
	 * @return true if everything is ok, else false
	 */
	 boolean resume();

	 /**
	 * Seek to a wanted position in the on demand stream.
	 *
	 * @param seconds
	 * 				- seek to position on the given seconds value
	 * @return true if everything is ok, else false
	 */
	 boolean seek(int seconds);

	 /**
	 * Set speed - fast forward or rewind the on demand stream.
	 *
	 * @param speed
	 * 				- set playback speed (1 normal, 2,4,8... FF; -1, -2,-4,-8... RW)
	 * @return true if everything is ok, else false
	 */
	 boolean setSpeed(int speed);

}
