package com.iwedia.comm.system.date_time;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeZone implements Parcelable {

    private String displayName;
    private String gmt;
    private String timeZoneName;
    private String id;

    public TimeZone(String displayName, String gmt, String timeZoneName,
                    String id) {
        this.displayName = displayName;
        this.gmt = gmt;
        this.timeZoneName = timeZoneName;
        this.id = id;

    }

    public String getDisplayName() {
        return displayName;
    }

    public String getGmt() {
        return gmt;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public String getId() {
        return id;
    }

    public static final Parcelable.Creator<TimeZone> CREATOR = new Parcelable.Creator<TimeZone>() {
        public TimeZone createFromParcel(Parcel in) {
            return new TimeZone(in);
        }

        public TimeZone[] newArray(int size) {
            return new TimeZone[size];
        }
    };

    private TimeZone(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(displayName);
        dest.writeString(gmt);
        dest.writeString(timeZoneName);
        dest.writeString(id);

    }

    public void readFromParcel(Parcel in) {
        displayName = in.readString();
        gmt = in.readString();
        timeZoneName = in.readString();
        id = in.readString();
    }

}