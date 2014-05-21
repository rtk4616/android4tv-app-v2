package com.iwedia.comm.system.external_and_local_storage;

interface IExternalLocalStorageSettings{

	String getExternalStorageTotalSpace();

	String getExternalStorageAvailableSpace();

	String getExternalStoragePath();

	String getLocalStorageTotalSpace();

	String getLocalStorageAvailableSpace();

	boolean isExternalMemoryFull();

	void unmount();

	void format();
}