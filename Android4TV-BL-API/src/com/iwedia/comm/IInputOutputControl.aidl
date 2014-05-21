package com.iwedia.comm;

import com.iwedia.dtv.io.AnalogVideoType;
import com.iwedia.dtv.route.common.RouteInputOutputDescriptor;
import com.iwedia.dtv.io.AspectRatioOutput;
import com.iwedia.dtv.types.AudioDigitalType;
import com.iwedia.dtv.types.VideoResolution;
import com.iwedia.dtv.io.VideoScanning;
import com.iwedia.dtv.io.AudioOutputMode;
import com.iwedia.comm.IInputOutputCallback;
import com.iwedia.dtv.io.LastInputDescriptor;

/**
 * The input and output controller.
 *
 * @author Marko Krnjetin
 *
 */
 interface IInputOutputControl{

	int ioDeviceGetAudioDelay(int deviceIndex);

	void ioDeviceResetActiveDevice();

	/**
	 * Gets the audio type.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return encoding mode.
	 */
	AudioDigitalType ioDeviceGetDigitalAudioEncodingMode(int deviceIndex);

	/**
	 * Gets the resolution.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return resolution.
	 */
	VideoResolution ioDeviceGetResolution(int deviceIndex);

	/**
	 * Gets the video frame rate.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return frame rate.
	 */
	int ioDeviceGetFrameRate(int deviceIndex);

	/**
	 * Gets the video frame rate.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return video scaling.
	 */
	VideoScanning ioDeviceGetVideoScanning(int deviceIndex);

	/**
	 * Gets the audio sample rate.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return audio sample rate.
	 */
	int ioDeviceGetAudioSampleRate(int deviceIndex);

	/**
	 * Gets the number of audio channels.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return number of audio channels.
	 */
	int ioDeviceGetAudioChannels(int deviceIndex);

	/**
	 * Gets the video type.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return video type.
	 */
	AnalogVideoType ioDeviceGetVideoType(int deviceIndex);

	/**
	 * Sets the audio delay.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @param delay
	 *            Audio delay.
	 * @return true if everything is ok, else false
	 */
	int ioDeviceSetAudioDelay(int deviceIndex, int delay);

	/**
	 * Sets the audio output mode.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @param mode
	 *            Audio output mode
	 * @return true if everything is ok, else false
	 */
	int ioDeviceSetAudioOutputMode(int deviceIndex, in AudioOutputMode mode);

	/**
	 * Gets the audio output mode.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return audio output mode
	 */
	AudioOutputMode ioDeviceGetAudioOutputMode(int deviceIndex);

	/**
	 * Sets the audio type.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @param encodingMode
	 *            encoding mode.
	 * @return true if everything is ok, else false
	 */
	int ioDeviceSetDigitalAudioEncodingMode(int deviceIndex, in AudioDigitalType encodingMode);

	/**
	 * Sets the resolution.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @param videoWidth
	 *            video width.
	 * @param videoHeight
	 *            video height.
	 * @return true if everything is ok, else false
	 */
	int ioDeviceSetResolution(int deviceIndex, int videoWidth, int videoHeight);

	/**
	 * Sets the video type.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @param videoOutType
	 *            video type.
	 * @return true if everything is ok, else false
	 */
	int ioDeviceSetVideoType(int deviceIndex, in AnalogVideoType videoOutType);

	/**
	 * Start device.
	 *
	 * @param decoderID
	 *            decoder Id.
	 * @param deviceIndex
	 *            Device index.
	 * @return true if everything is ok, else false
	 */
	int ioDeviceStart(int decoderID, int deviceIndex);

	/**
	 * Stop device.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return true if everything is ok, else false
	 */
	int ioDeviceStop(int decoderID, int deviceIndex);

	/**
	 * Gets is device active.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return is device active.
	 */
	boolean ioGetDeviceActive(int deviceIndex);

	RouteInputOutputDescriptor ioGetDeviceDescriptor(int arg0);
	/**
	 * Gets is device connected.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return is device connected.
	 */
	boolean ioGetDeviceConnected(int deviceIndex);

	/**
	 * Gets is device input.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return is device input.
	 */
	boolean ioGetDeviceInput(int deviceIndex);

	/**
	 * Gets is device input.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @return is device output.
	 */
	boolean ioGetDeviceOutput(int deviceIndex);

	int ioGetDevicesCount();

	boolean ioHdmiCecGetTvPowerOn();

	int ioHdmiCecSetTvPowerOn(int arg0);

	boolean ioHdmiGetArc(int deviceIndex);

	boolean ioHdmiGetAutoLinkPowerOff();

	boolean ioHdmiGetHdmiCec(int deviceIndex);

	boolean ioHdmiGetSpeakerOutput(int deviceIndex);

	void ioHdmiScanDevices();

	int ioHdmiSetAutoLinkPowerOff(int autoLinkPowerOffStatus);

	int ioHdmiSetHdmiCec(int deviceIndex, int hdmiCecStatus);

	int ioHdmiSetSpeakerOutput(int deviceIndex, int speakerOutputStatus);

	boolean ioInit();

	boolean ioTerm();
	
	LastInputDescriptor ioGetLastInput();


	/**
	 * Sets the aspect ratio.
	 *
	 * @param deviceIndex
	 *            Device index.
	 * @param videoOutType
	 *            video type.
	 * @return true if everything is ok, else false
	 */
	AspectRatioOutput outputDeviceGetAspectRatio(int arg0);

	int outputDeviceSetAspectRatio(int arg0, in AspectRatioOutput arg1);

	void ioDeviceStartDVB();
	void registerCallback(IInputOutputCallback callback);
	void unregisterCallback(IInputOutputCallback callback);
}
