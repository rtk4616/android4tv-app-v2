package com.iwedia.comm.content.ipcontent;

import android.os.Parcel; 
import android.os.Parcelable;
import android.os.RemoteException;

import com.iwedia.comm.IServiceControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.comm.images.ImageManager;
import com.iwedia.dtv.service.ServiceDescriptor;

/**
 * IpContent inherits all the fields and methods of Content class and use them
 * to represent IP service. This class is used in ContentFilterIP @lin
 * {@link com.iwedia.service.content.ContentFilterIP},
 * {@link com.iwedia.comm.content.Content}
 *
 * @author Marko Zivanovic
 *
 */
public class IpContent extends Content implements Parcelable {

    /**
     * IP Service url;
     */
    private String url;
    /**
     * ServiceList control interface {@link com.iwedia.comm.IServiceListControl}
     *
     */
    private IServiceControl serviceListControl;

    /**
     * Default constructor.
     */
    public IpContent() {
        this.name = "";
        this.url = "";
        this.filterType = FilterType.IP_STREAM;
        this.image = "";
        this.index = -1;
    }

    /**
     * Constructor used in ContentFilterIp to create IpContent from URL.
     * {@link com.iwedia.service.content.ContentFilterIP}.
     *
     * @param name
     *            name of IpContent.
     * @param url
     *            stream url.
     * @param index
     *            index of IpContent.
     */
    public IpContent(String name, String url, int index) {
        this.name = name;
        this.filterType = FilterType.IP_STREAM;
        this.image = ImageManager.getInstance().getImageUrl(name);
        this.index = index;
        this.indexInMasterList = index;
        this.url = url;
        this.serviceListIndex = ServiceListIndex.IP_STREAMED;
        this.selectable = true;
    }

    /**
     * Constructor for MW IP service.
     *
     * @param index
     *            - index of IP service in MW IP service list.
     * @param serviceListControl
     *            - ServiceList control interface
     *            {@link com.iwedia.comm.IServiceListControl}
     * @param serviceType
     *            - index of MW IP service list.
     */
    public IpContent(int index, IServiceControl serviceListControl,
                     int serviceType) {
        this.serviceListControl = serviceListControl;
        this.index = index;
        ServiceDescriptor serviceDesc = null;

        try {
            serviceDesc = this.serviceListControl.getServiceDescriptor(serviceType, index);
        } catch(RemoteException e) {
            e.printStackTrace();
        }

        if(serviceDesc != null) {
            this.indexInMasterList = serviceDesc.getMasterIndex();
            this.name = serviceDesc.getName();
        }
        this.filterType = FilterType.IP_STREAM;
        if(serviceDesc != null) {
            this.image = ImageManager.getInstance().getImageUrl(serviceDesc.getName());
        }
        this.serviceListIndex = serviceType;
        this.url = "";
        this.selectable = true;
    }

    public IpContent(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<IpContent> CREATOR = new Parcelable.Creator<IpContent>() {
        public IpContent createFromParcel(Parcel in) {
            return new IpContent(in);
        }

        public IpContent[] newArray(int size) {
            return new IpContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        try {
            super.writeToParcel(dest, flags);
            dest.writeString(url);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readFromParcel(Parcel in) {
        try {
            super.readFromParcel(in);
            this.url = in.readString();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return super.toString() + " IpContent url:" + url;
    }

}
