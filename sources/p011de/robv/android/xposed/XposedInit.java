package p011de.robv.android.xposed;

import android.annotation.SuppressLint;
import android.app.ActivityThread;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.app.LoadedApk;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XResources;
import android.content.res.XResources.XTypedArray;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.os.ZygoteInit;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import p011de.robv.android.xposed.IXposedHookLoadPackage.Wrapper;
import p011de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import p011de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import p011de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import p011de.robv.android.xposed.callbacks.XC_LoadPackage;
import p011de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import p011de.robv.android.xposed.callbacks.XCallback;
import p011de.robv.android.xposed.services.BaseService;

/* renamed from: de.robv.android.xposed.XposedInit */
final class XposedInit {
    @SuppressLint({"SdCardPath"})
    private static final String BASE_DIR = (VERSION.SDK_INT >= 24 ? "/data/user_de/0/de.robv.android.xposed.installer/" : "/data/data/de.robv.android.xposed.installer/");
    private static final String INSTALLER_PACKAGE_NAME = "de.robv.android.xposed.installer";
    private static final String INSTANT_RUN_CLASS = "com.android.tools.fd.runtime.BootstrapApplication";
    private static final String TAG = "Xposed";
    private static final String[] XRESOURCES_CONFLICTING_PACKAGES = {"com.sygic.aura"};
    private static boolean disableResources = false;
    private static final String startClassName = XposedBridge.getStartClassName();
    private static final boolean startsSystemServer = XposedBridge.startsSystemServer();

    private XposedInit() {
    }

    static void initForZygote() throws Throwable {
        if (needsToCloseFilesForFork()) {
            C12361 r0 = new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    XposedBridge.closeFilesBeforeForkNative();
                }

                /* access modifiers changed from: protected */
                public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    XposedBridge.reopenFilesAfterForkNative();
                }
            };
            Class findClass = XposedHelpers.findClass("com.android.internal.os.Zygote", null);
            XposedBridge.hookAllMethods(findClass, "nativeForkAndSpecialize", r0);
            XposedBridge.hookAllMethods(findClass, "nativeForkSystemServer", r0);
        }
        final HashSet hashSet = new HashSet(1);
        XposedHelpers.findAndHookMethod(ActivityThread.class, "handleBindApplication", "android.app.ActivityThread.AppBindData", new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                ActivityThread activityThread = (ActivityThread) methodHookParam.thisObject;
                ApplicationInfo applicationInfo = (ApplicationInfo) XposedHelpers.getObjectField(methodHookParam.args[0], "appInfo");
                String str = applicationInfo.packageName.equals("android") ? "system" : applicationInfo.packageName;
                SELinuxHelper.initForProcess(str);
                if (((ComponentName) XposedHelpers.getObjectField(methodHookParam.args[0], "instrumentationName")) != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Instrumentation detected, disabling framework for ");
                    sb.append(str);
                    Log.w("Xposed", sb.toString());
                    XposedBridge.disableHooks = true;
                    return;
                }
                CompatibilityInfo compatibilityInfo = (CompatibilityInfo) XposedHelpers.getObjectField(methodHookParam.args[0], "compatInfo");
                if (applicationInfo.sourceDir != null) {
                    XposedHelpers.setObjectField(activityThread, "mBoundApplication", methodHookParam.args[0]);
                    hashSet.add(str);
                    LoadedApk packageInfoNoCheck = activityThread.getPackageInfoNoCheck(applicationInfo, compatibilityInfo);
                    XResources.setPackageNameForResDir(applicationInfo.packageName, packageInfoNoCheck.getResDir());
                    LoadPackageParam loadPackageParam = new LoadPackageParam(XposedBridge.sLoadedPackageCallbacks);
                    loadPackageParam.packageName = str;
                    loadPackageParam.processName = (String) XposedHelpers.getObjectField(methodHookParam.args[0], "processName");
                    loadPackageParam.classLoader = packageInfoNoCheck.getClassLoader();
                    loadPackageParam.appInfo = applicationInfo;
                    loadPackageParam.isFirstApplication = true;
                    XC_LoadPackage.callAll(loadPackageParam);
                    if (str.equals("de.robv.android.xposed.installer")) {
                        XposedInit.hookXposedInstaller(loadPackageParam.classLoader);
                    }
                }
            }
        });
        if (VERSION.SDK_INT < 21) {
            XposedHelpers.findAndHookMethod("com.android.server.ServerThread", null, VERSION.SDK_INT < 19 ? "run" : "initAndLoop", new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    SELinuxHelper.initForProcess("android");
                    hashSet.add("android");
                    LoadPackageParam loadPackageParam = new LoadPackageParam(XposedBridge.sLoadedPackageCallbacks);
                    loadPackageParam.packageName = "android";
                    loadPackageParam.processName = "android";
                    loadPackageParam.classLoader = XposedBridge.BOOTCLASSLOADER;
                    loadPackageParam.appInfo = null;
                    loadPackageParam.isFirstApplication = true;
                    XC_LoadPackage.callAll(loadPackageParam);
                }
            });
        } else if (startsSystemServer) {
            XposedHelpers.findAndHookMethod(ActivityThread.class, "systemMain", new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    XposedHelpers.findAndHookMethod("com.android.server.SystemServer", contextClassLoader, "startBootstrapServices", new XC_MethodHook() {
                        /* access modifiers changed from: protected */
                        /* JADX WARNING: Failed to process nested try/catch */
                        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0040 */
                        /* JADX WARNING: Removed duplicated region for block: B:6:0x0050 A[Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }] */
                        /* JADX WARNING: Removed duplicated region for block: B:7:0x0053 A[Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }] */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void beforeHookedMethod(p011de.robv.android.xposed.XC_MethodHook.MethodHookParam r7) throws java.lang.Throwable {
                            /*
                                r6 = this;
                                java.lang.String r7 = "android"
                                p011de.robv.android.xposed.SELinuxHelper.initForProcess(r7)
                                de.robv.android.xposed.XposedInit$4 r7 = p011de.robv.android.xposed.XposedInit.C12424.this
                                java.util.HashSet r7 = r0
                                java.lang.String r0 = "android"
                                r7.add(r0)
                                de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam r7 = new de.robv.android.xposed.callbacks.XC_LoadPackage$LoadPackageParam
                                de.robv.android.xposed.XposedBridge$CopyOnWriteSortedSet<de.robv.android.xposed.callbacks.XC_LoadPackage> r0 = p011de.robv.android.xposed.XposedBridge.sLoadedPackageCallbacks
                                r7.<init>(r0)
                                java.lang.String r0 = "android"
                                r7.packageName = r0
                                java.lang.String r0 = "android"
                                r7.processName = r0
                                java.lang.ClassLoader r0 = r6
                                r7.classLoader = r0
                                r0 = 0
                                r7.appInfo = r0
                                r0 = 1
                                r7.isFirstApplication = r0
                                p011de.robv.android.xposed.callbacks.XC_LoadPackage.callAll(r7)
                                r7 = 0
                                java.lang.String r1 = "com.android.server.pm.HwPackageManagerService"
                                java.lang.ClassLoader r2 = r6     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0040 }
                                java.lang.String r3 = "isOdexMode"
                                java.lang.Object[] r4 = new java.lang.Object[r0]     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0040 }
                                java.lang.Boolean r5 = java.lang.Boolean.valueOf(r7)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0040 }
                                de.robv.android.xposed.XC_MethodReplacement r5 = p011de.robv.android.xposed.XC_MethodReplacement.returnConstant(r5)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0040 }
                                r4[r7] = r5     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0040 }
                                p011de.robv.android.xposed.XposedHelpers.findAndHookMethod(r1, r2, r3, r4)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0040 }
                            L_0x0040:
                                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                r1.<init>()     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                java.lang.String r2 = "com.android.server.pm."
                                r1.append(r2)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                r3 = 23
                                if (r2 < r3) goto L_0x0053
                                java.lang.String r2 = "PackageDexOptimizer"
                                goto L_0x0055
                            L_0x0053:
                                java.lang.String r2 = "PackageManagerService"
                            L_0x0055:
                                r1.append(r2)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                java.lang.String r1 = r1.toString()     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                java.lang.ClassLoader r2 = r6     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                java.lang.String r3 = "dexEntryExists"
                                r4 = 2
                                java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                java.lang.Class<java.lang.String> r5 = java.lang.String.class
                                r4[r7] = r5     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                java.lang.Boolean r7 = java.lang.Boolean.valueOf(r0)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                de.robv.android.xposed.XC_MethodReplacement r7 = p011de.robv.android.xposed.XC_MethodReplacement.returnConstant(r7)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                r4[r0] = r7     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                                p011de.robv.android.xposed.XposedHelpers.findAndHookMethod(r1, r2, r3, r4)     // Catch:{ ClassNotFoundError | NoSuchMethodError -> 0x0074 }
                            L_0x0074:
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: p011de.robv.android.xposed.XposedInit.C12424.C12431.beforeHookedMethod(de.robv.android.xposed.XC_MethodHook$MethodHookParam):void");
                        }
                    });
                }
            });
        }
        XposedBridge.hookAllConstructors(LoadedApk.class, new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                LoadedApk loadedApk = (LoadedApk) methodHookParam.thisObject;
                String packageName = loadedApk.getPackageName();
                XResources.setPackageNameForResDir(packageName, loadedApk.getResDir());
                if (!packageName.equals("android") && hashSet.add(packageName) && XposedHelpers.getBooleanField(loadedApk, "mIncludeCode")) {
                    LoadPackageParam loadPackageParam = new LoadPackageParam(XposedBridge.sLoadedPackageCallbacks);
                    loadPackageParam.packageName = packageName;
                    loadPackageParam.processName = AndroidAppHelper.currentProcessName();
                    loadPackageParam.classLoader = loadedApk.getClassLoader();
                    loadPackageParam.appInfo = loadedApk.getApplicationInfo();
                    loadPackageParam.isFirstApplication = false;
                    XC_LoadPackage.callAll(loadPackageParam);
                }
            }
        });
        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", null, "getResourcesForApplication", ApplicationInfo.class, new XC_MethodHook() {
            /* access modifiers changed from: protected */
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                ApplicationInfo applicationInfo = (ApplicationInfo) methodHookParam.args[0];
                XResources.setPackageNameForResDir(applicationInfo.packageName, applicationInfo.uid == Process.myUid() ? applicationInfo.sourceDir : applicationInfo.publicSourceDir);
            }
        });
        if (XposedHelpers.findFieldIfExists(ZygoteInit.class, "BOOT_START_TIME") != null) {
            XposedHelpers.setStaticLongField(ZygoteInit.class, "BOOT_START_TIME", XposedBridge.BOOT_START_TIME);
        }
        if (VERSION.SDK_INT >= 24) {
            try {
                XposedHelpers.setStaticBooleanField(XposedHelpers.findClass("com.android.internal.os.Zygote", null), "isEnhancedZygoteASLREnabled", false);
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    static void hookResources() throws Throwable {
        final Class cls;
        Class<ActivityThread> cls2;
        BaseService appDataFileService = SELinuxHelper.getAppDataFileService();
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_DIR);
        sb.append("conf/disable_resources");
        if (appDataFileService.checkFileExists(sb.toString())) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Found ");
            sb2.append(BASE_DIR);
            sb2.append("conf/disable_resources, not hooking resources");
            Log.w("Xposed", sb2.toString());
            disableResources = true;
        } else if (!XposedBridge.initXResourcesNative()) {
            Log.e("Xposed", "Cannot hook resources");
            disableResources = true;
        } else {
            final ThreadLocal threadLocal = new ThreadLocal();
            if (VERSION.SDK_INT <= 18) {
                cls2 = ActivityThread.class;
                cls = Class.forName("android.app.ActivityThread$ResourcesKey");
            } else {
                cls2 = Class.forName("android.app.ResourcesManager");
                cls = Class.forName("android.content.res.ResourcesKey");
            }
            if (VERSION.SDK_INT >= 24) {
                XposedBridge.hookAllMethods(cls2, "getOrCreateResources", new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        ArrayList arrayList;
                        int parameterIndexByType = XposedHelpers.getParameterIndexByType(methodHookParam.method, IBinder.class);
                        XResources access$100 = XposedInit.cloneToXResources(methodHookParam, (String) XposedHelpers.getObjectField(methodHookParam.args[XposedHelpers.getParameterIndexByType(methodHookParam.method, cls)], "mResDir"));
                        if (access$100 != null) {
                            Object obj = methodHookParam.args[parameterIndexByType];
                            synchronized (methodHookParam.thisObject) {
                                if (obj != null) {
                                    try {
                                        arrayList = (ArrayList) XposedHelpers.getObjectField(XposedHelpers.callMethod(methodHookParam.thisObject, "getOrCreateActivityResourcesStructLocked", obj), "activityResources");
                                    } catch (Throwable th) {
                                        throw th;
                                    }
                                } else {
                                    arrayList = (ArrayList) XposedHelpers.getObjectField(methodHookParam.thisObject, "mResourceReferences");
                                }
                                arrayList.add(new WeakReference(access$100));
                            }
                        }
                    }
                });
            } else {
                XposedBridge.hookAllConstructors(cls, new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        threadLocal.set(methodHookParam.thisObject);
                    }
                });
                XposedBridge.hookAllMethods(cls2, "getTopLevelResources", new XC_MethodHook() {
                    /* access modifiers changed from: protected */
                    public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        threadLocal.set(null);
                    }

                    /* access modifiers changed from: protected */
                    public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        Object obj = threadLocal.get();
                        if (obj != null) {
                            threadLocal.set(null);
                            XResources access$100 = XposedInit.cloneToXResources(methodHookParam, (String) XposedHelpers.getObjectField(obj, "mResDir"));
                            if (access$100 != null) {
                                Map map = (Map) XposedHelpers.getObjectField(methodHookParam.thisObject, "mActiveResources");
                                synchronized ((VERSION.SDK_INT <= 18 ? XposedHelpers.getObjectField(methodHookParam.thisObject, "mPackages") : methodHookParam.thisObject)) {
                                    WeakReference weakReference = (WeakReference) map.put(obj, new WeakReference(access$100));
                                    if (!(weakReference == null || weakReference.get() == null || ((Resources) weakReference.get()).getAssets() == access$100.getAssets())) {
                                        ((Resources) weakReference.get()).getAssets().close();
                                    }
                                }
                            }
                        }
                    }
                });
                if (VERSION.SDK_INT >= 19) {
                    XposedBridge.hookAllMethods(cls2, "getTopLevelThemedResources", new XC_MethodHook() {
                        /* access modifiers changed from: protected */
                        public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                            XposedInit.cloneToXResources(methodHookParam, (String) methodHookParam.args[0]);
                        }
                    });
                }
            }
            if (VERSION.SDK_INT >= 24) {
                Set overriddenMethods = XposedHelpers.getOverriddenMethods(XTypedArray.class);
                XposedBridge.invalidateCallersNative((Member[]) overriddenMethods.toArray(new Member[overriddenMethods.size()]));
            }
            XposedBridge.hookAllConstructors(TypedArray.class, new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    TypedArray typedArray = (TypedArray) methodHookParam.thisObject;
                    if (typedArray.getResources() instanceof XResources) {
                        XposedBridge.setObjectClass(typedArray, XTypedArray.class);
                    }
                }
            });
            XResources xResources = (XResources) XposedBridge.cloneToSubclass(Resources.getSystem(), XResources.class);
            xResources.initObject(null);
            XposedHelpers.setStaticObjectField(Resources.class, "mSystem", xResources);
            XResources.init(threadLocal);
        }
    }

    /* access modifiers changed from: private */
    public static XResources cloneToXResources(MethodHookParam methodHookParam, String str) {
        Object result = methodHookParam.getResult();
        if (result == null || (result instanceof XResources) || Arrays.binarySearch(XRESOURCES_CONFLICTING_PACKAGES, AndroidAppHelper.currentPackageName()) == 0) {
            return null;
        }
        XResources xResources = (XResources) XposedBridge.cloneToSubclass(result, XResources.class);
        xResources.initObject(str);
        if (xResources.isFirstLoad()) {
            String packageName = xResources.getPackageName();
            InitPackageResourcesParam initPackageResourcesParam = new InitPackageResourcesParam(XposedBridge.sInitPackageResourcesCallbacks);
            initPackageResourcesParam.packageName = packageName;
            initPackageResourcesParam.res = xResources;
            XCallback.callAll(initPackageResourcesParam);
        }
        methodHookParam.setResult(xResources);
        return xResources;
    }

    private static boolean needsToCloseFilesForFork() {
        if (VERSION.SDK_INT >= 24) {
            return true;
        }
        if (VERSION.SDK_INT < 21) {
            return false;
        }
        File file = new File(Environment.getRootDirectory(), "lib/libandroid_runtime.so");
        try {
            return XposedHelpers.fileContains(file, "Unable to construct file descriptor table");
        } catch (IOException unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("Could not check whether ");
            sb.append(file);
            sb.append(" has security patch level 5");
            Log.e("Xposed", sb.toString());
            return true;
        }
    }

    /* access modifiers changed from: private */
    public static void hookXposedInstaller(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod("de.robv.android.xposed.installer.XposedApp", classLoader, "getActiveXposedVersion", XC_MethodReplacement.returnConstant(Integer.valueOf(XposedBridge.getXposedVersion())));
            XposedHelpers.findAndHookMethod("de.robv.android.xposed.installer.XposedApp", classLoader, "onCreate", new XC_MethodHook() {
                /* access modifiers changed from: protected */
                public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    Application application = (Application) methodHookParam.thisObject;
                    if (application.getResources().getIdentifier("installer_needs_update", StringTypedProperty.TYPE, "de.robv.android.xposed.installer") == 0) {
                        Log.e("XposedInstaller", "Xposed Installer is outdated (resource string \"installer_needs_update\" is missing)");
                        Toast.makeText(application, "Please update Xposed Installer!", 1).show();
                    }
                }
            });
        } catch (Throwable th) {
            Log.e("Xposed", "Could not hook Xposed Installer", th);
        }
    }

    static void loadModules() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_DIR);
        sb.append("conf/modules.list");
        String sb2 = sb.toString();
        BaseService appDataFileService = SELinuxHelper.getAppDataFileService();
        if (!appDataFileService.checkFileExists(sb2)) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Cannot load any modules because ");
            sb3.append(sb2);
            sb3.append(" was not found");
            Log.e("Xposed", sb3.toString());
            return;
        }
        ClassLoader classLoader = XposedBridge.BOOTCLASSLOADER;
        while (true) {
            ClassLoader parent = classLoader.getParent();
            if (parent == null) {
                break;
            }
            classLoader = parent;
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(appDataFileService.getFileInputStream(sb2)));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                loadModule(readLine, classLoader);
            } else {
                bufferedReader.close();
                return;
            }
        }
    }

    private static void loadModule(String str, ClassLoader classLoader) {
        ZipFile zipFile;
        StringBuilder sb = new StringBuilder();
        sb.append("Loading modules from ");
        sb.append(str);
        Log.i("Xposed", sb.toString());
        if (!new File(str).exists()) {
            Log.e("Xposed", "  File does not exist");
            return;
        }
        try {
            DexFile dexFile = new DexFile(str);
            if (dexFile.loadClass(INSTANT_RUN_CLASS, classLoader) != null) {
                Log.e("Xposed", "  Cannot load module, please disable \"Instant Run\" in Android Studio.");
                XposedHelpers.closeSilently(dexFile);
            } else if (dexFile.loadClass(XposedBridge.class.getName(), classLoader) != null) {
                Log.e("Xposed", "  Cannot load module:");
                Log.e("Xposed", "  The Xposed API classes are compiled into the module's APK.");
                Log.e("Xposed", "  This may cause strange issues and must be fixed by the module developer.");
                Log.e("Xposed", "  For details, see: http://api.xposed.info/using.html");
                XposedHelpers.closeSilently(dexFile);
            } else {
                XposedHelpers.closeSilently(dexFile);
                try {
                    zipFile = new ZipFile(str);
                    try {
                        ZipEntry entry = zipFile.getEntry("assets/xposed_init");
                        if (entry == null) {
                            Log.e("Xposed", "  assets/xposed_init not found in the APK");
                            XposedHelpers.closeSilently(zipFile);
                            return;
                        }
                        InputStream inputStream = zipFile.getInputStream(entry);
                        PathClassLoader pathClassLoader = new PathClassLoader(str, XposedBridge.BOOTCLASSLOADER);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        while (true) {
                            try {
                                String readLine = bufferedReader.readLine();
                                if (readLine == null) {
                                    break;
                                }
                                String trim = readLine.trim();
                                if (!trim.isEmpty() && !trim.startsWith("#")) {
                                    String str2 = "Xposed";
                                    try {
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("  Loading class ");
                                        sb2.append(trim);
                                        Log.i(str2, sb2.toString());
                                        Class loadClass = pathClassLoader.loadClass(trim);
                                        if (!IXposedMod.class.isAssignableFrom(loadClass)) {
                                            Log.e("Xposed", "    This class doesn't implement any sub-interface of IXposedMod, skipping it");
                                        } else if (!disableResources || !IXposedHookInitPackageResources.class.isAssignableFrom(loadClass)) {
                                            Object newInstance = loadClass.newInstance();
                                            if (XposedBridge.isZygote) {
                                                if (newInstance instanceof IXposedHookZygoteInit) {
                                                    StartupParam startupParam = new StartupParam();
                                                    startupParam.modulePath = str;
                                                    startupParam.startsSystemServer = startsSystemServer;
                                                    ((IXposedHookZygoteInit) newInstance).initZygote(startupParam);
                                                }
                                                if (newInstance instanceof IXposedHookLoadPackage) {
                                                    XposedBridge.hookLoadPackage(new Wrapper((IXposedHookLoadPackage) newInstance));
                                                }
                                                if (newInstance instanceof IXposedHookInitPackageResources) {
                                                    XposedBridge.hookInitPackageResources(new IXposedHookInitPackageResources.Wrapper((IXposedHookInitPackageResources) newInstance));
                                                }
                                            } else if (newInstance instanceof IXposedHookCmdInit) {
                                                IXposedHookCmdInit.StartupParam startupParam2 = new IXposedHookCmdInit.StartupParam();
                                                startupParam2.modulePath = str;
                                                startupParam2.startClassName = startClassName;
                                                ((IXposedHookCmdInit) newInstance).initCmdApp(startupParam2);
                                            }
                                        } else {
                                            Log.e("Xposed", "    This class requires resource-related hooks (which are disabled), skipping it.");
                                        }
                                    } catch (Throwable th) {
                                        String str3 = "Xposed";
                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append("    Failed to load class ");
                                        sb3.append(trim);
                                        Log.e(str3, sb3.toString(), th);
                                    }
                                }
                            } catch (IOException e) {
                                String str4 = "Xposed";
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("  Failed to load module from ");
                                sb4.append(str);
                                Log.e(str4, sb4.toString(), e);
                            } catch (Throwable th2) {
                                XposedHelpers.closeSilently((Closeable) inputStream);
                                XposedHelpers.closeSilently(zipFile);
                                throw th2;
                            }
                        }
                        XposedHelpers.closeSilently((Closeable) inputStream);
                        XposedHelpers.closeSilently(zipFile);
                    } catch (IOException e2) {
                        e = e2;
                        Log.e("Xposed", "  Cannot read assets/xposed_init in the APK", e);
                        XposedHelpers.closeSilently(zipFile);
                    }
                } catch (IOException e3) {
                    e = e3;
                    zipFile = null;
                    Log.e("Xposed", "  Cannot read assets/xposed_init in the APK", e);
                    XposedHelpers.closeSilently(zipFile);
                }
            }
        } catch (IOException e4) {
            Log.e("Xposed", "  Cannot load module", e4);
        }
    }
}
