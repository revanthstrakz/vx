package com.lody.virtual.client.hook.delegate;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.fixer.ActivityFixer;
import com.lody.virtual.client.fixer.ContextFixer;
import com.lody.virtual.client.interfaces.IInjector;
import com.lody.virtual.client.ipc.ActivityClientRecord;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.compat.BundleCompat;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.server.interfaces.IUiCallback;
import mirror.android.app.ActivityThread;

public final class AppInstrumentation extends InstrumentationDelegate implements IInjector {
    private static final String TAG = "AppInstrumentation";
    private static AppInstrumentation gDefault;

    private AppInstrumentation(Instrumentation instrumentation) {
        super(instrumentation);
    }

    public static AppInstrumentation getDefault() {
        if (gDefault == null) {
            synchronized (AppInstrumentation.class) {
                if (gDefault == null) {
                    gDefault = create();
                }
            }
        }
        return gDefault;
    }

    private static AppInstrumentation create() {
        Instrumentation instrumentation = (Instrumentation) ActivityThread.mInstrumentation.get(VirtualCore.mainThread());
        if (instrumentation instanceof AppInstrumentation) {
            return (AppInstrumentation) instrumentation;
        }
        return new AppInstrumentation(instrumentation);
    }

    public void inject() throws Throwable {
        this.base = (Instrumentation) ActivityThread.mInstrumentation.get(VirtualCore.mainThread());
        ActivityThread.mInstrumentation.set(VirtualCore.mainThread(), this);
    }

    public boolean isEnvBad() {
        return !(ActivityThread.mInstrumentation.get(VirtualCore.mainThread()) instanceof AppInstrumentation);
    }

    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        if (bundle != null) {
            BundleCompat.clearParcelledData(bundle);
        }
        VirtualCore.get().getComponentDelegate().beforeActivityCreate(activity);
        ActivityClientRecord activityRecord = VActivityManager.get().getActivityRecord((IBinder) mirror.android.app.Activity.mToken.get(activity));
        if (activityRecord != null) {
            activityRecord.activity = activity;
        }
        ContextFixer.fixContext(activity);
        ActivityFixer.fixActivity(activity);
        ActivityInfo activityInfo = null;
        if (activityRecord != null) {
            activityInfo = activityRecord.info;
        }
        if (activityInfo != null) {
            if (activityInfo.theme != 0) {
                activity.setTheme(activityInfo.theme);
            }
            if (activity.getRequestedOrientation() == -1 && activityInfo.screenOrientation != -1) {
                activity.setRequestedOrientation(activityInfo.screenOrientation);
            }
        }
        try {
            super.callActivityOnCreate(activity, bundle);
            VirtualCore.get().getComponentDelegate().afterActivityCreate(activity);
        } catch (Throwable th) {
            VLog.m87e(TAG, "activity crashed when call onCreate, clearing", th);
            callUiCallback(activity.getIntent(), false);
            activity.finish();
            throw th;
        }
    }

    public Activity newActivity(Class<?> cls, Context context, IBinder iBinder, Application application, Intent intent, ActivityInfo activityInfo, CharSequence charSequence, Activity activity, String str, Object obj) throws InstantiationException, IllegalAccessException {
        try {
            return super.newActivity(cls, context, iBinder, application, intent, activityInfo, charSequence, activity, str, obj);
        } catch (Throwable th) {
            VLog.m87e(TAG, "activity crashed when call newActivity, clearing", th);
            callUiCallback(intent, false);
            throw th;
        }
    }

    public Activity newActivity(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            return super.newActivity(classLoader, str, intent);
        } catch (Throwable th) {
            VLog.m87e(TAG, "activity crashed when call newActivity, clearing", th);
            callUiCallback(intent, false);
            throw th;
        }
    }

    public void callActivityOnCreate(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        if (bundle != null) {
            BundleCompat.clearParcelledData(bundle);
        }
        super.callActivityOnCreate(activity, bundle, persistableBundle);
    }

    public void callActivityOnResume(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityResume(activity);
        VActivityManager.get().onActivityResumed(activity);
        super.callActivityOnResume(activity);
        VirtualCore.get().getComponentDelegate().afterActivityResume(activity);
        callUiCallback(activity.getIntent(), true);
    }

    public void callActivityOnDestroy(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityDestroy(activity);
        super.callActivityOnDestroy(activity);
        VirtualCore.get().getComponentDelegate().afterActivityDestroy(activity);
    }

    public void callActivityOnPause(Activity activity) {
        VirtualCore.get().getComponentDelegate().beforeActivityPause(activity);
        super.callActivityOnPause(activity);
        VirtualCore.get().getComponentDelegate().afterActivityPause(activity);
    }

    public void callApplicationOnCreate(Application application) {
        super.callApplicationOnCreate(application);
    }

    private void callUiCallback(Intent intent, boolean z) {
        IUiCallback uiCallback = VirtualCore.getUiCallback(intent);
        if (uiCallback == null) {
            return;
        }
        if (z) {
            try {
                uiCallback.onAppOpened(VClientImpl.get().getCurrentPackage(), VUserHandle.myUserId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            uiCallback.onOpenFailed(VClientImpl.get().getCurrentPackage(), VUserHandle.myUserId());
        }
    }
}
