package com.iwedia.comm;

import java.util.List;
import com.iwedia.dtv.service.Service;
import com.iwedia.dtv.service.ServiceDescriptor;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.IEpgCallback;
import com.iwedia.comm.IServiceListCallback;

/** A Service list. It holds the list of available services.
 *
 * @author Milan Vidakovic
 *
 */
 interface IServiceControl {

	/** Returns the number of services in the list with the given list index. */
	 int getServiceListCount(int listIndex);

	 /** Stops the active service. Applies only on active service list. */
	 boolean stopService();

	 /** Get  the active service. */
	 Service getActiveService();

	 /** Returns the service for the given service list index and index service.
	 */
	 ServiceDescriptor getServiceDescriptor(int serviceListIndex, int serviceIndex);

	 /** Switch to service with given service list index and serviceIndex.*/
	 boolean goServiceIndexFromServiceList(int serviceListIndex, int serviceIndex, int routeId);

	 /** Stores last played list and service. */
	 void storeLastPlayedListAndService(in Service service);

	 /** Returns the last played list and service. */
	 Service getLastPlayedListAndService();

	 /** Returns service index from given LCN */
	 int getServiceIndexByLCN(int serviceListIndex, int serviceLCN);
	 
	 /**
	 * Returns the number of service lists.
	 *
	 * @return number of service lists
	 */
	 int getNumberOfServiceLists();

	/**
	 * Creates service list with the given index.
	 *
	 * @return true if everything is ok, else false
	 */
    boolean createServiceList(String name);

	/***
	 * Adds service with the specified index to the service list specified by its index.
	 *
	 * @param listIndex
	 *
	 * @param serviceIndex
	 *            Index of the service to be added
	 * @return true if everything is ok, else false
	 */
    boolean addServiceInServiceList(int listIndex, int serviceIndex);

	/**
	 *  Deletes service with the specified index from the service list specified by its index.
	 *
	 * @param listIndex
	 *
	 * @param ServiceIndex
	 *            Index of the service to be deleted
	 * @return ok if everything is OK, else false
	 */
    boolean deleteServiceFromServiceList(int listIndex, int serviceIndex);

	/**
	 * Deletes service list
	 *
	 * @param index
	 *            Index of the favourite list to be deleted
	 * @return ok if everything is OK, else false
	 */
    boolean deleteServiceList(int index);

    /**
     *Set active service according to the provided url.
     *
	 * @param url - url of the service you want info set.
	 *
	 * @return true if everything is OK, else false
	 *
	 */
	 boolean setServiceURL(String url);

	 boolean togglePreviousService();
	 
    /**
     * Renames desired service.
     * 
     * @param listIndex
     *        - Index of list where service is located.
     * @param serviceIndex
     *        - Index of service.
     * @param newServiceName
     *        - New name of service.
     */
    boolean renameService(int listIndex, int serviceIndex, String newServiceName);
	
	/**
     * Renames service list.
     * 
     * @param listIndex
     *        - Index of list you want to sort.
     * @param newListName
     *        - New name of current list.
     */
    boolean renameList(int listIndex, String newListName);
	
	/**
     * Get service list name.
     * 
     * @param listIndex
     *        - Index of the list.
     * @return Name of list.
     */
    String getServiceListName(int listIndex);
	
	/**
     * Changes position of desired service.
     * 
     * @param listIndex
     *        - Index of list where service is located.
     * @param pointedServiceIndex
     *        - Index of service.
     * @param movedServiceIndex
     *        - Index of new service position.
     */
    void movePointedService(int listIndex, int pointedServiceIndex,
            int movedServiceIndex);
	  	 
	void registerCallback(IServiceListCallback callback);
	
	void unregisterCallback(IServiceListCallback callback);

}
