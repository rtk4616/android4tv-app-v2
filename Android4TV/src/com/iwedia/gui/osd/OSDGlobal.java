package com.iwedia.gui.osd;

/**
 * CurlEffect Global Fields
 * 
 * @author Milos Milanovic
 */
public interface OSDGlobal {
    /** Colors */
    public static final int COLOR_FOREGROUND_TEXTURE_GRADIENT_I = 0xFF4B4B4B;
    public static final int COLOR_FOREGROUND_TEXTURE_GRADIENT_II = 0xFFE1E1E1;
    public static final int COLOR_BACKGROUND_TEXTURE_GRADIENT_I = 0xFF2A2A2A;
    public static final int COLOR_BACKGROUND_TEXTURE_GRADIENT_II = 0xFF6C6C6C;
    /** Channel Change */
    public static final int CHANNEL_SYNC = -1;
    public static final int CHANNEL_UP = 0;
    public static final int CHANNEL_DOWN = 1;
    public static final int CHANNEL_GO_TO_INDEX = 2;
    public static final int CHANNEL_TOGGLE_PREVIOUS = 3;
    public static final int CHANNEL_CONTENT = 4;
    /** Animation time */
    public static final int ANIMATION_TIME_CHANNEL_INFO = 5000;
    public static final int ANIMATION_TIME_CHANNEL_CHANGE = 10000;
    public static final int ANIMATION_TIME_CHANNEL_CHANGE_FCZ = 1000;
    public static final int ANIMATION_TIME_CHANNEL_CLOSE = 2;
    public static final int ANIMATION_TIME_INFO = 15000;
    public static final int ANIMATION_TIME_INFO_DIALOG_OPEN_I = 20;
    public static final int ANIMATION_TIME_INFO_DIALOG_OPEN_II = 0;
    public static final int ANIMATION_TIME_INFO_DIALOG_OPEN_III = 0;
    public static final int ANIMATION_TIME_INFO_DIALOG_CLOSE = 700;
    public static final int ANIMATION_TIME_NUMEROUS_CHANGE_CHANNEL = 3000;
    public static final int ANIMATION_TIME_VOLUME = 3000;
    public static final int ANIMATION_TIME_INIT = 0;
    public static final int ANIMATION_TIME_DE_INIT = 1000;
    public static final int ANIMATION_TIME_PVR = 8000;
    public static final int ANIMATION_TIME_MULTIMEDIA_CONTROLLER = 8000;
    public static final int ANIMATION_TIME_PICTURE_FORMAT = 3000;
    /** TimerTask Time */
    public static final int TIMERTASK_TIME_MULTIMEDIA_CONTROLLER = 500;
    public static final int TIMERTASK_TIME_MULTIMEDIA_CONTROLLER_PVR = 500;
    public static final int TIMERTASK_TIME_CHANNEL_INFO = 3000;
    public static final int TIMERTASK_TIME_PVR = 500;
    /** TimerTask What */
    public static final int TIMERTASK_WHAT_MULTIMEDIA_CONTROLLER = 0;
    public static final int TIMERTASK_WHAT_MULTIMEDIA_CONTROLLER_PVR = 1;
    public static final int TIMERTASK_WHAT_CHANNEL_INFO = 2;
    public static final int TIMERTASK_WHAT_PVR = 3;
    /** StateMachine States */
    public static final int STATE_DO_NOTHING = 0;
    public static final int STATE_INIT = 1;
    public static final int STATE_CHANNEL_INFO = 2;
    public static final int STATE_INFO = 3;
    public static final int STATE_VOLUME = 4;
    public static final int STATE_NUMEROUS_CHANGE_CHANNEL = 5;
    public static final int STATE_CHANGE_CHANNEL = 6;
    public static final int STATE_PVR = 7;
    public static final int STATE_MULTIMEDIA_CONTROLLER = 8;
    public static final int STATE_DEINIT = 9;
    public static final int STATE_OFF = 10;
    /** My new state */
    public static final int STATE_PVR_TIMESHIFT_RECORD = 11;
    public static final int STATE_PLAY_PVR_PLAYBACK = 12;
    public static final int STATE_STOP_PVR_PLAYBACK = 13;
    public static final int STATE_UPDATE_MULTIMEDIA_PLAYBACK_TIME = 14;
    public static final int STATE_PLAY_TIMESHIFT = 15;
    public static final int STATE_INPUT_INFO = 16;
    public static final int STATE_PICTURE_FORMAT = 17;
    /**************************************************/
    /** Scenario **************************************/
    /**
     * S - Initialize, CI - ChannelInfo, I - Info, CC - ChannelChange, NCC -
     * Numerous Channel Change, V - Volume, P - PVR, M - MultiMedia, E -
     * DeInitialize, II - Input Info, PF - Picture format
     */
    /** Scenario Do Nothing: E */
    public static final int SCENARIO_DO_NOTHING = 0;
    /** Scenario ChannelInfo: S - CI - E */
    public static final int SCENARIO_CHANNEL_INFO = 1;
    /** Scenario Change Channel: S - CC - CI - E */
    public static final int SCENARIO_CHANNEL_CHANGE = 2;
    /** Scenario Circular Change Channel: S - CCC - CI - E */
    public static final int SCENARIO_TOGGLE_PREVIOUS_CHANNEL_CHANGE = 3;
    /** Scenario Info: S - I - E */
    public static final int SCENARIO_INFO = 4;
    /** Scenario Numerous Change Channel: S - NCC - CC - CI - E */
    public static final int SCENARIO_NUMEROUS_CHANNEL_CHANGE = 5;
    /** Scenario Volume: S - V - E */
    public static final int SCENARIO_VOLUME = 6;
    /** Scenario PVR: S - P - E */
    public static final int SCENARIO_PVR_RECORD = 7;
    /** Scenario MultimediaController: S - M - E */
    public static final int SCENARIO_MULTIMEDIA_CONTROLLER = 8;
    /** Scenario MultimediaController: S - CC - E */
    public static final int SCENARIO_CHANNEL_CHANGE_BY_CONTENT = 9;
    /** Scenario Input Controller: S - II - E */
    public static final int SCENARIO_INPUT_INFO = 10;
    /** Scenario Picture Format: S - PP - E */
    public static final int SCENARIO_PICTURE_FORMAT = 11;
    /**************************************************/
    /** Messages for handle */
    public static final int HANDLE_MESSAGE_RUN = 0;
    public static final int HANDLE_MESSAGE_FORCE_START = 1;
    /** Max length of channel number */
    public static final int MAX_CHANNEL_NUMBER_LENGTH = 4;
    /** Max length of major channel number */
    public static final int MAX_MAJOR_CHANNEL_NUMBER_LENGTH = 3;
    /** Max length of minor channel number */
    public static final int MAX_MINOR_CHANNEL_NUMBER_LENGTH = 3;
    /** Max length of minor channel number */
    public static final int MAJOR_MINOR_CONVERT_NUMBER = 1024;
    /** Max length of secret key */
    public static final int MAX_SECRET_KEY_LENGTH = 8;
    /** Volume */
    public static final int VOLUME_UP = 0;
    public static final int VOLUME_DOWN = 1;
    public static final int VOLUME_MUTE = 2;
    public static final int VOLUME_MODE_PER_CHANNEL = 0;
    public static final int VOLUME_MODE_ALL_CHANNEL = 1;
    /** CurlHandler States */
    /** PVR States */
    /** MultiMedia Controller State */
    public static final int CURL_HANDLER_STATE_DO_NOTHING = 0;
    public static final int PVR_STATE_RECORDING = 1;
    public static final int PVR_STATE_STOP_PLAY_BACK = 2;
    public static final int PVR_STATE_PLAY_PLAY_BACK = 3;
    public static final int PVR_STATE_PAUSE_PLAY_BACK = 4;
    public static final int PVR_STATE_FF_PLAY_BACK = 5;
    public static final int PVR_STATE_REW_PLAY_BACK = 6;
    public static final int PVR_STATE_STOP_TIME_SHIFT = 7;
    public static final int PVR_STATE_PLAY_TIME_SHIFT = 8;
    public static final int PVR_STATE_PAUSE_TIME_SHIFT = 9;
    public static final int PVR_STATE_FF_TIME_SHIFT = 10;
    public static final int PVR_STATE_REW_TIME_SHIFT = 11;
    public static final int CURL_HANDLER_STATE_MULTIMEDIA_CONTROLLER = 12;
    public static final int STATE_INFO_BANNER_HIDDEN = 13;
    public static final int STATE_INFO_BANNER_SHOWN = 14;
    /** MultiMedia Controller */
    public static final int MULTIMEDIA_CONTROLLER_STOP = 0;
    public static final int MULTIMEDIA_CONTROLLER_REW_PREVIOUS = 1;
    public static final int MULTIMEDIA_CONTROLLER_PLAY = 2;
    public static final int MULTIMEDIA_CONTROLLER_FF_NEXT = 3;
    public static final int MULTIMEDIA_CONTROLLER_RE = 4;
    public static final int MULTIMEDIA_CONTROLLER_REPEAT_OFF = 0;
    public static final int MULTIMEDIA_CONTROLLER_REPEAT_ONE = 1;
    public static final int MULTIMEDIA_CONTROLLER_REPEAT_ALL = 2;
}
