package com.iwedia.dlna;

/**
 * Controller sent PAUSE action to the DMR..
 * 
 * @author maksovic
 */
public class DlnaRendererPauseEvent extends DlnaEvent {
    /**
     * Constructor for render pause event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaRendererPauseEvent() {
        super(null);
    }
}
