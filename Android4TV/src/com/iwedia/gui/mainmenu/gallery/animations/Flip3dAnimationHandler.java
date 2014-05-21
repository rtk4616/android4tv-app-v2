package com.iwedia.gui.mainmenu.gallery.animations;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.iwedia.gui.MainActivity;

/**
 * Handler class for selected item animation
 * 
 * @author Veljko Ilkic
 */
public class Flip3dAnimationHandler {
    /** Selected item in main menu */
    private FrameLayout selectedItem;
    /** Two items of selected view in main menu */
    private ImageView image;
    private ImageView imageBackground;
    /** Vertical layout for main menu */
    private FrameLayout mainMenuOverlay;
    /** Rotation animation of image in selected frame layout */
    private Flip3dAnimation rotation;

    /** Constructor 1 */
    public Flip3dAnimationHandler(FrameLayout mainMenuOverlay) {
        super();
        // Take reference of overlay layout
        this.mainMenuOverlay = mainMenuOverlay;
    }

    /** Init animation views */
    public void init() {
        // References of current selected item
        if (mainMenuOverlay != null)
            this.selectedItem = (FrameLayout) this.mainMenuOverlay
                    .findViewById(com.iwedia.gui.R.id.mainMenuSelectedFrameLayout);
        if (selectedItem != null) {
            this.selectedItem.setVisibility(View.INVISIBLE);
        }
        // References of images in frame layout
        if (mainMenuOverlay != null)
            this.imageBackground = (ImageView) this.mainMenuOverlay
                    .findViewById(com.iwedia.gui.R.id.mainMenuSelectedImageBackground);
        if (selectedItem != null)
            this.image = (ImageView) this.selectedItem
                    .findViewById(com.iwedia.gui.R.id.mainMenuSelectedImageView);
        // Set size and params of views
        setParams();
    }

    /** Set Layout params on views */
    public void setParams() {
        // Layout params for overlay layout
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                MainActivity.screenWidth / 5,
                FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        if (selectedItem != null) {
            selectedItem.setLayoutParams(params1);
        }
        // Layout params for background of selected item
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                MainActivity.screenWidth / 5, MainActivity.screenWidth / 5,
                Gravity.CENTER);
        if (imageBackground != null) {
            imageBackground.setLayoutParams(params2);
        }
        // Layout params for image of selected item
        FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(
                MainActivity.screenWidth / 6, MainActivity.screenWidth / 6,
                Gravity.CENTER);
        if (image != null) {
            image.setLayoutParams(params3);
        }
    }

    /** Rotate view */
    public void rotate() {
        // Rotate whole circle
        applyRotation(0, 360);
    }

    /** Apply rotation on image view */
    private void applyRotation(float start, float end) {
        // Find the center of image
        final float centerX = image.getWidth() / 2.0f;
        final float centerY = image.getHeight() / 2.0f;
        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        rotation = new Flip3dAnimation(start, end, centerX, centerY);
        rotation.setDuration(500);
        rotation.setRepeatCount(Animation.INFINITE);
        rotation.setStartOffset(1000);
        rotation.setInterpolator(new AccelerateInterpolator());
        image.startAnimation(rotation);
    }

    // /////////////////////////////////////////////////
    // Getters and setters
    // /////////////////////////////////////////////////
    /** Frame layout of current selected item */
    public FrameLayout getSelectedItem() {
        return selectedItem;
    }

    /** Image in frame layout */
    public ImageView getImage() {
        return image;
    }

    /** Set image */
    public void setImage(ImageView image) {
        this.image = image;
    }

    /** Get rotating animation reference */
    public Flip3dAnimation getRotation() {
        return rotation;
    }

    // /////////////////////////////////////////////////////////////////
    // Inner Classes for animation handling
    // /////////////////////////////////////////////////////////////////
    /** Custom 3d animation */
    public class Flip3dAnimation extends Animation {
        private final float mFromDegrees;
        private final float mToDegrees;
        private final float mCenterX;
        private final float mCenterY;
        private Camera mCamera;

        public Flip3dAnimation(float fromDegrees, float toDegrees,
                float centerX, float centerY) {
            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mCenterX = centerX;
            mCenterY = centerY;
        }

        @Override
        public void initialize(int width, int height, int parentWidth,
                int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
        }

        @Override
        protected void applyTransformation(float interpolatedTime,
                Transformation t) {
            final float fromDegrees = mFromDegrees;
            float degrees = fromDegrees
                    + ((mToDegrees - fromDegrees) * interpolatedTime);
            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;
            final Matrix matrix = t.getMatrix();
            camera.save();
            // Rotate over Y
            camera.rotateY(degrees);
            // Translate over Z
            camera.translate(0, 0, degrees / (10.0f));
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }

        @Override
        public void reset() {
            super.reset();
        }
    }
}
