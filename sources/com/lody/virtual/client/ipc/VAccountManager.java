package com.lody.virtual.client.ipc;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.IAccountManagerResponse;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.stub.AmsTask;
import com.lody.virtual.helper.compat.AccountManagerCompat;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.server.IAccountManager;
import com.lody.virtual.server.IAccountManager.Stub;

public class VAccountManager {
    private static VAccountManager sMgr = new VAccountManager();
    private IAccountManager mRemote;

    public static VAccountManager get() {
        return sMgr;
    }

    public IAccountManager getRemote() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (VAccountManager.class) {
                this.mRemote = (IAccountManager) LocalProxyUtils.genProxy(IAccountManager.class, getStubInterface());
            }
        }
        return this.mRemote;
    }

    private Object getStubInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.ACCOUNT));
    }

    public AuthenticatorDescription[] getAuthenticatorTypes() {
        try {
            return getRemote().getAuthenticatorTypes(VUserHandle.myUserId());
        } catch (RemoteException e) {
            return (AuthenticatorDescription[]) VirtualRuntime.crash(e);
        }
    }

    public void removeAccount(IAccountManagerResponse iAccountManagerResponse, Account account, boolean z) {
        try {
            getRemote().removeAccount(VUserHandle.myUserId(), iAccountManagerResponse, account, z);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAuthToken(IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, boolean z2, Bundle bundle) {
        try {
            getRemote().getAuthToken(VUserHandle.myUserId(), iAccountManagerResponse, account, str, z, z2, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean addAccountExplicitly(Account account, String str, Bundle bundle) {
        try {
            return getRemote().addAccountExplicitly(VUserHandle.myUserId(), account, str, bundle);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public Account[] getAccounts(int i, String str) {
        try {
            return getRemote().getAccounts(i, str);
        } catch (RemoteException e) {
            return (Account[]) VirtualRuntime.crash(e);
        }
    }

    public Account[] getAccounts(String str) {
        try {
            return getRemote().getAccounts(VUserHandle.myUserId(), str);
        } catch (RemoteException e) {
            return (Account[]) VirtualRuntime.crash(e);
        }
    }

    public String peekAuthToken(Account account, String str) {
        try {
            return getRemote().peekAuthToken(VUserHandle.myUserId(), account, str);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public String getPreviousName(Account account) {
        try {
            return getRemote().getPreviousName(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public void hasFeatures(IAccountManagerResponse iAccountManagerResponse, Account account, String[] strArr) {
        try {
            getRemote().hasFeatures(VUserHandle.myUserId(), iAccountManagerResponse, account, strArr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean accountAuthenticated(Account account) {
        try {
            return getRemote().accountAuthenticated(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void clearPassword(Account account) {
        try {
            getRemote().clearPassword(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void renameAccount(IAccountManagerResponse iAccountManagerResponse, Account account, String str) {
        try {
            getRemote().renameAccount(VUserHandle.myUserId(), iAccountManagerResponse, account, str);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(Account account, String str) {
        try {
            getRemote().setPassword(VUserHandle.myUserId(), account, str);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(int i, IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) {
        try {
            getRemote().addAccount(i, iAccountManagerResponse, str, str2, strArr, z, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addAccount(IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) {
        try {
            getRemote().addAccount(VUserHandle.myUserId(), iAccountManagerResponse, str, str2, strArr, z, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateCredentials(IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, Bundle bundle) {
        try {
            getRemote().updateCredentials(VUserHandle.myUserId(), iAccountManagerResponse, account, str, z, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean removeAccountExplicitly(Account account) {
        try {
            return getRemote().removeAccountExplicitly(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void setUserData(Account account, String str, String str2) {
        try {
            getRemote().setUserData(VUserHandle.myUserId(), account, str, str2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void editProperties(IAccountManagerResponse iAccountManagerResponse, String str, boolean z) {
        try {
            getRemote().editProperties(VUserHandle.myUserId(), iAccountManagerResponse, str, z);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAuthTokenLabel(IAccountManagerResponse iAccountManagerResponse, String str, String str2) {
        try {
            getRemote().getAuthTokenLabel(VUserHandle.myUserId(), iAccountManagerResponse, str, str2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void confirmCredentials(IAccountManagerResponse iAccountManagerResponse, Account account, Bundle bundle, boolean z) {
        try {
            getRemote().confirmCredentials(VUserHandle.myUserId(), iAccountManagerResponse, account, bundle, z);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void invalidateAuthToken(String str, String str2) {
        try {
            getRemote().invalidateAuthToken(VUserHandle.myUserId(), str, str2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getAccountsByFeatures(IAccountManagerResponse iAccountManagerResponse, String str, String[] strArr) {
        try {
            getRemote().getAccountsByFeatures(VUserHandle.myUserId(), iAccountManagerResponse, str, strArr);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setAuthToken(Account account, String str, String str2) {
        try {
            getRemote().setAuthToken(VUserHandle.myUserId(), account, str, str2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Object getPassword(Account account) {
        try {
            return getRemote().getPassword(VUserHandle.myUserId(), account);
        } catch (RemoteException e) {
            return VirtualRuntime.crash(e);
        }
    }

    public String getUserData(Account account, String str) {
        try {
            return getRemote().getUserData(VUserHandle.myUserId(), account, str);
        } catch (RemoteException e) {
            return (String) VirtualRuntime.crash(e);
        }
    }

    public AccountManagerFuture<Bundle> addAccount(int i, String str, String str2, String[] strArr, Bundle bundle, Activity activity, AccountManagerCallback<Bundle> accountManagerCallback, Handler handler) {
        Bundle bundle2 = bundle;
        if (str != null) {
            final Bundle bundle3 = new Bundle();
            if (bundle2 != null) {
                bundle3.putAll(bundle2);
            }
            bundle3.putString(AccountManagerCompat.KEY_ANDROID_PACKAGE_NAME, "android");
            final int i2 = i;
            final String str3 = str;
            final String str4 = str2;
            final String[] strArr2 = strArr;
            final Activity activity2 = activity;
            C10241 r0 = new AmsTask(activity, handler, accountManagerCallback) {
                public void doWork() throws RemoteException {
                    VAccountManager.this.addAccount(i2, this.mResponse, str3, str4, strArr2, activity2 != null, bundle3);
                }
            };
            return r0.start();
        }
        throw new IllegalArgumentException("accountType is null");
    }
}
