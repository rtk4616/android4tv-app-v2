package com.iwedia.service.storage;

import java.util.HashMap;

import android.util.Log;

import com.iwedia.service.IWEDIAService;

public class ControllerManager {
    private final String LOG_TAG = "ControllerManager";
    private HashMap<Integer, IController> controllerManager;
    private IController activeController;
    private A_DbAdapter dbAdapter;

    public ControllerManager(A_DbAdapter dbAdapter) {
        controllerManager = new HashMap<Integer, IController>();
        this.dbAdapter = dbAdapter;
    }

    public void reinitialize() {
    }

    private void addController(int controllerType, IController controller) {
        controllerManager.put(controllerType, controller);
        this.activeController = controller;
    }

    public void setActiveController(int controllerType) {
        IController controller = controllerManager.get(controllerType);
        if (controller == null) {
            switch (controllerType) {
                case ControllerType.FAVOURITE_LIST: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "creating favourite list controller");
                    }
                    addController(ControllerType.FAVOURITE_LIST,
                            new FavouriteListController(dbAdapter));
                    break;
                }
                case ControllerType.RECENTLY_LIST: {
                    if (IWEDIAService.DEBUG) {
                        Log.e(LOG_TAG, "creating RECENTLY_LIST controller");
                    }
                    addController(ControllerType.RECENTLY_LIST,
                            new RecentlyListController(dbAdapter));
                    break;
                }
            }
        } else {
            this.activeController = controller;
        }
    }

    public IController getActiveController() {
        return activeController;
    }
}
