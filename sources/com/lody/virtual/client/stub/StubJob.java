package com.lody.virtual.client.stub;

import android.annotation.TargetApi;
import android.app.Service;
import android.app.job.IJobCallback;
import android.app.job.IJobService;
import android.app.job.IJobService.Stub;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import com.lody.virtual.client.core.InvocationStubManager;
import com.lody.virtual.client.hook.proxies.p005am.ActivityManagerStub;
import com.lody.virtual.helper.collection.SparseArray;

@TargetApi(21)
public class StubJob extends Service {
    /* access modifiers changed from: private */
    public static final String TAG = "StubJob";
    /* access modifiers changed from: private */
    public final SparseArray<JobSession> mJobSessions = new SparseArray<>();
    /* access modifiers changed from: private */
    public JobScheduler mScheduler;
    private final IJobService mService = new Stub() {
        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void startJob(android.app.job.JobParameters r9) throws android.os.RemoteException {
            /*
                r8 = this;
                int r0 = r9.getJobId()
                mirror.RefObject<android.os.IBinder> r1 = mirror.android.app.job.JobParameters.callback
                java.lang.Object r1 = r1.get(r9)
                android.os.IBinder r1 = (android.os.IBinder) r1
                android.app.job.IJobCallback r1 = android.app.job.IJobCallback.Stub.asInterface(r1)
                com.lody.virtual.server.job.VJobSchedulerService r2 = com.lody.virtual.server.job.VJobSchedulerService.get()
                java.util.Map$Entry r2 = r2.findJobByVirtualJobId(r0)
                if (r2 != 0) goto L_0x002a
                com.lody.virtual.client.stub.StubJob r9 = com.lody.virtual.client.stub.StubJob.this
                r9.emptyCallback(r1, r0)
                com.lody.virtual.client.stub.StubJob r9 = com.lody.virtual.client.stub.StubJob.this
                android.app.job.JobScheduler r9 = r9.mScheduler
                r9.cancel(r0)
                goto L_0x00b7
            L_0x002a:
                java.lang.Object r3 = r2.getKey()
                com.lody.virtual.server.job.VJobSchedulerService$JobId r3 = (com.lody.virtual.server.job.VJobSchedulerService.JobId) r3
                java.lang.Object r2 = r2.getValue()
                com.lody.virtual.server.job.VJobSchedulerService$JobConfig r2 = (com.lody.virtual.server.job.VJobSchedulerService.JobConfig) r2
                com.lody.virtual.client.stub.StubJob r4 = com.lody.virtual.client.stub.StubJob.this
                com.lody.virtual.helper.collection.SparseArray r4 = r4.mJobSessions
                monitor-enter(r4)
                com.lody.virtual.client.stub.StubJob r5 = com.lody.virtual.client.stub.StubJob.this     // Catch:{ all -> 0x00b8 }
                com.lody.virtual.helper.collection.SparseArray r5 = r5.mJobSessions     // Catch:{ all -> 0x00b8 }
                java.lang.Object r5 = r5.get(r0)     // Catch:{ all -> 0x00b8 }
                com.lody.virtual.client.stub.StubJob$JobSession r5 = (com.lody.virtual.client.stub.StubJob.JobSession) r5     // Catch:{ all -> 0x00b8 }
                if (r5 == 0) goto L_0x0051
                com.lody.virtual.client.stub.StubJob r9 = com.lody.virtual.client.stub.StubJob.this     // Catch:{ all -> 0x00b8 }
                r9.emptyCallback(r1, r0)     // Catch:{ all -> 0x00b8 }
                goto L_0x00b6
            L_0x0051:
                com.lody.virtual.client.stub.StubJob$JobSession r5 = new com.lody.virtual.client.stub.StubJob$JobSession     // Catch:{ all -> 0x00b8 }
                com.lody.virtual.client.stub.StubJob r6 = com.lody.virtual.client.stub.StubJob.this     // Catch:{ all -> 0x00b8 }
                r5.<init>(r0, r1, r9)     // Catch:{ all -> 0x00b8 }
                mirror.RefObject<android.os.IBinder> r6 = mirror.android.app.job.JobParameters.callback     // Catch:{ all -> 0x00b8 }
                android.os.IBinder r7 = r5.asBinder()     // Catch:{ all -> 0x00b8 }
                r6.set(r9, r7)     // Catch:{ all -> 0x00b8 }
                mirror.RefInt r6 = mirror.android.app.job.JobParameters.jobId     // Catch:{ all -> 0x00b8 }
                int r7 = r3.clientJobId     // Catch:{ all -> 0x00b8 }
                r6.set(r9, r7)     // Catch:{ all -> 0x00b8 }
                android.content.Intent r9 = new android.content.Intent     // Catch:{ all -> 0x00b8 }
                r9.<init>()     // Catch:{ all -> 0x00b8 }
                android.content.ComponentName r6 = new android.content.ComponentName     // Catch:{ all -> 0x00b8 }
                java.lang.String r7 = r3.packageName     // Catch:{ all -> 0x00b8 }
                java.lang.String r2 = r2.serviceName     // Catch:{ all -> 0x00b8 }
                r6.<init>(r7, r2)     // Catch:{ all -> 0x00b8 }
                r9.setComponent(r6)     // Catch:{ all -> 0x00b8 }
                java.lang.String r2 = "_VA_|_user_id_"
                int r3 = r3.vuid     // Catch:{ all -> 0x00b8 }
                int r3 = com.lody.virtual.p007os.VUserHandle.getUserId(r3)     // Catch:{ all -> 0x00b8 }
                r9.putExtra(r2, r3)     // Catch:{ all -> 0x00b8 }
                r2 = 0
                com.lody.virtual.client.stub.StubJob r3 = com.lody.virtual.client.stub.StubJob.this     // Catch:{ Throwable -> 0x008c }
                boolean r9 = r3.bindService(r9, r5, r2)     // Catch:{ Throwable -> 0x008c }
                goto L_0x0095
            L_0x008c:
                r9 = move-exception
                java.lang.String r3 = com.lody.virtual.client.stub.StubJob.TAG     // Catch:{ all -> 0x00b8 }
                com.lody.virtual.helper.utils.VLog.m88e(r3, r9)     // Catch:{ all -> 0x00b8 }
                r9 = 0
            L_0x0095:
                if (r9 == 0) goto L_0x00a1
                com.lody.virtual.client.stub.StubJob r9 = com.lody.virtual.client.stub.StubJob.this     // Catch:{ all -> 0x00b8 }
                com.lody.virtual.helper.collection.SparseArray r9 = r9.mJobSessions     // Catch:{ all -> 0x00b8 }
                r9.put(r0, r5)     // Catch:{ all -> 0x00b8 }
                goto L_0x00b6
            L_0x00a1:
                com.lody.virtual.client.stub.StubJob r9 = com.lody.virtual.client.stub.StubJob.this     // Catch:{ all -> 0x00b8 }
                r9.emptyCallback(r1, r0)     // Catch:{ all -> 0x00b8 }
                com.lody.virtual.client.stub.StubJob r9 = com.lody.virtual.client.stub.StubJob.this     // Catch:{ all -> 0x00b8 }
                android.app.job.JobScheduler r9 = r9.mScheduler     // Catch:{ all -> 0x00b8 }
                r9.cancel(r0)     // Catch:{ all -> 0x00b8 }
                com.lody.virtual.server.job.VJobSchedulerService r9 = com.lody.virtual.server.job.VJobSchedulerService.get()     // Catch:{ all -> 0x00b8 }
                r9.cancel(r0)     // Catch:{ all -> 0x00b8 }
            L_0x00b6:
                monitor-exit(r4)     // Catch:{ all -> 0x00b8 }
            L_0x00b7:
                return
            L_0x00b8:
                r9 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x00b8 }
                throw r9
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.client.stub.StubJob.C10701.startJob(android.app.job.JobParameters):void");
        }

        public void stopJob(JobParameters jobParameters) throws RemoteException {
            int jobId = jobParameters.getJobId();
            synchronized (StubJob.this.mJobSessions) {
                JobSession jobSession = (JobSession) StubJob.this.mJobSessions.get(jobId);
                if (jobSession != null) {
                    jobSession.stopSession();
                }
            }
        }
    };

    private final class JobSession extends IJobCallback.Stub implements ServiceConnection {
        private IJobCallback clientCallback;
        private IJobService clientJobService;
        private int jobId;
        private JobParameters jobParams;

        public void onServiceDisconnected(ComponentName componentName) {
        }

        JobSession(int i, IJobCallback iJobCallback, JobParameters jobParameters) {
            this.jobId = i;
            this.clientCallback = iJobCallback;
            this.jobParams = jobParameters;
        }

        public void acknowledgeStartMessage(int i, boolean z) throws RemoteException {
            this.clientCallback.acknowledgeStartMessage(i, z);
        }

        public void acknowledgeStopMessage(int i, boolean z) throws RemoteException {
            this.clientCallback.acknowledgeStopMessage(i, z);
        }

        public void jobFinished(int i, boolean z) throws RemoteException {
            this.clientCallback.jobFinished(i, z);
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            this.clientJobService = Stub.asInterface(iBinder);
            if (this.clientJobService == null) {
                StubJob.this.emptyCallback(this.clientCallback, this.jobId);
                stopSession();
                return;
            }
            try {
                this.clientJobService.startJob(this.jobParams);
            } catch (RemoteException e) {
                forceFinishJob();
                e.printStackTrace();
            }
        }

        /* access modifiers changed from: 0000 */
        public void forceFinishJob() {
            try {
                this.clientCallback.jobFinished(this.jobId, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Throwable th) {
                stopSession();
                throw th;
            }
            stopSession();
        }

        /* access modifiers changed from: 0000 */
        public void stopSession() {
            if (this.clientJobService != null) {
                try {
                    this.clientJobService.stopJob(this.jobParams);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            StubJob.this.mJobSessions.remove(this.jobId);
            StubJob.this.unbindService(this);
        }
    }

    /* access modifiers changed from: private */
    public void emptyCallback(IJobCallback iJobCallback, int i) {
        try {
            iJobCallback.acknowledgeStartMessage(i, false);
            iJobCallback.jobFinished(i, false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onCreate() {
        super.onCreate();
        InvocationStubManager.getInstance().checkEnv(ActivityManagerStub.class);
        this.mScheduler = (JobScheduler) getSystemService("jobscheduler");
    }

    public IBinder onBind(Intent intent) {
        return this.mService.asBinder();
    }
}
