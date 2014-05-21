package com.iwedia.gui.components.dialogs;

import android.content.Context;

import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;

import java.util.ArrayList;

/**
 * Dialog for content list
 * 
 * @author Branimir Pavlovic
 */
public class ContentDialog extends A4TVDialog implements A4TVDialogInterface {
    public ContentDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen,
                0);
        fillDialog();
    }

    @Override
    public void fillDialog() {
        // setContentView(R.layout.content_list);
        setContentView(R.layout.content_list_overscale);
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
}
