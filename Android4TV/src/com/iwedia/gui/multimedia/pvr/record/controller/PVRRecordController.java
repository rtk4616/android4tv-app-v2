package com.iwedia.gui.multimedia.pvr.record.controller;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVProgressBarPVR.ControlProviderPVR;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.OSDGlobal;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.pvr.PVRHandler;

/**
 * @author Milos Milanovic
 */
public class PVRRecordController extends ControlProviderPVR implements
        OSDGlobal {
    private final String TAG = "PVRRecordController";
    private PVRHandler mPvrHandler = null;

    public PVRRecordController(PVRHandler pvrHandler) {
        this.mPvrHandler = pvrHandler;
    }

    @Override
    public void stop() {
        if (mPvrHandler.pvrStop()) {
            prepareStop();
        }
    }

    @Override
    public void rewind() {
        if (mPvrHandler.pvrRewind()) {
            this.setFlagPlay(false);
        }
    }

    @Override
    public void resume() {
        if (mPvrHandler.pvrPlay()) {
            this.setFlagPlay(true);
            // CurlHandler.multimediaControllerPVR();
        } else {
            this.setFlagPlay(false);
            // CurlHandler.multimediaControllerPVR();
        }
    }

    @Override
    public void play() {
        if (mPvrHandler.pvrPlay()) {
            this.setFlagPlay(true);
        } else {
            this.setFlagPlay(false);
        }
    }

    @Override
    public void pause() {
        if (mPvrHandler.pvrPause()) {
            this.setFlagPlay(false);
            this.setFlagRecord(true);
            PVRHandler.setStringsForRecord();
        } else {
            this.setFlagPlay(true);
        }
    }

    @Override
    public void fastForward() {
        if (mPvrHandler.pvrFastForward()) {
            this.setFlagPlay(false);
        }
    }

    @Override
    public void record() {
        if (mPvrHandler.pvrRecord()) {
            PVRHandler.prepareRecord();
        }
    }

    @Override
    public void prepareStop() {
        this.setFlagPlay(false);
        this.setFlagRecord(false);
        this.setFirstTime(0);
        this.setSecondTime(0);
        this.setProgressValue(-1);
        this.setSecondaryProgressValue(-1);
        this.setElapsedTime(0);
        this.setDuration(0);
        if (this.isDiskFull()) {
            ControlProviderPVR.setFileDescription(MainActivity.activity
                    .getApplicationContext().getString(R.string.pvr_disk_full));
        } else {
            ControlProviderPVR.setFileDescription(MainActivity.activity
                    .getApplicationContext().getString(R.string.stop));
        }
        OSDHandlerHelper.setHandlerState(CURL_HANDLER_STATE_DO_NOTHING);
        // ZORANA - should delete this?
        MainKeyListener.setAppState(MainKeyListener.CLEAN_SCREEN);
    }
}
