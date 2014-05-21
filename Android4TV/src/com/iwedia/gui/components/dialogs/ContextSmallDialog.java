package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;

import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;

import java.util.ArrayList;

/**
 * Dialog for context drop down menu
 * 
 * @author Branimir Pavlovic
 */
public class ContextSmallDialog extends A4TVDialog implements
        A4TVDialogInterface {
    public ContextSmallDialog(Context context) {
        super(context, checkTheme(context), 0);
    }

    // not needed here
    @Override
    public void fillDialog() {
    }

    // not needed here, attributes are passed by style
    @Override
    public void setDialogAttributes() {
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
                new int[] { R.attr.A4TVDialogContext });
        int i = atts.getResourceId(0, 0);
        atts.recycle();
        return i;
    }
}
