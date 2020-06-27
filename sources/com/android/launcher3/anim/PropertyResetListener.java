package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Property;

public class PropertyResetListener<T, V> extends AnimatorListenerAdapter {
    private Property<T, V> mPropertyToReset;
    private V mResetToValue;

    public PropertyResetListener(Property<T, V> property, V v) {
        this.mPropertyToReset = property;
        this.mResetToValue = v;
    }

    public void onAnimationEnd(Animator animator) {
        this.mPropertyToReset.set(((ObjectAnimator) animator).getTarget(), this.mResetToValue);
    }
}
