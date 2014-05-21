package com.iwedia.gui.osd;

public class OSDHandlerHelper {
    private static IOSDHandler mOSDHandler = null;

    public static void setGlobalHandler(IOSDHandler handler) {
        mOSDHandler = handler;
    }

    public static void updateTimeChannelInfo() {
        if (mOSDHandler != null) {
            mOSDHandler.updateTimeChannelInfo();
        }
    }

    /** CallBack Method when Channel is Zapped */
    public static void channelIsZapped(boolean success) {
        if (mOSDHandler != null) {
            mOSDHandler.channelIsZapped(success);
        }
    }

    /** Getters and Setters */
    public static void setHandlerState(int state) {
        if (mOSDHandler != null) {
            mOSDHandler.setHandlerState(state);
        }
    }

    public static int getHandlerState() {
        if (mOSDHandler != null) {
            return mOSDHandler.getHandlerState();
        }
        return 0;
    }

    public static boolean isServiceListEmpty() {
        if (mOSDHandler != null) {
            return mOSDHandler.isServiceListEmpty();
        }
        return false;
    }
}
