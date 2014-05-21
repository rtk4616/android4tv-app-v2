package com.iwedia.service.content;

import java.util.ArrayList;

import android.content.Intent;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.applications.ApplicationContent;
import com.iwedia.comm.enums.AppListType;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.application.ApplicationManager;

/**
 * This class manage Android applications shown in Apps tab of GUI ContentList.
 * 
 * @author Marko Zivanovic
 */
public class ContentFilterApps extends ContentFilter {
    /**
     * Debug log tag.
     */
    private final String LOG_TAG = "ContentFilterApps";
    /**
     * Reference of global list "recenltyWatchedList" in ContentListControl.
     * Used to add played Android application to recently watched list of GUI
     * application.
     */
    private ArrayList<Content> recenltyWatched;
    /**
     * This array list holds indexes of ApplicationContents in recenltyWatched
     * list. (This list is needed because recenltyWatched list hold all kind of
     * Contents).
     */
    private ArrayList<Integer> recentlyWatchedIndexes;

    /**
     * Default constructor.
     * 
     * @param manager
     *        ContentManager instance of global content manager to handle
     *        ContentFilters.
     * @param recenltyWatched
     *        Instance of global list that represent recently accessed content
     *        items.
     */
    public ContentFilterApps(ContentManager manager,
            ArrayList<Content> recenltyWatched) {
        this.recenltyWatched = recenltyWatched;
        this.FILTER_TYPE = com.iwedia.comm.enums.FilterType.APPS;
    }

    /**
     * Returns Android application at given index as instance of
     * ApplicationContent.
     * {@link com.iwedia.comm.content.applications.ApplicationContent}
     */
    @Override
    public Content getContent(int index) {
        return new ApplicationContent(index, ApplicationManager.getInstance()
                .getApplication(index));
    }

    /**
     * Starts given Android application given as ApplicationContent.
     * {@link com.iwedia.comm.content.applications.ApplicationContent}
     */
    @Override
    public int goContent(Content content, int displayId) {
        Intent launchIntent = IWEDIAService.getInstance().getPackageManager()
                .getLaunchIntentForPackage(content.getImage());
        IWEDIAService.getInstance().startActivity(launchIntent);
        return 0;
    }

    /**
     * Returns a number of Android applications.
     */
    @Override
    public int getContentListSize() {
        return ApplicationManager.getInstance().getSize(AppListType.CONTENT);
    }

    /**
     * Returns number of items in recently watched list.
     */
    @Override
    public int getRecenltyWatchedListSize() {
        recentlyWatchedIndexes = new ArrayList<Integer>();
        for (int i = 0; i < recenltyWatched.size(); i++)
            if (recenltyWatched.get(i).getFilterType() == FILTER_TYPE) {
                recentlyWatchedIndexes.add(i);
            }
        return recentlyWatchedIndexes.size();
    }

    /**
     * Returns recently watched item at given index.
     */
    @Override
    public Content getRecentlyWatchedItem(int index) {
        return recenltyWatched.get(recentlyWatchedIndexes.get(index));
    }

    /**
     * Return enum {@link com.iwedia.comm.enums.FilterType} of this
     * ContentFilter.
     */
    @Override
    public int toInt() {
        return FILTER_TYPE;
    }
}
