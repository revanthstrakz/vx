package com.android.launcher3.accessibility;

import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Launcher;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragOptions;

public class AccessibleDragListenerAdapter implements DragListener {
    private final int mDragType;
    private final ViewGroup mViewGroup;

    public AccessibleDragListenerAdapter(ViewGroup viewGroup, int i) {
        this.mViewGroup = viewGroup;
        this.mDragType = i;
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        enableAccessibleDrag(true);
    }

    public void onDragEnd() {
        enableAccessibleDrag(false);
        Launcher.getLauncher(this.mViewGroup.getContext()).getDragController().removeDragListener(this);
    }

    /* access modifiers changed from: protected */
    public void enableAccessibleDrag(boolean z) {
        for (int i = 0; i < this.mViewGroup.getChildCount(); i++) {
            setEnableForLayout((CellLayout) this.mViewGroup.getChildAt(i), z);
        }
    }

    /* access modifiers changed from: protected */
    public final void setEnableForLayout(CellLayout cellLayout, boolean z) {
        cellLayout.enableAccessibleDrag(z, this.mDragType);
    }
}
