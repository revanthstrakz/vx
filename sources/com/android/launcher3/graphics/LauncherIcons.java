package com.android.launcher3.graphics;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import com.android.launcher3.AppInfo;
import com.android.launcher3.C0622R;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.IconCache;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.Provider;

public class LauncherIcons {
    private static final Canvas sCanvas = new Canvas();
    private static final Rect sOldBounds = new Rect();

    private static class FixedSizeBitmapDrawable extends BitmapDrawable {
        public FixedSizeBitmapDrawable(Bitmap bitmap) {
            super(null, bitmap);
        }

        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }

    static Drawable wrapToAdaptiveIconDrawable(Context context, Drawable drawable, float f) {
        return drawable;
    }

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
    }

    public static Bitmap createIconBitmap(ShortcutIconResource shortcutIconResource, Context context) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(shortcutIconResource.packageName);
            if (resourcesForApplication != null) {
                return createIconBitmap(resourcesForApplication.getDrawableForDensity(resourcesForApplication.getIdentifier(shortcutIconResource.resourceName, null, null), LauncherAppState.getIDP(context).fillResIconDpi), context);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public static Bitmap createIconBitmap(Bitmap bitmap, Context context) {
        int i = LauncherAppState.getIDP(context).iconBitmapSize;
        if (i == bitmap.getWidth() && i == bitmap.getHeight()) {
            return bitmap;
        }
        return createIconBitmap((Drawable) new BitmapDrawable(context.getResources(), bitmap), context);
    }

    public static Bitmap createBadgedIconBitmap(Drawable drawable, UserHandle userHandle, Context context, int i) {
        float f;
        if (!FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION) {
            IconNormalizer instance = IconNormalizer.getInstance(context);
            if (!Utilities.ATLEAST_OREO || i < 26) {
                f = instance.getScale(drawable, null, null, null);
            } else {
                boolean[] zArr = new boolean[1];
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) context.getDrawable(C0622R.C0624drawable.adaptive_icon_drawable_wrapper).mutate();
                adaptiveIconDrawable.setBounds(0, 0, 1, 1);
                f = instance.getScale(drawable, null, adaptiveIconDrawable.getIconMask(), zArr);
            }
        } else {
            f = 1.0f;
        }
        Bitmap createIconBitmap = createIconBitmap(drawable, context, f);
        if (Utilities.ATLEAST_OREO && (drawable instanceof AdaptiveIconDrawable)) {
            createIconBitmap = ShadowGenerator.getInstance(context).recreateIcon(createIconBitmap);
        }
        return badgeIconForUser(createIconBitmap, userHandle, context);
    }

    public static Bitmap badgeIconForUser(Bitmap bitmap, UserHandle userHandle, Context context) {
        if (userHandle == null) {
            return bitmap;
        }
        Drawable userBadgedIcon = UserManagerCompat.getInstance(context).getUserBadgedIcon(new FixedSizeBitmapDrawable(bitmap), userHandle);
        if (userBadgedIcon instanceof BitmapDrawable) {
            return ((BitmapDrawable) userBadgedIcon).getBitmap();
        }
        return createIconBitmap(userBadgedIcon, context);
    }

    public static Bitmap createScaledBitmapWithoutShadow(Drawable drawable, Context context, int i) {
        float f;
        RectF rectF = new RectF();
        if (!FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION) {
            IconNormalizer instance = IconNormalizer.getInstance(context);
            if (!Utilities.ATLEAST_OREO || i < 26) {
                f = instance.getScale(drawable, rectF, null, null);
            } else {
                boolean[] zArr = new boolean[1];
                AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) context.getDrawable(C0622R.C0624drawable.adaptive_icon_drawable_wrapper).mutate();
                adaptiveIconDrawable.setBounds(0, 0, 1, 1);
                f = instance.getScale(drawable, rectF, adaptiveIconDrawable.getIconMask(), zArr);
                boolean z = Utilities.ATLEAST_OREO;
            }
        } else {
            f = 1.0f;
        }
        return createIconBitmap(drawable, context, Math.min(f, ShadowGenerator.getScaleForBounds(rectF)));
    }

    public static Bitmap addShadowToIcon(Bitmap bitmap, Context context) {
        return ShadowGenerator.getInstance(context).recreateIcon(bitmap);
    }

    public static Bitmap badgeWithBitmap(Bitmap bitmap, Bitmap bitmap2, Context context) {
        return badgeWithDrawable(bitmap, new FastBitmapDrawable(bitmap2), context);
    }

    public static Bitmap badgeWithDrawable(Bitmap bitmap, Drawable drawable, Context context) {
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(C0622R.dimen.profile_badge_size);
        synchronized (sCanvas) {
            sCanvas.setBitmap(bitmap);
            int width = bitmap.getWidth();
            int i = width - dimensionPixelSize;
            drawable.setBounds(i, i, width, width);
            drawable.draw(sCanvas);
            sCanvas.setBitmap(null);
        }
        return bitmap;
    }

    public static Bitmap createIconBitmap(Drawable drawable, Context context) {
        Bitmap createIconBitmap = createIconBitmap(drawable, context, (!Utilities.ATLEAST_OREO || !(drawable instanceof AdaptiveIconDrawable)) ? 1.0f : ShadowGenerator.getScaleForBounds(new RectF(0.0f, 0.0f, 0.0f, 0.0f)));
        return (!Utilities.ATLEAST_OREO || !(drawable instanceof AdaptiveIconDrawable)) ? createIconBitmap : ShadowGenerator.getInstance(context).recreateIcon(createIconBitmap);
    }

    public static Bitmap createIconBitmap(Drawable drawable, Context context, float f) {
        int i;
        int i2;
        Bitmap createBitmap;
        int i3;
        int i4;
        synchronized (sCanvas) {
            int i5 = LauncherAppState.getIDP(context).iconBitmapSize;
            if (drawable instanceof PaintDrawable) {
                PaintDrawable paintDrawable = (PaintDrawable) drawable;
                paintDrawable.setIntrinsicWidth(i5);
                paintDrawable.setIntrinsicHeight(i5);
            } else if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && bitmap.getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float f2 = ((float) intrinsicWidth) / ((float) intrinsicHeight);
                if (intrinsicWidth > intrinsicHeight) {
                    i2 = (int) (((float) i5) / f2);
                    i = i5;
                } else if (intrinsicHeight > intrinsicWidth) {
                    i = (int) (((float) i5) * f2);
                    i2 = i5;
                }
                createBitmap = Bitmap.createBitmap(i5, i5, Config.ARGB_8888);
                Canvas canvas = sCanvas;
                canvas.setBitmap(createBitmap);
                i3 = (i5 - i) / 2;
                i4 = (i5 - i2) / 2;
                sOldBounds.set(drawable.getBounds());
                if (Utilities.ATLEAST_OREO || !(drawable instanceof AdaptiveIconDrawable)) {
                    drawable.setBounds(i3, i4, i + i3, i2 + i4);
                } else {
                    int max = Math.max((int) (((float) i5) * 0.010416667f), Math.min(i3, i4));
                    int max2 = Math.max(i, i2);
                    drawable.setBounds(max, max, max2, max2);
                }
                canvas.save(1);
                canvas.scale(f, f, (float) (i5 / 2), (float) (i5 / 2));
                drawable.draw(canvas);
                canvas.restore();
                drawable.setBounds(sOldBounds);
                canvas.setBitmap(null);
            }
            i = i5;
            i2 = i;
            createBitmap = Bitmap.createBitmap(i5, i5, Config.ARGB_8888);
            Canvas canvas2 = sCanvas;
            canvas2.setBitmap(createBitmap);
            i3 = (i5 - i) / 2;
            i4 = (i5 - i2) / 2;
            sOldBounds.set(drawable.getBounds());
            if (Utilities.ATLEAST_OREO) {
            }
            drawable.setBounds(i3, i4, i + i3, i2 + i4);
            canvas2.save(1);
            canvas2.scale(f, f, (float) (i5 / 2), (float) (i5 / 2));
            drawable.draw(canvas2);
            canvas2.restore();
            drawable.setBounds(sOldBounds);
            canvas2.setBitmap(null);
        }
        return createBitmap;
    }

    public static Bitmap createShortcutIcon(ShortcutInfoCompat shortcutInfoCompat, Context context) {
        return createShortcutIcon(shortcutInfoCompat, context, true);
    }

    public static Bitmap createShortcutIcon(ShortcutInfoCompat shortcutInfoCompat, Context context, boolean z) {
        return createShortcutIcon(shortcutInfoCompat, context, z, null);
    }

    public static Bitmap createShortcutIcon(ShortcutInfoCompat shortcutInfoCompat, Context context, final Bitmap bitmap) {
        return createShortcutIcon(shortcutInfoCompat, context, true, new Provider<Bitmap>() {
            public Bitmap get() {
                return bitmap;
            }
        });
    }

    public static Bitmap createShortcutIcon(ShortcutInfoCompat shortcutInfoCompat, Context context, boolean z, @Nullable Provider<Bitmap> provider) {
        Bitmap bitmap;
        LauncherAppState instance = LauncherAppState.getInstance(context);
        Drawable shortcutIconDrawable = DeepShortcutManager.getInstance(context).getShortcutIconDrawable(shortcutInfoCompat, instance.getInvariantDeviceProfile().fillResIconDpi);
        IconCache iconCache = instance.getIconCache();
        if (shortcutIconDrawable != null) {
            bitmap = createScaledBitmapWithoutShadow(shortcutIconDrawable, context, 0);
        } else {
            bitmap = provider != null ? (Bitmap) provider.get() : null;
            if (bitmap == null) {
                bitmap = iconCache.getDefaultIcon(Process.myUserHandle());
            }
        }
        if (!z) {
            return bitmap;
        }
        return badgeWithBitmap(addShadowToIcon(bitmap, context), getShortcutInfoBadge(shortcutInfoCompat, iconCache), context);
    }

    public static Bitmap getShortcutInfoBadge(ShortcutInfoCompat shortcutInfoCompat, IconCache iconCache) {
        ComponentName activity = shortcutInfoCompat.getActivity();
        if (activity != null) {
            AppInfo appInfo = new AppInfo();
            appInfo.user = shortcutInfoCompat.getUserHandle();
            appInfo.componentName = activity;
            appInfo.intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER").setComponent(activity);
            iconCache.getTitleAndIcon(appInfo, false);
            return appInfo.iconBitmap;
        }
        PackageItemInfo packageItemInfo = new PackageItemInfo(shortcutInfoCompat.getPackage());
        iconCache.getTitleAndIconForApp(packageItemInfo, false);
        return packageItemInfo.iconBitmap;
    }
}
