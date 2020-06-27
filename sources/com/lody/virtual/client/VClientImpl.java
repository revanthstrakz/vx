package com.lody.virtual.client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.ConditionVariable;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.system.ErrnoException;
import android.system.Os;
import com.lody.virtual.client.IVClient.Stub;
import com.lody.virtual.client.core.CrashHandler;
import com.lody.virtual.client.core.InvocationStubManager;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.SpecialComponentList;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.fixer.ContextFixer;
import com.lody.virtual.client.hook.delegate.AppInstrumentation;
import com.lody.virtual.client.hook.providers.ProviderHook;
import com.lody.virtual.client.hook.proxies.p005am.HCallbackStub;
import com.lody.virtual.client.hook.secondary.ProxyServiceFactory;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.ipc.VDeviceManager;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.client.ipc.VirtualStorageManager;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.compat.StorageManagerCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.InstalledAppInfo;
import com.lody.virtual.remote.PendingResultData;
import com.lody.virtual.remote.VDeviceInfo;
import com.lody.virtual.server.interfaces.IUiCallback;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import mirror.android.app.ActivityThread;
import mirror.android.app.ActivityThread.ProviderClientRecord;
import mirror.android.app.ActivityThread.ProviderClientRecordJB;
import mirror.android.app.ActivityThreadNMR1;
import mirror.android.app.ContextImpl;
import mirror.android.app.IActivityManager.ContentProviderHolder;
import mirror.android.app.LoadedApk;
import mirror.android.content.ContentProviderHolderOreo;
import mirror.android.p017os.Build;
import mirror.android.providers.Settings;
import mirror.android.providers.Settings.Global;
import mirror.android.providers.Settings.NameValueCache;
import mirror.android.providers.Settings.NameValueCacheOreo;
import mirror.android.providers.Settings.Secure;
import mirror.android.providers.Settings.System;
import mirror.android.renderscript.RenderScriptCacheDir;
import mirror.android.view.HardwareRenderer;
import mirror.android.view.RenderScript;
import mirror.android.view.ThreadedRenderer;
import mirror.com.android.internal.content.ReferrerIntent;
import mirror.dalvik.system.VMRuntime;
import mirror.java.lang.ThreadGroup;
import mirror.java.lang.ThreadGroupN;
import p013io.virtualapp.utils.HanziToPinyin.Token;
import p015me.weishu.exposed.ExposedBridge;

public final class VClientImpl extends Stub {
    private static final int NEW_INTENT = 11;
    private static final int RECEIVER = 12;
    private static final String TAG = "VClientImpl";
    /* access modifiers changed from: private */
    @SuppressLint({"StaticFieldLeak"})
    public static final VClientImpl gClient = new VClientImpl();
    /* access modifiers changed from: private */
    public CrashHandler crashHandler;
    private VDeviceInfo deviceInfo;
    private AppBindData mBoundApplication;

    /* renamed from: mH */
    private final C0969H f176mH = new C0969H();
    private Application mInitialApplication;
    private Instrumentation mInstrumentation = AppInstrumentation.getDefault();
    private ConditionVariable mTempLock;
    private IUiCallback mUiCallback;
    private IBinder token;
    private int vuid;

    private final class AppBindData {
        ApplicationInfo appInfo;
        Object info;
        String processName;
        List<ProviderInfo> providers;

        private AppBindData() {
        }
    }

    /* renamed from: com.lody.virtual.client.VClientImpl$H */
    private class C0969H extends Handler {
        private C0969H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 11:
                    VClientImpl.this.handleNewIntent((NewIntentData) message.obj);
                    return;
                case 12:
                    VClientImpl.this.handleReceiver((ReceiverData) message.obj);
                    return;
                default:
                    return;
            }
        }
    }

    private final class NewIntentData {
        String creator;
        Intent intent;
        IBinder token;

        private NewIntentData() {
        }
    }

    private final class ReceiverData {
        ComponentName component;
        Intent intent;
        String processName;
        PendingResultData resultData;

        private ReceiverData() {
        }
    }

    private static class RootThreadGroup extends ThreadGroup {
        RootThreadGroup(ThreadGroup threadGroup) {
            super(threadGroup, "VA-Root");
        }

        public void uncaughtException(Thread thread, Throwable th) {
            CrashHandler access$600 = VClientImpl.gClient.crashHandler;
            if (access$600 != null) {
                access$600.handleUncaughtException(thread, th);
                return;
            }
            VLog.m88e("uncaught", th);
            System.exit(0);
        }
    }

    public static VClientImpl get() {
        return gClient;
    }

    public boolean isBound() {
        return this.mBoundApplication != null;
    }

    public VDeviceInfo getDeviceInfo() {
        if (this.deviceInfo == null) {
            synchronized (this) {
                if (this.deviceInfo == null) {
                    this.deviceInfo = VDeviceManager.get().getDeviceInfo(VUserHandle.getUserId(this.vuid));
                }
            }
        }
        return this.deviceInfo;
    }

    public Application getCurrentApplication() {
        return this.mInitialApplication;
    }

    public String getCurrentPackage() {
        if (this.mBoundApplication != null) {
            return this.mBoundApplication.appInfo.packageName;
        }
        return VPackageManager.get().getNameForUid(getVUid());
    }

    public ApplicationInfo getCurrentApplicationInfo() {
        if (this.mBoundApplication != null) {
            return this.mBoundApplication.appInfo;
        }
        return null;
    }

    public CrashHandler getCrashHandler() {
        return this.crashHandler;
    }

    public void setCrashHandler(CrashHandler crashHandler2) {
        this.crashHandler = crashHandler2;
    }

    public int getVUid() {
        return this.vuid;
    }

    public int getBaseVUid() {
        return VUserHandle.getAppId(this.vuid);
    }

    public ClassLoader getClassLoader(ApplicationInfo applicationInfo) {
        return createPackageContext(applicationInfo.packageName).getClassLoader();
    }

    public ClassLoader getClassLoader(String str) {
        return createPackageContext(str).getClassLoader();
    }

    private void sendMessage(int i, Object obj) {
        Message obtain = Message.obtain();
        obtain.what = i;
        obtain.obj = obj;
        this.f176mH.sendMessage(obtain);
    }

    public IBinder getAppThread() {
        return (IBinder) ActivityThread.getApplicationThread.call(VirtualCore.mainThread(), new Object[0]);
    }

    public IBinder getToken() {
        return this.token;
    }

    public void initProcess(IBinder iBinder, int i) {
        this.token = iBinder;
        this.vuid = i;
    }

    /* access modifiers changed from: private */
    public void handleNewIntent(NewIntentData newIntentData) {
        Intent intent;
        if (VERSION.SDK_INT >= 22) {
            intent = (Intent) ReferrerIntent.ctor.newInstance(newIntentData.intent, newIntentData.creator);
        } else {
            intent = newIntentData.intent;
        }
        if (ActivityThread.performNewIntents != null) {
            ActivityThread.performNewIntents.call(VirtualCore.mainThread(), newIntentData.token, Collections.singletonList(intent));
        } else if (BuildCompat.isQ()) {
            ActivityThread.handleNewIntent.call(VirtualCore.mainThread(), newIntentData.token, Collections.singletonList(intent));
        } else {
            ActivityThreadNMR1.performNewIntents.call(VirtualCore.mainThread(), newIntentData.token, Collections.singletonList(intent), Boolean.valueOf(true));
        }
    }

    public void bindApplicationForActivity(String str, String str2, Intent intent) {
        this.mUiCallback = VirtualCore.getUiCallback(intent);
        bindApplication(str, str2);
    }

    public void bindApplication(final String str, final String str2) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            bindApplicationNoCheck(str, str2, new ConditionVariable());
            return;
        }
        final ConditionVariable conditionVariable = new ConditionVariable();
        VirtualRuntime.getUIHandler().post(new Runnable() {
            public void run() {
                VClientImpl.this.bindApplicationNoCheck(str, str2, conditionVariable);
                conditionVariable.open();
            }
        });
        conditionVariable.block();
    }

    /* access modifiers changed from: private */
    public void bindApplicationNoCheck(String str, String str2, ConditionVariable conditionVariable) {
        File file;
        String str3;
        String str4 = str;
        ConditionVariable conditionVariable2 = conditionVariable;
        VDeviceInfo deviceInfo2 = getDeviceInfo();
        String str5 = str2 == null ? str4 : str2;
        this.mTempLock = conditionVariable2;
        try {
            setupUncaughtHandler();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        try {
            fixInstalledProviders();
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
        Build.SERIAL.set(deviceInfo2.serial);
        Build.DEVICE.set(android.os.Build.DEVICE.replace(Token.SEPARATOR, "_"));
        ActivityThread.mInitialApplication.set(VirtualCore.mainThread(), null);
        AppBindData appBindData = new AppBindData();
        if (VirtualCore.get().getInstalledAppInfo(str4, 0) == null) {
            new Exception("App not exist!").printStackTrace();
            Process.killProcess(0);
            System.exit(0);
        }
        appBindData.appInfo = VPackageManager.get().getApplicationInfo(str4, 0, VUserHandle.getUserId(this.vuid));
        appBindData.processName = str5;
        appBindData.appInfo.processName = str5;
        appBindData.providers = VPackageManager.get().queryContentProviders(str5, getVUid(), 128);
        VLog.m89i(TAG, String.format("Binding application %s, (%s)", new Object[]{appBindData.appInfo.packageName, appBindData.processName}), new Object[0]);
        this.mBoundApplication = appBindData;
        VirtualRuntime.setupRuntime(appBindData.processName, appBindData.appInfo);
        int i = appBindData.appInfo.targetSdkVersion;
        if (i < 9) {
            StrictMode.setThreadPolicy(new Builder(StrictMode.getThreadPolicy()).permitNetwork().build());
        }
        if (VERSION.SDK_INT >= 21 && i < 21) {
            mirror.android.p017os.Message.updateCheckRecycle.call(Integer.valueOf(i));
        }
        if (VASettings.ENABLE_IO_REDIRECT) {
            startIOUniformer();
        }
        NativeEngine.launchEngine();
        Object mainThread = VirtualCore.mainThread();
        NativeEngine.startDexOverride();
        Context createPackageContext = createPackageContext(appBindData.appInfo.packageName);
        try {
            System.class.getDeclaredMethod("setProperty", new Class[]{String.class, String.class}).invoke(null, new Object[]{"java.io.tmpdir", createPackageContext.getCacheDir().getAbsolutePath()});
        } catch (Throwable th3) {
            VLog.m87e(TAG, "set tmp dir error:", th3);
        }
        if (VERSION.SDK_INT >= 23) {
            file = createPackageContext.getCodeCacheDir();
        } else {
            file = createPackageContext.getCacheDir();
        }
        if (VERSION.SDK_INT < 24) {
            if (HardwareRenderer.setupDiskCache != null) {
                HardwareRenderer.setupDiskCache.call(file);
            }
        } else if (ThreadedRenderer.setupDiskCache != null) {
            ThreadedRenderer.setupDiskCache.call(file);
        }
        if (VERSION.SDK_INT >= 23) {
            if (RenderScriptCacheDir.setupDiskCache != null) {
                RenderScriptCacheDir.setupDiskCache.call(file);
            }
        } else if (VERSION.SDK_INT >= 16 && RenderScript.setupDiskCache != null) {
            RenderScript.setupDiskCache.call(file);
        }
        Object fixBoundApp = fixBoundApp(this.mBoundApplication);
        this.mBoundApplication.info = ContextImpl.mPackageInfo.get(createPackageContext);
        mirror.android.app.ActivityThread.AppBindData.info.set(fixBoundApp, appBindData.info);
        VMRuntime.setTargetSdkVersion.call(VMRuntime.getRuntime.call(new Object[0]), Integer.valueOf(appBindData.appInfo.targetSdkVersion));
        boolean isConflictingInstrumentation = SpecialComponentList.isConflictingInstrumentation(str);
        if (!isConflictingInstrumentation) {
            InvocationStubManager.getInstance().checkEnv(AppInstrumentation.class);
        }
        ApplicationInfo applicationInfo = (ApplicationInfo) LoadedApk.mApplicationInfo.get(appBindData.info);
        if (VERSION.SDK_INT >= 26 && applicationInfo.splitNames == null) {
            applicationInfo.splitNames = new String[1];
        }
        if (VirtualCore.get().isXposedEnabled()) {
            VLog.m89i(TAG, "Xposed is enabled.", new Object[0]);
            ClassLoader classLoader = createPackageContext.getClassLoader();
            ExposedBridge.initOnce(createPackageContext, appBindData.appInfo, classLoader);
            for (InstalledAppInfo installedAppInfo : VirtualCore.get().getInstalledApps(0)) {
                ExposedBridge.loadModule(installedAppInfo.apkPath, installedAppInfo.getOdexFile().getParent(), installedAppInfo.libPath, appBindData.appInfo, classLoader);
            }
        } else {
            VLog.m91w(TAG, "Xposed is disable..", new Object[0]);
        }
        this.mInitialApplication = (Application) LoadedApk.makeApplication.call(appBindData.info, Boolean.valueOf(false), null);
        ActivityThread.mInitialApplication.set(mainThread, this.mInitialApplication);
        ContextFixer.fixContext(this.mInitialApplication);
        if (VERSION.SDK_INT >= 24 && "com.tencent.mm:recovery".equals(str5)) {
            fixWeChatRecovery(this.mInitialApplication);
        }
        if (appBindData.providers != null) {
            installContentProviders(this.mInitialApplication, appBindData.providers);
        }
        if (conditionVariable2 != null) {
            conditionVariable.open();
            this.mTempLock = null;
        }
        VirtualCore.get().getComponentDelegate().beforeApplicationCreate(this.mInitialApplication);
        try {
            this.mInstrumentation.callApplicationOnCreate(this.mInitialApplication);
            InvocationStubManager.getInstance().checkEnv(HCallbackStub.class);
            if (isConflictingInstrumentation) {
                InvocationStubManager.getInstance().checkEnv(AppInstrumentation.class);
            }
            Application application = (Application) ActivityThread.mInitialApplication.get(mainThread);
            if (application != null) {
                this.mInitialApplication = application;
            }
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(this.mInitialApplication, e)) {
                if (this.mUiCallback != null) {
                    try {
                        this.mUiCallback.onOpenFailed(str4, VUserHandle.myUserId());
                    } catch (RemoteException unused) {
                    }
                }
                VActivityManager.get().appDoneExecuting();
                StringBuilder sb = new StringBuilder();
                sb.append("Unable to create application ");
                if (this.mInitialApplication == null) {
                    str3 = " [null application] ";
                } else {
                    str3 = this.mInitialApplication.getClass().getName();
                }
                sb.append(str3);
                sb.append(": ");
                sb.append(e.toString());
                throw new RuntimeException(sb.toString(), e);
            }
        }
        VActivityManager.get().appDoneExecuting();
        VirtualCore.get().getComponentDelegate().afterApplicationCreate(this.mInitialApplication);
    }

    private void fixWeChatRecovery(Application application) {
        try {
            Field field = application.getClassLoader().loadClass("com.tencent.recovery.Recovery").getField("context");
            field.setAccessible(true);
            if (field.get(null) == null) {
                field.set(null, application.getBaseContext());
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void setupUncaughtHandler() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        while (threadGroup.getParent() != null) {
            threadGroup = threadGroup.getParent();
        }
        RootThreadGroup rootThreadGroup = new RootThreadGroup(threadGroup);
        if (VERSION.SDK_INT < 24) {
            List list = (List) ThreadGroup.groups.get(threadGroup);
            synchronized (list) {
                ArrayList<ThreadGroup> arrayList = new ArrayList<>(list);
                arrayList.remove(rootThreadGroup);
                ThreadGroup.groups.set(rootThreadGroup, arrayList);
                list.clear();
                list.add(rootThreadGroup);
                ThreadGroup.groups.set(threadGroup, list);
                for (ThreadGroup threadGroup2 : arrayList) {
                    if (threadGroup2 != rootThreadGroup) {
                        ThreadGroup.parent.set(threadGroup2, rootThreadGroup);
                    }
                }
            }
            return;
        }
        ThreadGroup[] threadGroupArr = (ThreadGroup[]) ThreadGroupN.groups.get(threadGroup);
        synchronized (threadGroupArr) {
            ThreadGroup[] threadGroupArr2 = (ThreadGroup[]) threadGroupArr.clone();
            ThreadGroupN.groups.set(rootThreadGroup, threadGroupArr2);
            ThreadGroupN.groups.set(threadGroup, new ThreadGroup[]{rootThreadGroup});
            for (ThreadGroup threadGroup3 : threadGroupArr2) {
                if (threadGroup3 != rootThreadGroup) {
                    ThreadGroupN.parent.set(threadGroup3, rootThreadGroup);
                }
            }
            ThreadGroupN.ngroups.set(threadGroup, Integer.valueOf(1));
        }
    }

    @SuppressLint({"SdCardPath"})
    private void startIOUniformer() {
        ApplicationInfo applicationInfo = this.mBoundApplication.appInfo;
        int myUserId = VUserHandle.myUserId();
        String path = this.deviceInfo.getWifiFile(myUserId).getPath();
        NativeEngine.redirectDirectory("/sys/class/net/wlan0/address", path);
        NativeEngine.redirectDirectory("/sys/class/net/eth0/address", path);
        NativeEngine.redirectDirectory("/sys/class/net/wifi/address", path);
        StringBuilder sb = new StringBuilder();
        sb.append("/data/data/");
        sb.append(applicationInfo.packageName);
        NativeEngine.redirectDirectory(sb.toString(), applicationInfo.dataDir);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("/data/user/0/");
        sb2.append(applicationInfo.packageName);
        NativeEngine.redirectDirectory(sb2.toString(), applicationInfo.dataDir);
        if (VERSION.SDK_INT >= 24) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("/data/user_de/0/");
            sb3.append(applicationInfo.packageName);
            NativeEngine.redirectDirectory(sb3.toString(), applicationInfo.dataDir);
        }
        String absolutePath = VEnvironment.getAppLibDirectory(applicationInfo.packageName).getAbsolutePath();
        File userSystemDirectory = VEnvironment.getUserSystemDirectory(myUserId);
        StringBuilder sb4 = new StringBuilder();
        sb4.append(applicationInfo.packageName);
        sb4.append("/lib");
        NativeEngine.redirectDirectory(new File(userSystemDirectory, sb4.toString()).getAbsolutePath(), absolutePath);
        StringBuilder sb5 = new StringBuilder();
        sb5.append("/data/data/");
        sb5.append(applicationInfo.packageName);
        sb5.append("/lib/");
        NativeEngine.redirectDirectory(sb5.toString(), absolutePath);
        StringBuilder sb6 = new StringBuilder();
        sb6.append("/data/user/0/");
        sb6.append(applicationInfo.packageName);
        sb6.append("/lib/");
        NativeEngine.redirectDirectory(sb6.toString(), absolutePath);
        File file = new File(VEnvironment.getDataUserPackageDirectory(myUserId, applicationInfo.packageName), "lib");
        if (!file.exists()) {
            try {
                Os.symlink(absolutePath, file.getPath());
            } catch (ErrnoException e) {
                VLog.m91w(TAG, "symlink error", e);
            }
        }
        setupVirtualStorage(applicationInfo, myUserId);
        NativeEngine.enableIORedirect();
    }

    private void setupVirtualStorage(ApplicationInfo applicationInfo, int i) {
        if (VirtualStorageManager.get().isVirtualStorageEnable(applicationInfo.packageName, i)) {
            File virtualStorageDir = VEnvironment.getVirtualStorageDir(applicationInfo.packageName, i);
            if (virtualStorageDir != null && virtualStorageDir.exists() && virtualStorageDir.isDirectory()) {
                HashSet mountPoints = getMountPoints();
                mountPoints.add(Environment.getExternalStorageDirectory().getAbsolutePath());
                HashSet<String> hashSet = new HashSet<>();
                hashSet.add(Environment.DIRECTORY_PODCASTS);
                hashSet.add(Environment.DIRECTORY_RINGTONES);
                hashSet.add(Environment.DIRECTORY_ALARMS);
                hashSet.add(Environment.DIRECTORY_NOTIFICATIONS);
                hashSet.add(Environment.DIRECTORY_PICTURES);
                hashSet.add(Environment.DIRECTORY_MOVIES);
                hashSet.add(Environment.DIRECTORY_DOWNLOADS);
                hashSet.add(Environment.DIRECTORY_DCIM);
                hashSet.add("Android/obb");
                if (VERSION.SDK_INT >= 19) {
                    hashSet.add(Environment.DIRECTORY_DOCUMENTS);
                }
                for (String str : hashSet) {
                    File file = new File(Environment.getExternalStorageDirectory(), str);
                    File file2 = new File(virtualStorageDir, str);
                    if (file.exists()) {
                        file2.mkdirs();
                    }
                }
                String absolutePath = virtualStorageDir.getAbsolutePath();
                NativeEngine.whitelist(absolutePath, true);
                String absolutePath2 = VEnvironment.getVirtualPrivateStorageDir(i).getAbsolutePath();
                NativeEngine.whitelist(absolutePath2, true);
                Iterator it = mountPoints.iterator();
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    for (String file3 : hashSet) {
                        NativeEngine.whitelist(new File(str2, file3).getAbsolutePath(), true);
                    }
                    NativeEngine.redirectDirectory(new File(str2, "Android/data/").getAbsolutePath(), absolutePath2);
                    NativeEngine.redirectDirectory(str2, absolutePath);
                }
            }
        }
    }

    @SuppressLint({"SdCardPath"})
    private HashSet<String> getMountPoints() {
        HashSet<String> hashSet = new HashSet<>(3);
        hashSet.add("/mnt/sdcard/");
        hashSet.add("/sdcard/");
        String[] allPoints = StorageManagerCompat.getAllPoints(VirtualCore.get().getContext());
        if (allPoints != null) {
            Collections.addAll(hashSet, allPoints);
        }
        return hashSet;
    }

    private Context createPackageContext(String str) {
        try {
            return VirtualCore.get().getContext().createPackageContext(str, 3);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            VirtualRuntime.crash(new RemoteException());
            throw new RuntimeException();
        }
    }

    private Object fixBoundApp(AppBindData appBindData) {
        Object obj = ActivityThread.mBoundApplication.get(VirtualCore.mainThread());
        mirror.android.app.ActivityThread.AppBindData.appInfo.set(obj, appBindData.appInfo);
        mirror.android.app.ActivityThread.AppBindData.processName.set(obj, appBindData.processName);
        mirror.android.app.ActivityThread.AppBindData.instrumentationName.set(obj, new ComponentName(appBindData.appInfo.packageName, Instrumentation.class.getName()));
        mirror.android.app.ActivityThread.AppBindData.providers.set(obj, appBindData.providers);
        return obj;
    }

    private void installContentProviders(Context context, List<ProviderInfo> list) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        Object mainThread = VirtualCore.mainThread();
        try {
            for (ProviderInfo installProvider : list) {
                ActivityThread.installProvider(mainThread, context, installProvider, null);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public IBinder acquireProviderClient(ProviderInfo providerInfo) {
        ContentProviderClient contentProviderClient;
        IInterface iInterface;
        if (this.mTempLock != null) {
            this.mTempLock.block();
        }
        if (!isBound()) {
            get().bindApplication(providerInfo.packageName, providerInfo.processName);
        }
        String[] split = providerInfo.authority.split(";");
        String str = split.length == 0 ? providerInfo.authority : split[0];
        ContentResolver contentResolver = VirtualCore.get().getContext().getContentResolver();
        try {
            if (VERSION.SDK_INT >= 16) {
                contentProviderClient = contentResolver.acquireUnstableContentProviderClient(str);
            } else {
                contentProviderClient = contentResolver.acquireContentProviderClient(str);
            }
        } catch (Throwable th) {
            VLog.m87e(TAG, "", th);
            contentProviderClient = null;
        }
        if (contentProviderClient != null) {
            iInterface = (IInterface) mirror.android.content.ContentProviderClient.mContentProvider.get(contentProviderClient);
            contentProviderClient.release();
        } else {
            iInterface = null;
        }
        if (iInterface != null) {
            return iInterface.asBinder();
        }
        return null;
    }

    private void fixInstalledProviders() {
        clearSettingProvider();
        for (Object next : ((Map) ActivityThread.mProviderMap.get(VirtualCore.mainThread())).values()) {
            if (BuildCompat.isOreo()) {
                IInterface iInterface = (IInterface) ProviderClientRecordJB.mProvider.get(next);
                Object obj = ProviderClientRecordJB.mHolder.get(next);
                if (obj != null) {
                    ProviderInfo providerInfo = (ProviderInfo) ContentProviderHolderOreo.info.get(obj);
                    if (!providerInfo.authority.startsWith(VASettings.STUB_CP_AUTHORITY)) {
                        IInterface createProxy = ProviderHook.createProxy(true, providerInfo.authority, iInterface);
                        ProviderClientRecordJB.mProvider.set(next, createProxy);
                        ContentProviderHolderOreo.provider.set(obj, createProxy);
                    }
                }
            } else if (VERSION.SDK_INT >= 16) {
                IInterface iInterface2 = (IInterface) ProviderClientRecordJB.mProvider.get(next);
                Object obj2 = ProviderClientRecordJB.mHolder.get(next);
                if (obj2 != null) {
                    ProviderInfo providerInfo2 = (ProviderInfo) ContentProviderHolder.info.get(obj2);
                    if (!providerInfo2.authority.startsWith(VASettings.STUB_CP_AUTHORITY)) {
                        IInterface createProxy2 = ProviderHook.createProxy(true, providerInfo2.authority, iInterface2);
                        ProviderClientRecordJB.mProvider.set(next, createProxy2);
                        ContentProviderHolder.provider.set(obj2, createProxy2);
                    }
                }
            } else {
                String str = (String) ProviderClientRecord.mName.get(next);
                IInterface iInterface3 = (IInterface) ProviderClientRecord.mProvider.get(next);
                if (iInterface3 != null && !str.startsWith(VASettings.STUB_CP_AUTHORITY)) {
                    ProviderClientRecord.mProvider.set(next, ProviderHook.createProxy(true, str, iInterface3));
                }
            }
        }
    }

    private void clearSettingProvider() {
        Object obj = System.sNameValueCache.get();
        if (obj != null) {
            clearContentProvider(obj);
        }
        Object obj2 = Secure.sNameValueCache.get();
        if (obj2 != null) {
            clearContentProvider(obj2);
        }
        if (VERSION.SDK_INT >= 17 && Global.TYPE != null) {
            Object obj3 = Global.sNameValueCache.get();
            if (obj3 != null) {
                clearContentProvider(obj3);
            }
        }
    }

    private static void clearContentProvider(Object obj) {
        if (BuildCompat.isOreo()) {
            Object obj2 = NameValueCacheOreo.mProviderHolder.get(obj);
            if (obj2 != null) {
                Settings.ContentProviderHolder.mContentProvider.set(obj2, null);
                return;
            }
            return;
        }
        NameValueCache.mContentProvider.set(obj, null);
    }

    public void finishActivity(IBinder iBinder) {
        VActivityManager.get().finishActivity(iBinder);
    }

    public void scheduleNewIntent(String str, IBinder iBinder, Intent intent) {
        NewIntentData newIntentData = new NewIntentData();
        newIntentData.creator = str;
        newIntentData.token = iBinder;
        newIntentData.intent = intent;
        sendMessage(11, newIntentData);
    }

    public void scheduleReceiver(String str, ComponentName componentName, Intent intent, PendingResultData pendingResultData) {
        ReceiverData receiverData = new ReceiverData();
        receiverData.resultData = pendingResultData;
        receiverData.intent = intent;
        receiverData.component = componentName;
        receiverData.processName = str;
        sendMessage(12, receiverData);
    }

    /* access modifiers changed from: private */
    public void handleReceiver(ReceiverData receiverData) {
        PendingResult build = receiverData.resultData.build();
        try {
            if (!isBound()) {
                bindApplication(receiverData.component.getPackageName(), receiverData.processName);
            }
            Context baseContext = this.mInitialApplication.getBaseContext();
            Context context = (Context) ContextImpl.getReceiverRestrictedContext.call(baseContext, new Object[0]);
            BroadcastReceiver broadcastReceiver = (BroadcastReceiver) baseContext.getClassLoader().loadClass(receiverData.component.getClassName()).newInstance();
            mirror.android.content.BroadcastReceiver.setPendingResult.call(broadcastReceiver, build);
            receiverData.intent.setExtrasClassLoader(baseContext.getClassLoader());
            if (receiverData.intent.getComponent() == null) {
                receiverData.intent.setComponent(receiverData.component);
            }
            broadcastReceiver.onReceive(context, receiverData.intent);
            if (mirror.android.content.BroadcastReceiver.getPendingResult.call(broadcastReceiver, new Object[0]) != null) {
                build.finish();
            }
            VActivityManager.get().broadcastFinish(receiverData.resultData);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Unable to start receiver: %s ", new Object[]{receiverData.component}), e);
        }
    }

    public IBinder createProxyService(ComponentName componentName, IBinder iBinder) {
        return ProxyServiceFactory.getProxyService(getCurrentApplication(), componentName, iBinder);
    }

    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("process : ");
        sb.append(VirtualRuntime.getProcessName());
        sb.append("\n");
        sb.append("initialPkg : ");
        sb.append(VirtualRuntime.getInitialPackageName());
        sb.append("\n");
        sb.append("vuid : ");
        sb.append(this.vuid);
        return sb.toString();
    }
}
