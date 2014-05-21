package com.iwedia.gui.osd.curleffect;

import android.app.Activity;
import android.dtv.graphics.GraphicsRendererNative;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVProgressVolume;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class for creating textures for Curl Mashes.
 * 
 * @author Milos Milanovic
 */
public class CreateBitmaps implements OSDGlobal {
    private final String TAG = "CreateBitmaps";
    private Paint paintBackground = null;
    private Paint paintForeground = null;
    private Paint paintText = null;
    private A4TVProgressBar mProgressBar = null;
    private A4TVProgressBarPVR mProgressBarPVR = null;
    private A4TVProgressVolume mProgressVolume = null;
    private A4TVMultimediaController mMultimediaController = null;
    private Bitmap mBitmapBackground = null;
    private Bitmap mBitmapForeground = null;
    private Bitmap mBitmapInfoForeground = null;

    /** Get Bitmaps from Resource */
    public Bitmap loadBitmapFromResource(int width, int height,
            Activity activity, int resource) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Drawable d = activity.getResources().getDrawable(resource);
        Rect r = new Rect(0, 0, width, height);
        d.setBounds(r);
        d.draw(c);
        return b;
    }

    /** Create Progress Bar for ChannelInfo */
    private void initProgress(int width, int height, Activity activity) {
        if (mProgressBar != null) {
            mProgressBar.setProgress(0);
            mProgressBar.setSecondaryProgress(0);
            return;
        } else {
            // Init pprogress
            mProgressBar = new A4TVProgressBar(activity, false);
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressBar.measure(width / 4, (2 * height) / 100);
            mProgressBar.layout(0, 0, width / 4, (2 * height) / 100);
        }
    }

    /** Create custom MediaController for MediaPlayer */
    private void initMultimediaControl(int width, int height, Activity activity) {
        if (mMultimediaController != null) {
            mMultimediaController.setProgress(0);
            mMultimediaController.setSecondaryProgress(0);
            return;
        } else {
            // Init MultimediaControl
            mMultimediaController = new A4TVMultimediaController(activity,
                    null, android.R.attr.seekBarStyle, width / 3,
                    (25 * height) / 100);
            mMultimediaController.setVisibility(View.INVISIBLE);
            mMultimediaController.measure(width / 3, (25 * height) / 100);
            mMultimediaController.layout(0, 0, width / 3, (25 * height) / 100);
        }
    }

    /** Create custom Progress Bar for PVR */
    private void initPVRProgress(int width, int height, Activity activity) {
        if (mProgressBarPVR != null) {
            mProgressBarPVR.setProgress(-1);
            mProgressBarPVR.setSecondaryProgress(-1);
            return;
        } else {
            // Init pprogress
            mProgressBarPVR = new A4TVProgressBarPVR(activity, null,
                    android.R.attr.progressBarStyleHorizontal, width / 3,
                    (25 * height) / 100);
            mProgressBarPVR.setVisibility(View.INVISIBLE);
            mProgressBarPVR.measure(width / 3, (25 * height) / 100);
            mProgressBarPVR.layout(0, 0, width / 3, (25 * height) / 100);
        }
    }

    /** Create custom Progress Bar for Volume */
    private void initVolumeProgress(int width, int height, Activity activity) {
        if (mProgressVolume != null) {
            mProgressVolume.setProgress(0);
            mProgressVolume.setSecondaryProgress(0);
            return;
        } else {
            // Init pprogress
            mProgressVolume = new A4TVProgressVolume(activity, null,
                    android.R.attr.progressBarStyleHorizontal, width / 4,
                    (25 * height) / 100);
            mProgressVolume.setVisibility(View.INVISIBLE);
            mProgressVolume.measure(width / 4, (25 * height) / 100);
            mProgressVolume.layout(0, 0, width / 4, (25 * height) / 100);
        }
    }

    /** Create and Initialize Paint for Background Texture */
    private void initPaintBackground(int width, int height) {
        if (paintBackground != null) {
            return;
        } else {
            paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
            int colors[] = { COLOR_BACKGROUND_TEXTURE_GRADIENT_I,
                    COLOR_BACKGROUND_TEXTURE_GRADIENT_II };
            float positions[] = { .7f, 1f };
            LinearGradient gradient = new LinearGradient((47 * width) / 100,
                    (7 * height) / 100, (63 * width) / 100,
                    (125 * height) / 100, colors, positions,
                    Shader.TileMode.CLAMP);
            paintBackground.setColor(Color.BLACK);
            paintBackground.setShader(gradient);
        }
    }

    /** Create and Initialize Paint for Foreground Texture */
    private void initPaintForeground(int width, int height) {
        if (paintForeground != null) {
            return;
        } else {
            paintForeground = new Paint(Paint.ANTI_ALIAS_FLAG
                    | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
            paintForeground.setColor(Color.BLACK);
        }
    }

    /** Create Gradient for Foreground Texture */
    private void makeLinearGradientForeground(int width, int height) {
        int colors[] = { COLOR_FOREGROUND_TEXTURE_GRADIENT_I,
                COLOR_FOREGROUND_TEXTURE_GRADIENT_II };
        float positions[] = { .7f, .9f };
        LinearGradient gradient = new LinearGradient((59 * width) / 100,
                (56 * height) / 100, (63 * width) / 100, 0, colors, positions,
                Shader.TileMode.CLAMP);
        paintForeground.setShader(gradient);
    }

    /** Create and Initialize Paint for Strings */
    private void initPaintText(int width, int height, int textSize) {
        if (paintText != null) {
            paintText.setTextSize(height / textSize);
            return;
        } else {
            paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            // Set Fonts
            Typeface tf = Typeface.create("Roboto", Typeface.BOLD);
            /** Set configuration */
            // Old size 27.09. "height / 17"
            paintText.setTextSize(height / textSize);
            paintText.setColor(Color.WHITE);
            paintText.setFlags(Paint.ANTI_ALIAS_FLAG);
            paintText.setTypeface(tf);
            paintText.setAntiAlias(true);
            paintText.setFilterBitmap(true);
            paintText.setDither(true);
        }
    }

    /** Create Native Background Layer */
    public void createBackgroundLayer(int width, int height) {
        if (mBitmapBackground == null) {
            mBitmapBackground = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(mBitmapBackground);
            initPaintBackground(width, height);
            // Draw Background
            canvas.drawPaint(paintBackground);
        }
    }

    /** Create Native Foreground Layer */
    public void createForegroundLayer(int width, int height) {
        if (mBitmapForeground == null) {
            mBitmapForeground = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(mBitmapForeground);
            canvas.rotate(180, width / 2, height / 2);
            canvas.scale(-1f, 1f, width / 2, height / 2);
            initPaintForeground(width, height);
            makeLinearGradientForeground(width, height);
            canvas.drawPaint(paintForeground);
        }
    }

    /** Create Native Transparent Foreground Layer for Info */
    public void createInfoForegroundLayer(int width, int height) {
        if (mBitmapInfoForeground == null) {
            mBitmapInfoForeground = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            mBitmapInfoForeground.eraseColor(Color.TRANSPARENT);
        } else {
            mBitmapInfoForeground.eraseColor(Color.TRANSPARENT);
        }
    }

    /** Create Background Texture for ChannelInfo */
    public Bitmap makeTextureChannelInfo(int width, int height,
            Activity activity, int percent, ArrayList<String> values,
            ArrayList<Boolean> scrambled) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        Rect bounds = new Rect();
        // Init Strings
        ArrayList<String> strValues;
        if (values != null) {
            if (values.size() == 8) {
                strValues = values;
            } else {
                String[] strNone = { "", "", "", "", "", "", "", "" };
                strValues = new ArrayList<String>();
                Collections.addAll(strValues, strNone);
            }
        } else {
            String[] strNone = { "", "", "", "", "", "", "", "" };
            strValues = new ArrayList<String>();
            Collections.addAll(strValues, strNone);
        }
        /** Initialize resource */
        initPaintText(width, height, 32);
        initProgress(width, height, activity);
        /** Clear Canvas */
        Path tmpPath = new Path();
        tmpPath.addRect(width, height, (1 * width) / 3, (2 * height) / 3,
                Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        Bitmap imgProgress;
        if (strValues.get(2).length() > 0 || strValues.get(3).length() > 0
                || strValues.get(4).length() > 0
                || strValues.get(5).length() > 0) {
            // SetUp Progress
            try {
                mProgressBar.setProgress(percent);
                mProgressBar.setDrawingCacheEnabled(true);
                imgProgress = Bitmap.createBitmap(mProgressBar
                        .getDrawingCache());
                mProgressBar.setDrawingCacheEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
                imgProgress = BitmapFactory.decodeResource(
                        activity.getResources(), R.drawable.progress_curl);
            }
            /** Draw Bitmaps on canvas */
            canvas.drawBitmap(imgProgress, ((30 * width) / 32 - 30)
                    - imgProgress.getWidth(), ((55 * height) / 64), null);
            /** Draw text on canvas */
            // Now
            paintText.getTextBounds(strValues.get(2), 0, strValues.get(2)
                    .length(), bounds);
            canvas.drawText(strValues.get(2), ((30 * width) / 32 - 30)
                    - imgProgress.getWidth(), ((108 * height) / 128), paintText);
            // Scrambled Now
            if (scrambled.get(0)) {
                if (bounds.width() > 0 && bounds.height() > 0) {
                    Bitmap imgIcon = BitmapFactory.decodeResource(
                            activity.getResources(),
                            R.drawable.scrambled_channel);
                    canvas.drawBitmap(imgIcon, ((31 * width) / 32),
                            ((108 * height) / 132), paintText);
                    if (imgIcon != null) {
                        imgIcon.recycle();
                    }
                }
            }
            // Next
            paintText.getTextBounds(strValues.get(3), 0, strValues.get(3)
                    .length(), bounds);
            canvas.drawText(strValues.get(3), ((30 * width) / 32 - 30)
                    - imgProgress.getWidth(), ((235 * height) / 256), paintText);
            // Scrambled Next
            if (scrambled.get(1)) {
                if (bounds.width() > 0 && bounds.height() > 0) {
                    Bitmap imgIcon = BitmapFactory.decodeResource(
                            activity.getResources(),
                            R.drawable.scrambled_channel);
                    canvas.drawBitmap(imgIcon, ((31 * width) / 32),
                            ((235 * height) / 266), paintText);
                    if (imgIcon != null) {
                        imgIcon.recycle();
                    }
                }
            }
            // Start Time
            paintText.getTextBounds(strValues.get(4), 0, strValues.get(4)
                    .length(), bounds);
            canvas.drawText(strValues.get(4), ((30 * width) / 32 - 30)
                    - imgProgress.getWidth() - ((32 * bounds.width()) / 30),
                    ((225 * height) / 256), paintText);
            // End Time
            paintText.getTextBounds(strValues.get(5), 0, strValues.get(5)
                    .length(), bounds);
            canvas.drawText(strValues.get(5), ((30 * width) / 32 - 30),
                    ((225 * height) / 256), paintText);
            imgProgress.recycle();
            System.gc();
        }
        // Draw Basic Info
        paintText.setTextSize(height / 22);
        // Channel index
        paintText.getTextBounds(strValues.get(0), 0, strValues.get(0).length(),
                bounds);
        canvas.drawText(strValues.get(0), width - bounds.width() - 30,
                height - 20, paintText);
        int boundChannelIndexWidth = width - bounds.width() - 30;
        // Time
        paintText.getTextBounds(strValues.get(1), 0, strValues.get(1).length(),
                bounds);
        canvas.drawText(strValues.get(1), width / 2, height - 20, paintText);
        int boundTimeHeight = bounds.height();
        // Channel name
        paintText.getTextBounds(strValues.get(6), 0, strValues.get(6).length(),
                bounds);
        canvas.drawText(strValues.get(6),
                boundChannelIndexWidth - bounds.width() - 50, height - 20,
                paintText);
        // Date
        paintText.getTextBounds(strValues.get(7), 0, strValues.get(7).length(),
                bounds);
        paintText.setTextSize(paintText.getTextSize() - 15);
        canvas.drawText(strValues.get(7), width / 2 + 15, height - 20
                - boundTimeHeight - bounds.height() / 2, paintText);
        return mBitmapBackground;
    }

    /** Create Background Texture for Numerous ChannelChange */
    public Bitmap makeTextureInputs(int width, int height, Activity activity,
            ArrayList<String> values) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        Rect bounds = new Rect();
        // Init Strings
        ArrayList<String> strValues = null;
        if (values != null) {
            if (values.size() == 3) {
                strValues = values;
            } else {
                String[] strNone = { "", "", "" };
                strValues = new ArrayList<String>();
                Collections.addAll(strValues, strNone);
            }
        } else {
            String[] strNone = { "", "", "" };
            strValues = new ArrayList<String>();
            Collections.addAll(strValues, strNone);
        }
        /** Initialize resource */
        initPaintText(width, height, 22);
        /** Clear Canvas */
        Path tmpPath = new Path();
        int lHeight;
        tmpPath.addRect(width, 0, (1 * width) / 3, height, Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        /** Draw text on canvas */
        // Input name
        paintText.getTextBounds(strValues.get(0), 0, strValues.get(0).length(),
                bounds);
        canvas.drawText(strValues.get(0), width - bounds.width() - 30,
                height - 20, paintText);
        lHeight = bounds.height();
        // Frequency
        paintText.getTextBounds(strValues.get(1), 0, strValues.get(1).length(),
                bounds);
        canvas.drawText(strValues.get(1), width - bounds.width() - 30, height
                - bounds.height() - 20, paintText);
        lHeight += bounds.height();
        // Resolution
        paintText.getTextBounds(strValues.get(2), 0, strValues.get(2).length(),
                bounds);
        canvas.drawText(strValues.get(2), width - bounds.width() - 30, height
                - bounds.height() - 20 - lHeight, paintText);
        return mBitmapBackground;
    }

    /** Create Background Texture for ChannelChange */
    public Bitmap makeTextureChannelChange(int width, int height,
            Activity activity, ArrayList<String> values) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        Rect bounds = new Rect();
        // Init Strings
        ArrayList<String> strValues;
        if (values != null) {
            if (values.size() == 2) {
                strValues = values;
            } else {
                String[] strNone = { "", "" };
                strValues = new ArrayList<String>();
                Collections.addAll(strValues, strNone);
            }
        } else {
            String[] strNone = { "", "" };
            strValues = new ArrayList<String>();
            Collections.addAll(strValues, strNone);
        }
        /** Initialize resource */
        initPaintText(width, height, 22);
        /** Clear Canvas */
        Path tmpPath = new Path();
        tmpPath.addRect(width, 0, (1 * width) / 3, height, Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        /** Draw text on canvas */
        // Channel index
        paintText.getTextBounds(strValues.get(0), 0, strValues.get(0).length(),
                bounds);
        canvas.drawText(strValues.get(0), width - bounds.width() - 30,
                height - 20, paintText);
        int boundChannelIndexWidth = width - bounds.width() - 30;
        paintText.setColor(Color.WHITE);
        // Channel name
        paintText.getTextBounds(strValues.get(1), 0, strValues.get(1).length(),
                bounds);
        canvas.drawText(strValues.get(1),
                boundChannelIndexWidth - bounds.width() - 50, height - 20,
                paintText);
        return mBitmapBackground;
    }

    /** Create Background Texture for Numerous ChannelChange */
    public Bitmap makeTextureNumChannelChange(int width, int height,
            Activity activity, ArrayList<String> values) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        Rect bounds = new Rect();
        // Init Strings
        ArrayList<String> strValues;
        if (values != null) {
            if (values.size() == 1) {
                strValues = values;
            } else {
                strValues = new ArrayList<String>();
                strValues.add("");
            }
        } else {
            strValues = new ArrayList<String>();
            strValues.add("");
        }
        /** Initialize resource */
        initPaintText(width, height, 26);
        /** Clear Canvas */
        Path tmpPath = new Path();
        tmpPath.addRect(width, 0, (1 * width) / 3, height, Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        /** Draw text on canvas */
        // Channel index
        paintText.getTextBounds(strValues.get(0), 0, strValues.get(0).length(),
                bounds);
        canvas.drawText(strValues.get(0), width - bounds.width() - 10,
                height - 20, paintText);
        return mBitmapBackground;
    }

    /** Picture format */
    public Bitmap makeTexturePictureFormat(int width, int height,
            Activity activity, ArrayList<String> values) {
        Log.d(TAG, "makeTexturePictureFormat");
        Rect bounds = new Rect();
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        /** Initialize resource */
        initPaintText(width, height, 22);
        // Init Strings
        ArrayList<String> strValues;
        if (values != null) {
            if (values.size() == 1) {
                strValues = values;
            } else {
                String[] strNone = { "0" };
                strValues = new ArrayList<String>();
                Collections.addAll(strValues, strNone);
            }
        } else {
            String[] strNone = { "0" };
            strValues = new ArrayList<String>();
            Collections.addAll(strValues, strNone);
        }
        /** Clear Canvas */
        Path tmpPath = new Path();
        tmpPath.addRect(width, height, (1 * width) / 3, (2 * height) / 3,
                Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        /** Draw Text on canvas */
        paintText.getTextBounds(strValues.get(0), 0, strValues.get(0).length(),
                bounds);
        canvas.drawText(strValues.get(0), width - bounds.width() - 30,
                height - 20, paintText);
        System.gc();
        return mBitmapBackground;
    }

    /** Volume */
    public Bitmap makeTextureVolume(int width, int height, Activity activity,
            ArrayList<String> values) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        /** Initialize resource */
        initPaintText(width, height, 17);
        initVolumeProgress(width, height, activity);
        // Init Strings
        ArrayList<String> strValues;
        if (values != null) {
            if (values.size() == 1) {
                strValues = values;
            } else {
                String[] strNone = { "0" };
                strValues = new ArrayList<String>();
                Collections.addAll(strValues, strNone);
            }
        } else {
            String[] strNone = { "0" };
            strValues = new ArrayList<String>();
            Collections.addAll(strValues, strNone);
        }
        /**
         * Scale: 720p - 30000, 1080p - (width*1024)/4)
         */
        // SetUp Progress
        Bitmap imgProgress;
        try {
            mProgressVolume.setProgress(Integer.valueOf(strValues.get(0)));
            mProgressVolume.setDrawingCacheEnabled(true);
            imgProgress = Bitmap
                    .createBitmap(mProgressVolume.getDrawingCache());
            mProgressVolume.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            imgProgress = BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.progress_curl);
        }
        /** Clear Canvas */
        Path tmpPath = new Path();
        tmpPath.addRect(width, height, (1 * width) / 3, (2 * height) / 3,
                Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        /** Draw Bitmaps on canvas */
        if (imgProgress != null) {
            canvas.drawBitmap(imgProgress,
                    ((31 * width) / 32) - imgProgress.getWidth(),
                    ((113 * height) / 128), null);
            imgProgress.recycle();
        }
        // imgProgress = null;
        System.gc();
        return mBitmapBackground;
    }

    /** Create Background Texture for MultimediaControl */
    public Bitmap makeTextureMultimediaControl(int width, int height,
            Activity activity) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        /** Initialize resource */
        initPaintText(width, height, 17);
        initMultimediaControl(width, height, activity);
        // SetUp Progress
        Bitmap imgProgress;
        try {
            mMultimediaController.invalidate();
            mMultimediaController.setDrawingCacheEnabled(true);
            imgProgress = Bitmap.createBitmap(mMultimediaController
                    .getDrawingCache());
            mMultimediaController.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            imgProgress = BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.progress_curl);
        }
        /** Clear Canvas */
        Path tmpPath = new Path();
        tmpPath.addRect(width, height, (1 * width) / 3, (2 * height) / 3,
                Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        /** Draw Bitmaps on canvas */
        canvas.drawBitmap(imgProgress,
                ((31 * width) / 32) - imgProgress.getWidth(),
                ((100 * height) / 128), null);
        imgProgress.recycle();
        System.gc();
        return mBitmapBackground;
    }

    /** Create Background Texture for PVR */
    public Bitmap makeTexturePVR(int width, int height, Activity activity) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        /** Initialize resource */
        initPaintText(width, height, 32);
        initPVRProgress(width, height, activity);
        /**
         * Scale: 720p - 30000, 1080p - (width*1024)/4)
         */
        // SetUp Progress
        Bitmap imgProgress;
        try {
            mProgressBarPVR.invalidate();
            mProgressBarPVR.setDrawingCacheEnabled(true);
            imgProgress = Bitmap
                    .createBitmap(mProgressBarPVR.getDrawingCache());
            mProgressBarPVR.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            imgProgress = BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.progress_curl);
        }
        /** Clear Canvas */
        Path tmpPath = new Path();
        tmpPath.addRect(width, height, (1 * width) / 3, (2 * height) / 3,
                Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        /** Draw Bitmaps on canvas */
        canvas.drawBitmap(imgProgress,
                ((124 * width) / 128) - imgProgress.getWidth(),
                ((93 * height) / 128), null);
        imgProgress.recycle();
        System.gc();
        return mBitmapBackground;
    }

    /** Create Foreground Texture for ChannelInfo */
    public Bitmap makeCurlBackSideTexture(int width, int height,
            Activity activity, ArrayList<Integer> imgID, String pathName) {
        Bitmap image;
        int dstImage = 0;
        /** Initialize Background */
        createForegroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapForeground);
        canvas.rotate(180, width / 2, height / 2);
        canvas.scale(-1f, 1f, width / 2, height / 2);
        Path tmpPath = new Path();
        tmpPath.addRect(width, 0, (1 * width) / 3, (2 * height) / 3,
                Direction.CW);
        canvas.drawPath(tmpPath, paintForeground);
        /** Draw ChannelIcon */
        if (pathName.length() == 0) {
            image = BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.default_channel_icon);
        } else {
            image = BitmapFactory.decodeFile(pathName);
        }
        if (image != null) {
            canvas.drawBitmap(image, width - image.getWidth() - 10, 10, null);
            dstImage += image.getWidth();
        }
        if (imgID != null) {
            for (int i = 0; i < imgID.size(); i++) {
                try {
                    /** Draw Bitmap on canvas */
                    image = BitmapFactory.decodeResource(
                            activity.getResources(), imgID.get(i));
                    if (image != null) {
                        canvas.drawBitmap(image,
                                width - dstImage - image.getWidth() - 30, 10,
                                null);
                        dstImage += image.getWidth() + 30;
                        image.recycle();
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        return mBitmapForeground;
    }

    /** Create Transparent Foreground Texture for Info */
    public Bitmap prepareInfoTexture(Activity activity, int width, int height) {
        /** Initialize Foreground */
        createInfoForegroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapInfoForeground);
        // //////////////////////////////////////////////////////////////////////////////
        // DRAW DIALOGS
        // //////////////////////////////////////////////////////////////////////////////
        ArrayList<A4TVDialog> mA4tvDialogs = A4TVDialog.getListOfDialogs();
        if (mA4tvDialogs != null) {
            A4TVDialog a4tvDialog;
            if (mA4tvDialogs.size() == 1) {
                a4tvDialog = mA4tvDialogs.get(0);
                a4tvDialog.getWindow().getDecorView().draw(canvas);
            } else if (mA4tvDialogs.size() >= 1) {
                mA4tvDialogs.get(mA4tvDialogs.size() - 1).getWindow()
                        .getDecorView().draw(canvas);
                // SetUp Dialog
                Bitmap imgInfo;
                try {
                    View view = mA4tvDialogs.get(0).getWindow().getDecorView();
                    view.setDrawingCacheEnabled(true);
                    imgInfo = Bitmap.createBitmap(view.getDrawingCache());
                    view.setDrawingCacheEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    imgInfo = BitmapFactory.decodeResource(
                            activity.getResources(),
                            R.drawable.transparent_image);
                }
                /** Draw Bitmaps on canvas */
                canvas.drawBitmap(imgInfo, (width / 2)
                        - (imgInfo.getWidth() / 2),
                        (height / 2) - (imgInfo.getHeight() / 2), null);
                imgInfo.recycle();
                System.gc();
            }
            // ////////////////////////////////////////////////////////////////////
            return mBitmapInfoForeground;
        } else {
            return null;
        }
    }

    /** Create Transparent Foreground Texture for Info */
    public Bitmap prepareChannelChangeTexture(Activity activity, int width,
            int height) {
        /** Initialize Foreground */
        createInfoForegroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapInfoForeground);
        GraphicsRendererNative.drawVideoFrameOnCanvas(canvas, paintForeground,
                1280, 720);
        return mBitmapInfoForeground;
    }

    /** Create Background Texture for Info */
    public Bitmap makeInfoTexture(Activity activity, int width, int height) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        Path tmpPath = new Path();
        tmpPath.addRect(width, 0, (1 * width) / 3, height, Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        // //////////////////////////////////////////////////////////////////////////////
        // DRAW RCU
        // //////////////////////////////////////////////////////////////////////////////
        Bitmap mRCU = BitmapFactory.decodeResource(
                MainActivity.activity.getResources(), R.drawable.rcu);
        if (mRCU != null) {
            mRCU = Bitmap.createScaledBitmap(mRCU, (3 * mRCU.getWidth()) / 4,
                    (3 * mRCU.getHeight()) / 4, true);
            canvas.drawBitmap(mRCU, ((5 * width) / 6) - (mRCU.getWidth() / 2),
                    (height / 2) - (mRCU.getHeight() / 2), null);
        }
        // //////////////////////////////////////////////////////////////////////////////
        return mBitmapBackground;
    }

    /** Create Background Texture only Gradient for Curled Page */
    public Bitmap makeEmptyCurlSideBackTexture(Activity activity, int width,
            int height) {
        /** Initialize Foreground */
        createForegroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapForeground);
        canvas.rotate(180, width / 2, height / 2);
        canvas.scale(-1f, 1f, width / 2, height / 2);
        Path tmpPath = new Path();
        tmpPath.addRect(width, 0, (1 * width) / 3, (2 * height) / 3,
                Direction.CW);
        canvas.drawPath(tmpPath, paintForeground);
        return mBitmapForeground;
    }

    /** Create Background Texture only Gradient */
    public Bitmap makeEmptySideBackTexture(Activity activity, int width,
            int height) {
        /** Initialize Background */
        createBackgroundLayer(width, height);
        Canvas canvas = new Canvas(mBitmapBackground);
        Path tmpPath = new Path();
        tmpPath.addRect(width, 0, (1 * width) / 3, height, Direction.CW);
        canvas.drawPath(tmpPath, paintBackground);
        return mBitmapBackground;
    }

    public A4TVMultimediaController getMultimediaController() {
        return mMultimediaController;
    }
}