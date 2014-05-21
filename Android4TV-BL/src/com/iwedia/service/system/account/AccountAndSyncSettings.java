package com.iwedia.service.system.account;

import java.util.List;

import android.os.RemoteException;

import com.iwedia.comm.system.account.Account;
import com.iwedia.comm.system.account.IAccountSyncSettings;

public class AccountAndSyncSettings extends IAccountSyncSettings.Stub {
    public AccountAndSyncSettings() {
        AccountAndSyncManager.getInstance().registerObserverListeners();
    }

    @Override
    public void addAccount(Account account) throws RemoteException {
        AccountAndSyncManager.getInstance()
                .addAccount(account.getAccountType());
    }

    @Override
    public List<Account> getAvailableAccounts() throws RemoteException {
        return AccountAndSyncManager.getInstance().getAvailableAccounts();
    }

    @Override
    public List<Account> manageAccounts() throws RemoteException {
        return AccountAndSyncManager.getInstance().manageAccounts();
    }

    @Override
    public List<String> getAuthorities(Account account) throws RemoteException {
        return AccountAndSyncManager.getInstance()
                .getAuthoritiesForAccountType(account.getAccountType());
    }

    @Override
    public void setIsSyncable(Account account, String authority,
            boolean isSyncable) throws RemoteException {
        AccountAndSyncManager.getInstance().setIsSyncable(account, authority,
                isSyncable);
    }

    @Override
    public boolean getIsSyncable(Account account, String authority)
            throws RemoteException {
        return AccountAndSyncManager.getInstance().getIsSyncable(account,
                authority);
    }

    @Override
    public boolean getSyncStatus(Account account) throws RemoteException {
        return AccountAndSyncManager.getInstance().getSyncStatus(account);
    }

    @Override
    public boolean isAutoSync() throws RemoteException {
        return AccountAndSyncManager.getInstance().isAutoSync();
    }

    @Override
    public void setAutoSync(boolean sync) throws RemoteException {
        AccountAndSyncManager.getInstance().setAutoSync(sync);
    }

    @Override
    public void syncNow(Account account, String authority)
            throws RemoteException {
        AccountAndSyncManager.getInstance().syncNow(account, authority);
    }
}