package com.lody.virtual.server;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import com.lody.virtual.server.interfaces.IAppRequestListener;
import com.lody.virtual.server.interfaces.IPackageObserver;
import java.util.List;

public interface IAppManager extends IInterface {

    public static abstract class Stub extends Binder implements IAppManager {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IAppManager";
        static final int TRANSACTION_addVisibleOutsidePackage = 3;
        static final int TRANSACTION_clearAppRequestListener = 23;
        static final int TRANSACTION_clearPackage = 14;
        static final int TRANSACTION_clearPackageAsUser = 13;
        static final int TRANSACTION_getAppRequestListener = 24;
        static final int TRANSACTION_getInstalledAppCount = 17;
        static final int TRANSACTION_getInstalledAppInfo = 6;
        static final int TRANSACTION_getInstalledApps = 15;
        static final int TRANSACTION_getInstalledAppsAsUser = 16;
        static final int TRANSACTION_getPackageInstalledUsers = 1;
        static final int TRANSACTION_installPackage = 7;
        static final int TRANSACTION_installPackageAsUser = 10;
        static final int TRANSACTION_isAppInstalled = 18;
        static final int TRANSACTION_isAppInstalledAsUser = 19;
        static final int TRANSACTION_isOutsidePackageVisible = 5;
        static final int TRANSACTION_isPackageLaunched = 8;
        static final int TRANSACTION_registerObserver = 20;
        static final int TRANSACTION_removeVisibleOutsidePackage = 4;
        static final int TRANSACTION_scanApps = 2;
        static final int TRANSACTION_setAppRequestListener = 22;
        static final int TRANSACTION_setPackageHidden = 9;
        static final int TRANSACTION_uninstallPackage = 12;
        static final int TRANSACTION_uninstallPackageAsUser = 11;
        static final int TRANSACTION_unregisterObserver = 21;

        private static class Proxy implements IAppManager {
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

            public int[] getPackageInstalledUsers(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createIntArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void scanApps() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void addVisibleOutsidePackage(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void removeVisibleOutsidePackage(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isOutsidePackageVisible(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(5, obtain, obtain2, 0);
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

            public InstalledAppInfo getInstalledAppInfo(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(6, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (InstalledAppInfo) InstalledAppInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public InstallResult installPackage(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    this.mRemote.transact(7, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (InstallResult) InstallResult.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isPackageLaunched(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(8, obtain, obtain2, 0);
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

            public void setPackageHidden(int i, String str, boolean z) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeInt(z ? 1 : 0);
                    this.mRemote.transact(9, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean installPackageAsUser(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(10, obtain, obtain2, 0);
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

            public boolean uninstallPackageAsUser(String str, int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeInt(i);
                    boolean z = false;
                    this.mRemote.transact(11, obtain, obtain2, 0);
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

            public boolean uninstallPackage(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
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

            public boolean clearPackageAsUser(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
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

            public boolean clearPackage(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(14, obtain, obtain2, 0);
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

            public List<InstalledAppInfo> getInstalledApps(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(15, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(InstalledAppInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public List<InstalledAppInfo> getInstalledAppsAsUser(int i, int i2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeInt(i2);
                    this.mRemote.transact(16, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createTypedArrayList(InstalledAppInfo.CREATOR);
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public int getInstalledAppCount() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public boolean isAppInstalled(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(18, obtain, obtain2, 0);
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

            public boolean isAppInstalledAsUser(int i, String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    boolean z = false;
                    this.mRemote.transact(19, obtain, obtain2, 0);
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

            public void registerObserver(IPackageObserver iPackageObserver) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iPackageObserver != null ? iPackageObserver.asBinder() : null);
                    this.mRemote.transact(20, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void unregisterObserver(IPackageObserver iPackageObserver) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iPackageObserver != null ? iPackageObserver.asBinder() : null);
                    this.mRemote.transact(21, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void setAppRequestListener(IAppRequestListener iAppRequestListener) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeStrongBinder(iAppRequestListener != null ? iAppRequestListener.asBinder() : null);
                    this.mRemote.transact(22, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void clearAppRequestListener() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, obtain, obtain2, 0);
                    obtain2.readException();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public IAppRequestListener getAppRequestListener() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, obtain, obtain2, 0);
                    obtain2.readException();
                    return com.lody.virtual.server.interfaces.IAppRequestListener.Stub.asInterface(obtain2.readStrongBinder());
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

        public static IAppManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IAppManager)) {
                return new Proxy(iBinder);
            }
            return (IAppManager) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            IBinder iBinder;
            String str = DESCRIPTOR;
            if (i != 1598968902) {
                boolean z = false;
                switch (i) {
                    case 1:
                        parcel.enforceInterface(str);
                        int[] packageInstalledUsers = getPackageInstalledUsers(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeIntArray(packageInstalledUsers);
                        return true;
                    case 2:
                        parcel.enforceInterface(str);
                        scanApps();
                        parcel2.writeNoException();
                        return true;
                    case 3:
                        parcel.enforceInterface(str);
                        addVisibleOutsidePackage(parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    case 4:
                        parcel.enforceInterface(str);
                        removeVisibleOutsidePackage(parcel.readString());
                        parcel2.writeNoException();
                        return true;
                    case 5:
                        parcel.enforceInterface(str);
                        boolean isOutsidePackageVisible = isOutsidePackageVisible(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(isOutsidePackageVisible ? 1 : 0);
                        return true;
                    case 6:
                        parcel.enforceInterface(str);
                        InstalledAppInfo installedAppInfo = getInstalledAppInfo(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        if (installedAppInfo != null) {
                            parcel2.writeInt(1);
                            installedAppInfo.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 7:
                        parcel.enforceInterface(str);
                        InstallResult installPackage = installPackage(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        if (installPackage != null) {
                            parcel2.writeInt(1);
                            installPackage.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 8:
                        parcel.enforceInterface(str);
                        boolean isPackageLaunched = isPackageLaunched(parcel.readInt(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(isPackageLaunched ? 1 : 0);
                        return true;
                    case 9:
                        parcel.enforceInterface(str);
                        int readInt = parcel.readInt();
                        String readString = parcel.readString();
                        if (parcel.readInt() != 0) {
                            z = true;
                        }
                        setPackageHidden(readInt, readString, z);
                        parcel2.writeNoException();
                        return true;
                    case 10:
                        parcel.enforceInterface(str);
                        boolean installPackageAsUser = installPackageAsUser(parcel.readInt(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(installPackageAsUser ? 1 : 0);
                        return true;
                    case 11:
                        parcel.enforceInterface(str);
                        boolean uninstallPackageAsUser = uninstallPackageAsUser(parcel.readString(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeInt(uninstallPackageAsUser ? 1 : 0);
                        return true;
                    case 12:
                        parcel.enforceInterface(str);
                        boolean uninstallPackage = uninstallPackage(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(uninstallPackage ? 1 : 0);
                        return true;
                    case 13:
                        parcel.enforceInterface(str);
                        boolean clearPackageAsUser = clearPackageAsUser(parcel.readInt(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(clearPackageAsUser ? 1 : 0);
                        return true;
                    case 14:
                        parcel.enforceInterface(str);
                        boolean clearPackage = clearPackage(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(clearPackage ? 1 : 0);
                        return true;
                    case 15:
                        parcel.enforceInterface(str);
                        List installedApps = getInstalledApps(parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeTypedList(installedApps);
                        return true;
                    case 16:
                        parcel.enforceInterface(str);
                        List installedAppsAsUser = getInstalledAppsAsUser(parcel.readInt(), parcel.readInt());
                        parcel2.writeNoException();
                        parcel2.writeTypedList(installedAppsAsUser);
                        return true;
                    case 17:
                        parcel.enforceInterface(str);
                        int installedAppCount = getInstalledAppCount();
                        parcel2.writeNoException();
                        parcel2.writeInt(installedAppCount);
                        return true;
                    case 18:
                        parcel.enforceInterface(str);
                        boolean isAppInstalled = isAppInstalled(parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(isAppInstalled ? 1 : 0);
                        return true;
                    case 19:
                        parcel.enforceInterface(str);
                        boolean isAppInstalledAsUser = isAppInstalledAsUser(parcel.readInt(), parcel.readString());
                        parcel2.writeNoException();
                        parcel2.writeInt(isAppInstalledAsUser ? 1 : 0);
                        return true;
                    case 20:
                        parcel.enforceInterface(str);
                        registerObserver(com.lody.virtual.server.interfaces.IPackageObserver.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 21:
                        parcel.enforceInterface(str);
                        unregisterObserver(com.lody.virtual.server.interfaces.IPackageObserver.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 22:
                        parcel.enforceInterface(str);
                        setAppRequestListener(com.lody.virtual.server.interfaces.IAppRequestListener.Stub.asInterface(parcel.readStrongBinder()));
                        parcel2.writeNoException();
                        return true;
                    case 23:
                        parcel.enforceInterface(str);
                        clearAppRequestListener();
                        parcel2.writeNoException();
                        return true;
                    case 24:
                        parcel.enforceInterface(str);
                        IAppRequestListener appRequestListener = getAppRequestListener();
                        parcel2.writeNoException();
                        if (appRequestListener != null) {
                            iBinder = appRequestListener.asBinder();
                        } else {
                            iBinder = null;
                        }
                        parcel2.writeStrongBinder(iBinder);
                        return true;
                    default:
                        return super.onTransact(i, parcel, parcel2, i2);
                }
            } else {
                parcel2.writeString(str);
                return true;
            }
        }
    }

    void addVisibleOutsidePackage(String str) throws RemoteException;

    void clearAppRequestListener() throws RemoteException;

    boolean clearPackage(String str) throws RemoteException;

    boolean clearPackageAsUser(int i, String str) throws RemoteException;

    IAppRequestListener getAppRequestListener() throws RemoteException;

    int getInstalledAppCount() throws RemoteException;

    InstalledAppInfo getInstalledAppInfo(String str, int i) throws RemoteException;

    List<InstalledAppInfo> getInstalledApps(int i) throws RemoteException;

    List<InstalledAppInfo> getInstalledAppsAsUser(int i, int i2) throws RemoteException;

    int[] getPackageInstalledUsers(String str) throws RemoteException;

    InstallResult installPackage(String str, int i) throws RemoteException;

    boolean installPackageAsUser(int i, String str) throws RemoteException;

    boolean isAppInstalled(String str) throws RemoteException;

    boolean isAppInstalledAsUser(int i, String str) throws RemoteException;

    boolean isOutsidePackageVisible(String str) throws RemoteException;

    boolean isPackageLaunched(int i, String str) throws RemoteException;

    void registerObserver(IPackageObserver iPackageObserver) throws RemoteException;

    void removeVisibleOutsidePackage(String str) throws RemoteException;

    void scanApps() throws RemoteException;

    void setAppRequestListener(IAppRequestListener iAppRequestListener) throws RemoteException;

    void setPackageHidden(int i, String str, boolean z) throws RemoteException;

    boolean uninstallPackage(String str) throws RemoteException;

    boolean uninstallPackageAsUser(String str, int i) throws RemoteException;

    void unregisterObserver(IPackageObserver iPackageObserver) throws RemoteException;
}
