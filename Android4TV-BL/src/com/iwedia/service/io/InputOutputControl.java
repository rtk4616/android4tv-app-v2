package com.iwedia.service.io;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IInputOutputCallback;
import com.iwedia.comm.IInputOutputControl;
import com.iwedia.dtv.io.AnalogVideoType;
import com.iwedia.dtv.io.AspectRatioOutput;
import com.iwedia.dtv.io.AudioOutputMode;
import com.iwedia.dtv.io.LastInputDescriptor;
import com.iwedia.dtv.io.VideoScanning;
import com.iwedia.dtv.route.common.RouteInputOutputDescriptor;
import com.iwedia.dtv.types.AudioDigitalType;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.dtv.types.VideoResolution;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.InputSettings;

public class InputOutputControl extends IInputOutputControl.Stub {
    public static boolean DEBUG = true;
    public static final String LOG_TAG = "InputOutputControl";
    private static Object lock = new Object();
    final static RemoteCallbackList<IInputOutputCallback> mInputDeviceCallback = new RemoteCallbackList<IInputOutputCallback>();
    private RouteInputOutputDescriptor activeDevice = null;
    private int activeDeviceIndex = -1;
    private static InputOutputControl instance = null;

    private InputOutputControl() {
    }

    public static InputOutputControl getInstance() {
        if (instance == null) {
            instance = new InputOutputControl();
        }
        return instance;
    }

    public void ioDeviceResetActiveDevice() throws RemoteException {
        activeDevice = null;
        activeDeviceIndex = -1;
    }

    @Override
    public int ioDeviceGetAudioDelay(int deviceIndex) throws RemoteException {
        int delay;
        delay = IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().deviceGetAudioDelay(deviceIndex);
        return delay;
    }

    @Override
    public AudioDigitalType ioDeviceGetDigitalAudioEncodingMode(int deviceIndex)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public VideoResolution ioDeviceGetResolution(int deviceIndex)
            throws RemoteException {
        VideoResolution resolution = null;
        if (activeDevice != null) {
            resolution = IWEDIAService.getInstance().getDTVManager()
                    .getInputOutputControl()
                    .deviceGetResolution(activeDeviceIndex);
        }
        return resolution;
    }

    @Override
    public int ioDeviceGetAudioChannels(int deviceIndex) throws RemoteException {
        int channels = 0;
        if (activeDevice != null) {
            channels = IWEDIAService.getInstance().getDTVManager()
                    .getInputOutputControl()
                    .deviceGetAudioChannels(activeDeviceIndex);
        }
        return channels;
    }

    @Override
    public int ioDeviceGetAudioSampleRate(int deviceIndex)
            throws RemoteException {
        int sampleRate = 0;
        if (activeDevice != null) {
            sampleRate = IWEDIAService.getInstance().getDTVManager()
                    .getInputOutputControl()
                    .deviceGetAudioSampleRate(activeDeviceIndex);
        }
        return sampleRate;
    }

    @Override
    public int ioDeviceGetFrameRate(int deviceIndex) throws RemoteException {
        int frameRate = 0;
        if (activeDevice != null) {
            frameRate = IWEDIAService.getInstance().getDTVManager()
                    .getInputOutputControl()
                    .deviceGetFrameRate(activeDeviceIndex);
        }
        return frameRate;
    }

    @Override
    public VideoScanning ioDeviceGetVideoScanning(int deviceIndex)
            throws RemoteException {
        VideoScanning scanning = null;
        if (activeDevice != null) {
            scanning = IWEDIAService.getInstance().getDTVManager()
                    .getInputOutputControl()
                    .deviceGetVideoScanning(activeDeviceIndex);
        }
        return scanning;
    }

    @Override
    public AnalogVideoType ioDeviceGetVideoType(int deviceIndex)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int ioDeviceSetAudioDelay(int deviceIndex, int delay)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getInputOutputControl()
                .deviceSetAudioDelay(deviceIndex, delay);
        return 0;
    }

    @Override
    public int ioDeviceSetAudioOutputMode(int deviceIndex, AudioOutputMode mode)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getInputOutputControl()
                .deviceSetAudioOutputMode(deviceIndex, mode);
        return 0;
    }

    @Override
    public AudioOutputMode ioDeviceGetAudioOutputMode(int deviceIndex)
            throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().deviceGetAudioOutputMode(deviceIndex);
    }

    @Override
    public int ioDeviceSetDigitalAudioEncodingMode(int deviceIndex,
            AudioDigitalType encodingMode) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int ioDeviceSetResolution(int deviceIndex, int videoWidth,
            int videoHeight) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int ioDeviceSetVideoType(int deviceIndex,
            AnalogVideoType videoOutType) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int ioDeviceStart(int decoderId, int deviceIndex)
            throws RemoteException {
        int routeID = IWEDIAService.getInstance().getDtvManagerProxy()
                .getRouteManager().getInputRouteID(deviceIndex, decoderId);
        int ret;
        if (activeDevice != null) {
            try {
                IWEDIAService.getInstance().getDTVManager()
                        .getInputOutputControl().deviceStop(routeID);
            } catch (InternalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            IWEDIAService.getInstance().getDTVManager().getInputOutputControl()
                    .deviceStart(routeID);
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        activeDevice = ioGetDeviceDescriptor(deviceIndex);
        activeDeviceIndex = deviceIndex;
        notifyInputDeviceStarted(deviceIndex);
        return 0;
    }

    @Override
    public int ioDeviceStop(int decoderID, int deviceIndex)
            throws RemoteException {
        int routeID = IWEDIAService.getInstance().getDtvManagerProxy()
                .getRouteManager().getInputRouteID(deviceIndex, decoderID);
        if (activeDevice != null) {
            try {
                IWEDIAService.getInstance().getDTVManager()
                        .getInputOutputControl().deviceStop(routeID);
            } catch (InternalException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            notifyInputDeviceStopped(deviceIndex);
            activeDevice = null;
            activeDeviceIndex = -1;
        }
        return 0;
    }

    @Override
    public boolean ioGetDeviceActive(int deviceIndex) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().isDeviceActive(deviceIndex);
    }

    @Override
    public RouteInputOutputDescriptor ioGetDeviceDescriptor(int deviceIndex)
            throws RemoteException {
        Log.e(LOG_TAG, "ioGetDeviceDescriptor (" + deviceIndex + ")");
        RouteInputOutputDescriptor deviceDescriptor = IWEDIAService
                .getInstance().getDTVManager().getCommonRouteControl()
                .getInputOutputDescriptor(deviceIndex);
        Log.e(LOG_TAG, deviceDescriptor.toString());
        return deviceDescriptor;
    }

    @Override
    public boolean ioGetDeviceConnected(int deviceIndex) throws RemoteException {
        Log.e(LOG_TAG, "ioGetDeviceConnected (" + deviceIndex + ")");
        return IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().isDeviceConnected(deviceIndex);
    }

    @Override
    public boolean ioGetDeviceInput(int deviceIndex) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().isDeviceInput(deviceIndex);
    }

    @Override
    public boolean ioGetDeviceOutput(int deviceIndex) throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().isDeviceOutput(deviceIndex);
    }

    @Override
    public int ioGetDevicesCount() throws RemoteException {
        Log.e(LOG_TAG, "ioGetDevicesCount()");
        int count = IWEDIAService.getInstance().getDTVManager()
                .getCommonRouteControl().getInputOutputNumber();
        Log.e(LOG_TAG, "ioGetDevicesCount() count = " + count);
        return count;
    }

    @Override
    public boolean ioHdmiCecGetTvPowerOn() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int ioHdmiCecSetTvPowerOn(int deviceIndex) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean ioHdmiGetArc(int deviceIndex) throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean ioHdmiGetAutoLinkPowerOff() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean ioHdmiGetHdmiCec(int deviceIndex) throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean ioHdmiGetSpeakerOutput(int deviceIndex)
            throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void ioHdmiScanDevices() throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public int ioHdmiSetAutoLinkPowerOff(int autoLinkPowerOffStatus)
            throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int ioHdmiSetHdmiCec(int deviceIndex, int hdmiCecStatus)
            throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int ioHdmiSetSpeakerOutput(int deviceIndex, int speakerOutputStatus)
            throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean ioInit() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean ioTerm() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public AspectRatioOutput outputDeviceGetAspectRatio(int deviceIndex)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int outputDeviceSetAspectRatio(int deviceIndex,
            AspectRatioOutput ratio) throws RemoteException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public LastInputDescriptor ioGetLastInput() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().getLastInput();
    }

    public static void notifyInputDeviceStarted(int deviceIndex) {
        synchronized (lock) {
            int i = mInputDeviceCallback.beginBroadcast();
            if (i > 1) {
                Log.e("ActionControl", "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mInputDeviceCallback.getBroadcastItem(i)
                            .inputDeviceStarted(deviceIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mInputDeviceCallback.unregister(mInputDeviceCallback
                            .getBroadcastItem(i));
                }
            }
            mInputDeviceCallback.finishBroadcast();
        }
    }

    public static void notifyInputDeviceStopped(int deviceIndex) {
        synchronized (lock) {
            int i = mInputDeviceCallback.beginBroadcast();
            if (i > 1) {
                Log.e(LOG_TAG, "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mInputDeviceCallback.getBroadcastItem(i)
                            .inputDeviceStopped(deviceIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mInputDeviceCallback.unregister(mInputDeviceCallback
                            .getBroadcastItem(i));
                }
            }
            mInputDeviceCallback.finishBroadcast();
        }
    }

    public static void notifyInputDeviceConnected(int deviceIndex) {
        synchronized (lock) {
            int i = mInputDeviceCallback.beginBroadcast();
            if (i > 1) {
                Log.e("ActionControl", "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mInputDeviceCallback.getBroadcastItem(i)
                            .inputDeviceConnected(deviceIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mInputDeviceCallback.unregister(mInputDeviceCallback
                            .getBroadcastItem(i));
                }
            }
            mInputDeviceCallback.finishBroadcast();
        }
    }

    public static void notifyInputDeviceDisconnected(int deviceIndex) {
        synchronized (lock) {
            int i = mInputDeviceCallback.beginBroadcast();
            if (i > 1) {
                Log.e("ActionControl", "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mInputDeviceCallback.getBroadcastItem(i)
                            .inputDeviceDisconnected(deviceIndex);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mInputDeviceCallback.unregister(mInputDeviceCallback
                            .getBroadcastItem(i));
                }
            }
            mInputDeviceCallback.finishBroadcast();
        }
    }

    public static void notifyInputDeviceVideoSignalChanged(int deviceIndex,
            boolean signalAvailable) {
        synchronized (lock) {
            int i = mInputDeviceCallback.beginBroadcast();
            if (i > 1) {
                Log.e("ActionControl", "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mInputDeviceCallback.getBroadcastItem(i)
                            .inputDeviceVideoSignalChanged(deviceIndex,
                                    signalAvailable);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mInputDeviceCallback.unregister(mInputDeviceCallback
                            .getBroadcastItem(i));
                }
            }
            mInputDeviceCallback.finishBroadcast();
        }
    }

    public static void notifyInputDeviceAudioSignalChanged(int deviceIndex,
            boolean signalAvailable) {
        synchronized (lock) {
            int i = mInputDeviceCallback.beginBroadcast();
            if (i > 1) {
                Log.e("ActionControl", "More than one callback (" + i + ")");
            }
            while (i > 0) {
                i--;
                try {
                    mInputDeviceCallback.getBroadcastItem(i)
                            .inputDeviceAudioSignalChanged(deviceIndex,
                                    signalAvailable);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mInputDeviceCallback.unregister(mInputDeviceCallback
                            .getBroadcastItem(i));
                }
            }
            mInputDeviceCallback.finishBroadcast();
        }
    }

    public void ioDeviceStartDVB() {
        if (activeDevice != null) {
            try {
                ioDeviceStop(0, activeDeviceIndex);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            /*
             * This is a temporary hack to notify MainActivity to reactivate
             * switch ack to main view. Will be fixed when we have better state
             * handling in application
             */
            notifyInputDeviceStopped(-1);
        }
    }

    private static boolean isClbkRegistered = false;

    public static com.iwedia.dtv.io.IInputOutputCallback getIoCallback() {
        return dtvServiceCallback;
    }

    private static com.iwedia.dtv.io.IInputOutputCallback dtvServiceCallback = new com.iwedia.dtv.io.IInputOutputCallback() {
        @Override
        public void afdChanged(int deviceIndex, int afd) {
            // TODO Auto-generated method stub
        }

        @Override
        public void deviceDisconnected(int deviceIndex) {
            getInstance().logd("Device disconnected, index: " + deviceIndex);
            notifyInputDeviceDisconnected(deviceIndex);
        }

        @Override
        public void deviceFound(int deviceIndex) {
            // TODO Auto-generated method stub
        }

        @Override
        public void deviceHotPlug(int deviceIndex) {
            getInstance().logd("Device connected, index: " + deviceIndex);
            notifyInputDeviceConnected(deviceIndex);
        }

        @Override
        public void videoSignalChanged(int deviceIndex, boolean signalAvailable) {
            getInstance().logd(
                    "videoSignalChanged, index: " + deviceIndex + " status: "
                            + signalAvailable);
            notifyInputDeviceVideoSignalChanged(deviceIndex, signalAvailable);
        }

        @Override
        public void audioSignalChanged(int deviceIndex, boolean signalAvailable) {
            getInstance().logd(
                    "audioSignalChanged, index: " + deviceIndex + " status: "
                            + signalAvailable);
            notifyInputDeviceAudioSignalChanged(deviceIndex, signalAvailable);
        }

        @Override
        public void hdmiAuthStatusChange(int deviceIndex, boolean success) {
            // TODO Auto-generated method stub
        }

        @Override
        public void scanFinished() {
            // TODO Auto-generated method stub
        }

        @Override
        public void wssChanged(int deviceIndex, int wss) {
            // TODO Auto-generated method stub
        }
    };

    @Override
    public void registerCallback(IInputOutputCallback callback)
            throws RemoteException {
        if (isClbkRegistered == false) {
            isClbkRegistered = mInputDeviceCallback.register(callback);
        } else {
            logd("registerCallback: Callback is already registered!");
        }
    }

    @Override
    public void unregisterCallback(IInputOutputCallback callback)
            throws RemoteException {
        if (isClbkRegistered == true) {
            mInputDeviceCallback.unregister(callback);
            isClbkRegistered = false;
        } else {
            logd("unregisterCallback: Callback is not registered!");
        }
    }

    private void logd(String message) {
        if (InputSettings.DEBUG) {
            Log.i(LOG_TAG, message);
        }
    }
}
