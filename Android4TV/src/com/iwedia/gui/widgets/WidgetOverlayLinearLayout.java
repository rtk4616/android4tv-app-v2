package com.iwedia.gui.widgets;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Overlay layout for widget
 * 
 * @author Veljko Ilkic
 */
public class WidgetOverlayLinearLayout extends LinearLayout {
    /** Widget object */
    private WidgetObject widgetObject;
    /** Widget handler object */
    private WidgetsHandler widgetHandler;
    /** Main widget layout */
    private FrameLayout widgetMainLayout;
    /** Real widget content */
    private AppWidgetHostView widgetContent;
    /** Countdown timer */
    private CountDownTimer couter;
    /** Long click threshold */
    public static final int LONG_CLICK_THRESHOLD = 750;
    /** Elapsed time on touch for long click */
    private long time;

    /** Constructor 1 */
    public WidgetOverlayLinearLayout(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    /** Constructor 2 */
    public WidgetOverlayLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** Constructor 3 */
    public WidgetOverlayLinearLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Push event into widget content
        widgetContent.dispatchTouchEvent(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                couter = new CountDownTimer(LONG_CLICK_THRESHOLD, 50) {
                    public void onTick(long millisUntilFinished) {
                        time = millisUntilFinished;
                    }

                    public void onFinish() {
                        // //////////////////////////////////
                        // Long click
                        // //////////////////////////////////
                        // Start dragging
                        widgetHandler.startDrag(widgetMainLayout, widgetObject);
                    }
                }.start();
                break;
            }
            case MotionEvent.ACTION_UP: {
                // ///////////////////////////
                // Short click
                // ////////////////////////////
                if (time > LONG_CLICK_THRESHOLD / 2) {
                    // Cancel long click counter
                    couter.cancel();
                    return false;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                // Cancel long click counter
                couter.cancel();
                return false;
            }
        }
        return true;
    }

    // //////////////////////////////////////////
    // Setters
    // //////////////////////////////////////////
    /** Set widget object */
    public void setWidgetObject(WidgetObject widgetObject) {
        this.widgetObject = widgetObject;
    }

    /** Set widget handler object */
    public void setWidgetHandler(WidgetsHandler widgetHandler) {
        this.widgetHandler = widgetHandler;
    }

    /** Set widget main layout */
    public void setWidgetMainLayout(FrameLayout widgetMainLayout) {
        this.widgetMainLayout = widgetMainLayout;
    }

    public void setWidgetContent(AppWidgetHostView widgetContent) {
        this.widgetContent = widgetContent;
    }
}
