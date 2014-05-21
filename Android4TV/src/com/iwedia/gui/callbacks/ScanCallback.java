package com.iwedia.gui.callbacks;

import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IScanCallback;
import com.iwedia.comm.content.Content;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVProgressBar;
import com.iwedia.gui.components.dialogs.ChannelScanDialog;

public class ScanCallback extends IScanCallback.Stub {
    private static final String TAG = "SCAN CALLBACK";
    private MainActivity mActivity;
    private static ScanCallback sInstance = null;

    public ScanCallback(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void errorOccurred() throws RemoteException {
        mActivity.getDialogManager().getChannelScanDialog()
                .getHandlerScanFinished()
                .sendEmptyMessage(ChannelScanDialog.ERROR_OCCURED);
    }

    @Override
    public void installService(Content content) throws RemoteException {
        Log.e(TAG, content.getName());
        ChannelScanDialog dialog = mActivity.getDialogManager()
                .getChannelScanDialog();
        A4TVProgressBar progressScan = dialog.getProgressScan();
        if (progressScan != null) {
            // set content type picture
            switch (content.getServiceType()) {
                case DATA_BROADCAST: {
                    dialog.setNumberOfData(dialog.getNumberOfData() + 1);
                    break;
                }
                case DIG_TV: {
                    dialog.setNumberOfServices(dialog.getNumberOfServices() + 1);
                    break;
                }
                case DIG_RAD: {
                    dialog.setNumberOfRadio(dialog.getNumberOfRadio() + 1);
                    break;
                }
                default:
                    dialog.setNumberOfServices(dialog.getNumberOfServices() + 1);
                    break;
            }
            // send message to handler to draw this text
            mActivity.getDialogManager().getChannelScanDialog()
                    .getHandlerScanFinished()
                    .sendEmptyMessage(ChannelScanDialog.FOUND_SERVICE_FLAG);
            // synchronized (lock) {
            dialog.getContentsList().add(content);
            // }
            if (dialog.getContentsList().size() % 12 == 0) {
                if (dialog.getContentsList().size() > 0) {
                    // load items and add them to layout
                    // refreshAdapterData();
                    mActivity
                            .getDialogManager()
                            .getChannelScanDialog()
                            .getHandlerScanFinished()
                            .sendEmptyMessage(
                                    ChannelScanDialog.REFRESH_ADAPTER_FLAG);
                }
            }
        }
    }

    @Override
    public void noChannelsFound() throws RemoteException {
        mActivity.getDialogManager().getChannelScanDialog()
                .getHandlerScanFinished()
                .sendEmptyMessage(ChannelScanDialog.NO_SERVICES_FOUND);
    }

    @Override
    public void scanFinished() throws RemoteException {
        ChannelScanDialog dialog = mActivity.getDialogManager()
                .getChannelScanDialog();
        if (ChannelScanDialog.isScanning()) {
            if (dialog.getContentsList().size() > 0) {
                // load items and add them to layout
                // contentFound(content);
                // refreshAdapterData();
                dialog.getHandlerScanFinished().sendEmptyMessage(
                        ChannelScanDialog.REFRESH_ADAPTER_FLAG);
            }
            dialog.getHandlerScanFinished().sendEmptyMessage(
                    ChannelScanDialog.SCAN_FINISHED_FLAG);
            // Sync Channel Index
            mActivity.getPageCurl().getChannelChangeHandler()
                    .syncChannelIndex();
        }
    }

    @Override
    public void scanNoServiceSpace() throws RemoteException {
        mActivity.getDialogManager().getChannelScanDialog()
                .getHandlerScanFinished()
                .sendEmptyMessage(ChannelScanDialog.NO_SERVICE_SPACE);
    }

    @Override
    public void scanProgressChanged(int arg0) throws RemoteException {
        A4TVProgressBar progressScan = mActivity.getDialogManager()
                .getChannelScanDialog().getProgressScan();
        if (progressScan != null) {
            progressScan.setProgress(arg0);
        }
    }

    @Override
    public void scanTunFrequency(int frequency) throws RemoteException {
        Log.d(TAG, "scan progress scanTunFrequency " + frequency);
        Message.obtain(
                mActivity.getDialogManager().getChannelScanDialog()
                        .getHandlerScanFinished(),
                ChannelScanDialog.SCANNED_FREQUENCY, String.valueOf(frequency))
                .sendToTarget();
    }

    @Override
    public void signalBer(int arg0) throws RemoteException {
    }

    @Override
    public void signalQuality(int quality) throws RemoteException {
        Log.d(TAG, "scan progress signalQuality " + quality);
        A4TVProgressBar progress = mActivity.getDialogManager()
                .getChannelScanDialog().getProgressSignalQuality();
        if (progress != null) {
            progress.setProgress(quality);
        }
    }

    @Override
    public void signalStrength(int strength) throws RemoteException {
        Log.d(TAG, "scan progress signalStrength " + strength);
        A4TVProgressBar progress = mActivity.getDialogManager()
                .getChannelScanDialog().getProgressSignalStrength();
        if (progress != null) {
            progress.setProgress(strength);
        }
    }
}
