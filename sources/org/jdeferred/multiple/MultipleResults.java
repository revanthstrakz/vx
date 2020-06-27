package org.jdeferred.multiple;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultipleResults implements Iterable<OneResult> {
    private final List<OneResult> results;

    public MultipleResults(int i) {
        this.results = new CopyOnWriteArrayList(new OneResult[i]);
    }

    /* access modifiers changed from: protected */
    public void set(int i, OneResult oneResult) {
        this.results.set(i, oneResult);
    }

    public OneResult get(int i) {
        return (OneResult) this.results.get(i);
    }

    public Iterator<OneResult> iterator() {
        return this.results.iterator();
    }

    public int size() {
        return this.results.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultipleResults [results=");
        sb.append(this.results);
        sb.append("]");
        return sb.toString();
    }
}
