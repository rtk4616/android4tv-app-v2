package com.iwedia.service.subtitle;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.ISubtitleControl;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.system.SystemControl;
import com.iwedia.service.system.language_and_keyboard.LanguageManager;
import com.iwedia.dtv.subtitle.SubtitleType;
import com.iwedia.dtv.subtitle.SubtitleMode;
import com.iwedia.dtv.types.InternalException;

public class SubtitleControl extends ISubtitleControl.Stub implements
        IDTVInterface {
    public SubtitleControl() {
    }

    private final String LOG_TAG = "SubtitleControl";

    @Override
    public int getSubtitleLanguage() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSubtitleControl()
                .getSubtitleTrackCount(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public SubtitleType getSubtitleType() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .getSubtitleType();
    }

    @Override
    public boolean hide() throws RemoteException {
        boolean state = true;
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getSubtitleControl()
                    .deselectCurrentSubtitleTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
        } catch (InternalException e) {
            state = false;
            e.printStackTrace();
        }
        SystemControl.broadcastSubtitleHide();
        return state;
    }

    @Override
    public boolean setSubtitleType(SubtitleType type) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .setSubtitleType(type);
        return true;
    }

    @Override
    public boolean show() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "show");
        }
        boolean state = false;
        SystemControl.broadcastSubtitleShow();
        return state;
    }

    @Override
    public boolean setCurrentSubtitleTrack(int index) throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getSubtitleControl()
                    .setCurrentSubtitleTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute(), index);
            return true;
        } catch (InternalException e) {
            return false;
        }
    }

    @Override
    public int getCurrentSubtitleTrackIndex() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSubtitleControl()
                .getCurrentSubtitleTrackIndex(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public int getSubtitleTrackCount() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSubtitleControl()
                .getSubtitleTrackCount(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public String getSubtitleTrack(int index) throws RemoteException {
        String languageToDisplay = LanguageManager.getInstance()
                .convertTrigramsToLanguage(
                        IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getSubtitleControl()
                                .getSubtitleTrack(
                                        IWEDIAService.getInstance()
                                                .getDtvManagerProxy()
                                                .getCurrentLiveRoute(), index)
                                .getLanguage(), true);
        return languageToDisplay;
    }

    /**
     * Returns subtitle enabled.
     */
    @Override
    public boolean getSubtitleEnabled() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .isAutomaticSubtitleDisplayEnabled();
    }

    /**
     * Sets subtitle enabled state.
     */
    @Override
    public void setSubtitleEnabled(boolean state) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .enableAutomaticSubtitleDisplay(state);
    }

    @Override
    public void channelZapping(boolean status) {
    }

    @Override
    public int getFirstSubtitleLanguage() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .getFirstSubtitleLanguage();
    }

    @Override
    public int getSecondSubtitleLanguage() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .getSecondSubtitleLanguage();
    }

    @Override
    public boolean setFirstSubtitleLanguage(int arg0) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .setFirstSubtitleLanguage(arg0);
        return true;
    }

    @Override
    public boolean setSecondSubtitleLanguage(int arg0) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .setSecondSubtitleLanguage(arg0);
        return true;
    }

    @Override
    public SubtitleMode getSubtitleMode() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .getSubtitleMode();
    }

    @Override
    public boolean setSubtitleMode(SubtitleMode mode) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .setSubtitleMode(mode);
        return true;
    }

    @Override
    public boolean resetSubtitleSettings() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSubtitleControl()
                .resetSubtitleSettings();
        return true;
    }
}
