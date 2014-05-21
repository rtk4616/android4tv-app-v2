package com.iwedia.comm;
/**
 * The CI module related callbacks.
 *
 * @author Milan Vidakovic
 *
 */
interface ICICallback {

	void undefined();
	void dialogRequested();
	void sessionStatus();
	void moduleInserted();
	void moduleRemoved();
	void statusOpened();
	void statusClosed();
	void dialogNone();
	void dialogMenu(int slotNumber);
	void dialogList(int slotNumber);
	void dialogEnquiry(int slotNumber);
	void dialogLabel();
	void opNotifyLabel();
	void opNotifyQuestionLabel();
	void opProfileNameChanged();
	void opProfileInstallStarted();
	void opProfileInstallFinished();
	void invalideCertificate();
	void updateApplications();
	void noCamOnScrambled();
}