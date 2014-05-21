package com.iwedia.service.teletext;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.ITeletextControl;
import com.iwedia.dtv.dtvmanager.DTVManager;
import com.iwedia.comm.teletext.TeletextMode;
import com.iwedia.dtv.teletext.TeletextTrack;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.dtv.types.UserControl;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.system.SystemControl;
import com.iwedia.service.system.language_and_keyboard.LanguageManager;

public class TeletextControl extends ITeletextControl.Stub implements
        IDTVInterface {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "TeletextControl";
    private final int TELETEXT_TRANSPARENCY_OFF = 0;
    private final int TELETEXT_TRANSPARENCY = 255;
    private TeletextMode teletextMode = TeletextMode.OFF;

    public boolean sendInputControl(int key, UserControl ctrl)
            throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "sendInputControl(" + ctrl + ", " + key + ")");
        }
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getTeletextControl()
                .sendInputControl(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), ctrl, key);
        return true;
    }

    public boolean getTeletextState() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "getTeletextState()");
        }
        return false;
    }

    public boolean setCurrentTeletextTrack(int index) throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "setCurrentTeletextTrack(" + index + ")");
        }
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getTeletextControl()
                    .setCurrentTeletextTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute(), index);
            return true;
        } catch (InternalException e) {
            return false;
        }
    }

    public boolean deselectCurrentTeletextTrack() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "deselectCurrentTeletextTrack");
        }
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getTeletextControl()
                    .deselectCurrentTeletextTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
            return true;
        } catch (InternalException e) {
            return false;
        }
    }

    public int getCurrentTeletextTrackIndex() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "getCurrentTeletextTrackIndex()");
        }
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getTeletextControl()
                .getCurrentTeletextTrackIndex(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    public TeletextMode getTeletextMode() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "getTeletextMode()");
        }
        return teletextMode;
    }

    public boolean setTeletextMode(TeletextMode mode) throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "setTeletextMode(" + mode + ")");
        }
        this.teletextMode = mode;
        return true;
    }

    public TeletextTrack getTeletextTrack(int index) throws RemoteException {
        TeletextTrack track = IWEDIAService
                .getInstance()
                .getDTVManager()
                .getTeletextControl()
                .getTeletextTrack(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), index);
        String languageToDisplay = LanguageManager.getInstance()
                .convertTrigramsToLanguage(track.getLanguage(), true);
        return new TeletextTrack(track, languageToDisplay);
    }

    public int getTeletextTrackCount() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "getTeletextTrackCount()");
        }
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getTeletextControl()
                .getTeletextTrackCount(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    @Override
    public int getFirstTeletextLanguage() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getTeletextControl()
                .getFirstTeletextLanguage();
    }

    @Override
    public int getSecondTeletextLanguage() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getTeletextControl()
                .getSecondTeletextLanguage();
    }

    @Override
    public boolean setFirstTeletextLanguage(int arg0) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getTeletextControl()
                .setFirstTeletextLanguage(arg0);
        return true;
    }

    @Override
    public boolean setSecondTeletextLanguage(int arg0) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getTeletextControl()
                .setSecondTeletextLanguage(arg0);
        return true;
    }

    @Override
    public boolean setTeletextBgAlpha(int alpha) throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "setTeletextBgAlpha (" + alpha + ")");
        }
        IWEDIAService.getInstance().getDTVManager().getTeletextControl()
                .setTeletextBgAlpha(alpha);
        return true;
    }
}
