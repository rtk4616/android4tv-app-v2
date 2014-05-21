package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVButton;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVPasswordDialog;
import com.iwedia.gui.components.A4TVSpinner;
import com.iwedia.gui.components.A4TVSpinner.OnSelectA4TVSpinnerListener;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.osd.IOSDHandler;

import java.util.ArrayList;

/**
 * Parental guidance dialog
 * 
 * @author Branimir Pavlovic
 */
public class ParentalGuidanceDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    public static final String TAG = "ParentalGuidanceDialog";
    public static final int PARENTAL_CONTROL_INDEX_OFFSET = 3;
    /** IDs for buttons */
    public static final int TV_MENU_PARENTIAL_SECURITY_SETTINGS_PARENTIAL_GUIDANCE = 12345234;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private static A4TVSpinner buttonParentalSpinner;
    public static A4TVPasswordDialog parentalAlertDialog = null;

    public ParentalGuidanceDialog(Context context) {
        super(context, checkTheme(context), 0);
        // fill lists
        returnArrayListsWithDialogContents(contentList, contentListIDs,
                titleIDs);
        // set content to dialog
        fillDialog();
        // set attributes
        setDialogAttributes();
        init(context);
    }

    /** Init parental control dialog */
    public static void initParentalControlDialog(Context context) {
        parentalAlertDialog = new A4TVPasswordDialog(context, true);
    }

    private void init(final Context ctx) {
        buttonParentalSpinner = (A4TVSpinner) findViewById(TV_MENU_PARENTIAL_SECURITY_SETTINGS_PARENTIAL_GUIDANCE);
        buttonParentalSpinner
                .setOnSelectA4TVSpinnerListener(new OnSelectA4TVSpinnerListener() {
                    @Override
                    public void onSelect(A4TVSpinner spinner, int index,
                            String[] contents) {
                        PasswordSecurityDialog
                                .wrongPasswordEntered(null, false);
                        if (PasswordSecurityDialog.waitFor10Minutes) {
                            new A4TVToast(ctx)
                                    .showToast(R.string.enter_password_no_more_attempts_active);
                        } else {
                            // show alert dialog
                            ParentalGuidanceDialog.showAlertDialogForUser(
                                    index, ctx, contents[index]);
                        }
                    }
                });
    }

    public static void showAlertDialogForUser(final int parentalLevel,
            final Context ctx, final String buttonText) {
        final A4TVPasswordDialog alertDialog = new A4TVPasswordDialog(ctx, true);
        final A4TVEditText editText1 = alertDialog.getEditText1();
        alertDialog.getEditText2().setVisibility(View.GONE);
        alertDialog.getEditText3().setVisibility(View.GONE);
        alertDialog.setTitleOfAlertDialog(R.string.parental_state_change)
                .setCancelable(true);
        alertDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                alertDialog.getNegativeButton().performClick();
            }
        });
        alertDialog.setNegativeButton(R.string.button_text_cancel,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                        buttonParentalSpinner.getDialogContext().dismiss();
                        buttonParentalSpinner.setSelected(false);
                        editText1.setText("");
                    }
                });
        alertDialog.setPositiveButton(R.string.button_text_set,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isPinValid = false;
                        try {
                            if (editText1.getText().toString().length() == 4) {
                                isPinValid = MainActivity.service
                                        .getParentalControl().checkPinCode(
                                                Integer.valueOf(editText1
                                                        .getText().toString()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (isPinValid) {
                            // TODO change parental control level
                            int offset = 0, parentalLevelOffset = 0;
                            Log.d(TAG, "Initial parentalLevelOffset :"
                                    + parentalLevelOffset);
                            try {
                                if (parentalLevel != 0) {
                                    offset = PARENTAL_CONTROL_INDEX_OFFSET;
                                }
                                parentalLevelOffset = parentalLevel + offset;
                                Log.d(TAG, "PARENTAL INDEX"
                                        + parentalLevelOffset);
                                MainActivity.service.getParentalControl()
                                        .setParentalRate(parentalLevelOffset);
                            } catch (Exception e) {
                                new A4TVToast(ctx)
                                        .showToast(R.string.parental_error);
                                e.printStackTrace();
                            }
                            if (parentalLevelOffset == 0) {
                                new A4TVToast(ctx)
                                        .showToast(R.string.parental_guidance_is_off);
                            } else {
                                String guidanceLevelString = ctx
                                        .getResources()
                                        .getString(
                                                R.string.parental_guidance_is_set_on)
                                        + " "
                                        + String.valueOf(parentalLevelOffset);
                                new A4TVToast(ctx)
                                        .showToast(guidanceLevelString);
                            }
                            buttonParentalSpinner.setText(buttonText);
                            buttonParentalSpinner.getDialogContext().cancel();
                            buttonParentalSpinner.setSelected(false);
                            alertDialog.cancel();
                        }
                        // //////////////////////
                        // Wrong pin
                        // //////////////////////
                        else {
                            editText1.setText("");
                            A4TVToast toast = new A4TVToast(ctx);
                            toast.showToast(R.string.wrong_pin_entered);
                            editText1.requestFocus();
                            PasswordSecurityDialog.wrongPasswordEntered(
                                    alertDialog, false);
                        }
                    }
                });
        // show dialog
        alertDialog.show();
    }

    /** Show alert dialog when parental control is activated */
    public static void showParentalControlAlertDialog(
            final MainActivity activity) {
        parentalAlertDialog
                .setTitleOfAlertDialog(R.string.parental_control_activated);
        final A4TVEditText editText1 = parentalAlertDialog.getEditText1();
        parentalAlertDialog.getEditText2().setVisibility(View.GONE);
        parentalAlertDialog.getEditText3().setVisibility(View.GONE);
        parentalAlertDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                        // ///////////////////////////////////////////////
                        // CHANNEL UP
                        // ///////////////////////////////////////////////
                            case KeyEvent.KEYCODE_F4:
                            case KeyEvent.KEYCODE_CHANNEL_UP: {
                                if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                                    changeChannelUp(activity);
                                    parentalAlertDialog.cancel();
                                }
                                return true;
                            }
                            // ///////////////////////////////////////////////////
                            // CHANNEL DOWN
                            // ///////////////////////////////////////////////////
                            case KeyEvent.KEYCODE_F3:
                            case KeyEvent.KEYCODE_CHANNEL_DOWN: {
                                if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                                    changeChannelDown(activity);
                                    parentalAlertDialog.cancel();
                                }
                                return true;
                            }
                            // ///////////////////////////////////////////////////////////////////////
                            // MAIN MENU
                            // ////////////////////////////////////////////////////////////////////////
                            case KeyEvent.KEYCODE_MENU:
                            case KeyEvent.KEYCODE_M: {
                                if (MainKeyListener.enableKeyCodeMenu) {
                                    parentalAlertDialog.cancel();
                                    // Show main menu
                                    MainKeyListener
                                            .setAppState(MainKeyListener.MAIN_MENU);
                                    // If main menu isn't created create it
                                    if (((MainActivity) activity)
                                            .getMainMenuHandler() == null) {
                                        ((MainActivity) activity)
                                                .initMainMenu();
                                    }
                                    // Show main menu
                                    ((MainActivity) activity)
                                            .getMainMenuHandler()
                                            .showMainMenu();
                                }
                                return true;
                            }
                            // //////////////////////////////////////////////////////////////
                            // VOLUME UP
                            // //////////////////////////////////////////////////////////////
                            // case KeyEvent.KEYCODE_F6:
                            case KeyEvent.KEYCODE_VOLUME_UP: {
                                IOSDHandler mCurlHandler = activity
                                        .getPageCurl();
                                mCurlHandler.volume(VOLUME_UP, false);
                                return true;
                            }
                            // ///////////////////////////////////////////////////////////////////
                            // VOLUME DOWN
                            // ///////////////////////////////////////////////////////////////////
                            // case KeyEvent.KEYCODE_F5:
                            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                                IOSDHandler mCurlHandler = activity
                                        .getPageCurl();
                                mCurlHandler.volume(VOLUME_DOWN, false);
                                return true;
                            }
                            // ///////////////////////////////////////////////////////////////////
                            // VOLUME MUTE
                            // ///////////////////////////////////////////////////////////////////
                            case KeyEvent.KEYCODE_MUTE: {
                                IOSDHandler curlHandler = activity
                                        .getPageCurl();
                                curlHandler.volume(VOLUME_MUTE, false);
                                return true;
                            }
                            default:
                                break;
                        }
                        return false;
                    } else {
                        // Action up
                        return false;
                    }
                }
                return false;
            }
        });
        parentalAlertDialog.setCancelable(false);
        parentalAlertDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        parentalAlertDialog.setNegativeButton(R.string.button_text_cancel,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parentalAlertDialog.cancel();
                    }
                });
        parentalAlertDialog.setPositiveButton(R.string.parental_control_ok,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isPinValid = false;
                        try {
                            if (editText1.getText().toString().length() == 4) {
                                isPinValid = MainActivity.service
                                        .getParentalControl().checkPinCode(
                                                Integer.valueOf(editText1
                                                        .getText().toString()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (isPinValid) {
                            // Start playback of secured channel
                            try {
                                // MainActivity.service.getContentListControl().startVideoPlayback();
                                MainActivity.service
                                        .getVideoControl()
                                        .setCurrentVideoTrack(
                                                MainActivity.activity
                                                        .getCurrentVideoTrackIndex());
                                MainActivity.service
                                        .getAudioControl()
                                        .setCurrentAudioTrack(
                                                MainActivity.activity
                                                        .getCurrentAudioTrackIndex());
                                // Hide parental control check service layer
                                activity.getCheckServiceType()
                                        .hideParentalControlLayer();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            parentalAlertDialog.cancel();
                        }
                        // //////////////////////
                        // Wrong pin
                        // //////////////////////
                        else {
                            editText1.setText("");
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(R.string.wrong_pin_entered);
                            editText1.requestFocus();
                            PasswordSecurityDialog.wrongPasswordEntered(
                                    parentalAlertDialog, false);
                        }
                    }
                });
        Log.d(TAG, "IS PARENTAL VISIBLE" + parentalAlertDialog.isShowing() + "");
        // show dialog
        parentalAlertDialog.show();
        // Show parental control check service layer
        activity.getCheckServiceType().showParentalControlLayer();
    }

    @Override
    public void show() {
        fillViews();
        super.show();
    }

    private void fillViews() {
        int parentalLevel = 0;
        try {
            parentalLevel = MainActivity.service.getParentalControl()
                    .getParentalRate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (parentalLevel == 0) {
            buttonParentalSpinner.setSelection(parentalLevel);
        } else {
            if (parentalLevel >= PARENTAL_CONTROL_INDEX_OFFSET + 1) {
                buttonParentalSpinner.setSelection(parentalLevel
                        - PARENTAL_CONTROL_INDEX_OFFSET);
            } else {
                buttonParentalSpinner.setSelection(0);
            }
        }
    }

    @Override
    public void onClick(View v) {
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
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.drawable.security);
        titleIDs.add(R.string.tv_menu_parential_security_settings);
        // parentail guidance******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVSpinner);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_parential_security_settings_parential_guidance);
        list.add(TV_MENU_PARENTIAL_SECURITY_SETTINGS_PARENTIAL_GUIDANCE);
        contentListIDs.add(list);
    }

    private static void changeChannelUp(MainActivity activity) {
        IOSDHandler curlHandler = activity.getPageCurl();
        curlHandler
                .prepareChannelAndChange(SCENARIO_CHANNEL_CHANGE, CHANNEL_UP);
    }

    private static void changeChannelDown(MainActivity activity) {
        IOSDHandler curlHandler = activity.getPageCurl();
        curlHandler.prepareChannelAndChange(SCENARIO_CHANNEL_CHANGE,
                CHANNEL_DOWN);
    }
    // private static void changeChannelTogglePrevious(MainActivity activity) {
    // IOSDHandler curlHandler = activity.getPageCurl();
    // curlHandler.prepareChannelAndChange(
    // SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE,
    // CHANNEL_TOGGLE_PREVIOUS);
    // }
}
