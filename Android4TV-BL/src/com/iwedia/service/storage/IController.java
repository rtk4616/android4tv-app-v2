package com.iwedia.service.storage;

import java.util.ArrayList;

import com.iwedia.comm.content.Content;

public interface IController {
    /**
     * Interface function used to add content to specific list.
     * 
     * @param name
     *        - Name of list (e.g. Recently list, Favorite list,...).
     * @param content
     *        - Content to add.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long addContentToList(String name, Content content);

    /**
     * Interface function used to remove Content item from specific list.
     * 
     * @param name
     *        - Name of list (e.g. Recently list, Favorite list,...).
     * @param content
     *        - Content to add.
     */
    public void removeContentFromList(String name, Content content);

    /**
     * Interface functions used to gain all element in specific list by filter
     * type.
     * 
     * @param name
     *        - Name of list (e.g. Recently list, Favorite list,...).
     * @param filterType
     *        {@link com.iwedia.comm.enum.FilterType}.
     * @return ArrayList of Content items
     *         {@link com.iwedia.comm.content.Content}.
     */
    public ArrayList<Content> getElementsInListByFilter(String name,
            int filterType);

    /**
     * Interface function used to gain all elements in specific list.
     * 
     * @param name
     *        - Name of list (e.g. Recently list, Favorite list,...).
     * @return ArrayList of Content items
     *         {@link com.iwedia.comm.content.Content}.
     */
    public ArrayList<Content> getElementsInList(String name);

    /**
     * Deletes IWEDIA database.
     */
    public void deleteDatabase();
}
