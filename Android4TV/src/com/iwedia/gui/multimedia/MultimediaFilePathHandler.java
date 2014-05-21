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
public class MultimediaFilePathHandler {
    /** Reference of main activity */
    private Activity activity;
    /** Drawing handler object */
    private MultimediaGridHelper contentHelper;
    /** Number of items per screen */
    public static final int FILE_PATH_ITEMS_PER_SCREEN = 6;
    /** Grid view container */
    private GridView gridFilePath;
    /** Adapter for grid view */
    private FilePathGridAdapter filePathGridAdapter;
    /** Number of file path items */
    public static int filePathNumberOfItems = 0;
    /** Current items visible on screen */
    public static Content[] filePathCurrentItems = new MultimediaContent[FILE_PATH_ITEMS_PER_SCREEN];
    /** File path list */
    // public static ArrayList<MultimediaContent> filePathList = new
    // ArrayList<MultimediaContent>();
    /** GridView scroller object */
    private GridViewScroller gridViewScroller;
    /** Cool down period counters for right and left keys */
    private long rightKeyCoolDownPeriod = 0;
    private long leftKeyCoolDownPeriod = 0;

    /** Constructor 1 */
    public MultimediaFilePathHandler(Activity activity, GridView gridView) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of grid view
        this.gridFilePath = gridView;
    }

    /** Init function */
    public void initView() {
        contentHelper = new MultimediaGridHelper(activity);
        gridFilePath.setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
        gridFilePath.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        filePathGridAdapter = new FilePathGridAdapter();
        gridFilePath.setAdapter(filePathGridAdapter);
        gridFilePath.setOnKeyListener(new FilePathGridKeyListener());
        gridFilePath.setOnItemClickListener(new FilePathGridOnItemClick());
        gridViewScroller = new GridViewScroller(
                GridViewScroller.MULTIMEDIA_SECOND_FILE_PATH, 1, 6,
                filePathGridAdapter, gridFilePath, filePathCurrentItems);
    }

    /** Init data */
    public void initData() {
        gridViewScroller.initData(filePathNumberOfItems);
        // Hide arrows from file path list if there isn't enough content items
        if (filePathNumberOfItems < FILE_PATH_ITEMS_PER_SCREEN) {
            ((MainActivity) activity).getMultimediaHandler()
                    .hideSecondScreenFilePathArrows();
        } else {
            ((MainActivity) activity).getMultimediaHandler()
                    .showSecondScreenFilePathArrows();
        }
    }

    /** Focus active element on beginning */
    public void focusActiveElement(int activeElement) {
        gridFilePath.requestFocusFromTouch();
        gridFilePath.setSelection(activeElement % FILE_PATH_ITEMS_PER_SCREEN);
    }

    /** Check focused element for scrolling right */
    private boolean checkFocusNextFilePath() {
        if (gridFilePath.getSelectedItemPosition() == 5) {
            return true;
        }
        return false;
    }

    /** Check focused element for scrolling left */
    private boolean checkFocusPreviousFilePath() {
        if (gridFilePath.getSelectedItemPosition() == 0) {
            return true;
        }
        return false;
    }

    /** Grid adapter for recently */
    public class FilePathGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return filePathCurrentItems.length;
        }

        @Override
        public Object getItem(int position) {
            return filePathCurrentItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retVal = contentHelper
                    .prepareDataForAdapter((MultimediaContent) filePathCurrentItems[position]);
            // If first item is visible hide left arrow
            if (gridViewScroller.getIndexOfFirstElement() == 0) {
                ((MainActivity) activity).getMultimediaHandler()
                        .getSecondScreenFilePathLeftArrow()
                        .setVisibility(View.INVISIBLE);
            }
            // First element isn't visible show left arrow
            else {
                ((MainActivity) activity).getMultimediaHandler()
                        .getSecondScreenFilePathLeftArrow()
                        .setVisibility(View.VISIBLE);
            }
            if (gridViewScroller.getIndexOfLastElement() == filePathNumberOfItems - 1) {
                if (position == FILE_PATH_ITEMS_PER_SCREEN - 1
                        || position == filePathNumberOfItems - 1) {
                    // Hide divider of right
                    ImageView dividerSmall = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                    dividerSmall.setImageBitmap(null);
                    ImageView dividerBig = (ImageView) retVal
                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                    dividerBig.setImageBitmap(null);
                }
                ((MainActivity) activity).getMultimediaHandler()
                        .getSecondScreenFilePathRightArrow()
                        .setVisibility(View.INVISIBLE);
            } else {
                ((MainActivity) activity).getMultimediaHandler()
                        .getSecondScreenFilePathRightArrow()
                        .setVisibility(View.VISIBLE);
            }
            return retVal;
        }
    }

    /** Get selected real selected position */
    private int getSelectedPosition() {
        try {
            return gridViewScroller.getIndexOfFirstElement()
                    + gridFilePath.getSelectedItemPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Select grid below */
    private void selectGridDown() {
        // Select favorite grid if it's available
        if (((MainActivity) activity).getMultimediaHandler()
                .getMultimediaFileBrowserSecondHandler().getGridFileBrowser()
                .isFocusable()) {
            ((MainActivity) activity).getMultimediaHandler()
                    .getMultimediaFileBrowserSecondHandler()
                    .focusActiveElement(0);
        }
    }

    /** Key listener for grid */
    private class FilePathGridKeyListener implements OnKeyListener {
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
                        if (getSelectedPosition() + 1 > filePathNumberOfItems - 1) {
                            return true;
                        }
                        if (checkFocusNextFilePath()) {
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
                        if (checkFocusPreviousFilePath()) {
                            gridViewScroller.scrollLeft();
                            return true;
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_0: {
                        gridViewScroller.pageNext();
                        return false;
                    }
                    case KeyEvent.KEYCODE_1: {
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
    private class FilePathGridOnItemClick implements OnItemClickListener,
            OSDGlobal {
        // TODO: Applies only on main channel
        int mDisplayId = 0;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Get clicked content object
            MultimediaContent contentClicked = (MultimediaContent) filePathCurrentItems[arg2];
            if (contentClicked != null) {
                contentHelper.goContent(contentClicked, true, mDisplayId);
            }
        }
    }

    /** Set current screen */
    public void setCurrentPage(int indexOfCurrentPage) {
        gridViewScroller.setIndexOfCurrentScreen(0);
    }

    // /////////////////////////////////////////
    // Getters and setters
    // /////////////////////////////////////////
    public GridView getGridFilePath() {
        return gridFilePath;
    }

    public FilePathGridAdapter getFilePathGridAdapter() {
        return filePathGridAdapter;
    }
}
