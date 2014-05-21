package com.iwedia.gui.util;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.enums.DateFormatOrder;
import com.iwedia.gui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for Conversion Date to Progress Bar Value
 * 
 * @author Branimir Kovacevic
 */
public class DateTimeConversions {
    private static final String TAG = "Conversions";
    private static SimpleDateFormat formatTime12HourFull = new SimpleDateFormat(
            "KK:mm:ss a");
    private static SimpleDateFormat formatTime24HourFull = new SimpleDateFormat(
            "HH:mm:ss");
    private static SimpleDateFormat formatTime12Hour = new SimpleDateFormat(
            "KK:mm a");
    private static SimpleDateFormat formatTime24Hour = new SimpleDateFormat(
            "HH:mm");
    private static SimpleDateFormat formatMDY = new SimpleDateFormat(
            "MM/dd/yyyy");
    private static SimpleDateFormat formatDMY = new SimpleDateFormat(
            "dd/MM/yyyy");
    private static SimpleDateFormat formatYMD = new SimpleDateFormat(
            "yyyy/MM/dd");

    public static String getTimeSting(Date date) {
        boolean is24HourFormat = true;
        try {
            is24HourFormat = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().is24HourFormat();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (is24HourFormat) {
            return formatTime24Hour.format(date);
        } else {
            return formatTime12Hour.format(date);
        }
    }

    public static String getDateTimeSting(Date date) {
        boolean is24HourFormat = true;
        String timeString = "";
        String dateString = "";
        int dateFormat = DateFormatOrder.DMY;
        try {
            is24HourFormat = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().is24HourFormat();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (is24HourFormat) {
            timeString = formatTime24HourFull.format(date);
        } else {
            timeString = formatTime12HourFull.format(date);
        }
        try {
            dateFormat = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().getDateFormat();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (dateFormat) {
            case DateFormatOrder.DMY:
                dateString = formatDMY.format(date);
                break;
            case DateFormatOrder.MDY:
                dateString = formatMDY.format(date);
                break;
            case DateFormatOrder.YMD:
                dateString = formatYMD.format(date);
                break;
            default:
                dateString = formatDMY.format(date);
                break;
        }
        return timeString + " " + dateString;
    }

    public static String getDateSting(Date date) {
        String dateString = "";
        int dateFormat = DateFormatOrder.DMY;
        try {
            dateFormat = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().getDateFormat();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (dateFormat) {
            case DateFormatOrder.DMY:
                dateString = formatDMY.format(date);
                break;
            case DateFormatOrder.MDY:
                dateString = formatMDY.format(date);
                break;
            case DateFormatOrder.YMD:
                dateString = formatYMD.format(date);
                break;
            default:
                dateString = formatDMY.format(date);
                break;
        }
        return dateString;
    }
}
