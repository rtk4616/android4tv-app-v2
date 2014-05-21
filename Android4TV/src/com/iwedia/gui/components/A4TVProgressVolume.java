package com.iwedia.gui.components;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ProgressBar;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;

public class A4TVProgressVolume extends ProgressBar {
    private Paint mPaint = null;
    private Rect mBounds = null;
    private Bitmap mImageAudio = null;
    private Activity mActivity = null;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mTextSize = 0;
    private int mThickness = 0;
    private int[] mAudioImagesResources = { R.drawable.volume_icon_mute,
            R.drawable.volume_icon_10, R.drawable.volume_icon_30,
            R.drawable.volume_icon_60, R.drawable.volume_icon_max };

    public A4TVProgressVolume(Activity activity, AttributeSet attrs,
            int defStyle, int width, int height) {
        super(activity, attrs, defStyle);
        this.mActivity = activity;
        this.mWidth = width;
        this.mHeight = height;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new Rect();
        getAudioImage();
        mTextSize = (22 * height) / 100;
        mThickness = (17 * height) / 100;
        if (mImageAudio != null)
            setPadding((6 * mImageAudio.getWidth()) / 3, mTextSize, 0, mHeight
                    - (2 * mTextSize) + mThickness);
        initPaint();
        setProgressDrawable(activity.getResources().getDrawable(
                R.drawable.a4tv_curl_progressbar_drawable_ics));
    }

    private void initPaint() {
        Typeface tf = Typeface.create("Roboto", Typeface.BOLD);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(0xFF01B7EF);
        mPaint.setTypeface(tf);
    }

    private void getAudioImage() {
        int iPogress = getProgress();
        if (iPogress == 0) {
            mImageAudio = BitmapFactory.decodeResource(
                    mActivity.getResources(), mAudioImagesResources[0]);
        } else if (iPogress > 0 && iPogress <= 10) {
            mImageAudio = BitmapFactory.decodeResource(
                    mActivity.getResources(), mAudioImagesResources[1]);
        } else if (iPogress > 10 && iPogress <= 30) {
            mImageAudio = BitmapFactory.decodeResource(
                    mActivity.getResources(), mAudioImagesResources[2]);
        } else if (iPogress > 30 && iPogress <= 60) {
            mImageAudio = BitmapFactory.decodeResource(
                    mActivity.getResources(), mAudioImagesResources[3]);
        } else if (iPogress > 60 && iPogress <= 100) {
            mImageAudio = BitmapFactory.decodeResource(
                    mActivity.getResources(), mAudioImagesResources[4]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height;
        // Get Image
        getAudioImage();
        // Draw Volume Icon
        if (mImageAudio != null)
            canvas.drawBitmap(mImageAudio, (2 * mImageAudio.getWidth()) / 3,
                    mTextSize - (mImageAudio.getHeight() / 2)
                            + ((10 * mImageAudio.getHeight()) / 100), mPaint);
        // Draw Text
        String strProgress;
        int iPogress = getProgress();
        int iWidth = 0;
        if (iPogress == 0) {
            strProgress = "Mute";
            mPaint.getTextBounds(strProgress, 0, strProgress.length(), mBounds);
            if (mImageAudio != null)
                iWidth = (mWidth / 2) + ((27 * mImageAudio.getWidth()) / 12)
                        - mBounds.width();
            height = mBounds.height();
        } else if (iPogress == 100) {
            strProgress = "Max";
            mPaint.getTextBounds(strProgress, 0, strProgress.length(), mBounds);
            if (mImageAudio != null)
                iWidth = (mWidth / 2) + ((11 * mImageAudio.getWidth()) / 6)
                        - mBounds.width();
            height = mBounds.height();
        } else if (iPogress > 0 && iPogress <= 9) {
            String strHeight = strProgress = "M";
            mPaint.getTextBounds(strHeight, 0, strHeight.length(), mBounds);
            height = mBounds.height();
            strProgress = String.valueOf(iPogress);
            if (mImageAudio != null) {
                iWidth = (mWidth / 2) + ((20 * mImageAudio.getWidth()) / 24);
            }
        } else {
            String strHeight = strProgress = "M";
            mPaint.getTextBounds(strHeight, 0, strHeight.length(), mBounds);
            height = mBounds.height();
            strProgress = String.valueOf(iPogress);
            if (mImageAudio != null) {
                iWidth = (mWidth / 2) + ((11 * mImageAudio.getWidth()) / 24);
            }
        }
        canvas.drawText(strProgress, iWidth, height, mPaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        return super.onKeyDown(keyCode, event);
    }
}
