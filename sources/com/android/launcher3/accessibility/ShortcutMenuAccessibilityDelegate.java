package com.android.launcher3.accessibility;

import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.C0622R;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.notification.NotificationMainView;
import com.android.launcher3.shortcuts.DeepShortcutView;
import java.util.ArrayList;

public class ShortcutMenuAccessibilityDelegate extends LauncherAccessibilityDelegate {
    private static final int DISMISS_NOTIFICATION = C0622R.C0625id.action_dismiss_notification;

    public ShortcutMenuAccessibilityDelegate(Launcher launcher) {
        super(launcher);
        this.mActions.put(DISMISS_NOTIFICATION, new AccessibilityAction(DISMISS_NOTIFICATION, launcher.getText(C0622R.string.action_dismiss_notification)));
    }

    public void addSupportedActions(View view, AccessibilityNodeInfo accessibilityNodeInfo, boolean z) {
        if (view.getParent() instanceof DeepShortcutView) {
            accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(ADD_TO_WORKSPACE));
        } else if ((view instanceof NotificationMainView) && ((NotificationMainView) view).canChildBeDismissed()) {
            accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(DISMISS_NOTIFICATION));
        }
    }

    public boolean performAction(View view, ItemInfo itemInfo, int i) {
        if (i == ADD_TO_WORKSPACE) {
            if (!(view.getParent() instanceof DeepShortcutView)) {
                return false;
            }
            final ShortcutInfo finalInfo = ((DeepShortcutView) view.getParent()).getFinalInfo();
            final int[] iArr = new int[2];
            final long findSpaceOnWorkspace = findSpaceOnWorkspace(itemInfo, iArr);
            C06631 r2 = new Runnable() {
                public void run() {
                    ShortcutMenuAccessibilityDelegate.this.mLauncher.getModelWriter().addItemToDatabase(finalInfo, -100, findSpaceOnWorkspace, iArr[0], iArr[1]);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(finalInfo);
                    ShortcutMenuAccessibilityDelegate.this.mLauncher.bindItems(arrayList, true);
                    AbstractFloatingView.closeAllOpenViews(ShortcutMenuAccessibilityDelegate.this.mLauncher);
                    ShortcutMenuAccessibilityDelegate.this.announceConfirmation(C0622R.string.item_added_to_workspace);
                }
            };
            if (!this.mLauncher.showWorkspace(true, r2)) {
                r2.run();
            }
            return true;
        } else if (i != DISMISS_NOTIFICATION || !(view instanceof NotificationMainView)) {
            return false;
        } else {
            ((NotificationMainView) view).onChildDismissed();
            announceConfirmation(C0622R.string.notification_dismissed);
            return true;
        }
    }
}
