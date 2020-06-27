package com.lody.virtual.server.accounts;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.accounts.IAccountAuthenticator;
import android.accounts.IAccountAuthenticatorResponse;
import android.accounts.IAccountManagerResponse;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.compat.AccountManagerCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VBinder;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.server.IAccountManager.Stub;
import com.lody.virtual.server.p008am.VActivityManagerService;
import com.lody.virtual.server.p009pm.VPackageManagerService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import mirror.com.android.internal.R_Hide.styleable;

public class VAccountManagerService extends Stub {
    private static final long CHECK_IN_TIME = 43200000;
    /* access modifiers changed from: private */
    public static final String TAG = VAccountManagerService.class.getSimpleName();
    private static final AtomicReference<VAccountManagerService> sInstance = new AtomicReference<>();
    /* access modifiers changed from: private */
    public final SparseArray<List<VAccount>> accountsByUserId = new SparseArray<>();
    /* access modifiers changed from: private */
    public final LinkedList<AuthTokenRecord> authTokenRecords = new LinkedList<>();
    private final AuthenticatorCache cache = new AuthenticatorCache();
    private long lastAccountChangeTime = 0;
    /* access modifiers changed from: private */
    public Context mContext = VirtualCore.get().getContext();
    /* access modifiers changed from: private */
    public final LinkedHashMap<String, Session> mSessions = new LinkedHashMap<>();

    static final class AuthTokenRecord {
        public Account account;
        public String authToken;
        /* access modifiers changed from: private */
        public String authTokenType;
        public long expiryEpochMillis;
        private String packageName;
        public int userId;

        AuthTokenRecord(int i, Account account2, String str, String str2, String str3, long j) {
            this.userId = i;
            this.account = account2;
            this.authTokenType = str;
            this.packageName = str2;
            this.authToken = str3;
            this.expiryEpochMillis = j;
        }

        AuthTokenRecord(int i, Account account2, String str, String str2) {
            this.userId = i;
            this.account = account2;
            this.authTokenType = str;
            this.packageName = str2;
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            AuthTokenRecord authTokenRecord = (AuthTokenRecord) obj;
            if (this.userId != authTokenRecord.userId || !this.account.equals(authTokenRecord.account) || !this.authTokenType.equals(authTokenRecord.authTokenType) || !this.packageName.equals(authTokenRecord.packageName)) {
                z = false;
            }
            return z;
        }

        public int hashCode() {
            return (((((this.userId * 31) + this.account.hashCode()) * 31) + this.authTokenType.hashCode()) * 31) + this.packageName.hashCode();
        }
    }

    private final class AuthenticatorCache {
        final Map<String, AuthenticatorInfo> authenticators;

        private AuthenticatorCache() {
            this.authenticators = new HashMap();
        }
    }

    private final class AuthenticatorInfo {
        final AuthenticatorDescription desc;
        final ServiceInfo serviceInfo;

        AuthenticatorInfo(AuthenticatorDescription authenticatorDescription, ServiceInfo serviceInfo2) {
            this.desc = authenticatorDescription;
            this.serviceInfo = serviceInfo2;
        }
    }

    private class GetAccountsByTypeAndFeatureSession extends Session {
        private volatile Account[] mAccountsOfType = null;
        private volatile ArrayList<Account> mAccountsWithFeatures = null;
        private volatile int mCurrentAccount = 0;
        private final String[] mFeatures;

        public GetAccountsByTypeAndFeatureSession(IAccountManagerResponse iAccountManagerResponse, int i, AuthenticatorInfo authenticatorInfo, String[] strArr) {
            super(VAccountManagerService.this, iAccountManagerResponse, i, authenticatorInfo, false, true, null);
            this.mFeatures = strArr;
        }

        public void run() throws RemoteException {
            this.mAccountsOfType = VAccountManagerService.this.getAccounts(this.mUserId, this.mAuthenticatorInfo.desc.type);
            this.mAccountsWithFeatures = new ArrayList<>(this.mAccountsOfType.length);
            this.mCurrentAccount = 0;
            checkAccount();
        }

        public void checkAccount() {
            if (this.mCurrentAccount >= this.mAccountsOfType.length) {
                sendResult();
                return;
            }
            IAccountAuthenticator iAccountAuthenticator = this.mAuthenticator;
            if (iAccountAuthenticator == null) {
                String access$500 = VAccountManagerService.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("checkAccount: aborting session since we are no longer connected to the authenticator, ");
                sb.append(toDebugString());
                Log.v(access$500, sb.toString());
                return;
            }
            try {
                iAccountAuthenticator.hasFeatures(this, this.mAccountsOfType[this.mCurrentAccount], this.mFeatures);
            } catch (RemoteException unused) {
                onError(1, "remote exception");
            }
        }

        public void onResult(Bundle bundle) {
            this.mNumResults++;
            if (bundle == null) {
                onError(5, "null bundle");
                return;
            }
            if (bundle.getBoolean("booleanResult", false)) {
                this.mAccountsWithFeatures.add(this.mAccountsOfType[this.mCurrentAccount]);
            }
            this.mCurrentAccount++;
            checkAccount();
        }

        public void sendResult() {
            IAccountManagerResponse responseAndClose = getResponseAndClose();
            if (responseAndClose != null) {
                try {
                    Account[] accountArr = new Account[this.mAccountsWithFeatures.size()];
                    for (int i = 0; i < accountArr.length; i++) {
                        accountArr[i] = (Account) this.mAccountsWithFeatures.get(i);
                    }
                    if (Log.isLoggable(VAccountManagerService.TAG, 2)) {
                        String access$500 = VAccountManagerService.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append(getClass().getSimpleName());
                        sb.append(" calling onResult() on response ");
                        sb.append(responseAndClose);
                        Log.v(access$500, sb.toString());
                    }
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArray("accounts", accountArr);
                    responseAndClose.onResult(bundle);
                } catch (RemoteException e) {
                    Log.v(VAccountManagerService.TAG, "failure while notifying response", e);
                }
            }
        }

        /* access modifiers changed from: protected */
        public String toDebugString(long j) {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toDebugString(j));
            sb.append(", getAccountsByTypeAndFeatures");
            sb.append(", ");
            sb.append(this.mFeatures != null ? TextUtils.join(",", this.mFeatures) : null);
            return sb.toString();
        }
    }

    private abstract class Session extends IAccountAuthenticatorResponse.Stub implements DeathRecipient, ServiceConnection {
        private String mAccountName;
        private boolean mAuthDetailsRequired;
        IAccountAuthenticator mAuthenticator;
        final AuthenticatorInfo mAuthenticatorInfo;
        private long mCreationTime;
        private boolean mExpectActivityLaunch;
        private int mNumErrors;
        private int mNumRequestContinued;
        public int mNumResults;
        private IAccountManagerResponse mResponse;
        private final boolean mStripAuthTokenFromResult;
        private boolean mUpdateLastAuthenticatedTime;
        final int mUserId;

        public abstract void run() throws RemoteException;

        Session(IAccountManagerResponse iAccountManagerResponse, int i, AuthenticatorInfo authenticatorInfo, boolean z, boolean z2, String str, boolean z3, boolean z4) {
            if (authenticatorInfo != null) {
                this.mStripAuthTokenFromResult = z2;
                this.mResponse = iAccountManagerResponse;
                this.mUserId = i;
                this.mAuthenticatorInfo = authenticatorInfo;
                this.mExpectActivityLaunch = z;
                this.mCreationTime = SystemClock.elapsedRealtime();
                this.mAccountName = str;
                this.mAuthDetailsRequired = z3;
                this.mUpdateLastAuthenticatedTime = z4;
                synchronized (VAccountManagerService.this.mSessions) {
                    VAccountManagerService.this.mSessions.put(toString(), this);
                }
                if (iAccountManagerResponse != null) {
                    try {
                        iAccountManagerResponse.asBinder().linkToDeath(this, 0);
                    } catch (RemoteException unused) {
                        this.mResponse = null;
                        binderDied();
                    }
                }
            } else {
                throw new IllegalArgumentException("accountType is null");
            }
        }

        Session(VAccountManagerService vAccountManagerService, IAccountManagerResponse iAccountManagerResponse, int i, AuthenticatorInfo authenticatorInfo, boolean z, boolean z2, String str) {
            this(iAccountManagerResponse, i, authenticatorInfo, z, z2, str, false, false);
        }

        /* access modifiers changed from: 0000 */
        public IAccountManagerResponse getResponseAndClose() {
            if (this.mResponse == null) {
                return null;
            }
            IAccountManagerResponse iAccountManagerResponse = this.mResponse;
            close();
            return iAccountManagerResponse;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001e, code lost:
            r3.mResponse.asBinder().unlinkToDeath(r3, 0);
            r3.mResponse = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
            unbind();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x001c, code lost:
            if (r3.mResponse == null) goto L_0x002b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void close() {
            /*
                r3 = this;
                com.lody.virtual.server.accounts.VAccountManagerService r0 = com.lody.virtual.server.accounts.VAccountManagerService.this
                java.util.LinkedHashMap r0 = r0.mSessions
                monitor-enter(r0)
                com.lody.virtual.server.accounts.VAccountManagerService r1 = com.lody.virtual.server.accounts.VAccountManagerService.this     // Catch:{ all -> 0x002f }
                java.util.LinkedHashMap r1 = r1.mSessions     // Catch:{ all -> 0x002f }
                java.lang.String r2 = r3.toString()     // Catch:{ all -> 0x002f }
                java.lang.Object r1 = r1.remove(r2)     // Catch:{ all -> 0x002f }
                if (r1 != 0) goto L_0x0019
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                return
            L_0x0019:
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                android.accounts.IAccountManagerResponse r0 = r3.mResponse
                if (r0 == 0) goto L_0x002b
                android.accounts.IAccountManagerResponse r0 = r3.mResponse
                android.os.IBinder r0 = r0.asBinder()
                r1 = 0
                r0.unlinkToDeath(r3, r1)
                r0 = 0
                r3.mResponse = r0
            L_0x002b:
                r3.unbind()
                return
            L_0x002f:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.accounts.VAccountManagerService.Session.close():void");
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            this.mAuthenticator = IAccountAuthenticator.Stub.asInterface(iBinder);
            try {
                run();
            } catch (RemoteException unused) {
                onError(1, "remote exception");
            }
        }

        public void onRequestContinued() {
            this.mNumRequestContinued++;
        }

        public void onError(int i, String str) {
            this.mNumErrors++;
            IAccountManagerResponse responseAndClose = getResponseAndClose();
            if (responseAndClose != null) {
                String access$500 = VAccountManagerService.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(getClass().getSimpleName());
                sb.append(" calling onError() on response ");
                sb.append(responseAndClose);
                Log.v(access$500, sb.toString());
                try {
                    responseAndClose.onError(i, str);
                } catch (RemoteException e) {
                    Log.v(VAccountManagerService.TAG, "Session.onError: caught RemoteException while responding", e);
                }
            } else {
                Log.v(VAccountManagerService.TAG, "Session.onError: already closed");
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            this.mAuthenticator = null;
            IAccountManagerResponse responseAndClose = getResponseAndClose();
            if (responseAndClose != null) {
                try {
                    responseAndClose.onError(1, "disconnected");
                } catch (RemoteException e) {
                    Log.v(VAccountManagerService.TAG, "Session.onServiceDisconnected: caught RemoteException while responding", e);
                }
            }
        }

        public void onResult(Bundle bundle) throws RemoteException {
            IAccountManagerResponse iAccountManagerResponse;
            boolean z = true;
            this.mNumResults++;
            if (bundle != null) {
                boolean z2 = bundle.getBoolean("booleanResult", false);
                boolean z3 = bundle.containsKey("authAccount") && bundle.containsKey("accountType");
                if (!this.mUpdateLastAuthenticatedTime || (!z2 && !z3)) {
                    z = false;
                }
                if (z || this.mAuthDetailsRequired) {
                    synchronized (VAccountManagerService.this.accountsByUserId) {
                        VAccount access$200 = VAccountManagerService.this.getAccount(this.mUserId, this.mAccountName, this.mAuthenticatorInfo.desc.type);
                        if (z && access$200 != null) {
                            access$200.lastAuthenticatedTime = System.currentTimeMillis();
                            VAccountManagerService.this.saveAllAccounts();
                        }
                        if (this.mAuthDetailsRequired) {
                            long j = -1;
                            if (access$200 != null) {
                                j = access$200.lastAuthenticatedTime;
                            }
                            bundle.putLong(AccountManagerCompat.KEY_LAST_AUTHENTICATED_TIME, j);
                        }
                    }
                }
            }
            if (bundle != null) {
                TextUtils.isEmpty(bundle.getString("authtoken"));
            }
            Intent intent = null;
            if (bundle != null) {
                intent = (Intent) bundle.getParcelable(BaseLauncherColumns.INTENT);
            }
            if (!this.mExpectActivityLaunch || bundle == null || !bundle.containsKey(BaseLauncherColumns.INTENT)) {
                iAccountManagerResponse = getResponseAndClose();
            } else {
                iAccountManagerResponse = this.mResponse;
            }
            if (iAccountManagerResponse == null) {
                return;
            }
            if (bundle == null) {
                try {
                    String access$500 = VAccountManagerService.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append(getClass().getSimpleName());
                    sb.append(" calling onError() on response ");
                    sb.append(iAccountManagerResponse);
                    Log.v(access$500, sb.toString());
                    iAccountManagerResponse.onError(5, "null bundle returned");
                } catch (RemoteException e) {
                    Log.v(VAccountManagerService.TAG, "failure while notifying response", e);
                }
            } else {
                if (this.mStripAuthTokenFromResult) {
                    bundle.remove("authtoken");
                }
                String access$5002 = VAccountManagerService.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(getClass().getSimpleName());
                sb2.append(" calling onResult() on response ");
                sb2.append(iAccountManagerResponse);
                Log.v(access$5002, sb2.toString());
                if (bundle.getInt("errorCode", -1) <= 0 || intent != null) {
                    iAccountManagerResponse.onResult(bundle);
                } else {
                    iAccountManagerResponse.onError(bundle.getInt("errorCode"), bundle.getString("errorMessage"));
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void bind() {
            String access$500 = VAccountManagerService.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("initiating bind to authenticator type ");
            sb.append(this.mAuthenticatorInfo.desc.type);
            Log.v(access$500, sb.toString());
            Intent intent = new Intent();
            intent.setAction("android.accounts.AccountAuthenticator");
            intent.setClassName(this.mAuthenticatorInfo.serviceInfo.packageName, this.mAuthenticatorInfo.serviceInfo.name);
            intent.putExtra("_VA_|_user_id_", this.mUserId);
            if (!VAccountManagerService.this.mContext.bindService(intent, this, 1)) {
                String access$5002 = VAccountManagerService.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("bind attempt failed for ");
                sb2.append(toDebugString());
                Log.d(access$5002, sb2.toString());
                onError(1, "bind failure");
            }
        }

        /* access modifiers changed from: protected */
        public String toDebugString() {
            return toDebugString(SystemClock.elapsedRealtime());
        }

        /* access modifiers changed from: protected */
        public String toDebugString(long j) {
            StringBuilder sb = new StringBuilder();
            sb.append("Session: expectLaunch ");
            sb.append(this.mExpectActivityLaunch);
            sb.append(", connected ");
            sb.append(this.mAuthenticator != null);
            sb.append(", stats (");
            sb.append(this.mNumResults);
            sb.append("/");
            sb.append(this.mNumRequestContinued);
            sb.append("/");
            sb.append(this.mNumErrors);
            sb.append(")");
            sb.append(", lifetime ");
            sb.append(((double) (j - this.mCreationTime)) / 1000.0d);
            return sb.toString();
        }

        private void unbind() {
            if (this.mAuthenticator != null) {
                this.mAuthenticator = null;
                VAccountManagerService.this.mContext.unbindService(this);
            }
        }

        public void binderDied() {
            this.mResponse = null;
            close();
        }
    }

    public static VAccountManagerService get() {
        return (VAccountManagerService) sInstance.get();
    }

    public static void systemReady() {
        VAccountManagerService vAccountManagerService = new VAccountManagerService();
        vAccountManagerService.readAllAccounts();
        sInstance.set(vAccountManagerService);
    }

    private static AuthenticatorDescription parseAuthenticatorDescription(Resources resources, String str, AttributeSet attributeSet) {
        TypedArray obtainAttributes = resources.obtainAttributes(attributeSet, (int[]) styleable.AccountAuthenticator.get());
        try {
            String string = obtainAttributes.getString(styleable.AccountAuthenticator_accountType.get());
            int resourceId = obtainAttributes.getResourceId(styleable.AccountAuthenticator_label.get(), 0);
            int resourceId2 = obtainAttributes.getResourceId(styleable.AccountAuthenticator_icon.get(), 0);
            int resourceId3 = obtainAttributes.getResourceId(styleable.AccountAuthenticator_smallIcon.get(), 0);
            int resourceId4 = obtainAttributes.getResourceId(styleable.AccountAuthenticator_accountPreferences.get(), 0);
            boolean z = obtainAttributes.getBoolean(styleable.AccountAuthenticator_customTokens.get(), false);
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            AuthenticatorDescription authenticatorDescription = new AuthenticatorDescription(string, str, resourceId, resourceId2, resourceId3, resourceId4, z);
            obtainAttributes.recycle();
            return authenticatorDescription;
        } finally {
            obtainAttributes.recycle();
        }
    }

    public AuthenticatorDescription[] getAuthenticatorTypes(int i) {
        AuthenticatorDescription[] authenticatorDescriptionArr;
        synchronized (this.cache) {
            authenticatorDescriptionArr = new AuthenticatorDescription[this.cache.authenticators.size()];
            int i2 = 0;
            for (AuthenticatorInfo authenticatorInfo : this.cache.authenticators.values()) {
                authenticatorDescriptionArr[i2] = authenticatorInfo.desc;
                i2++;
            }
        }
        return authenticatorDescriptionArr;
    }

    public void getAccountsByFeatures(int i, IAccountManagerResponse iAccountManagerResponse, String str, String[] strArr) {
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (str != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(str);
            if (authenticatorInfo == null) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArray("accounts", new Account[0]);
                try {
                    iAccountManagerResponse.onResult(bundle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (strArr == null || strArr.length == 0) {
                Bundle bundle2 = new Bundle();
                bundle2.putParcelableArray("accounts", getAccounts(i, str));
                try {
                    iAccountManagerResponse.onResult(bundle2);
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                }
            } else {
                GetAccountsByTypeAndFeatureSession getAccountsByTypeAndFeatureSession = new GetAccountsByTypeAndFeatureSession(iAccountManagerResponse, i, authenticatorInfo, strArr);
                getAccountsByTypeAndFeatureSession.bind();
            }
        } else {
            throw new IllegalArgumentException("accountType is null");
        }
    }

    public final String getPreviousName(int i, Account account) {
        String str;
        if (account != null) {
            synchronized (this.accountsByUserId) {
                str = null;
                VAccount account2 = getAccount(i, account);
                if (account2 != null) {
                    str = account2.previousName;
                }
            }
            return str;
        }
        throw new IllegalArgumentException("account is null");
    }

    public Account[] getAccounts(int i, String str) {
        List accountList = getAccountList(i, str);
        return (Account[]) accountList.toArray(new Account[accountList.size()]);
    }

    private List<Account> getAccountList(int i, String str) {
        ArrayList arrayList;
        synchronized (this.accountsByUserId) {
            arrayList = new ArrayList();
            List<VAccount> list = (List) this.accountsByUserId.get(i);
            if (list != null) {
                for (VAccount vAccount : list) {
                    if (str == null || vAccount.type.equals(str)) {
                        arrayList.add(new Account(vAccount.name, vAccount.type));
                    }
                }
            }
        }
        return arrayList;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        try {
            return super.onTransact(i, parcel, parcel2, i2);
        } catch (Throwable th) {
            th.printStackTrace();
            throw th;
        }
    }

    public final void getAuthToken(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, boolean z2, Bundle bundle) {
        VAccount account2;
        int i2 = i;
        IAccountManagerResponse iAccountManagerResponse2 = iAccountManagerResponse;
        Account account3 = account;
        String str2 = str;
        Bundle bundle2 = bundle;
        if (iAccountManagerResponse2 == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account3 == null) {
            try {
                VLog.m91w(TAG, "getAuthToken called with null account", new Object[0]);
                iAccountManagerResponse2.onError(7, "account is null");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (str2 == null) {
            VLog.m91w(TAG, "getAuthToken called with null authTokenType", new Object[0]);
            iAccountManagerResponse2.onError(7, "authTokenType is null");
        } else {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(account3.type);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse2.onError(7, "account.type does not exist");
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                }
                return;
            }
            String string = bundle2.getString(AccountManagerCompat.KEY_ANDROID_PACKAGE_NAME);
            final boolean z3 = authenticatorInfo.desc.customTokens;
            bundle2.putInt("callerUid", VBinder.getCallingUid());
            bundle2.putInt("callerPid", Binder.getCallingPid());
            if (z) {
                bundle2.putBoolean(AccountManagerCompat.KEY_NOTIFY_ON_FAILURE, true);
            }
            if (!z3) {
                synchronized (this.accountsByUserId) {
                    account2 = getAccount(i2, account3);
                }
                String str3 = account2 != null ? (String) account2.authTokens.get(str2) : null;
                if (str3 != null) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("authtoken", str3);
                    bundle3.putString("authAccount", account3.name);
                    bundle3.putString("accountType", account3.type);
                    onResult(iAccountManagerResponse2, bundle3);
                    return;
                }
            }
            if (z3) {
                String customAuthToken = getCustomAuthToken(i2, account3, str2, string);
                if (customAuthToken != null) {
                    Bundle bundle4 = new Bundle();
                    bundle4.putString("authtoken", customAuthToken);
                    bundle4.putString("authAccount", account3.name);
                    bundle4.putString("accountType", account3.type);
                    onResult(iAccountManagerResponse2, bundle4);
                    return;
                }
            }
            String str4 = account3.name;
            final Account account4 = account;
            final String str5 = str;
            final Bundle bundle5 = bundle;
            final boolean z4 = z;
            String str6 = string;
            final int i3 = i;
            final String str7 = str6;
            C10951 r1 = new Session(iAccountManagerResponse, i, authenticatorInfo, z2, false, str4) {
                /* access modifiers changed from: protected */
                public String toDebugString(long j) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(super.toDebugString(j));
                    sb.append(", getAuthToken");
                    sb.append(", ");
                    sb.append(account4);
                    sb.append(", authTokenType ");
                    sb.append(str5);
                    sb.append(", loginOptions ");
                    sb.append(bundle5);
                    sb.append(", notifyOnAuthFailure ");
                    sb.append(z4);
                    return sb.toString();
                }

                public void run() throws RemoteException {
                    this.mAuthenticator.getAuthToken(this, account4, str5, bundle5);
                }

                public void onResult(Bundle bundle) throws RemoteException {
                    if (bundle != null) {
                        String string = bundle.getString("authtoken");
                        if (string != null) {
                            String string2 = bundle.getString("authAccount");
                            String string3 = bundle.getString("accountType");
                            if (TextUtils.isEmpty(string3) || TextUtils.isEmpty(string2)) {
                                onError(5, "the type and name should not be empty");
                                return;
                            }
                            if (!z3) {
                                synchronized (VAccountManagerService.this.accountsByUserId) {
                                    if (VAccountManagerService.this.getAccount(i3, string2, string3) == null) {
                                        List list = (List) VAccountManagerService.this.accountsByUserId.get(i3);
                                        if (list == null) {
                                            list = new ArrayList();
                                            VAccountManagerService.this.accountsByUserId.put(i3, list);
                                        }
                                        list.add(new VAccount(i3, new Account(string2, string3)));
                                        VAccountManagerService.this.saveAllAccounts();
                                    }
                                }
                            }
                            long j = bundle.getLong(AccountManagerCompat.KEY_CUSTOM_TOKEN_EXPIRY, 0);
                            if (z3 && j > System.currentTimeMillis()) {
                                AuthTokenRecord authTokenRecord = new AuthTokenRecord(i3, account4, str5, str7, string, j);
                                synchronized (VAccountManagerService.this.authTokenRecords) {
                                    VAccountManagerService.this.authTokenRecords.remove(authTokenRecord);
                                    VAccountManagerService.this.authTokenRecords.add(authTokenRecord);
                                }
                            }
                        }
                        if (((Intent) bundle.getParcelable(BaseLauncherColumns.INTENT)) != null && z4) {
                            boolean z = z3;
                        }
                    }
                    super.onResult(bundle);
                }
            };
            r1.bind();
        }
    }

    public void setPassword(int i, Account account, String str) {
        if (account != null) {
            setPasswordInternal(i, account, str);
            return;
        }
        throw new IllegalArgumentException("account is null");
    }

    private void setPasswordInternal(int i, Account account, String str) {
        synchronized (this.accountsByUserId) {
            VAccount account2 = getAccount(i, account);
            if (account2 != null) {
                account2.password = str;
                account2.authTokens.clear();
                saveAllAccounts();
                synchronized (this.authTokenRecords) {
                    Iterator it = this.authTokenRecords.iterator();
                    while (it.hasNext()) {
                        AuthTokenRecord authTokenRecord = (AuthTokenRecord) it.next();
                        if (authTokenRecord.userId == i && authTokenRecord.account.equals(account)) {
                            it.remove();
                        }
                    }
                }
                sendAccountsChangedBroadcast(i);
            }
        }
    }

    public void setAuthToken(int i, Account account, String str, String str2) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        } else if (str != null) {
            synchronized (this.accountsByUserId) {
                VAccount account2 = getAccount(i, account);
                if (account2 != null) {
                    account2.authTokens.put(str, str2);
                    saveAllAccounts();
                }
            }
        } else {
            throw new IllegalArgumentException("authTokenType is null");
        }
    }

    public void setUserData(int i, Account account, String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("key is null");
        } else if (account != null) {
            VAccount account2 = getAccount(i, account);
            if (account2 != null) {
                synchronized (this.accountsByUserId) {
                    account2.userDatas.put(str, str2);
                    saveAllAccounts();
                }
            }
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    public void hasFeatures(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String[] strArr) {
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account == null) {
            throw new IllegalArgumentException("account is null");
        } else if (strArr != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(account.type);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse.onError(7, "account.type does not exist");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            final Account account2 = account;
            final String[] strArr2 = strArr;
            C10962 r1 = new Session(iAccountManagerResponse, i, authenticatorInfo, false, true, account.name) {
                public void run() throws RemoteException {
                    try {
                        this.mAuthenticator.hasFeatures(this, account2, strArr2);
                    } catch (RemoteException unused) {
                        onError(1, "remote exception");
                    }
                }

                public void onResult(Bundle bundle) throws RemoteException {
                    IAccountManagerResponse responseAndClose = getResponseAndClose();
                    if (responseAndClose != null) {
                        if (bundle == null) {
                            try {
                                responseAndClose.onError(5, "null bundle");
                            } catch (RemoteException e) {
                                Log.v(VAccountManagerService.TAG, "failure while notifying response", e);
                            }
                        } else {
                            String access$500 = VAccountManagerService.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append(getClass().getSimpleName());
                            sb.append(" calling onResult() on response ");
                            sb.append(responseAndClose);
                            Log.v(access$500, sb.toString());
                            Bundle bundle2 = new Bundle();
                            bundle2.putBoolean("booleanResult", bundle.getBoolean("booleanResult", false));
                            responseAndClose.onResult(bundle2);
                        }
                    }
                }
            };
            r1.bind();
        } else {
            throw new IllegalArgumentException("features is null");
        }
    }

    public void updateCredentials(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, Bundle bundle) {
        IAccountManagerResponse iAccountManagerResponse2 = iAccountManagerResponse;
        Account account2 = account;
        if (iAccountManagerResponse2 == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account2 == null) {
            throw new IllegalArgumentException("account is null");
        } else if (str != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(account2.type);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse2.onError(7, "account.type does not exist");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            String str2 = account2.name;
            final Account account3 = account;
            final String str3 = str;
            final Bundle bundle2 = bundle;
            C10973 r1 = new Session(iAccountManagerResponse, i, authenticatorInfo, z, false, str2) {
                public void run() throws RemoteException {
                    this.mAuthenticator.updateCredentials(this, account3, str3, bundle2);
                }

                /* access modifiers changed from: protected */
                public String toDebugString(long j) {
                    if (bundle2 != null) {
                        bundle2.keySet();
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(super.toDebugString(j));
                    sb.append(", updateCredentials");
                    sb.append(", ");
                    sb.append(account3);
                    sb.append(", authTokenType ");
                    sb.append(str3);
                    sb.append(", loginOptions ");
                    sb.append(bundle2);
                    return sb.toString();
                }
            };
            r1.bind();
        } else {
            throw new IllegalArgumentException("authTokenType is null");
        }
    }

    public String getPassword(int i, Account account) {
        if (account != null) {
            synchronized (this.accountsByUserId) {
                VAccount account2 = getAccount(i, account);
                if (account2 == null) {
                    return null;
                }
                String str = account2.password;
                return str;
            }
        }
        throw new IllegalArgumentException("account is null");
    }

    public String getUserData(int i, Account account, String str) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        } else if (str != null) {
            synchronized (this.accountsByUserId) {
                VAccount account2 = getAccount(i, account);
                if (account2 == null) {
                    return null;
                }
                String str2 = (String) account2.userDatas.get(str);
                return str2;
            }
        } else {
            throw new IllegalArgumentException("key is null");
        }
    }

    public void editProperties(int i, IAccountManagerResponse iAccountManagerResponse, String str, boolean z) {
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (str != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(str);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse.onError(7, "account.type does not exist");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            final String str2 = str;
            C10984 r0 = new Session(iAccountManagerResponse, i, authenticatorInfo, z, true, null) {
                public void run() throws RemoteException {
                    this.mAuthenticator.editProperties(this, this.mAuthenticatorInfo.desc.type);
                }

                /* access modifiers changed from: protected */
                public String toDebugString(long j) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(super.toDebugString(j));
                    sb.append(", editProperties");
                    sb.append(", accountType ");
                    sb.append(str2);
                    return sb.toString();
                }
            };
            r0.bind();
        } else {
            throw new IllegalArgumentException("accountType is null");
        }
    }

    public void getAuthTokenLabel(int i, IAccountManagerResponse iAccountManagerResponse, String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("accountType is null");
        } else if (str2 != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(str);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse.onError(7, "account.type does not exist");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            final String str3 = str2;
            C10995 r0 = new Session(iAccountManagerResponse, i, authenticatorInfo, false, false, null) {
                public void run() throws RemoteException {
                    this.mAuthenticator.getAuthTokenLabel(this, str3);
                }

                public void onResult(Bundle bundle) throws RemoteException {
                    if (bundle != null) {
                        String string = bundle.getString("authTokenLabelKey");
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("authTokenLabelKey", string);
                        super.onResult(bundle2);
                        return;
                    }
                    super.onResult(null);
                }
            };
            r0.bind();
        } else {
            throw new IllegalArgumentException("authTokenType is null");
        }
    }

    public void confirmCredentials(int i, IAccountManagerResponse iAccountManagerResponse, Account account, Bundle bundle, boolean z) {
        IAccountManagerResponse iAccountManagerResponse2 = iAccountManagerResponse;
        Account account2 = account;
        if (iAccountManagerResponse2 == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account2 != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(account2.type);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse2.onError(7, "account.type does not exist");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            String str = account2.name;
            final Account account3 = account;
            final Bundle bundle2 = bundle;
            C11006 r1 = new Session(iAccountManagerResponse, i, authenticatorInfo, z, true, str, true, true) {
                public void run() throws RemoteException {
                    this.mAuthenticator.confirmCredentials(this, account3, bundle2);
                }
            };
            r1.bind();
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    public void addAccount(int i, IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) {
        IAccountManagerResponse iAccountManagerResponse2 = iAccountManagerResponse;
        String str3 = str;
        if (iAccountManagerResponse2 == null) {
            throw new IllegalArgumentException("response is null");
        } else if (str3 != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(str3);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse2.onError(7, "account.type does not exist");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            final String str4 = str2;
            final String[] strArr2 = strArr;
            final Bundle bundle2 = bundle;
            final String str5 = str;
            C11017 r1 = new Session(iAccountManagerResponse, i, authenticatorInfo, z, true, null, false, true) {
                public void run() throws RemoteException {
                    this.mAuthenticator.addAccount(this, this.mAuthenticatorInfo.desc.type, str4, strArr2, bundle2);
                }

                /* access modifiers changed from: protected */
                public String toDebugString(long j) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(super.toDebugString(j));
                    sb.append(", addAccount");
                    sb.append(", accountType ");
                    sb.append(str5);
                    sb.append(", requiredFeatures ");
                    sb.append(strArr2 != null ? TextUtils.join(",", strArr2) : null);
                    return sb.toString();
                }
            };
            r1.bind();
        } else {
            throw new IllegalArgumentException("accountType is null");
        }
    }

    public boolean addAccountExplicitly(int i, Account account, String str, Bundle bundle) {
        if (account != null) {
            return insertAccountIntoDatabase(i, account, str, bundle);
        }
        throw new IllegalArgumentException("account is null");
    }

    public boolean removeAccountExplicitly(int i, Account account) {
        return account != null && removeAccountInternal(i, account);
    }

    public void renameAccount(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str) {
        if (account != null) {
            Account renameAccountInternal = renameAccountInternal(i, account, str);
            Bundle bundle = new Bundle();
            bundle.putString("authAccount", renameAccountInternal.name);
            bundle.putString("accountType", renameAccountInternal.type);
            try {
                iAccountManagerResponse.onResult(bundle);
            } catch (RemoteException e) {
                Log.w(TAG, e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    public void removeAccount(int i, IAccountManagerResponse iAccountManagerResponse, Account account, boolean z) {
        if (iAccountManagerResponse == null) {
            throw new IllegalArgumentException("response is null");
        } else if (account != null) {
            AuthenticatorInfo authenticatorInfo = getAuthenticatorInfo(account.type);
            if (authenticatorInfo == null) {
                try {
                    iAccountManagerResponse.onError(7, "account.type does not exist");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            final Account account2 = account;
            final int i2 = i;
            C11028 r1 = new Session(iAccountManagerResponse, i, authenticatorInfo, z, true, account.name) {
                /* access modifiers changed from: protected */
                public String toDebugString(long j) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(super.toDebugString(j));
                    sb.append(", removeAccount");
                    sb.append(", account ");
                    sb.append(account2);
                    return sb.toString();
                }

                public void run() throws RemoteException {
                    this.mAuthenticator.getAccountRemovalAllowed(this, account2);
                }

                public void onResult(Bundle bundle) throws RemoteException {
                    if (bundle != null && bundle.containsKey("booleanResult") && !bundle.containsKey(BaseLauncherColumns.INTENT)) {
                        boolean z = bundle.getBoolean("booleanResult");
                        if (z) {
                            VAccountManagerService.this.removeAccountInternal(i2, account2);
                        }
                        IAccountManagerResponse responseAndClose = getResponseAndClose();
                        if (responseAndClose != null) {
                            String access$500 = VAccountManagerService.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append(getClass().getSimpleName());
                            sb.append(" calling onResult() on response ");
                            sb.append(responseAndClose);
                            Log.v(access$500, sb.toString());
                            Bundle bundle2 = new Bundle();
                            bundle2.putBoolean("booleanResult", z);
                            try {
                                responseAndClose.onResult(bundle2);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    super.onResult(bundle);
                }
            };
            r1.bind();
        } else {
            throw new IllegalArgumentException("account is null");
        }
    }

    public void clearPassword(int i, Account account) {
        if (account != null) {
            setPasswordInternal(i, account, null);
            return;
        }
        throw new IllegalArgumentException("account is null");
    }

    /* access modifiers changed from: private */
    public boolean removeAccountInternal(int i, Account account) {
        List list = (List) this.accountsByUserId.get(i);
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                VAccount vAccount = (VAccount) it.next();
                if (i == vAccount.userId && TextUtils.equals(vAccount.name, account.name) && TextUtils.equals(account.type, vAccount.type)) {
                    it.remove();
                    saveAllAccounts();
                    sendAccountsChangedBroadcast(i);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean accountAuthenticated(int i, Account account) {
        if (account != null) {
            synchronized (this.accountsByUserId) {
                VAccount account2 = getAccount(i, account);
                if (account2 == null) {
                    return false;
                }
                account2.lastAuthenticatedTime = System.currentTimeMillis();
                saveAllAccounts();
                return true;
            }
        }
        throw new IllegalArgumentException("account is null");
    }

    public void invalidateAuthToken(int i, String str, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("accountType is null");
        } else if (str2 != null) {
            synchronized (this.accountsByUserId) {
                List<VAccount> list = (List) this.accountsByUserId.get(i);
                if (list != null) {
                    boolean z = false;
                    for (VAccount vAccount : list) {
                        if (vAccount.type.equals(str)) {
                            vAccount.authTokens.values().remove(str2);
                            z = true;
                        }
                    }
                    if (z) {
                        saveAllAccounts();
                    }
                }
                synchronized (this.authTokenRecords) {
                    Iterator it = this.authTokenRecords.iterator();
                    while (it.hasNext()) {
                        AuthTokenRecord authTokenRecord = (AuthTokenRecord) it.next();
                        if (authTokenRecord.userId == i && authTokenRecord.authTokenType.equals(str) && authTokenRecord.authToken.equals(str2)) {
                            it.remove();
                        }
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("authToken is null");
        }
    }

    private Account renameAccountInternal(int i, Account account, String str) {
        synchronized (this.accountsByUserId) {
            VAccount account2 = getAccount(i, account);
            if (account2 == null) {
                return account;
            }
            account2.previousName = account2.name;
            account2.name = str;
            saveAllAccounts();
            Account account3 = new Account(account2.name, account2.type);
            synchronized (this.authTokenRecords) {
                Iterator it = this.authTokenRecords.iterator();
                while (it.hasNext()) {
                    AuthTokenRecord authTokenRecord = (AuthTokenRecord) it.next();
                    if (authTokenRecord.userId == i && authTokenRecord.account.equals(account)) {
                        authTokenRecord.account = account3;
                    }
                }
            }
            sendAccountsChangedBroadcast(i);
            return account3;
        }
    }

    public String peekAuthToken(int i, Account account, String str) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        } else if (str != null) {
            synchronized (this.accountsByUserId) {
                VAccount account2 = getAccount(i, account);
                if (account2 == null) {
                    return null;
                }
                String str2 = (String) account2.authTokens.get(str);
                return str2;
            }
        } else {
            throw new IllegalArgumentException("authTokenType is null");
        }
    }

    private String getCustomAuthToken(int i, Account account, String str, String str2) {
        String str3;
        AuthTokenRecord authTokenRecord = new AuthTokenRecord(i, account, str, str2);
        long currentTimeMillis = System.currentTimeMillis();
        synchronized (this.authTokenRecords) {
            Iterator it = this.authTokenRecords.iterator();
            str3 = null;
            while (it.hasNext()) {
                AuthTokenRecord authTokenRecord2 = (AuthTokenRecord) it.next();
                if (authTokenRecord2.expiryEpochMillis > 0 && authTokenRecord2.expiryEpochMillis < currentTimeMillis) {
                    it.remove();
                } else if (authTokenRecord.equals(authTokenRecord2)) {
                    str3 = authTokenRecord.authToken;
                }
            }
        }
        return str3;
    }

    private void onResult(IAccountManagerResponse iAccountManagerResponse, Bundle bundle) {
        try {
            iAccountManagerResponse.onResult(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private AuthenticatorInfo getAuthenticatorInfo(String str) {
        AuthenticatorInfo authenticatorInfo;
        synchronized (this.cache) {
            if (str == null) {
                authenticatorInfo = null;
            } else {
                authenticatorInfo = (AuthenticatorInfo) this.cache.authenticators.get(str);
            }
        }
        return authenticatorInfo;
    }

    private VAccount getAccount(int i, Account account) {
        return getAccount(i, account.name, account.type);
    }

    private boolean insertAccountIntoDatabase(int i, Account account, String str, Bundle bundle) {
        if (account == null) {
            return false;
        }
        synchronized (this.accountsByUserId) {
            VAccount vAccount = new VAccount(i, account);
            vAccount.password = str;
            if (bundle != null) {
                for (String str2 : bundle.keySet()) {
                    Object obj = bundle.get(str2);
                    if (obj instanceof String) {
                        vAccount.userDatas.put(str2, (String) obj);
                    }
                }
            }
            List list = (List) this.accountsByUserId.get(i);
            if (list == null) {
                list = new ArrayList();
                this.accountsByUserId.put(i, list);
            }
            list.add(vAccount);
            saveAllAccounts();
            sendAccountsChangedBroadcast(vAccount.userId);
        }
        return true;
    }

    private void sendAccountsChangedBroadcast(int i) {
        VActivityManagerService.get().sendBroadcastAsUser(new Intent("android.accounts.LOGIN_ACCOUNTS_CHANGED"), new VUserHandle(i));
        broadcastCheckInNowIfNeed(i);
    }

    private void broadcastCheckInNowIfNeed(int i) {
        long currentTimeMillis = System.currentTimeMillis();
        if (Math.abs(currentTimeMillis - this.lastAccountChangeTime) > CHECK_IN_TIME) {
            this.lastAccountChangeTime = currentTimeMillis;
            saveAllAccounts();
            VActivityManagerService.get().sendBroadcastAsUser(new Intent("android.server.checkin.CHECKIN_NOW"), new VUserHandle(i));
        }
    }

    /* access modifiers changed from: private */
    public void saveAllAccounts() {
        File accountConfigFile = VEnvironment.getAccountConfigFile();
        Parcel obtain = Parcel.obtain();
        try {
            obtain.writeInt(1);
            ArrayList<VAccount> arrayList = new ArrayList<>();
            for (int i = 0; i < this.accountsByUserId.size(); i++) {
                List list = (List) this.accountsByUserId.valueAt(i);
                if (list != null) {
                    arrayList.addAll(list);
                }
            }
            obtain.writeInt(arrayList.size());
            for (VAccount writeToParcel : arrayList) {
                writeToParcel.writeToParcel(obtain, 0);
            }
            obtain.writeLong(this.lastAccountChangeTime);
            FileOutputStream fileOutputStream = new FileOutputStream(accountConfigFile);
            fileOutputStream.write(obtain.marshall());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        obtain.recycle();
    }

    private void readAllAccounts() {
        File accountConfigFile = VEnvironment.getAccountConfigFile();
        refreshAuthenticatorCache(null);
        if (accountConfigFile.exists()) {
            this.accountsByUserId.clear();
            Parcel obtain = Parcel.obtain();
            try {
                FileInputStream fileInputStream = new FileInputStream(accountConfigFile);
                byte[] bArr = new byte[((int) accountConfigFile.length())];
                int read = fileInputStream.read(bArr);
                fileInputStream.close();
                if (read == bArr.length) {
                    obtain.unmarshall(bArr, 0, bArr.length);
                    obtain.setDataPosition(0);
                    obtain.readInt();
                    int readInt = obtain.readInt();
                    boolean z = false;
                    while (true) {
                        int i = readInt - 1;
                        if (readInt <= 0) {
                            break;
                        }
                        VAccount vAccount = new VAccount(obtain);
                        String str = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Reading account : ");
                        sb.append(vAccount.type);
                        VLog.m86d(str, sb.toString(), new Object[0]);
                        if (((AuthenticatorInfo) this.cache.authenticators.get(vAccount.type)) != null) {
                            List list = (List) this.accountsByUserId.get(vAccount.userId);
                            if (list == null) {
                                list = new ArrayList();
                                this.accountsByUserId.put(vAccount.userId, list);
                            }
                            list.add(vAccount);
                        } else {
                            z = true;
                        }
                        readInt = i;
                    }
                    this.lastAccountChangeTime = obtain.readLong();
                    if (z) {
                        saveAllAccounts();
                    }
                    obtain.recycle();
                    return;
                }
                throw new IOException(String.format(Locale.ENGLISH, "Expect length %d, but got %d.", new Object[]{Integer.valueOf(bArr.length), Integer.valueOf(read)}));
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                obtain.recycle();
                throw th;
            }
        }
    }

    /* access modifiers changed from: private */
    public VAccount getAccount(int i, String str, String str2) {
        List<VAccount> list = (List) this.accountsByUserId.get(i);
        if (list != null) {
            for (VAccount vAccount : list) {
                if (TextUtils.equals(vAccount.name, str) && TextUtils.equals(vAccount.type, str2)) {
                    return vAccount;
                }
            }
        }
        return null;
    }

    public void refreshAuthenticatorCache(String str) {
        this.cache.authenticators.clear();
        Intent intent = new Intent("android.accounts.AccountAuthenticator");
        if (str != null) {
            intent.setPackage(str);
        }
        generateServicesMap(VPackageManagerService.get().queryIntentServices(intent, null, 128, 0), this.cache.authenticators, new RegisteredServicesParser());
    }

    private void generateServicesMap(List<ResolveInfo> list, Map<String, AuthenticatorInfo> map, RegisteredServicesParser registeredServicesParser) {
        for (ResolveInfo resolveInfo : list) {
            XmlResourceParser parser = registeredServicesParser.getParser(this.mContext, resolveInfo.serviceInfo, "android.accounts.AccountAuthenticator");
            if (parser != null) {
                try {
                    AttributeSet asAttributeSet = Xml.asAttributeSet(parser);
                    while (true) {
                        int next = parser.next();
                        if (next == 1 || next == 2) {
                        }
                    }
                    if ("account-authenticator".equals(parser.getName())) {
                        AuthenticatorDescription parseAuthenticatorDescription = parseAuthenticatorDescription(registeredServicesParser.getResources(this.mContext, resolveInfo.serviceInfo.applicationInfo), resolveInfo.serviceInfo.packageName, asAttributeSet);
                        if (parseAuthenticatorDescription != null) {
                            map.put(parseAuthenticatorDescription.type, new AuthenticatorInfo(parseAuthenticatorDescription, resolveInfo.serviceInfo));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
