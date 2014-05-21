package com.iwedia.service.system.date_time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.AlarmManager;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.util.Log;

import com.iwedia.comm.enums.DateFormatOrder;
import com.iwedia.comm.system.date_time.TimeZone;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.R;

public class DateTimeManager {
    private static final String LOG_TAG = "DateTimeManager";
    private SimpleDateFormat sdf;
    String dateTime;
    private static DateTimeManager instance = null;
    private static final String HOURS_12 = "12";
    private static final String HOURS_24 = "24";
    private static Calendar c;
    static int time_format = 0;
    private static final String XMLTAG_TIMEZONE = "timezone";
    private static final String KEY_ID = "id"; // value: String
    private static final String KEY_DISPLAYNAME = "name"; // value: String
    private static final String KEY_GMT = "gmt"; // value: String
    private static final String KEY_OFFSET = "offset"; // value: int (Integer)
    private static final int HOURS_1 = 60 * 60000;
    private Context context;
    private int activeTimeZoneIndex = 0;

    public DateTimeManager() {
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        context = IWEDIAService.getInstance();
    }

    public String getDateTime() {
        dateTime = sdf.format(new Date());
        return dateTime;
    }

    public boolean isAutomatic() {
        try {
            time_format = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AUTO_TIME);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (time_format == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void setAutomatic(boolean isAutomatic) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.AUTO_TIME, isAutomatic ? 1 : 0);
    }

    public void set24Hour(boolean is24Hour) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24, is24Hour ? HOURS_24 : HOURS_12);
    }

    public boolean is24Hour() {
        return DateFormat.is24HourFormat(context);
    }

    public void setDate(int day, int month, int year) {
        c = Calendar.getInstance();
        c.set(year, month, day);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.setTime(c.getTimeInMillis());
    }

    public void setTime(int hour, int minute) {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.setTime(c.getTimeInMillis());
    }

    //
    // public static String getDate() {
    // c = Calendar.getInstance();
    // int month = c.get(Calendar.MONTH) + 1;
    // return c.get(Calendar.DATE) + "/" + month + "/" + c.get(Calendar.YEAR);
    // }
    //
    // public static String getTime(Context ctx) {
    //
    // c = Calendar.getInstance();
    //
    // if (DateTimeManager.is24Hour(ctx)) {
    //
    // return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
    // } else {
    //
    // if (c.get(Calendar.AM_PM) == Calendar.AM)
    // am_pm = "AM";
    // else
    // am_pm = "PM";
    //
    // return c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + " "
    // + am_pm;
    //
    // }
    //
    // }
    public List<TimeZone> getTimeZones() {
        final MyComparator comparator = new MyComparator(KEY_OFFSET);
        final List<HashMap<String, Object>> sortedList = getZones();
        Collections.sort(sortedList, comparator);
        java.util.TimeZone tz;
        TimeZone timeZone;
        List<TimeZone> list = new ArrayList<TimeZone>();
        for (int i = 0; i < sortedList.size(); i++) {
            tz = java.util.TimeZone.getTimeZone(sortedList.get(i).get(KEY_ID)
                    .toString());
            timeZone = new TimeZone(sortedList.get(i).get(KEY_DISPLAYNAME)
                    .toString(), sortedList.get(i).get(KEY_GMT).toString(),
                    tz.getDisplayName(), tz.getID());
            if (tz.getID().equals(getActiveTimezone())) {
                activeTimeZoneIndex = i;
                Log.e(LOG_TAG, "activeTimeZoneIndex " + activeTimeZoneIndex);
            }
            list.add(timeZone);
        }
        return list;
    }

    public String getActiveTimezone() {
        String timezoneID = java.util.TimeZone.getDefault().getID();
        return timezoneID;
    }

    public int getActiveTimezoneIndex() {
        return activeTimeZoneIndex;
    }

    public void setTimeZone(String id) {
        final AlarmManager alarm = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarm.setTimeZone(id);
        DateTimeManager.getInstance().getTimeZones();
        if (DateTimeManager.getInstance().isAutomatic()) {
            System.out.println("setTime ManualZone" + getActiveTimezoneIndex());
            IWEDIAService.getInstance().getDTVManager().getSetupControl()
                    .setTimeZone(getActiveTimezoneIndex());
        }
    }

    private List<HashMap<String, Object>> getZones() {
        final List<HashMap<String, Object>> myData = new ArrayList<HashMap<String, Object>>();
        final long date = Calendar.getInstance().getTimeInMillis();
        try {
            XmlResourceParser xrp = context.getResources().getXml(
                    R.xml.timezones);
            while (xrp.next() != XmlResourceParser.START_TAG) {
                continue;
            }
            xrp.next();
            while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                    if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                        return myData;
                    }
                    xrp.next();
                }
                if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
                    String id = xrp.getAttributeValue(0);
                    String displayName = xrp.nextText();
                    addItem(myData, id, displayName, date);
                }
                while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                    xrp.next();
                }
                xrp.next();
            }
            xrp.close();
        } catch (XmlPullParserException xppe) {
            Log.e(LOG_TAG, "Ill-formatted timezones.xml file");
        } catch (java.io.IOException ioe) {
            Log.e(LOG_TAG, "Unable to read timezones.xml file");
        }
        return myData;
    }

    private void addItem(List<HashMap<String, Object>> myData, String id,
            String displayName, long date) {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(KEY_ID, id);
        map.put(KEY_DISPLAYNAME, displayName);
        final java.util.TimeZone tz = java.util.TimeZone.getTimeZone(id);
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        final StringBuilder name = new StringBuilder();
        name.append("GMT");
        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }
        name.append(p / (HOURS_1));
        name.append(':');
        int min = p / 60000;
        min %= 60;
        if (min < 10) {
            name.append('0');
        }
        name.append(min);
        map.put(KEY_GMT, name.toString());
        map.put(KEY_OFFSET, offset);
        myData.add(map);
    }

    private class MyComparator implements Comparator<HashMap<?, ?>> {
        private String mSortingKey;

        public MyComparator(String sortingKey) {
            mSortingKey = sortingKey;
        }

        @SuppressWarnings("unused")
        public void setSortingKey(String sortingKey) {
            mSortingKey = sortingKey;
        }

        @SuppressWarnings("unchecked")
        public int compare(HashMap<?, ?> map1, HashMap<?, ?> map2) {
            Object value1 = map1.get(mSortingKey);
            Object value2 = map2.get(mSortingKey);
            /*
             * This should never happen, but just in-case, put non-comparable
             * items at the end.
             */
            if (!isComparable(value1)) {
                return isComparable(value2) ? 1 : 0;
            } else if (!isComparable(value2)) {
                return -1;
            }
            return ((Comparable<Object>) value1).compareTo(value2);
        }

        private boolean isComparable(Object value) {
            return (value != null) && (value instanceof Comparable);
        }
    }

    public static DateTimeManager getInstance() {
        if (instance == null) {
            instance = new DateTimeManager();
        }
        return instance;
    }

    public void setDateFormet(int dateFormat) {
        switch (dateFormat) {
            case DateFormatOrder.MDY:
                Settings.System.putString(context.getContentResolver(),
                        Settings.System.DATE_FORMAT, "Mdy");
                break;
            case DateFormatOrder.DMY:
                Settings.System.putString(context.getContentResolver(),
                        Settings.System.DATE_FORMAT, "dMy");
                break;
            case DateFormatOrder.YMD:
                Settings.System.putString(context.getContentResolver(),
                        Settings.System.DATE_FORMAT, "yMd");
                break;
            default:
                break;
        }
    }

    public int getDateFormat() {
        String dateFormatOrder = new String(
                DateFormat.getDateFormatOrder(context));
        if (dateFormatOrder.equals("Mdy")) {
            return DateFormatOrder.MDY;
        } else if (dateFormatOrder.equals("dMy")) {
            return DateFormatOrder.DMY;
        } else if (dateFormatOrder.equals("yMd")) {
            return DateFormatOrder.YMD;
        } else {
            return DateFormatOrder.DMY;
        }
    }

    public void setTimeZoneFromStream(int minute, String id) {
        // TODO Auto-generated method stub
    }
}