package com.iwedia.service.servicemode;

import android.os.RemoteException;

import com.iwedia.comm.IServiceMode;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.proxyservice.IDTVInterface;

public class ServiceMode extends IServiceMode.Stub implements IDTVInterface {
    @Override
    public int getMaxVolume() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getMaxVolume();
    }

    @Override
    public boolean setMaxVolume(int maxVolume) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setMaxVolume(maxVolume);
        return true;
    }

    @Override
    public boolean getVolumeFixed() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getVolumeFixed();
    }

    @Override
    public boolean setVolumeFixed(boolean volumeFixed) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setVolumeFixed(volumeFixed);
        return true;
    }

    @Override
    public int getVolumeFixedLevel() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getVolumeFixedLevel();
    }

    @Override
    public boolean setVolumeFixedLevel(int volumeFixedLevel)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setVolumeFixedLevel(volumeFixedLevel);
        return true;
    }

    @Override
    public boolean getRCButton() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getRCButton();
    }

    @Override
    public boolean setRCButton(boolean RCButton) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setRCButton(RCButton);
        return true;
    }

    @Override
    public boolean getPanelButton() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getPanelButton();
    }

    @Override
    public boolean setPanelButton(boolean panelButton) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setPanelButton(panelButton);
        return true;
    }

    @Override
    public boolean getMenuButton() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getMenuButton();
    }

    @Override
    public boolean setMenuButton(boolean menuButton) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setMenuButton(menuButton);
        return true;
    }

    @Override
    public int getInputModeStart() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getInputModeStart();
    }

    @Override
    public boolean setInputModeStart(int input) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setInputModeStart(input);
        return true;
    }

    @Override
    public boolean getInputModeFixed() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getInputModeFixed();
    }

    @Override
    public boolean setInputModeFixed(boolean inputMode) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setInputModeFixed(inputMode);
        return true;
    }

    @Override
    public int getInputTVProgramNumber() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getInputTVProgramNumber();
    }

    @Override
    public boolean setInputTVProgramNumber(int prog_number)
            throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setInputTVProgramNumber(prog_number);
        return true;
    }

    @Override
    public boolean getOnScreenDisplay() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getOnScreenDisplay();
    }

    @Override
    public boolean setOnScreenDisplay(boolean value) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setOnScreenDisplay(value);
        return true;
    }

    @Override
    public boolean reset() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode().reset();
        return true;
    }

    @Override
    public boolean commit() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode().commit();
        return true;
    }

    @Override
    public String getNormalStandbyCause() throws RemoteException {
        return IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .getNormalStandbyCause();
    }

    @Override
    public boolean resetStandbyCause() throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .resetStandbyCause();
        return true;
    }

    @Override
    public boolean setPattern(int pattern) throws RemoteException {
        IWEDIAService.getInstance().getDTVManager().getServiceMode()
                .setPattern(pattern);
        return true;
    }

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}
