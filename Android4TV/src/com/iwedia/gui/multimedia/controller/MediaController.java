package com.iwedia.gui.multimedia.controller;

import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;
import android.widget.VideoView;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.dtv.service.SourceType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.listeners.MainKeyListener;
import com.iwedia.gui.multimedia.MultimediaGlobal;
import com.iwedia.gui.multimedia.MultimediaGridHelper;
import com.iwedia.gui.osd.CheckServiceType;

/**
 * MediaController Class is handling MediaPlayer functions
 * 
 * @author Milos Milanovic
 */
public class MediaController implements MultimediaGlobal {
    private final String TAG = "MediaController";
    private VideoView mVideoView = null;
    private int mFileCounter = -1;
    private Activity mActivity = null;
    public static boolean isStopped = false;

    public MediaController(Activity activity, VideoView videoView) {
        this.mActivity = activity;
        this.mVideoView = videoView;
    }

    /**
     * Set and Play File
     * 
     * @param filePath
     *        - File Path or URL
     * @return True if succeed or False if not
     */
    public boolean play(String filePath, int mode) {
        mVideoView = MainActivity.activity.playMultimediaVideo(filePath, mode);
        if (mVideoView != null) {
            isStopped = false;
            // Hide antenna overlay before every playback
            MultimediaGridHelper.hideAntennaOverlay();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pause
     * 
     * @return True if succeed or False if not
     */
    public boolean pause() {
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                return true;
            }
        }
        return false;
    }

    /**
     * Resume
     * 
     * @return True if succeed or False if not
     */
    public boolean resume() {
        if (mVideoView != null) {
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
                return true;
            }
        }
        return false;
    }

    /**
     * Stop Playing
     * 
     * @return True if succeed or False if not
     */
    public boolean stop(int mode) {
        if (mVideoView != null) {
            seekTo(0);
            // mVideoView.stopPlayback();
            MainActivity.activity.stopMultimediaVideo(mode);
            // Show antenna overlay if needed after playback
            MultimediaGridHelper.showAntennaOverlay();
            return true;
        }
        return false;
    }

    /**
     * FastForward Playing
     * 
     * @return True if succeed or False if not
     */
    public boolean fastforward() {
        // if (mVideoView != null) {
        // TODO Implement FF
        // }
        return false;
    }

    /**
     * Rewind Playing
     * 
     * @return True if succeed or False if not
     */
    public boolean rewind() {
        // if (mVideoView != null) {
        // TODO Implement REW
        // }
        return false;
    }

    /**
     * Seek Playing file from current position for given milliseconds
     * 
     * @param milliseconds
     *        - Milliseconds
     * @return True if succeed or False if not
     */
    public boolean seek(int milliseconds) {
        if (mVideoView != null) {
            if (mVideoView.isPlaying() && mVideoView.canSeekBackward()
                    && mVideoView.canSeekForward()) {
                mVideoView.seekTo(mVideoView.getCurrentPosition()
                        + milliseconds);
                return true;
            }
        }
        return false;
    }

    /**
     * Seek File to given milliseconds
     * 
     * @param milliseconds
     *        - Milliseconds
     * @return True if succeed or False if not
     */
    public boolean seekTo(int milliseconds) {
        if (mVideoView != null) {
            if (mVideoView.canSeekBackward() && mVideoView.canSeekForward()) {
                mVideoView.seekTo(milliseconds);
                return true;
            }
        }
        return false;
    }

    /** Start Live Stream from MW */
    public synchronized void startLiveStream(boolean cleanScreen) {
        // Com_4.0
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    /** ZORANA Quick Fix */
                    Log.d(TAG, "***********ACTIVE FILTER = "
                            + MainActivity.service.getContentListControl()
                                    .getActiveFilterIndex());
                    MainActivity.service.getContentListControl()
                            .setActiveFilter(FilterType.ALL);
                    Content activeContent = MainActivity.service
                            .getContentListControl().getActiveContent(0);
                    if ((activeContent != null)
                            && (activeContent.getFilterType() == FilterType.INPUTS)) {
                        MainActivity.service.getContentListControl().goContent(
                                activeContent, 0);
                    } else {
                        MainActivity.service.getInputOutputControl()
                                .ioDeviceStartDVB();
                        if ((activeContent != null)
                                && (activeContent.getSourceType() != SourceType.ANALOG)) {
                            /* Launch HbbTV Red Button if exists */
                            if (0 == (MainActivity.getKeySet())) {
                                int command = 0;
                                String param = "EXIT";
                                try {
                                    Log.d(TAG, "Show HbbTV graphic");
                                    MainActivity.activity.service
                                            .getHbbTvControl().notifyAppMngr(
                                                    command, param);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2500);
                                        MainActivity.service
                                                .getContentListControl()
                                                .startVideoPlayback();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        if (cleanScreen) {
            MainKeyListener.setAppState(MainKeyListener.CLEAN_SCREEN);
        }
        mActivity.runOnUiThread(new Runnable() {
            // TODO: Applies on main display only
            private final int mDisplayId = 0;

            public void run() {
                // when dlna turns off
                try {
                    Content activeContent = MainActivity.service
                            .getContentListControl().getActiveContent(
                                    mDisplayId);
                    CheckServiceType.checkService(activeContent, true);
                } catch (RemoteException e) {
                    Log.e(TAG, "Start Live PlayBack Exception", e);
                    e.printStackTrace();
                }
            }
        });
    }

    /** Stop Live Stream from MW */
    public synchronized void stopLiveStream() {
        MainActivity.activity.getPrimaryVideoView().pause();
    }

    // /////////////////////////////////////////////////////
    // Getters and Setters
    // /////////////////////////////////////////////////////
    public void setmVideoView(VideoView mVideoView) {
        this.mVideoView = mVideoView;
    }

    public boolean isPlaying() {
        if (mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }

    public int getPlayBackDuration() {
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                return mVideoView.getDuration();
            }
        }
        return 0;
    }

    public int getElapsedTime() {
        if (mVideoView != null) {
            if (mVideoView.isPlaying()) {
                return mVideoView.getCurrentPosition();
            }
        }
        return 0;
    }
}
