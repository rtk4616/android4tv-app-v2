package com.iwedia.service.content;

import java.util.HashMap;
import java.util.Map;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.service.IWEDIAService;

/**
 * ContentManager is used to handle ContentList filters.
 * {@link com.iwedia.service.content.ContentFilterAll,
 * com.iwedia.service.content.ContentFilterApps,
 * com.iwedia.service.content.ContentFilterData,
 * com.iwedia.service.content.ContentFilterDVB_C,
 * com.iwedia.service.content.ContentFilterDVB_S,
 * com.iwedia.service.content.ContentFilterDVB_T,
 * com.iwedia.service.content.ContentFilterInputs,
 * com.iwedia.service.content.ContentFilterIP,
 * com.iwedia.service.content.ContentFilterMultimedia,
 * com.iwedia.service.content.ContentFilterPVRRecorded,
 * com.iwedia.service.content.ContentFilterPVRScheduled,
 * com.iwedia.service.content.ContentFilterRadio,
 * com.iwedia.service.content.ContentFilterWidgets}.
 * 
 * @author Marko Zivanovic
 */
public class ContentManager {
    private final String LOG_TAG = "ContentManager";
    private ContentListControl cControl;
    /**
     * Hash map of content filters. Hash map key represent filterType
     * {@link com.iwedia.comm.enums.FilterType}. The value of hash map represent
     * content filter, one of the following:
     * {@link com.iwedia.service.content.ContentFilterAll,
     * com.iwedia.service.content.ContentFilterApps,
     * com.iwedia.service.content.ContentFilterData,
     * com.iwedia.service.content.ContentFilterDVB_C,
     * com.iwedia.service.content.ContentFilterDVB_S,
     * com.iwedia.service.content.ContentFilterDVB_T,
     * com.iwedia.service.content.ContentFilterInputs,
     * com.iwedia.service.content.ContentFilterIP,
     * com.iwedia.service.content.ContentFilterMultimedia,
     * com.iwedia.service.content.ContentFilterPVRRecorded,
     * com.iwedia.service.content.ContentFilterPVRScheduled,
     * com.iwedia.service.content.ContentFilterRadio,
     * com.iwedia.service.content.ContentFilterWidgets}.
     */
    private HashMap<Integer, IContentFilter> filters;
    /**
     * Currently active filter. One of the following:
     * {@link com.iwedia.service.content.ContentFilterAll,
     * com.iwedia.service.content.ContentFilterApps,
     * com.iwedia.service.content.ContentFilterData,
     * com.iwedia.service.content.ContentFilterDVB_C,
     * com.iwedia.service.content.ContentFilterDVB_S,
     * com.iwedia.service.content.ContentFilterDVB_T,
     * com.iwedia.service.content.ContentFilterInputs,
     * com.iwedia.service.content.ContentFilterIP,
     * com.iwedia.service.content.ContentFilterMultimedia,
     * com.iwedia.service.content.ContentFilterPVRRecorded,
     * com.iwedia.service.content.ContentFilterPVRScheduled,
     * com.iwedia.service.content.ContentFilterRadio,
     * com.iwedia.service.content.ContentFilterWidgets}.
     */
    private IContentFilter activeFilter;

    /**
     * Default constructor. Initialize necessary fields.
     */
    public ContentManager() {
        filters = new HashMap<Integer, IContentFilter>();
        try {
            cControl = ((ContentListControl) IWEDIAService.getInstance()
                    .getDtvManagerProxy().getContentListControl());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called in ContentListControl when MW service scan has
     * been finished to reinitialize all necessary fields of ContentFilters
     * after scan {@link com.iwedia.service.content.ContentFilterAll,
     * com.iwedia.service.content.ContentFilterApps,
     * com.iwedia.service.content.ContentFilterData,
     * com.iwedia.service.content.ContentFilterDVB_C,
     * com.iwedia.service.content.ContentFilterDVB_S,
     * com.iwedia.service.content.ContentFilterDVB_T,
     * com.iwedia.service.content.ContentFilterInputs,
     * com.iwedia.service.content.ContentFilterIP,
     * com.iwedia.service.content.ContentFilterMultimedia,
     * com.iwedia.service.content.ContentFilterPVRRecorded,
     * com.iwedia.service.content.ContentFilterPVRScheduled,
     * com.iwedia.service.content.ContentFilterRadio,
     * com.iwedia.service.content.ContentFilterWidgets}.
     */
    public void reinitialize() {
        try {
            for (Map.Entry<Integer, IContentFilter> entry : filters.entrySet()) {
                IContentFilter singleFilter = entry.getValue();
                if (singleFilter != null) {
                    if (IWEDIAService.DEBUG)
                        Log.e(LOG_TAG,
                                "singleFilter.toInt():" + singleFilter.toInt());
                    if (singleFilter.toInt() != FilterType.ALL) {
                        singleFilter.reinitialize();
                    }
                }
            }
            filters.get(FilterType.ALL).reinitialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns Content filter by given filterType.
     * 
     * @param filterType
     *        one of the following: {@link com.iwedia.comm.enums.FilterType} .
     *        Used as hash key to get specific filter. .
     * @return IContentFilter one of the following:
     *         {@link com.iwedia.service.content.ContentFilterAll,
     *         com.iwedia.service.content.ContentFilterApps,
     *         com.iwedia.service.content.ContentFilterData,
     *         com.iwedia.service.content.ContentFilterDVB_C,
     *         com.iwedia.service.content.ContentFilterDVB_S,
     *         com.iwedia.service.content.ContentFilterDVB_T,
     *         com.iwedia.service.content.ContentFilterInputs,
     *         com.iwedia.service.content.ContentFilterIP,
     *         com.iwedia.service.content.ContentFilterMultimedia,
     *         com.iwedia.service.content.ContentFilterPVRRecorded,
     *         com.iwedia.service.content.ContentFilterPVRScheduled,
     *         com.iwedia.service.content.ContentFilterRadio,
     *         com.iwedia.service.content.ContentFilterWidgets}.
     */
    public IContentFilter getContentFilter(int filterType) {
        /**
         * Get specific filter by filterType.
         */
        IContentFilter filter = filters.get(filterType);
        /**
         * If filter was not already initialized, initialize it and add it to
         * filters hash map.
         */
        if (filter == null)
            switch (filterType) {
                case FilterType.APPS: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: APPS");
                    }
                    addContentFilter(FilterType.APPS,
                            new ContentFilterApps(cControl.getContentManager(),
                                    cControl.getRecenltyWatchedList()));
                    break;
                }
                case FilterType.INPUTS: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: INPUTS");
                    }
                    addContentFilter(
                            FilterType.INPUTS,
                            new ContentFilterInputs(cControl
                                    .getContentManager(), cControl
                                    .getRecenltyWatchedList()));
                    break;
                }
                case FilterType.WIDGETS: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: WIDGETS");
                    }
                    addContentFilter(
                            FilterType.WIDGETS,
                            new ContentFilterWidgets(cControl
                                    .getContentManager(), cControl
                                    .getRecenltyWatchedList()));
                    break;
                }
                case FilterType.MULTIMEDIA: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: MULTIMEDIA");
                    }
                    addContentFilter(FilterType.MULTIMEDIA,
                            new ContentFilterMultimedia());
                    break;
                }
                case FilterType.PVR_RECORDED: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: PVR_RECORDED");
                    }
                    addContentFilter(FilterType.PVR_RECORDED,
                            cControl.getPvrRecordedFilter());
                    break;
                }
                case FilterType.PVR_SCHEDULED: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: PVR_SCHEDULED");
                    }
                    addContentFilter(FilterType.PVR_SCHEDULED,
                            cControl.getPvrScheduledFilter());
                    break;
                }
                case FilterType.IP_STREAM: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: IP_STREAM");
                    }
                    addContentFilter(FilterType.IP_STREAM,
                            new ContentFilterIP(cControl.getContentManager(),
                                    cControl.getRecenltyWatchedList()));
                    break;
                }
                default: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "getContentFilter-null: FAVORITES");
                    }
                    // TODO: filter type offset! odakle da ga vucem!
                    addContentFilter(
                            filterType,
                            new ContentFilterServiceList(cControl
                                    .getContentManager(), filterType, cControl
                                    .getRecenltyWatchedList()));
                    break;
                }
            }
        filter = filters.get(filterType);
        return filter;
    }

    /**
     * Adds new ContentFilter to hash map of available filters.
     * 
     * @param filterType
     *        one of the following: {@link com.iwedia.comm.enums.FilterType} .
     *        filterType is used as hash key to get specific filter.
     * @param contentFilter
     *        one of the following:
     *        {@link com.iwedia.service.content.ContentFilterAll,
     *        com.iwedia.service.content.ContentFilterApps,
     *        com.iwedia.service.content.ContentFilterData,
     *        com.iwedia.service.content.ContentFilterDVB_C,
     *        com.iwedia.service.content.ContentFilterDVB_S,
     *        com.iwedia.service.content.ContentFilterDVB_T,
     *        com.iwedia.service.content.ContentFilterInputs,
     *        com.iwedia.service.content.ContentFilterIP,
     *        com.iwedia.service.content.ContentFilterMultimedia,
     *        com.iwedia.service.content.ContentFilterPVRRecorded,
     *        com.iwedia.service.content.ContentFilterPVRScheduled,
     *        com.iwedia.service.content.ContentFilterRadio,
     *        com.iwedia.service.content.ContentFilterWidgets}.
     */
    public void addContentFilter(int filterType, IContentFilter contentFilter) {
        filters.put(filterType, contentFilter);
        this.activeFilter = contentFilter;
    }

    /**
     * Removes ContentFilter from hash map of available filters.
     * 
     * @param filterType
     *        one of the following: {@link com.iwedia.comm.enums.FilterType} .
     *        filterType is used as hash key to get specific filter.
     * @return Removed filter if found, otherwise null
     */
    public IContentFilter removeContentFilter(int filterType) {
        if (this.activeFilter.equals(filters.get(filterType))) {
            Log.d(LOG_TAG,
                    "removeContentFilter - trying to remove active filter");
            this.activeFilter = filters.get(FilterType.ALL);
        }
        return filters.remove(filterType);
    }

    /**
     * Sets currently active filter.
     * 
     * @param filter
     *        one of the following:
     *        {@link com.iwedia.service.content.ContentFilterAll,
     *        com.iwedia.service.content.ContentFilterApps,
     *        com.iwedia.service.content.ContentFilterData,
     *        com.iwedia.service.content.ContentFilterDVB_C,
     *        com.iwedia.service.content.ContentFilterDVB_S,
     *        com.iwedia.service.content.ContentFilterDVB_T,
     *        com.iwedia.service.content.ContentFilterInputs,
     *        com.iwedia.service.content.ContentFilterIP,
     *        com.iwedia.service.content.ContentFilterMultimedia,
     *        com.iwedia.service.content.ContentFilterPVRRecorded,
     *        com.iwedia.service.content.ContentFilterPVRScheduled,
     *        com.iwedia.service.content.ContentFilterRadio,
     *        com.iwedia.service.content.ContentFilterWidgets}.
     */
    public void setActiveFilter(IContentFilter filter) {
        this.activeFilter = filter;
    }

    /**
     * Returns currently active filter.
     * 
     * @return one of the following:
     *         {@link com.iwedia.service.content.ContentFilterAll,
     *         com.iwedia.service.content.ContentFilterApps,
     *         com.iwedia.service.content.ContentFilterData,
     *         com.iwedia.service.content.ContentFilterDVB_C,
     *         com.iwedia.service.content.ContentFilterDVB_S,
     *         com.iwedia.service.content.ContentFilterDVB_T,
     *         com.iwedia.service.content.ContentFilterInputs,
     *         com.iwedia.service.content.ContentFilterIP,
     *         com.iwedia.service.content.ContentFilterMultimedia,
     *         com.iwedia.service.content.ContentFilterPVRRecorded,
     *         com.iwedia.service.content.ContentFilterPVRScheduled,
     *         com.iwedia.service.content.ContentFilterRadio,
     *         com.iwedia.service.content.ContentFilterWidgets}.
     */
    public IContentFilter getActiveFilter() {
        return activeFilter;
    }
}
