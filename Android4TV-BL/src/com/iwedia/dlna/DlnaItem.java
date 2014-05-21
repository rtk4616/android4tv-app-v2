package com.iwedia.dlna;

/**
 * DLNA item class. Item is general DLNA file/stream.
 * 
 * @author maksovic
 */
public class DlnaItem extends DlnaObject {
    /**
     * Parent directory.
     */
    protected DlnaContainer parent;
    /**
     * Item URI.
     */
    private String uri;
    /**
     * MIME type.
     */
    private String mime;
    /**
     * Streaming capabilities for this item.
     */
    private DlnaStreamingCapabilities streamingCap;
    /**
     * Constructor.
     * 
     * @param id
     *        Item ID.
     * @param friendlyName
     *        Item friendly name.
     */
    private int profile;

    public DlnaItem(String id, String friendlyName, String parentID) {
        super(id, friendlyName, parentID);
    }

    /**
     * Gets item URI.
     * 
     * @return item URI
     */
    public String getURI() {
        return uri;
    }

    /**
     * Sets item URI.
     * 
     * @param uri
     *        item URI.
     */
    void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Gets item parent folder.
     * 
     * @return item parent folder.
     */
    public DlnaContainer getParent() {
        return parent;
    }

    /**
     * Sets item folder.
     * 
     * @param parent
     *        item parent folder.
     */
    void setParent(DlnaContainer parent) {
        this.parent = parent;
    }

    /**
     * Gets item MIME type.
     * 
     * @return MIME type.
     */
    public String getMime() {
        return mime;
    }

    /**
     * Sets item MIME type.
     * 
     * @param mime
     *        MIME type.
     */
    void setMime(String mime) {
        this.mime = mime;
    }

    /**
     * Gets streaming capabilities.
     * 
     * @return Streaming capabilities.
     */
    public DlnaStreamingCapabilities getStreamingCap() {
        return streamingCap;
    }

    /**
     * Sets streaming capabilities.
     * 
     * @param streamingCap
     *        Streaming capabilities.
     */
    public void setStreamingCap(DlnaStreamingCapabilities streamingCap) {
        this.streamingCap = streamingCap;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getProfile() {
        return profile;
    }
}
