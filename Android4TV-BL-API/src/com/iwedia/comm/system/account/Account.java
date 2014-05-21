package com.iwedia.comm.system.account;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is used to collect informations about android accounts.
 *
 * @author Stanislava Markovic
 *
 */
public class Account implements Parcelable {

    /**
     * Name of Android application.
     */
    private String label;
    /**
     * Account type.
     */
    private String type;

    /**
     * On of the following: {@link com.iwedia.comm.enums.AppListType}.
     */
    private byte[] image;

    public Account() {
        this.label = "";
        this.type = "";
    }

    /**
     * Returns account label.
     *
     * @return name of Account label.
     */
    public String getAccountLabel() {
        return label;
    }

    /**
     * Sets Account label.
     *
     * @param label
     */
    public void setAccountLabel(String label) {
        this.label = label;
    }

    /**
     * Gets account type.
     *
     * @return account type.
     */
    public String getAccountType() {
        return type;
    }

    /**
     * Sets account type
     *
     * @param account
     *            type - Android application package.
     */
    public void setAccountType(String accountType) {
        this.type = accountType;
    }

    /**
     * Gets account byte array image.
     *
     * @return account byte array.
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Sets account byte array image.
     *
     * @param byte array
     */
    public void setImage(byte[] image) {
        this.image = image;
    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    private Account(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {

            dest.writeString(label);
            dest.writeString(type);
            dest.writeInt(image.length);
            dest.writeByteArray(image);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void readFromParcel(Parcel in) {
        try {
            label = in.readString();
            type = in.readString();
            image = new byte[in.readInt()];
            in.readByteArray(image);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
