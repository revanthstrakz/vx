package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.util.SparseArray;
import com.android.launcher3.graphics.IconPalette;

public class FastBitmapDrawable extends Drawable {
    private static final Property<FastBitmapDrawable, Float> BRIGHTNESS = new Property<FastBitmapDrawable, Float>(Float.TYPE, "brightness") {
        public Float get(FastBitmapDrawable fastBitmapDrawable) {
            return Float.valueOf(fastBitmapDrawable.getBrightness());
        }

        public void set(FastBitmapDrawable fastBitmapDrawable, Float f) {
            fastBitmapDrawable.setBrightness(f.floatValue());
        }
    };
    public static final int CLICK_FEEDBACK_DURATION = 2000;
    public static final TimeInterpolator CLICK_FEEDBACK_INTERPOLATOR = new TimeInterpolator() {
        public float getInterpolation(float f) {
            if (f < 0.05f) {
                return f / 0.05f;
            }
            if (f < 0.3f) {
                return 1.0f;
            }
            return (1.0f - f) / 0.7f;
        }
    };
    private static final float DISABLED_BRIGHTNESS = 0.5f;
    private static final float DISABLED_DESATURATION = 1.0f;
    private static final float PRESSED_BRIGHTNESS = 0.39215687f;
    private static final int REDUCED_FILTER_VALUE_SPACE = 48;
    private static final SparseArray<ColorFilter> sCachedFilter = new SparseArray<>();
    private static final ColorMatrix sTempBrightnessMatrix = new ColorMatrix();
    private static final ColorMatrix sTempFilterMatrix = new ColorMatrix();
    private int mAlpha = 255;
    private final Bitmap mBitmap;
    private int mBrightness = 0;
    private ObjectAnimator mBrightnessAnimator;
    private int mDesaturation = 0;
    private IconPalette mIconPalette;
    private boolean mIsDisabled;
    private boolean mIsPressed;
    protected final Paint mPaint = new Paint(3);
    private int mPrevUpdateKey = Integer.MAX_VALUE;

    public int getOpacity() {
        return -3;
    }

    public boolean isStateful() {
        return true;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public FastBitmapDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;
        setFilterBitmap(true);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, null, getBounds(), this.mPaint);
    }

    public IconPalette getIconPalette() {
        if (this.mIconPalette == null) {
            this.mIconPalette = IconPalette.fromDominantColor(Utilities.findDominantColorByHue(this.mBitmap, 20), true);
        }
        return this.mIconPalette;
    }

    public void setAlpha(int i) {
        this.mAlpha = i;
        this.mPaint.setAlpha(i);
    }

    public void setFilterBitmap(boolean z) {
        this.mPaint.setFilterBitmap(z);
        this.mPaint.setAntiAlias(z);
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public int getIntrinsicWidth() {
        return this.mBitmap.getWidth();
    }

    public int getIntrinsicHeight() {
        return this.mBitmap.getHeight();
    }

    public int getMinimumWidth() {
        return getBounds().width();
    }

    public int getMinimumHeight() {
        return getBounds().height();
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] iArr) {
        boolean z;
        int length = iArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            } else if (iArr[i] == 16842919) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        if (this.mIsPressed == z) {
            return false;
        }
        this.mIsPressed = z;
        if (this.mBrightnessAnimator != null) {
            this.mBrightnessAnimator.cancel();
        }
        if (this.mIsPressed) {
            this.mBrightnessAnimator = ObjectAnimator.ofFloat(this, BRIGHTNESS, new float[]{getExpectedBrightness()});
            this.mBrightnessAnimator.setDuration(2000);
            this.mBrightnessAnimator.setInterpolator(CLICK_FEEDBACK_INTERPOLATOR);
            this.mBrightnessAnimator.start();
        } else {
            setBrightness(getExpectedBrightness());
        }
        return true;
    }

    private void invalidateDesaturationAndBrightness() {
        setDesaturation(this.mIsDisabled ? 1.0f : 0.0f);
        setBrightness(getExpectedBrightness());
    }

    private float getExpectedBrightness() {
        if (this.mIsDisabled) {
            return 0.5f;
        }
        if (this.mIsPressed) {
            return PRESSED_BRIGHTNESS;
        }
        return 0.0f;
    }

    public void setIsDisabled(boolean z) {
        if (this.mIsDisabled != z) {
            this.mIsDisabled = z;
            invalidateDesaturationAndBrightness();
        }
    }

    private void setDesaturation(float f) {
        int floor = (int) Math.floor((double) (f * 48.0f));
        if (this.mDesaturation != floor) {
            this.mDesaturation = floor;
            updateFilter();
        }
    }

    public float getDesaturation() {
        return ((float) this.mDesaturation) / 48.0f;
    }

    /* access modifiers changed from: private */
    public void setBrightness(float f) {
        int floor = (int) Math.floor((double) (f * 48.0f));
        if (this.mBrightness != floor) {
            this.mBrightness = floor;
            updateFilter();
        }
    }

    /* access modifiers changed from: private */
    public float getBrightness() {
        return ((float) this.mBrightness) / 48.0f;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0020 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0021  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateFilter() {
        /*
            r7 = this;
            int r0 = r7.mDesaturation
            r1 = -1
            r2 = 0
            if (r0 <= 0) goto L_0x000f
            int r0 = r7.mDesaturation
            int r0 = r0 << 16
            int r3 = r7.mBrightness
            r0 = r0 | r3
        L_0x000d:
            r3 = 0
            goto L_0x001c
        L_0x000f:
            int r0 = r7.mBrightness
            if (r0 <= 0) goto L_0x001a
            r0 = 65536(0x10000, float:9.18355E-41)
            int r3 = r7.mBrightness
            r0 = r0 | r3
            r3 = 1
            goto L_0x001c
        L_0x001a:
            r0 = -1
            goto L_0x000d
        L_0x001c:
            int r4 = r7.mPrevUpdateKey
            if (r0 != r4) goto L_0x0021
            return
        L_0x0021:
            r7.mPrevUpdateKey = r0
            if (r0 == r1) goto L_0x008e
            android.util.SparseArray<android.graphics.ColorFilter> r1 = sCachedFilter
            java.lang.Object r1 = r1.get(r0)
            android.graphics.ColorFilter r1 = (android.graphics.ColorFilter) r1
            if (r1 != 0) goto L_0x0088
            float r1 = r7.getBrightness()
            r4 = 1132396544(0x437f0000, float:255.0)
            float r4 = r4 * r1
            int r4 = (int) r4
            if (r3 == 0) goto L_0x0048
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            r2 = 255(0xff, float:3.57E-43)
            int r2 = android.graphics.Color.argb(r4, r2, r2, r2)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.SRC_ATOP
            r1.<init>(r2, r3)
            goto L_0x0083
        L_0x0048:
            float r3 = r7.getDesaturation()
            r5 = 1065353216(0x3f800000, float:1.0)
            float r3 = r5 - r3
            android.graphics.ColorMatrix r6 = sTempFilterMatrix
            r6.setSaturation(r3)
            int r3 = r7.mBrightness
            if (r3 <= 0) goto L_0x007c
            float r5 = r5 - r1
            android.graphics.ColorMatrix r1 = sTempBrightnessMatrix
            float[] r1 = r1.getArray()
            r1[r2] = r5
            r2 = 6
            r1[r2] = r5
            r2 = 12
            r1[r2] = r5
            r2 = 4
            float r3 = (float) r4
            r1[r2] = r3
            r2 = 9
            r1[r2] = r3
            r2 = 14
            r1[r2] = r3
            android.graphics.ColorMatrix r1 = sTempFilterMatrix
            android.graphics.ColorMatrix r2 = sTempBrightnessMatrix
            r1.preConcat(r2)
        L_0x007c:
            android.graphics.ColorMatrixColorFilter r1 = new android.graphics.ColorMatrixColorFilter
            android.graphics.ColorMatrix r2 = sTempFilterMatrix
            r1.<init>(r2)
        L_0x0083:
            android.util.SparseArray<android.graphics.ColorFilter> r2 = sCachedFilter
            r2.append(r0, r1)
        L_0x0088:
            android.graphics.Paint r0 = r7.mPaint
            r0.setColorFilter(r1)
            goto L_0x0094
        L_0x008e:
            android.graphics.Paint r0 = r7.mPaint
            r1 = 0
            r0.setColorFilter(r1)
        L_0x0094:
            r7.invalidateSelf()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FastBitmapDrawable.updateFilter():void");
    }
}
