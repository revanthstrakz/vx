package com.lody.virtual.client.stub;

import android.annotation.TargetApi;
import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build.VERSION;
import com.lody.virtual.server.p009pm.PrivilegeAppOptimizer;
import java.util.concurrent.TimeUnit;

@TargetApi(21)
public class DaemonJobService extends JobService {
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public boolean onStartJob(JobParameters jobParameters) {
        PrivilegeAppOptimizer.notifyBootFinish();
        return true;
    }

    public static void scheduleJob(Context context) {
        if (VERSION.SDK_INT >= 21) {
            try {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
                if (jobScheduler != null) {
                    jobScheduler.schedule(new Builder(1, new ComponentName(context, DaemonJobService.class)).setRequiresCharging(false).setRequiredNetworkType(1).setPeriodic(TimeUnit.MINUTES.toMillis(15)).build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
