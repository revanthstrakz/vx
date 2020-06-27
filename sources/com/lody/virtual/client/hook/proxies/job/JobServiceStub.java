package com.lody.virtual.client.hook.proxies.job;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.ipc.VJobScheduler;
import com.lody.virtual.helper.utils.ComponentUtils;
import java.lang.reflect.Method;
import mirror.android.app.job.IJobScheduler.Stub;
import mirror.android.app.job.JobWorkItem;

@TargetApi(21)
public class JobServiceStub extends BinderInvocationProxy {

    private class cancel extends MethodProxy {
        public String getMethodName() {
            return "cancel";
        }

        private cancel() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            VJobScheduler.get().cancel(objArr[0].intValue());
            return Integer.valueOf(0);
        }
    }

    private class cancelAll extends MethodProxy {
        public String getMethodName() {
            return "cancelAll";
        }

        private cancelAll() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            VJobScheduler.get().cancelAll();
            return Integer.valueOf(0);
        }
    }

    private class enqueue extends MethodProxy {
        public String getMethodName() {
            return "enqueue";
        }

        private enqueue() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Integer.valueOf(VJobScheduler.get().enqueue(objArr[0], JobServiceStub.this.redirect(objArr[1], MethodProxy.getAppPkg())));
        }
    }

    private class getAllPendingJobs extends MethodProxy {
        public String getMethodName() {
            return "getAllPendingJobs";
        }

        private getAllPendingJobs() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return VJobScheduler.get().getAllPendingJobs();
        }
    }

    private class getPendingJob extends MethodProxy {
        public String getMethodName() {
            return "getPendingJob";
        }

        private getPendingJob() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return VJobScheduler.get().getPendingJob(objArr[0].intValue());
        }
    }

    private class schedule extends MethodProxy {
        public String getMethodName() {
            return "schedule";
        }

        private schedule() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Integer.valueOf(VJobScheduler.get().schedule(objArr[0]));
        }
    }

    public JobServiceStub() {
        super(Stub.asInterface, "jobscheduler");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new schedule());
        addMethodProxy((MethodProxy) new getAllPendingJobs());
        addMethodProxy((MethodProxy) new cancelAll());
        addMethodProxy((MethodProxy) new cancel());
        if (VERSION.SDK_INT >= 24) {
            addMethodProxy((MethodProxy) new getPendingJob());
        }
        if (VERSION.SDK_INT >= 26) {
            addMethodProxy((MethodProxy) new enqueue());
        }
    }

    /* access modifiers changed from: private */
    public Object redirect(Object obj, String str) {
        if (obj == null) {
            return null;
        }
        Intent redirectIntentSender = ComponentUtils.redirectIntentSender(4, str, (Intent) JobWorkItem.getIntent.call(obj, new Object[0]), null);
        Object newInstance = JobWorkItem.ctor.newInstance(redirectIntentSender);
        JobWorkItem.mWorkId.set(newInstance, JobWorkItem.mWorkId.get(obj));
        JobWorkItem.mGrants.set(newInstance, JobWorkItem.mGrants.get(obj));
        JobWorkItem.mDeliveryCount.set(newInstance, JobWorkItem.mDeliveryCount.get(obj));
        return newInstance;
    }
}
