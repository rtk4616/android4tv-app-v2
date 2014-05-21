package com.iwedia.service.hbb;

import android.os.RemoteException;

import com.iwedia.comm.IHbbTvCallback;
import com.iwedia.comm.IHbbTvControl;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;

public class HbbTvControl extends IHbbTvControl.Stub implements IDTVInterface {
    public static IHbbTvCallback mHbbTVCallback;

    @Override
    public void setCallbackHbb(IHbbTvCallback hbbCallback)
            throws RemoteException {
        mHbbTVCallback = hbbCallback;
    }

    private static com.iwedia.dtv.hbbtv.IHbbTvCallback hbbTvCallback = new com.iwedia.dtv.hbbtv.IHbbTvCallback() {
        @Override
        public void createApplication(String uri) {
            if (mHbbTVCallback != null) {
                try {
                    mHbbTVCallback.createApplication(uri);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void destroyApplication() {
            if (mHbbTVCallback != null) {
                try {
                    mHbbTVCallback.destroyApplication();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void hideApplication() {
            if (mHbbTVCallback != null) {
                try {
                    mHbbTVCallback.hideApplication();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void setKeyMask(int mask) {
            if (mHbbTVCallback != null) {
                try {
                    mHbbTVCallback.setKeyMask(mask);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void showApplication() {
            if (mHbbTVCallback != null) {
                try {
                    mHbbTVCallback.showApplication();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static com.iwedia.dtv.hbbtv.IHbbTvCallback getHbbTvCallback() {
        return hbbTvCallback;
    }

    @Override
    public void unsetCallbackHbb(IHbbTvCallback arg0) throws RemoteException {
        mHbbTVCallback = null;
    }

    @Override
    public boolean isHbbEnabled() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getHbbTvControl()
                .isHbbTvEnabled(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public boolean enableHBB() throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getHbbTvControl()
                    .enableHbbTv(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean disableHBB() throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getHbbTvControl()
                    .disableHbbTv(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deselectHBBTVComponent() throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getHbbTvControl()
                    .deselectHbbTvComponent(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isHBBTVComponentSelected() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getHbbTvControl()
                .isHbbTvComponentSelected(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public boolean selectHBBTVComponent() throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getHbbTvControl()
                    .selectHbbTvComponent(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute());
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean notifyAppMngr(int arg0, String arg1) throws RemoteException {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getHbbTvControl()
                    .notifyAppMngr(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute(), arg0, arg1);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int getHbbState() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getHbbTvControl()
                .getHbbState();
    }
}
