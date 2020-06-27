package com.lody.virtual.helper.collection;

import java.util.Arrays;

public class IntArray {
    private static final int[] EMPTY_ARRAY = new int[0];
    private int[] mData;
    private int mSize;

    private IntArray() {
    }

    public IntArray(int i) {
        this.mData = new int[i];
    }

    /* renamed from: of */
    public static IntArray m77of(int... iArr) {
        IntArray intArray = new IntArray();
        intArray.mData = Arrays.copyOf(iArr, iArr.length);
        intArray.mSize = iArr.length;
        return intArray;
    }

    public void clear() {
        this.mSize = 0;
    }

    public void optimize() {
        if (this.mSize > this.mData.length) {
            this.mData = Arrays.copyOf(this.mData, this.mSize);
        }
    }

    public int[] getAll() {
        return this.mSize > 0 ? Arrays.copyOf(this.mData, this.mSize) : EMPTY_ARRAY;
    }

    public int get(int i) {
        return this.mData[i];
    }

    public int[] getRange(int i, int i2) {
        return Arrays.copyOfRange(this.mData, i, i2);
    }

    public void set(int i, int i2) {
        if (i < this.mSize) {
            this.mData[i] = i2;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Index ");
        sb.append(i);
        sb.append(" is greater than the list size ");
        sb.append(this.mSize);
        throw new IndexOutOfBoundsException(sb.toString());
    }

    private void ensureCapacity() {
        if (this.mSize > this.mData.length) {
            int length = this.mData.length;
            while (this.mSize > length) {
                length = ((length * 3) / 2) + 1;
            }
            this.mData = Arrays.copyOf(this.mData, length);
        }
    }

    public int size() {
        return this.mSize;
    }

    public void addAll(int[] iArr) {
        int i = this.mSize;
        this.mSize += iArr.length;
        ensureCapacity();
        System.arraycopy(iArr, 0, this.mData, i, iArr.length);
    }

    public void add(int i) {
        this.mSize++;
        ensureCapacity();
        this.mData[this.mSize - 1] = i;
    }

    public void remove(int i) {
        remove(i, 1);
    }

    public void remove(int i, int i2) {
        System.arraycopy(this.mData, i + i2, this.mData, i, (this.mSize - i) - i2);
        this.mSize -= i2;
    }

    public boolean contains(int i) {
        for (int i2 = 0; i2 < this.mSize; i2++) {
            if (this.mData[i2] == i) {
                return true;
            }
        }
        return false;
    }
}
