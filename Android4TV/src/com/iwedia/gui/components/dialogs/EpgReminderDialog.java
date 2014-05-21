package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.iwedia.comm.IReminderControl;
import com.iwedia.comm.content.IContentListControl;
import com.iwedia.dtv.reminder.ReminderSmartInfo;
import com.iwedia.dtv.reminder.ReminderTimerInfo;
import com.iwedia.dtv.reminder.ReminderType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.util.DateTimeConversions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Dialog for Reminders handling
 * 
 * @author Branimir Pavlovic
 */
public class EpgReminderDialog extends A4TVDialog implements
        A4TVDialogInterface, OnClickListener, OnItemClickListener {
    public static final String TAG = "EpgReminderDialog";
    private Context ctx;
    private ListView listViewReminders;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    // private ArrayList<String> reminders = new ArrayList<String>();
    private ReminderAdapter adapter;
    /** Small context dialog that has drop down items */
    private A4TVDialog dialogContext;
    private int selectedFromList = -1, reminderCount;
    private IReminderControl reminderControl;
    private IContentListControl contentListControl;

    public EpgReminderDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        // returnArrayListsWithDialogContents(contentList, contentListIDs,
        // titleIDs);
        fillDialog();
        setDialogAttributes();
        init();
        setMenuButtonEnabled(false);
    }

    @Override
    public void fillDialog() {
        // adapter = new ReminderAdapter();
        // View view = DialogManager.dialogCreator.fillDialogWithContents(
        // contentList, contentListIDs, titleIDs, null, this, adapter);// ,
        // pictureBackgroundID);
        // setContentView(view);
        setContentView(R.layout.epg_reminder_dialog);
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
    }

    @Override
    public void show() {
        reminderControl = null;
        try {
            reminderControl = MainActivity.service.getReminderControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        contentListControl = null;
        try {
            contentListControl = MainActivity.service.getContentListControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (reminderControl != null) {
            reminderCount = 0;
            try {
                reminderCount = reminderControl.updateList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Reminder count " + reminderCount);
            adapter.notifyDataSetChanged();
        } else {
            reminderCount = 0;
        }
        super.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (reminderCount > 0) {
                    // listViewReminders.requestChildFocus(
                    // listViewReminders.getChildAt(0),
                    // listViewReminders.getChildAt(0));
                    listViewReminders.setSelection(0);
                }
            }
        }, 100);
    }

    /** Take reference of list view and bind it with adapter */
    private void init() {
        // get reference of listview
        // listViewReminders = (ListView)
        // findViewById(DialogCreatorClass.LIST_VIEW_IN_DIALOG_ID);
        listViewReminders = (ListView) findViewById(R.id.listView1);
        listViewReminders.setOnItemClickListener(this);
        adapter = new ReminderAdapter();
        listViewReminders.setAdapter(adapter);
        dialogContext = new ContextSmallDialog(ctx);
        // fill dialog with desired view
        dialogContext.setContentView(fillDialogWithElements());
        // set dialog size
        dialogContext.getWindow().getAttributes().width = MainActivity.dialogWidth / 2;
        dialogContext.getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
        // reminder text size
        A4TVTextView tv = (A4TVTextView) findViewById(R.id.aTVTextViewMessage);
        tv.setTextSize(ctx.getResources().getDimension(
                com.iwedia.gui.R.dimen.content_list_banner_text_size));
    }

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
        // clear old data in lists
        contentList.clear();
        contentListIDs.clear();
        titleIDs.clear();
        // title
        titleIDs.add(R.string.epg_reminder);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        // close reminders
            case KeyEvent.KEYCODE_G:
            case KeyEvent.KEYCODE_PROG_GREEN: {
                cancel();
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        selectedFromList = arg2;
        // show drop down dialog
        dialogContext.show();
        Log.d(TAG, "LIST ITEM CLICKED");
    }

    private class ReminderAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return reminderCount;
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
            // create views
            convertView = new LinearLayout(ctx);
            A4TVTextView textViewEventName = new A4TVTextView(ctx);
            A4TVTextView textViewEventInfo = new A4TVTextView(ctx);
            textViewEventName
                    .setTextSize(MainActivity.dialogListElementHeight / 2 - 12);
            textViewEventInfo
                    .setTextSize(MainActivity.dialogListElementHeight / 4);
            textViewEventName.setSingleLine(true);
            textViewEventName.setEllipsize(TruncateAt.MARQUEE);
            ((LinearLayout) convertView).addView(textViewEventName);
            ((LinearLayout) convertView).addView(textViewEventInfo);
            ((LinearLayout) convertView).setOrientation(LinearLayout.VERTICAL);
            // ((LinearLayout) convertView).setGravity(Gravity.CENTER_VERTICAL);
            (convertView).setPadding(20, 2, 15, 2);
            convertView.setLayoutParams(new ListView.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    (int) (MainActivity.dialogListElementHeight * 1.2)));
            convertView.setBackgroundResource(R.drawable.list_view_selector);
            ReminderType type;
            type = ReminderType.REMINDER_TIMER;
            try {
                type = reminderControl.getType(position);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (type == ReminderType.REMINDER_TIMER) {
                try {
                    ReminderTimerInfo info = reminderControl
                            .getTimerInfo(position);
                    if (info != null) {
                        // set text to text views
                        Log.d(TAG, "info is not null " + info.getServiceIndex());
                        if (contentListControl != null) {
                            try {
                                Log.d(TAG,
                                        "content list is not null name : "
                                                + contentListControl
                                                        .getServiceByIndexInMasterList(
                                                                info.getServiceIndex(),
                                                                false)
                                                        .getName());
                                Log.d(TAG, "info : " + info.toString());
                                textViewEventName.setText(contentListControl
                                        .getServiceByIndexInMasterList(
                                                info.getServiceIndex(), false)
                                        .getName()
                                        + " - " + info.getTitle());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "content list is  null ");
                            textViewEventName.setText(info.getTitle());
                        }
                        try {
                            textViewEventInfo.setText(DateTimeConversions
                                    .getDateTimeSting(info.getTime()
                                            .getCalendar().getTime()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "info is  null ");
                        textViewEventName.setText("");
                        textViewEventInfo.setText("");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                try {
                    ReminderSmartInfo info = reminderControl
                            .getSmartInfo(position);
                    if (info != null) {
                        // set text to text views
                        Log.d(TAG, "info is not null " + info.getServiceIndex());
                        if (contentListControl != null) {
                            try {
                                Log.d(TAG,
                                        "content list is not null name : "
                                                + contentListControl
                                                        .getServiceByIndexInMasterList(
                                                                info.getServiceIndex(),
                                                                false)
                                                        .getName());
                                Log.d(TAG, "info : " + info.toString());
                                textViewEventName.setText(contentListControl
                                        .getServiceByIndexInMasterList(
                                                info.getServiceIndex(), false)
                                        .getName()
                                        + " - " + info.getTitle());
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, "content list is  null ");
                            textViewEventName.setText(info.getTitle());
                        }
                        try {
                            textViewEventInfo.setText(DateTimeConversions
                                    .getDateTimeSting(info.getTime()
                                            .getCalendar().getTime()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "info is  null ");
                        textViewEventName.setText("");
                        textViewEventInfo.setText("");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            return convertView;
        }
    }

    /**
     * Creates view for context dialog
     * 
     * @param allList_favoriteList
     *        add or remove from favourites
     * @return
     */
    private View fillDialogWithElements() {
        LinearLayout mainLinLayout = new LinearLayout(ctx);
        mainLinLayout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mainLinLayout.setOrientation(LinearLayout.VERTICAL);
        // get drawable from theme for image source
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogContextBackground });
        int backgroundID = atts.getResourceId(0, 0);
        atts.recycle();
        mainLinLayout.setBackgroundResource(backgroundID);
        // layout of dialog title
        LinearLayout titleLinearLayout = new LinearLayout(ctx);
        titleLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        titleLinearLayout.setOrientation(LinearLayout.VERTICAL);
        titleLinearLayout.setPadding(
                (int) ctx.getResources().getDimension(
                        R.dimen.a4tvdialog_padding_left),
                (int) ctx.getResources().getDimension(
                        R.dimen.a4tvdialog_spinner_padding_top), 0, 0);
        A4TVTextView text = new A4TVTextView(ctx, null);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        text.setText(ctx.getResources()
                .getString(R.string.spinner_choose_title));
        text.setTextSize(ctx.getResources().getDimension(
                R.dimen.a4tvdialog_textview_size));
        // add title
        titleLinearLayout.addView(text);
        // add title layout to main layout
        mainLinLayout.addView(titleLinearLayout);
        // create horizontal line
        ImageView horizLine = new ImageView(ctx);
        horizLine.setLayoutParams(new LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        // get drawable from theme for image source
        atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        backgroundID = atts.getResourceId(0, 0);
        horizLine.setBackgroundResource(backgroundID);
        // add horiz line to main layout
        mainLinLayout.addView(horizLine);
        // create scroll view
        ScrollView mainScrollView = new ScrollView(ctx);
        mainScrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        // set pading for scrollview
        // mainScrollView.setPadding(
        // (int) activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_left),
        // (int) activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_top),
        // (int) activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_right),
        // (int) activity.getResources().getDimension(
        // R.dimen.a4tvdialog_padding_botom));
        mainScrollView.setScrollbarFadingEnabled(false);
        mainScrollView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        // add scrollview to main view
        mainLinLayout.addView(mainScrollView);
        LinearLayout contentLinearLayout = new LinearLayout(ctx);
        contentLinearLayout.setLayoutParams(new ScrollView.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        contentLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        // add content layout to scroll view
        mainScrollView.addView(contentLinearLayout);
        /** GET FIELDS FOR CREATING DROP DOWN ITEMS */
        String[] strings = null;
        strings = ctx.getResources().getStringArray(R.array.reminders_dropdown);
        for (int i = 0; i < strings.length; i++) {
            // create small layout
            final LinearLayout smallLayoutHorizontal = new LinearLayout(ctx);
            smallLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            smallLayoutHorizontal
                    .setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            MainActivity.dialogListElementHeight));
            smallLayoutHorizontal.setPadding(15, 5, 15, 5);
            smallLayoutHorizontal.setGravity(Gravity.CENTER_VERTICAL);
            // create drop box item
            A4TVButton button = new A4TVButton(ctx, null);
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            button.setText(strings[i]);
            button.setGravity(Gravity.CENTER);
            button.setId(i);
            // for creating difference between first buttons
            button.setTag(strings[i]);
            button.setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    if (vi.getTag().equals(
                            ctx.getResources().getString(
                                    R.string.remove_from_reminders))) {
                        if (selectedFromList > -1) {
                            try {
                                Log.d(TAG, "DELETE REMINDER: "
                                        + selectedFromList);
                                reminderControl.destroy(selectedFromList);
                                reminderCount--;
                                Log.d(TAG, "DELETE REMINDER: "
                                        + selectedFromList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    // Close context dialog
                    dialogContext.cancel();
                }
            });
            // set focus listener of button
            button.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // get drawable from theme for small layout
                    // background
                    TypedArray atts = ctx.getTheme().obtainStyledAttributes(
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
                ImageView horizLineSmall = new ImageView(ctx);
                android.widget.LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dialogContext.getWindow().getAttributes().width - 10,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                horizLineSmall.setLayoutParams(params);
                // get drawable from theme for image source
                atts = ctx.getTheme().obtainStyledAttributes(
                        new int[] { R.attr.DialogContextDividerLine });
                backgroundID = atts.getResourceId(0, 0);
                horizLineSmall.setImageResource(backgroundID);
                // add view
                contentLinearLayout.addView(horizLineSmall);
            }
        }
        return mainLinLayout;
    }
}
