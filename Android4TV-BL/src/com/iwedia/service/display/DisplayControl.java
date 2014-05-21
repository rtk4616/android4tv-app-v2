package com.iwedia.service.display;

import android.os.RemoteException;
import android.view.Surface;

import com.iwedia.comm.IDisplayControl;
import com.iwedia.dtv.display.SurfaceBundle;
import com.iwedia.service.IWEDIAService;
import com.iwedia.dtv.display.SurfaceBundle;
import com.iwedia.dtv.types.InternalException;

public class DisplayControl extends IDisplayControl.Stub {
    @Override
    public Surface getVideoLayerSurface(int layer) throws RemoteException {
        Surface surface = IWEDIAService.getInstance().getDTVManager()
                .getDisplayControl().getVideoLayerSurface(layer);
        return surface;
    }

    @Override
    public int setVideoLayerSurface(int layer, SurfaceBundle handle)
            throws RemoteException {
        try {
            IWEDIAService.getInstance().getDTVManager().getDisplayControl()
                    .setVideoLayerSurface(layer, handle);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void scaleWindow(int x, int y, int width, int height)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getDisplayControl()
                .scaleWindow(x, y, width, height);
        return;
    }
}
