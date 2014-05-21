package com.iwedia.dlna;

/**
 * Controller requested playback (time) position from DMR.
 * 
 * @author maksovic
 */
public class DlnaRendererPositionEvent extends DlnaEvent {
    /**
     * Constructor for render position event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaRendererPositionEvent() {
        super(null);
    }
}
