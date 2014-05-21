package com.iwedia.dlna;

/**
 * General DLNA event.
 * 
 * @author maksovic
 */
public abstract class DlnaEvent {
    /**
     * Event value.
     */
    protected Object value;

    /**
     * Constructs event from passed value.
     * 
     * @param value
     *        Event value.
     */
    protected DlnaEvent(Object value) {
        this.value = value;
    }
}
