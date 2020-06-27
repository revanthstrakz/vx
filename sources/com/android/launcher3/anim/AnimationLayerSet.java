package com.android.launcher3.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.ArrayMap;
import android.view.View;
import java.util.Map.Entry;

public class AnimationLayerSet extends AnimatorListenerAdapter {
    private final ArrayMap<View, Integer> mViewsToLayerTypeMap;

    public AnimationLayerSet() {
        this.mViewsToLayerTypeMap = new ArrayMap<>();
    }

    public AnimationLayerSet(View view) {
        this.mViewsToLayerTypeMap = new ArrayMap<>(1);
        addView(view);
    }

    public void addView(View view) {
        this.mViewsToLayerTypeMap.put(view, Integer.valueOf(view.getLayerType()));
    }

    public void onAnimationStart(Animator animator) {
        for (Entry entry : this.mViewsToLayerTypeMap.entrySet()) {
            View view = (View) entry.getKey();
            entry.setValue(Integer.valueOf(view.getLayerType()));
            view.setLayerType(2, null);
            if (view.isAttachedToWindow() && view.getVisibility() == 0) {
                view.buildLayer();
            }
        }
    }

    public void onAnimationEnd(Animator animator) {
        for (Entry entry : this.mViewsToLayerTypeMap.entrySet()) {
            ((View) entry.getKey()).setLayerType(((Integer) entry.getValue()).intValue(), null);
        }
    }
}
