package com.iwedia.gui.osd;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for Conversion Date to Progress Bar Value
 * 
 * @author Branimir Kovacevic
 */
public class Conversions {
    private static final String TAG = "Conversions";
    public static SimpleDateFormat sdf = new SimpleDateFormat(
            "HH:mm:ss dd/MM/yyyy");
    public static Date date;

    public static int getEventPassedPercent(Date startDate, Date endDate,
            Date currentDate) {
        int returnValue = 0;
        try {
            long startTime = startDate.getTime();
            long endTime = endDate.getTime();
            long currentTime = currentDate.getTime();
            if (currentTime < startTime || currentTime > endTime) {
                return 0;
            }
            returnValue = (int) ((((currentTime - startTime) * 100) / (endTime - startTime)));
        } catch (Exception e) {
            Log.i(TAG, "getEventPassedPercent");
            e.printStackTrace();
        }
        if (returnValue > 100) {
            return 100;
        } else if (returnValue < 0) {
            return 0;
        } else {
            return returnValue;
        }
    }

    public static int getPVRPassedPercent(int currentTime, int endTime) {
        int returnValue = 0;
        Log.d(TAG, "Initial returnValue :" + returnValue);
        try {
            returnValue = (int) (((currentTime * 100) / (endTime)));
        } catch (Exception e) {
            Log.i(TAG, "getPVRPassedPercent");
            e.printStackTrace();
        }
        if (returnValue > 100) {
            return 100;
        } else if (returnValue < 0) {
            return 0;
        } else {
            return returnValue;
        }
    }
}
