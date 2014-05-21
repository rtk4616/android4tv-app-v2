package com.iwedia.dlna;

/**
 * Controller requested DMR to play some media..
 * 
 * @author maksovic
 */
public class DlnaRendererPlayEvent extends DlnaEvent {
    /**
     * Constructor for render play event.
     * 
     * @param udn
     *        Device UDN.
     */
    // public DlnaRendererPlayEvent(DlnaAudioItem item){
    // super(item);
    // }
    /**
     * Constructor for render play event.
     * 
     * @param udn
     *        Device UDN.
     */
    // public DlnaRendererPlayEvent(DlnaVideoItem item){
    // super(item);
    // }
    /**
     * Constructor for render play event.
     * 
     * @param udn
     *        Device UDN.
     */
    // public DlnaRendererPlayEvent(DlnaPictureItem item){
    // super(item);
    // }
    /**
     * Constructor for render play event.
     * 
     * @param udn
     *        Device UDN.
     */
    public DlnaRendererPlayEvent(DlnaItem item) {
        super(item);
    }

    /**
     * Retrieves item which should be played.
     * 
     * @return item to play.
     */
    public DlnaItem getPlaybackItem() {
        return (DlnaItem) value;
    }
}
