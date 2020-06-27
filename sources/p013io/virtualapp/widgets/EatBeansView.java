package p013io.virtualapp.widgets;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;

/* renamed from: io.virtualapp.widgets.EatBeansView */
public class EatBeansView extends BaseView {
    private float beansWidth = 10.0f;
    private float eatErEndAngle = (360.0f - (this.eatErStartAngle * 2.0f));
    private float eatErPositionX = 0.0f;
    private float eatErStartAngle = this.mAngle;
    private float eatErWidth = 50.0f;
    int eatSpeed = 8;
    private float mAngle = 34.0f;
    private float mHigh = 0.0f;
    private float mPadding = 5.0f;
    private Paint mPaint;
    private Paint mPaintBeans;
    private Paint mPaintEye;
    private RectF mRect = new RectF();
    private float mWidth = 0.0f;

    /* access modifiers changed from: protected */
    public void AnimIsRunning() {
    }

    /* access modifiers changed from: protected */
    public void OnAnimationRepeat(Animator animator) {
    }

    /* access modifiers changed from: protected */
    public int SetAnimRepeatCount() {
        return -1;
    }

    /* access modifiers changed from: protected */
    public int SetAnimRepeatMode() {
        return 1;
    }

    public EatBeansView(Context context) {
        super(context);
    }

    public EatBeansView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public EatBeansView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mWidth = (float) getMeasuredWidth();
        this.mHigh = (float) getMeasuredHeight();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float f = this.mPadding + this.eatErWidth + this.eatErPositionX;
        this.mRect.set(this.mPadding + this.eatErPositionX, (this.mHigh / 2.0f) - (this.eatErWidth / 2.0f), f, (this.mHigh / 2.0f) + (this.eatErWidth / 2.0f));
        canvas.drawArc(this.mRect, this.eatErStartAngle, this.eatErEndAngle, true, this.mPaint);
        canvas.drawCircle(this.mPadding + this.eatErPositionX + (this.eatErWidth / 2.0f), (this.mHigh / 2.0f) - (this.eatErWidth / 4.0f), this.beansWidth / 2.0f, this.mPaintEye);
        int i = (int) ((((this.mWidth - (this.mPadding * 2.0f)) - this.eatErWidth) / this.beansWidth) / 2.0f);
        for (int i2 = 0; i2 < i; i2++) {
            float f2 = ((float) (i * i2)) + (this.beansWidth / 2.0f) + this.mPadding + this.eatErWidth;
            if (f2 > f) {
                canvas.drawCircle(f2, this.mHigh / 2.0f, this.beansWidth / 2.0f, this.mPaintBeans);
            }
        }
    }

    private void initPaint() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setColor(-572662307);
        this.mPaintBeans = new Paint();
        this.mPaintBeans.setAntiAlias(true);
        this.mPaintBeans.setStyle(Style.FILL);
        this.mPaintBeans.setColor(-4473925);
        this.mPaintEye = new Paint();
        this.mPaintEye.setAntiAlias(true);
        this.mPaintEye.setStyle(Style.FILL);
        this.mPaintEye.setColor(-7829368);
    }

    public void setViewColor(int i) {
        this.mPaint.setColor(i);
        postInvalidate();
    }

    public void setEyeColor(int i) {
        this.mPaintEye.setColor(i);
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void InitPaint() {
        initPaint();
    }

    /* access modifiers changed from: protected */
    public void OnAnimationUpdate(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.eatErPositionX = ((this.mWidth - (this.mPadding * 2.0f)) - this.eatErWidth) * floatValue;
        this.eatErStartAngle = this.mAngle * (1.0f - ((((float) this.eatSpeed) * floatValue) - ((float) ((int) (floatValue * ((float) this.eatSpeed))))));
        this.eatErEndAngle = 360.0f - (this.eatErStartAngle * 2.0f);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public int OnStopAnim() {
        this.eatErPositionX = 0.0f;
        postInvalidate();
        return 1;
    }
}
