package com.lody.virtual.client.ipc;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobWorkItem;
import android.os.RemoteException;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;
import com.lody.virtual.server.IJobScheduler;
import com.lody.virtual.server.IJobScheduler.Stub;
import java.util.List;

public class VJobScheduler {
    private static final VJobScheduler sInstance = new VJobScheduler();
    private IJobScheduler mRemote;

    public static VJobScheduler get() {
        return sInstance;
    }

    public IJobScheduler getRemote() {
        if (this.mRemote == null || (!this.mRemote.asBinder().pingBinder() && !VirtualCore.get().isVAppProcess())) {
            synchronized (this) {
                this.mRemote = (IJobScheduler) LocalProxyUtils.genProxy(IJobScheduler.class, getRemoteInterface());
            }
        }
        return this.mRemote;
    }

    private Object getRemoteInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.JOB));
    }

    public int schedule(JobInfo jobInfo) {
        try {
            return getRemote().schedule(jobInfo);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }

    public List<JobInfo> getAllPendingJobs() {
        try {
            return getRemote().getAllPendingJobs();
        } catch (RemoteException e) {
            return (List) VirtualRuntime.crash(e);
        }
    }

    public void cancelAll() {
        try {
            getRemote().cancelAll();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void cancel(int i) {
        try {
            getRemote().cancel(i);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public JobInfo getPendingJob(int i) {
        try {
            return getRemote().getPendingJob(i);
        } catch (RemoteException e) {
            return (JobInfo) VirtualRuntime.crash(e);
        }
    }

    @TargetApi(26)
    public int enqueue(JobInfo jobInfo, Object obj) {
        if (obj == null) {
            return -1;
        }
        try {
            return getRemote().enqueue(jobInfo, (JobWorkItem) obj);
        } catch (RemoteException e) {
            return ((Integer) VirtualRuntime.crash(e)).intValue();
        }
    }
}
