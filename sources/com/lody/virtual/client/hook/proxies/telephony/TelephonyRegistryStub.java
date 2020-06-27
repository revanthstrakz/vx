package com.lody.virtual.client.hook.proxies.telephony;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ReplaceSequencePkgMethodProxy;
import java.lang.reflect.Method;
import mirror.com.android.internal.telephony.ITelephonyRegistry.Stub;

public class TelephonyRegistryStub extends BinderInvocationProxy {
    public TelephonyRegistryStub() {
        super(Stub.asInterface, "telephony.registry");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("listen"));
        addMethodProxy((MethodProxy) new ReplaceSequencePkgMethodProxy("listenForSubscriber", 1) {
            public boolean beforeCall(Object obj, Method method, Object... objArr) {
                if (VERSION.SDK_INT >= 17 && isFakeLocationEnable()) {
                    int length = objArr.length - 1;
                    while (true) {
                        if (length <= 0) {
                            break;
                        } else if (objArr[length] instanceof Integer) {
                            objArr[length] = Integer.valueOf((objArr[length].intValue() ^ 1024) ^ 16);
                            break;
                        } else {
                            length--;
                        }
                    }
                }
                return super.beforeCall(obj, method, objArr);
            }
        });
    }
}
