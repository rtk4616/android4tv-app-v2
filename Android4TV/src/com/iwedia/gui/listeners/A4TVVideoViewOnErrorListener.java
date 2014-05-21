package com.iwedia.gui.listeners;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.config_handler.ConfigHandler;

public class A4TVVideoViewOnErrorListener implements OnErrorListener {
    private int MEDIA_ERROR_SERVER_DIED = 100;
    private final String TAG = "A4TVVideoViewOnErrorListener";

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "VideoView OnError");
        if (what == MEDIA_ERROR_SERVER_DIED) {
            Log.i(TAG, "MediaServerDied");
            A4TVVideoViewOnCompletionListener.setVideoViewError(true);
            Toast.makeText(MainActivity.activity,
                    R.string.dlna_media_server_died, Toast.LENGTH_SHORT).show();
            if (ConfigHandler.TVPLATFORM) {
                try {
                    MainActivity.service
                            .getSystemControl()
                            .getApplicationRestart()
                            .binderRestart(
                                    MainActivity.activity.getComponentName()
                                            .getClassName());
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                MainActivity.activity.disconnectFromService();
                MainActivity.activity.finish();
            }
        } else {
            A4TVVideoViewOnCompletionListener.setVideoViewError(true);
//            Toast.makeText(MainActivity.activity,
//                    R.string.dlna_multimedia_decoding_error, Toast.LENGTH_SHORT)
//                    .show();
        }
        return true;
    }
}
