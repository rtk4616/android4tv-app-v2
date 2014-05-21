package com.iwedia.service.setup;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.ISetupCallback;
import com.iwedia.comm.ISetupControl;
import com.iwedia.dtv.setup.OffSignalTimerEvent;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.dtv.types.TimerRepeatMode;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.system.language_and_keyboard.LanguageManager;

/**
 * Setup controller. Sets Middleware parameters.
 * 
 * @author Marko Zivanovic
 */
public class SetupControl extends ISetupControl.Stub implements IDTVInterface {
    public static ISetupCallback setupCallback;
    private static final String LOG_TAG = "SetupControl";

    /** Gets active country. */
    @Override
    public int getActiveCountry() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getCountry();
    }

    /** Gets the country count */
    @Override
    public String getCountry(int index) throws RemoteException {
        return LanguageManager.getInstance().convertTrigramsToLanguage(
                IWEDIAService.getInstance().getDTVManager().getSetupControl()
                        .getCountryName(index), false);
    }

    /** Gets the country count */
    @Override
    public String getCountryCode(int index) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getCountryName(index);
    }

    /** Gets country name for the given index */
    @Override
    public int getCountryCount() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getCountryCount();
    }

    /** Sets active country. */
    @Override
    public boolean setCountry(int index) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setCountry(index);
        return true;
    }

    @Override
    public boolean setTimeZone(int minutes) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setTimeZone(minutes);
        return true;
    }

    @Override
    public int getTimeZone() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getTimeZone();
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    @Override
    public int getLanguageCount() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getLanguageCount();
    }

    @Override
    public String getLanguageName(int arg0) throws RemoteException {
        return LanguageManager.getInstance().convertTrigramsToLanguage(
                IWEDIAService.getInstance().getDTVManager().getSetupControl()
                        .getLanguageName(arg0), true);
    }

    /** Set off timer. */
    @Override
    public boolean setOffTimer(TimeDate time) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setOffTimerValue(time);
        return true;
    }

    /** Set off timer. */
    @Override
    public boolean startOffTimer() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .startOffTimer();
        return true;
    }

    /** Set off timer. */
    @Override
    public boolean endOffTimer() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .endOffTimer();
        return true;
    }

    /** Get off timer */
    @Override
    public String getOffTimer() throws RemoteException {
        TimeDate time = IWEDIAService.getInstance().getDTVManager()
                .getSetupControl().getOffTimerValue();
        if (time == null) {
            return "00:00";
        }
        StringBuilder returnValueBuilder = new StringBuilder();
        int hour = time.getHour();
        int min = time.getMin();
        if (hour < 10) {
            returnValueBuilder.append("0" + hour);
        } else {
            returnValueBuilder.append("" + hour);
        }
        returnValueBuilder.append(":");
        if (min < 10) {
            returnValueBuilder.append("0" + min);
        } else {
            returnValueBuilder.append("" + min);
        }
        return returnValueBuilder.toString();
    }

    /** Set off timer repeat mode. */
    @Override
    public boolean setOffTimerRepeat(TimerRepeatMode repeatMode)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setOffTimerRepeat(repeatMode);
        return true;
    }

    /** Get off timer repeat mode. */
    @Override
    public TimerRepeatMode getOffTimerRepeat() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getOffTimerRepeat();
    }

    /** Set timer for no operation off. */
    @Override
    public boolean setNoOperationOff(boolean noOperation)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setNoOperationOff(noOperation);
        return true;
    }

    /** Get timer for no operation off. */
    @Override
    public boolean getNoOperationOff() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getNoOperationOff();
    }

    /** Set timer for no signal off. */
    @Override
    public boolean setNoSignalOff(boolean noSignal) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setNoSignalOff(noSignal);
        return true;
    }

    /** Get timer for no signal off. */
    @Override
    public boolean getNoSignalOff() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getNoSignalOff();
    }

    /** Set on timer. */
    @Override
    public boolean setOnTimer(TimeDate time) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setOnTimerValue(time);
        return true;
    }

    /** Set off timer. */
    @Override
    public boolean startOnTimer() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .startOnTimer();
        return true;
    }

    /** Set off timer. */
    @Override
    public boolean endOnTimer() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .endOnTimer();
        return true;
    }

    /** Get on timer */
    @Override
    public String getOnTimer() throws RemoteException {
        TimeDate time = IWEDIAService.getInstance().getDTVManager()
                .getSetupControl().getOnTimerValue();
        if (time == null) {
            return "00:00";
        }
        StringBuilder returnValueBuilder = new StringBuilder();
        int hour = time.getHour();
        int min = time.getMin();
        if (hour < 10) {
            returnValueBuilder.append("0" + hour);
        } else {
            returnValueBuilder.append("" + hour);
        }
        returnValueBuilder.append(":");
        if (min < 10) {
            returnValueBuilder.append("0" + min);
        } else {
            returnValueBuilder.append("" + min);
        }
        return returnValueBuilder.toString();
    }

    /** Set on timer repeat mode. */
    @Override
    public boolean setOnTimerRepeat(TimerRepeatMode repeatMode)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .setOnTimerRepeat(repeatMode);
        return true;
    }

    /** Get on timer repeat mode. */
    @Override
    public TimerRepeatMode getOnTimerRepeat() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .getOnTimerRepeat();
    }

    /** Set event and start no signal off timer. */
    @Override
    public boolean offSignalTimerStatusUpdate(OffSignalTimerEvent event)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .offSignalTimerStatusUpdate(event);
        return true;
    }

    /** Start no operation off timer. */
    @Override
    public boolean offOperationTimerStart() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .offOperationTimerStart();
        return true;
    }

    /** Set default values for timers settings. */
    @Override
    public boolean resetTimersSettings() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .resetTimersSettings();
        return true;
    }

    @Override
    public boolean saveSettingsToUSB(String mediaPath) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .storeSettings(mediaPath);
        return true;
    }

    @Override
    public boolean loadSettingsFromUSB(String mediaPath) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .loadSettings(mediaPath);
        return true;
    }

    @Override
    public boolean resetSettingsInStoreMode() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .resetSettingsInStoreMode();
        Editor editor = IWEDIAService.getInstance().getPreferenceManager()
                .edit();
        editor.clear();
        editor.commit();
        IWEDIAService.getInstance().getStorageManager().deleteDatabase();
        return true;
    }

    @Override
    public boolean rebootTV() throws RemoteException {
        final PowerManager power = (PowerManager) IWEDIAService.getInstance()
                .getSystemService(Context.POWER_SERVICE);
        power.reboot("fav");
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "reboot-end");
        }
        return false;
    }

    @Override
    public boolean factoryMode(boolean mode) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSetupControl()
                .factoryMode(mode);
        return true;
    }

    @Override
    public void registerCallback(ISetupCallback eventCallback)
            throws RemoteException {
        setupCallback = eventCallback;
    }

    private static com.iwedia.dtv.setup.ISetupCallback eventsCallback = new com.iwedia.dtv.setup.ISetupCallback() {
        @Override
        public void offTimerChanged() {
            try {
                setupCallback.offTimerChanged();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void unregisterCallback(ISetupCallback arg0) throws RemoteException {
        // TODO Auto-generated method stub
    }

    public static com.iwedia.dtv.setup.ISetupCallback getSetupCallback() {
        return eventsCallback;
    }
}
