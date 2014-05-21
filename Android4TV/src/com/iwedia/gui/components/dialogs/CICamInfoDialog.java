package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
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

import com.iwedia.dtv.ci.ApplicationInfo;
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
public class CICamInfoDialog extends A4TVDialog implements A4TVDialogInterface,
        android.view.View.OnClickListener, OnItemClickListener {
    private final static String TAG = "CICamInfoDialog";
    private A4TVTextView textViewOnTop;
    private ImageView imageLine;
    private Context ctx;
    /** Fields for list view */
    private ListView listCamView;
    private static CIAdapter adapter;
    private ArrayList<String> listCamMenuItems = new ArrayList<String>();

    public CICamInfoDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init();
    }

    public void loadCAMApplicationInfo() {
        listCamMenuItems.clear();
        int numberOfApp = 0;
        try {
            numberOfApp = MainActivity.service.getCIControl()
                    .getNumberOfApplications();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < numberOfApp; i++) {
            ApplicationInfo info = null;
            try {
                info = MainActivity.service.getCIControl()
                        .getApplicationInfo(i);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (info != null) {
                listCamMenuItems.add(info.getName());
            }
        }
        refresh();
        Log.d(TAG, "loadCAMApplicationInfo executed");
    }

    @Override
    public void show() {
        loadCAMApplicationInfo();
        listCamView = (ListView) findViewById(R.id.listViewAudioLanguage);
        adapter = new CIAdapter();
        listCamView.setAdapter(adapter);
        listCamView.setOnItemClickListener(this);
        listCamView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        listCamView.setScrollbarFadingEnabled(false);
        super.show();
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
        textViewOnTop.setText("Available CAMs");
        imageLine = (ImageView) findViewById(R.id.imageViewHorizLine);
        // get drawable from theme for image source
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        int backgroundID = atts.getResourceId(0, 0);
        imageLine.setBackgroundResource(backgroundID);
        atts.recycle();
        listCamView = (ListView) findViewById(R.id.listViewAudioLanguage);
        adapter = new CIAdapter();
        listCamView.setAdapter(adapter);
        listCamView.setOnItemClickListener(this);
        listCamView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        listCamView.setScrollbarFadingEnabled(false);
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
        Log.d(TAG, "OnItemClicked chosen " + arg2);
        try {
            MainActivity.service.getCIControl().open(arg2);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "ENTER MENU: " + (arg2));
    }

    /** Adapter for list view */
    private class CIAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listCamMenuItems.size();
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
            ((A4TVTextView) convertView)
                    .setText(listCamMenuItems.get(position));
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
