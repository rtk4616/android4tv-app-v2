package com.iwedia.comm.reminder;

/**
 * The reminder related callbacks.
 *
 * @author Milan Vidakovic
 *
 */
 
import com.iwedia.dtv.reminder.ReminderEventTrigger;
import com.iwedia.dtv.reminder.ReminderEventAdd;
import com.iwedia.dtv.reminder.ReminderEventRemove;
 
interface IReminderCallback {

	/**Inform the user when the event with 'reminderHandle' in reminder list has started(true) or stopped (false).*/
	void reminderTrigger(in ReminderEventTrigger reminderEvent);
	void reminderAdd(in ReminderEventAdd eventAdd);
	void reminderRemove(in ReminderEventRemove eventRemove);
}