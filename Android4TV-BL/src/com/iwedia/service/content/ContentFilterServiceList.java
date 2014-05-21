package com.iwedia.service.content;

import java.util.ArrayList;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.PlaybackDestinationType;
import com.iwedia.service.IWEDIAService;

/**
 * Content filter used to manage Service List services. {@link ServiceContent}
 * 
 * @author Zorana Marasanov
 */
public class ContentFilterServiceList extends ContentFilter {
    private final String LOG_TAG = "ContentFilterServiceList";
    /**
     * Reference of global list "recenltyWatchedList" in ContentListControl.
     * Used to add played ServiceContent to recently watched list of GUI
     * application.
     */
    private ArrayList<Content> recenltyWatched;
    /**
     * This array list holds indexes of ServiceContents in recenltyWatched list.
     * (This list is needed because recenltyWatched list hold all kind of
     * Contents).
     */
    private ArrayList<Integer> recentlyWatchedIndexes;
    /**
     * Instance of ContentListControl {@link ContentListControl}.
     */
    private ContentListControl contentListControl;
    /**
     * Index of ServiceList (from 0 to
     * contentListControl.getNumberOfServiceLists())
     */
    private int serviceListIndex;
    /**
     * Applies on main display only
     */
    private int mDisplayId = PlaybackDestinationType.MAIN_LIVE;
    /** list of contents with and without hidden ones */
    private ArrayList<Content> allContentArray;
    private ArrayList<Content> contenArray;

    /**
     * Default constructor.
     * 
     * @param manager
     *        ContentManager instance of global content manager to handle
     *        ContentFilters.
     * @param index
     *        Index of service list (0 to
     *        contentListControl.getNumberOfServiceLists())
     * @param recenltyWatched
     *        Instance of global list that represent recently accessed content
     *        items.
     */
    public ContentFilterServiceList(ContentManager manager, int index,
            ArrayList<Content> recenltyWatched) {
        Log.d(LOG_TAG, "ContentFilterServiceList: " + index);
        try {
            contentListControl = (ContentListControl) IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.serviceListIndex = index;
        /**
         * Save reference of recentlyWatched list as local value.
         */
        this.recenltyWatched = recenltyWatched;
        allContentArray = new ArrayList<Content>();
        contenArray = new ArrayList<Content>();
        this.FILTER_TYPE = index;
    }

    @Override
    public void reinitialize() throws RemoteException {
    }

    @Override
    public Content getContent(int index) {
        Log.d(LOG_TAG, "getContent:" + index + " serviceList");
        try {
            return new ServiceContent(index, IWEDIAService.getInstance()
                    .getDtvManagerProxy().getServiceControl(),
                    this.serviceListIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Content getContentVisible(int index) {
        Log.d(LOG_TAG, "getContentVisible:" + index);
        if (index <= contenArray.size()) {
            Log.d(LOG_TAG, "content to return: "
                    + contenArray.get(index).toString());
            return contenArray.get(index);
        }
        return null;
    }

    @Override
    public Content getContentExtendedInfo() throws RemoteException {
        Log.d(LOG_TAG, "getContentExtendedInfo");
        try {
            Content cntActive = contentListControl.getActiveContent(mDisplayId);
            if (cntActive != null) {
                Log.d(LOG_TAG, "getContentExtendedInfo - serviceType"
                        + (this.serviceListIndex));
                return new ServiceContent(cntActive.getIndex(),
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getServiceControl(), this.serviceListIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getContentListName() throws RemoteException {
        return IWEDIAService.getInstance().getDtvManagerProxy().getServiceControl()
                .getServiceListName(this.serviceListIndex);
    }

    @Override
    public int getContentListSize() throws RemoteException {
        Log.d(LOG_TAG, "getContentListSize");
        return IWEDIAService.getInstance().getDtvManagerProxy()
                .getServiceControl().getServiceListCount(this.serviceListIndex);
    }

    @Override
    public int getContentListSizeVisible() throws RemoteException {
        Log.d(LOG_TAG, "getContentListSizeVisible");
        allContentArray.clear();
        contenArray.clear();
        for (int i = 0; i < getContentListSize(); i++) {
            try {
                ServiceContent content = new ServiceContent(i,
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getServiceControl(), this.serviceListIndex);
                allContentArray.add((Content) content);
                Log.d(LOG_TAG, "Adding all content: " + content.toString());
                Log.d(LOG_TAG, "all content size: " + allContentArray.size());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        Log.d(LOG_TAG, "All content: " + allContentArray.size());
        for (int i = 0; i < allContentArray.size(); i++) {
            Content content = allContentArray.get(i);
            Log.d(LOG_TAG, "Content: " + content.toString());
            if (content.isHidden() == false) {
                Log.d(LOG_TAG, "Adding content: " + content.toString());
                contenArray.add(content);
            } else {
                Log.d(LOG_TAG, "skiping hidden content" + content.toString());
            }
        }
        return contenArray.size();
    }

    @Override
    public Content getRecentlyWatchedItem(int arg0) {
        Log.d(LOG_TAG, "getRecentlyWatchedItem");
        return null;
    }

    @Override
    public int goContent(Content content, int displayId) {
        Log.d(LOG_TAG, "goContent");
        contentListControl.setActiveContent(content, displayId);
        try {
            // TODO: uncomment this when service list are fixed
            /*
             * long decoderID =
             * IWEDIAService.getInstance().getDtvManagerProxy().
             * getDecoderID(displayId); long routeID =
             * IWEDIAService.getInstance(
             * ).getDtvManagerProxy().getCurrentLiveRoute(); switch
             * (this.getServiceListIndex()) { case ServiceType.IP_STREAMED:
             * routeID =
             * IWEDIAService.getInstance().getDtvManagerProxy().getIpRouteID
             * (decoderID); break; case ServiceType.TERRESTRIAL: routeID =
             * IWEDIAService
             * .getInstance().getDtvManagerProxy().getLiveRouteIDTer(decoderID);
             * break; case ServiceType.CABLE: routeID =
             * IWEDIAService.getInstance
             * ().getDtvManagerProxy().getLiveRouteIDCab(decoderID); break; case
             * ServiceType.ANALOG: routeID =
             * IWEDIAService.getInstance().getDtvManagerProxy
             * ().getLiveRouteIDAtv(decoderID); break; case ServiceType.DATA:
             * case ServiceType.RADIO: default: Log.w(LOG_TAG,
             * "goContent current live route"); break; }
             */
            int decoderID = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getDecoderID(displayId);
            int routeID = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getRouteManager()
                    .getLiveRouteId(content.getSourceType(), decoderID);
            IWEDIAService
                    .getInstance()
                    .getDtvManagerProxy()
                    .getServiceControl()
                    .goServiceIndexFromServiceList(this.getServiceListIndex(),
                            content.getIndex(), routeID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Starts MW service at given index in MW service list
     * {@link ServiceType.DVB_T};
     */
    @Override
    public int goContentByIndex(int index, int displayId)
            throws RemoteException {
        Content content = getContent(index - 1);
        if (content != null) {
            ((ContentListControl) (IWEDIAService.getInstance()
                    .getDtvManagerProxy().getContentListControl()))
                    .setCurrentServiceListIndex(content.getServiceListIndex());
        }
        if (content != null) {
            goContent(content, displayId);
        }
        int returnIndex = 0;
        if (content != null) {
            returnIndex = content.getIndex();
        }
        return returnIndex;
    }

    @Override
    public boolean addContentToFavoriteList(Content content)
            throws RemoteException {
        Log.d(LOG_TAG, "addContentToFavoriteList:" + content.toString());
        boolean ret = IWEDIAService
                .getInstance()
                .getDtvManagerProxy()
                .getServiceControl()
                .addServiceInServiceList(this.getServiceListIndex(),
                        content.getIndexInMasterList());
        if (false != ret) {
            // number of contents has changed ..
            reinitialize();
        }
        return ret;
    }

    @Override
    public boolean removeContentFromFavoriteList(Content content)
            throws RemoteException {
        Log.d(LOG_TAG, "removeContentFromFavoriteList: " + content.toString());
        boolean ret = IWEDIAService
                .getInstance()
                .getDtvManagerProxy()
                .getServiceControl()
                .deleteServiceFromServiceList(this.getServiceListIndex(),
                        content.getIndexInMasterList());
        if (false != ret) {
            // number of contents has changed ..
            reinitialize();
        }
        return ret;
    }

    @Override
    public int getServiceListIndex() throws RemoteException {
        return this.serviceListIndex;
    }

    @Override
    public int toInt() throws RemoteException {
        return serviceListIndex;
    }

    @Override
    public Content getContentExtendedInfoByIndex(int index)
            throws RemoteException {
        return new ServiceContent(index, IWEDIAService.getInstance()
                .getDtvManagerProxy().getServiceControl(),
                getServiceListIndex());
    }

    @Override
    public int renameContent(Content content, String name)
            throws RemoteException {
        Log.d(LOG_TAG, "renameContent: " + content.getName() + " to " + name);
        IWEDIAService
                .getInstance()
                .getDtvManagerProxy()
                .getServiceControl()
                .renameService(content.getServiceListIndex(),
                        content.getIndex(), name);
        return 0;
    }
}
