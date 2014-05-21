package com.iwedia.comm.content.multimedia;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.pvr.SmartInfo;
import com.iwedia.dtv.types.TimeDate;

public class MultimediaContent extends Content implements IMultimediaContent,
    Parcelable {

    private String fileExtension;
    private String fileType;
    private String multimediaType;
    private String fileURL;
    private String absolutePath;

    private String description;
    private String extDescription;
    private String startTime;
    private String endTime;
    private String durationTime;
    private int genre;

    private int playlist_id;
    private String artist;
    private String title;
    private int duration;
    private String resolution;
    private TimeDate timeDate;
    private String playlistName;
    private String playlistType;

    private String id;
    private String dlnaName;
    private String mime;
    private String rootID;
    private int isFavorite;
    private boolean incomplete = false;

    public static final Parcelable.Creator<MultimediaContent> CREATOR = new Parcelable.Creator<MultimediaContent>() {
        public MultimediaContent createFromParcel(Parcel in) {
            return new MultimediaContent(in);
        }

        public MultimediaContent[] newArray(int size) {
            return new MultimediaContent[size];
        }
    };

    public MultimediaContent(Parcel in) {
        super(in);
    }

    // PVR record
    public MultimediaContent(String title, String description, String duration,
                             String multimediaType, TimeDate timeDate, int index, String fileType,
                             String extension) {

        this.index = index;
        this.name = title;
        this.image = "";
        this.filterType = FilterType.PVR_RECORDED;
        this.durationTime = duration;
        this.timeDate = timeDate;
        this.description = description;
        this.multimediaType = multimediaType;
        this.fileType = fileType;
        this.fileExtension = extension;
        this.incomplete = false;

    }

    // multimedia
    public MultimediaContent(String name, String multimediaFileUrl,
                             String multimediaFileExt, String multimediaFileType,
                             String multimediaType, int index, String path, String id,
                             String dlnaName, String rootID, int isFavorite) {

        this.index = index;
        this.name = name;
        this.image = "";
        this.filterType = FilterType.MULTIMEDIA;
        fileExtension = multimediaFileExt;
        fileType = multimediaFileType;
        this.multimediaType = multimediaType;
        fileURL = multimediaFileUrl;
        absolutePath = path;
        this.id = id;
        Log.e("MULTIMEDIA CONTENT:", "ID" + id);
        this.dlnaName = dlnaName;
        this.rootID = rootID;
        this.isFavorite = isFavorite;
        this.incomplete = false;
        this.playlist_id = 0;
    }

    // multimedia
    public MultimediaContent(String name, String multimediaFileUrl,
                             String multimediaFileExt, String multimediaFileType,
                             String multimediaType, int index, String path, String id,
                             String dlnaName, String rootID, int isFavorite, int playlistId, String artist, String title, int duration, String resolution,
                             String playlistName, String playlistType) {

        this.index = index;
        this.name = name;
        this.image = "";
        this.filterType = FilterType.MULTIMEDIA;
        fileExtension = multimediaFileExt;
        fileType = multimediaFileType;
        this.multimediaType = multimediaType;
        fileURL = multimediaFileUrl;
        absolutePath = path;
        this.id = id;
        Log.e("MULTIMEDIA CONTENT:", "ID" + id);
        this.dlnaName = dlnaName;
        this.rootID = rootID;
        this.isFavorite = isFavorite;
        this.playlist_id = playlistId;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
        this.resolution = resolution;
        this.playlistName = playlistName;
        this.playlistType = playlistType;

        this.incomplete = false;
    }

    // playlist
    public MultimediaContent(int id, String title, String artist, String source,
                             String resolution, int duration, String extension) {

        playlist_id = id;
        this.title = title;
        this.artist = artist;
        this.filterType = FilterType.MULTIMEDIA;
        fileURL = source;
        this.duration = duration;
        this.resolution = resolution;
        fileExtension = extension;
        this.incomplete = false;
    }

    // playlist
    public MultimediaContent(int id, String playlistName, String multimediaFileType, String path, String title,
                             String artist, String source, String resolution, int duration, String multimediaFileExt, String multimediaType,
                             String name, String playlistType) {

        playlist_id = id;
        this.playlistName = playlistName;
        this.name = name;
        fileType = multimediaFileType;
        this.multimediaType = multimediaType;
        absolutePath = path;
        this.title = title;
        this.artist = artist;
        this.filterType = FilterType.MULTIMEDIA;
        fileURL = source;
        this.duration = duration;
        this.resolution = resolution;
        fileExtension = multimediaFileExt;
        this.playlistType = playlistType;
        this.incomplete = false;
    }

    // PVR schedule
    public MultimediaContent(SmartInfo smartInfo, int index) {
        this.index = index;
        this.image = "";
        this.filterType = FilterType.PVR_SCHEDULED;

        this.multimediaType = "DEFAULT";
        this.fileType = "file";
        this.fileExtension = "pvrschedule";

        if(smartInfo != null) {
            this.name = smartInfo.getTitle();
            this.description = smartInfo.getDescription();
            this.startTime = smartInfo.getStartTime().toString();
            this.endTime = smartInfo.getEndTime().toString();
            this.incomplete = false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void readFromParcel(Parcel in) {

        super.readFromParcel(in);
        fileExtension = in.readString();
        fileType = in.readString();
        multimediaType = in.readString();
        fileURL = in.readString();
        absolutePath = in.readString();
        description = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        durationTime = in.readString();
        genre = in.readInt();
		int readTimeDate = in.readInt();
		if(readTimeDate == 1){
			timeDate = new TimeDate().readFromParcel(in, 1);
		}
        id = in.readString();
        dlnaName = in.readString();
        mime = in.readString();
        rootID = in.readString();
        isFavorite = in.readInt();
        incomplete = in.readInt() == 1;
        artist = in.readString();
        title = in.readString();
        resolution = in.readString();
        duration = in.readInt();
        playlist_id = in.readInt();
        playlistName = in.readString();
        playlistType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(fileExtension);
        dest.writeString(fileType);
        dest.writeString(multimediaType);
        dest.writeString(fileURL);
        dest.writeString(absolutePath);
        dest.writeString(description);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(durationTime);
        dest.writeInt(genre);
		if(timeDate == null){
		    dest.writeInt(0);
		}else{
			dest.writeInt(1);
			timeDate.writeToParcel(dest, 1);
		}
        dest.writeString(id);
        dest.writeString(dlnaName);
        dest.writeString(mime);
        dest.writeString(rootID);
        dest.writeInt(isFavorite);
        dest.writeInt((int) (incomplete ? 1 : 0));
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(resolution);
        dest.writeInt(duration);
        dest.writeInt(playlist_id);
        dest.writeString(playlistName);
        dest.writeString(playlistType);
    }

    @Override
    public String getExtension() {
        return fileExtension;
    }

    @Override
    public String getType() {
        return fileType;
    }

    @Override
    public String getImageType() {
        return multimediaType;
    }

    @Override
    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getStartTime() {
        return startTime;
    }

    @Override
    public String getEndTime() {
        return endTime;
    }

    @Override
    public String getDurationTime() {
        return durationTime;
    }

    @Override
    public int getGenre() {
        return genre;
    }

    @Override
    public TimeDate getTimeDate() {
        return timeDate;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getDlnaName() {
        return this.dlnaName;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getMime() {
        return mime;
    }

    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getRootID() {
        return rootID;
    }

    public int isFavorite() {
        return isFavorite;
    }

    public void setFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getResolution() {
        return resolution;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setPlaylistID(int playlist_id) {
        this.playlist_id = playlist_id;
    }

    public int getPlaylistID() {
        return playlist_id;
    }
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }

}
