package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;

import java.util.ArrayList;

/**
 * Dialog for multimedia show
 * 
 * @author Branimir Pavlovic
 */
public class MultimediaShowDialog extends A4TVDialog implements
        A4TVDialogInterface {
    private final int WIDTH_720p = 1280, HEIGHT_720p = 720, WIDTH_1080p = 1920,
            HEIGHT_1080p = 1080;

    public MultimediaShowDialog(Context context) {
        super(context, checkTheme(context), 0);
        fillDialog();
        setDialogAttributes();
    }

    @Override
    public void fillDialog() {
        setContentView(R.layout.multimedia_show);
    }

    @Override
    public void setDialogAttributes() {
        if (MainActivity.screenWidth == WIDTH_720p
                || MainActivity.screenHeight == WIDTH_720p) {
            getWindow().getAttributes().height = HEIGHT_720p;
            getWindow().getAttributes().width = WIDTH_720p;
        }
        if (MainActivity.screenWidth == WIDTH_1080p
                || MainActivity.screenHeight == WIDTH_1080p) {
            getWindow().getAttributes().height = HEIGHT_1080p;
            getWindow().getAttributes().width = WIDTH_1080p;
        }
    }

    // this is not needed here
    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }

    /**
     * Function that load theme
     * 
     * @param ctx
     * @return
     */
    private static int checkTheme(Context ctx) {
        TypedArray atts = ctx.getTheme().obtainStyledAttributes(
                new int[] { R.attr.A4TVDialogTransparent });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }
}
