package com.iwedia.service.streamcomponent;

import android.os.RemoteException;

import com.iwedia.comm.IStreamComponentCallback;
import com.iwedia.comm.IStreamComponentControl;
import com.iwedia.dtv.streamcomponent.StreamComponentType;
import com.iwedia.service.proxyservice.IDTVInterface;

/**
 * The Stream Component controller.
 * 
 * @author Sasa Jagodin
 */
public class StreamComponentControl extends IStreamComponentControl.Stub
        implements IDTVInterface {
    private static final String LOG_TAG = "StreamComponentControl";
    public static IStreamComponentCallback streamCallback;
    private static com.iwedia.dtv.streamcomponent.IStreamComponentCallback streamComponentCallback = new com.iwedia.dtv.streamcomponent.IStreamComponentCallback() {
        @Override
        public void componentChanged(int routeID,
                StreamComponentType componentType) {
            // try {
            // // streamCallback.componentChanged(routeID, componentType);
            // } catch (RemoteException e) {
            // e.printStackTrace();
            // }
        }
    };

    public static com.iwedia.dtv.streamcomponent.IStreamComponentCallback getStreamComponentControlCallback() {
        return streamComponentCallback;
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    @Override
    public void registerCallback(
            IStreamComponentCallback mStreamComponentCallback)
            throws RemoteException {
        streamCallback = mStreamComponentCallback;
    }

    @Override
    public void unregisterCallback(
            IStreamComponentCallback mStreamComponentCallback)
            throws RemoteException {
        streamCallback = null;
    }
}
