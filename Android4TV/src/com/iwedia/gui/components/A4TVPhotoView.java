/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes. Licensed under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 *******************************************************************************/
package com.iwedia.gui.components;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class A4TVPhotoView extends ImageView implements
        ViewTreeObserver.OnGlobalLayoutListener {
    private float mScale = 1;
    private WeakReference<ImageView> mImageView;
    // These are set so we don't keep allocating them on the heap
    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();
    private int mIvTop, mIvRight, mIvBottom, mIvLeft;

    public A4TVPhotoView(Context context) {
        this(context, null);
    }

    public A4TVPhotoView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public A4TVPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        super.setScaleType(ScaleType.MATRIX);
        init(this);
    }

    public void init(ImageView imageView) {
        mImageView = new WeakReference<ImageView>(imageView);
        ViewTreeObserver observer = imageView.getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(this);
        }
        // Finally, update the UI so that we're zoomable
        update();
    }

    protected void onDetachedFromWindow() {
        cleanup();
        super.onDetachedFromWindow();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        update();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        update();
    }

    @SuppressWarnings("deprecation")
    public final void cleanup() {
        if (null == mImageView) {
            return; // cleanup already done
        }
        final ImageView imageView = mImageView.get();
        if (null != imageView) {
            // Remove this as a global layout listener
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (null != observer && observer.isAlive()) {
                observer.removeGlobalOnLayoutListener(this);
            }
        }
        // Finally, clear ImageView
        mImageView = null;
    }

    public boolean setDisplayMatrix(Matrix finalMatrix) {
        if (finalMatrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }
        ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }
        if (null == imageView.getDrawable()) {
            return false;
        }
        mSuppMatrix.set(finalMatrix);
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
        return true;
    }

    public final ImageView getImageView() {
        ImageView imageView = null;
        if (null != mImageView) {
            imageView = mImageView.get();
        }
        // If we don't have an ImageView, call cleanup()
        if (null == imageView) {
            cleanup();
        }
        return imageView;
    }

    public boolean scale() {
        ImageView imageView = getImageView();
        float x = getImageViewWidth(imageView) / 2;
        float y = getImageViewHeight(imageView) / 2;
        setScale(mScale, x, y);
        return true;
    }

    @Override
    public final void onGlobalLayout() {
        ImageView imageView = getImageView();
        if (null != imageView) {
            final int top = imageView.getTop();
            final int right = imageView.getRight();
            final int bottom = imageView.getBottom();
            final int left = imageView.getLeft();
            if (top != mIvTop || bottom != mIvBottom || left != mIvLeft
                    || right != mIvRight) {
                // Update our base matrix, as the bounds have changed
                updateBaseMatrix(imageView.getDrawable());
                // Update values as something has changed
                mIvTop = top;
                mIvRight = right;
                mIvBottom = bottom;
                mIvLeft = left;
            }
        }
    }

    public void setScale(float scale) {
        mScale = scale;
    }

    public void setScale(float scale, float focalX, float focalY) {
        ImageView imageView = getImageView();
        if (null != imageView) {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    public final void update() {
        ImageView imageView = getImageView();
        if (null != imageView) {
            updateBaseMatrix(imageView.getDrawable());
        }
    }

    protected Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    /**
     * Helper method that simply checks the Matrix, and then displays the result
     */
    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageViewMatrix(getDrawMatrix());
        }
    }

    private void checkImageViewScaleType() {
        ImageView imageView = getImageView();
        /**
         * PhotoView's getScaleType() will just divert to this.getScaleType() so
         * only call if we're not attached to a PhotoView.
         */
        if (null != imageView && !(imageView instanceof A4TVPhotoView)) {
            if (!ScaleType.MATRIX.equals(imageView.getScaleType())) {
                throw new IllegalStateException(
                        "The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher");
            }
        }
    }

    private boolean checkMatrixBounds() {
        final ImageView imageView = getImageView();
        if (null == imageView) {
            return false;
        }
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }
        final float height = rect.height(), width = rect.width();
        float deltaX = 0, deltaY = 0;
        final int viewHeight = getImageViewHeight(imageView);
        if (height <= viewHeight) {
            deltaY = (viewHeight - height) / 2 - rect.top;
        } else if (rect.top > 0) {
            deltaY = -rect.top;
        } else if (rect.bottom < viewHeight) {
            deltaY = viewHeight - rect.bottom;
        }
        final int viewWidth = getImageViewWidth(imageView);
        if (width <= viewWidth) {
            deltaX = (viewWidth - width) / 2 - rect.left;
        } else if (rect.left > 0) {
            deltaX = -rect.left;
        } else if (rect.right < viewWidth) {
            deltaX = viewWidth - rect.right;
        }
        // Finally actually translate the matrix
        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     * 
     * @param matrix
     *        - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        ImageView imageView = getImageView();
        if (null != imageView) {
            Drawable d = imageView.getDrawable();
            if (null != d) {
                mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                        d.getIntrinsicHeight());
                matrix.mapRect(mDisplayRect);
                return mDisplayRect;
            }
        }
        return null;
    }

    /**
     * Resets the Matrix back to FIT_CENTER, and then displays it.s
     */
    private void resetMatrix() {
        mSuppMatrix.reset();
        setImageViewMatrix(getDrawMatrix());
        checkMatrixBounds();
    }

    private void setImageViewMatrix(Matrix matrix) {
        ImageView imageView = getImageView();
        if (null != imageView) {
            checkImageViewScaleType();
            imageView.setImageMatrix(matrix);
        }
    }

    /**
     * Calculate Matrix for FIT_CENTER
     * 
     * @param d
     *        - Drawable being displayed
     */
    private void updateBaseMatrix(Drawable d) {
        ImageView imageView = getImageView();
        if (null == imageView || null == d) {
            return;
        }
        final float viewWidth = getImageViewWidth(imageView);
        final float viewHeight = getImageViewHeight(imageView);
        final int drawableWidth = d.getIntrinsicWidth();
        final int drawableHeight = d.getIntrinsicHeight();
        mBaseMatrix.reset();
        RectF mTempSrc = new RectF(0, 0, drawableWidth, drawableHeight);
        RectF mTempDst = new RectF(0, 0, viewWidth, viewHeight);
        mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER);
        resetMatrix();
    }

    private int getImageViewWidth(ImageView imageView) {
        if (null == imageView) {
            return 0;
        }
        return imageView.getWidth() - imageView.getPaddingLeft()
                - imageView.getPaddingRight();
    }

    private int getImageViewHeight(ImageView imageView) {
        if (null == imageView) {
            return 0;
        }
        return imageView.getHeight() - imageView.getPaddingTop()
                - imageView.getPaddingBottom();
    }
}