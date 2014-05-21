package com.iwedia.dlna;

/**
 * DMS removed (no more available on the network) event.
 * 
 * @author maksovic
 */
public class DlnaServerRemovedEvent extends DlnaEvent {
    /**
     * Constructor for the server removed event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaServerRemovedEvent(String udn) {
        super(udn);
    }

    /**
     * Returns added server device descriptor.
     * 
     * @return
     */
    public String getServerUDN() {
        return (String) value;
    }
}
