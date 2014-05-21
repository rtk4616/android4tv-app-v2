package com.iwedia.comm;

import com.iwedia.comm.IActionCallback;
import android.os.Bundle;

 /** This interface contains functions that are used to handle client ActionCallbacks.
 *
 *  	@author Marko Zivanovic
 *
 */
interface IActionControl{

	/**
	*	Register client for ActionCallbacks.
	*  {@link com.iwedia.comm.IActionCallback}
	*/
    void registerCallback(IActionCallback actionCallback);

    /**
	*
	*/
    void onControllerStateChanged(in Bundle bundle);

	/**
	*	Unregister client for ActionCallback.
	*  {@link com.iwedia.comm.IActionCallback}
	*/
    void unregisterCallback(IActionCallback actionCallback);

}