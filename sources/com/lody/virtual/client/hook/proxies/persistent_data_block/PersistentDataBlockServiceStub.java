package com.lody.virtual.client.hook.proxies.persistent_data_block;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import mirror.android.service.persistentdata.IPersistentDataBlockService.Stub;

public class PersistentDataBlockServiceStub extends BinderInvocationProxy {
    public PersistentDataBlockServiceStub() {
        super(Stub.asInterface, "persistent_data_block");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("write", Integer.valueOf(-1)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("read", new byte[0]));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("wipe", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getDataBlockSize", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getMaximumDataBlockSize", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("setOemUnlockEnabled", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getOemUnlockEnabled", Boolean.valueOf(false)));
    }
}
