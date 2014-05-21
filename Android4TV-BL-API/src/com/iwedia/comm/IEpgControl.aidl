package com.iwedia.comm;

import java.util.List;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.epg.EpgFilter;
import com.iwedia.dtv.epg.EpgEventType;
import com.iwedia.comm.IEpgCallback;


/**
 * The EPG (Electronic Program Guide) controller. Manages the list of events.
 *
 * @author Sasa Jagodin
 *
 */
interface IEpgControl{

	int createEventList();
 	
 	void setFilter(int filterID, in EpgFilter epgFilter);
 	
 	void startAcquisition(int filterID);
 	
 	void stopAcquisition(int filterID);
 	
 	EpgEvent getRequestedEvent(int filterID, int serviceIndex, int eventIndex);

	int getAvailableEventsNumber(int filterID, int serviceIndex);
	
	void releaseEventList(int filterID);

	EpgEvent getPresentFollowingEvent(int filterID, int serviceIndex, in EpgEventType type);
	
	String getEventExtendedDescription(int filterID, int eventId, int serviceIndex);
	
	void registerCallback(IEpgCallback callback, int filterID);
	
	void unregisterCallback(IEpgCallback callback, int filterID);

}
