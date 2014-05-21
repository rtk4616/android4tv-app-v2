package com.iwedia.gui.keyhandlers;

import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.osd.IOSDHandler;
import com.iwedia.gui.osd.OSDHandlerHelper;
import com.iwedia.gui.osd.infobanner.InfoBannerHandler;
import com.iwedia.gui.osd.noneinfobanner.NoneBannerHandler;

/**
 * KeyHandler for PVR.
 */
public class PVRKeyHandler extends AppStateKeyHandler {
    private final String LOG_TAG = "PVRKeyListener";
    private MainActivity mActivity = null;
    private MainKeyListener mKeyListener = null;

    public PVRKeyHandler(MainActivity activity, MainKeyListener keyListener) {
        mActivity = activity;
        mKeyListener = keyListener;
    }

    @Override
    public boolean onKeyPressed(View v, DialogInterface dialog, int keyCode,
            KeyEvent event, boolean isFromMheg) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.d(LOG_TAG, "KeyCode: " + keyCode);
            switch (keyCode) {
            // Channel Change by Number
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                case KeyEvent.KEYCODE_F10: {
                    if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING
                            && !A4TVProgressBarPVR
                                    .getControlProviderPVR()
                                    .getFileDescription()
                                    .equals(mActivity.getApplicationContext()
                                            .getString(R.string.shedule_record))) {
                        IOSDHandler curlHandler = mActivity.getPageCurl();
                        curlHandler.changeChannelByNum(
                                mActivity.getMainKeyListener()
                                        .generateChannelNumber(keyCode), 0);
                    }
                    return true;
                }
                // Channel Up
                case KeyEvent.KEYCODE_CHANNEL_UP:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_F4: {
                    if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING
                            && !A4TVProgressBarPVR
                                    .getControlProviderPVR()
                                    .getFileDescription()
                                    .equals(mActivity.getApplicationContext()
                                            .getString(R.string.shedule_record))) {
                        if (OSDHandlerHelper.getHandlerState() != STATE_INFO_BANNER_SHOWN
                                || (OSDHandlerHelper.getHandlerState() == STATE_INFO_BANNER_SHOWN
                                        && mActivity.getPageCurl() instanceof InfoBannerHandler && !InfoBannerHandler.description)
                                || mActivity.getPageCurl() instanceof NoneBannerHandler) {
                            try {
                                if (FilterType.INPUTS == MainActivity.service
                                        .getContentListControl()
                                        .getActiveContent(0).getFilterType()) {
                                    A4TVToast toast = new A4TVToast(mActivity);
                                    toast.showToast(R.string.not_supported_action_for_input);
                                    return true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!mActivity
                                    .dualVideoActionHandler(CHANNEL_UP, 0)) {
                                return true;
                            }
                            mActivity.getMainKeyListener().changeChannelUp();
                        } else {
                            mActivity.getPageCurl().scroll(1);
                        }
                    }
                    return true;
                }
                // Channel Down
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_F3: {
                    if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING
                            && !A4TVProgressBarPVR
                                    .getControlProviderPVR()
                                    .getFileDescription()
                                    .equals(mActivity.getApplicationContext()
                                            .getString(R.string.shedule_record))) {
                        if (OSDHandlerHelper.getHandlerState() != STATE_INFO_BANNER_SHOWN
                                || (OSDHandlerHelper.getHandlerState() == STATE_INFO_BANNER_SHOWN
                                        && mActivity.getPageCurl() instanceof InfoBannerHandler && !InfoBannerHandler.description)
                                || mActivity.getPageCurl() instanceof NoneBannerHandler) {
                            try {
                                if (FilterType.INPUTS == MainActivity.service
                                        .getContentListControl()
                                        .getActiveContent(0).getFilterType()) {
                                    A4TVToast toast = new A4TVToast(mActivity);
                                    toast.showToast(R.string.not_supported_action_for_input);
                                    return true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!mActivity.dualVideoActionHandler(CHANNEL_DOWN,
                                    0)) {
                                return true;
                            }
                            mActivity.getMainKeyListener().changeChannelDown();
                        } else {
                            mActivity.getPageCurl().scroll(-1);
                        }
                    }
                    return true;
                }
                // PIP
                case KeyEvent.KEYCODE_PROG_YELLOW:
                case KeyEvent.KEYCODE_Y: {
                    // Get active content object and stop secondary display
                    // playback
                    Log.d(LOG_TAG, "YELLOW KEY ");
                    if (MainActivity.activity.getDualVideoManager().isPiP()) {
                        MainActivity.activity.getDualVideoManager().stop(
                                MainActivity.SECONDARY_DISPLAY_UNIT_ID);
                        return true;
                    }
                    // TODO: move to dualVideoManager
                    // check if renderer
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() != null) {
                        if (MainActivity.activity.getRendererController()
                                .getmRendererState() != 0) {
                            MainActivity.activity.getRendererController()
                                    .stop();
                        } else {
                            if (MainActivity.activity.getMultimediaMode() == MainActivity.MULTIMEDIA_PIP) {
                                MainActivity.activity
                                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PIP);
                            }
                        }
                        return true;
                    }
                    return false;
                }
                // PAP
                case KeyEvent.KEYCODE_PROG_BLUE:
                case KeyEvent.KEYCODE_B: {
                    Log.d(LOG_TAG, "BLUE KEY ");
                    if (MainActivity.activity.getDualVideoManager().isPaP()) {
                        if (MainActivity.activity.getDualVideoManager().stop(
                                MainActivity.SECONDARY_DISPLAY_UNIT_ID)) {
                            return true;
                        }
                    }
                    // TODO: move to dualVideoManager
                    // check if renderer
                    Log.d(LOG_TAG,
                            "IS pap returned false - check if renderer ... ");
                    if (MainActivity.activity.getPrimaryMultimediaVideoView() != null) {
                        if (MainActivity.activity.getRendererController()
                                .getmRendererState() != 0) {
                            MainActivity.activity.getRendererController()
                                    .stop();
                        } else {
                            if (MainActivity.activity.getMultimediaMode() == MainActivity.MULTIMEDIA_PAP) {
                                MainActivity.activity
                                        .stopMultimediaVideo(MainActivity.MULTIMEDIA_PAP);
                            }
                        }
                        return true;
                    }
                    return false;
                }
                // Move Focus Left on PVR OSD Controls
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    mCurlHandler.multimediaControllerMoveLeft();
                    return true;
                }
                // Move Focus Right on PVR OSD Controls
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    mCurlHandler.multimediaControllerMoveRight();
                    return true;
                }
                // Do Click on Selected PVR OSD Control
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER: {
                    Log.d(LOG_TAG, "KeyCode Enter PVR");
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    mCurlHandler.multimediaControllerClick(false);
                    return true;
                }
                // Show Info Banner
                case KeyEvent.KEYCODE_INFO:
                case KeyEvent.KEYCODE_SPACE: {
                    mActivity.getPageCurl().info();
                    return true;
                }
                // Turn Off TV(STB)
                case KeyEvent.KEYCODE_P: {
                    if (OSDHandlerHelper.getHandlerState() == PVR_STATE_RECORDING) {
                        final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                                mActivity);
                        askDialog.setTitleOfAlertDialog(
                                R.string.stop_recording_message).setCancelable(
                                false);
                        askDialog.setPositiveButton(R.string.button_text_yes,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        IOSDHandler mCurlHandler = mActivity
                                                .getPageCurl();
                                        A4TVProgressBarPVR
                                                .setControlPosition(A4TVProgressBarPVR.MULTIMEDIA_CONTROLLER_STOP);
                                        mCurlHandler
                                                .multimediaControllerClick(true);
                                        askDialog.cancel();
                                        try {
                                            MainActivity.service
                                                    .getSetupControl()
                                                    .rebootTV();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        askDialog.setNegativeButton(R.string.button_text_no,
                                new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        askDialog.cancel();
                                        return;
                                    }
                                });
                        askDialog.show();
                    }
                    return true;
                }
                // Back
                // Stop PlayBack
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_MEDIA_STOP: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    A4TVProgressBarPVR
                            .setControlPosition(A4TVProgressBarPVR.MULTIMEDIA_CONTROLLER_STOP);
                    mCurlHandler.multimediaControllerClick(true);
                    return true;
                }
                // Play & Pause & Resume
                case 126:
                case 127:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    A4TVProgressBarPVR
                            .setControlPosition(A4TVProgressBarPVR.MULTIMEDIA_CONTROLLER_PLAY);
                    mCurlHandler.multimediaControllerClick(true);
                    return true;
                }
                // FF & Next
                case 87:
                case 125:
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    A4TVProgressBarPVR
                            .setControlPosition(A4TVProgressBarPVR.MULTIMEDIA_CONTROLLER_FF_NEXT);
                    mCurlHandler.multimediaControllerClick(true);
                    return true;
                }
                // REW & Previous
                case 88:
                case KeyEvent.KEYCODE_MEDIA_REWIND: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    A4TVProgressBarPVR
                            .setControlPosition(A4TVProgressBarPVR.MULTIMEDIA_CONTROLLER_REW_PREVIOUS);
                    mCurlHandler.multimediaControllerClick(true);
                    return true;
                }
                // Volume Up
                case KeyEvent.KEYCODE_F8:
                case KeyEvent.KEYCODE_VOLUME_UP: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    mCurlHandler.volume(VOLUME_UP, true);
                    return true;
                }
                // Volume Down
                case KeyEvent.KEYCODE_F7:
                case KeyEvent.KEYCODE_VOLUME_DOWN: {
                    IOSDHandler mCurlHandler = mActivity.getPageCurl();
                    mCurlHandler.volume(VOLUME_DOWN, true);
                    return true;
                }
                // Volume Mute
                case KeyEvent.KEYCODE_MUTE: {
                    IOSDHandler curlHandler = mActivity.getPageCurl();
                    curlHandler.volume(VOLUME_MUTE, true);
                    return true;
                }
                // Show Dialog for Available Audio Languages
                case KeyEvent.KEYCODE_F6:
                case KeyEvent.KEYCODE_A: {
                    // mKeyListener.getCleanScreenHandler().showAudio();
                    return true;
                }
                // Teletext
                case KeyEvent.KEYCODE_T:
                case KeyEvent.KEYCODE_F5: {
                    // mKeyListener.getCleanScreenHandler().showTeletext();
                    return true;
                }
                // Subtitle
                case KeyEvent.KEYCODE_S:
                case KeyEvent.KEYCODE_CAPTIONS: {
                    // mKeyListener.getCleanScreenHandler().showSubtitle();
                    return true;
                }
            }
        }
        return false;
    }
}
