package com.android.launcher3.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.UiThread;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.C0622R;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsBackgroundDrawable;

public class DrawableFactory {
    private static final Object LOCK = new Object();
    private static final String TAG = "DrawableFactory";
    private static DrawableFactory sInstance;
    protected final UserHandle mMyUser = Process.myUserHandle();
    private Path mPreloadProgressPath;
    protected final ArrayMap<UserHandle, Bitmap> mUserBadges = new ArrayMap<>();

    public static DrawableFactory get(Context context) {
        DrawableFactory drawableFactory;
        synchronized (LOCK) {
            if (sInstance == null) {
                sInstance = (DrawableFactory) Utilities.getOverrideObject(DrawableFactory.class, context.getApplicationContext(), C0622R.string.drawable_factory_class);
            }
            drawableFactory = sInstance;
        }
        return drawableFactory;
    }

    public FastBitmapDrawable newIcon(Bitmap bitmap, ItemInfo itemInfo) {
        return new FastBitmapDrawable(bitmap);
    }

    public PreloadIconDrawable newPendingIcon(Bitmap bitmap, Context context) {
        if (this.mPreloadProgressPath == null) {
            this.mPreloadProgressPath = getPreloadProgressPath(context);
        }
        return new PreloadIconDrawable(bitmap, this.mPreloadProgressPath, context);
    }

    /* access modifiers changed from: protected */
    public Path getPreloadProgressPath(Context context) {
        if (Utilities.ATLEAST_OREO) {
            try {
                Drawable drawable = context.getDrawable(C0622R.C0624drawable.adaptive_icon_drawable_wrapper);
                drawable.setBounds(0, 0, 100, 100);
                return (Path) drawable.getClass().getMethod("getIconMask", new Class[0]).invoke(drawable, new Object[0]);
            } catch (Exception e) {
                Log.e(TAG, "Error loading mask icon", e);
            }
        }
        Path path = new Path();
        path.moveTo(50.0f, 0.0f);
        path.addArc(0.0f, 0.0f, 100.0f, 100.0f, -90.0f, 360.0f);
        return path;
    }

    public AllAppsBackgroundDrawable getAllAppsBackground(Context context) {
        return new AllAppsBackgroundDrawable(context);
    }

    @UiThread
    public Drawable getBadgeForUser(UserHandle userHandle, Context context) {
        if (this.mMyUser.equals(userHandle)) {
            return null;
        }
        Bitmap userBadge = getUserBadge(userHandle, context);
        FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable(userBadge);
        fastBitmapDrawable.setFilterBitmap(true);
        fastBitmapDrawable.setBounds(0, 0, userBadge.getWidth(), userBadge.getHeight());
        return fastBitmapDrawable;
    }

    /* access modifiers changed from: protected */
    public synchronized Bitmap getUserBadge(UserHandle userHandle, Context context) {
        Bitmap bitmap = (Bitmap) this.mUserBadges.get(userHandle);
        if (bitmap != null) {
            return bitmap;
        }
        Resources resources = context.getApplicationContext().getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(C0622R.dimen.profile_badge_size);
        Bitmap createBitmap = Bitmap.createBitmap(dimensionPixelSize, dimensionPixelSize, Config.ARGB_8888);
        Drawable userBadgedDrawableForDensity = context.getPackageManager().getUserBadgedDrawableForDensity(new BitmapDrawable(resources, createBitmap), userHandle, new Rect(0, 0, dimensionPixelSize, dimensionPixelSize), 0);
        if (userBadgedDrawableForDensity instanceof BitmapDrawable) {
            createBitmap = ((BitmapDrawable) userBadgedDrawableForDensity).getBitmap();
        } else {
            createBitmap.eraseColor(0);
            Canvas canvas = new Canvas(createBitmap);
            userBadgedDrawableForDensity.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
            userBadgedDrawableForDensity.draw(canvas);
            canvas.setBitmap(null);
        }
        this.mUserBadges.put(userHandle, createBitmap);
        return createBitmap;
    }
}
