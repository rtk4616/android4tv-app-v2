package com.iwedia.service.content;

import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.multimedia.PlaylistFile;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.storage.ControllerType;

// TODO: Move all filter common stuff and functionality up!
public abstract class ContentFilter extends IContentFilter.Stub {
    /**
     * Debug log tag.
     */
    private final String LOG_TAG = "ContentFilter";
    /**
     * List of favorite ServiceContents.
     */
    protected ArrayList<Content> favouriteList;
    /**
     * Filter type associated to ContentFilter. {@link FilterType}
     */
    protected int FILTER_TYPE;

    @Override
    public boolean addContentToFavoriteList(Content content)
            throws RemoteException {
        Log.d(LOG_TAG, "addContentToFavoriteList");
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeContentFromFavoriteList(Content arg0)
            throws RemoteException {
        Log.d(LOG_TAG, "removeContentFromFavoriteList");
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Adds given content to Content filter favorites.
     * 
     * @param Content
     * @return true if given content is successfully added to favorites, other
     *         wise false - content exists in list.
     */
    @Override
    public boolean addContentToFavorites(Content content)
            throws RemoteException {
        Log.d(LOG_TAG, "addContentToFavorites");
        for (int i = 0, tmpSize = favouriteList.size(); i < tmpSize; i++)
            if (favouriteList.get(i).equals(content)) {
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "favourite item already in list");
                }
                return false;
            }
        /**
         * Add new Content to favorites.
         */
        favouriteList.add(content);
        /**
         * Also add given Content to DB favorites.
         */
        IWEDIAService.getInstance().getStorageManager()
                .setActiveController(ControllerType.FAVOURITE_LIST);
        IWEDIAService
                .getInstance()
                .getStorageManager()
                .addContentToList(
                        IWEDIAService.getInstance().getFavoriteListTableName(),
                        content);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "favourite item added to list");
        }
        return true;
    }

    @Override
    public Content getContent(int index) throws RemoteException {
        Log.d(LOG_TAG, "getContent");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content getContentExtendedInfo() throws RemoteException {
        Log.d(LOG_TAG, "getContentExtendedInfo");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Content> getContentList(int start, int end)
            throws RemoteException {
        Log.d(LOG_TAG, "getContentList");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getContentListName() throws RemoteException {
        Log.d(LOG_TAG, "getContentListName");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getContentListSize() throws RemoteException {
        Log.d(LOG_TAG, "getContentListSize");
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Returns Content item in content filter favorite list by given index.
     * 
     * @return Content from favorites
     */
    @Override
    public Content getFavoriteItem(int index) {
        return favouriteList.get(index);
    }

    /**
     * Returns number of elements in favorites.
     */
    @Override
    public int getFavoritesSize() throws RemoteException {
        Log.d(LOG_TAG, "getFavoritesSize");
        return favouriteList.size();
    }

    @Override
    public Content getPath(int arg0) throws RemoteException {
        Log.d(LOG_TAG, "getPath");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPathSize() throws RemoteException {
        Log.d(LOG_TAG, "getPathSize");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRecenltyWatchedListSize() throws RemoteException {
        Log.d(LOG_TAG, "getRecenltyWatchedListSize");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Content getRecentlyWatchedItem(int arg0) throws RemoteException {
        Log.d(LOG_TAG, "getRecentlyWatchedItem");
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getServiceListIndex() throws RemoteException {
        Log.d(LOG_TAG, "getServiceList");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int goContent(Content arg0, int arg1) throws RemoteException {
        Log.d(LOG_TAG, "goContent");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int goContentByIndex(int arg0, int arg1) throws RemoteException {
        Log.d(LOG_TAG, "goContentByIndex");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void goPath(String path) throws RemoteException {
        Log.d(LOG_TAG, "goPath");
        // TODO Auto-generated method stub
    }

    /**
     * Reinitialize all necessary fields. Called on MW scanFinished() callback.
     */
    @Override
    public void reinitialize() throws RemoteException {
        Log.d(LOG_TAG, "reinitialize");
        /**
         * Initially fill favorite list from DB.
         */
        IWEDIAService.getInstance().getStorageManager()
                .setActiveController(ControllerType.FAVOURITE_LIST);
        this.favouriteList = IWEDIAService
                .getInstance()
                .getStorageManager()
                .getElementsInListByFilter(
                        IWEDIAService.getInstance().getFavoriteListTableName(),
                        this.FILTER_TYPE);
    }

    @Override
    public boolean removeAllContentsFromFavorites(int index)
            throws RemoteException {
        Log.d(LOG_TAG, "removeAllContentsFromFavorites");
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Removes given content from content filter favorite list.
     * 
     * @param ServiceContent
     *        to be removed
     * @return true if given content is successfully removed from favorites,
     *         other wise false - content exists in list.
     */
    @Override
    public boolean removeContentFromFavorites(Content content)
            throws RemoteException {
        Log.d(LOG_TAG, "removeContentFromFavorites");
        for (int i = 0, tmpSize = favouriteList.size(); i < tmpSize; i++)
            if (favouriteList.get(i).equals(content)) {
                favouriteList.remove(i);
                IWEDIAService.getInstance().getStorageManager()
                        .setActiveController(ControllerType.FAVOURITE_LIST);
                IWEDIAService
                        .getInstance()
                        .getStorageManager()
                        .removeContentFromList(
                                IWEDIAService.getInstance()
                                        .getFavoriteListTableName(), content);
                return true;
            }
        return false;
    }

    @Override
    public int stopContent(int index) throws RemoteException {
        Log.d(LOG_TAG, "stopContent");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int toInt() throws RemoteException {
        Log.d(LOG_TAG, "toInt");
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Content getContentExtendedInfoByIndex(int arg0)
            throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Content getContentVisible(int index) throws RemoteException {
        Log.d(LOG_TAG, "getContentVisibe");
        return getContent(index);
    }

    @Override
    public int getContentListSizeVisible() throws RemoteException {
        Log.d(LOG_TAG, "getContentListSizeVisible");
        return getContentListSize();
    }

    @Override
    public int renameContent(Content content, String name)
            throws RemoteException {
        Log.d(LOG_TAG, "renameContent");
        return 0;
    }

    @Override
    public boolean createPlaylist(String playlistName, String playlistType)
            throws RemoteException {
        return false;
    }

    @Override
    public boolean openPlaylist(String playlistName) throws RemoteException {
        return false;
    }

    @Override
    public boolean addAudioItemToPlaylist(String playlistName, String artist,
            String title, int duration, String URI) throws RemoteException {
        return false;
    }

    @Override
    public boolean addVideoItemToPlaylist(String playlistName, String title,
            int duration, String URI) throws RemoteException {
        return false;
    }

    @Override
    public boolean addImageItemToPlaylist(String playlistName, String title,
            String resolution, String URI) throws RemoteException {
        return false;
    }

    @Override
    public void removeItemFromPlaylist(Content content, String playlistName,
            String URI) throws RemoteException {
    }

    @Override
    public boolean sortPlaylist(String playlistName, String criteria)
            throws RemoteException {
        return false;
    }

    @Override
    public void clearPlaylist(String playlistName) throws RemoteException {
    }

    @Override
    public void deletePlaylist(Content content, String playlistName)
            throws RemoteException {
    }

    @Override
    public List<PlaylistFile> getPlaylists() throws RemoteException {
        return null;
    }

    @Override
    public List<MultimediaContent> getPlaylistItems(String playlistName)
            throws RemoteException {
        return null;
    }
}
