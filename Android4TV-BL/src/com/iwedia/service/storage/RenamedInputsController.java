package com.iwedia.service.storage;

import com.iwedia.service.IWEDIAService;

public class RenamedInputsController {
    private static RenamedInputsController instance;
    private A_DbAdapter dbAdapter;

    public RenamedInputsController() {
        this.dbAdapter = IWEDIAService.getInstance().getStorageManager()
                .getDbAdapter();
    }

    public long addContentToList(int index, String name) {
        dbAdapter.insertIntoRenamedInputContent(index, name);
        return -1;
    }

    public void removeContentFromList(int index) {
        dbAdapter.removeRenamedInputContent(index);
    }

    public String getInputContent(int index) {
        return dbAdapter.getNameForInputContent(index);
    }

    public static RenamedInputsController getInstance() {
        if (instance == null) {
            instance = new RenamedInputsController();
        }
        return instance;
    }
}
