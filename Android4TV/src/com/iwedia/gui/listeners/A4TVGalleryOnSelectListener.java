package com.iwedia.gui.listeners;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.mainmenu.MainMenuHandlingClass;
import com.iwedia.gui.mainmenu.gallery.A4TVCoverAdapterView;

/** OnItemSelect listnener */
public class A4TVGalleryOnSelectListener implements
        A4TVCoverAdapterView.OnItemSelectedListener {
    /** Reference to main activity */
    private Activity activity;
    /** Main Menu Handler class */
    private MainMenuHandlingClass mainMenuHandler;
    /** Previous selected item */
    public static View lastView = null;
    /** Current view */
    private ImageView currentSelectedViewGallery;
    /** Animation for zoom in */
    private Animation zoomInAndJumpSelected;

    /** Constructor 1 */
    public A4TVGalleryOnSelectListener(Activity activity) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of main menu handler
        this.mainMenuHandler = ((MainActivity) this.activity)
                .getMainMenuHandler();
        // Load animation for frame layout
        zoomInAndJumpSelected = AnimationUtils.loadAnimation(activity,
                com.iwedia.gui.R.anim.zoom_in_and_jump);
        // Init zoom in animation
        zoomInAndJumpSelected.reset();
    }

    public void onItemSelected(A4TVCoverAdapterView<?> parent, View view,
            final int position, long id) {
        // If last previous view exists do initialization
        if (null != lastView) {
            // Get previous view
            currentSelectedViewGallery = (ImageView) lastView;
            // Set visible previous element
            currentSelectedViewGallery.setVisibility(View.VISIBLE);
        }
        // Get current item in gallery
        currentSelectedViewGallery = (ImageView) view;
        if (currentSelectedViewGallery != null) {
            // Hide selected item in gallery element
            currentSelectedViewGallery.setVisibility(View.INVISIBLE);
        }
        // Because of gallery makes this view invisible on scroll
        mainMenuHandler.getFlip3dAnimationHandler().getSelectedItem()
                .setVisibility(View.VISIBLE);
        clearAnimationsManual();
        // Set selected item icon
        mainMenuHandler
                .getFlip3dAnimationHandler()
                .getImage()
                .setImageBitmap(
                        MainActivity.mMemoryCache
                                .loadBitmapFromResource(mainMenuHandler
                                        .getMenuItems().get(position)
                                        .getMenuImage()));
        // Start zoom in and jump of frame layout
        mainMenuHandler.getFlip3dAnimationHandler().getSelectedItem()
                .startAnimation(zoomInAndJumpSelected);
        // Invalidate image view element just in case
        mainMenuHandler.getFlip3dAnimationHandler().getImage().invalidate();
        // Delay starting of rotating animation
        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            public void run() {
                mainMenuHandler.getFlip3dAnimationHandler().rotate();
            }
        }, 50);
        // ////////////////////////////////////
        // Set text on item desctriptions
        // //////////////////////////////////////
        mainMenuHandler.getSelectedItemName().setText(
                mainMenuHandler.getMenuItems().get(position).getMenuItemName());
        mainMenuHandler.getSelectedItemDescription().setText(
                mainMenuHandler.getMenuItems().get(position)
                        .getMenuItemDescription());
        // Set the last view so we can clear the animation
        lastView = view;
        // ///////////////////////////////////////////////
        // Dots handler
        // ///////////////////////////////////////////////
        boolean changed = false;
        if (mainMenuHandler.getDotsHandler().getCurrentlySelected() == position - 1) {
            mainMenuHandler.getDotsHandler().increaseSelectedByOne();
            changed = true;
        }
        if (mainMenuHandler.getDotsHandler().getCurrentlySelected() == position + 1) {
            mainMenuHandler.getDotsHandler().decreaseSelectedByOne();
            changed = true;
        }
        if (!changed) {
            mainMenuHandler.getDotsHandler().setSelectedDot(position);
        }
    }

    public void onNothingSelected(A4TVCoverAdapterView<?> parent) {
    }

    /** Clear animations */
    public void clearAnimationsManual() {
        // Clear animation from frame layout and image view of selected item
        mainMenuHandler.getFlip3dAnimationHandler().getSelectedItem()
                .clearAnimation();
        mainMenuHandler.getFlip3dAnimationHandler().getImage().clearAnimation();
        if (mainMenuHandler.getFlip3dAnimationHandler().getRotation() != null) {
            // Reset animation states
            mainMenuHandler.getFlip3dAnimationHandler().getRotation().reset();
        }
        // Reset zooming animation
        zoomInAndJumpSelected.reset();
    }

    /** Manually start animations when needed */
    public void startAnimationsManual() {
        // Delay starting of rotating animation
        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            public void run() {
                // Start zoom in and jump of frame layout
                mainMenuHandler.getFlip3dAnimationHandler().getSelectedItem()
                        .startAnimation(zoomInAndJumpSelected);
                mainMenuHandler.getFlip3dAnimationHandler().rotate();
            }
        }, 50);
    }
}
