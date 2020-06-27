package com.android.launcher3;

import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import com.android.launcher3.LauncherAppWidgetHost.ProviderChangedListener;
import com.android.launcher3.LauncherSettings.WorkspaceScreens;
import com.android.launcher3.compat.LauncherAppsCompat.OnAppsChangedCallbackCompat;
import com.android.launcher3.compat.PackageInstallerCompat.PackageInstallInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.dynamicui.ExtractionUtils;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.model.AddWorkspaceItemsTask;
import com.android.launcher3.model.BaseModelUpdateTask;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.CacheDataUpdatedTask;
import com.android.launcher3.model.LoaderResults;
import com.android.launcher3.model.LoaderTask;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.PackageInstallStateChangedTask;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.PackageUpdatedTask;
import com.android.launcher3.model.ShortcutsChangedTask;
import com.android.launcher3.model.UserLockStateChangedTask;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import com.android.launcher3.util.ViewOnDrawExecutor;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;

public class LauncherModel extends BroadcastReceiver implements OnAppsChangedCallbackCompat {
    private static final boolean DEBUG_RECEIVER = false;
    static final String TAG = "Launcher.Model";
    static final BgDataModel sBgDataModel = new BgDataModel();
    static final Handler sWorker = new Handler(sWorkerThread.getLooper());
    static final HandlerThread sWorkerThread = new HandlerThread("launcher-loader");
    final LauncherAppState mApp;
    private final AllAppsList mBgAllAppsList;
    WeakReference<Callbacks> mCallbacks;
    boolean mIsLoaderTaskRunning;
    LoaderTask mLoaderTask;
    final Object mLock = new Object();
    /* access modifiers changed from: private */
    public boolean mModelLoaded;
    private final Runnable mShortcutPermissionCheckRunnable = new Runnable() {
        public void run() {
            if (LauncherModel.this.mModelLoaded && DeepShortcutManager.getInstance(LauncherModel.this.mApp.getContext()).hasHostPermission() != LauncherModel.sBgDataModel.hasShortcutHostPermission) {
                LauncherModel.this.forceReload();
            }
        }
    };
    private final MainThreadExecutor mUiExecutor = new MainThreadExecutor();

    public interface CallbackTask {
        void execute(Callbacks callbacks);
    }

    public interface Callbacks extends ProviderChangedListener {
        void bindAllApplications(ArrayList<AppInfo> arrayList);

        void bindAllWidgets(MultiHashMap<PackageItemInfo, WidgetItem> multiHashMap);

        void bindAppInfosRemoved(ArrayList<AppInfo> arrayList);

        void bindAppsAdded(ArrayList<Long> arrayList, ArrayList<ItemInfo> arrayList2, ArrayList<ItemInfo> arrayList3);

        void bindAppsAddedOrUpdated(ArrayList<AppInfo> arrayList);

        void bindDeepShortcutMap(MultiHashMap<ComponentKey, String> multiHashMap);

        void bindItems(List<ItemInfo> list, boolean z);

        void bindPromiseAppProgressUpdated(PromiseAppInfo promiseAppInfo);

        void bindRestoreItemsChange(HashSet<ItemInfo> hashSet);

        void bindScreens(ArrayList<Long> arrayList);

        void bindShortcutsChanged(ArrayList<ShortcutInfo> arrayList, UserHandle userHandle);

        void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> arrayList);

        void bindWorkspaceComponentsRemoved(ItemInfoMatcher itemInfoMatcher);

        void clearPendingBinds();

        void executeOnNextDraw(ViewOnDrawExecutor viewOnDrawExecutor);

        void finishBindingItems();

        void finishFirstPageBind(ViewOnDrawExecutor viewOnDrawExecutor);

        int getCurrentWorkspaceScreen();

        void onPageBoundSynchronously(int i);

        boolean setLoadOnResume();

        void startBinding();
    }

    public class LoaderTransaction implements AutoCloseable {
        private final LoaderTask mTask;

        private LoaderTransaction(LoaderTask loaderTask) throws CancellationException {
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mLoaderTask == loaderTask) {
                    this.mTask = loaderTask;
                    LauncherModel.this.mIsLoaderTaskRunning = true;
                    LauncherModel.this.mModelLoaded = false;
                } else {
                    throw new CancellationException("Loader already stopped");
                }
            }
        }

        public void commit() {
            synchronized (LauncherModel.this.mLock) {
                LauncherModel.this.mModelLoaded = true;
            }
        }

        public void close() {
            synchronized (LauncherModel.this.mLock) {
                if (LauncherModel.this.mLoaderTask == this.mTask) {
                    LauncherModel.this.mLoaderTask = null;
                }
                LauncherModel.this.mIsLoaderTaskRunning = false;
            }
        }
    }

    public interface ModelUpdateTask extends Runnable {
        void init(LauncherAppState launcherAppState, LauncherModel launcherModel, BgDataModel bgDataModel, AllAppsList allAppsList, Executor executor);
    }

    static {
        sWorkerThread.start();
    }

    public boolean isModelLoaded() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mModelLoaded && this.mLoaderTask == null;
        }
        return z;
    }

    LauncherModel(LauncherAppState launcherAppState, IconCache iconCache, AppFilter appFilter) {
        this.mApp = launcherAppState;
        this.mBgAllAppsList = new AllAppsList(iconCache, appFilter);
    }

    private static void runOnWorkerThread(Runnable runnable) {
        if (sWorkerThread.getThreadId() == Process.myTid()) {
            runnable.run();
        } else {
            sWorker.post(runnable);
        }
    }

    public void setPackageState(PackageInstallInfo packageInstallInfo) {
        enqueueModelUpdateTask(new PackageInstallStateChangedTask(packageInstallInfo));
    }

    public void updateSessionDisplayInfo(String str) {
        HashSet hashSet = new HashSet();
        hashSet.add(str);
        enqueueModelUpdateTask(new CacheDataUpdatedTask(2, Process.myUserHandle(), hashSet));
    }

    public void addAndBindAddedWorkspaceItems(Provider<List<Pair<ItemInfo, Object>>> provider) {
        enqueueModelUpdateTask(new AddWorkspaceItemsTask(provider));
    }

    public ModelWriter getWriter(boolean z) {
        return new ModelWriter(this.mApp.getContext(), sBgDataModel, z);
    }

    static void checkItemInfoLocked(long j, ItemInfo itemInfo, StackTraceElement[] stackTraceElementArr) {
        ItemInfo itemInfo2 = (ItemInfo) sBgDataModel.itemsIdMap.get(j);
        if (itemInfo2 != null && itemInfo != itemInfo2) {
            if ((itemInfo2 instanceof ShortcutInfo) && (itemInfo instanceof ShortcutInfo)) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo2;
                ShortcutInfo shortcutInfo2 = (ShortcutInfo) itemInfo;
                if (shortcutInfo.title.toString().equals(shortcutInfo2.title.toString()) && shortcutInfo.intent.filterEquals(shortcutInfo2.intent) && shortcutInfo.f52id == shortcutInfo2.f52id && shortcutInfo.itemType == shortcutInfo2.itemType && shortcutInfo.container == shortcutInfo2.container && shortcutInfo.screenId == shortcutInfo2.screenId && shortcutInfo.cellX == shortcutInfo2.cellX && shortcutInfo.cellY == shortcutInfo2.cellY && shortcutInfo.spanX == shortcutInfo2.spanX && shortcutInfo.spanY == shortcutInfo2.spanY) {
                    return;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("item: ");
            sb.append(itemInfo != null ? itemInfo.toString() : "null");
            sb.append("modelItem: ");
            sb.append(itemInfo2 != null ? itemInfo2.toString() : "null");
            sb.append("Error: ItemInfo passed to checkItemInfo doesn't match original");
            RuntimeException runtimeException = new RuntimeException(sb.toString());
            if (stackTraceElementArr != null) {
                runtimeException.setStackTrace(stackTraceElementArr);
            }
            throw runtimeException;
        }
    }

    static void checkItemInfo(final ItemInfo itemInfo) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        final long j = itemInfo.f52id;
        runOnWorkerThread(new Runnable() {
            public void run() {
                synchronized (LauncherModel.sBgDataModel) {
                    LauncherModel.checkItemInfoLocked(j, itemInfo, stackTrace);
                }
            }
        });
    }

    public static void updateWorkspaceScreenOrder(Context context, ArrayList<Long> arrayList) {
        final ArrayList arrayList2 = new ArrayList(arrayList);
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri uri = WorkspaceScreens.CONTENT_URI;
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            if (((Long) it.next()).longValue() < 0) {
                it.remove();
            }
        }
        runOnWorkerThread(new Runnable() {
            public void run() {
                ArrayList arrayList = new ArrayList();
                arrayList.add(ContentProviderOperation.newDelete(uri).build());
                int size = arrayList2.size();
                for (int i = 0; i < size; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_id", Long.valueOf(((Long) arrayList2.get(i)).longValue()));
                    contentValues.put(WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                    arrayList.add(ContentProviderOperation.newInsert(uri).withValues(contentValues).build());
                }
                try {
                    contentResolver.applyBatch(LauncherProvider.AUTHORITY, arrayList);
                    synchronized (LauncherModel.sBgDataModel) {
                        LauncherModel.sBgDataModel.workspaceScreens.clear();
                        LauncherModel.sBgDataModel.workspaceScreens.addAll(arrayList2);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void initialize(Callbacks callbacks) {
        synchronized (this.mLock) {
            Preconditions.assertUIThread();
            this.mCallbacks = new WeakReference<>(callbacks);
        }
    }

    public void onPackageChanged(String str, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(2, userHandle, str));
    }

    public void onPackageRemoved(String str, UserHandle userHandle) {
        onPackagesRemoved(userHandle, str);
    }

    public void onPackagesRemoved(UserHandle userHandle, String... strArr) {
        enqueueModelUpdateTask(new PackageUpdatedTask(3, userHandle, strArr));
    }

    public void onPackageAdded(String str, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(1, userHandle, str));
    }

    public void onPackagesAvailable(String[] strArr, UserHandle userHandle, boolean z) {
        enqueueModelUpdateTask(new PackageUpdatedTask(2, userHandle, strArr));
    }

    public void onPackagesUnavailable(String[] strArr, UserHandle userHandle, boolean z) {
        if (!z) {
            enqueueModelUpdateTask(new PackageUpdatedTask(4, userHandle, strArr));
        }
    }

    public void onPackagesSuspended(String[] strArr, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(5, userHandle, strArr));
    }

    public void onPackagesUnsuspended(String[] strArr, UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(6, userHandle, strArr));
    }

    public void onShortcutsChanged(String str, List<ShortcutInfoCompat> list, UserHandle userHandle) {
        enqueueModelUpdateTask(new ShortcutsChangedTask(str, list, userHandle, true));
    }

    public void updatePinnedShortcuts(String str, List<ShortcutInfoCompat> list, UserHandle userHandle) {
        enqueueModelUpdateTask(new ShortcutsChangedTask(str, list, userHandle, false));
    }

    public void onPackagesReload(UserHandle userHandle) {
        enqueueModelUpdateTask(new PackageUpdatedTask(8, userHandle, new String[0]));
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            forceReload();
        } else if ("android.intent.action.MANAGED_PROFILE_ADDED".equals(action) || "android.intent.action.MANAGED_PROFILE_REMOVED".equals(action)) {
            UserManagerCompat.getInstance(context).enableAndResetCache();
            forceReload();
        } else if ("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(action)) {
            UserHandle userHandle = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
            if (userHandle != null) {
                if ("android.intent.action.MANAGED_PROFILE_AVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action)) {
                    enqueueModelUpdateTask(new PackageUpdatedTask(7, userHandle, new String[0]));
                }
                if ("android.intent.action.MANAGED_PROFILE_UNAVAILABLE".equals(action) || "android.intent.action.MANAGED_PROFILE_UNLOCKED".equals(action)) {
                    enqueueModelUpdateTask(new UserLockStateChangedTask(userHandle));
                }
            }
        } else if ("android.intent.action.WALLPAPER_CHANGED".equals(action)) {
            ExtractionUtils.startColorExtractionServiceIfNecessary(context);
        }
    }

    public void forceReload() {
        synchronized (this.mLock) {
            stopLoader();
            this.mModelLoaded = false;
        }
        startLoaderFromBackground();
    }

    public void startLoaderFromBackground() {
        Callbacks callback = getCallback();
        if (callback != null && !callback.setLoadOnResume()) {
            startLoader(callback.getCurrentWorkspaceScreen());
        }
    }

    public boolean isCurrentCallbacks(Callbacks callbacks) {
        return this.mCallbacks != null && this.mCallbacks.get() == callbacks;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0053, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startLoader(int r11) {
        /*
            r10 = this;
            r0 = 2
            com.android.launcher3.InstallShortcutReceiver.enableInstallQueue(r0)
            java.lang.Object r0 = r10.mLock
            monitor-enter(r0)
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r1 = r10.mCallbacks     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0051
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r1 = r10.mCallbacks     // Catch:{ all -> 0x0054 }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x0054 }
            if (r1 == 0) goto L_0x0051
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r1 = r10.mCallbacks     // Catch:{ all -> 0x0054 }
            java.lang.Object r1 = r1.get()     // Catch:{ all -> 0x0054 }
            com.android.launcher3.LauncherModel$Callbacks r1 = (com.android.launcher3.LauncherModel.Callbacks) r1     // Catch:{ all -> 0x0054 }
            com.android.launcher3.MainThreadExecutor r2 = r10.mUiExecutor     // Catch:{ all -> 0x0054 }
            com.android.launcher3.LauncherModel$4 r3 = new com.android.launcher3.LauncherModel$4     // Catch:{ all -> 0x0054 }
            r3.<init>(r1)     // Catch:{ all -> 0x0054 }
            r2.execute(r3)     // Catch:{ all -> 0x0054 }
            r10.stopLoader()     // Catch:{ all -> 0x0054 }
            com.android.launcher3.model.LoaderResults r1 = new com.android.launcher3.model.LoaderResults     // Catch:{ all -> 0x0054 }
            com.android.launcher3.LauncherAppState r5 = r10.mApp     // Catch:{ all -> 0x0054 }
            com.android.launcher3.model.BgDataModel r6 = sBgDataModel     // Catch:{ all -> 0x0054 }
            com.android.launcher3.AllAppsList r7 = r10.mBgAllAppsList     // Catch:{ all -> 0x0054 }
            java.lang.ref.WeakReference<com.android.launcher3.LauncherModel$Callbacks> r9 = r10.mCallbacks     // Catch:{ all -> 0x0054 }
            r4 = r1
            r8 = r11
            r4.<init>(r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0054 }
            boolean r11 = r10.mModelLoaded     // Catch:{ all -> 0x0054 }
            if (r11 == 0) goto L_0x004e
            boolean r11 = r10.mIsLoaderTaskRunning     // Catch:{ all -> 0x0054 }
            if (r11 != 0) goto L_0x004e
            r1.bindWorkspace()     // Catch:{ all -> 0x0054 }
            r1.bindAllApps()     // Catch:{ all -> 0x0054 }
            r1.bindDeepShortcuts()     // Catch:{ all -> 0x0054 }
            r1.bindWidgets()     // Catch:{ all -> 0x0054 }
            r11 = 1
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            return r11
        L_0x004e:
            r10.startLoaderForResults(r1)     // Catch:{ all -> 0x0054 }
        L_0x0051:
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            r11 = 0
            return r11
        L_0x0054:
            r11 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0054 }
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherModel.startLoader(int):boolean");
    }

    public void stopLoader() {
        synchronized (this.mLock) {
            LoaderTask loaderTask = this.mLoaderTask;
            this.mLoaderTask = null;
            if (loaderTask != null) {
                loaderTask.stopLocked();
            }
        }
    }

    public void startLoaderForResults(LoaderResults loaderResults) {
        synchronized (this.mLock) {
            stopLoader();
            this.mLoaderTask = new LoaderTask(this.mApp, this.mBgAllAppsList, sBgDataModel, loaderResults);
            runOnWorkerThread(this.mLoaderTask);
        }
    }

    public static ArrayList<Long> loadWorkspaceScreensDb(Context context) {
        return LauncherDbUtils.getScreenIdsFromCursor(context.getContentResolver().query(WorkspaceScreens.CONTENT_URI, null, null, null, WorkspaceScreens.SCREEN_RANK));
    }

    public void onInstallSessionCreated(final PackageInstallInfo packageInstallInfo) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                allAppsList.addPromiseApp(launcherAppState.getContext(), packageInstallInfo);
                if (!allAppsList.added.isEmpty()) {
                    final ArrayList arrayList = new ArrayList(allAppsList.added);
                    allAppsList.added.clear();
                    scheduleCallbackTask(new CallbackTask() {
                        public void execute(Callbacks callbacks) {
                            callbacks.bindAppsAddedOrUpdated(arrayList);
                        }
                    });
                }
            }
        });
    }

    public LoaderTransaction beginLoader(LoaderTask loaderTask) throws CancellationException {
        return new LoaderTransaction(loaderTask);
    }

    public void refreshShortcutsIfRequired() {
        if (Utilities.ATLEAST_NOUGAT_MR1) {
            sWorker.removeCallbacks(this.mShortcutPermissionCheckRunnable);
            sWorker.post(this.mShortcutPermissionCheckRunnable);
        }
    }

    public void onPackageIconsUpdated(HashSet<String> hashSet, UserHandle userHandle) {
        enqueueModelUpdateTask(new CacheDataUpdatedTask(1, userHandle, hashSet));
    }

    public void enqueueModelUpdateTask(ModelUpdateTask modelUpdateTask) {
        modelUpdateTask.init(this.mApp, this, sBgDataModel, this.mBgAllAppsList, this.mUiExecutor);
        runOnWorkerThread(modelUpdateTask);
    }

    public void updateAndBindShortcutInfo(final ShortcutInfo shortcutInfo, final ShortcutInfoCompat shortcutInfoCompat) {
        updateAndBindShortcutInfo(new Provider<ShortcutInfo>() {
            public ShortcutInfo get() {
                shortcutInfo.updateFromDeepShortcutInfo(shortcutInfoCompat, LauncherModel.this.mApp.getContext());
                shortcutInfo.iconBitmap = LauncherIcons.createShortcutIcon(shortcutInfoCompat, LauncherModel.this.mApp.getContext());
                return shortcutInfo;
            }
        });
    }

    public void updateAndBindShortcutInfo(final Provider<ShortcutInfo> provider) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                ShortcutInfo shortcutInfo = (ShortcutInfo) provider.get();
                ArrayList arrayList = new ArrayList();
                arrayList.add(shortcutInfo);
                bindUpdatedShortcuts(arrayList, shortcutInfo.user);
            }
        });
    }

    public void refreshAndBindWidgetsAndShortcuts(@Nullable final PackageUserKey packageUserKey) {
        enqueueModelUpdateTask(new BaseModelUpdateTask() {
            public void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList) {
                bgDataModel.widgetsModel.update(launcherAppState, packageUserKey);
                bindUpdatedWidgets(bgDataModel);
            }
        });
    }

    public void dumpState(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (strArr.length > 0 && TextUtils.equals(strArr[0], "--all")) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("All apps list: size=");
            sb.append(this.mBgAllAppsList.data.size());
            printWriter.println(sb.toString());
            Iterator it = this.mBgAllAppsList.data.iterator();
            while (it.hasNext()) {
                AppInfo appInfo = (AppInfo) it.next();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append("   title=\"");
                sb2.append(appInfo.title);
                sb2.append("\" iconBitmap=");
                sb2.append(appInfo.iconBitmap);
                sb2.append(" componentName=");
                sb2.append(appInfo.componentName.getPackageName());
                printWriter.println(sb2.toString());
            }
        }
        sBgDataModel.dump(str, fileDescriptor, printWriter, strArr);
    }

    public Callbacks getCallback() {
        if (this.mCallbacks != null) {
            return (Callbacks) this.mCallbacks.get();
        }
        return null;
    }

    public static Looper getWorkerLooper() {
        return sWorkerThread.getLooper();
    }

    public static void setWorkerPriority(int i) {
        Process.setThreadPriority(sWorkerThread.getThreadId(), i);
    }
}
