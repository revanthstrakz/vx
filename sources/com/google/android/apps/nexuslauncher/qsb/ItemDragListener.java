package com.google.android.apps.nexuslauncher.qsb;

import android.content.pm.LauncherActivityInfo;
import android.graphics.Rect;
import android.view.View;
import com.android.launcher3.InstallShortcutReceiver;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.ShortcutConfigActivityInfo.ShortcutConfigActivityInfoVO;
import com.android.launcher3.dragndrop.BaseItemDragListener;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingItemDragHelper;

public class ItemDragListener extends BaseItemDragListener {
    /* access modifiers changed from: private */
    public final LauncherActivityInfo mActivityInfo;

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
    }

    public ItemDragListener(LauncherActivityInfo launcherActivityInfo, Rect rect) {
        super(rect, rect.width(), rect.width());
        this.mActivityInfo = launcherActivityInfo;
    }

    /* access modifiers changed from: protected */
    public PendingItemDragHelper createDragHelper() {
        PendingAddShortcutInfo pendingAddShortcutInfo = new PendingAddShortcutInfo(new ShortcutConfigActivityInfoVO(this.mActivityInfo) {
            public ShortcutInfo createShortcutInfo() {
                return InstallShortcutReceiver.fromActivityInfo(ItemDragListener.this.mActivityInfo, ItemDragListener.this.mLauncher);
            }
        });
        View view = new View(this.mLauncher);
        view.setTag(pendingAddShortcutInfo);
        return new PendingItemDragHelper(view);
    }
}
