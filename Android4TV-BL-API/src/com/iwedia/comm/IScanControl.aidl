package com.iwedia.comm;

import com.iwedia.comm.IScanCallback;
import com.iwedia.dtv.scan.SignalInfo;
import com.iwedia.dtv.scan.FecType;
import com.iwedia.dtv.scan.BandType;
import com.iwedia.dtv.scan.Polarization;
import com.iwedia.dtv.scan.Modulation;
import com.iwedia.dtv.types.AnalogEncodingMode;

/**
 * Service installation controller.
 *
 * @author Milan Vidakovic
 *
 */
 interface IScanControl {

	/**
	 *  Initiate scanning for all services on a complete frequency range.
	 *
	 * @param signalType Type of the signal (cable, terrestrial, satellite or IP)
	 * @param updateList Flag that specifies should existing service list be updated
	 * with new services, or reset before the scan
	 * @return true if everything is ok, else false`
	 */
	boolean scanAll(int signalType, boolean updateList);

	/**
	 *  Abort previously started scan.
	 *
	 *@return true if everything is ok, else false`
	 */
	boolean abortScan();

	/**
	 * Manual scan for desired input params.
	 * Depending on signal type, other parameters needs to be set for manual scan.
	 * @param signalType Type of the signal (cable, terrestrial,satellite or IP)
	 * @param updateList Flag that specifies should existing service list be updated
	 * with new stations, or reset before the scan
	 *@return true if everything is ok, else false
	 */
	boolean manualScan(int signalType, boolean updateList);

	/**
	 *  Sets the LNB type.
	 *
	 * @param lnbType Index in list of LNB types
	 *@return true if everything is ok, else false
	 */
	void setLnbType(int lnbType);

	/**
	 * Gets the index of current LNB type
	 *
	 * @return Index of current LNB type
	 */
	int getLnbType();

	/**
	 *  Gets the count of LNB types
	 *
	 * @return Count of LNB types
	 */
	int getLnbTypeCount();

	/**
	 *  Gets the Name of LNB type
	 *
	 * @param index Index in list of LNB Types
	 * @return name of the LNB
	 */
	String getLnbName(int index);

	/**
	 *  Sets the frequency of LNB oscillator for high band reception.
	 *
	 * @param lnbHi Frequency of LNB oscillator for high band reception.
	 * If LNB has only one oscillator this value should be zero.
	 *@return true if everything is ok, else false
	 */
	void setLnbHigh(int lnbHi);

	/**
	 *  Gets the frequency of LNB oscillator for high band reception.
	 *
	 * @return Frequency of LNB oscillator for high band reception.
	 * If LNB has only one oscillator the return value should be zero.
	 */
	int getLnbHigh();

	/**
	 *  Sets the frequency of LNB oscillator for low band reception.
	 *
	 * @param lnbLo Frequency of LNB oscillator for low band reception.
	 * If LNB has only one oscillator this value should represent frequency of LNB oscillator.
	 *@return true if everything is ok, else false
	 */
	void setLnbLow(int lnbLo);

	/**
	 *  Gets the frequency of LNB oscillator for high band reception.
	 *
	 * @return Frequency of LNB oscillator for low band reception.
	 * If LNB has only one oscillator the return value represents frequency of LNB oscillator
	 */
	int getLnbLow();

	/**
	 * Sets the LNB band type.
	 *
	 * @param bandType LNB band type
	 *@return true if everything is ok, else false
	 */
	void setLnbBandType(in BandType bandType);

	/**
	 * Gets LNB band type
	 *
	 * @return LNB band type
	 */
	BandType getLnbBandType();

	/**
	 *  Sets the frequency of the service for manual scan.
	 *
	 * @param frequency Frequency of the service for manual scan.
	 *@return true if everything is ok, else false
	 */
	void setFrequency(int frequency);

	/**
	 *  Gets the frequency of the current service.
	 *
	 * @return Frequency of the current service.
	 */
	int getFrequency();

	/**
	 *  Sets the symbol rate of the service for manual scan.
	 *
	 * @param symbolRate Symbol rate of the service for manual scan.
	 *@return true if everything is ok, else false
	 */
	void setSymbolRate(int symbolRate);

	/**
	 *  Gets the symbol rate of the current service.
	 *
	 * @return Symbol rate of the service
	 */
	int getSymbolRate();

	/**
	 *  Sets the modulation of the service for manual scan.
	 *
	 * @param modulation Modulation of the service for manual scan.
	 *@return true if everything is ok, else false
	 */
	void setModulation(in Modulation modulation);

	/**
	 *  Gets the modulation of the current service.
	 *
	 * @return modulation of the service
	 */
	Modulation getModulation();

	/**
	 *  Sets FecType of the service for manual scan.
	 *
	 * @param fec FecType of the service for manual scan.
	 *@return true if everything is ok, else false
	 */
	void setFecType(in FecType fec);

	/**
	 *  Gets the FecType of the current service.
	 *
	 * @return {@link FecType}
	 */
	FecType getFecType();

	/**
	 *  Sets the polarization of the service for manual scan.
	 *
	 * @param polarization Polarization of the service for manual scan
	 *@return true if everything is ok, else false
	 */
	void setPolarization(in Polarization polarization);

	/**
	 *  Gets the polarization of the service.
	 *
	 * @return Polarization of the service.
	 */
	Polarization getPolarization();

	/**Set the sattelite
	* @param index - index of the sattelite
	 *@return true if everything is ok, else false
	 */
	void setSatelite(int index);

	/**
	 * Get index of the current satellite from satellite list.
	 * @return Returns index of satellite
	 */
	int getCurrentSatelliteIndex();

	/**
	 * Get number of satellites.
	 * @return Returns number of satellites
	 */
	int getNumberOfSatellites();

	/**
	 * Get name of the satellite with 'satelliteIndex'.
	 * @param satelliteIndex Index of the satellite
	 * @return Returns satellite name
	 */
	String getSatelliteName(int satelliteIndex);


	void registerCallback(IScanCallback scanCallback);

	void unregisterCallback(IScanCallback scanCallback);

	/**
	 * Gets signal information.
	 *
	 *
	 * @return {@link SignalInfo}
	 */
	SignalInfo getSignalInfo();

	/**
	 *  Sets the scan type for manual scan.
	 *
	 * @param scanType Scan type for manual scan.
	 *@return true if everything is ok, else false
	 */
	void setScanType(int scanType);

	/**
	 *  Gets the current scan type.
	 *
	 * @return scanType Current scan type
	 */
	int getScanType();

	/**
	 * Sets the atv system for manual scan.
	 *
	 * @param system
	 *            Atv system of the service for manual scan
	 * @return true if everything is ok, else false
	 */
	void setAnalogEncodingMode(in AnalogEncodingMode system);

	/**
	 * Gets the atv system for manual scan.
	 * @return atv system value
	 */
	AnalogEncodingMode getAnalogEncodingMode();

	void atvFineTune(int frequency, boolean save);

	/**
	 * Sets the network number.
	 *
	 * @param netNumber
	 *            network number
	 * @return true if everything is ok, else false
	 */
	void setNetNumber(int netNumber);

	/**
     * Store network default values
	 * @return true if everything is ok, else false
     */
	void storeNetworkDefaultValues(int NID, int frequency, int symbolRate, in Modulation modulation);
}
