package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.text.TextUtils;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.ContentWriter;

public class ShortcutInfo extends ItemInfoWithIcon {
    public static final int DEFAULT = 0;
    public static final int FLAG_AUTOINSTALL_ICON = 2;
    public static final int FLAG_DISABLED_BY_PUBLISHER = 16;
    public static final int FLAG_DISABLED_LOCKED_USER = 32;
    public static final int FLAG_DISABLED_NOT_AVAILABLE = 2;
    public static final int FLAG_DISABLED_QUIET_USER = 8;
    public static final int FLAG_DISABLED_SAFEMODE = 1;
    public static final int FLAG_DISABLED_SUSPENDED = 4;
    public static final int FLAG_INSTALL_SESSION_ACTIVE = 4;
    @Deprecated
    public static final int FLAG_RESTORED_APP_TYPE = 240;
    public static final int FLAG_RESTORED_ICON = 1;
    public static final int FLAG_RESTORE_STARTED = 8;
    public static final int FLAG_SUPPORTS_WEB_UI = 16;
    CharSequence disabledMessage;
    public ShortcutIconResource iconResource;
    public Intent intent;
    public int isDisabled;
    private int mInstallProgress;
    public int status;

    public ShortcutInfo() {
        this.isDisabled = 0;
        this.itemType = 1;
    }

    public ShortcutInfo(ShortcutInfo shortcutInfo) {
        super(shortcutInfo);
        this.isDisabled = 0;
        this.title = shortcutInfo.title;
        this.intent = new Intent(shortcutInfo.intent);
        this.iconResource = shortcutInfo.iconResource;
        this.status = shortcutInfo.status;
        this.mInstallProgress = shortcutInfo.mInstallProgress;
        this.isDisabled = shortcutInfo.isDisabled;
    }

    public ShortcutInfo(AppInfo appInfo) {
        super(appInfo);
        this.isDisabled = 0;
        this.title = Utilities.trim(appInfo.title);
        this.intent = new Intent(appInfo.intent);
        this.isDisabled = appInfo.isDisabled;
    }

    @TargetApi(24)
    public ShortcutInfo(ShortcutInfoCompat shortcutInfoCompat, Context context) {
        this.isDisabled = 0;
        this.user = shortcutInfoCompat.getUserHandle();
        this.itemType = 6;
        updateFromDeepShortcutInfo(shortcutInfoCompat, context);
    }

    public void onAddToDatabase(ContentWriter contentWriter) {
        super.onAddToDatabase(contentWriter);
        contentWriter.put(BaseLauncherColumns.TITLE, this.title).put(BaseLauncherColumns.INTENT, getIntent()).put(Favorites.RESTORED, Integer.valueOf(this.status));
        if (!this.usingLowResIcon) {
            contentWriter.putIcon(this.iconBitmap, this.user);
        }
        if (this.iconResource != null) {
            contentWriter.put(BaseLauncherColumns.ICON_PACKAGE, this.iconResource.packageName).put(BaseLauncherColumns.ICON_RESOURCE, this.iconResource.resourceName);
        }
    }

    public Intent getIntent() {
        return this.intent;
    }

    public boolean hasStatusFlag(int i) {
        return (i & this.status) != 0;
    }

    public final boolean isPromise() {
        return hasStatusFlag(3);
    }

    public boolean hasPromiseIconUi() {
        return isPromise() && !hasStatusFlag(16);
    }

    public int getInstallProgress() {
        return this.mInstallProgress;
    }

    public void setInstallProgress(int i) {
        this.mInstallProgress = i;
        this.status |= 4;
    }

    public void updateFromDeepShortcutInfo(ShortcutInfoCompat shortcutInfoCompat, Context context) {
        this.intent = shortcutInfoCompat.makeIntent();
        this.title = shortcutInfoCompat.getShortLabel();
        CharSequence longLabel = shortcutInfoCompat.getLongLabel();
        if (TextUtils.isEmpty(longLabel)) {
            longLabel = shortcutInfoCompat.getShortLabel();
        }
        this.contentDescription = UserManagerCompat.getInstance(context).getBadgedLabelForUser(longLabel, this.user);
        if (shortcutInfoCompat.isEnabled()) {
            this.isDisabled &= -17;
        } else {
            this.isDisabled |= 16;
        }
        this.disabledMessage = shortcutInfoCompat.getDisabledMessage();
    }

    public String getDeepShortcutId() {
        if (this.itemType == 6) {
            return getIntent().getStringExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID);
        }
        return null;
    }

    public boolean isDisabled() {
        return this.isDisabled != 0;
    }

    public ComponentName getTargetComponent() {
        ComponentName componentName;
        ComponentName targetComponent = super.getTargetComponent();
        if (targetComponent != null || (this.itemType != 1 && !hasStatusFlag(16))) {
            return targetComponent;
        }
        String str = this.intent.getPackage();
        if (str == null) {
            componentName = null;
        } else {
            componentName = new ComponentName(str, IconCache.EMPTY_CLASS_NAME);
        }
        return componentName;
    }
}
