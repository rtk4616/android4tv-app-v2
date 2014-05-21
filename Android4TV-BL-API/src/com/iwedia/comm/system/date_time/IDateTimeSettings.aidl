package com.iwedia.comm.system.date_time;
import java.util.List;
import com.iwedia.comm.system.date_time.TimeZone;
import com.iwedia.dtv.types.TimeDate;


interface IDateTimeSettings {


	boolean isAutomatic();

	void setAutomatic(boolean value);

	void setDate(int day, int month, int year);

	void setTime(int hour, int minutes);
	
	TimeDate getTimeDate();

	boolean is24HourFormat();

	void set24HourFormat(boolean is24Hour);

	List<TimeZone> getTimeZones();

	void setTimeZone(String id);

	int getActiveTimeZoneIndex();

	String getTimer();

	void setTimer(String timer);

	boolean isSumerTime();

	void setSummerTime(boolean status);

	void setDateFormat(int dateFormat);

	int getDateFormat();
}