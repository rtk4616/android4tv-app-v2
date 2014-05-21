package com.iwedia.gui.osd.curleffect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.ArrayList;

/**
 * Class for Updating Textures
 */
public class PageProvider implements CurlView.PageProvider, OSDGlobal {
    /** Number of pages who can be seen. */
    private final int NUMBER_OF_PAGES = 2;
    private Activity mActivity = null;
    private CreateBitmaps mCreateBitmaps = null;
    private ArrayList<String> mStrValues = null;
    private ArrayList<Boolean> mScrambledValues = null;
    private ArrayList<Integer> mImageIds = null;
    private Bitmap mFront = null;
    private Bitmap mBack = null;
    private static String mChannelIconPath = "";
    private int mProgressValue = 0;

    public PageProvider(Activity activity) {
        this.mActivity = activity;
        // Initialize Progress
        mProgressValue = 0;
        mCreateBitmaps = new CreateBitmaps();
    }

    public int getPageCount() {
        return NUMBER_OF_PAGES;
    }

    public void updatePage(CurlPage page, int width, int height, int index,
            int state) {
        if (index == 0) {
            mFront = null;
            page.setColor(Color.TRANSPARENT, CurlPage.SIDE_FRONT);
            page.setTexture(mFront, CurlPage.SIDE_FRONT);
            mBack = mCreateBitmaps.makeEmptyCurlSideBackTexture(mActivity,
                    width, height);
            page.setColor(Color.WHITE, CurlPage.SIDE_BACK);
            page.setTexture(mBack, CurlPage.SIDE_BACK);
        } else {
            // Second Page
            updateFrontSideOfSecondPage(page, width, height, state);
        }
    }

    @Override
    public void updateFrontSideOfFirstPage(CurlPage page, int width,
            int height, int state) {
        switch (state) {
            case STATE_INFO: {
                mFront = mCreateBitmaps.prepareInfoTexture(mActivity, width,
                        height);
                break;
            }
            case STATE_CHANGE_CHANNEL: {
                mFront = mCreateBitmaps.prepareChannelChangeTexture(mActivity,
                        width, height);
                clearBackSideOfFirstPage(page, width, height);
                break;
            }
            default: {
                mFront = null;
                break;
            }
        }
        if (mFront != null) {
            page.setColor(Color.WHITE, CurlPage.SIDE_FRONT);
        } else {
            page.setColor(Color.TRANSPARENT, CurlPage.SIDE_FRONT);
        }
        page.setTexture(mFront, CurlPage.SIDE_FRONT);
    }

    @Override
    public void updateBackSideOfFirstPage(CurlPage page, int width, int height) {
        mBack = mCreateBitmaps.makeCurlBackSideTexture(width, height,
                mActivity, mImageIds, mChannelIconPath);
        page.setColor(Color.WHITE, CurlPage.SIDE_BACK);
        page.setTexture(mBack, CurlPage.SIDE_BACK);
    }

    @Override
    public void clearBackSideOfFirstPage(CurlPage page, int width, int height) {
        mBack = mCreateBitmaps.makeEmptyCurlSideBackTexture(mActivity, width,
                height);
        page.setColor(Color.WHITE, CurlPage.SIDE_BACK);
        page.setTexture(mBack, CurlPage.SIDE_BACK);
    }

    @Override
    public void updateFrontSideOfSecondPage(CurlPage page, int width,
            int height, int state) {
        switch (state) {
            case STATE_CHANGE_CHANNEL: {
                if (!A4TVProgressBarPVR.getControlProviderPVR().isFlagRecord()) {
                    mFront = mCreateBitmaps.makeTextureChannelChange(width,
                            height, mActivity, mStrValues);
                }
                break;
            }
            case STATE_CHANNEL_INFO: {
                mFront = mCreateBitmaps
                        .makeTextureChannelInfo(width, height, mActivity,
                                mProgressValue, mStrValues, mScrambledValues);
                break;
            }
            case STATE_INPUT_INFO: {
                mFront = mCreateBitmaps.makeTextureInputs(width, height,
                        mActivity, mStrValues);
                break;
            }
            case STATE_NUMEROUS_CHANGE_CHANNEL: {
                if (!A4TVProgressBarPVR.getControlProviderPVR().isFlagRecord()) {
                    mFront = mCreateBitmaps.makeTextureNumChannelChange(width,
                            height, mActivity, mStrValues);
                }
                break;
            }
            case STATE_VOLUME: {
                mFront = mCreateBitmaps.makeTextureVolume(width, height,
                        mActivity, mStrValues);
                break;
            }
            case STATE_PVR: {
                mFront = mCreateBitmaps
                        .makeTexturePVR(width, height, mActivity);
                break;
            }
            case STATE_MULTIMEDIA_CONTROLLER: {
                mFront = mCreateBitmaps.makeTextureMultimediaControl(width,
                        height, mActivity);
                break;
            }
            case STATE_INFO: {
                mFront = mCreateBitmaps.makeInfoTexture(mActivity, width,
                        height);
                break;
            }
            case STATE_PICTURE_FORMAT: {
                mFront = mCreateBitmaps.makeTexturePictureFormat(width, height,
                        mActivity, mStrValues);
                break;
            }
            default: {
                mFront = mCreateBitmaps.makeEmptyCurlSideBackTexture(mActivity,
                        width, height);
                break;
            }
        }
        page.setColor(Color.WHITE, CurlPage.SIDE_FRONT);
        page.setTexture(mFront, CurlPage.SIDE_FRONT);
        /** Don't Create BackSide Of Second Page */
        page.setColor(Color.WHITE, CurlPage.SIDE_BACK);
        page.setTexture(null, CurlPage.SIDE_BACK);
    }

    public ArrayList<String> getStrValues() {
        return mStrValues;
    }

    public void setStrValues(ArrayList<String> strValues) {
        this.mStrValues = strValues;
    }

    public void setScrambledValues(ArrayList<Boolean> scrambledValues) {
        this.mScrambledValues = scrambledValues;
    }

    public ArrayList<Integer> getImageIds() {
        return mImageIds;
    }

    public void setImageIds(ArrayList<Integer> imageIds) {
        this.mImageIds = imageIds;
    }

    public void setChannelIconPath(String channelIconUri) {
        mChannelIconPath = channelIconUri;
    }

    public void setProgressValue(int mProgressValue) {
        this.mProgressValue = mProgressValue;
    }

    public CreateBitmaps getCreateBitmaps() {
        return mCreateBitmaps;
    }
}
