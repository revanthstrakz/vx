package com.lody.virtual.client.hook.proxies.p006pm;

import android.os.Build.VERSION;
import android.os.IInterface;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.helper.compat.BuildCompat;
import mirror.android.app.ActivityThread;

@Inject(MethodProxies.class)
/* renamed from: com.lody.virtual.client.hook.proxies.pm.PackageManagerStub */
public final class PackageManagerStub extends MethodInvocationProxy<MethodInvocationStub<IInterface>> {
    public PackageManagerStub() {
        super(new MethodInvocationStub(ActivityThread.sPackageManager.get()));
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("addPermissionAsync", Boolean.valueOf(true)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("addPermission", Boolean.valueOf(true)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("performDexOpt", Boolean.valueOf(true)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("performDexOptIfNeeded", Boolean.valueOf(false)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("performDexOptSecondary", Boolean.valueOf(true)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("addOnPermissionsChangeListener", Integer.valueOf(0)));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("removeOnPermissionsChangeListener", Integer.valueOf(0)));
        if (VERSION.SDK_INT >= 24) {
            addMethodProxy((MethodProxy) new ResultStaticMethodProxy("checkPackageStartable", Integer.valueOf(0)));
        }
        if (BuildCompat.isOreo()) {
            addMethodProxy((MethodProxy) new ResultStaticMethodProxy("notifyDexLoad", Integer.valueOf(0)));
            addMethodProxy((MethodProxy) new ResultStaticMethodProxy("notifyPackageUse", Integer.valueOf(0)));
            addMethodProxy((MethodProxy) new ResultStaticMethodProxy("setInstantAppCookie", Boolean.valueOf(false)));
            addMethodProxy((MethodProxy) new ResultStaticMethodProxy("isInstantApp", Boolean.valueOf(false)));
        }
    }

    public void inject() throws Throwable {
        ActivityThread.sPackageManager.set((IInterface) getInvocationStub().getProxyInterface());
        BinderInvocationStub binderInvocationStub = new BinderInvocationStub((IInterface) getInvocationStub().getBaseInterface());
        binderInvocationStub.copyMethodProxies(getInvocationStub());
        binderInvocationStub.replaceService(ServiceManagerNative.PACKAGE);
    }

    public boolean isEnvBad() {
        return getInvocationStub().getProxyInterface() != ActivityThread.sPackageManager.get();
    }
}
