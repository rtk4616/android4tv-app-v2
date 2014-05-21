package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.os.RemoteException;

import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;

import java.util.ArrayList;

/**
 * Dialog for content list
 * 
 * @author Branimir Pavlovic
 */
public class MultimediaDialog extends A4TVDialog implements A4TVDialogInterface {
    public MultimediaDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen,
                0);
        fillDialog();
    }

    @Override
    public void fillDialog() {
        // setContentView(R.layout.multimedia_main);
        setContentView(R.layout.multimedia_main_overscale);
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

    @Override
    public void cancel() {
        super.cancel();
        try {
            MainActivity.service.getContentListControl().setActiveFilter(
                    FilterType.ALL);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
