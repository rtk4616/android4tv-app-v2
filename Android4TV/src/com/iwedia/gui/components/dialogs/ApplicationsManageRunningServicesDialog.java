package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;

import java.util.ArrayList;

/**
 * Applications dialog
 * 
 * @author Branimir Pavlovic
 */
public class ApplicationsManageRunningServicesDialog extends A4TVDialog
        implements A4TVDialogInterface, android.view.View.OnClickListener,
        OnItemClickListener {
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private ArrayList<String> runningServicesListContent = new ArrayList<String>();
    private Context ctx;
    private RunningServicesAdapter listAdapter;
    private ListView listViewApplications;

    public ApplicationsManageRunningServicesDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
    }

    @Override
    public void show() {
        fillInitialViews();
        super.show();
    }

    /** Populate views in dialog */
    private void fillInitialViews() {
        // get reference of listview
        listViewApplications = (ListView) findViewById(DialogCreatorClass.LIST_VIEW_IN_DIALOG_ID);
        listViewApplications.setOnItemClickListener(this);
    }

    @Override
    public void fillDialog() {
        listAdapter = new RunningServicesAdapter();
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, listAdapter);// ,
        // pictureBackgroundID);
        setContentView(view);
    }

    @Override
    public void setDialogAttributes() {
        getWindow().getAttributes().width = MainActivity.dialogWidth;
        getWindow().getAttributes().height = MainActivity.dialogHeight;
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

    /** List on item click listener */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        A4TVToast toast = new A4TVToast(getContext());
        toast.showToast("CHOSSEN " + arg2);
    }

    @Override
    public void onClick(View v) {
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
        titleIDs.add(R.drawable.settings_icon);
        titleIDs.add(R.drawable.applications_settings);
        titleIDs.add(R.string.tv_menu_applications_settings_running_services);
    }

    private class RunningServicesAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return runningServicesListContent.size();
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
            ((A4TVTextView) convertView).setText(runningServicesListContent
                    .get(position));
            ((A4TVTextView) convertView).setGravity(Gravity.LEFT
                    | Gravity.CENTER_VERTICAL);
            (convertView).setPadding(
                    (int) ctx.getResources()
                            .getDimension(R.dimen.padding_large), 0, 0, 0);
            (convertView).setBackgroundResource(R.drawable.list_view_selector);
            convertView.setLayoutParams(new ListView.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    MainActivity.dialogListElementHeight));
            return convertView;
        }
    }
}
