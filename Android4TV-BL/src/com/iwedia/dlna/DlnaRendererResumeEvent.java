package com.iwedia.dlna;

/**
 * Controller sent RESUME command to the DMR.
 * 
 * @author maksovic
 */
public class DlnaRendererResumeEvent extends DlnaEvent {
    /**
     * Constructor for render resume event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaRendererResumeEvent() {
        super(null);
    }
}
