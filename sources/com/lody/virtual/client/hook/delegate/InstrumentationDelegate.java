package com.lody.virtual.client.hook.delegate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.app.Instrumentation.ActivityResult;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class InstrumentationDelegate extends Instrumentation {
    protected Instrumentation base;

    public InstrumentationDelegate(Instrumentation instrumentation) {
        this.base = instrumentation;
    }

    public static Application newApplication(Class<?> cls, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return Instrumentation.newApplication(cls, context);
    }

    public void onCreate(Bundle bundle) {
        this.base.onCreate(bundle);
    }

    public void start() {
        this.base.start();
    }

    public void onStart() {
        this.base.onStart();
    }

    public boolean onException(Object obj, Throwable th) {
        return this.base.onException(obj, th);
    }

    public void sendStatus(int i, Bundle bundle) {
        this.base.sendStatus(i, bundle);
    }

    public void finish(int i, Bundle bundle) {
        this.base.finish(i, bundle);
    }

    public void setAutomaticPerformanceSnapshots() {
        this.base.setAutomaticPerformanceSnapshots();
    }

    public void startPerformanceSnapshot() {
        this.base.startPerformanceSnapshot();
    }

    public void endPerformanceSnapshot() {
        this.base.endPerformanceSnapshot();
    }

    public void onDestroy() {
        this.base.onDestroy();
    }

    public Context getContext() {
        return this.base.getContext();
    }

    public ComponentName getComponentName() {
        return this.base.getComponentName();
    }

    public Context getTargetContext() {
        return this.base.getTargetContext();
    }

    public boolean isProfiling() {
        return this.base.isProfiling();
    }

    public void startProfiling() {
        this.base.startProfiling();
    }

    public void stopProfiling() {
        this.base.stopProfiling();
    }

    public void setInTouchMode(boolean z) {
        this.base.setInTouchMode(z);
    }

    public void waitForIdle(Runnable runnable) {
        this.base.waitForIdle(runnable);
    }

    public void waitForIdleSync() {
        this.base.waitForIdleSync();
    }

    public void runOnMainSync(Runnable runnable) {
        this.base.runOnMainSync(runnable);
    }

    public Activity startActivitySync(Intent intent) {
        return this.base.startActivitySync(intent);
    }

    public void addMonitor(ActivityMonitor activityMonitor) {
        this.base.addMonitor(activityMonitor);
    }

    public ActivityMonitor addMonitor(IntentFilter intentFilter, ActivityResult activityResult, boolean z) {
        return this.base.addMonitor(intentFilter, activityResult, z);
    }

    public ActivityMonitor addMonitor(String str, ActivityResult activityResult, boolean z) {
        return this.base.addMonitor(str, activityResult, z);
    }

    public boolean checkMonitorHit(ActivityMonitor activityMonitor, int i) {
        return this.base.checkMonitorHit(activityMonitor, i);
    }

    public Activity waitForMonitor(ActivityMonitor activityMonitor) {
        return this.base.waitForMonitor(activityMonitor);
    }

    public Activity waitForMonitorWithTimeout(ActivityMonitor activityMonitor, long j) {
        return this.base.waitForMonitorWithTimeout(activityMonitor, j);
    }

    public void removeMonitor(ActivityMonitor activityMonitor) {
        this.base.removeMonitor(activityMonitor);
    }

    public boolean invokeMenuActionSync(Activity activity, int i, int i2) {
        return this.base.invokeMenuActionSync(activity, i, i2);
    }

    public boolean invokeContextMenuAction(Activity activity, int i, int i2) {
        return this.base.invokeContextMenuAction(activity, i, i2);
    }

    public void sendStringSync(String str) {
        this.base.sendStringSync(str);
    }

    public void sendKeySync(KeyEvent keyEvent) {
        this.base.sendKeySync(keyEvent);
    }

    public void sendKeyDownUpSync(int i) {
        this.base.sendKeyDownUpSync(i);
    }

    public void sendCharacterSync(int i) {
        this.base.sendCharacterSync(i);
    }

    public void sendPointerSync(MotionEvent motionEvent) {
        this.base.sendPointerSync(motionEvent);
    }

    public void sendTrackballEventSync(MotionEvent motionEvent) {
        this.base.sendTrackballEventSync(motionEvent);
    }

    public Application newApplication(ClassLoader classLoader, String str, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return this.base.newApplication(classLoader, str, context);
    }

    public void callApplicationOnCreate(Application application) {
        this.base.callApplicationOnCreate(application);
    }

    public Activity newActivity(Class<?> cls, Context context, IBinder iBinder, Application application, Intent intent, ActivityInfo activityInfo, CharSequence charSequence, Activity activity, String str, Object obj) throws InstantiationException, IllegalAccessException {
        return this.base.newActivity(cls, context, iBinder, application, intent, activityInfo, charSequence, activity, str, obj);
    }

    public Activity newActivity(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return this.base.newActivity(classLoader, str, intent);
    }

    public void callActivityOnCreate(Activity activity, Bundle bundle) {
        this.base.callActivityOnCreate(activity, bundle);
    }

    @TargetApi(21)
    public void callActivityOnCreate(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        this.base.callActivityOnCreate(activity, bundle, persistableBundle);
    }

    public void callActivityOnDestroy(Activity activity) {
        this.base.callActivityOnDestroy(activity);
    }

    public void callActivityOnRestoreInstanceState(Activity activity, Bundle bundle) {
        this.base.callActivityOnRestoreInstanceState(activity, bundle);
    }

    @TargetApi(21)
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        this.base.callActivityOnRestoreInstanceState(activity, bundle, persistableBundle);
    }

    public void callActivityOnPostCreate(Activity activity, Bundle bundle) {
        this.base.callActivityOnPostCreate(activity, bundle);
    }

    @TargetApi(21)
    public void callActivityOnPostCreate(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        this.base.callActivityOnPostCreate(activity, bundle, persistableBundle);
    }

    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        this.base.callActivityOnNewIntent(activity, intent);
    }

    public void callActivityOnStart(Activity activity) {
        this.base.callActivityOnStart(activity);
    }

    public void callActivityOnRestart(Activity activity) {
        this.base.callActivityOnRestart(activity);
    }

    public void callActivityOnResume(Activity activity) {
        this.base.callActivityOnResume(activity);
    }

    public void callActivityOnStop(Activity activity) {
        this.base.callActivityOnStop(activity);
    }

    public void callActivityOnSaveInstanceState(Activity activity, Bundle bundle) {
        this.base.callActivityOnSaveInstanceState(activity, bundle);
    }

    @TargetApi(21)
    public void callActivityOnSaveInstanceState(Activity activity, Bundle bundle, PersistableBundle persistableBundle) {
        this.base.callActivityOnSaveInstanceState(activity, bundle, persistableBundle);
    }

    public void callActivityOnPause(Activity activity) {
        this.base.callActivityOnPause(activity);
    }

    public void callActivityOnUserLeaving(Activity activity) {
        this.base.callActivityOnUserLeaving(activity);
    }

    public Bundle getAllocCounts() {
        return this.base.getAllocCounts();
    }

    public Bundle getBinderCounts() {
        return this.base.getBinderCounts();
    }

    @TargetApi(18)
    public UiAutomation getUiAutomation() {
        return this.base.getUiAutomation();
    }
}
