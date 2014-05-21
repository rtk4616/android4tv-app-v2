package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
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

import com.iwedia.comm.ISubtitleControl;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;

import java.util.ArrayList;

/**
 * Dialog for subtitle languages
 * 
 * @author Branimir Pavlovic
 */
public class SubtitleLanguageDialog extends A4TVDialog implements
        A4TVDialogInterface, OnItemClickListener {
    private final String TAG = "SubtitleLanguageDialog";
    private ListView listViewLanguages;
    private AdapterLanguages adapter;
    // //////////////////////////////////////////
    // Veljko Ilkic
    // //////////////////////////////////////////
    /** String contants */
    public static final String QAA = "qaa";
    public static final String ORIGINAL = "Original";
    // //////////////////////////////////////////
    // Veljko Ilkic
    // //////////////////////////////////////////
    private ArrayList<String> languagesAvailable = new ArrayList<String>();
    private Context ctx;
    private A4TVTextView textViewOnTop;
    private ImageView imageLine;

    public SubtitleLanguageDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        fillDialog();
        setDialogAttributes();
        init();
    }

    @Override
    public void fillDialog() {
        setContentView(R.layout.audio_language_dialog);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_F1:
            case KeyEvent.KEYCODE_CAPTIONS: {
                cancel();
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    // not needed here, attributes are passed by style
    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
    }

    @Override
    public void show() {
        ISubtitleControl subControl = null;
        try {
            subControl = MainActivity.service.getSubtitleControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (subControl != null) {
            int numberOfLanguages = 0;
            try {
                numberOfLanguages = subControl.getSubtitleTrackCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Number of subtitle languages " + numberOfLanguages);
            languagesAvailable.clear();
            for (int i = 0; i < numberOfLanguages; i++) {
                String track = null;
                try {
                    track = subControl.getSubtitleTrack(i);
                    // //////////////////////////////////
                    // Veljko Ilkic
                    // //////////////////////////////////
                    // if (track.equals(QAA)) {
                    // track = ORIGINAL;
                    // }
                    //
                    // // Capitalize first letter
                    // track = track.toLowerCase();
                    // track = track.substring(0, 1).toUpperCase()
                    // + track.substring(1);
                    // ///////////////////////////////////
                    // Veljko Ilkic
                    // ///////////////////////////////////
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (track != null) {
                    languagesAvailable.add(track);
                }
            }
            // Add NONE element
            languagesAvailable.add(ctx.getResources().getString(
                    R.string.subtitle_language_none));
            Log.d(TAG, "Subtitle languages " + languagesAvailable.toString());
            adapter.notifyDataSetChanged();
            if (languagesAvailable.size() == 1) {
                A4TVToast toast = new A4TVToast(ctx);
                toast.showToast(R.string.no_subtitle_language);
            } else {
                super.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int currentSubIndex = -1;
                        try {
                            currentSubIndex = MainActivity.service
                                    .getSubtitleControl()
                                    .getCurrentSubtitleTrackIndex();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (currentSubIndex >= 0
                                && currentSubIndex < languagesAvailable.size() - 1) {
                            // request focus to active subtitle
                            listViewLanguages.requestFocusFromTouch();
                            listViewLanguages.setSelection(currentSubIndex);
                        } else if (currentSubIndex == -1) {
                            listViewLanguages.requestFocusFromTouch();
                            listViewLanguages.setSelection(languagesAvailable
                                    .size() - 1);
                        }
                    }
                }, 100);
            }
        }
        // }
    }

    /** Take reference of list view and bind it with adapter */
    private void init() {
        listViewLanguages = (ListView) findViewById(R.id.listViewAudioLanguage);
        adapter = new AdapterLanguages();
        listViewLanguages.setAdapter(adapter);
        listViewLanguages.setOnItemClickListener(this);
        listViewLanguages.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        listViewLanguages.setScrollbarFadingEnabled(false);
        findViewById(R.id.linearLayoutDialogContent).setLayoutParams(
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0,
                        3.8f));
        textViewOnTop = (A4TVTextView) findViewById(R.id.aTVTextViewMessage);
        textViewOnTop.setText(R.string.choose_subtitle_language);
        textViewOnTop
                .setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        MainActivity.dialogListElementHeight));
        textViewOnTop.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textViewOnTop.setPadding(
                (int) ctx.getResources().getDimension(R.dimen.padding_medium),
                0, 0, 0);
        imageLine = (ImageView) findViewById(R.id.imageViewHorizLine);
        // get drawable from theme for image source
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        int backgroundID = atts.getResourceId(0, 0);
        imageLine.setBackgroundResource(backgroundID);
        atts.recycle();
    }

    // this is not needed here
    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }

    private class AdapterLanguages extends BaseAdapter {
        @Override
        public int getCount() {
            return languagesAvailable.size();
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
            if (convertView == null) {
                convertView = new A4TVTextView(ctx);
            }
            ((A4TVTextView) convertView).setText(languagesAvailable
                    .get(position));
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.i(TAG, "\n\nonItemClick--------------arg2= " + arg2
                + " languagesAvailable.size()=" + languagesAvailable.size());
        // Check for NONE element
        if (arg2 == languagesAvailable.size() - 1) { // No language
            cancel();
            Log.i(TAG, "\n\nonItemClick    NONE------------------");
            // hide subtitle
            try {
                MainActivity.activity.getSubtitleDialogView().hide();
                // MainActivity.service.getSubtitleControl().hide();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // MainActivity.subtitleON = false;
        } else {
            MainActivity.activity.getSubtitleDialogView().show(arg2);
            // MainActivity.showSubtitleDialog(arg2);
            // MainActivity.subtitleON = true;
        }
        SubtitleLanguageDialog.this.cancel();
    }
}
