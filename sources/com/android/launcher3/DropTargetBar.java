package com.android.launcher3;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import com.android.launcher3.DropTarget.DragObject;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragController.DragListener;
import com.android.launcher3.dragndrop.DragOptions;

public class DropTargetBar extends LinearLayout implements DragListener {
    protected static final int DEFAULT_DRAG_FADE_DURATION = 175;
    protected static final TimeInterpolator DEFAULT_INTERPOLATOR = new AccelerateInterpolator();
    private ViewPropertyAnimator mCurrentAnimation;
    @ExportedProperty(category = "launcher")
    protected boolean mDeferOnDragEnd;
    private final Runnable mFadeAnimationEndRunnable = new Runnable() {
        public void run() {
            AlphaUpdateListener.updateVisibility(DropTargetBar.this, ((AccessibilityManager) DropTargetBar.this.getContext().getSystemService("accessibility")).isEnabled());
        }
    };
    @ExportedProperty(category = "launcher")
    protected boolean mVisible = false;

    public DropTargetBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DropTargetBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setAlpha(0.0f);
    }

    public void setup(DragController dragController) {
        dragController.addDragListener(this);
        setupButtonDropTarget(this, dragController);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (hideTextHelper(true, hideTextHelper(false, false))) {
            super.onMeasure(i, i2);
        }
    }

    private boolean hideTextHelper(boolean z, boolean z2) {
        ButtonDropTarget buttonDropTarget;
        View view;
        boolean z3 = false;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            if (getChildAt(childCount) instanceof ButtonDropTarget) {
                ButtonDropTarget buttonDropTarget2 = (ButtonDropTarget) getChildAt(childCount);
                buttonDropTarget = buttonDropTarget2;
                view = buttonDropTarget2;
            } else if (getChildAt(childCount) instanceof ViewGroup) {
                View childAt = getChildAt(childCount);
                view = childAt;
                buttonDropTarget = (ButtonDropTarget) ((ViewGroup) childAt).getChildAt(0);
            } else {
                continue;
            }
            if (view.getVisibility() != 0) {
                continue;
            } else if (z) {
                z3 |= buttonDropTarget.updateText(z2);
            } else if (buttonDropTarget.isTextTruncated()) {
                return true;
            }
        }
        return z3;
    }

    private void setupButtonDropTarget(View view, DragController dragController) {
        if (view instanceof ButtonDropTarget) {
            ButtonDropTarget buttonDropTarget = (ButtonDropTarget) view;
            buttonDropTarget.setDropTargetBar(this);
            dragController.addDragListener(buttonDropTarget);
            dragController.addDropTarget(buttonDropTarget);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                setupButtonDropTarget(viewGroup.getChildAt(childCount), dragController);
            }
        }
    }

    private void animateToVisibility(boolean z) {
        if (this.mVisible != z) {
            this.mVisible = z;
            if (this.mCurrentAnimation != null) {
                this.mCurrentAnimation.cancel();
                this.mCurrentAnimation = null;
            }
            float f = this.mVisible ? 1.0f : 0.0f;
            if (Float.compare(getAlpha(), f) != 0) {
                setVisibility(0);
                this.mCurrentAnimation = animate().alpha(f).setInterpolator(DEFAULT_INTERPOLATOR).setDuration(175).withEndAction(this.mFadeAnimationEndRunnable);
            }
        }
    }

    public void onDragStart(DragObject dragObject, DragOptions dragOptions) {
        animateToVisibility(true);
    }

    /* access modifiers changed from: protected */
    public void deferOnDragEnd() {
        this.mDeferOnDragEnd = true;
    }

    public void onDragEnd() {
        if (!this.mDeferOnDragEnd) {
            animateToVisibility(false);
        } else {
            this.mDeferOnDragEnd = false;
        }
    }
}
