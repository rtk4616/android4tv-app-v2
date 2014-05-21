package com.iwedia.dlna;

/**
 * Controller sent STOP command to the DMR.
 * 
 * @author maksovic
 */
public class DlnaRendererStopEvent extends DlnaEvent {
    /**
     * Constructor for render stop event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaRendererStopEvent() {
        super(null);
    }
}
