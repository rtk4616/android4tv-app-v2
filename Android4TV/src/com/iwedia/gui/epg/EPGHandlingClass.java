package com.iwedia.gui.epg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwedia.comm.IEpgControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.comm.images.ImageManager;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.epg.EpgServiceFilter;
import com.iwedia.dtv.epg.EpgTimeFilter;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.EPGDialog;
import com.iwedia.gui.components.dialogs.EpgReminderDialog;
import com.iwedia.gui.components.dialogs.EpgScheduleRecordingDialog;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.util.DateTimeConversions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Main EPG class
 * 
 * @author Branimir Pavlovic
 */
public class EPGHandlingClass {
    public final String TAG = "EPGHandlingClass";
    private MainActivity activity;
    /** EPG dialog */
    private EPGDialog dialogEPG;
    /** EPG gallery */
    private EPGDownGallery galleryEPG;
    private EPGGalleryAdapter galleryAdapter;
    /** EPG Scroll view */
    public EPGScrollView scrollView;
    private int numberOfServices;
    /** time linear layout */
    private LinearLayout layoutForTime, layoutForTimeAndEvents;
    /** List views for EPG contents */
    private ArrayList<LinearLayout> linearLayoutsEPGContents = new ArrayList<LinearLayout>();
    /** Main layout that holds EPG data */
    private LinearLayout mainLayout;
    /** Width of EPG window for data representing */
    private int epgInformationsScreenWidth;
    // layout inflater
    private LayoutInflater inflater;
    // handler to handle thread messages
    private Handler handlerGUI;
    // Thread fields
    private Runnable runnableThreadRight, runnableThreadLeft,
            runnableThreadInitial, runnableAdderThread;
    // runnableThreadBackgroundCheck;
    private Thread backgroundThread;
    /** Important constants */
    // to calculate gallery item width
    public final int GALLERY_ITEM_DIVIDER = 6, GALLERY_SPACING = 0,
            NUMBER_OF_LAYOUTS = 5;
    // Handler constants
    private final int STOP_THREAD = 0, ADD_LAST_ITEM = 2, ADD_ONE_EVENT = 3,
            ADD_FIRST_ITEM = 4, ADD_SIDE_TIME_LAYOUT = 5, SET_DATE = 6,
            ADD_ONE_LAYOUT_FROM_INITIAL = 7, STOP_THREAD_ADDER = 8,
            CHANGE_SERVICE = 9, CHANGE_GENRE = 10, CANCEL_DIALOG = 11;
    public final int CENTRAL_COLUMN_INDEX = 2;
    private final long NUMBER_OF_MILISECONDS_IN_DAY = 86400000;
    public final int NUMBER_OF_VISIBLE_SERVICES = 5,
            TIME_TO_OPEN_SMALL_EVENT = 1200,
            TIME_TO_OPEN_SMALL_EVENT_ANIMATION = 150;
    public int NUMBER_OF_HOURS_IN_WINDOW = 2;
    public int EPG_WINDOW_HEIGHT = 0, UPPER_WINDOW_HEIGHT = 0;
    public int epg_one_hour_height = 0;
    private final float EPG_EVENTS_SCALLER = 0.65f;
    private final int empty_layout_id = 0x33333,
            MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL = 5;
    /** Counter in initial thread */
    private int column;
    /** List of heights of EPG events */
    private ArrayList<ArrayList<EPGEventShowed>> epgEventsHeights = new ArrayList<ArrayList<EPGEventShowed>>();
    /** List that keep number of EPG events for each TV service */
    private ArrayList<Integer> epgEventsListCounts = new ArrayList<Integer>();
    /** String builder object */
    private StringBuilder stringBuilder;
    /** Initial day to be shown in EPG */
    public int dayInWeekToLoadData = 1;
    /** Progress dialog for background loading */
    private A4TVProgressDialog progressDialog;
    /** TextView that shows date of EPG events showing */
    private A4TVTextView textViewDayShowing;
    /** Current time from stream to be shown first in side time layout */
    private Date timeFromStreamDate;
    /** Last number to show in side time layout */
    private int endTimeInt;
    /** Flag for loading side time again or not */
    private boolean isChangedEndTime = false;
    /** Close animation of really small events */
    private Animation closeAnim;
    /** Layout that holds views for really small events */
    private FrameLayout frameLayoutEPGForSmallEvents;
    /** Zoom controls image views */
    private ImageView zoomIn, zoomOut;
    /** Flag for close animation of really small events */
    // private boolean isStarted = false;
    /** Filter options fields */
    /** Array list of filter buttons */
    private ArrayList<LinearLayout> filterButtons = new ArrayList<LinearLayout>();
    /** Available filter options */
    private Boolean[] filterOptions;
    /** Value of current selected filtering option in list */
    public static int currentSelectedFilterOption = 0;
    /** Width measure unit */
    private int widthMeasureUnit;
    /** Width measure unit divider */
    public static final int widthMeasureUnitDivider = 12;
    /** Loaded active Filter from content list */
    public int currentFilterFromContentList = -1;
    /** Thread for changing chosen EPG service */
    private Thread threadChangeService;
    private int newServiceIndex, newServiceCounter;
    private final int NUMBER_OF_MILISECONDS_FOR_SERVICECHANGE = 2000;
    private A4TVTextView textViewInfo;
    private Thread threadChangeEpgGenre;
    private int newGenreIndex, newGenreCounter;
    private EpgSmallScrollView contentFilterOptionsScroll;
    private IContentListControl contentListControl = null;
    protected EPGHandlingClass epgHandlingClass;
    private EpgTimeFilter epgTimeFilter;
    private TimeDate startTime, endTime;
    // TODO: Applies on main display only
    private int mDisplayId = 0;
    private static SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    private EpgServiceFilter epgServiceFilter;
    private int currentServiceIndex = -1;

    /** Default constructor */
    public EPGHandlingClass(MainActivity activity) {
        this.activity = activity;
        epgHandlingClass = this;
        epgTimeFilter = new EpgTimeFilter();
        initFilterOptionArray();
    }

    /** Initialization function */
    public void init() {
        /** Calculate height of important epg window parts */
        if (activity.isFullHD()) {
            // constants are weights from xml file
            double height = (24.5f * MainActivity.SCREEN_HEIGHT_1080P) / 28.5f;
            EPG_WINDOW_HEIGHT = (int) ((14.7 * height) / 18.7f);
            UPPER_WINDOW_HEIGHT = (int) ((2 * MainActivity.SCREEN_HEIGHT_1080P) / 28.5f);
        } else {
            double height = (24.5f * MainActivity.SCREEN_HEIGHT_720P) / 28.5f;
            EPG_WINDOW_HEIGHT = (int) ((14.7 * height) / 18.7f);
            UPPER_WINDOW_HEIGHT = (int) ((2 * MainActivity.SCREEN_HEIGHT_720P) / 28.5f);
        }
        // Log.d(TAG, "EPG_WINDOW_HEIGHT " + EPG_WINDOW_HEIGHT +
        // " window_height "
        // + MainActivity.screenHeight);
        stringBuilder = new StringBuilder();
        initHandlerAndRunnables();
        // calculate layout width like in epg_main.xml
        epgInformationsScreenWidth = 18 * MainActivity.screenWidth / 20;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // create dialog
        dialogEPG = activity.getDialogManager().getEpgDialog();
        if (dialogEPG != null) {
            dialogEPG.setEpgHandler(this);
            textViewDayShowing = (A4TVTextView) dialogEPG
                    .findViewById(R.id.aTVTextViewEPGDayShowing);
            // init scroll view
            scrollView = (EPGScrollView) dialogEPG
                    .findViewById(R.id.scrollViewMain);
            scrollView.setVisibleRectHeight(EPG_WINDOW_HEIGHT);
            scrollView.setEpgHandlingClass(this);
            // take references from dialog
            galleryEPG = (EPGDownGallery) dialogEPG
                    .findViewById(R.id.galleryEPG);
            textViewInfo = (A4TVTextView) dialogEPG
                    .findViewById(R.id.aTVTextViewMoreInfo);
            // galleryEPG.setLayoutParams(new LinearLayout.LayoutParams(
            // epgInformationsScreenWidth, LayoutParams.MATCH_PARENT));
            // set adapter to gallery
            galleryEPG.setAdapter(galleryAdapter = new EPGGalleryAdapter());
            galleryEPG.setSpacing(GALLERY_SPACING);
            // get main layout
            mainLayout = (LinearLayout) dialogEPG
                    .findViewById(R.id.EPGMainLayoutWithLayouts);
            // set listener to change views size
            galleryEPG.setOnItemSelectedListener(new EPGGallerySelectListener(
                    this));
            galleryEPG.setFocusable(false);
            galleryEPG.setEpgHandler(this);
            layoutForTimeAndEvents = (LinearLayout) dialogEPG
                    .findViewById(R.id.linearLayoutEPGScroll);
            // side time stripe
            layoutForTime = (LinearLayout) dialogEPG
                    .findViewById(R.id.linearLayoutEPGScrollTime);
            layoutForTime.removeAllViews();
            layoutForTime.setGravity(Gravity.RIGHT);
            frameLayoutEPGForSmallEvents = (FrameLayout) dialogEPG
                    .findViewById(R.id.frameLayoutEPGForSmallEvents);
            // init list of heights
            epgEventsHeights.add(new ArrayList<EPGEventShowed>());
            epgEventsHeights.add(new ArrayList<EPGEventShowed>());
            epgEventsHeights.add(new ArrayList<EPGEventShowed>());
            epgEventsHeights.add(new ArrayList<EPGEventShowed>());
            epgEventsHeights.add(new ArrayList<EPGEventShowed>());
            // init progress dialog
            progressDialog = new A4TVProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setTitleOfAlertDialog(R.string.loading_title);
            progressDialog.setMessage(activity.getResources().getString(
                    R.string.loading_message));
            ImageView horizLineDivider = (ImageView) dialogEPG
                    .findViewById(R.id.allItemSecondGridDivider);
            // init zoom controls
            zoomIn = (ImageView) dialogEPG
                    .findViewById(R.id.imageViewEPGScalePlus);
            zoomOut = (ImageView) dialogEPG
                    .findViewById(R.id.imageViewEPGScaleMinus);
            // Check resolution and add layout params
            // //////////////////////////
            // 720p
            // //////////////////////////
            if (!activity.isFullHD()) {
                FrameLayout.LayoutParams recentlyDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                recentlyDivParams.topMargin = 35;
                horizLineDivider.setLayoutParams(recentlyDivParams);
            }
            // ////////////////////////////////
            // 1080p
            // ////////////////////////////////
            if (activity.isFullHD()) {
                FrameLayout.LayoutParams recentlyDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                recentlyDivParams.topMargin = 50;
                horizLineDivider.setLayoutParams(recentlyDivParams);
            }
            // Calculate width unit for filter buttons
            widthMeasureUnit = MainActivity.screenWidth
                    / widthMeasureUnitDivider;
            // init filters
            loadFilterOptions();
            if (MainActivity.activity.isFullHD()) {
                A4TVTextView topBanner = (A4TVTextView) dialogEPG
                        .findViewById(R.id.contentListTopBannerText);
                topBanner
                        .setTextSize(activity
                                .getResources()
                                .getDimension(
                                        com.iwedia.gui.R.dimen.a4tvdialog_button_text_size_1080p));
            }
            // init end time
            endTimeInt = 0;
            // select all filter
            filterButtons.get(0).setSelected(true);
            A4TVTextView reminders = (A4TVTextView) dialogEPG
                    .findViewById(R.id.aTVTextViewReminders);
            reminders.setTextColor(activity.getResources().getColor(
                    R.color.green_normal_epg));
            A4TVTextView textViewEpgScheduleRecording = (A4TVTextView) dialogEPG
                    .findViewById(R.id.aTVTextViewEpgScheduleRecording);
            textViewEpgScheduleRecording.setTextColor(activity.getResources()
                    .getColor(R.color.yellow_normal_epg));
            contentFilterOptionsScroll = (EpgSmallScrollView) dialogEPG
                    .findViewById(R.id.contentFilterOptionsScroll);
            // disable scroll
            contentFilterOptionsScroll.setEnabled(false);
            contentFilterOptionsScroll
                    .setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            Log.d(TAG, "contentFilterOptionsScroll focus "
                                    + hasFocus);
                            if (hasFocus) {
                                mainLayout.requestFocus();
                            }
                        }
                    });
            contentFilterOptionsScroll.setEnabledScroll(false);
        }
    }

    /**
     * When user clicks numbers on remote show info for that service
     * 
     * @param keyCode
     *        of number from remote
     */
    public void showEPGToServiceByNumber(final int keyCode) {
        if (threadChangeService == null) {
            threadChangeService = new Thread(new Runnable() {
                @Override
                public void run() {
                    newServiceCounter = 0;
                    while (newServiceCounter < NUMBER_OF_MILISECONDS_FOR_SERVICECHANGE) {
                        try {
                            Thread.sleep(100);
                            newServiceCounter += 100;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // go to channel change
                    handlerGUI.sendMessage(Message.obtain(handlerGUI,
                            CHANGE_SERVICE, newServiceIndex));
                }
            });
            threadChangeService.start();
            newServiceIndex = 0;
        }
        newServiceCounter = 0;
        if (newServiceIndex > 999 && newServiceIndex < 10000) {
            newServiceIndex = generateChannelNumber(keyCode);
        } else {
            newServiceIndex *= 10;
            newServiceIndex += generateChannelNumber(keyCode);
        }
        Log.d(TAG, "new service index clicked: " + newServiceIndex);
        textViewInfo.setText("" + newServiceIndex);
    }

    private int generateChannelNumber(int keycode) {
        switch (keycode) {
            case KeyEvent.KEYCODE_0:
                return 0;
            case KeyEvent.KEYCODE_1:
                return 1;
            case KeyEvent.KEYCODE_2:
                return 2;
            case KeyEvent.KEYCODE_3:
                return 3;
            case KeyEvent.KEYCODE_4:
                return 4;
            case KeyEvent.KEYCODE_5:
                return 5;
            case KeyEvent.KEYCODE_6:
                return 6;
            case KeyEvent.KEYCODE_7:
                return 7;
            case KeyEvent.KEYCODE_8:
                return 8;
            case KeyEvent.KEYCODE_9:
                return 9;
            default:
                return 0;
        }
    }

    /** Default values of filter option array */
    private void initFilterOptionArray() {
        // number of EPG genres is 11
        int x = 11;
        filterOptions = new Boolean[x];
        for (int i = 0; i < filterOptions.length; i++) {
            filterOptions[i] = true;
        }
    }

    /** Load filter options layout */
    private void loadFilterOptions() {
        LinearLayout layout = (LinearLayout) dialogEPG
                .findViewById(R.id.contentListInputFilterBanner);
        // Clear lists of filter buttons
        layout.removeAllViews();
        filterButtons.clear();
        for (int i = 0; i < filterOptions.length; i++) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton allButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            allButton.setFocusable(false);
            int textID = com.iwedia.gui.R.string.main_menu_content_list_filter_all;
            switch (i) {
                case 0:
                    textID = com.iwedia.gui.R.string.main_menu_content_list_filter_all;
                    break;
                case 1:
                    textID = com.iwedia.gui.R.string.spinner_item_movie;
                    break;
                case 2:
                    textID = com.iwedia.gui.R.string.epg_genre_news;
                    break;
                case 3:
                    textID = com.iwedia.gui.R.string.epg_genre_show;
                    break;
                case 4:
                    textID = com.iwedia.gui.R.string.epg_genre_politics;
                    break;
                case 6:
                    textID = com.iwedia.gui.R.string.epg_genre_children;
                    break;
                case 7:
                    textID = com.iwedia.gui.R.string.epg_genre_culture;
                    break;
                case 8:
                    textID = com.iwedia.gui.R.string.epg_genre_politics;
                    break;
                case 9:
                    textID = com.iwedia.gui.R.string.epg_genre_science;
                    break;
                case 10:
                    textID = com.iwedia.gui.R.string.epg_genre_hobies;
                    break;
            }
            allButton.setText(textID);
            if (((MainActivity) activity).isFullHD()) {
                allButton.setTextSize(activity.getResources().getDimension(
                        com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            } else {
                allButton.setTextSize(activity.getResources().getDimension(
                        com.iwedia.gui.R.dimen.content_filter_text_size));
            }
            setFilterItemParams(filterItem);
            filterButtons.add(filterItem);
            filterItem.setTag(i);
            layout.addView(filterItem);
        }
    }

    /** Filter content of content list */
    private void filterContent(final int filter, boolean isInitial) {
        int currentSelectedFilter = 0;
        contentFilterOptionsScroll.setEnabledScroll(true);
        // Deselect all options and find index of filter option in
        // list
        for (int i = 0; i < filterButtons.size(); i++) {
            filterButtons.get(i).setSelected(false);
            if ((Integer) filterButtons.get(i).getTag() == filter) {
                // Take value in local variable of current selected
                // option for
                // filtering
                currentSelectedFilter = i;
            }
        }
        // Select required one
        filterButtons.get(currentSelectedFilter).setSelected(true);
        filterButtons.get(currentSelectedFilter).setFocusable(true);
        filterButtons.get(currentSelectedFilter).requestFocus();
        filterButtons.get(currentSelectedFilter).setFocusable(false);
        filterButtons.get(currentSelectedFilter).clearFocus();
        contentFilterOptionsScroll.setEnabledScroll(false);
        // for calling from remote control
        if (!isInitial) {
            if (threadChangeEpgGenre == null) {
                threadChangeEpgGenre = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newGenreCounter = 0;
                        while (newGenreCounter < NUMBER_OF_MILISECONDS_FOR_SERVICECHANGE) {
                            try {
                                Thread.sleep(100);
                                newGenreCounter += 100;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // go to channel change
                        handlerGUI.sendMessage(Message.obtain(handlerGUI,
                                CHANGE_GENRE, currentSelectedFilterOption));
                    }
                });
                threadChangeEpgGenre.start();
            }
            newGenreCounter = 0;
        }
        // just for initial call
        else {
            Log.d(TAG, "SETTED FILTER: " + filter);
            currentSelectedFilterOption = filter;
            try {
                // MainActivity.service.getEpgControl().setEpgFilterGenre(filter);
                stopThread();
                clearViewsFromScreenAndData();
                startThread(runnableThreadInitial);
                scrollView.scrollTo(0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Select next filter option */
    public void selectNextFilter() {
        // Calculate next filter
        currentSelectedFilterOption++;
        if (currentSelectedFilterOption >= filterButtons.size()) {
            currentSelectedFilterOption = 0;
        }
        // Load filter
        filterContent((Integer) filterButtons.get(currentSelectedFilterOption)
                .getTag(), false);
    }

    /** Select previous filter option */
    public void selectPreviousFilter() {
        // Calculate next filter
        currentSelectedFilterOption--;
        if (currentSelectedFilterOption < 0) {
            currentSelectedFilterOption = filterButtons.size() - 1;
        }
        // Load filter
        filterContent((Integer) filterButtons.get(currentSelectedFilterOption)
                .getTag(), false);
    }

    /** Customize filter item */
    private void setFilterItemParams(LinearLayout filterItem) {
        LinearLayout.LayoutParams filterItemParams = new LinearLayout.LayoutParams(
                widthMeasureUnit, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        filterItem.setLayoutParams(filterItemParams);
        filterItem.setEnabled(false);
        filterItem.setFocusable(false);
        filterItem.setGravity(Gravity.CENTER);
    }

    public void changeEPGZoom() {
        // Log.d(TAG, "CHANGE ZOOM FUNCTION, current: "
        // + NUMBER_OF_HOURS_IN_WINDOW);
        if (NUMBER_OF_HOURS_IN_WINDOW == 2) {
            NUMBER_OF_HOURS_IN_WINDOW = 8;
        } else {
            if (NUMBER_OF_HOURS_IN_WINDOW == 8) {
                NUMBER_OF_HOURS_IN_WINDOW = 2;
            }
        }
        changeZoomControls();
        stopThread();
        clearViewsFromScreenAndData();
        startThread(runnableThreadInitial);
        scrollView.scrollTo(0, 0);
    }

    /**
     * Change zoom controls of EPG
     */
    private void changeZoomControls() {
        if (NUMBER_OF_HOURS_IN_WINDOW == 2) {
            zoomOut.setSelected(true);
            zoomIn.setSelected(false);
        } else {
            zoomOut.setSelected(false);
            zoomIn.setSelected(true);
        }
    }

    public void openReminderDialog() {
        EpgReminderDialog epgRemDialog = activity.getDialogManager()
                .getEpgReminderDialog();
        if (epgRemDialog != null) {
            epgRemDialog.show();
        }
    }

    public void openScheduleRecordingDialog() {
        EpgScheduleRecordingDialog epgScheduleRecordingDialog = activity
                .getDialogManager().getEpgScheduleRecordingDialog();
        if (epgScheduleRecordingDialog != null) {
            epgScheduleRecordingDialog.show();
        }
    }

    /**
     * Show dialog
     */
    public void showEPGDialog() {
        contentListControl = null;
        /** Get instance of content list control */
        try {
            contentListControl = MainActivity.service.getContentListControl();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        TimeDate timeDate = null;
        /** Check time from stream */
        try {
            timeDate = MainActivity.service.getSystemControl()
                    .getDateAndTimeControl().getTimeDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "date from stream:" + timeDate.toString());
        startTime = new TimeDate(0, 0, 0, timeDate.getDay(),
                timeDate.getMonth(), timeDate.getYear());
        endTime = new TimeDate(59, 59, 23, timeDate.getDay(),
                timeDate.getMonth(), timeDate.getYear());
        epgTimeFilter.setTime(startTime, endTime);
        try {
            MainActivity.service.getEpgControl().setFilter(
                    MainActivity.epgClientId, epgTimeFilter);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (contentListControl != null) {
            // check current filter type of content list
            try {
                currentFilterFromContentList = contentListControl
                        .getActiveFilterIndex();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Log.d(TAG, "CURRENT FILTER LOADED: " + currentFilterFromContentList);
            // if filter type is not one of following set filter type to ALL
            // if (currentFilterFromContentList != FilterType.ALL
            // && currentFilterFromContentList != FilterType.SATELLITE
            // && currentFilterFromContentList != FilterType.CABLE
            // && currentFilterFromContentList != FilterType.TERRESTRIAL) {
            try {
                contentListControl.setActiveFilter(FilterType.ALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "CURRENT FILTER SETTED IN IF: FilterType.ALL");
            // }
            /** If active filter is ALL subtract all others filters from ALL */
            int activeFilter = 0;
            try {
                activeFilter = contentListControl.getActiveFilterIndex();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (activeFilter == FilterType.ALL) {
                // Log.d(TAG,
                // "IF ACTIVE FILTER ALL, SUBSTRACT RADIO APPS etc...");
                // try {
                // contentListControl.setActiveFilter(FilterType.RADIO);
                // numberOfOtherContents += contentListControl
                // .getContentListSize();
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // Log.d(TAG, "NUMBER OF OTHER CONTENTS AFTER RADIO: "
                // + numberOfOtherContents);
                // try {
                // contentListControl.setActiveFilter(FilterType.IP_STREAM);
                // numberOfOtherContents += contentListControl
                // .getContentListSize();
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // Log.d(TAG, "NUMBER OF OTHER CONTENTS AFTER IP: "
                // + numberOfOtherContents);
                /** Return filter type to ALL for future use */
                try {
                    contentListControl.setActiveFilter(FilterType.ALL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // get number of services
            numberOfServices = 0;
            try {
                numberOfServices = MainActivity.service.getServiceControl()
                        .getServiceListCount(ServiceListIndex.MASTER_LIST);
                Log.d(TAG, "NUMBER OF SERVICES: " + numberOfServices);
                Log.d(TAG, "NUMBER OF SERVICES AFTER SUBSTRACTION: "
                        + numberOfServices);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (numberOfServices > 0) {
                if (dialogEPG != null) {
                    dialogEPG.show();
                }
                MainKeyListener.setAppState(MainKeyListener.EPG);
                int activeServiceIndex = 0;
                try {
                    activeServiceIndex = contentListControl
                            .getContentIndexInAllList(contentListControl
                                    .getActiveContent(mDisplayId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "ACTIVE SERVICE INDEX: " + activeServiceIndex);
                galleryAdapter.notifyDataSetChanged();
                if (numberOfServices >= MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
                    galleryEPG.setSelection((int) (Integer.MAX_VALUE / 2)
                            - (Integer.MAX_VALUE / 2) % numberOfServices
                            + activeServiceIndex);
                }
                // if there is less than 5 services
                else {
                    galleryEPG.setSelection(activeServiceIndex);
                }
                changeZoomControls();
                if (mainLayout.getChildCount() != 0) {
                    clearViewsFromScreenAndData();
                }
                // select active genre
                int genre = 0;
                try {
                    // genre = MainActivity.service.getEpgControl()
                    // .getEpgFilterGenre();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "GENRE: " + genre);
                filterContent(genre, true);
                // start thread to load epg data
                startThread(runnableThreadInitial);
            } else {
                // TODO Erase this debug toasts
                A4TVToast toast = new A4TVToast(activity);
                toast.showToast("No services available");
                MainKeyListener.returnToStoredAppState();
            }
        } else {
            A4TVToast toast = new A4TVToast(activity);
            toast.showToast("No Content list");
            MainKeyListener.returnToStoredAppState();
        }
    }

    public void hideEPGDialog() {
        dialogEPG.cancel();
    }

    /** Clear all views from screen and lists for saving epg contents */
    public void clearViewsFromScreenAndData() {
        mainLayout.removeAllViews();
        linearLayoutsEPGContents.clear();
        // clear data
        // for (LinearLayout layout : linearLayoutsEPGContents) {
        // layout.removeAllViews();
        // }
        for (ArrayList<EPGEventShowed> list : epgEventsHeights) {
            list.clear();
        }
        frameLayoutEPGForSmallEvents.removeAllViews();
        frameLayoutEPGForSmallEvents.refreshDrawableState();
        frameLayoutEPGForSmallEvents.invalidate();
        EPGEventFocusListener.isStarted = false;
        EPGEventFocusListener.handler = null;
    }

    /**
     * Runnables and handler initialization
     */
    private void initHandlerAndRunnables() {
        // handles messages from threads
        handlerGUI = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CANCEL_DIALOG: {
                        progressDialog.cancel();
                        break;
                    }
                    case STOP_THREAD: {
                        Log.d(TAG, "STOP THREAD IN HANDLER ENTERED");
                        mainLayout.invalidate();
                        stopThread();
                        progressDialog.cancel();
                        giveFocusToDesiredChild();
                        startThread(runnableAdderThread);
                        break;
                    }
                    case STOP_THREAD_ADDER: {
                        Log.d(TAG, "STOP THREAD ADDER IN HANDLER ENTERED");
                        mainLayout.invalidate();
                        stopThread();
                        // startThread(runnableThreadBackgroundCheck);
                        break;
                    }
                    case ADD_ONE_LAYOUT_FROM_INITIAL: {
                        LinearLayout lay = (LinearLayout) msg.obj;
                        // add layout
                        mainLayout.addView(lay);
                        linearLayoutsEPGContents.add(lay);
                        break;
                    }
                    case ADD_LAST_ITEM: {
                        Log.d(TAG, "ADD LAST ITEM IN HANDLER ENTERED");
                        setFocusabilityToLayoutContents(true);
                        // remove first views
                        mainLayout.removeViewAt(0);
                        linearLayoutsEPGContents.remove(0);
                        // set layout params
                        mainLayout.getChildAt(1).setLayoutParams(
                                new LinearLayout.LayoutParams(0,
                                        LayoutParams.MATCH_PARENT, 1));
                        mainLayout.getChildAt(2).setLayoutParams(
                                new LinearLayout.LayoutParams(0,
                                        LayoutParams.MATCH_PARENT, 2));
                        try {
                            // change background
                            FrameLayout tvLayout = (FrameLayout) mainLayout
                                    .getChildAt(2)
                                    .findViewById(empty_layout_id);
                            tvLayout.setBackgroundResource(R.drawable.epg_no_data_double_row);
                            tvLayout = (FrameLayout) mainLayout.getChildAt(1)
                                    .findViewById(empty_layout_id);
                            tvLayout.setBackgroundResource(R.drawable.epg_no_data_single_row);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LinearLayout lay = (LinearLayout) msg.obj;
                        // add views
                        mainLayout.addView(lay);
                        linearLayoutsEPGContents.add(lay);
                        // invalidate view
                        mainLayout.invalidate();
                        stopThread();
                        progressDialog.cancel();
                        giveFocusToDesiredChild();
                        startThread(runnableAdderThread);
                        break;
                    }
                    case ADD_FIRST_ITEM: {
                        Log.d(TAG, "ADD FIRST ITEM IN HANDLER ENTERED");
                        setFocusabilityToLayoutContents(false);
                        // remove first views
                        mainLayout.removeViewAt(4);
                        linearLayoutsEPGContents.remove(4);
                        // set layout params
                        mainLayout.getChildAt(2).setLayoutParams(
                                new LinearLayout.LayoutParams(0,
                                        LayoutParams.MATCH_PARENT, 1));
                        mainLayout.getChildAt(1).setLayoutParams(
                                new LinearLayout.LayoutParams(0,
                                        LayoutParams.MATCH_PARENT, 2));
                        try {
                            // change background
                            FrameLayout tvLayout = (FrameLayout) mainLayout
                                    .getChildAt(1)
                                    .findViewById(empty_layout_id);
                            tvLayout.setBackgroundResource(R.drawable.epg_no_data_double_row);
                            tvLayout = (FrameLayout) mainLayout.getChildAt(2)
                                    .findViewById(empty_layout_id);
                            tvLayout.setBackgroundResource(R.drawable.epg_no_data_single_row);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        LinearLayout lay = (LinearLayout) msg.obj;
                        // add views
                        mainLayout.addView(lay, 0);
                        linearLayoutsEPGContents.add(0, lay);
                        // invalidate view
                        mainLayout.invalidate();
                        stopThread();
                        progressDialog.cancel();
                        giveFocusToDesiredChild();
                        startThread(runnableAdderThread);
                        break;
                    }
                    case ADD_ONE_EVENT: {
                        Log.d(TAG, "ADD ONE EVENT IN HANDLER ENTERED");
                        if (mainLayout.getChildCount() > msg.arg1
                                && msg.obj != null) {
                            ((LinearLayout) mainLayout.getChildAt(msg.arg1))
                                    .addView((View) msg.obj);
                        }
                        // invalidate view
                        // mainLayout.invalidate();
                        break;
                    }
                    case ADD_SIDE_TIME_LAYOUT: {
                        layoutForTimeAndEvents.removeView(layoutForTime);
                        layoutForTimeAndEvents.addView((LinearLayout) msg.obj,
                                0);
                        layoutForTime = (LinearLayout) msg.obj;
                        layoutForTimeAndEvents.invalidate();
                        break;
                    }
                    case SET_DATE: {
                        stringBuilder = new StringBuilder();
                        String str = (String) msg.obj;
                        if (str.length() < 11) {
                            textViewDayShowing.setText(stringBuilder
                                    .append(" ").append(str).toString());
                        } else {
                            str = str.replace(':', '/');
                            textViewDayShowing.setText(stringBuilder
                                    .append(" ").append(str.substring(9))
                                    .toString());
                        }
                        break;
                    }
                    case CHANGE_SERVICE: {
                        // newServiceIndex = (Integer) msg.obj;
                        threadChangeService = null;
                        textViewInfo.setText(R.string.electronic_program_guide);
                        Log.d(TAG, "NEW SERVICE INDEX " + newServiceIndex);
                        if (newServiceIndex != 0
                                && newServiceIndex <= numberOfServices) {
                            if (numberOfServices >= MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
                                galleryEPG
                                        .setSelection((int) (Integer.MAX_VALUE / 2)
                                                - (Integer.MAX_VALUE / 2)
                                                % numberOfServices
                                                + newServiceIndex - 1);
                            }
                            // if there is less than 5 services
                            else {
                                galleryEPG.setSelection(newServiceIndex - 1);
                            }
                            if (mainLayout.getChildCount() != 0) {
                                clearViewsFromScreenAndData();
                            }
                            scrollView.scrollTo(0, 0);
                            stopThread();
                            // start thread to load epg data
                            startThread(runnableThreadInitial);
                        } else {
                            new A4TVToast(activity)
                                    .showToast(R.string.epg_error_with_number);
                        }
                        break;
                    }
                    case CHANGE_GENRE: {
                        threadChangeEpgGenre = null;
                        int filter = (Integer) msg.obj;
                        Log.d(TAG, "SETTED FILTER: " + filter);
                        try {
                            // MainActivity.service.getEpgControl().setEpgFilterGenre(
                            // filter);
                            stopThread();
                            // // if (viewThatHasFocus != null) {
                            // //
                            // removeViewsFromFrameLayoutEPGForSmallEvents((Integer)
                            // // viewThatHasFocus
                            // // .getTag());
                            // frameLayoutEPGForSmallEvents.removeAllViews();
                            // frameLayoutEPGForSmallEvents.refreshDrawableState();
                            // frameLayoutEPGForSmallEvents.invalidate();
                            // EPGEventFocusListener.isStarted = false;
                            // EPGEventFocusListener.handler = null;
                            // // }
                            clearViewsFromScreenAndData();
                            startThread(runnableThreadInitial);
                            scrollView.scrollTo(0, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
        // runnable to run when user opens EPG dialog
        runnableThreadInitial = new Runnable() {
            @Override
            public void run() {
                Thread thisThread = Thread.currentThread();
                if (thisThread.equals(backgroundThread)) {
                    Log.d(TAG, "ENTERED IF");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // check date and one hour height
                    getCurrentDate();
                    epgEventsListCounts.clear();
                    IContentListControl contentListControl = null;
                    try {
                        contentListControl = MainActivity.service
                                .getContentListControl();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (contentListControl != null) {
                        // initially fill EPG data
                        for (column = 0; column < NUMBER_OF_VISIBLE_SERVICES; column++) {
                            if (column != 0) {
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            // create linear layout to put data in
                            final LinearLayout layout = new LinearLayout(
                                    activity);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.setPadding(5, 0, 5, 0);
                            if (column == CENTRAL_COLUMN_INDEX) {
                                layout.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LayoutParams.MATCH_PARENT, 2));
                            } else {
                                layout.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LayoutParams.MATCH_PARENT, 1));
                            }
                            int indexInMasterList = 0;
                            if (numberOfServices >= MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
                                // if it is in the middle
                                if (galleryEPG.getSelectedItemPosition()
                                        % numberOfServices >= 2
                                        && galleryEPG.getSelectedItemPosition()
                                                % numberOfServices < numberOfServices - 2) {
                                    try {
                                        indexInMasterList = contentListControl
                                                .getContent(
                                                        (galleryEPG
                                                                .getSelectedItemPosition() % numberOfServices)
                                                                + (column - 2))
                                                .getIndexInMasterList();
                                        epgEventsListCounts
                                                .add(getEpgEventListCount(indexInMasterList));
                                        fillLayout(
                                                layout,
                                                false,
                                                (galleryEPG
                                                        .getSelectedItemPosition() % numberOfServices)
                                                        + (column - 2), column);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    /**********************************************************/
                                    // if it is on the beginning
                                    // selected second service
                                    if (galleryEPG.getSelectedItemPosition()
                                            % numberOfServices < numberOfServices - 2
                                            && galleryEPG
                                                    .getSelectedItemPosition()
                                                    % numberOfServices == 1) {
                                        if (column > 0) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(
                                                                (galleryEPG
                                                                        .getSelectedItemPosition() % numberOfServices)
                                                                        - 2
                                                                        + column)
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(
                                                        layout,
                                                        false,
                                                        (galleryEPG
                                                                .getSelectedItemPosition() % numberOfServices)
                                                                - 2 + column,
                                                        column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (column == 0) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(
                                                                numberOfServices - 1)
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(layout, false,
                                                        numberOfServices - 1,
                                                        column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    if (galleryEPG.getSelectedItemPosition()
                                            % numberOfServices < numberOfServices - 2
                                            && galleryEPG
                                                    .getSelectedItemPosition()
                                                    % numberOfServices == 0) {
                                        // selected first service
                                        if (column > 1) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(
                                                                (galleryEPG
                                                                        .getSelectedItemPosition() % numberOfServices)
                                                                        - 2
                                                                        + column)
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(
                                                        layout,
                                                        false,
                                                        (galleryEPG
                                                                .getSelectedItemPosition() % numberOfServices)
                                                                - 2 + column,
                                                        column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (column <= 1) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(
                                                                numberOfServices
                                                                        - (2 - column))
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(layout, false,
                                                        numberOfServices
                                                                - (2 - column),
                                                        column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    /*****************************************************************/
                                    // if it is in the end
                                    // selected second last
                                    if (galleryEPG.getSelectedItemPosition()
                                            % numberOfServices >= 2
                                            && galleryEPG
                                                    .getSelectedItemPosition()
                                                    % numberOfServices == numberOfServices - 2) {
                                        if (column < 4) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(
                                                                galleryEPG
                                                                        .getSelectedItemPosition()
                                                                        % numberOfServices
                                                                        + (column - 2))
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(
                                                        layout,
                                                        false,
                                                        (galleryEPG
                                                                .getSelectedItemPosition() % numberOfServices)
                                                                + (column - 2),
                                                        column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (column == 4) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(0)
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(layout, false, 0,
                                                        column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            } catch (RuntimeException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    // selected last service
                                    if (galleryEPG.getSelectedItemPosition()
                                            % numberOfServices >= 2
                                            && galleryEPG
                                                    .getSelectedItemPosition()
                                                    % numberOfServices == numberOfServices - 1) {
                                        if (column < 3) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(
                                                                (galleryEPG
                                                                        .getSelectedItemPosition() % numberOfServices)
                                                                        + (column - 2))
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(
                                                        layout,
                                                        false,
                                                        (galleryEPG
                                                                .getSelectedItemPosition() % numberOfServices)
                                                                + (column - 2),
                                                        column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            } catch (RuntimeException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (column >= 3) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(column - 3)
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(layout, false,
                                                        column - 3, column);
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            } catch (RuntimeException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            } else {
                                switch (numberOfServices) {
                                /** ONE SERVICE IN BASE **/
                                    case 1: {
                                        if (column == 2) {
                                            try {
                                                indexInMasterList = contentListControl
                                                        .getContent(0)
                                                        .getIndexInMasterList();
                                                epgEventsListCounts
                                                        .add(getEpgEventListCount(indexInMasterList));
                                                fillLayout(
                                                        layout,
                                                        false,
                                                        galleryEPG
                                                                .getSelectedItemPosition(),
                                                        column);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            epgEventsListCounts.add(0);
                                        }
                                        break;
                                    }
                                    /** TWO SERVICE IN BASE **/
                                    case 2: {
                                        // selected first
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 0) {
                                            if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 3) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        // selected second
                                        else {
                                            if (column == 1) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        break;
                                    }
                                    /** THREE SERVICE IN BASE **/
                                    case 3: {
                                        // selected first
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 0) {
                                            if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 3) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 4) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            2, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        // selected second
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 1) {
                                            if (column == 1) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 3) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(2)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            2, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        // selected third
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 2) {
                                            if (column == 0) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 1) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(2)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            2, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        break;
                                    }
                                    /** FOUR SERVICE IN BASE **/
                                    case 4: {
                                        // selected first
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 0) {
                                            if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 3) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 4) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(2)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            2, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        // selected second
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 1) {
                                            if (column == 1) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 3) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(2)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            2, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 4) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(3)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            3, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        // selected third
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 2) {
                                            if (column == 0) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(0)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            0, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 1) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(2)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            2, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 3) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(3)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            3, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        // selected fourth
                                        if (galleryEPG
                                                .getSelectedItemPosition() == 3) {
                                            if (column == 0) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(1)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            1, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 1) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(2)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            2, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (column == 2) {
                                                try {
                                                    indexInMasterList = contentListControl
                                                            .getContent(3)
                                                            .getIndexInMasterList();
                                                    epgEventsListCounts
                                                            .add(getEpgEventListCount(indexInMasterList));
                                                    fillLayout(layout, false,
                                                            3, column);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                epgEventsListCounts.add(0);
                                            }
                                        }
                                        break;
                                    }
                                    default:
                                        break;
                                }
                            }
                            // send message here
                            handlerGUI.sendMessage(Message.obtain(handlerGUI,
                                    ADD_ONE_LAYOUT_FROM_INITIAL, column, 0,
                                    layout));
                        }
                        // fill content of side bar with time
                        isChangedEndTime = false;
                        fillSideTime();
                        Log.d(TAG,
                                "EPG LIST COUNTS"
                                        + epgEventsListCounts.toString());
                    }
                    handlerGUI.sendEmptyMessage(STOP_THREAD);
                }
            }
        };
        // runnable to run when user click DPAD_RIGHT
        runnableThreadRight = new Runnable() {
            @Override
            public void run() {
                Thread thisThread = Thread.currentThread();
                if (thisThread.equals(backgroundThread)) {
                    Log.d(TAG, "ENTERED IF");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // create new layout
                    LinearLayout lay = new LinearLayout(activity);
                    lay.setPadding(5, 0, 5, 0);
                    lay.setOrientation(LinearLayout.VERTICAL);
                    lay.setLayoutParams(new LinearLayout.LayoutParams(0,
                            LayoutParams.MATCH_PARENT, 1));
                    // remove first list
                    epgEventsHeights.remove(0);
                    epgEventsHeights.add(new ArrayList<EPGEventShowed>());
                    // fill new layout with contents
                    if (numberOfServices >= MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
                        // check for border scenario
                        if (galleryEPG.getSelectedItemPosition()
                                % numberOfServices < numberOfServices - 2) {
                            fillLayout(lay, true,
                                    galleryEPG.getSelectedItemPosition()
                                            % numberOfServices + 2, 4);
                        } else {
                            if (galleryEPG.getSelectedItemPosition()
                                    % numberOfServices + 2 == numberOfServices) {
                                fillLayout(lay, true, 0, 4);
                            } else {
                                fillLayout(lay, true, 1, 4);
                            }
                        }
                    }
                    if (numberOfServices == MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL - 1) {
                        if (galleryEPG.getSelectedItemPosition() <= 1) {
                            fillLayout(lay, true,
                                    galleryEPG.getSelectedItemPosition() + 2, 4);
                        }
                    }
                    // fill content of side bar with time
                    if (isChangedEndTime) {
                        isChangedEndTime = false;
                        fillSideTime();
                    }
                    // add view
                    handlerGUI.sendMessage(Message.obtain(handlerGUI,
                            ADD_LAST_ITEM, lay));
                } else {
                    handlerGUI.sendEmptyMessageDelayed(CANCEL_DIALOG, 3000);
                }
            }
        };
        // runnable to run when user click DPAD_LEFT
        runnableThreadLeft = new Runnable() {
            @Override
            public void run() {
                Thread thisThread = Thread.currentThread();
                if (thisThread.equals(backgroundThread)) {
                    Log.d(TAG, "ENTERED IF");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // create new layout
                    LinearLayout lay = new LinearLayout(activity);
                    lay.setPadding(5, 0, 5, 0);
                    lay.setOrientation(LinearLayout.VERTICAL);
                    lay.setLayoutParams(new LinearLayout.LayoutParams(0,
                            LayoutParams.MATCH_PARENT, 1));
                    // remove last list
                    epgEventsHeights.remove(4);
                    epgEventsHeights.add(0, new ArrayList<EPGEventShowed>());
                    // fill new layout with contents
                    if (numberOfServices >= MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
                        // check for border scenario
                        if (galleryEPG.getSelectedItemPosition()
                                % numberOfServices > 1) {
                            fillLayout(lay, true,
                                    galleryEPG.getSelectedItemPosition()
                                            % numberOfServices - 2, 0);
                        } else {
                            if (galleryEPG.getSelectedItemPosition()
                                    % numberOfServices == 1) {
                                fillLayout(lay, true, numberOfServices - 1, 0);
                            } else {
                                fillLayout(lay, true, numberOfServices - 2, 0);
                            }
                        }
                    }
                    if (numberOfServices == MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL - 1) {
                        if (galleryEPG.getSelectedItemPosition() >= 2) {
                            fillLayout(lay, true,
                                    galleryEPG.getSelectedItemPosition() - 2, 0);
                        }
                    }
                    // fill content of side bar with time
                    if (isChangedEndTime) {
                        isChangedEndTime = false;
                        fillSideTime();
                    }
                    // add view
                    handlerGUI.sendMessage(Message.obtain(handlerGUI,
                            ADD_FIRST_ITEM, lay));
                } else {
                    handlerGUI.sendEmptyMessageDelayed(CANCEL_DIALOG, 3000);
                }
            }
        };
        runnableAdderThread = new Runnable() {
            @Override
            public void run() {
                Thread thisThread = Thread.currentThread();
                if (thisThread.equals(backgroundThread)) {
                    Log.d(TAG, "RUNNABLE ADDER THREAD ENTERED IF");
                    int biggestCount = 0;
                    // first for finds index of first view to show
                    for (int i = 0; i < epgEventsHeights.size(); i++) {
                        // check what service get the most elements
                        if (biggestCount < epgEventsHeights.get(i).size()) {
                            biggestCount = epgEventsHeights.get(i).size();
                        }
                    }
                    // go for every event vertically
                    for (int j = 0; j < biggestCount; j++) {
                        boolean isAddedSomeViews = false;
                        // go for every layout
                        for (int i = 0; i < epgEventsHeights.size(); i++) {
                            if (j < epgEventsHeights.get(i).size()) {
                                // if it is not shown on the screen
                                if (!epgEventsHeights.get(i).get(j).isShowed()) {
                                    // view to show on epg
                                    View viewToAdd = epgEventsHeights.get(i)
                                            .get(j).getBtnOnScreen();
                                    Button btn = (Button) viewToAdd
                                            .findViewById(R.id.buttonEPG);
                                    if (i == CENTRAL_COLUMN_INDEX) {
                                        btn.setFocusable(true);
                                    } else {
                                        btn.setFocusable(false);
                                    }
                                    if (!epgEventsHeights.get(i).get(j)
                                            .isShowed()) {
                                        // send message to handler to draw views
                                        handlerGUI.sendMessage(Message.obtain(
                                                handlerGUI, ADD_ONE_EVENT, i,
                                                0, viewToAdd));
                                        // set flag to EpgEventShowed object to
                                        // shown
                                        epgEventsHeights.get(i).get(j)
                                                .setShowed(true);
                                        isAddedSomeViews = true;
                                    }
                                }
                            }
                        }
                        if (isAddedSomeViews) {
                            if (j % 4 == 0) {
                                // sleep a little
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    handlerGUI.sendEmptyMessage(STOP_THREAD_ADDER);
                }
            }
        };
    }

    /**
     * Get EPG events count for desired service index.
     * 
     * @param serviceIndex
     *        Service index in master list.
     * @return EPG events count.
     * @throws RemoteException
     */
    private int getEpgEventListCount(int serviceIndex) throws RemoteException {
        IEpgControl epgControl = MainActivity.service.getEpgControl();
        // if desired service index is not already active
        if (currentServiceIndex != serviceIndex) {
            // for the first time
            if (currentServiceIndex != -1) {
                epgControl.stopAcquisition(MainActivity.epgClientId);
            }
            currentServiceIndex = serviceIndex;
            // setfilter
            epgServiceFilter = new EpgServiceFilter();
            epgServiceFilter.setServiceIndex(serviceIndex);
            epgControl.setFilter(MainActivity.epgClientId, epgServiceFilter);
            epgControl.startAcquisition(MainActivity.epgClientId);
        }
        return epgControl.getAvailableEventsNumber(MainActivity.epgClientId,
                currentServiceIndex);
    }

    /**
     * Give focus to running event or first on screen
     */
    public void giveFocusToDesiredChild() {
        if (mainLayout.getChildCount() > 3) {
            int startPixel = scrollView.getScrollY();
            int endPixel = scrollView.getScrollY()
                    + scrollView.getVisibleRectHeight();
            Log.d(TAG, "start pixel: " + startPixel + ", end pixel: "
                    + endPixel);
            View viewToRequestFocus = null;
            Date dateFromStream = null;
            /** Check time from stream */
            try {
                dateFromStream = MainActivity.service.getSystemControl()
                        .getDateAndTimeControl().getTimeDate().getCalendar()
                        .getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < ((LinearLayout) mainLayout
                    .getChildAt(CENTRAL_COLUMN_INDEX)).getChildCount(); i++) {
                View view = ((LinearLayout) mainLayout
                        .getChildAt(CENTRAL_COLUMN_INDEX)).getChildAt(i);
                Log.d(TAG, "view.getTop(): " + view.getTop()
                        + ", view.getBottom(): " + view.getBottom());
                if (view.getTop() >= startPixel && view.getBottom() <= endPixel) {
                    // save first visible button to give him focus if there is
                    // no event that is running
                    if (viewToRequestFocus == null) {
                        viewToRequestFocus = view;
                    }
                    Log.d(TAG, "view.getTop() in IF: " + view.getTop());
                    if (epgEventsHeights.size() >= 3
                            && epgEventsHeights.get(CENTRAL_COLUMN_INDEX)
                                    .size() > i) {
                        Date start = null, end = null;
                        start = epgEventsHeights.get(CENTRAL_COLUMN_INDEX)
                                .get(i).getStartTime().getCalendar().getTime();
                        end = epgEventsHeights.get(CENTRAL_COLUMN_INDEX).get(i)
                                .getEndTime().getCalendar().getTime();
                        // if it is running event give it focus
                        if (dateFromStream != null && start != null
                                && end != null && dateFromStream.after(start)
                                && dateFromStream.before(end)) {
                            /*
                             * Log.d(TAG, "request focus to view, start: " +
                             * start.toString() + ", end: " + end.toString() +
                             * ", current time: " + dateFromStream);
                             */
                            view.requestFocus();
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "view.getTop() in ELSE: " + view.getTop());
                    if (view.getTop() <= startPixel
                            && view.getBottom() >= endPixel) {
                        Log.d(TAG, "view.getTop() in ELSE: " + view.getTop());
                        Log.d(TAG, "request focus to view, view to BIG");
                        view.requestFocus();
                        return;
                    }
                }
            }
            if (viewToRequestFocus != null) {
                Log.d(TAG, "request focus to view, view first visible");
                viewToRequestFocus.requestFocus();
            }
        }
    }

    private void setFocusabilityToLayoutContents(boolean right) {
        // for past central column
        for (int j = 0; j < linearLayoutsEPGContents.get(CENTRAL_COLUMN_INDEX)
                .getChildCount(); j++) {
            try {
                linearLayoutsEPGContents.get(CENTRAL_COLUMN_INDEX)
                        .getChildAt(j).findViewById(R.id.buttonEPG)
                        .setFocusable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // for right button clicked
        if (right) {
            // for future central column
            for (int j = 0; j < linearLayoutsEPGContents.get(3).getChildCount(); j++) {
                try {
                    linearLayoutsEPGContents.get(3).getChildAt(j)
                            .findViewById(R.id.buttonEPG).setFocusable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // for left button clicked
        else {
            // for future central column
            for (int j = 0; j < linearLayoutsEPGContents.get(1).getChildCount(); j++) {
                try {
                    linearLayoutsEPGContents.get(1).getChildAt(j)
                            .findViewById(R.id.buttonEPG).setFocusable(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Fill one column with EPG data
     * 
     * @param lay
     *        layout to add data
     * @param addNow
     *        add now or send message to handler to draw it
     * @param indexOfService
     *        service index
     * @param indexOfList
     *        index of column
     */
    private void fillLayout(LinearLayout lay, boolean addNow,
            int indexOfService, int indexOfList) {
        // if it is right or left, first load number of EPG data for new service
        if (addNow) {
            IContentListControl contentListControl = null;
            try {
                contentListControl = MainActivity.service
                        .getContentListControl();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            int epgEventListCount = 10;
            try {
                if (contentListControl != null)
                    epgEventListCount = getEpgEventListCount(contentListControl
                            .getContent(indexOfService).getIndexInMasterList());
            } catch (RemoteException e1) {
                e1.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            // adjust array list epgEventsListCounts to represent new changed
            // services in gallery
            if (indexOfList == 0 && epgEventsListCounts.size() > 0) {
                epgEventsListCounts.remove(0);
                epgEventsListCounts.add(0, epgEventListCount);
            }
            if (indexOfList == 4 && epgEventsListCounts.size() > 4) {
                epgEventsListCounts.remove(4);
                epgEventsListCounts.add(epgEventListCount);
            }
        }
        // Log.d(TAG, "COUNTER IN FILL LAYOUT, iCnt: " + iCnt);
        if (epgEventsListCounts.size() > indexOfList) {
            /**
             * If there are not EPG data available add text "No data available"
             * to EPG row
             */
            if (epgEventsListCounts.get(indexOfList) < 1) {
                addEmptyViewToLayout(lay, indexOfList);
                return;
            }
            // create date format parser that parses time returned by service
            SimpleDateFormat format = new SimpleDateFormat(
                    "HH:mm:ss' 'dd/MM/yyyy");
            /** Load events for desired service */
            for (int i = 0; i < ((epgEventsListCounts.size() > indexOfList) ? epgEventsListCounts
                    .get(indexOfList) : 0); i++) {
                EpgEvent event = null;
                try {
                    event = MainActivity.service.getEpgControl()
                            .getRequestedEvent(
                                    MainActivity.epgClientId,
                                    contentListControl.getContent(
                                            indexOfService)
                                            .getIndexInMasterList(), i);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                // Log.d(TAG, "FILL LAYOUT, loading EPG EVENTS, event " + i);
                if (event != null) {
                    /** If called from right or left thread */
                    // if (addNow) {
                    // create new EPG view
                    View view = inflateEPGItem(i, indexOfService, event,
                            indexOfList, format);
                    if (view != null) {
                        // set central column focusable
                        if (indexOfList == CENTRAL_COLUMN_INDEX) {
                            view.findViewById(R.id.buttonEPG)
                                    .setFocusable(true);
                        }
                        try {
                            // if view should be shown add it to layout
                            if (epgEventsHeights
                                    .get(indexOfList)
                                    .get(epgEventsHeights.get(indexOfList)
                                            .size() - 1).isShowed()) {
                                // add view to layout
                                lay.addView(view);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            /** If there is no views to add to layout add empty view to it */
            if (epgEventsHeights.get(indexOfList).size() == 0) {
                addEmptyViewToLayout(lay, indexOfList);
            }
        }
    }

    /**
     * Adds text No data available to epg row
     * 
     * @param layout
     *        Where to add
     */
    private void addEmptyViewToLayout(LinearLayout layout, int indexOfList) {
        Log.d(TAG, "ADD TEXT AND EMPTY BUTTON");
        FrameLayout tvLayout = new FrameLayout(activity);
        tvLayout.setId(empty_layout_id);
        if (indexOfList != CENTRAL_COLUMN_INDEX) {
            tvLayout.setBackgroundResource(R.drawable.epg_no_data_single_row);
        } else {
            tvLayout.setBackgroundResource(R.drawable.epg_no_data_double_row);
        }
        // set layout params to created view
        LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        tvLayout.setLayoutParams(params);
        A4TVTextView verticalTV = new A4TVTextView(activity);
        // set text to text view
        stringBuilder = new StringBuilder();
        verticalTV.setText(R.string.epg_no_data);
        verticalTV.setTextColor(activity.getResources().getColor(
                R.color.epg_text_gray_text_color));
        // set text size
        verticalTV.setTextSize(activity.getResources().getDimension(
                R.dimen.epg_no_data_text_size));
        Button btn = new Button(activity);
        android.widget.FrameLayout.LayoutParams pr = new FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
        pr.gravity = Gravity.CENTER;
        btn.setLayoutParams(pr);
        verticalTV.setLayoutParams(pr);
        verticalTV.setGravity(Gravity.CENTER_HORIZONTAL);
        //
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setId(R.id.buttonEPG);
        btn.setTag(0);
        btn.setOnKeyListener(new EPGKeyListener(EPGHandlingClass.this, true,
                true));
        // add button to created view
        tvLayout.addView(btn);
        tvLayout.addView(verticalTV);
        // add view to layout
        layout.addView(tvLayout);
    }

    /** Check if there is gap between EPG events */
    private float checkForEmptyElement(SimpleDateFormat format,
            int indexOfList, EpgEvent event, int indexOfService,
            int indexOfEvent) {
        Date nowStartTime = null;
        Date prevEndTime = null;
        /** Create Date objects for now start time and last end time */
        try {
            // load start time from EPG event
            nowStartTime = event.getStartTime().getCalendar().getTime();
            // if there is loaded EPG event in list check its end time
            if (epgEventsHeights.get(indexOfList).size() > 0) {
                prevEndTime = epgEventsHeights.get(indexOfList)
                        .get(epgEventsHeights.get(indexOfList).size() - 1)
                        .getEndTime().getCalendar().getTime();
                Log.d(TAG,
                        "******************************"
                                + epgEventsHeights
                                        .get(indexOfList)
                                        .get(epgEventsHeights.get(indexOfList)
                                                .size() - 1).getEndTime()
                                        .getCalendar().getTime());
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        // if it is first event, it do not have previously event so we must
        // create it manually
        if (prevEndTime == null) {
            // get date from loaded event start time
            if (nowStartTime != null)
                prevEndTime = new Date(nowStartTime.getYear(),
                        nowStartTime.getMonth(), nowStartTime.getDate());
            // set time to first visible time on side of the screen
            if (prevEndTime != null) {
                prevEndTime.setHours(timeFromStreamDate.getHours());
                prevEndTime.setMinutes(0);
                prevEndTime.setSeconds(0);
            }
        }
        long differenceInMiliseconds = 0;
        /** Check difference between times */
        if (nowStartTime != null && prevEndTime != null) {
            differenceInMiliseconds = nowStartTime.getTime()
                    - prevEndTime.getTime();
        }
        // Log.d(TAG, "SIMPLE DATE FORMAT first time: " + prevEndTime.toString()
        // + " second time: " + nowStartTime.toString());
        // Log.d(TAG, "SIMPLE DATE FORMAT difference in seconds: "
        // + (differenceInMiliseconds / 1000));
        boolean isGap = false;
        /** Check for gap between events */
        if ((differenceInMiliseconds / 1000) > 60) {
            isGap = true;
        }
        // Log.d(TAG, "epgEventsHeights" + epgEventsHeights);
        /** If there is gap, create empty view and return it */
        if (isGap) {
            // View view = new View(activity);
            return getEPGEventHeight((int) (differenceInMiliseconds / 1000));
        }
        return 0;
    }

    /** Return EPG item view with populated EPG data */
    private View inflateEPGItem(int indexOfEvent, int serviceIndex,
            EpgEvent event, int indexOfList, SimpleDateFormat format) {
        FrameLayout item = null;
        int duration;
        Date nowStartTime = null;
        Date nowEndTime = null;
        // create date objects from events start and end time for easier
        // comparison
        if (event.getStartTime() != null && event.getEndTime() != null) {
            nowStartTime = event.getStartTime().getCalendar().getTime();
            nowEndTime = event.getEndTime().getCalendar().getTime();
            if (nowStartTime != null && nowEndTime != null) {
                // Log.d(TAG,
                // "INFLATE EPG EVENT, start time: "
                // + nowStartTime.toString() + ", end time: "
                // + nowEndTime.toString() + ", LIST: "
                // + indexOfList + ", EVENT INDEX" + indexOfEvent);
                // check if it is past epg event
                int timeFromSt = 0, startTime = 0, endTime = 0;
                // get now hour for today (for other days 00:00:00)
                if (timeFromStreamDate != null) {
                    timeFromSt = timeFromStreamDate.getHours();
                }
                // get event start time
                if (event.getStartTime() != null) {
                    startTime = nowStartTime.getHours();
                }
                // get event end time
                if (event.getEndTime() != null) {
                    endTime = nowEndTime.getHours();
                }
                // Log.d(TAG, "INFLATE EPG EVENT, timeFrom Stream: " +
                // timeFromSt
                // + ", startTime: " + startTime + ",endTime: " + endTime);
                if (timeFromStreamDate != null)
                    if (timeFromStreamDate.getDate() > nowStartTime.getDate()
                            || timeFromStreamDate.getMonth() > nowStartTime
                                    .getMonth()) {
                        return null;
                    }
                // if (timeFromStreamDate.after(nowEndTime)) {
                // return null;
                // }
                // if end time of event is lower than current time just return
                // null
                if (timeFromStreamDate != null)
                    if (timeFromStreamDate.getDate() == nowStartTime.getDate()
                            && timeFromSt > endTime && timeFromSt > startTime) {
                        return null;
                    }
                // load just first 3 hours for faster showing of GUI
                boolean toShowEvent = true;
                if (timeFromStreamDate != null)
                    if (timeFromStreamDate.getDate() == nowStartTime.getDate()) {
                        if (timeFromSt + 2 < startTime) {
                            toShowEvent = false;
                        }
                    } else {
                        toShowEvent = false;
                    }
                // Log.d(TAG, "INFLATE EPG EVENT, timeFrom Stream: " +
                // timeFromSt
                // + ", startTime: " + startTime + ",endTime: " + endTime);
                // if it is in the air
                if (timeFromSt <= endTime && timeFromSt > startTime) {
                    // get date from start time because of calendar date and set
                    // its
                    // time to first visible time on screen
                    Date startTimeFromStream = nowStartTime;
                    startTimeFromStream.setHours(timeFromSt);
                    startTimeFromStream.setMinutes(0);
                    startTimeFromStream.setSeconds(0);
                    // calculate duration between two times
                    duration = (int) ((nowEndTime.getTime() - startTimeFromStream
                            .getTime()) / 1000);
                    // Log.d(TAG,
                    // "IN THE AIR, START TIME: " +
                    // startTimeFromStream.toString());
                    // Log.d(TAG, "IN THE AIR, END TIME: " +
                    // nowEndTime.toString());
                    // Log.d(TAG, "IN THE AIR, DURATION IN SECONDS: " +
                    // duration);
                } else {
                    // if it is not in the air
                    // calculate duration
                    duration = getDurationFromTwoTimes(nowStartTime, nowEndTime);
                    // duration =
                    // getDurationFromString(event.getDurationTime());
                }
                // if duration exist create EPG view to represent it
                if (duration > 0) {
                    float height = getEPGEventHeight(duration);
                    item = (FrameLayout) inflater.inflate(
                            R.layout.epg_normal_item, null);
                    // set dimensions of EPG item
                    LayoutParams params = new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, (int) height);
                    // check if there is empty space between new and old
                    // event
                    if (timeFromStreamDate != null)
                        params.topMargin = (int) checkForEmptyElement(format,
                                indexOfList, event, serviceIndex, indexOfEvent);
                    item.setLayoutParams(params);
                    // take references of inflated view
                    LinearLayout layoutForColor = (LinearLayout) item
                            .findViewById(R.id.linearLayoutForEPGColor);
                    final TextView tvEventName = (TextView) item
                            .findViewById(R.id.textViewEPGEventName);
                    TextView tvEventTime;
                    TextView tvEventDescription;
                    Button btn = (Button) item.findViewById(R.id.buttonEPG);
                    EPGEventShowed eventShowed = new EPGEventShowed(height,
                            serviceIndex, indexOfEvent, duration,
                            event.getStartTime(), event.getEndTime(), item);
                    eventShowed.setShowed(toShowEvent);
                    // add dimension to list
                    epgEventsHeights.get(indexOfList).add(eventShowed);
                    // measure what time to represent last in side
                    if (timeFromStreamDate != null)
                        if (timeFromStreamDate.before(nowStartTime)
                                && timeFromStreamDate.getDate() != nowStartTime
                                        .getDate()) {
                            if (endTimeInt < nowEndTime.getHours()) {
                                if (nowEndTime.getMinutes() != 0) {
                                    endTimeInt = nowEndTime.getHours();
                                    // Log.d(TAG, "END TIME SIDE LAYOUT " +
                                    // endTimeInt);
                                    isChangedEndTime = true;
                                } else {
                                    endTimeInt = nowEndTime.getHours() - 1;
                                    // Log.d(TAG, "END TIME SIDE LAYOUT " +
                                    // endTimeInt);
                                    isChangedEndTime = true;
                                }
                            }
                        }
                    int hourMultiplier;
                    if (NUMBER_OF_HOURS_IN_WINDOW == 2) {
                        hourMultiplier = 1;
                    } else {
                        hourMultiplier = 4;
                    }
                    // fill data loaded from service
                    if (height <= (epg_one_hour_height * hourMultiplier) / 3) {
                        // if (height <= (epg_one_hour_height*hourMultiplier) /
                        // 5) {
                        // if it is very small event just put a focus listener
                        // to
                        // him
                        // btn.setOnFocusChangeListener(new
                        // EPGEventFocusListener(
                        // this, btn, event, true, null));
                        // } else {
                        tvEventName.setText(event.getName());
                        tvEventName.setSingleLine(true);
                        tvEventName.setEllipsize(TruncateAt.MARQUEE);
                        if (NUMBER_OF_HOURS_IN_WINDOW == 8
                                && height <= epg_one_hour_height / 2) {
                            tvEventName
                                    .setTextSize(height * EPG_EVENTS_SCALLER);
                        }
                        if (NUMBER_OF_HOURS_IN_WINDOW == 2
                                && height <= epg_one_hour_height / 8) {
                            tvEventName
                                    .setTextSize(height * EPG_EVENTS_SCALLER);
                        }
                        // set focus listener to button for circular text
                        btn.setOnFocusChangeListener(new EPGEventFocusListener(
                                this, btn, event, true, tvEventName));
                        // }
                    } else {
                        tvEventTime = (TextView) item
                                .findViewById(R.id.textViewEPGEventTime);
                        tvEventDescription = (TextView) item
                                .findViewById(R.id.textViewEPGEventDescription);
                        tvEventName.setText(event.getName());
                        stringBuilder = new StringBuilder();
                        tvEventTime
                                .setText(stringBuilder
                                        .append(DateTimeConversions
                                                .getTimeSting(event
                                                        .getStartTime()
                                                        .getCalendar()
                                                        .getTime()))
                                        .append(" - ")
                                        .append(DateTimeConversions
                                                .getTimeSting(event
                                                        .getEndTime()
                                                        .getCalendar()
                                                        .getTime())));
                        tvEventDescription.setText(event.getDescription());
                        // set focus listener to button for circular text
                        btn.setOnFocusChangeListener(new EPGEventFocusListener(
                                this, btn, event, false, tvEventName));
                    }
                    // set initial focusability and listeners
                    btn.setFocusable(false);
                    // set key listener
                    btn.setOnKeyListener(new EPGKeyListener(this, false, false));
                    if (epgEventsHeights.size() > indexOfList
                            && epgEventsHeights.get(indexOfList).size() == 1) {
                        btn.setOnKeyListener(new EPGKeyListener(this, true,
                                false));
                    }
                    if (epgEventsListCounts.size() > indexOfList
                            && indexOfEvent == epgEventsListCounts
                                    .get(indexOfList) - 1) {
                        btn.setOnKeyListener(new EPGKeyListener(this, false,
                                true));
                    }
                    // set click listener
                    btn.setOnClickListener(new EPGEventClickListener(activity,
                            event, EPGHandlingClass.this));
                    // set tag to button
                    btn.setTag(epgEventsHeights.get(indexOfList).size() - 1);
                    setColorToEPGLayout(layoutForColor, startTime);
                }
            }
        }
        return item;
    }

    public void setColorToEPGLayout(LinearLayout layoutForColor, int startTime) {
        // set side color to EPG view
        switch (startTime % 8) {
            case 0:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.green_normal_epg));
                break;
            case 1:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.yellow_normal_epg));
                break;
            case 2:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.blue_normal_epg));
                break;
            case 3:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.red_normal_epg));
                break;
            case 4:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.pink_normal_epg));
                break;
            case 5:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.white_normal_epg));
                break;
            case 6:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.orange_normal_epg));
                break;
            case 7:
                layoutForColor.setBackgroundColor(activity.getResources()
                        .getColor(R.color.purple_normal_epg));
                break;
            default:
                break;
        }
    }

    /** Return duration in seconds */
    private int getDurationFromTwoTimes(Date startTime, Date endTime) {
        return (int) ((endTime.getTime() - startTime.getTime()) / 1000);
    }

    /** Return height in pixels for duration in seconds */
    private float getEPGEventHeight(int seconds) {
        return ((float) seconds / 3600f) * (float) epg_one_hour_height;
    }

    /** Get date and calculate height of one hour */
    private void getCurrentDate() {
        if (dayInWeekToLoadData == 1) {
            String timeFromStream = "";
            /** Check time from stream */
            try {
                timeFromStreamDate = MainActivity.service.getSystemControl()
                        .getDateAndTimeControl().getTimeDate().getCalendar()
                        .getTime();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            timeFromStream = DateTimeConversions
                    .getDateSting(timeFromStreamDate);
            handlerGUI.sendMessage(Message.obtain(handlerGUI, SET_DATE,
                    timeFromStream));
            // set initial hour to request focus to
            // hourToRequestFocusTo = timeFromStreamDate.getHours();
            Log.d("TIME FROM STREAM", timeFromStream + " timeFromStreamDate: "
                    + timeFromStreamDate.toString());
        }
        // if it is other day than today
        else {
            timeFromStreamDate.setHours(0);
            timeFromStreamDate.setMinutes(0);
            timeFromStreamDate.setSeconds(0);
            // set initial hour to request focus to
            // hourToRequestFocusTo = timeFromStreamDate.getHours();
        }
        checkOneHourHeight();
    }

    /**
     * Function that calculates margins between text views that represent one
     * hour and fills that side view
     */
    private void fillSideTime() {
        /** Create new side layout for time to replace old one in handler */
        LinearLayout layoutForTimeNew = new LinearLayout(activity);
        // set id for side layout
        layoutForTimeNew.setId(R.id.linearLayoutEPGScrollTime);
        // set layout attributes
        layoutForTimeNew.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        layoutForTimeNew.setLayoutParams(params);
        int padding = (int) activity.getResources().getDimension(
                R.dimen.padding_small);
        layoutForTimeNew.setPadding(padding, 0, 0, 0);
        layoutForTimeNew.setGravity(Gravity.CENTER_HORIZONTAL);
        // create layout params with calculated height of one hour
        LayoutParams paramsTV = null;
        if (timeFromStreamDate != null) {
            /** Check step value */
            int step = 0;
            switch (NUMBER_OF_HOURS_IN_WINDOW) {
                case 2: {
                    step = 1;
                    paramsTV = new LayoutParams(LayoutParams.WRAP_CONTENT,
                            epg_one_hour_height);
                    break;
                }
                case 8: {
                    step = 4;
                    paramsTV = new LayoutParams(LayoutParams.WRAP_CONTENT,
                            epg_one_hour_height * 4);
                    break;
                }
                default:
                    break;
            }
            // first for loop goes to 23h
            int i;
            for (i = timeFromStreamDate.getHours(); i < 24; i += step) {
                /** Add vertical text view to layout */
                layoutForTimeNew.addView(inflateSideTimeView(i, paramsTV));
            }
            // second for loop goes to calculated last hour to represent
            for (; i % 24 <= endTimeInt; i += step) {
                /** Add vertical text view to layout */
                layoutForTimeNew.addView(inflateSideTimeView(i % 24, paramsTV));
            }
            // send message to handler to replace old layout with this layout
            handlerGUI.sendMessage(Message.obtain(handlerGUI,
                    ADD_SIDE_TIME_LAYOUT, layoutForTimeNew));
        }
    }

    /** Create view for side time layout */
    private View inflateSideTimeView(int i, LayoutParams paramsTV) {
        /** Create vertical text view and add it to layout */
        LinearLayout tvLayout = (LinearLayout) inflater.inflate(
                R.layout.epg_vertical_text_view, null);
        VerticalTextView verticalTV = (VerticalTextView) tvLayout
                .findViewById(R.id.verticalTextView);
        stringBuilder = new StringBuilder();
        // set text to vertical text view
        Date date = new Date(0, 0, 0, i, 0);
        verticalTV.setText(DateTimeConversions.getTimeSting(date));
        // set params to text view
        tvLayout.setLayoutParams(paramsTV);
        return tvLayout;
    }

    /** Calculate one hour height in pixels */
    private void checkOneHourHeight() {
        switch (NUMBER_OF_HOURS_IN_WINDOW) {
            case 2: {
                epg_one_hour_height = (EPG_WINDOW_HEIGHT / 2);
                break;
            }
            case 8: {
                epg_one_hour_height = (EPG_WINDOW_HEIGHT / 8);
                break;
            }
            default:
                break;
        }
        // Log.d(TAG, "EPG_ONE_HOUR_HEIGHT " + epg_one_hour_height);
    }

    public void goRigth(boolean updateGallery) {
        if (numberOfServices < MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
            if (galleryEPG.getSelectedItemPosition() == numberOfServices - 1) {
                return;
            }
        }
        if (backgroundThread != null) {
            stopThread();
        }
        frameLayoutEPGForSmallEvents.removeAllViews();
        frameLayoutEPGForSmallEvents.refreshDrawableState();
        frameLayoutEPGForSmallEvents.invalidate();
        EPGEventFocusListener.isStarted = false;
        EPGEventFocusListener.handler = null;
        // if (backgroundThread == null) {
        if (updateGallery) {
            // simulate right click on gallery
            galleryEPG.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT,
                    new KeyEvent(0, 0));
        }
        // start thread to load right item
        startThread(runnableThreadRight);
        // }
    }

    public void goLeft(boolean updateGallery) {
        if (numberOfServices < MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
            if (galleryEPG.getSelectedItemPosition() == 0) {
                return;
            }
        }
        if (backgroundThread != null) {
            stopThread();
        }
        frameLayoutEPGForSmallEvents.removeAllViews();
        frameLayoutEPGForSmallEvents.refreshDrawableState();
        frameLayoutEPGForSmallEvents.invalidate();
        EPGEventFocusListener.isStarted = false;
        EPGEventFocusListener.handler = null;
        // if (backgroundThread == null) {
        if (updateGallery) {
            // simulate left click on gallery
            galleryEPG
                    .onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, new KeyEvent(0, 0));
        }
        // start thread to load left item
        startThread(runnableThreadLeft);
        // }
    }

    /**
     * Adapter for lower gallery of EPG
     * 
     * @author Branimir Pavlovic
     */
    public class EPGGalleryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (numberOfServices >= MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
                return Integer.MAX_VALUE;
            } else {
                return numberOfServices;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemPos = 0;
            if (numberOfServices > 0) {
                if (numberOfServices >= MINIMUM_NUMBER_OF_SERVICES_TO_WORK_NORMAL) {
                    itemPos = (position % numberOfServices);
                } else {
                    itemPos = position;
                }
                // Log.d(TAG, "GET VIEW IN GALLERY ITEM POSITION: " + itemPos);
                if (convertView == null) {
                    // get element
                    convertView = (LinearLayout) inflater.inflate(
                            R.layout.multimedia_list_element_grid, null);
                }
                // ExtendedService activeService = null;
                Content activeContent = null;
                // IpContent activeContentIP = null;
                try {
                    activeContent = contentListControl.getContent(itemPos);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                // Log.d(TAG, "SERVICE NAME: " + activeContent.getName()
                // + " INDEX: " + activeContent.getIndex() + " position: "
                // + position);
                // Take references of views
                A4TVTextView contentName = (A4TVTextView) convertView
                        .findViewById(com.iwedia.gui.R.id.contentName);
                ImageView contentImage = (ImageView) convertView
                        .findViewById(com.iwedia.gui.R.id.contentImage);
                A4TVTextView contentNameText = (A4TVTextView) convertView
                        .findViewById(com.iwedia.gui.R.id.contentNameText);
                // set text size
                if (((MainActivity) activity).isFullHD()) {
                    contentName
                            .setTextSize(activity.getResources().getDimension(
                                    R.dimen.content_item_text_size_1080p));
                    contentNameText.setTextSize(activity.getResources()
                            .getDimension(
                                    R.dimen.content_list_no_image_name_1080p));
                } else {
                    contentName.setTextSize(activity.getResources()
                            .getDimension(R.dimen.content_item_text_size));
                    contentNameText.setTextSize(activity.getResources()
                            .getDimension(R.dimen.content_list_no_image_name));
                }
                if (activeContent != null) {
                    stringBuilder = new StringBuilder();
                    if (activeContent instanceof IpContent) {
                        contentName.setText(activity.getResources().getString(
                                R.string.main_menu_content_list_ip)
                                + " : " + activeContent.getName());
                    } else {
                        int index = 0;
                        try {
                            if (contentListControl.getActiveFilterIndex() == FilterType.ALL) {
                                index = contentListControl
                                        .getContentIndexInAllList(activeContent);
                            } else {
                                index = activeContent.getIndex();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        contentName.setText(stringBuilder.append(index + 1)
                                .append(". ").append(activeContent.getName()));
                    }
                    Bitmap bmp = MainActivity.mMemoryCache
                            .loadBitmapFromDisk(ImageManager.getInstance()
                                    .getImageUrl(activeContent.getName()));
                    if (bmp == null) {
                        contentNameText.setText(activeContent.getName());
                    } else {
                        contentImage.setScaleType(ScaleType.FIT_XY);
                        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                                MainActivity.screenWidth / 12,
                                MainActivity.screenHeight / 12);
                        imageParams.gravity = Gravity.CENTER;
                        contentImage.setLayoutParams(imageParams);
                    }
                    contentImage.setImageBitmap(bmp);
                    if (position == numberOfServices - 1) {
                        convertView.findViewById(R.id.contentDividerBig)
                                .setVisibility(View.INVISIBLE);
                        convertView.findViewById(R.id.contentDividerSmall)
                                .setVisibility(View.INVISIBLE);
                    }
                }
            }
            int selectedIndex;
            // try {
            // selectedIndex = MainActivity.service.getContentListControl()
            // .getActiveContent().getIndex();
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            selectedIndex = galleryEPG.getSelectedItemPosition()
                    % numberOfServices;
            if (itemPos == selectedIndex) {
                convertView.setLayoutParams(new Gallery.LayoutParams(2
                        * epgInformationsScreenWidth / GALLERY_ITEM_DIVIDER,
                        android.widget.Gallery.LayoutParams.MATCH_PARENT));
            } else {
                // set layout params to inflated view
                convertView.setLayoutParams(new Gallery.LayoutParams(
                        epgInformationsScreenWidth / GALLERY_ITEM_DIVIDER,
                        android.widget.Gallery.LayoutParams.MATCH_PARENT));
            }
            return convertView;
        }
    }

    /** View that currently has focus in EPG */
    // private View viewThatHasFocus = null;
    public boolean removeViewsFromFrameLayoutEPGForSmallEvents(int tag) {
        if (closeAnim == null) {
            closeAnim = AnimationUtils.loadAnimation(activity,
                    R.anim.epg_event_zoom_out);
            closeAnim.setDuration(TIME_TO_OPEN_SMALL_EVENT_ANIMATION);
            closeAnim.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (frameLayoutEPGForSmallEvents.getChildCount() > 0) {
                        new Handler().post(new Runnable() {
                            public void run() {
                                frameLayoutEPGForSmallEvents.removeAllViews();
                                frameLayoutEPGForSmallEvents
                                        .refreshDrawableState();
                                frameLayoutEPGForSmallEvents.invalidate();
                            }
                        });
                    }
                }
            });
        }
        if (frameLayoutEPGForSmallEvents.getChildCount() > 0) {
            View view = null;
            for (int i = 0; i < frameLayoutEPGForSmallEvents.getChildCount(); i++) {
                // Log.d(TAG, "frameLayoutEPGForSmallEvents count: "
                // + frameLayoutEPGForSmallEvents.getChildCount()
                // + frameLayoutEPGForSmallEvents.getChildAt(i));
                int viewTag = (Integer) frameLayoutEPGForSmallEvents
                        .getChildAt(i).getTag();
                if (viewTag == tag) {
                    view = frameLayoutEPGForSmallEvents.getChildAt(i);
                    break;
                }
            }
            if (view != null) {
                view.startAnimation(closeAnim);
                return true;
            }
        }
        return false;
    }

    private int scrollValue = -1;

    /**
     * Start background thread
     * 
     * @param run
     *        Runnable to run in thread
     */
    public synchronized void startThread(Runnable run) {
        Log.d(TAG, "start thread entered");
        if (backgroundThread == null) {
            // check for left and right
            if (run.equals(runnableThreadLeft)
                    || run.equals(runnableThreadRight)) {
                scrollValue = scrollView.getScrollY();
            } else {
                scrollValue = -1;
            }
            if (!run.equals(runnableAdderThread)) {
                // && run != runnableThreadBackgroundCheck) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                }, 150);
            }
            backgroundThread = new Thread(run);
            backgroundThread.setPriority(Thread.MAX_PRIORITY);
            backgroundThread.start();
        }
    }

    /**
     * Stops background thread
     */
    public synchronized void stopThread() {
        Log.d(TAG, "stop thread entered");
        if (scrollValue >= 0) {
            scrollView.scrollTo(0, scrollValue);
        }
        if (backgroundThread != null) {
            Thread moribund = backgroundThread;
            backgroundThread = null;
            moribund.interrupt();
        }
    }

    /**
     * Create alert dialog that ask user to load new data or not
     * 
     * @param nextDay
     *        Load next day or previous day
     */
    public void createAskDialogAndShow(final boolean nextDay) {
        final A4TVAlertDialog askDialog = new A4TVAlertDialog(activity);
        if (nextDay) {
            askDialog.setTitleOfAlertDialog(R.string.epg_load_next_day);
        } else {
            askDialog.setTitleOfAlertDialog(R.string.epg_load_previous_day);
        }
        askDialog.setCancelable(false);
        // if user clicked positive button
        if (nextDay) {
            // go to the next day
            askDialog.setPositiveButton(R.string.button_text_yes,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dayInWeekToLoadData < 7) {
                                dayInWeekToLoadData++;
                                startTime = addDay(startTime, 1);
                                endTime = addDay(endTime, 1);
                                epgTimeFilter.setTime(startTime, endTime);
                                try {
                                    MainActivity.service.getEpgControl()
                                            .setFilter(
                                                    MainActivity.epgClientId,
                                                    epgTimeFilter);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                stopThread();
                                clearViewsFromScreenAndData();
                                startThread(runnableThreadInitial);
                                scrollView.scrollTo(0, epg_one_hour_height / 4);
                                // Log.d(TAG,
                                // "STRING DATE "
                                // + timeFromStreamDate.toString());
                                // set new start time to next day
                                long milis = timeFromStreamDate.getTime();
                                timeFromStreamDate.setTime(milis
                                        + NUMBER_OF_MILISECONDS_IN_DAY);
                                // Log.d(TAG,
                                // "STRIN DATE "
                                // + timeFromStreamDate.toString());
                                stringBuilder = new StringBuilder();
                                String str = DateTimeConversions
                                        .getDateSting(timeFromStreamDate);
                                // Log.d(TAG,
                                // "STRIN DATE " + str + " YEAR: "
                                // + timeFromStreamDate.getYear()
                                // + " MONTH: "
                                // + timeFromStreamDate.getMonth());
                                // set new day string
                                handlerGUI.sendMessage(Message.obtain(
                                        handlerGUI, SET_DATE, str));
                                endTimeInt = 0;
                            }
                            askDialog.cancel();
                        }
                    });
        } else {
            // go to the previous day
            askDialog.setPositiveButton(R.string.button_text_yes,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dayInWeekToLoadData > 1) {
                                dayInWeekToLoadData--;
                                startTime = addDay(startTime, -1);
                                endTime = addDay(endTime, -1);
                                epgTimeFilter.setTime(startTime, endTime);
                                try {
                                    MainActivity.service.getEpgControl()
                                            .setFilter(
                                                    MainActivity.epgClientId,
                                                    epgTimeFilter);
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                stopThread();
                                clearViewsFromScreenAndData();
                                startThread(runnableThreadInitial);
                                if (dayInWeekToLoadData == 1) {
                                    scrollView.scrollTo(0, 0);
                                } else {
                                    scrollView.scrollTo(0,
                                            epg_one_hour_height / 4);
                                }
                                // set new start time to previous day
                                long milis = timeFromStreamDate.getTime();
                                timeFromStreamDate.setTime(milis
                                        - NUMBER_OF_MILISECONDS_IN_DAY);
                                stringBuilder = new StringBuilder();
                                String str = DateTimeConversions
                                        .getDateSting(timeFromStreamDate);
                                // set new day string
                                handlerGUI.sendMessage(Message.obtain(
                                        handlerGUI, SET_DATE, str));
                                endTimeInt = 0;
                            }
                            askDialog.cancel();
                        }
                    });
        }
        // if user clicked negative button listener
        askDialog.setNegativeButton(R.string.button_text_no,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nextDay) {
                            scrollView.smoothScrollBy(0, -100);
                        } else {
                            scrollView.smoothScrollBy(0, 100);
                        }
                        askDialog.cancel();
                    }
                });
        askDialog.show();
    }

    /**************************** GETTERS AND SETTERS ******************************/
    public int getEpgInformationsScreenWidth() {
        return epgInformationsScreenWidth;
    }

    public ArrayList<LinearLayout> getListViewsEPGContents() {
        return linearLayoutsEPGContents;
    }

    public LinearLayout getMainLayout() {
        return mainLayout;
    }

    public int getDayInWeekToLoadData() {
        return dayInWeekToLoadData;
    }

    public void setDayInWeekToLoadData(int dayInWeekToLoadData) {
        this.dayInWeekToLoadData = dayInWeekToLoadData;
    }

    public ArrayList<LinearLayout> getLinearLayoutsEPGContents() {
        return linearLayoutsEPGContents;
    }

    public Runnable getRunnableThreadRight() {
        return runnableThreadRight;
    }

    public Runnable getRunnableThreadLeft() {
        return runnableThreadLeft;
    }

    public Runnable getRunnableThreadInitial() {
        return runnableThreadInitial;
    }

    public Gallery getGalleryEPG() {
        return galleryEPG;
    }

    public ArrayList<ArrayList<EPGEventShowed>> getEpgEventsHeights() {
        return epgEventsHeights;
    }

    public FrameLayout getFrameLayoutEPGForSmallEvents() {
        return frameLayoutEPGForSmallEvents;
    }

    public int getNumberOfServices() {
        return numberOfServices;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public MainActivity getActivity() {
        return activity;
    }

    public TimeDate addDay(TimeDate time, int day) {
        Calendar c = Calendar.getInstance();
        c = time.getCalendar();
        c.add(Calendar.DATE, day);
        TimeDate timeDate = new TimeDate(c.get(Calendar.SECOND),
                c.get(Calendar.MINUTE), c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
        return timeDate;
    }
    // public View getViewThatHasFocus() {
    // return viewThatHasFocus;
    // }
    //
    // public void setViewThatHasFocus(View viewThatHasFocus) {
    // this.viewThatHasFocus = viewThatHasFocus;
    // }
}
