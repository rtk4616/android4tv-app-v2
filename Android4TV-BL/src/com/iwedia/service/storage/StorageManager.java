package com.iwedia.service.storage;

import java.util.ArrayList;

import com.iwedia.comm.content.Content;

public class StorageManager implements IStorageManager, IController {
    private ControllerManager controllerManager;
    private A_DbAdapter dbAdapter;

    /**
     * Default constructor. Initiates opening of database.
     */
    public StorageManager() {
        dbAdapter = new A_DbAdapter();
        dbAdapter.open();
        controllerManager = new ControllerManager(dbAdapter);
    }

    /**
     * Close database.
     */
    public void closeDatabase() {
        dbAdapter.close();
    }

    /**
     * Sets active controller, e.g. (RecentlyListController,
     * FavoriteListController, etc.).
     */
    @Override
    public void setActiveController(int controller) {
        controllerManager.setActiveController(controller);
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
    @Override
    public long addContentToList(String name, Content content) {
        return controllerManager.getActiveController().addContentToList(name,
                content);
    }

    @Override
    public void removeContentFromList(String name, Content content) {
        controllerManager.getActiveController().removeContentFromList(name,
                content);
    }

    @Override
    public ArrayList<Content> getElementsInListByFilter(String name,
            int filterType) {
        return controllerManager.getActiveController()
                .getElementsInListByFilter(name, filterType);
    }

    @Override
    public ArrayList<Content> getElementsInList(String name) {
        return controllerManager.getActiveController().getElementsInList(name);
    }

    /**
     * Deletes IWEDIA database.
     */
    @Override
    public void deleteDatabase() {
        dbAdapter.deleteDatabase();
    }

    public A_DbAdapter getDbAdapter() {
        return dbAdapter;
    }
}
