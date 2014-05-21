package com.iwedia.dlna;

/**
 * DLNA picture item descriptor.
 * 
 * @author maksovic
 */
public class DlnaPictureItem extends DlnaItem {
    /**
     * Horizontal resolution.
     */
    private int width;
    /**
     * Vertical resolution.
     */
    private int height;
    /**
     * Thumbnail URI.
     */
    private String thumbnailURI;

    /**
     * Default constructor.
     * 
     * @param id
     *        Item ID.
     * @param friendlyName
     *        Item friendly name.
     */
    public DlnaPictureItem(String id, String friendlyName, String parentID) {
        super(id, friendlyName, parentID);
        this.width = 0;
        this.height = 0;
        thumbnailURI = "";
    }

    /**
     * Set image resolution.
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
     * Returns thumbnail URI.
     * 
     * @return Thumbnail URI.
     */
    public String getThumbnailURI() {
        return thumbnailURI;
    }

    /**
     * Sets image thumbnail.
     * 
     * @param thumbnailURI
     *        picture thumbnail to set.
     */
    void setThumbnailURI(String thumbnailURI) {
        this.thumbnailURI = thumbnailURI;
    }
}
