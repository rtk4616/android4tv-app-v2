package com.iwedia.service.system.application;

import android.util.Log;

import com.iwedia.comm.system.application.IApplicationRestart;
import com.iwedia.service.IWEDIAService;

public class ApplicationRestart extends IApplicationRestart.Stub {
    private static final String LOG_TAG = "ApplicationRestart";

    public ApplicationRestart() {
    }

    public void binderRestart(String componentName) {
        Log.i(LOG_TAG, "Set restart application");
        IWEDIAService.getInstance().setRestartFlag(true);
        IWEDIAService.getInstance().setActivityComponentName(componentName);
    }
}
