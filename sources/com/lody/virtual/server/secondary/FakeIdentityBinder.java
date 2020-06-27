package com.lody.virtual.server.secondary;

import android.os.Binder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;

public class FakeIdentityBinder extends Binder {
    private Binder mBase;

    public FakeIdentityBinder(Binder binder) {
        this.mBase = binder;
    }

    public final void attachInterface(IInterface iInterface, String str) {
        this.mBase.attachInterface(iInterface, str);
    }

    public final String getInterfaceDescriptor() {
        return this.mBase.getInterfaceDescriptor();
    }

    public final boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Binder.restoreCallingIdentity(getFakeIdentity());
            return this.mBase.transact(i, parcel, parcel2, i2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* access modifiers changed from: protected */
    public long getFakeIdentity() {
        return (((long) getFakeUid()) << 32) | ((long) getFakePid());
    }

    /* access modifiers changed from: protected */
    public int getFakeUid() {
        return Process.myUid();
    }

    /* access modifiers changed from: protected */
    public int getFakePid() {
        return Process.myPid();
    }

    public final IInterface queryLocalInterface(String str) {
        return this.mBase.queryLocalInterface(str);
    }
}
