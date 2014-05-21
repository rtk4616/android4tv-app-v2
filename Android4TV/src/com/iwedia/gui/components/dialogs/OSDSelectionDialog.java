package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.ArrayList;

/**
 * s Picture settings dialog
 * 
 * @author Branimir Pavlovic
 */
public class OSDSelectionDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener, OSDGlobal {
    /** IDs for spinners in this dialog */
    public static final int CURL_ENABLED_ID = 3001;
    public static final int CURL_TIME_DELAY_SPINNER = 3002;
    public static final int CURL_ON_OFF = 3003;
    private PowerManager pm = null;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    /** Possible delay time values */
    public static int[] delayTimeValues = { 3, 5, 10, 15, 20 };
    private A4TVSpinner spinnerOsdSelection;
    private A4TVSpinner spinnerCurlDelay;
    private A4TVButtonSwitch buttonSwitchCurl;

    public OSDSelectionDialog(Context context) {
        super(context, checkTheme(context), 0);
        pm = (PowerManager) MainActivity.activity
                .getSystemService(Context.POWER_SERVICE);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        // set initial views
        fillInitialViews();
    }

    @Override
    public void show() {
        // fill initial
        fillViews();
        super.show();
        findViewById(R.string.tv_settings_menu_osd_selection).requestFocus();
    }

    /** Initially set up views */
    private void fillInitialViews() {
        spinnerOsdSelection = (A4TVSpinner) OSDSelectionDialog.this
                .findViewById(CURL_ENABLED_ID);
        /*************** Get references of views *************/
        spinnerCurlDelay = (A4TVSpinner) OSDSelectionDialog.this
                .findViewById(CURL_TIME_DELAY_SPINNER);
        // Get stored curl animation time
        int curlAnimationTime = MainActivity.sharedPrefs.getInt(
                MainActivity.CURL_ANIMATION_TIME_INFO, 5000) / 1000;
        int spinnerIndex = 0;
        // Find index in spinner
        for (int i = 0; i < delayTimeValues.length; i++) {
            if (curlAnimationTime == delayTimeValues[i]) {
                spinnerIndex = i;
                break;
            }
        }
        spinnerCurlDelay.setSelection(spinnerIndex);
        spinnerCurlDelay
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        int newTime = OSDSelectionDialog.delayTimeValues[index];
                        // Store to prefs
                        MainActivity.sharedPrefs
                                .edit()
                                .putInt(MainActivity.CURL_ANIMATION_TIME_INFO,
                                        newTime * 1000).commit();
                        // Set new animation time
                        MainActivity.activity.getPageCurl()
                                .setAnimationTimeChannelInfo(newTime * 1000);
                        A4TVToast toast = new A4TVToast(getContext());
                        toast.showToast(R.string.tv_menu_picture_settings_curl_settings_delay_time_updated);
                    }
                });
        // //////////////////////////////////////////////
        // Curl on off
        // //////////////////////////////////////////////
        buttonSwitchCurl = (A4TVButtonSwitch) OSDSelectionDialog.this
                .findViewById(CURL_ON_OFF);
        spinnerOsdSelection
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        Log.d(TAG,
                                "spinnerAnalogOutputMode onClick choosenItemIndex="
                                        + index);
                        Log.d("STOP", "spinner onClick choosenItemIndex="
                                + index);
                        MainActivity.sharedPrefs.edit()
                                .putInt(MainActivity.OSD_SELECTION, index)
                                .commit();
                        MainActivity.activity.initPageCurl();
                        setBackgroundView(index, false);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setBackgroundView(int choosenItemIndex, boolean firstOpened) {
        A4TVToast toast = new A4TVToast(getContext());
        if (choosenItemIndex == 0) {
            setLayoutDisplayMode(
                    R.string.tv_menu_picture_settings_curl_settings_delay_time,
                    DisplayMode.SHOW);
            setLayoutDisplayMode(R.string.curl_effect_on_off, DisplayMode.SHOW);
            /*
             * findViewById(
             * R.string.tv_menu_picture_settings_curl_settings_delay_time)
             * .setBackgroundColor(Color.TRANSPARENT);
             * findViewById(R.string.curl_effect_on_off).setBackgroundColor(
             * Color.TRANSPARENT); spinnerCurlDelay.setEnabled(true);
             * buttonSwitchCurl.setEnabled(true);
             * spinnerCurlDelay.setFocusable(true);
             * buttonSwitchCurl.setFocusable(true);
             */
            if (!firstOpened) {
                toast.showToast(R.string.curl_view_enabled_success);
            }
        } else {
            setLayoutDisplayMode(
                    R.string.tv_menu_picture_settings_curl_settings_delay_time,
                    DisplayMode.DISABLE);
            setLayoutDisplayMode(R.string.curl_effect_on_off,
                    DisplayMode.DISABLE);
            /*
             * findViewById(
             * R.string.tv_menu_picture_settings_curl_settings_delay_time)
             * .setBackgroundColor(Color.GRAY);
             * findViewById(R.string.curl_effect_on_off).setBackgroundColor(
             * Color.GRAY); spinnerCurlDelay.setEnabled(false);
             * buttonSwitchCurl.setEnabled(false);
             * spinnerCurlDelay.setFocusable(false);
             * buttonSwitchCurl.setFocusable(false);
             */
            if (!firstOpened) {
                if (choosenItemIndex == 1) {
                    toast.showToast(R.string.info_banner_enabled_success);
                } else {
                    toast.showToast(R.string.spinner_item_osd_selection_none);
                }
            }
        }
    }

    private void fillViews() {
        int osdSelection = MainActivity.sharedPrefs.getInt(
                MainActivity.OSD_SELECTION, 0);
        setBackgroundView(osdSelection, true);
        spinnerOsdSelection.setSelection(osdSelection);
        boolean isAnimEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.CURL_ANIMATION_ON_OFF, true);
        buttonSwitchCurl.setSelectedStateAndText(isAnimEnabled,
                isAnimEnabled ? R.string.button_text_on
                        : R.string.button_text_off);
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, null);// ,
        // pictureBackgroundID);
        setContentView(view);
    }

    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
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

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        switch (id) {
        /*
         * case CURL_ENABLED_ID: { buttonCurlEnabled.setSelectedStateAndText(!v
         * .isSelected(), !v.isSelected() ? R.string.button_text_on :
         * R.string.button_text_off); MainActivity.sharedPrefs .edit()
         * .putBoolean(MainActivity.CURL_ENABLED, v.isSelected()).commit();
         * MainActivity.sharedPrefs.edit() .putInt(MainActivity.OSD_SELECTION,
         * newTime * 1000) .commit(); MainActivity.activity.initPageCurl();
         * A4TVToast toast = new A4TVToast(getContext());
         * toast.showToast(R.string.curl_enabled_success); break; }
         */
            case CURL_ON_OFF: {
                buttonSwitchCurl.setSelectedStateAndText(!v.isSelected(), !v
                        .isSelected() ? R.string.button_text_on
                        : R.string.button_text_off);
                MainActivity.sharedPrefs
                        .edit()
                        .putBoolean(MainActivity.CURL_ANIMATION_ON_OFF,
                                v.isSelected()).commit();
                break;
            }
            default:
                break;
        }
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
        titleIDs.add(R.drawable.osd_selection_icon);
        titleIDs.add(R.string.tv_settings_menu_osd_selection);
        ArrayList<Integer> list;
        // curl enabled******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_settings_menu_osd_selection);
        list.add(CURL_ENABLED_ID);
        contentListIDs.add(list);
        // curl time delay******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_curl_settings_delay_time);
        list.add(CURL_TIME_DELAY_SPINNER);
        contentListIDs.add(list);
        // curl on off******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButtonSwitch);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.curl_effect_on_off);
        list.add(CURL_ON_OFF);
        contentListIDs.add(list);
    }
}
