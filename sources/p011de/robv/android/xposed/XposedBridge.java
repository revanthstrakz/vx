package p011de.robv.android.xposed;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.os.RuntimeInit;
import com.android.internal.os.ZygoteInit;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import p011de.robv.android.xposed.XC_MethodHook.Unhook;
import p011de.robv.android.xposed.callbacks.XC_InitPackageResources;
import p011de.robv.android.xposed.callbacks.XC_LoadPackage;

/* renamed from: de.robv.android.xposed.XposedBridge */
public final class XposedBridge {
    public static final ClassLoader BOOTCLASSLOADER = ClassLoader.getSystemClassLoader();
    static long BOOT_START_TIME = 0;
    /* access modifiers changed from: private */
    public static final Object[] EMPTY_ARRAY = new Object[0];
    private static final int RUNTIME_ART = 2;
    private static final int RUNTIME_DALVIK = 1;
    public static final String TAG = "Xposed";
    @Deprecated
    public static int XPOSED_BRIDGE_VERSION;
    static boolean disableHooks = false;
    static boolean isZygote = true;
    private static int runtime = 0;
    private static final Map<Member, CopyOnWriteSortedSet<XC_MethodHook>> sHookedMethodCallbacks = new HashMap();
    static final CopyOnWriteSortedSet<XC_InitPackageResources> sInitPackageResourcesCallbacks = new CopyOnWriteSortedSet<>();
    static final CopyOnWriteSortedSet<XC_LoadPackage> sLoadedPackageCallbacks = new CopyOnWriteSortedSet<>();

    /* renamed from: de.robv.android.xposed.XposedBridge$AdditionalHookInfo */
    private static class AdditionalHookInfo {
        final CopyOnWriteSortedSet<XC_MethodHook> callbacks;
        final Class<?>[] parameterTypes;
        final Class<?> returnType;

        private AdditionalHookInfo(CopyOnWriteSortedSet<XC_MethodHook> copyOnWriteSortedSet, Class<?>[] clsArr, Class<?> cls) {
            this.callbacks = copyOnWriteSortedSet;
            this.parameterTypes = clsArr;
            this.returnType = cls;
        }
    }

    /* renamed from: de.robv.android.xposed.XposedBridge$CopyOnWriteSortedSet */
    public static final class CopyOnWriteSortedSet<E> {
        private volatile transient Object[] elements = XposedBridge.EMPTY_ARRAY;

        public synchronized boolean add(E e) {
            if (indexOf(e) >= 0) {
                return false;
            }
            Object[] objArr = new Object[(this.elements.length + 1)];
            System.arraycopy(this.elements, 0, objArr, 0, this.elements.length);
            objArr[this.elements.length] = e;
            Arrays.sort(objArr);
            this.elements = objArr;
            return true;
        }

        public synchronized boolean remove(E e) {
            int indexOf = indexOf(e);
            if (indexOf == -1) {
                return false;
            }
            Object[] objArr = new Object[(this.elements.length - 1)];
            System.arraycopy(this.elements, 0, objArr, 0, indexOf);
            System.arraycopy(this.elements, indexOf + 1, objArr, indexOf, (this.elements.length - indexOf) - 1);
            this.elements = objArr;
            return true;
        }

        private int indexOf(Object obj) {
            for (int i = 0; i < this.elements.length; i++) {
                if (obj.equals(this.elements[i])) {
                    return i;
                }
            }
            return -1;
        }

        public Object[] getSnapshot() {
            return this.elements;
        }
    }

    /* renamed from: de.robv.android.xposed.XposedBridge$ToolEntryPoint */
    protected static final class ToolEntryPoint {
        protected ToolEntryPoint() {
        }

        protected static void main(String[] strArr) {
            XposedBridge.isZygote = false;
            XposedBridge.main(strArr);
        }
    }

    private static native Object cloneToSubclassNative(Object obj, Class<?> cls);

    static native void closeFilesBeforeForkNative();

    static native void dumpObjectNative(Object obj);

    private static native int getRuntime();

    static native String getStartClassName();

    public static native int getXposedVersion();

    private static native boolean hadInitErrors();

    private static native synchronized void hookMethodNative(Member member, Class<?> cls, int i, Object obj);

    static native boolean initXResourcesNative();

    static native void invalidateCallersNative(Member[] memberArr);

    private static native Object invokeOriginalMethodNative(Member member, int i, Class<?>[] clsArr, Class<?> cls, Object obj, Object[] objArr) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    private static native void removeFinalFlagNative(Class<?> cls);

    static native void reopenFilesAfterForkNative();

    private static native void setObjectClassNative(Object obj, Class<?> cls);

    static native boolean startsSystemServer();

    private XposedBridge() {
    }

    protected static void main(String[] strArr) {
        try {
            if (!hadInitErrors()) {
                initXResources();
                SELinuxHelper.initOnce();
                SELinuxHelper.initForProcess(null);
                runtime = getRuntime();
                XPOSED_BRIDGE_VERSION = getXposedVersion();
                if (isZygote) {
                    XposedInit.hookResources();
                    XposedInit.initForZygote();
                }
                XposedInit.loadModules();
            } else {
                Log.e(TAG, "Not initializing Xposed because of previous errors");
            }
        } catch (Throwable th) {
            Log.e(TAG, "Errors during Xposed initialization", th);
            disableHooks = true;
        }
        if (isZygote) {
            ZygoteInit.main(strArr);
        } else {
            RuntimeInit.main(strArr);
        }
    }

    private static void initXResources() throws IOException {
        Class<TypedArray> cls;
        Resources system = Resources.getSystem();
        File ensureSuperDexFile = ensureSuperDexFile("XResources", system.getClass(), Resources.class);
        try {
            TypedArray obtainTypedArray = system.obtainTypedArray(system.getIdentifier("preloaded_drawables", "array", "android"));
            cls = obtainTypedArray.getClass();
            try {
                obtainTypedArray.recycle();
            } catch (NotFoundException e) {
                e = e;
            }
        } catch (NotFoundException e2) {
            e = e2;
            cls = TypedArray.class;
            log((Throwable) e);
            Runtime.getRuntime().gc();
            File ensureSuperDexFile2 = ensureSuperDexFile("XTypedArray", cls, TypedArray.class);
            ClassLoader classLoader = XposedBridge.class.getClassLoader();
            StringBuilder sb = new StringBuilder();
            sb.append(ensureSuperDexFile.getAbsolutePath());
            sb.append(File.pathSeparator);
            sb.append(ensureSuperDexFile2.getAbsolutePath());
            XposedHelpers.setObjectField(classLoader, "parent", new PathClassLoader(sb.toString(), classLoader.getParent()));
        }
        Runtime.getRuntime().gc();
        File ensureSuperDexFile22 = ensureSuperDexFile("XTypedArray", cls, TypedArray.class);
        ClassLoader classLoader2 = XposedBridge.class.getClassLoader();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(ensureSuperDexFile.getAbsolutePath());
        sb2.append(File.pathSeparator);
        sb2.append(ensureSuperDexFile22.getAbsolutePath());
        XposedHelpers.setObjectField(classLoader2, "parent", new PathClassLoader(sb2.toString(), classLoader2.getParent()));
    }

    @SuppressLint({"SetWorldReadable"})
    private static File ensureSuperDexFile(String str, Class<?> cls, Class<?> cls2) throws IOException {
        removeFinalFlagNative(cls);
        File ensure = DexCreator.ensure(str, cls, cls2);
        ensure.setReadable(true, false);
        return ensure;
    }

    public static synchronized void log(String str) {
        synchronized (XposedBridge.class) {
            if (!TextUtils.isEmpty(str)) {
                Log.i(TAG, str);
            }
        }
    }

    public static synchronized void log(Throwable th) {
        synchronized (XposedBridge.class) {
            Log.e(TAG, Log.getStackTraceString(th));
        }
    }

    public static Unhook hookMethod(Member member, XC_MethodHook xC_MethodHook) {
        return (Unhook) XposedHelpers.callStaticMethod(XposedHelpers.findClass("me.weishu.exposed.ExposedBridge", XposedBridge.class.getClassLoader()), "hookMethod", member, xC_MethodHook);
    }

    @Deprecated
    public static void unhookMethod(Member member, XC_MethodHook xC_MethodHook) {
        synchronized (sHookedMethodCallbacks) {
            CopyOnWriteSortedSet copyOnWriteSortedSet = (CopyOnWriteSortedSet) sHookedMethodCallbacks.get(member);
            if (copyOnWriteSortedSet != null) {
                copyOnWriteSortedSet.remove(xC_MethodHook);
            }
        }
    }

    public static Set<Unhook> hookAllMethods(Class<?> cls, String str, XC_MethodHook xC_MethodHook) {
        Method[] declaredMethods;
        HashSet hashSet = new HashSet();
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().equals(str)) {
                hashSet.add(hookMethod(method, xC_MethodHook));
            }
        }
        return hashSet;
    }

    public static Set<Unhook> hookAllConstructors(Class<?> cls, XC_MethodHook xC_MethodHook) {
        HashSet hashSet = new HashSet();
        for (Constructor hookMethod : cls.getDeclaredConstructors()) {
            hashSet.add(hookMethod(hookMethod, xC_MethodHook));
        }
        return hashSet;
    }

    private static Object handleHookedMethod(Member member, int i, Object obj, Object obj2, Object[] objArr) throws Throwable {
        AdditionalHookInfo additionalHookInfo = (AdditionalHookInfo) obj;
        if (disableHooks) {
            try {
                return invokeOriginalMethodNative(member, i, additionalHookInfo.parameterTypes, additionalHookInfo.returnType, obj2, objArr);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        } else {
            Object[] snapshot = additionalHookInfo.callbacks.getSnapshot();
            int length = snapshot.length;
            if (length == 0) {
                try {
                    return invokeOriginalMethodNative(member, i, additionalHookInfo.parameterTypes, additionalHookInfo.returnType, obj2, objArr);
                } catch (InvocationTargetException e2) {
                    throw e2.getCause();
                }
            } else {
                MethodHookParam methodHookParam = new MethodHookParam();
                methodHookParam.method = member;
                methodHookParam.thisObject = obj2;
                methodHookParam.args = objArr;
                int i2 = 0;
                while (true) {
                    try {
                        ((XC_MethodHook) snapshot[i2]).beforeHookedMethod(methodHookParam);
                        if (methodHookParam.returnEarly) {
                            i2++;
                            break;
                        }
                    } catch (Throwable th) {
                        log(th);
                        methodHookParam.setResult(null);
                        methodHookParam.returnEarly = false;
                    }
                    i2++;
                    if (i2 >= length) {
                        break;
                    }
                }
                if (!methodHookParam.returnEarly) {
                    try {
                        methodHookParam.setResult(invokeOriginalMethodNative(member, i, additionalHookInfo.parameterTypes, additionalHookInfo.returnType, methodHookParam.thisObject, methodHookParam.args));
                    } catch (InvocationTargetException e3) {
                        methodHookParam.setThrowable(e3.getCause());
                    }
                }
                int i3 = i2 - 1;
                do {
                    Object result = methodHookParam.getResult();
                    Throwable throwable = methodHookParam.getThrowable();
                    try {
                        ((XC_MethodHook) snapshot[i3]).afterHookedMethod(methodHookParam);
                    } catch (Throwable th2) {
                        log(th2);
                        if (throwable == null) {
                            methodHookParam.setResult(result);
                        } else {
                            methodHookParam.setThrowable(throwable);
                        }
                    }
                    i3--;
                } while (i3 >= 0);
                if (!methodHookParam.hasThrowable()) {
                    return methodHookParam.getResult();
                }
                throw methodHookParam.getThrowable();
            }
        }
    }

    public static void hookLoadPackage(XC_LoadPackage xC_LoadPackage) {
        synchronized (sLoadedPackageCallbacks) {
            sLoadedPackageCallbacks.add(xC_LoadPackage);
        }
    }

    public static void hookInitPackageResources(XC_InitPackageResources xC_InitPackageResources) {
        synchronized (sInitPackageResourcesCallbacks) {
            sInitPackageResourcesCallbacks.add(xC_InitPackageResources);
        }
    }

    public static Object invokeOriginalMethod(Member member, Object obj, Object[] objArr) throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (objArr == null) {
            objArr = EMPTY_ARRAY;
        }
        return XposedHelpers.callStaticMethod(XposedHelpers.findClass("me.weishu.exposed.ExposedBridge", XposedBridge.class.getClassLoader()), "invokeOriginalMethod", member, obj, objArr);
    }

    static void setObjectClass(Object obj, Class<?> cls) {
        if (!cls.isAssignableFrom(obj.getClass())) {
            setObjectClassNative(obj, cls);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot transfer object from ");
        sb.append(obj.getClass());
        sb.append(" to ");
        sb.append(cls);
        throw new IllegalArgumentException(sb.toString());
    }

    static Object cloneToSubclass(Object obj, Class<?> cls) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass().isAssignableFrom(cls)) {
            return cloneToSubclassNative(obj, cls);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(cls);
        sb.append(" doesn't extend ");
        sb.append(obj.getClass());
        throw new ClassCastException(sb.toString());
    }
}
