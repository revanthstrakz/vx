package com.android.launcher3.pageindicators;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import com.android.launcher3.C0622R;
import com.android.launcher3.util.Themes;

public class CaretDrawable extends Drawable {
    public static final float PROGRESS_CARET_NEUTRAL = 0.0f;
    public static final float PROGRESS_CARET_POINTING_DOWN = 1.0f;
    public static final float PROGRESS_CARET_POINTING_UP = -1.0f;
    private Paint mCaretPaint = new Paint();
    private float mCaretProgress = 0.0f;
    private final int mCaretSizePx;
    private Path mPath = new Path();
    private Paint mShadowPaint = new Paint();
    private final boolean mUseShadow;

    public int getOpacity() {
        return -3;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public CaretDrawable(Context context) {
        Resources resources = context.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(C0622R.dimen.all_apps_caret_stroke_width);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(C0622R.dimen.all_apps_caret_shadow_spread);
        this.mCaretPaint.setColor(Themes.getAttrColor(context, C0622R.attr.workspaceTextColor));
        this.mCaretPaint.setAntiAlias(true);
        this.mCaretPaint.setStrokeWidth((float) dimensionPixelSize);
        this.mCaretPaint.setStyle(Style.STROKE);
        this.mCaretPaint.setStrokeCap(Cap.ROUND);
        this.mCaretPaint.setStrokeJoin(Join.ROUND);
        this.mShadowPaint.setColor(resources.getColor(C0622R.color.default_shadow_color_no_alpha));
        this.mShadowPaint.setAlpha(Themes.getAlpha(context, 16843967));
        this.mShadowPaint.setAntiAlias(true);
        this.mShadowPaint.setStrokeWidth((float) (dimensionPixelSize + (dimensionPixelSize2 * 2)));
        this.mShadowPaint.setStyle(Style.STROKE);
        this.mShadowPaint.setStrokeCap(Cap.ROUND);
        this.mShadowPaint.setStrokeJoin(Join.ROUND);
        this.mUseShadow = !Themes.getAttrBoolean(context, C0622R.attr.isWorkspaceDarkText);
        this.mCaretSizePx = resources.getDimensionPixelSize(C0622R.dimen.all_apps_caret_size);
    }

    public int getIntrinsicHeight() {
        return this.mCaretSizePx;
    }

    public int getIntrinsicWidth() {
        return this.mCaretSizePx;
    }

    public void draw(Canvas canvas) {
        if (Float.compare((float) this.mCaretPaint.getAlpha(), 0.0f) != 0) {
            float width = ((float) getBounds().width()) - this.mShadowPaint.getStrokeWidth();
            float height = ((float) getBounds().height()) - this.mShadowPaint.getStrokeWidth();
            float strokeWidth = ((float) getBounds().left) + (this.mShadowPaint.getStrokeWidth() / 2.0f);
            float strokeWidth2 = ((float) getBounds().top) + (this.mShadowPaint.getStrokeWidth() / 2.0f);
            float f = height - ((height / 4.0f) * 2.0f);
            this.mPath.reset();
            this.mPath.moveTo(strokeWidth, ((1.0f - getNormalizedCaretProgress()) * f) + strokeWidth2);
            this.mPath.lineTo((width / 2.0f) + strokeWidth, (getNormalizedCaretProgress() * f) + strokeWidth2);
            this.mPath.lineTo(strokeWidth + width, strokeWidth2 + (f * (1.0f - getNormalizedCaretProgress())));
            if (this.mUseShadow) {
                canvas.drawPath(this.mPath, this.mShadowPaint);
            }
            canvas.drawPath(this.mPath, this.mCaretPaint);
        }
    }

    public void setCaretProgress(float f) {
        this.mCaretProgress = f;
        invalidateSelf();
    }

    public float getCaretProgress() {
        return this.mCaretProgress;
    }

    public float getNormalizedCaretProgress() {
        return (this.mCaretProgress - -1.0f) / 2.0f;
    }

    public void setAlpha(int i) {
        this.mCaretPaint.setAlpha(i);
        this.mShadowPaint.setAlpha(i);
        invalidateSelf();
    }
}
