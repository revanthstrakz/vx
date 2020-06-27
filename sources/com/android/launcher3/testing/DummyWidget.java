package com.android.launcher3.testing;

import com.android.launcher3.C0622R;
import com.android.launcher3.CustomAppWidget;

public class DummyWidget implements CustomAppWidget {
    public int getIcon() {
        return 0;
    }

    public String getLabel() {
        return "Dumb Launcher Widget";
    }

    public int getMinSpanX() {
        return 1;
    }

    public int getMinSpanY() {
        return 1;
    }

    public int getPreviewImage() {
        return 0;
    }

    public int getResizeMode() {
        return 3;
    }

    public int getSpanX() {
        return 2;
    }

    public int getSpanY() {
        return 2;
    }

    public int getWidgetLayout() {
        return C0622R.layout.zzz_dummy_widget;
    }
}
