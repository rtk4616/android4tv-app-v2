package com.iwedia.gui.osd;

import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.dtv.service.ServiceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.osd.infobanner.InfoBannerHandler;

public class ChannelChangeHandler implements OSDGlobal {
    private static final String TAG = "ChannelChangeHandler";
    private Activity mActivity = null;
    // Channel Change
    private Content mActiveContent = null, mContentByIndex = null;
    private int mChannelIndex = CHANNEL_SYNC;
    private int mContentIndex = CHANNEL_SYNC;
    private int mMaxNumberOfServices = 0;
    private StringBuilder strBufferedChannelIndex = null;
    private StringBuilder strBufferedSecretKey = null;
    private StringBuilder strBufferedMajorChannelIndex = null;
    private StringBuilder strBufferedMinorChannelIndex = null;
    // Numerous ChannelChange Error
    private boolean mOverMaxChannelNumber = false;
    private static boolean mSameChannel = false;
    private boolean mSelectable = true;
    private static String secretKey = "22223333";
    public String dash = "";
    private int major = 0;
    private int minor = 0;
    private int channel = 0;
    private int mDisplayId = 0;

    public ChannelChangeHandler(Activity activity) {
        this.mActivity = activity;
        // String Buffer for KeyPad (Channel) Number
        strBufferedChannelIndex = new StringBuilder();
        strBufferedSecretKey = new StringBuilder();
        strBufferedMajorChannelIndex = new StringBuilder();
        strBufferedMinorChannelIndex = new StringBuilder();
    }

    public Content syncChannelIndex() {
        /** Get Active Content */
        try {
            if (MainActivity.service != null) {
                mActiveContent = MainActivity.service.getContentListControl()
                        .getActiveContent(mDisplayId);
                syncChannelIndexWithFavoriteLits(mActiveContent);
            }
        } catch (Exception e) {
            Log.e(TAG, "There was error in method 'syncChannelIndex'", e);
        }
        return mActiveContent;
    }

    private void syncChannelIndexWithFavoriteLits(Content content) {
        try {
            if (MainActivity.service.getContentListControl()
                    .getActiveFilterIndex() == FilterType.ALL) {
                mChannelIndex = MainActivity.service.getContentListControl()
                        .getContentIndexInAllList(content);
                Log.d(TAG,
                        "syncChannelIndexWithFavoriteLits - ALL: mChannelIndex = "
                                + mChannelIndex);
            } else {
                mChannelIndex = content.getIndex();
                Log.d(TAG,
                        "syncChannelIndexWithFavoriteLits - FILTER: mChannelIndex = "
                                + mChannelIndex);
            }
        } catch (Exception e) {
            Log.e(TAG,
                    "There was error in method 'syncChannelIndexWithFavoriteLits'",
                    e);
        }
    }

    public void syncChannelIndexByContent(Content content, int displayId) {
        try {
            switch (MainActivity.service.getContentListControl()
                    .getActiveFilterIndex()) {
                case FilterType.ALL: {
                    mContentIndex = MainActivity.service
                            .getContentListControl().getContentIndexInAllList(
                                    content);
                    break;
                }
                default: {
                    mContentIndex = content.getIndex();
                    break;
                }
            }
            mDisplayId = displayId;
        } catch (Exception e) {
            Log.e(TAG, "There was error in method 'syncChannelIndexByContent'",
                    e);
        }
    }

    public int getMaxNumberOfChannels() {
        return mMaxNumberOfServices;
    }

    public void setMaxNumberOfChannels() {
        try {
            switch (MainActivity.service.getContentListControl()
                    .getActiveFilterIndex()) {
                case FilterType.ALL: {
                    mMaxNumberOfServices = MainActivity.service
                            .getServiceControl().getServiceListCount(
                                    ServiceListIndex.MASTER_LIST);
                    mMaxNumberOfServices += MainActivity.service
                            .getContentListControl().getContentFilterListSize(
                                    FilterType.IP_STREAM);
                    Log.e(TAG, "setChannelInfoStrings FilterType.ALL:");
                    break;
                }
                default: {
                    Log.e(TAG, "setChannelInfoStrings FilterType.else:");
                    mMaxNumberOfServices = MainActivity.service
                            .getContentListControl().getContentListSize();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "There was error in method 'setMaxNumberOfChannels'", e);
        }
    }

    /******************************************************************************/
    /** Numerical Change Channel */
    /******************************************************************************/
    /**
     * Fill Numerical Channel Buffer
     * 
     * @param channelIndex
     */
    public StringBuilder fillNumericalChannelBuffer(int channelIndex) {
        if (strBufferedChannelIndex != null) {
            if (strBufferedChannelIndex.length() >= MAX_CHANNEL_NUMBER_LENGTH) {
                strBufferedChannelIndex.delete(0,
                        strBufferedChannelIndex.length());
            }
            strBufferedChannelIndex.append(String.valueOf(channelIndex));
            return strBufferedChannelIndex;
        }
        return null;
    }

    /**
     * Fill Secret key Buffer
     * 
     * @param channelIndex
     */
    public StringBuilder fillSecretKeyBuffer(int channelIndex) {
        if (strBufferedSecretKey != null) {
            if (strBufferedSecretKey.length() >= MAX_SECRET_KEY_LENGTH) {
                strBufferedSecretKey.delete(0, strBufferedSecretKey.length());
            }
            strBufferedSecretKey.append(String.valueOf(channelIndex));
            return strBufferedSecretKey;
        }
        return null;
    }

    /**
     * Fill Major channel number
     * 
     * @param channelIndex
     */
    public String fillMajorMinorBuffers(int channelIndex) {
        if (strBufferedMajorChannelIndex != null
                && strBufferedMinorChannelIndex != null) {
            if (channelIndex == -1) {
                if (strBufferedMajorChannelIndex.length() > 0) {
                    dash = "-";
                }
            } else {
                if (strBufferedMinorChannelIndex.length() >= MAX_MINOR_CHANNEL_NUMBER_LENGTH
                        || (strBufferedMajorChannelIndex.length() >= MAX_MAJOR_CHANNEL_NUMBER_LENGTH && (dash
                                .length() == 0))) {
                    strBufferedMajorChannelIndex.delete(0,
                            strBufferedMajorChannelIndex.length());
                    strBufferedMinorChannelIndex.delete(0,
                            strBufferedMinorChannelIndex.length());
                    dash = "";
                }
                if (strBufferedMajorChannelIndex.length() < MAX_MAJOR_CHANNEL_NUMBER_LENGTH
                        && (dash.length() == 0)) {
                    strBufferedMajorChannelIndex.append(String
                            .valueOf(channelIndex));
                } else if (dash.equals("-")) {
                    strBufferedMinorChannelIndex.append(String
                            .valueOf(channelIndex));
                }
            }
            return (strBufferedMajorChannelIndex.toString() + dash + strBufferedMinorChannelIndex
                    .toString());
        }
        return null;
    }

    public void flushChannelIndexBuffer() {
        if (strBufferedChannelIndex != null) {
            strBufferedChannelIndex.delete(0, strBufferedChannelIndex.length());
        }
    }

    public void flushSecretKeyBuffer() {
        if (strBufferedSecretKey != null) {
            strBufferedSecretKey.delete(0, strBufferedSecretKey.length());
        }
    }

    public void flushMajorMinorChannelIndexBuffer() {
        if (strBufferedMajorChannelIndex != null) {
            strBufferedMajorChannelIndex.delete(0,
                    strBufferedMajorChannelIndex.length());
            if (strBufferedMinorChannelIndex != null)
                strBufferedMinorChannelIndex.delete(0,
                        strBufferedMinorChannelIndex.length());
        } else if (strBufferedMinorChannelIndex != null) {
            if (strBufferedMajorChannelIndex != null)
                strBufferedMajorChannelIndex.delete(0,
                        strBufferedMajorChannelIndex.length());
            strBufferedMinorChannelIndex.delete(0,
                    strBufferedMinorChannelIndex.length());
        }
        dash = "";
    }

    public boolean checkSecretKey() {
        if (secretKey.contentEquals(strBufferedSecretKey.toString())) {
            return true;
        }
        return false;
    }

    /** This Method Checks If Chosen Numerical Exist */
    public boolean channelExistence(int scenario) {
        Log.d(TAG, "channelExistence");
        try {
            if (scenario == SCENARIO_NUMEROUS_CHANNEL_CHANGE) {
                if (strBufferedChannelIndex != null
                        || strBufferedMajorChannelIndex != null
                        || strBufferedMinorChannelIndex != null) {
                    if ((strBufferedChannelIndex != null && strBufferedChannelIndex
                            .length() > 0)
                            || (strBufferedMajorChannelIndex != null && strBufferedMajorChannelIndex
                                    .length() > 0)
                            || strBufferedMinorChannelIndex.length() > 0) {
                        if (ConfigHandler.ATSC) {
                            if (strBufferedMajorChannelIndex != null
                                    && strBufferedMajorChannelIndex.length() > 0) {
                                major = Integer
                                        .parseInt(strBufferedMajorChannelIndex
                                                .toString());
                            } else {
                                major = 0;
                            }
                            if (strBufferedMinorChannelIndex.length() > 0) {
                                minor = Integer
                                        .parseInt(strBufferedMinorChannelIndex
                                                .toString());
                            } else {
                                minor = 0;
                            }
                            channel = major * MAJOR_MINOR_CONVERT_NUMBER
                                    + minor;
                        } else {
                            if (strBufferedChannelIndex != null)
                                channel = Integer
                                        .parseInt(strBufferedChannelIndex
                                                .toString());
                        }
                        int lcn_to_channel = 0;
                        if (ConfigHandler.USE_LCN) {
                            lcn_to_channel = MainActivity.service
                                    .getServiceControl().getServiceIndexByLCN(
                                            MainActivity.service
                                                    .getServiceControl()
                                                    .getActiveService()
                                                    .getListIndex(), channel);
                        }
                        if (lcn_to_channel > 0) {
                            channel = lcn_to_channel;
                        }
                        if (channel > mMaxNumberOfServices) {
                            mOverMaxChannelNumber = true;
                            return false;
                        } else {
                            Content activeContent = MainActivity.service
                                    .getContentListControl()
                                    .getActiveContent(0);
                            if ((activeContent != null)
                                    && (activeContent.getFilterType() == FilterType.INPUTS)) {
                                return true;
                            } else {
                                if (MainActivity.service
                                        .getContentListControl()
                                        .getContent(channel - 1)
                                        .equals(activeContent)) {
                                    mSameChannel = true;
                                    return false;
                                } else if (MainActivity.service
                                        .getContentListControl()
                                        .getContent(channel - 1).isSelectable() == false) {
                                    mSelectable = false;
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error in Method channelExistence", e);
            return false;
        }
    }

    /**
     * This Method Shows Toasts If Chosen Numerical Does not Exist or is the
     * Same Channel
     */
    public void getToastForNumerousChannelChange() {
        if (mOverMaxChannelNumber) {
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.no_channel);
            mOverMaxChannelNumber = false;
        } else if (mSameChannel) {
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast(R.string.already_active);
            mSameChannel = false;
        } else if (mSelectable == false) {
            A4TVToast toast = new A4TVToast(mActivity);
            toast.showToast("Not selectable");
            mSelectable = true;
        }
    }

    /******************************************************************************/
    /******************************************************************************/
    /******************************************************************************/
    /** Changing Channel */
    /******************************************************************************/
    public void changeChannelUpDownPreviousContent(int channelState) {
        /** Set Max Number of Channels */
        setMaxNumberOfChannels();
        // Log.e("setChannelInfoStrings","setMaxNumberOfChannels:" +
        // mMaxNumberOfServices +"channelState: "+ channelState);
        // Log.e("setChannelInfoStrings","changeChannelUpDownPreviousContent mChannelIndex:"
        // + mChannelIndex);
        switch (channelState) {
            case CHANNEL_UP: {
                int count = 0;
                do {
                    mChannelIndex++;
                    if (mChannelIndex >= mMaxNumberOfServices) {
                        mChannelIndex = 0;
                    }
                    try {
                        Content content = MainActivity.service
                                .getContentListControl().getContent(
                                        mChannelIndex);
                        if (content != null) {
                            if (content.isSelectable() == true) {
                                Log.d(TAG, "next content" + content.toString());
                                break;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    count++;
                } while ((count < mMaxNumberOfServices));
                Log.d(TAG, "next UP index: " + mChannelIndex);
                break;
            }
            case CHANNEL_DOWN: {
                int count = 0;
                do {
                    mChannelIndex--;
                    if (mChannelIndex < 0) {
                        mChannelIndex = mMaxNumberOfServices - 1;
                    }
                    try {
                        Content content = MainActivity.service
                                .getContentListControl().getContent(
                                        mChannelIndex);
                        if (content != null) {
                            if (content.isSelectable() == true) {
                                Log.d(TAG, "next content" + content.toString());
                                break;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    count++;
                } while ((count < mMaxNumberOfServices));
                Log.d(TAG, "next DOWN index: " + mChannelIndex);
                break;
            }
            case CHANNEL_TOGGLE_PREVIOUS: {
                getToogleChannel();
                break;
            }
            case CHANNEL_CONTENT: {
                mChannelIndex = mContentIndex;
                break;
            }
            case CHANNEL_GO_TO_INDEX: {
                break;
            }
            default:
                break;
        }
    }

    public void changeChannel() {
        if (MainActivity.activity.getPageCurl() instanceof InfoBannerHandler) {
            InfoBannerHandler.flagZapp = false;
        }
        Log.i(TAG, "Method for ChannelChange is Called!");
        if (strBufferedChannelIndex.length() > 0
                || strBufferedMajorChannelIndex.length() > 0
                || strBufferedMinorChannelIndex.length() > 0) {
            Log.i(TAG, "strBufferedChannelIndex:");
            if (ConfigHandler.ATSC) {
                major = Integer.parseInt(strBufferedMajorChannelIndex
                        .toString());
                minor = Integer.parseInt(strBufferedMinorChannelIndex
                        .toString());
                channel = major * MAJOR_MINOR_CONVERT_NUMBER + minor;
                flushMajorMinorChannelIndexBuffer();
            } else {
                channel = Integer.parseInt(strBufferedChannelIndex.toString());
                flushChannelIndexBuffer();
            }
            flushSecretKeyBuffer();
            try {
                int lcn_to_channel = 0;
                if (ConfigHandler.USE_LCN) {
                    lcn_to_channel = MainActivity.service.getServiceControl()
                            .getServiceIndexByLCN(
                                    MainActivity.service.getServiceControl()
                                            .getActiveService().getListIndex(),
                                    channel);
                    Log.d(TAG, "changeChannel number=" + channel
                            + " lcn_to_channel=" + lcn_to_channel);
                }
                if (lcn_to_channel > 0) {
                    channel = lcn_to_channel;
                    if (mOverMaxChannelNumber) {
                        mOverMaxChannelNumber = false;
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (channel > 0) {
                mChannelIndex = channel;
                // Reduce to be equal with MasterServiceList
                mChannelIndex--;
            } else {
                return;
            }
        } else {
            channel = mChannelIndex + 1;
        }
        Log.i(TAG, "changeChannel:" + channel + "mChannelIndex:"
                + mChannelIndex);
        if (mChannelIndex > CHANNEL_SYNC) {
            Log.i(TAG, "mChannelIndex > CHANNEL_SYNC:");
            changeChannelByIndex(channel, mDisplayId);
        }
    }

    /******************************************************************************/
    /******************************************************************************/
    public boolean checkCurrentWithNextChannelIndex() {
        /** If ActiveContent is Null Enable to ChangeChannel */
        Log.d(TAG, "checkCurrentWithNextChannelIndex");
        if (mActiveContent == null) {
            return false;
        }
        try {
            if (strBufferedChannelIndex.length() > 0
                    || strBufferedMajorChannelIndex.length() > 0
                    || strBufferedMinorChannelIndex.length() > 0) {
                if (ConfigHandler.ATSC) {
                    major = Integer.parseInt(strBufferedMajorChannelIndex
                            .toString());
                    minor = Integer.parseInt(strBufferedMinorChannelIndex
                            .toString());
                    channel = major * MAJOR_MINOR_CONVERT_NUMBER + minor;
                } else {
                    channel = Integer.parseInt(strBufferedChannelIndex
                            .toString());
                }
                if (channel == 0) {
                    mOverMaxChannelNumber = true;
                    getToastForNumerousChannelChange();
                    return true;
                } else {
                    return false;
                }
            } else {
                try {
                    Content activeContent = MainActivity.service
                            .getContentListControl().getActiveContent(0);
                    if (activeContent != null) {
                        Log.d(TAG,
                                "checkCurrentWithNextChannelIndex : active content: "
                                        + activeContent.toString());
                        if (activeContent.getFilterType() == FilterType.INPUTS) {
                            return false;
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "checkCurrentWithNextChannelIndex: previous"
                        + MainActivity.service.getContentListControl()
                                .getContent(mChannelIndex).toString());
                Log.d(TAG, "checkCurrentWithNextChannelIndex: current"
                        + mActiveContent.toString());
                if (isContentActive(MainActivity.service
                        .getContentListControl().getContent(mChannelIndex),
                        mDisplayId)) {
                    try {
                        Content previousContent = MainActivity.service
                                .getContentListControl().getPreviousContent();
                        if ((previousContent != null)
                                && (previousContent.getFilterType() == FilterType.INPUTS)) {
                            return false;
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    mSameChannel = true;
                    getToastForNumerousChannelChange();
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in Method checkCurrentWithNextChannelIndex", e);
            return true;
        }
    }

    public boolean checkDualVideService() {
        if (strBufferedChannelIndex.length() > 0) {
            return ((MainActivity) mActivity).dualVideoActionHandler(
                    CHANNEL_GO_TO_INDEX,
                    Integer.parseInt(strBufferedChannelIndex.toString()));
        }
        return true;
    }

    private void getToogleChannel() {
        try {
            switch (MainActivity.service.getContentListControl()
                    .getActiveFilterIndex()) {
                case FilterType.ALL: {
                    mChannelIndex = MainActivity.service
                            .getContentListControl().getContentIndexInAllList(
                                    MainActivity.service
                                            .getContentListControl()
                                            .getPreviousContent());
                    break;
                }
                default: {
                    mChannelIndex = MainActivity.service
                            .getContentListControl().getPreviousContent()
                            .getIndex();
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "There was error in method 'syncChannelIndexByContent'",
                    e);
        }
    }

    /******************************************************************************/
    /******************************************************************************/
    /******************************************************************************/
    /** Handle Comedia to Change Channel */
    /******************************************************************************/
    public void changeChannelByIndex(final int index, int displayId) {
        try {
            Log.i(TAG, "changeChannelByIndex:");
            MainActivity.service.getContentListControl().goContentByIndex(
                    index, displayId);
            Content content = MainActivity.service.getContentListControl()
                    .getContentByIndexInMasterList(index);
            if (content != null) {
                if (content.getServiceType() == ServiceType.DIG_RAD) {
                    MainActivity.screenSaverDialog
                            .setScreenSaverCause(MainActivity.screenSaverDialog.RADIO);
                    MainActivity.screenSaverDialog.updateScreensaverTimer();
                } else {
                    MainActivity.screenSaverDialog
                            .setScreenSaverCause(MainActivity.screenSaverDialog.LIVE);
                    MainActivity.screenSaverDialog.updateScreensaverTimer();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Channel with index " + index + " can not be changed!",
                    e);
        }
        Log.i(TAG, "after changeChannelByIndex:");
        /** Sync Channel Index */
        syncChannelIndex();
    }

    /******************************************************************************/
    /******************************************************************************/
    /** Getters and Setters */
    public Content getActiveContent() {
        return mActiveContent;
    }

    /** Get active (current) channel */
    public void getExtendedInfo() {
        try {
            mActiveContent = MainActivity.service.getContentListControl()
                    .getContentExtendedInfo();
        } catch (Exception e) {
            Log.e(TAG, "Can't get Content, in method 'setChannelInfoStrings'",
                    e);
        }
    }

    /** Getters and Setters */
    public Content getContentByIndex() {
        return mContentByIndex;
    }

    /** Get active (current) channel */
    public Content getContentExtendedInfoByIndex(int index) {
        try {
            mContentByIndex = MainActivity.service.getContentListControl()
                    .getContentExtendedInfoByIndex(index);
        } catch (Exception e) {
            Log.e(TAG, "Can't get Content, in method 'setChannelInfoStrings'",
                    e);
        }
        return mContentByIndex;
    }

    public Content getCurrentChannelContent() {
        Content content = null;
        try {
            content = MainActivity.service.getContentListControl().getContent(
                    mChannelIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public int getNextContentIndex(int index) {
        setMaxNumberOfChannels();
        index++;
        if (index >= mMaxNumberOfServices) {
            index = 0;
        }
        return index;
    }

    public int getPreviousContentIndex(int index) {
        index--;
        if (index < 0) {
            index = mMaxNumberOfServices - 1;
        }
        return index;
    }

    public int getChannelIndex() {
        return mChannelIndex;
    }

    public StringBuilder getStrBufferedSecretKey() {
        return strBufferedSecretKey;
    }

    public void setStrBufferedSecretKey(StringBuilder strBufferedSecretKey) {
        this.strBufferedSecretKey = strBufferedSecretKey;
    }

    public void setOverMaxChannelNumber(boolean mOverMaxChannelNumber) {
        this.mOverMaxChannelNumber = mOverMaxChannelNumber;
    }

    /** Check if content is active */
    public boolean isContentActive(Content content, int displayId) {
        // ////////////////////////////////////////
        // TV, Data or Radio Service content
        // /////////////////////////////////////////
        // TODO check for IP
        if (content instanceof ServiceContent || content instanceof IpContent) {
            try {
                return content.equals(MainActivity.service
                        .getContentListControl().getActiveContent(displayId));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        // /////////////////////////////////////////////
        // Widget content
        // /////////////////////////////////////////////
        if (content.getFilterType() == FilterType.WIDGETS) {
            if (((MainActivity) mActivity).getWidgetsHandler()
                    .checkWidgetVisibility(content)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
