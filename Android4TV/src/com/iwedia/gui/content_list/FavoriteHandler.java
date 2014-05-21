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
 * Handler for drawing favorite content in content list
 * 
 * @author Veljko Ilkic
 */
public class FavoriteHandler {
    /** Reference of main activity */
    private Activity activity;
    /** Drawing handler object */
    private GridHelper contentHelper;
    /** Number of visible items on screen */
    public static final int FAVORITE_ITEMS_PER_SCREEN = 6;
    /** Grid view container */
    private GridView gridFavorite;
    /** Adapter for grid view */
    public FavoriteGridAdapter favoriteGridAdapter;
    /** Number of items in whole grid view */
    public static int favoriteNumberOfItems;
    /** Current items visible on screen */
    public static Content[] favoriteCurrentItems = new Content[FAVORITE_ITEMS_PER_SCREEN];
    /** Grid scroller class */
    private GridViewScroller gridViewScroller;
    /** Cool down period counters for right and left keys */
    private long rightKeyCoolDownPeriod = 0;
    private long leftKeyCoolDownPeriod = 0;

    /** Constructor 1 */
    public FavoriteHandler(Activity activity, GridView gridView) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of grid view
        this.gridFavorite = gridView;
    }

    /** Init function */
    public void initView() {
        contentHelper = new GridHelper(activity);
        if (gridFavorite != null) {
            gridFavorite.setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
            gridFavorite.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            favoriteGridAdapter = new FavoriteGridAdapter();
            gridFavorite.setAdapter(favoriteGridAdapter);
            gridFavorite.setOnKeyListener(new FavoriteGridKeyListener());
            gridFavorite.setOnItemClickListener(new FavoriteGridOnItemClick());
            gridViewScroller = new GridViewScroller(
                    GridViewScroller.CONTENT_LIST_FAVORITE, 1, 6,
                    favoriteGridAdapter, gridFavorite, favoriteCurrentItems);
        }
    }

    /** Init data */
    public void initData() {
        gridViewScroller.initData(favoriteNumberOfItems);
        // Hide arrows from favorite list if there isn't enough content items
        if (favoriteNumberOfItems < FAVORITE_ITEMS_PER_SCREEN) {
            ((MainActivity) activity).getContentListHandler()
                    .hideArrowsFavoriteList();
        } else {
            ((MainActivity) activity).getContentListHandler()
                    .showArrowsFavoriteList();
        }
    }

    /** Focus active element on beginning */
    public void focusActiveElement(int activeElement) {
        gridFavorite.requestFocusFromTouch();
        gridFavorite.setSelection(activeElement % FAVORITE_ITEMS_PER_SCREEN);
    }

    /** Check focused element for scrolling right */
    private boolean checkFocusNextFavorite() {
        if (gridFavorite.getSelectedItemPosition() == 5) {
            return true;
        }
        return false;
    }

    /** Check focused element for scrolling left */
    private boolean checkFocusPreviousFavorite() {
        if (gridFavorite.getSelectedItemPosition() == 0) {
            return true;
        }
        return false;
    }

    /** Grid adapter for favorite */
    public class FavoriteGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return favoriteCurrentItems.length;
        }

        @Override
        public Object getItem(int position) {
            return favoriteCurrentItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retVal = contentHelper
                    .prepareDataForAdapter(favoriteCurrentItems[position]);
            // If first item is visible hide left arrow
            if (gridViewScroller.getIndexOfFirstElement() == 0) {
                ((MainActivity) activity).getContentListHandler()
                        .getContentFavoriteArrowLeft()
                        .setVisibility(View.INVISIBLE);
            }
            // First element isn't visible show left arrow
            else {
                ((MainActivity) activity).getContentListHandler()
                        .getContentFavoriteArrowLeft()
                        .setVisibility(View.VISIBLE);
            }
            // Hide divider of right
            if (gridViewScroller.getIndexOfLastElement() == favoriteNumberOfItems - 1) {
                if (position == FAVORITE_ITEMS_PER_SCREEN - 1
                        || position == favoriteNumberOfItems - 1) {
                    ImageView dividerSmall = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                    dividerSmall.setImageBitmap(null);
                    ImageView dividerBig = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                    dividerBig.setImageBitmap(null);
                }
                ((MainActivity) activity).getContentListHandler()
                        .getContentFavoriteArrowRight()
                        .setVisibility(View.INVISIBLE);
            }
            // Last element isn't visible show right arrow
            else {
                ((MainActivity) activity).getContentListHandler()
                        .getContentFavoriteArrowRight()
                        .setVisibility(View.VISIBLE);
            }
            return retVal;
        }
    }

    /** Get selected real selected position */
    private int getSelectedPosition() {
        try {
            return gridViewScroller.getIndexOfFirstElement()
                    + gridFavorite.getSelectedItemPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Select upper grid */
    private void selectGridUp() {
        // Select recently grid if its available
        if (((MainActivity) activity).getContentListHandler()
                .getRecentlyHandler().getGridRecently().isFocusable()) {
            ((MainActivity) activity).getContentListHandler()
                    .getRecentlyHandler().focusActiveElement(0);
        } else {
            ((MainActivity) activity).getContentListHandler()
                    .getContentFilterOptionsScroll().requestFocus();
        }
    }

    /** Select grid below */
    private void selectGridDown() {
        // Select all grid if it's available
        if (((MainActivity) activity).getContentListHandler().getAllHandler()
                .getGridAll().isFocusable()) {
            ((MainActivity) activity).getContentListHandler().getAllHandler()
                    .focusActiveElement(0);
        }
    }

    /** Key listener for grid */
    private class FavoriteGridKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        selectGridUp();
                        return true;
                    }
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
                        if (getSelectedPosition() + 1 > favoriteNumberOfItems - 1) {
                            return true;
                        }
                        if (checkFocusNextFavorite()) {
                            // scrollRightFavorite();
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
                        if (checkFocusPreviousFavorite()) {
                            // scrollLeftFavorite();
                            gridViewScroller.scrollLeft();
                            return true;
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_FORWARD:
                    case KeyEvent.KEYCODE_0: {
                        // pageNextFavorite();
                        gridViewScroller.pageNext();
                        return true;
                    }
                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                    case KeyEvent.KEYCODE_1: {
                        // pagePreviousFavorite();
                        gridViewScroller.pagePrevious();
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }

    /** OnItemClick listener for grid */
    private class FavoriteGridOnItemClick implements OnItemClickListener,
            OSDGlobal {
        // TODO: Applies on main display only
        private int mDisplayId = 0;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Get clicked content object
            Content contentClicked = favoriteCurrentItems[arg2];
            if (contentClicked != null) {
                contentHelper.goContent(contentClicked, mDisplayId);
            }
        }
    }

    /** Calculate page of current active tv service */
    public void setCurrentPage(int index) {
        gridViewScroller.setIndexOfCurrentScreen(index);
    }

    // //////////////////////////////////
    // Getters and setters
    // //////////////////////////////////
    public FavoriteGridAdapter getFavoriteGridAdapter() {
        return favoriteGridAdapter;
    }

    public GridView getGridFavorite() {
        return gridFavorite;
    }
}
