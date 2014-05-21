package com.iwedia.gui.epg;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.LinearLayout;

/**
 * EPG gallery select listener
 * 
 * @author Branimir Pavlovic
 */
public class EPGGallerySelectListener implements OnItemSelectedListener {
    private EPGHandlingClass epgHandler;
    private LinearLayout galleryItemBig;

    public EPGGallerySelectListener(EPGHandlingClass epgHandler) {
        this.epgHandler = epgHandler;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        if (galleryItemBig != null) {
            galleryItemBig.setLayoutParams(new Gallery.LayoutParams(epgHandler
                    .getEpgInformationsScreenWidth()
                    / epgHandler.GALLERY_ITEM_DIVIDER,
                    android.widget.Gallery.LayoutParams.MATCH_PARENT));
            galleryItemBig.invalidate();
        }
        arg1.setLayoutParams(new Gallery.LayoutParams(2
                * epgHandler.getEpgInformationsScreenWidth()
                / epgHandler.GALLERY_ITEM_DIVIDER,
                android.widget.Gallery.LayoutParams.MATCH_PARENT));
        galleryItemBig = (LinearLayout) arg1;
        arg1.invalidate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
