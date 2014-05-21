package com.iwedia.gui.mainmenu;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.content.applications.AppItem;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVButtonSwitch;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.dialogs.AccountsAndSyncDialog;
import com.iwedia.gui.components.dialogs.ApplicationsManageDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationManualTunningDialog;
import com.iwedia.gui.components.dialogs.ChannelInstallationSignalInformationDialog;
import com.iwedia.gui.components.dialogs.EnergySaveDialog;
import com.iwedia.gui.components.dialogs.ExternalAndLocalStorageDialog;
import com.iwedia.gui.components.dialogs.FactoryResetDialog;
import com.iwedia.gui.components.dialogs.InputDevicesSettingsDialog;
import com.iwedia.gui.components.dialogs.LanguageAndKeyboardDialog;
import com.iwedia.gui.components.dialogs.NetworkAdvancedManualConfigDialog;
import com.iwedia.gui.components.dialogs.NetworkAdvancedSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessAddHiddenNetworkDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessSettingsDialog;
import com.iwedia.gui.components.dialogs.NetworkWirelessWPSConfigDialog;
import com.iwedia.gui.components.dialogs.OffTimersAddDialog;
import com.iwedia.gui.components.dialogs.OffTimersSettingsDialog;
import com.iwedia.gui.components.dialogs.PVRManualEventReminderDialog;
import com.iwedia.gui.components.dialogs.ParentalGuidanceDialog;
import com.iwedia.gui.components.dialogs.PasswordSecurityDialog;
import com.iwedia.gui.components.dialogs.PiPSettingsDialog;
import com.iwedia.gui.components.dialogs.ProductInfoDialog;
import com.iwedia.gui.components.dialogs.SoundSettingsDialog;
import com.iwedia.gui.components.dialogs.TimeAndDateSettingsDialog;
import com.iwedia.gui.components.dialogs.TimersSettingsDialog;
import com.iwedia.gui.components.dialogs.VoiceInputDialog;

import java.util.ArrayList;

/**
 * Class that creates small dialog in main menu
 * 
 * @author Branimir Pavlovic
 */
public class DialogCreatorClass {
    private MainActivity activity;
    public static final int LIST_VIEW_IN_DIALOG_ID = 1111,
            CUSTOM_TITLE_ID = 5443543, LAYOUT_FOR_INFLATING = 234234,
            LINES_BASE_ID = 9817650;
    /** Weights of layout and elements */
    public static final float SMALL_LAYOUT_WEIGHT_SUM = 2f,
            ELEMENTS_WEIGHT_BIG = 1.3f, ELEMENTS_WEIGHT_SMALL = 0.7f;
    private AppItem appItem;

    /**
     * Default constructor
     * 
     * @param activity
     */
    public DialogCreatorClass(MainActivity activity) {
        this.activity = activity;
    }

    /**
     * Function that fills dialog
     * 
     * @param contentList
     *        matrics of tags for views to add
     * @param contentListTextIDs
     *        matrics of text IDs for views to add
     * @return dialog view
     */
    public View fillDialogWithContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListTextIDs,
            ArrayList<Integer> titleTextIDs,
            OnSeekBarChangeListener listenerForProgressBars,
            OnClickListener listenerForButtons, BaseAdapter adapter) {// , int
        // pictureBackgroundID)
        // {
        FrameLayout frame = new FrameLayout(activity);
        frame.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        LinearLayout layoutForBackgroundPicture = new LinearLayout(activity);
        // set dimensions for background picture
        layoutForBackgroundPicture
                .setLayoutParams(new FrameLayout.LayoutParams(
                        2 * MainActivity.dialogHeight / 3,
                        2 * MainActivity.dialogHeight / 3, Gravity.CENTER));
        // Drawable myIcon = activity.getResources().getDrawable(
        // pictureBackgroundID);
        // myIcon.setAlpha(30);
        // set background drawable
        // layoutForBackgroundPicture.setBackgroundDrawable(myIcon);
        frame.addView(layoutForBackgroundPicture);
        // layout to add all views
        LinearLayout mainLinLayout = new LinearLayout(activity);
        mainLinLayout.setLayoutParams(new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mainLinLayout.setOrientation(LinearLayout.VERTICAL);
        // add linear layout to frame layout
        frame.addView(mainLinLayout);
        // layout of dialog title
        LinearLayout titleLinearLayout = new LinearLayout(activity);
        titleLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                (int) (MainActivity.dialogListElementHeight)));
        titleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        // titleLinearLayout.setBackgroundColor(Color.RED);/////////////////////////////////////////////////////
        // if (MainActivity.screenHeight == 1280
        // || MainActivity.screenWidth == 1280) {
        // titleLinearLayout.setPadding(
        // (int) (1.3 * activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_left)),
        // (int) (1.5 * activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_top)), 0, 0);
        // } else {
        // titleLinearLayout.setPadding(
        // 2 * (int) activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_left),
        // (int) (3.5 * activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_top)), 0, 0);
        // }
        Log.d("TITLE SIZE", "" + titleTextIDs.size());
        // set gravity
        titleLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        if (titleTextIDs.size() == 0) {
            // for custom title of dialog
            if (appItem != null) {
                A4TVTextView text = new A4TVTextView(activity, null);
                text.setId(CUSTOM_TITLE_ID);
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                text.setGravity(Gravity.CENTER);
                text.setText(appItem.getAppname());
                text.setTextSize(activity.getResources().getDimension(
                        R.dimen.a4tvdialog_textview_size));
                text.setPadding(5, 0, 5, 0);
                // add text view
                titleLinearLayout.addView(text);
            }
        }
        /** populate title layout */
        for (int i = 0; i < titleTextIDs.size(); i++) {
            if (i < titleTextIDs.size() - 1) { // add icon
                ImageView view = new ImageView(activity);
                // if (MainActivity.screenHeight == 1280
                // || MainActivity.screenWidth == 1280) {
                view.setLayoutParams(new LinearLayout.LayoutParams(
                        ((int) (MainActivity.dialogListElementHeight) - (int) (1.5 * activity
                                .getResources().getDimension(
                                        R.dimen.a4tvdialog_padding_top))),
                        LayoutParams.MATCH_PARENT));
                // } else {
                // view.setLayoutParams(new LinearLayout.LayoutParams(
                // ((int) (MainActivity.dialogListElementHeight) - (int) (1 *
                // activity
                // .getResources().getDimension(
                // R.dimen.a4tvdialog_padding_top))),
                // LayoutParams.MATCH_PARENT));
                // }
                view.setPadding(5, 0, 5, 0);
                view.setImageResource(titleTextIDs.get(i));
                view.setScaleType(ScaleType.FIT_CENTER);
                titleLinearLayout.addView(view);
            } else {// for last add text
                A4TVTextView text = new A4TVTextView(activity, null);
                text.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                text.setGravity(Gravity.CENTER);
                text.setText(activity.getResources().getString(
                        titleTextIDs.get(i)));
                // text.setTextSize(activity.getResources().getDimension(
                // R.dimen.a4tvdialog_textview_size));
                text.setPadding(5, 0, 5, 0);
                // add text view
                titleLinearLayout.addView(text);
            }
            // add arrow between text
            if (i != titleTextIDs.size() - 1) {
                ImageView image = new ImageView(activity);
                image.setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
                image.setImageResource(R.drawable.arrow1);
                titleLinearLayout.addView(image);
            }
        }
        // add title layout to main layout
        mainLinLayout.addView(titleLinearLayout);
        // create horizontal line
        ImageView horizLine = new ImageView(activity);
        horizLine.setLayoutParams(new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        // get drawable from theme for image source
        TypedArray atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        int backgroundID = atts.getResourceId(0, 0);
        horizLine.setBackgroundResource(backgroundID);
        atts.recycle();
        // add horiz line to main layout
        mainLinLayout.addView(horizLine);
        ScrollView mainScrollView = null;
        if (adapter == null) {
            // create scroll view
            mainScrollView = new ScrollView(activity);
            mainScrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            mainScrollView.setScrollbarFadingEnabled(false);
            // set pading for scrollview
            mainScrollView.setPadding(
                    (int) activity.getResources().getDimension(
                            R.dimen.a4tvdialog_padding_left),
                    (int) activity.getResources().getDimension(
                            R.dimen.a4tvdialog_padding_top),
                    (int) activity.getResources().getDimension(
                            R.dimen.a4tvdialog_padding_right),
                    (int) activity.getResources().getDimension(
                            R.dimen.a4tvdialog_padding_botom));
            // add scrollview to main view
            mainLinLayout.addView(mainScrollView);
        }
        LinearLayout contentLinearLayout = new LinearLayout(activity);
        contentLinearLayout.setLayoutParams(new ScrollView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        // set pading to content layout
        contentLinearLayout.setPadding(
                (int) activity.getResources().getDimension(
                        R.dimen.a4tvdialog_padding_left),
                (int) activity.getResources().getDimension(
                        R.dimen.a4tvdialog_padding_top),
                (int) activity.getResources().getDimension(
                        R.dimen.a4tvdialog_padding_right),
                (int) activity.getResources().getDimension(
                        R.dimen.a4tvdialog_padding_botom));
        if (adapter == null) {
            // add content layout to scroll view
            mainScrollView.addView(contentLinearLayout);
        } else {
            mainLinLayout.addView(contentLinearLayout);
        }
        // number of horizontal pairs
        for (int i = 0; i < contentList.size(); i++) {
            // create small layout
            final LinearLayout smallLayoutHorizontal = new LinearLayout(
                    activity);
            smallLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            smallLayoutHorizontal
                    .setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            MainActivity.dialogListElementHeight));
            // LayoutParams.WRAP_CONTENT));
            smallLayoutHorizontal.setWeightSum(SMALL_LAYOUT_WEIGHT_SUM);
            smallLayoutHorizontal.setPadding(15, 4, 15, 4);
            smallLayoutHorizontal.setGravity(Gravity.CENTER_VERTICAL);
            smallLayoutHorizontal.setId(contentListTextIDs.get(i).get(0));
            for (int j = 0; j < contentList.get(i).size(); j++) {
                // add our views to layout
                switch (contentList.get(i).get(j)) {
                    case MainMenuContent.TAGA4TVTextView: {
                        A4TVTextView textView = new A4TVTextView(activity, null);
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                0, LayoutParams.WRAP_CONTENT,
                                ELEMENTS_WEIGHT_BIG));
                        textView.setGravity(Gravity.CENTER_VERTICAL);
                        // auto scroll text in text view
                        textView.setEllipsize(TruncateAt.MARQUEE);
                        textView.setSingleLine(true);
//                        textView.setTextSize(activity.getResources()
//                                .getDimension(R.dimen.a4tvdialog_textview_size));
                        // set text to text view
                        textView.setText(activity.getResources().getString(
                                contentListTextIDs.get(i).get(j)));
                        textView.setId(contentListTextIDs.get(i).get(j));
                        // add text view to small layout
                        smallLayoutHorizontal.addView(textView);
                        break;
                    }
                    case MainMenuContent.TAGA4TVButtonSwitch: {
                        A4TVButtonSwitch button = new A4TVButtonSwitch(activity);
                        button.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.MATCH_PARENT,
                                ELEMENTS_WEIGHT_SMALL));
                        // set IDs for buttons
                        button.setId(contentListTextIDs.get(i).get(j));
                        // button.setTextSize(activity.getResources()
                        // .getDimension(
                        // R.dimen.a4tvdialog_button_text_size));
                        button.setEllipsize(TruncateAt.MARQUEE);
                        button.setSingleLine(true);
                        // add focus listener for button
                        button.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // get drawable from theme for small layout
                                // background
                                TypedArray atts = activity
                                        .getTheme()
                                        .obtainStyledAttributes(
                                                new int[] { R.attr.LayoutFocusDrawable });
                                int backgroundID = atts.getResourceId(0, 0);
                                if (hasFocus) {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(true);
                                    smallLayoutHorizontal
                                            .setBackgroundResource(backgroundID);
                                } else {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(false);
                                    smallLayoutHorizontal
                                            .setBackgroundColor(Color.TRANSPARENT);
                                }
                                atts.recycle();
                            }
                        });
                        // set click listener for button
                        button.setOnClickListener(listenerForButtons);
                        // set background to some buttons
                        int n = setBackgroundToButtons(contentListTextIDs
                                .get(i).get(j));
                        if (n != -1) {
                            button.setBackgroundColor(n);
                        }
                        // add button to small layout
                        smallLayoutHorizontal.addView(button);
                        break;
                    }
                    case MainMenuContent.TAGA4TVButton: {
                        A4TVButton button = new A4TVButton(activity);
                        button.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.MATCH_PARENT,
                                ELEMENTS_WEIGHT_SMALL));
                        // set IDs for buttons
                        button.setId(contentListTextIDs.get(i).get(j));
                        // set text to button
                        button.setText(setTextToButtons(
                                contentListTextIDs.get(i).get(j), button));
                        // button.setTextSize(activity.getResources()
                        // .getDimension(
                        // R.dimen.a4tvdialog_button_text_size));
                        button.setEllipsize(TruncateAt.MARQUEE);
                        button.setSingleLine(true);
                        // add focus listener for button
                        button.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // get drawable from theme for small layout
                                // background
                                TypedArray atts = activity
                                        .getTheme()
                                        .obtainStyledAttributes(
                                                new int[] { R.attr.LayoutFocusDrawable });
                                int backgroundID = atts.getResourceId(0, 0);
                                if (hasFocus) {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(true);
                                    smallLayoutHorizontal
                                            .setBackgroundResource(backgroundID);
                                } else {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(false);
                                    smallLayoutHorizontal
                                            .setBackgroundColor(Color.TRANSPARENT);
                                }
                                atts.recycle();
                            }
                        });
                        // set click listener for button
                        button.setOnClickListener(listenerForButtons);
                        // set background to some buttons
                        int n = setBackgroundToButtons(contentListTextIDs
                                .get(i).get(j));
                        if (n != -1) {
                            button.setBackgroundColor(n);
                        }
                        // add button to small layout
                        smallLayoutHorizontal.addView(button);
                        break;
                    }
                    case MainMenuContent.TAGA4TVCheckBox: {
                        break;
                    }
                    case MainMenuContent.TAGA4TVEditText: {
                        A4TVEditText edit = new A4TVEditText(activity, null);
                        edit.setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.MATCH_PARENT,
                                ELEMENTS_WEIGHT_SMALL));
                        // set id for edit text
                        edit.setId(contentListTextIDs.get(i).get(j));
                        // set input type
                        edit.setInputType(setInputTypeForEditText(edit));
                        // set focus listener for edit text
                        edit.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                TypedArray atts = activity
                                        .getTheme()
                                        .obtainStyledAttributes(
                                                new int[] { R.attr.LayoutFocusDrawable });
                                int backgroundID = atts.getResourceId(0, 0);
                                if (hasFocus) {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(true);
                                    smallLayoutHorizontal
                                            .setBackgroundResource(backgroundID);
                                } else {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(false);
                                    smallLayoutHorizontal
                                            .setBackgroundColor(Color.TRANSPARENT);
                                }
                                atts.recycle();
                            }
                        });
                        // add edit text to small layout
                        smallLayoutHorizontal.addView(edit);
                        break;
                    }
                    case MainMenuContent.TAGA4TVProgressBar: {
                        A4TVProgressBar progress = new A4TVProgressBar(
                                activity, true);
                        if (MainActivity.screenWidth == 1280
                                || MainActivity.screenHeight == 1280) {
                            progress.setLayoutParams(new LinearLayout.LayoutParams(
                                    0, MainActivity.screenHeight / 15,
                                    ELEMENTS_WEIGHT_SMALL));
                        } else {
                            progress.setLayoutParams(new LinearLayout.LayoutParams(
                                    0, MainActivity.screenHeight / 22,
                                    ELEMENTS_WEIGHT_SMALL));
                        }
                        // set IDs for progress
                        progress.setId(contentListTextIDs.get(i).get(j));
                        // add focus listener for button
                        progress.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // get drawable from theme for small layout
                                // background
                                TypedArray atts = activity
                                        .getTheme()
                                        .obtainStyledAttributes(
                                                new int[] { R.attr.LayoutFocusDrawable });
                                int backgroundID = atts.getResourceId(0, 0);
                                if (hasFocus) {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(true);
                                    smallLayoutHorizontal
                                            .setBackgroundResource(backgroundID);
                                } else {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(false);
                                    smallLayoutHorizontal
                                            .setBackgroundColor(Color.TRANSPARENT);
                                }
                                atts.recycle();
                            }
                        });
                        // set seekbar change listener for progress
                        progress.setOnSeekBarChangeListener(listenerForProgressBars);
                        // add button to small layout
                        smallLayoutHorizontal.addView(progress);
                        break;
                    }
                    case MainMenuContent.TAGA4TVRadioButton: {
                        break;
                    }
                    case MainMenuContent.TAGA4TVSpinner: {
                        A4TVSpinner spinner = new A4TVSpinner(activity, null);
                        spinner.setLayoutParams(new LinearLayout.LayoutParams(
                                0, LayoutParams.MATCH_PARENT,
                                ELEMENTS_WEIGHT_SMALL));
                        // set IDs for spinner
                        spinner.setId(contentListTextIDs.get(i).get(j));
                        // set initial text to spinner
                        spinner.setInitialText();
                        // spinner.setTextSize(activity.getResources()
                        // .getDimension(
                        // R.dimen.a4tvdialog_button_text_size));
                        spinner.setEllipsize(TruncateAt.MARQUEE);
                        spinner.setSingleLine(true);
                        // add focus listener for spinner
                        spinner.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                // get drawable from theme for small layout
                                // background
                                TypedArray atts = activity
                                        .getTheme()
                                        .obtainStyledAttributes(
                                                new int[] { R.attr.LayoutFocusDrawable });
                                int backgroundID = atts.getResourceId(0, 0);
                                if (hasFocus) {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(true);
                                    smallLayoutHorizontal
                                            .setBackgroundResource(backgroundID);
                                } else {
                                    smallLayoutHorizontal.getChildAt(0)
                                            .setSelected(false);
                                    smallLayoutHorizontal
                                            .setBackgroundColor(Color.TRANSPARENT);
                                }
                                atts.recycle();
                            }
                        });
                        // add button to small layout
                        smallLayoutHorizontal.addView(spinner);
                        break;
                    }
                    default:
                        break;
                }
            }
            // add horizontal layout to content layout
            contentLinearLayout.addView(smallLayoutHorizontal);
            if (i < contentList.size() - 1) {
                // create horizontal line
                ImageView horizLin = new ImageView(activity);
                horizLin.setLayoutParams(new LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
                // get drawable from theme for image source
                TypedArray att = activity.getTheme().obtainStyledAttributes(
                        new int[] { R.attr.DialogSmallDividerLine });
                int src = att.getResourceId(0, 0);
                horizLin.setBackgroundResource(src);
                att.recycle();
                // set id of line
                horizLin.setId(LINES_BASE_ID + i);
                // add horiz line to main layout
                contentLinearLayout.addView(horizLin);
            }
        }
        // ///////////////////////////////////////////////
        // create layout for inflating
        LinearLayout layoutForInflating = new LinearLayout(activity);
        layoutForInflating.setOrientation(LinearLayout.VERTICAL);
        layoutForInflating.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // layoutForInflating.setPadding(15, 4, 15, 4);
        layoutForInflating.setGravity(Gravity.TOP);
        layoutForInflating.setId(LAYOUT_FOR_INFLATING);
        contentLinearLayout.addView(layoutForInflating);
        /** If there is adapter create List View */
        if (adapter != null) {
            ListView listView = new ListView(activity);
            listView.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            listView.setAdapter(adapter);
            listView.setId(LIST_VIEW_IN_DIALOG_ID);
            listView.setScrollbarFadingEnabled(false);
            // listView.setPadding(0, 0, 20, 0);
            listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
            // set divider and selector for listview
            TypedArray att = activity.getTheme().obtainStyledAttributes(
                    new int[] { R.attr.DialogSmallDividerLine });
            int src = att.getResourceId(0, 0);
            Bitmap preparedBitmap1 = BitmapFactory.decodeResource(
                    activity.getResources(), src);
            att.recycle();
            listView.setDivider(new BitmapDrawable(preparedBitmap1));
            Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(),
                    R.drawable.transparent_image);
            listView.setSelector(new BitmapDrawable(activity.getResources(),
                    bmp));
            // add list view to dialog
            contentLinearLayout.addView(listView);
        }
        // //////////////////////////////////////////////////////
        // try
        // //////////////////////////////////////////////////////
        // LinearLayout linLayReturn = new LinearLayout(activity);
        // linLayReturn.setOrientation(LinearLayout.HORIZONTAL);
        // linLayReturn.setLayoutParams(new LinearLayout.LayoutParams(
        // LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // linLayReturn.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        //
        // ImageView image = new ImageView(activity);
        // image.setImageResource(R.drawable.back_icon);
        // A4TVTextView tvReturn = new A4TVTextView(activity);
        // tvReturn.setText(R.string.epg_return);
        //
        // linLayReturn.addView(image);
        // linLayReturn.addView(tvReturn);
        //
        // mainLinLayout.addView(linLayReturn);
        return frame;
        // return mainLinLayout;
    }

    /** Edit text input type */
    private int setInputTypeForEditText(A4TVEditText edit) {
        switch (edit.getId()) {
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_FREQUENCY: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(6);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_CLASS_NUMBER;
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_SYMBOL_RATE_EDIT_TEXT: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(5);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_CLASS_NUMBER;
            }
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_CHANNEL_NUMBER_DVBT: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(6);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            }
            case NetworkAdvancedManualConfigDialog.TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_DNS: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(30);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_CLASS_TEXT;
            }
            case NetworkAdvancedManualConfigDialog.TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_GATEWAY_IP: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(30);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_CLASS_TEXT;
            }
            case NetworkAdvancedManualConfigDialog.TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_IP: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(30);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_CLASS_TEXT;
            }
            case NetworkAdvancedManualConfigDialog.TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_NETWORK_PREFIX_LENGTH: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(30);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_CLASS_TEXT;
            }
            case NetworkWirelessAddHiddenNetworkDialog.TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_PASSWORD: {
                // Create a new InputFilter to define the maximum length
                InputFilter maxLengthFilter = new InputFilter.LengthFilter(30);
                // Apply the filter to the EditText. The array can contain other
                // filters.
                edit.setFilters(new InputFilter[] { maxLengthFilter });
                return InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
            default:
                break;
        }
        return InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
    }

    /** Hide background to some buttons */
    private int setBackgroundToButtons(int buttonID) {
        switch (buttonID) {
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_info_bit_error_level:
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_info_service_id:
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_info_channel_id:
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_info_network:
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_info_network_id:
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_info_multiplex:
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_information_service_name:
            case ChannelInstallationSignalInformationDialog.tv_menu_channel_installation_signal_info_centre_frequency: {
                return Color.TRANSPARENT;
            }
            default:
                break;
        }
        return -1;
    }

    /** Function that resolves what text go to button */
    private String setTextToButtons(int buttonID, Button button) {
        switch (buttonID) {
            case AccountsAndSyncDialog.TV_MENU_ACCOUNT_SETTINGS_MANAGE_ACCOUNTS:
            case ApplicationsManageDialog.TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPLICATIONS:
                // case
                // ExternalAndLocalStorageDialog.tv_menu_storage_settings_external_file_storage:
                // case
                // ExternalAndLocalStorageDialog.tv_menu_storage_settings_local_file_storage:
            case InputDevicesSettingsDialog.tv_menu_factory_input_settings_hdmi_cec:
            case LanguageAndKeyboardDialog.TV_MENU_LANGUAGE_SETTINGS_KEYBOARD_SETTINGS:
            // case
            // ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_PROGRAMME_EDIT:
            // case
            // ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_SATELITE_EDIT:
            {
                return activity.getResources().getString(
                        R.string.button_text_edit);// return
                // EDIT
            }
            case NetworkSettingsDialog.TV_MENU_NETWORK_SETTINGS_WIRELESS_SETTINGS:
            case NetworkSettingsDialog.TV_MENU_NETWORK_SETTINGS_ADVANCED_SETTINGS: {
                return activity.getResources().getString(
                        R.string.button_text_configure);
            }
            case AccountsAndSyncDialog.TV_MENU_ACCOUNT_SETTINGS_AUTO_SYNC:
            case VoiceInputDialog.TV_MENU_VOICE_SETTINGS_ALWAYS_USE_MY_SETTINGS:
                // case HBBSettingsDialog.tv_menu_hbb_settings_hbb_tv_enable: {
                // return
                // activity.getResources().getString(R.string.button_text_yes);//
                // return
                // // YES
                // }
                // case
                // ChannelInstallationManualTunningDialog.tv_menu_channel_installation_manual_tunning_keep_current_list:
                // {
                // return
                // activity.getResources().getString(R.string.button_text_no);//
                // return
                // // NO
                // }
            case AccountsAndSyncDialog.TV_MENU_ACCOUNT_SETTINGS_ADD_ACCOUNT:
            case NetworkWirelessAddHiddenNetworkDialog.TV_MENU_NETWORK_WIRELESS_ADD_HIDDEN_NETWORK_ADD:
            case NetworkWirelessSettingsDialog.TV_MENU_NETWORK_WIRELESS_SETTINGS_MANUAL_ADD_AP: {
                return activity.getResources().getString(
                        R.string.button_text_add);// return
                // ADD
            }
            case EnergySaveDialog.TV_MENU_ENERGY_SETTINGS_ENERGY_SAVE_ENABLE:
            case ParentalGuidanceDialog.TV_MENU_PARENTIAL_SECURITY_SETTINGS_PARENTIAL_GUIDANCE: {
                return activity.getResources().getString(
                        R.string.button_text_off);// return
                // OFF
            }
            case NetworkWirelessSettingsDialog.TV_MENU_NETWORK_WIRELESS_SETTINGS_FIND_AP: {
                return activity.getResources().getString(
                        R.string.button_text_start);
            }
            case SoundSettingsDialog.TV_MENU_SOUND_SETTINGS_AUTO_VOLUME:
            case ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_AUTO_CHANNEL_NUMBER: {
                return activity.getResources().getString(
                        R.string.button_text_on);// return
                // ON
            }
            case NetworkAdvancedSettingsDialog.TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG:
            case NetworkAdvancedSettingsDialog.TV_MENU_NETWORK_ADVANCED_PROXY_SETTINGS:
            case NetworkAdvancedSettingsDialog.TV_MENU_NETWORK_ADVANCED_SOFT_AP_SETTINGS:
            case NetworkWirelessSettingsDialog.TV_MENU_NETWORK_WIRELESS_SETTINGS_WPS_CONFIG:
            case TimeAndDateSettingsDialog.TV_MENU_TIME_AND_DATE_SETTINGS_TIMER: {
                return activity.getResources().getString(
                        R.string.button_text_configure);// return CONFIGURE
            }
            case OffTimersAddDialog.TV_MENU_OFFTIMERS_SETTINGS_START: {
                return activity.getResources().getString(
                        R.string.button_text_start);
            }
            case NetworkWirelessWPSConfigDialog.TV_MENU_NETWORK_WIRELESS_WPS_CONFIG_REGISTRAR:
            case NetworkWirelessWPSConfigDialog.TV_MENU_NETWORK_WIRELESS_WPS_CONFIG_ENROLLEE:
            case NetworkWirelessWPSConfigDialog.TV_MENU_NETWORK_WIRELESS_WPS_CONFIG_PBC:
            case PVRManualEventReminderDialog.TV_MENU_PVR_MANUAL_REMINDER_START: {
                return activity.getResources().getString(
                        R.string.button_text_start);
            }
            case ProductInfoDialog.TV_MENU_PRODUCT_INFO_SETTINGS_FIRMWARE_VERSION:
            case ProductInfoDialog.TV_MENU_PRODUCT_INFO_SETTINGS_SOFTWARE_VERSION:
            case ProductInfoDialog.TV_MENU_PRODUCT_INFO_SETTINGS_MODEL_NUMBER:
            case ProductInfoDialog.TV_MENU_PRODUCT_INFO_SETTINGS_IP_ADDRESS:
            case ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_SIGNAL_INFO:
            case ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_MANUAL_TUNNING: {
                return activity.getResources().getString(
                        R.string.button_text_tune);// return
                // TUNE
            }
            case ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_AUTO_TUNNING:
            case NetworkSettingsDialog.TV_MENU_NETWORK_SETTINGS_NETWORK_TEST:
            case NetworkSettingsDialog.TV_MENU_NETWORK_SETTINGS_NETWORK_INFORMATION:
            case NetworkAdvancedManualConfigDialog.TV_MENU_NETWORK_ADVANCED_MANUAL_CONFIG_START:
            case ChannelInstallationManualTunningDialog.TV_MENU_CHANNEL_INSTALLATION_MANUAL_TUNNING_SETTINGS_START_SEARCH: {
                return activity.getResources().getString(
                        R.string.button_text_start);// return START
            }
            case TimeAndDateSettingsDialog.TV_MENU_TIME_AND_DATE_SETTINGS_SET_DATE:
            case TimeAndDateSettingsDialog.TV_MENU_TIME_AND_DATE_SETTINGS_SET_TIME: {
                return activity.getResources().getString(
                        R.string.button_text_set);// return
                // SET
            }
            case TimersSettingsDialog.TV_MENU_TIMERS_SETTINGS_OFF_TIMERS: {
                return activity.getResources().getString(
                        R.string.button_text_configure);// return CHANGE
            }
            case TimersSettingsDialog.TV_MENU_TIMERS_SETTINGS_ON_TIMERS: {
                return activity.getResources().getString(
                        R.string.button_text_configure);// return CHANGE
            }
            case OffTimersSettingsDialog.TV_MENU_OFFTIMERS_SETTINGS_ADD_TIMERS: {
                return activity.getResources().getString(
                        R.string.button_text_configure);// return CHANGE
            }
            case OffTimersSettingsDialog.TV_MENU_OFFTIMERS_SETTINGS_DELETE_TIMERS: {
                return activity.getResources().getString(
                        R.string.button_text_confirm);// return CONFIRM
            }
            case PasswordSecurityDialog.TV_MENU_PASSWORD_SECURITY_SETTINGS_CHANGE_PASSWORD: {
                return activity.getResources().getString(
                        R.string.button_text_change);// return CHANGE
            }
            case PasswordSecurityDialog.TV_MENU_PASSWORD_SECURITY_SETTINGS_RESET_PASSWORD:
            case ExternalAndLocalStorageDialog.TV_MENU_STORAGE_SETTINGS_FACTORY_DATA_RESET:
            case FactoryResetDialog.TV_MENU_FACTORY_RESET_SETTINGS_RESSET: {
                return activity.getResources().getString(
                        R.string.button_text_reset);// return RESET
            }
            case VoiceInputDialog.TV_MENU_VOICE_SETTINGS_LISTEN_TO_AN_EXAMPLE: {
                return activity.getResources().getString(
                        R.string.button_text_listen);// return LISTEN
            }
            case PiPSettingsDialog.PIP_SETTINGS_CUSTOM_POSITION_APPLY: {
                return activity.getResources().getString(
                        R.string.pip_coordinate_apply);
            }
            case PiPSettingsDialog.PIP_SETTINGS_CUSTOM_SIZE_APPLY: {
                return activity.getResources().getString(
                        R.string.pip_size_apply);
            }
            default:
                break;
        }
        return "";
    }

    public void setAppItem(AppItem appItem) {
        this.appItem = appItem;
    }
}
