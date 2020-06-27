package com.android.launcher3.model;

import android.os.UserHandle;

public class PackageUpdatedTask extends BaseModelUpdateTask {
    private static final boolean DEBUG = false;
    public static final int OP_ADD = 1;
    public static final int OP_NONE = 0;
    public static final int OP_RELOAD = 8;
    public static final int OP_REMOVE = 3;
    public static final int OP_SUSPEND = 5;
    public static final int OP_UNAVAILABLE = 4;
    public static final int OP_UNSUSPEND = 6;
    public static final int OP_UPDATE = 2;
    public static final int OP_USER_AVAILABILITY_CHANGE = 7;
    private static final String TAG = "PackageUpdatedTask";
    private final int mOp;
    private final String[] mPackages;
    private final UserHandle mUser;

    public PackageUpdatedTask(int i, UserHandle userHandle, String... strArr) {
        this.mOp = i;
        this.mUser = userHandle;
        this.mPackages = strArr;
    }

    /* JADX WARNING: Removed duplicated region for block: B:111:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x037d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute(com.android.launcher3.LauncherAppState r25, com.android.launcher3.model.BgDataModel r26, com.android.launcher3.AllAppsList r27) {
        /*
            r24 = this;
            r1 = r24
            r2 = r26
            r0 = r27
            android.content.Context r3 = r25.getContext()
            com.android.launcher3.IconCache r4 = r25.getIconCache()
            java.lang.String[] r5 = r1.mPackages
            int r6 = r5.length
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.NO_OP
            java.util.HashSet r8 = new java.util.HashSet
            java.util.List r9 = java.util.Arrays.asList(r5)
            r8.<init>(r9)
            android.os.UserHandle r9 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r9 = com.android.launcher3.util.ItemInfoMatcher.ofPackages(r8, r9)
            int r10 = r1.mOp
            r11 = 4
            r12 = 2
            switch(r10) {
                case 1: goto L_0x00b5;
                case 2: goto L_0x0091;
                case 3: goto L_0x0067;
                case 4: goto L_0x0074;
                case 5: goto L_0x0054;
                case 6: goto L_0x0054;
                case 7: goto L_0x0032;
                case 8: goto L_0x002b;
                default: goto L_0x0029;
            }
        L_0x0029:
            goto L_0x00e4
        L_0x002b:
            android.os.UserHandle r10 = r1.mUser
            r0.reloadPackages(r3, r10)
            goto L_0x00e4
        L_0x0032:
            com.android.launcher3.compat.UserManagerCompat r7 = com.android.launcher3.compat.UserManagerCompat.getInstance(r3)
            android.os.UserHandle r9 = r1.mUser
            boolean r7 = r7.isQuietModeEnabled(r9)
            r9 = 8
            if (r7 == 0) goto L_0x0045
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.addFlag(r9)
            goto L_0x0049
        L_0x0045:
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.removeFlag(r9)
        L_0x0049:
            android.os.UserHandle r9 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r9 = com.android.launcher3.util.ItemInfoMatcher.ofUser(r9)
            r0.updateDisabledFlags(r9, r7)
            goto L_0x00e4
        L_0x0054:
            int r7 = r1.mOp
            r10 = 5
            if (r7 != r10) goto L_0x005e
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.addFlag(r11)
            goto L_0x0062
        L_0x005e:
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.removeFlag(r11)
        L_0x0062:
            r0.updateDisabledFlags(r9, r7)
            goto L_0x00e4
        L_0x0067:
            r7 = 0
        L_0x0068:
            if (r7 >= r6) goto L_0x0074
            r10 = r5[r7]
            android.os.UserHandle r14 = r1.mUser
            r4.removeIconsForPkg(r10, r14)
            int r7 = r7 + 1
            goto L_0x0068
        L_0x0074:
            r7 = 0
        L_0x0075:
            if (r7 >= r6) goto L_0x008c
            r10 = r5[r7]
            android.os.UserHandle r14 = r1.mUser
            r0.removePackage(r10, r14)
            com.android.launcher3.WidgetPreviewLoader r10 = r25.getWidgetCache()
            r14 = r5[r7]
            android.os.UserHandle r15 = r1.mUser
            r10.removePackage(r14, r15)
            int r7 = r7 + 1
            goto L_0x0075
        L_0x008c:
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.addFlag(r12)
            goto L_0x00e4
        L_0x0091:
            r7 = 0
        L_0x0092:
            if (r7 >= r6) goto L_0x00b0
            r10 = r5[r7]
            android.os.UserHandle r14 = r1.mUser
            r4.updateIconsForPkg(r10, r14)
            r10 = r5[r7]
            android.os.UserHandle r14 = r1.mUser
            r0.updatePackage(r3, r10, r14)
            com.android.launcher3.WidgetPreviewLoader r10 = r25.getWidgetCache()
            r14 = r5[r7]
            android.os.UserHandle r15 = r1.mUser
            r10.removePackage(r14, r15)
            int r7 = r7 + 1
            goto L_0x0092
        L_0x00b0:
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.removeFlag(r12)
            goto L_0x00e4
        L_0x00b5:
            r7 = 0
        L_0x00b6:
            if (r7 >= r6) goto L_0x00e0
            r10 = r5[r7]
            android.os.UserHandle r14 = r1.mUser
            r4.updateIconsForPkg(r10, r14)
            r10 = r5[r7]
            android.os.UserHandle r14 = r1.mUser
            r0.addPackage(r3, r10, r14)
            boolean r10 = com.android.launcher3.Utilities.ATLEAST_OREO
            if (r10 != 0) goto L_0x00dd
            android.os.UserHandle r10 = android.os.Process.myUserHandle()
            android.os.UserHandle r14 = r1.mUser
            boolean r10 = r10.equals(r14)
            if (r10 != 0) goto L_0x00dd
            r10 = r5[r7]
            android.os.UserHandle r14 = r1.mUser
            com.android.launcher3.SessionCommitReceiver.queueAppIconAddition(r3, r10, r14)
        L_0x00dd:
            int r7 = r7 + 1
            goto L_0x00b6
        L_0x00e0:
            com.android.launcher3.util.FlagOp r7 = com.android.launcher3.util.FlagOp.removeFlag(r12)
        L_0x00e4:
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            java.util.ArrayList<com.android.launcher3.AppInfo> r14 = r0.added
            r10.addAll(r14)
            java.util.ArrayList<com.android.launcher3.AppInfo> r14 = r0.added
            r14.clear()
            java.util.ArrayList<com.android.launcher3.AppInfo> r14 = r0.modified
            r10.addAll(r14)
            java.util.ArrayList<com.android.launcher3.AppInfo> r14 = r0.modified
            r14.clear()
            java.util.ArrayList r14 = new java.util.ArrayList
            java.util.ArrayList<com.android.launcher3.AppInfo> r15 = r0.removed
            r14.<init>(r15)
            java.util.ArrayList<com.android.launcher3.AppInfo> r0 = r0.removed
            r0.clear()
            android.util.ArrayMap r0 = new android.util.ArrayMap
            r0.<init>()
            boolean r15 = r10.isEmpty()
            if (r15 == 0) goto L_0x0118
            int r15 = r1.mOp
            if (r15 != r12) goto L_0x0137
        L_0x0118:
            com.android.launcher3.model.PackageUpdatedTask$1 r15 = new com.android.launcher3.model.PackageUpdatedTask$1
            r15.<init>(r10)
            r1.scheduleCallbackTask(r15)
            java.util.Iterator r10 = r10.iterator()
        L_0x0124:
            boolean r15 = r10.hasNext()
            if (r15 == 0) goto L_0x0137
            java.lang.Object r15 = r10.next()
            com.android.launcher3.AppInfo r15 = (com.android.launcher3.AppInfo) r15
            android.content.ComponentName r11 = r15.componentName
            r0.put(r11, r15)
            r11 = 4
            goto L_0x0124
        L_0x0137:
            com.android.launcher3.util.LongArrayMap r10 = new com.android.launcher3.util.LongArrayMap
            r10.<init>()
            int r11 = r1.mOp
            r15 = 1
            if (r11 == r15) goto L_0x0150
            com.android.launcher3.util.FlagOp r11 = com.android.launcher3.util.FlagOp.NO_OP
            if (r7 == r11) goto L_0x0146
            goto L_0x0150
        L_0x0146:
            r22 = r5
            r20 = r6
            r19 = r14
            r16 = 0
            goto L_0x02f7
        L_0x0150:
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            int r12 = r1.mOp
            if (r12 == r15) goto L_0x0166
            int r12 = r1.mOp
            r15 = 2
            if (r12 != r15) goto L_0x0164
            goto L_0x0166
        L_0x0164:
            r12 = 0
            goto L_0x0167
        L_0x0166:
            r12 = 1
        L_0x0167:
            monitor-enter(r26)
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r15 = r2.itemsIdMap     // Catch:{ all -> 0x03c6 }
            java.util.Iterator r15 = r15.iterator()     // Catch:{ all -> 0x03c6 }
        L_0x016e:
            boolean r17 = r15.hasNext()     // Catch:{ all -> 0x03c6 }
            if (r17 == 0) goto L_0x02c7
            java.lang.Object r17 = r15.next()     // Catch:{ all -> 0x03c6 }
            r18 = r15
            r15 = r17
            com.android.launcher3.ItemInfo r15 = (com.android.launcher3.ItemInfo) r15     // Catch:{ all -> 0x03c6 }
            r19 = r14
            boolean r14 = r15 instanceof com.android.launcher3.ShortcutInfo     // Catch:{ all -> 0x03c6 }
            if (r14 == 0) goto L_0x0278
            android.os.UserHandle r14 = r1.mUser     // Catch:{ all -> 0x03c6 }
            r20 = r6
            android.os.UserHandle r6 = r15.user     // Catch:{ all -> 0x03c6 }
            boolean r6 = r14.equals(r6)     // Catch:{ all -> 0x03c6 }
            if (r6 == 0) goto L_0x0275
            com.android.launcher3.ShortcutInfo r15 = (com.android.launcher3.ShortcutInfo) r15     // Catch:{ all -> 0x03c6 }
            android.content.Intent$ShortcutIconResource r6 = r15.iconResource     // Catch:{ all -> 0x03c6 }
            if (r6 == 0) goto L_0x01ac
            android.content.Intent$ShortcutIconResource r6 = r15.iconResource     // Catch:{ all -> 0x03c6 }
            java.lang.String r6 = r6.packageName     // Catch:{ all -> 0x03c6 }
            boolean r6 = r8.contains(r6)     // Catch:{ all -> 0x03c6 }
            if (r6 == 0) goto L_0x01ac
            android.content.Intent$ShortcutIconResource r6 = r15.iconResource     // Catch:{ all -> 0x03c6 }
            android.graphics.Bitmap r6 = com.android.launcher3.graphics.LauncherIcons.createIconBitmap(r6, r3)     // Catch:{ all -> 0x03c6 }
            if (r6 == 0) goto L_0x01ac
            r15.iconBitmap = r6     // Catch:{ all -> 0x03c6 }
            r6 = 1
            goto L_0x01ad
        L_0x01ac:
            r6 = 0
        L_0x01ad:
            android.content.ComponentName r14 = r15.getTargetComponent()     // Catch:{ all -> 0x03c6 }
            if (r14 == 0) goto L_0x025b
            boolean r17 = r9.matches(r15, r14)     // Catch:{ all -> 0x03c6 }
            if (r17 == 0) goto L_0x025b
            java.lang.Object r17 = r0.get(r14)     // Catch:{ all -> 0x03c6 }
            com.android.launcher3.AppInfo r17 = (com.android.launcher3.AppInfo) r17     // Catch:{ all -> 0x03c6 }
            r21 = r6
            r6 = 16
            boolean r6 = r15.hasStatusFlag(r6)     // Catch:{ all -> 0x03c6 }
            if (r6 == 0) goto L_0x01df
            r22 = r5
            long r5 = r15.f52id     // Catch:{ all -> 0x03c6 }
            r23 = r9
            r16 = 0
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r16)     // Catch:{ all -> 0x03c6 }
            r10.put(r5, r9)     // Catch:{ all -> 0x03c6 }
            int r5 = r1.mOp     // Catch:{ all -> 0x03c6 }
            r6 = 3
            if (r5 != r6) goto L_0x01e3
            goto L_0x02bb
        L_0x01df:
            r22 = r5
            r23 = r9
        L_0x01e3:
            boolean r5 = r15.isPromise()     // Catch:{ all -> 0x03c6 }
            if (r5 == 0) goto L_0x023c
            if (r12 == 0) goto L_0x023c
            r5 = 2
            boolean r6 = r15.hasStatusFlag(r5)     // Catch:{ all -> 0x03c6 }
            if (r6 == 0) goto L_0x0237
            com.android.launcher3.compat.LauncherAppsCompat r5 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r3)     // Catch:{ all -> 0x03c6 }
            android.os.UserHandle r6 = r1.mUser     // Catch:{ all -> 0x03c6 }
            boolean r5 = r5.isActivityEnabledForProfile(r14, r6)     // Catch:{ all -> 0x03c6 }
            if (r5 != 0) goto L_0x023c
            com.android.launcher3.util.PackageManagerHelper r5 = new com.android.launcher3.util.PackageManagerHelper     // Catch:{ all -> 0x03c6 }
            r5.<init>(r3)     // Catch:{ all -> 0x03c6 }
            java.lang.String r6 = r14.getPackageName()     // Catch:{ all -> 0x03c6 }
            android.os.UserHandle r9 = r1.mUser     // Catch:{ all -> 0x03c6 }
            android.content.Intent r5 = r5.getAppLaunchIntent(r6, r9)     // Catch:{ all -> 0x03c6 }
            if (r5 == 0) goto L_0x021b
            android.content.ComponentName r6 = r5.getComponent()     // Catch:{ all -> 0x03c6 }
            java.lang.Object r6 = r0.get(r6)     // Catch:{ all -> 0x03c6 }
            r17 = r6
            com.android.launcher3.AppInfo r17 = (com.android.launcher3.AppInfo) r17     // Catch:{ all -> 0x03c6 }
        L_0x021b:
            if (r5 == 0) goto L_0x0225
            if (r17 == 0) goto L_0x0225
            r15.intent = r5     // Catch:{ all -> 0x03c6 }
            r5 = 0
            r15.status = r5     // Catch:{ all -> 0x03c6 }
            goto L_0x023a
        L_0x0225:
            boolean r5 = r15.hasPromiseIconUi()     // Catch:{ all -> 0x03c6 }
            if (r5 == 0) goto L_0x023c
            long r5 = r15.f52id     // Catch:{ all -> 0x03c6 }
            r9 = 1
            java.lang.Boolean r14 = java.lang.Boolean.valueOf(r9)     // Catch:{ all -> 0x03c6 }
            r10.put(r5, r14)     // Catch:{ all -> 0x03c6 }
            goto L_0x02bb
        L_0x0237:
            r5 = 0
            r15.status = r5     // Catch:{ all -> 0x03c6 }
        L_0x023a:
            r21 = 1
        L_0x023c:
            if (r12 == 0) goto L_0x0249
            int r5 = r15.itemType     // Catch:{ all -> 0x03c6 }
            if (r5 != 0) goto L_0x0249
            boolean r5 = r15.usingLowResIcon     // Catch:{ all -> 0x03c6 }
            r4.getTitleAndIcon(r15, r5)     // Catch:{ all -> 0x03c6 }
            r6 = 1
            goto L_0x024b
        L_0x0249:
            r6 = r21
        L_0x024b:
            int r5 = r15.isDisabled     // Catch:{ all -> 0x03c6 }
            int r9 = r15.isDisabled     // Catch:{ all -> 0x03c6 }
            int r9 = r7.apply(r9)     // Catch:{ all -> 0x03c6 }
            r15.isDisabled = r9     // Catch:{ all -> 0x03c6 }
            int r9 = r15.isDisabled     // Catch:{ all -> 0x03c6 }
            if (r9 == r5) goto L_0x0263
            r5 = 1
            goto L_0x0264
        L_0x025b:
            r22 = r5
            r21 = r6
            r23 = r9
            r6 = r21
        L_0x0263:
            r5 = 0
        L_0x0264:
            if (r6 != 0) goto L_0x0268
            if (r5 == 0) goto L_0x026b
        L_0x0268:
            r11.add(r15)     // Catch:{ all -> 0x03c6 }
        L_0x026b:
            if (r6 == 0) goto L_0x02ba
            com.android.launcher3.model.ModelWriter r5 = r24.getModelWriter()     // Catch:{ all -> 0x03c6 }
            r5.updateItemInDatabase(r15)     // Catch:{ all -> 0x03c6 }
            goto L_0x02ba
        L_0x0275:
            r22 = r5
            goto L_0x027c
        L_0x0278:
            r22 = r5
            r20 = r6
        L_0x027c:
            r23 = r9
            boolean r5 = r15 instanceof com.android.launcher3.LauncherAppWidgetInfo     // Catch:{ all -> 0x03c6 }
            if (r5 == 0) goto L_0x02ba
            if (r12 == 0) goto L_0x02ba
            com.android.launcher3.LauncherAppWidgetInfo r15 = (com.android.launcher3.LauncherAppWidgetInfo) r15     // Catch:{ all -> 0x03c6 }
            android.os.UserHandle r5 = r1.mUser     // Catch:{ all -> 0x03c6 }
            android.os.UserHandle r6 = r15.user     // Catch:{ all -> 0x03c6 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x03c6 }
            if (r5 == 0) goto L_0x02ba
            r5 = 2
            boolean r6 = r15.hasRestoreFlag(r5)     // Catch:{ all -> 0x03c6 }
            if (r6 == 0) goto L_0x02ba
            android.content.ComponentName r5 = r15.providerName     // Catch:{ all -> 0x03c6 }
            java.lang.String r5 = r5.getPackageName()     // Catch:{ all -> 0x03c6 }
            boolean r5 = r8.contains(r5)     // Catch:{ all -> 0x03c6 }
            if (r5 == 0) goto L_0x02ba
            int r5 = r15.restoreStatus     // Catch:{ all -> 0x03c6 }
            r5 = r5 & -11
            r15.restoreStatus = r5     // Catch:{ all -> 0x03c6 }
            int r5 = r15.restoreStatus     // Catch:{ all -> 0x03c6 }
            r6 = 4
            r5 = r5 | r6
            r15.restoreStatus = r5     // Catch:{ all -> 0x03c6 }
            r13.add(r15)     // Catch:{ all -> 0x03c6 }
            com.android.launcher3.model.ModelWriter r5 = r24.getModelWriter()     // Catch:{ all -> 0x03c6 }
            r5.updateItemInDatabase(r15)     // Catch:{ all -> 0x03c6 }
            goto L_0x02bb
        L_0x02ba:
            r6 = 4
        L_0x02bb:
            r15 = r18
            r14 = r19
            r6 = r20
            r5 = r22
            r9 = r23
            goto L_0x016e
        L_0x02c7:
            r22 = r5
            r20 = r6
            r19 = r14
            monitor-exit(r26)     // Catch:{ all -> 0x03c6 }
            android.os.UserHandle r0 = r1.mUser
            r1.bindUpdatedShortcuts(r11, r0)
            boolean r0 = r10.isEmpty()
            if (r0 != 0) goto L_0x02e7
            r16 = 0
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r16)
            com.android.launcher3.util.ItemInfoMatcher r0 = com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r10, r0)
            r1.deleteAndBindComponentsRemoved(r0)
            goto L_0x02e9
        L_0x02e7:
            r16 = 0
        L_0x02e9:
            boolean r0 = r13.isEmpty()
            if (r0 != 0) goto L_0x02f7
            com.android.launcher3.model.PackageUpdatedTask$2 r0 = new com.android.launcher3.model.PackageUpdatedTask$2
            r0.<init>(r13)
            r1.scheduleCallbackTask(r0)
        L_0x02f7:
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            java.util.HashSet r4 = new java.util.HashSet
            r4.<init>()
            int r5 = r1.mOp
            r6 = 3
            if (r5 != r6) goto L_0x030e
            r5 = r22
            java.util.Collections.addAll(r0, r5)
        L_0x030b:
            r7 = r20
            goto L_0x0346
        L_0x030e:
            r5 = r22
            int r6 = r1.mOp
            r7 = 2
            if (r6 != r7) goto L_0x030b
            com.android.launcher3.compat.LauncherAppsCompat r6 = com.android.launcher3.compat.LauncherAppsCompat.getInstance(r3)
            r7 = r20
            r8 = 0
        L_0x031c:
            if (r8 >= r7) goto L_0x0330
            r9 = r5[r8]
            android.os.UserHandle r11 = r1.mUser
            boolean r9 = r6.isPackageEnabledForProfile(r9, r11)
            if (r9 != 0) goto L_0x032d
            r9 = r5[r8]
            r0.add(r9)
        L_0x032d:
            int r8 = r8 + 1
            goto L_0x031c
        L_0x0330:
            java.util.Iterator r6 = r19.iterator()
        L_0x0334:
            boolean r8 = r6.hasNext()
            if (r8 == 0) goto L_0x0346
            java.lang.Object r8 = r6.next()
            com.android.launcher3.AppInfo r8 = (com.android.launcher3.AppInfo) r8
            android.content.ComponentName r8 = r8.componentName
            r4.add(r8)
            goto L_0x0334
        L_0x0346:
            boolean r6 = r0.isEmpty()
            if (r6 == 0) goto L_0x0352
            boolean r6 = r4.isEmpty()
            if (r6 != 0) goto L_0x0377
        L_0x0352:
            android.os.UserHandle r6 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r6 = com.android.launcher3.util.ItemInfoMatcher.ofPackages(r0, r6)
            android.os.UserHandle r8 = r1.mUser
            com.android.launcher3.util.ItemInfoMatcher r4 = com.android.launcher3.util.ItemInfoMatcher.ofComponents(r4, r8)
            com.android.launcher3.util.ItemInfoMatcher r4 = r6.mo11537or(r4)
            r6 = 1
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r6)
            com.android.launcher3.util.ItemInfoMatcher r6 = com.android.launcher3.util.ItemInfoMatcher.ofItemIds(r10, r8)
            com.android.launcher3.util.ItemInfoMatcher r4 = r4.and(r6)
            r1.deleteAndBindComponentsRemoved(r4)
            android.os.UserHandle r4 = r1.mUser
            com.android.launcher3.InstallShortcutReceiver.removeFromInstallQueue(r3, r0, r4)
        L_0x0377:
            boolean r0 = r19.isEmpty()
            if (r0 != 0) goto L_0x0387
            com.android.launcher3.model.PackageUpdatedTask$3 r0 = new com.android.launcher3.model.PackageUpdatedTask$3
            r3 = r19
            r0.<init>(r3)
            r1.scheduleCallbackTask(r0)
        L_0x0387:
            boolean r0 = com.android.launcher3.Utilities.ATLEAST_MARSHMALLOW
            if (r0 != 0) goto L_0x03a3
            int r0 = r1.mOp
            r3 = 1
            if (r0 == r3) goto L_0x039a
            int r0 = r1.mOp
            r3 = 3
            if (r0 == r3) goto L_0x039a
            int r0 = r1.mOp
            r3 = 2
            if (r0 != r3) goto L_0x03a3
        L_0x039a:
            com.android.launcher3.model.PackageUpdatedTask$4 r0 = new com.android.launcher3.model.PackageUpdatedTask$4
            r0.<init>()
            r1.scheduleCallbackTask(r0)
            goto L_0x03c5
        L_0x03a3:
            boolean r0 = com.android.launcher3.Utilities.ATLEAST_OREO
            if (r0 == 0) goto L_0x03c5
            int r0 = r1.mOp
            r3 = 1
            if (r0 != r3) goto L_0x03c5
            r0 = 0
        L_0x03ad:
            if (r0 >= r7) goto L_0x03c2
            com.android.launcher3.model.WidgetsModel r3 = r2.widgetsModel
            com.android.launcher3.util.PackageUserKey r4 = new com.android.launcher3.util.PackageUserKey
            r6 = r5[r0]
            android.os.UserHandle r8 = r1.mUser
            r4.<init>(r6, r8)
            r6 = r25
            r3.update(r6, r4)
            int r0 = r0 + 1
            goto L_0x03ad
        L_0x03c2:
            r1.bindUpdatedWidgets(r2)
        L_0x03c5:
            return
        L_0x03c6:
            r0 = move-exception
            monitor-exit(r26)     // Catch:{ all -> 0x03c6 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.PackageUpdatedTask.execute(com.android.launcher3.LauncherAppState, com.android.launcher3.model.BgDataModel, com.android.launcher3.AllAppsList):void");
    }
}
