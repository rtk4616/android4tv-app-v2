package com.iwedia.comm;
/**
 * The epg related callbacks.
 *
 * @author Sasa Jagodin
 *
 */
interface IEpgCallback {
	/**
	*Inform the user about the changes about present following information.
	*/
	void pfEventChanged(int filterID, int serviceIndex);
	/**
	*Inform the user about the changes about schedule information.
	*/
	void scEventChanged(int filterID, int serviceIndex);
	void pfAcquisitionFinished(int filterID, int serviceIndex);
	void scAcquisitionFinished(int filterID, int serviceIndex);
}