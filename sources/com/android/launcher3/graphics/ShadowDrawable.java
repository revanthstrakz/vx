package com.android.launcher3.graphics;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@TargetApi(26)
public class ShadowDrawable extends Drawable {
    private final Paint mPaint;
    private final ShadowDrawableState mState;

    private static class ShadowDrawableState extends ConstantState {
        int mChangingConfigurations;
        ConstantState mChildState;
        int mDarkTintColor;
        int mIntrinsicHeight;
        int mIntrinsicWidth;
        boolean mIsDark;
        Bitmap mLastDrawnBitmap;
        int mShadowColor;
        int mShadowSize;

        public boolean canApplyTheme() {
            return true;
        }

        private ShadowDrawableState() {
        }

        public Drawable newDrawable() {
            return new ShadowDrawable(this);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    public int getOpacity() {
        return -3;
    }

    public ShadowDrawable() {
        this(new ShadowDrawableState());
    }

    private ShadowDrawable(ShadowDrawableState shadowDrawableState) {
        this.mPaint = new Paint(3);
        this.mState = shadowDrawableState;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (!bounds.isEmpty()) {
            if (this.mState.mLastDrawnBitmap == null) {
                regenerateBitmapCache();
            }
            canvas.drawBitmap(this.mState.mLastDrawnBitmap, null, bounds, this.mPaint);
        }
    }

    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public ConstantState getConstantState() {
        return this.mState;
    }

    public int getIntrinsicHeight() {
        return this.mState.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mState.mIntrinsicWidth;
    }

    public boolean canApplyTheme() {
        return this.mState.canApplyTheme();
    }

    public void applyTheme(Theme theme) {
        TypedArray obtainStyledAttributes = theme.obtainStyledAttributes(new int[]{C0622R.attr.isWorkspaceDarkText});
        boolean z = obtainStyledAttributes.getBoolean(0, false);
        obtainStyledAttributes.recycle();
        if (this.mState.mIsDark != z) {
            this.mState.mIsDark = z;
            this.mState.mLastDrawnBitmap = null;
            invalidateSelf();
        }
    }

    private void regenerateBitmapCache() {
        Bitmap createBitmap = Bitmap.createBitmap(this.mState.mIntrinsicWidth, this.mState.mIntrinsicHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Drawable mutate = this.mState.mChildState.newDrawable().mutate();
        mutate.setBounds(this.mState.mShadowSize, this.mState.mShadowSize, this.mState.mIntrinsicWidth - this.mState.mShadowSize, this.mState.mIntrinsicHeight - this.mState.mShadowSize);
        mutate.setTint(this.mState.mIsDark ? this.mState.mDarkTintColor : -1);
        mutate.draw(canvas);
        if (!this.mState.mIsDark) {
            Paint paint = new Paint(3);
            paint.setMaskFilter(new BlurMaskFilter((float) this.mState.mShadowSize, Blur.NORMAL));
            int[] iArr = new int[2];
            Bitmap extractAlpha = createBitmap.extractAlpha(paint, iArr);
            paint.setMaskFilter(null);
            paint.setColor(this.mState.mShadowColor);
            createBitmap.eraseColor(0);
            canvas.drawBitmap(extractAlpha, (float) iArr[0], (float) iArr[1], paint);
            mutate.draw(canvas);
        }
        if (Utilities.ATLEAST_OREO) {
            createBitmap = createBitmap.copy(Config.HARDWARE, false);
        }
        this.mState.mLastDrawnBitmap = createBitmap;
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Theme theme) throws XmlPullParserException, IOException {
        TypedArray typedArray;
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        if (theme == null) {
            typedArray = resources.obtainAttributes(attributeSet, C0622R.styleable.ShadowDrawable);
        } else {
            typedArray = theme.obtainStyledAttributes(attributeSet, C0622R.styleable.ShadowDrawable, 0, 0);
        }
        try {
            Drawable drawable = typedArray.getDrawable(C0622R.styleable.ShadowDrawable_android_src);
            if (drawable != null) {
                this.mState.mShadowColor = typedArray.getColor(C0622R.styleable.ShadowDrawable_android_shadowColor, -16777216);
                this.mState.mShadowSize = typedArray.getDimensionPixelSize(C0622R.styleable.ShadowDrawable_android_elevation, 0);
                this.mState.mDarkTintColor = typedArray.getColor(C0622R.styleable.ShadowDrawable_darkTintColor, -16777216);
                this.mState.mIntrinsicHeight = drawable.getIntrinsicHeight() + (this.mState.mShadowSize * 2);
                this.mState.mIntrinsicWidth = drawable.getIntrinsicWidth() + (this.mState.mShadowSize * 2);
                this.mState.mChangingConfigurations = drawable.getChangingConfigurations();
                this.mState.mChildState = drawable.getConstantState();
                return;
            }
            throw new XmlPullParserException("missing src attribute");
        } finally {
            typedArray.recycle();
        }
    }
}
