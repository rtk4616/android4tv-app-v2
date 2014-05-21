package com.iwedia.comm.content.service;

import android.os.Parcel; 
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IServiceControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.service.ServiceType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.comm.images.ImageManager;
import com.iwedia.dtv.service.ServiceDescriptor;

/**
 * ServiceContent inherits all the fields and methods of Content class and use
 * them to represent MW TV service. This class is used in ContentFilterDVB_S,
 * ContentFilterDVB_C and ContentFilterDVB_T.
 * {@link com.iwedia.service.content.ContentFilterDVB_S},
 * {@link com.iwedia.service.content.ContentFilterDVB_T},
 * {@link com.iwedia.service.content.ContentFilterDVB_C},
 * {@link com.iwedia.comm.content.Content}.
 * 
 * @author Marko Zivanovic
 * 
 */
public class ServiceContent extends Content implements IServiceContent,
		Parcelable {

	/**
	 * ServiceList control interface {@link com.iwedia.comm.IServiceListControl}
	 * 
	 */
	private IServiceControl iServiceListControl;

	/**
	 * Instance of ExtendedService. Used to retrieve service informations of MW
	 * TV service like frequency, audioPID, videoPID, subtitleComponentCount,
	 * teletextComponentCount, and many more.
	 */
	private ServiceDescriptor serviceDesc;

	/**
	 * This field is used to manage if service object of this class should be
	 * sent through binder. Used when ServiceContent needs to be sent in
	 * scanCallback.
	 */
	private boolean sendService;

	/**
	 * Constructor used in ContentFilterDVB_(S,T,C) to create ServiceContent
	 * from passed index. {@link com.iwedia.service.content.ContentFilterDVB_S},
	 * {@link com.iwedia.service.content.ContentFilterDVB_T},
	 * {@link com.iwedia.service.content.ContentFilterDVB_C},
	 * 
	 * @param index
	 *            index of radio content.
	 * @param serviceList
	 *            ServiceList control interface
	 *            {@link com.iwedia.comm.IServiceListControl}.
	 * @param serviceListIndex
	 *            - MW service list index.
	 */

	public ServiceContent(int index, IServiceControl serviceListControl,
			int serviceListIndex) {

		this.iServiceListControl = serviceListControl;
		initValues(index, serviceListIndex);
	}

	/**
	 * Constructor used to create ServiceContent for ScanCallback when a TV
	 * service has been scanned.
	 * 
	 * @param name
	 *            name of TV service.
	 */
	public ServiceContent(String name, ServiceType serviceType) {
		this.index = -1;
		this.name = name;
		this.filterType = FilterType.ALL;
		this.image = ImageManager.getInstance().getImageUrl(name);
		this.sendService = false;
		this.hidden = false;
		this.selectable = true;
		this.serviceType = serviceType;
	}

	/**
	 * Initialize fields
	 * 
	 * @param index
	 */
	private void initValues(int index, int serviceListIndex) {

		Log.e("Service", "index: " + index + " serviceListIndex: "
				+ serviceListIndex);

		try {
			if (iServiceListControl
					.getServiceDescriptor(0, 0)
					.getName().contains("Dummy")
					&& serviceListIndex == 0) {
				index += 1;
			}
			serviceDesc = iServiceListControl.getServiceDescriptor(
					serviceListIndex, index);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.index = index;

		if (serviceDesc != null) {
			Log.e("Service", serviceDesc.toString());

			this.indexInMasterList = serviceDesc.getMasterIndex();
			this.name = serviceDesc.getName();
			this.filterType = serviceListIndex;
			this.serviceLCN = serviceDesc.getLCN();
			this.hidden = serviceDesc.isHidden();
			this.selectable = serviceDesc.isSelectable();
			this.sourceType = serviceDesc.getSourceType();
			this.serviceType = serviceDesc.getServiceType();

			Log.e("Service", "ServiceType:" + serviceDesc.getServiceType());
			Log.e("Service", "serviceType:" + serviceListIndex);
			Log.e("Service", "FilterType:" + this.filterType);
			this.filterType = serviceListIndex;
			Log.e("Service", "FilterType:" + this.filterType);

			this.image = ImageManager.getInstance().getImageUrl(
					serviceDesc.getName());
		}
		this.sendService = true;
		this.serviceListIndex = serviceListIndex;

	}

	public ServiceContent(ServiceDescriptor dtvService, int index,
			int serviceListIndex) {
		this.index = index;
		serviceDesc = dtvService;

		this.indexInMasterList = serviceDesc.getMasterIndex();
		this.name = serviceDesc.getName();
		this.filterType = serviceListIndex;
		this.hidden = serviceDesc.isHidden();
		this.selectable = serviceDesc.isSelectable();
		this.sourceType = serviceDesc.getSourceType();
		this.serviceType = serviceDesc.getServiceType();
		this.filterType = serviceListIndex;

		this.image = ImageManager.getInstance().getImageUrl(
				serviceDesc.getName());
		this.sendService = true;
		this.serviceListIndex = serviceListIndex;
	}

	public static final Parcelable.Creator<ServiceContent> CREATOR = new Parcelable.Creator<ServiceContent>() {
		public ServiceContent createFromParcel(Parcel in) {
			return new ServiceContent(in);
		}

		public ServiceContent[] newArray(int size) {
			return new ServiceContent[size];
		}
	};

	public ServiceContent(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public void readFromParcel(Parcel in) {
		super.readFromParcel(in);

		this.sendService = in.readByte() == 1;
		if (sendService == true) {
			serviceDesc = ServiceDescriptor.CREATOR.createFromParcel(in);
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeByte((byte) (sendService ? 1 : 0));
		if (serviceDesc != null) {
			serviceDesc.writeToParcel(dest, flags);
		}
	}

	/**
	 * Returns true if service is scrambled, otherwise false.
	 */
	public boolean isScrambled() {
		return serviceDesc.isScrambled();
	}

	/**
	 * Returns TV Service image url.
	 */
	public String getImageUrl() {
		return ImageManager.getInstance().getImageUrl(name);
	}

	/**
	 * Returns index of service in the MW master list.
	 * 
	 * @return index in master list.
	 */
	@Override
	public int getIndexInMasterList() {
		return serviceDesc.getMasterIndex();
	}

	public ServiceDescriptor getServiceDescriptor() {
		return serviceDesc;
	}

}