package com.iwedia.comm;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.reminder.ReminderEvent;
import com.iwedia.comm.reminder.IReminderCallback;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.reminder.ReminderSmartInfo;
import com.iwedia.dtv.reminder.ReminderSmartParam;
import com.iwedia.dtv.reminder.ReminderTimerInfo;
import com.iwedia.dtv.reminder.ReminderTimerParam;
import com.iwedia.dtv.reminder.ReminderType;

/**
 * The reminder controller. The reminder item consists of the time, date,
 * description and a service.
 *
 * @author Stanislava Markovic
 *
 */
interface IReminderControl {

	/**
	 * Gets the number of reminder items.
	 *
	 * @return number of reminder items
	 */
	int updateList();
	
	/**
	 * Adds a new reminder to the list.
	 *
	 * @param item
	 *            - item you want to add.
	 * @return true if an item was added.
	 */
	void createSmart(in ReminderSmartParam reminderSmartParam);
	
	/**
	 * Gets the reminder item by the index.
	 *
	 * @param index
	 *            - index of the item you want to get.
	 * @return {@com.iwedia.dtv.reminder.ReminderSmartInfo}
	 */
	ReminderSmartInfo getSmartInfo(int index);

	/**
	 * Adds a new manual reminder to the list.
	 *
	 * @param item
	 *            - item you want to add.
	 * @return true if an item was added.
	 */
	void createTimer(in ReminderTimerParam param);
	
	/**
	 * Gets the reminder item by the index.
	 *
	 * @param index
	 *            - index of the item you want to get.
	 * @return {@com.iwedia.dtv.reminder.ReminderEvent}
	 */
	ReminderTimerInfo getTimerInfo(int index);

	/**
	 * Removes a reminder from the list.
	 *
	 * @param index
	 *            - index of the item you want to remove.
	 * @return true if an item was removed.
	 */
	void destroy(int index);

		
	/**
     * Gets reminder list sort mode
     *
     * @param 
     *         
     * @return list sort mode
     */
	PvrSortMode getListSortMode();
	
	 /**
     * Sets reminder list sort mode
     *
     * @param 
     *         
     * @return list sort mode
     */
	boolean setListSortMode(in PvrSortMode mode);
	
	/**
     * Gets reminder list sort order
     *
     * @param 
     *         
     * @return list sort order
     */
	PvrSortOrder getListSortOrder();
	
	 /**
     * Sets reminder list sort order
     *
     * @param 
     *         
     * @return list sort order
     */
	boolean setListSortOrder(in PvrSortOrder order);

	/**
	 * Updates a reminder in the list.
	 *
	 * @param item
	 *            Item to be updated.
	 * @return true if an item was updated.
	 */

	// boolean updateReminder(in EpgEvent item); //TODO
	
	/**
     * Gets reminder type.
     *
     * @param index
     *            index of the item.
     * @return reminder type
     */
	ReminderType getType(int index);

	void registerCallback(IReminderCallback callback);

}
