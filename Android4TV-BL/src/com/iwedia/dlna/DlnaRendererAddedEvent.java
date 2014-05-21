package com.iwedia.dlna;

/**
 * New DMR detected event.
 * 
 * @author maksovic
 */
public class DlnaRendererAddedEvent extends DlnaEvent {
    /**
     * Constructor for render added event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaRendererAddedEvent(String udn) {
        super(udn);
    }

    /**
     * Returns added renderer device UDN.
     * 
     * @return
     */
    public String getRendererUDN() {
        return (String) value;
    }
}
