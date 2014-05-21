package com.iwedia.comm;

import com.iwedia.comm.ICICallback;
import com.iwedia.dtv.ci.ApplicationInfo;
import com.iwedia.dtv.ci.EnquiryData;
import com.iwedia.dtv.ci.OperatorProfileInfo;

/** The Conditional Access Control. Used to control scrambled channels and other protected stuff.
 *
 * @author Milan Vidakovic
 *
 */
 interface ICIControl {

	/**
	* Obtains a handle to a card slot, for the purposes of the CI
	* interface.
	*
 	* @param slotNumber Slot number of the card interface.
 	*
	*/
	void open(int slotNumber);
	
	/**
	* close card slot, for the purposes of the CI
	* interface.
	*
 	* @param slotNumber Slot number of the card interface.
 	*
	*/
	void close(int slotNumber);
	
    /**
	* Sends an enquire question answer
	*
 	* @param slotNumber Slot number of the card interface.
 	* @param answer MMI answer to be sent.
 	* @param cancel notification of cancellation 	
	*/
	void answer(int slotNumber, String answer, int cancel);	
	
	/**
	* Sends menu item number of chosen item
	*
 	* @param slotNumber Slot number of the card interface.
 	* @param choice Menu item number.
	*/
	void selectMenuItem(int slotNumber, int choice);					
	
	/**
	* Gets title of the MMI menu/list.
	*
 	* @param slotNumber Slot number of the card interface.
 	*
	*/
	String getTitle(int slotNumber);
	
	/**
 	* Gets the top text of the MMI menu/list.
 	*
 	* @param slotNumber Slot number of the card interface. 		
 	*/
	String getTopText(int slotNumber);
	
	/**
 	* Gets the bottom text of the MMI menu/list.
 	*
 	* @param slotNumber Slot number of the card interface. 		
 	*/
	String getBottomText(int slotNumber);
	
	/**
 	* Gets the text of the given MMI menu item number
 	*
 	* @param slotNumber Slot number of the card interface. 
 	* @param itemNumber MMI menu item number	
 	*/
	String getMenuItemText(int slotNumber, int itemNumber);
	
	/**
 	* Gets the text of the given MMI list item number
 	*
 	* @param slotNumber Slot number of the card interface. 
 	* @param itemNumber MI menu item number	
 	*/	
	String getListItemText(int slotNumber, int itemNumber);
	
	/**
 	* Gets the text of the MMI Enquiry
 	*
 	* @param slotNumber Slot number of the card interface. 
 	*/	
	EnquiryData getEnquiryText(int slotNumber);
	
	/**
 	* Gets number of items
 	*
 	* @param slotNumber Slot number of the card interface. 
 	*/		
	int getNumberOfItems(int slotNumber);
	
	/**
 	* Gets current language on CI
 	*
 	* @param slotNumber Slot number of the card interface. 
 	*/		
	String getLanguage(int slotNumber);
	
	/**
 	* Sets current language on CI
 	*
 	* @param slotNumber Slot number of the card interface. 
 	*/		
	void setLanguage(int slotNumber, String language);
	
	/**
 	* Scans services by NIT (given by the CAM). Operator profile.
 	*
 	* @param slotNumber Slot number of the card interface. 
 	* @param liveRouteID liveRouteID
 	*/	
	void installOperatorProfile(int liveRouteID, int slotNumber);
	
	/**
 	* Gets count of currently installed operator profiles.
 	*/	
	int getOperatorProfileCount();
	
	/**
 	* Gets detailed info about the installed OP based on id.
 	* 
 	* @param profileId id of the profile	
 	*/	
	OperatorProfileInfo getOperatorProfileInfo(int profileId);
	
	/**
 	* Removes previously installed operator profile.
 	* 
 	* @param profileId id of the profile	
 	*/	
	void removeOperatorProfile(int profileId);

	/**
 	* Get number of CI+ application on system
 	*/		
	int getNumberOfApplications();
	
	/**
 	* Gets CI application info	
 	*/	
	ApplicationInfo getApplicationInfo(int appNumber);	
	
	/**
 	* Exits operator profile 	
 	*/	
	void exitOperatorProfile();

	/**
 	* Sends user reply.
 	* 
 	* @param reply user reply
 	*/	
    void operatorProfileUserReply(int reply);    
	
	/**
 	* Enter operator profile.
 	* 
 	* @param listIndex index of service list
 	*/	
	void enterOperatorProfile(int listIndex);
		
	/**
 	* Unattended recording, set CICAM pin.
 	* 
 	* @param pin
 	*/	
	void setPin(int pin);
	
	/**
 	* Unattended recording, get CICAM pin.
 	*/	
	int getPin();
	
	void registerCallback(ICICallback callback);
	
	void unregisterCallback(ICICallback callback);

}
