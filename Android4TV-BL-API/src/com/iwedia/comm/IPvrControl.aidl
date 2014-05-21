package com.iwedia.comm;

import java.util.List;

import com.iwedia.comm.IPvrCallback;
import com.iwedia.dtv.pvr.TimerCreateParams;
import com.iwedia.dtv.pvr.SmartCreateParams;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.pvr.PvrRecordType;
import com.iwedia.dtv.pvr.OnTouchInfo;
import com.iwedia.dtv.pvr.TimerInfo;
import com.iwedia.dtv.pvr.SmartInfo;
import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.dtv.pvr.PlaybackInfo;
import com.iwedia.dtv.pvr.TimeshiftInfo;

/**
 * The Personal Video Recorder.Used to record a service to some media (hard
 * disk, usb drive, etc.), to play the recorded data.
 *
 * @author Stanislava Markovic
 *
 */
 interface IPvrControl {
 
 
	 /**
	 * Sets device path
	 *
	 * @param path  Path for the specific device.
	 *
	 * @return
	 */
	void setDevicePath(String path);
 
 	/**
	 * Retrieves device path
	 *
	 * @return path of the specific device.
	 *
	 */
 	String getDevicePath();
 	
 	/**
	 * Sets device speed.
	 *
	 * @param speed Device speed to be set (in KB/s).
	 *
	 * @return
	 */
	void setDeviceSpeed(int speed);
 	
    /**
     * Instant creation of recording.
     *
     * @param serviceID     Index of service in master list to be recorded.
     *
     * @return
     */
    void createOnTouchRecord(int serviceID);
    
    /**
	 * Creates time based recording.
	 *
	 * @param timerCreateParams  Creation parameters for timer record.
	 *
	 * @return
	 */
    void createTimerRecord(in TimerCreateParams timerCreateParams);
    
    /** Creates EPG based recording
    * 
 	* @param smartCreateParams   Creation parameters for smart record.
    *
    * @return
	*/
	void createSmartRecord(in SmartCreateParams smartCreateParams);
	
	/**
	 * Stops and removes created record. The recorded media is saved to mass storage.
	 *
	 * @param index Index of the specific recording.
	 *
	 * @return
	 */
	void destroyRecord(int index);
	
	/** 
	* Updates record list
	*
	* @return number of recorded services in list.
	*
	*/
	int updateRecordList();
	
	
	/**
	 * @brief Sets record list sort mode
	 *
	 * @param mode Sort mode.
	 *
	 * @return
	 */
	void setRecordListSortMode(in PvrSortMode mode);
	
	/**
	 * @brief Gets record list sort mode
	 *
	 * @return sort mode.
	 *
	 */
	PvrSortMode getRecordListSortMode();
	
	/**
	 * Sets record list sort order
	 *
	 * @param order Sort order.
	 *
	 * @return
	 */
	void setRecordListSortOrder(in PvrSortOrder order);
	
	/**
	 * Gets record list sort order
	 *
	 * @return sort order.
	 *
	 */
	PvrSortOrder getRecordListSortOrder();
	
	/**
	 * @Retrieves record type
	 *
	 * @param index  Index of the specific recording.
	 *
	 * @return type of the specific recording.
	 *
	 */
	PvrRecordType getRecordType(int index);
	
	/**
	 * Retrieves on touch record info
	 *
	 * @param index  Index of the specific recording.
	 *
	 * @return requested record info.
	 *
	 */
	OnTouchInfo getOnTouchInfo(int index);
	
	/**
	 * Retrieves timer record info.
	 *
	 * @param index  Index of the specific recording.
	 *
	 * @return requested record info.
	 *
	 */
	TimerInfo getTimerInfo(int index);
	
	/**
	 * Retrieves smart record info.
	 *
	 * @param index  Index of the specific recording.
	 *
	 * @return requested record info.
	 *
	 */
	SmartInfo getSmartInfo(int index);
	
	/**
	 * Sets maximum possible duration of the recording (in seconds)
	 *
	 * @param duration Maximum possible duration of the recording (in seconds).
	 *
	 * @return
	 */
	void setRecordMaxDuration(int duration);
	
	/**
	 * Retrieves maximum possible duration of the recording (in seconds)
	 *
	 * @return maximum possible duration of the recording (in seconds).
	 *
	 */
	int getRecordMaxDuration();
	
	/**
	 * Updates media list
	 *
	 * @return number of media entries.
	 *
	 */
	int updateMediaList();
	
	/**
	 * @brief Sets media list sort mode
	 *
	 * @param mode Sort mode.
	 *
	 * @return
	 */
	void setMediaListSortMode(in PvrSortMode mode);
	
	/**
	 * @brief Gets media list sort mode
	 *
	 * @return sort mode.
	 *
	 */
	PvrSortMode getMediaListSortMode();
	
	/**
	 * Sets media list sort order
	 *
	 * @param order Sort order.
	 *
	 * @return
	 */
	void setMediaListSortOrder(in PvrSortOrder order);
	
	/**
	 * Gets media list sort order
	 *
	 * @return sort order.
	 *
	 */
	PvrSortOrder getMediaListSortOrder();
	
	/**
	 * Retrieves media info.
	 *
	 * @param index  Index of the specific media.
	 *
	 * @return requested media info.
	 *
	 */
	MediaInfo getMediaInfo(int index);
	
	/**
	 * Deletes media from list.
	 *
	 * @param index Index of the specific media.
	 *
	 * @return
	 */
	void deleteMedia(int index);
	
	/**
	 * Deletes media list.
	 *
	 * @return
	 */
	
	void deleteMediaList();
	
	/**
	 * Starts playback of selected media.
	 *
	 * @param index   Identifier of the specific media.
	 *
	 * @return
	 */
	void startPlayback(int displayId, int index);
	
	/**
	 * @Stops playback.
	 *
	 * @return
	 */
	void stopPlayback();
	
	/**
	 * Retrieves playback info
	 *
	 * @return requested playback info.
	 *
	 */
	PlaybackInfo getPlaybackInfo();
	
	/**
	 * Sets size of timeshift buffer (in megabytes).
	 *
	 * @param size Size of timeshift buffer (in megabytes).
	 *
	 * @return
	 */
	void setTimeshiftBufferSize(int size);
	
	/**
	 * Retrieves size of timeshift buffer (in megabytes)
	 *
	 * @return size of timeshift buffer (in megabytes).
	 */
	int getTimeshiftBufferSize();
	
	/**
	 * Starts timeshift.
	 *
	 * @return
	 */
	void startTimeshift();
	
	/**
	 * Stops timeshift.
	 *
	 * @param resume  Resumes recording of a service if TRUE.
	 *
	 * @return
	 */
	void stopTimeshift(boolean resume);
	
	/**
	 * Retrieves timeshift info
	 *
	 * @return requested timeshift info.
	 *
	 */
	TimeshiftInfo getTimeshiftInfo();
	
	/**
	 * Controls speed of playback/timeshift.
	 *
	 * @param speed   Speed for playback/timeshift.
	 *
	 * @return
	 */
	void controlSpeed(int speed);
	
	/**
	 * Jumps on given position of playback/timeshift.
	 *
	 * @param position Position of plaback/timeshift (in miliseconds). TODO
	 * @param relative TRUE if relative, FALSE if absolute.
	 *
	 * @return
	 */
	void jump(int position,boolean relative);
	
	
	/** Pause playback/timeshift.
	* @resumeRec     Resumes recording of a service if TRUE.
	* @return
	*/
	void pause(boolean  pauseResume);


	/** Fast forward playback/timeshift.
	* @resumeRec     Resumes recording of a service if TRUE.
	* @return
	*/
	void fastForward();


	/** Decreases speed of playback/timeshift.
	* @return
	*/
	void rewind();	
	
	void registerCallback(IPvrCallback pvrCallback);

	void unregisterCallback(IPvrCallback pvrCallback);

}