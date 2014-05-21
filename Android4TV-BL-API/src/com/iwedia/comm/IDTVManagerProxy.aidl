package com.iwedia.comm;

import com.iwedia.comm.IAudioControl;
import com.iwedia.comm.ICIControl;
import com.iwedia.comm.IEpgControl;
import com.iwedia.comm.IHbbTvControl;
import com.iwedia.comm.IScanControl;
import com.iwedia.comm.IMhegControl;
import com.iwedia.comm.IPvrControl; //later
import com.iwedia.comm.IReminderControl;
import com.iwedia.comm.IServiceControl;
import com.iwedia.comm.ISubtitleControl;
import com.iwedia.comm.ITeletextControl;
import com.iwedia.comm.IVideoControl;
import com.iwedia.comm.IVideoControl;
import com.iwedia.comm.ISystemControl;

import com.iwedia.comm.IParentalControl;
import com.iwedia.comm.ICallbacksControl;
import com.iwedia.comm.IActionControl;
import com.iwedia.comm.IDlnaControl;
import com.iwedia.comm.IOnDemandControl;
import com.iwedia.comm.ISetupControl;
import com.iwedia.comm.IInputOutputControl;
import com.iwedia.comm.IServiceMode;
import com.iwedia.comm.IDisplayControl;
import com.iwedia.comm.IStreamComponentControl;

import com.iwedia.comm.content.IContentListControl;


/** This is the main IWEDIA service interface. It is a facade for all the elements necessary for the DTV to work.
 *
 * @author Marko Zivanovic
 *
 */
 interface IDTVManagerProxy {

	/** Returns the audio controller ({@link com.iwedia.comm.IAudioControl}). */
	 IAudioControl getAudioControl();

	 /** Returns the CI Controller ({@link com.iwedia.comm.ICIControl}). */
	 ICIControl getCIControl();

	 /** Returns the EPG controller ({@link com.iwedia.comm.IEpgControl}). */
	 IEpgControl getEpgControl();

	  /** Returns the Hbb Control ({@link com.iwedia.comm.IHbbTvControl}). */
	 IHbbTvControl getHbbTvControl();

	 /** Returns the service installation controller ({@link com.iwedia.comm.IScanControl}). */
	 IScanControl getScanControl();

	 /** Returns the MHEG controller ({@link com.iwedia.comm.IMhegControl}). */
	 IMhegControl getMhegControl();

	 /** Returns the PVR controller ({@link com.iwedia.comm.IPvrControl}). */
	 IPvrControl getPvrControl();

	 /** Returns the reminder controller ({@linkcom.iwedia.comm.IReminderControl}). */
	 IReminderControl getReminderControl();

	 /** Returns the list of services ({@link com.iwedia.comm.IServiceListControl}). */
	 IServiceControl getServiceControl();

	 /** Returns the subtitle controller ({@link com.iwedia.comm.ISubtitleControl}). */
	 ISubtitleControl getSubtitleControl();

	/** Returns the video controller ({@link com.iwedia.comm.IVideoControl}). */
	 IVideoControl getVideoControl();

	/** Returns the teletext controller ({@link com.iwedia.comm.ITeletextControl}). */
	 ITeletextControl getTeletextControl();

	/** Returns the parental control settings. ({@link com.iwedia.comm.IParentalControl}). */
	 IParentalControl getParentalControl();

	 /** Returns the main setup ({@link com.iwedia.comm.ICallbacksControl}). */
	 ICallbacksControl getCallbacksControl();

     /** Returns the Contetent List controller ({@link com.iwedia.comm.content.IContentListControl}).*/
     IContentListControl getContentListControl();

     /** Returns the Android system controller ({@link com.iwedia.comm.ISystemControl}).*/
     ISystemControl getSystemControl();

     /** Returns the Action Control controller ({@link com.iwedia.comm.IActionControl}).*/
     IActionControl getActionControl();

     /** Returns the DLNA Control controller ({@link com.iwedia.comm.IDlnaControl}).*/
     IDlnaControl getDlnaControl();

     /**Returns the Video On Demand controller ({@link com.iwedia.com.IOnDemandControl}). */
     IOnDemandControl getVideoOnDemandControl();

     /**Returns the Setup controller ({@link com.iwedia.com.ISetupControl}). */
     ISetupControl getSetupControl();

     /**Returns the Service Mode controller ({@link com.iwedia.comm.IServiceMode}). */
     IServiceMode getServiceMode();

     /**Returns the Input Output controller ({@link com.iwedia.com.ISetupControl}). */
     IInputOutputControl getInputOutputControl();
	 
	 /**Returns the Stream Control controller ({@link com.iwedia.com.ISetupControl}). */
     IStreamComponentControl getStreamComponentControl();

     IDisplayControl getDisplayControl();
}