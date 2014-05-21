package com.iwedia.service.system.sound;

import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;

import com.iwedia.comm.system.ISoundSettings;
import com.iwedia.dtv.sound.AudioChannelMode;
import com.iwedia.dtv.sound.SoundEffect;
import com.iwedia.dtv.sound.SoundEffectParam;
import com.iwedia.dtv.sound.SoundMode;
import com.iwedia.dtv.sound.AudioEqualizerBand;
import com.iwedia.dtv.io.SpdifMode;
import com.iwedia.service.IWEDIAService;

/**
 * The sound controller. Sets the volume and other audio settings.
 * 
 * @author Stanislava Markovic
 */
public class SoundSettings extends ISoundSettings.Stub {
    private AudioManager audioManager;
    private int SYSTEM_MAX_VOLUME;
    private int androidVolume;
    private int MW_MAX_VOLUME = 100;
    private double STEP = 2.86;

    public SoundSettings() {
        audioManager = (AudioManager) IWEDIAService.getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        SYSTEM_MAX_VOLUME = audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        androidVolume = 0;
    }

    /**
     * Gets active sound mode.
     * 
     * @return active sound mode.
     */
    @Override
    public SoundMode getActiveSoundMode() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getSoundMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets active sound mode.
     * 
     * @param mode
     *        - sound mode you want to set
     */
    @Override
    public void setActiveSoundMode(SoundMode mode) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .setSoundMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), mode);
    }

    /**
     * Gets active SPDIF mode.
     * 
     * @return active SPDIF mode.
     */
    @Override
    public SpdifMode getActiveSpdifMode() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager()
                .getInputOutputControl().getActiveSpdifMode(0);
    }

    /**
     * Sets active SPDIF mode.
     * 
     * @param mode
     *        - SPDIF mode you want to set
     */
    @Override
    public void setActiveSpdifMode(SpdifMode mode) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getInputOutputControl()
                .setActiveSpdifMode(0, mode);
    }

    /**
     * Check if volume is automatic.
     * 
     * @return true if volume is automatic, else false.
     */
    @Override
    public boolean isAutoVolume() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .getAutoVolume(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets auto volume value;
     * 
     * @param autoVolume
     *        - true for auto volume, else false.
     */
    @Override
    public void setAutoVolume(boolean autoVolume) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .setAutoVolume(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), autoVolume);
    }

    /**
     * Gets volume value.
     * 
     * @return volume value.
     */
    @Override
    public int getVolume() throws RemoteException {
        return (int) IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .getVolume(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets volume value.
     * 
     * @param volume
     *        - value for the volume you want to set
     */
    @Override
    public void setVolume(double volume) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .setVolume(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), volume);
        setSystemVolume(volume);
    }

    /**
     * Mutes or unmutes audio
     * 
     * @param mute
     *        - true if you want to mute the audio, false if you want to unmute
     *        the audio.
     * @return true if everything is OK, else false
     */
    @Override
    public void muteAudio(boolean mute) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .muteAudio(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), mute);
        if (mute) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        } else {
            setSystemVolume(IWEDIAService
                    .getInstance()
                    .getDTVManager()
                    .getAudioControl()
                    .getVolume(
                            IWEDIAService.getInstance().getDtvManagerProxy()
                                    .getCurrentLiveRoute()));
        }
    }

    /**
     * Gets audio mute state.
     * 
     * @return true if muted, else false.
     */
    @Override
    public boolean getAudioMute() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .isMute(IWEDIAService.getInstance().getDtvManagerProxy()
                        .getCurrentLiveRoute());
    }

    /**
     * Gets treble value.
     * 
     * @return treble value.
     */
    @Override
    public int getTreble() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getTreble(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets treble value.
     * 
     * @param treble
     *        - value for the treble you want to set
     */
    @Override
    public void setTreble(int treble) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .setTreble(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), treble);
    }

    /**
     * Gets balance value;
     * 
     * @return balance value.
     */
    @Override
    public int getBalance() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .getVolumeBalance(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets balance value.
     * 
     * @param balance
     *        - value for the balance you want to set
     */
    @Override
    public void setBalance(int balance) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .setVolumeBalance(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), balance);
    }

    /**
     * Gets bass value.
     * 
     * @return bass value.
     */
    @Override
    public int getBass() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getBass(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets bass value.
     * 
     * @param bass
     *        - value for the bass you want to set
     */
    @Override
    public void setBass(int bass) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .setBass(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), bass);
    }

    /**
     * Gets headphone volume value.
     * 
     * @return headphone volume value.
     */
    @Override
    public int getHeadphoneVolume() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .getHeadphonesVolume(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets headphone volume value.
     * 
     * @param volume
     *        - value for the volume you want to set
     */
    @Override
    public void setHeadphoneVolume(int volume) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .setHeadphonesVolume(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), volume);
    }

    /**
     * Get number of EQ bands
     * 
     * @return number of EQ bands
     */
    @Override
    public int getNumberOfEqualizerBands() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getNumberOfEqualizerBands(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Get central frequency of EQ band
     * 
     * @param band
     *        bend whose frequency to return
     * @return central frequency of EQ band
     */
    @Override
    public int getEqualizerBandFrequency(AudioEqualizerBand band)
            throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getEqualizerBandFrequency(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), band);
    }

    /**
     * Get value of EQ band
     * 
     * @param band
     *        bend whose value to return
     * @return value of EQ band
     */
    @Override
    public int getEqualizerBandValue(AudioEqualizerBand band)
            throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getEqualizerBandValue(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), band);
    }

    /**
     * Sets headphone volume value.
     * 
     * @param volume
     *        - value for the volume you want to set
     */
    @Override
    public void setEqualizerBandValue(AudioEqualizerBand band, int value)
            throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .setEqualizerBandValue(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), band, value);
    }

    @Override
    public boolean isSoundEffectEnabled(SoundEffect effect)
            throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .isSoundEffectEnabled(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), effect);
    }

    @Override
    public void setSoundEffectEnabled(SoundEffect effect, boolean enabled)
            throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .setSoundEffectEnabled(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), effect, enabled);
    }

    @Override
    public int getSoundEffectParam(SoundEffectParam paramId)
            throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getSoundEffectParam(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), paramId);
    }

    @Override
    public void setSoundEffectParam(SoundEffectParam paramId, int value)
            throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .setSoundEffectParam(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), paramId, value);
    }

    /**
     * Returns mute status.
     * 
     * @return true if muted, else false.
     */
    @Override
    public boolean isMute() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getAudioControl()
                .isMute(IWEDIAService.getInstance().getDtvManagerProxy()
                        .getCurrentLiveRoute());
    }

    private void setSystemVolume(Double volume) {
        if (volume >= 0 && volume < MW_MAX_VOLUME) {
            androidVolume = (int) ((volume) / STEP);
        }
        if (volume == MW_MAX_VOLUME) {
            androidVolume = SYSTEM_MAX_VOLUME;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, androidVolume,
                0);
    }

    @Override
    public boolean getAudioDescritpion() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getAudioControl()
                .isAudioDescriptionEnabled();
    }

    @Override
    public void setAudioDescription(boolean onOff) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getAudioControl()
                .setAudioDescription(onOff);
    }

    @Override
    public AudioChannelMode getAudioChannelMode() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .getAudioChannelMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    @Override
    public void setAudioChannelMode(AudioChannelMode mode)
            throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getSoundControl()
                .setAudioChannelMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), mode);
    }

    @Override
    public void setAudioMenuDefaultSettings() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getSoundControl()
                .setAudioMenuDefaultSettings();
    }
}
