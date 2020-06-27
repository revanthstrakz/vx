package com.lody.virtual.client.hook.proxies.p005am;

import android.app.ClientTransactionHandler;
import android.app.TransactionHandlerProxy;
import android.util.Log;
import com.lody.virtual.client.interfaces.IInjector;
import java.lang.reflect.Field;
import mirror.android.app.ActivityThread;

/* renamed from: com.lody.virtual.client.hook.proxies.am.TransactionHandlerStub */
public class TransactionHandlerStub implements IInjector {
    private static final String TAG = "TransactionHandlerStub";

    public boolean isEnvBad() {
        return false;
    }

    public void inject() throws Throwable {
        Log.i(TAG, "inject transaction handler.");
        Object obj = ActivityThread.mTransactionExecutor.get(ActivityThread.currentActivityThread.call(new Object[0]));
        Field declaredField = obj.getClass().getDeclaredField("mTransactionHandler");
        declaredField.setAccessible(true);
        declaredField.set(obj, new TransactionHandlerProxy((ClientTransactionHandler) declaredField.get(obj)));
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("executor's handler: ");
        sb.append(declaredField.get(obj));
        Log.i(str, sb.toString());
    }
}
