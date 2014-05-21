package com.iwedia.gui.imagecache;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.util.LruCache;

import java.io.File;

/**
 * Implementation of LruMemoryCache. Caching Bitmap in memory.
 * 
 * @author Milos Milanovic
 */
public class ImageMemCache {
    /** Activity instance */
    private Activity activity = null;
    /** LruCache Instance */
    private LruCache<String, Bitmap> mCache = null;

    public ImageMemCache(Activity activity) {
        this.activity = activity;
        initMem();
    }

    /** Configuration of Memory size for Caching and initialization. */
    private void initMem() {
        // Get current memory size
        final int memClass = ((ActivityManager) activity
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Set the size of cached memory
        final int mCacheSize = 1024 * 1024 * memClass / 8;
        // Init LruCache
        mCache = new LruCache<String, Bitmap>(mCacheSize);
    }

    /**
     * Add Bitmap to LruCache (Memory).
     * 
     * @param key
     *        resourceId
     * @param rawBitmap
     *        Bitmap
     */
    public synchronized void addBitmapToMemoryCache(String key, Bitmap rawBitmap) {
        if (getBitmapFromMemCache(key) == null) {
            Bitmap bitmap = createReflectedImage(rawBitmap);
            mCache.put(key, bitmap);
        }
    }

    /**
     * Add Bitmap to LruCache (Memory).
     * 
     * @param key
     *        resourceId
     * @param rawBitmap
     *        Bitmap
     */
    public synchronized void addBitmapToMemoryCacheWithoutReflection(
            String key, Bitmap rawBitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mCache.put(key, rawBitmap);
        }
    }

    /**
     * Add Images to Memory from resource.
     * 
     * @param imageIds
     *        Array of resourceIds
     */
    public void addImagesToMemory(Integer[] imageIds) {
        for (Integer id : imageIds) {
            final Bitmap bitmap = BitmapFactory.decodeResource(
                    activity.getResources(), id);
            if (bitmap != null) {
                addBitmapToMemoryCache(String.valueOf(id), bitmap);
            }
        }
    }

    /**
     * Return bitmap from memory.
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) mCache.get(key);
    }

    /**
     * Get bitmap from memory or get from resource and put it in memory.
     */
    public Bitmap loadBitmapFromResource(int resId) {
        final String imageKey = String.valueOf(resId);
        Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            return bitmap;
        } else {
            Bitmap rawBitmap = BitmapFactory.decodeResource(
                    activity.getResources(), resId);
            if (rawBitmap != null) {
                bitmap = createReflectedImage(rawBitmap);
                rawBitmap.recycle();
            }
            BitmapWorkerTask task = new BitmapWorkerTask(this);
            task.execute(resId, bitmap);
            return bitmap;
        }
    }

    /**
     * Get bitmap from memory or get from disk and put it in memory.
     */
    public Bitmap loadBitmapFromDisk(String path) {
        File imgPath = new File(path);
        if (imgPath.exists()) {
            Bitmap bitmap = getBitmapFromMemCache(path);
            if (bitmap != null) {
                return bitmap;
            } else {
                Bitmap rawBitmap = BitmapFactory.decodeFile(path);
                addBitmapToMemoryCacheWithoutReflection(
                        imgPath.getAbsolutePath(), rawBitmap);
                return rawBitmap;
            }
        } else {
            return null;
        }
    }

    /** Image reflection */
    public Bitmap createReflectedImage(Bitmap originalImage) {
        // The gap we want between the reflection and the original image
        int reflectionGap = 0;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
                3 * height / 4, width, height / 4, matrix, false);
        // Create a new bitmap with same width but taller to fit
        // reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Config.ARGB_8888);
        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        // Create a shader that is a linear gradient that covers the
        // reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,
                originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
                        + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);
        return bitmapWithReflection;
    }
}
