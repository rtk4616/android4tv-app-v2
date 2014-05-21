package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.IInputOutputControl;
import com.iwedia.comm.system.ISoundSettings;
import com.iwedia.dtv.sound.AudioEqualizerBand;
import com.iwedia.dtv.sound.SoundMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Sound settings dialog
 * 
 * @author Marko Krnjetin
 */
public class SoundPostProcessingDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener,
        OnSeekBarChangeListener {
    private static final String TAG = "SoundSettingsDialog";
    private static final int EQ_BANDS_NO = 5;
    private static final int EQ_NEUTRAL_POWER = 10;
    /** IDs for spinner */
    public static final int TV_MENU_SOUND_PP_SOUND_MODE = 39799;
    /** IDs for progress */
    public static final int TV_MENU_SOUND_PP_TREBLE = 39798,
            TV_MENU_SOUND_PP_BASS = 39797,
            TV_MENU_SOUND_PP_EQ_BANDS_START = 39800;
    public static final int eqBandNames[] = {
            R.string.tv_menu_sound_settings_eq_band_1,
            R.string.tv_menu_sound_settings_eq_band_2,
            R.string.tv_menu_sound_settings_eq_band_3,
            R.string.tv_menu_sound_settings_eq_band_4,
            R.string.tv_menu_sound_settings_eq_band_5 };
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    /** Spinners **/
    private A4TVSpinner spinnerSoundMode;
    /** Progress bars */
    private A4TVProgressBar progressTreble, progressBass;
    private A4TVProgressBar eqBandProgress[] = null;
    private ISoundSettings soundSettings;
    private IInputOutputControl ioControl = null;

    public SoundPostProcessingDialog(Context context) {
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
            /** Spinners */
            spinnerSoundMode = (A4TVSpinner) findViewById(TV_MENU_SOUND_PP_SOUND_MODE);
            spinnerSoundMode
                    .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                        @Override
                        public void onSelect(A4TVSpinner spinner, int index,
                                String[] contents) {
                            try {
                                soundSettings.setActiveSoundMode(SoundMode
                                        .values()[index]);
                                show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            /** Progress bar */
            progressTreble = (A4TVProgressBar) findViewById(TV_MENU_SOUND_PP_TREBLE);
            progressBass = (A4TVProgressBar) findViewById(TV_MENU_SOUND_PP_BASS);
            try {
                int eqBandsNo = soundSettings.getNumberOfEqualizerBands();
                if (eqBandsNo == EQ_BANDS_NO) {
                    eqBandProgress = new A4TVProgressBar[EQ_BANDS_NO];
                    for (int i = 0; i < eqBandsNo; i++) {
                        eqBandProgress[i] = (A4TVProgressBar) findViewById(TV_MENU_SOUND_PP_EQ_BANDS_START
                                + i);
                        eqBandProgress[i].setMax(EQ_NEUTRAL_POWER * 2);
                    }
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void show() {
        Log.d("SoundPP", "Entering show()");
        fillViews();
        super.show();
    }

    private void fillViews() {
        soundSettings = null;
        Log.d("SoundPP", "Entering fillViews()");
        try {
            soundSettings = MainActivity.service.getSystemControl()
                    .getSoundControl();
            ioControl = MainActivity.service.getInputOutputControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (soundSettings != null) {
            if (ConfigHandler.COMPLEX_AUDIO) {
                /******************************** SOUND MODE *****************************/
                int soundMode = 0;
                try {
                    soundMode = soundSettings.getActiveSoundMode().getValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("SoundPP", "Got sound mode:" + soundMode);
                spinnerSoundMode.setSelection(soundMode);
                setSoundControleVisibility(soundMode);
                /******************************** TREBLE *****************************/
                int treble = 50;
                try {
                    Log.d("SoundPP", "Updating treble fillViews()");
                    treble = soundSettings.getTreble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressTreble.setProgress(treble);
                Log.d("SoundPP", "Updatated treble fillViews()");
                /******************************** BASS *****************************/
                int bass = 50;
                try {
                    bass = soundSettings.getBass();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressBass.setProgress(bass);
                /******************************** EQ *****************************/
                try {
                    if (eqBandProgress != null) {
                        int eqBandsNo = soundSettings
                                .getNumberOfEqualizerBands();
                        for (int i = 0; i < eqBandsNo; i++) {
                            int value = soundSettings
                                    .getEqualizerBandValue(AudioEqualizerBand
                                            .getFromValue(i));
                            Log.d("SoundPP", "Setting EQ progress: " + i
                                    + " progress:" + (value + EQ_NEUTRAL_POWER));
                            eqBandProgress[i].setProgress(value
                                    + EQ_NEUTRAL_POWER);
                        }
                    }
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        ((A4TVProgressBar) seekBar).setText(String.valueOf(seekBar
                .getProgress()));
        switch (seekBar.getId()) {
            case TV_MENU_SOUND_PP_TREBLE: {
                try {
                    soundSettings.setTreble(progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case TV_MENU_SOUND_PP_BASS: {
                try {
                    soundSettings.setBass(progress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            default:
                try {
                    /* Check the EQ bands */
                    int eqBandsNo = soundSettings.getNumberOfEqualizerBands();
                    for (int i = 0; i < eqBandsNo; i++) {
                        if (seekBar.getId() == TV_MENU_SOUND_PP_EQ_BANDS_START
                                + i) {
                            Log.d("SoundPP", "Setting EQ value: " + i
                                    + " progress:"
                                    + (progress - EQ_NEUTRAL_POWER));
                            soundSettings.setEqualizerBandValue(
                                    AudioEqualizerBand.getFromValue(i),
                                    progress - EQ_NEUTRAL_POWER);
                            break;
                        }
                    }
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
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
        if (ConfigHandler.COMPLEX_AUDIO) {
            // sound mode******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVSpinner);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_sound_mode);
            list.add(TV_MENU_SOUND_PP_SOUND_MODE);
            contentListIDs.add(list);
            // treble*******************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVProgressBar);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_treble);
            list.add(TV_MENU_SOUND_PP_TREBLE);
            contentListIDs.add(list);
            // bass****************************************************
            list = new ArrayList<Integer>();
            list.add(MainMenuContent.TAGA4TVTextView);
            list.add(MainMenuContent.TAGA4TVProgressBar);
            contentList.add(list);
            list = new ArrayList<Integer>();
            list.add(R.string.tv_menu_sound_settings_bass);
            list.add(TV_MENU_SOUND_PP_BASS);
            contentListIDs.add(list);
            try {
                soundSettings = MainActivity.service.getSystemControl()
                        .getSoundControl();
                /*
                 * For now only show EQ if it is a 5 band EQ. We cannot
                 * dynamically set the string for the slider (it has to be
                 * predefined in xml). So for now we only support 5 band EQ
                 * (this is what we use in MW now)
                 */
                int eqBandsNo;
                eqBandsNo = soundSettings.getNumberOfEqualizerBands();
                if (eqBandsNo == EQ_BANDS_NO) {
                    for (int i = 0; i < eqBandsNo; i++) {
                        list = new ArrayList<Integer>();
                        list.add(MainMenuContent.TAGA4TVTextView);
                        list.add(MainMenuContent.TAGA4TVProgressBar);
                        contentList.add(list);
                        // int eqBandFreq =
                        // soundSettings.getEqualizerBandFrequency(i);
                        list = new ArrayList<Integer>();
                        list.add(eqBandNames[i]);
                        list.add(TV_MENU_SOUND_PP_EQ_BANDS_START + i);
                        contentListIDs.add(list);
                    }
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void setSoundControleVisibility(int soundMode) {
        int displayMode = (soundMode == 5) ? DisplayMode.SHOW
                : DisplayMode.DISABLE;
        setLayoutDisplayMode(R.string.tv_menu_sound_settings_treble,
                displayMode);
        setLayoutDisplayMode(R.string.tv_menu_sound_settings_bass, displayMode);
        try {
            int eqBandsNo = soundSettings.getNumberOfEqualizerBands();
            if (eqBandsNo == EQ_BANDS_NO) {
                for (int i = 0; i < eqBandsNo; i++) {
                    setLayoutDisplayMode(eqBandNames[i], displayMode);
                }
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
