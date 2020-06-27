package com.lody.virtual.client.hook.providers;

import com.lody.virtual.client.core.VirtualCore;
import java.lang.reflect.Method;

public class ExternalProviderHook extends ProviderHook {
    public ExternalProviderHook(Object obj) {
        super(obj);
    }

    /* access modifiers changed from: protected */
    public void processArgs(Method method, Object... objArr) {
        if (objArr != null && objArr.length > 0 && (objArr[0] instanceof String)) {
            if (VirtualCore.get().isAppInstalled(objArr[0])) {
                objArr[0] = VirtualCore.get().getHostPkg();
            }
        }
    }
}
