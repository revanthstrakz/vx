package p015me.weishu.exposed;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.Process;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.AbsSavedState;
import android.view.View;
import com.getkeepsafe.relinker.ReLinker;
import dalvik.system.DexClassLoader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import p011de.robv.android.xposed.DexposedBridge;
import p011de.robv.android.xposed.ExposedHelper;
import p011de.robv.android.xposed.IXposedHookInitPackageResources;
import p011de.robv.android.xposed.IXposedHookLoadPackage;
import p011de.robv.android.xposed.IXposedHookLoadPackage.Wrapper;
import p011de.robv.android.xposed.IXposedHookZygoteInit;
import p011de.robv.android.xposed.XC_MethodHook;
import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import p011de.robv.android.xposed.XC_MethodHook.Unhook;
import p011de.robv.android.xposed.XSharedPreferences;
import p011de.robv.android.xposed.XposedBridge;
import p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet;
import p011de.robv.android.xposed.XposedHelpers;
import p011de.robv.android.xposed.callbacks.XC_LoadPackage;
import p011de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/* renamed from: me.weishu.exposed.ExposedBridge */
public class ExposedBridge {
    public static final String BASE_DIR = (VERSION.SDK_INT >= 24 ? "/data/user_de/0/de.robv.android.xposed.installer/" : BASE_DIR_LEGACY);
    @SuppressLint({"SdCardPath"})
    private static final String BASE_DIR_LEGACY = "/data/data/de.robv.android.xposed.installer/";
    private static final int FAKE_XPOSED_VERSION = 91;

    /* renamed from: QQ */
    private static final String f210QQ = decodeFromBase64("Y29tLnRlbmNlbnQubW9iaWxlcXE=");
    private static boolean SYSTEM_CLASSLOADER_INJECT = false;
    private static final String TAG = "ExposedBridge";
    private static final String VERSION_KEY = "version";
    private static final String WECHAT = decodeFromBase64("Y29tLnRlbmNlbnQubW0=");
    private static final String XPOSED_INSTALL_PACKAGE = "de.robv.android.xposed.installer";
    private static Context appContext;
    private static String currentPackage;
    private static Map<ClassLoader, ClassLoader> exposedClassLoaderMap = new HashMap();
    private static Pair<String, Set<String>> lastModuleList = Pair.create(null, null);
    private static ModuleLoadListener sModuleLoadListener = new ModuleLoadListener() {
        public void onLoadingModule(String str, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        }

        public void onModuleLoaded(String str, ApplicationInfo applicationInfo, ClassLoader classLoader) {
            ExposedBridge.initForWeChatTranslate(str, applicationInfo, classLoader);
        }
    };
    private static volatile boolean wcdbLoaded = false;
    private static ClassLoader xposedClassLoader;

    /* renamed from: me.weishu.exposed.ExposedBridge$ModuleLoadResult */
    enum ModuleLoadResult {
        DISABLED,
        NOT_EXIST,
        INVALID,
        SUCCESS,
        FAILED,
        IGNORED
    }

    public static void initOnce(Context context, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        XposedBridge.XPOSED_BRIDGE_VERSION = 91;
        appContext = context;
        initForPackage(context, applicationInfo);
        ReLinker.loadLibrary(context, "epic");
        ExposedHelper.initSeLinux(applicationInfo.processName);
        XSharedPreferences.setPackageBaseDirectory(new File(applicationInfo.dataDir).getParentFile());
        initForXposedModule(context, applicationInfo, classLoader);
        initForXposedInstaller(context, applicationInfo, classLoader);
        initForWechat(context, applicationInfo, classLoader);
        initForQQ(context, applicationInfo, classLoader);
    }

    private static void initForPackage(Context context, ApplicationInfo applicationInfo) {
        currentPackage = applicationInfo.packageName;
        if (currentPackage == null) {
            currentPackage = context.getPackageName();
        }
        System.setProperty("vxp", "1");
        System.setProperty("vxp_user_dir", new File(applicationInfo.dataDir).getParent());
    }

    private static boolean patchSystemClassLoader() {
        XposedClassLoader xposedClassLoader2 = new XposedClassLoader(ExposedBridge.class.getClassLoader());
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        boolean z = false;
        try {
            Field declaredField = ClassLoader.class.getDeclaredField("parent");
            declaredField.setAccessible(true);
            declaredField.set(systemClassLoader, xposedClassLoader2);
            StringBuilder sb = new StringBuilder();
            sb.append("XposedBridge's BootClassLoader: ");
            sb.append(XposedBridge.BOOTCLASSLOADER);
            sb.append(", parent: ");
            sb.append(XposedBridge.BOOTCLASSLOADER.getParent());
            XposedBridge.log(sb.toString());
            if (systemClassLoader.getParent() == xposedClassLoader2) {
                z = true;
            }
            return z;
        } catch (NoSuchFieldException e) {
            XposedBridge.log((Throwable) e);
            return false;
        } catch (IllegalAccessException e2) {
            XposedBridge.log((Throwable) e2);
            return false;
        }
    }

    private static synchronized ClassLoader getAppClassLoaderWithXposed(ClassLoader classLoader) {
        synchronized (ExposedBridge.class) {
            if (exposedClassLoaderMap.containsKey(classLoader)) {
                ClassLoader classLoader2 = (ClassLoader) exposedClassLoaderMap.get(classLoader);
                return classLoader2;
            }
            ComposeClassLoader composeClassLoader = new ComposeClassLoader(getXposedClassLoader(ExposedBridge.class.getClassLoader()), classLoader);
            exposedClassLoaderMap.put(classLoader, composeClassLoader);
            return composeClassLoader;
        }
    }

    public static synchronized ClassLoader getXposedClassLoader(ClassLoader classLoader) {
        ClassLoader classLoader2;
        synchronized (ExposedBridge.class) {
            if (xposedClassLoader == null) {
                xposedClassLoader = new XposedClassLoader(classLoader);
            }
            classLoader2 = xposedClassLoader;
        }
        return classLoader2;
    }

    public static ModuleLoadResult loadModule(String str, String str2, String str3, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        DexClassLoader dexClassLoader;
        InputStream resourceAsStream;
        if (filterApplication(applicationInfo)) {
            return ModuleLoadResult.IGNORED;
        }
        loadModuleConfig(new File(applicationInfo.dataDir).getParent(), applicationInfo.processName);
        if (lastModuleList.second == null || !((Set) lastModuleList.second).contains(str)) {
            String str4 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("module:");
            sb.append(str);
            sb.append(" is disabled, ignore");
            Log.i(str4, sb.toString());
            return ModuleLoadResult.DISABLED;
        }
        String str5 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Loading modules from ");
        sb2.append(str);
        sb2.append(" for process: ");
        sb2.append(applicationInfo.processName);
        sb2.append(" i s c: ");
        sb2.append(SYSTEM_CLASSLOADER_INJECT);
        Log.i(str5, sb2.toString());
        if (!new File(str).exists()) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append(" does not exist");
            XposedBridge.log(sb3.toString());
            return ModuleLoadResult.NOT_EXIST;
        }
        if (SYSTEM_CLASSLOADER_INJECT) {
            dexClassLoader = new DexClassLoader(str, str2, str3, XposedBridge.BOOTCLASSLOADER);
        } else {
            ClassLoader classLoader2 = ExposedBridge.class.getClassLoader();
            classLoader = getAppClassLoaderWithXposed(classLoader);
            dexClassLoader = new DexClassLoader(str, str2, str3, getXposedClassLoader(classLoader2));
        }
        resourceAsStream = dexClassLoader.getResourceAsStream("assets/xposed_init");
        if (resourceAsStream == null) {
            XposedBridge.log("assets/xposed_init not found in the APK");
            return ModuleLoadResult.INVALID;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        while (true) {
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String trim = readLine.trim();
                if (!trim.isEmpty()) {
                    if (!trim.startsWith("#")) {
                        if (filterModuleForApp(applicationInfo, trim)) {
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("ignore module: ");
                            sb4.append(trim);
                            sb4.append(" for application: ");
                            sb4.append(applicationInfo.packageName);
                            XposedBridge.log(sb4.toString());
                        } else {
                            String str6 = TAG;
                            try {
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append("  Loading class ");
                                sb5.append(trim);
                                Log.i(str6, sb5.toString());
                                Class loadClass = dexClassLoader.loadClass(trim);
                                sModuleLoadListener.onLoadingModule(trim, applicationInfo, dexClassLoader);
                                if (!ExposedHelper.isIXposedMod(loadClass)) {
                                    XposedBridge.log("    This class doesn't implement any sub-interface of IXposedMod, skipping it");
                                } else if (IXposedHookInitPackageResources.class.isAssignableFrom(loadClass)) {
                                    XposedBridge.log("    This class requires resource-related hooks (which are disabled), skipping it.");
                                } else {
                                    Object newInstance = loadClass.newInstance();
                                    if (newInstance instanceof IXposedHookZygoteInit) {
                                        ExposedHelper.callInitZygote(str, newInstance);
                                    }
                                    if (newInstance instanceof IXposedHookLoadPackage) {
                                        Wrapper wrapper = new Wrapper((IXposedHookLoadPackage) newInstance);
                                        CopyOnWriteSortedSet copyOnWriteSortedSet = new CopyOnWriteSortedSet();
                                        copyOnWriteSortedSet.add(wrapper);
                                        LoadPackageParam loadPackageParam = new LoadPackageParam(copyOnWriteSortedSet);
                                        loadPackageParam.packageName = applicationInfo.packageName;
                                        loadPackageParam.processName = applicationInfo.processName;
                                        loadPackageParam.classLoader = classLoader;
                                        loadPackageParam.appInfo = applicationInfo;
                                        loadPackageParam.isFirstApplication = true;
                                        XC_LoadPackage.callAll(loadPackageParam);
                                    }
                                    boolean z = newInstance instanceof IXposedHookInitPackageResources;
                                    sModuleLoadListener.onModuleLoaded(trim, applicationInfo, dexClassLoader);
                                }
                            } catch (Throwable th) {
                                XposedBridge.log(th);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                XposedBridge.log((Throwable) e);
            } catch (Throwable th2) {
                closeSliently(resourceAsStream);
                throw th2;
            }
        }
        closeSliently(resourceAsStream);
        return ModuleLoadResult.FAILED;
        ModuleLoadResult moduleLoadResult = ModuleLoadResult.SUCCESS;
        closeSliently(resourceAsStream);
        return moduleLoadResult;
    }

    private static boolean ignoreHooks(Member member) {
        if (member == null) {
            return false;
        }
        String name = member.getDeclaringClass().getName();
        if (!f210QQ.equals(currentPackage) || !name.contains("QQAppInterface")) {
            return false;
        }
        return true;
    }

    private static void presetMethod(Member member) {
        Class cls;
        if (member != null && WECHAT.equals(currentPackage)) {
            Class declaringClass = member.getDeclaringClass();
            if (declaringClass.getName().contains("wcdb") && !wcdbLoaded) {
                try {
                    cls = declaringClass.getClassLoader().loadClass("com.tencent.wcdb.database.SQLiteDatabase");
                } catch (ClassNotFoundException unused) {
                    XposedBridge.log("preload sqlite class failed!!!");
                    cls = null;
                }
                if (cls != null) {
                    wcdbLoaded = true;
                }
            }
        }
    }

    public static Unhook hookMethod(Member member, XC_MethodHook xC_MethodHook) {
        if (ignoreHooks(member)) {
            return null;
        }
        presetMethod(member);
        Unhook replaceForCHA = CHAHelper.replaceForCHA(member, xC_MethodHook);
        if (replaceForCHA != null) {
            return ExposedHelper.newUnHook(xC_MethodHook, replaceForCHA.getHookedMethod());
        }
        return ExposedHelper.newUnHook(xC_MethodHook, DexposedBridge.hookMethod(member, xC_MethodHook).getHookedMethod());
    }

    public static Object invokeOriginalMethod(Member member, Object obj, Object[] objArr) throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return DexposedBridge.invokeOriginalMethod(member, obj, objArr);
    }

    private static void initForXposedModule(Context context, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        Closeable closeable = null;
        try {
            InputStream open = context.getAssets().open("xposed_init");
            try {
                System.setProperty("epic.force", "true");
                closeSliently(open);
            } catch (IOException unused) {
                closeable = open;
                String str = TAG;
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(applicationInfo.packageName);
                    sb.append(" is not a Xposed module, do not init epic.force");
                    Log.i(str, sb.toString());
                    closeSliently(closeable);
                } catch (Throwable th) {
                    th = th;
                    closeSliently(closeable);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                closeable = open;
                closeSliently(closeable);
                throw th;
            }
        } catch (IOException unused2) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(applicationInfo.packageName);
            sb2.append(" is not a Xposed module, do not init epic.force");
            Log.i(str2, sb2.toString());
            closeSliently(closeable);
        }
    }

    private static boolean isXposedInstaller(ApplicationInfo applicationInfo) {
        return "de.robv.android.xposed.installer".equals(applicationInfo.packageName);
    }

    private static boolean filterApplication(ApplicationInfo applicationInfo) {
        if (applicationInfo == null || isXposedInstaller(applicationInfo)) {
            return true;
        }
        if (!decodeFromBase64("Y29tLnRlbmNlbnQubW06cHVzaA==").equalsIgnoreCase(applicationInfo.processName)) {
            return false;
        }
        XposedBridge.log("ignore process for wechat push.");
        return true;
    }

    private static boolean filterModuleForApp(ApplicationInfo applicationInfo, String str) {
        if (!(applicationInfo == null || applicationInfo.packageName == null || !WECHAT.equals(applicationInfo.packageName))) {
            if (applicationInfo.processName.contains("appbrand")) {
                if ("com.emily.mmjumphelper.xposed.XposedMain".equals(str)) {
                    return false;
                }
                return true;
            } else if ("com.emily.mmjumphelper.xposed.XposedMain".equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static void initForXposedInstaller(Context context, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        if (isXposedInstaller(applicationInfo)) {
            String valueOf = String.valueOf(91);
            File fileStreamPath = context.getFileStreamPath("xposed_prop");
            if (!fileStreamPath.exists()) {
                writeXposedProperty(fileStreamPath, valueOf, false);
            } else {
                XposedBridge.log("xposed config file exists, check version");
                if (!valueOf.equals(getXposedVersionFromProperty(fileStreamPath))) {
                    writeXposedProperty(fileStreamPath, valueOf, true);
                } else {
                    Log.i(TAG, "xposed version keep same, continue.");
                }
            }
            Class findClass = XposedHelpers.findClass("de.robv.android.xposed.installer.XposedApp", classLoader);
            try {
                Object staticObjectField = XposedHelpers.getStaticObjectField(findClass, "XPOSED_PROP_FILES");
                int length = Array.getLength(staticObjectField);
                String path = fileStreamPath.getPath();
                for (int i = 0; i < length; i++) {
                    Array.set(staticObjectField, i, path);
                }
                XposedHelpers.findAndHookMethod(findClass, "getActiveXposedVersion", new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        super.beforeHookedMethod(methodHookParam);
                        methodHookParam.setResult(Integer.valueOf(91));
                    }
                });
                XposedHelpers.findAndHookMethod(findClass, "getInstalledXposedVersion", new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        super.beforeHookedMethod(methodHookParam);
                        methodHookParam.setResult(Integer.valueOf(91));
                    }
                });
                if (VERSION.SDK_INT >= 26) {
                    XposedHelpers.findAndHookMethod(XposedHelpers.findClass("se.emilsjolander.stickylistheaders.StickyListHeadersListView", classLoader), "onSaveInstanceState", new XC_MethodHook() {
                        /* access modifiers changed from: protected */
                        public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            super.beforeHookedMethod(methodHookParam);
                            methodHookParam.setResult(AbsSavedState.EMPTY_STATE);
                            Field findField = XposedHelpers.findField(View.class, "mPrivateFlags");
                            findField.set(methodHookParam.thisObject, Integer.valueOf(findField.getInt(methodHookParam.thisObject) | 131072));
                        }
                    });
                }
            } catch (Throwable unused) {
            }
            Constructor findConstructorExact = XposedHelpers.findConstructorExact(File.class, (Class<?>[]) new Class[]{String.class});
            Constructor findConstructorExact2 = XposedHelpers.findConstructorExact(File.class, (Class<?>[]) new Class[]{String.class, String.class});
            final String str = applicationInfo.dataDir;
            XposedBridge.hookMethod(findConstructorExact, new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    String str;
                    super.beforeHookedMethod(methodHookParam);
                    String str2 = (String) methodHookParam.args[0];
                    if (str2.startsWith(ExposedBridge.BASE_DIR)) {
                        Object[] objArr = methodHookParam.args;
                        String str3 = ExposedBridge.BASE_DIR;
                        if (str2.equals(ExposedBridge.BASE_DIR)) {
                            str = str;
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append(str);
                            sb.append("/exposed_");
                            str = sb.toString();
                        }
                        objArr[0] = str2.replace(str3, str);
                    }
                }
            });
            XposedBridge.hookMethod(findConstructorExact2, new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    String str;
                    super.beforeHookedMethod(methodHookParam);
                    String str2 = (String) methodHookParam.args[0];
                    if (str2.startsWith(ExposedBridge.BASE_DIR)) {
                        Object[] objArr = methodHookParam.args;
                        String str3 = ExposedBridge.BASE_DIR;
                        if (str2.equals(ExposedBridge.BASE_DIR)) {
                            str = str;
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append(str);
                            sb.append("/exposed_");
                            str = sb.toString();
                        }
                        objArr[0] = str2.replace(str3, str);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public static void initForWeChatTranslate(String str, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        if ("com.hkdrjxy.wechart.xposed.XposedInit".equals(str)) {
            if ("com.hiwechart.translate".equals(applicationInfo.processName) || "com.tencent.mm".equals(applicationInfo.processName)) {
                final IBinder[] iBinderArr = new IBinder[1];
                Intent intent = new Intent();
                intent.setAction("com.hiwechart.translate.aidl.TranslateService");
                intent.setComponent(new ComponentName("com.hiwechart.translate", "com.hiwechart.translate.aidl.TranslateService"));
                appContext.bindService(intent, new ServiceConnection() {
                    public void onServiceDisconnected(ComponentName componentName) {
                    }

                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        iBinderArr[0] = iBinder;
                    }
                }, 1);
                Class findClass = XposedHelpers.findClass("android.os.ServiceManager", classLoader);
                final String str2 = VERSION.SDK_INT >= 21 ? "user.wechart.trans" : "wechart.trans";
                XposedHelpers.findAndHookMethod(findClass, "getService", String.class, new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        super.beforeHookedMethod(methodHookParam);
                        if (str2.equals(methodHookParam.args[0])) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("get service :");
                            sb.append(iBinderArr[0]);
                            Log.i("mylog", sb.toString());
                            methodHookParam.setResult(iBinderArr[0]);
                        }
                    }
                });
            }
        }
    }

    @SuppressLint({"ApplySharedPref"})
    private static void initForQQ(Context context, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        if (applicationInfo != null) {
            String decodeFromBase64 = decodeFromBase64("Y29tLnRlbmNlbnQubW9iaWxlcXE=");
            if (decodeFromBase64.equals(applicationInfo.packageName) && decodeFromBase64.equals(applicationInfo.processName)) {
                context.getSharedPreferences(decodeFromBase64("aG90cGF0Y2hfcHJlZmVyZW5jZQ=="), 0).edit().remove(decodeFromBase64("a2V5X2NvbmZpZ19wYXRjaF9kZXg=")).commit();
            }
        }
    }

    private static void initForWechat(Context context, ApplicationInfo applicationInfo, ClassLoader classLoader) {
        if (applicationInfo != null && WECHAT.equals(applicationInfo.packageName) && WECHAT.equals(applicationInfo.processName)) {
            String str = applicationInfo.dataDir;
            File file = new File(str, decodeFromBase64("dGlua2Vy"));
            File file2 = new File(str, decodeFromBase64("dGlua2VyX3RlbXA="));
            File file3 = new File(str, decodeFromBase64("dGlua2VyX3NlcnZlcg=="));
            deleteDir(file);
            deleteDir(file2);
            deleteDir(file3);
            final int myPid = Process.myPid();
            XposedHelpers.findAndHookMethod(Process.class, "killProcess", Integer.TYPE, new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    super.beforeHookedMethod(methodHookParam);
                    int i = 0;
                    if (((Integer) methodHookParam.args[0]).intValue() == myPid) {
                        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                        if (stackTrace != null) {
                            int length = stackTrace.length;
                            while (true) {
                                if (i >= length) {
                                    break;
                                } else if (stackTrace[i].getClassName().contains("com.tencent.mm.app")) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("do not suicide...");
                                    sb.append(Arrays.toString(stackTrace));
                                    XposedBridge.log(sb.toString());
                                    methodHookParam.setResult(null);
                                    break;
                                } else {
                                    i++;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public static boolean deleteDir(File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            for (String file2 : file.list()) {
                if (!deleteDir(new File(file, file2))) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    private static String decodeFromBase64(String str) {
        return new String(Base64.decode(str, 0));
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0038 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void writeXposedProperty(java.io.File r2, java.lang.String r3, boolean r4) {
        /*
            java.util.Properties r4 = new java.util.Properties
            r4.<init>()
            java.lang.String r0 = "version"
            r4.put(r0, r3)
            java.lang.String r0 = "arch"
            java.lang.String r1 = android.os.Build.CPU_ABI
            r4.put(r0, r1)
            java.lang.String r0 = "minsdk"
            java.lang.String r1 = "52"
            r4.put(r0, r1)
            java.lang.String r0 = "maxsdk"
            r1 = 2147483647(0x7fffffff, float:NaN)
            java.lang.String r1 = java.lang.String.valueOf(r1)
            r4.put(r0, r1)
            r0 = 0
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0038 }
            r1.<init>(r2)     // Catch:{ IOException -> 0x0038 }
            r4.store(r1, r0)     // Catch:{ IOException -> 0x0034, all -> 0x0031 }
            closeSliently(r1)
            goto L_0x0042
        L_0x0031:
            r2 = move-exception
            r0 = r1
            goto L_0x0043
        L_0x0034:
            r0 = r1
            goto L_0x0038
        L_0x0036:
            r2 = move-exception
            goto L_0x0043
        L_0x0038:
            r2.delete()     // Catch:{ all -> 0x0036 }
            r4 = 0
            writeXposedProperty(r2, r3, r4)     // Catch:{ all -> 0x0036 }
            closeSliently(r0)
        L_0x0042:
            return
        L_0x0043:
            closeSliently(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: p015me.weishu.exposed.ExposedBridge.writeXposedProperty(java.io.File, java.lang.String, boolean):void");
    }

    private static String getXposedVersionFromProperty(File file) {
        Closeable closeable;
        try {
            closeable = new FileInputStream(file);
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(file));
                String property = properties.getProperty(VERSION_KEY);
                closeSliently(closeable);
                return property;
            } catch (IOException unused) {
                try {
                    XposedBridge.log("getXposedVersion from property failed");
                    closeSliently(closeable);
                    return null;
                } catch (Throwable th) {
                    th = th;
                    closeSliently(closeable);
                    throw th;
                }
            }
        } catch (IOException unused2) {
            closeable = null;
            XposedBridge.log("getXposedVersion from property failed");
            closeSliently(closeable);
            return null;
        } catch (Throwable th2) {
            th = th2;
            closeable = null;
            closeSliently(closeable);
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0087 A[SYNTHETIC, Splitter:B:43:0x0087] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0092 A[SYNTHETIC, Splitter:B:49:0x0092] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean loadModuleConfig(java.lang.String r5, java.lang.String r6) {
        /*
            android.util.Pair<java.lang.String, java.util.Set<java.lang.String>> r0 = lastModuleList
            r1 = 1
            if (r0 == 0) goto L_0x0018
            android.util.Pair<java.lang.String, java.util.Set<java.lang.String>> r0 = lastModuleList
            java.lang.Object r0 = r0.first
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            boolean r0 = android.text.TextUtils.equals(r0, r6)
            if (r0 == 0) goto L_0x0018
            android.util.Pair<java.lang.String, java.util.Set<java.lang.String>> r0 = lastModuleList
            java.lang.Object r0 = r0.second
            if (r0 == 0) goto L_0x0018
            return r1
        L_0x0018:
            java.io.File r0 = new java.io.File
            java.lang.String r2 = "de.robv.android.xposed.installer"
            r0.<init>(r5, r2)
            boolean r5 = r0.exists()
            r2 = 0
            if (r5 != 0) goto L_0x0027
            return r2
        L_0x0027:
            java.io.File r5 = new java.io.File
            java.lang.String r3 = "exposed_conf/modules.list"
            r5.<init>(r0, r3)
            boolean r0 = r5.exists()
            if (r0 != 0) goto L_0x003c
            java.lang.String r5 = "ExposedBridge"
            java.lang.String r6 = "xposed installer's modules not exist, ignore."
            android.util.Log.d(r5, r6)
            return r2
        L_0x003c:
            r0 = 0
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0081 }
            java.io.FileReader r4 = new java.io.FileReader     // Catch:{ IOException -> 0x0081 }
            r4.<init>(r5)     // Catch:{ IOException -> 0x0081 }
            r3.<init>(r4)     // Catch:{ IOException -> 0x0081 }
            java.util.HashSet r5 = new java.util.HashSet     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            r5.<init>()     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
        L_0x004c:
            java.lang.String r0 = r3.readLine()     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            if (r0 == 0) goto L_0x006a
            java.lang.String r0 = r0.trim()     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            java.lang.String r4 = "#"
            boolean r4 = r0.startsWith(r4)     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            if (r4 == 0) goto L_0x005f
            goto L_0x004c
        L_0x005f:
            boolean r4 = r0.isEmpty()     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            if (r4 == 0) goto L_0x0066
            goto L_0x004c
        L_0x0066:
            r5.add(r0)     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            goto L_0x004c
        L_0x006a:
            android.util.Pair r5 = android.util.Pair.create(r6, r5)     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            lastModuleList = r5     // Catch:{ IOException -> 0x007b, all -> 0x0079 }
            r3.close()     // Catch:{ IOException -> 0x0074 }
            goto L_0x0078
        L_0x0074:
            r5 = move-exception
            r5.printStackTrace()
        L_0x0078:
            return r1
        L_0x0079:
            r5 = move-exception
            goto L_0x0090
        L_0x007b:
            r5 = move-exception
            r0 = r3
            goto L_0x0082
        L_0x007e:
            r5 = move-exception
            r3 = r0
            goto L_0x0090
        L_0x0081:
            r5 = move-exception
        L_0x0082:
            r5.printStackTrace()     // Catch:{ all -> 0x007e }
            if (r0 == 0) goto L_0x008f
            r0.close()     // Catch:{ IOException -> 0x008b }
            goto L_0x008f
        L_0x008b:
            r5 = move-exception
            r5.printStackTrace()
        L_0x008f:
            return r2
        L_0x0090:
            if (r3 == 0) goto L_0x009a
            r3.close()     // Catch:{ IOException -> 0x0096 }
            goto L_0x009a
        L_0x0096:
            r6 = move-exception
            r6.printStackTrace()
        L_0x009a:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: p015me.weishu.exposed.ExposedBridge.loadModuleConfig(java.lang.String, java.lang.String):boolean");
    }

    private static void closeSliently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable unused) {
            }
        }
    }
}
