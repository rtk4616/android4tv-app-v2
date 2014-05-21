package com.iwedia.gui.content_list;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.iwedia.comm.IParentalControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.comm.content.inputs.InputContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVPasswordDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.FavoriteListDialog;
import com.iwedia.gui.components.dialogs.PasswordSecurityDialog;
import com.iwedia.gui.content_list.AllHandler.AllGridAdapter;
import com.iwedia.gui.content_list.FavoriteHandler.FavoriteGridAdapter;
import com.iwedia.gui.content_list.RecentlyHandler.RecentlyGridAdapter;

import java.util.ArrayList;

/**
 * OnLong press listener for content list items
 * 
 * @author Veljko Ilkic
 */
public class GridOnLongPress implements OnItemLongClickListener {
    /** Grid id contents */
    public static final int ALL_CONTENT_LIST = 0;
    public static final int FAVORITE_CONTENT_LIST = 1;
    public static final int RECENTLY_CONTENT_LIST = 2;
    /** Grid id */
    private int gridId = ALL_CONTENT_LIST;
    /** Reference of main activity */
    private Activity activity;
    /** Content object of clicked item */
    private Content content;
    /** Small context dialog that has drop down items */
    private A4TVDialog dialogContext;
    /** Favorite grid adapter reference */
    private FavoriteGridAdapter favoriteGridAdapter;
    /** All grid adapter reference */
    private AllGridAdapter allGridAdapter;
    /** Recently grid adapter reference */
    private RecentlyGridAdapter recentlyGridAdapter;
    /** Content list control interface */
    private IContentListControl contentListControl = null;
    /** Parental control interface */
    private IParentalControl parentalControl = null;
    /** Password dialog for locking channels */
    private A4TVPasswordDialog passwordAlertDialog;
    /** Edit text for password input */
    private A4TVEditText editTextEnteredPin;
    /** Alert dialog */
    private A4TVAlertDialog alertDialog;

    /** Constructor 1 */
    public GridOnLongPress(Activity activity, int gridId) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Get grid id
        this.gridId = gridId;
        // Take favorite adapter
        this.favoriteGridAdapter = ((MainActivity) activity)
                .getContentListHandler().getFavoriteHandler()
                .getFavoriteGridAdapter();
        if (gridId == ALL_CONTENT_LIST) {
            // Take all adapter
            this.allGridAdapter = ((MainActivity) activity)
                    .getContentListHandler().getAllHandler()
                    .getAllGridAdapter();
        }
        if (gridId == RECENTLY_CONTENT_LIST) {
            // Take recently adapter
            this.recentlyGridAdapter = ((MainActivity) activity)
                    .getContentListHandler().getRecentlyHandler()
                    .getRecentlyGridAdapter();
        }
        // Create password dialog
        createDialogForPasswordInput();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        // Get content from all list
        if (gridId == ALL_CONTENT_LIST) {
            content = (Content) allGridAdapter.getItem(arg2);
        }
        // Get content from favorite list
        if (gridId == FAVORITE_CONTENT_LIST) {
            content = (Content) favoriteGridAdapter.getItem(arg2);
        }
        // Get content from recently list
        if (gridId == RECENTLY_CONTENT_LIST) {
            content = (Content) recentlyGridAdapter.getItem(arg2);
        }
        if (content != null) {
            dialogContext = ((MainActivity) activity).getDialogManager()
                    .getContextSmallDialog();
            // Show dialog for adding in favorite list
            // fill dialog with desired view
            if (dialogContext != null) {
                dialogContext.setContentView(fillDialogWithElements(gridId));
                // set dialog size
                dialogContext.getWindow().getAttributes().width = MainActivity.dialogWidth / 2;
                dialogContext.getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
                // show drop down dialog
                dialogContext.show();
            }
        }
        return true;
    }

    /** Create password dialog */
    private void createDialogForPasswordInput() {
        // Show password dialog
        passwordAlertDialog = new A4TVPasswordDialog(activity, true);
        editTextEnteredPin = passwordAlertDialog.getEditText1();
        passwordAlertDialog.getEditText2().setVisibility(View.GONE);
        passwordAlertDialog.getEditText3().setVisibility(View.GONE);
    }

    /**
     * Creates view for context dialog
     * 
     * @param allList_favoriteList
     *        add or remove from favourites
     * @return
     */
    private View fillDialogWithElements(int gridId) {
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
        /** GET FIELDS FOR CREATING DROP DOWN ITEMS */
        ArrayList<String> stringList = loadDropDownItems(gridId);
        for (int i = 0; i < stringList.size(); i++) {
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
            button.setText(stringList.get(i));
            button.setGravity(Gravity.CENTER);
            button.setId(i);
            // for creating difference between first buttons
            button.setTag(stringList.get(i));
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View vi) {
                    // ///////////////////////////////////////////////////////////////////////////
                    // Add to favorites
                    // ///////////////////////////////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.add_to_favourites))) {
                        // ///////////////////////////////////
                        // Add content to favorite
                        // ///////////////////////////////////
                        boolean addedInFavoriteList = false;
                        try {
                            addedInFavoriteList = contentListControl
                                    .addContentToFavorites(content);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        // ////////////////////////////////////
                        // Refresh graphics if needed
                        // ////////////////////////////////////
                        if (addedInFavoriteList) {
                            // Refresh graphics
                            try {
                                FavoriteHandler.favoriteNumberOfItems = contentListControl
                                        .getFavoritesSize();
                                ((MainActivity) activity)
                                        .getContentListHandler()
                                        .getFavoriteHandler().initData();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // /////////////////////////////////////////////
                            // Show message that items is already there
                            // //////////////////////////////////////////////
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(com.iwedia.gui.R.string.already_in_favorites);
                        }
                        // Close context dialog
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////
                    // Remove from favorites
                    // ///////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.remove_from_favourites))) {
                        // Create alert dialog
                        alertDialog = new A4TVAlertDialog(activity);
                        alertDialog
                                .setTitleOfAlertDialog(R.string.remove_from_favourites);
                        alertDialog.setNegativeButton(R.string.button_text_no,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.cancel();
                                    }
                                });
                        alertDialog.setPositiveButton(R.string.button_text_yes,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            contentListControl
                                                    .removeContentFromFavorites(content);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        // Refresh graphics
                                        try {
                                            FavoriteHandler.favoriteNumberOfItems = contentListControl
                                                    .getFavoritesSize();
                                            ((MainActivity) activity)
                                                    .getContentListHandler()
                                                    .getFavoriteHandler()
                                                    .initData();
                                            // Handle focus
                                            if (FavoriteHandler.favoriteNumberOfItems > 0) {
                                                ((MainActivity) activity)
                                                        .getContentListHandler()
                                                        .getFavoriteHandler()
                                                        .focusActiveElement(0);
                                            } else {
                                                ((MainActivity) activity)
                                                        .getContentListHandler()
                                                        .getAllHandler()
                                                        .getGridAll()
                                                        .requestFocus();
                                            }
                                        } catch (RemoteException e) {
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
                    // ///////////////////////////////////////////////////
                    // Remove from recently
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.remove_from_recently))) {
                        // Create alert dialog
                        alertDialog = new A4TVAlertDialog(activity);
                        alertDialog
                                .setTitleOfAlertDialog(R.string.remove_from_recently);
                        alertDialog.setNegativeButton(R.string.button_text_no,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.cancel();
                                    }
                                });
                        alertDialog.setPositiveButton(R.string.button_text_yes,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        boolean isRemoved = false;
                                        try {
                                            isRemoved = contentListControl
                                                    .removeContentFromRecentlyList(content);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        if (isRemoved) {
                                            // Refresh graphics
                                            try {
                                                RecentlyHandler.recentlyNumberOfItems = contentListControl
                                                        .getRecenltyWatchedListSize();
                                                ((MainActivity) activity)
                                                        .getContentListHandler()
                                                        .getRecentlyHandler()
                                                        .initData();
                                                // Handle focus
                                                if (RecentlyHandler.recentlyNumberOfItems > 0) {
                                                    ((MainActivity) activity)
                                                            .getContentListHandler()
                                                            .getRecentlyHandler()
                                                            .focusActiveElement(
                                                                    0);
                                                } else {
                                                    ((MainActivity) activity)
                                                            .getContentListHandler()
                                                            .getAllHandler()
                                                            .getGridAll()
                                                            .requestFocus();
                                                }
                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            // Recently content isn't removed
                                            new A4TVToast(activity)
                                                    .showToast(R.string.cant_remove_from_recently);
                                        }
                                        alertDialog.cancel();
                                    }
                                });
                        // Show alert dialog
                        alertDialog.show();
                        // Close context dialog
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Lock channel
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.lock_channel))) {
                        // //////////////////////////
                        // Enter password
                        // //////////////////////////
                        passwordAlertDialog
                                .setTitleOfAlertDialog(R.string.lock_channel);
                        passwordAlertDialog.setPositiveButton(
                                R.string.parental_control_ok,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String enteredPin = editTextEnteredPin
                                                .getText().toString();
                                        // Check valid pin
                                        boolean isPinValid = false;
                                        try {
                                            isPinValid = parentalControl.checkPinCode(Integer
                                                    .parseInt(enteredPin));
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        if (isPinValid) {
                                            if (content.getFilterType() == FilterType.APPS
                                                    || content.getFilterType() == FilterType.WIDGETS
                                                    || content.getFilterType() == FilterType.IP_STREAM) {
                                                // Lock app or widget or IP
                                                // stream
                                                try {
                                                    contentListControl
                                                            .setContentLockStatus(
                                                                    content,
                                                                    true);
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                // Lock channel
                                                try {
                                                    parentalControl.setChannelLock(
                                                            content.getIndexInMasterList(),
                                                            true);
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            // Invalidate content list
                                            MainActivity.activity
                                                    .getContentListHandler()
                                                    .getRecentlyHandler()
                                                    .getRecentlyGridAdapter()
                                                    .notifyDataSetChanged();
                                            MainActivity.activity
                                                    .getContentListHandler()
                                                    .getFavoriteHandler()
                                                    .getFavoriteGridAdapter()
                                                    .notifyDataSetChanged();
                                            MainActivity.activity
                                                    .getContentListHandler()
                                                    .getAllHandler()
                                                    .getAllGridAdapter()
                                                    .notifyDataSetChanged();
                                            passwordAlertDialog.cancel();
                                        } else {
                                            editTextEnteredPin.setText("");
                                            // Request focus back on edit text
                                            editTextEnteredPin.requestFocus();
                                            PasswordSecurityDialog
                                                    .wrongPasswordEntered(
                                                            passwordAlertDialog,
                                                            false);
                                            passwordAlertDialog
                                                    .getPositiveButton()
                                                    .setEnabled(false);
                                            A4TVToast toast = new A4TVToast(
                                                    activity);
                                            toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_error_message);
                                        }
                                    }
                                });
                        passwordAlertDialog.setNegativeButton(
                                R.string.button_text_cancel,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        passwordAlertDialog.cancel();
                                    }
                                });
                        // There is no â€œNo Attemptâ€� period
                        // activated
                        PasswordSecurityDialog
                                .wrongPasswordEntered(null, false);
                        if (!PasswordSecurityDialog.waitFor10Minutes) {
                            passwordAlertDialog.show();
                            editTextEnteredPin.setText("");
                            editTextEnteredPin.requestFocus();
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Show message
                                    A4TVToast toast = new A4TVToast(activity);
                                    toast.showToast(R.string.enter_password_no_more_attempts_active);
                                }
                            }, 1500);
                        }
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Unlock channel
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.unlock_channel))) {
                        // //////////////////////////
                        // Enter password
                        // //////////////////////////
                        passwordAlertDialog
                                .setTitleOfAlertDialog(R.string.unlock_channel);
                        passwordAlertDialog.setPositiveButton(
                                R.string.parental_control_ok,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String enteredPin = editTextEnteredPin
                                                .getText().toString();
                                        // Check valid pin
                                        boolean isPinValid = false;
                                        try {
                                            isPinValid = parentalControl.checkPinCode(Integer
                                                    .parseInt(enteredPin));
                                        } catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        if (isPinValid) {
                                            if (content.getFilterType() == FilterType.APPS
                                                    || content.getFilterType() == FilterType.WIDGETS
                                                    || content.getFilterType() == FilterType.IP_STREAM) {
                                                // Unlock app or widget or IP
                                                // stream
                                                try {
                                                    contentListControl
                                                            .setContentLockStatus(
                                                                    content,
                                                                    false);
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                // Unlock channel
                                                try {
                                                    parentalControl.setChannelLock(
                                                            content.getIndexInMasterList(),
                                                            false);
                                                    // Check if current channel
                                                    // is locked and active
                                                    if (content
                                                            .equals(((MainActivity) activity)
                                                                    .getPageCurl()
                                                                    .getChannelChangeHandler()
                                                                    .getActiveContent())) {
                                                        ((MainActivity) activity)
                                                                .getCheckServiceType()
                                                                .unlockService();
                                                    }
                                                } catch (RemoteException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            // Invalidate content list
                                            MainActivity.activity
                                                    .getContentListHandler()
                                                    .getRecentlyHandler()
                                                    .getRecentlyGridAdapter()
                                                    .notifyDataSetChanged();
                                            MainActivity.activity
                                                    .getContentListHandler()
                                                    .getFavoriteHandler()
                                                    .getFavoriteGridAdapter()
                                                    .notifyDataSetChanged();
                                            MainActivity.activity
                                                    .getContentListHandler()
                                                    .getAllHandler()
                                                    .getAllGridAdapter()
                                                    .notifyDataSetChanged();
                                            passwordAlertDialog.cancel();
                                        } else {
                                            editTextEnteredPin.setText("");
                                            // Request focus back on edit text
                                            editTextEnteredPin.requestFocus();
                                            PasswordSecurityDialog
                                                    .wrongPasswordEntered(
                                                            passwordAlertDialog,
                                                            false);
                                            passwordAlertDialog
                                                    .getPositiveButton()
                                                    .setEnabled(false);
                                            A4TVToast toast = new A4TVToast(
                                                    activity);
                                            toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_error_message);
                                        }
                                    }
                                });
                        passwordAlertDialog.setNegativeButton(
                                R.string.button_text_cancel,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        passwordAlertDialog.cancel();
                                    }
                                });
                        // There is no â€œNo Attemptâ€� period
                        // activated
                        PasswordSecurityDialog
                                .wrongPasswordEntered(null, false);
                        if (!PasswordSecurityDialog.waitFor10Minutes) {
                            passwordAlertDialog.show();
                            editTextEnteredPin.setText("");
                            editTextEnteredPin.requestFocus();
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Show message
                                    A4TVToast toast = new A4TVToast(activity);
                                    toast.showToast(R.string.enter_password_no_more_attempts_active);
                                }
                            }, 1500);
                        }
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Disable input
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.disable_input))) {
                        try {
                            contentListControl.setContentLockStatus(content,
                                    true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        MainActivity.activity.getContentListHandler()
                                .getRecentlyHandler().getRecentlyGridAdapter()
                                .notifyDataSetChanged();
                        MainActivity.activity.getContentListHandler()
                                .getFavoriteHandler().getFavoriteGridAdapter()
                                .notifyDataSetChanged();
                        MainActivity.activity.getContentListHandler()
                                .getAllHandler().getAllGridAdapter()
                                .notifyDataSetChanged();
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Enable input
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.enable_input))) {
                        try {
                            contentListControl.setContentLockStatus(content,
                                    false);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        MainActivity.activity.getContentListHandler()
                                .getRecentlyHandler().getRecentlyGridAdapter()
                                .notifyDataSetChanged();
                        MainActivity.activity.getContentListHandler()
                                .getFavoriteHandler().getFavoriteGridAdapter()
                                .notifyDataSetChanged();
                        MainActivity.activity.getContentListHandler()
                                .getAllHandler().getAllGridAdapter()
                                .notifyDataSetChanged();
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Rename
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.rename_content))) {
                        if (content.getFilterType() == FilterType.INPUTS) {
                            MainActivity.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                            MainActivity.activity);
                                    askDialog.setTitleOfAlertDialog(
                                            "Rename content").setCancelable(
                                            true);
                                    final A4TVEditText editText = new A4TVEditText(
                                            askDialog.getContext());
                                    editText.setEms(40);
                                    askDialog.setView(editText);
                                    askDialog.setCancelable(true);
                                    editText.setOnKeyListener(new View.OnKeyListener() {
                                        @Override
                                        public boolean onKey(View v,
                                                int keyCode, KeyEvent event) {
                                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                                switch (keyCode) {
                                                    case KeyEvent.KEYCODE_DPAD_CENTER:
                                                    case KeyEvent.KEYCODE_ENTER:
                                                        String name = editText
                                                                .getText()
                                                                .toString();
                                                        if (name.isEmpty() != true) {
                                                            Content inputContent;
                                                            int inputDeviceIdx;
                                                            int activeFilter;
                                                            boolean alreadyExist = false;
                                                            try {
                                                                activeFilter = MainActivity.service
                                                                        .getContentListControl()
                                                                        .getActiveFilterIndex();
                                                                contentListControl
                                                                        .setActiveFilter(FilterType.INPUTS);
                                                                for (inputDeviceIdx = 0; (inputDeviceIdx <= contentListControl
                                                                        .getContentListSize() && (alreadyExist == false)); inputDeviceIdx++) {
                                                                    if (inputDeviceIdx > 0) {
                                                                        inputContent = contentListControl
                                                                                .getContent(inputDeviceIdx - 1);
                                                                    } else {
                                                                        inputContent = contentListControl
                                                                                .getContent(inputDeviceIdx);
                                                                    }
                                                                    if (name.equals(inputContent
                                                                            .getName())) {
                                                                        alreadyExist = true;
                                                                    }
                                                                }
                                                                contentListControl
                                                                        .setActiveFilter(activeFilter);
                                                            } catch (RemoteException e) {
                                                                e.printStackTrace();
                                                            }
                                                            if (alreadyExist == false) {
                                                                try {
                                                                    contentListControl
                                                                            .renameContent(
                                                                                    content,
                                                                                    name);
                                                                    MainActivity.activity
                                                                            .getContentListHandler()
                                                                            .filterContent(
                                                                                    FilterType.INPUTS,
                                                                                    false);
                                                                } catch (RemoteException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            } else {
                                                                A4TVToast toast = new A4TVToast(
                                                                        activity);
                                                                toast.showToast(R.string.already_exist);
                                                            }
                                                        } else {
                                                            A4TVToast toast = new A4TVToast(
                                                                    activity);
                                                            toast.showToast(R.string.name_error);
                                                        }
                                                        askDialog.cancel();
                                                        break;
                                                    case KeyEvent.KEYCODE_BACK:
                                                    case KeyEvent.KEYCODE_DEL:
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
                            MainActivity.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                            MainActivity.activity);
                                    askDialog.setTitleOfAlertDialog(
                                            "Rename service").setCancelable(
                                            true);
                                    final A4TVEditText editText = new A4TVEditText(
                                            askDialog.getContext());
                                    editText.setEms(40);
                                    askDialog.setView(editText);
                                    askDialog.setCancelable(true);
                                    editText.setOnKeyListener(new View.OnKeyListener() {
                                        @Override
                                        public boolean onKey(View v,
                                                int keyCode, KeyEvent event) {
                                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                                switch (keyCode) {
                                                    case KeyEvent.KEYCODE_DPAD_CENTER:
                                                    case KeyEvent.KEYCODE_ENTER:
                                                        String name = editText
                                                                .getText()
                                                                .toString();
                                                        if (name.isEmpty() != true) {
                                                            try {
                                                                contentListControl
                                                                        .renameContent(
                                                                                content,
                                                                                name);
                                                            } catch (RemoteException e) {
                                                                e.printStackTrace();
                                                            }
                                                            // Filter current
                                                            // filter
                                                            MainActivity.activity
                                                                    .getContentListHandler()
                                                                    .filterContent(
                                                                            content.getServiceListIndex(),
                                                                            true);
                                                        } else {
                                                            A4TVToast toast = new A4TVToast(
                                                                    activity);
                                                            toast.showToast(R.string.name_error);
                                                        }
                                                        askDialog.cancel();
                                                        break;
                                                    case KeyEvent.KEYCODE_BACK:
                                                    case KeyEvent.KEYCODE_DEL:
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
                        }
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Add to favorite list
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.add_to_favorite_list))) {
                        FavoriteListDialog favoriteDialog = MainActivity.activity
                                .getDialogManager().getFavoriteListDialog();
                        if (favoriteDialog != null) {
                            favoriteDialog.show();
                            favoriteDialog.setContent(content);
                        }
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Move service in list
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.service_list_move_service))) {
                        MainActivity.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                        MainActivity.activity);
                                askDialog.setTitleOfAlertDialog(
                                        "Move service to position:")
                                        .setCancelable(true);
                                final A4TVEditText editText = new A4TVEditText(
                                        askDialog.getContext());
                                editText.setEms(5);
                                askDialog.setView(editText);
                                askDialog.setCancelable(true);
                                editText.setOnKeyListener(new View.OnKeyListener() {
                                    @Override
                                    public boolean onKey(View v, int keyCode,
                                            KeyEvent event) {
                                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                            switch (keyCode) {
                                                case KeyEvent.KEYCODE_DPAD_CENTER:
                                                case KeyEvent.KEYCODE_ENTER:
                                                    Integer movedServiceIndex = Integer
                                                            .parseInt(editText
                                                                    .getText()
                                                                    .toString());
                                                    int listSize = 0;
                                                    try {
                                                        listSize = contentListControl
                                                                .getContentListSize();
                                                    } catch (RemoteException e1) {
                                                        // TODO Auto-generated
                                                        // catch block
                                                        e1.printStackTrace();
                                                    }
                                                    if ((movedServiceIndex > listSize)
                                                            || (movedServiceIndex < 1)) {
                                                        new A4TVToast(activity)
                                                                .showToast(R.string.service_list_index_out_of_range);
                                                    } else {
                                                        try {
                                                            MainActivity.service
                                                                    .getServiceControl()
                                                                    .movePointedService(
                                                                            content.getServiceListIndex(),
                                                                            content.getIndex(),
                                                                            movedServiceIndex);
                                                        } catch (RemoteException e) {
                                                            e.printStackTrace();
                                                        }
                                                        new A4TVToast(activity)
                                                                .showToast(R.string.service_list_service_moved);
                                                    }
                                                    // Filter current
                                                    // filter
                                                    MainActivity.activity
                                                            .getContentListHandler()
                                                            .filterContent(
                                                                    content.getServiceListIndex(),
                                                                    true);
                                                    askDialog.cancel();
                                                    break;
                                                case KeyEvent.KEYCODE_BACK:
                                                case KeyEvent.KEYCODE_DEL:
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
                        dialogContext.cancel();
                    }
                    // ///////////////////////////////////////////////////
                    // Remove from favorite list
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.remove_from_favorite_list))) {
                        try {
                            int currentListIndex = MainActivity.service
                                    .getContentListControl()
                                    .getActiveFilterIndex();
                            MainActivity.service.getContentListControl()
                                    .removeContentFromFavoritesList(
                                            currentListIndex, content);
                            MainActivity.activity.getContentListHandler()
                                    .filterContent(currentListIndex, false);
                            // MainActivity.activity.getContentListHandler().showContentList();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        dialogContext.cancel();
                    }
                    // ////////////////////////////////////////////////////
                    // Cancel context dialog
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(R.string.cancel))) {
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
                    TypedArray atts = activity.getTheme()
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
            if (i < stringList.size() - 1) {
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

    /**
     * Load strings for long click drop down menu
     * 
     * @param gridID
     *        ID of selected grid
     * @return ArrayList of strings
     */
    ArrayList<String> loadDropDownItems(int gridID) {
        ArrayList<String> stringList = new ArrayList<String>();
        boolean isContentLocked = false;
        try {
            contentListControl = MainActivity.service.getContentListControl();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        try {
            parentalControl = MainActivity.service.getParentalControl();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        // //////////////////////////////////////////////
        // Check if content is APP, WIDGET, IP STREAM or INPUT
        // //////////////////////////////////////////////
        if (content.getFilterType() == FilterType.APPS
                || content.getFilterType() == FilterType.WIDGETS
                || content.getFilterType() == FilterType.IP_STREAM
                || content.getFilterType() == FilterType.INPUTS) {
            try {
                isContentLocked = contentListControl
                        .getContentLockedStatus(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // //////////////////////////////////////////////
        // For services use ParentalControl
        // //////////////////////////////////////////////
        else {
            try {
                isContentLocked = parentalControl.getChannelLock(content
                        .getIndexInMasterList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String lockedStringOption;
        if (content.getFilterType() == FilterType.INPUTS) {
            if (isContentLocked) {
                lockedStringOption = activity.getResources().getString(
                        R.string.enable_input);
            } else {
                lockedStringOption = activity.getResources().getString(
                        R.string.disable_input);
            }
        } else {
            if (isContentLocked) {
                lockedStringOption = activity.getResources().getString(
                        R.string.unlock_channel);
            } else {
                lockedStringOption = activity.getResources().getString(
                        R.string.lock_channel);
            }
        }
        switch (gridID) {
            case ALL_CONTENT_LIST:
                stringList.add(activity.getResources().getString(
                        R.string.add_to_favourites));
                /**
                 * If current filter is not favorite list, offer adding in
                 * favorite, otherwise offer removing from content list
                 */
                try {
                    if (contentListControl.getActiveFilterIndex() == FilterType.ALL) {
                        stringList.add(activity.getResources().getString(
                                R.string.add_to_favorite_list));
                    } else {
                        stringList.add(activity.getResources().getString(
                                R.string.remove_from_favorite_list));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
                stringList.add(lockedStringOption);
                if ((content instanceof ServiceContent && content
                        .getFilterType() != FilterType.ALL)
                        || (content instanceof InputContent)) {
                    stringList.add(activity.getResources().getString(
                            R.string.rename_content));
                }
                if (content instanceof ServiceContent
                        && content.getFilterType() != FilterType.ALL) {
                    stringList.add(activity.getResources().getString(
                            R.string.service_list_move_service));
                }
                stringList.add(activity.getResources().getString(
                        R.string.cancel));
                break;
            case FAVORITE_CONTENT_LIST:
                stringList.add(activity.getResources().getString(
                        R.string.remove_from_favourites));
                stringList.add(lockedStringOption);
                stringList.add(activity.getResources().getString(
                        R.string.cancel));
                break;
            case RECENTLY_CONTENT_LIST:
                stringList.add(activity.getResources().getString(
                        R.string.remove_from_recently));
                stringList.add(activity.getResources().getString(
                        R.string.add_to_favourites));
                stringList.add(lockedStringOption);
                stringList.add(activity.getResources().getString(
                        R.string.cancel));
                break;
            default:
                break;
        }
        return stringList;
    }
}
