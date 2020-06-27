package com.android.launcher3;

import android.content.Context;
import android.graphics.Canvas;
import android.support.p004v7.widget.RecyclerView;
import android.support.p004v7.widget.RecyclerView.OnItemTouchListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.launcher3.views.RecyclerViewFastScroller;

public abstract class BaseRecyclerView extends RecyclerView implements OnItemTouchListener {
    protected RecyclerViewFastScroller mScrollbar;

    /* access modifiers changed from: protected */
    public abstract int getAvailableScrollHeight();

    public abstract int getCurrentScrollY();

    public void onFastScrollCompleted() {
    }

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public abstract void onUpdateScrollbar(int i);

    public abstract String scrollToPositionAtProgress(float f);

    public boolean supportsFastScrolling() {
        return true;
    }

    public BaseRecyclerView(Context context) {
        this(context, null);
    }

    public BaseRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        addOnItemTouchListener(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup viewGroup = (ViewGroup) getParent();
        this.mScrollbar = (RecyclerViewFastScroller) viewGroup.findViewById(C0622R.C0625id.fast_scroller);
        this.mScrollbar.setRecyclerView(this, (TextView) viewGroup.findViewById(C0622R.C0625id.fast_scroller_popup));
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        return handleTouchEvent(motionEvent);
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        handleTouchEvent(motionEvent);
    }

    private boolean handleTouchEvent(MotionEvent motionEvent) {
        int left = getLeft() - this.mScrollbar.getLeft();
        int top = getTop() - this.mScrollbar.getTop();
        motionEvent.offsetLocation((float) left, (float) top);
        try {
            return this.mScrollbar.handleTouchEvent(motionEvent);
        } finally {
            motionEvent.offsetLocation((float) (-left), (float) (-top));
        }
    }

    public int getScrollbarTrackHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollBarHeight() {
        return getScrollbarTrackHeight() - this.mScrollbar.getThumbHeight();
    }

    public RecyclerViewFastScroller getScrollBar() {
        return this.mScrollbar;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        onUpdateScrollbar(0);
        super.dispatchDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void synchronizeScrollBarThumbOffsetToViewScroll(int i, int i2) {
        if (i2 <= 0) {
            this.mScrollbar.setThumbOffsetY(-1);
            return;
        }
        this.mScrollbar.setThumbOffsetY((int) ((((float) i) / ((float) i2)) * ((float) getAvailableScrollBarHeight())));
    }
}
