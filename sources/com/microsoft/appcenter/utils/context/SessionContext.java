package com.microsoft.appcenter.utils.context;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.WorkerThread;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.storage.SharedPreferencesManager;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

public class SessionContext {
    private static final String STORAGE_KEY = "sessions";
    private static final String STORAGE_KEY_VALUE_SEPARATOR = "/";
    private static final int STORAGE_MAX_SESSIONS = 10;
    private static SessionContext sInstance;
    private final long mAppLaunchTimestamp = System.currentTimeMillis();
    private final NavigableMap<Long, SessionInfo> mSessions = new TreeMap();

    public static class SessionInfo {
        private final long mAppLaunchTimestamp;
        private final UUID mSessionId;
        private final long mTimestamp;

        SessionInfo(long j, UUID uuid, long j2) {
            this.mTimestamp = j;
            this.mSessionId = uuid;
            this.mAppLaunchTimestamp = j2;
        }

        /* access modifiers changed from: 0000 */
        public long getTimestamp() {
            return this.mTimestamp;
        }

        public UUID getSessionId() {
            return this.mSessionId;
        }

        public long getAppLaunchTimestamp() {
            return this.mAppLaunchTimestamp;
        }

        @NonNull
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getTimestamp());
            sb.append(SessionContext.STORAGE_KEY_VALUE_SEPARATOR);
            String sb2 = sb.toString();
            if (getSessionId() != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(sb2);
                sb3.append(getSessionId());
                sb2 = sb3.toString();
            }
            StringBuilder sb4 = new StringBuilder();
            sb4.append(sb2);
            sb4.append(SessionContext.STORAGE_KEY_VALUE_SEPARATOR);
            sb4.append(getAppLaunchTimestamp());
            return sb4.toString();
        }
    }

    @WorkerThread
    private SessionContext() {
        Set<String> stringSet = SharedPreferencesManager.getStringSet(STORAGE_KEY);
        if (stringSet != null) {
            for (String str : stringSet) {
                String[] split = str.split(STORAGE_KEY_VALUE_SEPARATOR, -1);
                try {
                    long parseLong = Long.parseLong(split[0]);
                    String str2 = split[1];
                    UUID fromString = str2.isEmpty() ? null : UUID.fromString(str2);
                    long parseLong2 = split.length > 2 ? Long.parseLong(split[2]) : parseLong;
                    NavigableMap<Long, SessionInfo> navigableMap = this.mSessions;
                    Long valueOf = Long.valueOf(parseLong);
                    SessionInfo sessionInfo = new SessionInfo(parseLong, fromString, parseLong2);
                    navigableMap.put(valueOf, sessionInfo);
                } catch (RuntimeException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Ignore invalid session in store: ");
                    sb.append(str);
                    AppCenterLog.warn("AppCenter", sb.toString(), e);
                }
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Loaded stored sessions: ");
        sb2.append(this.mSessions);
        AppCenterLog.debug("AppCenter", sb2.toString());
        addSession(null);
    }

    @WorkerThread
    public static synchronized SessionContext getInstance() {
        SessionContext sessionContext;
        synchronized (SessionContext.class) {
            if (sInstance == null) {
                sInstance = new SessionContext();
            }
            sessionContext = sInstance;
        }
        return sessionContext;
    }

    @VisibleForTesting
    public static synchronized void unsetInstance() {
        synchronized (SessionContext.class) {
            sInstance = null;
        }
    }

    public synchronized void addSession(UUID uuid) {
        long currentTimeMillis = System.currentTimeMillis();
        NavigableMap<Long, SessionInfo> navigableMap = this.mSessions;
        Long valueOf = Long.valueOf(currentTimeMillis);
        SessionInfo sessionInfo = new SessionInfo(currentTimeMillis, uuid, this.mAppLaunchTimestamp);
        navigableMap.put(valueOf, sessionInfo);
        if (this.mSessions.size() > 10) {
            this.mSessions.pollFirstEntry();
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (SessionInfo sessionInfo2 : this.mSessions.values()) {
            linkedHashSet.add(sessionInfo2.toString());
        }
        SharedPreferencesManager.putStringSet(STORAGE_KEY, linkedHashSet);
    }

    public synchronized SessionInfo getSessionAt(long j) {
        Entry floorEntry = this.mSessions.floorEntry(Long.valueOf(j));
        if (floorEntry == null) {
            return null;
        }
        return (SessionInfo) floorEntry.getValue();
    }

    public synchronized void clearSessions() {
        this.mSessions.clear();
        SharedPreferencesManager.remove(STORAGE_KEY);
    }
}
