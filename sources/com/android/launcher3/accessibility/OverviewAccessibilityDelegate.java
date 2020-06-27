package com.android.launcher3.accessibility;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.launcher3.C0622R;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;

public class OverviewAccessibilityDelegate extends AccessibilityDelegate {
    private static final int OVERVIEW = C0622R.string.accessibility_action_overview;
    private static final int SETTINGS = C0622R.string.settings_button_text;
    private static final int WALLPAPERS = C0622R.string.wallpaper_button_text;
    private static final int WIDGETS = C0622R.string.widget_button_text;

    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        Context context = view.getContext();
        accessibilityNodeInfo.addAction(new AccessibilityAction(OVERVIEW, context.getText(OVERVIEW)));
        if (Utilities.isWallpaperAllowed(context)) {
            accessibilityNodeInfo.addAction(new AccessibilityAction(WALLPAPERS, context.getText(WALLPAPERS)));
        }
        accessibilityNodeInfo.addAction(new AccessibilityAction(WIDGETS, context.getText(WIDGETS)));
        accessibilityNodeInfo.addAction(new AccessibilityAction(SETTINGS, context.getText(SETTINGS)));
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        Launcher launcher = Launcher.getLauncher(view.getContext());
        if (i == OVERVIEW) {
            launcher.showOverviewMode(true);
            return true;
        } else if (i == WALLPAPERS) {
            launcher.onClickWallpaperPicker(view);
            return true;
        } else if (i == WIDGETS) {
            launcher.onClickAddWidgetButton(view);
            return true;
        } else if (i != SETTINGS) {
            return super.performAccessibilityAction(view, i, bundle);
        } else {
            launcher.onClickSettingsButton(view);
            return true;
        }
    }
}
