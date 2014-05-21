package com.iwedia.dlna;

/**
 * DLNA video item descriptor.
 * 
 * @author maksovic
 */
public class DlnaVideoItem extends DlnaItem {
    /**
     * Horizontal resolution.
     */
    private int width;
    /**
     * Vertical resolution.
     */
    private int height;
    /**
     * Duration in seconds.
     */
    private int duration;

    /**
     * Default constructor.
     * 
     * @param id
     *        Item ID.
     * @param friendlyName
     *        Item friendly name.
     */
    public DlnaVideoItem(String id, String friendlyName, String parentID) {
        super(id, friendlyName, parentID);
        this.width = 0;
        this.height = 0;
        this.duration = 0;
    }

    /**
     * Set video resolution.
     * 
     * @param width
     *        Horizontal size.
     * @param height
     *        Vertical size.
     */
    void setResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets width.
     * 
     * @return Horizontal size.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets vertical resolution.
     * 
     * @return Vertical size.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets item playback duration <b>in seconds!</b>
     * 
     * @return Duration.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets playback duration. Duration <b>MUST</b> be in seconds.
     * 
     * @param duration
     *        Duration to set.
     */
    void setDuration(int duration) {
        this.duration = duration;
    }
}
