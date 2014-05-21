package com.iwedia.comm;

import com.iwedia.comm.devices.SourceDevice;

interface ISourceDeviceCallback{

  void switchSourceDevice(in SourceDevice device);
}