package android.accounts;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IAccountAuthenticator extends IInterface {

    public static abstract class Stub extends Binder implements IAccountAuthenticator {
        private static final String DESCRIPTOR = "android.accounts.IAccountAuthenticator";
        static final int TRANSACTION_addAccount = 1;
        static final int TRANSACTION_addAccountFromCredentials = 10;
        static final int TRANSACTION_confirmCredentials = 2;
        static final int TRANSACTION_editProperties = 6;
        static final int TRANSACTION_getAccountCredentialsForCloning = 9;
        static final int TRANSACTION_getAccountRemovalAllowed = 8;
        static final int TRANSACTION_getAuthToken = 3;
        static final int TRANSACTION_getAuthTokenLabel = 4;
        static final int TRANSACTION_hasFeatures = 7;
        static final int TRANSACTION_updateCredentials = 5;

        private static class Proxy implements IAccountAuthenticator {
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

            public void addAccount(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str, String str2, String[] strArr, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeStringArray(strArr);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void confirmCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
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
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getAuthToken(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
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
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getAuthTokenLabel(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
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
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void editProperties(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
                    obtain.writeString(str);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void hasFeatures(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String[] strArr) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStringArray(strArr);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getAccountRemovalAllowed(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void getAccountCredentialsForCloning(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
                    if (account != null) {
                        obtain.writeInt(1);
                        account.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void addAccountFromCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAccountAuthenticatorResponse != null ? iAccountAuthenticatorResponse.asBinder() : null);
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
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
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

        public static IAccountAuthenticator asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IAccountAuthenticator)) {
                return new Proxy(iBinder);
            }
            return (IAccountAuthenticator) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r1v1 */
        /* JADX WARNING: type inference failed for: r1v2 */
        /* JADX WARNING: type inference failed for: r8v0, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v4, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v5, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v7, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v8, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v11, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v12, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v15, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v16, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r1v18, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r1v19, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r1v21, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r1v22, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r1v24, types: [android.accounts.Account] */
        /* JADX WARNING: type inference failed for: r1v25, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v27, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r1v28 */
        /* JADX WARNING: type inference failed for: r1v29 */
        /* JADX WARNING: type inference failed for: r1v30 */
        /* JADX WARNING: type inference failed for: r1v31 */
        /* JADX WARNING: type inference failed for: r1v32 */
        /* JADX WARNING: type inference failed for: r1v33 */
        /* JADX WARNING: type inference failed for: r1v34 */
        /* JADX WARNING: type inference failed for: r1v35 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r1v1
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], android.os.Bundle, android.accounts.Account]
          uses: [?[OBJECT, ARRAY], android.os.Bundle, android.accounts.Account]
          mth insns count: 158
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
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
         */
        /* JADX WARNING: Unknown variable types count: 10 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r10, android.os.Parcel r11, android.os.Parcel r12, int r13) throws android.os.RemoteException {
            /*
                r9 = this;
                java.lang.String r0 = "android.accounts.IAccountAuthenticator"
                r1 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r2 = 1
                if (r10 == r1) goto L_0x019f
                r1 = 0
                switch(r10) {
                    case 1: goto L_0x0170;
                    case 2: goto L_0x013f;
                    case 3: goto L_0x010a;
                    case 4: goto L_0x00f4;
                    case 5: goto L_0x00bf;
                    case 6: goto L_0x00a9;
                    case 7: goto L_0x0084;
                    case 8: goto L_0x0063;
                    case 9: goto L_0x0042;
                    case 10: goto L_0x0011;
                    default: goto L_0x000c;
                }
            L_0x000c:
                boolean r10 = super.onTransact(r10, r11, r12, r13)
                return r10
            L_0x0011:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x002b
                android.os.Parcelable$Creator r13 = android.accounts.Account.CREATOR
                java.lang.Object r13 = r13.createFromParcel(r11)
                android.accounts.Account r13 = (android.accounts.Account) r13
                goto L_0x002c
            L_0x002b:
                r13 = r1
            L_0x002c:
                int r0 = r11.readInt()
                if (r0 == 0) goto L_0x003b
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r11 = r0.createFromParcel(r11)
                r1 = r11
                android.os.Bundle r1 = (android.os.Bundle) r1
            L_0x003b:
                r9.addAccountFromCredentials(r10, r13, r1)
                r12.writeNoException()
                return r2
            L_0x0042:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x005c
                android.os.Parcelable$Creator r13 = android.accounts.Account.CREATOR
                java.lang.Object r11 = r13.createFromParcel(r11)
                r1 = r11
                android.accounts.Account r1 = (android.accounts.Account) r1
            L_0x005c:
                r9.getAccountCredentialsForCloning(r10, r1)
                r12.writeNoException()
                return r2
            L_0x0063:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x007d
                android.os.Parcelable$Creator r13 = android.accounts.Account.CREATOR
                java.lang.Object r11 = r13.createFromParcel(r11)
                r1 = r11
                android.accounts.Account r1 = (android.accounts.Account) r1
            L_0x007d:
                r9.getAccountRemovalAllowed(r10, r1)
                r12.writeNoException()
                return r2
            L_0x0084:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x009e
                android.os.Parcelable$Creator r13 = android.accounts.Account.CREATOR
                java.lang.Object r13 = r13.createFromParcel(r11)
                r1 = r13
                android.accounts.Account r1 = (android.accounts.Account) r1
            L_0x009e:
                java.lang.String[] r11 = r11.createStringArray()
                r9.hasFeatures(r10, r1, r11)
                r12.writeNoException()
                return r2
            L_0x00a9:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                java.lang.String r11 = r11.readString()
                r9.editProperties(r10, r11)
                r12.writeNoException()
                return r2
            L_0x00bf:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x00d9
                android.os.Parcelable$Creator r13 = android.accounts.Account.CREATOR
                java.lang.Object r13 = r13.createFromParcel(r11)
                android.accounts.Account r13 = (android.accounts.Account) r13
                goto L_0x00da
            L_0x00d9:
                r13 = r1
            L_0x00da:
                java.lang.String r0 = r11.readString()
                int r3 = r11.readInt()
                if (r3 == 0) goto L_0x00ed
                android.os.Parcelable$Creator r1 = android.os.Bundle.CREATOR
                java.lang.Object r11 = r1.createFromParcel(r11)
                r1 = r11
                android.os.Bundle r1 = (android.os.Bundle) r1
            L_0x00ed:
                r9.updateCredentials(r10, r13, r0, r1)
                r12.writeNoException()
                return r2
            L_0x00f4:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                java.lang.String r11 = r11.readString()
                r9.getAuthTokenLabel(r10, r11)
                r12.writeNoException()
                return r2
            L_0x010a:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x0124
                android.os.Parcelable$Creator r13 = android.accounts.Account.CREATOR
                java.lang.Object r13 = r13.createFromParcel(r11)
                android.accounts.Account r13 = (android.accounts.Account) r13
                goto L_0x0125
            L_0x0124:
                r13 = r1
            L_0x0125:
                java.lang.String r0 = r11.readString()
                int r3 = r11.readInt()
                if (r3 == 0) goto L_0x0138
                android.os.Parcelable$Creator r1 = android.os.Bundle.CREATOR
                java.lang.Object r11 = r1.createFromParcel(r11)
                r1 = r11
                android.os.Bundle r1 = (android.os.Bundle) r1
            L_0x0138:
                r9.getAuthToken(r10, r13, r0, r1)
                r12.writeNoException()
                return r2
            L_0x013f:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r10 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x0159
                android.os.Parcelable$Creator r13 = android.accounts.Account.CREATOR
                java.lang.Object r13 = r13.createFromParcel(r11)
                android.accounts.Account r13 = (android.accounts.Account) r13
                goto L_0x015a
            L_0x0159:
                r13 = r1
            L_0x015a:
                int r0 = r11.readInt()
                if (r0 == 0) goto L_0x0169
                android.os.Parcelable$Creator r0 = android.os.Bundle.CREATOR
                java.lang.Object r11 = r0.createFromParcel(r11)
                r1 = r11
                android.os.Bundle r1 = (android.os.Bundle) r1
            L_0x0169:
                r9.confirmCredentials(r10, r13, r1)
                r12.writeNoException()
                return r2
            L_0x0170:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.accounts.IAccountAuthenticatorResponse r4 = android.accounts.IAccountAuthenticatorResponse.Stub.asInterface(r10)
                java.lang.String r5 = r11.readString()
                java.lang.String r6 = r11.readString()
                java.lang.String[] r7 = r11.createStringArray()
                int r10 = r11.readInt()
                if (r10 == 0) goto L_0x0196
                android.os.Parcelable$Creator r10 = android.os.Bundle.CREATOR
                java.lang.Object r10 = r10.createFromParcel(r11)
                r1 = r10
                android.os.Bundle r1 = (android.os.Bundle) r1
            L_0x0196:
                r8 = r1
                r3 = r9
                r3.addAccount(r4, r5, r6, r7, r8)
                r12.writeNoException()
                return r2
            L_0x019f:
                r12.writeString(r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: android.accounts.IAccountAuthenticator.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void addAccount(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str, String str2, String[] strArr, Bundle bundle) throws RemoteException;

    void addAccountFromCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, Bundle bundle) throws RemoteException;

    void confirmCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, Bundle bundle) throws RemoteException;

    void editProperties(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str) throws RemoteException;

    void getAccountCredentialsForCloning(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account) throws RemoteException;

    void getAccountRemovalAllowed(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account) throws RemoteException;

    void getAuthToken(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str, Bundle bundle) throws RemoteException;

    void getAuthTokenLabel(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, String str) throws RemoteException;

    void hasFeatures(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String[] strArr) throws RemoteException;

    void updateCredentials(IAccountAuthenticatorResponse iAccountAuthenticatorResponse, Account account, String str, Bundle bundle) throws RemoteException;
}
