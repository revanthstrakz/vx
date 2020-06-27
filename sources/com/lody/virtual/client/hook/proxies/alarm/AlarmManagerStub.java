package com.lody.virtual.client.hook.proxies.alarm;

import android.os.Build.VERSION;
import android.os.WorkSource;
import android.support.p001v4.app.NotificationCompat;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.helper.utils.ArrayUtils;
import java.lang.reflect.Method;
import mirror.android.app.IAlarmManager.Stub;

public class AlarmManagerStub extends BinderInvocationProxy {

    private static class Set extends MethodProxy {
        public String getMethodName() {
            return "set";
        }

        private Set() {
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            if (VERSION.SDK_INT >= 24 && (objArr[0] instanceof String)) {
                objArr[0] = getHostPkg();
            }
            int indexOfFirst = ArrayUtils.indexOfFirst(objArr, WorkSource.class);
            if (indexOfFirst >= 0) {
                objArr[indexOfFirst] = null;
            }
            return true;
        }
    }

    private static class SetTime extends MethodProxy {
        public String getMethodName() {
            return "setTime";
        }

        private SetTime() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (VERSION.SDK_INT >= 21) {
                return Boolean.valueOf(false);
            }
            return null;
        }
    }

    private static class SetTimeZone extends MethodProxy {
        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return null;
        }

        public String getMethodName() {
            return "setTimeZone";
        }

        private SetTimeZone() {
        }
    }

    public AlarmManagerStub() {
        super(Stub.asInterface, NotificationCompat.CATEGORY_ALARM);
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new Set());
        addMethodProxy((MethodProxy) new SetTime());
        addMethodProxy((MethodProxy) new SetTimeZone());
    }
}
