package com.lody.virtual.client.hook.proxies.vibrator;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import java.lang.reflect.Method;
import mirror.com.android.internal.p018os.IVibratorService.Stub;

public class VibratorStub extends BinderInvocationProxy {

    private static final class VibrateMethodProxy extends ReplaceCallingPkgMethodProxy {
        private VibrateMethodProxy(String str) {
            super(str);
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            if (objArr[0] instanceof Integer) {
                objArr[0] = Integer.valueOf(getRealUid());
            }
            return super.beforeCall(obj, method, objArr);
        }
    }

    public VibratorStub() {
        super(Stub.asInterface, "vibrator");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        addMethodProxy((MethodProxy) new VibrateMethodProxy("vibrateMagnitude"));
        addMethodProxy((MethodProxy) new VibrateMethodProxy("vibratePatternMagnitude"));
        addMethodProxy((MethodProxy) new VibrateMethodProxy("vibrate"));
        addMethodProxy((MethodProxy) new VibrateMethodProxy("vibratePattern"));
    }
}
