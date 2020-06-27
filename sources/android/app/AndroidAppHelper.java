package android.app;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.IBinder;
import java.lang.ref.WeakReference;
import java.util.Map;
import p011de.robv.android.xposed.XSharedPreferences;
import p011de.robv.android.xposed.XposedBridge;
import p011de.robv.android.xposed.XposedHelpers;

public final class AndroidAppHelper {
    private static final Class<?> CLASS_RESOURCES_KEY;
    private static final boolean HAS_IS_THEMEABLE = (XposedHelpers.findFieldIfExists(CLASS_RESOURCES_KEY, "mIsThemeable") != null);
    private static final boolean HAS_THEME_CONFIG_PARAMETER;

    private AndroidAppHelper() {
    }

    static {
        Class<?> cls;
        if (VERSION.SDK_INT < 19) {
            cls = XposedHelpers.findClass("android.app.ActivityThread$ResourcesKey", null);
        } else {
            cls = XposedHelpers.findClass("android.content.res.ResourcesKey", null);
        }
        CLASS_RESOURCES_KEY = cls;
        boolean z = true;
        if (!HAS_IS_THEMEABLE || VERSION.SDK_INT < 21 || XposedHelpers.findMethodExactIfExists("android.app.ResourcesManager", null, "getThemeConfig", new Object[0]) == null) {
            z = false;
        }
        HAS_THEME_CONFIG_PARAMETER = z;
    }

    private static Map<Object, WeakReference> getResourcesMap(ActivityThread activityThread) {
        if (VERSION.SDK_INT >= 24) {
            return (Map) XposedHelpers.getObjectField(XposedHelpers.getObjectField(activityThread, "mResourcesManager"), "mResourceImpls");
        }
        if (VERSION.SDK_INT >= 19) {
            return (Map) XposedHelpers.getObjectField(XposedHelpers.getObjectField(activityThread, "mResourcesManager"), "mActiveResources");
        }
        return (Map) XposedHelpers.getObjectField(activityThread, "mActiveResources");
    }

    private static Object createResourcesKey(String str, float f) {
        try {
            if (HAS_IS_THEMEABLE) {
                return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Float.valueOf(f), Boolean.valueOf(false));
            }
            return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Float.valueOf(f));
        } catch (Throwable th) {
            XposedBridge.log(th);
            return null;
        }
    }

    private static Object createResourcesKey(String str, int i, Configuration configuration, float f) {
        try {
            if (HAS_THEME_CONFIG_PARAMETER) {
                return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Integer.valueOf(i), configuration, Float.valueOf(f), Boolean.valueOf(false), null);
            } else if (HAS_IS_THEMEABLE) {
                return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Integer.valueOf(i), configuration, Float.valueOf(f), Boolean.valueOf(false));
            } else {
                return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Integer.valueOf(i), configuration, Float.valueOf(f));
            }
        } catch (Throwable th) {
            XposedBridge.log(th);
            return null;
        }
    }

    private static Object createResourcesKey(String str, int i, Configuration configuration, float f, IBinder iBinder) {
        try {
            if (HAS_THEME_CONFIG_PARAMETER) {
                return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Integer.valueOf(i), configuration, Float.valueOf(f), Boolean.valueOf(false), null, iBinder);
            } else if (HAS_IS_THEMEABLE) {
                return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Integer.valueOf(i), configuration, Float.valueOf(f), Boolean.valueOf(false), iBinder);
            } else {
                return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, Integer.valueOf(i), configuration, Float.valueOf(f), iBinder);
            }
        } catch (Throwable th) {
            XposedBridge.log(th);
            return null;
        }
    }

    private static Object createResourcesKey(String str, String[] strArr, String[] strArr2, String[] strArr3, int i, Configuration configuration, CompatibilityInfo compatibilityInfo) {
        try {
            return XposedHelpers.newInstance(CLASS_RESOURCES_KEY, str, strArr, strArr2, strArr3, Integer.valueOf(i), configuration, compatibilityInfo);
        } catch (Throwable th) {
            XposedBridge.log(th);
            return null;
        }
    }

    public static void addActiveResource(String str, float f, boolean z, Resources resources) {
        addActiveResource(str, resources);
    }

    public static void addActiveResource(String str, Resources resources) {
        Object obj;
        ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
        if (currentActivityThread != null) {
            if (VERSION.SDK_INT >= 24) {
                CompatibilityInfo compatibilityInfo = (CompatibilityInfo) XposedHelpers.newInstance(CompatibilityInfo.class, new Object[0]);
                XposedHelpers.setFloatField(compatibilityInfo, "applicationScale", (float) resources.hashCode());
                obj = createResourcesKey(str, null, null, null, 0, null, compatibilityInfo);
            } else if (VERSION.SDK_INT == 23) {
                obj = createResourcesKey(str, 0, null, (float) resources.hashCode());
            } else if (VERSION.SDK_INT >= 19) {
                obj = createResourcesKey(str, 0, null, (float) resources.hashCode(), null);
            } else if (VERSION.SDK_INT >= 17) {
                obj = createResourcesKey(str, 0, null, (float) resources.hashCode());
            } else {
                obj = createResourcesKey(str, (float) resources.hashCode());
            }
            if (obj != null) {
                if (VERSION.SDK_INT >= 24) {
                    getResourcesMap(currentActivityThread).put(obj, new WeakReference(XposedHelpers.getObjectField(resources, "mResourcesImpl")));
                } else {
                    getResourcesMap(currentActivityThread).put(obj, new WeakReference(resources));
                }
            }
        }
    }

    public static String currentProcessName() {
        String currentPackageName = ActivityThread.currentPackageName();
        return currentPackageName == null ? "android" : currentPackageName;
    }

    public static ApplicationInfo currentApplicationInfo() {
        ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
        if (currentActivityThread == null) {
            return null;
        }
        Object objectField = XposedHelpers.getObjectField(currentActivityThread, "mBoundApplication");
        if (objectField == null) {
            return null;
        }
        return (ApplicationInfo) XposedHelpers.getObjectField(objectField, "appInfo");
    }

    public static String currentPackageName() {
        ApplicationInfo currentApplicationInfo = currentApplicationInfo();
        return currentApplicationInfo != null ? currentApplicationInfo.packageName : "android";
    }

    public static Application currentApplication() {
        return ActivityThread.currentApplication();
    }

    @Deprecated
    public static SharedPreferences getSharedPreferencesForPackage(String str, String str2, int i) {
        return new XSharedPreferences(str, str2);
    }

    @Deprecated
    public static SharedPreferences getDefaultSharedPreferencesForPackage(String str) {
        return new XSharedPreferences(str);
    }

    @Deprecated
    public static void reloadSharedPreferencesIfNeeded(SharedPreferences sharedPreferences) {
        if (sharedPreferences instanceof XSharedPreferences) {
            ((XSharedPreferences) sharedPreferences).reload();
        }
    }
}
