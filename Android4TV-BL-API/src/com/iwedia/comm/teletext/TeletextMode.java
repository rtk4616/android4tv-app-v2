package com.iwedia.comm.teletext;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Type for the given component:
 * {@link com.iwedia.dtv.teletext.TeletextMode#FULL}
 * {@link com.iwedia.dtv.teletext.TeletextMode#HALF}
 * {@link com.iwedia.dtv.teletext.TeletextMode#MIX}
 * {@link com.iwedia.dtv.teletext.TeletextMode#INVALID}
 * {@link com.iwedia.dtv.teletext.TeletextMode#OFF}
 */
public enum TeletextMode implements Parcelable {
    FULL(0), HALF(1), MIX(2), INVALID(3), OFF(4);
    
    private int mValue;

    TeletextMode(int value) {
        this.mValue = value;
    }

    /** Get int value of the component */
    public int getValue() {
        return mValue;
    }
    
    /** Get enum from value */
    public static TeletextMode getFromValue(int value) {
        try {
            return values()[value];
        }catch (ArrayIndexOutOfBoundsException e) {            
            return OFF;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<TeletextMode> CREATOR = new Creator<TeletextMode>() {
        @Override
        public TeletextMode createFromParcel(final Parcel source) {
            return getFromValue(source.readInt());
        }

        @Override
        public TeletextMode[] newArray(final int size) {
            return new TeletextMode[size];
        }
    };
}