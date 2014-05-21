package com.iwedia.gui.epg;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.gui.R;
import com.iwedia.gui.util.DateTimeConversions;

import java.text.SimpleDateFormat;

/**
 * Focus listener for EPG events
 * 
 * @author Branimir Pavlovic
 */
public class EPGEventFocusListener implements OnFocusChangeListener {
    public static final String TAG = "EPGEventFocusListener";
    private Button btn;
    private EpgEvent event;
    private boolean smallEventListener;
    private EPGHandlingClass epgHandler;
    public static boolean isStarted = false;
    public static Handler handler = null;
    private static SimpleDateFormat formatTime24Hour = new SimpleDateFormat(
            "HH:mm");
    private TextView tvEventName;

    public EPGEventFocusListener(EPGHandlingClass epgHandler, Button btn,
            EpgEvent event, boolean smallEventListener, TextView tvEventName) {
        this.btn = btn;
        this.event = event;
        this.smallEventListener = smallEventListener;
        this.epgHandler = epgHandler;
        this.tvEventName = tvEventName;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "onFocusChange, hasFocus: " + hasFocus
                + ", smallEventListener: " + smallEventListener);
        // if (hasFocus) {
        // epgHandler.setViewThatHasFocus(v);
        // }
        // if it is for small EPG event
        if (smallEventListener) {
            if (hasFocus) {
                Log.d(TAG, "BOOLEAN ISSTARTED: " + isStarted);
                if (!isStarted) {
                    // epgHandler.setStarted(true);
                    isStarted = true;
                    // show extended version of view delayed
                    Log.d(TAG, "handler: " + handler);
                    if (handler == null) {
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // do the rest if button still has focus
                                if (btn.hasFocus()
                                        && epgHandler
                                                .getFrameLayoutEPGForSmallEvents()
                                                .getChildCount() == 0) {
                                    // get location of view
                                    int[] location = new int[2];
                                    btn.getLocationOnScreen(location);
                                    int hourMultiplier;
                                    if (epgHandler.NUMBER_OF_HOURS_IN_WINDOW == 2) {
                                        hourMultiplier = 1;
                                    } else {
                                        hourMultiplier = 4;
                                    }
                                    float fromYScale = (float) (btn.getHeight() / (float) ((epgHandler.epg_one_hour_height * hourMultiplier) / 2));
                                    // Log.d(TAG, "FROM Y SCALE: " +
                                    // fromYScale);
                                    // create new view
                                    FrameLayout item = (FrameLayout) epgHandler
                                            .getInflater().inflate(
                                                    R.layout.epg_normal_item,
                                                    null);
                                    android.widget.FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                            btn.getWidth(),
                                            (epgHandler.epg_one_hour_height * hourMultiplier) / 2);
                                    // Log.d(TAG, "FOCUS LISTENER location: "
                                    // + location[1] + " UPPER_WINDOW: "
                                    // + epgHandler.UPPER_WINDOW_HEIGHT);
                                    params.topMargin = location[1]
                                            - epgHandler.UPPER_WINDOW_HEIGHT;
                                    // params.leftMargin = location[0];
                                    params.gravity = Gravity.CENTER_HORIZONTAL;
                                    item.setLayoutParams(params);
                                    // take references of inflated view
                                    LinearLayout layoutForColor = (LinearLayout) item
                                            .findViewById(R.id.linearLayoutForEPGColor);
                                    final TextView tvEventName = (TextView) item
                                            .findViewById(R.id.textViewEPGEventName);
                                    TextView tvEventTime = (TextView) item
                                            .findViewById(R.id.textViewEPGEventTime);
                                    TextView tvEventDescription = (TextView) item
                                            .findViewById(R.id.textViewEPGEventDescription);
                                    Button btn = (Button) item
                                            .findViewById(R.id.buttonEPG);
                                    /** Fill views with data */
                                    btn.setFocusable(false);
                                    btn.setSelected(true);
                                    item.setTag(EPGEventFocusListener.this.btn
                                            .getTag());
                                    tvEventName.setText(event.getName());
                                    StringBuilder stringBuilder = new StringBuilder();
                                    tvEventTime.setText(stringBuilder
                                            .append(DateTimeConversions
                                                    .getTimeSting(event
                                                            .getStartTime()
                                                            .getCalendar()
                                                            .getTime()))
                                            .append(" - ")
                                            .append(DateTimeConversions
                                                    .getTimeSting(event
                                                            .getEndTime()
                                                            .getCalendar()
                                                            .getTime()))
                                            .toString());
                                    tvEventDescription.setText(event
                                            .getDescription());
                                    // set the same color to view
                                    epgHandler.setColorToEPGLayout(
                                            layoutForColor, Integer
                                                    .valueOf(event
                                                            .getStartTime()
                                                            .getHour()));
                                    // load animation
                                    ScaleAnimation zoom = new ScaleAnimation(1,
                                            1, fromYScale, 1,
                                            Animation.ABSOLUTE, 0.5f,
                                            Animation.ABSOLUTE, 0);
                                    zoom.setInterpolator(new AccelerateInterpolator());
                                    zoom.setDuration(epgHandler.TIME_TO_OPEN_SMALL_EVENT_ANIMATION);
                                    // add view and start animation
                                    try {
                                        epgHandler
                                                .getFrameLayoutEPGForSmallEvents()
                                                .addView(item);
                                        Log.d(TAG,
                                                "Add zoom view to FrameLayoutEPGForSmallEvents");
                                    } catch (RuntimeException e) {
                                        e.printStackTrace();
                                    }
                                    item.startAnimation(zoom);
                                } else {
                                    // epgHandler.setStarted(false);
                                    isStarted = false;
                                    handler = null;
                                    // Log.d(TAG,
                                    // "BOOLEAN ISSTARTED ELSE IN HANDLER: "
                                    // + isStarted);
                                }
                            }
                        }, epgHandler.TIME_TO_OPEN_SMALL_EVENT);
                    }
                }
            } else {
                epgHandler
                        .removeViewsFromFrameLayoutEPGForSmallEvents((Integer) v
                                .getTag());
                // if (isRemoved) {
                // new Handler().postDelayed(new Runnable() {
                //
                // @Override
                // public void run() {
                // isStarted = false;
                // Log.d(TAG,
                // "BOOLEAN ISSTARTED SECOND POST DELAYED: "
                // + isStarted);
                // handler = null;
                // }
                // }, epgHandler.TIME_TO_OPEN_SMALL_EVENT_ANIMATION);
                // } else {
                isStarted = false;
                handler = null;
                // Log.d(TAG, "BOOLEAN ISSTARTED LOST FOCUS: " + isStarted);
                // }
            }
        }
        /****************************************************************************************************************/
        // if it is for regular EPG event
        if (hasFocus) {
            tvEventName.setSelected(true);
        } else {
            tvEventName.setSelected(false);
        }
    }
}
