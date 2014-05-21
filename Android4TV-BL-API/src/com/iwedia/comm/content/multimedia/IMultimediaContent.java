package com.iwedia.comm.content.multimedia;

import com.iwedia.dtv.types.TimeDate;

/**
 * This interface contains declaration of functions implemented in
 * MultimediaContent.
 *
 * @author Marko Zivanovic
 *
 *
 */
public interface IMultimediaContent {

    /**
     * Gets MultimediaContent extension.
     *
     * @return multimedia extension e.g. jpg, avi, mp3.
     */
    String getExtension();


    String getType();

    String getImageType();

    String getFileURL();

    String getAbsolutePath();

    String getDescription();

/*     String getExtendedDescription(); */

    String getStartTime();

    String getEndTime();

    String getDurationTime();

    int getGenre();

    TimeDate getTimeDate();

    String getId();

    String getDlnaName();

    String getMime();

    void setTitle(String title);

    String getTitle();

    void setArtist(String artist);

    String getArtist();

    void setResolution(String resolution);

    String getResolution();

    void setDuration(int duration);

    int getDuration();

    void setPlaylistID(int playlist_id);

    int getPlaylistID();

    boolean isIncomplete();
}
