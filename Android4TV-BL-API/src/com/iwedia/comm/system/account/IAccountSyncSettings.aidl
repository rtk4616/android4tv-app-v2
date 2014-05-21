package com.iwedia.comm.system.account;

import java.util.List;

import com.iwedia.comm.system.account.Account;

interface IAccountSyncSettings{

boolean isAutoSync();

void setAutoSync(boolean sync);

List<Account> getAvailableAccounts();

void addAccount(in Account account);

List<Account> manageAccounts();

List<String> getAuthorities(in Account account);

void setIsSyncable(in Account account, String authority, boolean isSyncable);

boolean getIsSyncable(in Account account, String authority);

boolean getSyncStatus(in Account account);

void syncNow(in Account account, String authority);

}