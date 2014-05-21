package com.iwedia.service.content;

import java.io.File;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.multimedia.PlaylistFile;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dlna.MultimediaManager;
import com.iwedia.service.IWEDIAService;

/**
 * Content filter used to manage multimedia contents. {@link MultimediaContent};
 * 
 * @author Marko Zivanovic.
 */
public class ContentFilterMultimedia extends ContentFilter {
    /**
     * Debug log tag.
     */
    private final String LOG_TAG = "ContentFilterMultimedia";

    /**
     * Default constructor.
     */
    public ContentFilterMultimedia() {
        this.FILTER_TYPE = FilterType.MULTIMEDIA;
        /**
         * Initialize native libraries and register handler.
         */
        Thread startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (MultimediaManager.getInstante().initNative() == 0) {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "Initialization FAILED!");
                    }
                }
                if (MultimediaManager.getInstante().registerEntry(
                        MultimediaManager.FILE_LOCAL) == 0) {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "Register Entry FAILED!");
                    }
                }
                // if (MultimediaManager.getInstante().registerEntry(
                // MultimediaManager.FILE_DLNA) == 0) {
                // if (IWEDIAService.DEBUG)
                // Log.e(LOG_TAG, "Register Entry FAILED!dlna");
                // }
                //
                // if (IWEDIAService.DEBUG)
                // Log.e(LOG_TAG, "*********after FILE_DLNA()");
                MultimediaManager.getInstante().init();
                goContent(new MultimediaContent("", null, null, null, null, -1,
                        "/", "", "", "", 0), 0);
            }
        });
        startThread.start();
    }

    /**
     * This function is called when the MW scan is completed.
     */
    @Override
    public void reinitialize() {
        try {
            MultimediaManager.getInstante().reinitialize();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Returns MultimediaContent at given index {@link MultimediaContent}.
     */
    @Override
    public Content getContent(int index) {
        return MultimediaManager.getInstante().getContent(index);
    }

    /**
     * Returns a list of MultimediaContent at given range.
     * {@link MultimediaContent}
     * 
     * @param startIndex
     *        first index of range.
     * @param endIndex
     *        last index of range.
     */
    @Override
    public List<Content> getContentList(int startIndex, int endIndex) {
        return MultimediaManager.getInstante().getContentList(startIndex,
                endIndex);
    }

    /**
     * Plays given MultimediaContent.
     * 
     * @param content
     *        MultimediaContent to be played.{@link MultimediaContent}
     */
    @Override
    public int goContent(Content content, int displayId) {
        MultimediaContent mContent = (MultimediaContent) content;
        if (mContent.getExtension() != null)
            if (mContent.getExtension().equals("apk")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String path = mContent.getAbsolutePath().replace("local:///",
                        "/");
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "install new application:" + path);
                }
                intent.setDataAndType(Uri.fromFile(new File(path)),
                        "application/vnd.android.package-archive");
                IWEDIAService.getInstance().startActivity(intent);
            } else {
                int returnValue = MultimediaManager.getInstante().goContent(
                        content, displayId);
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "returnValue:" + returnValue);
                }
                return returnValue;
            }
        return 0;
    }

    /**
     * Plays MultimediaContent at given index.
     */
    @Override
    public int goContentByIndex(int intex, int displayId) {
        return 0;
    }

    /**
     * Returns number of multimedia contents.
     */
    @Override
    public int getContentListSize() {
        return MultimediaManager.getInstante().getContentListSize();
    }

    /**
     * Adds given content to favorite list.
     * 
     * @param MultimediaContent
     *        to be added. {@link MultimediaContent}
     */
    @Override
    public boolean addContentToFavorites(Content content) {
        return MultimediaManager.getInstante().addContentToFavorites(content);
    }

    /**
     * Removes given content from favorite list.
     * 
     * @param MultimediaContent
     *        to be removed. {@link MultimediaContent}
     */
    @Override
    public boolean removeContentFromFavorites(Content content) {
        return MultimediaManager.getInstante().removeContentFromFavorites(
                content);
    }

    /**
     * Returns number of items in favorite list.
     */
    @Override
    public int getFavoritesSize() {
        return MultimediaManager.getInstante().getFavoritesSize();
    }

    /**
     * Returns MultimediaContent from favorite list at given index.
     * {@link MultimediaContent}
     */
    @Override
    public Content getFavoriteItem(int index) {
        return MultimediaManager.getInstante().getFavoriteItem(index);
    }

    /**
     * Returns number of items in recently list.
     */
    @Override
    public int getRecenltyWatchedListSize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getRecenltyWatchedListSize");
        }
        return MultimediaManager.getInstante().getRecenltyWatchedListSize();
    }

    /**
     * Returns MultimediaContent from recently list at given index.
     * {@link MultimediaContent}
     */
    @Override
    public Content getRecentlyWatchedItem(int index) {
        return MultimediaManager.getInstante().getRecentlyWatchedItem(index);
    }

    /**
     * Sets absolute path while browsing multimedia.
     */
    @Override
    public void goPath(String path) {
        MultimediaManager.getInstante().goPath(path);
    }

    /**
     * Returns number of items in Path list. Path list represent history list
     * (absolute path) while user is browsing multimedia content.
     */
    @Override
    public int getPathSize() {
        return MultimediaManager.getInstante().getPathSize();
    }

    /**
     * Returns MultimediaContent item at given index of absolute path.
     * {@link MultimediaContent}.
     */
    @Override
    public Content getPath(int index) {
        return MultimediaManager.getInstante().getPath(index);
    }

    /**
     * This content filter does not have associated MW service list.
     */
    @Override
    public int getServiceListIndex() {
        return -1;
    }

    /**
     * Return enum {@link com.iwedia.comm.enums.FilterType} of this
     * ContentFilter.
     */
    @Override
    public int toInt() {
        return FILTER_TYPE;
    }

    @Override
    public boolean createPlaylist(String playlistName, String playlistType) {
        return MultimediaManager.getInstante().getPlaylistManager()
                .createPlaylist(playlistName, playlistType);
    }

    @Override
    public boolean openPlaylist(String playlistName) {
        return MultimediaManager.getInstante().getPlaylistManager()
                .openPlaylist(playlistName);
    }

    @Override
    public boolean addAudioItemToPlaylist(String playlistName, String artist,
            String title, int duration, String URI) {
        return MultimediaManager
                .getInstante()
                .getPlaylistManager()
                .addAudioItemToPlaylist(playlistName, artist, title, duration,
                        URI);
    }

    @Override
    public boolean addVideoItemToPlaylist(String playlistName, String title,
            int duration, String URI) {
        return MultimediaManager.getInstante().getPlaylistManager()
                .addVideoItemToPlaylist(playlistName, title, duration, URI);
    }

    @Override
    public boolean addImageItemToPlaylist(String playlistName, String title,
            String resolution, String URI) {
        return MultimediaManager.getInstante().getPlaylistManager()
                .addImageItemToPlaylist(playlistName, title, resolution, URI);
    }

    @Override
    public void removeItemFromPlaylist(Content content, String playlistName,
            String URI) {
        MultimediaManager.getInstante().removeItemFromPlaylist(content,
                playlistName, URI);
    }

    @Override
    public boolean sortPlaylist(String playlistName, String criteria) {
        return MultimediaManager.getInstante().sortPlaylist(playlistName,
                criteria);
    }

    @Override
    public void clearPlaylist(String playlistName) {
        MultimediaManager.getInstante().clearPlaylist(playlistName);
    }

    @Override
    public void deletePlaylist(Content content, String playlistName) {
        MultimediaManager.getInstante().deletePlaylist(content, playlistName);
    }

    @Override
    public List<PlaylistFile> getPlaylists() {
        return MultimediaManager.getInstante().getPlaylistManager()
                .getPlaylists();
    }

    @Override
    public List<MultimediaContent> getPlaylistItems(String playlistName) {
        return MultimediaManager.getInstante().getPlaylistManager()
                .getPlaylistItems(playlistName);
    }
}
