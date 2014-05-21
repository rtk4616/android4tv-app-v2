package com.iwedia.gui.graphics;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import com.iwedia.gui.MainActivity;

/**
 * Android dialog for showing MHEG. Dialog default width: 800 Dialog default
 * height: 800. Dialog default gravity: Center
 * 
 * @author Milan Vidakovic
 */
public class MhegDialog extends Dialog {
    private static MhegDialogView mhegView;
    public static final String TAG = "MhegDialog";
    // if mheg is started
    public static boolean started = false;

    public MhegDialog(Context context, int theme) {
        super(context, theme);
        mhegView = new MhegDialogView(this.getContext());
        setContentView(mhegView);
        getWindow().getAttributes().width = 1280;
        getWindow().getAttributes().height = 720; /* Temporary */
        getWindow().setGravity(Gravity.CENTER);
        Log.d(TAG, "screen width and height" + MainActivity.screenWidth + "x"
                + MainActivity.screenHeight);
        Log.d(TAG, "dialog width and height"
                + getWindow().getAttributes().width + "x"
                + getWindow().getAttributes().height + "   y= "
                + getWindow().getAttributes().y);
    }

    public static void invalidateMhegView() {
        if (mhegView != null) {
            mhegView.postInvalidate();
        } else {
            Log.i(TAG, "mhegView = null");
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public MhegDialogView getMhegView() {
        return mhegView;
    }
}
