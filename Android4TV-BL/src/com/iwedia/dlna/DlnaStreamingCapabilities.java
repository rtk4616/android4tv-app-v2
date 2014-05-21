package com.iwedia.dlna;

/**
 * This class describes what are capabilities of the particular DLNA stream.
 * 
 * @author maksovic
 */
public class DlnaStreamingCapabilities {
    private boolean httpConnectionStall;
    private boolean httpRange;

    public DlnaStreamingCapabilities(boolean httpConnectionStall,
            boolean httpRange) {
        this.httpConnectionStall = httpConnectionStall;
        this.httpRange = httpRange;
    }

    /**
     * If this flag is true, stream can be paused with HTTP connection stalling
     * method.
     * 
     * @return Is connection stalling enabled.
     */
    public boolean canHttpConnectionStall() {
        return httpConnectionStall;
    }

    /**
     * Sets HTTP connection stalling flag.
     * 
     * @param httpConnectionStall
     *        HTTP connection stalling flag.
     */
    public void setHttpConnectionStall(boolean httpConnectionStall) {
        this.httpConnectionStall = httpConnectionStall;
    }

    /**
     * If this flag is true, network level byte seeking can be done with HTTP
     * Range request. All trick modes can be done via HTTP Range, but if this
     * flag is false, no Range requests should be sent to the network for the
     * particular stream.
     * 
     * @return HTTP Range flag.
     */
    public boolean canHttpRange() {
        return httpRange;
    }

    /**
     * Sets HTTP Range flag.
     * 
     * @param httpRange
     *        HTTP Range flag.
     */
    public void setHttpRange(boolean httpRange) {
        this.httpRange = httpRange;
    }
}
