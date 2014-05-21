package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.IScanCallback;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.comm.enums.ServiceListIndex;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.content_list.ContentListHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.ArrayList;

/**
 * Dialog for scanning procedure
 * 
 * @author Branimir Pavlovic
 */
public class ChannelScanDialog extends A4TVDialog implements
        A4TVDialogInterface, OnSeekBarChangeListener, OSDGlobal {
    private final static String TAG = "ChannelScanDialog";
    private Context ctx;
    /** Fields for scanning procedure */
    private A4TVProgressBar progressScan = null, progressSignalStrength = null,
            progressSignalQuality = null;
    // private static LinearLayout layoutToAddServices;
    // private static HorizontalScrollView scrollView;
    private int numberOfServices = 0, numberOfRadio = 0, numberOfData = 0;
    private A4TVTextView servicesText, dataText, radioText;
    private static boolean isScanning = false;
    private A4TVTextView textTopBanner;
    private LayoutInflater inflater;
    private Handler handlerScanFinished;
    private A4TVProgressDialog progressDialog;
    private final int WIDTH_720p = 1280, HEIGHT_720p = 720, WIDTH_1080p = 1920,
            HEIGHT_1080p = 1080, TIME_TO_SHOW_FTI_END_TEXT = 3000;
    public static final String NO_IMAGE = "-1";
    private int selectedTunerType;
    private GridView gridViewForChannels;
    private ChannelsGridAdapter gridAdapter;
    private Content[] contentsForGridView = new Content[12];
    private ArrayList<Content> contentsList = new ArrayList<Content>();
    public static final int[] INDEX_LOOK_UP_TABLE = { 0, 6, 1, 7, 2, 8, 3, 9,
            4, 10, 5, 11 };
    // public static final int[] INDEX_LOOK_UP_TABLE = { 11, 5, 10, 4, 9, 3, 8,
    // 2,
    // 7, 1, 6, 0 };
    private Animation animTranslate, animTranslateExit;
    private A4TVTextView textViewScannedFrequncy;
    public final static int SCAN_FINISHED_FLAG = 0, FOUND_SERVICE_FLAG = 1,
            REFRESH_ADAPTER_FLAG = 2, SCANNED_FREQUENCY = 3,
            NO_SERVICES_FOUND = 4, ERROR_OCCURED = 5, NO_SERVICE_SPACE = 6;
    /** Filter options fields */
    /** Array list of filter buttons */
    private ArrayList<LinearLayout> filterButtons = new ArrayList<LinearLayout>();
    /** Filter options ids */
    public static final int FILTER_ALL_OPTION = 0;
    public static final int FILTER_DVB_T_OPTION = 1;
    public static final int FILTER_DVB_C_OPTION = 2;
    public static final int FILTER_DVB_S_OPTION = 3;
    public static final int FILTER_IP_OPTION = 4;
    public static final int FILTER_ATV_OPTION = 5;
    /** Available filter options */
    private Boolean[] filterOptions = new Boolean[6];
    /** Width measure unit */
    private int widthMeasureUnit;
    /** Width measure unit divider */
    public static final int widthMeasureUnitDivider = 12;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        // Abort scan
            case KeyEvent.KEYCODE_DEL:
            case KeyEvent.KEYCODE_BACK: {
                if (getTextTopBanner()
                        .getText()
                        .toString()
                        .equals(ctx
                                .getResources()
                                .getString(
                                        R.string.tv_menu_channel_installation_settings_manual_tunning))) {
                    new A4TVToast(ctx)
                            .showToast(R.string.tv_menu_channel_installation_settings_manual_tunning_abort_not_supported);
                } else {
                    new AbortScanTask().execute();
                }
                return true;
            }
            case KeyEvent.KEYCODE_INFO: {
                if (isScanning) {
                    return super.onKeyDown(keyCode, event);
                } else {
                    return true;
                }
            }
            default: {
                return true;
            }
        }
    }

    /** Constructor */
    public ChannelScanDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // disable menu button
        setMenuButtonEnabled(false);
        // check what tunner is selected
        ChannelInstallationDialog chDialog = MainActivity.activity
                .getDialogManager().getChannelInstallationDialog();
        if (chDialog != null)
            selectedTunerType = ((A4TVSpinner) chDialog
                    .findViewById(ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE))
                    .getCHOOSEN_ITEM_INDEX();
        // set content to dialog
        fillDialog();
        setDialogAttributes();
        gridViewForChannels = (GridView) findViewById(R.id.allItemsGrid);
        // Set number of columns
        gridViewForChannels.setNumColumns(ContentListHandler.NUMBER_OF_COLUMNS);
        gridViewForChannels.setVerticalScrollBarEnabled(false);
        // Set up stretch mode for grid
        gridViewForChannels.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        // Attach adapter
        gridAdapter = new ChannelsGridAdapter();
        gridViewForChannels.setAdapter(gridAdapter);
        // disable grid view
        gridViewForChannels.setFocusable(false);
        // get reference to text views
        servicesText = (A4TVTextView) findViewById(R.id.aTVTextViewNumberOfServices);
        radioText = (A4TVTextView) findViewById(R.id.aTVTextViewNumberOfRadioServices);
        dataText = (A4TVTextView) findViewById(R.id.aTVTextViewNumberOfDataServices);
        // get reference to progress
        progressScan = (A4TVProgressBar) findViewById(R.id.aTVProgressBarScanProgress);
        progressScan.setOnSeekBarChangeListener(this);
        // disable progress changing manually
        progressScan.setEnabled(false);
        progressSignalQuality = (A4TVProgressBar) findViewById(R.id.aTVProgressBarSignalQualityIndicator);
        progressSignalQuality.setOnSeekBarChangeListener(this);
        // disable progress changing manually
        progressSignalQuality.setEnabled(false);
        progressSignalStrength = (A4TVProgressBar) findViewById(R.id.aTVProgressBarSignalStrengthIndicator);
        progressSignalStrength.setOnSeekBarChangeListener(this);
        // disable progress changing manually
        progressSignalStrength.setEnabled(false);
        textViewScannedFrequncy = (A4TVTextView) findViewById(R.id.aTVTextViewScannedFrequency);
        textTopBanner = (A4TVTextView) findViewById(R.id.contentListTopBannerText);
        // set text sizes
        // textTopBanner.setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // textViewScannedFrequncy.setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // servicesText.setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // radioText.setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // dataText.setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewMoreInfo)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.contentListRecentlyWatchedText))
        // .setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources()
        // .getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewFr)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewStr)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewQu)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewSc)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewNu)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewRa)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        // ((A4TVTextView) findViewById(R.id.aTVTextViewDa)).setTextSize(
        // TypedValue.COMPLEX_UNIT_DIP,
        // ctx.getResources().getDimension(
        // com.iwedia.gui.R.dimen.content_list_banner_text_size));
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        handlerScanFinished = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SCAN_FINISHED_FLAG) {
                    // hide and show dialogs
                    // Close scan dialog
                    isScanning = false;
                    Log.d(TAG, "handler scan finished entered");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            endScan(false);
                        }
                    }, 2500);
                }
                if (msg.what == FOUND_SERVICE_FLAG) {
                    // refresh number of services fields
                    servicesText.setText(numberOfServices + "");
                    // refresh number of radios
                    radioText.setText(numberOfRadio + "");
                    // Refresh number of data
                    dataText.setText(numberOfData + "");
                }
                if (msg.what == REFRESH_ADAPTER_FLAG) {
                    refreshAdapterData();
                }
                if (msg.what == SCANNED_FREQUENCY) {
                    if (getTextViewScannedFrequncy() != null) {
                        getTextViewScannedFrequncy().setText(
                                (String) msg.obj + " MHz");
                    }
                }
                if (msg.what == NO_SERVICES_FOUND) {
                    new A4TVToast(ctx)
                            .showToast(R.string.no_services_found_during_scan);
                }
                if (msg.what == NO_SERVICE_SPACE) {
                    new A4TVToast(ctx).showToast(R.string.no_service_space);
                }
                if (msg.what == ERROR_OCCURED) {
                    ChannelScanDialog.this.cancel();
                    resetViews();
                    // ////////////////////////////////////////////////
                    // Gallery unscaled and unrotated images bug fix
                    // ////////////////////////////////////////////////
                    Handler delay = new Handler();
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.activity.getMainMenuHandler()
                                    .getMainMenuDialog().cancel();
                            MainActivity.activity.getMainMenuHandler()
                                    .getMainMenuDialog().show();
                            MainActivity.activity.getMainMenuHandler()
                                    .getA4TVOnSelectLister()
                                    .startAnimationsManual();
                            MainKeyListener
                                    .setAppState(MainKeyListener.MAIN_MENU);
                            ChannelInstallationDialog chDialog = MainActivity.activity
                                    .getDialogManager()
                                    .getChannelInstallationDialog();
                            if (chDialog != null) {
                                chDialog.show();
                            }
                        }
                    }, 50);
                    new A4TVToast(ctx).showToast(R.string.error_during_scan);
                    isScanning = false;
                }
                super.handleMessage(msg);
            }
        };
        // set params of upper buttons
        initFilterOptionArray();
        // Calculate width unit for filter buttons
        widthMeasureUnit = MainActivity.screenWidth / widthMeasureUnitDivider;
        // init filters
        loadFilterOptions();
        // if (MainActivity.activity.isFullHD()) {
        // A4TVTextView topBanner = (A4TVTextView)
        // findViewById(R.id.contentListTopBannerText);
        // topBanner.setTextSize(context.getResources().getDimension(
        // com.iwedia.gui.R.dimen.a4tvdialog_button_text_size_1080p));
        // }
        progressDialog = new A4TVProgressDialog(ctx);
        progressDialog.setTitleOfAlertDialog(R.string.aborting_scan);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(ctx.getResources().getString(
                R.string.please_wait));
        setUpDividers();
        animTranslate = AnimationUtils.loadAnimation(ctx,
                R.anim.translate_left_scan_animation);
        animTranslateExit = AnimationUtils.loadAnimation(ctx,
                R.anim.translate_left_exit_scan_animation);
        resetViews();
    }

    private void resetViews() {
        for (int i = 0; i < contentsForGridView.length; i++) {
            contentsForGridView[i] = null;
        }
        contentsList.clear();
        if (servicesText != null) {
            servicesText.setText("0");
        }
        if (radioText != null) {
            radioText.setText("0");
        }
        if (dataText != null) {
            dataText.setText("0");
        }
        numberOfServices = 0;
        numberOfRadio = 0;
        numberOfData = 0;
        textViewScannedFrequncy.setText("");
        progressScan.setProgress(0);
        progressSignalQuality.setProgress(0);
        progressSignalStrength.setProgress(0);
    }

    @Override
    public void onBackPressed() {
        if (getTextTopBanner()
                .getText()
                .toString()
                .equals(ctx
                        .getResources()
                        .getString(
                                R.string.tv_menu_channel_installation_settings_manual_tunning))) {
            new A4TVToast(ctx)
                    .showToast(R.string.tv_menu_channel_installation_settings_manual_tunning_abort_not_supported);
        } else {
            new AbortScanTask().execute();
        }
    }

    @Override
    public void fillDialog() {
        setContentView(R.layout.channel_scan_dialog_main_overscale);
    }

    @Override
    public void setDialogAttributes() {
        if (MainActivity.screenWidth == WIDTH_720p
                || MainActivity.screenHeight == WIDTH_720p) {
            getWindow().getAttributes().height = HEIGHT_720p;
            getWindow().getAttributes().width = WIDTH_720p;
        }
        if (MainActivity.screenWidth == WIDTH_1080p
                || MainActivity.screenHeight == WIDTH_1080p) {
            getWindow().getAttributes().height = HEIGHT_1080p;
            getWindow().getAttributes().width = WIDTH_1080p;
        }
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialogTransparent });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
        // return android.R.style.Theme_Translucent_NoTitleBar_Fullscreen;
    }

    /**
     * Function for inflating views
     */
    private LinearLayout loadItemsIntoMatrics(boolean fakeItem, Content content) {
        // Log.d(TAG, "load items into matrics function entered");
        LinearLayout item;
        // Inflate grid element xml
        item = (LinearLayout) inflater.inflate(
                com.iwedia.gui.R.layout.content_list_element_grid, null);
        GridView.LayoutParams params1 = new GridView.LayoutParams(
                LayoutParams.MATCH_PARENT, 4 * MainActivity.screenHeight / 21);
        // Take reference of frame layout holder
        item.setLayoutParams(params1);
        if (!fakeItem) {
            // ////////////////////////// ITEM 1 ///////////////////////////
            A4TVTextView itemText1 = (A4TVTextView) item
                    .findViewById(com.iwedia.gui.R.id.contentName);//
            // Take reference of image view in content list item
            ImageView itemImage1 = (ImageView) item
                    .findViewById(com.iwedia.gui.R.id.contentImage);//
            ImageView contentTypeImage = (ImageView) item
                    .findViewById(com.iwedia.gui.R.id.contentFilterTypeImage);//
            A4TVTextView contentNameText1 = (A4TVTextView) item
                    .findViewById(com.iwedia.gui.R.id.contentNameText);//
            // /////////////////////////////////////////////////////////////////////
            // Set image and text on content list item
            String imagePath = content.getImage();
            if (!imagePath.equals(NO_IMAGE)) {
                itemImage1.setImageBitmap(MainActivity.mMemoryCache
                        .loadBitmapFromDisk(imagePath));
                itemImage1.setScaleType(ScaleType.FIT_XY);
                FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                        MainActivity.screenWidth / 12,
                        MainActivity.screenHeight / 12);
                imageParams.gravity = Gravity.CENTER;
                itemImage1.setLayoutParams(imageParams);
                contentNameText1.setText("");
            } else {
                // ////////////////////////////////
                // There is no image
                // ////////////////////////////////
                contentNameText1.setText(content.getName());
                itemImage1.setImageBitmap(null);
            }
            // set content name
            itemText1.setText(content.getName());
            // if (MainActivity.activity.isFullHD()) {
            // itemText1.setTextSize(ctx.getResources().getDimension(
            // R.dimen.content_item_text_size_1080p));
            // } else {
            // itemText1.setTextSize(ctx.getResources().getDimension(
            // R.dimen.content_item_text_size));
            // }
            ChannelInstallationDialog chDialog = MainActivity.activity
                    .getDialogManager().getChannelInstallationDialog();
            if (chDialog != null)
                selectedTunerType = ((A4TVSpinner) chDialog
                        .findViewById(ChannelInstallationDialog.TV_MENU_CHANNEL_INSTALLATION_SETTINGS_TUNER_TYPE))
                        .getCHOOSEN_ITEM_INDEX();
            // set content type picture
            switch (content.getServiceType()) {
                case DATA_BROADCAST: {
                    // numberOfData++;
                    contentTypeImage
                            .setImageResource(com.iwedia.gui.R.drawable.data_filter_selector);
                    break;
                }
                case DIG_TV: {
                    // numberOfServices++;
                    if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.t_filter_selector);
                    } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.s_filter_selector);
                    } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.c_filter_selector);
                    } else if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.a_filter_selector);
                    }
                    break;
                }
                case DIG_RAD: {
                    // numberOfRadio++;
                    contentTypeImage
                            .setImageResource(com.iwedia.gui.R.drawable.radio_filter_selector);
                    break;
                }
                default:
                    // numberOfServices++;
                    if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBT) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.t_filter_selector);
                    } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBS) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.s_filter_selector);
                    } else if (selectedTunerType == ChannelInstallationDialog.TUNER_DVBC) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.c_filter_selector);
                    } else if (selectedTunerType == ChannelInstallationDialog.TUNER_ATV) {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.a_filter_selector);
                    }
                    break;
            }
        }
        // add fake item
        else {
            // Set image and text on content list item
            item.setVisibility(View.INVISIBLE);
            item.setFocusable(false);
            item.setEnabled(false);
            item.setClickable(false);
        }
        return item;
    }

    private void refreshAdapterData() {
        Log.d(TAG, "REFRESH ADAPTER DATA IN SCAN");
        int countTo;
        if (contentsList.size() >= 12) {
            countTo = 12;
        } else {
            countTo = contentsList.size();
        }
        for (int i = 0; i < countTo; i++) {
            contentsForGridView[INDEX_LOOK_UP_TABLE[i]] = contentsList.get(i);
        }
        gridAdapter.notifyDataSetChanged();
        // handlerScanFinished.sendEmptyMessage(1);
        for (int i = 0; i < countTo; i++) {
            contentsList.remove(0);
        }
    }

    private class ChannelsGridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contentsForGridView.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (contentsForGridView[position] == null) {
                convertView = loadItemsIntoMatrics(true,
                        contentsForGridView[position]);
            } else {
                View view = convertView;
                convertView = loadItemsIntoMatrics(false,
                        contentsForGridView[position]);
                if (view != null) {
                    view.startAnimation(animTranslateExit);
                }
                if (convertView != null) {
                    convertView.startAnimation(animTranslate);
                }
            }
            return convertView;
        }
    }

    /** Async Task for get_category_properties() from web service */
    private class AbortScanTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (isScanning) {
                try {
                    MainActivity.service.getScanControl().abortScan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Boolean result) {
            // post delayed for protection if wrong tuner is selected
            // new Handler().postDelayed(new Runnable() {
            //
            // @Override
            // public void run() {
            // endScan(false);
            // }
            // }, 60000);
        }
    };

    /** Method for clearing dialogs for scanning and showing info message */
    public void endScan(boolean isForIP) {
        Log.d(TAG, "END SCAN FUNCTION ENTERED");
        resetViews();
        if (MainActivity.isInFirstTimeInstall) {
            if (ChannelScanDialog.this.isShowing() || isForIP) {
                progressDialog.dismiss();
                ChannelScanDialog.this.cancel();
                ChannelInstallationDialog chDialog = MainActivity.activity
                        .getDialogManager().getChannelInstallationDialog();
                if (chDialog != null) {
                    chDialog.cancel();
                }
                // //////////////////////////////////////
                // Services is found
                // //////////////////////////////////////
                if (getNumberOfServicesAfterScan() > 0) {
                    if (MainActivity.activity.getFirstTimeInfoText() != null) {
                        MainActivity.activity.getFirstTimeInfoText().setText(
                                R.string.first_time_install_end_fti);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.activity
                                    .getFirstTimeInstallLayout() != null) {
                                MainActivity.activity
                                        .getFirstTimeInstallLayout()
                                        .setVisibility(View.GONE);
                                MainKeyListener
                                        .setAppState(MainKeyListener.CLEAN_SCREEN);
                                MainActivity
                                        .getSharedPrefs()
                                        .edit()
                                        .putBoolean(
                                                MainActivity.FIRST_TIME_INSTALL,
                                                false).commit();
                                MainActivity.isInFirstTimeInstall = false;
                            }
                        }
                    }, TIME_TO_SHOW_FTI_END_TEXT);
                    // If it is info active, close it
                    if (MainActivity.activity.getPageCurl().getCurrentState() == STATE_INFO) {
                        MainActivity.activity.getPageCurl().info();
                    }
                }
                // ///////////////////////////////////////////
                // No services found
                // ///////////////////////////////////////////
                else {
                    showAlertDialogWhenThereIsNoServicesFoundAfterScan(MainActivity.isInFirstTimeInstall);
                }
                isScanning = false;
            }
        } else
        // if scan dialog is showing cancel it and open main menu
        if (ChannelScanDialog.this.isShowing() || isForIP) {
            progressDialog.dismiss();
            ChannelScanDialog.this.cancel();
            if (getNumberOfServicesAfterScan() > 0) {
                if (!isForIP) {
                    // ////////////////////////////////////////////////
                    // Gallery unscaled and unrotated images bug fix
                    // ////////////////////////////////////////////////
                    Handler delay = new Handler();
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.activity.getMainMenuHandler()
                                    .getMainMenuDialog().cancel();
                            MainActivity.activity.getMainMenuHandler()
                                    .getMainMenuDialog().show();
                            MainActivity.activity.getMainMenuHandler()
                                    .getA4TVOnSelectLister()
                                    .startAnimationsManual();
                            MainKeyListener
                                    .setAppState(MainKeyListener.MAIN_MENU);
                        }
                    }, 150);
                    Log.d(TAG, "BEFORE CHECK OF INFO");
                    // If it is info active, close it
                    if (MainActivity.activity.getPageCurl().getCurrentState() == STATE_INFO) {
                        MainActivity.activity.getPageCurl().info();
                    }
                } else {
                    new A4TVToast(ctx).showToast(R.string.scan_finished);
                }
            } else {
                showAlertDialogWhenThereIsNoServicesFoundAfterScan(false);
            }
            isScanning = false;
        }
    }

    /**
     * Calculates number of services scanned
     * 
     * @return number of services
     */
    private int getNumberOfServicesAfterScan() {
        IContentListControl conControl = null;
        try {
            conControl = MainActivity.service.getContentListControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (conControl != null) {
            int size = 0;
            try {
                size = MainActivity.service.getServiceControl()
                        .getServiceListCount(ServiceListIndex.MASTER_LIST);
                if (ConfigHandler.IP) {
                    size += conControl
                            .getContentFilterListSize(FilterType.IP_STREAM);
                    size += conControl.getContentListSize();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        }
        return 0;
    }

    /**
     * Shows alert dialog when there is no services found after scan
     */
    private void showAlertDialogWhenThereIsNoServicesFoundAfterScan(
            final boolean isInFirstTimeInstall) {
        final A4TVAlertDialog alert = new A4TVAlertDialog(ctx);
        alert.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                switch (keyCode) {
                // ///////////////////////////////////////////////////////////////////
                // Disable Volume keys when retry scan dialog is visible
                // ///////////////////////////////////////////////////////////////////
                // case KeyEvent.KEYCODE_F6:
                    case KeyEvent.KEYCODE_VOLUME_UP:
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                    case KeyEvent.KEYCODE_MUTE: {
                        return true;
                    }
                    default:
                        break;
                }
                return false;
            }
        });
        alert.setTitleOfAlertDialog(R.string.first_time_install_no_services_found);
        alert.setPositiveButton(R.string.first_time_install_scan_restart,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isInFirstTimeInstall) {
                            MainActivity.activity.getMainMenuHandler()
                                    .getMainMenuDialog().show();
                            MainActivity.activity.getMainMenuHandler()
                                    .getA4TVOnSelectLister()
                                    .startAnimationsManual();
                            MainKeyListener
                                    .setAppState(MainKeyListener.MAIN_MENU);
                        }
                        ChannelInstallationDialog chDialog = MainActivity.activity
                                .getDialogManager()
                                .getChannelInstallationDialog();
                        if (chDialog != null) {
                            chDialog.show();
                        }
                        alert.cancel();
                    }
                });
        alert.setNegativeButton(R.string.first_time_install_just_finish,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInFirstTimeInstall) {
                            // end first time install and show main
                            // menu
                            if (MainActivity.activity.getFirstTimeInfoText() != null) {
                                MainActivity.activity
                                        .getFirstTimeInfoText()
                                        .setText(
                                                R.string.first_time_install_end_fti);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (MainActivity.activity
                                            .getFirstTimeInstallLayout() != null) {
                                        MainActivity.activity
                                                .getFirstTimeInstallLayout()
                                                .setVisibility(View.GONE);
                                    }
                                    MainActivity
                                            .getSharedPrefs()
                                            .edit()
                                            .putBoolean(
                                                    MainActivity.FIRST_TIME_INSTALL,
                                                    false).commit();
                                    MainActivity.isInFirstTimeInstall = false;
                                    MainActivity.activity.getCheckServiceType()
                                            .showNoSignalLayout();
                                    MainActivity.activity.getCallBackHandler()
                                            .setAntenaConnected(false);
                                }
                            }, TIME_TO_SHOW_FTI_END_TEXT);
                        } else {
                            new Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.activity
                                                    .getMainMenuHandler()
                                                    .getMainMenuDialog().show();
                                            MainActivity.activity
                                                    .getMainMenuHandler()
                                                    .getA4TVOnSelectLister()
                                                    .startAnimationsManual();
                                            MainKeyListener
                                                    .setAppState(MainKeyListener.MAIN_MENU);
                                        }
                                    },
                                    isInFirstTimeInstall ? TIME_TO_SHOW_FTI_END_TEXT + 100
                                            : 100);
                        }
                        alert.cancel();
                    }
                });
        // show alert dialog
        alert.show();
    }

    /** Set up position of grid dividers on screen */
    public void setUpDividers() {
        // Check resolution and add layout params
        ImageView allFirstGridDivider = (ImageView) findViewById(com.iwedia.gui.R.id.allItemFirstGridDivider);
        ImageView allSecondGridDivider = (ImageView) findViewById(com.iwedia.gui.R.id.allItemSecondGridDivider);
        ImageView allThirdGridDivider = (ImageView) findViewById(com.iwedia.gui.R.id.allItemThirdGridDivider);
        // //////////////////////////
        // 720p
        // //////////////////////////
        if (MainActivity.screenWidth == 1280) {
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 35;
            allFirstGridDivider.setLayoutParams(firstDivParams);
            // PE Android4TV
            if (MainActivity.screenHeight != MainActivity.SCREEN_HEIGHT_720P) {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 127;
                allSecondGridDivider.setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 165;
                allThirdGridDivider.setLayoutParams(thirdDivParams);
            }
            // AMP Android4TV
            else {
                FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                secondDivParams.topMargin = 135;
                allSecondGridDivider.setLayoutParams(secondDivParams);
                FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                        android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                        android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.TOP);
                thirdDivParams.topMargin = 175;
                allThirdGridDivider.setLayoutParams(thirdDivParams);
            }
        }
        // ////////////////////////////////
        // 1080p
        // ////////////////////////////////
        if (MainActivity.screenWidth == 1920) {
            FrameLayout.LayoutParams firstDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            firstDivParams.topMargin = 60;
            allFirstGridDivider.setLayoutParams(firstDivParams);
            FrameLayout.LayoutParams secondDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            secondDivParams.topMargin = 210;
            allSecondGridDivider.setLayoutParams(secondDivParams);
            FrameLayout.LayoutParams thirdDivParams = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP);
            thirdDivParams.topMargin = 265;
            allThirdGridDivider.setLayoutParams(thirdDivParams);
        }
    }

    /** Default values of filter option array */
    private void initFilterOptionArray() {
        // ////////////////////////////////
        // Check config
        // ////////////////////////////////
        filterOptions[FILTER_ALL_OPTION] = true;
        if (ConfigHandler.DVB_T) {
            filterOptions[FILTER_DVB_T_OPTION] = true;
        } else {
            filterOptions[FILTER_DVB_T_OPTION] = false;
        }
        if (ConfigHandler.DVB_S) {
            filterOptions[FILTER_DVB_S_OPTION] = true;
        } else {
            filterOptions[FILTER_DVB_S_OPTION] = false;
        }
        if (ConfigHandler.DVB_C) {
            filterOptions[FILTER_DVB_C_OPTION] = true;
        } else {
            filterOptions[FILTER_DVB_C_OPTION] = false;
        }
        if (ConfigHandler.IP) {
            filterOptions[FILTER_IP_OPTION] = true;
        } else {
            filterOptions[FILTER_IP_OPTION] = false;
        }
        if (ConfigHandler.ATV) {
            filterOptions[FILTER_ATV_OPTION] = true;
        } else {
            filterOptions[FILTER_ATV_OPTION] = false;
        }
    }

    /** Customize filter item */
    private void setFilterItemParams(LinearLayout filterItem) {
        LinearLayout.LayoutParams filterItemParams = new LinearLayout.LayoutParams(
                widthMeasureUnit, LayoutParams.MATCH_PARENT, Gravity.CENTER);
        filterItem.setLayoutParams(filterItemParams);
        filterItem.setEnabled(false);
        filterItem.setFocusable(false);
        filterItem.setGravity(Gravity.CENTER);
    }

    /** Filter content of content list */
    public void selectFilter(int filter) {
        int filterIndex = 0;
        for (int i = 0; i < filterButtons.size(); i++) {
            filterButtons.get(i).setSelected(false);
            if ((Integer) filterButtons.get(i).getTag() == filter) {
                filterIndex = i;
            }
        }
        // Select required one
        filterButtons.get(filterIndex).setSelected(true);
        filterButtons.get(filterIndex).setFocusable(true);
        filterButtons.get(filterIndex).requestFocus();
        filterButtons.get(filterIndex).setFocusable(false);
    }

    /** Load filter options layout */
    private void loadFilterOptions() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.contentListInputFilterBanner);
        // Clear lists of filter buttons
        layout.removeAllViews();
        filterButtons.clear();
        if (filterOptions[FILTER_ALL_OPTION]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton allButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            allButton.setFocusable(false);
            allButton.setText(ctx.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_filter_all));
            // if (MainActivity.activity.isFullHD()) {
            // allButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // allButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            setFilterItemParams(filterItem);
            filterButtons.add(filterItem);
            filterItem.setTag(FILTER_ALL_OPTION);
            layout.addView(filterItem);
        }
        if (filterOptions[FILTER_DVB_T_OPTION]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton dvbtButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            dvbtButton.setFocusable(false);
            if (ConfigHandler.ATSC) {
                dvbtButton
                        .setText(ctx
                                .getResources()
                                .getString(
                                        com.iwedia.gui.R.string.tv_menu_channel_installation_settings_air));
            } else {
                dvbtButton.setText(ctx.getResources().getString(
                        com.iwedia.gui.R.string.main_menu_content_list_dvb_t));
            }
            // if (MainActivity.activity.isFullHD()) {
            // dvbtButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // dvbtButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = ctx.getResources().getDrawable(
                    R.drawable.t_filter_option_selector);
            img.setBounds(0, 0, 23, 18);
            dvbtButton.setCompoundDrawables(img, null, null, null);
            dvbtButton.setPadding(
                    (int) ctx.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            setFilterItemParams(filterItem);
            filterButtons.add(filterItem);
            filterItem.setTag(FILTER_DVB_T_OPTION);
            layout.addView(filterItem);
        }
        if (filterOptions[FILTER_DVB_C_OPTION]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton dvbcButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            dvbcButton.setFocusable(false);
            if (ConfigHandler.ATSC) {
                dvbcButton
                        .setText(ctx
                                .getResources()
                                .getString(
                                        com.iwedia.gui.R.string.tv_menu_channel_installation_settings_cable));
            } else {
                dvbcButton.setText(ctx.getResources().getString(
                        com.iwedia.gui.R.string.main_menu_content_list_dvb_c));
            }
            // if (MainActivity.activity.isFullHD()) {
            // dvbcButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // dvbcButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = ctx.getResources().getDrawable(
                    R.drawable.c_filter_option);
            img.setBounds(0, 0, 23, 18);
            dvbcButton.setCompoundDrawables(img, null, null, null);
            dvbcButton.setPadding(
                    (int) ctx.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            setFilterItemParams(filterItem);
            filterButtons.add(filterItem);
            filterItem.setTag(FILTER_DVB_C_OPTION);
            layout.addView(filterItem);
        }
        if (filterOptions[FILTER_DVB_S_OPTION]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton dvbsButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            dvbsButton.setFocusable(false);
            if (ConfigHandler.ATSC) {
                dvbsButton.setText(ctx.getResources().getString(
                        com.iwedia.gui.R.string.main_menu_content_list_atsc_s));
            } else {
                dvbsButton.setText(ctx.getResources().getString(
                        com.iwedia.gui.R.string.main_menu_content_list_dvb_s));
            }
            // if (MainActivity.activity.isFullHD()) {
            // dvbsButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // dvbsButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = ctx.getResources().getDrawable(
                    R.drawable.s_filter_option);
            img.setBounds(0, 0, 23, 18);
            dvbsButton.setCompoundDrawables(img, null, null, null);
            dvbsButton.setPadding(
                    (int) ctx.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            setFilterItemParams(filterItem);
            filterButtons.add(filterItem);
            filterItem.setTag(FILTER_DVB_S_OPTION);
            layout.addView(filterItem);
        }
        if (filterOptions[FILTER_IP_OPTION]) {
            LinearLayout filterItem = (LinearLayout) inflater.inflate(
                    com.iwedia.gui.R.layout.content_list_filter_item, null);
            A4TVButton dvbsButton = (A4TVButton) filterItem
                    .findViewById(com.iwedia.gui.R.id.filterButton);
            dvbsButton.setFocusable(false);
            dvbsButton.setText(ctx.getResources().getString(
                    com.iwedia.gui.R.string.main_menu_content_list_ip));
            // if (MainActivity.activity.isFullHD()) {
            // dvbsButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
            // } else {
            // dvbsButton.setTextSize(ctx.getResources().getDimension(
            // com.iwedia.gui.R.dimen.content_filter_text_size));
            // }
            Drawable img = ctx.getResources().getDrawable(
                    R.drawable.ip_filter_option);
            img.setBounds(0, 0, 23, 18);
            dvbsButton.setCompoundDrawables(img, null, null, null);
            dvbsButton.setPadding(
                    (int) ctx.getResources().getDimension(
                            com.iwedia.gui.R.dimen.content_filter_padding), 0,
                    0, 0);
            setFilterItemParams(filterItem);
            filterButtons.add(filterItem);
            filterItem.setTag(FILTER_IP_OPTION);
            layout.addView(filterItem);
        }
        if (filterOptions[FILTER_ATV_OPTION]) {
            if (!ConfigHandler.ATSC) {
                LinearLayout filterItem = (LinearLayout) inflater.inflate(
                        com.iwedia.gui.R.layout.content_list_filter_item, null);
                A4TVButton atvButton = (A4TVButton) filterItem
                        .findViewById(com.iwedia.gui.R.id.filterButton);
                atvButton.setFocusable(false);
                atvButton.setText(ctx.getResources().getString(
                        com.iwedia.gui.R.string.main_menu_content_list_atv));
                // if (MainActivity.activity.isFullHD()) {
                // atvButton
                // .setTextSize(ctx
                // .getResources()
                // .getDimension(
                // com.iwedia.gui.R.dimen.content_filter_text_size_1080p));
                // } else {
                // atvButton.setTextSize(ctx.getResources().getDimension(
                // com.iwedia.gui.R.dimen.content_filter_text_size));
                // }
                Drawable img = ctx.getResources().getDrawable(
                        R.drawable.a_filter_option);
                img.setBounds(0, 0, 23, 18);
                atvButton.setCompoundDrawables(img, null, null, null);
                atvButton.setPadding(
                        (int) ctx.getResources().getDimension(
                                com.iwedia.gui.R.dimen.content_filter_padding),
                        0, 0, 0);
                setFilterItemParams(filterItem);
                filterButtons.add(filterItem);
                filterItem.setTag(FILTER_ATV_OPTION);
                layout.addView(filterItem);
            }
        }
    }

    /** Include DVB T in as filtering option */
    public void enableDVBT() {
        filterOptions[FILTER_DVB_T_OPTION] = true;
    }

    /** Include DVB T in as filtering option */
    public void enableDVBC() {
        filterOptions[FILTER_DVB_C_OPTION] = true;
    }

    /** Include DVB T in as filtering option */
    public void enableDVBS() {
        filterOptions[FILTER_DVB_S_OPTION] = true;
    }

    /** Include IP in as filtering option */
    public void enableIP() {
        filterOptions[FILTER_IP_OPTION] = true;
    }

    /** Include IP in as filtering option */
    public void enableATV() {
        filterOptions[FILTER_ATV_OPTION] = true;
    }

    // not needed here
    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
        ((A4TVProgressBar) seekBar).setText(String.valueOf(seekBar
                .getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public A4TVProgressBar getProgressScan() {
        return progressScan;
    }

    public A4TVProgressBar getProgressSignalStrength() {
        return progressSignalStrength;
    }

    public A4TVProgressBar getProgressSignalQuality() {
        return progressSignalQuality;
    }

    public A4TVTextView getTextTopBanner() {
        return textTopBanner;
    }

    public A4TVTextView getTextViewScannedFrequncy() {
        return textViewScannedFrequncy;
    }

    public Handler getHandlerScanFinished() {
        return handlerScanFinished;
    }

    public int getNumberOfServices() {
        return numberOfServices;
    }

    public void setNumberOfServices(int numberOfServices) {
        this.numberOfServices = numberOfServices;
    }

    public int getNumberOfRadio() {
        return numberOfRadio;
    }

    public void setNumberOfRadio(int numberOfRadio) {
        this.numberOfRadio = numberOfRadio;
    }

    public int getNumberOfData() {
        return numberOfData;
    }

    public void setNumberOfData(int numberOfData) {
        this.numberOfData = numberOfData;
    }

    public ArrayList<Content> getContentsList() {
        return contentsList;
    }

    public static boolean isScanning() {
        return isScanning;
    }

    public static void setScanning(boolean isScanning) {
        ChannelScanDialog.isScanning = isScanning;
    }
}
