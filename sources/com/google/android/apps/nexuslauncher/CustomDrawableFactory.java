package com.google.android.apps.nexuslauncher;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Process;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.LooperExecutor;
import com.google.android.apps.nexuslauncher.clock.CustomClock;
import com.google.android.apps.nexuslauncher.clock.CustomClock.Metadata;
import com.google.android.apps.nexuslauncher.utils.ActionIntentFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class CustomDrawableFactory extends DynamicDrawableFactory implements Runnable {
    String iconPack;
    private final BroadcastReceiver mAutoUpdatePack;
    private final Context mContext;
    private CustomClock mCustomClockDrawer;
    private boolean mRegistered = false;
    final Map<ComponentName, String> packCalendars = new HashMap();
    final Map<Integer, Metadata> packClocks = new HashMap();
    final Map<ComponentName, Integer> packComponents = new HashMap();
    private Semaphore waiter = new Semaphore(0);

    public CustomDrawableFactory(Context context) {
        super(context);
        this.mContext = context;
        this.mCustomClockDrawer = new CustomClock(context);
        this.mAutoUpdatePack = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (!CustomIconUtils.usingValidPack(context)) {
                    CustomIconUtils.setCurrentPack(context, "");
                }
                CustomIconUtils.applyIconPackAsync(context);
            }
        };
        new LooperExecutor(LauncherModel.getWorkerLooper()).execute(this);
    }

    public void run() {
        reloadIconPack();
        this.waiter.release();
    }

    /* access modifiers changed from: 0000 */
    public void reloadIconPack() {
        this.iconPack = CustomIconUtils.getCurrentPack(this.mContext);
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mAutoUpdatePack);
            this.mRegistered = false;
        }
        if (!this.iconPack.isEmpty()) {
            this.mContext.registerReceiver(this.mAutoUpdatePack, ActionIntentFilter.newInstance(this.iconPack, "android.intent.action.PACKAGE_CHANGED", "android.intent.action.PACKAGE_REPLACED", "android.intent.action.PACKAGE_FULLY_REMOVED"), null, new Handler(LauncherModel.getWorkerLooper()));
            this.mRegistered = true;
        }
        this.packComponents.clear();
        this.packCalendars.clear();
        this.packClocks.clear();
        if (CustomIconUtils.usingValidPack(this.mContext)) {
            CustomIconUtils.parsePack(this, this.mContext.getPackageManager(), this.iconPack);
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized void ensureInitialLoadComplete() {
        if (this.waiter != null) {
            this.waiter.acquireUninterruptibly();
            this.waiter.release();
            this.waiter = null;
        }
    }

    public FastBitmapDrawable newIcon(Bitmap bitmap, ItemInfo itemInfo) {
        ensureInitialLoadComplete();
        ComponentName targetComponent = itemInfo.getTargetComponent();
        if (!this.packComponents.containsKey(itemInfo.getTargetComponent()) || !CustomIconProvider.isEnabledForApp(this.mContext, new ComponentKey(targetComponent, itemInfo.user))) {
            return super.newIcon(bitmap, itemInfo);
        }
        if (Utilities.ATLEAST_OREO && itemInfo.itemType == 0 && itemInfo.user.equals(Process.myUserHandle())) {
            int intValue = ((Integer) this.packComponents.get(targetComponent)).intValue();
            if (this.packClocks.containsKey(Integer.valueOf(intValue))) {
                return this.mCustomClockDrawer.drawIcon(bitmap, this.mContext.getPackageManager().getDrawable(this.iconPack, intValue, null), (Metadata) this.packClocks.get(Integer.valueOf(intValue)));
            }
        }
        return new FastBitmapDrawable(bitmap);
    }
}
