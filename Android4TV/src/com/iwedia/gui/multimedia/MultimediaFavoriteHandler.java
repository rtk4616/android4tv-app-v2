package com.iwedia.gui.multimedia;

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
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.content_list.ContentListHandler;
import com.iwedia.gui.content_list.GridViewScroller;
import com.iwedia.gui.osd.OSDGlobal;

/**
 * Handler for drawing favorite content in content list
 * 
 * @author Veljko Ilkic
 */
public class MultimediaFavoriteHandler {
    /** Reference of main activity */
    private Activity activity;
    /** Drawing handler object */
    private MultimediaGridHelper contentHelper;
    /** Number of visible items on screen */
    public static final int MULTIMEDIA_FAVORITE_ITEMS_PER_SCREEN = 6;
    /** Grid view container */
    private GridView multimediaGridFavorite;
    /** Adapter for grid view */
    public MultimediaFavoriteGridAdapter multimediaFavoriteGridAdapter;
    /** Number of items in favorite grid view */
    public static int multimediaFavoriteNumberOfItems;
    /** Current items visible on screen */
    public static Content[] multimediaFavoriteCurrentItems = new MultimediaContent[MULTIMEDIA_FAVORITE_ITEMS_PER_SCREEN];
    /** GridView scroller */
    private GridViewScroller gridViewScroller;
    /** Cool down period counters for right and left keys */
    private long rightKeyCoolDownPeriod = 0;
    private long leftKeyCoolDownPeriod = 0;

    /** Constructor 1 */
    public MultimediaFavoriteHandler(Activity activity, GridView gridView,
            boolean contentList_multimedia) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of grid view
        this.multimediaGridFavorite = gridView;
    }

    /** Init function */
    public void initView() {
        // Create content helper
        contentHelper = new MultimediaGridHelper(activity);
        // Set number of columns
        multimediaGridFavorite
                .setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
        multimediaGridFavorite.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        // Attach adapter
        multimediaFavoriteGridAdapter = new MultimediaFavoriteGridAdapter();
        multimediaGridFavorite.setAdapter(multimediaFavoriteGridAdapter);
        // Attach listeners
        multimediaGridFavorite.setOnKeyListener(new FavoriteGridKeyListener());
        multimediaGridFavorite
                .setOnItemClickListener(new FavoriteGridOnItemClick());
        // Create scroller
        gridViewScroller = new GridViewScroller(
                GridViewScroller.MULTIMEDIA_FIRST_FAVORITE, 1, 6,
                multimediaFavoriteGridAdapter, multimediaGridFavorite,
                multimediaFavoriteCurrentItems);
    }

    /** Init data */
    public void initData() {
        gridViewScroller.initData(multimediaFavoriteNumberOfItems);
        // Hide arrows from favorite list if there isn't enough content items
        if (multimediaFavoriteNumberOfItems < MULTIMEDIA_FAVORITE_ITEMS_PER_SCREEN) {
            ((MainActivity) activity).getMultimediaHandler()
                    .hideFirstScreenFavoriteArrows();
        } else {
            ((MainActivity) activity).getMultimediaHandler()
                    .showFirstScreenFavoriteArrows();
        }
    }

    /** Focus active element on beginning */
    public void focusActiveElement(int activeElement) {
        multimediaGridFavorite.requestFocusFromTouch();
        multimediaGridFavorite.setSelection(activeElement
                % MULTIMEDIA_FAVORITE_ITEMS_PER_SCREEN);
    }

    /** Check focused element for scrolling right */
    private boolean checkFocusNextFavorite() {
        if (multimediaGridFavorite.getSelectedItemPosition() == 5) {
            return true;
        }
        return false;
    }

    /** Check focused element for scrolling left */
    private boolean checkFocusPreviousFavorite() {
        if (multimediaGridFavorite.getSelectedItemPosition() == 0) {
            return true;
        }
        return false;
    }

    /** Grid adapter for favorite */
    public class MultimediaFavoriteGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return multimediaFavoriteCurrentItems.length;
        }

        @Override
        public Object getItem(int position) {
            return multimediaFavoriteCurrentItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retVal = contentHelper
                    .prepareDataForAdapter((MultimediaContent) multimediaFavoriteCurrentItems[position]);
            // If first item is visible hide left arrow
            if (gridViewScroller.getIndexOfFirstElement() == 0) {
                ((MainActivity) activity).getMultimediaHandler()
                        .getFirstScreenRecentlyLeftArrow()
                        .setVisibility(View.INVISIBLE);
            }
            // First element isn't visible show left arrow
            else {
                ((MainActivity) activity).getMultimediaHandler()
                        .getFirstScreenRecentlyLeftArrow()
                        .setVisibility(View.VISIBLE);
            }
            if (gridViewScroller.getIndexOfLastElement() == multimediaFavoriteNumberOfItems - 1) {
                if (position == MULTIMEDIA_FAVORITE_ITEMS_PER_SCREEN - 1
                        || position == multimediaFavoriteNumberOfItems - 1) {
                    // Hide divider of right
                    ImageView dividerSmall = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                    dividerSmall.setImageBitmap(null);
                    ImageView dividerBig = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                    dividerBig.setImageBitmap(null);
                }
                ((MainActivity) activity).getMultimediaHandler()
                        .getFirstScreenFavoriteRightArrow()
                        .setVisibility(View.INVISIBLE);
            }
            // Last element isn't visible show right arrow
            else {
                ((MainActivity) activity).getMultimediaHandler()
                        .getFirstScreenFavoriteRightArrow()
                        .setVisibility(View.VISIBLE);
            }
            return retVal;
        }
    }

    /** Get selected real selected position */
    private int getSelectedPosition() {
        try {
            return gridViewScroller.getIndexOfFirstElement()
                    + multimediaGridFavorite.getSelectedItemPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Select upper grid */
    private void selectGridUp() {
        // Select recently grid if its available
        if (((MainActivity) activity).getMultimediaHandler()
                .getMultimediaRecentlyHandler().getMultimediaGridRecently()
                .isFocusable()) {
            ((MainActivity) activity).getMultimediaHandler()
                    .getMultimediaRecentlyHandler().focusActiveElement(0);
        }
    }

    /** Select grid below */
    private void selectGridDown() {
        // Select all grid if it's available
        if (((MainActivity) activity).getMultimediaHandler()
                .getMultimediaFileBrowserFirstHandler().getGridFileBrowser()
                .isFocusable()) {
            ((MainActivity) activity).getMultimediaHandler()
                    .getMultimediaFileBrowserFirstHandler()
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
                        if (getSelectedPosition() + 1 > multimediaFavoriteNumberOfItems - 1) {
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
                    case KeyEvent.KEYCODE_0: {
                        // pageNextFavorite();
                        gridViewScroller.pageNext();
                        return false;
                    }
                    case KeyEvent.KEYCODE_1: {
                        // pagePreviousFavorite();
                        gridViewScroller.pagePrevious();
                        return false;
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
        // TODO: Applies only on main display
        int mDisplayId = 0;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Get clicked content object
            MultimediaContent contentClicked = (MultimediaContent) multimediaFavoriteCurrentItems[arg2];
            if (contentClicked != null) {
                contentHelper.goContent(contentClicked, false, mDisplayId);
            }
        }
    }

    /** Set current screen */
    public void setCurrentPage(int indexOfCurrentPage) {
        gridViewScroller.setIndexOfCurrentScreen(0);
    }

    // //////////////////////////////////
    // Getters and setters
    // //////////////////////////////////
    public MultimediaFavoriteGridAdapter getFavoriteGridAdapter() {
        return multimediaFavoriteGridAdapter;
    }

    public GridView getMultimediaGridFavorite() {
        return multimediaGridFavorite;
    }
}
