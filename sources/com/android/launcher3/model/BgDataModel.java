package com.android.launcher3.model;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.MutableInt;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.DumpTargetWrapper;
import com.android.launcher3.model.nano.LauncherDumpProto.DumpTarget;
import com.android.launcher3.model.nano.LauncherDumpProto.LauncherImpression;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.MultiHashMap;
import com.google.protobuf.nano.MessageNano;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BgDataModel {
    private static final String TAG = "BgDataModel";
    public final ArrayList<LauncherAppWidgetInfo> appWidgets = new ArrayList<>();
    public final MultiHashMap<ComponentKey, String> deepShortcutMap = new MultiHashMap<>();
    public final LongArrayMap<FolderInfo> folders = new LongArrayMap<>();
    public boolean hasShortcutHostPermission;
    public final LongArrayMap<ItemInfo> itemsIdMap = new LongArrayMap<>();
    public final Map<ShortcutKey, MutableInt> pinnedShortcutCounts = new HashMap();
    public final WidgetsModel widgetsModel = new WidgetsModel();
    public final ArrayList<ItemInfo> workspaceItems = new ArrayList<>();
    public final ArrayList<Long> workspaceScreens = new ArrayList<>();

    public synchronized void clear() {
        this.workspaceItems.clear();
        this.appWidgets.clear();
        this.folders.clear();
        this.itemsIdMap.clear();
        this.workspaceScreens.clear();
        this.pinnedShortcutCounts.clear();
        this.deepShortcutMap.clear();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x01e6, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void dump(java.lang.String r5, java.io.FileDescriptor r6, java.io.PrintWriter r7, java.lang.String[] r8) {
        /*
            r4 = this;
            monitor-enter(r4)
            int r0 = r8.length     // Catch:{ all -> 0x01e7 }
            r1 = 0
            if (r0 <= 0) goto L_0x0014
            r0 = r8[r1]     // Catch:{ all -> 0x01e7 }
            java.lang.String r2 = "--proto"
            boolean r0 = android.text.TextUtils.equals(r0, r2)     // Catch:{ all -> 0x01e7 }
            if (r0 == 0) goto L_0x0014
            r4.dumpProto(r5, r6, r7, r8)     // Catch:{ all -> 0x01e7 }
            monitor-exit(r4)
            return
        L_0x0014:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r6.<init>()     // Catch:{ all -> 0x01e7 }
            r6.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = "Data Model:"
            r6.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r6)     // Catch:{ all -> 0x01e7 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r6.<init>()     // Catch:{ all -> 0x01e7 }
            r6.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = " ---- workspace screens: "
            r6.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e7 }
            r7.print(r6)     // Catch:{ all -> 0x01e7 }
            r6 = 0
        L_0x003d:
            java.util.ArrayList<java.lang.Long> r0 = r4.workspaceScreens     // Catch:{ all -> 0x01e7 }
            int r0 = r0.size()     // Catch:{ all -> 0x01e7 }
            if (r6 >= r0) goto L_0x0068
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r0.<init>()     // Catch:{ all -> 0x01e7 }
            java.lang.String r2 = " "
            r0.append(r2)     // Catch:{ all -> 0x01e7 }
            java.util.ArrayList<java.lang.Long> r2 = r4.workspaceScreens     // Catch:{ all -> 0x01e7 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ all -> 0x01e7 }
            java.lang.Long r2 = (java.lang.Long) r2     // Catch:{ all -> 0x01e7 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e7 }
            r0.append(r2)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e7 }
            r7.print(r0)     // Catch:{ all -> 0x01e7 }
            int r6 = r6 + 1
            goto L_0x003d
        L_0x0068:
            r7.println()     // Catch:{ all -> 0x01e7 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r6.<init>()     // Catch:{ all -> 0x01e7 }
            r6.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = " ---- workspace items "
            r6.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r6)     // Catch:{ all -> 0x01e7 }
            r6 = 0
        L_0x0080:
            java.util.ArrayList<com.android.launcher3.ItemInfo> r0 = r4.workspaceItems     // Catch:{ all -> 0x01e7 }
            int r0 = r0.size()     // Catch:{ all -> 0x01e7 }
            r2 = 9
            if (r6 >= r0) goto L_0x00ae
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r0.<init>()     // Catch:{ all -> 0x01e7 }
            r0.append(r5)     // Catch:{ all -> 0x01e7 }
            r0.append(r2)     // Catch:{ all -> 0x01e7 }
            java.util.ArrayList<com.android.launcher3.ItemInfo> r2 = r4.workspaceItems     // Catch:{ all -> 0x01e7 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ all -> 0x01e7 }
            com.android.launcher3.ItemInfo r2 = (com.android.launcher3.ItemInfo) r2     // Catch:{ all -> 0x01e7 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e7 }
            r0.append(r2)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r0)     // Catch:{ all -> 0x01e7 }
            int r6 = r6 + 1
            goto L_0x0080
        L_0x00ae:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r6.<init>()     // Catch:{ all -> 0x01e7 }
            r6.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = " ---- appwidget items "
            r6.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r6)     // Catch:{ all -> 0x01e7 }
            r6 = 0
        L_0x00c3:
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r0 = r4.appWidgets     // Catch:{ all -> 0x01e7 }
            int r0 = r0.size()     // Catch:{ all -> 0x01e7 }
            if (r6 >= r0) goto L_0x00ef
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r0.<init>()     // Catch:{ all -> 0x01e7 }
            r0.append(r5)     // Catch:{ all -> 0x01e7 }
            r0.append(r2)     // Catch:{ all -> 0x01e7 }
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r3 = r4.appWidgets     // Catch:{ all -> 0x01e7 }
            java.lang.Object r3 = r3.get(r6)     // Catch:{ all -> 0x01e7 }
            com.android.launcher3.LauncherAppWidgetInfo r3 = (com.android.launcher3.LauncherAppWidgetInfo) r3     // Catch:{ all -> 0x01e7 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01e7 }
            r0.append(r3)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r0)     // Catch:{ all -> 0x01e7 }
            int r6 = r6 + 1
            goto L_0x00c3
        L_0x00ef:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r6.<init>()     // Catch:{ all -> 0x01e7 }
            r6.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = " ---- folder items "
            r6.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r6)     // Catch:{ all -> 0x01e7 }
            r6 = 0
        L_0x0104:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r0 = r4.folders     // Catch:{ all -> 0x01e7 }
            int r0 = r0.size()     // Catch:{ all -> 0x01e7 }
            if (r6 >= r0) goto L_0x0130
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r0.<init>()     // Catch:{ all -> 0x01e7 }
            r0.append(r5)     // Catch:{ all -> 0x01e7 }
            r0.append(r2)     // Catch:{ all -> 0x01e7 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r3 = r4.folders     // Catch:{ all -> 0x01e7 }
            java.lang.Object r3 = r3.valueAt(r6)     // Catch:{ all -> 0x01e7 }
            com.android.launcher3.FolderInfo r3 = (com.android.launcher3.FolderInfo) r3     // Catch:{ all -> 0x01e7 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01e7 }
            r0.append(r3)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r0)     // Catch:{ all -> 0x01e7 }
            int r6 = r6 + 1
            goto L_0x0104
        L_0x0130:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r6.<init>()     // Catch:{ all -> 0x01e7 }
            r6.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = " ---- items id map "
            r6.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r6)     // Catch:{ all -> 0x01e7 }
            r6 = 0
        L_0x0145:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r0 = r4.itemsIdMap     // Catch:{ all -> 0x01e7 }
            int r0 = r0.size()     // Catch:{ all -> 0x01e7 }
            if (r6 >= r0) goto L_0x0171
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r0.<init>()     // Catch:{ all -> 0x01e7 }
            r0.append(r5)     // Catch:{ all -> 0x01e7 }
            r0.append(r2)     // Catch:{ all -> 0x01e7 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r3 = r4.itemsIdMap     // Catch:{ all -> 0x01e7 }
            java.lang.Object r3 = r3.valueAt(r6)     // Catch:{ all -> 0x01e7 }
            com.android.launcher3.ItemInfo r3 = (com.android.launcher3.ItemInfo) r3     // Catch:{ all -> 0x01e7 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01e7 }
            r0.append(r3)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r0)     // Catch:{ all -> 0x01e7 }
            int r6 = r6 + 1
            goto L_0x0145
        L_0x0171:
            int r6 = r8.length     // Catch:{ all -> 0x01e7 }
            if (r6 <= 0) goto L_0x01e5
            r6 = r8[r1]     // Catch:{ all -> 0x01e7 }
            java.lang.String r8 = "--all"
            boolean r6 = android.text.TextUtils.equals(r6, r8)     // Catch:{ all -> 0x01e7 }
            if (r6 == 0) goto L_0x01e5
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r6.<init>()     // Catch:{ all -> 0x01e7 }
            r6.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r8 = "shortcuts"
            r6.append(r8)     // Catch:{ all -> 0x01e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01e7 }
            r7.println(r6)     // Catch:{ all -> 0x01e7 }
            com.android.launcher3.util.MultiHashMap<com.android.launcher3.util.ComponentKey, java.lang.String> r6 = r4.deepShortcutMap     // Catch:{ all -> 0x01e7 }
            java.util.Collection r6 = r6.values()     // Catch:{ all -> 0x01e7 }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x01e7 }
        L_0x019c:
            boolean r8 = r6.hasNext()     // Catch:{ all -> 0x01e7 }
            if (r8 == 0) goto L_0x01e5
            java.lang.Object r8 = r6.next()     // Catch:{ all -> 0x01e7 }
            java.util.ArrayList r8 = (java.util.ArrayList) r8     // Catch:{ all -> 0x01e7 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r0.<init>()     // Catch:{ all -> 0x01e7 }
            r0.append(r5)     // Catch:{ all -> 0x01e7 }
            java.lang.String r1 = "  "
            r0.append(r1)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e7 }
            r7.print(r0)     // Catch:{ all -> 0x01e7 }
            java.util.Iterator r8 = r8.iterator()     // Catch:{ all -> 0x01e7 }
        L_0x01c0:
            boolean r0 = r8.hasNext()     // Catch:{ all -> 0x01e7 }
            if (r0 == 0) goto L_0x01e1
            java.lang.Object r0 = r8.next()     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x01e7 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e7 }
            r1.<init>()     // Catch:{ all -> 0x01e7 }
            r1.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = ", "
            r1.append(r0)     // Catch:{ all -> 0x01e7 }
            java.lang.String r0 = r1.toString()     // Catch:{ all -> 0x01e7 }
            r7.print(r0)     // Catch:{ all -> 0x01e7 }
            goto L_0x01c0
        L_0x01e1:
            r7.println()     // Catch:{ all -> 0x01e7 }
            goto L_0x019c
        L_0x01e5:
            monitor-exit(r4)
            return
        L_0x01e7:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.BgDataModel.dump(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    private synchronized void dumpProto(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String[] strArr2 = strArr;
        synchronized (this) {
            int i = 0;
            DumpTargetWrapper dumpTargetWrapper = new DumpTargetWrapper(2, 0);
            LongArrayMap longArrayMap = new LongArrayMap();
            for (int i2 = 0; i2 < this.workspaceScreens.size(); i2++) {
                longArrayMap.put(((Long) this.workspaceScreens.get(i2)).longValue(), new DumpTargetWrapper(1, i2));
            }
            for (int i3 = 0; i3 < this.folders.size(); i3++) {
                FolderInfo folderInfo = (FolderInfo) this.folders.valueAt(i3);
                DumpTargetWrapper dumpTargetWrapper2 = new DumpTargetWrapper(3, this.folders.size());
                dumpTargetWrapper2.writeToDumpTarget(folderInfo);
                Iterator it = folderInfo.contents.iterator();
                while (it.hasNext()) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) it.next();
                    DumpTargetWrapper dumpTargetWrapper3 = new DumpTargetWrapper(shortcutInfo);
                    dumpTargetWrapper3.writeToDumpTarget(shortcutInfo);
                    dumpTargetWrapper2.add(dumpTargetWrapper3);
                }
                if (folderInfo.container == -101) {
                    dumpTargetWrapper.add(dumpTargetWrapper2);
                } else if (folderInfo.container == -100) {
                    ((DumpTargetWrapper) longArrayMap.get(folderInfo.screenId)).add(dumpTargetWrapper2);
                }
            }
            for (int i4 = 0; i4 < this.workspaceItems.size(); i4++) {
                ItemInfo itemInfo = (ItemInfo) this.workspaceItems.get(i4);
                if (!(itemInfo instanceof FolderInfo)) {
                    DumpTargetWrapper dumpTargetWrapper4 = new DumpTargetWrapper(itemInfo);
                    dumpTargetWrapper4.writeToDumpTarget(itemInfo);
                    if (itemInfo.container == -101) {
                        dumpTargetWrapper.add(dumpTargetWrapper4);
                    } else if (itemInfo.container == -100) {
                        ((DumpTargetWrapper) longArrayMap.get(itemInfo.screenId)).add(dumpTargetWrapper4);
                    }
                }
            }
            for (int i5 = 0; i5 < this.appWidgets.size(); i5++) {
                ItemInfo itemInfo2 = (ItemInfo) this.appWidgets.get(i5);
                DumpTargetWrapper dumpTargetWrapper5 = new DumpTargetWrapper(itemInfo2);
                dumpTargetWrapper5.writeToDumpTarget(itemInfo2);
                if (itemInfo2.container == -101) {
                    dumpTargetWrapper.add(dumpTargetWrapper5);
                } else if (itemInfo2.container == -100) {
                    ((DumpTargetWrapper) longArrayMap.get(itemInfo2.screenId)).add(dumpTargetWrapper5);
                }
            }
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(dumpTargetWrapper.getFlattenedList());
            for (int i6 = 0; i6 < longArrayMap.size(); i6++) {
                arrayList.addAll(((DumpTargetWrapper) longArrayMap.valueAt(i6)).getFlattenedList());
            }
            if (strArr2.length <= 1 || !TextUtils.equals(strArr2[1], "--debug")) {
                LauncherImpression launcherImpression = new LauncherImpression();
                launcherImpression.targets = new DumpTarget[arrayList.size()];
                while (i < arrayList.size()) {
                    launcherImpression.targets[i] = (DumpTarget) arrayList.get(i);
                    i++;
                }
                try {
                    new FileOutputStream(fileDescriptor).write(MessageNano.toByteArray(launcherImpression));
                    String str2 = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append(MessageNano.toByteArray(launcherImpression).length);
                    sb.append("Bytes");
                    Log.d(str2, sb.toString());
                } catch (IOException e) {
                    Log.e(TAG, "Exception writing dumpsys --proto", e);
                }
            } else {
                while (i < arrayList.size()) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(DumpTargetWrapper.getDumpTargetStr((DumpTarget) arrayList.get(i)));
                    printWriter.println(sb2.toString());
                    i++;
                }
                return;
            }
        }
        return;
    }

    public synchronized void removeItem(Context context, ItemInfo... itemInfoArr) {
        removeItem(context, (Iterable<? extends ItemInfo>) Arrays.asList(itemInfoArr));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002f, code lost:
        if (r3 == 0) goto L_0x0031;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeItem(android.content.Context r5, java.lang.Iterable<? extends com.android.launcher3.ItemInfo> r6) {
        /*
            r4 = this;
            monitor-enter(r4)
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x0065 }
        L_0x0005:
            boolean r0 = r6.hasNext()     // Catch:{ all -> 0x0065 }
            if (r0 == 0) goto L_0x0063
            java.lang.Object r0 = r6.next()     // Catch:{ all -> 0x0065 }
            com.android.launcher3.ItemInfo r0 = (com.android.launcher3.ItemInfo) r0     // Catch:{ all -> 0x0065 }
            int r1 = r0.itemType     // Catch:{ all -> 0x0065 }
            switch(r1) {
                case 0: goto L_0x0056;
                case 1: goto L_0x0056;
                case 2: goto L_0x0049;
                case 3: goto L_0x0016;
                case 4: goto L_0x0043;
                case 5: goto L_0x0043;
                case 6: goto L_0x0017;
                default: goto L_0x0016;
            }     // Catch:{ all -> 0x0065 }
        L_0x0016:
            goto L_0x005b
        L_0x0017:
            boolean r1 = com.android.launcher3.Utilities.ATLEAST_NOUGAT_MR1     // Catch:{ all -> 0x0065 }
            if (r1 == 0) goto L_0x0056
            com.android.launcher3.shortcuts.ShortcutKey r1 = com.android.launcher3.shortcuts.ShortcutKey.fromItemInfo(r0)     // Catch:{ all -> 0x0065 }
            java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r2 = r4.pinnedShortcutCounts     // Catch:{ all -> 0x0065 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x0065 }
            android.util.MutableInt r2 = (android.util.MutableInt) r2     // Catch:{ all -> 0x0065 }
            if (r2 == 0) goto L_0x0031
            int r3 = r2.value     // Catch:{ all -> 0x0065 }
            int r3 = r3 + -1
            r2.value = r3     // Catch:{ all -> 0x0065 }
            if (r3 != 0) goto L_0x0056
        L_0x0031:
            java.util.HashSet r2 = com.android.launcher3.InstallShortcutReceiver.getPendingShortcuts(r5)     // Catch:{ all -> 0x0065 }
            boolean r2 = r2.contains(r1)     // Catch:{ all -> 0x0065 }
            if (r2 != 0) goto L_0x0056
            com.android.launcher3.shortcuts.DeepShortcutManager r2 = com.android.launcher3.shortcuts.DeepShortcutManager.getInstance(r5)     // Catch:{ all -> 0x0065 }
            r2.unpinShortcut(r1)     // Catch:{ all -> 0x0065 }
            goto L_0x0056
        L_0x0043:
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r1 = r4.appWidgets     // Catch:{ all -> 0x0065 }
            r1.remove(r0)     // Catch:{ all -> 0x0065 }
            goto L_0x005b
        L_0x0049:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r1 = r4.folders     // Catch:{ all -> 0x0065 }
            long r2 = r0.f52id     // Catch:{ all -> 0x0065 }
            r1.remove(r2)     // Catch:{ all -> 0x0065 }
            java.util.ArrayList<com.android.launcher3.ItemInfo> r1 = r4.workspaceItems     // Catch:{ all -> 0x0065 }
            r1.remove(r0)     // Catch:{ all -> 0x0065 }
            goto L_0x005b
        L_0x0056:
            java.util.ArrayList<com.android.launcher3.ItemInfo> r1 = r4.workspaceItems     // Catch:{ all -> 0x0065 }
            r1.remove(r0)     // Catch:{ all -> 0x0065 }
        L_0x005b:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r1 = r4.itemsIdMap     // Catch:{ all -> 0x0065 }
            long r2 = r0.f52id     // Catch:{ all -> 0x0065 }
            r1.remove(r2)     // Catch:{ all -> 0x0065 }
            goto L_0x0005
        L_0x0063:
            monitor-exit(r4)
            return
        L_0x0065:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.BgDataModel.removeItem(android.content.Context, java.lang.Iterable):void");
    }

    public synchronized void addItem(Context context, ItemInfo itemInfo, boolean z) {
        this.itemsIdMap.put(itemInfo.f52id, itemInfo);
        switch (itemInfo.itemType) {
            case 0:
            case 1:
                break;
            case 2:
                this.folders.put(itemInfo.f52id, (FolderInfo) itemInfo);
                this.workspaceItems.add(itemInfo);
                break;
            case 4:
            case 5:
                this.appWidgets.add((LauncherAppWidgetInfo) itemInfo);
                break;
            case 6:
                if (Utilities.ATLEAST_NOUGAT_MR1) {
                    ShortcutKey fromItemInfo = ShortcutKey.fromItemInfo(itemInfo);
                    MutableInt mutableInt = (MutableInt) this.pinnedShortcutCounts.get(fromItemInfo);
                    if (mutableInt == null) {
                        mutableInt = new MutableInt(1);
                        this.pinnedShortcutCounts.put(fromItemInfo, mutableInt);
                    } else {
                        mutableInt.value++;
                    }
                    if (z && mutableInt.value == 1) {
                        DeepShortcutManager.getInstance(context).pinShortcut(fromItemInfo);
                        break;
                    }
                }
                break;
        }
        if (itemInfo.container != -100) {
            if (itemInfo.container != -101) {
                if (!z) {
                    findOrMakeFolder(itemInfo.container).add((ShortcutInfo) itemInfo, false);
                } else if (!this.folders.containsKey(itemInfo.container)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("adding item: ");
                    sb.append(itemInfo);
                    sb.append(" to a folder that ");
                    sb.append(" doesn't exist");
                    Log.e(TAG, sb.toString());
                }
            }
        }
        this.workspaceItems.add(itemInfo);
    }

    public synchronized FolderInfo findOrMakeFolder(long j) {
        FolderInfo folderInfo;
        folderInfo = (FolderInfo) this.folders.get(j);
        if (folderInfo == null) {
            folderInfo = new FolderInfo();
            this.folders.put(j, folderInfo);
        }
        return folderInfo;
    }

    public synchronized void updateDeepShortcutMap(String str, UserHandle userHandle, List<ShortcutInfoCompat> list) {
        if (str != null) {
            try {
                Iterator it = this.deepShortcutMap.keySet().iterator();
                while (it.hasNext()) {
                    ComponentKey componentKey = (ComponentKey) it.next();
                    if (componentKey.componentName.getPackageName().equals(str) && componentKey.user.equals(userHandle)) {
                        it.remove();
                    }
                }
            } finally {
            }
        }
        for (ShortcutInfoCompat shortcutInfoCompat : list) {
            if (shortcutInfoCompat.isEnabled() && (shortcutInfoCompat.isDeclaredInManifest() || shortcutInfoCompat.isDynamic())) {
                this.deepShortcutMap.addToList(new ComponentKey(shortcutInfoCompat.getActivity(), shortcutInfoCompat.getUserHandle()), shortcutInfoCompat.getId());
            }
        }
    }
}
