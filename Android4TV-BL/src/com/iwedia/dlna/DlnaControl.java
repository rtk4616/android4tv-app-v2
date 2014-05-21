package com.iwedia.dlna;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IDlnaCallback;
import com.iwedia.comm.IDlnaControl;
import com.iwedia.dlna.dmr.service.DmrEvent;
import com.iwedia.dlna.dmr.service.IDlnaDefaultListener;
import com.iwedia.service.proxyservice.IDTVInterface;

public class DlnaControl extends IDlnaControl.Stub implements IDTVInterface {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "DTVManagerProxy.DlnaControl";
    private static boolean isDtcp = false;
    private static boolean serverCreated = false;
    private static boolean rendererCreated = false;
    private static int dtcp_port = 10080;
    DlnaDMR MRenderer;
    DlnaDMS MServer;
    DlnaStreamParam param;
    private String mime = "";
    private String url = "http://192.168.232.112:8983/Live_Stream.ts";
    private String dtcp = "0";
    private int port = 0;
    final static RemoteCallbackList<IDlnaCallback> mDlnaCallbackManager = new RemoteCallbackList<IDlnaCallback>();

    private static String getUsbPath() {
        String usbPath = "/storage/usb/";
        try {
            Process proc = Runtime.getRuntime().exec("ls /storage/usb");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            String line;
            try {
                if ((line = in.readLine()) != null) {
                    usbPath += line;
                    System.out.println(usbPath);
                }
            } finally {
                in.close();
            }
            proc.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("usbPath: " + usbPath);
        return usbPath;
    }

    public void deinitDlna() throws RemoteException {
        Log.i(LOG_TAG, "deinitDlna()");
        if (rendererCreated) {
            if (MRenderer.isStarted()) {
                Log.i(LOG_TAG, "Stoping renderer ...");
                try {
                    MRenderer.stop();
                } catch (DlnaException e1) {
                    e1.printStackTrace();
                }
            }
            Log.i(LOG_TAG, "Terminate renderer ...");
            MRenderer.terminate();
            rendererCreated = false;
        }
        if (serverCreated) {
            if (MServer.isStarted()) {
                Log.i(LOG_TAG, "Stoping server ...");
                try {
                    MServer.stop();
                } catch (DlnaException e1) {
                    e1.printStackTrace();
                }
            }
            Log.i(LOG_TAG, "Terminate server ...");
            MServer.terminate();
            serverCreated = false;
        }
        try {
            Log.i(LOG_TAG, "Stoping player ...");
            MultimediaManager.mPlayerControl.stop();
        } catch (DlnaException e1) {
            e1.printStackTrace();
        }
        MultimediaManager.mPlayerControl.terminate();
        try {
            Log.i(LOG_TAG, "nativeDmcService deinitDlna()");
            if (MultimediaManager.nativeDmcService != null) {
                MultimediaManager.nativeDmcService.getDlnaService().deinit();
            } else {
                Log.i(LOG_TAG, "DLNA DMC Service not running");
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        try {
            Log.i(LOG_TAG, "nativeService deinitDlna()");
            if (MultimediaManager.nativeService.getDlnaService() != null) {
                MultimediaManager.nativeService.getDlnaService().deinit();
            } else {
                Log.i(LOG_TAG, "DLNA DMS Service not running");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            Log.i(LOG_TAG, "nativeDmrService deinitDlna()");
            if (MultimediaManager.nativeDmrService.getDlnaService() != null) {
                MultimediaManager.nativeDmrService.getDlnaService().deinit();
            } else {
                Log.i(LOG_TAG, "DLNA DMR Service not running");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean getServerStatus() throws RemoteException {
        if (serverCreated) {
            if (MServer.isStarted()) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean getRendererStatus() throws RemoteException {
        if (rendererCreated) {
            if (MRenderer.isStarted()) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public boolean startDlnaRenderer(String friendlyName)
            throws RemoteException {
        DlnaDeviceIpAddress deviceAddress = new DlnaDeviceIpAddress();
        try {
            rendererCreated = true;
            Log.i(LOG_TAG, "Start new DLNA Renderer... " + friendlyName);
            MRenderer = new DlnaDMR(MultimediaManager.getNativeDmrService(),
                    "DLNA_Renderer", "");
            MRenderer.setFrandlyName(
                    friendlyName + "(" + deviceAddress.getDeviceIpAddress()
                            + ")", "DMR");
            MRenderer.setManufacturerName("iWedia", "DMR");
            MRenderer.setManufacturerURL("http://www.iwedia.com", "DMR");
            MRenderer.setModelName("iWedia DLNA Demo", "DMR");
            MRenderer
                    .setModelDesc("iWedia - DLNA v1.5 Demo application", "DMR");
            MRenderer.setModelNumber("2.0", "DMR");
            MRenderer
                    .setModelURL(
                            "http://www.iwedia.com/software-components/dlna-and-dtcp-ip",
                            "DMR");
            MRenderer.start();
            MRenderer.setFriendlyName(friendlyName + "("
                    + deviceAddress.getDeviceIpAddress() + ")");
        } catch (DlnaException e) {
            e.printStackTrace();
        }
        IDlnaDefaultListener mDlnaDefaultListener = new IDlnaDefaultListener.Stub() {
            @Override
            public void handleEvent(int arg0, DmrEvent arg1)
                    throws RemoteException {
                System.out.println("DlnaRendererEvent event: " + arg0);
                if (arg0 == 5) {
                    System.out.println("DlnaRendererPlayEvent");
                    sDlnaPlayRendererEvent(arg1.getDmrObject()
                            .getDmrObjectUri(), arg1.getDmrObject()
                            .getDmrObjectFriendlyName(), arg1.getDmrObject()
                            .getDmrObjectMime());
                } else if (arg0 == 6) {
                    System.out.println("DlnaRendererStopEvent");
                    sDlnaStopRendererEvent();
                } else if (arg0 == 7) {
                    System.out.println("DlnaRendererPauseEvent");
                    sDlnaPauseRendererEvent();
                } else if (arg0 == 8) {
                    System.out.println("DlnaRendererResumeEvent");
                    sDlnaResumeRendererEvent();
                } else if (arg0 == 9) {
                    System.out.println("DlnaRendererVolumeEvent");
                } else if (arg0 == 10) {
                    System.out.println("DlnaRendererMuteEvent");
                } else if (arg0 == 11) {
                    int iSeekTo;
                    System.out.println("DlnaRendererSeekToEvent");
                    String sSeekTo = arg1.getDmrSeekValue();
                    Log.e("JAVA-String", sSeekTo);
                    int k = sSeekTo.lastIndexOf(':');
                    String tail = sSeekTo.substring(k + 1);
                    int iSeekTo1 = Integer.parseInt(tail);
                    sSeekTo = sSeekTo.substring(0, k);
                    k = sSeekTo.lastIndexOf(':');
                    tail = sSeekTo.substring(k + 1);
                    int iSeekTo2 = Integer.parseInt(tail);
                    String head = sSeekTo.substring(0, k);
                    int iSeekTo3 = Integer.parseInt(head);
                    iSeekTo = (iSeekTo3 * 3600 + iSeekTo2 * 60 + iSeekTo1) * 1000;
                    sDlnaSeekToRendererEvent(iSeekTo);
                } else if (arg0 == 12) {
                    System.out.println("DlnaRendererPositionEvent");
                    sDlnaPositionRendererEvent();
                }
            }
        };
        MultimediaManager.getNativeDmrService().getDlnaService()
                .registerDlnaDefaultListener(mDlnaDefaultListener);
        return true;
    }

    @Override
    public boolean startDlnaServer(String friendlyName) throws RemoteException {
        File Server_base = new File("/data/data/com.iwedia.service/dlna.sqlite");
        if (Server_base.exists()) {
            Server_base.delete();
        }
        String usbFullPath = getUsbPath();
        DlnaDeviceIpAddress deviceAddress = new DlnaDeviceIpAddress();
        try {
            serverCreated = true;
            System.out.println("Start new DLNA Server");
            MServer = new DlnaDMS(MultimediaManager.getNativeService(),
                    "DLNA_Server", "/data/data/com.iwedia.service/dlna.sqlite",
                    "");
            MServer.setManufacturerName("iWedia", "DMS");
            MServer.setManufacturerURL("http://www.iwedia.com", "DMS");
            MServer.setModelName("iWedia DLNA Demo", "DMS");
            MServer.setModelDesc("iWedia - DLNA v1.5 Demo application", "DMS");
            MServer.setModelNumber("2.0", "DMS");
            MServer.setModelURL(
                    "http://www.iwedia.com/software-components/dlna-and-dtcp-ip",
                    "DMS");
            MServer.setFrandlyName(
                    friendlyName + "(" + deviceAddress.getDeviceIpAddress()
                            + ")", "DMS");
            MServer.setFriendlyName(friendlyName + "("
                    + deviceAddress.getDeviceIpAddress() + ")");
            if (isDtcp) {
                System.out.println("Starting server with dtcp port: "
                        + dtcp_port);
                MServer.setDtcpPort(dtcp_port);
                MServer.start(dtcp_port);
            } else {
                MServer.start(-1);
            }
            param = new DlnaStreamParam();
            url = ("http://" + deviceAddress.getDeviceIpAddress() + ":8983/Live_Stream.ts");
            /*
             * param.setDtcp_host(dtcp); param.setMime(mime);
             * param.setPort(port); param.setProfile(0); param.setUri(url);
             * MServer.shareStreamExt(param);
             */
            MServer.share(usbFullPath);
        } catch (DlnaException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean changeDMSName(String friendlyName) throws RemoteException {
        DlnaDeviceIpAddress deviceAddress = new DlnaDeviceIpAddress();
        if (DEBUG) {
            Log.i(LOG_TAG, "Change DMS name");
        }
        try {
            if (serverCreated) {
                if (MServer.isStarted()) {
                    try {
                        MServer.stop();
                    } catch (DlnaException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Change DLNA Server name");
                MServer.setFrandlyName(
                        friendlyName + "(" + deviceAddress.getDeviceIpAddress()
                                + ")", "DMS");
                MServer.setFriendlyName(friendlyName + "("
                        + deviceAddress.getDeviceIpAddress() + ")");
                if (isDtcp) {
                    System.out.println("Starting server with dtcp port: "
                            + dtcp_port);
                    MServer.setDtcpPort(dtcp_port);
                    MServer.start(dtcp_port);
                } else {
                    MServer.start(-1);
                }
            }
        } catch (DlnaException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean changeDMRName(String friendlyName) throws RemoteException {
        DlnaDeviceIpAddress deviceAddress = new DlnaDeviceIpAddress();
        if (DEBUG) {
            Log.e(LOG_TAG, "Change DMR name");
        }
        try {
            if (rendererCreated) {
                if (MRenderer.isStarted()) {
                    try {
                        MRenderer.stop();
                    } catch (DlnaException e) {
                        e.printStackTrace();
                    }
                }
                Log.i(LOG_TAG, "Changing DLNA Renderer name to: "
                        + friendlyName);
                MRenderer.setFrandlyName(
                        friendlyName + "(" + deviceAddress.getDeviceIpAddress()
                                + ")", "DMR");
                MRenderer.setFriendlyName(friendlyName + "("
                        + deviceAddress.getDeviceIpAddress() + ")");
                MRenderer.start();
            }
        } catch (DlnaException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean stopDlnaRenderer() throws RemoteException {
        MultimediaManager.getNativeDmrService().getDlnaService()
                .unRegisterDlnaDefaultListener();
        if (DEBUG) {
            Log.e(LOG_TAG, "stopDlnaRenderer");
        }
        try {
            MRenderer.stop();
        } catch (DlnaException e) {
            e.printStackTrace();
        }
        if (DEBUG) {
            Log.e(LOG_TAG, "terminateDlnaRenderer");
        }
        MRenderer.terminate();
        rendererCreated = false;
        return true;
    }

    @Override
    public boolean stopDlnaServer() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "stopDlnaServer(");
        }
        try {
            MServer.stop();
        } catch (DlnaException e) {
            e.printStackTrace();
        }
        MServer.terminate();
        serverCreated = false;
        return true;
    }

    @Override
    public boolean notifyDlnaRenderer(int notifyType, int notifyValue,
            String position) throws RemoteException {
        if (DEBUG) {
            Log.i(LOG_TAG, "notifyDlnaRenderer-> notifyType: " + notifyType
                    + "; notifyValue : " + notifyValue + "; position: "
                    + position);
        }
        synchronized (MRenderer) {
            try {
                MRenderer.dlnaSetNotify(notifyType, notifyValue, position);
            } catch (DlnaException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public static void sDlnaPlayRendererEvent(String uri, String friendlyName,
            String mime) {
        synchronized (mDlnaCallbackManager) {
            int i = mDlnaCallbackManager.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mDlnaCallbackManager.getBroadcastItem(i)
                            .dlnaPlayRendererEvent(uri, friendlyName, mime);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mDlnaCallbackManager.unregister(mDlnaCallbackManager
                            .getBroadcastItem(i));
                }
            }
            mDlnaCallbackManager.finishBroadcast();
        }
    }

    public static void sDlnaPauseRendererEvent() {
        synchronized (mDlnaCallbackManager) {
            int i = mDlnaCallbackManager.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mDlnaCallbackManager.getBroadcastItem(i)
                            .dlnaPauseRendererEvent();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mDlnaCallbackManager.unregister(mDlnaCallbackManager
                            .getBroadcastItem(i));
                }
            }
            mDlnaCallbackManager.finishBroadcast();
        }
    }

    public static void sDlnaResumeRendererEvent() {
        synchronized (mDlnaCallbackManager) {
            int i = mDlnaCallbackManager.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mDlnaCallbackManager.getBroadcastItem(i)
                            .dlnaResumeRendererEvent();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mDlnaCallbackManager.unregister(mDlnaCallbackManager
                            .getBroadcastItem(i));
                }
            }
            mDlnaCallbackManager.finishBroadcast();
        }
    }

    public static void sDlnaStopRendererEvent() {
        synchronized (mDlnaCallbackManager) {
            int i = mDlnaCallbackManager.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mDlnaCallbackManager.getBroadcastItem(i)
                            .dlnaStopRendererEvent();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mDlnaCallbackManager.unregister(mDlnaCallbackManager
                            .getBroadcastItem(i));
                }
            }
            mDlnaCallbackManager.finishBroadcast();
        }
    }

    public static void sDlnaPositionRendererEvent() {
        synchronized (mDlnaCallbackManager) {
            int i = mDlnaCallbackManager.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mDlnaCallbackManager.getBroadcastItem(i)
                            .dlnaPositionRendererEvent();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mDlnaCallbackManager.unregister(mDlnaCallbackManager
                            .getBroadcastItem(i));
                }
            }
            mDlnaCallbackManager.finishBroadcast();
        }
    }

    public static void sDlnaSeekToRendererEvent(int seekTo) {
        synchronized (mDlnaCallbackManager) {
            int i = mDlnaCallbackManager.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mDlnaCallbackManager.getBroadcastItem(i)
                            .dlnaSeekToRendererEvent(seekTo);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mDlnaCallbackManager.unregister(mDlnaCallbackManager
                            .getBroadcastItem(i));
                }
            }
            mDlnaCallbackManager.finishBroadcast();
        }
    }

    private static boolean isClbkRegistered = false;

    @Override
    public void registerCallback(IDlnaCallback dlnaCallback)
            throws RemoteException {
        if (isClbkRegistered == false) {
            isClbkRegistered = mDlnaCallbackManager.register(dlnaCallback);
        } else {
            Log.d(LOG_TAG, "registerCallback: Callback is already registered!");
        }
    }

    @Override
    public void unregisterCallback(IDlnaCallback dlnaCallback)
            throws RemoteException {
        if (isClbkRegistered == true) {
            mDlnaCallbackManager.unregister(dlnaCallback);
            isClbkRegistered = false;
        } else {
            Log.d(LOG_TAG, "unregisterCallback: Callback is not registered!");
        }
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}
