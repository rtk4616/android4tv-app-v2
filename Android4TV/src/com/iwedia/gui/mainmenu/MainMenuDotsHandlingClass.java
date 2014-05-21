package com.iwedia.gui.mainmenu;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVMainMenuDot;

import java.util.ArrayList;

/**
 * Class for handling main menu dots (number of them and selection)
 * 
 * @author Branimir Pavlovic
 */
public class MainMenuDotsHandlingClass {
    // private final String TAG = "A4TV2.0";
    private MainActivity activity;
    private LayoutInflater inflater;
    /** Layout that holds dots */
    private LinearLayout layoutForDots;
    private ArrayList<LinearLayout> dots;
    private int currentlySelected;

    /**
     * Default constructor
     * 
     * @param activity
     */
    public MainMenuDotsHandlingClass(MainActivity activity) {
        this.activity = activity;
        layoutForDots = (LinearLayout) this.activity.getMainMenuHandler()
                .getMainMenuDialog()
                .findViewById(R.id.linearLayoutForIconsDots);
        dots = new ArrayList<LinearLayout>();
        setParams();
    }

    /** Set size and position of views */
    public void setParams() {
        // Layout params for dots containter
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        params1.bottomMargin = MainActivity.screenHeight / 6;
        layoutForDots.setLayoutParams(params1);
    }

    /**
     * Function for initial setting of dots
     * 
     * @param number
     *        Total number of dots
     */
    public void setNumberOfDots(int number) {
        dots.clear();
        layoutForDots.removeAllViews();
        currentlySelected = 0;
        for (int i = 0; i < number; i++) {
            LinearLayout lay = inflateDot();
            layoutForDots.addView(lay);
            dots.add(lay);
        }
        layoutForDots.invalidate();
    }

    private LinearLayout inflateDot() {
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dot,
                null);
        A4TVMainMenuDot dot = (A4TVMainMenuDot) layout
                .findViewById(R.id.aTVMainMenuDotItem);
        dot.setFocusable(false);
        dot.setEnabled(false);
        dot.setSelected(false);
        return layout;
    }

    /**
     * Function for setting selection to desired dot
     * 
     * @param position
     *        Position of selection
     */
    public void setSelectedDot(int position) {
        if (dots != null) {
            if (dots.size() > 0) {
                for (int i = 0; i < dots.size(); i++) {
                    A4TVMainMenuDot dot = (A4TVMainMenuDot) dots.get(i)
                            .findViewById(R.id.aTVMainMenuDotItem);
                    if (i == position) {
                        dot.setSelected(true);
                    } else {
                        dot.setSelected(false);
                    }
                }
                currentlySelected = position;
            }
        }
    }

    /**
     * Function for increasing selection of dots
     */
    public void increaseSelectedByOne() {
        A4TVMainMenuDot dot = (A4TVMainMenuDot) dots.get(currentlySelected)
                .findViewById(R.id.aTVMainMenuDotItem);
        dot.setSelected(false);
        dot = (A4TVMainMenuDot) dots.get(currentlySelected + 1).findViewById(
                R.id.aTVMainMenuDotItem);
        dot.setSelected(true);
        currentlySelected++;
    }

    /**
     * Function for decreasing selection of dots
     */
    public void decreaseSelectedByOne() {
        A4TVMainMenuDot dot = (A4TVMainMenuDot) dots.get(currentlySelected)
                .findViewById(R.id.aTVMainMenuDotItem);
        dot.setSelected(false);
        dot = (A4TVMainMenuDot) dots.get(currentlySelected - 1).findViewById(
                R.id.aTVMainMenuDotItem);
        dot.setSelected(true);
        currentlySelected--;
    }

    public int getCurrentlySelected() {
        return currentlySelected;
    }

    public void setCurrentlySelected(int currentlySelected) {
        this.currentlySelected = currentlySelected;
    }
}
