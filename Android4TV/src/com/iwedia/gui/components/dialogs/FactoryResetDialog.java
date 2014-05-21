package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVPasswordDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Factory reset dialog
 * 
 * @author Branimir Pavlovic
 */
public class FactoryResetDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    /** IDs for buttons */
    public static final int TV_MENU_FACTORY_RESET_SETTINGS_RESSET = 23;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private Context ctx;
    private A4TVPasswordDialog alert;
    private A4TVEditText editText1;

    public FactoryResetDialog(Context context) {
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
    public void fillDialog() {
        View view = DialogManager.dialogCreator.fillDialogWithContents(
                contentList, contentListIDs, titleIDs, null, this, null);// ,
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

    private void init() {
        /** Create ask dialog and show it to user */
        alert = new A4TVPasswordDialog(ctx, true);
        alert.setTitleOfAlertDialog(ctx.getResources().getString(
                R.string.ask_massage)
                + " "
                + ctx.getResources().getString(
                        R.string.ask_massage_factory_reset));
        alert.setCancelable(true);
        alert.setNegativeButton(R.string.button_text_no,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.cancel();
                    }
                });
        alert.setPositiveButton(R.string.button_text_yes,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = editText1.getText().toString();
                        boolean valid = false;
                        try {
                            if (password.length() == 4) {
                                valid = MainActivity.service
                                        .getParentalControl().checkPinCode(
                                                Integer.valueOf(password));
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        if (valid) {
                            try {
                                MainActivity.factoryReset = true;
                                Editor editor = MainActivity.activity
                                        .getSharedPreferences("myPrefs",
                                                Context.MODE_PRIVATE).edit();
                                editor.clear();
                                editor.commit();
                                // Remove widgets
                                if (MainActivity.activity.getWidgetsHandler() != null) {
                                    MainActivity.activity.getWidgetsHandler()
                                            .removeAllWidgets();
                                    try {
                                        MainActivity.activity
                                                .getWidgetsHandler()
                                                .saveImportantData();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                MainActivity.service.getSystemControl()
                                        .getAbout().factoryReset();
                                A4TVToast toast = new A4TVToast(getContext());
                                toast.showToast(R.string.factory_reset_success);
                            } catch (Exception e) {
                                A4TVToast toast = new A4TVToast(getContext());
                                toast.showToast(R.string.factory_reset_fail);
                                e.printStackTrace();
                            }
                            alert.cancel();
                            FactoryResetDialog.this.cancel();
                        } else {
                            // //////////////////////
                            // Wrong pin
                            // //////////////////////
                            editText1.setText("");
                            A4TVToast toast = new A4TVToast(ctx);
                            toast.showToast(R.string.wrong_pin_entered);
                            editText1.requestFocus();
                            PasswordSecurityDialog.wrongPasswordEntered(alert,
                                    false);
                            // disable positive button for 5 seconds
                            // alert.getPositiveButton().setEnabled(false);
                            // new Handler().postDelayed(new Runnable() {
                            //
                            // @Override
                            // public void run() {
                            // alert.getPositiveButton().setEnabled(true);
                            // }
                            // }, 5000);
                        }
                    }
                });
        editText1 = alert.getEditText1();
        alert.getEditText2().setVisibility(View.GONE);
        alert.getEditText3().setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == TV_MENU_FACTORY_RESET_SETTINGS_RESSET) {
            // There is no “No Attempt” period activated
            PasswordSecurityDialog.wrongPasswordEntered(null, false);
            if (PasswordSecurityDialog.waitFor10Minutes) {
                A4TVToast toast = new A4TVToast(ctx);
                toast.showToast(R.string.enter_password_no_more_attempts_active);
            } else {
                editText1.setText("");
                alert.show();
                editText1.requestFocus();
            }
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
        titleIDs.add(R.string.tv_menu_factory_reset_settings);
        // factory reset******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_factory_reset_settings_reset);
        list.add(TV_MENU_FACTORY_RESET_SETTINGS_RESSET);
        contentListIDs.add(list);
    }
}
