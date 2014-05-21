package com.iwedia.gui.content_list;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;

import java.util.ArrayList;

/**
 * OnLong press listener for content list tabs
 * 
 * @author Sasa Jagodin
 */
public class TabOnLongPress implements OnLongClickListener {
    /** Grid id */
    private int serviceListIndex = 0;
    /** Reference of main activity */
    private Activity activity;
    /** Small context dialog that has drop down items */
    private A4TVDialog dialogContext;

    /** Constructor 1 */
    public TabOnLongPress(Activity activity, int serviceListIndex) {
        super();
        // Take reference of main activity
        this.activity = activity;
        // Get filter type
        this.serviceListIndex = serviceListIndex;
    }

    @Override
    public boolean onLongClick(View arg0) {
        dialogContext = ((MainActivity) activity).getDialogManager()
                .getContextSmallDialog();
        // Show dialog for adding in favorite list
        // fill dialog with desired view
        if (dialogContext != null) {
            dialogContext
                    .setContentView(fillDialogWithElements(serviceListIndex));
            // set dialog size
            dialogContext.getWindow().getAttributes().width = MainActivity.dialogWidth / 2;
            dialogContext.getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
            // show drop down dialog
            dialogContext.show();
        }
        return true;
    }

    /**
     * Creates view for context dialog
     * 
     * @param allList_favoriteList
     *        add or remove from favourites
     * @return
     */
    private View fillDialogWithElements(final int serviceListIndex) {
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
        ArrayList<String> stringList = loadDropDownItems(serviceListIndex);
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
                    // Create service list
                    // ///////////////////////////////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.service_list_create))) {
                        MainActivity.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                        MainActivity.activity);
                                askDialog
                                        .setTitleOfAlertDialog("Create service list");
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
                                                    try {
                                                        String serviceListName = editText
                                                                .getText()
                                                                .toString();
                                                        if (MainActivity.service
                                                                .getServiceControl()
                                                                .createServiceList(
                                                                        serviceListName)) {
                                                            MainActivity.activity
                                                                    .getContentListHandler()
                                                                    .reinitFilterOptionArray();
                                                            new A4TVToast(
                                                                    activity)
                                                                    .showToast(R.string.service_list_created);
                                                        }
                                                        askDialog.cancel();
                                                    } catch (RemoteException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
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
                        dialogContext.cancel();
                    }
                    // ////////////////////////////////////////////////////
                    // Cancel context dialog
                    // ///////////////////////////////////////////////////
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.service_list_delete))) {
                        try {
                            MainActivity.service.getServiceControl()
                                    .deleteServiceList(serviceListIndex);
                            MainActivity.activity.getContentListHandler()
                                    .reinitFilterOptionArray();
                            MainActivity.activity.getContentListHandler()
                                    .filterContent(FilterType.ALL, false);
                            new A4TVToast(activity)
                                    .showToast(R.string.service_list_deleted);
                            dialogContext.cancel();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.service_list_rename))) {
                        MainActivity.activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                        MainActivity.activity);
                                askDialog
                                        .setTitleOfAlertDialog("Rename service list");
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
                                                    try {
                                                        String serviceListName = editText
                                                                .getText()
                                                                .toString();
                                                        if (MainActivity.service
                                                                .getServiceControl()
                                                                .renameList(
                                                                        serviceListIndex,
                                                                        serviceListName)) {
                                                            MainActivity.activity
                                                                    .getContentListHandler()
                                                                    .reinitFilterOptionArray();
                                                        }
                                                        MainActivity.activity
                                                                .getContentListHandler()
                                                                .filterContent(
                                                                        FilterType.ALL,
                                                                        false);
                                                        new A4TVToast(activity)
                                                                .showToast(R.string.service_list_renamed);
                                                        askDialog.cancel();
                                                    } catch (RemoteException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
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
                        dialogContext.cancel();
                    }
                    if (vi.getTag().equals(
                            activity.getResources().getString(
                                    R.string.service_list_sort))) {
                        new A4TVToast(activity)
                                .showToast(R.string.not_implemented);
                        dialogContext.cancel();
                    }
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
    ArrayList<String> loadDropDownItems(int filterType) {
        ArrayList<String> stringList = new ArrayList<String>();
        switch (filterType) {
            case FilterType.ALL:
            case FilterType.INPUTS:
            case FilterType.APPS:
            case FilterType.WIDGETS:
                stringList.add(activity.getResources().getString(
                        R.string.service_list_create));
                stringList.add(activity.getResources().getString(
                        R.string.cancel));
                break;
            default:
                stringList.add(activity.getResources().getString(
                        R.string.service_list_create));
                stringList.add(activity.getResources().getString(
                        R.string.service_list_delete));
                stringList.add(activity.getResources().getString(
                        R.string.service_list_rename));
                stringList.add(activity.getResources().getString(
                        R.string.service_list_sort));
                stringList.add(activity.getResources().getString(
                        R.string.cancel));
                break;
        }
        return stringList;
    }
}
