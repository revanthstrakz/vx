package com.lody.virtual.client.hook.proxies.search;

import android.annotation.TargetApi;
import android.content.ComponentName;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import java.lang.reflect.Method;
import mirror.android.app.ISearchManager.Stub;

@TargetApi(17)
public class SearchManagerStub extends BinderInvocationProxy {

    private static class GetSearchableInfo extends MethodProxy {
        public String getMethodName() {
            return "getSearchableInfo";
        }

        private GetSearchableInfo() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ComponentName componentName = objArr[0];
            if (componentName == null || VirtualCore.getPM().getActivityInfo(componentName, 0) == null) {
                return method.invoke(obj, objArr);
            }
            return null;
        }
    }

    public SearchManagerStub() {
        super(Stub.asInterface, "search");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new StaticMethodProxy("launchLegacyAssist"));
        addMethodProxy((MethodProxy) new GetSearchableInfo());
    }
}
