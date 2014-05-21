package com.iwedia.service.content;

import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.inputs.InputContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.dtv.route.common.RouteInputOutputDescriptor;
import com.iwedia.dtv.route.common.RouteInputOutputDeviceType;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.io.InputOutputControl;
import com.iwedia.service.storage.RenamedInputsController;

/**
 * Manage available inputs {@link InputContent}.
 * 
 * @author Marko Zivanovic
 */
public class ContentFilterInputs extends ContentFilter {
    /**
     * Debug log tag.
     */
    private final String LOG_TAG = "ContentFilterInputs";
    /**
     * Reference of global list "recenltyWatchedList" in ContentListControl.
     * Used to add set InputContent to recently list of GUI application.
     */
    private ArrayList<Content> recenltyWatched;
    /**
     * This array list holds indexes of InputContents in recently list. (This
     * list is needed because recently list hold all kind of Contents).
     */
    private ArrayList<Integer> recentlyWatchedIndexes;
    /**
     * Available devices list.
     */
    private List<Content> availableDevices = null;
    private static RenamedInputsController sqlBaseController;

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
    public ContentFilterInputs(ContentManager manager,
            ArrayList<Content> recenltyWatched) {
        /**
         * Save reference of recentlyWatched list as local value.
         */
        this.recenltyWatched = recenltyWatched;
        this.FILTER_TYPE = com.iwedia.comm.enums.FilterType.INPUTS;
        sqlBaseController = RenamedInputsController.getInstance();
        collectAvailableInputs();
    }

    /**
     * Returns InputContent at given index. {@link InputContent}
     */
    @Override
    public Content getContent(int index) {
        return availableDevices.get(index);
    }

    /**
     * Returns a list of InputContents at given range. {@link InputContent}
     * 
     * @param startIndex
     *        first index of range.
     * @param endIndex
     *        last index of range.
     */
    @Override
    public List<Content> getContentList(int startIndex, int endIndex) {
        return availableDevices.subList(startIndex, endIndex);
    }

    /**
     * Switch to given InputContent. {@link InputContent}. On STB does nothing.
     */
    @Override
    public int goContent(Content content, int displayId) {
        int ret = 0;
        try {
            if (InputOutputControl.DEBUG)
                Log.e(LOG_TAG,
                        "Content Filter Inputs goContent: " + content.getName());
            ContentListControl contentListControl = (ContentListControl) IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
            Content activeContent = contentListControl
                    .getActiveContent(displayId);
            long decoderID = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getRouteManager().getDecoderID(displayId);
            Log.d(LOG_TAG, "goContent decoderID[" + decoderID + "]");
            if (activeContent != null) {
                if (activeContent.getIndex() != content.getIndex()) {
                    if (activeContent instanceof InputContent) {
                        IWEDIAService
                                .getInstance()
                                .getDtvManagerProxy()
                                .getInputOutputControl()
                                .ioDeviceStop((int) decoderID,
                                        activeContent.getIndex());
                    }
                    if (!(activeContent instanceof ServiceContent)) {
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getInputOutputControl().ioDeviceStartDVB();
                    }
                } else {
                    IWEDIAService.getInstance().getDtvManagerProxy()
                            .getInputOutputControl()
                            .ioDeviceResetActiveDevice();
                }
            }
            contentListControl.setActiveContent(content, displayId);
            int deviceID = content.getIndex();
            ret = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getInputOutputControl()
                    .ioDeviceStart((int) decoderID, deviceID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Stops content playback.
     */
    @Override
    public int stopContent(int displayId) {
        if (InputOutputControl.DEBUG) {
            Log.e(LOG_TAG, "stopContent");
        }
        try {
            ContentListControl contentListControl = (ContentListControl) IWEDIAService
                    .getInstance().getDtvManagerProxy().getContentListControl();
            Content activeContent = contentListControl
                    .getActiveContent(displayId);
            long decoderID = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getRouteManager().getDecoderID(displayId);
            Log.d(LOG_TAG, "goContent decoderID[" + decoderID + "]");
            if (activeContent instanceof InputContent) {
                IWEDIAService
                        .getInstance()
                        .getDtvManagerProxy()
                        .getInputOutputControl()
                        .ioDeviceStop((int) decoderID, activeContent.getIndex());
            }
            contentListControl.setActiveContent(null, displayId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns number of available inputs. On STB returns 0.
     */
    @Override
    public int getContentListSize() {
        int size = IWEDIAService.getInstance().getDtvManagerProxy()
                .getRouteManager().getInputDevicesCount();
        size = size - 1; /*
                          * Fixed problem with ContentListSize after remove RF
                          * from InputContentList
                          */
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentListSize - size is: " + size);
        }
        return size;
    }

    /**
     * Returns number of items in recently watched list of content filter.
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
     * Returns Content item in recently watched list size of content filter.
     * 
     * @param {@link InputContent}
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

    private InputContent generateInputContent(int index,
            ArrayList<RouteInputOutputDescriptor> ioDeviceList) {
        String name;
        RouteInputOutputDescriptor device = ioDeviceList.get(index);
        Log.e(LOG_TAG,
                "generateInputContent(" + index + ")" + device.toString());
        Log.e(LOG_TAG, "generateInputContent() "
                + device.getInputOutputDeviceType().getValue());
        switch (device.getInputOutputDeviceType()) {
            case HDMI:
                name = "HDMI";
                break;
            case DVI:
                name = "DVI";
                break;
            case SCART:
                name = "SCART";
                break;
            case CVBS:
                name = "CVBS (Composite)";
                break;
            case RGB:
                name = "RGB";
                break;
            case VGA:
                name = "VGA";
                break;
            case SVIDEO:
                name = "S-Video";
                break;
            case RF:
                name = "RF";
                break;
            case USB:
                name = "USB";
                break;
            case COMPONENT:
                name = "Component";
                break;
            case SPDIF:
                name = "S/PDIF";
                break;
            case SPEAKER:
                name = "Speaker";
                break;
            case HEADPHONE:
                name = "Headphone";
                break;
            case DIGITAL_TUNER:
                name = "Digital Tuner";
                break;
            case PANEL:
                name = "Panel";
                break;
            case ANALOG_TUNER:
                name = "Analog Tuner";
                break;
            default:
                name = "Unknown";
        }
        if (!isOneOfAKindIoDevice(device, ioDeviceList)) {
            name = name + "" + (device.getPort() + 1);
        }
        String newName = sqlBaseController.getInputContent(index);
        if (newName != null) {
            name = newName;
            Log.d(LOG_TAG, "Input has been renamed to" + name);
        }
        Log.d(LOG_TAG, "Input name:" + name);
        InputContent content = new InputContent(device.getInputOutputId(), name);
        return content;
    }

    private boolean isOneOfAKindIoDevice(RouteInputOutputDescriptor device,
            ArrayList<RouteInputOutputDescriptor> ioDeviceList) {
        int count = 0;
        for (RouteInputOutputDescriptor currDevice : ioDeviceList) {
            if (device.getInputOutputDeviceType() == currDevice
                    .getInputOutputDeviceType()) {
                count++;
            }
        }
        return (count == 1);
    }

    private void collectAvailableInputs() {
        if (availableDevices == null) {
            availableDevices = new ArrayList<Content>();
        }
        try {
            /*
             * Get the list of all devices. We need it to generate the names
             * properly (to enumerate devices when multiple devices of the same
             * type are present)
             */
            ArrayList<RouteInputOutputDescriptor> inputDeviceList = IWEDIAService
                    .getInstance().getDtvManagerProxy().getRouteManager()
                    .getInputDevicesList();
            /* Now generate the input contents */
            for (int i = 0; i < inputDeviceList.size(); i++) {
                if (inputDeviceList.get(i).getInputOutputDeviceType() != RouteInputOutputDeviceType.ANALOG_TUNER) {
                    InputContent inContent = generateInputContent(i,
                            inputDeviceList);
                    availableDevices.add(inContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int renameContent(Content content, String name)
            throws RemoteException {
        Log.d(LOG_TAG, "renameContent: " + content.getName() + " to " + name);
        int index = content.getIndex();
        String newName = sqlBaseController.getInputContent(index);
        if (newName != null) {
            Log.d(LOG_TAG, "found in base - renamed");
            sqlBaseController.removeContentFromList(index);
        }
        sqlBaseController.addContentToList(index, name);
        InputContent renamedContent = new InputContent(index, name);
        int correctedIndex;
        if (index == 0) {
            correctedIndex = index;
        } else {
            correctedIndex = index - 1;
        }
        if (IWEDIAService.getInstance().getDtvManagerProxy()
                .getContentListControl()
                .getContentLockedStatus(availableDevices.get(correctedIndex))) {
            /** Content is locked - remove it and add new renamed one */
            IWEDIAService
                    .getInstance()
                    .getDtvManagerProxy()
                    .getContentListControl()
                    .setContentLockStatus(availableDevices.get(correctedIndex),
                            false);
            IWEDIAService.getInstance().getDtvManagerProxy()
                    .getContentListControl()
                    .setContentLockStatus(renamedContent, true);
        }
        /** Update devices list */
        availableDevices.remove(correctedIndex);
        availableDevices.add(correctedIndex, renamedContent);
        return 0;
    }
}
