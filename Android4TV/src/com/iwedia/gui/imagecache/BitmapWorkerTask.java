package com.iwedia.gui.imagecache;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * AsyncTask for caching bitmaps.
 * 
 * @author Milos Milanovic
 */
public class BitmapWorkerTask extends AsyncTask<Object, Void, Boolean> {
    private ImageMemCache memCache = null;

    public BitmapWorkerTask(ImageMemCache memCache) {
        this.memCache = memCache;
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        memCache.addBitmapToMemoryCache(String.valueOf(params[0]),
                (Bitmap) params[1]);
        return true;
    }
}
