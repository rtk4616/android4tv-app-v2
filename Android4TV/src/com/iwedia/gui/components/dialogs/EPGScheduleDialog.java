package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.iwedia.comm.IReminderControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.epg.EpgEventType;
import com.iwedia.dtv.pvr.SmartCreateParams;
import com.iwedia.dtv.reminder.ReminderSmartInfo;
import com.iwedia.dtv.reminder.ReminderSmartParam;
import com.iwedia.dtv.reminder.ReminderTimerInfo;
import com.iwedia.dtv.reminder.ReminderType;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.util.DateTimeConversions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Dialog for EPG scheduling
 * 
 * @author Branimir Pavlovic
 */
public class EPGScheduleDialog extends A4TVDialog implements
        A4TVDialogInterface, OSDGlobal {
    public static final String TAG = "EPGScheduleDialog";
    private Context ctx;
    private A4TVTextView eventName, eventTime, eventDescription, eventGenre,
            tvGenre, eventComponents;
    private A4TVButton buttonScheduleRecord, buttonReminder, buttonIMDBInfo,
            buttonNowNext;
    private TextView textViewParentalRate;
    private EpgEvent event, eventNext;
    private A4TVAlertDialog alertDialog;
    // fields for callback
    private A4TVAlertDialog alertDialogCallBack;
    private Handler handlerForCallback;
    // private EpgEvent eventReminder;
    private static Content activeContent = null;

    public EPGScheduleDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        fillDialog();
        setDialogAttributes();
        init();
        setMenuButtonEnabled(false);
    }

    @Override
    public void fillDialog() {
        setContentView(R.layout.epg_schedule_dialog);
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialog });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }

    // not needed here, attributes are passed by style
    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = (int) (MainActivity.dialogWidth);
        getWindow().getAttributes().height = (int) (MainActivity.dialogHeight);
    }

    public void showEpgExtended(EpgEvent event, EpgEvent eventNext,
            boolean isFromEPG) {
        StringBuffer components = new StringBuffer();
        String extendedDescription = "";
        components.append(ctx.getResources().getString(
                R.string.component_type_components)
                + "\n");
        // String components = ctx.getResources().getString(
        // R.string.component_type_components)
        // + "\n";
        this.event = event;
        this.eventNext = eventNext;
        try {
            activeContent = MainActivity.activity.getPageCurl()
                    .getChannelChangeHandler().getActiveContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvGenre.setText(R.string.epg_genre);
        if (isFromEPG) {
            eventName.setText(event.getName());
        } else {
            buttonNowNext.setText(R.string.next);
            if (activeContent != null) {
                eventName.setText(activeContent.getName() + " - "
                        + event.getName());
            }
        }
        eventTime.setText(DateTimeConversions.getDateTimeSting(event
                .getStartTime().getCalendar().getTime())
                + " - "
                + DateTimeConversions.getDateTimeSting(event.getEndTime()
                        .getCalendar().getTime()));
        try {
            extendedDescription = MainActivity.service.getEpgControl()
                    .getEventExtendedDescription(0, event.getEventId(),
                            activeContent.getIndex());
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (extendedDescription.length() == 0) {
            eventDescription.setText(event.getDescription());
        } else {
            eventDescription.setText(extendedDescription);
        }
        for (int i = 0; i < event.getNumberOfComponents(); i++) {
            components.append(String.format(
                    "%d. %s %s",
                    i + 1,
                    getComponentDescription(event.getComponentType(i)
                            .getStreamContent(), event.getComponentType(i)
                            .getComponenetType(), event.getComponentType(i)
                            .getLanguageCode()), "\n"));
        }
        eventComponents.setText(components);
        eventGenre.setText(convertGenreToString(event.getGenre()));
        if (event.getGenre() == 1) {
            buttonIMDBInfo.setVisibility(View.VISIBLE);
            buttonIMDBInfo.setOnClickListener(new IMDBButtonClickListener(event
                    .getName()));
        } else {
            buttonIMDBInfo.setVisibility(View.INVISIBLE);
        }
        Log.d(TAG, "PARENTAL RATE: " + event.getParentalRate());
        if (event.getParentalRate() > 0) {
            textViewParentalRate.setVisibility(View.VISIBLE);
            textViewParentalRate
                    .setText(String.valueOf(event.getParentalRate()));
        } else {
            textViewParentalRate.setVisibility(View.INVISIBLE);
        }
        /** Check if event is running */
        Date dateFromStream = null;
        /** Check time from stream */
        try {
            dateFromStream = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().getTimeDate().getCalendar()
                    .getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date start = null;// , end = null;
        start = event.getStartTime().getCalendar().getTime();
        // if it is running event hide buttons for reminder and PVR
        if (dateFromStream != null && start != null) {
            if (isFromEPG) {
                buttonNowNext.setVisibility(View.GONE);
                buttonReminder.setVisibility(View.VISIBLE);
                // Check config file
                if (!ConfigHandler.PVR) {
                    // Hide schedule record button
                    buttonScheduleRecord.setVisibility(View.INVISIBLE);
                } else {
                    buttonScheduleRecord.setVisibility(View.VISIBLE);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Check config file
                        if (!ConfigHandler.PVR) {
                            buttonReminder.requestFocus();
                        } else {
                            buttonScheduleRecord.requestFocus();
                        }
                    }
                }, 150);
            } else {
                buttonNowNext.setVisibility(View.VISIBLE);
                buttonReminder.setVisibility(View.GONE);
                buttonScheduleRecord.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonNowNext.requestFocus();
                    }
                }, 150);
            }
        } else {
            if (!isFromEPG) {
                try {
                    activeContent = MainActivity.activity.getPageCurl()
                            .getChannelChangeHandler().getActiveContent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (activeContent != null) {
                    eventName.setText(activeContent.getName()
                            + " - "
                            + ctx.getResources()
                                    .getString(R.string.epg_no_data));
                    eventTime.setText("");
                    tvGenre.setText("");
                }
                buttonNowNext.setVisibility(View.VISIBLE);
                buttonReminder.setVisibility(View.GONE);
                buttonScheduleRecord.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonNowNext.requestFocus();
                    }
                }, 150);
            }
        }
        super.show();
    }

    public void setUpNewExtendedInfo(int serviceIndex) {
        StringBuffer components = new StringBuffer();
        components.append(ctx.getResources().getString(
                R.string.component_type_components)
                + "\n");
        String extendedDescription = "";
        try {
            buttonNowNext.setText(R.string.next);
            activeContent = MainActivity.activity.getPageCurl()
                    .getChannelChangeHandler()
                    .getContentExtendedInfoByIndex(serviceIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.event = MainActivity.service.getEpgControl()
                    .getPresentFollowingEvent(0, serviceIndex,
                            EpgEventType.PRESENT_EVENT);
            this.eventNext = MainActivity.service.getEpgControl()
                    .getPresentFollowingEvent(0, serviceIndex,
                            EpgEventType.FOLLOWING_EVENT);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (buttonNowNext.getText().toString()
                .equals(ctx.getResources().getString(R.string.now))) {
            event = eventNext;
        }
        tvGenre.setText(R.string.epg_genre);
        try {
            extendedDescription = MainActivity.service.getEpgControl()
                    .getEventExtendedDescription(0, event.getEventId(),
                            serviceIndex);
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (activeContent != null) {
            eventName
                    .setText(activeContent.getName() + " - " + event.getName());
        }
        eventTime.setText(DateTimeConversions.getDateTimeSting(event
                .getStartTime().getCalendar().getTime())
                + " - "
                + DateTimeConversions.getDateTimeSting(event.getEndTime()
                        .getCalendar().getTime()));
        if (extendedDescription.length() == 0) {
            eventDescription.setText("");
        } else {
            eventDescription.setText(extendedDescription);
        }
        for (int i = 0; i < event.getNumberOfComponents(); i++) {
            components.append(String.format(
                    "%d. %s %s",
                    i + 1,
                    getComponentDescription(event.getComponentType(i)
                            .getStreamContent(), event.getComponentType(i)
                            .getComponenetType(), event.getComponentType(i)
                            .getLanguageCode()), "\n"));
        }
        eventComponents.setText(components);
        eventGenre.setText(convertGenreToString(event.getGenre()));
        if (event.getGenre() == 1) {
            buttonIMDBInfo.setVisibility(View.VISIBLE);
            buttonIMDBInfo.setOnClickListener(new IMDBButtonClickListener(event
                    .getName()));
        } else {
            buttonIMDBInfo.setVisibility(View.INVISIBLE);
        }
        if (event.getParentalRate() > 0) {
            textViewParentalRate.setVisibility(View.VISIBLE);
            textViewParentalRate
                    .setText(String.valueOf(event.getParentalRate()));
        } else {
            textViewParentalRate.setVisibility(View.INVISIBLE);
        }
        /** Check if event is running */
        Date dateFromStream = null;
        /** Check time from stream */
        try {
            dateFromStream = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().getTimeDate().getCalendar()
                    .getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date start = null;
        start = event.getStartTime().getCalendar().getTime();
        // if it is running event hide buttons for reminder and PVR
        if (dateFromStream != null && start != null) {
            buttonNowNext.setVisibility(View.VISIBLE);
            buttonReminder.setVisibility(View.GONE);
            buttonScheduleRecord.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttonNowNext.requestFocus();
                }
            }, 150);
        } else {
            if (activeContent != null) {
                eventName.setText(activeContent.getName() + " - "
                        + ctx.getResources().getString(R.string.epg_no_data));
                eventTime.setText("");
                tvGenre.setText("");
            }
            buttonNowNext.setVisibility(View.VISIBLE);
            buttonReminder.setVisibility(View.GONE);
            buttonScheduleRecord.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    buttonNowNext.requestFocus();
                }
            }, 150);
        }
        super.show();
    }

    private static String toASCII(int value) {
        int length = 3;
        StringBuilder builder = new StringBuilder(length);
        for (int i = length - 1; i >= 0; i--) {
            builder.append((char) ((value >> (8 * i)) & 0xFF));
        }
        return builder.toString();
    }

    /** Take reference of important views */
    private void init() {
        eventName = (A4TVTextView) findViewById(R.id.aTVTextViewEventName);
        eventTime = (A4TVTextView) findViewById(R.id.aTVTextViewEventTime);
        eventDescription = (A4TVTextView) findViewById(R.id.aTVTextViewEventDescription);
        eventGenre = (A4TVTextView) findViewById(R.id.aTVTextViewGenre);
        tvGenre = (A4TVTextView) findViewById(R.id.aTVTextViewGenreText);
        textViewParentalRate = (TextView) findViewById(R.id.textViewParentalRate);
        eventComponents = (A4TVTextView) findViewById(R.id.aTVTextViewEventComponents);
        eventComponents.setSingleLine(false);
        buttonScheduleRecord = (A4TVButton) findViewById(R.id.aTVButtonEpgScheduleRecording);
        buttonScheduleRecord.setOnClickListener(new ButtonScheduleListener());
        // Check config file
        if (!ConfigHandler.PVR) {
            // Hide schedule record button
            buttonScheduleRecord.setVisibility(View.INVISIBLE);
        }
        // bug fix for event name in two rows
        eventName.setSingleLine(true);
        eventName.setSelected(true);
        buttonIMDBInfo = (A4TVButton) findViewById(R.id.aTVButtonEPGIMDBInfo);
        buttonIMDBInfo.setVisibility(View.INVISIBLE);
        buttonReminder = (A4TVButton) findViewById(R.id.aTVButtonEpgReminder);
        buttonReminder
                .setOnClickListener(new ScheduleReminderOnCLickListener());
        buttonNowNext = (A4TVButton) findViewById(R.id.aTVButtonNowNext);
        buttonNowNext.setOnClickListener(new NowNextClickListener());
        handlerForCallback = new Handler() {
            // TODO: Applies on main display only
            private final int mDisplayId = 0;
            ReminderSmartInfo smartInfo = null;
            ReminderTimerInfo timerInfo = null;
            ReminderType type = ReminderType.REMINDER_TIMER;
            String title;
            int serviceIndex = 0;
            TimeDate time;

            @Override
            public void handleMessage(Message msg) {
                final int index = msg.what;
                boolean startStopReminder = (Boolean) msg.obj;
                IReminderControl reminderControl = null;
                IContentListControl contentListControl = null;
                try {
                    contentListControl = MainActivity.service
                            .getContentListControl();
                    reminderControl = MainActivity.service.getReminderControl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (reminderControl != null) {
                    try {
                        type = reminderControl.getType(index);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    timerInfo = null;
                    smartInfo = null;
                    if (type == ReminderType.REMINDER_TIMER) {
                        try {
                            timerInfo = reminderControl.getTimerInfo(index);
                            title = timerInfo.getTitle();
                            serviceIndex = timerInfo.getServiceIndex();
                            time = timerInfo.getTime();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            smartInfo = reminderControl.getSmartInfo(index);
                            title = smartInfo.getTitle();
                            serviceIndex = smartInfo.getServiceIndex();
                            time = smartInfo.getTime();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (timerInfo != null || smartInfo != null) {
                        // check if reminder is for current channel
                        if (contentListControl != null) {
                            Content content = null;
                            try {
                                content = contentListControl
                                        .getActiveContent(mDisplayId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (content != null) {
                                if (content.getIndexInMasterList() == serviceIndex) {
                                    try {
                                        reminderControl.destroy(index);
                                        return;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Log.d(TAG,
                                        "%%%%%%%%%%%%%% REMINDER CALLBACK ACTIVE CONTENT IS NULL %%%%%%%%%%%%%%%%%");
                            }
                        }
                        alertDialogCallBack = new A4TVAlertDialog(ctx);
                        alertDialogCallBack.setCancelable(true);
                        // ////////////////////////////////////////
                        // Title
                        // ////////////////////////////////////////
                        if (contentListControl != null) {
                            if (startStopReminder) {
                                try {
                                    alertDialogCallBack
                                            .setTitleOfAlertDialog(ctx
                                                    .getResources()
                                                    .getString(
                                                            R.string.epg_scheduled_reminder)
                                                    + ": "
                                                    + contentListControl
                                                            .getServiceByIndexInMasterList(
                                                                    serviceIndex,
                                                                    false)
                                                            .getName()
                                                    + " - "
                                                    + title);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                            // /////////////////////////////////
                            // REMINDER PASSED
                            // //////////////////////////////////
                            else {
                                try {
                                    alertDialogCallBack
                                            .setTitleOfAlertDialog(ctx
                                                    .getResources()
                                                    .getString(
                                                            R.string.epg_scheduled_reminder)
                                                    + ": "
                                                    + contentListControl
                                                            .getServiceByIndexInMasterList(
                                                                    serviceIndex,
                                                                    false)
                                                            .getName()
                                                    + " - "
                                                    + title
                                                    + "  "
                                                    + ctx.getResources()
                                                            .getString(
                                                                    R.string.epg_scheduled_reminder_passed));
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        } else {
                            if (startStopReminder) {
                                alertDialogCallBack
                                        .setTitleOfAlertDialog(ctx
                                                .getResources()
                                                .getString(
                                                        R.string.epg_scheduled_reminder)
                                                + ": " + title);
                            }
                            // /////////////////////////////////
                            // REMINDER PASSED
                            // //////////////////////////////////
                            else {
                                alertDialogCallBack
                                        .setTitleOfAlertDialog(ctx
                                                .getResources()
                                                .getString(
                                                        R.string.epg_scheduled_reminder)
                                                + ": "
                                                + title
                                                + "  "
                                                + ctx.getResources()
                                                        .getString(
                                                                R.string.epg_scheduled_reminder_passed));
                            }
                        }
                        // ////////////////////////////////////////
                        // Message
                        // ////////////////////////////////////////
                        alertDialogCallBack.setMessage(ctx.getResources()
                                .getString(R.string.duration)
                                + "  "
                                + DateTimeConversions.getTimeSting(time
                                        .getCalendar().getTime()));
                        alertDialogCallBack
                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        try {
                                            MainActivity.service
                                                    .getReminderControl()
                                                    .destroy(index);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        alertDialogCallBack.setPositiveButton(
                                R.string.parental_control_ok,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialogCallBack.cancel();
                                    }
                                });
                        if (startStopReminder) {
                            alertDialogCallBack.setNegativeButton(
                                    R.string.epg_reminder_go_to_channel,
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Content content = null;
                                            Log.d(TAG,
                                                    "INDEX OF SERVICE FROM REMINDER: "
                                                            + (serviceIndex));
                                            try {
                                                content = MainActivity.service
                                                        .getContentListControl()
                                                        .getContentByIndexInMasterList(
                                                                serviceIndex);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                            if (content != null) {
                                                MainActivity.activity
                                                        .getPageCurl()
                                                        .changeChannelByContent(
                                                                content,
                                                                mDisplayId);
                                            }
                                            alertDialogCallBack.cancel();
                                        }
                                    });
                        }
                        alertDialogCallBack.show();
                    }
                }
                super.handleMessage(msg);
            }
        };
    }

    private class NowNextClickListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            StringBuffer components = new StringBuffer();
            String extendedDescription = "";
            components.append(ctx.getResources().getString(
                    R.string.component_type_components)
                    + "\n");
            tvGenre.setText(R.string.epg_genre);
            // show next
            if (buttonNowNext.getText().toString()
                    .equals(ctx.getResources().getString(R.string.next))) {
                boolean isInfoExist = false;
                try {
                    eventName.setText("");
                    if (activeContent != null) {
                        String str;
                        if (eventNext.getName().length() == 0) {
                            str = ctx.getResources().getString(
                                    R.string.epg_no_data);
                            isInfoExist = false;
                        } else {
                            str = eventNext.getName();
                            isInfoExist = true;
                        }
                        eventName
                                .setText(activeContent.getName() + " - " + str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isInfoExist) {
                    eventTime.setText(DateTimeConversions
                            .getDateTimeSting(eventNext.getStartTime()
                                    .getCalendar().getTime())
                            + " - "
                            + DateTimeConversions.getDateTimeSting(eventNext
                                    .getEndTime().getCalendar().getTime()));
                } else {
                    eventTime.setText("");
                }
                try {
                    extendedDescription = MainActivity.service.getEpgControl()
                            .getEventExtendedDescription(0, event.getEventId(),
                                    activeContent.getIndex());
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (extendedDescription.length() == 0) {
                    eventDescription.setText(eventNext.getDescription());
                } else {
                    eventDescription.setText(extendedDescription);
                }
                for (int i = 0; i < eventNext.getNumberOfComponents(); i++) {
                    components.append(String.format(
                            "%d. %s %s",
                            i + 1,
                            getComponentDescription(
                                    eventNext.getComponentType(i)
                                            .getStreamContent(), eventNext
                                            .getComponentType(i)
                                            .getComponenetType(), eventNext
                                            .getComponentType(i)
                                            .getLanguageCode()), "\n"));
                }
                eventComponents.setText(components);
                if (isInfoExist) {
                    eventGenre.setText(convertGenreToString(event.getGenre()));
                } else {
                    eventGenre.setText("");
                    tvGenre.setText("");
                }
                if (eventNext.getGenre() == 1) {
                    buttonIMDBInfo.setVisibility(View.VISIBLE);
                    buttonIMDBInfo
                            .setOnClickListener(new IMDBButtonClickListener(
                                    eventNext.getName()));
                } else {
                    buttonIMDBInfo.setVisibility(View.INVISIBLE);
                }
                if (eventNext.getParentalRate() > 0) {
                    textViewParentalRate.setVisibility(View.VISIBLE);
                    textViewParentalRate.setText(String.valueOf(eventNext
                            .getParentalRate()));
                } else {
                    textViewParentalRate.setVisibility(View.INVISIBLE);
                }
                buttonNowNext.setText(R.string.now);
            }
            // show now
            else {
                boolean isInfoExist = false;
                try {
                    eventName.setText("");
                    if (activeContent != null) {
                        String str;
                        if (event.getName().length() == 0) {
                            str = ctx.getResources().getString(
                                    R.string.epg_no_data);
                            isInfoExist = false;
                        } else {
                            str = event.getName();
                            isInfoExist = true;
                        }
                        eventName
                                .setText(activeContent.getName() + " - " + str);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isInfoExist) {
                    eventTime.setText(DateTimeConversions
                            .getDateTimeSting(eventNext.getStartTime()
                                    .getCalendar().getTime())
                            + " - "
                            + DateTimeConversions.getDateTimeSting(eventNext
                                    .getEndTime().getCalendar().getTime()));
                } else {
                    eventTime.setText("");
                }
                try {
                    extendedDescription = MainActivity.service.getEpgControl()
                            .getEventExtendedDescription(0, event.getEventId(),
                                    activeContent.getIndex());
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (extendedDescription.length() == 0) {
                    eventDescription.setText("");
                } else {
                    eventDescription.setText(extendedDescription);
                }
                for (int i = 0; i < event.getNumberOfComponents(); i++) {
                    components.append(String
                            .format("%d. %s %s",
                                    i + 1,
                                    getComponentDescription(event
                                            .getComponentType(i)
                                            .getStreamContent(), event
                                            .getComponentType(i)
                                            .getComponenetType(), event
                                            .getComponentType(i)
                                            .getLanguageCode()), "\n"));
                }
                eventComponents.setText(components);
                if (isInfoExist) {
                    eventGenre.setText(convertGenreToString(event.getGenre()));
                } else {
                    eventGenre.setText("");
                    tvGenre.setText("");
                }
                if (event.getGenre() == 1) {
                    buttonIMDBInfo.setVisibility(View.VISIBLE);
                    buttonIMDBInfo
                            .setOnClickListener(new IMDBButtonClickListener(
                                    event.getName()));
                } else {
                    buttonIMDBInfo.setVisibility(View.INVISIBLE);
                }
                if (event.getParentalRate() > 0) {
                    textViewParentalRate.setVisibility(View.VISIBLE);
                    textViewParentalRate.setText(String.valueOf(event
                            .getParentalRate()));
                } else {
                    textViewParentalRate.setVisibility(View.INVISIBLE);
                }
                buttonNowNext.setText(R.string.next);
            }
        }
    }

    private class IMDBButtonClickListener implements
            android.view.View.OnClickListener {
        private String movieName;

        public IMDBButtonClickListener(String movieName) {
            this.movieName = movieName;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            StringBuilder builder = new StringBuilder();
            intent.setData(Uri.parse(builder
                    .append("http://www.imdb.com/find?q=").append(movieName)
                    .toString()));
            EPGScheduleDialog.this.cancel();
            EPGDialog epgDialog = MainActivity.activity.getDialogManager()
                    .getEpgDialog();
            if (epgDialog != null) {
                epgDialog.cancel();
            }
            try {
                ctx.startActivity(intent);
                MainActivity.activity.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ButtonScheduleListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            showAlertDialogForConfirmation(false);
        }
    }

    /** OnClick listener for schedule reminder button */
    private class ScheduleReminderOnCLickListener implements
            android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            showAlertDialogForConfirmation(true);
        }
    }

    private void showAlertDialogForConfirmation(final boolean isForReminder) {
        alertDialog = new A4TVAlertDialog(ctx);
        alertDialog.setNegativeButton(R.string.button_text_no,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
        // //////////////////////////////////////////
        // For reminder
        // //////////////////////////////////////////
        if (isForReminder) {
            alertDialog.setTitleOfAlertDialog(R.string.reminder_message_title);
            alertDialog.setPositiveButton(R.string.button_text_yes,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int isAdded = 0;
                            Log.d(TAG,
                                    "SERVICE INDEX: " + event.getServiceIndex());
                            try {
                                Log.d(TAG,
                                        "Number of components: "
                                                + event.getNumberOfComponents());
                                ReminderSmartParam param = new ReminderSmartParam(
                                        event.getName(),
                                        event.getDescription(), event
                                                .getServiceIndex(), 0, event
                                                .getEventId(), event
                                                .getStartTime());
                                MainActivity.service.getReminderControl()
                                        .createSmart(param);
                                Log.d(TAG,
                                        "EPG EVENT WITH ID ADDED TO REMINDER: "
                                                + event.getEventId()
                                                + " ERROR CODE: " + isAdded
                                                + " DATE "
                                                + event.getStartTime() + " "
                                                + param.getTime());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // TODO: catch exception
                            // if (isAdded ==
                            // ReminderErrorCode.REMINDER_LIST_FULL
                            // .ordinal()) {
                            // A4TVToast toast = new A4TVToast(ctx);
                            // toast.showToast(R.string.epg_reminder_error_list_full);
                            // } else if (isAdded ==
                            // ReminderErrorCode.CONFLICT_FOUND
                            // .ordinal()) {
                            // A4TVToast toast = new A4TVToast(ctx);
                            // toast.showToast(R.string.epg_reminder_error_exist);
                            // } else if (isAdded == ReminderErrorCode.NO_ERROR
                            // .ordinal()) {
                            // A4TVToast toast = new A4TVToast(ctx);
                            // toast.showToast(R.string.epg_reminder_added);
                            // } else {
                            // A4TVToast toast = new A4TVToast(ctx);
                            // toast.showToast(R.string.reminder_error);
                            // }
                            alertDialog.cancel();
                        }
                    });
        }
        // ///////////////////////////////////////
        // For smart recording
        // ///////////////////////////////////////
        else {
            alertDialog
                    .setTitleOfAlertDialog(R.string.smart_record_message_title);
            alertDialog.setPositiveButton(R.string.button_text_yes,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            A4TVToast toast = new A4TVToast(getContext());
                            // int isAdded =
                            // PvrErrorCode.PVR_NO_ERROR.ordinal();
                            try {
                                // Schedule event recording
                                SmartCreateParams smartCreateParams = new SmartCreateParams(
                                        event.getServiceIndex(), event
                                                .getEventId(), event.getName(),
                                        event.getDescription(), event
                                                .getStartTime(), event
                                                .getEndTime());
                                MainActivity.service.getPvrControl()
                                        .createSmartRecord(smartCreateParams);
                                // stanislava
                                // if(!isAdded) {
                                // toast.showToast(R.string.smart_record_error);
                                // }
                            } catch (Exception e) {
                                toast.showToast(R.string.smart_record_error);
                                e.printStackTrace();
                            }
                            // if (isAdded ==
                            // PvrErrorCode.PVR_ERROR_RECORD_RESCHEDULE
                            // .ordinal()) {
                            //
                            // toast.showToast(R.string.epg_smart_record_error_reschedule);
                            //
                            // } else if (isAdded ==
                            // PvrErrorCode.PVR_ERROR_RECORD_CONFLICT_FOUND
                            // .ordinal()) {
                            //
                            // toast.showToast(R.string.epg_smart_record_error_conflit_found);
                            //
                            // } else if (isAdded == PvrErrorCode.PVR_NO_ERROR
                            // .ordinal()) {
                            //
                            // toast.showToast(R.string.epg_smart_record_added);
                            //
                            // } else {
                            //
                            // toast.showToast(R.string.smart_record_error);
                            // }
                            alertDialog.cancel();
                        }
                    });
        }
        alertDialog.show();
    }

    public void showDialogFromCallBack(int reminderHandle,
            boolean startStopReminder) {
        Log.d(TAG, "REMINDER CALL BACK ENTERED");
        handlerForCallback.sendMessage(Message.obtain(handlerForCallback,
                reminderHandle, startStopReminder));
    }

    // this is not needed here
    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }

    private String getComponentDescription(int streamContent,
            int componentType, int languageInt) {
        String componentDescriptor = ctx.getResources().getString(
                R.string.component_type_unknown);
        String language = "";
        try {
            language = MainActivity.service.getSystemControl()
                    .getLanguageAndKeyboardControl()
                    .convertTrigramsToLanguage(toASCII(languageInt), false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        switch (streamContent) {
            case 0x01: {
                switch (componentType) {
                    case 0x01: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_4_3_aspect_ratio_25hz);
                        break;
                    }
                    case 0x02: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_16_9_aspect_ratio_with_pan_25hz);
                        break;
                    }
                    case 0x03: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_16_9_aspect_ratio_without_pan_25hz);
                        break;
                    }
                    case 0x04: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_16_9_aspect_ratio_25hz);
                        break;
                    }
                    case 0x05: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_4_3_aspect_ratio_30hz);
                        break;
                    }
                    case 0x06: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_16_9_aspect_ratio_with_pan_30hz);
                        break;
                    }
                    case 0x07: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_16_9_aspect_ratio_without_pan_30hz);
                        break;
                    }
                    case 0x08: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_video_16_9_aspect_ratio_30hz);
                        break;
                    }
                    case 0x09: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_4_3_aspect_ratio_25hz);
                        break;
                    }
                    case 0x0A: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_16_9_aspect_ratio_with_pan_25hz);
                        break;
                    }
                    case 0x0B: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_16_9_aspect_ratio_without_pan_25hz);
                        break;
                    }
                    case 0x0C: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_16_9_aspect_ratio_25hz);
                        break;
                    }
                    case 0x0D: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_4_3_aspect_ratio_30hz);
                        break;
                    }
                    case 0x0E: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_16_9_aspect_ratio_with_pan_30hz);
                        break;
                    }
                    case 0x0F: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_16_9_aspect_ratio_without_pan_30hz);
                        break;
                    }
                    case 0x10: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg2_hd_video_16_9_aspect_ratio_30hz);
                        break;
                    }
                    default:
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_unknown);
                        break;
                }
                break;
            }
            case 0x02: {
                switch (componentType) {
                    case 0x01: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_single_mono);
                        break;
                    }
                    case 0x02: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_dual_mono);
                        break;
                    }
                    case 0x03: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_stereo);
                        break;
                    }
                    case 0x04: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_multi);
                        break;
                    }
                    case 0x05: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_surround);
                        break;
                    }
                    case 0x40: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_visually_impaired);
                        break;
                    }
                    case 0x41: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_hard_hearing);
                        break;
                    }
                    case 0x42: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_receiver_mixed_supplementary_audio);
                        break;
                    }
                    case 0x47: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_receiver_mix);
                        break;
                    }
                    case 0x48: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_mpeg1_layer2_audio_broadcater_mix);
                        break;
                    }
                    default:
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_unknown);
                        break;
                }
                break;
            }
            case 0x03: {
                switch (componentType) {
                    case 0x01: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_ebu_teletext_subtitles);
                        break;
                    }
                    case 0x02: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_associated_ebu_teletext);
                        break;
                    }
                    case 0x03: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_vbi_data);
                        break;
                    }
                    case 0x10: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_normal_no_monitor_aspect_ratio);
                        break;
                    }
                    case 0x11: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_normal_4_3_aspect_ratio_monitor);
                        break;
                    }
                    case 0x12: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_normal_16_9_aspect_ratio_monitor);
                        break;
                    }
                    case 0x13: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_normal_2_21_1_3_aspect_ratio_monitor);
                        break;
                    }
                    case 0x14: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_normal_hd_monitor);
                        break;
                    }
                    case 0x20: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_hard_of_hearing_no_monitor_aspect_ratio);
                        break;
                    }
                    case 0x21: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_hard_of_hearing_4_3_aspect_ratio_monitor);
                        break;
                    }
                    case 0x22: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_hard_of_hearing_16_9_aspect_ratio_monitor);
                        break;
                    }
                    case 0x23: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_hard_of_hearing_2_21_1_3_aspect_ratio_monitor);
                        break;
                    }
                    case 0x24: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_dvb_subtitles_hard_of_hearing_hd_monitor);
                        break;
                    }
                    case 0x30: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_open_sign_language);
                        break;
                    }
                    case 0x31: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_closed_sign_language);
                        break;
                    }
                    case 0x40: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_video_up_sampled);
                        break;
                    }
                    default:
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_unknown);
                        break;
                }
                break;
            }
            case 0x04: {
                if (componentType > 0x00 && componentType < 0x7F) {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_ac3_audio_mode);
                } else if (componentType > 0x80 && componentType < 0xFF) {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_enhanced_ac3_audio_mode);
                } else {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_unknown);
                }
                break;
            }
            case 0x05: {
                switch (componentType) {
                    case 0x01: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_sd_video_4_3_aspect_ratio_25hz);
                        break;
                    }
                    case 0x03: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_sd_video_16_9_aspect_ratio_25hz);
                        break;
                    }
                    case 0x04: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_sd_video_aspect_ratio_25hz);
                        break;
                    }
                    case 0x05: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_sd_video_4_3_aspect_ratio_30hz);
                        break;
                    }
                    case 0x07: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_sd_video_16_9_aspect_ratio_30hz);
                        break;
                    }
                    case 0x08: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_sd_video_aspect_ratio_30hz);
                        break;
                    }
                    case 0x0B: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_hd_video_16_9_aspect_ratio_25hz);
                        break;
                    }
                    case 0x0C: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_hd_video_aspect_ratio_25hz);
                        break;
                    }
                    case 0x0F: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_hd_video_16_9_aspect_ratio_30hz);
                        break;
                    }
                    case 0x10: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_h264_avc_hd_video_aspect_ratio_30hz);
                        break;
                    }
                    default:
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_unknown);
                        break;
                }
                break;
            }
            case 0x06: {
                switch (componentType) {
                    case 0x01: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_single_mono);
                        break;
                    }
                    case 0x03: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_stereo);
                        break;
                    }
                    case 0x05: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_surround_sound);
                        break;
                    }
                    case 0x40: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_he_aac_visually_impaired);
                        break;
                    }
                    case 0x41: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_hard_of_hearing);
                        break;
                    }
                    case 0x42: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_he_aac_supplementary_audio);
                        break;
                    }
                    case 0x43: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_v2_stereo);
                        break;
                    }
                    case 0x44: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_he_aac_v2_visually_impaired);
                        break;
                    }
                    case 0x45: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_he_aac_v2_hard_of_hearing);
                        break;
                    }
                    case 0x46: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_he_aac_v2_supplementary_audio);
                        break;
                    }
                    case 0x47: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_receiver_mix);
                        break;
                    }
                    case 0x48: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_broadcaster_mix);
                        break;
                    }
                    case 0x49: {
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_he_aac_v2_receiver_mix);
                        break;
                    }
                    case 0x4A: {
                        componentDescriptor = ctx
                                .getResources()
                                .getString(
                                        R.string.component_type_he_aac_v2_broadcaster_mix);
                        break;
                    }
                    default:
                        componentDescriptor = ctx.getResources().getString(
                                R.string.component_type_unknown);
                        break;
                }
                break;
            }
            case 0x07: {
                if (componentType > 0x00 && componentType < 0x7F) {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_dts_audio_modes);
                } else {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_unknown);
                }
                break;
            }
            case 0x08: {
                if (componentType == 0x01) {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_dvb_srm_data);
                } else if (componentType > 0x02 && componentType < 0xFF) {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_dvb_cpcm_modes);
                } else {
                    componentDescriptor = ctx.getResources().getString(
                            R.string.component_type_unknown);
                }
                break;
            }
        }
        return String.format("%s     %s %s", componentDescriptor, ctx
                .getResources().getString(R.string.component_type_language),
                language);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_INFO: {
                Log.i(TAG, "Do nothing while EPG Extended dialog is active!");
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String convertGenreToString(int genre) {
        String genreString = "";
        switch (genre) {
            case 0x1:
                genreString = ctx.getResources().getString(
                        R.string.genre_movie_drama);
                break;
            case 0x2:
                genreString = ctx.getResources().getString(
                        R.string.genre_news_current_affairs);
                break;
            case 0x3:
                genreString = ctx.getResources().getString(
                        R.string.genre_show_game_show);
                break;
            case 0x4:
                genreString = ctx.getResources().getString(
                        R.string.genre_sports);
                break;
            case 0x5:
                genreString = ctx.getResources().getString(
                        R.string.genre_children_youth_programmes);
                break;
            case 0x6:
                genreString = ctx.getResources().getString(
                        R.string.genre_music_ballet_dance);
                break;
            case 0x7:
                genreString = ctx.getResources().getString(
                        R.string.genre_arts_culture);
                break;
            case 0x8:
                genreString = ctx.getResources().getString(
                        R.string.genre_social_political_issues);
                break;
            case 0x9:
                genreString = ctx.getResources().getString(
                        R.string.genre_education_science);
                break;
            case 0xA:
                genreString = ctx.getResources().getString(
                        R.string.genre_leisure_hobbies);
                break;
            default:
                genreString = "";
                break;
        }
        return genreString;
    }
}
