package com.iwedia.comm;

import com.iwedia.comm.IParentalCallback;
import com.iwedia.dtv.types.TimeDate;

/** The class for managing parental control settings.
 *
 * @author Stanislava Markovic
 *
 */

interface IParentalControl {

 	/**
 	* Sets parental control PIN.
 	* @param pin - new pin
 	* @return true if everything is OK, else false.
 	*/
	void setPinCode(int pinCode);

	/**
	* Sets level of parental guidance.
	* @param level - new level of parental guidance.
	* @return
	*/
	void setParentalRate(int rate);

	/**
	 * Get currently set parental rate
	 *
	 * @return Returns currently set age lock type (parental rate limit). {@link com.iwedia.comm.enums.ParentalLevel}
	 */
	int getParentalRate();

	/**
	* Checks if entered PIN is valid.
	* @param pin - pin to check.
	*/
	boolean checkPinCode(int pinCode);

	/**
	* Set the channel lock status.
	* @param serviceIndex index of the service to be locked/unlocked
 	* @param lockStatus new service status to be set
 	* @return true if everything is OK, else false.
 	*/
	void setChannelLock(int serviceIndex, boolean lockStatus);

	/**
	 * Get channel lock status
	 * @param serviceIndex index of the service which status is to be read
	 * @return Returns true if channel if locked, false otherwise
	 */
	boolean getChannelLock(int serviceIndex);

	/**
	 * Get parental lock age for specified service current program.
	 * (viewer age limit))
	 *
	 * @param serviceIndex master index of the service (channel) to be checked
	 * @return Returns parental lock age.
	 */

	int getCurrentProgramParental(int serviceIndex);

	/**
	*Registers parental control callback.
	*/
	void registerCallback(IParentalCallback callback);
	
	/**
	*Unregisters parental control callback.
	*/
	void unregisterCallback(IParentalCallback callback);
}