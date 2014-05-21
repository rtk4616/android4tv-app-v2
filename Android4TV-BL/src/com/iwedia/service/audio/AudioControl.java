package com.iwedia.service.audio;

import android.os.RemoteException;

import com.iwedia.comm.IAudioControl;
import com.iwedia.dtv.audio.AudioTrack;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.system.language_and_keyboard.LanguageManager;

public class AudioControl extends IAudioControl.Stub implements IDTVInterface {
    @Override
    public int getAudioTrackCount() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .getAudioTrackCount(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public int getCurrentAudioTrackIndex() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .getCurrentAudioTrackIndex(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public void setCurrentAudioTrack(int currentAudioTrackIndex)
            throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getAudioControl()
                    .setCurrentAudioTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute(),
                            currentAudioTrackIndex);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AudioTrack getAudioTrack(int index) throws RemoteException {
        int liveRouteId = IWEDIAService.getInstance().getDtvManagerProxy()
                .getCurrentLiveRoute();
        com.iwedia.dtv.audio.AudioTrack dtvAudioTrack = IWEDIAService
                .getInstance().getDTVManager().getAudioControl()
                .getAudioTrack(liveRouteId, index);
        return dtvAudioTrack;
    }

    @Override
    public void deselectCurrentAudioTrack() throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getAudioControl()
                    .deselectCurrentAudioTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    @Override
    public int getFirstAudioLanguage() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getAudioControl()
                .getFirstAudioLanguage();
    }

    @Override
    public int getSecondAudioLanguage() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getAudioControl()
                .getSecondAudioLanguage();
    }

    @Override
    public void setFirstAudioLanguage(int arg0) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getAudioControl()
                .setFirstAudioLanguage(arg0);
    }

    @Override
    public void setSecondAudioLanguage(int arg0) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getAudioControl()
                .setSecondAudioLanguage(arg0);
    }
}
