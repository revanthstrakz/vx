package com.android.launcher3.popup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;

public abstract class PopupItemView extends FrameLayout {
    private final Paint mBackgroundClipPaint;
    /* access modifiers changed from: protected */
    public View mIconView;
    protected final boolean mIsRtl;
    private final Matrix mMatrix;
    protected final Rect mPillRect;
    private Bitmap mRoundedCornerBitmap;
    protected int mRoundedCorners;

    public PopupItemView(Context context) {
        this(context, null, 0);
    }

    public PopupItemView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PopupItemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mBackgroundClipPaint = new Paint(5);
        this.mMatrix = new Matrix();
        this.mPillRect = new Rect();
        int backgroundRadius = (int) getBackgroundRadius();
        this.mRoundedCornerBitmap = Bitmap.createBitmap(backgroundRadius, backgroundRadius, Config.ALPHA_8);
        Canvas canvas = new Canvas();
        canvas.setBitmap(this.mRoundedCornerBitmap);
        float f = (float) (backgroundRadius * 2);
        canvas.drawArc(0.0f, 0.0f, f, f, 180.0f, 90.0f, true, this.mBackgroundClipPaint);
        this.mBackgroundClipPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mIconView = findViewById(C0622R.C0625id.popup_item_icon);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mPillRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.mRoundedCorners == 0) {
            super.dispatchDraw(canvas);
            return;
        }
        int saveLayer = canvas.saveLayer(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), null);
        super.dispatchDraw(canvas);
        int width = this.mRoundedCornerBitmap.getWidth();
        int height = this.mRoundedCornerBitmap.getHeight();
        int round = Math.round(((float) width) / 2.0f);
        int round2 = Math.round(((float) height) / 2.0f);
        if ((this.mRoundedCorners & 1) != 0) {
            this.mMatrix.reset();
            canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
            this.mMatrix.setRotate(90.0f, (float) round, (float) round2);
            this.mMatrix.postTranslate((float) (canvas.getWidth() - width), 0.0f);
            canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
        }
        if ((this.mRoundedCorners & 2) != 0) {
            float f = (float) round;
            float f2 = (float) round2;
            this.mMatrix.setRotate(180.0f, f, f2);
            this.mMatrix.postTranslate((float) (canvas.getWidth() - width), (float) (canvas.getHeight() - height));
            canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
            this.mMatrix.setRotate(270.0f, f, f2);
            this.mMatrix.postTranslate(0.0f, (float) (canvas.getHeight() - height));
            canvas.drawBitmap(this.mRoundedCornerBitmap, this.mMatrix, this.mBackgroundClipPaint);
        }
        canvas.restoreToCount(saveLayer);
    }

    public void setBackgroundWithCorners(int i, int i2) {
        float f;
        this.mRoundedCorners = i2;
        float f2 = 0.0f;
        if ((i2 & 1) == 0) {
            f = 0.0f;
        } else {
            f = getBackgroundRadius();
        }
        if ((i2 & 2) != 0) {
            f2 = getBackgroundRadius();
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f2, f2, f2, f2}, null, null));
        shapeDrawable.getPaint().setColor(i);
        setBackground(shapeDrawable);
    }

    /* access modifiers changed from: protected */
    public float getBackgroundRadius() {
        return (float) getResources().getDimensionPixelSize(C0622R.dimen.bg_round_rect_radius);
    }
}
