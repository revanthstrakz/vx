package com.android.launcher3.accessibility;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.config.FeatureFlags;

public class OverviewScreenAccessibilityDelegate extends AccessibilityDelegate {
    private static final int MOVE_BACKWARD = C0622R.C0625id.action_move_screen_backwards;
    private static final int MOVE_FORWARD = C0622R.C0625id.action_move_screen_forwards;
    private final SparseArray<AccessibilityAction> mActions = new SparseArray<>();
    private final Workspace mWorkspace;

    public OverviewScreenAccessibilityDelegate(Workspace workspace) {
        this.mWorkspace = workspace;
        Context context = this.mWorkspace.getContext();
        boolean isRtl = Utilities.isRtl(context.getResources());
        this.mActions.put(MOVE_BACKWARD, new AccessibilityAction(MOVE_BACKWARD, context.getText(isRtl ? C0622R.string.action_move_screen_right : C0622R.string.action_move_screen_left)));
        this.mActions.put(MOVE_FORWARD, new AccessibilityAction(MOVE_FORWARD, context.getText(isRtl ? C0622R.string.action_move_screen_left : C0622R.string.action_move_screen_right)));
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        if (view != null) {
            if (i == 64) {
                this.mWorkspace.setCurrentPage(this.mWorkspace.indexOfChild(view));
            } else if (i == MOVE_FORWARD) {
                movePage(this.mWorkspace.indexOfChild(view) + 1, view);
                return true;
            } else if (i == MOVE_BACKWARD) {
                movePage(this.mWorkspace.indexOfChild(view) - 1, view);
                return true;
            }
        }
        return super.performAccessibilityAction(view, i, bundle);
    }

    private void movePage(int i, View view) {
        this.mWorkspace.onStartReordering();
        this.mWorkspace.removeView(view);
        this.mWorkspace.addView(view, i);
        this.mWorkspace.onEndReordering();
        this.mWorkspace.announceForAccessibility(this.mWorkspace.getContext().getText(C0622R.string.screen_moved));
        this.mWorkspace.updateAccessibilityFlags();
        view.performAccessibilityAction(64, null);
    }

    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        int indexOfChild = this.mWorkspace.indexOfChild(view);
        if (indexOfChild < this.mWorkspace.getChildCount() - 1) {
            accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(MOVE_FORWARD));
        }
        if (indexOfChild > this.mWorkspace.numCustomPages() + (FeatureFlags.QSB_ON_FIRST_SCREEN ? 1 : 0)) {
            accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(MOVE_BACKWARD));
        }
    }
}
