package com.iwedia.gui.content_list;

import android.app.Activity;
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
import com.iwedia.gui.osd.OSDGlobal;

/**
 * Handler for drawing recently content in content list
 * 
 * @author Veljko Ilkic
 */
public class RecentlyHandler {
    /** Reference of main activity */
    private Activity activity;
    /** Drawing handler object */
    private GridHelper contentHelper;
    /** Number of items per screen */
    public static final int RECENTLY_ITEMS_PER_SCREEN = 6;
    /** Grid view container */
    private GridView gridRecently;
    /** Adapter for grid view */
    private RecentlyGridAdapter recentlyGridAdapter;
    /** Number of recently items */
    public static int recentlyNumberOfItems;
    /** Current items visible on screen */
    public static Content[] recentlyCurrentItems = new Content[RECENTLY_ITEMS_PER_SCREEN];
    private GridViewScroller gridViewScroller;
    /** Cool down period counters for right and left keys */
    private long rightKeyCoolDownPeriod = 0;
    private long leftKeyCoolDownPeriod = 0;

    /** Constructor 1 */
    public RecentlyHandler(Activity activity, GridView gridView) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of grid view
        this.gridRecently = gridView;
    }

    /** Init function */
    public void initView() {
        contentHelper = new GridHelper(activity);
        gridRecently.setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
        gridRecently.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        recentlyGridAdapter = new RecentlyGridAdapter();
        gridRecently.setAdapter(recentlyGridAdapter);
        gridRecently.setOnKeyListener(new RecentlyGridKeyListener());
        gridRecently.setOnItemClickListener(new RecentlyGridOnItemClick());
        gridViewScroller = new GridViewScroller(
                GridViewScroller.CONTENT_LIST_RECENTLY, 1, 6,
                recentlyGridAdapter, gridRecently, recentlyCurrentItems);
    }

    /** Init data */
    public void initData() {
        gridViewScroller.initData(recentlyNumberOfItems);
        // Hide arrows from recently list if there isn't enough content items
        if (recentlyNumberOfItems < RECENTLY_ITEMS_PER_SCREEN) {
            ((MainActivity) activity).getContentListHandler()
                    .hideArrowsRecentlyList();
        } else {
            ((MainActivity) activity).getContentListHandler()
                    .showArrowsRecentlyList();
        }
    }

    /** Calculate page of current active tv service */
    public void setCurrentPage(int index) {
        gridViewScroller.setIndexOfCurrentScreen(index);
    }

    /** Focus active element on beginning */
    public void focusActiveElement(int activeElement) {
        gridRecently.requestFocusFromTouch();
        gridRecently.setSelection(activeElement % RECENTLY_ITEMS_PER_SCREEN);
    }

    /** Check focused element for scrolling right */
    private boolean checkFocusNextRecently() {
        if (gridRecently.getSelectedItemPosition() == 5) {
            return true;
        }
        return false;
    }

    /** Check focused element for scrolling left */
    private boolean checkFocusPreviousRecently() {
        if (gridRecently.getSelectedItemPosition() == 0) {
            return true;
        }
        return false;
    }

    /** Grid adapter for recently */
    public class RecentlyGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return recentlyCurrentItems.length;
        }

        @Override
        public Object getItem(int position) {
            return recentlyCurrentItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retVal = contentHelper
                    .prepareDataForAdapter(recentlyCurrentItems[position]);
            // If first item is visible hide left arrow
            if (gridViewScroller.getIndexOfFirstElement() == 0) {
                ((MainActivity) activity).getContentListHandler()
                        .getContentRecentlyArrowLeft()
                        .setVisibility(View.INVISIBLE);
            }
            // First element isn't visible show left arrow
            else {
                ((MainActivity) activity).getContentListHandler()
                        .getContentRecentlyArrowLeft()
                        .setVisibility(View.VISIBLE);
            }
            if (gridViewScroller.getIndexOfLastElement() == recentlyNumberOfItems - 1) {
                if (position == RECENTLY_ITEMS_PER_SCREEN - 1
                        || position == recentlyNumberOfItems - 1) {
                    // Hide divider of right
                    ImageView dividerSmall = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                    dividerSmall.setImageBitmap(null);
                    ImageView dividerBig = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                    dividerBig.setImageBitmap(null);
                }
                ((MainActivity) activity).getContentListHandler()
                        .getContentRecentlyArrowRight()
                        .setVisibility(View.INVISIBLE);
            }
            // Last element isn't visible show right arrow
            else {
                ((MainActivity) activity).getContentListHandler()
                        .getContentRecentlyArrowRight()
                        .setVisibility(View.VISIBLE);
            }
            return retVal;
        }
    }

    /** Get selected real selected position */
    private int getSelectedPosition() {
        try {
            return gridViewScroller.getIndexOfFirstElement()
                    + gridRecently.getSelectedItemPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Select grid below */
    private void selectGridDown() {
        // Select favorite grid if it's available
        if (((MainActivity) activity).getContentListHandler()
                .getFavoriteHandler().getGridFavorite().isFocusable()) {
            ((MainActivity) activity).getContentListHandler()
                    .getFavoriteHandler().focusActiveElement(0);
        } else {
            // Select all if it's available, in case that favoirites are
            // unavailable
            if (((MainActivity) activity).getContentListHandler()
                    .getAllHandler().getGridAll().isFocusable()) {
                ((MainActivity) activity).getContentListHandler()
                        .getAllHandler().focusActiveElement(0);
            }
        }
    }

    /** Key listener for grid */
    private class RecentlyGridKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        selectGridDown();
                        return true;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT: {
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
                        // Check if focus can move right
                        if (getSelectedPosition() + 1 > recentlyNumberOfItems - 1) {
                            return true;
                        }
                        if (checkFocusNextRecently()) {
                            // scrollRightRecently();
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
                                            - rightKeyCoolDownPeriod > 0) {
                                return true;
                            }
                            leftKeyCoolDownPeriod = System.currentTimeMillis();
                        }
                        if (checkFocusPreviousRecently()) {
                            // scrollLeftRecently();
                            gridViewScroller.scrollLeft();
                            return true;
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_FORWARD:
                    case KeyEvent.KEYCODE_0: {
                        // pageNextRecently();
                        gridViewScroller.pageNext();
                        return true;
                    }
                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                    case KeyEvent.KEYCODE_1: {
                        // pagePreviousRecently();
                        gridViewScroller.pagePrevious();
                        return true;
                    }
                }
                return false;
            }
            // Action Up
            return false;
        }
    }

    /** OnItemClick listener for grid */
    private class RecentlyGridOnItemClick implements OnItemClickListener,
            OSDGlobal {
        // TODO: Applies on main display only
        private int mDisplayId = 0;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Get clicked content object
            Content contentClicked = recentlyCurrentItems[arg2];
            if (contentClicked != null) {
                contentHelper.goContent(contentClicked, mDisplayId);
            }
        }
    }

    // ////////////////////////////////////
    // Getters and setters
    // ////////////////////////////////////
    public GridView getGridRecently() {
        return gridRecently;
    }

    public RecentlyGridAdapter getRecentlyGridAdapter() {
        return recentlyGridAdapter;
    }
}
