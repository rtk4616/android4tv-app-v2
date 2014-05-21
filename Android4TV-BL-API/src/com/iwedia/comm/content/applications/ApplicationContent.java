package com.iwedia.comm.content.applications;

import android.os.Parcel;
import android.os.Parcelable;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;

/**
 * ApplicationContent inherits all the fields and methods of Content class and
 * use them to represent an Android application. This class is used
 * in ContentFilterApps. {@link com.iwedia.service.content.ContentFilterApps},
 * {@link com.iwedia.comm.content.Content}.
 *
 * @author Marko Zivanovic
 *
 */
public class ApplicationContent extends Content implements Parcelable {

    /**
     * Default constructor.
     *
     * @param index
     *            - index of ApplicationContent.
     * @param item
     *            - AppItem {@link com.iwedia.comm.content.applications.AppItem}
     */
    public ApplicationContent(int index, AppItem item) {
        this.index = index;
        this.name = item.getAppname();
        this.filterType = FilterType.APPS;
        this.image = item.getAppPackage();
    }

    public static final Parcelable.Creator<ApplicationContent> CREATOR = new Parcelable.Creator<ApplicationContent>() {
        public ApplicationContent createFromParcel(Parcel in) {
            return new ApplicationContent(in);

        }

        public ApplicationContent[] newArray(int size) {
            return new ApplicationContent[size];
        }
    };

    public ApplicationContent(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}
