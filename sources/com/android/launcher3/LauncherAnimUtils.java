package com.android.launcher3;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.View;
import android.view.ViewTreeObserver.OnDrawListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;

public class LauncherAnimUtils {
    public static final Property<Drawable, Integer> DRAWABLE_ALPHA = new Property<Drawable, Integer>(Integer.TYPE, "drawableAlpha") {
        public Integer get(Drawable drawable) {
            return Integer.valueOf(drawable.getAlpha());
        }

        public void set(Drawable drawable, Integer num) {
            drawable.setAlpha(num.intValue());
        }
    };
    static WeakHashMap<Animator, Object> sAnimators = new WeakHashMap<>();
    static AnimatorListener sEndAnimListener = new AnimatorListener() {
        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
            LauncherAnimUtils.sAnimators.put(animator, null);
        }

        public void onAnimationEnd(Animator animator) {
            LauncherAnimUtils.sAnimators.remove(animator);
        }

        public void onAnimationCancel(Animator animator) {
            LauncherAnimUtils.sAnimators.remove(animator);
        }
    };

    public static void cancelOnDestroyActivity(Animator animator) {
        animator.addListener(sEndAnimListener);
    }

    public static void startAnimationAfterNextDraw(final Animator animator, final View view) {
        view.getViewTreeObserver().addOnDrawListener(new OnDrawListener() {
            private boolean mStarted = false;

            public void onDraw() {
                if (!this.mStarted) {
                    this.mStarted = true;
                    if (animator.getDuration() != 0) {
                        animator.start();
                        view.post(new Runnable() {
                            public void run() {
                                view.getViewTreeObserver().removeOnDrawListener(this);
                            }
                        });
                    }
                }
            }
        });
    }

    public static void onDestroyActivity() {
        Iterator it = new HashSet(sAnimators.keySet()).iterator();
        while (it.hasNext()) {
            Animator animator = (Animator) it.next();
            if (animator.isRunning()) {
                animator.cancel();
            }
            sAnimators.remove(animator);
        }
    }

    public static AnimatorSet createAnimatorSet() {
        AnimatorSet animatorSet = new AnimatorSet();
        cancelOnDestroyActivity(animatorSet);
        return animatorSet;
    }

    public static ValueAnimator ofFloat(float... fArr) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(fArr);
        cancelOnDestroyActivity(valueAnimator);
        return valueAnimator;
    }

    public static ObjectAnimator ofFloat(View view, Property<View, Float> property, float... fArr) {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, property, fArr);
        cancelOnDestroyActivity(ofFloat);
        new FirstFrameAnimatorHelper((ValueAnimator) ofFloat, view);
        return ofFloat;
    }

    public static ObjectAnimator ofViewAlphaAndScale(View view, float f, float f2, float f3) {
        return ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{f}), PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{f2}), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{f3}));
    }

    public static ObjectAnimator ofPropertyValuesHolder(View view, PropertyValuesHolder... propertyValuesHolderArr) {
        return ofPropertyValuesHolder(view, view, propertyValuesHolderArr);
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object obj, View view, PropertyValuesHolder... propertyValuesHolderArr) {
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(obj, propertyValuesHolderArr);
        cancelOnDestroyActivity(ofPropertyValuesHolder);
        new FirstFrameAnimatorHelper((ValueAnimator) ofPropertyValuesHolder, view);
        return ofPropertyValuesHolder;
    }
}
