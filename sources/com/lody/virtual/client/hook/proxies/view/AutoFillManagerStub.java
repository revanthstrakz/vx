package com.lody.virtual.client.hook.proxies.view;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.util.Log;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.BinderInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.microsoft.appcenter.analytics.ingestion.models.StartSessionLog;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import mirror.android.view.IAutoFillManager.Stub;

public class AutoFillManagerStub extends BinderInvocationProxy {
    private static final String AUTO_FILL_NAME = "autofill";
    private static final String TAG = "AutoFillManagerStub";

    static class ReplacePkgAndComponentProxy extends ReplaceLastPkgMethodProxy {
        ReplacePkgAndComponentProxy(String str) {
            super(str);
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            replaceLastAppComponent(objArr, getHostPkg());
            return super.beforeCall(obj, method, objArr);
        }

        static ComponentName replaceLastAppComponent(Object[] objArr, String str) {
            int indexOfLast = ArrayUtils.indexOfLast(objArr, ComponentName.class);
            if (indexOfLast == -1) {
                return null;
            }
            ComponentName componentName = new ComponentName(str, ((ComponentName) objArr[indexOfLast]).getClassName());
            objArr[indexOfLast] = componentName;
            return componentName;
        }
    }

    public AutoFillManagerStub() {
        super(Stub.asInterface, AUTO_FILL_NAME);
    }

    @SuppressLint({"WrongConstant"})
    public void inject() throws Throwable {
        super.inject();
        try {
            Object systemService = getContext().getSystemService(AUTO_FILL_NAME);
            if (systemService != null) {
                Object proxyInterface = ((BinderInvocationStub) getInvocationStub()).getProxyInterface();
                if (proxyInterface != null) {
                    Field declaredField = systemService.getClass().getDeclaredField("mService");
                    declaredField.setAccessible(true);
                    declaredField.set(systemService, proxyInterface);
                    addMethodProxy((MethodProxy) new ReplacePkgAndComponentProxy(StartSessionLog.TYPE));
                    addMethodProxy((MethodProxy) new ReplacePkgAndComponentProxy("updateOrRestartSession"));
                    addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("isServiceEnabled"));
                    return;
                }
                throw new NullPointerException("AutoFillManagerProxy is null.");
            }
            throw new NullPointerException("AutoFillManagerInstance is null.");
        } catch (Throwable th) {
            Log.e(TAG, "AutoFillManagerStub inject error.", th);
        }
    }
}
