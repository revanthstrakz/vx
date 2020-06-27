package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.p001v4.graphics.ColorUtils;
import com.android.launcher3.LauncherAppState;

public class ShadowGenerator {
    private static final int AMBIENT_SHADOW_ALPHA = 30;
    public static final float BLUR_FACTOR = 0.010416667f;
    private static final float HALF_DISTANCE = 0.5f;
    private static final int KEY_SHADOW_ALPHA = 61;
    public static final float KEY_SHADOW_DISTANCE = 0.020833334f;
    private static final Object LOCK = new Object();
    private static ShadowGenerator sShadowGenerator;
    private final Paint mBlurPaint = new Paint(3);
    private final Canvas mCanvas = new Canvas();
    private final BlurMaskFilter mDefaultBlurMaskFilter = new BlurMaskFilter(((float) this.mIconSize) * 0.010416667f, Blur.NORMAL);
    private final Paint mDrawPaint = new Paint(3);
    private final int mIconSize;

    public static class Builder {
        public int ambientShadowAlpha = 30;
        public final RectF bounds = new RectF();
        public final int color;
        public int keyShadowAlpha = 61;
        public float keyShadowDistance;
        public float radius;
        public float shadowBlur;

        public Builder(int i) {
            this.color = i;
        }

        public Builder setupBlurForSize(int i) {
            float f = ((float) i) * 1.0f;
            this.shadowBlur = f / 32.0f;
            this.keyShadowDistance = f / 16.0f;
            return this;
        }

        public Bitmap createPill(int i, int i2) {
            int i3 = i2 / 2;
            this.radius = (float) i3;
            int i4 = i / 2;
            int max = Math.max(Math.round(((float) i4) + this.shadowBlur), Math.round(this.radius + this.shadowBlur + this.keyShadowDistance));
            this.bounds.set(0.0f, 0.0f, (float) i, (float) i2);
            this.bounds.offsetTo((float) (max - i4), (float) (max - i3));
            int i5 = max * 2;
            Bitmap createBitmap = Bitmap.createBitmap(i5, i5, Config.ARGB_8888);
            drawShadow(new Canvas(createBitmap));
            return createBitmap;
        }

        public void drawShadow(Canvas canvas) {
            Paint paint = new Paint(3);
            paint.setColor(this.color);
            paint.setShadowLayer(this.shadowBlur, 0.0f, this.keyShadowDistance, ColorUtils.setAlphaComponent(-16777216, this.keyShadowAlpha));
            canvas.drawRoundRect(this.bounds, this.radius, this.radius, paint);
            paint.setShadowLayer(this.shadowBlur, 0.0f, 0.0f, ColorUtils.setAlphaComponent(-16777216, this.ambientShadowAlpha));
            canvas.drawRoundRect(this.bounds, this.radius, this.radius, paint);
        }
    }

    private ShadowGenerator(Context context) {
        this.mIconSize = LauncherAppState.getIDP(context).iconBitmapSize;
    }

    public synchronized Bitmap recreateIcon(Bitmap bitmap) {
        return recreateIcon(bitmap, true, this.mDefaultBlurMaskFilter, 30, 61);
    }

    public synchronized Bitmap recreateIcon(Bitmap bitmap, boolean z, BlurMaskFilter blurMaskFilter, int i, int i2) {
        int i3;
        Bitmap createBitmap;
        if (z) {
            try {
                i3 = this.mIconSize;
            } catch (Throwable th) {
                throw th;
            }
        } else {
            i3 = bitmap.getWidth();
        }
        int height = z ? this.mIconSize : bitmap.getHeight();
        int[] iArr = new int[2];
        this.mBlurPaint.setMaskFilter(blurMaskFilter);
        Bitmap extractAlpha = bitmap.extractAlpha(this.mBlurPaint, iArr);
        createBitmap = Bitmap.createBitmap(i3, height, Config.ARGB_8888);
        this.mCanvas.setBitmap(createBitmap);
        this.mDrawPaint.setAlpha(i);
        this.mCanvas.drawBitmap(extractAlpha, (float) iArr[0], (float) iArr[1], this.mDrawPaint);
        this.mDrawPaint.setAlpha(i2);
        this.mCanvas.drawBitmap(extractAlpha, (float) iArr[0], ((float) iArr[1]) + (((float) this.mIconSize) * 0.020833334f), this.mDrawPaint);
        this.mDrawPaint.setAlpha(255);
        this.mCanvas.drawBitmap(bitmap, 0.0f, 0.0f, this.mDrawPaint);
        this.mCanvas.setBitmap(null);
        return createBitmap;
    }

    public static ShadowGenerator getInstance(Context context) {
        synchronized (LOCK) {
            if (sShadowGenerator == null) {
                sShadowGenerator = new ShadowGenerator(context);
            }
        }
        return sShadowGenerator;
    }

    public static float getScaleForBounds(RectF rectF) {
        float min = Math.min(Math.min(rectF.left, rectF.right), rectF.top);
        float f = min < 0.010416667f ? 0.48958334f / (0.5f - min) : 1.0f;
        return rectF.bottom < 0.03125f ? Math.min(f, 0.46875f / (0.5f - rectF.bottom)) : f;
    }
}
