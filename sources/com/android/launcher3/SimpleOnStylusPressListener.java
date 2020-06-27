package com.android.launcher3;

import android.view.MotionEvent;
import android.view.View;
import com.android.launcher3.StylusEventHelper.StylusButtonListener;

public class SimpleOnStylusPressListener implements StylusButtonListener {
    private View mView;

    public boolean onReleased(MotionEvent motionEvent) {
        return false;
    }

    public SimpleOnStylusPressListener(View view) {
        this.mView = view;
    }

    public boolean onPressed(MotionEvent motionEvent) {
        return this.mView.isLongClickable() && this.mView.performLongClick();
    }
}
