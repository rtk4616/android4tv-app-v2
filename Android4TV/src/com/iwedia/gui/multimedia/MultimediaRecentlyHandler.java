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
 * Handler for drawing recently content in content list
 * 
 * @author Veljko Ilkic
 */
public class MultimediaRecentlyHandler {
    /** Reference of main activity */
    private Activity activity;
    /** Drawing handler object */
    private MultimediaGridHelper contentHelper;
    /** Number of items per screen */
    public static final int MULTIMEDIA_RECENTLY_ITEMS_PER_SCREEN = 6;
    /** Grid view container */
    private GridView multimediaGridRecently;
    /** Adapter for grid view */
    private MultimediaRecentlyGridAdapter multimediaRecentlyGridAdapter;
    /** Number of recently items */
    public static int multimediaRecentlyNumberOfItems;
    /** Current items visible on screen */
    public static Content[] multimediaRecentlyCurrentItems = new MultimediaContent[MULTIMEDIA_RECENTLY_ITEMS_PER_SCREEN];
    /** GridView scroller object */
    private GridViewScroller gridViewScroller;
    /** Cool down period counters for right and left keys */
    private long rightKeyCoolDownPeriod = 0;
    private long leftKeyCoolDownPeriod = 0;

    /** Constructor 1 */
    public MultimediaRecentlyHandler(Activity activity, GridView gridView) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of grid view
        this.multimediaGridRecently = gridView;
    }

    /** Init function */
    public void initView() {
        contentHelper = new MultimediaGridHelper(activity);
        multimediaGridRecently
                .setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
        multimediaGridRecently.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        multimediaRecentlyGridAdapter = new MultimediaRecentlyGridAdapter();
        multimediaGridRecently.setAdapter(multimediaRecentlyGridAdapter);
        multimediaGridRecently.setOnKeyListener(new RecentlyGridKeyListener());
        multimediaGridRecently
                .setOnItemClickListener(new RecentlyGridOnItemClick());
        gridViewScroller = new GridViewScroller(
                GridViewScroller.MULTIMEDIA_FIRST_RECENTLY, 1, 6,
                multimediaRecentlyGridAdapter, multimediaGridRecently,
                multimediaRecentlyCurrentItems);
    }

    /** Init data */
    public void initData() {
        gridViewScroller.initData(multimediaRecentlyNumberOfItems);
        // Hide arrows from recently list if there isn't enough content items
        if (multimediaRecentlyNumberOfItems < MULTIMEDIA_RECENTLY_ITEMS_PER_SCREEN) {
            ((MainActivity) activity).getMultimediaHandler()
                    .hideFirstScreenRecentlyArrows();
        } else {
            ((MainActivity) activity).getMultimediaHandler()
                    .showFirstScreenRecentlyArrows();
        }
    }

    /** Focus active element on beginning */
    public void focusActiveElement(int activeElement) {
        multimediaGridRecently.requestFocusFromTouch();
        multimediaGridRecently.setSelection(activeElement
                % MULTIMEDIA_RECENTLY_ITEMS_PER_SCREEN);
    }

    /** Check focused element for scrolling right */
    private boolean checkFocusNextRecently() {
        if (multimediaGridRecently.getSelectedItemPosition() == 5) {
            return true;
        }
        return false;
    }

    /** Check focused element for scrolling left */
    private boolean checkFocusPreviousRecently() {
        if (multimediaGridRecently.getSelectedItemPosition() == 0) {
            return true;
        }
        return false;
    }

    /** Grid adapter for recently */
    public class MultimediaRecentlyGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return multimediaRecentlyCurrentItems.length;
        }

        @Override
        public Object getItem(int position) {
            return multimediaRecentlyCurrentItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retVal = contentHelper
                    .prepareDataForAdapter((MultimediaContent) multimediaRecentlyCurrentItems[position]);
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
            if (gridViewScroller.getIndexOfLastElement() == multimediaRecentlyNumberOfItems - 1) {
                if (position == MULTIMEDIA_RECENTLY_ITEMS_PER_SCREEN - 1
                        || position == multimediaRecentlyNumberOfItems - 1) {
                    // Hide divider of right
                    ImageView dividerSmall = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                    dividerSmall.setImageBitmap(null);
                    ImageView dividerBig = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                    dividerBig.setImageBitmap(null);
                }
                ((MainActivity) activity).getMultimediaHandler()
                        .getFirstScreenRecentlyRightArrow()
                        .setVisibility(View.INVISIBLE);
            }
            // Last element isn't visible show right arrow
            else {
                ((MainActivity) activity).getMultimediaHandler()
                        .getFirstScreenRecentlyRightArrow()
                        .setVisibility(View.VISIBLE);
            }
            return retVal;
        }
    }

    /** Get selected real selected position */
    private int getSelectedPosition() {
        try {
            return gridViewScroller.getIndexOfFirstElement()
                    + multimediaGridRecently.getSelectedItemPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Select grid below */
    private void selectGridDown() {
        // Select favorite grid if it's available
        if (((MainActivity) activity).getMultimediaHandler()
                .getMutlimediaFavoriteHandler().getMultimediaGridFavorite()
                .isFocusable()) {
            ((MainActivity) activity).getMultimediaHandler()
                    .getMutlimediaFavoriteHandler().focusActiveElement(0);
        } else {
            // Select all if it's available, in case that favorites are
            // unavailable
            if (((MainActivity) activity).getMultimediaHandler()
                    .getMultimediaFileBrowserFirstHandler()
                    .getGridFileBrowser().isFocusable()) {
                ((MainActivity) activity).getMultimediaHandler()
                        .getMultimediaFileBrowserFirstHandler()
                        .focusActiveElement(0);
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
                        if (getSelectedPosition() + 1 > multimediaRecentlyNumberOfItems - 1) {
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
                    case KeyEvent.KEYCODE_F4: {
                        // pageNextRecently();
                        gridViewScroller.pageNext();
                        return false;
                    }
                    case KeyEvent.KEYCODE_1: {
                        // pagePreviousRecently();
                        gridViewScroller.pagePrevious();
                        return false;
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
        // TODO: Applies only on main display
        private int mDisplayId = 0;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Get clicked content object
            MultimediaContent contentClicked = (MultimediaContent) multimediaRecentlyCurrentItems[arg2];
            if (contentClicked != null) {
                // Check if it is recorder pvr file
                // if (contentClicked.getFilterType() ==
                // FilterType.PVR_RECORDED) {
                //
                // // Check pvr is available on mass storage
                // // TODO
                //
                // }
                contentHelper.goContent(contentClicked, false, mDisplayId);
            }
        }
    }

    /** Set current screen */
    public void setCurrentPage(int indexOfCurrentPage) {
        gridViewScroller.setIndexOfCurrentScreen(0);
    }

    // ///////////////////////////////////////////
    // Getters and setters
    // ///////////////////////////////////////////
    public GridView getMultimediaGridRecently() {
        return multimediaGridRecently;
    }

    public MultimediaRecentlyGridAdapter getMultimediaRecentlyGridAdapter() {
        return multimediaRecentlyGridAdapter;
    }
}
