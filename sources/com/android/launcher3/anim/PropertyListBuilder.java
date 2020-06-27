package com.android.launcher3.anim;

import android.animation.PropertyValuesHolder;
import android.view.View;
import java.util.ArrayList;

public class PropertyListBuilder {
    private final ArrayList<PropertyValuesHolder> mProperties = new ArrayList<>();

    public PropertyListBuilder translationX(float f) {
        this.mProperties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_X, new float[]{f}));
        return this;
    }

    public PropertyListBuilder translationY(float f) {
        this.mProperties.add(PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, new float[]{f}));
        return this;
    }

    public PropertyListBuilder scaleX(float f) {
        this.mProperties.add(PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{f}));
        return this;
    }

    public PropertyListBuilder scaleY(float f) {
        this.mProperties.add(PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{f}));
        return this;
    }

    public PropertyListBuilder scale(float f) {
        return scaleX(f).scaleY(f);
    }

    public PropertyListBuilder alpha(float f) {
        this.mProperties.add(PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{f}));
        return this;
    }

    public PropertyValuesHolder[] build() {
        return (PropertyValuesHolder[]) this.mProperties.toArray(new PropertyValuesHolder[this.mProperties.size()]);
    }
}
