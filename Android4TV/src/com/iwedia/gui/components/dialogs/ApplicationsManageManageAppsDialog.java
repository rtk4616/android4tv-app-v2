package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.iwedia.comm.content.applications.AppItem;
import com.iwedia.comm.enums.AppListType;
import com.iwedia.comm.system.application.IApplicationDetails;
import com.iwedia.comm.system.application.IApplicationSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

/**
 * Applications dialog
 * 
 * @author Branimir Pavlovic
 */
public class ApplicationsManageManageAppsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener,
        OnItemClickListener {
    /** IDs for spinner in this dialog */
    public static final int TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_SPINNER = 313;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private ArrayList<AppItem> manageApplicationsListContent = new ArrayList<AppItem>();
    private Context ctx;
    private ManageAppsAdapter listAdapter;
    private ListView listViewApplications;
    private A4TVSpinner spinner;

    public ApplicationsManageManageAppsDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        // get reference of listview
        listViewApplications = (ListView) findViewById(DialogCreatorClass.LIST_VIEW_IN_DIALOG_ID);
        listViewApplications.setOnItemClickListener(this);
        // get spinner reference
        spinner = (A4TVSpinner) findViewById(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_SPINNER);
        spinner.setSelection(0);
        spinner.setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
            @Override
            public void onSelect(A4TVSpinner spinner, int index,
                    String[] contents) {
                Log.d(TAG, "Choosen from running services: " + contents[index]);
                changedSourceOfApplicationsToShow(index);
            }
        });
    }

    @Override
    public void show() {
        changedSourceOfApplicationsToShow(spinner.getCHOOSEN_ITEM_INDEX());
        super.show();
    }

    public void changedSourceOfApplicationsToShow(int index) {
        int appsType = 0;
        switch (index) {
            case 0:
                appsType = AppListType.INSTALLED;
                break;
            case 1:
                appsType = AppListType.RUNNING;
                break;
            case 2:
                appsType = AppListType.ALL;
                break;
            case 3:
                appsType = AppListType.EXTERNAL;
                break;
            default:
                break;
        }
        IApplicationSettings appSettings = null;
        try {
            appSettings = MainActivity.service.getSystemControl()
                    .getApplicationControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (appSettings != null) {
            int numberOfApps = 0;
            try {
                if (appsType != AppListType.RUNNING) {
                    numberOfApps = appSettings.getAppListSize(appsType);
                }
                // ////////////////////////////////////////////
                // Running services
                // ////////////////////////////////////////////
                else {
                    manageApplicationsListContent = (ArrayList<AppItem>) appSettings
                            .getRunningServices();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            if (appsType != AppListType.RUNNING) {
                manageApplicationsListContent.clear();
                for (int i = 0; i < numberOfApps; i++) {
                    AppItem appItem = null;
                    try {
                        appItem = appSettings.getApplication(i);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    manageApplicationsListContent.add(appItem);
                }
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    /** List on item click listener */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int position,
            long arg3) {
        if (spinner.getCHOOSEN_ITEM_INDEX() != 1) {
            ApplicationsAppControlDialog appDialog = MainActivity.activity
                    .getDialogManager().getApplicationsAppControlDialog();
            if (appDialog != null)
                appDialog.showDialog(manageApplicationsListContent
                        .get(position));
        } else {
            final A4TVAlertDialog alert = new A4TVAlertDialog(ctx);
            alert.setTitleOfAlertDialog(ctx
                    .getString(R.string.tv_menu_applications_settings_manage_applications_force_stop)
                    + "?");
            alert.setCancelable(true);
            alert.setNegativeButton(R.string.button_text_no,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                        }
                    });
            alert.setPositiveButton(R.string.button_text_yes,
                    new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                MainActivity.service
                                        .getSystemControl()
                                        .getApplicationControl()
                                        .stopService(
                                                manageApplicationsListContent
                                                        .get(position)
                                                        .getAppPackage(),
                                                manageApplicationsListContent
                                                        .get(position)
                                                        .getAppClass());
                                // refresh list
                                changedSourceOfApplicationsToShow(spinner
                                        .getCHOOSEN_ITEM_INDEX());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            alert.cancel();
                        }
                    });
            alert.show();
        }
    }

    @Override
    public void fillDialog() {
        listAdapter = new ManageAppsAdapter();
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, listAdapter);// ,
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
        // titleIDs.add(R.drawable.applications_settings);
        titleIDs.add(R.string.tv_menu_applications_settings_manage_applications);
        // manage apps spinner******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_applications_settings_manage_applications_choose_by_type);
        list.add(TV_MENU_APPLICATIONS_SETTINGS_MANAGE_APPS_SPINNER);
        contentListIDs.add(list);
    }

    private class ManageAppsAdapter extends BaseAdapter {
        private final int LIST_ITEM_WEIGHT_SUM = 5, TEXT_WEIGHT = 4,
                IMAGE_WEIGHT = 1;

        @Override
        public int getCount() {
            return manageApplicationsListContent.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = new LinearLayout(ctx);
                holder = new ViewHolder();
                holder.textView = new A4TVTextView(ctx);
                holder.image = new ImageView(ctx);
                ((LinearLayout) convertView).addView(holder.image);
                ((LinearLayout) convertView).addView(holder.textView);
                ((LinearLayout) convertView)
                        .setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) convertView)
                        .setGravity(Gravity.CENTER_VERTICAL);
                ((LinearLayout) convertView).setWeightSum(LIST_ITEM_WEIGHT_SUM);
                convertView.setLayoutParams(new ListView.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        MainActivity.dialogListElementHeight));
                convertView
                        .setBackgroundResource(R.drawable.list_view_selector);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            setRow(holder, position);
            return convertView;
        }

        /** Function that connects list views child with view holder */
        private void setRow(ViewHolder holder, int position) {
            IApplicationDetails appDetails = null;
            try {
                appDetails = MainActivity.service
                        .getSystemControl()
                        .getApplicationControl()
                        .getApplicationDeatails(
                                manageApplicationsListContent.get(position)
                                        .getAppPackage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (appDetails != null) {
                try {
                    appDetails.getAppSizeInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            holder.textView.setText(manageApplicationsListContent.get(position)
                    .getAppname());
            holder.textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            holder.textView.setPadding(
                    (int) ctx.getResources()
                            .getDimension(R.dimen.padding_large), 0, 0, 0);
            Drawable appDrawable = null;
            try {
                appDrawable = ctx.getPackageManager().getApplicationIcon(
                        manageApplicationsListContent.get(position)
                                .getAppPackage());
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (appDrawable != null) {
                holder.image.setImageDrawable(appDrawable);
            }
            holder.image.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, IMAGE_WEIGHT));
            holder.textView.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, TEXT_WEIGHT));
        }

        private class ViewHolder {
            ImageView image;
            TextView textView;
        }
    }
}
