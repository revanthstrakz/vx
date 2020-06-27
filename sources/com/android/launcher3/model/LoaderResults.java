package com.android.launcher3.model;

import android.os.Looper;
import android.util.Log;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherModel.Callbacks;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.PagedView;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.LooperIdleLock;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.ViewOnDrawExecutor;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class LoaderResults {
    private static final long INVALID_SCREEN_ID = -1;
    private static final int ITEMS_CHUNK = 6;
    private static final String TAG = "LoaderResults";
    private final LauncherAppState mApp;
    private final AllAppsList mBgAllAppsList;
    private final BgDataModel mBgDataModel;
    /* access modifiers changed from: private */
    public final WeakReference<Callbacks> mCallbacks;
    private final int mPageToBindFirst;
    private final Executor mUiExecutor = new MainThreadExecutor();

    public LoaderResults(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList, int i, WeakReference<Callbacks> weakReference) {
        this.mApp = launcherAppState;
        this.mBgDataModel = bgDataModel;
        this.mBgAllAppsList = allAppsList;
        this.mPageToBindFirst = i;
        if (weakReference == null) {
            weakReference = new WeakReference<>(null);
        }
        this.mCallbacks = weakReference;
    }

    public void bindWorkspace() {
        final int i;
        Callbacks callbacks = (Callbacks) this.mCallbacks.get();
        if (callbacks == null) {
            Log.w(TAG, "LoaderTask running with no launcher");
            return;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        synchronized (this.mBgDataModel) {
            arrayList.addAll(this.mBgDataModel.workspaceItems);
            arrayList2.addAll(this.mBgDataModel.appWidgets);
            arrayList3.addAll(this.mBgDataModel.workspaceScreens);
        }
        if (this.mPageToBindFirst != -1001) {
            i = this.mPageToBindFirst;
        } else {
            i = callbacks.getCurrentWorkspaceScreen();
        }
        if (i >= arrayList3.size()) {
            i = PagedView.INVALID_RESTORE_PAGE;
        }
        final boolean z = i >= 0;
        long longValue = z ? ((Long) arrayList3.get(i)).longValue() : -1;
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList arrayList6 = new ArrayList();
        long j = longValue;
        ArrayList arrayList7 = new ArrayList();
        filterCurrentWorkspaceItems(j, arrayList, arrayList4, arrayList5);
        filterCurrentWorkspaceItems(j, arrayList2, arrayList6, arrayList7);
        sortWorkspaceItemsSpatially(arrayList4);
        sortWorkspaceItemsSpatially(arrayList5);
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.clearPendingBinds();
                    callbacks.startBinding();
                }
            }
        });
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindScreens(arrayList3);
                }
            }
        });
        Executor executor = this.mUiExecutor;
        bindWorkspaceItems(arrayList4, arrayList6, executor);
        final Executor viewOnDrawExecutor = z ? new ViewOnDrawExecutor(this.mUiExecutor) : executor;
        executor.execute(new Runnable() {
            public void run() {
                Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.finishFirstPageBind(z ? (ViewOnDrawExecutor) viewOnDrawExecutor : null);
                }
            }
        });
        bindWorkspaceItems(arrayList5, arrayList7, viewOnDrawExecutor);
        viewOnDrawExecutor.execute(new Runnable() {
            public void run() {
                Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.finishBindingItems();
                }
            }
        });
        if (z) {
            this.mUiExecutor.execute(new Runnable() {
                public void run() {
                    Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                    if (callbacks != null) {
                        if (i != -1001) {
                            callbacks.onPageBoundSynchronously(i);
                        }
                        callbacks.executeOnNextDraw((ViewOnDrawExecutor) viewOnDrawExecutor);
                    }
                }
            });
        }
    }

    private <T extends ItemInfo> void filterCurrentWorkspaceItems(long j, ArrayList<T> arrayList, ArrayList<T> arrayList2, ArrayList<T> arrayList3) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (((ItemInfo) it.next()) == null) {
                it.remove();
            }
        }
        HashSet hashSet = new HashSet();
        Collections.sort(arrayList, new Comparator<ItemInfo>() {
            public int compare(ItemInfo itemInfo, ItemInfo itemInfo2) {
                return Utilities.longCompare(itemInfo.container, itemInfo2.container);
            }
        });
        Iterator it2 = arrayList.iterator();
        while (it2.hasNext()) {
            ItemInfo itemInfo = (ItemInfo) it2.next();
            if (itemInfo.container == -100) {
                if (itemInfo.screenId == j) {
                    arrayList2.add(itemInfo);
                    hashSet.add(Long.valueOf(itemInfo.f52id));
                } else {
                    arrayList3.add(itemInfo);
                }
            } else if (itemInfo.container == -101) {
                arrayList2.add(itemInfo);
                hashSet.add(Long.valueOf(itemInfo.f52id));
            } else if (hashSet.contains(Long.valueOf(itemInfo.container))) {
                arrayList2.add(itemInfo);
                hashSet.add(Long.valueOf(itemInfo.f52id));
            } else {
                arrayList3.add(itemInfo);
            }
        }
    }

    private void sortWorkspaceItemsSpatially(ArrayList<ItemInfo> arrayList) {
        InvariantDeviceProfile invariantDeviceProfile = this.mApp.getInvariantDeviceProfile();
        final int i = invariantDeviceProfile.numColumns;
        final int i2 = invariantDeviceProfile.numColumns * invariantDeviceProfile.numRows;
        Collections.sort(arrayList, new Comparator<ItemInfo>() {
            public int compare(ItemInfo itemInfo, ItemInfo itemInfo2) {
                if (itemInfo.container != itemInfo2.container) {
                    return Utilities.longCompare(itemInfo.container, itemInfo2.container);
                }
                switch ((int) itemInfo.container) {
                    case -101:
                        return Utilities.longCompare(itemInfo.screenId, itemInfo2.screenId);
                    case -100:
                        return Utilities.longCompare((itemInfo.screenId * ((long) i2)) + ((long) (itemInfo.cellY * i)) + ((long) itemInfo.cellX), (itemInfo2.screenId * ((long) i2)) + ((long) (itemInfo2.cellY * i)) + ((long) itemInfo2.cellX));
                    default:
                        return 0;
                }
            }
        });
    }

    private void bindWorkspaceItems(final ArrayList<ItemInfo> arrayList, ArrayList<LauncherAppWidgetInfo> arrayList2, Executor executor) {
        int size = arrayList.size();
        final int i = 0;
        while (i < size) {
            int i2 = i + 6;
            final int i3 = i2 <= size ? 6 : size - i;
            executor.execute(new Runnable() {
                public void run() {
                    Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                    if (callbacks != null) {
                        callbacks.bindItems(arrayList.subList(i, i + i3), false);
                    }
                }
            });
            i = i2;
        }
        int size2 = arrayList2.size();
        for (int i4 = 0; i4 < size2; i4++) {
            final ItemInfo itemInfo = (ItemInfo) arrayList2.get(i4);
            executor.execute(new Runnable() {
                public void run() {
                    Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                    if (callbacks != null) {
                        callbacks.bindItems(Collections.singletonList(itemInfo), false);
                    }
                }
            });
        }
    }

    public void bindDeepShortcuts() {
        final MultiHashMap clone;
        synchronized (this.mBgDataModel) {
            clone = this.mBgDataModel.deepShortcutMap.clone();
        }
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindDeepShortcutMap(clone);
                }
            }
        });
    }

    public void bindAllApps() {
        final ArrayList arrayList = (ArrayList) this.mBgAllAppsList.data.clone();
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindAllApplications(arrayList);
                }
            }
        });
    }

    public void bindWidgets() {
        final MultiHashMap widgetsMap = this.mBgDataModel.widgetsModel.getWidgetsMap();
        this.mUiExecutor.execute(new Runnable() {
            public void run() {
                Callbacks callbacks = (Callbacks) LoaderResults.this.mCallbacks.get();
                if (callbacks != null) {
                    callbacks.bindAllWidgets(widgetsMap);
                }
            }
        });
    }

    public LooperIdleLock newIdleLock(Object obj) {
        LooperIdleLock looperIdleLock = new LooperIdleLock(obj, Looper.getMainLooper());
        if (this.mCallbacks.get() == null) {
            looperIdleLock.queueIdle();
        }
        return looperIdleLock;
    }
}
