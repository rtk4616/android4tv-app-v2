package com.iwedia.comm;

import java.util.List;
import com.iwedia.dtv.audio.AudioTrack;


 /** The audio controller. Sets the volume and does the audio tracks management.
 *
 *  	@author Marko Zivanovic
 *
 */
interface IAudioControl{

 /**
	* Returns the audio track of the given index.
	*
	* @return {@link android.dtv.audio.AudioTrack}
	*/
	AudioTrack	getAudioTrack(int index);

	/**
	* Returns the number of audio tracks.
	*
	* @return number of available audio tracks.
	*/
	int getAudioTrackCount();

	/**
	* Gets the index of current audio track.
	*
	* @return the index of current audio track.
	*/
	int	getCurrentAudioTrackIndex() ;


	/**
	* Sets the current audio track.
	*
	* @param currentAudioTrackIndex
	*            - index of the track you want to make active
	* @return
	*/
	void setCurrentAudioTrack(int currentAudioTrackIndex);


	/**
	* Deselect current audio track.
	* @return true if everything is ok, else false
	*/
	void deselectCurrentAudioTrack();

	/** Set the first audio language  */
	void setFirstAudioLanguage(int languageIndex);

	/** Get the first audio language */
	int getFirstAudioLanguage();

	/** Set the second audio language  */
	void setSecondAudioLanguage(int languageIndex);

	/** Get the second audio language */
	int getSecondAudioLanguage();

}