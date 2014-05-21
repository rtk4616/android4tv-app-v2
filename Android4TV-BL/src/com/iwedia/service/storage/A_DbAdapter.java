package com.iwedia.service.storage;

import java.util.ArrayList;
import java.util.Collections;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.applications.ApplicationContent;
import com.iwedia.comm.content.inputs.InputContent;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.content.widgets.WidgetContent;
import com.iwedia.comm.enums.AppListType;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.application.ApplicationManager;
import com.iwedia.service.widget.WidgetManager;

public class A_DbAdapter {
    private final String LOG_TAG = "A_DbAdapter";
    // FAVOURITE LIST TABLE CONSTANTS
    public static final String KEY_ROW_FAV_NAME_KEY_ROW = "list_id";
    public static final String KEY_ROW_FAV_NAME = "name";
    // CONTENT ITEM TABLE CONSTANTS
    public static final String KEY_ROW_CONTENT_ITEM_ROW_ID = "item_id";
    public static final String KEY_ROW_CONTENT_ITEM_INDEX = "s_index";
    public static final String KEY_ROW_CONTENT_ITEM_FILTER_TYPE = "filter_type";
    public static final String MULTIMEDIA_FILE_URL = "mm_file_url";
    public static final String MULTIMEDIA_FILE_EXT = "mm_file_ext";
    public static final String MULTIMEDIA_FILE_TYPE = "mm_file_type";
    public static final String MULTIMEDIA_TYPE = "mm_type";
    public static final String MULTIMEDIA_ABSOLUTE_PATH = "mm_absolute_path";
    public static final String MULTIMEDIA_CONTENT_NAME = "content_name";
    public static final String MULTIMEDIA_CONTENT_ID = "mm_id";
    public static final String MULTIMEDIA_CONTENT_DLNA_NAME = "mm_dlna_name";
    public static final String MULTIMEDIA_ROOT_ID = "mm_dlna_root_id";
    public static final String MULTIMEDIA_IS_FAVORITE = "mm_dlna_is_favorite";
    public static final String MULTIMEDIA_PLAYLIST_ID = "mm_playlist_id";
    public static final String MULTIMEDIA_PLAYLIST_ARTIST = "mm_playlist_artist";
    public static final String MULTIMEDIA_PLAYLIST_TITLE = "mm_playlist_title";
    public static final String MULTIMEDIA_PLAYLIST_DURATION = "mm_playlist_duration";
    public static final String MULTIMEDIA_PLAYLIST_RESOLUTION = "mm_playlist_resolution";
    public static final String MULTIMEDIA_PLAYLIST_NAME = "mm_playlist_name";
    public static final String MULTIMEDIA_PLAYLIST_TYPE = "mm_playlist_type";
    public static final String LOCKED_CONTENTS_COLUMN_NAME = "name";
    public static final String LOCKED_CONTENTS_COLUMN_INDEX = "_index";
    public static final String LOCKED_CONTENTS_COLUMN_FILTER_TYPE = "filter_type";
    public static final String RENAMED_INPUT_CONTENT_INDEX = "_input_index";
    public static final String RENAMED_INPUT_CONTENT_NAME = "new_name";
    private final String CLEAR_FAVOURITE_LIST = "DELETE FROM list_to_item WHERE EXISTS( select favourite_list.name from favourite_list where favourite_list.list_id = list_to_item.list_id and favourite_list.name=?);";
    private final String DELETE_CONTENT_FROM_FAVOURITE_LIST = "DELETE FROM list_to_item WHERE EXISTS( select favourite_list.name, content_item.s_index from favourite_list, content_item where favourite_list.list_id = list_to_item.list_id and list_to_item.item_id = content_item.item_id and favourite_list.name = ? and content_item.s_index = ?);";
    private final String GET_ELEMENT_IN_FAV_LIST = "select content_item.s_index, content_item.filter_type, content_item.mm_file_url, content_item.mm_file_ext, content_item.mm_file_type, content_item.mm_type, content_item.mm_absolute_path, content_item.content_name, content_item.mm_id, content_item.mm_dlna_name, content_item.mm_dlna_root_id, content_item.mm_dlna_is_favorite, content_item.mm_playlist_id, content_item.mm_playlist_artist, content_item.mm_playlist_title, content_item.mm_playlist_duration, content_item.mm_playlist_resolution, content_item.mm_playlist_name, content_item.mm_playlist_type from favourite_list, content_item, list_to_item where favourite_list.name=? and content_item.filter_type=? and favourite_list.list_id=list_to_item.list_id and list_to_item.item_id=content_item.item_id;";
    private final String GET_ELEMENT_IN_LIST = "select content_item.s_index, content_item.filter_type, content_item.mm_file_url, content_item.mm_file_ext, content_item.mm_file_type, content_item.mm_type, content_item.mm_absolute_path, content_item.content_name, content_item.mm_id, content_item.mm_dlna_name, content_item.mm_dlna_root_id, content_item.mm_dlna_is_favorite, content_item.mm_playlist_id, content_item.mm_playlist_artist, content_item.mm_playlist_title, content_item.mm_playlist_duration, content_item.mm_playlist_resolution, content_item.mm_playlist_name, content_item.mm_playlist_type from favourite_list, content_item, list_to_item where favourite_list.name=? and favourite_list.list_id=list_to_item.list_id and list_to_item.item_id=content_item.item_id;";
    private final String GET_FAVOURITE_LIST_POSITION = "select * from favourite_list where name=?";
    // LOCKED CONTENTS
    private final String INSERT_INTO_LOCKED_CONTENT = "insert into content_lock (name, filter_type, _index) values(?,?,?)";
    private final String SELECT_ALL_FROM_LOCKED_CONTENT_BY_FILTER_TYPE = "select * from content_lock";
    private final String REMOVE_LOCKED_CONTENT_FROM_CONTENT_LIST = "delete from content_lock where name=? and filter_type=?";
    // RENAMED INPUT CONTENTS
    private final String INSERT_INTO_RENAMED_CONTENT = "insert into renamed_content (_input_index, new_name) values (?, ?)";
    private final String REMOVE_RENAMED_INPUT_FROM_CONTENT_LIST = "delete from renamed_content where _input_index=?";
    private final String GET_ELEMENT_FROM_RENAMED_CONTENT = "select * from renamed_content where _input_index=?";
    private SQLiteDatabase database;
    private A_DatabaseHelper dbHelper;

    public void open() {
        try {
            dbHelper = new A_DatabaseHelper(IWEDIAService.getInstance());
            database = dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        createFavouriteList(IWEDIAService.getInstance()
                .getFavoriteListTableName());
    }

    public void close() {
        database.close();
    }

    /**
     * Adds content to database table.
     * 
     * @param name
     *        - Database table name.
     * @param content
     *        - Content to be added.
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long addContentToList(String name, Content content) {
        /**
         * Add content to content_item DB table (table that contains only
         * content items) and retrieve raw index of added content.
         */
        int contentPosition = (int) database.insert(
                A_DatabaseHelper.TABLE_NAME_CONTENT_ITEM, null,
                createContentValueContentItem(content));
        /**
         * retrieve raw index of content_list name in DB table favourite_list.
         */
        int favPosition = (int) createFavouriteList(name);
        /**
         * Link content item in content_item table (all contents in DB are
         * stored in this table), with content list name stored in
         * favourite_list (all content list names are stored in this table
         * (recently_watched, favourite_list_sport, favourite_list_kids,
         * favourite_list_movies, etc.) table.
         */
        return database.insert(A_DatabaseHelper.TABLE_NAME_FAV_LIST_TO_ITEM,
                null,
                createContentValueListToItem(favPosition, contentPosition));
    }

    public void clearList(String name) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "clear");
        }
        database.execSQL(CLEAR_FAVOURITE_LIST, new String[] { name });
    }

    public void removeContentFromList(String name, Content content) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "remove single item");
        }
        /**
         * Remove content from list_to_item list
         */
        database.execSQL(DELETE_CONTENT_FROM_FAVOURITE_LIST, new String[] {
                name, "" + content.getIndex() });
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "remove is not fully implemented");
        }
        // content.getIndex nece raditi kako treba za multimedia-u...
        // getElementInListByFilter(name, content.getFilterType());
    }

    public ArrayList<Content> getElementInList(String name) {
        // TODO SNIMITI IP I DATA Servise
        ArrayList<Content> element = new ArrayList<Content>();
        // Cursor cursor = database.rawQuery(GET_ELEMENT_IN_LIST,
        // new String[] { name });
        // if(cursor.getCount() > 0) {
        // cursor.moveToFirst();
        // while(!cursor.isAfterLast()) {
        // switch(cursor.getInt(1)) {
        //
        // case FilterType.APPS:
        // ApplicationManager.getInstance().setAppType(
        // AppListType.CONTENT);
        // element.add(new ApplicationContent(cursor.getInt(0),
        // ApplicationManager.getInstance().getApplication(
        // cursor.getInt(0))));
        // break;
        // case FilterType.CABLE:
        // try {
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.CABLE, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.SATELLITE:
        // try {
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.SATELLITE, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.TERRESTRIAL:
        // try {
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.TERRESTRIAL, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.ANALOG:
        // try {
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.ANALOG, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.INPUTS:
        // try {
        // int index = cursor.getInt(0);
        // /** this is dirty hack because of skipping RF */
        // if(index > 0) {
        // index = index - 1;
        // }
        // String inputName = IWEDIAService.getInstance()
        // .getDtvManagerProxy().getContentListControl()
        // .getContentFilter(FilterType.INPUTS)
        // .getContent(index).getName();
        // Log.d(LOG_TAG,
        // "content to add: "
        // + (new InputContent(cursor.getInt(0),
        // inputName)).toString());
        // element.add(new InputContent(cursor.getInt(0),
        // inputName));
        // } catch(RemoteException e1) {
        // e1.printStackTrace();
        // }
        // break;
        // case FilterType.RADIO:
        // try {
        // element.add(new RadioContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.RADIO, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.WIDGETS:
        // element.add(new WidgetContent(cursor.getInt(0),
        // WidgetManager.getInstance().getWidgetItem(
        // cursor.getInt(0))));
        // break;
        // case FilterType.MULTIMEDIA:
        // element.add(new MultimediaContent(cursor.getString(7),
        // cursor.getString(2), cursor.getString(3), cursor
        // .getString(4), cursor.getString(5), cursor
        // .getInt(0), cursor.getString(6), cursor
        // .getString(8), cursor.getString(9), cursor
        // .getString(10), cursor.getInt(11), cursor
        // .getInt(12), cursor.getString(13), cursor
        // .getString(14), cursor.getInt(15), cursor
        // .getString(16), cursor.getString(17),
        // cursor.getString(18)));
        // break;
        // case FilterType.IP_STREAM:
        // // element.add(object)
        // break;
        // }
        //
        // cursor.moveToNext();
        // }
        //
        // }
        //
        // try {
        // cursor.close();
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        //
        // // Because last watched service has been added to database as last,
        // and
        // // it should be the first one in recently watched list in GUI holder,
        // // this list
        // // should be inverted.
        // if(name.equals(IWEDIAService.getInstance().getRecentlyListTableName()))
        // {
        // Collections.reverse(element);
        // }
        return element;
    }

    public String getNameForInputContent(int index) {
        Cursor cursor = database.rawQuery(GET_ELEMENT_FROM_RENAMED_CONTENT,
                new String[] { "" + index });
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String ret = cursor.getString(2);
            cursor.close();
            return ret;
        }
        cursor.close();
        return null;
    }

    public ArrayList<Content> getElementsInListByFilter(String name,
            int filterType) {
        // TODO dodati cuvanje FILTER_TYPE.DATA u bazu i IP
        ArrayList<Content> element = new ArrayList<Content>();
        // Cursor cursor = database.rawQuery(GET_ELEMENT_IN_FAV_LIST,
        // new String[] { name, "" + filterType });
        // if(IWEDIAService.DEBUG) {
        // Log.e(LOG_TAG, "number of elements:" + cursor.getCount()
        // + " in favorite list:" + name + " by filter type:"
        // + filterType);
        // }
        // if(cursor.getCount() > 0) {
        // cursor.moveToFirst();
        // while(!cursor.isAfterLast()) {
        // if(IWEDIAService.DEBUG)
        // try {
        // Log.e("INDEX:" + cursor.getInt(0), "FILTER TYPE:"
        // + cursor.getInt(1));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // switch(cursor.getInt(1)) {
        //
        // case FilterType.APPS:
        // ApplicationManager.getInstance().setAppType(
        // AppListType.CONTENT);
        // element.add(new ApplicationContent(cursor.getInt(0),
        // ApplicationManager.getInstance().getApplication(
        // cursor.getInt(0))));
        // break;
        // case FilterType.CABLE:
        //
        // try {
        //
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.CABLE, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        //
        // break;
        // case FilterType.SATELLITE:
        // try {
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.SATELLITE, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.TERRESTRIAL:
        // try {
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.TERRESTRIAL, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.ANALOG:
        // try {
        // element.add(new ServiceContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.ANALOG, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.INPUTS:
        // try {
        // int index = cursor.getInt(0);
        // /** this is dirty hack because of skipping RF */
        // if(index > 0) {
        // index = index - 1;
        // }
        // String inputName = IWEDIAService.getInstance()
        // .getDtvManagerProxy().getContentListControl()
        // .getContentFilter(FilterType.INPUTS)
        // .getContent(index).getName();
        // Log.d(LOG_TAG,
        // "content to add: "
        // + (new InputContent(cursor.getInt(0),
        // inputName)).toString());
        // element.add(new InputContent(cursor.getInt(0),
        // inputName));
        // } catch(RemoteException e1) {
        // e1.printStackTrace();
        // }
        // break;
        // case FilterType.RADIO:
        // try {
        // element.add(new RadioContent(cursor.getInt(0),
        // IWEDIAService.getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl(),
        // ServiceListIndex.RADIO, false));
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        // break;
        // case FilterType.WIDGETS:
        // element.add(new WidgetContent(cursor.getInt(0),
        // WidgetManager.getInstance().getWidgetItem(
        // cursor.getInt(0))));
        // break;
        // case FilterType.MULTIMEDIA:
        //
        // element.add(new MultimediaContent(cursor.getString(7),
        // cursor.getString(2), cursor.getString(3), cursor
        // .getString(4), cursor.getString(5), cursor
        // .getInt(0), cursor.getString(6), cursor
        // .getString(8), cursor.getString(9), cursor
        // .getString(10), cursor.getInt(11), cursor
        // .getInt(12), cursor.getString(13), cursor
        // .getString(14), cursor.getInt(15), cursor
        // .getString(16), cursor.getString(17),
        // cursor.getString(18)));
        // break;
        //
        // case FilterType.PVR_RECORDED: {
        //
        // int size;
        // try {
        // size = IWEDIAService.getInstance().getDtvManagerProxy()
        // .getPvrControl().updateMediaList();
        //
        // int index = cursor.getInt(cursor
        // .getColumnIndex("s_index"));
        // MediaInfo mediaDescriptor = new MediaInfo();
        // Log.d("PVR", "initial mediaDescriptor: "
        // + mediaDescriptor);
        // if(size > index)
        //
        // {
        // mediaDescriptor = IWEDIAService.getInstance()
        // .getDtvManagerProxy().getPvrControl()
        // .getMediaInfo(index);
        // element.add(new MultimediaContent(mediaDescriptor
        // .getTitle(), mediaDescriptor
        // .getDescription(), String
        // .valueOf(mediaDescriptor.getDuration()),
        // "DEFAULT", mediaDescriptor.getStartTime()
        // .toString(), index, "file",
        // "pvrfile"));
        //
        // }
        // } catch(RemoteException e) {
        // e.printStackTrace();
        // }
        //
        // break;
        //
        // }
        //
        // }
        //
        // cursor.moveToNext();
        // }
        //
        // }
        //
        // try {
        // cursor.close();
        // } catch(Exception e) {
        // e.printStackTrace();
        // }
        return element;
    }

    private ContentValues createContentValueListToItem(int favPosition,
            int contentPosition) {
        ContentValues values = new ContentValues();
        values.put(KEY_ROW_FAV_NAME_KEY_ROW, favPosition);
        values.put(KEY_ROW_CONTENT_ITEM_ROW_ID, contentPosition);
        return values;
    }

    private ContentValues createContentValueContentItem(Content mItem) {
        ContentValues values = new ContentValues();
        values.put(KEY_ROW_CONTENT_ITEM_INDEX, mItem.getIndex());
        values.put(KEY_ROW_CONTENT_ITEM_FILTER_TYPE, mItem.getFilterType());
        if (mItem.getFilterType() == FilterType.MULTIMEDIA) {
            MultimediaContent item = (MultimediaContent) mItem;
            values.put(MULTIMEDIA_FILE_EXT, item.getExtension());
            values.put(MULTIMEDIA_ABSOLUTE_PATH, item.getAbsolutePath());
            values.put(MULTIMEDIA_FILE_TYPE, item.getType());
            values.put(MULTIMEDIA_FILE_URL, item.getFileURL());
            values.put(MULTIMEDIA_TYPE, item.getImageType());
            values.put(MULTIMEDIA_CONTENT_NAME, item.getName());
            values.put(MULTIMEDIA_ROOT_ID, item.getRootID());
            values.put(MULTIMEDIA_IS_FAVORITE, item.isFavorite());
            values.put(MULTIMEDIA_CONTENT_ID, item.getId());
            values.put(MULTIMEDIA_PLAYLIST_ID, item.getPlaylistID());
            values.put(MULTIMEDIA_PLAYLIST_ARTIST, item.getArtist());
            values.put(MULTIMEDIA_PLAYLIST_TITLE, item.getTitle());
            values.put(MULTIMEDIA_PLAYLIST_DURATION, item.getDuration());
            values.put(MULTIMEDIA_PLAYLIST_RESOLUTION, item.getResolution());
            values.put(MULTIMEDIA_PLAYLIST_NAME, item.getName());
            values.put(MULTIMEDIA_PLAYLIST_TYPE, item.getPlaylistType());
        }
        return values;
    }

    /**
     * Returns raw index of content_list name in DB table favourite_list. If
     * name does not exist in DB table, it will be added.
     * 
     * @param name
     *        name of content list table e.g. (recently_list,
     *        favourite_list_sports, favourite_list_movies, etc.).
     * @return raw index of content_list name in DB table favourite_list.
     */
    private long createFavouriteList(String name) {
        /**
         * Gets raw index of content list name in DB table favourite_list.
         */
        int position = getFavouriteListPosition(name);
        /**
         * If content list name does not exist in DB table favourite_list, add
         * it and return raw index of position where added.
         */
        if (position == -1)
            return database.insert(A_DatabaseHelper.TABLE_NAME_FAVOURITE_LIST,
                    null, createContentValueFavouriteList(name));
        else {
            return position;
        }
    }

    /**
     * Returns raw index of content list (recently list, favorite list,...) in
     * DB table favourite_list.
     * 
     * @param name
     *        Name of list.
     * @return raw index o content list in DB table favourite_list.
     */
    private int getFavouriteListPosition(String name) {
        int returnValue = -1;
        Cursor cursor = database.rawQuery(GET_FAVOURITE_LIST_POSITION,
                new String[] { name });
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "size:" + cursor.getCount());
        }
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            returnValue = cursor.getInt(cursor
                    .getColumnIndex(KEY_ROW_FAV_NAME_KEY_ROW));
        }
        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public void insertIntoLockedContent(String name, int filterType, int index) {
        database.execSQL(INSERT_INTO_LOCKED_CONTENT, new String[] { name,
                "" + filterType, "" + index });
    }

    public void insertIntoRenamedInputContent(int index, String name) {
        database.execSQL(INSERT_INTO_RENAMED_CONTENT, new String[] {
                "" + index, name });
    }

    public void removeRenamedInputContent(int index) {
        database.execSQL(REMOVE_RENAMED_INPUT_FROM_CONTENT_LIST,
                new String[] { "" + index });
    }

    public Cursor getAllLockedContents() {
        Cursor cursor = database.rawQuery(
                SELECT_ALL_FROM_LOCKED_CONTENT_BY_FILTER_TYPE, new String[] {});
        return cursor;
    }

    public void removeLockedContent(String name, int filterType) {
        database.execSQL(REMOVE_LOCKED_CONTENT_FROM_CONTENT_LIST, new String[] {
                name, "" + filterType });
    }

    private ContentValues createContentValueFavouriteList(String name) {
        ContentValues values = new ContentValues();
        values.put(KEY_ROW_FAV_NAME, name);
        return values;
    }

    public void deleteDatabase() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "deleteDB");
        }
        try {
            IWEDIAService.getInstance().deleteDatabase("iwedia_database");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
