package com.iwedia.service.system.about;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

/**
 * Class which handles the connection to the native 'stbmonitord' daemon. All
 * services which are supported by the daemon are exported with the methods of
 * this class.
 * 
 * @author maksovic
 */
public class STBMonitor {
    /**
     * Logger Tag.
     */
    private static final String TAG = "STBMonitor";
    /**
     * Daemon port.
     */
    private static final int STB_MONITOR_PORT = 44800;
    /**
     * Check FW upgrade message.
     */
    private static final String OTA_CHECK = "OTA_CHECK";
    /**
     * Execute actual upgrade (OTA).
     */
    private static final String OTA_DO_UPGRADE = "OTA_UPGRADE";
    /**
     * Execute actual upgrade (USB).
     */
    private static final String USB_DO_UPGRADE = "USB_UPGRADE";
    /**
     * Check upgrade version.(USB).
     */
    private static final String USB_CHECK_UPGRADE = "USB_CHECK_UPGRADE";
    /**
     * Upgrade available event.
     */
    private static final String OTA_UPGRADE_EVENT = "OTA_UPGRADE_EVENT";
    /**
     * Upgrade NOT available event.
     */
    private static final String OTA_NO_UPGRADE_EVENT = "OTA_NO_UPGRADE_EVENT";
    /**
     * Upgrade USB event.
     */
    private static final String USB_UPDATE_EVENT = "USB_UPDATE_MSG";
    /**
     * Daemon sent some error message.
     */
    private static final String OTA_ERROR_EVENT = "OTA_ERROR_EVENT";
    private static final String USB_CHECK_UPDATE_EVENT = "USB_CHECK_UPDATE_MSG";
    private static final String USB_FINISH_UPGRADE_EVENT = "USB_FINISH_UPGRADE";
    /**
     * Connection socet.
     */
    private Socket socket;
    /**
     * Socket writer.
     */
    private PrintWriter out;
    /**
     * Event listener.
     */
    private ISTBMonitorListener eventListener;
    /**
     * Task which handles communication with the daemon.
     */
    private Thread executor;

    /**
     * The constructor.
     * 
     * @param listener
     *        Listener implementor object. <b>MUST BE != null</b>
     * @throws IOException
     */
    public STBMonitor(ISTBMonitorListener listener) throws IOException {
        if (listener == null) {
            throw new IllegalArgumentException("ISTBMonitorListener missing!");
        }
        eventListener = listener;
        executor = new Thread(runnable);
        executor.start();
    }

    /**
     * Check whether there is available FW upgrade. This call is asynchronous
     * (in sense that there is no return value), and caller should expect
     * appropriate event (if there is one). It is synchronous in sense that it
     * WAITS for a connection to be established.
     * 
     * @param url
     *        URL to the
     */
    public void fwUpgradeCheck(String url) {
        synchronized (executor) {
            if (out == null) {
                try {
                    executor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.println(OTA_CHECK);
                out.println(url);
            }
        }
    }

    /**
     * Executes FW upgrade. Bear in mind that this method should <b>never</b>
     * return!
     */
    public void doFWUpgrade() {
        synchronized (executor) {
            out.println(OTA_DO_UPGRADE);
        }
    }

    /**
     * Check USB FW upgrade.
     */
    public void usbFWVersionCheck() {
        synchronized (executor) {
            if (out == null) {
                try {
                    executor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                out.println(USB_CHECK_UPGRADE);
            }
        }
    }

    /**
     * Executes USB FW upgrade. Bear in mind that this method should
     * <b>never</b> return!
     */
    public void copyUpgradeFWFromUSB() {
        synchronized (executor) {
            out.println(USB_DO_UPGRADE);
        }
    }

    public void finishUSBFWUpgrade() {
        synchronized (executor) {
            out.println(USB_FINISH_UPGRADE_EVENT);
        }
    }

    /**
     * Stops the connection to the daemon. This method should be called whenever
     * application does not need the connection any more (e.g. exiting the
     * application).
     */
    public void stopConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns FW version as a string in major.minor.revision format.
     * 
     * @return FW version, or empty string if there was an error.
     */
    public String getRunnungFWVersion() {
        BufferedReader br;
        String buildVersion;
        try {
            br = new BufferedReader(new InputStreamReader(Runtime.getRuntime()
                    .exec("getprop ro.build.version.incremental")
                    .getInputStream()));
            try {
                buildVersion = br.readLine();
            } finally {
                br.close();
            }
            /* remove unnecessary info about build variant and user */
            if (buildVersion != null) {
                buildVersion = buildVersion
                        .substring(buildVersion.indexOf('.') + 1);
                buildVersion = buildVersion
                        .substring(buildVersion.indexOf('.') + 1);
            }
            /*
             * TODO: check if build date and time contain enough info about
             * version
             */
            return buildVersion;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Task which handles the communication with stbmonitord.
     * 
     * @author maksovic
     */
    Runnable runnable = new Runnable() {
        BufferedReader in = null;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String line;
            try {
                synchronized (executor) {
                    // try to connect
                    socket = new Socket("127.0.0.1", STB_MONITOR_PORT);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    executor.notify();
                }
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                Log.d(TAG, "Waiting for message from server!");
                // in 'infinite loop' check for server messages and react
                while ((line = in.readLine()) != null) {
                    Log.d(TAG, "Message from server: " + line);
                    if (line.startsWith(OTA_UPGRADE_EVENT) == true) {
                        eventListener.handleEvent(
                                ISTBMonitorListener.FW_UPGRADE_EVENT,
                                line.substring(line.indexOf(':') + 1));
                    } else if (line.startsWith(OTA_ERROR_EVENT) == true) {
                        eventListener.handleEvent(
                                ISTBMonitorListener.ERROR_EVENT,
                                line.substring(line.indexOf(':') + 1));
                    }
                    if (line.startsWith(OTA_NO_UPGRADE_EVENT) == true) {
                        eventListener.handleEvent(
                                ISTBMonitorListener.NO_FW_UPGRADE_EVENT, null);
                    }
                    if (line.startsWith(USB_UPDATE_EVENT) == true) {
                        eventListener.handleEvent(
                                ISTBMonitorListener.USB_FW_UPGRADE_EVENT,
                                line.substring(line.indexOf(':') + 1));
                    }
                    if (line.startsWith(USB_CHECK_UPDATE_EVENT) == true) {
                        eventListener.handleEvent(
                                ISTBMonitorListener.USB_CHECK_UPGRADE,
                                line.substring(line.indexOf(':') + 1));
                    }
                }
                Log.d(TAG, "Connection closed!");
                eventListener.handleEvent(
                        ISTBMonitorListener.CONNECTION_LOST_EVENT, null);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    synchronized (executor) {
                        if (out != null) {
                            out.close();
                        }
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
// thread.start();
