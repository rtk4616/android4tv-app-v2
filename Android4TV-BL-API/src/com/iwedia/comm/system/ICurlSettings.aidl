package com.iwedia.comm.system;

/**
*	GUI Curl settings controller.
* 	@author Marko Zivanovic
*/
interface ICurlSettings{

	/**Gets CURL effect timeout value. */
	float getTimeout();

	/** Sets CURL effect timeout value. */
	void setTimeout(float value);

	/** Returns state of CURL effect. True if CURL is enabled, otherwise false. */
	boolean isEnabled();

	/** Sets state of CURL effect. True to enable CURL effect, otherwise false. */
	void setEnabled(boolean state);
}