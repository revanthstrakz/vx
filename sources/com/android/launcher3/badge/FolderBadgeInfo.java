package com.android.launcher3.badge;

import com.android.launcher3.Utilities;

public class FolderBadgeInfo extends BadgeInfo {
    private static final int MIN_COUNT = 0;
    private int mNumNotifications;

    public int getNotificationCount() {
        return 0;
    }

    public FolderBadgeInfo() {
        super(null);
    }

    public void addBadgeInfo(BadgeInfo badgeInfo) {
        if (badgeInfo != null) {
            this.mNumNotifications += badgeInfo.getNotificationKeys().size();
            this.mNumNotifications = Utilities.boundToRange(this.mNumNotifications, 0, (int) BadgeInfo.MAX_COUNT);
        }
    }

    public void subtractBadgeInfo(BadgeInfo badgeInfo) {
        if (badgeInfo != null) {
            this.mNumNotifications -= badgeInfo.getNotificationKeys().size();
            this.mNumNotifications = Utilities.boundToRange(this.mNumNotifications, 0, (int) BadgeInfo.MAX_COUNT);
        }
    }

    public boolean hasBadge() {
        return this.mNumNotifications > 0;
    }
}
