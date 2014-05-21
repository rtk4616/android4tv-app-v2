package com.iwedia.comm.system.application;

import android.os.Parcel;
import android.os.Parcelable;

public class AppPermission implements Parcelable {

    private String permissionGroup;
    private String description;

    public AppPermission() {
        // TODO Auto-generated constructor stub
        permissionGroup = "";
        description = "";
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static final Parcelable.Creator<AppPermission> CREATOR = new Parcelable.Creator<AppPermission>() {
        public AppPermission createFromParcel(Parcel in) {
            return new AppPermission(in);
        }

        public AppPermission[] newArray(int size) {
            return new AppPermission[size];
        }
    };

    private AppPermission(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {

            dest.writeString(permissionGroup);
            dest.writeString(description);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFromParcel(Parcel in) {
        try {
            permissionGroup = in.readString();
            description = in.readString();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
