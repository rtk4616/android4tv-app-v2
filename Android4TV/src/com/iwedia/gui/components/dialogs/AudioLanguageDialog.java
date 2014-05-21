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

import com.iwedia.comm.IAudioControl;
import com.iwedia.dtv.audio.AudioTrack;
import com.iwedia.dtv.types.AudioChannelConfiguration;
import com.iwedia.dtv.types.AudioDigitalType;
import com.iwedia.dtv.types.AudioTrackType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;

import java.util.ArrayList;

/**
 * Dialog for audio languages
 * 
 * @author Branimir Pavlovic
 */
public class AudioLanguageDialog extends A4TVDialog implements
        A4TVDialogInterface, OnItemClickListener {
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
    private ArrayList<AudioTrack> languagesAvailable = new ArrayList<AudioTrack>();
    private Context ctx;
    private A4TVTextView textViewOnTop;
    private ImageView imageLine;
    private IAudioControl audioControl;

    public AudioLanguageDialog(Context context) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F6:
            case KeyEvent.KEYCODE_A: {
                cancel();
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
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

    // not needed here, attributes are passed by style
    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
        // if (MainActivity.screenWidth == WIDTH_720p
        // || MainActivity.screenHeight == WIDTH_720p) {
        // getWindow().getAttributes().height = HEIGHT_720p / 2;
        // getWindow().getAttributes().width = WIDTH_720p / 4;
        //
        // getWindow().getAttributes().x = -WIDTH_720p / 2;
        // getWindow().getAttributes().y = 0;
        // }
        // if (MainActivity.screenWidth == WIDTH_1080p
        // || MainActivity.screenHeight == WIDTH_1080p) {
        // getWindow().getAttributes().height = HEIGHT_1080p / 2;
        // getWindow().getAttributes().width = WIDTH_1080p / 4;
        //
        // getWindow().getAttributes().x = -WIDTH_1080p / 2;
        // getWindow().getAttributes().y = 0;
        // }
    }

    @Override
    public void show() {
        audioControl = null;
        try {
            audioControl = MainActivity.service.getAudioControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (audioControl != null) {
            if (languagesAvailable != null) {
                languagesAvailable.clear();
            }
            try {
                int audioTrackCount = audioControl.getAudioTrackCount();
                languagesAvailable = null;
                if (audioTrackCount != 0) {
                    languagesAvailable = new ArrayList<AudioTrack>();
                    for (int i = 0; i < audioTrackCount; i++) {
                        AudioTrack track = audioControl.getAudioTrack(i);
                        Log.d(TAG, "Audio Lang Dialog: track=" + track);
                        languagesAvailable.add(track);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (languagesAvailable == null) {
                languagesAvailable = new ArrayList<AudioTrack>();
            }
            // ///////////////////////////////////
            // Veljko Ilkic
            // ///////////////////////////////////
            adapter.notifyDataSetChanged();
            if (languagesAvailable.size() < 2) {
                A4TVToast toast = new A4TVToast(getContext());
                toast.showToast(R.string.only_one_audio_language);
            } else {
                super.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int index = 0;
                        try {
                            index = audioControl.getCurrentAudioTrackIndex();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (index > -1 && index < languagesAvailable.size()) {
                            listViewLanguages.requestFocusFromTouch();
                            listViewLanguages.setSelection(index);
                        }
                    }
                }, 100);
            }
        }
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
        textViewOnTop.setText(R.string.choose_audio_language);
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

    private class AdapterLanguages extends BaseAdapter {
        private final float LAYOUT_SUM = 5, AUDIOLANGUAGE_SUM = 2.0f,
                AUDIOTYPE_SUM = 1.0f, AUDIOCODEC_SUM = 1.0f,
                AUDIOCHANNEL_SUM = 1.0f;

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
            convertView = new LinearLayout(ctx);
            A4TVTextView textViewAudioLanguage = new A4TVTextView(ctx);
            A4TVTextView textViewAudioType = new A4TVTextView(ctx);
            A4TVTextView textViewDigitalAudioEncodingMode = new A4TVTextView(
                    ctx);
            A4TVTextView textViewChannels = new A4TVTextView(ctx);
            // set linear layout
            ((LinearLayout) convertView)
                    .setOrientation(LinearLayout.HORIZONTAL);
            ((LinearLayout) convertView).setWeightSum(LAYOUT_SUM);
            (convertView).setLayoutParams(new AbsListView.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    MainActivity.dialogListElementHeight));
            ((LinearLayout) convertView).setGravity(Gravity.LEFT
                    | Gravity.CENTER_VERTICAL);
            (convertView).setPadding(
                    (int) ctx.getResources()
                            .getDimension(R.dimen.padding_large), 0, (int) ctx
                            .getResources()
                            .getDimension(R.dimen.padding_medium), 0);
            (convertView).setBackgroundResource(R.drawable.list_view_selector);
            // add text views to linear layout
            ((LinearLayout) convertView).addView(textViewAudioLanguage);
            ((LinearLayout) convertView).addView(textViewAudioType);
            ((LinearLayout) convertView)
                    .addView(textViewDigitalAudioEncodingMode);
            ((LinearLayout) convertView).addView(textViewChannels);
            // set up text views
            textViewAudioType.setGravity(Gravity.RIGHT
                    | Gravity.CENTER_VERTICAL);
            String typeTrackName = getTypeString(languagesAvailable.get(
                    position).getAudioTrackType());
            textViewAudioType.setText(typeTrackName);
            textViewAudioType.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.MATCH_PARENT, AUDIOTYPE_SUM));
            textViewDigitalAudioEncodingMode.setGravity(Gravity.RIGHT
                    | Gravity.CENTER_VERTICAL);
            String encodingName = getCodecString(languagesAvailable.get(
                    position).getAudioDigitalType());
            textViewDigitalAudioEncodingMode.setText(encodingName);
            textViewDigitalAudioEncodingMode
                    .setLayoutParams(new LinearLayout.LayoutParams(0,
                            LayoutParams.MATCH_PARENT, AUDIOCODEC_SUM));
            textViewChannels
                    .setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            String channelsName = getChannleString(languagesAvailable.get(
                    position).getAudioChannleCfg());
            textViewChannels.setText(channelsName);
            textViewChannels.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.MATCH_PARENT, AUDIOCHANNEL_SUM));
            textViewAudioLanguage.setGravity(Gravity.CENTER_VERTICAL);
            textViewAudioLanguage.setText(languagesAvailable.get(position)
                    .getLanguage());
            textViewAudioLanguage
                    .setLayoutParams(new LinearLayout.LayoutParams(0,
                            LayoutParams.MATCH_PARENT, AUDIOLANGUAGE_SUM));
            return convertView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        try {
            audioControl.setCurrentAudioTrack(arg2);
            AudioLanguageDialog.this.cancel();
        } catch (Exception e) {
            A4TVToast toast = new A4TVToast(ctx);
            toast.showToast(R.string.error_with_playing_audio);
            e.printStackTrace();
        }
    }

    // this is not needed here
    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }

    private String getCodecString(AudioDigitalType encodingMode) {
        switch (encodingMode) {
            case UNKNOWN:
                return "UNKNOWN";
            case LPCM:
                return "LPCM";
            case AC2:
                return "AC2";
            case AC3:
                return "AC3";
            case EAC3:
                return "EAC3";
            case MPEG1:
                return "MPEG1";
            case MP3:
                return "MP3";
            case MPEG2:
                return "MPEG2";
            case AAC:
                return "AAC";
            case HEAAC:
                return "HEAAC";
            case DTS:
                return "DTS";
            case DTS_HD:
                return "DTS_HD";
            case ATRAC:
                return "ATRAC";
            case OBA:
                return "OBA";
            case MLP:
                return "MLP";
            case CDDA:
                return "CDDA";
        }
        return "";
    }

    private String getTypeString(AudioTrackType trackType) {
        switch (trackType) {
            case AUDIO:
                return "AUDIO";
            case AUDIO_DESCRIPTION:
                return "AD";
            case HEARING_IMPAIRED:
                return "HI";
            case DIALOG:
                return "DIALOG";
            case COMENTARY:
                return "COMENTARY";
            case VOICEOVER:
                return "VOICEOVER";
            case EMERGENCY:
                return "EMERGENCY";
        }
        return "AUDIO";
    }

    private String getChannleString(AudioChannelConfiguration audioChannels) {
        switch (audioChannels) {
            case UNSPECIFIED:
                return "";
            case MONO:
                return "MONO";
            case STEREO:
                return "STEREO";
            case MULTICHANNEL:
                return "SURROUND";
        }
        return "";
    }
}
