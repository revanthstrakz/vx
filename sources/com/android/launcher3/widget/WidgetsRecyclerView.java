package com.android.launcher3.widget;

import android.content.Context;
import android.support.p004v7.widget.LinearLayoutManager;
import android.support.p004v7.widget.RecyclerView.Adapter;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.BaseRecyclerView;

public class WidgetsRecyclerView extends BaseRecyclerView {
    private static final String TAG = "WidgetsRecyclerView";
    private WidgetsListAdapter mAdapter;

    public WidgetsRecyclerView(Context context) {
        this(context, null);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public WidgetsRecyclerView(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        addOnItemTouchListener(this);
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        this.mAdapter = (WidgetsListAdapter) adapter;
    }

    public String scrollToPositionAtProgress(float f) {
        if (isModelNotReady()) {
            return "";
        }
        stopScroll();
        float itemCount = ((float) this.mAdapter.getItemCount()) * f;
        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(0, (int) (-(((float) getAvailableScrollHeight()) * f)));
        if (f == 1.0f) {
            itemCount -= 1.0f;
        }
        return this.mAdapter.getSectionName((int) itemCount);
    }

    public void onUpdateScrollbar(int i) {
        if (!isModelNotReady()) {
            int currentScrollY = getCurrentScrollY();
            if (currentScrollY < 0) {
                this.mScrollbar.setThumbOffsetY(-1);
            } else {
                synchronizeScrollBarThumbOffsetToViewScroll(currentScrollY, getAvailableScrollHeight());
            }
        }
    }

    public int getCurrentScrollY() {
        if (isModelNotReady() || getChildCount() == 0) {
            return -1;
        }
        View childAt = getChildAt(0);
        int measuredHeight = childAt.getMeasuredHeight() * getChildPosition(childAt);
        return (getPaddingTop() + measuredHeight) - getLayoutManager().getDecoratedTop(childAt);
    }

    /* access modifiers changed from: protected */
    public int getAvailableScrollHeight() {
        return ((getPaddingTop() + (getChildAt(0).getMeasuredHeight() * this.mAdapter.getItemCount())) + getPaddingBottom()) - getScrollbarTrackHeight();
    }

    private boolean isModelNotReady() {
        return this.mAdapter.getItemCount() == 0;
    }
}
