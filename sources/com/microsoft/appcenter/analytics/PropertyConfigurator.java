package com.microsoft.appcenter.analytics;

import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import com.microsoft.appcenter.channel.AbstractChannelListener;
import com.microsoft.appcenter.ingestion.models.Log;
import com.microsoft.appcenter.ingestion.models.one.AppExtension;
import com.microsoft.appcenter.ingestion.models.one.CommonSchemaLog;
import com.microsoft.appcenter.ingestion.models.one.DeviceExtension;
import com.microsoft.appcenter.ingestion.models.one.UserExtension;
import com.microsoft.appcenter.utils.context.UserIdContext;
import java.util.Date;
import java.util.Map.Entry;

public class PropertyConfigurator extends AbstractChannelListener {
    private static final String ANDROID_DEVICE_ID_PREFIX = "a:";
    /* access modifiers changed from: private */
    public String mAppLocale;
    /* access modifiers changed from: private */
    public String mAppName;
    /* access modifiers changed from: private */
    public String mAppVersion;
    /* access modifiers changed from: private */
    public boolean mDeviceIdEnabled;
    private final EventProperties mEventProperties = new EventProperties();
    private final AnalyticsTransmissionTarget mTransmissionTarget;
    /* access modifiers changed from: private */
    public String mUserId;

    PropertyConfigurator(AnalyticsTransmissionTarget analyticsTransmissionTarget) {
        this.mTransmissionTarget = analyticsTransmissionTarget;
    }

    public void onPreparingLog(@NonNull Log log, @NonNull String str) {
        if (shouldOverridePartAProperties(log)) {
            CommonSchemaLog commonSchemaLog = (CommonSchemaLog) log;
            AppExtension app = commonSchemaLog.getExt().getApp();
            UserExtension user = commonSchemaLog.getExt().getUser();
            DeviceExtension device = commonSchemaLog.getExt().getDevice();
            if (this.mAppName == null) {
                AnalyticsTransmissionTarget analyticsTransmissionTarget = this.mTransmissionTarget;
                while (true) {
                    analyticsTransmissionTarget = analyticsTransmissionTarget.mParentTarget;
                    if (analyticsTransmissionTarget == null) {
                        break;
                    }
                    String appName = analyticsTransmissionTarget.getPropertyConfigurator().getAppName();
                    if (appName != null) {
                        app.setName(appName);
                        break;
                    }
                }
            } else {
                app.setName(this.mAppName);
            }
            if (this.mAppVersion == null) {
                AnalyticsTransmissionTarget analyticsTransmissionTarget2 = this.mTransmissionTarget;
                while (true) {
                    analyticsTransmissionTarget2 = analyticsTransmissionTarget2.mParentTarget;
                    if (analyticsTransmissionTarget2 == null) {
                        break;
                    }
                    String appVersion = analyticsTransmissionTarget2.getPropertyConfigurator().getAppVersion();
                    if (appVersion != null) {
                        app.setVer(appVersion);
                        break;
                    }
                }
            } else {
                app.setVer(this.mAppVersion);
            }
            if (this.mAppLocale == null) {
                AnalyticsTransmissionTarget analyticsTransmissionTarget3 = this.mTransmissionTarget;
                while (true) {
                    analyticsTransmissionTarget3 = analyticsTransmissionTarget3.mParentTarget;
                    if (analyticsTransmissionTarget3 == null) {
                        break;
                    }
                    String appLocale = analyticsTransmissionTarget3.getPropertyConfigurator().getAppLocale();
                    if (appLocale != null) {
                        app.setLocale(appLocale);
                        break;
                    }
                }
            } else {
                app.setLocale(this.mAppLocale);
            }
            if (this.mUserId == null) {
                AnalyticsTransmissionTarget analyticsTransmissionTarget4 = this.mTransmissionTarget;
                while (true) {
                    analyticsTransmissionTarget4 = analyticsTransmissionTarget4.mParentTarget;
                    if (analyticsTransmissionTarget4 == null) {
                        break;
                    }
                    String userId = analyticsTransmissionTarget4.getPropertyConfigurator().getUserId();
                    if (userId != null) {
                        user.setLocalId(userId);
                        break;
                    }
                }
            } else {
                user.setLocalId(this.mUserId);
            }
            if (this.mDeviceIdEnabled) {
                String string = Secure.getString(this.mTransmissionTarget.mContext.getContentResolver(), "android_id");
                StringBuilder sb = new StringBuilder();
                sb.append(ANDROID_DEVICE_ID_PREFIX);
                sb.append(string);
                device.setLocalId(sb.toString());
            }
        }
    }

    private boolean shouldOverridePartAProperties(@NonNull Log log) {
        return (log instanceof CommonSchemaLog) && log.getTag() == this.mTransmissionTarget && this.mTransmissionTarget.isEnabled();
    }

    private String getAppName() {
        return this.mAppName;
    }

    public void setAppName(final String str) {
        Analytics.getInstance().postCommandEvenIfDisabled(new Runnable() {
            public void run() {
                PropertyConfigurator.this.mAppName = str;
            }
        });
    }

    private String getAppVersion() {
        return this.mAppVersion;
    }

    public void setAppVersion(final String str) {
        Analytics.getInstance().postCommandEvenIfDisabled(new Runnable() {
            public void run() {
                PropertyConfigurator.this.mAppVersion = str;
            }
        });
    }

    private String getAppLocale() {
        return this.mAppLocale;
    }

    public void setAppLocale(final String str) {
        Analytics.getInstance().postCommandEvenIfDisabled(new Runnable() {
            public void run() {
                PropertyConfigurator.this.mAppLocale = str;
            }
        });
    }

    private String getUserId() {
        return this.mUserId;
    }

    public void setUserId(final String str) {
        if (UserIdContext.checkUserIdValidForOneCollector(str)) {
            Analytics.getInstance().postCommandEvenIfDisabled(new Runnable() {
                public void run() {
                    PropertyConfigurator.this.mUserId = UserIdContext.getPrefixedUserId(str);
                }
            });
        }
    }

    public synchronized void setEventProperty(String str, boolean z) {
        this.mEventProperties.set(str, z);
    }

    public synchronized void setEventProperty(String str, Date date) {
        this.mEventProperties.set(str, date);
    }

    public synchronized void setEventProperty(String str, double d) {
        this.mEventProperties.set(str, d);
    }

    public synchronized void setEventProperty(String str, long j) {
        this.mEventProperties.set(str, j);
    }

    public synchronized void setEventProperty(String str, String str2) {
        this.mEventProperties.set(str, str2);
    }

    public synchronized void removeEventProperty(String str) {
        this.mEventProperties.getProperties().remove(str);
    }

    public void collectDeviceId() {
        Analytics.getInstance().postCommandEvenIfDisabled(new Runnable() {
            public void run() {
                PropertyConfigurator.this.mDeviceIdEnabled = true;
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public synchronized void mergeEventProperties(EventProperties eventProperties) {
        for (Entry entry : this.mEventProperties.getProperties().entrySet()) {
            String str = (String) entry.getKey();
            if (!eventProperties.getProperties().containsKey(str)) {
                eventProperties.getProperties().put(str, entry.getValue());
            }
        }
    }
}
