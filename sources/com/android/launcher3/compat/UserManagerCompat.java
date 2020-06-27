package com.android.launcher3.compat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.support.p001v4.internal.view.SupportMenu;
import android.support.p001v4.view.InputDeviceCompat;
import android.text.TextUtils;
import java.util.List;

public abstract class UserManagerCompat {
    private static UserManagerCompat sInstance;
    private static final Object sInstanceLock = new Object();

    public abstract void enableAndResetCache();

    public abstract CharSequence getBadgedLabelForUser(CharSequence charSequence, UserHandle userHandle);

    public abstract long getSerialNumberForUser(UserHandle userHandle);

    public abstract long getUserCreationTime(UserHandle userHandle);

    public abstract UserHandle getUserForSerialNumber(long j);

    public abstract List<UserHandle> getUserProfiles();

    public abstract boolean isDemoUser();

    public abstract boolean isQuietModeEnabled(UserHandle userHandle);

    public abstract boolean isUserUnlocked(UserHandle userHandle);

    protected UserManagerCompat() {
    }

    public static UserManagerCompat getInstance(Context context) {
        UserManagerCompat userManagerCompat;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new UserManagerCompatVA();
            }
            userManagerCompat = sInstance;
        }
        return userManagerCompat;
    }

    public Drawable getUserBadgedIcon(Drawable drawable, UserHandle userHandle) {
        int userId = toUserId(userHandle);
        if (userId == 0) {
            return generatorNumIcon(drawable, true, "1");
        }
        return generatorNumIcon(drawable, true, String.valueOf(userId));
    }

    public static UserHandle fromUserId(int i) {
        return (UserHandle) mirror.android.p017os.UserHandle.ctor.newInstance(Integer.valueOf(i));
    }

    public static int toUserId(UserHandle userHandle) {
        if (userHandle == null) {
            return 0;
        }
        return ((Integer) mirror.android.p017os.UserHandle.getIdentifier.call(userHandle, new Object[0])).intValue();
    }

    public static Drawable generatorNumIcon(Drawable drawable, boolean z, String str) {
        float f = Resources.getSystem().getDisplayMetrics().density / 1.5f;
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Canvas canvas = new Canvas(Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Config.ARGB_8888));
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setFilterBitmap(true);
        new Rect(0, 0, intrinsicWidth, intrinsicHeight);
        new Rect(0, 0, intrinsicWidth, intrinsicHeight);
        if (z) {
            if (TextUtils.isEmpty(str)) {
                str = "0";
            }
            if (!TextUtils.isDigitsOnly(str)) {
                str = "0";
            }
            if (Integer.valueOf(str).intValue() > 99) {
                String str2 = "99+";
                Paint paint2 = new Paint(InputDeviceCompat.SOURCE_KEYBOARD);
                paint2.setColor(-1);
                paint2.setTextSize(20.0f * f);
                paint2.setTypeface(Typeface.DEFAULT_BOLD);
                int measureText = (int) paint2.measureText(str2, 0, str2.length());
                int i = (int) (15.0f * f);
                int i2 = (int) (13.0f * f);
                Paint paint3 = new Paint(1);
                paint3.setColor(SupportMenu.CATEGORY_MASK);
                int i3 = intrinsicWidth - i2;
                float f2 = ((float) (i3 - measureText)) + (10.0f * f);
                float f3 = (float) i;
                float f4 = (float) i2;
                canvas.drawCircle(f2, f3, f4, paint3);
                Paint paint4 = new Paint(1);
                paint4.setColor(SupportMenu.CATEGORY_MASK);
                float f5 = (float) i3;
                canvas.drawCircle(f5, f3, f4, paint4);
                Paint paint5 = new Paint(1);
                paint5.setColor(SupportMenu.CATEGORY_MASK);
                float f6 = 2.0f * f;
                canvas.drawRect(new RectF(f2, f6, f5, ((float) (i2 * 2)) + f6), paint5);
                canvas.drawText(str2, ((float) (intrinsicWidth - (measureText / 2))) - (24.0f * f), f * 23.0f, paint2);
            } else {
                Paint paint6 = new Paint(InputDeviceCompat.SOURCE_KEYBOARD);
                paint6.setColor(-1);
                paint6.setTextSize(20.0f * f);
                paint6.setTypeface(Typeface.DEFAULT_BOLD);
                int measureText2 = (int) paint6.measureText(str, 0, str.length());
                Paint paint7 = new Paint(1);
                paint7.setColor(SupportMenu.CATEGORY_MASK);
                float f7 = 15.0f * f;
                canvas.drawCircle(((float) intrinsicWidth) - f7, f7, f7, paint7);
                canvas.drawText(str, ((float) (intrinsicWidth - (measureText2 / 2))) - f7, f * 22.0f, paint6);
            }
        }
        drawable.draw(canvas);
        return drawable;
    }
}
