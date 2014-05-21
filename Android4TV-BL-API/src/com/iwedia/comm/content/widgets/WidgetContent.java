package com.iwedia.comm.content.widgets;

import android.os.Parcel;
import android.os.Parcelable;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;

/**
 * WidgetContent inherits all the fields and methods of Content class and use
 * them to represent an Android widget. This class is used in
 * ContentFilterWidgets. {@link com.iwedia.service.content.ContentFilterWidgets}
 * , {@link com.iwedia.comm.content.Content}.
 *
 * @author Marko Zivanovic
 *
 */
public class WidgetContent extends Content implements Parcelable {

    /**
     * Default constructor.
     *
     * @param index
     *            - index of WidgetContent.
     * @param item
     *            - WidgetItem
     *            {@link com.iwedia.comm.content.widgets.WidgetItem}
     */
    public WidgetContent(int index, WidgetItem item) {

        this.index = index;
        this.name = item.getName() + ":" + item.getClassName();
        this.filterType = FilterType.WIDGETS;
        this.image = item.getPackageName();
    }

    public static final Parcelable.Creator<WidgetContent> CREATOR = new Parcelable.Creator<WidgetContent>() {
        public WidgetContent createFromParcel(Parcel in) {
            return new WidgetContent(in);

        }

        public WidgetContent[] newArray(int size) {
            return new WidgetContent[size];
        }
    };

    public WidgetContent(Parcel in) {
        super(in);
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
