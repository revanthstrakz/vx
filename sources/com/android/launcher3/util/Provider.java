package com.android.launcher3.util;

public abstract class Provider<T> {
    public abstract T get();

    /* renamed from: of */
    public static <T> Provider<T> m16of(final T t) {
        return new Provider<T>() {
            public T get() {
                return t;
            }
        };
    }
}
