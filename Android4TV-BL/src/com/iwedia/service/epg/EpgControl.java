package com.iwedia.service.epg;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IEpgCallback;
import com.iwedia.comm.IEpgControl;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.epg.EpgEventType;
import com.iwedia.dtv.epg.EpgFilter;
import com.iwedia.dtv.epg.EpgServiceFilter;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;

/**
 * EPG Controller.
 * 
 * @author Marko Zivanovic.
 */
public class EpgControl extends IEpgControl.Stub implements IDTVInterface {
    private final String LOG_TAG = getClass().getSimpleName();
    public static IEpgCallback epgCallback;
    private static boolean isClbkRegistered = false;

    @Override
    public int createEventList() throws RemoteException {
        try {
            return IWEDIAService.getInstance().getDTVManager().getEpgControl()
                    .createEventList();
        } catch (InternalException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setFilter(int filterID, EpgFilter data) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .setFilter(filterID, data);
    }

    @Override
    public void startAcquisition(int filterID) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .startAcquisition(filterID);
    }

    @Override
    public void stopAcquisition(int filterID) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .stopAcquisition(filterID);
    }

    /**
     * Returns EPG event {@link EpgEvent} for given service index, and index of
     * event.
     */
    @Override
    public EpgEvent getRequestedEvent(int filterID, int serviceIndex,
            int eventIndex) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .getRequestedEvent(filterID, serviceIndex, eventIndex);
    }

    private int servIndex = -1;
    private EpgServiceFilter serviceFilter;

    /**
     * Returns number of EPG events for given serviceIndex for given day in
     * week.
     */
    @Override
    public int getAvailableEventsNumber(int filterID, int serviceIndex)
            throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .getAvailableEventsNumber(filterID, serviceIndex);
    }

    @Override
    public void releaseEventList(int filterID) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .releaseEventList(filterID);
    }

    /**
     * Returns EPG event {@link EpgEvent} for given service index, and index of
     * event.
     */
    @Override
    public EpgEvent getPresentFollowingEvent(int filterID, int serviceIndex,
            EpgEventType type) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .getPresentFollowingEvent(filterID, serviceIndex, type);
    }

    @Override
    public String getEventExtendedDescription(int filterID, int eventId,
            int serviceIndex) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getEpgControl()
                .getEventExtendedDescription(filterID, eventId, serviceIndex);
    }

    @Override
    public void registerCallback(IEpgCallback eventCallback, int filterID)
            throws RemoteException {
        if (isClbkRegistered == false) {
            epgCallback = eventCallback;
            IWEDIAService.getInstance().getDTVManager().getEpgControl()
                    .registerCallback(eventsCallback, filterID);
            isClbkRegistered = true;
        } else {
            Log.i(LOG_TAG, "registerCallback: Callback is already registered!");
        }
    }

    @Override
    public void unregisterCallback(IEpgCallback callback, int filterID)
            throws RemoteException {
        if (isClbkRegistered == true) {
            IWEDIAService.getInstance().getDTVManager().getEpgControl()
                    .unregisterCallback(eventsCallback, filterID);
            epgCallback = null;
            isClbkRegistered = false;
        } else {
            Log.i(LOG_TAG, "unregisterCallback: Callback is not registered!");
        }
    }

    private static com.iwedia.dtv.epg.IEpgCallback eventsCallback = new com.iwedia.dtv.epg.IEpgCallback() {
        @Override
        public void pfAcquisitionFinished(int filterID, int serviceIndex) {
            try {
                epgCallback.pfAcquisitionFinished(filterID, serviceIndex);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void pfEventChanged(int filterID, int serviceIndex) {
            try {
                epgCallback.pfEventChanged(filterID, serviceIndex);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void scAcquisitionFinished(int filterID, int serviceIndex) {
            try {
                epgCallback.scAcquisitionFinished(filterID, serviceIndex);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void scEventChanged(int filterID, int serviceIndex) {
            try {
                epgCallback.scEventChanged(filterID, serviceIndex);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}