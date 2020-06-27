package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.LauncherApps.PinItemRequest;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.android.launcher3.C0622R;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.IconCache;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.compat.LauncherAppsCompatVO;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;

@TargetApi(26)
class PinShortcutRequestActivityInfo extends ShortcutConfigActivityInfo {
    private static final String DUMMY_COMPONENT_CLASS = "pinned-shortcut";
    private final Context mContext;
    private final ShortcutInfo mInfo;
    private final PinItemRequest mRequest;

    public int getItemType() {
        return 6;
    }

    public boolean isPersistable() {
        return false;
    }

    public boolean startConfigActivity(Activity activity, int i) {
        return false;
    }

    public PinShortcutRequestActivityInfo(PinItemRequest pinItemRequest, Context context) {
        super(new ComponentName(pinItemRequest.getShortcutInfo().getPackage(), DUMMY_COMPONENT_CLASS), pinItemRequest.getShortcutInfo().getUserHandle());
        this.mRequest = pinItemRequest;
        this.mInfo = pinItemRequest.getShortcutInfo();
        this.mContext = context;
    }

    public CharSequence getLabel() {
        return this.mInfo.getShortLabel();
    }

    public Drawable getFullResIcon(IconCache iconCache) {
        Drawable shortcutIconDrawable = ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getShortcutIconDrawable(this.mInfo, LauncherAppState.getIDP(this.mContext).fillResIconDpi);
        return shortcutIconDrawable == null ? new FastBitmapDrawable(iconCache.getDefaultIcon(Process.myUserHandle())) : shortcutIconDrawable;
    }

    public com.android.launcher3.ShortcutInfo createShortcutInfo() {
        return LauncherAppsCompatVO.createShortcutInfoFromPinItemRequest(this.mContext, this.mRequest, (long) (this.mContext.getResources().getInteger(C0622R.integer.config_dropAnimMaxDuration) + 500 + (this.mContext.getResources().getInteger(C0622R.integer.config_overlayTransitionTime) / 2)));
    }
}
