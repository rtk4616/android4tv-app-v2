package com.iwedia.comm;

interface IInputOutputCallback{

  void inputDeviceStarted(int deviceIndex);
  void inputDeviceStopped(int deviceIndex);
  void inputDeviceConnected(int deviceIndex);
  void inputDeviceDisconnected(int deviceIndex);
  void inputDeviceVideoSignalChanged(int deviceIndex, boolean signalAvailable);
  void inputDeviceAudioSignalChanged(int deviceIndex, boolean signalAvailable);
}