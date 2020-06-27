package org.jdeferred.multiple;

import org.jdeferred.Promise;

public class OneProgress extends MasterProgress {
    private final int index;
    private final Object progress;
    private final Promise promise;

    public OneProgress(int i, int i2, int i3, int i4, Promise promise2, Object obj) {
        super(i, i2, i3);
        this.index = i4;
        this.promise = promise2;
        this.progress = obj;
    }

    public int getIndex() {
        return this.index;
    }

    public Promise getPromise() {
        return this.promise;
    }

    public Object getProgress() {
        return this.progress;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OneProgress [index=");
        sb.append(this.index);
        sb.append(", promise=");
        sb.append(this.promise);
        sb.append(", progress=");
        sb.append(this.progress);
        sb.append(", getDone()=");
        sb.append(getDone());
        sb.append(", getFail()=");
        sb.append(getFail());
        sb.append(", getTotal()=");
        sb.append(getTotal());
        sb.append("]");
        return sb.toString();
    }
}
