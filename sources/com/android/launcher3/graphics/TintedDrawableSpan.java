package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import com.android.launcher3.Utilities;

public class TintedDrawableSpan extends DynamicDrawableSpan {
    private final Drawable mDrawable;
    private int mOldTint = 0;

    public TintedDrawableSpan(Context context, int i) {
        super(0);
        this.mDrawable = context.getDrawable(i);
        this.mDrawable.setTint(0);
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt == null) {
            fontMetricsInt = paint.getFontMetricsInt();
        }
        FontMetricsInt fontMetricsInt2 = fontMetricsInt;
        int i3 = fontMetricsInt2.bottom - fontMetricsInt2.top;
        if (Utilities.ATLEAST_NOUGAT) {
            this.mDrawable.setBounds(0, 0, i3, i3);
        } else {
            this.mDrawable.setBounds(0, -i3, i3, 0);
        }
        return super.getSize(paint, charSequence, i, i2, fontMetricsInt2);
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        int color = paint.getColor();
        if (this.mOldTint != color) {
            this.mOldTint = color;
            this.mDrawable.setTint(this.mOldTint);
        }
        super.draw(canvas, charSequence, i, i2, f, i3, i4, i5, paint);
    }

    public Drawable getDrawable() {
        return this.mDrawable;
    }
}
