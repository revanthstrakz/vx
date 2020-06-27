package com.android.launcher3.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.support.p001v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.View;
import com.android.launcher3.AppInfo;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate.DragInfo;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate.DragType;
import com.android.launcher3.dragndrop.DragLayer;
import java.util.Iterator;

public class WorkspaceAccessibilityHelper extends DragAndDropAccessibilityDelegate {
    private final int[] mTempCords = new int[2];
    private final Rect mTempRect = new Rect();

    public WorkspaceAccessibilityHelper(CellLayout cellLayout) {
        super(cellLayout);
    }

    /* access modifiers changed from: protected */
    public int intersectsValidDropTarget(int i) {
        int countX = this.mView.getCountX();
        int countY = this.mView.getCountY();
        int i2 = i % countX;
        int i3 = i / countX;
        DragInfo dragInfo = this.mDelegate.getDragInfo();
        if (dragInfo.dragType == DragType.WIDGET && !this.mView.acceptsWidget()) {
            return -1;
        }
        if (dragInfo.dragType == DragType.WIDGET) {
            int i4 = dragInfo.info.spanX;
            int i5 = dragInfo.info.spanY;
            for (int i6 = 0; i6 < i4; i6++) {
                for (int i7 = 0; i7 < i5; i7++) {
                    int i8 = i2 - i6;
                    int i9 = i3 - i7;
                    if (i8 >= 0 && i9 >= 0) {
                        boolean z = true;
                        for (int i10 = i8; i10 < i8 + i4 && z; i10++) {
                            int i11 = i9;
                            while (true) {
                                if (i11 >= i9 + i5) {
                                    break;
                                } else if (i10 >= countX || i11 >= countY || this.mView.isOccupied(i10, i11)) {
                                    z = false;
                                } else {
                                    i11++;
                                }
                            }
                            z = false;
                        }
                        if (z) {
                            return i8 + (countX * i9);
                        }
                    }
                }
            }
            return -1;
        }
        View childAt = this.mView.getChildAt(i2, i3);
        if (childAt == null || childAt == dragInfo.item) {
            return i;
        }
        if (dragInfo.dragType != DragType.FOLDER) {
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if ((itemInfo instanceof AppInfo) || (itemInfo instanceof FolderInfo) || (itemInfo instanceof ShortcutInfo)) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public String getConfirmationForIconDrop(int i) {
        int countX = i % this.mView.getCountX();
        int countX2 = i / this.mView.getCountX();
        DragInfo dragInfo = this.mDelegate.getDragInfo();
        View childAt = this.mView.getChildAt(countX, countX2);
        if (childAt == null || childAt == dragInfo.item) {
            return this.mContext.getString(C0622R.string.item_moved);
        }
        ItemInfo itemInfo = (ItemInfo) childAt.getTag();
        if ((itemInfo instanceof AppInfo) || (itemInfo instanceof ShortcutInfo)) {
            return this.mContext.getString(C0622R.string.folder_created);
        }
        return itemInfo instanceof FolderInfo ? this.mContext.getString(C0622R.string.added_to_folder) : "";
    }

    /* access modifiers changed from: protected */
    public void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        super.onPopulateNodeForVirtualView(i, accessibilityNodeInfoCompat);
        DragLayer dragLayer = Launcher.getLauncher(this.mView.getContext()).getDragLayer();
        int[] iArr = this.mTempCords;
        this.mTempCords[1] = 0;
        iArr[0] = 0;
        float descendantCoordRelativeToSelf = dragLayer.getDescendantCoordRelativeToSelf(this.mView, this.mTempCords);
        accessibilityNodeInfoCompat.getBoundsInParent(this.mTempRect);
        this.mTempRect.left = this.mTempCords[0] + ((int) (((float) this.mTempRect.left) * descendantCoordRelativeToSelf));
        this.mTempRect.right = this.mTempCords[0] + ((int) (((float) this.mTempRect.right) * descendantCoordRelativeToSelf));
        this.mTempRect.top = this.mTempCords[1] + ((int) (((float) this.mTempRect.top) * descendantCoordRelativeToSelf));
        this.mTempRect.bottom = this.mTempCords[1] + ((int) (((float) this.mTempRect.bottom) * descendantCoordRelativeToSelf));
        accessibilityNodeInfoCompat.setBoundsInScreen(this.mTempRect);
    }

    /* access modifiers changed from: protected */
    public String getLocationDescriptionForIconDrop(int i) {
        int countX = i % this.mView.getCountX();
        int countX2 = i / this.mView.getCountX();
        DragInfo dragInfo = this.mDelegate.getDragInfo();
        View childAt = this.mView.getChildAt(countX, countX2);
        if (childAt == null || childAt == dragInfo.item) {
            return this.mView.getItemMoveDescription(countX, countX2);
        }
        return getDescriptionForDropOver(childAt, this.mContext);
    }

    public static String getDescriptionForDropOver(View view, Context context) {
        ItemInfo itemInfo = (ItemInfo) view.getTag();
        if (itemInfo instanceof ShortcutInfo) {
            return context.getString(C0622R.string.create_folder_with, new Object[]{itemInfo.title});
        } else if (!(itemInfo instanceof FolderInfo)) {
            return "";
        } else {
            if (TextUtils.isEmpty(itemInfo.title)) {
                ShortcutInfo shortcutInfo = null;
                Iterator it = ((FolderInfo) itemInfo).contents.iterator();
                while (it.hasNext()) {
                    ShortcutInfo shortcutInfo2 = (ShortcutInfo) it.next();
                    if (shortcutInfo == null || shortcutInfo.rank > shortcutInfo2.rank) {
                        shortcutInfo = shortcutInfo2;
                    }
                }
                if (shortcutInfo != null) {
                    return context.getString(C0622R.string.add_to_folder_with_app, new Object[]{shortcutInfo.title});
                }
            }
            return context.getString(C0622R.string.add_to_folder, new Object[]{itemInfo.title});
        }
    }
}
