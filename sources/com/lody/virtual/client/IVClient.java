package com.lody.virtual.client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.lody.virtual.remote.PendingResultData;

public interface IVClient extends IInterface {

    public static abstract class Stub extends Binder implements IVClient {
        private static final String DESCRIPTOR = "com.lody.virtual.client.IVClient";
        static final int TRANSACTION_acquireProviderClient = 5;
        static final int TRANSACTION_createProxyService = 4;
        static final int TRANSACTION_finishActivity = 3;
        static final int TRANSACTION_getAppThread = 6;
        static final int TRANSACTION_getDebugInfo = 8;
        static final int TRANSACTION_getToken = 7;
        static final int TRANSACTION_scheduleNewIntent = 2;
        static final int TRANSACTION_scheduleReceiver = 1;

        private static class Proxy implements IVClient {
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

            public void scheduleReceiver(String str, ComponentName componentName, Intent intent, PendingResultData pendingResultData) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    if (pendingResultData != null) {
                        obtain.writeInt(1);
                        pendingResultData.writeToParcel(obtain, 0);
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

            public void scheduleNewIntent(String str, IBinder iBinder, Intent intent) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeStrongBinder(iBinder);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
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

            public void finishActivity(IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder createProxyService(ComponentName componentName, IBinder iBinder) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (componentName != null) {
                        obtain.writeInt(1);
                        componentName.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iBinder);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder acquireProviderClient(ProviderInfo providerInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (providerInfo != null) {
                        obtain.writeInt(1);
                        providerInfo.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder getAppThread() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IBinder getToken() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readStrongBinder();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getDebugInfo() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, obtain, obtain2, 0);
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

        public static IVClient asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IVClient)) {
                return new Proxy(iBinder);
            }
            return (IVClient) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r1v1 */
        /* JADX WARNING: type inference failed for: r1v2, types: [com.lody.virtual.remote.PendingResultData] */
        /* JADX WARNING: type inference failed for: r1v5, types: [com.lody.virtual.remote.PendingResultData] */
        /* JADX WARNING: type inference failed for: r1v6, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v8, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v9, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v11, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v12, types: [android.content.pm.ProviderInfo] */
        /* JADX WARNING: type inference failed for: r1v14, types: [android.content.pm.ProviderInfo] */
        /* JADX WARNING: type inference failed for: r1v15 */
        /* JADX WARNING: type inference failed for: r1v16 */
        /* JADX WARNING: type inference failed for: r1v17 */
        /* JADX WARNING: type inference failed for: r1v18 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r1v1
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], android.content.Intent, com.lody.virtual.remote.PendingResultData, android.content.ComponentName, android.content.pm.ProviderInfo]
          uses: [com.lody.virtual.remote.PendingResultData, android.content.Intent, android.content.ComponentName, android.content.pm.ProviderInfo]
          mth insns count: 90
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
        /* JADX WARNING: Unknown variable types count: 5 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) throws android.os.RemoteException {
            /*
                r4 = this;
                java.lang.String r0 = "com.lody.virtual.client.IVClient"
                r1 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r2 = 1
                if (r5 == r1) goto L_0x00e5
                r1 = 0
                switch(r5) {
                    case 1: goto L_0x00a8;
                    case 2: goto L_0x0087;
                    case 3: goto L_0x0079;
                    case 4: goto L_0x0058;
                    case 5: goto L_0x003b;
                    case 6: goto L_0x002d;
                    case 7: goto L_0x001f;
                    case 8: goto L_0x0011;
                    default: goto L_0x000c;
                }
            L_0x000c:
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0011:
                r6.enforceInterface(r0)
                java.lang.String r5 = r4.getDebugInfo()
                r7.writeNoException()
                r7.writeString(r5)
                return r2
            L_0x001f:
                r6.enforceInterface(r0)
                android.os.IBinder r5 = r4.getToken()
                r7.writeNoException()
                r7.writeStrongBinder(r5)
                return r2
            L_0x002d:
                r6.enforceInterface(r0)
                android.os.IBinder r5 = r4.getAppThread()
                r7.writeNoException()
                r7.writeStrongBinder(r5)
                return r2
            L_0x003b:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x004d
                android.os.Parcelable$Creator r5 = android.content.pm.ProviderInfo.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.pm.ProviderInfo r1 = (android.content.pm.ProviderInfo) r1
            L_0x004d:
                android.os.IBinder r5 = r4.acquireProviderClient(r1)
                r7.writeNoException()
                r7.writeStrongBinder(r5)
                return r2
            L_0x0058:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x006a
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.ComponentName r1 = (android.content.ComponentName) r1
            L_0x006a:
                android.os.IBinder r5 = r6.readStrongBinder()
                android.os.IBinder r5 = r4.createProxyService(r1, r5)
                r7.writeNoException()
                r7.writeStrongBinder(r5)
                return r2
            L_0x0079:
                r6.enforceInterface(r0)
                android.os.IBinder r5 = r6.readStrongBinder()
                r4.finishActivity(r5)
                r7.writeNoException()
                return r2
            L_0x0087:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                android.os.IBinder r8 = r6.readStrongBinder()
                int r0 = r6.readInt()
                if (r0 == 0) goto L_0x00a1
                android.os.Parcelable$Creator r0 = android.content.Intent.CREATOR
                java.lang.Object r6 = r0.createFromParcel(r6)
                r1 = r6
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x00a1:
                r4.scheduleNewIntent(r5, r8, r1)
                r7.writeNoException()
                return r2
            L_0x00a8:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                if (r8 == 0) goto L_0x00be
                android.os.Parcelable$Creator r8 = android.content.ComponentName.CREATOR
                java.lang.Object r8 = r8.createFromParcel(r6)
                android.content.ComponentName r8 = (android.content.ComponentName) r8
                goto L_0x00bf
            L_0x00be:
                r8 = r1
            L_0x00bf:
                int r0 = r6.readInt()
                if (r0 == 0) goto L_0x00ce
                android.os.Parcelable$Creator r0 = android.content.Intent.CREATOR
                java.lang.Object r0 = r0.createFromParcel(r6)
                android.content.Intent r0 = (android.content.Intent) r0
                goto L_0x00cf
            L_0x00ce:
                r0 = r1
            L_0x00cf:
                int r3 = r6.readInt()
                if (r3 == 0) goto L_0x00de
                android.os.Parcelable$Creator<com.lody.virtual.remote.PendingResultData> r1 = com.lody.virtual.remote.PendingResultData.CREATOR
                java.lang.Object r6 = r1.createFromParcel(r6)
                r1 = r6
                com.lody.virtual.remote.PendingResultData r1 = (com.lody.virtual.remote.PendingResultData) r1
            L_0x00de:
                r4.scheduleReceiver(r5, r8, r0, r1)
                r7.writeNoException()
                return r2
            L_0x00e5:
                r7.writeString(r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.IVClient.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    IBinder acquireProviderClient(ProviderInfo providerInfo) throws RemoteException;

    IBinder createProxyService(ComponentName componentName, IBinder iBinder) throws RemoteException;

    void finishActivity(IBinder iBinder) throws RemoteException;

    IBinder getAppThread() throws RemoteException;

    String getDebugInfo() throws RemoteException;

    IBinder getToken() throws RemoteException;

    void scheduleNewIntent(String str, IBinder iBinder, Intent intent) throws RemoteException;

    void scheduleReceiver(String str, ComponentName componentName, Intent intent, PendingResultData pendingResultData) throws RemoteException;
}
