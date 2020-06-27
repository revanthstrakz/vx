package com.lody.virtual.client.hook.proxies.devicepolicy;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import java.lang.reflect.Method;
import mirror.android.app.admin.IDevicePolicyManager.Stub;

public class DevicePolicyManagerStub extends BinderInvocationProxy {

    private static class GetStorageEncryptionStatus extends MethodProxy {
        public String getMethodName() {
            return "getStorageEncryptionStatus";
        }

        private GetStorageEncryptionStatus() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            objArr[0] = VirtualCore.get().getHostPkg();
            return method.invoke(obj, objArr);
        }
    }

    public DevicePolicyManagerStub() {
        super(Stub.asInterface, "device_policy");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new GetStorageEncryptionStatus());
    }
}
