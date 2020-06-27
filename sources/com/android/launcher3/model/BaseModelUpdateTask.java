package com.android.launcher3.model;

import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherModel.CallbackTask;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.LauncherModel.ModelUpdateTask;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public abstract class BaseModelUpdateTask implements ModelUpdateTask {
    private static final boolean DEBUG_TASKS = false;
    private static final String TAG = "BaseModelUpdateTask";
    private AllAppsList mAllAppsList;
    private LauncherAppState mApp;
    private BgDataModel mDataModel;
    /* access modifiers changed from: private */
    public LauncherModel mModel;
    private Executor mUiExecutor;

    public abstract void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList);

    public void init(LauncherAppState launcherAppState, LauncherModel launcherModel, BgDataModel bgDataModel, AllAppsList allAppsList, Executor executor) {
        this.mApp = launcherAppState;
        this.mModel = launcherModel;
        this.mDataModel = bgDataModel;
        this.mAllAppsList = allAppsList;
        this.mUiExecutor = executor;
    }

    public final void run() {
        if (this.mModel.isModelLoaded()) {
            execute(this.mApp, this.mDataModel, this.mAllAppsList);
        }
    }

    public final void scheduleCallbackTask(final CallbackTask callbackTask) {
        final Callbacks callback = this.mModel.getCallback();
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                Callbacks callback = BaseModelUpdateTask.this.mModel.getCallback();
                if (callback == callback && callback != null) {
                    callbackTask.execute(callback);
                }
            }
        });
    }

    public ModelWriter getModelWriter() {
        return this.mModel.getWriter(false);
    }

    public void bindUpdatedShortcuts(final ArrayList<ShortcutInfo> arrayList, final UserHandle userHandle) {
        if (!arrayList.isEmpty()) {
            scheduleCallbackTask(new CallbackTask() {
                public void execute(Callbacks callbacks) {
                    callbacks.bindShortcutsChanged(arrayList, userHandle);
                }
            });
        }
    }

    public void bindDeepShortcuts(BgDataModel bgDataModel) {
        final MultiHashMap clone = bgDataModel.deepShortcutMap.clone();
        scheduleCallbackTask(new CallbackTask() {
            public void execute(Callbacks callbacks) {
                callbacks.bindDeepShortcutMap(clone);
            }
        });
    }

    public void bindUpdatedWidgets(BgDataModel bgDataModel) {
        final MultiHashMap widgetsMap = bgDataModel.widgetsModel.getWidgetsMap();
        scheduleCallbackTask(new CallbackTask() {
            public void execute(Callbacks callbacks) {
                callbacks.bindAllWidgets(widgetsMap);
            }
        });
    }

    public void deleteAndBindComponentsRemoved(final ItemInfoMatcher itemInfoMatcher) {
        getModelWriter().deleteItemsFromDatabase(itemInfoMatcher);
        scheduleCallbackTask(new CallbackTask() {
            public void execute(Callbacks callbacks) {
                callbacks.bindWorkspaceComponentsRemoved(itemInfoMatcher);
            }
        });
    }
}
