package com.android.launcher3.accessibility;

import android.app.AlertDialog.Builder;
import android.appwidget.AppWidgetProviderInfo;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.launcher3.AppInfo;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.CellLayout.CellInfo;
import com.android.launcher3.CellLayout.LayoutParams;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.InfoDropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.UninstallDropTarget;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import java.util.ArrayList;

public class LauncherAccessibilityDelegate extends AccessibilityDelegate implements DragListener {
    protected static final int ADD_TO_WORKSPACE = C0622R.C0625id.action_add_to_workspace;
    public static final int DEEP_SHORTCUTS = C0622R.C0625id.action_deep_shortcuts;
    protected static final int INFO = C0622R.C0625id.action_info;
    protected static final int MOVE = C0622R.C0625id.action_move;
    protected static final int MOVE_TO_WORKSPACE = C0622R.C0625id.action_move_to_workspace;
    protected static final int REMOVE = C0622R.C0625id.action_remove;
    protected static final int RESIZE = C0622R.C0625id.action_resize;
    private static final String TAG = "LauncherAccessibilityDelegate";
    protected static final int UNINSTALL = C0622R.C0625id.action_uninstall;
    protected final SparseArray<AccessibilityAction> mActions = new SparseArray<>();
    private DragInfo mDragInfo = null;
    final Launcher mLauncher;

    public static class DragInfo {
        public DragType dragType;
        public ItemInfo info;
        public View item;
    }

    public enum DragType {
        ICON,
        FOLDER,
        WIDGET
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
    }

    public LauncherAccessibilityDelegate(Launcher launcher) {
        this.mLauncher = launcher;
        this.mActions.put(REMOVE, new AccessibilityAction(REMOVE, launcher.getText(C0622R.string.remove_drop_target_label)));
        this.mActions.put(INFO, new AccessibilityAction(INFO, launcher.getText(C0622R.string.app_info_drop_target_label)));
        this.mActions.put(UNINSTALL, new AccessibilityAction(UNINSTALL, launcher.getText(C0622R.string.uninstall_drop_target_label)));
        this.mActions.put(ADD_TO_WORKSPACE, new AccessibilityAction(ADD_TO_WORKSPACE, launcher.getText(C0622R.string.action_add_to_workspace)));
        this.mActions.put(MOVE, new AccessibilityAction(MOVE, launcher.getText(C0622R.string.action_move)));
        this.mActions.put(MOVE_TO_WORKSPACE, new AccessibilityAction(MOVE_TO_WORKSPACE, launcher.getText(C0622R.string.action_move_to_workspace)));
        this.mActions.put(RESIZE, new AccessibilityAction(RESIZE, launcher.getText(C0622R.string.action_resize)));
        this.mActions.put(DEEP_SHORTCUTS, new AccessibilityAction(DEEP_SHORTCUTS, launcher.getText(C0622R.string.action_deep_shortcut)));
    }

    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        addSupportedActions(view, accessibilityNodeInfo, false);
    }

    public void addSupportedActions(View view, AccessibilityNodeInfo accessibilityNodeInfo, boolean z) {
        if (view.getTag() instanceof ItemInfo) {
            ItemInfo itemInfo = (ItemInfo) view.getTag();
            if (!z && DeepShortcutManager.supportsShortcuts(itemInfo)) {
                accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(DEEP_SHORTCUTS));
            }
            if (DeleteDropTarget.supportsAccessibleDrop(itemInfo)) {
                accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(REMOVE));
            }
            if (UninstallDropTarget.supportsDrop(view.getContext(), itemInfo)) {
                accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(UNINSTALL));
            }
            if (InfoDropTarget.supportsDrop(view.getContext(), itemInfo)) {
                accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(INFO));
            }
            if (!z && ((itemInfo instanceof ShortcutInfo) || (itemInfo instanceof LauncherAppWidgetInfo) || (itemInfo instanceof FolderInfo))) {
                accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(MOVE));
                if (itemInfo.container >= 0) {
                    accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(MOVE_TO_WORKSPACE));
                } else if ((itemInfo instanceof LauncherAppWidgetInfo) && !getSupportedResizeActions(view, (LauncherAppWidgetInfo) itemInfo).isEmpty()) {
                    accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(RESIZE));
                }
            }
            if ((itemInfo instanceof AppInfo) || (itemInfo instanceof PendingAddItemInfo)) {
                accessibilityNodeInfo.addAction((AccessibilityAction) this.mActions.get(ADD_TO_WORKSPACE));
            }
        }
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        if (!(view.getTag() instanceof ItemInfo) || !performAction(view, (ItemInfo) view.getTag(), i)) {
            return super.performAccessibilityAction(view, i, bundle);
        }
        return true;
    }

    public boolean performAction(final View view, final ItemInfo itemInfo, int i) {
        boolean z = true;
        if (i == REMOVE) {
            DeleteDropTarget.removeWorkspaceOrFolderItem(this.mLauncher, itemInfo, view);
            return true;
        } else if (i == INFO) {
            InfoDropTarget.startDetailsActivityForInfo(itemInfo, this.mLauncher, null);
            return true;
        } else if (i == UNINSTALL) {
            return UninstallDropTarget.startUninstallActivity(this.mLauncher, itemInfo);
        } else {
            if (i == MOVE) {
                beginAccessibleDrag(view, itemInfo);
            } else if (i == ADD_TO_WORKSPACE) {
                final int[] iArr = new int[2];
                final long findSpaceOnWorkspace = findSpaceOnWorkspace(itemInfo, iArr);
                Launcher launcher = this.mLauncher;
                final ItemInfo itemInfo2 = itemInfo;
                C06601 r4 = new Runnable() {
                    public void run() {
                        if (itemInfo2 instanceof AppInfo) {
                            ShortcutInfo makeShortcut = ((AppInfo) itemInfo2).makeShortcut();
                            LauncherAccessibilityDelegate.this.mLauncher.getModelWriter().addItemToDatabase(makeShortcut, -100, findSpaceOnWorkspace, iArr[0], iArr[1]);
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(makeShortcut);
                            LauncherAccessibilityDelegate.this.mLauncher.bindItems(arrayList, true);
                        } else if (itemInfo2 instanceof PendingAddItemInfo) {
                            PendingAddItemInfo pendingAddItemInfo = (PendingAddItemInfo) itemInfo2;
                            Workspace workspace = LauncherAccessibilityDelegate.this.mLauncher.getWorkspace();
                            workspace.snapToPage(workspace.getPageIndexForScreenId(findSpaceOnWorkspace));
                            LauncherAccessibilityDelegate.this.mLauncher.addPendingItem(pendingAddItemInfo, -100, findSpaceOnWorkspace, iArr, pendingAddItemInfo.spanX, pendingAddItemInfo.spanY);
                        }
                        LauncherAccessibilityDelegate.this.announceConfirmation(C0622R.string.item_added_to_workspace);
                    }
                };
                launcher.showWorkspace(true, r4);
                return true;
            } else if (i == MOVE_TO_WORKSPACE) {
                Folder open = Folder.getOpen(this.mLauncher);
                open.close(true);
                ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                open.getInfo().remove(shortcutInfo, false);
                int[] iArr2 = new int[2];
                this.mLauncher.getModelWriter().moveItemInDatabase(shortcutInfo, -100, findSpaceOnWorkspace(itemInfo, iArr2), iArr2[0], iArr2[1]);
                new Handler().post(new Runnable() {
                    public void run() {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(itemInfo);
                        LauncherAccessibilityDelegate.this.mLauncher.bindItems(arrayList, true);
                        LauncherAccessibilityDelegate.this.announceConfirmation(C0622R.string.item_moved);
                    }
                });
            } else if (i == RESIZE) {
                final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
                final ArrayList supportedResizeActions = getSupportedResizeActions(view, launcherAppWidgetInfo);
                CharSequence[] charSequenceArr = new CharSequence[supportedResizeActions.size()];
                for (int i2 = 0; i2 < supportedResizeActions.size(); i2++) {
                    charSequenceArr[i2] = this.mLauncher.getText(((Integer) supportedResizeActions.get(i2)).intValue());
                }
                new Builder(this.mLauncher).setTitle(C0622R.string.action_resize).setItems(charSequenceArr, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LauncherAccessibilityDelegate.this.performResizeAction(((Integer) supportedResizeActions.get(i)).intValue(), view, launcherAppWidgetInfo);
                        dialogInterface.dismiss();
                    }
                }).show();
                return true;
            } else if (i == DEEP_SHORTCUTS) {
                if (PopupContainerWithArrow.showForIcon((BubbleTextView) view) == null) {
                    z = false;
                }
                return z;
            }
            return false;
        }
    }

    private ArrayList<Integer> getSupportedResizeActions(View view, LauncherAppWidgetInfo launcherAppWidgetInfo) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        AppWidgetProviderInfo appWidgetInfo = ((LauncherAppWidgetHostView) view).getAppWidgetInfo();
        if (appWidgetInfo == null) {
            return arrayList;
        }
        CellLayout cellLayout = (CellLayout) view.getParent().getParent();
        if ((appWidgetInfo.resizeMode & 1) != 0) {
            if (cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX + launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY) || cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX - 1, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) {
                arrayList.add(Integer.valueOf(C0622R.string.action_increase_width));
            }
            if (launcherAppWidgetInfo.spanX > launcherAppWidgetInfo.minSpanX && launcherAppWidgetInfo.spanX > 1) {
                arrayList.add(Integer.valueOf(C0622R.string.action_decrease_width));
            }
        }
        if ((appWidgetInfo.resizeMode & 2) != 0) {
            if (cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY + launcherAppWidgetInfo.spanY, launcherAppWidgetInfo.spanX, 1) || cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY - 1, launcherAppWidgetInfo.spanX, 1)) {
                arrayList.add(Integer.valueOf(C0622R.string.action_increase_height));
            }
            if (launcherAppWidgetInfo.spanY > launcherAppWidgetInfo.minSpanY && launcherAppWidgetInfo.spanY > 1) {
                arrayList.add(Integer.valueOf(C0622R.string.action_decrease_height));
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public void performResizeAction(int i, View view, LauncherAppWidgetInfo launcherAppWidgetInfo) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        CellLayout cellLayout = (CellLayout) view.getParent().getParent();
        cellLayout.markCellsAsUnoccupiedForView(view);
        if (i == C0622R.string.action_increase_width) {
            if ((view.getLayoutDirection() == 1 && cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX - 1, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) || !cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX + launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) {
                layoutParams.cellX--;
                launcherAppWidgetInfo.cellX--;
            }
            layoutParams.cellHSpan++;
            launcherAppWidgetInfo.spanX++;
        } else if (i == C0622R.string.action_decrease_width) {
            layoutParams.cellHSpan--;
            launcherAppWidgetInfo.spanX--;
        } else if (i == C0622R.string.action_increase_height) {
            if (!cellLayout.isRegionVacant(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY + launcherAppWidgetInfo.spanY, launcherAppWidgetInfo.spanX, 1)) {
                layoutParams.cellY--;
                launcherAppWidgetInfo.cellY--;
            }
            layoutParams.cellVSpan++;
            launcherAppWidgetInfo.spanY++;
        } else if (i == C0622R.string.action_decrease_height) {
            layoutParams.cellVSpan--;
            launcherAppWidgetInfo.spanY--;
        }
        cellLayout.markCellsAsOccupiedForView(view);
        Rect rect = new Rect();
        AppWidgetResizeFrame.getWidgetSizeRanges(this.mLauncher, launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.spanY, rect);
        ((LauncherAppWidgetHostView) view).updateAppWidgetSize(null, rect.left, rect.top, rect.right, rect.bottom);
        view.requestLayout();
        this.mLauncher.getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
        announceConfirmation(this.mLauncher.getString(C0622R.string.widget_resized, new Object[]{Integer.valueOf(launcherAppWidgetInfo.spanX), Integer.valueOf(launcherAppWidgetInfo.spanY)}));
    }

    /* access modifiers changed from: 0000 */
    public void announceConfirmation(int i) {
        announceConfirmation(this.mLauncher.getResources().getString(i));
    }

    /* access modifiers changed from: 0000 */
    public void announceConfirmation(String str) {
        this.mLauncher.getDragLayer().announceForAccessibility(str);
    }

    public boolean isInAccessibleDrag() {
        return this.mDragInfo != null;
    }

    public DragInfo getDragInfo() {
        return this.mDragInfo;
    }

    public void handleAccessibleDrop(View view, Rect rect, String str) {
        if (isInAccessibleDrag()) {
            int[] iArr = new int[2];
            if (rect == null) {
                iArr[0] = view.getWidth() / 2;
                iArr[1] = view.getHeight() / 2;
            } else {
                iArr[0] = rect.centerX();
                iArr[1] = rect.centerY();
            }
            this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(view, iArr);
            this.mLauncher.getDragController().completeAccessibleDrag(iArr);
            if (!TextUtils.isEmpty(str)) {
                announceConfirmation(str);
            }
        }
    }

    public void beginAccessibleDrag(View view, ItemInfo itemInfo) {
        this.mDragInfo = new DragInfo();
        this.mDragInfo.info = itemInfo;
        this.mDragInfo.item = view;
        this.mDragInfo.dragType = DragType.ICON;
        if (itemInfo instanceof FolderInfo) {
            this.mDragInfo.dragType = DragType.FOLDER;
        } else if (itemInfo instanceof LauncherAppWidgetInfo) {
            this.mDragInfo.dragType = DragType.WIDGET;
        }
        CellInfo cellInfo = new CellInfo(view, itemInfo);
        Rect rect = new Rect();
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(view, rect);
        this.mLauncher.getDragController().prepareAccessibleDrag(rect.centerX(), rect.centerY());
        Folder open = Folder.getOpen(this.mLauncher);
        if (open != null && !open.getItemsInReadingOrder().contains(view)) {
            open.close(true);
            open = null;
        }
        this.mLauncher.getDragController().addDragListener(this);
        DragOptions dragOptions = new DragOptions();
        dragOptions.isAccessibleDrag = true;
        if (open != null) {
            open.startDrag(cellInfo.cell, dragOptions);
        } else {
            this.mLauncher.getWorkspace().startDrag(cellInfo, dragOptions);
        }
    }

    public void onDragEnd() {
        this.mLauncher.getDragController().removeDragListener(this);
        this.mDragInfo = null;
    }

    /* access modifiers changed from: protected */
    public long findSpaceOnWorkspace(ItemInfo itemInfo, int[] iArr) {
        Workspace workspace = this.mLauncher.getWorkspace();
        ArrayList screenOrder = workspace.getScreenOrder();
        int currentPage = workspace.getCurrentPage();
        long longValue = ((Long) screenOrder.get(currentPage)).longValue();
        boolean findCellForSpan = ((CellLayout) workspace.getPageAt(currentPage)).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY);
        int hasCustomContent = workspace.hasCustomContent();
        while (!findCellForSpan && hasCustomContent < screenOrder.size()) {
            longValue = ((Long) screenOrder.get(hasCustomContent)).longValue();
            findCellForSpan = ((CellLayout) workspace.getPageAt(hasCustomContent)).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY);
            hasCustomContent++;
        }
        if (findCellForSpan) {
            return longValue;
        }
        workspace.addExtraEmptyScreen();
        long commitExtraEmptyScreen = workspace.commitExtraEmptyScreen();
        if (!workspace.getScreenWithId(commitExtraEmptyScreen).findCellForSpan(iArr, itemInfo.spanX, itemInfo.spanY)) {
            Log.wtf(TAG, "Not enough space on an empty screen");
        }
        return commitExtraEmptyScreen;
    }
}
