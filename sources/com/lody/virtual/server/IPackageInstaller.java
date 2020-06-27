package com.lody.virtual.server;

import android.content.IntentSender;
import android.content.p000pm.IPackageInstallerCallback;
import android.content.p000pm.IPackageInstallerSession;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.lody.virtual.remote.VParceledListSlice;
import com.lody.virtual.server.p009pm.installer.SessionInfo;
import com.lody.virtual.server.p009pm.installer.SessionParams;

public interface IPackageInstaller extends IInterface {

    public static abstract class Stub extends Binder implements IPackageInstaller {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IPackageInstaller";
        static final int TRANSACTION_abandonSession = 4;
        static final int TRANSACTION_createSession = 1;
        static final int TRANSACTION_getAllSessions = 7;
        static final int TRANSACTION_getMySessions = 8;
        static final int TRANSACTION_getSessionInfo = 6;
        static final int TRANSACTION_openSession = 5;
        static final int TRANSACTION_registerCallback = 9;
        static final int TRANSACTION_setPermissionsResult = 12;
        static final int TRANSACTION_uninstall = 11;
        static final int TRANSACTION_unregisterCallback = 10;
        static final int TRANSACTION_updateSessionAppIcon = 2;
        static final int TRANSACTION_updateSessionAppLabel = 3;

        private static class Proxy implements IPackageInstaller {
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

            public int createSession(SessionParams sessionParams, String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (sessionParams != null) {
                        obtain.writeInt(1);
                        sessionParams.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateSessionAppIcon(int i, Bitmap bitmap) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (bitmap != null) {
                        obtain.writeInt(1);
                        bitmap.writeToParcel(obtain, 0);
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

            public void updateSessionAppLabel(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void abandonSession(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IPackageInstallerSession openSession(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return android.content.p000pm.IPackageInstallerSession.Stub.asInterface(obtain2.readStrongBinder());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public SessionInfo getSessionInfo(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (SessionInfo) SessionInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VParceledListSlice getAllSessions(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VParceledListSlice) VParceledListSlice.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VParceledListSlice getMySessions(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VParceledListSlice) VParceledListSlice.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void registerCallback(IPackageInstallerCallback iPackageInstallerCallback, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iPackageInstallerCallback != null ? iPackageInstallerCallback.asBinder() : null);
                    obtain.writeInt(i);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterCallback(IPackageInstallerCallback iPackageInstallerCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iPackageInstallerCallback != null ? iPackageInstallerCallback.asBinder() : null);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void uninstall(String str, String str2, int i, IntentSender intentSender, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    if (intentSender != null) {
                        obtain.writeInt(1);
                        intentSender.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeInt(i2);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setPermissionsResult(int i, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(12, obtain, obtain2, 0);
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

        public static IPackageInstaller asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IPackageInstaller)) {
                return new Proxy(iBinder);
            }
            return (IPackageInstaller) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v1, types: [com.lody.virtual.server.pm.installer.SessionParams] */
        /* JADX WARNING: type inference failed for: r3v3, types: [com.lody.virtual.server.pm.installer.SessionParams] */
        /* JADX WARNING: type inference failed for: r3v4, types: [android.graphics.Bitmap] */
        /* JADX WARNING: type inference failed for: r3v6, types: [android.graphics.Bitmap] */
        /* JADX WARNING: type inference failed for: r3v7, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r3v8, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r3v9 */
        /* JADX WARNING: type inference failed for: r7v0, types: [android.content.IntentSender] */
        /* JADX WARNING: type inference failed for: r3v12, types: [android.content.IntentSender] */
        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v14 */
        /* JADX WARNING: type inference failed for: r3v15 */
        /* JADX WARNING: type inference failed for: r3v16 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r3v0
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], android.graphics.Bitmap, com.lody.virtual.server.pm.installer.SessionParams, android.os.IBinder, android.content.IntentSender]
          uses: [com.lody.virtual.server.pm.installer.SessionParams, android.graphics.Bitmap, android.os.IBinder, ?[OBJECT, ARRAY]]
          mth insns count: 121
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
        /* JADX WARNING: Unknown variable types count: 6 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r10, android.os.Parcel r11, android.os.Parcel r12, int r13) throws android.os.RemoteException {
            /*
                r9 = this;
                java.lang.String r0 = "com.lody.virtual.server.IPackageInstaller"
                r1 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r2 = 1
                if (r10 == r1) goto L_0x0149
                r1 = 0
                r3 = 0
                switch(r10) {
                    case 1: goto L_0x0124;
                    case 2: goto L_0x0107;
                    case 3: goto L_0x00f5;
                    case 4: goto L_0x00e7;
                    case 5: goto L_0x00cf;
                    case 6: goto L_0x00b4;
                    case 7: goto L_0x0099;
                    case 8: goto L_0x007a;
                    case 9: goto L_0x0064;
                    case 10: goto L_0x0052;
                    case 11: goto L_0x0027;
                    case 12: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r10 = super.onTransact(r10, r11, r12, r13)
                return r10
            L_0x0012:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                int r11 = r11.readInt()
                if (r11 == 0) goto L_0x0020
                r1 = 1
            L_0x0020:
                r9.setPermissionsResult(r10, r1)
                r12.writeNoException()
                return r2
            L_0x0027:
                r11.enforceInterface(r0)
                java.lang.String r4 = r11.readString()
                java.lang.String r5 = r11.readString()
                int r6 = r11.readInt()
                int r10 = r11.readInt()
                if (r10 == 0) goto L_0x0045
                android.os.Parcelable$Creator r10 = android.content.IntentSender.CREATOR
                java.lang.Object r10 = r10.createFromParcel(r11)
                r3 = r10
                android.content.IntentSender r3 = (android.content.IntentSender) r3
            L_0x0045:
                r7 = r3
                int r8 = r11.readInt()
                r3 = r9
                r3.uninstall(r4, r5, r6, r7, r8)
                r12.writeNoException()
                return r2
            L_0x0052:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.content.pm.IPackageInstallerCallback r10 = android.content.p000pm.IPackageInstallerCallback.Stub.asInterface(r10)
                r9.unregisterCallback(r10)
                r12.writeNoException()
                return r2
            L_0x0064:
                r11.enforceInterface(r0)
                android.os.IBinder r10 = r11.readStrongBinder()
                android.content.pm.IPackageInstallerCallback r10 = android.content.p000pm.IPackageInstallerCallback.Stub.asInterface(r10)
                int r11 = r11.readInt()
                r9.registerCallback(r10, r11)
                r12.writeNoException()
                return r2
            L_0x007a:
                r11.enforceInterface(r0)
                java.lang.String r10 = r11.readString()
                int r11 = r11.readInt()
                com.lody.virtual.remote.VParceledListSlice r10 = r9.getMySessions(r10, r11)
                r12.writeNoException()
                if (r10 == 0) goto L_0x0095
                r12.writeInt(r2)
                r10.writeToParcel(r12, r2)
                goto L_0x0098
            L_0x0095:
                r12.writeInt(r1)
            L_0x0098:
                return r2
            L_0x0099:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                com.lody.virtual.remote.VParceledListSlice r10 = r9.getAllSessions(r10)
                r12.writeNoException()
                if (r10 == 0) goto L_0x00b0
                r12.writeInt(r2)
                r10.writeToParcel(r12, r2)
                goto L_0x00b3
            L_0x00b0:
                r12.writeInt(r1)
            L_0x00b3:
                return r2
            L_0x00b4:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                com.lody.virtual.server.pm.installer.SessionInfo r10 = r9.getSessionInfo(r10)
                r12.writeNoException()
                if (r10 == 0) goto L_0x00cb
                r12.writeInt(r2)
                r10.writeToParcel(r12, r2)
                goto L_0x00ce
            L_0x00cb:
                r12.writeInt(r1)
            L_0x00ce:
                return r2
            L_0x00cf:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                android.content.pm.IPackageInstallerSession r10 = r9.openSession(r10)
                r12.writeNoException()
                if (r10 == 0) goto L_0x00e3
                android.os.IBinder r3 = r10.asBinder()
            L_0x00e3:
                r12.writeStrongBinder(r3)
                return r2
            L_0x00e7:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                r9.abandonSession(r10)
                r12.writeNoException()
                return r2
            L_0x00f5:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                java.lang.String r11 = r11.readString()
                r9.updateSessionAppLabel(r10, r11)
                r12.writeNoException()
                return r2
            L_0x0107:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                int r13 = r11.readInt()
                if (r13 == 0) goto L_0x011d
                android.os.Parcelable$Creator r13 = android.graphics.Bitmap.CREATOR
                java.lang.Object r11 = r13.createFromParcel(r11)
                r3 = r11
                android.graphics.Bitmap r3 = (android.graphics.Bitmap) r3
            L_0x011d:
                r9.updateSessionAppIcon(r10, r3)
                r12.writeNoException()
                return r2
            L_0x0124:
                r11.enforceInterface(r0)
                int r10 = r11.readInt()
                if (r10 == 0) goto L_0x0136
                android.os.Parcelable$Creator<com.lody.virtual.server.pm.installer.SessionParams> r10 = com.lody.virtual.server.p009pm.installer.SessionParams.CREATOR
                java.lang.Object r10 = r10.createFromParcel(r11)
                r3 = r10
                com.lody.virtual.server.pm.installer.SessionParams r3 = (com.lody.virtual.server.p009pm.installer.SessionParams) r3
            L_0x0136:
                java.lang.String r10 = r11.readString()
                int r11 = r11.readInt()
                int r10 = r9.createSession(r3, r10, r11)
                r12.writeNoException()
                r12.writeInt(r10)
                return r2
            L_0x0149:
                r12.writeString(r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.IPackageInstaller.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void abandonSession(int i) throws RemoteException;

    int createSession(SessionParams sessionParams, String str, int i) throws RemoteException;

    VParceledListSlice getAllSessions(int i) throws RemoteException;

    VParceledListSlice getMySessions(String str, int i) throws RemoteException;

    SessionInfo getSessionInfo(int i) throws RemoteException;

    IPackageInstallerSession openSession(int i) throws RemoteException;

    void registerCallback(IPackageInstallerCallback iPackageInstallerCallback, int i) throws RemoteException;

    void setPermissionsResult(int i, boolean z) throws RemoteException;

    void uninstall(String str, String str2, int i, IntentSender intentSender, int i2) throws RemoteException;

    void unregisterCallback(IPackageInstallerCallback iPackageInstallerCallback) throws RemoteException;

    void updateSessionAppIcon(int i, Bitmap bitmap) throws RemoteException;

    void updateSessionAppLabel(int i, String str) throws RemoteException;
}
