package com.iwedia.comm;

/**
 * The PVR and timeshift related callbacks.
 *
 * @author Sasa Jagodin
 *
 */
interface IPvrCallback {

	
	/**
	*Notifies that PVR recording  time has changed.
	*@param currentTimeSec - current recording  time
	*@param endTime - end recording  time
	*/
	
	void recordStart();
	
	void recordStop();
	
	void recordRemove();
	
	void recordPosition(int recordTime, int endTime);
	
	void recordAdd();
	
	/**
	*Notifies that PVR playback state has changed.
	*@param playState - current playback state
	*@param playbackSpeed - playback speed.
	*/
	
	void playbackPlay();
	
	void playbackStop();
	
	void playbackFastForward(int speed);
	
	void playbackRewind(int speed);
	
	void playbackPosition(int playbackTime);
	
	void playbackPause();
	

	/**
	*Notifies that PVR schedule recording  state has changed.
	*@param  scheduledRecordState - current schedule recording  state
	*/
	
	void timeshiftStart();
	
 	void timeshiftStop();
 	
 	void timeshiftPlay();
 	
 	void timeshiftFastForward(int speed);
 	
 	void timeshiftRewind(int speed);
 	
 	void timeshiftPause();
 	
 	void timeshiftPosition(int playbackTime, int recordTime, int endTime);
 	

	void eventUsbSpeed(int speed);

	void eventUSBMediaStorageFull();
}