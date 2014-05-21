package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Window;
import android.view.WindowManager;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;

import java.util.ArrayList;

/**
 * Dialog for content list
 * 
 * @author Branimir Pavlovic
 */
public class MainMenuDialog extends A4TVDialog implements A4TVDialogInterface {
    public MainMenuDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen,
                0);
        fillDialog();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DITHER,
                WindowManager.LayoutParams.FLAG_DITHER);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    public void fillDialog() {
        // setContentView(R.layout.main_menu);
        setContentView(R.layout.main_menu_overscale);
    }

    // not needed here, attributes are passed by style
    @Override
    public void setDialogAttributes() {
    }

    // @Override
    // public void show() {
    // MainActivity.activity.getMainMenuHandler().loadMenuItems(0);
    // MainActivity.activity.getMainMenuHandler().refreshMainMenu(true);
    // super.show();
    // }

    // this is not needed here
    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }
}
