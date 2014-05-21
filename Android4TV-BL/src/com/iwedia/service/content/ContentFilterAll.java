package com.iwedia.service.content;

import java.util.ArrayList;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentFilter;
import com.iwedia.comm.content.inputs.InputContent;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.service.IWEDIAService;

/**
 * This class represent content filter all of GUI application. All Content items
 * shown in ContentFilterAll are collected from following ContentFilters:
 * com.iwedia.service.content.ContentFilterApps,
 * com.iwedia.service.content.ContentFilterData,
 * com.iwedia.service.content.ContentFilterDVB_C,
 * com.iwedia.service.content.ContentFilterDVB_S,
 * com.iwedia.service.content.ContentFilterDVB_T,
 * com.iwedia.service.content.ContentFilterIP,
 * com.iwedia.service.content.ContentFilterRadio,
 * com.iwedia.service.content.ContentFilterWidgets}.
 * 
 * @author Marko Zivanovic
 */
public class ContentFilterAll extends ContentFilter {
    private final String LOG_TAG = "ContentFilterAll";
    private final int FILTER_TYPE = FilterType.ALL;
    /**
     * ContentManager is used to handle ContentFilters.
     */
    private ContentManager contentManager;
    /**
     * List of recently accessed content items.
     */
    private ArrayList<Content> recenltyWatched;
    /**
     * Array list of favorite items.
     */
    private ArrayList<Content> favoriteList;
    private IContentFilter cntFilter;
    private int numberOfServicesInMasterList = 0;
    private int numberOfIpServices = 0;
    private int numberOfInputs = 0;
    private int numberOfWidgets = 0;
    private int numberOfApps = 0;
    private int mDisplayId = 0;
    private boolean isDummyFound = false;

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
    public ContentFilterAll(ContentManager manager,
            ArrayList<Content> recenltyWatched) {
        this.contentManager = manager;
        this.recenltyWatched = recenltyWatched;
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when MW service scan has finished.
     */
    @Override
    public void reinitialize() {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "reinitialize");
        }
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize all necessary fields.
     */
    private void init() throws RemoteException {
        numberOfServicesInMasterList = IWEDIAService.getInstance()
                .getDTVManager().getServiceControl().getServiceListCount(0);
        if (isDummyFound(IWEDIAService.getInstance().getDTVManager()
                .getServiceControl().getServiceDescriptor(0, 0).getName())
                && numberOfServicesInMasterList != 0) {
            numberOfServicesInMasterList -= 1;
            isDummyFound = true;
        }
        favoriteList = new ArrayList<Content>();
        cntFilter = contentManager.getContentFilter(FilterType.IP_STREAM);
        if (cntFilter != null) {
            numberOfIpServices = cntFilter.getContentListSize();
        }
        // ZORANA C 3.0 MERGE - Must count inputs
        cntFilter = contentManager.getContentFilter(FilterType.INPUTS);
        if (cntFilter != null) {
            numberOfInputs = cntFilter.getContentListSize();
        }
    }

    /**
     * Returns Content item of "All list" of GUI content filter all by given
     * index.
     */
    @Override
    public Content getContent(int index) {
        try {
            return returnContent(index, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Content getContentExtendedInfo() {
        try {
            return returnContent(IWEDIAService.getInstance()
                    .getDtvManagerProxy().getContentListControl()
                    .getActiveContent(mDisplayId).getIndex(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Content returnContent(int index, boolean extendedContent)
            throws RemoteException {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContent:" + index);
        }
        cntFilter = contentManager.getContentFilter(FilterType.APPS);
        if (cntFilter != null)
            numberOfApps = cntFilter.getContentListSize();
        cntFilter = contentManager.getContentFilter(FilterType.WIDGETS);
        if (cntFilter != null)
            numberOfWidgets = cntFilter.getContentListSize();
        if (index < numberOfServicesInMasterList) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "RETURNING CONTENT FROM MW MASTER SERVICE LIST");
            }
            return new ServiceContent(index, IWEDIAService.getInstance()
                    .getDtvManagerProxy().getServiceControl(), 0);
        } else if (index < numberOfServicesInMasterList + numberOfIpServices) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "RETURNING IP CONTENT");
            }
            cntFilter = contentManager.getContentFilter(FilterType.IP_STREAM);
            if (cntFilter != null) {
                if (!extendedContent) {
                    return cntFilter.getContent(index
                            - numberOfServicesInMasterList);
                }
                return cntFilter.getContentExtendedInfo();
            }
        } else if (index < numberOfServicesInMasterList + numberOfIpServices
                + numberOfInputs) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "RETURNING INPUT CONTENT");
            }
            cntFilter = contentManager.getContentFilter(FilterType.INPUTS);
            if (cntFilter != null) {
                if (!extendedContent) {
                    return cntFilter
                            .getContent(index
                                    - (numberOfServicesInMasterList + numberOfIpServices));
                }
                return cntFilter.getContentExtendedInfoByIndex(index
                        - (numberOfServicesInMasterList + numberOfIpServices));
            }
        } else if (index < numberOfServicesInMasterList + numberOfIpServices
                + numberOfInputs + numberOfApps) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "RETURNING APPLICATION CONTENT");
            }
            cntFilter = contentManager.getContentFilter(FilterType.APPS);
            if (cntFilter != null) {
                return cntFilter
                        .getContent(index
                                - (numberOfServicesInMasterList
                                        + numberOfIpServices + numberOfInputs));
            }
        } else if (index < numberOfServicesInMasterList + numberOfIpServices
                + numberOfInputs + numberOfApps + numberOfWidgets) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "RETURNING WIDGET CONTENT");
            }
            cntFilter = contentManager.getContentFilter(FilterType.WIDGETS);
            if (cntFilter != null) {
                return cntFilter.getContent(index
                        - (numberOfServicesInMasterList + numberOfIpServices
                                + numberOfInputs + numberOfApps));
            }
        }
        return null;
    }

    /**
     * This method does not directly handle given Content, but only calls
     * corresponding method of currently active filter to handle given Content
     * item. If currently active filter is one of the following:
     * {@link com.iwedia.service.content.ContentFilterData,
     * com.iwedia.service.content.ContentFilterDVB_C,
     * com.iwedia.service.content.ContentFilterDVB_S,
     * com.iwedia.service.content.ContentFilterDVB_T,
     * com.iwedia.service.content.ContentFilterIP,
     * com.iwedia.service.content.ContentFilterRadio}, given Content will be
     * passed to MW for handling. If Android widget or application, it will be
     * started.
     * 
     * @throws RemoteException
     */
    @Override
    public int goContent(Content content, int displayId) throws RemoteException {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG, "goContent:" + content.toString());
        int indexInAllList = getContentIndexInAllList(content);
        ContentListControl contentListControl = (ContentListControl) IWEDIAService
                .getInstance().getDtvManagerProxy().getContentListControl();
        if (indexInAllList < numberOfServicesInMasterList) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "GO CONTENT FROM MW MASTER SERVICE LIST");
            }
            contentListControl.setActiveContent(content, displayId);
            try {
                int decoderID = IWEDIAService.getInstance()
                        .getDtvManagerProxy().getDecoderID(displayId);
                int routeID = IWEDIAService.getInstance().getDtvManagerProxy()
                        .getRouteManager()
                        .getLiveRouteId(content.getSourceType(), decoderID);
                Log.d(LOG_TAG, "goContent decoderID[" + decoderID
                        + "] routeID[" + routeID + "]");
                IWEDIAService
                        .getInstance()
                        .getDtvManagerProxy()
                        .getServiceControl()
                        .goServiceIndexFromServiceList(0,
                                content.getIndexInMasterList(), routeID);
                return content.getIndex();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (indexInAllList < numberOfServicesInMasterList
                + numberOfIpServices) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "GO IP CONTENT");
            }
            cntFilter = contentManager.getContentFilter(FilterType.IP_STREAM);
            if (cntFilter != null) {
                return cntFilter.goContent(content, displayId);
            }
        } else if (indexInAllList < numberOfServicesInMasterList
                + numberOfIpServices + numberOfInputs) {
            if (IWEDIAService.DEBUG) {
                Log.e(LOG_TAG, "GO INPUT CONTENT");
            }
            cntFilter = contentManager.getContentFilter(FilterType.INPUTS);
            if (cntFilter != null) {
                return cntFilter.goContent(content, displayId);
            }
        }
        return -1;
    }

    /**
     * Opens a content by given index, e.g. runs Android application, plays
     * Radio channel.
     */
    @Override
    public int goContentByIndex(int index, int displayId) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "goContentbyIndex:" + index);
        }
        Content content = getContent(index - 1);
        if (content != null) {
            if (content.getFilterType() != FilterType.APPS
                    && content.getFilterType() != FilterType.WIDGETS) {
                try {
                    ((ContentListControl) (IWEDIAService.getInstance()
                            .getDtvManagerProxy().getContentListControl()))
                            .setCurrentServiceListIndex(content
                                    .getServiceListIndex());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                try {
                    goContent(content, displayId);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return content.getIndex();
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Returns number of items in "All list" of GUI content filter all.
     * 
     * @return number of all MW services + number of Android applications +
     *         number of Android widgets.
     */
    @Override
    public int getContentListSize() {
        int nbA = 0;
        int nbW = 0;
        int numberOfAllContents = 0;
        try {
            cntFilter = contentManager.getContentFilter(FilterType.APPS);
            if (cntFilter != null) {
                nbA = cntFilter.getContentListSize();
            }
            cntFilter = contentManager.getContentFilter(FilterType.WIDGETS);
            if (cntFilter != null) {
                nbW = cntFilter.getContentListSize();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        numberOfAllContents = numberOfServicesInMasterList + numberOfIpServices
                + numberOfInputs + nbA + nbW;
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentListSize:" + numberOfAllContents);
        }
        return numberOfAllContents;
    }

    /**
     * Adds given Content to favorite list.
     */
    @Override
    public boolean addContentToFavorites(Content content) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "addContentToFavouriteList");
        }
        IContentFilter cntFilter = contentManager.getContentFilter(content
                .getFilterType());
        if (cntFilter != null) {
            try {
                return cntFilter.addContentToFavorites(content);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Removes given Content from favorite list.
     */
    @Override
    public boolean removeContentFromFavorites(Content content) {
        IContentFilter cntFilter = contentManager.getContentFilter(content
                .getFilterType());
        if (cntFilter != null) {
            try {
                return cntFilter.removeContentFromFavorites(content);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns number of Content items in favorite list.
     */
    @Override
    public int getFavoritesSize() {
        return favoriteList.size();
    }

    /**
     * Returns Content item of favorite list by given index.
     */
    @Override
    public Content getFavoriteItem(int index) {
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getFavoriteItem-start");
        }
        if (index > favoriteList.size()) {
            return null;
        }
        return favoriteList.get(index);
    }

    /**
     * Returns number of Content items in recently accessed list.
     */
    @Override
    public int getRecenltyWatchedListSize() {
        if (IWEDIAService.DEBUG)
            Log.e(LOG_TAG,
                    "getRecenltyWatchedListSize:" + recenltyWatched.size());
        return recenltyWatched.size();
    }

    /**
     * Returns Content item of recently accessed list by given index.
     */
    @Override
    public Content getRecentlyWatchedItem(int index) {
        return recenltyWatched.get(index);
    }

    /**
     * Return index in ContentFilterAll list of given content.
     */
    public int getContentIndexInAllList(Content content) throws RemoteException {
        int position = 0;
        if (content instanceof ServiceContent) {
            position = content.getIndexInMasterList();
            if (isDummyFound && position != 0) {
                position -= 1;
            }
        } else if (content instanceof IpContent)
            position = numberOfServicesInMasterList + content.getIndex();
        else if (content instanceof InputContent)
            position = numberOfServicesInMasterList + numberOfIpServices
                    + content.getIndex();
        // int filterType = content.getFilterType();
        // int position = 0;
        // int numberOfElements = 0;
        // IContentFilter cntFilter;
        // // ZORANA C 3.0 Must count Inputs
        // for (int i = FilterType.TERRESTRIAL; i <= FilterType.DATA; i++) {
        // cntFilter = contentManager.getContentFilter(i);
        // if (cntFilter != null) {
        // if (cntFilter.toInt() == filterType) {
        //
        // position = numberOfElements + content.getIndex();
        //
        // break;
        // }
        //
        // numberOfElements += cntFilter.getContentListSize();
        // }
        // }
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentIndexInAllList:" + position);
        }
        return position;
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
    public Content getContentExtendedInfoByIndex(int index) {
        Content content = null;
        try {
            content = returnContent(index, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public boolean isDummyFound(String contentName) {
        if (contentName.contains("Dummy")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int stopContent(int index) throws RemoteException {
        IWEDIAService.getInstance().getDtvManagerProxy().getServiceControl()
                .stopService();
        return 0;
    }
}
