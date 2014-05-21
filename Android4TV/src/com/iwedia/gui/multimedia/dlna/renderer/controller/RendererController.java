package com.iwedia.gui.multimedia.dlna.renderer.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaGlobal;
import com.iwedia.gui.multimedia.MultimediaHandler;
import com.iwedia.gui.multimedia.MultimediaShowHandler;
import com.iwedia.gui.multimedia.controller.MediaController;
import com.iwedia.gui.osd.CheckServiceType;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Handle renderer events
 * 
 * @author Milos Milanovic
 */
public class RendererController implements MultimediaGlobal, OSDGlobal {
    private final String TAG = "RendererController";
    private static final int ASYNC_PLAY = 0;
    private static final int ASYNC_STOP = 1;
    private Activity mActivity = null;
    private MediaController mMediaController = null;
    private MultimediaHandler mMultimediaHandler = null;
    /** Progress dialog for loading data from DLNA controller */
    private A4TVProgressDialog mProgressDialog = null;
    private WaitForPlayback asyncWaitForPlayback = null;
    private static boolean flagOnCompletition = false;
    private boolean flagWaitForPlayback = false;
    private int mRendererState = RENDERER_STATE_STOP;
    private String mFilePath = "";
    private String mFriendlyName = "";
    private String mMime = "";
    private A4TVDialog dialogContext;
    private CheckServiceType checkServiceType;

    public RendererController(Activity activity, MediaController mediaController) {
        this.mActivity = activity;
        this.mMediaController = mediaController;
        initProgressDialog();
    }

    public void play(final String filePath, final String friendlyName,
            final String mime) {
        this.mFilePath = filePath;
        this.mFriendlyName = friendlyName;
        this.mMime = mime;
        flagOnCompletition = false;
        Log.d(TAG, "EXTENSIONS_VIDEO: " + EXTENSIONS_VIDEO.contains(mime));
        if (EXTENSIONS_VIDEO.contains(mime)) {
            showDialog(true);
        } else {
            showDialog(false);
        }
    }

    public boolean pause() {
        if (mRendererState == RENDERER_STATE_PLAY) {
            Log.d(TAG, "Pause -> MAIN");
            try {
                mMediaController.pause();
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 2,
                        "");
                // SetCurHandler State
                OSDHandlerHelper
                        .setHandlerState(CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER);
                A4TVMultimediaController
                        .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_PLAY);
                ControlProvider.setFlagPlay(false);
                ControlProvider.setFileDescription("Renderer Pause");
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
            Log.d(TAG, "Pause -> PIP");
            try {
                mMediaController.pause();
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 2,
                        "");
                return true;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (mRendererState == RENDERER_STATE_PLAY_PAP) {
            try {
                mMediaController.pause();
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 2,
                        "");
                return true;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean stop() {
        if (mRendererState == RENDERER_STATE_PLAY) {
            // SetCurHandler State
            A4TVMultimediaController
                    .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_STOP);
            ControlProvider.setFlagPlay(false);
            ControlProvider.setFileDescription("Renderer Stop");
            OSDHandlerHelper.setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
            // Set Key Listener State
            MainKeyListener.setAppState(MainKeyListener.CLEAN_SCREEN);
            if (EXTENSIONS_IMAGE.contains(mMime)) {
                try {
                    MainActivity.service.getDlnaControl().notifyDlnaRenderer(0,
                            0, "");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (EXTENSIONS_AUDIO.contains(mMime)) {
                startAsyncTask(ASYNC_STOP);
            } else if (EXTENSIONS_VIDEO.contains(mMime)) {
                startAsyncTask(ASYNC_STOP);
            }
            mHandler.sendEmptyMessage(RENDERER_STOP);
            mMediaController.stop(0);
            mMediaController.startLiveStream(true);
            // Set Renderer State
            mRendererState = RENDERER_STATE_STOP;
            return true;
        } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
            if (EXTENSIONS_VIDEO.contains(mMime)) {
                startAsyncTask(ASYNC_STOP);
            }
            mMediaController.stop(1);
            mRendererState = RENDERER_STATE_STOP;
            return true;
        } else if (mRendererState == RENDERER_STATE_PLAY_PAP) {
            if (EXTENSIONS_VIDEO.contains(mMime)) {
                startAsyncTask(ASYNC_STOP);
            }
            mMediaController.stop(2);
            mRendererState = RENDERER_STATE_STOP;
            return true;
        }
        return false;
    }

    public boolean resume() {
        if (mRendererState == RENDERER_STATE_PLAY) {
            Log.d(TAG, "Resume -> MAIN");
            try {
                mMediaController.resume();
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 1,
                        "");
                // SetCurHandler State
                OSDHandlerHelper
                        .setHandlerState(CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER);
                A4TVMultimediaController
                        .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_PLAY);
                ControlProvider.setFlagPlay(true);
                ControlProvider.setFileDescription((mActivity)
                        .getApplicationContext().getString(
                                R.string.dlna_renderer_play));
                // Show Info()
                ((MainActivity) mActivity).getPageCurl().multimediaController(
                        false);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "NotifyDlnaRenderer", e);
            }
        } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
            Log.d(TAG, "Resume -> PIP");
            try {
                mMediaController.resume();
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 1,
                        "");
                return true;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (mRendererState == RENDERER_STATE_PLAY_PAP) {
            Log.d(TAG, "Resume -> PIP");
            try {
                mMediaController.resume();
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 1,
                        "");
                return true;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    // milliseconds
    public boolean seekTo(int milliseconds) {
        if (mRendererState == RENDERER_STATE_PLAY) {
            try {
                mMediaController.seekTo(milliseconds);
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 1,
                        "");
                // SetCurHandler State
                OSDHandlerHelper
                        .setHandlerState(CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER);
                A4TVMultimediaController
                        .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_FF_NEXT);
                ControlProvider.setFlagPlay(true);
                // Show Info()
                ((MainActivity) mActivity).getPageCurl().multimediaController(
                        false);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "NotifyDlnaRenderer", e);
            }
        } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
            try {
                mMediaController.seekTo(milliseconds);
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 1,
                        "");
                return true;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (mRendererState == RENDERER_STATE_PLAY_PAP) {
            try {
                mMediaController.seekTo(milliseconds);
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 1,
                        "");
                return true;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean setElapsedTime() {
        String sTime;
        int iTime = 0;
        if (mRendererState == RENDERER_STATE_PLAY) {
            iTime = mMediaController.getElapsedTime() / 1000;
        } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
            iTime = mMediaController.getElapsedTime() / 1000;
        } else if (mRendererState == RENDERER_STATE_PLAY_PAP) {
            iTime = mMediaController.getElapsedTime() / 1000;
        }
        sTime = String.format("%02d:%02d:%02d", (iTime / 3600) % 24,
                (iTime / 60) % 60, iTime % 60);
        Log.i(TAG, "Time is: " + sTime);
        try {
            MainActivity.service.getDlnaControl().notifyDlnaRenderer(3, 0,
                    sTime);
        } catch (Exception e) {
            Log.e(TAG, "NotifyDlnaRenderer", e);
            return false;
        }
        return true;
    }

    public boolean onCompletion() {
        if (mRendererState == RENDERER_STATE_PLAY) {
            try {
                // SetCurHandler State
                OSDHandlerHelper.setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
                A4TVMultimediaController
                        .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_STOP);
                ControlProvider.setFlagPlay(false);
                ControlProvider.setFileDescription("Renderer Stop");
                mHandler.sendEmptyMessage(RENDERER_STOP);
                mMediaController.stop(0);
                mMediaController.startLiveStream(true);
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 0,
                        "");
                // Set Renderer State
                mRendererState = RENDERER_STATE_STOP;
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
            try {
                // MainActivity.activity.getPiPView().stop();
                MainActivity.service.getDlnaControl().notifyDlnaRenderer(0, 0,
                        "");
                // Set Renderer State
                mRendererState = RENDERER_STATE_STOP;
                return true;
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        setOnCompletition(true);
        return false;
    }

    // Work with UI
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            initMultimediaHandler();
            // Play Video
            if (msg.what == RENDERER_VIDEO) {
                // Hide all if it is video
                mMultimediaHandler.getMusicFromDlnaLayout().setVisibility(
                        View.GONE);
                // Hide other layouts
                (mActivity).findViewById(R.id.linLayMessages).setVisibility(
                        View.GONE);
                // Play Audio
            } else if (msg.what == RENDERER_AUDIO) {
                // Show music icon
                mMultimediaHandler.getMusicFromDlnaLayout().setVisibility(
                        View.VISIBLE);
                // Hide other layouts
                (mActivity).findViewById(R.id.linLayMessages).setVisibility(
                        View.GONE);
                // Play Image
            } else if (msg.what == RENDERER_IMAGE) {
                new LoadImageTask(mFilePath, mFriendlyName).execute();
                // Stop
            } else if (msg.what == RENDERER_STOP) {
                // Hide music from dlna message
                (mActivity).findViewById(
                        com.iwedia.gui.R.id.musicReproductionFromDlnaLayout)
                        .setVisibility(View.GONE);
                // Hide multimedia show dialog
                mMultimediaHandler.closeMultimediaShow();
                // try {
                // when dlna turn off check for service type
                // IServiceListControl servControl = MainActivity.service
                // .getServiceListControl();
                // just init service type
                // checkServiceType = new CheckServiceType((MainActivity)
                // mActivity);
                //
                // int currentActiveIndex = servControl.getServiceList(0)
                // .getActiveServiceIndex();
                //
                // int activeSignalType = servControl
                // .getServiceList(0)
                // .getServiceFromServiceList(0, currentActiveIndex,
                // false).getDigitalSourceType();
                // int activeServiceType = servControl
                // .getServiceList(0)
                // .getServiceFromServiceList(0, currentActiveIndex,
                // false).getServiceType();
                // if (servControl.getServiceList(0).getServicesNumber(
                // activeSignalType, activeServiceType) > 0) {
                // CheckServiceType
                // .checkService(
                // servControl
                // .getServiceList(0)
                // .getService(
                // servControl
                // .getServiceList(
                // 0)
                // .getActiveServiceIndex()),
                // true);
                // }
                // } catch (RemoteException e) {
                // e.printStackTrace();
                // }
            }
        }
    };

    private void initProgressDialog() {
        // Create progress dialog for loading data from DLNA controller
        mProgressDialog = new A4TVProgressDialog(mActivity);
        mProgressDialog.setTitleOfAlertDialog(R.string.loading_data);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(R.string.please_wait);
    }

    /** Init Multimedia Handler */
    private void initMultimediaHandler() {
        if (mMultimediaHandler == null) {
            ((MainActivity) mActivity).initMultimediaHandler();
            mMultimediaHandler = ((MainActivity) mActivity)
                    .getMultimediaHandler();
        }
    }

    /** Async Task for loading data */
    private class LoadImageTask extends AsyncTask<Void, Void, Boolean> {
        /** Image url */
        private String imageUrl;
        private String imageName;
        private Bitmap multimediaImage;

        public LoadImageTask(String imageUrl, String imageName) {
            super();
            this.imageUrl = imageUrl;
            this.imageName = imageName;
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // /////////////////////////////
            // DLNA image
            // /////////////////////////////
            InputStream is = null;
            try {
                is = new URL(imageUrl).openConnection().getInputStream();
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            // Decode image and scale it
            if (is != null) {
                multimediaImage = MultimediaShowHandler
                        .scaleImage(BitmapFactory.decodeStream(is));
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Boolean result) {
            initMultimediaHandler();
            // Set image
            mMultimediaHandler.getMultimediaShowHandler().setImage(
                    multimediaImage);
            // Set image name
            mMultimediaHandler.getMultimediaShowHandler().setImageName(
                    imageName);
            // Show multimedia show dialog
            mMultimediaHandler.showMultimediaShow();
            // Hide navigation buttons
            mMultimediaHandler.getMultimediaShowHandler()
                    .hideNavigationArrows();
            // Hide progress dialog
            mProgressDialog.dismiss();
        }
    };

    public int getmRendererState() {
        return mRendererState;
    }

    public boolean setmRendererState(int state) {
        mRendererState = state;
        return true;
    }

    /**
     * Get Extension From FilePath
     * 
     * @param filePath
     *        - File Path or URL
     * @return Extension
     */
    // private String getExtension(String filePath) {
    // int index = filePath.lastIndexOf('.');
    // if (index == -1 || (index == (filePath.length() - 1))) {
    // return "";
    // } else {
    // if (filePath.substring(index + 1).toLowerCase().contains("?"))
    // return filePath.substring(index + 1, filePath.indexOf('?'));
    // else
    // return filePath.substring(index + 1).toLowerCase();
    // }
    //
    // }
    private void startAsyncTask(int flag) {
        if (asyncWaitForPlayback != null) {
            WaitForPlayback tmpWaitForPlayback = asyncWaitForPlayback;
            asyncWaitForPlayback = null;
            tmpWaitForPlayback.cancel(true);
        }
        asyncWaitForPlayback = new WaitForPlayback(flag);
        asyncWaitForPlayback.execute(null, null, null);
    }

    /** Async Task for playback start */
    private class WaitForPlayback extends AsyncTask<Void, Void, Void> {
        private int flagPlay = -1;

        public WaitForPlayback(int flag) {
            this.flagPlay = flag;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (flagPlay == ASYNC_PLAY) {
                if (mRendererState == RENDERER_STATE_PLAY) {
                    while (!mMediaController.isPlaying()) {
                        // Log.i("DoInBack", "WhileLoop: isPlaying = false");
                        if (flagOnCompletition) {
                            flagPlay = ASYNC_STOP;
                            return null;
                        }
                    }
                } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
                    while (!MainActivity.activity
                            .getPrimaryMultimediaVideoView().isPlaying()) {
                        // Log.i("DoInBack", "WhileLoop: isPlaying = false");
                        if (flagOnCompletition) {
                            flagPlay = ASYNC_STOP;
                            return null;
                        }
                    }
                } else if (mRendererState == RENDERER_STATE_PLAY_PAP) {
                    while (!MainActivity.activity
                            .getPrimaryMultimediaVideoView().isPlaying()) {
                        // Log.i("DoInBack", "WhileLoop: isPlaying = false");
                        if (flagOnCompletition) {
                            flagPlay = ASYNC_STOP;
                            return null;
                        }
                    }
                }
            } else if (flagPlay == ASYNC_STOP) {
                if (mRendererState == RENDERER_STATE_PLAY) {
                    while (mMediaController.isPlaying()) {
                        // Log.i("DoInBack",
                        // "WhileLoop: isPlaying = true RENDERER_STATE_PLAY");
                    }
                } else if (mRendererState == RENDERER_STATE_PLAY_PIP) {
                    while (MainActivity.activity
                            .getPrimaryMultimediaVideoView().isPlaying()) {
                        // Log.i("DoInBack",
                        // "WhileLoop: isPlaying = true RENDERER_STATE_PLAY_PIP");
                    }
                } else if (mRendererState == RENDERER_STATE_PLAY_PAP) {
                    while (MainActivity.activity
                            .getPrimaryMultimediaVideoView().isPlaying()) {
                        // Log.i("DoInBack",
                        // "WhileLoop: isPlaying = true RENDERER_STATE_PLAY_PIP");
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (flagPlay == ASYNC_PLAY) {
                    MainActivity.service.getDlnaControl().notifyDlnaRenderer(0,
                            1, "");
                } else if (flagPlay == ASYNC_STOP) {
                    MainActivity.service.getDlnaControl().notifyDlnaRenderer(0,
                            0, "");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    private Handler mDialogHandler = new Handler();

    public void showDialog(final boolean isVideo) {
        mDialogHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogContext = MainActivity.activity.getDialogManager()
                        .getContextSmallDialog();
                // Show dialog for adding in favorite list
                // fill dialog with desired view
                if (dialogContext != null) {
                    dialogContext
                            .setContentView(fillDialogWithElements(isVideo));
                    dialogContext
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    try {
                                        MainActivity.service.getDlnaControl()
                                                .notifyDlnaRenderer(0, 0, "");
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                    // Close context dialog
                                    dialogContext.cancel();
                                }
                            });
                    // set dialog size
                    dialogContext.getWindow().getAttributes().width = MainActivity.dialogWidth / 2;
                    dialogContext.getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
                    // show drop down dialog
                    dialogContext.show();
                }
            }
        }, 100);
    }

    /**
     * Creates view for context dialog
     * 
     * @return
     */
    private View fillDialogWithElements(boolean isVideo) {
        LinearLayout mainLinLayout = new LinearLayout(mActivity);
        mainLinLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mainLinLayout.setOrientation(LinearLayout.VERTICAL);
        // get drawable from theme for image source
        TypedArray atts = mActivity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogContextBackground });
        int backgroundID = atts.getResourceId(0, 0);
        atts.recycle();
        mainLinLayout.setBackgroundResource(backgroundID);
        // layout of dialog title
        LinearLayout titleLinearLayout = new LinearLayout(mActivity);
        titleLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        titleLinearLayout.setOrientation(LinearLayout.VERTICAL);
        titleLinearLayout.setPadding(
                (int) mActivity.getResources().getDimension(
                        R.dimen.a4tvdialog_padding_left),
                (int) mActivity.getResources().getDimension(
                        R.dimen.a4tvdialog_spinner_padding_top), 0, 0);
        A4TVTextView text = new A4TVTextView(mActivity, null);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        /** Set text */
        text.setText("Play file " + mFriendlyName + "?");
        text.setTextSize(mActivity.getResources().getDimension(
                R.dimen.a4tvdialog_textview_size));
        // add title
        titleLinearLayout.addView(text);
        // add title layout to main layout
        mainLinLayout.addView(titleLinearLayout);
        // create horizontal line
        ImageView horizLine = new ImageView(mActivity);
        horizLine.setLayoutParams(new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        // get drawable from theme for image source
        atts = mActivity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        backgroundID = atts.getResourceId(0, 0);
        horizLine.setBackgroundResource(backgroundID);
        // add horiz line to main layout
        mainLinLayout.addView(horizLine);
        // create scroll view
        ScrollView mainScrollView = new ScrollView(mActivity);
        mainScrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // add scrollview to main view
        mainLinLayout.addView(mainScrollView);
        LinearLayout contentLinearLayout = new LinearLayout(mActivity);
        contentLinearLayout.setLayoutParams(new ScrollView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        contentLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        // add content layout to scroll view
        mainScrollView.addView(contentLinearLayout);
        /** GET FIELDS FOR CREATING DROP DOWN ITEMS */
        String[] strings;
        if (isVideo) {
            strings = mActivity.getResources().getStringArray(
                    R.array.renderer_video_play_mode_dropdown);
        } else {
            strings = mActivity.getResources().getStringArray(
                    R.array.renderer_play_mode_dropdown);
        }
        for (int i = 0; i < strings.length; i++) {
            // create small layout
            final LinearLayout smallLayoutHorizontal = new LinearLayout(
                    mActivity);
            smallLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            smallLayoutHorizontal
                    .setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            MainActivity.dialogListElementHeight));
            smallLayoutHorizontal.setPadding(15, 5, 15, 5);
            smallLayoutHorizontal.setGravity(Gravity.CENTER_VERTICAL);
            // create drop box item
            A4TVButton button = new A4TVButton(mActivity, null);
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            button.setText(strings[i]);
            button.setGravity(Gravity.CENTER);
            button.setId(i);
            // for creating difference between first buttons
            button.setTag(strings[i]);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View vi) {
                    // ////////////////////////////////////////////////////////////////////////////
                    // do something from dropdown items
                    // ////////////////////////////////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            mActivity.getResources().getString(
                                    R.string.play_in_full_screen))
                            || vi.getTag().equals(
                                    mActivity.getResources().getString(
                                            R.string.play))) {
                        // ///////////////////////////////////
                        // Play file in full screen
                        // ///////////////////////////////////
                        Log.d(TAG, "Play file in full screen");
                        // Close context dialog
                        dialogContext.cancel();
                        if (mFilePath.length() > 1) {
                            // Stop
                            mMediaController.stopLiveStream();
                            mMediaController.stop(0);
                            mHandler.sendEmptyMessage(RENDERER_STOP);
                            if (EXTENSIONS_IMAGE.contains(mMime)) {
                                mHandler.sendEmptyMessage(RENDERER_IMAGE);
                                try {
                                    MainActivity.service.getDlnaControl()
                                            .notifyDlnaRenderer(0, 1, "");
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            } else if (EXTENSIONS_AUDIO.contains(mMime)) {
                                mMediaController.play(mFilePath, 0);
                                mHandler.sendEmptyMessage(RENDERER_AUDIO);
                                startAsyncTask(ASYNC_PLAY);
                            } else if (EXTENSIONS_VIDEO.contains(mMime)) {
                                mMediaController.play(mFilePath, 0);
                                mHandler.sendEmptyMessage(RENDERER_VIDEO);
                                startAsyncTask(ASYNC_PLAY);
                                Log.i(TAG, "Play Video!");
                            } else {
                                Log.i(TAG, "Wrong Extension");
                                try {
                                    MainActivity.service.getDlnaControl()
                                            .notifyDlnaRenderer(0, 0, "");
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                mHandler.sendEmptyMessage(RENDERER_STOP);
                                mMediaController.stop(0);
                                mMediaController.startLiveStream(true);
                                // Cancel alert dialog
                                dialogContext.cancel();
                                Toast.makeText(mActivity,
                                        R.string.dlna_renderer_wrong_extension,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Set Renderer State
                            mRendererState = RENDERER_STATE_PLAY;
                            // SetCurHandler State
                            OSDHandlerHelper
                                    .setHandlerState(CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER);
                            A4TVMultimediaController
                                    .setControlPosition(A4TVMultimediaController.MULTIMEDIA_CONTROLLER_PLAY);
                            ControlProvider.setFlagPlay(true);
                            ControlProvider.setFileName(mFriendlyName);
                            ControlProvider.setFileDescription("Renderer Play");
                            // Show Info()
                            ((MainActivity) mActivity).getPageCurl().info();
                            // Set Key Listener State
                            MainKeyListener
                                    .setAppState(MainKeyListener.DLNA_RENDERER);
                        } else {
                            Log.i(TAG, "URI is empty!!!");
                            try {
                                MainActivity.service.getDlnaControl()
                                        .notifyDlnaRenderer(0, 0, "");
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (vi.getTag().equals(
                            mActivity.getResources().getString(
                                    R.string.play_in_pip))) {
                        // ///////////////////////////////////
                        // Play file in PIP
                        // ///////////////////////////////////
                        Log.d(TAG, "Play file in PIP");
                        // Toast.makeText(mActivity,
                        // R.string.not_implemented,
                        // Toast.LENGTH_SHORT).show();
                        // Close context dialog
                        dialogContext.cancel();
                        if (mFilePath.length() > 1) {
                            if (EXTENSIONS_VIDEO.contains(mMime)) {
                                mMediaController.play(mFilePath, 1);
                                startAsyncTask(ASYNC_PLAY);
                                // Set Renderer State
                                mRendererState = RENDERER_STATE_PLAY_PIP;
                            }
                        } else {
                            Log.i(TAG, "URI is empty!!!");
                            try {
                                MainActivity.service.getDlnaControl()
                                        .notifyDlnaRenderer(0, 0, "");
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (vi.getTag().equals(
                            mActivity.getResources().getString(
                                    R.string.play_in_pap))) {
                        // ///////////////////////////////////
                        // Play file in PIP
                        // ///////////////////////////////////
                        Log.d(TAG, "Play file in PAP");
                        // Toast.makeText(mActivity,
                        // R.string.not_implemented,
                        // Toast.LENGTH_SHORT).show();
                        // Close context dialog
                        dialogContext.cancel();
                        if (mFilePath.length() > 1) {
                            if (EXTENSIONS_VIDEO.contains(mMime)) {
                                mMediaController.play(mFilePath, 2);
                                startAsyncTask(ASYNC_PLAY);
                                // Set Renderer State
                                mRendererState = RENDERER_STATE_PLAY_PAP;
                            }
                        } else {
                            Log.i(TAG, "URI is empty!!!");
                            try {
                                MainActivity.service.getDlnaControl()
                                        .notifyDlnaRenderer(0, 0, "");
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (vi.getTag()
                            .equals(mActivity.getResources().getString(
                                    R.string.cancel))) {
                        // ///////////////////////////////////
                        // Reject
                        // ///////////////////////////////////
                        // Close context dialog
                        dialogContext.cancel();
                        try {
                            MainActivity.service.getDlnaControl()
                                    .notifyDlnaRenderer(0, 0, "");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            // set focus listener of button
            button.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // get drawable from theme for small layout
                    // background
                    TypedArray atts = mActivity.getTheme()
                            .obtainStyledAttributes(
                                    new int[] { R.attr.LayoutFocusDrawable });
                    int backgroundID = atts.getResourceId(0, 0);
                    atts.recycle();
                    if (hasFocus) {
                        smallLayoutHorizontal.getChildAt(0).setSelected(true);
                        smallLayoutHorizontal
                                .setBackgroundResource(backgroundID);
                    } else {
                        smallLayoutHorizontal.getChildAt(0).setSelected(false);
                        smallLayoutHorizontal
                                .setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            });
            button.setBackgroundColor(Color.TRANSPARENT);
            smallLayoutHorizontal.addView(button);
            // add view
            contentLinearLayout.addView(smallLayoutHorizontal);
            if (i < strings.length - 1) {
                // create horizontal line
                ImageView horizLineSmall = new ImageView(mActivity);
                android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dialogContext.getWindow().getAttributes().width - 10,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                horizLineSmall.setLayoutParams(params);
                // get drawable from theme for image source
                atts = mActivity.getTheme().obtainStyledAttributes(
                        new int[] { R.attr.DialogContextDividerLine });
                backgroundID = atts.getResourceId(0, 0);
                horizLineSmall.setImageResource(backgroundID);
                // add view
                contentLinearLayout.addView(horizLineSmall);
            }
        }
        return mainLinLayout;
    }

    public CheckServiceType getCheckServiceType() {
        return checkServiceType;
    }

    public boolean isPiPMode() {
        return (mRendererState == RENDERER_STATE_PLAY_PIP) ? true : false;
    }

    public static void setOnCompletition(boolean flag) {
        RendererController.flagOnCompletition = flag;
    }
}
