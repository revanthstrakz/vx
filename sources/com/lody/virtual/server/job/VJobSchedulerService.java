package com.lody.virtual.server.job;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VJobScheduler;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.utils.Singleton;
import com.lody.virtual.p007os.VBinder;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.server.IJobScheduler.Stub;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

@TargetApi(21)
public class VJobSchedulerService extends Stub {
    private static final int JOB_FILE_VERSION = 1;
    private static final String TAG = VJobScheduler.class.getSimpleName();
    private static final Singleton<VJobSchedulerService> gDefault = new Singleton<VJobSchedulerService>() {
        /* access modifiers changed from: protected */
        public VJobSchedulerService create() {
            return new VJobSchedulerService();
        }
    };
    private int mGlobalJobId;
    private final ComponentName mJobProxyComponent;
    private final Map<JobId, JobConfig> mJobStore;
    private final JobScheduler mScheduler;

    public static final class JobConfig implements Parcelable {
        public static final Creator<JobConfig> CREATOR = new Creator<JobConfig>() {
            public JobConfig createFromParcel(Parcel parcel) {
                return new JobConfig(parcel);
            }

            public JobConfig[] newArray(int i) {
                return new JobConfig[i];
            }
        };
        public PersistableBundle extras;
        public String serviceName;
        public int virtualJobId;

        public int describeContents() {
            return 0;
        }

        JobConfig(int i, String str, PersistableBundle persistableBundle) {
            this.virtualJobId = i;
            this.serviceName = str;
            this.extras = persistableBundle;
        }

        JobConfig(Parcel parcel) {
            this.virtualJobId = parcel.readInt();
            this.serviceName = parcel.readString();
            this.extras = (PersistableBundle) parcel.readParcelable(PersistableBundle.class.getClassLoader());
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.virtualJobId);
            parcel.writeString(this.serviceName);
            parcel.writeParcelable(this.extras, i);
        }
    }

    public static final class JobId implements Parcelable {
        public static final Creator<JobId> CREATOR = new Creator<JobId>() {
            public JobId createFromParcel(Parcel parcel) {
                return new JobId(parcel);
            }

            public JobId[] newArray(int i) {
                return new JobId[i];
            }
        };
        public int clientJobId;
        public String packageName;
        public int vuid;

        public int describeContents() {
            return 0;
        }

        JobId(int i, String str, int i2) {
            this.vuid = i;
            this.packageName = str;
            this.clientJobId = i2;
        }

        JobId(Parcel parcel) {
            this.vuid = parcel.readInt();
            this.packageName = parcel.readString();
            this.clientJobId = parcel.readInt();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            JobId jobId = (JobId) obj;
            if (!(this.vuid == jobId.vuid && this.clientJobId == jobId.clientJobId && TextUtils.equals(this.packageName, jobId.packageName))) {
                z = false;
            }
            return z;
        }

        public int hashCode() {
            return (((this.vuid * 31) + (this.packageName != null ? this.packageName.hashCode() : 0)) * 31) + this.clientJobId;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.vuid);
            parcel.writeString(this.packageName);
            parcel.writeInt(this.clientJobId);
        }
    }

    public int enqueue(JobInfo jobInfo, JobWorkItem jobWorkItem) throws RemoteException {
        return 0;
    }

    public JobInfo getPendingJob(int i) throws RemoteException {
        return null;
    }

    private VJobSchedulerService() {
        this.mJobStore = new HashMap();
        this.mScheduler = (JobScheduler) VirtualCore.get().getContext().getSystemService("jobscheduler");
        this.mJobProxyComponent = new ComponentName(VirtualCore.get().getHostPkg(), VASettings.STUB_JOB);
        readJobs();
    }

    public static VJobSchedulerService get() {
        return (VJobSchedulerService) gDefault.get();
    }

    public int schedule(JobInfo jobInfo) throws RemoteException {
        int callingUid = VBinder.getCallingUid();
        int id = jobInfo.getId();
        ComponentName service = jobInfo.getService();
        JobId jobId = new JobId(callingUid, service.getPackageName(), id);
        JobConfig jobConfig = (JobConfig) this.mJobStore.get(jobId);
        if (jobConfig == null) {
            int i = this.mGlobalJobId;
            this.mGlobalJobId = i + 1;
            jobConfig = new JobConfig(i, service.getClassName(), jobInfo.getExtras());
            this.mJobStore.put(jobId, jobConfig);
        } else {
            jobConfig.serviceName = service.getClassName();
            jobConfig.extras = jobInfo.getExtras();
        }
        saveJobs();
        mirror.android.app.job.JobInfo.jobId.set(jobInfo, jobConfig.virtualJobId);
        mirror.android.app.job.JobInfo.service.set(jobInfo, this.mJobProxyComponent);
        return this.mScheduler.schedule(jobInfo);
    }

    private void saveJobs() {
        File jobConfigFile = VEnvironment.getJobConfigFile();
        Parcel obtain = Parcel.obtain();
        try {
            obtain.writeInt(1);
            obtain.writeInt(this.mJobStore.size());
            for (Entry entry : this.mJobStore.entrySet()) {
                ((JobId) entry.getKey()).writeToParcel(obtain, 0);
                ((JobConfig) entry.getValue()).writeToParcel(obtain, 0);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(jobConfigFile);
            fileOutputStream.write(obtain.marshall());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            obtain.recycle();
            throw th;
        }
        obtain.recycle();
    }

    private void readJobs() {
        File jobConfigFile = VEnvironment.getJobConfigFile();
        if (jobConfigFile.exists()) {
            Parcel obtain = Parcel.obtain();
            try {
                FileInputStream fileInputStream = new FileInputStream(jobConfigFile);
                byte[] bArr = new byte[((int) jobConfigFile.length())];
                int read = fileInputStream.read(bArr);
                fileInputStream.close();
                if (read == bArr.length) {
                    obtain.unmarshall(bArr, 0, bArr.length);
                    obtain.setDataPosition(0);
                    int readInt = obtain.readInt();
                    if (readInt == 1) {
                        if (!this.mJobStore.isEmpty()) {
                            this.mJobStore.clear();
                        }
                        int readInt2 = obtain.readInt();
                        for (int i = 0; i < readInt2; i++) {
                            JobId jobId = new JobId(obtain);
                            JobConfig jobConfig = new JobConfig(obtain);
                            this.mJobStore.put(jobId, jobConfig);
                            this.mGlobalJobId = Math.max(this.mGlobalJobId, jobConfig.virtualJobId);
                        }
                        obtain.recycle();
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bad version of job file: ");
                    sb.append(readInt);
                    throw new IOException(sb.toString());
                }
                throw new IOException("Unable to read job config.");
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                obtain.recycle();
                throw th;
            }
        }
    }

    public void cancel(int i) throws RemoteException {
        int callingUid = VBinder.getCallingUid();
        synchronized (this.mJobStore) {
            boolean z = false;
            Iterator it = this.mJobStore.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Entry entry = (Entry) it.next();
                JobId jobId = (JobId) entry.getKey();
                JobConfig jobConfig = (JobConfig) entry.getValue();
                if (jobId.vuid == callingUid && jobId.clientJobId == i) {
                    z = true;
                    this.mScheduler.cancel(jobConfig.virtualJobId);
                    it.remove();
                    break;
                }
            }
            if (z) {
                saveJobs();
            }
        }
    }

    public void cancelAll() throws RemoteException {
        int callingUid = VBinder.getCallingUid();
        synchronized (this.mJobStore) {
            boolean z = false;
            Iterator it = this.mJobStore.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Entry entry = (Entry) it.next();
                if (((JobId) entry.getKey()).vuid == callingUid) {
                    this.mScheduler.cancel(((JobConfig) entry.getValue()).virtualJobId);
                    z = true;
                    it.remove();
                    break;
                }
            }
            if (z) {
                saveJobs();
            }
        }
    }

    public List<JobInfo> getAllPendingJobs() throws RemoteException {
        int callingUid = VBinder.getCallingUid();
        List<JobInfo> allPendingJobs = this.mScheduler.getAllPendingJobs();
        synchronized (this.mJobStore) {
            ListIterator listIterator = allPendingJobs.listIterator();
            while (listIterator.hasNext()) {
                JobInfo jobInfo = (JobInfo) listIterator.next();
                if (!VASettings.STUB_JOB.equals(jobInfo.getService().getClassName())) {
                    listIterator.remove();
                } else {
                    Entry findJobByVirtualJobId = findJobByVirtualJobId(jobInfo.getId());
                    if (findJobByVirtualJobId == null) {
                        listIterator.remove();
                    } else {
                        JobId jobId = (JobId) findJobByVirtualJobId.getKey();
                        JobConfig jobConfig = (JobConfig) findJobByVirtualJobId.getValue();
                        if (jobId.vuid != callingUid) {
                            listIterator.remove();
                        } else {
                            mirror.android.app.job.JobInfo.jobId.set(jobInfo, jobId.clientJobId);
                            mirror.android.app.job.JobInfo.service.set(jobInfo, new ComponentName(jobId.packageName, jobConfig.serviceName));
                        }
                    }
                }
            }
        }
        return allPendingJobs;
    }

    public Entry<JobId, JobConfig> findJobByVirtualJobId(int i) {
        synchronized (this.mJobStore) {
            for (Entry<JobId, JobConfig> entry : this.mJobStore.entrySet()) {
                if (((JobConfig) entry.getValue()).virtualJobId == i) {
                    return entry;
                }
            }
            return null;
        }
    }
}
