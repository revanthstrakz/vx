package com.lody.virtual.client.hook.proxies.appops;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import java.lang.reflect.Method;
import mirror.com.android.internal.app.IAppOpsService.Stub;

@TargetApi(19)
public class AppOpsManagerStub extends BinderInvocationProxy {

    private class BaseMethodProxy extends StaticMethodProxy {
        final int pkgIndex;
        final int uidIndex;

        BaseMethodProxy(String str, int i, int i2) {
            super(str);
            this.pkgIndex = i2;
            this.uidIndex = i;
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            if (this.pkgIndex != -1 && objArr.length > this.pkgIndex && (objArr[this.pkgIndex] instanceof String)) {
                objArr[this.pkgIndex] = getHostPkg();
            }
            if (this.uidIndex != -1 && (objArr[this.uidIndex] instanceof Integer)) {
                objArr[this.uidIndex] = Integer.valueOf(getRealUid());
            }
            return true;
        }
    }

    public AppOpsManagerStub() {
        super(Stub.asInterface, "appops");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new BaseMethodProxy("checkOperation", 1, 2));
        addMethodProxy((MethodProxy) new BaseMethodProxy("noteOperation", 1, 2));
        addMethodProxy((MethodProxy) new BaseMethodProxy("startOperation", 2, 3));
        addMethodProxy((MethodProxy) new BaseMethodProxy("finishOperation", 2, 3));
        addMethodProxy((MethodProxy) new BaseMethodProxy("startWatchingMode", -1, 1));
        addMethodProxy((MethodProxy) new BaseMethodProxy("checkPackage", 0, 1));
        addMethodProxy((MethodProxy) new BaseMethodProxy("getOpsForPackage", 0, 1));
        addMethodProxy((MethodProxy) new BaseMethodProxy("setMode", 1, 2));
        addMethodProxy((MethodProxy) new BaseMethodProxy("checkAudioOperation", 2, 3));
        addMethodProxy((MethodProxy) new BaseMethodProxy("setAudioRestriction", 2, -1));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("resetAllModes"));
        addMethodProxy((MethodProxy) new MethodProxy() {
            public String getMethodName() {
                return "noteProxyOperation";
            }

            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                return Integer.valueOf(0);
            }
        });
    }
}
