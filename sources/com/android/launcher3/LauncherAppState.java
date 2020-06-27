package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Looper;
import android.util.Log;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.dynamicui.ExtractionUtils;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.util.ConfigMonitor;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.SettingsObserver;
import com.android.launcher3.util.SettingsObserver.Secure;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class LauncherAppState {
    private static LauncherAppState INSTANCE = null;
    public static final boolean PROFILE_STARTUP = false;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final IconCache mIconCache;
    private final InvariantDeviceProfile mInvariantDeviceProfile;
    private final LauncherModel mModel;
    private final SettingsObserver mNotificationBadgingObserver;
    private final WidgetPreviewLoader mWidgetCache;

    public static LauncherAppState getInstance(final Context context) {
        if (INSTANCE == null) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                INSTANCE = new LauncherAppState(context.getApplicationContext());
            } else {
                try {
                    return (LauncherAppState) new MainThreadExecutor().submit(new Callable<LauncherAppState>() {
                        public LauncherAppState call() throws Exception {
                            return LauncherAppState.getInstance(context);
                        }
                    }).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return INSTANCE;
    }

    public static LauncherAppState getInstanceNoCreate() {
        return INSTANCE;
    }

    public Context getContext() {
        return this.mContext;
    }

    private LauncherAppState(Context context) {
        if (getLocalProvider(context) != null) {
            Log.v(Launcher.TAG, "LauncherAppState initiated");
            Preconditions.assertUIThread();
            this.mContext = context;
            this.mInvariantDeviceProfile = new InvariantDeviceProfile(this.mContext);
            this.mIconCache = new IconCache(this.mContext, this.mInvariantDeviceProfile);
            this.mWidgetCache = new WidgetPreviewLoader(this.mContext, this.mIconCache);
            this.mModel = new LauncherModel(this, this.mIconCache, AppFilter.newInstance(this.mContext));
            LauncherAppsCompat.getInstance(this.mContext).addOnAppsChangedCallback(this.mModel);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_ADDED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_REMOVED");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_AVAILABLE");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNAVAILABLE");
            intentFilter.addAction("android.intent.action.MANAGED_PROFILE_UNLOCKED");
            if (Utilities.ATLEAST_NOUGAT) {
                intentFilter.addAction("android.intent.action.WALLPAPER_CHANGED");
            }
            this.mContext.registerReceiver(this.mModel, intentFilter);
            UserManagerCompat.getInstance(this.mContext).enableAndResetCache();
            new ConfigMonitor(this.mContext).register();
            ExtractionUtils.startColorExtractionServiceIfNecessary(this.mContext);
            if (!this.mContext.getResources().getBoolean(C0622R.bool.notification_badging_enabled)) {
                this.mNotificationBadgingObserver = null;
                return;
            }
            this.mNotificationBadgingObserver = new Secure(this.mContext.getContentResolver()) {
                public void onSettingChanged(boolean z) {
                    if (z && Utilities.ATLEAST_NOUGAT) {
                        NotificationListener.requestRebind(new ComponentName(LauncherAppState.this.mContext, NotificationListener.class));
                    }
                }
            };
            this.mNotificationBadgingObserver.register(SettingsActivity.NOTIFICATION_BADGING, new String[0]);
            return;
        }
        throw new RuntimeException("Initializing LauncherAppState in the absence of LauncherProvider");
    }

    public void onTerminate() {
        this.mContext.unregisterReceiver(this.mModel);
        LauncherAppsCompat.getInstance(this.mContext).removeOnAppsChangedCallback(this.mModel);
        PackageInstallerCompat.getInstance(this.mContext).onStop();
        if (this.mNotificationBadgingObserver != null) {
            this.mNotificationBadgingObserver.unregister();
        }
    }

    /* access modifiers changed from: 0000 */
    public LauncherModel setLauncher(Launcher launcher) {
        getLocalProvider(this.mContext).setLauncherProviderChangeListener(launcher);
        this.mModel.initialize(launcher);
        return this.mModel;
    }

    public IconCache getIconCache() {
        return this.mIconCache;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public WidgetPreviewLoader getWidgetCache() {
        return this.mWidgetCache;
    }

    public InvariantDeviceProfile getInvariantDeviceProfile() {
        return this.mInvariantDeviceProfile;
    }

    public static InvariantDeviceProfile getIDP(Context context) {
        return getInstance(context).getInvariantDeviceProfile();
    }

    private static LauncherProvider getLocalProvider(Context context) {
        ContentProviderClient acquireContentProviderClient = context.getContentResolver().acquireContentProviderClient(LauncherProvider.AUTHORITY);
        LauncherProvider launcherProvider = (LauncherProvider) acquireContentProviderClient.getLocalContentProvider();
        acquireContentProviderClient.release();
        return launcherProvider;
    }
}
