package com.iwedia.service.content;

import java.util.List;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dlna.MultimediaManager;
import com.iwedia.service.IWEDIAService;

public class ContentFilterPVRRecorded extends ContentFilter {
    private final String LOG_TAG = "ContentFilterPVRRecorded";

    public ContentFilterPVRRecorded() {
    }

    @Override
    public void reinitialize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "reinitialize");
        }
        try {
            MultimediaManager.getInstante().reinitialize();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void mediaEjected() {
        MultimediaManager.getInstante().mediaEjected();
    }

    @Override
    public Content getContent(int index) {
        return MultimediaManager.getInstante().getContent(index);
    }

    @Override
    public List<Content> getContentList(int startIndex, int endIndex) {
        return MultimediaManager.getInstante().getContentList(startIndex,
                endIndex);
    }

    @Override
    public int goContent(Content content, int displayId) {
        int returnValue = MultimediaManager.getInstante().goContent(content,
                displayId);
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "returnValue:" + returnValue);
        }
        return returnValue;
    }

    @Override
    public int getContentListSize() {
        return MultimediaManager.getInstante().getContentListSize();
    }

    @Override
    public boolean addContentToFavorites(Content content) {
        return MultimediaManager.getInstante().addContentToFavorites(content);
    }

    @Override
    public boolean removeContentFromFavorites(Content content) {
        return MultimediaManager.getInstante().removeContentFromFavorites(
                content);
    }

    @Override
    public boolean removeAllContentsFromFavorites(int filterType) {
        return MultimediaManager.getInstante().removeAllContentsFromFavorites(
                filterType);
    }

    @Override
    public int getFavoritesSize() {
        return MultimediaManager.getInstante().getFavoritesSize();
    }

    @Override
    public Content getFavoriteItem(int index) {
        return MultimediaManager.getInstante().getFavoriteItem(index);
    }

    @Override
    public int getRecenltyWatchedListSize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getRecenltyWatchedListSize");
        }
        return MultimediaManager.getInstante().getRecenltyWatchedListSize();
    }

    @Override
    public Content getRecentlyWatchedItem(int index) {
        return MultimediaManager.getInstante().getRecentlyWatchedItem(index);
    }

    @Override
    public void goPath(String path) {
        MultimediaManager.getInstante().goPath(path);
    }

    @Override
    public int getPathSize() {
        return MultimediaManager.getInstante().getPathSize();
    }

    @Override
    public Content getPath(int index) {
        return MultimediaManager.getInstante().getPath(index);
    }

    @Override
    public int toInt() {
        return FilterType.PVR_RECORDED;
    }
}
