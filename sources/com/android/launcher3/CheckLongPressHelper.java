package com.android.launcher3;

import android.view.View;
import android.view.View.OnLongClickListener;

public class CheckLongPressHelper {
    public static final int DEFAULT_LONG_PRESS_TIMEOUT = 300;
    boolean mHasPerformedLongPress;
    OnLongClickListener mListener;
    private int mLongPressTimeout = DEFAULT_LONG_PRESS_TIMEOUT;
    private CheckForLongPress mPendingCheckForLongPress;
    View mView;

    class CheckForLongPress implements Runnable {
        CheckForLongPress() {
        }

        public void run() {
            boolean z;
            if (CheckLongPressHelper.this.mView.getParent() != null && CheckLongPressHelper.this.mView.hasWindowFocus() && !CheckLongPressHelper.this.mHasPerformedLongPress) {
                if (CheckLongPressHelper.this.mListener != null) {
                    z = CheckLongPressHelper.this.mListener.onLongClick(CheckLongPressHelper.this.mView);
                } else {
                    z = CheckLongPressHelper.this.mView.performLongClick();
                }
                if (z) {
                    CheckLongPressHelper.this.mView.setPressed(false);
                    CheckLongPressHelper.this.mHasPerformedLongPress = true;
                }
            }
        }
    }

    public CheckLongPressHelper(View view) {
        this.mView = view;
    }

    public CheckLongPressHelper(View view, OnLongClickListener onLongClickListener) {
        this.mView = view;
        this.mListener = onLongClickListener;
    }

    public void setLongPressTimeout(int i) {
        this.mLongPressTimeout = i;
    }

    public void postCheckForLongPress() {
        this.mHasPerformedLongPress = false;
        if (this.mPendingCheckForLongPress == null) {
            this.mPendingCheckForLongPress = new CheckForLongPress();
        }
        this.mView.postDelayed(this.mPendingCheckForLongPress, (long) this.mLongPressTimeout);
    }

    public void cancelLongPress() {
        this.mHasPerformedLongPress = false;
        if (this.mPendingCheckForLongPress != null) {
            this.mView.removeCallbacks(this.mPendingCheckForLongPress);
            this.mPendingCheckForLongPress = null;
        }
    }

    public boolean hasPerformedLongPress() {
        return this.mHasPerformedLongPress;
    }
}
