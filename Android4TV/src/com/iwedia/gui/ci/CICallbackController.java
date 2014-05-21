package com.iwedia.gui.ci;

import android.app.Activity;
import android.os.RemoteException;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.iwedia.comm.ICICallback;
import com.iwedia.dtv.ci.EnquiryData;
import com.iwedia.gui.MainActivity;
import com.iwedia.gui.components.A4TVAlertDialog;
import com.iwedia.gui.components.A4TVEditText;
import com.iwedia.gui.components.A4TVToast;
import com.iwedia.gui.components.dialogs.CICamInfoDialog;
import com.iwedia.gui.components.dialogs.CIInfoDialog;

public class CICallbackController {
    public static final String TAG = "CICallbackController";
    private static final int CI_MAX_TOTAL_CAM = 2;
    private Activity activity = null;
    private CIInfoDialog ciInfoDialog = null;
    private CICamInfoDialog ciCamDialog = null;
    private int totalCAM = 0;

    public CICallbackController(Activity a) {
        activity = a;
    }

    public void setInfoDialog(CIInfoDialog dlg) {
        ciInfoDialog = dlg;
    }

    public void setCamInfoDialog(CICamInfoDialog dlg) {
        ciCamDialog = dlg;
    }

    public int getTotalCam() {
        return totalCAM;
    }

    public ICICallback getCallback() {
        return ciCallBack;
    }

    private ICICallback ciCallBack = new ICICallback.Stub() {
        @Override
        public void dialogEnquiry(final int ssnb) throws RemoteException {
            Log.d(TAG, "CI CALLBACK dialogEnquiry - ssnb = " + ssnb);
            final EnquiryData enquiryData = MainActivity.service.getCIControl()
                    .getEnquiryText(ssnb);
            Log.i(TAG, "dialogEnquiry - question: " + enquiryData.getText());
            Log.i(TAG, "dialogEnquiry - isBlind: " + enquiryData.getIsBlind());
            Log.i(TAG,
                    "dialogEnquiry - answerSize: "
                            + enquiryData.getAnswerSize());
            MainActivity.activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final A4TVAlertDialog askDialog = new A4TVAlertDialog(
                            MainActivity.activity);
                    askDialog.setTitleOfAlertDialog(enquiryData.getText())
                            .setCancelable(true);
                    final A4TVEditText editText = new A4TVEditText(askDialog
                            .getContext());
                    editText.setEms(enquiryData.getAnswerSize());
                    if (enquiryData.getIsBlind() != 0) {
                        editText.setTransformationMethod(new PasswordTransformationMethod());
                    }
                    askDialog.setView(editText);
                    // Show virtual keyboard
                    // InputMethodManager imm = (InputMethodManager)
                    // editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    editText.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            Log.d(TAG, "keyCode is: " + keyCode);
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                switch (keyCode) {
                                    case KeyEvent.KEYCODE_DPAD_CENTER:
                                    case KeyEvent.KEYCODE_ENTER: {
                                        try {
                                            MainActivity.service
                                                    .getCIControl()
                                                    .answer(ssnb,
                                                            editText.getText()
                                                                    .toString(),
                                                            0);
                                            // Hide virtual keyboard
                                            // InputMethodManager imm =
                                            // (InputMethodManager)
                                            // askDialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            // imm.hideSoftInputFromWindow(editText.getWindowToken(),
                                            // 0);
                                            askDialog.cancel();
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        return false;
                                    }
                                    case KeyEvent.KEYCODE_BACK:
                                        try {
                                            MainActivity.service.getCIControl()
                                                    .answer(ssnb, "", 1);
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        askDialog.cancel();
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                            return false;
                        }
                    });
                    askDialog.show();
                }
            });
        }

        @Override
        public void dialogLabel() throws RemoteException {
            Log.d(TAG, "CI CALLBACK dialogLabel - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void dialogList(int ssnb) throws RemoteException {
            Log.d(TAG, "CI CALLBACK dialogList - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void dialogMenu(final int ssnb) throws RemoteException {
            Log.d(TAG, "CI CALLBACK dialogMenu");
            if (0 != (MainActivity.getKeySet())) {
                int command = 0;
                String param = "EXIT";
                try {
                    MainActivity.service.getHbbTvControl().notifyAppMngr(
                            command, param);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ciInfoDialog != null) {
                        ciInfoDialog.showDialog(ssnb);
                    }
                }
            });
        }

        @Override
        public void dialogNone() throws RemoteException {
            Log.d(TAG, "CI CALLBACK dialogNone");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ciInfoDialog != null) {
                        ciInfoDialog.cancelDialog();
                    }
                }
            });
        }

        @Override
        public void dialogRequested() throws RemoteException {
            Log.d(TAG, "CI CALLBACK dialogRequested - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void invalideCertificate() throws RemoteException {
            Log.d(TAG, "CI CALLBACK invalideCertificate - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void moduleInserted() throws RemoteException {
            Log.d(TAG, "CI CALLBACK moduleInserted");
            if (0 != (MainActivity.getKeySet())) {
                int command = 0;
                String param = "EXIT";
                try {
                    MainActivity.service.getHbbTvControl().notifyAppMngr(
                            command, param);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            totalCAM++;
            if (totalCAM > CI_MAX_TOTAL_CAM) {
                totalCAM = CI_MAX_TOTAL_CAM;
            }
        }

        @Override
        public void moduleRemoved() throws RemoteException {
            Log.d(TAG, "CI CALLBACK moduleRemoved");
            totalCAM--;
            if (totalCAM < 0) {
                totalCAM = 0;
            }
        }

        @Override
        public void noCamOnScrambled() throws RemoteException {
            Log.d(TAG, "CI CALLBACK noCamOnScrambled");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new A4TVToast(activity)
                            .showToast(activity.getResources().getString(
                                    com.iwedia.gui.R.string.ci_cam_not_present));
                }
            });
        }

        @Override
        public void opNotifyLabel() throws RemoteException {
            Log.d(TAG, "CI CALLBACK opNotifyLabel");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new A4TVToast(activity).showToast(activity.getResources()
                            .getString(
                                    com.iwedia.gui.R.string.ci_oprofile_label));
                }
            });
        }

        @Override
        public void opNotifyQuestionLabel() throws RemoteException {
            Log.d(TAG, "CI CALLBACK opNotifyQuestionLabel - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void opProfileInstallFinished() throws RemoteException {
            Log.d(TAG, "CI CALLBACK opProfileInstallFinished");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new A4TVToast(activity)
                            .showToast(activity
                                    .getResources()
                                    .getString(
                                            com.iwedia.gui.R.string.ci_oprofile_install_finished));
                    try {
                        if (MainActivity.activity.getContentListHandler() == null) {
                            MainActivity.activity.initContentList();
                        }
                        MainActivity.activity.getContentListHandler()
                                .reinitFilterOptionArray();
                        MainActivity.service.getContentListControl()
                                .refreshServiceLists();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void opProfileInstallStarted() throws RemoteException {
            Log.d(TAG, "CI CALLBACK opProfileInstallStarted");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new A4TVToast(activity)
                            .showToast(activity
                                    .getResources()
                                    .getString(
                                            com.iwedia.gui.R.string.ci_oprofile_install_started));
                }
            });
        }

        @Override
        public void opProfileNameChanged() throws RemoteException {
            Log.d(TAG, "CI CALLBACK opProfileNameChanged - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void sessionStatus() throws RemoteException {
            Log.d(TAG, "CI CALLBACK sessionStatus - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void statusClosed() throws RemoteException {
            Log.d(TAG, "CI CALLBACK statusClosed - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void statusOpened() throws RemoteException {
            Log.d(TAG, "CI CALLBACK statusOpened - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void undefined() throws RemoteException {
            Log.d(TAG, "CI CALLBACK undefined - NOT IMPLEMENTED");
            // TODO Auto-generated method stub
        }

        @Override
        public void updateApplications() throws RemoteException {
            Log.d(TAG, "CI CALLBACK updateApplications");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ciCamDialog != null && ciCamDialog.isShowing()) {
                        ciCamDialog.loadCAMApplicationInfo();
                    }
                }
            });
        }
    };
}
