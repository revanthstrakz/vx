package com.android.launcher3.logging;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import com.android.launcher3.C0622R;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.userevent.nano.LauncherLogProto.LauncherEvent;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import java.util.Locale;
import java.util.UUID;

public class UserEventDispatcher {
    private static final boolean IS_VERBOSE = false;
    private static final int MAXIMUM_VIEW_HIERARCHY_LEVEL = 5;
    private static final String TAG = "UserEvent";
    private static final String UUID_STORAGE = "uuid";
    private long mActionDurationMillis;
    private long mElapsedContainerMillis;
    private long mElapsedSessionMillis;
    private boolean mIsInLandscapeMode;
    private boolean mIsInMultiWindowMode;
    private String mUuidStr;

    public interface LogContainerProvider {
        void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2);
    }

    public static UserEventDispatcher newInstance(Context context, boolean z, boolean z2) {
        SharedPreferences devicePrefs = Utilities.getDevicePrefs(context);
        String string = devicePrefs.getString(UUID_STORAGE, null);
        if (string == null) {
            string = UUID.randomUUID().toString();
            devicePrefs.edit().putString(UUID_STORAGE, string).apply();
        }
        UserEventDispatcher userEventDispatcher = (UserEventDispatcher) Utilities.getOverrideObject(UserEventDispatcher.class, context.getApplicationContext(), C0622R.string.user_event_dispatcher_class);
        userEventDispatcher.mIsInLandscapeMode = z;
        userEventDispatcher.mIsInMultiWindowMode = z2;
        userEventDispatcher.mUuidStr = string;
        return userEventDispatcher;
    }

    public static LogContainerProvider getLaunchProviderRecursive(@Nullable View view) {
        if (view == null) {
            return null;
        }
        ViewParent parent = view.getParent();
        int i = 5;
        while (parent != null) {
            int i2 = i - 1;
            if (i <= 0) {
                break;
            } else if (parent instanceof LogContainerProvider) {
                return (LogContainerProvider) parent;
            } else {
                parent = parent.getParent();
                i = i2;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean fillInLogContainerData(LauncherEvent launcherEvent, @Nullable View view) {
        LogContainerProvider launchProviderRecursive = getLaunchProviderRecursive(view);
        if (view == null || !(view.getTag() instanceof ItemInfo) || launchProviderRecursive == null) {
            return false;
        }
        launchProviderRecursive.fillInLogContainerData(view, (ItemInfo) view.getTag(), launcherEvent.srcTarget[0], launcherEvent.srcTarget[1]);
        return true;
    }

    public void logAppLaunch(View view, Intent intent, UserHandle userHandle) {
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), LoggerUtils.newItemTarget(view), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(newLauncherEvent, view)) {
            fillIntentInfo(newLauncherEvent.srcTarget[0], intent);
        }
        dispatchUserEvent(newLauncherEvent, intent);
    }

    /* access modifiers changed from: protected */
    public void fillIntentInfo(Target target, Intent intent) {
        target.intentHash = intent.hashCode();
        ComponentName component = intent.getComponent();
        if (component != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mUuidStr);
            sb.append(component.getPackageName());
            target.packageNameHash = sb.toString().hashCode();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(this.mUuidStr);
            sb2.append(component.flattenToString());
            target.componentHash = sb2.toString().hashCode();
        }
    }

    public void logNotificationLaunch(View view, PendingIntent pendingIntent) {
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), LoggerUtils.newItemTarget(view), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(newLauncherEvent, view)) {
            Target target = newLauncherEvent.srcTarget[0];
            StringBuilder sb = new StringBuilder();
            sb.append(this.mUuidStr);
            sb.append(pendingIntent.getCreatorPackage());
            target.packageNameHash = sb.toString().hashCode();
        }
        dispatchUserEvent(newLauncherEvent, null);
    }

    public void logActionCommand(int i, int i2) {
        logActionCommand(i, i2, 0);
    }

    public void logActionCommand(int i, int i2, int i3) {
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newCommandAction(i), LoggerUtils.newContainerTarget(i2));
        newLauncherEvent.srcTarget[0].pageIndex = i3;
        dispatchUserEvent(newLauncherEvent, null);
    }

    public void logActionCommand(int i, View view, int i2) {
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newCommandAction(i), LoggerUtils.newItemTarget(view), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(newLauncherEvent, view)) {
            newLauncherEvent.srcTarget[0].type = 3;
            newLauncherEvent.srcTarget[0].containerType = i2;
        }
        dispatchUserEvent(newLauncherEvent, null);
    }

    public void logActionOnControl(int i, int i2) {
        logActionOnControl(i, i2, null);
    }

    public void logActionOnControl(int i, int i2, @Nullable View view) {
        LauncherEvent launcherEvent;
        if (view == null) {
            launcherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(i), LoggerUtils.newTarget(2));
        } else {
            launcherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(i), LoggerUtils.newTarget(2), LoggerUtils.newTarget(3));
        }
        launcherEvent.srcTarget[0].controlType = i2;
        fillInLogContainerData(launcherEvent, view);
        dispatchUserEvent(launcherEvent, null);
    }

    public void logActionTapOutside(Target target) {
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), target);
        newLauncherEvent.action.isOutside = true;
        dispatchUserEvent(newLauncherEvent, null);
    }

    public void logActionOnContainer(int i, int i2, int i3) {
        logActionOnContainer(i, i2, i3, 0);
    }

    public void logActionOnContainer(int i, int i2, int i3, int i4) {
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(i), LoggerUtils.newContainerTarget(i3));
        newLauncherEvent.action.dir = i2;
        newLauncherEvent.srcTarget[0].pageIndex = i4;
        dispatchUserEvent(newLauncherEvent, null);
    }

    public void logActionOnItem(int i, int i2, int i3) {
        Target newTarget = LoggerUtils.newTarget(1);
        newTarget.itemType = i3;
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(i), newTarget);
        newLauncherEvent.action.dir = i2;
        dispatchUserEvent(newLauncherEvent, null);
    }

    public void logDeepShortcutsOpen(View view) {
        LogContainerProvider launchProviderRecursive = getLaunchProviderRecursive(view);
        if (view != null && (view.getTag() instanceof ItemInfo)) {
            ItemInfo itemInfo = (ItemInfo) view.getTag();
            LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(1), LoggerUtils.newItemTarget(itemInfo), LoggerUtils.newTarget(3));
            launchProviderRecursive.fillInLogContainerData(view, itemInfo, newLauncherEvent.srcTarget[0], newLauncherEvent.srcTarget[1]);
            dispatchUserEvent(newLauncherEvent, null);
            resetElapsedContainerMillis();
        }
    }

    public void logOverviewReorder() {
        dispatchUserEvent(LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(2), LoggerUtils.newContainerTarget(1), LoggerUtils.newContainerTarget(6)), null);
    }

    public void logDragNDrop(DragObject dragObject, View view) {
        LauncherEvent newLauncherEvent = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(2), LoggerUtils.newItemTarget(dragObject.originalDragInfo), LoggerUtils.newTarget(3));
        newLauncherEvent.destTarget = new Target[]{LoggerUtils.newItemTarget(dragObject.originalDragInfo), LoggerUtils.newDropTarget(view)};
        dragObject.dragSource.fillInLogContainerData(null, dragObject.originalDragInfo, newLauncherEvent.srcTarget[0], newLauncherEvent.srcTarget[1]);
        if (view instanceof LogContainerProvider) {
            ((LogContainerProvider) view).fillInLogContainerData(null, dragObject.dragInfo, newLauncherEvent.destTarget[0], newLauncherEvent.destTarget[1]);
        }
        newLauncherEvent.actionDurationMillis = SystemClock.uptimeMillis() - this.mActionDurationMillis;
        dispatchUserEvent(newLauncherEvent, null);
    }

    public final void resetElapsedContainerMillis() {
        this.mElapsedContainerMillis = SystemClock.uptimeMillis();
    }

    public final void resetElapsedSessionMillis() {
        this.mElapsedSessionMillis = SystemClock.uptimeMillis();
        this.mElapsedContainerMillis = SystemClock.uptimeMillis();
    }

    public final void resetActionDurationMillis() {
        this.mActionDurationMillis = SystemClock.uptimeMillis();
    }

    public void dispatchUserEvent(LauncherEvent launcherEvent, Intent intent) {
        launcherEvent.isInLandscapeMode = this.mIsInLandscapeMode;
        launcherEvent.isInMultiWindowMode = this.mIsInMultiWindowMode;
        launcherEvent.elapsedContainerMillis = SystemClock.uptimeMillis() - this.mElapsedContainerMillis;
        launcherEvent.elapsedSessionMillis = SystemClock.uptimeMillis() - this.mElapsedSessionMillis;
        if (IS_VERBOSE) {
            StringBuilder sb = new StringBuilder();
            sb.append("action:");
            sb.append(LoggerUtils.getActionStr(launcherEvent.action));
            String sb2 = sb.toString();
            if (launcherEvent.srcTarget != null && launcherEvent.srcTarget.length > 0) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(sb2);
                sb3.append("\n Source ");
                sb3.append(getTargetsStr(launcherEvent.srcTarget));
                sb2 = sb3.toString();
            }
            if (launcherEvent.destTarget != null && launcherEvent.destTarget.length > 0) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(sb2);
                sb4.append("\n Destination ");
                sb4.append(getTargetsStr(launcherEvent.destTarget));
                sb2 = sb4.toString();
            }
            StringBuilder sb5 = new StringBuilder();
            sb5.append(sb2);
            sb5.append(String.format(Locale.US, "\n Elapsed container %d ms session %d ms action %d ms", new Object[]{Long.valueOf(launcherEvent.elapsedContainerMillis), Long.valueOf(launcherEvent.elapsedSessionMillis), Long.valueOf(launcherEvent.actionDurationMillis)}));
            String sb6 = sb5.toString();
            StringBuilder sb7 = new StringBuilder();
            sb7.append(sb6);
            sb7.append("\n isInLandscapeMode ");
            sb7.append(launcherEvent.isInLandscapeMode);
            String sb8 = sb7.toString();
            StringBuilder sb9 = new StringBuilder();
            sb9.append(sb8);
            sb9.append("\n isInMultiWindowMode ");
            sb9.append(launcherEvent.isInMultiWindowMode);
            Log.d("UserEvent", sb9.toString());
        }
    }

    private static String getTargetsStr(Target[] targetArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("child:");
        sb.append(LoggerUtils.getTargetStr(targetArr[0]));
        String sb2 = sb.toString();
        for (int i = 1; i < targetArr.length; i++) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append("\tparent:");
            sb3.append(LoggerUtils.getTargetStr(targetArr[i]));
            sb2 = sb3.toString();
        }
        return sb2;
    }
}
