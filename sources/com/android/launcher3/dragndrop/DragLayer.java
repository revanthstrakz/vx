package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.support.p001v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.C0622R;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTargetBar;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.InsettableFrameLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.PinchToOverviewListener;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.dynamicui.WallpaperColorInfo;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TouchController;
import com.android.launcher3.widget.WidgetsBottomSheet;
import java.util.ArrayList;

public class DragLayer extends InsettableFrameLayout {
    public static final int ANIMATION_END_DISAPPEAR = 0;
    public static final int ANIMATION_END_REMAIN_VISIBLE = 2;
    private TouchController mActiveController;
    private AllAppsTransitionController mAllAppsController;
    View mAnchorView = null;
    int mAnchorViewInitialScrollX = 0;
    private float mBackgroundAlpha = 0.0f;
    private int mChildCountOnLastUpdate = -1;
    private final TimeInterpolator mCubicEaseOutInterpolator = new DecelerateInterpolator(1.5f);
    private AppWidgetResizeFrame mCurrentResizeFrame;
    DragController mDragController;
    private ValueAnimator mDropAnim = null;
    DragView mDropView = null;
    private final ViewGroupFocusHelper mFocusIndicatorHelper;
    private final Rect mHighlightRect = new Rect();
    private final Rect mHitRect = new Rect();
    private boolean mHoverPointClosesFolder = false;
    private final boolean mIsRtl;
    private Launcher mLauncher;
    private PinchToOverviewListener mPinchListener = null;
    private final Rect mScrollChildPosition = new Rect();
    private final int[] mTmpXY = new int[2];
    private int mTopViewIndex;
    private TouchCompleteListener mTouchCompleteListener;
    private final WallpaperColorInfo mWallpaperColorInfo;

    public static class LayoutParams extends com.android.launcher3.InsettableFrameLayout.LayoutParams {
        public boolean customPosition = false;

        /* renamed from: x */
        public int f61x;

        /* renamed from: y */
        public int f62y;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public void setWidth(int i) {
            this.width = i;
        }

        public int getWidth() {
            return this.width;
        }

        public void setHeight(int i) {
            this.height = i;
        }

        public int getHeight() {
            return this.height;
        }

        public void setX(int i) {
            this.f61x = i;
        }

        public int getX() {
            return this.f61x;
        }

        public void setY(int i) {
            this.f62y = i;
        }

        public int getY() {
            return this.f62y;
        }
    }

    public interface TouchCompleteListener {
        void onTouchComplete();
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        return false;
    }

    public DragLayer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setMotionEventSplittingEnabled(false);
        setChildrenDrawingOrderEnabled(true);
        this.mIsRtl = Utilities.isRtl(getResources());
        this.mFocusIndicatorHelper = new ViewGroupFocusHelper(this);
        this.mWallpaperColorInfo = WallpaperColorInfo.getInstance(getContext());
    }

    public void setup(Launcher launcher, DragController dragController, AllAppsTransitionController allAppsTransitionController) {
        this.mLauncher = launcher;
        this.mDragController = dragController;
        this.mAllAppsController = allAppsTransitionController;
        onAccessibilityStateChanged(((AccessibilityManager) this.mLauncher.getSystemService("accessibility")).isEnabled());
    }

    public ViewGroupFocusHelper getFocusIndicatorHelper() {
        return this.mFocusIndicatorHelper;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return this.mDragController.dispatchKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent);
    }

    public void onAccessibilityStateChanged(boolean z) {
        this.mPinchListener = (FeatureFlags.LAUNCHER3_DISABLE_PINCH_TO_OVERVIEW || z) ? null : new PinchToOverviewListener(this.mLauncher);
    }

    public boolean isEventOverPageIndicator(MotionEvent motionEvent) {
        return isEventOverView(this.mLauncher.getWorkspace().getPageIndicator(), motionEvent);
    }

    public boolean isEventOverHotseat(MotionEvent motionEvent) {
        return isEventOverView(this.mLauncher.getHotseat(), motionEvent);
    }

    private boolean isEventOverFolder(Folder folder, MotionEvent motionEvent) {
        return isEventOverView(folder, motionEvent);
    }

    private boolean isEventOverDropTargetBar(MotionEvent motionEvent) {
        return isEventOverView(this.mLauncher.getDropTargetBar(), motionEvent);
    }

    public boolean isEventOverView(View view, MotionEvent motionEvent) {
        getDescendantRectRelativeToSelf(view, this.mHitRect);
        return this.mHitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY());
    }

    private boolean handleTouchDown(MotionEvent motionEvent, boolean z) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        boolean z2 = false;
        if (topOpenView != null && z) {
            ExtendedEditText activeTextView = topOpenView.getActiveTextView();
            if (activeTextView != null) {
                if (!isEventOverView(activeTextView, motionEvent)) {
                    activeTextView.dispatchBackKey();
                    return true;
                }
            } else if (!isEventOverView(topOpenView, motionEvent)) {
                if (!isInAccessibleDrag()) {
                    this.mLauncher.getUserEventDispatcher().logActionTapOutside(LoggerUtils.newContainerTarget(topOpenView.getLogContainerType()));
                    topOpenView.close(true);
                    View extendedTouchView = topOpenView.getExtendedTouchView();
                    if (extendedTouchView == null || !isEventOverView(extendedTouchView, motionEvent)) {
                        z2 = true;
                    }
                    return z2;
                } else if (!isEventOverDropTargetBar(motionEvent)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            this.mAllAppsController.cancelDiscoveryAnimation();
            if (handleTouchDown(motionEvent, true)) {
                return true;
            }
        } else if (action == 1 || action == 3) {
            if (this.mTouchCompleteListener != null) {
                this.mTouchCompleteListener.onTouchComplete();
            }
            this.mTouchCompleteListener = null;
        }
        this.mActiveController = null;
        if (this.mCurrentResizeFrame != null && this.mCurrentResizeFrame.onControllerInterceptTouchEvent(motionEvent)) {
            this.mActiveController = this.mCurrentResizeFrame;
            return true;
        } else if (clearResizeFrame()) {
            return true;
        } else {
            if (this.mDragController.onControllerInterceptTouchEvent(motionEvent)) {
                this.mActiveController = this.mDragController;
                return true;
            } else if (this.mAllAppsController.onControllerInterceptTouchEvent(motionEvent)) {
                this.mActiveController = this.mAllAppsController;
                return true;
            } else {
                WidgetsBottomSheet open = WidgetsBottomSheet.getOpen(this.mLauncher);
                if (open != null && open.onControllerInterceptTouchEvent(motionEvent)) {
                    this.mActiveController = open;
                    return true;
                } else if (this.mPinchListener == null || !this.mPinchListener.onControllerInterceptTouchEvent(motionEvent)) {
                    return false;
                } else {
                    this.mActiveController = this.mPinchListener;
                    return true;
                }
            }
        }
    }

    public boolean onInterceptHoverEvent(MotionEvent motionEvent) {
        if (this.mLauncher == null || this.mLauncher.getWorkspace() == null) {
            return false;
        }
        Folder open = Folder.getOpen(this.mLauncher);
        if (open != null && ((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            int action = motionEvent.getAction();
            if (action == 7) {
                boolean z = isEventOverFolder(open, motionEvent) || (isInAccessibleDrag() && isEventOverDropTargetBar(motionEvent));
                if (!z && !this.mHoverPointClosesFolder) {
                    sendTapOutsideFolderAccessibilityEvent(open.isEditingName());
                    this.mHoverPointClosesFolder = true;
                    return true;
                } else if (!z) {
                    return true;
                } else {
                    this.mHoverPointClosesFolder = false;
                }
            } else if (action == 9) {
                if (!(isEventOverFolder(open, motionEvent) || (isInAccessibleDrag() && isEventOverDropTargetBar(motionEvent)))) {
                    sendTapOutsideFolderAccessibilityEvent(open.isEditingName());
                    this.mHoverPointClosesFolder = true;
                    return true;
                }
                this.mHoverPointClosesFolder = false;
            }
        }
        return false;
    }

    private void sendTapOutsideFolderAccessibilityEvent(boolean z) {
        Utilities.sendCustomAccessibilityEvent(this, 8, getContext().getString(z ? C0622R.string.folder_tap_to_rename : C0622R.string.folder_tap_to_close));
    }

    private boolean isInAccessibleDrag() {
        return this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag();
    }

    public boolean onRequestSendAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView == null) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        if (view == topOpenView) {
            return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
        }
        if (!isInAccessibleDrag() || !(view instanceof DropTargetBar)) {
            return false;
        }
        return super.onRequestSendAccessibilityEvent(view, accessibilityEvent);
    }

    public void addChildrenForAccessibility(ArrayList<View> arrayList) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView != null) {
            arrayList.add(topOpenView);
            if (isInAccessibleDrag()) {
                arrayList.add(this.mLauncher.getDropTargetBar());
                return;
            }
            return;
        }
        super.addChildrenForAccessibility(arrayList);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            if (handleTouchDown(motionEvent, false)) {
                return true;
            }
        } else if (action == 1 || action == 3) {
            if (this.mTouchCompleteListener != null) {
                this.mTouchCompleteListener.onTouchComplete();
            }
            this.mTouchCompleteListener = null;
        }
        if (this.mActiveController != null) {
            return this.mActiveController.onControllerTouchEvent(motionEvent);
        }
        return false;
    }

    public float getDescendantRectRelativeToSelf(View view, Rect rect) {
        this.mTmpXY[0] = 0;
        this.mTmpXY[1] = 0;
        float descendantCoordRelativeToSelf = getDescendantCoordRelativeToSelf(view, this.mTmpXY);
        rect.set(this.mTmpXY[0], this.mTmpXY[1], (int) (((float) this.mTmpXY[0]) + (((float) view.getMeasuredWidth()) * descendantCoordRelativeToSelf)), (int) (((float) this.mTmpXY[1]) + (((float) view.getMeasuredHeight()) * descendantCoordRelativeToSelf)));
        return descendantCoordRelativeToSelf;
    }

    public float getLocationInDragLayer(View view, int[] iArr) {
        iArr[0] = 0;
        iArr[1] = 0;
        return getDescendantCoordRelativeToSelf(view, iArr);
    }

    public float getDescendantCoordRelativeToSelf(View view, int[] iArr) {
        return getDescendantCoordRelativeToSelf(view, iArr, false);
    }

    public float getDescendantCoordRelativeToSelf(View view, int[] iArr, boolean z) {
        return Utilities.getDescendantCoordRelativeToAncestor(view, this, iArr, z);
    }

    public void mapCoordInSelfToDescendant(View view, int[] iArr) {
        Utilities.mapCoordInSelfToDescendant(view, this, iArr);
    }

    public void getViewRectRelativeToSelf(View view, Rect rect) {
        int[] iArr = new int[2];
        getLocationInWindow(iArr);
        int i = iArr[0];
        int i2 = iArr[1];
        view.getLocationInWindow(iArr);
        int i3 = iArr[0] - i;
        int i4 = iArr[1] - i2;
        rect.set(i3, i4, view.getMeasuredWidth() + i3, view.getMeasuredHeight() + i4);
    }

    public boolean dispatchUnhandledMove(View view, int i) {
        if ((AbstractFloatingView.getTopOpenView(this.mLauncher) != null) || this.mDragController.dispatchUnhandledMove(view, i)) {
            return true;
        }
        return false;
    }

    public void setInsets(Rect rect) {
        Drawable drawable;
        super.setInsets(rect);
        if (rect.top == 0) {
            drawable = null;
        } else {
            drawable = Themes.getAttrDrawable(getContext(), C0622R.attr.workspaceStatusBarScrim);
        }
        setBackground(drawable);
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            android.widget.FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) childAt.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                LayoutParams layoutParams2 = (LayoutParams) layoutParams;
                if (layoutParams2.customPosition) {
                    childAt.layout(layoutParams2.f61x, layoutParams2.f62y, layoutParams2.f61x + layoutParams2.width, layoutParams2.f62y + layoutParams2.height);
                }
            }
        }
    }

    public boolean clearResizeFrame() {
        if (this.mCurrentResizeFrame == null) {
            return false;
        }
        removeView(this.mCurrentResizeFrame);
        this.mCurrentResizeFrame = null;
        return true;
    }

    public void addResizeFrame(LauncherAppWidgetHostView launcherAppWidgetHostView, CellLayout cellLayout) {
        clearResizeFrame();
        this.mCurrentResizeFrame = (AppWidgetResizeFrame) LayoutInflater.from(this.mLauncher).inflate(C0622R.layout.app_widget_resize_frame, this, false);
        this.mCurrentResizeFrame.setupForWidget(launcherAppWidgetHostView, cellLayout, this);
        ((LayoutParams) this.mCurrentResizeFrame.getLayoutParams()).customPosition = true;
        addView(this.mCurrentResizeFrame);
        this.mCurrentResizeFrame.snapToWidget(false);
    }

    public void animateViewIntoPosition(DragView dragView, int[] iArr, float f, float f2, float f3, int i, Runnable runnable, int i2) {
        Rect rect = new Rect();
        DragView dragView2 = dragView;
        getViewRectRelativeToSelf(dragView2, rect);
        animateViewIntoPosition(dragView2, rect.left, rect.top, iArr[0], iArr[1], f, 1.0f, 1.0f, f2, f3, runnable, i, i2, null);
    }

    public void animateViewIntoPosition(DragView dragView, View view, Runnable runnable, View view2) {
        animateViewIntoPosition(dragView, view, -1, runnable, view2);
    }

    public void animateViewIntoPosition(DragView dragView, View view, int i, Runnable runnable, View view2) {
        float f;
        int i2;
        int i3;
        int round;
        int round2;
        final View view3 = view;
        com.android.launcher3.CellLayout.LayoutParams layoutParams = (com.android.launcher3.CellLayout.LayoutParams) view.getLayoutParams();
        ((ShortcutAndWidgetContainer) view.getParent()).measureChild(view3);
        Rect rect = new Rect();
        getViewRectRelativeToSelf(dragView, rect);
        float scaleX = view.getScaleX();
        float f2 = 1.0f - scaleX;
        int[] iArr = {layoutParams.f46x + ((int) ((((float) view.getMeasuredWidth()) * f2) / 2.0f)), layoutParams.f47y + ((int) ((((float) view.getMeasuredHeight()) * f2) / 2.0f))};
        float descendantCoordRelativeToSelf = getDescendantCoordRelativeToSelf((View) view.getParent(), iArr) * scaleX;
        int i4 = iArr[0];
        int i5 = iArr[1];
        if (view3 instanceof TextView) {
            float intrinsicIconScaleFactor = descendantCoordRelativeToSelf / dragView.getIntrinsicIconScaleFactor();
            i2 = (int) (((float) (i5 + Math.round(((float) ((TextView) view3).getPaddingTop()) * intrinsicIconScaleFactor))) - ((((float) dragView.getMeasuredHeight()) * (1.0f - intrinsicIconScaleFactor)) / 2.0f));
            if (dragView.getDragVisualizeOffset() != null) {
                i2 -= Math.round(((float) dragView.getDragVisualizeOffset().y) * intrinsicIconScaleFactor);
            }
            i3 = i4 - ((dragView.getMeasuredWidth() - Math.round(descendantCoordRelativeToSelf * ((float) view.getMeasuredWidth()))) / 2);
            f = intrinsicIconScaleFactor;
        } else {
            if (view3 instanceof FolderIcon) {
                round = (int) (((float) ((int) (((float) (i5 + Math.round(((float) (view.getPaddingTop() - dragView.getDragRegionTop())) * descendantCoordRelativeToSelf))) - ((((float) dragView.getBlurSizeOutline()) * descendantCoordRelativeToSelf) / 2.0f)))) - (((1.0f - descendantCoordRelativeToSelf) * ((float) dragView.getMeasuredHeight())) / 2.0f));
                round2 = i4 - ((dragView.getMeasuredWidth() - Math.round(((float) view.getMeasuredWidth()) * descendantCoordRelativeToSelf)) / 2);
            } else {
                round = i5 - (Math.round(((float) (dragView.getHeight() - view.getMeasuredHeight())) * descendantCoordRelativeToSelf) / 2);
                round2 = i4 - (Math.round(((float) (dragView.getMeasuredWidth() - view.getMeasuredWidth())) * descendantCoordRelativeToSelf) / 2);
            }
            f = descendantCoordRelativeToSelf;
            i3 = round2;
        }
        int i6 = rect.left;
        int i7 = rect.top;
        view3.setVisibility(4);
        final Runnable runnable2 = runnable;
        animateViewIntoPosition(dragView, i6, i7, i3, i2, 1.0f, 1.0f, 1.0f, f, f, new Runnable() {
            public void run() {
                view3.setVisibility(0);
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        }, 0, i, view2);
    }

    public void animateViewIntoPosition(DragView dragView, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4, float f5, Runnable runnable, int i5, int i6, View view) {
        int i7 = i;
        int i8 = i2;
        int i9 = i3;
        int i10 = i4;
        animateView(dragView, new Rect(i7, i8, dragView.getMeasuredWidth() + i7, dragView.getMeasuredHeight() + i8), new Rect(i9, i10, dragView.getMeasuredWidth() + i9, dragView.getMeasuredHeight() + i10), f, f2, f3, f4, f5, i6, null, null, runnable, i5, view);
    }

    public void animateView(DragView dragView, Rect rect, Rect rect2, float f, float f2, float f3, float f4, float f5, int i, Interpolator interpolator, Interpolator interpolator2, Runnable runnable, int i2, View view) {
        int i3;
        Rect rect3 = rect;
        Rect rect4 = rect2;
        float hypot = (float) Math.hypot((double) (rect4.left - rect3.left), (double) (rect4.top - rect3.top));
        Resources resources = getResources();
        float integer = (float) resources.getInteger(C0622R.integer.config_dropAnimMaxDist);
        if (i < 0) {
            int integer2 = resources.getInteger(C0622R.integer.config_dropAnimMaxDuration);
            if (hypot < integer) {
                integer2 = (int) (((float) integer2) * this.mCubicEaseOutInterpolator.getInterpolation(hypot / integer));
            }
            i3 = Math.max(integer2, resources.getInteger(C0622R.integer.config_dropAnimMinDuration));
        } else {
            i3 = i;
        }
        TimeInterpolator timeInterpolator = null;
        if (interpolator2 == null || interpolator == null) {
            timeInterpolator = this.mCubicEaseOutInterpolator;
        }
        TimeInterpolator timeInterpolator2 = timeInterpolator;
        final float alpha = dragView.getAlpha();
        final float scaleX = dragView.getScaleX();
        final DragView dragView2 = dragView;
        final Interpolator interpolator3 = interpolator2;
        final Interpolator interpolator4 = interpolator;
        final float f6 = f2;
        final float f7 = f3;
        final float f8 = f4;
        final float f9 = f5;
        final float f10 = f;
        final Rect rect5 = rect;
        final Rect rect6 = rect2;
        C06932 r0 = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float f;
                float f2;
                int i;
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                int measuredWidth = dragView2.getMeasuredWidth();
                int measuredHeight = dragView2.getMeasuredHeight();
                if (interpolator3 == null) {
                    f = floatValue;
                } else {
                    f = interpolator3.getInterpolation(floatValue);
                }
                if (interpolator4 == null) {
                    f2 = floatValue;
                } else {
                    f2 = interpolator4.getInterpolation(floatValue);
                }
                float f3 = f6 * scaleX;
                float f4 = f7 * scaleX;
                float f5 = 1.0f - floatValue;
                float f6 = (f8 * floatValue) + (f3 * f5);
                float f7 = (f9 * floatValue) + (f5 * f4);
                float f8 = (f10 * f) + (alpha * (1.0f - f));
                float f9 = ((float) rect5.left) + (((f3 - 1.0f) * ((float) measuredWidth)) / 2.0f);
                float f10 = ((float) rect5.top) + (((f4 - 1.0f) * ((float) measuredHeight)) / 2.0f);
                int round = (int) (f9 + ((float) Math.round((((float) rect6.left) - f9) * f2)));
                int round2 = (int) (f10 + ((float) Math.round((((float) rect6.top) - f10) * f2)));
                if (DragLayer.this.mAnchorView == null) {
                    i = 0;
                } else {
                    i = (int) (DragLayer.this.mAnchorView.getScaleX() * ((float) (DragLayer.this.mAnchorViewInitialScrollX - DragLayer.this.mAnchorView.getScrollX())));
                }
                int scrollY = round2 - DragLayer.this.mDropView.getScrollY();
                DragLayer.this.mDropView.setTranslationX((float) ((round - DragLayer.this.mDropView.getScrollX()) + i));
                DragLayer.this.mDropView.setTranslationY((float) scrollY);
                DragLayer.this.mDropView.setScaleX(f6);
                DragLayer.this.mDropView.setScaleY(f7);
                DragLayer.this.mDropView.setAlpha(f8);
            }
        };
        animateView(dragView, r0, i3, timeInterpolator2, runnable, i2, view);
    }

    public void animateView(DragView dragView, AnimatorUpdateListener animatorUpdateListener, int i, TimeInterpolator timeInterpolator, final Runnable runnable, final int i2, View view) {
        if (this.mDropAnim != null) {
            this.mDropAnim.cancel();
        }
        this.mDropView = dragView;
        this.mDropView.cancelAnimation();
        this.mDropView.requestLayout();
        if (view != null) {
            this.mAnchorViewInitialScrollX = view.getScrollX();
        }
        this.mAnchorView = view;
        this.mDropAnim = new ValueAnimator();
        this.mDropAnim.setInterpolator(timeInterpolator);
        this.mDropAnim.setDuration((long) i);
        this.mDropAnim.setFloatValues(new float[]{0.0f, 1.0f});
        this.mDropAnim.addUpdateListener(animatorUpdateListener);
        this.mDropAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (runnable != null) {
                    runnable.run();
                }
                if (i2 == 0) {
                    DragLayer.this.clearAnimatedView();
                }
            }
        });
        this.mDropAnim.start();
    }

    public void clearAnimatedView() {
        if (this.mDropAnim != null) {
            this.mDropAnim.cancel();
        }
        if (this.mDropView != null) {
            this.mDragController.onDeferredEndDrag(this.mDropView);
        }
        this.mDropView = null;
        invalidate();
    }

    public View getAnimatedView() {
        return this.mDropView;
    }

    public void onChildViewAdded(View view, View view2) {
        super.onChildViewAdded(view, view2);
        updateChildIndices();
    }

    public void onChildViewRemoved(View view, View view2) {
        updateChildIndices();
    }

    public void bringChildToFront(View view) {
        super.bringChildToFront(view);
        updateChildIndices();
    }

    private void updateChildIndices() {
        this.mTopViewIndex = -1;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof DragView) {
                this.mTopViewIndex = i;
            }
        }
        this.mChildCountOnLastUpdate = childCount;
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int i, int i2) {
        if (this.mChildCountOnLastUpdate != i) {
            updateChildIndices();
        }
        if (this.mTopViewIndex == -1) {
            return i2;
        }
        if (i2 == i - 1) {
            return this.mTopViewIndex;
        }
        return i2 < this.mTopViewIndex ? i2 : i2 + 1;
    }

    public void invalidateScrim() {
        if (this.mBackgroundAlpha > 0.0f) {
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        if (this.mBackgroundAlpha > 0.0f) {
            this.mLauncher.getWorkspace().computeScrollWithoutInvalidation();
            int i = (int) (this.mBackgroundAlpha * 255.0f);
            CellLayout currentDragOverlappingLayout = this.mLauncher.getWorkspace().getCurrentDragOverlappingLayout();
            canvas.save();
            if (!(currentDragOverlappingLayout == null || currentDragOverlappingLayout == this.mLauncher.getHotseat().getLayout())) {
                getDescendantRectRelativeToSelf(currentDragOverlappingLayout, this.mHighlightRect);
                canvas.clipRect(this.mHighlightRect, Op.DIFFERENCE);
            }
            canvas.drawColor(ColorUtils.setAlphaComponent(ColorUtils.compositeColors(1711276032, this.mWallpaperColorInfo.getMainColor()), i));
            canvas.restore();
        }
        this.mFocusIndicatorHelper.draw(canvas);
        super.dispatchDraw(canvas);
    }

    public void setBackgroundAlpha(float f) {
        if (f != this.mBackgroundAlpha) {
            this.mBackgroundAlpha = f;
            invalidate();
        }
    }

    public float getBackgroundAlpha() {
        return this.mBackgroundAlpha;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView != null) {
            return topOpenView.requestFocus(i, rect);
        }
        return super.onRequestFocusInDescendants(i, rect);
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this.mLauncher);
        if (topOpenView != null) {
            topOpenView.addFocusables(arrayList, i);
        } else {
            super.addFocusables(arrayList, i, i2);
        }
    }

    public void setTouchCompleteListener(TouchCompleteListener touchCompleteListener) {
        this.mTouchCompleteListener = touchCompleteListener;
    }
}
