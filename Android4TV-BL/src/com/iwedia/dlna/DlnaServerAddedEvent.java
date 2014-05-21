package com.iwedia.dlna;

/**
 * New DMS detected event.
 * 
 * @author maksovic
 */
public class DlnaServerAddedEvent extends DlnaEvent {
    /**
     * Constructor for server added event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaServerAddedEvent(String udn) {
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
