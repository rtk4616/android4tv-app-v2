package com.iwedia.dlna;

/**
 * Set volume value in DMR
 * 
 * @author radakovic
 */
public class DlnaRendererVolumeEvent extends DlnaEvent {
    protected int volume;

    /**
     * Constructor for render volume event.
     * 
     * @param volume
     *        requested volume value.
     */
    public DlnaRendererVolumeEvent(int volume) {
        super(null);
        this.volume = volume;
    }

    /**
     * Returns volume value.
     * 
     * @return volume
     */
    public int getVolume() {
        return volume;
    }
}
