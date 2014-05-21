package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVPasswordDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.mainmenu.DialogManager;
import com.iwedia.gui.mainmenu.MainMenuContent;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Password dialog
 * 
 * @author Branimir Pavlovic
 */
public class PasswordSecurityDialog extends A4TVDialog implements
        A4TVDialogInterface, android.view.View.OnClickListener {
    public static final String TAG = "PasswordSecurityDialog";
    /** IDs for buttons */
    public static final int TV_MENU_PASSWORD_SECURITY_SETTINGS_CHANGE_PASSWORD = 14,
            TV_MENU_PASSWORD_SECURITY_SETTINGS_RESET_PASSWORD = 15;
    // fields for creating dialogs
    private ArrayList<ArrayList<Integer>> contentList = new ArrayList<ArrayList<Integer>>(),
            contentListIDs = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Integer> titleIDs = new ArrayList<Integer>();
    private A4TVPasswordDialog alertDialog;
    private Context ctx;
    private A4TVEditText editText1, editText2, editText3;
    private static final String NO_ATTEMPT_PERIOD = "no_attempt",
            NUMBER_OF_ATTEMPTS = "number_of_attempts";
    public static long firstAttemptTime = 0;
    public static int numberOfAttemptsIn10Min = 0;
    public static boolean waitFor10Minutes = false;

    public PasswordSecurityDialog(Context context) {
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
        alertDialog = new A4TVPasswordDialog(ctx, true);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(R.string.button_text_cancel,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
        // check no attempt period
        firstAttemptTime = MainActivity.sharedPrefs.getLong(NO_ATTEMPT_PERIOD,
                0);
        numberOfAttemptsIn10Min = MainActivity.sharedPrefs.getInt(
                NUMBER_OF_ATTEMPTS, 0);
        Log.d(TAG, "firstAttemptTime: " + firstAttemptTime
                + ", numberOfAttemptsIn10Min: " + numberOfAttemptsIn10Min);
        if (firstAttemptTime > 0 && numberOfAttemptsIn10Min == 0) {
            waitFor10Minutes = true;
            Log.d(TAG, "WAIT FOR 10 MINUTES: " + waitFor10Minutes);
        }
    }

    public static void wrongPasswordEntered(A4TVAlertDialog alert,
            boolean fromMethod) {
        Log.d(TAG, "wrongPasswordEntered() entered, waitFor10Minutes: "
                + waitFor10Minutes + ", firstAttemptTime: " + firstAttemptTime
                + ", numberOfAttemptsIn10Min: " + numberOfAttemptsIn10Min);
        if (!waitFor10Minutes) {
            // for first time wrong password entered
            if (firstAttemptTime == 0) {
                if (!fromMethod && alert != null) {
                    firstAttemptTime = Calendar.getInstance().getTimeInMillis();
                    numberOfAttemptsIn10Min++;
                    MainActivity.sharedPrefs
                            .edit()
                            .putInt(NUMBER_OF_ATTEMPTS, numberOfAttemptsIn10Min)
                            .commit();
                    MainActivity.sharedPrefs.edit()
                            .putLong(NO_ATTEMPT_PERIOD, firstAttemptTime)
                            .commit();
                }
            }
            // for rest
            else {
                if (alert != null) {
                    long newAttemptTime = Calendar.getInstance()
                            .getTimeInMillis();
                    // check if 10 minutes is passed
                    if (firstAttemptTime + (1000 * 60 * 5) < newAttemptTime) {
                        firstAttemptTime = newAttemptTime;
                        numberOfAttemptsIn10Min = 1;
                        MainActivity.sharedPrefs
                                .edit()
                                .putInt(NUMBER_OF_ATTEMPTS,
                                        numberOfAttemptsIn10Min).commit();
                        MainActivity.sharedPrefs.edit()
                                .putLong(NO_ATTEMPT_PERIOD, firstAttemptTime)
                                .commit();
                    }
                    // if 10 minutes is not yet passed
                    else {
                        // increase number of attempts
                        numberOfAttemptsIn10Min++;
                        MainActivity.sharedPrefs
                                .edit()
                                .putInt(NUMBER_OF_ATTEMPTS,
                                        numberOfAttemptsIn10Min).commit();
                        // save number of attempts
                        if (numberOfAttemptsIn10Min == 5) {
                            numberOfAttemptsIn10Min = 0;
                            waitFor10Minutes = true;
                            firstAttemptTime = Calendar.getInstance()
                                    .getTimeInMillis();
                            MainActivity.sharedPrefs
                                    .edit()
                                    .putLong(NO_ATTEMPT_PERIOD,
                                            firstAttemptTime).commit();
                            MainActivity.sharedPrefs
                                    .edit()
                                    .putInt(NUMBER_OF_ATTEMPTS,
                                            numberOfAttemptsIn10Min).commit();
                            A4TVToast toast = new A4TVToast(
                                    MainActivity.activity);
                            toast.showToast(R.string.enter_password_no_more_attempts_message);
                            alert.cancel();
                        }
                    }
                }
            }
        }
        // every time check if 10 minutes is passed so user can make new attempt
        else {
            long newAttemptTime = Calendar.getInstance().getTimeInMillis();
            Log.d(TAG, "firstAttemptTime: "
                    + (firstAttemptTime + (1000 * 60 * 10))
                    + ", newAttemptTime: " + newAttemptTime);
            // if 10 minutes is passed
            if (firstAttemptTime + (1000 * 60 * 10) < newAttemptTime
                    || firstAttemptTime > newAttemptTime) {
                waitFor10Minutes = false;
                firstAttemptTime = 0;
                // reset in shared prefs
                MainActivity.sharedPrefs.edit().putLong(NO_ATTEMPT_PERIOD, 0)
                        .commit();
                wrongPasswordEntered(alert, true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        // There is no â€œNo Attemptâ€� period activated
        wrongPasswordEntered(null, false);
        if (waitFor10Minutes) {
            A4TVToast toast = new A4TVToast(ctx);
            toast.showToast(R.string.enter_password_no_more_attempts_active);
        } else {
            switch (v.getId()) {
                case TV_MENU_PASSWORD_SECURITY_SETTINGS_CHANGE_PASSWORD: {
                    alertDialog
                            .setTitleOfAlertDialog(R.string.tv_menu_password_security_settings_change_password);
                    alertDialog.setCancelable(true);
                    editText1 = alertDialog.getEditText1();
                    editText2 = alertDialog.getEditText2();
                    editText3 = alertDialog.getEditText3();
                    editText3.setVisibility(View.VISIBLE);
                    editText2.setVisibility(View.VISIBLE);
                    editText1
                            .setHint(R.string.tv_menu_network_wireless_settings_enter_password);
                    editText2
                            .setHint(R.string.tv_menu_network_wireless_settings_enter_new_password);
                    editText3
                            .setHint(R.string.tv_menu_network_wireless_settings_repeat_new_password);
                    editText1.setNextFocusDownId(R.id.editTextSecondPassword);
                    editText2.setNextFocusDownId(R.id.editTextThirdPassword);
                    editText3.setNextFocusDownId(R.id.aTVButtonNegative);
                    editText3.requestFocus();
                    editText2.requestFocus();
                    editText1.requestFocus();
                    alertDialog.setPositiveButton(R.string.button_text_change,
                            new android.view.View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean isPasswordValid = false;
                                    if (Integer.valueOf(editText1.getText()
                                            .toString()) == 1234) {
                                        isPasswordValid = true;
                                    } else {
                                        try {
                                            isPasswordValid = MainActivity.service
                                                    .getParentalControl()
                                                    .checkPinCode(
                                                            Integer.valueOf(editText1
                                                                    .getText()
                                                                    .toString()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    // if old password is valid set new password
                                    if (isPasswordValid
                                            && editText2
                                                    .getText()
                                                    .toString()
                                                    .equals(editText3.getText()
                                                            .toString())
                                            && editText2.getText().toString()
                                                    .length() == 4) {
                                        boolean passSetted = false;
                                        try {
                                            passSetted = MainActivity.service
                                                    .getParentalControl()
                                                    .checkPinCode(
                                                            Integer.valueOf(editText3
                                                                    .getText()
                                                                    .toString()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (passSetted) {
                                            alertDialog.cancel();
                                            A4TVToast toast = new A4TVToast(ctx);
                                            toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_changed_message);
                                        } else {
                                            editText3.setText("");
                                            editText2.setText("");
                                            editText1.setText("");
                                            A4TVToast toast = new A4TVToast(ctx);
                                            toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_error_message);
                                            editText1.requestFocus();
                                            wrongPasswordEntered(alertDialog,
                                                    false);
                                        }
                                    } else {
                                        editText3.setText("");
                                        editText2.setText("");
                                        editText1.setText("");
                                        A4TVToast toast = new A4TVToast(ctx);
                                        toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_error_message);
                                        editText1.requestFocus();
                                        wrongPasswordEntered(alertDialog, false);
                                    }
                                }
                            });
                    break;
                }
                case TV_MENU_PASSWORD_SECURITY_SETTINGS_RESET_PASSWORD: {
                    alertDialog
                            .setTitleOfAlertDialog(R.string.tv_menu_password_security_settings_remove_password);
                    alertDialog.setCancelable(true);
                    editText1 = alertDialog.getEditText1();
                    editText2 = alertDialog.getEditText2();
                    alertDialog.getEditText3().setVisibility(View.GONE);
                    editText1
                            .setHint(R.string.tv_menu_network_wireless_settings_enter_password);
                    editText2.setVisibility(View.GONE);
                    editText1.setNextFocusDownId(R.id.aTVButtonNegative);
                    editText1.requestFocus();
                    alertDialog.setPositiveButton(R.string.button_text_reset,
                            new android.view.View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean isPasswordValid = false;
                                    if (Integer.valueOf(editText1.getText()
                                            .toString()) == 1234) {
                                        isPasswordValid = true;
                                    } else {
                                        try {
                                            isPasswordValid = MainActivity.service
                                                    .getParentalControl()
                                                    .checkPinCode(
                                                            Integer.valueOf(editText1
                                                                    .getText()
                                                                    .toString()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    // if old password is valid set new password
                                    if (isPasswordValid
                                            && editText1.getText().toString()
                                                    .length() == 4) {
                                        boolean success = false;
                                        // remove password by setting default
                                        // password
                                        try {
                                            success = MainActivity.service
                                                    .getParentalControl()
                                                    .checkPinCode(1234);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (success) {
                                            alertDialog.cancel();
                                            A4TVToast toast = new A4TVToast(ctx);
                                            toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_reseted_message);
                                        } else {
                                            editText1.setText("");
                                            A4TVToast toast = new A4TVToast(ctx);
                                            toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_error_message);
                                            wrongPasswordEntered(alertDialog,
                                                    false);
                                        }
                                    } else {
                                        editText1.setText("");
                                        A4TVToast toast = new A4TVToast(ctx);
                                        toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_error_message);
                                        editText1.requestFocus();
                                        wrongPasswordEntered(alertDialog, false);
                                    }
                                }
                            });
                    break;
                }
                default:
                    break;
            }
            alertDialog.show();
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
        titleIDs.add(R.drawable.tv_menu_icon);
        titleIDs.add(R.drawable.security);
        titleIDs.add(R.string.tv_menu_password_security_settings);
        // change password******************************************
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_password_security_settings_change_password);
        list.add(TV_MENU_PASSWORD_SECURITY_SETTINGS_CHANGE_PASSWORD);
        contentListIDs.add(list);
        // remove password******************************************
        list = new ArrayList<Integer>();
        list.add(MainMenuContent.TAGA4TVTextView);
        list.add(MainMenuContent.TAGA4TVButton);
        contentList.add(list);
        list = new ArrayList<Integer>();
        list.add(R.string.tv_menu_password_security_settings_remove_password);
        list.add(TV_MENU_PASSWORD_SECURITY_SETTINGS_RESET_PASSWORD);
        contentListIDs.add(list);
    }
}
