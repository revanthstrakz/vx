package com.android.launcher3.util;

public class CellAndSpan {
    public int cellX = -1;
    public int cellY = -1;
    public int spanX = 1;
    public int spanY = 1;

    public CellAndSpan() {
    }

    public void copyFrom(CellAndSpan cellAndSpan) {
        this.cellX = cellAndSpan.cellX;
        this.cellY = cellAndSpan.cellY;
        this.spanX = cellAndSpan.spanX;
        this.spanY = cellAndSpan.spanY;
    }

    public CellAndSpan(int i, int i2, int i3, int i4) {
        this.cellX = i;
        this.cellY = i2;
        this.spanX = i3;
        this.spanY = i4;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.cellX);
        sb.append(", ");
        sb.append(this.cellY);
        sb.append(": ");
        sb.append(this.spanX);
        sb.append(", ");
        sb.append(this.spanY);
        sb.append(")");
        return sb.toString();
    }
}
