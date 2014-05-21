package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.content_list.ContentListHandler;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.IOSDHandler;

public class ContentListKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "ContentListKeyHandler";
    private MainActivity activity;

    public ContentListKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "KeyCode: " + keyCode);
            switch (keyCode) {
            // //////////////////////////////////////
            // CLOSE
            // ///////////////////////////////////////
                case KeyEvent.KEYCODE_BACK: {
                    try {
                        if (MainKeyListener.contentListFromMainMenu) {
                            activity.getContentListHandler().closeContentList();
                        } else {
                            activity.getContentListHandler().closeContentList();
                            MainKeyListener.contentListFromMainMenu = false;
                        }
                        ContentListHandler.syncFilterIndexes(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                // /////////////////////////////////////////////////////
                // INFO BANNER
                // //////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_I:
                case KeyEvent.KEYCODE_INFO: {
                    activity.getPageCurl().info();
                    return true;
                }
                // //////////////////////////////////////
                // FILTER NEXT
                // //////////////////////////////////////
                case KeyEvent.KEYCODE_P:
                case KeyEvent.KEYCODE_CHANNEL_UP:
                case KeyEvent.KEYCODE_F4: {
                    // Move selection to next option and do proper filtering
                    // of content
                    // activity.getContentListHandler().selectNextFilter();
                    return true;
                }
                // //////////////////////////////////////
                // FILTER PREVIOUS
                // //////////////////////////////////////
                case KeyEvent.KEYCODE_R:
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                case KeyEvent.KEYCODE_F3: {
                    // Move selection to previous option and do proper filtering
                    // of content
                    // activity.getContentListHandler().selectPreviousFilter();
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME UP
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F8:
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_UP, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME DOWN
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_F7:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_DOWN, false);
                    return true;
                }
                // ///////////////////////////////////////////////////////////////////
                // VOLUME MUTE
                // ///////////////////////////////////////////////////////////////////
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = activity.getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, false);
                    return true;
                }
                default:
                    return false;
            }
        }
        return false;
    }
}
