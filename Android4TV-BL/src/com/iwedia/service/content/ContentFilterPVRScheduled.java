package com.iwedia.service.content;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dlna.MultimediaManager;
import com.iwedia.dtv.pvr.SmartInfo;
import com.iwedia.service.IWEDIAService;

public class ContentFilterPVRScheduled extends ContentFilter {
    private final String LOG_TAG = "ContentFilterPVRScheduled";

    public ContentFilterPVRScheduled() {
    }

    @Override
    public void reinitialize() {
    }

    @Override
    public Content getContent(int index) {
        SmartInfo smartInfo = null;
        try {
            smartInfo = IWEDIAService.getInstance().getDtvManagerProxy()
                    .getPvrControl().getSmartInfo(index);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new MultimediaContent(smartInfo, index);
    }

    @Override
    public int getContentListSize() throws RemoteException {
        int size;
        size = IWEDIAService.getInstance().getDtvManagerProxy().getPvrControl()
                .updateRecordList();
        if (IWEDIAService.DEBUG) {
            Log.e(LOG_TAG, "getContentListSize:" + size);
        }
        return size;
    }

    @Override
    public int toInt() {
        return FilterType.PVR_SCHEDULED;
    }

    @Override
    public Content getContentExtendedInfo() {
        // PvrRecord pvrRecord = null;
        // try {
        // pvrRecord = IWEDIAService.getInstance().getDtvManagerProxy()
        // .getPvrControl().getRecord(index);
        // } catch (RemoteException e) {
        // e.printStackTrace();
        // }
        //
        // return new MultimediaContent(pvrRecord, index);
        return null;
    }

    @Override
    public boolean removeAllContentsFromFavorites(int filterType) {
        return MultimediaManager.getInstante().removeAllContentsFromFavorites(
                filterType);
    }
}
