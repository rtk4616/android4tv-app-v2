package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.iwedia.comm.system.account.Account;
import com.iwedia.comm.system.account.IAccountSyncSettings;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVProgressDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;

public class AccountsAndSyncManageAccountsDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener,
        OnItemClickListener {
    public static final String TAG = "AccountsAndSyncManageAccountsDialog";
    private final int BUTTON_ACCOUNT_FOR_SECOND_STATE = 82742378;
    private final int LIST_ACCOUNTS_STATE = 0, SYNC_ACCOUNT_STATE = 1,
            SYNC_STARTED = 2, SYNC_FINISHED = 3;
    private int currrentState = LIST_ACCOUNTS_STATE;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private ArrayList<Account> accounts = new ArrayList<Account>();
    private ArrayList<String> authorities = new ArrayList<String>();
    private Account clickedAccount;
    private A4TVButton buttonAccount;
    private LinearLayout layoutForHiding;
    private ListView listView;
    private ManageAccountsAdapter listAdapter;
    private Context ctx;
    private IAccountSyncSettings accountSettings;
    private A4TVProgressDialog progressDialog;
    private Handler handler;

    public AccountsAndSyncManageAccountsDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init();
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    @Override
    public void onBackPressed() {
        if (currrentState == SYNC_ACCOUNT_STATE) {
            currrentState = LIST_ACCOUNTS_STATE;
            clickedAccount = null;
            fillViews();
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        /** Init views */
        listView = (ListView) findViewById(DialogCreatorClass.LIST_VIEW_IN_DIALOG_ID);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
        buttonAccount = (A4TVButton) findViewById(BUTTON_ACCOUNT_FOR_SECOND_STATE);
        layoutForHiding = (LinearLayout) findViewById(R.string.tv_menu_account_settings_manage_account);
        buttonAccount.setBackgroundColor(Color.TRANSPARENT);
        /** Initialize progress dialog */
        progressDialog = new A4TVProgressDialog(ctx);
        progressDialog.setCancelable(true);
        progressDialog.setTitleOfAlertDialog(R.string.syncing);
        progressDialog.setMessage(R.string.please_wait);
        /** Init handler */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == SYNC_STARTED) {
                    progressDialog.show();
                }
                if (msg.what == SYNC_FINISHED) {
                    progressDialog.cancel();
                }
                super.handleMessage(msg);
            }
        };
    }

    private void fillViews() {
        /** Hide views that needs to be hidden */
        layoutForHiding.setVisibility(View.GONE);
        accountSettings = null;
        try {
            accountSettings = MainActivity.service.getSystemControl()
                    .getAccountSyncControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (accountSettings != null) {
            accounts = null;
            try {
                accounts = (ArrayList<Account>) MainActivity.service
                        .getSystemControl().getAccountSyncControl()
                        .manageAccounts();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (accounts != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public void syncStarted() {
        handler.sendEmptyMessage(SYNC_STARTED);
    }

    public void syncFinished() {
        handler.sendEmptyMessage(SYNC_FINISHED);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Log.d(TAG, "LIST ON ITEM CLICK " + arg2);
        // /////////////////////////////////
        // List accounts state
        // /////////////////////////////////
        if (currrentState == LIST_ACCOUNTS_STATE) {
            authorities = null;
            try {
                authorities = (ArrayList<String>) MainActivity.service
                        .getSystemControl().getAccountSyncControl()
                        .getAuthorities(accounts.get(arg2));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (authorities != null) {
                /** Show views that needs to be shown */
                layoutForHiding.setVisibility(View.VISIBLE);
                buttonAccount.setText(accounts.get(arg2).getAccountLabel());
                clickedAccount = accounts.get(arg2);
                // change state
                currrentState = SYNC_ACCOUNT_STATE;
                listAdapter.notifyDataSetChanged();
            }
            return;
        }
        // /////////////////////////////////////
        // Sync accounts state
        // /////////////////////////////////////
        if (currrentState == SYNC_ACCOUNT_STATE) {
            boolean isAutoSync = false;
            try {
                isAutoSync = accountSettings.isAutoSync();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            // //////////////////////////////////////////
            // AUTO SYNC IS ON
            // //////////////////////////////////////////
            if (isAutoSync) {
                Log.d(TAG, "LIST ON ITEM CLICK " + arg2);
                CheckBox checkB = (CheckBox) arg1
                        .findViewById(listAdapter.CHECK_BOX_ID);
                checkB.performClick();
            }
            // //////////////////////////////////////////
            // AUTO SYNC IS OFF
            // //////////////////////////////////////////
            else {
                try {
                    accountSettings.syncNow(clickedAccount,
                            authorities.get(arg2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void fillDialog() {
        listAdapter = new ManageAccountsAdapter();
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, listAdapter);
        setContentView(view);
    }

    /** Adapter for list of accounts */
    private class ManageAccountsAdapter extends BaseAdapter {
        public final float LIST_ITEM_WEIGHT_SUM = 5, IMAGE_WEIGHT = 0.5f;
        public final int CHECK_BOX_ID = 5434;
        public final float CHECK_BOX_WEIGHT = 0.3f, TEXT_WEIGHT = 4.2f;

        @Override
        public int getCount() {
            if (currrentState == LIST_ACCOUNTS_STATE) {
                if (accounts != null) {
                    return accounts.size();
                } else {
                    return 0;
                }
            }
            if (currrentState == SYNC_ACCOUNT_STATE) {
                if (authorities != null) {
                    return authorities.size();
                } else {
                    return 0;
                }
            }
            return 0;
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
                holder.textViewAccountName = new A4TVTextView(ctx);
                holder.textViewDescription = new A4TVTextView(ctx);
                holder.image = new ImageView(ctx);
                holder.layoutForText = new LinearLayout(ctx);
                holder.checkBox = new CheckBox(ctx);
                holder.checkBox.setId(CHECK_BOX_ID);
                // add text to layout
                holder.layoutForText.addView(holder.textViewAccountName);
                holder.layoutForText.addView(holder.textViewDescription);
                holder.textViewAccountName.setSingleLine(true);
                holder.textViewAccountName.setEllipsize(TruncateAt.MARQUEE);
                holder.textViewDescription.setSingleLine(true);
                holder.textViewDescription.setEllipsize(TruncateAt.MARQUEE);
                ((LinearLayout) convertView).addView(holder.image);
                ((LinearLayout) convertView).addView(holder.layoutForText);
                ((LinearLayout) convertView).addView(holder.checkBox);
                (convertView).setPadding(20, 2, 15, 2);
                ((LinearLayout) convertView)
                        .setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) convertView)
                        .setGravity(Gravity.CENTER_VERTICAL);
                ((LinearLayout) convertView).setWeightSum(LIST_ITEM_WEIGHT_SUM);
                convertView
                        .setBackgroundResource(R.drawable.list_view_selector);
                convertView.setTag(holder);
            }
            // /////////////////////////////////////
            // Sync accounts state
            // /////////////////////////////////////
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            setRow(holder, position);
            convertView.setLayoutParams(new ListView.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    (int) (MainActivity.dialogListElementHeight * 1.25)));
            return convertView;
        }

        /** Function that connects list views child with view holder */
        private void setRow(ViewHolder holder, final int position) {
            // /////////////////////////////////
            // List accounts state
            // /////////////////////////////////
            if (currrentState == LIST_ACCOUNTS_STATE) {
                /************** ACCOUNT NAME **********/
                holder.textViewAccountName.setText(accounts.get(position)
                        .getAccountLabel());
                /************** SYNC STATUS **********/
                boolean syncStatus = false;
                try {
                    syncStatus = accountSettings.getSyncStatus(accounts
                            .get(position));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (syncStatus) {
                    holder.textViewDescription
                            .setText(R.string.tv_menu_account_settings_manage_accounts_sync_on);
                } else {
                    holder.textViewDescription
                            .setText(R.string.tv_menu_account_settings_manage_accounts_sync_off);
                }
                /************** PICTURE **********/
                Bitmap bmp = null;
                try {
                    bmp = BitmapFactory.decodeByteArray(accounts.get(position)
                            .getImage(), 0,
                            accounts.get(position).getImage().length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bmp != null) {
                    holder.image.setImageBitmap(bmp);
                } else {
                    holder.image.setImageResource(R.drawable.tv_menu_icon);
                }
                holder.textViewDescription.setVisibility(View.VISIBLE);
                holder.checkBox.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
            }
            // /////////////////////////////////////
            // Sync accounts state
            // /////////////////////////////////////
            else {
                /******************* AUTHORITIES *****************/
                holder.textViewAccountName.setText(authorities.get(position));
                /******************* SYNC CHECK BOX ******************/
                boolean isAutoSync = false;
                try {
                    isAutoSync = accountSettings.isAutoSync();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                // //////////////////////////////////////////
                // AUTO SYNC IS ON
                // //////////////////////////////////////////
                if (isAutoSync) {
                    boolean isSyncable = false;
                    try {
                        isSyncable = accountSettings.getIsSyncable(
                                clickedAccount, authorities.get(position));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    holder.checkBox.setChecked(isSyncable);
                    holder.checkBox
                            .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(
                                        CompoundButton buttonView,
                                        boolean isChecked) {
                                    try {
                                        accountSettings.setIsSyncable(
                                                clickedAccount,
                                                authorities.get(position),
                                                isChecked);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    holder.checkBox.setVisibility(View.VISIBLE);
                    holder.textViewDescription.setVisibility(View.GONE);
                }
                // //////////////////////////////////////////
                // AUTO SYNC IS OFF
                // //////////////////////////////////////////
                else {
                    holder.textViewDescription
                            .setText(R.string.tv_menu_account_settings_click_to_sync);
                    holder.checkBox.setVisibility(View.GONE);
                    holder.textViewDescription.setVisibility(View.VISIBLE);
                }
                holder.image.setVisibility(View.GONE);
            }
            holder.checkBox.setFocusable(false);
            // //////////////////////////////
            // Set text and layout params for views
            // //////////////////////////////
            holder.layoutForText.setOrientation(LinearLayout.VERTICAL);
            holder.textViewAccountName
                    .setTextSize(MainActivity.dialogListElementHeight / 2 - 5);
            holder.textViewDescription
                    .setTextSize(MainActivity.dialogListElementHeight / 4);
            holder.checkBox.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, CHECK_BOX_WEIGHT));
            holder.image.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, IMAGE_WEIGHT));
            holder.layoutForText.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LayoutParams.WRAP_CONTENT, TEXT_WEIGHT));
        }

        private class ViewHolder {
            ImageView image;
            TextView textViewAccountName, textViewDescription;
            LinearLayout layoutForText;
            CheckBox checkBox;
        }
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

    /**
     * This is called when a button is clicked
     */
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
        titleIDs.add(R.drawable.account_sync);
        titleIDs.add(R.string.tv_menu_account_settings_manage_accounts);
        // button account******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_account_settings_manage_account);
        list.add(BUTTON_ACCOUNT_FOR_SECOND_STATE);
        contentListIDs.add(list);
    }
}
