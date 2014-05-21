package com.iwedia.gui.epg;

import android.view.View;
import android.view.View.OnClickListener;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.dialogs.EPGScheduleDialog;

/**
 * Click listener for EPG event
 * 
 * @author Branimir Pavlovic
 */
public class EPGEventClickListener implements OnClickListener {
    private MainActivity activity;
    private EpgEvent event;
    private EPGHandlingClass epgHandler;

    public EPGEventClickListener(MainActivity activity, EpgEvent event,
            EPGHandlingClass epgHandler) {
        this.activity = activity;
        this.event = event;
        this.epgHandler = epgHandler;
    }

    @Override
    public void onClick(View v) {
        EPGScheduleDialog epgScheduleDialog = activity.getDialogManager()
                .getEpgScheduleDialog();
        if (epgScheduleDialog != null) {
            epgScheduleDialog.showEpgExtended(event, null, true);
        }
    }
}
