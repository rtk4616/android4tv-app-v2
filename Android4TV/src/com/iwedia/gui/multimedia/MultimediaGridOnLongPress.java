package com.iwedia.gui.multimedia;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.comm.content.multimedia.PlaylistFile;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.multimedia.MultimediaFavoriteHandler.MultimediaFavoriteGridAdapter;
import com.iwedia.gui.multimedia.MultimediaFileBrowserHandler.FileBrowserGridAdapter;
import com.iwedia.gui.multimedia.MultimediaFilePathHandler.FilePathGridAdapter;
import com.iwedia.gui.multimedia.MultimediaRecentlyHandler.MultimediaRecentlyGridAdapter;

import java.util.ArrayList;

/**
 * OnLong press listener for content list items
 * 
 * @author Veljko Ilkic
 */
public class MultimediaGridOnLongPress implements OnItemLongClickListener,
        MultimediaGlobal {
    public static final String TAG = "MultimediaGridOnLongPress";
    /** Grid id contants */
    public static int FILE_BROWSER_MULTIMEDIA = 0;
    public static int FAVORITE_MULTIMEDIA = 1;
    public static int RECENTLY_MULTIMEDIA = 2;
    public static int FILE_PATH_MULTIMEDIA = 3;
    private static int NUMBER_OF_ITEMS_IN_THREE_ROWS = 18;
    private final int PLAYLIST_NAME_ID = 0;
    /** Grid id */
    private int gridId = FILE_BROWSER_MULTIMEDIA;
    /** Reference of main activity */
    private Activity activity;
    /** Content object of clicked item */
    private Content content;
    /** Small context dialog that has drop down items */
    private A4TVDialog dialogContext;
    /** Screen id */
    private int screenId;
    /** Recently grid adapter reference */
    private MultimediaRecentlyGridAdapter recentlyGridAdapter;
    /** Favorite grid adapter reference */
    private MultimediaFavoriteGridAdapter favoriteGridAdapter;
    /** File browser grid adapter reference */
    private FileBrowserGridAdapter fileBrowserGridAdapter;
    /** File path grid adapter reference */
    private FilePathGridAdapter filePathGridAdapter;
    /** Alert dialog */
    private A4TVAlertDialog alertDialog;
    private static final int[] INDEX_LOOK_UP_TABLE_THREE_ROWS = { 0, 3, 6, 9,
            12, 15, 1, 4, 7, 10, 13, 16, 2, 5, 8, 11, 14, 17 };
    private ArrayList<PlaylistFile> playlists = new ArrayList<PlaylistFile>();
    private PlaylistAdapter adapter;
    private A4TVDialog playlistDialog;

    /** Constructor 1 */
    public MultimediaGridOnLongPress(Activity activity, int screenId, int gridId) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Take screen id
        this.screenId = screenId;
        // Get grid id
        this.gridId = gridId;
        // Take favorite adapter
        this.favoriteGridAdapter = ((MainActivity) activity)
                .getMultimediaHandler().getMutlimediaFavoriteHandler()
                .getFavoriteGridAdapter();
        // /////////////////////////////////
        // First screen
        // /////////////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
            if (gridId == FILE_BROWSER_MULTIMEDIA) {
                // Take all adapter
                this.fileBrowserGridAdapter = ((MainActivity) activity)
                        .getMultimediaHandler()
                        .getMultimediaFileBrowserFirstHandler()
                        .getFileBrowserGridAdapter();
            }
            if (gridId == RECENTLY_MULTIMEDIA) {
                // Take recently adapter
                this.recentlyGridAdapter = ((MainActivity) activity)
                        .getMultimediaHandler().getMultimediaRecentlyHandler()
                        .getMultimediaRecentlyGridAdapter();
            }
        }
        // ///////////////////////////////////
        // Second screen
        // ///////////////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_SECOND_SCREEN) {
            if (gridId == FILE_BROWSER_MULTIMEDIA) {
                // Take all adapter
                this.fileBrowserGridAdapter = ((MainActivity) activity)
                        .getMultimediaHandler()
                        .getMultimediaFileBrowserSecondHandler()
                        .getFileBrowserGridAdapter();
            }
            if (gridId == FILE_PATH_MULTIMEDIA) {
                // Get file path adapter
                this.filePathGridAdapter = ((MainActivity) activity)
                        .getMultimediaHandler().getFilePathHandler()
                        .getFilePathGridAdapter();
            }
        }
        // ///////////////////////////////////
        // PVR screen
        // ///////////////////////////////////
        if (screenId == MultimediaHandler.MULTIMEDIA_PVR_SCREEN) {
            // Take all adapter
            this.fileBrowserGridAdapter = ((MainActivity) activity)
                    .getMultimediaHandler()
                    .getMultimediaFileBrowserPvrHandler()
                    .getFileBrowserGridAdapter();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        // Get clicked content from file browser
        if (gridId == FILE_BROWSER_MULTIMEDIA) {
            content = (Content) fileBrowserGridAdapter.getItem(arg2);
        }
        // Get clicked content from recently
        if (gridId == RECENTLY_MULTIMEDIA) {
            content = ((Content) recentlyGridAdapter.getItem(arg2));
        }
        // Get clicked content from favorite
        if (gridId == FAVORITE_MULTIMEDIA) {
            content = (Content) favoriteGridAdapter.getItem(arg2);
        }
        if (gridId == FILE_PATH_MULTIMEDIA) {
            content = (Content) filePathGridAdapter.getItem(arg2);
        }
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
                    dialogContext.setContentView(fillDialogWithElements(gridId,
                            true, false, arg2));
            } else {
                // ///////////////////////////////////////////
                // Context dialog for PVR RECORD
                // ///////////////////////////////////////////
                if (content.getFilterType() == FilterType.PVR_RECORDED) {
                    // fill dialog with desired view
                    if (dialogContext != null)
                        dialogContext.setContentView(fillDialogWithElements(
                                gridId, false, true, arg2));
                }
                // ////////////////////////////////////
                // Others
                // ////////////////////////////////////
                else {
                    // fill dialog with desired view
                    if (dialogContext != null)
                        dialogContext.setContentView(fillDialogWithElements(
                                gridId, false, false, 0));
                }
            }
            // set dialog size
            if (dialogContext != null) {
                dialogContext.getWindow().getAttributes().width = MainActivity.dialogWidth / 2;
                dialogContext.getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
                // show drop down dialog
                dialogContext.show();
            }
        }
        return true;
    }

    private View fillDialogWithPlaylists() {
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
        text.setText(activity.getResources()
                .getString(R.string.add_to_playlist));
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
        ListView mainListView = new ListView(activity);
        mainListView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mainListView.setDivider(activity.getResources().getDrawable(
                R.drawable.drop_down_divider_line_ics));
        mainListView.setScrollbarFadingEnabled(false);
        mainListView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        TypedArray atts1 = activity.getTheme().obtainStyledAttributes(
                new int[] { R.attr.LayoutFocusDrawable });
        int backgroundID1 = atts1.getResourceId(0, 0);
        mainListView.setSelector(backgroundID1);
        adapter = new PlaylistAdapter();
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long arg3) {
                if (position == 0) {
                    playlistDialog.cancel();
                    MainActivity.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                    MainActivity.activity);
                            askDialog
                                    .setTitleOfAlertDialog("Create new playlist");
                            final A4TVEditText editText = new A4TVEditText(
                                    askDialog.getContext());
                            editText.setEms(40);
                            askDialog.setView(editText);
                            editText.setOnKeyListener(new View.OnKeyListener() {
                                @Override
                                public boolean onKey(View v, int keyCode,
                                        KeyEvent event) {
                                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                        switch (keyCode) {
                                            case KeyEvent.KEYCODE_DPAD_CENTER:
                                            case KeyEvent.KEYCODE_ENTER:
                                                MultimediaContent mContent = null;
                                                A4TVToast toast = new A4TVToast(
                                                        activity);
                                                if (content instanceof MultimediaContent) {
                                                    mContent = (MultimediaContent) content;
                                                }
                                                String multimediaFileType = getMultimediaFileType();
                                                try {
                                                    if (MainActivity.service
                                                            .getContentListControl()
                                                            .getContentFilter(
                                                                    FilterType.MULTIMEDIA)
                                                            .createPlaylist(
                                                                    editText.getText()
                                                                            .toString(),
                                                                    multimediaFileType)) {
                                                        toast.showToast(com.iwedia.gui.R.string.new_playlist);
                                                        if (mContent != null)
                                                            addItemToPlaylist(
                                                                    multimediaFileType,
                                                                    editText.getText()
                                                                            .toString(),
                                                                    mContent);
                                                    } else {
                                                        toast.showToast(com.iwedia.gui.R.string.playlist_already_exist);
                                                    }
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                                askDialog.cancel();
                                                return true;
                                            case KeyEvent.KEYCODE_CLEAR:
                                            case KeyEvent.KEYCODE_FUNCTION:
                                            case KeyEvent.KEYCODE_F10: {
                                                v.onKeyDown(
                                                        KeyEvent.KEYCODE_DEL,
                                                        new KeyEvent(0, 0));
                                                return true;
                                            }
                                            case KeyEvent.KEYCODE_BACK:
                                                askDialog.cancel();
                                                return true;
                                            default:
                                                return false;
                                        }
                                    }
                                    return false;
                                }
                            });
                            askDialog.show();
                        }
                    });
                } else {
                    TextView textView = (TextView) arg1
                            .findViewById(PLAYLIST_NAME_ID);
                    String playlistName = textView.getText().toString();
                    MultimediaContent mContent = null;
                    if (content instanceof MultimediaContent) {
                        mContent = (MultimediaContent) content;
                    }
                    String multimediaFileType = getMultimediaFileType();
                    try {
                        A4TVToast toast = new A4TVToast(activity);
                        if (playlists.get(position).getType()
                                .equals(multimediaFileType)) {
                            if (mContent != null)
                                addItemToPlaylist(multimediaFileType,
                                        playlistName, mContent);
                        } else {
                            String itemType = multimediaFileType
                                    .substring(0, 1).toUpperCase()
                                    + multimediaFileType.substring(1);
                            toast.showToast(itemType
                                    + " item cannot be added to "
                                    + playlists.get(position).getType()
                                    + " playlist.");// com.iwedia.gui.R.string.item_is_not_added_to_playlist);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    playlistDialog.cancel();
                }
            }
        });
        // add listView to main view
        mainLinLayout.addView(mainListView);
        // add playlists to playlist adapter
        playlists.clear();
        ArrayList<PlaylistFile> allPlaylists = null;
        try {
            allPlaylists = (ArrayList<PlaylistFile>) MainActivity.service
                    .getContentListControl()
                    .getContentFilter(FilterType.MULTIMEDIA).getPlaylists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        playlists.add(new PlaylistFile("New Playlist", ""));
        if (allPlaylists != null) {
            for (int i = 0; i < allPlaylists.size(); i++) {
                String playlist = allPlaylists.get(i).getName();
                playlist = playlist.substring(0, playlist.lastIndexOf("."));
                if (getMultimediaFileType().equals(
                        allPlaylists.get(i).getType())) {
                    playlists.add(new PlaylistFile(playlist, allPlaylists
                            .get(i).getType()));
                }
            }
        }
        adapter.notifyDataSetChanged();
        return mainLinLayout;
    }

    /**
     * Creates view for context dialog
     */
    private View fillDialogWithElements(int gridId, boolean pvr_scheduled,
            boolean pvr_recorded, final int position) {
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
        if (gridId == FILE_BROWSER_MULTIMEDIA) {
            // ////////////////////////////
            // Multimedia and file browser
            // ////////////////////////////
            if (!pvr_scheduled && !pvr_recorded) {
                strings = activity.getResources().getStringArray(
                        R.array.add_to_favourites_dropdown);
                if (content instanceof MultimediaContent) {
                    MultimediaContent mContent = (MultimediaContent) content;
                    if (mContent != null) {
                        if (mContent.getType() != null) {
                            if (mContent.getType().toLowerCase().equals("file")) {
                                if (EXTENSIONS_VIDEO.contains(mContent
                                        .getExtension().toLowerCase())
                                        || EXTENSIONS_AUDIO.contains(mContent
                                                .getExtension().toLowerCase())
                                        || EXTENSIONS_IMAGE.contains(mContent
                                                .getExtension().toLowerCase())) {
                                    strings = activity
                                            .getResources()
                                            .getStringArray(
                                                    R.array.add_to_playlist_dropdown);
                                }
                            }
                            if (mContent.getPlaylistID() != 0
                                    && mContent.getType().equals("dir")) {
                                strings = activity
                                        .getResources()
                                        .getStringArray(
                                                R.array.remove_playlist_dropdown);
                            } else {
                                if (mContent.getPlaylistID() != 0
                                        && mContent.getType().equals("file")) {
                                    strings = activity
                                            .getResources()
                                            .getStringArray(
                                                    R.array.remove_item_from_playlist_dropdown);
                                }
                            }
                        }
                    }
                }
            }
            // ///////////////////////////////
            // PVR
            // ///////////////////////////////
            else {
                // /////////////////////////////
                // PVR SCHEDULED
                // /////////////////////////////
                if (pvr_scheduled) {
                    strings = activity.getResources().getStringArray(
                            R.array.remove_from_schedule_dropdown);
                } else {
                    // ////////////////////////////////
                    // PVR RECORDED
                    // ////////////////////////////////
                    if (pvr_recorded) {
                        strings = activity.getResources().getStringArray(
                                R.array.remove_from_pvr_list_dropdown);
                    }
                }
            }
        }
        // //////////////////////////////////////
        // Favorite list
        // //////////////////////////////////////
        if (gridId == FAVORITE_MULTIMEDIA) {
            strings = activity.getResources().getStringArray(
                    R.array.remove_from_favourites_dropdown);
        }
        // ///////////////////////////////////////
        // Recently list
        // ///////////////////////////////////////
        if (gridId == RECENTLY_MULTIMEDIA) {
            strings = activity.getResources().getStringArray(
                    R.array.add_to_favourites_remove_from_recenlty_dropdown);
        }
        // //////////////////////////////////////////
        // File path
        // //////////////////////////////////////////
        if (gridId == FILE_PATH_MULTIMEDIA) {
            strings = activity.getResources().getStringArray(
                    R.array.add_to_favourites_dropdown);
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
                        // /////////////////////////////////////////////////////////////////////////////
                        // Remove all items from playlist
                        // /////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag()
                                .equals(activity
                                        .getResources()
                                        .getString(
                                                R.string.remove_all_items_from_playlist))) {
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_all_items_from_playlist);
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
                                            if (content instanceof MultimediaContent) {
                                                MultimediaContent mContent = (MultimediaContent) content;
                                                try {
                                                    MainActivity.service
                                                            .getContentListControl()
                                                            .getContentFilter(
                                                                    FilterType.MULTIMEDIA)
                                                            .clearPlaylist(
                                                                    mContent.getPlaylistName());
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            try {
                                                MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                                        .getContentListControl()
                                                        .getContentListSize();
                                                // ////////////////////////////////////////
                                                // Prepare data for focusing
                                                // ////////////////////////////////////////
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .setCurrentPage(0);
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .initData();
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .focusActiveElement(0);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            alertDialog.cancel();
                                        }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                        }
                        // /////////////////////////////////////////////////////////////////////////////
                        // Remove playlist
                        // /////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.remove_playlist))) {
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_playlist);
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
                                            if (content instanceof MultimediaContent) {
                                                MultimediaContent mContent = (MultimediaContent) content;
                                                try {
                                                    MainActivity.service
                                                            .getContentListControl()
                                                            .getContentFilter(
                                                                    FilterType.MULTIMEDIA)
                                                            .deletePlaylist(
                                                                    content,
                                                                    mContent.getPlaylistName());
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            try {
                                                MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                                        .getContentListControl()
                                                        .getContentListSize();
                                                // ////////////////////////////////////////
                                                // Prepare data for focusing
                                                // ////////////////////////////////////////
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .setCurrentPage(0);
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .initData();
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .focusActiveElement(0);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            alertDialog.cancel();
                                        }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                        }
                        // /////////////////////////////////////////////////////////////////////////////
                        // Remove item from playlist
                        // /////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.remove_item_from_playlist))) {
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_item_from_playlist);
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
                                            if (content instanceof MultimediaContent) {
                                                MultimediaContent mContent = (MultimediaContent) content;
                                                try {
                                                    MainActivity.service
                                                            .getContentListControl()
                                                            .getContentFilter(
                                                                    FilterType.MULTIMEDIA)
                                                            .removeItemFromPlaylist(
                                                                    content,
                                                                    mContent.getPlaylistName(),
                                                                    mContent.getFileURL());
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            try {
                                                MultimediaFileBrowserHandler.fileBrowserNumberOfItems = MainActivity.service
                                                        .getContentListControl()
                                                        .getContentListSize();
                                                // ////////////////////////////////////////
                                                // Prepare data for focusing
                                                // ////////////////////////////////////////
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .setCurrentPage(0);
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .initData();
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMultimediaFileBrowserSecondHandler()
                                                        .focusActiveElement(0);
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch
                                                // block
                                                e.printStackTrace();
                                            }
                                            alertDialog.cancel();
                                        }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                        }
                        // /////////////////////////////////////////////////////////////////////////////
                        // Add to playlist
                        // /////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.add_to_playlist))) {
                            dialogContext.cancel();
                            playlistDialog = ((MainActivity) activity)
                                    .getDialogManager().getContextSmallDialog();
                            if (playlistDialog != null) {
                                playlistDialog
                                        .setContentView(fillDialogWithPlaylists());
                                playlistDialog.getWindow().getAttributes().width = MainActivity.dialogWidth / 2;
                                playlistDialog.getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
                                playlistDialog.show();
                            }
                        }
                        // /////////////////////////////////////////////////////////////////////////////
                        // Add to favorites
                        // /////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.add_to_favourites))) {
                            // ///////////////////////////////////
                            // Add content to favorite
                            // ///////////////////////////////////
                            boolean addedInFavoriteList = false;
                            try {
                                addedInFavoriteList = MainActivity.service
                                        .getContentListControl()
                                        .addContentToFavorites(content);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // /////////////////////////////////////////////////////////
                            // Refresh graphics if needed on multimedia first
                            // screen
                            // /////////////////////////////////////////////////////////
                            if (addedInFavoriteList) {
                                if (screenId == MultimediaHandler.MULTIMEDIA_FIRST_SCREEN) {
                                    // Refresh graphics
                                    try {
                                        MultimediaFavoriteHandler.multimediaFavoriteNumberOfItems = MainActivity.service
                                                .getContentListControl()
                                                .getFavoritesSize();
                                        ((MainActivity) activity)
                                                .getMultimediaHandler()
                                                .getMutlimediaFavoriteHandler()
                                                .initData();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                // /////////////////////////////////////////////
                                // Show message that items is already there
                                // //////////////////////////////////////////////
                                A4TVToast toast = new A4TVToast(activity);
                                toast.showToast(com.iwedia.gui.R.string.already_in_favorite_list);
                            }
                            // Close context dialog
                            dialogContext.cancel();
                        }
                        // /////////////////////////////////////////////////////////////////////////////
                        // Remove from favorite
                        // /////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.remove_from_favourites))) {
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_from_favourites);
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
                                            try {
                                                MainActivity.service
                                                        .getContentListControl()
                                                        .setActiveFilter(
                                                                FilterType.PVR_RECORDED);
                                                MainActivity.service
                                                        .getContentListControl()
                                                        .removeContentFromFavorites(
                                                                content);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // Refresh graphics
                                            try {
                                                MultimediaFavoriteHandler.multimediaFavoriteNumberOfItems = MainActivity.service
                                                        .getContentListControl()
                                                        .getFavoritesSize();
                                                ((MainActivity) activity)
                                                        .getMultimediaHandler()
                                                        .getMutlimediaFavoriteHandler()
                                                        .initData();
                                                // Handle focus
                                                if (MultimediaFavoriteHandler.multimediaFavoriteNumberOfItems > 0) {
                                                    ((MainActivity) activity)
                                                            .getMultimediaHandler()
                                                            .getMutlimediaFavoriteHandler()
                                                            .focusActiveElement(
                                                                    0);
                                                } else {
                                                    ((MainActivity) activity)
                                                            .getMultimediaHandler()
                                                            .getMultimediaFileBrowserFirstHandler()
                                                            .getGridFileBrowser()
                                                            .requestFocus();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            alertDialog.cancel();
                                        }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                        }
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
                                            Log.d(TAG, "REMOVED FROM SCEDULED "
                                                    + removedFromSchedule + " ");
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
                        // ////////////////////////////////////////////////////////////////////////////
                        // Remove PVR file from list
                        // ////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.remove_from_pvr_record))) {
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_from_pvr_record);
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
                                            alertDialog.cancel();
                                            boolean pvrFileRemoved = false;
                                            // ///////////////////////////////////////////////////////
                                            // Remove from favorite, recently
                                            // and
                                            // local pvr list
                                            // ///////////////////////////////////////////////////////
                                            try {
                                                MainActivity.service
                                                        .getContentListControl()
                                                        .removeContentFromFavorites(
                                                                content);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // ///////////////////////////////
                                            // Remove from PVR list
                                            // ///////////////////////////////
                                            int index = INDEX_LOOK_UP_TABLE_THREE_ROWS[position
                                                    % NUMBER_OF_ITEMS_IN_THREE_ROWS];
                                            try {
                                                MainActivity.service
                                                        .getPvrControl()
                                                        .deleteMedia(index);                                                
                                                MainActivity.service.getPvrControl().updateMediaList();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // Refresh graphics
                                            // if (pvrFileRemoved) {
                                            A4TVToast toast = new A4TVToast(
                                                    activity);
                                            toast.showToast(R.string.pvr_file_is_deleted);
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
                                                if (MultimediaFileBrowserHandler.fileBrowserNumberOfItems == 0) {
                                                    MultimediaHandler.pvrFileBrowserText
                                                            .setText(activity
                                                                    .getResources()
                                                                    .getString(
                                                                            R.string.multimedia_pvr_playlist));
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        // }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                        }
                        // ////////////////////////////////////////////////////////////////////////////
                        // Remove all PVR files from list
                        // ////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.remove_all_files_from_list))) {
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_all_files_from_list);
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
                                            alertDialog.cancel();
                                            boolean pvrListDeleted = false;
                                            // ///////////////////////////////////////////////////////
                                            // Remove from favorite, recently
                                            // and
                                            // local pvr list
                                            // ///////////////////////////////////////////////////////
                                            try {
                                                MainActivity.service
                                                        .getContentListControl()
                                                        .removeAllContentsFromFavorites(
                                                                content.getFilterType());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // ///////////////////////////////
                                            // Remove all from PVR list
                                            // ///////////////////////////////
                                            try {
                                                MainActivity.service
                                                        .getPvrControl()
                                                        .deleteMediaList();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // Refresh graphics
                                            // if(pvrListDeleted) {
                                            A4TVToast toast = new A4TVToast(
                                                    activity);
                                            toast.showToast(R.string.all_pvr_files_are_deleted);
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
                                                MultimediaHandler.pvrFileBrowserText
                                                        .setText(activity
                                                                .getResources()
                                                                .getString(
                                                                        R.string.multimedia_pvr_playlist));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            // }
                                        }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                        }
                        // //////////////////////////////////////////////////////////////////////////////
                        // Remove from recently list
                        // //////////////////////////////////////////////////////////////////////////////
                        if (vi.getTag().equals(
                                activity.getResources().getString(
                                        R.string.remove_from_recently))) {
                            // TODO
                            /**
                             * DO NOTHING WAIT AFTER CES
                             */
                            // Create alert dialog
                            alertDialog = new A4TVAlertDialog(activity);
                            alertDialog
                                    .setTitleOfAlertDialog(R.string.remove_from_recently);
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
                                            alertDialog.cancel();
                                        }
                                    });
                            // Show alert dialog
                            alertDialog.show();
                            // Close context dialog
                            dialogContext.cancel();
                            // try {
                            //
                            // //TODO CHECK THIS
                            // MainActivity.service.getContentListControl()
                            // .removeContentFromRecentlyList(content);
                            // } catch (RemoteException e) {
                            //
                            // e.printStackTrace();
                            // }
                            //
                            // // Refresh graphics
                            // try {
                            // MultimediaRecentlyHandler.multimediaRecentlyNumberOfItems
                            // = MainActivity.service
                            // .getContentListControl()
                            // .getRecenltyWatchedListSize();
                            // ((MainActivity) activity).getMultimediaHandler()
                            // .getMutlimediaFavoriteHandler().initData();
                            //
                            // // Handle focus
                            // if
                            // (MultimediaRecentlyHandler.multimediaRecentlyNumberOfItems
                            // > 0) {
                            // ((MainActivity) activity)
                            // .getMultimediaHandler()
                            // .getMultimediaRecentlyHandler()
                            // .focusActiveElement(0);
                            // } else {
                            // ((MainActivity) activity)
                            // .getMultimediaHandler()
                            // .getMultimediaFileBrowserFirstHandler()
                            // .getGridFileBrowser().requestFocus();
                            // }
                            //
                            // } catch (RemoteException e) {
                            //
                            // e.printStackTrace();
                            // }
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

    private class PlaylistAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return playlists.size();
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
            if (position == 0) {
                A4TVTextView textView = new A4TVTextView(activity);
                textView.setText(playlists.get(position).getName());
                textView.setLayoutParams(new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        MainActivity.dialogListElementHeight));
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                textView.setPadding(
                        (int) activity.getResources().getDimension(
                                R.dimen.padding_large), 0, 0, 0);
                return textView;
            } else {
                LinearLayout linearLayout = new LinearLayout(activity);
                A4TVTextView textViewPlaylistName = new A4TVTextView(activity);
                ImageView imageViewPlaylistType = new ImageView(activity);
                // set linear layout
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setWeightSum(5.0f);
                linearLayout.setLayoutParams(new AbsListView.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        MainActivity.dialogListElementHeight));
                linearLayout.setPadding((int) activity.getResources()
                        .getDimension(R.dimen.padding_large), 0, 0, 0);
                // add text views to linear layout
                linearLayout.addView(textViewPlaylistName);
                linearLayout.addView(imageViewPlaylistType);
                // set up text views
                textViewPlaylistName.setText(playlists.get(position).getName());
                textViewPlaylistName
                        .setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.MATCH_PARENT, 3.0f));
                textViewPlaylistName.setGravity(Gravity.LEFT
                        | Gravity.CENTER_VERTICAL);
                textViewPlaylistName.setId(PLAYLIST_NAME_ID);
                if (playlists.get(position).getType().equals("audio"))
                    imageViewPlaylistType
                            .setImageResource(R.drawable.multimedia_music_folder);
                else if (playlists.get(position).getType().equals("image"))
                    imageViewPlaylistType
                            .setImageResource(R.drawable.multimedia_photos_folder);
                else
                    imageViewPlaylistType
                            .setImageResource(R.drawable.multimedia_video_folder);
                imageViewPlaylistType
                        .setLayoutParams(new LinearLayout.LayoutParams(0,
                                LayoutParams.MATCH_PARENT, 2.0f));
                imageViewPlaylistType.setPadding(
                        (int) activity.getResources().getDimension(
                                R.dimen.padding_medium),
                        (int) activity.getResources().getDimension(
                                R.dimen.padding_medium),
                        (int) activity.getResources().getDimension(
                                R.dimen.padding_medium),
                        (int) activity.getResources().getDimension(
                                R.dimen.padding_medium));
                return linearLayout;
            }
        }
    }

    private String getMultimediaFileType() {
        String multimediaFileType = "";
        MultimediaContent fileContent;
        if (content instanceof MultimediaContent) {
            fileContent = (MultimediaContent) content;
            if (fileContent != null) {
                if (fileContent.getType() != null) {
                    if (fileContent.getType().toLowerCase().equals("file")) {
                        if (EXTENSIONS_VIDEO.contains(fileContent
                                .getExtension().toLowerCase())) {
                            multimediaFileType = "video";
                        } else if (EXTENSIONS_AUDIO.contains(fileContent
                                .getExtension().toLowerCase())) {
                            multimediaFileType = "audio";
                        } else if (EXTENSIONS_IMAGE.contains(fileContent
                                .getExtension().toLowerCase())) {
                            multimediaFileType = "image";
                        }
                    }
                }
            }
        }
        return multimediaFileType;
    }

    private void addItemToPlaylist(String multimediaFileType,
            String playlistName, MultimediaContent mContent) {
        A4TVToast toast = new A4TVToast(activity);
        try {
            MediaPlayer myMediaPlayer = MediaPlayer.create(activity,
                    Uri.parse(mContent.getFileURL()));
            int duration = 0;
            if (myMediaPlayer != null) {
                duration = myMediaPlayer.getDuration();
                myMediaPlayer.release();
            }
            if (multimediaFileType.equals("audio")) {
                MediaMetadataRetriever retriever = MultimediaGridHelper
                        .initMetadataRetriever(mContent.getFileURL());
                if (retriever != null) {
                    String title = MultimediaGridHelper.getMetadataInfo(
                            retriever,
                            MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String artist = MultimediaGridHelper.getMetadataInfo(
                            retriever,
                            MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    if (title == null && artist == null) {
                        if (MainActivity.service
                                .getContentListControl()
                                .getContentFilter(FilterType.MULTIMEDIA)
                                .addAudioItemToPlaylist(playlistName, "",
                                        mContent.getName(), duration,
                                        mContent.getFileURL())) {
                            toast.showToast(com.iwedia.gui.R.string.item_is_added_in_playlist);
                        } else {
                            toast.showToast(com.iwedia.gui.R.string.item_is_not_added_to_playlist);
                        }
                    } else {
                        if (MainActivity.service
                                .getContentListControl()
                                .getContentFilter(FilterType.MULTIMEDIA)
                                .addAudioItemToPlaylist(playlistName, artist,
                                        title, duration, mContent.getFileURL())) {
                            toast.showToast(com.iwedia.gui.R.string.item_is_added_in_playlist);
                        } else {
                            toast.showToast(com.iwedia.gui.R.string.item_is_not_added_to_playlist);
                        }
                    }
                } else {
                    if (MainActivity.service
                            .getContentListControl()
                            .getContentFilter(FilterType.MULTIMEDIA)
                            .addAudioItemToPlaylist(playlistName, "",
                                    mContent.getName(), duration,
                                    mContent.getFileURL())) {
                        toast.showToast(com.iwedia.gui.R.string.item_is_added_in_playlist);
                    } else {
                        toast.showToast(com.iwedia.gui.R.string.item_is_not_added_to_playlist);
                    }
                }
            } else if (multimediaFileType.equals("video")) {
                if (MainActivity.service
                        .getContentListControl()
                        .getContentFilter(FilterType.MULTIMEDIA)
                        .addVideoItemToPlaylist(playlistName,
                                mContent.getName(), duration,
                                mContent.getFileURL())) {
                    toast.showToast(com.iwedia.gui.R.string.item_is_added_in_playlist);
                } else {
                    toast.showToast(com.iwedia.gui.R.string.item_is_not_added_to_playlist);
                }
            } else {
                if (MainActivity.service
                        .getContentListControl()
                        .getContentFilter(FilterType.MULTIMEDIA)
                        .addImageItemToPlaylist(playlistName,
                                mContent.getName(), "", mContent.getFileURL())) {
                    toast.showToast(com.iwedia.gui.R.string.item_is_added_in_playlist);
                } else {
                    toast.showToast(com.iwedia.gui.R.string.item_is_not_added_to_playlist);
                }
            }
        } catch (Exception e) {
            toast.showToast(com.iwedia.gui.R.string.item_is_not_added_to_playlist);
            e.printStackTrace();
        }
    }
}
