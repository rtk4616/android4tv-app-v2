package com.iwedia.comm;

/**
 * The DTV service related callbacks.
 *
 * @author Sasa Jagodin
 *
 */
interface IServiceListCallback {

	void channelChangeStatus(long liveRoute, boolean channelChanged);

	void signalStatus(long liveRoute, boolean channelScrambled);

	void serviceScrambledStatus(long liveRoute, boolean channelChanged);

	void serviceStopped(long liveRoute, boolean serviceStopped);

	void updateServiceList();
	
	void safeToUnblank(long liveRoute);
}