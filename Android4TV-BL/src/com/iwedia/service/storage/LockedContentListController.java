package com.iwedia.service.storage;

import android.database.Cursor;

import com.iwedia.service.IWEDIAService;

public class LockedContentListController {
    private static LockedContentListController instance;
    private A_DbAdapter dbAdapter;

    public LockedContentListController() {
        this.dbAdapter = IWEDIAService.getInstance().getStorageManager()
                .getDbAdapter();
    }

    public long addContentToList(String name, int filterType, int index) {
        dbAdapter.insertIntoLockedContent(name, filterType, index);
        return -1;
    }

    public void removeContentFromList(String name, int filterType) {
        dbAdapter.removeLockedContent(name, filterType);
    }

    public Cursor getAllLockedContents() {
        return dbAdapter.getAllLockedContents();
    }

    public static LockedContentListController getInstance() {
        if (instance == null) {
            instance = new LockedContentListController();
        }
        return instance;
    }
}
