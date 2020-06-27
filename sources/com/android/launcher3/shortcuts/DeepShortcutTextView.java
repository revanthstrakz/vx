package com.android.launcher3.shortcuts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.C0622R;
import com.android.launcher3.Utilities;

public class DeepShortcutTextView extends BubbleTextView {
    private final Rect mDragHandleBounds;
    private final int mDragHandleWidth;
    private boolean mShouldPerformClick;

    /* access modifiers changed from: protected */
    public void applyCompoundDrawables(Drawable drawable) {
    }

    public DeepShortcutTextView(Context context) {
        this(context, null, 0);
    }

    public DeepShortcutTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DeepShortcutTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDragHandleBounds = new Rect();
        this.mShouldPerformClick = true;
        Resources resources = getResources();
        this.mDragHandleWidth = resources.getDimensionPixelSize(C0622R.dimen.popup_padding_end) + resources.getDimensionPixelSize(C0622R.dimen.deep_shortcut_drag_handle_size) + (resources.getDimensionPixelSize(C0622R.dimen.deep_shortcut_drawable_padding) / 2);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mDragHandleBounds.set(0, 0, this.mDragHandleWidth, getMeasuredHeight());
        if (!Utilities.isRtl(getResources())) {
            this.mDragHandleBounds.offset(getMeasuredWidth() - this.mDragHandleBounds.width(), 0);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.mShouldPerformClick = !this.mDragHandleBounds.contains((int) motionEvent.getX(), (int) motionEvent.getY());
        }
        return super.onTouchEvent(motionEvent);
    }

    public boolean performClick() {
        return this.mShouldPerformClick && super.performClick();
    }
}
