package com.iwedia.service.reminder;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IReminderControl;
import com.iwedia.comm.reminder.IReminderCallback;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.reminder.ReminderEvent;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import android.util.Log;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.reminder.ReminderSmartInfo;
import com.iwedia.dtv.reminder.ReminderSmartParam;
import com.iwedia.dtv.reminder.ReminderTimerInfo;
import com.iwedia.dtv.reminder.ReminderTimerParam;
import com.iwedia.dtv.reminder.ReminderType;
import com.iwedia.dtv.reminder.ReminderEvent;
import com.iwedia.dtv.reminder.ReminderEventTrigger;
import com.iwedia.dtv.reminder.ReminderEventAdd;
import com.iwedia.dtv.reminder.ReminderEventRemove;
import com.iwedia.dtv.types.InternalException;

/**
 * The reminder controller. The reminder item consists of the time, date,
 * description and a service.
 * 
 * @author Stanislava Markovic
 */
public class ReminderControl extends IReminderControl.Stub implements
        IDTVInterface {
    private static IReminderCallback reminderCallback;
    public static final String LOG_TAG = "RendererControlerJava";
    private static final int REMIDER_EVENT_TRIGGER = 0;
    private static final int REMIDER_EVENT_ADD = 1;
    private static final int REMIDER_EVENT_REMOVE = 2;

    /**
     * Gets the number of reminder items.
     * 
     * @return number of reminder items
     */
    @Override
    public int updateList() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .updateList();
    }

    /**
     * Adds a new reminder to the list.
     * 
     * @param item
     *        - item you want to add.
     * @return true if an item was added.
     */
    @Override
    public void createSmart(ReminderSmartParam reminderSmartParam)
            throws RemoteException {
        Log.d(LOG_TAG, "createSmart");
        try {
            IWEDIAService.getInstance().getDTVManager().getReminderControl()
                    .createSmart(reminderSmartParam);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.e(LOG_TAG, "setSmartReminder" + reminderSmartParam.getTime());
    }

    /**
     * Adds a new manual reminder to the list.
     * 
     * @param item
     *        - item you want to add.
     * @return true if an item was added.
     */
    @Override
    public void createTimer(ReminderTimerParam param) throws RemoteException {
        try {
            IWEDIAService.getInstance().getDTVManager().getReminderControl()
                    .createTimer(param);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Removes a reminder from the list.
     * 
     * @param index
     *        - index of the item you want to remove.
     * @return true if an item was removed.
     */
    @Override
    public void destroy(int index) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .destroy(index);
    }

    /**
     * Gets the reminder item by the index.
     * 
     * @param index
     *        - index of the item you want to get.
     * @return {@com.iwedia.comm.epg.EpgEvent}
     */
    @Override
    public ReminderSmartInfo getSmartInfo(int index) throws RemoteException {
        Log.e(LOG_TAG, "getSmartInfo");
        return IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .getSmartInfo(index);
    }

    /**
     * Gets the reminder item by the index.
     * 
     * @param index
     *        - index of the item you want to get.
     * @return {@com.iwedia.comm.epg.EpgEvent}
     */
    @Override
    public ReminderTimerInfo getTimerInfo(int index) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .getTimerInfo(index);
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    /**
     * Gets reminder type.
     * 
     * @param index
     *        index of the item.
     * @return reminder type
     */
    @Override
    public ReminderType getType(int index) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .getType(index);
    }

    /**
     * Gets reminder list sort mode
     * 
     * @param
     * @return list sort mode
     */
    @Override
    public PvrSortMode getListSortMode() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .getListSortMode();
    }

    /**
     * Set reminder list sort mode
     * 
     * @param
     * @return list sort mode
     */
    @Override
    public boolean setListSortMode(PvrSortMode mode) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .setListSortMode(mode);
        return true;
    }

    /**
     * Gets reminder list sort order
     * 
     * @param
     * @return list sort order
     */
    @Override
    public PvrSortOrder getListSortOrder() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .getListSortOrder();
    }

    /**
     * Set reminder list sort order
     * 
     * @param
     * @return list sort order
     */
    @Override
    public boolean setListSortOrder(PvrSortOrder order) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getReminderControl()
                .setListSortOrder(order);
        return true;
    }

    /**
     * Register reminder callback.
     */
    @Override
    public void registerCallback(IReminderCallback callback)
            throws RemoteException {
        if (callback != null) {
            reminderCallback = callback;
        }
    }

    /**
     * DTV Reminder callback.
     */
    public static com.iwedia.dtv.reminder.IReminderCallback dtvReminderCallback = new com.iwedia.dtv.reminder.IReminderCallback() {
        @Override
        public void reminderTrigger(ReminderEventTrigger reminderEvent) {
            Log.d(LOG_TAG,
                    "reminderTrigger :" + reminderEvent.getServiceIndex() + " "
                            + reminderEvent.getEventID() + " "
                            + reminderEvent.getTitle());
            try {
                reminderCallback.reminderTrigger(reminderEvent);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void reminderAdd(ReminderEventAdd eventAdd) {
            try {
                reminderCallback.reminderAdd(eventAdd);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void reminderRemove(ReminderEventRemove eventRemove) {
            try {
                reminderCallback.reminderRemove(eventRemove);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    public static com.iwedia.dtv.reminder.IReminderCallback getReminderCallback() {
        return dtvReminderCallback;
    }
}
