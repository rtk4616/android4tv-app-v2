package com.iwedia.gui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.iwedia.gui.MainActivity;

public class A4TVMultimediaVideoView extends VideoView {
    public static final String LOG_TAG = "A4TVMultimediaVideoView";
    public int videoWidth, videoHeight;
    public int windowWidth, windowHeight, windowX, windowY;

    @Override
    public void stopPlayback() {
        Log.e("VideoView", LOG_TAG
                + "***********************************  stopPlayback");
        super.stopPlayback();
    }

    @Override
    public void start() {
        Log.e("VideoView", LOG_TAG
                + "***********************************  start");
        super.start();
    }

    private void setVideoActiveRectangle() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) this
                .getLayoutParams();
        int x, y, width, height;
        float ratioX, ratioY, ratioRef;
        ratioX = (float) windowWidth / (float) videoWidth;
        ratioY = (float) windowHeight / (float) videoHeight;
        if (ratioX > ratioY) {
            ratioRef = ratioY;
        } else {
            ratioRef = ratioX;
        }
        x = windowX + (windowWidth - (int) (ratioRef * (float) videoWidth)) / 2;
        y = windowY + (windowHeight - (int) (ratioRef * (float) videoHeight))
                / 2;
        width = (int) (ratioRef * (float) videoWidth);
        height = (int) (ratioRef * (float) videoHeight);
        Log.i(LOG_TAG, " setVideoActiveRectangle:  x[" + x + "] y[" + y
                + "] width[" + width + "] height[" + height + "]");
        params.width = width;
        params.height = height;
        setX(x);
        setY(y);
        setLayoutParams(params);
        getParent().requestLayout();
        invalidate();
    }

    public A4TVMultimediaVideoView(Context context) {
        super(context);
    }

    public A4TVMultimediaVideoView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public A4TVMultimediaVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void setVideoSize(int width, int height) {
        this.videoWidth = width;
        this.videoHeight = height;
    }

    public void updateWidow() {
        if ((this.videoWidth != 0) && (this.videoHeight != 0)) {
            setVideoActiveRectangle();
        }
    }

    public void setScaling(int x, int y, int width, int height) {
        Log.i(LOG_TAG, " setScaling:  x[" + x + "] y[" + y + "] width[" + width
                + "] height[" + height + "]");
        Log.i(LOG_TAG, " setScaling:  width[" + this.videoWidth + "] height["
                + this.videoHeight + "]");
        this.windowX = x;
        this.windowY = y;
        this.windowWidth = width;
        this.windowHeight = height;
        if ((this.videoWidth != 0) && (this.videoHeight != 0)) {
            setVideoActiveRectangle();
        }
    }

    public void updateVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    /** Scale VideoView to pip coordinates */
    public void gotoPIP() {
        Log.d(LOG_TAG, "gotoPIP");
        MainActivity.activity.updatePIPCoordinates();
        updateVisibility(View.VISIBLE);
        setScaling(MainActivity.pipWindowCoordinateLeft,
                MainActivity.pipWindowCoordinateTop,
                MainActivity.pipWindowWidth, MainActivity.pipWindowHeight);
    }

    /** Scale VideoView to pap coordinates */
    public void gotoPaP(int displayID) {
        /**
         * deppending on display id scale will be on the left or right side of
         * the screen
         */
        Log.d(LOG_TAG, "gotoPaP - display id:" + displayID);
        if (displayID == MainActivity.PRIMARY_DISPLAY_UNIT_ID) {
            updateVisibility(View.VISIBLE);
            setScaling(0, 0, 960, 1080);
        } else if (displayID == MainActivity.SECONDARY_DISPLAY_UNIT_ID) {
            updateVisibility(View.VISIBLE);
            setScaling(960, 0, 960, 1080);
        }
    }

    /** Scale VideoView to full screen */
    public void gotoFullScreen() {
        Log.d(LOG_TAG, "gotoFullScreen");
        updateVisibility(View.VISIBLE);
        setScaling(0, 0, 1920, 1080);
    }

    /** Scale to zero and hide */
    public void hide() {
        Log.d(LOG_TAG, "hide");
        // setScaling(0, 0, 0, 0);
        updateVisibility(View.INVISIBLE);
    }
}
