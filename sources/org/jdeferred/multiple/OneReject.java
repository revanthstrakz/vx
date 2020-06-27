package org.jdeferred.multiple;

import org.jdeferred.Promise;

public class OneReject {
    private final int index;
    private final Promise promise;
    private final Object reject;

    public OneReject(int i, Promise promise2, Object obj) {
        this.index = i;
        this.promise = promise2;
        this.reject = obj;
    }

    public int getIndex() {
        return this.index;
    }

    public Promise getPromise() {
        return this.promise;
    }

    public Object getReject() {
        return this.reject;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OneReject [index=");
        sb.append(this.index);
        sb.append(", promise=");
        sb.append(this.promise);
        sb.append(", reject=");
        sb.append(this.reject);
        sb.append("]");
        return sb.toString();
    }
}
