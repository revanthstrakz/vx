package android.app;

import android.app.ActivityThread.ActivityClientRecord;
import android.app.servertransaction.PendingTransactionActions;
import android.app.servertransaction.TransactionExecutor;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;
import android.util.MergedConfiguration;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.utils.ComponentUtils;
import com.lody.virtual.remote.StubActivityRecord;
import java.util.List;
import java.util.Map;
import mirror.android.app.ActivityManagerNative;
import mirror.android.app.ActivityThread;
import mirror.android.app.IActivityManager;

public class TransactionHandlerProxy extends ClientTransactionHandler {
    private static final String TAG = "TransactionHandlerProxy";
    private ClientTransactionHandler originalHandler;

    public TransactionHandlerProxy(ClientTransactionHandler clientTransactionHandler) {
        this.originalHandler = clientTransactionHandler;
    }

    /* access modifiers changed from: 0000 */
    public TransactionExecutor getTransactionExecutor() {
        return this.originalHandler.getTransactionExecutor();
    }

    /* access modifiers changed from: 0000 */
    public void sendMessage(int i, Object obj) {
        this.originalHandler.sendMessage(i, obj);
    }

    public void updatePendingConfiguration(Configuration configuration) {
        this.originalHandler.updatePendingConfiguration(configuration);
    }

    public void updateProcessState(int i, boolean z) {
        this.originalHandler.updateProcessState(i, z);
    }

    public void handleDestroyActivity(IBinder iBinder, boolean z, int i, boolean z2, String str) {
        this.originalHandler.handleDestroyActivity(iBinder, z, i, z2, str);
    }

    public void handlePauseActivity(IBinder iBinder, boolean z, boolean z2, int i, PendingTransactionActions pendingTransactionActions, String str) {
        this.originalHandler.handlePauseActivity(iBinder, z, z2, i, pendingTransactionActions, str);
    }

    public void handleResumeActivity(IBinder iBinder, boolean z, boolean z2, String str) {
        this.originalHandler.handleResumeActivity(iBinder, z, z2, str);
    }

    public void handleStopActivity(IBinder iBinder, boolean z, int i, PendingTransactionActions pendingTransactionActions, boolean z2, String str) {
        this.originalHandler.handleStopActivity(iBinder, z, i, pendingTransactionActions, z2, str);
    }

    public void reportStop(PendingTransactionActions pendingTransactionActions) {
        this.originalHandler.reportStop(pendingTransactionActions);
    }

    public void performRestartActivity(IBinder iBinder, boolean z) {
        this.originalHandler.performRestartActivity(iBinder, z);
    }

    public void handleActivityConfigurationChanged(IBinder iBinder, Configuration configuration, int i) {
        this.originalHandler.handleActivityConfigurationChanged(iBinder, configuration, i);
    }

    public void handleSendResult(IBinder iBinder, List list, String str) {
        this.originalHandler.handleSendResult(iBinder, list, str);
    }

    public void handleMultiWindowModeChanged(IBinder iBinder, boolean z, Configuration configuration) {
        this.originalHandler.handleMultiWindowModeChanged(iBinder, z, configuration);
    }

    public void handleNewIntent(IBinder iBinder, List list, boolean z) {
        this.originalHandler.handleNewIntent(iBinder, list, z);
    }

    public void handlePictureInPictureModeChanged(IBinder iBinder, boolean z, Configuration configuration) {
        this.originalHandler.handlePictureInPictureModeChanged(iBinder, z, configuration);
    }

    public void handleWindowVisibility(IBinder iBinder, boolean z) {
        this.originalHandler.handleWindowVisibility(iBinder, z);
    }

    public Activity handleLaunchActivity(ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions, Intent intent) {
        ActivityClientRecord activityClientRecord2 = activityClientRecord;
        StubActivityRecord stubActivityRecord = new StubActivityRecord((Intent) ActivityThread.ActivityClientRecord.intent.get(activityClientRecord2));
        if (stubActivityRecord.intent == null) {
            Log.i(TAG, "save instance intent is null, return");
            return null;
        }
        Intent intent2 = stubActivityRecord.intent;
        ComponentName componentName = stubActivityRecord.caller;
        IBinder iBinder = (IBinder) ActivityThread.ActivityClientRecord.token.get(activityClientRecord2);
        ActivityInfo activityInfo = stubActivityRecord.info;
        if (VClientImpl.get().getToken() == null) {
            if (VirtualCore.get().getInstalledAppInfo(activityInfo.packageName, 0) == null) {
                Log.i(TAG, "install app info is null, return");
                return null;
            }
            VActivityManager.get().processRestarted(activityInfo.packageName, activityInfo.processName, stubActivityRecord.userId);
            Log.i(TAG, "restart process, return");
            return handleLaunchActivity(activityClientRecord, pendingTransactionActions, intent);
        } else if (!VClientImpl.get().isBound()) {
            VClientImpl.get().bindApplicationForActivity(activityInfo.packageName, activityInfo.processName, intent2);
            Log.i(TAG, "rebound application, return");
            return handleLaunchActivity(activityClientRecord, pendingTransactionActions, intent);
        } else {
            int intValue = ((Integer) IActivityManager.getTaskForActivity.call(ActivityManagerNative.getDefault.call(new Object[0]), iBinder, Boolean.valueOf(false))).intValue();
            ActivityThread.ActivityClientRecord.packageInfo.get(activityClientRecord2);
            ActivityThread.ActivityClientRecord.packageInfo.set(activityClientRecord2, null);
            VActivityManager.get().onActivityCreate(ComponentUtils.toComponentName(activityInfo), componentName, iBinder, activityInfo, intent2, ComponentUtils.getTaskAffinity(activityInfo), intValue, activityInfo.launchMode, activityInfo.flags);
            intent2.setExtrasClassLoader(VClientImpl.get().getClassLoader(activityInfo.applicationInfo));
            ActivityThread.ActivityClientRecord.intent.set(activityClientRecord2, intent2);
            ActivityThread.ActivityClientRecord.activityInfo.set(activityClientRecord2, activityInfo);
            return this.originalHandler.handleLaunchActivity(activityClientRecord2, pendingTransactionActions, intent);
        }
    }

    public void handleStartActivity(ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions) {
        this.originalHandler.handleStartActivity(activityClientRecord, pendingTransactionActions);
    }

    public LoadedApk getPackageInfoNoCheck(ApplicationInfo applicationInfo, CompatibilityInfo compatibilityInfo) {
        return this.originalHandler.getPackageInfoNoCheck(applicationInfo, compatibilityInfo);
    }

    public void handleConfigurationChanged(Configuration configuration) {
        this.originalHandler.handleConfigurationChanged(configuration);
    }

    public ActivityClientRecord getActivityClient(IBinder iBinder) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getActivityClient : ");
        sb.append(iBinder);
        Log.i(str, sb.toString());
        return this.originalHandler.getActivityClient(iBinder);
    }

    public ActivityClientRecord prepareRelaunchActivity(IBinder iBinder, List list, List list2, int i, MergedConfiguration mergedConfiguration, boolean z) {
        return this.originalHandler.prepareRelaunchActivity(iBinder, list, list2, i, mergedConfiguration, z);
    }

    public void handleRelaunchActivity(ActivityClientRecord activityClientRecord, PendingTransactionActions pendingTransactionActions) {
        this.originalHandler.handleRelaunchActivity(activityClientRecord, pendingTransactionActions);
    }

    public void reportRelaunch(IBinder iBinder, PendingTransactionActions pendingTransactionActions) {
        this.originalHandler.reportRelaunch(iBinder, pendingTransactionActions);
    }

    public Map getActivitiesToBeDestroyed() {
        return this.originalHandler.getActivitiesToBeDestroyed();
    }

    public Activity getActivity(IBinder iBinder) {
        return this.originalHandler.getActivity(iBinder);
    }

    public void updatePendingActivityConfiguration(IBinder iBinder, Configuration configuration) {
        this.originalHandler.updatePendingActivityConfiguration(iBinder, configuration);
    }

    public void handleTopResumedActivityChanged(IBinder iBinder, boolean z, String str) {
        this.originalHandler.handleTopResumedActivityChanged(iBinder, z, str);
    }

    public void countLaunchingActivities(int i) {
        this.originalHandler.countLaunchingActivities(i);
    }

    public void handleNewIntent(IBinder iBinder, List list) {
        this.originalHandler.handleNewIntent(iBinder, list);
    }
}
