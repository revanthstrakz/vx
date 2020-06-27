package com.android.launcher3.discovery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.C0622R;

public class RatingView extends View {
    private static final int MAX_LEVEL = 10000;
    private static final int MAX_STARS = 5;
    private static final float WIDTH_FACTOR = 0.9f;
    private final int mColorGray;
    private final int mColorHighlight;
    private final Drawable mStarDrawable;
    private float rating;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RatingView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mStarDrawable = getResources().getDrawable(C0622R.C0624drawable.ic_star_rating, null);
        this.mColorGray = 503316480;
        this.mColorHighlight = -1979711488;
    }

    public void setRating(float f) {
        this.rating = Math.min(Math.max(f, 0.0f), 5.0f);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        drawStars(canvas, 5.0f, this.mColorGray);
        drawStars(canvas, this.rating, this.mColorHighlight);
    }

    private void drawStars(Canvas canvas, float f, int i) {
        int i2 = getLayoutParams().width / 5;
        int i3 = (int) (((float) i2) * WIDTH_FACTOR);
        int i4 = i2 - i3;
        int i5 = (int) f;
        float f2 = f - ((float) i5);
        for (int i6 = 0; i6 < i5; i6++) {
            int i7 = (i6 * i2) + i4;
            Drawable mutate = this.mStarDrawable.getConstantState().newDrawable().mutate();
            mutate.setTint(i);
            mutate.setBounds(i7, i4, i7 + i3, i4 + i3);
            mutate.draw(canvas);
        }
        if (f2 > 0.0f) {
            int i8 = (i5 * i2) + i4;
            ClipDrawable clipDrawable = new ClipDrawable(this.mStarDrawable, 3, 1);
            clipDrawable.setTint(i);
            clipDrawable.setLevel((int) (f2 * 10000.0f));
            clipDrawable.setBounds(i8, i4, i8 + i3, i3 + i4);
            clipDrawable.draw(canvas);
        }
    }
}
