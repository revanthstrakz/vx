package com.android.launcher3.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.p001v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.p001v4.widget.ExploreByTouchHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityEvent;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate.DragInfo;
import java.util.List;

public abstract class DragAndDropAccessibilityDelegate extends ExploreByTouchHelper implements OnClickListener {
    protected static final int INVALID_POSITION = -1;
    private static final int[] sTempArray = new int[2];
    protected final Context mContext;
    protected final LauncherAccessibilityDelegate mDelegate;
    private final Rect mTempRect = new Rect();
    protected final CellLayout mView;

    /* access modifiers changed from: protected */
    public abstract String getConfirmationForIconDrop(int i);

    /* access modifiers changed from: protected */
    public abstract String getLocationDescriptionForIconDrop(int i);

    /* access modifiers changed from: protected */
    public abstract int intersectsValidDropTarget(int i);

    public DragAndDropAccessibilityDelegate(CellLayout cellLayout) {
        super(cellLayout);
        this.mView = cellLayout;
        this.mContext = this.mView.getContext();
        this.mDelegate = Launcher.getLauncher(this.mContext).getAccessibilityDelegate();
    }

    /* access modifiers changed from: protected */
    public int getVirtualViewAt(float f, float f2) {
        if (f < 0.0f || f2 < 0.0f || f > ((float) this.mView.getMeasuredWidth()) || f2 > ((float) this.mView.getMeasuredHeight())) {
            return Integer.MIN_VALUE;
        }
        this.mView.pointToCellExact((int) f, (int) f2, sTempArray);
        return intersectsValidDropTarget(sTempArray[0] + (sTempArray[1] * this.mView.getCountX()));
    }

    /* access modifiers changed from: protected */
    public void getVisibleVirtualViews(List<Integer> list) {
        int countX = this.mView.getCountX() * this.mView.getCountY();
        for (int i = 0; i < countX; i++) {
            if (intersectsValidDropTarget(i) == i) {
                list.add(Integer.valueOf(i));
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
        if (i2 != 16 || i == Integer.MIN_VALUE) {
            return false;
        }
        this.mDelegate.handleAccessibleDrop(this.mView, getItemBounds(i), getConfirmationForIconDrop(i));
        return true;
    }

    public void onClick(View view) {
        onPerformActionForVirtualView(getFocusedVirtualView(), 16, null);
    }

    /* access modifiers changed from: protected */
    public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
        if (i != Integer.MIN_VALUE) {
            accessibilityEvent.setContentDescription(this.mContext.getString(C0622R.string.action_move_here));
            return;
        }
        throw new IllegalArgumentException("Invalid virtual view id");
    }

    /* access modifiers changed from: protected */
    public void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        if (i != Integer.MIN_VALUE) {
            accessibilityNodeInfoCompat.setContentDescription(getLocationDescriptionForIconDrop(i));
            accessibilityNodeInfoCompat.setBoundsInParent(getItemBounds(i));
            accessibilityNodeInfoCompat.addAction(16);
            accessibilityNodeInfoCompat.setClickable(true);
            accessibilityNodeInfoCompat.setFocusable(true);
            return;
        }
        throw new IllegalArgumentException("Invalid virtual view id");
    }

    private Rect getItemBounds(int i) {
        int countX = i % this.mView.getCountX();
        int countX2 = i / this.mView.getCountX();
        DragInfo dragInfo = this.mDelegate.getDragInfo();
        this.mView.cellToRect(countX, countX2, dragInfo.info.spanX, dragInfo.info.spanY, this.mTempRect);
        return this.mTempRect;
    }
}
