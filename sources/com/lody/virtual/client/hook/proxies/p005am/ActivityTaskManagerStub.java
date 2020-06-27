package com.lody.virtual.client.hook.proxies.p005am;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.ipc.VActivityManager;
import java.lang.reflect.Method;
import mirror.android.app.IActivityTaskManager.Stub;

@Inject(MethodProxies.class)
/* renamed from: com.lody.virtual.client.hook.proxies.am.ActivityTaskManagerStub */
public class ActivityTaskManagerStub extends BinderInvocationProxy {
    public ActivityTaskManagerStub() {
        super(Stub.TYPE, "activity_task");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new StaticMethodProxy("activityDestroyed") {
            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                VActivityManager.get().onActivityDestroy(objArr[0]);
                return super.call(obj, method, objArr);
            }
        });
        addMethodProxy((MethodProxy) new StaticMethodProxy("activityResumed") {
            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                VActivityManager.get().onActivityResumed(objArr[0]);
                return super.call(obj, method, objArr);
            }
        });
        addMethodProxy((MethodProxy) new StaticMethodProxy("finishActivity") {
            public Object call(Object obj, Method method, Object... objArr) throws Throwable {
                VActivityManager.get().finishActivity(objArr[0]);
                return super.call(obj, method, objArr);
            }
        });
    }
}
