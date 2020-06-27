package com.lody.virtual.server.p009pm;

import java.util.AbstractSet;
import java.util.Iterator;

/* renamed from: com.lody.virtual.server.pm.FastImmutableArraySet */
public final class FastImmutableArraySet<T> extends AbstractSet<T> {
    T[] mContents;
    FastIterator<T> mIterator;

    /* renamed from: com.lody.virtual.server.pm.FastImmutableArraySet$FastIterator */
    private static final class FastIterator<T> implements Iterator<T> {
        private final T[] mContents;
        int mIndex;

        public FastIterator(T[] tArr) {
            this.mContents = tArr;
        }

        public boolean hasNext() {
            return this.mIndex != this.mContents.length;
        }

        public T next() {
            T[] tArr = this.mContents;
            int i = this.mIndex;
            this.mIndex = i + 1;
            return tArr[i];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public FastImmutableArraySet(T[] tArr) {
        this.mContents = tArr;
    }

    public Iterator<T> iterator() {
        FastIterator<T> fastIterator = this.mIterator;
        if (fastIterator == null) {
            FastIterator<T> fastIterator2 = new FastIterator<>(this.mContents);
            this.mIterator = fastIterator2;
            return fastIterator2;
        }
        fastIterator.mIndex = 0;
        return fastIterator;
    }

    public int size() {
        return this.mContents.length;
    }
}
