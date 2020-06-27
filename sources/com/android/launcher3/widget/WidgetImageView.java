package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;

public class WidgetImageView extends View {
    private Drawable mBadge;
    private final int mBadgeMargin;
    private Bitmap mBitmap;
    private final RectF mDstRectF;
    private final Paint mPaint;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public WidgetImageView(Context context) {
        this(context, null);
    }

    public WidgetImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPaint = new Paint(3);
        this.mDstRectF = new RectF();
        this.mBadgeMargin = context.getResources().getDimensionPixelSize(C0622R.dimen.profile_badge_margin);
    }

    public void setBitmap(Bitmap bitmap, Drawable drawable) {
        this.mBitmap = bitmap;
        this.mBadge = drawable;
        invalidate();
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mBitmap != null) {
            updateDstRectF();
            canvas.drawBitmap(this.mBitmap, null, this.mDstRectF, this.mPaint);
            if (this.mBadge != null) {
                this.mBadge.draw(canvas);
            }
        }
    }

    private void updateDstRectF() {
        float width = (float) getWidth();
        float height = (float) getHeight();
        float width2 = (float) this.mBitmap.getWidth();
        float f = width2 > width ? width / width2 : 1.0f;
        float f2 = width2 * f;
        float height2 = ((float) this.mBitmap.getHeight()) * f;
        this.mDstRectF.left = (width - f2) / 2.0f;
        this.mDstRectF.right = (width + f2) / 2.0f;
        if (height2 > height) {
            this.mDstRectF.top = 0.0f;
            this.mDstRectF.bottom = height2;
        } else {
            this.mDstRectF.top = (height - height2) / 2.0f;
            this.mDstRectF.bottom = (height + height2) / 2.0f;
        }
        if (this.mBadge != null) {
            Rect bounds = this.mBadge.getBounds();
            int boundToRange = Utilities.boundToRange((int) ((this.mDstRectF.right + ((float) this.mBadgeMargin)) - ((float) bounds.width())), this.mBadgeMargin, getWidth() - bounds.width());
            int boundToRange2 = Utilities.boundToRange((int) ((this.mDstRectF.bottom + ((float) this.mBadgeMargin)) - ((float) bounds.height())), this.mBadgeMargin, getHeight() - bounds.height());
            this.mBadge.setBounds(boundToRange, boundToRange2, bounds.width() + boundToRange, bounds.height() + boundToRange2);
        }
    }

    public Rect getBitmapBounds() {
        updateDstRectF();
        Rect rect = new Rect();
        this.mDstRectF.round(rect);
        return rect;
    }
}
