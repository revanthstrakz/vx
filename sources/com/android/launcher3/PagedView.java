package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.p001v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.animation.Interpolator;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.touch.OverScroll;
import java.util.ArrayList;

public abstract class PagedView extends ViewGroup implements OnHierarchyChangeListener {
    private static final boolean DEBUG = false;
    private static final int FLING_THRESHOLD_VELOCITY = 500;
    protected static final int INVALID_PAGE = -1;
    protected static final int INVALID_POINTER = -1;
    public static final int INVALID_RESTORE_PAGE = -1001;
    private static final float MAX_SCROLL_PROGRESS = 1.0f;
    private static final int MIN_FLING_VELOCITY = 250;
    private static final int MIN_LENGTH_FOR_FLING = 25;
    private static final int MIN_SNAP_VELOCITY = 1500;
    private static final int NUM_ANIMATIONS_RUNNING_BEFORE_ZOOM_OUT = 2;
    private static final int OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION = 270;
    public static final int PAGE_SNAP_ANIMATION_DURATION = 750;
    private static int REORDERING_DROP_REPOSITION_DURATION = 200;
    static int REORDERING_REORDER_REPOSITION_DURATION = 300;
    private static int REORDERING_SIDE_PAGE_HOVER_TIMEOUT = 80;
    private static final float RETURN_TO_ORIGINAL_PAGE_THRESHOLD = 0.33f;
    private static final float SIGNIFICANT_MOVE_THRESHOLD = 0.4f;
    protected static final int SLOW_PAGE_SNAP_ANIMATION_DURATION = 950;
    private static final String TAG = "PagedView";
    protected static final int TOUCH_STATE_NEXT_PAGE = 3;
    protected static final int TOUCH_STATE_PREV_PAGE = 2;
    protected static final int TOUCH_STATE_REORDERING = 4;
    protected static final int TOUCH_STATE_REST = 0;
    protected static final int TOUCH_STATE_SCROLLING = 1;
    private static final Matrix sTmpInvMatrix = new Matrix();
    private static final float[] sTmpPoint = new float[2];
    private static final Rect sTmpRect = new Rect();
    protected int mActivePointerId;
    protected boolean mAllowOverScroll;
    private boolean mCancelTap;
    private int mChildCountOnLastLayout;
    @ExportedProperty(category = "launcher")
    protected int mCurrentPage;
    private Interpolator mDefaultInterpolator;
    private float mDownMotionX;
    private float mDownMotionY;
    private float mDownScrollX;
    View mDragView;
    private float mDragViewBaselineLeft;
    protected boolean mFirstLayout;
    protected int mFlingThresholdVelocity;
    private boolean mFreeScroll;
    private int mFreeScrollMaxScrollX;
    private int mFreeScrollMinScrollX;
    protected final Rect mInsets;
    protected boolean mIsPageInTransition;
    private boolean mIsReordering;
    protected final boolean mIsRtl;
    private float mLastMotionX;
    private float mLastMotionXRemainder;
    private float mLastMotionY;
    protected OnLongClickListener mLongClickListener;
    protected int mMaxScrollX;
    private int mMaximumVelocity;
    protected int mMinFlingVelocity;
    private float mMinScale;
    protected int mMinSnapVelocity;
    @ExportedProperty(category = "launcher")
    protected int mNextPage;
    private int mNormalChildHeight;
    protected int mOverScrollX;
    protected PageIndicator mPageIndicator;
    int mPageIndicatorViewId;
    private int[] mPageScrolls;
    int mPageSpacing;
    private float mParentDownMotionX;
    private float mParentDownMotionY;
    private int mPostReorderingPreZoomInRemainingAnimationCount;
    private Runnable mPostReorderingPreZoomInRunnable;
    private boolean mReorderingStarted;
    protected LauncherScroller mScroller;
    int mSidePageHoverIndex;
    private Runnable mSidePageHoverRunnable;
    protected int[] mTempVisiblePagesRange;
    private float mTotalMotionX;
    protected int mTouchSlop;
    protected int mTouchState;
    protected int mUnboundedScrollX;
    private boolean mUseMinScale;
    private VelocityTracker mVelocityTracker;
    @ExportedProperty(category = "launcher")
    private Rect mViewport;
    protected boolean mWasInOverscroll;

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        public boolean isFullScreenPage = false;
        public boolean matchStartEdge = false;

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int currentPage = -1;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.currentPage = parcel.readInt();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.currentPage);
        }
    }

    public static class ScrollInterpolator implements Interpolator {
        public float getInterpolation(float f) {
            float f2 = f - 1.0f;
            return (f2 * f2 * f2 * f2 * f2) + 1.0f;
        }
    }

    /* access modifiers changed from: protected */
    public int getChildGap() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int indexToPage(int i) {
        return i;
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onPageBeginTransition() {
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionBegin() {
    }

    /* access modifiers changed from: protected */
    public void onScrollInteractionEnd() {
    }

    public PagedView(Context context) {
        this(context, null);
    }

    public PagedView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PagedView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFreeScroll = false;
        this.mFreeScrollMinScrollX = -1;
        this.mFreeScrollMaxScrollX = -1;
        this.mFirstLayout = true;
        this.mNextPage = -1;
        this.mPageSpacing = 0;
        this.mTouchState = 0;
        this.mAllowOverScroll = true;
        this.mTempVisiblePagesRange = new int[2];
        this.mActivePointerId = -1;
        this.mIsPageInTransition = false;
        this.mWasInOverscroll = false;
        this.mViewport = new Rect();
        this.mMinScale = 1.0f;
        this.mUseMinScale = false;
        this.mSidePageHoverIndex = -1;
        this.mReorderingStarted = false;
        this.mInsets = new Rect();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0622R.styleable.PagedView, i, 0);
        this.mPageIndicatorViewId = obtainStyledAttributes.getResourceId(C0622R.styleable.PagedView_pageIndicator, -1);
        obtainStyledAttributes.recycle();
        setHapticFeedbackEnabled(false);
        this.mIsRtl = Utilities.isRtl(getResources());
        init();
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.mScroller = new LauncherScroller(getContext());
        setDefaultInterpolator(new ScrollInterpolator());
        this.mCurrentPage = 0;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        float f = getResources().getDisplayMetrics().density;
        this.mFlingThresholdVelocity = (int) (500.0f * f);
        this.mMinFlingVelocity = (int) (250.0f * f);
        this.mMinSnapVelocity = (int) (f * 1500.0f);
        setOnHierarchyChangeListener(this);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void setDefaultInterpolator(Interpolator interpolator) {
        this.mDefaultInterpolator = interpolator;
        this.mScroller.setInterpolator(this.mDefaultInterpolator);
    }

    public void initParentViews(View view) {
        if (this.mPageIndicatorViewId > -1) {
            this.mPageIndicator = (PageIndicator) view.findViewById(this.mPageIndicatorViewId);
            this.mPageIndicator.setMarkersCount(getChildCount());
            this.mPageIndicator.setContentDescription(getPageIndicatorDescription());
        }
    }

    private float[] mapPointFromViewToParent(View view, float f, float f2) {
        sTmpPoint[0] = f;
        sTmpPoint[1] = f2;
        view.getMatrix().mapPoints(sTmpPoint);
        float[] fArr = sTmpPoint;
        fArr[0] = fArr[0] + ((float) view.getLeft());
        float[] fArr2 = sTmpPoint;
        fArr2[1] = fArr2[1] + ((float) view.getTop());
        return sTmpPoint;
    }

    private float[] mapPointFromParentToView(View view, float f, float f2) {
        sTmpPoint[0] = f - ((float) view.getLeft());
        sTmpPoint[1] = f2 - ((float) view.getTop());
        view.getMatrix().invert(sTmpInvMatrix);
        sTmpInvMatrix.mapPoints(sTmpPoint);
        return sTmpPoint;
    }

    private void updateDragViewTranslationDuringDrag() {
        if (this.mDragView != null) {
            float f = this.mLastMotionY - this.mDownMotionY;
            this.mDragView.setTranslationX((this.mLastMotionX - this.mDownMotionX) + (((float) getScrollX()) - this.mDownScrollX) + (this.mDragViewBaselineLeft - ((float) this.mDragView.getLeft())));
            this.mDragView.setTranslationY(f);
        }
    }

    public void setMinScale(float f) {
        this.mMinScale = f;
        this.mUseMinScale = true;
        requestLayout();
    }

    public void setScaleX(float f) {
        super.setScaleX(f);
        if (isReordering(true)) {
            float[] mapPointFromParentToView = mapPointFromParentToView(this, this.mParentDownMotionX, this.mParentDownMotionY);
            this.mLastMotionX = mapPointFromParentToView[0];
            this.mLastMotionY = mapPointFromParentToView[1];
            updateDragViewTranslationDuringDrag();
        }
    }

    /* access modifiers changed from: 0000 */
    public int getViewportWidth() {
        return this.mViewport.width();
    }

    public int getViewportHeight() {
        return this.mViewport.height();
    }

    /* access modifiers changed from: 0000 */
    public int getViewportOffsetX() {
        return (getMeasuredWidth() - getViewportWidth()) / 2;
    }

    /* access modifiers changed from: 0000 */
    public int getViewportOffsetY() {
        return (getMeasuredHeight() - getViewportHeight()) / 2;
    }

    public PageIndicator getPageIndicator() {
        return this.mPageIndicator;
    }

    public int getCurrentPage() {
        return this.mCurrentPage;
    }

    public int getNextPage() {
        return this.mNextPage != -1 ? this.mNextPage : this.mCurrentPage;
    }

    public int getPageCount() {
        return getChildCount();
    }

    public View getPageAt(int i) {
        return getChildAt(i);
    }

    /* access modifiers changed from: protected */
    public void updateCurrentPageScroll() {
        int scrollForPage = (this.mCurrentPage < 0 || this.mCurrentPage >= getPageCount()) ? 0 : getScrollForPage(this.mCurrentPage);
        scrollTo(scrollForPage, 0);
        this.mScroller.setFinalX(scrollForPage);
        forceFinishScroller(true);
    }

    private void abortScrollerAnimation(boolean z) {
        this.mScroller.abortAnimation();
        if (z) {
            this.mNextPage = -1;
        }
    }

    private void forceFinishScroller(boolean z) {
        this.mScroller.forceFinished(true);
        if (z) {
            this.mNextPage = -1;
        }
    }

    private int validateNewPage(int i) {
        if (this.mFreeScroll) {
            getFreeScrollPageRange(this.mTempVisiblePagesRange);
            i = Math.max(this.mTempVisiblePagesRange[0], Math.min(i, this.mTempVisiblePagesRange[1]));
        }
        return Utilities.boundToRange(i, 0, getPageCount() - 1);
    }

    public void setCurrentPage(int i) {
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(true);
        }
        if (getChildCount() != 0) {
            int i2 = this.mCurrentPage;
            this.mCurrentPage = validateNewPage(i);
            updateCurrentPageScroll();
            notifyPageSwitchListener(i2);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void notifyPageSwitchListener(int i) {
        updatePageIndicator();
    }

    private void updatePageIndicator() {
        if (this.mPageIndicator != null) {
            this.mPageIndicator.setContentDescription(getPageIndicatorDescription());
            if (!isReordering(false)) {
                this.mPageIndicator.setActiveMarker(getNextPage());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void pageBeginTransition() {
        if (!this.mIsPageInTransition) {
            this.mIsPageInTransition = true;
            onPageBeginTransition();
        }
    }

    /* access modifiers changed from: protected */
    public void pageEndTransition() {
        if (this.mIsPageInTransition) {
            this.mIsPageInTransition = false;
            onPageEndTransition();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isPageInTransition() {
        return this.mIsPageInTransition;
    }

    /* access modifiers changed from: protected */
    public void onPageEndTransition() {
        this.mWasInOverscroll = false;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.mLongClickListener = onLongClickListener;
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            getPageAt(i).setOnLongClickListener(onLongClickListener);
        }
        super.setOnLongClickListener(onLongClickListener);
    }

    /* access modifiers changed from: protected */
    public int getUnboundedScrollX() {
        return this.mUnboundedScrollX;
    }

    public void scrollBy(int i, int i2) {
        scrollTo(getUnboundedScrollX() + i, getScrollY() + i2);
    }

    public void scrollTo(int i, int i2) {
        if (this.mFreeScroll) {
            if (!this.mScroller.isFinished() && (i > this.mFreeScrollMaxScrollX || i < this.mFreeScrollMinScrollX)) {
                forceFinishScroller(false);
            }
            i = Math.max(Math.min(i, this.mFreeScrollMaxScrollX), this.mFreeScrollMinScrollX);
        }
        this.mUnboundedScrollX = i;
        boolean z = !this.mIsRtl ? i < 0 : i > this.mMaxScrollX;
        boolean z2 = !this.mIsRtl ? i > this.mMaxScrollX : i < 0;
        if (z) {
            super.scrollTo(this.mIsRtl ? this.mMaxScrollX : 0, i2);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                if (this.mIsRtl) {
                    overScroll((float) (i - this.mMaxScrollX));
                } else {
                    overScroll((float) i);
                }
            }
        } else if (z2) {
            super.scrollTo(this.mIsRtl ? 0 : this.mMaxScrollX, i2);
            if (this.mAllowOverScroll) {
                this.mWasInOverscroll = true;
                if (this.mIsRtl) {
                    overScroll((float) i);
                } else {
                    overScroll((float) (i - this.mMaxScrollX));
                }
            }
        } else {
            if (this.mWasInOverscroll) {
                overScroll(0.0f);
                this.mWasInOverscroll = false;
            }
            this.mOverScrollX = i;
            super.scrollTo(i, i2);
        }
        if (isReordering(true)) {
            float[] mapPointFromParentToView = mapPointFromParentToView(this, this.mParentDownMotionX, this.mParentDownMotionY);
            this.mLastMotionX = mapPointFromParentToView[0];
            this.mLastMotionY = mapPointFromParentToView[1];
            updateDragViewTranslationDuringDrag();
        }
    }

    private void sendScrollAccessibilityEvent() {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled() && this.mCurrentPage != getNextPage()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(4096);
            obtain.setScrollable(true);
            obtain.setScrollX(getScrollX());
            obtain.setScrollY(getScrollY());
            obtain.setMaxScrollX(this.mMaxScrollX);
            obtain.setMaxScrollY(0);
            sendAccessibilityEventUnchecked(obtain);
        }
    }

    /* access modifiers changed from: protected */
    public boolean computeScrollHelper() {
        return computeScrollHelper(true);
    }

    /* access modifiers changed from: protected */
    public boolean computeScrollHelper(boolean z) {
        if (this.mScroller.computeScrollOffset()) {
            if (!(getUnboundedScrollX() == this.mScroller.getCurrX() && getScrollY() == this.mScroller.getCurrY() && this.mOverScrollX == this.mScroller.getCurrX())) {
                scrollTo((int) (((float) this.mScroller.getCurrX()) * (1.0f / (this.mFreeScroll ? getScaleX() : 1.0f))), this.mScroller.getCurrY());
            }
            if (z) {
                invalidate();
            }
            return true;
        } else if (this.mNextPage == -1 || !z) {
            return false;
        } else {
            sendScrollAccessibilityEvent();
            int i = this.mCurrentPage;
            this.mCurrentPage = validateNewPage(this.mNextPage);
            this.mNextPage = -1;
            notifyPageSwitchListener(i);
            if (this.mTouchState == 0) {
                pageEndTransition();
            }
            onPostReorderingAnimationCompleted();
            if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
                announceForAccessibility(getCurrentPageDescription());
            }
            return true;
        }
    }

    public void computeScroll() {
        computeScrollHelper();
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void addFullScreenPage(View view) {
        LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
        generateDefaultLayoutParams.isFullScreenPage = true;
        super.addView(view, 0, generateDefaultLayoutParams);
    }

    public int getNormalChildHeight() {
        return this.mNormalChildHeight;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        if (getChildCount() == 0) {
            super.onMeasure(i, i2);
            return;
        }
        int mode = MeasureSpec.getMode(i);
        int size = MeasureSpec.getSize(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size2 = MeasureSpec.getSize(i2);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int max = (int) (((float) Math.max(displayMetrics.widthPixels + this.mInsets.left + this.mInsets.right, displayMetrics.heightPixels + this.mInsets.top + this.mInsets.bottom)) * 2.0f);
        if (this.mUseMinScale) {
            float f = (float) max;
            i3 = (int) (f / this.mMinScale);
            i4 = (int) (f / this.mMinScale);
        } else {
            i3 = size;
            i4 = size2;
        }
        this.mViewport.set(0, 0, size, size2);
        if (mode == 0 || mode2 == 0) {
            super.onMeasure(i, i2);
        } else if (size <= 0 || size2 <= 0) {
            super.onMeasure(i, i2);
        } else {
            int paddingTop = getPaddingTop() + getPaddingBottom();
            int paddingLeft = getPaddingLeft() + getPaddingRight();
            int childCount = getChildCount();
            int i8 = 0;
            for (int i9 = 0; i9 < childCount; i9++) {
                View pageAt = getPageAt(i9);
                if (pageAt.getVisibility() != 8) {
                    LayoutParams layoutParams = (LayoutParams) pageAt.getLayoutParams();
                    int i10 = 1073741824;
                    if (!layoutParams.isFullScreenPage) {
                        i6 = layoutParams.width == -2 ? Integer.MIN_VALUE : 1073741824;
                        if (layoutParams.height == -2) {
                            i10 = Integer.MIN_VALUE;
                        }
                        i7 = ((getViewportWidth() - paddingLeft) - this.mInsets.left) - this.mInsets.right;
                        i5 = ((getViewportHeight() - paddingTop) - this.mInsets.top) - this.mInsets.bottom;
                        this.mNormalChildHeight = i5;
                    } else {
                        i7 = getViewportWidth();
                        i5 = getViewportHeight();
                        i6 = 1073741824;
                    }
                    if (i8 == 0) {
                        i8 = i7;
                    }
                    pageAt.measure(MeasureSpec.makeMeasureSpec(i7, i6), MeasureSpec.makeMeasureSpec(i5, i10));
                }
            }
            setMeasuredDimension(i3, i4);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        if (getChildCount() != 0) {
            int childCount = getChildCount();
            int viewportOffsetX = getViewportOffsetX();
            int viewportOffsetY = getViewportOffsetY();
            this.mViewport.offset(viewportOffsetX, viewportOffsetY);
            int i6 = this.mIsRtl ? childCount - 1 : 0;
            int i7 = -1;
            int i8 = this.mIsRtl ? -1 : childCount;
            if (!this.mIsRtl) {
                i7 = 1;
            }
            int paddingTop = getPaddingTop() + getPaddingBottom();
            int paddingLeft = (((LayoutParams) getChildAt(i6).getLayoutParams()).isFullScreenPage ? 0 : getPaddingLeft()) + viewportOffsetX;
            if (this.mPageScrolls == null || childCount != this.mChildCountOnLastLayout) {
                this.mPageScrolls = new int[childCount];
            }
            while (i6 != i8) {
                View pageAt = getPageAt(i6);
                if (pageAt.getVisibility() != 8) {
                    LayoutParams layoutParams = (LayoutParams) pageAt.getLayoutParams();
                    if (layoutParams.isFullScreenPage) {
                        i5 = viewportOffsetY;
                    } else {
                        i5 = getPaddingTop() + viewportOffsetY + this.mInsets.top + (((((getViewportHeight() - this.mInsets.top) - this.mInsets.bottom) - paddingTop) - pageAt.getMeasuredHeight()) / 2);
                    }
                    int measuredWidth = pageAt.getMeasuredWidth();
                    pageAt.layout(paddingLeft, i5, paddingLeft + pageAt.getMeasuredWidth(), pageAt.getMeasuredHeight() + i5);
                    this.mPageScrolls[i6] = (paddingLeft - (layoutParams.isFullScreenPage ? 0 : getPaddingLeft())) - viewportOffsetX;
                    int i9 = this.mPageSpacing;
                    int i10 = i6 + i7;
                    LayoutParams layoutParams2 = i10 != i8 ? (LayoutParams) getPageAt(i10).getLayoutParams() : null;
                    if (layoutParams.isFullScreenPage) {
                        i9 = getPaddingLeft();
                    } else if (layoutParams2 != null && layoutParams2.isFullScreenPage) {
                        i9 = getPaddingRight();
                    }
                    paddingLeft += measuredWidth + i9 + getChildGap();
                }
                i6 += i7;
            }
            LayoutTransition layoutTransition = getLayoutTransition();
            if (layoutTransition == null || !layoutTransition.isRunning()) {
                updateMaxScrollX();
            } else {
                layoutTransition.addTransitionListener(new TransitionListener() {
                    public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                    }

                    public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                        if (!layoutTransition.isRunning()) {
                            layoutTransition.removeTransitionListener(this);
                            PagedView.this.updateMaxScrollX();
                        }
                    }
                });
            }
            if (this.mFirstLayout && this.mCurrentPage >= 0 && this.mCurrentPage < childCount) {
                updateCurrentPageScroll();
                this.mFirstLayout = false;
            }
            if (this.mScroller.isFinished() && this.mChildCountOnLastLayout != childCount) {
                setCurrentPage(getNextPage());
            }
            this.mChildCountOnLastLayout = childCount;
            if (isReordering(true)) {
                updateDragViewTranslationDuringDrag();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateMaxScrollX() {
        this.mMaxScrollX = computeMaxScrollX();
    }

    /* access modifiers changed from: protected */
    public int computeMaxScrollX() {
        int childCount = getChildCount();
        int i = 0;
        if (childCount <= 0) {
            return 0;
        }
        if (!this.mIsRtl) {
            i = childCount - 1;
        }
        return getScrollForPage(i);
    }

    public void setPageSpacing(int i) {
        this.mPageSpacing = i;
        requestLayout();
    }

    public void onChildViewAdded(View view, View view2) {
        if (this.mPageIndicator != null && !isReordering(false)) {
            this.mPageIndicator.addMarker();
        }
        updateFreescrollBounds();
        invalidate();
    }

    public void onChildViewRemoved(View view, View view2) {
        updateFreescrollBounds();
        this.mCurrentPage = validateNewPage(this.mCurrentPage);
        invalidate();
    }

    private void removeMarkerForView() {
        if (this.mPageIndicator != null && !isReordering(false)) {
            this.mPageIndicator.removeMarker();
        }
    }

    public void removeView(View view) {
        removeMarkerForView();
        super.removeView(view);
    }

    public void removeViewInLayout(View view) {
        removeMarkerForView();
        super.removeViewInLayout(view);
    }

    public void removeViewAt(int i) {
        removeMarkerForView();
        super.removeViewAt(i);
    }

    public void removeAllViewsInLayout() {
        if (this.mPageIndicator != null) {
            this.mPageIndicator.setMarkersCount(0);
        }
        super.removeAllViewsInLayout();
    }

    /* access modifiers changed from: protected */
    public int getChildOffset(int i) {
        if (i < 0 || i > getChildCount() - 1) {
            return 0;
        }
        return getPageAt(i).getLeft() - getViewportOffsetX();
    }

    /* access modifiers changed from: protected */
    public void getFreeScrollPageRange(int[] iArr) {
        iArr[0] = 0;
        iArr[1] = Math.max(0, getChildCount() - 1);
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        int indexToPage = indexToPage(indexOfChild(view));
        if (indexToPage == this.mCurrentPage && this.mScroller.isFinished()) {
            return false;
        }
        snapToPage(indexToPage);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        int i2;
        if (this.mNextPage != -1) {
            i2 = this.mNextPage;
        } else {
            i2 = this.mCurrentPage;
        }
        View pageAt = getPageAt(i2);
        if (pageAt != null) {
            return pageAt.requestFocus(i, rect);
        }
        return false;
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        if (super.dispatchUnhandledMove(view, i)) {
            return true;
        }
        if (this.mIsRtl) {
            if (i == 17) {
                i = 66;
            } else if (i == 66) {
                i = 17;
            }
        }
        if (i == 17) {
            if (getCurrentPage() > 0) {
                snapToPage(getCurrentPage() - 1);
                return true;
            }
        } else if (i == 66 && getCurrentPage() < getPageCount() - 1) {
            snapToPage(getCurrentPage() + 1);
            return true;
        }
        return false;
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (getDescendantFocusability() != 393216) {
            if (this.mCurrentPage >= 0 && this.mCurrentPage < getPageCount()) {
                getPageAt(this.mCurrentPage).addFocusables(arrayList, i, i2);
            }
            if (i == 17) {
                if (this.mCurrentPage > 0) {
                    getPageAt(this.mCurrentPage - 1).addFocusables(arrayList, i, i2);
                }
            } else if (i == 66 && this.mCurrentPage < getPageCount() - 1) {
                getPageAt(this.mCurrentPage + 1).addFocusables(arrayList, i, i2);
            }
        }
    }

    public void focusableViewAvailable(View view) {
        View pageAt = getPageAt(this.mCurrentPage);
        View view2 = view;
        while (view2 != pageAt) {
            if (view2 != this && (view2.getParent() instanceof View)) {
                view2 = (View) view2.getParent();
            } else {
                return;
            }
        }
        super.focusableViewAvailable(view);
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (z) {
            getPageAt(this.mCurrentPage).cancelLongPress();
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    /* access modifiers changed from: protected */
    public boolean hitsPreviousPage(float f, float f2) {
        boolean z = false;
        if (this.mIsRtl) {
            if (f > ((float) (((getViewportOffsetX() + getViewportWidth()) - getPaddingRight()) - this.mPageSpacing))) {
                z = true;
            }
            return z;
        }
        if (f < ((float) (getViewportOffsetX() + getPaddingLeft() + this.mPageSpacing))) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public boolean hitsNextPage(float f, float f2) {
        boolean z = false;
        if (this.mIsRtl) {
            if (f < ((float) (getViewportOffsetX() + getPaddingLeft() + this.mPageSpacing))) {
                z = true;
            }
            return z;
        }
        if (f > ((float) (((getViewportOffsetX() + getViewportWidth()) - getPaddingRight()) - this.mPageSpacing))) {
            z = true;
        }
        return z;
    }

    private boolean isTouchPointInViewportWithBuffer(int i, int i2) {
        sTmpRect.set(this.mViewport.left - (this.mViewport.width() / 2), this.mViewport.top, this.mViewport.right + (this.mViewport.width() / 2), this.mViewport.bottom);
        return sTmpRect.contains(i, i2);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        acquireVelocityTrackerAndAddMovement(motionEvent);
        if (getChildCount() <= 0) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        int action = motionEvent.getAction();
        boolean z = true;
        if (action == 2 && this.mTouchState == 1) {
            return true;
        }
        int i = action & 255;
        if (i != 6) {
            switch (i) {
                case 0:
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    this.mDownMotionX = x;
                    this.mDownMotionY = y;
                    this.mDownScrollX = (float) getScrollX();
                    this.mLastMotionX = x;
                    this.mLastMotionY = y;
                    float[] mapPointFromViewToParent = mapPointFromViewToParent(this, x, y);
                    this.mParentDownMotionX = mapPointFromViewToParent[0];
                    this.mParentDownMotionY = mapPointFromViewToParent[1];
                    this.mLastMotionXRemainder = 0.0f;
                    this.mTotalMotionX = 0.0f;
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    if (!(this.mScroller.isFinished() || Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) < this.mTouchSlop / 3)) {
                        if (!isTouchPointInViewportWithBuffer((int) this.mDownMotionX, (int) this.mDownMotionY)) {
                            this.mTouchState = 0;
                            break;
                        } else {
                            this.mTouchState = 1;
                            break;
                        }
                    } else {
                        this.mTouchState = 0;
                        if (!this.mScroller.isFinished() && !this.mFreeScroll) {
                            setCurrentPage(getNextPage());
                            pageEndTransition();
                            break;
                        }
                    }
                    break;
                case 1:
                case 3:
                    resetTouchState();
                    break;
                case 2:
                    if (this.mActivePointerId != -1) {
                        determineScrollingStart(motionEvent);
                        break;
                    }
                    break;
            }
        } else {
            onSecondaryPointerUp(motionEvent);
            releaseVelocityTracker();
        }
        if (this.mTouchState == 0) {
            z = false;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent) {
        determineScrollingStart(motionEvent, 1.0f);
    }

    /* access modifiers changed from: protected */
    public void determineScrollingStart(MotionEvent motionEvent, float f) {
        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
        if (findPointerIndex != -1) {
            float x = motionEvent.getX(findPointerIndex);
            if (isTouchPointInViewportWithBuffer((int) x, (int) motionEvent.getY(findPointerIndex))) {
                if (((int) Math.abs(x - this.mLastMotionX)) > Math.round(f * ((float) this.mTouchSlop))) {
                    this.mTouchState = 1;
                    this.mTotalMotionX += Math.abs(this.mLastMotionX - x);
                    this.mLastMotionX = x;
                    this.mLastMotionXRemainder = 0.0f;
                    onScrollInteractionBegin();
                    pageBeginTransition();
                    requestDisallowInterceptTouchEvent(true);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCurrentPageLongPress() {
        View pageAt = getPageAt(this.mCurrentPage);
        if (pageAt != null) {
            pageAt.cancelLongPress();
        }
    }

    /* access modifiers changed from: protected */
    public float getScrollProgress(int i, View view, int i2) {
        int i3;
        int scrollForPage = i - (getScrollForPage(i2) + (getViewportWidth() / 2));
        int childCount = getChildCount();
        int i4 = i2 + 1;
        if ((scrollForPage < 0 && !this.mIsRtl) || (scrollForPage > 0 && this.mIsRtl)) {
            i4 = i2 - 1;
        }
        if (i4 < 0 || i4 > childCount - 1) {
            i3 = view.getMeasuredWidth() + this.mPageSpacing;
        } else {
            i3 = Math.abs(getScrollForPage(i4) - getScrollForPage(i2));
        }
        return Math.max(Math.min(((float) scrollForPage) / (((float) i3) * 1.0f), 1.0f), -1.0f);
    }

    public int getScrollForPage(int i) {
        if (this.mPageScrolls == null || i >= this.mPageScrolls.length || i < 0) {
            return 0;
        }
        return this.mPageScrolls[i];
    }

    public int getLayoutTransitionOffsetForPage(int i) {
        int i2 = 0;
        if (this.mPageScrolls == null || i >= this.mPageScrolls.length || i < 0) {
            return 0;
        }
        View childAt = getChildAt(i);
        if (!((LayoutParams) childAt.getLayoutParams()).isFullScreenPage) {
            i2 = this.mIsRtl ? getPaddingRight() : getPaddingLeft();
        }
        return (int) (childAt.getX() - ((float) ((this.mPageScrolls[i] + i2) + getViewportOffsetX())));
    }

    /* access modifiers changed from: protected */
    public void dampedOverScroll(float f) {
        if (Float.compare(f, 0.0f) != 0) {
            int dampedScroll = OverScroll.dampedScroll(f, getViewportWidth());
            if (f < 0.0f) {
                this.mOverScrollX = dampedScroll;
                super.scrollTo(this.mOverScrollX, getScrollY());
            } else {
                this.mOverScrollX = this.mMaxScrollX + dampedScroll;
                super.scrollTo(this.mOverScrollX, getScrollY());
            }
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void overScroll(float f) {
        dampedOverScroll(f);
    }

    public boolean enableFreeScroll() {
        setEnableFreeScroll(true);
        return true;
    }

    public void disableFreeScroll() {
        setEnableFreeScroll(false);
    }

    /* access modifiers changed from: 0000 */
    public void updateFreescrollBounds() {
        getFreeScrollPageRange(this.mTempVisiblePagesRange);
        if (this.mIsRtl) {
            this.mFreeScrollMinScrollX = getScrollForPage(this.mTempVisiblePagesRange[1]);
            this.mFreeScrollMaxScrollX = getScrollForPage(this.mTempVisiblePagesRange[0]);
            return;
        }
        this.mFreeScrollMinScrollX = getScrollForPage(this.mTempVisiblePagesRange[0]);
        this.mFreeScrollMaxScrollX = getScrollForPage(this.mTempVisiblePagesRange[1]);
    }

    private void setEnableFreeScroll(boolean z) {
        boolean z2 = this.mFreeScroll;
        this.mFreeScroll = z;
        if (this.mFreeScroll) {
            updateFreescrollBounds();
            getFreeScrollPageRange(this.mTempVisiblePagesRange);
            if (getCurrentPage() < this.mTempVisiblePagesRange[0]) {
                setCurrentPage(this.mTempVisiblePagesRange[0]);
            } else if (getCurrentPage() > this.mTempVisiblePagesRange[1]) {
                setCurrentPage(this.mTempVisiblePagesRange[1]);
            }
        } else if (z2) {
            snapToPage(getNextPage());
        }
        setEnableOverscroll(!z);
    }

    /* access modifiers changed from: protected */
    public void setEnableOverscroll(boolean z) {
        this.mAllowOverScroll = z;
    }

    private int getNearestHoverOverPageIndex() {
        if (this.mDragView == null) {
            return -1;
        }
        int left = (int) (((float) (this.mDragView.getLeft() + (this.mDragView.getMeasuredWidth() / 2))) + this.mDragView.getTranslationX());
        getFreeScrollPageRange(this.mTempVisiblePagesRange);
        int i = Integer.MAX_VALUE;
        int indexOfChild = indexOfChild(this.mDragView);
        for (int i2 = this.mTempVisiblePagesRange[0]; i2 <= this.mTempVisiblePagesRange[1]; i2++) {
            View pageAt = getPageAt(i2);
            int abs = Math.abs(left - (pageAt.getLeft() + (pageAt.getMeasuredWidth() / 2)));
            if (abs < i) {
                indexOfChild = i2;
                i = abs;
            }
        }
        return indexOfChild;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        if (getChildCount() <= 0) {
            return super.onTouchEvent(motionEvent);
        }
        acquireVelocityTrackerAndAddMovement(motionEvent);
        int action = motionEvent.getAction() & 255;
        if (action != 6) {
            boolean z = false;
            switch (action) {
                case 0:
                    if (!this.mScroller.isFinished()) {
                        abortScrollerAnimation(false);
                    }
                    float x = motionEvent.getX();
                    this.mLastMotionX = x;
                    this.mDownMotionX = x;
                    float y = motionEvent.getY();
                    this.mLastMotionY = y;
                    this.mDownMotionY = y;
                    this.mDownScrollX = (float) getScrollX();
                    float[] mapPointFromViewToParent = mapPointFromViewToParent(this, this.mLastMotionX, this.mLastMotionY);
                    this.mParentDownMotionX = mapPointFromViewToParent[0];
                    this.mParentDownMotionY = mapPointFromViewToParent[1];
                    this.mLastMotionXRemainder = 0.0f;
                    this.mTotalMotionX = 0.0f;
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    if (this.mTouchState == 1) {
                        onScrollInteractionBegin();
                        pageBeginTransition();
                        break;
                    }
                    break;
                case 1:
                    if (this.mTouchState == 1) {
                        int i = this.mActivePointerId;
                        float x2 = motionEvent.getX(motionEvent.findPointerIndex(i));
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                        int xVelocity = (int) velocityTracker.getXVelocity(i);
                        int i2 = (int) (x2 - this.mDownMotionX);
                        float measuredWidth = (float) getPageAt(this.mCurrentPage).getMeasuredWidth();
                        boolean z2 = ((float) Math.abs(i2)) > 0.4f * measuredWidth;
                        this.mTotalMotionX += Math.abs((this.mLastMotionX + this.mLastMotionXRemainder) - x2);
                        boolean z3 = this.mTotalMotionX > 25.0f && shouldFlingForVelocity(xVelocity);
                        if (!this.mFreeScroll) {
                            boolean z4 = ((float) Math.abs(i2)) > measuredWidth * RETURN_TO_ORIGINAL_PAGE_THRESHOLD && Math.signum((float) xVelocity) != Math.signum((float) i2) && z3;
                            boolean z5 = !this.mIsRtl ? i2 < 0 : i2 > 0;
                            if (!this.mIsRtl ? xVelocity < 0 : xVelocity > 0) {
                                z = true;
                            }
                            if (((z2 && !z5 && !z3) || (z3 && !z)) && this.mCurrentPage > 0) {
                                snapToPageWithVelocity(z4 ? this.mCurrentPage : this.mCurrentPage - 1, xVelocity);
                            } else if (((!z2 || !z5 || z3) && (!z3 || !z)) || this.mCurrentPage >= getChildCount() - 1) {
                                snapToDestination();
                            } else {
                                snapToPageWithVelocity(z4 ? this.mCurrentPage : this.mCurrentPage + 1, xVelocity);
                            }
                        } else {
                            if (!this.mScroller.isFinished()) {
                                abortScrollerAnimation(true);
                            }
                            float scaleX = getScaleX();
                            int i3 = (int) (((float) (-xVelocity)) * scaleX);
                            int scrollX = (int) (((float) getScrollX()) * scaleX);
                            this.mScroller.setInterpolator(this.mDefaultInterpolator);
                            this.mScroller.fling(scrollX, getScrollY(), i3, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                            this.mNextPage = getPageNearestToCenterOfScreen((int) (((float) this.mScroller.getFinalX()) / scaleX));
                            invalidate();
                        }
                        onScrollInteractionEnd();
                    } else if (this.mTouchState == 2) {
                        int max = Math.max(0, this.mCurrentPage - 1);
                        if (max != this.mCurrentPage) {
                            snapToPage(max);
                        } else {
                            snapToDestination();
                        }
                    } else if (this.mTouchState == 3) {
                        int min = Math.min(getChildCount() - 1, this.mCurrentPage + 1);
                        if (min != this.mCurrentPage) {
                            snapToPage(min);
                        } else {
                            snapToDestination();
                        }
                    } else if (this.mTouchState == 4) {
                        this.mLastMotionX = motionEvent.getX();
                        this.mLastMotionY = motionEvent.getY();
                        float[] mapPointFromViewToParent2 = mapPointFromViewToParent(this, this.mLastMotionX, this.mLastMotionY);
                        this.mParentDownMotionX = mapPointFromViewToParent2[0];
                        this.mParentDownMotionY = mapPointFromViewToParent2[1];
                        updateDragViewTranslationDuringDrag();
                    } else if (!this.mCancelTap) {
                        onUnhandledTap(motionEvent);
                    }
                    removeCallbacks(this.mSidePageHoverRunnable);
                    resetTouchState();
                    break;
                case 2:
                    if (this.mTouchState != 1) {
                        if (this.mTouchState != 4) {
                            determineScrollingStart(motionEvent);
                            break;
                        } else {
                            this.mLastMotionX = motionEvent.getX();
                            this.mLastMotionY = motionEvent.getY();
                            float[] mapPointFromViewToParent3 = mapPointFromViewToParent(this, this.mLastMotionX, this.mLastMotionY);
                            this.mParentDownMotionX = mapPointFromViewToParent3[0];
                            this.mParentDownMotionY = mapPointFromViewToParent3[1];
                            updateDragViewTranslationDuringDrag();
                            final int indexOfChild = indexOfChild(this.mDragView);
                            final int nearestHoverOverPageIndex = getNearestHoverOverPageIndex();
                            if (nearestHoverOverPageIndex > 0 && nearestHoverOverPageIndex != indexOfChild(this.mDragView)) {
                                this.mTempVisiblePagesRange[0] = 0;
                                this.mTempVisiblePagesRange[1] = getPageCount() - 1;
                                getFreeScrollPageRange(this.mTempVisiblePagesRange);
                                if (this.mTempVisiblePagesRange[0] <= nearestHoverOverPageIndex && nearestHoverOverPageIndex <= this.mTempVisiblePagesRange[1] && nearestHoverOverPageIndex != this.mSidePageHoverIndex && this.mScroller.isFinished()) {
                                    this.mSidePageHoverIndex = nearestHoverOverPageIndex;
                                    this.mSidePageHoverRunnable = new Runnable() {
                                        public void run() {
                                            PagedView.this.snapToPage(nearestHoverOverPageIndex);
                                            int i = indexOfChild < nearestHoverOverPageIndex ? -1 : 1;
                                            int i2 = indexOfChild > nearestHoverOverPageIndex ? indexOfChild - 1 : nearestHoverOverPageIndex;
                                            for (int i3 = indexOfChild < nearestHoverOverPageIndex ? indexOfChild + 1 : nearestHoverOverPageIndex; i3 <= i2; i3++) {
                                                View childAt = PagedView.this.getChildAt(i3);
                                                int viewportOffsetX = PagedView.this.getViewportOffsetX() + PagedView.this.getChildOffset(i3);
                                                int viewportOffsetX2 = PagedView.this.getViewportOffsetX() + PagedView.this.getChildOffset(i3 + i);
                                                ObjectAnimator objectAnimator = (ObjectAnimator) childAt.getTag();
                                                if (objectAnimator != null) {
                                                    objectAnimator.cancel();
                                                }
                                                childAt.setTranslationX((float) (viewportOffsetX - viewportOffsetX2));
                                                ObjectAnimator ofFloat = LauncherAnimUtils.ofFloat(childAt, View.TRANSLATION_X, 0.0f);
                                                ofFloat.setDuration((long) PagedView.REORDERING_REORDER_REPOSITION_DURATION);
                                                ofFloat.start();
                                                childAt.setTag(ofFloat);
                                            }
                                            PagedView.this.removeView(PagedView.this.mDragView);
                                            PagedView.this.addView(PagedView.this.mDragView, nearestHoverOverPageIndex);
                                            PagedView.this.mSidePageHoverIndex = -1;
                                            if (PagedView.this.mPageIndicator != null) {
                                                PagedView.this.mPageIndicator.setActiveMarker(PagedView.this.getNextPage());
                                            }
                                        }
                                    };
                                    postDelayed(this.mSidePageHoverRunnable, (long) REORDERING_SIDE_PAGE_HOVER_TIMEOUT);
                                    break;
                                }
                            } else {
                                removeCallbacks(this.mSidePageHoverRunnable);
                                this.mSidePageHoverIndex = -1;
                                break;
                            }
                        }
                    } else {
                        int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                        if (findPointerIndex != -1) {
                            float x3 = motionEvent.getX(findPointerIndex);
                            float f = (this.mLastMotionX + this.mLastMotionXRemainder) - x3;
                            this.mTotalMotionX += Math.abs(f);
                            if (Math.abs(f) < 1.0f) {
                                awakenScrollBars();
                                break;
                            } else {
                                int i4 = (int) f;
                                scrollBy(i4, 0);
                                this.mLastMotionX = x3;
                                this.mLastMotionXRemainder = f - ((float) i4);
                                break;
                            }
                        } else {
                            return true;
                        }
                    }
                    break;
                case 3:
                    if (this.mTouchState == 1) {
                        snapToDestination();
                        onScrollInteractionEnd();
                    }
                    resetTouchState();
                    break;
            }
        } else {
            onSecondaryPointerUp(motionEvent);
            releaseVelocityTracker();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean shouldFlingForVelocity(int i) {
        return Math.abs(i) > this.mFlingThresholdVelocity;
    }

    private void resetTouchState() {
        releaseVelocityTracker();
        endReordering();
        this.mCancelTap = false;
        this.mTouchState = 0;
        this.mActivePointerId = -1;
    }

    /* access modifiers changed from: protected */
    public void onUnhandledTap(MotionEvent motionEvent) {
        Launcher.getLauncher(getContext()).onClick(this);
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        float f;
        float f2;
        if ((motionEvent.getSource() & 2) != 0 && motionEvent.getAction() == 8) {
            if ((motionEvent.getMetaState() & 1) != 0) {
                f2 = motionEvent.getAxisValue(9);
                f = 0.0f;
            } else {
                float f3 = -motionEvent.getAxisValue(9);
                f = f3;
                f2 = motionEvent.getAxisValue(10);
            }
            int i = (f2 > 0.0f ? 1 : (f2 == 0.0f ? 0 : -1));
            if (!(i == 0 && f == 0.0f)) {
                boolean z = false;
                if (!this.mIsRtl ? i > 0 || f > 0.0f : f2 < 0.0f || f < 0.0f) {
                    z = true;
                }
                if (z) {
                    scrollRight();
                } else {
                    scrollLeft();
                }
                return true;
            }
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    private void acquireVelocityTrackerAndAddMovement(MotionEvent motionEvent) {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.clear();
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int action = (motionEvent.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
        if (motionEvent.getPointerId(action) == this.mActivePointerId) {
            int i = action == 0 ? 1 : 0;
            float x = motionEvent.getX(i);
            this.mDownMotionX = x;
            this.mLastMotionX = x;
            this.mLastMotionY = motionEvent.getY(i);
            this.mLastMotionXRemainder = 0.0f;
            this.mActivePointerId = motionEvent.getPointerId(i);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        int indexToPage = indexToPage(indexOfChild(view));
        if (indexToPage >= 0 && indexToPage != getCurrentPage() && !isInTouchMode()) {
            snapToPage(indexToPage);
        }
    }

    /* access modifiers changed from: 0000 */
    public int getPageNearestToCenterOfScreen() {
        return getPageNearestToCenterOfScreen(getScrollX());
    }

    private int getPageNearestToCenterOfScreen(int i) {
        int viewportOffsetX = getViewportOffsetX() + i + (getViewportWidth() / 2);
        int childCount = getChildCount();
        int i2 = Integer.MAX_VALUE;
        int i3 = -1;
        for (int i4 = 0; i4 < childCount; i4++) {
            int abs = Math.abs(((getViewportOffsetX() + getChildOffset(i4)) + (getPageAt(i4).getMeasuredWidth() / 2)) - viewportOffsetX);
            if (abs < i2) {
                i3 = i4;
                i2 = abs;
            }
        }
        return i3;
    }

    /* access modifiers changed from: protected */
    public void snapToDestination() {
        snapToPage(getPageNearestToCenterOfScreen(), getPageSnapDuration());
    }

    /* access modifiers changed from: protected */
    public boolean isInOverScroll() {
        return this.mOverScrollX > this.mMaxScrollX || this.mOverScrollX < 0;
    }

    /* access modifiers changed from: protected */
    public int getPageSnapDuration() {
        return isInOverScroll() ? OVERSCROLL_PAGE_SNAP_ANIMATION_DURATION : PAGE_SNAP_ANIMATION_DURATION;
    }

    private float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((float) (((double) (f - 0.5f)) * 0.4712389167638204d)));
    }

    /* access modifiers changed from: protected */
    public void snapToPageWithVelocity(int i, int i2) {
        int validateNewPage = validateNewPage(i);
        int viewportWidth = getViewportWidth() / 2;
        int scrollForPage = getScrollForPage(validateNewPage) - getUnboundedScrollX();
        if (Math.abs(i2) < this.mMinFlingVelocity) {
            snapToPage(validateNewPage, PAGE_SNAP_ANIMATION_DURATION);
            return;
        }
        float f = (float) viewportWidth;
        snapToPage(validateNewPage, scrollForPage, Math.round(Math.abs((f + (distanceInfluenceForSnapDuration(Math.min(1.0f, (((float) Math.abs(scrollForPage)) * 1.0f) / ((float) (viewportWidth * 2)))) * f)) / ((float) Math.max(this.mMinSnapVelocity, Math.abs(i2)))) * 1000.0f) * 4);
    }

    public void snapToPage(int i) {
        snapToPage(i, PAGE_SNAP_ANIMATION_DURATION);
    }

    public void snapToPageImmediately(int i) {
        snapToPage(i, PAGE_SNAP_ANIMATION_DURATION, true, null);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int i, int i2) {
        snapToPage(i, i2, false, null);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int i, int i2, TimeInterpolator timeInterpolator) {
        snapToPage(i, i2, false, timeInterpolator);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int i, int i2, boolean z, TimeInterpolator timeInterpolator) {
        int validateNewPage = validateNewPage(i);
        snapToPage(validateNewPage, getScrollForPage(validateNewPage) - getUnboundedScrollX(), i2, z, timeInterpolator);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int i, int i2, int i3) {
        snapToPage(i, i2, i3, false, null);
    }

    /* access modifiers changed from: protected */
    public void snapToPage(int i, int i2, int i3, boolean z, TimeInterpolator timeInterpolator) {
        int i4;
        this.mNextPage = validateNewPage(i);
        awakenScrollBars(i3);
        if (z) {
            i4 = 0;
        } else {
            if (i3 == 0) {
                i3 = Math.abs(i2);
            }
            i4 = i3;
        }
        if (i4 != 0) {
            pageBeginTransition();
        }
        if (!this.mScroller.isFinished()) {
            abortScrollerAnimation(false);
        }
        if (timeInterpolator != null) {
            this.mScroller.setInterpolator(timeInterpolator);
        } else {
            this.mScroller.setInterpolator(this.mDefaultInterpolator);
        }
        this.mScroller.startScroll(getUnboundedScrollX(), 0, i2, 0, i4);
        updatePageIndicator();
        if (z) {
            computeScroll();
            pageEndTransition();
        }
        invalidate();
    }

    public void scrollLeft() {
        if (getNextPage() > 0) {
            snapToPage(getNextPage() - 1);
        }
    }

    public void scrollRight() {
        if (getNextPage() < getChildCount() - 1) {
            snapToPage(getNextPage() + 1);
        }
    }

    public boolean performLongClick() {
        this.mCancelTap = true;
        return super.performLongClick();
    }

    private void animateDragViewToOriginalPosition() {
        if (this.mDragView != null) {
            ObjectAnimator duration = LauncherAnimUtils.ofPropertyValuesHolder(this.mDragView, new PropertyListBuilder().scale(1.0f).translationX(0.0f).translationY(0.0f).build()).setDuration((long) REORDERING_DROP_REPOSITION_DURATION);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PagedView.this.onPostReorderingAnimationCompleted();
                }
            });
            duration.start();
        }
    }

    public void onStartReordering() {
        this.mTouchState = 4;
        this.mIsReordering = true;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void onPostReorderingAnimationCompleted() {
        this.mPostReorderingPreZoomInRemainingAnimationCount--;
        if (this.mPostReorderingPreZoomInRunnable != null && this.mPostReorderingPreZoomInRemainingAnimationCount == 0) {
            this.mPostReorderingPreZoomInRunnable.run();
            this.mPostReorderingPreZoomInRunnable = null;
        }
    }

    public void onEndReordering() {
        this.mIsReordering = false;
    }

    public boolean startReordering(View view) {
        int indexOfChild = indexOfChild(view);
        if (this.mTouchState != 0 || indexOfChild <= 0) {
            return false;
        }
        this.mTempVisiblePagesRange[0] = 0;
        this.mTempVisiblePagesRange[1] = getPageCount() - 1;
        getFreeScrollPageRange(this.mTempVisiblePagesRange);
        this.mReorderingStarted = true;
        if (this.mTempVisiblePagesRange[0] > indexOfChild || indexOfChild > this.mTempVisiblePagesRange[1]) {
            return false;
        }
        this.mDragView = getChildAt(indexOfChild);
        this.mDragView.animate().scaleX(1.15f).scaleY(1.15f).setDuration(100).start();
        this.mDragViewBaselineLeft = (float) this.mDragView.getLeft();
        snapToPage(getPageNearestToCenterOfScreen());
        disableFreeScroll();
        onStartReordering();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isReordering(boolean z) {
        boolean z2 = this.mIsReordering;
        if (!z) {
            return z2;
        }
        return z2 & (this.mTouchState == 4);
    }

    /* access modifiers changed from: 0000 */
    public void endReordering() {
        if (this.mReorderingStarted) {
            this.mReorderingStarted = false;
            this.mPostReorderingPreZoomInRunnable = new Runnable() {
                public void run() {
                    PagedView.this.onEndReordering();
                    PagedView.this.enableFreeScroll();
                }
            };
            this.mPostReorderingPreZoomInRemainingAnimationCount = 2;
            snapToPage(indexOfChild(this.mDragView), 0);
            animateDragViewToOriginalPosition();
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setScrollable(getPageCount() > 1);
        if (getCurrentPage() < getPageCount() - 1) {
            accessibilityNodeInfo.addAction(4096);
        }
        if (getCurrentPage() > 0) {
            accessibilityNodeInfo.addAction(8192);
        }
        accessibilityNodeInfo.setClassName(getClass().getName());
        accessibilityNodeInfo.setLongClickable(false);
        accessibilityNodeInfo.removeAction(AccessibilityAction.ACTION_LONG_CLICK);
    }

    public void sendAccessibilityEvent(int i) {
        if (i != 4096) {
            super.sendAccessibilityEvent(i);
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        boolean z = true;
        if (getPageCount() <= 1) {
            z = false;
        }
        accessibilityEvent.setScrollable(z);
    }

    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (super.performAccessibilityAction(i, bundle)) {
            return true;
        }
        if (i != 4096) {
            if (i == 8192 && getCurrentPage() > 0) {
                scrollLeft();
                return true;
            }
        } else if (getCurrentPage() < getPageCount() - 1) {
            scrollRight();
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public String getPageIndicatorDescription() {
        return getCurrentPageDescription();
    }

    /* access modifiers changed from: protected */
    public String getCurrentPageDescription() {
        return getContext().getString(C0622R.string.default_scroll_format, new Object[]{Integer.valueOf(getNextPage() + 1), Integer.valueOf(getChildCount())});
    }
}
