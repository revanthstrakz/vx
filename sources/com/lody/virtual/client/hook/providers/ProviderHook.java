package com.lody.virtual.client.hook.providers;

import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IInterface;
import android.os.ParcelFileDescriptor;
import android.support.p001v4.app.NotificationCompat;
import com.lody.virtual.client.hook.base.MethodBox;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.utils.VLog;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import mirror.android.content.IContentProvider;

public class ProviderHook implements InvocationHandler {
    private static final Map<String, HookFetcher> PROVIDER_MAP = new HashMap();
    public static final String QUERY_ARG_SQL_SELECTION = "android:query-arg-sql-selection";
    public static final String QUERY_ARG_SQL_SELECTION_ARGS = "android:query-arg-sql-selection-args";
    public static final String QUERY_ARG_SQL_SORT_ORDER = "android:query-arg-sql-sort-order";
    protected final Object mBase;

    public interface HookFetcher {
        ProviderHook fetch(boolean z, IInterface iInterface);
    }

    /* access modifiers changed from: protected */
    public void processArgs(Method method, Object... objArr) {
    }

    static {
        PROVIDER_MAP.put("settings", new HookFetcher() {
            public ProviderHook fetch(boolean z, IInterface iInterface) {
                return new SettingsProviderHook(iInterface);
            }
        });
        PROVIDER_MAP.put("downloads", new HookFetcher() {
            public ProviderHook fetch(boolean z, IInterface iInterface) {
                return new DownloadProviderHook(iInterface);
            }
        });
        PROVIDER_MAP.put("media", new HookFetcher() {
            public ProviderHook fetch(boolean z, IInterface iInterface) {
                return new MediaProviderHook(iInterface);
            }
        });
    }

    public ProviderHook(Object obj) {
        this.mBase = obj;
    }

    private static HookFetcher fetchHook(String str) {
        HookFetcher hookFetcher = (HookFetcher) PROVIDER_MAP.get(str);
        return hookFetcher == null ? new HookFetcher() {
            public ProviderHook fetch(boolean z, IInterface iInterface) {
                if (z) {
                    return new ExternalProviderHook(iInterface);
                }
                return new InternalProviderHook(iInterface);
            }
        } : hookFetcher;
    }

    private static IInterface createProxy(IInterface iInterface, ProviderHook providerHook) {
        if (iInterface == null || providerHook == null) {
            return null;
        }
        return (IInterface) Proxy.newProxyInstance(iInterface.getClass().getClassLoader(), new Class[]{IContentProvider.TYPE}, providerHook);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001b, code lost:
        if (r1 != null) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.os.IInterface createProxy(boolean r1, java.lang.String r2, android.os.IInterface r3) {
        /*
            boolean r0 = r3 instanceof java.lang.reflect.Proxy
            if (r0 == 0) goto L_0x000d
            java.lang.reflect.InvocationHandler r0 = java.lang.reflect.Proxy.getInvocationHandler(r3)
            boolean r0 = r0 instanceof com.lody.virtual.client.hook.providers.ProviderHook
            if (r0 == 0) goto L_0x000d
            return r3
        L_0x000d:
            com.lody.virtual.client.hook.providers.ProviderHook$HookFetcher r2 = fetchHook(r2)
            if (r2 == 0) goto L_0x001e
            com.lody.virtual.client.hook.providers.ProviderHook r1 = r2.fetch(r1, r3)
            android.os.IInterface r1 = createProxy(r3, r1)
            if (r1 == 0) goto L_0x001e
            goto L_0x001f
        L_0x001e:
            r1 = r3
        L_0x001f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.hook.providers.ProviderHook.createProxy(boolean, java.lang.String, android.os.IInterface):android.os.IInterface");
    }

    public Bundle call(MethodBox methodBox, String str, String str2, Bundle bundle) throws InvocationTargetException {
        return (Bundle) methodBox.call();
    }

    public Uri insert(MethodBox methodBox, Uri uri, ContentValues contentValues) throws InvocationTargetException {
        return (Uri) methodBox.call();
    }

    public Cursor query(MethodBox methodBox, Uri uri, String[] strArr, String str, String[] strArr2, String str2, Bundle bundle) throws InvocationTargetException {
        return (Cursor) methodBox.call();
    }

    public String getType(MethodBox methodBox, Uri uri) throws InvocationTargetException {
        return (String) methodBox.call();
    }

    public int bulkInsert(MethodBox methodBox, Uri uri, ContentValues[] contentValuesArr) throws InvocationTargetException {
        return ((Integer) methodBox.call()).intValue();
    }

    public int delete(MethodBox methodBox, Uri uri, String str, String[] strArr) throws InvocationTargetException {
        return ((Integer) methodBox.call()).intValue();
    }

    public int update(MethodBox methodBox, Uri uri, ContentValues contentValues, String str, String[] strArr) throws InvocationTargetException {
        return ((Integer) methodBox.call()).intValue();
    }

    public ParcelFileDescriptor openFile(MethodBox methodBox, Uri uri, String str) throws InvocationTargetException {
        return (ParcelFileDescriptor) methodBox.call();
    }

    public AssetFileDescriptor openAssetFile(MethodBox methodBox, Uri uri, String str) throws InvocationTargetException {
        return (AssetFileDescriptor) methodBox.call();
    }

    public Object invoke(Object obj, Method method, Object... objArr) throws Throwable {
        Bundle bundle;
        String str;
        String str2;
        String[] strArr;
        String str3;
        try {
            processArgs(method, objArr);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        MethodBox methodBox = new MethodBox(method, this.mBase, objArr);
        int i = VERSION.SDK_INT >= 18 ? 1 : 0;
        try {
            String name = method.getName();
            if (NotificationCompat.CATEGORY_CALL.equals(name)) {
                if (BuildCompat.isQ()) {
                    i = 2;
                }
                return call(methodBox, objArr[i], objArr[i + 1], objArr[i + 2]);
            } else if ("insert".equals(name)) {
                return insert(methodBox, objArr[i], objArr[i + 1]);
            } else {
                if ("getType".equals(name)) {
                    return getType(methodBox, objArr[0]);
                }
                if ("delete".equals(name)) {
                    return Integer.valueOf(delete(methodBox, objArr[i], objArr[i + 1], objArr[i + 2]));
                }
                if ("bulkInsert".equals(name)) {
                    return Integer.valueOf(bulkInsert(methodBox, objArr[i], objArr[i + 1]));
                }
                if ("update".equals(name)) {
                    return Integer.valueOf(update(methodBox, (Uri) objArr[i], (ContentValues) objArr[i + 1], (String) objArr[i + 2], objArr[i + 3]));
                } else if ("openFile".equals(name)) {
                    return openFile(methodBox, objArr[i], objArr[i + 1]);
                } else {
                    if ("openAssetFile".equals(name)) {
                        return openAssetFile(methodBox, objArr[i], objArr[i + 1]);
                    }
                    if (!"query".equals(name)) {
                        return methodBox.call();
                    }
                    Uri uri = (Uri) objArr[i];
                    String[] strArr2 = objArr[i + 1];
                    String[] strArr3 = null;
                    if (VERSION.SDK_INT >= 26) {
                        Bundle bundle2 = objArr[i + 2];
                        if (bundle2 != null) {
                            str3 = bundle2.getString(QUERY_ARG_SQL_SELECTION);
                            strArr = bundle2.getStringArray(QUERY_ARG_SQL_SELECTION_ARGS);
                            bundle = bundle2;
                            str = bundle2.getString(QUERY_ARG_SQL_SORT_ORDER);
                        } else {
                            bundle = bundle2;
                            str2 = null;
                            str = null;
                            return query(methodBox, uri, strArr2, str2, strArr3, str, bundle);
                        }
                    } else {
                        str3 = objArr[i + 2];
                        strArr = objArr[i + 3];
                        str = objArr[i + 4];
                        bundle = null;
                    }
                    strArr3 = strArr;
                    str2 = str3;
                    return query(methodBox, uri, strArr2, str2, strArr3, str, bundle);
                }
            }
        } catch (Throwable th2) {
            VLog.m86d("ProviderHook", "call: %s (%s) with error", method.getName(), Arrays.toString(objArr));
            if (th2 instanceof InvocationTargetException) {
                throw th2.getCause();
            }
            throw th2;
        }
    }
}
