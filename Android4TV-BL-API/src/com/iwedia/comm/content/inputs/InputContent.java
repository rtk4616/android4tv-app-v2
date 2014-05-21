package com.iwedia.comm.content.inputs;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.iwedia.comm.ISystemControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.devices.SourceDevice;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.images.ImageManager;

public class InputContent extends Content {

    private ISystemControl systemControl;

    /**
     * Default constructor
     *
     * @param index
     *            - index of input.
     * @param name
     *            Name of the input to be displayed
     */
    public InputContent(int index, String name) {
        this.name = name;
        this.index = index;
        this.filterType = FilterType.INPUTS;
        this.image = ImageManager.getInstance().getImageUrl(name);
    }

    public static final Parcelable.Creator<InputContent> CREATOR = new Parcelable.Creator<InputContent>() {
        public InputContent createFromParcel(Parcel in) {
            return new InputContent(in);

        }

        public InputContent[] newArray(int size) {
            return new InputContent[size];
        }
    };

    public InputContent(Parcel in) {
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
