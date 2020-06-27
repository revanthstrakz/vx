package com.lody.virtual.server;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.lody.virtual.remote.vloc.VCell;
import com.lody.virtual.remote.vloc.VLocation;
import java.util.List;

public interface IVirtualLocationManager extends IInterface {

    public static abstract class Stub extends Binder implements IVirtualLocationManager {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IVirtualLocationManager";
        static final int TRANSACTION_getAllCell = 10;
        static final int TRANSACTION_getCell = 9;
        static final int TRANSACTION_getGlobalLocation = 15;
        static final int TRANSACTION_getLocation = 13;
        static final int TRANSACTION_getMode = 1;
        static final int TRANSACTION_getNeighboringCell = 11;
        static final int TRANSACTION_setAllCell = 4;
        static final int TRANSACTION_setCell = 3;
        static final int TRANSACTION_setGlobalAllCell = 7;
        static final int TRANSACTION_setGlobalCell = 6;
        static final int TRANSACTION_setGlobalLocation = 14;
        static final int TRANSACTION_setGlobalNeighboringCell = 8;
        static final int TRANSACTION_setLocation = 12;
        static final int TRANSACTION_setMode = 2;
        static final int TRANSACTION_setNeighboringCell = 5;

        private static class Proxy implements IVirtualLocationManager {
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

            public int getMode(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setMode(int i, String str, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeInt(i2);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setCell(int i, String str, VCell vCell) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    if (vCell != null) {
                        obtain.writeInt(1);
                        vCell.writeToParcel(obtain, 0);
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

            public void setAllCell(int i, String str, List<VCell> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeTypedList(list);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setNeighboringCell(int i, String str, List<VCell> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeTypedList(list);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setGlobalCell(VCell vCell) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (vCell != null) {
                        obtain.writeInt(1);
                        vCell.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setGlobalAllCell(List<VCell> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedList(list);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setGlobalNeighboringCell(List<VCell> list) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeTypedList(list);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VCell getCell(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VCell) VCell.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<VCell> getAllCell(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(VCell.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<VCell> getNeighboringCell(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(VCell.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setLocation(int i, String str, VLocation vLocation) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    if (vLocation != null) {
                        obtain.writeInt(1);
                        vLocation.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VLocation getLocation(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VLocation) VLocation.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setGlobalLocation(VLocation vLocation) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (vLocation != null) {
                        obtain.writeInt(1);
                        vLocation.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VLocation getGlobalLocation() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VLocation) VLocation.CREATOR.createFromParcel(obtain2) : null;
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

        public static IVirtualLocationManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IVirtualLocationManager)) {
                return new Proxy(iBinder);
            }
            return (IVirtualLocationManager) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v1, types: [com.lody.virtual.remote.vloc.VCell] */
        /* JADX WARNING: type inference failed for: r3v3, types: [com.lody.virtual.remote.vloc.VCell] */
        /* JADX WARNING: type inference failed for: r3v4, types: [com.lody.virtual.remote.vloc.VCell] */
        /* JADX WARNING: type inference failed for: r3v6, types: [com.lody.virtual.remote.vloc.VCell] */
        /* JADX WARNING: type inference failed for: r3v7, types: [com.lody.virtual.remote.vloc.VLocation] */
        /* JADX WARNING: type inference failed for: r3v9, types: [com.lody.virtual.remote.vloc.VLocation] */
        /* JADX WARNING: type inference failed for: r3v10, types: [com.lody.virtual.remote.vloc.VLocation] */
        /* JADX WARNING: type inference failed for: r3v12, types: [com.lody.virtual.remote.vloc.VLocation] */
        /* JADX WARNING: type inference failed for: r3v13 */
        /* JADX WARNING: type inference failed for: r3v14 */
        /* JADX WARNING: type inference failed for: r3v15 */
        /* JADX WARNING: type inference failed for: r3v16 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r3v0
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], com.lody.virtual.remote.vloc.VCell, com.lody.virtual.remote.vloc.VLocation]
          uses: [com.lody.virtual.remote.vloc.VCell, com.lody.virtual.remote.vloc.VLocation]
          mth insns count: 140
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
        /* JADX WARNING: Unknown variable types count: 5 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) throws android.os.RemoteException {
            /*
                r4 = this;
                java.lang.String r0 = "com.lody.virtual.server.IVirtualLocationManager"
                r1 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r2 = 1
                if (r5 == r1) goto L_0x0183
                r1 = 0
                r3 = 0
                switch(r5) {
                    case 1: goto L_0x016d;
                    case 2: goto L_0x0157;
                    case 3: goto L_0x0136;
                    case 4: goto L_0x011e;
                    case 5: goto L_0x0106;
                    case 6: goto L_0x00ed;
                    case 7: goto L_0x00dd;
                    case 8: goto L_0x00cd;
                    case 9: goto L_0x00ae;
                    case 10: goto L_0x0098;
                    case 11: goto L_0x0082;
                    case 12: goto L_0x0061;
                    case 13: goto L_0x0042;
                    case 14: goto L_0x0029;
                    case 15: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0012:
                r6.enforceInterface(r0)
                com.lody.virtual.remote.vloc.VLocation r5 = r4.getGlobalLocation()
                r7.writeNoException()
                if (r5 == 0) goto L_0x0025
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x0028
            L_0x0025:
                r7.writeInt(r1)
            L_0x0028:
                return r2
            L_0x0029:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x003b
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VLocation> r5 = com.lody.virtual.remote.vloc.VLocation.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                com.lody.virtual.remote.vloc.VLocation r3 = (com.lody.virtual.remote.vloc.VLocation) r3
            L_0x003b:
                r4.setGlobalLocation(r3)
                r7.writeNoException()
                return r2
            L_0x0042:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r6 = r6.readString()
                com.lody.virtual.remote.vloc.VLocation r5 = r4.getLocation(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x005d
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x0060
            L_0x005d:
                r7.writeInt(r1)
            L_0x0060:
                return r2
            L_0x0061:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r8 = r6.readString()
                int r0 = r6.readInt()
                if (r0 == 0) goto L_0x007b
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VLocation> r0 = com.lody.virtual.remote.vloc.VLocation.CREATOR
                java.lang.Object r6 = r0.createFromParcel(r6)
                r3 = r6
                com.lody.virtual.remote.vloc.VLocation r3 = (com.lody.virtual.remote.vloc.VLocation) r3
            L_0x007b:
                r4.setLocation(r5, r8, r3)
                r7.writeNoException()
                return r2
            L_0x0082:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r6 = r6.readString()
                java.util.List r5 = r4.getNeighboringCell(r5, r6)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x0098:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r6 = r6.readString()
                java.util.List r5 = r4.getAllCell(r5, r6)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x00ae:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r6 = r6.readString()
                com.lody.virtual.remote.vloc.VCell r5 = r4.getCell(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x00c9
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x00cc
            L_0x00c9:
                r7.writeInt(r1)
            L_0x00cc:
                return r2
            L_0x00cd:
                r6.enforceInterface(r0)
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VCell> r5 = com.lody.virtual.remote.vloc.VCell.CREATOR
                java.util.ArrayList r5 = r6.createTypedArrayList(r5)
                r4.setGlobalNeighboringCell(r5)
                r7.writeNoException()
                return r2
            L_0x00dd:
                r6.enforceInterface(r0)
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VCell> r5 = com.lody.virtual.remote.vloc.VCell.CREATOR
                java.util.ArrayList r5 = r6.createTypedArrayList(r5)
                r4.setGlobalAllCell(r5)
                r7.writeNoException()
                return r2
            L_0x00ed:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x00ff
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VCell> r5 = com.lody.virtual.remote.vloc.VCell.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                com.lody.virtual.remote.vloc.VCell r3 = (com.lody.virtual.remote.vloc.VCell) r3
            L_0x00ff:
                r4.setGlobalCell(r3)
                r7.writeNoException()
                return r2
            L_0x0106:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r8 = r6.readString()
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VCell> r0 = com.lody.virtual.remote.vloc.VCell.CREATOR
                java.util.ArrayList r6 = r6.createTypedArrayList(r0)
                r4.setNeighboringCell(r5, r8, r6)
                r7.writeNoException()
                return r2
            L_0x011e:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r8 = r6.readString()
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VCell> r0 = com.lody.virtual.remote.vloc.VCell.CREATOR
                java.util.ArrayList r6 = r6.createTypedArrayList(r0)
                r4.setAllCell(r5, r8, r6)
                r7.writeNoException()
                return r2
            L_0x0136:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r8 = r6.readString()
                int r0 = r6.readInt()
                if (r0 == 0) goto L_0x0150
                android.os.Parcelable$Creator<com.lody.virtual.remote.vloc.VCell> r0 = com.lody.virtual.remote.vloc.VCell.CREATOR
                java.lang.Object r6 = r0.createFromParcel(r6)
                r3 = r6
                com.lody.virtual.remote.vloc.VCell r3 = (com.lody.virtual.remote.vloc.VCell) r3
            L_0x0150:
                r4.setCell(r5, r8, r3)
                r7.writeNoException()
                return r2
            L_0x0157:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r8 = r6.readString()
                int r6 = r6.readInt()
                r4.setMode(r5, r8, r6)
                r7.writeNoException()
                return r2
            L_0x016d:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r6 = r6.readString()
                int r5 = r4.getMode(r5, r6)
                r7.writeNoException()
                r7.writeInt(r5)
                return r2
            L_0x0183:
                r7.writeString(r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.IVirtualLocationManager.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    List<VCell> getAllCell(int i, String str) throws RemoteException;

    VCell getCell(int i, String str) throws RemoteException;

    VLocation getGlobalLocation() throws RemoteException;

    VLocation getLocation(int i, String str) throws RemoteException;

    int getMode(int i, String str) throws RemoteException;

    List<VCell> getNeighboringCell(int i, String str) throws RemoteException;

    void setAllCell(int i, String str, List<VCell> list) throws RemoteException;

    void setCell(int i, String str, VCell vCell) throws RemoteException;

    void setGlobalAllCell(List<VCell> list) throws RemoteException;

    void setGlobalCell(VCell vCell) throws RemoteException;

    void setGlobalLocation(VLocation vLocation) throws RemoteException;

    void setGlobalNeighboringCell(List<VCell> list) throws RemoteException;

    void setLocation(int i, String str, VLocation vLocation) throws RemoteException;

    void setMode(int i, String str, int i2) throws RemoteException;

    void setNeighboringCell(int i, String str, List<VCell> list) throws RemoteException;
}
