package com.lody.virtual.client.core;

import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.MethodInvocationProxy;
import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.delegate.AppInstrumentation;
import com.lody.virtual.client.hook.proxies.account.AccountManagerStub;
import com.lody.virtual.client.hook.proxies.alarm.AlarmManagerStub;
import com.lody.virtual.client.hook.proxies.appops.AppOpsManagerStub;
import com.lody.virtual.client.hook.proxies.appwidget.AppWidgetManagerStub;
import com.lody.virtual.client.hook.proxies.audio.AudioManagerStub;
import com.lody.virtual.client.hook.proxies.backup.BackupManagerStub;
import com.lody.virtual.client.hook.proxies.bluetooth.BluetoothStub;
import com.lody.virtual.client.hook.proxies.clipboard.ClipBoardStub;
import com.lody.virtual.client.hook.proxies.connectivity.ConnectivityStub;
import com.lody.virtual.client.hook.proxies.content.ContentServiceStub;
import com.lody.virtual.client.hook.proxies.context_hub.ContextHubServiceStub;
import com.lody.virtual.client.hook.proxies.devicepolicy.DevicePolicyManagerStub;
import com.lody.virtual.client.hook.proxies.display.DisplayStub;
import com.lody.virtual.client.hook.proxies.dropbox.DropBoxManagerStub;
import com.lody.virtual.client.hook.proxies.fingerprint.FingerprintManagerStub;
import com.lody.virtual.client.hook.proxies.graphics.GraphicsStatsStub;
import com.lody.virtual.client.hook.proxies.imms.MmsStub;
import com.lody.virtual.client.hook.proxies.input.InputMethodManagerStub;
import com.lody.virtual.client.hook.proxies.isms.ISmsStub;
import com.lody.virtual.client.hook.proxies.isub.ISubStub;
import com.lody.virtual.client.hook.proxies.job.JobServiceStub;
import com.lody.virtual.client.hook.proxies.libcore.LibCoreStub;
import com.lody.virtual.client.hook.proxies.location.LocationManagerStub;
import com.lody.virtual.client.hook.proxies.media.router.MediaRouterServiceStub;
import com.lody.virtual.client.hook.proxies.media.session.SessionManagerStub;
import com.lody.virtual.client.hook.proxies.mount.MountServiceStub;
import com.lody.virtual.client.hook.proxies.network.NetworkManagementStub;
import com.lody.virtual.client.hook.proxies.notification.NotificationManagerStub;
import com.lody.virtual.client.hook.proxies.p005am.ActivityManagerStub;
import com.lody.virtual.client.hook.proxies.p005am.ActivityTaskManagerStub;
import com.lody.virtual.client.hook.proxies.p005am.HCallbackStub;
import com.lody.virtual.client.hook.proxies.p005am.TransactionHandlerStub;
import com.lody.virtual.client.hook.proxies.p006pm.LauncherAppsStub;
import com.lody.virtual.client.hook.proxies.p006pm.PackageManagerStub;
import com.lody.virtual.client.hook.proxies.persistent_data_block.PersistentDataBlockServiceStub;
import com.lody.virtual.client.hook.proxies.phonesubinfo.PhoneSubInfoStub;
import com.lody.virtual.client.hook.proxies.power.PowerManagerStub;
import com.lody.virtual.client.hook.proxies.restriction.RestrictionStub;
import com.lody.virtual.client.hook.proxies.search.SearchManagerStub;
import com.lody.virtual.client.hook.proxies.shortcut.ShortcutServiceStub;
import com.lody.virtual.client.hook.proxies.telephony.TelephonyRegistryStub;
import com.lody.virtual.client.hook.proxies.telephony.TelephonyStub;
import com.lody.virtual.client.hook.proxies.usage.UsageStatsManagerStub;
import com.lody.virtual.client.hook.proxies.user.UserManagerStub;
import com.lody.virtual.client.hook.proxies.vibrator.VibratorStub;
import com.lody.virtual.client.hook.proxies.view.AutoFillManagerStub;
import com.lody.virtual.client.hook.proxies.wifi.WifiManagerStub;
import com.lody.virtual.client.hook.proxies.wifi_scanner.WifiScannerStub;
import com.lody.virtual.client.hook.proxies.window.WindowManagerStub;
import com.lody.virtual.client.interfaces.IInjector;
import com.lody.virtual.helper.compat.BuildCompat;
import java.util.HashMap;
import java.util.Map;

public final class InvocationStubManager {
    private static boolean sInit;
    private static InvocationStubManager sInstance = new InvocationStubManager();
    private Map<Class<?>, IInjector> mInjectors = new HashMap(13);

    private InvocationStubManager() {
    }

    public static InvocationStubManager getInstance() {
        return sInstance;
    }

    /* access modifiers changed from: 0000 */
    public void injectAll() throws Throwable {
        for (IInjector inject : this.mInjectors.values()) {
            inject.inject();
        }
        addInjector(AppInstrumentation.getDefault());
    }

    public boolean isInit() {
        return sInit;
    }

    public void init() throws Throwable {
        if (!isInit()) {
            injectInternal();
            sInit = true;
            return;
        }
        throw new IllegalStateException("InvocationStubManager Has been initialized.");
    }

    private void injectInternal() throws Throwable {
        if (!VirtualCore.get().isMainProcess()) {
            if (VirtualCore.get().isServerProcess()) {
                addInjector(new ActivityManagerStub());
                addInjector(new PackageManagerStub());
                return;
            }
            if (VirtualCore.get().isVAppProcess()) {
                addInjector(new LibCoreStub());
                addInjector(new ActivityManagerStub());
                addInjector(new PackageManagerStub());
                if (VERSION.SDK_INT >= 28) {
                    addInjector(new TransactionHandlerStub());
                }
                addInjector(HCallbackStub.getDefault());
                addInjector(new ISmsStub());
                addInjector(new ISubStub());
                addInjector(new DropBoxManagerStub());
                addInjector(new NotificationManagerStub());
                addInjector(new LocationManagerStub());
                addInjector(new WindowManagerStub());
                addInjector(new ClipBoardStub());
                addInjector(new MountServiceStub());
                addInjector(new BackupManagerStub());
                addInjector(new TelephonyStub());
                addInjector(new TelephonyRegistryStub());
                addInjector(new PhoneSubInfoStub());
                addInjector(new PowerManagerStub());
                addInjector(new AppWidgetManagerStub());
                addInjector(new AccountManagerStub());
                addInjector(new AudioManagerStub());
                addInjector(new SearchManagerStub());
                addInjector(new ContentServiceStub());
                addInjector(new ConnectivityStub());
                if (VERSION.SDK_INT >= 18) {
                    addInjector(new VibratorStub());
                    addInjector(new WifiManagerStub());
                    addInjector(new BluetoothStub());
                    addInjector(new ContextHubServiceStub());
                }
                if (VERSION.SDK_INT >= 17) {
                    addInjector(new UserManagerStub());
                }
                if (VERSION.SDK_INT >= 17) {
                    addInjector(new DisplayStub());
                }
                if (VERSION.SDK_INT >= 21) {
                    addInjector(new PersistentDataBlockServiceStub());
                    addInjector(new InputMethodManagerStub());
                    addInjector(new MmsStub());
                    addInjector(new SessionManagerStub());
                    addInjector(new JobServiceStub());
                    addInjector(new RestrictionStub());
                }
                if (VERSION.SDK_INT >= 19) {
                    addInjector(new AlarmManagerStub());
                    addInjector(new AppOpsManagerStub());
                    addInjector(new MediaRouterServiceStub());
                }
                if (VERSION.SDK_INT >= 22) {
                    addInjector(new GraphicsStatsStub());
                    addInjector(new UsageStatsManagerStub());
                    addInjector(new LauncherAppsStub());
                }
                if (VERSION.SDK_INT >= 23) {
                    addInjector(new FingerprintManagerStub());
                    addInjector(new NetworkManagementStub());
                }
                if (VERSION.SDK_INT >= 24) {
                    addInjector(new WifiScannerStub());
                    addInjector(new ShortcutServiceStub());
                    addInjector(new DevicePolicyManagerStub());
                }
                if (BuildCompat.isOreo()) {
                    addInjector(new AutoFillManagerStub());
                }
                if (BuildCompat.isQ()) {
                    addInjector(new ActivityTaskManagerStub());
                }
            }
        }
    }

    private void addInjector(IInjector iInjector) {
        this.mInjectors.put(iInjector.getClass(), iInjector);
    }

    public <T extends IInjector> T findInjector(Class<T> cls) {
        return (IInjector) this.mInjectors.get(cls);
    }

    public <T extends IInjector> void checkEnv(Class<T> cls) {
        IInjector findInjector = findInjector(cls);
        if (findInjector != null && findInjector.isEnvBad()) {
            try {
                findInjector.inject();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public <T extends IInjector, H extends MethodInvocationStub> H getInvocationStub(Class<T> cls) {
        IInjector findInjector = findInjector(cls);
        if (findInjector == null || !(findInjector instanceof MethodInvocationProxy)) {
            return null;
        }
        return ((MethodInvocationProxy) findInjector).getInvocationStub();
    }
}
