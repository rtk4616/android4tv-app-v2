package com.iwedia.service.system.about;

/**
 * Listener interface. It <b>MUST</b> be implemented by the class which want so
 * receive events from STBMonitor
 * 
 * @author maksovic
 */
public interface ISTBMonitorListener {
    /**
     * FW Upgrade available event.
     */
    public static final int FW_UPGRADE_EVENT = 1;
    /**
     * Error event (some action failed). It is accompanied y the error message.
     */
    public static final int ERROR_EVENT = 2;
    /**
     * Connection to the STBMonitor daemon lost. Most probably daemon died!!!
     */
    public static final int CONNECTION_LOST_EVENT = 3;
    /**
     * FW Upgrade NOT available event.
     */
    public static final int NO_FW_UPGRADE_EVENT = 4;
    /**
     * USB Upgrade available event.
     */
    public static final int USB_FW_UPGRADE_EVENT = 5;
    public static final int USB_CHECK_UPGRADE = 6;

    /**
     * Event handling method.
     * 
     * @param code
     *        One of the event codes.
     * @param value
     *        Event message (can be null).
     */
    public void handleEvent(int code, String value);
}
