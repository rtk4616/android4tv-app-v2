package com.iwedia.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Class that receives internet state change event
 * 
 * @author Branimir Pavlovic
 */
public class InternetStateChangeReceiver extends BroadcastReceiver {
    private final String LOG_TAG = "InternetStateChangeReceiver";
    String str;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(MainActivity.TAG, "Network connectivity change");
        /*
         * str = ""; if (intent.getExtras() != null) { NetworkInfo ni =
         * (NetworkInfo) intent.getExtras().get(
         * ConnectivityManager.EXTRA_NETWORK_INFO); if (ni != null &&
         * ni.getState() == NetworkInfo.State.CONNECTED) { str = "Connected";
         * Log.d(LOG_TAG, "Network " + ni.getTypeName() + " connected"); if
         * (MainActivity.activity != null) { A4TVToast toast = new
         * A4TVToast(MainActivity.activity);
         * toast.showToast("Network is now connected!!"); } } if (ni != null &&
         * ni.getState() == NetworkInfo.State.DISCONNECTED) { str =
         * "Disconnected"; Log.d(LOG_TAG, "Network " + ni.getTypeName() +
         * " disconnected"); } } if (intent.getExtras().getBoolean(
         * ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) { str =
         * "Disconnected"; Log.d(LOG_TAG, "There's no network connectivity"); if
         * (MainActivity.activity != null) { A4TVToast toast = new
         * A4TVToast(MainActivity.activity);
         * toast.showToast("Network is now disconnected!!"); } }
         */
        // if (MainActivity.activity != null) {
        // MainActivity.activity.runOnUiThread(new Runnable() {
        //
        // @Override
        // public void run() {
        // Toast.makeText(MainActivity.activity, str,
        // Toast.LENGTH_SHORT).show();
        // }
        // });
        // }
    }
}
