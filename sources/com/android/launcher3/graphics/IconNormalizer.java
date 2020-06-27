package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.p001v4.internal.view.SupportMenu;
import com.android.launcher3.LauncherAppState;
import java.io.File;
import java.util.Random;

public class IconNormalizer {
    private static final float BOUND_RATIO_MARGIN = 0.05f;
    private static final float CIRCLE_AREA_BY_RECT = 0.7853982f;
    private static final boolean DEBUG = false;
    private static final float LINEAR_SCALE_SLOPE = 0.040449437f;
    private static final Object LOCK = new Object();
    private static final float MAX_CIRCLE_AREA_FACTOR = 0.6597222f;
    private static final float MAX_SQUARE_AREA_FACTOR = 0.6510417f;
    private static final int MIN_VISIBLE_ALPHA = 40;
    private static final float PIXEL_DIFF_PERCENTAGE_THRESHOLD = 0.005f;
    private static final float SCALE_NOT_INITIALIZED = 0.0f;
    private static final String TAG = "IconNormalizer";
    private static IconNormalizer sIconNormalizer;
    private final Rect mAdaptiveIconBounds = new Rect();
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap = Bitmap.createBitmap(this.mMaxSize, this.mMaxSize, Config.ALPHA_8);
    private final Bitmap mBitmapARGB = Bitmap.createBitmap(this.mMaxSize, this.mMaxSize, Config.ARGB_8888);
    private final Rect mBounds = new Rect();
    private final Canvas mCanvas = new Canvas(this.mBitmap);
    private final Canvas mCanvasARGB = new Canvas(this.mBitmapARGB);
    private final File mDir;
    private int mFileId;
    private final float[] mLeftBorder = new float[this.mMaxSize];
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintIcon = new Paint();
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels = new byte[(this.mMaxSize * this.mMaxSize)];
    private final int[] mPixelsARGB = new int[(this.mMaxSize * this.mMaxSize)];
    private final Random mRandom;
    private final float[] mRightBorder = new float[this.mMaxSize];

    private IconNormalizer(Context context) {
        this.mMaxSize = LauncherAppState.getIDP(context).iconBitmapSize * 2;
        this.mPaintIcon.setColor(-1);
        this.mPaintMaskShape = new Paint();
        this.mPaintMaskShape.setColor(SupportMenu.CATEGORY_MASK);
        this.mPaintMaskShape.setStyle(Style.FILL);
        this.mPaintMaskShape.setXfermode(new PorterDuffXfermode(Mode.XOR));
        this.mPaintMaskShapeOutline = new Paint();
        this.mPaintMaskShapeOutline.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        this.mPaintMaskShapeOutline.setStyle(Style.STROKE);
        this.mPaintMaskShapeOutline.setColor(-16777216);
        this.mPaintMaskShapeOutline.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mDir = context.getExternalFilesDir(null);
        this.mRandom = new Random();
    }

    private boolean isShape(Path path) {
        if (Math.abs((((float) this.mBounds.width()) / ((float) this.mBounds.height())) - 1.0f) > BOUND_RATIO_MARGIN) {
            return false;
        }
        this.mFileId = this.mRandom.nextInt();
        this.mBitmapARGB.eraseColor(0);
        this.mCanvasARGB.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaintIcon);
        this.mMatrix.reset();
        this.mMatrix.setScale((float) this.mBounds.width(), (float) this.mBounds.height());
        this.mMatrix.postTranslate((float) this.mBounds.left, (float) this.mBounds.top);
        path.transform(this.mMatrix);
        this.mCanvasARGB.drawPath(path, this.mPaintMaskShape);
        this.mCanvasARGB.drawPath(path, this.mPaintMaskShapeOutline);
        if (!isTransparentBitmap(this.mBitmapARGB)) {
            return false;
        }
        return true;
    }

    private boolean isTransparentBitmap(Bitmap bitmap) {
        int width = this.mBounds.width();
        int height = this.mBounds.height();
        bitmap.getPixels(this.mPixelsARGB, 0, width, this.mBounds.left, this.mBounds.top, width, height);
        int i = 0;
        for (int i2 = 0; i2 < width * height; i2++) {
            if (Color.alpha(this.mPixelsARGB[i2]) > 40) {
                i++;
            }
        }
        if (((float) i) / ((float) (this.mBounds.width() * this.mBounds.height())) < PIXEL_DIFF_PERCENTAGE_THRESHOLD) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0181, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0183, code lost:
        return 1.0f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0084  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00ca  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized float getScale(@android.support.annotation.NonNull android.graphics.drawable.Drawable r18, @android.support.annotation.Nullable android.graphics.RectF r19, @android.support.annotation.Nullable android.graphics.Path r20, @android.support.annotation.Nullable boolean[] r21) {
        /*
            r17 = this;
            r1 = r17
            r0 = r18
            r2 = r19
            r3 = r21
            monitor-enter(r17)
            boolean r4 = com.android.launcher3.Utilities.ATLEAST_OREO     // Catch:{ all -> 0x0184 }
            r5 = 0
            if (r4 == 0) goto L_0x0023
            boolean r4 = r0 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch:{ all -> 0x0184 }
            if (r4 == 0) goto L_0x0023
            float r4 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x0184 }
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 == 0) goto L_0x0023
            if (r2 == 0) goto L_0x001f
            android.graphics.Rect r0 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x0184 }
            r2.set(r0)     // Catch:{ all -> 0x0184 }
        L_0x001f:
            float r0 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x0184 }
            monitor-exit(r17)
            return r0
        L_0x0023:
            int r4 = r18.getIntrinsicWidth()     // Catch:{ all -> 0x0184 }
            int r6 = r18.getIntrinsicHeight()     // Catch:{ all -> 0x0184 }
            if (r4 <= 0) goto L_0x0049
            if (r6 > 0) goto L_0x0030
            goto L_0x0049
        L_0x0030:
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            if (r4 > r7) goto L_0x0038
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            if (r6 <= r7) goto L_0x0059
        L_0x0038:
            int r7 = java.lang.Math.max(r4, r6)     // Catch:{ all -> 0x0184 }
            int r8 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            int r8 = r8 * r4
            int r4 = r8 / r7
            int r8 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            int r8 = r8 * r6
            int r6 = r8 / r7
            goto L_0x0059
        L_0x0049:
            if (r4 <= 0) goto L_0x004f
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            if (r4 <= r7) goto L_0x0051
        L_0x004f:
            int r4 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
        L_0x0051:
            if (r6 <= 0) goto L_0x0057
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            if (r6 <= r7) goto L_0x0059
        L_0x0057:
            int r6 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
        L_0x0059:
            android.graphics.Bitmap r7 = r1.mBitmap     // Catch:{ all -> 0x0184 }
            r8 = 0
            r7.eraseColor(r8)     // Catch:{ all -> 0x0184 }
            r0.setBounds(r8, r8, r4, r6)     // Catch:{ all -> 0x0184 }
            android.graphics.Canvas r7 = r1.mCanvas     // Catch:{ all -> 0x0184 }
            r0.draw(r7)     // Catch:{ all -> 0x0184 }
            byte[] r7 = r1.mPixels     // Catch:{ all -> 0x0184 }
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.wrap(r7)     // Catch:{ all -> 0x0184 }
            r7.rewind()     // Catch:{ all -> 0x0184 }
            android.graphics.Bitmap r9 = r1.mBitmap     // Catch:{ all -> 0x0184 }
            r9.copyPixelsToBuffer(r7)     // Catch:{ all -> 0x0184 }
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            r9 = 1
            int r7 = r7 + r9
            int r10 = r1.mMaxSize     // Catch:{ all -> 0x0184 }
            int r10 = r10 - r4
            r5 = r7
            r7 = 0
            r12 = -1
            r13 = -1
            r14 = -1
            r15 = 0
        L_0x0082:
            if (r7 >= r6) goto L_0x00c5
            r16 = r15
            r8 = -1
            r9 = -1
            r15 = 0
        L_0x0089:
            if (r15 >= r4) goto L_0x00a1
            byte[] r11 = r1.mPixels     // Catch:{ all -> 0x0184 }
            byte r11 = r11[r16]     // Catch:{ all -> 0x0184 }
            r11 = r11 & 255(0xff, float:3.57E-43)
            r0 = 40
            if (r11 <= r0) goto L_0x009a
            r0 = -1
            if (r8 != r0) goto L_0x0099
            r8 = r15
        L_0x0099:
            r9 = r15
        L_0x009a:
            int r16 = r16 + 1
            int r15 = r15 + 1
            r0 = r18
            goto L_0x0089
        L_0x00a1:
            int r15 = r16 + r10
            float[] r0 = r1.mLeftBorder     // Catch:{ all -> 0x0184 }
            float r11 = (float) r8     // Catch:{ all -> 0x0184 }
            r0[r7] = r11     // Catch:{ all -> 0x0184 }
            float[] r0 = r1.mRightBorder     // Catch:{ all -> 0x0184 }
            float r11 = (float) r9     // Catch:{ all -> 0x0184 }
            r0[r7] = r11     // Catch:{ all -> 0x0184 }
            r0 = -1
            if (r8 == r0) goto L_0x00be
            if (r12 != r0) goto L_0x00b3
            r12 = r7
        L_0x00b3:
            int r0 = java.lang.Math.min(r5, r8)     // Catch:{ all -> 0x0184 }
            int r5 = java.lang.Math.max(r13, r9)     // Catch:{ all -> 0x0184 }
            r13 = r5
            r14 = r7
            r5 = r0
        L_0x00be:
            int r7 = r7 + 1
            r0 = r18
            r8 = 0
            r9 = 1
            goto L_0x0082
        L_0x00c5:
            r0 = 1065353216(0x3f800000, float:1.0)
            r7 = -1
            if (r12 == r7) goto L_0x0182
            if (r13 != r7) goto L_0x00ce
            goto L_0x0182
        L_0x00ce:
            float[] r8 = r1.mLeftBorder     // Catch:{ all -> 0x0184 }
            r9 = 1
            convertToConvexArray(r8, r9, r12, r14)     // Catch:{ all -> 0x0184 }
            float[] r8 = r1.mRightBorder     // Catch:{ all -> 0x0184 }
            convertToConvexArray(r8, r7, r12, r14)     // Catch:{ all -> 0x0184 }
            r7 = 0
            r8 = 0
        L_0x00db:
            if (r7 >= r6) goto L_0x00f6
            float[] r9 = r1.mLeftBorder     // Catch:{ all -> 0x0184 }
            r9 = r9[r7]     // Catch:{ all -> 0x0184 }
            r10 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 > 0) goto L_0x00e8
            goto L_0x00f3
        L_0x00e8:
            float[] r9 = r1.mRightBorder     // Catch:{ all -> 0x0184 }
            r9 = r9[r7]     // Catch:{ all -> 0x0184 }
            float[] r10 = r1.mLeftBorder     // Catch:{ all -> 0x0184 }
            r10 = r10[r7]     // Catch:{ all -> 0x0184 }
            float r9 = r9 - r10
            float r9 = r9 + r0
            float r8 = r8 + r9
        L_0x00f3:
            int r7 = r7 + 1
            goto L_0x00db
        L_0x00f6:
            int r7 = r14 + 1
            int r7 = r7 - r12
            int r9 = r13 + 1
            int r9 = r9 - r5
            int r7 = r7 * r9
            float r7 = (float) r7     // Catch:{ all -> 0x0184 }
            float r7 = r8 / r7
            r9 = 1061752795(0x3f490fdb, float:0.7853982)
            int r9 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x010c
            r7 = 1059644302(0x3f28e38e, float:0.6597222)
            goto L_0x0117
        L_0x010c:
            r9 = 1059498667(0x3f26aaab, float:0.6510417)
            r10 = 1025879631(0x3d25ae4f, float:0.040449437)
            float r7 = r0 - r7
            float r7 = r7 * r10
            float r7 = r7 + r9
        L_0x0117:
            android.graphics.Rect r9 = r1.mBounds     // Catch:{ all -> 0x0184 }
            r9.left = r5     // Catch:{ all -> 0x0184 }
            android.graphics.Rect r5 = r1.mBounds     // Catch:{ all -> 0x0184 }
            r5.right = r13     // Catch:{ all -> 0x0184 }
            android.graphics.Rect r5 = r1.mBounds     // Catch:{ all -> 0x0184 }
            r5.top = r12     // Catch:{ all -> 0x0184 }
            android.graphics.Rect r5 = r1.mBounds     // Catch:{ all -> 0x0184 }
            r5.bottom = r14     // Catch:{ all -> 0x0184 }
            if (r2 == 0) goto L_0x0149
            android.graphics.Rect r5 = r1.mBounds     // Catch:{ all -> 0x0184 }
            int r5 = r5.left     // Catch:{ all -> 0x0184 }
            float r5 = (float) r5     // Catch:{ all -> 0x0184 }
            float r9 = (float) r4     // Catch:{ all -> 0x0184 }
            float r5 = r5 / r9
            android.graphics.Rect r10 = r1.mBounds     // Catch:{ all -> 0x0184 }
            int r10 = r10.top     // Catch:{ all -> 0x0184 }
            float r10 = (float) r10     // Catch:{ all -> 0x0184 }
            android.graphics.Rect r11 = r1.mBounds     // Catch:{ all -> 0x0184 }
            int r11 = r11.right     // Catch:{ all -> 0x0184 }
            float r11 = (float) r11     // Catch:{ all -> 0x0184 }
            float r11 = r11 / r9
            float r9 = r0 - r11
            android.graphics.Rect r11 = r1.mBounds     // Catch:{ all -> 0x0184 }
            int r11 = r11.bottom     // Catch:{ all -> 0x0184 }
            float r11 = (float) r11     // Catch:{ all -> 0x0184 }
            float r12 = (float) r6     // Catch:{ all -> 0x0184 }
            float r11 = r11 / r12
            float r11 = r0 - r11
            r2.set(r5, r10, r9, r11)     // Catch:{ all -> 0x0184 }
        L_0x0149:
            if (r3 == 0) goto L_0x0157
            int r2 = r3.length     // Catch:{ all -> 0x0184 }
            if (r2 <= 0) goto L_0x0157
            r2 = r20
            boolean r2 = r1.isShape(r2)     // Catch:{ all -> 0x0184 }
            r5 = 0
            r3[r5] = r2     // Catch:{ all -> 0x0184 }
        L_0x0157:
            int r4 = r4 * r6
            float r2 = (float) r4     // Catch:{ all -> 0x0184 }
            float r8 = r8 / r2
            int r2 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r2 <= 0) goto L_0x0166
            float r7 = r7 / r8
            double r2 = (double) r7     // Catch:{ all -> 0x0184 }
            double r2 = java.lang.Math.sqrt(r2)     // Catch:{ all -> 0x0184 }
            float r0 = (float) r2     // Catch:{ all -> 0x0184 }
        L_0x0166:
            boolean r2 = com.android.launcher3.Utilities.ATLEAST_OREO     // Catch:{ all -> 0x0184 }
            if (r2 == 0) goto L_0x0180
            r2 = r18
            boolean r2 = r2 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch:{ all -> 0x0184 }
            if (r2 == 0) goto L_0x0180
            float r2 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x0184 }
            r3 = 0
            int r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x0180
            r1.mAdaptiveIconScale = r0     // Catch:{ all -> 0x0184 }
            android.graphics.Rect r2 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x0184 }
            android.graphics.Rect r3 = r1.mBounds     // Catch:{ all -> 0x0184 }
            r2.set(r3)     // Catch:{ all -> 0x0184 }
        L_0x0180:
            monitor-exit(r17)
            return r0
        L_0x0182:
            monitor-exit(r17)
            return r0
        L_0x0184:
            r0 = move-exception
            monitor-exit(r17)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.IconNormalizer.getScale(android.graphics.drawable.Drawable, android.graphics.RectF, android.graphics.Path, boolean[]):float");
    }

    private static void convertToConvexArray(float[] fArr, int i, int i2, int i3) {
        float[] fArr2 = new float[(fArr.length - 1)];
        float f = Float.MAX_VALUE;
        int i4 = -1;
        for (int i5 = i2 + 1; i5 <= i3; i5++) {
            if (fArr[i5] > -1.0f) {
                if (f == Float.MAX_VALUE) {
                    i4 = i2;
                } else {
                    float f2 = ((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - f;
                    float f3 = (float) i;
                    if (f2 * f3 < 0.0f) {
                        while (i4 > i2) {
                            i4--;
                            if ((((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - fArr2[i4]) * f3 >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                f = (fArr[i5] - fArr[i4]) / ((float) (i5 - i4));
                for (int i6 = i4; i6 < i5; i6++) {
                    fArr2[i6] = f;
                    fArr[i6] = fArr[i4] + (((float) (i6 - i4)) * f);
                }
                i4 = i5;
            }
        }
    }

    public static IconNormalizer getInstance(Context context) {
        synchronized (LOCK) {
            if (sIconNormalizer == null) {
                sIconNormalizer = new IconNormalizer(context);
            }
        }
        return sIconNormalizer;
    }
}
