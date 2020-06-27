package com.microsoft.appcenter;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import com.android.launcher3.IconCache;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.channel.DefaultChannel;
import com.microsoft.appcenter.channel.OneCollectorChannelListener;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.http.HttpUtils;
import com.microsoft.appcenter.ingestion.models.CustomPropertiesLog;
import com.microsoft.appcenter.ingestion.models.StartServiceLog;
import com.microsoft.appcenter.ingestion.models.WrapperSdk;
import com.microsoft.appcenter.ingestion.models.json.CustomPropertiesLogFactory;
import com.microsoft.appcenter.ingestion.models.json.DefaultLogSerializer;
import com.microsoft.appcenter.ingestion.models.json.LogFactory;
import com.microsoft.appcenter.ingestion.models.json.LogSerializer;
import com.microsoft.appcenter.ingestion.models.json.StartServiceLogFactory;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.DeviceInfoHelper;
import com.microsoft.appcenter.utils.IdHelper;
import com.microsoft.appcenter.utils.InstrumentationRegistryHelper;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import com.microsoft.appcenter.utils.PrefStorageConstants;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.microsoft.appcenter.utils.async.DefaultAppCenterFuture;
import com.microsoft.appcenter.utils.context.SessionContext;
import com.microsoft.appcenter.utils.context.UserIdContext;
import com.microsoft.appcenter.utils.storage.FileManager;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class AppCenter {
    @VisibleForTesting
    static final String APP_SECRET_KEY = "appsecret";
    @VisibleForTesting
    static final String CORE_GROUP = "group_core";
    @VisibleForTesting
    static final long DEFAULT_MAX_STORAGE_SIZE_IN_BYTES = 10485760;
    @VisibleForTesting
    static final String KEY_VALUE_DELIMITER = "=";
    public static final String LOG_TAG = "AppCenter";
    @VisibleForTesting
    static final long MINIMUM_STORAGE_SIZE = 24576;
    @VisibleForTesting
    static final String PAIR_DELIMITER = ";";
    @VisibleForTesting
    static final String RUNNING_IN_APP_CENTER = "RUNNING_IN_APP_CENTER";
    @VisibleForTesting
    static final String TRANSMISSION_TARGET_TOKEN_KEY = "target";
    private static final String TRUE_ENVIRONMENT_STRING = "1";
    @SuppressLint({"StaticFieldLeak"})
    private static AppCenter sInstance;
    private AppCenterHandler mAppCenterHandler;
    /* access modifiers changed from: private */
    public String mAppSecret;
    private Application mApplication;
    /* access modifiers changed from: private */
    public Channel mChannel;
    private boolean mConfiguredFromApp;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mLogLevelConfigured;
    private LogSerializer mLogSerializer;
    private String mLogUrl;
    private long mMaxStorageSizeInBytes = DEFAULT_MAX_STORAGE_SIZE_IN_BYTES;
    /* access modifiers changed from: private */
    public OneCollectorChannelListener mOneCollectorChannelListener;
    private Set<AppCenterService> mServices;
    private Set<AppCenterService> mServicesStartedFromLibrary;
    private DefaultAppCenterFuture<Boolean> mSetMaxStorageSizeFuture;
    private final List<String> mStartedServicesNamesToLog = new ArrayList();
    private String mTransmissionTargetToken;
    private UncaughtExceptionHandler mUncaughtExceptionHandler;

    public static String getSdkVersion() {
        return "3.0.0";
    }

    public static synchronized AppCenter getInstance() {
        AppCenter appCenter;
        synchronized (AppCenter.class) {
            if (sInstance == null) {
                sInstance = new AppCenter();
            }
            appCenter = sInstance;
        }
        return appCenter;
    }

    @VisibleForTesting
    static synchronized void unsetInstance() {
        synchronized (AppCenter.class) {
            sInstance = null;
            NetworkStateHelper.unsetInstance();
        }
    }

    public static void setWrapperSdk(WrapperSdk wrapperSdk) {
        getInstance().setInstanceWrapperSdk(wrapperSdk);
    }

    @IntRange(from = 2, mo452to = 8)
    public static int getLogLevel() {
        return AppCenterLog.getLogLevel();
    }

    public static void setLogLevel(@IntRange(from = 2, mo452to = 8) int i) {
        getInstance().setInstanceLogLevel(i);
    }

    public static void setLogUrl(String str) {
        getInstance().setInstanceLogUrl(str);
    }

    public static void setCustomProperties(CustomProperties customProperties) {
        getInstance().setInstanceCustomProperties(customProperties);
    }

    public static boolean isConfigured() {
        return getInstance().isInstanceConfigured();
    }

    public static boolean isRunningInAppCenterTestCloud() {
        try {
            return TRUE_ENVIRONMENT_STRING.equals(InstrumentationRegistryHelper.getArguments().getString(RUNNING_IN_APP_CENTER));
        } catch (IllegalStateException unused) {
            return false;
        }
    }

    public static void configure(Application application, String str) {
        getInstance().configureInstanceWithRequiredAppSecret(application, str);
    }

    public static void configure(Application application) {
        getInstance().configureInstance(application, null, true);
    }

    @SafeVarargs
    public static void start(Class<? extends AppCenterService>... clsArr) {
        getInstance().startServices(true, clsArr);
    }

    @SafeVarargs
    public static void start(Application application, String str, Class<? extends AppCenterService>... clsArr) {
        getInstance().configureAndStartServices(application, str, clsArr);
    }

    @SafeVarargs
    public static void start(Application application, Class<? extends AppCenterService>... clsArr) {
        getInstance().configureAndStartServices(application, null, true, clsArr);
    }

    @SafeVarargs
    public static void startFromLibrary(Context context, Class<? extends AppCenterService>... clsArr) {
        getInstance().startInstanceFromLibrary(context, clsArr);
    }

    public static AppCenterFuture<Boolean> isEnabled() {
        return getInstance().isInstanceEnabledAsync();
    }

    public static AppCenterFuture<Void> setEnabled(boolean z) {
        return getInstance().setInstanceEnabledAsync(z);
    }

    public static AppCenterFuture<UUID> getInstallId() {
        return getInstance().getInstanceInstallIdAsync();
    }

    public static AppCenterFuture<Boolean> setMaxStorageSize(long j) {
        return getInstance().setInstanceMaxStorageSizeAsync(j);
    }

    private synchronized void setInstanceUserId(String str) {
        if (!this.mConfiguredFromApp) {
            AppCenterLog.error("AppCenter", "AppCenter must be configured from application, libraries cannot use call setUserId.");
        } else if (this.mAppSecret == null && this.mTransmissionTargetToken == null) {
            AppCenterLog.error("AppCenter", "AppCenter must be configured with a secret from application to call setUserId.");
        } else {
            if (str != null) {
                if (this.mAppSecret != null && !UserIdContext.checkUserIdValidForAppCenter(str)) {
                    return;
                }
                if (this.mTransmissionTargetToken != null && !UserIdContext.checkUserIdValidForOneCollector(str)) {
                    return;
                }
            }
            UserIdContext.getInstance().setUserId(str);
        }
    }

    private synchronized boolean checkPrecondition() {
        if (isInstanceConfigured()) {
            return true;
        }
        AppCenterLog.error("AppCenter", "App Center hasn't been configured. You need to call AppCenter.start with appSecret or AppCenter.configure first.");
        return false;
    }

    private synchronized void setInstanceWrapperSdk(WrapperSdk wrapperSdk) {
        DeviceInfoHelper.setWrapperSdk(wrapperSdk);
        if (this.mHandler != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    AppCenter.this.mChannel.invalidateDeviceCache();
                }
            });
        }
    }

    private synchronized void setInstanceLogLevel(int i) {
        this.mLogLevelConfigured = true;
        AppCenterLog.setLogLevel(i);
    }

    private synchronized void setInstanceLogUrl(final String str) {
        this.mLogUrl = str;
        if (this.mHandler != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (AppCenter.this.mAppSecret != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("The log url of App Center endpoint has been changed to ");
                        sb.append(str);
                        AppCenterLog.info("AppCenter", sb.toString());
                        AppCenter.this.mChannel.setLogUrl(str);
                        return;
                    }
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("The log url of One Collector endpoint has been changed to ");
                    sb2.append(str);
                    AppCenterLog.info("AppCenter", sb2.toString());
                    AppCenter.this.mOneCollectorChannelListener.setLogUrl(str);
                }
            });
        }
    }

    private synchronized void setInstanceCustomProperties(CustomProperties customProperties) {
        if (customProperties == null) {
            AppCenterLog.error("AppCenter", "Custom properties may not be null.");
            return;
        }
        final Map properties = customProperties.getProperties();
        if (properties.size() == 0) {
            AppCenterLog.error("AppCenter", "Custom properties may not be empty.");
        } else {
            handlerAppCenterOperation(new Runnable() {
                public void run() {
                    AppCenter.this.queueCustomProperties(properties);
                }
            }, null);
        }
    }

    private synchronized AppCenterFuture<Boolean> setInstanceMaxStorageSizeAsync(long j) {
        DefaultAppCenterFuture<Boolean> defaultAppCenterFuture = new DefaultAppCenterFuture<>();
        if (this.mConfiguredFromApp) {
            AppCenterLog.error("AppCenter", "setMaxStorageSize may not be called after App Center has been configured.");
            defaultAppCenterFuture.complete(Boolean.valueOf(false));
            return defaultAppCenterFuture;
        } else if (j < MINIMUM_STORAGE_SIZE) {
            AppCenterLog.error("AppCenter", "Maximum storage size must be at least 24576 bytes.");
            defaultAppCenterFuture.complete(Boolean.valueOf(false));
            return defaultAppCenterFuture;
        } else if (this.mSetMaxStorageSizeFuture != null) {
            AppCenterLog.error("AppCenter", "setMaxStorageSize may only be called once per app launch.");
            defaultAppCenterFuture.complete(Boolean.valueOf(false));
            return defaultAppCenterFuture;
        } else {
            this.mMaxStorageSizeInBytes = j;
            this.mSetMaxStorageSizeFuture = defaultAppCenterFuture;
            return defaultAppCenterFuture;
        }
    }

    private synchronized boolean isInstanceConfigured() {
        return this.mApplication != null;
    }

    private void configureInstanceWithRequiredAppSecret(Application application, String str) {
        if (str == null || str.isEmpty()) {
            AppCenterLog.error("AppCenter", "appSecret may not be null or empty.");
        } else {
            configureInstance(application, str, true);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004a, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized boolean configureInstance(android.app.Application r4, java.lang.String r5, final boolean r6) {
        /*
            r3 = this;
            monitor-enter(r3)
            r0 = 0
            if (r4 != 0) goto L_0x0010
            java.lang.String r4 = "AppCenter"
            java.lang.String r5 = "Application context may not be null."
            com.microsoft.appcenter.utils.AppCenterLog.error(r4, r5)     // Catch:{ all -> 0x000d }
            monitor-exit(r3)
            return r0
        L_0x000d:
            r4 = move-exception
            goto L_0x0090
        L_0x0010:
            boolean r1 = r3.mLogLevelConfigured     // Catch:{ all -> 0x000d }
            if (r1 != 0) goto L_0x0022
            android.content.pm.ApplicationInfo r1 = r4.getApplicationInfo()     // Catch:{ all -> 0x000d }
            int r1 = r1.flags     // Catch:{ all -> 0x000d }
            r2 = 2
            r1 = r1 & r2
            if (r1 != r2) goto L_0x0022
            r1 = 5
            com.microsoft.appcenter.utils.AppCenterLog.setLogLevel(r1)     // Catch:{ all -> 0x000d }
        L_0x0022:
            java.lang.String r1 = r3.mAppSecret     // Catch:{ all -> 0x000d }
            if (r6 == 0) goto L_0x002e
            boolean r5 = r3.configureSecretString(r5)     // Catch:{ all -> 0x000d }
            if (r5 != 0) goto L_0x002e
            monitor-exit(r3)
            return r0
        L_0x002e:
            android.os.Handler r5 = r3.mHandler     // Catch:{ all -> 0x000d }
            r0 = 1
            if (r5 == 0) goto L_0x004b
            java.lang.String r4 = r3.mAppSecret     // Catch:{ all -> 0x000d }
            if (r4 == 0) goto L_0x0049
            java.lang.String r4 = r3.mAppSecret     // Catch:{ all -> 0x000d }
            boolean r4 = r4.equals(r1)     // Catch:{ all -> 0x000d }
            if (r4 != 0) goto L_0x0049
            android.os.Handler r4 = r3.mHandler     // Catch:{ all -> 0x000d }
            com.microsoft.appcenter.AppCenter$4 r5 = new com.microsoft.appcenter.AppCenter$4     // Catch:{ all -> 0x000d }
            r5.<init>()     // Catch:{ all -> 0x000d }
            r4.post(r5)     // Catch:{ all -> 0x000d }
        L_0x0049:
            monitor-exit(r3)
            return r0
        L_0x004b:
            r3.mApplication = r4     // Catch:{ all -> 0x000d }
            android.os.HandlerThread r4 = new android.os.HandlerThread     // Catch:{ all -> 0x000d }
            java.lang.String r5 = "AppCenter.Looper"
            r4.<init>(r5)     // Catch:{ all -> 0x000d }
            r3.mHandlerThread = r4     // Catch:{ all -> 0x000d }
            android.os.HandlerThread r4 = r3.mHandlerThread     // Catch:{ all -> 0x000d }
            r4.start()     // Catch:{ all -> 0x000d }
            android.os.Handler r4 = new android.os.Handler     // Catch:{ all -> 0x000d }
            android.os.HandlerThread r5 = r3.mHandlerThread     // Catch:{ all -> 0x000d }
            android.os.Looper r5 = r5.getLooper()     // Catch:{ all -> 0x000d }
            r4.<init>(r5)     // Catch:{ all -> 0x000d }
            r3.mHandler = r4     // Catch:{ all -> 0x000d }
            com.microsoft.appcenter.AppCenter$5 r4 = new com.microsoft.appcenter.AppCenter$5     // Catch:{ all -> 0x000d }
            r4.<init>()     // Catch:{ all -> 0x000d }
            r3.mAppCenterHandler = r4     // Catch:{ all -> 0x000d }
            java.util.HashSet r4 = new java.util.HashSet     // Catch:{ all -> 0x000d }
            r4.<init>()     // Catch:{ all -> 0x000d }
            r3.mServices = r4     // Catch:{ all -> 0x000d }
            java.util.HashSet r4 = new java.util.HashSet     // Catch:{ all -> 0x000d }
            r4.<init>()     // Catch:{ all -> 0x000d }
            r3.mServicesStartedFromLibrary = r4     // Catch:{ all -> 0x000d }
            android.os.Handler r4 = r3.mHandler     // Catch:{ all -> 0x000d }
            com.microsoft.appcenter.AppCenter$6 r5 = new com.microsoft.appcenter.AppCenter$6     // Catch:{ all -> 0x000d }
            r5.<init>(r6)     // Catch:{ all -> 0x000d }
            r4.post(r5)     // Catch:{ all -> 0x000d }
            java.lang.String r4 = "AppCenter"
            java.lang.String r5 = "App Center SDK configured successfully."
            com.microsoft.appcenter.utils.AppCenterLog.info(r4, r5)     // Catch:{ all -> 0x000d }
            monitor-exit(r3)
            return r0
        L_0x0090:
            monitor-exit(r3)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.AppCenter.configureInstance(android.app.Application, java.lang.String, boolean):boolean");
    }

    private boolean configureSecretString(String str) {
        if (this.mConfiguredFromApp) {
            AppCenterLog.warn("AppCenter", "App Center may only be configured once.");
            return false;
        }
        this.mConfiguredFromApp = true;
        if (str != null) {
            for (String split : str.split(PAIR_DELIMITER)) {
                String[] split2 = split.split(KEY_VALUE_DELIMITER, -1);
                String str2 = split2[0];
                if (split2.length == 1) {
                    if (!str2.isEmpty()) {
                        this.mAppSecret = str2;
                    }
                } else if (!split2[1].isEmpty()) {
                    String str3 = split2[1];
                    if (APP_SECRET_KEY.equals(str2)) {
                        this.mAppSecret = str3;
                    } else if (TRANSMISSION_TARGET_TOKEN_KEY.equals(str2)) {
                        this.mTransmissionTargetToken = str3;
                    }
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public synchronized void handlerAppCenterOperation(final Runnable runnable, final Runnable runnable2) {
        if (checkPrecondition()) {
            C11587 r0 = new Runnable() {
                public void run() {
                    if (AppCenter.this.isInstanceEnabled()) {
                        runnable.run();
                    } else if (runnable2 != null) {
                        runnable2.run();
                    } else {
                        AppCenterLog.error("AppCenter", "App Center SDK is disabled.");
                    }
                }
            };
            if (Thread.currentThread() == this.mHandlerThread) {
                runnable.run();
            } else {
                this.mHandler.post(r0);
            }
        }
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public void finishConfiguration(boolean z) {
        Constants.loadFromContext(this.mApplication);
        FileManager.initialize(this.mApplication);
        SharedPreferencesManager.initialize(this.mApplication);
        SessionContext.getInstance();
        boolean isInstanceEnabled = isInstanceEnabled();
        HttpClient httpClient = DependencyConfiguration.getHttpClient();
        if (httpClient == null) {
            httpClient = HttpUtils.createHttpClient(this.mApplication);
        }
        this.mLogSerializer = new DefaultLogSerializer();
        this.mLogSerializer.addLogFactory(StartServiceLog.TYPE, new StartServiceLogFactory());
        this.mLogSerializer.addLogFactory(CustomPropertiesLog.TYPE, new CustomPropertiesLogFactory());
        DefaultChannel defaultChannel = new DefaultChannel((Context) this.mApplication, this.mAppSecret, this.mLogSerializer, httpClient, this.mHandler);
        this.mChannel = defaultChannel;
        if (z) {
            applyStorageMaxSize();
        } else {
            this.mChannel.setMaxStorageSize(DEFAULT_MAX_STORAGE_SIZE_IN_BYTES);
        }
        this.mChannel.setEnabled(isInstanceEnabled);
        this.mChannel.addGroup(CORE_GROUP, 50, 3000, 3, null, null);
        this.mOneCollectorChannelListener = new OneCollectorChannelListener(this.mChannel, this.mLogSerializer, httpClient, IdHelper.getInstallId());
        if (this.mLogUrl != null) {
            if (this.mAppSecret != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("The log url of App Center endpoint has been changed to ");
                sb.append(this.mLogUrl);
                AppCenterLog.info("AppCenter", sb.toString());
                this.mChannel.setLogUrl(this.mLogUrl);
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("The log url of One Collector endpoint has been changed to ");
                sb2.append(this.mLogUrl);
                AppCenterLog.info("AppCenter", sb2.toString());
                this.mOneCollectorChannelListener.setLogUrl(this.mLogUrl);
            }
        }
        this.mChannel.addListener(this.mOneCollectorChannelListener);
        if (!isInstanceEnabled) {
            NetworkStateHelper.getSharedInstance(this.mApplication).close();
        }
        this.mUncaughtExceptionHandler = new UncaughtExceptionHandler(this.mHandler, this.mChannel);
        if (isInstanceEnabled) {
            this.mUncaughtExceptionHandler.register();
        }
        AppCenterLog.debug("AppCenter", "App Center initialized.");
    }

    /* access modifiers changed from: private */
    public void applyStorageMaxSize() {
        boolean maxStorageSize = this.mChannel.setMaxStorageSize(this.mMaxStorageSizeInBytes);
        if (this.mSetMaxStorageSizeFuture != null) {
            this.mSetMaxStorageSizeFuture.complete(Boolean.valueOf(maxStorageSize));
        }
    }

    @SafeVarargs
    private final synchronized void startServices(final boolean z, Class<? extends AppCenterService>... clsArr) {
        if (clsArr == null) {
            AppCenterLog.error("AppCenter", "Cannot start services, services array is null. Failed to start services.");
            return;
        }
        if (this.mApplication == null) {
            StringBuilder sb = new StringBuilder();
            for (Class<? extends AppCenterService> cls : clsArr) {
                sb.append("\t");
                sb.append(cls.getName());
                sb.append("\n");
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot start services, App Center has not been configured. Failed to start the following services:\n");
            sb2.append(sb);
            AppCenterLog.error("AppCenter", sb2.toString());
            return;
        }
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        for (Class<? extends AppCenterService> cls2 : clsArr) {
            if (cls2 == null) {
                AppCenterLog.warn("AppCenter", "Skipping null service, please check your varargs/array does not contain any null reference.");
            } else {
                try {
                    startOrUpdateService((AppCenterService) cls2.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]), arrayList, arrayList2, z);
                } catch (Exception e) {
                    String str = "AppCenter";
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Failed to get service instance '");
                    sb3.append(cls2.getName());
                    sb3.append("', skipping it.");
                    AppCenterLog.error(str, sb3.toString(), e);
                }
            }
        }
        this.mHandler.post(new Runnable() {
            public void run() {
                AppCenter.this.finishStartServices(arrayList2, arrayList, z);
            }
        });
    }

    private void startOrUpdateService(AppCenterService appCenterService, Collection<AppCenterService> collection, Collection<AppCenterService> collection2, boolean z) {
        if (z) {
            startOrUpdateServiceFromApp(appCenterService, collection, collection2);
        } else if (!this.mServices.contains(appCenterService)) {
            startServiceFromLibrary(appCenterService, collection);
        }
    }

    private void startOrUpdateServiceFromApp(AppCenterService appCenterService, Collection<AppCenterService> collection, Collection<AppCenterService> collection2) {
        String serviceName = appCenterService.getServiceName();
        if (this.mServices.contains(appCenterService)) {
            if (this.mServicesStartedFromLibrary.remove(appCenterService)) {
                collection2.add(appCenterService);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("App Center has already started the service with class name: ");
            sb.append(appCenterService.getServiceName());
            AppCenterLog.warn("AppCenter", sb.toString());
        } else if (this.mAppSecret != null || !appCenterService.isAppSecretRequired()) {
            startService(appCenterService, collection);
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("App Center was started without app secret, but the service requires it; not starting service ");
            sb2.append(serviceName);
            sb2.append(IconCache.EMPTY_CLASS_NAME);
            AppCenterLog.error("AppCenter", sb2.toString());
        }
    }

    private void startServiceFromLibrary(AppCenterService appCenterService, Collection<AppCenterService> collection) {
        String serviceName = appCenterService.getServiceName();
        if (appCenterService.isAppSecretRequired()) {
            StringBuilder sb = new StringBuilder();
            sb.append("This service cannot be started from a library: ");
            sb.append(serviceName);
            sb.append(IconCache.EMPTY_CLASS_NAME);
            AppCenterLog.error("AppCenter", sb.toString());
        } else if (startService(appCenterService, collection)) {
            this.mServicesStartedFromLibrary.add(appCenterService);
        }
    }

    private boolean startService(AppCenterService appCenterService, Collection<AppCenterService> collection) {
        String serviceName = appCenterService.getServiceName();
        if (ServiceInstrumentationUtils.isServiceDisabledByInstrumentation(serviceName)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Instrumentation variable to disable service has been set; not starting service ");
            sb.append(serviceName);
            sb.append(IconCache.EMPTY_CLASS_NAME);
            AppCenterLog.debug("AppCenter", sb.toString());
            return false;
        }
        appCenterService.onStarting(this.mAppCenterHandler);
        this.mApplication.registerActivityLifecycleCallbacks(appCenterService);
        this.mServices.add(appCenterService);
        collection.add(appCenterService);
        return true;
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public void finishStartServices(Iterable<AppCenterService> iterable, Iterable<AppCenterService> iterable2, boolean z) {
        for (AppCenterService appCenterService : iterable) {
            appCenterService.onConfigurationUpdated(this.mAppSecret, this.mTransmissionTargetToken);
            StringBuilder sb = new StringBuilder();
            sb.append(appCenterService.getClass().getSimpleName());
            sb.append(" service configuration updated.");
            AppCenterLog.info("AppCenter", sb.toString());
        }
        boolean isInstanceEnabled = isInstanceEnabled();
        for (AppCenterService appCenterService2 : iterable2) {
            Map logFactories = appCenterService2.getLogFactories();
            if (logFactories != null) {
                for (Entry entry : logFactories.entrySet()) {
                    this.mLogSerializer.addLogFactory((String) entry.getKey(), (LogFactory) entry.getValue());
                }
            }
            if (!isInstanceEnabled && appCenterService2.isInstanceEnabled()) {
                appCenterService2.setInstanceEnabled(false);
            }
            if (z) {
                appCenterService2.onStarted(this.mApplication, this.mChannel, this.mAppSecret, this.mTransmissionTargetToken, true);
                StringBuilder sb2 = new StringBuilder();
                sb2.append(appCenterService2.getClass().getSimpleName());
                sb2.append(" service started from application.");
                AppCenterLog.info("AppCenter", sb2.toString());
            } else {
                appCenterService2.onStarted(this.mApplication, this.mChannel, null, null, false);
                StringBuilder sb3 = new StringBuilder();
                sb3.append(appCenterService2.getClass().getSimpleName());
                sb3.append(" service started from library.");
                AppCenterLog.info("AppCenter", sb3.toString());
            }
        }
        if (z) {
            for (AppCenterService serviceName : iterable) {
                this.mStartedServicesNamesToLog.add(serviceName.getServiceName());
            }
            for (AppCenterService serviceName2 : iterable2) {
                this.mStartedServicesNamesToLog.add(serviceName2.getServiceName());
            }
            sendStartServiceLog();
        }
    }

    @WorkerThread
    private void sendStartServiceLog() {
        if (!this.mStartedServicesNamesToLog.isEmpty() && isInstanceEnabled()) {
            ArrayList arrayList = new ArrayList(this.mStartedServicesNamesToLog);
            this.mStartedServicesNamesToLog.clear();
            StartServiceLog startServiceLog = new StartServiceLog();
            startServiceLog.setServices(arrayList);
            this.mChannel.enqueue(startServiceLog, CORE_GROUP, 1);
        }
    }

    private synchronized void configureAndStartServices(Application application, String str, Class<? extends AppCenterService>[] clsArr) {
        if (str != null) {
            try {
                if (!str.isEmpty()) {
                    configureAndStartServices(application, str, true, clsArr);
                }
            } finally {
            }
        }
        AppCenterLog.error("AppCenter", "appSecret may not be null or empty.");
    }

    private synchronized void startInstanceFromLibrary(Context context, Class<? extends AppCenterService>[] clsArr) {
        Application application;
        if (context != null) {
            try {
                application = (Application) context.getApplicationContext();
            } catch (Throwable th) {
                throw th;
            }
        } else {
            application = null;
        }
        configureAndStartServices(application, null, false, clsArr);
    }

    private void configureAndStartServices(Application application, String str, boolean z, Class<? extends AppCenterService>[] clsArr) {
        if (configureInstance(application, str, z)) {
            startServices(z, clsArr);
        }
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public void queueCustomProperties(@NonNull Map<String, Object> map) {
        CustomPropertiesLog customPropertiesLog = new CustomPropertiesLog();
        customPropertiesLog.setProperties(map);
        this.mChannel.enqueue(customPropertiesLog, CORE_GROUP, 1);
    }

    private synchronized AppCenterFuture<Boolean> isInstanceEnabledAsync() {
        final DefaultAppCenterFuture defaultAppCenterFuture;
        defaultAppCenterFuture = new DefaultAppCenterFuture();
        if (checkPrecondition()) {
            this.mAppCenterHandler.post(new Runnable() {
                public void run() {
                    defaultAppCenterFuture.complete(Boolean.valueOf(true));
                }
            }, new Runnable() {
                public void run() {
                    defaultAppCenterFuture.complete(Boolean.valueOf(false));
                }
            });
        } else {
            defaultAppCenterFuture.complete(Boolean.valueOf(false));
        }
        return defaultAppCenterFuture;
    }

    /* access modifiers changed from: 0000 */
    public boolean isInstanceEnabled() {
        return SharedPreferencesManager.getBoolean(PrefStorageConstants.KEY_ENABLED, true);
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public void setInstanceEnabled(boolean z) {
        this.mChannel.setEnabled(z);
        boolean isInstanceEnabled = isInstanceEnabled();
        boolean z2 = isInstanceEnabled && !z;
        boolean z3 = !isInstanceEnabled && z;
        if (z3) {
            this.mUncaughtExceptionHandler.register();
            NetworkStateHelper.getSharedInstance(this.mApplication).reopen();
        } else if (z2) {
            this.mUncaughtExceptionHandler.unregister();
            NetworkStateHelper.getSharedInstance(this.mApplication).close();
        }
        if (z) {
            SharedPreferencesManager.putBoolean(PrefStorageConstants.KEY_ENABLED, true);
        }
        if (!this.mStartedServicesNamesToLog.isEmpty() && z3) {
            sendStartServiceLog();
        }
        for (AppCenterService appCenterService : this.mServices) {
            if (appCenterService.isInstanceEnabled() != z) {
                appCenterService.setInstanceEnabled(z);
            }
        }
        if (!z) {
            SharedPreferencesManager.putBoolean(PrefStorageConstants.KEY_ENABLED, false);
        }
        if (z2) {
            AppCenterLog.info("AppCenter", "App Center has been disabled.");
        } else if (z3) {
            AppCenterLog.info("AppCenter", "App Center has been enabled.");
        } else {
            String str = "AppCenter";
            StringBuilder sb = new StringBuilder();
            sb.append("App Center has already been ");
            sb.append(z ? PrefStorageConstants.KEY_ENABLED : "disabled");
            sb.append(IconCache.EMPTY_CLASS_NAME);
            AppCenterLog.info(str, sb.toString());
        }
    }

    private synchronized AppCenterFuture<Void> setInstanceEnabledAsync(final boolean z) {
        final DefaultAppCenterFuture defaultAppCenterFuture;
        defaultAppCenterFuture = new DefaultAppCenterFuture();
        if (checkPrecondition()) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    AppCenter.this.setInstanceEnabled(z);
                    defaultAppCenterFuture.complete(null);
                }
            });
        } else {
            defaultAppCenterFuture.complete(null);
        }
        return defaultAppCenterFuture;
    }

    private synchronized AppCenterFuture<UUID> getInstanceInstallIdAsync() {
        final DefaultAppCenterFuture defaultAppCenterFuture;
        defaultAppCenterFuture = new DefaultAppCenterFuture();
        if (checkPrecondition()) {
            this.mAppCenterHandler.post(new Runnable() {
                public void run() {
                    defaultAppCenterFuture.complete(IdHelper.getInstallId());
                }
            }, new Runnable() {
                public void run() {
                    defaultAppCenterFuture.complete(null);
                }
            });
        } else {
            defaultAppCenterFuture.complete(null);
        }
        return defaultAppCenterFuture;
    }

    public static void setUserId(String str) {
        getInstance().setInstanceUserId(str);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public Set<AppCenterService> getServices() {
        return this.mServices;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public Application getApplication() {
        return this.mApplication;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return this.mUncaughtExceptionHandler;
    }

    @VisibleForTesting
    public void setChannel(Channel channel) {
        this.mChannel = channel;
    }
}
