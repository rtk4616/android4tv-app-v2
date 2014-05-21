package com.iwedia.gui.components;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.gui.R;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.concurrent.TimeUnit;

/**
 * Class that represents media controller in DLNA media playback
 * 
 * @author Milos Milanovic
 */
public class A4TVMultimediaController extends SeekBar implements OSDGlobal {
    private final static String TAG = "A4TVMultimediaController";
    // Progress Thumb Shadow Color
    private static final int THUMB_SHADOW_COLOR = 0x66000000;
    private static ControlProvider sControlProvider = null;
    // Current chosen control
    private static int sControlPosition = 2;
    // Current RepeatControl Position
    private static int sControlRepeatPosition = 0;
    // Flag for Stop, Pause
    private static boolean sFlagPlay = false;
    // Flag for REW, FF
    private static boolean sFlagFFREW = true;
    // Time Strings
    private static int sElapsedTime = 0;
    private static int sDuration = 0;
    // About File who is gone be to be played
    private static String sFileName = "Rok M Ring Live";
    private static String sFileDescription = "Description";
    private static String sNameOfAlbum = "";
    private Paint mPaint = null;
    private Rect mBounds = null;
    private final String strTimeBounds = "00:00:00";
    private Activity mActivity = null;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mThicknessTop = 0;
    private int mThicknessBottom = 0;
    private int mThumbThickness = 0;
    private int mTextSizeFileName = 0;
    private int mTextSizeDescription = 0;
    private int mTextSizeTime = 0;
    private int mTextTimeWidth = 0;
    private int mTextTimeHeight = 0;

    public A4TVMultimediaController(final Activity activity,
            AttributeSet attrs, int defStyle, int width, int height) {
        super(activity, attrs, defStyle);
        this.mActivity = activity;
        this.mWidth = width;
        this.mHeight = height;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new Rect();
        mThicknessTop = (22 * height) / 100;
        mThicknessBottom = (15 * height) / 100;
        mThumbThickness = height / 9;
        setPadding(mThumbThickness, 2 * mThicknessTop, mThumbThickness, mHeight
                - (3 * mThicknessTop) + mThicknessBottom);
        mTextSizeFileName = (14 * height) / 100;
        mTextSizeTime = (9 * mHeight) / 100;
        mTextSizeDescription = (11 * height) / 100;
        initPaint(Typeface.BOLD, mTextSizeFileName);
        setProgressDrawable(activity.getResources().getDrawable(
                R.drawable.a4tv_curl_mediacontrol_drawable_ics));
        if (mThumbThickness > 0) {
            setThumb(drawThumb(mThumbThickness, mThumbThickness));
        }
        // Get Time Bounds
        mPaint.getTextBounds(strTimeBounds, 0, strTimeBounds.length(), mBounds);
        mTextTimeWidth = mBounds.width();
        mTextTimeHeight = mBounds.height();
    }

    /**
     * Initialize paint object
     * 
     * @param style
     *        Style of paint text
     * @param textsize
     *        Size of paint text
     */
    private void initPaint(int style, int textsize) {
        Typeface tf = Typeface.create("Roboto", style);
        mPaint.setTextSize(textsize);
        mPaint.setColor(Color.WHITE);
        mPaint.setTypeface(tf);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        setProgress(calculateProrgressPercent());
        if (sFileName.length() > 0) {
            // Draw File Name
            initPaint(Typeface.BOLD, mTextSizeFileName);
            mPaint.getTextBounds(sFileName, 0, sFileName.length(), mBounds);
            canvas.drawText(sFileName,
                    mWidth - mThumbThickness - mBounds.width(),
                    ((5 * mBounds.height()) / 3), mPaint);
        }
        if (sFileDescription.length() > 0) {
            // Draw File Description
            initPaint(Typeface.NORMAL, mTextSizeDescription);
            mPaint.getTextBounds(sFileDescription, 0,
                    sFileDescription.length(), mBounds);
            if (mBounds.width() > 0 && mBounds.height() > 0)
                canvas.drawText(sFileDescription, mWidth - mThumbThickness
                        - mBounds.width(), ((5 * mBounds.height()) / 4)
                        + ((5 * mTextSizeFileName) / 3), mPaint);
        }
        if (sNameOfAlbum.length() > 0) {
            initPaint(Typeface.ITALIC, mTextSizeDescription);
            mPaint.getTextBounds(sNameOfAlbum, 0, sNameOfAlbum.length(),
                    mBounds);
            if (mBounds.width() > 0 && mBounds.height() > 0)
                canvas.drawText(sNameOfAlbum, mWidth - mThumbThickness
                        - mBounds.width(), (2 * mBounds.height()
                        + mTextSizeFileName + 12), mPaint);
        }
        // Draw ElapsedTime
        String time = "";
        initPaint(Typeface.NORMAL, mTextSizeTime);
        time = calculateTime(sElapsedTime);
        canvas.drawText(time, mThumbThickness, (mHeight / 2) + mTextTimeHeight,
                mPaint);
        // Draw Duration Time
        time = calculateTime(sDuration);
        canvas.drawText(time, mWidth - (21 * mTextTimeWidth) / 24,
                (mHeight / 2) + mTextTimeHeight, mPaint);
        if (mWidth > 0 && mHeight > 0) {
            // Draw MediaControl
            Bitmap imgMediaControl = drawMediaControl(mWidth
                    - (2 * mThumbThickness) - (2 * mTextTimeWidth), mHeight / 3);
            canvas.drawBitmap(imgMediaControl,
                    mTextTimeWidth + mThumbThickness, (mHeight / 2)
                            + ((1 * mTextTimeHeight) / 2), mPaint);
            imgMediaControl.recycle();
        }
        super.onDraw(canvas);
    }

    /**
     * Create progress thumb drawable
     * 
     * @param width
     * @param height
     * @return
     */
    private Drawable drawThumb(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(THUMB_SHADOW_COLOR);
        canvas.drawCircle((5 * width) / 24, (8 * height) / 24, width / 6, paint);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2, (8 * height) / 24, width / 3, paint);
        // Bitmap -> Drawable
        return new BitmapDrawable(mActivity.getResources(), bitmap);
    }

    /**
     * Returns media Icon from resources
     * 
     * @param width
     * @param height
     * @return
     */
    private Bitmap drawMediaIcon(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap bmpIcon = null;
        bmpIcon = BitmapFactory.decodeResource(mActivity.getResources(),
                R.drawable.media_all_media);
        canvas.drawBitmap(bmpIcon, width - bmpIcon.getWidth(),
                height - bmpIcon.getHeight(), paint);
        bmpIcon.recycle();
        return bitmap;
    }

    /**
     * Draw media controls to bitmap
     * 
     * @param width
     * @param height
     * @return
     */
    private Bitmap drawMediaControl(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap bmpControl = null;
        int refImageWidth = 0;
        int refImageHeight = 0;
        // Draw Play
        bmpControl = getImagePlayPause();
        if (bmpControl != null) {
            refImageHeight = bmpControl.getHeight() / 4;
            refImageWidth = bmpControl.getWidth();
        }
        canvas.drawBitmap(bmpControl, (width / 2) - (refImageWidth / 2), 0,
                paint);
        if (bmpControl != null) {
            bmpControl.recycle();
        }
        // Draw Rewind
        bmpControl = getImageRewind();
        canvas.drawBitmap(bmpControl, (width / 2) - (2 * refImageWidth),
                refImageHeight, paint);
        if (bmpControl != null) {
            bmpControl.recycle();
        }
        // Draw Stop
        bmpControl = getImageStop();
        canvas.drawBitmap(bmpControl, (width / 2) - (3 * refImageWidth),
                refImageHeight, paint);
        if (bmpControl != null) {
            bmpControl.recycle();
        }
        // Draw Forward
        bmpControl = getImageForward();
        canvas.drawBitmap(bmpControl,
                (width / 2) + ((20 * refImageWidth) / 24), refImageHeight,
                paint);
        if (bmpControl != null) {
            bmpControl.recycle();
        }
        // Draw Repeat
        bmpControl = getImageRepeat();
        canvas.drawBitmap(bmpControl,
                (width / 2) + ((55 * refImageWidth) / 24), refImageHeight,
                paint);
        if (bmpControl != null) {
            bmpControl.recycle();
        }
        return bitmap;
    }

    /**
     * Returns appropriate STOP image
     * 
     * @return
     */
    private Bitmap getImageStop() {
        Bitmap bmpStop = null;
        if (sControlPosition == MULTIMEDIA_CONTROLLER_STOP) {
            bmpStop = BitmapFactory.decodeResource(mActivity.getResources(),
                    R.drawable.media_controller_stop_focused);
        } else {
            bmpStop = BitmapFactory.decodeResource(mActivity.getResources(),
                    R.drawable.media_controller_stop_un_focused);
        }
        return bmpStop;
    }

    /**
     * Returns appropriate REWIND image
     * 
     * @return
     */
    private Bitmap getImageRewind() {
        Bitmap bmpRewind = null;
        if (sControlPosition == MULTIMEDIA_CONTROLLER_REW_PREVIOUS) {
            if (sFlagPlay || sFlagFFREW) {
                bmpRewind = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_rew_focused);
            } else {
                bmpRewind = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_previous_focused);
            }
        } else {
            if (sFlagPlay || sFlagFFREW) {
                bmpRewind = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_rew_un_focused);
            } else {
                bmpRewind = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_previous_un_focused);
            }
        }
        return bmpRewind;
    }

    /**
     * Returns appropriate PLAY/PAUSE image
     * 
     * @return
     */
    private Bitmap getImagePlayPause() {
        Bitmap bmpPlayPause = null;
        if (sControlPosition == MULTIMEDIA_CONTROLLER_PLAY) {
            if (!sFlagPlay) {
                bmpPlayPause = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_play_focused);
            } else {
                bmpPlayPause = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_pause_focused);
            }
        } else {
            if (!sFlagPlay) {
                bmpPlayPause = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_play_un_focused);
            } else {
                bmpPlayPause = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_pause_un_focused);
            }
        }
        return bmpPlayPause;
    }

    /**
     * Returns appropriate FORWARD image
     * 
     * @return
     */
    private Bitmap getImageForward() {
        Bitmap bmpForward = null;
        if (sControlPosition == MULTIMEDIA_CONTROLLER_FF_NEXT) {
            if (sFlagPlay || sFlagFFREW) {
                bmpForward = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_ff_focused);
            } else {
                bmpForward = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_next_focused);
            }
        } else {
            if (sFlagPlay || sFlagFFREW) {
                bmpForward = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_ff_un_focused);
            } else {
                bmpForward = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_next_un_focused);
            }
        }
        return bmpForward;
    }

    /**
     * Returns appropriate REPEAT image
     * 
     * @return
     */
    private Bitmap getImageRepeat() {
        Bitmap bmpRepeat = null;
        if (sControlPosition == MULTIMEDIA_CONTROLLER_RE) {
            switch (sControlRepeatPosition) {
                case 0: {
                    bmpRepeat = BitmapFactory.decodeResource(
                            mActivity.getResources(),
                            R.drawable.media_controller_repeat_off_focused);
                    break;
                }
                case 1: {
                    bmpRepeat = BitmapFactory.decodeResource(
                            mActivity.getResources(),
                            R.drawable.media_controller_repeat_one_focused);
                    break;
                }
                case 2: {
                    bmpRepeat = BitmapFactory.decodeResource(
                            mActivity.getResources(),
                            R.drawable.media_controller_repeat_all_focused);
                    break;
                }
                default:
                    break;
            }
        } else {
            switch (sControlRepeatPosition) {
                case 0: {
                    bmpRepeat = BitmapFactory.decodeResource(
                            mActivity.getResources(),
                            R.drawable.media_controller_repeat_off_un_focused);
                    break;
                }
                case 1: {
                    bmpRepeat = BitmapFactory.decodeResource(
                            mActivity.getResources(),
                            R.drawable.media_controller_repeat_one_un_focused);
                    break;
                }
                case 2: {
                    bmpRepeat = BitmapFactory.decodeResource(
                            mActivity.getResources(),
                            R.drawable.media_controller_repeat_all_un_focused);
                    break;
                }
                default:
                    break;
            }
        }
        return bmpRepeat;
    }

    /**
     * Convert milliseconds time to human readable format
     * 
     * @param milliSeconds
     *        Current playback time in milliseconds
     * @return Human readable representation
     */
    private String calculateTime(long milliSeconds) {
        String strTime = "--:--:--";
        if (0 != milliSeconds) {
            strTime = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(milliSeconds),
                    TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                    .toHours(milliSeconds)),
                    TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                    .toMinutes(milliSeconds)));
        }
        return strTime;
    }

    /**
     * Calculates current progress
     * 
     * @return current playback progress
     */
    public int calculateProrgressPercent() {
        int returnValue = 0;
        try {
            returnValue = (sElapsedTime * 100) / sDuration;
        } catch (Exception e) {
            returnValue = 0;
        }
        if (returnValue > 100) {
            return 100;
        } else if (returnValue < 0) {
            return 0;
        } else {
            return returnValue;
        }
    }

    // ///////////////////////////////////////////////////////
    // Use Controls
    // ///////////////////////////////////////////////////////
    public static abstract class ControlProvider {
        public abstract void setContent(MultimediaContent content);

        public abstract MultimediaContent getContent();

        public abstract void play(int displayId);

        public abstract void stop(int displayId);

        public abstract void pause(int displayId);

        public abstract void next(int displayId);

        public abstract void previous(int displayId);

        public abstract void fastForward(int displayId);

        public abstract void rewind(int displayId);

        public abstract void repeatOff(int displayId);

        public abstract void repeatOne(int displayId);

        public abstract void repeatAll(int displayId);

        public abstract void resume(int displayId);

        public void click(int displayId) {
            switch (sControlPosition) {
                case MULTIMEDIA_CONTROLLER_STOP: {
                    sFlagPlay = false;
                    sFlagFFREW = false;
                    sElapsedTime = 0;
                    sControlProvider.stop(displayId);
                    break;
                }
                case MULTIMEDIA_CONTROLLER_REW_PREVIOUS: {
                    if (sFlagPlay || sFlagFFREW) {
                        sControlProvider.rewind(displayId);
                    } else {
                        sControlProvider.previous(displayId);
                    }
                    break;
                }
                case MULTIMEDIA_CONTROLLER_PLAY: {
                    sFlagPlay = !sFlagPlay;
                    if (sFlagPlay) {
                        if (0 == sElapsedTime) {
                            sControlProvider.play(displayId);
                        } else {
                            sControlProvider.resume(displayId);
                        }
                    } else {
                        sFlagFFREW = false;
                        sControlProvider.pause(displayId);
                    }
                    break;
                }
                case MULTIMEDIA_CONTROLLER_FF_NEXT: {
                    if (sFlagPlay || sFlagFFREW) {
                        sControlProvider.fastForward(displayId);
                    } else {
                        sControlProvider.next(displayId);
                    }
                    break;
                }
                case MULTIMEDIA_CONTROLLER_RE: {
                    sControlRepeatPosition++;
                    sControlRepeatPosition = sControlRepeatPosition % 3;
                    break;
                }
                default:
                    break;
            }
        }

        public void moveLeft() {
            if (sControlPosition > 0) {
                sControlPosition--;
            } else {
                sControlPosition = MULTIMEDIA_CONTROLLER_RE;
            }
        }

        public void moveRight() {
            if (sControlPosition < 4) {
                sControlPosition++;
            } else {
                sControlPosition = MULTIMEDIA_CONTROLLER_STOP;
            }
        }

        // ///////////////////////////////////////////////////////
        // Getters and Setters
        // ///////////////////////////////////////////////////////
        public void setDuration(int milliSeconds) {
            sDuration = milliSeconds;
        }

        public int getDuration() {
            return sDuration;
        }

        public static void setFileName(String fileName) {
            try {
                if (fileName.length() >= 15) {
                    fileName = fileName.substring(0, 14) + "...";
                }
            } catch (Exception e) {
                Log.e(TAG, "Method: setFileName", e);
            }
            sFileName = fileName;
        }

        public String getFileName() {
            return sFileName;
        }

        public static void setFileDescription(String fileDescription) {
            try {
                if (fileDescription.length() >= 25) {
                    fileDescription = fileDescription.substring(0, 24) + "...";
                }
            } catch (Exception e) {
                Log.e(TAG, "Method: setFileDescription", e);
            }
            sFileDescription = fileDescription;
        }

        public void setElapsedTime(int milliSeconds) {
            sElapsedTime = milliSeconds;
        }

        public String getFileDescription() {
            return sFileDescription;
        }

        public static void setNameOfAlbum(String nameOfAlbum) {
            try {
                if (nameOfAlbum.length() >= 25) {
                    nameOfAlbum = nameOfAlbum.substring(0, 24) + "...";
                }
            } catch (Exception e) {
                Log.e(TAG, "Method: setNameOfAlbum", e);
            }
            sNameOfAlbum = nameOfAlbum;
        }

        public String getNameOfAlbum() {
            return sNameOfAlbum;
        }

        public int getElapsedTime() {
            return sElapsedTime;
        }

        public void setFlagFFREW(boolean flagFFREW) {
            sFlagFFREW = flagFFREW;
        }

        public boolean getFlagFFREW() {
            return sFlagFFREW;
        }

        public static void setFlagPlay(boolean flagPlay) {
            sFlagPlay = flagPlay;
        }

        public boolean getFlagPlay() {
            return sFlagPlay;
        }
    }

    public static void setControlProvider(ControlProvider controlProvider) {
        A4TVMultimediaController.sControlProvider = controlProvider;
    }

    public static ControlProvider getControlProvider() {
        return sControlProvider;
    }

    public static int getControlRepeatPosition() {
        return sControlRepeatPosition;
    }

    public static void setControlRepeatPosition(int position) {
        sControlRepeatPosition = position;
    }

    public static int getControlPosition() {
        return sControlPosition;
    }

    public static void setControlPosition(int mControlPosition) {
        A4TVMultimediaController.sControlPosition = mControlPosition;
    }
}
