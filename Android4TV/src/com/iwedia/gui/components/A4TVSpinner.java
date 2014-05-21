package com.iwedia.gui.components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.iwedia.comm.enums.FontScale;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.comm.system.date_time.TimeZone;
import com.iwedia.dtv.io.SpdifMode;
import com.iwedia.dtv.sound.SoundMode;
import com.iwedia.dtv.subtitle.SubtitleMode;
import com.iwedia.dtv.subtitle.SubtitleType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.ThemeUtils;
import com.iwedia.gui.components.dialogs.ApplicationsManageManageAppsDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationManualTunningDialog;
import com.iwedia.gui.components.dialogs.InputDevicesSettingsDialog;
import com.iwedia.gui.components.dialogs.LanguageAndKeyboardDialog;
import com.iwedia.gui.components.dialogs.NetworkAdvancedManualConfigDialog;
import com.iwedia.gui.components.dialogs.NetworkSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessAddHiddenNetworkDialog;
import com.iwedia.gui.components.dialogs.OSDSelectionDialog;
import com.iwedia.gui.components.dialogs.OffTimersAddDialog;
import com.iwedia.gui.components.dialogs.PVRManualEventReminderDialog;
import com.iwedia.gui.components.dialogs.PVRManualScheduleDialog;
import com.iwedia.gui.components.dialogs.PVRSettingsDialog;
import com.iwedia.gui.components.dialogs.ParentalGuidanceDialog;
import com.iwedia.gui.components.dialogs.PasswordSecurityDialog;
import com.iwedia.gui.components.dialogs.PiPSettingsDialog;
import com.iwedia.gui.components.dialogs.PictureSettingsDialog;
import com.iwedia.gui.components.dialogs.SoundPostProcessingDialog;
import com.iwedia.gui.components.dialogs.SoundSettingsDialog;
import com.iwedia.gui.components.dialogs.SubtitleSettingsDialog;
import com.iwedia.gui.components.dialogs.SystemSettingsDialog;
import com.iwedia.gui.components.dialogs.TeletextSettingsDialog;
import com.iwedia.gui.components.dialogs.TimeAndDateSettingsDialog;
import com.iwedia.gui.components.dialogs.VoiceInputDialog;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.pvr.A4TVStorageManager;
import com.iwedia.gui.pvr.A4TVUSBStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * This is implemented for theme change
 * 
 * @author Branimir Pavlovic
 */
public class A4TVSpinner extends Button implements OnClickListener { // Spinner
    // {
    private final String TAG = "A4TVSpinner";
    private Context ctx;
    private int spinnerBackgroundPictureID;
    private int CHOOSEN_ITEM_INDEX = 0;
    private String[] contents;
    /** Dialog that act like spinners drop down list */
    private A4TVDialog dialogContext;

    /**
     * Interface for notifying when item is selected in spinner context dialog
     * 
     * @author Branimir Pavlovic
     */
    public interface OnSelectA4TVSpinnerListener {
        public void onSelect(A4TVSpinner spinner, int index, String[] contents);
    }

    private OnSelectA4TVSpinnerListener mOnSelectA4TVSpinnerListener;

    public A4TVSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        init(ctx);
    }

    public A4TVSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
        init(ctx);
    }

    public A4TVSpinner(Context context) {
        super(context);
        ctx = context;
        init(ctx);
    }

    private void init(Context context) {
        setTag(MainMenuContent.TAGA4TVSpinner);
        TypedArray atts = context.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVSpinner });
        this.spinnerBackgroundPictureID = atts.getResourceId(0, 0);
        setBackgroundResource(this.spinnerBackgroundPictureID);
        atts.recycle();
        setOnClickListener(this);
        setMaxLines(2);
        // scrollable text
        setEllipsize(TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setSingleLine();
        setHorizontallyScrolling(true);
        setFocusableInTouchMode(true);
        initDropDownDialog();
    }

    /**
     * Initialize context dialog
     */
    private void initDropDownDialog() {
        dialogContext = MainActivity.activity.getDialogManager()
                .getContextSmallDialog();
        dialogContext.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d(TAG, "SPINNER CONTEXT DIALOG CANCEL");
                A4TVSpinner.this.setSelected(false);
            }
        });
    }

    /**
     * Set initial text to spinner
     * 
     * @param index
     */
    public void setInitialText() {
        Log.d(TAG, "SET INITIAL TEXT");
        String[] strings;
        strings = getDialogContextItems(getId());
        if (strings.length > 0) {
            setText(strings[0]);
        }
    }

    /**
     * Set selection to spinner
     * 
     * @param index
     */
    public void setSelection(int index) {
        if (contents == null) {
            contents = getDialogContextItems(getId());
        }
        if (contents.length > index) {
            setText(contents[index]);
            CHOOSEN_ITEM_INDEX = index;
        }
    }

    /**
     * Set selection to spinner
     * 
     * @param index
     */
    public boolean setSelectionByString(String what) {
        if (contents == null) {
            contents = getDialogContextItems(getId());
        }
        for (int i = 0; i < contents.length; i++) {
            if (what.equalsIgnoreCase(contents[i])) {
                Log.d(TAG, "METHOD (setSelectionByString) SELECTED: " + what);
                setSelection(i);
                return true;
            }
        }
        Log.d(TAG, "METHOD (setSelectionByString) NOT FOUND: " + what);
        return false;
    }

    /**
     * Again populate context dialog with refreshed elements
     */
    public void refreshSpinnerElements() {
        dialogContext.setContentView(fillDialogWithElements(this));
    }

    /**
     * On Spinner click listener
     */
    @Override
    public void onClick(View v) {
        // get dialog background from theme
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialog });
        atts.recycle();
        Log.d(TAG, "SPINNER ON CLICK");
        // //////////////////////////////////////
        // Veljko Ilkic
        // //////////////////////////////////////
        // dialogContext = MainActivity.activity.getDialogManager()
        // .getContextSmallDialog();
        // fill dialog with desired view
        dialogContext.setContentView(fillDialogWithElements(v));
        // set dialog size
        dialogContext.getWindow().getAttributes().width = v.getWidth();
        dialogContext.getWindow().getAttributes().height = MainActivity.dialogHeight / 3;
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        // set location
        dialogContext.getWindow().getAttributes().x = -MainActivity.screenWidth
                / 2 + location[0] + v.getWidth() / 2;
        // PE Android4TV
        if (MainActivity.screenHeight != MainActivity.SCREEN_HEIGHT_720P
                && MainActivity.screenHeight != MainActivity.SCREEN_HEIGHT_1080P) {
            dialogContext.getWindow().getAttributes().y = -MainActivity.screenHeight
                    / 2
                    + location[1]
                    + v.getHeight()
                    / 2
                    + dialogContext.getWindow().getAttributes().height / 2;
        }
        // AMP Android4TV
        else {
            dialogContext.getWindow().getAttributes().y = -MainActivity.screenHeight
                    / 2
                    + location[1]
                    + v.getHeight()
                    + dialogContext.getWindow().getAttributes().height / 2;
        }
        // show drop down dialog
        dialogContext.show();
        setSelected(true);
    }

    /**
     * Creates view for context dialog
     * 
     * @param spinnerID
     * @return
     */
    public View fillDialogWithElements(final View v) {
        LinearLayout mainLinLayout = new LinearLayout(ctx);
        mainLinLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mainLinLayout.setOrientation(LinearLayout.VERTICAL);
        // get drawable from theme for image source
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogContextBackground });
        int backgroundID = atts.getResourceId(0, 0);
        atts.recycle();
        mainLinLayout.setBackgroundResource(backgroundID);
        // create scroll view
        ScrollView mainScrollView = new ScrollView(ctx);
        mainScrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mainScrollView.setScrollbarFadingEnabled(false);
        mainScrollView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        // add scrollview to main view
        mainLinLayout.addView(mainScrollView);
        LinearLayout contentLinearLayout = new LinearLayout(ctx);
        contentLinearLayout.setLayoutParams(new ScrollView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        contentLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        // add content layout to scroll view
        mainScrollView.addView(contentLinearLayout);
        contents = getDialogContextItems(v.getId());
        for (int i = 0; i < contents.length; i++) {
            // create small layout
            final LinearLayout smallLayoutHorizontal = new LinearLayout(ctx);
            smallLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            smallLayoutHorizontal
                    .setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            MainActivity.dialogListElementHeight));
            smallLayoutHorizontal.setPadding(10, 4, 10, 4);
            smallLayoutHorizontal.setGravity(Gravity.CENTER_VERTICAL);
            // create drop box item
            A4TVButton button = new A4TVButton(ctx, null);
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            button.setText(contents[i]);
            button.setGravity(Gravity.CENTER);
            button.setId(i);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View vi) {
                    if (CHOOSEN_ITEM_INDEX == ((A4TVButton) vi).getId()) {
                        new A4TVToast(ctx).showToast(R.string.already_active);
                        dialogContext.cancel();
                        return;
                    }
                    CHOOSEN_ITEM_INDEX = ((A4TVButton) vi).getId();
                    if (v.getId() != ParentalGuidanceDialog.TV_MENU_PARENTIAL_SECURITY_SETTINGS_PARENTIAL_GUIDANCE) {
                        dialogContext.cancel();
                        A4TVSpinner.this.setText(((A4TVButton) vi).getText());
                        setSelected(false);
                    }
                    if (null != mOnSelectA4TVSpinnerListener) {
                        mOnSelectA4TVSpinnerListener.onSelect(A4TVSpinner.this,
                                CHOOSEN_ITEM_INDEX, contents);
                    }
                }
            });
            // set focus listener of button
            button.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // get drawable from theme for small layout
                    // background
                    TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                            new int[] { R.attr.LayoutFocusDrawable });
                    int backgroundID = atts.getResourceId(0, 0);
                    atts.recycle();
                    if (hasFocus) {
                        v.setSelected(true);
                        smallLayoutHorizontal
                                .setBackgroundResource(backgroundID);
                    } else {
                        v.setSelected(false);
                        smallLayoutHorizontal
                                .setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            });
            button.setBackgroundColor(Color.TRANSPARENT);
            if (i == CHOOSEN_ITEM_INDEX) {
                button.requestFocus();
            }
            smallLayoutHorizontal.addView(button);
            contentLinearLayout.addView(smallLayoutHorizontal);
            if (i < contents.length - 1) {
                // create horizontal line
                ImageView horizLine = new ImageView(ctx);
                android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        v.getWidth() - 10,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                horizLine.setLayoutParams(params);
                // get drawable from theme for image source
                atts = ctx.getTheme().obtainStyledAttributes(
                        new int[] { R.attr.DialogContextDividerLine });
                backgroundID = atts.getResourceId(0, 0);
                horizLine.setImageResource(backgroundID);
                // add view
                contentLinearLayout.addView(horizLine);
            }
        }
        return mainLinLayout;
    }

    /**
     * get spinner items
     * 
     * @param spinnerID
     */
    private String[] getDialogContextItems(int spinnerID) {
        switch (spinnerID) {
            case PictureSettingsDialog.PICTURE_SETTINGS_PICTURE_MODE: {
                return ctx.getResources().getStringArray(
                        R.array.picture_mode_settings);
            }
            case InputDevicesSettingsDialog.tv_menu_factory_input_settings_input_labels: {
                // TODO:
                return new String[0];
            }
            case PictureSettingsDialog.PICTURE_SETTINGS_THEME: {
                return ThemeUtils.getThemes();
            }
            case PictureSettingsDialog.PICTURE_SETTINGS_NR: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_picture_settings_nr);
            }
            case PictureSettingsDialog.PICTURE_SETTINGS_ASPECT_RATIO: {
                switch (PictureSettingsDialog.getCurrentInputTypeGroup(0 /*
                                                                          * display
                                                                          * id
                                                                          */)) {
                    case PictureSettingsDialog.DigitalInputTypeGroup: // If
                                                                      // digital
                        // tuner, HDMI
                        // or Component
                        // source is
                        // active the
                        // following
                        // picture
                        // formats shall
                        // be supported:
                        // - Auto -
                        // Normal 4:3 -
                        // Zoom 14:9 -
                        // Panorama -
                        // Letterbox -
                        // Full - Cinema
                        // 16:9 - Cinema
                        // 14:9
                        return ctx
                                .getResources()
                                .getStringArray(
                                        R.array.tv_menu_picture_settings_aspect_ratio_digital_group);
                    case PictureSettingsDialog.AnalogInputTypeGroup: // * If
                                                                     // analog
                        // tuner, SCART
                        // or Composite
                        // source is
                        // active the
                        // following
                        // picture
                        // formats shall
                        // be supported:
                        // - Auto -
                        // Normal 4:3 -
                        // Zoom 14:9 -
                        // Panorama -
                        // Letterbox -
                        // Cinema 16:9 -
                        // Cinema 14:9
                        return ctx
                                .getResources()
                                .getStringArray(
                                        R.array.tv_menu_picture_settings_aspect_ratio_analog_group);
                    case PictureSettingsDialog.VGAInputTypeGroup: // * If VGA
                                                                  // (PC)
                        // source is active,
                        // device shall
                        // support following
                        // picture formats:
                        // - Normal 4:3 -
                        // Cinema 16:9
                        return ctx
                                .getResources()
                                .getStringArray(
                                        R.array.tv_menu_picture_settings_aspect_ratio_vga_group);
                }
                return ctx
                        .getResources()
                        .getStringArray(
                                R.array.tv_menu_picture_settings_aspect_ratio_digital_group);
            }
            case ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE: {
                // ///////////////////////////
                // Check config mode
                // ///////////////////////////
                ArrayList<String> spinnerOptionsList = new ArrayList<String>();
                if (ConfigHandler.DVB_S) {
                    if (ConfigHandler.ATSC) {
                        spinnerOptionsList.add(getResources().getString(
                                R.string.main_menu_content_list_atsc_s));
                    } else {
                        spinnerOptionsList.add(getResources().getString(
                                R.string.main_menu_content_list_dvb_s));
                    }
                }
                if (ConfigHandler.DVB_T) {
                    if (ConfigHandler.ATSC) {
                        spinnerOptionsList
                                .add(getResources()
                                        .getString(
                                                R.string.tv_menu_channel_installation_settings_air));
                    } else {
                        spinnerOptionsList.add(getResources().getString(
                                R.string.main_menu_content_list_dvb_t));
                    }
                }
                if (ConfigHandler.DVB_C) {
                    if (ConfigHandler.ATSC) {
                        spinnerOptionsList
                                .add(getResources()
                                        .getString(
                                                R.string.tv_menu_channel_installation_settings_cable));
                    } else {
                        spinnerOptionsList.add(getResources().getString(
                                R.string.main_menu_content_list_dvb_c));
                    }
                }
                if (ConfigHandler.IP) {
                    spinnerOptionsList.add(getResources().getString(
                            R.string.main_menu_content_list_ip));
                }
                if (!ConfigHandler.ATSC) {
                    if (ConfigHandler.ATV) {
                        spinnerOptionsList.add(getResources().getString(
                                R.string.main_menu_content_list_atv));
                    }
                }
                Log.d(TAG, spinnerOptionsList.toString());
                String[] spinnerContent = new String[spinnerOptionsList.size()];
                for (int i = 0; i < spinnerOptionsList.size(); i++) {
                    spinnerContent[i] = spinnerOptionsList.get(i);
                }
                return spinnerContent;
            }
            case LanguageAndKeyboardDialog.TV_MENU_LANGUAGE_SETTINGS_TEXT_SIZE: {
                // TODO: use from proxy service
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_language_settings_text_size_array);
            }
            case LanguageAndKeyboardDialog.TV_MENU_LANGUAGE_SETTINGS_SELECT_COUNTRY: {
                // List<String> countryList = null;
                int count = 0;
                try {
                    count = MainActivity.service.getSetupControl()
                            .getCountryCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] coountries = new String[count];
                for (int i = 0; i < count; i++) {
                    try {
                        coountries[i] = MainActivity.service.getSetupControl()
                                .getCountry(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // if (countryList == null) {
                // countryList = new ArrayList<String>();
                // countryList.add("Country 1");
                // countryList.add("Country 2");
                // countryList.add("Country 3");
                // }
                // Log.d(TAG, "select country " + countryList.toString());
                // String[] strarray = new String[countryList.size()];
                // countryList.toArray(strarray);
                return coountries;
            }
            case LanguageAndKeyboardDialog.TV_MENU_LANGUAGE_SETTINGS_SELECT_LANGUAGE: {
                // TODO: use from proxy service
                List<String> languageList = null;
                try {
                    languageList = MainActivity.service.getSystemControl()
                            .getLanguageAndKeyboardControl()
                            .getAvailableLanguages();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                if (languageList == null) {
                    languageList = new ArrayList<String>();
                    languageList.add("Language 1");
                    languageList.add("Language 2");
                    languageList.add("Language 3");
                }
                Log.d(TAG, "Languages " + languageList.toString());
                String[] strarray = new String[languageList.size()];
                languageList.toArray(strarray);
                return strarray;
            }
            case NetworkSettingsDialog.TV_MENU_NETWORK_SETTINGS_NETWORK_TYPE: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_network_settings_network_type);
            }
            case NetworkAdvancedManualConfigDialog.TV_MENU_NETWORK_SETTINGS_ADDRESS_TYPE: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_network_settings_address_type);
            }
            case PiPSettingsDialog.PIP_SETTINGS_SET_POSITION: {
                return ctx.getResources().getStringArray(
                        R.array.pip_settings_position);
            }
            case PiPSettingsDialog.PIP_SETTINGS_SET_SIZE: {
                return ctx.getResources().getStringArray(
                        R.array.pip_settings_size);
            }
            case SoundSettingsDialog.TV_MENU_SOUND_SETTINGS_SOUND_MODE: {
                return ctx.getResources().getStringArray(R.array.sound_mode);
            }
            case SoundPostProcessingDialog.TV_MENU_SOUND_PP_SOUND_MODE: {
                return ctx.getResources().getStringArray(
                        R.array.sound_mode_settings);
            }
            case SoundSettingsDialog.TV_MENU_SOUND_SETTINGS_SPDIF_MODE: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_sound_settings_spdif_mode_spinner);
            }
            case SoundSettingsDialog.TV_MENU_SOUND_SETTINGS_HEADPHONE_OUTPUT_MODE:
            case SoundSettingsDialog.TV_MENU_SOUND_SETTINGS_ANALOG_OUTPUT_MODE: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_sound_settings_output_mode_spinner);
            }
            case SoundSettingsDialog.TV_MENU_SOUND_SETTINGS_SECOND_AUDIO:
            case SoundSettingsDialog.TV_MENU_SOUND_SETTINGS_FIRST_AUDIO: {
                int count = 0;
                try {
                    count = MainActivity.service.getSetupControl()
                            .getLanguageCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] languages = new String[count];
                for (int i = 0; i < count; i++) {
                    try {
                        languages[i] = MainActivity.service.getSetupControl()
                                .getLanguageName(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return languages;
            }
            case TimeAndDateSettingsDialog.TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_TIME_ZONE: {
                List<TimeZone> timeZones = null;
                try {
                    timeZones = MainActivity.service.getSystemControl()
                            .getDateAndTimeControl().getTimeZones();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                if (timeZones == null) {
                    timeZones = new ArrayList<TimeZone>();
                }
                Log.d(TAG, "LOADED TIME ZONES: " + timeZones.size() + " "
                        + timeZones.get(0).getDisplayName());
                String[] strarray = new String[timeZones.size()];
                StringBuilder builder;
                for (int i = 0; i < timeZones.size(); i++) {
                    builder = new StringBuilder();
                    strarray[i] = builder
                            .append(timeZones.get(i).getDisplayName())
                            .append("\n").append(timeZones.get(i).getGmt())
                            .toString();
                }
                return strarray;
            }
            case ApplicationsManageManageAppsDialog.TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_SPINNER: {
                return ctx
                        .getResources()
                        .getStringArray(
                                R.array.tv_menu_applications_settings_manage_applications);
            }
            case VoiceInputDialog.TV_MENU_VOICE_SETTINGS_LANGUAGE: {
                // TODO: get languages
                return new String[0];
            }
            case TimeAndDateSettingsDialog.TV_MENU_TIME_AND_DATE_SETTINGS_SELECT_DATE_FORMAT: {
                return ctx
                        .getResources()
                        .getStringArray(
                                R.array.tv_menu_time_and_date_settings_select_date_format);
            }
            case SubtitleSettingsDialog.TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_SECOND_LANGUAGE:
            case SubtitleSettingsDialog.TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_FIRST_LANGUAGE: {
                int count = 0;
                try {
                    count = MainActivity.service.getSetupControl()
                            .getLanguageCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] languages = new String[count];
                for (int i = 0; i < count; i++) {
                    try {
                        languages[i] = MainActivity.service.getSetupControl()
                                .getLanguageName(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return languages;
            }
            case SubtitleSettingsDialog.TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_MODE: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_subtitle_settings_subtitle_mode);
            }
            case SubtitleSettingsDialog.TV_MENU_SUBTITLE_SETTINGS_SUBTITLE_TYPE: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_subtitle_settings_subtitle_type);
            }
            case TeletextSettingsDialog.TV_MENU_TELETEXT_SETTINGS_TELETEXT_SECOND_LANGUAGE:
            case TeletextSettingsDialog.TV_MENU_TELETEXT_SETTINGS_TELETEXT_FIRST_LANGUAGE: {
                int count = 0;
                try {
                    count = MainActivity.service.getSetupControl()
                            .getLanguageCount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] languages = new String[count];
                for (int i = 0; i < count; i++) {
                    try {
                        languages[i] = MainActivity.service.getSetupControl()
                                .getLanguageName(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return languages;
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE: {
                return ctx.getResources().getStringArray(
                        R.array.manual_tunning_settings_symbol_rate);
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_polarization: {
                return ctx.getResources().getStringArray(
                        R.array.manual_tunning_settings_polarization);
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_MODULATION: {
                return ctx.getResources().getStringArray(
                        R.array.manual_tunning_settings_modulation);
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SCRAMBLED: {
                return ctx.getResources().getStringArray(
                        R.array.manual_tunning_settings_scrambled);
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYSTEM: {
                return ctx.getResources().getStringArray(
                        R.array.manual_tunning_settings_system);
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NORDIC_BAND: {
                return ctx.getResources().getStringArray(
                        R.array.manual_tunning_nordic_band);
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FEC: {
                return ctx.getResources().getStringArray(
                        R.array.manual_tunning_settings_fec);
            }
            case ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TABLE_TYPE: {
                return ctx.getResources().getStringArray(
                        R.array.channel_installation_table_type);
            }
            case ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_CHANNEL_TUNING_MODE: {
                return ctx.getResources().getStringArray(
                        R.array.channel_installation_channel_tuning_mode);
            }
            // /////////////////////////////////////////////////
            // Veljko Ilkic
            // /////////////////////////////////////////////////
            case ChannelInstallationDialog.SATELLITE_NAME_SPINNER: {
                Log.d(TAG, "SATELLITE NAMES");
                // Get scan control
                /*
                 * IScanControl scanControl = MainActivity.service
                 * .getScanControl();
                 */
                // Get number of satellites
                /* int numberOfSatellites = scanControl.getNumberOfSatellites(); */
                String[] satelliteNames = new String[2];
                /*
                 * for (int i = 0; i < numberOfSatellites; i++) {
                 * Log.d("SATELLITE NAME", scanControl.getSatelliteName(i) +
                 * ""); satelliteNames[i] = scanControl.getSatelliteName(i); }
                 */
                return satelliteNames;
            }
            case OSDSelectionDialog.CURL_TIME_DELAY_SPINNER: {
                return ctx
                        .getResources()
                        .getStringArray(
                                R.array.picture_settings_curl_settings_delay_time_array);
            }
            case OSDSelectionDialog.CURL_ENABLED_ID: {
                return ctx.getResources().getStringArray(
                        R.array.osd_selection_mode);
            }
            case PictureSettingsDialog.PICTURE_SETTINGS_COLOR_TEMPERATURE: {
                return ctx
                        .getResources()
                        .getStringArray(
                                R.array.tv_menu_picture_settings_color_temperature_values);
            }
            case OffTimersAddDialog.TV_MENU_OFFTIMERS_SETTINGS_REPEAT_MODE: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_off_timers_repeat_mode);
            }
            case NetworkWirelessAddHiddenNetworkDialog.TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_SECURITY: {
                return ctx
                        .getResources()
                        .getStringArray(
                                R.array.tv_menu_network_wireless_add_hidden_network_security);
            }
            case PVRManualEventReminderDialog.TV_MENU_PVR_MANUAL_REMINDER_CHANNEL: {
                int count = 0;
                try {
                    count = MainActivity.service.getServiceControl()
                            .getServiceListCount(ServiceListIndex.MASTER_LIST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] channel = new String[count];
                for (int i = 0; i < count; i++) {
                    try {
                        channel[i] = MainActivity.service
                                .getContentListControl()
                                .getContentByIndexInMasterList(i).getName();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return channel;
            }
            case PVRManualScheduleDialog.TV_MENU_PVR_SCHEDULE_REMINDER_CHANNEL: {
                int count = 0;
                try {
                    count = MainActivity.service.getServiceControl()
                            .getServiceListCount(ServiceListIndex.MASTER_LIST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String[] channel = new String[count];
                for (int i = 0; i < count; i++) {
                    try {
                        channel[i] = MainActivity.service
                                .getContentListControl()
                                .getContentByIndexInMasterList(i).getName();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return channel;
            }
            case PVRManualScheduleDialog.TV_MENU_PVR_SCHEDULE_RECORDING_REPEAT: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_manual_pvr_schedule_repeat);
            }
            case PVRManualEventReminderDialog.TV_MENU_PVR_MANUAL_REMINDER_REPEAT: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_manual_pvr_schedule_repeat);
            }
            case ParentalGuidanceDialog.TV_MENU_PARENTIAL_SECURITY_SETTINGS_PARENTIAL_GUIDANCE: {
                ArrayList<String> guidanceLevel = new ArrayList<String>();
                // fill list
                // first add OFF
                guidanceLevel.add(ctx.getResources().getString(
                        R.string.button_text_off));
                String underString = ctx.getResources().getString(
                        R.string.under)
                        + " ";
                // add rest
                for (int i = 4; i < 19; i++) {
                    guidanceLevel.add(underString + String.valueOf(i));
                }
                String[] strarray = new String[guidanceLevel.size()];
                guidanceLevel.toArray(strarray);
                return strarray;
            }
            case PVRSettingsDialog.TV_MENU_PVR_SETTINGS_DEVICE: {
                A4TVStorageManager storage = new A4TVStorageManager();
                int count;
                count = storage.getNumberOfUSBSorages();
                String[] devices = new String[count];
                for (int i = 0; i < count; i++) {
                    A4TVUSBStorage usbStorage = storage.getUSBStorage(i);
                    if (usbStorage != null) {
                        devices[i] = usbStorage.getDescription();
                    }
                }
                return devices;
            }
            case SystemSettingsDialog.TV_MENU_SYSTEM_SETTINGS_INPUT_MODE_START: {
                return ctx.getResources().getStringArray(
                        R.array.tv_menu_system_settings_input_mode);
            }
            /*
             * case
             * DebuggingDataDialog.TV_MENU_DEBUGGING_DATA_NORMAL_STANDBY_CAUSE:
             * { return ctx.getResources().getStringArray(
             * R.array.tv_menu_debugging_data_normal_standby_cause); }
             */
            default:
                return new String[0];
        }
    }

    public int getCHOOSEN_ITEM_INDEX() {
        return CHOOSEN_ITEM_INDEX;
    }

    // ///////////////////////////////////
    // Veljko Ilkic
    // ///////////////////////////////////
    public A4TVDialog getDialogContext() {
        return dialogContext;
    }

    public OnSelectA4TVSpinnerListener getOnSelectA4TVSpinnerListener() {
        return mOnSelectA4TVSpinnerListener;
    }

    public void setOnSelectA4TVSpinnerListener(
            OnSelectA4TVSpinnerListener onSelectA4TVSpinnerListener) {
        this.mOnSelectA4TVSpinnerListener = onSelectA4TVSpinnerListener;
    }

    public String[] getContents() {
        return contents;
    }
}
