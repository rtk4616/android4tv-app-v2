package com.iwedia.comm.system.application;
import com.iwedia.comm.content.applications.AppItem;
import com.iwedia.comm.system.application.IApplicationDetails;

/**
 * The applications controller. This interface manages applications, their details and settings.
 *
 * @author Stanislava Markovic
 *
 */


interface IApplicationSettings{

   /**
	* Check if is allowed installation of non-Market applications.
	*
	* @return true if allowed, else false.
	*/
	boolean isUnknownSource();

   /**
	* Allow or disallow installation of non-Market applications.
	*
	* @param value
	*			- true if you want to allow, else false.
	*/
	void setUnknownSource(boolean allow);


   /**
	* Gets application with the given index.
	*
	* @param index
	*            - index of the application you want to get.
	* @return {@link com.iwedia.comm.content.applications.AppItem}
	*/
	AppItem getApplication(int index);


   /**
	* Returns the number of applications with the given application type.
	*
	* @param appType
	*			- it can be {@link AppListType#ALL} or
	*            {@link AppListType#INSTALLED} or {@link AppListType#RUNNING} or
	*			 {@link AppListType#EXTERNAL}
	* @return number of applications.
	*/
	int getAppListSize(int appType);

   /**
	* Gets application details with the given package name
	*
	* @param packageName
	*			- package name of application you want to get details
	* @return {@link com.iwedia.comm.system.application.IApplicationDetails}
	*/
	IApplicationDetails getApplicationDeatails(String packageName);


   /**
	* Gets list of running services.
	*
	* @return list of runningServices.
	*/
	List<AppItem> getRunningServices();


	/**
	* Stops service.
	*
	*/
	void stopService(String packageName,String className);
}