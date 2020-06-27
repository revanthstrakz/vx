package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import com.android.launcher3.util.SQLiteCacheHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

public class IconCache {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_IGNORE_CACHE = false;
    public static final String EMPTY_CLASS_NAME = ".";
    static final Object ICON_UPDATE_TOKEN = new Object();
    private static final int INITIAL_ICON_CACHE_CAPACITY = 50;
    private static final int LOW_RES_SCALE_FACTOR = 5;
    private static final String TAG = "Launcher.IconCache";
    private final HashMap<ComponentKey, CacheEntry> mCache = new HashMap<>(50);
    /* access modifiers changed from: private */
    public final Context mContext;
    private final HashMap<UserHandle, Bitmap> mDefaultIcons = new HashMap<>();
    final IconDB mIconDb;
    private final int mIconDpi;
    private final IconProvider mIconProvider;
    private final InstantAppResolver mInstantAppResolver;
    /* access modifiers changed from: private */
    public final LauncherAppsCompat mLauncherApps;
    private final Options mLowResOptions;
    final MainThreadExecutor mMainThreadExecutor = new MainThreadExecutor();
    private final PackageManager mPackageManager;
    final UserManagerCompat mUserManager;
    final Handler mWorkerHandler;

    private class ActivityInfoProvider extends Provider<LauncherActivityInfo> {
        private final Intent mIntent;
        private final UserHandle mUser;

        public ActivityInfoProvider(Intent intent, UserHandle userHandle) {
            this.mIntent = intent;
            this.mUser = userHandle;
        }

        public LauncherActivityInfo get() {
            return IconCache.this.mLauncherApps.resolveActivity(this.mIntent, this.mUser);
        }
    }

    public static class CacheEntry {
        public CharSequence contentDescription = "";
        public Bitmap icon;
        public boolean isLowResIcon;
        public CharSequence title = "";
    }

    private static final class IconDB extends SQLiteCacheHelper {
        private static final String COLUMN_COMPONENT = "componentName";
        private static final String COLUMN_ICON = "icon";
        private static final String COLUMN_ICON_LOW_RES = "icon_low_res";
        private static final String COLUMN_LABEL = "label";
        private static final String COLUMN_LAST_UPDATED = "lastUpdated";
        private static final String COLUMN_ROWID = "rowid";
        private static final String COLUMN_SYSTEM_STATE = "system_state";
        private static final String COLUMN_USER = "profileId";
        private static final String COLUMN_VERSION = "version";
        private static final int DB_VERSION = 17;
        private static final int RELEASE_VERSION = ((FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION ^ true ? 1 : 0) + true);
        private static final String TABLE_NAME = "icons";

        public IconDB(Context context, int i) {
            super(context, LauncherFiles.APP_ICONS_DB, (RELEASE_VERSION << 16) + i, TABLE_NAME);
        }

        /* access modifiers changed from: protected */
        public void onCreateTable(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS icons (componentName TEXT NOT NULL, profileId INTEGER NOT NULL, lastUpdated INTEGER NOT NULL DEFAULT 0, version INTEGER NOT NULL DEFAULT 0, icon BLOB, icon_low_res BLOB, label TEXT, system_state TEXT, PRIMARY KEY (componentName, profileId) );");
        }
    }

    public static class IconLoadRequest {
        private final Handler mHandler;
        private final Runnable mRunnable;

        IconLoadRequest(Runnable runnable, Handler handler) {
            this.mRunnable = runnable;
            this.mHandler = handler;
        }

        public void cancel() {
            this.mHandler.removeCallbacks(this.mRunnable);
        }
    }

    public interface ItemInfoUpdateReceiver {
        void reapplyItemInfo(ItemInfoWithIcon itemInfoWithIcon);
    }

    class SerializedIconUpdateTask implements Runnable {
        private final Stack<LauncherActivityInfo> mAppsToAdd;
        private final Stack<LauncherActivityInfo> mAppsToUpdate;
        private final HashMap<String, PackageInfo> mPkgInfoMap;
        private final HashSet<String> mUpdatedPackages = new HashSet<>();
        private final long mUserSerial;

        SerializedIconUpdateTask(long j, HashMap<String, PackageInfo> hashMap, Stack<LauncherActivityInfo> stack, Stack<LauncherActivityInfo> stack2) {
            this.mUserSerial = j;
            this.mPkgInfoMap = hashMap;
            this.mAppsToAdd = stack;
            this.mAppsToUpdate = stack2;
        }

        public void run() {
            if (!this.mAppsToUpdate.isEmpty()) {
                LauncherActivityInfo launcherActivityInfo = (LauncherActivityInfo) this.mAppsToUpdate.pop();
                String packageName = launcherActivityInfo.getComponentName().getPackageName();
                IconCache.this.addIconToDBAndMemCache(launcherActivityInfo, (PackageInfo) this.mPkgInfoMap.get(packageName), this.mUserSerial, true);
                this.mUpdatedPackages.add(packageName);
                if (this.mAppsToUpdate.isEmpty() && !this.mUpdatedPackages.isEmpty()) {
                    LauncherAppState.getInstance(IconCache.this.mContext).getModel().onPackageIconsUpdated(this.mUpdatedPackages, IconCache.this.mUserManager.getUserForSerialNumber(this.mUserSerial));
                }
                scheduleNext();
            } else if (!this.mAppsToAdd.isEmpty()) {
                LauncherActivityInfo launcherActivityInfo2 = (LauncherActivityInfo) this.mAppsToAdd.pop();
                PackageInfo packageInfo = (PackageInfo) this.mPkgInfoMap.get(launcherActivityInfo2.getComponentName().getPackageName());
                if (packageInfo != null) {
                    IconCache.this.addIconToDBAndMemCache(launcherActivityInfo2, packageInfo, this.mUserSerial, false);
                }
                if (!this.mAppsToAdd.isEmpty()) {
                    scheduleNext();
                }
            }
        }

        public void scheduleNext() {
            IconCache.this.mWorkerHandler.postAtTime(this, IconCache.ICON_UPDATE_TOKEN, SystemClock.uptimeMillis() + 1);
        }
    }

    public IconCache(Context context, InvariantDeviceProfile invariantDeviceProfile) {
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = UserManagerCompat.getInstance(this.mContext);
        this.mLauncherApps = LauncherAppsCompat.getInstance(this.mContext);
        this.mInstantAppResolver = InstantAppResolver.newInstance(this.mContext);
        this.mIconDpi = invariantDeviceProfile.fillResIconDpi;
        this.mIconDb = new IconDB(context, invariantDeviceProfile.iconBitmapSize);
        this.mIconProvider = (IconProvider) Utilities.getOverrideObject(IconProvider.class, context, C0622R.string.icon_provider_class);
        this.mWorkerHandler = new Handler(LauncherModel.getWorkerLooper());
        this.mLowResOptions = new Options();
        this.mLowResOptions.inPreferredConfig = Config.RGB_565;
    }

    private Drawable getFullResDefaultActivityIcon() {
        return getFullResIcon(Resources.getSystem(), Utilities.ATLEAST_OREO ? 17301651 : 17629184);
    }

    private Drawable getFullResIcon(Resources resources, int i) {
        Drawable drawable;
        try {
            drawable = resources.getDrawableForDensity(i, this.mIconDpi);
        } catch (NotFoundException unused) {
            drawable = null;
        }
        return drawable != null ? drawable : getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(String str, int i) {
        Resources resources;
        try {
            resources = this.mPackageManager.getResourcesForApplication(str);
        } catch (NameNotFoundException unused) {
            resources = null;
        }
        if (resources == null || i == 0) {
            return getFullResDefaultActivityIcon();
        }
        return getFullResIcon(resources, i);
    }

    public Drawable getFullResIcon(ActivityInfo activityInfo) {
        Resources resources;
        try {
            resources = this.mPackageManager.getResourcesForApplication(activityInfo.applicationInfo);
        } catch (NameNotFoundException unused) {
            resources = null;
        }
        if (resources != null) {
            int iconResource = activityInfo.getIconResource();
            if (iconResource != 0) {
                return getFullResIcon(resources, iconResource);
            }
        }
        return getFullResDefaultActivityIcon();
    }

    public Drawable getFullResIcon(LauncherActivityInfo launcherActivityInfo) {
        return getFullResIcon(launcherActivityInfo, true);
    }

    public Drawable getFullResIcon(LauncherActivityInfo launcherActivityInfo, boolean z) {
        return this.mIconProvider.getIcon(launcherActivityInfo, this.mIconDpi, z);
    }

    /* access modifiers changed from: protected */
    public Bitmap makeDefaultIcon(UserHandle userHandle) {
        return LauncherIcons.createBadgedIconBitmap(getFullResDefaultActivityIcon(), userHandle, this.mContext, 26);
    }

    public synchronized void remove(ComponentName componentName, UserHandle userHandle) {
        this.mCache.remove(new ComponentKey(componentName, userHandle));
    }

    private void removeFromMemCacheLocked(String str, UserHandle userHandle) {
        HashSet hashSet = new HashSet();
        for (ComponentKey componentKey : this.mCache.keySet()) {
            if (componentKey.componentName.getPackageName().equals(str) && componentKey.user.equals(userHandle)) {
                hashSet.add(componentKey);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.mCache.remove((ComponentKey) it.next());
        }
    }

    public synchronized void updateIconsForPkg(String str, UserHandle userHandle) {
        removeIconsForPkg(str, userHandle);
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, 8192);
            long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandle);
            for (LauncherActivityInfo addIconToDBAndMemCache : this.mLauncherApps.getActivityList(str, userHandle)) {
                addIconToDBAndMemCache(addIconToDBAndMemCache, packageInfo, serialNumberForUser, false);
            }
        } catch (NameNotFoundException e) {
            Log.d(TAG, "Package not found", e);
        }
        return;
    }

    public synchronized void removeIconsForPkg(String str, UserHandle userHandle) {
        removeFromMemCacheLocked(str, userHandle);
        long serialNumberForUser = this.mUserManager.getSerialNumberForUser(userHandle);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("/%");
        this.mIconDb.delete("componentName LIKE ? AND profileId = ?", new String[]{sb.toString(), Long.toString(serialNumberForUser)});
    }

    public void updateDbIcons(Set<String> set) {
        Set<String> set2;
        this.mWorkerHandler.removeCallbacksAndMessages(ICON_UPDATE_TOKEN);
        this.mIconProvider.updateSystemStateString();
        for (UserHandle userHandle : this.mUserManager.getUserProfiles()) {
            List activityList = this.mLauncherApps.getActivityList(null, userHandle);
            if (activityList != null && !activityList.isEmpty()) {
                if (Process.myUserHandle().equals(userHandle)) {
                    set2 = set;
                } else {
                    set2 = Collections.emptySet();
                }
                updateDBIcons(userHandle, activityList, set2);
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0139, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x013b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x013c, code lost:
        r22 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0150, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x018f, code lost:
        r9.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0139 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:12:0x007a] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0150  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x016b  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x018f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateDBIcons(android.os.UserHandle r25, java.util.List<android.content.pm.LauncherActivityInfo> r26, java.util.Set<java.lang.String> r27) {
        /*
            r24 = this;
            r8 = r24
            r0 = r25
            com.android.launcher3.compat.UserManagerCompat r1 = r8.mUserManager
            long r3 = r1.getSerialNumberForUser(r0)
            android.content.Context r1 = r8.mContext
            android.content.pm.PackageManager r1 = r1.getPackageManager()
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            r2 = 8192(0x2000, float:1.14794E-41)
            java.util.List r1 = r1.getInstalledPackages(r2)
            java.util.Iterator r1 = r1.iterator()
        L_0x001f:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x0031
            java.lang.Object r2 = r1.next()
            android.content.pm.PackageInfo r2 = (android.content.pm.PackageInfo) r2
            java.lang.String r6 = r2.packageName
            r5.put(r6, r2)
            goto L_0x001f
        L_0x0031:
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            java.util.Iterator r2 = r26.iterator()
        L_0x003a:
            boolean r6 = r2.hasNext()
            if (r6 == 0) goto L_0x004e
            java.lang.Object r6 = r2.next()
            android.content.pm.LauncherActivityInfo r6 = (android.content.pm.LauncherActivityInfo) r6
            android.content.ComponentName r7 = r6.getComponentName()
            r1.put(r7, r6)
            goto L_0x003a
        L_0x004e:
            java.util.HashSet r2 = new java.util.HashSet
            r2.<init>()
            java.util.Stack r7 = new java.util.Stack
            r7.<init>()
            com.android.launcher3.IconCache$IconDB r9 = r8.mIconDb     // Catch:{ SQLiteException -> 0x0143, all -> 0x0140 }
            java.lang.String r10 = "rowid"
            java.lang.String r11 = "componentName"
            java.lang.String r12 = "lastUpdated"
            java.lang.String r13 = "version"
            java.lang.String r14 = "system_state"
            java.lang.String[] r10 = new java.lang.String[]{r10, r11, r12, r13, r14}     // Catch:{ SQLiteException -> 0x0143, all -> 0x0140 }
            java.lang.String r11 = "profileId = ? "
            r12 = 1
            java.lang.String[] r12 = new java.lang.String[r12]     // Catch:{ SQLiteException -> 0x0143, all -> 0x0140 }
            r13 = 0
            java.lang.String r14 = java.lang.Long.toString(r3)     // Catch:{ SQLiteException -> 0x0143, all -> 0x0140 }
            r12[r13] = r14     // Catch:{ SQLiteException -> 0x0143, all -> 0x0140 }
            android.database.Cursor r9 = r9.query(r10, r11, r12)     // Catch:{ SQLiteException -> 0x0143, all -> 0x0140 }
            java.lang.String r10 = "componentName"
            int r10 = r9.getColumnIndex(r10)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.String r11 = "lastUpdated"
            int r11 = r9.getColumnIndex(r11)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.String r12 = "version"
            int r12 = r9.getColumnIndex(r12)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.String r13 = "rowid"
            int r13 = r9.getColumnIndex(r13)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.String r14 = "system_state"
            int r14 = r9.getColumnIndex(r14)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
        L_0x0096:
            boolean r15 = r9.moveToNext()     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            if (r15 == 0) goto L_0x0131
            java.lang.String r15 = r9.getString(r10)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            android.content.ComponentName r15 = android.content.ComponentName.unflattenFromString(r15)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.String r6 = r15.getPackageName()     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.Object r6 = r5.get(r6)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            android.content.pm.PackageInfo r6 = (android.content.pm.PackageInfo) r6     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            if (r6 != 0) goto L_0x00cf
            java.lang.String r6 = r15.getPackageName()     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            r16 = r10
            r10 = r27
            boolean r6 = r10.contains(r6)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            if (r6 != 0) goto L_0x00cc
            r8.remove(r15, r0)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            int r6 = r9.getInt(r13)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            r2.add(r6)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
        L_0x00cc:
            r10 = r16
            goto L_0x0096
        L_0x00cf:
            r16 = r10
            android.content.pm.ApplicationInfo r10 = r6.applicationInfo     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            int r10 = r10.flags     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            r17 = 16777216(0x1000000, float:2.3509887E-38)
            r10 = r10 & r17
            if (r10 == 0) goto L_0x00dc
            goto L_0x00cc
        L_0x00dc:
            long r17 = r9.getLong(r11)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            int r10 = r9.getInt(r12)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            java.lang.Object r19 = r1.remove(r15)     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            r20 = r11
            r11 = r19
            android.content.pm.LauncherActivityInfo r11 = (android.content.pm.LauncherActivityInfo) r11     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            r21 = r12
            int r12 = r6.versionCode     // Catch:{ SQLiteException -> 0x013b, all -> 0x0139 }
            if (r10 != r12) goto L_0x0111
            r22 = r3
            long r3 = r6.lastUpdateTime     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            int r3 = (r17 > r3 ? 1 : (r17 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x0113
            java.lang.String r3 = r9.getString(r14)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            com.android.launcher3.IconProvider r4 = r8.mIconProvider     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            java.lang.String r6 = r6.packageName     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            java.lang.String r4 = r4.getIconSystemState(r6)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            boolean r3 = android.text.TextUtils.equals(r3, r4)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            if (r3 == 0) goto L_0x0113
            goto L_0x0127
        L_0x010f:
            r0 = move-exception
            goto L_0x013e
        L_0x0111:
            r22 = r3
        L_0x0113:
            if (r11 != 0) goto L_0x0124
            r8.remove(r15, r0)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            int r3 = r9.getInt(r13)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            r2.add(r3)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
            goto L_0x0127
        L_0x0124:
            r7.add(r11)     // Catch:{ SQLiteException -> 0x010f, all -> 0x0139 }
        L_0x0127:
            r10 = r16
            r11 = r20
            r12 = r21
            r3 = r22
            goto L_0x0096
        L_0x0131:
            r22 = r3
            if (r9 == 0) goto L_0x0153
            r9.close()
            goto L_0x0153
        L_0x0139:
            r0 = move-exception
            goto L_0x018d
        L_0x013b:
            r0 = move-exception
            r22 = r3
        L_0x013e:
            r6 = r9
            goto L_0x0147
        L_0x0140:
            r0 = move-exception
            r9 = 0
            goto L_0x018d
        L_0x0143:
            r0 = move-exception
            r22 = r3
            r6 = 0
        L_0x0147:
            java.lang.String r3 = "Launcher.IconCache"
            java.lang.String r4 = "Error reading icon cache"
            android.util.Log.d(r3, r4, r0)     // Catch:{ all -> 0x018b }
            if (r6 == 0) goto L_0x0153
            r6.close()
        L_0x0153:
            boolean r0 = r2.isEmpty()
            if (r0 != 0) goto L_0x0165
            com.android.launcher3.IconCache$IconDB r0 = r8.mIconDb
            java.lang.String r3 = "rowid"
            java.lang.String r2 = com.android.launcher3.Utilities.createDbSelectionQuery(r3, r2)
            r3 = 0
            r0.delete(r2, r3)
        L_0x0165:
            boolean r0 = r1.isEmpty()
            if (r0 == 0) goto L_0x0171
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x018a
        L_0x0171:
            java.util.Stack r6 = new java.util.Stack
            r6.<init>()
            java.util.Collection r0 = r1.values()
            r6.addAll(r0)
            com.android.launcher3.IconCache$SerializedIconUpdateTask r0 = new com.android.launcher3.IconCache$SerializedIconUpdateTask
            r1 = r0
            r2 = r24
            r3 = r22
            r1.<init>(r3, r5, r6, r7)
            r0.scheduleNext()
        L_0x018a:
            return
        L_0x018b:
            r0 = move-exception
            r9 = r6
        L_0x018d:
            if (r9 == 0) goto L_0x0192
            r9.close()
        L_0x0192:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.IconCache.updateDBIcons(android.os.UserHandle, java.util.List, java.util.Set):void");
    }

    /* access modifiers changed from: 0000 */
    public synchronized void addIconToDBAndMemCache(LauncherActivityInfo launcherActivityInfo, PackageInfo packageInfo, long j, boolean z) {
        ComponentKey componentKey = new ComponentKey(launcherActivityInfo.getComponentName(), launcherActivityInfo.getUser());
        CacheEntry cacheEntry = null;
        if (!z) {
            CacheEntry cacheEntry2 = (CacheEntry) this.mCache.get(componentKey);
            if (cacheEntry2 != null && !cacheEntry2.isLowResIcon) {
                if (cacheEntry2.icon != null) {
                    cacheEntry = cacheEntry2;
                }
            }
        }
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry();
            cacheEntry.icon = LauncherIcons.createBadgedIconBitmap(getFullResIcon(launcherActivityInfo), launcherActivityInfo.getUser(), this.mContext, launcherActivityInfo.getApplicationInfo().targetSdkVersion);
        }
        cacheEntry.title = launcherActivityInfo.getLabel();
        cacheEntry.contentDescription = this.mUserManager.getBadgedLabelForUser(cacheEntry.title, launcherActivityInfo.getUser());
        uniformTitleAndIcon(cacheEntry, launcherActivityInfo.getUser());
        this.mCache.put(componentKey, cacheEntry);
        addIconToDB(newContentValues(cacheEntry.icon, generateLowResIcon(cacheEntry.icon), cacheEntry.title.toString(), launcherActivityInfo.getApplicationInfo().packageName), launcherActivityInfo.getComponentName(), packageInfo, j);
    }

    private void uniformTitleAndIcon(CacheEntry cacheEntry, UserHandle userHandle) {
        int userId = UserManagerCompat.toUserId(userHandle);
        if (userId > 0) {
            cacheEntry.title = String.format(Locale.getDefault(), "[%d]%s", new Object[]{Integer.valueOf(userId + 1), cacheEntry.title});
        }
    }

    private void addIconToDB(ContentValues contentValues, ComponentName componentName, PackageInfo packageInfo, long j) {
        contentValues.put("componentName", componentName.flattenToString());
        contentValues.put(Favorites.PROFILE_ID, Long.valueOf(j));
        contentValues.put("lastUpdated", Long.valueOf(packageInfo.lastUpdateTime));
        contentValues.put("version", Integer.valueOf(packageInfo.versionCode));
        this.mIconDb.insertOrReplace(contentValues);
    }

    public IconLoadRequest updateIconInBackground(final ItemInfoUpdateReceiver itemInfoUpdateReceiver, final ItemInfoWithIcon itemInfoWithIcon) {
        C05371 r0 = new Runnable() {
            public void run() {
                if ((itemInfoWithIcon instanceof AppInfo) || (itemInfoWithIcon instanceof ShortcutInfo)) {
                    IconCache.this.getTitleAndIcon(itemInfoWithIcon, false);
                } else if (itemInfoWithIcon instanceof PackageItemInfo) {
                    IconCache.this.getTitleAndIconForApp((PackageItemInfo) itemInfoWithIcon, false);
                }
                IconCache.this.mMainThreadExecutor.execute(new Runnable() {
                    public void run() {
                        itemInfoUpdateReceiver.reapplyItemInfo(itemInfoWithIcon);
                    }
                });
            }
        };
        this.mWorkerHandler.post(r0);
        return new IconLoadRequest(r0, this.mWorkerHandler);
    }

    public synchronized void updateTitleAndIcon(AppInfo appInfo) {
        CacheEntry cacheLocked = cacheLocked(appInfo.componentName, Provider.m16of(null), appInfo.user, false, appInfo.usingLowResIcon);
        if (cacheLocked.icon != null && !isDefaultIcon(cacheLocked.icon, appInfo.user)) {
            applyCacheEntry(cacheLocked, appInfo);
        }
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon itemInfoWithIcon, LauncherActivityInfo launcherActivityInfo, boolean z) {
        getTitleAndIcon(itemInfoWithIcon, Provider.m16of(launcherActivityInfo), false, z);
    }

    public synchronized void getTitleAndIcon(ItemInfoWithIcon itemInfoWithIcon, boolean z) {
        if (itemInfoWithIcon.getTargetComponent() == null) {
            itemInfoWithIcon.iconBitmap = getDefaultIcon(itemInfoWithIcon.user);
            itemInfoWithIcon.title = "";
            itemInfoWithIcon.contentDescription = "";
            itemInfoWithIcon.usingLowResIcon = false;
        } else {
            getTitleAndIcon(itemInfoWithIcon, new ActivityInfoProvider(itemInfoWithIcon.getIntent(), itemInfoWithIcon.user), true, z);
        }
    }

    private synchronized void getTitleAndIcon(@NonNull ItemInfoWithIcon itemInfoWithIcon, @NonNull Provider<LauncherActivityInfo> provider, boolean z, boolean z2) {
        applyCacheEntry(cacheLocked(itemInfoWithIcon.getTargetComponent(), provider, itemInfoWithIcon.user, z, z2), itemInfoWithIcon);
    }

    public synchronized void getTitleAndIconForApp(PackageItemInfo packageItemInfo, boolean z) {
        applyCacheEntry(getEntryForPackageLocked(packageItemInfo.packageName, packageItemInfo.user, z), packageItemInfo);
    }

    private void applyCacheEntry(CacheEntry cacheEntry, ItemInfoWithIcon itemInfoWithIcon) {
        itemInfoWithIcon.title = Utilities.trim(cacheEntry.title);
        itemInfoWithIcon.contentDescription = cacheEntry.contentDescription;
        itemInfoWithIcon.iconBitmap = cacheEntry.icon == null ? getDefaultIcon(itemInfoWithIcon.user) : cacheEntry.icon;
        itemInfoWithIcon.usingLowResIcon = cacheEntry.isLowResIcon;
    }

    public synchronized Bitmap getDefaultIcon(UserHandle userHandle) {
        if (!this.mDefaultIcons.containsKey(userHandle)) {
            this.mDefaultIcons.put(userHandle, makeDefaultIcon(userHandle));
        }
        return (Bitmap) this.mDefaultIcons.get(userHandle);
    }

    public boolean isDefaultIcon(Bitmap bitmap, UserHandle userHandle) {
        return this.mDefaultIcons.get(userHandle) == bitmap;
    }

    /* access modifiers changed from: protected */
    public CacheEntry cacheLocked(@NonNull ComponentName componentName, @NonNull Provider<LauncherActivityInfo> provider, UserHandle userHandle, boolean z, boolean z2) {
        boolean z3;
        Preconditions.assertWorkerThread();
        ComponentKey componentKey = new ComponentKey(componentName, userHandle);
        CacheEntry cacheEntry = (CacheEntry) this.mCache.get(componentKey);
        if (cacheEntry == null || (cacheEntry.isLowResIcon && !z2)) {
            cacheEntry = new CacheEntry();
            this.mCache.put(componentKey, cacheEntry);
            LauncherActivityInfo launcherActivityInfo = null;
            if (getEntryFromDB(componentKey, cacheEntry, z2)) {
                z3 = false;
            } else {
                launcherActivityInfo = (LauncherActivityInfo) provider.get();
                z3 = true;
                if (launcherActivityInfo != null) {
                    cacheEntry.icon = LauncherIcons.createBadgedIconBitmap(getFullResIcon(launcherActivityInfo), launcherActivityInfo.getUser(), this.mContext, ((LauncherActivityInfo) provider.get()).getApplicationInfo().targetSdkVersion);
                } else {
                    if (z) {
                        CacheEntry entryForPackageLocked = getEntryForPackageLocked(componentName.getPackageName(), userHandle, false);
                        if (entryForPackageLocked != null) {
                            cacheEntry.icon = entryForPackageLocked.icon;
                            cacheEntry.title = entryForPackageLocked.title;
                            cacheEntry.contentDescription = entryForPackageLocked.contentDescription;
                        }
                    }
                    if (cacheEntry.icon == null) {
                        cacheEntry.icon = getDefaultIcon(userHandle);
                    }
                }
            }
            if (TextUtils.isEmpty(cacheEntry.title)) {
                if (launcherActivityInfo == null && !z3) {
                    launcherActivityInfo = (LauncherActivityInfo) provider.get();
                }
                if (launcherActivityInfo != null) {
                    cacheEntry.title = launcherActivityInfo.getLabel();
                    cacheEntry.contentDescription = this.mUserManager.getBadgedLabelForUser(cacheEntry.title, userHandle);
                }
            }
        }
        return cacheEntry;
    }

    public synchronized void clear() {
        Preconditions.assertWorkerThread();
        this.mIconDb.clear();
    }

    public synchronized void cachePackageInstallInfo(String str, UserHandle userHandle, Bitmap bitmap, CharSequence charSequence) {
        removeFromMemCacheLocked(str, userHandle);
        ComponentKey packageKey = getPackageKey(str, userHandle);
        CacheEntry cacheEntry = (CacheEntry) this.mCache.get(packageKey);
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry();
        }
        if (!TextUtils.isEmpty(charSequence)) {
            cacheEntry.title = charSequence;
        }
        if (bitmap != null) {
            cacheEntry.icon = LauncherIcons.createIconBitmap(bitmap, this.mContext);
        }
        if (!TextUtils.isEmpty(charSequence) && cacheEntry.icon != null) {
            this.mCache.put(packageKey, cacheEntry);
        }
    }

    private static ComponentKey getPackageKey(String str, UserHandle userHandle) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(EMPTY_CLASS_NAME);
        return new ComponentKey(new ComponentName(str, sb.toString()), userHandle);
    }

    private CacheEntry getEntryForPackageLocked(String str, UserHandle userHandle, boolean z) {
        Preconditions.assertWorkerThread();
        ComponentKey packageKey = getPackageKey(str, userHandle);
        CacheEntry cacheEntry = (CacheEntry) this.mCache.get(packageKey);
        if (cacheEntry == null || (cacheEntry.isLowResIcon && !z)) {
            cacheEntry = new CacheEntry();
            boolean z2 = true;
            if (!getEntryFromDB(packageKey, cacheEntry, z)) {
                try {
                    PackageInfo packageInfo = this.mPackageManager.getPackageInfo(str, Process.myUserHandle().equals(userHandle) ? 0 : 8192);
                    ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                    if (applicationInfo != null) {
                        Bitmap createBadgedIconBitmap = LauncherIcons.createBadgedIconBitmap(applicationInfo.loadIcon(this.mPackageManager), userHandle, this.mContext, applicationInfo.targetSdkVersion);
                        if (this.mInstantAppResolver.isInstantApp(applicationInfo)) {
                            createBadgedIconBitmap = LauncherIcons.badgeWithDrawable(createBadgedIconBitmap, this.mContext.getDrawable(C0622R.C0624drawable.ic_launcher_home), this.mContext);
                        }
                        Bitmap generateLowResIcon = generateLowResIcon(createBadgedIconBitmap);
                        cacheEntry.title = applicationInfo.loadLabel(this.mPackageManager);
                        cacheEntry.contentDescription = this.mUserManager.getBadgedLabelForUser(cacheEntry.title, userHandle);
                        cacheEntry.icon = z ? generateLowResIcon : createBadgedIconBitmap;
                        cacheEntry.isLowResIcon = z;
                        addIconToDB(newContentValues(createBadgedIconBitmap, generateLowResIcon, cacheEntry.title.toString(), str), packageKey.componentName, packageInfo, this.mUserManager.getSerialNumberForUser(userHandle));
                    } else {
                        throw new NameNotFoundException("ApplicationInfo is null");
                    }
                } catch (NameNotFoundException unused) {
                    z2 = false;
                }
            }
            if (z2) {
                this.mCache.put(packageKey, cacheEntry);
            }
        }
        return cacheEntry;
    }

    /* JADX WARNING: type inference failed for: r0v1, types: [android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r0v2, types: [android.database.Cursor] */
    /* JADX WARNING: type inference failed for: r0v3 */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r0v9 */
    /* JADX WARNING: type inference failed for: r0v10 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0088  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getEntryFromDB(com.android.launcher3.util.ComponentKey r10, com.android.launcher3.IconCache.CacheEntry r11, boolean r12) {
        /*
            r9 = this;
            r0 = 0
            r1 = 0
            com.android.launcher3.IconCache$IconDB r2 = r9.mIconDb     // Catch:{ SQLiteException -> 0x0078 }
            r3 = 2
            java.lang.String[] r4 = new java.lang.String[r3]     // Catch:{ SQLiteException -> 0x0078 }
            if (r12 == 0) goto L_0x000c
            java.lang.String r5 = "icon_low_res"
            goto L_0x000e
        L_0x000c:
            java.lang.String r5 = "icon"
        L_0x000e:
            r4[r1] = r5     // Catch:{ SQLiteException -> 0x0078 }
            java.lang.String r5 = "label"
            r6 = 1
            r4[r6] = r5     // Catch:{ SQLiteException -> 0x0078 }
            java.lang.String r5 = "componentName = ? AND profileId = ?"
            java.lang.String[] r3 = new java.lang.String[r3]     // Catch:{ SQLiteException -> 0x0078 }
            android.content.ComponentName r7 = r10.componentName     // Catch:{ SQLiteException -> 0x0078 }
            java.lang.String r7 = r7.flattenToString()     // Catch:{ SQLiteException -> 0x0078 }
            r3[r1] = r7     // Catch:{ SQLiteException -> 0x0078 }
            com.android.launcher3.compat.UserManagerCompat r7 = r9.mUserManager     // Catch:{ SQLiteException -> 0x0078 }
            android.os.UserHandle r8 = r10.user     // Catch:{ SQLiteException -> 0x0078 }
            long r7 = r7.getSerialNumberForUser(r8)     // Catch:{ SQLiteException -> 0x0078 }
            java.lang.String r7 = java.lang.Long.toString(r7)     // Catch:{ SQLiteException -> 0x0078 }
            r3[r6] = r7     // Catch:{ SQLiteException -> 0x0078 }
            android.database.Cursor r2 = r2.query(r4, r5, r3)     // Catch:{ SQLiteException -> 0x0078 }
            boolean r3 = r2.moveToNext()     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            if (r3 == 0) goto L_0x006a
            if (r12 == 0) goto L_0x003d
            android.graphics.BitmapFactory$Options r0 = r9.mLowResOptions     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
        L_0x003d:
            android.graphics.Bitmap r0 = loadIconNoResize(r2, r1, r0)     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            r11.icon = r0     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            r11.isLowResIcon = r12     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            java.lang.String r12 = r2.getString(r6)     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            r11.title = r12     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            java.lang.CharSequence r12 = r11.title     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            if (r12 != 0) goto L_0x0058
            java.lang.String r10 = ""
            r11.title = r10     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            java.lang.String r10 = ""
            r11.contentDescription = r10     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            goto L_0x0064
        L_0x0058:
            com.android.launcher3.compat.UserManagerCompat r12 = r9.mUserManager     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            java.lang.CharSequence r0 = r11.title     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            android.os.UserHandle r10 = r10.user     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            java.lang.CharSequence r10 = r12.getBadgedLabelForUser(r0, r10)     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
            r11.contentDescription = r10     // Catch:{ SQLiteException -> 0x0073, all -> 0x0070 }
        L_0x0064:
            if (r2 == 0) goto L_0x0069
            r2.close()
        L_0x0069:
            return r6
        L_0x006a:
            if (r2 == 0) goto L_0x0085
            r2.close()
            goto L_0x0085
        L_0x0070:
            r10 = move-exception
            r0 = r2
            goto L_0x0086
        L_0x0073:
            r10 = move-exception
            r0 = r2
            goto L_0x0079
        L_0x0076:
            r10 = move-exception
            goto L_0x0086
        L_0x0078:
            r10 = move-exception
        L_0x0079:
            java.lang.String r11 = "Launcher.IconCache"
            java.lang.String r12 = "Error reading icon cache"
            android.util.Log.d(r11, r12, r10)     // Catch:{ all -> 0x0076 }
            if (r0 == 0) goto L_0x0085
            r0.close()
        L_0x0085:
            return r1
        L_0x0086:
            if (r0 == 0) goto L_0x008b
            r0.close()
        L_0x008b:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.IconCache.getEntryFromDB(com.android.launcher3.util.ComponentKey, com.android.launcher3.IconCache$CacheEntry, boolean):boolean");
    }

    private ContentValues newContentValues(Bitmap bitmap, Bitmap bitmap2, String str, String str2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BaseLauncherColumns.ICON, Utilities.flattenBitmap(bitmap));
        contentValues.put("icon_low_res", Utilities.flattenBitmap(bitmap2));
        contentValues.put("label", str);
        contentValues.put("system_state", this.mIconProvider.getIconSystemState(str2));
        return contentValues;
    }

    private Bitmap generateLowResIcon(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5, true);
    }

    private static Bitmap loadIconNoResize(Cursor cursor, int i, Options options) {
        byte[] blob = cursor.getBlob(i);
        try {
            return BitmapFactory.decodeByteArray(blob, 0, blob.length, options);
        } catch (Exception unused) {
            return null;
        }
    }
}
