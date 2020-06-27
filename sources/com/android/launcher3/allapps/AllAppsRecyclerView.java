package com.android.launcher3.allapps;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.p004v7.widget.RecyclerView;
import android.support.p004v7.widget.RecyclerView.Adapter;
import android.support.p004v7.widget.RecyclerView.AdapterDataObserver;
import android.support.p004v7.widget.RecyclerView.OnScrollListener;
import android.support.p004v7.widget.RecyclerView.RecycledViewPool;
import android.util.AttributeSet;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import com.android.launcher3.BaseRecyclerView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.allapps.AlphabeticalAppsList.AdapterItem;
import com.android.launcher3.allapps.AlphabeticalAppsList.FastScrollSectionInfo;
import com.android.launcher3.anim.SpringAnimationHandler;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.graphics.DrawableFactory;
import com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.touch.SwipeDetector.Listener;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import java.util.List;

public class AllAppsRecyclerView extends BaseRecyclerView implements LogContainerProvider {
    public static final Property<AllAppsRecyclerView, Float> CONTENT_TRANS_Y = new Property<AllAppsRecyclerView, Float>(Float.class, "appsRecyclerViewContentTransY") {
        public Float get(AllAppsRecyclerView allAppsRecyclerView) {
            return Float.valueOf(allAppsRecyclerView.getContentTranslationY());
        }

        public void set(AllAppsRecyclerView allAppsRecyclerView, Float f) {
            allAppsRecyclerView.setContentTranslationY(f.floatValue());
        }
    };
    private AlphabeticalAppsList mApps;
    /* access modifiers changed from: private */
    public SparseIntArray mCachedScrollPositions;
    private float mContentTranslationY;
    private AllAppsBackgroundDrawable mEmptySearchBackground;
    private int mEmptySearchBackgroundTopOffset;
    private AllAppsFastScrollHelper mFastScrollHelper;
    private int mNumAppsPerRow;
    /* access modifiers changed from: private */
    public OverScrollHelper mOverScrollHelper;
    private SwipeDetector mPullDetector;
    /* access modifiers changed from: private */
    public SpringAnimationHandler mSpringAnimationHandler;
    private SparseIntArray mViewHeights;

    private class OverScrollHelper implements Listener {
        private static final float MAX_OVERSCROLL_PERCENTAGE = 0.07f;
        private static final float MAX_RELEASE_VELOCITY = 5000.0f;
        private boolean mAlreadyScrollingUp;
        private float mFirstDisplacement;
        private int mFirstScrollYOnScrollUp;
        private boolean mIsInOverScroll;

        public void onDragStart(boolean z) {
        }

        private OverScrollHelper() {
            this.mFirstDisplacement = 0.0f;
        }

        public boolean onDrag(float f, float f2) {
            boolean z = true;
            boolean z2 = f > 0.0f;
            if (!z2) {
                this.mAlreadyScrollingUp = false;
            } else if (!this.mAlreadyScrollingUp) {
                this.mFirstScrollYOnScrollUp = AllAppsRecyclerView.this.getCurrentScrollY();
                this.mAlreadyScrollingUp = true;
            }
            boolean z3 = this.mIsInOverScroll;
            if (AllAppsRecyclerView.this.mScrollbar.isDraggingThumb() || ((AllAppsRecyclerView.this.canScrollVertically(1) || f >= 0.0f) && (AllAppsRecyclerView.this.canScrollVertically(-1) || !z2 || this.mFirstScrollYOnScrollUp == 0))) {
                z = false;
            }
            this.mIsInOverScroll = z;
            if (z3 && !this.mIsInOverScroll) {
                reset(false);
            } else if (this.mIsInOverScroll) {
                if (Float.compare(this.mFirstDisplacement, 0.0f) == 0) {
                    this.mFirstDisplacement = f;
                }
                AllAppsRecyclerView.this.setContentTranslationY(getDampedOverScroll(f - this.mFirstDisplacement));
            }
            return this.mIsInOverScroll;
        }

        public void onDragEnd(float f, boolean z) {
            reset(this.mIsInOverScroll);
        }

        private void reset(boolean z) {
            float contentTranslationY = AllAppsRecyclerView.this.getContentTranslationY();
            if (Float.compare(contentTranslationY, 0.0f) != 0) {
                if (z) {
                    AllAppsRecyclerView.this.mSpringAnimationHandler.animateToPositionWithVelocity(0.0f, -1, -((contentTranslationY / getDampedOverScroll((float) AllAppsRecyclerView.this.getHeight())) * MAX_RELEASE_VELOCITY));
                }
                ObjectAnimator.ofFloat(AllAppsRecyclerView.this, AllAppsRecyclerView.CONTENT_TRANS_Y, new float[]{0.0f}).setDuration(100).start();
            }
            this.mIsInOverScroll = false;
            this.mFirstDisplacement = 0.0f;
            this.mFirstScrollYOnScrollUp = 0;
            this.mAlreadyScrollingUp = false;
        }

        public boolean isInOverScroll() {
            return this.mIsInOverScroll;
        }

        private float getDampedOverScroll(float f) {
            return (float) OverScroll.dampedScroll(f, AllAppsRecyclerView.this.getHeight());
        }
    }

    private class SpringMotionOnScrollListener extends OnScrollListener {
        private SpringMotionOnScrollListener() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (!AllAppsRecyclerView.this.mOverScrollHelper.isInOverScroll()) {
                if (i2 < 0 && !AllAppsRecyclerView.this.canScrollVertically(-1)) {
                    AllAppsRecyclerView.this.mSpringAnimationHandler.animateToFinalPosition(0.0f, 1);
                } else if (i2 > 0 && !AllAppsRecyclerView.this.canScrollVertically(1)) {
                    AllAppsRecyclerView.this.mSpringAnimationHandler.animateToFinalPosition(0.0f, -1);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public float getBottomFadingEdgeStrength() {
        return 0.0f;
    }

    /* access modifiers changed from: protected */
    public boolean isPaddingOffsetRequired() {
        return true;
    }

    public AllAppsRecyclerView(Context context) {
        this(context, null);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public AllAppsRecyclerView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i);
        this.mViewHeights = new SparseIntArray();
        this.mCachedScrollPositions = new SparseIntArray();
        this.mContentTranslationY = 0.0f;
        Resources resources = getResources();
        addOnItemTouchListener(this);
        this.mEmptySearchBackgroundTopOffset = resources.getDimensionPixelSize(C0622R.dimen.all_apps_empty_search_bg_top_offset);
        this.mOverScrollHelper = new OverScrollHelper();
        this.mPullDetector = new SwipeDetector(getContext(), (Listener) this.mOverScrollHelper, SwipeDetector.VERTICAL);
        this.mPullDetector.setDetectableScrollConditions(3, true);
    }

    public void setSpringAnimationHandler(SpringAnimationHandler springAnimationHandler) {
        this.mSpringAnimationHandler = springAnimationHandler;
        addOnScrollListener(new SpringMotionOnScrollListener());
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mPullDetector.onTouchEvent(motionEvent);
        if (this.mSpringAnimationHandler != null) {
            this.mSpringAnimationHandler.addMovement(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setApps(AlphabeticalAppsList alphabeticalAppsList) {
        this.mApps = alphabeticalAppsList;
        this.mFastScrollHelper = new AllAppsFastScrollHelper(this, alphabeticalAppsList);
    }

    public AlphabeticalAppsList getApps() {
        return this.mApps;
    }

    public void setNumAppsPerRow(DeviceProfile deviceProfile, int i) {
        this.mNumAppsPerRow = i;
        RecycledViewPool recycledViewPool = getRecycledViewPool();
        int ceil = (int) Math.ceil((double) (deviceProfile.availableHeightPx / deviceProfile.allAppsIconSizePx));
        recycledViewPool.setMaxRecycledViews(8, 1);
        recycledViewPool.setMaxRecycledViews(32, 1);
        recycledViewPool.setMaxRecycledViews(16, 1);
        recycledViewPool.setMaxRecycledViews(2, ceil * this.mNumAppsPerRow);
        recycledViewPool.setMaxRecycledViews(4, this.mNumAppsPerRow);
        recycledViewPool.setMaxRecycledViews(64, 1);
    }

    public void preMeasureViews(AllAppsGridAdapter allAppsGridAdapter) {
        int i = allAppsGridAdapter.onCreateViewHolder((ViewGroup) this, 2).itemView.getLayoutParams().height;
        this.mViewHeights.put(2, i);
        this.mViewHeights.put(4, i);
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, Integer.MIN_VALUE);
        int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().heightPixels, Integer.MIN_VALUE);
        putSameHeightFor(allAppsGridAdapter, makeMeasureSpec, makeMeasureSpec2, 64, 32);
        putSameHeightFor(allAppsGridAdapter, makeMeasureSpec, makeMeasureSpec2, 16);
        putSameHeightFor(allAppsGridAdapter, makeMeasureSpec, makeMeasureSpec2, 8);
    }

    private void putSameHeightFor(AllAppsGridAdapter allAppsGridAdapter, int i, int i2, int... iArr) {
        View view = allAppsGridAdapter.onCreateViewHolder((ViewGroup) this, iArr[0]).itemView;
        view.measure(i, i2);
        for (int put : iArr) {
            this.mViewHeights.put(put, view.getMeasuredHeight());
        }
    }

    public void scrollToTop() {
        if (this.mScrollbar != null) {
            this.mScrollbar.reattachThumbToScroll();
        }
        scrollToPosition(0);
    }

    public void onDraw(Canvas canvas) {
        if (this.mEmptySearchBackground != null && this.mEmptySearchBackground.getAlpha() > 0) {
            this.mEmptySearchBackground.draw(canvas);
        }
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        canvas.translate(0.0f, this.mContentTranslationY);
        super.dispatchDraw(canvas);
        canvas.translate(0.0f, -this.mContentTranslationY);
    }

    public float getContentTranslationY() {
        return this.mContentTranslationY;
    }

    public void setContentTranslationY(float f) {
        this.mContentTranslationY = f;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mEmptySearchBackground || super.verifyDrawable(drawable);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        updateEmptySearchBackgroundBounds();
    }

    public void fillInLogContainerData(View view, ItemInfo itemInfo, Target target, Target target2) {
        if (this.mApps.hasFilter()) {
            target2.containerType = 8;
        } else {
            if (view instanceof BubbleTextView) {
                int childPosition = getChildPosition((BubbleTextView) view);
                if (childPosition != -1) {
                    AdapterItem adapterItem = (AdapterItem) this.mApps.getAdapterItems().get(childPosition);
                    if (adapterItem.viewType == 4) {
                        target2.containerType = 7;
                        target.predictedRank = adapterItem.rowAppIndex;
                        return;
                    }
                }
            }
            target2.containerType = 4;
        }
    }

    public void onSearchResultsChanged() {
        scrollToTop();
        if (this.mApps.shouldShowEmptySearch()) {
            if (this.mEmptySearchBackground == null) {
                this.mEmptySearchBackground = DrawableFactory.get(getContext()).getAllAppsBackground(getContext());
                this.mEmptySearchBackground.setAlpha(0);
                this.mEmptySearchBackground.setCallback(this);
                updateEmptySearchBackgroundBounds();
            }
            this.mEmptySearchBackground.animateBgAlpha(1.0f, DragView.VIEW_ZOOM_DURATION);
        } else if (this.mEmptySearchBackground != null) {
            this.mEmptySearchBackground.setBgAlpha(0.0f);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        this.mPullDetector.onTouchEvent(motionEvent);
        boolean z = super.onInterceptTouchEvent(motionEvent) || this.mOverScrollHelper.isInOverScroll();
        if (!z && motionEvent.getAction() == 0 && this.mEmptySearchBackground != null && this.mEmptySearchBackground.getAlpha() > 0) {
            this.mEmptySearchBackground.setHotspot(motionEvent.getX(), motionEvent.getY());
        }
        return z;
    }

    public String scrollToPositionAtProgress(float f) {
        if (this.mApps.getNumAppRows() == 0) {
            return "";
        }
        stopScroll();
        List fastScrollerSections = this.mApps.getFastScrollerSections();
        FastScrollSectionInfo fastScrollSectionInfo = (FastScrollSectionInfo) fastScrollerSections.get(0);
        int i = 1;
        while (i < fastScrollerSections.size()) {
            FastScrollSectionInfo fastScrollSectionInfo2 = (FastScrollSectionInfo) fastScrollerSections.get(i);
            if (fastScrollSectionInfo2.touchFraction > f) {
                break;
            }
            i++;
            fastScrollSectionInfo = fastScrollSectionInfo2;
        }
        this.mFastScrollHelper.smoothScrollToSection(getCurrentScrollY(), getAvailableScrollHeight(), fastScrollSectionInfo);
        return fastScrollSectionInfo.sectionName;
    }

    public void onFastScrollCompleted() {
        super.onFastScrollCompleted();
        this.mFastScrollHelper.onFastScrollCompleted();
    }

    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
            public void onChanged() {
                AllAppsRecyclerView.this.mCachedScrollPositions.clear();
            }
        });
        this.mFastScrollHelper.onSetAdapter((AllAppsGridAdapter) adapter);
    }

    /* access modifiers changed from: protected */
    public int getTopPaddingOffset() {
        return -getPaddingTop();
    }

    public void onUpdateScrollbar(int i) {
        int i2;
        if (this.mApps.getAdapterItems().isEmpty() || this.mNumAppsPerRow == 0) {
            this.mScrollbar.setThumbOffsetY(-1);
            return;
        }
        int currentScrollY = getCurrentScrollY();
        if (currentScrollY < 0) {
            this.mScrollbar.setThumbOffsetY(-1);
            return;
        }
        int availableScrollBarHeight = getAvailableScrollBarHeight();
        int availableScrollHeight = getAvailableScrollHeight();
        if (availableScrollHeight <= 0) {
            this.mScrollbar.setThumbOffsetY(-1);
            return;
        }
        if (!this.mScrollbar.isThumbDetached()) {
            synchronizeScrollBarThumbOffsetToViewScroll(currentScrollY, availableScrollHeight);
        } else if (!this.mScrollbar.isDraggingThumb()) {
            int i3 = (int) ((((float) currentScrollY) / ((float) availableScrollHeight)) * ((float) availableScrollBarHeight));
            int thumbOffsetY = this.mScrollbar.getThumbOffsetY();
            int i4 = i3 - thumbOffsetY;
            if (((float) (i4 * i)) > 0.0f) {
                if (i < 0) {
                    i2 = thumbOffsetY + Math.max((int) (((float) (i * thumbOffsetY)) / ((float) i3)), i4);
                } else {
                    i2 = thumbOffsetY + Math.min((int) (((float) (i * (availableScrollBarHeight - thumbOffsetY))) / ((float) (availableScrollBarHeight - i3))), i4);
                }
                int max = Math.max(0, Math.min(availableScrollBarHeight, i2));
                this.mScrollbar.setThumbOffsetY(max);
                if (i3 == max) {
                    this.mScrollbar.reattachThumbToScroll();
                }
            } else {
                this.mScrollbar.setThumbOffsetY(thumbOffsetY);
            }
        }
    }

    public boolean supportsFastScrolling() {
        return !this.mApps.hasFilter();
    }

    public int getCurrentScrollY() {
        if (this.mApps.getAdapterItems().isEmpty() || this.mNumAppsPerRow == 0 || getChildCount() == 0) {
            return -1;
        }
        View childAt = getChildAt(0);
        int childPosition = getChildPosition(childAt);
        if (childPosition == -1) {
            return -1;
        }
        return getPaddingTop() + getCurrentScrollY(childPosition, getLayoutManager().getDecoratedTop(childAt));
    }

    public int getCurrentScrollY(int i, int i2) {
        List adapterItems = this.mApps.getAdapterItems();
        AdapterItem adapterItem = i < adapterItems.size() ? (AdapterItem) adapterItems.get(i) : null;
        int i3 = this.mCachedScrollPositions.get(i, -1);
        if (i3 < 0) {
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                AdapterItem adapterItem2 = (AdapterItem) adapterItems.get(i5);
                if (AllAppsGridAdapter.isIconViewType(adapterItem2.viewType)) {
                    if (adapterItem != null && adapterItem.viewType == adapterItem2.viewType && adapterItem.rowIndex == adapterItem2.rowIndex) {
                        break;
                    } else if (adapterItem2.rowAppIndex == 0) {
                        i4 += this.mViewHeights.get(adapterItem2.viewType, 0);
                    }
                } else {
                    i4 += this.mViewHeights.get(adapterItem2.viewType, 0);
                }
            }
            this.mCachedScrollPositions.put(i, i4);
            i3 = i4;
        }
        return i3 - i2;
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollHeight() {
        return ((getPaddingTop() + getCurrentScrollY(this.mApps.getAdapterItems().size(), 0)) - getHeight()) + getPaddingBottom();
    }

    private void updateEmptySearchBackgroundBounds() {
        if (this.mEmptySearchBackground != null) {
            int measuredWidth = (getMeasuredWidth() - this.mEmptySearchBackground.getIntrinsicWidth()) / 2;
            int i = this.mEmptySearchBackgroundTopOffset;
            this.mEmptySearchBackground.setBounds(measuredWidth, i, this.mEmptySearchBackground.getIntrinsicWidth() + measuredWidth, this.mEmptySearchBackground.getIntrinsicHeight() + i);
        }
    }
}
