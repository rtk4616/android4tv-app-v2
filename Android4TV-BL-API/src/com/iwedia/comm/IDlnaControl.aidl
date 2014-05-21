package com.iwedia.comm;

import java.util.List;
import com.iwedia.comm.IDlnaCallback;

/**
 *
 *
 * @author Sasa Jagodin
 *
 */
 interface IDlnaControl {

	/** Starts DLNA renderer
	*@return true if everything is OK, else false.
	*/
	boolean startDlnaRenderer(String friendlyName);

	/** Stops DLNA renderer
	*@return true if everything is OK, else false.
	*/
	boolean stopDlnaRenderer();

	boolean changeDMRName(String friendlyName);

	/** Notify DLNA renderer
	* @param notifyType type of notification.
	* @param notifyValue value of notification.
	* @param position current position of played file.
	*@return true if everything is OK, else false.
	*/
	boolean notifyDlnaRenderer(int notifyType,int notifyValue, String position);

	void registerCallback(IDlnaCallback dlnaCallback);

	void unregisterCallback(IDlnaCallback dlnaCallback);

	boolean startDlnaServer(String friendlyName);

	boolean stopDlnaServer();

	boolean changeDMSName(String friendlyName);

	boolean getServerStatus();

	boolean getRendererStatus();

	void deinitDlna();
}
