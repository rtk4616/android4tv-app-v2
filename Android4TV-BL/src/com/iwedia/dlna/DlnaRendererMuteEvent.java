package com.iwedia.dlna;

/**
 * Set mute value in DMR
 * 
 * @author radakovic
 */
public class DlnaRendererMuteEvent extends DlnaEvent {
    protected int mute;

    /**
     * Constructor for render seek to event.
     * 
     * @param position
     *        requested time position of playback.
     */
    public DlnaRendererMuteEvent(int mute) {
        super(null);
        this.mute = mute;
    }

    /**
     * Returns mute value.
     * 
     * @return mute
     */
    public int getMute() {
        return mute;
    }
}
