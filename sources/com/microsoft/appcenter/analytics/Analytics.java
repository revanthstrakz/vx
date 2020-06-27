package com.microsoft.appcenter.analytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import com.microsoft.appcenter.AbstractAppCenterService;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.Flags;
import com.microsoft.appcenter.analytics.channel.AnalyticsListener;
import com.microsoft.appcenter.analytics.channel.AnalyticsValidator;
import com.microsoft.appcenter.analytics.channel.SessionTracker;
import com.microsoft.appcenter.analytics.ingestion.models.EventLog;
import com.microsoft.appcenter.analytics.ingestion.models.PageLog;
import com.microsoft.appcenter.analytics.ingestion.models.StartSessionLog;
import com.microsoft.appcenter.analytics.ingestion.models.json.EventLogFactory;
import com.microsoft.appcenter.analytics.ingestion.models.json.PageLogFactory;
import com.microsoft.appcenter.analytics.ingestion.models.json.StartSessionLogFactory;
import com.microsoft.appcenter.analytics.ingestion.models.one.CommonSchemaEventLog;
import com.microsoft.appcenter.analytics.ingestion.models.one.json.CommonSchemaEventLogFactory;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.channel.Channel.GroupListener;
import com.microsoft.appcenter.channel.Channel.Listener;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.json.LogFactory;
import com.microsoft.appcenter.ingestion.models.properties.StringTypedProperty;
import com.microsoft.appcenter.ingestion.models.properties.TypedProperty;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.microsoft.appcenter.utils.async.DefaultAppCenterFuture;
import com.microsoft.appcenter.utils.context.UserIdContext;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Analytics extends AbstractAppCenterService {
    private static final String ACTIVITY_SUFFIX = "Activity";
    static final String ANALYTICS_CRITICAL_GROUP = "group_analytics_critical";
    static final String ANALYTICS_GROUP = "group_analytics";
    public static final String LOG_TAG = "AppCenterAnalytics";
    @VisibleForTesting
    static final int MAXIMUM_TRANSMISSION_INTERVAL_IN_SECONDS = 86400;
    @VisibleForTesting
    static final int MINIMUM_TRANSMISSION_INTERVAL_IN_SECONDS = 3;
    private static final String SERVICE_NAME = "Analytics";
    @SuppressLint({"StaticFieldLeak"})
    private static Analytics sInstance;
    /* access modifiers changed from: private */
    public AnalyticsListener mAnalyticsListener;
    private Listener mAnalyticsTransmissionTargetListener;
    private AnalyticsValidator mAnalyticsValidator;
    private boolean mAutoPageTrackingEnabled = false;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public WeakReference<Activity> mCurrentActivity;
    @VisibleForTesting
    AnalyticsTransmissionTarget mDefaultTransmissionTarget;
    private final Map<String, LogFactory> mFactories = new HashMap();
    /* access modifiers changed from: private */
    public SessionTracker mSessionTracker;
    /* access modifiers changed from: private */
    public boolean mStartedFromApp;
    private long mTransmissionInterval;
    private final Map<String, AnalyticsTransmissionTarget> mTransmissionTargets;

    /* access modifiers changed from: protected */
    public String getGroupName() {
        return ANALYTICS_GROUP;
    }

    /* access modifiers changed from: protected */
    public String getLoggerTag() {
        return LOG_TAG;
    }

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public boolean isAppSecretRequired() {
        return false;
    }

    private Analytics() {
        this.mFactories.put(StartSessionLog.TYPE, new StartSessionLogFactory());
        this.mFactories.put(PageLog.TYPE, new PageLogFactory());
        this.mFactories.put("event", new EventLogFactory());
        this.mFactories.put(CommonSchemaEventLog.TYPE, new CommonSchemaEventLogFactory());
        this.mTransmissionTargets = new HashMap();
        this.mTransmissionInterval = TimeUnit.SECONDS.toMillis(3);
    }

    public static synchronized Analytics getInstance() {
        Analytics analytics;
        synchronized (Analytics.class) {
            if (sInstance == null) {
                sInstance = new Analytics();
            }
            analytics = sInstance;
        }
        return analytics;
    }

    @VisibleForTesting
    static synchronized void unsetInstance() {
        synchronized (Analytics.class) {
            sInstance = null;
        }
    }

    public static AppCenterFuture<Boolean> isEnabled() {
        return getInstance().isInstanceEnabledAsync();
    }

    public static AppCenterFuture<Void> setEnabled(boolean z) {
        return getInstance().setInstanceEnabledAsync(z);
    }

    public static boolean setTransmissionInterval(int i) {
        return getInstance().setInstanceTransmissionInterval(i);
    }

    public static void pause() {
        getInstance().pauseInstanceAsync();
    }

    public static void resume() {
        getInstance().resumeInstanceAsync();
    }

    @VisibleForTesting
    protected static void setListener(AnalyticsListener analyticsListener) {
        getInstance().setInstanceListener(analyticsListener);
    }

    protected static boolean isAutoPageTrackingEnabled() {
        return getInstance().isInstanceAutoPageTrackingEnabled();
    }

    protected static void setAutoPageTrackingEnabled(boolean z) {
        getInstance().setInstanceAutoPageTrackingEnabled(z);
    }

    protected static void trackPage(String str) {
        trackPage(str, null);
    }

    protected static void trackPage(String str, Map<String, String> map) {
        getInstance().trackPageAsync(str, map);
    }

    public static void trackEvent(String str) {
        trackEvent(str, null, null, 1);
    }

    public static void trackEvent(String str, Map<String, String> map) {
        getInstance().trackEventAsync(str, convertProperties(map), null, 1);
    }

    public static void trackEvent(String str, Map<String, String> map, int i) {
        getInstance().trackEventAsync(str, convertProperties(map), null, i);
    }

    public static void trackEvent(String str, EventProperties eventProperties) {
        trackEvent(str, eventProperties, 1);
    }

    public static void trackEvent(String str, EventProperties eventProperties, int i) {
        trackEvent(str, eventProperties, null, i);
    }

    static void trackEvent(String str, EventProperties eventProperties, AnalyticsTransmissionTarget analyticsTransmissionTarget, int i) {
        getInstance().trackEventAsync(str, convertProperties(eventProperties), analyticsTransmissionTarget, i);
    }

    private static List<TypedProperty> convertProperties(EventProperties eventProperties) {
        if (eventProperties == null) {
            return null;
        }
        return new ArrayList(eventProperties.getProperties().values());
    }

    private static List<TypedProperty> convertProperties(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(map.size());
        for (Entry entry : map.entrySet()) {
            StringTypedProperty stringTypedProperty = new StringTypedProperty();
            stringTypedProperty.setName((String) entry.getKey());
            stringTypedProperty.setValue((String) entry.getValue());
            arrayList.add(stringTypedProperty);
        }
        return arrayList;
    }

    public static AnalyticsTransmissionTarget getTransmissionTarget(String str) {
        return getInstance().getInstanceTransmissionTarget(str);
    }

    private static String generatePageName(Class<?> cls) {
        String simpleName = cls.getSimpleName();
        String str = ACTIVITY_SUFFIX;
        return (!simpleName.endsWith(str) || simpleName.length() <= str.length()) ? simpleName : simpleName.substring(0, simpleName.length() - str.length());
    }

    private synchronized AnalyticsTransmissionTarget getInstanceTransmissionTarget(String str) {
        if (str != null) {
            if (!str.isEmpty()) {
                if (!AppCenter.isConfigured()) {
                    AppCenterLog.error(LOG_TAG, "Cannot create transmission target, AppCenter is not configured or started.");
                    return null;
                }
                AnalyticsTransmissionTarget analyticsTransmissionTarget = (AnalyticsTransmissionTarget) this.mTransmissionTargets.get(str);
                if (analyticsTransmissionTarget != null) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Returning transmission target found with token ");
                    sb.append(str);
                    AppCenterLog.debug(str2, sb.toString());
                    return analyticsTransmissionTarget;
                }
                AnalyticsTransmissionTarget createAnalyticsTransmissionTarget = createAnalyticsTransmissionTarget(str);
                this.mTransmissionTargets.put(str, createAnalyticsTransmissionTarget);
                return createAnalyticsTransmissionTarget;
            }
        }
        AppCenterLog.error(LOG_TAG, "Transmission target token may not be null or empty.");
        return null;
    }

    private AnalyticsTransmissionTarget createAnalyticsTransmissionTarget(String str) {
        final AnalyticsTransmissionTarget analyticsTransmissionTarget = new AnalyticsTransmissionTarget(str, null);
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Created transmission target with token ");
        sb.append(str);
        AppCenterLog.debug(str2, sb.toString());
        postCommandEvenIfDisabled(new Runnable() {
            public void run() {
                analyticsTransmissionTarget.initInBackground(Analytics.this.mContext, Analytics.this.mChannel);
            }
        });
        return analyticsTransmissionTarget;
    }

    public Map<String, LogFactory> getLogFactories() {
        return this.mFactories;
    }

    public synchronized void onActivityResumed(final Activity activity) {
        final C11652 r0 = new Runnable() {
            public void run() {
                Analytics.this.mCurrentActivity = new WeakReference(activity);
            }
        };
        post(new Runnable() {
            public void run() {
                r0.run();
                Analytics.this.processOnResume(activity);
            }
        }, r0, r0);
    }

    /* access modifiers changed from: protected */
    public long getTriggerInterval() {
        return this.mTransmissionInterval;
    }

    /* access modifiers changed from: private */
    public void processOnResume(Activity activity) {
        if (this.mSessionTracker != null) {
            this.mSessionTracker.onActivityResumed();
            if (this.mAutoPageTrackingEnabled) {
                queuePage(generatePageName(activity.getClass()), null);
            }
        }
    }

    public synchronized void onActivityPaused(Activity activity) {
        final C11674 r2 = new Runnable() {
            public void run() {
                Analytics.this.mCurrentActivity = null;
            }
        };
        post(new Runnable() {
            public void run() {
                r2.run();
                if (Analytics.this.mSessionTracker != null) {
                    Analytics.this.mSessionTracker.onActivityPaused();
                }
            }
        }, r2, r2);
    }

    /* access modifiers changed from: protected */
    public GroupListener getChannelListener() {
        return new GroupListener() {
            public void onBeforeSending(Log log) {
                if (Analytics.this.mAnalyticsListener != null) {
                    Analytics.this.mAnalyticsListener.onBeforeSending(log);
                }
            }

            public void onSuccess(Log log) {
                if (Analytics.this.mAnalyticsListener != null) {
                    Analytics.this.mAnalyticsListener.onSendingSucceeded(log);
                }
            }

            public void onFailure(Log log, Exception exc) {
                if (Analytics.this.mAnalyticsListener != null) {
                    Analytics.this.mAnalyticsListener.onSendingFailed(log, exc);
                }
            }
        };
    }

    /* access modifiers changed from: protected */
    public synchronized void applyEnabledState(boolean z) {
        if (z) {
            try {
                this.mChannel.addGroup(ANALYTICS_CRITICAL_GROUP, getTriggerCount(), 3000, getTriggerMaxParallelRequests(), null, getChannelListener());
                startAppLevelFeatures();
            } catch (Throwable th) {
                throw th;
            }
        } else {
            this.mChannel.removeGroup(ANALYTICS_CRITICAL_GROUP);
            if (this.mAnalyticsValidator != null) {
                this.mChannel.removeListener(this.mAnalyticsValidator);
                this.mAnalyticsValidator = null;
            }
            if (this.mSessionTracker != null) {
                this.mChannel.removeListener(this.mSessionTracker);
                this.mSessionTracker.clearSessions();
                this.mSessionTracker = null;
            }
            if (this.mAnalyticsTransmissionTargetListener != null) {
                this.mChannel.removeListener(this.mAnalyticsTransmissionTargetListener);
                this.mAnalyticsTransmissionTargetListener = null;
            }
        }
    }

    @WorkerThread
    private void startAppLevelFeatures() {
        if (this.mStartedFromApp) {
            this.mAnalyticsValidator = new AnalyticsValidator();
            this.mChannel.addListener(this.mAnalyticsValidator);
            this.mSessionTracker = new SessionTracker(this.mChannel, ANALYTICS_GROUP);
            this.mChannel.addListener(this.mSessionTracker);
            if (this.mCurrentActivity != null) {
                Activity activity = (Activity) this.mCurrentActivity.get();
                if (activity != null) {
                    processOnResume(activity);
                }
            }
            this.mAnalyticsTransmissionTargetListener = AnalyticsTransmissionTarget.getChannelListener();
            this.mChannel.addListener(this.mAnalyticsTransmissionTargetListener);
        }
    }

    private synchronized void trackPageAsync(final String str, Map<String, String> map) {
        final HashMap hashMap;
        if (map != null) {
            try {
                hashMap = new HashMap(map);
            } catch (Throwable th) {
                throw th;
            }
        } else {
            hashMap = null;
        }
        post(new Runnable() {
            public void run() {
                if (Analytics.this.mStartedFromApp) {
                    Analytics.this.queuePage(str, hashMap);
                } else {
                    AppCenterLog.error(Analytics.LOG_TAG, "Cannot track page if not started from app.");
                }
            }
        });
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public void queuePage(String str, Map<String, String> map) {
        PageLog pageLog = new PageLog();
        pageLog.setName(str);
        pageLog.setProperties(map);
        this.mChannel.enqueue(pageLog, ANALYTICS_GROUP, 1);
    }

    private synchronized void trackEventAsync(String str, List<TypedProperty> list, AnalyticsTransmissionTarget analyticsTransmissionTarget, int i) {
        final String userId = UserIdContext.getInstance().getUserId();
        final AnalyticsTransmissionTarget analyticsTransmissionTarget2 = analyticsTransmissionTarget;
        final String str2 = str;
        final List<TypedProperty> list2 = list;
        final int i2 = i;
        C11718 r1 = new Runnable() {
            public void run() {
                AnalyticsTransmissionTarget analyticsTransmissionTarget = analyticsTransmissionTarget2 == null ? Analytics.this.mDefaultTransmissionTarget : analyticsTransmissionTarget2;
                EventLog eventLog = new EventLog();
                if (analyticsTransmissionTarget != null) {
                    if (analyticsTransmissionTarget.isEnabled()) {
                        eventLog.addTransmissionTarget(analyticsTransmissionTarget.getTransmissionTargetToken());
                        eventLog.setTag(analyticsTransmissionTarget);
                        if (analyticsTransmissionTarget == Analytics.this.mDefaultTransmissionTarget) {
                            eventLog.setUserId(userId);
                        }
                    } else {
                        AppCenterLog.error(Analytics.LOG_TAG, "This transmission target is disabled.");
                        return;
                    }
                } else if (!Analytics.this.mStartedFromApp) {
                    AppCenterLog.error(Analytics.LOG_TAG, "Cannot track event using Analytics.trackEvent if not started from app, please start from the application or use Analytics.getTransmissionTarget.");
                    return;
                }
                eventLog.setId(UUID.randomUUID());
                eventLog.setName(str2);
                eventLog.setTypedProperties(list2);
                int persistenceFlag = Flags.getPersistenceFlag(i2, true);
                Analytics.this.mChannel.enqueue(eventLog, persistenceFlag == 2 ? Analytics.ANALYTICS_CRITICAL_GROUP : Analytics.ANALYTICS_GROUP, persistenceFlag);
            }
        };
        post(r1);
    }

    private boolean isInstanceAutoPageTrackingEnabled() {
        return this.mAutoPageTrackingEnabled;
    }

    private synchronized void setInstanceAutoPageTrackingEnabled(boolean z) {
        this.mAutoPageTrackingEnabled = z;
    }

    private synchronized void setInstanceListener(AnalyticsListener analyticsListener) {
        this.mAnalyticsListener = analyticsListener;
    }

    private synchronized void pauseInstanceAsync() {
        post(new Runnable() {
            public void run() {
                Analytics.this.mChannel.pauseGroup(Analytics.ANALYTICS_GROUP, null);
                Analytics.this.mChannel.pauseGroup(Analytics.ANALYTICS_CRITICAL_GROUP, null);
            }
        });
    }

    private synchronized void resumeInstanceAsync() {
        post(new Runnable() {
            public void run() {
                Analytics.this.mChannel.resumeGroup(Analytics.ANALYTICS_GROUP, null);
                Analytics.this.mChannel.resumeGroup(Analytics.ANALYTICS_CRITICAL_GROUP, null);
            }
        });
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public WeakReference<Activity> getCurrentActivity() {
        return this.mCurrentActivity;
    }

    public synchronized void onStarted(@NonNull Context context, @NonNull Channel channel, String str, String str2, boolean z) {
        this.mContext = context;
        this.mStartedFromApp = z;
        super.onStarted(context, channel, str, str2, z);
        setDefaultTransmissionTarget(str2);
    }

    public void onConfigurationUpdated(String str, String str2) {
        this.mStartedFromApp = true;
        startAppLevelFeatures();
        setDefaultTransmissionTarget(str2);
    }

    @WorkerThread
    private void setDefaultTransmissionTarget(String str) {
        if (str != null) {
            this.mDefaultTransmissionTarget = createAnalyticsTransmissionTarget(str);
        }
    }

    private boolean setInstanceTransmissionInterval(int i) {
        if (this.mChannel != null) {
            AppCenterLog.error(LOG_TAG, "Transmission interval should be set before the service is started.");
            return false;
        } else if (i < 3 || i > MAXIMUM_TRANSMISSION_INTERVAL_IN_SECONDS) {
            AppCenterLog.error(LOG_TAG, String.format(Locale.ENGLISH, "The transmission interval is invalid. The value should be between %d seconds and %d seconds (%d day).", new Object[]{Integer.valueOf(3), Integer.valueOf(MAXIMUM_TRANSMISSION_INTERVAL_IN_SECONDS), Long.valueOf(TimeUnit.SECONDS.toDays(86400))}));
            return false;
        } else {
            this.mTransmissionInterval = TimeUnit.SECONDS.toMillis((long) i);
            return true;
        }
    }

    /* access modifiers changed from: 0000 */
    public <T> void postCommand(Runnable runnable, DefaultAppCenterFuture<T> defaultAppCenterFuture, T t) {
        postAsyncGetter(runnable, defaultAppCenterFuture, t);
    }

    /* access modifiers changed from: protected */
    public synchronized void post(Runnable runnable) {
        super.post(runnable);
    }

    /* access modifiers changed from: 0000 */
    public void postCommandEvenIfDisabled(Runnable runnable) {
        post(runnable, runnable, runnable);
    }

    /* access modifiers changed from: 0000 */
    public String getEnabledPreferenceKeyPrefix() {
        StringBuilder sb = new StringBuilder();
        sb.append(getEnabledPreferenceKey());
        sb.append("/");
        return sb.toString();
    }
}
