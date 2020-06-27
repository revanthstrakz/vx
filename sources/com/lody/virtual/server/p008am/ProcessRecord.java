package com.lody.virtual.server.p008am;

import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.IInterface;
import com.lody.virtual.client.IVClient;
import com.lody.virtual.p007os.VUserHandle;
import java.util.HashSet;
import java.util.Set;

/* renamed from: com.lody.virtual.server.am.ProcessRecord */
final class ProcessRecord extends Binder implements Comparable<ProcessRecord> {
    IInterface appThread;
    public IVClient client;
    boolean doneExecuting;
    public final ApplicationInfo info;
    final ConditionVariable lock = new ConditionVariable();
    public int pid;
    final Set<String> pkgList = new HashSet();
    int priority;
    public final String processName;
    public int userId;
    public int vpid;
    public int vuid;

    public ProcessRecord(ApplicationInfo applicationInfo, String str, int i, int i2) {
        this.info = applicationInfo;
        this.vuid = i;
        this.vpid = i2;
        this.userId = VUserHandle.getUserId(i);
        this.processName = str;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProcessRecord processRecord = (ProcessRecord) obj;
        if (this.processName != null) {
            z = this.processName.equals(processRecord.processName);
        } else if (processRecord.processName != null) {
            z = false;
        }
        return z;
    }

    public int compareTo(ProcessRecord processRecord) {
        return this.priority - processRecord.priority;
    }
}
