package com.iwedia.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.iwedia.comm.IDTVManagerProxy;
import com.iwedia.gui.ci.CICallbackController;
import com.iwedia.gui.components.dialogs.CICamInfoDialog;
import com.iwedia.gui.components.dialogs.CIInfoDialog;

public class CIDialogActivity extends Activity {
    public static final String TAG = "CIDialogActivity";
    private CIInfoDialog mInfoDialog = null;
    private CICamInfoDialog mCamInfoDialog = null;
    private IDTVManagerProxy mService = null;
    private CICallbackController ciCallbackController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + this.getPackageName());
        connectWithServer();
    }

    private void init() {
        MainActivity.initDialogDimensions(this);
        mInfoDialog = new CIInfoDialog(this);
        mInfoDialog.setActivity(this);
        mInfoDialog.showDialog(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        try {
            mService.getCIControl().unregisterCallback(
                    ciCallbackController.getCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mInfoDialog.cancel();
        mInfoDialog = null;
        unbindService(mServiceConnection);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed");
        finish();
    }

    public void connectWithServer() {
        Intent i = new Intent("com.iwedia.PROXY_SERVICE");
        boolean isBound = bindService(i, mServiceConnection,
                Context.BIND_AUTO_CREATE);
        Log.e(TAG, "Bind to service in onResume(), isBinded: " + isBound);
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName component, IBinder binder) {
            Log.d(TAG, "onServiceConnected");
            init();
            mService = IDTVManagerProxy.Stub.asInterface((IBinder) binder);
            try {
                ciCallbackController = new CICallbackController(
                        CIDialogActivity.this);
                ciCallbackController.setInfoDialog(mInfoDialog);
                ciCallbackController.setCamInfoDialog(mCamInfoDialog);
                mService.getCIControl().registerCallback(
                        ciCallbackController.getCallback());
                mInfoDialog.setProxyService(mService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName component) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}
