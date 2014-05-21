package com.iwedia.gui.content_list;

import android.app.Activity;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.iwedia.comm.content.Content;
import com.iwedia.gui.MainActivity;

/**
 * Handler for drawing All content in content list
 * 
 * @author Veljko Ilkic
 */
public class AllHandler {
    /** Log tag name */
    private final static String LOG_TAG = "AllHandler";
    /** Reference of main activity */
    private Activity activity;
    /** Drawing handler object */
    public static GridHelper contentHelper;
    /** Used for index conversion */
    public static final int[] GRID_VIEW_INDEX_CONVERTOR = { 0, 2, 4, 6, 8, 10,
            1, 3, 5, 7, 9, 11 };
    /** Used for index conversion */
    public static final int[] GRID_VIEW_INDEX_CONVERTOR_BACKWARD = { 0, 6, 1,
            7, 2, 8, 3, 9, 4, 10, 5, 11 };
    /** Number of visible items per screen */
    public static final int ALL_ITEMS_PER_SCREEN = 12;
    /** Grid view container */
    private GridView gridAll;
    /** Adapter for grid view */
    private AllGridAdapter allGridAdapter;
    /** Current visible screen */
    // public static int allNumberOfCurrentScreen = 0;
    /** Number of all items */
    public static int allNumberOfItems;
    /** Current active index */
    public static int currentActive;
    /** Current visible items */
    public static Content[] allCurrentItems = new Content[ALL_ITEMS_PER_SCREEN];
    /** Grid view scroller */
    private GridViewScroller gridViewScroller;
    /** Index of element that needs to be focused */
    int indexToFocus = 0;
    /** Cool down period counters for right and left keys */
    private long rightKeyCoolDownPeriod = 0;
    private long leftKeyCoolDownPeriod = 0;

    /** Constructor 1 */
    public AllHandler(Activity activity, GridView gridView) {
        super();
        // Take reference of activity
        this.activity = activity;
        // Take reference of grid view
        this.gridAll = gridView;
    }

    /** Init function */
    public void initView() {
        // Create grid helper
        contentHelper = new GridHelper(activity);
        // Set number of columns
        gridAll.setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
        // Set up stretch mode for grid
        gridAll.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        // Attach adapter
        allGridAdapter = new AllGridAdapter();
        gridAll.setAdapter(allGridAdapter);
        // Set up listeners
        gridAll.setOnKeyListener(new AllGridKeyListener());
        gridAll.setOnItemClickListener(new AllGridOnItemClick());
        gridViewScroller = new GridViewScroller(
                GridViewScroller.CONTENT_LIST_ALL, 2, 12, allGridAdapter,
                gridAll, allCurrentItems);
    }

    /** Init data */
    public void initData() {
        gridViewScroller.initData(allNumberOfItems);
        // Hide arrows from all list if there isn't enough content items
        if (allNumberOfItems < ALL_ITEMS_PER_SCREEN) {
            ((MainActivity) activity).getContentListHandler()
                    .hideArrowsAllList();
        } else {
            ((MainActivity) activity).getContentListHandler()
                    .showArrowsAllList();
        }
    }

    /**
     * Focus active element on beginning
     * 
     * @param index
     *        index of content in imaginary local list
     */
    public void focusActiveElement(int index) {
        // Request focus needed
        gridAll.requestFocusFromTouch();
        // Calculate index in current visible page
        int tempIndex = index % 12;
        if (tempIndex % 2 == 0) {
            indexToFocus = tempIndex / 2;
        } else {
            indexToFocus = tempIndex / 2 + 6;
        }
        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Set selection
                gridAll.setSelection(indexToFocus);
            }
        }, 50);
    }

    /** Check if last two elements has focus */
    private boolean checkFocusNextAll() {
        // If selected position is last in first row
        if (gridAll.getSelectedItemPosition() == ALL_ITEMS_PER_SCREEN / 2 - 1) {
            return true;
        }
        // If selected position is last in second row
        if (gridAll.getSelectedItemPosition() == ALL_ITEMS_PER_SCREEN - 1) {
            return true;
        }
        return false;
    }

    /** Check if last two elements has focus */
    private boolean checkFocusPreviousAll() {
        // If selected position is first in first row
        if (gridAll.getSelectedItemPosition() == 0) {
            return true;
        }
        // If selected position is first in second row
        if (gridAll.getSelectedItemPosition() == ALL_ITEMS_PER_SCREEN / 2) {
            return true;
        }
        return false;
    }

    /** Grid view adapter */
    public class AllGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return allCurrentItems.length;
        }

        @Override
        public Object getItem(int position) {
            return allCurrentItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retVal = contentHelper
                    .prepareDataForAdapter(allCurrentItems[position]);
            // If first item is visible hide left arrow
            if (gridViewScroller.getIndexOfFirstElement() == 0) {
                ((MainActivity) activity).getContentListHandler()
                        .getContentAllArrowLeft().setVisibility(View.INVISIBLE);
            }
            // First element isn't visible show left arrow
            else {
                ((MainActivity) activity).getContentListHandler()
                        .getContentAllArrowLeft().setVisibility(View.VISIBLE);
            }
            // If last element is visible hide right arrow and divider
            if (gridViewScroller.getIndexOfLastElement() == allNumberOfItems - 1) {
                if (position == ALL_ITEMS_PER_SCREEN - 1
                        || position == ALL_ITEMS_PER_SCREEN / 2 - 1) {
                    // Hide divider of right
                    ImageView dividerSmall = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                    dividerSmall.setImageBitmap(null);
                    ImageView dividerBig = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                    dividerBig.setImageBitmap(null);
                }
                // Check if there is less contents then 12
                if (allNumberOfItems < ALL_ITEMS_PER_SCREEN
                        && allNumberOfItems > 0) {
                    if (allNumberOfItems % 2 == 0) {
                        if (position == GRID_VIEW_INDEX_CONVERTOR_BACKWARD[allNumberOfItems - 1]
                                || position == GRID_VIEW_INDEX_CONVERTOR_BACKWARD[allNumberOfItems - 2]) {
                            // Hide divider of right
                            ImageView dividerSmall = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                            dividerSmall.setImageBitmap(null);
                            ImageView dividerBig = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                            dividerBig.setImageBitmap(null);
                        }
                    } else {
                        if (position == GRID_VIEW_INDEX_CONVERTOR_BACKWARD[allNumberOfItems - 1]) {
                            // Hide divider of right
                            ImageView dividerSmall = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                            dividerSmall.setImageBitmap(null);
                            ImageView dividerBig = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                            dividerBig.setImageBitmap(null);
                        }
                    }
                }
                ((MainActivity) activity).getContentListHandler()
                        .getContentAllArrowRight()
                        .setVisibility(View.INVISIBLE);
            }
            // Last element isn't visible show right arrow
            else {
                ((MainActivity) activity).getContentListHandler()
                        .getContentAllArrowRight().setVisibility(View.VISIBLE);
            }
            return retVal;
        }
    }

    /** Get selected real selected position */
    private int getSelectedPosition() {
        try {
            return gridViewScroller.getIndexOfFirstElement()
                    + GRID_VIEW_INDEX_CONVERTOR[gridAll
                            .getSelectedItemPosition()];
        } catch (Exception e) {
            return 0;
        }
    }

    /** Select upper grid */
    private void selectGridUp() {
        // Check if favorites is available
        if (((MainActivity) activity).getContentListHandler()
                .getFavoriteHandler().getGridFavorite().isFocusable()) {
            ((MainActivity) activity).getContentListHandler()
                    .getFavoriteHandler().focusActiveElement(0);
        } else {
            // Check if recently is available
            if (((MainActivity) activity).getContentListHandler()
                    .getRecentlyHandler().getGridRecently().isFocusable()) {
                ((MainActivity) activity).getContentListHandler()
                        .getRecentlyHandler().focusActiveElement(0);
            } else {
                ((MainActivity) activity).getContentListHandler()
                        .getContentFilterOptionsScroll().requestFocus();
            }
        }
    }

    /** Key listener for grid */
    private class AllGridKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_PROG_YELLOW:
                    case KeyEvent.KEYCODE_Y: {
                        // Get clicked content object and indicate secondary
                        // display playback
                        Content contentClicked = allCurrentItems[gridAll
                                .getSelectedItemPosition()];
                        if (MainActivity.activity.getDualVideoManager()
                                .playPiP(contentClicked)) {
                            ((MainActivity) activity).getContentListHandler()
                                    .closeContentList();
                            return true;
                        } else {
                            return false;
                        }
                    }
                    case KeyEvent.KEYCODE_PROG_BLUE:
                    case KeyEvent.KEYCODE_B: {
                        // Get clicked content object and indicate secondary
                        // display playback
                        Content contentClicked = allCurrentItems[gridAll
                                .getSelectedItemPosition()];
                        if (MainActivity.activity.getDualVideoManager()
                                .playPaP(contentClicked)) {
                            ((MainActivity) activity).getContentListHandler()
                                    .closeContentList();
                            return true;
                        } else {
                            return false;
                        }
                    }
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        if (gridAll.getSelectedItemPosition() < ALL_ITEMS_PER_SCREEN / 2) {
                            selectGridUp();
                            return true;
                        } else {
                            return false;
                        }
                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        if (gridAll.getSelectedItemPosition() != -1) {
                            // Check if focus can move down
                            if (getSelectedPosition() + 1 > allNumberOfItems - 1) {
                                return true;
                            }
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
                        // Check if cool down period is enabled
                        if (MainActivity.enableKeyHandlingCoolDownPerion) {
                            // Ignore cool down first time and key handling
                            // wasn't
                            // active for some time
                            if (rightKeyCoolDownPeriod != 0
                                    && (System.currentTimeMillis()
                                            + 2
                                            * MainActivity.KEY_HANDLING_COOL_DOWN_PERIOD > rightKeyCoolDownPeriod)) {
                                // Check when last time key event was here
                                if (System.currentTimeMillis()
                                        - rightKeyCoolDownPeriod < MainActivity.KEY_HANDLING_COOL_DOWN_PERIOD
                                        && System.currentTimeMillis()
                                                - rightKeyCoolDownPeriod > 0) {
                                    return true;
                                }
                            }
                            rightKeyCoolDownPeriod = System.currentTimeMillis();
                        }
                        // //////////////////////////////////
                        // Odd number of content items
                        // /////////////////////////////////
                        if (allNumberOfItems % 2 != 0) {
                            // //////////////////////////////
                            // First row
                            // //////////////////////////////
                            if (getSelectedPosition() % 2 == 0) {
                                // Check if focus can move
                                if (getSelectedPosition() + 2 > allNumberOfItems - 1) {
                                    return true;
                                }
                            }
                            // //////////////////////////////
                            // Second row
                            // //////////////////////////////
                            else {
                                // Check if focus can move
                                if (getSelectedPosition() + 1 > allNumberOfItems - 1) {
                                    return true;
                                }
                                if (getSelectedPosition() + 1 == allNumberOfItems - 1) {
                                    int positionToSelect;
                                    if (allNumberOfItems < ALL_ITEMS_PER_SCREEN) {
                                        positionToSelect = GRID_VIEW_INDEX_CONVERTOR_BACKWARD[allNumberOfItems - 1];
                                        gridAll.requestFocusFromTouch();
                                        gridAll.setSelection(positionToSelect);
                                        return true;
                                    } else {
                                        positionToSelect = ALL_ITEMS_PER_SCREEN / 2 - 1;
                                        gridAll.requestFocusFromTouch();
                                        gridAll.setSelection(positionToSelect);
                                    }
                                }
                                // //////////////////////////////////////
                                // Check if focus can move
                                if (getSelectedPosition() + 1 > allNumberOfItems - 1) {
                                    return true;
                                }
                                if (getSelectedPosition() + 1 == allNumberOfItems - 1) {
                                    gridAll.requestFocusFromTouch();
                                    gridAll.setSelection(ALL_ITEMS_PER_SCREEN / 2 - 1);
                                }
                            }
                        }
                        // /////////////////////////////////
                        // Even number of content items
                        // /////////////////////////////////
                        else {
                            // //////////////////////////////
                            // First row
                            // //////////////////////////////
                            if (getSelectedPosition() % 2 == 0) {
                                // Check if focus can move
                                if (getSelectedPosition() + 3 > allNumberOfItems - 1) {
                                    return true;
                                }
                            }
                            // //////////////////////////////
                            // Second row
                            // //////////////////////////////
                            else {
                                // Check if focus can move
                                if (getSelectedPosition() + 2 > allNumberOfItems - 1) {
                                    return true;
                                }
                            }
                        }
                        if (checkFocusNextAll()) {
                            gridViewScroller.scrollRight();
                            return true;
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        // Check if cool down period is enabled
                        if (MainActivity.enableKeyHandlingCoolDownPerion) {
                            // Check when last time key event was here
                            if (System.currentTimeMillis()
                                    - leftKeyCoolDownPeriod < MainActivity.KEY_HANDLING_COOL_DOWN_PERIOD
                                    && System.currentTimeMillis()
                                            - leftKeyCoolDownPeriod > 0) {
                                return true;
                            }
                            leftKeyCoolDownPeriod = System.currentTimeMillis();
                        }
                        if (checkFocusPreviousAll()) {
                            gridViewScroller.scrollLeft();
                            // scrollLeftAll();
                            return true;
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_FORWARD:
                    case KeyEvent.KEYCODE_0: {
                        // pageNextAll();
                        gridViewScroller.pageNext();
                        return true;
                    }
                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                    case KeyEvent.KEYCODE_1: {
                        // pagePreviousAll();
                        gridViewScroller.pagePrevious();
                        return true;
                    }
                }
                return false;
            }
            // On action up
            return false;
        }
    }

    /** OnItemClick listener for grid */
    private class AllGridOnItemClick implements OnItemClickListener {
        // TODO: Applies on main display only
        private int mDisplayId = 0;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Get clicked content object
            Content contentClicked = allCurrentItems[arg2];
            if (contentClicked != null) {
                if (MainActivity.activity.getDualVideoManager()
                        .checkSupportedScenario(contentClicked, mDisplayId)) {
                    contentHelper.goContent(contentClicked, mDisplayId);
                }
            }
        }
    }

    /** Calculate page of current active tv service */
    public void setCurrentScreen(int index) {
        gridViewScroller.setIndexOfCurrentScreen(index / ALL_ITEMS_PER_SCREEN);
    }

    // ////////////////////////////////////
    // Getters and setters
    // ////////////////////////////////////
    public AllGridAdapter getAllGridAdapter() {
        return allGridAdapter;
    }

    public GridView getGridAll() {
        return gridAll;
    }
}
