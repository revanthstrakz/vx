package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;
import com.android.launcher3.accessibility.DragViewStateAnnouncer;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragLayer.LayoutParams;
import com.android.launcher3.util.FocusLogic;
import com.android.launcher3.util.TouchController;

public class AppWidgetResizeFrame extends FrameLayout implements OnKeyListener, TouchController {
    private static final float DIMMED_HANDLE_ALPHA = 0.0f;
    private static final int HANDLE_COUNT = 4;
    private static final int INDEX_BOTTOM = 3;
    private static final int INDEX_LEFT = 0;
    private static final int INDEX_RIGHT = 2;
    private static final int INDEX_TOP = 1;
    private static final float RESIZE_THRESHOLD = 0.66f;
    private static final int SNAP_DURATION = 150;
    private static Point[] sCellSize;
    private static final Rect sTmpRect = new Rect();
    private final int mBackgroundPadding;
    private final IntRange mBaselineX;
    private final IntRange mBaselineY;
    private boolean mBottomBorderActive;
    private int mBottomTouchRegionAdjustment;
    private CellLayout mCellLayout;
    private int mDeltaX;
    private int mDeltaXAddOn;
    private final IntRange mDeltaXRange;
    private int mDeltaY;
    private int mDeltaYAddOn;
    private final IntRange mDeltaYRange;
    private final int[] mDirectionVector;
    private final View[] mDragHandles;
    private DragLayer mDragLayer;
    private final int[] mLastDirectionVector;
    private final Launcher mLauncher;
    private boolean mLeftBorderActive;
    private int mMinHSpan;
    private int mMinVSpan;
    private int mResizeMode;
    private boolean mRightBorderActive;
    private int mRunningHInc;
    private int mRunningVInc;
    private final DragViewStateAnnouncer mStateAnnouncer;
    private final IntRange mTempRange1;
    private final IntRange mTempRange2;
    private boolean mTopBorderActive;
    private int mTopTouchRegionAdjustment;
    private final int mTouchTargetWidth;
    private Rect mWidgetPadding;
    private LauncherAppWidgetHostView mWidgetView;
    private int mXDown;
    private int mYDown;

    private static class IntRange {
        public int end;
        public int start;

        private IntRange() {
        }

        public int clamp(int i) {
            return Utilities.boundToRange(i, this.start, this.end);
        }

        public void set(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public int size() {
            return this.end - this.start;
        }

        public void applyDelta(boolean z, boolean z2, int i, IntRange intRange) {
            intRange.start = z ? this.start + i : this.start;
            intRange.end = z2 ? this.end + i : this.end;
        }

        public int applyDeltaAndBound(boolean z, boolean z2, int i, int i2, int i3, IntRange intRange) {
            int size;
            int size2;
            applyDelta(z, z2, i, intRange);
            if (intRange.start < 0) {
                intRange.start = 0;
            }
            if (intRange.end > i3) {
                intRange.end = i3;
            }
            if (intRange.size() < i2) {
                if (z) {
                    intRange.start = intRange.end - i2;
                } else if (z2) {
                    intRange.end = intRange.start + i2;
                }
            }
            if (z2) {
                size = intRange.size();
                size2 = size();
            } else {
                size = size();
                size2 = intRange.size();
            }
            return size - size2;
        }
    }

    public AppWidgetResizeFrame(Context context) {
        this(context, null);
    }

    public AppWidgetResizeFrame(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppWidgetResizeFrame(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDragHandles = new View[4];
        this.mDirectionVector = new int[2];
        this.mLastDirectionVector = new int[2];
        this.mTempRange1 = new IntRange();
        this.mTempRange2 = new IntRange();
        this.mDeltaXRange = new IntRange();
        this.mBaselineX = new IntRange();
        this.mDeltaYRange = new IntRange();
        this.mBaselineY = new IntRange();
        this.mTopTouchRegionAdjustment = 0;
        this.mBottomTouchRegionAdjustment = 0;
        this.mLauncher = Launcher.getLauncher(context);
        this.mStateAnnouncer = DragViewStateAnnouncer.createFor(this);
        this.mBackgroundPadding = getResources().getDimensionPixelSize(C0622R.dimen.resize_frame_background_padding);
        this.mTouchTargetWidth = this.mBackgroundPadding * 2;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < 4; i++) {
            this.mDragHandles[i] = getChildAt(i);
        }
    }

    public void setupForWidget(LauncherAppWidgetHostView launcherAppWidgetHostView, CellLayout cellLayout, DragLayer dragLayer) {
        this.mCellLayout = cellLayout;
        this.mWidgetView = launcherAppWidgetHostView;
        LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = (LauncherAppWidgetProviderInfo) launcherAppWidgetHostView.getAppWidgetInfo();
        this.mResizeMode = launcherAppWidgetProviderInfo.resizeMode;
        this.mDragLayer = dragLayer;
        this.mMinHSpan = launcherAppWidgetProviderInfo.minSpanX;
        this.mMinVSpan = launcherAppWidgetProviderInfo.minSpanY;
        if (!launcherAppWidgetProviderInfo.isCustomWidget) {
            this.mWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(getContext(), launcherAppWidgetHostView.getAppWidgetInfo().provider, null);
        } else {
            int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(C0622R.dimen.default_widget_padding);
            this.mWidgetPadding = new Rect(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        }
        if (launcherAppWidgetProviderInfo.minSpanX > 1) {
            this.mResizeMode |= 1;
        }
        if (launcherAppWidgetProviderInfo.minSpanY > 1) {
            this.mResizeMode |= 2;
        }
        if (this.mResizeMode == 1) {
            this.mDragHandles[1].setVisibility(8);
            this.mDragHandles[3].setVisibility(8);
        } else if (this.mResizeMode == 2) {
            this.mDragHandles[0].setVisibility(8);
            this.mDragHandles[2].setVisibility(8);
        }
        this.mCellLayout.markCellsAsUnoccupiedForView(this.mWidgetView);
        setOnKeyListener(this);
    }

    public boolean beginResizeIfPointInRegion(int i, int i2) {
        boolean z = (this.mResizeMode & 1) != 0;
        boolean z2 = (this.mResizeMode & 2) != 0;
        this.mLeftBorderActive = i < this.mTouchTargetWidth && z;
        this.mRightBorderActive = i > getWidth() - this.mTouchTargetWidth && z;
        this.mTopBorderActive = i2 < this.mTouchTargetWidth + this.mTopTouchRegionAdjustment && z2;
        this.mBottomBorderActive = i2 > (getHeight() - this.mTouchTargetWidth) + this.mBottomTouchRegionAdjustment && z2;
        boolean z3 = this.mLeftBorderActive || this.mRightBorderActive || this.mTopBorderActive || this.mBottomBorderActive;
        if (z3) {
            float f = 0.0f;
            this.mDragHandles[0].setAlpha(this.mLeftBorderActive ? 1.0f : 0.0f);
            this.mDragHandles[2].setAlpha(this.mRightBorderActive ? 1.0f : 0.0f);
            this.mDragHandles[1].setAlpha(this.mTopBorderActive ? 1.0f : 0.0f);
            View view = this.mDragHandles[3];
            if (this.mBottomBorderActive) {
                f = 1.0f;
            }
            view.setAlpha(f);
        }
        if (this.mLeftBorderActive) {
            this.mDeltaXRange.set(-getLeft(), getWidth() - (this.mTouchTargetWidth * 2));
        } else if (this.mRightBorderActive) {
            this.mDeltaXRange.set((this.mTouchTargetWidth * 2) - getWidth(), this.mDragLayer.getWidth() - getRight());
        } else {
            this.mDeltaXRange.set(0, 0);
        }
        this.mBaselineX.set(getLeft(), getRight());
        if (this.mTopBorderActive) {
            this.mDeltaYRange.set(-getTop(), getHeight() - (this.mTouchTargetWidth * 2));
        } else if (this.mBottomBorderActive) {
            this.mDeltaYRange.set((this.mTouchTargetWidth * 2) - getHeight(), this.mDragLayer.getHeight() - getBottom());
        } else {
            this.mDeltaYRange.set(0, 0);
        }
        this.mBaselineY.set(getTop(), getBottom());
        return z3;
    }

    public void visualizeResizeForDelta(int i, int i2) {
        this.mDeltaX = this.mDeltaXRange.clamp(i);
        this.mDeltaY = this.mDeltaYRange.clamp(i2);
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        this.mDeltaX = this.mDeltaXRange.clamp(i);
        this.mBaselineX.applyDelta(this.mLeftBorderActive, this.mRightBorderActive, this.mDeltaX, this.mTempRange1);
        layoutParams.f61x = this.mTempRange1.start;
        layoutParams.width = this.mTempRange1.size();
        this.mDeltaY = this.mDeltaYRange.clamp(i2);
        this.mBaselineY.applyDelta(this.mTopBorderActive, this.mBottomBorderActive, this.mDeltaY, this.mTempRange1);
        layoutParams.f62y = this.mTempRange1.start;
        layoutParams.height = this.mTempRange1.size();
        resizeWidgetIfNeeded(false);
        getSnappedRectRelativeToDragLayer(sTmpRect);
        if (this.mLeftBorderActive) {
            layoutParams.width = (sTmpRect.width() + sTmpRect.left) - layoutParams.f61x;
        }
        if (this.mTopBorderActive) {
            layoutParams.height = (sTmpRect.height() + sTmpRect.top) - layoutParams.f62y;
        }
        if (this.mRightBorderActive) {
            layoutParams.f61x = sTmpRect.left;
        }
        if (this.mBottomBorderActive) {
            layoutParams.f62y = sTmpRect.top;
        }
        requestLayout();
    }

    private static int getSpanIncrement(float f) {
        if (Math.abs(f) > RESIZE_THRESHOLD) {
            return Math.round(f);
        }
        return 0;
    }

    private void resizeWidgetIfNeeded(boolean z) {
        float cellHeight = (float) this.mCellLayout.getCellHeight();
        int spanIncrement = getSpanIncrement((((float) (this.mDeltaX + this.mDeltaXAddOn)) / ((float) this.mCellLayout.getCellWidth())) - ((float) this.mRunningHInc));
        int spanIncrement2 = getSpanIncrement((((float) (this.mDeltaY + this.mDeltaYAddOn)) / cellHeight) - ((float) this.mRunningVInc));
        if (z || spanIncrement != 0 || spanIncrement2 != 0) {
            this.mDirectionVector[0] = 0;
            this.mDirectionVector[1] = 0;
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) this.mWidgetView.getLayoutParams();
            int i = layoutParams.cellHSpan;
            int i2 = layoutParams.cellVSpan;
            int i3 = layoutParams.useTmpCoords ? layoutParams.tmpCellX : layoutParams.cellX;
            int i4 = layoutParams.useTmpCoords ? layoutParams.tmpCellY : layoutParams.cellY;
            this.mTempRange1.set(i3, i + i3);
            int applyDeltaAndBound = this.mTempRange1.applyDeltaAndBound(this.mLeftBorderActive, this.mRightBorderActive, spanIncrement, this.mMinHSpan, this.mCellLayout.getCountX(), this.mTempRange2);
            int i5 = this.mTempRange2.start;
            int size = this.mTempRange2.size();
            int i6 = -1;
            if (applyDeltaAndBound != 0) {
                this.mDirectionVector[0] = this.mLeftBorderActive ? -1 : 1;
            }
            this.mTempRange1.set(i4, i2 + i4);
            int applyDeltaAndBound2 = this.mTempRange1.applyDeltaAndBound(this.mTopBorderActive, this.mBottomBorderActive, spanIncrement2, this.mMinVSpan, this.mCellLayout.getCountY(), this.mTempRange2);
            int i7 = this.mTempRange2.start;
            int size2 = this.mTempRange2.size();
            if (applyDeltaAndBound2 != 0) {
                int[] iArr = this.mDirectionVector;
                if (!this.mTopBorderActive) {
                    i6 = 1;
                }
                iArr[1] = i6;
            }
            if (z || applyDeltaAndBound2 != 0 || applyDeltaAndBound != 0) {
                if (z) {
                    this.mDirectionVector[0] = this.mLastDirectionVector[0];
                    this.mDirectionVector[1] = this.mLastDirectionVector[1];
                } else {
                    this.mLastDirectionVector[0] = this.mDirectionVector[0];
                    this.mLastDirectionVector[1] = this.mDirectionVector[1];
                }
                CellLayout.LayoutParams layoutParams2 = layoutParams;
                int i8 = size2;
                if (this.mCellLayout.createAreaForResize(i5, i7, size, size2, this.mWidgetView, this.mDirectionVector, z)) {
                    if (!(this.mStateAnnouncer == null || (layoutParams2.cellHSpan == size && layoutParams2.cellVSpan == i8))) {
                        this.mStateAnnouncer.announce(this.mLauncher.getString(C0622R.string.widget_resized, new Object[]{Integer.valueOf(size), Integer.valueOf(i8)}));
                    }
                    layoutParams2.tmpCellX = i5;
                    layoutParams2.tmpCellY = i7;
                    layoutParams2.cellHSpan = size;
                    layoutParams2.cellVSpan = i8;
                    this.mRunningVInc += applyDeltaAndBound2;
                    this.mRunningHInc += applyDeltaAndBound;
                    int i9 = i8;
                    if (!z) {
                        updateWidgetSizeRanges(this.mWidgetView, this.mLauncher, size, i9);
                    }
                }
                this.mWidgetView.requestLayout();
            }
        }
    }

    static void updateWidgetSizeRanges(AppWidgetHostView appWidgetHostView, Launcher launcher, int i, int i2) {
        getWidgetSizeRanges(launcher, i, i2, sTmpRect);
        appWidgetHostView.updateAppWidgetSize(null, sTmpRect.left, sTmpRect.top, sTmpRect.right, sTmpRect.bottom);
    }

    public static Rect getWidgetSizeRanges(Context context, int i, int i2, Rect rect) {
        if (sCellSize == null) {
            InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
            sCellSize = new Point[2];
            sCellSize[0] = idp.landscapeProfile.getCellSize();
            sCellSize[1] = idp.portraitProfile.getCellSize();
        }
        if (rect == null) {
            rect = new Rect();
        }
        float f = context.getResources().getDisplayMetrics().density;
        int i3 = (int) (((float) (sCellSize[0].y * i2)) / f);
        rect.set((int) (((float) (i * sCellSize[1].x)) / f), i3, (int) (((float) (sCellSize[0].x * i)) / f), (int) (((float) (i2 * sCellSize[1].y)) / f));
        return rect;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resizeWidgetIfNeeded(true);
    }

    private void onTouchUp() {
        int cellWidth = this.mCellLayout.getCellWidth();
        int cellHeight = this.mCellLayout.getCellHeight();
        this.mDeltaXAddOn = this.mRunningHInc * cellWidth;
        this.mDeltaYAddOn = this.mRunningVInc * cellHeight;
        this.mDeltaX = 0;
        this.mDeltaY = 0;
        post(new Runnable() {
            public void run() {
                AppWidgetResizeFrame.this.snapToWidget(true);
            }
        });
    }

    private void getSnappedRectRelativeToDragLayer(Rect rect) {
        float scaleToFit = this.mWidgetView.getScaleToFit();
        this.mDragLayer.getViewRectRelativeToSelf(this.mWidgetView, rect);
        int width = (this.mBackgroundPadding * 2) + ((int) (((float) ((rect.width() - this.mWidgetPadding.left) - this.mWidgetPadding.right)) * scaleToFit));
        int height = (this.mBackgroundPadding * 2) + ((int) (((float) ((rect.height() - this.mWidgetPadding.top) - this.mWidgetPadding.bottom)) * scaleToFit));
        int i = (int) (((float) (rect.left - this.mBackgroundPadding)) + (((float) this.mWidgetPadding.left) * scaleToFit));
        int i2 = (int) (((float) (rect.top - this.mBackgroundPadding)) + (scaleToFit * ((float) this.mWidgetPadding.top)));
        rect.left = i;
        rect.top = i2;
        rect.right = rect.left + width;
        rect.bottom = rect.top + height;
    }

    public void snapToWidget(boolean z) {
        getSnappedRectRelativeToDragLayer(sTmpRect);
        int width = sTmpRect.width();
        int height = sTmpRect.height();
        int i = sTmpRect.left;
        int i2 = sTmpRect.top;
        if (i2 < 0) {
            this.mTopTouchRegionAdjustment = -i2;
        } else {
            this.mTopTouchRegionAdjustment = 0;
        }
        int i3 = i2 + height;
        if (i3 > this.mDragLayer.getHeight()) {
            this.mBottomTouchRegionAdjustment = -(i3 - this.mDragLayer.getHeight());
        } else {
            this.mBottomTouchRegionAdjustment = 0;
        }
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (!z) {
            layoutParams.width = width;
            layoutParams.height = height;
            layoutParams.f61x = i;
            layoutParams.f62y = i2;
            for (int i4 = 0; i4 < 4; i4++) {
                this.mDragHandles[i4].setAlpha(1.0f);
            }
            requestLayout();
        } else {
            ObjectAnimator ofPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(layoutParams, this, PropertyValuesHolder.ofInt("width", new int[]{layoutParams.width, width}), PropertyValuesHolder.ofInt("height", new int[]{layoutParams.height, height}), PropertyValuesHolder.ofInt("x", new int[]{layoutParams.f61x, i}), PropertyValuesHolder.ofInt("y", new int[]{layoutParams.f62y, i2}));
            ofPropertyValuesHolder.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AppWidgetResizeFrame.this.requestLayout();
                }
            });
            AnimatorSet createAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            createAnimatorSet.play(ofPropertyValuesHolder);
            for (int i5 = 0; i5 < 4; i5++) {
                createAnimatorSet.play(LauncherAnimUtils.ofFloat(this.mDragHandles[i5], ALPHA, 1.0f));
            }
            createAnimatorSet.setDuration(150);
            createAnimatorSet.start();
        }
        setFocusableInTouchMode(true);
        requestFocus();
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (!FocusLogic.shouldConsume(i)) {
            return false;
        }
        this.mDragLayer.clearResizeFrame();
        this.mWidgetView.requestFocus();
        return true;
    }

    private boolean handleTouchDown(MotionEvent motionEvent) {
        Rect rect = new Rect();
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        getHitRect(rect);
        if (!rect.contains(x, y) || !beginResizeIfPointInRegion(x - getLeft(), y - getTop())) {
            return false;
        }
        this.mXDown = x;
        this.mYDown = y;
        return true;
    }

    public boolean onControllerTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        switch (motionEvent.getAction()) {
            case 0:
                return handleTouchDown(motionEvent);
            case 1:
            case 3:
                visualizeResizeForDelta(x - this.mXDown, y - this.mYDown);
                onTouchUp();
                this.mYDown = 0;
                this.mXDown = 0;
                break;
            case 2:
                visualizeResizeForDelta(x - this.mXDown, y - this.mYDown);
                break;
        }
        return true;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent motionEvent) {
        return motionEvent.getAction() == 0 && handleTouchDown(motionEvent);
    }
}
