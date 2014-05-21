package com.iwedia.comm.content.multimedia;

import android.os.Parcel;
import android.os.Parcelable;

public class PlaylistFile implements Parcelable {

    private String name;
    private String type;

    public static final Parcelable.Creator<PlaylistFile> CREATOR = new Parcelable.Creator<PlaylistFile>() {
        public PlaylistFile createFromParcel(Parcel in) {
            return new PlaylistFile(in);
        }

        public PlaylistFile[] newArray(int size) {
            return new PlaylistFile[size];
        }
    };

    public PlaylistFile(Parcel in) {
        readFromParcel(in);
    }

    public PlaylistFile(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        name = in.readString();
        type = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);

    }


}
