package com.iwedia.gui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iwedia.gui.components.A4TVToast;

public class DevicePolicyManager extends Activity {
    @SuppressWarnings("unused")
    private final String LOG_TAG = "DevicePolicyManager";
    private static final int ACTIVATION_REQUEST = 47; // identifies our request
    // id
    android.app.admin.DevicePolicyManager devicePolicyManager;
    private ComponentName demoDeviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicePolicyManager = (android.app.admin.DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        demoDeviceAdmin = new ComponentName(this, DemoDeviceAdminReceiver.class);
        Intent intent = new Intent(
                android.app.admin.DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(
                android.app.admin.DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                demoDeviceAdmin);
        intent.putExtra(
                android.app.admin.DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Your boss told you to do this");
        startActivityForResult(intent, ACTIVATION_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        MainActivity.service.getSystemControl().getAbout()
                                .factoryReset();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    devicePolicyManager.wipeData(ACTIVATION_REQUEST);
                } else {
                    A4TVToast toast = new A4TVToast(MainActivity.activity);
                    toast.showToast("Operation failed");
                    finish();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
