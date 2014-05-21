package com.iwedia.service.vod;

import android.os.RemoteException;

import com.iwedia.comm.IOnDemandControl;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;

/**
 * Video on demand module.
 * 
 * @author Marko Zivanovic
 */
public class VideoOnDemandControl extends IOnDemandControl.Stub implements
        IDTVInterface {
    /**
     * Pause an on demand stream.
     * 
     * @return true if everything is ok, else false
     */
    @Override
    public boolean pause() throws RemoteException {
        return false;
    }

    /**
     * Resume an on demand stream.
     * 
     * @return true if everything is ok, else false
     */
    @Override
    public boolean resume() throws RemoteException {
        return false;
    }

    /**
     * Seek to a wanted position in the on demand stream.
     * 
     * @param seconds
     *        - seek to position on the given seconds value
     * @return true if everything is ok, else false
     */
    @Override
    public boolean seek(int seconds) throws RemoteException {
        return false;
    }

    /**
     * Set speed - fast forward or rewind the on demand stream.
     * 
     * @param speed
     *        - set playback speed (1 normal, 2,4,8... FF; -1, -2,-4,-8... RW)
     * @return true if everything is ok, else false
     */
    @Override
    public boolean setSpeed(int speed) throws RemoteException {
        return false;
    }

    /**
     * Start an on demand stream.
     * 
     * @param url
     *        - url to the on demand stream.
     * @return true if everything is ok, else false
     */
    @Override
    public boolean start(String url) throws RemoteException {
        return false;
    }

    /**
     * Stop an on demand stream.
     * 
     * @return true if everything is ok, else false
     */
    @Override
    public boolean stop() throws RemoteException {
        return false;
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}
