package com.iwedia.dlna;

/**
 * DLNA event listener interface. Must be implemented, if user needs to be
 * notified about DLNA events.
 * 
 * @author maksovic
 */
public interface IDlnaEventListener {
    /**
     * Event handler.
     * 
     * @param event
     *        Fired event.
     */
    public void handleEvent(DlnaEvent event);
}
