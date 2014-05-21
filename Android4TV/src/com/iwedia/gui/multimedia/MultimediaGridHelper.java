package com.iwedia.gui.multimedia;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVMultimediaController.ControlProvider;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.pvr.PVRHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

;
/**
 * Handling preparing data for drawing
 * 
 * @author Veljko Ilkic
 */
public class MultimediaGridHelper implements OSDGlobal, MultimediaGlobal {
    public static final String LOG_TAG = "MultimediaGridHelper";
    /** Path if there is no image */
    public static final String NO_IMAGE = "-1";
    /** Ret value from service if folder is empty */
    public static final int EMPTY_FOLDER = 0;
    /** Reference of main activity */
    private Activity activity;
    /** Layout inflater object */
    private LayoutInflater inflater;
    /** Progress dialog for loading data */
    private A4TVProgressDialog progressDialog;
    private int SAME_FOLDER = 66666;
    public static boolean isBrowsingUSB = false;
    /** Small context dialog that has drop down items */
    private A4TVDialog dialogContext;
    /** Alert dialog for context dialog */
    private A4TVAlertDialog alertDialog;
    private static final int kHIGH_BIT = 1 << 7;
    private String lyrics = "";
    private File file = null;
    private Bitmap albumArt = null;

    /** Constructor 1 */
    public MultimediaGridHelper(Activity activity) {
        super();
        // Take reference of main activity;
        this.activity = activity;
        // Create inflater
        inflater = (LayoutInflater) activity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Create progress dialog object
        progressDialog = new A4TVProgressDialog(activity);
        progressDialog.setTitleOfAlertDialog(R.string.loading_data);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(R.string.please_wait);
    }

    /** Prepare data for get view method in adapters */
    public View prepareDataForAdapter(MultimediaContent content) {
        // Inflate grid element xml
        LinearLayout item = (LinearLayout) inflater.inflate(
                com.iwedia.gui.R.layout.multimedia_list_element_grid, null);
        // Set layout params on frame layout
        GridView.LayoutParams params1 = new GridView.LayoutParams(
                LayoutParams.MATCH_PARENT,
                (int) (5 * MainActivity.screenHeight / 28.5));
        item.setLayoutParams(params1);
        // Take references of multimedia list item
        A4TVTextView contentName = (A4TVTextView) item
                .findViewById(com.iwedia.gui.R.id.contentName);
        ImageView contentImage = (ImageView) item
                .findViewById(com.iwedia.gui.R.id.contentImage);
        A4TVTextView contentNameText = (A4TVTextView) item
                .findViewById(com.iwedia.gui.R.id.contentNameText);
        ImageView contentIncompleteImage = (ImageView) item
                .findViewById(com.iwedia.gui.R.id.contentIncompleteImage);
        ImageView contentManualImage = (ImageView) item
                .findViewById(com.iwedia.gui.R.id.contentManualImage);
        // ///////////////////////////////////////
        // Set text size
        // ///////////////////////////////////////
        // if (((MainActivity) activity).isFullHD()) {
        // contentName.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_item_text_size_1080p));
        // contentNameText.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_list_no_image_name_1080p));
        // } else {
        // contentName.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_item_text_size));
        // contentNameText.setTextSize(activity.getResources().getDimension(
        // R.dimen.content_list_no_image_name));
        // }
        // //////////////////////
        // Real element
        // //////////////////////
        if (content != null) {
            // ////////////////////////////////////
            // Set content filter icon
            // ////////////////////////////////////
            // /////////////////////////////////////////////
            // DRAW CONTENT
            // /////////////////////////////////////////////
            if (content.getImageType() != null) {
                // ///////////////////////////////
                // First screen contents
                // ////////////////////////////////
                if (!content.getImageType().equals("DEFAULT")) {
                    drawFirstScreenItems(content, contentImage);
                }
                // //////////////////////////////////
                // Other content
                // //////////////////////////////////
                else {
                    // Check file type
                    if (content.getType() != null) {
                        // ////////////////////////
                        // Folders
                        // /////////////////////////
                        if (content.getType().toLowerCase().equals("dir")) {
                            // Draw dir item
                            drawFolder(content, contentImage);
                        }
                        // ////////////////////////////////
                        // File
                        // ////////////////////////////////
                        else {
                            // Check file type
                            if (content.getType().toLowerCase().equals("file")) {
                                drawFile(content, contentImage);
                            }
                            // ////////////////////////////
                            // Nor file Nor folder
                            // /////////////////////////////
                            else {
                                contentImage
                                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_all_media);
                            }
                        }
                    } else {
                        // //////////////////////////
                        // No file type
                        // //////////////////////////
                        contentImage
                                .setImageResource(com.iwedia.gui.R.drawable.multimedia_all_media);
                    }
                }
            } else {
                // NO multimedia file type
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_all_media);
            }
            // Set name
            contentName.setText(content.getName());
            checkIncomplete(content.isIncomplete(), contentIncompleteImage);
            checkManual(false, contentManualImage);
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

    /** Draw first screen items */
    private void drawFirstScreenItems(MultimediaContent content,
            ImageView contentImage) {
        // /////////////////////
        // Local
        // /////////////////////
        if (content.getImageType().equals("LOCAL")) {
            contentImage
                    .setImageResource(com.iwedia.gui.R.drawable.multimedia_local_storage);
        } else {
            // /////////////////////
            // USB
            // /////////////////////
            if (content.getImageType().equals("USB")) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_usb);
            }
            // /////////////////////
            // DLNA
            // /////////////////////
            else {
                if (content.getImageType().equals("DLNA")) {
                    contentImage
                            .setImageResource(com.iwedia.gui.R.drawable.multimedia_dlna);
                } else {
                    if (content.getImageType().equals("Playlists")) {
                        contentImage
                                .setImageResource(com.iwedia.gui.R.drawable.multimedia_collections);
                    }
                    // //////////////////////
                    // PVR
                    // ///////////////////////
                    else {
                        contentImage
                                .setImageResource(com.iwedia.gui.R.drawable.multimedia_pvr_device);
                    }
                }
            }
        }
    }

    /** Draw folder item */
    private void drawFolder(Content content, ImageView contentImage) {
        // ////////////////////////
        // Music folder
        // ////////////////////////
        if (content.getName().equalsIgnoreCase("Music")
                || content.getName().equalsIgnoreCase("Audio")
                || (((MultimediaContent) content).getPlaylistType() != null && ((MultimediaContent) content)
                        .getPlaylistType().equalsIgnoreCase("audio"))) {
            contentImage
                    .setImageResource(com.iwedia.gui.R.drawable.multimedia_music_folder);
        } else {
            // ////////////////////////
            // Photo folder
            // ////////////////////////
            if (content.getName().equalsIgnoreCase("Photos")
                    || content.getName().equalsIgnoreCase("Photo")
                    || content.getName().equalsIgnoreCase("Pictures")
                    || (((MultimediaContent) content).getPlaylistType() != null && ((MultimediaContent) content)
                            .getPlaylistType().equalsIgnoreCase("image"))) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_photos_folder);
            } else {
                // ////////////////////////
                // Videos folder
                // ////////////////////////
                if (content.getName().equalsIgnoreCase("Videos")
                        || content.getName().equalsIgnoreCase("Video")
                        || (((MultimediaContent) content).getPlaylistType() != null && ((MultimediaContent) content)
                                .getPlaylistType().equalsIgnoreCase("video"))) {
                    contentImage
                            .setImageResource(com.iwedia.gui.R.drawable.multimedia_video_folder);
                } else {
                    // ////////////////////////
                    // Collections folder
                    // ////////////////////////
                    if (content.getName().equalsIgnoreCase("Collections")
                            || content.getName().equalsIgnoreCase("Playlists")) {
                        contentImage
                                .setImageResource(com.iwedia.gui.R.drawable.multimedia_collections);
                    } else {
                        // /////////////////////////
                        // All
                        // //////////////////////////
                        contentImage
                                .setImageResource(com.iwedia.gui.R.drawable.multimedia_default_folder);
                    }
                }
            }
        }
    }

    /** Draw file */
    private void drawFile(MultimediaContent content, ImageView contentImage) {
        // /////////////////////////////
        // Check MIME Type
        // /////////////////////////////
        if (content.getMime() != null) {
            // ////////////////////////////
            // Video files
            // ////////////////////////////
            if (EXTENSIONS_VIDEO.contains(content.getMime().toLowerCase())) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_video);
                return;
            }
            // //////////////////////////////
            // Audio files
            // //////////////////////////////
            if (EXTENSIONS_AUDIO.contains(content.getMime().toLowerCase())) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_audio);
                return;
            }
            // //////////////////////////////////
            // IMAGE FILES
            // ///////////////////////////////////
            if (EXTENSIONS_IMAGE.contains(content.getMime().toLowerCase())) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_photo);
                return;
            }
            // //////////////////////////////////
            // NOT DEFINED MIME
            // //////////////////////////////////
            contentImage
                    .setImageResource(com.iwedia.gui.R.drawable.multimedia_all_media);
        }
        // ///////////////////////////////////
        // Check extension
        // /////////////////////////////////////
        else {
            // ////////////////////////////
            // Video files
            // ////////////////////////////
            if (EXTENSIONS_VIDEO.contains(content.getExtension().toLowerCase())) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_video);
                return;
            }
            // //////////////////////////////
            // Audio files
            // //////////////////////////////
            if (EXTENSIONS_AUDIO.contains(content.getExtension().toLowerCase())) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_audio);
                return;
            }
            // ///////////////////////////////
            // Image files
            // ////////////////////////////////
            if (EXTENSIONS_IMAGE.contains(content.getExtension().toLowerCase())) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_photo);
                return;
            }
            // //////////////////////////////////
            // PVR file
            // //////////////////////////////////
            if (content.getExtension().toLowerCase()
                    .equalsIgnoreCase("pvrfile")) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.multimedia_pvr_file);
                return;
            }
            // ////////////////////////////////////
            // PVR scheduled file
            // ////////////////////////////////////
            if (content.getExtension().toLowerCase()
                    .equalsIgnoreCase("pvrschedule")) {
                contentImage
                        .setImageResource(com.iwedia.gui.R.drawable.pvr_scheduled_file);
                return;
            }
            // //////////////////////////////////
            // NOT DEFINED EXTENSTION
            // //////////////////////////////////
            contentImage
                    .setImageResource(com.iwedia.gui.R.drawable.multimedia_all_media);
        }
    }

    /** Refresh multimedia content */
    private void refreshMultimedia(boolean multimedia_pvr) {
        // //////////////////////////////
        // First screen
        // //////////////////////////////
        if (MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            // //////////////////////////
            // Multimedia folder
            // //////////////////////////
            if (multimedia_pvr) {
                // Show second
                ((MainActivity) activity).getMultimediaHandler()
                        .showMultimediaSecond();
                ((MainActivity) activity).getMultimediaHandler().filterContent(
                        MultimediaHandler.FILTER_MULTIMEDIA);
            }
            // //////////////////////////////
            // PVR folder
            // //////////////////////////////
            else {
                // Show PVR
                ((MainActivity) activity).getMultimediaHandler()
                        .showMultimediaPvr();
                ((MainActivity) activity).getMultimediaHandler().filterContent(
                        MultimediaHandler.FILTER_RECORD_PVR_OPTION);
                // Focus first element
                ((MainActivity) activity).getMultimediaHandler()
                        .getMultimediaFileBrowserPvrHandler()
                        .focusActiveElement(0);
            }
        } else {
            // //////////////////////////////////
            // Second screen
            // //////////////////////////////////
            if (MultimediaHandler.multimediaScreen == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
                ((MainActivity) activity).getMultimediaHandler().filterContent(
                        MultimediaHandler.FILTER_MULTIMEDIA);
            }
        }
        ((MainActivity) activity).getMultimediaHandler().requestContentItems(0);
    }

    /** Async Task for loading data */
    private class LoadTask extends AsyncTask<Void, Void, Boolean> {
        /** Contetn to load */
        private MultimediaContent content;
        private int goContentSuccess = 1;

        public LoadTask(MultimediaContent content) {
            super();
            this.content = content;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: Applies only on main display
            final int displayId = 0;
            try {
                goContentSuccess = MainActivity.service.getContentListControl()
                        .goContent(content, displayId);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Boolean result) {
            if (goContentSuccess != EMPTY_FOLDER) {
                // Check if it is same folder as current
                if (goContentSuccess != SAME_FOLDER) {
                    // Increase folder level
                    MultimediaHandler.secondScreenFolderLevel = MultimediaHandler.secondScreenFolderLevel
                            + goContentSuccess;
                    // ////////////////////////////////
                    // PVR SCREEN
                    // ////////////////////////////////
                    if (content.getImageType().equals("PVR")) {
                        try {
                            PvrSortMode sortMode = MainActivity.service
                                    .getPvrControl().getMediaListSortMode();
                            PvrSortOrder sortOrder = MainActivity.service
                                    .getPvrControl().getMediaListSortOrder();
                            int pvrSize = MainActivity.service
                                    .getContentListControl()
                                    .getContentListSize();
                            if (pvrSize > 0) {
                                MultimediaHandler.pvrFileBrowserText
                                        .setText("PVR playlist sorted by "
                                                + MultimediaHandler.sortPvrFilesBy[sortMode
                                                        .getValue()]
                                                + " in "
                                                + MultimediaHandler.sortPvrFilesByOrder[sortOrder
                                                        .getValue()] + " order");
                            } else {
                                MultimediaHandler.pvrFileBrowserText
                                        .setText(activity
                                                .getResources()
                                                .getString(
                                                        R.string.multimedia_pvr_playlist));
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (!((MainActivity) activity).getMultimediaHandler()
                                .getLinearLayoutSortedBy().isShown()) {
                            ((MainActivity) activity).getMultimediaHandler()
                                    .getLinearLayoutSortedBy()
                                    .setVisibility(View.VISIBLE);
                        }
                        // Refresh multimedia content
                        refreshMultimedia(false);
                    }
                    // //////////////////////////////////
                    // LOCAL USB DLNA
                    // //////////////////////////////////
                    else {
                        // Reduce navigation path
                        if (goContentSuccess < 0) {
                            MultimediaNavigationHandler
                                    .returnToPreviousFolder(Math
                                            .abs(goContentSuccess));
                        }
                        // Refresh multimedia content
                        refreshMultimedia(true);
                        // display image icon for playlist sort
                        if (MultimediaHandler.secondScreenFolderLevel == 2
                                && content.getPlaylistID() != 0) {
                            ((MainActivity) activity)
                                    .getMultimediaHandler()
                                    .getLinearLayoutSortedPlaylistItemsByTitle()
                                    .setVisibility(View.VISIBLE);
                            if (!content.getPlaylistType().equals("image")) {
                                ((MainActivity) activity)
                                        .getMultimediaHandler()
                                        .getLinearLayoutSortedPlaylistItemsByDuration()
                                        .setVisibility(View.VISIBLE);
                                if (content.getPlaylistType().equals("audio")) {
                                    ((MainActivity) activity)
                                            .getMultimediaHandler()
                                            .getLinearLayoutSortedPlaylistItemsByArtist()
                                            .setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
                // ////////////////////////////
                // Show same folder message
                // /////////////////////////////
                else {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(com.iwedia.gui.R.string.already_in_this_folder);
                    // Remove navigation object if folder can't be opened
                    if (goContentSuccess == 0) {
                        MultimediaNavigationHandler.returnToPreviousFolder(1);
                    }
                }
            } else {
                // Show message that folder is empty
                A4TVToast toast = new A4TVToast(activity);
                toast.showToast(com.iwedia.gui.R.string.folder_is_empty);
                // Remove navigation object if folder can't be opened
                if (goContentSuccess == 0) {
                    MultimediaNavigationHandler.returnToPreviousFolder(1);
                }
            }
            progressDialog.dismiss();
        }
    };

    /**
     * Go content on click
     * 
     * @param content
     * @param enableNavigationArrows
     * @return true if content is folder
     */
    public boolean goContent(final MultimediaContent content,
            boolean enableNavigationArrows, int displayId) {
        boolean isRecognizedFormat = false;
        // /////////////////////////////////
        // Check if file type isn't null
        // /////////////////////////////////
        if (content.getType() != null) {
            // //////////////////////////////////////
            // File clicked
            // //////////////////////////////////////
            if (content.getType().toLowerCase().equals("file")) {
                Content activeContent;
                try {
                    activeContent = MainActivity.service
                            .getContentListControl().getActiveContent(0);
                    if ((activeContent.getFilterType() == FilterType.INPUTS)
                            && (displayId == 0)) {
                        MainActivity.service.getContentListControl()
                                .stopContent(activeContent, 0);
                    } else {
                        if (activeContent.getSourceType() != SourceType.ANALOG) {
                            /*
                             * Remove WebView from screen and set key mask to 0
                             */
                            if (0 != (MainActivity.getKeySet())) {
                                try {
                                    if (!MainActivity.activity
                                            .isHbbTVInHTTPPlaybackMode()) {
                                        MainActivity.activity.webDialog
                                                .getHbbTVView().setAlpha(
                                                        (float) 0.00);
                                        MainActivity.setKeySet(0);
                                        MainActivity.activity
                                                .getPrimaryVideoView()
                                                .setScaling(0, 0, 1920, 1080);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                boolean isVideoOrAudio = false;
                // ///////////////////////////////////////
                // Check if content is video or audio
                // ///////////////////////////////////////
                if (content.getMime() != null) {
                    if (EXTENSIONS_VIDEO.contains(content.getMime()
                            .toLowerCase())
                            || EXTENSIONS_AUDIO.contains(content.getMime()
                                    .toLowerCase())) {
                        isVideoOrAudio = true;
                    }
                } else {
                    if (EXTENSIONS_VIDEO.contains(content.getExtension()
                            .toLowerCase())
                            || EXTENSIONS_AUDIO.contains(content.getExtension()
                                    .toLowerCase())) {
                        isVideoOrAudio = true;
                    }
                }
                // /////////////////////////////
                // Video or audio
                // /////////////////////////////
                if (isVideoOrAudio) {
                    isRecognizedFormat = true;
                    try {
                        MainActivity.service.getContentListControl().goContent(
                                content, displayId);
                        // //////////////////////////////////////////////////////////
                        // Check for music file and show music from dlna layout
                        // //////////////////////////////////////////////////////////
                        MediaMetadataRetriever retriever = initMetadataRetriever(content
                                .getFileURL());
                        boolean isMusicInfo = false;
                        if (retriever != null) {
                            String title = getMetadataInfo(retriever,
                                    MediaMetadataRetriever.METADATA_KEY_TITLE);
                            String artist = getMetadataInfo(retriever,
                                    MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            String album = getMetadataInfo(retriever,
                                    MediaMetadataRetriever.METADATA_KEY_ALBUM);
                            albumArt = getAlbumArt(content.getFileURL());
                            if (title != null) {
                                ControlProvider.setFileName(title);
                            }
                            if (artist != null) {
                                ControlProvider.setFileDescription(artist);
                            }
                            if (album != null) {
                                ControlProvider.setNameOfAlbum(album);
                            }
                            isMusicInfo = true;
                            if (title == null && artist == null) {
                                isMusicInfo = false;
                            }
                        } else {
                            albumArt = null;
                        }
                        // ////////////////////////////////////////////
                        // Play multimedia file on main video view
                        // ////////////////////////////////////////////
                        IOSDHandler curlHandler = ((MainActivity) activity)
                                .getPageCurl();
                        curlHandler.initControlProviderDLNALocal();
                        curlHandler.prepareAndStartMultiMediaPlayBackDLNALocal(
                                content, isMusicInfo);
                        // /////////////////////////////
                        // Close multimedia
                        // /////////////////////////////
                        ((MainActivity) activity).getMultimediaHandler()
                                .hideMultimedia();
                        MainKeyListener
                                .setAppState(MainKeyListener.MULTIMEDIA_PLAYBACK);
                        // ///////////////////////////////////////////////
                        // Save index of clicked item (Video or Audio)
                        // //////////////////////////////////////////////////
                        // Update image of last showed image
                        ((MainActivity) activity)
                                .getMultimediaHandler()
                                .getMultimediaShowHandler()
                                .setIndexOfLastAudioOrVideoShowed(
                                        ((MainActivity) activity)
                                                .getMultimediaHandler()
                                                .getMultimediaFileBrowserSecondHandler()
                                                .getIndexOfClickedItem());
                        // //////////////////////////////////////////////////////////
                        // Check for music file and show music from dlna layout
                        // //////////////////////////////////////////////////////////
                        // ////////////////////////////////
                        // Check MIME
                        // ////////////////////////////////
                        if (content.getMime() != null) {
                            // //////////////////////////////////
                            // Audio files
                            // /////////////////////////////////
                            if (EXTENSIONS_AUDIO.contains(content.getMime()
                                    .toLowerCase())) {
                                ImageView musicIcon = MainActivity.activity
                                        .getMultimediaHandler()
                                        .getImageViewMusicReproduction();
                                if (albumArt != null) {
                                    musicIcon.setImageBitmap(albumArt);
                                } else {
                                    musicIcon
                                            .setImageResource(com.iwedia.gui.R.drawable.music_icon);
                                }
                                showDlnaMusicIcon();
                            }
                            // ///////////////////////////////////
                            // Video files
                            // ///////////////////////////////////
                            if (EXTENSIONS_VIDEO.contains(content.getMime()
                                    .toLowerCase())) {
                                // /////////////////////////////////
                                // Hide all if it is video
                                // //////////////////////////////////
                                hideDlnaOverlays();
                            }
                        }
                        // ///////////////////////////////
                        // Check extension
                        // ////////////////////////////////
                        else {
                            // //////////////////////////////////
                            // Audio files
                            // /////////////////////////////////
                            if (EXTENSIONS_AUDIO.contains(content
                                    .getExtension().toLowerCase())) {
                                final ImageView musicIcon = MainActivity.activity
                                        .getMultimediaHandler()
                                        .getImageViewMusicReproduction();
                                if (content.getFileURL().endsWith("mp3")) {
                                    file = new File(content.getFileURL());
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (hasID3(file) != -1) {
                                                        lyrics = parseMP3AudioFile(file);
                                                        // if (lyrics.length() >
                                                        // 0) {
                                                        MainActivity.activity
                                                                .getMultimediaHandler()
                                                                .setScrollLyricsValue(
                                                                        0);
                                                        MainActivity.activity
                                                                .getMultimediaHandler()
                                                                .getScrollViewLyrics()
                                                                .scrollTo(
                                                                        0,
                                                                        MainActivity.activity
                                                                                .getMultimediaHandler()
                                                                                .getScrollLyricsValue());
                                                        MainActivity.activity
                                                                .getMultimediaHandler()
                                                                .setDetectScrollLyricsEnd(
                                                                        false);
                                                        MainActivity.activity
                                                                .getMultimediaHandler()
                                                                .getTextViewLyrics()
                                                                .setVisibility(
                                                                        View.VISIBLE);
                                                        MainActivity.activity
                                                                .getMultimediaHandler()
                                                                .getTextViewLyrics()
                                                                .setText(lyrics);
                                                        // }
                                                    } else {
                                                        MainActivity.activity
                                                                .getMultimediaHandler()
                                                                .getTextViewLyrics()
                                                                .setVisibility(
                                                                        View.GONE);
                                                    }
                                                    if (albumArt != null) {
                                                        musicIcon
                                                                .setImageBitmap(albumArt);
                                                    } else {
                                                        musicIcon
                                                                .setImageResource(com.iwedia.gui.R.drawable.music_icon);
                                                    }
                                                    showDlnaMusicIcon();
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    MainActivity.activity
                                            .getMultimediaHandler()
                                            .getTextViewLyrics()
                                            .setVisibility(View.GONE);
                                    if (albumArt != null) {
                                        musicIcon.setImageBitmap(albumArt);
                                    } else {
                                        musicIcon
                                                .setImageResource(com.iwedia.gui.R.drawable.music_icon);
                                    }
                                    showDlnaMusicIcon();
                                }
                            }
                            // ///////////////////////////////////
                            // Video files
                            // ///////////////////////////////////
                            if (EXTENSIONS_VIDEO.contains(content
                                    .getExtension().toLowerCase())) {
                                // /////////////////////////////////
                                // Hide all if it is video
                                // //////////////////////////////////
                                hideDlnaOverlays();
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                // /////////////////////////////
                // JPG PNG
                // /////////////////////////////
                if (EXTENSIONS_IMAGE.contains(content.getExtension()
                        .toLowerCase())) {
                    // Check enable navigation arrows flag
                    MultimediaShowHandler
                            .setShowNavigationArrows(enableNavigationArrows);
                    isRecognizedFormat = true;
                    final String url = content.getFileURL();
                    // /////////////////////////////////
                    // Image from local
                    // /////////////////////////////////
                    Log.d(LOG_TAG, "url: " + url);
                    if (!url.contains("http:")) {
                        // Check if usb is attached
                        // if (PVRHandler.detectUSB()) {
                        int indexOfClickedItem = ((MainActivity) activity)
                                .getMultimediaHandler()
                                .getMultimediaFileBrowserSecondHandler()
                                .getIndexOfClickedItem();
                        ((MainActivity) activity).getMultimediaHandler()
                                .getMultimediaShowHandler()
                                .setIndexOfLastImageShowed(indexOfClickedItem);
                        // Load image task
                        ((MainActivity) activity).getMultimediaHandler()
                                .getMultimediaShowHandler()
                                .startLoadImageTask(content, true);
                        // } else {
                        // // No usb drive
                        // new A4TVToast(activity)
                        // .showToast(R.string.pvr_no_usb);
                        // }
                    }
                    // /////////////////////////////
                    // Image from DLNA
                    // /////////////////////////////
                    else {
                        int indexOfClickedItem = ((MainActivity) activity)
                                .getMultimediaHandler()
                                .getMultimediaFileBrowserSecondHandler()
                                .getIndexOfClickedItem();
                        ((MainActivity) activity).getMultimediaHandler()
                                .getMultimediaShowHandler()
                                .setIndexOfLastImageShowed(indexOfClickedItem);
                        // Load image task
                        ((MainActivity) activity).getMultimediaHandler()
                                .getMultimediaShowHandler()
                                .startLoadImageTask(content, false);
                    }
                }
                // //////////////////////////////
                // PVR file
                // //////////////////////////////
                if (content.getExtension().toLowerCase().equals("pvrfile")) {
                    boolean playPvrFile;
                    // Check if PVR file should be played
                    if (ConfigHandler.PVR_STORAGE_STRING
                            .equalsIgnoreCase(ConfigHandler.USB_TEXT)) {
                        if (PVRHandler.detectUSB()) {
                            playPvrFile = true;
                        } else {
                            playPvrFile = false;
                            // No usb drive
                            new A4TVToast(activity)
                                    .showToast(R.string.pvr_no_usb);
                        }
                    } else {
                        playPvrFile = true;
                    }
                    if (playPvrFile) {
                        isRecognizedFormat = true;
                        // ///////////////////////////////////////////////
                        // Save index of clicked item (PVR file)
                        // //////////////////////////////////////////////////
                        // Update image of last showed image
                        ((MainActivity) activity)
                                .getMultimediaHandler()
                                .getMultimediaShowHandler()
                                .setIndexOfLastPvrFileShowed(
                                        ((MainActivity) activity)
                                                .getMultimediaHandler()
                                                .getMultimediaFileBrowserPvrHandler()
                                                .getIndexOfClickedItem());
                        IOSDHandler curlHandler = ((MainActivity) activity)
                                .getPageCurl();
                        curlHandler.initControlProviderPVR();
                        curlHandler.prepareAndStartMultiMediaPlayBackPVR(
                                content, displayId);
                        ((MainActivity) activity).getMultimediaHandler()
                                .closeMultimedia();
                        // App state to main menu and show main menu
                        MainKeyListener
                                .setAppState(MainKeyListener.MULTIMEDIA_PLAYBACK);
                        hideDlnaOverlays();
                    }
                }
                // /////////////////////////////////////////
                // PVR SCHEDULED file
                // //////////////////////////////////////////
                if (content.getExtension().toLowerCase().equals("pvrschedule")) {
                    isRecognizedFormat = true;
                    showContextDialogForPvrSchedule(content, activity);
                }
                // ////////////////////////////////////////////
                // APK file
                // ////////////////////////////////////////////
                if (content.getExtension().equals("apk")) {
                    isRecognizedFormat = true;
                    // Call install procedure
                    try {
                        MainActivity.service.getContentListControl().goContent(
                                content, displayId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Not recognized file format
                if (!isRecognizedFormat) {
                    A4TVToast toast = new A4TVToast(activity);
                    toast.setText(R.string.format_is_not_supported);
                    toast.show();
                }
                // It is file
                return false;
            } else {
                // //////////////////////////////
                // Folder/Server clicked
                // //////////////////////////////
                if (content.getImageType().equals("USB")) {
                    isBrowsingUSB = true;
                }
                // Explore next level
                new LoadTask(content).execute();
                // It is folder
                return true;
            }
        }
        // It is null
        return false;
    }

    /** Show multimedia music icon */
    public static void showDlnaMusicIcon() {
        Log.d(LOG_TAG, "showDlnaMusicIcon ");
        MainActivity.screenSaverDialog
                .setScreenSaverCause(MainActivity.screenSaverDialog.RADIO);
        MainActivity.screenSaverDialog.updateScreensaverTimer();
        MainActivity.activity.getMultimediaHandler().getMusicFromDlnaLayout()
                .setVisibility(View.VISIBLE);
        // Hide other layouts for
        MainActivity.activity.findViewById(R.id.linLayMessages).setVisibility(
                View.GONE);
    }

    /** Hide multimedia overlays */
    public static void hideDlnaOverlays() {
        MainActivity.activity.getMultimediaHandler().getMusicFromDlnaLayout()
                .setVisibility(View.GONE);
        // Hide other layouts for
        MainActivity.activity.findViewById(R.id.linLayMessages).setVisibility(
                View.GONE);
    }

    /** Hide antenna overlay before playback */
    public static void hideAntennaOverlay() {
        // Hide antenna overlay
        MainActivity.activity.findViewById(R.id.noSignalAvailableLayout)
                .setVisibility(View.GONE);
    }

    /** Show antenna overlay after playback */
    public static void showAntennaOverlay() {
        if (!MainActivity.activity.getCallBackHandler().isAntennaConnected()) {
            // Hide antenna overlay
            MainActivity.activity.findViewById(R.id.noSignalAvailableLayout)
                    .setVisibility(View.VISIBLE);
        }
    }

    /** Check if content is incomplete */
    public void checkIncomplete(boolean incompleteString,
            ImageView contentIncomplete) {
        if (incompleteString) {
            contentIncomplete
                    .setImageResource(com.iwedia.gui.R.drawable.exclamation_mark);
        } else {
            contentIncomplete.setImageBitmap(null);
        }
    }

    /** Check if content is manual */
    public void checkManual(boolean manualString, ImageView contentManual) {
        if (manualString) {
            contentManual
                    .setImageResource(com.iwedia.gui.R.drawable.exclamation_mark);
        } else {
            contentManual.setImageBitmap(null);
        }
    }

    public void showContextDialogForPvrSchedule(Content content,
            Activity activity) {
        if (content != null) {
            dialogContext = ((MainActivity) activity).getDialogManager()
                    .getContextSmallDialog();
            // Show context
            // //////////////////////////////////
            // Context dialog for PVR SCHEDULED
            // //////////////////////////////////
            if (content.getFilterType() == FilterType.PVR_SCHEDULED) {
                // fill dialog with desired view
                if (dialogContext != null)
                    dialogContext.setContentView(fillDialogWithElements(
                            MultimediaGridOnLongPress.FILE_BROWSER_MULTIMEDIA,
                            content));
            }
            // set dialog size
            if (dialogContext != null) {
                dialogContext.getWindow().getAttributes().width = MainActivity.dialogWidth / 2;
                dialogContext.getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
                // show drop down dialog
                dialogContext.show();
            }
        }
    }

    public static MediaMetadataRetriever initMetadataRetriever(String URI) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (!URI.contains("http:") && URI.endsWith("mp3")) {
            try {
                retriever.setDataSource(URI);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Failed: " + URI + " " + e.toString());
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
        return retriever;
    }

    public static String getMetadataInfo(MediaMetadataRetriever retriever,
            int tag) {
        String value;
        if (tag != -1) {
            value = retriever.extractMetadata(tag);
            Log.d(LOG_TAG, "Return metadata with tag : " + tag + " value: "
                    + value);
            return value;
        }
        retriever.release();
        return null;
    }

    private Bitmap getAlbumArt(String URI) {
        byte[] albumArt;
        if (URI.contains("http:") || !URI.endsWith("mp3")) {
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(URI);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed: " + URI + " " + e.toString());
            e.printStackTrace();
            return null;
        }
        albumArt = retriever.getEmbeddedPicture();
        if (albumArt != null) {
            Bitmap bMap = BitmapFactory.decodeByteArray(albumArt, 0,
                    albumArt.length);
            return bMap;
        }
        return null;
    }

    /**
     * Creates view for context dialog
     */
    private View fillDialogWithElements(int gridId, final Content content) {
        LinearLayout mainLinLayout = new LinearLayout(activity);
        mainLinLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mainLinLayout.setOrientation(LinearLayout.VERTICAL);
        // get drawable from theme for image source
        TypedArray atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogContextBackground });
        int backgroundID = atts.getResourceId(0, 0);
        atts.recycle();
        mainLinLayout.setBackgroundResource(backgroundID);
        // layout of dialog title
        LinearLayout titleLinearLayout = new LinearLayout(activity);
        titleLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        titleLinearLayout.setOrientation(LinearLayout.VERTICAL);
        titleLinearLayout.setPadding(
                (int) activity.getResources().getDimension(
                        R.dimen.a4tvdialog_padding_left),
                (int) activity.getResources().getDimension(
                        R.dimen.a4tvdialog_spinner_padding_top), 0, 0);
        A4TVTextView text = new A4TVTextView(activity, null);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        text.setText(activity.getResources().getString(
                R.string.spinner_choose_title));
        text.setTextSize(activity.getResources().getDimension(
                R.dimen.a4tvdialog_textview_size));
        // add title
        titleLinearLayout.addView(text);
        // add title layout to main layout
        mainLinLayout.addView(titleLinearLayout);
        // create horizontal line
        ImageView horizLine = new ImageView(activity);
        horizLine.setLayoutParams(new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        // get drawable from theme for image source
        atts = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        backgroundID = atts.getResourceId(0, 0);
        horizLine.setBackgroundResource(backgroundID);
        // add horiz line to main layout
        mainLinLayout.addView(horizLine);
        // create scroll view
        ScrollView mainScrollView = new ScrollView(activity);
        mainScrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mainScrollView.setScrollbarFadingEnabled(false);
        mainScrollView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        // add scrollview to main view
        mainLinLayout.addView(mainScrollView);
        LinearLayout contentLinearLayout = new LinearLayout(activity);
        contentLinearLayout.setLayoutParams(new ScrollView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        contentLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        // add content layout to scroll view
        mainScrollView.addView(contentLinearLayout);
        // Init strings
        String[] strings = null;
        /** GET FIELDS FOR CREATING DROP DOWN ITEMS */
        if (gridId == MultimediaGridOnLongPress.FILE_BROWSER_MULTIMEDIA) {
            // /////////////////////////////
            // PVR SCHEDULED
            // /////////////////////////////
            strings = activity.getResources().getStringArray(
                    R.array.remove_from_schedule_dropdown);
        }
        if (strings != null)
            for (int i = 0; i < strings.length; i++) {
                // create small layout
                final LinearLayout smallLayoutHorizontal = new LinearLayout(
                        activity);
                smallLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                smallLayoutHorizontal
                        .setLayoutParams(new LinearLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                MainActivity.dialogListElementHeight));
                smallLayoutHorizontal.setPadding(15, 5, 15, 5);
                smallLayoutHorizontal.setGravity(Gravity.CENTER_VERTICAL);
                // create drop box item
                A4TVButton button = new A4TVButton(activity, null);
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
                        // //////////////////////////////////////////////////////////////////////////////
                        // Remove from schedule
                        // //////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.remove_from_schedulue))) {
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_from_schedulue);
                            alertDialog.setNegativeButton(
                                    R.string.button_text_no,
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.cancel();
                                        }
                                    });
                            alertDialog.setPositiveButton(
                                    R.string.button_text_yes,
                                    new android.view.View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean removedFromSchedule = false;
                                            try {
                                                MainActivity.service
                                                        .getPvrControl()
                                                        .destroyRecord(
                                                                (content)
                                                                        .getIndex());
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }
                                            // Refresh graphics
                                            // if (removedFromSchedule) {
                                            A4TVToast toast = new A4TVToast(
                                                    activity);
                                            toast.showToast(R.string.pvr_schedule_file_is_deleted);
                                            try {
                                                MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                                        .getContentListControl()
                                                        .getContentListSize();
                                                // ////////////////////////////////////////
                                                // Prepare data for focusing
                                                // ////////////////////////////////////////
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserPvrHandler()
                                                        .setCurrentPage(0);
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserPvrHandler()
                                                        .initData();
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserPvrHandler()
                                                        .focusActiveElement(0);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // }
                                            alertDialog.cancel();
                                        }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                        }
                        // /////////////////////////////////
                        // Cancel
                        // /////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.cancel))) {
                            // Close context dialog
                            dialogContext.cancel();
                        }
                    }
                });
                // set focus listener of button
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
                        atts.recycle();
                        if (hasFocus) {
                            smallLayoutHorizontal.getChildAt(0).setSelected(
                                    true);
                            smallLayoutHorizontal
                                    .setBackgroundResource(backgroundID);
                        } else {
                            smallLayoutHorizontal.getChildAt(0).setSelected(
                                    false);
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
                    ImageView horizLineSmall = new ImageView(activity);
                    android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            dialogContext.getWindow().getAttributes().width - 10,
                            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    horizLineSmall.setLayoutParams(params);
                    // get drawable from theme for image source
                    atts = activity.getTheme().obtainStyledAttributes(
                            new int[] { R.attr.DialogContextDividerLine });
                    backgroundID = atts.getResourceId(0, 0);
                    horizLineSmall.setImageResource(backgroundID);
                    // add view
                    contentLinearLayout.addView(horizLineSmall);
                }
            }
        return mainLinLayout;
    }

    private String getLyricsText(byte[] buffer, int pos, RandomAccessFile tag) {
        int encode = 0;
        int temp = 0;
        int frameSize = 0;
        int offset = 4;
        try {
            if (tag.read(buffer, 0, 4) != -1) {
                frameSize = java.nio.ByteBuffer.wrap(buffer).getInt();
            }
            pos += 6;
            tag.seek(pos);
            // encode
            tag.read(buffer, 0, 1);
            switch (buffer[0]) {
                case 0x00:
                    encode = 0;
                    break;
                case 0x01:
                    encode = 1;
                    break;
                case 0x02:
                    encode = 2;
                    break;
                case 0x03:
                    encode = 3;
                    break;
                default:
                    break;
            }
            pos += 4;
            tag.seek(pos);
            while (tag.read(buffer, temp, 1) != -1) {
                if (buffer[temp] >= 0x41 && buffer[temp] <= 0x5A) {
                    offset = offset + temp;
                    break;
                }
                temp++;
                pos++;
            }
            // lyrics text
            tag.seek(pos);
            tag.read(buffer, 0, frameSize - offset);
            if (encode == 0) {
                lyrics = new String(buffer, 0, frameSize - offset, "ISO-8859-1");
            } else if (encode == 1) {
                lyrics = new String(buffer, 0, frameSize - offset, "UTF-16LE");
            } else if (encode == 2) {
                lyrics = new String(buffer, 0, frameSize - offset, "UTF-16BE");
            } else if (encode == 3) {
                lyrics = new String(buffer, 0, frameSize - offset, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lyrics;
    }

    private String parseMP3AudioFile(File file) {
        RandomAccessFile tag = null;
        try {
            tag = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[(int) file.length()];
        try {
            int pos = 0;
            if (tag != null) {
                tag.read(buffer, 0, 3);
                if (buffer[0] == 'I' && buffer[1] == 'D' && buffer[2] == '3') {
                    pos += 3;
                } else {
                    tag.close();
                    return "";
                }
                // int id3Length = getID3Length(file);
                while (tag.read(buffer, pos, 1) != -1 /* && pos <= id3Length */) {
                    // if "U"
                    if (buffer[pos] == 0x55) {
                        pos++;
                        tag.seek(pos);
                        if (tag.read(buffer, 0, 3) != -1) {
                            // if "LT"
                            if (buffer[0] == 0x4C && buffer[1] == 0x54) {
                                pos += 2;
                                tag.seek(pos);
                                lyrics = getLyricsText(buffer, pos, tag);
                                tag.close();
                                return lyrics;
                            }
                            // if "SLT"
                            if (buffer[0] == 0x53 && buffer[1] == 0x4C
                                    && buffer[2] == 0x54) {
                                pos += 3;
                                tag.seek(pos);
                                lyrics = getLyricsText(buffer, pos, tag);
                                tag.close();
                                return lyrics;
                            } else {
                                pos += 3;
                            }
                        }
                    }
                    pos++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (tag != null) {
                    tag.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }

    private int hasID3(File file) {
        int index = -1;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            String receiveString;
            StringBuilder s = new StringBuilder();
            int id3Length = getID3Length(file);
            while ((receiveString = bufferedReader.readLine()) != null
                    && s.length() < id3Length) {
                if ((index = receiveString.indexOf("USLT")) != -1
                        || (index = receiveString.indexOf("ULT")) != -1) {
                    bufferedReader.close();
                    return index;
                }
                s.append(receiveString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return index;
    }

    private int getID3Length(File file) throws IOException {
        if (file == null || !file.exists()) {
            return 0;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            is = new BufferedInputStream(is, 8192);
            byte footer[];
            footer = readArray(is, 10);
            Number tagLength = readSynchsafeInt(footer, 6);
            int totalLength = 0;
            if (tagLength != null) {
                totalLength = 10 + tagLength.intValue();
            }
            return totalLength;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] readArray(InputStream is, int length) throws IOException {
        byte result[] = new byte[length];
        int total = 0;
        while (total < length) {
            int read = is.read(result, total, length - total);
            if (read < 0) {
                throw new IOException("bad read");
            }
            total += read;
        }
        return result;
    }

    private Number readSynchsafeInt(byte bytes[], int start) {
        if ((start + 3) >= bytes.length) {
            return null;
        }
        int array[] = { 0xff & bytes[start++], 0xff & bytes[start++],
                0xff & bytes[start++], 0xff & bytes[start++], };
        Log.d(LOG_TAG, "start: " + start);
        for (int i = 0; i < array.length; i++) {
            if ((array[i] & kHIGH_BIT) > 0) {
                array[i] &= kHIGH_BIT;
            }
        }
        int result = (array[0] << 21) | (array[1] << 14) | (array[2] << 7)
                | (array[3] << 0);
        return Integer.valueOf(result);
    }
}
