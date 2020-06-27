package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.android.launcher3.views.RecyclerViewFastScroller;

public class LandscapeFastScroller extends RecyclerViewFastScroller {
    public boolean handleTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public LandscapeFastScroller(Context context) {
        super(context);
    }

    public LandscapeFastScroller(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public LandscapeFastScroller(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        motionEvent.offsetLocation(0.0f, (float) (-this.mRv.getPaddingTop()));
        if (super.handleTouchEvent(motionEvent)) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        motionEvent.offsetLocation(0.0f, (float) this.mRv.getPaddingTop());
        return true;
    }

    public boolean shouldBlockIntercept(int i, int i2) {
        return i >= 0 && i < getWidth() && i2 >= 0 && i2 < getHeight();
    }
}
