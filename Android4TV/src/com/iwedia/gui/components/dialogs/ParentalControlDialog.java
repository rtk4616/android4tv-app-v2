package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.components.A4TVToast;

import java.util.ArrayList;

/**
 * Dialog for audio languages
 * 
 * @author Branimir Pavlovic
 */
public class ParentalControlDialog extends A4TVDialog implements
        A4TVDialogInterface, OnClickListener {
    private A4TVButton parentalControlOkButton;
    private A4TVEditText parentalControleEditText;
    private Context ctx;
    private A4TVTextView textViewOnTop;
    private A4TVTextView textReminderMessage;
    private ImageView imageLine;
    private Handler handler;
    private final int SHOW_DIALOG = 1, HIDE_DIALOG = 2;

    public ParentalControlDialog(Context context) {
        super(context, checkTheme(context), 0);
        ctx = context;
        fillDialog();
        setDialogAttributes();
        init();
    }

    @Override
    public void fillDialog() {
        setContentView(R.layout.parental_control_dialog);
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
        getWindow().getAttributes().height = MainActivity.dialogHeight / 2;
    }

    @Override
    public void show() {
        // TODO set message
        super.show();
    }

    /** Show dialog from callback */
    public void showDialog() {
        handler.sendEmptyMessage(SHOW_DIALOG);
    }

    /** Close dialog from callback */
    public void cancelDialog() {
        handler.sendEmptyMessage(HIDE_DIALOG);
    }

    /** Take reference of list view and bind it with adapter */
    private void init() {
        textViewOnTop = (A4TVTextView) findViewById(R.id.aTVTextViewMessage);
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
        parentalControlOkButton = (A4TVButton) findViewById(R.id.parentalControlOkButton);
        parentalControleEditText = (A4TVEditText) findViewById(R.id.parentalControlPinEditText);
        parentalControlOkButton.setOnClickListener(this);
        // create handler for call backs
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_DIALOG: {
                        ParentalControlDialog.this.show();
                        break;
                    }
                    case HIDE_DIALOG: {
                        ParentalControlDialog.this.cancel();
                        break;
                    }
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.parentalControlOkButton: {
                A4TVToast toast = new A4TVToast(getContext());
                toast.showToast("Ok");
                // TODO add reminder
                break;
            }
        }
    }
}
