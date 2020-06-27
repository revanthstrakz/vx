package com.android.launcher3.util;

import android.view.Window;
import com.android.launcher3.Utilities;

public class SystemUiController {
    public static final int FLAG_DARK_NAV = 2;
    public static final int FLAG_DARK_STATUS = 8;
    public static final int FLAG_LIGHT_NAV = 1;
    public static final int FLAG_LIGHT_STATUS = 4;
    public static final int UI_STATE_ALL_APPS = 1;
    public static final int UI_STATE_BASE_WINDOW = 0;
    public static final int UI_STATE_ROOT_VIEW = 3;
    public static final int UI_STATE_WIDGET_BOTTOM_SHEET = 2;
    private final int[] mStates = new int[4];
    private final Window mWindow;

    public SystemUiController(Window window) {
        this.mWindow = window;
    }

    public void updateUiState(int i, boolean z) {
        updateUiState(i, z ? 5 : 10);
    }

    public void updateUiState(int i, int i2) {
        int[] iArr;
        if (this.mStates[i] != i2) {
            this.mStates[i] = i2;
            int systemUiVisibility = this.mWindow.getDecorView().getSystemUiVisibility();
            int i3 = systemUiVisibility;
            for (int i4 : this.mStates) {
                if (Utilities.ATLEAST_OREO) {
                    if ((i4 & 1) != 0) {
                        i3 |= 16;
                    } else if ((i4 & 2) != 0) {
                        i3 &= -17;
                    }
                }
                if ((i4 & 4) != 0) {
                    i3 |= 8192;
                } else if ((i4 & 8) != 0) {
                    i3 &= -8193;
                }
            }
            if (i3 != systemUiVisibility) {
                this.mWindow.getDecorView().setSystemUiVisibility(i3);
            }
        }
    }
}
