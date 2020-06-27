package com.lody.virtual.client.hook.proxies.mount;

import android.os.IInterface;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.helper.compat.BuildCompat;
import mirror.RefStaticMethod;
import mirror.android.p017os.mount.IMountService;
import mirror.android.p017os.storage.IStorageManager.Stub;

@Inject(MethodProxies.class)
public class MountServiceStub extends BinderInvocationProxy {
    public MountServiceStub() {
        super(getInterfaceMethod(), "mount");
    }

    private static RefStaticMethod<IInterface> getInterfaceMethod() {
        if (BuildCompat.isOreo()) {
            return Stub.asInterface;
        }
        return IMountService.Stub.asInterface;
    }
}
