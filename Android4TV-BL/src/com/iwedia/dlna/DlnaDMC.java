package com.iwedia.dlna;

import android.os.RemoteException;

import com.iwedia.dlna.dmc.service.DlnaDmcService;

public class DlnaDMC extends DlnaDMP {
    public DlnaDMC(DlnaDmcService nativeService) throws DlnaException {
        if (nativeService == null) {
            throw new IllegalArgumentException();
        }
        try {
            nativeHandle = nativeService.getDlnaService().dmcCreate(1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.nativeService = nativeService;
    }
}
