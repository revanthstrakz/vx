package com.android.launcher3.keyboard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.RectEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Property;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import com.android.launcher3.C0622R;

public abstract class FocusIndicatorHelper implements OnFocusChangeListener, AnimatorUpdateListener {
    public static final Property<FocusIndicatorHelper, Float> ALPHA = new Property<FocusIndicatorHelper, Float>(Float.TYPE, "alpha") {
        public void set(FocusIndicatorHelper focusIndicatorHelper, Float f) {
            focusIndicatorHelper.setAlpha(f.floatValue());
        }

        public Float get(FocusIndicatorHelper focusIndicatorHelper) {
            return Float.valueOf(focusIndicatorHelper.mAlpha);
        }
    };
    private static final long ANIM_DURATION = 150;
    private static final float MIN_VISIBLE_ALPHA = 0.2f;
    private static final RectEvaluator RECT_EVALUATOR = new RectEvaluator(new Rect());
    public static final Property<FocusIndicatorHelper, Float> SHIFT = new Property<FocusIndicatorHelper, Float>(Float.TYPE, "shift") {
        public void set(FocusIndicatorHelper focusIndicatorHelper, Float f) {
            focusIndicatorHelper.mShift = f.floatValue();
        }

        public Float get(FocusIndicatorHelper focusIndicatorHelper) {
            return Float.valueOf(focusIndicatorHelper.mShift);
        }
    };
    private static final Rect sTempRect1 = new Rect();
    private static final Rect sTempRect2 = new Rect();
    /* access modifiers changed from: private */
    public float mAlpha;
    private final View mContainer;
    private ObjectAnimator mCurrentAnimation;
    private View mCurrentView;
    private final Rect mDirtyRect = new Rect();
    private boolean mIsDirty = false;
    private View mLastFocusedView;
    private final int mMaxAlpha;
    private final Paint mPaint;
    /* access modifiers changed from: private */
    public float mShift;
    private View mTargetView;

    private class ViewSetListener extends AnimatorListenerAdapter {
        private final boolean mCallOnCancel;
        private boolean mCalled = false;
        private final View mViewToSet;

        public ViewSetListener(View view, boolean z) {
            this.mViewToSet = view;
            this.mCallOnCancel = z;
        }

        public void onAnimationCancel(Animator animator) {
            if (!this.mCallOnCancel) {
                this.mCalled = true;
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCalled) {
                FocusIndicatorHelper.this.setCurrentView(this.mViewToSet);
                this.mCalled = true;
            }
        }
    }

    public abstract void viewToRect(View view, Rect rect);

    public FocusIndicatorHelper(View view) {
        this.mContainer = view;
        this.mPaint = new Paint(1);
        int color = view.getResources().getColor(C0622R.color.focused_background);
        this.mMaxAlpha = Color.alpha(color);
        this.mPaint.setColor(color | -16777216);
        setAlpha(0.0f);
        this.mShift = 0.0f;
    }

    /* access modifiers changed from: protected */
    public void setAlpha(float f) {
        this.mAlpha = f;
        this.mPaint.setAlpha((int) (this.mAlpha * ((float) this.mMaxAlpha)));
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        invalidateDirty();
    }

    /* access modifiers changed from: protected */
    public void invalidateDirty() {
        if (this.mIsDirty) {
            this.mContainer.invalidate(this.mDirtyRect);
            this.mIsDirty = false;
        }
        Rect drawRect = getDrawRect();
        if (drawRect != null) {
            this.mContainer.invalidate(drawRect);
        }
    }

    public void draw(Canvas canvas) {
        if (this.mAlpha > 0.0f) {
            Rect drawRect = getDrawRect();
            if (drawRect != null) {
                this.mDirtyRect.set(drawRect);
                canvas.drawRect(this.mDirtyRect, this.mPaint);
                this.mIsDirty = true;
            }
        }
    }

    private Rect getDrawRect() {
        if (this.mCurrentView == null || !this.mCurrentView.isAttachedToWindow()) {
            return null;
        }
        viewToRect(this.mCurrentView, sTempRect1);
        if (this.mShift <= 0.0f || this.mTargetView == null) {
            return sTempRect1;
        }
        viewToRect(this.mTargetView, sTempRect2);
        return RECT_EVALUATOR.evaluate(this.mShift, sTempRect1, sTempRect2);
    }

    public void onFocusChange(View view, boolean z) {
        if (z) {
            endCurrentAnimation();
            if (this.mAlpha > 0.2f) {
                this.mTargetView = view;
                this.mCurrentAnimation = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{1.0f}), PropertyValuesHolder.ofFloat(SHIFT, new float[]{1.0f})});
                this.mCurrentAnimation.addListener(new ViewSetListener(view, true));
            } else {
                setCurrentView(view);
                this.mCurrentAnimation = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{1.0f})});
            }
            this.mLastFocusedView = view;
        } else if (this.mLastFocusedView == view) {
            this.mLastFocusedView = null;
            endCurrentAnimation();
            this.mCurrentAnimation = ObjectAnimator.ofPropertyValuesHolder(this, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(ALPHA, new float[]{0.0f})});
            this.mCurrentAnimation.addListener(new ViewSetListener(null, false));
        }
        invalidateDirty();
        if (!z) {
            view = null;
        }
        this.mLastFocusedView = view;
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.addUpdateListener(this);
            this.mCurrentAnimation.setDuration(ANIM_DURATION).start();
        }
    }

    /* access modifiers changed from: protected */
    public void endCurrentAnimation() {
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.cancel();
            this.mCurrentAnimation = null;
        }
    }

    /* access modifiers changed from: protected */
    public void setCurrentView(View view) {
        this.mCurrentView = view;
        this.mShift = 0.0f;
        this.mTargetView = null;
    }
}
