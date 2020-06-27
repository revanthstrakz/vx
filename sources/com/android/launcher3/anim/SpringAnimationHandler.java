package com.android.launcher3.anim;

import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import com.android.launcher3.C0622R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class SpringAnimationHandler<T> {
    private static final boolean DEBUG = false;
    private static final String TAG = "SpringAnimationHandler";
    private static final float VELOCITY_DAMPING_FACTOR = 0.175f;
    public static final int X_DIRECTION = 1;
    public static final int Y_DIRECTION = 0;
    private AnimationFactory<T> mAnimationFactory;
    private ArrayList<SpringAnimation> mAnimations = new ArrayList<>();
    private float mCurrentVelocity = 0.0f;
    private boolean mShouldComputeVelocity = false;
    private int mVelocityDirection;
    private VelocityTracker mVelocityTracker;

    public interface AnimationFactory<T> {
        SpringAnimation initialize(T t);

        void setDefaultValues(SpringAnimation springAnimation);

        void update(SpringAnimation springAnimation, T t);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    public SpringAnimationHandler(int i, AnimationFactory<T> animationFactory) {
        this.mVelocityDirection = i;
        this.mAnimationFactory = animationFactory;
    }

    public void add(SpringAnimation springAnimation, boolean z) {
        if (z) {
            this.mAnimationFactory.setDefaultValues(springAnimation);
        }
        springAnimation.setStartVelocity(this.mCurrentVelocity);
        this.mAnimations.add(springAnimation);
    }

    public void add(View view, T t) {
        SpringAnimation springAnimation = (SpringAnimation) view.getTag(C0622R.C0625id.spring_animation_tag);
        if (springAnimation == null) {
            springAnimation = this.mAnimationFactory.initialize(t);
            view.setTag(C0622R.C0625id.spring_animation_tag, springAnimation);
        }
        this.mAnimationFactory.update(springAnimation, t);
        add(springAnimation, false);
    }

    public void remove(View view) {
        remove((SpringAnimation) view.getTag(C0622R.C0625id.spring_animation_tag));
    }

    public void remove(SpringAnimation springAnimation) {
        if (springAnimation.canSkipToEnd()) {
            springAnimation.skipToEnd();
        }
        while (this.mAnimations.contains(springAnimation)) {
            this.mAnimations.remove(springAnimation);
        }
    }

    public void addMovement(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0 || actionMasked == 3) {
            reset();
        }
        getVelocityTracker().addMovement(motionEvent);
        this.mShouldComputeVelocity = true;
    }

    public void animateToFinalPosition(float f, int i) {
        animateToFinalPosition(f, i, this.mShouldComputeVelocity);
    }

    private void animateToFinalPosition(float f, int i, boolean z) {
        if (this.mShouldComputeVelocity) {
            this.mCurrentVelocity = computeVelocity();
        }
        int size = this.mAnimations.size();
        for (int i2 = 0; i2 < size; i2++) {
            ((SpringAnimation) this.mAnimations.get(i2)).setStartValue((float) i);
            if (z) {
                ((SpringAnimation) this.mAnimations.get(i2)).setStartVelocity(this.mCurrentVelocity);
            }
            ((SpringAnimation) this.mAnimations.get(i2)).animateToFinalPosition(f);
        }
        reset();
    }

    public void animateToPositionWithVelocity(float f, int i, float f2) {
        this.mCurrentVelocity = f2;
        this.mShouldComputeVelocity = false;
        animateToFinalPosition(f, i, true);
    }

    public boolean isRunning() {
        return !this.mAnimations.isEmpty() && ((SpringAnimation) this.mAnimations.get(0)).isRunning();
    }

    public void skipToEnd() {
        int size = this.mAnimations.size();
        for (int i = 0; i < size; i++) {
            if (((SpringAnimation) this.mAnimations.get(i)).canSkipToEnd()) {
                ((SpringAnimation) this.mAnimations.get(i)).skipToEnd();
            }
        }
    }

    public void reset() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
        this.mCurrentVelocity = 0.0f;
        this.mShouldComputeVelocity = false;
    }

    private float computeVelocity() {
        float f;
        getVelocityTracker().computeCurrentVelocity(1000);
        if (isVerticalDirection()) {
            f = getVelocityTracker().getYVelocity();
        } else {
            f = getVelocityTracker().getXVelocity();
        }
        return f * VELOCITY_DAMPING_FACTOR;
    }

    private boolean isVerticalDirection() {
        return this.mVelocityDirection == 0;
    }

    private VelocityTracker getVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        return this.mVelocityTracker;
    }

    public static SpringAnimation forView(View view, FloatPropertyCompat floatPropertyCompat, float f) {
        SpringAnimation springAnimation = new SpringAnimation(view, floatPropertyCompat, f);
        springAnimation.setSpring(new SpringForce(f));
        return springAnimation;
    }
}
