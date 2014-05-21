package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.IInputOutputControl;
import com.iwedia.comm.system.ISoundSettings;
import com.iwedia.dtv.io.AudioOutputMode;
import com.iwedia.dtv.io.SpdifMode;
import com.iwedia.dtv.route.common.RouteInputOutputDescriptor;
import com.iwedia.dtv.route.common.RouteInputOutputDeviceType;
import com.iwedia.dtv.sound.SoundEffect;
import com.iwedia.dtv.sound.SoundEffectParam;
import com.iwedia.dtv.sound.SoundMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Sound settings dialog
 * 
 * @author Branimir Pavlovic
 */
public class SoundSettingsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener,
        OnSeekBarChangeListener {
    private static final String TAG = "SoundSettingsDialog";
    /** IDs for spinner */
    public static final int TV_MENU_SOUND_SETTINGS_SOUND_MODE = 2,
            TV_MENU_SOUND_SETTINGS_SPDIF_MODE = 3,
            TV_MENU_SOUND_SETTINGS_FIRST_AUDIO = 834756,
            TV_MENU_SOUND_SETTINGS_SECOND_AUDIO = 34578,
            TV_MENU_SOUND_SETTINGS_ANALOG_OUTPUT_MODE = 34583,
            TV_MENU_SOUND_SETTINGS_TV_SPEAKER = 34584,
            TV_MENU_SOUND_SETTINGS_HEADPHONE_OUTPUT_MODE = 34585;
    /** IDs for progress */
    public static final int TV_MENU_SOUND_SETTINGS_BALANCE = 7,
            TV_MENU_SOUND_SETTINGS_HEADPHONES_VOLUME = 9,
            TV_MENU_SOUND_SETTINGS_DELAY = 34534517;
    /** IDs for buttons */
    public static final int TV_MENU_SOUND_SETTINGS_AUTO_VOLUME = 12,
            TV_MENU_SOUND_SETTINGS_SRS_TRUSURROUND = 33314,
            TV_MENU_SOUND_SETTINGS_SRS_TRUBASS = 33315,
            TV_MENU_SOUND_SETTINGS_SRS_DIALOG_CLARITY = 33316,
            TV_MENU_SOUND_SETTINGS_AUDIO_DESCRIPTION = 14,
            TV_MENU_SOUND_SETTINGS_POST_PROCESSING = 15,
            TV_MENU_SOUND_SETTINGS_SET_DEFAULT_SETTINGS = 16;
    private final int SOUND_EFFECT_PARAM_OFFSET = 5;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    /** Switch buttons */
    private A4TVButtonSwitch buttonAutoVolume, buttonSrsTruSurround,
            buttonSrsTruBass, buttonSrsDialogClarity, buttonAudioDescription,
            buttonTvSpeaker;
    /** Buttons */
    private A4TVButton buttonPostProcessing, buttonSetDefaultSettings;
    /** Spinners **/
    private A4TVSpinner spinnerSoundMode, spinnerSPDIFMode, spinnerFirstAudio,
            spinnerSecondAudio, spinnerAnalogOutputMode,
            spinnerHeadphoneOutputMode;
    /** Progress bars */
    private A4TVProgressBar progressBalance, progressHeadPhonesVolume,
            progressDelay;
    private ISoundSettings soundSettings;
    private IInputOutputControl ioControl = null;

    public SoundSettingsDialog(Context context) {
        super(context, checkTheme(context), 0);
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

    /** Init views */
    private void init() {
        /** Switch buttons */
        if (ConfigHandler.COMPLEX_AUDIO) {
            buttonAutoVolume = (A4TVButtonSwitch) findViewById(TV_MENU_SOUND_SETTINGS_AUTO_VOLUME);
            buttonSrsDialogClarity = (A4TVButtonSwitch) findViewById(TV_MENU_SOUND_SETTINGS_SRS_DIALOG_CLARITY);
            buttonSrsTruBass = (A4TVButtonSwitch) findViewById(TV_MENU_SOUND_SETTINGS_SRS_TRUBASS);
            buttonSrsTruSurround = (A4TVButtonSwitch) findViewById(TV_MENU_SOUND_SETTINGS_SRS_TRUSURROUND);
            buttonAudioDescription = (A4TVButtonSwitch) findViewById(TV_MENU_SOUND_SETTINGS_AUDIO_DESCRIPTION);
            buttonTvSpeaker = (A4TVButtonSwitch) findViewById(TV_MENU_SOUND_SETTINGS_TV_SPEAKER);
            buttonPostProcessing = (A4TVButton) findViewById(TV_MENU_SOUND_SETTINGS_POST_PROCESSING);
            buttonPostProcessing.setText(R.string.button_text_view);
            buttonSetDefaultSettings = (A4TVButton) findViewById(TV_MENU_SOUND_SETTINGS_SET_DEFAULT_SETTINGS);
            buttonSetDefaultSettings.setText(R.string.button_text_ok);
            /** Spinners */
            spinnerSoundMode = (A4TVSpinner) findViewById(TV_MENU_SOUND_SETTINGS_SOUND_MODE);
            spinnerSPDIFMode = (A4TVSpinner) findViewById(TV_MENU_SOUND_SETTINGS_SPDIF_MODE);
            /** Progress bar */
            progressHeadPhonesVolume = (A4TVProgressBar) findViewById(TV_MENU_SOUND_SETTINGS_HEADPHONES_VOLUME);
            progressDelay = (A4TVProgressBar) findViewById(TV_MENU_SOUND_SETTINGS_DELAY);
            progressDelay.setMax(250);
            spinnerAnalogOutputMode = (A4TVSpinner) findViewById(TV_MENU_SOUND_SETTINGS_ANALOG_OUTPUT_MODE);
            spinnerAnalogOutputMode
                    .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                        @Override
                        public void onSelect(A4TVSpinner spinner, int index,
                                String[] contents) {
                            Log.d(TAG,
                                    "spinnerAnalogOutputMode onClick choosenItemIndex="
                                            + index);
                            try {
                                long deviceCount = ioControl
                                        .ioGetDevicesCount();
                                for (int i = 0; i < deviceCount; i++) {
                                    // For analog output mode set for all analog
                                    // audio outputs
                                    if (ioControl.ioGetDeviceOutput(i)) {
                                        RouteInputOutputDescriptor deviceDescriptor = ioControl
                                                .ioGetDeviceDescriptor(i);
                                        if (isAnalogDevice(deviceDescriptor)) {
                                            Log.d(TAG,
                                                    "Setting analog output for device "
                                                            + deviceDescriptor
                                                                    .toString()
                                                            + " to mode: "
                                                            + index);
                                            AudioOutputMode outputMode = AudioOutputMode
                                                    .getFromValue(index);
                                            ioControl
                                                    .ioDeviceSetAudioOutputMode(
                                                            i, outputMode);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            spinnerHeadphoneOutputMode = (A4TVSpinner) findViewById(TV_MENU_SOUND_SETTINGS_HEADPHONE_OUTPUT_MODE);
            spinnerHeadphoneOutputMode
                    .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                        @Override
                        public void onSelect(A4TVSpinner spinner, int index,
                                String[] contents) {
                            Log.d(TAG,
                                    "spinnerHeadphoneOutputMode onClick choosenItemIndex="
                                            + index);
                            try {
                                long deviceCount = ioControl
                                        .ioGetDevicesCount();
                                for (int i = 0; i < deviceCount; i++) {
                                    RouteInputOutputDescriptor deviceDescriptor = ioControl
                                            .ioGetDeviceDescriptor(i);
                                    if (deviceDescriptor
                                            .getInputOutputDeviceType()
                                            .equals(RouteInputOutputDeviceType.HEADPHONE)) {
                                        Log.d(TAG,
                                                "Setting headphone mode for device "
                                                        + deviceDescriptor
                                                                .toString()
                                                        + " to mode: " + index);
                                        AudioOutputMode outputMode = AudioOutputMode
                                                .getFromValue(index);
                                        ioControl.ioDeviceSetAudioOutputMode(i,
                                                outputMode);
                                    }
                                }
                                AudioOutputMode outputMode = AudioOutputMode
                                        .getFromValue(index);
                                if (outputMode.equals(AudioOutputMode.FIXED))
                                    setLayoutDisplayMode(
                                            R.string.tv_menu_sound_settings_headphones_volume,
                                            DisplayMode.SHOW);
                                else
                                    setLayoutDisplayMode(
                                            R.string.tv_menu_sound_settings_headphones_volume,
                                            DisplayMode.DISABLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            spinnerSoundMode
                    .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                        @Override
                        public void onSelect(A4TVSpinner spinner, int index,
                                String[] contents) {
                            Log.d(TAG,
                                    "spinnerSoundMode onClick choosenItemIndex="
                                            + index);
                            try {
                                switch (index) {
                                    case 0:
                                        soundSettings.setSoundEffectEnabled(
                                                SoundEffect.SRS, false);
                                        soundSettings
                                                .setSoundEffectEnabled(
                                                        SoundEffect.EXPANDED_SPATIAL_STEREO,
                                                        false);
                                        break;
                                    case 1:
                                        soundSettings.setSoundEffectEnabled(
                                                SoundEffect.SRS, true);
                                        soundSettings
                                                .setSoundEffectEnabled(
                                                        SoundEffect.EXPANDED_SPATIAL_STEREO,
                                                        false);
                                        break;
                                    case 2:
                                        soundSettings.setSoundEffectEnabled(
                                                SoundEffect.SRS, false);
                                        soundSettings
                                                .setSoundEffectEnabled(
                                                        SoundEffect.EXPANDED_SPATIAL_STEREO,
                                                        true);
                                        break;
                                }
                                try {
                                    MainActivity.service
                                            .getSystemControl()
                                            .getSoundControl()
                                            .setActiveSoundMode(
                                                    SoundMode.values()[index]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // TODO maybe we could just update the relevant
                                // components?
                                // show();
                                updateSoundModeComponents(index);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            spinnerSPDIFMode
                    .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                        @Override
                        public void onSelect(A4TVSpinner spinner, int index,
                                String[] contents) {
                            try {
                                MainActivity.service
                                        .getSystemControl()
                                        .getSoundControl()
                                        .setActiveSpdifMode(
                                                SpdifMode.values()[index]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        /** Progress bars */
        progressBalance = (A4TVProgressBar) findViewById(TV_MENU_SOUND_SETTINGS_BALANCE);
        spinnerFirstAudio = (A4TVSpinner) findViewById(TV_MENU_SOUND_SETTINGS_FIRST_AUDIO);
        spinnerSecondAudio = (A4TVSpinner) findViewById(TV_MENU_SOUND_SETTINGS_SECOND_AUDIO);
        spinnerFirstAudio
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getAudioControl()
                                    .setFirstAudioLanguage(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        spinnerSecondAudio
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        try {
                            MainActivity.service.getAudioControl()
                                    .setSecondAudioLanguage(index);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    private void fillViews() {
        soundSettings = null;
        try {
            soundSettings = MainActivity.service.getSystemControl()
                    .getSoundControl();
            ioControl = MainActivity.service.getInputOutputControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (soundSettings != null) {
            if (ConfigHandler.COMPLEX_AUDIO) {
                /******************************** AUTO VOLUME *****************************/
                boolean isAuto = false;
                try {
                    isAuto = soundSettings.isAutoVolume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isAuto) {
                    buttonAutoVolume.setSelectedStateAndText(true,
                            R.string.button_text_on);
                } else {
                    buttonAutoVolume.setSelectedStateAndText(false,
                            R.string.button_text_off);
                }
                /******************************** SOUND MODE *****************************/
                boolean srsSoundEffect;
                boolean spatialEffect;
                int soundMode = 0;
                try {
                    srsSoundEffect = soundSettings
                            .isSoundEffectEnabled(SoundEffect.SRS);
                    spatialEffect = soundSettings
                            .isSoundEffectEnabled(SoundEffect.EXPANDED_SPATIAL_STEREO);
                    Log.d(TAG, "SOUND MODE: srsSoundEffect=" + srsSoundEffect
                            + " spatialEffect" + spatialEffect);
                    if (srsSoundEffect) {
                        soundMode = 1;
                    } else if (spatialEffect) {
                        soundMode = 2;
                    } else {
                        soundMode = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateSoundModeComponents(soundMode);
                /******************************** SRS SURROUND *****************************/
                int surroundState = 0;
                try {
                    surroundState = soundSettings
                            .getSoundEffectParam(SoundEffectParam.SRS_TRUSURROUND);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (surroundState == 1) {
                    buttonSrsTruSurround.setSelectedStateAndText(true,
                            R.string.button_text_on);
                } else {
                    buttonSrsTruSurround.setSelectedStateAndText(false,
                            R.string.button_text_off);
                }
                /******************************** SRS TRUBASS *****************************/
                int truBassState = 0;
                try {
                    truBassState = soundSettings
                            .getSoundEffectParam(SoundEffectParam.SRS_TRUBASS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (truBassState == 1) {
                    buttonSrsTruBass.setSelectedStateAndText(true,
                            R.string.button_text_on);
                } else {
                    buttonSrsTruBass.setSelectedStateAndText(false,
                            R.string.button_text_off);
                }
                /******************************** SRS DIALOG CLARITY *****************************/
                int dialogClarityState = 0;
                try {
                    dialogClarityState = soundSettings
                            .getSoundEffectParam(SoundEffectParam.SRS_DIALOG_CLARITY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (dialogClarityState == 1) {
                    buttonSrsDialogClarity.setSelectedStateAndText(true,
                            R.string.button_text_on);
                } else {
                    buttonSrsDialogClarity.setSelectedStateAndText(false,
                            R.string.button_text_off);
                }
                /******************************** SPDIF MODE *****************************/
                int spdifMode = 0;
                try {
                    spdifMode = soundSettings.getActiveSpdifMode().getValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                spinnerSPDIFMode.setSelection(spdifMode);
                /******************************** DELAY *****************************/
                int delay = 0;
                try {
                    long deviceCount = ioControl.ioGetDevicesCount();
                    for (int i = 0; i < deviceCount; i++) {
                        if (ioControl.ioGetDeviceOutput(i)) {
                            // For now all output devices get set by the same
                            // delay, so reading any of them
                            // is enough
                            delay = ioControl.ioDeviceGetAudioDelay(i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDelay.setProgress(delay);
                /******************************** HEADPHONE OUTPUT MODE *****************************/
                AudioOutputMode headphoneMode = null;
                try {
                    long deviceCount = ioControl.ioGetDevicesCount();
                    for (int i = 0; i < deviceCount; i++) {
                        RouteInputOutputDescriptor deviceDescriptor = ioControl
                                .ioGetDeviceDescriptor(i);
                        if (deviceDescriptor.getInputOutputDeviceType().equals(
                                RouteInputOutputDeviceType.HEADPHONE)) {
                            headphoneMode = ioControl
                                    .ioDeviceGetAudioOutputMode(i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (headphoneMode != null) {
                    spinnerHeadphoneOutputMode.setSelection(headphoneMode
                            .getValue());
                    if (headphoneMode.equals(AudioOutputMode.FIXED))
                        setLayoutDisplayMode(
                                R.string.tv_menu_sound_settings_headphones_volume,
                                DisplayMode.SHOW);
                    else
                        setLayoutDisplayMode(
                                R.string.tv_menu_sound_settings_headphones_volume,
                                DisplayMode.DISABLE);
                } else {
                    spinnerHeadphoneOutputMode
                            .setSelection(AudioOutputMode.DISABLED.getValue());
                    setLayoutDisplayMode(
                            R.string.tv_menu_sound_settings_headphones_volume,
                            DisplayMode.DISABLE);
                }
                /******************************** HEADPHONES VOLUME *****************************/
                int hpv = 50;
                try {
                    hpv = soundSettings.getHeadphoneVolume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressHeadPhonesVolume.setProgress(hpv);
                /******************************** AUDIO DESCRIPTION *****************************/
                boolean isAudioDescription = false;
                try {
                    isAudioDescription = soundSettings.getAudioDescritpion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (isAudioDescription) {
                    buttonAudioDescription.setSelectedStateAndText(true,
                            R.string.button_text_on);
                } else {
                    buttonAudioDescription.setSelectedStateAndText(false,
                            R.string.button_text_off);
                }
                /******************************** ANALOG OUTPUT MODE *****************************/
                AudioOutputMode outputMode = null;
                try {
                    long deviceCount = ioControl.ioGetDevicesCount();
                    for (int i = 0; i < deviceCount; i++) {
                        // Since analog output mode is set for all analog audio
                        // outputs
                        // Find any analog outptut device and read it's mode
                        if (ioControl.ioGetDeviceOutput(i)) {
                            RouteInputOutputDescriptor deviceDescriptor = ioControl
                                    .ioGetDeviceDescriptor(i);
                            if (isAnalogDevice(deviceDescriptor)) {
                                // For now all output devices get set by the
                                // same mode, so reading any of them
                                // is enough
                                outputMode = ioControl
                                        .ioDeviceGetAudioOutputMode(i);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (outputMode != null) {
                    spinnerAnalogOutputMode.setSelection(outputMode.getValue());
                } else {
                    spinnerAnalogOutputMode.setSelection(0);
                }
                /******************************** TV SPEAKERS ENABLED *****************************/
                boolean tvSpeakersEnabled = true;
                try {
                    long deviceCount = ioControl.ioGetDevicesCount();
                    for (int i = 0; i < deviceCount; i++) {
                        RouteInputOutputDescriptor deviceDescriptor = ioControl
                                .ioGetDeviceDescriptor(i);
                        if (deviceDescriptor.getInputOutputDeviceType().equals(
                                RouteInputOutputDeviceType.SPEAKER)) {
                            AudioOutputMode speakersOutMode;
                            speakersOutMode = ioControl
                                    .ioDeviceGetAudioOutputMode(i);
                            if (speakersOutMode
                                    .equals(AudioOutputMode.DISABLED)) {
                                tvSpeakersEnabled = false;
                            } else {
                                tvSpeakersEnabled = true;
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (tvSpeakersEnabled) {
                    buttonTvSpeaker.setSelectedStateAndText(true,
                            R.string.button_text_on);
                } else {
                    buttonTvSpeaker.setSelectedStateAndText(false,
                            R.string.button_text_off);
                }
            }
            /******************************** BALANCE *****************************/
            int balance = 50;
            try {
                balance = soundSettings.getBalance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressBalance.setProgress(balance);
            /****************************** FIRST AUDIO *******************************/
            int first = 0;
            try {
                first = MainActivity.service.getAudioControl()
                        .getFirstAudioLanguage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            spinnerFirstAudio.setSelection(first);
            /****************************** SECOND AUDIO *******************************/
            int second = 0;
            try {
                second = MainActivity.service.getAudioControl()
                        .getSecondAudioLanguage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            spinnerSecondAudio.setSelection(second);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TV_MENU_SOUND_SETTINGS_AUTO_VOLUME: {
                if (buttonAutoVolume.isSelected()) {
                    try {
                        soundSettings.setAutoVolume(false);
                        buttonAutoVolume.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        soundSettings.setAutoVolume(true);
                        buttonAutoVolume.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_SRS_TRUSURROUND: {
                if (buttonSrsTruSurround.isSelected()) {
                    try {
                        soundSettings.setSoundEffectParam(
                                SoundEffectParam.SRS_TRUSURROUND, 0);
                        buttonSrsTruSurround.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        soundSettings.setSoundEffectParam(
                                SoundEffectParam.SRS_TRUSURROUND, 1);
                        buttonSrsTruSurround.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_SRS_TRUBASS: {
                if (buttonSrsTruBass.isSelected()) {
                    try {
                        soundSettings.setSoundEffectParam(
                                SoundEffectParam.SRS_TRUBASS, 0);
                        buttonSrsTruBass.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        soundSettings.setSoundEffectParam(
                                SoundEffectParam.SRS_TRUBASS, 1);
                        buttonSrsTruBass.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_SRS_DIALOG_CLARITY: {
                if (buttonSrsDialogClarity.isSelected()) {
                    try {
                        soundSettings.setSoundEffectParam(
                                SoundEffectParam.SRS_DIALOG_CLARITY, 0);
                        buttonSrsDialogClarity.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        soundSettings.setSoundEffectParam(
                                SoundEffectParam.SRS_DIALOG_CLARITY, 1);
                        buttonSrsDialogClarity.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_AUDIO_DESCRIPTION: {
                if (buttonAudioDescription.isSelected()) {
                    try {
                        soundSettings.setAudioDescription(false);
                        buttonAudioDescription.setSelectedStateAndText(false,
                                R.string.button_text_off);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        soundSettings.setAudioDescription(true);
                        buttonAudioDescription.setSelectedStateAndText(true,
                                R.string.button_text_on);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_TV_SPEAKER: {
                try {
                    long deviceCount = ioControl.ioGetDevicesCount();
                    for (int i = 0; i < deviceCount; i++) {
                        RouteInputOutputDescriptor deviceDescriptor = ioControl
                                .ioGetDeviceDescriptor(i);
                        if (deviceDescriptor.getInputOutputDeviceType().equals(
                                RouteInputOutputDeviceType.SPEAKER)) {
                            boolean tvSpeakersEnabled = buttonTvSpeaker
                                    .isSelected();
                            if (tvSpeakersEnabled) {
                                ioControl.ioDeviceSetAudioOutputMode(i,
                                        AudioOutputMode.DISABLED);
                                buttonTvSpeaker.setSelectedStateAndText(false,
                                        R.string.button_text_off);
                            } else {
                                ioControl.ioDeviceSetAudioOutputMode(i,
                                        AudioOutputMode.VARIABLE);
                                buttonTvSpeaker.setSelectedStateAndText(true,
                                        R.string.button_text_on);
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_POST_PROCESSING: {
                SoundPostProcessingDialog sppDialog = MainActivity.activity
                        .getDialogManager().getSoundPostProcessingDialog();
                if (sppDialog != null) {
                    sppDialog.show();
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_SET_DEFAULT_SETTINGS: {
                try {
                    soundSettings.setAudioMenuDefaultSettings();
                    updateSoundModeComponents(0);
                    MainActivity.service.getAudioControl()
                            .setFirstAudioLanguage(0);
                    MainActivity.service.getAudioControl()
                            .setSecondAudioLanguage(24);
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        ((A4TVProgressBar) seekBar).setText(String.valueOf(seekBar
                .getProgress()));
        switch (seekBar.getId()) {
            case TV_MENU_SOUND_SETTINGS_BALANCE: {
                try {
                    soundSettings.setBalance(progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_HEADPHONES_VOLUME: {
                try {
                    soundSettings.setHeadphoneVolume(progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOUND_SETTINGS_DELAY: {
                try {
                    long deviceCount = ioControl.ioGetDevicesCount();
                    for (int i = 0; i < deviceCount; i++) {
                        if (ioControl.ioGetDeviceOutput(i)) {
                            // For now all output devices get set by the same
                            // delay
                            ioControl.ioDeviceSetAudioDelay(i, progress);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        titleIDs.add(R.drawable.settings_icon);
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.string.tv_menu_sound_settings);
        ArrayList<Integer> list;
        // balance**********************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVProgressBar);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_sound_settings_balance);
        list.add(TV_MENU_SOUND_SETTINGS_BALANCE);
        contentListIDs.add(list);
        // first audio**********************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_sound_settings_first_audio);
        list.add(TV_MENU_SOUND_SETTINGS_FIRST_AUDIO);
        contentListIDs.add(list);
        // second audio**********************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_sound_settings_second_audio);
        list.add(TV_MENU_SOUND_SETTINGS_SECOND_AUDIO);
        contentListIDs.add(list);
        if (ConfigHandler.COMPLEX_AUDIO) {
            // audio
            // description*****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButtonSwitch);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_audio_description);
            list.add(TV_MENU_SOUND_SETTINGS_AUDIO_DESCRIPTION);
            contentListIDs.add(list);
            // sound mode******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_sound_mode);
            list.add(TV_MENU_SOUND_SETTINGS_SOUND_MODE);
            contentListIDs.add(list);
            // srs trusurround
            // *****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButtonSwitch);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_srs_trusurround);
            list.add(TV_MENU_SOUND_SETTINGS_SRS_TRUSURROUND);
            contentListIDs.add(list);
            // srs trubass *****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButtonSwitch);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_srs_trubass);
            list.add(TV_MENU_SOUND_SETTINGS_SRS_TRUBASS);
            contentListIDs.add(list);
            // srs trubass *****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButtonSwitch);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_srs_dialog_clarity);
            list.add(TV_MENU_SOUND_SETTINGS_SRS_DIALOG_CLARITY);
            contentListIDs.add(list);
            // sound post processing ******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_post_processing);
            list.add(TV_MENU_SOUND_SETTINGS_POST_PROCESSING);
            contentListIDs.add(list);
            // auto volume*************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButtonSwitch);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_auto_volume);
            list.add(TV_MENU_SOUND_SETTINGS_AUTO_VOLUME);
            contentListIDs.add(list);
            // spdif mode*************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_spdif_mode);
            list.add(TV_MENU_SOUND_SETTINGS_SPDIF_MODE);
            contentListIDs.add(list);
            // delay*****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVProgressBar);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_delay);
            list.add(TV_MENU_SOUND_SETTINGS_DELAY);
            contentListIDs.add(list);
            // headphones
            // headphone
            // mode*****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_headphones_mode);
            list.add(TV_MENU_SOUND_SETTINGS_HEADPHONE_OUTPUT_MODE);
            contentListIDs.add(list);
            // volume*****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVProgressBar);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_headphones_volume);
            list.add(TV_MENU_SOUND_SETTINGS_HEADPHONES_VOLUME);
            contentListIDs.add(list);
            // output *****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_analog_output_mode);
            list.add(TV_MENU_SOUND_SETTINGS_ANALOG_OUTPUT_MODE);
            contentListIDs.add(list);
            // tv speakers *****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButtonSwitch);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_tv_speakers);
            list.add(TV_MENU_SOUND_SETTINGS_TV_SPEAKER);
            contentListIDs.add(list);
            // sound set default settings
            // ******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVButton);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_set_default_settings);
            list.add(TV_MENU_SOUND_SETTINGS_SET_DEFAULT_SETTINGS);
            contentListIDs.add(list);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private boolean isAnalogDevice(RouteInputOutputDescriptor device) {
        switch (device.getInputOutputDeviceType()) {
            case SCART:
            case CVBS:
            case RGB:
            case VGA:
            case SVIDEO:
            case COMPONENT:
                return true;
            default:
                return false;
        }
    }

    private void setSrsEffectsVisibility(int visibility) {
        findViewById(R.string.tv_menu_sound_settings_srs_trusurround)
                .setVisibility(visibility);
        findViewById(R.string.tv_menu_sound_settings_srs_trubass)
                .setVisibility(visibility);
        findViewById(R.string.tv_menu_sound_settings_srs_dialog_clarity)
                .setVisibility(visibility);
        /* Hide the lines as well */
        findViewById(
                DialogCreatorClass.LINES_BASE_ID + SOUND_EFFECT_PARAM_OFFSET)
                .setVisibility(visibility);
        findViewById(
                DialogCreatorClass.LINES_BASE_ID + SOUND_EFFECT_PARAM_OFFSET
                        + 1).setVisibility(visibility);
        findViewById(
                DialogCreatorClass.LINES_BASE_ID + SOUND_EFFECT_PARAM_OFFSET
                        + 2).setVisibility(visibility);
    }

    private void setEqSettingVisibility(int visibility) {
        findViewById(R.string.tv_menu_sound_settings_post_processing)
                .setVisibility(visibility);
        findViewById(
                DialogCreatorClass.LINES_BASE_ID + SOUND_EFFECT_PARAM_OFFSET
                        + 3).setVisibility(visibility);
    }

    private void updateSoundModeComponents(int soundMode) {
        spinnerSoundMode.setSelection(soundMode);
        if (soundMode == 0) {
            /* No effects - Normal */
            Log.d(TAG, "SOUND MODE: Normal selected");
            setSrsEffectsVisibility(View.GONE);
            setEqSettingVisibility(View.VISIBLE);
        } else if (soundMode == 1) {
            /* SRS Effects */
            Log.d(TAG, "SOUND MODE: SRS selected");
            setSrsEffectsVisibility(View.VISIBLE);
            setEqSettingVisibility(View.GONE);
        } else {
            /* SpatialStereo Effects */
            Log.d(TAG, "SOUND MODE: Spatial selected");
            setSrsEffectsVisibility(View.GONE);
            setEqSettingVisibility(View.VISIBLE);
        }
    }
}
