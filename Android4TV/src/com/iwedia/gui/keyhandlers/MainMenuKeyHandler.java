package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.config_handler.ConfigGeneratorActivity;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.mainmenu.MainMenuContent;
import com.iwedia.gui.mainmenu.gallery.animations.TransitionItemAnimationHandler;
import com.iwedia.gui.osd.IOSDHandler;

import java.util.Timer;
import java.util.TimerTask;

public class MainMenuKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "MainMenuKeyListener";
    /** ConfigHandler Generator */
    private final int CONFIGFILE_DURATION = 3000;
    private MainActivity mActivity = null;
    /** ConfigHandler Generator */
    private int mConfigHandlerCounter = 0;
    private Timer mConfigHandlerTimer = null;
    private TimerTask mConfigHandlerTimerTask = null;

    public MainMenuKeyHandler(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "- keycode " + keyCode);
            switch (keyCode) {
            // /////////////////////////////////////
            // CLOSE
            // /////////////////////////////////////
                case KeyEvent.KEYCODE_BACK: {
                    // Check if main menu dialog is showing and its not in
                    // main menu
                    if (mActivity.getMainMenuHandler() != null) {
                        if (mActivity.getMainMenuHandler().getMainMenuDialog()
                                .isShowing()
                                && MainMenuContent.currentState != MainMenuContent.MAIN_MENU) {
                            // Delay starting of translate animation
                            Handler delay = new Handler();
                            delay.postDelayed(new Runnable() {
                                public void run() {
                                    // ///////////////////////////
                                    // If back key is enabled
                                    // ///////////////////////////
                                    if (MainKeyListener.enableKeyCodeBack
                                            || MainActivity.service == null) {
                                        // Start transition
                                        mActivity
                                                .getMainMenuHandler()
                                                .getTransitionItemAnimHandler()
                                                .translate(
                                                        mActivity
                                                                .getMainMenuHandler()
                                                                .getFlip3dAnimationHandler()
                                                                .getImage(),
                                                        mActivity
                                                                .getMainMenuHandler()
                                                                .getTransitionItemAnimHandler()
                                                                .getSubmenuRootImage(),
                                                        TransitionItemAnimationHandler.ANIMATE_DOWN);
                                    }
                                }
                            }, 10);
                        }
                        // If it is in main menu...and close it
                        if (mActivity.getMainMenuHandler().getMainMenuDialog()
                                .isShowing()
                                && MainMenuContent.currentState == MainMenuContent.MAIN_MENU) {
                            mActivity.getMainMenuHandler().closeMainMenu(true);
                        }
                        return true;
                    }
                    return true;
                }
                // /////////////////////////////////////////////////////
                // INFO BANNER
                // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_I:
                case KeyEvent.KEYCODE_INFO: {
                    mActivity.getPageCurl().info();
                    return true;
                }
                // ////////////////////////////////////////////
                // CLOSE 2
                // ////////////////////////////////////////////
                case KeyEvent.KEYCODE_TAB:
                case KeyEvent.KEYCODE_MENU:
                case KeyEvent.KEYCODE_M: {
                    mActivity.getMainMenuHandler().closeMainMenu(true);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME UP
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F8:
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    IOSDHandler curlHandler = mActivity.getPageCurl();
                    curlHandler.volume(VOLUME_UP, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME DOWN
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F7:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler curlHandler = mActivity.getPageCurl();
                    curlHandler.volume(VOLUME_DOWN, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME MUTE
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = mActivity.getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, false);
                    return true;
                }
                // Activate ConfigHandler Generator
                case KeyEvent.KEYCODE_MEDIA_RECORD: {
                    mConfigHandlerCounter++;
                    startConfigFileGenerator(CONFIGFILE_DURATION);
                }
                default:
                    return false;
            }
        }
        // Nothing happened
        return false;
    }

    /**
     * Start ConfigFile Generator
     */
    public void startConfigFileGenerator(final int milliseconds) {
        if (null != mConfigHandlerTimer) {
            mConfigHandlerTimer.purge();
            if (null != mConfigHandlerTimerTask)
                mConfigHandlerTimerTask.cancel();
            mConfigHandlerTimerTask = null;
            mConfigHandlerTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (mConfigHandlerCounter >= 5) {
                        mActivity.startActivity(new Intent(mActivity,
                                ConfigGeneratorActivity.class));
                        mActivity.finish();
                    } else {
                        mConfigHandlerCounter = 0;
                    }
                }
            };
            mConfigHandlerTimer.schedule(mConfigHandlerTimerTask, milliseconds);
        } else {
            mConfigHandlerTimer = new Timer();
            startConfigFileGenerator(CONFIGFILE_DURATION);
        }
    }
}
