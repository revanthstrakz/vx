package com.lody.virtual.client.hook.proxies.p005am;

import android.annotation.TargetApi;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.TaskDescription;
import android.app.Application;
import android.app.IServiceConnection;
import android.app.Notification;
import android.content.ComponentName;
import android.content.IIntentReceiver;
import android.content.IIntentReceiver.Stub;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.IInterface;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.TypedValue;
import com.lody.virtual.client.NativeEngine;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.badger.BadgerManager;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.client.env.SpecialComponentList;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import com.lody.virtual.client.hook.delegate.TaskDescriptionDelegate;
import com.lody.virtual.client.hook.providers.ProviderHook;
import com.lody.virtual.client.hook.secondary.ServiceConnectionDelegate;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.client.ipc.ActivityClientRecord;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.client.ipc.VNotificationManager;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.client.stub.ChooserActivity;
import com.lody.virtual.client.stub.StubPendingActivity;
import com.lody.virtual.client.stub.StubPendingReceiver;
import com.lody.virtual.client.stub.StubPendingService;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.compat.BuildCompat;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.BitmapUtils;
import com.lody.virtual.helper.utils.ComponentUtils;
import com.lody.virtual.helper.utils.DrawableUtils;
import com.lody.virtual.helper.utils.EncodeUtils;
import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.remote.AppTaskInfo;
import com.lody.virtual.server.interfaces.IAppRequestListener;
import com.microsoft.appcenter.ingestion.models.StartServiceLog;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.WeakHashMap;
import mirror.android.app.IActivityManager.ContentProviderHolder;
import mirror.android.app.LoadedApk.ReceiverDispatcher;
import mirror.android.app.LoadedApk.ReceiverDispatcher.InnerReceiver;
import mirror.android.content.ContentProviderHolderOreo;
import mirror.android.content.IIntentReceiverJB;
import mirror.android.content.p016pm.UserInfo;

/* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies */
class MethodProxies {

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$AddPackageDependency */
    static class AddPackageDependency extends MethodProxy {
        public String getMethodName() {
            return "addPackageDependency";
        }

        AddPackageDependency() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$BindIsolatedService */
    static class BindIsolatedService extends BindService {
        public String getMethodName() {
            return "bindIsolatedService";
        }

        BindIsolatedService() {
        }

        public boolean beforeCall(Object obj, Method method, Object... objArr) {
            MethodParameterUtils.replaceLastAppPkg(objArr);
            return super.beforeCall(obj, method, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$BindService */
    static class BindService extends MethodProxy {
        public String getMethodName() {
            return "bindService";
        }

        BindService() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IInterface iInterface = objArr[0];
            IBinder iBinder = (IBinder) objArr[1];
            Intent intent = (Intent) objArr[2];
            String str = (String) objArr[3];
            IServiceConnection iServiceConnection = objArr[4];
            int intValue = objArr[5].intValue();
            int myUserId = VUserHandle.myUserId();
            if (isServerProcess()) {
                myUserId = intent.getIntExtra("_VA_|_user_id_", -10000);
            }
            int i = myUserId;
            if (i == -10000) {
                return method.invoke(obj, objArr);
            }
            ServiceInfo resolveServiceInfo = VirtualCore.get().resolveServiceInfo(intent, i);
            if (resolveServiceInfo == null) {
                return method.invoke(obj, objArr);
            }
            if (VERSION.SDK_INT >= 21) {
                intent.setComponent(new ComponentName(resolveServiceInfo.packageName, resolveServiceInfo.name));
            }
            return Integer.valueOf(VActivityManager.get().bindService(iInterface.asBinder(), iBinder, intent, str, ServiceConnectionDelegate.getDelegate(iServiceConnection), intValue, i));
        }

        public boolean isEnable() {
            return isAppProcess() || isServerProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$BroadcastIntent */
    static class BroadcastIntent extends MethodProxy {
        public String getMethodName() {
            return "broadcastIntent";
        }

        BroadcastIntent() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            Intent intent = objArr[1];
            intent.setDataAndType(intent.getData(), objArr[2]);
            if (VirtualCore.get().getComponentDelegate() != null) {
                VirtualCore.get().getComponentDelegate().onSendBroadcast(intent);
            }
            Intent handleIntent = handleIntent(intent);
            if (handleIntent == null) {
                return Integer.valueOf(0);
            }
            objArr[1] = handleIntent;
            if ((objArr[7] instanceof String) || (objArr[7] instanceof String[])) {
                objArr[7] = null;
            }
            return method.invoke(obj, objArr);
        }

        private Intent handleIntent(Intent intent) {
            String action = intent.getAction();
            Intent intent2 = null;
            if ("android.intent.action.CREATE_SHORTCUT".equals(action) || "com.android.launcher.action.INSTALL_SHORTCUT".equals(action)) {
                if (VASettings.ENABLE_INNER_SHORTCUT) {
                    intent2 = handleInstallShortcutIntent(intent);
                }
                return intent2;
            } else if ("com.android.launcher.action.UNINSTALL_SHORTCUT".equals(action)) {
                handleUninstallShortcutIntent(intent);
                return intent;
            } else if (BadgerManager.handleBadger(intent)) {
                return null;
            } else {
                if ("android.intent.action.MEDIA_SCANNER_SCAN_FILE".equals(action)) {
                    return handleMediaScannerIntent(intent);
                }
                return ComponentUtils.redirectBroadcastIntent(intent, VUserHandle.myUserId());
            }
        }

        private Intent handleMediaScannerIntent(Intent intent) {
            if (intent == null) {
                return null;
            }
            Uri data = intent.getData();
            if (data == null) {
                return intent;
            }
            if (!"file".equalsIgnoreCase(data.getScheme())) {
                return intent;
            }
            String path = data.getPath();
            if (path == null) {
                return intent;
            }
            File file = new File(NativeEngine.getRedirectedPath(path));
            if (!file.exists()) {
                return intent;
            }
            intent.setData(Uri.fromFile(file));
            return intent;
        }

        private Intent handleInstallShortcutIntent(Intent intent) {
            Intent intent2 = (Intent) intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
            if (intent2 != null) {
                ComponentName resolveActivity = intent2.resolveActivity(VirtualCore.getPM());
                if (resolveActivity != null) {
                    String packageName = resolveActivity.getPackageName();
                    Intent intent3 = new Intent();
                    intent3.setClassName(getHostPkg(), Constants.SHORTCUT_PROXY_ACTIVITY_NAME);
                    intent3.addCategory("android.intent.category.DEFAULT");
                    intent3.putExtra("_VA_|_intent_", intent2);
                    intent3.putExtra("_VA_|_uri_", intent2.toUri(0));
                    intent3.putExtra("_VA_|_user_id_", VUserHandle.myUserId());
                    intent.removeExtra("android.intent.extra.shortcut.INTENT");
                    intent.putExtra("android.intent.extra.shortcut.INTENT", intent3);
                    ShortcutIconResource shortcutIconResource = (ShortcutIconResource) intent.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
                    if (shortcutIconResource != null && !TextUtils.equals(shortcutIconResource.packageName, getHostPkg())) {
                        try {
                            Resources resources = VirtualCore.get().getResources(packageName);
                            int identifier = resources.getIdentifier(shortcutIconResource.resourceName, "drawable", packageName);
                            if (identifier > 0) {
                                Bitmap drawableToBitmap = BitmapUtils.drawableToBitmap(resources.getDrawable(identifier));
                                if (drawableToBitmap != null) {
                                    intent.removeExtra("android.intent.extra.shortcut.ICON_RESOURCE");
                                    intent.putExtra("android.intent.extra.shortcut.ICON", drawableToBitmap);
                                }
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                }
            }
            return intent;
        }

        private void handleUninstallShortcutIntent(Intent intent) {
            Intent intent2 = (Intent) intent.getParcelableExtra("android.intent.extra.shortcut.INTENT");
            if (intent2 != null && intent2.resolveActivity(getPM()) != null) {
                Intent intent3 = new Intent();
                intent3.putExtra("_VA_|_uri_", intent2.toUri(0));
                intent3.setClassName(getHostPkg(), Constants.SHORTCUT_PROXY_ACTIVITY_NAME);
                intent3.removeExtra("android.intent.extra.shortcut.INTENT");
                intent.putExtra("android.intent.extra.shortcut.INTENT", intent3);
            }
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$CheckGrantUriPermission */
    static class CheckGrantUriPermission extends MethodProxy {
        public String getMethodName() {
            return "checkGrantUriPermission";
        }

        CheckGrantUriPermission() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$CheckPermission */
    static class CheckPermission extends MethodProxy {
        public String getMethodName() {
            return "checkPermission";
        }

        CheckPermission() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            if (SpecialComponentList.isWhitePermission(str)) {
                return Integer.valueOf(0);
            }
            if (str.startsWith("com.google")) {
                return Integer.valueOf(0);
            }
            objArr[objArr.length - 1] = Integer.valueOf(getRealUid());
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$CrashApplication */
    static class CrashApplication extends MethodProxy {
        public String getMethodName() {
            return "crashApplication";
        }

        CrashApplication() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$FinishActivity */
    static class FinishActivity extends MethodProxy {
        public String getMethodName() {
            return "finishActivity";
        }

        FinishActivity() {
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            IBinder iBinder = objArr[0];
            ActivityClientRecord activityRecord = VActivityManager.get().getActivityRecord(iBinder);
            if (!(VActivityManager.get().onActivityDestroy(iBinder) || activityRecord == null || activityRecord.activity == null || activityRecord.info.getThemeResource() == 0)) {
                try {
                    TypedValue typedValue = new TypedValue();
                    Theme newTheme = activityRecord.activity.getResources().newTheme();
                    newTheme.applyStyle(activityRecord.info.getThemeResource(), true);
                    if (newTheme.resolveAttribute(16842926, typedValue, true)) {
                        TypedArray obtainStyledAttributes = newTheme.obtainStyledAttributes(typedValue.data, new int[]{16842938, 16842939});
                        activityRecord.activity.overridePendingTransition(obtainStyledAttributes.getResourceId(0, 0), obtainStyledAttributes.getResourceId(1, 0));
                        obtainStyledAttributes.recycle();
                    }
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
            return super.afterCall(obj, method, objArr, obj2);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$ForceStopPackage */
    static class ForceStopPackage extends MethodProxy {
        public String getMethodName() {
            return "forceStopPackage";
        }

        ForceStopPackage() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            VActivityManager.get().killAppByPkg(objArr[0], VUserHandle.myUserId());
            return Integer.valueOf(0);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetActivityClassForToken */
    static class GetActivityClassForToken extends MethodProxy {
        public String getMethodName() {
            return "getActivityClassForToken";
        }

        GetActivityClassForToken() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return VActivityManager.get().getActivityForToken(objArr[0]);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetCallingActivity */
    static class GetCallingActivity extends MethodProxy {
        public String getMethodName() {
            return "getCallingActivity";
        }

        GetCallingActivity() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return VActivityManager.get().getCallingActivity(objArr[0]);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetCallingPackage */
    static class GetCallingPackage extends MethodProxy {
        public String getMethodName() {
            return "getCallingPackage";
        }

        GetCallingPackage() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return VActivityManager.get().getCallingPackage(objArr[0]);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetContentProvider */
    static class GetContentProvider extends MethodProxy {
        public String getMethodName() {
            return "getContentProvider";
        }

        GetContentProvider() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            int providerNameIndex = getProviderNameIndex();
            String str = objArr[providerNameIndex];
            int myUserId = VUserHandle.myUserId();
            ProviderInfo resolveContentProvider = VPackageManager.get().resolveContentProvider(str, 0, myUserId);
            if (resolveContentProvider == null || !resolveContentProvider.enabled || !isAppPkg(resolveContentProvider.packageName)) {
                if (BuildCompat.isQ()) {
                    int packageIndex = getPackageIndex();
                    if (packageIndex > 0 && (objArr[packageIndex] instanceof String)) {
                        objArr[packageIndex] = getHostPkg();
                    }
                }
                Object invoke = method.invoke(obj, objArr);
                if (invoke == null) {
                    return null;
                }
                if (BuildCompat.isOreo()) {
                    IInterface iInterface = (IInterface) ContentProviderHolderOreo.provider.get(invoke);
                    ProviderInfo providerInfo = (ProviderInfo) ContentProviderHolderOreo.info.get(invoke);
                    if (iInterface != null) {
                        iInterface = ProviderHook.createProxy(true, providerInfo.authority, iInterface);
                    }
                    ContentProviderHolderOreo.provider.set(invoke, iInterface);
                } else {
                    IInterface iInterface2 = (IInterface) ContentProviderHolder.provider.get(invoke);
                    ProviderInfo providerInfo2 = (ProviderInfo) ContentProviderHolder.info.get(invoke);
                    if (iInterface2 != null) {
                        iInterface2 = ProviderHook.createProxy(true, providerInfo2.authority, iInterface2);
                    }
                    ContentProviderHolder.provider.set(invoke, iInterface2);
                }
                return invoke;
            }
            int initProcess = VActivityManager.get().initProcess(resolveContentProvider.packageName, resolveContentProvider.processName, myUserId);
            if (initProcess == -1) {
                return null;
            }
            objArr[providerNameIndex] = VASettings.getStubAuthority(initProcess);
            Object invoke2 = method.invoke(obj, objArr);
            if (invoke2 == null) {
                return null;
            }
            if (BuildCompat.isOreo()) {
                IInterface iInterface3 = (IInterface) ContentProviderHolderOreo.provider.get(invoke2);
                if (iInterface3 != null) {
                    iInterface3 = VActivityManager.get().acquireProviderClient(myUserId, resolveContentProvider);
                }
                ContentProviderHolderOreo.provider.set(invoke2, iInterface3);
                ContentProviderHolderOreo.info.set(invoke2, resolveContentProvider);
            } else {
                IInterface iInterface4 = (IInterface) ContentProviderHolder.provider.get(invoke2);
                if (iInterface4 != null) {
                    iInterface4 = VActivityManager.get().acquireProviderClient(myUserId, resolveContentProvider);
                }
                ContentProviderHolder.provider.set(invoke2, iInterface4);
                ContentProviderHolder.info.set(invoke2, resolveContentProvider);
            }
            return invoke2;
        }

        public int getProviderNameIndex() {
            return BuildCompat.isQ() ? 2 : 1;
        }

        public int getPackageIndex() {
            return BuildCompat.isQ() ? 1 : -1;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetContentProviderExternal */
    static class GetContentProviderExternal extends GetContentProvider {
        public String getMethodName() {
            return "getContentProviderExternal";
        }

        public int getPackageIndex() {
            return -1;
        }

        public int getProviderNameIndex() {
            return 0;
        }

        GetContentProviderExternal() {
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetCurrentUser */
    static class GetCurrentUser extends MethodProxy {
        public String getMethodName() {
            return "getCurrentUser";
        }

        GetCurrentUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            try {
                return UserInfo.ctor.newInstance(Integer.valueOf(0), ServiceManagerNative.USER, Integer.valueOf(1));
            } catch (Throwable th) {
                th.printStackTrace();
                return null;
            }
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetIntentForIntentSender */
    static class GetIntentForIntentSender extends MethodProxy {
        public String getMethodName() {
            return "getIntentForIntentSender";
        }

        GetIntentForIntentSender() {
        }

        public Object afterCall(Object obj, Method method, Object[] objArr, Object obj2) throws Throwable {
            Intent intent = (Intent) super.afterCall(obj, method, objArr, obj2);
            return (intent == null || !intent.hasExtra("_VA_|_intent_")) ? intent : intent.getParcelableExtra("_VA_|_intent_");
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetIntentSender */
    static class GetIntentSender extends MethodProxy {
        public String getMethodName() {
            return "getIntentSender";
        }

        GetIntentSender() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[1];
            String[] strArr = objArr[6];
            int intValue = objArr[0].intValue();
            int intValue2 = objArr[7].intValue();
            if (objArr[5] instanceof Intent[]) {
                Intent[] intentArr = objArr[5];
                for (int i = 0; i < intentArr.length; i++) {
                    Intent intent = intentArr[i];
                    if (strArr != null && i < strArr.length) {
                        intent.setDataAndType(intent.getData(), strArr[i]);
                    }
                    Intent redirectIntentSender = redirectIntentSender(intValue, str, intent);
                    if (redirectIntentSender != null) {
                        intentArr[i] = redirectIntentSender;
                    }
                }
            }
            objArr[7] = Integer.valueOf(intValue2);
            objArr[1] = getHostPkg();
            if (objArr[objArr.length - 1] instanceof Integer) {
                objArr[objArr.length - 1] = Integer.valueOf(0);
            }
            IInterface iInterface = (IInterface) method.invoke(obj, objArr);
            if (!(iInterface == null || str == null)) {
                VActivityManager.get().addPendingIntent(iInterface.asBinder(), str);
            }
            return iInterface;
        }

        private Intent redirectIntentSender(int i, String str, Intent intent) {
            Intent cloneFilter = intent.cloneFilter();
            if (i != 4) {
                switch (i) {
                    case 1:
                        cloneFilter.setClass(getHostContext(), StubPendingReceiver.class);
                        break;
                    case 2:
                        if (VirtualCore.get().resolveActivityInfo(intent, VUserHandle.myUserId()) != null) {
                            cloneFilter.setClass(getHostContext(), StubPendingActivity.class);
                            cloneFilter.addFlags(268435456);
                            break;
                        }
                        break;
                    default:
                        return null;
                }
            } else if (VirtualCore.get().resolveServiceInfo(intent, VUserHandle.myUserId()) != null) {
                cloneFilter.setClass(getHostContext(), StubPendingService.class);
            }
            cloneFilter.putExtra("_VA_|_user_id_", VUserHandle.myUserId());
            cloneFilter.putExtra("_VA_|_intent_", intent);
            cloneFilter.putExtra("_VA_|_creator_", str);
            cloneFilter.putExtra("_VA_|_from_inner_", true);
            return cloneFilter;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetPackageAskScreenCompat */
    static class GetPackageAskScreenCompat extends MethodProxy {
        public String getMethodName() {
            return "getPackageAskScreenCompat";
        }

        GetPackageAskScreenCompat() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (VERSION.SDK_INT >= 15 && objArr.length > 0 && (objArr[0] instanceof String)) {
                objArr[0] = getHostPkg();
            }
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetPackageForIntentSender */
    static class GetPackageForIntentSender extends MethodProxy {
        public String getMethodName() {
            return "getPackageForIntentSender";
        }

        GetPackageForIntentSender() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IInterface iInterface = objArr[0];
            if (iInterface != null) {
                String packageForIntentSender = VActivityManager.get().getPackageForIntentSender(iInterface.asBinder());
                if (packageForIntentSender != null) {
                    return packageForIntentSender;
                }
            }
            return super.call(obj, method, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetPackageForToken */
    static class GetPackageForToken extends MethodProxy {
        public String getMethodName() {
            return "getPackageForToken";
        }

        GetPackageForToken() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String packageForToken = VActivityManager.get().getPackageForToken(objArr[0]);
            if (packageForToken != null) {
                return packageForToken;
            }
            return super.call(obj, method, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetPackageProcessState */
    static class GetPackageProcessState extends ReplaceLastPkgMethodProxy {
        public GetPackageProcessState() {
            super("getPackageProcessState");
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetPersistedUriPermissions */
    static class GetPersistedUriPermissions extends MethodProxy {
        public String getMethodName() {
            return "getPersistedUriPermissions";
        }

        GetPersistedUriPermissions() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetRunningAppProcesses */
    static class GetRunningAppProcesses extends MethodProxy {
        public String getMethodName() {
            return "getRunningAppProcesses";
        }

        GetRunningAppProcesses() {
        }

        public synchronized Object call(Object obj, Method method, Object... objArr) throws Throwable {
            List<RunningAppProcessInfo> list;
            list = (List) method.invoke(obj, objArr);
            if (list != null) {
                for (RunningAppProcessInfo runningAppProcessInfo : list) {
                    if (VActivityManager.get().isAppPid(runningAppProcessInfo.pid)) {
                        List processPkgList = VActivityManager.get().getProcessPkgList(runningAppProcessInfo.pid);
                        String appProcessName = VActivityManager.get().getAppProcessName(runningAppProcessInfo.pid);
                        if (appProcessName != null) {
                            runningAppProcessInfo.processName = appProcessName;
                        }
                        runningAppProcessInfo.pkgList = (String[]) processPkgList.toArray(new String[processPkgList.size()]);
                        runningAppProcessInfo.uid = VUserHandle.getAppId(VActivityManager.get().getUidByPid(runningAppProcessInfo.pid));
                    }
                }
            }
            return list;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetServices */
    static class GetServices extends MethodProxy {
        public String getMethodName() {
            return "getServices";
        }

        GetServices() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return VActivityManager.get().getServices(objArr[0].intValue(), objArr[1].intValue()).getList();
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GetTasks */
    static class GetTasks extends MethodProxy {
        public String getMethodName() {
            return "getTasks";
        }

        GetTasks() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            List<RunningTaskInfo> list = (List) method.invoke(obj, objArr);
            for (RunningTaskInfo runningTaskInfo : list) {
                AppTaskInfo taskInfo = VActivityManager.get().getTaskInfo(runningTaskInfo.id);
                if (taskInfo != null) {
                    runningTaskInfo.topActivity = taskInfo.topActivity;
                    runningTaskInfo.baseActivity = taskInfo.baseActivity;
                }
            }
            return list;
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$GrantUriPermissionFromOwner */
    static class GrantUriPermissionFromOwner extends MethodProxy {
        public String getMethodName() {
            return "grantUriPermissionFromOwner";
        }

        GrantUriPermissionFromOwner() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$HandleIncomingUser */
    static class HandleIncomingUser extends MethodProxy {
        public String getMethodName() {
            return "handleIncomingUser";
        }

        HandleIncomingUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            int length = objArr.length - 1;
            if (objArr[length] instanceof String) {
                objArr[length] = getHostPkg();
            }
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$KillApplicationProcess */
    static class KillApplicationProcess extends MethodProxy {
        public String getMethodName() {
            return "killApplicationProcess";
        }

        KillApplicationProcess() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (objArr.length <= 1 || !(objArr[0] instanceof String) || !(objArr[1] instanceof Integer)) {
                return method.invoke(obj, objArr);
            }
            VActivityManager.get().killApplicationProcess(objArr[0], objArr[1].intValue());
            return Integer.valueOf(0);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$OverridePendingTransition */
    static class OverridePendingTransition extends MethodProxy {
        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return null;
        }

        public String getMethodName() {
            return "overridePendingTransition";
        }

        OverridePendingTransition() {
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$PeekService */
    static class PeekService extends MethodProxy {
        public String getMethodName() {
            return "peekService";
        }

        PeekService() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceLastAppPkg(objArr);
            return VActivityManager.get().peekService(objArr[0], objArr[1]);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$PublishContentProviders */
    static class PublishContentProviders extends MethodProxy {
        public String getMethodName() {
            return "publishContentProviders";
        }

        PublishContentProviders() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$PublishService */
    static class PublishService extends MethodProxy {
        public String getMethodName() {
            return "publishService";
        }

        PublishService() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IBinder iBinder = objArr[0];
            if (!VActivityManager.get().isVAServiceToken(iBinder)) {
                return method.invoke(obj, objArr);
            }
            VActivityManager.get().publishService(iBinder, objArr[1], objArr[2]);
            return Integer.valueOf(0);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$RegisterReceiver */
    static class RegisterReceiver extends MethodProxy {
        private static final int IDX_IIntentReceiver = (VERSION.SDK_INT >= 15 ? 2 : 1);
        private static final int IDX_IntentFilter;
        private static final int IDX_RequiredPermission = (VERSION.SDK_INT >= 15 ? 4 : 3);
        /* access modifiers changed from: private */
        public WeakHashMap<IBinder, IIntentReceiver> mProxyIIntentReceivers = new WeakHashMap<>();

        /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$RegisterReceiver$IIntentReceiverProxy */
        private static class IIntentReceiverProxy extends Stub {
            IInterface mOld;

            IIntentReceiverProxy(IInterface iInterface) {
                this.mOld = iInterface;
            }

            public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2, int i2) throws RemoteException {
                Intent intent2 = intent;
                if (accept(intent)) {
                    if (intent.hasExtra("_VA_|_intent_")) {
                        intent2 = (Intent) intent.getParcelableExtra("_VA_|_intent_");
                    }
                    SpecialComponentList.unprotectIntent(intent2);
                    if (VERSION.SDK_INT > 16) {
                        IIntentReceiverJB.performReceive.call(this.mOld, intent2, Integer.valueOf(i), str, bundle, Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(i2));
                    } else {
                        mirror.android.content.IIntentReceiver.performReceive.call(this.mOld, intent2, Integer.valueOf(i), str, bundle, Boolean.valueOf(z), Boolean.valueOf(z2));
                    }
                }
            }

            private boolean accept(Intent intent) {
                int intExtra = intent.getIntExtra("_VA_|_uid_", -1);
                boolean z = false;
                if (intExtra != -1) {
                    if (VClientImpl.get().getVUid() == intExtra) {
                        z = true;
                    }
                    return z;
                }
                int intExtra2 = intent.getIntExtra("_VA_|_user_id_", -1);
                if (intExtra2 == -1 || intExtra2 == VUserHandle.myUserId()) {
                    z = true;
                }
                return z;
            }

            public void performReceive(Intent intent, int i, String str, Bundle bundle, boolean z, boolean z2) throws RemoteException {
                performReceive(intent, i, str, bundle, z, z2, 0);
            }
        }

        public String getMethodName() {
            return "registerReceiver";
        }

        RegisterReceiver() {
        }

        static {
            int i = 2;
            if (VERSION.SDK_INT >= 15) {
                i = 3;
            }
            IDX_IntentFilter = i;
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            objArr[IDX_RequiredPermission] = null;
            SpecialComponentList.protectIntentFilter(objArr[IDX_IntentFilter]);
            if (objArr.length > IDX_IIntentReceiver && IIntentReceiver.class.isInstance(objArr[IDX_IIntentReceiver])) {
                IInterface iInterface = objArr[IDX_IIntentReceiver];
                if (!IIntentReceiverProxy.class.isInstance(iInterface)) {
                    final IBinder asBinder = iInterface.asBinder();
                    if (asBinder != null) {
                        asBinder.linkToDeath(new DeathRecipient() {
                            public void binderDied() {
                                asBinder.unlinkToDeath(this, 0);
                                RegisterReceiver.this.mProxyIIntentReceivers.remove(asBinder);
                            }
                        }, 0);
                        Object obj2 = (IIntentReceiver) this.mProxyIIntentReceivers.get(asBinder);
                        if (obj2 == null) {
                            obj2 = new IIntentReceiverProxy(iInterface);
                            this.mProxyIIntentReceivers.put(asBinder, obj2);
                        }
                        WeakReference weakReference = (WeakReference) InnerReceiver.mDispatcher.get(iInterface);
                        if (weakReference != null) {
                            ReceiverDispatcher.mIIntentReceiver.set(weakReference.get(), obj2);
                            objArr[IDX_IIntentReceiver] = obj2;
                        }
                    }
                }
            }
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$ServiceDoneExecuting */
    static class ServiceDoneExecuting extends MethodProxy {
        public String getMethodName() {
            return "serviceDoneExecuting";
        }

        ServiceDoneExecuting() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IBinder iBinder = objArr[0];
            if (!VActivityManager.get().isVAServiceToken(iBinder)) {
                return method.invoke(obj, objArr);
            }
            VActivityManager.get().serviceDoneExecuting(iBinder, objArr[1].intValue(), objArr[2].intValue(), objArr[3].intValue());
            return Integer.valueOf(0);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$SetPackageAskScreenCompat */
    static class SetPackageAskScreenCompat extends MethodProxy {
        public String getMethodName() {
            return "setPackageAskScreenCompat";
        }

        SetPackageAskScreenCompat() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (VERSION.SDK_INT >= 15 && objArr.length > 0 && (objArr[0] instanceof String)) {
                objArr[0] = getHostPkg();
            }
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$SetServiceForeground */
    static class SetServiceForeground extends MethodProxy {
        public String getMethodName() {
            return "setServiceForeground";
        }

        SetServiceForeground() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            boolean z;
            boolean z2;
            ComponentName componentName = (ComponentName) objArr[0];
            IBinder iBinder = (IBinder) objArr[1];
            int intValue = objArr[2].intValue();
            Notification notification = (Notification) objArr[3];
            if (objArr[4] instanceof Boolean) {
                z2 = objArr[4].booleanValue();
            } else if (VERSION.SDK_INT < 24 || !(objArr[4] instanceof Integer)) {
                String simpleName = getClass().getSimpleName();
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown flag : ");
                sb.append(objArr[4]);
                VLog.m87e(simpleName, sb.toString(), new Object[0]);
                z = false;
                VNotificationManager.get().dealNotification(intValue, notification, getAppPkg());
                if (notification != null && VERSION.SDK_INT >= 23 && (Build.BRAND.equalsIgnoreCase("samsung") || Build.MANUFACTURER.equalsIgnoreCase("samsung"))) {
                    notification.icon = getHostContext().getApplicationInfo().icon;
                    Reflect.m80on((Object) notification).call("setSmallIcon", Icon.createWithResource(getHostPkg(), notification.icon));
                }
                VActivityManager.get().setServiceForeground(componentName, iBinder, intValue, notification, z);
                return Integer.valueOf(0);
            } else {
                z2 = (objArr[4].intValue() & 1) != 0;
            }
            z = z2;
            VNotificationManager.get().dealNotification(intValue, notification, getAppPkg());
            notification.icon = getHostContext().getApplicationInfo().icon;
            Reflect.m80on((Object) notification).call("setSmallIcon", Icon.createWithResource(getHostPkg(), notification.icon));
            VActivityManager.get().setServiceForeground(componentName, iBinder, intValue, notification, z);
            return Integer.valueOf(0);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    @TargetApi(21)
    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$SetTaskDescription */
    static class SetTaskDescription extends MethodProxy {
        public String getMethodName() {
            return "setTaskDescription";
        }

        SetTaskDescription() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            TaskDescription taskDescription = (TaskDescription) objArr[1];
            String label = taskDescription.getLabel();
            Bitmap icon = taskDescription.getIcon();
            if (label == null || icon == null) {
                Application currentApplication = VClientImpl.get().getCurrentApplication();
                if (currentApplication != null) {
                    if (label == null) {
                        try {
                            label = currentApplication.getApplicationInfo().loadLabel(currentApplication.getPackageManager()).toString();
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                    }
                    if (icon == null) {
                        Drawable loadIcon = currentApplication.getApplicationInfo().loadIcon(currentApplication.getPackageManager());
                        if (loadIcon != null) {
                            icon = DrawableUtils.drawableToBitMap(loadIcon);
                        }
                    }
                    taskDescription = new TaskDescription(label, icon, taskDescription.getPrimaryColor());
                }
            }
            TaskDescriptionDelegate taskDescriptionDelegate = VirtualCore.get().getTaskDescriptionDelegate();
            if (taskDescriptionDelegate != null) {
                taskDescription = taskDescriptionDelegate.getTaskDescription(taskDescription);
            }
            objArr[1] = taskDescription;
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartActivities */
    static class StartActivities extends MethodProxy {
        public String getMethodName() {
            return "startActivities";
        }

        StartActivities() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            Intent[] intentArr = (Intent[]) ArrayUtils.getFirst(objArr, Intent[].class);
            String[] strArr = (String[]) ArrayUtils.getFirst(objArr, String[].class);
            int indexOfObject = ArrayUtils.indexOfObject(objArr, IBinder.class, 2);
            return Integer.valueOf(VActivityManager.get().startActivities(intentArr, strArr, indexOfObject != -1 ? objArr[indexOfObject] : null, (Bundle) ArrayUtils.getFirst(objArr, Bundle.class), VUserHandle.myUserId()));
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartActivity */
    static class StartActivity extends MethodProxy {
        private static final String SCHEME_CONTENT = "content";
        private static final String SCHEME_FILE = "file";
        private static final String SCHEME_PACKAGE = "package";

        public String getMethodName() {
            return "startActivity";
        }

        StartActivity() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            int i;
            Object obj2 = obj;
            Method method2 = method;
            Object[] objArr2 = objArr;
            int indexOfObject = ArrayUtils.indexOfObject(objArr2, Intent.class, 1);
            if (indexOfObject < 0) {
                return Integer.valueOf(-1);
            }
            int indexOfObject2 = ArrayUtils.indexOfObject(objArr2, IBinder.class, 2);
            Intent intent = (Intent) objArr2[indexOfObject];
            intent.setDataAndType(intent.getData(), (String) objArr2[indexOfObject + 1]);
            String str = null;
            IBinder iBinder = indexOfObject2 >= 0 ? (IBinder) objArr2[indexOfObject2] : null;
            int myUserId = VUserHandle.myUserId();
            if (ComponentUtils.isStubComponent(intent)) {
                return method2.invoke(obj2, objArr2);
            }
            if ("android.intent.action.INSTALL_PACKAGE".equals(intent.getAction()) || ("android.intent.action.VIEW".equals(intent.getAction()) && "application/vnd.android.package-archive".equals(intent.getType()))) {
                if (handleInstallRequest(intent)) {
                    return Integer.valueOf(0);
                }
            } else if (("android.intent.action.UNINSTALL_PACKAGE".equals(intent.getAction()) || "android.intent.action.DELETE".equals(intent.getAction())) && "package".equals(intent.getScheme())) {
                if (handleUninstallRequest(intent)) {
                    return Integer.valueOf(0);
                }
            } else if ("android.media.action.IMAGE_CAPTURE".equals(intent.getAction()) || "android.media.action.VIDEO_CAPTURE".equals(intent.getAction()) || "android.media.action.IMAGE_CAPTURE_SECURE".equals(intent.getAction())) {
                handleMediaCaptureRequest(intent);
            }
            Bundle bundle = (Bundle) ArrayUtils.getFirst(objArr2, Bundle.class);
            if (iBinder != null) {
                str = (String) objArr2[indexOfObject2 + 1];
                i = ((Integer) objArr2[indexOfObject2 + 2]).intValue();
            } else {
                i = 0;
            }
            if (ChooserActivity.check(intent)) {
                intent.setComponent(new ComponentName(getHostContext(), ChooserActivity.class));
                intent.putExtra(Constants.EXTRA_USER_HANDLE, myUserId);
                intent.putExtra(ChooserActivity.EXTRA_DATA, bundle);
                intent.putExtra(ChooserActivity.EXTRA_WHO, str);
                intent.putExtra(ChooserActivity.EXTRA_REQUEST_CODE, i);
                return method2.invoke(obj2, objArr2);
            }
            if (VERSION.SDK_INT >= 18) {
                objArr2[indexOfObject - 1] = getHostPkg();
            }
            if (!(intent.getScheme() == null || !intent.getScheme().equals("package") || intent.getData() == null || intent.getAction() == null || !intent.getAction().startsWith("android.settings."))) {
                StringBuilder sb = new StringBuilder();
                sb.append("package:");
                sb.append(getHostPkg());
                intent.setData(Uri.parse(sb.toString()));
            }
            ActivityInfo resolveActivityInfo = VirtualCore.get().resolveActivityInfo(intent, myUserId);
            if (resolveActivityInfo == null) {
                VLog.m87e("VActivityManager", "Unable to resolve activityInfo : %s", intent);
                if (intent.getPackage() == null || !isAppPkg(intent.getPackage())) {
                    return method2.invoke(obj2, objArr2);
                }
                return Integer.valueOf(-1);
            }
            int startActivity = VActivityManager.get().startActivity(intent, resolveActivityInfo, iBinder, bundle, str, i, VUserHandle.myUserId());
            if (!(startActivity == 0 || iBinder == null || i <= 0)) {
                VActivityManager.get().sendActivityResult(iBinder, str, i);
            }
            if (iBinder != null) {
                ActivityClientRecord activityRecord = VActivityManager.get().getActivityRecord(iBinder);
                if (!(activityRecord == null || activityRecord.activity == null)) {
                    try {
                        TypedValue typedValue = new TypedValue();
                        Theme newTheme = activityRecord.activity.getResources().newTheme();
                        newTheme.applyStyle(resolveActivityInfo.getThemeResource(), true);
                        if (newTheme.resolveAttribute(16842926, typedValue, true)) {
                            TypedArray obtainStyledAttributes = newTheme.obtainStyledAttributes(typedValue.data, new int[]{16842936, 16842937});
                            activityRecord.activity.overridePendingTransition(obtainStyledAttributes.getResourceId(0, 0), obtainStyledAttributes.getResourceId(1, 0));
                            obtainStyledAttributes.recycle();
                        }
                    } catch (Throwable unused) {
                    }
                }
            }
            return Integer.valueOf(startActivity);
        }

        private boolean handleInstallRequest(Intent intent) {
            IAppRequestListener appRequestListener = VirtualCore.get().getAppRequestListener();
            if (appRequestListener != null) {
                try {
                    appRequestListener.onRequestInstall(FileUtils.getFileFromUri(getHostContext(), intent.getData()));
                    return true;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        private boolean handleUninstallRequest(Intent intent) {
            IAppRequestListener appRequestListener = VirtualCore.get().getAppRequestListener();
            if (appRequestListener != null) {
                Uri data = intent.getData();
                if ("package".equals(data.getScheme())) {
                    try {
                        appRequestListener.onRequestUninstall(data.getSchemeSpecificPart());
                        return true;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        private void handleMediaCaptureRequest(Intent intent) {
            Uri uri = (Uri) intent.getParcelableExtra("output");
            if (uri != null && SCHEME_FILE.equals(uri.getScheme())) {
                String redirectedPath = NativeEngine.getRedirectedPath(uri.getPath());
                if (redirectedPath != null) {
                    intent.putExtra("output", Uri.fromFile(new File(redirectedPath)));
                }
            }
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartActivityAndWait */
    static class StartActivityAndWait extends StartActivity {
        public String getMethodName() {
            return "startActivityAndWait";
        }

        StartActivityAndWait() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return super.call(obj, method, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartActivityAsCaller */
    static class StartActivityAsCaller extends StartActivity {
        public String getMethodName() {
            return "startActivityAsCaller";
        }

        StartActivityAsCaller() {
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartActivityAsUser */
    static class StartActivityAsUser extends StartActivity {
        public String getMethodName() {
            return "startActivityAsUser";
        }

        StartActivityAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return super.call(obj, method, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartActivityIntentSender */
    static class StartActivityIntentSender extends MethodProxy {
        public String getMethodName() {
            return "startActivityIntentSender";
        }

        StartActivityIntentSender() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return super.call(obj, method, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartActivityWithConfig */
    static class StartActivityWithConfig extends StartActivity {
        public String getMethodName() {
            return "startActivityWithConfig";
        }

        StartActivityWithConfig() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return super.call(obj, method, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartNextMatchingActivity */
    static class StartNextMatchingActivity extends StartActivity {
        public String getMethodName() {
            return "startNextMatchingActivity";
        }

        StartNextMatchingActivity() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Boolean.valueOf(false);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartService */
    static class StartService extends MethodProxy {
        public String getMethodName() {
            return StartServiceLog.TYPE;
        }

        StartService() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IInterface iInterface = objArr[0];
            Intent intent = objArr[1];
            String str = objArr[2];
            if (intent.getComponent() != null && getHostPkg().equals(intent.getComponent().getPackageName())) {
                return method.invoke(obj, objArr);
            }
            int myUserId = VUserHandle.myUserId();
            if (intent.getBooleanExtra("_VA_|_from_inner_", false)) {
                myUserId = intent.getIntExtra("_VA_|_user_id_", myUserId);
                intent = (Intent) intent.getParcelableExtra("_VA_|_intent_");
            } else if (isServerProcess()) {
                myUserId = intent.getIntExtra("_VA_|_user_id_", -10000);
            }
            intent.setDataAndType(intent.getData(), str);
            if (VirtualCore.get().resolveServiceInfo(intent, VUserHandle.myUserId()) == null) {
                return method.invoke(obj, objArr);
            }
            if (isFiltered(intent)) {
                return intent.getComponent();
            }
            return VActivityManager.get().startService(iInterface, intent, str, myUserId);
        }

        public boolean isEnable() {
            return isAppProcess() || isServerProcess();
        }

        private boolean isFiltered(Intent intent) {
            return (intent == null || intent.getComponent() == null || !EncodeUtils.decode("Y29tLnRlbmNlbnQudGlua2VyLmxpYi5zZXJ2aWMuVGlua2VyUGF0Y2hTZXJ2aWNl").equals(intent.getComponent().getClassName())) ? false : true;
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StartVoiceActivity */
    static class StartVoiceActivity extends StartActivity {
        public String getMethodName() {
            return "startVoiceActivity";
        }

        StartVoiceActivity() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return super.call(obj, method, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StopService */
    static class StopService extends MethodProxy {
        public String getMethodName() {
            return "stopService";
        }

        StopService() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IInterface iInterface = objArr[0];
            Intent intent = objArr[1];
            String str = objArr[2];
            intent.setDataAndType(intent.getData(), str);
            ComponentName component = intent.getComponent();
            PackageManager pm = VirtualCore.getPM();
            if (component == null) {
                ResolveInfo resolveService = pm.resolveService(intent, 0);
                if (!(resolveService == null || resolveService.serviceInfo == null)) {
                    component = new ComponentName(resolveService.serviceInfo.packageName, resolveService.serviceInfo.name);
                }
            }
            if (component == null || getHostPkg().equals(component.getPackageName())) {
                return method.invoke(obj, objArr);
            }
            return Integer.valueOf(VActivityManager.get().stopService(iInterface, intent, str));
        }

        public boolean isEnable() {
            return isAppProcess() || isServerProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$StopServiceToken */
    static class StopServiceToken extends MethodProxy {
        public String getMethodName() {
            return "stopServiceToken";
        }

        StopServiceToken() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ComponentName componentName = objArr[0];
            IBinder iBinder = objArr[1];
            if (!VActivityManager.get().isVAServiceToken(iBinder)) {
                return method.invoke(obj, objArr);
            }
            int intValue = objArr[2].intValue();
            if (componentName != null) {
                return Boolean.valueOf(VActivityManager.get().stopServiceToken(componentName, iBinder, intValue));
            }
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess() || isServerProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$UnbindFinished */
    static class UnbindFinished extends MethodProxy {
        public String getMethodName() {
            return "unbindFinished";
        }

        UnbindFinished() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            VActivityManager.get().unbindFinished(objArr[0], objArr[1], objArr[2].booleanValue());
            return Integer.valueOf(0);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$UnbindService */
    static class UnbindService extends MethodProxy {
        public String getMethodName() {
            return "unbindService";
        }

        UnbindService() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            ServiceConnectionDelegate removeDelegate = ServiceConnectionDelegate.removeDelegate(objArr[0]);
            if (removeDelegate == null) {
                return method.invoke(obj, objArr);
            }
            return Boolean.valueOf(VActivityManager.get().unbindService(removeDelegate));
        }

        public boolean isEnable() {
            return isAppProcess() || isServerProcess();
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$UnstableProviderDied */
    static class UnstableProviderDied extends MethodProxy {
        public String getMethodName() {
            return "unstableProviderDied";
        }

        UnstableProviderDied() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            if (objArr[0] == null) {
                return Integer.valueOf(0);
            }
            return method.invoke(obj, objArr);
        }
    }

    /* renamed from: com.lody.virtual.client.hook.proxies.am.MethodProxies$UpdateDeviceOwner */
    static class UpdateDeviceOwner extends MethodProxy {
        public String getMethodName() {
            return "updateDeviceOwner";
        }

        UpdateDeviceOwner() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(objArr);
            return method.invoke(obj, objArr);
        }

        public boolean isEnable() {
            return isAppProcess();
        }
    }

    MethodProxies() {
    }
}
