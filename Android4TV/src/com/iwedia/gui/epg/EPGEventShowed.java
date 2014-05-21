package com.iwedia.gui.epg;

import android.view.View;
import android.widget.Button;

import com.iwedia.dtv.types.TimeDate;

/**
 * EPG Event showed in EPG
 * 
 * @author Branimir Pavlovic
 */
public class EPGEventShowed {
    private float eventHeight;
    private int indexOfService;
    private int indexOfEvent;
    private int eventDuration;
    private TimeDate startTime;
    private TimeDate endTime;
    private View btnOnScreen;
    private boolean isShowed;

    public EPGEventShowed(float eventHeight, int indexOfService,
            int indexOfEvent, int eventDuration, TimeDate startTime,
            TimeDate endTime, View btnOnScreen) {
        super();
        this.eventHeight = eventHeight;
        this.indexOfService = indexOfService;
        this.indexOfEvent = indexOfEvent;
        this.eventDuration = eventDuration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.btnOnScreen = btnOnScreen;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(startTime).append(" - ").append(endTime)
                .append(" Event INDEX: ").append(indexOfEvent).toString();
    }

    public float getEventHeight() {
        return eventHeight;
    }

    public int getIndexOfService() {
        return indexOfService;
    }

    public int getIndexOfEvent() {
        return indexOfEvent;
    }

    public int getEventDuration() {
        return eventDuration;
    }

    public TimeDate getStartTime() {
        return startTime;
    }

    public TimeDate getEndTime() {
        return endTime;
    }

    public void setEventHeight(float eventHeight) {
        this.eventHeight = eventHeight;
    }

    public void setIndexOfService(int indexOfService) {
        this.indexOfService = indexOfService;
    }

    public void setIndexOfEvent(int indexOfEvent) {
        this.indexOfEvent = indexOfEvent;
    }

    public void setEventDuration(int eventDuration) {
        this.eventDuration = eventDuration;
    }

    public void setStartTime(TimeDate startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(TimeDate endTime) {
        this.endTime = endTime;
    }

    public View getBtnOnScreen() {
        return btnOnScreen;
    }

    public void setBtnOnScreen(Button btnOnScreen) {
        this.btnOnScreen = btnOnScreen;
    }

    public boolean isShowed() {
        return isShowed;
    }

    public void setShowed(boolean isShowed) {
        this.isShowed = isShowed;
    }
}
