package com.microsoft.appcenter.analytics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.channel.Channel;
import com.microsoft.appcenter.channel.Channel.Listener;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import com.microsoft.appcenter.ingestion.models.one.PartAUtils;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.async.AppCenterFuture;
import com.microsoft.appcenter.utils.async.DefaultAppCenterFuture;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

public class AnalyticsTransmissionTarget {
    @VisibleForTesting
    static AuthenticationProvider sAuthenticationProvider;
    /* access modifiers changed from: private */
    public Channel mChannel;
    /* access modifiers changed from: private */
    public final Map<String, AnalyticsTransmissionTarget> mChildrenTargets = new HashMap();
    Context mContext;
    final AnalyticsTransmissionTarget mParentTarget;
    private final PropertyConfigurator mPropertyConfigurator;
    /* access modifiers changed from: private */
    public final String mTransmissionTargetToken;

    AnalyticsTransmissionTarget(@NonNull String str, AnalyticsTransmissionTarget analyticsTransmissionTarget) {
        this.mTransmissionTargetToken = str;
        this.mParentTarget = analyticsTransmissionTarget;
        this.mPropertyConfigurator = new PropertyConfigurator(this);
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public void initInBackground(Context context, Channel channel) {
        this.mContext = context;
        this.mChannel = channel;
        channel.addListener(this.mPropertyConfigurator);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0054, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static synchronized void addAuthenticationProvider(final com.microsoft.appcenter.analytics.AuthenticationProvider r3) {
        /*
            java.lang.Class<com.microsoft.appcenter.analytics.AnalyticsTransmissionTarget> r0 = com.microsoft.appcenter.analytics.AnalyticsTransmissionTarget.class
            monitor-enter(r0)
            if (r3 != 0) goto L_0x0010
            java.lang.String r3 = "AppCenterAnalytics"
            java.lang.String r1 = "Authentication provider may not be null."
            com.microsoft.appcenter.utils.AppCenterLog.error(r3, r1)     // Catch:{ all -> 0x000e }
            monitor-exit(r0)
            return
        L_0x000e:
            r3 = move-exception
            goto L_0x0055
        L_0x0010:
            com.microsoft.appcenter.analytics.AuthenticationProvider$Type r1 = r3.getType()     // Catch:{ all -> 0x000e }
            if (r1 != 0) goto L_0x001f
            java.lang.String r3 = "AppCenterAnalytics"
            java.lang.String r1 = "Authentication provider type may not be null."
            com.microsoft.appcenter.utils.AppCenterLog.error(r3, r1)     // Catch:{ all -> 0x000e }
            monitor-exit(r0)
            return
        L_0x001f:
            java.lang.String r1 = r3.getTicketKey()     // Catch:{ all -> 0x000e }
            if (r1 != 0) goto L_0x002e
            java.lang.String r3 = "AppCenterAnalytics"
            java.lang.String r1 = "Authentication ticket key may not be null."
            com.microsoft.appcenter.utils.AppCenterLog.error(r3, r1)     // Catch:{ all -> 0x000e }
            monitor-exit(r0)
            return
        L_0x002e:
            com.microsoft.appcenter.analytics.AuthenticationProvider$TokenProvider r1 = r3.getTokenProvider()     // Catch:{ all -> 0x000e }
            if (r1 != 0) goto L_0x003d
            java.lang.String r3 = "AppCenterAnalytics"
            java.lang.String r1 = "Authentication token provider may not be null."
            com.microsoft.appcenter.utils.AppCenterLog.error(r3, r1)     // Catch:{ all -> 0x000e }
            monitor-exit(r0)
            return
        L_0x003d:
            boolean r1 = com.microsoft.appcenter.AppCenter.isConfigured()     // Catch:{ all -> 0x000e }
            if (r1 == 0) goto L_0x0050
            com.microsoft.appcenter.analytics.Analytics r1 = com.microsoft.appcenter.analytics.Analytics.getInstance()     // Catch:{ all -> 0x000e }
            com.microsoft.appcenter.analytics.AnalyticsTransmissionTarget$1 r2 = new com.microsoft.appcenter.analytics.AnalyticsTransmissionTarget$1     // Catch:{ all -> 0x000e }
            r2.<init>(r3)     // Catch:{ all -> 0x000e }
            r1.postCommandEvenIfDisabled(r2)     // Catch:{ all -> 0x000e }
            goto L_0x0053
        L_0x0050:
            updateProvider(r3)     // Catch:{ all -> 0x000e }
        L_0x0053:
            monitor-exit(r0)
            return
        L_0x0055:
            monitor-exit(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.appcenter.analytics.AnalyticsTransmissionTarget.addAuthenticationProvider(com.microsoft.appcenter.analytics.AuthenticationProvider):void");
    }

    /* access modifiers changed from: private */
    public static void updateProvider(AuthenticationProvider authenticationProvider) {
        sAuthenticationProvider = authenticationProvider;
        authenticationProvider.acquireTokenAsync();
    }

    public void trackEvent(String str) {
        trackEvent(str, (EventProperties) null, 1);
    }

    public void trackEvent(String str, Map<String, String> map) {
        trackEvent(str, map, 1);
    }

    public void trackEvent(String str, Map<String, String> map, int i) {
        EventProperties eventProperties;
        if (map != null) {
            eventProperties = new EventProperties();
            for (Entry entry : map.entrySet()) {
                eventProperties.set((String) entry.getKey(), (String) entry.getValue());
            }
        } else {
            eventProperties = null;
        }
        trackEvent(str, eventProperties, i);
    }

    public void trackEvent(String str, EventProperties eventProperties) {
        trackEvent(str, eventProperties, 1);
    }

    public void trackEvent(String str, EventProperties eventProperties, int i) {
        EventProperties eventProperties2 = new EventProperties();
        for (AnalyticsTransmissionTarget analyticsTransmissionTarget = this; analyticsTransmissionTarget != null; analyticsTransmissionTarget = analyticsTransmissionTarget.mParentTarget) {
            analyticsTransmissionTarget.getPropertyConfigurator().mergeEventProperties(eventProperties2);
        }
        if (eventProperties != null) {
            eventProperties2.getProperties().putAll(eventProperties.getProperties());
        } else if (eventProperties2.getProperties().isEmpty()) {
            eventProperties2 = null;
        }
        Analytics.trackEvent(str, eventProperties2, this, i);
    }

    public synchronized AnalyticsTransmissionTarget getTransmissionTarget(String str) {
        final AnalyticsTransmissionTarget analyticsTransmissionTarget;
        analyticsTransmissionTarget = (AnalyticsTransmissionTarget) this.mChildrenTargets.get(str);
        if (analyticsTransmissionTarget == null) {
            analyticsTransmissionTarget = new AnalyticsTransmissionTarget(str, this);
            this.mChildrenTargets.put(str, analyticsTransmissionTarget);
            Analytics.getInstance().postCommandEvenIfDisabled(new Runnable() {
                public void run() {
                    analyticsTransmissionTarget.initInBackground(AnalyticsTransmissionTarget.this.mContext, AnalyticsTransmissionTarget.this.mChannel);
                }
            });
        }
        return analyticsTransmissionTarget;
    }

    public AppCenterFuture<Boolean> isEnabledAsync() {
        final DefaultAppCenterFuture defaultAppCenterFuture = new DefaultAppCenterFuture();
        Analytics.getInstance().postCommand(new Runnable() {
            public void run() {
                defaultAppCenterFuture.complete(Boolean.valueOf(AnalyticsTransmissionTarget.this.isEnabled()));
            }
        }, defaultAppCenterFuture, Boolean.valueOf(false));
        return defaultAppCenterFuture;
    }

    public AppCenterFuture<Void> setEnabledAsync(final boolean z) {
        final DefaultAppCenterFuture defaultAppCenterFuture = new DefaultAppCenterFuture();
        Analytics.getInstance().postCommand(new Runnable() {
            public void run() {
                if (AnalyticsTransmissionTarget.this.areAncestorsEnabled()) {
                    LinkedList linkedList = new LinkedList();
                    linkedList.add(AnalyticsTransmissionTarget.this);
                    while (!linkedList.isEmpty()) {
                        ListIterator listIterator = linkedList.listIterator();
                        while (listIterator.hasNext()) {
                            AnalyticsTransmissionTarget analyticsTransmissionTarget = (AnalyticsTransmissionTarget) listIterator.next();
                            listIterator.remove();
                            SharedPreferencesManager.putBoolean(analyticsTransmissionTarget.getEnabledPreferenceKey(), z);
                            for (AnalyticsTransmissionTarget add : analyticsTransmissionTarget.mChildrenTargets.values()) {
                                listIterator.add(add);
                            }
                        }
                    }
                } else {
                    AppCenterLog.error(Analytics.LOG_TAG, "One of the parent transmission target is disabled, cannot change state.");
                }
                defaultAppCenterFuture.complete(null);
            }
        }, defaultAppCenterFuture, null);
        return defaultAppCenterFuture;
    }

    public void pause() {
        Analytics.getInstance().post(new Runnable() {
            public void run() {
                AnalyticsTransmissionTarget.this.mChannel.pauseGroup("group_analytics", AnalyticsTransmissionTarget.this.mTransmissionTargetToken);
                AnalyticsTransmissionTarget.this.mChannel.pauseGroup("group_analytics_critical", AnalyticsTransmissionTarget.this.mTransmissionTargetToken);
            }
        });
    }

    public void resume() {
        Analytics.getInstance().post(new Runnable() {
            public void run() {
                AnalyticsTransmissionTarget.this.mChannel.resumeGroup("group_analytics", AnalyticsTransmissionTarget.this.mTransmissionTargetToken);
                AnalyticsTransmissionTarget.this.mChannel.resumeGroup("group_analytics_critical", AnalyticsTransmissionTarget.this.mTransmissionTargetToken);
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public String getTransmissionTargetToken() {
        return this.mTransmissionTargetToken;
    }

    static Listener getChannelListener() {
        return new AbstractChannelListener() {
            public void onPreparingLog(@NonNull Log log, @NonNull String str) {
                AnalyticsTransmissionTarget.addTicketToLog(log);
            }
        };
    }

    /* access modifiers changed from: private */
    public static void addTicketToLog(@NonNull Log log) {
        if (sAuthenticationProvider != null && (log instanceof CommonSchemaLog)) {
            ((CommonSchemaLog) log).getExt().getProtocol().setTicketKeys(Collections.singletonList(sAuthenticationProvider.getTicketKeyHash()));
            sAuthenticationProvider.checkTokenExpiry();
        }
    }

    /* access modifiers changed from: private */
    @NonNull
    public String getEnabledPreferenceKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(Analytics.getInstance().getEnabledPreferenceKeyPrefix());
        sb.append(PartAUtils.getTargetKey(this.mTransmissionTargetToken));
        return sb.toString();
    }

    @WorkerThread
    private boolean isEnabledInStorage() {
        return SharedPreferencesManager.getBoolean(getEnabledPreferenceKey(), true);
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public boolean areAncestorsEnabled() {
        for (AnalyticsTransmissionTarget analyticsTransmissionTarget = this.mParentTarget; analyticsTransmissionTarget != null; analyticsTransmissionTarget = analyticsTransmissionTarget.mParentTarget) {
            if (!analyticsTransmissionTarget.isEnabledInStorage()) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public boolean isEnabled() {
        return areAncestorsEnabled() && isEnabledInStorage();
    }

    public PropertyConfigurator getPropertyConfigurator() {
        return this.mPropertyConfigurator;
    }
}
