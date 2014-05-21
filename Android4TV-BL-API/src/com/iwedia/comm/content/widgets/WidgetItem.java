package com.iwedia.comm.content.widgets;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is used to collect informations about Android system widget.
 *
 * @author Marko Zivanovic
 *
 */
public class WidgetItem implements Parcelable {

    /**
     * Widget index.
     */
    private int index;

    /**
     * Android widget name.
     */
    private String name;

    /**
     * Widget class name.
     */
    private String className;

    /**
     * Widget package name.
     */
    private String packageName;

    /**
     * Default constructor.
     */
    public WidgetItem() {
        className = "";
        packageName = "";
        index = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public static final Parcelable.Creator<WidgetItem> CREATOR = new Parcelable.Creator<WidgetItem>() {
        public WidgetItem createFromParcel(Parcel in) {
            return new WidgetItem(in);
        }

        public WidgetItem[] newArray(int size) {
            return new WidgetItem[size];
        }
    };

    private WidgetItem(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            dest.writeString(className);
            dest.writeString(packageName);
            dest.writeInt(index);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFromParcel(Parcel in) {
        try {
            className = in.readString();
            packageName = in.readString();
            index = in.readInt();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
