package com.android.launcher3.notification;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.service.notification.StatusBarNotification;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.util.PackageUserKey;

public class NotificationInfo implements OnClickListener {
    public final boolean autoCancel;
    public final boolean dismissable;
    public final PendingIntent intent;
    private int mBadgeIcon;
    private int mIconColor;
    private Drawable mIconDrawable;
    private boolean mIsIconLarge;
    public final String notificationKey;
    public final PackageUserKey packageUserKey;
    public final CharSequence text;
    public final CharSequence title;

    public NotificationInfo(Context context, StatusBarNotification statusBarNotification) {
        this.packageUserKey = PackageUserKey.fromNotification(statusBarNotification);
        this.notificationKey = statusBarNotification.getKey();
        Notification notification = statusBarNotification.getNotification();
        this.title = notification.extras.getCharSequence("android.title");
        this.text = notification.extras.getCharSequence("android.text");
        if (Utilities.ATLEAST_OREO) {
            this.mBadgeIcon = notification.getBadgeIconType();
        }
        boolean z = true;
        Icon largeIcon = this.mBadgeIcon == 1 ? null : notification.getLargeIcon();
        if (largeIcon == null) {
            Icon smallIcon = notification.getSmallIcon();
            if (smallIcon == null) {
                this.mIconDrawable = context.getApplicationInfo().loadIcon(context.getPackageManager());
            } else {
                this.mIconDrawable = smallIcon.loadDrawable(context);
            }
            this.mIconColor = statusBarNotification.getNotification().color;
            this.mIsIconLarge = false;
        } else {
            this.mIconDrawable = largeIcon.loadDrawable(context);
            this.mIsIconLarge = true;
        }
        if (this.mIconDrawable == null) {
            this.mIconDrawable = new BitmapDrawable(context.getResources(), LauncherAppState.getInstance(context).getIconCache().getDefaultIcon(statusBarNotification.getUser()));
            this.mBadgeIcon = 0;
        }
        this.intent = notification.contentIntent;
        this.autoCancel = (notification.flags & 16) != 0;
        if ((notification.flags & 2) != 0) {
            z = false;
        }
        this.dismissable = z;
    }

    public void onClick(View view) {
        if (this.intent != null) {
            Launcher launcher = Launcher.getLauncher(view.getContext());
            try {
                this.intent.send(null, 0, null, null, null, null, ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle());
                launcher.getUserEventDispatcher().logNotificationLaunch(view, this.intent);
            } catch (CanceledException e) {
                e.printStackTrace();
            }
            if (this.autoCancel) {
                launcher.getPopupDataProvider().cancelNotification(this.notificationKey);
            }
            AbstractFloatingView.closeOpenContainer(launcher, 2);
        }
    }

    public Drawable getIconForBackground(Context context, int i) {
        if (this.mIsIconLarge) {
            return this.mIconDrawable;
        }
        this.mIconColor = IconPalette.resolveContrastColor(context, this.mIconColor, i);
        Drawable mutate = this.mIconDrawable.mutate();
        mutate.setTintList(null);
        mutate.setTint(this.mIconColor);
        return mutate;
    }

    public boolean isIconLarge() {
        return this.mIsIconLarge;
    }

    public boolean shouldShowIconInBadge() {
        if (!this.mIsIconLarge || this.mBadgeIcon != 2) {
            return !this.mIsIconLarge && this.mBadgeIcon == 1;
        }
        return true;
    }
}
