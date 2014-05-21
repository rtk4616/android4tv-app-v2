package com.iwedia.service.storage;

import java.util.ArrayList;

import com.iwedia.comm.content.Content;

public class FavouriteListController implements IController {
    private A_DbAdapter dbAdapter;

    public FavouriteListController(A_DbAdapter dbAdapter) {
        this.dbAdapter = dbAdapter;
    }

    @Override
    public long addContentToList(String name, Content content) {
        return dbAdapter.addContentToList(name, content);
    }

    @Override
    public void removeContentFromList(String name, Content content) {
        if (content == null) {
            dbAdapter.clearList(name);
        } else {
            dbAdapter.removeContentFromList(name, content);
        }
    }

    @Override
    public ArrayList<Content> getElementsInListByFilter(String name,
            int filterType) {
        return dbAdapter.getElementsInListByFilter(name, filterType);
    }

    @Override
    public ArrayList<Content> getElementsInList(String name) {
        return dbAdapter.getElementInList(name);
    }

    @Override
    public void deleteDatabase() {
    }
}
