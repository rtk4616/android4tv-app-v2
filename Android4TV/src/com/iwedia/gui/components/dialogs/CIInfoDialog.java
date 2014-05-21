package com.iwedia.gui.components.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.iwedia.comm.IDTVManagerProxy;
import com.iwedia.dtv.types.AspectRatioMode;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;

import java.util.ArrayList;

/**
 * CI info dialog
 * 
 * @author Branimir Pavlovic
 */
public class CIInfoDialog extends A4TVDialog implements A4TVDialogInterface,
        android.view.View.OnClickListener, OnItemClickListener {
    private A4TVTextView textViewOnTop, textViewMenuTop, textViewMenuBottom;
    private ImageView imageLine;
    private Context ctx;
    private Activity mActivity = null;
    private IDTVManagerProxy mService = null;
    private boolean mActivityKeyEvents = false;
    /** Fields for list view */
    private ListView listView;
    private static CIAdapter adapter;
    private ArrayList<String> listMenuItems = new ArrayList<String>();
    private int ssnb;

    public CIInfoDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        mActivity = MainActivity.activity;
        mService = MainActivity.service;
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init();
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
        mActivityKeyEvents = true;
    }

    public void setProxyService(IDTVManagerProxy service) {
        mService = service;
    }

    @Override
    public void show() {
        // Fix for late service creation in MainActivity
        if (MainActivity.service != null) {
            mService = MainActivity.service;
        }
        loadMenuItems();
        listView = (ListView) findViewById(R.id.listViewAudioLanguage);
        adapter = new CIAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        listView.setScrollbarFadingEnabled(false);
        super.show();
        listView.requestFocus();
        listView.requestFocusFromTouch();
    }

    private void loadMenuItems() {
        listMenuItems.clear();
        try {
            textViewOnTop.setText(mService.getCIControl().getTitle(ssnb));
            textViewMenuTop.setText(mService.getCIControl().getTopText(ssnb));
            int numberOfItems = mService.getCIControl().getNumberOfItems(ssnb);
            Log.d(TAG, "loadMenuItems - numberOfItems: " + numberOfItems);
            for (int i = 0; i < numberOfItems; i++) {
                String itemText = mService.getCIControl().getMenuItemText(ssnb,
                        i);
                Log.d(TAG, "loadMenuItems - itemText: " + itemText);
                addItem(itemText);
            }
            refresh();
            textViewMenuBottom.setText(mService.getCIControl().getBottomText(
                    ssnb));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "loadMenuItems executed");
    }

    private void addItem(String itemName) {
        if (listMenuItems.size() == 0) {
            listMenuItems.add("..");
        }
        listMenuItems.add(itemName);
        adapter.notifyDataSetChanged();
        listView.invalidateViews();
        listView.requestFocus();
        listView.requestFocusFromTouch();
    }

    /** Show dialog from callback */
    public void showDialog(int ssnb) {
        this.ssnb = ssnb;
        if (isShowing()) {
            // just refresh adapter
            loadMenuItems();
        } else {
            show();
        }
    }

    /** Close dialog from callback */
    public void cancelDialog() {
        if (mActivityKeyEvents) {
            mActivity.onBackPressed();
        } else {
            CIInfoDialog.this.cancel();
            listMenuItems.clear();
            listMenuItems.add("..");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "KeyHandling Ci Info dialog : " + keyCode);
        // BACK and ESC key fix for CIDialogActivity
        if (mActivityKeyEvents
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == 111)) {
            mActivity.onBackPressed();
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case 111: // ESC
                super.onBackPressed();
                try {
                    Log.d(TAG, "OnItemClicked chosen back");
                    cancelDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "SEND MENU ANSWER TO CI: 0");
                // super.onBackPressed();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                try {
                    Log.d(TAG, "OnItemClicked chosen back");
                    MainActivity.service.getCIControl().selectMenuItem(
                            this.ssnb, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "SEND MENU ANSWER TO CI: 0");
                // super.onBackPressed();
                break;
            case KeyEvent.KEYCODE_WINDOW: {
                AspectRatioMode aspectRatio = AspectRatioMode.AUTO;
                try {
                    aspectRatio = mService.getSystemControl()
                            .getPictureControl().getAspectRatioMode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (aspectRatio == AspectRatioMode.AUTO) {
                    try {
                        mService.getSystemControl().getPictureControl()
                                .setAspectRatioMode(AspectRatioMode.NORMAL_4_3);
                        // aspectRatio = 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mService.getSystemControl().getPictureControl()
                                .setAspectRatioMode(AspectRatioMode.AUTO);
                        // aspectRatio = 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
        return true;
    }

    /** Initialization function */
    private void init() {
        textViewOnTop = (A4TVTextView) findViewById(R.id.aTVTextViewMessage);
        textViewOnTop
                .setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        MainActivity.dialogListElementHeight));
        textViewOnTop.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textViewOnTop.setPadding(
                (int) ctx.getResources().getDimension(R.dimen.padding_small),
                0, 0, 0);
        textViewOnTop.setText("");
        // menu top text view
        textViewMenuTop = (A4TVTextView) findViewById(R.id.aTVTextViewMenuTop);
        textViewMenuTop.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textViewMenuTop.setVisibility(View.VISIBLE);
        textViewMenuTop.setText("");
        // menu bottom text view
        textViewMenuBottom = (A4TVTextView) findViewById(R.id.aTVTextViewMenuBottom);
        textViewMenuBottom.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textViewMenuBottom.setVisibility(View.VISIBLE);
        textViewMenuBottom.setText("");
        imageLine = (ImageView) findViewById(R.id.imageViewHorizLine);
        // get drawable from theme for image source
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        int backgroundID = atts.getResourceId(0, 0);
        imageLine.setBackgroundResource(backgroundID);
        atts.recycle();
        listView = (ListView) findViewById(R.id.listViewAudioLanguage);
        adapter = new CIAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        listView.setScrollbarFadingEnabled(false);
    }

    @Override
    public void fillDialog() {
        setContentView(R.layout.audio_language_dialog);
    }

    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            Log.d(TAG, "OnItemClicked chosen " + arg2);
            mService.getCIControl().selectMenuItem(ssnb, arg2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // listMenuItems.clear();
        // listMenuItems.add("..");
        Log.d(TAG, "SEND MENU ANSWER TO CI: " + (arg2));
    }

    /** Adapter for list view */
    private class CIAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listMenuItems.size();
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
            convertView = new A4TVTextView(ctx);
            ((A4TVTextView) convertView).setText(listMenuItems.get(position));
            ((A4TVTextView) convertView).setTextColor(Color.WHITE);
            (convertView).setLayoutParams(new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    MainActivity.dialogListElementHeight));
            ((A4TVTextView) convertView).setGravity(Gravity.LEFT
                    | Gravity.CENTER_VERTICAL);
            (convertView).setPadding(
                    (int) ctx.getResources()
                            .getDimension(R.dimen.padding_large), 0, 0, 0);
            (convertView).setBackgroundResource(R.drawable.list_view_selector);
            return convertView;
        }
    }

    public static void refresh() {
        Log.d(TAG, "refresh");
        adapter.notifyDataSetChanged();
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialog });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }
}
