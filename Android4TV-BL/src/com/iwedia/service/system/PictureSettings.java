package com.iwedia.service.system;

import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.system.IPictureSettings;
import com.iwedia.dtv.picture.FilmModeDetection;
import com.iwedia.dtv.picture.PictureFineMotionMode;
import com.iwedia.dtv.picture.PictureMode;
import com.iwedia.dtv.picture.PictureNoiseReductionMode;
import com.iwedia.dtv.types.AspectRatioMode;
import com.iwedia.service.IWEDIAService;

/**
 * MW picture settings.
 * 
 * @author Marko Zivanovic
 */
public class PictureSettings extends IPictureSettings.Stub {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "PictureSettings";

    /**
     * Return active picture mode.
     */
    @Override
    public int getActivePictureMode() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "getActivePictureMode");
        }
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getPictureMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute()).getValue();
    }

    /**
     * Sets active picture mode.
     */
    @Override
    public void setActivePictureMode(int pictureMode) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setPictureMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(),
                        PictureMode.values()[pictureMode]);
    }

    /**
     * Returns active aspect ratio.
     */
    @Override
    public AspectRatioMode getAspectRatioMode() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "getAspectRatioMode");
        }
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getAspectRatioMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets active aspect ratio.
     * 
     * @param aspectRatio
     *        enums {@link com.iwedia.comm.enums.AspectRatio}
     */
    @Override
    public void setAspectRatioMode(AspectRatioMode aspectRatioMode)
            throws RemoteException {
        if (DEBUG)
            Log.e(LOG_TAG,
                    "setActiveAspectRatio (" + aspectRatioMode.getValue() + ")");
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setAspectRatioMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), aspectRatioMode);
    }

    /**
     * Returns active color temperature.
     */
    @Override
    public int getActiveColorTemperature() throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "getActiveColorTemperature");
        }
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getColor(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets active color temperature.
     */
    @Override
    public void setActiveColorTemperature(int colorTemperature)
            throws RemoteException {
        if (DEBUG)
            Log.e(LOG_TAG, "setActiveColorTemperature (" + colorTemperature
                    + ")");
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setColor(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), colorTemperature);
    }

    /**
     * Gets active noise reduction.
     */
    @Override
    public int getActiveNoiseReduction() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getNoiseReductionMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute()).getValue();
    }

    /**
     * Sets active noise reduction.
     */
    @Override
    public void setActiveNoiseReduction(int noiseReduction)
            throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setNoiseReductionMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(),
                        PictureNoiseReductionMode.values()[noiseReduction]);
    }

    /**
     * Gets active film mode.
     */
    @Override
    public int getActiveFilmMode() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getFilmMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute()).getValue();
    }

    /**
     * Sets active film mode.
     */
    @Override
    public void setActiveFilmMode(int filmMode) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setFilmMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(),
                        FilmModeDetection.values()[filmMode]);
    }

    /**
     * Gets active fine motion.
     */
    @Override
    public int getActiveFineMotion() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getFineMotionMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute()).getValue();
    }

    /**
     * Sets active fine mode.
     */
    @Override
    public void setActiveFineMode(int fineMode) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setFineMotionMode(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(),
                        PictureFineMotionMode.values()[fineMode]);
    }

    /**
     * Returns active theme.
     */
    @Override
    public String getActiveTheme() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Sets active theme.
     */
    @Override
    public void setActiveTheme(String theme) throws RemoteException {
        // TODO Auto-generated method stub
    }

    /**
     * Returns sharpness.
     */
    @Override
    public int getSharpness() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getSharpness(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets sharpness.
     */
    @Override
    public void setSharpness(double sharpness) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setSharpness(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), (int) sharpness);
    }

    /**
     * Returns dynamic backlight.
     */
    @Override
    public boolean isDynamicBacklight() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getDynamicBacklight(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets dynamic backlight.
     */
    @Override
    public void setDynamicBacklight(boolean dynamicBacklight)
            throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setDynamicBacklight(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), dynamicBacklight);
    }

    /**
     * Returns hue.
     */
    @Override
    public int getHue() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getHue(IWEDIAService.getInstance().getDtvManagerProxy()
                        .getCurrentLiveRoute());
    }

    /**
     * Returns saturation.
     */
    @Override
    public int getSaturation() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getSaturation(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets brightness.
     */
    @Override
    public void setBrightness(int arg0) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setBrightness(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), arg0);
    }

    /**
     * Sets color.
     */
    @Override
    public void setColor(int arg0) throws RemoteException {
        // TODO Auto-generated method stub
    }

    /**
     * Sets contrast.
     */
    @Override
    public void setContrast(int arg0) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setContrast(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), arg0);
    }

    /**
     * Sets hue.
     */
    @Override
    public void setHue(int arg0) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setHue(IWEDIAService.getInstance().getDtvManagerProxy()
                        .getCurrentLiveRoute(), arg0);
    }

    /**
     * sets saturation.
     */
    @Override
    public void setSaturation(int arg0) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setSaturation(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), arg0);
    }

    /**
     * Sets backlight.
     */
    @Override
    public void setsBacklight(int arg0) throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setBackLight(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute(), arg0);
    }

    /**
     * Returns backlight.
     */
    @Override
    public int getBacklight() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getBackLight(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Returns brightness.
     */
    @Override
    public int getBrightness() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getBrightness(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Returns color.
     */
    @Override
    public int getColor() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getColor(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Returns contrast.
     */
    @Override
    public int getContrast() throws RemoteException {
        return IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .getContrast(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }

    /**
     * Sets all parameters to default values.
     */
    @Override
    public void setPictureMenuDefaultSettings() throws RemoteException {
        IWEDIAService
                .getInstance()
                .getDTVManager()
                .getPictureControl()
                .setPictureMenuDefaultSettings(
                        IWEDIAService.getInstance().getDtvManagerProxy()
                                .getCurrentLiveRoute());
    }
}
