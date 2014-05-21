package com.iwedia.comm;

import com.iwedia.comm.IStreamComponentCallback;

/**
 * Stream Component related.
 *
 * @author Sasa Jagodin
 *
 */
 interface IStreamComponentControl {
	 void registerCallback(IStreamComponentCallback callback);
	
	void unregisterCallback(IStreamComponentCallback callback);
}
