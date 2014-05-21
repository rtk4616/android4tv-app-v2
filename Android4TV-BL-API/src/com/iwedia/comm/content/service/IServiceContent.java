package com.iwedia.comm.content.service;

import com.iwedia.dtv.epg.EpgEvent;

public interface IServiceContent {

    /**
     * returns true if service is scrambled, otherwise false;
     */
    boolean isScrambled();

    /**
     * Gets ServiceContent image url.
     */
    String getImageUrl();

    /**
     * Returns index of service in the MW master list.
     *
     * @return index in master list.
     */
    int getIndexInMasterList();

}
