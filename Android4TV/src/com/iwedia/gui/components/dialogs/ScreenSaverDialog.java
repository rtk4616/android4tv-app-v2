package com.iwedia.gui.components.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.config_handler.ConfigHandler;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenSaverDialog extends Dialog {
    private final static String TAG = "ScreenSaver";
    public static Timer timer;
    public static boolean screenSaverStarted = false;
    /** Possible states for screen saver */
    public final int LIVE = 0;
    public final int RADIO = 1;
    public final int NO_SIGNAL = 2;
    public final int PAUSE = 3;
    public int screenSaverCause = 0;
    /**
     * Activation times for screen saver or store mode video presentation
     * (milliseconds)
     */
    public int TIME_LIVE_TV = 5 * 60 * 60 * 1000; /* 5 hours */
    public int TIME_STORE_MODE_VIDEO_PRESENTATION = 5 * 60 * 1000; /* 5 minutes */
    public int TIME_DEFAULT = 10 * 60 * 1000; /* 10 minutes */

    public interface SomeKeyEntered {
        public void keyEntered(Dialog dialog);
    }

    private SomeKeyEntered someKeyEntered;
    private ImageView screenSaverImage;
    private VideoView storeModeVideoPresentation;
    private boolean storeModeVideoPresentationExists = false;
    private TextView storeModeVideoPresentationText;
    private String path;

    public ScreenSaverDialog(Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.screensaver_layout);
        setCancelable(true);
        /* Prepare screen saver image */
        screenSaverImage = (ImageView) findViewById(R.id.imageScreensaver);
        String imageName = ConfigHandler.SCREENSAVER_IMAGE;
        int id = MainActivity.activity.getResources().getIdentifier(imageName,
                "drawable", MainActivity.activity.getPackageName());
        screenSaverImage.setImageResource(id);
        /* Prepare store mode video */
        String videoName = ConfigHandler.STORE_MODE_VIDEO;
        int idVideo = MainActivity.activity.getResources().getIdentifier(
                videoName, "raw", MainActivity.activity.getPackageName());
        path = "android.resource://" + MainActivity.activity.getPackageName()
                + "/" + idVideo;
        // path = "/data/data/storeModeVideoPresentation/" + videoName;
        storeModeVideoPresentationText = (TextView) findViewById(R.id.textStoreModeVideoNotExist);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (someKeyEntered != null) {
            someKeyEntered.keyEntered(ScreenSaverDialog.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public SomeKeyEntered getSomeKeyEntered() {
        return someKeyEntered;
    }

    public void setSomeKeyEntered(SomeKeyEntered someKeyEntered) {
        this.someKeyEntered = someKeyEntered;
    }

    public ImageView getScreenSaverImage() {
        return screenSaverImage;
    }

    public void setScreenSaverCause(int cause) {
        screenSaverCause = cause;
    }

    public int getScreenSaverTime() {
        int screenSaverTime;
        /* Check is store mode enabled */
        boolean isStoreModeEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.STORE_MODE_START, false);
        if (isStoreModeEnabled) {
            /* Check is video presentation enabled */
            final boolean isVideoPresentationEnabled = MainActivity.sharedPrefs
                    .getBoolean(MainActivity.STORE_MODE_VIDEO_PRESENTATION,
                            true);
            if (isVideoPresentationEnabled) {
                /* 5 minutes */
                screenSaverTime = TIME_STORE_MODE_VIDEO_PRESENTATION;
            } else {
                /* Video should not be started */
                return -1;
            }
        } else {
            /* Check is screen saver enabled */
            boolean isScreenSaverEnabled = MainActivity.sharedPrefs.getBoolean(
                    MainActivity.SCREENSAVER_ENABLED, true);
            if (!isScreenSaverEnabled) {
                /* Screen saver should not be started */
                return -1;
            }
            if (screenSaverCause == NO_SIGNAL) {
                /*
                 * Get stored screen saver start time in milliseconds
                 */
                screenSaverTime = MainActivity.sharedPrefs
                        .getInt(MainActivity.SCREENSAVER_TIME_MILISECONDS,
                                TIME_DEFAULT);
            } else if (screenSaverCause == RADIO) {
                /*
                 * Get stored screen saver start time in milliseconds
                 */
                screenSaverTime = MainActivity.sharedPrefs
                        .getInt(MainActivity.SCREENSAVER_TIME_MILISECONDS,
                                TIME_DEFAULT);
            } else if (screenSaverCause == LIVE) {
                /* 5 hours */
                screenSaverTime = TIME_LIVE_TV;
            } else {
                /*
                 * Get stored screen saver start time in milliseconds
                 */
                screenSaverTime = MainActivity.sharedPrefs
                        .getInt(MainActivity.SCREENSAVER_TIME_MILISECONDS,
                                TIME_DEFAULT);
            }
        }
        return screenSaverTime;
    }

    public void startScreensaverTimer() {
        int screenSaverTime;
        final boolean isStoreModeEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.STORE_MODE_START, false);
        screenSaverTime = getScreenSaverTime();
        // Log.e(TAG, "startScreensaverTimer time = " + screenSaverTime);
        if (screenSaverTime == -1) {
            return;
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "Start screensaver");
                MainActivity.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isStoreModeEnabled) {
                            startStoreModeVideoPresentation();
                            screenSaverStarted = true;
                        } else {
                            startScreensaver();
                            screenSaverStarted = true;
                        }
                    }
                });
            }
        };
        // Log.e(TAG, "TIMER   startScreensaverTimer\n ");
        timer = new Timer();
        timer.schedule(timerTask, screenSaverTime);
    }

    public void updateScreensaverTimer() {
        int screenSaverTime;
        final boolean isStoreModeEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.STORE_MODE_START, false);
        screenSaverTime = getScreenSaverTime();
        // Log.e(TAG, "updateScreensaverTimer time = " + screenSaverTime);
        if (screenSaverTime == -1) {
            if (timer != null) {
                timer.cancel();
            }
            return;
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "Start screensaver\n ");
                MainActivity.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (isStoreModeEnabled) {
                            startStoreModeVideoPresentation();
                            screenSaverStarted = true;
                        } else {
                            startScreensaver();
                            screenSaverStarted = true;
                        }
                    }
                });
            }
        };
        // Log.e(TAG, "TIMER   updateScreenSaverTimer\n ");
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(timerTask, screenSaverTime);
    }

    public void stopScreensaverTimer() {
        boolean isStoreModeEnabled = MainActivity.sharedPrefs.getBoolean(
                MainActivity.STORE_MODE_START, false);
        if (screenSaverStarted) {
            screenSaverStarted = false;
            if (isStoreModeEnabled) {
                if (storeModeVideoPresentationExists) {
                    storeModeVideoPresentation.stopPlayback();
                    storeModeVideoPresentation.setVisibility(View.GONE);
                } else {
                    storeModeVideoPresentationText.setVisibility(View.GONE);
                }
            } else {
                if (screenSaverImage.getAnimation() != null) {
                    screenSaverImage.clearAnimation();
                    screenSaverImage.setVisibility(View.GONE);
                }
            }
            this.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
        // Log.e(TAG, "TIMER   stopScreenSaverTimer\n ");
    }

    public void stopScreensaver() {
        /* Run screen saver timer */
        if (screenSaverStarted) {
            screenSaverStarted = false;
            if (screenSaverImage.getAnimation() != null) {
                screenSaverImage.clearAnimation();
                screenSaverImage.setVisibility(View.GONE);
                ScreenSaverDialog.this.cancel();
            }
            startScreensaverTimer();
        } else {
            updateScreensaverTimer();
        }
    }

    public void startScreensaver() {
        setSomeKeyEntered(new SomeKeyEntered() {
            @Override
            public void keyEntered(Dialog dialog) {
                /* Run screen saver timer */
                if (screenSaverStarted) {
                    screenSaverStarted = false;
                    if (dialog != null) {
                        if (screenSaverImage.getAnimation() != null) {
                            screenSaverImage.clearAnimation();
                            screenSaverImage.setVisibility(View.GONE);
                            dialog.cancel();
                        }
                    }
                    startScreensaverTimer();
                } else {
                    updateScreensaverTimer();
                }
            }
        });
        Animation animation = AnimationUtils.loadAnimation(
                MainActivity.activity, R.anim.screensaver);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (screenSaverStarted == true) {
                    getScreenSaverImage().startAnimation(animation);
                }
            }
        });
        this.show();
        // workaround to show black background
        storeModeVideoPresentation = (VideoView) findViewById(R.id.storeModeVideo);
        storeModeVideoPresentation.setVisibility(View.VISIBLE);
        storeModeVideoPresentation.setVisibility(View.GONE);
        screenSaverImage.setVisibility(View.VISIBLE);
        if (storeModeVideoPresentationExists) {
            storeModeVideoPresentation.setVisibility(View.GONE);
        }
        storeModeVideoPresentationText.setVisibility(View.GONE);
        screenSaverImage.startAnimation(animation);
    }

    void startStoreModeVideoPresentation() {
        /* Check if video exists */
        File file = new File(path);
        // if(file.exists()) {
        storeModeVideoPresentationExists = true;
        // path = MainActivity.activity.getFilesDir().getPath() +
        // "/storeModeVideoPresentation/" + videoName;
        storeModeVideoPresentation = (VideoView) findViewById(R.id.storeModeVideo);
        Uri uri = Uri.parse(path);
        storeModeVideoPresentation.setVideoURI(uri);
        // Log.e(TAG, "Video exists" + path);
        // } else {
        // storeModeVideoPresentationExists = true; //false; workaround, because
        // file is played from raw
        // Log.e(TAG, "Video does not exist");
        // }
        setSomeKeyEntered(new SomeKeyEntered() {
            @Override
            public void keyEntered(Dialog dialog) {
                /* Run screen saver timer */
                if (screenSaverStarted) {
                    screenSaverStarted = false;
                    MainActivity.activity.getPrimaryVideoView().start();
                    if (dialog != null) {
                        if (storeModeVideoPresentationExists) {
                            storeModeVideoPresentation.stopPlayback();
                            storeModeVideoPresentation.setVisibility(View.GONE);
                        } else {
                            storeModeVideoPresentationText
                                    .setVisibility(View.GONE);
                        }
                        dialog.cancel();
                    }
                    startScreensaverTimer();
                } else {
                    updateScreensaverTimer();
                }
            }
        });
        this.show();
        /* Show video if exists */
        if (storeModeVideoPresentationExists) {
            storeModeVideoPresentation.setVisibility(View.VISIBLE);
            storeModeVideoPresentationText.setVisibility(View.GONE);
            screenSaverImage.setVisibility(View.GONE);
            storeModeVideoPresentation.start();
            storeModeVideoPresentation
                    .setOnPreparedListener(new OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                        }
                    });
        } else { /* if video does not exist show text */
            // workaround to show black background
            storeModeVideoPresentation = (VideoView) findViewById(R.id.storeModeVideo);
            storeModeVideoPresentation.setVisibility(View.VISIBLE);
            storeModeVideoPresentation.setVisibility(View.GONE);
            screenSaverImage.setVisibility(View.GONE);
            // storeModeVideoPresentation.setVisibility(View.VISIBLE);
            storeModeVideoPresentationText.setVisibility(View.VISIBLE);
        }
        MainActivity.activity.getPrimaryVideoView().pause();
        /* restore to default all settings */
        try {
            /* Save first time install value */
            boolean isFirstTimeInstall = MainActivity.sharedPrefs.getBoolean(
                    MainActivity.FIRST_TIME_INSTALL, true);
            Editor editor = MainActivity.activity.getSharedPreferences(
                    "myPrefs", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
            MainActivity.service.getSetupControl().resetSettingsInStoreMode();
            MainActivity.sharedPrefs.edit()
                    .putBoolean(MainActivity.STORE_MODE_START, true).commit();
            MainActivity.sharedPrefs
                    .edit()
                    .putBoolean(MainActivity.FIRST_TIME_INSTALL,
                            isFirstTimeInstall).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
