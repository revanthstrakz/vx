package com.android.launcher3.shortcuts;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import java.util.ArrayList;
import java.util.List;

public class DeepShortcutManager {
    private static final int FLAG_GET_ALL = 11;
    private static final String TAG = "DeepShortcutManager";
    private static DeepShortcutManager sInstance;
    private static final Object sInstanceLock = new Object();
    private final Context mContext;
    private final LauncherApps mLauncherApps;
    private boolean mWasLastCallSuccess;

    public void onShortcutsChanged(List<ShortcutInfoCompat> list) {
    }

    public static DeepShortcutManager getInstance(Context context) {
        DeepShortcutManager deepShortcutManager;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new DeepShortcutManager(context.getApplicationContext());
            }
            deepShortcutManager = sInstance;
        }
        return deepShortcutManager;
    }

    private DeepShortcutManager(Context context) {
        this.mContext = context;
        this.mLauncherApps = (LauncherApps) context.getSystemService("launcherapps");
        if (Utilities.ATLEAST_MARSHMALLOW && !Utilities.ATLEAST_NOUGAT_MR1) {
            this.mWasLastCallSuccess = true;
        }
    }

    public static boolean supportsShortcuts(ItemInfo itemInfo) {
        boolean z = (itemInfo instanceof ShortcutInfo) && ((ShortcutInfo) itemInfo).hasPromiseIconUi();
        if (itemInfo.itemType != 0 || itemInfo.isDisabled() || z) {
            return false;
        }
        return true;
    }

    public boolean wasLastCallSuccess() {
        return this.mWasLastCallSuccess;
    }

    public List<ShortcutInfoCompat> queryForFullDetails(String str, List<String> list, UserHandle userHandle) {
        return query(11, str, null, list, userHandle);
    }

    public List<ShortcutInfoCompat> queryForShortcutsContainer(ComponentName componentName, List<String> list, UserHandle userHandle) {
        return query(9, componentName.getPackageName(), componentName, list, userHandle);
    }

    @TargetApi(25)
    public void unpinShortcut(ShortcutKey shortcutKey) {
        String packageName = shortcutKey.componentName.getPackageName();
        String id = shortcutKey.getId();
        UserHandle userHandle = shortcutKey.user;
        List extractIds = extractIds(queryForPinnedShortcuts(packageName, userHandle));
        extractIds.remove(id);
        try {
            this.mLauncherApps.pinShortcuts(packageName, extractIds, userHandle);
            this.mWasLastCallSuccess = true;
        } catch (IllegalStateException | SecurityException e) {
            Log.w(TAG, "Failed to unpin shortcut", e);
            this.mWasLastCallSuccess = false;
        }
    }

    @TargetApi(25)
    public void pinShortcut(ShortcutKey shortcutKey) {
        String packageName = shortcutKey.componentName.getPackageName();
        String id = shortcutKey.getId();
        UserHandle userHandle = shortcutKey.user;
        List extractIds = extractIds(queryForPinnedShortcuts(packageName, userHandle));
        extractIds.add(id);
        try {
            this.mLauncherApps.pinShortcuts(packageName, extractIds, userHandle);
            this.mWasLastCallSuccess = true;
        } catch (IllegalStateException | SecurityException e) {
            Log.w(TAG, "Failed to pin shortcut", e);
            this.mWasLastCallSuccess = false;
        }
    }

    @TargetApi(25)
    public void startShortcut(String str, String str2, Intent intent, Bundle bundle, UserHandle userHandle) {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            try {
                this.mLauncherApps.startShortcut(str, str2, intent.getSourceBounds(), bundle, userHandle);
                this.mWasLastCallSuccess = true;
            } catch (IllegalStateException | SecurityException e) {
                Log.e(TAG, "Failed to start shortcut", e);
                this.mWasLastCallSuccess = false;
            }
        } else {
            this.mContext.startActivity(ShortcutInfoCompatBackport.stripPackage(intent), bundle);
        }
    }

    @TargetApi(25)
    public Drawable getShortcutIconDrawable(ShortcutInfoCompat shortcutInfoCompat, int i) {
        if (!Utilities.ATLEAST_NOUGAT_MR1) {
            return DeepShortcutManagerBackport.getShortcutIconDrawable(shortcutInfoCompat, i);
        }
        try {
            Drawable shortcutIconDrawable = this.mLauncherApps.getShortcutIconDrawable(shortcutInfoCompat.getShortcutInfo(), i);
            this.mWasLastCallSuccess = true;
            return shortcutIconDrawable;
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to get shortcut icon", e);
            this.mWasLastCallSuccess = false;
            return null;
        }
    }

    public List<ShortcutInfoCompat> queryForPinnedShortcuts(String str, UserHandle userHandle) {
        return query(2, str, null, null, userHandle);
    }

    public List<ShortcutInfoCompat> queryForAllShortcuts(UserHandle userHandle) {
        return query(11, null, null, null, userHandle);
    }

    private List<String> extractIds(List<ShortcutInfoCompat> list) {
        ArrayList arrayList = new ArrayList(list.size());
        for (ShortcutInfoCompat id : list) {
            arrayList.add(id.getId());
        }
        return arrayList;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003c  */
    @android.annotation.TargetApi(25)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.List<com.android.launcher3.shortcuts.ShortcutInfoCompat> query(int r2, java.lang.String r3, android.content.ComponentName r4, java.util.List<java.lang.String> r5, android.os.UserHandle r6) {
        /*
            r1 = this;
            boolean r6 = com.android.launcher3.Utilities.ATLEAST_NOUGAT_MR1
            if (r6 == 0) goto L_0x005f
            android.content.pm.LauncherApps$ShortcutQuery r6 = new android.content.pm.LauncherApps$ShortcutQuery
            r6.<init>()
            r6.setQueryFlags(r2)
            if (r3 == 0) goto L_0x0017
            r6.setPackage(r3)
            r6.setActivity(r4)
            r6.setShortcutIds(r5)
        L_0x0017:
            r2 = 0
            android.content.pm.LauncherApps r3 = r1.mLauncherApps     // Catch:{ IllegalStateException | SecurityException -> 0x002c }
            android.os.UserHandle r4 = android.os.Process.myUserHandle()     // Catch:{ IllegalStateException | SecurityException -> 0x002c }
            java.util.List r3 = r3.getShortcuts(r6, r4)     // Catch:{ IllegalStateException | SecurityException -> 0x002c }
            r2 = 1
            r1.mWasLastCallSuccess = r2     // Catch:{ IllegalStateException | SecurityException -> 0x0027 }
            r2 = r3
            goto L_0x0037
        L_0x0027:
            r2 = move-exception
            r0 = r3
            r3 = r2
            r2 = r0
            goto L_0x002d
        L_0x002c:
            r3 = move-exception
        L_0x002d:
            java.lang.String r4 = "DeepShortcutManager"
            java.lang.String r5 = "Failed to query for shortcuts"
            android.util.Log.e(r4, r5, r3)
            r3 = 0
            r1.mWasLastCallSuccess = r3
        L_0x0037:
            if (r2 != 0) goto L_0x003c
            java.util.List r2 = java.util.Collections.EMPTY_LIST
            return r2
        L_0x003c:
            java.util.ArrayList r3 = new java.util.ArrayList
            int r4 = r2.size()
            r3.<init>(r4)
            java.util.Iterator r2 = r2.iterator()
        L_0x0049:
            boolean r4 = r2.hasNext()
            if (r4 == 0) goto L_0x005e
            java.lang.Object r4 = r2.next()
            android.content.pm.ShortcutInfo r4 = (android.content.pm.ShortcutInfo) r4
            com.android.launcher3.shortcuts.ShortcutInfoCompat r5 = new com.android.launcher3.shortcuts.ShortcutInfoCompat
            r5.<init>(r4)
            r3.add(r5)
            goto L_0x0049
        L_0x005e:
            return r3
        L_0x005f:
            android.content.Context r2 = r1.mContext
            android.content.pm.LauncherApps r5 = r1.mLauncherApps
            java.util.List r2 = com.android.launcher3.shortcuts.DeepShortcutManagerBackport.getForPackage(r2, r5, r4, r3)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.shortcuts.DeepShortcutManager.query(int, java.lang.String, android.content.ComponentName, java.util.List, android.os.UserHandle):java.util.List");
    }

    @TargetApi(25)
    public boolean hasHostPermission() {
        if (!Utilities.ATLEAST_NOUGAT_MR1) {
            return true;
        }
        try {
            return this.mLauncherApps.hasShortcutHostPermission();
        } catch (IllegalStateException | SecurityException e) {
            Log.e(TAG, "Failed to make shortcut manager call", e);
            return false;
        }
    }
}
