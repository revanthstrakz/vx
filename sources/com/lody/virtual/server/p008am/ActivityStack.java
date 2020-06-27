package com.lody.virtual.server.p008am;

import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.SparseArray;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.ClassUtils;
import com.lody.virtual.helper.utils.ComponentUtils;
import com.lody.virtual.remote.AppTaskInfo;
import com.lody.virtual.remote.StubActivityRecord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import mirror.android.app.ActivityManagerNative;
import mirror.android.app.ActivityThread;
import mirror.android.app.IActivityManager;
import mirror.android.app.IApplicationThread;

/* renamed from: com.lody.virtual.server.am.ActivityStack */
class ActivityStack {
    private final ActivityManager mAM;
    private final SparseArray<TaskRecord> mHistory = new SparseArray<>();
    private final VActivityManagerService mService;

    /* renamed from: com.lody.virtual.server.am.ActivityStack$ClearTarget */
    private enum ClearTarget {
        NOTHING,
        SPEC_ACTIVITY,
        TASK(true),
        TOP(true);
        
        boolean deliverIntent;

        private ClearTarget(boolean z) {
            this.deliverIntent = z;
        }
    }

    /* renamed from: com.lody.virtual.server.am.ActivityStack$ReuseTarget */
    private enum ReuseTarget {
        CURRENT,
        AFFINITY,
        DOCUMENT,
        MULTIPLE
    }

    ActivityStack(VActivityManagerService vActivityManagerService) {
        this.mService = vActivityManagerService;
        this.mAM = (ActivityManager) VirtualCore.get().getContext().getSystemService(ServiceManagerNative.ACTIVITY);
    }

    private static void removeFlags(Intent intent, int i) {
        intent.setFlags((~i) & intent.getFlags());
    }

    private static boolean containFlags(Intent intent, int i) {
        return (intent.getFlags() & i) != 0;
    }

    private static ActivityRecord topActivityInTask(TaskRecord taskRecord) {
        synchronized (taskRecord.activities) {
            for (int size = taskRecord.activities.size() - 1; size >= 0; size--) {
                ActivityRecord activityRecord = (ActivityRecord) taskRecord.activities.get(size);
                if (!activityRecord.marked) {
                    return activityRecord;
                }
            }
            return null;
        }
    }

    private void deliverNewIntentLocked(ActivityRecord activityRecord, ActivityRecord activityRecord2, Intent intent) {
        if (activityRecord2 != null) {
            try {
                activityRecord2.process.client.scheduleNewIntent(activityRecord != null ? activityRecord.component.getPackageName() : "android", activityRecord2.token, intent);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (NullPointerException e2) {
                e2.printStackTrace();
            }
        }
    }

    private TaskRecord findTaskByAffinityLocked(int i, String str) {
        for (int i2 = 0; i2 < this.mHistory.size(); i2++) {
            TaskRecord taskRecord = (TaskRecord) this.mHistory.valueAt(i2);
            if (i == taskRecord.userId && str.equals(taskRecord.affinity)) {
                return taskRecord;
            }
        }
        return null;
    }

    private TaskRecord findTaskByIntentLocked(int i, Intent intent) {
        for (int i2 = 0; i2 < this.mHistory.size(); i2++) {
            TaskRecord taskRecord = (TaskRecord) this.mHistory.valueAt(i2);
            if (i == taskRecord.userId && taskRecord.taskRoot != null && intent.getComponent().equals(taskRecord.taskRoot.getComponent())) {
                return taskRecord;
            }
        }
        return null;
    }

    private ActivityRecord findActivityByToken(int i, IBinder iBinder) {
        ActivityRecord activityRecord = null;
        if (iBinder != null) {
            for (int i2 = 0; i2 < this.mHistory.size(); i2++) {
                TaskRecord taskRecord = (TaskRecord) this.mHistory.valueAt(i2);
                if (taskRecord.userId == i) {
                    synchronized (taskRecord.activities) {
                        for (ActivityRecord activityRecord2 : taskRecord.activities) {
                            if (activityRecord2.token == iBinder) {
                                activityRecord = activityRecord2;
                            }
                        }
                    }
                }
            }
        }
        return activityRecord;
    }

    private boolean markTaskByClearTarget(TaskRecord taskRecord, ClearTarget clearTarget, ComponentName componentName) {
        boolean z;
        int i;
        synchronized (taskRecord.activities) {
            z = false;
            switch (clearTarget) {
                case TASK:
                    for (ActivityRecord activityRecord : taskRecord.activities) {
                        activityRecord.marked = true;
                        z = true;
                    }
                    break;
                case SPEC_ACTIVITY:
                    for (ActivityRecord activityRecord2 : taskRecord.activities) {
                        if (activityRecord2.component.equals(componentName)) {
                            activityRecord2.marked = true;
                            z = true;
                        }
                    }
                    break;
                case TOP:
                    int size = taskRecord.activities.size();
                    while (true) {
                        i = size - 1;
                        if (size > 0) {
                            if (((ActivityRecord) taskRecord.activities.get(i)).component.equals(componentName)) {
                                z = true;
                            } else {
                                size = i;
                            }
                        }
                    }
                    if (z) {
                        while (true) {
                            int i2 = i + 1;
                            if (i >= taskRecord.activities.size() - 1) {
                                break;
                            } else {
                                ((ActivityRecord) taskRecord.activities.get(i2)).marked = true;
                                i = i2;
                            }
                        }
                    }
                    break;
            }
        }
        return z;
    }

    private void optimizeTasksLocked() {
        ArrayList arrayList = new ArrayList(this.mAM.getRecentTasks(Integer.MAX_VALUE, 3));
        int size = this.mHistory.size();
        while (true) {
            int i = size - 1;
            if (size > 0) {
                TaskRecord taskRecord = (TaskRecord) this.mHistory.valueAt(i);
                ListIterator listIterator = arrayList.listIterator();
                boolean z = false;
                while (true) {
                    if (listIterator.hasNext()) {
                        if (((RecentTaskInfo) listIterator.next()).id == taskRecord.taskId) {
                            z = true;
                            listIterator.remove();
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (!z) {
                    this.mHistory.removeAt(i);
                }
                size = i;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int startActivitiesLocked(int i, Intent[] intentArr, ActivityInfo[] activityInfoArr, String[] strArr, IBinder iBinder, Bundle bundle) {
        optimizeTasksLocked();
        ReuseTarget reuseTarget = ReuseTarget.CURRENT;
        Intent intent = intentArr[0];
        ActivityInfo activityInfo = activityInfoArr[0];
        ActivityRecord findActivityByToken = findActivityByToken(i, iBinder);
        if (findActivityByToken != null && findActivityByToken.launchMode == 3) {
            intent.addFlags(268435456);
        }
        if (containFlags(intent, 67108864)) {
            removeFlags(intent, 131072);
        }
        if (containFlags(intent, 32768) && !containFlags(intent, 268435456)) {
            removeFlags(intent, 32768);
        }
        if (VERSION.SDK_INT >= 21) {
            switch (activityInfo.documentLaunchMode) {
                case 1:
                    reuseTarget = ReuseTarget.DOCUMENT;
                    break;
                case 2:
                    reuseTarget = ReuseTarget.MULTIPLE;
                    break;
            }
        }
        if (containFlags(intent, 268435456)) {
            reuseTarget = containFlags(intent, 134217728) ? ReuseTarget.MULTIPLE : ReuseTarget.AFFINITY;
        } else if (activityInfo.launchMode == 2) {
            reuseTarget = containFlags(intent, 134217728) ? ReuseTarget.MULTIPLE : ReuseTarget.AFFINITY;
        }
        if (findActivityByToken == null && reuseTarget == ReuseTarget.CURRENT) {
            reuseTarget = ReuseTarget.AFFINITY;
        }
        TaskRecord taskRecord = reuseTarget == ReuseTarget.AFFINITY ? findTaskByAffinityLocked(i, ComponentUtils.getTaskAffinity(activityInfo)) : reuseTarget == ReuseTarget.CURRENT ? findActivityByToken.task : reuseTarget == ReuseTarget.DOCUMENT ? findTaskByIntentLocked(i, intent) : null;
        Intent[] startActivitiesProcess = startActivitiesProcess(i, intentArr, activityInfoArr, findActivityByToken);
        if (taskRecord == null) {
            realStartActivitiesLocked(null, startActivitiesProcess, strArr, bundle);
        } else {
            ActivityRecord activityRecord = topActivityInTask(taskRecord);
            if (activityRecord != null) {
                realStartActivitiesLocked(activityRecord.token, startActivitiesProcess, strArr, bundle);
            }
        }
        return 0;
    }

    private Intent[] startActivitiesProcess(int i, Intent[] intentArr, ActivityInfo[] activityInfoArr, ActivityRecord activityRecord) {
        Intent[] intentArr2 = new Intent[intentArr.length];
        for (int i2 = 0; i2 < intentArr.length; i2++) {
            intentArr2[i2] = startActivityProcess(i, activityRecord, intentArr[i2], activityInfoArr[i2]);
        }
        return intentArr2;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0158, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x007f, code lost:
        if (containFlags(r1, 536870912) != false) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0086, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a7, code lost:
        r10 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00a9, code lost:
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00ac, code lost:
        if (r7 != com.lody.virtual.server.p008am.ActivityStack.ClearTarget.NOTHING) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b2, code lost:
        if (containFlags(r1, 131072) == false) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00b4, code lost:
        r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.SPEC_ACTIVITY;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00b6, code lost:
        if (r5 != null) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ba, code lost:
        if (r6 != com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.CURRENT) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00bc, code lost:
        r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.AFFINITY;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00be, code lost:
        r12 = com.lody.virtual.helper.utils.ComponentUtils.getTaskAffinity(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ca, code lost:
        switch(r6) {
            case com.lody.virtual.server.am.ActivityStack.ReuseTarget.AFFINITY :com.lody.virtual.server.am.ActivityStack$ReuseTarget: goto L_0x00d5;
            case com.lody.virtual.server.am.ActivityStack.ReuseTarget.DOCUMENT :com.lody.virtual.server.am.ActivityStack$ReuseTarget: goto L_0x00d0;
            case com.lody.virtual.server.am.ActivityStack.ReuseTarget.CURRENT :com.lody.virtual.server.am.ActivityStack$ReuseTarget: goto L_0x00ce;
            default: goto L_0x00cd;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ce, code lost:
        r3 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00d0, code lost:
        r3 = findTaskByIntentLocked(r17, r18);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00d5, code lost:
        r3 = findTaskByAffinityLocked(r0, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00d9, code lost:
        if (r3 != null) goto L_0x00e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00db, code lost:
        startActivityInNewTaskLocked(r0, r1, r4, r21);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00e2, code lost:
        r12 = r21;
        r8.mAM.moveTaskToFront(r3.taskId, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00eb, code lost:
        if (r11 != false) goto L_0x00f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00ed, code lost:
        if (r9 != false) goto L_0x00f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00f5, code lost:
        if (com.lody.virtual.helper.utils.ComponentUtils.isSameIntent(r1, r3.taskRoot) == false) goto L_0x00f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00f7, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00f9, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00fc, code lost:
        if (r7.deliverIntent != false) goto L_0x0104;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00fe, code lost:
        if (r10 == false) goto L_0x0101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0101, code lost:
        r6 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0102, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0104, code lost:
        r6 = markTaskByClearTarget(r3, r7, r18.getComponent());
        r7 = topActivityInTask(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0110, code lost:
        if (r9 == false) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0112, code lost:
        if (r10 != false) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0114, code lost:
        if (r7 == null) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0116, code lost:
        if (r6 == false) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0118, code lost:
        r9 = true;
        r7.marked = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x011c, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x011d, code lost:
        if (r7 == null) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0121, code lost:
        if (r7.marked != false) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x012d, code lost:
        if (r7.component.equals(r18.getComponent()) == false) goto L_0x0102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x012f, code lost:
        deliverNewIntentLocked(r2, r7, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0132, code lost:
        if (r6 == false) goto L_0x013f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x0134, code lost:
        r6 = r8.mHistory;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0136, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:?, code lost:
        scheduleFinishMarkedActivityLocked();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x013a, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x013f, code lost:
        if (r5 != false) goto L_0x0158;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0141, code lost:
        if (r9 != false) goto L_0x0158;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0143, code lost:
        r0 = startActivityProcess(r0, r2, r1, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0147, code lost:
        if (r0 == null) goto L_0x0158;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0149, code lost:
        startActivityFromSourceTask(r3, r0, r19, r22, r23, r21);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int startActivityLocked(int r17, android.content.Intent r18, android.content.pm.ActivityInfo r19, android.os.IBinder r20, android.os.Bundle r21, java.lang.String r22, int r23) {
        /*
            r16 = this;
            r8 = r16
            r0 = r17
            r1 = r18
            r4 = r19
            r16.optimizeTasksLocked()
            r2 = r20
            com.lody.virtual.server.am.ActivityRecord r2 = r8.findActivityByToken(r0, r2)
            r3 = 0
            if (r2 == 0) goto L_0x0017
            com.lody.virtual.server.am.TaskRecord r5 = r2.task
            goto L_0x0018
        L_0x0017:
            r5 = r3
        L_0x0018:
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.CURRENT
            com.lody.virtual.server.am.ActivityStack$ClearTarget r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.NOTHING
            r9 = 67108864(0x4000000, float:1.5046328E-36)
            boolean r9 = containFlags(r1, r9)
            r10 = 32768(0x8000, float:4.5918E-41)
            boolean r11 = containFlags(r1, r10)
            android.content.ComponentName r12 = r18.getComponent()
            if (r12 != 0) goto L_0x003b
            android.content.ComponentName r12 = new android.content.ComponentName
            java.lang.String r13 = r4.packageName
            java.lang.String r14 = r4.name
            r12.<init>(r13, r14)
            r1.setComponent(r12)
        L_0x003b:
            r12 = 268435456(0x10000000, float:2.5243549E-29)
            if (r2 == 0) goto L_0x0047
            int r13 = r2.launchMode
            r14 = 3
            if (r13 != r14) goto L_0x0047
            r1.addFlags(r12)
        L_0x0047:
            r13 = 131072(0x20000, float:1.83671E-40)
            if (r9 == 0) goto L_0x0050
            removeFlags(r1, r13)
            com.lody.virtual.server.am.ActivityStack$ClearTarget r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.TOP
        L_0x0050:
            if (r11 == 0) goto L_0x005e
            boolean r14 = containFlags(r1, r12)
            if (r14 == 0) goto L_0x005b
            com.lody.virtual.server.am.ActivityStack$ClearTarget r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.TASK
            goto L_0x005e
        L_0x005b:
            removeFlags(r1, r10)
        L_0x005e:
            int r10 = android.os.Build.VERSION.SDK_INT
            r14 = 21
            if (r10 < r14) goto L_0x0071
            int r10 = r4.documentLaunchMode
            switch(r10) {
                case 1: goto L_0x006d;
                case 2: goto L_0x006a;
                default: goto L_0x0069;
            }
        L_0x0069:
            goto L_0x0071
        L_0x006a:
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.MULTIPLE
            goto L_0x0071
        L_0x006d:
            com.lody.virtual.server.am.ActivityStack$ClearTarget r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.TASK
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.DOCUMENT
        L_0x0071:
            int r10 = r4.launchMode
            r14 = 134217728(0x8000000, float:3.85186E-34)
            r15 = 0
            switch(r10) {
                case 1: goto L_0x0096;
                case 2: goto L_0x0088;
                case 3: goto L_0x0082;
                default: goto L_0x0079;
            }
        L_0x0079:
            r10 = 536870912(0x20000000, float:1.0842022E-19)
            boolean r10 = containFlags(r1, r10)
            if (r10 == 0) goto L_0x00a9
            goto L_0x00a7
        L_0x0082:
            com.lody.virtual.server.am.ActivityStack$ClearTarget r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.TOP
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.AFFINITY
        L_0x0086:
            r9 = 0
            goto L_0x00a9
        L_0x0088:
            com.lody.virtual.server.am.ActivityStack$ClearTarget r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.TOP
            boolean r6 = containFlags(r1, r14)
            if (r6 == 0) goto L_0x0093
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.MULTIPLE
            goto L_0x0086
        L_0x0093:
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.AFFINITY
            goto L_0x0086
        L_0x0096:
            boolean r10 = containFlags(r1, r12)
            if (r10 == 0) goto L_0x00a7
            boolean r6 = containFlags(r1, r14)
            if (r6 == 0) goto L_0x00a5
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.MULTIPLE
            goto L_0x00a7
        L_0x00a5:
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.AFFINITY
        L_0x00a7:
            r10 = 1
            goto L_0x00aa
        L_0x00a9:
            r10 = 0
        L_0x00aa:
            com.lody.virtual.server.am.ActivityStack$ClearTarget r12 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.NOTHING
            if (r7 != r12) goto L_0x00b6
            boolean r12 = containFlags(r1, r13)
            if (r12 == 0) goto L_0x00b6
            com.lody.virtual.server.am.ActivityStack$ClearTarget r7 = com.lody.virtual.server.p008am.ActivityStack.ClearTarget.SPEC_ACTIVITY
        L_0x00b6:
            if (r5 != 0) goto L_0x00be
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r12 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.CURRENT
            if (r6 != r12) goto L_0x00be
            com.lody.virtual.server.am.ActivityStack$ReuseTarget r6 = com.lody.virtual.server.p008am.ActivityStack.ReuseTarget.AFFINITY
        L_0x00be:
            java.lang.String r12 = com.lody.virtual.helper.utils.ComponentUtils.getTaskAffinity(r19)
            int[] r13 = com.lody.virtual.server.p008am.ActivityStack.C11072.$SwitchMap$com$lody$virtual$server$am$ActivityStack$ReuseTarget
            int r6 = r6.ordinal()
            r6 = r13[r6]
            switch(r6) {
                case 1: goto L_0x00d5;
                case 2: goto L_0x00d0;
                case 3: goto L_0x00ce;
                default: goto L_0x00cd;
            }
        L_0x00cd:
            goto L_0x00d9
        L_0x00ce:
            r3 = r5
            goto L_0x00d9
        L_0x00d0:
            com.lody.virtual.server.am.TaskRecord r3 = r16.findTaskByIntentLocked(r17, r18)
            goto L_0x00d9
        L_0x00d5:
            com.lody.virtual.server.am.TaskRecord r3 = r8.findTaskByAffinityLocked(r0, r12)
        L_0x00d9:
            if (r3 != 0) goto L_0x00e2
            r12 = r21
            r8.startActivityInNewTaskLocked(r0, r1, r4, r12)
            goto L_0x0158
        L_0x00e2:
            r12 = r21
            android.app.ActivityManager r5 = r8.mAM
            int r6 = r3.taskId
            r5.moveTaskToFront(r6, r15)
            if (r11 != 0) goto L_0x00f9
            if (r9 != 0) goto L_0x00f9
            android.content.Intent r5 = r3.taskRoot
            boolean r5 = com.lody.virtual.helper.utils.ComponentUtils.isSameIntent(r1, r5)
            if (r5 == 0) goto L_0x00f9
            r5 = 1
            goto L_0x00fa
        L_0x00f9:
            r5 = 0
        L_0x00fa:
            boolean r6 = r7.deliverIntent
            if (r6 != 0) goto L_0x0104
            if (r10 == 0) goto L_0x0101
            goto L_0x0104
        L_0x0101:
            r6 = 0
        L_0x0102:
            r9 = 0
            goto L_0x0132
        L_0x0104:
            android.content.ComponentName r6 = r18.getComponent()
            boolean r6 = r8.markTaskByClearTarget(r3, r7, r6)
            com.lody.virtual.server.am.ActivityRecord r7 = topActivityInTask(r3)
            if (r9 == 0) goto L_0x011c
            if (r10 != 0) goto L_0x011c
            if (r7 == 0) goto L_0x011c
            if (r6 == 0) goto L_0x011c
            r9 = 1
            r7.marked = r9
            goto L_0x011d
        L_0x011c:
            r9 = 1
        L_0x011d:
            if (r7 == 0) goto L_0x0102
            boolean r10 = r7.marked
            if (r10 != 0) goto L_0x0102
            android.content.ComponentName r10 = r7.component
            android.content.ComponentName r11 = r18.getComponent()
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x0102
            r8.deliverNewIntentLocked(r2, r7, r1)
        L_0x0132:
            if (r6 == 0) goto L_0x013f
            android.util.SparseArray<com.lody.virtual.server.am.TaskRecord> r6 = r8.mHistory
            monitor-enter(r6)
            r16.scheduleFinishMarkedActivityLocked()     // Catch:{ all -> 0x013c }
            monitor-exit(r6)     // Catch:{ all -> 0x013c }
            goto L_0x013f
        L_0x013c:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x013c }
            throw r0
        L_0x013f:
            if (r5 != 0) goto L_0x0158
            if (r9 != 0) goto L_0x0158
            android.content.Intent r0 = r8.startActivityProcess(r0, r2, r1, r4)
            if (r0 == 0) goto L_0x0158
            r1 = r16
            r2 = r3
            r3 = r0
            r4 = r19
            r5 = r22
            r6 = r23
            r7 = r21
            r1.startActivityFromSourceTask(r2, r3, r4, r5, r6, r7)
        L_0x0158:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p008am.ActivityStack.startActivityLocked(int, android.content.Intent, android.content.pm.ActivityInfo, android.os.IBinder, android.os.Bundle, java.lang.String, int):int");
    }

    private void startActivityInNewTaskLocked(int i, Intent intent, ActivityInfo activityInfo, Bundle bundle) {
        Intent startActivityProcess = startActivityProcess(i, null, intent, activityInfo);
        if (startActivityProcess != null) {
            startActivityProcess.addFlags(268435456);
            startActivityProcess.addFlags(134217728);
            startActivityProcess.addFlags(2097152);
            if (VERSION.SDK_INT < 21) {
                startActivityProcess.addFlags(524288);
            } else {
                startActivityProcess.addFlags(524288);
            }
            if (VERSION.SDK_INT >= 16) {
                VirtualCore.get().getContext().startActivity(startActivityProcess, bundle);
            } else {
                VirtualCore.get().getContext().startActivity(startActivityProcess);
            }
        }
    }

    private void scheduleFinishMarkedActivityLocked() {
        int size = this.mHistory.size();
        while (true) {
            int i = size - 1;
            if (size > 0) {
                for (final ActivityRecord activityRecord : ((TaskRecord) this.mHistory.valueAt(i)).activities) {
                    if (activityRecord.marked) {
                        VirtualRuntime.getUIHandler().post(new Runnable() {
                            public void run() {
                                try {
                                    activityRecord.process.client.finishActivity(activityRecord.token);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
                size = i;
            } else {
                return;
            }
        }
    }

    private void startActivityFromSourceTask(TaskRecord taskRecord, Intent intent, ActivityInfo activityInfo, String str, int i, Bundle bundle) {
        ActivityRecord activityRecord = taskRecord.activities.isEmpty() ? null : (ActivityRecord) taskRecord.activities.get(taskRecord.activities.size() - 1);
        if (activityRecord != null && startActivityProcess(taskRecord.userId, activityRecord, intent, activityInfo) != null) {
            realStartActivityLocked(activityRecord.token, intent, str, i, bundle);
        }
    }

    private void realStartActivitiesLocked(IBinder iBinder, Intent[] intentArr, String[] strArr, Bundle bundle) {
        Class<?>[] paramList = IActivityManager.startActivities.paramList();
        Object[] objArr = new Object[paramList.length];
        if (paramList[0] == IApplicationThread.TYPE) {
            objArr[0] = ActivityThread.getApplicationThread.call(VirtualCore.mainThread(), new Object[0]);
        }
        int protoIndexOf = ArrayUtils.protoIndexOf(paramList, String.class);
        int protoIndexOf2 = ArrayUtils.protoIndexOf(paramList, Intent[].class);
        int protoIndexOf3 = ArrayUtils.protoIndexOf(paramList, IBinder.class, 2);
        int protoIndexOf4 = ArrayUtils.protoIndexOf(paramList, Bundle.class);
        int i = protoIndexOf2 + 1;
        if (protoIndexOf != -1) {
            objArr[protoIndexOf] = VirtualCore.get().getHostPkg();
        }
        objArr[protoIndexOf2] = intentArr;
        objArr[protoIndexOf3] = iBinder;
        objArr[i] = strArr;
        objArr[protoIndexOf4] = bundle;
        ClassUtils.fixArgs(paramList, objArr);
        IActivityManager.startActivities.call(ActivityManagerNative.getDefault.call(new Object[0]), objArr);
    }

    private void realStartActivityLocked(IBinder iBinder, Intent intent, String str, int i, Bundle bundle) {
        Class<?>[] paramList = IActivityManager.startActivity.paramList();
        Object[] objArr = new Object[paramList.length];
        if (paramList[0] == IApplicationThread.TYPE) {
            objArr[0] = ActivityThread.getApplicationThread.call(VirtualCore.mainThread(), new Object[0]);
        }
        int protoIndexOf = ArrayUtils.protoIndexOf(paramList, Intent.class);
        int protoIndexOf2 = ArrayUtils.protoIndexOf(paramList, IBinder.class, 2);
        int protoIndexOf3 = ArrayUtils.protoIndexOf(paramList, Bundle.class);
        int i2 = protoIndexOf + 1;
        int i3 = protoIndexOf2 + 1;
        int i4 = protoIndexOf2 + 2;
        objArr[protoIndexOf] = intent;
        objArr[protoIndexOf2] = iBinder;
        objArr[i3] = str;
        objArr[i4] = Integer.valueOf(i);
        if (protoIndexOf3 != -1) {
            objArr[protoIndexOf3] = bundle;
        }
        objArr[i2] = intent.getType();
        if (VERSION.SDK_INT >= 18) {
            objArr[protoIndexOf - 1] = VirtualCore.get().getHostPkg();
        }
        ClassUtils.fixArgs(paramList, objArr);
        IActivityManager.startActivity.call(ActivityManagerNative.getDefault.call(new Object[0]), objArr);
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x008e A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String fetchStubActivity(int r9, android.content.pm.ActivityInfo r10) {
        /*
            r8 = this;
            r0 = 0
            mirror.RefStaticObject<int[]> r1 = mirror.com.android.internal.R_Hide.styleable.Window     // Catch:{ Throwable -> 0x0076 }
            java.lang.Object r1 = r1.get()     // Catch:{ Throwable -> 0x0076 }
            int[] r1 = (int[]) r1     // Catch:{ Throwable -> 0x0076 }
            mirror.RefStaticInt r2 = mirror.com.android.internal.R_Hide.styleable.Window_windowIsTranslucent     // Catch:{ Throwable -> 0x0076 }
            int r2 = r2.get()     // Catch:{ Throwable -> 0x0076 }
            mirror.RefStaticInt r3 = mirror.com.android.internal.R_Hide.styleable.Window_windowIsFloating     // Catch:{ Throwable -> 0x0076 }
            int r3 = r3.get()     // Catch:{ Throwable -> 0x0076 }
            mirror.RefStaticInt r4 = mirror.com.android.internal.R_Hide.styleable.Window_windowShowWallpaper     // Catch:{ Throwable -> 0x0076 }
            int r4 = r4.get()     // Catch:{ Throwable -> 0x0076 }
            com.lody.virtual.server.am.AttributeCache r5 = com.lody.virtual.server.p008am.AttributeCache.instance()     // Catch:{ Throwable -> 0x0076 }
            java.lang.String r6 = r10.packageName     // Catch:{ Throwable -> 0x0076 }
            int r7 = r10.theme     // Catch:{ Throwable -> 0x0076 }
            com.lody.virtual.server.am.AttributeCache$Entry r5 = r5.get(r6, r7, r1)     // Catch:{ Throwable -> 0x0076 }
            if (r5 == 0) goto L_0x0049
            android.content.res.TypedArray r6 = r5.array     // Catch:{ Throwable -> 0x0076 }
            if (r6 == 0) goto L_0x0049
            android.content.res.TypedArray r1 = r5.array     // Catch:{ Throwable -> 0x0076 }
            boolean r1 = r1.getBoolean(r4, r0)     // Catch:{ Throwable -> 0x0076 }
            android.content.res.TypedArray r4 = r5.array     // Catch:{ Throwable -> 0x0045 }
            boolean r2 = r4.getBoolean(r2, r0)     // Catch:{ Throwable -> 0x0045 }
            android.content.res.TypedArray r4 = r5.array     // Catch:{ Throwable -> 0x0042 }
            boolean r3 = r4.getBoolean(r3, r0)     // Catch:{ Throwable -> 0x0042 }
            r4 = r1
            r1 = r3
            goto L_0x007d
        L_0x0042:
            r3 = move-exception
            r4 = r1
            goto L_0x0079
        L_0x0045:
            r3 = move-exception
            r4 = r1
        L_0x0047:
            r2 = 0
            goto L_0x0079
        L_0x0049:
            com.lody.virtual.client.core.VirtualCore r5 = com.lody.virtual.client.core.VirtualCore.get()     // Catch:{ Throwable -> 0x0076 }
            java.lang.String r6 = r10.packageName     // Catch:{ Throwable -> 0x0076 }
            android.content.res.Resources r5 = r5.getResources(r6)     // Catch:{ Throwable -> 0x0076 }
            if (r5 == 0) goto L_0x0072
            android.content.res.Resources$Theme r5 = r5.newTheme()     // Catch:{ Throwable -> 0x0076 }
            int r6 = r10.theme     // Catch:{ Throwable -> 0x0076 }
            android.content.res.TypedArray r1 = r5.obtainStyledAttributes(r6, r1)     // Catch:{ Throwable -> 0x0076 }
            if (r1 == 0) goto L_0x0072
            boolean r4 = r1.getBoolean(r4, r0)     // Catch:{ Throwable -> 0x0076 }
            boolean r2 = r1.getBoolean(r2, r0)     // Catch:{ Throwable -> 0x0070 }
            boolean r1 = r1.getBoolean(r3, r0)     // Catch:{ Throwable -> 0x006e }
            goto L_0x007d
        L_0x006e:
            r3 = move-exception
            goto L_0x0079
        L_0x0070:
            r3 = move-exception
            goto L_0x0047
        L_0x0072:
            r1 = 0
            r2 = 0
            r4 = 0
            goto L_0x007d
        L_0x0076:
            r3 = move-exception
            r2 = 0
            r4 = 0
        L_0x0079:
            r3.printStackTrace()
            r1 = 0
        L_0x007d:
            int r10 = r10.flags
            r10 = r10 & 32
            r3 = 1
            if (r10 == 0) goto L_0x0086
            r10 = 1
            goto L_0x0087
        L_0x0086:
            r10 = 0
        L_0x0087:
            if (r10 == 0) goto L_0x008e
            java.lang.String r9 = com.lody.virtual.client.stub.VASettings.getStubExcludeFromRecentActivityName(r9)
            return r9
        L_0x008e:
            if (r1 != 0) goto L_0x0094
            if (r2 != 0) goto L_0x0094
            if (r4 == 0) goto L_0x0095
        L_0x0094:
            r0 = 1
        L_0x0095:
            if (r0 == 0) goto L_0x009c
            java.lang.String r9 = com.lody.virtual.client.stub.VASettings.getStubDialogName(r9)
            return r9
        L_0x009c:
            java.lang.String r9 = com.lody.virtual.client.stub.VASettings.getStubActivityName(r9)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p008am.ActivityStack.fetchStubActivity(int, android.content.pm.ActivityInfo):java.lang.String");
    }

    private Intent startActivityProcess(int i, ActivityRecord activityRecord, Intent intent, ActivityInfo activityInfo) {
        Intent intent2 = new Intent(intent);
        ProcessRecord startProcessIfNeedLocked = this.mService.startProcessIfNeedLocked(activityInfo.processName, i, activityInfo.packageName);
        ComponentName componentName = null;
        if (startProcessIfNeedLocked == null) {
            return null;
        }
        Intent intent3 = new Intent();
        intent3.setClassName(VirtualCore.get().getHostPkg(), fetchStubActivity(startProcessIfNeedLocked.vpid, activityInfo));
        ComponentName component = intent2.getComponent();
        if (component == null) {
            component = ComponentUtils.toComponentName(activityInfo);
        }
        intent3.setType(component.flattenToString());
        if (activityRecord != null) {
            componentName = activityRecord.component;
        }
        new StubActivityRecord(intent2, activityInfo, componentName, i).saveToIntent(intent3);
        return intent3;
    }

    /* access modifiers changed from: 0000 */
    public void onActivityCreated(ProcessRecord processRecord, ComponentName componentName, ComponentName componentName2, IBinder iBinder, Intent intent, String str, int i, int i2, int i3) {
        ProcessRecord processRecord2 = processRecord;
        int i4 = i;
        synchronized (this.mHistory) {
            optimizeTasksLocked();
            TaskRecord taskRecord = (TaskRecord) this.mHistory.get(i4);
            if (taskRecord == null) {
                taskRecord = new TaskRecord(i4, processRecord2.userId, str, intent);
                this.mHistory.put(i4, taskRecord);
            } else {
                String str2 = str;
            }
            TaskRecord taskRecord2 = taskRecord;
            ActivityRecord activityRecord = new ActivityRecord(taskRecord2, componentName, componentName2, iBinder, processRecord2.userId, processRecord, i2, i3, str);
            synchronized (taskRecord2.activities) {
                taskRecord2.activities.add(activityRecord);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void onActivityResumed(int i, IBinder iBinder) {
        synchronized (this.mHistory) {
            optimizeTasksLocked();
            ActivityRecord findActivityByToken = findActivityByToken(i, iBinder);
            if (findActivityByToken != null) {
                synchronized (findActivityByToken.task.activities) {
                    findActivityByToken.task.activities.remove(findActivityByToken);
                    findActivityByToken.task.activities.add(findActivityByToken);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public ActivityRecord onActivityDestroyed(int i, IBinder iBinder) {
        ActivityRecord findActivityByToken;
        synchronized (this.mHistory) {
            optimizeTasksLocked();
            findActivityByToken = findActivityByToken(i, iBinder);
            if (findActivityByToken != null) {
                synchronized (findActivityByToken.task.activities) {
                    findActivityByToken.task.activities.remove(findActivityByToken);
                }
            }
        }
        return findActivityByToken;
    }

    /* access modifiers changed from: 0000 */
    public void processDied(ProcessRecord processRecord) {
        synchronized (this.mHistory) {
            optimizeTasksLocked();
            int size = this.mHistory.size();
            while (true) {
                int i = size - 1;
                if (size > 0) {
                    TaskRecord taskRecord = (TaskRecord) this.mHistory.valueAt(i);
                    synchronized (taskRecord.activities) {
                        Iterator it = taskRecord.activities.iterator();
                        while (it.hasNext()) {
                            if (((ActivityRecord) it.next()).process.pid == processRecord.pid) {
                                it.remove();
                                if (taskRecord.activities.isEmpty()) {
                                    this.mHistory.remove(taskRecord.taskId);
                                }
                            }
                        }
                    }
                    size = i;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public String getPackageForToken(int i, IBinder iBinder) {
        synchronized (this.mHistory) {
            ActivityRecord findActivityByToken = findActivityByToken(i, iBinder);
            if (findActivityByToken == null) {
                return null;
            }
            String packageName = findActivityByToken.component.getPackageName();
            return packageName;
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0013, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.content.ComponentName getCallingActivity(int r2, android.os.IBinder r3) {
        /*
            r1 = this;
            android.util.SparseArray<com.lody.virtual.server.am.TaskRecord> r0 = r1.mHistory
            monitor-enter(r0)
            com.lody.virtual.server.am.ActivityRecord r2 = r1.findActivityByToken(r2, r3)     // Catch:{ all -> 0x0017 }
            if (r2 == 0) goto L_0x0014
            android.content.ComponentName r3 = r2.caller     // Catch:{ all -> 0x0017 }
            if (r3 == 0) goto L_0x0010
            android.content.ComponentName r2 = r2.caller     // Catch:{ all -> 0x0017 }
            goto L_0x0012
        L_0x0010:
            android.content.ComponentName r2 = r2.component     // Catch:{ all -> 0x0017 }
        L_0x0012:
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            return r2
        L_0x0014:
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            return r2
        L_0x0017:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p008am.ActivityStack.getCallingActivity(int, android.os.IBinder):android.content.ComponentName");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0017, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getCallingPackage(int r2, android.os.IBinder r3) {
        /*
            r1 = this;
            android.util.SparseArray<com.lody.virtual.server.am.TaskRecord> r0 = r1.mHistory
            monitor-enter(r0)
            com.lody.virtual.server.am.ActivityRecord r2 = r1.findActivityByToken(r2, r3)     // Catch:{ all -> 0x001c }
            if (r2 == 0) goto L_0x0018
            android.content.ComponentName r3 = r2.caller     // Catch:{ all -> 0x001c }
            if (r3 == 0) goto L_0x0014
            android.content.ComponentName r2 = r2.caller     // Catch:{ all -> 0x001c }
            java.lang.String r2 = r2.getPackageName()     // Catch:{ all -> 0x001c }
            goto L_0x0016
        L_0x0014:
            java.lang.String r2 = "android"
        L_0x0016:
            monitor-exit(r0)     // Catch:{ all -> 0x001c }
            return r2
        L_0x0018:
            java.lang.String r2 = "android"
            monitor-exit(r0)     // Catch:{ all -> 0x001c }
            return r2
        L_0x001c:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001c }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p008am.ActivityStack.getCallingPackage(int, android.os.IBinder):java.lang.String");
    }

    /* access modifiers changed from: 0000 */
    public AppTaskInfo getTaskInfo(int i) {
        synchronized (this.mHistory) {
            TaskRecord taskRecord = (TaskRecord) this.mHistory.get(i);
            if (taskRecord == null) {
                return null;
            }
            AppTaskInfo appTaskInfo = taskRecord.getAppTaskInfo();
            return appTaskInfo;
        }
    }

    /* access modifiers changed from: 0000 */
    public ComponentName getActivityClassForToken(int i, IBinder iBinder) {
        synchronized (this.mHistory) {
            ActivityRecord findActivityByToken = findActivityByToken(i, iBinder);
            if (findActivityByToken == null) {
                return null;
            }
            ComponentName componentName = findActivityByToken.component;
            return componentName;
        }
    }
}
