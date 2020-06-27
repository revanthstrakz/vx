package com.google.android.apps.nexuslauncher;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.popup.SystemShortcut.Custom;

public class CustomEditShortcut extends Custom {
    public CustomEditShortcut(Context context) {
    }

    public OnClickListener getOnClickListener(final Launcher launcher, final ItemInfo itemInfo) {
        if (!CustomIconUtils.usingValidPack(launcher)) {
            return null;
        }
        ((CustomDrawableFactory) DrawableFactory.get(launcher)).ensureInitialLoadComplete();
        return new OnClickListener() {
            private boolean mOpened = false;

            public void onClick(View view) {
                if (!this.mOpened) {
                    this.mOpened = true;
                    AbstractFloatingView.closeAllOpenViews(launcher);
                    ((CustomBottomSheet) launcher.getLayoutInflater().inflate(C0622R.layout.app_edit_bottom_sheet, launcher.getDragLayer(), false)).populateAndShow(itemInfo);
                }
            }
        };
    }
}
