package com.iwedia.comm;

import java.util.List;
import com.iwedia.dtv.video.VideoTrack;

/**
 * The video controller. It is used to set brightness, cointrast, scaling and
 * manage video tracks.
 *
 * @author Milan Vidakovic
 *
 */
 interface IVideoControl {

	/**
	 * Gets the current video track.
	 *
	 * @return {@link VideoTrack}
	 */
	 VideoTrack	getCurrentVideoTrack();  //TODO

	 /**
	 * Returns the video track of the given index.
	 *
	 * @param index
	 *            - index of the track you want to get
	 * @return {@link VideoTrack}
	 */
	 VideoTrack	getVideoTrack(int index); //TODO

	/**
	 * Returns the number of video tracks.
	 *
	 * @return number of available video tracks
	 */
	 int getVideoTrackCount();

	 /**
	 * Sets the current video track.
	 *
	 * @return
	 */
	 void setCurrentVideoTrack(int currentVideoTrackIndex);

	/**
	  * This function blanks the video.
	  * @param routeId route identifier
	  * @param blank Video shall be blank on true, visible on false.
	  * @return
	  */
 	void videoBlank(int routeId, boolean blank);

 	/**
	* Deselects currently active video track and makes it inactive.
	*/
	void deselectCurrentVideoTrack();

	/**
	 * Gets the current video track index.
	 *
	 * @return index of the current video track
	 */
	 int getCurrentVideoTrackIndex();

}