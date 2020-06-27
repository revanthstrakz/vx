package com.lody.virtual.client.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.content.pm.ShortcutManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Toast;
import com.android.launcher3.IconCache;
import com.lody.virtual.C0966R;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.fixer.ContextFixer;
import com.lody.virtual.client.hook.delegate.ComponentDelegate;
import com.lody.virtual.client.hook.delegate.PhoneInfoDelegate;
import com.lody.virtual.client.hook.delegate.TaskDescriptionDelegate;
import com.lody.virtual.client.ipc.LocalProxyUtils;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.helper.utils.BitmapUtils;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.InstallResult;
import com.lody.virtual.remote.InstalledAppInfo;
import com.lody.virtual.server.IAppManager;
import com.lody.virtual.server.interfaces.IAppRequestListener;
import com.lody.virtual.server.interfaces.IPackageObserver;
import com.lody.virtual.server.interfaces.IPackageObserver.Stub;
import com.lody.virtual.server.interfaces.IUiCallback;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import mirror.android.app.ActivityThread;
import p015me.weishu.reflection.Reflection;

public final class VirtualCore {
    public static final int GET_HIDDEN_APP = 1;
    public static final String TAICHI_PACKAGE = "me.weishu.exp";
    @SuppressLint({"StaticFieldLeak"})
    private static VirtualCore gCore = new VirtualCore();
    private ComponentDelegate componentDelegate;
    private Context context;
    private PackageInfo hostPkgInfo;
    private String hostPkgName;
    private ConditionVariable initLock = new ConditionVariable();
    private boolean isStartUp;
    private IAppManager mService;
    private String mainProcessName;
    private Object mainThread;
    private final int myUid = Process.myUid();
    private PhoneInfoDelegate phoneInfoDelegate;
    private String processName;
    private ProcessType processType;
    private int systemPid;
    private TaskDescriptionDelegate taskDescriptionDelegate;
    private PackageManager unHookPackageManager;

    public interface AppRequestListener {
        void onRequestInstall(String str);

        void onRequestUninstall(String str);
    }

    public interface OnEmitShortcutListener {
        Bitmap getIcon(Bitmap bitmap);

        String getName(String str);
    }

    public static abstract class PackageObserver extends Stub {
    }

    private enum ProcessType {
        Server,
        VAppClient,
        Main,
        CHILD
    }

    public static abstract class UiCallback extends IUiCallback.Stub {
    }

    public static abstract class VirtualInitializer {
        public void onChildProcess() {
        }

        public void onMainProcess() {
        }

        public void onServerProcess() {
        }

        public void onVirtualProcess() {
        }
    }

    @Deprecated
    public void preOpt(String str) throws IOException {
    }

    private VirtualCore() {
    }

    public static VirtualCore get() {
        return gCore;
    }

    public static PackageManager getPM() {
        return get().getPackageManager();
    }

    public static Object mainThread() {
        return get().mainThread;
    }

    public ConditionVariable getInitLock() {
        return this.initLock;
    }

    public int myUid() {
        return this.myUid;
    }

    public int myUserId() {
        return VUserHandle.getUserId(this.myUid);
    }

    public ComponentDelegate getComponentDelegate() {
        return this.componentDelegate == null ? ComponentDelegate.EMPTY : this.componentDelegate;
    }

    public void setComponentDelegate(ComponentDelegate componentDelegate2) {
        this.componentDelegate = componentDelegate2;
    }

    public PhoneInfoDelegate getPhoneInfoDelegate() {
        return this.phoneInfoDelegate;
    }

    public void setPhoneInfoDelegate(PhoneInfoDelegate phoneInfoDelegate2) {
        this.phoneInfoDelegate = phoneInfoDelegate2;
    }

    public void setCrashHandler(CrashHandler crashHandler) {
        VClientImpl.get().setCrashHandler(crashHandler);
    }

    public TaskDescriptionDelegate getTaskDescriptionDelegate() {
        return this.taskDescriptionDelegate;
    }

    public void setTaskDescriptionDelegate(TaskDescriptionDelegate taskDescriptionDelegate2) {
        this.taskDescriptionDelegate = taskDescriptionDelegate2;
    }

    public int[] getGids() {
        return this.hostPkgInfo.gids;
    }

    public Context getContext() {
        return this.context;
    }

    public PackageManager getPackageManager() {
        return this.context.getPackageManager();
    }

    public String getHostPkg() {
        return this.hostPkgName;
    }

    public PackageManager getUnHookPackageManager() {
        return this.unHookPackageManager;
    }

    public void startup(Context context2) throws Throwable {
        if (this.isStartUp) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Reflection.unseal(context2);
            StringBuilder sb = new StringBuilder();
            sb.append(context2.getPackageName());
            sb.append(IconCache.EMPTY_CLASS_NAME);
            sb.append(VASettings.STUB_DEF_AUTHORITY);
            VASettings.STUB_CP_AUTHORITY = sb.toString();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(context2.getPackageName());
            sb2.append(IconCache.EMPTY_CLASS_NAME);
            sb2.append(ServiceManagerNative.SERVICE_DEF_AUTH);
            ServiceManagerNative.SERVICE_CP_AUTH = sb2.toString();
            this.context = context2;
            this.mainThread = ActivityThread.currentActivityThread.call(new Object[0]);
            this.unHookPackageManager = context2.getPackageManager();
            this.hostPkgInfo = this.unHookPackageManager.getPackageInfo(context2.getPackageName(), 8);
            detectProcessType();
            InvocationStubManager instance = InvocationStubManager.getInstance();
            instance.init();
            instance.injectAll();
            ContextFixer.fixContext(context2);
            this.isStartUp = true;
            if (this.initLock != null) {
                this.initLock.open();
                this.initLock = null;
                return;
            }
            return;
        }
        throw new IllegalStateException("VirtualCore.startup() must called in main thread.");
    }

    public void waitForEngine() {
        ServiceManagerNative.ensureServerStarted();
    }

    public boolean isEngineLaunched() {
        String engineProcessName = getEngineProcessName();
        for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) this.context.getSystemService(ServiceManagerNative.ACTIVITY)).getRunningAppProcesses()) {
            if (runningAppProcessInfo.processName.endsWith(engineProcessName)) {
                return true;
            }
        }
        return false;
    }

    public String getEngineProcessName() {
        return this.context.getString(C0966R.string.engine_process_name);
    }

    public void initialize(VirtualInitializer virtualInitializer) {
        if (virtualInitializer != null) {
            switch (this.processType) {
                case Main:
                    virtualInitializer.onMainProcess();
                    return;
                case VAppClient:
                    virtualInitializer.onVirtualProcess();
                    return;
                case Server:
                    virtualInitializer.onServerProcess();
                    return;
                case CHILD:
                    virtualInitializer.onChildProcess();
                    return;
                default:
                    return;
            }
        } else {
            throw new IllegalStateException("Initializer = NULL");
        }
    }

    private void detectProcessType() {
        this.hostPkgName = this.context.getApplicationInfo().packageName;
        this.mainProcessName = this.context.getApplicationInfo().processName;
        this.processName = (String) ActivityThread.getProcessName.call(this.mainThread, new Object[0]);
        if (this.processName.equals(this.mainProcessName)) {
            this.processType = ProcessType.Main;
        } else if (this.processName.endsWith(Constants.SERVER_PROCESS_NAME)) {
            this.processType = ProcessType.Server;
        } else if (VActivityManager.get().isAppProcess(this.processName)) {
            this.processType = ProcessType.VAppClient;
        } else {
            this.processType = ProcessType.CHILD;
        }
        if (isVAppProcess()) {
            this.systemPid = VActivityManager.get().getSystemPid();
        }
    }

    private IAppManager getService() {
        if (this.mService == null || (!get().isVAppProcess() && !this.mService.asBinder().pingBinder())) {
            synchronized (this) {
                this.mService = (IAppManager) LocalProxyUtils.genProxy(IAppManager.class, getStubInterface());
            }
        }
        return this.mService;
    }

    private Object getStubInterface() {
        return IAppManager.Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.APP));
    }

    public boolean isVAppProcess() {
        return ProcessType.VAppClient == this.processType;
    }

    public boolean isMainProcess() {
        return ProcessType.Main == this.processType;
    }

    public boolean isChildProcess() {
        return ProcessType.CHILD == this.processType;
    }

    public boolean isServerProcess() {
        return ProcessType.Server == this.processType;
    }

    public String getProcessName() {
        return this.processName;
    }

    public String getMainProcessName() {
        return this.mainProcessName;
    }

    public boolean isAppRunning(String str, int i) {
        return VActivityManager.get().isAppRunning(str, i);
    }

    public InstallResult installPackage(String str, int i) {
        try {
            return getService().installPackage(str, i);
        } catch (RemoteException e) {
            return (InstallResult) VirtualRuntime.crash(e);
        }
    }

    public boolean clearPackage(String str) {
        try {
            return getService().clearPackage(str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public boolean clearPackageAsUser(int i, String str) {
        try {
            return getService().clearPackageAsUser(i, str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void addVisibleOutsidePackage(String str) {
        try {
            getService().addVisibleOutsidePackage(str);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void removeVisibleOutsidePackage(String str) {
        try {
            getService().removeVisibleOutsidePackage(str);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public boolean isOutsidePackageVisible(String str) {
        if (!isXposedEnabled()) {
            try {
                getUnHookPackageManager().getPackageInfo(str, 0);
                return true;
            } catch (NameNotFoundException unused) {
                return false;
            }
        } else {
            try {
                return getService().isOutsidePackageVisible(str);
            } catch (RemoteException e) {
                return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
            }
        }
    }

    public boolean isXposedEnabled() {
        return !get().getContext().getFileStreamPath(".disable_xposed").exists();
    }

    public boolean isAppInstalled(String str) {
        try {
            return getService().isAppInstalled(str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public boolean isPackageLaunchable(String str) {
        InstalledAppInfo installedAppInfo = getInstalledAppInfo(str, 0);
        if (installedAppInfo == null || getLaunchIntent(str, installedAppInfo.getInstalledUsers()[0]) == null) {
            return false;
        }
        return true;
    }

    public Intent getLaunchIntent(String str, int i) {
        VPackageManager vPackageManager = VPackageManager.get();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.INFO");
        intent.setPackage(str);
        List queryIntentActivities = vPackageManager.queryIntentActivities(intent, intent.resolveType(this.context), 0, i);
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            intent.removeCategory("android.intent.category.INFO");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(str);
            queryIntentActivities = vPackageManager.queryIntentActivities(intent, intent.resolveType(this.context), 0, i);
        }
        ActivityInfo activityInfo = null;
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            return null;
        }
        Iterator it = queryIntentActivities.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ResolveInfo resolveInfo = (ResolveInfo) it.next();
            if (resolveInfo.activityInfo.enabled) {
                activityInfo = resolveInfo.activityInfo;
                break;
            }
        }
        if (activityInfo == null) {
            activityInfo = ((ResolveInfo) queryIntentActivities.get(0)).activityInfo;
        }
        Intent intent2 = new Intent(intent);
        intent2.setFlags(268435456);
        intent2.setClassName(activityInfo.packageName, activityInfo.name);
        return intent2;
    }

    public boolean createShortcut(int i, String str, OnEmitShortcutListener onEmitShortcutListener) {
        return createShortcut(i, str, null, onEmitShortcutListener);
    }

    public boolean createShortcut(int i, String str, Intent intent, OnEmitShortcutListener onEmitShortcutListener) {
        InstalledAppInfo installedAppInfo = getInstalledAppInfo(str, 0);
        if (installedAppInfo == null) {
            return false;
        }
        ApplicationInfo applicationInfo = installedAppInfo.getApplicationInfo(i);
        PackageManager packageManager = this.context.getPackageManager();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(i);
        String sb2 = sb.toString();
        try {
            String charSequence = applicationInfo.loadLabel(packageManager).toString();
            Bitmap drawableToBitmap = BitmapUtils.drawableToBitmap(applicationInfo.loadIcon(packageManager));
            if (onEmitShortcutListener != null) {
                String name = onEmitShortcutListener.getName(charSequence);
                if (name != null) {
                    charSequence = name;
                }
                Bitmap icon = onEmitShortcutListener.getIcon(drawableToBitmap);
                if (icon != null) {
                    drawableToBitmap = icon;
                }
            }
            Intent launchIntent = getLaunchIntent(str, i);
            if (launchIntent == null) {
                return false;
            }
            Intent intent2 = new Intent("android.intent.action.VIEW");
            intent2.setClassName(getHostPkg(), Constants.SHORTCUT_PROXY_ACTIVITY_NAME);
            intent2.addCategory("android.intent.category.DEFAULT");
            if (intent != null) {
                intent2.putExtra("_VA_|_splash_", intent.toUri(0));
            }
            intent2.putExtra("_VA_|_intent_", launchIntent);
            intent2.putExtra("_VA_|_uri_", launchIntent.toUri(0));
            intent2.putExtra("_VA_|_user_id_", i);
            if (VERSION.SDK_INT >= 25) {
                intent2.removeExtra("_VA_|_intent_");
                ShortcutInfo build = new Builder(this.context, sb2).setShortLabel(charSequence).setLongLabel(charSequence).setIcon(Icon.createWithBitmap(drawableToBitmap)).setIntent(intent2).build();
                createShortcutAboveN(this.context, build);
                if (VERSION.SDK_INT >= 26) {
                    return createDeskShortcutAboveO(this.context, build);
                }
            }
            Intent intent3 = new Intent();
            intent3.putExtra("android.intent.extra.shortcut.INTENT", intent2);
            intent3.putExtra("android.intent.extra.shortcut.NAME", charSequence);
            intent3.putExtra("android.intent.extra.shortcut.ICON", drawableToBitmap);
            intent3.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            try {
                this.context.sendBroadcast(intent3);
                return true;
            } catch (Throwable unused) {
                return false;
            }
        } catch (Throwable unused2) {
            return false;
        }
    }

    @TargetApi(25)
    private static boolean createShortcutAboveN(Context context2, ShortcutInfo shortcutInfo) {
        ShortcutManager shortcutManager = (ShortcutManager) context2.getSystemService(ShortcutManager.class);
        if (shortcutManager == null) {
            return false;
        }
        try {
            int maxShortcutCountPerActivity = shortcutManager.getMaxShortcutCountPerActivity();
            List dynamicShortcuts = shortcutManager.getDynamicShortcuts();
            if (dynamicShortcuts.size() >= maxShortcutCountPerActivity) {
                Collections.sort(dynamicShortcuts, new Comparator<ShortcutInfo>() {
                    public int compare(ShortcutInfo shortcutInfo, ShortcutInfo shortcutInfo2) {
                        int i = ((shortcutInfo.getLastChangedTimestamp() - shortcutInfo2.getLastChangedTimestamp()) > 0 ? 1 : ((shortcutInfo.getLastChangedTimestamp() - shortcutInfo2.getLastChangedTimestamp()) == 0 ? 0 : -1));
                        if (i == 0) {
                            return 0;
                        }
                        return i > 0 ? 1 : -1;
                    }
                });
                shortcutManager.removeDynamicShortcuts(Collections.singletonList(((ShortcutInfo) dynamicShortcuts.remove(0)).getId()));
            }
            shortcutManager.addDynamicShortcuts(Collections.singletonList(shortcutInfo));
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    @TargetApi(26)
    private static boolean createDeskShortcutAboveO(Context context2, ShortcutInfo shortcutInfo) {
        ShortcutManager shortcutManager = (ShortcutManager) context2.getSystemService(ShortcutManager.class);
        boolean z = false;
        if (shortcutManager == null || !shortcutManager.isRequestPinShortcutSupported()) {
            return false;
        }
        Iterator it = shortcutManager.getPinnedShortcuts().iterator();
        while (true) {
            if (it.hasNext()) {
                if (TextUtils.equals(((ShortcutInfo) it.next()).getId(), shortcutInfo.getId())) {
                    Toast.makeText(context2, C0966R.string.create_shortcut_already_exist, 0).show();
                    z = true;
                    break;
                }
            } else {
                break;
            }
        }
        if (!z) {
            shortcutManager.requestPinShortcut(shortcutInfo, null);
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0020, code lost:
        if (r7 != null) goto L_0x0024;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeShortcut(int r4, java.lang.String r5, android.content.Intent r6, com.lody.virtual.client.core.VirtualCore.OnEmitShortcutListener r7) {
        /*
            r3 = this;
            r0 = 0
            com.lody.virtual.remote.InstalledAppInfo r1 = r3.getInstalledAppInfo(r5, r0)
            if (r1 != 0) goto L_0x0008
            return r0
        L_0x0008:
            android.content.pm.ApplicationInfo r1 = r1.getApplicationInfo(r4)
            android.content.Context r2 = r3.context
            android.content.pm.PackageManager r2 = r2.getPackageManager()
            java.lang.CharSequence r1 = r1.loadLabel(r2)     // Catch:{ Throwable -> 0x007b }
            java.lang.String r1 = r1.toString()     // Catch:{ Throwable -> 0x007b }
            if (r7 == 0) goto L_0x0023
            java.lang.String r7 = r7.getName(r1)
            if (r7 == 0) goto L_0x0023
            goto L_0x0024
        L_0x0023:
            r7 = r1
        L_0x0024:
            android.content.Intent r4 = r3.getLaunchIntent(r5, r4)
            if (r4 != 0) goto L_0x002b
            return r0
        L_0x002b:
            android.content.Intent r5 = new android.content.Intent
            r5.<init>()
            java.lang.String r1 = r3.getHostPkg()
            java.lang.String r2 = com.lody.virtual.client.env.Constants.SHORTCUT_PROXY_ACTIVITY_NAME
            r5.setClassName(r1, r2)
            java.lang.String r1 = "android.intent.category.DEFAULT"
            r5.addCategory(r1)
            if (r6 == 0) goto L_0x0049
            java.lang.String r1 = "_VA_|_splash_"
            java.lang.String r6 = r6.toUri(r0)
            r5.putExtra(r1, r6)
        L_0x0049:
            java.lang.String r6 = "_VA_|_intent_"
            r5.putExtra(r6, r4)
            java.lang.String r6 = "_VA_|_uri_"
            java.lang.String r4 = r4.toUri(r0)
            r5.putExtra(r6, r4)
            java.lang.String r4 = "_VA_|_user_id_"
            int r6 = com.lody.virtual.p007os.VUserHandle.myUserId()
            r5.putExtra(r4, r6)
            android.content.Intent r4 = new android.content.Intent
            r4.<init>()
            java.lang.String r6 = "android.intent.extra.shortcut.INTENT"
            r4.putExtra(r6, r5)
            java.lang.String r5 = "android.intent.extra.shortcut.NAME"
            r4.putExtra(r5, r7)
            java.lang.String r5 = "com.android.launcher.action.UNINSTALL_SHORTCUT"
            r4.setAction(r5)
            android.content.Context r5 = r3.context
            r5.sendBroadcast(r4)
            r4 = 1
            return r4
        L_0x007b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.core.VirtualCore.removeShortcut(int, java.lang.String, android.content.Intent, com.lody.virtual.client.core.VirtualCore$OnEmitShortcutListener):boolean");
    }

    public void setUiCallback(Intent intent, IUiCallback iUiCallback) {
        if (iUiCallback != null) {
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "_VA_|_ui_callback_", iUiCallback.asBinder());
            intent.putExtra("_VA_|_sender_", bundle);
        }
    }

    public static IUiCallback getUiCallback(Intent intent) {
        if (intent == null || !"android.intent.action.MAIN".equals(intent.getAction())) {
            return null;
        }
        try {
            Bundle bundleExtra = intent.getBundleExtra("_VA_|_sender_");
            if (bundleExtra != null) {
                return IUiCallback.Stub.asInterface(BundleCompat.getBinder(bundleExtra, "_VA_|_ui_callback_"));
            }
        } catch (Throwable unused) {
        }
        return null;
    }

    public InstalledAppInfo getInstalledAppInfo(String str, int i) {
        try {
            return getService().getInstalledAppInfo(str, i);
        } catch (RemoteException e) {
            return (InstalledAppInfo) VirtualRuntime.crash(e);
        }
    }

    public int getInstalledAppCount() {
        try {
            return getService().getInstalledAppCount();
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public boolean isStartup() {
        return this.isStartUp;
    }

    public boolean uninstallPackageAsUser(String str, int i) {
        try {
            return getService().uninstallPackageAsUser(str, i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public boolean uninstallPackage(String str) {
        try {
            return getService().uninstallPackage(str);
        } catch (RemoteException unused) {
            return false;
        }
    }

    public Resources getResources(String str) throws NotFoundException {
        InstalledAppInfo installedAppInfo = getInstalledAppInfo(str, 0);
        if (installedAppInfo != null) {
            AssetManager assetManager = (AssetManager) mirror.android.content.res.AssetManager.ctor.newInstance();
            mirror.android.content.res.AssetManager.addAssetPath.call(assetManager, installedAppInfo.apkPath);
            Resources resources = this.context.getResources();
            return new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
        }
        throw new NotFoundException(str);
    }

    public synchronized ActivityInfo resolveActivityInfo(Intent intent, int i) {
        ActivityInfo activityInfo;
        activityInfo = null;
        if (intent.getComponent() == null) {
            ResolveInfo resolveIntent = VPackageManager.get().resolveIntent(intent, intent.getType(), 0, i);
            if (!(resolveIntent == null || resolveIntent.activityInfo == null)) {
                activityInfo = resolveIntent.activityInfo;
                intent.setClassName(activityInfo.packageName, activityInfo.name);
            }
        } else {
            activityInfo = resolveActivityInfo(intent.getComponent(), i);
        }
        if (!(activityInfo == null || activityInfo.targetActivity == null)) {
            ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.targetActivity);
            activityInfo = VPackageManager.get().getActivityInfo(componentName, 0, i);
            intent.setComponent(componentName);
        }
        return activityInfo;
    }

    public ActivityInfo resolveActivityInfo(ComponentName componentName, int i) {
        return VPackageManager.get().getActivityInfo(componentName, 0, i);
    }

    public ServiceInfo resolveServiceInfo(Intent intent, int i) {
        ResolveInfo resolveService = VPackageManager.get().resolveService(intent, intent.getType(), 0, i);
        if (resolveService != null) {
            return resolveService.serviceInfo;
        }
        return null;
    }

    public void killApp(String str, int i) {
        VActivityManager.get().killAppByPkg(str, i);
    }

    public void killAllApps() {
        VActivityManager.get().killAllApps();
    }

    public List<InstalledAppInfo> getInstalledApps(int i) {
        try {
            return getService().getInstalledApps(i);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public List<InstalledAppInfo> getInstalledAppsAsUser(int i, int i2) {
        try {
            return getService().getInstalledAppsAsUser(i, i2);
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public void clearAppRequestListener() {
        try {
            getService().clearAppRequestListener();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void scanApps() {
        try {
            getService().scanApps();
        } catch (RemoteException unused) {
        }
    }

    public IAppRequestListener getAppRequestListener() {
        try {
            return getService().getAppRequestListener();
        } catch (RemoteException e) {
            return (IAppRequestListener) VirtualRuntime.crash(e);
        }
    }

    public void setAppRequestListener(final AppRequestListener appRequestListener) {
        try {
            getService().setAppRequestListener(new IAppRequestListener.Stub() {
                public void onRequestInstall(final String str) {
                    VirtualRuntime.getUIHandler().post(new Runnable() {
                        public void run() {
                            appRequestListener.onRequestInstall(str);
                        }
                    });
                }

                public void onRequestUninstall(final String str) {
                    VirtualRuntime.getUIHandler().post(new Runnable() {
                        public void run() {
                            appRequestListener.onRequestUninstall(str);
                        }
                    });
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isPackageLaunched(int i, String str) {
        try {
            return getService().isPackageLaunched(i, str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public void setPackageHidden(int i, String str, boolean z) {
        try {
            getService().setPackageHidden(i, str, z);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean installPackageAsUser(int i, String str) {
        try {
            return getService().installPackageAsUser(i, str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public boolean isAppInstalledAsUser(int i, String str) {
        try {
            return getService().isAppInstalledAsUser(i, str);
        } catch (RemoteException e) {
            return ((Boolean) VirtualRuntime.crash(e)).booleanValue();
        }
    }

    public int[] getPackageInstalledUsers(String str) {
        try {
            return getService().getPackageInstalledUsers(str);
        } catch (RemoteException e) {
            return (int[]) VirtualRuntime.crash(e);
        }
    }

    public void registerObserver(IPackageObserver iPackageObserver) {
        try {
            getService().registerObserver(iPackageObserver);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public void unregisterObserver(IPackageObserver iPackageObserver) {
        try {
            getService().unregisterObserver(iPackageObserver);
        } catch (RemoteException e) {
            VirtualRuntime.crash(e);
        }
    }

    public boolean isOutsideInstalled(String str) {
        boolean z = false;
        try {
            if (this.unHookPackageManager.getApplicationInfo(str, 0) != null) {
                z = true;
            }
            return z;
        } catch (NameNotFoundException unused) {
            return false;
        }
    }

    public int getSystemPid() {
        return this.systemPid;
    }
}
