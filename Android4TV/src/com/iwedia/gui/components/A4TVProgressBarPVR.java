package com.iwedia.gui.components;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ProgressBar;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.concurrent.TimeUnit;

/**
 * Class that represents media controller in PVR media playback
 * 
 * @author Milos Milanovic
 */
public class A4TVProgressBarPVR extends ProgressBar implements OSDGlobal {
    private final static String TAG = "A4TVProgressBarPVR";
    private final int FIRST_ARROW_COLOR = 0xFF01B7EF;
    private final int SECOND_ARROW_COLOR = 0xFFFF1A00;
    private final String STR_TIME_BOUNDS = "00:00:00";
    private int mThickness = 0;
    private int mMoveRightSide = 0;
    private int mMoveProgressBar = 0;
    private int mMoveArrow = 0;
    private Paint mPaint = null;
    private Bitmap mArrow = null;
    private static ControlProviderPVR sControlProvider = null;
    private int mPadding = 0;
    private int mArrowSizeWidth = 0;
    private int mArrowSizeHeight = 0;
    private static int sFirstTime = 0;
    private static int sSecondTime = 0;
    private Activity mActivity = null;
    private static int sProgressValue = -1;
    private static int sSecondaryProgressValue = -1;
    private int mTextSizeFileName = 0;
    private int mTextSizeDescription = 0;
    private int mTextSizeTime = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    // Time Strings
    private static int sElapsedTime = 0;
    private static int sDuration = 0;
    // Current chosen control
    private static int sControlPosition = 2;
    // Current RepeatControl Position
    private static int sControlRepeatPosition = 0;
    // Flag for Stop, Pause
    private static boolean sFlagPlay = false;
    private static boolean sFlagRecord = false;
    // Flag for Disk Full and Neatly Full
    private static boolean sFlagDiskFull = false;
    private static boolean sFlagDiskNearlyFull = false;
    // About File who is gone be to be played
    private static String sFileName = "";
    private static String sFileDescription = "";
    private boolean mRotateLineProgress = false;
    private Rect mBounds = null;

    public A4TVProgressBarPVR(final Activity activity, AttributeSet attrs,
            int defStyle, int width, int height) {
        super(activity, attrs, defStyle);
        this.mActivity = activity;
        this.mWidth = width;
        this.mHeight = height;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new Rect();
        mPadding = (20 * width) / 100;
        mArrowSizeHeight = (15 * height) / 100;
        mArrowSizeWidth = ((15 * height) / 100) / 2;
        mThickness = (33 * height) / 100;
        mMoveProgressBar = (4 * height) / 100;
        mMoveArrow = (13 * height) / 200;
        mTextSizeFileName = (14 * height) / 100;
        mTextSizeDescription = (11 * height) / 100;
        mTextSizeTime = (11 * mHeight) / 100;
        mMoveRightSide = (3 * height) / 200;
        initPaint(Typeface.BOLD, mTextSizeFileName);
        setPadding(mPadding, mArrowSizeHeight + mThickness + mMoveProgressBar,
                mPadding, mArrowSizeHeight + mThickness - mMoveProgressBar);
        setProgressDrawable(activity.getResources().getDrawable(
                R.drawable.a4tv_curl_progressbar_drawable_ics));
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
        // Draw File Name
        if (sFileName.length() > 0) {
            initPaint(Typeface.BOLD, mTextSizeFileName);
            mPaint.getTextBounds(sFileName, 0, sFileName.length(), mBounds);
            if (mBounds.width() > 0 && mBounds.height() > 0)
                canvas.drawText(sFileName, mWidth - mBounds.width()
                        - mMoveRightSide, ((5 * mBounds.height()) / 3), mPaint);
        }
        // Draw File Description
        if (sFileDescription.length() > 0) {
            initPaint(Typeface.NORMAL, mTextSizeDescription);
            mPaint.getTextBounds(sFileDescription, 0,
                    sFileDescription.length(), mBounds);
            if (mBounds.width() > 0 && mBounds.height() > 0)
                canvas.drawText(sFileDescription, mWidth - mBounds.width()
                        - mMoveRightSide, ((5 * mBounds.height()) / 4)
                        + ((5 * mTextSizeFileName) / 3), mPaint);
        }
        // Draw ElapsedTime
        String time = "";
        initPaint(Typeface.NORMAL, mTextSizeTime);
        time = calculateTime(sElapsedTime);
        mPaint.getTextBounds(STR_TIME_BOUNDS, 0, STR_TIME_BOUNDS.length(),
                mBounds);
        if (mBounds.width() > 0 && mBounds.height() > 0)
            canvas.drawText(time, 0, (mHeight / 2) + mBounds.height(), mPaint);
        // Draw Duration Time
        time = calculateTime(sDuration);
        mPaint.getTextBounds(STR_TIME_BOUNDS, 0, STR_TIME_BOUNDS.length(),
                mBounds);
        if (mBounds.width() > 0 && mBounds.height() > 0)
            canvas.drawText(time, mWidth - mBounds.width() - mMoveRightSide,
                    (mHeight / 2) + mBounds.height(), mPaint);
        // Draw MediaControl
        Bitmap imgMediaControl = drawMediaControl(mWidth
                - (2 * mBounds.width()), mHeight / 3);
        if (mBounds.width() > 0 && mBounds.height() > 0)
            canvas.drawBitmap(imgMediaControl, mBounds.width(), (mHeight / 2)
                    + ((1 * mBounds.height()) / 2) + mArrowSizeHeight
                    + mMoveProgressBar, mPaint);
        imgMediaControl.recycle();
        // Draw progress
        setProgress(sProgressValue);
        setSecondaryProgress(sSecondaryProgressValue);
        drawProgress(canvas);
        super.onDraw(canvas);
    }

    /**
     * Draw progress on canvas
     * 
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        String time = "";
        mPaint.getTextBounds(STR_TIME_BOUNDS, 0, STR_TIME_BOUNDS.length(),
                mBounds);
        if (sProgressValue != -1) {
            // Draw First Arrow
            time = calculateTime(sFirstTime);
            if ((((getProgress() * (getWidth() - (2 * mPadding)))) / 100) <= ((getWidth() - (2 * mPadding)) / 2)) {
                mRotateLineProgress = false;
            } else {
                mRotateLineProgress = true;
            }
            mArrow = drawArrow(mArrowSizeWidth, mArrowSizeHeight,
                    mArrowSizeHeight / 10, FIRST_ARROW_COLOR,
                    mRotateLineProgress, false);
            if (mRotateLineProgress) {
                canvas.drawBitmap(
                        mArrow,
                        (((getProgress() * (getWidth() - (2 * mPadding)))) / 100)
                                + mPadding - mArrowSizeWidth / 2, mMoveArrow
                                + mThickness, mPaint);
                canvas.drawText(
                        time,
                        ((((getProgress() * (getWidth() - (2 * mPadding)))) / 100))
                                + ((7 * mArrow.getWidth()) / 4)
                                - mMoveRightSide + mBounds.width(),
                        ((35 * mArrow.getHeight()) / 48) + mThickness
                                + mMoveArrow, mPaint);
            } else {
                canvas.drawBitmap(
                        mArrow,
                        ((((getProgress() * (getWidth() - (2 * mPadding)))) / 100))
                                + mPadding - mArrowSizeWidth / 2, mMoveArrow
                                + mThickness, mPaint);
                canvas.drawText(
                        time,
                        ((((getProgress() * (getWidth() - (2 * mPadding)))) / 100)),
                        ((35 * mArrow.getHeight()) / 48) + mThickness
                                + mMoveArrow, mPaint);
            }
            mArrow.recycle();
            mArrow = null;
            System.gc();
        }
        if (sSecondaryProgressValue != -1) {
            // Draw Second Arrow
            time = calculateTime(sSecondTime);
            if ((((getSecondaryProgress() * (getWidth() - (2 * mPadding)))) / 100) <= ((getWidth() - (2 * mPadding)) / 2)) {
                mRotateLineProgress = false;
            } else {
                mRotateLineProgress = true;
            }
            mArrow = drawArrow(mArrowSizeWidth, mArrowSizeHeight,
                    mArrowSizeHeight / 10, SECOND_ARROW_COLOR,
                    mRotateLineProgress, true);
            if (mRotateLineProgress) {
                canvas.drawBitmap(
                        mArrow,
                        (((getSecondaryProgress() * (getWidth() - (2 * mPadding)))) / 100)
                                + mPadding - mArrowSizeWidth / 2, mMoveArrow
                                + mThickness + mArrowSizeHeight, mPaint);
                canvas.drawText(
                        time,
                        ((((getSecondaryProgress() * (getWidth() - (2 * mPadding)))) / 100))
                                + ((7 * mArrow.getWidth()) / 4)
                                - mMoveRightSide + mBounds.width(), mMoveArrow
                                + mThickness + ((23 * mArrowSizeHeight) / 12),
                        mPaint);
            } else {
                canvas.drawBitmap(
                        mArrow,
                        ((((getSecondaryProgress() * (getWidth() - (2 * mPadding)))) / 100))
                                + mPadding - mArrowSizeWidth / 2, mMoveArrow
                                + mThickness + mArrowSizeHeight, mPaint);
                canvas.drawText(
                        time,
                        ((((getSecondaryProgress() * (getWidth() - (2 * mPadding)))) / 100)),
                        mMoveArrow + mThickness
                                + ((23 * mArrowSizeHeight) / 12), mPaint);
            }
            mArrow.recycle();
            mArrow = null;
            System.gc();
        }
    }

    /**
     * Draw arrows that indicates current PVR progresses
     * 
     * @param width
     * @param height
     * @param strokeWidth
     * @param strokeColor
     * @param rotateLine
     * @param rotate
     * @return
     */
    private Bitmap drawArrow(int width, int height, int strokeWidth,
            int strokeColor, boolean rotateLine, boolean rotate) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int offsetX = width / 2;
        int offsetY = height / 2;
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(strokeColor);
        Path path = new Path();
        path.moveTo(0, height / 5);
        path.lineTo(width / 10, 0);
        path.lineTo(-(width / 10), 0);
        path.close();
        path.offset(offsetX, offsetY);
        canvas.drawPath(path, mPaint);
        path = new Path();
        path.lineTo(0, -(height / 3));
        path.close();
        path.offset(offsetX, offsetY);
        canvas.drawPath(path, mPaint);
        path = new Path();
        path.moveTo(0, -(height / 3));
        if (rotateLine) {
            path.lineTo((width / 2), -(height / 3));
            path.close();
            path.offset(offsetX - (strokeWidth / 2), offsetY);
        } else {
            path.lineTo(-(width / 2), -(height / 3));
            path.close();
            path.offset(offsetX + (strokeWidth / 2), offsetY);
        }
        canvas.drawPath(path, mPaint);
        /** Create Final Bitmap Rotated. */
        if (rotate) {
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(),
                    bmpRotateMirror(width, height), true);
            bitmap.recycle();
            bitmap = null;
            return rotated;
        }
        return bitmap;
    }

    /** Bitmap configuration. Rotation for 180 degrees. */
    private static Matrix bmpRotateMirror(int width, int height) {
        Matrix mtx = new Matrix();
        mtx.setRotate(180, width / 2, height / 2);
        mtx.preScale(-1, 1);
        return mtx;
    }

    // /////////////////////////////////////////////////////////////
    // MultiMedia Controller
    // /////////////////////////////////////////////////////////////
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
        refImageHeight = bmpControl.getHeight() / 4;
        refImageWidth = bmpControl.getWidth();
        canvas.drawBitmap(bmpControl, (width / 2) - (refImageWidth / 2), 0,
                paint);
        bmpControl.recycle();
        // Draw Rewind
        bmpControl = getImageRewind();
        canvas.drawBitmap(bmpControl, (width / 2) - (2 * refImageWidth),
                refImageHeight, paint);
        bmpControl.recycle();
        // Draw Stop
        bmpControl = getImageStop();
        canvas.drawBitmap(bmpControl, (width / 2) - (3 * refImageWidth),
                refImageHeight, paint);
        bmpControl.recycle();
        // Draw Forward
        bmpControl = getImageForward();
        canvas.drawBitmap(bmpControl,
                (width / 2) + ((20 * refImageWidth) / 24), refImageHeight,
                paint);
        bmpControl.recycle();
        // Draw Repeat
        bmpControl = getImageRecord();
        canvas.drawBitmap(bmpControl,
                (width / 2) + ((57 * refImageWidth) / 24), refImageHeight,
                paint);
        bmpControl.recycle();
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
            bmpRewind = BitmapFactory.decodeResource(mActivity.getResources(),
                    R.drawable.media_controller_rew_focused);
        } else {
            bmpRewind = BitmapFactory.decodeResource(mActivity.getResources(),
                    R.drawable.media_controller_rew_un_focused);
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
            bmpForward = BitmapFactory.decodeResource(mActivity.getResources(),
                    R.drawable.media_controller_ff_focused);
        } else {
            bmpForward = BitmapFactory.decodeResource(mActivity.getResources(),
                    R.drawable.media_controller_ff_un_focused);
        }
        return bmpForward;
    }

    /**
     * Returns appropriate RECORD image
     * 
     * @return
     */
    private Bitmap getImageRecord() {
        Bitmap bmpRecord = null;
        if (sControlPosition == MULTIMEDIA_CONTROLLER_RE) {
            bmpRecord = BitmapFactory.decodeResource(mActivity.getResources(),
                    R.drawable.media_controller_record_focused);
        } else {
            if (sFlagRecord) {
                bmpRecord = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_record_recording);
            } else {
                bmpRecord = BitmapFactory.decodeResource(
                        mActivity.getResources(),
                        R.drawable.media_controller_record_un_focused);
            }
        }
        return bmpRecord;
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
        if (milliSeconds != 0) {
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

    // ///////////////////////////////////////////////////////
    // Use Controls
    // ///////////////////////////////////////////////////////
    public static abstract class ControlProviderPVR {
        public abstract void record();

        public abstract void play();

        public abstract void stop();

        public abstract void pause();

        public abstract void fastForward();

        public abstract void rewind();

        public abstract void resume();

        public abstract void prepareStop();

        public void click() {
            switch (sControlPosition) {
                case MULTIMEDIA_CONTROLLER_STOP: {
                    sFlagPlay = false;
                    sFlagRecord = false;
                    sControlProvider.stop();
                    break;
                }
                case MULTIMEDIA_CONTROLLER_REW_PREVIOUS: {
                    sControlProvider.rewind();
                    break;
                }
                case MULTIMEDIA_CONTROLLER_PLAY: {
                    sFlagPlay = !sFlagPlay;
                    if (sFlagPlay) {
                        if (sFirstTime == 0) {
                            if (sControlProvider == null) {
                                Log.i(TAG, "mControlProvider == null");
                            } else {
                                sControlProvider.play();
                            }
                        } else {
                            sControlProvider.resume();
                        }
                    } else {
                        sControlProvider.pause();
                    }
                    break;
                }
                case MULTIMEDIA_CONTROLLER_FF_NEXT: {
                    sControlProvider.fastForward();
                    break;
                }
                case MULTIMEDIA_CONTROLLER_RE: {
                    // mFlagRecord = !mFlagRecord;
                    // mControlProvider.record();
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
        public static void setFlagPlay(boolean flagPlay) {
            sFlagPlay = flagPlay;
        }

        public boolean getFlagPlay() {
            return sFlagPlay;
        }

        public void setFlagRecord(boolean flagRecord) {
            sFlagRecord = flagRecord;
        }

        public boolean isFlagRecord() {
            return sFlagRecord;
        }

        public static void setFileName(String fileName) {
            try {
                if (fileName.length() >= 5) {
                    fileName = fileName.substring(0, 4) + "...";
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
                if (fileDescription.length() >= 18) {
                    fileDescription = fileDescription.substring(0, 17) + "...";
                }
            } catch (Exception e) {
                Log.e(TAG, "Method: setFileDescription", e);
            }
            sFileDescription = fileDescription;
        }

        public String getFileDescription() {
            return sFileDescription;
        }

        public void setProgressValue(int value) {
            sProgressValue = value;
        }

        public void setSecondaryProgressValue(int value) {
            sSecondaryProgressValue = value;
        }

        public void setElapsedTime(int milliSeconds) {
            sElapsedTime = milliSeconds;
        }

        public void setDuration(int milliSeconds) {
            sDuration = milliSeconds;
        }

        // ////////////////////////////////////////////////////////////
        public void setFirstTime(int firstTime) {
            sFirstTime = firstTime;
        }

        public void setSecondTime(int secondTime) {
            sSecondTime = secondTime;
        }

        public void setFlagDiskFull(boolean flag) {
            sFlagDiskFull = flag;
        }

        public boolean isDiskFull() {
            return sFlagDiskFull;
        }

        public void setFlagDiskNearlyFull(boolean flag) {
            sFlagDiskNearlyFull = flag;
        }

        public boolean isDiskNearlyFull() {
            return sFlagDiskNearlyFull;
        }
    }

    public static void setControlProvider(ControlProviderPVR controlProvider) {
        A4TVProgressBarPVR.sControlProvider = controlProvider;
    }

    public static ControlProviderPVR getControlProviderPVR() {
        return sControlProvider;
    }

    public static int getControlRepeatPosition() {
        return sControlRepeatPosition;
    }

    public static int getControlPosition() {
        return sControlPosition;
    }

    public static void setControlPosition(int mControlPosition) {
        A4TVProgressBarPVR.sControlPosition = mControlPosition;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}