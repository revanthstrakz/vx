package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import com.android.launcher3.badge.BadgeRenderer;
import java.util.ArrayList;

public class DeviceProfile {
    private static final float MAX_HORIZONTAL_PADDING_PERCENT = 0.14f;
    private static final float TALL_DEVICE_ASPECT_RATIO_THRESHOLD = 2.0f;
    public int allAppsButtonVisualSize;
    public int allAppsCellHeightPx;
    public int allAppsIconDrawablePaddingPx;
    public int allAppsIconSizePx;
    public float allAppsIconTextSizePx;
    public int allAppsNumCols;
    public int allAppsNumPredictiveCols;
    public final PointF appWidgetScale = new PointF(1.0f, 1.0f);
    public final int availableHeightPx;
    public final int availableWidthPx;
    public int cellHeightPx;
    public final int cellLayoutBottomPaddingPx;
    public final int cellLayoutPaddingLeftRightPx;
    public int cellWidthPx;
    private final int defaultPageSpacingPx;
    public final Rect defaultWidgetPadding;
    private final int desiredWorkspaceLeftRightMarginPx;
    public int dropTargetBarSizePx;
    public final int edgeMarginPx;
    public int folderBackgroundOffset;
    public int folderCellHeightPx;
    public int folderCellWidthPx;
    public int folderChildDrawablePaddingPx;
    public int folderChildIconSizePx;
    public int folderChildTextSizePx;
    public int folderIconPreviewPadding;
    public int folderIconSizePx;
    public final int heightPx;
    public int hotseatBarBottomPaddingPx;
    public int hotseatBarLeftNavBarLeftPaddingPx;
    public int hotseatBarLeftNavBarRightPaddingPx;
    public int hotseatBarRightNavBarLeftPaddingPx;
    public int hotseatBarRightNavBarRightPaddingPx;
    public int hotseatBarSizePx;
    public int hotseatBarTopPaddingPx;
    public int hotseatCellHeightPx;
    public int iconDrawablePaddingOriginalPx;
    public int iconDrawablePaddingPx;
    public int iconSizePx;
    public int iconTextSizePx;
    public final InvariantDeviceProfile inv;
    public final boolean isLandscape;
    public final boolean isLargeTablet;
    public final boolean isPhone;
    public final boolean isTablet;
    public BadgeRenderer mBadgeRenderer;
    private final int mBottomMarginHw;
    private Rect mInsets = new Rect();
    private ArrayList<LauncherLayoutChangeListener> mListeners = new ArrayList<>();
    private final int overviewModeBarItemWidthPx;
    private final int overviewModeBarSpacerWidthPx;
    private final float overviewModeIconZoneRatio;
    private final int overviewModeMaxIconZoneHeightPx;
    private final int overviewModeMinIconZoneHeightPx;
    private final int pageIndicatorLandLeftNavBarGutterPx;
    private final int pageIndicatorLandRightNavBarGutterPx;
    private final int pageIndicatorLandWorkspaceOffsetPx;
    private int pageIndicatorSizePx;
    private final int topWorkspacePadding;
    public final boolean transposeLayoutWithOrientation;
    public final int widthPx;
    public int workspaceCellPaddingXPx;
    public float workspaceSpringLoadShrinkFactor;
    public final int workspaceSpringLoadedBottomSpace;

    public interface LauncherLayoutChangeListener {
        void onLauncherLayoutChanged();
    }

    public DeviceProfile(Context context, InvariantDeviceProfile invariantDeviceProfile, Point point, Point point2, int i, int i2, boolean z) {
        int i3;
        this.inv = invariantDeviceProfile;
        this.isLandscape = z;
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        this.isTablet = resources.getBoolean(C0622R.bool.is_tablet);
        this.isLargeTablet = resources.getBoolean(C0622R.bool.is_large_tablet);
        boolean z2 = true;
        this.isPhone = !this.isTablet && !this.isLargeTablet;
        this.transposeLayoutWithOrientation = resources.getBoolean(C0622R.bool.hotseat_transpose_layout_with_orientation);
        Context context2 = getContext(context, isVerticalBarLayout() ? 2 : 1);
        Resources resources2 = context2.getResources();
        this.defaultWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context2, new ComponentName(context2.getPackageName(), getClass().getName()), null);
        this.edgeMarginPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_edge_margin);
        this.desiredWorkspaceLeftRightMarginPx = isVerticalBarLayout() ? 0 : this.edgeMarginPx;
        this.cellLayoutPaddingLeftRightPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_cell_layout_padding);
        this.cellLayoutBottomPaddingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_cell_layout_bottom_padding);
        this.pageIndicatorSizePx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_min_page_indicator_size);
        this.pageIndicatorLandLeftNavBarGutterPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_page_indicator_land_left_nav_bar_gutter_width);
        this.pageIndicatorLandRightNavBarGutterPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_page_indicator_land_right_nav_bar_gutter_width);
        this.pageIndicatorLandWorkspaceOffsetPx = resources2.getDimensionPixelSize(C0622R.dimen.all_apps_caret_workspace_offset);
        this.defaultPageSpacingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_workspace_page_spacing);
        this.topWorkspacePadding = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_workspace_top_padding);
        this.overviewModeMinIconZoneHeightPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_overview_min_icon_zone_height);
        this.overviewModeMaxIconZoneHeightPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_overview_max_icon_zone_height);
        this.overviewModeBarItemWidthPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_overview_bar_item_width);
        this.overviewModeBarSpacerWidthPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_overview_bar_spacer_width);
        this.overviewModeIconZoneRatio = ((float) resources2.getInteger(C0622R.integer.config_dynamic_grid_overview_icon_zone_percentage)) / 100.0f;
        this.iconDrawablePaddingOriginalPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_icon_drawable_padding);
        this.dropTargetBarSizePx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_drop_target_size);
        this.workspaceSpringLoadedBottomSpace = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_min_spring_loaded_space);
        this.workspaceCellPaddingXPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_cell_padding_x);
        this.hotseatBarTopPaddingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_hotseat_top_padding);
        this.hotseatBarBottomPaddingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_hotseat_bottom_padding);
        this.hotseatBarLeftNavBarRightPaddingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_hotseat_land_left_nav_bar_right_padding);
        this.hotseatBarRightNavBarRightPaddingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_hotseat_land_right_nav_bar_right_padding);
        this.hotseatBarLeftNavBarLeftPaddingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_hotseat_land_left_nav_bar_left_padding);
        this.hotseatBarRightNavBarLeftPaddingPx = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_hotseat_land_right_nav_bar_left_padding);
        if (isVerticalBarLayout()) {
            i3 = Utilities.pxFromDp(invariantDeviceProfile.iconSize, displayMetrics);
        } else {
            i3 = resources2.getDimensionPixelSize(C0622R.dimen.dynamic_grid_hotseat_size) + this.hotseatBarTopPaddingPx + this.hotseatBarBottomPaddingPx;
        }
        this.hotseatBarSizePx = i3;
        this.mBottomMarginHw = resources2.getDimensionPixelSize(C0622R.dimen.qsb_hotseat_bottom_margin_hw);
        if (!isVerticalBarLayout()) {
            this.hotseatBarSizePx += this.mBottomMarginHw;
            this.hotseatBarBottomPaddingPx += this.mBottomMarginHw;
        }
        this.widthPx = i;
        this.heightPx = i2;
        if (z) {
            this.availableWidthPx = point2.x;
            this.availableHeightPx = point.y;
        } else {
            this.availableWidthPx = point.x;
            this.availableHeightPx = point2.y;
        }
        updateAvailableDimensions(displayMetrics, resources2);
        if (Float.compare(((float) Math.max(this.widthPx, this.heightPx)) / ((float) Math.min(this.widthPx, this.heightPx)), TALL_DEVICE_ASPECT_RATIO_THRESHOLD) < 0) {
            z2 = false;
        }
        if (!isVerticalBarLayout() && this.isPhone && z2) {
            this.hotseatBarSizePx += ((getCellSize().y - this.iconSizePx) - this.iconDrawablePaddingPx) - this.pageIndicatorSizePx;
            updateAvailableDimensions(displayMetrics, resources2);
        }
        computeAllAppsButtonSize(context2);
        this.mBadgeRenderer = new BadgeRenderer(context2, this.iconSizePx);
    }

    /* access modifiers changed from: 0000 */
    public DeviceProfile getMultiWindowProfile(Context context, Point point) {
        point.set(Math.min(this.availableWidthPx, point.x), Math.min(this.availableHeightPx, point.y));
        DeviceProfile deviceProfile = new DeviceProfile(context, this.inv, point, point, point.x, point.y, this.isLandscape);
        deviceProfile.adjustToHideWorkspaceLabels();
        deviceProfile.appWidgetScale.set(((float) deviceProfile.getCellSize().x) / ((float) getCellSize().x), ((float) deviceProfile.getCellSize().y) / ((float) getCellSize().y));
        return deviceProfile;
    }

    public void addLauncherLayoutChangedListener(LauncherLayoutChangeListener launcherLayoutChangeListener) {
        if (!this.mListeners.contains(launcherLayoutChangeListener)) {
            this.mListeners.add(launcherLayoutChangeListener);
        }
    }

    public void removeLauncherLayoutChangedListener(LauncherLayoutChangeListener launcherLayoutChangeListener) {
        if (this.mListeners.contains(launcherLayoutChangeListener)) {
            this.mListeners.remove(launcherLayoutChangeListener);
        }
    }

    private void adjustToHideWorkspaceLabels() {
        this.iconTextSizePx = 0;
        this.iconDrawablePaddingPx = 0;
        this.cellHeightPx = this.iconSizePx;
        this.allAppsCellHeightPx = this.allAppsIconSizePx + this.allAppsIconDrawablePaddingPx + Utilities.calculateTextHeight(this.allAppsIconTextSizePx) + (this.allAppsIconDrawablePaddingPx * (isVerticalBarLayout() ? 2 : 1) * 2);
    }

    private void computeAllAppsButtonSize(Context context) {
        this.allAppsButtonVisualSize = ((int) (((float) this.iconSizePx) * (1.0f - (((float) context.getResources().getInteger(C0622R.integer.config_allAppsButtonPaddingPercent)) / 100.0f)))) - context.getResources().getDimensionPixelSize(C0622R.dimen.all_apps_button_scale_down);
    }

    private void updateAvailableDimensions(DisplayMetrics displayMetrics, Resources resources) {
        updateIconSize(1.0f, resources, displayMetrics);
        float f = (float) (this.cellHeightPx * this.inv.numRows);
        float f2 = (float) (this.availableHeightPx - getTotalWorkspacePadding().y);
        if (f > f2) {
            updateIconSize(f2 / f, resources, displayMetrics);
        }
        updateAvailableFolderCellDimensions(displayMetrics, resources);
    }

    private void updateIconSize(float f, Resources resources, DisplayMetrics displayMetrics) {
        this.iconSizePx = (int) (((float) Utilities.pxFromDp(isVerticalBarLayout() ? this.inv.landscapeIconSize : this.inv.iconSize, displayMetrics)) * f);
        this.iconTextSizePx = (int) (((float) Utilities.pxFromSp(this.inv.iconTextSize, displayMetrics)) * f);
        this.iconDrawablePaddingPx = (int) (((float) this.iconDrawablePaddingOriginalPx) * f);
        this.cellHeightPx = this.iconSizePx + this.iconDrawablePaddingPx + Utilities.calculateTextHeight((float) this.iconTextSizePx);
        int i = (getCellSize().y - this.cellHeightPx) / 2;
        if (this.iconDrawablePaddingPx > i && !isVerticalBarLayout() && !inMultiWindowMode()) {
            this.cellHeightPx -= this.iconDrawablePaddingPx - i;
            this.iconDrawablePaddingPx = i;
        }
        this.cellWidthPx = this.iconSizePx + this.iconDrawablePaddingPx;
        this.allAppsIconTextSizePx = (float) this.iconTextSizePx;
        this.allAppsIconSizePx = this.iconSizePx;
        this.allAppsIconDrawablePaddingPx = this.iconDrawablePaddingPx;
        this.allAppsCellHeightPx = getCellSize().y;
        if (isVerticalBarLayout()) {
            adjustToHideWorkspaceLabels();
        }
        if (isVerticalBarLayout()) {
            this.hotseatBarSizePx = this.iconSizePx;
        }
        this.hotseatCellHeightPx = this.iconSizePx;
        if (!isVerticalBarLayout()) {
            this.workspaceSpringLoadShrinkFactor = Math.min(((float) resources.getInteger(C0622R.integer.config_workspaceSpringLoadShrinkPercentage)) / 100.0f, 1.0f - (((float) (this.dropTargetBarSizePx + this.workspaceSpringLoadedBottomSpace)) / ((float) (((this.availableHeightPx - this.hotseatBarSizePx) - this.pageIndicatorSizePx) - this.topWorkspacePadding))));
        } else {
            this.workspaceSpringLoadShrinkFactor = ((float) resources.getInteger(C0622R.integer.config_workspaceSpringLoadShrinkPercentage)) / 100.0f;
        }
        this.folderBackgroundOffset = -this.iconDrawablePaddingPx;
        this.folderIconSizePx = this.iconSizePx + ((-this.folderBackgroundOffset) * 2);
        this.folderIconPreviewPadding = resources.getDimensionPixelSize(C0622R.dimen.folder_preview_padding);
    }

    private void updateAvailableFolderCellDimensions(DisplayMetrics displayMetrics, Resources resources) {
        int dimensionPixelSize = resources.getDimensionPixelSize(C0622R.dimen.folder_label_padding_top) + resources.getDimensionPixelSize(C0622R.dimen.folder_label_padding_bottom) + Utilities.calculateTextHeight(resources.getDimension(C0622R.dimen.folder_label_text_size));
        updateFolderCellSize(1.0f, displayMetrics, resources);
        int i = this.edgeMarginPx;
        float min = Math.min(((float) ((this.availableWidthPx - getTotalWorkspacePadding().x) - i)) / ((float) (this.folderCellWidthPx * this.inv.numFolderColumns)), ((float) ((this.availableHeightPx - getTotalWorkspacePadding().y) - i)) / ((float) ((this.folderCellHeightPx * this.inv.numFolderRows) + dimensionPixelSize)));
        if (min < 1.0f) {
            updateFolderCellSize(min, displayMetrics, resources);
        }
    }

    private void updateFolderCellSize(float f, DisplayMetrics displayMetrics, Resources resources) {
        this.folderChildIconSizePx = (int) (((float) Utilities.pxFromDp(this.inv.iconSize, displayMetrics)) * f);
        this.folderChildTextSizePx = (int) (((float) resources.getDimensionPixelSize(C0622R.dimen.folder_child_text_size)) * f);
        int calculateTextHeight = Utilities.calculateTextHeight((float) this.folderChildTextSizePx);
        int dimensionPixelSize = (int) (((float) resources.getDimensionPixelSize(C0622R.dimen.folder_cell_x_padding)) * f);
        int dimensionPixelSize2 = (int) (((float) resources.getDimensionPixelSize(C0622R.dimen.folder_cell_y_padding)) * f);
        this.folderCellWidthPx = this.folderChildIconSizePx + (dimensionPixelSize * 2);
        this.folderCellHeightPx = this.folderChildIconSizePx + (dimensionPixelSize2 * 2) + calculateTextHeight;
        this.folderChildDrawablePaddingPx = Math.max(0, ((this.folderCellHeightPx - this.folderChildIconSizePx) - calculateTextHeight) / 3);
    }

    public void updateInsets(Rect rect) {
        if (!isVerticalBarLayout()) {
            if (this.mInsets.bottom == 0 && rect.bottom != 0) {
                this.hotseatBarSizePx -= this.mBottomMarginHw;
                this.hotseatBarBottomPaddingPx -= this.mBottomMarginHw;
            } else if (this.mInsets.bottom != 0 && rect.bottom == 0) {
                this.hotseatBarSizePx += this.mBottomMarginHw;
                this.hotseatBarBottomPaddingPx += this.mBottomMarginHw;
            }
        }
        this.mInsets.set(rect);
    }

    public void updateAppsViewNumCols() {
        int i = this.inv.numColumns;
        this.allAppsNumPredictiveCols = i;
        this.allAppsNumCols = i;
    }

    public Point getSearchBarDimensForWidgetOpts() {
        int i;
        if (isVerticalBarLayout()) {
            return new Point(this.dropTargetBarSizePx, this.availableHeightPx - (this.edgeMarginPx * 2));
        }
        if (this.isTablet) {
            i = (((getCurrentWidth() - (this.edgeMarginPx * 2)) - (this.inv.numColumns * this.cellWidthPx)) / ((this.inv.numColumns + 1) * 2)) + this.edgeMarginPx;
        } else {
            i = this.desiredWorkspaceLeftRightMarginPx - this.defaultWidgetPadding.right;
        }
        return new Point(this.availableWidthPx - (i * 2), this.dropTargetBarSizePx);
    }

    public Point getCellSize() {
        Point point = new Point();
        Point totalWorkspacePadding = getTotalWorkspacePadding();
        point.x = calculateCellWidth((this.availableWidthPx - totalWorkspacePadding.x) - (this.cellLayoutPaddingLeftRightPx * 2), this.inv.numColumns);
        point.y = calculateCellHeight((this.availableHeightPx - totalWorkspacePadding.y) - this.cellLayoutBottomPaddingPx, this.inv.numRows);
        return point;
    }

    public Point getTotalWorkspacePadding() {
        Rect workspacePadding = getWorkspacePadding(null);
        return new Point(workspacePadding.left + workspacePadding.right, workspacePadding.top + workspacePadding.bottom);
    }

    public Rect getWorkspacePadding(Rect rect) {
        if (rect == null) {
            rect = new Rect();
        }
        if (!isVerticalBarLayout()) {
            int i = this.hotseatBarSizePx + this.pageIndicatorSizePx;
            if (this.isTablet) {
                int currentWidth = getCurrentWidth();
                int min = (int) Math.min((float) Math.max(0, currentWidth - ((this.inv.numColumns * this.cellWidthPx) + ((this.inv.numColumns - 1) * this.cellWidthPx))), ((float) currentWidth) * MAX_HORIZONTAL_PADDING_PERCENT);
                int i2 = min / 2;
                int max = Math.max(0, ((((getCurrentHeight() - this.topWorkspacePadding) - i) - ((this.inv.numRows * 2) * this.cellHeightPx)) - this.hotseatBarTopPaddingPx) - this.hotseatBarBottomPaddingPx) / 2;
                rect.set(i2, this.topWorkspacePadding + max, i2, i + max);
            } else {
                rect.set(this.desiredWorkspaceLeftRightMarginPx, this.topWorkspacePadding, this.desiredWorkspaceLeftRightMarginPx, i);
            }
        } else if (this.mInsets.left > 0) {
            rect.set(this.mInsets.left + this.pageIndicatorLandLeftNavBarGutterPx, 0, ((this.hotseatBarSizePx + this.hotseatBarLeftNavBarRightPaddingPx) + this.hotseatBarLeftNavBarLeftPaddingPx) - this.mInsets.left, this.edgeMarginPx);
        } else {
            rect.set(this.pageIndicatorLandRightNavBarGutterPx, 0, this.hotseatBarSizePx + this.hotseatBarRightNavBarRightPaddingPx + this.hotseatBarRightNavBarLeftPaddingPx, this.edgeMarginPx);
        }
        return rect;
    }

    public Rect getAbsoluteOpenFolderBounds() {
        if (isVerticalBarLayout()) {
            return new Rect(this.mInsets.left + this.dropTargetBarSizePx + this.edgeMarginPx, this.mInsets.top, ((this.mInsets.left + this.availableWidthPx) - this.hotseatBarSizePx) - this.edgeMarginPx, this.mInsets.top + this.availableHeightPx);
        }
        return new Rect(this.mInsets.left, this.mInsets.top + this.dropTargetBarSizePx + this.edgeMarginPx, this.mInsets.left + this.availableWidthPx, (((this.mInsets.top + this.availableHeightPx) - this.hotseatBarSizePx) - this.pageIndicatorSizePx) - this.edgeMarginPx);
    }

    private int getWorkspacePageSpacing() {
        return (isVerticalBarLayout() || this.isLargeTablet) ? this.defaultPageSpacingPx : Math.max(this.defaultPageSpacingPx, getWorkspacePadding(null).left + 1);
    }

    /* access modifiers changed from: 0000 */
    public int getOverviewModeButtonBarHeight() {
        return Utilities.boundToRange((int) (this.overviewModeIconZoneRatio * ((float) this.availableHeightPx)), this.overviewModeMinIconZoneHeightPx, this.overviewModeMaxIconZoneHeightPx);
    }

    public static int calculateCellWidth(int i, int i2) {
        return i / i2;
    }

    public static int calculateCellHeight(int i, int i2) {
        return i / i2;
    }

    public boolean isVerticalBarLayout() {
        return this.isLandscape && this.transposeLayoutWithOrientation;
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldFadeAdjacentWorkspaceScreens() {
        return isVerticalBarLayout() || this.isLargeTablet;
    }

    private int getVisibleChildCount(ViewGroup viewGroup) {
        int i = 0;
        for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
            if (viewGroup.getChildAt(i2).getVisibility() != 8) {
                i++;
            }
        }
        return i;
    }

    public void layout(Launcher launcher, boolean z) {
        boolean isVerticalBarLayout = isVerticalBarLayout();
        Point searchBarDimensForWidgetOpts = getSearchBarDimensForWidgetOpts();
        DropTargetBar dropTargetBar = launcher.getDropTargetBar();
        LayoutParams layoutParams = (LayoutParams) dropTargetBar.getLayoutParams();
        layoutParams.width = searchBarDimensForWidgetOpts.x;
        layoutParams.height = searchBarDimensForWidgetOpts.y;
        layoutParams.topMargin = this.mInsets.top + this.edgeMarginPx;
        dropTargetBar.setLayoutParams(layoutParams);
        PagedView pagedView = (PagedView) launcher.findViewById(C0622R.C0625id.workspace);
        Rect workspacePadding = getWorkspacePadding(null);
        pagedView.setPadding(workspacePadding.left, workspacePadding.top, workspacePadding.right, workspacePadding.bottom);
        pagedView.setPageSpacing(getWorkspacePageSpacing());
        Hotseat hotseat = (Hotseat) launcher.findViewById(C0622R.C0625id.hotseat);
        LayoutParams layoutParams2 = (LayoutParams) hotseat.getLayoutParams();
        int round = Math.round(((((float) getCurrentWidth()) / ((float) this.inv.numColumns)) - (((float) getCurrentWidth()) / ((float) this.inv.numHotseatIcons))) / TALL_DEVICE_ASPECT_RATIO_THRESHOLD);
        if (isVerticalBarLayout) {
            int i = this.mInsets.left > 0 ? this.hotseatBarLeftNavBarRightPaddingPx : this.hotseatBarRightNavBarRightPaddingPx;
            int i2 = this.mInsets.left > 0 ? this.hotseatBarLeftNavBarLeftPaddingPx : this.hotseatBarRightNavBarLeftPaddingPx;
            layoutParams2.gravity = 5;
            layoutParams2.width = this.hotseatBarSizePx + this.mInsets.left + this.mInsets.right + i2 + i;
            layoutParams2.height = -1;
            hotseat.getLayout().setPadding(this.mInsets.left + this.cellLayoutPaddingLeftRightPx + i2, this.mInsets.top, this.mInsets.right + this.cellLayoutPaddingLeftRightPx + i, workspacePadding.bottom + this.cellLayoutBottomPaddingPx);
        } else if (this.isTablet) {
            layoutParams2.gravity = 80;
            layoutParams2.width = -1;
            layoutParams2.height = this.hotseatBarSizePx + this.mInsets.bottom;
            hotseat.getLayout().setPadding(workspacePadding.left + round + this.cellLayoutPaddingLeftRightPx, this.hotseatBarTopPaddingPx, round + workspacePadding.right + this.cellLayoutPaddingLeftRightPx, this.hotseatBarBottomPaddingPx + this.mInsets.bottom + this.cellLayoutBottomPaddingPx);
        } else {
            layoutParams2.gravity = 80;
            layoutParams2.width = -1;
            layoutParams2.height = this.hotseatBarSizePx + this.mInsets.bottom;
            hotseat.getLayout().setPadding(workspacePadding.left + round + this.cellLayoutPaddingLeftRightPx, this.hotseatBarTopPaddingPx, round + workspacePadding.right + this.cellLayoutPaddingLeftRightPx, this.hotseatBarBottomPaddingPx + this.mInsets.bottom + this.cellLayoutBottomPaddingPx);
        }
        hotseat.setLayoutParams(layoutParams2);
        View findViewById = launcher.findViewById(C0622R.C0625id.page_indicator);
        if (findViewById != null) {
            LayoutParams layoutParams3 = (LayoutParams) findViewById.getLayoutParams();
            if (isVerticalBarLayout()) {
                if (this.mInsets.left > 0) {
                    layoutParams3.leftMargin = this.mInsets.left;
                } else {
                    layoutParams3.leftMargin = this.pageIndicatorLandWorkspaceOffsetPx;
                }
                layoutParams3.bottomMargin = workspacePadding.bottom;
            } else {
                layoutParams3.gravity = 81;
                layoutParams3.height = this.pageIndicatorSizePx;
                layoutParams3.bottomMargin = this.hotseatBarSizePx + this.mInsets.bottom;
            }
            findViewById.setLayoutParams(layoutParams3);
        }
        ViewGroup overviewPanel = launcher.getOverviewPanel();
        if (overviewPanel != null) {
            int visibleChildCount = getVisibleChildCount(overviewPanel);
            int i3 = (this.overviewModeBarItemWidthPx * visibleChildCount) + ((visibleChildCount - 1) * this.overviewModeBarSpacerWidthPx);
            LayoutParams layoutParams4 = (LayoutParams) overviewPanel.getLayoutParams();
            layoutParams4.width = Math.min(this.availableWidthPx, i3);
            layoutParams4.height = getOverviewModeButtonBarHeight();
            layoutParams4.bottomMargin = this.mInsets.bottom;
            overviewPanel.setLayoutParams(layoutParams4);
        }
        View findViewById2 = launcher.findViewById(C0622R.C0625id.apps_list_view);
        int i4 = this.desiredWorkspaceLeftRightMarginPx + this.cellLayoutPaddingLeftRightPx;
        findViewById2.setPadding(i4, findViewById2.getPaddingTop(), i4, findViewById2.getPaddingBottom());
        if (z) {
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                ((LauncherLayoutChangeListener) this.mListeners.get(size)).onLauncherLayoutChanged();
            }
        }
    }

    private int getCurrentWidth() {
        if (this.isLandscape) {
            return Math.max(this.widthPx, this.heightPx);
        }
        return Math.min(this.widthPx, this.heightPx);
    }

    private int getCurrentHeight() {
        if (this.isLandscape) {
            return Math.min(this.widthPx, this.heightPx);
        }
        return Math.max(this.widthPx, this.heightPx);
    }

    public int getCellHeight(int i) {
        switch (i) {
            case 0:
                return this.cellHeightPx;
            case 1:
                return this.hotseatCellHeightPx;
            case 2:
                return this.folderCellHeightPx;
            default:
                return 0;
        }
    }

    public final int[] getContainerPadding() {
        if (this.isPhone && !isVerticalBarLayout()) {
            return new int[]{0, 0};
        }
        Rect workspacePadding = getWorkspacePadding(null);
        return new int[]{workspacePadding.left - this.mInsets.left, workspacePadding.right + this.mInsets.left};
    }

    public boolean inMultiWindowMode() {
        return (this == this.inv.landscapeProfile || this == this.inv.portraitProfile) ? false : true;
    }

    public boolean shouldIgnoreLongPressToOverview(float f) {
        boolean z = this.mInsets.left == 0 && f < ((float) this.edgeMarginPx);
        boolean z2 = this.mInsets.right == 0 && f > ((float) (this.widthPx - this.edgeMarginPx));
        if (inMultiWindowMode() || (!z && !z2)) {
            return false;
        }
        return true;
    }

    private static Context getContext(Context context, int i) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.orientation = i;
        return context.createConfigurationContext(configuration);
    }
}
