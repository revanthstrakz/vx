package com.android.launcher3.util;

import android.graphics.Rect;
import com.android.launcher3.ItemInfo;
import java.lang.reflect.Array;

public class GridOccupancy {
    public final boolean[][] cells;
    private final int mCountX;
    private final int mCountY;

    public GridOccupancy(int i, int i2) {
        this.mCountX = i;
        this.mCountY = i2;
        this.cells = (boolean[][]) Array.newInstance(boolean.class, new int[]{i, i2});
    }

    public boolean findVacantCell(int[] iArr, int i, int i2) {
        int i3 = 0;
        while (true) {
            int i4 = i3 + i2;
            if (i4 <= this.mCountY) {
                int i5 = 0;
                while (true) {
                    int i6 = i5 + i;
                    if (i6 > this.mCountX) {
                        break;
                    }
                    boolean z = !this.cells[i5][i3];
                    int i7 = i5;
                    while (true) {
                        if (i7 >= i6) {
                            break;
                        }
                        boolean z2 = z;
                        for (int i8 = i3; i8 < i4; i8++) {
                            z2 = z2 && !this.cells[i7][i8];
                            if (!z2) {
                                z = z2;
                                break;
                            }
                        }
                        i7++;
                        z = z2;
                    }
                    if (z) {
                        iArr[0] = i5;
                        iArr[1] = i3;
                        return true;
                    }
                    i5++;
                }
            } else {
                return false;
            }
            i3++;
        }
    }

    public void copyTo(GridOccupancy gridOccupancy) {
        for (int i = 0; i < this.mCountX; i++) {
            for (int i2 = 0; i2 < this.mCountY; i2++) {
                gridOccupancy.cells[i][i2] = this.cells[i][i2];
            }
        }
    }

    public boolean isRegionVacant(int i, int i2, int i3, int i4) {
        int i5 = (i3 + i) - 1;
        int i6 = (i4 + i2) - 1;
        if (i < 0 || i2 < 0 || i5 >= this.mCountX || i6 >= this.mCountY) {
            return false;
        }
        while (i <= i5) {
            for (int i7 = i2; i7 <= i6; i7++) {
                if (this.cells[i][i7]) {
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    public void markCells(int i, int i2, int i3, int i4, boolean z) {
        if (i >= 0 && i2 >= 0) {
            int i5 = i;
            while (i5 < i + i3 && i5 < this.mCountX) {
                int i6 = i2;
                while (i6 < i2 + i4 && i6 < this.mCountY) {
                    this.cells[i5][i6] = z;
                    i6++;
                }
                i5++;
            }
        }
    }

    public void markCells(Rect rect, boolean z) {
        markCells(rect.left, rect.top, rect.width(), rect.height(), z);
    }

    public void markCells(CellAndSpan cellAndSpan, boolean z) {
        markCells(cellAndSpan.cellX, cellAndSpan.cellY, cellAndSpan.spanX, cellAndSpan.spanY, z);
    }

    public void markCells(ItemInfo itemInfo, boolean z) {
        markCells(itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY, z);
    }

    public void clear() {
        markCells(0, 0, this.mCountX, this.mCountY, false);
    }
}
