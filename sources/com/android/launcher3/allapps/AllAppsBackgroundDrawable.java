package com.android.launcher3.allapps;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import com.android.launcher3.C0622R;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.util.Themes;

public class AllAppsBackgroundDrawable extends Drawable {
    private ObjectAnimator mBackgroundAnim;
    protected final TransformedImageDrawable mHand;
    private final int mHeight;
    protected final TransformedImageDrawable[] mIcons;
    private final int mWidth;

    protected static class TransformedImageDrawable {
        private int mAlpha;
        private int mGravity;
        private Drawable mImage;
        private float mXPercent;
        private float mYPercent;

        public TransformedImageDrawable(Context context, int i, float f, float f2, int i2) {
            this.mImage = context.getDrawable(i);
            this.mXPercent = f;
            this.mYPercent = f2;
            this.mGravity = i2;
        }

        public void setAlpha(int i) {
            this.mImage.setAlpha(i);
            this.mAlpha = i;
        }

        public int getAlpha() {
            return this.mAlpha;
        }

        public void updateBounds(Rect rect) {
            int intrinsicWidth = this.mImage.getIntrinsicWidth();
            int intrinsicHeight = this.mImage.getIntrinsicHeight();
            int width = rect.left + ((int) (this.mXPercent * ((float) rect.width())));
            int height = rect.top + ((int) (this.mYPercent * ((float) rect.height())));
            if ((this.mGravity & 1) == 1) {
                width -= intrinsicWidth / 2;
            }
            if ((this.mGravity & 16) == 16) {
                height -= intrinsicHeight / 2;
            }
            this.mImage.setBounds(width, height, intrinsicWidth + width, intrinsicHeight + height);
        }

        public void draw(Canvas canvas) {
            this.mImage.draw(canvas);
        }

        public Rect getBounds() {
            return this.mImage.getBounds();
        }
    }

    public int getOpacity() {
        return -3;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public AllAppsBackgroundDrawable(Context context) {
        Resources resources = context.getResources();
        this.mWidth = resources.getDimensionPixelSize(C0622R.dimen.all_apps_background_canvas_width);
        this.mHeight = resources.getDimensionPixelSize(C0622R.dimen.all_apps_background_canvas_height);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Themes.getAttrBoolean(context, C0622R.attr.isMainColorDark) ? C0622R.style.AllAppsEmptySearchBackground_Dark : C0622R.style.AllAppsEmptySearchBackground);
        TransformedImageDrawable transformedImageDrawable = new TransformedImageDrawable(contextThemeWrapper, C0622R.C0624drawable.ic_all_apps_bg_hand, 0.575f, 0.0f, 1);
        this.mHand = transformedImageDrawable;
        this.mIcons = new TransformedImageDrawable[4];
        TransformedImageDrawable[] transformedImageDrawableArr = this.mIcons;
        TransformedImageDrawable transformedImageDrawable2 = new TransformedImageDrawable(contextThemeWrapper, C0622R.C0624drawable.ic_all_apps_bg_icon_1, 0.375f, 0.0f, 1);
        transformedImageDrawableArr[0] = transformedImageDrawable2;
        TransformedImageDrawable[] transformedImageDrawableArr2 = this.mIcons;
        TransformedImageDrawable transformedImageDrawable3 = new TransformedImageDrawable(contextThemeWrapper, C0622R.C0624drawable.ic_all_apps_bg_icon_2, 0.3125f, 0.2f, 1);
        transformedImageDrawableArr2[1] = transformedImageDrawable3;
        TransformedImageDrawable[] transformedImageDrawableArr3 = this.mIcons;
        TransformedImageDrawable transformedImageDrawable4 = new TransformedImageDrawable(contextThemeWrapper, C0622R.C0624drawable.ic_all_apps_bg_icon_3, 0.475f, 0.26f, 1);
        transformedImageDrawableArr3[2] = transformedImageDrawable4;
        TransformedImageDrawable[] transformedImageDrawableArr4 = this.mIcons;
        TransformedImageDrawable transformedImageDrawable5 = new TransformedImageDrawable(contextThemeWrapper, C0622R.C0624drawable.ic_all_apps_bg_icon_4, 0.7f, 0.125f, 1);
        transformedImageDrawableArr4[3] = transformedImageDrawable5;
    }

    public void animateBgAlpha(float f, int i) {
        int i2 = (int) (f * 255.0f);
        if (getAlpha() != i2) {
            this.mBackgroundAnim = cancelAnimator(this.mBackgroundAnim);
            this.mBackgroundAnim = ObjectAnimator.ofInt(this, LauncherAnimUtils.DRAWABLE_ALPHA, new int[]{i2});
            this.mBackgroundAnim.setDuration((long) i);
            this.mBackgroundAnim.start();
        }
    }

    public void setBgAlpha(float f) {
        int i = (int) (f * 255.0f);
        if (getAlpha() != i) {
            this.mBackgroundAnim = cancelAnimator(this.mBackgroundAnim);
            setAlpha(i);
        }
    }

    public int getIntrinsicWidth() {
        return this.mWidth;
    }

    public int getIntrinsicHeight() {
        return this.mHeight;
    }

    public void draw(Canvas canvas) {
        this.mHand.draw(canvas);
        for (TransformedImageDrawable draw : this.mIcons) {
            draw.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mHand.updateBounds(rect);
        for (TransformedImageDrawable updateBounds : this.mIcons) {
            updateBounds.updateBounds(rect);
        }
        invalidateSelf();
    }

    public void setAlpha(int i) {
        this.mHand.setAlpha(i);
        for (TransformedImageDrawable alpha : this.mIcons) {
            alpha.setAlpha(i);
        }
        invalidateSelf();
    }

    public int getAlpha() {
        return this.mHand.getAlpha();
    }

    private ObjectAnimator cancelAnimator(ObjectAnimator objectAnimator) {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        return null;
    }
}
