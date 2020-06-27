package com.lody.virtual.helper.compat;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import mirror.android.p017os.BaseBundle;
import mirror.android.p017os.BundleICS;

public class BundleCompat {
    public static IBinder getBinder(Bundle bundle, String str) {
        if (VERSION.SDK_INT >= 18) {
            return bundle.getBinder(str);
        }
        return (IBinder) mirror.android.p017os.Bundle.getIBinder.call(bundle, str);
    }

    public static void putBinder(Bundle bundle, String str, IBinder iBinder) {
        if (VERSION.SDK_INT >= 18) {
            bundle.putBinder(str, iBinder);
            return;
        }
        mirror.android.p017os.Bundle.putIBinder.call(bundle, str, iBinder);
    }

    public static void clearParcelledData(Bundle bundle) {
        Parcel obtain = Parcel.obtain();
        obtain.writeInt(0);
        obtain.setDataPosition(0);
        if (BaseBundle.TYPE != null) {
            Parcel parcel = (Parcel) BaseBundle.mParcelledData.get(bundle);
            if (parcel != null) {
                parcel.recycle();
            }
            BaseBundle.mParcelledData.set(bundle, obtain);
        } else if (BundleICS.TYPE != null) {
            Parcel parcel2 = (Parcel) BundleICS.mParcelledData.get(bundle);
            if (parcel2 != null) {
                parcel2.recycle();
            }
            BundleICS.mParcelledData.set(bundle, obtain);
        }
    }
}
