package com.android.launcher3.util;

import android.util.Log;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.CellLayout.LayoutParams;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ShortcutAndWidgetContainer;
import java.lang.reflect.Array;
import java.util.Arrays;

public class FocusLogic {
    public static final int ALL_APPS_COLUMN = -11;
    public static final int CURRENT_PAGE_FIRST_ITEM = -6;
    public static final int CURRENT_PAGE_LAST_ITEM = -7;
    private static final boolean DEBUG = false;
    public static final int EMPTY = -1;
    public static final int NEXT_PAGE_FIRST_ITEM = -8;
    public static final int NEXT_PAGE_LEFT_COLUMN = -9;
    public static final int NEXT_PAGE_RIGHT_COLUMN = -10;
    public static final int NOOP = -1;
    public static final int PIVOT = 100;
    public static final int PREVIOUS_PAGE_FIRST_ITEM = -3;
    public static final int PREVIOUS_PAGE_LAST_ITEM = -4;
    public static final int PREVIOUS_PAGE_LEFT_COLUMN = -5;
    public static final int PREVIOUS_PAGE_RIGHT_COLUMN = -2;
    private static final String TAG = "FocusLogic";

    private static int handleMoveEnd() {
        return -7;
    }

    private static int handleMoveHome() {
        return -6;
    }

    private static int handlePageDown(int i, int i2) {
        return i < i2 + -1 ? -8 : -7;
    }

    private static int handlePageUp(int i) {
        return i > 0 ? -3 : -6;
    }

    private static boolean isValid(int i, int i2, int i3, int i4) {
        return i >= 0 && i < i3 && i2 >= 0 && i2 < i4;
    }

    public static boolean shouldConsume(int i) {
        return i == 21 || i == 22 || i == 19 || i == 20 || i == 122 || i == 123 || i == 92 || i == 93;
    }

    public static int handleKeyEvent(int i, int[][] iArr, int i2, int i3, int i4, boolean z) {
        int i5;
        int i6;
        int length = iArr == null ? -1 : iArr.length;
        if (iArr == null) {
            i5 = -1;
        } else {
            i5 = iArr[0].length;
        }
        switch (i) {
            case 19:
                return handleDpadVertical(i2, length, i5, iArr, -1);
            case 20:
                return handleDpadVertical(i2, length, i5, iArr, 1);
            case 21:
                i6 = handleDpadHorizontal(i2, length, i5, iArr, -1, z);
                if (!z && i6 == -1 && i3 > 0) {
                    return -2;
                }
                if (z && i6 == -1 && i3 < i4 - 1) {
                    return -10;
                }
            case 22:
                i6 = handleDpadHorizontal(i2, length, i5, iArr, 1, z);
                if (!z && i6 == -1 && i3 < i4 - 1) {
                    return -9;
                }
                if (z && i6 == -1 && i3 > 0) {
                    return -5;
                }
            case 92:
                return handlePageUp(i3);
            case 93:
                return handlePageDown(i3, i4);
            case 122:
                return handleMoveHome();
            case 123:
                return handleMoveEnd();
            default:
                return -1;
        }
        return i6;
    }

    private static int[][] createFullMatrix(int i, int i2) {
        int[][] iArr = (int[][]) Array.newInstance(int.class, new int[]{i, i2});
        for (int i3 = 0; i3 < i; i3++) {
            Arrays.fill(iArr[i3], -1);
        }
        return iArr;
    }

    public static int[][] createSparseMatrix(CellLayout cellLayout) {
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
        int countX = cellLayout.getCountX();
        int countY = cellLayout.getCountY();
        boolean invertLayoutHorizontally = shortcutsAndWidgets.invertLayoutHorizontally();
        int[][] createFullMatrix = createFullMatrix(countX, countY);
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            View childAt = shortcutsAndWidgets.getChildAt(i);
            if (childAt.isFocusable()) {
                int i2 = ((LayoutParams) childAt.getLayoutParams()).cellX;
                int i3 = ((LayoutParams) childAt.getLayoutParams()).cellY;
                if (invertLayoutHorizontally) {
                    i2 = (countX - i2) - 1;
                }
                createFullMatrix[i2][i3] = i;
            }
        }
        return createFullMatrix;
    }

    public static int[][] createSparseMatrixWithHotseat(CellLayout cellLayout, CellLayout cellLayout2, DeviceProfile deviceProfile) {
        int i;
        int i2;
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
        ShortcutAndWidgetContainer shortcutsAndWidgets2 = cellLayout2.getShortcutsAndWidgets();
        boolean z = !deviceProfile.isVerticalBarLayout();
        if (z) {
            i2 = cellLayout2.getCountX();
            i = cellLayout.getCountY() + cellLayout2.getCountY();
        } else {
            i2 = cellLayout.getCountX() + cellLayout2.getCountX();
            i = cellLayout2.getCountY();
        }
        int[][] createFullMatrix = createFullMatrix(i2, i);
        for (int i3 = 0; i3 < shortcutsAndWidgets.getChildCount(); i3++) {
            View childAt = shortcutsAndWidgets.getChildAt(i3);
            if (childAt.isFocusable()) {
                int i4 = ((LayoutParams) childAt.getLayoutParams()).cellX;
                createFullMatrix[i4][((LayoutParams) childAt.getLayoutParams()).cellY] = i3;
            }
        }
        for (int childCount = shortcutsAndWidgets2.getChildCount() - 1; childCount >= 0; childCount--) {
            if (z) {
                createFullMatrix[((LayoutParams) shortcutsAndWidgets2.getChildAt(childCount).getLayoutParams()).cellX][cellLayout.getCountY()] = shortcutsAndWidgets.getChildCount() + childCount;
            } else {
                createFullMatrix[cellLayout.getCountX()][((LayoutParams) shortcutsAndWidgets2.getChildAt(childCount).getLayoutParams()).cellY] = shortcutsAndWidgets.getChildCount() + childCount;
            }
        }
        return createFullMatrix;
    }

    public static int[][] createSparseMatrixWithPivotColumn(CellLayout cellLayout, int i, int i2) {
        ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
        int[][] createFullMatrix = createFullMatrix(cellLayout.getCountX() + 1, cellLayout.getCountY());
        for (int i3 = 0; i3 < shortcutsAndWidgets.getChildCount(); i3++) {
            View childAt = shortcutsAndWidgets.getChildAt(i3);
            if (childAt.isFocusable()) {
                int i4 = ((LayoutParams) childAt.getLayoutParams()).cellX;
                int i5 = ((LayoutParams) childAt.getLayoutParams()).cellY;
                if (i < 0) {
                    createFullMatrix[i4 - i][i5] = i3;
                } else {
                    createFullMatrix[i4][i5] = i3;
                }
            }
        }
        if (i < 0) {
            createFullMatrix[0][i2] = 100;
        } else {
            createFullMatrix[i][i2] = 100;
        }
        return createFullMatrix;
    }

    private static int handleDpadHorizontal(int i, int i2, int i3, int[][] iArr, int i4, boolean z) {
        int i5 = i;
        int i6 = i2;
        int i7 = i3;
        int[][] iArr2 = iArr;
        int i8 = i4;
        if (iArr2 != null) {
            int i9 = 0;
            int i10 = -1;
            int i11 = -1;
            while (i9 < i6) {
                int i12 = i11;
                int i13 = i10;
                for (int i14 = 0; i14 < i7; i14++) {
                    if (iArr2[i9][i14] == i5) {
                        i13 = i9;
                        i12 = i14;
                    }
                }
                i9++;
                i10 = i13;
                i11 = i12;
            }
            int i15 = i10 + i8;
            int i16 = -1;
            while (i15 >= 0 && i15 < i6) {
                i16 = inspectMatrix(i15, i11, i6, i7, iArr2);
                if (i16 != -1 && i16 != -11) {
                    return i16;
                }
                i15 += i8;
            }
            int i17 = i16;
            boolean z2 = false;
            boolean z3 = false;
            for (int i18 = 1; i18 < i7; i18++) {
                int i19 = i18 * i8;
                int i20 = i11 + i19;
                int i21 = i11 - i19;
                int i22 = i19 + i10;
                if (inspectMatrix(i22, i20, i6, i7, iArr2) == -11) {
                    z2 = true;
                }
                if (inspectMatrix(i22, i21, i6, i7, iArr2) == -11) {
                    z3 = true;
                }
                while (i22 >= 0 && i22 < i6) {
                    int inspectMatrix = inspectMatrix(i22, ((!z2 || i22 >= i6 + -1) ? 0 : i8) + i20, i6, i7, iArr2);
                    if (inspectMatrix != -1) {
                        return inspectMatrix;
                    }
                    i17 = inspectMatrix(i22, ((!z3 || i22 >= i6 + -1) ? 0 : -i8) + i21, i6, i7, iArr2);
                    if (i17 != -1) {
                        return i17;
                    }
                    i22 += i8;
                }
            }
            if (i5 != 100) {
                return i17;
            }
            int i23 = -4;
            if (z) {
                if (i8 < 0) {
                    i23 = -8;
                }
                return i23;
            }
            if (i8 >= 0) {
                i23 = -8;
            }
            return i23;
        }
        throw new IllegalStateException("Dpad navigation requires a matrix.");
    }

    private static int handleDpadVertical(int i, int i2, int i3, int[][] iArr, int i4) {
        int i5 = i2;
        int i6 = i3;
        int[][] iArr2 = iArr;
        int i7 = i4;
        if (iArr2 != null) {
            int i8 = 0;
            int i9 = -1;
            int i10 = -1;
            while (i8 < i5) {
                int i11 = i10;
                int i12 = i9;
                for (int i13 = 0; i13 < i6; i13++) {
                    if (iArr2[i8][i13] == i) {
                        i11 = i8;
                        i12 = i13;
                    }
                }
                int i14 = i;
                i8++;
                i9 = i12;
                i10 = i11;
            }
            int i15 = i9 + i7;
            int i16 = -1;
            while (i15 >= 0 && i15 < i6 && i15 >= 0) {
                i16 = inspectMatrix(i10, i15, i5, i6, iArr2);
                if (i16 != -1 && i16 != -11) {
                    return i16;
                }
                i15 += i7;
            }
            int i17 = i16;
            boolean z = false;
            boolean z2 = false;
            for (int i18 = 1; i18 < i5; i18++) {
                int i19 = i18 * i7;
                int i20 = i10 + i19;
                int i21 = i10 - i19;
                int i22 = i19 + i9;
                if (inspectMatrix(i20, i22, i5, i6, iArr2) == -11) {
                    z = true;
                }
                if (inspectMatrix(i21, i22, i5, i6, iArr2) == -11) {
                    z2 = true;
                }
                while (i22 >= 0 && i22 < i6) {
                    int inspectMatrix = inspectMatrix(((!z || i22 >= i6 + -1) ? 0 : i7) + i20, i22, i5, i6, iArr2);
                    if (inspectMatrix != -1) {
                        return inspectMatrix;
                    }
                    i17 = inspectMatrix(((!z2 || i22 >= i6 + -1) ? 0 : -i7) + i21, i22, i5, i6, iArr2);
                    if (i17 != -1) {
                        return i17;
                    }
                    i22 += i7;
                }
            }
            return i17;
        }
        throw new IllegalStateException("Dpad navigation requires a matrix.");
    }

    private static int inspectMatrix(int i, int i2, int i3, int i4, int[][] iArr) {
        if (!isValid(i, i2, i3, i4) || iArr[i][i2] == -1) {
            return -1;
        }
        return iArr[i][i2];
    }

    private static String getStringIndex(int i) {
        switch (i) {
            case -11:
                return "ALL_APPS_COLUMN";
            case -9:
                return "NEXT_PAGE_LEFT_COLUMN";
            case -8:
                return "NEXT_PAGE_FIRST";
            case -7:
                return "CURRENT_PAGE_LAST";
            case -6:
                return "CURRENT_PAGE_FIRST";
            case -4:
                return "PREVIOUS_PAGE_LAST";
            case -3:
                return "PREVIOUS_PAGE_FIRST";
            case -2:
                return "PREVIOUS_PAGE_RIGHT_COLUMN";
            case -1:
                return "NOOP";
            default:
                return Integer.toString(i);
        }
    }

    private static void printMatrix(int[][] iArr) {
        Log.v(TAG, "\tprintMap:");
        int length = iArr[0].length;
        for (int i = 0; i < length; i++) {
            String str = "\t\t";
            for (int[] iArr2 : iArr) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(String.format("%3d", new Object[]{Integer.valueOf(iArr2[i])}));
                str = sb.toString();
            }
            Log.v(TAG, str);
        }
    }

    public static View getAdjacentChildInNextFolderPage(ShortcutAndWidgetContainer shortcutAndWidgetContainer, View view, int i) {
        int i2 = ((LayoutParams) view.getLayoutParams()).cellY;
        int i3 = 0;
        if (!((i == -9) ^ shortcutAndWidgetContainer.invertLayoutHorizontally())) {
            i3 = ((CellLayout) shortcutAndWidgetContainer.getParent()).getCountX() - 1;
        }
        while (i3 >= 0) {
            for (int i4 = i2; i4 >= 0; i4--) {
                View childAt = shortcutAndWidgetContainer.getChildAt(i3, i4);
                if (childAt != null) {
                    return childAt;
                }
            }
            i3--;
        }
        return null;
    }
}
