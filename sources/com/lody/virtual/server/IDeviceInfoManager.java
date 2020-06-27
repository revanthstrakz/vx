package com.lody.virtual.server;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.lody.virtual.remote.VDeviceInfo;

public interface IDeviceInfoManager extends IInterface {

    public static abstract class Stub extends Binder implements IDeviceInfoManager {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IDeviceInfoManager";
        static final int TRANSACTION_getDeviceInfo = 1;
        static final int TRANSACTION_updateDeviceInfo = 2;

        private static class Proxy implements IDeviceInfoManager {
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

            public VDeviceInfo getDeviceInfo(int i) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? (VDeviceInfo) VDeviceInfo.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public void updateDeviceInfo(int i, VDeviceInfo vDeviceInfo) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    if (vDeviceInfo != null) {
                        obtain.writeInt(1);
                        vDeviceInfo.writeToParcel(obtain, 0);
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
        }

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDeviceInfoManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IDeviceInfoManager)) {
                return new Proxy(iBinder);
            }
            return (IDeviceInfoManager) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            String str = DESCRIPTOR;
            if (i != 1598968902) {
                switch (i) {
                    case 1:
                        parcel.enforceInterface(str);
                        VDeviceInfo deviceInfo = getDeviceInfo(parcel.readInt());
                        parcel2.writeNoException();
                        if (deviceInfo != null) {
                            parcel2.writeInt(1);
                            deviceInfo.writeToParcel(parcel2, 1);
                        } else {
                            parcel2.writeInt(0);
                        }
                        return true;
                    case 2:
                        parcel.enforceInterface(str);
                        updateDeviceInfo(parcel.readInt(), parcel.readInt() != 0 ? (VDeviceInfo) VDeviceInfo.CREATOR.createFromParcel(parcel) : null);
                        parcel2.writeNoException();
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

    VDeviceInfo getDeviceInfo(int i) throws RemoteException;

    void updateDeviceInfo(int i, VDeviceInfo vDeviceInfo) throws RemoteException;
}
