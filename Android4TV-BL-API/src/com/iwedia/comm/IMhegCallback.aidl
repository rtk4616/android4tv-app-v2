package com.iwedia.comm;

import com.iwedia.comm.content.Content;

/**
 * The DTV services installation related callbacks.
 *
 * @author Dusan Petkovic
 *
 */
interface IMhegCallback {
	/**
	*Notifies that Mheg key mask is  changed.
	*/
	void mhegKeyMaskEvent(int keyMask);
}