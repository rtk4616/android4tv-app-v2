package com.iwedia.gui.mainmenu.gallery.animations;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Handler;
import android.view.Gravity;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.mainmenu.MainMenuHandlingClass;

/**
 * Handler for transition animation
 * 
 * @author Veljko Ilkic
 */
public class TransitionItemAnimationHandler {
    /** Animation direction */
    public static final int ANIMATE_UP = 1;
    public static final int ANIMATE_DOWN = 2;
    /** Reference of main menu handler */
    private MainMenuHandlingClass mainMenuHandler;
    /** Reference of layout overlay in main menu */
    private FrameLayout mainMenuOverlay;
    /** Reference of submenu root image */
    private ImageView submenuRootImage;
    /** Zoom animation for submenu root */
    private Animation zoomAnimationRootSubmenu;

    /** Constructor 1 */
    public TransitionItemAnimationHandler(FrameLayout mainMenuOverlay,
            MainMenuHandlingClass mainMenuHandler) {
        super();
        // Take reference of layout overlay
        this.mainMenuOverlay = mainMenuOverlay;
        // Take reference of main menu handler
        this.mainMenuHandler = mainMenuHandler;
    }

    /** Initialize */
    public void init() {
        // Take reference of overlay layout
        submenuRootImage = (ImageView) mainMenuOverlay
                .findViewById(com.iwedia.gui.R.id.mainMenuRootSubmenu);
        // Hide submenu root icon on the beginning
        submenuRootImage.setBackgroundResource(0);
        setParams();
        // Load animation for root submenu element
        zoomAnimationRootSubmenu = AnimationUtils.loadAnimation(
                mainMenuHandler.getActivity(),
                com.iwedia.gui.R.anim.zoom_in_and_alpha);
    }

    /** Size and position of views */
    public void setParams() {
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                MainActivity.screenWidth / 12, MainActivity.screenWidth / 12,
                Gravity.CENTER_HORIZONTAL);
        params1.setMargins(0, MainActivity.screenHeight / 10, 0, 0);
        submenuRootImage.setLayoutParams(params1);
    }

    // /////////////////////////////
    // Getters and setters
    // /////////////////////////////
    /** Get image view of submenu root item */
    public ImageView getSubmenuRootImage() {
        return submenuRootImage;
    }

    // ////////////////////////////////////
    // Anim functions
    // ////////////////////////////////////
    /** Rotate view */
    public void translate(ImageView root, ImageView image, int direction) {
        // Start transition
        applyTransition(root, image, direction);
    }

    /** Apply rotation on image view */
    private void applyTransition(ImageView root, ImageView image,
            final int direction) {
        // For centered transition over Z plane
        final float centerXView = image.getWidth() / 2.0f;
        final float centerYView = image.getHeight() / 2.0f;
        // Find the center of image
        int[] cooridinatesCenter = new int[2];
        image.getLocationOnScreen(cooridinatesCenter);
        // Start Y value
        final float centerY = cooridinatesCenter[1] + image.getHeight() / 2.0f;
        // Coordinates of end
        int[] cooridinatesRoot = new int[2];
        root.getLocationOnScreen(cooridinatesRoot);
        // End Y value
        final float endY = cooridinatesRoot[1] + root.getHeight() / 2.0f;
        // Create animation set
        final AnimationSet animationSet = new AnimationSet(true);
        // Alpha animation
        final AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(500);
        alpha.setInterpolator(new AccelerateInterpolator());
        // Transition animation
        final TransitionAnimation transition = new TransitionAnimation(endY,
                centerY, centerXView, centerYView);
        transition.setDuration(500);
        transition.setInterpolator(new AccelerateInterpolator());
        // Attach animation listener and litener and wait for end of animation
        animationSet.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                if (direction == ANIMATE_DOWN) {
                    // Disable back key until animation is finished
                    MainKeyListener.enableKeyCodeBack = false;
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (direction == ANIMATE_UP) {
                    // Reset animation and view just in case
                    submenuRootImage.clearAnimation();
                    zoomAnimationRootSubmenu.reset();
                    // Start zoom animation
                    submenuRootImage.startAnimation(zoomAnimationRootSubmenu);
                    // Load menu new menu items
                    mainMenuHandler.loadMenuItems(MainMenuContent
                            .checkIdResourceNextSubmenu(
                                    MainMenuContent.submenuRootResId, true));
                    // Redraw main menu
                    mainMenuHandler.refreshMainMenu(true);
                } else {
                    mainMenuHandler.loadMenuItems(MainMenuContent
                            .checkIdResourceNextSubmenu(-1, false));
                    Handler delay = new Handler();
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Redraw main menu
                            mainMenuHandler.refreshMainMenu(false);
                            // Enable back key
                            MainKeyListener.enableKeyCodeBack = true;
                        }
                    }, 20);
                }
                // Set image of root menu
                submenuRootImage
                        .setBackgroundResource(MainMenuContent.submenuRootResId);
            }
        });
        // Add animation in set
        animationSet.addAnimation(alpha);
        animationSet.addAnimation(transition);
        // Start animations
        image.startAnimation(animationSet);
    }

    // /////////////////////////////////////////////////////////////////
    // Inner Classes for animation handling
    // /////////////////////////////////////////////////////////////////
    /** Custom Transition animation */
    private class TransitionAnimation extends Animation {
        private final float mToY;
        private final float mCenterY;
        private Camera mCamera;
        private float centerXView;
        private float centerYView;

        public TransitionAnimation(float toY, float centerY, float centerXView,
                float centerYView) {
            mToY = toY;
            mCenterY = centerY;
            this.centerXView = centerXView;
            this.centerYView = centerYView;
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
            final float fromY = mCenterY;
            float distance = ((fromY - mToY) * interpolatedTime);
            final Camera camera = mCamera;
            final Matrix matrix = t.getMatrix();
            camera.save();
            // Translate over Z and Y
            camera.translate(0, distance, distance);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-centerXView, -centerYView);
            matrix.postTranslate(centerXView, centerYView);
        }
    }
}
