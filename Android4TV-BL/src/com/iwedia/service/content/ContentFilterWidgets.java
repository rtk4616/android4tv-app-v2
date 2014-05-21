package com.iwedia.service.content;

import java.util.ArrayList;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.widgets.WidgetContent;
import com.iwedia.service.widget.WidgetManager;

/**
 * Content filter used to manage Android system widgets. {@link WidgetContent}.
 * 
 * @author Marko Zivanovic
 */
public class ContentFilterWidgets extends ContentFilter {
    /** The log tag. */
    private final String LOG_TAG = "ContentFilterWidgets";
    /**
     * Reference of global list "recenltyWatchedList" in ContentListControl.
     * Used to add played WidgetContent to recently watched list of GUI
     * application.
     */
    private ArrayList<Content> recenltyWatched;
    /**
     * This array list holds indexes of WidgetContents in recenltyWatched list.
     * (This list is needed because recenltyWatched list hold all kind of
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
    public ContentFilterWidgets(ContentManager manager,
            ArrayList<Content> recenltyWatched) {
        /**
         * Save reference of recentlyWatched list as local value.
         */
        this.recenltyWatched = recenltyWatched;
        this.FILTER_TYPE = com.iwedia.comm.enums.FilterType.WIDGETS;
    }

    /**
     * Returns WidgetContent at given index.
     */
    @Override
    public Content getContent(int index) {
        return new WidgetContent(index, WidgetManager.getInstance()
                .getWidgetItem(index));
    }

    /**
     * Returns number of widgets.
     */
    @Override
    public int getContentListSize() {
        return WidgetManager.getInstance().getSize();
    }

    /**
     * Returns number of items in recently list.
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
     * Returns Content item from recently list by given index.
     * 
     * @return WidgetContent.
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
