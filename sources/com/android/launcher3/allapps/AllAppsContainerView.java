package com.android.launcher3.allapps;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.support.p004v7.widget.LinearLayoutManager;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BaseContainerView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Insettable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.PromiseAppInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.SpringAnimationHandler;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.keyboard.FocusedItemDecorator;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.util.ComponentKeyMapper;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;
import java.util.Set;

public class AllAppsContainerView extends BaseContainerView implements DragSource, OnLongClickListener, Insettable {
    private final AllAppsGridAdapter mAdapter;
    private final AlphabeticalAppsList mApps;
    /* access modifiers changed from: private */
    public AllAppsRecyclerView mAppsRecyclerView;
    private final Launcher mLauncher;
    private final LinearLayoutManager mLayoutManager;
    private int mNumAppsPerRow;
    private int mNumPredictedAppsPerRow;
    private View mSearchContainer;
    private SpannableStringBuilder mSearchQueryBuilder;
    private SearchUiManager mSearchUiManager;
    private SpringAnimationHandler mSpringAnimationHandler;

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
    }

    public boolean supportsAppInfoDropTarget() {
        return true;
    }

    public boolean supportsDeleteDropTarget() {
        return false;
    }

    public AllAppsContainerView(Context context) {
        this(context, null);
    }

    public AllAppsContainerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AllAppsContainerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSearchQueryBuilder = null;
        this.mLauncher = Launcher.getLauncher(context);
        this.mApps = new AlphabeticalAppsList(context);
        this.mAdapter = new AllAppsGridAdapter(this.mLauncher, this.mApps, this.mLauncher, this);
        this.mSpringAnimationHandler = this.mAdapter.getSpringAnimationHandler();
        this.mApps.setAdapter(this.mAdapter);
        this.mLayoutManager = this.mAdapter.getLayoutManager();
        this.mSearchQueryBuilder = new SpannableStringBuilder();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
    }

    /* access modifiers changed from: protected */
    public void updateBackground(int i, int i2, int i3, int i4) {
        if (this.mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            View revealView = getRevealView();
            int i5 = i;
            int i6 = i2;
            int i7 = i3;
            int i8 = i4;
            InsetDrawable insetDrawable = new InsetDrawable(this.mBaseDrawable, i5, i6, i7, i8);
            revealView.setBackground(insetDrawable);
            View contentView = getContentView();
            InsetDrawable insetDrawable2 = new InsetDrawable(new ColorDrawable(0), i5, i6, i7, i8);
            contentView.setBackground(insetDrawable2);
            return;
        }
        getRevealView().setBackground(this.mBaseDrawable);
    }

    public void setPredictedApps(List<ComponentKeyMapper<AppInfo>> list) {
        this.mApps.setPredictedApps(list);
    }

    public void setApps(List<AppInfo> list) {
        this.mApps.setApps(list);
    }

    public void addOrUpdateApps(List<AppInfo> list) {
        this.mApps.addOrUpdateApps(list);
        this.mSearchUiManager.refreshSearchResult();
    }

    public void updatePromiseAppProgress(PromiseAppInfo promiseAppInfo) {
        int childCount = this.mAppsRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mAppsRecyclerView.getChildAt(i);
            if ((childAt instanceof BubbleTextView) && childAt.getTag() == promiseAppInfo) {
                ((BubbleTextView) childAt).applyProgressLevel(promiseAppInfo.level);
            }
        }
    }

    public void removeApps(List<AppInfo> list) {
        this.mApps.removeApps(list);
        this.mSearchUiManager.refreshSearchResult();
    }

    public boolean shouldContainerScroll(MotionEvent motionEvent) {
        if (this.mLauncher.getDragLayer().isEventOverView(this.mSearchContainer, motionEvent)) {
            return true;
        }
        int[] iArr = {(int) motionEvent.getX(), (int) motionEvent.getY()};
        Utilities.mapCoordInSelfToDescendant(this.mAppsRecyclerView.getScrollBar(), this.mLauncher.getDragLayer(), iArr);
        if (!this.mAppsRecyclerView.getScrollBar().shouldBlockIntercept(iArr[0], iArr[1]) && this.mAppsRecyclerView.getCurrentScrollY() == 0) {
            return true;
        }
        return false;
    }

    public void startAppsSearch() {
        this.mSearchUiManager.startAppsSearch();
    }

    public void reset() {
        this.mAppsRecyclerView.scrollToTop();
        this.mSearchUiManager.reset();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        getContentView().setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    AllAppsContainerView.this.mAppsRecyclerView.requestFocus();
                }
            }
        });
        this.mAppsRecyclerView = (AllAppsRecyclerView) findViewById(C0622R.C0625id.apps_list_view);
        this.mAppsRecyclerView.setApps(this.mApps);
        this.mAppsRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mAppsRecyclerView.setAdapter(this.mAdapter);
        this.mAppsRecyclerView.setHasFixedSize(true);
        this.mAppsRecyclerView.setItemAnimator(null);
        this.mAppsRecyclerView.setSpringAnimationHandler(this.mSpringAnimationHandler);
        this.mSearchContainer = findViewById(C0622R.C0625id.search_container_all_apps);
        this.mSearchUiManager = (SearchUiManager) this.mSearchContainer;
        this.mSearchUiManager.initialize(this.mApps, this.mAppsRecyclerView);
        FocusedItemDecorator focusedItemDecorator = new FocusedItemDecorator(this.mAppsRecyclerView);
        this.mAppsRecyclerView.addItemDecoration(focusedItemDecorator);
        this.mAppsRecyclerView.preMeasureViews(this.mAdapter);
        this.mAdapter.setIconFocusListener(focusedItemDecorator.getFocusListener());
        getRevealView().setVisibility(0);
        getContentView().setVisibility(0);
        getContentView().setBackground(null);
    }

    public SearchUiManager getSearchUiManager() {
        return this.mSearchUiManager;
    }

    public View getTouchDelegateTargetView() {
        return this.mAppsRecyclerView;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        deviceProfile.updateAppsViewNumCols();
        if (!(this.mNumAppsPerRow == deviceProfile.inv.numColumns && this.mNumPredictedAppsPerRow == deviceProfile.inv.numColumns)) {
            this.mNumAppsPerRow = deviceProfile.inv.numColumns;
            this.mNumPredictedAppsPerRow = deviceProfile.inv.numColumns;
            this.mAppsRecyclerView.setNumAppsPerRow(deviceProfile, this.mNumAppsPerRow);
            this.mAdapter.setNumAppsPerRow(this.mNumAppsPerRow);
            this.mApps.setNumAppsPerRow(this.mNumAppsPerRow, this.mNumPredictedAppsPerRow);
        }
        super.onMeasure(i, i2);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        this.mSearchUiManager.preDispatchKeyEvent(keyEvent);
        return super.dispatchKeyEvent(keyEvent);
    }

    public boolean onLongClick(final View view) {
        if (!this.mLauncher.isAppsViewVisible() || this.mLauncher.getWorkspace().isSwitchingState() || !this.mLauncher.isDraggingEnabled() || this.mLauncher.getDragController().isDragging()) {
            return false;
        }
        final DragController dragController = this.mLauncher.getDragController();
        dragController.addDragListener(new DragListener() {
            public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
                view.setVisibility(4);
            }

            public void onDragEnd() {
                view.setVisibility(0);
                dragController.removeDragListener(this);
            }
        });
        this.mLauncher.getWorkspace().beginDragShared(view, this, new DragOptions());
        return false;
    }

    public float getIntrinsicIconScaleFactor() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        return ((float) deviceProfile.allAppsIconSizePx) / ((float) deviceProfile.iconSizePx);
    }

    public void onDropCompleted(View view, DragObject dragObject, boolean z, boolean z2) {
        if (z || !z2 || (view != this.mLauncher.getWorkspace() && !(view instanceof DeleteDropTarget) && !(view instanceof Folder))) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 500, null);
        }
        this.mLauncher.unlockScreenOrientation(false);
        if (!z2) {
            dragObject.deferDragViewCleanupPostAnimation = false;
        }
    }

    public void setInsets(Rect rect) {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mAppsRecyclerView.setPadding(this.mAppsRecyclerView.getPaddingLeft(), this.mAppsRecyclerView.getPaddingTop(), this.mAppsRecyclerView.getPaddingRight(), rect.bottom);
        if (deviceProfile.isVerticalBarLayout()) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
            marginLayoutParams.leftMargin = rect.left;
            marginLayoutParams.topMargin = rect.top;
            marginLayoutParams.rightMargin = rect.right;
            setLayoutParams(marginLayoutParams);
            return;
        }
        View findViewById = findViewById(C0622R.C0625id.nav_bar_bg);
        LayoutParams layoutParams = findViewById.getLayoutParams();
        layoutParams.height = rect.bottom;
        findViewById.setLayoutParams(layoutParams);
    }

    public void updateIconBadges(Set<PackageUserKey> set) {
        PackageUserKey packageUserKey = new PackageUserKey(null, null);
        int childCount = this.mAppsRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mAppsRecyclerView.getChildAt(i);
            if ((childAt instanceof BubbleTextView) && (childAt.getTag() instanceof ItemInfo)) {
                ItemInfo itemInfo = (ItemInfo) childAt.getTag();
                if (packageUserKey.updateFromItemInfo(itemInfo) && set.contains(packageUserKey)) {
                    ((BubbleTextView) childAt).applyBadgeState(itemInfo, true);
                }
            }
        }
    }

    public SpringAnimationHandler getSpringAnimationHandler() {
        return this.mSpringAnimationHandler;
    }
}
