package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout.LayoutParams;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderPagedView;
import com.android.launcher3.util.FocusLogic;

public class FocusHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "FocusHelper";

    public static class PagedFolderKeyEventListener implements OnKeyListener {
        private final Folder mFolder;

        public PagedFolderKeyEventListener(Folder folder) {
            this.mFolder = folder;
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            View view2 = view;
            int i2 = i;
            boolean shouldConsume = FocusLogic.shouldConsume(i);
            if (keyEvent.getAction() == 1) {
                return shouldConsume;
            }
            int i3 = 0;
            if (!(view.getParent() instanceof ShortcutAndWidgetContainer)) {
                return false;
            }
            ShortcutAndWidgetContainer shortcutAndWidgetContainer = (ShortcutAndWidgetContainer) view.getParent();
            CellLayout cellLayout = (CellLayout) shortcutAndWidgetContainer.getParent();
            int indexOfChild = shortcutAndWidgetContainer.indexOfChild(view2);
            FolderPagedView folderPagedView = (FolderPagedView) cellLayout.getParent();
            int indexOfChild2 = folderPagedView.indexOfChild(cellLayout);
            int pageCount = folderPagedView.getPageCount();
            boolean isRtl = Utilities.isRtl(view.getResources());
            int[][] createSparseMatrix = FocusLogic.createSparseMatrix(cellLayout);
            int[][] iArr = createSparseMatrix;
            int handleKeyEvent = FocusLogic.handleKeyEvent(i, createSparseMatrix, indexOfChild, indexOfChild2, pageCount, isRtl);
            if (handleKeyEvent == -1) {
                handleNoopKey(i2, view2);
                return shouldConsume;
            }
            View view3 = null;
            switch (handleKeyEvent) {
                case -10:
                case -9:
                    int i4 = indexOfChild2 + 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i4);
                    if (cellLayoutChildrenForIndex != null) {
                        folderPagedView.snapToPage(i4);
                        view3 = FocusLogic.getAdjacentChildInNextFolderPage(cellLayoutChildrenForIndex, view2, handleKeyEvent);
                        break;
                    }
                    break;
                case -8:
                    int i5 = indexOfChild2 + 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex2 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i5);
                    if (cellLayoutChildrenForIndex2 != null) {
                        folderPagedView.snapToPage(i5);
                        view3 = cellLayoutChildrenForIndex2.getChildAt(0, 0);
                        break;
                    }
                    break;
                case -7:
                    view3 = folderPagedView.getLastItem();
                    break;
                case -6:
                    view3 = cellLayout.getChildAt(0, 0);
                    break;
                case -5:
                case -2:
                    int[][] iArr2 = iArr;
                    int i6 = indexOfChild2 - 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex3 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i6);
                    if (cellLayoutChildrenForIndex3 != null) {
                        int i7 = ((LayoutParams) view.getLayoutParams()).cellY;
                        folderPagedView.snapToPage(i6);
                        if (!((handleKeyEvent == -5) ^ cellLayoutChildrenForIndex3.invertLayoutHorizontally())) {
                            i3 = iArr2.length - 1;
                        }
                        view3 = cellLayoutChildrenForIndex3.getChildAt(i3, i7);
                        break;
                    }
                    break;
                case -4:
                    int i8 = indexOfChild2 - 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex4 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i8);
                    if (cellLayoutChildrenForIndex4 != null) {
                        folderPagedView.snapToPage(i8);
                        int[][] iArr3 = iArr;
                        view3 = cellLayoutChildrenForIndex4.getChildAt(iArr3.length - 1, iArr3[0].length - 1);
                        break;
                    }
                    break;
                case -3:
                    int i9 = indexOfChild2 - 1;
                    ShortcutAndWidgetContainer cellLayoutChildrenForIndex5 = FocusHelper.getCellLayoutChildrenForIndex(folderPagedView, i9);
                    if (cellLayoutChildrenForIndex5 != null) {
                        folderPagedView.snapToPage(i9);
                        view3 = cellLayoutChildrenForIndex5.getChildAt(0, 0);
                        break;
                    }
                    break;
                default:
                    view3 = shortcutAndWidgetContainer.getChildAt(handleKeyEvent);
                    break;
            }
            if (view3 != null) {
                view3.requestFocus();
                FocusHelper.playSoundEffect(i2, view2);
            } else {
                handleNoopKey(i2, view2);
            }
            return shouldConsume;
        }

        public void handleNoopKey(int i, View view) {
            if (i == 20) {
                this.mFolder.mFolderName.requestFocus();
                FocusHelper.playSoundEffect(i, view);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0143 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean handleHotseatButtonKeyEvent(android.view.View r17, int r18, android.view.KeyEvent r19) {
        /*
            r0 = r17
            r1 = r18
            boolean r2 = com.android.launcher3.util.FocusLogic.shouldConsume(r18)
            int r3 = r19.getAction()
            r4 = 1
            if (r3 == r4) goto L_0x0154
            if (r2 != 0) goto L_0x0013
            goto L_0x0154
        L_0x0013:
            android.content.Context r3 = r17.getContext()
            com.android.launcher3.Launcher r3 = com.android.launcher3.Launcher.getLauncher(r3)
            com.android.launcher3.DeviceProfile r3 = r3.getDeviceProfile()
            android.view.View r5 = r17.getRootView()
            int r6 = com.android.launcher3.C0622R.C0625id.workspace
            android.view.View r5 = r5.findViewById(r6)
            com.android.launcher3.Workspace r5 = (com.android.launcher3.Workspace) r5
            android.view.ViewParent r6 = r17.getParent()
            com.android.launcher3.ShortcutAndWidgetContainer r6 = (com.android.launcher3.ShortcutAndWidgetContainer) r6
            android.view.ViewParent r7 = r6.getParent()
            com.android.launcher3.CellLayout r7 = (com.android.launcher3.CellLayout) r7
            java.lang.Object r8 = r17.getTag()
            com.android.launcher3.ItemInfo r8 = (com.android.launcher3.ItemInfo) r8
            int r8 = r5.getNextPage()
            int r13 = r5.getChildCount()
            int r9 = r6.indexOfChild(r0)
            com.android.launcher3.ShortcutAndWidgetContainer r10 = r7.getShortcutsAndWidgets()
            android.view.View r10 = r10.getChildAt(r9)
            android.view.ViewGroup$LayoutParams r10 = r10.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r10 = (com.android.launcher3.CellLayout.LayoutParams) r10
            int r10 = r10.cellX
            android.view.View r10 = r5.getChildAt(r8)
            com.android.launcher3.CellLayout r10 = (com.android.launcher3.CellLayout) r10
            if (r10 != 0) goto L_0x0062
            return r2
        L_0x0062:
            com.android.launcher3.ShortcutAndWidgetContainer r15 = r10.getShortcutsAndWidgets()
            r16 = 0
            r11 = r16
            int[][] r11 = (int[][]) r11
            r12 = 19
            if (r1 != r12) goto L_0x0083
            boolean r12 = r3.isVerticalBarLayout()
            if (r12 != 0) goto L_0x0083
            int[][] r3 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r10, r7, r3)
            int r6 = r15.getChildCount()
            int r9 = r9 + r6
        L_0x007f:
            r10 = r3
            r11 = r9
            r6 = r15
            goto L_0x00ad
        L_0x0083:
            r12 = 21
            if (r1 != r12) goto L_0x0097
            boolean r12 = r3.isVerticalBarLayout()
            if (r12 == 0) goto L_0x0097
            int[][] r3 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r10, r7, r3)
            int r6 = r15.getChildCount()
            int r9 = r9 + r6
            goto L_0x007f
        L_0x0097:
            r10 = 22
            if (r1 != r10) goto L_0x00a7
            boolean r3 = r3.isVerticalBarLayout()
            if (r3 == 0) goto L_0x00a7
            r1 = 93
            r10 = r11
            r6 = r16
            goto L_0x00ac
        L_0x00a7:
            int[][] r3 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r7)
            r10 = r3
        L_0x00ac:
            r11 = r9
        L_0x00ad:
            android.content.res.Resources r3 = r17.getResources()
            boolean r14 = com.android.launcher3.Utilities.isRtl(r3)
            r9 = r1
            r12 = r8
            int r3 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r9, r10, r11, r12, r13, r14)
            r7 = 0
            switch(r3) {
                case -10: goto L_0x0111;
                case -9: goto L_0x0111;
                case -8: goto L_0x0104;
                case -7: goto L_0x00bf;
                case -6: goto L_0x00bf;
                case -5: goto L_0x00e0;
                case -4: goto L_0x00ce;
                case -3: goto L_0x00c1;
                case -2: goto L_0x00e0;
                default: goto L_0x00bf;
            }
        L_0x00bf:
            goto L_0x0134
        L_0x00c1:
            int r8 = r8 - r4
            com.android.launcher3.ShortcutAndWidgetContainer r6 = getCellLayoutChildrenForIndex(r5, r8)
            android.view.View r16 = r6.getChildAt(r7)
            r5.snapToPage(r8)
            goto L_0x0134
        L_0x00ce:
            int r8 = r8 - r4
            com.android.launcher3.ShortcutAndWidgetContainer r6 = getCellLayoutChildrenForIndex(r5, r8)
            int r7 = r6.getChildCount()
            int r7 = r7 - r4
            android.view.View r16 = r6.getChildAt(r7)
            r5.snapToPage(r8)
            goto L_0x0134
        L_0x00e0:
            int r8 = r8 - r4
            r5.snapToPage(r8)
            android.view.View r4 = r5.getPageAt(r8)
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            com.android.launcher3.ShortcutAndWidgetContainer r4 = r4.getShortcutsAndWidgets()
            android.view.View r4 = r4.getChildAt(r7)
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r4 = (com.android.launcher3.CellLayout.LayoutParams) r4
            boolean r4 = r4.isFullscreen
            if (r4 == 0) goto L_0x0134
            android.view.View r4 = r5.getPageAt(r8)
            r4.requestFocus()
            goto L_0x0134
        L_0x0104:
            int r8 = r8 + r4
            com.android.launcher3.ShortcutAndWidgetContainer r6 = getCellLayoutChildrenForIndex(r5, r8)
            android.view.View r16 = r6.getChildAt(r7)
            r5.snapToPage(r8)
            goto L_0x0134
        L_0x0111:
            int r8 = r8 + r4
            r5.snapToPage(r8)
            android.view.View r4 = r5.getPageAt(r8)
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            com.android.launcher3.ShortcutAndWidgetContainer r4 = r4.getShortcutsAndWidgets()
            android.view.View r4 = r4.getChildAt(r7)
            android.view.ViewGroup$LayoutParams r4 = r4.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r4 = (com.android.launcher3.CellLayout.LayoutParams) r4
            boolean r4 = r4.isFullscreen
            if (r4 == 0) goto L_0x0134
            android.view.View r4 = r5.getPageAt(r8)
            r4.requestFocus()
        L_0x0134:
            if (r6 != r15) goto L_0x0141
            int r4 = r15.getChildCount()
            if (r3 < r4) goto L_0x0141
            int r4 = r15.getChildCount()
            int r3 = r3 - r4
        L_0x0141:
            if (r6 == 0) goto L_0x0153
            if (r16 != 0) goto L_0x014b
            if (r3 < 0) goto L_0x014b
            android.view.View r16 = r6.getChildAt(r3)
        L_0x014b:
            if (r16 == 0) goto L_0x0153
            r16.requestFocus()
            playSoundEffect(r1, r0)
        L_0x0153:
            return r2
        L_0x0154:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FocusHelper.handleHotseatButtonKeyEvent(android.view.View, int, android.view.KeyEvent):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00c5, code lost:
        if (r7 == 19) goto L_0x01c1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean handleIconKeyEvent(android.view.View r18, int r19, android.view.KeyEvent r20) {
        /*
            r0 = r18
            r7 = r19
            boolean r8 = com.android.launcher3.util.FocusLogic.shouldConsume(r19)
            int r1 = r20.getAction()
            r9 = 1
            if (r1 == r9) goto L_0x01ca
            if (r8 != 0) goto L_0x0013
            goto L_0x01ca
        L_0x0013:
            android.content.Context r1 = r18.getContext()
            com.android.launcher3.Launcher r1 = com.android.launcher3.Launcher.getLauncher(r1)
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            android.view.ViewParent r2 = r18.getParent()
            r10 = r2
            com.android.launcher3.ShortcutAndWidgetContainer r10 = (com.android.launcher3.ShortcutAndWidgetContainer) r10
            android.view.ViewParent r2 = r10.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            android.view.ViewParent r3 = r2.getParent()
            r11 = r3
            com.android.launcher3.Workspace r11 = (com.android.launcher3.Workspace) r11
            android.view.ViewParent r3 = r11.getParent()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            int r4 = com.android.launcher3.C0622R.C0625id.drop_target_bar
            android.view.View r4 = r3.findViewById(r4)
            r12 = r4
            android.view.ViewGroup r12 = (android.view.ViewGroup) r12
            int r4 = com.android.launcher3.C0622R.C0625id.hotseat
            android.view.View r3 = r3.findViewById(r4)
            com.android.launcher3.Hotseat r3 = (com.android.launcher3.Hotseat) r3
            java.lang.Object r4 = r18.getTag()
            com.android.launcher3.ItemInfo r4 = (com.android.launcher3.ItemInfo) r4
            int r4 = r10.indexOfChild(r0)
            int r13 = r11.indexOfChild(r2)
            int r14 = r11.getChildCount()
            r5 = 0
            android.view.View r3 = r3.getChildAt(r5)
            r15 = r3
            com.android.launcher3.CellLayout r15 = (com.android.launcher3.CellLayout) r15
            com.android.launcher3.ShortcutAndWidgetContainer r6 = r15.getShortcutsAndWidgets()
            r3 = 20
            if (r7 != r3) goto L_0x0078
            boolean r3 = r1.isVerticalBarLayout()
            if (r3 != 0) goto L_0x0078
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r2, r15, r1)
        L_0x0076:
            r2 = r1
            goto L_0x008c
        L_0x0078:
            r3 = 22
            if (r7 != r3) goto L_0x0087
            boolean r3 = r1.isVerticalBarLayout()
            if (r3 == 0) goto L_0x0087
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r2, r15, r1)
            goto L_0x0076
        L_0x0087:
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r2)
            goto L_0x0076
        L_0x008c:
            android.content.res.Resources r1 = r18.getResources()
            boolean r16 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r19
            r3 = r4
            r4 = r13
            r5 = r14
            r17 = r6
            r6 = r16
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6)
            android.content.res.Resources r2 = r18.getResources()
            boolean r6 = com.android.launcher3.Utilities.isRtl(r2)
            r2 = 0
            android.view.View r3 = r11.getChildAt(r13)
            com.android.launcher3.CellLayout r3 = (com.android.launcher3.CellLayout) r3
            r5 = -4
            r4 = -8
            switch(r1) {
                case -10: goto L_0x0152;
                case -9: goto L_0x0103;
                case -8: goto L_0x00fd;
                case -7: goto L_0x00f1;
                case -6: goto L_0x00e5;
                case -5: goto L_0x0103;
                case -4: goto L_0x00df;
                case -3: goto L_0x00c9;
                case -2: goto L_0x0152;
                case -1: goto L_0x00c3;
                default: goto L_0x00b5;
            }
        L_0x00b5:
            if (r1 < 0) goto L_0x01a3
            int r3 = r10.getChildCount()
            if (r1 >= r3) goto L_0x01a3
            android.view.View r12 = r10.getChildAt(r1)
            goto L_0x01c1
        L_0x00c3:
            r1 = 19
            if (r7 != r1) goto L_0x01c0
            goto L_0x01c1
        L_0x00c9:
            int r13 = r13 - r9
            android.view.View r1 = r11.getChildAt(r13)
            com.android.launcher3.CellLayout r1 = (com.android.launcher3.CellLayout) r1
            android.view.View r12 = getFirstFocusableIconInReadingOrder(r1, r6)
            if (r12 != 0) goto L_0x01c1
            android.view.View r12 = getFirstFocusableIconInReadingOrder(r15, r6)
            r11.snapToPage(r13)
            goto L_0x01c1
        L_0x00df:
            android.view.View r12 = handlePreviousPageLastItem(r11, r15, r13, r6)
            goto L_0x01c1
        L_0x00e5:
            android.view.View r12 = getFirstFocusableIconInReadingOrder(r3, r6)
            if (r12 != 0) goto L_0x01c1
            android.view.View r12 = getFirstFocusableIconInReadingOrder(r15, r6)
            goto L_0x01c1
        L_0x00f1:
            android.view.View r12 = getFirstFocusableIconInReverseReadingOrder(r3, r6)
            if (r12 != 0) goto L_0x01c1
            android.view.View r12 = getFirstFocusableIconInReverseReadingOrder(r15, r6)
            goto L_0x01c1
        L_0x00fd:
            android.view.View r12 = handleNextPageFirstItem(r11, r15, r13, r6)
            goto L_0x01c1
        L_0x0103:
            int r3 = r13 + 1
            r9 = -5
            if (r1 != r9) goto L_0x010c
            int r1 = r13 + -1
            r9 = r1
            goto L_0x010d
        L_0x010c:
            r9 = r3
        L_0x010d:
            android.view.ViewGroup$LayoutParams r1 = r18.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r1 = (com.android.launcher3.CellLayout.LayoutParams) r1
            int r1 = r1.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r10 = getCellLayoutChildrenForIndex(r11, r9)
            if (r10 == 0) goto L_0x01c0
            android.view.ViewParent r2 = r10.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            r3 = -1
            int[][] r2 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithPivotColumn(r2, r3, r1)
            r3 = 100
            android.content.res.Resources r1 = r18.getResources()
            boolean r12 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r19
            r4 = r9
            r9 = -4
            r5 = r14
            r14 = r6
            r6 = r12
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6)
            r12 = -8
            if (r1 != r12) goto L_0x0144
            android.view.View r12 = handleNextPageFirstItem(r11, r15, r13, r14)
            goto L_0x01c1
        L_0x0144:
            if (r1 != r9) goto L_0x014c
            android.view.View r12 = handlePreviousPageLastItem(r11, r15, r13, r14)
            goto L_0x01c1
        L_0x014c:
            android.view.View r12 = r10.getChildAt(r1)
            goto L_0x01c1
        L_0x0152:
            r10 = r6
            r9 = -4
            r12 = -8
            int r3 = r13 + -1
            r4 = -10
            if (r1 != r4) goto L_0x015f
            int r1 = r13 + 1
            r4 = r1
            goto L_0x0160
        L_0x015f:
            r4 = r3
        L_0x0160:
            android.view.ViewGroup$LayoutParams r1 = r18.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r1 = (com.android.launcher3.CellLayout.LayoutParams) r1
            int r1 = r1.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r6 = getCellLayoutChildrenForIndex(r11, r4)
            if (r6 == 0) goto L_0x01c0
            android.view.ViewParent r2 = r6.getParent()
            com.android.launcher3.CellLayout r2 = (com.android.launcher3.CellLayout) r2
            int r3 = r2.getCountX()
            int[][] r2 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithPivotColumn(r2, r3, r1)
            r3 = 100
            android.content.res.Resources r1 = r18.getResources()
            boolean r16 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r19
            r5 = r14
            r14 = r6
            r6 = r16
            int r1 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6)
            if (r1 != r12) goto L_0x0197
            android.view.View r12 = handleNextPageFirstItem(r11, r15, r13, r10)
            goto L_0x01c1
        L_0x0197:
            if (r1 != r9) goto L_0x019e
            android.view.View r12 = handlePreviousPageLastItem(r11, r15, r13, r10)
            goto L_0x01c1
        L_0x019e:
            android.view.View r12 = r14.getChildAt(r1)
            goto L_0x01c1
        L_0x01a3:
            int r3 = r10.getChildCount()
            if (r3 > r1) goto L_0x01c0
            int r3 = r10.getChildCount()
            int r4 = r17.getChildCount()
            int r3 = r3 + r4
            if (r1 >= r3) goto L_0x01c0
            int r2 = r10.getChildCount()
            int r1 = r1 - r2
            r2 = r17
            android.view.View r12 = r2.getChildAt(r1)
            goto L_0x01c1
        L_0x01c0:
            r12 = r2
        L_0x01c1:
            if (r12 == 0) goto L_0x01c9
            r12.requestFocus()
            playSoundEffect(r7, r0)
        L_0x01c9:
            return r8
        L_0x01ca:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FocusHelper.handleIconKeyEvent(android.view.View, int, android.view.KeyEvent):boolean");
    }

    static ShortcutAndWidgetContainer getCellLayoutChildrenForIndex(ViewGroup viewGroup, int i) {
        return ((CellLayout) viewGroup.getChildAt(i)).getShortcutsAndWidgets();
    }

    static void playSoundEffect(int i, View view) {
        switch (i) {
            case 19:
            case 92:
            case 122:
                view.playSoundEffect(2);
                return;
            case 20:
            case 93:
            case 123:
                view.playSoundEffect(4);
                return;
            case 21:
                view.playSoundEffect(1);
                return;
            case 22:
                view.playSoundEffect(3);
                return;
            default:
                return;
        }
    }

    private static View handlePreviousPageLastItem(Workspace workspace, CellLayout cellLayout, int i, boolean z) {
        int i2 = i - 1;
        if (i2 < 0) {
            return null;
        }
        View firstFocusableIconInReverseReadingOrder = getFirstFocusableIconInReverseReadingOrder((CellLayout) workspace.getChildAt(i2), z);
        if (firstFocusableIconInReverseReadingOrder == null) {
            firstFocusableIconInReverseReadingOrder = getFirstFocusableIconInReverseReadingOrder(cellLayout, z);
            workspace.snapToPage(i2);
        }
        return firstFocusableIconInReverseReadingOrder;
    }

    private static View handleNextPageFirstItem(Workspace workspace, CellLayout cellLayout, int i, boolean z) {
        int i2 = i + 1;
        if (i2 >= workspace.getPageCount()) {
            return null;
        }
        View firstFocusableIconInReadingOrder = getFirstFocusableIconInReadingOrder((CellLayout) workspace.getChildAt(i2), z);
        if (firstFocusableIconInReadingOrder == null) {
            firstFocusableIconInReadingOrder = getFirstFocusableIconInReadingOrder(cellLayout, z);
            workspace.snapToPage(i2);
        }
        return firstFocusableIconInReadingOrder;
    }

    private static View getFirstFocusableIconInReadingOrder(CellLayout cellLayout, boolean z) {
        int countX = cellLayout.getCountX();
        for (int i = 0; i < cellLayout.getCountY(); i++) {
            int i2 = z ? -1 : 1;
            int i3 = z ? countX - 1 : 0;
            while (i3 >= 0 && i3 < countX) {
                View childAt = cellLayout.getChildAt(i3, i);
                if (childAt != null && childAt.isFocusable()) {
                    return childAt;
                }
                i3 += i2;
            }
        }
        return null;
    }

    private static View getFirstFocusableIconInReverseReadingOrder(CellLayout cellLayout, boolean z) {
        int countX = cellLayout.getCountX();
        for (int countY = cellLayout.getCountY() - 1; countY >= 0; countY--) {
            int i = z ? 1 : -1;
            int i2 = z ? 0 : countX - 1;
            while (i2 >= 0 && i2 < countX) {
                View childAt = cellLayout.getChildAt(i2, countY);
                if (childAt != null && childAt.isFocusable()) {
                    return childAt;
                }
                i2 += i;
            }
        }
        return null;
    }
}
