package com.lody.virtual.client.hook.proxies.location;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.LogInvocation;
import com.lody.virtual.client.hook.base.LogInvocation.Condition;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import java.lang.reflect.Method;
import mirror.android.location.ILocationManager.Stub;

@Inject(MethodProxies.class)
@LogInvocation(Condition.ALWAYS)
public class LocationManagerStub extends BinderInvocationProxy {

    private static class FakeReplaceLastPkgMethodProxy extends ReplaceLastPkgMethodProxy {
        private Object mDefValue;

        private FakeReplaceLastPkgMethodProxy(String str, Object obj) {
            super(str);
            this.mDefValue = obj;
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (isFakeLocationEnable()) {
                return this.mDefValue;
            }
            return super.call(obj, method, objArr);
        }
    }

    public LocationManagerStub() {
        super(Stub.asInterface, "location");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        if (VERSION.SDK_INT >= 23) {
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("addTestProvider"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("removeTestProvider"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setTestProviderLocation"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("clearTestProviderLocation"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setTestProviderEnabled"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("clearTestProviderEnabled"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("setTestProviderStatus"));
            addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("clearTestProviderStatus"));
        }
        if (VERSION.SDK_INT >= 21) {
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("addGpsMeasurementsListener", Boolean.valueOf(true)));
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("addGpsNavigationMessageListener", Boolean.valueOf(true)));
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("removeGpsMeasurementListener", Integer.valueOf(0)));
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("removeGpsNavigationMessageListener", Integer.valueOf(0)));
        }
        if (VERSION.SDK_INT >= 17) {
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("requestGeofence", Integer.valueOf(0)));
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("removeGeofence", Integer.valueOf(0)));
        }
        if (VERSION.SDK_INT <= 16) {
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("addProximityAlert", Integer.valueOf(0)));
        }
        if (VERSION.SDK_INT >= 17) {
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("addNmeaListener", Integer.valueOf(0)));
            addMethodProxy((MethodProxy) new FakeReplaceLastPkgMethodProxy("removeNmeaListener", Integer.valueOf(0)));
        }
    }
}
