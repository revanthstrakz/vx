package com.android.launcher3.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import java.nio.ByteBuffer;

public class HolographicOutlineHelper {
    private static HolographicOutlineHelper sInstance;
    private final SparseArray<Bitmap> mBitmapCache = new SparseArray<>(4);
    private final Paint mBlurPaint = new Paint(3);
    private final Canvas mCanvas = new Canvas();
    private final Paint mDrawPaint = new Paint(3);
    private final Paint mErasePaint = new Paint(3);
    private final BlurMaskFilter mMediumInnerBlurMaskFilter;
    private final BlurMaskFilter mMediumOuterBlurMaskFilter;
    private final float mShadowBitmapShift;
    private final BlurMaskFilter mShadowBlurMaskFilter;
    private final BlurMaskFilter mThinOuterBlurMaskFilter;

    private HolographicOutlineHelper(Context context) {
        Resources resources = context.getResources();
        float dimension = resources.getDimension(C0622R.dimen.blur_size_medium_outline);
        this.mMediumOuterBlurMaskFilter = new BlurMaskFilter(dimension, Blur.OUTER);
        this.mMediumInnerBlurMaskFilter = new BlurMaskFilter(dimension, Blur.NORMAL);
        this.mThinOuterBlurMaskFilter = new BlurMaskFilter(resources.getDimension(C0622R.dimen.blur_size_thin_outline), Blur.OUTER);
        this.mShadowBitmapShift = resources.getDimension(C0622R.dimen.blur_size_click_shadow);
        this.mShadowBlurMaskFilter = new BlurMaskFilter(this.mShadowBitmapShift, Blur.NORMAL);
        this.mErasePaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
    }

    public static HolographicOutlineHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new HolographicOutlineHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public void applyExpensiveOutlineWithBlur(Bitmap bitmap, Canvas canvas) {
        Bitmap bitmap2 = bitmap;
        Canvas canvas2 = canvas;
        byte[] bArr = new byte[(bitmap.getWidth() * bitmap.getHeight())];
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        wrap.rewind();
        bitmap2.copyPixelsToBuffer(wrap);
        for (int i = 0; i < bArr.length; i++) {
            if ((bArr[i] & 255) < 188) {
                bArr[i] = 0;
            }
        }
        wrap.rewind();
        bitmap2.copyPixelsFromBuffer(wrap);
        this.mBlurPaint.setMaskFilter(this.mMediumOuterBlurMaskFilter);
        int[] iArr = new int[2];
        Bitmap extractAlpha = bitmap2.extractAlpha(this.mBlurPaint, iArr);
        this.mBlurPaint.setMaskFilter(this.mThinOuterBlurMaskFilter);
        int[] iArr2 = new int[2];
        Bitmap extractAlpha2 = bitmap2.extractAlpha(this.mBlurPaint, iArr2);
        canvas2.setBitmap(bitmap2);
        canvas2.drawColor(-16777216, Mode.SRC_OUT);
        this.mBlurPaint.setMaskFilter(this.mMediumInnerBlurMaskFilter);
        int[] iArr3 = new int[2];
        Bitmap extractAlpha3 = bitmap2.extractAlpha(this.mBlurPaint, iArr3);
        canvas2.setBitmap(extractAlpha3);
        canvas2.drawBitmap(bitmap2, (float) (-iArr3[0]), (float) (-iArr3[1]), this.mErasePaint);
        canvas.drawRect(0.0f, 0.0f, (float) (-iArr3[0]), (float) extractAlpha3.getHeight(), this.mErasePaint);
        canvas.drawRect(0.0f, 0.0f, (float) extractAlpha3.getWidth(), (float) (-iArr3[1]), this.mErasePaint);
        canvas2.setBitmap(bitmap2);
        canvas2.drawColor(0, Mode.CLEAR);
        canvas2.drawBitmap(extractAlpha3, (float) iArr3[0], (float) iArr3[1], this.mDrawPaint);
        canvas2.drawBitmap(extractAlpha, (float) iArr[0], (float) iArr[1], this.mDrawPaint);
        canvas2.drawBitmap(extractAlpha2, (float) iArr2[0], (float) iArr2[1], this.mDrawPaint);
        canvas2.setBitmap(null);
        extractAlpha2.recycle();
        extractAlpha.recycle();
        extractAlpha3.recycle();
    }

    public Bitmap createMediumDropShadow(BubbleTextView bubbleTextView) {
        Drawable icon = bubbleTextView.getIcon();
        if (icon == null) {
            return null;
        }
        float scaleX = bubbleTextView.getScaleX();
        float scaleY = bubbleTextView.getScaleY();
        Rect bounds = icon.getBounds();
        int width = (int) (((float) bounds.width()) * scaleX);
        int height = (int) (((float) bounds.height()) * scaleY);
        if (height <= 0 || width <= 0) {
            return null;
        }
        int i = (width << 16) | height;
        Bitmap bitmap = (Bitmap) this.mBitmapCache.get(i);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Config.ALPHA_8);
            this.mCanvas.setBitmap(bitmap);
            this.mBitmapCache.put(i, bitmap);
        } else {
            this.mCanvas.setBitmap(bitmap);
            this.mCanvas.drawColor(-16777216, Mode.CLEAR);
        }
        int save = this.mCanvas.save();
        this.mCanvas.scale(scaleX, scaleY);
        this.mCanvas.translate((float) (-bounds.left), (float) (-bounds.top));
        icon.draw(this.mCanvas);
        this.mCanvas.restoreToCount(save);
        this.mCanvas.setBitmap(null);
        this.mBlurPaint.setMaskFilter(this.mShadowBlurMaskFilter);
        int i2 = (int) (this.mShadowBitmapShift * 2.0f);
        int i3 = width + i2;
        int i4 = height + i2;
        int i5 = (i3 << 16) | i4;
        Bitmap bitmap2 = (Bitmap) this.mBitmapCache.get(i5);
        if (bitmap2 == null) {
            bitmap2 = Bitmap.createBitmap(i3, i4, Config.ALPHA_8);
            this.mCanvas.setBitmap(bitmap2);
        } else {
            this.mBitmapCache.put(i5, null);
            this.mCanvas.setBitmap(bitmap2);
            this.mCanvas.drawColor(-16777216, Mode.CLEAR);
        }
        this.mCanvas.drawBitmap(bitmap, this.mShadowBitmapShift, this.mShadowBitmapShift, this.mBlurPaint);
        this.mCanvas.setBitmap(null);
        return bitmap2;
    }

    public void recycleShadowBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.mBitmapCache.put((bitmap.getWidth() << 16) | bitmap.getHeight(), bitmap);
        }
    }
}
