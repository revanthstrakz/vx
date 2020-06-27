package com.android.launcher3.util;

public abstract class RunnableWithId implements Runnable {
    public static final int RUNNABLE_ID_BIND_APPS = 1;
    public static final int RUNNABLE_ID_BIND_WIDGETS = 2;

    /* renamed from: id */
    public final int f68id;

    public RunnableWithId(int i) {
        this.f68id = i;
    }

    public boolean equals(Object obj) {
        return (obj instanceof RunnableWithId) && ((RunnableWithId) obj).f68id == this.f68id;
    }
}
