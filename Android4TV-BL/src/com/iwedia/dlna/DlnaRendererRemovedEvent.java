package com.iwedia.dlna;

/**
 * DMR is no more available on the network event.
 * 
 * @author maksovic
 */
public class DlnaRendererRemovedEvent extends DlnaEvent {
    /**
     * Constructor for render removed event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaRendererRemovedEvent(String udn) {
        super(udn);
    }

    /**
     * Returns removed renderer device UDN.
     * 
     * @return
     */
    public String getRendererUDN() {
        return (String) value;
    }
}
