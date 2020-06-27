package org.jdeferred.multiple;

import org.jdeferred.Promise;

public class OneResult {
    private final int index;
    private final Promise promise;
    private final Object result;

    public OneResult(int i, Promise promise2, Object obj) {
        this.index = i;
        this.promise = promise2;
        this.result = obj;
    }

    public int getIndex() {
        return this.index;
    }

    public Promise getPromise() {
        return this.promise;
    }

    public Object getResult() {
        return this.result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OneResult [index=");
        sb.append(this.index);
        sb.append(", promise=");
        sb.append(this.promise);
        sb.append(", result=");
        sb.append(this.result);
        sb.append("]");
        return sb.toString();
    }
}
