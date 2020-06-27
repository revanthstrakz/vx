package com.android.launcher3.model;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.os.Process;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.Pair;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherModel.CallbackTask;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.LauncherSettings.Settings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.ManagedProfileHeuristic.UserFolderInfo;
import com.android.launcher3.util.Provider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddWorkspaceItemsTask extends BaseModelUpdateTask {
    private final Provider<List<Pair<ItemInfo, Object>>> mAppsProvider;

    public AddWorkspaceItemsTask(Provider<List<Pair<ItemInfo, Object>>> provider) {
        this.mAppsProvider = provider;
    }

    public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
        BgDataModel bgDataModel2 = bgDataModel;
        List<Pair> list = (List) this.mAppsProvider.get();
        if (!list.isEmpty()) {
            Context context = launcherAppState.getContext();
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            ArrayMap arrayMap = new ArrayMap();
            ArrayList loadWorkspaceScreensDb = LauncherModel.loadWorkspaceScreensDb(context);
            synchronized (bgDataModel) {
                ArrayList<ItemInfo> arrayList3 = new ArrayList<>();
                for (Pair pair : list) {
                    ItemInfo itemInfo = (ItemInfo) pair.first;
                    if ((itemInfo.itemType != 0 && itemInfo.itemType != 1) || !shortcutExists(bgDataModel2, itemInfo.getIntent(), itemInfo.user)) {
                        if (itemInfo.itemType == 0) {
                            if (itemInfo instanceof AppInfo) {
                                itemInfo = ((AppInfo) itemInfo).makeShortcut();
                            }
                            if (!Process.myUserHandle().equals(itemInfo.user)) {
                                if (pair.second instanceof LauncherActivityInfo) {
                                    UserFolderInfo userFolderInfo = (UserFolderInfo) arrayMap.get(itemInfo.user);
                                    if (userFolderInfo == null) {
                                        userFolderInfo = new UserFolderInfo(context, itemInfo.user, bgDataModel2);
                                        arrayMap.put(itemInfo.user, userFolderInfo);
                                    }
                                    itemInfo = userFolderInfo.convertToWorkspaceItem((ShortcutInfo) itemInfo, (LauncherActivityInfo) pair.second);
                                }
                            }
                        }
                        if (itemInfo != null) {
                            arrayList3.add(itemInfo);
                        }
                    }
                }
                for (ItemInfo itemInfo2 : arrayList3) {
                    ItemInfo itemInfo3 = itemInfo2;
                    Pair findSpaceForItem = findSpaceForItem(launcherAppState, bgDataModel, loadWorkspaceScreensDb, arrayList2, itemInfo2.spanX, itemInfo2.spanY);
                    long longValue = ((Long) findSpaceForItem.first).longValue();
                    int[] iArr = (int[]) findSpaceForItem.second;
                    if (!(itemInfo3 instanceof ShortcutInfo) && !(itemInfo3 instanceof FolderInfo)) {
                        if (!(itemInfo3 instanceof LauncherAppWidgetInfo)) {
                            if (itemInfo3 instanceof AppInfo) {
                                itemInfo3 = ((AppInfo) itemInfo3).makeShortcut();
                            } else {
                                throw new RuntimeException("Unexpected info type");
                            }
                        }
                    }
                    getModelWriter().addItemToDatabase(itemInfo3, -100, longValue, iArr[0], iArr[1]);
                    arrayList.add(itemInfo3);
                }
            }
            updateScreens(context, loadWorkspaceScreensDb);
            if (!arrayList.isEmpty()) {
                scheduleCallbackTask(new CallbackTask() {
                    public void execute(Callbacks callbacks) {
                        ArrayList arrayList = new ArrayList();
                        ArrayList arrayList2 = new ArrayList();
                        if (!arrayList.isEmpty()) {
                            long j = ((ItemInfo) arrayList.get(arrayList.size() - 1)).screenId;
                            Iterator it = arrayList.iterator();
                            while (it.hasNext()) {
                                ItemInfo itemInfo = (ItemInfo) it.next();
                                if (itemInfo.screenId == j) {
                                    arrayList.add(itemInfo);
                                } else {
                                    arrayList2.add(itemInfo);
                                }
                            }
                        }
                        callbacks.bindAppsAdded(arrayList2, arrayList2, arrayList);
                    }
                });
            }
            for (UserFolderInfo applyPendingState : arrayMap.values()) {
                applyPendingState.applyPendingState(getModelWriter());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateScreens(Context context, ArrayList<Long> arrayList) {
        LauncherModel.updateWorkspaceScreenOrder(context, arrayList);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00be, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shortcutExists(com.android.launcher3.model.BgDataModel r12, android.content.Intent r13, android.os.UserHandle r14) {
        /*
            r11 = this;
            r0 = 1
            if (r13 != 0) goto L_0x0004
            return r0
        L_0x0004:
            android.content.ComponentName r1 = r13.getComponent()
            r2 = 0
            r3 = 0
            if (r1 == 0) goto L_0x003e
            android.content.ComponentName r1 = r13.getComponent()
            java.lang.String r1 = r1.getPackageName()
            java.lang.String r4 = r13.getPackage()
            if (r4 == 0) goto L_0x002c
            java.lang.String r4 = r13.toUri(r3)
            android.content.Intent r5 = new android.content.Intent
            r5.<init>(r13)
            android.content.Intent r2 = r5.setPackage(r2)
            java.lang.String r2 = r2.toUri(r3)
            goto L_0x0049
        L_0x002c:
            android.content.Intent r2 = new android.content.Intent
            r2.<init>(r13)
            android.content.Intent r2 = r2.setPackage(r1)
            java.lang.String r4 = r2.toUri(r3)
            java.lang.String r2 = r13.toUri(r3)
            goto L_0x0049
        L_0x003e:
            java.lang.String r4 = r13.toUri(r3)
            java.lang.String r1 = r13.toUri(r3)
            r10 = r2
            r2 = r1
            r1 = r10
        L_0x0049:
            boolean r5 = com.android.launcher3.Utilities.isLauncherAppTarget(r13)
            monitor-enter(r12)
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r6 = r12.itemsIdMap     // Catch:{ all -> 0x00c1 }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x00c1 }
        L_0x0054:
            boolean r7 = r6.hasNext()     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x00bf
            java.lang.Object r7 = r6.next()     // Catch:{ all -> 0x00c1 }
            com.android.launcher3.ItemInfo r7 = (com.android.launcher3.ItemInfo) r7     // Catch:{ all -> 0x00c1 }
            boolean r8 = r7 instanceof com.android.launcher3.ShortcutInfo     // Catch:{ all -> 0x00c1 }
            if (r8 == 0) goto L_0x0054
            r8 = r7
            com.android.launcher3.ShortcutInfo r8 = (com.android.launcher3.ShortcutInfo) r8     // Catch:{ all -> 0x00c1 }
            android.content.Intent r9 = r7.getIntent()     // Catch:{ all -> 0x00c1 }
            if (r9 == 0) goto L_0x0054
            android.os.UserHandle r9 = r8.user     // Catch:{ all -> 0x00c1 }
            boolean r9 = r9.equals(r14)     // Catch:{ all -> 0x00c1 }
            if (r9 == 0) goto L_0x0054
            android.content.Intent r9 = new android.content.Intent     // Catch:{ all -> 0x00c1 }
            android.content.Intent r7 = r7.getIntent()     // Catch:{ all -> 0x00c1 }
            r9.<init>(r7)     // Catch:{ all -> 0x00c1 }
            android.graphics.Rect r7 = r13.getSourceBounds()     // Catch:{ all -> 0x00c1 }
            r9.setSourceBounds(r7)     // Catch:{ all -> 0x00c1 }
            java.lang.String r7 = r9.toUri(r3)     // Catch:{ all -> 0x00c1 }
            boolean r9 = r4.equals(r7)     // Catch:{ all -> 0x00c1 }
            if (r9 != 0) goto L_0x00bd
            boolean r7 = r2.equals(r7)     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0096
            goto L_0x00bd
        L_0x0096:
            if (r5 == 0) goto L_0x0054
            boolean r7 = r8.isPromise()     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            r7 = 2
            boolean r7 = r8.hasStatusFlag(r7)     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            android.content.ComponentName r7 = r8.getTargetComponent()     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            if (r1 == 0) goto L_0x0054
            android.content.ComponentName r7 = r8.getTargetComponent()     // Catch:{ all -> 0x00c1 }
            java.lang.String r7 = r7.getPackageName()     // Catch:{ all -> 0x00c1 }
            boolean r7 = r1.equals(r7)     // Catch:{ all -> 0x00c1 }
            if (r7 == 0) goto L_0x0054
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            return r0
        L_0x00bd:
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            return r0
        L_0x00bf:
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            return r3
        L_0x00c1:
            r13 = move-exception
            monitor-exit(r12)     // Catch:{ all -> 0x00c1 }
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.AddWorkspaceItemsTask.shortcutExists(com.android.launcher3.model.BgDataModel, android.content.Intent, android.os.UserHandle):boolean");
    }

    /* access modifiers changed from: protected */
    public Pair<Long, int[]> findSpaceForItem(LauncherAppState launcherAppState, BgDataModel bgDataModel, ArrayList<Long> arrayList, ArrayList<Long> arrayList2, int i, int i2) {
        long j;
        boolean z;
        long j2;
        BgDataModel bgDataModel2 = bgDataModel;
        ArrayList<Long> arrayList3 = arrayList;
        LongSparseArray longSparseArray = new LongSparseArray();
        synchronized (bgDataModel) {
            Iterator it = bgDataModel2.itemsIdMap.iterator();
            while (it.hasNext()) {
                ItemInfo itemInfo = (ItemInfo) it.next();
                if (itemInfo.container == -100) {
                    ArrayList arrayList4 = (ArrayList) longSparseArray.get(itemInfo.screenId);
                    if (arrayList4 == null) {
                        arrayList4 = new ArrayList();
                        longSparseArray.put(itemInfo.screenId, arrayList4);
                    }
                    arrayList4.add(itemInfo);
                }
            }
        }
        int[] iArr = new int[2];
        int size = arrayList.size();
        int i3 = !arrayList.isEmpty();
        if (i3 < size) {
            long longValue = ((Long) arrayList3.get(i3)).longValue();
            long j3 = longValue;
            z = findNextAvailableIconSpaceInScreen(launcherAppState, (ArrayList) longSparseArray.get(longValue), iArr, i, i2);
            j = j3;
        } else {
            j = 0;
            z = false;
        }
        if (!z) {
            long j4 = j;
            int i4 = 1;
            while (true) {
                if (i4 >= size) {
                    j2 = j4;
                    break;
                }
                j2 = ((Long) arrayList3.get(i4)).longValue();
                if (findNextAvailableIconSpaceInScreen(launcherAppState, (ArrayList) longSparseArray.get(j2), iArr, i, i2)) {
                    z = true;
                    break;
                }
                i4++;
                j4 = j2;
            }
        } else {
            j2 = j;
        }
        if (!z) {
            j2 = Settings.call(launcherAppState.getContext().getContentResolver(), Settings.METHOD_NEW_SCREEN_ID).getLong("value");
            arrayList3.add(Long.valueOf(j2));
            arrayList2.add(Long.valueOf(j2));
            if (!findNextAvailableIconSpaceInScreen(launcherAppState, (ArrayList) longSparseArray.get(j2), iArr, i, i2)) {
                throw new RuntimeException("Can't find space to add the item");
            }
        }
        return Pair.create(Long.valueOf(j2), iArr);
    }

    private boolean findNextAvailableIconSpaceInScreen(LauncherAppState launcherAppState, ArrayList<ItemInfo> arrayList, int[] iArr, int i, int i2) {
        InvariantDeviceProfile invariantDeviceProfile = launcherAppState.getInvariantDeviceProfile();
        GridOccupancy gridOccupancy = new GridOccupancy(invariantDeviceProfile.numColumns, invariantDeviceProfile.numRows);
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                gridOccupancy.markCells((ItemInfo) it.next(), true);
            }
        }
        return gridOccupancy.findVacantCell(iArr, i, i2);
    }
}
