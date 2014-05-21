package com.iwedia.service.parental;

import android.os.RemoteException;

import com.iwedia.comm.IParentalCallback;
import com.iwedia.comm.IParentalControl;
import com.iwedia.dtv.parental.dvb.ParentalLockAge;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.dtv.parental.dvb.ParentalAgeEvent;

/**
 * Parental Control module.
 * 
 * @author Marko zivanovic
 */
public class ParentalControl extends IParentalControl.Stub implements
        IDTVInterface {
    private static IParentalCallback sParentalCallback;

    /**
     * Sets parental control PIN.
     * 
     * @return true if everything is ok, else false.
     */
    @Override
    public void setPinCode(int pinCode) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getParentalControlDvb()
                .setPinCode(pinCode);
    }

    /**
     * Sets level of parental guidance.
     * 
     * @return true if everything is ok, else false.
     */
    @Override
    public boolean checkPinCode(int pinCode) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getParentalControlDvb().checkPinCode(pinCode);
    }

    /**
     * Get channel lock status
     * 
     * @param serviceIndex
     *        master index of the service which status is to be read
     * @return Returns true if channel if locked, false otherwise
     */
    @Override
    public boolean getChannelLock(int serviceIndex) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getParentalControlDvb().getChannelLock(serviceIndex);
    }

    /**
     * Get parental lock age for specified service current program. (viewer age
     * limit))
     * 
     * @param serviceIndex
     *        master index of the service (channel) to be checked
     * @return Returns parental lock age.
     */
    @Override
    public int getCurrentProgramParental(int serviceIndex)
            throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getParentalControlDvb()
                .getCurrentProgramParental(serviceIndex).getValue();
    }

    /**
     * Get currently set parental rate
     * 
     * @return Returns currently set age lock type (parental rate limit).
     *         {@link android.dtv.parental.ParentalLockAge}
     */
    @Override
    public int getParentalRate() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getParentalControlDvb().getParentalRate().getValue();
    }

    /**
     * Set the channel lock status.
     * 
     * @param serviceIndex
     *        index of the service to be locked/unlocked
     * @param lockStatus
     *        new service status to be set
     * @return true if everything is OK, else false.
     */
    @Override
    public void setChannelLock(int serviceIndex, boolean lockStatus)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getParentalControlDvb()
                .setChannelLock(serviceIndex, lockStatus);
    }

    /**
     * Sets level of parental guidance.
     * 
     * @param level
     *        - new level of parental guidence.
     * @return true if everything is OK, else false.
     */
    @Override
    public void setParentalRate(int rate) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getParentalControlDvb()
                .setParentalRate(ParentalLockAge.values()[rate]);
    }

    /**
     * Register parental control callback.
     */
    @SuppressWarnings("static-access")
    @Override
    public void registerCallback(IParentalCallback callback)
            throws RemoteException {
        sParentalCallback = callback;
    }

    @Override
    public void unregisterCallback(IParentalCallback callback)
            throws RemoteException {
        sParentalCallback = null;
    }

    static com.iwedia.dtv.parental.dvb.IParentalCallbackDvb parentalCallbacks = new com.iwedia.dtv.parental.dvb.IParentalCallbackDvb() {
        @Override
        public void ageLocked(ParentalAgeEvent arg0) {
            try {
                sParentalCallback.ageLocked(arg0.isLocked());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void channelLocked(int arg0, boolean arg1) {
            // TODO Auto-generated method stub
        }
    };

    public static com.iwedia.dtv.parental.dvb.IParentalCallbackDvb getParentalCallbackDvb() {
        return parentalCallbacks;
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}
