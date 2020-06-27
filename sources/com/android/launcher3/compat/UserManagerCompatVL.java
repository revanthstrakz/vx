package com.android.launcher3.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.ManagedProfileHeuristic;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import java.util.List;

public class UserManagerCompatVL extends UserManagerCompat {
    private static final String USER_CREATION_TIME_KEY = "user_creation_time_";
    private final Context mContext;
    private final PackageManager mPm;
    protected final UserManager mUserManager;
    protected ArrayMap<UserHandle, Long> mUserToSerialMap;
    protected LongArrayMap<UserHandle> mUsers;

    public boolean isDemoUser() {
        return false;
    }

    public boolean isQuietModeEnabled(UserHandle userHandle) {
        return false;
    }

    public boolean isUserUnlocked(UserHandle userHandle) {
        return true;
    }

    UserManagerCompatVL(Context context) {
        this.mUserManager = (UserManager) context.getSystemService(ServiceManagerNative.USER);
        this.mPm = context.getPackageManager();
        this.mContext = context;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0017, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getSerialNumberForUser(android.os.UserHandle r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r0 = r2.mUserToSerialMap     // Catch:{ all -> 0x0020 }
            if (r0 == 0) goto L_0x0018
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r0 = r2.mUserToSerialMap     // Catch:{ all -> 0x0020 }
            java.lang.Object r3 = r0.get(r3)     // Catch:{ all -> 0x0020 }
            java.lang.Long r3 = (java.lang.Long) r3     // Catch:{ all -> 0x0020 }
            if (r3 != 0) goto L_0x0012
            r0 = 0
            goto L_0x0016
        L_0x0012:
            long r0 = r3.longValue()     // Catch:{ all -> 0x0020 }
        L_0x0016:
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            return r0
        L_0x0018:
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            android.os.UserManager r0 = r2.mUserManager
            long r0 = r0.getSerialNumberForUser(r3)
            return r0
        L_0x0020:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.compat.UserManagerCompatVL.getSerialNumberForUser(android.os.UserHandle):long");
    }

    public UserHandle getUserForSerialNumber(long j) {
        synchronized (this) {
            if (this.mUsers == null) {
                return this.mUserManager.getUserForSerialNumber(j);
            }
            UserHandle userHandle = (UserHandle) this.mUsers.get(j);
            return userHandle;
        }
    }

    public void enableAndResetCache() {
        synchronized (this) {
            this.mUsers = new LongArrayMap<>();
            this.mUserToSerialMap = new ArrayMap<>();
            List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
            if (userProfiles != null) {
                for (UserHandle userHandle : userProfiles) {
                    long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandle);
                    this.mUsers.put(serialNumberForUser, userHandle);
                    this.mUserToSerialMap.put(userHandle, Long.valueOf(serialNumberForUser));
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001b, code lost:
        r0 = java.util.Collections.emptyList();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001f, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0013, code lost:
        r0 = r2.mUserManager.getUserProfiles();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        if (r0 != null) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.os.UserHandle> getUserProfiles() {
        /*
            r2 = this;
            monitor-enter(r2)
            com.android.launcher3.util.LongArrayMap<android.os.UserHandle> r0 = r2.mUsers     // Catch:{ all -> 0x0020 }
            if (r0 == 0) goto L_0x0012
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x0020 }
            android.util.ArrayMap<android.os.UserHandle, java.lang.Long> r1 = r2.mUserToSerialMap     // Catch:{ all -> 0x0020 }
            java.util.Set r1 = r1.keySet()     // Catch:{ all -> 0x0020 }
            r0.<init>(r1)     // Catch:{ all -> 0x0020 }
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            return r0
        L_0x0012:
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            android.os.UserManager r0 = r2.mUserManager
            java.util.List r0 = r0.getUserProfiles()
            if (r0 != 0) goto L_0x001f
            java.util.List r0 = java.util.Collections.emptyList()
        L_0x001f:
            return r0
        L_0x0020:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0020 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.compat.UserManagerCompatVL.getUserProfiles():java.util.List");
    }

    public CharSequence getBadgedLabelForUser(CharSequence charSequence, UserHandle userHandle) {
        return userHandle == null ? charSequence : this.mPm.getUserBadgedLabel(charSequence, userHandle);
    }

    public long getUserCreationTime(UserHandle userHandle) {
        SharedPreferences prefs = ManagedProfileHeuristic.prefs(this.mContext);
        StringBuilder sb = new StringBuilder();
        sb.append(USER_CREATION_TIME_KEY);
        sb.append(getSerialNumberForUser(userHandle));
        String sb2 = sb.toString();
        if (!prefs.contains(sb2)) {
            prefs.edit().putLong(sb2, System.currentTimeMillis()).apply();
        }
        return prefs.getLong(sb2, 0);
    }
}
