package p013io.virtualapp.settings;

import android.content.Context;
import android.os.Build.VERSION;

/* renamed from: io.virtualapp.settings.NougatPolicy */
public class NougatPolicy {
    static boolean fullCompile(Context context) {
        if (VERSION.SDK_INT < 24) {
            return true;
        }
        try {
            Object packageManagerBinderProxy = getPackageManagerBinderProxy();
            if (packageManagerBinderProxy == null) {
                return false;
            }
            return ((Boolean) packageManagerBinderProxy.getClass().getDeclaredMethod("performDexOptMode", new Class[]{String.class, Boolean.TYPE, String.class, Boolean.TYPE}).invoke(packageManagerBinderProxy, new Object[]{context.getPackageName(), Boolean.valueOf(false), "speed", Boolean.valueOf(true)})).booleanValue();
        } catch (Throwable unused) {
            return false;
        }
    }

    public static boolean clearCompileData(Context context) {
        try {
            Object packageManagerBinderProxy = getPackageManagerBinderProxy();
            return ((Boolean) packageManagerBinderProxy.getClass().getDeclaredMethod("performDexOpt", new Class[]{String.class, Boolean.TYPE, Integer.TYPE, Boolean.TYPE}).invoke(packageManagerBinderProxy, new Object[]{context.getPackageName(), Boolean.valueOf(false), Integer.valueOf(2), Boolean.valueOf(true)})).booleanValue();
        } catch (Throwable unused) {
            return false;
        }
    }

    private static Object getPackageManagerBinderProxy() throws Exception {
        return Class.forName("android.app.ActivityThread").getDeclaredMethod("getPackageManager", new Class[0]).invoke(null, new Object[0]);
    }
}
