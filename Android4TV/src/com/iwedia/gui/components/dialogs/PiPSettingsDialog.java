package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.A4TVVideoView;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

public class PiPSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    // TODO: How to generate unique ID!?
    public static final int PIP_SETTINGS_SET_POSITION = 10001;
    public static final int PIP_SETTINGS_SET_SIZE = 10002;
    public static final int PIP_SETTINGS_CUSTOM_X_POSITION = 10003;
    public static final int PIP_SETTINGS_CUSTOM_Y_POSITION = 10004;
    public static final int PIP_SETTINGS_CUSTOM_POSITION_APPLY = 10005;
    public static final int PIP_SETTINGS_CUSTOM_WIDTH = 10006;
    public static final int PIP_SETTINGS_CUSTOM_HEIGHT = 10007;
    public static final int PIP_SETTINGS_CUSTOM_SIZE_APPLY = 10008;
    private Context ctx;
    private A4TVSpinner pipPositionSpinner, pipSizeSpinner;
    private A4TVEditText pipCustomXposition, pipCustomYPosition,
            pipCustomWidth, pipCustomHeight;
    private A4TVButton pipCustomPositionApply, pipCustomSizeApply;
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();

    public PiPSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case PIP_SETTINGS_CUSTOM_SIZE_APPLY: {
                int pip_width = Integer.parseInt(pipCustomWidth.getText()
                        .toString());
                int pip_height = Integer.parseInt(pipCustomHeight.getText()
                        .toString());
                Log.d(TAG, "width = " + pip_width + " height = " + pip_height);
                if (isSizeValid(pip_width, pip_height)) {
                    MainActivity.sharedPrefs.edit()
                            .putInt(MainActivity.PIP_SIZE, 2).commit();
                    MainActivity.sharedPrefs.edit()
                            .putInt(MainActivity.PIP_WIDTH, pip_width).commit();
                    MainActivity.sharedPrefs.edit()
                            .putInt(MainActivity.PIP_HEIGHT, pip_height)
                            .commit();
                    MainActivity.activity.updatePIPCoordinates();
                    updatePiPWindow();
                } else {
                    new A4TVToast(ctx).showToast(R.string.pip_size_not_valid);
                }
            }
                break;
            case PIP_SETTINGS_CUSTOM_POSITION_APPLY: {
                int pip_x = Integer.parseInt(pipCustomXposition.getText()
                        .toString());
                int pip_y = Integer.parseInt(pipCustomYPosition.getText()
                        .toString());
                Log.d(TAG, "X = " + pip_x + " Y = " + pip_y);
                if (isPositionValid(pip_x, pip_y)) {
                    MainActivity.sharedPrefs.edit()
                            .putInt(MainActivity.PIP_POSITION, 4).commit();
                    MainActivity.sharedPrefs.edit()
                            .putInt(MainActivity.PIP_X, pip_x).commit();
                    MainActivity.sharedPrefs.edit()
                            .putInt(MainActivity.PIP_Y, pip_y).commit();
                    MainActivity.activity.updatePIPCoordinates();
                    updatePiPWindow();
                } else {
                    new A4TVToast(ctx)
                            .showToast(R.string.pip_position_not_valid);
                }
            }
                break;
            default:
                break;
        }
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, null);
        setContentView(view);
    }

    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    /** Fill views with data */
    private void fillViews() {
        int spinnerSelector;
        // Get stored value for PIP position. If not defined than take default:
        // upper left
        spinnerSelector = MainActivity.sharedPrefs.getInt(
                MainActivity.PIP_POSITION, 0);
        pipPositionSpinner.setSelection(spinnerSelector);
        if (spinnerSelector < 4) {
            showCustomPositionFields(false);
        }
        // Get stored value for PIP size. If not defined than take default: 1/9
        spinnerSelector = MainActivity.sharedPrefs.getInt(
                MainActivity.PIP_SIZE, 0);
        pipSizeSpinner.setSelection(spinnerSelector);
        if (spinnerSelector < 2) {
            showCustomSizeFields(false);
        }
        MainActivity.activity.updatePIPCoordinates();
        updateCustomFields();
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialog });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }

    private void init() {
        /** Spinners */
        pipPositionSpinner = (A4TVSpinner) findViewById(PIP_SETTINGS_SET_POSITION);
        pipSizeSpinner = (A4TVSpinner) findViewById(PIP_SETTINGS_SET_SIZE);
        /** Edit text fields */
        pipCustomXposition = (A4TVEditText) findViewById(PIP_SETTINGS_CUSTOM_X_POSITION);
        pipCustomXposition.setInputType(InputType.TYPE_CLASS_NUMBER);
        pipCustomYPosition = (A4TVEditText) findViewById(PIP_SETTINGS_CUSTOM_Y_POSITION);
        pipCustomYPosition.setInputType(InputType.TYPE_CLASS_NUMBER);
        pipCustomWidth = (A4TVEditText) findViewById(PIP_SETTINGS_CUSTOM_WIDTH);
        pipCustomWidth.setInputType(InputType.TYPE_CLASS_NUMBER);
        pipCustomHeight = (A4TVEditText) findViewById(PIP_SETTINGS_CUSTOM_HEIGHT);
        pipCustomHeight.setInputType(InputType.TYPE_CLASS_NUMBER);
        /** Buttons */
        pipCustomPositionApply = (A4TVButton) findViewById(PIP_SETTINGS_CUSTOM_POSITION_APPLY);
        pipCustomSizeApply = (A4TVButton) findViewById(PIP_SETTINGS_CUSTOM_SIZE_APPLY);
        /** OnClick listeners */
        pipPositionSpinner
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        if (index < 4) {
                            showCustomPositionFields(false);
                            MainActivity.sharedPrefs.edit()
                                    .putInt(MainActivity.PIP_POSITION, index)
                                    .commit();
                            MainActivity.activity.updatePIPCoordinates();
                            updateCustomFields();
                            updatePiPWindow();
                        } else {
                            /** Custom size settings */
                            Log.d(TAG, "PiP custom position");
                            showCustomPositionFields(true);
                        }
                    }
                });
        pipSizeSpinner
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        if (index < 2) {
                            MainActivity.sharedPrefs.edit()
                                    .putInt(MainActivity.PIP_SIZE, index)
                                    .commit();
                            showCustomSizeFields(false);
                            MainActivity.activity.updatePIPCoordinates();
                            updateCustomFields();
                            updatePiPWindow();
                        } else {
                            /** Custom size settings */
                            Log.d(TAG, "PiP custom size");
                            showCustomSizeFields(true);
                        }
                    }
                });
    }

    private void showCustomSizeFields(boolean show) {
        if (show) {
            setLayoutDisplayMode(R.string.pip_width, DisplayMode.SHOW);
            setLayoutDisplayMode(R.string.pip_height, DisplayMode.SHOW);
            setLayoutDisplayMode(R.string.pip_size_apply, DisplayMode.SHOW);
        } else {
            setLayoutDisplayMode(R.string.pip_width, DisplayMode.DISABLE);
            setLayoutDisplayMode(R.string.pip_height, DisplayMode.DISABLE);
            setLayoutDisplayMode(R.string.pip_size_apply, DisplayMode.DISABLE);
        }
    }

    private void showCustomPositionFields(boolean show) {
        if (show) {
            setLayoutDisplayMode(R.string.pip_x_coordinate, DisplayMode.SHOW);
            setLayoutDisplayMode(R.string.pip_y_coordinate, DisplayMode.SHOW);
            setLayoutDisplayMode(R.string.pip_coordinate_apply,
                    DisplayMode.SHOW);
        } else {
            setLayoutDisplayMode(R.string.pip_x_coordinate, DisplayMode.DISABLE);
            setLayoutDisplayMode(R.string.pip_y_coordinate, DisplayMode.DISABLE);
            setLayoutDisplayMode(R.string.pip_coordinate_apply,
                    DisplayMode.DISABLE);
        }
    }

    public void updateCustomFields() {
        pipCustomXposition.setText(Integer
                .toString(MainActivity.pipWindowCoordinateLeft));
        pipCustomYPosition.setText(Integer
                .toString(MainActivity.pipWindowCoordinateTop));
        pipCustomWidth.setText(Integer.toString(MainActivity.pipWindowWidth));
        pipCustomHeight.setText(Integer.toString(MainActivity.pipWindowHeight));
    }

    public void updatePiPWindow() {
        /**
         * Currently not functional because android VideoVide is actualy never
         * called
         */
        // if (MainActivity.activity.getDualVideoManager()
        // .getSecondaryDisplayUnit().isPlaying()
        if ((MainActivity.activity.getDualVideoManager()
                .getSecondaryDisplayUnit().getVisibility() == View.VISIBLE)
                && (MainActivity.activity.getDualVideoManager()
                        .getSecondaryDisplayUnit().getPlayMode() == A4TVVideoView.PIP_DISPLAY_MODE)) {
            Log.d(TAG, "PIP is currently active and needs to be repositioned");
            MainActivity.activity.getDualVideoManager()
                    .getSecondaryDisplayUnit().gotoPIP();
        } else if ((MainActivity.activity.getPrimaryMultimediaVideoView() != null)
                && (MainActivity.activity.getMultimediaMode() == MainActivity.MULTIMEDIA_PIP)) {
            Log.d(TAG,
                    "PIP is currently active (multimedia) and needs to be repositioned");
            MainActivity.activity.getPrimaryMultimediaVideoView().gotoPIP();
        }
    }

    public boolean isPositionValid(int new_x, int new_y) {
        if (new_x + MainActivity.pipWindowWidth <= 1920) {
            if (new_y + MainActivity.pipWindowHeight <= 1080) {
                return true;
            }
        }
        return false;
    }

    public boolean isSizeValid(int new_width, int new_height) {
        if ((MainActivity.pipWindowCoordinateLeft + new_width) <= 1920) {
            if ((MainActivity.pipWindowCoordinateTop + new_height) <= 1080) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
        // clear old data in lists
        contentList.clear();
        contentListIDs.clear();
        titleIDs.clear();
        // title
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.drawable.pip_icon);
        titleIDs.add(R.string.tv_settings_menu_pip_settings);
        ArrayList<Integer> list = new ArrayList<Integer>();
        /** PiP position selector */
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pip_settings_position);
        list.add(PIP_SETTINGS_SET_POSITION);
        contentListIDs.add(list);
        /** PiP custom x value */
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.pip_x_coordinate);
        list.add(PIP_SETTINGS_CUSTOM_X_POSITION);
        contentListIDs.add(list);
        /** PiP custom y value */
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.pip_y_coordinate);
        list.add(PIP_SETTINGS_CUSTOM_Y_POSITION);
        contentListIDs.add(list);
        /** Apply coordinates */
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.pip_coordinate_apply);
        list.add(PIP_SETTINGS_CUSTOM_POSITION_APPLY);
        contentListIDs.add(list);
        /** PiP size selector */
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_pip_settings_size);
        list.add(PIP_SETTINGS_SET_SIZE);
        contentListIDs.add(list);
        /** PiP custom width */
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.pip_width);
        list.add(PIP_SETTINGS_CUSTOM_WIDTH);
        contentListIDs.add(list);
        /** PiP custom height */
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVEditText);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.pip_height);
        list.add(PIP_SETTINGS_CUSTOM_HEIGHT);
        contentListIDs.add(list);
        /** Apply size */
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.pip_size_apply);
        list.add(PIP_SETTINGS_CUSTOM_SIZE_APPLY);
        contentListIDs.add(list);
    }
}
