package com.iwedia.gui.content_list;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.service.Service;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;

import java.util.ArrayList;

/**
 * Content list handler class
 * 
 * @author Veljko Ilkic
 */
public class ContentListHandler {
    public static final String TAG = "ContentListHandler";
    /** Categories of content in content list */
    public static final int RECENTLY_WATCHED = 1;
    public static final int FAVORITES = 2;
    public static final int ALL = 3;
    /** Position of item in page */
    public static int focusPosition = 0;
    /** Number of columns in grids */
    public static final int NUMBER_OF_COLUMNS = 6;
    /** Reference of main activity */
    private Activity activity;
    /** Dialog holder content list */
    private A4TVDialog contentListDialog;
    // /////////////////////////////////////////
    // Views
    // /////////////////////////////////////////
    /** Main layout of content list important for animation */
    private LinearLayout contentListMainLayout;
    /** Scroll view for filtering options */
    private HorizontalScrollView contentFilterOptionsScroll;
    /** Layout holder for filter buttons */
    private LinearLayout filterButtonsHolder;
    /** Scroll views in content list */
    private GridView gridViewRecently, gridViewFavorites, gridViewAll;
    /** Dividers in grid view */
    private ImageView recentlyGridDivider, favortiteGridDivider,
            allFirstGridDivider, allSecondGridDivider, allThirdGridDivider;
    /** Arrow images */
    private ImageView contentRecentlyArrowLeft, contentRecentlyArrowRight,
            contentFavoriteArrowLeft, contentFavoriteArrowRight,
            contentAllArrowLeft, contentAllArrowRight;
    /** Animation in */
    private Animation translationLeftIn;
    /** Animation out */
    private Animation translationLeftOut;
    /** Handlers for drawing items */
    private RecentlyHandler recentlyHandler;
    private FavoriteHandler favoriteHandler;
    private AllHandler allHandler;
    /** Layout inflater for filter buttons */
    private LayoutInflater inflater;
    // //////////////////////////////////////////////////////////////
    // Dynamic filtering fields
    // ///////////////////////////////////////////////////////////////
    /** Value of current selected filtering option in list */
    public static int currentSelectedFilterOption = 0;
    /** Number of possible filter options */
    public static final int numberOfFilterOptions = 5;
    /** Number of favorite lists (user defined and op profile) */
    public static int numberOfServiceLists = 0;
    public static final int MAX_NUMBER_OF_FAVORITE_LISTS = 30;
    /** Filter options ids */
    private static boolean all_tab_option;
    private static boolean ip_tab_option;
    private static boolean input_tab_option;
    private static boolean apps_tab_option;
    private static boolean widgets_tab_option;
    /** Store last accessed filter */
    public static int CONTENT_LIST_LAST_FILTER = 0;
    /** Store active filter on content list show */
    public static int CONTENT_LIST_ON_OPEN_FILTER = 0;
    /** Screen width */
    private int screenWidth;
    /** Width measure unit */
    private int widthMeasureUnit;
    /** Width measure unit divider */
    public static final int widthMeasureUnitDivider = 12;
    /** Available service lists filter options */
    private Boolean[] serviceListsTabOptions;
    /** Array list of filter buttons */
    private ArrayList<LinearLayout> filterButtons = new ArrayList<LinearLayout>();
    protected static final int NUMBER_OF_MILISECONDS_FOR_SERVICECHANGE = 1000;
    // TODO: Applies on main channel only
    private int mDisplayId = 0;

    /** Constructor 1 */
    public ContentListHandler(Activity activity) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Get layout iflater
        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            numberOfServiceLists = MainActivity.service.getContentListControl()
                    .getNumberOfServiceLists();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // Init filter option array
        initFilterOptionArray();
    }

    /** Default values of filter option array */
    private void initFilterOptionArray() {
        serviceListsTabOptions = new Boolean[MAX_NUMBER_OF_FAVORITE_LISTS];
        all_tab_option = true;
        ip_tab_option = ConfigHandler.IP;
        input_tab_option = ConfigHandler.TV_FEATURES;
        apps_tab_option = true;
        widgets_tab_option = true;
        // Init favorite lists filters flags
        for (int i = 1; i < numberOfServiceLists; i++) {
            serviceListsTabOptions[i] = true;
        }
        try {
            MainActivity.service.getContentListControl().refreshServiceLists();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** Reinit filter option array if service list number has been changed */
    public void reinitFilterOptionArray() {
        Log.d(TAG, "reinitFilterOptionArray");
        int newNumberOfFavoriteLists = numberOfServiceLists;
        // Get number of service list
        try {
            newNumberOfFavoriteLists = MainActivity.service
                    .getContentListControl().getNumberOfServiceLists();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        for (int i = 1; i < newNumberOfFavoriteLists; i++) {
            serviceListsTabOptions[i] = true;
        }
        numberOfServiceLists = newNumberOfFavoriteLists;
        // Redraw filter option array
        loadFilterOptionsLayout();
    }

    /** Init function */
    public void init() {
        // Get screen width of activity
        screenWidth = activity.getWindowManager().getDefaultDisplay()
                .getWidth();
        // Calculate width unit for filter buttons
        widthMeasureUnit = screenWidth / widthMeasureUnitDivider;
        // /////////////////////////////////////////
        // Dialog
        // /////////////////////////////////////////
        // Create dialog holder for content list menu
        contentListDialog = ((MainActivity) activity).getDialogManager()
                .getContentDialog();
        // Attach key listener
        if (contentListDialog != null)
            contentListDialog.setOnKeyListener(new MainKeyListener(
                    (MainActivity) activity));
        // //////////////////////////////////////////////
        // Views
        // //////////////////////////////////////////////
        // Init views in content list dialog
        // if (((MainActivity) activity).isFullHD() && contentListDialog !=
        // null) {
        // A4TVTextView topBanner = (A4TVTextView) contentListDialog
        // .findViewById(R.id.contentListText);
        // topBanner.setTextSize(activity.getResources().getDimension(
        // com.iwedia.gui.R.dimen.a4tvdialog_button_text_size_1080p));
        // }
        // Take reference of main content list layout
        if (contentListDialog != null)
            contentListMainLayout = (LinearLayout) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.contentListMainLayout);
        // Take reference of horizontal scroll view for filter options
        if (contentListDialog != null)
            contentFilterOptionsScroll = (HorizontalScrollView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.contentFilterOptionsScroll);
        // contentFilterOptionsScroll.setEnabled(false);
        // contentFilterOptionsScroll.setFocusable(false);
        // contentFilterOptionsScroll.setClickable(false);
        // Take reference of filter buttons holder layout
        if (contentListDialog != null)
            filterButtonsHolder = (LinearLayout) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.contentListInputFilterBanner);
        // Load filter buttons
        loadFilterOptionsLayout();
        // //////////////////////////////////////
        // Grid dividers
        // //////////////////////////////////////
        if (contentListDialog != null) {
            recentlyGridDivider = (ImageView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.recentlyGridDivider);
            favortiteGridDivider = (ImageView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.favoriteGridDivider);
            allFirstGridDivider = (ImageView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.allItemFirstGridDivider);
            allSecondGridDivider = (ImageView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.allItemSecondGridDivider);
            allThirdGridDivider = (ImageView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.allItemThirdGridDivider);
        }
        setUpDividers();
        // ////////////////////////////////////////////
        // Grid arrows
        // ////////////////////////////////////////////
        if (contentListDialog != null) {
            contentRecentlyArrowLeft = (ImageView) contentListDialog
                    .findViewById(R.id.contentRecentlyListLeftArrow);
            contentRecentlyArrowRight = (ImageView) contentListDialog
                    .findViewById(R.id.contentRecentlyListRightArrow);
            contentFavoriteArrowLeft = (ImageView) contentListDialog
                    .findViewById(R.id.contentFavoriteListLeftArrow);
            contentFavoriteArrowRight = (ImageView) contentListDialog
                    .findViewById(R.id.contentFavoriteListRightArrow);
            contentAllArrowLeft = (ImageView) contentListDialog
                    .findViewById(R.id.contentAllListLeftArrow);
            contentAllArrowRight = (ImageView) contentListDialog
                    .findViewById(R.id.contentAllListRightArrow);
        }
        // /////////////////////////////////
        // Grid views and drawing handlers
        // /////////////////////////////////
        if (contentListDialog != null)
            gridViewRecently = (GridView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.recentlyWatchedItemsGrid);
        if (gridViewRecently != null) {
            recentlyHandler = new RecentlyHandler(activity, gridViewRecently);
        }
        if (recentlyHandler != null) {
            recentlyHandler.initView();
        }
        if (contentListDialog != null)
            gridViewFavorites = (GridView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.favoriteItemsGrid);
        favoriteHandler = new FavoriteHandler(activity, gridViewFavorites);
        favoriteHandler.initView();
        if (contentListDialog != null)
            gridViewAll = (GridView) contentListDialog
                    .findViewById(com.iwedia.gui.R.id.allItemsGrid);
        if (gridViewAll != null) {
            allHandler = new AllHandler(activity, gridViewAll);
        }
        if (allHandler != null) {
            allHandler.initView();
        }
        // ///////////////////////////////////////////////////
        // Attach onLong press listener of grid views
        // ///////////////////////////////////////////////////
        if (recentlyHandler != null)
            recentlyHandler.getGridRecently().setOnItemLongClickListener(
                    new GridOnLongPress(activity,
                            GridOnLongPress.RECENTLY_CONTENT_LIST));
        favoriteHandler.getGridFavorite().setOnItemLongClickListener(
                new GridOnLongPress(activity,
                        GridOnLongPress.FAVORITE_CONTENT_LIST));
        if (allHandler != null)
            allHandler.getGridAll().setOnItemLongClickListener(
                    new GridOnLongPress(activity,
                            GridOnLongPress.ALL_CONTENT_LIST));
        // //////////////////////////////////////////////
        // Animations
        // //////////////////////////////////////////////
        // Load animation opening and closing
        translationLeftIn = AnimationUtils.loadAnimation(activity,
                com.iwedia.gui.R.anim.translate_left_channel_list);
        translationLeftOut = AnimationUtils.loadAnimation(activity,
                com.iwedia.gui.R.anim.translate_left_exit_channel_list);
        // Animation listener for in animation
        translationLeftIn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                contentListMainLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        // Animation listener for out animation
        translationLeftOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Close content list
                contentListDialog.cancel();
                // Open main menu if needed
                if (MainKeyListener.contentListFromMainMenu) {
                    if (MainKeyListener.getAppState() != MainKeyListener.CLEAN_SCREEN) {
                        ((MainActivity) activity).getMainMenuHandler()
                                .showMainMenu();
                        // ////////////////////////////////////////////////
                        // Gallery unscaled and unrotated images bug fix
                        // ////////////////////////////////////////////////
                        Handler delay = new Handler();
                        delay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ((MainActivity) activity).getMainMenuHandler()
                                        .getA4TVOnSelectLister()
                                        .startAnimationsManual();
                            }
                        }, 50);
                    }
                    MainKeyListener.contentListFromMainMenu = false;
                } else {
                    MainKeyListener.returnToStoredAppState();
                }
            }
        });
    }

    /** Show content list dialog on screen */
    public void showContentList() {
        // Show content list
        MainKeyListener.setAppState(MainKeyListener.CONTENT_LIST);
        // Reinit filter option array if needed
        reinitFilterOptionArray();
        // Invisible main layout
        if (MainActivity.enabledAnimations) {
            contentListMainLayout.setVisibility(View.INVISIBLE);
        }
        // Show content list dialog
        if (contentListDialog != null) {
            contentListDialog.show();
        }
        // Start animation
        if (MainActivity.enabledAnimations) {
            animateInContentListDialog();
        }
        try {
            CONTENT_LIST_ON_OPEN_FILTER = MainActivity.service
                    .getContentListControl().getActiveFilterIndex();
            currentSelectedFilterOption = ContentListHandler.CONTENT_LIST_ON_OPEN_FILTER;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /** Hide content list dialog from screen */
    public void closeContentList() {
        // Animation for closing content list
        if (MainActivity.enabledAnimations) {
            animateOutContentListDialog();
        } else {
            contentListDialog.cancel();
        }
    }

    /** In animation for content list */
    public void animateInContentListDialog() {
        // Animate views
        contentListMainLayout.startAnimation(translationLeftIn);
    }

    /** Out animation for main menu */
    public void animateOutContentListDialog() {
        // Animate out
        contentListMainLayout.startAnimation(translationLeftOut);
    }

    /** Include Inputs in as filtering option */
    public void enableInputs() {
        input_tab_option = true;
    }

    /** Include IP in as filtering option */
    public void enableIP() {
        ip_tab_option = true;
    }

    /** Load fiter opitons layout */
    private void loadFilterOptionsLayout() {
        // Clear lists of filter buttons
        filterButtonsHolder.removeAllViews();
        filterButtons.clear();
        if (all_tab_option) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton allButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            // allButton.setFocusable(false);
            allButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_filter_all));
            // if (((MainActivity) activity).isFullHD()) {
            // allButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // allButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            // Attach click listener
            allButton.setOnClickListener(new FilterOnClick(FilterType.ALL));
            allButton.setOnLongClickListener(new TabOnLongPress(activity, 0));
            // Attach key listener on filter options list
            allButton.setOnKeyListener(new FilterOptionsKeyListener());
            setFilterItemParams(filterItem);
            filterItem.setTag(FilterType.ALL);
            filterButtons.add(filterItem);
            filterButtonsHolder.addView(filterItem);
        }
        // //////////////////////////////////////////////
        // Add favorite tabs
        // //////////////////////////////////////////////
        for (int i = 1; i < numberOfServiceLists; i++) {
            if (serviceListsTabOptions[i]) {
                LinearLayout filterItem = (LinearLayout) inflater.inflate(
                        com.iwedia.gui.R.layout.content_list_filter_item, null);
                A4TVButton serviceListButton = (A4TVButton) filterItem
                        .findViewById(com.iwedia.gui.R.id.filterButton);
                try {
                    IContentFilter contentFilter = MainActivity.service
                            .getContentListControl().getContentFilter(i);
                    serviceListButton.setText(contentFilter
                            .getContentListName());
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // if (((MainActivity) activity).isFullHD()) {
                // favoriteButton
                // .setTextSize(activity
                // .getResources()
                // .getDimension(
                // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
                // } else {
                // favoriteButton
                // .setTextSize(activity
                // .getResources()
                // .getDimension(
                // com.iwedia.gui.R.dimen.content_filter_text_size));
                // }
                Drawable img = activity.getResources().getDrawable(
                        R.drawable.operator_profile_filter_option);
                img.setBounds(0, 0, 23, 18);
                serviceListButton.setCompoundDrawables(img, null, null, null);
                serviceListButton.setPadding(
                        (int) activity.getResources().getDimension(
                                com.iwedia.gui.R.dimen.content_filter_padding),
                        0, 0, 0);
                serviceListButton.setOnClickListener(new FilterOnClick(i));
                serviceListButton.setOnLongClickListener(new TabOnLongPress(
                        activity, i));
                // Attach key listener on filter options list
                serviceListButton
                        .setOnKeyListener(new FilterOptionsKeyListener());
                setFilterItemParams(filterItem);
                filterItem.setTag(i);
                filterButtons.add(filterItem);
                filterButtonsHolder.addView(filterItem);
            }
        }
        if (ip_tab_option) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton dataButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            // dataButton.setFocusable(false);
            dataButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_ip));
            // if (((MainActivity) activity).isFullHD()) {
            // dataButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // dataButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = activity.getResources().getDrawable(
                    R.drawable.ip_filter_option);
            img.setBounds(0, 0, 23, 18);
            dataButton.setCompoundDrawables(img, null, null, null);
            dataButton.setPadding(
                    (int) activity.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            dataButton.setOnClickListener(new FilterOnClick(
                    FilterType.IP_STREAM));
            dataButton.setOnLongClickListener(new TabOnLongPress(activity,
                    FilterType.IP_STREAM));
            // Attach key listener on filter options list
            dataButton.setOnKeyListener(new FilterOptionsKeyListener());
            setFilterItemParams(filterItem);
            filterItem.setTag(FilterType.IP_STREAM);
            filterButtons.add(filterItem);
            filterButtonsHolder.addView(filterItem);
        }
        if (input_tab_option) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton inputButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            // inputButton.setFocusable(false);
            inputButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_inputs));
            // if (((MainActivity) activity).isFullHD()) {
            // inputButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // inputButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = activity.getResources().getDrawable(
                    R.drawable.inputs_filter_option);
            img.setBounds(0, 0, 23, 18);
            inputButton.setCompoundDrawables(img, null, null, null);
            inputButton.setPadding(
                    (int) activity.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            inputButton
                    .setOnClickListener(new FilterOnClick(FilterType.INPUTS));
            inputButton.setOnLongClickListener(new TabOnLongPress(activity,
                    FilterType.INPUTS));
            // Attach key listener on filter options list
            inputButton.setOnKeyListener(new FilterOptionsKeyListener());
            setFilterItemParams(filterItem);
            filterItem.setTag(FilterType.INPUTS);
            filterButtons.add(filterItem);
            filterButtonsHolder.addView(filterItem);
        }
        if (apps_tab_option) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton appsButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            // appsButton.setFocusable(false);
            appsButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_apps));
            // if (((MainActivity) activity).isFullHD()) {
            // appsButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // appsButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = activity.getResources().getDrawable(
                    R.drawable.apps_filter_option);
            img.setBounds(0, 0, 23, 18);
            appsButton.setCompoundDrawables(img, null, null, null);
            appsButton.setPadding(
                    (int) activity.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            appsButton.setOnClickListener(new FilterOnClick(FilterType.APPS));
            appsButton.setOnLongClickListener(new TabOnLongPress(activity,
                    FilterType.APPS));
            // Attach key listener on filter options list
            appsButton.setOnKeyListener(new FilterOptionsKeyListener());
            setFilterItemParams(filterItem);
            filterItem.setTag(FilterType.APPS);
            filterButtons.add(filterItem);
            filterButtonsHolder.addView(filterItem);
        }
        if (widgets_tab_option) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton widgetsButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            // widgetsButton.setFocusable(false);
            widgetsButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_widgets));
            // if (((MainActivity) activity).isFullHD()) {
            // widgetsButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // widgetsButton.setTextSize(activity.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = activity.getResources().getDrawable(
                    R.drawable.widget_filter_option);
            img.setBounds(0, 0, 23, 18);
            widgetsButton.setCompoundDrawables(img, null, null, null);
            widgetsButton.setPadding(
                    (int) activity.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            widgetsButton.setOnClickListener(new FilterOnClick(
                    FilterType.WIDGETS));
            widgetsButton.setOnLongClickListener(new TabOnLongPress(activity,
                    FilterType.WIDGETS));
            // Attach key listener on filter options list
            widgetsButton.setOnKeyListener(new FilterOptionsKeyListener());
            setFilterItemParams(filterItem);
            filterItem.setTag(FilterType.WIDGETS);
            filterButtons.add(filterItem);
            filterButtonsHolder.addView(filterItem);
        }
        // Remove divider from last filter button
        ImageView filterDivider = (ImageView) filterButtons.get(
                filterButtons.size() - 1).findViewById(
                com.iwedia.gui.R.id.filterButtonDivider);
        filterDivider.setBackgroundColor(Color.TRANSPARENT);
        filterButtonsHolder.invalidate();
    }

    /** Customize filter item */
    private void setFilterItemParams(LinearLayout filterItem) {
        LinearLayout.LayoutParams filterItemParams = new LinearLayout.LayoutParams(
                widthMeasureUnit, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        filterItem.setLayoutParams(filterItemParams);
        // filterItem.setEnabled(false);
        // filterItem.setFocusable(false);
        filterItem.setClickable(true);
        filterItem.setGravity(Gravity.CENTER);
    }

    /** Filter content of content list */
    public void filterContent(final int filter, boolean isInitial) {
        int currentSelectedFilterButton = 0;
        // Deselect all options and find index of filter option in
        // list
        for (int i = 0; i < filterButtons.size(); i++) {
            filterButtons.get(i).setSelected(false);
            if ((Integer) filterButtons.get(i).getTag() == filter) {
                currentSelectedFilterButton = i;
            }
        }
        filterButtons.get(currentSelectedFilterButton).setSelected(true);
        // Proceed with filtering and loading content
        try {
            MainActivity.service.getContentListControl().setActiveFilter(
                    (Integer) filterButtons.get(currentSelectedFilterButton)
                            .getTag());
            // ZORANA!
            // OProfile - must send enter when oppening favorite list, and
            // exit if exiting favorite list ...
            // TODO - find a way to determine is it favorite list without math
            // ... this is bad approach
            // it should be easy to change size of fav lists - see how to make
            // this offset less hardcoded
            // move this to service - when favorite is set to active - send this
            // notification
            int serviceListIndex = MainActivity.service.getContentListControl()
                    .getActiveContentFilter().getServiceListIndex();
            if (serviceListIndex >= ServiceListIndex.FAVORITE) {
                // in case it is not closed ...
                MainActivity.service.getCIControl().exitOperatorProfile();
                MainActivity.service.getCIControl().enterOperatorProfile(
                        serviceListIndex);
            } else {
                MainActivity.service.getCIControl().exitOperatorProfile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestContentItems((Integer) filterButtons.get(
                currentSelectedFilterButton).getTag());
    }

    /** Select filter option */
    public void selectFilter(final int filterType) {
        Log.d(TAG, "selectFilter - filterType: " + filterType);
        // Return focus position on zero
        focusPosition = 0;
        currentSelectedFilterOption = filterType;
        // Load filter
        filterContent((currentSelectedFilterOption), false);
    }

    /** Sync filter indexes */
    public static void syncFilterIndexes(final boolean changeFilter) {
        // Store last active filter
        try {
            if (changeFilter == false) {
                MainActivity.service.getContentListControl().setActiveFilter(
                        ContentListHandler.CONTENT_LIST_ON_OPEN_FILTER);
                ContentListHandler.CONTENT_LIST_LAST_FILTER = ContentListHandler.CONTENT_LIST_ON_OPEN_FILTER;
            } else {
                ContentListHandler.CONTENT_LIST_LAST_FILTER = currentSelectedFilterOption;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Request new content items from service
     * 
     * @param initLoad
     *        If content list data needs to be initialized
     */
    public void requestContentItems(final int filter) {
        if (MainActivity.service != null) {
            try {
                // Load number of contents per sublist
                RecentlyHandler.recentlyNumberOfItems = MainActivity.service
                        .getContentListControl().getRecenltyWatchedListSize();
                FavoriteHandler.favoriteNumberOfItems = MainActivity.service
                        .getContentListControl().getFavoritesSize();
                AllHandler.allNumberOfItems = MainActivity.service
                        .getContentListControl().getContentListSizeVisible();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // ////////////////////////////////////////
            // Prepare data for focusing
            // ////////////////////////////////////////
            // //////////////////////////////////////
            // Set current visible screen
            // //////////////////////////////////////
            recentlyHandler.setCurrentPage(0);
            favoriteHandler.setCurrentPage(0);
            int position = setCurrentScreen(filter);
            // //////////////////////////////////////
            // Recently watched
            // //////////////////////////////////////
            recentlyHandler.initData();
            // //////////////////////////////////////
            // Favorites
            // //////////////////////////////////////
            favoriteHandler.initData();
            // //////////////////////////////////////
            // All
            // //////////////////////////////////////
            allHandler.initData();
            allHandler.focusActiveElement(position);
            // Focus content
            // focusContent(filter, position);
        } else {
            // ////////////////////////////////
            // Show error message
            // ////////////////////////////////
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(com.iwedia.gui.R.string.proxy_service_is_null);
                }
            });
        }
    }

    /** Set current screen in all list */
    private int setCurrentScreen(int filter) {
        // Get active content from service
        Content activeContent;
        try {
            activeContent = MainActivity.service.getContentListControl()
                    .getActiveContent(mDisplayId);
            // Position
            int position = 0;
            // Check if active content exists
            if (activeContent != null) {
                switch (filter) {
                    case FilterType.ALL:
                        position = MainActivity.service.getContentListControl()
                                .getContentIndexInAllList(activeContent);
                        break;
                    case FilterType.INPUTS:
                        if (activeContent.getFilterType() == FilterType.INPUTS) {
                            position = activeContent.getIndex();
                        } else {
                            position = 0;
                        }
                        break;
                    case FilterType.IP_STREAM:
                    case FilterType.APPS:
                    case FilterType.WIDGETS:
                        position = 0;
                        break;
                    default:
                        Service activeService = MainActivity.service
                                .getServiceControl().getActiveService();
                        if (activeService.getListIndex() != filter) {
                            position = 0;
                        } else {
                            position = activeService.getServiceIndex();
                        }
                        break;
                }
            }
            allHandler.setCurrentScreen(position);
            return position;
        } catch (Exception e) {
            allHandler.setCurrentScreen(0);
            e.printStackTrace();
        }
        return 0;
    }

    /** Focus content in content list */
    public void focusContent(int filter) {
        // //////////////////////////////////////
        // Focus active tv service
        // //////////////////////////////////////
        if (filter == FilterType.ALL) {
            // Current active index service
            int currentActiveIndex = 0;
            try {
                currentActiveIndex = MainActivity.service
                        .getContentListControl().getActiveContent(mDisplayId)
                        .getIndex();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Focus active tv service
            allHandler.focusActiveElement(currentActiveIndex);
        } else {
            // Focus first element
            allHandler.focusActiveElement(0);
        }
    }

    // /** Check if content is active */
    // public boolean isContentActive(Content content, int displayId) {
    //
    // // ////////////////////////////////////////
    // // TV, Data or Radio Service content
    // // /////////////////////////////////////////
    //
    // // TODO check for IP
    // if (content instanceof ServiceContent || content instanceof IpContent) {
    //
    // try {
    // return content.equals(MainActivity.service
    // .getContentListControl().getActiveContent(displayId));
    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // }
    // }
    //
    // // /////////////////////////////////////////////
    // // Widget content
    // // /////////////////////////////////////////////
    //
    // if (content.getFilterType() == FilterType.WIDGETS) {
    // if (((MainActivity) activity).getWidgetsHandler()
    // .checkWidgetVisibility(content)) {
    // return true;
    // } else {
    // return false;
    // }
    // }
    //
    // return false;
    // }
    /** Hide navigation arrows from recently list */
    public void hideArrowsRecentlyList() {
        contentRecentlyArrowRight.setVisibility(View.INVISIBLE);
        contentRecentlyArrowLeft.setVisibility(View.INVISIBLE);
    }

    /** Show navigation arrows in recently list */
    public void showArrowsRecentlyList() {
        contentRecentlyArrowRight.setVisibility(View.VISIBLE);
        contentRecentlyArrowLeft.setVisibility(View.VISIBLE);
    }

    /** Hide navigation arrows from favorite list */
    public void hideArrowsFavoriteList() {
        contentFavoriteArrowRight.setVisibility(View.INVISIBLE);
        contentFavoriteArrowLeft.setVisibility(View.INVISIBLE);
    }

    /** Show navigation arrows in favorite list */
    public void showArrowsFavoriteList() {
        contentFavoriteArrowRight.setVisibility(View.VISIBLE);
        contentFavoriteArrowLeft.setVisibility(View.VISIBLE);
    }

    /** Hide navigation arrows from all list */
    public void hideArrowsAllList() {
        contentAllArrowRight.setVisibility(View.INVISIBLE);
        contentAllArrowLeft.setVisibility(View.INVISIBLE);
    }

    /** Show navigation arrows in all list */
    public void showArrowsAllList() {
        contentAllArrowRight.setVisibility(View.VISIBLE);
        contentAllArrowLeft.setVisibility(View.VISIBLE);
    }

    /** Set up position of grid dividers on screen */
    public void setUpDividers() {
        // Check resolution and add layout params
        // //////////////////////////
        // 720p
        // //////////////////////////
        if (MainActivity.screenWidth == 1280) {
            FrameLayout.LayoutParams recentlyDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            recentlyDivParams.topMargin = 35;
            if (recentlyGridDivider != null) {
                recentlyGridDivider.setLayoutParams(recentlyDivParams);
            }
            FrameLayout.LayoutParams favoriteDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            favoriteDivParams.topMargin = 35;
            if (favortiteGridDivider != null) {
                favortiteGridDivider.setLayoutParams(favoriteDivParams);
            }
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 35;
            if (allFirstGridDivider != null) {
                allFirstGridDivider.setLayoutParams(firstDivParams);
            }
            // PE Android4TV
            if (MainActivity.screenHeight != MainActivity.SCREEN_HEIGHT_720P) {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 117;
                if (allSecondGridDivider != null) {
                    allSecondGridDivider.setLayoutParams(secondDivParams);
                }
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 152;
                if (allThirdGridDivider != null) {
                    allThirdGridDivider.setLayoutParams(thirdDivParams);
                }
            }
            // AMP Android4TV
            else {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 125;
                if (allSecondGridDivider != null) {
                    allSecondGridDivider.setLayoutParams(secondDivParams);
                }
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 162;
                if (allThirdGridDivider != null) {
                    allThirdGridDivider.setLayoutParams(thirdDivParams);
                }
            }
        }
        // ////////////////////////////////
        // 1080p
        // ////////////////////////////////
        if (MainActivity.screenWidth == 1920) {
            FrameLayout.LayoutParams recentlyDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            recentlyDivParams.topMargin = 50;
            if (recentlyGridDivider != null) {
                recentlyGridDivider.setLayoutParams(recentlyDivParams);
            }
            FrameLayout.LayoutParams favoriteDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            favoriteDivParams.topMargin = 50;
            if (favortiteGridDivider != null) {
                favortiteGridDivider.setLayoutParams(favoriteDivParams);
            }
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 50;
            if (allFirstGridDivider != null) {
                allFirstGridDivider.setLayoutParams(firstDivParams);
            }
            FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            secondDivParams.topMargin = 194;
            if (allSecondGridDivider != null) {
                allSecondGridDivider.setLayoutParams(secondDivParams);
            }
            FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            thirdDivParams.topMargin = 244;
            if (allThirdGridDivider != null) {
                allThirdGridDivider.setLayoutParams(thirdDivParams);
            }
        }
    }

    // //////////////////////////////////////////////////
    // Getters and Setters
    // //////////////////////////////////////////////////
    /** Get reference of content list dialog */
    public A4TVDialog getContentListDialog() {
        return contentListDialog;
    }

    /** Get reference of recently handler */
    public RecentlyHandler getRecentlyHandler() {
        return recentlyHandler;
    }

    /** Get reference of favorite handler */
    public FavoriteHandler getFavoriteHandler() {
        return favoriteHandler;
    }

    /** Get reference of all handler */
    public AllHandler getAllHandler() {
        return allHandler;
    }

    /** Are inputs enabled */
    public boolean areInputsEnabled() {
        return input_tab_option;
    }

    /** OnClick listener for filter buttons */
    private class FilterOnClick implements OnClickListener {
        private int tag;

        public FilterOnClick(int tag) {
            this.tag = tag;
        }

        @Override
        public void onClick(View v) {
            selectFilter(tag);
        }
    }

    private class FilterOptionsKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (((MainActivity) activity).getContentListHandler()
                            .getRecentlyHandler().getGridRecently()
                            .isFocusable()) {
                        ((MainActivity) activity).getContentListHandler()
                                .getRecentlyHandler().focusActiveElement(0);
                        return true;
                    } else if (((MainActivity) activity)
                            .getContentListHandler().getFavoriteHandler()
                            .getGridFavorite().isFocusable()) {
                        ((MainActivity) activity).getContentListHandler()
                                .getFavoriteHandler().focusActiveElement(0);
                        return true;
                    } else if (((MainActivity) activity)
                            .getContentListHandler().getAllHandler()
                            .getGridAll().isFocusable()) {
                        ((MainActivity) activity).getContentListHandler()
                                .getAllHandler().focusActiveElement(0);
                        return true;
                    }
                } else {
                    return false;
                }
            }
            return false;
        }
    }

    // //////////////////////////////////
    // Grid arrows
    // //////////////////////////////////
    /** Get left arrow for recently grid */
    public ImageView getContentRecentlyArrowLeft() {
        return contentRecentlyArrowLeft;
    }

    /** Get right arrow for recently grid */
    public ImageView getContentRecentlyArrowRight() {
        return contentRecentlyArrowRight;
    }

    /** Get left arrow for favorite grid */
    public ImageView getContentFavoriteArrowLeft() {
        return contentFavoriteArrowLeft;
    }

    /** Get right arrow for favorite grid */
    public ImageView getContentFavoriteArrowRight() {
        return contentFavoriteArrowRight;
    }

    /** Get left arrow for favorite grid */
    public ImageView getContentAllArrowLeft() {
        return contentAllArrowLeft;
    }

    /** Get right arrow for all grid */
    public ImageView getContentAllArrowRight() {
        return contentAllArrowRight;
    }

    public HorizontalScrollView getContentFilterOptionsScroll() {
        return contentFilterOptionsScroll;
    }

    public ArrayList<LinearLayout> getFilterButtons() {
        return filterButtons;
    }
}
