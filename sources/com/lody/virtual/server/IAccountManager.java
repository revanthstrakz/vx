package com.lody.virtual.server;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.accounts.IAccountManagerResponse;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAccountManager extends IInterface {

    public static abstract class Stub extends Binder implements IAccountManager {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IAccountManager";
        static final int TRANSACTION_accountAuthenticated = 22;
        static final int TRANSACTION_addAccount = 16;
        static final int TRANSACTION_addAccountExplicitly = 17;
        static final int TRANSACTION_clearPassword = 21;
        static final int TRANSACTION_confirmCredentials = 15;
        static final int TRANSACTION_editProperties = 11;
        static final int TRANSACTION_getAccounts = 4;
        static final int TRANSACTION_getAccountsByFeatures = 2;
        static final int TRANSACTION_getAuthToken = 5;
        static final int TRANSACTION_getAuthTokenLabel = 12;
        static final int TRANSACTION_getAuthenticatorTypes = 1;
        static final int TRANSACTION_getPassword = 14;
        static final int TRANSACTION_getPreviousName = 3;
        static final int TRANSACTION_getUserData = 13;
        static final int TRANSACTION_hasFeatures = 9;
        static final int TRANSACTION_invalidateAuthToken = 23;
        static final int TRANSACTION_peekAuthToken = 24;
        static final int TRANSACTION_removeAccount = 20;
        static final int TRANSACTION_removeAccountExplicitly = 18;
        static final int TRANSACTION_renameAccount = 19;
        static final int TRANSACTION_setAuthToken = 7;
        static final int TRANSACTION_setPassword = 6;
        static final int TRANSACTION_setUserData = 8;
        static final int TRANSACTION_updateCredentials = 10;

        private static class Proxy implements IAccountManager {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public AuthenticatorDescription[] getAuthenticatorTypes(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return (AuthenticatorDescription[]) obtain2.createTypedArray(AuthenticatorDescription.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getAccountsByFeatures(int i, IAccountManagerResponse iAccountManagerResponse, String str, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    obtain.writeString(str);
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getPreviousName(int i, Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public Account[] getAccounts(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return (Account[]) obtain2.createTypedArray(Account.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getAuthToken(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, boolean z2, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    obtain.writeInt(z2 ? 1 : 0);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setPassword(int i, Account account, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setAuthToken(int i, Account account, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setUserData(int i, Account account, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void hasFeatures(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateCredentials(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void editProperties(int i, IAccountManagerResponse iAccountManagerResponse, String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getAuthTokenLabel(int i, IAccountManagerResponse iAccountManagerResponse, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getUserData(int i, Account account, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getPassword(int i, Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void confirmCredentials(int i, IAccountManagerResponse iAccountManagerResponse, Account account, Bundle bundle, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void addAccount(int i, IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeStringArray(strArr);
                    obtain.writeInt(z ? 1 : 0);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean addAccountExplicitly(int i, Account account, String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = true;
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean removeAccountExplicitly(int i, Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = true;
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void renameAccount(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void removeAccount(int i, IAccountManagerResponse iAccountManagerResponse, Account account, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeStrongBinder(iAccountManagerResponse != null ? iAccountManagerResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void clearPassword(int i, Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean accountAuthenticated(int i, Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    boolean z = true;
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() == 0) {
                        z = false;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void invalidateAuthToken(int i, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String peekAuthToken(int i, Account account, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAccountManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IAccountManager)) {
                return new Proxy(iBinder);
            }
            return (IAccountManager) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r4v0 */
        /* JADX WARNING: type inference failed for: r4v1, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v3, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v5, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v7, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v8, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v10, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v11, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v13, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v14, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v16, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v19, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v21, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v22, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v24, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v25, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r0v48, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r4v26 */
        /* JADX WARNING: type inference failed for: r4v28, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r4v31, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r4v32, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v34, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v35, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v37, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v38, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v41, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v42, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v44, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v45, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v47, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v48, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v50, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r4v51 */
        /* JADX WARNING: type inference failed for: r4v52 */
        /* JADX WARNING: type inference failed for: r4v53 */
        /* JADX WARNING: type inference failed for: r4v54 */
        /* JADX WARNING: type inference failed for: r4v55 */
        /* JADX WARNING: type inference failed for: r4v56 */
        /* JADX WARNING: type inference failed for: r4v57 */
        /* JADX WARNING: type inference failed for: r4v58 */
        /* JADX WARNING: type inference failed for: r4v59 */
        /* JADX WARNING: type inference failed for: r4v60 */
        /* JADX WARNING: type inference failed for: r4v61 */
        /* JADX WARNING: type inference failed for: r4v62 */
        /* JADX WARNING: type inference failed for: r4v63 */
        /* JADX WARNING: type inference failed for: r4v64 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r4v0
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], android.accounts.Account, ?[OBJECT, ARRAY], android.os.Bundle]
          uses: [android.accounts.Account, android.os.Bundle]
          mth insns count: 390
        	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
        	at jadx.core.ProcessClass.process(ProcessClass.java:30)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
        	at jadx.core.ProcessClass.process(ProcessClass.java:35)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
         */
        /* JADX WARNING: Unknown variable types count: 16 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r15, android.os.Parcel r16, android.os.Parcel r17, int r18) throws android.os.RemoteException {
            /*
                r14 = this;
                r8 = r14
                r0 = r15
                r1 = r16
                r9 = r17
                java.lang.String r2 = "com.lody.virtual.server.IAccountManager"
                r3 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r10 = 1
                if (r0 == r3) goto L_0x03e5
                r3 = 0
                r4 = 0
                switch(r0) {
                    case 1: goto L_0x03d3;
                    case 2: goto L_0x03b5;
                    case 3: goto L_0x0394;
                    case 4: goto L_0x037e;
                    case 5: goto L_0x0328;
                    case 6: goto L_0x0307;
                    case 7: goto L_0x02e2;
                    case 8: goto L_0x02bd;
                    case 9: goto L_0x0294;
                    case 10: goto L_0x0248;
                    case 11: goto L_0x0227;
                    case 12: goto L_0x0209;
                    case 13: goto L_0x01e4;
                    case 14: goto L_0x01c3;
                    case 15: goto L_0x017f;
                    case 16: goto L_0x013b;
                    case 17: goto L_0x0106;
                    case 18: goto L_0x00e5;
                    case 19: goto L_0x00bc;
                    case 20: goto L_0x0091;
                    case 21: goto L_0x0074;
                    case 22: goto L_0x0053;
                    case 23: goto L_0x003d;
                    case 24: goto L_0x0018;
                    default: goto L_0x0013;
                }
            L_0x0013:
                boolean r0 = super.onTransact(r15, r16, r17, r18)
                return r0
            L_0x0018:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x002e
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r1)
                r4 = r2
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x002e:
                java.lang.String r1 = r16.readString()
                java.lang.String r0 = r14.peekAuthToken(r0, r4, r1)
                r17.writeNoException()
                r9.writeString(r0)
                return r10
            L_0x003d:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                java.lang.String r2 = r16.readString()
                java.lang.String r1 = r16.readString()
                r14.invalidateAuthToken(r0, r2, r1)
                r17.writeNoException()
                return r10
            L_0x0053:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x0069
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r1 = r2.createFromParcel(r1)
                r4 = r1
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x0069:
                boolean r0 = r14.accountAuthenticated(r0, r4)
                r17.writeNoException()
                r9.writeInt(r0)
                return r10
            L_0x0074:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x008a
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r1 = r2.createFromParcel(r1)
                r4 = r1
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x008a:
                r14.clearPassword(r0, r4)
                r17.writeNoException()
                return r10
            L_0x0091:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                android.os.IBinder r2 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r2 = android.accounts.IAccountManagerResponse.Stub.asInterface(r2)
                int r5 = r16.readInt()
                if (r5 == 0) goto L_0x00ae
                android.os.Parcelable$Creator r4 = android.accounts.Account.CREATOR
                java.lang.Object r4 = r4.createFromParcel(r1)
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x00ae:
                int r1 = r16.readInt()
                if (r1 == 0) goto L_0x00b5
                r3 = 1
            L_0x00b5:
                r14.removeAccount(r0, r2, r4, r3)
                r17.writeNoException()
                return r10
            L_0x00bc:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                android.os.IBinder r2 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r2 = android.accounts.IAccountManagerResponse.Stub.asInterface(r2)
                int r3 = r16.readInt()
                if (r3 == 0) goto L_0x00da
                android.os.Parcelable$Creator r3 = android.accounts.Account.CREATOR
                java.lang.Object r3 = r3.createFromParcel(r1)
                r4 = r3
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x00da:
                java.lang.String r1 = r16.readString()
                r14.renameAccount(r0, r2, r4, r1)
                r17.writeNoException()
                return r10
            L_0x00e5:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x00fb
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r1 = r2.createFromParcel(r1)
                r4 = r1
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x00fb:
                boolean r0 = r14.removeAccountExplicitly(r0, r4)
                r17.writeNoException()
                r9.writeInt(r0)
                return r10
            L_0x0106:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x011c
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r1)
                android.accounts.Account r2 = (android.accounts.Account) r2
                goto L_0x011d
            L_0x011c:
                r2 = r4
            L_0x011d:
                java.lang.String r3 = r16.readString()
                int r5 = r16.readInt()
                if (r5 == 0) goto L_0x0130
                android.os.Parcelable$Creator r4 = android.os.Bundle.CREATOR
                java.lang.Object r1 = r4.createFromParcel(r1)
                r4 = r1
                android.os.Bundle r4 = (android.os.Bundle) r4
            L_0x0130:
                boolean r0 = r14.addAccountExplicitly(r0, r2, r3, r4)
                r17.writeNoException()
                r9.writeInt(r0)
                return r10
            L_0x013b:
                r1.enforceInterface(r2)
                int r2 = r16.readInt()
                android.os.IBinder r0 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r5 = android.accounts.IAccountManagerResponse.Stub.asInterface(r0)
                java.lang.String r6 = r16.readString()
                java.lang.String r7 = r16.readString()
                java.lang.String[] r11 = r16.createStringArray()
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x015e
                r12 = 1
                goto L_0x015f
            L_0x015e:
                r12 = 0
            L_0x015f:
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x016f
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r13 = r0
                goto L_0x0170
            L_0x016f:
                r13 = r4
            L_0x0170:
                r0 = r14
                r1 = r2
                r2 = r5
                r3 = r6
                r4 = r7
                r5 = r11
                r6 = r12
                r7 = r13
                r0.addAccount(r1, r2, r3, r4, r5, r6, r7)
                r17.writeNoException()
                return r10
            L_0x017f:
                r1.enforceInterface(r2)
                int r2 = r16.readInt()
                android.os.IBinder r0 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r5 = android.accounts.IAccountManagerResponse.Stub.asInterface(r0)
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x019e
                android.os.Parcelable$Creator r0 = android.accounts.Account.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                android.accounts.Account r0 = (android.accounts.Account) r0
                r6 = r0
                goto L_0x019f
            L_0x019e:
                r6 = r4
            L_0x019f:
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x01ae
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r4 = r0
            L_0x01ae:
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x01b6
                r7 = 1
                goto L_0x01b7
            L_0x01b6:
                r7 = 0
            L_0x01b7:
                r0 = r14
                r1 = r2
                r2 = r5
                r3 = r6
                r5 = r7
                r0.confirmCredentials(r1, r2, r3, r4, r5)
                r17.writeNoException()
                return r10
            L_0x01c3:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x01d9
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r1 = r2.createFromParcel(r1)
                r4 = r1
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x01d9:
                java.lang.String r0 = r14.getPassword(r0, r4)
                r17.writeNoException()
                r9.writeString(r0)
                return r10
            L_0x01e4:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x01fa
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r1)
                r4 = r2
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x01fa:
                java.lang.String r1 = r16.readString()
                java.lang.String r0 = r14.getUserData(r0, r4, r1)
                r17.writeNoException()
                r9.writeString(r0)
                return r10
            L_0x0209:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                android.os.IBinder r2 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r2 = android.accounts.IAccountManagerResponse.Stub.asInterface(r2)
                java.lang.String r3 = r16.readString()
                java.lang.String r1 = r16.readString()
                r14.getAuthTokenLabel(r0, r2, r3, r1)
                r17.writeNoException()
                return r10
            L_0x0227:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                android.os.IBinder r2 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r2 = android.accounts.IAccountManagerResponse.Stub.asInterface(r2)
                java.lang.String r4 = r16.readString()
                int r1 = r16.readInt()
                if (r1 == 0) goto L_0x0241
                r3 = 1
            L_0x0241:
                r14.editProperties(r0, r2, r4, r3)
                r17.writeNoException()
                return r10
            L_0x0248:
                r1.enforceInterface(r2)
                int r2 = r16.readInt()
                android.os.IBinder r0 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r5 = android.accounts.IAccountManagerResponse.Stub.asInterface(r0)
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x0267
                android.os.Parcelable$Creator r0 = android.accounts.Account.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                android.accounts.Account r0 = (android.accounts.Account) r0
                r6 = r0
                goto L_0x0268
            L_0x0267:
                r6 = r4
            L_0x0268:
                java.lang.String r7 = r16.readString()
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x0274
                r11 = 1
                goto L_0x0275
            L_0x0274:
                r11 = 0
            L_0x0275:
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x0285
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r12 = r0
                goto L_0x0286
            L_0x0285:
                r12 = r4
            L_0x0286:
                r0 = r14
                r1 = r2
                r2 = r5
                r3 = r6
                r4 = r7
                r5 = r11
                r6 = r12
                r0.updateCredentials(r1, r2, r3, r4, r5, r6)
                r17.writeNoException()
                return r10
            L_0x0294:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                android.os.IBinder r2 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r2 = android.accounts.IAccountManagerResponse.Stub.asInterface(r2)
                int r3 = r16.readInt()
                if (r3 == 0) goto L_0x02b2
                android.os.Parcelable$Creator r3 = android.accounts.Account.CREATOR
                java.lang.Object r3 = r3.createFromParcel(r1)
                r4 = r3
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x02b2:
                java.lang.String[] r1 = r16.createStringArray()
                r14.hasFeatures(r0, r2, r4, r1)
                r17.writeNoException()
                return r10
            L_0x02bd:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x02d3
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r1)
                r4 = r2
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x02d3:
                java.lang.String r2 = r16.readString()
                java.lang.String r1 = r16.readString()
                r14.setUserData(r0, r4, r2, r1)
                r17.writeNoException()
                return r10
            L_0x02e2:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x02f8
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r1)
                r4 = r2
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x02f8:
                java.lang.String r2 = r16.readString()
                java.lang.String r1 = r16.readString()
                r14.setAuthToken(r0, r4, r2, r1)
                r17.writeNoException()
                return r10
            L_0x0307:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x031d
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r2 = r2.createFromParcel(r1)
                r4 = r2
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x031d:
                java.lang.String r1 = r16.readString()
                r14.setPassword(r0, r4, r1)
                r17.writeNoException()
                return r10
            L_0x0328:
                r1.enforceInterface(r2)
                int r2 = r16.readInt()
                android.os.IBinder r0 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r5 = android.accounts.IAccountManagerResponse.Stub.asInterface(r0)
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x0347
                android.os.Parcelable$Creator r0 = android.accounts.Account.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                android.accounts.Account r0 = (android.accounts.Account) r0
                r6 = r0
                goto L_0x0348
            L_0x0347:
                r6 = r4
            L_0x0348:
                java.lang.String r7 = r16.readString()
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x0354
                r11 = 1
                goto L_0x0355
            L_0x0354:
                r11 = 0
            L_0x0355:
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x035d
                r12 = 1
                goto L_0x035e
            L_0x035d:
                r12 = 0
            L_0x035e:
                int r0 = r16.readInt()
                if (r0 == 0) goto L_0x036e
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r1)
                android.os.Bundle r0 = (android.os.Bundle) r0
                r13 = r0
                goto L_0x036f
            L_0x036e:
                r13 = r4
            L_0x036f:
                r0 = r14
                r1 = r2
                r2 = r5
                r3 = r6
                r4 = r7
                r5 = r11
                r6 = r12
                r7 = r13
                r0.getAuthToken(r1, r2, r3, r4, r5, r6, r7)
                r17.writeNoException()
                return r10
            L_0x037e:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                java.lang.String r1 = r16.readString()
                android.accounts.Account[] r0 = r14.getAccounts(r0, r1)
                r17.writeNoException()
                r9.writeTypedArray(r0, r10)
                return r10
            L_0x0394:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                int r2 = r16.readInt()
                if (r2 == 0) goto L_0x03aa
                android.os.Parcelable$Creator r2 = android.accounts.Account.CREATOR
                java.lang.Object r1 = r2.createFromParcel(r1)
                r4 = r1
                android.accounts.Account r4 = (android.accounts.Account) r4
            L_0x03aa:
                java.lang.String r0 = r14.getPreviousName(r0, r4)
                r17.writeNoException()
                r9.writeString(r0)
                return r10
            L_0x03b5:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                android.os.IBinder r2 = r16.readStrongBinder()
                android.accounts.IAccountManagerResponse r2 = android.accounts.IAccountManagerResponse.Stub.asInterface(r2)
                java.lang.String r3 = r16.readString()
                java.lang.String[] r1 = r16.createStringArray()
                r14.getAccountsByFeatures(r0, r2, r3, r1)
                r17.writeNoException()
                return r10
            L_0x03d3:
                r1.enforceInterface(r2)
                int r0 = r16.readInt()
                android.accounts.AuthenticatorDescription[] r0 = r14.getAuthenticatorTypes(r0)
                r17.writeNoException()
                r9.writeTypedArray(r0, r10)
                return r10
            L_0x03e5:
                r9.writeString(r2)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.IAccountManager.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    boolean accountAuthenticated(int i, Account account) throws RemoteException;

    void addAccount(int i, IAccountManagerResponse iAccountManagerResponse, String str, String str2, String[] strArr, boolean z, Bundle bundle) throws RemoteException;

    boolean addAccountExplicitly(int i, Account account, String str, Bundle bundle) throws RemoteException;

    void clearPassword(int i, Account account) throws RemoteException;

    void confirmCredentials(int i, IAccountManagerResponse iAccountManagerResponse, Account account, Bundle bundle, boolean z) throws RemoteException;

    void editProperties(int i, IAccountManagerResponse iAccountManagerResponse, String str, boolean z) throws RemoteException;

    Account[] getAccounts(int i, String str) throws RemoteException;

    void getAccountsByFeatures(int i, IAccountManagerResponse iAccountManagerResponse, String str, String[] strArr) throws RemoteException;

    void getAuthToken(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, boolean z2, Bundle bundle) throws RemoteException;

    void getAuthTokenLabel(int i, IAccountManagerResponse iAccountManagerResponse, String str, String str2) throws RemoteException;

    AuthenticatorDescription[] getAuthenticatorTypes(int i) throws RemoteException;

    String getPassword(int i, Account account) throws RemoteException;

    String getPreviousName(int i, Account account) throws RemoteException;

    String getUserData(int i, Account account, String str) throws RemoteException;

    void hasFeatures(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String[] strArr) throws RemoteException;

    void invalidateAuthToken(int i, String str, String str2) throws RemoteException;

    String peekAuthToken(int i, Account account, String str) throws RemoteException;

    void removeAccount(int i, IAccountManagerResponse iAccountManagerResponse, Account account, boolean z) throws RemoteException;

    boolean removeAccountExplicitly(int i, Account account) throws RemoteException;

    void renameAccount(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str) throws RemoteException;

    void setAuthToken(int i, Account account, String str, String str2) throws RemoteException;

    void setPassword(int i, Account account, String str) throws RemoteException;

    void setUserData(int i, Account account, String str, String str2) throws RemoteException;

    void updateCredentials(int i, IAccountManagerResponse iAccountManagerResponse, Account account, String str, boolean z, Bundle bundle) throws RemoteException;
}
