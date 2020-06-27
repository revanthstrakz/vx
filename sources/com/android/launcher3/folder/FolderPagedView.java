package com.android.launcher3.folder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.CellLayout.LayoutParams;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.FocusHelper.PagedFolderKeyEventListener;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.PagedView;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace.ItemOperator;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.pageindicators.PageIndicator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class FolderPagedView extends PagedView {
    private static final boolean ALLOW_FOLDER_SCROLL = true;
    private static final int REORDER_ANIMATION_DURATION = 230;
    private static final float SCROLL_HINT_FRACTION = 0.07f;
    private static final int START_VIEW_REORDER_DELAY = 30;
    private static final String TAG = "FolderPagedView";
    private static final float VIEW_REORDER_DELAY_FACTOR = 0.9f;
    private static final int[] sTmpArray = new int[2];
    private int mAllocatedContentSize;
    private final ViewGroupFocusHelper mFocusIndicatorHelper;
    private Folder mFolder;
    @ExportedProperty(category = "launcher")
    private int mGridCountX;
    @ExportedProperty(category = "launcher")
    private int mGridCountY;
    private final LayoutInflater mInflater;
    public final boolean mIsRtl;
    private PagedFolderKeyEventListener mKeyListener;
    @ExportedProperty(category = "launcher")
    private final int mMaxCountX;
    @ExportedProperty(category = "launcher")
    private final int mMaxCountY;
    @ExportedProperty(category = "launcher")
    private final int mMaxItemsPerPage;
    private PageIndicator mPageIndicator;
    final ArrayMap<View, Runnable> mPendingAnimations = new ArrayMap<>();

    public boolean isFull() {
        return false;
    }

    public FolderPagedView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        this.mMaxCountX = idp.numFolderColumns;
        this.mMaxCountY = idp.numFolderRows;
        this.mMaxItemsPerPage = this.mMaxCountX * this.mMaxCountY;
        this.mInflater = LayoutInflater.from(context);
        this.mIsRtl = Utilities.isRtl(getResources());
        setImportantForAccessibility(1);
        this.mFocusIndicatorHelper = new ViewGroupFocusHelper(this);
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
        this.mKeyListener = new PagedFolderKeyEventListener(folder);
        this.mPageIndicator = (PageIndicator) folder.findViewById(C0622R.C0625id.folder_page_indicator);
        initParentViews(folder);
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0025  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void calculateGridSize(int r3, int r4, int r5, int r6, int r7, int r8, int[] r9) {
        /*
            r0 = 0
            r1 = 1
            if (r3 < r8) goto L_0x0008
            r5 = r6
            r8 = r7
            r4 = 1
            goto L_0x000b
        L_0x0008:
            r8 = r5
            r5 = r4
            r4 = 0
        L_0x000b:
            if (r4 != 0) goto L_0x004f
            int r4 = r5 * r8
            if (r4 >= r3) goto L_0x0028
            if (r5 <= r8) goto L_0x0015
            if (r8 != r7) goto L_0x001b
        L_0x0015:
            if (r5 >= r6) goto L_0x001b
            int r4 = r5 + 1
            r2 = r4
            goto L_0x0022
        L_0x001b:
            if (r8 >= r7) goto L_0x0021
            int r4 = r8 + 1
            r2 = r5
            goto L_0x0023
        L_0x0021:
            r2 = r5
        L_0x0022:
            r4 = r8
        L_0x0023:
            if (r4 != 0) goto L_0x0044
            int r4 = r4 + 1
            goto L_0x0044
        L_0x0028:
            int r4 = r8 + -1
            int r2 = r4 * r5
            if (r2 < r3) goto L_0x0036
            if (r8 < r5) goto L_0x0036
            int r4 = java.lang.Math.max(r0, r4)
            r2 = r5
            goto L_0x0044
        L_0x0036:
            int r4 = r5 + -1
            int r2 = r4 * r8
            if (r2 < r3) goto L_0x0042
            int r4 = java.lang.Math.max(r0, r4)
            r2 = r4
            goto L_0x0043
        L_0x0042:
            r2 = r5
        L_0x0043:
            r4 = r8
        L_0x0044:
            if (r2 != r5) goto L_0x004a
            if (r4 != r8) goto L_0x004a
            r5 = 1
            goto L_0x004b
        L_0x004a:
            r5 = 0
        L_0x004b:
            r8 = r4
            r4 = r5
            r5 = r2
            goto L_0x000b
        L_0x004f:
            r9[r0] = r5
            r9[r1] = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderPagedView.calculateGridSize(int, int, int, int, int, int, int[]):void");
    }

    public void setupContentDimensions(int i) {
        this.mAllocatedContentSize = i;
        calculateGridSize(i, this.mGridCountX, this.mGridCountY, this.mMaxCountX, this.mMaxCountY, this.mMaxItemsPerPage, sTmpArray);
        this.mGridCountX = sTmpArray[0];
        this.mGridCountY = sTmpArray[1];
        for (int pageCount = getPageCount() - 1; pageCount >= 0; pageCount--) {
            getPageAt(pageCount).setGridSize(this.mGridCountX, this.mGridCountY);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.mFocusIndicatorHelper.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public ArrayList<ShortcutInfo> bindItems(ArrayList<ShortcutInfo> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        ArrayList<ShortcutInfo> arrayList3 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(createNewView((ShortcutInfo) it.next()));
        }
        arrangeChildren(arrayList2, arrayList2.size(), false);
        return arrayList3;
    }

    public void allocateSpaceForRank(int i) {
        ArrayList arrayList = new ArrayList(this.mFolder.getItemsInReadingOrder());
        arrayList.add(i, null);
        arrangeChildren(arrayList, arrayList.size(), false);
    }

    public int allocateRankForNewItem() {
        int itemCount = getItemCount();
        allocateSpaceForRank(itemCount);
        setCurrentPage(itemCount / this.mMaxItemsPerPage);
        return itemCount;
    }

    public View createAndAddViewForRank(ShortcutInfo shortcutInfo, int i) {
        View createNewView = createNewView(shortcutInfo);
        allocateSpaceForRank(i);
        addViewForRank(createNewView, shortcutInfo, i);
        return createNewView;
    }

    public void addViewForRank(View view, ShortcutInfo shortcutInfo, int i) {
        int i2 = i % this.mMaxItemsPerPage;
        int i3 = i / this.mMaxItemsPerPage;
        shortcutInfo.rank = i;
        shortcutInfo.cellX = i2 % this.mGridCountX;
        shortcutInfo.cellY = i2 / this.mGridCountX;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.cellX = shortcutInfo.cellX;
        layoutParams.cellY = shortcutInfo.cellY;
        getPageAt(i3).addViewToCellLayout(view, -1, this.mFolder.mLauncher.getViewIdForItem(shortcutInfo), layoutParams, true);
    }

    @SuppressLint({"InflateParams"})
    public View createNewView(ShortcutInfo shortcutInfo) {
        BubbleTextView bubbleTextView = (BubbleTextView) this.mInflater.inflate(C0622R.layout.folder_application, null, false);
        bubbleTextView.applyFromShortcutInfo(shortcutInfo);
        bubbleTextView.setHapticFeedbackEnabled(false);
        bubbleTextView.setOnClickListener(this.mFolder);
        bubbleTextView.setOnLongClickListener(this.mFolder);
        bubbleTextView.setOnFocusChangeListener(this.mFocusIndicatorHelper);
        bubbleTextView.setOnKeyListener(this.mKeyListener);
        bubbleTextView.setLayoutParams(new LayoutParams(shortcutInfo.cellX, shortcutInfo.cellY, shortcutInfo.spanX, shortcutInfo.spanY));
        return bubbleTextView;
    }

    public CellLayout getPageAt(int i) {
        return (CellLayout) getChildAt(i);
    }

    public CellLayout getCurrentCellLayout() {
        return getPageAt(getNextPage());
    }

    private CellLayout createAndAddNewPage() {
        DeviceProfile deviceProfile = Launcher.getLauncher(getContext()).getDeviceProfile();
        CellLayout cellLayout = (CellLayout) this.mInflater.inflate(C0622R.layout.folder_page, this, false);
        cellLayout.setCellDimensions(deviceProfile.folderCellWidthPx, deviceProfile.folderCellHeightPx);
        cellLayout.getShortcutsAndWidgets().setMotionEventSplittingEnabled(false);
        cellLayout.setInvertIfRtl(true);
        cellLayout.setGridSize(this.mGridCountX, this.mGridCountY);
        addView(cellLayout, -1, generateDefaultLayoutParams());
        return cellLayout;
    }

    /* access modifiers changed from: protected */
    public int getChildGap() {
        return getPaddingLeft() + getPaddingRight();
    }

    public void setFixedSize(int i, int i2) {
        int paddingLeft = i - (getPaddingLeft() + getPaddingRight());
        int paddingTop = i2 - (getPaddingTop() + getPaddingBottom());
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            ((CellLayout) getChildAt(childCount)).setFixedSize(paddingLeft, paddingTop);
        }
    }

    public void removeItem(View view) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            getPageAt(childCount).removeView(view);
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        this.mPageIndicator.setScroll(i, this.mMaxScrollX);
    }

    public void arrangeChildren(ArrayList<View> arrayList, int i) {
        arrangeChildren(arrayList, i, true);
    }

    @SuppressLint({"RtlHardcoded"})
    private void arrangeChildren(ArrayList<View> arrayList, int i, boolean z) {
        int i2;
        View view;
        int i3 = i;
        ArrayList arrayList2 = new ArrayList();
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            CellLayout cellLayout = (CellLayout) getChildAt(i4);
            cellLayout.removeAllViews();
            arrayList2.add(cellLayout);
        }
        setupContentDimensions(i3);
        Iterator it = arrayList2.iterator();
        FolderIconPreviewVerifier folderIconPreviewVerifier = new FolderIconPreviewVerifier(Launcher.getLauncher(getContext()).getDeviceProfile().inv);
        int i5 = 0;
        CellLayout cellLayout2 = null;
        int i6 = 0;
        int i7 = 0;
        while (i5 < i3) {
            if (arrayList.size() > i5) {
                view = (View) arrayList.get(i5);
            } else {
                ArrayList<View> arrayList3 = arrayList;
                view = null;
            }
            if (cellLayout2 == null || i6 >= this.mMaxItemsPerPage) {
                if (it.hasNext()) {
                    cellLayout2 = (CellLayout) it.next();
                } else {
                    cellLayout2 = createAndAddNewPage();
                }
                i6 = 0;
            }
            if (view != null) {
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                int i8 = i6 % this.mGridCountX;
                int i9 = i6 / this.mGridCountX;
                ItemInfo itemInfo = (ItemInfo) view.getTag();
                if (!(itemInfo.cellX == i8 && itemInfo.cellY == i9 && itemInfo.rank == i7)) {
                    itemInfo.cellX = i8;
                    itemInfo.cellY = i9;
                    itemInfo.rank = i7;
                    if (z) {
                        this.mFolder.mLauncher.getModelWriter().addOrMoveItemInDatabase(itemInfo, this.mFolder.mInfo.f52id, 0, itemInfo.cellX, itemInfo.cellY);
                    }
                }
                layoutParams.cellX = itemInfo.cellX;
                layoutParams.cellY = itemInfo.cellY;
                cellLayout2.addViewToCellLayout(view, -1, this.mFolder.mLauncher.getViewIdForItem(itemInfo), layoutParams, true);
                if (folderIconPreviewVerifier.isItemInPreview(i7) && (view instanceof BubbleTextView)) {
                    ((BubbleTextView) view).verifyHighRes();
                }
            }
            i7++;
            i6++;
            i5++;
            i3 = i;
        }
        int i10 = 1;
        boolean z2 = false;
        while (it.hasNext()) {
            removeView((View) it.next());
            z2 = true;
        }
        if (z2) {
            i2 = 0;
            setCurrentPage(0);
        } else {
            i2 = 0;
        }
        setEnableOverscroll(getPageCount() > 1);
        PageIndicator pageIndicator = this.mPageIndicator;
        if (getPageCount() <= 1) {
            i2 = 8;
        }
        pageIndicator.setVisibility(i2);
        ExtendedEditText extendedEditText = this.mFolder.mFolderName;
        if (getPageCount() > 1) {
            i10 = this.mIsRtl ? 5 : 3;
        }
        extendedEditText.setGravity(i10);
    }

    public int getDesiredWidth() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingRight() + getPageAt(0).getDesiredWidth() + getPaddingLeft();
    }

    public int getDesiredHeight() {
        if (getPageCount() <= 0) {
            return 0;
        }
        return getPaddingBottom() + getPageAt(0).getDesiredHeight() + getPaddingTop();
    }

    public int getItemCount() {
        int childCount = getChildCount() - 1;
        if (childCount < 0) {
            return 0;
        }
        return getPageAt(childCount).getShortcutsAndWidgets().getChildCount() + (childCount * this.mMaxItemsPerPage);
    }

    public int findNearestArea(int i, int i2) {
        int nextPage = getNextPage();
        CellLayout pageAt = getPageAt(nextPage);
        pageAt.findNearestArea(i, i2, 1, 1, sTmpArray);
        if (this.mFolder.isLayoutRtl()) {
            sTmpArray[0] = (pageAt.getCountX() - sTmpArray[0]) - 1;
        }
        return Math.min(this.mAllocatedContentSize - 1, (nextPage * this.mMaxItemsPerPage) + (sTmpArray[1] * this.mGridCountX) + sTmpArray[0]);
    }

    public View getFirstItem() {
        if (getChildCount() < 1) {
            return null;
        }
        ShortcutAndWidgetContainer shortcutsAndWidgets = getCurrentCellLayout().getShortcutsAndWidgets();
        if (this.mGridCountX > 0) {
            return shortcutsAndWidgets.getChildAt(0, 0);
        }
        return shortcutsAndWidgets.getChildAt(0);
    }

    public View getLastItem() {
        if (getChildCount() < 1) {
            return null;
        }
        ShortcutAndWidgetContainer shortcutsAndWidgets = getCurrentCellLayout().getShortcutsAndWidgets();
        int childCount = shortcutsAndWidgets.getChildCount() - 1;
        if (this.mGridCountX > 0) {
            return shortcutsAndWidgets.getChildAt(childCount % this.mGridCountX, childCount / this.mGridCountX);
        }
        return shortcutsAndWidgets.getChildAt(childCount);
    }

    public View iterateOverItems(ItemOperator itemOperator) {
        for (int i = 0; i < getChildCount(); i++) {
            CellLayout pageAt = getPageAt(i);
            for (int i2 = 0; i2 < pageAt.getCountY(); i2++) {
                for (int i3 = 0; i3 < pageAt.getCountX(); i3++) {
                    View childAt = pageAt.getChildAt(i3, i2);
                    if (childAt != null && itemOperator.evaluate((ItemInfo) childAt.getTag(), childAt)) {
                        return childAt;
                    }
                }
            }
        }
        return null;
    }

    public String getAccessibilityDescription() {
        return getContext().getString(C0622R.string.folder_opened, new Object[]{Integer.valueOf(this.mGridCountX), Integer.valueOf(this.mGridCountY)});
    }

    public void setFocusOnFirstChild() {
        View childAt = getCurrentCellLayout().getChildAt(0, 0);
        if (childAt != null) {
            childAt.requestFocus();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        super.notifyPageSwitchListener(i);
        if (this.mFolder != null) {
            this.mFolder.updateTextViewFocus();
        }
    }

    public void showScrollHint(int i) {
        int scrollForPage = (getScrollForPage(getNextPage()) + ((int) (((i == 0) ^ this.mIsRtl ? -0.07f : SCROLL_HINT_FRACTION) * ((float) getWidth())))) - getScrollX();
        if (scrollForPage != 0) {
            this.mScroller.setInterpolator(new DecelerateInterpolator());
            this.mScroller.startScroll(getScrollX(), 0, scrollForPage, 0, 500);
            invalidate();
        }
    }

    public void clearScrollHint() {
        if (getScrollX() != getScrollForPage(getNextPage())) {
            snapToPage(getNextPage());
        }
    }

    public void completePendingPageChanges() {
        if (!this.mPendingAnimations.isEmpty()) {
            for (Entry entry : new ArrayMap(this.mPendingAnimations).entrySet()) {
                ((View) entry.getKey()).animate().cancel();
                ((Runnable) entry.getValue()).run();
            }
        }
    }

    public boolean rankOnCurrentPage(int i) {
        return i / this.mMaxItemsPerPage == getNextPage();
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
        super.onPageBeginTransition();
        verifyVisibleHighResIcons(getCurrentPage() - 1);
        verifyVisibleHighResIcons(getCurrentPage() + 1);
    }

    public void verifyVisibleHighResIcons(int i) {
        CellLayout pageAt = getPageAt(i);
        if (pageAt != null) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = pageAt.getShortcutsAndWidgets();
            for (int childCount = shortcutsAndWidgets.getChildCount() - 1; childCount >= 0; childCount--) {
                BubbleTextView bubbleTextView = (BubbleTextView) shortcutsAndWidgets.getChildAt(childCount);
                bubbleTextView.verifyHighRes();
                Drawable drawable = bubbleTextView.getCompoundDrawables()[1];
                if (drawable != null) {
                    drawable.setCallback(bubbleTextView);
                }
            }
        }
    }

    public int getAllocatedContentSize() {
        return this.mAllocatedContentSize;
    }

    public void realTimeReorder(int i, int i2) {
        int i3;
        int i4;
        final int i5 = i;
        int i6 = i2;
        completePendingPageChanges();
        int nextPage = getNextPage();
        int i7 = i6 % this.mMaxItemsPerPage;
        if (i6 / this.mMaxItemsPerPage != nextPage) {
            Log.e(TAG, "Cannot animate when the target cell is invisible");
        }
        int i8 = i5 % this.mMaxItemsPerPage;
        int i9 = i5 / this.mMaxItemsPerPage;
        if (i6 != i5) {
            int i10 = -1;
            int i11 = 0;
            if (i6 > i5) {
                if (i9 < nextPage) {
                    i10 = nextPage * this.mMaxItemsPerPage;
                    i8 = 0;
                } else {
                    i5 = -1;
                }
                i3 = 1;
            } else {
                if (i9 > nextPage) {
                    i4 = ((nextPage + 1) * this.mMaxItemsPerPage) - 1;
                    i8 = this.mMaxItemsPerPage - 1;
                } else {
                    i5 = -1;
                    i4 = -1;
                }
                i10 = i4;
                i3 = -1;
            }
            while (i5 != i10) {
                int i12 = i5 + i3;
                int i13 = i12 / this.mMaxItemsPerPage;
                int i14 = i12 % this.mMaxItemsPerPage;
                int i15 = i14 % this.mGridCountX;
                int i16 = i14 / this.mGridCountX;
                CellLayout pageAt = getPageAt(i13);
                final View childAt = pageAt.getChildAt(i15, i16);
                if (childAt != null) {
                    if (nextPage != i13) {
                        pageAt.removeView(childAt);
                        addViewForRank(childAt, (ShortcutInfo) childAt.getTag(), i5);
                    } else {
                        final float translationX = childAt.getTranslationX();
                        C07361 r12 = new Runnable() {
                            public void run() {
                                FolderPagedView.this.mPendingAnimations.remove(childAt);
                                childAt.setTranslationX(translationX);
                                ((CellLayout) childAt.getParent().getParent()).removeView(childAt);
                                FolderPagedView.this.addViewForRank(childAt, (ShortcutInfo) childAt.getTag(), i5);
                            }
                        };
                        childAt.animate().translationXBy((float) ((i3 > 0) ^ this.mIsRtl ? -childAt.getWidth() : childAt.getWidth())).setDuration(230).setStartDelay(0).withEndAction(r12);
                        this.mPendingAnimations.put(childAt, r12);
                    }
                }
                i5 = i12;
            }
            if ((i7 - i8) * i3 > 0) {
                CellLayout pageAt2 = getPageAt(nextPage);
                float f = 30.0f;
                while (i8 != i7) {
                    int i17 = i8 + i3;
                    View childAt2 = pageAt2.getChildAt(i17 % this.mGridCountX, i17 / this.mGridCountX);
                    if (childAt2 != null) {
                        ItemInfo itemInfo = (ItemInfo) childAt2.getTag();
                        itemInfo.rank -= i3;
                    }
                    if (pageAt2.animateChildToPosition(childAt2, i8 % this.mGridCountX, i8 / this.mGridCountX, REORDER_ANIMATION_DURATION, i11, true, true)) {
                        f *= VIEW_REORDER_DELAY_FACTOR;
                        i11 = (int) (((float) i11) + f);
                    }
                    i8 = i17;
                }
            }
        }
    }

    public int itemsPerPage() {
        return this.mMaxItemsPerPage;
    }
}
