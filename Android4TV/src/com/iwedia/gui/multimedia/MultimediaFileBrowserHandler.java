package com.iwedia.gui.multimedia;

import android.app.Activity;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.types.TimeDate;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.content_list.ContentListHandler;
import com.iwedia.gui.content_list.GridViewScroller;
import com.iwedia.gui.util.DateTimeConversions;

/**
 * Handler for drawing All content in content list
 * 
 * @author Veljko Ilkic
 */
public class MultimediaFileBrowserHandler {
    public static final String TAG = "MultimediaFileBrowserHandler";
    /** Reference of main activity */
    private Activity activity;
    /** Drawing handler object */
    private MultimediaGridHelper contentHelper;
    /** Number of visible items per screen */
    public static final int FILE_BROWSER_ITEMS_PER_SCREEN_FIRST_SCREEN = 12;
    public static final int FILE_BROWSER_ITEMS_PER_SCREEN_SECOND_SCREEN = 18;
    /** Number of items on screen */
    private int numberOfItemsOnScreen = FILE_BROWSER_ITEMS_PER_SCREEN_FIRST_SCREEN;
    /** Used for index conversion */
    public static final int[] GRID_VIEW_INDEX_CONVERTOR_FIRST = { 0, 2, 4, 6,
            8, 10, 1, 3, 5, 7, 9, 11 };
    public static final int[] GRID_VIEW_INDEX_CONVERTOR_SECOND = { 0, 3, 6, 9,
            12, 15, 1, 4, 7, 10, 13, 16, 2, 5, 8, 11, 14, 17 };
    /** Used for index conversion */
    public static final int[] GRID_VIEW_INDEX_CONVERTOR_FIRST_BACKWARD = { 0,
            6, 1, 7, 2, 8, 3, 9, 4, 10, 5, 11 };
    /** Used for index conversion */
    public static final int[] GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD = { 0,
            6, 12, 1, 7, 13, 2, 8, 14, 3, 9, 15, 4, 10, 16, 5, 11, 17 };
    /** Grid view container */
    private GridView gridFileBrowser;
    /** Adapter for grid view */
    private FileBrowserGridAdapter fileBrowserGridAdapter;
    /** Number of all items */
    public static int fileBrowserNumberOfItems;
    /** Current visible items */
    public static Content[] fileBrowserCurrentItemsFirstScreen;
    public static Content[] fileBrowserCurrentItemsSecondScreen;
    public static Content[] fileBrowserCurrentItemsPvrScreen;
    /** Flag for selecting first,second or pvr multimedia screen */
    private int screenId = MultimediaHandler.MULTIMEDIA_FIRST_SCREEN;
    /** Index of clicked item */
    private int indexOfClickedItem;
    /** GridView scroller */
    private GridViewScroller gridViewScroller;
    /** Cool down period counters for right and left keys */
    private long rightKeyCoolDownPeriod = 0;
    private long leftKeyCoolDownPeriod = 0;

    /** Constructor 1 */
    public MultimediaFileBrowserHandler(Activity activity, GridView gridView,
            int screenId) {
        super();
        // Take reference of activity
        this.activity = activity;
        // Take reference of grid view
        this.gridFileBrowser = gridView;
        // Screen id
        this.screenId = screenId;
        // Set up data for particullaty screen
        if (this.screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            numberOfItemsOnScreen = FILE_BROWSER_ITEMS_PER_SCREEN_FIRST_SCREEN;
            fileBrowserCurrentItemsFirstScreen = new MultimediaContent[numberOfItemsOnScreen];
        }
        if (this.screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
            numberOfItemsOnScreen = FILE_BROWSER_ITEMS_PER_SCREEN_SECOND_SCREEN;
            fileBrowserCurrentItemsSecondScreen = new MultimediaContent[numberOfItemsOnScreen];
        }
        if (this.screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
            numberOfItemsOnScreen = FILE_BROWSER_ITEMS_PER_SCREEN_SECOND_SCREEN;
            fileBrowserCurrentItemsPvrScreen = new MultimediaContent[numberOfItemsOnScreen];
        }
    }

    /** Init function */
    public void initView() {
        // Create grid helper
        contentHelper = new MultimediaGridHelper(activity);
        // Set number of columns
        gridFileBrowser.setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
        // Set up stretch mode for grid
        gridFileBrowser.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        // Attach adapter
        fileBrowserGridAdapter = new FileBrowserGridAdapter();
        gridFileBrowser.setAdapter(fileBrowserGridAdapter);
        // Set up listeners
        gridFileBrowser.setOnKeyListener(new FileBrowserGridKeyListener());
        gridFileBrowser
                .setOnItemClickListener(new FileBrowserGridOnItemClick());
        // //////////////////////////////////////
        // Mutlimedia FIRST screen
        // //////////////////////////////////////
        if (this.screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            gridViewScroller = new GridViewScroller(
                    GridViewScroller.MULTIMEDIA_FIRST_FILE_BROWSER, 2, 12,
                    fileBrowserGridAdapter, gridFileBrowser,
                    fileBrowserCurrentItemsFirstScreen);
        }
        // /////////////////////////////////////////
        // Multimedia SECOND screen
        // /////////////////////////////////////////
        if (this.screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
            gridViewScroller = new GridViewScroller(
                    GridViewScroller.MULTIMEDIA_SECOND_FILE_BROWSER, 3, 18,
                    fileBrowserGridAdapter, gridFileBrowser,
                    fileBrowserCurrentItemsSecondScreen);
        }
        // ////////////////////////////////////////////
        // Multimedia THIRD screen
        // ////////////////////////////////////////////
        if (this.screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
            gridViewScroller = new GridViewScroller(
                    GridViewScroller.MULTIMEDIA_PVR_FILE_BROWSER, 3, 18,
                    fileBrowserGridAdapter, gridFileBrowser,
                    fileBrowserCurrentItemsPvrScreen);
            // Attach OnItemSelected listener
            gridFileBrowser.setOnItemSelectedListener(new PvrOnItemSelected());
        }
    }

    /** Init data */
    public void initData() {
        // Init grid view scroller
        gridViewScroller.initData(fileBrowserNumberOfItems);
        // Hide arrows from all list if there isn't enough content items
        if (fileBrowserNumberOfItems < numberOfItemsOnScreen) {
            if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
                ((MainActivity) activity).getMultimediaHandler()
                        .hideFirstScreenAllArrows();
            }
            if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                ((MainActivity) activity).getMultimediaHandler()
                        .hideSecondScreenAllArrows();
            }
            if (screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                ((MainActivity) activity).getMultimediaHandler()
                        .hidePvrScreenAllArrows();
            }
        }
        // Show arrows
        else {
            if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
                ((MainActivity) activity).getMultimediaHandler()
                        .showFirstScreenAllArrows();
            }
            if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                ((MainActivity) activity).getMultimediaHandler()
                        .showSecondScreenAllArrows();
            }
            if (screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                ((MainActivity) activity).getMultimediaHandler()
                        .showPvrScreenAllArrows();
            }
        }
    }

    /**
     * Focus active element on beginning
     * 
     * @param index
     *        index of content in imaginary local list
     */
    public void focusActiveElement(final int index) {
        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Request focus needed
                gridFileBrowser.requestFocusFromTouch();
                // Set selection
                gridFileBrowser.setSelection(index);
                // ////////////////////////////////
                // Display description manually
                // ////////////////////////////////
                Content selectedContent = fileBrowserCurrentItemsPvrScreen[index];
                // Display description
                if (selectedContent != null
                        && MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                    displayFileDescription(selectedContent);
                }
                // Remove strings if there is no elements
                else {
                    ((MainActivity) activity).getMultimediaHandler()
                            .getPvrFileInfo1().setText("");
                    ((MainActivity) activity).getMultimediaHandler()
                            .getPvrFileInfo2().setText("");
                    ((MainActivity) activity).getMultimediaHandler()
                            .getPvrFileInfo3().setText("");
                    ((MainActivity) activity).getMultimediaHandler()
                            .getPvrFileInfo4().setText("");
                }
            }
        }, 50);
    }

    /** Check if last two elements has focus */
    private boolean checkFocusNextFileBrowser() {
        // ///////////////////////////////
        // First screen
        // ///////////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            // If selected position is last in first row
            if (gridFileBrowser.getSelectedItemPosition() == numberOfItemsOnScreen / 2 - 1) {
                return true;
            }
            // If selected position is last in second row
            if (gridFileBrowser.getSelectedItemPosition() == numberOfItemsOnScreen - 1) {
                return true;
            }
        }
        // //////////////////////////////////
        // Second screen or PVR screen
        // //////////////////////////////////
        else {
            // In first row
            if (gridFileBrowser.getSelectedItemPosition() == numberOfItemsOnScreen / 3 - 1) {
                return true;
            }
            // In second row
            if (gridFileBrowser.getSelectedItemPosition() == 2 * numberOfItemsOnScreen / 3 - 1) {
                return true;
            }
            // If selected position is last in third row
            if (gridFileBrowser.getSelectedItemPosition() == numberOfItemsOnScreen - 1) {
                return true;
            }
        }
        return false;
    }

    /** Check if last two elements has focus */
    private boolean checkFocusPreviousFileBrowser() {
        // ///////////////////////////////
        // First screen
        // ///////////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            // If selected position is first in first row
            if (gridFileBrowser.getSelectedItemPosition() == 0) {
                return true;
            }
            // If selected position is first in second row
            if (gridFileBrowser.getSelectedItemPosition() == numberOfItemsOnScreen / 2) {
                return true;
            }
        }
        // ///////////////////////////////////
        // Second screen and PVR screen
        // ///////////////////////////////////
        else {
            // If selected position is first in first row
            if (gridFileBrowser.getSelectedItemPosition() == 0) {
                return true;
            }
            // If selected position is first in second row
            if (gridFileBrowser.getSelectedItemPosition() == numberOfItemsOnScreen / 3) {
                return true;
            }
            // If selected position is first in third row
            if (gridFileBrowser.getSelectedItemPosition() == 2 * numberOfItemsOnScreen / 3) {
                return true;
            }
        }
        return false;
    }

    /** Grid view adapter */
    public class FileBrowserGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            switch (screenId) {
                case MultimediaHandler.MULTIMEDIA_FIRST_SCREEN: {
                    return fileBrowserCurrentItemsFirstScreen.length;
                }
                case MultimediaHandler.MULTIMEDIA_SECOND_SCREEN: {
                    return fileBrowserCurrentItemsSecondScreen.length;
                }
                case MultimediaHandler.MULTIMEDIA_PVR_SCREEN: {
                    return fileBrowserCurrentItemsPvrScreen.length;
                }
                default:
                    return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            switch (screenId) {
                case MultimediaHandler.MULTIMEDIA_FIRST_SCREEN: {
                    return fileBrowserCurrentItemsFirstScreen[position];
                }
                case MultimediaHandler.MULTIMEDIA_SECOND_SCREEN: {
                    return fileBrowserCurrentItemsSecondScreen[position];
                }
                case MultimediaHandler.MULTIMEDIA_PVR_SCREEN: {
                    return fileBrowserCurrentItemsPvrScreen[position];
                }
                default:
                    return 0;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retVal = null;
            switch (screenId) {
                case MultimediaHandler.MULTIMEDIA_FIRST_SCREEN: {
                    retVal = contentHelper
                            .prepareDataForAdapter((MultimediaContent) fileBrowserCurrentItemsFirstScreen[position]);
                    // If first item is visible hide left arrow
                    if (gridViewScroller.getIndexOfFirstElement() == 0) {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getFirstScreenAllLeftArrow()
                                .setVisibility(View.INVISIBLE);
                    }
                    // First element isn't visible show left arrow
                    else {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getFirstScreenAllLeftArrow()
                                .setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case MultimediaHandler.MULTIMEDIA_SECOND_SCREEN: {
                    retVal = contentHelper
                            .prepareDataForAdapter((MultimediaContent) fileBrowserCurrentItemsSecondScreen[position]);
                    // If first item is visible hide left arrow
                    if (gridViewScroller.getIndexOfFirstElement() == 0) {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getSecondScreenAllLeftArrow()
                                .setVisibility(View.INVISIBLE);
                    }
                    // First element isn't visible show left arrow
                    else {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getSecondScreenAllLeftArrow()
                                .setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case MultimediaHandler.MULTIMEDIA_PVR_SCREEN: {
                    retVal = contentHelper
                            .prepareDataForAdapter((MultimediaContent) fileBrowserCurrentItemsPvrScreen[position]);
                    // If first item is visible hide left arrow
                    if (gridViewScroller.getIndexOfFirstElement() == 0) {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getPvrScreenAllLeftArrow()
                                .setVisibility(View.INVISIBLE);
                    }
                    // First element isn't visible show left arrow
                    else {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getPvrScreenAllLeftArrow()
                                .setVisibility(View.VISIBLE);
                    }
                    break;
                }
                default:
                    break;
            }
            // ///////////////////////////
            // First screen
            // ///////////////////////////
            if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
                // Check if last element is visible
                if (gridViewScroller.getIndexOfLastElement() == fileBrowserNumberOfItems - 1) {
                    // Hide dividers in last row
                    if (position == numberOfItemsOnScreen - 1
                            || position == numberOfItemsOnScreen / 2 - 1) {
                        // Hide divider of right
                        if (retVal != null) {
                            ImageView dividerSmall = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                            dividerSmall.setImageBitmap(null);
                            ImageView dividerBig = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                            dividerBig.setImageBitmap(null);
                        }
                    }
                    // Check if there is less contents then 12
                    if (fileBrowserNumberOfItems < numberOfItemsOnScreen
                            && fileBrowserNumberOfItems > 0) {
                        if (fileBrowserNumberOfItems % 2 == 0) {
                            if (position == GRID_VIEW_INDEX_CONVERTOR_FIRST_BACKWARD[fileBrowserNumberOfItems - 1]
                                    || position == GRID_VIEW_INDEX_CONVERTOR_FIRST_BACKWARD[fileBrowserNumberOfItems - 2]) {
                                // Hide divider of right
                                if (retVal != null) {
                                    ImageView dividerSmall = (ImageView) retVal
                                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                                    dividerSmall.setImageBitmap(null);
                                    ImageView dividerBig = (ImageView) retVal
                                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                                    dividerBig.setImageBitmap(null);
                                }
                            }
                        } else {
                            if (position == GRID_VIEW_INDEX_CONVERTOR_FIRST_BACKWARD[fileBrowserNumberOfItems - 1]) {
                                // Hide divider of right
                                if (retVal != null) {
                                    ImageView dividerSmall = (ImageView) retVal
                                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                                    dividerSmall.setImageBitmap(null);
                                    ImageView dividerBig = (ImageView) retVal
                                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                                    dividerBig.setImageBitmap(null);
                                }
                            }
                        }
                    }
                    ((MainActivity) activity).getMultimediaHandler()
                            .getFirstScreenAllRightArrow()
                            .setVisibility(View.INVISIBLE);
                }
                // Last element isn't visible show right arrow
                else {
                    ((MainActivity) activity).getMultimediaHandler()
                            .getFirstScreenAllRightArrow()
                            .setVisibility(View.VISIBLE);
                }
            }
            // ///////////////////////////
            // Second screen or PVR screen
            // ///////////////////////////
            else {
                // Check if last element is visible
                if (gridViewScroller.getIndexOfLastElement() == fileBrowserNumberOfItems - 1) {
                    // Hide dividers in last row
                    if (position == numberOfItemsOnScreen - 1
                            || position == numberOfItemsOnScreen / 3 - 1
                            || position == 2 * numberOfItemsOnScreen / 3 - 1) {
                        // Hide divider of right
                        if (retVal != null) {
                            ImageView dividerSmall = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                            dividerSmall.setImageBitmap(null);
                            ImageView dividerBig = (ImageView) retVal
                                    .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                            dividerBig.setImageBitmap(null);
                        }
                    }
                    // Check if there is less contents then 18
                    if (fileBrowserNumberOfItems < numberOfItemsOnScreen
                            && fileBrowserNumberOfItems > 0) {
                        if (fileBrowserNumberOfItems % 3 == 0) {
                            if (position == GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 1]
                                    || position == GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 2]
                                    || position == GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 3]) {
                                // Hide divider of right
                                if (retVal != null) {
                                    ImageView dividerSmall = (ImageView) retVal
                                            .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                                    dividerSmall.setImageBitmap(null);
                                    ImageView dividerBig = (ImageView) retVal
                                            .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                                    dividerBig.setImageBitmap(null);
                                }
                            }
                        } else {
                            if (fileBrowserNumberOfItems % 3 == 2) {
                                if (position == GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 1]
                                        || position == GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 2]) {
                                    // Hide divider of right
                                    if (retVal != null) {
                                        ImageView dividerSmall = (ImageView) retVal
                                                .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                                        dividerSmall.setImageBitmap(null);
                                        ImageView dividerBig = (ImageView) retVal
                                                .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                                        dividerBig.setImageBitmap(null);
                                    }
                                }
                            } else {
                                if (position == GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 1]) {
                                    // Hide divider of right
                                    if (retVal != null) {
                                        ImageView dividerSmall = (ImageView) retVal
                                                .findViewById(com.iwedia.gui.R.id.contentDividerSmall);
                                        dividerSmall.setImageBitmap(null);
                                        ImageView dividerBig = (ImageView) retVal
                                                .findViewById(com.iwedia.gui.R.id.contentDividerBig);
                                        dividerBig.setImageBitmap(null);
                                    }
                                }
                            }
                        }
                    }
                    // Hide navigation arrows from second screen
                    if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getSecondScreenAllRightArrow()
                                .setVisibility(View.INVISIBLE);
                    }
                    // Hide navigation arrows from pvr screen
                    if (screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                        ((MainActivity) activity).getMultimediaHandler()
                                .getPvrScreenAllRightArrow()
                                .setVisibility(View.INVISIBLE);
                    }
                }
            }
            return retVal;
        }
    }

    /** Get selected real selected position */
    private int getSelectedPosition() {
        // ///////////////////////////
        // First screen
        // ///////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            try {
                return gridViewScroller.getIndexOfFirstElement()
                        + GRID_VIEW_INDEX_CONVERTOR_FIRST[gridFileBrowser
                                .getSelectedItemPosition()];
            } catch (Exception e) {
                return 0;
            }
        }
        // ///////////////////////////
        // Second screen or PVR screen
        // ///////////////////////////
        else {
            try {
                return gridViewScroller.getIndexOfFirstElement()
                        + GRID_VIEW_INDEX_CONVERTOR_SECOND[gridFileBrowser
                                .getSelectedItemPosition()];
            } catch (Exception e) {
                return 0;
            }
        }
    }

    /** Select upper grid */
    private void selectGridUp() {
        // //////////////////////////
        // First screen
        // //////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            // ////////////////////////
            // Favorite available
            // /////////////////////////
            if (((MainActivity) activity).getMultimediaHandler()
                    .getMutlimediaFavoriteHandler().getMultimediaGridFavorite()
                    .isFocusable()) {
                ((MainActivity) activity).getMultimediaHandler()
                        .getMutlimediaFavoriteHandler().focusActiveElement(0);
            } else {
                // //////////////////////////
                // Recently available
                // //////////////////////////
                if (((MainActivity) activity).getMultimediaHandler()
                        .getMultimediaRecentlyHandler()
                        .getMultimediaGridRecently().isFocusable()) {
                    ((MainActivity) activity).getMultimediaHandler()
                            .getMultimediaRecentlyHandler()
                            .focusActiveElement(0);
                }
            }
        }
        // //////////////////////////////////
        // Second screen
        // //////////////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
            // ////////////////////////////
            // File path available
            // ////////////////////////////
            if (((MainActivity) activity).getMultimediaHandler()
                    .getFilePathHandler().getGridFilePath().isFocusable()) {
                ((MainActivity) activity).getMultimediaHandler()
                        .getFilePathHandler().focusActiveElement(0);
            }
        }
    }

    /** Key listener for grid */
    private class FileBrowserGridKeyListener implements OnKeyListener {
        private IContentListControl contentListControl;

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d(TAG, "keyCode: " + keyCode);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        // ///////////////////////////////
                        // First screen
                        // ///////////////////////////////
                        if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
                            if (gridFileBrowser.getSelectedItemPosition() < numberOfItemsOnScreen / 2) {
                                selectGridUp();
                                return true;
                            } else {
                                return false;
                            }
                        }
                        // ////////////////////////////////
                        // Second screen
                        // ////////////////////////////////
                        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                            if (gridFileBrowser.getSelectedItemPosition() < numberOfItemsOnScreen / 3) {
                                selectGridUp();
                                return true;
                            } else {
                                return false;
                            }
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        if (gridFileBrowser.getSelectedItemPosition() != -1) {
                            // Check if focus can move down
                            if (getSelectedPosition() + 1 > fileBrowserNumberOfItems - 1) {
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
                            // long systemmsTime = System.currentTimeMillis();
                            long systemmsTime = System.nanoTime() / 1000000;
                            if (rightKeyCoolDownPeriod != 0
                                    && (systemmsTime
                                            + 2
                                            * MainActivity.KEY_HANDLING_COOL_DOWN_PERIOD > rightKeyCoolDownPeriod)) {
                                // Check when last time key event was here and
                                // is
                                // the diff > 0
                                if ((systemmsTime - rightKeyCoolDownPeriod) < MainActivity.KEY_HANDLING_COOL_DOWN_PERIOD
                                        && (systemmsTime - rightKeyCoolDownPeriod) > 0) {
                                    return true;
                                }
                            }
                            if (systemmsTime - rightKeyCoolDownPeriod < 0) {
                                Log.e(TAG, "TIME GLITCH!");
                                Log.e(TAG, "System.currentTimeMillis = "
                                        + systemmsTime);
                                Log.e(TAG, "rightKeyCoolDownPeriod= "
                                        + rightKeyCoolDownPeriod);
                            }
                            rightKeyCoolDownPeriod = systemmsTime;
                        }
                        // ///////////////////////////////////
                        // First screen
                        // ///////////////////////////////////
                        if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
                            // //////////////////////////////////
                            // Odd number of content items
                            // /////////////////////////////////
                            if (fileBrowserNumberOfItems % 2 != 0) {
                                // //////////////////////////////
                                // First row
                                // //////////////////////////////
                                if (getSelectedPosition() % 2 == 0) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 2 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                                // //////////////////////////////
                                // Second row
                                // //////////////////////////////
                                else {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 1 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                    if (getSelectedPosition() + 1 == fileBrowserNumberOfItems - 1) {
                                        int positionToSelect;
                                        if (fileBrowserNumberOfItems < numberOfItemsOnScreen) {
                                            positionToSelect = GRID_VIEW_INDEX_CONVERTOR_FIRST_BACKWARD[fileBrowserNumberOfItems - 1];
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                            return true;
                                        } else {
                                            positionToSelect = numberOfItemsOnScreen / 2 - 1;
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                        }
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
                                    if (getSelectedPosition() + 3 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                                // //////////////////////////////
                                // Second row
                                // //////////////////////////////
                                else {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 2 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                            }
                        }
                        // ////////////////////////////////////
                        // Second screen or PVR screen
                        // ////////////////////////////////////
                        else {
                            // ////////////////////////////////////
                            // Divisible with 3
                            // ////////////////////////////////////
                            if (fileBrowserNumberOfItems % 3 == 0) {
                                // //////////////////////////////
                                // First row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 0) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 5 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                                // //////////////////////////////
                                // Second row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 1) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 4 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                                // //////////////////////////////
                                // Third row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 2) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 3 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                            }
                            // ////////////////////////////////////////
                            // Rest 2
                            // ///////////////////////////////////////
                            if (fileBrowserNumberOfItems % 3 == 2) {
                                // //////////////////////////////
                                // First row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 0) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 4 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                                // //////////////////////////////
                                // Second row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 1) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 3 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                                // //////////////////////////////
                                // Third row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 2) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 2 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                    if (getSelectedPosition() + 2 == fileBrowserNumberOfItems - 1) {
                                        int positionToSelect;
                                        // Check if it's not full screen of
                                        // items
                                        if (fileBrowserNumberOfItems < numberOfItemsOnScreen) {
                                            positionToSelect = GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 1];
                                            // Select last element
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .requestChildFocus(
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect),
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect));
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                            return true;
                                        } else {
                                            positionToSelect = 2 * (numberOfItemsOnScreen / 3) - 1;
                                            // Select last element
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .requestChildFocus(
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect),
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect));
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                        }
                                    }
                                }
                            }
                            // /////////////////////////////////////
                            // Rest 1
                            // /////////////////////////////////////
                            if (fileBrowserNumberOfItems % 3 == 1) {
                                // //////////////////////////////
                                // First row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 0) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 3 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                }
                                // //////////////////////////////
                                // Second row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 1) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 2 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                    if (getSelectedPosition() + 2 == fileBrowserNumberOfItems - 1) {
                                        int positionToSelect;
                                        // Check if it's not full screen of
                                        // items
                                        if (fileBrowserNumberOfItems < numberOfItemsOnScreen) {
                                            positionToSelect = GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 1];
                                            // Select last element
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .requestChildFocus(
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect),
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect));
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                            return true;
                                        } else {
                                            positionToSelect = numberOfItemsOnScreen / 3 - 1;
                                            // Select last element
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .requestChildFocus(
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect),
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect));
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                        }
                                    }
                                }
                                // //////////////////////////////
                                // Third row
                                // //////////////////////////////
                                if (getSelectedPosition() % 3 == 2) {
                                    // Check if focus can move
                                    if (getSelectedPosition() + 1 > fileBrowserNumberOfItems - 1) {
                                        return true;
                                    }
                                    if (getSelectedPosition() + 1 == fileBrowserNumberOfItems - 1) {
                                        int positionToSelect;
                                        // Check if it's not full screen of
                                        // items
                                        if (fileBrowserNumberOfItems < numberOfItemsOnScreen) {
                                            positionToSelect = GRID_VIEW_INDEX_CONVERTOR_SECOND_BACKWARD[fileBrowserNumberOfItems - 1];
                                            // Select last element
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .requestChildFocus(
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect),
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect));
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                            return true;
                                        } else {
                                            positionToSelect = numberOfItemsOnScreen / 3 - 1;
                                            // Select last element
                                            gridFileBrowser
                                                    .requestFocusFromTouch();
                                            gridFileBrowser
                                                    .requestChildFocus(
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect),
                                                            gridFileBrowser
                                                                    .getChildAt(positionToSelect));
                                            gridFileBrowser
                                                    .setSelection(positionToSelect);
                                        }
                                    }
                                }
                            }
                        }
                        if (fileBrowserNumberOfItems > numberOfItemsOnScreen) {
                            if (checkFocusNextFileBrowser()) {
                                gridViewScroller.scrollRight();
                                return true;
                            }
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        // Check if cool down period is enabled
                        if (MainActivity.enableKeyHandlingCoolDownPerion) {
                            // Ignore cool down first time and key handling
                            // wasn't
                            // active for some time
                            // long systemmsTime = System.currentTimeMillis();
                            long systemmsTime = System.nanoTime() / 1000000;
                            if (leftKeyCoolDownPeriod != 0
                                    && (systemmsTime
                                            + 2
                                            * MainActivity.KEY_HANDLING_COOL_DOWN_PERIOD > leftKeyCoolDownPeriod)) {
                                // Check when last time key event was here and
                                // is
                                // the diff > 0
                                if ((systemmsTime - leftKeyCoolDownPeriod) < MainActivity.KEY_HANDLING_COOL_DOWN_PERIOD
                                        && (systemmsTime - leftKeyCoolDownPeriod) > 0) {
                                    return true;
                                }
                                if (systemmsTime - leftKeyCoolDownPeriod < 0) {
                                    Log.e(TAG, "TIME GLITCH!");
                                    Log.e(TAG, "System.currentTimeMillis = "
                                            + systemmsTime);
                                    Log.e(TAG, "leftKeyCoolDownPeriod= "
                                            + leftKeyCoolDownPeriod);
                                }
                            }
                            leftKeyCoolDownPeriod = systemmsTime;
                        }
                        if (checkFocusPreviousFileBrowser()) {
                            gridViewScroller.scrollLeft();
                            return true;
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_0: {
                        // pageNextFileBrowser();
                        gridViewScroller.pageNext();
                        return true;
                    }
                    case KeyEvent.KEYCODE_1: {
                        // pagePreviousFileBrowser();
                        gridViewScroller.pagePrevious();
                        return true;
                    }
                    case KeyEvent.KEYCODE_PROG_GREEN:
                    case KeyEvent.KEYCODE_G: {
                        Content contentSelected = null;
                        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                            contentSelected = fileBrowserCurrentItemsSecondScreen[gridFileBrowser
                                    .getSelectedItemPosition()];
                        }
                        // sort playlist files
                        if (contentSelected instanceof MultimediaContent) {
                            MultimediaContent mContent = (MultimediaContent) contentSelected;
                            if (mContent.getPlaylistID() != 0
                                    && mContent.getType().equals("file")) {
                                try {
                                    MainActivity.service
                                            .getContentListControl()
                                            .getContentFilter(
                                                    FilterType.MULTIMEDIA)
                                            .sortPlaylist(
                                                    mContent.getPlaylistName(),
                                                    "title");
                                    MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                            .getContentListControl()
                                            .getContentListSize();
                                    // ////////////////////////////////////////
                                    // Prepare data for focusing
                                    // ////////////////////////////////////////
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .setCurrentPage(0);
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .initData();
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .focusActiveElement(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        }
                        // sort pvr files
                        int sortMode = 0;
                        int sortOrder;
                        Log.d(TAG, "initial sort mode: " + sortMode);
                        try {
                            sortMode = MainActivity.service.getPvrControl()
                                    .getMediaListSortMode().getValue();
                            sortMode++;
                            if (sortMode > 2) {
                                sortMode = 0;
                            }
                            MainActivity.service.getPvrControl()
                                    .setMediaListSortMode(
                                            PvrSortMode.getFromValue(sortMode));
                            sortMode = MainActivity.service.getPvrControl()
                                    .getMediaListSortMode().getValue();
                            sortOrder = MainActivity.service.getPvrControl()
                                    .getMediaListSortOrder().getValue();
                            MultimediaHandler.pvrFileBrowserText
                                    .setText("PVR playlist sorted by "
                                            + MultimediaHandler.sortPvrFilesBy[sortMode]
                                            + " in "
                                            + MultimediaHandler.sortPvrFilesByOrder[sortOrder]
                                            + " order");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        MultimediaContent contentClicked = new MultimediaContent(
                                "PVR", "", "", "root", "PVR", -1, "PVR"
                                        + sortMode, "", "", "", 0);
                        try {
                            MainActivity.service.getContentListControl()
                                    .goContent(contentClicked, 0);
                            MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                    .getContentListControl()
                                    .getContentListSize();
                            // ////////////////////////////////////////
                            // Prepare data for focusing
                            // ////////////////////////////////////////
                            ((MainActivity) activity).getMultimediaHandler()
                                    .getMultimediaFileBrowserPvrHandler()
                                    .setCurrentPage(0);
                            ((MainActivity) activity).getMultimediaHandler()
                                    .getMultimediaFileBrowserPvrHandler()
                                    .initData();
                            ((MainActivity) activity).getMultimediaHandler()
                                    .getMultimediaFileBrowserPvrHandler()
                                    .focusActiveElement(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    case KeyEvent.KEYCODE_PROG_RED:
                    case KeyEvent.KEYCODE_R: {
                        Content contentSelected = null;
                        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                            contentSelected = fileBrowserCurrentItemsSecondScreen[gridFileBrowser
                                    .getSelectedItemPosition()];
                        }
                        // sort playlist files
                        if (contentSelected instanceof MultimediaContent) {
                            MultimediaContent mContent = (MultimediaContent) contentSelected;
                            if (mContent.getPlaylistID() != 0
                                    && mContent.getType().equals("file")) {
                                try {
                                    MainActivity.service
                                            .getContentListControl()
                                            .getContentFilter(
                                                    FilterType.MULTIMEDIA)
                                            .sortPlaylist(
                                                    mContent.getPlaylistName(),
                                                    "artist");
                                    MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                            .getContentListControl()
                                            .getContentListSize();
                                    // ////////////////////////////////////////
                                    // Prepare data for focusing
                                    // ////////////////////////////////////////
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .setCurrentPage(0);
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .initData();
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .focusActiveElement(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        }
                        int sortMode = 0;
                        int sortOrder;
                        try {
                            sortOrder = MainActivity.service.getPvrControl()
                                    .getMediaListSortOrder().getValue();
                            sortOrder++;
                            if (sortOrder > 1) {
                                sortOrder = 0;
                            }
                            MainActivity.service.getPvrControl()
                                    .setMediaListSortOrder(
                                            PvrSortOrder
                                                    .getFromValue(sortOrder));
                            sortMode = MainActivity.service.getPvrControl()
                                    .getMediaListSortMode().getValue();
                            sortOrder = MainActivity.service.getPvrControl()
                                    .getMediaListSortOrder().getValue();
                            MultimediaHandler.pvrFileBrowserText
                                    .setText("PVR playlist sorted by "
                                            + MultimediaHandler.sortPvrFilesBy[sortMode]
                                            + " in "
                                            + MultimediaHandler.sortPvrFilesByOrder[sortOrder]
                                            + " order");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        MultimediaContent contentClicked = new MultimediaContent(
                                "PVR", "", "", "root", "PVR", -1, "PVR"
                                        + sortMode, "", "", "", 0);
                        try {
                            MainActivity.service.getContentListControl()
                                    .goContent(contentClicked, 0);
                            MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                    .getContentListControl()
                                    .getContentListSize();
                            // ////////////////////////////////////////
                            // Prepare data for focusing
                            // ////////////////////////////////////////
                            ((MainActivity) activity).getMultimediaHandler()
                                    .getMultimediaFileBrowserPvrHandler()
                                    .setCurrentPage(0);
                            ((MainActivity) activity).getMultimediaHandler()
                                    .getMultimediaFileBrowserPvrHandler()
                                    .initData();
                            ((MainActivity) activity).getMultimediaHandler()
                                    .getMultimediaFileBrowserPvrHandler()
                                    .focusActiveElement(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    /** YELLOW KEY */
                    case KeyEvent.KEYCODE_PROG_YELLOW:
                    case KeyEvent.KEYCODE_Y: {
                        Content contentSelected = null;
                        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                            contentSelected = fileBrowserCurrentItemsSecondScreen[gridFileBrowser
                                    .getSelectedItemPosition()];
                        }
                        // sort playlist files
                        if (contentSelected instanceof MultimediaContent) {
                            MultimediaContent mContent = (MultimediaContent) contentSelected;
                            if (mContent.getPlaylistID() != 0
                                    && mContent.getType().equals("file")) {
                                Log.d(TAG, "KEYCODE_Y");
                                try {
                                    MainActivity.service
                                            .getContentListControl()
                                            .getContentFilter(
                                                    FilterType.MULTIMEDIA)
                                            .sortPlaylist(
                                                    mContent.getPlaylistName(),
                                                    "duration");
                                    MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                            .getContentListControl()
                                            .getContentListSize();
                                    // ////////////////////////////////////////
                                    // Prepare data for focusing
                                    // ////////////////////////////////////////
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .setCurrentPage(0);
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .initData();
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getMultimediaFileBrowserSecondHandler()
                                            .focusActiveElement(0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        }
                        Log.d(TAG, "Play multimedia PIP");
                        Content contentClicked = null;
                        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                            contentClicked = fileBrowserCurrentItemsSecondScreen[gridFileBrowser
                                    .getSelectedItemPosition()];
                        } else if (screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                            contentClicked = fileBrowserCurrentItemsPvrScreen[gridFileBrowser
                                    .getSelectedItemPosition()];
                        }
                        if (MainActivity.activity.getDualVideoManager()
                                .playPiP(contentClicked)) {
                            ((MainActivity) activity).getMultimediaHandler()
                                    .closeMultimedia();
                            return true;
                        }
                        return false;
                    }
                    case KeyEvent.KEYCODE_PROG_BLUE:
                    case KeyEvent.KEYCODE_B: {
                        Log.d(TAG, "Play multimedia PAP");
                        Content contentClicked = null;
                        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                            contentClicked = fileBrowserCurrentItemsSecondScreen[gridFileBrowser
                                    .getSelectedItemPosition()];
                        } else if (screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                            contentClicked = fileBrowserCurrentItemsPvrScreen[gridFileBrowser
                                    .getSelectedItemPosition()];
                        }
                        if (MainActivity.activity.getDualVideoManager()
                                .playPaP(contentClicked)) {
                            ((MainActivity) activity).getMultimediaHandler()
                                    .closeMultimedia();
                            return true;
                        }
                        return false;
                    }
                }// switch
                return false;
            }
            // On action up
            return false;
        }
    }

    /** OnItemClick listener for grid */
    private class FileBrowserGridOnItemClick implements OnItemClickListener {
        // TODO: Applies only on main display
        int mDisplayId = 0;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            // Get clicked content object
            MultimediaContent contentClicked = null;
            switch (screenId) {
                case MultimediaHandler.MULTIMEDIA_FIRST_SCREEN: {
                    contentClicked = (MultimediaContent) fileBrowserCurrentItemsFirstScreen[arg2];
                    break;
                }
                case MultimediaHandler.MULTIMEDIA_SECOND_SCREEN: {
                    contentClicked = (MultimediaContent) fileBrowserCurrentItemsSecondScreen[arg2];
                    break;
                }
                case MultimediaHandler.MULTIMEDIA_PVR_SCREEN: {
                    contentClicked = (MultimediaContent) fileBrowserCurrentItemsPvrScreen[arg2];
                    int index = GRID_VIEW_INDEX_CONVERTOR_SECOND[arg2
                            % FILE_BROWSER_ITEMS_PER_SCREEN_SECOND_SCREEN];
                    contentClicked.setIndex(index);
                    break;
                }
            }
            if (contentClicked != null) {
                // Save index of clicked item in second screen
                if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN
                        || screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                    indexOfClickedItem = GRID_VIEW_INDEX_CONVERTOR_SECOND[arg2]
                            + gridViewScroller.getIndexOfFirstElement();
                }
                boolean isNavigationPathNeeded = contentHelper.goContent(
                        contentClicked, true, mDisplayId);
                if (isNavigationPathNeeded) {
                    // Store navigation path
                    MultimediaNavigationHandler
                            .addNavigationObject(new MultimediaNavigationObject(
                                    contentClicked.getName(), contentClicked
                                            .getIndex(), contentClicked
                                            .getIndex() / numberOfItemsOnScreen));
                }
            }
        }
    }

    /** On item selected listener for grid view in pvr screen */
    private class PvrOnItemSelected implements OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                long arg3) {
            Content selectedContent = fileBrowserCurrentItemsPvrScreen[arg2];
            if (selectedContent != null
                    && MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
                displayFileDescription(selectedContent);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    /** Display file description in description banner on PVR screen */
    private void displayFileDescription(Content selectedContent) {
        // //////////////////////////////
        // Recorded elements
        // //////////////////////////////
        if (MultimediaHandler.currentSelectedFilterOptionPvr == 0) {
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo1()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.title)
                                    + " " + selectedContent.getName());
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo2()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.duration)
                                    + " "
                                    + convertSeconds(Long
                                            .parseLong(((MultimediaContent) selectedContent)
                                                    .getDurationTime())));
            String date = DateTimeConversions
                    .getDateTimeSting(((MultimediaContent) selectedContent)
                            .getTimeDate().getCalendar().getTime());
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo3()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.date)
                                    + " " + date);
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo4()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.description)
                                    + " "
                                    + ((MultimediaContent) selectedContent)
                                            .getDescription());
        }
        // ////////////////////////////////////
        // Scheduled elements
        // ////////////////////////////////////
        else {
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo1()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.title)
                                    + " " + selectedContent.getName());
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo2()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.start_time)
                                    + " "
                                    + ((MultimediaContent) selectedContent)
                                            .getStartTime());
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo3()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.end_time)
                                    + " "
                                    + ((MultimediaContent) selectedContent)
                                            .getEndTime());
            ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getPvrFileInfo4()
                    .setText(
                            activity.getResources().getString(
                                    com.iwedia.gui.R.string.description)
                                    + " "
                                    + ((MultimediaContent) selectedContent)
                                            .getDescription());
        }
    }

    /** Convert seconds to time format */
    public String convertSeconds(long seconds) {
        String convert = String.format("%02dh: %02dm: %02ds", seconds
                / (60 * 60), (seconds % (60 * 60)) / (60), (seconds % 60));
        return convert;
    }

    /** Set current screen */
    public void setCurrentPage(int indexOfCurrentPage) {
        gridViewScroller.setIndexOfCurrentScreen(indexOfCurrentPage);
    }

    // ////////////////////////////////////
    // Getters and setters
    // ////////////////////////////////////
    public GridView getGridFileBrowser() {
        return gridFileBrowser;
    }

    public FileBrowserGridAdapter getFileBrowserGridAdapter() {
        return fileBrowserGridAdapter;
    }

    public static int getFileBrowserNumberOfItems() {
        return fileBrowserNumberOfItems;
    }

    public int getIndexOfClickedItem() {
        return indexOfClickedItem;
    }
}
