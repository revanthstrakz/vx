package com.lody.virtual.server;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.lody.virtual.remote.VParceledListSlice;
import java.util.List;

public interface IPackageManager extends IInterface {

    public static abstract class Stub extends Binder implements IPackageManager {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IPackageManager";
        static final int TRANSACTION_activitySupportsIntent = 7;
        static final int TRANSACTION_checkPermission = 4;
        static final int TRANSACTION_getActivityInfo = 6;
        static final int TRANSACTION_getAllPermissionGroups = 22;
        static final int TRANSACTION_getApplicationInfo = 24;
        static final int TRANSACTION_getInstalledApplications = 18;
        static final int TRANSACTION_getInstalledPackages = 17;
        static final int TRANSACTION_getNameForUid = 27;
        static final int TRANSACTION_getPackageInfo = 5;
        static final int TRANSACTION_getPackageInstaller = 28;
        static final int TRANSACTION_getPackageUid = 1;
        static final int TRANSACTION_getPackagesForUid = 2;
        static final int TRANSACTION_getPermissionGroupInfo = 21;
        static final int TRANSACTION_getPermissionInfo = 19;
        static final int TRANSACTION_getProviderInfo = 10;
        static final int TRANSACTION_getReceiverInfo = 8;
        static final int TRANSACTION_getServiceInfo = 9;
        static final int TRANSACTION_getSharedLibraries = 3;
        static final int TRANSACTION_queryContentProviders = 25;
        static final int TRANSACTION_queryIntentActivities = 12;
        static final int TRANSACTION_queryIntentContentProviders = 16;
        static final int TRANSACTION_queryIntentReceivers = 13;
        static final int TRANSACTION_queryIntentServices = 15;
        static final int TRANSACTION_queryPermissionsByGroup = 20;
        static final int TRANSACTION_querySharedPackages = 26;
        static final int TRANSACTION_resolveContentProvider = 23;
        static final int TRANSACTION_resolveIntent = 11;
        static final int TRANSACTION_resolveService = 14;

        private static class Proxy implements IPackageManager {
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

            public int getPackageUid(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
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

            public String[] getPackagesForUid(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<String> getSharedLibraries(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int checkPermission(String str, String str2, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeInt(i);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public PackageInfo getPackageInfo(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (PackageInfo) PackageInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ActivityInfo getActivityInfo(ComponentName componentName, int i, int i2) throws RemoteException {
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
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ActivityInfo) ActivityInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean activitySupportsIntent(ComponentName componentName, Intent intent, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean z = true;
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
                    obtain.writeString(str);
                    this.mRemote.transact(7, obtain, obtain2, 0);
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

            public ActivityInfo getReceiverInfo(ComponentName componentName, int i, int i2) throws RemoteException {
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
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(8, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ActivityInfo) ActivityInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ServiceInfo getServiceInfo(ComponentName componentName, int i, int i2) throws RemoteException {
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
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ServiceInfo) ServiceInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ProviderInfo getProviderInfo(ComponentName componentName, int i, int i2) throws RemoteException {
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
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(10, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ProviderInfo) ProviderInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ResolveInfo resolveIntent(Intent intent, String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(11, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ResolveInfo) ResolveInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<ResolveInfo> queryIntentActivities(Intent intent, String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(12, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(ResolveInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<ResolveInfo> queryIntentReceivers(Intent intent, String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(13, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(ResolveInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ResolveInfo resolveService(Intent intent, String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(14, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ResolveInfo) ResolveInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<ResolveInfo> queryIntentServices(Intent intent, String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(ResolveInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<ResolveInfo> queryIntentContentProviders(Intent intent, String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (intent != null) {
                        obtain.writeInt(1);
                        intent.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(ResolveInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VParceledListSlice getInstalledPackages(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VParceledListSlice) VParceledListSlice.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VParceledListSlice getInstalledApplications(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(18, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VParceledListSlice) VParceledListSlice.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public PermissionInfo getPermissionInfo(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(19, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (PermissionInfo) PermissionInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<PermissionInfo> queryPermissionsByGroup(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(PermissionInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (PermissionGroupInfo) PermissionGroupInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<PermissionGroupInfo> getAllPermissionGroups(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(PermissionGroupInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ProviderInfo resolveContentProvider(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ProviderInfo) ProviderInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public ApplicationInfo getApplicationInfo(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (ApplicationInfo) ApplicationInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public VParceledListSlice queryContentProviders(String str, int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(25, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VParceledListSlice) VParceledListSlice.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<String> querySharedPackages(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(26, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createStringArrayList();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getNameForUid(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(27, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readString();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IPackageInstaller getPackageInstaller() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, obtain, obtain2, 0);
                    obtain2.readException();
                    return com.lody.virtual.server.IPackageInstaller.Stub.asInterface(obtain2.readStrongBinder());
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

        public static IPackageManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IPackageManager)) {
                return new Proxy(iBinder);
            }
            return (IPackageManager) queryLocalInterface;
        }

        /* JADX WARNING: type inference failed for: r1v1 */
        /* JADX WARNING: type inference failed for: r1v2, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v4, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v5, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v7, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v8, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v10, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v11, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v13, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v14, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v16, types: [android.content.ComponentName] */
        /* JADX WARNING: type inference failed for: r1v17, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v19, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v20, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v22, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v23, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v25, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v26, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v28, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v29, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v31, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v32, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v34, types: [android.content.Intent] */
        /* JADX WARNING: type inference failed for: r1v35, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r1v36, types: [android.os.IBinder] */
        /* JADX WARNING: type inference failed for: r1v37 */
        /* JADX WARNING: type inference failed for: r1v38 */
        /* JADX WARNING: type inference failed for: r1v39 */
        /* JADX WARNING: type inference failed for: r1v40 */
        /* JADX WARNING: type inference failed for: r1v41 */
        /* JADX WARNING: type inference failed for: r1v42 */
        /* JADX WARNING: type inference failed for: r1v43 */
        /* JADX WARNING: type inference failed for: r1v44 */
        /* JADX WARNING: type inference failed for: r1v45 */
        /* JADX WARNING: type inference failed for: r1v46 */
        /* JADX WARNING: type inference failed for: r1v47 */
        /* JADX WARNING: type inference failed for: r1v48 */
        /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r1v1
          assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], android.content.Intent, android.content.ComponentName, android.os.IBinder]
          uses: [android.content.ComponentName, android.content.Intent, android.os.IBinder]
          mth insns count: 335
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
        /* JADX WARNING: Unknown variable types count: 13 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTransact(int r5, android.os.Parcel r6, android.os.Parcel r7, int r8) throws android.os.RemoteException {
            /*
                r4 = this;
                java.lang.String r0 = "com.lody.virtual.server.IPackageManager"
                r1 = 1598968902(0x5f4e5446, float:1.4867585E19)
                r2 = 1
                if (r5 == r1) goto L_0x03bf
                r1 = 0
                r3 = 0
                switch(r5) {
                    case 1: goto L_0x03a9;
                    case 2: goto L_0x0397;
                    case 3: goto L_0x0385;
                    case 4: goto L_0x036b;
                    case 5: goto L_0x0348;
                    case 6: goto L_0x031a;
                    case 7: goto L_0x02e9;
                    case 8: goto L_0x02bb;
                    case 9: goto L_0x028d;
                    case 10: goto L_0x025f;
                    case 11: goto L_0x022d;
                    case 12: goto L_0x0204;
                    case 13: goto L_0x01db;
                    case 14: goto L_0x01a9;
                    case 15: goto L_0x0180;
                    case 16: goto L_0x0157;
                    case 17: goto L_0x0138;
                    case 18: goto L_0x0119;
                    case 19: goto L_0x00fa;
                    case 20: goto L_0x00e4;
                    case 21: goto L_0x00c5;
                    case 22: goto L_0x00b3;
                    case 23: goto L_0x0090;
                    case 24: goto L_0x006d;
                    case 25: goto L_0x004a;
                    case 26: goto L_0x0038;
                    case 27: goto L_0x0026;
                    case 28: goto L_0x0012;
                    default: goto L_0x000d;
                }
            L_0x000d:
                boolean r5 = super.onTransact(r5, r6, r7, r8)
                return r5
            L_0x0012:
                r6.enforceInterface(r0)
                com.lody.virtual.server.IPackageInstaller r5 = r4.getPackageInstaller()
                r7.writeNoException()
                if (r5 == 0) goto L_0x0022
                android.os.IBinder r1 = r5.asBinder()
            L_0x0022:
                r7.writeStrongBinder(r1)
                return r2
            L_0x0026:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String r5 = r4.getNameForUid(r5)
                r7.writeNoException()
                r7.writeString(r5)
                return r2
            L_0x0038:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                java.util.List r5 = r4.querySharedPackages(r5)
                r7.writeNoException()
                r7.writeStringList(r5)
                return r2
            L_0x004a:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                com.lody.virtual.remote.VParceledListSlice r5 = r4.queryContentProviders(r5, r8, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0069
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x006c
            L_0x0069:
                r7.writeInt(r3)
            L_0x006c:
                return r2
            L_0x006d:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ApplicationInfo r5 = r4.getApplicationInfo(r5, r8, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x008c
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x008f
            L_0x008c:
                r7.writeInt(r3)
            L_0x008f:
                return r2
            L_0x0090:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ProviderInfo r5 = r4.resolveContentProvider(r5, r8, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x00af
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x00b2
            L_0x00af:
                r7.writeInt(r3)
            L_0x00b2:
                return r2
            L_0x00b3:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.util.List r5 = r4.getAllPermissionGroups(r5)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x00c5:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r6 = r6.readInt()
                android.content.pm.PermissionGroupInfo r5 = r4.getPermissionGroupInfo(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x00e0
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x00e3
            L_0x00e0:
                r7.writeInt(r3)
            L_0x00e3:
                return r2
            L_0x00e4:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r6 = r6.readInt()
                java.util.List r5 = r4.queryPermissionsByGroup(r5, r6)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x00fa:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r6 = r6.readInt()
                android.content.pm.PermissionInfo r5 = r4.getPermissionInfo(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0115
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x0118
            L_0x0115:
                r7.writeInt(r3)
            L_0x0118:
                return r2
            L_0x0119:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                int r6 = r6.readInt()
                com.lody.virtual.remote.VParceledListSlice r5 = r4.getInstalledApplications(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0134
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x0137
            L_0x0134:
                r7.writeInt(r3)
            L_0x0137:
                return r2
            L_0x0138:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                int r6 = r6.readInt()
                com.lody.virtual.remote.VParceledListSlice r5 = r4.getInstalledPackages(r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0153
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x0156
            L_0x0153:
                r7.writeInt(r3)
            L_0x0156:
                return r2
            L_0x0157:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0169
                android.os.Parcelable$Creator r5 = android.content.Intent.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x0169:
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                java.util.List r5 = r4.queryIntentContentProviders(r1, r5, r8, r6)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x0180:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0192
                android.os.Parcelable$Creator r5 = android.content.Intent.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x0192:
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                java.util.List r5 = r4.queryIntentServices(r1, r5, r8, r6)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x01a9:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x01bb
                android.os.Parcelable$Creator r5 = android.content.Intent.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x01bb:
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ResolveInfo r5 = r4.resolveService(r1, r5, r8, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x01d7
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x01da
            L_0x01d7:
                r7.writeInt(r3)
            L_0x01da:
                return r2
            L_0x01db:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x01ed
                android.os.Parcelable$Creator r5 = android.content.Intent.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x01ed:
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                java.util.List r5 = r4.queryIntentReceivers(r1, r5, r8, r6)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x0204:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0216
                android.os.Parcelable$Creator r5 = android.content.Intent.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x0216:
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                java.util.List r5 = r4.queryIntentActivities(r1, r5, r8, r6)
                r7.writeNoException()
                r7.writeTypedList(r5)
                return r2
            L_0x022d:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x023f
                android.os.Parcelable$Creator r5 = android.content.Intent.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x023f:
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ResolveInfo r5 = r4.resolveIntent(r1, r5, r8, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x025b
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x025e
            L_0x025b:
                r7.writeInt(r3)
            L_0x025e:
                return r2
            L_0x025f:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x0271
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.ComponentName r1 = (android.content.ComponentName) r1
            L_0x0271:
                int r5 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ProviderInfo r5 = r4.getProviderInfo(r1, r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0289
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x028c
            L_0x0289:
                r7.writeInt(r3)
            L_0x028c:
                return r2
            L_0x028d:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x029f
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.ComponentName r1 = (android.content.ComponentName) r1
            L_0x029f:
                int r5 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ServiceInfo r5 = r4.getServiceInfo(r1, r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x02b7
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x02ba
            L_0x02b7:
                r7.writeInt(r3)
            L_0x02ba:
                return r2
            L_0x02bb:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x02cd
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.ComponentName r1 = (android.content.ComponentName) r1
            L_0x02cd:
                int r5 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ActivityInfo r5 = r4.getReceiverInfo(r1, r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x02e5
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x02e8
            L_0x02e5:
                r7.writeInt(r3)
            L_0x02e8:
                return r2
            L_0x02e9:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x02fb
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                android.content.ComponentName r5 = (android.content.ComponentName) r5
                goto L_0x02fc
            L_0x02fb:
                r5 = r1
            L_0x02fc:
                int r8 = r6.readInt()
                if (r8 == 0) goto L_0x030b
                android.os.Parcelable$Creator r8 = android.content.Intent.CREATOR
                java.lang.Object r8 = r8.createFromParcel(r6)
                r1 = r8
                android.content.Intent r1 = (android.content.Intent) r1
            L_0x030b:
                java.lang.String r6 = r6.readString()
                boolean r5 = r4.activitySupportsIntent(r5, r1, r6)
                r7.writeNoException()
                r7.writeInt(r5)
                return r2
            L_0x031a:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                if (r5 == 0) goto L_0x032c
                android.os.Parcelable$Creator r5 = android.content.ComponentName.CREATOR
                java.lang.Object r5 = r5.createFromParcel(r6)
                r1 = r5
                android.content.ComponentName r1 = (android.content.ComponentName) r1
            L_0x032c:
                int r5 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.ActivityInfo r5 = r4.getActivityInfo(r1, r5, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0344
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x0347
            L_0x0344:
                r7.writeInt(r3)
            L_0x0347:
                return r2
            L_0x0348:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r8 = r6.readInt()
                int r6 = r6.readInt()
                android.content.pm.PackageInfo r5 = r4.getPackageInfo(r5, r8, r6)
                r7.writeNoException()
                if (r5 == 0) goto L_0x0367
                r7.writeInt(r2)
                r5.writeToParcel(r7, r2)
                goto L_0x036a
            L_0x0367:
                r7.writeInt(r3)
            L_0x036a:
                return r2
            L_0x036b:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                java.lang.String r8 = r6.readString()
                int r6 = r6.readInt()
                int r5 = r4.checkPermission(r5, r8, r6)
                r7.writeNoException()
                r7.writeInt(r5)
                return r2
            L_0x0385:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                java.util.List r5 = r4.getSharedLibraries(r5)
                r7.writeNoException()
                r7.writeStringList(r5)
                return r2
            L_0x0397:
                r6.enforceInterface(r0)
                int r5 = r6.readInt()
                java.lang.String[] r5 = r4.getPackagesForUid(r5)
                r7.writeNoException()
                r7.writeStringArray(r5)
                return r2
            L_0x03a9:
                r6.enforceInterface(r0)
                java.lang.String r5 = r6.readString()
                int r6 = r6.readInt()
                int r5 = r4.getPackageUid(r5, r6)
                r7.writeNoException()
                r7.writeInt(r5)
                return r2
            L_0x03bf:
                r7.writeString(r0)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.IPackageManager.Stub.onTransact(int, android.os.Parcel, android.os.Parcel, int):boolean");
        }
    }

    boolean activitySupportsIntent(ComponentName componentName, Intent intent, String str) throws RemoteException;

    int checkPermission(String str, String str2, int i) throws RemoteException;

    ActivityInfo getActivityInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    List<PermissionGroupInfo> getAllPermissionGroups(int i) throws RemoteException;

    ApplicationInfo getApplicationInfo(String str, int i, int i2) throws RemoteException;

    VParceledListSlice getInstalledApplications(int i, int i2) throws RemoteException;

    VParceledListSlice getInstalledPackages(int i, int i2) throws RemoteException;

    String getNameForUid(int i) throws RemoteException;

    PackageInfo getPackageInfo(String str, int i, int i2) throws RemoteException;

    IPackageInstaller getPackageInstaller() throws RemoteException;

    int getPackageUid(String str, int i) throws RemoteException;

    String[] getPackagesForUid(int i) throws RemoteException;

    PermissionGroupInfo getPermissionGroupInfo(String str, int i) throws RemoteException;

    PermissionInfo getPermissionInfo(String str, int i) throws RemoteException;

    ProviderInfo getProviderInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    ActivityInfo getReceiverInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    ServiceInfo getServiceInfo(ComponentName componentName, int i, int i2) throws RemoteException;

    List<String> getSharedLibraries(String str) throws RemoteException;

    VParceledListSlice queryContentProviders(String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentActivities(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentContentProviders(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentReceivers(Intent intent, String str, int i, int i2) throws RemoteException;

    List<ResolveInfo> queryIntentServices(Intent intent, String str, int i, int i2) throws RemoteException;

    List<PermissionInfo> queryPermissionsByGroup(String str, int i) throws RemoteException;

    List<String> querySharedPackages(String str) throws RemoteException;

    ProviderInfo resolveContentProvider(String str, int i, int i2) throws RemoteException;

    ResolveInfo resolveIntent(Intent intent, String str, int i, int i2) throws RemoteException;

    ResolveInfo resolveService(Intent intent, String str, int i, int i2) throws RemoteException;
}
