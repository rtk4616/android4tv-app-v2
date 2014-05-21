package com.iwedia.gui.osd.curleffect;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;

import com.iwedia.gui.osd.OSDGlobal;

/**
 * Storage class for page textures, blend colors and possibly some other values
 * in the future.
 */
public class CurlPage implements OSDGlobal {
    public static final int SIDE_BACK = 2;
    public static final int SIDE_BOTH = 3;
    public static final int SIDE_FRONT = 1;
    private int mColorBack;
    private int mColorFront;
    private Bitmap mTextureBack;
    private Bitmap mTextureFront;
    private boolean mTexturesChanged;

    /** Default constructor. */
    public CurlPage() {
        reset();
    }

    /** Getter for color. */
    public int getColor(int side) {
        switch (side) {
            case SIDE_FRONT:
                return mColorFront;
            default:
                return mColorBack;
        }
    }

    /**
     * Get texture
     * 
     * @param textureRect
     *        to reset
     * @param side
     *        From what side to get texture
     * @return Appropriate texture
     */
    public Bitmap getTexture(RectF textureRect, int side) {
        textureRect.set(0f, 0f, 1f, 1f);
        switch (side) {
            case SIDE_FRONT:
                return mTextureFront;
            default:
                return mTextureBack;
        }
    }

    /** Recycles and frees underlying Bitmaps. */
    public void recycle() {
        if (null != mTextureFront) {
            mTextureFront.recycle();
        }
        mTextureFront = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        mTextureFront.eraseColor(mColorFront);
        if (null != mTextureBack) {
            mTextureBack.recycle();
        }
        mTextureBack = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        mTextureBack.eraseColor(mColorBack);
        mTexturesChanged = false;
    }

    /** Resets this CurlPage into its initial state. */
    public void reset() {
        mColorBack = Color.WHITE;
        mColorFront = Color.TRANSPARENT;
        recycle();
    }

    /** Setter blend color. */
    public void setColor(int color, int side) {
        switch (side) {
            case SIDE_FRONT:
                mColorFront = color;
                break;
            case SIDE_BACK:
                mColorBack = color;
                break;
            default:
                mColorFront = color;
                mColorBack = color;
                break;
        }
    }

    /**
     * Erase color from textures
     */
    public void getCleanBitmap() {
        mTextureFront = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        mTextureFront.eraseColor(mColorFront);
        mTextureBack = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
        mTextureBack.eraseColor(mColorBack);
        mTexturesChanged = false;
    }

    /** Setter for textures. */
    public void setTexture(Bitmap texture, int side) {
        switch (side) {
            case SIDE_FRONT:
                mTextureFront = texture;
                break;
            case SIDE_BACK:
                mTextureBack = texture;
                break;
            case SIDE_BOTH:
                mTextureFront = texture;
                mTextureBack = texture;
                break;
        }
        mTexturesChanged = true;
    }

    public boolean hasBackTexture() {
        if (null != mTextureFront && null != mTextureBack)
            return !mTextureFront.equals(mTextureBack);
        if (null != mTextureBack)
            return true;
        return false;
    }

    public boolean isTexturesChanged() {
        return mTexturesChanged;
    }
}