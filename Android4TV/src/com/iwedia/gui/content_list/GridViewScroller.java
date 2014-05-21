package com.iwedia.gui.content_list;

import android.os.RemoteException;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.multimedia.MultimediaFavoriteHandler;
import com.iwedia.gui.multimedia.MultimediaFileBrowserHandler;
import com.iwedia.gui.multimedia.MultimediaFilePathHandler;
import com.iwedia.gui.multimedia.MultimediaRecentlyHandler;

/**
 * GridView code wrapper
 * 
 * @author Veljko Ilkic
 */
public class GridViewScroller {
    /** GridView ids */
    // //////////////////////////////////////
    // Content list
    // //////////////////////////////////////
    public static final int CONTENT_LIST_RECENTLY = 1;
    public static final int CONTENT_LIST_FAVORITE = 2;
    public static final int CONTENT_LIST_ALL = 3;
    // ///////////////////////////////////////
    // Multimedia first screen
    // ///////////////////////////////////////
    public static final int MULTIMEDIA_FIRST_RECENTLY = 11;
    public static final int MULTIMEDIA_FIRST_FAVORITE = 12;
    public static final int MULTIMEDIA_FIRST_FILE_BROWSER = 13;
    // ///////////////////////////////////////
    // Multimedia second screen
    // ///////////////////////////////////////
    public static final int MULTIMEDIA_SECOND_FILE_PATH = 21;
    public static final int MULTIMEDIA_SECOND_FILE_BROWSER = 22;
    // ///////////////////////////////////////
    // Multimedia PVR screen
    // ///////////////////////////////////////
    public static final int MULTIMEDIA_PVR_FILE_BROWSER = 31;
    public static final int[] INDEX_LOOK_UP_TABLE_TWO_ROWS = { 0, 6, 1, 7, 2,
            8, 3, 9, 4, 10, 5, 11 };
    /** Grid view indexes look up table second */
    public static final int[] INDEX_LOOK_UP_TABLE_THREE_ROWS = { 0, 6, 12, 1,
            7, 13, 2, 8, 14, 3, 9, 15, 4, 10, 16, 5, 11, 17 };
    /** Grid view id */
    private int gridId = CONTENT_LIST_RECENTLY;
    /** Number of all items */
    private int numberOfItems;
    /** Number of rows in grid view */
    private int numberOfRows;
    /** Number of items per screen */
    private int itemsPerScreen;
    /** Current items on screen */
    private Content[] currentItemsOnScreen;
    /** Index of last element on screen */
    private int indexOfLastElement = 0;
    /** Index of first element on screen */
    private int indexOfFirstElement = 0;
    /** Index of current screen */
    private int indexOfCurrentScreen = 0;
    /** Number of screens */
    private int numberOfScreens;
    /** Number of fake items on screen */
    private int numberOfFakeItems;
    /** Adapter */
    private BaseAdapter adapter;
    /** Grid view */
    private GridView gridView;
    /** Content list control */
    private IContentListControl contentListControl;

    /** Constructor 1 */
    public GridViewScroller(int gridId, int numberOfRows, int itemsPerScreen,
            BaseAdapter adapter, GridView gridView, Content[] currentItems) {
        super();
        this.gridId = gridId;
        this.numberOfRows = numberOfRows;
        this.itemsPerScreen = itemsPerScreen;
        this.adapter = adapter;
        this.gridView = gridView;
        this.currentItemsOnScreen = currentItems;
    }

    /** Init data */
    public void initData(int numberOfItems) {
        this.numberOfItems = numberOfItems;
        calculateNumberOfScreens();
        fillPage();
    }

    /** Calculate number of screens */
    private void calculateNumberOfScreens() {
        if (numberOfItems % itemsPerScreen == 0) {
            numberOfScreens = numberOfItems / itemsPerScreen;
        } else {
            numberOfScreens = numberOfItems / itemsPerScreen + 1;
        }
    }

    public void fill_one_row() {
        // Init number of fake items
        numberOfFakeItems = 0;
        /** Start and end index */
        int startIndex;
        int endIndex;
        // Start index
        startIndex = indexOfCurrentScreen * itemsPerScreen;
        // Store index of fist element for later
        indexOfFirstElement = startIndex;
        int indexInCurrentList = 0;
        // End index
        if (startIndex + itemsPerScreen <= numberOfItems) {
            endIndex = startIndex + itemsPerScreen;
            // Store index of last element for later
            indexOfLastElement = endIndex - 1;
            // Add content in array window
            for (int i = startIndex; i < endIndex; i++) {
                try {
                    switch (gridId) {
                    // /////////////////////////////
                    // Content list RECENLTY
                    // /////////////////////////////
                        case CONTENT_LIST_RECENTLY: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getRecentlyWatchedItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Content list FAVORITE
                        // ////////////////////////////////
                        case CONTENT_LIST_FAVORITE: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getFavoriteItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Multimedia first screen RECENTLY
                        // ////////////////////////////////
                        case MULTIMEDIA_FIRST_RECENTLY: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getRecentlyWatchedItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Multimedia first screen FAVORITE
                        // ////////////////////////////////
                        case MULTIMEDIA_FIRST_FAVORITE: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getFavoriteItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Multimedia second screen FILE PATH
                        // ////////////////////////////////
                        case MULTIMEDIA_SECOND_FILE_PATH: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getPath(i);
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                indexInCurrentList++;
            }
        } else {
            endIndex = numberOfItems;
            // Store index of last element for later
            indexOfLastElement = endIndex - 1;
            // Add content in array window
            for (int i = startIndex; i < endIndex; i++) {
                try {
                    switch (gridId) {
                    // //////////////////////////////////
                    // Content list RECENLTY
                    // //////////////////////////////////
                        case CONTENT_LIST_RECENTLY: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getRecentlyWatchedItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Content list FAVORITE
                        // ////////////////////////////////
                        case CONTENT_LIST_FAVORITE: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getFavoriteItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Multimedia first screen RECENTLY
                        // ////////////////////////////////
                        case MULTIMEDIA_FIRST_RECENTLY: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getRecentlyWatchedItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Multimedia first screen FAVORITE
                        // ////////////////////////////////
                        case MULTIMEDIA_FIRST_FAVORITE: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getFavoriteItem(i);
                            break;
                        }
                        // ////////////////////////////////
                        // Multimedia second screen FILE PATH
                        // ////////////////////////////////
                        case MULTIMEDIA_SECOND_FILE_PATH: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getPath(i);
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                indexInCurrentList++;
            }
            if (numberOfItems != itemsPerScreen) {
                // Add fake items
                for (int i = endIndex % itemsPerScreen; i < itemsPerScreen; i++) {
                    currentItemsOnScreen[indexInCurrentList] = null;
                    indexInCurrentList++;
                    numberOfFakeItems++;
                }
            }
        }
        // If there is no elements add all fake items
        if (endIndex == 0) {
            for (int i = 0; i < itemsPerScreen; i++) {
                // Fake item
                currentItemsOnScreen[i] = null;
                numberOfFakeItems++;
            }
        }
    }

    public void fill_two_rows() {
        // Init number of fake items
        numberOfFakeItems = 0;
        /** Start and end index */
        int startIndex;
        int endIndex;
        // Start index
        startIndex = indexOfCurrentScreen * itemsPerScreen;
        // Store index of first element for later
        indexOfFirstElement = startIndex;
        int indexInCurrentList;
        // End index
        if (startIndex + itemsPerScreen <= numberOfItems) {
            endIndex = startIndex + itemsPerScreen;
            // Store index of last element for later
            indexOfLastElement = endIndex - 1;
            // Add content in array window
            int contentCount = 0;
            int indexInContentList = 0;
            while (contentCount < itemsPerScreen) {
                indexInCurrentList = INDEX_LOOK_UP_TABLE_TWO_ROWS[contentCount
                        % itemsPerScreen];
                Log.d("GridViewScroller", "indexInCurrentList = "
                        + indexInCurrentList);
                Log.d("GridViewScroller", "indexInContentList = "
                        + indexInContentList);
                Log.d("GridViewScroller", "contentCount = " + contentCount);
                try {
                    switch (gridId) {
                    // /////////////////////////////
                    // Content list ALL
                    // /////////////////////////////
                        case CONTENT_LIST_ALL: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContentVisible(indexInContentList
                                            + startIndex);
                            break;
                        }
                        // /////////////////////////////
                        // Multimedia first screen FILE BROWSER
                        // /////////////////////////////
                        case MULTIMEDIA_FIRST_FILE_BROWSER: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContent(indexInContentList);
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    // Add fake element
                    currentItemsOnScreen[indexInCurrentList] = null;
                    numberOfFakeItems++;
                }
                Log.d("GridViewScroller", "*********************increment!");
                contentCount++;
                indexInContentList++;
            }
        } else {
            endIndex = numberOfItems;
            // Store index of last element for later
            indexOfLastElement = endIndex - 1;
            // Counter of contents added in content list
            int contentCount = 0;
            // index of content in content filter (proxy)
            int indexInContentList = 0;
            // index of a content in grid (because order is not grid natural
            // these are calculated
            // with INDEX_LOOK_UP_TABLE_TWO_ROWS)
            // Add content in array window
            // for (int i = startIndex; i < endIndex; i++) {
            while (contentCount < numberOfItems
                    && indexInContentList < numberOfItems) {
                indexInCurrentList = INDEX_LOOK_UP_TABLE_TWO_ROWS[contentCount
                        % itemsPerScreen];
                Log.d("GridViewScroller", "indexInCurrentList = "
                        + indexInCurrentList);
                Log.d("GridViewScroller", "indexInContentList = "
                        + indexInContentList);
                Log.d("GridViewScroller", "contentCount = " + contentCount);
                try {
                    switch (gridId) {
                    // ////////////////////////////////
                    // Content list ALL
                    // ////////////////////////////////
                        case CONTENT_LIST_ALL: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContentVisible(indexInContentList
                                            + startIndex);
                            break;
                        }
                        // ////////////////////////////////
                        // Multimedia first screen FILE BROWSER
                        // ////////////////////////////////
                        case MULTIMEDIA_FIRST_FILE_BROWSER: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContent(indexInContentList);
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.d("GridViewScroller", "*********************increment!");
                contentCount++;
                indexInContentList++;
            }
            // Add fake items
            for (int i = endIndex % itemsPerScreen; i < itemsPerScreen; i++) {
                indexInCurrentList = INDEX_LOOK_UP_TABLE_TWO_ROWS[i
                        % itemsPerScreen];
                // Fake item
                currentItemsOnScreen[indexInCurrentList] = null;
                numberOfFakeItems++;
            }
            // If there is no elements add all fake items
            if (endIndex == 0) {
                for (int i = 0; i < itemsPerScreen; i++) {
                    // Fake item
                    currentItemsOnScreen[i] = null;
                    numberOfFakeItems++;
                }
            }
        }
    }

    public void fill_three_rows() {
        // Init number of fake items
        numberOfFakeItems = 0;
        /** Start and end index */
        int startIndex;
        int endIndex;
        // Start index
        startIndex = indexOfCurrentScreen * itemsPerScreen;
        // Store index of first element for later
        indexOfFirstElement = startIndex;
        int indexInCurrentList;
        // End index
        if (startIndex + itemsPerScreen <= numberOfItems) {
            endIndex = startIndex + itemsPerScreen;
            // Store index of last element for later
            indexOfLastElement = endIndex - 1;
            // Add content in array window
            for (int i = startIndex; i < endIndex; i++) {
                indexInCurrentList = INDEX_LOOK_UP_TABLE_THREE_ROWS[i
                        % itemsPerScreen];
                try {
                    switch (gridId) {
                    // ///////////////////////////////////////////
                    // Multimedia second screen FILE BROWSER
                    // ///////////////////////////////////////////
                        case MULTIMEDIA_SECOND_FILE_BROWSER: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContent(i);
                            break;
                        }
                        // ///////////////////////////////////////////
                        // PVR screen FILE BROWSER
                        // ///////////////////////////////////////////
                        case MULTIMEDIA_PVR_FILE_BROWSER: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContent(i);
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else {
            endIndex = numberOfItems;
            // Store index of last element for later
            indexOfLastElement = endIndex - 1;
            // Add content in array window
            for (int i = startIndex; i < endIndex; i++) {
                indexInCurrentList = INDEX_LOOK_UP_TABLE_THREE_ROWS[i
                        % itemsPerScreen];
                try {
                    switch (gridId) {
                    // ///////////////////////////////////////////
                    // Multimedia second screen FILE BROWSER
                    // ///////////////////////////////////////////
                        case MULTIMEDIA_SECOND_FILE_BROWSER: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContent(i);
                            break;
                        }
                        // ///////////////////////////////////////////
                        // PVR screen FILE BROWSER
                        // ///////////////////////////////////////////
                        case MULTIMEDIA_PVR_FILE_BROWSER: {
                            currentItemsOnScreen[indexInCurrentList] = contentListControl
                                    .getContent(i);
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // Add fake items
            for (int i = endIndex % itemsPerScreen; i < itemsPerScreen; i++) {
                indexInCurrentList = INDEX_LOOK_UP_TABLE_THREE_ROWS[i
                        % itemsPerScreen];
                // Fake item
                currentItemsOnScreen[indexInCurrentList] = null;
                numberOfFakeItems++;
            }
            // If there is no elements add all fake items
            if (endIndex == 0) {
                for (int i = 0; i < itemsPerScreen; i++) {
                    // Fake item
                    currentItemsOnScreen[i] = null;
                    numberOfFakeItems++;
                }
            }
        }
    }

    /** Fill page */
    private void fillPage() {
        try {
            contentListControl = MainActivity.service.getContentListControl();
            if (contentListControl != null) {
                switch (numberOfRows) {
                    case 1:
                        fill_one_row();
                        break;
                    case 2:
                        fill_two_rows();
                        break;
                    case 3:
                        fill_three_rows();
                        break;
                    default:
                        return;
                }
                // Refresh contents
                refreshContentLists(numberOfRows, currentItemsOnScreen);
                // Refresh adapter
                adapter.notifyDataSetChanged();
                // Check focus ability of grid view
                if (numberOfItems == 0) {
                    disableGridView();
                } else {
                    enableGridView();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /** Enable grid view */
    private void enableGridView() {
        gridView.setFocusable(true);
        gridView.setEnabled(true);
    }

    /** Disable grid view */
    private void disableGridView() {
        gridView.setFocusable(false);
        gridView.setEnabled(false);
    }

    /** Scroll right */
    public void scrollRight() {
        if (contentListControl != null) {
            // //////////////////////////
            // 1 ROW
            // /////////////////////////////
            if (numberOfRows == 1) {
                Content[] tempArray = new Content[itemsPerScreen];
                int indexOfLastVisible = indexOfLastElement;
                if (indexOfLastVisible != numberOfItems - 1) {
                    // Add two new items on right
                    if (indexOfLastVisible + 1 < numberOfItems) {
                        // Shift one place right
                        for (int i = 1; i < itemsPerScreen; i++) {
                            tempArray[i - 1] = currentItemsOnScreen[i];
                        }
                        int lastIndex = indexOfLastVisible + 1;
                        try {
                            switch (gridId) {
                            // ///////////////////////////////////
                            // Content list RECENTLY
                            // ///////////////////////////////////
                                case CONTENT_LIST_RECENTLY: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getRecentlyWatchedItem(lastIndex);
                                    break;
                                }
                                // ////////////////////////////////
                                // Content list FAVORITE
                                // ////////////////////////////////
                                case CONTENT_LIST_FAVORITE: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getFavoriteItem(lastIndex);
                                    break;
                                }
                                // ////////////////////////////////
                                // Multimedia first screen RECENTLY
                                // ////////////////////////////////
                                case MULTIMEDIA_FIRST_RECENTLY: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getRecentlyWatchedItem(lastIndex);
                                    break;
                                }
                                // ////////////////////////////////
                                // Multimedia first screen FAVORITE
                                // ////////////////////////////////
                                case MULTIMEDIA_FIRST_FAVORITE: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getFavoriteItem(lastIndex);
                                    break;
                                }
                                // ////////////////////////////////
                                // Multimedia second screen FILE PATH
                                // ////////////////////////////////
                                case MULTIMEDIA_SECOND_FILE_PATH: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getPath(lastIndex);
                                    break;
                                }
                            }
                            indexOfFirstElement++;
                            indexOfLastElement++;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        currentItemsOnScreen = tempArray;
                    }
                    // Calculate current screen
                    indexOfCurrentScreen = indexOfFirstElement / itemsPerScreen;
                    refreshContentLists(1, currentItemsOnScreen);
                    // Refresh adapter
                    adapter.notifyDataSetChanged();
                }
            }
            // /////////////////////////////////////////////////
            // 2 ROWS
            // /////////////////////////////////////////////////
            if (numberOfRows == 2) {
                // Create temporary array
                Content[] tempArray = new Content[itemsPerScreen];
                // Index of last visible element in local list
                int indexOfLastVisible = indexOfLastElement;
                if (indexOfLastVisible != numberOfItems - 1) {
                    // Add two new items on right
                    if (indexOfLastVisible + 2 < numberOfItems) {
                        // Shift one place right
                        // First row
                        for (int i = 1; i < itemsPerScreen / 2; i++) {
                            tempArray[i - 1] = currentItemsOnScreen[i];
                        }
                        // Second row
                        for (int i = itemsPerScreen / 2 + 1; i < itemsPerScreen; i++) {
                            tempArray[i - 1] = currentItemsOnScreen[i];
                        }
                        int firstRowLastIndex = indexOfLastVisible + 1;
                        try {
                            switch (gridId) {
                            // /////////////////////////////////
                            // Content list ALL
                            // /////////////////////////////////
                                case CONTENT_LIST_ALL: {
                                    tempArray[itemsPerScreen / 2 - 1] = contentListControl
                                            .getContent(firstRowLastIndex);
                                    break;
                                }
                                // /////////////////////////////////
                                // Multimedia first screen FILE BROWSER
                                // /////////////////////////////////
                                case MULTIMEDIA_FIRST_FILE_BROWSER: {
                                    tempArray[itemsPerScreen / 2 - 1] = contentListControl
                                            .getContent(firstRowLastIndex);
                                    break;
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        int secondRowLastIndex = indexOfLastVisible + 2;
                        try {
                            switch (gridId) {
                            // ////////////////////////////////
                            // Content list ALL
                            // ////////////////////////////////
                                case CONTENT_LIST_ALL: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getContent(secondRowLastIndex);
                                    break;
                                }
                                // ////////////////////////////////
                                // Multimedia first screen FILE BROWSER
                                // ////////////////////////////////
                                case MULTIMEDIA_FIRST_FILE_BROWSER: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getContent(secondRowLastIndex);
                                    break;
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        indexOfFirstElement = indexOfFirstElement + 2;
                        indexOfLastElement = indexOfLastElement + 2;
                        currentItemsOnScreen = tempArray;
                    } else {
                        // Add one item and one fake on right
                        if (indexOfLastVisible + 1 < numberOfItems) {
                            // Shift one place right
                            // First row
                            for (int i = 1; i < itemsPerScreen / 2; i++) {
                                tempArray[i - 1] = currentItemsOnScreen[i];
                            }
                            // Second row
                            for (int i = itemsPerScreen / 2 + 1; i < itemsPerScreen; i++) {
                                tempArray[i - 1] = currentItemsOnScreen[i];
                            }
                            int firstRowLastIndex = indexOfLastVisible + 1;
                            try {
                                switch (gridId) {
                                // //////////////////////////////
                                // Content list ALL
                                // ///////////////////////////////
                                    case CONTENT_LIST_ALL: {
                                        tempArray[itemsPerScreen / 2 - 1] = contentListControl
                                                .getContent(firstRowLastIndex);
                                        break;
                                    }
                                    // //////////////////////////////
                                    // Multimedia first screen FILE BROWSER
                                    // ///////////////////////////////
                                    case MULTIMEDIA_FIRST_FILE_BROWSER: {
                                        tempArray[itemsPerScreen / 2 - 1] = contentListControl
                                                .getContent(firstRowLastIndex);
                                        break;
                                    }
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            // Fake element
                            tempArray[itemsPerScreen - 1] = null;
                            numberOfFakeItems++;
                            indexOfFirstElement = indexOfFirstElement + 2;
                            indexOfLastElement = indexOfLastElement + 1;
                            currentItemsOnScreen = tempArray;
                        }
                    }
                    // Calculate current screen
                    indexOfCurrentScreen = indexOfFirstElement / itemsPerScreen;
                    // Refresh contents
                    refreshContentLists(2, currentItemsOnScreen);
                    // Refresh adapter
                    adapter.notifyDataSetChanged();
                }
            }
            // ///////////////////////////////////////
            // 3 ROWS
            // ///////////////////////////////////////
            if (numberOfRows == 3) {
                // Create temporary array
                Content[] tempArray = new Content[itemsPerScreen];
                // Index of last visible element in local list
                int indexOfLastVisible = indexOfLastElement;
                if (indexOfLastVisible != numberOfItems - 1) {
                    // Add three new items on right
                    if (indexOfLastVisible + 3 < numberOfItems) {
                        // Shift one place right
                        // First row
                        for (int i = 1; i < itemsPerScreen / 3; i++) {
                            tempArray[i - 1] = currentItemsOnScreen[i];
                        }
                        // Second row
                        for (int i = itemsPerScreen / 3 + 1; i < 2 * itemsPerScreen / 3; i++) {
                            tempArray[i - 1] = currentItemsOnScreen[i];
                        }
                        // Third row
                        for (int i = 2 * itemsPerScreen / 3 + 1; i < itemsPerScreen; i++) {
                            tempArray[i - 1] = currentItemsOnScreen[i];
                        }
                        // /////////////////////////
                        // First row
                        // /////////////////////////
                        int firstRowLastIndex = indexOfLastVisible + 1;
                        try {
                            switch (gridId) {
                            // ///////////////////////////////////////////
                            // Multimedia second screen FILE BROWSER
                            // ///////////////////////////////////////////
                                case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                    tempArray[itemsPerScreen / 3 - 1] = contentListControl
                                            .getContent(firstRowLastIndex);
                                    break;
                                }
                                // ///////////////////////////////////////////
                                // PVR screen FILE BROWSER
                                // ///////////////////////////////////////////
                                case MULTIMEDIA_PVR_FILE_BROWSER: {
                                    tempArray[itemsPerScreen / 3 - 1] = contentListControl
                                            .getContent(firstRowLastIndex);
                                    break;
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        // /////////////////////////
                        // Second row
                        // /////////////////////////
                        int secondRowLastIndex = indexOfLastVisible + 2;
                        try {
                            switch (gridId) {
                            // ///////////////////////////////////////////
                            // Multimedia second screen FILE BROWSER
                            // ///////////////////////////////////////////
                                case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                    tempArray[2 * itemsPerScreen / 3 - 1] = contentListControl
                                            .getContent(secondRowLastIndex);
                                    break;
                                }
                                // ///////////////////////////////////////////
                                // PVR screen FILE BROWSER
                                // ///////////////////////////////////////////
                                case MULTIMEDIA_PVR_FILE_BROWSER: {
                                    tempArray[2 * itemsPerScreen / 3 - 1] = contentListControl
                                            .getContent(secondRowLastIndex);
                                    break;
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        // /////////////////////////
                        // Third row
                        // /////////////////////////
                        int thirdRowLastIndex = indexOfLastVisible + 3;
                        try {
                            switch (gridId) {
                            // ///////////////////////////////////////////
                            // Multimedia second screen FILE BROWSER
                            // ///////////////////////////////////////////
                                case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getContent(thirdRowLastIndex);
                                    break;
                                }
                                // ///////////////////////////////////////////
                                // PVR screen FILE BROWSER
                                // ///////////////////////////////////////////
                                case MULTIMEDIA_PVR_FILE_BROWSER: {
                                    tempArray[itemsPerScreen - 1] = contentListControl
                                            .getContent(thirdRowLastIndex);
                                    break;
                                }
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        indexOfFirstElement = indexOfFirstElement + 3;
                        indexOfLastElement = indexOfLastElement + 3;
                        currentItemsOnScreen = tempArray;
                    } else {
                        // Add one item and one fake on right
                        if (indexOfLastVisible + 2 < numberOfItems) {
                            // Shift one place right
                            // First row
                            for (int i = 1; i < itemsPerScreen / 3; i++) {
                                tempArray[i - 1] = currentItemsOnScreen[i];
                            }
                            // Second row
                            for (int i = itemsPerScreen / 3 + 1; i < 2 * itemsPerScreen / 3; i++) {
                                tempArray[i - 1] = currentItemsOnScreen[i];
                            }
                            // Third row
                            for (int i = 2 * itemsPerScreen / 3 + 1; i < itemsPerScreen; i++) {
                                tempArray[i - 1] = currentItemsOnScreen[i];
                            }
                            // //////////////////////////
                            // First row
                            // //////////////////////////
                            int firstRowLastIndex = indexOfLastVisible + 1;
                            try {
                                switch (gridId) {
                                // ///////////////////////////////////////////
                                // Multimedia second screen FILE BROWSER
                                // ///////////////////////////////////////////
                                    case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                        tempArray[itemsPerScreen / 3 - 1] = contentListControl
                                                .getContent(firstRowLastIndex);
                                        break;
                                    }
                                    // ///////////////////////////////////////////
                                    // PVR screen FILE BROWSER
                                    // ///////////////////////////////////////////
                                    case MULTIMEDIA_PVR_FILE_BROWSER: {
                                        tempArray[itemsPerScreen - 1] = contentListControl
                                                .getContent(firstRowLastIndex);
                                        break;
                                    }
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            // ///////////////////////////
                            // Second row
                            // ///////////////////////////
                            int secondRowLastIndex = indexOfLastVisible + 2;
                            try {
                                switch (gridId) {
                                // ///////////////////////////////////////////
                                // Multimedia second screen FILE BROWSER
                                // ///////////////////////////////////////////
                                    case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                        tempArray[2 * itemsPerScreen / 3 - 1] = contentListControl
                                                .getContent(secondRowLastIndex);
                                        break;
                                    }
                                    // ///////////////////////////////////////////
                                    // PVR screen FILE BROWSER
                                    // ///////////////////////////////////////////
                                    case MULTIMEDIA_PVR_FILE_BROWSER: {
                                        tempArray[2 * itemsPerScreen / 3 - 1] = contentListControl
                                                .getContent(secondRowLastIndex);
                                        break;
                                    }
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            // Fake element
                            tempArray[itemsPerScreen - 1] = null;
                            numberOfFakeItems++;
                            indexOfFirstElement = indexOfFirstElement + 3;
                            indexOfLastElement = indexOfLastElement + 2;
                            currentItemsOnScreen = tempArray;
                        } else {
                            // Add two fake items
                            if (indexOfLastVisible + 1 < numberOfItems) {
                                // Shift one place right
                                // First row
                                for (int i = 1; i < itemsPerScreen / 3; i++) {
                                    tempArray[i - 1] = currentItemsOnScreen[i];
                                }
                                // Second row
                                for (int i = itemsPerScreen / 3 + 1; i < 2 * itemsPerScreen / 3; i++) {
                                    tempArray[i - 1] = currentItemsOnScreen[i];
                                }
                                // Third row
                                for (int i = 2 * itemsPerScreen / 3 + 1; i < itemsPerScreen; i++) {
                                    tempArray[i - 1] = currentItemsOnScreen[i];
                                }
                                // //////////////////////////
                                // First row
                                // //////////////////////////
                                int firstRowLastIndex = indexOfLastVisible + 1;
                                try {
                                    switch (gridId) {
                                    // ///////////////////////////////////////////
                                    // Multimedia second screen FILE BROWSER
                                    // ///////////////////////////////////////////
                                        case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                            tempArray[itemsPerScreen / 3 - 1] = contentListControl
                                                    .getContent(firstRowLastIndex);
                                            break;
                                        }
                                        // ///////////////////////////////////////////
                                        // PVR screen FILE BROWSER
                                        // ///////////////////////////////////////////
                                        case MULTIMEDIA_PVR_FILE_BROWSER: {
                                            tempArray[itemsPerScreen / 3 - 1] = contentListControl
                                                    .getContent(firstRowLastIndex);
                                            break;
                                        }
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                // Fake elements
                                tempArray[2 * itemsPerScreen / 3 - 1] = null;
                                tempArray[itemsPerScreen - 1] = null;
                                numberOfFakeItems = numberOfFakeItems + 2;
                                indexOfFirstElement = indexOfFirstElement + 3;
                                indexOfLastElement = indexOfLastElement + 1;
                                currentItemsOnScreen = tempArray;
                            }
                        }
                    }
                    // Calculate current screen
                    indexOfCurrentScreen = indexOfFirstElement / itemsPerScreen;
                    // Refresh contents
                    refreshContentLists(3, currentItemsOnScreen);
                    // Refresh adapter
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    /** Scroll left */
    public void scrollLeft() {
        if (contentListControl != null) {
            // //////////////////////////////
            // ROW 1
            // //////////////////////////////
            if (numberOfRows == 1) {
                Content[] tempArray = new Content[itemsPerScreen];
                int indexOfFirstVisible = indexOfFirstElement;
                // Add one new item on right
                if (indexOfFirstVisible - 1 >= 0) {
                    // Shift one place left
                    for (int i = 0; i < itemsPerScreen - 1; i++) {
                        tempArray[i + 1] = currentItemsOnScreen[i];
                    }
                    int firstIndex = indexOfFirstVisible - 1;
                    try {
                        switch (gridId) {
                        // //////////////////////////////
                        // Content list RECENTLY
                        // //////////////////////////////
                            case CONTENT_LIST_RECENTLY: {
                                tempArray[0] = contentListControl
                                        .getRecentlyWatchedItem(firstIndex);
                                break;
                            }
                            // ///////////////////////////////////
                            // Content list FAVORITE
                            // //////////////////////////////////
                            case CONTENT_LIST_FAVORITE: {
                                tempArray[0] = contentListControl
                                        .getFavoriteItem(firstIndex);
                                break;
                            }
                            // ///////////////////////////////////
                            // Multimedia first screen RECENTLY
                            // //////////////////////////////////
                            case MULTIMEDIA_FIRST_RECENTLY: {
                                tempArray[0] = contentListControl
                                        .getRecentlyWatchedItem(firstIndex);
                                break;
                            }
                            // ///////////////////////////////////
                            // Multimedia first screen FAVORITE
                            // //////////////////////////////////
                            case MULTIMEDIA_FIRST_FAVORITE: {
                                tempArray[0] = contentListControl
                                        .getFavoriteItem(firstIndex);
                                break;
                            }
                            // ///////////////////////////////////
                            // Multimedia second screen FILE PATH
                            // //////////////////////////////////
                            case MULTIMEDIA_SECOND_FILE_PATH: {
                                tempArray[0] = contentListControl
                                        .getPath(firstIndex);
                                break;
                            }
                        }
                        indexOfFirstElement--;
                        if (numberOfFakeItems == 0) {
                            indexOfLastElement = indexOfLastElement - 1;
                        }
                        // If there is fake elements reduce number of it
                        if (numberOfFakeItems > 0) {
                            numberOfFakeItems = numberOfFakeItems - 1;
                            numberOfFakeItems--;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    currentItemsOnScreen = tempArray;
                }
                // Calculate current screen
                indexOfCurrentScreen = indexOfFirstElement / itemsPerScreen;
                // Refresh contents
                refreshContentLists(1, currentItemsOnScreen);
                // Refresh adapter
                adapter.notifyDataSetChanged();
            }
            // ////////////////////////////////
            // ROW 2
            // ////////////////////////////////
            if (numberOfRows == 2) {
                Content[] tempArray = new Content[itemsPerScreen];
                int indexOfFirstVisible = indexOfFirstElement;
                // Add two new items on right
                if (indexOfFirstVisible - 2 >= 0) {
                    // Shift one place left
                    // First row
                    for (int i = 0; i < itemsPerScreen / 2 - 1; i++) {
                        tempArray[i + 1] = currentItemsOnScreen[i];
                    }
                    // Second row
                    for (int i = itemsPerScreen / 2; i < itemsPerScreen - 1; i++) {
                        tempArray[i + 1] = currentItemsOnScreen[i];
                    }
                    int firstRowFirstIndex = indexOfFirstVisible - 2;
                    try {
                        switch (gridId) {
                        // ////////////////////////////
                        // Content list ALL
                        // ////////////////////////////
                            case CONTENT_LIST_ALL: {
                                tempArray[0] = contentListControl
                                        .getContent(firstRowFirstIndex);
                                break;
                            }
                            // ////////////////////////////
                            // Multimedia first screen FILE BROWSER
                            // ////////////////////////////
                            case MULTIMEDIA_FIRST_FILE_BROWSER: {
                                tempArray[0] = contentListControl
                                        .getContent(firstRowFirstIndex);
                                break;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    int secondRowFirstIndex = indexOfFirstVisible - 1;
                    try {
                        switch (gridId) {
                        // ////////////////////////////////
                        // Content list ALL
                        // ////////////////////////////////
                            case CONTENT_LIST_ALL: {
                                tempArray[itemsPerScreen / 2] = contentListControl
                                        .getContent(secondRowFirstIndex);
                                break;
                            }
                            // ////////////////////////////////
                            // Multimedia first screen FILE BROWSER
                            // ////////////////////////////////
                            case MULTIMEDIA_FIRST_FILE_BROWSER: {
                                tempArray[itemsPerScreen / 2] = contentListControl
                                        .getContent(secondRowFirstIndex);
                                break;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    indexOfFirstElement = indexOfFirstElement - 2;
                    if (numberOfFakeItems == 0) {
                        indexOfLastElement = indexOfLastElement - 2;
                    }
                    if (numberOfFakeItems == 1) {
                        indexOfLastElement = indexOfLastElement - 1;
                    }
                    // If there is fake elements reduce number of it
                    if (numberOfFakeItems >= 2) {
                        numberOfFakeItems = numberOfFakeItems - 2;
                    } else {
                        if (numberOfFakeItems >= 1) {
                            numberOfFakeItems--;
                        }
                    }
                    currentItemsOnScreen = tempArray;
                }
                // Calculate current screen
                indexOfCurrentScreen = indexOfFirstElement / itemsPerScreen;
                // Refresh contents
                refreshContentLists(2, currentItemsOnScreen);
                // Refresh adapter
                adapter.notifyDataSetChanged();
            }
            // //////////////////////////////////////////////
            // 3 ROWS
            // //////////////////////////////////////////////
            if (numberOfRows == 3) {
                Content[] tempArray = new Content[itemsPerScreen];
                int indexOfFirstVisible = indexOfFirstElement;
                // Add three new items on right
                if (indexOfFirstVisible - 3 >= 0) {
                    // Shift one place left
                    // First row
                    for (int i = 0; i < itemsPerScreen / 3 - 1; i++) {
                        tempArray[i + 1] = currentItemsOnScreen[i];
                    }
                    // Second row
                    for (int i = itemsPerScreen / 3; i < 2 * itemsPerScreen / 3 - 1; i++) {
                        tempArray[i + 1] = currentItemsOnScreen[i];
                    }
                    // Third row
                    for (int i = 2 * itemsPerScreen / 3; i < itemsPerScreen - 1; i++) {
                        tempArray[i + 1] = currentItemsOnScreen[i];
                    }
                    // ///////////////////////////
                    // First row
                    // /////////////////////////////
                    int firstRowFirstIndex = indexOfFirstVisible - 3;
                    try {
                        switch (gridId) {
                        // //////////////////////////////////////////
                        // Multimedia second screen FILE BROWSER
                        // //////////////////////////////////////////
                            case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                tempArray[0] = contentListControl
                                        .getContent(firstRowFirstIndex);
                                break;
                            }
                            // //////////////////////////////////////////
                            // PVR screen FILE BROWSER
                            // //////////////////////////////////////////
                            case MULTIMEDIA_PVR_FILE_BROWSER: {
                                tempArray[0] = contentListControl
                                        .getContent(firstRowFirstIndex);
                                break;
                            }
                            default:
                                break;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    // /////////////////////////////////
                    // Second row
                    // /////////////////////////////////
                    int secondRowFirstIndex = indexOfFirstVisible - 2;
                    try {
                        switch (gridId) {
                        // //////////////////////////////////////////
                        // Multimedia second screen FILE BROWSER
                        // //////////////////////////////////////////
                            case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                tempArray[itemsPerScreen / 3] = contentListControl
                                        .getContent(secondRowFirstIndex);
                                break;
                            }
                            // //////////////////////////////////////////
                            // PVR screen FILE BROWSER
                            // //////////////////////////////////////////
                            case MULTIMEDIA_PVR_FILE_BROWSER: {
                                tempArray[itemsPerScreen / 3] = contentListControl
                                        .getContent(secondRowFirstIndex);
                                break;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    // /////////////////////////////////
                    // Third row
                    // /////////////////////////////////
                    int thirdRowFirstIndex = indexOfFirstVisible - 1;
                    try {
                        switch (gridId) {
                        // //////////////////////////////////////////
                        // Multimedia second screen FILE BROWSER
                        // //////////////////////////////////////////
                            case MULTIMEDIA_SECOND_FILE_BROWSER: {
                                tempArray[2 * itemsPerScreen / 3] = contentListControl
                                        .getContent(thirdRowFirstIndex);
                                break;
                            }
                            // //////////////////////////////////////////
                            // PVR screen FILE BROWSER
                            // //////////////////////////////////////////
                            case MULTIMEDIA_PVR_FILE_BROWSER: {
                                tempArray[2 * itemsPerScreen / 3] = contentListControl
                                        .getContent(thirdRowFirstIndex);
                                break;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    indexOfFirstElement = indexOfFirstElement - 3;
                    if (numberOfFakeItems == 0) {
                        indexOfLastElement = indexOfLastElement - 3;
                    }
                    if (numberOfFakeItems == 1) {
                        indexOfLastElement = indexOfLastElement - 2;
                    }
                    if (numberOfFakeItems == 2) {
                        indexOfLastElement = indexOfLastElement - 1;
                    }
                    // If there is fake elements reduce number of it
                    if (numberOfFakeItems >= 3) {
                        numberOfFakeItems = numberOfFakeItems - 3;
                    } else {
                        if (numberOfFakeItems >= 2) {
                            numberOfFakeItems = numberOfFakeItems - 2;
                        } else {
                            if (numberOfFakeItems >= 1) {
                                numberOfFakeItems = numberOfFakeItems - 1;
                            }
                        }
                    }
                    currentItemsOnScreen = tempArray;
                }
                // Calculate current screen
                indexOfCurrentScreen = indexOfFirstElement / itemsPerScreen;
                // Refresh contents
                refreshContentLists(3, currentItemsOnScreen);
                // Refresh adapter
                adapter.notifyDataSetChanged();
            }
        }
    }

    /** Next recently page */
    public void pageNext() {
        if (indexOfCurrentScreen < numberOfScreens - 1) {
            indexOfCurrentScreen++;
            fillPage();
            // Request focus needed
            gridView.requestFocusFromTouch();
            // Set selection
            gridView.setSelection(0);
        }
    }

    /** Previous page recently */
    public void pagePrevious() {
        if (indexOfCurrentScreen > 0) {
            indexOfCurrentScreen--;
            fillPage();
            // Request focus needed
            gridView.requestFocusFromTouch();
            // Set selection
            gridView.setSelection(0);
        }
    }

    /** Refresh contents lists in adapters */
    private void refreshContentLists(int numberOfRows, Content[] currentItems) {
        // Check number of rows
        switch (numberOfRows) {
        // /////////////////////////////////
        // ONE ROW
        // /////////////////////////////////
            case 1: {
                switch (gridId) {
                // ///////////////////////////
                // Content list RECENTLY
                // ///////////////////////////
                    case CONTENT_LIST_RECENTLY: {
                        RecentlyHandler.recentlyCurrentItems = currentItems;
                        break;
                    }
                    // //////////////////////////////
                    // Content list FAVORITE
                    // //////////////////////////////
                    case CONTENT_LIST_FAVORITE: {
                        FavoriteHandler.favoriteCurrentItems = currentItems;
                        break;
                    }
                    // //////////////////////////////
                    // Multimedia first screen RECENTLY
                    // //////////////////////////////
                    case MULTIMEDIA_FIRST_RECENTLY: {
                        MultimediaRecentlyHandler.multimediaRecentlyCurrentItems = currentItems;
                        break;
                    }
                    // //////////////////////////////
                    // Multimedia first screen FAVORITE
                    // //////////////////////////////
                    case MULTIMEDIA_FIRST_FAVORITE: {
                        MultimediaFavoriteHandler.multimediaFavoriteCurrentItems = currentItems;
                        break;
                    }
                    // //////////////////////////////
                    // Multimedia second screen FILE PATH
                    // //////////////////////////////
                    case MULTIMEDIA_SECOND_FILE_PATH: {
                        MultimediaFilePathHandler.filePathCurrentItems = currentItems;
                        break;
                    }
                }
                break;
            }
            // ////////////////////////////
            // TWO ROWS
            // ////////////////////////////
            case 2: {
                switch (gridId) {
                // ///////////////////////////////
                // Content list ALL
                // ///////////////////////////////
                    case CONTENT_LIST_ALL: {
                        AllHandler.allCurrentItems = currentItems;
                        break;
                    }
                    // ///////////////////////////////
                    // Multimedia first screen FILE BROWSER
                    // ///////////////////////////////
                    case MULTIMEDIA_FIRST_FILE_BROWSER: {
                        MultimediaFileBrowserHandler.fileBrowserCurrentItemsFirstScreen = currentItems;
                        break;
                    }
                }
                break;
            }
            // /////////////////////////////////////
            // THREE ROWS
            // //////////////////////////////////////
            case 3: {
                switch (gridId) {
                // ////////////////////////////////////////////
                // Multimedia second screen FILE BROWSER
                // ////////////////////////////////////////////
                    case MULTIMEDIA_SECOND_FILE_BROWSER: {
                        MultimediaFileBrowserHandler.fileBrowserCurrentItemsSecondScreen = currentItems;
                        break;
                    }
                    // ////////////////////////////////////////////
                    // PVR screen FILE BROWSER
                    // ////////////////////////////////////////////
                    case MULTIMEDIA_PVR_FILE_BROWSER: {
                        MultimediaFileBrowserHandler.fileBrowserCurrentItemsPvrScreen = currentItems;
                        break;
                    }
                }
                break;
            }
        }
    }

    // /////////////////////////////////
    // Getters and setters
    // /////////////////////////////////
    public int getIndexOfLastElement() {
        return indexOfLastElement;
    }

    public int getIndexOfFirstElement() {
        return indexOfFirstElement;
    }

    public void setIndexOfCurrentScreen(int indexOfCurrentScreen) {
        this.indexOfCurrentScreen = indexOfCurrentScreen;
    }
}
