package com.iwedia.gui.multimedia;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ViewFlipper;

import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVInfoDescriptionScrollView;
import com.iwedia.gui.components.A4TVInfoDescriptionScrollView.Scrolled;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.pvr.PVRHandler;

import java.util.ArrayList;

/**
 * Handler for multimedia dialog
 * 
 * @author Veljko Ilkic
 */
public class MultimediaHandler implements OSDGlobal {
    public static final String TAG = "MultimediaHandler";
    /** Multimedia modes */
    public static final int MULTIMEDIA_FIRST_SCREEN = 1;
    public static final int MULTIMEDIA_SECOND_SCREEN = 2;
    public static final int MULTIMEDIA_PVR_SCREEN = 3;
    /** Current multimedia mode */
    public static int multimediaScreen = MULTIMEDIA_FIRST_SCREEN;
    /** Number of columns in grids */
    public static final int NUMBER_OF_COLUMNS = 6;
    /** Reference of main activity */
    private Activity activity;
    /** Dialog holder multimedia */
    private A4TVDialog multimediaDialog;
    /** Dialog for showing multimedia content */
    private A4TVDialog multimediaShowDialog;
    /** Multimedia show handler object */
    private MultimediaShowHandler multimediaShowHandler;
    /** Music from DLNA layout */
    private LinearLayout musicFromDlnaLayout;
    private ImageView imageViewMusicReproduction;
    private A4TVTextView textViewLyrics;
    private A4TVInfoDescriptionScrollView scrollViewLyrics;
    // /////////////////////////////////////////
    // Views
    // /////////////////////////////////////////
    private ViewFlipper multimediaViewFlipper;
    // //////////////////////////////////////////////////
    // First screen
    // //////////////////////////////////////////////////
    /** Main layout of multimedia important for animation */
    @SuppressWarnings("unused")
    private LinearLayout multimediaFirstMainLayout;
    /** Scroll view for filtering options */
    private HorizontalScrollView multimediaFilterOptionsScroll;
    /** Layout holder for filter buttons */
    private LinearLayout filterButtonsHolderFirst;
    /** Scroll views in multimedia first screen */
    private GridView gridViewRecentlyFirstScreen, gridViewFavoritesFirstScreen,
            gridViewFileBrowserFirstScreen;
    /** Dividers in grid view */
    private ImageView firstScreenRecentlyGridDivider,
            firstScreenFavortiteGridDivider,
            firstScreenBrowserFirstGridDivider,
            firstScreenBrowserSecondGridDivider,
            firstScreenBrowserThirdGridDivider;
    /** Navigation arrows */
    private ImageView firstScreenRecentlyLeftArrow,
            firstScreenRecentlyRightArrow, firstScreenFavoriteLeftArrow,
            firstScreenFavoriteRightArrow, firstScreenAllLeftArrow,
            firstScreenAllRightArrow;
    /** Handlers for drawing items */
    private MultimediaRecentlyHandler multimediaRecentlyHandler;
    private MultimediaFavoriteHandler mutlimediaFavoriteHandler;
    private MultimediaFileBrowserHandler multimediaFileBrowserFirstHandler;
    // //////////////////////////////////////////////////////////
    // Second screen
    // //////////////////////////////////////////////////////////
    public static int secondScreenFolderLevel = 0;
    /** Main layout of multimedia important for animation */
    @SuppressWarnings("unused")
    private LinearLayout multimediaSecondMainLayout;
    /** Scroll view for filtering options */
    private HorizontalScrollView multimediaSecondFilterOptionsScroll;
    /** Layout holder for filter buttons */
    private LinearLayout filterSecondButtonsHolder;
    /** Scroll views in multimedia second screen */
    private GridView gridViewPathSecondScreen, gridViewFileBrowserSecondScreen;
    /** Dividers in grid view */
    private ImageView secondScreenFilePathGridDivider,
            secondScreenBrowserFirstGridDivider,
            secondScreenBrowserSecondGridDivider,
            secondScreenBrowserThirdGridDivider,
            secondScreenBrowserFourthGridDivider,
            secondScreenBrowserFifthGridDivider;
    /** Navigation arrows */
    private ImageView secondScreenFilePathLeftArrow,
            secondScreenFilePathRightArrow, secondScreenAllLeftArrow,
            secondScreenAllRightArrow;
    /** Handler for second screen */
    private MultimediaFilePathHandler filePathHandler;
    private MultimediaFileBrowserHandler multimediaFileBrowserSecondHandler;
    // //////////////////////////////////////////////////////////
    // PVR screen
    // //////////////////////////////////////////////////////////
    /** Main layout of pvr multimedia important for animation */
    @SuppressWarnings("unused")
    private LinearLayout multimediaPvrMainLayout;
    /** Scroll view for filtering options */
    private HorizontalScrollView multimediaPvrFilterOptionsScroll;
    /** Layout holder for filter buttons */
    private LinearLayout filterPvrButtonsHolder;
    /** Scroll views in multimedia pvr screen */
    private GridView gridViewFileBrowserPvrScreen;
    /** PVR file info */
    private A4TVTextView pvrFileInfo1, pvrFileInfo2, pvrFileInfo3,
            pvrFileInfo4;
    public static A4TVTextView pvrFileBrowserText;
    /** Dividers in grid view */
    private ImageView pvrScreenBrowserFirstGridDivider,
            pvrScreenBrowserSecondGridDivider,
            pvrScreenBrowserThirdGridDivider,
            pvrScreenBrowserFourthGridDivider,
            pvrScreenBrowserFifthGridDivider;
    /** Navigation arrows */
    private ImageView pvrScreenAllLeftArrow, pvrScreenAllRightArrow;
    /** Handler for PVR screen */
    private MultimediaFileBrowserHandler multimediaFileBrowserPvrHandler;
    /** Animation for content list */
    @SuppressWarnings("unused")
    private Animation alphaScaleIn;
    /** Fade out animation for multimedia closing */
    @SuppressWarnings("unused")
    private Animation alphaScaleOut;
    /** Layout inflater for filter buttons */
    private LayoutInflater inflater;
    // //////////////////////////////////////////////////////////////
    // Dynamic filtering fields
    // ///////////////////////////////////////////////////////////////
    /** Value of current selected filtering option in list */
    public static int currentSelectedFilterOptionMultimedia = 0;
    public static int currentSelectedFilterOptionPvr = 0;
    /** Number of possible filter options */
    public static final int numberOfFilterOptionsMultimedia = 1;
    public static final int numberOfFilterOptionsPvr = 2;
    /** Filter options ids */
    public static final int FILTER_MULTIMEDIA = 0;
    public static final int FILTER_MULTIMEDIA_OFFSET = 100;
    /** Filter options PVR */
    public static final int FILTER_PVR_OPTION_INDEX_OFFSET = 110;
    public static final int FILTER_RECORD_PVR_OPTION = 110;
    public static final int FILTER_SCHEDULE_PVR_OPTION = 111;
    /** Screen width */
    private int screenWidth;
    /** Width measure unit */
    private int widthMeasureUnit;
    /** Width measure unit divider */
    public static final int widthMeasureUnitDivider = 12;
    /** Available filter options */
    private Boolean[] filterOptionsMultimedia = new Boolean[numberOfFilterOptionsMultimedia];
    private Boolean[] filterOptionsPvr = new Boolean[numberOfFilterOptionsPvr];
    /** Array list of filter buttons */
    private ArrayList<LinearLayout> filterButtonsFirst = new ArrayList<LinearLayout>();
    private ArrayList<LinearLayout> filterButtonsSecond = new ArrayList<LinearLayout>();
    private ArrayList<LinearLayout> filterButtonsPvr = new ArrayList<LinearLayout>();
    /** Progress dialog for loading data */
    private A4TVProgressDialog progressDialog;
    private LinearLayout linearLayoutSortedBy;
    /** Multimedia action constants */
    public static final int LOAD_BACK_LEVEL = 1;
    public static final int LOAD_BACK_FIRST_SCREEN = 2;
    public static final int LOAD_BACK_FIRST_SCREEN_FROM_PVR = 3;
    public static final int REOPEN_MULTIMEDIA = 4;
    public static String[] sortPvrFilesBy = { "NAME", "DATE", "DURATION" };
    public static String[] sortPvrFilesByOrder = { "ASCENDING", "DESCENDING" };
    private LinearLayout linearLayoutSortedPlaylistItemsByTitle;
    private LinearLayout linearLayoutSortedPlaylistItemsByArtist;
    private LinearLayout linearLayoutSortedPlaylistItemsByDuration;
    private boolean detectScrollLyricsEnd = false;
    private int scrollLyricsValue = 0;

    /** Constructor 1 */
    public MultimediaHandler(Activity activity) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Get layout iflater
        this.inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Init filter option array
        initFilterOptionArray();
        // Create progress dialog object
        progressDialog = new A4TVProgressDialog(activity);
        progressDialog.setTitleOfAlertDialog(R.string.loading_data);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(R.string.please_wait);
    }

    /** Default values of filter option array */
    private void initFilterOptionArray() {
        // ///////////////////////////////
        // Multimedia
        // ///////////////////////////////
        filterOptionsMultimedia[FILTER_MULTIMEDIA] = true;
        // ////////////////////////////////
        // PVR
        // ////////////////////////////////
        filterOptionsPvr[FILTER_RECORD_PVR_OPTION
                - FILTER_PVR_OPTION_INDEX_OFFSET] = true;
        filterOptionsPvr[FILTER_SCHEDULE_PVR_OPTION
                - FILTER_PVR_OPTION_INDEX_OFFSET] = true;
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
        multimediaDialog = ((MainActivity) activity).getDialogManager()
                .getMultimediaDialog();
        // Attach key listener
        if (multimediaDialog != null)
            multimediaDialog.setOnKeyListener(new MainKeyListener(
                    (MainActivity) activity));
        // //////////////////////////////////////////////
        // Views
        // //////////////////////////////////////////////
        // ///////////////////////////////////////////
        // Get music from dlna layout
        // ///////////////////////////////////////////
        musicFromDlnaLayout = (LinearLayout) activity
                .findViewById(R.id.musicReproductionFromDlnaLayout);
        imageViewMusicReproduction = (ImageView) activity
                .findViewById(R.id.imageViewMusicReproduction);
        textViewLyrics = (A4TVTextView) activity
                .findViewById(R.id.textViewLyrics);
        scrollViewLyrics = (A4TVInfoDescriptionScrollView) activity
                .findViewById(R.id.scrollViewLyrics);
        scrollViewLyrics.setScrooled(new ScrollLyrics());
//        if (((MainActivity) activity).isFullHD()) {
//            if (multimediaDialog != null) {
//                A4TVTextView topBanner = (A4TVTextView) multimediaDialog
//                        .findViewById(R.id.multimediatSecondText);
//                topBanner
//                        .setTextSize(activity
//                                .getResources()
//                                .getDimension(
//                                        com.iwedia.gui.R.dimen.a4tvdialog_button_text_size_1080p));
//                topBanner = (A4TVTextView) multimediaDialog
//                        .findViewById(R.id.multimediatText);
//                topBanner
//                        .setTextSize(activity
//                                .getResources()
//                                .getDimension(
//                                        com.iwedia.gui.R.dimen.a4tvdialog_button_text_size_1080p));
//            }
//        }
        if (multimediaDialog != null) {
            multimediaViewFlipper = (ViewFlipper) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaViewFlipper);
            // ////////////////////////////////////////////////
            // First screen
            // ////////////////////////////////////////////////
            // Take reference of main content list layout
            multimediaFirstMainLayout = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaMainLayout);
            // Take reference of horizontal scroll view for filter options
            multimediaFilterOptionsScroll = (HorizontalScrollView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaFilterOptionsScroll);
            // Take reference of filter buttons holder layout
            filterButtonsHolderFirst = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaInputFilterBanner);
            // Load filter buttons
            loadFilterOptionsLayout(filterButtonsHolderFirst, true);
            // //////////////////////////////////////
            // Navigation arrows first screen
            // //////////////////////////////////////
            firstScreenRecentlyLeftArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaRecentlyLeftArrow);
            firstScreenRecentlyRightArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaRecentlyRightArrow);
            firstScreenFavoriteLeftArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaFavoriteLeftArrow);
            firstScreenFavoriteRightArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaFavoriteRightArrow);
            firstScreenAllLeftArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaFirstAllLeftArrow);
            firstScreenAllRightArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaFirstAllRightArrow);
            // //////////////////////////////////////
            // Grid dividers
            // //////////////////////////////////////
            firstScreenRecentlyGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaRecentlyGridDivider);
            firstScreenFavortiteGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaFavoriteGridDivider);
            firstScreenBrowserFirstGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaAllItemFirstGridDivider);
            firstScreenBrowserSecondGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaAllItemSecondGridDivider);
            firstScreenBrowserThirdGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaAllItemThirdGridDivider);
            setUpDividersFirstScreen();
            // /////////////////////////////////
            // Grid views and drawing handlers
            // /////////////////////////////////
            gridViewRecentlyFirstScreen = (GridView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaRecentlyWatchedItemsGrid);
            multimediaRecentlyHandler = new MultimediaRecentlyHandler(activity,
                    gridViewRecentlyFirstScreen);
            multimediaRecentlyHandler.initView();
            gridViewFavoritesFirstScreen = (GridView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaFavoriteItemsGrid);
            mutlimediaFavoriteHandler = new MultimediaFavoriteHandler(activity,
                    gridViewFavoritesFirstScreen, false);
            mutlimediaFavoriteHandler.initView();
            gridViewFileBrowserFirstScreen = (GridView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaAllItemsGrid);
            multimediaFileBrowserFirstHandler = new MultimediaFileBrowserHandler(
                    activity, gridViewFileBrowserFirstScreen,
                    MULTIMEDIA_FIRST_SCREEN);
            multimediaFileBrowserFirstHandler.initView();
            // ////////////////////////////////////////
            // Attach onLong listener
            // ////////////////////////////////////////
            multimediaRecentlyHandler
                    .getMultimediaGridRecently()
                    .setOnItemLongClickListener(
                            new MultimediaGridOnLongPress(
                                    activity,
                                    MULTIMEDIA_FIRST_SCREEN,
                                    MultimediaGridOnLongPress.RECENTLY_MULTIMEDIA));
            mutlimediaFavoriteHandler
                    .getMultimediaGridFavorite()
                    .setOnItemLongClickListener(
                            new MultimediaGridOnLongPress(
                                    activity,
                                    MULTIMEDIA_FIRST_SCREEN,
                                    MultimediaGridOnLongPress.FAVORITE_MULTIMEDIA));
            multimediaFileBrowserFirstHandler
                    .getGridFileBrowser()
                    .setOnItemLongClickListener(
                            new MultimediaGridOnLongPress(
                                    activity,
                                    MULTIMEDIA_FIRST_SCREEN,
                                    MultimediaGridOnLongPress.FILE_BROWSER_MULTIMEDIA));
            // ////////////////////////////////////////////
            // Second screen
            // ////////////////////////////////////////////
            // Init views in multimedia second
            // Take reference of main content list layout
            multimediaSecondMainLayout = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondMainLayout);
            // Take reference of horizontal scroll view for filter options
            multimediaSecondFilterOptionsScroll = (HorizontalScrollView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondFilterOptionsScroll);
            multimediaSecondFilterOptionsScroll.setEnabled(false);
            multimediaSecondFilterOptionsScroll.setFocusable(false);
            // Take reference of filter buttons holder layout
            filterSecondButtonsHolder = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondInputFilterBanner);
            // Load filter buttons
            loadFilterOptionsLayout(filterSecondButtonsHolder, false);
            // //////////////////////////////////////
            // Navigation arrows first screen
            // //////////////////////////////////////
            secondScreenFilePathLeftArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaSecondPathLeftArrow);
            secondScreenFilePathRightArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaSecondPathRightArrow);
            secondScreenAllLeftArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaSecondAllLeftArrow);
            secondScreenAllRightArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaSecondAllRightArrow);
            // //////////////////////////////////////
            // Grid dividers
            // //////////////////////////////////////
            secondScreenFilePathGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondPathBrowserGridDivider);
            secondScreenBrowserFirstGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondFileBrowserItemFirstGridDivider);
            secondScreenBrowserSecondGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondFileBrowserItemSecondGridDivider);
            secondScreenBrowserThirdGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondFileBrowserItemThirdGridDivider);
            secondScreenBrowserFourthGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondFileBrowserItemFourthGridDivider);
            secondScreenBrowserFifthGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondFileBrowserItemFifthGridDivider);
            setUpDividersSecondScreen();
            // /////////////////////////////////
            // Grid views and drawing handlers
            // /////////////////////////////////
            gridViewPathSecondScreen = (GridView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondPathBrowserItemsGrid);
            filePathHandler = new MultimediaFilePathHandler(activity,
                    gridViewPathSecondScreen);
            filePathHandler.initView();
            gridViewFileBrowserSecondScreen = (GridView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaSecondFileBrowserItemsGrid);
            multimediaFileBrowserSecondHandler = new MultimediaFileBrowserHandler(
                    activity, gridViewFileBrowserSecondScreen,
                    MULTIMEDIA_SECOND_SCREEN);
            multimediaFileBrowserSecondHandler.initView();
            // ///////////////////////////////////////////
            // Attach onLong listeners
            // ///////////////////////////////////////////
            filePathHandler.getGridFilePath().setOnItemLongClickListener(
                    new MultimediaGridOnLongPress(activity,
                            MULTIMEDIA_SECOND_SCREEN,
                            MultimediaGridOnLongPress.FILE_PATH_MULTIMEDIA));
            multimediaFileBrowserSecondHandler
                    .getGridFileBrowser()
                    .setOnItemLongClickListener(
                            new MultimediaGridOnLongPress(
                                    activity,
                                    MULTIMEDIA_SECOND_SCREEN,
                                    MultimediaGridOnLongPress.FILE_BROWSER_MULTIMEDIA));
            // ///////////////////////////////////////////
            // PVR Screen
            // ///////////////////////////////////////////
            // Take reference of main content list layout
            multimediaPvrMainLayout = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrMainLayout);
            // Take reference of horizontal scroll view for filter options
            multimediaPvrFilterOptionsScroll = (HorizontalScrollView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFilterOptionsScroll);
            multimediaPvrFilterOptionsScroll.setEnabled(false);
            multimediaPvrFilterOptionsScroll.setFocusable(false);
            // Take reference of filter buttons holder layout
            filterPvrButtonsHolder = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrInputFilterBanner);
            // Load filter buttons
            loadFilterOptionsPvrLayout(filterPvrButtonsHolder);
            // Take reference of linear layout for sorting pvr files
            linearLayoutSortedBy = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.linearLayoutSortedBy);
            linearLayoutSortedPlaylistItemsByTitle = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.linearLayoutSortedPlaylistItemsByTitle);
            linearLayoutSortedPlaylistItemsByArtist = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.linearLayoutSortedPlaylistItemsByArtist);
            linearLayoutSortedPlaylistItemsByDuration = (LinearLayout) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.linearLayoutSortedPlaylistItemsByDuration);
            // ///////////////////////////////////////
            // Navigation arrows
            // ///////////////////////////////////////
            pvrScreenAllLeftArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaPvrAllLeftArrow);
            pvrScreenAllRightArrow = (ImageView) multimediaDialog
                    .findViewById(R.id.multimediaPvrAllRightArrow);
            // //////////////////////////////////////
            // Grid dividers
            // //////////////////////////////////////
            pvrScreenBrowserFirstGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFileBrowserItemFirstGridDivider);
            pvrScreenBrowserSecondGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFileBrowserItemSecondGridDivider);
            pvrScreenBrowserThirdGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFileBrowserItemThirdGridDivider);
            pvrScreenBrowserFourthGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFileBrowserItemFourthGridDivider);
            pvrScreenBrowserFifthGridDivider = (ImageView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFileBrowserItemFifthGridDivider);
            setUpDividersPvrScreen();
            // /////////////////////////////////
            // Grid views and drawing handlers
            // /////////////////////////////////
            gridViewFileBrowserPvrScreen = (GridView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFileBrowserItemsGrid);
            multimediaFileBrowserPvrHandler = new MultimediaFileBrowserHandler(
                    activity, gridViewFileBrowserPvrScreen,
                    MULTIMEDIA_PVR_SCREEN);
            multimediaFileBrowserPvrHandler.initView();
            // //////////////////////////////////////////////////////////////
            // Attach onLong listeners
            // //////////////////////////////////////////////////////////////
            multimediaFileBrowserPvrHandler
                    .getGridFileBrowser()
                    .setOnItemLongClickListener(
                            new MultimediaGridOnLongPress(
                                    activity,
                                    MULTIMEDIA_PVR_SCREEN,
                                    MultimediaGridOnLongPress.FILE_BROWSER_MULTIMEDIA));
            // /////////////////////////////////////////////////////////////////
            // Get references of info views
            // /////////////////////////////////////////////////////////////////
            pvrFileInfo1 = (A4TVTextView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPVRInfoText1);
            pvrFileInfo2 = (A4TVTextView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPVRInfoText2);
            pvrFileInfo3 = (A4TVTextView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPVRInfoText3);
            pvrFileInfo4 = (A4TVTextView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPVRInfoText4);
            // ///////////////////////////////////////////
            // Multimedia show dialog
            // ///////////////////////////////////////////
            pvrFileBrowserText = (A4TVTextView) multimediaDialog
                    .findViewById(com.iwedia.gui.R.id.multimediaPvrFileBrowserText);
            multimediaShowDialog = ((MainActivity) activity).getDialogManager()
                    .getMultimediaShowDialog();
            if (multimediaShowDialog != null)
                multimediaShowHandler = new MultimediaShowHandler(activity,
                        multimediaShowDialog);
        }
    }

    /** Show multimedia first dialog on screen */
    public void showMultimedia() {
        // Show multimedia list dialog
        multimediaDialog.show();
        multimediaViewFlipper.setDisplayedChild(0);
        // Update multimedia state
        multimediaScreen = MULTIMEDIA_FIRST_SCREEN;
        MainKeyListener.setAppState(MainKeyListener.MULTIMEDIA_FIRST);
    }

    /** Close multimedia first dialog from screen */
    public void closeMultimedia() {
        multimediaDialog.cancel();
        MainKeyListener.returnToStoredAppState();
    }

    /** Hide multimedia first dialog from screen */
    public void hideMultimedia() {
        multimediaDialog.hide();
    }

    /** Show multimedia second dialog on screen */
    public void showMultimediaSecond() {
        // Show second screen
        multimediaViewFlipper.setDisplayedChild(1);
        multimediaScreen = MULTIMEDIA_SECOND_SCREEN;
        MainKeyListener.setAppState(MainKeyListener.MULTIMEDIA_SECOND);
    }

    /** Hide multimedia second dialog from screen */
    public void closeMultimediaSecond() {
        // Show first screen
        multimediaViewFlipper.setDisplayedChild(0);
    }

    /** Show multimedia pvr on screen */
    public void showMultimediaPvr() {
        // Show second screen
        multimediaViewFlipper.setDisplayedChild(2);
        multimediaScreen = MULTIMEDIA_PVR_SCREEN;
        MainKeyListener.setAppState(MainKeyListener.MULTIMEDIA_PVR);
    }

    /** Hide multimedia pvr from screen */
    public void closeMultimediaPvr() {
        // Show first screen
        multimediaViewFlipper.setDisplayedChild(0);
    }

    /** Show multimedia show dialog */
    public void showMultimediaShow() {
        if (multimediaShowDialog != null) {
            multimediaShowDialog.show();
        }
    }

    /** Hide multimedia show dialog */
    public void closeMultimediaShow() {
        if (multimediaShowDialog != null) {
            multimediaShowDialog.cancel();
        }
    }

    /** Load filter options layout */
    private void loadFilterOptionsLayout(LinearLayout holderFilterButtons,
            boolean init) {
        // Clear lists of filter buttons
        holderFilterButtons.removeAllViews();
        if (init) {
            filterButtonsFirst.clear();
        } else {
            filterButtonsSecond.clear();
        }
        if (filterOptionsMultimedia[FILTER_MULTIMEDIA]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton allButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            allButton.setFocusable(false);
            allButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_filter_all));
//            if (((MainActivity) activity).isFullHD()) {
//                allButton.setTextSize(activity.getResources().getDimension(
//                        com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
//            } else {
//                allButton.setTextSize(activity.getResources().getDimension(
//                        com.iwedia.gui.R.dimen.content_filter_text_size));
//            }
            setFilterItemParams(filterItem);
            if (init) {
                filterButtonsFirst.add(filterItem);
            } else {
                filterButtonsSecond.add(filterItem);
            }
            filterItem.setTag(FILTER_MULTIMEDIA);
            holderFilterButtons.addView(filterItem);
        }
        if (init) {
            // Remove divider from last filter button
            ImageView filterDivider = (ImageView) filterButtonsFirst.get(
                    filterButtonsFirst.size() - 1).findViewById(
                    com.iwedia.gui.R.id.filterButtonDivider);
            filterDivider.setBackgroundColor(Color.TRANSPARENT);
        } else {
            // Remove divider from last filter button
            ImageView filterDivider = (ImageView) filterButtonsSecond.get(
                    filterButtonsSecond.size() - 1).findViewById(
                    com.iwedia.gui.R.id.filterButtonDivider);
            filterDivider.setBackgroundColor(Color.TRANSPARENT);
        }
        holderFilterButtons.invalidate();
    }

    /** Load filter options for pvr filter options */
    private void loadFilterOptionsPvrLayout(LinearLayout holderFilterButtons) {
        // Clear lists of filter buttons
        holderFilterButtons.removeAllViews();
        filterButtonsPvr.clear();
        // ////////////////////////////
        // Record
        // ////////////////////////////
        if (filterOptionsPvr[FILTER_RECORD_PVR_OPTION
                - FILTER_PVR_OPTION_INDEX_OFFSET]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton recordButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            recordButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.multimedia_pvr_record));
//            if (((MainActivity) activity).isFullHD()) {
//                recordButton.setTextSize(activity.getResources().getDimension(
//                        com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
//            } else {
//                recordButton.setTextSize(activity.getResources().getDimension(
//                        com.iwedia.gui.R.dimen.content_filter_text_size));
//            }
            recordButton.setOnClickListener(new FilterOnClick(
                    FILTER_RECORD_PVR_OPTION));
            // Attach key listener on filter options list
            recordButton.setOnKeyListener(new FilterOptionsKeyListener());
            setFilterItemParams(filterItem);
            filterButtonsPvr.add(filterItem);
            filterItem.setTag(FILTER_RECORD_PVR_OPTION);
            holderFilterButtons.addView(filterItem);
        }
        // //////////////////////////////////
        // Schedule
        // //////////////////////////////////
        if (filterOptionsPvr[FILTER_SCHEDULE_PVR_OPTION
                - FILTER_PVR_OPTION_INDEX_OFFSET]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton scheduleButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            scheduleButton.setText(activity.getResources().getString(
                    com.iwedia.gui.R.string.multimedia_pvr_schedule));
            scheduleButton.setOnClickListener(new FilterOnClick(
                    FILTER_SCHEDULE_PVR_OPTION));
            // Attach key listener on filter options list
            scheduleButton.setOnKeyListener(new FilterOptionsKeyListener());
            // if (((MainActivity) activity).isFullHD()) {
            // scheduleButton
            // .setTextSize(activity
            // .getResources()
            // .getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // scheduleButton
            // .setTextSize(activity
            // .getResources()
            // .getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            setFilterItemParams(filterItem);
            filterButtonsPvr.add(filterItem);
            filterItem.setTag(FILTER_SCHEDULE_PVR_OPTION);
            holderFilterButtons.addView(filterItem);
        }
        // Remove divider from last filter button
        ImageView filterDivider = (ImageView) filterButtonsPvr.get(
                filterButtonsPvr.size() - 1).findViewById(
                com.iwedia.gui.R.id.filterButtonDivider);
        filterDivider.setBackgroundColor(Color.TRANSPARENT);
        holderFilterButtons.invalidate();
    }

    /** Customize filter item */
    private void setFilterItemParams(LinearLayout filterItem) {
        LinearLayout.LayoutParams filterItemParams = new LinearLayout.LayoutParams(
                widthMeasureUnit, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        filterItem.setLayoutParams(filterItemParams);
        filterItem.setClickable(true);
        filterItem.setGravity(Gravity.CENTER);
    }

    /** Filter content of content list */
    public void filterContent(int filter) {
        // ////////////////////////////////
        // Multimedia screens
        // /////////////////////////////////
        if (multimediaScreen == MULTIMEDIA_FIRST_SCREEN
                || multimediaScreen == MULTIMEDIA_SECOND_SCREEN) {
            // Deselect all options and find index of filter option in list
            for (int i = 0; i < filterButtonsFirst.size(); i++) {
                filterButtonsFirst.get(i).setSelected(false);
                filterButtonsSecond.get(i).setSelected(false);
                if ((Integer) filterButtonsFirst.get(i).getTag() == filter) {
                    // Take value in local variable of current selected option
                    // for
                    // filtering
                    currentSelectedFilterOptionMultimedia = i;
                }
            }
            // Select required one
            filterButtonsFirst.get(currentSelectedFilterOptionMultimedia)
                    .setSelected(true);
            filterButtonsFirst.get(currentSelectedFilterOptionMultimedia)
                    .setFocusable(true);
            filterButtonsFirst.get(currentSelectedFilterOptionMultimedia)
                    .requestFocus();
            filterButtonsFirst.get(currentSelectedFilterOptionMultimedia)
                    .setFocusable(false);
            filterButtonsSecond.get(currentSelectedFilterOptionMultimedia)
                    .setSelected(true);
            filterButtonsSecond.get(currentSelectedFilterOptionMultimedia)
                    .setFocusable(true);
            filterButtonsSecond.get(currentSelectedFilterOptionMultimedia)
                    .requestFocus();
            filterButtonsSecond.get(currentSelectedFilterOptionMultimedia)
                    .setFocusable(false);
            // Proceed with filtering and loading content
            try {
                MainActivity.service.getContentListControl().setActiveFilter(
                        filter + FILTER_MULTIMEDIA_OFFSET);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        // ////////////////////////////////////////
        // PVR screen
        // ////////////////////////////////////////
        if (multimediaScreen == MULTIMEDIA_PVR_SCREEN) {
            // Deselect all options and find index of filter option in list
            for (int i = 0; i < filterButtonsPvr.size(); i++) {
                filterButtonsPvr.get(i).setSelected(false);
                if ((Integer) filterButtonsPvr.get(i).getTag() == filter) {
                    // Take value in local variable of current selected option
                    // for
                    // filtering
                    currentSelectedFilterOptionPvr = i;
                }
            }
            filterButtonsPvr.get(currentSelectedFilterOptionPvr).setSelected(
                    true);
            // Proceed with filtering and loading content
            try {
                MainActivity.service.getContentListControl().setActiveFilter(
                        filter);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    /** Select next filter option */
    public void selectNextFilter() {
        // ////////////////////////////////////////
        // Multimedia screens
        // ////////////////////////////////////////
        if (multimediaScreen == MULTIMEDIA_FIRST_SCREEN
                || multimediaScreen == MULTIMEDIA_SECOND_SCREEN) {
            // Calculate next filter
            currentSelectedFilterOptionMultimedia++;
            if (currentSelectedFilterOptionMultimedia >= numberOfFilterOptionsMultimedia) {
                currentSelectedFilterOptionMultimedia = 0;
            }
            // Load filter
            filterContent((Integer) filterButtonsFirst.get(
                    currentSelectedFilterOptionMultimedia).getTag());
        }
        // ///////////////////////////////////////////
        // PVR screen
        // ///////////////////////////////////////////
        else {
            // Calculate next filter
            currentSelectedFilterOptionPvr++;
            if (currentSelectedFilterOptionPvr >= numberOfFilterOptionsPvr) {
                currentSelectedFilterOptionPvr = 0;
            }
            // Load filter
            filterContent((Integer) filterButtonsPvr.get(
                    currentSelectedFilterOptionPvr).getTag());
        }
        // Request items
        requestContentItems(0);
    }

    /** Select previous filter option */
    public void selectPreviousFilter() {
        // ////////////////////////////////////////
        // Multimedia screens
        // ////////////////////////////////////////
        if (multimediaScreen == MULTIMEDIA_FIRST_SCREEN
                || multimediaScreen == MULTIMEDIA_SECOND_SCREEN) {
            // Calculate next filter
            currentSelectedFilterOptionMultimedia--;
            if (currentSelectedFilterOptionMultimedia < 0) {
                currentSelectedFilterOptionMultimedia = numberOfFilterOptionsMultimedia - 1;
            }
            // Load filter
            filterContent((Integer) filterButtonsFirst.get(
                    currentSelectedFilterOptionMultimedia).getTag());
        }
        // ///////////////////////////////////////////
        // PVR screen
        // ///////////////////////////////////////////
        else {
            // Calculate next filter
            currentSelectedFilterOptionPvr--;
            if (currentSelectedFilterOptionPvr < 0) {
                currentSelectedFilterOptionPvr = numberOfFilterOptionsPvr - 1;
            }
            // Load filter
            filterContent((Integer) filterButtonsPvr.get(
                    currentSelectedFilterOptionPvr).getTag());
        }
        // Request items
        requestContentItems(0);
    }

    /**
     * Request new content items from service
     * 
     * @param initLoad
     *        If content list data needs to be initialized
     */
    public void requestContentItems(int currentPageFileBrowser) {
        if (MainActivity.service != null) {
            if (multimediaScreen == MULTIMEDIA_FIRST_SCREEN) {
                // ///////////////////////////////////
                // Request first screen
                // ///////////////////////////////////
                try {
                    // Load number of contents per sublist
                    MultimediaRecentlyHandler.multimediaRecentlyNumberOfItems = MainActivity.service
                            .getContentListControl()
                            .getRecenltyWatchedListSize();
                    MultimediaFavoriteHandler.multimediaFavoriteNumberOfItems = MainActivity.service
                            .getContentListControl().getFavoritesSize();
                    MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                            .getContentListControl().getContentListSize();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                // ////////////////////////////////////////
                // Prepare data for focusing
                // ////////////////////////////////////////
                multimediaRecentlyHandler.setCurrentPage(0);
                mutlimediaFavoriteHandler.setCurrentPage(0);
                multimediaFileBrowserFirstHandler
                        .setCurrentPage(currentPageFileBrowser);
                // //////////////////////////////////////
                // Recently watched
                // //////////////////////////////////////
                multimediaRecentlyHandler.initData();
                // //////////////////////////////////////
                // Favorites
                // //////////////////////////////////////
                mutlimediaFavoriteHandler.initData();
                // //////////////////////////////////////
                // File browser
                // //////////////////////////////////////
                multimediaFileBrowserFirstHandler.initData();
                // //////////////////////////////////////
                // Focus active element
                // //////////////////////////////////////
                multimediaFileBrowserFirstHandler.focusActiveElement(0);
            } else {
                // ///////////////////////////
                // Second screen request
                // ////////////////////////////
                if (multimediaScreen == MULTIMEDIA_SECOND_SCREEN) {
                    try {
                        MultimediaFilePathHandler.filePathNumberOfItems = MainActivity.service
                                .getContentListControl().getPathSize();
                        MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                .getContentListControl().getContentListSize();
                        // ////////////////////////////////////////
                        // Prepare data for focusing
                        // ////////////////////////////////////////
                        filePathHandler.setCurrentPage(0);
                        multimediaFileBrowserSecondHandler
                                .setCurrentPage(currentPageFileBrowser);
                        // //////////////////////////////////////
                        // File path
                        // //////////////////////////////////////
                        filePathHandler.initData();
                        // //////////////////////////////////////
                        // File browser
                        // //////////////////////////////////////
                        multimediaFileBrowserSecondHandler.initData();
                        // //////////////////////////////////////
                        // Focus active tv service
                        // //////////////////////////////////////
                        multimediaFileBrowserSecondHandler
                                .focusActiveElement(0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                // ////////////////////////////////////////
                // PVR screen
                // ////////////////////////////////////////
                else {
                    try {
                        // Check config file and if USB drive is attached
                        if (ConfigHandler.PVR_STORAGE_STRING
                                .equalsIgnoreCase(ConfigHandler.USB_TEXT)) {
                            // Check if USB is detected
                            if (PVRHandler.detectUSB()) {
                                // Load real number of PVR contents
                                MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                        .getContentListControl()
                                        .getContentListSize();
                            } else {
                                // Don't load number of content from NAND
                                MultimediaFileBrowserHandler.fileBrowserNumberOfItems = 0;
                                // No usb drive
                                new A4TVToast(activity)
                                        .showToast(R.string.pvr_no_usb);
                            }
                        } else {
                            // Get list size
                            MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                    .getContentListControl()
                                    .getContentListSize();
                        }
                        // ////////////////////////////////////////
                        // Prepare data for focusing
                        // ////////////////////////////////////////
                        multimediaFileBrowserPvrHandler.setCurrentPage(0);
                        multimediaFileBrowserPvrHandler.initData();
                        multimediaFileBrowserPvrHandler.focusActiveElement(0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
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

    /** Hide recently navigation arrows from first screen */
    public void hideFirstScreenRecentlyArrows() {
        firstScreenRecentlyLeftArrow.setVisibility(View.INVISIBLE);
        firstScreenRecentlyRightArrow.setVisibility(View.INVISIBLE);
    }

    /** Show recently navigation arrows from first screen */
    public void showFirstScreenRecentlyArrows() {
        firstScreenRecentlyLeftArrow.setVisibility(View.VISIBLE);
        firstScreenRecentlyRightArrow.setVisibility(View.VISIBLE);
    }

    /** Hide favorite navigation arrows from first screen */
    public void hideFirstScreenFavoriteArrows() {
        firstScreenFavoriteLeftArrow.setVisibility(View.INVISIBLE);
        firstScreenFavoriteRightArrow.setVisibility(View.INVISIBLE);
    }

    /** Show favorite navigation arrows from first screen */
    public void showFirstScreenFavoriteArrows() {
        firstScreenFavoriteLeftArrow.setVisibility(View.VISIBLE);
        firstScreenFavoriteRightArrow.setVisibility(View.VISIBLE);
    }

    /** Hide all navigation arrows from first screen */
    public void hideFirstScreenAllArrows() {
        firstScreenAllLeftArrow.setVisibility(View.INVISIBLE);
        firstScreenAllRightArrow.setVisibility(View.INVISIBLE);
    }

    /** Show favorite navigation arrows from first screen */
    public void showFirstScreenAllArrows() {
        firstScreenAllLeftArrow.setVisibility(View.VISIBLE);
        firstScreenAllRightArrow.setVisibility(View.VISIBLE);
    }

    /** Set up position of grid dividers on first screen */
    public void setUpDividersFirstScreen() {
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
            firstScreenRecentlyGridDivider.setLayoutParams(recentlyDivParams);
            FrameLayout.LayoutParams favoriteDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            favoriteDivParams.topMargin = 35;
            firstScreenFavortiteGridDivider.setLayoutParams(favoriteDivParams);
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 35;
            firstScreenBrowserFirstGridDivider.setLayoutParams(firstDivParams);
            // PE Android4TV
            if (MainActivity.screenHeight != MainActivity.SCREEN_HEIGHT_720P) {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 117;
                firstScreenBrowserSecondGridDivider
                        .setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 152;
                firstScreenBrowserThirdGridDivider
                        .setLayoutParams(thirdDivParams);
            }
            // AMP Android4TV
            else {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 125;
                firstScreenBrowserSecondGridDivider
                        .setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 162;
                firstScreenBrowserThirdGridDivider
                        .setLayoutParams(thirdDivParams);
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
            firstScreenRecentlyGridDivider.setLayoutParams(recentlyDivParams);
            FrameLayout.LayoutParams favoriteDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            favoriteDivParams.topMargin = 50;
            firstScreenFavortiteGridDivider.setLayoutParams(favoriteDivParams);
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 50;
            firstScreenBrowserFirstGridDivider.setLayoutParams(firstDivParams);
            FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            secondDivParams.topMargin = 194;
            firstScreenBrowserSecondGridDivider
                    .setLayoutParams(secondDivParams);
            FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            thirdDivParams.topMargin = 244;
            firstScreenBrowserThirdGridDivider.setLayoutParams(thirdDivParams);
        }
    }

    /** Hide file path navigation arrows from second screen */
    public void hideSecondScreenFilePathArrows() {
        secondScreenFilePathLeftArrow.setVisibility(View.INVISIBLE);
        secondScreenFilePathRightArrow.setVisibility(View.INVISIBLE);
    }

    /** Show recently navigation arrows from second screen */
    public void showSecondScreenFilePathArrows() {
        secondScreenFilePathLeftArrow.setVisibility(View.VISIBLE);
        secondScreenFilePathRightArrow.setVisibility(View.VISIBLE);
    }

    /** Hide file browser navigation arrows from second screen */
    public void hideSecondScreenAllArrows() {
        secondScreenAllLeftArrow.setVisibility(View.INVISIBLE);
        secondScreenAllRightArrow.setVisibility(View.INVISIBLE);
    }

    /** Show file browser navigation arrows from second screen */
    public void showSecondScreenAllArrows() {
        secondScreenAllLeftArrow.setVisibility(View.VISIBLE);
        secondScreenAllRightArrow.setVisibility(View.VISIBLE);
    }

    /** Set up position of grid dividers on second screen */
    public void setUpDividersSecondScreen() {
        // Check resolution and add layout params
        // //////////////////////////
        // 720p
        // //////////////////////////
        if (MainActivity.screenWidth == 1280) {
            FrameLayout.LayoutParams filePathDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            filePathDivParams.topMargin = 35;
            secondScreenFilePathGridDivider.setLayoutParams(filePathDivParams);
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 35;
            secondScreenBrowserFirstGridDivider.setLayoutParams(firstDivParams);
            // PE Android4TV
            if (MainActivity.screenHeight != MainActivity.SCREEN_HEIGHT_720P) {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 117;
                secondScreenBrowserSecondGridDivider
                        .setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 152;
                secondScreenBrowserThirdGridDivider
                        .setLayoutParams(thirdDivParams);
                FrameLayout.LayoutParams fourthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fourthDivParams.topMargin = 235;
                secondScreenBrowserFourthGridDivider
                        .setLayoutParams(fourthDivParams);
                FrameLayout.LayoutParams fifthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fifthDivParams.topMargin = 270;
                secondScreenBrowserFifthGridDivider
                        .setLayoutParams(fifthDivParams);
            }
            // AMP Android4TV
            else {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 125;
                secondScreenBrowserSecondGridDivider
                        .setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 162;
                secondScreenBrowserThirdGridDivider
                        .setLayoutParams(thirdDivParams);
                FrameLayout.LayoutParams fourthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fourthDivParams.topMargin = 252;
                secondScreenBrowserFourthGridDivider
                        .setLayoutParams(fourthDivParams);
                FrameLayout.LayoutParams fifthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fifthDivParams.topMargin = 290;
                secondScreenBrowserFifthGridDivider
                        .setLayoutParams(fifthDivParams);
            }
        }
        // ////////////////////////////////
        // 1080p
        // ////////////////////////////////
        if (MainActivity.screenWidth == 1920) {
            FrameLayout.LayoutParams filePathDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            filePathDivParams.topMargin = 50;
            secondScreenFilePathGridDivider.setLayoutParams(filePathDivParams);
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 50;
            secondScreenBrowserFirstGridDivider.setLayoutParams(firstDivParams);
            FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            secondDivParams.topMargin = 194;
            secondScreenBrowserSecondGridDivider
                    .setLayoutParams(secondDivParams);
            FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            thirdDivParams.topMargin = 244;
            secondScreenBrowserThirdGridDivider.setLayoutParams(thirdDivParams);
            FrameLayout.LayoutParams fourthDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            fourthDivParams.topMargin = 390;
            secondScreenBrowserFourthGridDivider
                    .setLayoutParams(fourthDivParams);
            FrameLayout.LayoutParams fifthDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            fifthDivParams.topMargin = 440;
            secondScreenBrowserFifthGridDivider.setLayoutParams(fifthDivParams);
        }
    }

    /** Hide file browser navigation arrows from pvr screen */
    public void hidePvrScreenAllArrows() {
        pvrScreenAllLeftArrow.setVisibility(View.INVISIBLE);
        pvrScreenAllRightArrow.setVisibility(View.INVISIBLE);
    }

    /** Show file browser navigation arrows from pvr screen */
    public void showPvrScreenAllArrows() {
        pvrScreenAllLeftArrow.setVisibility(View.VISIBLE);
        pvrScreenAllRightArrow.setVisibility(View.VISIBLE);
    }

    /** Set up position of grid dividers on pvr screen */
    public void setUpDividersPvrScreen() {
        // Check resolution and add layout params
        // //////////////////////////
        // 720p
        // //////////////////////////
        if (MainActivity.screenWidth == 1280) {
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 35;
            pvrScreenBrowserFirstGridDivider.setLayoutParams(firstDivParams);
            // PE Android4TV
            if (MainActivity.screenHeight != MainActivity.SCREEN_HEIGHT_720P) {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 117;
                pvrScreenBrowserSecondGridDivider
                        .setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 152;
                pvrScreenBrowserThirdGridDivider
                        .setLayoutParams(thirdDivParams);
                FrameLayout.LayoutParams fourthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fourthDivParams.topMargin = 235;
                pvrScreenBrowserFourthGridDivider
                        .setLayoutParams(fourthDivParams);
                FrameLayout.LayoutParams fifthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fifthDivParams.topMargin = 270;
                pvrScreenBrowserFifthGridDivider
                        .setLayoutParams(fifthDivParams);
            }
            // AMP Android4TV
            else {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 125;
                pvrScreenBrowserSecondGridDivider
                        .setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 162;
                pvrScreenBrowserThirdGridDivider
                        .setLayoutParams(thirdDivParams);
                FrameLayout.LayoutParams fourthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fourthDivParams.topMargin = 252;
                pvrScreenBrowserFourthGridDivider
                        .setLayoutParams(fourthDivParams);
                FrameLayout.LayoutParams fifthDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                fifthDivParams.topMargin = 290;
                pvrScreenBrowserFifthGridDivider
                        .setLayoutParams(fifthDivParams);
            }
        }
        // ////////////////////////////////
        // 1080p
        // ////////////////////////////////
        if (MainActivity.screenWidth == 1920) {
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 50;
            pvrScreenBrowserFirstGridDivider.setLayoutParams(firstDivParams);
            FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            secondDivParams.topMargin = 194;
            pvrScreenBrowserSecondGridDivider.setLayoutParams(secondDivParams);
            FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            thirdDivParams.topMargin = 244;
            pvrScreenBrowserThirdGridDivider.setLayoutParams(thirdDivParams);
            FrameLayout.LayoutParams fourthDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            fourthDivParams.topMargin = 390;
            pvrScreenBrowserFourthGridDivider.setLayoutParams(fourthDivParams);
            FrameLayout.LayoutParams fifthDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            fifthDivParams.topMargin = 440;
            pvrScreenBrowserFifthGridDivider.setLayoutParams(fifthDivParams);
        }
    }

    /** Reopen multimedia screen when needed */
    public static void returnMultimediaToPreviousState() {
        /** SetCurlHandler in Normal State */
        OSDHandlerHelper.setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
        // ///////////////////////////////////
        // Hide multimedia music icon and overlays
        // ///////////////////////////////////
        MultimediaGridHelper.hideDlnaOverlays();
        if (MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            MainActivity.activity.getMultimediaHandler().getMultimediaDialog()
                    .show();
            // Update multimedia state
            multimediaScreen = MULTIMEDIA_FIRST_SCREEN;
            MainKeyListener.setAppState(MainKeyListener.MULTIMEDIA_FIRST);
        }
        if (MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
            MainActivity.activity.getMultimediaHandler().getMultimediaDialog()
                    .show();
            multimediaScreen = MULTIMEDIA_SECOND_SCREEN;
            MainKeyListener.setAppState(MainKeyListener.MULTIMEDIA_SECOND);
            try {
                MainActivity.service.getContentListControl().setActiveFilter(
                        FilterType.MULTIMEDIA);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        if (MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
            MainActivity.activity.getMultimediaHandler().getMultimediaDialog()
                    .show();
            multimediaScreen = MULTIMEDIA_PVR_SCREEN;
            MainKeyListener.setAppState(MainKeyListener.MULTIMEDIA_PVR);
            try {
                MainActivity.service.getContentListControl().setActiveFilter(
                        FilterType.PVR_RECORDED);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /** Async Task for loading data */
    public class LoadTask extends AsyncTask<Void, Void, Boolean> {
        private String path;

        public LoadTask(String path) {
            super();
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            filterContent(MultimediaHandler.FILTER_MULTIMEDIA);
            try {
                MainActivity.service.getContentListControl().goPath(path);
            } catch (Exception e) {
                Log.e(TAG, "GoPath", e);
            }
            return null;
        }

        protected void onPostExecute(Boolean result) {
            // Hide main menu
            if (MainKeyListener.multimediaFromMainMenu) {
                ((MainActivity) activity).getMainMenuHandler().closeMainMenu(
                        false);
            }
            // Show multimedia
            showMultimedia();
            // Delay start of loading content
            Handler delay = new Handler();
            delay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestContentItems(0);
                }
            }, 200);
            progressDialog.dismiss();
        }
    };

    /** Async Task for loading data in multimedia */
    public class LoadTaskMultimediaBack extends AsyncTask<Void, Void, Boolean> {
        private String path;
        private int state;

        public LoadTaskMultimediaBack(String path, int state) {
            super();
            this.path = path;
            this.state = state;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                MainActivity.service.getContentListControl().setActiveFilter(
                        MultimediaHandler.FILTER_MULTIMEDIA
                                + MultimediaHandler.FILTER_MULTIMEDIA_OFFSET);
                MainActivity.service.getContentListControl().goPath(path);
            } catch (Exception e) {
                Log.e("Multimedia handler", "GoPath", e);
            }
            return null;
        }

        protected void onPostExecute(Boolean result) {
            ((MainActivity) activity).getMultimediaHandler()
                    .getLinearLayoutSortedPlaylistItemsByTitle()
                    .setVisibility(View.INVISIBLE);
            ((MainActivity) activity).getMultimediaHandler()
                    .getLinearLayoutSortedPlaylistItemsByArtist()
                    .setVisibility(View.INVISIBLE);
            ((MainActivity) activity).getMultimediaHandler()
                    .getLinearLayoutSortedPlaylistItemsByDuration()
                    .setVisibility(View.INVISIBLE);
            // ///////////////////////////////////
            // Multimedia
            // ///////////////////////////////////
            if (state == LOAD_BACK_FIRST_SCREEN) {
                closeMultimediaSecond();
            }
            // /////////////////////////////////////
            // PVR
            // /////////////////////////////////////
            if (state == LOAD_BACK_FIRST_SCREEN_FROM_PVR) {
                closeMultimediaPvr();
            }
            // ///////////////////////////////////
            // ReOpen Multimedia from Player
            // ///////////////////////////////////
            if (state == REOPEN_MULTIMEDIA) {
                showMultimedia();
            }
            ((MainActivity) activity).getMultimediaHandler().filterContent(
                    MultimediaHandler.FILTER_MULTIMEDIA);
            // /////////////////////////////////////////
            // Request items
            // /////////////////////////////////////////
            // Get current navigation object
            MultimediaNavigationObject navigationObject = MultimediaNavigationHandler
                    .getCurrentNavigationObject();
            if (navigationObject != null) {
                // Request contents for defined page
                ((MainActivity) activity).getMultimediaHandler()
                        .requestContentItems(navigationObject.getPage());
                // Find position of focus
                int focusPosition;
                switch (multimediaScreen) {
                    case MULTIMEDIA_FIRST_SCREEN: {
                        focusPosition = MultimediaNavigationHandler
                                .findPositionOfPreviousFolder(
                                        navigationObject,
                                        MultimediaFileBrowserHandler.fileBrowserCurrentItemsFirstScreen);
                        multimediaFileBrowserFirstHandler
                                .focusActiveElement(focusPosition);
                        break;
                    }
                    case MULTIMEDIA_SECOND_SCREEN: {
                        focusPosition = MultimediaNavigationHandler
                                .findPositionOfPreviousFolder(
                                        navigationObject,
                                        MultimediaFileBrowserHandler.fileBrowserCurrentItemsSecondScreen);
                        multimediaFileBrowserSecondHandler
                                .focusActiveElement(focusPosition);
                        break;
                    }
                }
            } else {
                // Request contents for defined page
                ((MainActivity) activity).getMultimediaHandler()
                        .requestContentItems(0);
            }
            // Return to previous folder
            MultimediaNavigationHandler.returnToPreviousFolder(1);
            progressDialog.dismiss();
        }
    };

    // //////////////////////////////////////////////////
    // Getters and Setters
    // //////////////////////////////////////////////////
    /** Get first screen dialog */
    public A4TVDialog getMultimediaDialog() {
        return multimediaDialog;
    }

    public MultimediaRecentlyHandler getMultimediaRecentlyHandler() {
        return multimediaRecentlyHandler;
    }

    public MultimediaFavoriteHandler getMutlimediaFavoriteHandler() {
        return mutlimediaFavoriteHandler;
    }

    public MultimediaFileBrowserHandler getMultimediaFileBrowserFirstHandler() {
        return multimediaFileBrowserFirstHandler;
    }

    public MultimediaFileBrowserHandler getMultimediaFileBrowserSecondHandler() {
        return multimediaFileBrowserSecondHandler;
    }

    public MultimediaFilePathHandler getFilePathHandler() {
        return filePathHandler;
    }

    public MultimediaShowHandler getMultimediaShowHandler() {
        return multimediaShowHandler;
    }

    public A4TVTextView getPvrFileInfo1() {
        return pvrFileInfo1;
    }

    public A4TVTextView getPvrFileInfo2() {
        return pvrFileInfo2;
    }

    public A4TVTextView getPvrFileInfo3() {
        return pvrFileInfo3;
    }

    public A4TVTextView getPvrFileInfo4() {
        return pvrFileInfo4;
    }

    public MultimediaFileBrowserHandler getMultimediaFileBrowserPvrHandler() {
        return multimediaFileBrowserPvrHandler;
    }

    /** Get layout that shows that music is player from dlna */
    public LinearLayout getMusicFromDlnaLayout() {
        return musicFromDlnaLayout;
    }

    public ImageView getImageViewMusicReproduction() {
        return imageViewMusicReproduction;
    }

    public A4TVTextView getTextViewLyrics() {
        return textViewLyrics;
    }

    public A4TVInfoDescriptionScrollView getScrollViewLyrics() {
        return scrollViewLyrics;
    }

    // /////////////////////////////////////////////
    // Navigation arrows getters and setters
    // /////////////////////////////////////////////
    public ImageView getFirstScreenRecentlyLeftArrow() {
        return firstScreenRecentlyLeftArrow;
    }

    public ImageView getFirstScreenRecentlyRightArrow() {
        return firstScreenRecentlyRightArrow;
    }

    public ImageView getFirstScreenFavoriteLeftArrow() {
        return firstScreenFavoriteLeftArrow;
    }

    public ImageView getFirstScreenFavoriteRightArrow() {
        return firstScreenFavoriteRightArrow;
    }

    public ImageView getFirstScreenAllLeftArrow() {
        return firstScreenAllLeftArrow;
    }

    public ImageView getFirstScreenAllRightArrow() {
        return firstScreenAllRightArrow;
    }

    public ImageView getSecondScreenFilePathLeftArrow() {
        return secondScreenFilePathLeftArrow;
    }

    public ImageView getSecondScreenFilePathRightArrow() {
        return secondScreenFilePathRightArrow;
    }

    public ImageView getSecondScreenAllLeftArrow() {
        return secondScreenAllLeftArrow;
    }

    public ImageView getSecondScreenAllRightArrow() {
        return secondScreenAllRightArrow;
    }

    public ImageView getPvrScreenAllLeftArrow() {
        return pvrScreenAllLeftArrow;
    }

    public ImageView getPvrScreenAllRightArrow() {
        return pvrScreenAllRightArrow;
    }

    public A4TVDialog getMultimediaShowDialog() {
        return multimediaShowDialog;
    }

    public LinearLayout getLinearLayoutSortedBy() {
        return linearLayoutSortedBy;
    }

    public LinearLayout getLinearLayoutSortedPlaylistItemsByTitle() {
        return linearLayoutSortedPlaylistItemsByTitle;
    }

    public LinearLayout getLinearLayoutSortedPlaylistItemsByArtist() {
        return linearLayoutSortedPlaylistItemsByArtist;
    }

    public LinearLayout getLinearLayoutSortedPlaylistItemsByDuration() {
        return linearLayoutSortedPlaylistItemsByDuration;
    }

    public boolean isDetectScrollLyricsEnd() {
        return detectScrollLyricsEnd;
    }

    public int getScrollLyricsValue() {
        return scrollLyricsValue;
    }

    public void setDetectScrollLyricsEnd(boolean detectScrollLyricsEnd) {
        this.detectScrollLyricsEnd = detectScrollLyricsEnd;
    }

    public void setScrollLyricsValue(int scrollLyricsValue) {
        this.scrollLyricsValue = scrollLyricsValue;
    }

    /** OnClick listener for filter buttons */
    private class FilterOnClick implements OnClickListener {
        private int tag;

        public FilterOnClick(int tag) {
            this.tag = tag;
        }

        @Override
        public void onClick(View v) {
            if (tag == FILTER_RECORD_PVR_OPTION) {
                PvrSortMode sortMode;
                PvrSortOrder sortOrder;
                linearLayoutSortedBy.setVisibility(View.VISIBLE);
                try {
                    sortMode = MainActivity.service.getPvrControl()
                            .getMediaListSortMode();
                    sortOrder = MainActivity.service.getPvrControl()
                            .getMediaListSortOrder();
                    int filterType = MainActivity.service
                            .getContentListControl().getActiveFilterIndex();
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.PVR_RECORDED);
                    int pvrSize = MainActivity.service.getContentListControl()
                            .getContentListSize();
                    if (pvrSize > 0) {
                        MultimediaHandler.pvrFileBrowserText
                                .setText("PVR playlist sorted by "
                                        + MultimediaHandler.sortPvrFilesBy[sortMode
                                                .getValue()]
                                        + " in "
                                        + MultimediaHandler.sortPvrFilesByOrder[sortOrder
                                                .getValue()] + " order");
                    } else {
                        MultimediaHandler.pvrFileBrowserText.setText(activity
                                .getResources().getString(
                                        R.string.multimedia_pvr_playlist));
                    }
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(filterType);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                linearLayoutSortedBy.setVisibility(View.INVISIBLE);
                pvrFileBrowserText.setText(activity.getResources().getString(
                        R.string.multimedia_file_explorer));
            }
            // Load filter
            filterContent(tag);
            // Request items
            requestContentItems(0);
        }
    }

    private class FilterOptionsKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    multimediaFileBrowserPvrHandler.focusActiveElement(0);
                } else {
                    return false;
                }
            }
            return false;
        }
    }

    private class ScrollLyrics implements Scrolled {
        @Override
        public void scrolled() {
        }

        @Override
        public void detectEnd() {
            detectScrollLyricsEnd = true;
        }
    }

    public void scrollLyrics(int direction) {
        if (direction == -1) {
            if (detectScrollLyricsEnd) {
                return;
            }
            scrollLyricsValue += 10;
            MainActivity.activity.getMultimediaHandler().getScrollViewLyrics()
                    .scrollTo(0, scrollLyricsValue);
        } else {
            detectScrollLyricsEnd = false;
            if (scrollLyricsValue == 0) {
                return;
            }
            scrollLyricsValue -= 10;
            MainActivity.activity.getMultimediaHandler().getScrollViewLyrics()
                    .scrollTo(0, scrollLyricsValue);
        }
    }
}
