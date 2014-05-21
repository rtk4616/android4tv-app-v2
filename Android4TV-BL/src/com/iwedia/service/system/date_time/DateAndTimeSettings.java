package com.iwedia.service.system.date_time;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.system.date_time.IDateTimeSettings;
import com.iwedia.comm.system.date_time.TimeZone;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.service.IWEDIAService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateAndTimeSettings extends IDateTimeSettings.Stub {
    @Override
    public boolean isAutomatic() throws RemoteException {
        return DateTimeManager.getInstance().isAutomatic();
    }

    @Override
    public void setAutomatic(boolean isAutomatic) throws RemoteException {
        DateTimeManager.getInstance().setAutomatic(isAutomatic);
    }

    @Override
    public TimeDate getTimeDate() throws RemoteException {
        if (DateTimeManager.getInstance().isAutomatic()) {
            return IWEDIAService.getInstance().getDTVManager()
                    .getSetupControl().getTimeDate();
        } else {
            Date date = Calendar.getInstance().getTime();
            return new TimeDate(date.getSeconds(), date.getMinutes(),
                    date.getHours(), date.getDay(), date.getMonth(),
                    date.getYear() + 1900);
        }
    }

    @Override
    public String getTimer() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTimer(String timer) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isSumerTime() throws RemoteException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setSummerTime(boolean status) throws RemoteException {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean is24HourFormat() throws RemoteException {
        return DateTimeManager.getInstance().is24Hour();
    }

    @Override
    public void set24HourFormat(boolean is24Hour) throws RemoteException {
        DateTimeManager.getInstance().set24Hour(is24Hour);
    }

    @Override
    public void setDate(int day, int month, int year) throws RemoteException {
        DateTimeManager.getInstance().setDate(day, month, year);
    }

    @Override
    public void setTime(int hour, int minute) throws RemoteException {
        DateTimeManager.getInstance().setTime(hour, minute);
    }

    @Override
    public List<TimeZone> getTimeZones() throws RemoteException {
        return DateTimeManager.getInstance().getTimeZones();
    }

    @Override
    public void setTimeZone(String id) throws RemoteException {
        DateTimeManager.getInstance().setTimeZone(id);
    }

    @Override
    public int getActiveTimeZoneIndex() throws RemoteException {
        return DateTimeManager.getInstance().getActiveTimezoneIndex();
    }

    @Override
    public int getDateFormat() throws RemoteException {
        return DateTimeManager.getInstance().getDateFormat();
    }

    @Override
    public void setDateFormat(int dateFormat) throws RemoteException {
        DateTimeManager.getInstance().setDateFormet(dateFormat);
    }
}