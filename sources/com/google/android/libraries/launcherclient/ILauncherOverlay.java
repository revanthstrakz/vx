package com.google.android.libraries.launcherclient;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.WindowManager.LayoutParams;

public interface ILauncherOverlay extends IInterface {

    public static abstract class Stub extends Binder implements ILauncherOverlay {
        private static final String DESCRIPTOR = "com.google.android.libraries.launcherclient.ILauncherOverlay";
        static final int TRANSACTION_closeOverlay = 6;
        static final int TRANSACTION_endScroll = 3;
        static final int TRANSACTION_getVoiceSearchLanguage = 11;
        static final int TRANSACTION_hasOverlayContent = 13;
        static final int TRANSACTION_isVoiceDetectionRunning = 12;
        static final int TRANSACTION_onPause = 7;
        static final int TRANSACTION_onResume = 8;
        static final int TRANSACTION_onScroll = 2;
        static final int TRANSACTION_openOverlay = 9;
        static final int TRANSACTION_requestVoiceDetection = 10;
        static final int TRANSACTION_setActivityState = 16;
        static final int TRANSACTION_startScroll = 1;
        static final int TRANSACTION_startSearch = 17;
        static final int TRANSACTION_unusedMethod = 15;
        static final int TRANSACTION_windowAttached = 4;
        static final int TRANSACTION_windowAttached2 = 14;
        static final int TRANSACTION_windowDetached = 5;

        private static class Proxy implements ILauncherOverlay {
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

            public void startScroll() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onScroll(float f) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeFloat(f);
                    this.mRemote.transact(2, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void endScroll() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(3, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void windowAttached(LayoutParams layoutParams, ILauncherOverlayCallback iLauncherOverlayCallback, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (layoutParams != null) {
                        obtain.writeInt(1);
                        layoutParams.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iLauncherOverlayCallback != null ? iLauncherOverlayCallback.asBinder() : null);
                    obtain.writeInt(i);
                    this.mRemote.transact(4, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void windowDetached(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(5, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void closeOverlay(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(6, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onPause() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void onResume() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void openOverlay(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(9, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void requestVoiceDetection(boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(10, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public String getVoiceSearchLanguage() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isVoiceDetectionRunning() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean hasOverlayContent() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = false;
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    if (obtain2.readInt() != 0) {
                        z = true;
                    }
                    return z;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void windowAttached2(Bundle bundle, ILauncherOverlayCallback iLauncherOverlayCallback) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeStrongBinder(iLauncherOverlayCallback != null ? iLauncherOverlayCallback.asBinder() : null);
                    this.mRemote.transact(14, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void unusedMethod() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public void setActivityState(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(16, obtain, null, 1);
                } finally {
                    obtain.recycle();
                }
            }

            public boolean startSearch(byte[] bArr, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeByteArray(bArr);
                    boolean z = true;
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
        }

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILauncherOverlay asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof ILauncherOverlay)) {
                return new Proxy(iBinder);
            }
            return (ILauncherOverlay) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v1, types: [android.view.WindowManager$LayoutParams] */
        /* JADX WARNING: type inference failed for: r3v3, types: [android.view.WindowManager$LayoutParams] */
        /* JADX WARNING: type inference failed for: r3v4, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r3v6, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r3v7, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r3v9, types: [android.os.Bundle] */
        /* JADX WARNING: type inference failed for: r3v10 */
        /* JADX WARNING: type inference failed for: r3v11 */
        /* JADX WARNING: type inference failed for: r3v12 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r3v0
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], android.os.Bundle, android.view.WindowManager$LayoutParams]
          uses: [android.view.WindowManager$LayoutParams, android.os.Bundle]
          mth insns count: 105
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
        /* JADX WARNING: Unknown variable types count: 4 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) throws android.os.RemoteException {
            /*
                r4 = this;
                java.lang.String r0 = "com.google.android.libraries.launcherclient.ILauncherOverlay"
                r1 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r2 = 1
                if (r5 == r1) goto L_0x0108
                r1 = 0
                r3 = 0
                switch(r5) {
                    case 1: goto L_0x0101;
                    case 2: goto L_0x00f6;
                    case 3: goto L_0x00ef;
                    case 4: goto L_0x00cd;
                    case 5: goto L_0x00bf;
                    case 6: goto L_0x00b4;
                    case 7: goto L_0x00ad;
                    case 8: goto L_0x00a6;
                    case 9: goto L_0x009b;
                    case 10: goto L_0x008d;
                    case 11: goto L_0x007f;
                    case 12: goto L_0x0071;
                    case 13: goto L_0x0063;
                    case 14: goto L_0x0045;
                    case 15: goto L_0x003e;
                    case 16: goto L_0x0033;
                    case 17: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0012:
                r6.enforceInterface(r0)
                byte[] r5 = r6.createByteArray()
                int r8 = r6.readInt()
                if (r8 == 0) goto L_0x0028
                android.os.Parcelable$Creator r8 = android.os.Bundle.CREATOR
                java.lang.Object r6 = r8.createFromParcel(r6)
                r3 = r6
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x0028:
                boolean r5 = r4.startSearch(r5, r3)
                r7.writeNoException()
                r7.writeInt(r5)
                return r2
            L_0x0033:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                r4.setActivityState(r5)
                return r2
            L_0x003e:
                r6.enforceInterface(r0)
                r4.unusedMethod()
                return r2
            L_0x0045:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0057
                android.os.Parcelable$Creator r5 = android.os.Bundle.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.os.Bundle r3 = (android.os.Bundle) r3
            L_0x0057:
                android.os.IBinder r5 = r6.readStrongBinder()
                com.google.android.libraries.launcherclient.ILauncherOverlayCallback r5 = com.google.android.libraries.launcherclient.ILauncherOverlayCallback.Stub.asInterface(r5)
                r4.windowAttached2(r3, r5)
                return r2
            L_0x0063:
                r6.enforceInterface(r0)
                boolean r5 = r4.hasOverlayContent()
                r7.writeNoException()
                r7.writeInt(r5)
                return r2
            L_0x0071:
                r6.enforceInterface(r0)
                boolean r5 = r4.isVoiceDetectionRunning()
                r7.writeNoException()
                r7.writeInt(r5)
                return r2
            L_0x007f:
                r6.enforceInterface(r0)
                java.lang.String r5 = r4.getVoiceSearchLanguage()
                r7.writeNoException()
                r7.writeString(r5)
                return r2
            L_0x008d:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0097
                r1 = 1
            L_0x0097:
                r4.requestVoiceDetection(r1)
                return r2
            L_0x009b:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                r4.openOverlay(r5)
                return r2
            L_0x00a6:
                r6.enforceInterface(r0)
                r4.onResume()
                return r2
            L_0x00ad:
                r6.enforceInterface(r0)
                r4.onPause()
                return r2
            L_0x00b4:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                r4.closeOverlay(r5)
                return r2
            L_0x00bf:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x00c9
                r1 = 1
            L_0x00c9:
                r4.windowDetached(r1)
                return r2
            L_0x00cd:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x00df
                android.os.Parcelable$Creator r5 = android.view.WindowManager.LayoutParams.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r3 = r5
                android.view.WindowManager$LayoutParams r3 = (android.view.WindowManager.LayoutParams) r3
            L_0x00df:
                android.os.IBinder r5 = r6.readStrongBinder()
                com.google.android.libraries.launcherclient.ILauncherOverlayCallback r5 = com.google.android.libraries.launcherclient.ILauncherOverlayCallback.Stub.asInterface(r5)
                int r6 = r6.readInt()
                r4.windowAttached(r3, r5, r6)
                return r2
            L_0x00ef:
                r6.enforceInterface(r0)
                r4.endScroll()
                return r2
            L_0x00f6:
                r6.enforceInterface(r0)
                float r5 = r6.readFloat()
                r4.onScroll(r5)
                return r2
            L_0x0101:
                r6.enforceInterface(r0)
                r4.startScroll()
                return r2
            L_0x0108:
                r7.writeString(r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.libraries.launcherclient.ILauncherOverlay.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    void closeOverlay(int i) throws RemoteException;

    void endScroll() throws RemoteException;

    String getVoiceSearchLanguage() throws RemoteException;

    boolean hasOverlayContent() throws RemoteException;

    boolean isVoiceDetectionRunning() throws RemoteException;

    void onPause() throws RemoteException;

    void onResume() throws RemoteException;

    void onScroll(float f) throws RemoteException;

    void openOverlay(int i) throws RemoteException;

    void requestVoiceDetection(boolean z) throws RemoteException;

    void setActivityState(int i) throws RemoteException;

    void startScroll() throws RemoteException;

    boolean startSearch(byte[] bArr, Bundle bundle) throws RemoteException;

    void unusedMethod() throws RemoteException;

    void windowAttached(LayoutParams layoutParams, ILauncherOverlayCallback iLauncherOverlayCallback, int i) throws RemoteException;

    void windowAttached2(Bundle bundle, ILauncherOverlayCallback iLauncherOverlayCallback) throws RemoteException;

    void windowDetached(boolean z) throws RemoteException;
}
