package com.lody.virtual.server.p008am;

import android.content.Intent;
import com.lody.virtual.remote.AppTaskInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* renamed from: com.lody.virtual.server.am.TaskRecord */
class TaskRecord {
    public final List<ActivityRecord> activities = Collections.synchronizedList(new ArrayList());
    public String affinity;
    public int taskId;
    public Intent taskRoot;
    public int userId;

    TaskRecord(int i, int i2, String str, Intent intent) {
        this.taskId = i;
        this.userId = i2;
        this.affinity = str;
        this.taskRoot = intent;
    }

    /* access modifiers changed from: 0000 */
    public AppTaskInfo getAppTaskInfo() {
        int size = this.activities.size();
        if (size <= 0) {
            return null;
        }
        return new AppTaskInfo(this.taskId, this.taskRoot, this.taskRoot.getComponent(), ((ActivityRecord) this.activities.get(size - 1)).component);
    }

    public boolean isFinishing() {
        boolean z = true;
        for (ActivityRecord activityRecord : this.activities) {
            if (!activityRecord.marked) {
                z = false;
            }
        }
        return z;
    }
}
