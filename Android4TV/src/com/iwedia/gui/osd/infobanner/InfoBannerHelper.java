package com.iwedia.gui.osd.infobanner;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.iwedia.comm.IAudioControl;
import com.iwedia.comm.ISubtitleControl;
import com.iwedia.comm.ITeletextControl;
import com.iwedia.comm.content.Content;
import com.iwedia.comm.enums.FilterType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVInfoDescriptionScrollView;
import com.iwedia.gui.components.A4TVMultimediaController;
import com.iwedia.gui.components.A4TVProgressBarPVR;
import com.iwedia.gui.components.A4TVProgressInfoBanner;
import com.iwedia.gui.components.A4TVProgressVolumeBanner;
import com.iwedia.gui.components.A4TVTextView;
import com.iwedia.gui.config_handler.ConfigHandler;
import com.iwedia.gui.osd.OSDGlobal;

import java.util.ArrayList;

public class InfoBannerHelper implements OSDGlobal {
    private MainActivity mActivity = null;
    /** Is animation in progress or not */
    public static boolean animationFlag = false;
    /** Linear Layout for volume_layout.xml */
    protected LinearLayout linearLayoutVolume = null;
    /** Linear Layout for pvr, timeshift and playback */
    protected LinearLayout linearLayoutPlayerInfo = null;
    /** Linear Layout for elapsed time in play timeshift */
    protected LinearLayout linearLayoutPlayerElapsedTime = null;
    /** Linear Layout for input */
    protected LinearLayout linearLayoutInput = null;
    /** Title: Recording or Elapsed */
    protected A4TVTextView textViewPlayerTitle = null;
    /** Name of pvr, timeshift file */
    protected A4TVTextView textViewFileName = null;
    protected A4TVTextView textViewFileDescription = null;
    protected A4TVTextView textViewNameOfAlbum = null;
    protected ImageView imageViewMediaIcon = null;
    protected LinearLayout mLinearLayoutPictureFormatInfo = null;
    protected A4TVTextView mTextViewPictureFormatInfo = null;
    // /////////////////////////////////////////////////
    // PVR information
    protected A4TVTextView textViewStartTime = null;;
    protected A4TVTextView textViewEndTime = null;
    protected A4TVTextView textViewRecordTime = null;
    protected A4TVTextView textViewElapsedTime = null;
    protected A4TVTextView textViewPlayerState = null;
    protected A4TVProgressInfoBanner playerProgressBarTime = null;
    // ////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////
    // PVR, timeshift icon(play, stop,...)
    protected ImageView imageViewStopPlayerControl = null;;
    protected ImageView imageViewRewPlayerControl = null;
    protected ImageView imageViewPlayPlayerControl = null;
    protected ImageView imageViewFFPlayerControl = null;
    protected ImageView imageViewRecPlayerControl = null;
    // ///////////////////////////////////////////////////////////////
    // Volume information
    protected A4TVProgressVolumeBanner volumeProgressBar = null;
    protected A4TVTextView textViewVolumeValue = null;
    protected ImageView imageViewVolumeState = null;
    // ////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////
    // Input information
    protected A4TVTextView textViewFrameRate = null;
    protected A4TVTextView textViewResolution = null;
    protected A4TVTextView textViewInputName = null;
    // ///////////////////////////////////////////////////////////////
    protected ArrayList<String> mStrValues = null;
    /** Is service: TV, Radio or Data */
    protected int tvStatus = -1;
    protected int mChannelProgressValue = 0;
    protected int currentState = STATE_INIT;
    protected int previousState = STATE_INIT;
    protected ObjectAnimator objectAnimator = null;
    /** Detect is there is long description in info */
    public static boolean description = false;
    // ////////////////////////////////////////////////////
    // Info banner
    // ///////////////////////////////////////////////////
    protected LinearLayout mViewFlipper = null;
    protected LinearLayout infobanerBackground = null;
    protected LinearLayout linearLayoutLeftSeparator = null;
    protected LinearLayout linearLayoutRightSeparator = null;
    protected LinearLayout linearLayoutEpg = null;
    protected LinearLayout linearLayoutDate = null;
    protected LinearLayout linearLayoutProgressBar = null;
    protected A4TVInfoDescriptionScrollView scrollView = null;
    protected ImageView channelIcon = null;
    /** Is service: radio, data or tv service */
    protected A4TVTextView textViewTVStatus = null;
    protected A4TVTextView channelName = null;
    protected A4TVTextView progressBarStart = null;
    protected A4TVTextView progressBarDuration = null;
    protected A4TVTextView currentShow = null;
    protected A4TVTextView shortDescription = null;
    protected A4TVTextView longDescription = null;
    protected A4TVTextView timeFromStream = null;
    protected A4TVTextView dateFromStream = null;
    protected ImageView imageViewTeletext = null;
    protected ImageView imageViewHD = null;
    protected ImageView imageViewSubtitle = null;
    protected ImageView imageViewAudio = null;
    protected ImageView imageViewHbbTv = null;
    protected ProgressBar progressBar = null;
    protected A4TVTextView playerChannelName = null;
    protected ImageView infobanerArrowRight = null;
    protected ImageView infobanerArrowLeft = null;
    private ISubtitleControl subControl = null;
    private ITeletextControl ttxControl = null;
    private IAudioControl audControl = null;
    protected boolean nowEvent = false;
    // ///////////////////////////////////////
    // New player channel info
    // ////////////////////////////////////////
    LinearLayout playerlinearLayoutDate = null;
    ProgressBar playerProgressBar = null;
    LinearLayout playerlinearLayoutProgressBar = null;
    A4TVTextView playerProgressBarStart = null;
    A4TVTextView playerProgressBarDuration = null;
    A4TVTextView playerTimeFromStream = null;
    A4TVTextView playerDateFromStream = null;
    ImageView playerImageViewSubtitle = null;
    ImageView playerImageViewTeletext = null;
    ImageView playerImageViewAudio = null;
    ImageView playerImageViewHbbTv = null;
    ImageView playerImageViewHD = null;

    public InfoBannerHelper(MainActivity activity) {
        this.mActivity = activity;
        initViews();
    }

    protected void setChannelNumericInfoInformation() {
        linearLayoutInput.setVisibility(View.GONE);
        linearLayoutPlayerInfo.setVisibility(View.GONE);
        linearLayoutVolume.setVisibility(View.GONE);
        linearLayoutEpg.setVisibility(View.GONE);
        linearLayoutLeftSeparator.setVisibility(View.GONE);
        linearLayoutRightSeparator.setVisibility(View.GONE);
        linearLayoutDate.setVisibility(View.GONE);
        channelIcon.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linearLayoutProgressBar.setVisibility(View.GONE);
        mLinearLayoutPictureFormatInfo.setVisibility(View.GONE);
        channelName.setText(mStrValues.get(0));
        mStrValues = null;
    }

    protected void setChannelUpDownInfoInformation() {
        linearLayoutInput.setVisibility(View.GONE);
        linearLayoutPlayerInfo.setVisibility(View.GONE);
        linearLayoutVolume.setVisibility(View.GONE);
        linearLayoutEpg.setVisibility(View.GONE);
        linearLayoutLeftSeparator.setVisibility(View.GONE);
        linearLayoutRightSeparator.setVisibility(View.GONE);
        linearLayoutDate.setVisibility(View.GONE);
        channelIcon.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linearLayoutProgressBar.setVisibility(View.GONE);
        mLinearLayoutPictureFormatInfo.setVisibility(View.GONE);
        channelName.setText(mStrValues.get(0) + "." + mStrValues.get(1));
        mStrValues = null;
    }

    protected void setChannelInfoInformation() {
        if (mStrValues != null && mStrValues.size() > 2) { // if service except
            // channel name and
            // channel number
            // have and epg,
            // start, end time
            // information
            if (nowEvent) {
                infobanerArrowLeft.setVisibility(View.INVISIBLE);
                infobanerArrowRight.setVisibility(View.VISIBLE);
                currentShow.setText(mStrValues.get(2));
                shortDescription.setText(mStrValues.get(7));
                longDescription.setText(mStrValues.get(12));
            } else {
                infobanerArrowLeft.setVisibility(View.VISIBLE);
                infobanerArrowRight.setVisibility(View.INVISIBLE);
                currentShow.setText(mStrValues.get(3));
                shortDescription.setText(mStrValues.get(8));
                longDescription.setText(mStrValues.get(13));
            }
            linearLayoutEpg.setVisibility(View.VISIBLE);
            linearLayoutLeftSeparator.setVisibility(View.VISIBLE);
            linearLayoutRightSeparator.setVisibility(View.VISIBLE);
            linearLayoutDate.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            linearLayoutProgressBar.setVisibility(View.VISIBLE);
            channelIcon.setVisibility(View.VISIBLE);
            progressBar.setProgress(mChannelProgressValue);
            playerChannelName.setSelected(false);
            channelName.setSelected(true);
            Content cntChannel = ((MainActivity) mActivity).getPageCurl()
                    .getChannelChangeHandler().getCurrentChannelContent();
            if (cntChannel != null)
                channelName.setText(mStrValues.get(0) + "."
                        + cntChannel.getName());
            progressBarStart.setText(mStrValues.get(4));
            progressBarDuration.setText(mStrValues.get(11));
            dateFromStream.setText(mStrValues.get(10));
            timeFromStream.setText(mStrValues.get(1));
            serviceState();
            // //////////////////////////////////////////////////
            // If no now and next information set progress to invisible
            // //////////////////////////////////////////////////
            if (mStrValues.get(2).length() == 0) {
                linearLayoutProgressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                infobanerArrowRight.setVisibility(View.INVISIBLE);
                infobanerArrowLeft.setVisibility(View.INVISIBLE);
            } else {
                linearLayoutProgressBar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
            // ////////////////////////////////////////////////////////////////////////////
            // If we don't have now long description we may press channel up and
            // channel down
            // /////////////////////////////////////////////////////////////////////////////
            if (mStrValues.get(12).length() == 0) {
                description = false;
            } else {
                description = true;
            }
        } else {
            description = false;
            int activeFilterInService = FilterType.ALL;
            try {
                activeFilterInService = MainActivity.service
                        .getContentListControl().getActiveFilterIndex();
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
            int index = MainActivity.activity.getPageCurl()
                    .getChannelChangeHandler().getChannelIndex();
            Content content = ((MainActivity) mActivity).getPageCurl()
                    .getChannelChangeHandler().getCurrentChannelContent();
            String strChannelNumber = "";
            if (ConfigHandler.ATSC && content != null) {
                int major;
                int minor;
                if (ConfigHandler.USE_LCN) {
                    major = content.getServiceLCN()
                            / MAJOR_MINOR_CONVERT_NUMBER;
                    minor = content.getServiceLCN()
                            % MAJOR_MINOR_CONVERT_NUMBER;
                } else {
                    major = content.getIndex() / MAJOR_MINOR_CONVERT_NUMBER;
                    minor = content.getIndex() % MAJOR_MINOR_CONVERT_NUMBER;
                }
                strChannelNumber = String.format("%d-%d", major, minor);
            } else {
                if (activeFilterInService == FilterType.ALL) {
                    strChannelNumber = String.valueOf(index + 1);
                } else {
                    if (content != null) {
                        if (ConfigHandler.USE_LCN)
                            strChannelNumber = String.valueOf(content
                                    .getServiceLCN());
                        else
                            strChannelNumber = String.valueOf(content
                                    .getIndex());
                    }
                }
            }
            playerChannelName.setSelected(false);
            channelName.setSelected(true);
            Content cntChannel = ((MainActivity) mActivity).getPageCurl()
                    .getChannelChangeHandler().getCurrentChannelContent();
            if (cntChannel != null)
                channelName.setText(strChannelNumber/* mStrValues.get(0) */
                        + "." + cntChannel.getName());
            linearLayoutDate.setVisibility(View.INVISIBLE);
            linearLayoutEpg.setVisibility(View.INVISIBLE);
            linearLayoutProgressBar.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    protected void setPlayerChannelInfoInformation() {
        if (mStrValues != null && mStrValues.size() > 2) {
            playerlinearLayoutDate.setVisibility(View.VISIBLE);
            playerProgressBar.setVisibility(View.VISIBLE);
            playerlinearLayoutProgressBar.setVisibility(View.VISIBLE);
            channelIcon.setVisibility(View.VISIBLE);
            playerProgressBar.setProgress(mChannelProgressValue);
            channelName.setSelected(false);
            playerChannelName.setSelected(true);
            playerChannelName.setText(mStrValues.get(0) + "."
                    + mStrValues.get(6));
            /*
             * + "." + ((MainActivity) mActivity).getPageCurl()
             * .getChannelChangeHandler()
             * .getCurrentChannelContent().getName());
             */
            playerProgressBarStart.setText(mStrValues.get(4));
            playerProgressBarDuration.setText(mStrValues.get(11));
            playerDateFromStream.setText(mStrValues.get(10));
            playerTimeFromStream.setText(mStrValues.get(1));
            playerServiceState();
            if (mStrValues.get(4).length() == 0) {
                playerlinearLayoutProgressBar.setVisibility(View.GONE);
                playerProgressBar.setVisibility(View.GONE);
            } else {
                playerlinearLayoutProgressBar.setVisibility(View.VISIBLE);
                playerProgressBar.setVisibility(View.VISIBLE);
            }
        } else {
            int activeFilterInService = FilterType.ALL;
            try {
                activeFilterInService = MainActivity.service
                        .getContentListControl().getActiveFilterIndex();
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
            int index = MainActivity.activity.getPageCurl()
                    .getChannelChangeHandler().getChannelIndex();
            Content content = ((MainActivity) mActivity).getPageCurl()
                    .getChannelChangeHandler().getCurrentChannelContent();
            String strChannelNumber = "";
            if (ConfigHandler.ATSC && content != null) {
                int major;
                int minor;
                if (ConfigHandler.USE_LCN) {
                    major = content.getServiceLCN()
                            / MAJOR_MINOR_CONVERT_NUMBER;
                    minor = content.getServiceLCN()
                            % MAJOR_MINOR_CONVERT_NUMBER;
                } else {
                    major = content.getIndex() / MAJOR_MINOR_CONVERT_NUMBER;
                    minor = content.getIndex() % MAJOR_MINOR_CONVERT_NUMBER;
                }
                strChannelNumber = String.format("%d-%d", major, minor);
            } else {
                if (activeFilterInService == FilterType.ALL) {
                    strChannelNumber = String.valueOf(index + 1);
                } else {
                    if (content != null) {
                        if (ConfigHandler.USE_LCN)
                            strChannelNumber = String.valueOf(content
                                    .getServiceLCN());
                        else
                            strChannelNumber = String.valueOf(content
                                    .getIndex());
                    }
                }
            }
            channelName.setSelected(false);
            playerChannelName.setSelected(true);
            Content cntChannel = ((MainActivity) mActivity).getPageCurl()
                    .getChannelChangeHandler().getCurrentChannelContent();
            if (cntChannel != null)
                playerChannelName.setText(strChannelNumber + "."
                        + cntChannel.getName());
            linearLayoutDate.setVisibility(View.INVISIBLE);
            playerlinearLayoutProgressBar.setVisibility(View.INVISIBLE);
            playerProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void playerServiceState() {
        try {
            subControl = MainActivity.service.getSubtitleControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            ttxControl = MainActivity.service.getTeletextControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            audControl = MainActivity.service.getAudioControl();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        switch (tvStatus) {
            case 0: {
                playerImageViewSubtitle
                        .setImageResource(R.drawable.sub_unselect);
                if (subControl != null) {
                    int numberOfLanguages = 0;
                    try {
                        numberOfLanguages = subControl.getSubtitleTrackCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (numberOfLanguages > 0) {
                        playerImageViewSubtitle
                                .setImageResource(R.drawable.sub_select);
                    }
                }
                playerImageViewTeletext
                        .setImageResource(R.drawable.ttx_unselect);
                if (ttxControl != null) {
                    int numberOfLanguages = 0;
                    try {
                        numberOfLanguages = ttxControl.getTeletextTrackCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (numberOfLanguages > 0) {
                        playerImageViewTeletext
                                .setImageResource(R.drawable.ttx_select);
                    }
                }
                playerImageViewAudio.setImageResource(R.drawable.aud_unselect);
                if (audControl != null) {
                    int numberOfLanguages = 0;
                    try {
                        numberOfLanguages = audControl.getAudioTrackCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (numberOfLanguages > 0) {
                        playerImageViewAudio
                                .setImageResource(R.drawable.aud_select);
                    }
                }
                playerImageViewHbbTv.setImageResource(R.drawable.hbb_unselect);
                boolean isHbbTVEnabled = false;
                try {
                    isHbbTVEnabled = MainActivity.service.getHbbTvControl()
                            .isHbbEnabled();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (isHbbTVEnabled) {
                    playerImageViewHbbTv
                            .setImageResource(R.drawable.hbb_select);
                }
                break;
            }
            case 1: {
                playerImageViewSubtitle
                        .setImageResource(R.drawable.sub_unselect);
                playerImageViewTeletext
                        .setImageResource(R.drawable.ttx_unselect);
                playerImageViewHbbTv.setImageResource(R.drawable.hbb_unselect);
                playerImageViewAudio.setImageResource(R.drawable.aud_unselect);
                playerImageViewHD.setImageResource(R.drawable.hd_unselect);
                break;
            }
            case 2: {
                playerImageViewSubtitle
                        .setImageResource(R.drawable.sub_unselect);
                playerImageViewTeletext
                        .setImageResource(R.drawable.ttx_unselect);
                playerImageViewHbbTv.setImageResource(R.drawable.hbb_unselect);
                playerImageViewAudio.setImageResource(R.drawable.aud_select);
                playerImageViewHD.setImageResource(R.drawable.hd_unselect);
                break;
            }
        }
    }

    private void serviceState() {
        switch (tvStatus) {
            case 0: {
                try {
                    subControl = MainActivity.service.getSubtitleControl();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    ttxControl = MainActivity.service.getTeletextControl();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    audControl = MainActivity.service.getAudioControl();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                imageViewSubtitle.setImageResource(R.drawable.sub_unselect);
                if (subControl != null) {
                    int numberOfLanguages = 0;
                    try {
                        numberOfLanguages = subControl.getSubtitleTrackCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (numberOfLanguages > 0) {
                        imageViewSubtitle
                                .setImageResource(R.drawable.sub_select);
                    }
                }
                imageViewTeletext.setImageResource(R.drawable.ttx_unselect);
                if (ttxControl != null) {
                    int numberOfLanguages = 0;
                    try {
                        numberOfLanguages = ttxControl.getTeletextTrackCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (numberOfLanguages > 0) {
                        imageViewTeletext
                                .setImageResource(R.drawable.ttx_select);
                    }
                }
                imageViewAudio.setImageResource(R.drawable.aud_unselect);
                if (audControl != null) {
                    int numberOfLanguages = 0;
                    try {
                        numberOfLanguages = audControl.getAudioTrackCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (numberOfLanguages > 0) {
                        imageViewAudio.setImageResource(R.drawable.aud_select);
                    }
                }
                boolean isHbbTVEnabled = false;
                try {
                    isHbbTVEnabled = MainActivity.service.getHbbTvControl()
                            .isHbbEnabled();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (isHbbTVEnabled) {
                    imageViewHbbTv.setImageResource(R.drawable.hbb_select);
                } else {
                    imageViewHbbTv.setImageResource(R.drawable.hbb_unselect);
                }
                break;
            }
            case 1: {
                imageViewSubtitle.setImageResource(R.drawable.sub_unselect);
                imageViewTeletext.setImageResource(R.drawable.ttx_unselect);
                imageViewHbbTv.setImageResource(R.drawable.hbb_unselect);
                imageViewAudio.setImageResource(R.drawable.aud_unselect);
                imageViewHD.setImageResource(R.drawable.hd_unselect);
                break;
            }
            case 2: {
                imageViewSubtitle.setImageResource(R.drawable.sub_unselect);
                imageViewTeletext.setImageResource(R.drawable.ttx_unselect);
                imageViewHbbTv.setImageResource(R.drawable.hbb_unselect);
                imageViewAudio.setImageResource(R.drawable.aud_select);
                imageViewHD.setImageResource(R.drawable.hd_unselect);
                break;
            }
        }
    }

    /** Init views for info, pvr, timeshift, playback banner */
    protected void initViews() {
        mViewFlipper = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.infobaner_backgroud);
        channelName = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewChannelName);
        linearLayoutProgressBar = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.linearLayoutProgressBar);
        progressBarStart = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewProgressBarStart);
        progressBarDuration = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewProgressBarDuration);
        progressBar = (ProgressBar) ((MainActivity) mActivity)
                .findViewById(R.id.progress_bar);
        channelIcon = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.channel_icon);
        scrollView = (A4TVInfoDescriptionScrollView) ((MainActivity) mActivity)
                .findViewById(R.id.infobaner_descriptions);
        linearLayoutLeftSeparator = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.linearLayoutLeftSeparator);
        linearLayoutRightSeparator = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.linearLayoutRightSeparator);
        linearLayoutEpg = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.linearLayoutEpg);
        currentShow = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewCurrentShow);
        shortDescription = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewShortDescription);
        longDescription = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewlongDescription);
        linearLayoutDate = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.linearLayoutDate);
        timeFromStream = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewTimeFromStream);
        dateFromStream = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewDateFromStream);
        imageViewTeletext = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.ImageViewTeletext);
        imageViewHD = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.ImageViewHD);
        imageViewSubtitle = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.ImageViewSubtitle);
        imageViewAudio = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.ImageViewAudio);
        imageViewHbbTv = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.ImageViewHbbTv);
        infobanerArrowRight = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.infobaner_right);
        infobanerArrowLeft = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.infobaner_left);
        linearLayoutVolume = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.LinearLayoutVolume);
        linearLayoutInput = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.LinearLayoutInput);
        linearLayoutPlayerInfo = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.LinearLayoutPlayerInfoLayout);
        linearLayoutPlayerElapsedTime = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.LinearLayoutPlayerElapsedTime);
        mLinearLayoutPictureFormatInfo = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.LinearLayoutPictureFormatInfo);
        mTextViewPictureFormatInfo = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.A4TVTextViewPictureFormatInfo);
        textViewPlayerTitle = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewPlayerRecordDummy);
        textViewFileName = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewFileName);
        textViewFileDescription = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewFileDescription);
        textViewNameOfAlbum = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewNameOfAlbum);
        imageViewMediaIcon = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.imageViewMediaIcon);
        textViewRecordTime = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewPlayerRecordTime);
        textViewElapsedTime = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewPlayerElapsedTime);
        textViewStartTime = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewPlayerStartTime);
        textViewEndTime = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewPlayerEndTime);
        textViewPlayerState = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.textViewPlayerState);
        playerProgressBarTime = (A4TVProgressInfoBanner) ((MainActivity) mActivity)
                .findViewById(R.id.playerProgressBarTime);
        imageViewStopPlayerControl = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.imageViewPlayerStop);
        imageViewRewPlayerControl = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.imageViewPlayerREW);
        imageViewPlayPlayerControl = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.imageViewPlayerPlay);
        imageViewFFPlayerControl = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.imageViewPlayerFF);
        imageViewRecPlayerControl = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.imageViewPlayerRecord);
        volumeProgressBar = (A4TVProgressVolumeBanner) ((MainActivity) mActivity)
                .findViewById(R.id.aTVProgressBarVolume);
        textViewVolumeValue = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.aTVTextViewVolumeValue);
        imageViewVolumeState = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.imageViewVolumeState);
        textViewFrameRate = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.TextViewFrameRate);
        textViewResolution = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.TextViewResolution);
        textViewInputName = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.TextViewInputName);
        // ///////////////////////
        // Player channel info
        // /////////////////////////
        playerChannelName = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.player_channel_name);
        playerlinearLayoutDate = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.playerlinearLayoutDate);
        playerProgressBar = (ProgressBar) ((MainActivity) mActivity)
                .findViewById(R.id.player_progress_bar);
        playerlinearLayoutProgressBar = (LinearLayout) ((MainActivity) mActivity)
                .findViewById(R.id.player_linear_layout_progress_bar);
        playerProgressBarStart = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.player_progress_bar_start);
        playerProgressBarDuration = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.player_progress_bar_duration);
        playerTimeFromStream = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.player_time_from_stream);
        playerDateFromStream = (A4TVTextView) ((MainActivity) mActivity)
                .findViewById(R.id.player_date_from_stream);
        playerImageViewSubtitle = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.playerImageViewSubtitle);
        playerImageViewTeletext = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.playerImageViewTeletext);
        playerImageViewAudio = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.playerImageViewAudio);
        playerImageViewHbbTv = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.playerImageViewHbbTv);
        playerImageViewHD = (ImageView) ((MainActivity) mActivity)
                .findViewById(R.id.playerImageViewHD);
    }

    /** Animation in for info,pvr,timeshift and playback banner */
    protected void animationIn(final View v) {
        objectAnimator = ObjectAnimator.ofFloat(v, "translationY", 100, 0);
        objectAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        objectAnimator.setDuration(60);
        objectAnimator.start();
    }

    /** Animation out for info, pvr, timeshift and playback banner */
    protected void animationOut(final View v) {
        objectAnimator = ObjectAnimator.ofFloat(v, "translationY", 0, 100);
        objectAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        objectAnimator.setDuration(60);
        objectAnimator.start();
    }

    /** Set multimedia playback fields to null */
    protected void flushMultimediaPlaybackFields() {
        textViewPlayerTitle.setText("");
        textViewRecordTime.setText("");
        textViewStartTime.setText("00:00:00");
        playerProgressBarTime.setProgress(0);
        playerProgressBarTime.setSecondaryProgress(0);
        imageViewMediaIcon.setImageBitmap(null);
        textViewFileName.setText("");
        textViewFileDescription.setText("");
        textViewNameOfAlbum.setText("");
        textViewPlayerState.setText("");
    }

    /** Set pvr and timeshift fields to null */
    protected void flushPlayerFields() {
        linearLayoutPlayerElapsedTime.setVisibility(View.INVISIBLE);
        textViewPlayerTitle.setText("");
        textViewRecordTime.setText("");
        textViewElapsedTime.setText("");
        textViewEndTime.setText("00:00:00");
        textViewStartTime.setText("00:00:00");
        playerProgressBarTime.setProgress(0);
        playerProgressBarTime.setSecondaryProgress(0);
        imageViewMediaIcon.setImageBitmap(null);
        textViewFileName.setText("");
        textViewPlayerState.setText("");
    }

    protected void setImageRecord(int position) {
        if (position == MULTIMEDIA_CONTROLLER_RE) {
            imageViewRecPlayerControl
                    .setImageResource(R.drawable.media_controller_record_focused);
        } else {
            if (A4TVProgressBarPVR.getControlProviderPVR().isFlagRecord()) {
                imageViewRecPlayerControl
                        .setImageResource(R.drawable.media_controller_record_recording);
            } else {
                imageViewRecPlayerControl
                        .setImageResource(R.drawable.media_controller_record_un_focused);
            }
        }
    }

    protected void setImageStop(int position) {
        if (position == MULTIMEDIA_CONTROLLER_STOP) {
            imageViewStopPlayerControl
                    .setImageResource(R.drawable.media_controller_stop_focused);
        } else {
            imageViewStopPlayerControl
                    .setImageResource(R.drawable.media_controller_stop_un_focused);
        }
    }

    protected void setImageRew(int position) {
        if (position == MULTIMEDIA_CONTROLLER_REW_PREVIOUS) {
            imageViewRewPlayerControl
                    .setImageResource(R.drawable.media_controller_rew_focused);
        } else {
            imageViewRewPlayerControl
                    .setImageResource(R.drawable.media_controller_rew_un_focused);
        }
    }

    protected void setPlaybackImageRew(int position) {
        if (position == MULTIMEDIA_CONTROLLER_REW_PREVIOUS) {
            if (A4TVMultimediaController.getControlProvider().getFlagPlay()
                    || A4TVMultimediaController.getControlProvider()
                            .getFlagFFREW()) {
                imageViewRewPlayerControl
                        .setImageResource(R.drawable.media_controller_rew_focused);
            } else {
                imageViewRewPlayerControl
                        .setImageResource(R.drawable.media_controller_previous_focused);
            }
        } else {
            if (A4TVMultimediaController.getControlProvider().getFlagPlay()
                    || A4TVMultimediaController.getControlProvider()
                            .getFlagFFREW()) {
                imageViewRewPlayerControl
                        .setImageResource(R.drawable.media_controller_rew_un_focused);
            } else {
                imageViewRewPlayerControl
                        .setImageResource(R.drawable.media_controller_previous_un_focused);
            }
        }
    }

    protected void setImageFF(int position) {
        if (position == MULTIMEDIA_CONTROLLER_FF_NEXT) {
            imageViewFFPlayerControl
                    .setImageResource(R.drawable.media_controller_ff_focused);
        } else {
            imageViewFFPlayerControl
                    .setImageResource(R.drawable.media_controller_ff_un_focused);
        }
    }

    protected void setPlaybackImageForward(int position) {
        if (position == MULTIMEDIA_CONTROLLER_FF_NEXT) {
            if (A4TVMultimediaController.getControlProvider().getFlagPlay()
                    || A4TVMultimediaController.getControlProvider()
                            .getFlagFFREW()) {
                imageViewFFPlayerControl
                        .setImageResource(R.drawable.media_controller_ff_focused);
            } else {
                imageViewFFPlayerControl
                        .setImageResource(R.drawable.media_controller_next_focused);
            }
        } else {
            if (A4TVMultimediaController.getControlProvider().getFlagPlay()
                    || A4TVMultimediaController.getControlProvider()
                            .getFlagFFREW()) {
                imageViewFFPlayerControl
                        .setImageResource(R.drawable.media_controller_ff_un_focused);
            } else {
                imageViewFFPlayerControl
                        .setImageResource(R.drawable.media_controller_next_un_focused);
            }
        }
    }

    protected void setImagePlay(int position) {
        if (position == MULTIMEDIA_CONTROLLER_PLAY) {
            if (!A4TVProgressBarPVR.getControlProviderPVR().getFlagPlay()) {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_play_focused);
            } else {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_pause_focused);
            }
        } else {
            if (!A4TVProgressBarPVR.getControlProviderPVR().getFlagPlay()) {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_play_un_focused);
            } else {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_pause_un_focused);
            }
        }
    }

    protected void setPlaybackImagePlay(int position) {
        if (position == MULTIMEDIA_CONTROLLER_PLAY) {
            if (!A4TVMultimediaController.getControlProvider().getFlagPlay()) {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_play_focused);
            } else {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_pause_focused);
            }
        } else {
            if (!A4TVMultimediaController.getControlProvider().getFlagPlay()) {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_play_un_focused);
            } else {
                imageViewPlayPlayerControl
                        .setImageResource(R.drawable.media_controller_pause_un_focused);
            }
        }
    }

    protected void setImageRepeat(int position) {
        if (position == MULTIMEDIA_CONTROLLER_RE) {
            switch (A4TVMultimediaController.getControlRepeatPosition()) {
                case 0: {
                    imageViewRecPlayerControl
                            .setImageResource(R.drawable.media_controller_repeat_off_focused);
                    break;
                }
                case 1: {
                    imageViewRecPlayerControl
                            .setImageResource(R.drawable.media_controller_repeat_one_focused);
                    break;
                }
                case 2: {
                    imageViewRecPlayerControl
                            .setImageResource(R.drawable.media_controller_repeat_all_focused);
                    break;
                }
                default:
                    break;
            }
        } else {
            switch (A4TVMultimediaController.getControlRepeatPosition()) {
                case 0: {
                    imageViewRecPlayerControl
                            .setImageResource(R.drawable.media_controller_repeat_off_un_focused);
                    break;
                }
                case 1: {
                    imageViewRecPlayerControl
                            .setImageResource(R.drawable.media_controller_repeat_one_un_focused);
                    break;
                }
                case 2: {
                    imageViewRecPlayerControl
                            .setImageResource(R.drawable.media_controller_repeat_all_un_focused);
                    break;
                }
                default:
                    break;
            }
        }
    }

    /** Draw player image control */
    protected void setPlayerImageControl(int state) {
        int position;
        if (state == STATE_PVR) {
            position = A4TVProgressBarPVR.getControlPosition();
            setImagePlay(position);
            setImageRecord(position);
            setImageFF(position);
            setImageRew(position);
            setImageStop(position);
        } else {
            position = A4TVMultimediaController.getControlPosition();
            setPlaybackImagePlay(position);
            setImageRepeat(position);
            setImageStop(position);
            setPlaybackImageForward(position);
            setPlaybackImageRew(position);
        }
    }

    protected void setStrValues(ArrayList<String> strValues) {
        this.mStrValues = strValues;
    }

    protected void setChannelProgressValue(int mChannelProgressValue) {
        this.mChannelProgressValue = mChannelProgressValue;
    }

    protected int getImageByVolumeLevel(int volumeLevel) {
        if (volumeLevel == 0) {
            return R.drawable.volume_icon_mute;
        } else if (volumeLevel >= 0 && volumeLevel <= 10) {
            return R.drawable.volume_icon_10;
        } else if (volumeLevel <= 30) {
            return R.drawable.volume_icon_30;
        } else if (volumeLevel <= 60) {
            return R.drawable.volume_icon_60;
        } else {
            return R.drawable.volume_icon_max;
        }
    }
}
