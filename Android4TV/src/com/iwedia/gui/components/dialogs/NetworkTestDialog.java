package com.iwedia.gui.components.dialogs;

import android.content.Context;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.iwedia.comm.enums.NetworkType;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.R;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.A4TVTextView;

/**
 * Network test dialog
 * 
 * @author Branimir Pavlovic
 */
public class NetworkTestDialog extends A4TVAlertDialog implements
        OnSeekBarChangeListener {
    private A4TVProgressBar networkTestProgress;
    private A4TVTextView textViewNetworkTestDownloadSpeed,
            textViewNetworkTestNetworkType;
    private Context ctx;

    public NetworkTestDialog(Context context) {
        super(context);
        ctx = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.network_test_layout, null);
        setView(view);
        init();
        setCancelable(true);
    }

    @Override
    public void show() {
        int networkType;
        try {
            // Get active network type
            networkType = MainActivity.service.getSystemControl()
                    .getNetworkControl().getActiveNetworkType();
            textViewNetworkTestNetworkType
                    .setText(networkTypeToString(networkType));
            // Get network speed
            String networkSpeed = MainActivity.service.getSystemControl()
                    .getNetworkControl().getLinkSpeed();
            textViewNetworkTestDownloadSpeed.setText(networkSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.show();
    }

    private void init() {
        // init views
        textViewNetworkTestDownloadSpeed = (A4TVTextView) findViewById(R.id.aTVTextViewNetworkTestDownloadSpeed);
        textViewNetworkTestNetworkType = (A4TVTextView) findViewById(R.id.aTVTextViewNetworkTestNetworkType);
        // set title
        setTitleOfAlertDialog(R.string.tv_menu_network_settings_network_connection_test);
        // set button
        setPositiveButton(R.string.close,
                new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetworkTestDialog.this.cancel();
                    }
                });
    }

    private String networkTypeToString(int networkType) {
        String returnString;
        switch (networkType) {
            case NetworkType.ETHERNET: {
                returnString = ctx.getString(R.string.button_text_ethernet);
                break;
            }
            case NetworkType.WIRELESS: {
                returnString = ctx
                        .getString(R.string.tv_menu_network_settings_wireless);
                break;
            }
            default: {
                returnString = ctx.getString(R.string.unknown);
                break;
            }
        }
        return returnString;
    }

    public void testFinished() throws RemoteException {
    }

    public void progressChanged(int value) throws RemoteException {
    }

    public void networkTypeChanged(int type) throws RemoteException {
        textViewNetworkTestNetworkType.setText(networkTypeToString(type));
    }

    public void downloadSpeed(double speed) throws RemoteException {
    }

    public void connectionTimeChanged(int time) throws RemoteException {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
