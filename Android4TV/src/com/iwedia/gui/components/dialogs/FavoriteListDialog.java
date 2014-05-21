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

import com.iwedia.comm.content.Content;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVTextView;

import java.util.ArrayList;

/**
 * Favorite list dialog
 * 
 * @author Zorana Marasanov
 */
public class FavoriteListDialog extends A4TVDialog implements
        A4TVDialogInterface, OnItemClickListener {
    private final static String TAG = "FavoriteListDialog";
    private Context ctx;
    private A4TVTextView textViewOnTop;
    private ImageView imageLine;
    private ListView listFavoriteView;
    private static FavoriteAdapter adapter;
    private ArrayList<String> listFavoriteItems = new ArrayList<String>();
    // TODO: Find a way to store read selected content from contentlist - out
    // from onLongItemClick callbacks
    private Content selected_content = null;
    public final static int NONE = 0;
    public final static int ADD_TO_LIST = 1;
    public final static int DELETE_LIST = 2;

    public FavoriteListDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init();
    }

    public void init() {
        textViewOnTop = (A4TVTextView) findViewById(R.id.aTVTextViewMessage);
        textViewOnTop
                .setLayoutParams(new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        MainActivity.dialogListElementHeight));
        textViewOnTop.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textViewOnTop.setPadding(
                (int) ctx.getResources().getDimension(R.dimen.padding_small),
                0, 0, 0);
        textViewOnTop.setText("Favorite Lists");
        imageLine = (ImageView) findViewById(R.id.imageViewHorizLine);
        // get drawable from theme for image source
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.DialogSmallUpperDividerLine });
        int backgroundID = atts.getResourceId(0, 0);
        imageLine.setBackgroundResource(backgroundID);
        atts.recycle();
        listFavoriteView = (ListView) findViewById(R.id.listViewAudioLanguage);
        adapter = new FavoriteAdapter();
        listFavoriteView.setAdapter(adapter);
        listFavoriteView.setOnItemClickListener(this);
        listFavoriteView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        listFavoriteView.setScrollbarFadingEnabled(false);
    }

    @Override
    public void show() {
        int numberOfFavoriteLists;
        selected_content = null;
        listFavoriteItems.clear();
        try {
            numberOfFavoriteLists = MainActivity.service.getServiceControl()
                    .getNumberOfServiceLists();
            Log.d(TAG, "Number of favorites: " + numberOfFavoriteLists);
            for (int i = 1; i < numberOfFavoriteLists; i++) {
                String favLististName;
                favLististName = MainActivity.service.getContentListControl()
                        .getContentFilter(i).getContentListName();
                Log.d(TAG, "favoriteListName = " + favLististName);
                listFavoriteItems.add(favLististName);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        listFavoriteView = (ListView) findViewById(R.id.listViewAudioLanguage);
        adapter = new FavoriteAdapter();
        listFavoriteView.setAdapter(adapter);
        listFavoriteView.setOnItemClickListener(this);
        listFavoriteView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        listFavoriteView.setScrollbarFadingEnabled(false);
        super.show();
    }

    public void setContent(Content content) {
        selected_content = content;
    }

    /** Adapter for list view */
    private class FavoriteAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listFavoriteItems.size();
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
            ((A4TVTextView) convertView).setText(listFavoriteItems
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
        Log.d(TAG, "OnItemClicked chosen " + arg2);
        try {
            MainActivity.service.getContentListControl()
                    .addContentToFavoriteList(arg2 + 1, selected_content);
            MainActivity.service.getContentListControl()
                    .getContentFilter(arg2 + 1).reinitialize();
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
        this.cancel();
    }

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
        // TODO Auto-generated method stub
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
}
