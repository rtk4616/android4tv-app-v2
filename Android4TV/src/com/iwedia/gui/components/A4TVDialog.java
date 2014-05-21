package com.iwedia.gui.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.dialogs.ChannelScanDialog;
import com.iwedia.gui.components.dialogs.MultimediaShowDialog;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.ArrayList;

/**
 * Dialog with theme change
 * 
 * @author Branimir Pavlovic
 */
public abstract class A4TVDialog extends Dialog implements OSDGlobal {
    public static final String TAG = "A4TVDialog";
    private boolean menuEnabled = true;
    private static ArrayList<A4TVDialog> mA4tvDialogs = null;
    private int mDescriptionId = 0;

    public class DisplayMode {
        public static final int HIDE = 0;
        public static final int DISABLE = 1;
        public static final int SHOW = 2;
    };

    public A4TVDialog(Context context, int theme, int descriptionId) {
        super(context, theme);
        this.mDescriptionId = descriptionId;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // hide statusbar of Android
        // could also be done later
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        getWindow().getDecorView().getBackground().setDither(true);
        if (mA4tvDialogs == null) {
            mA4tvDialogs = new ArrayList<A4TVDialog>();
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void show() {
        if (!(this instanceof MultimediaShowDialog)) {
            mA4tvDialogs.add(0, this);
        }
        super.show();
    }

    @Override
    public void cancel() {
        if (mA4tvDialogs.size() > 0) {
            int index = mA4tvDialogs.indexOf(this);
            if (index > -1) {
                mA4tvDialogs.remove(index);
            }
        }
        super.cancel();
    }

    @Override
    public void hide() {
        // fix for scan dialog while scanning
        if (this instanceof ChannelScanDialog && ChannelScanDialog.isScanning()) {
            super.hide();
            return;
        }
        super.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MainActivity.activity.getScreenSaverDialog().updateScreensaverTimer();
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_M: {
                if (MainActivity.isInFirstTimeInstall) {
                    return true;
                }
                if (MainActivity.sharedPrefs.getBoolean(
                        MainActivity.SERVICE_MODE_START, false)) {
                    return true;
                }
                if (menuEnabled) {
                    cancel();
                    // close all other dialogs
                    MainActivity.activity.getDialogManager().hideAllDialogs();
                    MainKeyListener.returnToStoredAppState();
                    return true;
                } else {
                    return true;
                }
            }
            case KeyEvent.KEYCODE_INFO: {
                Log.i(TAG, "list Lenght: " + mA4tvDialogs.size());
                MainActivity.activity.getPageCurl().info();
                // cancelDialogs();
                return true;
            }
            // ///////////////////////////////////////////////////////////////////
            // VOLUME UP
            // ///////////////////////////////////////////////////////////////////
            // case KeyEvent.KEYCODE_F6:
            case KeyEvent.KEYCODE_VOLUME_UP: {
                IOSDHandler curlHandler = MainActivity.activity.getPageCurl();
                curlHandler.volume(VOLUME_UP, false);
                return true;
            }
            // ///////////////////////////////////////////////////////////////////
            // VOLUME DOWN
            // ///////////////////////////////////////////////////////////////////
            // case 135: {
            // case KeyEvent.KEYCODE_F5: {
            case KeyEvent.KEYCODE_VOLUME_DOWN: {
                IOSDHandler curlHandler = MainActivity.activity.getPageCurl();
                curlHandler.volume(VOLUME_DOWN, false);
                return true;
            }
            // ///////////////////////////////////////////////////////////////////
            // VOLUME MUTE
            // ///////////////////////////////////////////////////////////////////
            case KeyEvent.KEYCODE_MUTE: {
                IOSDHandler curlHandler = MainActivity.activity.getPageCurl();
                curlHandler.volume(VOLUME_MUTE, false);
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setMenuButtonEnabled(boolean isEnabled) {
        menuEnabled = isEnabled;
    }

    // private void cancelDialogs() {
    // this.cancel();
    // }
    public static ArrayList<A4TVDialog> getListOfDialogs() {
        return mA4tvDialogs;
    }

    public void setLayoutDisplayMode(int layoutID, int displayMode) {
        LinearLayout layout = (LinearLayout) findViewById(layoutID);
        if (layout == null) {
            Log.e(TAG, "Layout ID is not valid");
            return;
        }
        switch (displayMode) {
            case DisplayMode.HIDE:
                /*
                 * TODO HIDE properly, lines still stay and we don't have
                 * generic way to calculate id's
                 */
                // layout.setVisibility(View.INVISIBLE);
                return;
            case DisplayMode.DISABLE: {
                layout.setVisibility(View.VISIBLE);
                int childComponentCount = layout.getChildCount();
                for (int i = 0; i < childComponentCount; i++) {
                    layout.getChildAt(i).setEnabled(false);
                    layout.getChildAt(i).setFocusable(false);
                }
            }
                break;
            case DisplayMode.SHOW: {
                layout.setVisibility(View.VISIBLE);
                int childComponentCount = layout.getChildCount();
                for (int i = 0; i < childComponentCount; i++) {
                    layout.getChildAt(i).setEnabled(true);
                    layout.getChildAt(i).setFocusable(true);
                }
            }
                break;
            default:
                return;
        }
    }
}
