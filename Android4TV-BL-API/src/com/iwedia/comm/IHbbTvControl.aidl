package com.iwedia.comm;

import com.iwedia.comm.IHbbTvCallback;

/**
 * The HBB controller.
 *
 * @author Stanislava Markovic
 *
 */
 interface IHbbTvControl {

	 /**
	 *	Register client for HbbTvCallback.
	 *  {@link com.iwedia.comm.IHbbTvCallback}
	 */
	 void setCallbackHbb(IHbbTvCallback hbbCallback);

	 /**
	 *	Unregister client for HbbTvCallback.
	 *  {@link com.iwedia.comm.IHbbTvCallback}
	 */
	 void unsetCallbackHbb(IHbbTvCallback hbbCallback);

	/**
	*Enable HBB.
	*/
	boolean enableHBB();

	/**
	*Disable HBB.
	*/
	boolean disableHBB();

	/**
	*Check if HbbTV is enabled or disabled.
	*/
	boolean isHbbEnabled();

    /**
	*Select HbbTV component if present.
	*/
	boolean selectHBBTVComponent();

    /**
	*Deselect HbbTV component.
	*/
	boolean deselectHBBTVComponent();

    /**
	*Check if HbbTV component is selected.
	*/
	boolean isHBBTVComponentSelected();

	 /**
	 *Notify HbbTVAppMngr of browser related events(error loading page, load_completed...)
	 */
	boolean notifyAppMngr(int eventId, String eventData);

	int getHbbState();
}
