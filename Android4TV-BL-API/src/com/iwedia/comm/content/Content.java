package com.iwedia.comm.content;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.iwedia.dtv.service.ServiceType;
import com.iwedia.dtv.service.SourceType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The abstract class that represents parent class of following child classes.
 * {@link com.iwedia.comm.applications.ApplicationContent},
 * {@link com.iwedia.comm.data.DataContent},
 * {@link com.iwedia.comm.inputs.InputContent},
 * {@link com.iwedia.comm.multimedia.MultimediaContent},
 * {@link com.iwedia.comm.radio.RadioContent},
 * {@link com.iwedia.comm.service.ServiceContent},
 * {@link com.iwedia.comm.widgets.WidgetContent},
 *
 * This class provide member variables and methods that are wholly shared by all
 * subclasses.
 *
 * @author Marko Zivanovic
 *
 */
public abstract class Content implements Parcelable {

    /**
     * This variable is used to create subclass when the object comes through
     * the binder.
     */
    public String className;

    /**
     * Index of Content item, e.g. channel index in MW in ServiceContent.
     */
    protected int index;

    /**
     * Content item name, e.g. name of Android application in AndroidContent.
     */
    protected String name;

    /**
     * Image of ContentItem, e.g. image of radio service in RadioContent.
     */
    protected String image;

    /**
     * FilterType - {@link com.iwedia.comm.enums.FilterType}.
     */
    protected int filterType;

    /**
     * MW service list index - {@link com.iwedia.comm.enums.ServiceListIndex}.
     */
    protected int serviceListIndex;

    /**
     * Index of service in the MW master list.
     */
    protected int indexInMasterList;

    protected int serviceLCN;

    protected boolean hidden;

    protected boolean selectable;
    
    protected SourceType sourceType = SourceType.UNDEFINED;
    
    protected ServiceType serviceType = ServiceType.UNDEFINED;
    
    public Content() {
        this.className = getClass().getName();
    }

    public Content(int index, String name, String image, int contentType,
                   int serviceListIndex, boolean hidden, boolean selectable, SourceType sourceType, ServiceType serviceType) {
        this.className = getClass().getName();
        this.index = index;
        this.name = name;
        this.image = image;
        this.filterType = contentType;
        this.serviceListIndex = serviceListIndex;
        this.hidden = hidden;
        this.selectable = selectable;
        this.sourceType = sourceType;
        this.serviceType = serviceType;
    }

    /**
     * Returns content item index.
     *
     * @return index of Content item.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set content item index.
     *
     * @param index of Content item.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Returns content item name.
     *
     * @return name of Content item.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns content item image.
     *
     * @return image of Content item.
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns content item filterType.
     *
     * @return filterType of Content item
     *         {@link com.iwedia.comm.enums.FilterType}.
     */
    public int getFilterType() {
        return filterType;
    }

    /**
     * Returns MW service list index.
     *
     * @return service list index.
     */
    public int getServiceListIndex() {
        return serviceListIndex;
    }

    public int getIndexInMasterList() {
        return indexInMasterList;
    }

    public int getServiceLCN() {
        return serviceLCN;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isSelectable() {
        return selectable;
    }


    public SourceType getSourceType() {
		return sourceType;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	/**
     * Returns String representation of Content.
     */
    public String toString() {
        return "Content name:" + name + " index:" + index
               + " service list index:" + serviceListIndex
               + " hidden:" + hidden + " selectable:" + selectable;
    }

    /**
     * Compare content item with given content.
     *
     * @param content
     *            - Content item to compare with.
     * @return true if same, else false.
     */
    public boolean equals(Content content) {

        if(content != null) {
            if(this.name.equals(content.getName())
               && this.index == content.getIndex()
               && this.filterType == content.getFilterType()
               && this.serviceListIndex == content.getServiceListIndex()) {
                return true;
            }
        }

        return false;

    }

    public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Content createFromParcel(Parcel in) {

            String className = in.readString();

            try {
                Class clazz = Class.forName(className);
                Constructor c = clazz.getDeclaredConstructor(Parcel.class);
                Content retVal = (Content) c.newInstance(in);
                Field classNameField = clazz.getField("className");
                classNameField.set(retVal, className);
                return retVal;
            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            } catch(NoSuchMethodException e) {
                e.printStackTrace();
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
            } catch(InstantiationException e) {
                e.printStackTrace();
            } catch(IllegalAccessException e) {
                e.printStackTrace();
            } catch(InvocationTargetException e) {
                e.printStackTrace();
            } catch(NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    protected Content(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(className);

        dest.writeInt(index);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeInt(filterType);
        dest.writeInt(serviceListIndex);
        dest.writeInt(indexInMasterList);
        dest.writeInt(serviceLCN);
        dest.writeInt((int)(hidden ? 1 : 0));
        dest.writeInt((int)(selectable ? 1 : 0));
        dest.writeInt(sourceType.getValue());
        dest.writeInt(serviceType.getValue());
    }

    public void readFromParcel(Parcel in) {
        index = in.readInt();
        name = in.readString();
        image = in.readString();
        filterType = in.readInt();
        serviceListIndex = in.readInt();
        indexInMasterList = in.readInt();
        serviceLCN = in.readInt();
        hidden = in.readInt() == 1;
        selectable = in.readInt() == 1;
        sourceType = SourceType.get(in.readInt());
        serviceType = ServiceType.get(in.readInt());
    }

}
