package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;
import com.android.launcher3.CellLayout.CellInfo;
import com.android.launcher3.CellLayout.LayoutParams;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.Launcher.CustomContentCallbacks;
import com.android.launcher3.Launcher.LauncherOverlay;
import com.android.launcher3.LauncherAppWidgetHost.ProviderChangedListener;
import com.android.launcher3.LauncherSettings.Settings;
import com.android.launcher3.UninstallDropTarget.DropTargetSource;
import com.android.launcher3.accessibility.AccessibleDragListenerAdapter;
import com.android.launcher3.accessibility.OverviewAccessibilityDelegate;
import com.android.launcher3.accessibility.OverviewScreenAccessibilityDelegate;
import com.android.launcher3.accessibility.WorkspaceAccessibilityHelper;
import com.android.launcher3.anim.AnimationLayerSet;
import com.android.launcher3.badge.FolderBadgeInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.SpringLoadedDragController;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.graphics.PreloadIconDrawable;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.WallpaperOffsetInterpolator;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Workspace extends PagedView implements DropTarget, DragSource, OnTouchListener, DragListener, OnHierarchyChangeListener, Insettable, DropTargetSource {
    private static final int ADJACENT_SCREEN_DROP_DURATION = 300;
    private static final float ALLOW_DROP_TRANSITION_PROGRESS = 0.25f;
    public static final int ANIMATE_INTO_POSITION_AND_DISAPPEAR = 0;
    public static final int ANIMATE_INTO_POSITION_AND_REMAIN = 1;
    public static final int ANIMATE_INTO_POSITION_AND_RESIZE = 2;
    public static final int CANCEL_TWO_STAGE_WIDGET_DROP_ANIMATION = 4;
    public static final int COMPLETE_TWO_STAGE_WIDGET_DROP_ANIMATION = 3;
    private static final long CUSTOM_CONTENT_GESTURE_DELAY = 200;
    private static final long CUSTOM_CONTENT_SCREEN_ID = -301;
    private static final int DRAG_MODE_ADD_TO_FOLDER = 2;
    private static final int DRAG_MODE_CREATE_FOLDER = 1;
    private static final int DRAG_MODE_NONE = 0;
    private static final int DRAG_MODE_REORDER = 3;
    private static final boolean ENFORCE_DRAG_EVENT_ORDER = false;
    public static final long EXTRA_EMPTY_SCREEN_ID = -201;
    private static final int FADE_EMPTY_SCREEN_DURATION = 150;
    private static final float FINISHED_SWITCHING_STATE_TRANSITION_PROGRESS = 0.5f;
    public static final long FIRST_SCREEN_ID = 0;
    private static final int FOLDER_CREATION_TIMEOUT = 0;
    private static final int HOTSEAT_STATE_ALPHA_INDEX = 2;
    private static final boolean MAP_NO_RECURSE = false;
    private static final boolean MAP_RECURSE = true;
    static final float MAX_SWIPE_ANGLE = 1.0471976f;
    public static final int REORDER_TIMEOUT = 350;
    private static final int SNAP_OFF_EMPTY_SCREEN_DURATION = 400;
    static final float START_DAMPING_TOUCH_SLOP_ANGLE = 0.5235988f;
    private static final String TAG = "Launcher.Workspace";
    static final float TOUCH_SLOP_DAMPING_FACTOR = 4.0f;
    private static final Rect sTempRect = new Rect();
    private boolean mAddToExistingFolderOnDrop;
    private final Interpolator mAlphaInterpolator;
    boolean mAnimatingViewIntoPlace;
    private final Canvas mCanvas;
    boolean mChildrenLayersEnabled;
    private boolean mCreateUserFolderOnDrop;
    private float mCurrentScale;
    CustomContentCallbacks mCustomContentCallbacks;
    private String mCustomContentDescription;
    private long mCustomContentShowTime;
    boolean mCustomContentShowing;
    private boolean mDeferDropAfterUninstall;
    boolean mDeferRemoveExtraEmptyScreen;
    Runnable mDeferredAction;
    Runnable mDelayedResizeRunnable;
    private Runnable mDelayedSnapToPageRunnable;
    DragController mDragController;
    /* access modifiers changed from: private */
    public CellInfo mDragInfo;
    private int mDragMode;
    private FolderIcon mDragOverFolderIcon;
    private int mDragOverX;
    private int mDragOverY;
    private CellLayout mDragOverlappingLayout;
    private ShortcutAndWidgetContainer mDragSourceInternal;
    CellLayout mDragTargetLayout;
    float[] mDragViewVisualCenter;
    private CellLayout mDropToLayout;
    /* access modifiers changed from: private */
    public PreviewBackground mFolderCreateBg;
    private final Alarm mFolderCreationAlarm;
    private boolean mForceDrawAdjacentPages;
    private final float[] mHotseatAlpha;
    private boolean mIsSwitchingState;
    private float mLastCustomContentScrollProgress;
    float mLastOverlayScroll;
    int mLastReorderX;
    int mLastReorderY;
    final Launcher mLauncher;
    LauncherOverlay mLauncherOverlay;
    private LayoutTransition mLayoutTransition;
    private float mMaxDistanceForFolderCreation;
    /* access modifiers changed from: private */
    public DragPreviewProvider mOutlineProvider;
    boolean mOverlayShown;
    private float mOverlayTranslation;
    private final float mOverviewModeShrinkFactor;
    private final float[] mPageAlpha;
    private AccessibilityDelegate mPagesAccessibilityDelegate;
    Runnable mRemoveEmptyScreenRunnable;
    private final Alarm mReorderAlarm;
    private final ArrayList<Integer> mRestoredPages;
    private SparseArray<Parcelable> mSavedStates;
    final ArrayList<Long> mScreenOrder;
    boolean mScrollInteractionBegan;
    private SpringLoadedDragController mSpringLoadedDragController;
    boolean mStartedSendingScrollEvents;
    /* access modifiers changed from: private */
    @ExportedProperty(category = "launcher")
    public State mState;
    private final WorkspaceStateTransitionAnimation mStateTransitionAnimation;
    private boolean mStripScreensOnPageStopMoving;
    int[] mTargetCell;
    private final float[] mTempTouchCoordinates;
    private final int[] mTempXY;
    private long mTouchDownTime;
    /* access modifiers changed from: private */
    public float mTransitionProgress;
    private boolean mUninstallSuccessful;
    private boolean mUnlockWallpaperFromDefaultPageOnLayout;
    final WallpaperManager mWallpaperManager;
    final WallpaperOffsetInterpolator mWallpaperOffset;
    private final boolean mWorkspaceFadeInAdjacentScreens;
    final LongArrayMap<CellLayout> mWorkspaceScreens;
    private float mXDown;
    private float mYDown;

    private class DeferredWidgetRefresh implements Runnable, ProviderChangedListener {
        private final Handler mHandler = new Handler();
        private final LauncherAppWidgetHost mHost;
        /* access modifiers changed from: private */
        public final ArrayList<LauncherAppWidgetInfo> mInfos;
        private boolean mRefreshPending = true;

        DeferredWidgetRefresh(ArrayList<LauncherAppWidgetInfo> arrayList, LauncherAppWidgetHost launcherAppWidgetHost) {
            this.mInfos = arrayList;
            this.mHost = launcherAppWidgetHost;
            this.mHost.addProviderChangeListener(this);
            this.mHandler.postDelayed(this, 10000);
        }

        public void run() {
            this.mHost.removeProviderChangeListener(this);
            this.mHandler.removeCallbacks(this);
            if (this.mRefreshPending) {
                this.mRefreshPending = false;
                Workspace.this.mapOverItems(false, new ItemOperator() {
                    public boolean evaluate(ItemInfo itemInfo, View view) {
                        if ((view instanceof PendingAppWidgetHostView) && DeferredWidgetRefresh.this.mInfos.contains(itemInfo)) {
                            Workspace.this.mLauncher.removeItem(view, itemInfo, false);
                            Workspace.this.mLauncher.bindAppWidget((LauncherAppWidgetInfo) itemInfo);
                        }
                        return false;
                    }
                });
            }
        }

        public void notifyWidgetProvidersChanged() {
            run();
        }
    }

    public enum Direction {
        X(View.TRANSLATION_X),
        Y(View.TRANSLATION_Y);
        
        /* access modifiers changed from: private */
        public final Property<View, Float> viewProperty;

        private Direction(Property<View, Float> property) {
            this.viewProperty = property;
        }
    }

    class FolderCreationAlarmListener implements OnAlarmListener {

        /* renamed from: bg */
        final PreviewBackground f59bg = new PreviewBackground();
        final int cellX;
        final int cellY;
        final CellLayout layout;

        public FolderCreationAlarmListener(CellLayout cellLayout, int i, int i2) {
            this.layout = cellLayout;
            this.cellX = i;
            this.cellY = i2;
            BubbleTextView bubbleTextView = (BubbleTextView) cellLayout.getChildAt(i, i2);
            this.f59bg.setup(Workspace.this.mLauncher, null, bubbleTextView.getMeasuredWidth(), bubbleTextView.getPaddingTop());
            this.f59bg.isClipping = false;
        }

        public void onAlarm(Alarm alarm) {
            Workspace.this.mFolderCreateBg = this.f59bg;
            Workspace.this.mFolderCreateBg.animateToAccept(this.layout, this.cellX, this.cellY);
            this.layout.clearDragOutlines();
            Workspace.this.setDragMode(1);
        }
    }

    public interface ItemOperator {
        boolean evaluate(ItemInfo itemInfo, View view);
    }

    class ReorderAlarmListener implements OnAlarmListener {
        final View child;
        final DragObject dragObject;
        final float[] dragViewCenter;
        final int minSpanX;
        final int minSpanY;
        final int spanX;
        final int spanY;

        public ReorderAlarmListener(float[] fArr, int i, int i2, int i3, int i4, DragObject dragObject2, View view) {
            this.dragViewCenter = fArr;
            this.minSpanX = i;
            this.minSpanY = i2;
            this.spanX = i3;
            this.spanY = i4;
            this.child = view;
            this.dragObject = dragObject2;
        }

        public void onAlarm(Alarm alarm) {
            int[] iArr = new int[2];
            Workspace.this.mTargetCell = Workspace.this.findNearestArea((int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.minSpanX, this.minSpanY, Workspace.this.mDragTargetLayout, Workspace.this.mTargetCell);
            Workspace.this.mLastReorderX = Workspace.this.mTargetCell[0];
            Workspace.this.mLastReorderY = Workspace.this.mTargetCell[1];
            Workspace.this.mTargetCell = Workspace.this.mDragTargetLayout.performReorder((int) Workspace.this.mDragViewVisualCenter[0], (int) Workspace.this.mDragViewVisualCenter[1], this.minSpanX, this.minSpanY, this.spanX, this.spanY, this.child, Workspace.this.mTargetCell, iArr, 1);
            if (Workspace.this.mTargetCell[0] < 0 || Workspace.this.mTargetCell[1] < 0) {
                Workspace.this.mDragTargetLayout.revertTempState();
            } else {
                Workspace.this.setDragMode(3);
            }
            Workspace.this.mDragTargetLayout.visualizeDropLocation(this.child, Workspace.this.mOutlineProvider, Workspace.this.mTargetCell[0], Workspace.this.mTargetCell[1], iArr[0], iArr[1], (iArr[0] == this.spanX && iArr[1] == this.spanY) ? false : true, this.dragObject);
        }
    }

    public enum State {
        NORMAL(false, false, 1),
        NORMAL_HIDDEN(false, false, 4),
        SPRING_LOADED(false, true, 1),
        OVERVIEW(true, true, 6),
        OVERVIEW_HIDDEN(true, false, 5);
        
        public final int containerType;
        public final boolean hasMultipleVisiblePages;
        public final boolean shouldUpdateWidget;

        private State(boolean z, boolean z2, int i) {
            this.shouldUpdateWidget = z;
            this.hasMultipleVisiblePages = z2;
            this.containerType = i;
        }
    }

    private class StateTransitionListener extends AnimatorListenerAdapter implements AnimatorUpdateListener {
        private StateTransitionListener() {
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            Workspace.this.mTransitionProgress = valueAnimator.getAnimatedFraction();
        }

        public void onAnimationStart(Animator animator) {
            if (Workspace.this.mState == State.SPRING_LOADED) {
                Workspace.this.showPageIndicatorAtCurrentScroll();
            }
            Workspace.this.mTransitionProgress = 0.0f;
        }

        public void onAnimationEnd(Animator animator) {
            Workspace.this.onEndStateTransition();
        }
    }

    public float getIntrinsicIconScaleFactor() {
        return 1.0f;
    }

    public boolean isDropEnabled() {
        return true;
    }

    public void prepareAccessibilityDrop() {
    }

    public boolean supportsAppInfoDropTarget() {
        return true;
    }

    public boolean supportsDeleteDropTarget() {
        return true;
    }

    public Workspace(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public Workspace(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTouchDownTime = -1;
        this.mCustomContentShowTime = -1;
        this.mWorkspaceScreens = new LongArrayMap<>();
        this.mScreenOrder = new ArrayList<>();
        this.mDeferRemoveExtraEmptyScreen = false;
        this.mTargetCell = new int[2];
        this.mDragOverX = -1;
        this.mDragOverY = -1;
        this.mLastCustomContentScrollProgress = -1.0f;
        this.mCustomContentDescription = "";
        this.mDragTargetLayout = null;
        this.mDragOverlappingLayout = null;
        this.mDropToLayout = null;
        this.mTempXY = new int[2];
        this.mDragViewVisualCenter = new float[2];
        this.mTempTouchCoordinates = new float[2];
        this.mPageAlpha = new float[]{1.0f, 1.0f};
        this.mHotseatAlpha = new float[]{1.0f, 1.0f, 1.0f};
        this.mState = State.NORMAL;
        this.mIsSwitchingState = false;
        this.mAnimatingViewIntoPlace = false;
        this.mChildrenLayersEnabled = true;
        this.mStripScreensOnPageStopMoving = false;
        this.mOutlineProvider = null;
        this.mFolderCreationAlarm = new Alarm();
        this.mReorderAlarm = new Alarm();
        this.mDragOverFolderIcon = null;
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mCanvas = new Canvas();
        this.mDragMode = 0;
        this.mLastReorderX = -1;
        this.mLastReorderY = -1;
        this.mRestoredPages = new ArrayList<>();
        this.mLastOverlayScroll = 0.0f;
        this.mOverlayShown = false;
        this.mForceDrawAdjacentPages = false;
        this.mAlphaInterpolator = new DecelerateInterpolator(3.0f);
        this.mLauncher = Launcher.getLauncher(context);
        this.mStateTransitionAnimation = new WorkspaceStateTransitionAnimation(this.mLauncher, this);
        Resources resources = getResources();
        this.mWorkspaceFadeInAdjacentScreens = this.mLauncher.getDeviceProfile().shouldFadeAdjacentWorkspaceScreens();
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mWallpaperOffset = new WallpaperOffsetInterpolator(this);
        this.mOverviewModeShrinkFactor = ((float) resources.getInteger(C0622R.integer.config_workspaceOverviewShrinkPercentage)) / 100.0f;
        setOnHierarchyChangeListener(this);
        setHapticFeedbackEnabled(false);
        initWorkspace();
        setMotionEventSplittingEnabled(true);
    }

    public void setInsets(Rect rect) {
        this.mInsets.set(rect);
        CellLayout screenWithId = getScreenWithId(CUSTOM_CONTENT_SCREEN_ID);
        if (screenWithId != null) {
            View childAt = screenWithId.getShortcutsAndWidgets().getChildAt(0);
            if (childAt instanceof Insettable) {
                ((Insettable) childAt).setInsets(this.mInsets);
            }
        }
    }

    public int[] estimateItemSize(ItemInfo itemInfo, boolean z, boolean z2) {
        float f = this.mLauncher.getDeviceProfile().workspaceSpringLoadShrinkFactor;
        int[] iArr = new int[2];
        if (getChildCount() > 0) {
            CellLayout cellLayout = (CellLayout) getChildAt(numCustomPages());
            boolean z3 = itemInfo.itemType == 4;
            Rect estimateItemPosition = estimateItemPosition(cellLayout, 0, 0, itemInfo.spanX, itemInfo.spanY);
            float f2 = 1.0f;
            if (z3) {
                DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
                f2 = Utilities.shrinkRect(estimateItemPosition, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y);
            }
            iArr[0] = estimateItemPosition.width();
            iArr[1] = estimateItemPosition.height();
            if (z3 && z2) {
                iArr[0] = (int) (((float) iArr[0]) / f2);
                iArr[1] = (int) (((float) iArr[1]) / f2);
            }
            if (z) {
                iArr[0] = (int) (((float) iArr[0]) * f);
                iArr[1] = (int) (((float) iArr[1]) * f);
            }
            return iArr;
        }
        iArr[0] = Integer.MAX_VALUE;
        iArr[1] = Integer.MAX_VALUE;
        return iArr;
    }

    public Rect estimateItemPosition(CellLayout cellLayout, int i, int i2, int i3, int i4) {
        Rect rect = new Rect();
        cellLayout.cellToRect(i, i2, i3, i4, rect);
        return rect;
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        if (!(this.mDragInfo == null || this.mDragInfo.cell == null)) {
            ((CellLayout) this.mDragInfo.cell.getParent().getParent()).markCellsAsUnoccupiedForView(this.mDragInfo.cell);
        }
        if (this.mOutlineProvider != null) {
            this.mOutlineProvider.generateDragOutline(this.mCanvas);
        }
        updateChildrenLayersEnabled(false);
        this.mLauncher.onDragStarted();
        this.mLauncher.lockScreenOrientation();
        this.mLauncher.onInteractionBegin();
        InstallShortcutReceiver.enableInstallQueue(4);
        if (!dragOptions.isAccessibleDrag || dragObject.dragSource == this) {
            this.mDeferRemoveExtraEmptyScreen = false;
            addExtraEmptyScreenOnDrag();
            if (dragObject.dragInfo.itemType == 4 && dragObject.dragSource != this) {
                int pageNearestToCenterOfScreen = getPageNearestToCenterOfScreen();
                while (true) {
                    if (pageNearestToCenterOfScreen >= getPageCount()) {
                        break;
                    } else if (((CellLayout) getPageAt(pageNearestToCenterOfScreen)).hasReorderSolution(dragObject.dragInfo)) {
                        setCurrentPage(pageNearestToCenterOfScreen);
                        break;
                    } else {
                        pageNearestToCenterOfScreen++;
                    }
                }
            }
        }
        this.mLauncher.enterSpringLoadedDragMode();
    }

    public void deferRemoveExtraEmptyScreen() {
        this.mDeferRemoveExtraEmptyScreen = true;
    }

    public void onDragEnd() {
        if (!this.mDeferRemoveExtraEmptyScreen) {
            removeExtraEmptyScreen(true, this.mDragSourceInternal != null);
        }
        updateChildrenLayersEnabled(false);
        this.mLauncher.unlockScreenOrientation(false);
        InstallShortcutReceiver.disableAndFlushInstallQueue(4, getContext());
        this.mOutlineProvider = null;
        this.mDragInfo = null;
        this.mDragSourceInternal = null;
        this.mLauncher.onInteractionEnd();
    }

    /* access modifiers changed from: protected */
    public void initWorkspace() {
        this.mCurrentPage = getDefaultPage();
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        setWillNotDraw(false);
        setClipChildren(false);
        setClipToPadding(false);
        setMinScale(this.mOverviewModeShrinkFactor);
        setupLayoutTransition();
        this.mMaxDistanceForFolderCreation = ((float) deviceProfile.iconSizePx) * 0.55f;
        setWallpaperDimension();
    }

    public void initParentViews(View view) {
        super.initParentViews(view);
        this.mPageIndicator.setAccessibilityDelegate(new OverviewAccessibilityDelegate());
    }

    private int getDefaultPage() {
        return numCustomPages();
    }

    private void setupLayoutTransition() {
        this.mLayoutTransition = new LayoutTransition();
        this.mLayoutTransition.enableTransitionType(3);
        this.mLayoutTransition.enableTransitionType(1);
        this.mLayoutTransition.disableTransitionType(2);
        this.mLayoutTransition.disableTransitionType(0);
        setLayoutTransition(this.mLayoutTransition);
    }

    /* access modifiers changed from: 0000 */
    public void enableLayoutTransitions() {
        setLayoutTransition(this.mLayoutTransition);
    }

    /* access modifiers changed from: 0000 */
    public void disableLayoutTransitions() {
        setLayoutTransition(null);
    }

    public void onChildViewAdded(View view, View view2) {
        if (view2 instanceof CellLayout) {
            CellLayout cellLayout = (CellLayout) view2;
            cellLayout.setOnInterceptTouchListener(this);
            cellLayout.setClickable(true);
            cellLayout.setImportantForAccessibility(2);
            super.onChildViewAdded(view, view2);
            return;
        }
        throw new IllegalArgumentException("A Workspace can only have CellLayout children.");
    }

    /* access modifiers changed from: 0000 */
    public boolean isTouchActive() {
        return this.mTouchState != 0;
    }

    public void bindAndInitFirstWorkspaceScreen(View view) {
        if (FeatureFlags.QSB_ON_FIRST_SCREEN) {
            CellLayout insertNewWorkspaceScreen = insertNewWorkspaceScreen(0, 0);
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(C0622R.layout.search_container_workspace, insertNewWorkspaceScreen, false);
            }
            View view2 = view;
            LayoutParams layoutParams = new LayoutParams(0, 0, insertNewWorkspaceScreen.getCountX(), 1);
            layoutParams.canReorder = false;
            if (!insertNewWorkspaceScreen.addViewToCellLayout(view2, 0, C0622R.C0625id.search_container_workspace, layoutParams, true)) {
                Log.e(TAG, "Failed to add to item at (0, 0) to CellLayout");
            }
        }
    }

    public void removeAllWorkspaceScreens() {
        disableLayoutTransitions();
        if (hasCustomContent()) {
            removeCustomContentPage();
        }
        View findViewById = findViewById(C0622R.C0625id.search_container_workspace);
        if (findViewById != null) {
            ((ViewGroup) findViewById.getParent()).removeView(findViewById);
        }
        removeAllViews();
        this.mScreenOrder.clear();
        this.mWorkspaceScreens.clear();
        bindAndInitFirstWorkspaceScreen(findViewById);
        enableLayoutTransitions();
    }

    public void insertNewWorkspaceScreenBeforeEmptyScreen(long j) {
        int indexOf = this.mScreenOrder.indexOf(Long.valueOf(-201));
        if (indexOf < 0) {
            indexOf = this.mScreenOrder.size();
        }
        insertNewWorkspaceScreen(j, indexOf);
    }

    public void insertNewWorkspaceScreen(long j) {
        insertNewWorkspaceScreen(j, getChildCount());
    }

    public CellLayout insertNewWorkspaceScreen(long j, int i) {
        if (!this.mWorkspaceScreens.containsKey(j)) {
            CellLayout cellLayout = (CellLayout) LayoutInflater.from(getContext()).inflate(C0622R.layout.workspace_screen, this, false);
            cellLayout.setOnLongClickListener(this.mLongClickListener);
            cellLayout.setOnClickListener(this.mLauncher);
            cellLayout.setSoundEffectsEnabled(false);
            int i2 = this.mLauncher.getDeviceProfile().cellLayoutPaddingLeftRightPx;
            cellLayout.setPadding(i2, 0, i2, this.mLauncher.getDeviceProfile().cellLayoutBottomPaddingPx);
            this.mWorkspaceScreens.put(j, cellLayout);
            this.mScreenOrder.add(i, Long.valueOf(j));
            addView(cellLayout, i);
            if (this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag()) {
                cellLayout.enableAccessibleDrag(true, 2);
            }
            return cellLayout;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Screen id ");
        sb.append(j);
        sb.append(" already exists!");
        throw new RuntimeException(sb.toString());
    }

    public void createCustomContentContainer() {
        CellLayout cellLayout = (CellLayout) LayoutInflater.from(getContext()).inflate(C0622R.layout.workspace_screen, this, false);
        cellLayout.disableDragTarget();
        cellLayout.disableJailContent();
        this.mWorkspaceScreens.put(CUSTOM_CONTENT_SCREEN_ID, cellLayout);
        this.mScreenOrder.add(0, Long.valueOf(CUSTOM_CONTENT_SCREEN_ID));
        cellLayout.setPadding(0, 0, 0, 0);
        addFullScreenPage(cellLayout);
        setCurrentPage(getCurrentPage() + 1);
    }

    public void removeCustomContentPage() {
        CellLayout screenWithId = getScreenWithId(CUSTOM_CONTENT_SCREEN_ID);
        if (screenWithId != null) {
            this.mWorkspaceScreens.remove(CUSTOM_CONTENT_SCREEN_ID);
            this.mScreenOrder.remove(Long.valueOf(CUSTOM_CONTENT_SCREEN_ID));
            removeView(screenWithId);
            if (this.mCustomContentCallbacks != null) {
                this.mCustomContentCallbacks.onScrollProgressChanged(0.0f);
                this.mCustomContentCallbacks.onHide();
            }
            this.mCustomContentCallbacks = null;
            setCurrentPage(getCurrentPage() - 1);
            return;
        }
        throw new RuntimeException("Expected custom content screen to exist");
    }

    public void addToCustomContentPage(View view, CustomContentCallbacks customContentCallbacks, String str) {
        if (getPageIndexForScreenId(CUSTOM_CONTENT_SCREEN_ID) >= 0) {
            CellLayout screenWithId = getScreenWithId(CUSTOM_CONTENT_SCREEN_ID);
            LayoutParams layoutParams = new LayoutParams(0, 0, screenWithId.getCountX(), screenWithId.getCountY());
            layoutParams.canReorder = false;
            layoutParams.isFullscreen = true;
            if (view instanceof Insettable) {
                ((Insettable) view).setInsets(this.mInsets);
            }
            if (view.getParent() instanceof ViewGroup) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            screenWithId.removeAllViews();
            view.setFocusable(true);
            view.setOnKeyListener(new FullscreenKeyEventListener());
            view.setOnFocusChangeListener(this.mLauncher.mFocusHandler.getHideIndicatorOnFocusListener());
            screenWithId.addViewToCellLayout(view, 0, 0, layoutParams, true);
            this.mCustomContentDescription = str;
            this.mCustomContentCallbacks = customContentCallbacks;
            return;
        }
        throw new RuntimeException("Expected custom content screen to exist");
    }

    public void addExtraEmptyScreenOnDrag() {
        boolean z;
        this.mRemoveEmptyScreenRunnable = null;
        boolean z2 = false;
        if (this.mDragSourceInternal != null) {
            z = this.mDragSourceInternal.getChildCount() == 1;
            if (indexOfChild((CellLayout) this.mDragSourceInternal.getParent()) == getChildCount() - 1) {
                z2 = true;
            }
        } else {
            z = false;
        }
        if ((!z || !z2) && !this.mWorkspaceScreens.containsKey(-201)) {
            insertNewWorkspaceScreen(-201);
        }
    }

    public boolean addExtraEmptyScreen() {
        if (this.mWorkspaceScreens.containsKey(-201)) {
            return false;
        }
        insertNewWorkspaceScreen(-201);
        return true;
    }

    private void convertFinalScreenToEmptyScreenIfNecessary() {
        if (!this.mLauncher.isWorkspaceLoading() && !hasExtraEmptyScreen() && this.mScreenOrder.size() != 0) {
            long longValue = ((Long) this.mScreenOrder.get(this.mScreenOrder.size() - 1)).longValue();
            if (longValue != CUSTOM_CONTENT_SCREEN_ID) {
                CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.get(longValue);
                if (cellLayout.getShortcutsAndWidgets().getChildCount() == 0 && !cellLayout.isDropPending()) {
                    this.mWorkspaceScreens.remove(longValue);
                    this.mScreenOrder.remove(Long.valueOf(longValue));
                    this.mWorkspaceScreens.put(-201, cellLayout);
                    this.mScreenOrder.add(Long.valueOf(-201));
                    LauncherModel.updateWorkspaceScreenOrder(this.mLauncher, this.mScreenOrder);
                }
            }
        }
    }

    public void removeExtraEmptyScreen(boolean z, boolean z2) {
        removeExtraEmptyScreenDelayed(z, null, 0, z2);
    }

    public void removeExtraEmptyScreenDelayed(final boolean z, final Runnable runnable, int i, final boolean z2) {
        if (!this.mLauncher.isWorkspaceLoading()) {
            if (i > 0) {
                postDelayed(new Runnable() {
                    public void run() {
                        Workspace.this.removeExtraEmptyScreenDelayed(z, runnable, 0, z2);
                    }
                }, (long) i);
                return;
            }
            convertFinalScreenToEmptyScreenIfNecessary();
            if (hasExtraEmptyScreen()) {
                if (getNextPage() == this.mScreenOrder.indexOf(Long.valueOf(-201))) {
                    snapToPage(getNextPage() - 1, SNAP_OFF_EMPTY_SCREEN_DURATION);
                    fadeAndRemoveEmptyScreen(SNAP_OFF_EMPTY_SCREEN_DURATION, 150, runnable, z2);
                } else {
                    snapToPage(getNextPage(), 0);
                    fadeAndRemoveEmptyScreen(0, 150, runnable, z2);
                }
                return;
            }
            if (z2) {
                stripEmptyScreens();
            }
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    private void fadeAndRemoveEmptyScreen(int i, int i2, final Runnable runnable, final boolean z) {
        PropertyValuesHolder ofFloat = PropertyValuesHolder.ofFloat("alpha", new float[]{0.0f});
        PropertyValuesHolder ofFloat2 = PropertyValuesHolder.ofFloat("backgroundAlpha", new float[]{0.0f});
        final CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.get(-201);
        this.mRemoveEmptyScreenRunnable = new Runnable() {
            public void run() {
                if (Workspace.this.hasExtraEmptyScreen()) {
                    Workspace.this.mWorkspaceScreens.remove(-201);
                    Workspace.this.mScreenOrder.remove(Long.valueOf(-201));
                    Workspace.this.removeView(cellLayout);
                    if (z) {
                        Workspace.this.stripEmptyScreens();
                    }
                    Workspace.this.showPageIndicatorAtCurrentScroll();
                }
            }
        };
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(cellLayout, new PropertyValuesHolder[]{ofFloat, ofFloat2});
        ofPropertyValuesHolder.setDuration((long) i2);
        ofPropertyValuesHolder.setStartDelay((long) i);
        ofPropertyValuesHolder.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (Workspace.this.mRemoveEmptyScreenRunnable != null) {
                    Workspace.this.mRemoveEmptyScreenRunnable.run();
                }
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        ofPropertyValuesHolder.start();
    }

    public boolean hasExtraEmptyScreen() {
        return this.mWorkspaceScreens.containsKey(-201) && getChildCount() - numCustomPages() > 1;
    }

    public long commitExtraEmptyScreen() {
        if (this.mLauncher.isWorkspaceLoading()) {
            return -1;
        }
        CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.get(-201);
        this.mWorkspaceScreens.remove(-201);
        this.mScreenOrder.remove(Long.valueOf(-201));
        long j = Settings.call(getContext().getContentResolver(), Settings.METHOD_NEW_SCREEN_ID).getLong("value");
        this.mWorkspaceScreens.put(j, cellLayout);
        this.mScreenOrder.add(Long.valueOf(j));
        LauncherModel.updateWorkspaceScreenOrder(this.mLauncher, this.mScreenOrder);
        return j;
    }

    public CellLayout getScreenWithId(long j) {
        return (CellLayout) this.mWorkspaceScreens.get(j);
    }

    public long getIdForScreen(CellLayout cellLayout) {
        int indexOfValue = this.mWorkspaceScreens.indexOfValue(cellLayout);
        if (indexOfValue != -1) {
            return this.mWorkspaceScreens.keyAt(indexOfValue);
        }
        return -1;
    }

    public int getPageIndexForScreenId(long j) {
        return indexOfChild((View) this.mWorkspaceScreens.get(j));
    }

    public long getScreenIdForPageIndex(int i) {
        if (i < 0 || i >= this.mScreenOrder.size()) {
            return -1;
        }
        return ((Long) this.mScreenOrder.get(i)).longValue();
    }

    public ArrayList<Long> getScreenOrder() {
        return this.mScreenOrder;
    }

    public void stripEmptyScreens() {
        if (!this.mLauncher.isWorkspaceLoading()) {
            if (isPageInTransition()) {
                this.mStripScreensOnPageStopMoving = true;
                return;
            }
            int nextPage = getNextPage();
            ArrayList arrayList = new ArrayList();
            int size = this.mWorkspaceScreens.size();
            for (int i = 0; i < size; i++) {
                long keyAt = this.mWorkspaceScreens.keyAt(i);
                CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.valueAt(i);
                if ((!FeatureFlags.QSB_ON_FIRST_SCREEN || keyAt > 0) && cellLayout.getShortcutsAndWidgets().getChildCount() == 0) {
                    arrayList.add(Long.valueOf(keyAt));
                }
            }
            boolean isInAccessibleDrag = this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag();
            int numCustomPages = numCustomPages() + 1;
            Iterator it = arrayList.iterator();
            int i2 = 0;
            while (it.hasNext()) {
                Long l = (Long) it.next();
                CellLayout cellLayout2 = (CellLayout) this.mWorkspaceScreens.get(l.longValue());
                this.mWorkspaceScreens.remove(l.longValue());
                this.mScreenOrder.remove(l);
                if (getChildCount() > numCustomPages) {
                    if (indexOfChild(cellLayout2) < nextPage) {
                        i2++;
                    }
                    if (isInAccessibleDrag) {
                        cellLayout2.enableAccessibleDrag(false, 2);
                    }
                    removeView(cellLayout2);
                } else {
                    this.mRemoveEmptyScreenRunnable = null;
                    this.mWorkspaceScreens.put(-201, cellLayout2);
                    this.mScreenOrder.add(Long.valueOf(-201));
                }
            }
            if (!arrayList.isEmpty()) {
                LauncherModel.updateWorkspaceScreenOrder(this.mLauncher, this.mScreenOrder);
            }
            if (i2 >= 0) {
                setCurrentPage(nextPage - i2);
            }
        }
    }

    public void addInScreenFromBind(View view, ItemInfo itemInfo) {
        int i;
        int i2;
        int i3 = itemInfo.cellX;
        int i4 = itemInfo.cellY;
        if (itemInfo.container == -101) {
            int i5 = (int) itemInfo.screenId;
            int cellXFromOrder = this.mLauncher.getHotseat().getCellXFromOrder(i5);
            i = this.mLauncher.getHotseat().getCellYFromOrder(i5);
            i2 = cellXFromOrder;
        } else {
            i2 = i3;
            i = i4;
        }
        addInScreen(view, itemInfo.container, itemInfo.screenId, i2, i, itemInfo.spanX, itemInfo.spanY);
    }

    public void addInScreen(View view, ItemInfo itemInfo) {
        addInScreen(view, itemInfo.container, itemInfo.screenId, itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY);
    }

    private void addInScreen(View view, long j, long j2, int i, int i2, int i3, int i4) {
        CellLayout screenWithId;
        LayoutParams layoutParams;
        if (j == -100 && getScreenWithId(j2) == null) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Skipping child, screenId ");
            sb.append(j2);
            sb.append(" not found");
            Log.e(str, sb.toString());
            new Throwable().printStackTrace();
        } else if (j2 != -201) {
            if (j == -101) {
                screenWithId = this.mLauncher.getHotseat().getLayout();
                view.setOnKeyListener(new HotseatIconKeyEventListener());
                if (view instanceof FolderIcon) {
                    ((FolderIcon) view).setTextVisible(false);
                }
            } else {
                if (view instanceof FolderIcon) {
                    ((FolderIcon) view).setTextVisible(true);
                }
                screenWithId = getScreenWithId(j2);
                view.setOnKeyListener(new IconKeyEventListener());
            }
            CellLayout cellLayout = screenWithId;
            ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
            if (layoutParams2 == null || !(layoutParams2 instanceof LayoutParams)) {
                layoutParams = new LayoutParams(i, i2, i3, i4);
            } else {
                layoutParams = (LayoutParams) layoutParams2;
                layoutParams.cellX = i;
                layoutParams.cellY = i2;
                layoutParams.cellHSpan = i3;
                layoutParams.cellVSpan = i4;
            }
            if (i3 < 0 && i4 < 0) {
                layoutParams.isLockedToGrid = false;
            }
            boolean z = view instanceof Folder;
            if (!cellLayout.addViewToCellLayout(view, -1, this.mLauncher.getViewIdForItem((ItemInfo) view.getTag()), layoutParams, !z)) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Failed to add to item at (");
                sb2.append(layoutParams.cellX);
                sb2.append(",");
                sb2.append(layoutParams.cellY);
                sb2.append(") to CellLayout");
                Log.e(str2, sb2.toString());
            }
            if (!z) {
                view.setHapticFeedbackEnabled(false);
                view.setOnLongClickListener(this.mLongClickListener);
            }
            if (view instanceof DropTarget) {
                this.mDragController.addDropTarget((DropTarget) view);
            }
        } else {
            throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return shouldConsumeTouch(view);
    }

    private boolean shouldConsumeTouch(View view) {
        return !workspaceIconsCanBeDragged() || (!workspaceInModalState() && indexOfChild(view) != this.mCurrentPage);
    }

    public boolean isSwitchingState() {
        return this.mIsSwitchingState;
    }

    public boolean isFinishedSwitchingState() {
        return !this.mIsSwitchingState || this.mTransitionProgress > 0.5f;
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int i) {
        this.mLauncher.onWindowVisibilityChanged(i);
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        if (workspaceInModalState() || !isFinishedSwitchingState()) {
            return false;
        }
        return super.dispatchUnhandledMove(view, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mLauncher.isAllAppsVisible()) {
            return true;
        }
        int action = motionEvent.getAction() & 255;
        if (action != 6) {
            switch (action) {
                case 0:
                    this.mXDown = motionEvent.getX();
                    this.mYDown = motionEvent.getY();
                    this.mTouchDownTime = System.currentTimeMillis();
                    break;
                case 1:
                    break;
            }
        }
        if (this.mTouchState == 0 && ((CellLayout) getChildAt(this.mCurrentPage)) != null) {
            onWallpaperTap(motionEvent);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (getScreenIdForPageIndex(getCurrentPage()) != CUSTOM_CONTENT_SCREEN_ID || this.mCustomContentCallbacks == null || this.mCustomContentCallbacks.isScrollingAllowed()) {
            return super.onGenericMotionEvent(motionEvent);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void reinflateWidgetsIfNecessary() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = ((CellLayout) getChildAt(i)).getShortcutsAndWidgets();
            int childCount2 = shortcutsAndWidgets.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View childAt = shortcutsAndWidgets.getChildAt(i2);
                if ((childAt instanceof LauncherAppWidgetHostView) && (childAt.getTag() instanceof LauncherAppWidgetInfo)) {
                    LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) childAt.getTag();
                    LauncherAppWidgetHostView launcherAppWidgetHostView = (LauncherAppWidgetHostView) childAt;
                    if (launcherAppWidgetHostView.isReinflateRequired(this.mLauncher.getOrientation())) {
                        this.mLauncher.removeItem(launcherAppWidgetHostView, launcherAppWidgetInfo, false);
                        this.mLauncher.bindAppWidget(launcherAppWidgetInfo);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent) {
        if (isFinishedSwitchingState()) {
            float x = motionEvent.getX() - this.mXDown;
            float abs = Math.abs(x);
            float abs2 = Math.abs(motionEvent.getY() - this.mYDown);
            if (Float.compare(abs, 0.0f) != 0) {
                float atan = (float) Math.atan((double) (abs2 / abs));
                if (abs > ((float) this.mTouchSlop) || abs2 > ((float) this.mTouchSlop)) {
                    cancelCurrentPageLongPress();
                }
                boolean z = false;
                boolean z2 = this.mTouchDownTime - this.mCustomContentShowTime > CUSTOM_CONTENT_GESTURE_DELAY;
                boolean z3 = !this.mIsRtl ? x > 0.0f : x < 0.0f;
                if (getScreenIdForPageIndex(getCurrentPage()) == CUSTOM_CONTENT_SCREEN_ID) {
                    z = true;
                }
                if (z3 && z && z2) {
                    return;
                }
                if ((!z || this.mCustomContentCallbacks == null || this.mCustomContentCallbacks.isScrollingAllowed()) && atan <= MAX_SWIPE_ANGLE) {
                    if (atan > START_DAMPING_TOUCH_SLOP_ANGLE) {
                        super.determineScrollingStart(motionEvent, (((float) Math.sqrt((double) ((atan - START_DAMPING_TOUCH_SLOP_ANGLE) / START_DAMPING_TOUCH_SLOP_ANGLE))) * TOUCH_SLOP_DAMPING_FACTOR) + 1.0f);
                    } else {
                        super.determineScrollingStart(motionEvent);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
        super.onPageBeginTransition();
        updateChildrenLayersEnabled(false);
        AbstractFloatingView.closeAllOpenViews(this.mLauncher);
    }

    /* access modifiers changed from: protected */
    public void onPageEndTransition() {
        super.onPageEndTransition();
        updateChildrenLayersEnabled(false);
        if (this.mDragController.isDragging() && workspaceInModalState()) {
            this.mDragController.forceTouchMove();
        }
        if (this.mDelayedResizeRunnable != null && !this.mIsSwitchingState) {
            this.mDelayedResizeRunnable.run();
            this.mDelayedResizeRunnable = null;
        }
        if (this.mDelayedSnapToPageRunnable != null) {
            this.mDelayedSnapToPageRunnable.run();
            this.mDelayedSnapToPageRunnable = null;
        }
        if (this.mStripScreensOnPageStopMoving) {
            stripEmptyScreens();
            this.mStripScreensOnPageStopMoving = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionBegin() {
        super.onScrollInteractionEnd();
        this.mScrollInteractionBegan = true;
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionEnd() {
        super.onScrollInteractionEnd();
        this.mScrollInteractionBegan = false;
        if (this.mStartedSendingScrollEvents) {
            this.mStartedSendingScrollEvents = false;
            this.mLauncherOverlay.onScrollInteractionEnd();
        }
    }

    public void setLauncherOverlay(LauncherOverlay launcherOverlay) {
        this.mLauncherOverlay = launcherOverlay;
        this.mStartedSendingScrollEvents = false;
        onOverlayScrollChanged(0.0f);
    }

    private boolean isScrollingOverlay() {
        return this.mLauncherOverlay != null && ((this.mIsRtl && getUnboundedScrollX() > this.mMaxScrollX) || (!this.mIsRtl && getUnboundedScrollX() < 0));
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        if (isScrollingOverlay()) {
            this.mWasInOverscroll = false;
            snapToPageImmediately(0);
            return;
        }
        super.snapToDestination();
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (!(this.mIsSwitchingState || (getLayoutTransition() != null && getLayoutTransition().isRunning()))) {
            showPageIndicatorAtCurrentScroll();
        }
        updatePageAlphaValues();
        updateStateForCustomContent();
        enableHwLayersOnVisiblePages();
    }

    /* access modifiers changed from: private */
    public void showPageIndicatorAtCurrentScroll() {
        if (this.mPageIndicator != null) {
            this.mPageIndicator.setScroll(getScrollX(), computeMaxScrollX());
        }
    }

    /* access modifiers changed from: protected */
    public void overScroll(float f) {
        boolean z = false;
        int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        boolean z2 = (i <= 0 && (!hasCustomContent() || this.mIsRtl)) || (f >= 0.0f && (!hasCustomContent() || !this.mIsRtl));
        boolean z3 = this.mLauncherOverlay != null && ((i <= 0 && !this.mIsRtl) || (f >= 0.0f && this.mIsRtl));
        if (!(this.mLauncherOverlay == null || this.mLastOverlayScroll == 0.0f || ((f < 0.0f || this.mIsRtl) && (i > 0 || !this.mIsRtl)))) {
            z = true;
        }
        if (z3) {
            if (!this.mStartedSendingScrollEvents && this.mScrollInteractionBegan) {
                this.mStartedSendingScrollEvents = true;
                this.mLauncherOverlay.onScrollInteractionBegin();
            }
            this.mLastOverlayScroll = Math.abs(f / ((float) getViewportWidth()));
            this.mLauncherOverlay.onScrollChange(this.mLastOverlayScroll, this.mIsRtl);
        } else if (z2) {
            dampedOverScroll(f);
        }
        if (z) {
            this.mLauncherOverlay.onScrollChange(0.0f, this.mIsRtl);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mLauncher.isAllAppsVisible() || super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public boolean shouldFlingForVelocity(int i) {
        return Float.compare(Math.abs(this.mOverlayTranslation), 0.0f) == 0 && super.shouldFlingForVelocity(i);
    }

    public void onOverlayScrollChanged(float f) {
        if (Float.compare(f, 1.0f) == 0) {
            if (!this.mOverlayShown) {
                this.mLauncher.getUserEventDispatcher().logActionOnContainer(3, 3, 1, 0);
            }
            this.mOverlayShown = true;
        } else if (Float.compare(f, 0.0f) == 0) {
            if (this.mOverlayShown) {
                this.mLauncher.getUserEventDispatcher().logActionOnContainer(3, 4, 1, -1);
            }
            this.mOverlayShown = false;
        }
        float min = Math.min(1.0f, Math.max(f - 0.0f, 0.0f) / 1.0f);
        float interpolation = 1.0f - this.mAlphaInterpolator.getInterpolation(min);
        float measuredWidth = ((float) this.mLauncher.getDragLayer().getMeasuredWidth()) * min * 1.0f;
        if (this.mIsRtl) {
            measuredWidth = -measuredWidth;
        }
        this.mOverlayTranslation = measuredWidth;
        setWorkspaceTranslationAndAlpha(Direction.X, measuredWidth, interpolation);
        setHotseatTranslationAndAlpha(Direction.X, measuredWidth, interpolation);
    }

    public void setWorkspaceYTranslationAndAlpha(float f, float f2) {
        setWorkspaceTranslationAndAlpha(Direction.Y, f, f2);
    }

    private void setWorkspaceTranslationAndAlpha(Direction direction, float f, float f2) {
        Property access$100 = direction.viewProperty;
        this.mPageAlpha[direction.ordinal()] = f2;
        float f3 = this.mPageAlpha[0] * this.mPageAlpha[1];
        View childAt = getChildAt(getCurrentPage());
        if (childAt != null) {
            access$100.set(childAt, Float.valueOf(f));
            childAt.setAlpha(f3);
        }
        if (direction == Direction.Y) {
            View childAt2 = getChildAt(getNextPage());
            if (childAt2 != null) {
                access$100.set(childAt2, Float.valueOf(f));
                childAt2.setAlpha(f3);
            }
        }
        if (Float.compare(f, 0.0f) == 0) {
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt3 = getChildAt(childCount);
                access$100.set(childAt3, Float.valueOf(f));
                childAt3.setAlpha(f3);
            }
        }
    }

    public void setHotseatTranslationAndAlpha(Direction direction, float f, float f2) {
        Property access$100 = direction.viewProperty;
        if (direction != Direction.Y || !this.mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            access$100.set(this.mPageIndicator, Float.valueOf(f));
        }
        access$100.set(this.mLauncher.getHotseat(), Float.valueOf(f));
        setHotseatAlphaAtIndex(f2, direction.ordinal());
    }

    /* access modifiers changed from: private */
    public void setHotseatAlphaAtIndex(float f, int i) {
        this.mHotseatAlpha[i] = f;
        float f2 = this.mHotseatAlpha[0] * this.mHotseatAlpha[2];
        this.mLauncher.getHotseat().setAlpha(this.mHotseatAlpha[0] * this.mHotseatAlpha[1] * this.mHotseatAlpha[2]);
        this.mPageIndicator.setAlpha(f2);
    }

    public ValueAnimator createHotseatAlphaAnimator(float f) {
        if (Float.compare(f, this.mHotseatAlpha[2]) == 0) {
            return ValueAnimator.ofFloat(new float[]{0.0f, 0.0f});
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mHotseatAlpha[2], f});
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Workspace.this.setHotseatAlphaAtIndex(((Float) valueAnimator.getAnimatedValue()).floatValue(), 2);
            }
        });
        boolean isEnabled = ((AccessibilityManager) this.mLauncher.getSystemService("accessibility")).isEnabled();
        ofFloat.addUpdateListener(new AlphaUpdateListener(this.mLauncher.getHotseat(), isEnabled));
        ofFloat.addUpdateListener(new AlphaUpdateListener(this.mPageIndicator, isEnabled));
        return ofFloat;
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        super.notifyPageSwitchListener(i);
        if (i != this.mCurrentPage) {
            this.mLauncher.getUserEventDispatcher().logActionOnContainer(3, i < this.mCurrentPage ? 4 : 3, 1, i);
        }
        if (hasCustomContent() && getNextPage() == 0 && !this.mCustomContentShowing) {
            this.mCustomContentShowing = true;
            if (this.mCustomContentCallbacks != null) {
                this.mCustomContentCallbacks.onShow(false);
                this.mCustomContentShowTime = System.currentTimeMillis();
            }
        } else if (hasCustomContent() && getNextPage() != 0 && this.mCustomContentShowing) {
            this.mCustomContentShowing = false;
            if (this.mCustomContentCallbacks != null) {
                this.mCustomContentCallbacks.onHide();
            }
        }
    }

    /* access modifiers changed from: protected */
    public CustomContentCallbacks getCustomContentCallbacks() {
        return this.mCustomContentCallbacks;
    }

    /* access modifiers changed from: protected */
    public void setWallpaperDimension() {
        Utilities.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            public void run() {
                Point point = LauncherAppState.getIDP(Workspace.this.getContext()).defaultWallpaperSize;
                if (point.x != Workspace.this.mWallpaperManager.getDesiredMinimumWidth() || point.y != Workspace.this.mWallpaperManager.getDesiredMinimumHeight()) {
                    Workspace.this.mWallpaperManager.suggestDesiredDimensions(point.x, point.y);
                }
            }
        });
    }

    public void lockWallpaperToDefaultPage() {
        this.mWallpaperOffset.setLockToDefaultPage(true);
    }

    public void unlockWallpaperFromDefaultPageOnNextLayout() {
        if (this.mWallpaperOffset.isLockedToDefaultPage()) {
            this.mUnlockWallpaperFromDefaultPageOnLayout = true;
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int i, Runnable runnable) {
        snapToPage(i, 950, runnable);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int i, int i2, Runnable runnable) {
        if (this.mDelayedSnapToPageRunnable != null) {
            this.mDelayedSnapToPageRunnable.run();
        }
        this.mDelayedSnapToPageRunnable = runnable;
        snapToPage(i, i2);
    }

    public void snapToScreenId(long j) {
        snapToScreenId(j, null);
    }

    /* access modifiers changed from: protected */
    public void snapToScreenId(long j, Runnable runnable) {
        snapToPage(getPageIndexForScreenId(j), runnable);
    }

    public void computeScroll() {
        super.computeScroll();
        this.mWallpaperOffset.syncWithScroll();
    }

    public void computeScrollWithoutInvalidation() {
        computeScrollHelper(false);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent, float f) {
        if (!isSwitchingState()) {
            super.determineScrollingStart(motionEvent, f);
        }
    }

    public void announceForAccessibility(CharSequence charSequence) {
        if (!this.mLauncher.isAppsViewVisible()) {
            super.announceForAccessibility(charSequence);
        }
    }

    public void showOutlinesTemporarily() {
        if (!this.mIsPageInTransition && !isTouchActive()) {
            snapToPage(this.mCurrentPage);
        }
    }

    private void updatePageAlphaValues() {
        if (!workspaceInModalState() && !this.mIsSwitchingState) {
            int scrollX = getScrollX() + (getViewportWidth() / 2);
            for (int numCustomPages = numCustomPages(); numCustomPages < getChildCount(); numCustomPages++) {
                CellLayout cellLayout = (CellLayout) getChildAt(numCustomPages);
                if (cellLayout != null) {
                    float abs = 1.0f - Math.abs(getScrollProgress(scrollX, cellLayout, numCustomPages));
                    if (this.mWorkspaceFadeInAdjacentScreens) {
                        cellLayout.getShortcutsAndWidgets().setAlpha(abs);
                    } else {
                        cellLayout.getShortcutsAndWidgets().setImportantForAccessibility(abs > 0.0f ? 0 : 4);
                    }
                }
            }
        }
    }

    public boolean hasCustomContent() {
        return this.mScreenOrder.size() > 0 && ((Long) this.mScreenOrder.get(0)).longValue() == CUSTOM_CONTENT_SCREEN_ID;
    }

    public int numCustomPages() {
        return hasCustomContent() ? 1 : 0;
    }

    public boolean isOnOrMovingToCustomContent() {
        return hasCustomContent() && getNextPage() == 0;
    }

    private void updateStateForCustomContent() {
        float f;
        float f2;
        float f3 = 0.0f;
        if (hasCustomContent()) {
            int indexOf = this.mScreenOrder.indexOf(Long.valueOf(CUSTOM_CONTENT_SCREEN_ID));
            int scrollX = (getScrollX() - getScrollForPage(indexOf)) - getLayoutTransitionOffsetForPage(indexOf);
            float scrollForPage = (float) (getScrollForPage(indexOf + 1) - getScrollForPage(indexOf));
            float f4 = scrollForPage - ((float) scrollX);
            float f5 = f4 / scrollForPage;
            if (this.mIsRtl) {
                f = Math.min(0.0f, f4);
            } else {
                f = Math.max(0.0f, f4);
            }
            f2 = Math.max(0.0f, f5);
        } else {
            f2 = 0.0f;
            f = 0.0f;
        }
        if (Float.compare(f2, this.mLastCustomContentScrollProgress) != 0) {
            CellLayout cellLayout = (CellLayout) this.mWorkspaceScreens.get(CUSTOM_CONTENT_SCREEN_ID);
            if (f2 > 0.0f && cellLayout.getVisibility() != 0 && !workspaceInModalState()) {
                cellLayout.setVisibility(0);
            }
            this.mLastCustomContentScrollProgress = f2;
            if (this.mState == State.NORMAL) {
                DragLayer dragLayer = this.mLauncher.getDragLayer();
                if (f2 != 1.0f) {
                    f3 = f2 * 0.8f;
                }
                dragLayer.setBackgroundAlpha(f3);
            }
            if (this.mLauncher.getHotseat() != null) {
                this.mLauncher.getHotseat().setTranslationX(f);
            }
            if (this.mPageIndicator != null) {
                this.mPageIndicator.setTranslationX(f);
            }
            if (this.mCustomContentCallbacks != null) {
                this.mCustomContentCallbacks.onScrollProgressChanged(f2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IBinder windowToken = getWindowToken();
        this.mWallpaperOffset.setWindowToken(windowToken);
        computeScroll();
        this.mDragController.setWindowToken(windowToken);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mWallpaperOffset.setWindowToken(null);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        this.mWallpaperOffset.onResume();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mUnlockWallpaperFromDefaultPageOnLayout) {
            this.mWallpaperOffset.setLockToDefaultPage(false);
            this.mUnlockWallpaperFromDefaultPageOnLayout = false;
        }
        if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < getChildCount()) {
            this.mWallpaperOffset.syncWithScroll();
            this.mWallpaperOffset.jumpToFinal();
        }
        super.onLayout(z, i, i2, i3, i4);
        updatePageAlphaValues();
    }

    public int getDescendantFocusability() {
        if (workspaceInModalState()) {
            return 393216;
        }
        return super.getDescendantFocusability();
    }

    public boolean workspaceInModalState() {
        return this.mState != State.NORMAL;
    }

    public boolean workspaceIconsCanBeDragged() {
        return this.mState == State.NORMAL || this.mState == State.SPRING_LOADED;
    }

    /* access modifiers changed from: 0000 */
    public void updateChildrenLayersEnabled(boolean z) {
        boolean z2 = true;
        boolean z3 = this.mState == State.OVERVIEW || this.mIsSwitchingState;
        if (!z && !z3 && !this.mAnimatingViewIntoPlace && !isPageInTransition()) {
            z2 = false;
        }
        if (z2 != this.mChildrenLayersEnabled) {
            this.mChildrenLayersEnabled = z2;
            if (this.mChildrenLayersEnabled) {
                enableHwLayersOnVisiblePages();
                return;
            }
            for (int i = 0; i < getPageCount(); i++) {
                ((CellLayout) getChildAt(i)).enableHardwareLayer(false);
            }
        }
    }

    private void enableHwLayersOnVisiblePages() {
        if (this.mChildrenLayersEnabled) {
            int childCount = getChildCount();
            float viewportOffsetX = (float) getViewportOffsetX();
            float viewportWidth = ((float) getViewportWidth()) + viewportOffsetX;
            float scaleX = getScaleX();
            if (scaleX < 1.0f && scaleX > 0.0f) {
                float measuredWidth = (float) (getMeasuredWidth() / 2);
                viewportOffsetX = measuredWidth - ((measuredWidth - viewportOffsetX) / scaleX);
                viewportWidth = ((viewportWidth - measuredWidth) / scaleX) + measuredWidth;
            }
            int i = -1;
            int i2 = -1;
            for (int numCustomPages = numCustomPages(); numCustomPages < childCount; numCustomPages++) {
                View pageAt = getPageAt(numCustomPages);
                float left = (((float) pageAt.getLeft()) + pageAt.getTranslationX()) - ((float) getScrollX());
                if (left <= viewportWidth && left + ((float) pageAt.getMeasuredWidth()) >= viewportOffsetX) {
                    if (i2 == -1) {
                        i2 = numCustomPages;
                    }
                    i = numCustomPages;
                }
            }
            if (this.mForceDrawAdjacentPages) {
                i2 = Utilities.boundToRange(getCurrentPage() - 1, numCustomPages(), i);
                i = Utilities.boundToRange(getCurrentPage() + 1, i2, getPageCount() - 1);
            }
            if (i2 == i) {
                if (i < childCount - 1) {
                    i++;
                } else if (i2 > 0) {
                    i2--;
                }
            }
            int numCustomPages2 = numCustomPages();
            while (numCustomPages2 < childCount) {
                ((CellLayout) getPageAt(numCustomPages2)).enableHardwareLayer(i2 <= numCustomPages2 && numCustomPages2 <= i);
                numCustomPages2++;
            }
        }
    }

    public void buildPageHardwareLayers() {
        updateChildrenLayersEnabled(true);
        if (getWindowToken() != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                ((CellLayout) getChildAt(i)).buildHardwareLayer();
            }
        }
        updateChildrenLayersEnabled(false);
    }

    /* access modifiers changed from: protected */
    public void onWallpaperTap(MotionEvent motionEvent) {
        int[] iArr = this.mTempXY;
        getLocationOnScreen(iArr);
        int actionIndex = motionEvent.getActionIndex();
        iArr[0] = iArr[0] + ((int) motionEvent.getX(actionIndex));
        iArr[1] = iArr[1] + ((int) motionEvent.getY(actionIndex));
        this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), motionEvent.getAction() == 1 ? "android.wallpaper.tap" : "android.wallpaper.secondaryTap", iArr[0], iArr[1], 0, null);
    }

    public void prepareDragWithProvider(DragPreviewProvider dragPreviewProvider) {
        this.mOutlineProvider = dragPreviewProvider;
    }

    public void exitWidgetResizeMode() {
        this.mLauncher.getDragLayer().clearResizeFrame();
    }

    /* access modifiers changed from: protected */
    public void getFreeScrollPageRange(int[] iArr) {
        getOverviewModePages(iArr);
    }

    private void getOverviewModePages(int[] iArr) {
        int childCount = getChildCount() - 1;
        iArr[0] = Math.max(0, Math.min(numCustomPages(), getChildCount() - 1));
        iArr[1] = Math.max(0, childCount);
    }

    public void onStartReordering() {
        super.onStartReordering();
        disableLayoutTransitions();
    }

    public void onEndReordering() {
        super.onEndReordering();
        if (!this.mLauncher.isWorkspaceLoading()) {
            ArrayList arrayList = (ArrayList) this.mScreenOrder.clone();
            this.mScreenOrder.clear();
            int childCount = getChildCount();
            int i = 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                this.mScreenOrder.add(Long.valueOf(getIdForScreen((CellLayout) getChildAt(i2))));
            }
            while (true) {
                if (i >= arrayList.size()) {
                    break;
                } else if (this.mScreenOrder.get(i) != arrayList.get(i)) {
                    this.mLauncher.getUserEventDispatcher().logOverviewReorder();
                    break;
                } else {
                    i++;
                }
            }
            LauncherModel.updateWorkspaceScreenOrder(this.mLauncher, this.mScreenOrder);
            enableLayoutTransitions();
        }
    }

    public boolean isInOverviewMode() {
        return this.mState == State.OVERVIEW;
    }

    public void snapToPageFromOverView(int i) {
        this.mStateTransitionAnimation.snapToPageFromOverView(i);
    }

    /* access modifiers changed from: 0000 */
    public int getOverviewModeTranslationY() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        int overviewModeButtonBarHeight = deviceProfile.getOverviewModeButtonBarHeight();
        int normalChildHeight = (int) (this.mOverviewModeShrinkFactor * ((float) getNormalChildHeight()));
        Rect workspacePadding = deviceProfile.getWorkspacePadding(sTempRect);
        int i = this.mInsets.top + workspacePadding.top;
        int viewportHeight = (getViewportHeight() - this.mInsets.bottom) - workspacePadding.bottom;
        int i2 = this.mInsets.top;
        return (-(i + (((viewportHeight - i) - normalChildHeight) / 2))) + i2 + (((((getViewportHeight() - this.mInsets.bottom) - overviewModeButtonBarHeight) - i2) - normalChildHeight) / 2);
    }

    /* access modifiers changed from: 0000 */
    public float getSpringLoadedTranslationY() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        if (deviceProfile.isVerticalBarLayout() || getChildCount() == 0) {
            return 0.0f;
        }
        float f = (float) (this.mInsets.top + deviceProfile.dropTargetBarSizePx);
        float height = (float) (getHeight() / 2);
        return ((f + (((((float) (((getViewportHeight() - this.mInsets.bottom) - deviceProfile.getWorkspacePadding(sTempRect).bottom) - deviceProfile.workspaceSpringLoadedBottomSpace)) - f) - (deviceProfile.workspaceSpringLoadShrinkFactor * ((float) getNormalChildHeight()))) / 2.0f)) - ((((float) getTop()) + height) - ((height - ((float) getChildAt(0).getTop())) * deviceProfile.workspaceSpringLoadShrinkFactor))) / deviceProfile.workspaceSpringLoadShrinkFactor;
    }

    /* access modifiers changed from: 0000 */
    public float getOverviewModeShrinkFactor() {
        return this.mOverviewModeShrinkFactor;
    }

    public Animator setStateWithAnimation(State state, boolean z, AnimationLayerSet animationLayerSet) {
        State state2 = this.mState;
        this.mState = state;
        AnimatorSet animationToState = this.mStateTransitionAnimation.getAnimationToState(state2, state, z, animationLayerSet);
        boolean z2 = !state2.shouldUpdateWidget && state.shouldUpdateWidget;
        updateAccessibilityFlags();
        if (z2) {
            this.mLauncher.notifyWidgetProvidersChanged();
        }
        onPrepareStateTransition(this.mState.hasMultipleVisiblePages);
        StateTransitionListener stateTransitionListener = new StateTransitionListener();
        if (z) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            ofFloat.addUpdateListener(stateTransitionListener);
            animationToState.play(ofFloat);
            animationToState.addListener(stateTransitionListener);
        } else {
            stateTransitionListener.onAnimationStart(null);
            stateTransitionListener.onAnimationEnd(null);
        }
        return animationToState;
    }

    public State getState() {
        return this.mState;
    }

    public void updateAccessibilityFlags() {
        if (!this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag()) {
            int pageCount = getPageCount();
            for (int numCustomPages = numCustomPages(); numCustomPages < pageCount; numCustomPages++) {
                updateAccessibilityFlags((CellLayout) getPageAt(numCustomPages), numCustomPages);
            }
            setImportantForAccessibility((this.mState == State.NORMAL || this.mState == State.OVERVIEW) ? 0 : 4);
        }
    }

    private void updateAccessibilityFlags(CellLayout cellLayout, int i) {
        int i2 = 4;
        if (this.mState == State.OVERVIEW) {
            cellLayout.setImportantForAccessibility(1);
            cellLayout.getShortcutsAndWidgets().setImportantForAccessibility(4);
            cellLayout.setContentDescription(getPageDescription(i));
            if (!FeatureFlags.QSB_ON_FIRST_SCREEN || i > 0) {
                if (this.mPagesAccessibilityDelegate == null) {
                    this.mPagesAccessibilityDelegate = new OverviewScreenAccessibilityDelegate(this);
                }
                cellLayout.setAccessibilityDelegate(this.mPagesAccessibilityDelegate);
                return;
            }
            return;
        }
        if (this.mState == State.NORMAL) {
            i2 = 0;
        }
        cellLayout.setImportantForAccessibility(2);
        cellLayout.getShortcutsAndWidgets().setImportantForAccessibility(i2);
        cellLayout.setContentDescription(null);
        cellLayout.setAccessibilityDelegate(null);
    }

    public void onPrepareStateTransition(boolean z) {
        this.mIsSwitchingState = true;
        this.mTransitionProgress = 0.0f;
        if (z) {
            this.mForceDrawAdjacentPages = true;
        }
        invalidate();
        updateChildrenLayersEnabled(false);
        hideCustomContentIfNecessary();
    }

    public void onEndStateTransition() {
        this.mIsSwitchingState = false;
        updateChildrenLayersEnabled(false);
        showCustomContentIfNecessary();
        this.mForceDrawAdjacentPages = false;
        this.mTransitionProgress = 1.0f;
    }

    /* access modifiers changed from: 0000 */
    public void updateCustomContentVisibility() {
        setCustomContentVisibility(this.mState == State.NORMAL ? 0 : 4);
    }

    /* access modifiers changed from: 0000 */
    public void setCustomContentVisibility(int i) {
        if (hasCustomContent()) {
            ((CellLayout) this.mWorkspaceScreens.get(CUSTOM_CONTENT_SCREEN_ID)).setVisibility(i);
        }
    }

    /* access modifiers changed from: 0000 */
    public void showCustomContentIfNecessary() {
        if ((this.mState == State.NORMAL) && hasCustomContent()) {
            ((CellLayout) this.mWorkspaceScreens.get(CUSTOM_CONTENT_SCREEN_ID)).setVisibility(0);
        }
    }

    /* access modifiers changed from: 0000 */
    public void hideCustomContentIfNecessary() {
        if ((this.mState != State.NORMAL) && hasCustomContent()) {
            disableLayoutTransitions();
            ((CellLayout) this.mWorkspaceScreens.get(CUSTOM_CONTENT_SCREEN_ID)).setVisibility(4);
            enableLayoutTransitions();
        }
    }

    public void startDrag(CellInfo cellInfo, DragOptions dragOptions) {
        View view = cellInfo.cell;
        this.mDragInfo = cellInfo;
        view.setVisibility(4);
        if (dragOptions.isAccessibleDrag) {
            this.mDragController.addDragListener(new AccessibleDragListenerAdapter(this, 2) {
                /* access modifiers changed from: protected */
                public void enableAccessibleDrag(boolean z) {
                    super.enableAccessibleDrag(z);
                    setEnableForLayout(Workspace.this.mLauncher.getHotseat().getLayout(), z);
                    Workspace.this.setOnClickListener(z ? null : Workspace.this.mLauncher);
                }
            });
        }
        beginDragShared(view, this, dragOptions);
    }

    public void beginDragShared(View view, DragSource dragSource, DragOptions dragOptions) {
        Object tag = view.getTag();
        if (tag instanceof ItemInfo) {
            beginDragShared(view, dragSource, (ItemInfo) tag, new DragPreviewProvider(view), dragOptions);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Drag started with a view that has no tag set. This will cause a crash (issue 11627249) down the line. View: ");
        sb.append(view);
        sb.append("  tag: ");
        sb.append(view.getTag());
        throw new IllegalStateException(sb.toString());
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x009f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.launcher3.dragndrop.DragView beginDragShared(android.view.View r15, com.android.launcher3.DragSource r16, com.android.launcher3.ItemInfo r17, com.android.launcher3.graphics.DragPreviewProvider r18, com.android.launcher3.dragndrop.DragOptions r19) {
        /*
            r14 = this;
            r0 = r14
            r1 = r15
            r2 = r18
            r10 = r19
            r15.clearFocus()
            r3 = 0
            r15.setPressed(r3)
            r0.mOutlineProvider = r2
            android.graphics.Canvas r4 = r0.mCanvas
            android.graphics.Bitmap r11 = r2.createDragBitmap(r4)
            int r4 = r2.previewPadding
            int r4 = r4 / 2
            int[] r5 = r0.mTempXY
            float r9 = r2.getScaleAndPosition(r11, r5)
            int[] r5 = r0.mTempXY
            r5 = r5[r3]
            int[] r6 = r0.mTempXY
            r7 = 1
            r6 = r6[r7]
            com.android.launcher3.Launcher r7 = r0.mLauncher
            com.android.launcher3.DeviceProfile r7 = r7.getDeviceProfile()
            boolean r8 = r1 instanceof com.android.launcher3.BubbleTextView
            r12 = 0
            if (r8 == 0) goto L_0x004b
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r3 = r1
            com.android.launcher3.BubbleTextView r3 = (com.android.launcher3.BubbleTextView) r3
            r3.getIconBounds(r2)
            int r3 = r2.top
            int r6 = r6 + r3
            android.graphics.Point r3 = new android.graphics.Point
            int r7 = -r4
            r3.<init>(r7, r4)
            r12 = r2
            r7 = r3
        L_0x0049:
            r4 = r6
            goto L_0x0079
        L_0x004b:
            boolean r13 = r1 instanceof com.android.launcher3.folder.FolderIcon
            if (r13 == 0) goto L_0x006b
            int r2 = r7.folderIconSizePx
            android.graphics.Point r7 = new android.graphics.Point
            int r12 = -r4
            int r13 = r15.getPaddingTop()
            int r4 = r4 - r13
            r7.<init>(r12, r4)
            android.graphics.Rect r4 = new android.graphics.Rect
            int r12 = r15.getPaddingTop()
            int r13 = r15.getWidth()
            r4.<init>(r3, r12, r13, r2)
            r12 = r4
            goto L_0x0049
        L_0x006b:
            boolean r2 = r2 instanceof com.android.launcher3.shortcuts.ShortcutDragPreviewProvider
            if (r2 == 0) goto L_0x0077
            android.graphics.Point r2 = new android.graphics.Point
            int r3 = -r4
            r2.<init>(r3, r4)
            r7 = r2
            goto L_0x0049
        L_0x0077:
            r4 = r6
            r7 = r12
        L_0x0079:
            if (r8 == 0) goto L_0x0081
            r2 = r1
            com.android.launcher3.BubbleTextView r2 = (com.android.launcher3.BubbleTextView) r2
            r2.clearPressedBackground()
        L_0x0081:
            android.view.ViewParent r2 = r15.getParent()
            boolean r2 = r2 instanceof com.android.launcher3.ShortcutAndWidgetContainer
            if (r2 == 0) goto L_0x0091
            android.view.ViewParent r2 = r15.getParent()
            com.android.launcher3.ShortcutAndWidgetContainer r2 = (com.android.launcher3.ShortcutAndWidgetContainer) r2
            r0.mDragSourceInternal = r2
        L_0x0091:
            if (r8 == 0) goto L_0x00ae
            boolean r2 = r10.isAccessibleDrag
            if (r2 != 0) goto L_0x00ae
            com.android.launcher3.BubbleTextView r1 = (com.android.launcher3.BubbleTextView) r1
            com.android.launcher3.popup.PopupContainerWithArrow r1 = com.android.launcher3.popup.PopupContainerWithArrow.showForIcon(r1)
            if (r1 == 0) goto L_0x00ae
            com.android.launcher3.dragndrop.DragOptions$PreDragCondition r1 = r1.createPreDragCondition()
            r10.preDragCondition = r1
            com.android.launcher3.Launcher r1 = r0.mLauncher
            com.android.launcher3.logging.UserEventDispatcher r1 = r1.getUserEventDispatcher()
            r1.resetElapsedContainerMillis()
        L_0x00ae:
            com.android.launcher3.dragndrop.DragController r1 = r0.mDragController
            r2 = r11
            r3 = r5
            r5 = r16
            r6 = r17
            r8 = r12
            r10 = r19
            com.android.launcher3.dragndrop.DragView r1 = r1.startDrag(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            float r2 = r16.getIntrinsicIconScaleFactor()
            r1.setIntrinsicIconScaleFactor(r2)
            r11.recycle()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.beginDragShared(android.view.View, com.android.launcher3.DragSource, com.android.launcher3.ItemInfo, com.android.launcher3.graphics.DragPreviewProvider, com.android.launcher3.dragndrop.DragOptions):com.android.launcher3.dragndrop.DragView");
    }

    private boolean transitionStateShouldAllowDrop() {
        return (!isSwitchingState() || this.mTransitionProgress > ALLOW_DROP_TRANSITION_PROGRESS) && (this.mState == State.NORMAL || this.mState == State.SPRING_LOADED);
    }

    public boolean acceptDrop(DragObject dragObject) {
        CellLayout cellLayout;
        int i;
        int i2;
        int i3;
        int i4;
        DragObject dragObject2 = dragObject;
        CellLayout cellLayout2 = this.mDropToLayout;
        if (dragObject2.dragSource == this) {
            cellLayout = cellLayout2;
        } else if (cellLayout2 == null || !transitionStateShouldAllowDrop()) {
            return false;
        } else {
            this.mDragViewVisualCenter = dragObject2.getVisualCenter(this.mDragViewVisualCenter);
            if (this.mLauncher.isHotseatLayout(cellLayout2)) {
                mapPointFromSelfToHotseatLayout(this.mLauncher.getHotseat(), this.mDragViewVisualCenter);
            } else {
                mapPointFromSelfToChild(cellLayout2, this.mDragViewVisualCenter);
            }
            if (this.mDragInfo != null) {
                CellInfo cellInfo = this.mDragInfo;
                int i5 = cellInfo.spanX;
                i = cellInfo.spanY;
                i2 = i5;
            } else {
                i2 = dragObject2.dragInfo.spanX;
                i = dragObject2.dragInfo.spanY;
            }
            if (dragObject2.dragInfo instanceof PendingAddWidgetInfo) {
                i4 = ((PendingAddWidgetInfo) dragObject2.dragInfo).minSpanX;
                i3 = ((PendingAddWidgetInfo) dragObject2.dragInfo).minSpanY;
            } else {
                i4 = i2;
                i3 = i;
            }
            this.mTargetCell = findNearestArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], i4, i3, cellLayout2, this.mTargetCell);
            float distanceFromCell = cellLayout2.getDistanceFromCell(this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1], this.mTargetCell);
            if (this.mCreateUserFolderOnDrop) {
                if (willCreateUserFolder(dragObject2.dragInfo, cellLayout2, this.mTargetCell, distanceFromCell, true)) {
                    return true;
                }
            }
            if (this.mAddToExistingFolderOnDrop && willAddToExistingUserFolder(dragObject2.dragInfo, cellLayout2, this.mTargetCell, distanceFromCell)) {
                return true;
            }
            cellLayout = cellLayout2;
            this.mTargetCell = cellLayout2.performReorder((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], i4, i3, i2, i, null, this.mTargetCell, new int[2], 4);
            if (!(this.mTargetCell[0] >= 0 && this.mTargetCell[1] >= 0)) {
                onNoCellFound(cellLayout);
                return false;
            }
        }
        if (getIdForScreen(cellLayout) == -201) {
            commitExtraEmptyScreen();
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean willCreateUserFolder(ItemInfo itemInfo, CellLayout cellLayout, int[] iArr, float f, boolean z) {
        if (f > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        return willCreateUserFolder(itemInfo, cellLayout.getChildAt(iArr[0], iArr[1]), z);
    }

    /* access modifiers changed from: 0000 */
    public boolean willCreateUserFolder(ItemInfo itemInfo, View view, boolean z) {
        boolean z2 = false;
        if (view != null) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams.useTmpCoords && !(layoutParams.tmpCellX == layoutParams.cellX && layoutParams.tmpCellY == layoutParams.cellY)) {
                return false;
            }
        }
        boolean z3 = this.mDragInfo != null && view == this.mDragInfo.cell;
        if (view == null || z3 || (z && !this.mCreateUserFolderOnDrop)) {
            return false;
        }
        boolean z4 = view.getTag() instanceof ShortcutInfo;
        boolean z5 = itemInfo.itemType == 0 || itemInfo.itemType == 1 || itemInfo.itemType == 6;
        if (z4 && z5) {
            z2 = true;
        }
        return z2;
    }

    /* access modifiers changed from: 0000 */
    public boolean willAddToExistingUserFolder(ItemInfo itemInfo, CellLayout cellLayout, int[] iArr, float f) {
        if (f > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        return willAddToExistingUserFolder(itemInfo, cellLayout.getChildAt(iArr[0], iArr[1]));
    }

    /* access modifiers changed from: 0000 */
    public boolean willAddToExistingUserFolder(ItemInfo itemInfo, View view) {
        if (view != null) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams.useTmpCoords && !(layoutParams.tmpCellX == layoutParams.cellX && layoutParams.tmpCellY == layoutParams.cellY)) {
                return false;
            }
        }
        if (!(view instanceof FolderIcon) || !((FolderIcon) view).acceptDrop(itemInfo)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean createUserFolderIfNecessary(View view, long j, CellLayout cellLayout, int[] iArr, float f, boolean z, DragView dragView, Runnable runnable) {
        boolean z2;
        CellLayout cellLayout2 = cellLayout;
        boolean z3 = false;
        if (f > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        View childAt = cellLayout2.getChildAt(iArr[0], iArr[1]);
        if (this.mDragInfo != null) {
            CellLayout parentCellLayoutForView = getParentCellLayoutForView(this.mDragInfo.cell);
            if (this.mDragInfo.cellX == iArr[0] && this.mDragInfo.cellY == iArr[1] && parentCellLayoutForView == cellLayout2) {
                z2 = true;
                if (childAt != null || z2 || !this.mCreateUserFolderOnDrop) {
                    return false;
                }
                this.mCreateUserFolderOnDrop = false;
                long idForScreen = getIdForScreen(cellLayout2);
                boolean z4 = view.getTag() instanceof ShortcutInfo;
                if (!(childAt.getTag() instanceof ShortcutInfo) || !z4) {
                    return false;
                }
                ShortcutInfo shortcutInfo = (ShortcutInfo) view.getTag();
                ShortcutInfo shortcutInfo2 = (ShortcutInfo) childAt.getTag();
                if (!z) {
                    getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
                }
                Rect rect = new Rect();
                float descendantRectRelativeToSelf = this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(childAt, rect);
                cellLayout2.removeView(childAt);
                FolderIcon addFolder = this.mLauncher.addFolder(cellLayout, j, idForScreen, iArr[0], iArr[1]);
                shortcutInfo2.cellX = -1;
                shortcutInfo2.cellY = -1;
                shortcutInfo.cellX = -1;
                shortcutInfo.cellY = -1;
                if (dragView != null) {
                    z3 = true;
                }
                if (z3) {
                    addFolder.setFolderBackground(this.mFolderCreateBg);
                    this.mFolderCreateBg = new PreviewBackground();
                    addFolder.performCreateAnimation(shortcutInfo2, childAt, shortcutInfo, dragView, rect, descendantRectRelativeToSelf, runnable);
                } else {
                    addFolder.prepareCreateAnimation(childAt);
                    addFolder.addItem(shortcutInfo2);
                    addFolder.addItem(shortcutInfo);
                }
                return true;
            }
        }
        z2 = false;
        if (childAt != null) {
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public boolean addToExistingFolderIfNecessary(View view, CellLayout cellLayout, int[] iArr, float f, DragObject dragObject, boolean z) {
        if (f > this.mMaxDistanceForFolderCreation) {
            return false;
        }
        View childAt = cellLayout.getChildAt(iArr[0], iArr[1]);
        if (!this.mAddToExistingFolderOnDrop) {
            return false;
        }
        this.mAddToExistingFolderOnDrop = false;
        if (childAt instanceof FolderIcon) {
            FolderIcon folderIcon = (FolderIcon) childAt;
            if (folderIcon.acceptDrop(dragObject.dragInfo)) {
                folderIcon.onDrop(dragObject);
                if (!z) {
                    getParentCellLayoutForView(this.mDragInfo.cell).removeView(this.mDragInfo.cell);
                }
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: type inference failed for: r11v5 */
    /* JADX WARNING: type inference failed for: r11v6 */
    /* JADX WARNING: type inference failed for: r11v7 */
    /* JADX WARNING: type inference failed for: r11v12 */
    /* JADX WARNING: type inference failed for: r11v13 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02a8  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0165  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01af  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01d7  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01dc  */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDrop(com.android.launcher3.DropTarget.DragObject r49) {
        /*
            r48 = this;
            r10 = r48
            r11 = r49
            float[] r0 = r10.mDragViewVisualCenter
            float[] r0 = r11.getVisualCenter(r0)
            r10.mDragViewVisualCenter = r0
            com.android.launcher3.CellLayout r15 = r10.mDropToLayout
            if (r15 == 0) goto L_0x0029
            com.android.launcher3.Launcher r0 = r10.mLauncher
            boolean r0 = r0.isHotseatLayout(r15)
            if (r0 == 0) goto L_0x0024
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.Hotseat r0 = r0.getHotseat()
            float[] r1 = r10.mDragViewVisualCenter
            r10.mapPointFromSelfToHotseatLayout(r0, r1)
            goto L_0x0029
        L_0x0024:
            float[] r0 = r10.mDragViewVisualCenter
            r10.mapPointFromSelfToChild(r15, r0)
        L_0x0029:
            com.android.launcher3.DragSource r0 = r11.dragSource
            r14 = 2
            r13 = 1
            r12 = 0
            if (r0 == r10) goto L_0x0047
            int[] r0 = new int[r14]
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r12]
            int r1 = (int) r1
            r0[r12] = r1
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r13]
            int r1 = (int) r1
            r0[r13] = r1
            r10.onDropExternal(r0, r15, r11)
        L_0x0043:
            r8 = r11
            r11 = 0
            goto L_0x0363
        L_0x0047:
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            if (r0 == 0) goto L_0x0043
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            android.view.View r9 = r0.cell
            r23 = -1
            if (r15 == 0) goto L_0x02d3
            boolean r0 = r11.cancelled
            if (r0 != 0) goto L_0x02d3
            com.android.launcher3.CellLayout r0 = r10.getParentCellLayoutForView(r9)
            if (r0 == r15) goto L_0x0060
            r24 = 1
            goto L_0x0062
        L_0x0060:
            r24 = 0
        L_0x0062:
            com.android.launcher3.Launcher r0 = r10.mLauncher
            boolean r25 = r0.isHotseatLayout(r15)
            r26 = -101(0xffffffffffffff9b, double:NaN)
            if (r25 == 0) goto L_0x006f
            r30 = r26
            goto L_0x0073
        L_0x006f:
            r0 = -100
            r30 = r0
        L_0x0073:
            int[] r0 = r10.mTargetCell
            r0 = r0[r12]
            if (r0 >= 0) goto L_0x007f
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            long r0 = r0.screenId
        L_0x007d:
            r7 = r0
            goto L_0x0084
        L_0x007f:
            long r0 = r10.getIdForScreen(r15)
            goto L_0x007d
        L_0x0084:
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            if (r0 == 0) goto L_0x008e
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            int r0 = r0.spanX
            r6 = r0
            goto L_0x008f
        L_0x008e:
            r6 = 1
        L_0x008f:
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            if (r0 == 0) goto L_0x0099
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            int r0 = r0.spanY
            r5 = r0
            goto L_0x009a
        L_0x0099:
            r5 = 1
        L_0x009a:
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r12]
            int r1 = (int) r0
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r13]
            int r2 = (int) r0
            int[] r4 = r10.mTargetCell
            r0 = r48
            r3 = r6
            r16 = r4
            r4 = r5
            r14 = r5
            r5 = r15
            r39 = r6
            r6 = r16
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r10.mTargetCell = r0
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r12]
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r13]
            int[] r2 = r10.mTargetCell
            float r16 = r15.getDistanceFromCell(r0, r1, r2)
            int[] r5 = r10.mTargetCell
            r17 = 0
            com.android.launcher3.dragndrop.DragView r6 = r11.dragView
            r18 = 0
            r0 = r48
            r1 = r9
            r2 = r30
            r4 = r15
            r19 = r6
            r6 = r16
            r40 = r14
            r13 = r7
            r7 = r17
            r8 = r19
            r41 = r9
            r9 = r18
            boolean r0 = r0.createUserFolderIfNecessary(r1, r2, r4, r5, r6, r7, r8, r9)
            if (r0 == 0) goto L_0x00ea
            return
        L_0x00ea:
            int[] r3 = r10.mTargetCell
            r6 = 0
            r0 = r48
            r1 = r41
            r2 = r15
            r4 = r16
            r5 = r49
            boolean r0 = r0.addToExistingFolderIfNecessary(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L_0x00fd
            return
        L_0x00fd:
            com.android.launcher3.ItemInfo r9 = r11.dragInfo
            int r0 = r9.spanX
            int r1 = r9.spanY
            int r2 = r9.minSpanX
            if (r2 <= 0) goto L_0x010f
            int r2 = r9.minSpanY
            if (r2 <= 0) goto L_0x010f
            int r0 = r9.minSpanX
            int r1 = r9.minSpanY
        L_0x010f:
            r16 = r1
            long r1 = r9.screenId
            int r1 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r1 != 0) goto L_0x0131
            long r1 = r9.container
            int r1 = (r1 > r30 ? 1 : (r1 == r30 ? 0 : -1))
            if (r1 != 0) goto L_0x0131
            int r1 = r9.cellX
            int[] r2 = r10.mTargetCell
            r2 = r2[r12]
            if (r1 != r2) goto L_0x0131
            int r1 = r9.cellY
            int[] r2 = r10.mTargetCell
            r3 = 1
            r2 = r2[r3]
            if (r1 != r2) goto L_0x0131
            r42 = 1
            goto L_0x0133
        L_0x0131:
            r42 = 0
        L_0x0133:
            if (r42 == 0) goto L_0x013c
            boolean r1 = r10.mIsSwitchingState
            if (r1 == 0) goto L_0x013c
            r43 = 1
            goto L_0x013e
        L_0x013c:
            r43 = 0
        L_0x013e:
            boolean r1 = r48.isFinishedSwitchingState()
            if (r1 != 0) goto L_0x015b
            if (r43 != 0) goto L_0x015b
            int[] r1 = r10.mTargetCell
            r1 = r1[r12]
            int[] r2 = r10.mTargetCell
            r3 = 1
            r2 = r2[r3]
            r3 = r39
            r4 = r40
            boolean r1 = r15.isRegionVacant(r1, r2, r3, r4)
            if (r1 != 0) goto L_0x015f
            r1 = 1
            goto L_0x0160
        L_0x015b:
            r3 = r39
            r4 = r40
        L_0x015f:
            r1 = 0
        L_0x0160:
            r2 = 2
            int[] r5 = new int[r2]
            if (r1 == 0) goto L_0x0175
            int[] r0 = r10.mTargetCell
            int[] r3 = r10.mTargetCell
            r6 = 1
            r3[r6] = r23
            r0[r12] = r23
            r44 = r13
            r8 = r15
            r11 = 0
            r38 = 2
            goto L_0x019e
        L_0x0175:
            r6 = 1
            float[] r7 = r10.mDragViewVisualCenter
            r7 = r7[r12]
            int r7 = (int) r7
            float[] r8 = r10.mDragViewVisualCenter
            r8 = r8[r6]
            int r8 = (int) r8
            int[] r2 = r10.mTargetCell
            r22 = 2
            r11 = 0
            r12 = r15
            r44 = r13
            r13 = r7
            r38 = 2
            r14 = r8
            r8 = r15
            r15 = r0
            r17 = r3
            r18 = r4
            r19 = r41
            r20 = r2
            r21 = r5
            int[] r0 = r12.performReorder(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            r10.mTargetCell = r0
        L_0x019e:
            int[] r0 = r10.mTargetCell
            r0 = r0[r11]
            if (r0 < 0) goto L_0x01ac
            int[] r0 = r10.mTargetCell
            r0 = r0[r6]
            if (r0 < 0) goto L_0x01ac
            r0 = 1
            goto L_0x01ad
        L_0x01ac:
            r0 = 0
        L_0x01ad:
            if (r0 == 0) goto L_0x01d7
            r12 = r41
            boolean r2 = r12 instanceof android.appwidget.AppWidgetHostView
            if (r2 == 0) goto L_0x01d9
            r2 = r5[r11]
            int r3 = r9.spanX
            if (r2 != r3) goto L_0x01c1
            r2 = r5[r6]
            int r3 = r9.spanY
            if (r2 == r3) goto L_0x01d9
        L_0x01c1:
            r2 = r5[r11]
            r9.spanX = r2
            r2 = r5[r6]
            r9.spanY = r2
            r2 = r12
            android.appwidget.AppWidgetHostView r2 = (android.appwidget.AppWidgetHostView) r2
            com.android.launcher3.Launcher r3 = r10.mLauncher
            r4 = r5[r11]
            r5 = r5[r6]
            com.android.launcher3.AppWidgetResizeFrame.updateWidgetSizeRanges(r2, r3, r4, r5)
            r13 = 1
            goto L_0x01da
        L_0x01d7:
            r12 = r41
        L_0x01d9:
            r13 = 0
        L_0x01da:
            if (r0 == 0) goto L_0x02a8
            int r0 = r10.mCurrentPage
            long r0 = r10.getScreenIdForPageIndex(r0)
            r14 = r44
            int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r0 == 0) goto L_0x01f4
            if (r25 != 0) goto L_0x01f4
            int r0 = r10.getPageIndexForScreenId(r14)
            r10.snapToPage(r0)
            r16 = r0
            goto L_0x01f6
        L_0x01f4:
            r16 = -1
        L_0x01f6:
            java.lang.Object r0 = r12.getTag()
            r7 = r0
            com.android.launcher3.ItemInfo r7 = (com.android.launcher3.ItemInfo) r7
            if (r24 == 0) goto L_0x0230
            com.android.launcher3.CellLayout r0 = r10.getParentCellLayoutForView(r12)
            if (r0 == 0) goto L_0x0208
            r0.removeView(r12)
        L_0x0208:
            int[] r0 = r10.mTargetCell
            r17 = r0[r11]
            int[] r0 = r10.mTargetCell
            r18 = r0[r6]
            int r4 = r7.spanX
            int r5 = r7.spanY
            r0 = r48
            r1 = r12
            r2 = r30
            r19 = r4
            r20 = r5
            r4 = r14
            r6 = r17
            r17 = r7
            r7 = r18
            r46 = r8
            r8 = r19
            r47 = r9
            r9 = r20
            r0.addInScreen(r1, r2, r4, r6, r7, r8, r9)
            goto L_0x0236
        L_0x0230:
            r17 = r7
            r46 = r8
            r47 = r9
        L_0x0236:
            android.view.ViewGroup$LayoutParams r0 = r12.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r0 = (com.android.launcher3.CellLayout.LayoutParams) r0
            int[] r1 = r10.mTargetCell
            r1 = r1[r11]
            r0.tmpCellX = r1
            r0.cellX = r1
            int[] r1 = r10.mTargetCell
            r2 = 1
            r1 = r1[r2]
            r0.tmpCellY = r1
            r0.cellY = r1
            r1 = r47
            int r3 = r1.spanX
            r0.cellHSpan = r3
            int r3 = r1.spanY
            r0.cellVSpan = r3
            r0.isLockedToGrid = r2
            int r3 = (r30 > r26 ? 1 : (r30 == r26 ? 0 : -1))
            if (r3 == 0) goto L_0x0288
            boolean r3 = r12 instanceof com.android.launcher3.LauncherAppWidgetHostView
            if (r3 == 0) goto L_0x0288
            r9 = r12
            com.android.launcher3.LauncherAppWidgetHostView r9 = (com.android.launcher3.LauncherAppWidgetHostView) r9
            android.appwidget.AppWidgetProviderInfo r3 = r9.getAppWidgetInfo()
            com.android.launcher3.LauncherAppWidgetProviderInfo r3 = (com.android.launcher3.LauncherAppWidgetProviderInfo) r3
            if (r3 == 0) goto L_0x0288
            int r4 = r3.resizeMode
            if (r4 != 0) goto L_0x0278
            int r4 = r3.minSpanX
            if (r4 > r2) goto L_0x0278
            int r3 = r3.minSpanY
            if (r3 <= r2) goto L_0x0288
        L_0x0278:
            r8 = r49
            boolean r3 = r8.accessibleDrag
            if (r3 != 0) goto L_0x028a
            com.android.launcher3.Workspace$9 r3 = new com.android.launcher3.Workspace$9
            r4 = r46
            r3.<init>(r9, r4)
            r10.mDelayedResizeRunnable = r3
            goto L_0x028a
        L_0x0288:
            r8 = r49
        L_0x028a:
            com.android.launcher3.Launcher r3 = r10.mLauncher
            com.android.launcher3.model.ModelWriter r28 = r3.getModelWriter()
            int r3 = r0.cellX
            int r0 = r0.cellY
            int r4 = r1.spanX
            int r1 = r1.spanY
            r29 = r17
            r32 = r14
            r34 = r3
            r35 = r0
            r36 = r4
            r37 = r1
            r28.modifyItemInDatabase(r29, r30, r32, r34, r35, r36, r37)
            goto L_0x02e0
        L_0x02a8:
            r4 = r8
            r2 = 1
            r8 = r49
            if (r1 != 0) goto L_0x02b1
            r10.onNoCellFound(r4)
        L_0x02b1:
            android.view.ViewGroup$LayoutParams r0 = r12.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r0 = (com.android.launcher3.CellLayout.LayoutParams) r0
            int[] r1 = r10.mTargetCell
            int r3 = r0.cellX
            r1[r11] = r3
            int[] r1 = r10.mTargetCell
            int r0 = r0.cellY
            r1[r2] = r0
            android.view.ViewParent r0 = r12.getParent()
            android.view.ViewParent r0 = r0.getParent()
            com.android.launcher3.CellLayout r0 = (com.android.launcher3.CellLayout) r0
            r0.markCellsAsOccupiedForView(r12)
            r16 = -1
            goto L_0x02e0
        L_0x02d3:
            r12 = r9
            r8 = r11
            r2 = 1
            r11 = 0
            r38 = 2
            r13 = 0
            r16 = -1
            r42 = 0
            r43 = 0
        L_0x02e0:
            android.view.ViewParent r0 = r12.getParent()
            android.view.ViewParent r0 = r0.getParent()
            r9 = r0
            com.android.launcher3.CellLayout r9 = (com.android.launcher3.CellLayout) r9
            com.android.launcher3.Workspace$10 r4 = new com.android.launcher3.Workspace$10
            r4.<init>()
            r10.mAnimatingViewIntoPlace = r2
            com.android.launcher3.dragndrop.DragView r0 = r8.dragView
            boolean r0 = r0.hasDrawn()
            if (r0 == 0) goto L_0x0359
            if (r43 == 0) goto L_0x031d
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.dragndrop.DragController r0 = r0.getDragController()
            java.lang.Runnable r1 = r10.mDelayedResizeRunnable
            com.android.launcher3.WorkspaceStateTransitionAnimation r2 = r10.mStateTransitionAnimation
            int r2 = r2.mSpringLoadedTransitionTime
            r0.animateDragViewToOriginalPosition(r1, r12, r2)
            com.android.launcher3.Launcher r0 = r10.mLauncher
            r0.exitSpringLoadedDragMode()
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.DropTargetBar r0 = r0.getDropTargetBar()
            r0.onDragEnd()
            r9.onDropChild(r12)
            return
        L_0x031d:
            java.lang.Object r0 = r12.getTag()
            r1 = r0
            com.android.launcher3.ItemInfo r1 = (com.android.launcher3.ItemInfo) r1
            int r0 = r1.itemType
            r3 = 4
            if (r0 == r3) goto L_0x0330
            int r0 = r1.itemType
            r3 = 5
            if (r0 != r3) goto L_0x032f
            goto L_0x0330
        L_0x032f:
            r2 = 0
        L_0x0330:
            if (r2 == 0) goto L_0x0342
            if (r13 == 0) goto L_0x0336
            r5 = 2
            goto L_0x0337
        L_0x0336:
            r5 = 0
        L_0x0337:
            com.android.launcher3.dragndrop.DragView r3 = r8.dragView
            r7 = 0
            r0 = r48
            r2 = r9
            r6 = r12
            r0.animateWidgetDrop(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x035e
        L_0x0342:
            if (r16 >= 0) goto L_0x0346
            r3 = -1
            goto L_0x034a
        L_0x0346:
            r0 = 300(0x12c, float:4.2E-43)
            r3 = 300(0x12c, float:4.2E-43)
        L_0x034a:
            com.android.launcher3.Launcher r0 = r10.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            com.android.launcher3.dragndrop.DragView r1 = r8.dragView
            r2 = r12
            r5 = r48
            r0.animateViewIntoPosition(r1, r2, r3, r4, r5)
            goto L_0x035e
        L_0x0359:
            r8.deferDragViewCleanupPostAnimation = r11
            r12.setVisibility(r11)
        L_0x035e:
            r9.onDropChild(r12)
            r11 = r42
        L_0x0363:
            com.android.launcher3.accessibility.DragViewStateAnnouncer r0 = r8.stateAnnouncer
            if (r0 == 0) goto L_0x0370
            if (r11 != 0) goto L_0x0370
            com.android.launcher3.accessibility.DragViewStateAnnouncer r0 = r8.stateAnnouncer
            int r1 = com.android.launcher3.C0622R.string.item_moved
            r0.completeAction(r1)
        L_0x0370:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.onDrop(com.android.launcher3.DropTarget$DragObject):void");
    }

    public void onNoCellFound(View view) {
        if (this.mLauncher.isHotseatLayout(view)) {
            this.mLauncher.getHotseat();
            showOutOfSpaceMessage(true);
            return;
        }
        showOutOfSpaceMessage(false);
    }

    private void showOutOfSpaceMessage(boolean z) {
        Toast.makeText(this.mLauncher, this.mLauncher.getString(z ? C0622R.string.hotseat_out_of_space : C0622R.string.out_of_space), 0).show();
    }

    public void getPageAreaRelativeToDragLayer(Rect rect) {
        CellLayout cellLayout = (CellLayout) getChildAt(getNextPage());
        if (cellLayout != null) {
            ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
            this.mTempXY[0] = getViewportOffsetX() + getPaddingLeft() + shortcutsAndWidgets.getLeft();
            this.mTempXY[1] = cellLayout.getTop() + shortcutsAndWidgets.getTop();
            float descendantCoordRelativeToSelf = this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, this.mTempXY);
            rect.set(this.mTempXY[0], this.mTempXY[1], (int) (((float) this.mTempXY[0]) + (((float) shortcutsAndWidgets.getMeasuredWidth()) * descendantCoordRelativeToSelf)), (int) (((float) this.mTempXY[1]) + (descendantCoordRelativeToSelf * ((float) shortcutsAndWidgets.getMeasuredHeight()))));
        }
    }

    public void onDragEnter(DragObject dragObject) {
        this.mCreateUserFolderOnDrop = false;
        this.mAddToExistingFolderOnDrop = false;
        this.mDropToLayout = null;
        this.mDragViewVisualCenter = dragObject.getVisualCenter(this.mDragViewVisualCenter);
        setDropLayoutForDragObject(dragObject, this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1]);
    }

    public void onDragExit(DragObject dragObject) {
        this.mDropToLayout = this.mDragTargetLayout;
        if (this.mDragMode == 1) {
            this.mCreateUserFolderOnDrop = true;
        } else if (this.mDragMode == 2) {
            this.mAddToExistingFolderOnDrop = true;
        }
        setCurrentDropLayout(null);
        setCurrentDragOverlappingLayout(null);
        this.mSpringLoadedDragController.cancel();
    }

    private void enforceDragParity(String str, int i, int i2) {
        enforceDragParity(this, str, i, i2);
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            enforceDragParity(getChildAt(i3), str, i, i2);
        }
    }

    private void enforceDragParity(View view, String str, int i, int i2) {
        int i3;
        Object tag = view.getTag(C0622R.C0625id.drag_event_parity);
        if (tag == null) {
            i3 = 0;
        } else {
            i3 = ((Integer) tag).intValue();
        }
        int i4 = i3 + i;
        view.setTag(C0622R.C0625id.drag_event_parity, Integer.valueOf(i4));
        if (i4 != i2) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(": Drag contract violated: ");
            sb.append(i4);
            Log.e(str2, sb.toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public void setCurrentDropLayout(CellLayout cellLayout) {
        if (this.mDragTargetLayout != null) {
            this.mDragTargetLayout.revertTempState();
            this.mDragTargetLayout.onDragExit();
        }
        this.mDragTargetLayout = cellLayout;
        if (this.mDragTargetLayout != null) {
            this.mDragTargetLayout.onDragEnter();
        }
        cleanupReorder(true);
        cleanupFolderCreation();
        setCurrentDropOverCell(-1, -1);
    }

    /* access modifiers changed from: 0000 */
    public void setCurrentDragOverlappingLayout(CellLayout cellLayout) {
        if (this.mDragOverlappingLayout != null) {
            this.mDragOverlappingLayout.setIsDragOverlapping(false);
        }
        this.mDragOverlappingLayout = cellLayout;
        if (this.mDragOverlappingLayout != null) {
            this.mDragOverlappingLayout.setIsDragOverlapping(true);
        }
        this.mLauncher.getDragLayer().invalidateScrim();
    }

    public CellLayout getCurrentDragOverlappingLayout() {
        return this.mDragOverlappingLayout;
    }

    /* access modifiers changed from: 0000 */
    public void setCurrentDropOverCell(int i, int i2) {
        if (i != this.mDragOverX || i2 != this.mDragOverY) {
            this.mDragOverX = i;
            this.mDragOverY = i2;
            setDragMode(0);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setDragMode(int i) {
        if (i != this.mDragMode) {
            if (i == 0) {
                cleanupAddToFolder();
                cleanupReorder(false);
                cleanupFolderCreation();
            } else if (i == 2) {
                cleanupReorder(true);
                cleanupFolderCreation();
            } else if (i == 1) {
                cleanupAddToFolder();
                cleanupReorder(true);
            } else if (i == 3) {
                cleanupAddToFolder();
                cleanupFolderCreation();
            }
            this.mDragMode = i;
        }
    }

    private void cleanupFolderCreation() {
        if (this.mFolderCreateBg != null) {
            this.mFolderCreateBg.animateToRest();
        }
        this.mFolderCreationAlarm.setOnAlarmListener(null);
        this.mFolderCreationAlarm.cancelAlarm();
    }

    private void cleanupAddToFolder() {
        if (this.mDragOverFolderIcon != null) {
            this.mDragOverFolderIcon.onDragExit();
            this.mDragOverFolderIcon = null;
        }
    }

    private void cleanupReorder(boolean z) {
        if (z) {
            this.mReorderAlarm.cancelAlarm();
        }
        this.mLastReorderX = -1;
        this.mLastReorderY = -1;
    }

    /* access modifiers changed from: 0000 */
    public void mapPointFromSelfToChild(View view, float[] fArr) {
        fArr[0] = fArr[0] - ((float) view.getLeft());
        fArr[1] = fArr[1] - ((float) view.getTop());
    }

    /* access modifiers changed from: 0000 */
    public boolean isPointInSelfOverHotseat(int i, int i2) {
        this.mTempXY[0] = i;
        this.mTempXY[1] = i2;
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, this.mTempXY, true);
        Hotseat hotseat = this.mLauncher.getHotseat();
        if (this.mTempXY[0] < hotseat.getLeft() || this.mTempXY[0] > hotseat.getRight() || this.mTempXY[1] < hotseat.getTop() || this.mTempXY[1] > hotseat.getBottom()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void mapPointFromSelfToHotseatLayout(Hotseat hotseat, float[] fArr) {
        this.mTempXY[0] = (int) fArr[0];
        this.mTempXY[1] = (int) fArr[1];
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, this.mTempXY, true);
        this.mLauncher.getDragLayer().mapCoordInSelfToDescendant(hotseat.getLayout(), this.mTempXY);
        fArr[0] = (float) this.mTempXY[0];
        fArr[1] = (float) this.mTempXY[1];
    }

    /* access modifiers changed from: 0000 */
    public void mapPointFromChildToSelf(View view, float[] fArr) {
        fArr[0] = fArr[0] + ((float) view.getLeft());
        fArr[1] = fArr[1] + ((float) view.getTop());
    }

    private boolean isDragWidget(DragObject dragObject) {
        return (dragObject.dragInfo instanceof LauncherAppWidgetInfo) || (dragObject.dragInfo instanceof PendingAddWidgetInfo);
    }

    public void onDragOver(DragObject dragObject) {
        int i;
        int i2;
        DragObject dragObject2 = dragObject;
        if (transitionStateShouldAllowDrop()) {
            ItemInfo itemInfo = dragObject2.dragInfo;
            if (itemInfo != null) {
                if (itemInfo.spanX < 0 || itemInfo.spanY < 0) {
                    throw new RuntimeException("Improper spans found");
                }
                this.mDragViewVisualCenter = dragObject2.getVisualCenter(this.mDragViewVisualCenter);
                View view = this.mDragInfo == null ? null : this.mDragInfo.cell;
                if (setDropLayoutForDragObject(dragObject2, this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1])) {
                    if (this.mLauncher.isHotseatLayout(this.mDragTargetLayout)) {
                        this.mSpringLoadedDragController.cancel();
                    } else {
                        this.mSpringLoadedDragController.setAlarm(this.mDragTargetLayout);
                    }
                }
                if (this.mDragTargetLayout != null) {
                    if (this.mLauncher.isHotseatLayout(this.mDragTargetLayout)) {
                        mapPointFromSelfToHotseatLayout(this.mLauncher.getHotseat(), this.mDragViewVisualCenter);
                    } else {
                        mapPointFromSelfToChild(this.mDragTargetLayout, this.mDragViewVisualCenter);
                    }
                    int i3 = itemInfo.spanX;
                    int i4 = itemInfo.spanY;
                    if (itemInfo.minSpanX > 0 && itemInfo.minSpanY > 0) {
                        i3 = itemInfo.minSpanX;
                        i4 = itemInfo.minSpanY;
                    }
                    int i5 = i3;
                    int i6 = i4;
                    this.mTargetCell = findNearestArea((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], i5, i6, this.mDragTargetLayout, this.mTargetCell);
                    int i7 = this.mTargetCell[0];
                    int i8 = this.mTargetCell[1];
                    setCurrentDropOverCell(this.mTargetCell[0], this.mTargetCell[1]);
                    manageFolderFeedback(this.mDragTargetLayout, this.mTargetCell, this.mDragTargetLayout.getDistanceFromCell(this.mDragViewVisualCenter[0], this.mDragViewVisualCenter[1], this.mTargetCell), dragObject2);
                    boolean isNearestDropLocationOccupied = this.mDragTargetLayout.isNearestDropLocationOccupied((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], itemInfo.spanX, itemInfo.spanY, view, this.mTargetCell);
                    if (!isNearestDropLocationOccupied) {
                        this.mDragTargetLayout.visualizeDropLocation(view, this.mOutlineProvider, this.mTargetCell[0], this.mTargetCell[1], itemInfo.spanX, itemInfo.spanY, false, dragObject);
                    } else if ((this.mDragMode == 0 || this.mDragMode == 3) && !this.mReorderAlarm.alarmPending() && !(this.mLastReorderX == i7 && this.mLastReorderY == i8)) {
                        this.mDragTargetLayout.performReorder((int) this.mDragViewVisualCenter[0], (int) this.mDragViewVisualCenter[1], i5, i6, itemInfo.spanX, itemInfo.spanY, view, this.mTargetCell, new int[2], 0);
                        i = 2;
                        i2 = 1;
                        ReorderAlarmListener reorderAlarmListener = new ReorderAlarmListener(this.mDragViewVisualCenter, i5, i6, itemInfo.spanX, itemInfo.spanY, dragObject, view);
                        this.mReorderAlarm.setOnAlarmListener(reorderAlarmListener);
                        this.mReorderAlarm.setAlarm(350);
                        if ((this.mDragMode == i2 || this.mDragMode == i || !isNearestDropLocationOccupied) && this.mDragTargetLayout != null) {
                            this.mDragTargetLayout.revertTempState();
                        }
                    }
                    i2 = 1;
                    i = 2;
                    this.mDragTargetLayout.revertTempState();
                }
            }
        }
    }

    private boolean setDropLayoutForDragObject(DragObject dragObject, float f, float f2) {
        CellLayout layout = (this.mLauncher.getHotseat() == null || isDragWidget(dragObject) || !isPointInSelfOverHotseat(dragObject.f49x, dragObject.f50y)) ? null : this.mLauncher.getHotseat().getLayout();
        int nextPage = getNextPage();
        int i = -1;
        if (layout == null && !isPageInTransition()) {
            this.mTempTouchCoordinates[0] = Math.min(f, (float) dragObject.f49x);
            this.mTempTouchCoordinates[1] = (float) dragObject.f50y;
            layout = verifyInsidePage((this.mIsRtl ? 1 : -1) + nextPage, this.mTempTouchCoordinates);
        }
        if (layout == null && !isPageInTransition()) {
            this.mTempTouchCoordinates[0] = Math.max(f, (float) dragObject.f49x);
            this.mTempTouchCoordinates[1] = (float) dragObject.f50y;
            if (!this.mIsRtl) {
                i = 1;
            }
            layout = verifyInsidePage(i + nextPage, this.mTempTouchCoordinates);
        }
        if (layout == null && nextPage >= numCustomPages() && nextPage < getPageCount()) {
            layout = (CellLayout) getChildAt(nextPage);
        }
        if (layout == this.mDragTargetLayout) {
            return false;
        }
        setCurrentDropLayout(layout);
        setCurrentDragOverlappingLayout(layout);
        return true;
    }

    private CellLayout verifyInsidePage(int i, float[] fArr) {
        if (i >= numCustomPages() && i < getPageCount()) {
            CellLayout cellLayout = (CellLayout) getChildAt(i);
            mapPointFromSelfToChild(cellLayout, fArr);
            if (fArr[0] >= 0.0f && fArr[0] <= ((float) cellLayout.getWidth()) && fArr[1] >= 0.0f && fArr[1] <= ((float) cellLayout.getHeight())) {
                return cellLayout;
            }
        }
        return null;
    }

    private void manageFolderFeedback(CellLayout cellLayout, int[] iArr, float f, DragObject dragObject) {
        if (f <= this.mMaxDistanceForFolderCreation) {
            View childAt = this.mDragTargetLayout.getChildAt(this.mTargetCell[0], this.mTargetCell[1]);
            ItemInfo itemInfo = dragObject.dragInfo;
            boolean willCreateUserFolder = willCreateUserFolder(itemInfo, childAt, false);
            if (this.mDragMode != 0 || !willCreateUserFolder || this.mFolderCreationAlarm.alarmPending()) {
                boolean willAddToExistingUserFolder = willAddToExistingUserFolder(itemInfo, childAt);
                if (!willAddToExistingUserFolder || this.mDragMode != 0) {
                    if (this.mDragMode == 2 && !willAddToExistingUserFolder) {
                        setDragMode(0);
                    }
                    if (this.mDragMode == 1 && !willCreateUserFolder) {
                        setDragMode(0);
                    }
                    return;
                }
                this.mDragOverFolderIcon = (FolderIcon) childAt;
                this.mDragOverFolderIcon.onDragEnter(itemInfo);
                if (cellLayout != null) {
                    cellLayout.clearDragOutlines();
                }
                setDragMode(2);
                if (dragObject.stateAnnouncer != null) {
                    dragObject.stateAnnouncer.announce(WorkspaceAccessibilityHelper.getDescriptionForDropOver(childAt, getContext()));
                }
                return;
            }
            FolderCreationAlarmListener folderCreationAlarmListener = new FolderCreationAlarmListener(cellLayout, iArr[0], iArr[1]);
            if (!dragObject.accessibleDrag) {
                this.mFolderCreationAlarm.setOnAlarmListener(folderCreationAlarmListener);
                this.mFolderCreationAlarm.setAlarm(0);
            } else {
                folderCreationAlarmListener.onAlarm(this.mFolderCreationAlarm);
            }
            if (dragObject.stateAnnouncer != null) {
                dragObject.stateAnnouncer.announce(WorkspaceAccessibilityHelper.getDescriptionForDropOver(childAt, getContext()));
            }
        }
    }

    public void getHitRectRelativeToDragLayer(Rect rect) {
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this, rect);
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0142  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0149  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x015a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onDropExternal(int[] r33, com.android.launcher3.CellLayout r34, com.android.launcher3.DropTarget.DragObject r35) {
        /*
            r32 = this;
            r10 = r32
            r9 = r34
            r8 = r35
            com.android.launcher3.Workspace$11 r15 = new com.android.launcher3.Workspace$11
            r15.<init>()
            com.android.launcher3.ItemInfo r0 = r8.dragInfo
            boolean r0 = r0 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            if (r0 == 0) goto L_0x001f
            com.android.launcher3.ItemInfo r0 = r8.dragInfo
            com.android.launcher3.widget.PendingAddShortcutInfo r0 = (com.android.launcher3.widget.PendingAddShortcutInfo) r0
            com.android.launcher3.compat.ShortcutConfigActivityInfo r0 = r0.activityInfo
            com.android.launcher3.ShortcutInfo r0 = r0.createShortcutInfo()
            if (r0 == 0) goto L_0x001f
            r8.dragInfo = r0
        L_0x001f:
            com.android.launcher3.ItemInfo r7 = r8.dragInfo
            int r0 = r7.spanX
            int r1 = r7.spanY
            com.android.launcher3.CellLayout$CellInfo r2 = r10.mDragInfo
            if (r2 == 0) goto L_0x0031
            com.android.launcher3.CellLayout$CellInfo r0 = r10.mDragInfo
            int r0 = r0.spanX
            com.android.launcher3.CellLayout$CellInfo r1 = r10.mDragInfo
            int r1 = r1.spanY
        L_0x0031:
            r3 = r0
            r4 = r1
            com.android.launcher3.Launcher r0 = r10.mLauncher
            boolean r0 = r0.isHotseatLayout(r9)
            if (r0 == 0) goto L_0x0040
            r0 = -101(0xffffffffffffff9b, double:NaN)
        L_0x003d:
            r24 = r0
            goto L_0x0043
        L_0x0040:
            r0 = -100
            goto L_0x003d
        L_0x0043:
            long r13 = r10.getIdForScreen(r9)
            com.android.launcher3.Launcher r0 = r10.mLauncher
            boolean r0 = r0.isHotseatLayout(r9)
            r12 = 0
            if (r0 != 0) goto L_0x0063
            int r0 = r10.mCurrentPage
            long r0 = r10.getScreenIdForPageIndex(r0)
            int r0 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x0063
            com.android.launcher3.Workspace$State r0 = r10.mState
            com.android.launcher3.Workspace$State r1 = com.android.launcher3.Workspace.State.SPRING_LOADED
            if (r0 == r1) goto L_0x0063
            r10.snapToScreenId(r13, r12)
        L_0x0063:
            boolean r0 = r7 instanceof com.android.launcher3.PendingAddItemInfo
            r26 = 0
            r6 = 1
            if (r0 == 0) goto L_0x017d
            r15 = r7
            com.android.launcher3.PendingAddItemInfo r15 = (com.android.launcher3.PendingAddItemInfo) r15
            int r0 = r15.itemType
            if (r0 != r6) goto L_0x00af
            r1 = r33[r26]
            r2 = r33[r6]
            int[] r11 = r10.mTargetCell
            r0 = r32
            r5 = r34
            r27 = r15
            r15 = 1
            r6 = r11
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r10.mTargetCell = r0
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r26]
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r15]
            int[] r2 = r10.mTargetCell
            float r6 = r9.getDistanceFromCell(r0, r1, r2)
            com.android.launcher3.ItemInfo r1 = r8.dragInfo
            int[] r3 = r10.mTargetCell
            r5 = 1
            r0 = r32
            r2 = r34
            r4 = r6
            boolean r0 = r0.willCreateUserFolder(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x00ad
            com.android.launcher3.ItemInfo r0 = r8.dragInfo
            int[] r1 = r10.mTargetCell
            boolean r0 = r10.willAddToExistingUserFolder(r0, r9, r1, r6)
            if (r0 == 0) goto L_0x00b2
        L_0x00ad:
            r0 = 0
            goto L_0x00b3
        L_0x00af:
            r27 = r15
            r15 = 1
        L_0x00b2:
            r0 = 1
        L_0x00b3:
            com.android.launcher3.ItemInfo r5 = r8.dragInfo
            if (r0 == 0) goto L_0x0115
            int r0 = r5.spanX
            int r1 = r5.spanY
            int r2 = r5.minSpanX
            if (r2 <= 0) goto L_0x00c7
            int r2 = r5.minSpanY
            if (r2 <= 0) goto L_0x00c7
            int r0 = r5.minSpanX
            int r1 = r5.minSpanY
        L_0x00c7:
            r2 = 2
            int[] r2 = new int[r2]
            float[] r3 = r10.mDragViewVisualCenter
            r3 = r3[r26]
            int r3 = (int) r3
            float[] r4 = r10.mDragViewVisualCenter
            r4 = r4[r15]
            int r4 = (int) r4
            int r6 = r7.spanX
            int r11 = r7.spanY
            r18 = 0
            r28 = r7
            int[] r7 = r10.mTargetCell
            r21 = 3
            r17 = r11
            r11 = r34
            r22 = r12
            r12 = r3
            r29 = r13
            r13 = r4
            r14 = r0
            r3 = r27
            r4 = 1
            r15 = r1
            r16 = r6
            r19 = r7
            r20 = r2
            int[] r0 = r11.performReorder(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
            r10.mTargetCell = r0
            r0 = r2[r26]
            int r1 = r5.spanX
            if (r0 != r1) goto L_0x010a
            r0 = r2[r4]
            int r1 = r5.spanY
            if (r0 == r1) goto L_0x0108
            goto L_0x010a
        L_0x0108:
            r0 = 0
            goto L_0x010b
        L_0x010a:
            r0 = 1
        L_0x010b:
            r1 = r2[r26]
            r5.spanX = r1
            r1 = r2[r4]
            r5.spanY = r1
            r11 = r0
            goto L_0x011f
        L_0x0115:
            r28 = r7
            r22 = r12
            r29 = r13
            r3 = r27
            r4 = 1
            r11 = 0
        L_0x011f:
            com.android.launcher3.Workspace$12 r12 = new com.android.launcher3.Workspace$12
            r0 = r12
            r1 = r32
            r2 = r3
            r13 = r3
            r14 = 1
            r3 = r24
            r15 = r5
            r5 = r29
            r31 = r28
            r7 = r15
            r0.<init>(r2, r3, r5, r7)
            int r0 = r13.itemType
            r1 = 4
            if (r0 == r1) goto L_0x013f
            int r0 = r13.itemType
            r1 = 5
            if (r0 != r1) goto L_0x013d
            goto L_0x013f
        L_0x013d:
            r0 = 0
            goto L_0x0140
        L_0x013f:
            r0 = 1
        L_0x0140:
            if (r0 == 0) goto L_0x0149
            r1 = r13
            com.android.launcher3.widget.PendingAddWidgetInfo r1 = (com.android.launcher3.widget.PendingAddWidgetInfo) r1
            android.appwidget.AppWidgetHostView r1 = r1.boundWidget
            r6 = r1
            goto L_0x014b
        L_0x0149:
            r6 = r22
        L_0x014b:
            if (r6 == 0) goto L_0x0158
            if (r11 == 0) goto L_0x0158
            com.android.launcher3.Launcher r1 = r10.mLauncher
            int r2 = r15.spanX
            int r3 = r15.spanY
            com.android.launcher3.AppWidgetResizeFrame.updateWidgetSizeRanges(r6, r1, r2, r3)
        L_0x0158:
            if (r0 == 0) goto L_0x016d
            r15 = r13
            com.android.launcher3.widget.PendingAddWidgetInfo r15 = (com.android.launcher3.widget.PendingAddWidgetInfo) r15
            com.android.launcher3.LauncherAppWidgetProviderInfo r0 = r15.info
            if (r0 == 0) goto L_0x016d
            com.android.launcher3.widget.WidgetAddFlowHandler r0 = r15.getHandler()
            boolean r0 = r0.needsConfigure()
            if (r0 == 0) goto L_0x016d
            r5 = 1
            goto L_0x016e
        L_0x016d:
            r5 = 0
        L_0x016e:
            com.android.launcher3.dragndrop.DragView r3 = r8.dragView
            r7 = 1
            r0 = r32
            r1 = r31
            r2 = r34
            r4 = r12
            r0.animateWidgetDrop(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x02b1
        L_0x017d:
            r0 = r7
            r29 = r13
            r14 = 1
            int r1 = r0.itemType
            r2 = 6
            if (r1 == r2) goto L_0x01b0
            switch(r1) {
                case 0: goto L_0x01b0;
                case 1: goto L_0x01b0;
                case 2: goto L_0x01a2;
                default: goto L_0x0189;
            }
        L_0x0189:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown item type: "
            r2.append(r3)
            int r0 = r0.itemType
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            throw r1
        L_0x01a2:
            int r1 = com.android.launcher3.C0622R.layout.folder_icon
            com.android.launcher3.Launcher r2 = r10.mLauncher
            r7 = r0
            com.android.launcher3.FolderInfo r7 = (com.android.launcher3.FolderInfo) r7
            com.android.launcher3.folder.FolderIcon r1 = com.android.launcher3.folder.FolderIcon.fromXml(r1, r2, r9, r7)
            r12 = r0
            r13 = r1
            goto L_0x01d2
        L_0x01b0:
            long r1 = r0.container
            r5 = -1
            int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x01c6
            boolean r1 = r0 instanceof com.android.launcher3.AppInfo
            if (r1 == 0) goto L_0x01c6
            r7 = r0
            com.android.launcher3.AppInfo r7 = (com.android.launcher3.AppInfo) r7
            com.android.launcher3.ShortcutInfo r7 = r7.makeShortcut()
            r8.dragInfo = r7
            goto L_0x01c7
        L_0x01c6:
            r7 = r0
        L_0x01c7:
            com.android.launcher3.Launcher r0 = r10.mLauncher
            r1 = r7
            com.android.launcher3.ShortcutInfo r1 = (com.android.launcher3.ShortcutInfo) r1
            android.view.View r0 = r0.createShortcut(r9, r1)
            r13 = r0
            r12 = r7
        L_0x01d2:
            if (r33 == 0) goto L_0x0226
            r1 = r33[r26]
            r2 = r33[r14]
            int[] r6 = r10.mTargetCell
            r0 = r32
            r5 = r34
            int[] r0 = r0.findNearestArea(r1, r2, r3, r4, r5, r6)
            r10.mTargetCell = r0
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r26]
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r14]
            int[] r2 = r10.mTargetCell
            float r16 = r9.getDistanceFromCell(r0, r1, r2)
            r8.postAnimationRunnable = r15
            int[] r5 = r10.mTargetCell
            r7 = 1
            com.android.launcher3.dragndrop.DragView r6 = r8.dragView
            java.lang.Runnable r4 = r8.postAnimationRunnable
            r0 = r32
            r1 = r13
            r2 = r24
            r17 = r4
            r4 = r34
            r18 = r6
            r6 = r16
            r8 = r18
            r9 = r17
            boolean r0 = r0.createUserFolderIfNecessary(r1, r2, r4, r5, r6, r7, r8, r9)
            if (r0 == 0) goto L_0x0213
            return
        L_0x0213:
            int[] r3 = r10.mTargetCell
            r6 = 1
            r0 = r32
            r1 = r13
            r2 = r34
            r4 = r16
            r5 = r35
            boolean r0 = r0.addToExistingFolderIfNecessary(r1, r2, r3, r4, r5, r6)
            if (r0 == 0) goto L_0x0226
            return
        L_0x0226:
            if (r33 == 0) goto L_0x0253
            float[] r0 = r10.mDragViewVisualCenter
            r0 = r0[r26]
            int r0 = (int) r0
            float[] r1 = r10.mDragViewVisualCenter
            r1 = r1[r14]
            int r1 = (int) r1
            r2 = 1
            r3 = 1
            r16 = 1
            r17 = 1
            r18 = 0
            int[] r4 = r10.mTargetCell
            r20 = 0
            r21 = 3
            r11 = r34
            r7 = r12
            r12 = r0
            r9 = r13
            r13 = r1
            r0 = 1
            r14 = r2
            r8 = r15
            r15 = r3
            r19 = r4
            int[] r1 = r11.performReorder(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21)
            r10.mTargetCell = r1
            goto L_0x025e
        L_0x0253:
            r7 = r12
            r9 = r13
            r8 = r15
            r0 = 1
            int[] r1 = r10.mTargetCell
            r11 = r34
            r11.findCellForSpan(r1, r0, r0)
        L_0x025e:
            com.android.launcher3.Launcher r1 = r10.mLauncher
            com.android.launcher3.model.ModelWriter r16 = r1.getModelWriter()
            int[] r1 = r10.mTargetCell
            r22 = r1[r26]
            int[] r1 = r10.mTargetCell
            r23 = r1[r0]
            r17 = r7
            r18 = r24
            r20 = r29
            r16.addOrMoveItemInDatabase(r17, r18, r20, r22, r23)
            int[] r1 = r10.mTargetCell
            r6 = r1[r26]
            int[] r1 = r10.mTargetCell
            r12 = r1[r0]
            int r13 = r7.spanX
            int r14 = r7.spanY
            r0 = r32
            r1 = r9
            r2 = r24
            r4 = r29
            r7 = r12
            r12 = r8
            r8 = r13
            r13 = r9
            r9 = r14
            r0.addInScreen(r1, r2, r4, r6, r7, r8, r9)
            r11.onDropChild(r13)
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r34.getShortcutsAndWidgets()
            r0.measureChild(r13)
            r0 = r35
            com.android.launcher3.dragndrop.DragView r1 = r0.dragView
            if (r1 == 0) goto L_0x02b1
            r10.setFinalTransitionTransform(r11)
            com.android.launcher3.Launcher r1 = r10.mLauncher
            com.android.launcher3.dragndrop.DragLayer r1 = r1.getDragLayer()
            com.android.launcher3.dragndrop.DragView r0 = r0.dragView
            r1.animateViewIntoPosition(r0, r13, r12, r10)
            r10.resetTransitionTransform(r11)
        L_0x02b1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.onDropExternal(int[], com.android.launcher3.CellLayout, com.android.launcher3.DropTarget$DragObject):void");
    }

    public Bitmap createWidgetBitmap(ItemInfo itemInfo, View view) {
        int[] estimateItemSize = this.mLauncher.getWorkspace().estimateItemSize(itemInfo, false, true);
        int visibility = view.getVisibility();
        view.setVisibility(0);
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(estimateItemSize[0], 1073741824);
        int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(estimateItemSize[1], 1073741824);
        Bitmap createBitmap = Bitmap.createBitmap(estimateItemSize[0], estimateItemSize[1], Config.ARGB_8888);
        this.mCanvas.setBitmap(createBitmap);
        view.measure(makeMeasureSpec, makeMeasureSpec2);
        view.layout(0, 0, estimateItemSize[0], estimateItemSize[1]);
        view.draw(this.mCanvas);
        this.mCanvas.setBitmap(null);
        view.setVisibility(visibility);
        return createBitmap;
    }

    private void getFinalPositionForDropAnimation(int[] iArr, float[] fArr, DragView dragView, CellLayout cellLayout, ItemInfo itemInfo, int[] iArr2, boolean z) {
        int[] iArr3 = iArr;
        CellLayout cellLayout2 = cellLayout;
        ItemInfo itemInfo2 = itemInfo;
        CellLayout cellLayout3 = cellLayout;
        Rect estimateItemPosition = estimateItemPosition(cellLayout3, iArr2[0], iArr2[1], itemInfo2.spanX, itemInfo2.spanY);
        if (itemInfo2.itemType == 4) {
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            Utilities.shrinkRect(estimateItemPosition, deviceProfile.appWidgetScale.x, deviceProfile.appWidgetScale.y);
        }
        iArr3[0] = estimateItemPosition.left;
        iArr3[1] = estimateItemPosition.top;
        setFinalTransitionTransform(cellLayout2);
        float descendantCoordRelativeToSelf = this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(cellLayout2, iArr, true);
        resetTransitionTransform(cellLayout2);
        if (z) {
            float width = (((float) estimateItemPosition.width()) * 1.0f) / ((float) dragView.getMeasuredWidth());
            float height = (((float) estimateItemPosition.height()) * 1.0f) / ((float) dragView.getMeasuredHeight());
            iArr3[0] = (int) (((double) iArr3[0]) - (((double) ((((float) dragView.getMeasuredWidth()) - (((float) estimateItemPosition.width()) * descendantCoordRelativeToSelf)) / 2.0f)) - Math.ceil((double) (((float) cellLayout.getUnusedHorizontalSpace()) / 2.0f))));
            iArr3[1] = (int) (((float) iArr3[1]) - ((((float) dragView.getMeasuredHeight()) - (((float) estimateItemPosition.height()) * descendantCoordRelativeToSelf)) / 2.0f));
            fArr[0] = width * descendantCoordRelativeToSelf;
            fArr[1] = height * descendantCoordRelativeToSelf;
            return;
        }
        float initialScale = dragView.getInitialScale() * descendantCoordRelativeToSelf;
        float f = initialScale - 1.0f;
        iArr3[0] = (int) (((float) iArr3[0]) + ((((float) dragView.getWidth()) * f) / 2.0f));
        iArr3[1] = (int) (((float) iArr3[1]) + ((f * ((float) dragView.getHeight())) / 2.0f));
        fArr[1] = initialScale;
        fArr[0] = initialScale;
        Rect dragRegion = dragView.getDragRegion();
        if (dragRegion != null) {
            iArr3[0] = (int) (((float) iArr3[0]) + (((float) dragRegion.left) * descendantCoordRelativeToSelf));
            iArr3[1] = (int) (((float) iArr3[1]) + (descendantCoordRelativeToSelf * ((float) dragRegion.top)));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00ad  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void animateWidgetDrop(com.android.launcher3.ItemInfo r21, com.android.launcher3.CellLayout r22, com.android.launcher3.dragndrop.DragView r23, java.lang.Runnable r24, int r25, android.view.View r26, boolean r27) {
        /*
            r20 = this;
            r15 = r20
            r8 = r21
            r9 = r23
            r10 = r25
            r11 = r26
            android.graphics.Rect r13 = new android.graphics.Rect
            r13.<init>()
            com.android.launcher3.Launcher r0 = r15.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            r0.getViewRectRelativeToSelf(r9, r13)
            r14 = 2
            int[] r7 = new int[r14]
            float[] r6 = new float[r14]
            boolean r0 = r8 instanceof com.android.launcher3.widget.PendingAddShortcutInfo
            r16 = 0
            r5 = 1
            if (r0 != 0) goto L_0x0027
            r17 = 1
            goto L_0x0029
        L_0x0027:
            r17 = 0
        L_0x0029:
            int[] r4 = r15.mTargetCell
            r0 = r20
            r1 = r7
            r2 = r6
            r3 = r23
            r18 = r4
            r4 = r22
            r5 = r21
            r19 = r6
            r6 = r18
            r18 = r7
            r7 = r17
            r0.getFinalPositionForDropAnimation(r1, r2, r3, r4, r5, r6, r7)
            com.android.launcher3.Launcher r0 = r15.mLauncher
            android.content.res.Resources r0 = r0.getResources()
            int r1 = com.android.launcher3.C0622R.integer.config_dropAnimMaxDuration
            int r0 = r0.getInteger(r1)
            int r7 = r0 + -200
            int r0 = r8.itemType
            r1 = 4
            if (r0 == r1) goto L_0x005d
            int r0 = r8.itemType
            r2 = 5
            if (r0 != r2) goto L_0x005b
            goto L_0x005d
        L_0x005b:
            r0 = 0
            goto L_0x005e
        L_0x005d:
            r0 = 1
        L_0x005e:
            if (r10 == r14) goto L_0x0062
            if (r27 == 0) goto L_0x0076
        L_0x0062:
            if (r11 == 0) goto L_0x0076
            android.graphics.Bitmap r0 = r15.createWidgetBitmap(r8, r11)
            r9.setCrossFadeBitmap(r0)
            float r0 = (float) r7
            r2 = 1061997773(0x3f4ccccd, float:0.8)
            float r0 = r0 * r2
            int r0 = (int) r0
            r9.crossFade(r0)
            goto L_0x0088
        L_0x0076:
            if (r0 == 0) goto L_0x0088
            if (r27 == 0) goto L_0x0088
            r0 = r19[r16]
            r2 = 1
            r3 = r19[r2]
            float r0 = java.lang.Math.min(r0, r3)
            r19[r2] = r0
            r19[r16] = r0
            goto L_0x0089
        L_0x0088:
            r2 = 1
        L_0x0089:
            com.android.launcher3.Launcher r0 = r15.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            if (r10 != r1) goto L_0x00ad
            com.android.launcher3.Launcher r0 = r15.mLauncher
            com.android.launcher3.dragndrop.DragLayer r0 = r0.getDragLayer()
            r3 = 0
            r4 = 1036831949(0x3dcccccd, float:0.1)
            r5 = 1036831949(0x3dcccccd, float:0.1)
            r6 = 0
            r1 = r23
            r2 = r18
            r17 = r7
            r7 = r24
            r8 = r17
            r0.animateViewIntoPosition(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x00e0
        L_0x00ad:
            r17 = r7
            if (r10 != r2) goto L_0x00b3
            r12 = 2
            goto L_0x00b4
        L_0x00b3:
            r12 = 0
        L_0x00b4:
            com.android.launcher3.Workspace$13 r14 = new com.android.launcher3.Workspace$13
            r1 = r24
            r14.<init>(r11, r1)
            int r3 = r13.left
            int r4 = r13.top
            r5 = r18[r16]
            r6 = r18[r2]
            r7 = 1065353216(0x3f800000, float:1.0)
            r8 = 1065353216(0x3f800000, float:1.0)
            r10 = 1065353216(0x3f800000, float:1.0)
            r11 = r19[r16]
            r13 = r19[r2]
            r1 = r23
            r2 = r3
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r8
            r8 = r10
            r9 = r11
            r10 = r13
            r11 = r14
            r13 = r17
            r14 = r20
            r0.animateViewIntoPosition(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
        L_0x00e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Workspace.animateWidgetDrop(com.android.launcher3.ItemInfo, com.android.launcher3.CellLayout, com.android.launcher3.dragndrop.DragView, java.lang.Runnable, int, android.view.View, boolean):void");
    }

    public void setFinalTransitionTransform(CellLayout cellLayout) {
        if (isSwitchingState()) {
            this.mCurrentScale = getScaleX();
            setScaleX(this.mStateTransitionAnimation.getFinalScale());
            setScaleY(this.mStateTransitionAnimation.getFinalScale());
        }
    }

    public void resetTransitionTransform(CellLayout cellLayout) {
        if (isSwitchingState()) {
            setScaleX(this.mCurrentScale);
            setScaleY(this.mCurrentScale);
        }
    }

    public WorkspaceStateTransitionAnimation getStateTransitionAnimation() {
        return this.mStateTransitionAnimation;
    }

    public CellInfo getDragInfo() {
        return this.mDragInfo;
    }

    public int getCurrentPageOffsetFromCustomContent() {
        return getNextPage() - numCustomPages();
    }

    /* access modifiers changed from: 0000 */
    public int[] findNearestArea(int i, int i2, int i3, int i4, CellLayout cellLayout, int[] iArr) {
        return cellLayout.findNearestArea(i, i2, i3, i4, iArr);
    }

    /* access modifiers changed from: 0000 */
    public void setup(DragController dragController) {
        this.mSpringLoadedDragController = new SpringLoadedDragController(this.mLauncher);
        this.mDragController = dragController;
        updateChildrenLayersEnabled(false);
    }

    public void onDropCompleted(View view, DragObject dragObject, boolean z, boolean z2) {
        if (this.mDeferDropAfterUninstall) {
            final CellInfo cellInfo = this.mDragInfo;
            final View view2 = view;
            final DragObject dragObject2 = dragObject;
            final boolean z3 = z;
            final boolean z4 = z2;
            C063714 r1 = new Runnable() {
                public void run() {
                    Workspace.this.mDragInfo = cellInfo;
                    Workspace.this.onDropCompleted(view2, dragObject2, z3, z4);
                    Workspace.this.mDeferredAction = null;
                }
            };
            this.mDeferredAction = r1;
            return;
        }
        boolean z5 = this.mDeferredAction != null;
        if (!z2 || (z5 && !this.mUninstallSuccessful)) {
            if (this.mDragInfo != null) {
                CellLayout cellLayout = this.mLauncher.getCellLayout(this.mDragInfo.container, this.mDragInfo.screenId);
                if (cellLayout != null) {
                    cellLayout.onDropChild(this.mDragInfo.cell);
                }
            }
        } else if (!(view == this || this.mDragInfo == null)) {
            removeWorkspaceItem(this.mDragInfo.cell);
        }
        if (!((!dragObject.cancelled && (!z5 || this.mUninstallSuccessful)) || this.mDragInfo == null || this.mDragInfo.cell == null)) {
            this.mDragInfo.cell.setVisibility(0);
        }
        this.mDragInfo = null;
        if (!z) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(z2, 500, this.mDelayedResizeRunnable);
            this.mDelayedResizeRunnable = null;
        }
    }

    public void removeWorkspaceItem(View view) {
        CellLayout parentCellLayoutForView = getParentCellLayoutForView(view);
        if (parentCellLayoutForView != null) {
            parentCellLayoutForView.removeView(view);
        }
        if (view instanceof DropTarget) {
            this.mDragController.removeDropTarget((DropTarget) view);
        }
    }

    public void removeFolderListeners() {
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if (view instanceof FolderIcon) {
                    ((FolderIcon) view).removeListeners();
                }
                return false;
            }
        });
    }

    public void deferCompleteDropAfterUninstallActivity() {
        this.mDeferDropAfterUninstall = true;
    }

    public void onDragObjectRemoved(boolean z) {
        this.mDeferDropAfterUninstall = false;
        this.mUninstallSuccessful = z;
        if (this.mDeferredAction != null) {
            this.mDeferredAction.run();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        this.mSavedStates = sparseArray;
    }

    public void restoreInstanceStateForChild(int i) {
        if (this.mSavedStates != null) {
            this.mRestoredPages.add(Integer.valueOf(i));
            CellLayout cellLayout = (CellLayout) getChildAt(i);
            if (cellLayout != null) {
                cellLayout.restoreInstanceState(this.mSavedStates);
            }
        }
    }

    public void restoreInstanceStateForRemainingPages() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (!this.mRestoredPages.contains(Integer.valueOf(i))) {
                restoreInstanceStateForChild(i);
            }
        }
        this.mRestoredPages.clear();
        this.mSavedStates = null;
    }

    public void scrollLeft() {
        if (!workspaceInModalState() && !this.mIsSwitchingState) {
            super.scrollLeft();
        }
        Folder open = Folder.getOpen(this.mLauncher);
        if (open != null) {
            open.completeDragExit();
        }
    }

    public void scrollRight() {
        if (!workspaceInModalState() && !this.mIsSwitchingState) {
            super.scrollRight();
        }
        Folder open = Folder.getOpen(this.mLauncher);
        if (open != null) {
            open.completeDragExit();
        }
    }

    /* access modifiers changed from: 0000 */
    public CellLayout getParentCellLayoutForView(View view) {
        Iterator it = getWorkspaceAndHotseatCellLayouts().iterator();
        while (it.hasNext()) {
            CellLayout cellLayout = (CellLayout) it.next();
            if (cellLayout.getShortcutsAndWidgets().indexOfChild(view) > -1) {
                return cellLayout;
            }
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public ArrayList<CellLayout> getWorkspaceAndHotseatCellLayouts() {
        ArrayList<CellLayout> arrayList = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            arrayList.add((CellLayout) getChildAt(i));
        }
        if (this.mLauncher.getHotseat() != null) {
            arrayList.add(this.mLauncher.getHotseat().getLayout());
        }
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public ArrayList<ShortcutAndWidgetContainer> getAllShortcutAndWidgetContainers() {
        ArrayList<ShortcutAndWidgetContainer> arrayList = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            arrayList.add(((CellLayout) getChildAt(i)).getShortcutsAndWidgets());
        }
        if (this.mLauncher.getHotseat() != null) {
            arrayList.add(this.mLauncher.getHotseat().getLayout().getShortcutsAndWidgets());
        }
        return arrayList;
    }

    public View getHomescreenIconByItemId(final long j) {
        return getFirstMatch(new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                return itemInfo != null && itemInfo.f52id == j;
            }
        });
    }

    public View getViewForTag(final Object obj) {
        return getFirstMatch(new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                return itemInfo == obj;
            }
        });
    }

    public LauncherAppWidgetHostView getWidgetForAppWidgetId(final int i) {
        return (LauncherAppWidgetHostView) getFirstMatch(new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                return (itemInfo instanceof LauncherAppWidgetInfo) && ((LauncherAppWidgetInfo) itemInfo).appWidgetId == i;
            }
        });
    }

    public View getFirstMatch(final ItemOperator itemOperator) {
        final View[] viewArr = new View[1];
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if (!itemOperator.evaluate(itemInfo, view)) {
                    return false;
                }
                viewArr[0] = view;
                return true;
            }
        });
        return viewArr[0];
    }

    /* access modifiers changed from: 0000 */
    public void clearDropTargets() {
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if (view instanceof DropTarget) {
                    Workspace.this.mDragController.removeDropTarget((DropTarget) view);
                }
                return false;
            }
        });
    }

    public void removeItemsByMatcher(ItemInfoMatcher itemInfoMatcher) {
        Iterator it = getWorkspaceAndHotseatCellLayouts().iterator();
        while (it.hasNext()) {
            CellLayout cellLayout = (CellLayout) it.next();
            ShortcutAndWidgetContainer shortcutsAndWidgets = cellLayout.getShortcutsAndWidgets();
            LongArrayMap longArrayMap = new LongArrayMap();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
                View childAt = shortcutsAndWidgets.getChildAt(i);
                if (childAt.getTag() instanceof ItemInfo) {
                    ItemInfo itemInfo = (ItemInfo) childAt.getTag();
                    arrayList.add(itemInfo);
                    longArrayMap.put(itemInfo.f52id, childAt);
                }
            }
            Iterator it2 = itemInfoMatcher.filterItemInfos(arrayList).iterator();
            while (it2.hasNext()) {
                ItemInfo itemInfo2 = (ItemInfo) it2.next();
                View view = (View) longArrayMap.get(itemInfo2.f52id);
                if (view != null) {
                    cellLayout.removeViewInLayout(view);
                    if (view instanceof DropTarget) {
                        this.mDragController.removeDropTarget((DropTarget) view);
                    }
                } else if (itemInfo2.container >= 0) {
                    View view2 = (View) longArrayMap.get(itemInfo2.container);
                    if (view2 != null) {
                        FolderInfo folderInfo = (FolderInfo) view2.getTag();
                        folderInfo.prepareAutoUpdate();
                        folderInfo.remove((ShortcutInfo) itemInfo2, false);
                    }
                }
            }
        }
        stripEmptyScreens();
    }

    /* access modifiers changed from: 0000 */
    public void mapOverItems(boolean z, ItemOperator itemOperator) {
        ArrayList allShortcutAndWidgetContainers = getAllShortcutAndWidgetContainers();
        int size = allShortcutAndWidgetContainers.size();
        for (int i = 0; i < size; i++) {
            ShortcutAndWidgetContainer shortcutAndWidgetContainer = (ShortcutAndWidgetContainer) allShortcutAndWidgetContainers.get(i);
            int childCount = shortcutAndWidgetContainer.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = shortcutAndWidgetContainer.getChildAt(i2);
                ItemInfo itemInfo = (ItemInfo) childAt.getTag();
                if (z && (itemInfo instanceof FolderInfo) && (childAt instanceof FolderIcon)) {
                    ArrayList itemsInReadingOrder = ((FolderIcon) childAt).getFolder().getItemsInReadingOrder();
                    int size2 = itemsInReadingOrder.size();
                    int i3 = 0;
                    while (i3 < size2) {
                        View view = (View) itemsInReadingOrder.get(i3);
                        if (!itemOperator.evaluate((ItemInfo) view.getTag(), view)) {
                            i3++;
                        } else {
                            return;
                        }
                    }
                    continue;
                } else if (itemOperator.evaluate(itemInfo, childAt)) {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateShortcuts(ArrayList<ShortcutInfo> arrayList) {
        int size = arrayList.size();
        final HashSet hashSet = new HashSet(size);
        final HashSet hashSet2 = new HashSet();
        for (int i = 0; i < size; i++) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) arrayList.get(i);
            hashSet.add(shortcutInfo);
            hashSet2.add(Long.valueOf(shortcutInfo.container));
        }
        mapOverItems(true, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if ((itemInfo instanceof ShortcutInfo) && (view instanceof BubbleTextView) && hashSet.contains(itemInfo)) {
                    ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
                    BubbleTextView bubbleTextView = (BubbleTextView) view;
                    Drawable icon = bubbleTextView.getIcon();
                    boolean z = true;
                    if (shortcutInfo.isPromise() == ((icon instanceof PreloadIconDrawable) && ((PreloadIconDrawable) icon).hasNotCompleted())) {
                        z = false;
                    }
                    bubbleTextView.applyFromShortcutInfo(shortcutInfo, z);
                }
                return false;
            }
        });
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if ((itemInfo instanceof FolderInfo) && hashSet2.contains(Long.valueOf(itemInfo.f52id))) {
                    ((FolderInfo) itemInfo).itemsChanged(false);
                }
                return false;
            }
        });
    }

    public void updateIconBadges(final Set<PackageUserKey> set) {
        final PackageUserKey packageUserKey = new PackageUserKey(null, null);
        final HashSet hashSet = new HashSet();
        mapOverItems(true, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if ((itemInfo instanceof ShortcutInfo) && (view instanceof BubbleTextView) && packageUserKey.updateFromItemInfo(itemInfo) && set.contains(packageUserKey)) {
                    ((BubbleTextView) view).applyBadgeState(itemInfo, true);
                    hashSet.add(Long.valueOf(itemInfo.container));
                }
                return false;
            }
        });
        mapOverItems(false, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if ((itemInfo instanceof FolderInfo) && hashSet.contains(Long.valueOf(itemInfo.f52id)) && (view instanceof FolderIcon)) {
                    FolderBadgeInfo folderBadgeInfo = new FolderBadgeInfo();
                    Iterator it = ((FolderInfo) itemInfo).contents.iterator();
                    while (it.hasNext()) {
                        folderBadgeInfo.addBadgeInfo(Workspace.this.mLauncher.getPopupDataProvider().getBadgeInfoForItem((ShortcutInfo) it.next()));
                    }
                    ((FolderIcon) view).setBadgeInfo(folderBadgeInfo);
                }
                return false;
            }
        });
    }

    public void removeAbandonedPromise(String str, UserHandle userHandle) {
        HashSet hashSet = new HashSet(1);
        hashSet.add(str);
        ItemInfoMatcher ofPackages = ItemInfoMatcher.ofPackages(hashSet, userHandle);
        this.mLauncher.getModelWriter().deleteItemsFromDatabase(ofPackages);
        removeItemsByMatcher(ofPackages);
    }

    public void updateRestoreItems(final HashSet<ItemInfo> hashSet) {
        mapOverItems(true, new ItemOperator() {
            public boolean evaluate(ItemInfo itemInfo, View view) {
                if ((itemInfo instanceof ShortcutInfo) && (view instanceof BubbleTextView) && hashSet.contains(itemInfo)) {
                    ((BubbleTextView) view).applyPromiseState(false);
                } else if ((view instanceof PendingAppWidgetHostView) && (itemInfo instanceof LauncherAppWidgetInfo) && hashSet.contains(itemInfo)) {
                    ((PendingAppWidgetHostView) view).applyState();
                }
                return false;
            }
        });
    }

    public void widgetsRestored(final ArrayList<LauncherAppWidgetInfo> arrayList) {
        Object obj;
        if (!arrayList.isEmpty()) {
            DeferredWidgetRefresh deferredWidgetRefresh = new DeferredWidgetRefresh(arrayList, this.mLauncher.getAppWidgetHost());
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) arrayList.get(0);
            if (launcherAppWidgetInfo.hasRestoreFlag(1)) {
                obj = AppWidgetManagerCompat.getInstance(this.mLauncher).findProvider(launcherAppWidgetInfo.providerName, launcherAppWidgetInfo.user);
            } else {
                obj = AppWidgetManagerCompat.getInstance(this.mLauncher).getAppWidgetInfo(launcherAppWidgetInfo.appWidgetId);
            }
            if (obj != null) {
                deferredWidgetRefresh.run();
            } else {
                mapOverItems(false, new ItemOperator() {
                    public boolean evaluate(ItemInfo itemInfo, View view) {
                        if ((view instanceof PendingAppWidgetHostView) && arrayList.contains(itemInfo)) {
                            ((LauncherAppWidgetInfo) itemInfo).installProgress = 100;
                            ((PendingAppWidgetHostView) view).applyState();
                        }
                        return false;
                    }
                });
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void moveToDefaultScreen(boolean z) {
        int defaultPage = getDefaultPage();
        if (!workspaceInModalState() && getNextPage() != defaultPage) {
            if (z) {
                snapToPage(defaultPage);
            } else {
                setCurrentPage(defaultPage);
            }
        }
        View childAt = getChildAt(defaultPage);
        if (childAt != null) {
            childAt.requestFocus();
        }
    }

    /* access modifiers changed from: 0000 */
    public void moveToCustomContentScreen(boolean z) {
        if (hasCustomContent()) {
            int pageIndexForScreenId = getPageIndexForScreenId(CUSTOM_CONTENT_SCREEN_ID);
            if (z) {
                snapToPage(pageIndexForScreenId);
            } else {
                setCurrentPage(pageIndexForScreenId);
            }
            View childAt = getChildAt(pageIndexForScreenId);
            if (childAt != null) {
                childAt.requestFocus();
            }
        }
        exitWidgetResizeMode();
    }

    /* access modifiers changed from: protected */
    public String getPageIndicatorDescription() {
        return getResources().getString(C0622R.string.all_apps_button_label);
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        if (hasCustomContent() && getNextPage() == 0) {
            return this.mCustomContentDescription;
        }
        return getPageDescription(this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage);
    }

    private String getPageDescription(int i) {
        int numCustomPages = numCustomPages();
        int childCount = getChildCount() - numCustomPages;
        int indexOf = this.mScreenOrder.indexOf(Long.valueOf(-201));
        if (indexOf >= 0 && childCount > 1) {
            if (i == indexOf) {
                return getContext().getString(C0622R.string.workspace_new_page);
            }
            childCount--;
        }
        if (childCount == 0) {
            return getContext().getString(C0622R.string.all_apps_home_button_label);
        }
        return getContext().getString(C0622R.string.workspace_scroll_format, new Object[]{Integer.valueOf((i + 1) - numCustomPages), Integer.valueOf(childCount)});
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        target.gridX = itemInfo.cellX;
        target.gridY = itemInfo.cellY;
        target.pageIndex = getCurrentPage();
        target2.containerType = 1;
        if (itemInfo.container == -101) {
            target.rank = itemInfo.rank;
            target2.containerType = 2;
        } else if (itemInfo.container >= 0) {
            target2.containerType = 3;
        }
    }

    public boolean enableFreeScroll() {
        if (getState() == State.OVERVIEW) {
            return super.enableFreeScroll();
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("enableFreeScroll called but not in overview: state=");
        sb.append(getState());
        Log.w(str, sb.toString());
        return false;
    }
}
