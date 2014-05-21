package com.iwedia.comm;
/**
 * The setup related callbacks.
 *
 * @author Sasa Jagodin
 *
 */
interface ISetupCallback {
	/**
	*Inform the user about the changes about time from stream information.
	*/
	void offTimerChanged();
}