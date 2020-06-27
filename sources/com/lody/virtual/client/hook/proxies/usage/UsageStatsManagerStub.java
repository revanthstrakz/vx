package com.lody.virtual.client.hook.proxies.usage;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import mirror.android.app.IUsageStatsManager.Stub;

@TargetApi(22)
public class UsageStatsManagerStub extends BinderInvocationProxy {
    public UsageStatsManagerStub() {
        super(Stub.asInterface, "usagestats");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("queryUsageStats"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("queryConfigurations"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("queryEvents"));
    }
}
