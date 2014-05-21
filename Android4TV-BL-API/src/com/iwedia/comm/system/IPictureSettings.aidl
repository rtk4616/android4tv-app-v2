package com.iwedia.comm.system;

import com.iwedia.dtv.types.AspectRatioMode;

interface IPictureSettings{

	/**
	 *  returns active picture mode
	 */
	int getActivePictureMode();

	/**
	 *  sets active picture mode
	 */
	void setActivePictureMode(int pictureMode);

	/**
	 *  returns active aspect ratio
	 */
	AspectRatioMode getAspectRatioMode();

	/**
	 *  sets active aspect ratio;
	 */
	void setAspectRatioMode(in AspectRatioMode aspectRatioMode);

	/**
	 *  returns active color temperature;
	 */
	int getActiveColorTemperature();

	/**
	 *  sets active color temperature;
	 */
	void setActiveColorTemperature(int colorTemperature);

	/**
	 *  returns active noise reduction;
	 */
	int getActiveNoiseReduction();

	/**
	 *  sets active noise reduction;
	 */
	void setActiveNoiseReduction(int noiseReduction);

	/**
	 *  returns active film mode;
	 */
	int getActiveFilmMode();

	/**
	 *  sets active film mode;
	 */
	void setActiveFilmMode(int filmMode);

	/**
	 *  returns active fine motion;
	 */
	int getActiveFineMotion();

	/**
	 *  sets active fine mode;
	 */
	void setActiveFineMode(int fineMode);

	/**
	 *  returns active theme;
	 */
	String getActiveTheme();

	/**
	 *  sets active theme;
	 */
	void setActiveTheme(String theme);

	/**
	 *  returns brightness value;
	 */
	int getBrightness();

	/**
	 *  sets brightness value;
	 */
	void setBrightness(int brightness);

	/**
	 *  returns contast values;
	 */
	int getContrast();

	/**
	 *  sets contrast value;
	 */
	void setContrast(int contrast);

	/**
	 *  returns color value;
	 */
	int getColor();

	/**
	 *  sets color value;
	 */
	void setColor(int color);

	/**
	 *  returns sharpness value;
	 */
	int getSharpness();

	/**
	 *  sets sharpness value;
	 */
	void setSharpness(double sharpness);

    /**
     *  returns hue value;
     */
    int getHue();

    /**
     *  sets hue value;
     */
    void setHue(int hue);

    /**
     *  returns saturation value;
     */
    int getSaturation();

    /**
     *  sets saturation value;
     */
    void setSaturation(int saturation);

	/**
	 *  returns backlight value;
	 */
	int getBacklight();

	/**
	 *  sets backlight value;
	 */
	void setsBacklight(int backlight);

	/**
	 *  returns dynamic backlight state;
	 */
	boolean isDynamicBacklight();

	/**
	 *  sets dynamic backlight state;
	 */
	void setDynamicBacklight(boolean dynamicBacklight);

	/**
	 *  Sets all parameters to default values
	 */
	void setPictureMenuDefaultSettings();

}