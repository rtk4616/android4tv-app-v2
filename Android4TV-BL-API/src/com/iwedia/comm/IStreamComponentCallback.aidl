package com.iwedia.comm;

/**
 * The Stream Component.
 *
 * @author Sasa Jagodin
 *
 */
interface IStreamComponentCallback {

	/**
	*Inform the user when component is changed.
	*/
	void componentChanged(int routeID);
}