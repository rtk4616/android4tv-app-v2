package com.iwedia.gui.mainmenu;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.A4TVGalleryOnClickListener;
import com.iwedia.gui.listeners.A4TVGalleryOnSelectListener;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.mainmenu.gallery.A4TVGallery;
import com.iwedia.gui.mainmenu.gallery.animations.Flip3dAnimationHandler;
import com.iwedia.gui.mainmenu.gallery.animations.TransitionItemAnimationHandler;

import java.util.ArrayList;

/**
 * Handling class for main menu
 * 
 * @author Veljko Ilkic
 */
public class MainMenuHandlingClass {
    private final static String TAG = "MainMenuHandlingClass";
    /** Reference of main activity */
    private Activity activity;
    /** Dialog holder for main menu */
    private A4TVDialog mainMenuDialog;
    /** A4TV gallery element */
    private A4TVGallery mainMenuGallery;
    /** OnSelect listener for main menu gallery */
    private A4TVGalleryOnSelectListener a4TVOnSelectLister;
    /** Background image of main menu */
    private ImageView mainMenuGalleryBackground;
    /** Image adapter for gallery */
    private ImageAdapterForGallery mainMenuAdapter;
    /** Layout over gallery element in main menu */
    private FrameLayout mainMenuOverlay;
    /** Name of selected item in main menu */
    private A4TVTextView selectedItemName;
    /** Description of selected item in main menu */
    private A4TVTextView selectedItemDescription;
    /** Info bar holder in main menu */
    private LinearLayout infoBarHolder;
    /** Handler for 3d flip animation */
    private Flip3dAnimationHandler flip3dAnimationHandler;
    /** Animation for transition between selected item and root of submenu item */
    private TransitionItemAnimationHandler transitionItemAnimHandler;
    /** Animation for gallery items and main menu */
    private Animation alphaScaleIn;
    /** Fade out animation for main menu closing */
    private Animation alphaScaleOut;
    /** Default selection index */
    private final int defaultSelectedIndex = 0;
    /** Opacity constants for dimming effect */
    public static final int elementOpacityMid = 100;
    /** Dots handling class */
    private MainMenuDotsHandlingClass dotsHandler;
    /** Dialog for settings */
    private A4TVDialog smallDialog;
    /** Views of content dialog */
    private View contentOfSmallDialog;
    /** List of current menu items */
    private ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
    /** Class that creates small dialog content */
    private DialogCreatorClass dialogCreator;
    /** User navigation path */
    public static ArrayList<Integer> navigationPath = new ArrayList<Integer>();
    private boolean returnToCleanScreen = false;

    /** Constructor 1 */
    public MainMenuHandlingClass(Activity activity) {
        // Take reference of main activity
        this.activity = activity;
    }

    /** Init function */
    public void init() {
        // //////////////////////////////////////////
        // Init main menu state
        // //////////////////////////////////////////
        // Needed for return main menu state after app restart
        if (MainActivity.stopVideoOnPauseAndReturnMenuToUser) {
            MainMenuContent.currentState = MainMenuContent.MAIN_MENU;
            loadMenuItems(MainMenuContent.currentState);
        }
        // /////////////////////////////////////////
        // Dialog
        // /////////////////////////////////////////
        // Create dialog holder for main menu
        mainMenuDialog = ((MainActivity) activity).getDialogManager()
                .getMainMenuDialog();
        if (mainMenuDialog != null) {
            mainMenuDialog.setOnKeyListener(new MainKeyListener(
                    (MainActivity) activity));
            // ///////////////////////////////////////////
            // Layouts and views
            // ///////////////////////////////////////////
            // Take reference of main gallery background
            mainMenuGalleryBackground = (ImageView) mainMenuDialog
                    .findViewById(com.iwedia.gui.R.id.mainMenuGalleryBackground);
            mainMenuGalleryBackground.getBackground().setDither(true);
            // Take reference of gallery in dialog
            mainMenuGallery = (A4TVGallery) mainMenuDialog
                    .findViewById(com.iwedia.gui.R.id.mainMenuGallery);
            // Take reference of main menu overlay layout
            mainMenuOverlay = (FrameLayout) mainMenuDialog
                    .findViewById(com.iwedia.gui.R.id.mainMenuOverlayLayout);
            // Set current frame layout in gallery
            mainMenuGallery.setCurrentSelectedItemFrame(mainMenuOverlay);
            // Create image adapter
            mainMenuAdapter = new ImageAdapterForGallery(activity);
            // Attach image adapter
            mainMenuGallery.setAdapter(mainMenuAdapter);
            a4TVOnSelectLister = new A4TVGalleryOnSelectListener(activity);
            // Attach onSelect listener
            mainMenuGallery.setOnItemSelectedListener(a4TVOnSelectLister);
            // Set on click listener
            mainMenuGallery
                    .setOnItemClickListener(new A4TVGalleryOnClickListener(
                            activity));
            // Set spacing
            mainMenuGallery.setSpacing(5);
            // Set index of default selection
            mainMenuGallery.setSelection(defaultSelectedIndex);
            mainMenuGallery.requestFocus();
            // Name of the selected item in main menu gallery
            selectedItemName = (A4TVTextView) mainMenuDialog
                    .findViewById(com.iwedia.gui.R.id.aTVTextViewIconsName);
            // Description of the selected item in main menu gallery
            selectedItemDescription = (A4TVTextView) mainMenuDialog
                    .findViewById(com.iwedia.gui.R.id.aTVTextViewMoreInfo);
            // Info bar holder
            infoBarHolder = (LinearLayout) mainMenuDialog
                    .findViewById(com.iwedia.gui.R.id.linearLayoutForMenuInfo);
        }
        // ////////////////////////////////////////
        // Animations
        // ////////////////////////////////////////
        // Create Flip 3d animation handler
        flip3dAnimationHandler = new Flip3dAnimationHandler(mainMenuOverlay);
        if (flip3dAnimationHandler != null) {
            this.flip3dAnimationHandler.init();
        }
        // Create transition to root of submenu animation handler
        transitionItemAnimHandler = new TransitionItemAnimationHandler(
                mainMenuOverlay, this);
        this.transitionItemAnimHandler.init();
        // Load animation for root submenu element
        alphaScaleIn = AnimationUtils.loadAnimation(activity,
                com.iwedia.gui.R.anim.scale_alpha_in);
        alphaScaleOut = AnimationUtils.loadAnimation(activity,
                com.iwedia.gui.R.anim.scale_alpha_out);
        // Animation listener listens for end of animation
        alphaScaleOut.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainMenuDialog.cancel();
                if (returnToCleanScreen) {
                    MainKeyListener.returnToStoredAppState();
                }
            }
        });
        // Set custom layout params on views
        setParams();
        // ///////////////////////////////////////////
        // Dots handler
        // ///////////////////////////////////////////
        dotsHandler = new MainMenuDotsHandlingClass((MainActivity) activity);
        if (mainMenuGallery != null) {
            dotsHandler.setNumberOfDots(mainMenuGallery.getCount());
        }
        dotsHandler.setSelectedDot(0);
        // init dialog creator
        dialogCreator = new DialogCreatorClass(((MainActivity) activity));
    }

    /**
     * Load array list of menu items
     */
    public void loadMenuItems(int menuId) {
        // Array of strings for menu items data
        String[] itemNames = null;
        String[] itemDescriptions = null;
        // Update main menu state
        MainMenuContent.currentState = menuId;
        // Check which menu to load
        if (MainActivity.getKeySet() > 1) {
            // HBBTV is active, load appropriate menu tree
            ((MainActivity) activity).getMainMenuContent().reloadFromThemes(
                    MainMenuContent.HBB_TV_MENU);
            menuItems.clear();
            itemNames = activity.getResources().getStringArray(
                    com.iwedia.gui.R.array.main_menu_hbb_item_names);
            itemDescriptions = activity.getResources().getStringArray(
                    com.iwedia.gui.R.array.main_menu_hbb_item_description);
            // Load menu items
            for (int i = 0; i < MainMenuContent.mainMenuIcons.length; i++) {
                if (itemNames != null && itemDescriptions != null) {
                    Log.d(MainActivity.TAG, "ItemName = " + itemNames[i]);
                    menuItems.add(new MenuItem((MainActivity) activity,
                            itemNames[i], itemDescriptions[i],
                            MainMenuContent.mainMenuIcons[i]));
                }
            }
        } else {
            // load default menu tree
            ((MainActivity) activity).getMainMenuContent().reloadFromThemes(
                    MainMenuContent.DEFAULT_MENU);
            // Check
            switch (menuId) {
                case MainMenuContent.MAIN_MENU:
                    // Clear menu item list before adding new ones
                    menuItems.clear();
                    // ////////////////////////////
                    // NO TV FEATURES
                    // ////////////////////////////
                    if (!ConfigHandler.TV_FEATURES) {
                        // Load strings
                        itemNames = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.main_menu_item_names_tv_features);
                        itemDescriptions = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.main_menu_item_description_no_tv_features);
                    }
                    // ////////////////////////////
                    // DEFAULT config
                    // ////////////////////////////
                    else {
                        // Load strings
                        itemNames = activity.getResources().getStringArray(
                                com.iwedia.gui.R.array.main_menu_item_names);
                        itemDescriptions = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.main_menu_item_description);
                    }
                    // Load menu items
                    for (int i = 0; i < MainMenuContent.mainMenuIcons.length; i++) {
                        if (itemNames != null && itemDescriptions != null)
                            menuItems.add(new MenuItem((MainActivity) activity,
                                    itemNames[i], itemDescriptions[i],
                                    MainMenuContent.mainMenuIcons[i]));
                    }
                    break;
                case MainMenuContent.SETTINGS: {
                    // Clear menu item list before adding new ones
                    menuItems.clear();
                    // ////////////////////////////
                    // NO TV FEATURES OR NO DLNA
                    // ////////////////////////////
                    if (!ConfigHandler.TV_FEATURES || !ConfigHandler.DLNA) {
                        // No tv features
                        if (!ConfigHandler.TV_FEATURES) {
                            // Load strings
                            itemNames = activity
                                    .getResources()
                                    .getStringArray(
                                            com.iwedia.gui.R.array.settings_menu_item_names_no_tv_features);
                            itemDescriptions = activity
                                    .getResources()
                                    .getStringArray(
                                            com.iwedia.gui.R.array.settings_menu_description_names_description_no_tv_features);
                        }
                        // No dlna
                        if (!ConfigHandler.DLNA) {
                            // Load strings
                            itemNames = activity
                                    .getResources()
                                    .getStringArray(
                                            com.iwedia.gui.R.array.settings_menu_item_names_no_dlna);
                            itemDescriptions = activity
                                    .getResources()
                                    .getStringArray(
                                            com.iwedia.gui.R.array.settings_menu_description_names_description_no_dlna);
                        }
                    }
                    // ////////////////////////////
                    // NO TV FEATURES AND NO DLNA
                    // ////////////////////////////
                    if (!ConfigHandler.TV_FEATURES && !ConfigHandler.DLNA) {
                        // Load strings
                        itemNames = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.settings_menu_item_names_no_dlna_no_tv_features);
                        itemDescriptions = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.settings_menu_description_names_description_no_dlna_no_tv_features);
                    }
                    // ////////////////////////////
                    // DEFAULT config
                    // ////////////////////////////
                    if (ConfigHandler.TV_FEATURES && ConfigHandler.DLNA) {
                        // Load strings
                        itemNames = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.settings_menu_item_names);
                        itemDescriptions = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.settings_menu_description_names_description);
                    }
                    // Load menu items
                    for (int i = 0; i < MainMenuContent.submenuSettings.length; i++) {
                        if (itemNames != null && itemDescriptions != null)
                            menuItems.add(new MenuItem((MainActivity) activity,
                                    itemNames[i], itemDescriptions[i],
                                    MainMenuContent.submenuSettings[i]));
                    }
                    break;
                }
                case MainMenuContent.TV_SETTINGS: {
                    // Clear menu item list before adding new ones
                    menuItems.clear();
                    // ////////////////////////////
                    // NO HBB
                    // ////////////////////////////
                    if (!ConfigHandler.HBB) {
                        // Load strings
                        itemNames = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.tv_settings_menu_item_names_no_hbb);
                        itemDescriptions = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.tv_settings_menu_description_names_no_hbb);
                    }
                    // ////////////////////////////
                    // DEFAULT config
                    // ////////////////////////////
                    else {
                        // Load strings
                        itemNames = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.tv_settings_menu_item_names);
                        itemDescriptions = activity
                                .getResources()
                                .getStringArray(
                                        com.iwedia.gui.R.array.tv_settings_menu_description_names);
                    }
                    // Load menu items
                    for (int i = 0; i < MainMenuContent.submenuSettingsTvSettings.length; i++) {
                        menuItems.add(new MenuItem((MainActivity) activity,
                                itemNames[i], itemDescriptions[i],
                                MainMenuContent.submenuSettingsTvSettings[i]));
                    }
                    break;
                }
                case MainMenuContent.SECURITY_SETTINGS: {
                    // Clear menu item list before adding new ones
                    menuItems.clear();
                    // Load strings
                    itemNames = activity
                            .getResources()
                            .getStringArray(
                                    com.iwedia.gui.R.array.security_settings_menu_item_names);
                    itemDescriptions = activity
                            .getResources()
                            .getStringArray(
                                    com.iwedia.gui.R.array.security_settings_menu_description_names);
                    // Load menu items
                    for (int i = 0; i < MainMenuContent.submenuSettingsTvSecuritySettings.length; i++) {
                        menuItems
                                .add(new MenuItem(
                                        (MainActivity) activity,
                                        itemNames[i],
                                        itemDescriptions[i],
                                        MainMenuContent.submenuSettingsTvSecuritySettings[i]));
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    /** Refresh main menu gallery */
    public void refreshMainMenu(boolean forward_backward) {
        // Refresh number of dots
        dotsHandler.setNumberOfDots(menuItems.size());
        // ///////////////////////////////
        // Init gallery state
        // ///////////////////////////////
        if (forward_backward) {
            // ////////////////////////////////////
            // Forward
            // ////////////////////////////////////
            // Set default selection
            mainMenuGallery.setSelection(defaultSelectedIndex);
            // Notify new data
            mainMenuAdapter.notifyDataSetChanged();
            Handler delay = new Handler();
            delay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Set first element last elements
                    A4TVGalleryOnSelectListener.lastView = mainMenuGallery
                            .getChildAt(defaultSelectedIndex);
                    // Hide selected item in gallery element
                    mainMenuGallery.getChildAt(defaultSelectedIndex)
                            .setVisibility(View.INVISIBLE);
                    // Set selected item icon
                    flip3dAnimationHandler.getImage().setImageBitmap(
                            MainActivity.mMemoryCache
                                    .loadBitmapFromResource(menuItems.get(
                                            defaultSelectedIndex)
                                            .getMenuImage()));
                    // Set text on name of selected item
                    selectedItemName.setText(menuItems
                            .get(defaultSelectedIndex).getMenuItemName());
                    // Set text on description of selected item
                    selectedItemDescription.setText(menuItems.get(
                            defaultSelectedIndex).getMenuItemDescription());
                    // Set default selection
                    mainMenuGallery.setSelection(defaultSelectedIndex);
                    // Set selected first dot by default
                    dotsHandler.setSelectedDot(defaultSelectedIndex);
                }
            }, 20);
        } else {
            // ////////////////////////////////////
            // Backward
            // ////////////////////////////////////
            Log.d(TAG, "NAVIGATION PATH" + navigationPath.size() + "");
            final int indexOfLastElement;
            if (navigationPath.size() > 0) {
                // Get index of element, that we need to work with
                indexOfLastElement = navigationPath
                        .get(navigationPath.size() - 1);
                // Remove index from navigation path
                navigationPath.remove(navigationPath.size() - 1);
                // Set default selection
                mainMenuGallery.setSelection(indexOfLastElement);
            } else {
                indexOfLastElement = 0;
            }
            // Notify new data
            mainMenuAdapter.notifyDataSetChanged();
            Handler delay = new Handler();
            delay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Set first element last elements
                    A4TVGalleryOnSelectListener.lastView = mainMenuGallery
                            .getSelectedView();
                    Log.d(TAG, "lastSelectedItem " + indexOfLastElement + "");
                    Log.d(TAG,
                            "size of gallery "
                                    + mainMenuGallery.getChildCount() + "");
                    // Hide selected item in gallery element
                    View sView = mainMenuGallery.getSelectedView();
                    if (sView != null) {
                        sView.setVisibility(View.INVISIBLE);
                    }
                    // Set selected item icon
                    flip3dAnimationHandler.getImage()
                            .setImageBitmap(
                                    MainActivity.mMemoryCache
                                            .loadBitmapFromResource(menuItems
                                                    .get(indexOfLastElement)
                                                    .getMenuImage()));
                    // Set text on name of selected item
                    selectedItemName.setText(menuItems.get(indexOfLastElement)
                            .getMenuItemName());
                    // Set text on description of selected item
                    selectedItemDescription.setText(menuItems.get(
                            indexOfLastElement).getMenuItemDescription());
                    // Set default selection
                    mainMenuGallery.setSelection(indexOfLastElement);
                    // Set selected first dot by default
                    dotsHandler.setSelectedDot(indexOfLastElement);
                }
            }, 20);
        }
    }

    /** Show main menu dialog on screen */
    public void showMainMenu() {
        MainKeyListener.setAppState(MainKeyListener.MAIN_MENU);
        mainMenuDialog.show();
        // ///////////////////////////////////
        // Unscalled gallery image bug fix
        // ////////////////////////////////////
        /*
         * mainMenuDialog.cancel(); mainMenuDialog.show();
         */
        // ///////////////////////////////////
        // Unscalled gallery image bug fix
        // ////////////////////////////////////
        animateInMainMenuDialog();
        a4TVOnSelectLister.startAnimationsManual();
    }

    /** Hide main menu dialog from screen */
    public void closeMainMenu(boolean returnToCleanScreen) {
        this.returnToCleanScreen = returnToCleanScreen;
        animateOutMainMenuDialog();
        a4TVOnSelectLister.clearAnimationsManual();
    }

    /** In animation for main menu */
    public void animateInMainMenuDialog() {
        // Animate views
        mainMenuGallery.startAnimation(alphaScaleIn);
    }

    /** Out animation for main menu */
    public void animateOutMainMenuDialog() {
        mainMenuGallery.startAnimation(alphaScaleOut);
    }

    /** Animate small dialog opening */
    public void animateSmallDialog() {
        // Animate content of small dialog
        contentOfSmallDialog.startAnimation(alphaScaleIn);
    }

    /** Set layout params on views */
    public void setParams() {
        // Name of selected item
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        if (((MainActivity) activity).isFullHD()) {
            params1.topMargin = 130;
            if (selectedItemName != null)
                selectedItemName
                        .setTextSize(
                                TypedValue.COMPLEX_UNIT_DIP,
                                activity.getResources()
                                        .getDimension(
                                                com.iwedia.gui.R.dimen.a4tvdialog_button_text_size));
        } else {
            params1.topMargin = 80;
            if (selectedItemName != null)
                selectedItemName
                        .setTextSize(activity
                                .getResources()
                                .getDimension(
                                        com.iwedia.gui.R.dimen.selected_item_name_font_size));
        }
        if (selectedItemName != null) {
            selectedItemName.setLayoutParams(params1);
        }
        // Info bar holder
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                (int) (2 * MainActivity.screenHeight / 28.5));
        params2.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        if (infoBarHolder != null) {
            infoBarHolder.setLayoutParams(params2);
        }
        // Main menu background
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(
                MainActivity.screenWidth, MainActivity.screenWidth / 6);
        params3.gravity = Gravity.CENTER;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = true;
        options.inScaled = false;
        // options.inDither = false;
        options.inPurgeable = true;
        TypedArray atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuStripeBackground });
        int backgroundID = atts.getResourceId(0, 0);
        // atts.recycle();
        Bitmap preparedBitmap = BitmapFactory.decodeResource(
                activity.getResources(), backgroundID, options);
        if (mainMenuGalleryBackground != null) {
            mainMenuGalleryBackground.setImageDrawable(new BitmapDrawable(
                    preparedBitmap));
            mainMenuGalleryBackground.setLayoutParams(params3);
            mainMenuGalleryBackground.setScaleType(ScaleType.FIT_XY);
        }
        atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.MainMenuSelectionBoxBackground });
        backgroundID = atts.getResourceId(0, 0);
        atts.recycle();
        Bitmap preparedBitmap1 = BitmapFactory.decodeResource(
                activity.getResources(), backgroundID, options);
        if (mainMenuDialog != null)
            ((ImageView) mainMenuDialog
                    .findViewById(com.iwedia.gui.R.id.mainMenuSelectedImageBackground))
                    .setImageDrawable(new BitmapDrawable(preparedBitmap1));
    }

    /** Return id's of images of current selected sub menu */
    public Integer[] getImagesCurrentSelectedSubMenuItems(int currentState) {
        // Check current state
        switch (currentState) {
            case MainMenuContent.MAIN_MENU: {
                return MainMenuContent.mainMenuIcons;
            }
            case MainMenuContent.SETTINGS: {
                return MainMenuContent.submenuSettings;
            }
            case MainMenuContent.TV_SETTINGS: {
                return MainMenuContent.submenuSettingsTvSettings;
            }
            case MainMenuContent.SECURITY_SETTINGS: {
                return MainMenuContent.submenuSettingsTvSecuritySettings;
            }
            default:
                return MainMenuContent.mainMenuIcons;
        }
    }

    /** Show specific submenu with selected option */
    public void showSpecificSubmenu(int menuCategory, final int selectedOption) {
        // Load menu items from specified category
        loadMenuItems(menuCategory);
        // Refresh gallery
        mainMenuAdapter.notifyDataSetChanged();
        // Find out previous menu category
        int previousCategoty = 0;
        switch (menuCategory) {
            case MainMenuContent.SETTINGS: {
                previousCategoty = MainMenuContent.MAIN_MENU;
                break;
            }
            case MainMenuContent.TV_SETTINGS: {
                previousCategoty = MainMenuContent.SETTINGS;
                break;
            }
            case MainMenuContent.SECURITY_SETTINGS: {
                previousCategoty = MainMenuContent.TV_SETTINGS;
                break;
            }
        }
        // Get previous menu category image
        MainMenuContent.submenuRootResId = getImagesCurrentSelectedSubMenuItems(previousCategoty)[navigationPath
                .get(navigationPath.size() - 1)];
        // Set image of root menu
        transitionItemAnimHandler.getSubmenuRootImage().setBackgroundResource(
                MainMenuContent.submenuRootResId);
        // Redraw main menu dots
        dotsHandler.setNumberOfDots(mainMenuGallery.getCount());
        dotsHandler.setSelectedDot(selectedOption);
        mainMenuGallery.setSelection(selectedOption);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mainMenuGallery.getViewTreeObserver()
                            .addOnGlobalLayoutListener(
                                    new OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            if (mainMenuGallery.isShown()) {
                                                mainMenuGallery
                                                        .getViewTreeObserver()
                                                        .removeGlobalOnLayoutListener(
                                                                this);
                                                mainMenuGallery.performItemClick(
                                                        mainMenuAdapter
                                                                .getView(
                                                                        selectedOption,
                                                                        null,
                                                                        null),
                                                        selectedOption,
                                                        mainMenuAdapter
                                                                .getItemId(selectedOption));
                                            }
                                        }
                                    });
                    showMainMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 400);
        // Update key listener state
        MainKeyListener.setAppState(MainKeyListener.MAIN_MENU);
    }

    /** Image adapter for gallery */
    public class ImageAdapterForGallery extends BaseAdapter {
        /** Context */
        private Context mContext;

        /** Constructor 1 */
        public ImageAdapterForGallery(Context c) {
            mContext = c;
        }

        public int getCount() {
            return menuItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(MainActivity.mMemoryCache
                    .loadBitmapFromResource(menuItems.get(position)
                            .getMenuImage()));
            imageView.setEnabled(true);
            // Set size of pictures in gallery
            imageView.setLayoutParams(new A4TVGallery.LayoutParams(
                    MainActivity.screenWidth / 5,
                    6 * MainActivity.screenHeight / 20));
            imageView.setScaleType(ScaleType.MATRIX);
            // Set opacity
            imageView.setColorFilter(Color.rgb(elementOpacityMid,
                    elementOpacityMid, elementOpacityMid),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
            // Animate new view on screen
            alphaScaleIn.reset();
            imageView.clearAnimation();
            // Don't add animation on current selected item in gallery
            if (position != mainMenuGallery.getSelectedItemPosition()) {
                imageView.startAnimation(alphaScaleIn);
            }
            return imageView;
        }
    }

    // ////////////////////////////////////////////////
    // Getters and setters
    // ////////////////////////////////////////////////
    /** Get reference of main activity */
    public Activity getActivity() {
        return activity;
    }

    /** Get animation handler of current selected frame layout */
    public Flip3dAnimationHandler getFlip3dAnimationHandler() {
        return flip3dAnimationHandler;
    }

    /** Handler for transition to root of submenu animation */
    public TransitionItemAnimationHandler getTransitionItemAnimHandler() {
        return transitionItemAnimHandler;
    }

    /** Get main menu gallery element */
    public A4TVGallery getMainMenuGallery() {
        return mainMenuGallery;
    }

    /** Get dots handler from main menu */
    public MainMenuDotsHandlingClass getDotsHandler() {
        return dotsHandler;
    }

    /** Get main menu dialog */
    public A4TVDialog getMainMenuDialog() {
        return mainMenuDialog;
    }

    /** Dialog for settings and other stuff */
    public A4TVDialog getSmallDialog() {
        return smallDialog;
    }

    /** Menu items */
    public ArrayList<MenuItem> getMenuItems() {
        return menuItems;
    }

    /** Get TextView for name of selected item in main menu */
    public A4TVTextView getSelectedItemName() {
        return selectedItemName;
    }

    /** Reference of description of selected menu item */
    public A4TVTextView getSelectedItemDescription() {
        return selectedItemDescription;
    }

    /** Get dialog creator object */
    public DialogCreatorClass getDialogCreator() {
        return dialogCreator;
    }

    /** Set content of small dialog for animation */
    public View getContentOfSmallDialog() {
        return contentOfSmallDialog;
    }

    /** Get content of small dialog for animation */
    public void setContentOfSmallDialog(View contentOfSmallDialog) {
        this.contentOfSmallDialog = contentOfSmallDialog;
    }

    /** Get reference of select listener */
    public A4TVGalleryOnSelectListener getA4TVOnSelectLister() {
        return a4TVOnSelectLister;
    }

    public ImageAdapterForGallery getMainMenuAdapter() {
        return mainMenuAdapter;
    }
}
