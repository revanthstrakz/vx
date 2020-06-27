package com.google.android.apps.nexuslauncher.smartspace;

import android.view.View;
import android.view.View.OnClickListener;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.popup.SystemShortcut;

class SmartspacePreferencesShortcut extends SystemShortcut {
    SmartspacePreferencesShortcut() {
        super(C0622R.C0624drawable.ic_smartspace_preferences, C0622R.string.smartspace_preferences);
    }

    public OnClickListener getOnClickListener(final Launcher launcher, ItemInfo itemInfo) {
        return new OnClickListener() {
            public void onClick(View view) {
                SmartspaceController.get(view.getContext()).mo12985cZ();
                AbstractFloatingView.closeAllOpenViews(launcher);
            }
        };
    }
}
