package com.iwedia.service.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class A_DatabaseHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = "A_DatabaseHelper";
    private static final String DATABASE_NAME = "iwedia_database";
    private static int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_FAVOURITE_LIST = "favourite_list";
    public static final String TABLE_NAME_CONTENT_ITEM = "content_item";
    public static final String TABLE_NAME_FAV_LIST_TO_ITEM = "list_to_item";
    private final String TABLE_NAME_LOCKED_CONTENTS = "content_lock";
    private final String TABLE_NAME_RENAMED_CONTENTS = "renamed_content";
    private final String DATABASE_CREATE_FAVOURITE_LIST = "create table "
            + TABLE_NAME_FAVOURITE_LIST
            + " (list_id integer, name text, primary key (list_id));";
    private final String DATABASE_CREATE_CONTENT_ITEM = "create table "
            + TABLE_NAME_CONTENT_ITEM
            + " (item_id integer, s_index integer, filter_type integer, mm_file_url text, mm_file_ext text, mm_file_type text, mm_type text, mm_absolute_path text, content_name text, mm_id text, mm_dlna_name text, mm_dlna_root_id text, mm_dlna_is_favorite integer, mm_playlist_id integer, mm_playlist_artist text, mm_playlist_title text, mm_playlist_duration integer, mm_playlist_resolution text, mm_playlist_name text, mm_playlist_type text, primary key (item_id));";
    private final String DATABASE_CREATE_FAV_LIST_TO_ITEM = "create table "
            + TABLE_NAME_FAV_LIST_TO_ITEM
            + " (list_id integer not null, item_id integer not null, PRIMARY KEY(list_id, item_id), FOREIGN KEY(list_id) REFERENCES "
            + TABLE_NAME_FAVOURITE_LIST
            + "(list_id) ON DELETE RESTRICT,FOREIGN KEY(item_id) REFERENCES "
            + TABLE_NAME_CONTENT_ITEM + "(item_id) ON DELETE RESTRICT);";
    private final String DATABASE_CREATE_LOCKED_CONTENT = "create table "
            + TABLE_NAME_LOCKED_CONTENTS
            + " (_id integer, filter_type integer, name text unique, _index integer);";
    private final String DATABASE_CREATE_RENAMED_CONTENT = "create table "
            + TABLE_NAME_RENAMED_CONTENTS
            + " (_id integer, _input_index integer, new_name text unique);";

    public A_DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_FAVOURITE_LIST);
        database.execSQL(DATABASE_CREATE_CONTENT_ITEM);
        database.execSQL(DATABASE_CREATE_FAV_LIST_TO_ITEM);
        database.execSQL(DATABASE_CREATE_LOCKED_CONTENT);
        database.execSQL(DATABASE_CREATE_RENAMED_CONTENT);
    }

    // Method is called during an upgrade of the database, e.g. if you increase
    // the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion) {
        Log.e(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
    }
}
