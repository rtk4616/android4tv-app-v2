package com.iwedia.service.video;

import android.os.RemoteException;

import com.iwedia.comm.IVideoControl;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.dtv.video.VideoTrack;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;

public class VideoControl extends IVideoControl.Stub implements IDTVInterface {
    @Override
    public VideoTrack getCurrentVideoTrack() throws RemoteException {
        return getVideoTrack(IWEDIAService
                .getInstance()
                .getDTVManager()
                .getVideoControl()
                .getCurrentVideoTrackIndex(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute()));
    }

    @Override
    public VideoTrack getVideoTrack(int index) throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getVideoControl()
                .getVideoTrack(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), index);
    }

    @Override
    public int getVideoTrackCount() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getVideoControl()
                .getVideoTrackCount(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public void setCurrentVideoTrack(int currentVideoTrackIndex)
            throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getVideoControl()
                    .setCurrentVideoTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute(),
                            currentVideoTrackIndex);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void videoBlank(int arg0, boolean arg1) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getVideoControl()
                .videoBlank(
                        (int) IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), arg1);
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    /**
     * Deselects currently active video track and makes it inactive.
     */
    @Override
    public void deselectCurrentVideoTrack() throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getVideoControl()
                    .deselectCurrentVideoTrack(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current video track index.
     * 
     * @return index of the current video track
     */
    @Override
    public int getCurrentVideoTrackIndex() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getVideoControl()
                .getCurrentVideoTrackIndex(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }
}
