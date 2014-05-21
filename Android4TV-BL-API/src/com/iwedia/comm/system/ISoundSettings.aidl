package com.iwedia.comm.system;

import com.iwedia.dtv.sound.SoundEffect;
import com.iwedia.dtv.sound.SoundEffectParam;
import com.iwedia.dtv.sound.AudioChannelMode;
import com.iwedia.dtv.io.SpdifMode;
import com.iwedia.dtv.sound.SoundMode;
import com.iwedia.dtv.sound.AudioEqualizerBand;

/**
 * The sound controller. Sets the volume and other audio settings.
 *
 * @author Stanislava Markovic
 *
 */

interface ISoundSettings {

   /**
	* Gets active sound mode.
	*
	* @return active sound mode.
	*/
	SoundMode getActiveSoundMode();

   /**
	* Sets active sound mode.
	*
	* @param mode
	*			- sound mode you want to set
    */
	void setActiveSoundMode(in SoundMode mode);

   /**
	* Gets active SPDIF mode.
	*
	* @return active SPDIF mode.
	*/
	SpdifMode getActiveSpdifMode();

   /**
	* Sets active SPDIF mode.
	*
	* @param mode
	*			- SPDIF mode you want to set
	*/
	void setActiveSpdifMode(in SpdifMode mode);

   /**
    * Gets auto volume value.
    *
    * @return true if volume is auto, else false.
	*/
	boolean isAutoVolume();

   /**
    * Sets auto volume value;
    *
	* @param autoVolume
	*			- true for auto volume, else false.
	*/
	void setAutoVolume(boolean autoVolume);

   /**
	* Check if volume is automatic.
	*
	* @return true if volume is automatic, else false.
	*/
	int getVolume();

   /**
	* Sets volume value.
	*
	* @param volume
	*            - value for the volume you want to set
	*/
	void setVolume(double volume);

   /**
	* Mutes or unmutes audio
	*
	* @param mute
	*			 - true if you want to mute the audio, false if you want to unmute the audio.
	* @return true if everything is OK, else false
	*/
	void muteAudio(boolean mute);

   /**
	* Gets audio mute state.
	*
	* @return true if muted, else false.
	*/
	boolean getAudioMute();

   /**
	* Gets treble value.
	*
	* @return treble value.
	*/
	int getTreble();

   /**
	* Sets treble value.
	*
	* @param treble
	*            - value for the treble you want to set
	*/
	void setTreble(int treble);

   /**
	* Gets balance value;
	*
	* @return balance value.
	*/
	int getBalance();

   /**
	* Sets balance value.
	*
	* @param balance
	*            - value for the balance you want to set
	*/
	void setBalance(int balance);

   /**
	* Gets bass value.
	*
	* @return bass value.
	*/
	int getBass();

   /**
	* Sets bass value.
	*
	* @param bass
	*            - value for the bass you want to set
	*/
	void setBass(int bass);

   /**
	* Gets headphone volume value.
	*
	* @return headphone volume value.
	*/
	int getHeadphoneVolume();

   /**
	*	Sets headphone volume value.
	*
	* @param volume
	*            - value for the volume you want to set
	*/
	void setHeadphoneVolume(int volume);

/**
	 * Get the number of equalizer bands
	 * @return number of equalizer bands
	 */
	int getNumberOfEqualizerBands();

	/**
	 * Get the equalizer band central frequency
	 * @param band
	 *        whose frequency to read
	 * @return equalizer band central frequency
	 */
    int getEqualizerBandFrequency(in AudioEqualizerBand band);

    /**
	 * Get the equalizer band value
	 * @param band
	 *        whose value to read
	 * @return equalizer band value
	 */
    int getEqualizerBandValue(in AudioEqualizerBand band);

    /**
	 * Set the equalizer band value
	 * @param band
	 *        whose value to write
	 * @param value
	 *        new value of the equalizer band
	 * @return
	 */
    void setEqualizerBandValue(in AudioEqualizerBand band, int value);

    /**
	 * Check if specified sound effect is enabled
	 * @param effect
	 *        whose state to check
	 * @return TRUE if everything is effect is enabled, else false.
	 */
    boolean isSoundEffectEnabled(in SoundEffect effect);

     /**
	 * Set new enabled state for the specified sound effect
	 * @param effect
	 *        whose state to check
	 * @param enabled
	 *        new enabled state
	 * @return
	 */
	void setSoundEffectEnabled(in SoundEffect effect, boolean enabled);

	/**
	 * Get sound effect parameter value
	 * @param paramId
	 *       whose value to get.
	 * @return value of the sound effect param.
	 */
	int getSoundEffectParam(in SoundEffectParam paramId);

	/**
	 * Set sound effect parameter value
	 * @param paramId
	 *        whose value to set.
	 * @param value
	 *        new value for the param.
	 * @return
	 */
	void setSoundEffectParam(in SoundEffectParam paramId, int value);

   /**
	* Returns mute status.
	*
	* @return true if muted, else false.
	*/
	boolean isMute();

	/** Set audio description on or off */
	void setAudioDescription(boolean onOff);

	/** Get audio description state. */
	boolean getAudioDescritpion();

	/* Gets audio mode */
	AudioChannelMode getAudioChannelMode();

	/* Sets audio mode */
	void setAudioChannelMode(in AudioChannelMode audioMode);

	/* Sets default settings */
	void setAudioMenuDefaultSettings();
}