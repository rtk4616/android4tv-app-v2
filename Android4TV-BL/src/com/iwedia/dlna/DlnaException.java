package com.iwedia.dlna;

import android.util.Log;

/**
 * Exception related to the DLNA functionalities.
 * 
 * @author maksovic
 */
public class DlnaException extends Exception {
    /**
     * Just for warning.
     */
    private static final long serialVersionUID = 1L;

    public DlnaException(String reason) {
        Log.e("DlnaException", "Reason: " + reason);
    }
}
