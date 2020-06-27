package com.android.launcher3.util;

import android.support.annotation.Nullable;
import java.util.Map;

public class ComponentKeyMapper<T> {
    protected final ComponentKey mComponentKey;

    public ComponentKeyMapper(ComponentKey componentKey) {
        this.mComponentKey = componentKey;
    }

    @Nullable
    public T getItem(Map<ComponentKey, T> map) {
        return map.get(this.mComponentKey);
    }

    public String getPackage() {
        return this.mComponentKey.componentName.getPackageName();
    }

    public String getComponentClass() {
        return this.mComponentKey.componentName.getClassName();
    }

    public String toString() {
        return this.mComponentKey.toString();
    }
}
