package com.iwedia.comm;
/**
 * The Callbacks controller.
 *
 *
 *
 */
 interface ICallbacksControl {

	 void setEventsCallback(in IBinder binder);
	/**
	* set the callback for channel installation.
	*/
	 void setChannelsCallback(in IBinder binder);
 	/**
	* set the callback for CI events.
	*/
	 void setCICallback(in IBinder binder);
  	/**
	* set the callback for graphics rendering events.
	*/

}
