package com.iwedia.gui.content_list;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.iwedia.comm.IParentalControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.program_blocking.ProgramLockingHandler;
import com.iwedia.gui.program_blocking.ProgramLockingHandler.ProgramLocking;

/**
 * Handling preparing data for drawing
 * 
 * @author Veljko Ilkic
 */
public class GridHelper implements OSDGlobal {
    /** Path if there is no image */
    public static final String NO_IMAGE = "-1";
    /** Log tag name */
    private final static String LOG_TAG = "GridHelper";
    /** Reference of main activity */
    private Activity activity;
    /** Layout inflater object */
    private LayoutInflater inflater;
    /** Parental control interface */
    private IParentalControl parentalControl = null;
    /** Content list control interface */
    private IContentListControl contentListControl = null;
    /** Program locking handler and interface */
    private ProgramLockingHandler programLockingHandler;
    private ProgramLocking programLockingInterface;
    /** Applies on main display only */
    private static final int mDisplayId = 0;

    /** Constructor 1 */
    public GridHelper(Activity activity) {
        super();
        // Take reference of main activity;
        this.activity = activity;
        // Create inflater
        inflater = (LayoutInflater) activity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /** Prepare data for get view method in adapters */
    public View prepareDataForAdapter(Content content) {
        // Inflate grid element xml
        LinearLayout item = (LinearLayout) inflater.inflate(
                com.iwedia.gui.R.layout.content_list_element_grid, null);
        // Set layout params on frame layout
        GridView.LayoutParams params1 = new GridView.LayoutParams(
                LayoutParams.MATCH_PARENT,
                (int) (5 * MainActivity.screenHeight / 28.5));
        item.setLayoutParams(params1);
        // Take reference of image view in content list item
        ImageView contentTypeImage = (ImageView) item
                .findViewById(com.iwedia.gui.R.id.contentFilterTypeImage);
        A4TVTextView contentName = (A4TVTextView) item
                .findViewById(com.iwedia.gui.R.id.contentName);
        ImageView contentImage = (ImageView) item
                .findViewById(com.iwedia.gui.R.id.contentImage);
        A4TVTextView contentIndexText = (A4TVTextView) item
                .findViewById(com.iwedia.gui.R.id.contentIndexText);
        A4TVTextView contentNameText = (A4TVTextView) item
                .findViewById(com.iwedia.gui.R.id.contentNameText);
        ImageView contentScrambled = (ImageView) item
                .findViewById(com.iwedia.gui.R.id.contentScrambledChannel);
        ImageView contentLockImage = (ImageView) item
                .findViewById(com.iwedia.gui.R.id.contentLockChannel);
        // if (((MainActivity) activity).isFullHD()) {
        // contentName.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_item_text_size_1080p));
        // contentNameText.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_list_no_image_name_1080p));
        // contentIndexText.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_list_no_image_index_1080p));
        // } else {
        // contentName.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_item_text_size));
        // contentNameText.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_list_no_image_name));
        // contentIndexText.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_list_no_image_index));
        // }
        // //////////////////////
        // Real element
        // //////////////////////
        if (content != null) {
            // ////////////////////////////////////
            // Set content filter icon
            // ////////////////////////////////////
            Log.d(LOG_TAG, "content: " + content.toString());
            Log.d(LOG_TAG, "content filter type: " + content.getFilterType());
            if (content instanceof ServiceContent) {
                switch (content.getServiceType()) {
                    case DIG_TV: {
                        switch (content.getSourceType()) {
                            case TER: {
                                contentTypeImage
                                        .setImageResource(com.iwedia.gui.R.drawable.t_filter_selector);
                                break;
                            }
                            case CAB: {
                                contentTypeImage
                                        .setImageResource(com.iwedia.gui.R.drawable.c_filter_selector);
                                break;
                            }
                            case SAT: {
                                contentTypeImage
                                        .setImageResource(com.iwedia.gui.R.drawable.s_filter_selector);
                                break;
                            }
                            case ANALOG: {
                                contentTypeImage
                                        .setImageResource(com.iwedia.gui.R.drawable.a_filter_selector);
                                break;
                            }
                        }
                        break;
                    }
                    case DIG_RAD: {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.radio_filter_selector);
                        break;
                    }
                    case DATA_BROADCAST: {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.data_filter_selector);
                        break;
                    }
                }
            } else {
                switch (content.getFilterType()) {
                    case FilterType.INPUTS: {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.inputs_filter_selector);
                        break;
                    }
                    case FilterType.APPS: {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.apps_filter_selector);
                        break;
                    }
                    case FilterType.WIDGETS: {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.widget_filter_selector);
                        break;
                    }
                    case FilterType.IP_STREAM: {
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.ip_filter_selector);
                        break;
                    }
                }
            }
            // Get content list control object and active filter
            int activeFilterInService = FilterType.ALL;
            if (contentListControl == null) {
                try {
                    contentListControl = MainActivity.service
                            .getContentListControl();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                if (contentListControl != null)
                    activeFilterInService = contentListControl
                            .getActiveFilterIndex();
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
            if (parentalControl == null) {
                // Get parental control interface
                try {
                    parentalControl = MainActivity.service.getParentalControl();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
            // /////////////////////////////
            // TV service
            // /////////////////////////////
            if (content instanceof ServiceContent) {
                // ////////////////////////////
                // Name and scrambled checking
                // /////////////////////////////
                // ///////////////////
                // Check HD
                // ///////////////////
                String stringHD = "";
                Log.d(LOG_TAG, "prepareDataForAdapter");
                ServiceContent sContent = (ServiceContent) content;
                // TODO: ZORANA: determine is HD according to dtv.ServiceType
                // if(sContent.isHD()) {
                // stringHD = "HD ";
                // }
                int contentIndex = 0;
                if (activeFilterInService == FilterType.ALL) {
                    if (contentListControl != null) {
                        try {
                            contentIndex = contentListControl
                                    .getContentIndexInAllList(content) + 1;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (ConfigHandler.USE_LCN)
                        contentIndex = content.getServiceLCN();
                    else
                        contentIndex = content.getIndex() + 1;
                }
                if (ConfigHandler.ATSC) {
                    int major;
                    int minor;
                    if (ConfigHandler.USE_LCN) {
                        major = content.getServiceLCN()
                                / MAJOR_MINOR_CONVERT_NUMBER;
                        minor = content.getServiceLCN()
                                % MAJOR_MINOR_CONVERT_NUMBER;
                    } else {
                        major = content.getIndex() / MAJOR_MINOR_CONVERT_NUMBER;
                        minor = content.getIndex() % MAJOR_MINOR_CONVERT_NUMBER;
                    }
                    contentName.setText(stringHD + " "
                            + String.format("%d-%d", major, minor) + ". "
                            + content.getName());
                } else {
                    contentName.setText(stringHD + " " + contentIndex + ". "
                            + content.getName());
                }
                // ////////////////////////////////////
                // Check if channel is scrambled
                // ////////////////////////////////////
                checkScrambled(sContent.isScrambled(), contentScrambled);
                // /////////////////////////////////////
                // Check if channel is locked
                // /////////////////////////////////////
                boolean isContentLocked = false;
                try {
                    if (parentalControl != null)
                        isContentLocked = parentalControl
                                .getChannelLock(sContent.getIndexInMasterList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkLock(isContentLocked, contentLockImage);
                // Get image path
                String imagePath = content.getImage();
                if (!imagePath.equals(NO_IMAGE)) {
                    // ///////////////////////
                    // There is some image
                    // ////////////////////////
                    contentImage.setImageBitmap(MainActivity.mMemoryCache
                            .loadBitmapFromDisk(imagePath));
                    contentImage.setScaleType(ScaleType.FIT_XY);
                    FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                            MainActivity.screenWidth / 12,
                            MainActivity.screenHeight / 12);
                    imageParams.gravity = Gravity.CENTER;
                    contentImage.setLayoutParams(imageParams);
                    contentIndexText.setText("");
                    contentNameText.setText("");
                } else {
                    // ////////////////////////////////
                    // There is no image
                    // ////////////////////////////////
                    // contentIndexText
                    // .setText(String.valueOf(content.getIndex() + 1));
                    if (content.getName().trim().length() < 2) {
                        if (activeFilterInService == FilterType.ALL) {
                            contentIndexText.setText(String.valueOf(content
                                    .getIndexInMasterList()));
                        } else {
                            if (ConfigHandler.USE_LCN)
                                contentIndexText.setText(String.valueOf(content
                                        .getServiceLCN()));
                            else
                                contentIndexText.setText(String.valueOf(content
                                        .getIndex()));
                        }
                    } else {
                        contentNameText.setText(content.getName());
                    }
                    contentImage.setImageBitmap(null);
                }
                // ////////////////////////////////////
                // Check if channel is selectable
                // ////////////////////////////////////
                if (sContent.isSelectable() == false) {
                    contentName.setTextColor(activity.getResources().getColor(
                            R.color.filter_option_non_selected_text_ics));
                    contentNameText
                            .setTextColor(activity
                                    .getResources()
                                    .getColor(
                                            R.color.filter_option_non_selected_text_ics));
                }
            }
            // // /////////////////////////////////
            // // Radio service
            // // /////////////////////////////////
            // if (content.getFilterType() == FilterType.RADIO) {
            //
            // // ////////////////////////////
            // // Name and scrambled checking
            // // /////////////////////////////
            //
            // int contentIndex = 0;
            //
            // if (activeFilterInService == FilterType.ALL) {
            //
            // if (contentListControl != null) {
            // try {
            //
            // contentIndex = contentListControl
            // .getContentIndexInAllList(content) + 1;
            // } catch (RemoteException e) {
            // e.printStackTrace();
            // }
            // }
            // } else {
            //
            // contentIndex = content.getIndex() + 1;
            // }
            //
            // contentName.setText(contentIndex + ". " + content.getName());
            //
            // // ////////////////////////////////////
            // // Check if channel is scrambled
            // // ////////////////////////////////////
            //
            // RadioContent rContent = (RadioContent) content;
            //
            // checkScrambled(rContent.isScrambled(), contentScrambled);
            //
            // // /////////////////////////////////////
            // // Check if channel is locked
            // // /////////////////////////////////////
            //
            // boolean isContentLocked = false;
            // try {
            // if (parentalControl != null)
            // isContentLocked = parentalControl
            // .getChannelLock(rContent.getIndexInMasterList());
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            //
            // checkLock(isContentLocked, contentLockImage);
            //
            // String imagePath = content.getImage();
            // if (!imagePath.equals(NO_IMAGE)) {
            // // ///////////////////////
            // // There is some image
            // // ////////////////////////
            // contentImage.setImageBitmap(MainActivity.mMemoryCache
            // .loadBitmapFromDisk(imagePath));
            // contentImage.setScaleType(ScaleType.FIT_XY);
            //
            // FrameLayout.LayoutParams imageParams = new
            // FrameLayout.LayoutParams(
            // MainActivity.screenWidth / 12,
            // MainActivity.screenHeight / 12);
            // imageParams.gravity = Gravity.CENTER;
            // contentImage.setLayoutParams(imageParams);
            //
            // contentIndexText.setText("");
            // contentNameText.setText("");
            //
            // } else {
            //
            // // ////////////////////////////////
            // // There is no image
            // // ////////////////////////////////
            //
            // // contentIndexText.setText(content.getIndex() + 1);
            // // contentNameText.setText(content.getName());
            //
            // if (content.getName().trim().length() < 2) {
            // contentIndexText.setText(String.valueOf(content
            // .getIndex() + 1));
            // } else {
            // contentNameText.setText(content.getName());
            // }
            //
            // contentImage.setImageBitmap(null);
            // }
            //
            // // ////////////////////////////////////
            // // Check if channel is selectable
            // // ////////////////////////////////////
            // if (content.isSelectable() == false) {
            // contentName.setTextColor(activity.getResources().getColor(
            // R.color.filter_option_non_selected_text_ics));
            // contentNameText
            // .setTextColor(activity
            // .getResources()
            // .getColor(
            // R.color.filter_option_non_selected_text_ics));
            // }
            //
            // }
            // ////////////////////////////////////////
            // Inputs
            // /////////////////////////////////////////
            if (content.getFilterType() == FilterType.INPUTS) {
                boolean isContentConnected;
                try {
                    int contentIndex = content.getIndex();
                    isContentConnected = MainActivity.service
                            .getInputOutputControl().ioGetDeviceConnected(
                                    contentIndex);
                    if (isContentConnected == false) {
                        contentName
                                .setTextColor(activity
                                        .getResources()
                                        .getColor(
                                                R.color.filter_option_non_selected_text_ics));
                        contentTypeImage
                                .setImageResource(com.iwedia.gui.R.drawable.inputs_filter_non_active);
                        contentImage
                                .setImageResource(com.iwedia.gui.R.drawable.inputs_filter_non_active);
                    } else {
                        contentImage
                                .setImageResource(com.iwedia.gui.R.drawable.inputs_filter_selector);
                    }
                    // /////////////////////////////////////
                    // Check if input is disabled
                    // /////////////////////////////////////
                    boolean isContentDisabled = false;
                    try {
                        if (contentListControl != null)
                            isContentDisabled = contentListControl
                                    .getContentLockedStatus(content);
                        if (isContentDisabled) {
                            checkDisabled(isContentDisabled, contentLockImage);
                            contentName
                                    .setTextColor(activity
                                            .getResources()
                                            .getColor(
                                                    R.color.filter_option_non_selected_text_ics));
                            contentTypeImage
                                    .setImageResource(com.iwedia.gui.R.drawable.inputs_filter_non_active);
                            contentImage
                                    .setImageResource(com.iwedia.gui.R.drawable.inputs_filter_non_active);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                contentName.setText(content.getName());
            }
            // App
            if (content.getFilterType() == FilterType.APPS) {
                try {
                    // /////////////////////////////////////
                    // Check if channel is locked
                    // /////////////////////////////////////
                    boolean isContentLocked = false;
                    try {
                        if (contentListControl != null)
                            isContentLocked = contentListControl
                                    .getContentLockedStatus(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkLock(isContentLocked, contentLockImage);
                    Drawable myDrawable = activity.getPackageManager()
                            .getApplicationIcon(content.getImage());
                    contentImage.setBackgroundDrawable(myDrawable);
                    contentImage.setImageBitmap(null);
                    FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                            MainActivity.screenHeight / 10,
                            MainActivity.screenHeight / 10);
                    imageParams.gravity = Gravity.CENTER;
                    contentImage.setLayoutParams(imageParams);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                contentName.setText(content.getName());
            }
            // ////////////////////////////////////////
            // DATA
            // /////////////////////////////////////////
            // if (content.getFilterType() == FilterType.DATA) {
            //
            // // ////////////////////////////
            // // Name and scrambled checking
            // // /////////////////////////////
            //
            // int contentIndex = 0;
            //
            // if (activeFilterInService == FilterType.ALL) {
            //
            // if (contentListControl != null) {
            // try {
            // contentIndex = contentListControl
            // .getContentIndexInAllList(content) + 1;
            // } catch (RemoteException e) {
            // e.printStackTrace();
            // }
            // }
            // } else {
            // contentIndex = content.getIndex() + 1;
            // }
            //
            // contentName.setText(contentIndex + ". " + content.getName());
            //
            // // ////////////////////////////////////
            // // Check if channel is scrambled
            // // ////////////////////////////////////
            //
            // checkScrambled(false, contentScrambled);
            //
            // // /////////////////////////////////////
            // // Check if channel is locked
            // // /////////////////////////////////////
            //
            // boolean isContentLocked = false;
            // try {
            // if (parentalControl != null)
            // isContentLocked = parentalControl
            // .getChannelLock(content.getIndexInMasterList());
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
            //
            // checkLock(isContentLocked, contentLockImage);
            //
            // String imagePath = content.getImage();
            // if (!imagePath.equals(NO_IMAGE)) {
            // // ///////////////////////
            // // There is some image
            // // ////////////////////////
            // contentImage.setImageBitmap(MainActivity.mMemoryCache
            // .loadBitmapFromDisk(imagePath));
            // contentImage.setScaleType(ScaleType.FIT_XY);
            //
            // FrameLayout.LayoutParams imageParams = new
            // FrameLayout.LayoutParams(
            // MainActivity.screenWidth / 12,
            // MainActivity.screenHeight / 12);
            // imageParams.gravity = Gravity.CENTER;
            // contentImage.setLayoutParams(imageParams);
            //
            // contentIndexText.setText("");
            // contentNameText.setText("");
            //
            // } else {
            //
            // // ////////////////////////////////
            // // There is no image
            // // ////////////////////////////////
            //
            // // contentIndexText.setText(content.getIndex() + 1);
            // // contentNameText.setText(content.getName());
            //
            // if (content.getName().trim().length() < 2) {
            // contentIndexText.setText(String.valueOf(content
            // .getIndex() + 1));
            // } else {
            // contentNameText.setText(content.getName());
            // }
            //
            // contentImage.setImageBitmap(null);
            // }
            //
            // }
            // //////////////////////////////////////
            // Widgets
            // /////////////////////////////////////////////
            if (content.getFilterType() == FilterType.WIDGETS) {
                try {
                    // /////////////////////////////////////
                    // Check if channel is locked
                    // /////////////////////////////////////
                    boolean isContentLocked = false;
                    try {
                        if (contentListControl != null)
                            isContentLocked = contentListControl
                                    .getContentLockedStatus(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkLock(isContentLocked, contentLockImage);
                    Drawable myDrawable = activity.getPackageManager()
                            .getApplicationIcon(content.getImage());
                    contentImage.setBackgroundDrawable(myDrawable);
                    contentImage.setImageBitmap(null);
                    FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                            MainActivity.screenHeight / 10,
                            MainActivity.screenHeight / 10);
                    imageParams.gravity = Gravity.CENTER;
                    contentImage.setLayoutParams(imageParams);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                String[] widgetText = content.getName().split(":");
                String widgetName = widgetText[0];
                contentName.setText(widgetName);
                // Set active flag on widget if needed
                if (((MainActivity) activity).getPageCurl()
                        .getChannelChangeHandler()
                        .isContentActive(content, mDisplayId)) {
                    ImageView widgetActiveFlag = (ImageView) item
                            .findViewById(com.iwedia.gui.R.id.contentWidgetActiveFlag);
                    widgetActiveFlag
                            .setImageResource(com.iwedia.gui.R.drawable.widget_active);
                }
            }
            // /////////////////////////////////////////////
            // IP STREAM
            // /////////////////////////////////////////////
            if (content.getFilterType() == FilterType.IP_STREAM) {
                int contentIndex = 0;
                if (activeFilterInService == FilterType.ALL) {
                    if (contentListControl != null) {
                        try {
                            contentIndex = contentListControl
                                    .getContentIndexInAllList(content) + 1;
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    contentIndex = content.getIndex() + 1;
                }
                // contentImage.setImageBitmap(null);
                // /////////////////////////////////////
                // Check if channel is locked
                // /////////////////////////////////////
                boolean isContentLocked = false;
                try {
                    if (contentListControl != null)
                        isContentLocked = contentListControl
                                .getContentLockedStatus(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                contentName.setText(contentIndex + ". " + content.getName());
                checkLock(isContentLocked, contentLockImage);
                String imagePath = content.getImage();
                if (!imagePath.equals(NO_IMAGE)) {
                    // ///////////////////////
                    // There is some image
                    // ////////////////////////
                    contentImage.setImageBitmap(MainActivity.mMemoryCache
                            .loadBitmapFromDisk(imagePath));
                    contentImage.setScaleType(ScaleType.FIT_XY);
                    FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                            MainActivity.screenWidth / 12,
                            MainActivity.screenHeight / 12);
                    imageParams.gravity = Gravity.CENTER;
                    contentImage.setLayoutParams(imageParams);
                    contentIndexText.setText("");
                    contentNameText.setText("");
                } else {
                    // ////////////////////////////////
                    // There is no image
                    // ////////////////////////////////
                    // contentIndexText.setText(content.getIndex() + 1);
                    // contentNameText.setText(content.getName());
                    if (content.getName().trim().length() < 2) {
                        contentIndexText.setText(String.valueOf(content
                                .getIndex() + 1));
                    } else {
                        contentNameText.setText(activity.getResources()
                                .getString(R.string.main_menu_content_list_ip)
                                + " : " + content.getName());
                    }
                    contentImage.setImageBitmap(null);
                }
            }
        }
        // //////////////////////
        // Fake item
        // //////////////////////
        else {
            // Set image and text on content list item
            contentImage.setImageBitmap(null);
            contentImage.setVisibility(View.INVISIBLE);
            contentName.setText("");
            contentName.setVisibility(View.INVISIBLE);
            item.setVisibility(View.INVISIBLE);
            item.setFocusable(false);
            item.setEnabled(false);
            item.setClickable(false);
        }
        return item;
    }

    /** Check if content is scrambled */
    public void checkScrambled(boolean scrambledString,
            ImageView contentScrambled) {
        if (scrambledString) {
            contentScrambled
                    .setImageResource(com.iwedia.gui.R.drawable.scrambled_channel);
        } else {
            contentScrambled.setImageBitmap(null);
        }
    }

    /** Check if content is locked */
    public void checkLock(boolean contentLocked, ImageView contentLockImage) {
        if (contentLocked) {
            contentLockImage.setImageResource(com.iwedia.gui.R.drawable.lock);
        } else {
            contentLockImage.setImageBitmap(null);
        }
    }

    /** Check if content is disabled */
    public void checkDisabled(boolean contentDisabled,
            ImageView contentLockImage) {
        if (contentDisabled) {
            contentLockImage
                    .setImageResource(com.iwedia.gui.R.drawable.disable);
        } else {
            contentLockImage.setImageBitmap(null);
        }
    }

    /** Change channel from content list procedure */
    private void changeChannel(Content content, int displayId) {
        Log.d(LOG_TAG, "CHANGE CHANNEL ENTERED");
        ((MainActivity) activity).getContentListHandler().closeContentList();
        ContentListHandler.syncFilterIndexes(true);
        try {
            IOSDHandler curlHandler = ((MainActivity) activity).getPageCurl();
            if (content.isSelectable() == true) {
                // Start curl animation
                if (1 != displayId) {
                    curlHandler.changeChannelByContent(content, displayId);
                } else
                    MainActivity.service.getContentListControl().goContent(
                            content, displayId);
            } else {
                A4TVToast toast = new A4TVToast(activity);
                toast.showToast("Not selectable content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Go widget */
    private void goWidget(Content content) {
        // Check visibility of widget
        if (!((MainActivity) activity).getPageCurl().getChannelChangeHandler()
                .isContentActive(content, mDisplayId)) {
            // /////////////////////////////////
            // Not visible`
            // //////////////////////////////////
            String[] widgetText = content.getName().split(":");
            String widgetClass = widgetText[1];
            ((MainActivity) activity).getWidgetsHandler()
                    .createWidgetComponent(content.getImage(), widgetClass);
        } else {
            // ///////////////////////////////
            // Visible
            // ///////////////////////////////
            ((MainActivity) activity).getWidgetsHandler()
                    .removeWidgetFromScreen(content);
        }
        ((MainActivity) activity).getContentListHandler().closeContentList();
    }

    /** Go application */
    private void goApp(Content content, int displayId) {
        Log.d(LOG_TAG, "goApp: displayId=" + displayId);
        if (displayId != 0) {
            MainActivity.activity.playOverlayVideo(false);
        }
        // Start content
        try {
            ((MainActivity) activity).getContentListHandler()
                    .getContentListDialog().cancel();
            ((MainActivity) activity).getContentListHandler()
                    .closeContentList();
            if (contentListControl != null) {
                contentListControl.goContent(content, 0);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /** Go content on click */
    public void goContent(final Content content, final int displayId) {
        Log.d(LOG_TAG, "GO CONTENT ENTERED");
        if (contentListControl == null) {
            try {
                contentListControl = MainActivity.service
                        .getContentListControl();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        // ////////////////////////////////////
        // Tv service
        // ////////////////////////////////////
        if (content instanceof ServiceContent) {
            Log.d(LOG_TAG, "TV SERVICES ENTERED");
            try {
                if (contentListControl != null) {
                    Content activeContent = contentListControl
                            .getActiveContent(displayId);
                    if ((activeContent != null)
                            && (activeContent.getFilterType() == FilterType.INPUTS)) {
                        contentListControl
                                .stopContent(activeContent, displayId);
                        ((MainActivity) activity).setAnalogSignalLock(false);
                        changeChannel(content, displayId);
                    } else {
                        // If this channel isn't already active start it
                        if (!((MainActivity) activity).getPageCurl()
                                .getChannelChangeHandler()
                                .isContentActive(content, displayId)) {
                            // Channel isn't locked and is selectable
                            changeChannel(content, displayId);
                        } else {
                            ((MainActivity) activity).getContentListHandler()
                                    .closeContentList();
                            ContentListHandler.syncFilterIndexes(false);
                            // Show already active message
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(com.iwedia.gui.R.string.already_active);
                        }
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (content.getFilterType() == FilterType.INPUTS) {
            // Start content
            try {
                Log.d(LOG_TAG, "Start input");
                // Check if it is disabled
                boolean isDisabled = false;
                try {
                    if (contentListControl != null) {
                        isDisabled = contentListControl
                                .getContentLockedStatus(content);
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                if (isDisabled) {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(R.string.input_is_disabled);
                } else {
                    ((MainActivity) activity).getContentListHandler()
                            .closeContentList();
                    int contentIndex = content.getIndex();
                    if (contentIndex > 1) { // Fix for DVB and ATV inputs.
                        if (MainActivity.service.getInputOutputControl()
                                .ioGetDeviceConnected(contentIndex)) {
                            if (contentListControl != null) {
                                Content activeContent = contentListControl
                                        .getActiveContent(displayId);
                                if ((activeContent != null)
                                        && (activeContent.getFilterType() == content
                                                .getFilterType())
                                        && (activeContent.getIndex() == content
                                                .getIndex())) {
                                    ContentListHandler.syncFilterIndexes(false);
                                    // Show already active message
                                    A4TVToast toast = new A4TVToast(activity);
                                    toast.showToast(com.iwedia.gui.R.string.already_active);
                                } else {
                                    // stop previous inputs
                                    if ((activeContent != null)
                                            && (displayId == 0)) {
                                        contentListControl.stopContent(
                                                activeContent, displayId);
                                        ((MainActivity) activity)
                                                .setAnalogSignalLock(false);
                                    }
                                    if (displayId == 0) {
                                        ContentListHandler
                                                .syncFilterIndexes(true);
                                    }
                                    if ((activeContent != null)
                                            && (activeContent.getFilterType() != FilterType.INPUTS)
                                            && (activeContent.getSourceType() != SourceType.ANALOG)) {
                                        /*
                                         * Remove WebView from screen and set
                                         * key mask to 0
                                         */
                                        if (0 != (MainActivity.getKeySet())) {
                                            try {
                                                if (!MainActivity.activity
                                                        .isHbbTVInHTTPPlaybackMode()) {
                                                    MainActivity.activity.webDialog
                                                            .getHbbTVView()
                                                            .setAlpha(
                                                                    (float) 0.00);
                                                    MainActivity.setKeySet(0);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    ((MainActivity) activity).sourceSwichingProgressDialog
                                            .show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                contentListControl.goContent(
                                                        content, displayId);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                Thread.sleep(10000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            activity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    ((MainActivity) activity).sourceSwichingProgressDialog
                                                            .cancel();
                                                    if (displayId == 0) {
                                                        if (((MainActivity) activity)
                                                                .getIsAnalogSignalLocked() == false) {
                                                            MainActivity.activity
                                                                    .getCheckServiceType()
                                                                    .showNoSignalLayout();
                                                        }
                                                    } else {
                                                        if (((MainActivity) activity)
                                                                .getIsAnalogSignalLocked() == false) {
                                                            try {
                                                                contentListControl
                                                                        .stopContent(
                                                                                content,
                                                                                1);
                                                                MainActivity.activity
                                                                        .getPrimaryVideoView()
                                                                        .setScaling(
                                                                                0,
                                                                                0,
                                                                                1920,
                                                                                1080);
                                                                MainActivity.activity
                                                                        .getSecondaryVideoView()
                                                                        .updateVisibility(
                                                                                View.INVISIBLE);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                            A4TVToast toast = new A4TVToast(
                                                                    activity);
                                                            toast.setDuration(A4TVToast.LENGTH_LONG);
                                                            toast.showToast("No signal on secondary display unit");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            } else {
                                A4TVToast toast = new A4TVToast(activity);
                                toast.showToast(com.iwedia.gui.R.string.input_not_connected);
                            }
                        }
                    } else {
                        if (contentListControl != null) {
                            Content activeContent = contentListControl
                                    .getActiveContent(displayId);
                            Content inputContent = null;
                            // stop previous inputs
                            if ((activeContent != null)
                                    && (displayId == 0)
                                    && (activeContent.getFilterType() == FilterType.INPUTS)) {
                                contentListControl.stopContent(activeContent,
                                        displayId);
                                ((MainActivity) activity)
                                        .setAnalogSignalLock(false);
                            }
                            contentListControl.setActiveFilter(FilterType.ALL);
                            inputContent = contentListControl.getContent(0);
                            if (inputContent == null
                                    || !(inputContent instanceof ServiceContent)) {
                                // Show empty list message
                                A4TVToast toast = new A4TVToast(activity);
                                toast.showToast(R.string.empty_list);
                            } else {
                                changeChannel(inputContent, 0);
                            }
                        }
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            // /////////////////////////////////////////////
            // IP stream
            // /////////////////////////////////////////////
            if (content.getFilterType() == FilterType.IP_STREAM) {
                Log.d(LOG_TAG, "IP CHANNEL ENTERED");
                // If this channel isn't already active start it
                if (!((MainActivity) activity).getPageCurl()
                        .getChannelChangeHandler()
                        .isContentActive(content, displayId)) {
                    Log.d(LOG_TAG, "IP NOT ACTIVE");
                    if (contentListControl == null) {
                        // Get parental control interface
                        try {
                            contentListControl = MainActivity.service
                                    .getContentListControl();
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                    // Check if it is locked
                    boolean isLocked = false;
                    try {
                        if (contentListControl != null)
                            isLocked = contentListControl
                                    .getContentLockedStatus(content);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    Log.d(LOG_TAG, "IP CHANNEL IS LOCKED" + isLocked);
                    // ////////////////////////
                    // Locked
                    // ////////////////////////
                    if (isLocked) {
                        Log.d(LOG_TAG, "SHOW PASSWORD");
                        programLockingInterface = new ProgramLocking() {
                            @Override
                            public void pinIsOk() {
                                // Must be called this way for IP content
                                changeChannel(content, displayId);
                            }

                            @Override
                            public void cancel() {
                            }
                        };
                        programLockingHandler = new ProgramLockingHandler(
                                activity, programLockingInterface);
                        programLockingHandler.showPasswordDialog();
                    }
                    // //////////////////////////
                    // Not locked
                    // //////////////////////////
                    else {
                        // Must be called this way for IP content
                        changeChannel(content, displayId);
                    }
                } else {
                    ((MainActivity) activity).getContentListHandler()
                            .closeContentList();
                    // Show already active message
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(com.iwedia.gui.R.string.already_active);
                }
            } else {
                // //////////////////////////////////////
                // Widgets
                // //////////////////////////////////////
                if (content.getFilterType() == FilterType.WIDGETS) {
                    if (contentListControl == null) {
                        // Get parental control interface
                        try {
                            contentListControl = MainActivity.service
                                    .getContentListControl();
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                    // Check if it is locked
                    boolean isLocked = false;
                    try {
                        if (contentListControl != null)
                            isLocked = contentListControl
                                    .getContentLockedStatus(content);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    // ////////////////////////
                    // Locked
                    // ////////////////////////
                    if (isLocked) {
                        programLockingInterface = new ProgramLocking() {
                            @Override
                            public void pinIsOk() {
                                goWidget(content);
                            }

                            @Override
                            public void cancel() {
                            }
                        };
                        programLockingHandler = new ProgramLockingHandler(
                                activity, programLockingInterface);
                        programLockingHandler.showPasswordDialog();
                    }
                    // //////////////////////////
                    // Not locked
                    // //////////////////////////
                    else {
                        goWidget(content);
                    }
                } else {
                    // ///////////////////////////////
                    // Other (Applications)
                    // ///////////////////////////////
                    if (parentalControl == null) {
                        // Get parental control interface
                        try {
                            parentalControl = MainActivity.service
                                    .getParentalControl();
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                    // Check if it is locked
                    boolean isLocked = false;
                    try {
                        if (contentListControl != null)
                            isLocked = contentListControl
                                    .getContentLockedStatus(content);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    // ////////////////////////
                    // Locked
                    // ////////////////////////
                    if (isLocked) {
                        programLockingInterface = new ProgramLocking() {
                            @Override
                            public void pinIsOk() {
                                goApp(content, displayId);
                            }

                            @Override
                            public void cancel() {
                            }
                        };
                        programLockingHandler = new ProgramLockingHandler(
                                activity, programLockingInterface);
                        programLockingHandler.showPasswordDialog();
                    }
                    // //////////////////////////
                    // Not locked
                    // //////////////////////////
                    else {
                        goApp(content, displayId);
                    }
                }
            }
        }
    }

    public void stopContent(final Content activeContent, final int displayId) {
        Log.d(LOG_TAG, "stopContent entered");
        if (contentListControl == null) {
            try {
                contentListControl = MainActivity.service
                        .getContentListControl();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        try {
            // ////////////////////////////////////
            // Tv service
            // ////////////////////////////////////
            if (activeContent instanceof ServiceContent) {
                Log.d(LOG_TAG, "TV services entered");
                if (contentListControl != null) {
                    contentListControl.stopContent(activeContent, displayId);
                }
                ((MainActivity) activity).getContentListHandler()
                        .closeContentList();
                ContentListHandler.syncFilterIndexes(true);
            }
            // ////////////////////////////////////
            // Input
            // ////////////////////////////////////
            else if (activeContent.getFilterType() == FilterType.INPUTS) {
                Log.d(LOG_TAG, "Input entered");
                if (contentListControl != null) {
                    contentListControl.stopContent(activeContent, displayId);
                }
                ((MainActivity) activity).setAnalogSignalLock(false);
                ((MainActivity) activity).getContentListHandler()
                        .closeContentList();
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }
}
