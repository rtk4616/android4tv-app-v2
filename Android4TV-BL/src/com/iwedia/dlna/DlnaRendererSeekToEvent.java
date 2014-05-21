package com.iwedia.dlna;

/**
 * Controller requested DMR to play some media..
 * 
 * @author radakovic
 */
public class DlnaRendererSeekToEvent extends DlnaEvent {
    /**
     * Constructor for render seek to event.
     * 
     * @param position
     *        requested time position of playback.
     */
    public DlnaRendererSeekToEvent(String position) {
        super(position);
    }

    /**
     * Returns requested playback position.
     * 
     * @return
     */
    public String getSeekToPosition() {
        return (String) value;
    }
}
