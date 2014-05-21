package com.iwedia.gui.multimedia;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVPhotoView;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.listeners.MainKeyListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Multimedia show handler
 * 
 * @author Veljko Ilkic
 */
public class MultimediaShowHandler implements MultimediaGlobal {
    private static final String TAG = "MultimediaShowHandler";
    /** Reference of main activity */
    private Activity activity;
    /** Multimedia show dialog */
    private A4TVDialog multimediaShowDialog;
    /** Multimedia show image */
    private A4TVPhotoView multimediaShowImage;
    /** Next and previous buttons for images */
    private static A4TVButton multimediaShowPreviousButton,
            multimediaShowNextButton;
    /** Image name */
    private A4TVTextView multimediaShowImageName;
    /** Index of last showed image */
    private int indexOfLastImageShowed = 0;
    /** Index of last showed audio or video */
    private int indexOfLastAudioOrVideoShowed;
    /** Index of last showed pvr file */
    private int indexOfLastPvrFileShowed;
    /** Number of items in folder */
    private int numberOfItems;
    /** Bitmap to show */
    private Bitmap newBitmap;
    /** Scaled and rotated bitmap */
    private Bitmap mBitmap = null;
    /** Progress dialog for loading data */
    private A4TVProgressDialog progressDialog;
    private final static int IMAGE_MAX_SIZE = 1920;
    private static boolean showNavigationArrows = true;
    private ExifInterface exif;
    private int degree = 0;
    private int zoomLevel = 0;
    private Timer mTimer = new Timer();
    private TimerTask mStateTimerTask = null;
    private static final int NEXT_IMAGE = 0;
    private static final int HOLD_IMAGE = 1;
    private static final long IMAGE_ANIMATION_TIME = 4000L;
    private ImageView imageViewSlideShow = null;
    private LinearLayout multimediaShow = null;
    private LinearLayout linearLayoutSlideShow = null;
    private boolean slideShow = false;
    private long timeToHoldImageOnScreen = 0;
    private boolean repeatSlideShow = false;
    private PausableScaleAnimation alphaAnimation;
    private boolean mFlagPlayPause = true;
    private long startTimeSlideShow = 0;
    private long timeFromPlayToPauseSlideShow = 0;
    private boolean isRunning = false;
    private ImageView imageViewRepeatSlideShow = null;

    /** Constructor 1 */
    public MultimediaShowHandler(final Activity activity,
            A4TVDialog multimediaShowDialog) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take reference of multimedia show dialog
        this.multimediaShowDialog = multimediaShowDialog;
        // Init
        init();
    }

    /** Init function */
    private void init() {
        // /////////////////////////////////////////////
        // Set key listener on multimedia show dialog
        // //////////////////////////////////////////////
        multimediaShowDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (MainKeyListener.getAppState() != MainKeyListener.DLNA_RENDERER) {
                        // Number of items
                        numberOfItems = MultimediaFileBrowserHandler
                                .getFileBrowserNumberOfItems();
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                // Check if navigation is enabled
                                if (showNavigationArrows) {
                                    zoomLevel = 0;
                                    // Load next image
                                    if (!findNextImage()) {
                                        A4TVToast toast = new A4TVToast(
                                                activity);
                                        toast.showToast(R.string.dlna_playback_no_next_file);
                                    }
                                } else {
                                    return true;
                                }
                                break;
                            }
                            case KeyEvent.KEYCODE_DPAD_LEFT: {
                                // Check if navigation is enabled
                                if (showNavigationArrows) {
                                    zoomLevel = 0;
                                    // Load previous image
                                    if (!findPreviousImage()) {
                                        A4TVToast toast = new A4TVToast(
                                                activity);
                                        toast.showToast(R.string.dlna_playback_no_previous_file);
                                    }
                                } else {
                                    return true;
                                }
                                break;
                            }
                            // Image slide show
                            case 126:
                            case 127:
                            case KeyEvent.KEYCODE_P:
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                                if (mFlagPlayPause) {
                                    slideShow = true;
                                    imageViewSlideShow.setImageBitmap(mBitmap);
                                    multimediaShow.setVisibility(View.GONE);
                                    linearLayoutSlideShow
                                            .setVisibility(View.VISIBLE);
                                    if (!isRunning) {
                                        alphaAnimation = new PausableScaleAnimation(
                                                0, 1, 0, 1);
                                        alphaAnimation
                                                .setDuration(IMAGE_ANIMATION_TIME);
                                        alphaAnimation.start();
                                        imageViewSlideShow
                                                .startAnimation(alphaAnimation);
                                        startTimeSlideShow = System
                                                .currentTimeMillis();
                                        startTimer(IMAGE_ANIMATION_TIME,
                                                NEXT_IMAGE);
                                    } else {
                                        alphaAnimation.resume();
                                        startTimeSlideShow = System
                                                .currentTimeMillis();
                                        startTimer(IMAGE_ANIMATION_TIME
                                                - timeFromPlayToPauseSlideShow,
                                                NEXT_IMAGE);
                                    }
                                    mFlagPlayPause = false;
                                } else {
                                    isRunning = true;
                                    long current = System.currentTimeMillis();
                                    timeFromPlayToPauseSlideShow += current
                                            - startTimeSlideShow;
                                    alphaAnimation.pause();
                                    mTimer.cancel();
                                    mTimer.purge();
                                    mTimer = null;
                                    mTimer = new Timer();
                                    mFlagPlayPause = true;
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_PROG_YELLOW:
                            case KeyEvent.KEYCODE_Y: {
                                if (zoomLevel < 4 && !slideShow) {
                                    zoomLevel++;
                                    int zoom = (int) Math.pow(2, zoomLevel);
                                    multimediaShowImage.setImageBitmap(mBitmap);
                                    multimediaShowImage
                                            .init(multimediaShowImage);
                                    multimediaShowImage.setScale(zoom);
                                    multimediaShowImage.scale();
                                    if (multimediaShowImage == null) {
                                        new A4TVToast(activity)
                                                .showToast(R.string.error_while_decoding_image);
                                    }
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_PROG_BLUE:
                            case KeyEvent.KEYCODE_B: {
                                if (zoomLevel > 0 && !slideShow) {
                                    zoomLevel--;
                                    int zoom = (int) Math.pow(2, zoomLevel);
                                    multimediaShowImage
                                            .init(multimediaShowImage);
                                    multimediaShowImage.setScale(zoom);
                                    multimediaShowImage.scale();
                                    if (multimediaShowImage == null) {
                                        new A4TVToast(activity)
                                                .showToast(R.string.error_while_decoding_image);
                                    }
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_PROG_GREEN:
                            case KeyEvent.KEYCODE_G: {
                                if (!slideShow) {
                                    degree -= 90;
                                    Bitmap b = rotateImage(mBitmap, degree);
                                    setImage(b);
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_PROG_RED:
                            case KeyEvent.KEYCODE_R: {
                                if (!slideShow) {
                                    degree += 90;
                                    Bitmap b = rotateImage(mBitmap, degree);
                                    setImage(b);
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_INFO:
                            case KeyEvent.KEYCODE_SPACE: {
                                if (!slideShow) {
                                    A4TVDialog dialogContext = ((MainActivity) activity)
                                            .getDialogManager()
                                            .getContextSmallDialog();
                                    if (dialogContext != null)
                                        dialogContext
                                                .setContentView(R.layout.image_info_dialog);
                                    if (exif != null && dialogContext != null) {
                                        LinearLayout linearLayoutImageInfo = (LinearLayout) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.linearLayoutImageInfo);
                                        TypedArray atts = activity
                                                .getTheme()
                                                .obtainStyledAttributes(
                                                        new int[] { R.attr.DialogContextBackground });
                                        int backgroundID = atts.getResourceId(
                                                0, 0);
                                        atts.recycle();
                                        linearLayoutImageInfo
                                                .setBackgroundResource(backgroundID);
                                        A4TVTextView textViewDateTimeImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewDateTimeImageInfo);
                                        A4TVTextView textViewMakeImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewMakeImageInfo);
                                        A4TVTextView textViewModelImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewModelImageInfo);
                                        A4TVTextView textViewFlashImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewFlashImageInfo);
                                        A4TVTextView textViewImageWidthImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewImageWidthImageInfo);
                                        A4TVTextView textViewImageHeightImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewImageHeightImageInfo);
                                        A4TVTextView textViewExposureTimeImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewExposureTimeImageInfo);
                                        A4TVTextView textViewApertureImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewApertureImageInfo);
                                        A4TVTextView textViewISOImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewISOImageInfo);
                                        A4TVTextView textViewWhiteBalanceImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewWhiteBalanceImageInfo);
                                        A4TVTextView textViewFocalLengthImageInfo = (A4TVTextView) dialogContext
                                                .findViewById(com.iwedia.gui.R.id.textViewFocalLengthImageInfo);
                                        String dateTime = extractExifData(exif,
                                                ExifInterface.TAG_DATETIME);
                                        dateTime = isNullExifData(dateTime);
                                        String make = extractExifData(exif,
                                                ExifInterface.TAG_MAKE);
                                        make = isNullExifData(make);
                                        String model = extractExifData(exif,
                                                ExifInterface.TAG_MODEL);
                                        model = isNullExifData(model);
                                        String flash = extractExifData(exif,
                                                ExifInterface.TAG_FLASH);
                                        flash = isNullExifData(flash);
                                        String imageWidth = extractExifData(
                                                exif,
                                                ExifInterface.TAG_IMAGE_WIDTH);
                                        imageWidth = isNullExifData(imageWidth);
                                        String imageHeight = extractExifData(
                                                exif,
                                                ExifInterface.TAG_IMAGE_LENGTH);
                                        imageHeight = isNullExifData(imageHeight);
                                        String exposureTime = extractExifData(
                                                exif,
                                                ExifInterface.TAG_EXPOSURE_TIME);
                                        exposureTime = isNullExifData(exposureTime);
                                        String aperture = extractExifData(exif,
                                                ExifInterface.TAG_APERTURE);
                                        aperture = isNullExifData(aperture);
                                        String iso = extractExifData(exif,
                                                ExifInterface.TAG_ISO);
                                        iso = isNullExifData(iso);
                                        String whiteBalance = extractExifData(
                                                exif,
                                                ExifInterface.TAG_WHITE_BALANCE);
                                        whiteBalance = isNullExifData(whiteBalance);
                                        String focalLength = extractExifData(
                                                exif,
                                                ExifInterface.TAG_FOCAL_LENGTH);
                                        focalLength = isNullExifData(focalLength);
                                        textViewDateTimeImageInfo
                                                .setText(" Date time: "
                                                        + dateTime);
                                        textViewMakeImageInfo.setText(" Make: "
                                                + make);
                                        textViewModelImageInfo
                                                .setText(" Model: " + model);
                                        textViewFlashImageInfo
                                                .setText(" Flash: " + flash);
                                        textViewImageWidthImageInfo
                                                .setText(" Image Width: "
                                                        + imageWidth);
                                        textViewImageHeightImageInfo
                                                .setText(" Image Height: "
                                                        + imageHeight);
                                        textViewExposureTimeImageInfo
                                                .setText(" Exposure Time: "
                                                        + exposureTime);
                                        textViewApertureImageInfo
                                                .setText(" Aperture: "
                                                        + aperture);
                                        textViewISOImageInfo.setText(" ISO: "
                                                + iso);
                                        textViewWhiteBalanceImageInfo
                                                .setText(" White Balance: "
                                                        + whiteBalance);
                                        textViewFocalLengthImageInfo
                                                .setText(" Focal Length: "
                                                        + focalLength);
                                        dialogContext.getWindow()
                                                .getAttributes().width = MainActivity.dialogWidth / 2;
                                        dialogContext.getWindow()
                                                .getAttributes().height = MainActivity.dialogHeight / 2;
                                        // show drop down dialog
                                        dialogContext.show();
                                    } else {
                                        A4TVToast toast = new A4TVToast(
                                                activity);
                                        toast.showToast(com.iwedia.gui.R.string.picture_has_not_more_details);
                                    }
                                }
                                return true;
                            }
                            case KeyEvent.KEYCODE_ENTER:
                            case KeyEvent.KEYCODE_DPAD_CENTER: {
                                if (!repeatSlideShow) {
                                    repeatSlideShow = true;
                                    imageViewRepeatSlideShow
                                            .setImageResource(R.drawable.media_controller_repeat_all_focused);
                                } else {
                                    repeatSlideShow = false;
                                    imageViewRepeatSlideShow
                                            .setImageResource(R.drawable.media_controller_repeat_off_focused);
                                }
                                return true;
                            }
                            // /////////////////////////////////////////////////////
                            // BACK
                            // //////////////////////////////////////////////////////
                            case KeyEvent.KEYCODE_BACK:
                            case KeyEvent.KEYCODE_DEL: {
                                mFlagPlayPause = true;
                                isRunning = false;
                                if (!slideShow) {
                                    multimediaShowDialog.cancel();
                                }
                                mTimer.cancel();
                                mTimer = null;
                                mTimer = new Timer();
                                multimediaShow.setVisibility(View.VISIBLE);
                                linearLayoutSlideShow.setVisibility(View.GONE);
                                slideShow = false;
                                return true;
                            }
                            default:
                                A4TVToast toast = new A4TVToast(activity);
                                toast.showToast(com.iwedia.gui.R.string.action_is_not_available);
                                return true;
                        }
                    } else {
                        switch (keyCode) {
                        // //////////////////////////////////////////////////////////////
                        // STOP
                        // //////////////////////////////////////////////////////////////
                            case KeyEvent.KEYCODE_MEDIA_STOP: {
                                // Stop Renderer
                                ((MainActivity) activity)
                                        .getRendererController().stop();
                                return true;
                            }
                            // /////////////////////////////////////////////////////
                            // INFO BANNER
                            // //////////////////////////////////////////////////////
                            case KeyEvent.KEYCODE_INFO: {
                                ((MainActivity) activity).getPageCurl().info();
                                return true;
                            }
                            default:
                                A4TVToast toast = new A4TVToast(activity);
                                toast.showToast(com.iwedia.gui.R.string.action_is_not_available);
                                return true;
                        }
                    }
                }
                return false;
            }
        });
        // Create progress dialog object
        progressDialog = new A4TVProgressDialog(activity);
        progressDialog.setTitleOfAlertDialog(R.string.loading_data);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(R.string.please_wait);
        // ////////////////////////////////
        // Take references of views
        // ////////////////////////////////
        // Image
        multimediaShowImage = (A4TVPhotoView) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.multimediaShowImage);
        // Image name
        multimediaShowImageName = (A4TVTextView) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.multimediaShowImageName);
        // Next button
        multimediaShowNextButton = (A4TVButton) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.multimedaShowNext);
        multimediaShowNextButton.setFocusable(false);
        multimediaShowNextButton
                .setOnClickListener(new MultimediaShowOnClick());
        // Previous button
        multimediaShowPreviousButton = (A4TVButton) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.multimedaShowPrevious);
        multimediaShowPreviousButton.setFocusable(false);
        multimediaShowPreviousButton
                .setOnClickListener(new MultimediaShowOnClick());
        imageViewSlideShow = (ImageView) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.imageViewSlideShow);
        multimediaShow = (LinearLayout) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.multimediaShow);
        linearLayoutSlideShow = (LinearLayout) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.linearLayoutSlideShow);
        imageViewRepeatSlideShow = (ImageView) multimediaShowDialog
                .findViewById(com.iwedia.gui.R.id.imageViewRepeatSlideShow);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEXT_IMAGE: {
                    if (slideShow) {
                        if (timeToHoldImageOnScreen != 0) {
                            startTimer(timeToHoldImageOnScreen, HOLD_IMAGE);
                        } else {
                            findNextImage();
                        }
                    }
                    break;
                }
                case HOLD_IMAGE: {
                    if (slideShow) {
                        findNextImage();
                    }
                    break;
                }
            }
        }
    };

    private void startTimer(final long milliseconds, final int what) {
        if (mTimer != null) {
            mTimer.purge();
            if (mStateTimerTask != null) {
                mStateTimerTask.cancel();
            }
            mStateTimerTask = null;
            mStateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(what);
                    }
                }
            };
            mTimer.schedule(mStateTimerTask, milliseconds);
        }
    }

    private String isNullExifData(String data) {
        if (data == null) {
            return " - ";
        }
        return data;
    }

    /** Set image */
    public void setImage(Bitmap bitmap) {
        multimediaShowImage.setImageBitmap(bitmap);
        if (bitmap == null) {
            new A4TVToast(activity)
                    .showToast(R.string.error_while_decoding_image);
        }
    }

    /** Set image name */
    public void setImageName(String name) {
        multimediaShowImageName.setText(name);
    }

    /** MultimediaShow onClick */
    private class MultimediaShowOnClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            // Take id of view
            int id = v.getId();
            // Number of items
            numberOfItems = MultimediaFileBrowserHandler
                    .getFileBrowserNumberOfItems();
            switch (id) {
            // /////////////////////////////
            // Next button
            // /////////////////////////////
                case com.iwedia.gui.R.id.multimedaShowNext:
                    // Load next image
                    if (!findNextImage()) {
                        A4TVToast toast = new A4TVToast(activity);
                        toast.showToast(R.string.dlna_playback_no_next_file);
                    }
                    break;
                // //////////////////////////////
                // Previous button
                // //////////////////////////////
                case com.iwedia.gui.R.id.multimedaShowPrevious: {
                    // Load previous image
                    if (!findPreviousImage()) {
                        A4TVToast toast = new A4TVToast(activity);
                        toast.showToast(R.string.dlna_playback_no_previous_file);
                    }
                    break;
                }
            }
        }
    }

    /** Find next image in folder */
    private boolean findNextImage() {
        Log.d(TAG, "FIND NEXT IMAGE INDEX" + indexOfLastImageShowed + " "
                + numberOfItems);
        // Find next image
        for (int i = indexOfLastImageShowed + 1; i < numberOfItems; i++) {
            try {
                // Get next content in folder
                final MultimediaContent nextContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                if (nextContent != null) {
                    // Check if file type exists
                    if (nextContent.getType() != null) {
                        // /////////////////////////////
                        // JPG PNG
                        // /////////////////////////////
                        if (EXTENSIONS_IMAGE.contains(nextContent
                                .getExtension().toLowerCase())) {
                            // ////////////////////////////
                            // Decode image
                            // ////////////////////////////
                            final String url = nextContent.getFileURL();
                            // /////////////////////////////////
                            // Image from local
                            // /////////////////////////////////
                            if (!url.startsWith("http:")) {
                                // Load image task
                                new LoadImageTask(nextContent, true).execute();
                            }
                            // /////////////////////////////
                            // Image from DLNA
                            // /////////////////////////////
                            else {
                                // Load image task
                                new LoadImageTask(nextContent, false).execute();
                            }
                            indexOfLastImageShowed = i;
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (slideShow) {
            if (repeatSlideShow) {
                indexOfLastImageShowed = 0;
                findNextImage();
            } else {
                mTimer.cancel();
                mTimer = null;
                mTimer = new Timer();
                multimediaShow.setVisibility(View.VISIBLE);
                linearLayoutSlideShow.setVisibility(View.GONE);
                slideShow = false;
            }
        }
        // No image found
        return false;
    }

    /** Find previous image in folder */
    private boolean findPreviousImage() {
        Log.d(TAG, "FIND PREVIOUS IMAGE " + indexOfLastImageShowed + " ");
        // Find next image
        for (int i = indexOfLastImageShowed - 1; i >= 0; i--) {
            Log.d(TAG, "FIND LOOP " + i);
            try {
                // Get next content in folder
                final MultimediaContent previousContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                Log.d(TAG, "PREVIOUS CONTENT " + previousContent.toString());
                // Check if file type exists
                if (previousContent.getType() != null) {
                    // /////////////////////////////
                    // JPG PNG
                    // /////////////////////////////
                    if (EXTENSIONS_IMAGE.contains(previousContent
                            .getExtension().toLowerCase())) {
                        // ////////////////////////////
                        // Decode image
                        // ////////////////////////////
                        final String url = previousContent.getFileURL();
                        // /////////////////////////////////
                        // Image from local
                        // /////////////////////////////////
                        if (!url.contains("http:")) {
                            Log.d(TAG, "START IMAGE DECODING");
                            new LoadImageTask(previousContent, true).execute();
                        }
                        // /////////////////////////////
                        // Image from DLNA
                        // /////////////////////////////
                        else {
                            new LoadImageTask(previousContent, false).execute();
                        }
                        indexOfLastImageShowed = i;
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // No image found
        return false;
    }

    /**
     * Get scaled bitmap
     * 
     * @param url
     * @param reqWidth
     * @return
     */
    private Bitmap decodeSampledBitmap(String url, int reqWidth) {
        if (url == null) {
            return null;
        }
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url, options);
    }

    /**
     * Check if image needs to be scaled down
     * 
     * @param options
     * @param reqWidth
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth) {
        int width = options.outWidth;
        int inSampleSize = 1;
        while (width > reqWidth) {
            width /= 2;
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    /** Start load image async task */
    public void startLoadImageTask(MultimediaContent content, boolean local_dlna) {
        new LoadImageTask(content, local_dlna).execute();
    }

    /** Async Task for loading data */
    private class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
        /** Content to load */
        private MultimediaContent content;
        /** Local or DLNA flag */
        private boolean local_dlna;

        public LoadImageTask(MultimediaContent content, boolean local_dlna) {
            super();
            this.content = content;
            this.local_dlna = local_dlna;
        }

        @Override
        protected void onPreExecute() {
            if (!slideShow) {
                progressDialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO: Applies on main display only
            final int displayId = 0;
            // ////////////////////////////
            // Local image
            // ////////////////////////////
            if (local_dlna) {
                Bitmap bitmap = decodeSampledBitmap(content.getFileURL(),
                        IMAGE_MAX_SIZE);
                // Bitmap bitmap =
                // BitmapFactory.decodeFile(content.getFileURL());
                if (!slideShow) {
                    exif = initExif(content);
                }
                try {
                    MainActivity.service.getContentListControl().goContent(
                            content, displayId);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return bitmap;
            } else {
                try {
                    HttpURLConnection connection;
                    connection = (HttpURLConnection) new URL(
                            content.getFileURL()).openConnection();
                    content.getFileURL();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    input.markSupported();
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    o.inPurgeable = true;
                    Log.d("GetBitmapFromUrl", "Options");
                    BitmapFactory.decodeStream(input, null, o);
                    input.close();
                    int scale = 1;
                    if (o.outHeight > IMAGE_MAX_SIZE
                            || o.outWidth > IMAGE_MAX_SIZE) {
                        scale = (int) Math.pow(
                                2,
                                (int) Math.round(Math.log(IMAGE_MAX_SIZE
                                        / (double) Math.max(o.outHeight,
                                                o.outWidth))
                                        / Math.log(0.5)));
                    }
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    o2.inPurgeable = true;
                    connection = (HttpURLConnection) new URL(
                            content.getFileURL()).openConnection();
                    // connection.setRequestProperty("User-agent",
                    // "Mozilla/4.0");
                    connection.connect();
                    input = connection.getInputStream();
                    Bitmap b = BitmapFactory.decodeStream(input, null, o2);
                    input.close();
                    connection.disconnect();
                    try {
                        MainActivity.service.getContentListControl().goContent(
                                content, displayId);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return b;
                } catch (Exception e) {
                    Log.d(TAG, "Problem!!!");
                    return null;
                }
            }
        }

        protected void onPostExecute(Bitmap b) {
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
                System.gc();
            }
            mBitmap = b;
            if (slideShow) {
                imageViewSlideShow.setImageBitmap(mBitmap);
                timeFromPlayToPauseSlideShow = 0;
                imageViewSlideShow.startAnimation(alphaAnimation);
                startTimeSlideShow = System.currentTimeMillis();
                startTimer(IMAGE_ANIMATION_TIME, NEXT_IMAGE);
            }
            ((MainActivity) activity).getMultimediaHandler()
                    .getMultimediaShowHandler().setImage(mBitmap);
            multimediaShowImage.init(multimediaShowImage);
            // Set image name
            ((MainActivity) activity).getMultimediaHandler()
                    .getMultimediaShowHandler().setImageName(content.getName());
            // Show multimedia show dialog
            ((MainActivity) activity).getMultimediaHandler()
                    .showMultimediaShow();
            if (!slideShow) {
                // Hide progress dialog
                progressDialog.dismiss();
            }
        }
    };

    /** Show navigation arrows */
    public void showNavigationArrows() {
        multimediaShowPreviousButton.setVisibility(View.VISIBLE);
        multimediaShowNextButton.setVisibility(View.VISIBLE);
    }

    /** Hide navigation arrows */
    public void hideNavigationArrows() {
        multimediaShowPreviousButton.setVisibility(View.INVISIBLE);
        multimediaShowNextButton.setVisibility(View.INVISIBLE);
    }

    /** Decode image file */
    public static Bitmap decodeFile(File f) {
        try {
            try {
                // decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                // Find the correct scale value. It should be the power of 2.
                final int REQUIRED_SIZE = 1024;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE
                            || height_tmp / 2 < REQUIRED_SIZE) {
                        break;
                    }
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale++;
                }
                // decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                InputStream is = new FileInputStream(f);
                Bitmap returnBitmap = BitmapFactory.decodeStream(is, null, o2);
                is.close();
                return returnBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            return null;
        }
        return null;
    }

    /** Scale bitmap */
    public static Bitmap scaleImage(Bitmap oldBitmap) {
        try {
            if (oldBitmap != null) {
                int oldBitmapWidth = oldBitmap.getWidth();
                int oldBitmapHeight = oldBitmap.getHeight();
                float oldBitmapAspectRatio;
                // Calculate original aspect ratio
                if (oldBitmapHeight > oldBitmapWidth) {
                    oldBitmapAspectRatio = (float) oldBitmapHeight
                            / (float) oldBitmapWidth;
                } else {
                    oldBitmapAspectRatio = (float) oldBitmapWidth
                            / (float) oldBitmapHeight;
                }
                int newBitmapWidth = 1;
                int newBitmapHeight = 1;
                // Check if image size is bigger that allowed
                if (oldBitmapWidth > IMAGE_MAX_SIZE
                        || oldBitmapHeight > IMAGE_MAX_SIZE) {
                    // And width and height is bigger that allowed
                    if (oldBitmapWidth > IMAGE_MAX_SIZE
                            && oldBitmapHeight > IMAGE_MAX_SIZE) {
                        if (oldBitmapWidth > oldBitmapHeight) {
                            newBitmapWidth = IMAGE_MAX_SIZE;
                            newBitmapHeight = (int) ((float) newBitmapWidth / (float) oldBitmapAspectRatio);
                        } else {
                            newBitmapHeight = IMAGE_MAX_SIZE;
                            newBitmapWidth = (int) ((float) newBitmapHeight / (float) oldBitmapAspectRatio);
                        }
                    }
                    // Width is bigger than allowed
                    if (oldBitmapWidth > IMAGE_MAX_SIZE
                            && oldBitmapHeight <= IMAGE_MAX_SIZE) {
                        newBitmapWidth = IMAGE_MAX_SIZE;
                        newBitmapHeight = (int) ((float) newBitmapWidth / (float) oldBitmapAspectRatio);
                    }
                    // Height is bigger than allowed
                    if (oldBitmapWidth <= IMAGE_MAX_SIZE
                            && oldBitmapHeight > IMAGE_MAX_SIZE) {
                        newBitmapHeight = IMAGE_MAX_SIZE;
                        newBitmapWidth = (int) ((float) newBitmapHeight / (float) oldBitmapAspectRatio);
                    }
                } else {
                    newBitmapWidth = oldBitmapWidth;
                    newBitmapHeight = oldBitmapHeight;
                }
                Bitmap newBitmap;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                newBitmap = Bitmap.createScaledBitmap(oldBitmap,
                        newBitmapWidth, newBitmapHeight, false);
                oldBitmap.recycle();
                System.gc();
                return newBitmap;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
            return null;
        }
        return null;
    }

    // ////////////////////////////////////////////////////////
    // Video
    // ////////////////////////////////////////////////////////
    /** Find next video in folder */
    public Content findNextVideo() {
        // Number of items
        numberOfItems = MultimediaFileBrowserHandler
                .getFileBrowserNumberOfItems();
        if ((indexOfLastAudioOrVideoShowed + 1) >= numberOfItems) {
            indexOfLastAudioOrVideoShowed = -1;
        }
        // Find next video
        for (int i = indexOfLastAudioOrVideoShowed + 1; i < numberOfItems; i++) {
            try {
                // Get next content in folder
                final MultimediaContent nextContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                if (nextContent != null) {
                    // Check if file type exists
                    if (nextContent.getType() != null) {
                        // /////////////////////////////
                        // Video file
                        // /////////////////////////////
                        /** Get Mime or Extension */
                        String extension;
                        if (nextContent.getMime() != null) {
                            extension = nextContent.getMime();
                        } else {
                            extension = nextContent.getExtension();
                        }
                        if (EXTENSIONS_VIDEO.contains(extension.toLowerCase())) {
                            indexOfLastAudioOrVideoShowed = i;
                            return nextContent;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // No video found
        return null;
    }

    /** Find previous video in folder */
    public Content findPreviousVideo() {
        // Number of items
        numberOfItems = MultimediaFileBrowserHandler
                .getFileBrowserNumberOfItems();
        if ((indexOfLastAudioOrVideoShowed - 1) < 0) {
            indexOfLastAudioOrVideoShowed = numberOfItems;
        }
        // Find next video
        for (int i = indexOfLastAudioOrVideoShowed - 1; i >= 0; i--) {
            try {
                // Get next content in folder
                final MultimediaContent nextContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                if (nextContent != null) {
                    // Check if file type exists
                    if (nextContent.getType() != null) {
                        // /////////////////////////////
                        // Video file
                        // /////////////////////////////
                        /** Get Mime or Extension */
                        String extension;
                        if (nextContent.getMime() != null) {
                            extension = nextContent.getMime();
                        } else {
                            extension = nextContent.getExtension();
                        }
                        if (EXTENSIONS_VIDEO.contains(extension.toLowerCase())) {
                            indexOfLastAudioOrVideoShowed = i;
                            return nextContent;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // No video found
        return null;
    }

    // ////////////////////////////////////////////////////////
    // Audio
    // ////////////////////////////////////////////////////////
    /** Find next audio in folder */
    public Content findNextAudio() {
        // Number of items
        numberOfItems = MultimediaFileBrowserHandler
                .getFileBrowserNumberOfItems();
        if ((indexOfLastAudioOrVideoShowed + 1) >= numberOfItems) {
            indexOfLastAudioOrVideoShowed = -1;
        }
        // Find next audio
        for (int i = indexOfLastAudioOrVideoShowed + 1; i < numberOfItems; i++) {
            try {
                // Get next content in folder
                final MultimediaContent nextContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                if (nextContent != null) {
                    // Check if file type exists
                    if (nextContent.getType() != null) {
                        // /////////////////////////////
                        // Audio file
                        // /////////////////////////////
                        /** Get Mime or Extension */
                        String extension;
                        if (nextContent.getMime() != null) {
                            extension = nextContent.getMime();
                        } else {
                            extension = nextContent.getExtension();
                        }
                        if (EXTENSIONS_AUDIO.contains(extension.toLowerCase())) {
                            indexOfLastAudioOrVideoShowed = i;
                            return nextContent;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // No audio found
        return null;
    }

    /** Find previous audio in folder */
    public Content findPreviousAudio() {
        // Number of items
        numberOfItems = MultimediaFileBrowserHandler
                .getFileBrowserNumberOfItems();
        if ((indexOfLastAudioOrVideoShowed - 1) < 0) {
            indexOfLastAudioOrVideoShowed = numberOfItems;
        }
        // Find next audio
        for (int i = indexOfLastAudioOrVideoShowed - 1; i >= 0; i--) {
            try {
                // Get next content in folder
                final MultimediaContent nextContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                if (nextContent != null) {
                    // Check if file type exists
                    if (nextContent.getType() != null) {
                        // /////////////////////////////
                        // Audio file
                        // /////////////////////////////
                        String extension;
                        if (nextContent.getMime() != null) {
                            extension = nextContent.getMime();
                        } else {
                            extension = nextContent.getExtension();
                        }
                        if (EXTENSIONS_AUDIO.contains(extension.toLowerCase())) {
                            indexOfLastAudioOrVideoShowed = i;
                            return nextContent;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // No audio found
        return null;
    }

    // ////////////////////////////////////////////////////////
    // PVR
    // ////////////////////////////////////////////////////////
    /** Find next pvr file in folder */
    public Content findNextPvr() {
        // Number of items
        numberOfItems = MultimediaFileBrowserHandler
                .getFileBrowserNumberOfItems();
        if ((indexOfLastPvrFileShowed + 1) >= numberOfItems) {
            indexOfLastPvrFileShowed = -1;
        }
        // Find next video
        for (int i = indexOfLastPvrFileShowed + 1; i < numberOfItems; i++) {
            try {
                // Get next content in folder
                final MultimediaContent nextContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                if (nextContent != null) {
                    indexOfLastPvrFileShowed = i;
                    return nextContent;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // No pvr file found
        return null;
    }

    /** Find previous pvr file in folder */
    public Content findPreviousPvr() {
        // Number of items
        numberOfItems = MultimediaFileBrowserHandler
                .getFileBrowserNumberOfItems();
        if ((indexOfLastPvrFileShowed - 1) < 0) {
            indexOfLastPvrFileShowed = numberOfItems;
        }
        // Find next video
        for (int i = indexOfLastPvrFileShowed - 1; i >= 0; i--) {
            try {
                // Get next content in folder
                final MultimediaContent nextContent = (MultimediaContent) MainActivity.service
                        .getContentListControl().getContent(i);
                if (nextContent != null) {
                    indexOfLastPvrFileShowed = i;
                    return nextContent;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // No pvr found
        return null;
    }

    // /////////////////////////////////////
    // Getters and setters
    // /////////////////////////////////////
    public int getIndexOfLastImageShowed() {
        return indexOfLastImageShowed;
    }

    public void setIndexOfLastImageShowed(int indexOfLastImageShowed) {
        this.indexOfLastImageShowed = indexOfLastImageShowed;
    }

    public int getIndexOfLastAudioOrVideoShowed() {
        return indexOfLastAudioOrVideoShowed;
    }

    public void setIndexOfLastAudioOrVideoShowed(
            int indexOfLastAudioOrVideoShowed) {
        this.indexOfLastAudioOrVideoShowed = indexOfLastAudioOrVideoShowed;
    }

    public int getIndexOfLastPvrFileShowed() {
        return indexOfLastPvrFileShowed;
    }

    public void setIndexOfLastPvrFileShowed(int indexOfLastPvrFileShowed) {
        this.indexOfLastPvrFileShowed = indexOfLastPvrFileShowed;
    }

    public static void setShowNavigationArrows(boolean showNavigationArrows) {
        if (showNavigationArrows) {
            // Show navigation arrows
            multimediaShowPreviousButton.setVisibility(View.VISIBLE);
            multimediaShowNextButton.setVisibility(View.VISIBLE);
        } else {
            // Hide navigation arrows
            multimediaShowPreviousButton.setVisibility(View.INVISIBLE);
            multimediaShowNextButton.setVisibility(View.INVISIBLE);
        }
        MultimediaShowHandler.showNavigationArrows = showNavigationArrows;
    }

    public static ExifInterface initExif(MultimediaContent Content) {
        Log.d("image", "Try to get Exif data: " + Content.getFileURL() + " ("
                + Content.getExtension() + ")");
        if (!Content.getExtension().equalsIgnoreCase("jpg")
                && !Content.getExtension().equalsIgnoreCase("jpeg")) {
            return null;
        }
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(Content.getFileURL());
        } catch (IOException ex) {
            Log.e("image", "cannot read exif", ex);
        }
        return exif;
    }

    public static String extractExifData(ExifInterface exif, String tag) {
        String value = null;
        if (tag.equals(ExifInterface.TAG_DATETIME)) {
            int degree = 0;
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
            value = Integer.toString(degree);
            Log.d(TAG, "Orientation: " + value);
        } else if (tag.equals(ExifInterface.TAG_DATETIME)) {
            value = exif.getAttribute(ExifInterface.TAG_DATETIME);
            Log.d(TAG, "Date time: " + value);
        } else if (tag.equals(ExifInterface.TAG_MAKE)) {
            value = exif.getAttribute(ExifInterface.TAG_MAKE);
            Log.d(TAG, "Maker: " + value);
        } else if (tag.equals(ExifInterface.TAG_MODEL)) {
            value = exif.getAttribute(ExifInterface.TAG_MODEL);
            Log.d(TAG, "Model: " + value);
        } else if (tag.equals(ExifInterface.TAG_FLASH)) {
            value = Integer.toString(exif.getAttributeInt(
                    ExifInterface.TAG_FLASH, -1));
            Log.d(TAG, "Flash: " + value);
        } else if (tag.equals(ExifInterface.TAG_IMAGE_WIDTH)) {
            value = Integer.toString(exif.getAttributeInt(
                    ExifInterface.TAG_IMAGE_WIDTH, -1));
            Log.d(TAG, "Width: " + value);
        } else if (tag.equals(ExifInterface.TAG_IMAGE_LENGTH)) {
            value = Integer.toString(exif.getAttributeInt(
                    ExifInterface.TAG_IMAGE_LENGTH, -1));
            Log.d(TAG, "Length: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_LATITUDE)) {
            value = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            Log.d(TAG, "GPS Latitude: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_LONGITUDE)) {
            value = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            Log.d(TAG, "GPS Longitude: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_LATITUDE_REF)) {
            value = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            Log.d(TAG, "GPS Latitude ref: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_LONGITUDE_REF)) {
            value = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            Log.d(TAG, "GPS Longitude ref: " + value);
        } else if (tag.equals(ExifInterface.TAG_EXPOSURE_TIME)) {
            value = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            Log.d(TAG, "Exposure: " + value);
        } else if (tag.equals(ExifInterface.TAG_APERTURE)) {
            value = exif.getAttribute(ExifInterface.TAG_APERTURE);
            Log.d(TAG, "Aperture: " + value);
        } else if (tag.equals(ExifInterface.TAG_ISO)) {
            value = exif.getAttribute(ExifInterface.TAG_ISO);
            Log.d(TAG, "ISO: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_ALTITUDE)) {
            value = String.valueOf(exif.getAttributeDouble(
                    ExifInterface.TAG_GPS_ALTITUDE, 0));
            Log.d(TAG, "GPS Altitude: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_ALTITUDE_REF)) {
            value = Integer.toString(exif.getAttributeInt(
                    ExifInterface.TAG_GPS_ALTITUDE_REF, -1));
            Log.d(TAG, "GPS Altitude ref: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_TIMESTAMP)) {
            value = exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            Log.d(TAG, "GPS Timestamp: " + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_DATESTAMP)) {
            value = exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);
            Log.d(TAG, "GPS Datestamp: " + value);
        } else if (tag.equals(ExifInterface.TAG_WHITE_BALANCE)) {
            value = Integer.toString(exif.getAttributeInt(
                    ExifInterface.TAG_WHITE_BALANCE, -1));
            Log.d(TAG, "White balance: " + value);
        } else if (tag.equals(ExifInterface.TAG_FOCAL_LENGTH)) {
            value = String.valueOf(exif.getAttributeDouble(
                    ExifInterface.TAG_FOCAL_LENGTH, 0));
            Log.d(TAG, "Focal length: f/" + value);
        } else if (tag.equals(ExifInterface.TAG_GPS_PROCESSING_METHOD)) {
            value = exif.getAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD);
            Log.d(TAG, "GPS processing method: " + value);
        }
        return value;
    }

    private Bitmap rotateImage(Bitmap oldBitmap, float degree) {
        // Create object of new Matrix.
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        // Create bitmap with new values.
        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0,
                oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, true);
        return newBitmap;
    }

    public class PausableScaleAnimation extends ScaleAnimation {
        private long mElapsedAtPause = 0;
        private boolean mPaused = false;

        public PausableScaleAnimation(float fromX, float toX, float fromY,
                float toY) {
            super(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
        }

        @Override
        public boolean getTransformation(long currentTime,
                Transformation outTransformation) {
            if (mPaused && mElapsedAtPause == 0) {
                mElapsedAtPause = currentTime - getStartTime();
            }
            if (mPaused) {
                setStartTime(currentTime - mElapsedAtPause);
            }
            return super.getTransformation(currentTime, outTransformation);
        }

        public void pause() {
            mElapsedAtPause = 0;
            mPaused = true;
        }

        public void resume() {
            mPaused = false;
        }
    }
}
