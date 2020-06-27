package com.android.launcher3.badge;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import com.android.launcher3.C0622R;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.graphics.ShadowGenerator.Builder;

public class BadgeRenderer {
    private static final float CHAR_SIZE_PERCENTAGE = 0.12f;
    private static final boolean DOTS_ONLY = true;
    private static final float DOT_SCALE = 0.6f;
    private static final float OFFSET_PERCENTAGE = 0.02f;
    private static final float SIZE_PERCENTAGE = 0.38f;
    private static final float STACK_OFFSET_PERCENTAGE_X = 0.05f;
    private static final float STACK_OFFSET_PERCENTAGE_Y = 0.06f;
    private static final float TEXT_SIZE_PERCENTAGE = 0.26f;
    private final Paint mBackgroundPaint = new Paint(3);
    private final SparseArray<Bitmap> mBackgroundsWithShadow;
    private final int mCharSize;
    private final Context mContext;
    private final IconDrawer mLargeIconDrawer;
    private final int mOffset;
    /* access modifiers changed from: private */
    public final int mSize;
    private final IconDrawer mSmallIconDrawer;
    private final int mStackOffsetX;
    private final int mStackOffsetY;
    private final int mTextHeight;
    private final Paint mTextPaint = new Paint(1);

    private class IconDrawer {
        private final Bitmap mCircleClipBitmap;
        /* access modifiers changed from: private */
        public final int mPadding;
        private final Paint mPaint = new Paint(7);

        public IconDrawer(int i) {
            this.mPadding = i;
            this.mCircleClipBitmap = Bitmap.createBitmap(BadgeRenderer.this.mSize, BadgeRenderer.this.mSize, Config.ALPHA_8);
            Canvas canvas = new Canvas();
            canvas.setBitmap(this.mCircleClipBitmap);
            canvas.drawCircle((float) (BadgeRenderer.this.mSize / 2), (float) (BadgeRenderer.this.mSize / 2), (float) ((BadgeRenderer.this.mSize / 2) - i), this.mPaint);
        }

        public void drawIcon(Shader shader, Canvas canvas) {
            this.mPaint.setShader(shader);
            canvas.drawBitmap(this.mCircleClipBitmap, (float) ((-BadgeRenderer.this.mSize) / 2), (float) ((-BadgeRenderer.this.mSize) / 2), this.mPaint);
            this.mPaint.setShader(null);
        }
    }

    public BadgeRenderer(Context context, int i) {
        this.mContext = context;
        Resources resources = context.getResources();
        float f = (float) i;
        this.mSize = (int) (SIZE_PERCENTAGE * f);
        this.mCharSize = (int) (CHAR_SIZE_PERCENTAGE * f);
        this.mOffset = (int) (OFFSET_PERCENTAGE * f);
        this.mStackOffsetX = (int) (STACK_OFFSET_PERCENTAGE_X * f);
        this.mStackOffsetY = (int) (STACK_OFFSET_PERCENTAGE_Y * f);
        this.mTextPaint.setTextSize(f * TEXT_SIZE_PERCENTAGE);
        this.mTextPaint.setTextAlign(Align.CENTER);
        this.mLargeIconDrawer = new IconDrawer(resources.getDimensionPixelSize(C0622R.dimen.badge_small_padding));
        this.mSmallIconDrawer = new IconDrawer(resources.getDimensionPixelSize(C0622R.dimen.badge_large_padding));
        Rect rect = new Rect();
        this.mTextPaint.getTextBounds("0", 0, 1, rect);
        this.mTextHeight = rect.height();
        this.mBackgroundsWithShadow = new SparseArray<>(3);
    }

    public void draw(Canvas canvas, IconPalette iconPalette, @Nullable BadgeInfo badgeInfo, Rect rect, float f, Point point) {
        String str;
        this.mTextPaint.setColor(iconPalette.textColor);
        IconDrawer iconDrawer = (badgeInfo == null || !badgeInfo.isIconLarge()) ? this.mSmallIconDrawer : this.mLargeIconDrawer;
        if (badgeInfo != null) {
            badgeInfo.getNotificationIconForBadge(this.mContext, iconPalette.backgroundColor, this.mSize, iconDrawer.mPadding);
        }
        if (badgeInfo == null) {
            str = "0";
        } else {
            str = String.valueOf(badgeInfo.getNotificationCount());
        }
        int length = str.length();
        int i = this.mSize;
        Bitmap bitmap = (Bitmap) this.mBackgroundsWithShadow.get(length);
        if (bitmap == null) {
            bitmap = new Builder(-1).setupBlurForSize(this.mSize).createPill(i, this.mSize);
            this.mBackgroundsWithShadow.put(length, bitmap);
        }
        canvas.save(1);
        float f2 = f * DOT_SCALE;
        canvas.translate((float) ((rect.right - (i / 2)) + Math.min(this.mOffset, point.x)), (float) ((rect.top + (this.mSize / 2)) - Math.min(this.mOffset, point.y)));
        canvas.scale(f2, f2);
        this.mBackgroundPaint.setColorFilter(iconPalette.backgroundColorMatrixFilter);
        int height = bitmap.getHeight();
        this.mBackgroundPaint.setColorFilter(iconPalette.saturatedBackgroundColorMatrixFilter);
        float f3 = (float) ((-height) / 2);
        canvas.drawBitmap(bitmap, f3, f3, this.mBackgroundPaint);
        canvas.restore();
    }
}
