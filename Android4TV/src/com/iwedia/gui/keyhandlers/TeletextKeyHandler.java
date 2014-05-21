package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.teletext.TeletextMode;
import com.iwedia.dtv.types.UserControl;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.graphics.TeletextDialogView;
import com.iwedia.gui.listeners.MainKeyListener;

public class TeletextKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "TeletextKeyListener";
    private MainActivity activity;

    public TeletextKeyHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "- keycode " + keyCode);
            TeletextDialogView teletextDialogView = activity
                    .getTeletextDialogView();
            TeletextMode teletextMode;
            // if app receives TELETEXT key while in teletext state - change
            // teletext mode or turn it off (FULL MODE -> MIX MODE -> OFF).
            if ((keyCode == KeyEvent.KEYCODE_F2)
                    || (keyCode == KeyEvent.KEYCODE_F5)) {
                try {
                    teletextMode = teletextDialogView.getMode();
                    Log.d(TAG, "Teletext mode - " + teletextMode);
                    if (teletextMode == TeletextMode.FULL) {
                        teletextDialogView.show(TeletextMode.HALF, 0);
                    } else if (teletextMode == TeletextMode.HALF) {
                        teletextDialogView.show(TeletextMode.MIX, 0);
                        // MainActivity.service.getTeletextControl().setTeletextMode(TeletextMode.MIX);
                        // MainActivity.service.getTeletextControl().show(TeletextMode.MIX);
                    } else {
                        if (!teletextDialogView.hide()) {
                            Log.d(TAG, "Problem hiding teletext!");
                        } else {
                            if (MainActivity.activity.getSubtitleDialogView()
                                    .isOn()) {
                                int index = MainActivity.service
                                        .getSubtitleControl()
                                        .getCurrentSubtitleTrackIndex();
                                if (index > -1) {
                                    MainActivity.activity
                                            .getSubtitleDialogView()
                                            .show(index);
                                }
                            }
                            /*
                             * if (MainActivity.showSubtitleWhenTeletextHide &&
                             * MainActivity.subtitleTitleTrackIndex >= 0 &&
                             * MainActivity.subtitleON == true) {
                             * MainActivity.showSubtitleDialog
                             * (MainActivity.subtitleTitleTrackIndex);
                             * MainActivity.subtitleTitleTrackIndex = -1;
                             * MainActivity.showSubtitleWhenTeletextHide =
                             * false; }
                             */
                        }
                        teletextDialogView.setMode(TeletextMode.OFF);
                        MainKeyListener.returnToStoredAppState();
                    }
                } catch (RemoteException e) {
                    System.out.println("Teletext can't be displayed/hidden!");
                    e.printStackTrace();
                }
            } else { // send key to teletext module.
                try {
                    MainActivity.service.getTeletextControl().sendInputControl(
                            keyCode, UserControl.PRESSED);
                } catch (RemoteException e) {
                    System.out
                            .println("Problem sending input control to teletext.");
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }
}
