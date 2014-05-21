package com.iwedia.gui.osd;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.ipcontent.IpContent;
import com.iwedia.comm.content.service.ServiceContent;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.program_blocking.ProgramLockingHandler;
import com.iwedia.gui.program_blocking.ProgramLockingHandler.ProgramLocking;

/**
 * @author Branimir Pavlovic
 */
public class CheckServiceType {
    private final static String TAG = "CheckServiceType";
    private MainActivity ctx;
    /** Layout place holder for no signal graphic layer */
    private RelativeLayout noSignal;
    private LinearLayout scrambled, radio, data;
    private static LinearLayout all;
    private LinearLayout locked;
    private LinearLayout parental;
    public static Handler handler;
    public static boolean isScrambled = false, isRadio = false, isData = false,
            isIP = false, isLocked = false, isMhegPresent = false;
    private ProgramLocking mProgramBlockingInterface = null;
    private ProgramLockingHandler mProgramBlockingHandler = null;
    private Handler mHandler = null;
    private Runnable mRunnable = null;
    private Content mContent = null;
    private final static int CHECK_SERVICE = 0, CLEAR_SCRAMBLED_SCREEN = 1,
            SHOW_PARENTAL_LAYER = 3, HIDE_PARENTAL_LAYER = 4,
            SHOW_NO_SIGNAL = 5, HIDE_NO_SIGNAL = 6;
    private int savedAudioTrack;
    private int savedVideoTrack;

    public CheckServiceType(MainActivity ctx) {
        this.ctx = ctx;
        getReferencesAndInit();
        initPasswordDialog();
        isScrambled = false;
        isRadio = false;
        isData = false;
        isIP = false;
        isLocked = false;
    }

    /**
     * Get references from HBB dialog
     */
    private void getReferencesAndInit() {
        scrambled = (LinearLayout) ctx.findViewById(R.id.linLayScrambled);
        radio = (LinearLayout) ctx.findViewById(R.id.linLayRadio);
        all = (LinearLayout) ctx.findViewById(R.id.linLayMessages);
        data = (LinearLayout) ctx.findViewById(R.id.linLayData);
        locked = (LinearLayout) ctx.findViewById(R.id.linLayLocked);
        parental = (LinearLayout) ctx.findViewById(R.id.linLayParentalControl);
        noSignal = (RelativeLayout) ctx
                .findViewById(R.id.noSignalAvailableLayout);
        // set initial views
        scrambled.setVisibility(View.GONE);
        radio.setVisibility(View.GONE);
        all.setVisibility(View.GONE);
        data.setVisibility(View.GONE);
        locked.setVisibility(View.GONE);
        parental.setVisibility(View.GONE);
        noSignal.setVisibility(View.GONE);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CHECK_SERVICE: {
                        Content content = (Content) msg.obj;
                        new CheckServiceTypeTask().execute(content);
                        break;
                    }
                    case CLEAR_SCRAMBLED_SCREEN: {
                        if (scrambled.isShown()) {
                            scrambled.setVisibility(View.GONE);
                            all.setVisibility(View.GONE);
                            radio.setVisibility(View.GONE);
                            data.setVisibility(View.GONE);
                        }
                        break;
                    }
                    case SHOW_PARENTAL_LAYER: {
                        all.setVisibility(View.VISIBLE);
                        parental.setVisibility(View.VISIBLE);
                        break;
                    }
                    case HIDE_PARENTAL_LAYER: {
                        parental.setVisibility(View.GONE);
                        if (!scrambled.isShown() && !radio.isShown()
                                && !data.isShown() && !locked.isShown()) {
                            all.setVisibility(View.GONE);
                        }
                        break;
                    }
                    case SHOW_NO_SIGNAL: {
                        MainActivity.screenSaverDialog
                                .setScreenSaverCause(MainActivity.screenSaverDialog.NO_SIGNAL);
                        MainActivity.screenSaverDialog.updateScreensaverTimer();
                        noSignal.setVisibility(View.VISIBLE);
                        break;
                    }
                    case HIDE_NO_SIGNAL: {
                        noSignal.setVisibility(View.GONE);
                        MainActivity.screenSaverDialog
                                .setScreenSaverCause(MainActivity.screenSaverDialog.LIVE);
                        MainActivity.screenSaverDialog.stopScreensaver();
                        MainActivity.screenSaverDialog.updateScreensaverTimer();
                        break;
                    }
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    /** Async Task for checking service type */
    private class CheckServiceTypeTask extends
            AsyncTask<Content, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Content... params) {
            Content content = params[0];
            isData = false;
            isRadio = false;
            isIP = false;
            // Disabled because of this flag is important for activating Alert
            // dialog
            // isLocked = false;
            if (content == null) {
                return true;
            }
            Log.e(TAG, "content.toString: " + content.toString());
            if (content instanceof ServiceContent) {
                switch (content.getServiceType()) {
                    case DATA_BROADCAST:
                        isData = true;
                        break;
                    case DIG_RAD:
                        isRadio = true;
                        // RadioContent rContent = (RadioContent) content;
                        break;
                }
            } else if (content instanceof IpContent) {
                isIP = true;
            }
            // ///////////////////////
            if (mContent != null) {
                if (!mContent.equals(content)) {
                    checkContentLock(content);
                    mContent = content;
                    if (isLocked) {
                        mHandler.post(mRunnable);
                    }
                }
            } else {
                mContent = content;
                checkContentLock(content);
                if (isLocked) {
                    mHandler.post(mRunnable);
                }
            }
            // ///////////////////////
            Log.d(TAG,
                    "CHECK SERVICE TYPEm, checking for service: "
                            + content.getName() + " scrambled:" + isScrambled
                            + " data:" + isData + " radio:" + isRadio + " IP:"
                            + isIP);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (isScrambled || isRadio || isData || isLocked) {
                try {
                    isMhegPresent = MainActivity.service.getMhegControl()
                            .isPresent();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (isMhegPresent) {
                    all.setVisibility(View.GONE);
                } else {
                    all.setVisibility(View.VISIBLE);
                }
                // if scrambled
                if (MainActivity.isCICardEntered) {
                    all.setVisibility(View.GONE);
                } else {
                    if (isScrambled) {
                        scrambled.setVisibility(View.VISIBLE);
                    } else {
                        scrambled.setVisibility(View.GONE);
                    }
                }
                // if radio
                if (isRadio) {
                    radio.setVisibility(View.VISIBLE);
                    MainActivity.screenSaverDialog
                            .setScreenSaverCause(MainActivity.screenSaverDialog.RADIO);
                    MainActivity.screenSaverDialog.updateScreensaverTimer();
                } else {
                    radio.setVisibility(View.GONE);
                }
                // if data
                if (isData) {
                    data.setVisibility(View.VISIBLE);
                } else {
                    data.setVisibility(View.GONE);
                }
                // if locked
                if (isLocked) {
                    locked.setVisibility(View.VISIBLE);
                } else {
                    locked.setVisibility(View.GONE);
                }
            } else {
                all.setVisibility(View.GONE);
            }
            // // Hide locked layer
            // locked.setVisibility(View.GONE);
            all.invalidate();
        }
    }

    /** Show locked layer */
    public void showLockedLayer() {
        // if locked
        all.setVisibility(View.VISIBLE);
        locked.setVisibility(View.VISIBLE);
    }

    /** Show locked layer */
    public static void showMhegData() {
        all.setVisibility(View.GONE);
    }

    public void showParentalControlLayer() {
        Log.d(TAG, "showParentalControlLayer");
        handler.sendEmptyMessage(SHOW_PARENTAL_LAYER);
    }

    public void hideParentalControlLayer() {
        Log.d(TAG, "hideParentalControlLayer");
        handler.sendEmptyMessage(HIDE_PARENTAL_LAYER);
    }

    private void initPasswordDialog() {
        // Create program locking handler
        mProgramBlockingInterface = new ProgramLocking() {
            @Override
            public void pinIsOk() {
                unlockService();
            }

            @Override
            public void cancel() {
                mProgramBlockingHandler.getPasswordAlertDialog().dismiss();
            }
        };
        mProgramBlockingHandler = new ProgramLockingHandler(ctx,
                mProgramBlockingInterface);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                // Show password dialog
                mProgramBlockingHandler.showPasswordDialog();
                // save audio and video track, and disable audio and video
                savedAudioTrack = -1;
                savedVideoTrack = -1;
                try {
                    // Get Audio and disable it
                    savedAudioTrack = MainActivity.service.getAudioControl()
                            .getCurrentAudioTrackIndex();
                    MainActivity.service.getAudioControl()
                            .deselectCurrentAudioTrack();
                    // Get Video and disable it
                    savedVideoTrack = MainActivity.service.getVideoControl()
                            .getCurrentVideoTrackIndex();
                    MainActivity.service.getVideoControl()
                            .deselectCurrentVideoTrack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mHandler = new Handler();
    }

    /**
     * New service check function
     * 
     * @param service
     */
    public static void checkService(Content content, boolean success) {
        if (success) {
            Log.d(TAG, "CHECK SERVICE TYPE RADIO SCRAMBLED");
            handler.sendMessage(Message.obtain(handler, CHECK_SERVICE, content));
        }
    }

    public static void serviceScrambledChanged(final boolean state) {
        final LinearLayout allLayout = (LinearLayout) MainActivity.activity
                .findViewById(R.id.linLayMessages);
        final LinearLayout scrambled = (LinearLayout) MainActivity.activity
                .findViewById(R.id.linLayScrambled);
        final LinearLayout radio = (LinearLayout) MainActivity.activity
                .findViewById(R.id.linLayRadio);
        final LinearLayout data = (LinearLayout) MainActivity.activity
                .findViewById(R.id.linLayData);
        final LinearLayout locked = (LinearLayout) MainActivity.activity
                .findViewById(R.id.linLayLocked);
        isScrambled = state;
        MainActivity.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // scrambled
                if (state) {
                    if (!allLayout.isShown()) {
                        allLayout.setVisibility(View.VISIBLE);
                    }
                    scrambled.setVisibility(View.VISIBLE);
                }
                // not scrambled
                else {
                    if (!radio.isShown() && !data.isShown()
                            && !locked.isShown()) {
                        allLayout.setVisibility(View.GONE);
                    }
                    scrambled.setVisibility(View.GONE);
                }
            }
        });
    }

    private void checkContentLock(Content content) {
        try {
            if (content.getFilterType() == FilterType.IP_STREAM) {
                isLocked = MainActivity.service.getContentListControl()
                        .getContentLockedStatus(content);
            } else {
                isLocked = MainActivity.service.getParentalControl()
                        .getChannelLock(content.getIndexInMasterList());
            }
        } catch (Exception e) {
            Log.i(TAG, "Can not get Channel Lock", e);
        }
    }

    /** Clear scrambled screen when CI CAM is inserted */
    public static void clearScrambledScreen() {
        handler.sendEmptyMessage(CLEAR_SCRAMBLED_SCREEN);
    }

    public ProgramLockingHandler getProgramBlockingHandler() {
        return mProgramBlockingHandler;
    }

    public void unlockService() {
        // Blank Screen
        try {
            MainActivity.service.getVideoControl().videoBlank(0, true);
        } catch (Exception e) {
            Log.i(TAG, "Blank Screen Exception");
            e.printStackTrace();
        }
        if (!isScrambled && !isData && !isRadio && !isIP) {
            all.setVisibility(View.GONE);
        }
        locked.setVisibility(View.GONE);
        mProgramBlockingHandler.getPasswordAlertDialog().dismiss();
        // turn on audio and video
        try {
            MainActivity.service.getAudioControl().setCurrentAudioTrack(
                    savedAudioTrack);
            MainActivity.service.getVideoControl().setCurrentVideoTrack(
                    savedVideoTrack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isLocked = false;
    }

    public LinearLayout getParental() {
        return parental;
    }

    public void showNoSignalLayout() {
        handler.sendEmptyMessage(SHOW_NO_SIGNAL);
    }

    public void hideNoSignalLayout() {
        handler.sendEmptyMessage(HIDE_NO_SIGNAL);
    }
}
