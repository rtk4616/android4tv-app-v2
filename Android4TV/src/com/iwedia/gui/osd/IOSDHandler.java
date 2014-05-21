package com.iwedia.gui.osd;

import android.view.View;

import com.iwedia.comm.content.Content;
import com.iwedia.comm.content.multimedia.MultimediaContent;
import com.iwedia.gui.multimedia.pvr.player.controller.PVRPlayerController;

public interface IOSDHandler {
    public void init(View view);

    public void prepareChannelAndChange(int scenario, int channelState);

    public void changeChannelByContent(Content content, int displayId);

    public void changeChannelByNum(int channelNum, int displayId);

    public void volume(int volState, boolean startCurl);

    public void info();

    public boolean multimediaController(boolean openFirst);

    public boolean multimediaControllerPVR(boolean openFirst);

    public void prepareAndStartMultiMediaPlayBackDLNALocal(
            MultimediaContent content, boolean isMusicInfo);

    public void prepareAndStartMultiMediaPlayBackPVR(MultimediaContent content,
            int displayId);

    public void multimediaControllerMoveLeft();

    public void multimediaControllerClick(boolean immediatelyClick);

    public void multimediaControllerMoveRight();

    public void updateTimeChannelInfo();

    public boolean isServiceListEmpty();

    public void channelIsZapped(boolean success);

    public void setHandlerState(int state);

    public int getHandlerState();

    public ChannelChangeHandler getChannelChangeHandler();

    public Content getContentExtendedInfo();

    public PVRPlayerController getPvrPlayerController();

    public void initControlProviderDLNALocal();

    public void initControlProviderPVR();

    public void setAnimationTimeChannelInfo(int i);

    public int getCurrentState();

    public void startCurlEffect(int scenarioDoNothing);

    public boolean isFlagChannelInfo();

    public void setUpNewChannelInfo(int index);

    public void updateChannelInfo(int channelIndex);

    public void getPreviousChannelInfo();

    public void getNextChannelInfo();

    public int getChannelInfoIndex();

    public void drawInputInfo();

    public void drawInfoBanner(int state);

    public void updatePlayingTime(int mStartTime, int mEndTime,
            int mPlayingTime, int progressValue);

    public void updateTimeShiftPlayingTime(int mPlayingTime, int progressValue);

    public void scroll(int direction);

    public void getExtendedInfo();

    public void showPictureFormat(String format);
}
