package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVDialog;
import com.iwedia.gui.epg.EPGHandlingClass;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.IOSDHandler;

import java.util.ArrayList;

/**
 * Dialog for EPG
 * 
 * @author Branimir Pavlovic
 */
public class EPGDialog extends A4TVDialog implements A4TVDialogInterface {
    public static final String TAG = "EPGDialog";
    private final int WIDTH_720p = 1280, HEIGHT_720p = 720, WIDTH_1080p = 1920,
            HEIGHT_1080p = 1080, TIME_TO_WAIT = 2000;
    private EPGHandlingClass epgHandler;
    private boolean canIGo = true;
    private boolean first, last;

    /** Constructor */
    public EPGDialog(Context context) {
        super(context, checkTheme(context), 0);
        // set content to dialog
        fillDialog();
        setDialogAttributes();
        super.setMenuButtonEnabled(false);
    }

    @Override
    public void fillDialog() {
        setContentView(R.layout.epg_main_overscale);
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        // left or right
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (epgHandler != null) {
            switch (keyCode) {
            // input filter right
                case KeyEvent.KEYCODE_B:
                case KeyEvent.KEYCODE_PROG_BLUE: {
                    Log.d(TAG,
                            "ON KEY LISTENER ********************************BLUE******************************");
                    epgHandler.selectNextFilter();
                    return true;
                }
                // input filter left
                case KeyEvent.KEYCODE_R:
                case KeyEvent.KEYCODE_PROG_RED: {
                    Log.d(TAG,
                            "ON KEY LISTENER ********************************RED******************************");
                    epgHandler.changeEPGZoom();
                    return true;
                }
                // schedule recording dialog
                case KeyEvent.KEYCODE_Y:
                case KeyEvent.KEYCODE_PROG_YELLOW: {
                    Log.d(TAG,
                            "ON KEY LISTENER ********************************YELLOW******************************");
                    // epgHandler.selectPreviousFilter();
                    epgHandler.openScheduleRecordingDialog();
                    return true;
                }
                // reminders
                case KeyEvent.KEYCODE_G:
                case KeyEvent.KEYCODE_PROG_GREEN: {
                    Log.d(TAG,
                            "ON KEY LISTENER ********************************GREEN******************************");
                    epgHandler.openReminderDialog();
                    return true;
                }
                case KeyEvent.KEYCODE_F3:
                case KeyEvent.KEYCODE_GUIDE:
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_E:
                case KeyEvent.KEYCODE_SEARCH: {
                    MainKeyListener.returnToStoredAppState();
                    epgHandler.stopThread();
                    epgHandler.clearViewsFromScreenAndData();
                    epgHandler.dayInWeekToLoadData = 1;
                    epgHandler.scrollView.scrollTo(0, 0);
                    if (epgHandler.currentFilterFromContentList > -1) {
                        /** Return filter to filter before entering EPG */
                        try {
                            MainActivity.service
                                    .getContentListControl()
                                    .setActiveFilter(
                                            epgHandler.currentFilterFromContentList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /** Return value to default */
                        epgHandler.currentFilterFromContentList = -1;
                    }
                    EPGDialog.this.cancel();
                    // epgHandler.hideEPGDialog();
                    return true;
                }
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9: {
                    epgHandler.showEPGToServiceByNumber(keyCode);
                    return true;
                }
                // go left
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    if (canIGo) {
                        canIGo = false;
                        Log.d(TAG,
                                "ON KEY LISTENER ********************************LEFT******************************");
                        epgHandler.goLeft(true);
                        /** Enable left in one second */
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                canIGo = true;
                            }
                        }, TIME_TO_WAIT);
                    }
                    return true;
                }
                // go right
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    if (canIGo) {
                        canIGo = false;
                        Log.d(TAG,
                                "ON KEY LISTENER ********************************RIGHT******************************");
                        epgHandler.goRigth(true);
                        /** Enable right in one second */
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                canIGo = true;
                            }
                        }, TIME_TO_WAIT);
                    }
                    return true;
                }
                // //////////////////////////////////////////////////////////////
                // VOLUME UP
                // //////////////////////////////////////////////////////////////
                // case KeyEvent.KEYCODE_F6:
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    IOSDHandler mCurlHandler = MainActivity.activity
                            .getPageCurl();
                    mCurlHandler.volume(VOLUME_UP, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME DOWN
                // ///////////////////////////////////////////////////////////////////
                // case KeyEvent.KEYCODE_F5:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler mCurlHandler = MainActivity.activity
                            .getPageCurl();
                    mCurlHandler.volume(VOLUME_DOWN, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME MUTE
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = MainActivity.activity
                            .getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, false);
                    return true;
                }
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
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

    // not needed here
    @Override
    public void returnArrayListsWithDialogContents(
            ArrayList<ArrayList<Integer>> contentList,
            ArrayList<ArrayList<Integer>> contentListIDs,
            ArrayList<Integer> titleIDs) {
    }

    public EPGHandlingClass getEpgHandler() {
        return epgHandler;
    }

    public void setEpgHandler(EPGHandlingClass epgHandler) {
        this.epgHandler = epgHandler;
    }
}
