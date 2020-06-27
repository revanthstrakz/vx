package com.android.launcher3;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;

public class DeleteDropTarget extends ButtonDropTarget {
    /* access modifiers changed from: protected */
    public boolean supportsDrop(DragSource dragSource, ItemInfo itemInfo) {
        return true;
    }

    public DeleteDropTarget(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DeleteDropTarget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHoverColor = getResources().getColor(C0622R.color.delete_target_hover_tint);
        setDrawable(C0622R.C0624drawable.ic_remove_shadow);
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        super.onDragStart(dragObject, dragOptions);
        setTextBasedOnDragSource(dragObject.dragSource);
    }

    public static boolean supportsAccessibleDrop(ItemInfo itemInfo) {
        return (itemInfo instanceof ShortcutInfo) || (itemInfo instanceof LauncherAppWidgetInfo) || (itemInfo instanceof FolderInfo);
    }

    public void setTextBasedOnDragSource(DragSource dragSource) {
        if (!TextUtils.isEmpty(this.mText)) {
            this.mText = getResources().getString(dragSource.supportsDeleteDropTarget() ? C0622R.string.remove_drop_target_label : 17039360);
            requestLayout();
        }
    }

    public void completeDrop(DragObject dragObject) {
        ItemInfo itemInfo = dragObject.dragInfo;
        if ((dragObject.dragSource instanceof Workspace) || (dragObject.dragSource instanceof Folder)) {
            removeWorkspaceOrFolderItem(this.mLauncher, itemInfo, null);
        }
    }

    public static void removeWorkspaceOrFolderItem(Launcher launcher, ItemInfo itemInfo, View view) {
        launcher.removeItem(view, itemInfo, true);
        launcher.getWorkspace().stripEmptyScreens();
        launcher.getDragLayer().announceForAccessibility(launcher.getString(C0622R.string.item_removed));
    }
}
