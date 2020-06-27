package com.android.launcher3.accessibility;

import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.folder.FolderPagedView;

public class FolderAccessibilityHelper extends DragAndDropAccessibilityDelegate {
    private final FolderPagedView mParent;
    private final int mStartPosition;

    public FolderAccessibilityHelper(CellLayout cellLayout) {
        super(cellLayout);
        this.mParent = (FolderPagedView) cellLayout.getParent();
        this.mStartPosition = this.mParent.indexOfChild(cellLayout) * cellLayout.getCountX() * cellLayout.getCountY();
    }

    /* access modifiers changed from: protected */
    public int intersectsValidDropTarget(int i) {
        return Math.min(i, (this.mParent.getAllocatedContentSize() - this.mStartPosition) - 1);
    }

    /* access modifiers changed from: protected */
    public String getLocationDescriptionForIconDrop(int i) {
        return this.mContext.getString(C0622R.string.move_to_position, new Object[]{Integer.valueOf(i + this.mStartPosition + 1)});
    }

    /* access modifiers changed from: protected */
    public String getConfirmationForIconDrop(int i) {
        return this.mContext.getString(C0622R.string.item_moved);
    }
}
