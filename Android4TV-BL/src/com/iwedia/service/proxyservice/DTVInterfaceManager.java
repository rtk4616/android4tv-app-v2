package com.iwedia.service.proxyservice;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds instance of all Interface classes: {@link IAudioControl,
 * IConditionalAccessControl, IEpgControl, IHbbControl, IScanControl,
 * IMhegControl, IPvrControl, IReminderControl, IServiceListControl,
 * ISubtitleControl, IVideoControl , ITeletextControl , IParentalControl ,
 * ICallbacksControl, IActionControl , IContentListControl , ISystemControl ,
 * IDlnaControl , IOnDemandControl , ISetupControl }. Used to propagate system
 * important events to all modules for handling if needed, e.g. (channelZapping
 * event).
 * 
 * @author Marko Zivanovic
 */
public class DTVInterfaceManager implements IDTVInterface {
    @SuppressWarnings("unused")
    private final String LOG_TAG = "DTVInterfaceManager";
    private HashMap<Integer, IDTVInterface> dtvInterfaces;

    public DTVInterfaceManager() {
        dtvInterfaces = new HashMap<Integer, IDTVInterface>();
    }

    public void addDTVInterface(Integer key, IDTVInterface dtvInterface) {
        dtvInterfaces.put(key, dtvInterface);
    }

    @Override
    public void channelZapping(boolean status) {
        for (Map.Entry<Integer, IDTVInterface> entry : dtvInterfaces.entrySet()) {
            IDTVInterface dtvInterface = entry.getValue();
            if (dtvInterface != null) {
                dtvInterface.channelZapping(status);
            }
        }
    }
}
