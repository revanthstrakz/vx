package com.lody.virtual.client.hook.proxies.p005am;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.interfaces.IInjector;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.utils.ComponentUtils;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.remote.StubActivityRecord;
import mirror.android.app.ActivityManagerNative;
import mirror.android.app.ActivityThread;
import mirror.android.app.ActivityThread.ActivityClientRecord;
import mirror.android.app.ActivityThread.C1312H;
import mirror.android.app.IActivityManager;

/* renamed from: com.lody.virtual.client.hook.proxies.am.HCallbackStub */
public class HCallbackStub implements Callback, IInjector {
    private static final int CREATE_SERVICE = C1312H.CREATE_SERVICE.get();
    private static int LAUNCH_ACTIVITY = C1312H.LAUNCH_ACTIVITY.get();
    private static final int SCHEDULE_CRASH = (C1312H.SCHEDULE_CRASH != null ? C1312H.SCHEDULE_CRASH.get() : -1);
    private static final String TAG = HCallbackStub.class.getSimpleName();
    private static final HCallbackStub sCallback = new HCallbackStub();
    private boolean mCalling = false;
    private Callback otherCallback;

    static {
        if (VERSION.SDK_INT < 28) {
        }
    }

    private HCallbackStub() {
    }

    public static HCallbackStub getDefault() {
        return sCallback;
    }

    private static Handler getH() {
        return (Handler) ActivityThread.f212mH.get(VirtualCore.mainThread());
    }

    private static Callback getHCallback() {
        try {
            return (Callback) mirror.android.p017os.Handler.mCallback.get(getH());
        } catch (Throwable th) {
            th.printStackTrace();
            return null;
        }
    }

    public boolean handleMessage(Message message) {
        if (!this.mCalling) {
            this.mCalling = true;
            try {
                if (LAUNCH_ACTIVITY == message.what) {
                    if (!handleLaunchActivity(message)) {
                        return true;
                    }
                } else if (CREATE_SERVICE == message.what) {
                    if (!VClientImpl.get().isBound()) {
                        ServiceInfo serviceInfo = (ServiceInfo) Reflect.m80on(message.obj).get("info");
                        VClientImpl.get().bindApplication(serviceInfo.packageName, serviceInfo.processName);
                    }
                } else if (SCHEDULE_CRASH == message.what) {
                    this.mCalling = false;
                    return true;
                }
                if (this.otherCallback != null) {
                    boolean handleMessage = this.otherCallback.handleMessage(message);
                    this.mCalling = false;
                    this.mCalling = false;
                    return handleMessage;
                }
                this.mCalling = false;
                this.mCalling = false;
            } finally {
                this.mCalling = false;
            }
        }
        return false;
    }

    private boolean handleLaunchActivity(Message message) {
        Object obj = message.obj;
        StubActivityRecord stubActivityRecord = new StubActivityRecord((Intent) ActivityClientRecord.intent.get(obj));
        if (stubActivityRecord.intent == null) {
            return true;
        }
        Intent intent = stubActivityRecord.intent;
        ComponentName componentName = stubActivityRecord.caller;
        IBinder iBinder = (IBinder) ActivityClientRecord.token.get(obj);
        ActivityInfo activityInfo = stubActivityRecord.info;
        if (VClientImpl.get().getToken() == null) {
            if (VirtualCore.get().getInstalledAppInfo(activityInfo.packageName, 0) == null) {
                return true;
            }
            VActivityManager.get().processRestarted(activityInfo.packageName, activityInfo.processName, stubActivityRecord.userId);
            getH().sendMessageAtFrontOfQueue(Message.obtain(message));
            return false;
        } else if (!VClientImpl.get().isBound()) {
            VClientImpl.get().bindApplicationForActivity(activityInfo.packageName, activityInfo.processName, intent);
            getH().sendMessageAtFrontOfQueue(Message.obtain(message));
            return false;
        } else {
            ActivityInfo activityInfo2 = activityInfo;
            Intent intent2 = intent;
            VActivityManager.get().onActivityCreate(ComponentUtils.toComponentName(activityInfo), componentName, iBinder, activityInfo2, intent2, ComponentUtils.getTaskAffinity(activityInfo), ((Integer) IActivityManager.getTaskForActivity.call(ActivityManagerNative.getDefault.call(new Object[0]), iBinder, Boolean.valueOf(false))).intValue(), activityInfo.launchMode, activityInfo.flags);
            intent.setExtrasClassLoader(VClientImpl.get().getClassLoader(activityInfo.applicationInfo));
            ActivityClientRecord.intent.set(obj, intent);
            ActivityClientRecord.activityInfo.set(obj, activityInfo);
            return true;
        }
    }

    public void inject() throws Throwable {
        this.otherCallback = getHCallback();
        mirror.android.p017os.Handler.mCallback.set(getH(), this);
    }

    public boolean isEnvBad() {
        Callback hCallback = getHCallback();
        boolean z = hCallback != this;
        if (hCallback != null && z) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("HCallback has bad, other callback = ");
            sb.append(hCallback);
            VLog.m86d(str, sb.toString(), new Object[0]);
        }
        return z;
    }
}
