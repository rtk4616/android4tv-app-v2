package com.iwedia.service.system.account;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.iwedia.comm.system.account.Account;
import com.iwedia.service.IWEDIAService;
import com.iwedia.service.system.SystemControl;

public class AccountAndSyncManager {
    /**
     * Extra parameter to identify the caller. Applications may display a
     * different UI if the calls is made from Settings or from a specific
     * application.
     */
    private static final String KEY_CALLER_IDENTITY = "pendingIntent";
    private static final String TAG = "AccountAndSyncManager";
    /* package */static final String EXTRA_SELECTED_ACCOUNT = "selected_account";
    // show additional info regarding the use of a device with multiple users
    static final String EXTRA_HAS_MULTIPLE_USERS = "hasMultipleUsers";
    private static AccountAndSyncManager instance;
    private PendingIntent mPendingIntent;
    public HashSet<String> mAccountTypesFilter;
    @SuppressWarnings("unused")
    private boolean mAddAccountCalled = false;
    private AuthenticatorDescription[] mAuthDescs;
    private Map<String, AuthenticatorDescription> mTypeToAuthDescription;
    private ArrayList<ProviderEntry> mProviderList;
    private HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = null;
    private Account account;
    private List<Account> accountList;
    private List<Account> manageAccountList;
    android.accounts.Account accounts[] = null;
    Object syncObserverHandle;
    Object syncObserverHandle2;
    Object syncObserverHandle3;

    public AccountAndSyncManager() {
        accountList = new ArrayList<Account>();
    }

    public void addAccount(String accountType) {
        Log.e(Thread.currentThread().getStackTrace()[2].getLineNumber() + ". "
                + "ADD ACCOUNT", "" + accountType);
        Bundle addAccountOptions = new Bundle();
        mPendingIntent = PendingIntent.getBroadcast(IWEDIAService.getContext(),
                0, new Intent(), 0);
        addAccountOptions.putParcelable(KEY_CALLER_IDENTITY, mPendingIntent);
        addAccountOptions.putBoolean(EXTRA_HAS_MULTIPLE_USERS, false);
        AccountManager.get(IWEDIAService.getContext()).addAccount(accountType,
                null, /* authTokenType */
                null, /* requiredFeatures */
                addAccountOptions, null, mCallback, null /* handler */);
        mAddAccountCalled = true;
    }

    /**
     * Updates provider icons. Subclasses should call this in onCreate() and
     * update any UI that depends on AuthenticatorDescriptions in
     * onAuthDescriptionsUpdated().
     */
    private void typeToAuthDescription() {
        mTypeToAuthDescription = new HashMap<String, AuthenticatorDescription>();
        mAuthDescs = AccountManager.get(IWEDIAService.getContext())
                .getAuthenticatorTypes();
        for (int i = 0; i < mAuthDescs.length; i++) {
            mTypeToAuthDescription.put(mAuthDescs[i].type, mAuthDescs[i]);
        }
    }

    public List<Account> getAvailableAccounts() {
        accountList = new ArrayList<Account>();
        mProviderList = new ArrayList<ProviderEntry>();
        typeToAuthDescription();
        return onAuthDescriptionsUpdated();
    }

    private List<Account> onAuthDescriptionsUpdated() {
        for (int i = 0; i < mAuthDescs.length; i++) {
            String accountType = mAuthDescs[i].type;
            CharSequence providerName = getLabelForType(accountType);
            mProviderList.add(new ProviderEntry(providerName, accountType));
        }
        if (mProviderList.size() > 0) {
            Collections.sort(mProviderList);
            for (ProviderEntry pref : mProviderList) {
                account = new Account();
                account.setAccountLabel(pref.name.toString());
                account.setAccountType(pref.type);
                account.setImage(getByteArrayFromDrawable(getDrawableForType(pref.type)));
                if (IWEDIAService.DEBUG)
                    Log.e("Account type: " + pref.type, " Account name: "
                            + pref.name);
                accountList.add(account);
            }
        } else {
            if (IWEDIAService.DEBUG) {
                Log.e(TAG, "No providers found.");
            }
            accountList.add(null);
        }
        return accountList;
    }

    List<Account> manageAccounts() {
        manageAccountList = new ArrayList<Account>();
        typeToAuthDescription();
        accounts = AccountManager.get(IWEDIAService.getContext()).getAccounts();
        for (int i = 0; i < accounts.length; i++) {
            account = new Account();
            account.setAccountLabel(accounts[i].name);
            account.setAccountType(accounts[i].type);
            account.setImage(getByteArrayFromDrawable(getDrawableForType(account
                    .getAccountType())));
            manageAccountList.add(account);
        }
        if (accounts.length == 0) {
            return null;
        }
        return manageAccountList;
    }

    public void setIsSyncable(Account account, String authority,
            boolean isSyncable) {
        int sync;
        android.accounts.Account accountSync = null;
        if (isSyncable) {
            sync = 1;
        } else {
            sync = 0;
        }
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].name.equals(account.getAccountLabel())) {
                accountSync = accounts[i];
                if (IWEDIAService.DEBUG) {
                    Log.w("******setIsSyncable", accountSync.name);
                }
                break;
            }
        }
        ContentResolver.setIsSyncable(accountSync, authority, sync);
    }

    public boolean getIsSyncable(Account account, String authority) {
        android.accounts.Account accountSync = null;
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].name.equals(account.getAccountLabel())) {
                accountSync = accounts[i];
                break;
            }
        }
        int isSync = ContentResolver.getIsSyncable(accountSync, authority);
        if (isSync == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean getSyncStatus(Account account) {
        boolean isSync = false;
        if (isAutoSync()) {
            ArrayList<String> authAcc = getAuthoritiesForAccountType(account
                    .getAccountType());
            if (authAcc != null) {
                for (int i = 0; i < authAcc.size(); i++) {
                    isSync = getIsSyncable(account, authAcc.get(i));
                    if (isSync) {
                        break;
                    }
                }
            }
            return isSync;
        } else {
            return false;
        }
    }

    public boolean isAutoSync() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    public void setAutoSync(boolean sync) {
        ContentResolver.setMasterSyncAutomatically(sync);
    }

    public void syncNow(Account account, String authority) {
        Log.e("Account: " + account.getAccountLabel(), "Authority: "
                + authority);
        android.accounts.Account account2 = null;
        if (accounts == null)
            accounts = AccountManager.get(IWEDIAService.getContext())
                    .getAccounts();
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].name.equals(account.getAccountLabel())) {
                account2 = accounts[i];
                break;
            }
        }
        if (account2 != null) {
            Bundle extras = new Bundle();
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.requestSync(account2, authority, extras);
        }
    }

    public ArrayList<String> getAuthoritiesForAccountType(String type) {
        if (mAccountTypeToAuthorities == null) {
            mAccountTypeToAuthorities = new HashMap<String, ArrayList<String>>();
            SyncAdapterType[] syncAdapters = ContentResolver
                    .getSyncAdapterTypes();
            for (int i = 0, n = syncAdapters.length; i < n; i++) {
                final SyncAdapterType sa = syncAdapters[i];
                ArrayList<String> authorities = mAccountTypeToAuthorities
                        .get(sa.accountType);
                if (authorities == null) {
                    authorities = new ArrayList<String>();
                    mAccountTypeToAuthorities.put(sa.accountType, authorities);
                }
                if (!sa.authority.equals("com.android.contacts")
                        && !sa.authority.equals("subscribedfeeds")) {
                    authorities.add(sa.authority);
                }
            }
        }
        return mAccountTypeToAuthorities.get(type);
    }

    /**
     * Gets an icon associated with a particular account type. If none found,
     * return null.
     * 
     * @param accountType
     *        the type of account
     * @return a drawable for the icon or null if one cannot be found.
     */
    protected Drawable getDrawableForType(final String accountType) {
        Drawable icon = null;
        if (mTypeToAuthDescription.containsKey(accountType)) {
            try {
                AuthenticatorDescription desc = mTypeToAuthDescription
                        .get(accountType);
                Context authContext = IWEDIAService.getContext()
                        .createPackageContext(desc.packageName, 0);
                icon = authContext.getResources().getDrawable(desc.iconId);
            } catch (PackageManager.NameNotFoundException e) {
                // TODO: place holder icon for missing account icons?
                Log.w(TAG, "No icon name for account type " + accountType);
            } catch (Resources.NotFoundException e) {
                // TODO: place holder icon for missing account icons?
                Log.w(TAG, "No icon resource for account type " + accountType);
            }
        }
        return icon;
    }

    /**
     * Gets the label associated with a particular account type. If none found,
     * return null.
     * 
     * @param accountType
     *        the type of account
     * @return a CharSequence for the label or null if one cannot be found.
     */
    protected CharSequence getLabelForType(final String accountType) {
        CharSequence label = null;
        if (mTypeToAuthDescription.containsKey(accountType)) {
            try {
                AuthenticatorDescription desc = mTypeToAuthDescription
                        .get(accountType);
                Context authContext = IWEDIAService.getContext()
                        .createPackageContext(desc.packageName, 0);
                label = authContext.getResources().getText(desc.labelId);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "No label name for account type " + accountType);
            } catch (Resources.NotFoundException e) {
                Log.w(TAG, "No label resource for account type " + accountType);
            }
        }
        return label;
    }

    private static class ProviderEntry implements Comparable<ProviderEntry> {
        private final CharSequence name;
        private final String type;

        ProviderEntry(CharSequence providerName, String accountType) {
            name = providerName;
            type = accountType;
        }

        public int compareTo(ProviderEntry another) {
            if (name == null) {
                return -1;
            }
            if (another.name == null) {
                return +1;
            }
            return name.toString().compareToIgnoreCase(another.name.toString());
        }
    }

    private AccountManagerCallback<Bundle> mCallback = new AccountManagerCallback<Bundle>() {
        public void run(AccountManagerFuture<Bundle> future) {
            // boolean done = true;
            try {
                Bundle bundle = future.getResult();
                // bundle.keySet();
                Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (intent != null) {
                    // done = false;
                    Bundle addAccountOptions = new Bundle();
                    addAccountOptions.putParcelable(KEY_CALLER_IDENTITY,
                            mPendingIntent);
                    addAccountOptions.putBoolean(EXTRA_HAS_MULTIPLE_USERS,
                            false);
                    intent.putExtras(addAccountOptions);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (IWEDIAService.DEBUG) {
                        Log.e("TAG", "" + intent);
                    }
                    IWEDIAService.getInstance().startActivity(intent);
                }
                if (IWEDIAService.DEBUG) {
                    Log.e(TAG, "account added: " + bundle);
                }
            } catch (OperationCanceledException e) {
                Log.e(TAG, "addAccount was canceled");
            } catch (IOException e) {
                Log.e(TAG, "addAccount failed: " + e);
            } catch (AuthenticatorException e) {
                Log.e(TAG, "addAccount failed: " + e);
            }
        }
    };

    public byte[] getByteArrayFromDrawable(Drawable drawable) {
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        } else {
            return null;
        }
    }

    public static AccountAndSyncManager getInstance() {
        if (instance == null) {
            instance = new AccountAndSyncManager();
        }
        return instance;
    }

    public void registerObserverListeners() {
        syncObserverHandle = ContentResolver.addStatusChangeListener(
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE,
                new SyncStatusObserver() {
                    @Override
                    public void onStatusChanged(int which) {
                        SystemControl.broadcastSyncFinishedEvent();
                    }
                });
        syncObserverHandle2 = ContentResolver.addStatusChangeListener(
                ContentResolver.SYNC_OBSERVER_TYPE_PENDING,
                new SyncStatusObserver() {
                    @Override
                    public void onStatusChanged(int which) {
                        SystemControl.broadcastSyncStartedEvent();
                    }
                });
        syncObserverHandle3 = ContentResolver.addStatusChangeListener(
                ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS,
                new SyncStatusObserver() {
                    @Override
                    public void onStatusChanged(int which) {
                        Log.e("SYNC_OBSERVER_TYPE_SETTINGS", "" + which);
                    }
                });
    }

    public void unregisterObserverListeners() {
        ContentResolver.removeStatusChangeListener(syncObserverHandle);
        ContentResolver.removeStatusChangeListener(syncObserverHandle2);
        ContentResolver.removeStatusChangeListener(syncObserverHandle3);
    }
}
