package com.iwedia.gui.program_blocking;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVPasswordDialog;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.PasswordSecurityDialog;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;

/**
 * Program locking handler class
 * 
 * @author Veljko Ilkic
 */
public class ProgramLockingHandler implements OSDGlobal {
    private final String TAG = "ProgramLockingHandler";
    /** Password dialog for locking channels */
    private A4TVPasswordDialog passwordAlertDialog = null;
    /** Activity reference */
    private Activity activity;
    /** Edit text for password input */
    private A4TVEditText editTextEnteredPin = null;
    private ProgramLocking programLocking;

    /** Constructor 1 */
    public ProgramLockingHandler(Activity activity,
            ProgramLocking programLocking) {
        this.activity = activity;
        this.programLocking = programLocking;
        // Create password dialog
        createDialogForPasswordInput();
    }

    /** Create password dialog */
    private void createDialogForPasswordInput() {
        // Show password dialog
        if (passwordAlertDialog == null) {
            passwordAlertDialog = new A4TVPasswordDialog(activity, true);
        }
        passwordAlertDialog.setOnKeyListener(new ProgramLockingOnKey());
        passwordAlertDialog.setCancelable(false);
        editTextEnteredPin = passwordAlertDialog.getEditText1();
        passwordAlertDialog.getEditText2().setVisibility(View.GONE);
        passwordAlertDialog.getEditText3().setVisibility(View.GONE);
        passwordAlertDialog.setTitleOfAlertDialog(R.string.unlock_channel);
        passwordAlertDialog.setPositiveButton(R.string.parental_control_ok,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "ON CLICK OK PASSWORD");
                        String enteredPin = editTextEnteredPin.getText()
                                .toString();
                        // Check valid pin
                        boolean isPinValid = false;
                        try {
                            isPinValid = MainActivity.service
                                    .getParentalControl().checkPinCode(
                                            Integer.parseInt(enteredPin));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "NumberFormatException", e);
                        } catch (Exception e) {
                            Log.e(TAG, "Check Pin", e);
                        }
                        if (isPinValid && enteredPin.length() == 4) {
                            programLocking.pinIsOk();
                            // Close password dialog
                            passwordAlertDialog.dismiss();
                        } else {
                            Log.d(TAG, "WRONG PASSWORD");
                            editTextEnteredPin.setText("");
                            // Request focus back on edit text
                            editTextEnteredPin.requestFocus();
                            PasswordSecurityDialog.wrongPasswordEntered(
                                    passwordAlertDialog, false);
                            passwordAlertDialog.getPositiveButton().setEnabled(
                                    false);
                            A4TVToast toast = new A4TVToast(activity);
                            toast.showToast(R.string.tv_menu_network_wireless_settings_enter_password_error_message);
                        }
                    }
                });
        passwordAlertDialog.setNegativeButton(R.string.button_text_cancel,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        passwordAlertDialog.cancel();
                    }
                });
        passwordAlertDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                programLocking.cancel();
            }
        });
    }

    /** Show password dialog */
    public void showPasswordDialog() {
        // There is no â€œNo Attemptâ€� period activated
        PasswordSecurityDialog.wrongPasswordEntered(null, false);
        if (!PasswordSecurityDialog.waitFor10Minutes) {
            passwordAlertDialog.show();
            editTextEnteredPin.setText("");
            editTextEnteredPin.requestFocus();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    programLocking.cancel();
                    // Show message
                    A4TVToast toast = new A4TVToast(activity);
                    toast.showToast(R.string.enter_password_no_more_attempts_active);
                }
            }, 1500);
        }
    }

    /** OnKeyListener for program locking dialog */
    private class ProgramLockingOnKey implements OnKeyListener {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                    // ///////////////////////////////////////////////
                    // CHANNEL UP
                    // ///////////////////////////////////////////////
                        case KeyEvent.KEYCODE_F4:
                        case KeyEvent.KEYCODE_CHANNEL_UP: {
                            if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                                changeChannelUp(((MainActivity) activity));
                                passwordAlertDialog.cancel();
                            }
                            return true;
                        }
                        // ///////////////////////////////////////////////////
                        // CHANNEL DOWN
                        // ///////////////////////////////////////////////////
                        case KeyEvent.KEYCODE_F3:
                        case KeyEvent.KEYCODE_CHANNEL_DOWN: {
                            if (MainKeyListener.getAppState() == MainKeyListener.CLEAN_SCREEN) {
                                changeChannelDown(((MainActivity) activity));
                                passwordAlertDialog.cancel();
                            }
                            return true;
                        }
                        // ///////////////////////////////////////////////////////////////////////
                        // MAIN MENU
                        // ////////////////////////////////////////////////////////////////////////
                        case KeyEvent.KEYCODE_MENU:
                        case KeyEvent.KEYCODE_M: {
                            if (MainKeyListener.enableKeyCodeMenu) {
                                passwordAlertDialog.cancel();
                                // Show main menu
                                MainKeyListener
                                        .setAppState(MainKeyListener.MAIN_MENU);
                                // If main menu isn't created create it
                                if (((MainActivity) activity)
                                        .getMainMenuHandler() == null) {
                                    ((MainActivity) activity).initMainMenu();
                                }
                                // Show main menu
                                ((MainActivity) activity).getMainMenuHandler()
                                        .showMainMenu();
                            }
                            return true;
                        }
                        // //////////////////////////////////////////////////////////////
                        // VOLUME UP
                        // //////////////////////////////////////////////////////////////
                        // case KeyEvent.KEYCODE_F6:
                        case KeyEvent.KEYCODE_VOLUME_UP: {
                            IOSDHandler mCurlHandler = ((MainActivity) activity)
                                    .getPageCurl();
                            mCurlHandler.volume(VOLUME_UP, false);
                            return true;
                        }
                        // ///////////////////////////////////////////////////////////////////
                        // VOLUME DOWN
                        // ///////////////////////////////////////////////////////////////////
                        // case KeyEvent.KEYCODE_F5:
                        case KeyEvent.KEYCODE_VOLUME_DOWN: {
                            IOSDHandler mCurlHandler = ((MainActivity) activity)
                                    .getPageCurl();
                            mCurlHandler.volume(VOLUME_DOWN, false);
                            return true;
                        }
                        // ///////////////////////////////////////////////////////////////////
                        // VOLUME MUTE
                        // ///////////////////////////////////////////////////////////////////
                        case KeyEvent.KEYCODE_MUTE: {
                            IOSDHandler curlHandler = ((MainActivity) activity)
                                    .getPageCurl();
                            curlHandler.volume(VOLUME_MUTE, false);
                            return true;
                        }
                        // /////////////////////////////////////////////////////
                        // INFO BANNER
                        // //////////////////////////////////////////////////////
                        case KeyEvent.KEYCODE_I:
                        case KeyEvent.KEYCODE_INFO: {
                            IOSDHandler curlHandler = ((MainActivity) activity)
                                    .getPageCurl();
                            curlHandler.info();
                            return true;
                        }
                        // TODO
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
    public interface ProgramLocking {
        public void pinIsOk();

        public void cancel();
    }

    public A4TVAlertDialog getPasswordAlertDialog() {
        return passwordAlertDialog;
    }
}
