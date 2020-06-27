package com.android.launcher3.util;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;

public class FlingAnimation implements AnimatorUpdateListener, Runnable {
    private static final int DRAG_END_DELAY = 300;
    private static final float MAX_ACCELERATION = 0.5f;
    protected float mAX;
    protected float mAY;
    protected final TimeInterpolator mAlphaInterpolator = new DecelerateInterpolator(0.75f);
    protected float mAnimationTimeFraction;
    protected final DragLayer mDragLayer;
    protected final DragObject mDragObject;
    /* access modifiers changed from: private */
    public final ButtonDropTarget mDropTarget;
    protected int mDuration;
    protected Rect mFrom;
    protected Rect mIconRect;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    protected final float mUX;
    protected final float mUY;

    public FlingAnimation(DragObject dragObject, PointF pointF, ButtonDropTarget buttonDropTarget, Launcher launcher) {
        this.mDropTarget = buttonDropTarget;
        this.mLauncher = launcher;
        this.mDragObject = dragObject;
        this.mUX = pointF.x / 1000.0f;
        this.mUY = pointF.y / 1000.0f;
        this.mDragLayer = this.mLauncher.getDragLayer();
    }

    public void run() {
        this.mIconRect = this.mDropTarget.getIconRect(this.mDragObject);
        this.mFrom = new Rect();
        this.mDragLayer.getViewRectRelativeToSelf(this.mDragObject.dragView, this.mFrom);
        float scaleX = this.mDragObject.dragView.getScaleX() - 1.0f;
        float measuredWidth = (((float) this.mDragObject.dragView.getMeasuredWidth()) * scaleX) / 2.0f;
        float measuredHeight = (scaleX * ((float) this.mDragObject.dragView.getMeasuredHeight())) / 2.0f;
        Rect rect = this.mFrom;
        rect.left = (int) (((float) rect.left) + measuredWidth);
        Rect rect2 = this.mFrom;
        rect2.right = (int) (((float) rect2.right) - measuredWidth);
        Rect rect3 = this.mFrom;
        rect3.top = (int) (((float) rect3.top) + measuredHeight);
        Rect rect4 = this.mFrom;
        rect4.bottom = (int) (((float) rect4.bottom) - measuredHeight);
        this.mDuration = Math.abs(this.mUY) > Math.abs(this.mUX) ? initFlingUpDuration() : initFlingLeftDuration();
        this.mAnimationTimeFraction = ((float) this.mDuration) / ((float) (this.mDuration + 300));
        this.mDragObject.dragView.setColor(0);
        final int i = this.mDuration + 300;
        final long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        this.mDragLayer.animateView(this.mDragObject.dragView, this, i, new TimeInterpolator() {
            private int mCount = -1;
            private float mOffset = 0.0f;

            public float getInterpolation(float f) {
                if (this.mCount < 0) {
                    this.mCount++;
                } else if (this.mCount == 0) {
                    this.mOffset = Math.min(0.5f, ((float) (AnimationUtils.currentAnimationTimeMillis() - currentAnimationTimeMillis)) / ((float) i));
                    this.mCount++;
                }
                return Math.min(1.0f, this.mOffset + f);
            }
        }, new Runnable() {
            public void run() {
                FlingAnimation.this.mLauncher.exitSpringLoadedDragMode();
                FlingAnimation.this.mDropTarget.completeDrop(FlingAnimation.this.mDragObject);
            }
        }, 0, null);
    }

    /* access modifiers changed from: protected */
    public int initFlingUpDuration() {
        float f = (float) (-this.mFrom.bottom);
        float f2 = (this.mUY * this.mUY) + (f * 2.0f * 0.5f);
        if (f2 >= 0.0f) {
            this.mAY = 0.5f;
        } else {
            this.mAY = (this.mUY * this.mUY) / ((-f) * 2.0f);
            f2 = 0.0f;
        }
        double sqrt = (((double) (-this.mUY)) - Math.sqrt((double) f2)) / ((double) this.mAY);
        this.mAX = (float) (((((double) ((-this.mFrom.exactCenterX()) + this.mIconRect.exactCenterX())) - (((double) this.mUX) * sqrt)) * 2.0d) / (sqrt * sqrt));
        return (int) Math.round(sqrt);
    }

    /* access modifiers changed from: protected */
    public int initFlingLeftDuration() {
        float f = (float) (-this.mFrom.right);
        float f2 = (this.mUX * this.mUX) + (f * 2.0f * 0.5f);
        if (f2 >= 0.0f) {
            this.mAX = 0.5f;
        } else {
            this.mAX = (this.mUX * this.mUX) / ((-f) * 2.0f);
            f2 = 0.0f;
        }
        double sqrt = (((double) (-this.mUX)) - Math.sqrt((double) f2)) / ((double) this.mAX);
        this.mAY = (float) (((((double) ((-this.mFrom.exactCenterY()) + this.mIconRect.exactCenterY())) - (((double) this.mUY) * sqrt)) * 2.0d) / (sqrt * sqrt));
        return (int) Math.round(sqrt);
    }

    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float f;
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (animatedFraction > this.mAnimationTimeFraction) {
            f = 1.0f;
        } else {
            f = animatedFraction / this.mAnimationTimeFraction;
        }
        DragView dragView = (DragView) this.mDragLayer.getAnimatedView();
        float f2 = ((float) this.mDuration) * f;
        dragView.setTranslationX((this.mUX * f2) + ((float) this.mFrom.left) + (((this.mAX * f2) * f2) / 2.0f));
        dragView.setTranslationY((this.mUY * f2) + ((float) this.mFrom.top) + (((this.mAY * f2) * f2) / 2.0f));
        dragView.setAlpha(1.0f - this.mAlphaInterpolator.getInterpolation(f));
    }
}
