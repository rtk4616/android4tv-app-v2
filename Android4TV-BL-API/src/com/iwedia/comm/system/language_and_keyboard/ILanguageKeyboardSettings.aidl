package com.iwedia.comm.system.language_and_keyboard;

interface ILanguageKeyboardSettings{

List<String> getAvailableLanguages();

void setActiveLanguage(String language);

int getActiveLanguageIndex();

List<String> getAvailableCountries();

void setActiveContry(String country);

String getActiveCountry();

List<String> getAvailableKeyboardTypes();

String getActiveKeyboardType();

void setActiveKeyboardType(String keyboardType);

boolean systemStandby();

void setFontScale(float scale);

float getActiveFontScale();

//PowerOnStatus powerOnStatus();

String convertTrigramsToLanguage(String language, boolean isLanguage);

}