package com.iwedia.service.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IChannelsCallback;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.content.applications.AppItem;
import com.iwedia.comm.content.applications.ApplicationContent;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.content.widgets.WidgetContent;
import com.iwedia.comm.content.widgets.WidgetItem;
import com.iwedia.comm.enums.AppListType;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.PlaybackDestinationType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.io.LastInputDescriptor;
import com.iwedia.dtv.service.Service;
import com.iwedia.dtv.service.ServiceDescriptor;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;
import com.iwedia.service.service.ServiceControl;
import com.iwedia.service.storage.A_DbAdapter;
import com.iwedia.service.storage.ControllerType;
import com.iwedia.service.storage.LockedContentListController;
import com.iwedia.service.system.application.ApplicationManager;
import com.iwedia.service.widget.WidgetManager;

/**
 * ContentListControl handles all GUI ContentList filters, and exposes API to
 * control them.
 * 
 * @author Marko Zivanovic
 */
public class ContentListControl extends IContentListControl.Stub implements
        IDTVInterface {
    private final String LOG_TAG = "ContentListControl";
    /**
     * Maximum size of recently watched list;
     */
    private final int MAX_RECENTLY_WATCHED_SIZE = 20;
    /**
     * Maximum number of service lists operator profile dedicated
     */
    private final int MAX_NUMBER_OF_SERVICE_LISTS = 12;
    /**
     * List of recently accessed Content items - used as global list for all
     * ContentFilters.
     */
    private ArrayList<Content> recenltyWatchedList;
    /**
     * ContentManager is used to handle ContentList filters - used as global
     * manager for all ContentFilters.
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
    private ContentManager contentManager;
    private IContentFilter multimediaFilter;
    private IContentFilter pvrRecordedFilter;
    private IContentFilter pvrScheduledFilter;
    /**
     * Currently active MW service (only broadcast services).;
     */
    private Content[] activeContent = new Content[2];
    /**
     * Previously active Content. Used for toggle option.
     */
    private Content[] previousContent = new Content[2];
    /**
     * IChannels callback.
     */
    private IChannelsCallback mChannelsCallbackManager;
    private int currentServiceIndex;
    private int currentServiceListIndex;
    private static HashMap<Integer, HashMap<String, Content>> lockedContent;
    private AudioManager audioManager;
    @SuppressWarnings("unused")
    private int SYSTEM_MAX_VOLUME;
    // private int MW_MAX_VOLUME = 100;
    // private double STEP = 2.86;
    private boolean guiVideoViewIPUri = false;
    private boolean isPlayerDeinit = true;
    // TODO: Applies on main display only
    private static final int mDisplayId = PlaybackDestinationType.MAIN_LIVE;
    private int ipDisplayId = 0;

    /**
     * Initialize Content list control.
     */
    public void initialize() {
        activeContent[0] = null;
        activeContent[1] = null;
        previousContent[0] = null;
        previousContent[1] = null;
        int numberOfFavoriteLists = 0;
        IWEDIAService.getInstance().getStorageManager()
                .setActiveController(ControllerType.RECENTLY_LIST);
        contentManager = new ContentManager();
        try {
            IContentListControl contentListControl = IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
            numberOfFavoriteLists = contentListControl
                    .getNumberOfServiceLists();
        } catch (RemoteException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        recenltyWatchedList = IWEDIAService
                .getInstance()
                .getStorageManager()
                .getElementsInList(
                        IWEDIAService.getInstance().getRecentlyListTableName());
        pvrRecordedFilter = new ContentFilterPVRRecorded();
        pvrScheduledFilter = new ContentFilterPVRScheduled();
        multimediaFilter = new ContentFilterMultimedia();
        contentManager.addContentFilter(FilterType.APPS, new ContentFilterApps(
                contentManager, recenltyWatchedList));
        contentManager.addContentFilter(FilterType.WIDGETS,
                new ContentFilterWidgets(contentManager, recenltyWatchedList));
        contentManager.addContentFilter(FilterType.INPUTS,
                new ContentFilterInputs(contentManager, recenltyWatchedList));
        contentManager
                .addContentFilter(FilterType.MULTIMEDIA, multimediaFilter);
        contentManager.addContentFilter(FilterType.IP_STREAM,
                new ContentFilterIP(contentManager, recenltyWatchedList));
        for (int i = 1; i < numberOfFavoriteLists; i++) {
            contentManager.addContentFilter((i), new ContentFilterServiceList(
                    contentManager, i, recenltyWatchedList));
        }
        /**
         * It is important that last added ContentFilter is ContentFilterAll
         * because function startVideoPlayback uses currently active filter that
         * now is ContentFilterAll.
         */
        contentManager.addContentFilter(FilterType.ALL, new ContentFilterAll(
                contentManager, recenltyWatchedList));
        contentManager.reinitialize();
        try {
            setActiveFilter(FilterType.ALL);
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        setActiveContent(mDisplayId);
        lockedContent = new HashMap<Integer, HashMap<String, Content>>();
        Cursor cursor = LockedContentListController.getInstance()
                .getAllLockedContents();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "cursor size:" + cursor.getCount());
            }
            AppItem item;
            WidgetItem widgetItem;
            WidgetContent wContent;
            ApplicationContent aContent;
            int size = ApplicationManager.getInstance().getSize(
                    AppListType.CONTENT);
            int sizeWidgets = WidgetManager.getInstance().getSize();
            IContentFilter cntFilter = contentManager
                    .getContentFilter(FilterType.INPUTS);
            Log.d(LOG_TAG, "cntFilter: " + cntFilter);
            while (!cursor.isAfterLast()) {
                String name = cursor
                        .getString(cursor
                                .getColumnIndex(A_DbAdapter.LOCKED_CONTENTS_COLUMN_NAME));
                int index = cursor
                        .getInt(cursor
                                .getColumnIndex(A_DbAdapter.LOCKED_CONTENTS_COLUMN_INDEX));
                int filterType = cursor
                        .getInt(cursor
                                .getColumnIndex(A_DbAdapter.LOCKED_CONTENTS_COLUMN_FILTER_TYPE));
                switch (filterType) {
                    case FilterType.APPS:
                        for (int i = 0; i < size; i++) {
                            ApplicationManager.getInstance().setAppType(
                                    AppListType.CONTENT);
                            item = ApplicationManager.getInstance()
                                    .getApplication(index);
                            if (item.getAppname().equals(name)) {
                                aContent = new ApplicationContent(index, item);
                                setLockStatus(aContent);
                                break;
                            }
                        }
                        break;
                    case FilterType.WIDGETS:
                        for (int j = 0; j < sizeWidgets; j++) {
                            widgetItem = WidgetManager.getInstance()
                                    .getWidgetItem(j);
                            wContent = new WidgetContent(j, widgetItem);
                            if (wContent.getName().equals(name)) {
                                setLockStatus(wContent);
                                break;
                            }
                        }
                        break;
                    case FilterType.INPUTS:
                        cntFilter = contentManager
                                .getContentFilter(FilterType.INPUTS);
                        if (cntFilter != null)
                            try {
                                if (index > 0) {
                                    index = index - 1;
                                }
                                Content content = cntFilter.getContent(index);
                                if (content != null) {
                                    setLockStatus(content);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        break;
                    default:
                        break;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        audioManager = (AudioManager) IWEDIAService.getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        SYSTEM_MAX_VOLUME = audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * This function is called when MW service scan has been finished, and it is
     * used to set all necessary fields to it's default value.
     */
    public void reinitialize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "reinitialize-start");
        }
        recenltyWatchedList.clear();
        contentManager.reinitialize();
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "reinitialize-end");
        }
    }

    /**
     * refresh service list is called when number of service lists is changed
     * and content manager filter list needs to updated
     */
    public void refreshServiceLists() {
        Log.d(LOG_TAG, "refreshServiceLists");
        try {
            IContentListControl contentListControl = IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
            int numberOfServiceLists = contentListControl
                    .getNumberOfServiceLists();
            int currentActiveFilter = getActiveFilterIndex();
            Log.d(LOG_TAG, "currentActiveFilter = " + currentActiveFilter);
            for (int i = 1; i <= MAX_NUMBER_OF_SERVICE_LISTS; i++) {
                if (contentManager != null)
                    contentManager.removeContentFilter(i);
            }
            for (int i = 1; i < numberOfServiceLists; i++) {
                Log.d(LOG_TAG, "Adding service filter:" + i);
                if (contentManager != null)
                    contentManager.addContentFilter((i),
                            new ContentFilterServiceList(contentManager, i,
                                    recenltyWatchedList));
            }
            if (contentManager != null) {
                Log.d(LOG_TAG, "currentActiveFilter = " + currentActiveFilter);
                if (getContentFilter(currentActiveFilter) != null) {
                    setActiveFilter(currentActiveFilter);
                } else {
                    /**
                     * Set current filter to ALL if previous does not exist
                     * (case of deleting current service list)
                     */
                    Log.d(LOG_TAG, "currentActiveFilter = ALL");
                    setActiveFilter(FilterType.ALL);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is called from proxy rebind
     */
    public void refreshActiveContent() {
        setActiveContent(0);
    }

    private void setActiveContent(int displayId) {
        try {
            int numberOfServices;
            try {
                LastInputDescriptor activeInput;
                boolean isInputConnected;
                Service lastService = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getServiceControl()
                        .getLastPlayedListAndService();
                activeInput = IWEDIAService.getInstance().getDTVManager()
                        .getInputOutputControl().getLastInput();
                // TODO: DISABLED UNTIL RESOLVED
                if ((activeInput.getType().getValue() >= 0)
                        && (activeInput.getType().getValue() < 0x08)
                        && (activeInput.getType().getValue() != 0x01)) {
                    ContentListControl control = (ContentListControl) IWEDIAService
                            .getInstance().getDtvManagerProxy()
                            .getContentListControl();
                    isInputConnected = IWEDIAService
                            .getInstance()
                            .getDtvManagerProxy()
                            .getInputOutputControl()
                            .ioGetDeviceConnected(
                                    activeInput.getType().getValue());
                    if (isInputConnected) {
                        IContentFilter cntFilter = control
                                .getContentFilter(FilterType.INPUTS);
                        if (cntFilter != null) {
                            Content iContent = cntFilter.getContent(activeInput
                                    .getType().getValue());
                            setActiveContent(iContent, displayId);
                        }
                        return;
                    }
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            Service activeService = IWEDIAService.getInstance()
                    .getDtvManagerProxy().getServiceControl()
                    .getActiveService();
            Log.e(LOG_TAG, "setActiveContent() activeService = "
                    + activeService);
            /**
             * Get number of services in MW favorite list.
             */
            numberOfServices = IWEDIAService.getInstance().getDTVManager()
                    .getServiceControl()
                    .getServiceListCount(ServiceListIndex.MASTER_LIST);
            Log.e(LOG_TAG, "setActiveContent() numberOfServices = "
                    + numberOfServices);
            currentServiceIndex = IWEDIAService.getInstance()
                    .getPreferenceManager().getInt("lastServiceIndex", 0);
            Log.e(LOG_TAG, "setActiveContent() currentServiceIndex = "
                    + currentServiceIndex);
            if (currentServiceListIndex == ServiceListIndex.IP_STREAMED) {
                int nbIpServices = 0;
                IContentFilter cntFilter = contentManager
                        .getContentFilter(FilterType.IP_STREAM);
                if (cntFilter != null) {
                    nbIpServices = cntFilter.getContentListSize();
                }
                if (cntFilter != null)
                    if (nbIpServices > currentServiceIndex) {
                        IpContent iContent = (IpContent) cntFilter
                                .getContent(currentServiceIndex + 1);
                        setActiveContent(iContent, displayId);
                        if (IWEDIAService.DEBUG)
                            Log.e(LOG_TAG, "active content IP:"
                                    + activeContent[displayId].toString());
                        cntFilter.goContent(iContent, displayId);
                    }
                return;
            }
            boolean isDummyService = false;
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "numberOfServices:" + numberOfServices);
            }
            if (numberOfServices != 0) {
                if (IWEDIAService.DEBUG)
                    Log.e(LOG_TAG, "currentServiceListIndex:"
                            + currentServiceListIndex);
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "currentServiceIndex:" + currentServiceIndex);
                }
                ServiceDescriptor serviceDesc = IWEDIAService
                        .getInstance()
                        .getDTVManager()
                        .getServiceControl()
                        .getServiceDescriptor(currentServiceListIndex,
                                currentServiceIndex);
                if (serviceDesc.getName().toLowerCase()
                        .equals("DummyIP_Service")
                        || serviceDesc.getName().length() == 0) {
                    isDummyService = true;
                }
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, serviceDesc.toString());
                }
                if (!isDummyService) {
                    activeContent[displayId] = new ServiceContent(serviceDesc,
                            currentServiceIndex, currentServiceListIndex);
                    if (IWEDIAService.DEBUG)
                        Log.e(LOG_TAG, "active content:"
                                + activeContent[displayId].toString());
                }
            }
            if (isDummyService || numberOfServices == 0) {
                IContentFilter cntFilter = contentManager
                        .getContentFilter(FilterType.IP_STREAM);
                if (cntFilter != null)
                    if (cntFilter.getContentListSize() != 0) {
                        int position = IWEDIAService.getInstance()
                                .getPreferenceManager()
                                .getInt("lastServiceIndex", 0);
                        activeContent[displayId] = cntFilter
                                .getContent(position);
                    } else {
                        activeContent[displayId] = null;
                    }
            }
            if (activeContent[displayId] != null)
                if (IWEDIAService.DEBUG) {
                    Log.e(LOG_TAG, "active Content:" + activeContent[displayId]);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isIpLastWatched = IWEDIAService.getInstance()
                .getPreferenceManager().getBoolean("ip_last_watched", false);
        Log.e(LOG_TAG, "isIpLastWatched:" + isIpLastWatched);
        if (isIpLastWatched) {
            ipDisplayId = IWEDIAService.getInstance().getPreferenceManager()
                    .getInt("ip_displayId", displayId);
            String url = IWEDIAService.getInstance().getPreferenceManager()
                    .getString("ip_url", "");
            if (!url.equals("")) {
                IContentFilter ipFilter = contentManager
                        .getContentFilter(FilterType.IP_STREAM);
                int numberOfIpServices;
                try {
                    numberOfIpServices = ipFilter.getContentListSize();
                    Content iContent;
                    for (int i = 0; i < numberOfIpServices; i++) {
                        iContent = ipFilter.getContent(i);
                        if (((IpContent) iContent).getUrl().equals(url)) {
                            Log.e(LOG_TAG, "found ip:" + iContent.toString());
                            activeContent[0] = iContent;
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sets currently active Content list filter by given parameter.
     * 
     * @param typeOfFilter
     *        One of the following {@link com.iwedia.comm.enums.FilterType}.
     */
    @Override
    public void setActiveFilter(int typeOfFilter) throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "setActiveFilter:" + typeOfFilter);
        }
        // Get IContentFilter object by passed Integer typeOfFilter.
        IContentFilter filter = contentManager.getContentFilter(typeOfFilter);
        if (contentManager != null) {
            if (filter != null) {
                contentManager.setActiveFilter(filter);
            }
        }
    }

    /**
     * Returns Content item by given index of currently active filter. Currently
     * active filter can be one of the following:
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
     * com.iwedia.service.content.ContentFilterWidgets}. This method does not
     * create Content item, but only calls corresponding method of currently
     * active filter that creates and returns Content item.
     */
    @Override
    public Content getContent(int index) throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContent:" + index);
        }
        // return new ServiceContent("content: " + index);
        return contentManager.getActiveFilter().getContent(index);
    }

    /**
     * Returns extended information (now, next, frequency,...) of Content item
     * by given index of currently active filter. Currently active filter can be
     * one of the following: {@link com.iwedia.service.content.ContentFilterAll,
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
     * com.iwedia.service.content.ContentFilterWidgets}. This method does not
     * create Content item, but only calls corresponding method of currently
     * active filter that creates and returns Content item.
     */
    @Override
    public Content getContentExtendedInfo() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentExtendedInfo()");
        }
        Log.e(LOG_TAG, "getContentExtendedInfo() - activeContent: "
                + activeContent.toString());
        Log.e(LOG_TAG, "getContentExtendedInfo() - activeContentFilter: "
                + activeContent[mDisplayId].getFilterType());
        IContentFilter cntFilter = contentManager
                .getContentFilter(activeContent[mDisplayId].getFilterType());
        if (cntFilter != null) {
            return cntFilter.getContentExtendedInfo();
        } else {
            return null;
        }
    }

    /**
     * Returns list of Content items by given range of indexes of currently
     * active filter. Currently active filter can be one of the following:
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
     * com.iwedia.service.content.ContentFilterWidgets}. This method does not
     * create list of Content items, but only calls corresponding method of
     * currently active filter that creates and returns list.
     */
    @Override
    public List<Content> getContentList(int startIndex, int endIndex)
            throws RemoteException {
        return contentManager.getActiveFilter().getContentList(startIndex,
                endIndex);
    }

    /**
     * This method does not directly handle given Content, but based on field
     * filterType of given Content, calls corresponding method of currently
     * active filter to handle given Content item. Content filter type can be
     * one of the following: {@link com.iwedia.comm.enums.FilterType,
     * com.iwedia.comm.content.Content} .
     */
    @Override
    public int goContent(Content content, int displayId) throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "goContent:" + content.toString());
        }
        currentServiceIndex = content.getIndex();
        currentServiceListIndex = content.getServiceListIndex();
        IContentFilter cntFilter = contentManager.getContentFilter(content
                .getFilterType());
        if (cntFilter != null) {
            return cntFilter.goContent(content, displayId);
        } else {
            return 0;
        }
    }

    /**
     * Opens a content by given index, e.g. runs Android application, plays
     * Radio channel.
     */
    @Override
    public int goContentByIndex(int index, int displayId)
            throws RemoteException {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG,
                    "goContentByIndex:" + index
                            + "contentManager.getActiveFilter():"
                            + contentManager.getActiveFilter());
        if (index >= 1) {
            int iIndex = contentManager.getActiveFilter().goContentByIndex(
                    index, displayId);
            currentServiceIndex = iIndex;
            return iIndex;
        }
        return -1;
    }

    /**
     * Opens previously played content.
     */
    @Override
    public int togglePreviousContent(int displayId) throws RemoteException {
        if (previousContent[displayId] != null) {
            return goContent(previousContent[displayId], displayId);
        }
        return -1;
    }

    /**
     * Stops content playback.
     */
    @Override
    public int stopContent(Content content, int displayId)
            throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "stopContent:" + content.toString());
            Log.e(LOG_TAG, "content.getFilterType():" + content.getFilterType());
        }
        IContentFilter cntFilter = contentManager.getContentFilter(content
                .getFilterType());
        if (cntFilter != null) {
            Log.e(LOG_TAG, "cntFilter:" + cntFilter);
            return cntFilter.stopContent(displayId);
        } else {
            Log.e(LOG_TAG, "cntFilter - null");
            return 0;
        }
    }

    /**
     * Add given content to favorite list.
     * 
     * @param Content
     *        to add to favorite list. {@link com.iwedia.comm.content.Content}.
     */
    @Override
    public boolean addContentToFavorites(Content content)
            throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "addContentToFavouriteList");
        }
        return contentManager.getActiveFilter().addContentToFavorites(content);
    }

    /**
     * Remove the content from favorite list.
     * 
     * @param Content
     *        to remove. {@link com.iwedia.comm.content.Content}.
     */
    @Override
    public boolean removeContentFromFavorites(Content content)
            throws RemoteException {
        return contentManager.getActiveFilter().removeContentFromFavorites(
                content);
    }

    /**
     * Returns the number of items in favorite list of currently active filter.
     */
    @Override
    public int getFavoritesSize() throws RemoteException {
        try {
            return contentManager.getActiveFilter().getFavoritesSize();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Returns the item of favorite list of currently active filter by given
     * index.
     * 
     * @param index
     *        index of Content to return.
     * @return Content item {@link com.iwedia.comm.content.Content}.
     */
    @Override
    public Content getFavoriteItem(int index) throws RemoteException {
        return contentManager.getActiveFilter().getFavoriteItem(index);
    }

    /**
     * Returns the number of items in recently watched list of currently active
     * filter.
     */
    @Override
    public int getRecenltyWatchedListSize() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getRecenltyWatchedListSize");
        }
        try {
            return contentManager.getActiveFilter()
                    .getRecenltyWatchedListSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Returns the item of recently watched list of currently active filter by
     * given index.
     * 
     * @param index
     *        of item to return.
     * @return Content {@link com.iwedia.comm.content.Content}.
     */
    @Override
    public Content getRecentlyWatchedItem(int index) throws RemoteException {
        return contentManager.getActiveFilter().getRecentlyWatchedItem(index);
    }

    /**
     * Called when GUI application starts. This function starts last watched TV
     * service.
     */
    @Override
    public boolean startVideoPlayback() throws RemoteException {
        // float systemVolume = audioManager
        // .getStreamVolume(AudioManager.STREAM_MUSIC);
        // double mwVolume = 0;
        //
        // if (systemVolume == SYSTEM_MAX_VOLUME) {
        // mwVolume = MW_MAX_VOLUME;
        // } else {
        // mwVolume = (systemVolume * STEP);
        // }
        //
        // IWEDIAService
        // .getInstance()
        // .getDTVManager()
        // .getAudioControl()
        // .setCurrentVolume(
        // IWEDIAService.getInstance().getDtvManagerProxy()
        // .getLiveRouteID(), mwVolume);
        if (activeContent[mDisplayId] != null) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "start video playback:"
                        + activeContent[mDisplayId].toString());
            }
            int decoderID = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getDecoderID(mDisplayId);
            if (activeContent[mDisplayId].getFilterType() != FilterType.IP_STREAM) {
                switch (activeContent[mDisplayId].getSourceType()) {
                    case SAT:
                        IWEDIAService
                                .getInstance()
                                .getDtvManagerProxy()
                                .setCurrentLiveRoute(
                                        IWEDIAService.getInstance()
                                                .getDtvManagerProxy()
                                                .getLiveRouteIDSat(decoderID));
                        break;
                    case TER: {
                        IWEDIAService
                                .getInstance()
                                .getDtvManagerProxy()
                                .setCurrentLiveRoute(
                                        IWEDIAService.getInstance()
                                                .getDtvManagerProxy()
                                                .getLiveRouteIDTer(decoderID));
                        break;
                    }
                    case CAB:
                        IWEDIAService
                                .getInstance()
                                .getDtvManagerProxy()
                                .setCurrentLiveRoute(
                                        IWEDIAService.getInstance()
                                                .getDtvManagerProxy()
                                                .getLiveRouteIDCab(decoderID));
                        break;
                    case ANALOG:
                        IWEDIAService
                                .getInstance()
                                .getDtvManagerProxy()
                                .setCurrentLiveRoute(
                                        IWEDIAService.getInstance()
                                                .getDtvManagerProxy()
                                                .getLiveRouteIDAtv(decoderID));
                        break;
                }
                isPlayerDeinit = false;
                // ZORANA C 3.0 MERGE - play active channel
                try {
                    IWEDIAService.getInstance().getDtvManagerProxy()
                            .getContentListControl()
                            .goContent(activeContent[mDisplayId], 0);
                    addContentToRecentlyWatchedList();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return true;
            } else if (activeContent[mDisplayId].getFilterType() == FilterType.IP_STREAM) {
                IpContent iContent = (IpContent) activeContent[mDisplayId];
                try {
                    contentManager.getContentFilter(FilterType.IP_STREAM)
                            .goContent(iContent, decoderID);
                    // IWEDIAService
                    // .getInstance()
                    // .getDTVManager()
                    // .getServiceControl()
                    // .zapURL(IWEDIAService.getInstance()
                    // .getDtvManagerProxy()
                    // .getIpRouteID(decoderID), iContent.getUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Called when GUI application is closing. This function stops video
     * playback.
     */
    @Override
    public void stopVideoPlayback() throws RemoteException {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "stopVideoPlayback");
        (IWEDIAService.getInstance().getDtvManagerProxy().getTeletextControl())
                .deselectCurrentTeletextTrack();
        (IWEDIAService.getInstance().getDtvManagerProxy().getMhegControl())
                .hide();
        isPlayerDeinit = true;
        Content activeContent = getActiveContent(0);
        if (activeContent != null) {
            stopContent(activeContent, 0);
        }
        activeContent = getActiveContent(1);
        if (activeContent != null) {
            stopContent(activeContent, 1);
        }
    }

    /**
     * Adds Content to the recently watched list by given content index.
     * 
     * @param index
     * @return
     */
    public void addContentToRecentlyWatchedList() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "addContentToRecentlyWatchedList"
                    + activeContent[mDisplayId].toString());
            Log.e(LOG_TAG,
                    "lastWatched:"
                            + activeContent[mDisplayId].getIndexInMasterList());
            Log.e(LOG_TAG,
                    "lastServiceListIndex:"
                            + activeContent[mDisplayId].getServiceListIndex());
            Log.e(LOG_TAG,
                    "lastServiceIndex:" + activeContent[mDisplayId].getIndex());
        }
        Editor edit = IWEDIAService.getInstance().getPreferenceManager().edit();
        edit.putInt("lastWatched",
                activeContent[mDisplayId].getIndexInMasterList());
        edit.putInt("lastServiceListIndex",
                activeContent[mDisplayId].getServiceListIndex());
        edit.putInt("lastServiceIndex", activeContent[mDisplayId].getIndex());
        edit.commit();
        addItemToRecentlyWatchedList(activeContent[mDisplayId]);
    }

    /**
     * Adds Content to recently watched list and saves index and
     * serviceListIndex of last watched channel to Shared Preferences.
     * 
     * @param content
     *        - Content that needs to be added.
     *        {@link com.iwedia.comm.content.Content}.
     */
    public void addItemToRecentlyWatchedList(Content content) {
        recenltyWatchedList.add(0, content);
        /**
         * Remove Content item if already exists in application recentlyWatched
         * temporary list, and also in DB.
         */
        try {
            removeContentFromRecentlyList(content);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (recenltyWatchedList.size() == MAX_RECENTLY_WATCHED_SIZE) {
            recenltyWatchedList.remove(MAX_RECENTLY_WATCHED_SIZE - 1);
        }
        /**
         * Add content to DB content list recently_watched so can be restored
         * after restart.
         */
        IWEDIAService
                .getInstance()
                .getStorageManager()
                .addContentToList(
                        IWEDIAService.getInstance().getRecentlyListTableName(),
                        content);
    }

    /**
     * Returns PVR recorded filter.
     * 
     * @return IContentFilter {@link com.iwedia.service.content.IContentFilter}
     */
    public IContentFilter getPvrRecordedFilter() {
        return pvrRecordedFilter;
    }

    /**
     * Returns PVR scheduled filter.
     * 
     * @return IContentFilter {@link com.iwedia.service.content.IContentFilter}
     */
    public IContentFilter getPvrScheduledFilter() {
        return pvrScheduledFilter;
    }

    /**
     * Returns recently watched list.
     */
    public ArrayList<Content> getRecenltyWatchedList() {
        return recenltyWatchedList;
    }

    /**
     * Returns contentManager.
     * 
     * @return ContentManager {@link com.iwedia.service.content.ContentManager}.
     */
    public ContentManager getContentManager() {
        return contentManager;
    }

    /**
     * Returns number of items in list of all items of currently active filter.
     */
    @Override
    public int getContentListSize() throws RemoteException {
        return contentManager.getActiveFilter().getContentListSize();
    }

    /**
     * Sets absolute path while browsing multimedia.
     */
    @Override
    public void goPath(String path) throws RemoteException {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG,
                    "goPath-active filter:" + contentManager.getActiveFilter());
        contentManager.getActiveFilter().goPath(path);
    }

    /**
     * Returns size of recently accessed folders list. Recently accessed folders
     * list represent absolute path while browsing multimedia.
     */
    @Override
    public int getPathSize() throws RemoteException {
        return contentManager.getActiveFilter().getPathSize();
    }

    /**
     * Returns item of recently accessed folders list. Recently accessed folders
     * list represent absolute path while browsing multimedia.
     */
    @Override
    public Content getPath(int index) throws RemoteException {
        return contentManager.getActiveFilter().getPath(index);
    }

    @Override
    public IContentFilter getActiveContentFilter() throws RemoteException {
        return contentManager.getActiveFilter();
    }

    /**
     * Returns index of currently active filter
     * {@link com.iwedia.comm.enums.FilterType}.
     */
    @Override
    public int getActiveFilterIndex() throws RemoteException {
        if (contentManager != null) {
            return contentManager.getActiveFilter().toInt();
        } else {
            return 0;
        }
    }

    /**
     * Removes given Content item from recently list.
     * 
     * @return true if Content item was successfully removed, else - item does
     *         not exit in list.
     */
    @Override
    public boolean removeContentFromRecentlyList(Content content)
            throws RemoteException {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG,
                    "remove content from recently list:" + content.getName());
        boolean state = false;
        IWEDIAService.getInstance().getStorageManager()
                .setActiveController(ControllerType.RECENTLY_LIST);
        for (int i = 1; i < recenltyWatchedList.size(); i++) {
            if (IWEDIAService.DEBUG)
                Log.e(LOG_TAG, "recenltyWatchedList.get(i).getName():"
                        + recenltyWatchedList.get(i).getName());
            /**
             * Remove content item from recenltyWatchedList, and DB.
             */
            if (recenltyWatchedList.get(i).getName().equals(content.getName())) {
                recenltyWatchedList.remove(i);
                try {
                    IWEDIAService
                            .getInstance()
                            .getStorageManager()
                            .removeContentFromList(
                                    IWEDIAService.getInstance()
                                            .getRecentlyListTableName(),
                                    content);
                    state = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return state;
    }

    /**
     * Returns currently active MW service as Content item.
     * 
     * @return currently active content.
     */
    @Override
    public Content getActiveContent(int displayId) throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getActiveContent");
        }
        if (activeContent[displayId] != null)
            Log.e(LOG_TAG,
                    "getActiveContent:" + activeContent[displayId].toString()
                            + " display ID:" + displayId + " filter: "
                            + +activeContent[displayId].getFilterType());
        if (activeContent[displayId] != null) {
            return activeContent[displayId];
        } else {
            return null;
        }
    }

    /**
     * Sets currently active Content.
     * 
     * @param activeContent
     *        new Content to be set.
     */
    @Override
    public void setActiveContent(Content newContent, int displayId) {
        if ((displayId != 0)
                || ((displayId == 0) && (this.activeContent[displayId] != null))) {
            previousContent[displayId] = this.activeContent[displayId];
        }
        this.activeContent[displayId] = newContent;
        if (IWEDIAService.DEBUG)
            if (activeContent[displayId] != null)
                Log.e(LOG_TAG,
                        "setActiveContent:"
                                + activeContent[displayId].toString()
                                + " filter: "
                                + +activeContent[displayId].getFilterType()
                                + " display ID:" + displayId);
    }

    @Override
    public void registerCallback(IChannelsCallback channelsCallback)
            throws RemoteException {
        mChannelsCallbackManager = channelsCallback;
    }

    /**
     * Returns channels callback for zappingCallback, nowNextChanged,
     * epgEventsChanged.
     * 
     * @return {@link IChannelsCallback}
     */
    public IChannelsCallback getChannelsCallbackManager() {
        return mChannelsCallbackManager;
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO
    }

    /**
     * Returns number of content items in ContentFilter by filter type.
     */
    @Override
    public int getContentFilterListSize(int filterType) throws RemoteException {
        IContentFilter cntFilter = contentManager.getContentFilter(filterType);
        if (cntFilter != null) {
            return cntFilter.getContentListSize();
        } else {
            return 0;
        }
    }

    /**
     * Return index in ContentFilterAll list of given content.
     */
    @Override
    public int getContentIndexInAllList(Content content) throws RemoteException {
        if (content != null && content.getIndex() >= 0) {
            ContentFilterAll cntFilter = (ContentFilterAll) contentManager
                    .getContentFilter(FilterType.ALL);
            if (cntFilter != null) {
                return cntFilter.getContentIndexInAllList(content);
            } else {
                return 0;
            }
        }
        return 0;
    }

    public void setCurrentServiceListIndex(int currentServiceListIndex) {
        this.currentServiceListIndex = currentServiceListIndex;
    }

    public int getCurrentServiceIndex() {
        return currentServiceIndex;
    }

    public void setCurrentServiceIndex(int currentServiceIndex) {
        this.currentServiceIndex = currentServiceIndex;
    }

    public int getCurrentServiceListIndex() {
        return currentServiceListIndex;
    }

    /**
     * Used to lock and unlock content.
     */
    @Override
    public boolean setContentLockStatus(Content content, boolean status)
            throws RemoteException {
        HashMap<String, Content> lockedContentByFilter = lockedContent
                .get(content.getFilterType());
        if (lockedContentByFilter == null) {
            if (status == false) {
                return true;
            }
            // hash map with content.getFilterType() key doesn't exist.
            HashMap<String, Content> tmp = new HashMap<String, Content>();
            tmp.put(content.toString(), content);
            lockedContent.put(content.getFilterType(), tmp);
            LockedContentListController.getInstance().addContentToList(
                    content.getName(), content.getFilterType(),
                    content.getIndex());
            return true;
        }
        // hash map with key content.getFilterType already exists.
        if (lockedContentByFilter.get(content.toString()) == null) {
            // Not in list.
            if (status == true) {
                HashMap<String, Content> mapContent = lockedContent.get(content
                        .getFilterType());
                if (mapContent != null) {
                    mapContent.put(content.toString(), content);
                }
                LockedContentListController.getInstance().addContentToList(
                        content.getName(), content.getFilterType(),
                        content.getIndex());
            }
            return true;
        } else {
            // Already in list.
            if (status) {
                return false;
            } else {
                // Remove item from list.
                HashMap<String, Content> mapContent = lockedContent.get(content
                        .getFilterType());
                if (mapContent != null) {
                    mapContent.remove(content.toString());
                }
                LockedContentListController.getInstance()
                        .removeContentFromList(content.getName(),
                                content.getFilterType());
                return true;
            }
        }
    }

    private void setLockStatus(Content content) {
        HashMap<String, Content> lockedContentByFilter = lockedContent
                .get(content.getFilterType());
        if (lockedContentByFilter == null) {
            HashMap<String, Content> tmp = new HashMap<String, Content>();
            tmp.put(content.toString(), content);
            lockedContent.put(content.getFilterType(), tmp);
        } else {
            if (lockedContentByFilter.get(content.toString()) == null) {
                // hash map with key content.getFilterType already exists.
                // Not in list.
                HashMap<String, Content> mapContent = lockedContent.get(content
                        .getFilterType());
                if (mapContent != null) {
                    mapContent.put(content.toString(), content);
                }
            }
        }
    }

    /**
     * Returns content locked status. True if content is locked, otherwise
     * false.
     */
    @Override
    public boolean getContentLockedStatus(Content content)
            throws RemoteException {
        HashMap<String, Content> lockedContentByFilter = lockedContent
                .get(content.getFilterType());
        if (lockedContentByFilter == null) {
            return false;
        }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, content.toString());
        }
        // hash map with key content.getFilterType already exists.
        if (lockedContentByFilter.get(content.toString()) == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public ServiceDescriptor getServiceByIndexInMasterList(int index,
            boolean getNowNext) throws RemoteException {
        ServiceDescriptor serviceDesc = null;
        try {
            serviceDesc = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getServiceControl()
                    .getServiceDescriptor(ServiceListIndex.MASTER_LIST, index);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return serviceDesc;
    }

    @Override
    public Content getPreviousContent() throws RemoteException {
        return previousContent[mDisplayId];
    }

    public boolean isGuiVideoViewIPUri() {
        return guiVideoViewIPUri;
    }

    public void setGuiVideoViewIPUri(boolean guiVideoViewIPUri) {
        this.guiVideoViewIPUri = guiVideoViewIPUri;
    }

    @Override
    public Content getContentByIndexInMasterList(int index)
            throws RemoteException {
        Content content;
        for (int i = 0; i < getContentListSize(); i++) {
            content = getContent(i);
            if (content.getIndexInMasterList() == index) {
                return content;
            }
        }
        return null;
    }

    @Override
    public IContentFilter getContentFilter(int filterType) {
        if (contentManager != null) {
            return contentManager.getContentFilter(filterType);
        } else {
            return null;
        }
    }

    public boolean isPlayerDeinit() {
        return isPlayerDeinit;
    }

    @Override
    public boolean removeAllContentsFromFavorites(int filterType)
            throws RemoteException {
        return contentManager.getActiveFilter().removeAllContentsFromFavorites(
                filterType);
    }

    @Override
    public String getContentListName() throws RemoteException {
        Log.d(LOG_TAG, "getContentListName");
        return null;
    }

    @Override
    public int getNumberOfServiceLists() throws RemoteException {
        Log.d(LOG_TAG, "getNumberOfServiceLists");
        int size = IWEDIAService.getInstance().getDtvManagerProxy()
                .getServiceControl().getNumberOfServiceLists();
        Log.d(LOG_TAG, "getNumberOfServiceLists returns: " + size);
        return size;
    }

    @Override
    public Content getContentExtendedInfoByIndex(int index)
            throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentExtendedInfoByIndex(): " + index);
        }
        return contentManager.getActiveFilter().getContentExtendedInfoByIndex(
                index);
    }

    @Override
    public boolean addContentToFavoriteList(int favListIndex, Content content)
            throws RemoteException {
        IWEDIAService.getInstance().getDtvManagerProxy()
                .getContentListControl().getContentFilter(favListIndex)
                .addContentToFavoriteList(content);
        // IWEDIAService
        // .getInstance()
        // .getDtvManagerProxy()
        // .getServiceListControl()
        // .addServiceToFavouriteList(ServiceListIndex.FAVORITE + favListIndex,
        // content.getIndexInMasterList());
        return false;
    }

    @Override
    public boolean removeContentFromFavoritesList(int favListIndex,
            Content content) throws RemoteException {
        /*
         * IWEDIAService .getInstance() .getDtvManagerProxy()
         * .getServiceListControl()
         * .deleteServiceFromFavouriteList(ServiceListIndex.FAVORITE +
         * favListIndex, content.getIndexInMasterList());
         */
        return IWEDIAService.getInstance().getDtvManagerProxy()
                .getContentListControl().getActiveContentFilter()
                .removeContentFromFavoriteList(content);
    }

    @Override
    public Content getContentVisible(int index) throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentVisible:" + index);
        }
        return contentManager.getActiveFilter().getContentVisible(index);
    }

    @Override
    public int getContentListSizeVisible() throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentListSizeVisible");
        }
        return contentManager.getActiveFilter().getContentListSizeVisible();
    }

    @Override
    public boolean renameContent(Content content, String name)
            throws RemoteException {
        if (IWEDIAService.DEBUG)
            Log.d(LOG_TAG, "renameContent: " + content.toString() + "to "
                    + name);
        IContentFilter cntFilter = contentManager.getContentFilter(content
                .getFilterType());
        if (cntFilter != null) {
            cntFilter.renameContent(content, name);
        }
        return true;
    }
}