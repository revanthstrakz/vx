package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Region.Op;
import android.graphics.Shader.TileMode;
import android.support.p001v4.graphics.ColorUtils;
import android.util.Property;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.util.Themes;

public class PreviewBackground {
    private static final float ACCEPT_COLOR_MULTIPLIER = 1.5f;
    private static final float ACCEPT_SCALE_FACTOR = 1.2f;
    private static final int BG_OPACITY = 160;
    private static final int CONSUMPTION_ANIMATION_DURATION = 100;
    private static final int MAX_BG_OPACITY = 225;
    private static final Property<PreviewBackground, Integer> SHADOW_ALPHA = new Property<PreviewBackground, Integer>(Integer.class, "shadowAlpha") {
        public Integer get(PreviewBackground previewBackground) {
            return Integer.valueOf(previewBackground.mShadowAlpha);
        }

        public void set(PreviewBackground previewBackground, Integer num) {
            previewBackground.mShadowAlpha = num.intValue();
            previewBackground.invalidate();
        }
    };
    private static final int SHADOW_OPACITY = 40;
    private static final Property<PreviewBackground, Integer> STROKE_ALPHA = new Property<PreviewBackground, Integer>(Integer.class, "strokeAlpha") {
        public Integer get(PreviewBackground previewBackground) {
            return Integer.valueOf(previewBackground.mStrokeAlpha);
        }

        public void set(PreviewBackground previewBackground, Integer num) {
            previewBackground.mStrokeAlpha = num.intValue();
            previewBackground.invalidate();
        }
    };
    int basePreviewOffsetX;
    int basePreviewOffsetY;
    public int delegateCellX;
    public int delegateCellY;
    public boolean isClipping;
    private int mBgColor;
    private final PorterDuffXfermode mClipPorterDuffXfermode = new PorterDuffXfermode(Mode.DST_IN);
    private final RadialGradient mClipShader;
    /* access modifiers changed from: private */
    public float mColorMultiplier;
    private CellLayout mDrawingDelegate;
    private View mInvalidateDelegate;
    private final Paint mPaint;
    private final Path mPath;
    float mScale;
    /* access modifiers changed from: private */
    public ValueAnimator mScaleAnimator;
    private final Matrix mShaderMatrix;
    /* access modifiers changed from: private */
    public int mShadowAlpha;
    /* access modifiers changed from: private */
    public ObjectAnimator mShadowAnimator;
    private final PorterDuffXfermode mShadowPorterDuffXfermode;
    private RadialGradient mShadowShader;
    /* access modifiers changed from: private */
    public int mStrokeAlpha;
    /* access modifiers changed from: private */
    public ObjectAnimator mStrokeAlphaAnimator;
    private float mStrokeWidth;
    int previewSize;

    public PreviewBackground() {
        RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, 1.0f, new int[]{-16777216, -16777216, 0}, new float[]{0.0f, 0.999f, 1.0f}, TileMode.CLAMP);
        this.mClipShader = radialGradient;
        this.mShadowPorterDuffXfermode = new PorterDuffXfermode(Mode.DST_OUT);
        this.mShadowShader = null;
        this.mShaderMatrix = new Matrix();
        this.mPath = new Path();
        this.mPaint = new Paint(1);
        this.mScale = 1.0f;
        this.mColorMultiplier = 1.0f;
        this.mStrokeAlpha = MAX_BG_OPACITY;
        this.mShadowAlpha = 255;
        this.isClipping = true;
    }

    public void setup(Launcher launcher, View view, int i, int i2) {
        this.mInvalidateDelegate = view;
        this.mBgColor = Themes.getAttrColor(launcher, 16843827);
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        int i3 = deviceProfile.folderIconSizePx;
        int i4 = deviceProfile.folderIconPreviewPadding;
        this.previewSize = i3 - (i4 * 2);
        this.basePreviewOffsetX = (i - this.previewSize) / 2;
        this.basePreviewOffsetY = i4 + deviceProfile.folderBackgroundOffset + i2;
        this.mStrokeWidth = launcher.getResources().getDisplayMetrics().density;
        float scaledRadius = (float) getScaledRadius();
        RadialGradient radialGradient = new RadialGradient(0.0f, 0.0f, 1.0f, new int[]{Color.argb(40, 0, 0, 0), 0}, new float[]{scaledRadius / (this.mStrokeWidth + scaledRadius), 1.0f}, TileMode.CLAMP);
        this.mShadowShader = radialGradient;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public int getRadius() {
        return this.previewSize / 2;
    }

    /* access modifiers changed from: 0000 */
    public int getScaledRadius() {
        return (int) (this.mScale * ((float) getRadius()));
    }

    /* access modifiers changed from: 0000 */
    public int getOffsetX() {
        return this.basePreviewOffsetX - (getScaledRadius() - getRadius());
    }

    /* access modifiers changed from: 0000 */
    public int getOffsetY() {
        return this.basePreviewOffsetY - (getScaledRadius() - getRadius());
    }

    /* access modifiers changed from: 0000 */
    public float getScaleProgress() {
        return (this.mScale - 1.0f) / 0.20000005f;
    }

    /* access modifiers changed from: 0000 */
    public void invalidate() {
        if (this.mInvalidateDelegate != null) {
            this.mInvalidateDelegate.invalidate();
        }
        if (this.mDrawingDelegate != null) {
            this.mDrawingDelegate.invalidate();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setInvalidateDelegate(View view) {
        this.mInvalidateDelegate = view;
        invalidate();
    }

    public int getBgColor() {
        return ColorUtils.setAlphaComponent(this.mBgColor, (int) Math.min(225.0f, this.mColorMultiplier * 160.0f));
    }

    public void drawBackground(Canvas canvas) {
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(getBgColor());
        drawCircle(canvas, 0.0f);
        drawShadow(canvas);
    }

    public void drawShadow(Canvas canvas) {
        int i;
        if (this.mShadowShader != null) {
            float scaledRadius = (float) getScaledRadius();
            float f = this.mStrokeWidth + scaledRadius;
            this.mPaint.setStyle(Style.FILL);
            this.mPaint.setColor(-16777216);
            int offsetX = getOffsetX();
            int offsetY = getOffsetY();
            if (canvas.isHardwareAccelerated()) {
                float f2 = (float) offsetX;
                float f3 = (float) offsetY;
                i = canvas.saveLayer(f2 - this.mStrokeWidth, f3, f2 + scaledRadius + f, f3 + f + f, null, 20);
            } else {
                i = canvas.save(2);
                canvas.clipPath(getClipPath(), Op.DIFFERENCE);
            }
            this.mShaderMatrix.setScale(f, f);
            float f4 = ((float) offsetX) + scaledRadius;
            float f5 = (float) offsetY;
            this.mShaderMatrix.postTranslate(f4, f + f5);
            this.mShadowShader.setLocalMatrix(this.mShaderMatrix);
            this.mPaint.setAlpha(this.mShadowAlpha);
            this.mPaint.setShader(this.mShadowShader);
            canvas.drawPaint(this.mPaint);
            this.mPaint.setAlpha(255);
            this.mPaint.setShader(null);
            if (canvas.isHardwareAccelerated()) {
                this.mPaint.setXfermode(this.mShadowPorterDuffXfermode);
                canvas.drawCircle(f4, f5 + scaledRadius, scaledRadius, this.mPaint);
                this.mPaint.setXfermode(null);
            }
            canvas.restoreToCount(i);
        }
    }

    public void fadeInBackgroundShadow() {
        if (this.mShadowAnimator != null) {
            this.mShadowAnimator.cancel();
        }
        this.mShadowAnimator = ObjectAnimator.ofInt(this, SHADOW_ALPHA, new int[]{0, 255}).setDuration(100);
        this.mShadowAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PreviewBackground.this.mShadowAnimator = null;
            }
        });
        this.mShadowAnimator.start();
    }

    public void animateBackgroundStroke() {
        if (this.mStrokeAlphaAnimator != null) {
            this.mStrokeAlphaAnimator.cancel();
        }
        this.mStrokeAlphaAnimator = ObjectAnimator.ofInt(this, STROKE_ALPHA, new int[]{112, MAX_BG_OPACITY}).setDuration(100);
        this.mStrokeAlphaAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PreviewBackground.this.mStrokeAlphaAnimator = null;
            }
        });
        this.mStrokeAlphaAnimator.start();
    }

    public void drawBackgroundStroke(Canvas canvas) {
        this.mPaint.setColor(ColorUtils.setAlphaComponent(this.mBgColor, this.mStrokeAlpha));
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        drawCircle(canvas, 1.0f);
    }

    public void drawLeaveBehind(Canvas canvas) {
        float f = this.mScale;
        this.mScale = 0.5f;
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(Color.argb(BG_OPACITY, 245, 245, 245));
        drawCircle(canvas, 0.0f);
        this.mScale = f;
    }

    private void drawCircle(Canvas canvas, float f) {
        float scaledRadius = (float) getScaledRadius();
        canvas.drawCircle(((float) getOffsetX()) + scaledRadius, ((float) getOffsetY()) + scaledRadius, scaledRadius - f, this.mPaint);
    }

    public Path getClipPath() {
        this.mPath.reset();
        float scaledRadius = (float) getScaledRadius();
        this.mPath.addCircle(((float) getOffsetX()) + scaledRadius, ((float) getOffsetY()) + scaledRadius, scaledRadius, Direction.CW);
        return this.mPath;
    }

    /* access modifiers changed from: 0000 */
    public void clipCanvasHardware(Canvas canvas) {
        this.mPaint.setColor(-16777216);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setXfermode(this.mClipPorterDuffXfermode);
        float scaledRadius = (float) getScaledRadius();
        this.mShaderMatrix.setScale(scaledRadius, scaledRadius);
        this.mShaderMatrix.postTranslate(((float) getOffsetX()) + scaledRadius, scaledRadius + ((float) getOffsetY()));
        this.mClipShader.setLocalMatrix(this.mShaderMatrix);
        this.mPaint.setShader(this.mClipShader);
        canvas.drawPaint(this.mPaint);
        this.mPaint.setXfermode(null);
        this.mPaint.setShader(null);
    }

    /* access modifiers changed from: private */
    public void delegateDrawing(CellLayout cellLayout, int i, int i2) {
        if (this.mDrawingDelegate != cellLayout) {
            cellLayout.addFolderBackground(this);
        }
        this.mDrawingDelegate = cellLayout;
        this.delegateCellX = i;
        this.delegateCellY = i2;
        invalidate();
    }

    /* access modifiers changed from: private */
    public void clearDrawingDelegate() {
        if (this.mDrawingDelegate != null) {
            this.mDrawingDelegate.removeFolderBackground(this);
        }
        this.mDrawingDelegate = null;
        this.isClipping = true;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public boolean drawingDelegated() {
        return this.mDrawingDelegate != null;
    }

    private void animateScale(float f, float f2, final Runnable runnable, final Runnable runnable2) {
        final float f3 = this.mScale;
        final float f4 = this.mColorMultiplier;
        if (this.mScaleAnimator != null) {
            this.mScaleAnimator.cancel();
        }
        this.mScaleAnimator = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        ValueAnimator valueAnimator = this.mScaleAnimator;
        final float f5 = f;
        final float f6 = f2;
        C07435 r0 = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                float f = 1.0f - animatedFraction;
                PreviewBackground.this.mScale = (f5 * animatedFraction) + (f3 * f);
                PreviewBackground.this.mColorMultiplier = (animatedFraction * f6) + (f * f4);
                PreviewBackground.this.invalidate();
            }
        };
        valueAnimator.addUpdateListener(r0);
        this.mScaleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                if (runnable != null) {
                    runnable.run();
                }
            }

            public void onAnimationEnd(Animator animator) {
                if (runnable2 != null) {
                    runnable2.run();
                }
                PreviewBackground.this.mScaleAnimator = null;
            }
        });
        this.mScaleAnimator.setDuration(100);
        this.mScaleAnimator.start();
    }

    public void animateToAccept(final CellLayout cellLayout, final int i, final int i2) {
        animateScale(ACCEPT_SCALE_FACTOR, ACCEPT_COLOR_MULTIPLIER, new Runnable() {
            public void run() {
                PreviewBackground.this.delegateDrawing(cellLayout, i, i2);
            }
        }, null);
    }

    public void animateToRest() {
        final CellLayout cellLayout = this.mDrawingDelegate;
        final int i = this.delegateCellX;
        final int i2 = this.delegateCellY;
        animateScale(1.0f, 1.0f, new Runnable() {
            public void run() {
                PreviewBackground.this.delegateDrawing(cellLayout, i, i2);
            }
        }, new Runnable() {
            public void run() {
                PreviewBackground.this.clearDrawingDelegate();
            }
        });
    }

    public int getBackgroundAlpha() {
        return (int) Math.min(225.0f, this.mColorMultiplier * 160.0f);
    }

    public float getStrokeWidth() {
        return this.mStrokeWidth;
    }
}
