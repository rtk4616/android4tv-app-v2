package com.iwedia.comm;

import com.iwedia.dtv.teletext.TeletextTrack;
import com.iwedia.comm.teletext.TeletextMode;
import com.iwedia.dtv.types.UserControl;

/**
 * The teletext controller.
 * The teletext is fetched from the transport stream and handled by the MW.
 *
 * @author Milan Vidakovic
 *
 */
 interface ITeletextControl {

	 /**
	 * Sends the UI input to the MW.
	 *
	 * @param ctrl
	 *            - input control
	 * @param param
	 *            - input value for control
	 * @return true if everything is ok, else false
	 */
	 boolean sendInputControl(int key, in UserControl ctrl);

	 /** Returns current teletext state (on/off). */
	 boolean getTeletextState();

	 /** Returns teletext mode. */
	 TeletextMode getTeletextMode();

	 /** Sets Teletext mode. */
	 boolean setTeletextMode(in TeletextMode mode);

	 /** Returns TeletextTrack by index.*/
	 TeletextTrack getTeletextTrack(int index); //TODO

	 /** Sets current teletext track. */
	 boolean setCurrentTeletextTrack(int index);
     
     /** Deselects current teletext track. */
     boolean deselectCurrentTeletextTrack();
     
     /** Sets teletext background transparency. */
     boolean setTeletextBgAlpha(int alpha);

	 /** Gets index of current teletext track. */
	 int getCurrentTeletextTrackIndex();

	  /** Gets size of teletext track. */
	 int getTeletextTrackCount();

	 /** Set the first teletext language  */
	 boolean setFirstTeletextLanguage(int languageIndex);

	 /** Get the first teletext language */
	 int getFirstTeletextLanguage();

	 /** Set the second teletext language  */
	 boolean setSecondTeletextLanguage(int languageIndex);

	 /** Get the second teletext language */
	 int getSecondTeletextLanguage();

}
