package com.android.launcher3.badge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.List;

public class BadgeInfo {
    public static final int MAX_COUNT = 999;
    private Shader mNotificationIcon;
    private NotificationInfo mNotificationInfo;
    private List<NotificationKeyData> mNotificationKeys = new ArrayList();
    private PackageUserKey mPackageUserKey;
    private int mTotalCount;

    public BadgeInfo(PackageUserKey packageUserKey) {
        this.mPackageUserKey = packageUserKey;
    }

    public boolean addOrUpdateNotificationKey(NotificationKeyData notificationKeyData) {
        NotificationKeyData notificationKeyData2;
        int indexOf = this.mNotificationKeys.indexOf(notificationKeyData);
        if (indexOf == -1) {
            notificationKeyData2 = null;
        } else {
            notificationKeyData2 = (NotificationKeyData) this.mNotificationKeys.get(indexOf);
        }
        if (notificationKeyData2 == null) {
            boolean add = this.mNotificationKeys.add(notificationKeyData);
            if (add) {
                this.mTotalCount += notificationKeyData.count;
            }
            return add;
        } else if (notificationKeyData2.count == notificationKeyData.count) {
            return false;
        } else {
            this.mTotalCount -= notificationKeyData2.count;
            this.mTotalCount += notificationKeyData.count;
            notificationKeyData2.count = notificationKeyData.count;
            return true;
        }
    }

    public boolean removeNotificationKey(NotificationKeyData notificationKeyData) {
        boolean remove = this.mNotificationKeys.remove(notificationKeyData);
        if (remove) {
            this.mTotalCount -= notificationKeyData.count;
        }
        return remove;
    }

    public List<NotificationKeyData> getNotificationKeys() {
        return this.mNotificationKeys;
    }

    public int getNotificationCount() {
        return Math.min(this.mTotalCount, MAX_COUNT);
    }

    public void setNotificationToShow(@Nullable NotificationInfo notificationInfo) {
        this.mNotificationInfo = notificationInfo;
        this.mNotificationIcon = null;
    }

    public boolean hasNotificationToShow() {
        return this.mNotificationInfo != null;
    }

    @Nullable
    public Shader getNotificationIconForBadge(Context context, int i, int i2, int i3) {
        if (this.mNotificationInfo == null) {
            return null;
        }
        if (this.mNotificationIcon == null) {
            Drawable newDrawable = this.mNotificationInfo.getIconForBackground(context, i).getConstantState().newDrawable();
            int i4 = i2 - (i3 * 2);
            newDrawable.setBounds(0, 0, i4, i4);
            Bitmap createBitmap = Bitmap.createBitmap(i2, i2, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            float f = (float) i3;
            canvas.translate(f, f);
            newDrawable.draw(canvas);
            this.mNotificationIcon = new BitmapShader(createBitmap, TileMode.CLAMP, TileMode.CLAMP);
        }
        return this.mNotificationIcon;
    }

    public boolean isIconLarge() {
        return this.mNotificationInfo != null && this.mNotificationInfo.isIconLarge();
    }

    public boolean shouldBeInvalidated(BadgeInfo badgeInfo) {
        return this.mPackageUserKey.equals(badgeInfo.mPackageUserKey) && (getNotificationCount() != badgeInfo.getNotificationCount() || hasNotificationToShow());
    }
}
