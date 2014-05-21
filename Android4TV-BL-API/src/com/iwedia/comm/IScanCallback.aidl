package com.iwedia.comm;

import com.iwedia.comm.content.Content;

/**
 * The DTV services installation related callbacks.
 *
 * @author Milan Vidakovic
 *
 */
interface IScanCallback {

	/**
	*Inform the user about scan progress change.
	*@param value - current scan progress
	*/
	void scanProgressChanged(int value);
	/**
	*Inform the user about scan done.
	*/
	void scanFinished();

	/** Inform the user when found new TV service. */
	void installService(in Content content);

	/**Inform the user about the current signal quality*/
	void signalQuality(int quality);

	/**Inform the user about the current signal strength*/
	void signalStrength(int strength);

	/**Inform the user about the current signal BER*/
	void signalBer(int ber);

	/**
	*Inform the user about locked frequency.
	*/
	void scanTunFrequency(int frequency);

	/**
	 * Inform the user that no channels is found after scan.
	 */
	void noChannelsFound();

	void errorOccurred();
	/**
	*Inform the user when there is no service space.
	*/
	void scanNoServiceSpace();
}