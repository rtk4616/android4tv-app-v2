package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.system.IPictureSettings;
import com.iwedia.dtv.types.AspectRatioMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.ThemeUtils;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.osd.IOSDHandler;

import java.util.ArrayList;

/**
 * s Picture settings dialog
 * 
 * @author Branimir Pavlovic
 */
public class PictureSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener,
        OnSeekBarChangeListener {
    private static final String TAG = "PictureSettingsDialog";
    /** IDs for spinners */
    public static final int PICTURE_SETTINGS_PICTURE_MODE = 0,
            PICTURE_SETTINGS_THEME = 1, PICTURE_SETTINGS_ASPECT_RATIO = 67,
            PICTURE_SETTINGS_COLOR_TEMPERATURE = 20004,
            PICTURE_SETTINGS_NR = 20010;
    /** IDs for buttons */
    public static final int FIRST_TIME_INSTALL_NEXT_BUTTON = 10001;
    public static final int PICTURE_SETTINGS_SET_DEFAULT_SETTINGS = 10002;
    /** IDs for progress */
    public static final int PICTURE_SETTINGS_BRIGHTNESS = 20000,
            PICTURE_SETTINGS_CONTRAST = 20001,
            PICTURE_SETTINGS_SHARPNESS = 20002, PICTURE_SETTINGS_COLOR = 20003,
            PICTURE_SETTINGS_TINT = 20005, PICTURE_SETTINGS_HUE = 20007,
            PICTURE_SETTINGS_SATURATION = 20008,
            PICTURE_SETTINGS_BACKLIGHT = 20009;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    /** Buttons */
    private A4TVButton buttonFirstInstallNext;
    private A4TVButton buttonCurlSettings;
    private A4TVButton buttonSetDefaultSettings;
    /** Spinners */
    private A4TVSpinner spinnerAspectRatio, spinnerPictureMode, spinnerTheme,
            spinnerColorTemperature, spinnerNRMode;
    /** Progress bars */
    private A4TVProgressBar progressBrightness, progressContrast,
            progressSharpness, progressHue, progressSaturation,
            progressBacklight;
    private IPictureSettings pictureSettings;
    private final boolean mSwitchToUserModeIfPredefinedModeParamChanged = false;
    private int mCurrentPictureModeIdx = -1;
    public static final int DigitalInputTypeGroup = 0;
    public static final int AnalogInputTypeGroup = 1;
    public static final int VGAInputTypeGroup = 2;

    public PictureSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        // fill initial
        init(context);
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        if (MainActivity.isInFirstTimeInstall) {
            LanguageAndKeyboardDialog langDialog = MainActivity.activity
                    .getDialogManager().getLanguageAndKeyboardDialog();
            if (langDialog != null) {
                langDialog.show();
            }
        }
        super.onBackPressed();
    }

    private void init(final Context context) {
        /** Get spinner views */
        spinnerAspectRatio = (A4TVSpinner) findViewById(PICTURE_SETTINGS_ASPECT_RATIO);
        spinnerPictureMode = (A4TVSpinner) findViewById(PICTURE_SETTINGS_PICTURE_MODE);
        spinnerTheme = (A4TVSpinner) findViewById(PICTURE_SETTINGS_THEME);
        spinnerNRMode = (A4TVSpinner) findViewById(PICTURE_SETTINGS_NR);
        /** Progress bars */
        progressBrightness = (A4TVProgressBar) findViewById(PICTURE_SETTINGS_BRIGHTNESS);
        progressContrast = (A4TVProgressBar) findViewById(PICTURE_SETTINGS_CONTRAST);
        progressSharpness = (A4TVProgressBar) findViewById(PICTURE_SETTINGS_SHARPNESS);
        spinnerColorTemperature = (A4TVSpinner) findViewById(PICTURE_SETTINGS_COLOR_TEMPERATURE);
        progressHue = (A4TVProgressBar) findViewById(PICTURE_SETTINGS_HUE);
        progressSaturation = (A4TVProgressBar) findViewById(PICTURE_SETTINGS_SATURATION);
        progressBacklight = (A4TVProgressBar) findViewById(PICTURE_SETTINGS_BACKLIGHT);
        buttonSetDefaultSettings = (A4TVButton) findViewById(PICTURE_SETTINGS_SET_DEFAULT_SETTINGS);
        buttonSetDefaultSettings.setText(R.string.button_text_ok);
        // ////////////////////////////////
        // Check config file
        // ////////////////////////////////
        // set spinner listeners
        spinnerAspectRatio
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        Log.d(TAG,
                                "spinnerAspectRatio onClick choosenItemIndex="
                                        + index);
                        try {
                            pictureSettings
                                    .setAspectRatioMode(convertToAspectOutputRatioIndex(index));
                            IOSDHandler curlHandler = MainActivity.activity
                                    .getPageCurl();
                            if (curlHandler != null) {
                                curlHandler
                                        .showPictureFormat(currentFormatToString(convertToAspectOutputRatioIndex(index)));
                            } else {
                                Log.e(TAG,
                                        "Failed to show current picture format on OSD.");
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                });
        spinnerPictureMode
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        Log.d(TAG,
                                "spinnerPictureMode onClick choosenItemIndex="
                                        + index);
                        if (mCurrentPictureModeIdx != index) {
                            mCurrentPictureModeIdx = index;
                            Log.d(TAG,
                                    "spinnerPictureMode onClick mCurrentPictureModeIdx="
                                            + mCurrentPictureModeIdx);
                            updatePictureModeParams(-1,
                                    (mCurrentPictureModeIdx != 5));
                        }
                        try {
                            MainActivity.service.getSystemControl()
                                    .getPictureControl()
                                    .setActivePictureMode(index);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
        spinnerNRMode
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        boolean otherParamsNeedUpdate = false;
                        if (mSwitchToUserModeIfPredefinedModeParamChanged
                                && 5 != spinnerPictureMode
                                        .getCHOOSEN_ITEM_INDEX()) { // Diff than
                                                                    // user mode
                            setUserPictureMode();
                        }
                        try {
                            pictureSettings.setActiveNoiseReduction(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (otherParamsNeedUpdate) {
                            updatePictureModeParams(PICTURE_SETTINGS_NR, false);
                        }
                    }
                });
        spinnerColorTemperature
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        boolean otherParamsNeedUpdate = false;
                        if (mSwitchToUserModeIfPredefinedModeParamChanged
                                && 5 != spinnerPictureMode
                                        .getCHOOSEN_ITEM_INDEX()) { // Diff than
                                                                    // user mode
                            setUserPictureMode();
                        }
                        try {
                            pictureSettings.setActiveColorTemperature(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (otherParamsNeedUpdate) {
                            updatePictureModeParams(
                                    PICTURE_SETTINGS_COLOR_TEMPERATURE, false);
                        }
                    }
                });
        spinnerTheme
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        if (index != ThemeUtils.getActiveThemeIndex()) {
                            MainActivity.stopVideoOnPauseAndReturnMenuToUser = false;
                            final int id = spinner.getId();
                            A4TVToast toast = new A4TVToast(context);
                            toast.showToast(R.string.app_recreate);
                            Handler delay = new Handler();
                            delay.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // change theme
                                    ThemeUtils.changeToTheme(
                                            MainActivity.activity, id);
                                }
                            }, 500);
                        }
                    }
                });
        // ///////////////////////
        // NO TV FEATURES
        // ///////////////////////
        if (!ConfigHandler.TV_FEATURES) {
            // hide picture mode
            findViewById(R.string.tv_menu_picture_settings_picture_mode)
                    .setVisibility(View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID).setVisibility(
                    View.GONE);
            // Hide backlight row
            findViewById(R.string.tv_menu_picture_settings_backlight)
                    .setVisibility(View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 7).setVisibility(
                    View.GONE);
            // hide HUE
            findViewById(R.string.tv_menu_picture_settings_hue).setVisibility(
                    View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 5).setVisibility(
                    View.GONE);
            // hide THEME
            findViewById(R.string.tv_menu_picture_settings_theme)
                    .setVisibility(View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 8).setVisibility(
                    View.GONE);
        } else {
            // hide picture mode
            findViewById(R.string.tv_menu_picture_settings_picture_mode)
                    .setVisibility(View.VISIBLE);
            findViewById(DialogCreatorClass.LINES_BASE_ID).setVisibility(
                    View.VISIBLE);
            // Show backlight name row
            findViewById(R.string.tv_menu_picture_settings_backlight)
                    .setVisibility(View.VISIBLE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 7).setVisibility(
                    View.VISIBLE);
            // show HUE
            findViewById(R.string.tv_menu_picture_settings_hue).setVisibility(
                    View.VISIBLE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 5).setVisibility(
                    View.VISIBLE);
            // show THEME
            findViewById(R.string.tv_menu_picture_settings_theme)
                    .setVisibility(View.VISIBLE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 8).setVisibility(
                    View.VISIBLE);
        }
        // just for first time in FTI
        try {
            pictureSettings = MainActivity.service.getSystemControl()
                    .getPictureControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (pictureSettings != null) {
            if (MainActivity.isInFirstTimeInstall) {
                try {
                    AspectRatioMode format = pictureSettings
                            .getAspectRatioMode();
                    pictureSettings.setAspectRatioMode(format);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void fillViews() {
        boolean disableParams = false;
        if (!MainActivity.isInFirstTimeInstall) {
            findViewById(R.string.first_time_install_next).setVisibility(
                    View.GONE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 9).setVisibility(
                    View.GONE);
        } else {
            findViewById(R.string.first_time_install_next).setVisibility(
                    View.VISIBLE);
            findViewById(DialogCreatorClass.LINES_BASE_ID + 9).setVisibility(
                    View.VISIBLE);
            buttonFirstInstallNext = (A4TVButton) findViewById(FIRST_TIME_INSTALL_NEXT_BUTTON);
            buttonFirstInstallNext
                    .setText(R.string.first_time_install_next_button_text);
            if (MainActivity.activity.getFirstTimeInfoText() != null) {
                MainActivity.activity.getFirstTimeInfoText().setText(
                        R.string.first_time_install_set_tv_mode);
            }
        }
        pictureSettings = null;
        try {
            pictureSettings = MainActivity.service.getSystemControl()
                    .getPictureControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (pictureSettings != null) {
            /************************* ASPECT RATIO ***************************/
            int aspectRatio = 0;
            try {
                aspectRatio = convertToAspectRatioSpinnerIndex(pictureSettings
                        .getAspectRatioMode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            spinnerAspectRatio.setSelection(aspectRatio);
            if (ConfigHandler.TV_FEATURES) {
                /************************* PICTURE MODE ***************************/
                int pictureMode = 0;
                try {
                    pictureMode = pictureSettings.getActivePictureMode();
                    // Log.d(TAG, "fillViews pictureMode[" + pictureMode +
                    // "] setSelection");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                spinnerPictureMode.setSelection(pictureMode);
                disableParams = pictureMode != 5;// different than user mode
            }
            /************************** THEME **********************************/
            spinnerTheme.setSelection(ThemeUtils.getActiveThemeIndex());
            /************************* BRIGHTNESS ***************************/
            int brightnessProgress = 50;
            try {
                brightnessProgress = pictureSettings.getBrightness();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressBrightness.setProgress(brightnessProgress);
            progressBrightness.setClickable(!disableParams);
            if (disableParams) {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_brightness,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_brightness,
                        DisplayMode.SHOW);
            }
            /************************* CONTRAST ***************************/
            int contrastProgress = 50;
            try {
                contrastProgress = pictureSettings.getContrast();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressContrast.setProgress(contrastProgress);
            progressContrast.setClickable(!disableParams);
            if (disableParams) {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_contrast,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_contrast,
                        DisplayMode.SHOW);
            }
            /************************* SHARPNESS ***************************/
            int sharpnessProgress = 50;
            try {
                sharpnessProgress = pictureSettings.getSharpness();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressSharpness.setProgress(sharpnessProgress);
            progressSharpness.setClickable(!disableParams);
            if (disableParams) {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_sharpness,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_sharpness,
                        DisplayMode.SHOW);
            }
            /************************* COLOR TEMPERATURE ***************************/
            int colorTemperature = 0;
            try {
                colorTemperature = pictureSettings.getActiveColorTemperature();
            } catch (Exception e) {
                e.printStackTrace();
            }
            spinnerColorTemperature.setSelection(colorTemperature);
            spinnerColorTemperature.setClickable(!disableParams);
            if (disableParams) {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_color_temperature,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_color_temperature,
                        DisplayMode.SHOW);
            }
            /************************* HUE ***************************/
            int hueProgress = 50;
            try {
                hueProgress = pictureSettings.getHue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressHue.setProgress(hueProgress);
            /************************* SATURATION ***************************/
            int saturationProgress = 50;
            try {
                saturationProgress = pictureSettings.getSaturation();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressSaturation.setProgress(saturationProgress);
            progressSaturation.setClickable(!disableParams);
            if (disableParams) {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_saturation,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(
                        R.string.tv_menu_picture_settings_saturation,
                        DisplayMode.SHOW);
            }
            /************************* BACKLIGHT ***************************/
            if (ConfigHandler.TV_FEATURES) {
                int backlightProgress = 50;
                try {
                    backlightProgress = pictureSettings.getBacklight();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBacklight.setProgress(backlightProgress);
                progressBacklight.setClickable(!disableParams);
                if (disableParams) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_backlight,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_backlight,
                            DisplayMode.SHOW);
                }
            }
            /************************* NR MODE ***************************/
            int nRMode = 0;
            try {
                nRMode = pictureSettings.getActiveNoiseReduction();
            } catch (Exception e) {
                e.printStackTrace();
            }
            spinnerNRMode.setSelection(nRMode);
            spinnerNRMode.setClickable(!disableParams);
            if (disableParams) {
                setLayoutDisplayMode(R.string.tv_menu_picture_settings_nr,
                        DisplayMode.DISABLE);
            } else {
                setLayoutDisplayMode(R.string.tv_menu_picture_settings_nr,
                        DisplayMode.SHOW);
            }
        }
    }

    @Override
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, this, this, null);// ,
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
    public void onClick(View v) {
        switch (v.getId()) {
            case FIRST_TIME_INSTALL_NEXT_BUTTON: {
                NetworkSettingsDialog netSettDialog = MainActivity.activity
                        .getDialogManager().getNetworkSettingsDialog();
                if (netSettDialog != null) {
                    netSettDialog.show();
                }
                PictureSettingsDialog.this.cancel();
                break;
            }
            case PICTURE_SETTINGS_SET_DEFAULT_SETTINGS: {
                try {
                    pictureSettings.setPictureMenuDefaultSettings();
                    AspectRatioMode format = pictureSettings
                            .getAspectRatioMode();
                    pictureSettings.setAspectRatioMode(format);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.show();
                break;
            }
            default:
                break;
        }
    }

    /*
     * - Brightness - Contrast - Saturation - Color Temperature - Tint -
     * Sharpness - Backlight - Noise reduction
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        ((A4TVProgressBar) seekBar).setText(String.valueOf(seekBar
                .getProgress()));
        // Log.d(TAG, "onProgressChanged seekBar.getId()[" + seekBar.getId()
        // +"] progress[" + progress + "] fromUser[" + fromUser + "]" );
        switch (seekBar.getId()) {
        // Brightness
            case PICTURE_SETTINGS_BRIGHTNESS: {
                boolean otherParamsNeedUpdate = false;
                try {
                    if (mSwitchToUserModeIfPredefinedModeParamChanged
                            && fromUser
                            && 5 != pictureSettings.getActivePictureMode()) {// Diff
                                                                             // than
                                                                             // user
                                                                             // mode
                        setUserPictureMode();
                        otherParamsNeedUpdate = true;
                    }
                    pictureSettings.setBrightness(progress);
                    if (otherParamsNeedUpdate) {
                        updatePictureModeParams(PICTURE_SETTINGS_BRIGHTNESS,
                                false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            // Contrast
            case PICTURE_SETTINGS_CONTRAST: {
                boolean otherParamsNeedUpdate = false;
                try {
                    if (mSwitchToUserModeIfPredefinedModeParamChanged
                            && fromUser
                            && 5 != pictureSettings.getActivePictureMode()) {// Diff
                                                                             // than
                                                                             // user
                                                                             // mode
                        setUserPictureMode();
                        otherParamsNeedUpdate = true;
                    }
                    pictureSettings.setContrast(progress);
                    if (otherParamsNeedUpdate) {
                        updatePictureModeParams(PICTURE_SETTINGS_CONTRAST,
                                false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            // Sharpness
            case PICTURE_SETTINGS_SHARPNESS: {
                boolean otherParamsNeedUpdate = false;
                try {
                    if (mSwitchToUserModeIfPredefinedModeParamChanged
                            && fromUser
                            && 5 != pictureSettings.getActivePictureMode()) {// Diff
                                                                             // than
                                                                             // user
                                                                             // mode
                        setUserPictureMode();
                        otherParamsNeedUpdate = true;
                    }
                    pictureSettings.setSharpness(progress);
                    if (otherParamsNeedUpdate) {
                        updatePictureModeParams(PICTURE_SETTINGS_SHARPNESS,
                                false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            // Hue
            case PICTURE_SETTINGS_HUE: {
                try {
                    pictureSettings.setHue(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            }
            // Saturation
            case PICTURE_SETTINGS_SATURATION: {
                boolean otherParamsNeedUpdate = false;
                try {
                    if (mSwitchToUserModeIfPredefinedModeParamChanged
                            && fromUser
                            && 5 != pictureSettings.getActivePictureMode()) {// Diff
                                                                             // than
                                                                             // user
                                                                             // mode
                        setUserPictureMode();
                        otherParamsNeedUpdate = true;
                    }
                    pictureSettings.setSaturation(progress);
                    if (otherParamsNeedUpdate) {
                        updatePictureModeParams(PICTURE_SETTINGS_SATURATION,
                                false);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            }
            // Backlight
            case PICTURE_SETTINGS_BACKLIGHT: {
                boolean otherParamsNeedUpdate = false;
                try {
                    if (mSwitchToUserModeIfPredefinedModeParamChanged
                            && fromUser
                            && 5 != pictureSettings.getActivePictureMode()) {// Diff
                                                                             // than
                                                                             // user
                                                                             // mode
                        setUserPictureMode();
                        otherParamsNeedUpdate = true;
                    }
                    pictureSettings.setsBacklight(progress);
                    if (otherParamsNeedUpdate) {
                        updatePictureModeParams(PICTURE_SETTINGS_BACKLIGHT,
                                false);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
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
        titleIDs.add(R.drawable.settings_icon);
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.string.tv_menu_picture_settings);
        // picture mode******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_picture_mode);
        list.add(PICTURE_SETTINGS_PICTURE_MODE);
        contentListIDs.add(list);
        // aspect ratio ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_aspect_ratio);
        list.add(PICTURE_SETTINGS_ASPECT_RATIO);
        contentListIDs.add(list);
        // brightness*******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_brightness);
        list.add(PICTURE_SETTINGS_BRIGHTNESS);
        contentListIDs.add(list);
        // contrast**********************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_contrast);
        list.add(PICTURE_SETTINGS_CONTRAST);
        contentListIDs.add(list);
        /*
         * // color**************************************************** list =
         * new ArrayList<Integer>(); list.add(MainMenuContent.TAGA4TVTextView);
         * list.add(MainMenuContent.TAGA4TVProgressBar); contentList.add(list);
         * list = new ArrayList<Integer>();
         * list.add(R.string.tv_menu_picture_settings_color);
         * list.add(picture_settings_color); contentListIDs.add(list); //
         * tint***************************************************** list = new
         * ArrayList<Integer>(); list.add(MainMenuContent.TAGA4TVTextView);
         * list.add(MainMenuContent.TAGA4TVProgressBar); contentList.add(list);
         * list = new ArrayList<Integer>();
         * list.add(R.string.tv_menu_picture_settings_tint);
         * list.add(picture_settings_tint); contentListIDs.add(list);
         */
        // sharpness*************************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_sharpness);
        list.add(PICTURE_SETTINGS_SHARPNESS);
        contentListIDs.add(list);
        // color temperature****************************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_color_temperature);
        list.add(PICTURE_SETTINGS_COLOR_TEMPERATURE);
        contentListIDs.add(list);
        // hue*****************************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_hue);
        list.add(PICTURE_SETTINGS_HUE);
        contentListIDs.add(list);
        // saturation*************************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_saturation);
        list.add(PICTURE_SETTINGS_SATURATION);
        contentListIDs.add(list);
        // backlight*************************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_backlight);
        list.add(PICTURE_SETTINGS_BACKLIGHT);
        contentListIDs.add(list);
        // theme*****************************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_theme);
        list.add(PICTURE_SETTINGS_THEME);
        contentListIDs.add(list);
        // noise reduction mode***************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_picture_settings_nr);
        list.add(PICTURE_SETTINGS_NR);
        contentListIDs.add(list);
        // picture set default settings
        // ******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_sound_settings_set_default_settings);
        list.add(PICTURE_SETTINGS_SET_DEFAULT_SETTINGS);
        contentListIDs.add(list);
        // first time install next ***********************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.first_time_install_next);
        list.add(FIRST_TIME_INSTALL_NEXT_BUTTON);
        contentListIDs.add(list);
    }

    private void updatePictureModeParams(int excludeParam, boolean disableParams) {
        int value;
        Log.d(TAG, "updatePictureModeParams");
        try {
            if (PICTURE_SETTINGS_BRIGHTNESS != excludeParam) {
                value = pictureSettings.getBrightness();
                progressBrightness.setProgress(value);
                if (disableParams) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_brightness,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_brightness,
                            DisplayMode.SHOW);
                }
            }
            if (PICTURE_SETTINGS_CONTRAST != excludeParam) {
                value = pictureSettings.getContrast();
                progressContrast.setProgress(value);
                if (disableParams) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_contrast,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_contrast,
                            DisplayMode.SHOW);
                }
            }
            if (PICTURE_SETTINGS_SATURATION != excludeParam) {
                value = pictureSettings.getSaturation();
                progressSaturation.setProgress(value);
                if (disableParams) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_saturation,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_saturation,
                            DisplayMode.SHOW);
                }
            }
            if (PICTURE_SETTINGS_COLOR_TEMPERATURE != excludeParam) {
                value = pictureSettings.getActiveColorTemperature();
                spinnerColorTemperature.setSelection(value);
                if (disableParams) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_color_temperature,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_color_temperature,
                            DisplayMode.SHOW);
                }
            }
            if (PICTURE_SETTINGS_SHARPNESS != excludeParam) {
                value = pictureSettings.getSharpness();
                progressSharpness.setProgress(value);
                if (disableParams) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_sharpness,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_sharpness,
                            DisplayMode.SHOW);
                }
            }
            if (PICTURE_SETTINGS_BACKLIGHT != excludeParam) {
                value = pictureSettings.getBacklight();
                progressBacklight.setProgress(value);
                if (disableParams) {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_backlight,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(
                            R.string.tv_menu_picture_settings_backlight,
                            DisplayMode.SHOW);
                }
            }
            if (PICTURE_SETTINGS_NR != excludeParam) {
                value = pictureSettings.getActiveNoiseReduction();
                spinnerNRMode.setSelection(value);
                if (disableParams) {
                    setLayoutDisplayMode(R.string.tv_menu_picture_settings_nr,
                            DisplayMode.DISABLE);
                } else {
                    setLayoutDisplayMode(R.string.tv_menu_picture_settings_nr,
                            DisplayMode.SHOW);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserPictureMode() {
        Log.d(TAG, "setUserPictureMode");
        try {
            // PictureMode.PICTURE_USER_MODE == 5
            pictureSettings.setActivePictureMode(5);
            spinnerPictureMode.setSelection(5);
            mCurrentPictureModeIdx = 5;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private AspectRatioMode convertToAspectOutputRatioIndex(int index) {
        AspectRatioMode aspectRatio = AspectRatioMode.AUTO;
        switch (getCurrentInputTypeGroup(0)) {
            case 0: // digital group
                switch (index) {
                    case 0: // tv_menu_picture_settings_aspect_ratio_auto
                        aspectRatio = AspectRatioMode.AUTO;
                        break;
                    case 1: // tv_menu_picture_settings_aspect_ratio_normal_4_3
                        aspectRatio = AspectRatioMode.NORMAL_4_3;
                        break;
                    case 2: // tv_menu_picture_settings_aspect_ratio_zoom_14_9
                        aspectRatio = AspectRatioMode.ZOOM_14_9;
                        break;
                    case 3: // tv_menu_picture_settings_aspect_ratio_panorama
                        aspectRatio = AspectRatioMode.PANORAMA;
                        break;
                    case 4: // tv_menu_picture_settings_aspect_ratio_letterbox
                        aspectRatio = AspectRatioMode.LETTERBOX;
                        break;
                    case 5: // tv_menu_picture_settings_aspect_ratio_full
                        aspectRatio = AspectRatioMode.FULL;
                        break;
                    case 6: // tv_menu_picture_settings_aspect_ratio_cinema_16_9
                        aspectRatio = AspectRatioMode.CINEMA_16_9;
                        break;
                    case 7: // tv_menu_picture_settings_aspect_ratio_cinema_14_9
                        aspectRatio = AspectRatioMode.CINEMA_14_9;
                        break;
                    default:
                        break;
                }
                break;
            case 1: // analog group
                switch (index) {
                    case 0: // tv_menu_picture_settings_aspect_ratio_auto
                        aspectRatio = AspectRatioMode.AUTO;
                        break;
                    case 1: // tv_menu_picture_settings_aspect_ratio_normal_4_3
                        aspectRatio = AspectRatioMode.NORMAL_4_3;
                        break;
                    case 2: // tv_menu_picture_settings_aspect_ratio_zoom_14_9
                        aspectRatio = AspectRatioMode.ZOOM_14_9;
                        break;
                    case 3: // tv_menu_picture_settings_aspect_ratio_panorama
                        aspectRatio = AspectRatioMode.PANORAMA;
                        break;
                    case 4: // tv_menu_picture_settings_aspect_ratio_letterbox
                        aspectRatio = AspectRatioMode.LETTERBOX;
                        break;
                    case 5: // tv_menu_picture_settings_aspect_ratio_cinema_16_9
                        aspectRatio = AspectRatioMode.CINEMA_16_9;
                        break;
                    case 6: // tv_menu_picture_settings_aspect_ratio_cinema_14_9
                        aspectRatio = AspectRatioMode.CINEMA_14_9;
                        break;
                    default:
                        break;
                }
                break;
            case 2: // vga group
                switch (index) {
                    case 0: // tv_menu_picture_settings_aspect_ratio_normal_4_3
                        aspectRatio = AspectRatioMode.NORMAL_4_3;
                        break;
                    case 1: // tv_menu_picture_settings_aspect_ratio_cinema_16_9
                        aspectRatio = AspectRatioMode.CINEMA_16_9;
                        break;
                    default:
                        break;
                }
                break;
        }
        return aspectRatio;
    }

    private int convertToAspectRatioSpinnerIndex(AspectRatioMode aspectRatio) {
        int index = -1;
        switch (getCurrentInputTypeGroup(0)) {
            case 0: // digital group
                switch (aspectRatio) {
                    case AUTO:
                        index = 0; // tv_menu_picture_settings_aspect_ratio_auto
                        break;
                    case NORMAL_4_3:
                        index = 1; // tv_menu_picture_settings_aspect_ratio_normal_4_3
                        break;
                    case ZOOM_14_9:
                        index = 2; // tv_menu_picture_settings_aspect_ratio_zoom_14_9
                        break;
                    case PANORAMA:
                        index = 3; // tv_menu_picture_settings_aspect_ratio_panorama
                        break;
                    case LETTERBOX:
                        index = 4; // tv_menu_picture_settings_aspect_ratio_letterbox
                        break;
                    case FULL:
                        index = 5; // tv_menu_picture_settings_aspect_ratio_full
                        break;
                    case CINEMA_16_9:
                        index = 6; // tv_menu_picture_settings_aspect_ratio_cinema_16_9
                        break;
                    case CINEMA_14_9:
                        index = 7; // tv_menu_picture_settings_aspect_ratio_cinema_14_9
                        break;
                    default:
                        break;
                }
                break;
            case 1: // analog group
                switch (aspectRatio) {
                    case AUTO:
                        index = 0; // tv_menu_picture_settings_aspect_ratio_auto
                        break;
                    case NORMAL_4_3:
                        index = 1; // tv_menu_picture_settings_aspect_ratio_normal_4_3
                        break;
                    case ZOOM_14_9:
                        index = 2; // tv_menu_picture_settings_aspect_ratio_zoom_14_9
                        break;
                    case PANORAMA:
                        index = 3; // tv_menu_picture_settings_aspect_ratio_panorama
                        break;
                    case LETTERBOX:
                        index = 4; // tv_menu_picture_settings_aspect_ratio_letterbox
                        break;
                    case CINEMA_16_9:
                        index = 5; // tv_menu_picture_settings_aspect_ratio_cinema_16_9
                        break;
                    case CINEMA_14_9:
                        index = 6; // tv_menu_picture_settings_aspect_ratio_cinema_14_9
                        break;
                    default:
                        break;
                }
                break;
            case 2: // vga group
                switch (aspectRatio) {
                    case NORMAL_4_3:
                        index = 0; // tv_menu_picture_settings_aspect_ratio_normal_4_3
                        break;
                    case CINEMA_16_9:
                        index = 1; // tv_menu_picture_settings_aspect_ratio_cinema_16_9
                        break;
                    default:
                        break;
                }
                break;
        }
        return index;
    }

    private String currentFormatToString(AspectRatioMode format) {
        Log.d(TAG, "currentFormatToString: format[" + format + "]");
        switch (format.getValue()) {
            case 0:
                return MainActivity.activity.getResources().getString(
                        R.string.tv_menu_picture_settings_aspect_ratio_auto);
            case 1:
                return MainActivity.activity.getResources().getString(
                        R.string.tv_menu_picture_settings_aspect_ratio_full);
            case 2:
                return MainActivity.activity
                        .getResources()
                        .getString(
                                R.string.tv_menu_picture_settings_aspect_ratio_cinema_16_9);
            case 3:
                return MainActivity.activity
                        .getResources()
                        .getString(
                                R.string.tv_menu_picture_settings_aspect_ratio_cinema_14_9);
            case 4:
                return MainActivity.activity
                        .getResources()
                        .getString(
                                R.string.tv_menu_picture_settings_aspect_ratio_normal_4_3);
            case 5:
                return MainActivity.activity
                        .getResources()
                        .getString(
                                R.string.tv_menu_picture_settings_aspect_ratio_zoom_14_9);
            case 6:
                return MainActivity.activity
                        .getResources()
                        .getString(
                                R.string.tv_menu_picture_settings_aspect_ratio_panorama);
            case 7:
                return MainActivity.activity
                        .getResources()
                        .getString(
                                R.string.tv_menu_picture_settings_aspect_ratio_letterbox);
            default:
                return "Unknown";
        }
    }

    public static int getCurrentInputTypeGroup(int displayID) {
        Content content = null;
        try {
            content = MainActivity.service.getContentListControl()
                    .getActiveContent(displayID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (content != null) {
            Log.d(TAG, "filterType[" + content.getFilterType() + "] index["
                    + content.getIndex() + "]");
            if (content instanceof ServiceContent) {
                switch (content.getSourceType()) {
                    case TER:
                    case CAB:
                    case SAT:
                        return DigitalInputTypeGroup;
                    case ANALOG:
                        return AnalogInputTypeGroup;
                }
            } else
                switch (content.getFilterType()) {
                    case FilterType.INPUTS:
                        int index = content.getIndex();
                        switch (index) {
                            case 3: // VGA,
                                return VGAInputTypeGroup;
                            case 5: // HDMI
                            case 6: // HDMI
                            case 7: // HDMI
                            case 8: // HDMI
                                // D/PictureSettingsDialog( 1539):
                                // filterType[18]
                                // index[5,6,7,8]
                                return DigitalInputTypeGroup;
                            case 4: // Component
                                // PictureSettingsDialog( 1539): filterType[18]
                                // index[4]
                                return DigitalInputTypeGroup;
                            case 2: // CVBS(composite)
                                // case SCART:????
                                return AnalogInputTypeGroup;
                        }
                        break;
                    default:
                        break;
                }
        }
        Log.d(TAG,
                "getCurrentInputTypeGroup return default group for display id["
                        + displayID + "].");
        return DigitalInputTypeGroup;
    }
}
