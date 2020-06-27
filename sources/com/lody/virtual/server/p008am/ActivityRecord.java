package com.lody.virtual.server.p008am;

import android.content.ComponentName;
import android.os.IBinder;

/* renamed from: com.lody.virtual.server.am.ActivityRecord */
class ActivityRecord {
    public String affinity;
    public ComponentName caller;
    public ComponentName component;
    public int flags;
    public int launchMode;
    public boolean marked;
    public ProcessRecord process;
    public TaskRecord task;
    public IBinder token;
    public int userId;

    public ActivityRecord(TaskRecord taskRecord, ComponentName componentName, ComponentName componentName2, IBinder iBinder, int i, ProcessRecord processRecord, int i2, int i3, String str) {
        this.task = taskRecord;
        this.component = componentName;
        this.caller = componentName2;
        this.token = iBinder;
        this.userId = i;
        this.process = processRecord;
        this.launchMode = i2;
        this.flags = i3;
        this.affinity = str;
    }
}
