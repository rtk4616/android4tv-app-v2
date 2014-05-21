package com.iwedia.comm.system;

interface IVoiceInputOutputSettings{

	boolean isCustomSettings();

	void setCustomSettings(boolean value);

	int getNumberOfAvailableLanguages();

	String getAvailableLanguage(int index);

	void setActiveLanguage(int index);

	String getActiveLanguage();

}