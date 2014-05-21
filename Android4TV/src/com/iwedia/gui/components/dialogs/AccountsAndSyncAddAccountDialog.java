package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.iwedia.comm.system.account.Account;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogCreatorClass;
import com.iwedia.gui.mainmenu.DialogManager;

import java.util.ArrayList;

public class AccountsAndSyncAddAccountDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener,
        OnItemClickListener {
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private ArrayList<Account> accounts = new ArrayList<Account>();
    private Context ctx;
    private ManageAccountsAdapter listAdapter;
    private ListView listViewAccounts;

    public AccountsAndSyncAddAccountDialog(Context context) {
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
        if (fillViews()) {
            super.show();
        }
    }

    private void init() {
        listViewAccounts = (ListView) findViewById(DialogCreatorClass.LIST_VIEW_IN_DIALOG_ID);
        listViewAccounts.setAdapter(listAdapter);
        listViewAccounts.setOnItemClickListener(this);
    }

    private boolean fillViews() {
        accounts.clear();
        accounts = null;
        try {
            accounts = (ArrayList<Account>) MainActivity.service
                    .getSystemControl().getAccountSyncControl()
                    .getAvailableAccounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (accounts != null) {
            listAdapter.notifyDataSetChanged();
            return true;
        } else {
            A4TVToast toast = new A4TVToast(getContext());
            toast.showToast(R.string.no_accout_message);
            return false;
        }
    }

    @Override
    public void fillDialog() {
        listAdapter = new ManageAccountsAdapter();
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

    /**
     * This is called when a button is clicked
     */
    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        MainActivity.activity.getMainMenuHandler().closeMainMenu(false);
        AccountsAndSyncAddAccountDialog.this.cancel();
        AccountsAndSyncDialog asDialog = MainActivity.activity
                .getDialogManager().getAccountsAndSyncDialog();
        if (asDialog != null) {
            asDialog.cancel();
        }
        try {
            MainActivity.service.getSystemControl().getAccountSyncControl()
                    .addAccount(accounts.get(arg2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Adapter for list of accounts */
    private class ManageAccountsAdapter extends BaseAdapter {
        private final int LIST_ITEM_WEIGHT_SUM = 5, TEXT_WEIGHT = 4,
                IMAGE_WEIGHT = 1;

        @Override
        public int getCount() {
            return accounts.size();
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
            holder.textView.setText(accounts.get(position).getAccountLabel());
            holder.textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            holder.textView.setPadding(
                    (int) ctx.getResources()
                            .getDimension(R.dimen.padding_large), 0, 0, 0);
            try {
                Bitmap bmp = BitmapFactory.decodeByteArray(
                        accounts.get(position).getImage(), 0,
                        accounts.get(position).getImage().length);
                if (bmp != null) {
                    holder.image.setImageBitmap(bmp);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        titleIDs.add(R.string.tv_menu_account_settings_add_account);
    }
}
