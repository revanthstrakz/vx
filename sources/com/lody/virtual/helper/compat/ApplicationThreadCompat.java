package com.lody.virtual.helper.compat;

import android.os.IBinder;
import android.os.IInterface;
import mirror.android.app.ApplicationThreadNative;
import mirror.android.app.IApplicationThreadOreo.Stub;

public class ApplicationThreadCompat {
    public static IInterface asInterface(IBinder iBinder) {
        if (BuildCompat.isOreo()) {
            return (IInterface) Stub.asInterface.call(iBinder);
        }
        return (IInterface) ApplicationThreadNative.asInterface.call(iBinder);
    }
}
