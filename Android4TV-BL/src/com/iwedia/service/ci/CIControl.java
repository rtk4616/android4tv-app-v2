package com.iwedia.service.ci;

import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.iwedia.comm.ICICallback;
import com.iwedia.comm.ICIControl;
import com.iwedia.dtv.ci.ApplicationInfo;
import com.iwedia.dtv.ci.EnquiryData;
import com.iwedia.dtv.ci.OperatorProfileInfo;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.R;
import com.iwedia.service.proxyservice.IDTVInterface;

public class CIControl extends ICIControl.Stub implements IDTVInterface {
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "CIControl";
    private static com.iwedia.comm.ICICallback ciCallback;
    public static final int CI_REMOVED = 0;
    public static final int CI_INSERTED = 1;
    public static final int CI_INVALID_HOST_CERTIFICATE = 2;
    public static final int CI_MAX_TOTAL_CAM = 2;

    @Override
    public void open(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "open slotNumber: " + slotNumber);
        }
        try {
            IWEDIAService.getInstance().getDTVManager().getCIControl()
                    .open(slotNumber);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.e(LOG_TAG, "close (" + slotNumber + ")");
        }
        try {
            IWEDIAService.getInstance().getDTVManager().getCIControl()
                    .close(slotNumber);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void answer(int slotNumber, String answer, int cancel)
            throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "answer: " + slotNumber + ", " + answer);
        }
        IWEDIAService.getInstance().getDTVManager().getCIControl()
                .answer(slotNumber, answer, cancel);
    }

    @Override
    public void selectMenuItem(int slotNumber, int choice)
            throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "selectMenuItem: " + slotNumber + ", " + choice);
        }
        IWEDIAService.getInstance().getDTVManager().getCIControl()
                .selectMenuItem(slotNumber, choice);
    }

    @Override
    public String getTitle(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getTitle: " + slotNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getTitle(slotNumber);
    }

    @Override
    public String getTopText(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getTopText: " + slotNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getTopText(slotNumber);
    }

    @Override
    public String getBottomText(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getBottomText: " + slotNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getBottomText(slotNumber);
    }

    @Override
    public String getMenuItemText(int slotNumber, int itemNumber)
            throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getMenuItemText: " + slotNumber + " " + itemNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getMenuItemText(slotNumber, itemNumber);
    }

    @Override
    public String getListItemText(int slotNumber, int itemNumber)
            throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getListItemText: " + slotNumber + " " + itemNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getListItemText(slotNumber, itemNumber);
    }

    @Override
    public EnquiryData getEnquiryText(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getEnquiryText: " + slotNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getEnquiryText(slotNumber);
    }

    @Override
    public int getNumberOfItems(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getNumberOfItems: " + slotNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getNumberOfItems(slotNumber);
    }

    @Override
    public String getLanguage(int slotNumber) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getLanguage: " + slotNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getLanguage(slotNumber);
    }

    @Override
    public void setLanguage(int slotNumber, String language)
            throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "setLanguage: " + slotNumber + " " + language);
        }
        IWEDIAService.getInstance().getDTVManager().getCIControl()
                .setLanguage(slotNumber, language);
    }

    @Override
    public void installOperatorProfile(int liveRouteID, int slotNumber)
            throws RemoteException {
        if (DEBUG)
            Log.d(LOG_TAG, "installOperatorProfile: " + liveRouteID + " "
                    + slotNumber);
        IWEDIAService.getInstance().getDTVManager().getCIControl()
                .installOperatorProfile(liveRouteID, slotNumber);
    }

    @Override
    public int getOperatorProfileCount() throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getOperatorProfileCount");
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getOperatorProfileCount();
    }

    @Override
    public OperatorProfileInfo getOperatorProfileInfo(int profileId)
            throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getOperatorProfileInfo: " + profileId);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getOperatorProfileInfo(profileId);
    }

    @Override
    public void removeOperatorProfile(int profileId) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "removeOperatorProfile: " + profileId);
        }
        try {
            IWEDIAService.getInstance().getDTVManager().getCIControl()
                    .removeOperatorProfile(profileId);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNumberOfApplications() throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getNumberOfApplications");
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getNumberOfApplications();
    }

    @Override
    public ApplicationInfo getApplicationInfo(int appNumber)
            throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getApplicationInfo appNumber: " + appNumber);
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getApplicationInfo(appNumber);
    }

    @Override
    public void exitOperatorProfile() throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "exitOperatorProfile ");
        }
        IWEDIAService.getInstance().getDTVManager().getCIControl()
                .exitOperatorProfile();
    }

    @Override
    public void operatorProfileUserReply(int reply) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "operatorProfileUserReply - reply: " + reply);
        }
        IWEDIAService.getInstance().getDTVManager().getCIControl()
                .operatorProfileUserReply(reply);
    }

    @Override
    public void enterOperatorProfile(int listIndex) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "enterOperatorProfile - index: " + listIndex);
        }
        IWEDIAService.getInstance().getDTVManager().getCIControl()
                .operatorProfileUserReply(listIndex);
    }

    @Override
    public void setPin(int pin) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "setPin: " + pin);
        }
        IWEDIAService.getInstance().getDTVManager().getCIControl().setPin(pin);
    }

    @Override
    public int getPin() throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "getPin");
        }
        return IWEDIAService.getInstance().getDTVManager().getCIControl()
                .getPin();
    }

    @Override
    public void registerCallback(ICICallback callback) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "registerCallback");
        }
        ciCallback = callback;
    }

    @Override
    public void unregisterCallback(ICICallback callback) throws RemoteException {
        if (DEBUG) {
            Log.d(LOG_TAG, "unregisterCallback");
        }
        ciCallback = null;
    }

    public static com.iwedia.dtv.ci.ICICallback getCICallback() {
        return CICallback;
    }

    private static com.iwedia.dtv.ci.ICICallback CICallback = new com.iwedia.dtv.ci.ICICallback() {
        Handler mHandler = new Handler();

        @Override
        public void dialogEnquiry(int slotNumber) {
            Log.d(LOG_TAG, "dialogEnquiry");
            if (ciCallback != null) {
                try {
                    ciCallback.dialogEnquiry(slotNumber);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void dialogLabel() {
            Log.d(LOG_TAG, "dialogLabel");
            if (ciCallback != null) {
                try {
                    ciCallback.dialogLabel();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void dialogList(int slotNumber) {
            Log.d(LOG_TAG, "dialogList");
            if (ciCallback != null) {
                try {
                    ciCallback.dialogList(slotNumber);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void dialogMenu(int slotNumber) {
            Log.d(LOG_TAG, "dialogMenu");
            if (ciCallback != null) {
                Log.d(LOG_TAG, "dialogMenu - OK");
                try {
                    ciCallback.dialogMenu(slotNumber);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(LOG_TAG, "dialogMenu - no callback send intent");
                Intent intent = new Intent();
                intent.setAction("iwedia.action.CI_DIALOG");
                intent.setPackage("com.iwedia.gui");
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                IWEDIAService.getInstance().startActivity(intent);
                // wait for callback registration
                for (int i = 0; i < 10; i++) {
                    Log.d(LOG_TAG, "Waiting for callback... " + i);
                    if (ciCallback != null) {
                        Log.d(LOG_TAG, "Callback registered");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void dialogNone() {
            Log.d(LOG_TAG, "dialogNone");
            if (ciCallback != null) {
                try {
                    ciCallback.dialogNone();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void dialogRequested() {
            Log.d(LOG_TAG, "dialogRequested");
            if (ciCallback != null) {
                try {
                    ciCallback.dialogRequested();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void invalideCertificate() {
            Log.d(LOG_TAG, "invalideCertificate");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(IWEDIAService.getContext(),
                            R.string.invalid_host_certificate,
                            Toast.LENGTH_SHORT).show();
                }
            });
            if (ciCallback != null) {
                try {
                    ciCallback.invalideCertificate();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void moduleInserted() {
            Log.d(LOG_TAG, "moduleInserted");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(IWEDIAService.getContext(),
                            R.string.cam_inserted, Toast.LENGTH_SHORT).show();
                }
            });
            if (ciCallback != null) {
                try {
                    ciCallback.moduleInserted();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void moduleRemoved() {
            Log.d(LOG_TAG, "moduleRemoved");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(IWEDIAService.getContext(),
                            R.string.cam_removed, Toast.LENGTH_SHORT).show();
                }
            });
            if (ciCallback != null) {
                try {
                    ciCallback.moduleRemoved();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void noCamOnScrambled() {
            Log.d(LOG_TAG, "noCamOnScrambled");
            if (ciCallback != null) {
                try {
                    ciCallback.noCamOnScrambled();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void opNotifyLabel() {
            Log.d(LOG_TAG, "opNotifyLabel");
            if (ciCallback != null) {
                try {
                    ciCallback.opNotifyLabel();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void opNotifyQuestionLabel() {
            Log.d(LOG_TAG, "opNotifyQuestionLabel");
            if (ciCallback != null) {
                try {
                    ciCallback.opNotifyQuestionLabel();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void opProfileInstallFinished() {
            Log.d(LOG_TAG, "opProfileInstallFinished");
            if (ciCallback != null) {
                try {
                    ciCallback.opProfileInstallFinished();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void opProfileInstallStarted() {
            Log.d(LOG_TAG, "opProfileInstallStarted");
            if (ciCallback != null) {
                try {
                    ciCallback.opProfileInstallStarted();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void opProfileNameChanged() {
            Log.d(LOG_TAG, "opProfileNameChanged");
            if (ciCallback != null) {
                try {
                    ciCallback.opProfileNameChanged();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void sessionStatus() {
            Log.d(LOG_TAG, "sessionStatus");
            if (ciCallback != null) {
                try {
                    ciCallback.sessionStatus();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void statusClosed() {
            Log.d(LOG_TAG, "statusClosed");
            if (ciCallback != null) {
                try {
                    ciCallback.statusClosed();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void statusOpened() {
            Log.d(LOG_TAG, "statusOpened");
            if (ciCallback != null) {
                try {
                    ciCallback.statusOpened();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void undefined() {
            Log.d(LOG_TAG, "undefined");
            if (ciCallback != null) {
                try {
                    ciCallback.undefined();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void updateApplications() {
            Log.d(LOG_TAG, "updateApplications");
            if (ciCallback != null) {
                try {
                    ciCallback.updateApplications();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void channelZapping(boolean status) {
        // TODO Auto-generated method stub
    }
}