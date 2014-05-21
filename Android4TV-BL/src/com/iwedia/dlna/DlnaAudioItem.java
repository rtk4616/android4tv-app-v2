package com.iwedia.dlna;

/**
 * DLNA Audio item description.
 * 
 * @author maksovic
 */
public class DlnaAudioItem extends DlnaItem {
    /**
     * Bit rate.
     */
    private int bitRate;
    /**
     * Sampling rate.
     */
    private int samplingRate;
    /**
     * Number of audio channels.
     */
    private int numChannels;
    /**
     * Album art URI.
     */
    private String albumArtUri;
    /**
     * Duration in seconds.
     */
    private int duration;

    /**
     * Constructor.
     * 
     * @param id
     *        item ID.
     * @param friendlyName
     *        item friendly name.
     */
    public DlnaAudioItem(String id, String friendlyName, String parentID) {
        super(id, friendlyName, parentID);
        this.bitRate = 0;
        this.samplingRate = 0;
        this.numChannels = 0;
        this.duration = 0;
        this.albumArtUri = "";
    }

    /**
     * Gets bit rate.
     * 
     * @return bit rate.
     */
    public int getBitRate() {
        return bitRate;
    }

    /**
     * Sets bit rate.
     * 
     * @param bitRate
     *        bit rate to set.
     */
    void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    /**
     * Gets sampling rate.
     * 
     * @return sampling rate.
     */
    public int getSamplingRate() {
        return samplingRate;
    }

    /**
     * Sets sampling rate.
     * 
     * @param samplingRate
     *        sampling rate to set.
     */
    void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    /**
     * Gets number of channels in the audio stream.
     * 
     * @return number of channels.
     */
    public int getNumChannels() {
        return numChannels;
    }

    /**
     * Sets number of channels.
     * 
     * @param numChannels
     *        number of channels to set.
     */
    void setNumChannels(int numChannels) {
        this.numChannels = numChannels;
    }

    /**
     * Returns album art URI (if there is one)
     * 
     * @return
     */
    public String getAlbumArtURI() {
        return albumArtUri;
    }

    /**
     * Sets album art URI.
     * 
     * @param albumArtUri
     */
    void setAlbumArtURI(String albumArtUri) {
        this.albumArtUri = albumArtUri;
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
