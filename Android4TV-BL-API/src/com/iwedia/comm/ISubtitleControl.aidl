package com.iwedia.comm;

import com.iwedia.dtv.subtitle.SubtitleType;
import com.iwedia.dtv.subtitle.SubtitleMode;


/**
 * The subtitle controller. Subtitles can be either text or bitmap.
 * Subtitle language can be set. The default language is English.
 * The Language information holds the Locale info (see {@link android.dtv.setup.Language}).
 *
 * The subtitles are fetched from the transport stream by the MW.
 *
 *
 */

interface ISubtitleControl {

	/**
	 * Gets the language of the subtitle.
	 *
	 * @return subtitle lanuage code
	 */
	 int getSubtitleLanguage();

	 /** Gets the type of the subtitle. */
	 SubtitleType getSubtitleType();

	 /**
	 * Hides subtitles.
	 *
	 * @return true if everything is OK, else false
	 */
	 boolean hide();

	/**
	 * Sets the type of the subtitle.
	 *
	 * @return true if everything is OK, else false
	 */
	 boolean setSubtitleType(in SubtitleType type);

	/**
	 * Displays subtitles.
	 *
	 * @return true if everything is OK, else false
	 */
 	 boolean show();

	 /** Returns subtitle mode. */
	 SubtitleMode getSubtitleMode();

	 /** Sets Subtitle mode. */
	 boolean setSubtitleMode(in SubtitleMode mode);

	 /** Set default values for subtitle settings. */
	 boolean resetSubtitleSettings();

	 /** Returns SubtitleTrack country name by index. */
	 String getSubtitleTrack(int index);

	 /** Sets current subtitle track. */
	 boolean setCurrentSubtitleTrack(int index);

	 /** Gets index of current subtitle track. */
	 int getCurrentSubtitleTrackIndex();

	 /** Gets number of available subtitle tracks. */
	 int getSubtitleTrackCount();

	 /**
	 *Sets subtitle state.
	 */
	 void setSubtitleEnabled(boolean state);

	 /**
	 *Returns subtitle state.
	 */
	 boolean getSubtitleEnabled();

	 /** Set the first subtitle language  */
	boolean setFirstSubtitleLanguage(int languageIndex);

	/** Get the first subtitle language */
	int getFirstSubtitleLanguage();

	/** Set the second subtitle language  */
	boolean setSecondSubtitleLanguage(int languageIndex);

	/** Get the second subtitle language */
	int getSecondSubtitleLanguage();
}
