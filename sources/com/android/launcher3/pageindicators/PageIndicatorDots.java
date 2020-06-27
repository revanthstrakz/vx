package com.android.launcher3.pageindicators;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewOutlineProvider;
import android.view.animation.OvershootInterpolator;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Themes;

public class PageIndicatorDots extends PageIndicator {
    private static final long ANIMATION_DURATION = 150;
    private static final Property<PageIndicatorDots, Float> CURRENT_POSITION = new Property<PageIndicatorDots, Float>(Float.TYPE, "current_position") {
        public Float get(PageIndicatorDots pageIndicatorDots) {
            return Float.valueOf(pageIndicatorDots.mCurrentPosition);
        }

        public void set(PageIndicatorDots pageIndicatorDots, Float f) {
            pageIndicatorDots.mCurrentPosition = f.floatValue();
            pageIndicatorDots.invalidate();
            pageIndicatorDots.invalidateOutline();
        }
    };
    private static final int ENTER_ANIMATION_DURATION = 400;
    private static final float ENTER_ANIMATION_OVERSHOOT_TENSION = 4.9f;
    private static final int ENTER_ANIMATION_STAGGERED_DELAY = 150;
    private static final int ENTER_ANIMATION_START_DELAY = 300;
    private static final float SHIFT_PER_ANIMATION = 0.5f;
    private static final float SHIFT_THRESHOLD = 0.1f;
    private static final RectF sTempRect = new RectF();
    private final int mActiveColor;
    private int mActivePage;
    /* access modifiers changed from: private */
    public ObjectAnimator mAnimator;
    private final Paint mCirclePaint;
    /* access modifiers changed from: private */
    public float mCurrentPosition;
    /* access modifiers changed from: private */
    public final float mDotRadius;
    /* access modifiers changed from: private */
    public float[] mEntryAnimationRadiusFactors;
    /* access modifiers changed from: private */
    public float mFinalPosition;
    private final int mInActiveColor;
    private final boolean mIsRtl;

    private class AnimationCycleListener extends AnimatorListenerAdapter {
        private boolean mCancelled;

        private AnimationCycleListener() {
            this.mCancelled = false;
        }

        public void onAnimationCancel(Animator animator) {
            this.mCancelled = true;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCancelled) {
                PageIndicatorDots.this.mAnimator = null;
                PageIndicatorDots.this.animateToPosition(PageIndicatorDots.this.mFinalPosition);
            }
        }
    }

    private class MyOutlineProver extends ViewOutlineProvider {
        private MyOutlineProver() {
        }

        public void getOutline(View view, Outline outline) {
            if (PageIndicatorDots.this.mEntryAnimationRadiusFactors == null) {
                RectF access$400 = PageIndicatorDots.this.getActiveRect();
                outline.setRoundRect((int) access$400.left, (int) access$400.top, (int) access$400.right, (int) access$400.bottom, PageIndicatorDots.this.mDotRadius);
            }
        }
    }

    public PageIndicatorDots(Context context) {
        this(context, null);
    }

    public PageIndicatorDots(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PageIndicatorDots(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCirclePaint = new Paint(1);
        this.mCirclePaint.setStyle(Style.FILL);
        this.mDotRadius = getResources().getDimension(C0622R.dimen.page_indicator_dot_size) / 2.0f;
        setOutlineProvider(new MyOutlineProver());
        this.mActiveColor = Themes.getColorAccent(context);
        this.mInActiveColor = Themes.getAttrColor(context, 16843820);
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    public void setScroll(int i, int i2) {
        if (this.mNumPages > 1) {
            if (this.mIsRtl) {
                i = i2 - i;
            }
            int i3 = i2 / (this.mNumPages - 1);
            int i4 = i / i3;
            int i5 = i4 * i3;
            int i6 = i5 + i3;
            float f = ((float) i3) * 0.1f;
            float f2 = (float) i;
            if (f2 < ((float) i5) + f) {
                animateToPosition((float) i4);
            } else if (f2 > ((float) i6) - f) {
                animateToPosition((float) (i4 + 1));
            } else {
                animateToPosition(((float) i4) + 0.5f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void animateToPosition(float f) {
        this.mFinalPosition = f;
        if (Math.abs(this.mCurrentPosition - this.mFinalPosition) < 0.1f) {
            this.mCurrentPosition = this.mFinalPosition;
        }
        if (this.mAnimator == null && Float.compare(this.mCurrentPosition, this.mFinalPosition) != 0) {
            this.mAnimator = ObjectAnimator.ofFloat(this, CURRENT_POSITION, new float[]{this.mCurrentPosition > this.mFinalPosition ? this.mCurrentPosition - 0.5f : this.mCurrentPosition + 0.5f});
            this.mAnimator.addListener(new AnimationCycleListener());
            this.mAnimator.setDuration(ANIMATION_DURATION);
            this.mAnimator.start();
        }
    }

    public void stopAllAnimations() {
        if (this.mAnimator != null) {
            this.mAnimator.cancel();
            this.mAnimator = null;
        }
        this.mFinalPosition = (float) this.mActivePage;
        CURRENT_POSITION.set(this, Float.valueOf(this.mFinalPosition));
    }

    public void prepareEntryAnimation() {
        this.mEntryAnimationRadiusFactors = new float[this.mNumPages];
        invalidate();
    }

    public void playEntryAnimation() {
        int length = this.mEntryAnimationRadiusFactors.length;
        if (length == 0) {
            this.mEntryAnimationRadiusFactors = null;
            invalidate();
            return;
        }
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(ENTER_ANIMATION_OVERSHOOT_TENSION);
        AnimatorSet animatorSet = new AnimatorSet();
        for (final int i = 0; i < length; i++) {
            ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(400);
            duration.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PageIndicatorDots.this.mEntryAnimationRadiusFactors[i] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    PageIndicatorDots.this.invalidate();
                }
            });
            duration.setInterpolator(overshootInterpolator);
            duration.setStartDelay((long) ((i * 150) + 300));
            animatorSet.play(duration);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                PageIndicatorDots.this.mEntryAnimationRadiusFactors = null;
                PageIndicatorDots.this.invalidateOutline();
                PageIndicatorDots.this.invalidate();
            }
        });
        animatorSet.start();
    }

    public void setActiveMarker(int i) {
        if (this.mActivePage != i) {
            this.mActivePage = i;
        }
    }

    /* access modifiers changed from: protected */
    public void onPageCountChanged() {
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getMode(i) == 1073741824 ? MeasureSpec.getSize(i) : (int) (((float) ((this.mNumPages * 3) + 2)) * this.mDotRadius), MeasureSpec.getMode(i2) == 1073741824 ? MeasureSpec.getSize(i2) : (int) (this.mDotRadius * 4.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = this.mDotRadius * 3.0f;
        float width = (((((float) getWidth()) - (((float) this.mNumPages) * f)) + this.mDotRadius) / 2.0f) + this.mDotRadius;
        float height = (float) (canvas.getHeight() / 2);
        int i = 0;
        if (this.mEntryAnimationRadiusFactors != null) {
            if (this.mIsRtl) {
                width = ((float) getWidth()) - width;
                f = -f;
            }
            while (i < this.mEntryAnimationRadiusFactors.length) {
                this.mCirclePaint.setColor(i == this.mActivePage ? this.mActiveColor : this.mInActiveColor);
                canvas.drawCircle(width, height, this.mDotRadius * this.mEntryAnimationRadiusFactors[i], this.mCirclePaint);
                width += f;
                i++;
            }
            return;
        }
        this.mCirclePaint.setColor(this.mInActiveColor);
        while (i < this.mNumPages) {
            canvas.drawCircle(width, height, this.mDotRadius, this.mCirclePaint);
            width += f;
            i++;
        }
        this.mCirclePaint.setColor(this.mActiveColor);
        canvas.drawRoundRect(getActiveRect(), this.mDotRadius, this.mDotRadius, this.mCirclePaint);
    }

    /* access modifiers changed from: private */
    public RectF getActiveRect() {
        float f = (float) ((int) this.mCurrentPosition);
        float f2 = this.mCurrentPosition - f;
        float f3 = this.mDotRadius * 2.0f;
        float f4 = this.mDotRadius * 3.0f;
        float width = ((((float) getWidth()) - (((float) this.mNumPages) * f4)) + this.mDotRadius) / 2.0f;
        sTempRect.top = (((float) getHeight()) * 0.5f) - this.mDotRadius;
        sTempRect.bottom = (((float) getHeight()) * 0.5f) + this.mDotRadius;
        sTempRect.left = width + (f * f4);
        sTempRect.right = sTempRect.left + f3;
        if (f2 < 0.5f) {
            RectF rectF = sTempRect;
            rectF.right += f2 * f4 * 2.0f;
        } else {
            RectF rectF2 = sTempRect;
            rectF2.right += f4;
            float f5 = f2 - 0.5f;
            RectF rectF3 = sTempRect;
            rectF3.left += f5 * f4 * 2.0f;
        }
        if (this.mIsRtl) {
            float width2 = sTempRect.width();
            sTempRect.right = ((float) getWidth()) - sTempRect.left;
            sTempRect.left = sTempRect.right - width2;
        }
        return sTempRect;
    }
}
