package com.iwedia.service.pvr;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IPvrCallback;
import com.iwedia.comm.IPvrControl;
import com.iwedia.comm.enums.PvrSpeedMode;
import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.dtv.pvr.OnTouchInfo;
import com.iwedia.dtv.pvr.PlaybackInfo;
import com.iwedia.dtv.pvr.PvrEventMediaAdd;
import com.iwedia.dtv.pvr.PvrEventMediaRemove;
import com.iwedia.dtv.pvr.PvrEventPlaybackJump;
import com.iwedia.dtv.pvr.PvrEventPlaybackPosition;
import com.iwedia.dtv.pvr.PvrEventPlaybackSpeed;
import com.iwedia.dtv.pvr.PvrEventPlaybackStart;
import com.iwedia.dtv.pvr.PvrEventPlaybackStop;
import com.iwedia.dtv.pvr.PvrEventRecordAdd;
import com.iwedia.dtv.pvr.PvrEventRecordConflict;
import com.iwedia.dtv.pvr.PvrEventRecordPosition;
import com.iwedia.dtv.pvr.PvrEventRecordRemove;
import com.iwedia.dtv.pvr.PvrEventRecordResourceIssue;
import com.iwedia.dtv.pvr.PvrEventRecordStart;
import com.iwedia.dtv.pvr.PvrEventRecordStop;
import com.iwedia.dtv.pvr.PvrEventTimeshiftJump;
import com.iwedia.dtv.pvr.PvrEventTimeshiftPosition;
import com.iwedia.dtv.pvr.PvrEventTimeshiftSpeed;
import com.iwedia.dtv.pvr.PvrEventTimeshiftStart;
import com.iwedia.dtv.pvr.PvrEventTimeshiftStop;
import com.iwedia.dtv.pvr.PvrRecordType;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.pvr.SmartCreateParams;
import com.iwedia.dtv.pvr.SmartInfo;
import com.iwedia.dtv.pvr.TimerCreateParams;
import com.iwedia.dtv.pvr.TimerInfo;
import com.iwedia.dtv.pvr.TimeshiftInfo;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;

public class PvrControl extends IPvrControl.Stub implements IDTVInterface {
    public static final String LOG_TAG = "PvrControl";
    private static IPvrCallback pvrCallback;
    private static int timeshiftRecordSpace = 0;
    private static int timeshiftRecordTime = 0;
    private static int timeshiftEndTime = 0;
    private static int recordSpace = 0;
    private static int recordTime = 0;
    private static int endTime = 0;
    private static int speed = PvrSpeedMode.PVR_SPEED_PAUSE;;

    @Override
    public void setDevicePath(String path) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setDevicePath(path);
    }

    @Override
    public String getDevicePath() {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getDevicePath();
    }

    @Override
    public void setDeviceSpeed(int speed) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setDeviceSpeed(speed);
    }

    @Override
    public void createOnTouchRecord(int serviceID) {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .createOnTouchRecord(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentRecRoute(), serviceID);
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void createTimerRecord(TimerCreateParams timerCreateParams) {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .createTimerRecord(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentRecRoute(), timerCreateParams);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void createSmartRecord(SmartCreateParams smartCreateParams) {
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .createSmartRecord(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentRecRoute(), smartCreateParams);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void destroyRecord(int index) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .destroyRecord(index);
    }

    @Override
    public int updateRecordList() {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .updateRecordList();
    }

    @Override
    public void setRecordListSortMode(PvrSortMode mode) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setRecordListSortMode(mode);
    }

    @Override
    public PvrSortMode getRecordListSortMode() {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getRecordListSortMode();
    }

    @Override
    public void setRecordListSortOrder(PvrSortOrder order) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setRecordListSortOrder(order);
    }

    @Override
    public PvrSortOrder getRecordListSortOrder() {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getRecordListSortOrder();
    }

    @Override
    public PvrRecordType getRecordType(int index) {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getRecordType(index);
    }

    @Override
    public OnTouchInfo getOnTouchInfo(int index) {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getOnTouchInfo(index);
    }

    @Override
    public TimerInfo getTimerInfo(int index) {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getTimerInfo(index);
    }

    @Override
    public SmartInfo getSmartInfo(int index) {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getSmartInfo(index);
    }

    @Override
    public void setRecordMaxDuration(int duration) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setRecordMaxDuration(duration);
    }

    @Override
    public int getRecordMaxDuration() {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getRecordMaxDuration();
    }

    @Override
    public int updateMediaList() {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .updateMediaList();
    }

    @Override
    public void setMediaListSortMode(PvrSortMode mode) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setMediaListSortMode(mode);
    }

    @Override
    public PvrSortMode getMediaListSortMode() {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getMediaListSortMode();
    }

    @Override
    public void setMediaListSortOrder(PvrSortOrder order) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setMediaListSortOrder(order);
    }

    @Override
    public PvrSortOrder getMediaListSortOrder() {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getMediaListSortOrder();
    }

    @Override
    public MediaInfo getMediaInfo(int index) {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getMediaInfo(index);
    }

    @Override
    public void deleteMedia(int index) {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .deleteMedia(index);
    }

    @Override
    public void deleteMediaList() {
        while (IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .updateMediaList() > 0) {
            IWEDIAService.getInstance().getDTVManager().getPvrControl()
                    .deleteMedia(0);
        }
    }

    @Override
    public void startPlayback(int displayId, int index) {
        speed = PvrSpeedMode.PVR_SPEED_FORWARD_X1;
        int decoderID = IWEDIAService.getInstance().getDtvManagerProxy()
                .getDecoderID(displayId);
        int routeID = IWEDIAService.getInstance().getDtvManagerProxy()
                .getPlayRouteID(decoderID);
        try {
            IWEDIAService.getInstance().getDTVManager().getPvrControl()
                    .startPlayback(routeID, index);
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void stopPlayback() {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .stopPlayback(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getPlayRouteID());
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public PlaybackInfo getPlaybackInfo() {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPvrControl()
                .getPlaybackInfo(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getPlayRouteID());
    }

    @Override
    public void setTimeshiftBufferSize(int size) {
        IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .setTimeshiftBufferSize(size);
    }

    @Override
    public int getTimeshiftBufferSize() {
        return IWEDIAService.getInstance().getDTVManager().getPvrControl()
                .getTimeshiftBufferSize();
    }

    @Override
    public void startTimeshift() {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .startTimeshift(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getPlayRouteID());
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void stopTimeshift(boolean resume) {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        try {
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .stopTimeshift(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getPlayRouteID(), resume);
        } catch (InternalException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public TimeshiftInfo getTimeshiftInfo() {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPvrControl()
                .getTimeshiftInfo(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getPlayRouteID());
    }

    @Override
    public void controlSpeed(int speed) {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPvrControl()
                .controlSpeed(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getPlayRouteID(), speed);
    }

    @Override
    public void jump(int position, boolean relative) {
        speed = PvrSpeedMode.PVR_SPEED_PAUSE;;
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPvrControl()
                .jump(IWEDIAService.getInstance().getDtvManagerProxy()
                        .getPlayRouteID(), position, relative);
    }

    @Override
    public void pause(boolean pauseResume) {
        if (IWEDIAService.DEBUG)
            Log.i(LOG_TAG, "pause: " + pauseResume);
        if (pauseResume) {
            speed = PvrSpeedMode.PVR_SPEED_PAUSE;
        } else {
            speed = PvrSpeedMode.PVR_SPEED_FORWARD_X1;
        }
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPvrControl()
                .controlSpeed(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getPlayRouteID(), speed);
    }

    @Override
    public void fastForward() {
        if (IWEDIAService.DEBUG)
            Log.i(LOG_TAG, "fastForward enter - SPEED: " + speed);
        if (speed > PvrSpeedMode.PVR_SPEED_BACKWARD_X64
                && speed < PvrSpeedMode.PVR_SPEED_FORWARD_X64) {
            if (speed > PvrSpeedMode.PVR_SPEED_PAUSE) {
                speed *= 2;
            } else if (speed < PvrSpeedMode.PVR_SPEED_PAUSE) {
                if (speed == PvrSpeedMode.PVR_SPEED_BACKWARD_X1) {
                    speed = PvrSpeedMode.PVR_SPEED_FORWARD_X1;
                } else {
                    speed /= 2;
                }
            } else {
                speed = PvrSpeedMode.PVR_SPEED_FORWARD_X2;
            }
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .controlSpeed(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getPlayRouteID(), speed);
        }
        if (IWEDIAService.DEBUG)
            Log.i(LOG_TAG, "fastForward - SPEED: " + speed);
    }

    @Override
    public void rewind() {
        if (IWEDIAService.DEBUG)
            Log.i(LOG_TAG, "rewind enter - SPEED: " + speed);
        if (speed > PvrSpeedMode.PVR_SPEED_BACKWARD_X64
                && speed < PvrSpeedMode.PVR_SPEED_FORWARD_X64) {
            if (speed == PvrSpeedMode.PVR_SPEED_PAUSE
                    || speed == PvrSpeedMode.PVR_SPEED_FORWARD_X1) {
                speed = PvrSpeedMode.PVR_SPEED_BACKWARD_X1;
            } else if (speed < PvrSpeedMode.PVR_SPEED_PAUSE) {
                speed *= 2;
            } else if (speed > PvrSpeedMode.PVR_SPEED_PAUSE) {
                speed /= 2;
            }
            IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getPvrControl()
                    .controlSpeed(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getPlayRouteID(), speed);
        }
        if (IWEDIAService.DEBUG)
            Log.i(LOG_TAG, "rewind - SPEED: " + speed);
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }

    @SuppressWarnings("static-access")
    @Override
    public void registerCallback(IPvrCallback pvrCallback) {
        this.pvrCallback = pvrCallback;
    }

    @Override
    public void unregisterCallback(IPvrCallback arg0) {
        // TODO Auto-generated method stub
    }

    public static com.iwedia.dtv.pvr.IPvrCallback getPVRCallback() {
        return callback;
        // return null;
    }

    private static com.iwedia.dtv.pvr.IPvrCallback callback = new com.iwedia.dtv.pvr.IPvrCallback() {
        @Override
        public void eventTimeshiftStop(
                PvrEventTimeshiftStop pvrEventTimeshiftStop) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventTimeshiftStop");
                }
                pvrCallback.timeshiftStop();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventTimeshiftStart(
                PvrEventTimeshiftStart pvrEventTimeshiftStart) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventTimeshiftStart");
                }
                pvrCallback.timeshiftStart();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventTimeshiftSpeed(
                PvrEventTimeshiftSpeed pvrEventTimeshiftSpeed) {
            speed = pvrEventTimeshiftSpeed.getSpeed();
            int timeshiftSpeed = speed / 100;
            if (IWEDIAService.DEBUG) {
                Log.d(LOG_TAG, "eventTimeshiftSpeed - timeshiftSpeed: "
                        + timeshiftSpeed);
            }
            if (speed > PvrSpeedMode.PVR_SPEED_PAUSE) {
                try {
                    if (speed == PvrSpeedMode.PVR_SPEED_FORWARD_X1) {
                        pvrCallback.timeshiftPlay();
                    } else
                        pvrCallback.timeshiftFastForward(timeshiftSpeed);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (speed < PvrSpeedMode.PVR_SPEED_PAUSE) {
                try {
                    pvrCallback.timeshiftRewind(-timeshiftSpeed);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    pvrCallback.timeshiftPause();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void eventTimeshiftPosition(
                PvrEventTimeshiftPosition pvrEventTimeshiftPosition) {
            if (IWEDIAService.DEBUG) {
                Log.d(LOG_TAG, "eventTimeshiftPosition");
            }
            try {
                if (pvrEventTimeshiftPosition.isPlaybackBegin()
                        || pvrEventTimeshiftPosition.isPlaybackEnd()) {
                    if (IWEDIAService.DEBUG) {
                        Log.d(LOG_TAG, "timeshiftPlay() - isPlaybackBegin(): "
                                + pvrEventTimeshiftPosition.isPlaybackBegin());
                        Log.d(LOG_TAG, "timeshiftPlay() - isPlaybackEnd(): "
                                + pvrEventTimeshiftPosition.isPlaybackEnd());
                    }
                    speed = PvrSpeedMode.PVR_SPEED_FORWARD_X1;
                    IWEDIAService
                            .getInstance()
                            .getDTVManager()
                            .getPvrControl()
                            .controlSpeed(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getPlayRouteID(), speed);
                } else {
                    /*
                     * int timeshiftBufferSize = IWEDIAService.getInstance()
                     * .getDTVManager().getPvrControl()
                     * .getTimeshiftBufferSize();
                     */
                    int timeshiftBufferSize = 512; // temporary value - not
                                                   // implemented in MW
                    timeshiftEndTime = 1800000; // temporary value
                    int recordTimePosition = pvrEventTimeshiftPosition
                            .getRecordTimePosition();
                    int recordSpacePosition = pvrEventTimeshiftPosition
                            .getRecordSpacePosition();
                    int palybackTimePosition = pvrEventTimeshiftPosition
                            .getPlaybackTimePosition();
                    int palybackSpacePosition = pvrEventTimeshiftPosition
                            .getPlaybackSpacePosition();
                    // Calculate BitRate
                    /*
                     * if (timeshiftRecordSpace != recordSpacePosition) {
                     * timeshiftEndTime = timeshiftBufferSize
                     * (recordTimePosition - timeshiftRecordTime);
                     * timeshiftRecordTime = recordTimePosition;
                     * timeshiftRecordSpace = recordSpacePosition; }
                     */
                    if (IWEDIAService.DEBUG) {
                        Log.d(LOG_TAG,
                                "eventTimeshiftPosition - playbackTimePosition: "
                                        + palybackTimePosition);
                        Log.d(LOG_TAG,
                                "eventTimeshiftPosition - recordTimePosition "
                                        + recordTimePosition);
                    }
                    pvrCallback.timeshiftPosition(recordTimePosition,
                            palybackTimePosition, timeshiftEndTime);
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventTimeshiftJump(
                PvrEventTimeshiftJump pvrEventTimeshiftJump) {
            // TODO Auto-generated method stub
        }

        @Override
        public void eventRecordStop(PvrEventRecordStop pvrEventRecordStop) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventRecordStop");
                }
                pvrCallback.recordStop();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventRecordStart(PvrEventRecordStart pvrEventRecordStart) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventRecordStart");
                }
                pvrCallback.recordStart();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventRecordResourceIssue(
                PvrEventRecordResourceIssue pvrEventRecordResourceIssue) {
            if (IWEDIAService.DEBUG) {
                Log.d(LOG_TAG, "eventRecordResourceIssue");
            }
            // TODO Auto-generated method stub
        }

        @Override
        public void eventRecordRemove(PvrEventRecordRemove pvrEventRecordRemove) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventRecordRemove");
                }
                pvrCallback.recordRemove();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventRecordPosition(
                PvrEventRecordPosition pvrEventRecordPosition) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventRecordPosition");
                }
                int bufferSize = 512; // temporary value
                endTime = 3600000; // temporary value
                int recordTimePosition = pvrEventRecordPosition
                        .getTimePosition();
                int recordSpacePosition = pvrEventRecordPosition
                        .getSpacePosition();
                // Calculate BitRate
                /*
                 * if (recordSpace != recordSpacePosition) { endTime =
                 * bufferSize * (recordTimePosition - recordTime); recordTime =
                 * recordTimePosition; recordSpace = recordSpacePosition; }
                 */
                Log.d(LOG_TAG, "eventRecordPosition - bufferSize: "
                        + bufferSize);
                Log.d(LOG_TAG, "eventRecordPosition - recordTimePosition: "
                        + recordTimePosition);
                Log.d(LOG_TAG, "eventRecordPosition - recordSpacePosition: "
                        + recordSpacePosition);
                pvrCallback.recordPosition(recordTimePosition, endTime);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventRecordConflict(
                PvrEventRecordConflict pvrEventRecordConflict) {
            if (IWEDIAService.DEBUG) {
                Log.d(LOG_TAG, "eventRecordConflict");
            }
        }

        @Override
        public void eventRecordAdd(PvrEventRecordAdd pvrEventRecordAdd) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventRecordAdd");
                }
                pvrCallback.recordAdd();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventPlaybackStop(PvrEventPlaybackStop pvrEventPlaybackStop) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventPlaybackStop");
                }
                pvrCallback.playbackStop();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventPlaybackStart(
                PvrEventPlaybackStart pvrEventPlaybackStart) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventPlaybackStart");
                }
                pvrCallback.playbackPlay();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventPlaybackSpeed(
                PvrEventPlaybackSpeed pvrEventPlaybackSpeed) {
            speed = pvrEventPlaybackSpeed.getSpeed();
            int playbackSpeed = speed / 100;
            if (IWEDIAService.DEBUG) {
                Log.d(LOG_TAG, "eventPlaybackSpeed - playbackSpeed: "
                        + playbackSpeed);
            }
            if (speed < PvrSpeedMode.PVR_SPEED_PAUSE) {
                try {
                    pvrCallback.playbackRewind(-playbackSpeed);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (playbackSpeed > PvrSpeedMode.PVR_SPEED_PAUSE) {
                try {
                    pvrCallback.playbackFastForward(playbackSpeed);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    pvrCallback.playbackPause();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void eventPlaybackPosition(
                PvrEventPlaybackPosition pvrEventPlaybackPosition) {
            try {
                if (IWEDIAService.DEBUG) {
                    Log.d(LOG_TAG, "eventPlaybackPosition - timePosition"
                            + pvrEventPlaybackPosition.getTimePosition());
                }
                if (pvrEventPlaybackPosition.isBegin()) {
                    if (IWEDIAService.DEBUG) {
                        Log.d(LOG_TAG, "eventPlaybackPosition - isBegin(): "
                                + pvrEventPlaybackPosition.isBegin());
                    }
                    speed = PvrSpeedMode.PVR_SPEED_FORWARD_X1;
                    IWEDIAService
                            .getInstance()
                            .getDTVManager()
                            .getPvrControl()
                            .controlSpeed(
                                    IWEDIAService.getInstance()
                                            .getDtvManagerProxy()
                                            .getPlayRouteID(), speed);
                    // pvrCallback.playbackPlay();
                } else if (pvrEventPlaybackPosition.isEnd()) {
                    if (IWEDIAService.DEBUG) {
                        Log.d(LOG_TAG, "eventPlaybackPosition - isEnd(): "
                                + pvrEventPlaybackPosition.isEnd());
                    }
                    speed = PvrSpeedMode.PVR_SPEED_PAUSE;
                    try {
                        IWEDIAService
                                .getInstance()
                                .getDTVManager()
                                .getPvrControl()
                                .stopPlayback(
                                        IWEDIAService.getInstance()
                                                .getDtvManagerProxy()
                                                .getPlayRouteID());
                    } catch (InternalException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // pvrCallback.playbackStop();
                } else {
                    pvrCallback.playbackPosition(pvrEventPlaybackPosition
                            .getTimePosition());
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void eventPlaybackJump(PvrEventPlaybackJump pvrEventPlaybackJump) {
            // TODO Auto-generated method stub
        }

        @Override
        public void eventMediaRemove(PvrEventMediaRemove pvrEventMediaRemove) {
            // TODO Auto-generated method stub
        }

        @Override
        public void eventMediaAdd(PvrEventMediaAdd pvrEventMediaAdd) {
            // TODO Auto-generated method stub
        }

        @Override
        public void eventDeviceError() {
            // TODO Auto-generated method stub
        }
    };
}
