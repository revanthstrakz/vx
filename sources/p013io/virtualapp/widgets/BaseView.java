package p013io.virtualapp.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/* renamed from: io.virtualapp.widgets.BaseView */
public abstract class BaseView extends View {
    public ValueAnimator valueAnimator;

    /* access modifiers changed from: protected */
    public abstract void AnimIsRunning();

    /* access modifiers changed from: protected */
    public abstract void InitPaint();

    /* access modifiers changed from: protected */
    public abstract void OnAnimationRepeat(Animator animator);

    /* access modifiers changed from: protected */
    public abstract void OnAnimationUpdate(ValueAnimator valueAnimator2);

    /* access modifiers changed from: protected */
    public abstract int OnStopAnim();

    /* access modifiers changed from: protected */
    public abstract int SetAnimRepeatCount();

    /* access modifiers changed from: protected */
    public abstract int SetAnimRepeatMode();

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        InitPaint();
    }

    public void startAnim() {
        stopAnim();
        startViewAnim(0.0f, 1.0f, 1250);
    }

    public void startAnim(int i) {
        stopAnim();
        startViewAnim(0.0f, 1.0f, (long) i);
    }

    public void stopAnim() {
        if (this.valueAnimator != null) {
            clearAnimation();
            this.valueAnimator.setRepeatCount(0);
            this.valueAnimator.cancel();
            this.valueAnimator.end();
            if (OnStopAnim() == 0) {
                this.valueAnimator.setRepeatCount(0);
                this.valueAnimator.cancel();
                this.valueAnimator.end();
            }
        }
    }

    private ValueAnimator startViewAnim(float f, float f2, long j) {
        this.valueAnimator = ValueAnimator.ofFloat(new float[]{f, f2});
        this.valueAnimator.setDuration(j);
        this.valueAnimator.setInterpolator(new LinearInterpolator());
        this.valueAnimator.setRepeatCount(SetAnimRepeatCount());
        if (1 == SetAnimRepeatMode()) {
            this.valueAnimator.setRepeatMode(1);
        } else if (2 == SetAnimRepeatMode()) {
            this.valueAnimator.setRepeatMode(2);
        }
        this.valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                BaseView.this.OnAnimationUpdate(valueAnimator);
            }
        });
        this.valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
            }

            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
            }

            public void onAnimationRepeat(Animator animator) {
                super.onAnimationRepeat(animator);
                BaseView.this.OnAnimationRepeat(animator);
            }
        });
        if (!this.valueAnimator.isRunning()) {
            AnimIsRunning();
            this.valueAnimator.start();
        }
        return this.valueAnimator;
    }

    public float getFontlength(Paint paint, String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return (float) rect.width();
    }

    public float getFontHeight(Paint paint, String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return (float) rect.height();
    }

    public float getFontHeight(Paint paint) {
        FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }
}
