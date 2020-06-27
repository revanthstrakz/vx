package com.google.android.apps.nexuslauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Process;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.DrawableFactory;
import com.google.android.apps.nexuslauncher.clock.DynamicClock;

public class DynamicDrawableFactory extends DrawableFactory {
    private final DynamicClock mDynamicClockDrawer;

    public DynamicDrawableFactory(Context context) {
        this.mDynamicClockDrawer = new DynamicClock(context);
    }

    public FastBitmapDrawable newIcon(Bitmap bitmap, ItemInfo itemInfo) {
        if (itemInfo == null || !Utilities.ATLEAST_OREO || itemInfo.itemType != 0 || !DynamicClock.DESK_CLOCK.equals(itemInfo.getTargetComponent()) || !itemInfo.user.equals(Process.myUserHandle())) {
            return super.newIcon(bitmap, itemInfo);
        }
        return this.mDynamicClockDrawer.drawIcon(bitmap);
    }
}
