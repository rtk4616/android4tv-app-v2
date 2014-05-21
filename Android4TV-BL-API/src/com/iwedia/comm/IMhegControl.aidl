package com.iwedia.comm;

import com.iwedia.comm.IMhegCallback;
import com.iwedia.dtv.types.UserControl;

/**
 * The Multimedia and Hypermedia Experts Group (MHEG) interactive television services controller.
 *
 *
 */
 interface IMhegControl {

	/**
	 * Shows the MHEG layer.
	 *
	 * @return true if everything is ok, else false
	 */
	 boolean show();

	/**
	 * Hides the MHEG layer.
	 *
	 * @return true if everything is ok, else false
	 */
	 boolean hide();

	/**
	 * Sends the UI input to the MW.
	 *
	 * @param keyCode
	 * - Android keyCode of pressed key.
	 *
	 * @return true if everything is ok, else false
	 */
	 boolean sendInputControl(int keyCode, in UserControl ctrl);

	 int getState();

	 boolean isPresent();

	 void registerCallback(IMhegCallback mhegCallback);
}
