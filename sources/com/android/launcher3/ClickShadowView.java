package com.android.launcher3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;

public class ClickShadowView extends View {
    private static final int SHADOW_HIGH_ALPHA = 60;
    private static final int SHADOW_LOW_ALPHA = 30;
    private static final int SHADOW_SIZE_FACTOR = 3;
    private Bitmap mBitmap;
    private final Paint mPaint = new Paint(2);
    @ExportedProperty(category = "launcher")
    private final float mShadowOffset;
    @ExportedProperty(category = "launcher")
    private final float mShadowPadding;

    public ClickShadowView(Context context) {
        super(context);
        this.mPaint.setColor(-16777216);
        this.mShadowPadding = getResources().getDimension(C0622R.dimen.blur_size_click_shadow);
        this.mShadowOffset = getResources().getDimension(C0622R.dimen.click_shadow_high_shift);
    }

    public int getExtraSize() {
        return (int) (this.mShadowPadding * 3.0f);
    }

    public boolean setBitmap(Bitmap bitmap) {
        if (bitmap == this.mBitmap) {
            return false;
        }
        this.mBitmap = bitmap;
        invalidate();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mBitmap != null) {
            this.mPaint.setAlpha(30);
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
            this.mPaint.setAlpha(60);
            canvas.drawBitmap(this.mBitmap, 0.0f, this.mShadowOffset, this.mPaint);
        }
    }

    public void animateShadow() {
        setAlpha(0.0f);
        animate().alpha(1.0f).setDuration(2000).setInterpolator(FastBitmapDrawable.CLICK_FEEDBACK_INTERPOLATOR).start();
    }

    public void alignWithIconView(BubbleTextView bubbleTextView, ViewGroup viewGroup, View view) {
        float left = (float) ((bubbleTextView.getLeft() + viewGroup.getLeft()) - getLeft());
        float top = (float) ((bubbleTextView.getTop() + viewGroup.getTop()) - getTop());
        int right = bubbleTextView.getRight() - bubbleTextView.getLeft();
        int bottom = bubbleTextView.getBottom() - bubbleTextView.getTop();
        int compoundPaddingRight = (right - bubbleTextView.getCompoundPaddingRight()) - bubbleTextView.getCompoundPaddingLeft();
        float width = (float) bubbleTextView.getIcon().getBounds().width();
        if (view != null) {
            int[] iArr = {0, 0};
            Utilities.getDescendantCoordRelativeToAncestor(view, (View) getParent(), iArr, false);
            int max = (int) Math.max(0.0f, (((float) iArr[0]) - left) - this.mShadowPadding);
            int max2 = (int) Math.max(0.0f, (((float) iArr[1]) - top) - this.mShadowPadding);
            setClipBounds(new Rect(max, max2, max + right, bottom + max2));
        } else {
            setClipBounds(null);
        }
        setTranslationX(((((left + viewGroup.getTranslationX()) + (((float) bubbleTextView.getCompoundPaddingLeft()) * bubbleTextView.getScaleX())) + (((((float) compoundPaddingRight) - width) * bubbleTextView.getScaleX()) / 2.0f)) + ((((float) right) * (1.0f - bubbleTextView.getScaleX())) / 2.0f)) - this.mShadowPadding);
        setTranslationY((((top + viewGroup.getTranslationY()) + (((float) bubbleTextView.getPaddingTop()) * bubbleTextView.getScaleY())) + ((((float) bubbleTextView.getHeight()) * (1.0f - bubbleTextView.getScaleY())) / 2.0f)) - this.mShadowPadding);
    }
}
