package com.lody.virtual.client.hook.proxies.bluetooth;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import java.lang.reflect.Method;
import mirror.android.bluetooth.IBluetooth.Stub;

public class BluetoothStub extends BinderInvocationProxy {
    public static final String SERVICE_NAME = (VERSION.SDK_INT >= 17 ? "bluetooth_manager" : "bluetooth");

    private static class GetAddress extends StaticMethodProxy {
        GetAddress() {
            super("getAddress");
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return getDeviceInfo().bluetoothMac;
        }
    }

    public BluetoothStub() {
        super(Stub.asInterface, SERVICE_NAME);
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new GetAddress());
    }
}
