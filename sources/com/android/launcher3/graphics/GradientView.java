package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.support.p001v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.dynamicui.WallpaperColorInfo;
import com.android.launcher3.dynamicui.WallpaperColorInfo.OnChangeListener;
import com.android.launcher3.util.Themes;

public class GradientView extends View implements OnChangeListener {
    private static final int ALPHA_MASK_HEIGHT_DP = 500;
    private static final int ALPHA_MASK_WIDTH_DP = 2;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_COLOR = -1;
    private final Interpolator mAccelerator = new AccelerateInterpolator();
    private final int mAlphaColors;
    private final Bitmap mAlphaGradientMask;
    private final RectF mAlphaMaskRect = new RectF();
    private final float mAlphaStart;
    private int mColor1 = -1;
    private int mColor2 = -1;
    private final Paint mDebugPaint = null;
    private final RectF mFinalMaskRect = new RectF();
    private int mHeight;
    private final int mMaskHeight;
    private final int mMaskWidth;
    private final Paint mPaintNoScrim = new Paint();
    private final Paint mPaintWithScrim = new Paint();
    private float mProgress;
    private final int mScrimColor;
    private boolean mShiftScrim = false;
    private boolean mShowScrim = true;
    private final WallpaperColorInfo mWallpaperColorInfo;
    private int mWidth;

    public GradientView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.mMaskHeight = Utilities.pxFromDp(500.0f, displayMetrics);
        this.mMaskWidth = Utilities.pxFromDp(2.0f, displayMetrics);
        Launcher launcher = Launcher.getLauncher(context);
        this.mAlphaStart = launcher.getDeviceProfile().isVerticalBarLayout() ? 0.0f : 100.0f;
        this.mScrimColor = Themes.getAttrColor(context, C0622R.attr.allAppsScrimColor);
        this.mWallpaperColorInfo = WallpaperColorInfo.getInstance(launcher);
        this.mAlphaColors = getResources().getInteger(C0622R.integer.extracted_color_gradient_alpha);
        updateColors();
        this.mAlphaGradientMask = createDitheredAlphaMask();
    }

    public void setShiftScrim(boolean z) {
        this.mShiftScrim = z;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mWallpaperColorInfo.addOnChangeListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWallpaperColorInfo.removeOnChangeListener(this);
    }

    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        updateColors();
        invalidate();
    }

    private void updateColors() {
        this.mColor1 = ColorUtils.setAlphaComponent(this.mWallpaperColorInfo.getMainColor(), this.mAlphaColors);
        this.mColor2 = ColorUtils.setAlphaComponent(this.mWallpaperColorInfo.getSecondaryColor(), this.mAlphaColors);
        if (this.mWidth + this.mHeight > 0) {
            createRadialShader();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mWidth = getMeasuredWidth();
        this.mHeight = getMeasuredHeight();
        if (this.mWidth + this.mHeight > 0) {
            createRadialShader();
        }
    }

    private void createRadialShader() {
        float max = ((float) Math.max(this.mHeight, this.mWidth)) * 1.05f;
        float f = (max - ((float) this.mHeight)) / max;
        float f2 = max;
        RadialGradient radialGradient = new RadialGradient(((float) this.mWidth) * 0.5f, ((float) this.mHeight) * 1.05f, f2, new int[]{this.mColor1, this.mColor1, this.mColor2}, new float[]{0.0f, f, 1.0f}, TileMode.CLAMP);
        this.mPaintNoScrim.setShader(radialGradient);
        int compositeColors = ColorUtils.compositeColors(this.mScrimColor, this.mColor1);
        int compositeColors2 = ColorUtils.compositeColors(this.mScrimColor, this.mColor2);
        float f3 = ((float) this.mWidth) * 0.5f;
        float f4 = 1.05f * ((float) this.mHeight);
        int[] iArr = new int[3];
        iArr[0] = compositeColors;
        if (this.mShiftScrim) {
            compositeColors = compositeColors2;
        }
        iArr[1] = compositeColors;
        iArr[2] = compositeColors2;
        float f5 = f3;
        float f6 = f4;
        float f7 = max;
        RadialGradient radialGradient2 = new RadialGradient(f5, f6, f7, iArr, new float[]{0.0f, f, 1.0f}, TileMode.CLAMP);
        this.mPaintWithScrim.setShader(radialGradient2);
    }

    public void setProgress(float f) {
        setProgress(f, true);
    }

    public void setProgress(float f, boolean z) {
        this.mProgress = f;
        this.mShowScrim = z;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Paint paint = this.mShowScrim ? this.mPaintWithScrim : this.mPaintNoScrim;
        float f = (this.mProgress * (this.mShiftScrim ? 0.85f : 1.0f) * 0.71000004f) + 0.29f;
        float f2 = ((1.0f - f) * ((float) this.mHeight)) - (((float) this.mMaskHeight) * f);
        paint.setAlpha((int) (this.mAlphaStart + ((255.0f - this.mAlphaStart) * this.mAccelerator.getInterpolation(this.mProgress))));
        float floor = (float) Math.floor((double) (((float) this.mMaskHeight) + f2));
        this.mAlphaMaskRect.set(0.0f, f2, (float) this.mWidth, floor);
        this.mFinalMaskRect.set(0.0f, floor, (float) this.mWidth, (float) this.mHeight);
        canvas.drawBitmap(this.mAlphaGradientMask, null, this.mAlphaMaskRect, paint);
        canvas.drawRect(this.mFinalMaskRect, paint);
    }

    public Bitmap createDitheredAlphaMask() {
        Bitmap createBitmap = Bitmap.createBitmap(this.mMaskWidth, this.mMaskHeight, Config.ALPHA_8);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint(4);
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.mMaskHeight, new int[]{16777215, ColorUtils.setAlphaComponent(-1, 242), -1}, new float[]{0.0f, 0.8f, 1.0f}, TileMode.CLAMP);
        paint.setShader(linearGradient);
        canvas.drawRect(0.0f, 0.0f, (float) this.mMaskWidth, (float) this.mMaskHeight, paint);
        return createBitmap;
    }
}
