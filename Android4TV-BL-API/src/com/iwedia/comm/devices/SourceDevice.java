/**
 *   SourceDevice.java
 *
 * @author Marko Krnjetin@rt-rk.com
 * @version
 * @date 11 Oct 2012
 */

package com.iwedia.comm.devices;

import android.os.Parcel;
import android.os.Parcelable;
import android.net.Uri;


public  class SourceDevice implements Parcelable {

    public static final String DVB  = "DVB";
    public static final String HDMI = "HDMI";
    public static final String CVBS = "CVBS";
    public static final String COMP = "COMPONENT";
    public static final String VGA  = "VGA";
    public static final String SCART = "SCART";
    public static final String ATV  = "ATV";

    /** SourceDevice type (HDMI, SCART etc).*/
    private String type;

    /** SourceDevice port number.*/
    private int portNumber;

    /** SourceDevice uri*/
    private String defaultUri;

    /** SourceDevice uri*/
    private String name;

    /** SourceDevice properties.*/
    //private IODeviceProps Properties;

    protected SourceDevice() {
        // TODO Auto-generated constructor stub
    }

    public SourceDevice(String type, int portNumber, String name) {
        this.type = type;
        this.portNumber = portNumber;
        this.name = name;
        calculateUri();
    }

    public String getType() {
        return type;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getDefaultUri() {
        return defaultUri;
    }

    public String getName() {
        return name;
    }

    /** Starts SourceDevice.*/
    public boolean start() {
        return true;
    }

    /** Stops SourceDevice.*/
    public boolean stop() {
        return false;
    }

    private void calculateUri() {
        /*  StringBuilder sb = new StringBuilder();

         if(type.equals(DVB))
         {
             sb.append(MediaUriUtils.DVB_SCHEME);
         }
         else if(type.equals(HDMI))
         {
             sb.append(MediaUriUtils.HDMI_SCHEME);
         }
         else if(type.equals(CVBS))
         {
             sb.append(MediaUriUtils.CVBS_SCHEME);
         }
         else if(type.equals(COMP))
         {
             sb.append(MediaUriUtils.COMPONENT_SCHEME);
         }
         else if(type.equals(VGA))
         {
             sb.append(MediaUriUtils.VGA_SCHEME);
         }
         else if(type.equals(ATV))
         {
             sb.append(MediaUriUtils.ATV_SCHEME);
         }

         sb.append("://localhost:");
         sb.append(portNumber);

         Uri generatedUri = MediaUriUtils.addGroupId(Uri.parse(sb.toString()), MediaUriUtils.DEFAULT_GROUP_ID);
         defaultUri = generatedUri.toString(); */
    }

    public static final Parcelable.Creator<SourceDevice> CREATOR = new Parcelable.Creator<SourceDevice>() {
        public SourceDevice createFromParcel(Parcel in) {

            return new SourceDevice(in) {
            };
        }

        public SourceDevice[] newArray(int size) {
            return new SourceDevice[size];
        }
    };

    protected SourceDevice(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeInt(portNumber);
        dest.writeString(defaultUri);
        dest.writeString(name);
    }

    public void readFromParcel(Parcel in) {
        type = in.readString();
        portNumber = in.readInt();
        defaultUri = in.readString();
        name = in.readString();

    }

}
